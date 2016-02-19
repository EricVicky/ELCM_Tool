#!/usr/bin/python
import json
import sys
import traceback

if len(sys.argv) < 3:
   print 'illegal argument'
   sys.exit(1)
entry = {'ip_address': sys.argv[1], 'name': sys.argv[2]}

with open('/opt/PlexView/ELCM/datasource/hosts.json', 'r') as hostsjson:
    try:
        feeds = json.load(hostsjson)
    except Exception: 
        feeds = []

with open('/opt/PlexView/ELCM/datasource/hosts.json', 'w') as hostsjson:
	for host in feeds:
		if entry['name'] == host['name']:
			host['name'] = entry['name']
			host['ip_address'] = entry['ip_address']
			json.dump(feeds, hostsjson, indent=2)
			sys.exit(0)
	feeds.append(entry)
	json.dump(feeds, hostsjson, indent=2)
