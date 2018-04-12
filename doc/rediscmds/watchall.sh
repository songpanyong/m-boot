#!/bin/sh

cd $(dirname $0)
[ -d logs ] || mkdir logs
for x in t_*.sql
do
	oldpids=$(ps -ef | grep "$x" | grep -v grep |  awk '{print $2}')
	[ "x$oldpids" == "x" ] || kill -9 $oldpids
	nohup watch -n 1 sh redsync2.sh $x 2>&1 > logs/watch.$x.log &
done


