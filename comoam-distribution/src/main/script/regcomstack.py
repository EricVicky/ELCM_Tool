#!/usr/bin/python

import sys, getopt,traceback
import os.path
import yaml
import json
import time
import re
import socket

class Usage(Exception):
    def __init__(self, msg):
            self.msg = msg

def error(message):
    sys.stderr.write("error: %s\n" % message)
    sys.exit(1)


class COMStack():

    comstackfile="/opt/PlexView/ELCM/datasource/comstack.json"

    def __init__(self, comType=None, name=None, status='STANDALONE', actionResult='MIGRATE_SUCCEED', **kwargs ):
            self.comType = comType
            self.name = name
            self.status = status
            self.actionResult = actionResult
            self.updatedate = int(round(time.time() * 1000))

    def setComconfig(self, comConfig):
            self.comConfig = comConfig

    def append(self):
            comstacks = self.loadcomstacks()
            comstacks.append(self.__dict__)
	    with open(self.comstackfile, 'w') as comstacksfs:
    		json.dump(comstacks, comstacksfs,  indent=2)

    def loadcomstacks(self):
        with open(self.comstackfile, 'r') as comstacksfs:
            try:
                comstacks = json.load(comstacksfs)
                return comstacks
            except ValueError:
                comstacks = []
                return comstacks
        return None

def getRealIP(host):
    if host == '127.0.0.1' or host == 'localhost': 
        host = socket.gethostbyname(socket.gethostname())
    return host

def error(message):
    sys.stderr.write("error: %s\n" % message)
    sys.exit(1)

def reg(varfilename,host):
    if os.path.exists(varfilename) :
        print 'load com stacks'
        try:
            with open(varfilename, 'r') as stream:
                comConfig = yaml.load(stream)
                comConfig['environment'] = 'KVM'
                comConfig['active_host_ip'] = host
                print 'create new com stack'
                comStack = COMStack(comConfig['comType'], comConfig['deployment_prefix'])
                comConfigStr = json.dumps(comConfig)
                p=re.compile("\{\{\s*deployment_prefix\s*\}\}")
                comConfigStr = p.sub(comConfig['deployment_prefix'], comConfigStr)
                comStack.setComconfig(comConfigStr)
                comStack.append()
                print 'stack created completed!'
        except Exception:
            error("failed to  save stack")
            raise Usage('failed to  save stack')
    else:
            error("failed to open the file {0}".format(varfilename))
            raise Usage('failed to  save stack')
        
def main(argv):
   varfilename = ''
   host = '127.0.0.1'
   try:
      opts, args = getopt.getopt(argv,"hv:i:",["vfile=", "ifile="])
   except getopt.GetoptError:
      raise Usage('regcomstack.py -v <group_var/all>')
   for opt, arg in opts:
      if opt == '-h':
         Usage('regcomstack.py -v <group_var/all>')
         sys.exit()
      elif opt in ("-v", "--vfile"):
         varfilename = arg
      elif opt in ("-i", "--ifile"):
         host = arg
   try:
      host = getRealIP(host) 
      reg(varfilename, host)
   except Exception:
      traceback.print_exc(file=sys.stdout)
      error("failed to register comstack")

if __name__ == "__main__":
   main(sys.argv[1:])
