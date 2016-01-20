#!/bin/sh
vercomp () {
    if [[ $1 == $2 ]]
    then
        return 0
    fi
    local IFS=.
    local i ver1=($1) ver2=($2)
    # fill empty fields in ver1 with zeros
    for ((i=${#ver1[@]}; i<${#ver2[@]}; i++))
    do
        ver1[i]=0
    done
    for ((i=0; i<${#ver1[@]}; i++))
    do
        if [[ -z ${ver2[i]} ]]
        then
            # fill empty fields in ver2 with zeros
            ver2[i]=0
        fi
        if ((10#${ver1[i]} > 10#${ver2[i]}))
        then
            return 1
        fi
        if ((10#${ver1[i]} < 10#${ver2[i]}))
        then
            return 2
        fi
    done
    return 0
}
function supportU() {
  VERSION_FILE=/opt/PlexView/ELCM/server/webapps/ROOT/WEB-INF/classes/config/version
  SUPPORT_VERSION=1.0.7.1
  if [ -f $VERSION_FILE ]; then
    ELCM_VERSION=$(cat $VERSION_FILE) 
    vercomp ${SUPPORT_VERSION} ${ELCM_VERSION}
    res=$?
    if [ "$res" -eq "1" ]; then
        echo "This version don't support rpm -U"
        echo "Please remove it by rpm -e firstly, then re-install ELCM by rpm -i" 
        exit 1
    fi
  fi
}
real_exec_user=`/usr/bin/id -un`
[ "${runner}" = "" ] && runner=root
if [ "${real_exec_user}" != "${runner}" ]; then
   echo " The ELCM tool must be installed by ${runner} user"
   exit 1
fi
#check if the to replaced version support 'rpm -U'
supportU
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
   . ${INSTALL_ROOT}/script/databackup_lib.sh 
   ELCM_data_backup
fi
