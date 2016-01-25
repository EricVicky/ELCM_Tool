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
		checkVT: function (hostip) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/cpuVTCheck", {"params": {"hostip": hostip}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		test: function (stackName) {
			var deferred = $q.defer();
		    $http.get(restUrl + "check/grReplicat_data", {"params": {"stackName": stackName}}).success(function(res) {
		        deferred.resolve({ isValid: res.succeed, message: res.message });
		    });
		    return deferred.promise;
		},
		fullbackupPreCheck:function(fullbackupConfig){
			var Res = $resource(restUrl + "check/fullbackupPreCheck");
			return Res.save(fullbackupConfig).$promise;
		},
		fullrestorePreCheck:function(fullbackupConfig){
			var Res = $resource(restUrl + "check/fullrestorePreCheck");
			return Res.save(fullbackupConfig).$promise;
		},
		databackupPreCheck:function(databackupConfig){
			var Res = $resource(restUrl + "check/databackupPreCheck");
			return Res.save(databackupConfig).$promise;
		},
		datarestorePreCheck:function(databackupConfig){
			var Res = $resource(restUrl + "check/datarestorePreCheck");
			return Res.save(databackupConfig).$promise;
		}
	};
});

