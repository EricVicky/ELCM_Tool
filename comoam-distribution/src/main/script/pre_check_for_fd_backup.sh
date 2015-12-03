#!/bin/sh
################################################################################
#? pre_check_for_fd_backup.sh:
#?    Availability check of backup server including disk space etc.
#?
#?  usage : pre_check_for_fd_backup.sh
#?
#-------------------------------------------------------------------------------
# HISTORY:
#
#  2015-11-30 - Y. Terence    - creation
#  2015-11-30 - Y. Terence    - OMC-37877 No control of existence/write access/available space of destination directories before data backup launching
###############################################################################

check_disk_writable() {
    directory=$1
    if [ ! -d "$directory" ]; then
        echo "Error: The target $directory is NOT existing."
        exit 1
    fi
    
    filename="${directory}/try_writable";
    touch $filename 2>/dev/null
    if [ $? != 0 ]; then
        echo "Error: The target $directory is NOT writeable."
        exit 1
    fi
    
    rm -f $filename 2>/dev/null
}

check_disk_space() {
    directory=$1
    size=$2

    df_line=`df -kP $directory | grep -v ^Filesystem 2>/dev/null`
    avail_size=`echo ${df_line} | awk -F ' ' '{print $4}'`

    if [ $avail_size -lt $size ]; then
        echo "Error: The target $directory has NOT enough disk space (${size}KB is required)."
        exit 2
    fi
}

# Main Program

# Required directory name for the backup
dir=$1
size=$2

if [ -z "${dir}" ]; then
    echo "Error: the backup target name is required in arguments."
    exit 3
fi

if [ -z "${size}" ]; then
    size=10000
fi

check_disk_writable $dir

check_disk_space $dir $size

exit 0
