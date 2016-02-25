angular.module('datatable',['ui.grid', 'ui.grid.resizeColumns']).controller('datatablectr', function($scope,KVMService,$modal,DashboardService,monitorService,$modal, $state){
	
	KVMService.getComInstance().then( function(data) {
		$scope.comInstance = data;
		for(var ci=0;ci<$scope.comInstance.length;ci++){
			$scope.comInstance[ci].comConfig = JSON3.parse($scope.comInstance[ci].comConfig);
		}
		
    });
	
	$scope.comGridOptions = {
			data: 'comInstance',
            enableGridMenu: true,
            enableRowSelection: false,
            enableSelectAll: false,
            multiSelect: false,
            enableFiltering: true,
			columnDefs: [
			             {displayName: 'COM Name',  name: 'name',
			            	 enableFiltering: false,
                        	 enableColumnMenu: false,
			            	 width:100
			             },
			             {displayName: 'VNF Type',  name: 'comType',
			            	 enableFiltering: false,
                        	 enableColumnMenu: false,
			            	 width:90
			             },
//                         {displayName: 'Environment',  name: 'Environment',
//			            	 cellTemplate: 'views/dashboard/component/environment.html',
//                        	 enableFiltering: false,
//                        	 enableColumnMenu: false,
//                        	 width:100
//                         },
                         {displayName: 'Host Server',  name: 'comConfig.active_host_ip',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:120
                         },
                         {displayName: 'Timezone',  name: 'comConfig.timezone',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:120
                         },
                         {displayName: 'Last Action',  name: 'Last Action',
                        	 cellTemplate: 'views/dashboard/component/status.html',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:160
                         },
                         {displayName: 'GR Status',  name: 'GR Status',
                        	 cellTemplate: 'views/dashboard/component/grstatus.html',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:110
                         },
                         {displayName: 'GR Role',  name: 'GR Role',
                        	 cellTemplate: 'views/dashboard/component/grrole.html',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:80
                         },
                         {displayName: 'GR Mate',  name: 'GR Mate',
                        	 cellTemplate: 'views/dashboard/component/grmate.html',
                        	 enableFiltering: false,
                        	 enableColumnMenu: false,
                        	 width:80
                         },
                         {displayName: 'Operation', name: 'Operation', 
                        	 //cellTemplate: 'views/dashboard/operatebutton.html',
                        	 cellTemplate: 'views/dashboard/component/groupbar.html',
                        	 enableColumnMenu: false,
                        	 enableFiltering: false,
                        	 enableSorting: false,
                        	 enableColumnResizing: false
                         }
                        ],
            onRegisterApi: function( gridApi ) { 
                $scope.gridApi = gridApi;
            }
	};
	
	$scope.godelete = function(row){
		  $scope.selectedIns = row.entity.comConfig;
		  var selectedInsModal = 'delete' + row.entity.comConfig.environment + 'InsModal';
		  var modalInstance = $modal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'views/common/' + selectedInsModal + '.html',
		      controller: 'deleteController',
		      backdrop: true,
		      resolve: {
		    	  selectedIns: function () {
		    		  return $scope.selectedIns;
		    	  }
		      }
		  });
		  modalInstance.result.then(function (item) {
		      	
      	  });
	};
	
	$scope.addport = function(row){
		DashboardService.setSelectedInstance(row.entity.comConfig);
		$state.go('dashboard.addPort');
	}
	
