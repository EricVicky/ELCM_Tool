#!/bin/sh
ELCM_ROOT=/opt/PlexView/ELCM
echo "extract playbook"
if [ -f /opt/PlexView/ELCM/playbook.tar ]; then
    tar -xf  /opt/PlexView/ELCM/playbook.tar -C /opt/PlexView/ELCM/
    chown -R root:root  /opt/PlexView/ELCM/ELCM-playbook
fi
chmod 600 /opt/PlexView/ELCM/ELCM-playbook/group_vars

if [ -f /opt/PlexView/ELCM/datasource.tar.gz ]; then
    echo "restore data"
    tar -xzf /opt/PlexView/ELCM/datasource.tar.gz  -C / 
fi
chmod 600 /opt/PlexView/ELCM/datasource

if [ -f /opt/PlexView/ELCM/cert.tar.gz ]; then
    echo "restore openstack certification"
    tar -xzf /opt/PlexView/ELCM/cert.tar.gz  -C / 
fi
echo "install jpam"
tar -xzf /opt/PlexView/ELCM/lib/libjpam.so.tar.gz -C /opt/PlexView/ELCM/lib/
echo "start COM ELCM"
if [ ! -d /opt/PlexView/ELCM/server/logs ]; then
    mkdir /opt/PlexView/ELCM/server/logs
fi
if [ ! -d /opt/PlexView/ELCM/workspace ]; then
    mkdir /opt/PlexView/ELCM/workspace
fi

if [ ! -d /opt/PlexView/ELCM/ELCM-playbook/hot_files ]; then
    mkdir /opt/PlexView/ELCM/ELCM-playbook/hot_files 
fi
chmod +x /opt/PlexView/ELCM/server/bin/*sh

echo "generate ssl key"
${ELCM_ROOT}/script/sslKey.sh

hostname=`hostname --fqdn`
ip_addr=`hostname -i`
if [ -n "$ip_addr" ] && [ -n "$hostname" ]; then
	echo "remove localhost and add hostname to host.json"
	/usr/bin/python /opt/PlexView/ELCM/script/updateHostJson.py $ip_addr $hostname
fi

if [ -f /opt/PlexView/ELCM/datasource/comstack.json ]; then
    mkdir -p ${ELCM_ROOT}/backup
    current_time=`date +%Y-%m-%d.%H:%M:%S`
    echo "backup data as ${ELCM_ROOT}/backup/datasource_${current_time}"
    cp -r ${ELCM_ROOT}/datasource ${ELCM_ROOT}/backup/datasource_${current_time}
    ${ELCM_ROOT}/script/migration-0.7-8.py
    ${ELCM_ROOT}/script/migration-1.5-1.7.py
fi

${ELCM_ROOT}/server/bin/startup.sh
bootrc=$(grep ELCM /etc/rc.d/rc.local)
if [ -z "$bootrc" ]; then
    echo "/opt/PlexView/ELCM/server/bin/startup.sh" >>/etc/rc.d/rc.local
fi
if [ ! -f ~/.ssh/id_rsa ]; then
    echo "generate ssh key"
    ssh-keygen -f ~/.ssh/id_rsa -t rsa -N '' -q
fi
echo 'Installation of ELCM tool completed!'
echo 'Access COM ELCM from the link https://HOST_IP_ADDRESS/ as axadmin user'


