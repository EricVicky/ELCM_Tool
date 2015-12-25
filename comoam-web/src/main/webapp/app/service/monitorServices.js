'use strict';

angular.module('monitor').factory('monitorService', function($log, $location, $resource) {
	var stepsDict= {
			"KVM" : {
				"install" :["Start", "Generate Config Driver", "Start VM Instance", "Prepare Install Options",  "Finished"],
				"upgrade": ["Start", "Data Backup", "Prepare Virtual Machines", "Post Image Replacement", "COM Upgrade","Data Restore", "Finished"],
				 "backup":["Start","Data Backup","Finished"],
				 "fullbackup":["Start","Full Backup","Finished"],
				 "fullrestore":["Start","Full Restore","Finished"],
				 "delete":["Start","Destroy Virtual Machine","Undefine Virtual Machine","Finished"],
				 "restore":["Start","Data Restore","Finished"],
				 "gr_pri_install":["Start","Pri GR Install","Finished"],
				 "gr_sec_install":["Start","Sec GR Install","Finished"],
				 "gr_uninstall":["Start","GR Uninstall","Finished"],
				 "chhostname":["Start","Changing Hostname","Finished"]
			},
			"Openstack" :{
				"install" : ["Start", "Valiadtion Key", "Generate Heat Templates",  "Check Presence of Heat Stack", "Cloud Init",  "Start COM", "Finished"],
				 "upgrade": ["Start", "Data Backup", "Post Image Replacement", "Post Configuration", "Data Restore", "Finished"],
				 "backup":["Start","Data Backup","Finished"],
				 "delete":["Start","Check Presence of stack","Destroy stack","Finished"],
				 "restore":["Start","Data Restore","Finished"],
				 "gr_pri_install":["Start","Pri GR Install","Finished"],
				 "gr_sec_install":["Start","Sec GR Install","Finished"],
				 "gr_uninstall":["Start","GR Uninstall","Finished"],
				 "chhostname":["Start","Changing Hostname","Finished"]
			},
			"KVM_OVM" :{
				"install" : ["Start", "Generate Config Driver", "Start VM Instance", "Post Configuration", "Finished"],
				"upgrade":["Start","Data Backup","Start Virtual Machines","Finished"],
				"delete":["Start","Destroy Virtual Machines","Undefine Virtual Machines","Delete Virtual Machine Files","Finished"]
			},
			"KVM_QOSAC":{
				"install":["Start", "Generate Config Driver", "Start VM Instance", "Post Configuration", "Finished"],
				"upgrade":["Start","Data Backup","Prepare Virtual Machines","Post Image Replacement","Data Restore","Finished"],
				"delete":["Start","Destroy Virtual Machines","Undefine Virtual Machines","Delete Virtual Machine Files","Finished"],
				"backup":["Start","Data Backup","Finished"],
				"restore":["Start","Data Restore","Finished"],
				"chhostname":["Start","Changing Hostname","Finished"],
			},
			"KVM_ARS":{
				"install":["Start", "Prepare Environment", "Install", "Finished"]			
			},
			"Openstack_QOSAC":{
				"install":["Start", "Running Credentials", "Update Document", "Configure new disk drive", "Finished"],
				"upgrade": ["Start", "Data Backup", "Update Document", "Heat status", "Configure new disk drive", "Data Restore", "Finished"],
				"delete":["Start","Check Presence of stack","Destroy stack","Finished"],
				"chhostname":["Start","Changing Hostname","Finished"],
				"backup":["Start","Data Backup","Finished"],
				"restore":["Start","Data Restore","Finished"],
			},
			"Openstack_OVM":{
				"install":["Start", "Running Credentials", "Update Document", "Config switches for OVM", "Finished"],
			    "delete":["Start","Check Presence of stack","Destroy stack","Finished"]
			},
			"Openstack_ARS":{
				"install":["Start", "Prepare Environment", "Install", "Finished"]			
			},
	};
	
	var endMsg = {
			"install": {
				"succeed" : "Installation Completed",
				"failed": "Installation Failed"
			},
			"upgrade":{
				"succeed" : "Upgrade Completed",
				"failed": "Upgrade Failed"
			},
			"backup":{
				"succeed" : "Backup Completed",
				"failed": "Backup Failed"
			},
			"fullbackup":{
				"succeed" : "FullBackup Completed",
				"failed": "FullBackup Failed"
			},
			"fullrestore":{
				"succeed" : "FullRestore Completed",
				"failed": "FullRestore Failed"
			},
			"delete":{
				"succeed" : "Destroyed",
				"failed": "Deletion failed"
			},
			"restore":{
				"succeed" : "Restore completed!",
				"failed": "Restore failed"
			},
			"gr_pri_install":{
				"succeed" : "Installation of Primary completed!",
				"failed": "Installation of Primary failed"
			},
			"gr_sec_install":{
				"succeed" : "Installation of GR on secondary completed!",
				"failed": "Installation of GR on secondary  failed"
			},
			"gr_uninstall":{
				"succeed" : "Uninstallation of GR completed!",
				"failed": "Uninstallation  of GR failed"
			},
			"chhostname":{
				"succeed" : "Change Hostname completed!",
				"failed": "Change Hostname failed"
			}
	}
	var environment;
	var action;
	var channel;
	var topicPrefix= "/log/tail/";
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl;
	return {
		monitorKVMInstall: function(ch) {
			environment = "KVM";
			action = "install";
			channel = ch;
		},
		monitorKVMUpgrade: function(ch) {
			environment = "KVM";
			action = "upgrade";
			channel = ch;
		},
		monitorKVMCHHostname: function(ch) {
			environment = "KVM";
			action = "chhostname";
			channel = ch;
		},
		monitorKVMQOSACCHHostname: function(ch) {
			environment = "KVM_QOSAC";
			action = "chhostname";
			channel = ch;
		},
		monitorOSCHHostname: function(ch) {
			environment = "Openstack";
			action = "chhostname";
			channel = ch;
		},
		monitorOSQOSACCHHostname: function(ch) {
			environment = "OPENSTACK_QOSAC";
			action = "chhostname";
			channel = ch;
		},
		monitorKVMBackup: function(ch,comType) {
			if(comType=='QOSAC'){
				environment = "KVM_QOSAC";
			}
			environment = "KVM";
			action = "backup";
			channel = ch;
		},
		monitorKVMRestore:function(ch,comType) {
			if(comType=='QOSAC'){
				environment = "KVM_QOSAC";
			}
			environment = "KVM";
			action = "restore";
			channel = ch;
		},
		
		monitorKVMfullBackup: function(ch,comType) {
			if(comType=='QOSAC'){
				environment = "KVM_QOSAC";
			}
			environment = "KVM";
			action = "fullbackup";
			channel = ch;
		},
		
		monitorKVMfullRestore: function(ch,comType) {
			if(comType=='QOSAC'){
				environment = "KVM_QOSAC";
			}
			environment = "KVM";
			action = "fullrestore";
			channel = ch;
		},
		monitorKVMDelete: function(ch,comType) {
			if(comType=='FCAPS'||comType=='CM'||comType=='OAM'){
				environment = "KVM";
			}else if(comType=='QOSAC'||comType=='ARS'){
				environment = "KVM"+"_"+comType;
			}else{
				environment = "KVM_OVM";
			}
			action = "delete";
			channel = ch;
		},
		monitorKVMGR_Pri_Install: function(ch) {
			environment = "KVM";
			action = "gr_pri_install";
			channel = ch;
		},
		monitorKVMGR_Sec_Install: function(ch) {
			environment = "KVM";
			action = "gr_sec_install";
			channel = ch;
		},
		monitorKVMGR_UnInstall: function(ch) {
			environment = "KVM";
			action = "gr_uninstall";
			channel = ch;
		},
		monitorOSInstall: function(ch) {
			environment = "Openstack";
			action = "install";
			channel = ch;
		},
		monitorOSUpgrade: function(ch) {
			environment = "Openstack";
			action = "upgrade";
			channel = ch;
		},
		monitorOSBackup: function(ch) {
			environment = "Openstack";
			action = "backup";
			channel = ch;
		},
		monitorOSRestore: function(ch) {
			environment = "Openstack";
			action = "restore";
			channel = ch;
		},
		monitorOSDelete: function(ch) {
			environment = "Openstack";
			action = "delete";
			channel = ch;
		},
		monitorKVMOVMInstall: function(ch) {
			environment = "KVM_OVM";
			action = "install";
			channel = ch;
		},
		monitorKVMOVMUpgrade: function(ch) {
			environment = "KVM_OVM";
			action = "upgrade";
			channel = ch;
		},
		monitorKVMQOSACUpgrade: function(ch) {
			environment = "KVM_QOSAC";
			action = "upgrade";
			channel = ch;
		},
		monitorKVMQOSACInstall:function(ch) {
			environment = "KVM_QOSAC";
			action = "install";
			channel = ch;
		},
		monitor: function(env, act, comType, ch){
			environment = env;
			action = act.toLowerCase();
			channel  = ch;
			if(comType == "ARS" || comType == "QOSAC"){
				environment = env + "_" + comType;
			}else if(comType == "ATC" || comType == "HPSIM"){
				environment = env + "_OVM";
			}
		},
		runAnsibleTask : function(){
			var runAnsibleRes = $resource(restUrl + "rest/ansible/task");
			return runAnsibleRes.get({"comStack" : channel}).$promise;
		},
		monitorKVMQOSACDelete: function(ch) {
			environment = "KVM_QOSAC";
			action = "delete";
			channel = ch;
		},
		monitorKVMARSInstall: function(ch) {
			environment = "KVM_ARS";
			action = "install";
			channel = ch;
		},
		getChannel : function (){
			return (topicPrefix  + channel);
		},
		getSteps: function(){
			return stepsDict[environment][action];
		},
		getEndMsg: function(res){
			return endMsg[action][res];
		}
	};
});