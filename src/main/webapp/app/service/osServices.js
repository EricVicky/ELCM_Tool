'use strict';
angular.module('os').factory('OSService', function($location, $resource, $log) {
	var baseUrl = $location.absUrl().split("#", 1)[0];
	var restUrl = baseUrl + "rest/";
	return {
		baseUrl: baseUrl,
		restUrl: restUrl,
		getFlavorStore: function () {
			var flavorRes = $resource(restUrl + "nfv/os/compute/flavor/list");
			return flavorRes.query().$promise;
		},
		deploy: function (config, success, error) {
			var deployRes = $resource(restUrl + "os/deployment");
			deployRes.save(config,
				function (data) {
					$log.info(data);
					success(data);
				},
				// error
				function (response) {
					error(response);
				}
			);
		},
		getComTypeStore: function () {
			var comTypeRes = $resource(baseUrl + "data/comType.json");
			return comTypeRes.query().$promise;
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
		}
	};
});