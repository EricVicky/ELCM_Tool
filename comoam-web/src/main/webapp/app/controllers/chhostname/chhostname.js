angular.module('chhostname',[]).controller('chhostnamectr', function($scope,KVMService,OSService,monitorService,DashboardService,$state){
	
	KVMService.getComInstance().then( function(data) {
		$scope.Instance = data;
		$scope.comInstance = [];
		var selectedInstance = DashboardService.getSelectedInstance();
		for(var ci=0;ci<$scope.Instance.length;ci++){
			$scope.Instance[ci].comConfig = JSON3.parse($scope.Instance[ci].comConfig);
			if(selectedInstance.deployment_prefix == $scope.Instance[ci].comConfig.deployment_prefix){
				$scope.comInstance.push($scope.Instance[ci]);
			}
		}
		$scope.com_instance = $scope.comInstance[0];
    });

    $scope.initconfig = function(){
    	$scope.installConfig = $scope.com_instance.comConfig;
    	if($scope.installConfig.comType == 'FCAPS'||$scope.installConfig.comType == 'CM'){
    		$scope.installConfig.old_vm_config = {
    				"oam": { "hostname": $scope.installConfig.vm_config.oam.hostname},
    				"cm" : { "hostname": $scope.installConfig.vm_config.cm.hostname},
    				"db" : { "hostname": $scope.installConfig.vm_config.db.hostname}
    		};
    	}else if($scope.installConfig.comType == 'OAM'){
    		$scope.installConfig.old_vm_config = {
    				"oam": { "hostname": $scope.installConfig.vm_config.oam.hostname},
    				"db" : { "hostname": $scope.installConfig.vm_config.db.hostname}
    		};
    	}else{
    		$scope.installConfig.old_vm_config = {
    				"ovm": { "hostname": $scope.installConfig.vm_config.ovm.hostname}
    		};
    	}
    }; 
    
    
    $scope.changehostname = function(){
    	
    	if($scope.installConfig.environment == 'KVM'){
    		if($scope.installConfig.comType == 'QOSAC'){
    			KVMService.chQosacHostname($scope.installConfig).then( function(){
    				monitorService.monitor("KVM", "CHHOSTNAME", $scope.installConfig.comType, $scope.installConfig.deployment_prefix);
    				$state.go("dashboard.monitor");
    			});
    		}else{
    			KVMService.chHostname($scope.installConfig).then( function(){
    				monitorService.monitor("KVM", "CHHOSTNAME", $scope.installConfig.comType, $scope.installConfig.deployment_prefix);
    				$state.go("dashboard.monitor");
    			});
    		}
    	}
    };
	

}); 