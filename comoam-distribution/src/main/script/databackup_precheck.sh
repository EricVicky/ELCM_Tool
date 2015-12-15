#!/bin/sh
################################################################################
#  databackup_precheck.sh:
#  Availability check of databackup server including disk space/files exist etc.
#
#  usage : databackup_precheck.sh
#
#-------------------------------------------------------------------------------
# HISTORY:
#
#  2015-12-11 - Eric Wang  --- creation
################################################################################

local_backup_dir=$1
filename=$2
remote_backup_dir=$3

################################################################################
# Check Function
################################################################################
databackup_file_exist() {
    Backup_File_Dir=$1
    File_name=$2
    
    ls ${Backup_File_Dir} | grep ***${File_name} > /dev/null
    if [ $? -eq 0 ]; then
    	echo "Warning: Initial backup files exist!!!"
    fi

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
    directory=$1
    size=$2

    df_line=`df -kP $directory | grep -v ^Filesystem 2>/dev/null`
    avail_size=`echo ${df_line} | awk -F ' ' '{print $4}'`

    if [ $avail_size -lt $size ]; then
        echo "Error: The target $directory has NOT enough disk space."
        return 2
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
    databackup_file_exist ${local_backup_dir} ${filename}
    check_disk_writable ${local_backup_dir}
    if [ $? -eq 0 ];then
        check_disk_space ${local_backup_dir} ${size}
        if [ $? -eq 0 ];then
            exit 0
        else
            exit 1
        fi
    else
        exit 1
    fi
}
#######################################################################
# Program Start
#######################################################################

if [ -z ${remote_backup_dir} ];then
    size=10000    
    backup_precheck
else
    size=30000
    mount_2_server ${remote_backup_dir} ${local_backup_dir}
    if [ $? -eq 0 ];then
        backup_precheck        
        umount_2_server ${local_backup_dir}
    else
        exit 1
    fi
fi

