#!/bin/bash
#env|grep scripts


set path = "$PATH":/alcatel/omc1/OMC_MON/scripts/tools

#/alcatel/omc1/OMC_MON/scripts/tools/omc_stat | grep 'THIS SERVER ROLE'
omc_stat | grep 'THIS SERVER ROLE'
