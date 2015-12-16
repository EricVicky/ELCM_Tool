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
		fullbackupPreCheck: function(hostip,deployment_prefix,vm_img_dir,remoteip,remotedir) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullbackupPreCheck", {"params": {"hostip": hostip,"deployment_prefix": deployment_prefix,"vm_img_dir": vm_img_dir,"remoteip":remoteip,"remotedir":remotedir}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		fullrestorePreCheck: function(hostip,deployment_prefix,vm_img_dir,remoteip,remotedir) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/fullrestorePreCheck", {"params": {"hostip": hostip,"deployment_prefix": deployment_prefix,"vm_img_dir": vm_img_dir,"remoteip":remoteip,"remotedir":remotedir}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		databackupPreCheck: function(oamip,dbip,cmip,localdir,filename,remoteip,remotedir) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/databackupPreCheck", {"params": {"oamip": oamip,"dbip": dbip,"cmip": cmip,"localdir": localdir,"filename": filename,"remoteip":remoteip,"remotedir":remotedir}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.mutiMessage });
		    });
		    return deferred.promise;
		},
		datarestorePreCheck: function(oamip,dbip,cmip,localdir,filename,remoteip,remotedir) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/datarestorePreCheck", {"params": {"oamip": oamip,"dbip": dbip,"cmip": cmip,"localdir": localdir,"filename": filename,"remoteip":remoteip,"remotedir":remotedir}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.mutiMessage });
		    });
		    return deferred.promise;
		},
		

	};
});

