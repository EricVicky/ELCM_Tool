#!/bin/sh
################################################################################
#  datarestore_precheck.sh:
#  Availability check of datarestore server including disk space/files exist etc.
#
#  usage : datarestore_precheck.sh
#
#-------------------------------------------------------------------------------
# HISTORY:
#
#  2015-12-11 - Eric Wang  --- creation
################################################################################

local_restore_dir=$1
filename=$2
remote_restore_dir=$3

################################################################################
# Check Function
################################################################################
datarestore_file_exist() {
    Restore_File_Dir=$1
    File_name=$2
    
    ls ${Restore_File_Dir} | grep ^.*${File_name}$ > /dev/null
    if [ $? -eq 0 ]; then
    	return 0
    else 
        return 1    
    fi
}

mount_2_server() {
    Remote_IP_DIR=$1
    Mount_point=$2
    mount -o nolock -t nfs ${Remote_IP_DIR} ${Mount_point}
}

umount_2_server() {
    Mount_point=$1
    umount ${Mount_point}
}
######################################################################
# Main Function
######################################################################
restore_precheck() {
    datarestore_file_exist ${local_restore_dir} ${filename}
    if [ $? -eq 0 ];then
        echo "Success"
    else
        echo "Error: No data backup files exist, data restore is prohibited."
    fi
}
#######################################################################
# Program Start
#######################################################################

if [ -z ${remote_restore_dir} ];then    
    restore_precheck
else
    mount_2_server ${remote_restore_dir} ${local_restore_dir}
    if [ $? -eq 0 ];then
        restore_precheck        
        umount_2_server ${local_restore_dir}
    else
        exit 1
    fi
fi

