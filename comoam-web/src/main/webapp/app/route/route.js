'use strict';
angular.module('comoamApp')
.config(['$stateProvider','$urlRouterProvider','$ocLazyLoadProvider',function ($stateProvider,$urlRouterProvider,$ocLazyLoadProvider) {
    $ocLazyLoadProvider.config({
      debug:false,
      events:true,
    });

    $urlRouterProvider.otherwise('/dashboard/datatable');

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
    })
      .state('dashboard.home',{
        url:'/home',
        controller: 'MainCtrl',
        templateUrl:'views/dashboard/home.html',
        resolve: {
          loadMyFiles:function($ocLazyLoad) {
            return $ocLazyLoad.load({
              name:'comoamApp',
              files:[
              'app/controllers/home/main.js',
              'app/directives/dashboard/stats/stats.js',
              'app/directives/networkTopo/networkTopo.js'
              ]
            })
          }
        }
      })
      .state('dashboard.datatable',{
        templateUrl:'views/dashboard/datatable.html',
        url:'/datatable'
    })
      .state('dashboard.kvminstall',{
        templateUrl:'views/kvm/install_kvm.html',
        url:'/kvminstall'
    })
      .state('dashboard.kvmupgrade',{
        templateUrl:'views/kvm/upgrade_kvm.html',
        url:'/kvmupgrade'
    })
      .state('dashboard.osupgrade',{
        templateUrl:'views/os/upgrade_os.html',
        url:'/osupgrade'
    })
    .state('dashboard.osinstall',{
        templateUrl:'views/os/install_os.html',
        url:'/osinstall'
    })
      .state('login',{
        templateUrl:'views/pages/login.html',
        url:'/login',
        controller: 'LoginController'
    })
      .state('dashboard.oscredential',{
        templateUrl:'views/os/credential.html',
        url:'/oscredential',
    })
      .state('dashboard.monitor',{
        templateUrl:'views/monitor/monitor.html',
        url:'/monitor',
    })
    .state('dashboard.backup',{
        templateUrl:'views/backup_restore/backup.html',
        url:'/backup',
    })
    .state('dashboard.restore',{
        templateUrl:'views/backup_restore/restore.html',
        url:'/restore',
    })
    
    .state('dashboard.fullbackup',{
        templateUrl:'views/backup_restore/fullbackup.html',
        url:'/fullbackup',
    })
    .state('dashboard.fullrestore',{
        templateUrl:'views/backup_restore/fullrestore.html',
        url:'/fullrestore',
    })
    
    .state('dashboard.chhostname',{
        templateUrl:'views/chhostname/chhostname.html',
        url:'/chhostname',
    })
    .state('dashboard.grinstall',{
        templateUrl:'views/gr/install.html',
        url:'/gr/install',
    })
    .state('dashboard.gruninstall',{
        templateUrl:'views/gr/uninstall.html',
        url:'/gr/uninstall',
    })
    .state('dashboard.kvmovminstall', {
    	templateUrl:'views/optionalVNF/kvm/install.html',
    	url:'/kvmovminstall',
    })
    .state('dashboard.kvmovmupgrade', {
    	templateUrl:'views/optionalVNF/kvm/qosac_upgrade.html',
    	url:'/kvmovmupgrade',
    })
    .state('dashboard.kvmarsinstall', {
    	templateUrl:'views/optionalVNF/kvm/ars_install.html',
    	url:'/kvmarsinstall',
    })
    .state('dashboard.kvmqosacinstall', {
    	templateUrl:'views/optionalVNF/kvm/qosac_install.html',
    	url:'/kvmqosacinstall',
    })
    .state('dashboard.kvmqosacupgrade', {
    	templateUrl:'views/optionalVNF/kvm/qosac_upgrade.html',
    	url:'/kvmqosacupgrade',
    })
    .state('dashboard.osovminstall', {
    	templateUrl:'views/optionalVNF/os/install.html',
    	url:'/osovminstall',
    })
    .state('dashboard.osqosacinstall', {
    	templateUrl:'views/optionalVNF/os/qosac_install.html',
    	url:'/osqosacinstall',
    })
    .state('dashboard.osarsinstall', {
    	templateUrl:'views/optionalVNF/os/ars_install.html',
    	url:'/osarsinstall',
    })
    .state('dashboard.osqosacupgrade', {
    	templateUrl:'views/optionalVNF/os/qosac_upgrade.html',
    	url:'/osqosacupgrade',
    })
    .state('dashboard.operationlog', {
    	templateUrl:'views/operationlog/operationlog.html',
    	url:'/operationlog',
    })
    .state('dashboard.addPort', {
    	templateUrl:'views/kvm/addPort.html',
    	url:'/addPort',
    })
    .state('dashboard.addIPV6', {
    	templateUrl:'views/kvm/addIPV6.html',
    	url:'/addIPV6',
    })
  }])