'use strict';

angular.module('datatest',[]).controller('testController',function ($location,$scope,DATAService,$http) {
	var URL = $location.absUrl().split("#", 1)[0];
	$scope.getData = function(){
		DATAService.getTestData().then(function(callback){
			$scope.data = callback.DATA;
		});	
	}
	
	$scope.getData_Ajax = function(){
		$http.get(URL+"data/testdata.json").success(function(response){
			$scope.data = response.DATA;
		});	
	}

});