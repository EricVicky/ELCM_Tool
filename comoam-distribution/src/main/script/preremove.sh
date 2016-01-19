#!/bin/sh
PRO_COMOAM=$(ps -ef|grep 'opt/PlexView/ELCM'|grep -v grep)
if [ ! -z "$PRO_COMOAM" ]; then
    echo "Stop ELCM"
    /opt/PlexView/ELCM/server/bin/shutdown.sh
    sleep 5 
fi
#force shutdown ELCM if the ELCM is still runnig
SERVER_PROCESS_ID=$(ps -ef|grep '/opt/PlexView/ELCM/server'|grep -v 'grep'|awk '{print $2}')
if [ ! -z $SERVER_PROCESS_ID ]; then
   echo "force shutdown..."
   kill -15 $SERVER_PROCESS_ID
fi
/opt/PlexView/ELCM/server/bin/databackup.sh

