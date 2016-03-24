angular.module('login', [
	'ui.bootstrap',
	'ngCookies',
	'ngResource',
	'pascalprecht.translate',
])
.controller('LoginController', function($scope, $state) {
	
	$scope.login = function() {
		$state.go('dashboard.home');
	};
	
});
