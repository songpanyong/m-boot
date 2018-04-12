#!/bin/bash
oldpids=$(ps -ef | grep "redsync2" | grep -v grep |  awk '{print $2}')
[ "x$oldpids" == "x" ] || kill -9 $oldpids
