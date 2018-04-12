#!/bin/bash
#　* * * * * root /opt/shells/move2his.sh
baseDir=/opt/shells
source $baseDir/db.conf.sh
echo $(date) start >> $baseDir/log.log 
cat $baseDir/esql.sql | $mysqlcli
echo $(date) end >> $baseDir/log.log 
