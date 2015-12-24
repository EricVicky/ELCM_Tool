angular.module('os').controller('ovmosctr', function($scope, $modal, $log, OSService, monitorService, timezoneService, $state, validationService){
     
	$scope.genExport = function (){
    	$scope.export = !$scope.export;
    };
    
    $scope.heat_version = [ {"name":"juno", "version": "2014-10-16" }];
    $scope.installConfig ={};
    
    $scope.deploy = function (){
    	if($scope.installConfig.comType=='HPSIM'){
    		$scope.installConfig.vm_config.ovm.com_data_vol_size = 0;
    	}
    	OSService.deployOVM($scope.installConfig).then( function(){
    		monitorService.monitor("Openstack", "INSTALL", $scope.installConfig.comType,  $scope.installConfig.stack_name);
			$state.go("dashboard.monitor");
		});
    };
    
    $scope.HostNameChanged = false;
    $scope.initHostName = function(){
   	 if(!$scope.HostNameChanged){
    		OSService.getHostNameStore().then(function(data){
            	$scope.qosac_suffix = data['QOSAC'].ovm;
                $scope.installConfig.vm_config.ovm.hostname = $scope.installConfig.deployment_prefix + $scope.qosac_suffix;
        });
    	}
    };
    
    $scope.changeHostName = function(){
    	$scope.HostNameChanged = true;
    };
    
    OSService.getComTypeStore().then(function(data){
		$scope.temp_comTypeStore = data.OVMType;
		var comTypeStore=[];
		for(var index in $scope.temp_comTypeStore){
			if($scope.temp_comTypeStore[index].Name=='ATC'||$scope.temp_comTypeStore[index].Name=='HPSIM'){
				comTypeStore.push($scope.temp_comTypeStore[index]);
			}
		}
		$scope.comTypeStore = comTypeStore;
	 	$scope.installConfig.comType = OSService.VNFType;
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
    $scope.$watch('installConfig.com_provider_network.network',function(){
    	if($scope.installConfig.com_provider_network){
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
    OSService.getComputeAvailZoneStore().then(function(data) {
    	$scope.computeAvailZoneStore = data;
	});
    OSService.getcinderzones().then(function(data) {
    	$scope.blockAvailZoneStore = data;
	});
    OSService.getKeys().then(function(data){
    	$scope.keys= data;
    });
    $scope.reloadimglist = function(){
    	OSService.getImages().then(function(data){
    		$scope.ovm_images = data;
    	});
    };
    $scope.reloadimglist();
    
    $scope.ping = function(ip){
    	return validationService.ping(ip);
    };
    
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

})
.controller('ovmosqosacctr', function($scope, $modal, $log, OSService, monitorService, timezoneService, $state, validationService){
	
	OSService.getComTypeStore().then(function(data){
		$scope.temp_comTypeStore = data.OVMType;
		var comTypeStore=[];
		for(var index in $scope.temp_comTypeStore){
			if($scope.temp_comTypeStore[index].Name=='QOSAC'){
				comTypeStore.push($scope.temp_comTypeStore[index]);
			}
		}
		$scope.comTypeStore = comTypeStore;
	 	$scope.installConfig.comType = 'QOSAC';
	 	
	});
	
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
            	$scope.qosac_suffix = data['QOSAC'].ovm;
                $scope.installConfig.vm_config.ovm.hostname = $scope.installConfig.deployment_prefix + $scope.qosac_suffix;
        });
    	}
    };
    
    $scope.changeHostName = function(){
    	$scope.HostNameChanged = true;
    };
 
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
    
    OSService.getFlavorStore().then(function(data) {
    	$scope.flavorStore = data;
	});
    
    OSService.getNetworkStore().then(function(data) {
    	$scope.networkStore = data;
	});
    $scope.$watch('installConfig.com_provider_network.network',function(){
    	if($scope.installConfig.com_provider_network){
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
    
    OSService.getComputeAvailZoneStore().then(function(data) {
    	$scope.computeAvailZoneStore = data;
	});
    OSService.getcinderzones().then(function(data) {
    	$scope.blockAvailZoneStore = data;
	});
    

    OSService.getKeys().then(function(data){
    	$scope.keys= data;
    });
    
    $scope.genExport = function (){
    	$scope.export = !$scope.export;
    };
    
    $scope.heat_version = [ {"name":"juno", "version": "2014-10-16" }];
    $scope.installConfig ={};
    
    $scope.installConfig.app_install_options = {
			BACKUP_SERVER_DISK_SPACE:'2000',
			CALL_TRACE_DISK_SPACE:'1000',
			CODE_SERVER_DISK_SPACE:'2000',
			OMCCN_SUPPORT_WEBSSO_SANE:'false',
			NTP_SERVER:'COM_LOCAL_CLOCK',
			SEC_UNIX_ENABLE:'YES',
			OMCCN_SUPPORT_COM_GR:'false',
			OMCCN_SUPPORT_SP_FM:'NO',
			OMCCN_SUPPORT_SP_PM:'NO',
			OMCCN_SUPPORT_SP_HVP:'NO',
			BACKUP_SERVER_IS_LOCAL:'YES',
			SOFTWARE_SERVER_IS_LOCAL:'YES',
			OMCCN_SUPPORT_3GPP:'true',
			OMCCN_SUPPORT_SNMP_N_ITF:'true',
			INSTALL_ETHEREAL:'YES'
	};
    
    $scope.Backup_Server_Addr = function(){
    	$scope.installConfig.app_install_options.SOFTWARE_SERVER_ADDRESS = $scope.installConfig.vm_config.ovm.provider_ip_address;
        $scope.installConfig.app_install_options.BACKUP_SERVER_ADDRESS = $scope.installConfig.vm_config.ovm.provider_ip_address;
    }
    
    $scope.deploy = function (){
    	OSService.deployOVM($scope.installConfig).then( function(){
    		monitorService.monitor("Openstack", "INSTALL", $scope.installConfig.comType,  $scope.installConfig.stack_name);
			$state.go("dashboard.monitor");
		});
    };
    $scope.ping = function(ip){
    	return validationService.ping(ip);
    };
    $scope.reloadimglist = function(){
    	OSService.getImages().then(function(data){
    		$scope.ovm_images = data;
    	});
    };
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

})
.controller('ovmosarsctr', function($scope,  $log, OSService, monitorService, timezoneService, $state, validationService){
	
	$scope.installConfig = {
    		"vm_config":{
    			"db":{
    				"provider_ip_address":""
    			}
    		}
    };    
	
	$scope.reloadinstallconfig = function (){
		if($scope.com_instance != null){
        	$scope.installConfig.vm_config.db.provider_ip_address = $scope.com_instance.vm_config.db.provider_ip_address;
    	}
	};
	
	OSService.getComInstance().then( function(data) {
		$scope.oscomInstance = [];
		for(var ci=0;ci<data.length;ci++){
			if(data[ci].comType == "OAM" ||  data[ci].comType == "CM" || data[ci].comType == "FCAPS"){
			    if(JSON3.parse(data[ci].comConfig).environment ==  "OPENSTACK"){
					$scope.oscomInstance.push(JSON3.parse(data[ci].comConfig));
				}
			}
		}
		
    });
    
    $scope.genExport = function (){
    	$scope.export = !$scope.export;
    };
    
    $scope.deploy = function (){
    	$scope.com_instance.comType = $scope.installConfig.comType;
    	$scope.com_instance.license_key = $scope.installConfig.license_key;
    	OSService.deployOVM($scope.com_instance).then( function(){
            monitorService.monitor("Openstack", "INSTALL", $scope.installConfig.comType, $scope.com_instance.stack_name);
            $state.go("dashboard.monitor");

		});
    };
    $scope.ping = function(ip){
    	return validationService.ping(ip);
    };
});