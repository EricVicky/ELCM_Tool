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
			$scope.studentsOBJ = response;
			$scope.students = response.Students;	
		});	
	}
	
	
	
	$scope.validateScore = function(){
		if(!$scope.studentsOBJ){
			$scope.getData_Ajax();
		}else{
			for(var student in $scope.studentsOBJ.Students){
				DATAService.getValidateScore($scope.studentsOBJ.Students[student].Percentage).then(function(validate){
					$scope.validate = validate;
				});	
			}
		}
	}

});