#!/bin/bash
#env|grep scripts

export PATH=$PATH:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/alcatel/omc1/OMC_MON/scripts:/alcatel/omc1/OMC_MON/scripts/tools:/usr/lib/jvm/java/jre/bin:/usr/lib/jvm/java/bin


#/alcatel/omc1/OMC_MON/scripts/tools/omc_stat | grep 'THIS SERVER ROLE'
omc_stat | grep 'THIS SERVER ROLE'
