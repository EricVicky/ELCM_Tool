#!/bin/bash

export PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/alcatel/omc1/OMC_MON/scripts:/alcatel/omc1/OMC_MON/scripts/tools:/usr/lib/jvm/java/jre/bin:/usr/lib/jvm/java/bin

if [ -d "/export/home/omcmon" ]; then
    omc_stat | grep 'THIS SERVER ROLE'
else
    echo "GR not installed"
fi
