#!/bin/sh
PRO_COMOAM=$(ps -ef|grep 'opt/PlexView/ELCM'|grep -v grep)
#force shutdown ELCM if the ELCM is still runnig
SERVER_PROCESS_ID=$(ps -ef|grep '/opt/PlexView/ELCM/server'|grep -v 'grep'|awk '{print $2}')
if [ ! -z $SERVER_PROCESS_ID ]; then
   echo "force shutdown..."
   kill -15 $SERVER_PROCESS_ID
fi
INSTALL_ROOT=/opt/PlexView/ELCM
. ${INSTALL_ROOT}/script/databackup_lib.sh 
ELCM_data_backup

