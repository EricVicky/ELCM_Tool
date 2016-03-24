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
	
})
.controller('LogoutController', function($scope, $state) {
	
	$scope.logout = function() {
		$state.go('login');
	};
	
});;
