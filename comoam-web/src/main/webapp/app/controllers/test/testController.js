'use strict';

angular.module('datatest',[]).controller('testController',function ($location,$scope,DATAService,$http) {
	var URL = $location.absUrl().split("#", 1)[0];
	$scope.getData = function(){
		DATAService.getTestData().then(function(callback){
			$scope.data = callback.DATA;
		});	
	}
	
	$scope.getData_Ajax = function(){
		$http.get(URL+"data/list.json").success(function(response){
			$scope.students = response;
		});	
	}
	
	
	
	$scope.validateScore = function(){
		if(!$scope.students){
			$scope.getData_Ajax();
		}else{
			var students = JSON.parse($scope.students);
			for(var student in students){
				DATAService.getValidateScore(student.Percentage).then(function(validate){
					$scope.validate = validate;
				});	
			}
		}
	}

});