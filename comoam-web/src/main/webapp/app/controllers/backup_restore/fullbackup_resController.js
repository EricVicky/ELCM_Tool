angular.module('fullbackup_restore', ['ui.router',
                                  'ui.bootstrap', 
                                  'dialogs',
                                  'rcWizard',
                                  'rcForm', 
                                  'ghiscoding.validation',
                                  'monitor',
                                  'ngResource']).controller('fullbackup_resctr', function($scope,  $log, KVMService
		, fullBackup_ResService, monitorService,DashboardService, $dialogs, $state,$translate,validationService) {
                                	
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
                                	    
                                	    $scope.IPNFSCheck = function(){
                                	    	if($scope.backupConfig.backupLocation.remote_server_dir){
                                	    		$scope.preNFSCheck();
                                	    	}
                                	    };
                                	    
                                	    $scope.preNFSCheck = function(){
                                	    	if($scope.remote_server){
                                	    		$scope.valid_nfs = false;
                                	        	$scope.message_nfs = "";
                                	        	$scope.chemessage_nfs = false;        	
                                	        	validationService.fullbackupNfsPrecheck($scope.installConfig.vm_img_dir,$scope.installConfig.deployment_prefix,
                                	        			                                $scope.installConfig.host.ip_address,$scope.fullbackupConfig.remote_server_ip,
                                	        			                                $scope.fullbackupConfig.remote_server_dir).then( function(data) {
                                            		$scope.valid_nfs = data.isValid;
                                            		$scope.chemessage_nfs = true;
                                            		if($scope.valid_nfs!=true){
                                            			if(data.message.indexOf("mount.nfs:")!=-1){
                                            				$scope.message_nfs = data.message.split("mount.nfs:")[1].split("\r\n")[0];		
                                            			}else{
                                            				$scope.message_nfs = "Time out while mounting server.";
                                            			}
                                            		}else{
                                            			$scope.message_nfs = "Success.";
                                            		}
                                            	}); 
                                	        	
                                	    	}
                                	    };                             	    	    
                                	    
                                	    $scope.preCheck = function(){
                                	    	$scope.valid = false;
                                	    	$scope.chemessage = false;
                                        	//$scope.message = "";
                                	    	if($scope.installConfig.comType=="FCAPS"||$scope.installConfig.comType=="OAM"||$scope.installConfig.comType=="CM"){
                                	    		var hostname = $scope.installConfig.vm_config.oam.hostname; 	    		
                                	    	}else{
                                	    		var hostname = $scope.installConfig.vm_config.ovm.hostname; 
                                	    	}
                                        	validationService.fullbackupDupcheck($scope.installConfig.host.ip_address,$scope.installConfig.deployment_prefix, $scope.installConfig.vm_img_dir,hostname).then( function(data) {
                                        		$scope.valid = data.isValid;
                                        		//$scope.message = data.message;
                                        		$scope.chemessage = true;
                                        	}); 
                                	    };
                                	         	    
                                	    $scope.fullbackup = function(){
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
                                	    
                                	    $scope.fullrestore = function(){
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
  
} );


