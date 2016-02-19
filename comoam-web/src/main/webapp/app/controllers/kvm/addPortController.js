angular.module('addport',[]).controller('addPortctr', function($scope,KVMService,OSService,monitorService,DashboardService,$state){
	
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
    });
	
	$scope.initVmInstance = function(){
		$scope.VMInstance = [];
		for (var vm in $scope.SelectedCOM.vm_config){
			$scope.VMInstance.push(vm.toUpperCase());
		}	
	};
	
	$scope.changeVMInstance = function(){
		for (var vm in $scope.SelectedCOM.vm_config){
			if($scope.vm_instance == vm.toUpperCase()){
				$scope.VM = $scope.SelectedCOM.vm_config[vm];
			}
		}
	};




}); 