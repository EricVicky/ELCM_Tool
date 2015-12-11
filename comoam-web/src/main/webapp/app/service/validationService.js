'use strict';
angular.module('validation',[]).factory('validationService', function($location, $q, $resource, $log, $http) {
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl + "rest/";
	return {
		restUrl: restUrl,
		ping: function (ip) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/ping", {"params": {"host": ip}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		backupPrecheck: function(dir,oamip,dbip,cmip) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/backupPrecheck", {"params": {"dir": dir,"oamip": oamip,"dbip": dbip,"cmip": cmip}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		backupNfsPrecheck: function(dir,nfsip,oamip) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/backupNfsPrecheck", {"params": {"dir": dir,"nfsip": nfsip,"oamip": oamip}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		fullbackupExistCheck: function(hostip,deployment_prefix,vm_img_dir,hostname) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullbackupExistCheck", {"params": {"hostip": hostip,"deployment_prefix": deployment_prefix,"vm_img_dir": vm_img_dir,"hostname":hostname}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		fullbackupNfsPrecheck: function(vm_img_dir,deployment_prefix,hostip,nfsip,nfsdir) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullbackupNfsPrecheck", {"params": {"vm_img_dir": vm_img_dir,"deployment_prefix": deployment_prefix,"hostip": hostip,"nfsip": nfsip,"nfsdir": nfsdir}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		fullrestoreNfsPrecheck: function(vm_img_dir,deployment_prefix,hostip,nfsip,nfsdir,hostname) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullrestoreNfsPrecheck", {"params": {"vm_img_dir": "/lvvm03/stTest","deployment_prefix": "EricTest","hostip": "135.251.236.98","nfsip": "135.252.138.135","nfsdir": "/var/images/EricTest","hostname": "EricTestM001OAM01"}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed,isExist: res.exist, message: res.message });
		    });
		    return deferred.promise;
		},
	};
});

