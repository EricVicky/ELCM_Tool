#!/bin/sh
################################################################################
#  fullrestore_precheck.sh:
#  Availability check of fullrestore server including disk space/files exist etc.
#
#  usage : fullrestore_precheck.sh
#
#-------------------------------------------------------------------------------
# HISTORY:
#
#  2015-12-11 - Eric Wang  --- creation
################################################################################

local_restore_dir=$1
hostname=$2
remote_restore_dir=$3

################################################################################
# Check Function
################################################################################
fullrestore_dir_exist() {
    Restore_File_Dir=$1
    arr=(${hostname//:/ })
    for vm_name in ${arr[@]}
    do
        ls ${Restore_File_Dir} | grep ${vm_name}_snapshot > /dev/null
        if [ ! $? -eq 0 ]; then
            return 1
        fi
    done
}

fullrestore_file_exist() {
    Restore_File_Dir=$1
    arr=(${hostname//:/ })
    for vm_name in ${arr[@]}
    do
        ls ${Restore_File_Dir}/${vm_name}_snapshot | grep ^configdrive.iso$ > /dev/null
        if [ ! $? -eq 0 ]; then
            return 1
        fi
        ls ${Restore_File_Dir}/${vm_name}_snapshot | grep ^datadisk.qcow2$ > /dev/null
        if [ ! $? -eq 0 ]; then
            return 1
        fi
        ls ${Restore_File_Dir}/${vm_name}_snapshot | grep ^rhel.qcow2$ > /dev/null
        if [ ! $? -eq 0 ]; then
            return 1
        fi
        ls ${Restore_File_Dir}/${vm_name}_snapshot | grep ^vmdomain.xml$ > /dev/null
        if [ ! $? -eq 0 ]; then
            return 1
        fi
    done
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
    fullrestore_dir_exist ${local_restore_dir}
    if [ $? -eq 1 ];then
        echo "Error: Files are missing. Full restore cannot be performed.."
    else
        fullrestore_file_exist ${local_restore_dir}
        if [ $? -eq 1 ];then
            echo "Error: Files are missing. Full restore cannot be performed.."
        else
            echo "Success"
        fi
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
        exit 0
    fi
fi

