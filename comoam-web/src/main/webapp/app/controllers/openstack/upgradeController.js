angular.module('os').controller('osupgradectr', function($scope, $filter,  $log 
		,  OSService, monitorService, $dialogs, $state , $modal,DashboardService) {
   $scope.reloadimglist = function(){
          OSService.getImages().then(function(data){
            		$scope.oam_cm_images = data;
            		$scope.db_images = data;
            	});
          if($scope.com_instance != null){
        	  $scope.initistoption();	  
          }
   };
   $scope.reloadimglist();
    var default_app_install_options = {
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
    
    $scope.initistoption = function(){
    	//set default value if not set
    	for(var attr in default_app_install_options){
    		if(!$scope.installConfig.app_install_options[attr]){
    			$scope.installConfig.app_install_options[attr] = default_app_install_options[attr];
    		}
    	}
    };
    
    $scope.setDefaultInstace = function(){
    	var selectedOSInstance = DashboardService.getSelectedInstance();
    	if(selectedOSInstance == null){
    		return;
    	}
        $scope.installConfig = $scope.com_instance;
        for(var inst in $scope.oscomInstance){
    		var com_config = JSON3.parse($scope.oscomInstance[inst].comConfig);
    		if(angular.equals(com_config,selectedOSInstance)){
    		   $scope.com_instance = $scope.oscomInstance[inst];
    		   $scope.oscomInstance = [];
    		   $scope.oscomInstance.push($scope.com_instance);	
    		   $scope.installConfig = com_config;
    		   break;
    		}
        }
        $scope.initistoption();
    };
    
    OSService.getComInstance().then( function(data) {
		$log.info(data);
		$scope.comInstance = data;
		$scope.oscomInstance = [];
		for(var ci=0;ci<$scope.comInstance.length;ci++){
			if(JSON3.parse($scope.comInstance[ci].comConfig).environment ==  "OPENSTACK"){
				if(OSService.VNFType && $scope.comInstance[ci].comType != OSService.VNFType){
					continue;
				}
				$scope.oscomInstance.push($scope.comInstance[ci]);
			}
		}
		$scope.setDefaultInstace();
    });
    $scope.showInstance = function(){
           $scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
		   for(var attr in default_app_install_options){
          		if(!$scope.installConfig.app_install_options[attr]){
          			$scope.installConfig.app_install_options[attr] = default_app_install_options[attr];
          		}
          	}
    }
    $scope.doUpgrade = function (){
    	OSService.upgrade($scope.installConfig).then( function(){
    		monitorService.monitorKVMUpgrade($scope.installConfig.stack_name);
     		$state.go("dashboard.monitor");
		});
    };

    $scope.upgrade = function(){
         $scope.doUpgrade();
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



