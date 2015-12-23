#!/bin/bash

ls /dev/kvm > /dev/null
if [ $? -eq 0 ];then
    echo "Success"
else
    echo "Fail"
fi
