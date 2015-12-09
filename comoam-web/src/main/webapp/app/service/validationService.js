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
		fullbackupDupcheck: function(hostip,deployment_prefix,vm_img_dir,hostname) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullbackupDupcheck", {"params": {"hostip": hostip,"deployment_prefix": deployment_prefix,"vm_img_dir": vm_img_dir,"hostname":hostname}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
			
	};
});

