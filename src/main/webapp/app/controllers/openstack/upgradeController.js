angular.module('os').controller('osupgradectr', function($scope, $filter,  $log 
		,  OSService, monitorService, $dialogs, $state) {
    OSService.getImages().then(function(data){
            	$scope.oam_cm_images = data;
            	$scope.db_images = data;
            });
    OSService.getComInstance().then( function(data) {
		$log.info(data);
		$scope.comInstance = data;
		$scope.oscomInstance = [];
		for(var ci=0;ci<$scope.comInstance.length;ci++){
			if(JSON3.parse($scope.comInstance[ci].comConfig).environment ==  "OPENSTACK"){
				$scope.oscomInstance.push($scope.comInstance[ci]);
			}
		}
    });
    $scope.showInstance = function(){
           $scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
    }
    $scope.doUpgrade = function (){
		 //$scope.installConfig.oam_cm_image = $scope.oam_cm_image;
		 //$scope.installConfig.db_image = $scope.db_image;
		OSService.upgrade(
         		$scope.installConfig,
    			function(data){
            			monitorService.monitorKVMUpgrade($scope.installConfig.stack_name);
                 		$state.go("dashboard.monitor");
    			}, 
    			function(response){
    					$log.info(response);
    			});
    };

    $scope.upgrade = function(){
         $scope.doUpgrade();
    };

} );



