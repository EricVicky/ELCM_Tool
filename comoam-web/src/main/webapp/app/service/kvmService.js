'use strict';
angular.module('kvm').factory('KVMService', function($location, $q, $resource, $log, $http) {
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl;
	var VNFType = '';
	var installEnvItems = [{'Name':'KVM','url':'kvm'},{'Name':'Openstack','url':'os'}];
	return {
		baseUrl: baseUrl,
		restUrl: restUrl,
		VNFType    : VNFType,
		getFlavorStore: function () {
			var flavorRes = $resource(restUrl + "data/kvmflavor.json");
			return flavorRes.get().$promise;
		},
		images: function(success,error) {
			var imageRes = $resource(restUrl + "rest/kvm/images");
			return imageRes.query().$promise;
		},
		deploy:function(config){
			var deployRes = $resource(restUrl + "rest/kvm/deployment");
			return deployRes.save(config).$promise;
		},
		deployOVM:function(config){
			var deployRes = $resource(restUrl + "rest/ovm/" + config.comType + "deployment");
			return deployRes.save(config).$promise;
		},
		getComTypeStore: function(success,error) {
			var comTypeRes = $resource(restUrl + "data/comType.json");
			return comTypeRes.get().$promise;
		},
		getTimezoneStore: function(success,error) {
			var timezoneRes = $resource(baseUrl + "data/timezone.json");
			return timezoneRes.query().$promise;
		},
		hostips: function(success,error) {
			var AcHostIPRes = $resource(restUrl + "rest/kvm/hostips");
			return AcHostIPRes.query().$promise;
		},
		imagelist: function(host) {
			var OamCmImagesRes = $resource(restUrl + "rest/kvm/images");
			return OamCmImagesRes.query(host).$promise;
		},
		uniqueDeploy: function(name) {
			var uniquecom = $resource(restUrl + "rest/kvm/check/unique");
			return uniquecom.get(name).$promise;
		},
		pingcheck: function(host) {
			var hostping = $resource(restUrl + "rest/check/ping");
			return hostping.get(host).$promise;
		},
		isLockedHost: function(ip_address) {
			var lockedHostRes = $resource(restUrl + "rest/check/lockedhost");
			return lockedHostRes.get({"ip_address":ip_address}).$promise;
		},
		lockedHostStatus: function(ip_address) {
			var lockedHostRes = $resource(restUrl + "rest/check/host/status");
			return lockedHostRes.get({"ip_address":ip_address}).$promise;
		},
		upgrade:function(config){
			var upgradeRes = $resource(restUrl + "rest/kvm/upgrade");
			return upgradeRes.save(config).$promise;
		},
		upgradeOVM:function(config){
			var upgradeRes = $resource(restUrl + "rest/ovm/" + config.comType + "upgrade");
			return upgradeRes.save(config).$promise;
		},
		getComInstance: function(success,error) {
			var comInstanceRes = $resource(restUrl + "rest/kvm/instances");
			return comInstanceRes.query().$promise;
		},
		deletecom:function(config){
			var name = config.deployment_prefix;
			var vnfType = config.comtype;
			var deleteRes = $resource(restUrl + "rest/kvm/instances/"+vnfType+name);
			return deleteRes.save(config).$promise;
		},
		getInstallEnvItems: function(){
			return installEnvItems;
		}
	};
});

