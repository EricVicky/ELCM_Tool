angular.module('os', [ 'ui.router', 
                       'ui.bootstrap', 
                       'rcWizard',
                       'rcForm',  
                       'websocket', 
                       'ghiscoding.validation', 
                       'mgo-angular-wizard',
                       'ngFileUpload',
                       'validation',
                       'ngResource']).controller('osctr', function($scope, $q, $timeout, $log, OSService,$modal,
		$state, websocketService, validationService, WizardHandler,monitorService,timezoneService, validationService) {
            OSService.getUpdateOSCred().then(function(data) {
                $scope.crendential = data;
                if($scope.crendential.osUsername == "" && $scope.crendential.osPassword == ""){
                	alert("config first");
                	$state.go("dashboard.oscredential");
                }
            });
            $scope.heat_version = [ {"name":"juno", "version": "2014-10-16" }];
            $scope.config_drive = [ 'True', 'False' ];
            $scope.installConfig ={};
            

            $scope.genExport = function(){
            	$scope.export=!$scope.export;
            }
            $scope.$watchGroup(['installConfig.root_password', 'installConfig.re_root_password','installConfig.axadmin_password','installConfig.re_axadmin_password'], function() {
            	if($scope.installConfig.root_password!=$scope.installConfig.re_root_password||$scope.installConfig.axadmin_password!=$scope.installConfig.re_axadmin_password){
            		$scope.disMatch = true;
            	}else{
            		$scope.disMatch = false;
            	}
            });
            $scope.reset_password = function(){
             	if($scope.disMatch == true){
             		$scope.installConfig.root_password = "";
             		$scope.installConfig.re_root_password = "";
             		$scope.installConfig.axadmin_password = "";
             		$scope.installConfig.re_axadmin_password = "";
             	}
             };
            $scope.HostNameChanged = false;
            $scope.initHostName = function(){
            	if(!$scope.HostNameChanged){
            		OSService.getHostNameStore().then(function(data){
                    	$scope.oam_suffix = data[$scope.installConfig.comType].oam;
                        $scope.db_suffix = data[$scope.installConfig.comType].db;
                        $scope.cm_suffix = data[$scope.installConfig.comType].cm;
                        $scope.installConfig.vm_config.oam.hostname = $scope.installConfig.deployment_prefix + $scope.oam_suffix;
                        $scope.installConfig.vm_config.db.hostname = $scope.installConfig.deployment_prefix + $scope.db_suffix;
                        $scope.installConfig.vm_config.cm.hostname = $scope.installConfig.deployment_prefix + $scope.cm_suffix;
                });
            	}
            };
            
            $scope.changeHostName = function(){
            	$scope.HostNameChanged = true;
            };
            
            $scope.installConfig.app_install_options = {
					BACKUP_SERVER_DISK_SPACE:'2000',
					CALL_TRACE_DISK_SPACE:'1000',
					CODE_SERVER_DISK_SPACE:'2000',
					OMCCN_SUPPORT_WEBSSO_SANE:'false',
					NTP_SERVER:'COM_LOCAL_CLOCK',
					SEC_UNIX_ENABLE:'YES',
					OMCCN_SUPPORT_COM_GR:'true',
					OMCCN_SUPPORT_SP_FM:'YES',
					OMCCN_SUPPORT_SP_PM:'YES',
					OMCCN_SUPPORT_SP_HVP:'NO',
					OMCCN_SUPPORT_SP_LVP:'NO',
					BACKUP_SERVER_IS_LOCAL:'YES',
					SOFTWARE_SERVER_IS_LOCAL:'YES',
					OMCCN_SUPPORT_3GPP:'true',
					OMCCN_SUPPORT_SNMP_N_ITF:'true',
					OMCCN_SUPPORT_NE_TYPES:'all',
					INSTALL_ETHEREAL:'YES'
			};

            $scope.Backup_Server_Addr = function(){
            	$scope.installConfig.app_install_options.SOFTWARE_SERVER_ADDRESS = $scope.installConfig.vm_config.oam.provider_ip_address;
                $scope.installConfig.app_install_options.BACKUP_SERVER_ADDRESS = $scope.installConfig.vm_config.oam.provider_ip_address;
            }
            $scope.deploy = function (){
            	$scope.clean_dirty();
            	OSService.deploy($scope.installConfig).then( function(){
            		monitorService.monitorOSInstall($scope.installConfig.stack_name);
    				$state.go("dashboard.monitor");
        		});
            };
            $scope.clean_dirty = function(){
				if($scope.installConfig.comType=='OAM'){
            		delete $scope.installConfig.vm_config['cm'];
            		delete $scope.installConfig.vm_config['oam'].hide;
    				delete $scope.installConfig.vm_config['db'].hide;
            	}else{
            		delete $scope.installConfig.vm_config['oam'].hide;
    				delete $scope.installConfig.vm_config['db'].hide;
    				delete $scope.installConfig.vm_config['cm'].hide;
            	}
            };
            $scope.$watch('installConfig.com_provider_network.network',function(){
            	if($scope.installConfig.com_provider_network!=null){
            		$scope.installConfig.com_provider_network.v6_subnet = '';
            		$scope.installConfig.com_provider_network.subnet = '';
            		OSService.getV4Subnets($scope.installConfig.com_provider_network.network)
               	 	.then(function(data){
               	 		$scope.v4subnets = data;
               	 	});
            		OSService.getV6Subnets($scope.installConfig.com_provider_network.network)
               	 	.then(function(data){
               	 		$scope.v6subnets = data;
               	 	});
            	}
            });
            OSService.getFlavorStore().then(function(data) {
            	$scope.flavorStore = data;
			});
            OSService.getComTypeStore().then(function(data) {
            	$scope.comTypeStore = data.COMType;
			 	$scope.installConfig.comType = OSService.VNFType;
			});
            $scope.$watch("installConfig.comType", function(){
    	        if($scope.installConfig.comType == 'CM'){
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'NO';
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'NO';
    	        }else{
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'YES';
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'YES';
    	        }
            });
            timezoneService.timezonelist().then( function(data) {
				$scope.timezoneStore = data;
				return data;
			}).then(function (timezonelist){
		           timezoneService.getHostTZ().then( function(hostTimeZone) {
		        	   for(var i in timezonelist){
		        		   if( timezonelist[i].id == hostTimeZone.id){
		        			   $scope.installConfig.timezone = timezonelist[i].id; 
		        		   }
		        	   }
		          });
			});
            OSService.getNetworkStore().then(function(data) {
            	$scope.networkStore = data;
			});
            OSService.getComputeAvailZoneStore().then(function(data) {
            	$scope.computeAvailZoneStore = data;
			});
            OSService.getcinderzones().then(function(data) {
            	$scope.blockAvailZoneStore = data;
			});

            $scope.ping = function(ip){
            	return validationService.ping(ip);
            };
            $scope.reloadimglist = function(){
            	OSService.getImages().then(function(data){
            		$scope.oam_cm_images = data;
            		$scope.db_images = data;
            	});
            };
            $scope.reloadkplist = function(){
            	OSService.getKeys().then(function(data){
            		$scope.keys= data;
            	});
            };
            $scope.reloadkplist();
            $scope.reloadimglist();
            
            OSService.validateCred().then(function(data) {
            	if(data.succeed == false){
            		var errorMsg = data.message;
            		var modalInstance = $modal.open({
    					animation: true,
    					backdrop:'static',
    					templateUrl: 'views/os/checkCred.html',
    					controller: 'checkCred',
    					resolve: {
    						msg: function() {
               				 return errorMsg;
               			 }
            		    },  
    				});
            	}
			});
            
} ).controller('checkCred', function($scope, $modalInstance,$state,msg){
	$scope.ok = function(){
		$state.go('dashboard.oscredential');
		$modalInstance.close();
	};
	$scope.message = msg;
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
    };
});