//	$scope.addip = function(row){
//		DashboardService.setSelectedInstance(row.entity.comConfig);
//		var modalInstance = $modal.open({
//			animation: true,
//			backdrop:'static',
//			templateUrl: 'views/common/addip.html',
//			controller: function($scope, $modalInstance ){
//				    $scope.ipTypes  = [ {"label":"--IPv4--", "mode": 1}, 
//		    	                        {"label":"--IPv6--", "mode": 2 }];
//					$scope.ok = function(){
//						$modalInstance.close($scope.ipType);
//					};
//					$scope.cancel = function () {
//						$modalInstance.dismiss('cancel');
//					};
//			},
//			resolve: {
//				
//			},   
//		});	
//		modalInstance.result.then(function (ipType) {
//			$scope.iplabel = ipType.label;
//			if($scope.iplabel == "--IPv4--"){
//				$state.go('dashboard.addV4Port');
//			}else{
//				$state.go('dashboard.addV6Port');
//			}   
//		}, function () {
//		});	
//		
//	};


	
	$scope.details = function(row){
		  var selectedInsModal = "details" + row.entity.comConfig.environment + 'InsModal';
		  for (var index in $scope.comInstance){
			  if($scope.comInstance[index].name == row.entity.comConfig.stackName){
				  row.entity.comConfig['actionResult'] = $scope.comInstance[index].actionResult;
				  break;
			  }
		  }
		  $scope.selectedIns = row.entity.comConfig;
		  var selectComstack = row.entity;
		  var modalInstance = $modal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'views/common/' + selectedInsModal + '.html',
		      controller: 'detailController',
		      backdrop: true,
		      resolve: {
		    	  selectedIns: function () {
		    		  return $scope.selectedIns;
		    	  },
		    	  selectComstack: function () {
		    		  return selectComstack;
		    	  }
		      }
		  });
		  modalInstance.result.then(function (item) {
      	
      	  });
	};
	
	$scope.operationlog = function(row){
		DashboardService.setSelectedInstance(row.entity.comConfig);
		$state.go("dashboard.operationlog");
	};
	
	$scope.goupgraqde = function(row){
		  DashboardService.setSelectedInstance(row.entity.comConfig);
		  if(row.entity.comConfig.environment == "KVM"){
			  if (row.entity.comConfig.comType == "QOSAC") {
				  $state.go("dashboard.kvmovmupgrade");
			  }else{
				  $state.go("dashboard.kvmupgrade");	
			  }
		  }else{
			  if(row.entity.comConfig.comType == "QOSAC"){
				  $state.go("dashboard.osqosacupgrade");
			  }else{
				  $state.go("dashboard.osupgrade");
			  }
		  }
	  };
	  
	  $scope.gobackup = function(row){
		  DashboardService.setSelectedInstance(row.entity.comConfig);
		  comType = row.entity.comConfig.comType;
          if(comType && (comType == 'QOSAC' ||  comType == 'OAM' || comType =='CM' || comType == 'FCAPS')){
				 $state.go("dashboard.backup");			  
		  } 			  
	  };
	  
	  $scope.gorestore = function(row){
		  DashboardService.setSelectedInstance(row.entity.comConfig);
		  $state.go("dashboard.restore");			  
	  };
	  
	  $scope.fullbackup = function(row){
			  DashboardService.setSelectedInstance(row.entity.comConfig);
			  $state.go("dashboard.fullbackup");			   
	  };
	  
	  $scope.fullrestore = function(row){
			  DashboardService.setSelectedInstance(row.entity.comConfig);
			  $state.go("dashboard.fullrestore");			  	  
	  };
	  
	  $scope.gochhostname = function(row){
		  DashboardService.setSelectedInstance(row.entity.comConfig);
		  $state.go("dashboard.chhostname");			  
	  };
	  
	  $scope.healing = function(row){
		  var modalInstance = $modal.open({
				animation: true,
				backdrop:'static',
				templateUrl: 'views/common/handlerErrorConfirm.html',
				controller: function($scope, $modalInstance, message ){
						$scope.message = "Start Healing " + message + "?";;
						$scope.ok = function(){
								$modalInstance.close(true);
						};
						$scope.cancel = function () {
							$modalInstance.dismiss('cancel');
						};
				},
				resolve: {
					message: function() {
						return row.entity.comConfig.deployment_prefix;
					}
				},   
			});	
			modalInstance.result.then(function (res) {
			    $scope.result = res;
			    if($scope.result == true){
			    	KVMService.healing(row.entity.comConfig).then( function(){
			      	      monitorService.monitor("KVM", "HEALING", row.entity.comConfig.comType,  row.entity.comConfig.deployment_prefix);
			   			  $state.go("dashboard.monitor");
			  		  });
				}
			}, function () {
			});		  
	  };
	
	
}).controller('detailController', function($scope, $modalInstance, selectedIns, selectComstack, KVMService, OSService, monitorService, $state){
	 $scope.selectedIns = selectedIns;
     $scope.selectComstack = selectComstack;
	 if($scope.selectedIns.environment == "KVM"){
		 if($scope.selectedIns.comType=='FCAPS'||$scope.selectedIns.comType=='OAM'||$scope.selectedIns.comType=='CM'){
			 $scope.oamRowspan = $scope.selectedIns.vm_config.oam.nic.length * 2 + 2;
		 	 $scope.dbRowspan = $scope.selectedIns.vm_config.db.nic.length * 2 + 2;
		 	 if($scope.selectedIns.comType != "OAM"){
		 		 $scope.cmRowspan = $scope.selectedIns.vm_config.cm.nic.length * 2 + 2;                		
		 	 }
		 }
	 }
	 $scope.ok = function(){
		 $modalInstance.close($scope.selectedIns);
	 };
	 $scope.cancel = function () {
		 $modalInstance.dismiss('cancel');
     };
}).controller('deleteController', function($scope, $modalInstance, selectedIns, KVMService, OSService, monitorService, $state){
	  $scope.deletecom = function(){
		  if($scope.selectedIns.environment == "KVM"){
			  KVMService.deletecom($scope.selectedIns).then( function(){
				  monitorService.monitorKVMDelete($scope.selectedIns.deployment_prefix, $scope.selectedIns.comType);
	       		  $state.go("dashboard.monitor");
			  });
		  }else{
			  if($scope.selectedIns.comType=='QOSAC' || $scope.selectedIns.comType =='HPSIM' || $scope.selectedIns.comType=='ATC'){
				  OSService.deleteovm($scope.selectedIns).then( function(){
					  monitorService.monitor("Openstack","DELETE",$scope.selectedIns.comType,$scope.selectedIns.stack_name);
					  $state.go("dashboard.monitor");
				  });
			  }else{
				  OSService.deletecom($scope.selectedIns).then( function(){
					  monitorService.monitor("Openstack","DELETE",$scope.selectedIns.comType,$scope.selectedIns.stack_name);
					  $state.go("dashboard.monitor");
				  });
			  }
		  }  
	  };
	 $scope.selectedIns = selectedIns;
	 if($scope.selectedIns.environment == "KVM"){
		 if($scope.selectedIns.comType=='FCAPS'||$scope.selectedIns.comType=='OAM'||$scope.selectedIns.comType=='CM'){
			 $scope.oamRowspan = $scope.selectedIns.vm_config.oam.nic.length * 2 + 2;
		 	 $scope.dbRowspan = $scope.selectedIns.vm_config.db.nic.length * 2 + 2;
		 	 if($scope.selectedIns.comType != "OAM"){
		 		 $scope.cmRowspan = $scope.selectedIns.vm_config.cm.nic.length * 2 + 2;                		
		 	 }
		 }
	 }
	 
	 $scope.ok = function(){
		 $scope.deletecom();
		 $modalInstance.close($scope.selectedIns);
	 };
	 $scope.cancel = function () {
		 $modalInstance.dismiss('cancel');
     };
});