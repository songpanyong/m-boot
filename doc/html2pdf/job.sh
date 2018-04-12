#!/bin/sh
#/etc/crontab 0 0/10 * * * root /opt/mimosa_file/shells/job.sh
baseDir=/opt/mimosa_file/shells/
cd $baseDir
if [ ! -f pid ]
then
echo $$ > pid
echo $(date) &>> job.log
for i in $(ls /opt/mimosa_file/shells/*.first.success)
    do
        shfile=${i%.*}
        echo $shfile
        echo "$(date) $shfile.sh start" &>> job.log
        sh $shfile.sh &>> $shfile.log
        echo "$(date) $shfile.sh end" &>> job.log
        mv $shfile.log /opt/mimosa_file/log/
        mv $shfile.success /opt/mimosa_file/log/
        mv $shfile.sh /opt/mimosa_file/log/
    done
    rm -rf pid
else
    echo "$(cat pid) is running" &>> job.log
fi
