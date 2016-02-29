#!/bin/sh
################################################################################
#  fullbackup_precheck.sh:
#  Availability check of fullbackup server including disk space/files exist etc.
#
#  usage : fullbackup_precheck.sh
#
#-------------------------------------------------------------------------------
# HISTORY:
#
#  2015-12-11 - Eric Wang  --- creation
################################################################################

local_backup_dir=$1
hostname=$2
remote_backup_dir=$3

################################################################################
# Check Function
################################################################################
fullbackup_file_exist() {
    Backup_File_Dir=$1  
    arr=(${hostname//:/ })
    for vm_name in ${arr[@]}
    do
        ls ${Backup_File_Dir} | grep ^${vm_name}_snapshot$ > /dev/null
        if [ $? -eq 0 ]; then
            echo "Warning: Initial backup files will be erased by the new full backup!"
            break;
        fi
    done

}

check_disk_writable() {
    directory=$1
    if [ ! -d "$directory" ]; then
        echo "Error: The target $directory is NOT existing."
        return 1
    fi
    
    filename="${directory}/try_writable";
    touch $filename 2>/dev/null
    if [ $? != 0 ]; then
        echo "Error: The target $directory is NOT writeable."
        return 1
    fi
    
    rm -f $filename 2>/dev/null
}

check_disk_space() {
    File_space=$1
   
    sys_space=`df ${local_backup_dir} |tail -1|awk '{ print $3 }'`
    if [ ${sys_space} -lt ${File_space} ];then
        echo "Error: The target ${remote_backup_dir} has NOT enough disk space." 
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
backup_precheck() {
    fullbackup_file_exist ${local_backup_dir}
    check_disk_writable ${local_backup_dir}
    if [ $? -eq 0 ];then
        check_disk_space ${file_space}
        if [ $? -eq 0 ];then
            echo "Success"
        else
            return 1
        fi
    else
        return 1
    fi
}
#######################################################################
# Program Start
#######################################################################


file_space=0;
arr=(${hostname//:/ })
for vm_name in ${arr[@]}
    do
       tmp_space=`du ${local_backup_dir}/${vm_name} --max-depth=2|awk '{ print $1 }'|tail -1`
       file_space=` expr ${file_space} + ${tmp_space}`
    done

if [ -z ${remote_backup_dir} ];then
    backup_precheck
else
    mount_2_server ${remote_backup_dir} ${local_backup_dir}
    if [ $? -eq 0 ];then
        backup_precheck
        umount_2_server ${local_backup_dir}
    else
        exit 0
    fi
fi

