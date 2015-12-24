angular.module('os').controller('ovmqosacupgradectr', function($scope, $filter,  $log, OSService,  
		monitorService, DashboardService,$modal, $dialogs, $state) {

	
	$scope.setDefaultInstace = function(){
    	var selectedOSInstance = DashboardService.getSelectedInstance();
    	if(selectedOSInstance == null){
    		return;
    	}
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
    	 $scope.showInstance();
    };
	
    OSService.getComInstance().then( function(data) {
		$log.info(data);
		$scope.comInstance = data;
		$scope.oscomInstance = [];
		for(var ci=0;ci<$scope.comInstance.length;ci++){
			if(JSON3.parse($scope.comInstance[ci].comConfig).environment ==  "OPENSTACK"){
				if(JSON3.parse($scope.comInstance[ci].comConfig).comType == "QOSAC"){
					$scope.oscomInstance.push($scope.comInstance[ci]);
				}
			}
		}
		$scope.setDefaultInstace();
    });
    
    var default_app_install_options = {
			BACKUP_SERVER_DISK_SPACE:'2000',
			CALL_TRACE_DISK_SPACE:'1000',
			CODE_SERVER_DISK_SPACE:'2000',
			OMCCN_SUPPORT_WEBSSO_SANE:'false',
			NTP_SERVER:'COM_LOCAL_CLOCK',
			SEC_UNIX_ENABLE:'YES',
			OMCCN_SUPPORT_COM_GR:'false',
			OMCCN_SUPPORT_SP_FM:'YES',
			OMCCN_SUPPORT_SP_PM:'YES',
			OMCCN_SUPPORT_SP_HVP:'NO',
			BACKUP_SERVER_IS_LOCAL:'YES',
			SOFTWARE_SERVER_IS_LOCAL:'YES',
			OMCCN_SUPPORT_3GPP:'true',
			OMCCN_SUPPORT_SNMP_N_ITF:'true',
			INSTALL_ETHEREAL:'YES'

	};
	
    $scope.showInstance = function(){
    	$scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
    	for(var attr in default_app_install_options){
      		if(!$scope.installConfig.app_install_options[attr]){
      			$scope.installConfig.app_install_options[attr] = default_app_install_options[attr];
      		}
      	}
    };
    
    $scope.upgrade = function (){
    	OSService.upgradeOVM($scope.installConfig).then( function(){
    		monitorService.monitor("Openstack", "UPGRADE", $scope.installConfig.comType,  $scope.installConfig.stack_name);
     		$state.go("dashboard.monitor");
		});
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
    
}).controller('checkCred', function($scope, $modalInstance,$state,msg){
	$scope.ok = function(){
		$state.go('dashboard.oscredential');
		$modalInstance.close();
	};
	$scope.message = msg;
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
    };
});