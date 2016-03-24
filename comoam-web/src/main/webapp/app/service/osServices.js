'use strict';
angular.module('os').factory('OSService', function($location, $resource, $log) {
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl + "rest/";
	var VNFType = '';
	return {
		baseUrl: baseUrl,
		restUrl: restUrl,
		VNFType    : VNFType,
		getFlavorStore: function () {
			var flavorRes = $resource(restUrl + "nfv/os/compute/flavor/list");
			return flavorRes.query().$promise;
		},
		getHostNameStore:function() {
			var hostnameRes = $resource(baseUrl + "data/hostname.json");
			return hostnameRes.get().$promise;
		},
		deploy:function(config){
			var deployRes = $resource(restUrl + "os/deployment");
			return deployRes.save(config).$promise;
		},
		deployOVM:function(config){
			var deployRes = $resource(restUrl + "os/ovm/" + config.comType + "deployment");
			return deployRes.save(config).$promise;
		},
		getComInstance: function(success,error) {
			var comInstanceRes = $resource(restUrl + "os/instances");
			return comInstanceRes.query().$promise;
		},
		getComTypeStore: function () {
			var comTypeRes = $resource(baseUrl + "data/comType.json");
			return comTypeRes.get().$promise;
		},
		getTimezoneStore: function () {
			var timezoneRes = $resource(baseUrl + "data/timezone.json");
			return timezoneRes.query().$promise;
		},
		getComputeAvailZoneStore: function () {
			var computeavailzoneRes = $resource(restUrl + "nfv/os/compute/availabilityzone/list");
			return computeavailzoneRes.query().$promise;
		},
		getNetworkStore: function () {
			var networkRes = $resource(restUrl + "nfv/os/neutron/network/list");
			return networkRes.query().$promise;
		},
		getSubNetworkStore: function () {
			var subNetworkRes = $resource(restUrl + "nfv/os/neutron/subnet/list/names");
			return subNetworkRes.query().$promise;
		},
		
		updateOSCred: function(credential) {
			var OSCredRes = $resource(restUrl + "os/uCred");
			return OSCredRes.save(credential).$promise;
		},
		getUpdateOSCred: function() {
			var OSCredRes = $resource(restUrl + "os/rCred");
			return OSCredRes.get().$promise;
		},
		getSubnets: function (networkId){
			var subnetRes = $resource(restUrl + "nfv/os/neutron/" + networkId + "/subnet/list/names");
			return subnetRes.query().$promise;
		},
		getV4Subnets: function (networkId){
			var subnetRes = $resource(restUrl + "nfv/os/neutron/" + networkId + "/subnet/v4/list/names");
			return subnetRes.query().$promise;
		},
		getV6Subnets: function (networkId){
			var subnetRes = $resource(restUrl + "nfv/os/neutron/" + networkId + "/subnet/v6/list/names");
			return subnetRes.query().$promise;
		},
		getcinderzones: function (){
			var cinderRes = $resource(restUrl + "nfv/os/cinder/zone/list");
			return cinderRes.query().$promise;
		},
		getImages: function(){
			var imgRes = $resource(restUrl + "nfv/os/glance/image/list");
			return imgRes.query().$promise;
		},
		getKeys: function(){
			var keyRes = $resource(restUrl + "nfv/os/compute/keypair/list/names");
			return keyRes.query().$promise;
		},
		uniqueDeploy: function(name) {
			var uniquecom = $resource(restUrl + "os/check/unique");
			return uniquecom.get(name).$promise;
		},
		imagelist: function(host) {
			var OamCmImagesRes = $resource(restUrl + "os/images");
			return OamCmImagesRes.query(host).$promise;
		},
		upgrade:function(config){
			var upgradeRes = $resource(restUrl + "os/upgrade");
			return upgradeRes.save(config).$promise;
		},
		chOSHostname:function(config){
			var hostnameRes = $resource(restUrl + "os/chhostname");
			return hostnameRes.save(config).$promise;
		},
		chOSQosacHostname:function(config){
			var qohostnameRes = $resource(restUrl + "os/qosac/chhostname");
			return qohostnameRes.save(config).$promise;
		},
		upgradeOVM:function(config){
			var upgradeRes = $resource(restUrl + "os/ovm/" + config.comType + "upgrade");
			return upgradeRes.save(config).$promise;
		},
		deletecom:function(config){
			var name = config.deployment_prefix;
			var deleteRes = $resource(restUrl + "os/instances/"+name);
			return deleteRes.save(config).$promise;
		},
		deleteovm:function(config){
			var deleteRes = $resource(restUrl + "os/ovm/" + config.comType + "delete");
			return deleteRes.save(config).$promise;
		},
		validateCred:function(){
			var CredRes = $resource(restUrl + "os/vCred");
			return CredRes.get().$promise;
		}
	};
});