#!/bin/bash

set +e

#read commands
db=gh_jz_mimosa
[ $# -gt 1 ] && db=$2
echo "db=$db"

sqlfile=$1
echo "sqlfile=$sqlfile"
mysqlcli="mysql --defaults-file=myconf -N -r"
rediscli="sh myredis"

tid="${db}:${sqlfile%.sql}"
echo "tid=$tid"
key_pos=sync:pos:$tid
key_lock=sync:lock:$tid
echo "key_post=$key_pos"
echo "key_lock=$key_lock"

#lock auto timeout in seconds
lock_timeout=300
trylock=$($rediscli set ${key_lock} 888 EX ${lock_timeout} NX)

if [ "x$trylock" != "xOK" ] 
then
	#running
	echo 'lock failure!!!' >>$outfile.log
	exit -1
else
	echo 'lock success!!!' >>$outfile.log
fi
startpos=0
limit=800
batchmax=40000
echo "startpos=${startpos}, limit=${limit}, batchmax=${batchmax}"

#get last updateTime in timestamp(6)
lastpos=$($rediscli get ${key_pos})
echo "lastpos=$lastpos"
[ "x$lastpos" == "x"  ] && lastpos=$startpos
echo "lastpos=$lastpos"

daydir=$(date '+%Y%m%d')
[ -d $daydir ] || mkdir $daydir
curname=$sqlfile.$lastpos

oidsfile=$daydir/oids.csv
outfile=$daydir/out.csv
logfile=$daydir/$curname.log
tmpsql=$daydir/tmp.rediscmd.sql
maxsql=max.$sqlfile
oidsql=oids.sql
tmpOidSQL=$daydir/tmp.oids.sql
updatexid_sql=update_ids.sql
tmpUpdateXidSQL=$daydir/$updatexid_sql


echo "curname=$curname"
echo "oidsfile=$oidsfile"
echo "outfile=$outfile"
echo "logfile=$logfile"
echo "tmpsql=$tmpsql"
echo "maxsql=$maxsql"
echo "tmpOidSQL=$tmpOidSQL"
#nextpos=min(max(oid), $lastpos+2000)

echo "lastupdate query ...$lastpos" >>$outfile.log
if [ -f $maxsql ]
then
	maxpos=$(cat $maxsql | $mysqlcli 2>>$outfile.log)
else
	#useless
	maxpos=$(cat max.temp.sql | sed -e "s|#table|$tid|g" | $mysqlcli 2>>$outfile.log)
fi

echo "maxpos=$maxpos"
if [ "NULLx" == "${maxpos}x" ]
then
 echo "maxpos is null, so nothing todo ..." >>$outfile.log
 $rediscli del ${key_lock} 2>&1 >/dev/null
 exit 100
fi

if [ "$lastpos" == "$maxpos" ]
then
	echo "lastpos == maxpos , so nothing todo ..." >>$outfile.log
	$rediscli del ${key_lock} 2>&1 >/dev/null
	exit 100
fi


limitpos=$((lastpos + batchmax))
[ $maxpos -gt $limitpos ] && maxpos=$limitpos
echo "limitpos(lastpos+batchmax)=$limitpos, maxpos=$maxpos"

for (( pos=$lastpos; pos<$maxpos; pos+=$limit ))
do
	lastpos=$pos
	nextpos=$((pos + limit))
	[ $nextpos -gt $maxpos ] && nextpos=$maxpos

	range="${pos}-${nextpos}"
	echo "processing ... $range" >>$outfile.log

	roidsfile=$oidsfile.$range
	routfile=$outfile.$range
	rtmpsql=$tmpsql.$range
	rtmpUpdateXidSQL=${tmpUpdateXidSQL}.$range

	rTmpOidSQL=$tmpOidSQL.$range

	cat $oidsql | sed -e "s|#start|$lastpos|g" | sed -e "s|#end|$nextpos|g" > $rTmpOidSQL
	echo "generate $rTmpOidSQL" >>$outfile.log
	cat $rTmpOidSQL | $mysqlcli 1>$roidsfile 2>>$outfile.log
	echo "generate $roidsfile" >>$outfile.log

	if [ ! -s $roidsfile ] 
	then
		echo "$roidsfile is empty"  
		rm -rf $roidsfile
		continue
		
	fi
	>$routfile

	cat $roidsfile |
	while read syncType ids oids poids
	do
		myfile=$syncType.sql
		[ -f $myfile ] || continue
		cat $myfile | sed -e "s|#oids|$oids|g" | sed -e "s|#poids|$poids|g" > $rtmpsql.$syncType
		echo "generate $rtmpsql.$syncType" >>$outfile.log
		$mysqlcli < ${rtmpsql}.$syncType 1>>$routfile 2>>$outfile.log
		echo "generate $routfile" >>$outfile.log
		
		#update xid=oid
		cat ${updatexid_sql} | sed -e "s|#oids|$ids|g" >$rtmpUpdateXidSQL.$syncType
		echo "generate $rtmpUpdateXidSQL.$syncType" >>$outfile.log
		$mysqlcli < $rtmpUpdateXidSQL.$syncType 2>&1 >>$outfile.log
		echo "exec $rtmpUpdateXidSQL.$syncType ok" >>$outfile.log
	done 

	if [ -s $routfile ] 
	then
		#exec redis cmds
		unix2dos -q $routfile 2>&1 >>$outfile.log
		echo "unix2dos ok" >>$outfile.log
		$rediscli < $routfile 2>&1 >>$outfile.log
		#ok
		if [ $? -eq 0 ]
		then
			setlastpos=$($rediscli set ${key_pos} "$nextpos")
			echo "setlastpos return $setlastpos" >>$outfile.log
		fi
	else
		#remove empty file
		rm -f $routfile
	fi
done

#sleep for test
#sleep 3s
#delete lock
$rediscli del ${key_lock} 2>&1 >/dev/null
set -e

