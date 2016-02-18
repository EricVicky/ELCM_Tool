#!/usr/bin/python
import json
import sys
import socket
import re
import traceback

comstackfile="/opt/PlexView/ELCM/datasource/comstack.json"
hostsfile="/opt/PlexView/ELCM/datasource/hosts.json"

#get the host IP
def getHostIP():
    hostip = socket.gethostbyname(socket.gethostname())
    return hostip

#replace the deployment_preifix, localhost, 127.0.0.1
def replaceVars(comConfigStr, deployment_prefix, hostip):
    p=re.compile("\{\{\s+deployment_prefix\s+\}\}")
    comConfigStr = p.sub(deployment_prefix, comConfigStr)
    comConfig = json.loads(comConfigStr)
    if comConfig['active_host_ip'] == '127.0.0.1':
        comConfig['active_host_ip'] = hostip
    if 'host' in comConfig:
        if comConfig['host']['ip_address'] == '127.0.0.1':
            comConfig['host']['ip_address'] = hostip      
    comConfigStr = json.dumps(comConfig)
    return comConfigStr

#replace the locahost and 127.0.0.1 as real IP
def replceHosts(hostip):
    with open(hostsfile, 'r') as hostsfs:
        try:
            hosts = json.load(hostsfs)
            for host in hosts:
                 ip_address = host['ip_address']
                 if ip_address == '127.0.0.1' or ip_address == 'localhost':
                     host['ip_address'] = hostip
            with open(hostsfile, 'w') as hostsfs:
                json.dump(hosts, hostsfs,  indent=2)
        except Exception:
            print 'failed to load comstacks'
            traceback.print_exc()

#save the change to comstack.json file
def writecomconfig(comstacks):
	with open(comstackfile, 'w') as comstacksfs:
    		json.dump(comstacks, comstacksfs,  indent=2)

with open(comstackfile, 'r') as comstacksfs:
    try:
        comstacks = json.load(comstacksfs)
        hostIP = getHostIP()
        for comstack in comstacks:
	     comConfig = comstack['comConfig']
             deployment_prefix = comstack['name']
	     comConfig = replaceVars(comConfig, deployment_prefix, hostIP)		
	     comstack['comConfig'] = comConfig
	writecomconfig(comstacks)
        replceHosts(hostIP)
    except Exception:
        print 'failed to load comstacks'
        traceback.print_exc()
