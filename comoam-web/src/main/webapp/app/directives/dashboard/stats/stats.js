'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('comoamApp')
    .directive('stats',function() {
    	return {
  		templateUrl:'app/directives/dashboard/stats/stats.html',
  		restrict:'AE',
  		replace:true,
  		scope: {
        'model': '=',
        'comments': '@',
        'number': '@',
        'name': '@',
        'colour': '@',
        'details':'@',
        'type':'@'
  		},
    	controller:function($scope, $state){
    		$scope.goDetails = function(){
    			$state.go('dashboard.chart');
    		}
    	}
  	}
  });
