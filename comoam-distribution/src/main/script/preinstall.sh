#!/bin/sh
real_exec_user=`/usr/bin/id -un`
[ "${runner}" = "" ] && runner=root
if [ "${real_exec_user}" != "${runner}" ]; then
   echo " The ELCM tool must be installed by ${runner} user"
   exit 1
fi
echo "start install....."
INSTALL_ROOT=/opt/PlexView/ELCM
OLD_INSTALL_ROOT=/opt/PlexView/comoam
if [  -d $OLD_INSTALL_ROOT ]; then
   mv $OLD_INSTALL_ROOT $INSTALL_ROOT    
fi
echo "disable execute mode of virsh command for other user"
chmod o-x /usr/bin/virsh
#force shutdown ELCM if the ELCM is still runnig
SERVER_PROCESS_ID=$(ps -ef|grep '/opt/PlexView/ELCM/server'|grep -v 'grep'|awk '{print $2}')
if [ ! -z $SERVER_PROCESS_ID ]; then
   echo "force shutdown..."
   kill -15 $SERVER_PROCESS_ID
   . /opt/PlexView/ELCM/script/databackup_lib.sh
   ELCM_data_backup
fi
