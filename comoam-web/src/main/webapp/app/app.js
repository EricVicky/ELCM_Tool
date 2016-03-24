'use strict';
angular.module('comoamApp', [
    'oc.lazyLoad',
    'ui.router',
    'ui.bootstrap',
    'dialogs',
    'angular-loading-bar',
    'ghiscoding.validation',
    'pascalprecht.translate',
    'kvm',
    'os',
    'rcWizard', 
    'mgo-angular-wizard',
    'rcForm', 
    'login',
    'datatable'
  ]).controller('AppController', function($rootScope, $scope, $translate, $cookieStore, $window, $state, $location, $log, $modal, Auth) {
    $rootScope._user = {
        username: $cookieStore.get("username") || "",
        password: "",
        rememberme: true,
        language: "en",
        reason: ""
    };

    $scope.lang = 'en-US';
    $scope.language = 'English';


    $rootScope.showNoPermission = function() {
        $modal.open({
            templateUrl: 'app/comApp/view/noPermission.html',
            controller: ModalInstanceCtrl,
            size: '',
            resolve: {
                node: function() {
                    return $scope.node;
                }
            }
        });
    }

    $rootScope.showError = function(errMsg) {
        alert(errMsg);
    }

    $(document).click(function() {
    	$cookieStore.put("lastClickedTime", new Date().getTime());
    	//$log.info("Clicked: " + $cookieStore.get("lastClickedTime"));
    });
    
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
        $log.info("State change from " + angular.toJson(fromState) + ", to " + angular.toJson(toState));
        $log.info("location=" + $location.path());

        if (toState.name === "noChange") {
            $log.info("Should keep the current state, no change");
            event.preventDefault();
        }
 
    	var currentURL = window.location.href;
        if ((!$cookieStore.get("token") || !$cookieStore.get("username")) && (currentURL.indexOf('#') != -1 || currentURL.indexOf("/login") != -1)) {
        	$(document).ready(function() {
            	setTimeout('window.location.replace("' + currentURL.substring(0, currentURL.indexOf("#")) + '#/login");', 1);
        	});	
        }
    });
});

