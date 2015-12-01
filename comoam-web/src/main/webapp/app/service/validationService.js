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
	};
});

