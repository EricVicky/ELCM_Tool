#!/bin/sh
ELCM_STARTUP=/opt/PlexView/ELCM/server/bin/startup.sh
if [ -f ${ELCM_STARTUP} ]; then
   echo "restart ELCM"
   #force shutdown ELCM if the ELCM is still runnig
   SERVER_PROCESS_ID=$(ps -ef|grep '/opt/PlexView/ELCM/server'|grep -v 'grep'|awk '{print $2}')
   if [ ! -z $SERVER_PROCESS_ID ]; then
      echo "force shutdown..."
      kill -15 $SERVER_PROCESS_ID
   fi
   ${ELCM_STARTUP}
fi
