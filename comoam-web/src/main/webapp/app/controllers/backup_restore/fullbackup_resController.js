angular.module('fullbackup_restore', ['ui.router',
                                  'ui.bootstrap', 
                                  'dialogs',
                                  'rcWizard',
                                  'rcForm', 
                                  'ghiscoding.validation',
                                  'monitor',
                                  'ngResource']).controller('fullbackup_resctr', function($scope,  $log, KVMService
		, fullBackup_ResService, monitorService,DashboardService, $dialogs, $modal,$state,$translate,validationService) {
                                	
                                	  $scope.reloadimglist = function(){
                                	    	if($scope.com_instance != null){
                                	        	 $scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
                                	        	 if($scope.installConfig.environment == 'KVM'){
                                	        		 if($scope.installConfig.comType=='FCAPS'||$scope.installConfig.comType=='OAM'||$scope.installConfig.comType=='CM'){
                                	        			 $scope.oamRowspan = $scope.installConfig.vm_config.oam.nic.length * 2 + 2;
                                	        			 $scope.dbRowspan = $scope.installConfig.vm_config.db.nic.length * 2 + 2;
                                	        			 if($scope.installConfig.comType != "OAM"){
                                	        				 $scope.cmRowspan = $scope.installConfig.vm_config.cm.nic.length * 2 + 2;
                                	        			 }
                                	        		 }        		 
                                	        	 }
                                	    	}
                                	    };
                                	    
                                	    $scope.setDefaultInstace = function(){
                                	    	var selectedInstance = DashboardService.getSelectedInstance();
                                	    	if(selectedInstance == null){
                                	    		return;
                                	    	}
                                	        $scope.installConfig = $scope.com_instance;
                                	        for(var inst in $scope.comInstance){
                                	    		var com_config = JSON3.parse($scope.comInstance[inst].comConfig);
                                	    		if(angular.equals(com_config,selectedInstance)){
                                	    		   $scope.com_instance = $scope.comInstance[inst];
                                	    		   $scope.installConfig = com_config;
                                	    		   break;
                                	    		}
                                	        }
                                	    };                                	  
          
                                	    fullBackup_ResService.getComInstance().then( function(data) {
                                	    	var comInstance = [];
                                			for(var index in data){
                                				if(data[index].comType=='FCAPS'||data[index].comType=='OAM'||data[index].comType=='CM'||data[index].comType=='ATC'||data[index].comType=='QOSAC'){
                                					if(JSON3.parse(data[index].comConfig).environment == 'KVM'){
                                						comInstance.push(data[index]);			
                                					}
                                				}
                                			}
                                			$scope.comInstance = comInstance;
                                			$scope.setDefaultInstace();
                                	    });

                                	    $scope.init = function(){
                                	    	$scope.checkmessage = false;
                                	    	$scope.showmessage = false;
                                	    	$scope.valid = true;
                                	    };
                                	    
                                	    $scope.fullbackup_lockcheck = function(){
                                	    	KVMService.comstackStatus($scope.installConfig.deployment_prefix).then(function(status){
                                        		var ACTION_IN_PROGRESS = 2;
                                        		if(status.state == ACTION_IN_PROGRESS){
                                        			window.confirm(status.lastAction.toLowerCase()+" has been proceed on selected VNF instance, please wait!");
                                        		}else{
                                                    $scope.fullbackup();
                                        		}
                                        	});
                                	    };
                                	    
                                	    $scope.test = function(){
                                	    	validationService.test($scope.installConfig.deployment_prefix).then( function(data){
                                	    		$scope.message = data.message;
                                	    	}
                                	    )};
                                	    
                                	    $scope.fullrestore_lockcheck = function(){
                                	    	KVMService.comstackStatus($scope.installConfig.deployment_prefix).then(function(status){
                                        		var ACTION_IN_PROGRESS = 2;
                                        		if(status.state == ACTION_IN_PROGRESS){
                                        			window.confirm(status.lastAction.toLowerCase()+" has been proceed on selected VNF instance, please wait!");
                                        		}else{
                                                    $scope.fullrestore();
                                        		}
                                        	});
                                	    };
                                	    
                                	    $scope.fullbackup = function(){
                                	    	if($scope.installConfig.comType=="QOSAC"||$scope.installConfig.comType=="ATC"){
                                	    		$scope.dofullbackup();  
                                	    	}
                                	    	$scope.showmessage = false;
                                	    	$scope.checkmessage = true;
                                	    	$scope.fullbackupConfig.stackName = $scope.installConfig.deployment_prefix;
                                	    	validationService.fullbackupPreCheck($scope.fullbackupConfig).then( function(data){
                                	                $scope.showmessage = true;
                                	                $scope.checkmessage = false;
                                	    			$scope.valid = data.succeed;
                                	    			$scope.message = data.message;				
                                	    			if($scope.valid == true){
                                	    				if($scope.message.indexOf("Warning") != -1){
                                	    					$scope.showmessage = false;
                                	    					var modalInstance = $modal.open({
                                	    						animation: true,
                                	    						backdrop:'static',
                                	    						templateUrl: 'views/backup_restore/fullbackup_message.html',
                                	    						controller: 'fullmessage_ctrl',
                                	    						resolve: {
                                	    							msg: function() {
       
                                	    									return $scope.message.substring(0,$scope.message.indexOf("Success"));							

                                	    							}
                                	    						},   
                                	    					});	
                                	    					modalInstance.result.then(function (res) {
                                	    					    $scope.result = res;
                                	    					    if($scope.result == true){
                                    	    						$scope.dofullbackup();
                                    	    					}
                                	    					}, function () {
                                	    					});
                                	    				}else{
                                	    					$scope.dofullbackup();    					
                                	    				}
                                        	    	}else{
                                        	    		if($scope.message == ""){
                                        	    			$scope.message = "Time out while mounting server.";
                                        	    		}
                                        	    	}
                                	    	});
                                	    };
                                	    
                                	    $scope.fullrestore = function(){
                                	    	if($scope.installConfig.comType=="QOSAC"||$scope.installConfig.comType=="ATC"){
                                	    		$scope.dofullrestore();  
                                	    	}
                                	    	$scope.showmessage = false;
                                	    	$scope.checkmessage = true;
                                	    	$scope.fullbackupConfig.stackName = $scope.installConfig.deployment_prefix;
                                	    	validationService.fullrestorePreCheck($scope.fullbackupConfig).then( function(data){
   	    			                            	$scope.showmessage = true;
                                	                $scope.checkmessage = false;
                                	    			$scope.valid = data.succeed;
                                	    			$scope.message = data.message;
                                	    			if($scope.valid == true){
                                	    				$scope.dofullrestore(); 
                                	    			}else{
                                	    				if($scope.message == ""){
                                        	    			$scope.message = "Time out while mounting server.";
                                        	    		}
                                	    			} 
   	    			                        });
                                	    };
                                	         	    
                                	    $scope.dofullbackup = function(){
                                	    	var vm_img_dir = $scope.installConfig.vm_img_dir;
                	    					var deployment_prefix = $scope.installConfig.deployment_prefix;
                                	    	if($scope.remote_server == null||$scope.remote_server == false){
                            	    			$scope.fullbackupConfig ={                         	    					
                                    					full_backup_dir : vm_img_dir+"/"+deployment_prefix
                                    			};
                            	    		}
                                	    	$scope.fullbackupConfig.stackName = $scope.installConfig.environment == 'KVM'?$scope.installConfig.deployment_prefix:$scope.installConfig.stackName;
                                	    	if($scope.installConfig.environment=='KVM'){
                                	    		if($scope.installConfig.comType == 'ATC'){
                                	    			fullBackup_ResService.kvmatcfullbackup($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullBackup($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			}); 
                                	    		}else if($scope.installConfig.comType == 'QOSAC'){
                                	    			fullBackup_ResService.kvmqosacfullbackup($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullBackup($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			}); 
                                	    		}else{
                                	    			fullBackup_ResService.kvmfullbackup($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullBackup($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			});  	    			
                                	    		}
                                	    	}//else
                                	    };
                                	                                  	    
                                	    $scope.dofullrestore = function(){
                                	    	var vm_img_dir = $scope.installConfig.vm_img_dir;
                	    					var deployment_prefix = $scope.installConfig.deployment_prefix;
                                	    	if($scope.remote_server == null||$scope.remote_server == false){
                            	    			$scope.fullbackupConfig ={                         	    					
                                    					full_backup_dir : vm_img_dir+"/"+deployment_prefix
                                    			};
                            	    		}
                                	    	$scope.fullbackupConfig.stackName = $scope.installConfig.environment == 'KVM'?$scope.installConfig.deployment_prefix:$scope.installConfig.stackName;
                                	    	if($scope.installConfig.environment=='KVM'){
                                	    		if($scope.installConfig.comType == 'ATC'){
                                	    			fullBackup_ResService.kvmatcfullrestore($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullRestore($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			});  
                                	    		}else if($scope.installConfig.comType == 'QOSAC'){
                                	    			fullBackup_ResService.kvmqosacfullrestore($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullRestore($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			});  
                                	    		}else{
                                	    			fullBackup_ResService.kvmfullrestore($scope.fullbackupConfig).then( function(){
                                	    				monitorService.monitorKVMfullRestore($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
                                	    				$state.go("dashboard.monitor");
                                	    			});   	    			
                                	    		}
                                	    	}//else
                                	    };
                                  		} ).controller('fullmessage_ctrl', function($scope, $modalInstance,$state,msg){
                                  				$scope.ok = function(){
                                  					$modalInstance.close(true);
                                  				};
                                  				$scope.message = msg;
                                  				$scope.cancel = function () {
                                  					$modalInstance.dismiss('cancel');
                                  				};
                                  		});


