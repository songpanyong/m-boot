#!/bin/sh

set +e

#read commands
db=gh_jz_mimosa
#db=
echo "$(date +%H:%M:%S) start ..."

[ $# -gt 1 ] && db=$1
mysqlcli="mysql --defaults-file=myconf -N -r $db"
rediscli="sh myredis"

echo -n "test mysql conf..."
echo "select 'OK'" | $mysqlcli

echo -n "test redis conf..."
$rediscli set sync:all:test 1


tid="${db}:t_redis_sync"
key_pos=sync:pos:$tid
key_lock=sync:lock:$tid

#lock auto timeout in seconds
lock_timeout=300
trylock=$($rediscli set ${key_lock} 888 EX ${lock_timeout} NX)

if [ "x$trylock" != "xOK" ] 
then
	#running
	exit -1
fi


#sh clearredis.sh

[ -d resync ] || mkdir resync

synclist=synclist.txt
[ $# -gt 1 ] && synclist=$2

relog=resync/resync.log
cat $synclist |
while read line
do
	x=$line.sql
	resql=resync/resync.$x
	recsv=$resql.csv

	echo "$(date +%H:%M:%S) resync $line ......"
	cat $x | sed -e "s|#xoids|x|g" > $resql

	echo "get csv $recsv ......"
	cat $resql >>$relog
	$mysqlcli < $resql 1>$recsv

	echo "$(date +%H:%M:%S) load csv to redis ..."
	unix2dos -q $recsv
	[ -s $recsv ] && $rediscli --pipe <$recsv 2>&1 >>$relog 
done

maxpos=$(echo "select max(oid) from t_money_redis_sync;" | $mysqlcli 2>>$relog)
[ "$maxpos" == 'NULL' ] && maxpos=0
[ "x$maxpos" == "x" ] && maxpos=0

echo -n "set last pos: $maxpos ... "
$rediscli set ${key_pos} $maxpos
$rediscli del ${key_lock} 2>&1 >/dev/null

echo "$(date +%H:%M:%S) done."


