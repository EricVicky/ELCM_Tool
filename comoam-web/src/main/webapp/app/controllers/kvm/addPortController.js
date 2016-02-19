angular.module('addport',[]).controller('addPortctr', function($scope,KVMService,OSService,monitorService,DashboardService,validationService,$state){
	
	$scope.Config={};
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
		$scope.SelectedCOM = $scope.com_instance.comConfig;
		$scope.Config.KVMCOMConfig = $scope.SelectedCOM;
		$scope.Config.vm_config = {
	    		"oam": { "nic": []},
	    		"cm" : { "nic": []},
	    		"db" : { "nic": []}
	    };
    });
	
	$scope.initVmInstance = function(){
		$scope.VMInstance = [];
		for (var vm in $scope.Config.KVMCOMConfig.vm_config){
			$scope.VMInstance.push(vm.toUpperCase());
		}	
	};
	
	$scope.nics=[];
	
	$scope.changeVMInstance = function(){
		for (var vm in $scope.Config.KVMCOMConfig.vm_config){
			if($scope.vm_instance == vm.toUpperCase()){
				$scope.VM = $scope.Config.KVMCOMConfig.vm_config[vm];
				break;
			}
		}
	};
	
	$scope.addPort = function(){
		$scope.nics.push("eth".concat($scope.nics.length+$scope.VM.nic.length));
		var vm = $scope.vm_instance.toLowerCase();
		$scope.ethname = {
				"name":"eth".concat($scope.nics.length+$scope.VM.nic.length-1)
		};
		$scope.Config.vm_config[vm].nic.push($scope.ethname);
	};

    $scope.deletePort = function(){
    	$scope.nics.pop("eth".concat($scope.nics.length+$scope.VM.nic.length));
	};
	
	$scope.deploy = function(){
		$scope.cleanDirty();
		var aa = $scope.Config;
		var ff =1;
	};
	
	$scope.cleanDirty = function(){
		for(var vm in $scope.Config.vm_config){
			if($scope.vm_instance.toLowerCase() == vm){
				continue;
			}
			delete $scope.Config.vm_config[vm];
		}
	};
	
	$scope.ping = function(ip){
    	return validationService.ping(ip);
    };


}); 