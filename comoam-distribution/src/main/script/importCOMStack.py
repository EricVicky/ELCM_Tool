#!/usr/bin/python
import sys, getopt, traceback
import os.path
import json

#return: [] or stack list
def loadCOMStacksJSON(fileName):
    COMStacksJSON = []

    if os.path.exists(fileName):
       with open(fileName, 'r') as fileObject:
           try:
               COMStacksJSON = json.load(fileObject)
           except ValueError:
               COMStacksJSON = []
    else:
       print("could not find file named " + fileName)

    return COMStacksJSON

#return: None or stack
def filterCOMStackByName(stacks, name):
    if stacks and name:
       for stack in stacks:
           if stack['name'] == name:
              return stack;

    print("could not find stack named '" + name +"'")

    return None

#raise exception if duplicate stack exists 
def checkDuplicateCOMStack(stacks, stack):
    if stack and stacks:
       for iteritem in stacks:
           if iteritem['name'] == stack['name']:
              raise Exception("stack named '" + stack['name'] + "' exists!!!")

def dumpToLCMCOMStacksJSON(stacks):
    fileName = "/opt/PlexView/ELCM/datasource/comstack.json"

    if os.path.exists(fileName):
       with open(fileName, 'w') as fileObject:
            json.dump(stacks, fileObject, indent = 2)

def addToLCMCOMStacksJSON(stack):
    fileName = "/opt/PlexView/ELCM/datasource/comstack.json"

    if stack:
       stacks = loadCOMStacksJSON(fileName)
       checkDuplicateCOMStack(stacks, stack)
       stacks.append(stack)
       dumpToLCMCOMStacksJSON(stacks)

def usage():
    print(sys.argv[0] + " {-h|--help}")
    print(sys.argv[0] + " {-f|--from <comstack.json>} {-s|--stack <stack name>}")
    print("for example:")
    print(sys.argv[0] + " -h")
    print(sys.argv[0] + " -f comstack.json -s cprs")
    print(sys.argv[0] + " --from comstack.json --stack cprs")

def main():
   COMStack = ''
   COMStackName = ''
   COMStacks = ''
   COMStacksFileName = ''

   try:
      opts, args = getopt.getopt(sys.argv[1:], "hf:s:", ["help", "from=", "stack="])
   except getopt.GetoptError:
      usage()
      sys.exit()

   for opt, arg in opts:
      if opt in ('-h', "--help"):
         usage()
         sys.exit()
      elif opt in ("-s", "--stack"):
         COMStackName = arg
      elif opt in ("-f", "--from"):
         COMStacksFileName = arg
      else:
         usage()
         sys.exit()

   if not opts or not COMStackName or not COMStacksFileName:
      usage()
      sys.exit()

   COMStacks = loadCOMStacksJSON(COMStacksFileName)
   COMStack = filterCOMStackByName(COMStacks, COMStackName)

   if COMStack:
      addToLCMCOMStacksJSON(COMStack)

if __name__ == "__main__":
   main()
