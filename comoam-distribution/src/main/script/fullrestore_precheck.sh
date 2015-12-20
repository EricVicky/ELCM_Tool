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
hostname_oam=$2
hostname_db=$3
hostname_cm=$4
remote_restore_dir=$5

################################################################################
# Check Function
################################################################################
fullrestore_file_exist() {
    Restore_File_Dir=$1
    ls ${Restore_File_Dir} | grep ${hostname_oam}_snapshot > /dev/null
    if [ $? -eq 0 ]; then
        ls ${Restore_File_Dir} | grep ${hostname_db}_snapshot > /dev/null
        if [ $? -eq 0 ]; then
            if [ ! "${hostname_cm}" = "" ]; then
                ls ${Restore_File_Dir} | grep ${hostname_cm}_snapshot > /dev/null
                if [ $? -eq 0 ]; then
                    return 0
                else
                    return 1
                fi
            else
                return 0 
            fi   
        else
            return 1
        fi
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
    fullrestore_file_exist ${local_restore_dir}
    if [ $? -eq 0 ];then
        echo "Success"
    else
        echo "Error: No full backup files exist, full restore is prohibited."
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

