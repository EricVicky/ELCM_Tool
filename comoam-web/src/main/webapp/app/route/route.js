'use strict';
angular.module('comoamApp')
.config(['$stateProvider','$urlRouterProvider','$ocLazyLoadProvider',function ($stateProvider,$urlRouterProvider,$ocLazyLoadProvider) {
    $ocLazyLoadProvider.config({
      debug:false,
      events:true,
    });

    $urlRouterProvider.otherwise('login');

    $stateProvider
      .state('dashboard', {
        url:'/dashboard',
        templateUrl: 'views/dashboard/main.html',
        resolve: {
            loadMyDirectives:function($ocLazyLoad){
                return $ocLazyLoad.load(
                {
                    name:'comoamApp',
                    files:[
                    'app/directives/header/header.js',
                    'app/directives/header/header-notification/header-notification.js',
                    'app/directives/sidebar/sidebar.js',
                    'app/directives/sidebar/sidebar-search/sidebar-search.js'
                    ]
                }),
                $ocLazyLoad.load(
                {
                   name:'toggle-switch',
                   files:["vendor/angular-toggle-switch/angular-toggle-switch.min.js",
                          "vendor/angular-toggle-switch/angular-toggle-switch.css"
                      ]
                }),
                $ocLazyLoad.load(
                {
                  name:'logview',
                  files:['app/directives/ansiblelog/logview.js']
                }),
                $ocLazyLoad.load(
                {
                  name:'hostcheck',
                  files:['app/directives/validation/hostcheck.js']
                }),
                $ocLazyLoad.load(
                        {
                          name:'stackcheck',
                          files:['app/directives/validation/stackcheck.js']
                        }),
                $ocLazyLoad.load(
                {
                  name:'comcheck',
                  files:['app/directives/validation/comcheck.js']
                }),
                $ocLazyLoad.load(
                        {
                          name:'comcheck',
                          files:['app/directives/import/import.js']
                }),
                $ocLazyLoad.load(
                        {
                          name:'oamflavor',
                          files:['app/directives/flavorinit.js']
                }),
                $ocLazyLoad.load(
                        {
                          name:'table_sidebarcss',
                          files:['css/table_sidebar.css']
                })
            }
        }
    }).state('dashboard.home',{
        templateUrl:'views/dashboard/dashboard.html',
        url:'/datatable'
    })
      .state('dashboard.kvminstall',{
        templateUrl:'views/kvm/install_kvm.html',
        url:'/kvminstall'
    }).state('login',{
        templateUrl:'views/pages/login.html',
        url:'/login',
        controller: 'LoginController'
    }) 
  }])
