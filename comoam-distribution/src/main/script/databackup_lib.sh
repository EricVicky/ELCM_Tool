#!/bin/sh
ELCM_ROOT=/opt/PlexView/ELCM/
function ELCM_data_backup(){
 rm -f ${ELCM_ROOT}datasource.tar.gz
 rm -f ${ELCM_ROOT}cert.tar.gz
 echo "backup data"
if [ -d ${ELCM_ROOT}datasource ]; then
 tar -czvf ${ELCM_ROOT}datasource.tar.gz  ${ELCM_ROOT}datasource/*
 tar -czvf ${ELCM_ROOT}cert.tar.gz  ${ELCM_ROOT}ELCM-playbook/crt/*
 tar -czvf ${ELCM_ROOT}certtemp.tar.gz  ${ELCM_ROOT}/tmp/
 tar -czvf ${ELCM_ROOT}keystore.tar.gz ${ELCM_ROOT}server/ssl/keystore 
fi
}
