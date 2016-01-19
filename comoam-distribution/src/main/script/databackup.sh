#!/bin/sh
rm -f /opt/PlexView/ELCM/datasource.tar.gz
rm -f /opt/PlexView/ELCM/cert.tar.gz
echo "backup data"
tar -czvf /opt/PlexView/ELCM/datasource.tar.gz  /opt/PlexView/ELCM/datasource/*
tar -czvf /opt/PlexView/ELCM/cert.tar.gz  /opt/PlexView/ELCM/ELCM-playbook/crt/*
tar -czvf /opt/PlexView/ELCM/certtemp.tar.gz  /opt/PlexView/ELCM/tmp/
tar -czvf /opt/PlexView/ELCM/keystore.tar.gz /opt/PlexView/ELCM/server/ssl/keystore 
