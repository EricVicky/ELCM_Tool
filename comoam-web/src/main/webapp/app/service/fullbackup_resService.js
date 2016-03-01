'use strict';
angular.module('fullbackup_restore').factory('fullBackup_ResService', function($location, $q, $resource, $log, $http) {
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl;
	return {
		baseUrl: baseUrl,
		restUrl: restUrl,
		getComInstance: function(success,error) {
			var comInstanceRes = $resource(restUrl + "rest/kvm/instances");
			return comInstanceRes.query().$promise;
		},
		kvmfullbackup:function(fullbackupConfig){
			var fullbackupRes = $resource(restUrl + "rest/kvm/fullbackup");
			return fullbackupRes.save(fullbackupConfig).$promise;
		},
		kvmfullrestore:function(fullrestoreConfig){
			var fullrestoreRes = $resource(restUrl + "rest/kvm/fullrestore");
			return fullrestoreRes.save(fullrestoreConfig).$promise;
		},
		kvmatcfullbackup:function(fullbackupConfig){
			var fullbackupRes = $resource(restUrl + "rest/kvm/atc/fullbackup");
			return fullbackupRes.save(fullbackupConfig).$promise;
		},
		kvmatcfullrestore:function(fullrestoreConfig){
			var fullrestoreRes = $resource(restUrl + "rest/kvm/atc/fullrestore");
			return fullrestoreRes.save(fullrestoreConfig).$promise;
		},
		kvmqosacfullbackup:function(fullbackupConfig){
			var fullbackupRes = $resource(restUrl + "rest/kvm/qosac/fullbackup");
			return fullbackupRes.save(fullbackupConfig).$promise;
		},
		kvmqosacfullrestore:function(fullrestoreConfig){
			var fullrestoreRes = $resource(restUrl + "rest/kvm/qosac/fullrestore");
			return fullrestoreRes.save(fullrestoreConfig).$promise;
		},
                kvmgangliafullbackup:function(fullbackupConfig){
                        var fullbackupRes = $resource(restUrl + "rest/kvm/ganglia/fullbackup");
                        return fullbackupRes.save(fullbackupConfig).$promise;
                },
                kvmgangliafullrestore:function(fullrestoreConfig){
                        var fullrestoreRes = $resource(restUrl + "rest/kvm/ganglia/fullrestore");
                        return fullrestoreRes.save(fullrestoreConfig).$promise;
                },
//		osfullbackup:function(config){
//			var backupRes = $resource(restUrl + "rest/kvm/backup");
//			return backupRes.save(config).$promise;
//		},
	};
});

