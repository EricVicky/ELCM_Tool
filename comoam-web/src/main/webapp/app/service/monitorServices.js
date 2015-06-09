'use strict';

angular.module('monitor').factory('monitorService', function($log) {
	var stepsDict= {
			"KVM" : {
				"install" :["Start", "Generate Config Driver", "Start VM instance", "Prepare Install Options",  "Finished"],
				 "upgrade": ["Start", "Data Backup", "Post Image Replacement", "Post Configuration", "Data Restore", "Finished"],
				 "backup":["Start","Data Backup","Finished"],
				 "delete":["Start","Destroy Virtual Machine","Undefine Virtual Machine","Finished"],
				 "restore":["Start","Data Restore","Finished"],
				 "gr_pri_install":["Start","Pri GR Install","Finished"],
				 "gr_sec_install":["Start","Sec GR Install","Finished"],
				 "gr_uninstall":["Start","GR Uninstall","Finished"]
			},
			"Openstack" :{
				"install" : ["Start", "valiadtion key", "Generate Heat Templates",  "check Presence of Heat stack", "Cloud Init",  "Start COM"],
				 "upgrade": ["Start", "Data Backup", "Post Image Replacement", "Post Configuration", "Data Restore", "Finished"],
				 "backup":["Start","Data Backup","Finished"],
				 "delete":["Start","Destroy Virtual Machine","Undefine Virtual Machine","Finished"],
				 "restore":["Start","Data Restore","Finished"]
			}
	};
	var environment;
	var action;
	var channel;
	var topicPrefix= "/log/tail/" 
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
		monitorKVMBackup: function(ch) {
			environment = "KVM";
			action = "backup";
			channel = ch;
		},
		monitorKVMRestore:function(ch) {
			environment = "KVM";
			action = "restore";
			channel = ch;
		},
		monitorKVMDelete: function(ch) {
			environment = "KVM";
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
		getChannel : function (){
			return (topicPrefix  + channel);
		},
		getSteps: function(){
			return stepsDict[environment][action];
		}
	};
});