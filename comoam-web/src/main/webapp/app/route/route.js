'use strict';
angular.module('comoamApp')
.config(['$stateProvider','$urlRouterProvider','$ocLazyLoadProvider',function ($stateProvider,$urlRouterProvider,$ocLazyLoadProvider) {
    $ocLazyLoadProvider.config({
      debug:false,
      events:true,
    });

    $urlRouterProvider.otherwise('/login');

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
                          name:'table_sidebarcss',
                          files:['css/table_sidebar.css']
                })
            }
        }
    }).state('dashboard.home',{
        templateUrl:'views/dashboard/dashboard.html',
        url:'/datatable'
    }).state('dashboard.test',{
        templateUrl:'views/test/test.html',
        url:'/test'
    }).state('dashboard.chart',{
        templateUrl:'views/dashboard/chart.html',
        url:'/chart',
        controller:'ChartCtrl',
        resolve: {
          loadMyFile:function($ocLazyLoad) {
            return $ocLazyLoad.load(
                'vendor/Chart.js/Chart.min.js'
            ),
            $ocLazyLoad.load({
              name:'chart.js',
              files:[
                'vendor/angular-chart/angular-chart.min.js',
                'vendor/angular-chart/angular-chart.css'
              ]
            }),
            $ocLazyLoad.load({
                name:'chart.js',
                files:['app/controllers/home/chartController.js']
            })
          }
        }
    }).state('login',{
        templateUrl:'views/pages/login.html',
        url:'/login',
        controller: 'LoginController'
    }) 
  }])
