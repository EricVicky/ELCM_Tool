#!/bin/sh
bridge=$1
if [ $# -lt 1 ]; then
   echo "bridge name required"
   exit 1
fi
count=$(brctl show|grep -Pc ^${bridge}\\s)
echo $count
