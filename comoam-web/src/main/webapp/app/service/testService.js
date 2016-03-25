'use strict';
angular.module('datatest').factory('DATAService', function($location,$resource) {
	var URL = $location.absUrl().split("#", 1)[0];
	return {
		getTestData : function () {
			var data = $resource(URL + "data/testdata.json");
			return data.get().$promise;
		}
	};
});

