angular.module('fullbackup_restore', ['ui.router',
                                  'ui.bootstrap', 
                                  'dialogs',
                                  'rcWizard',
                                  'rcForm', 
                                  'ghiscoding.validation',
                                  'monitor',
                                  'ngResource']).controller('fullbackup_resctr', function($scope,  $log, KVMService
		, Backup_ResService, monitorService,DashboardService, $dialogs, $state,$translate) {
                                	
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
                                	        if($scope.installConfig.environment == 'KVM'){
                                	        	if($scope.installConfig.comType!='QOSAC'){	
                                	        		$scope.oamRowspan = $scope.installConfig.vm_config.oam.nic.length * 2 + 2;
                                	        		$scope.dbRowspan = $scope.installConfig.vm_config.db.nic.length * 2 + 2;
                                	        		if($scope.installConfig.comType != "OAM"){
                                	        			$scope.cmRowspan = $scope.installConfig.vm_config.cm.nic.length * 2 + 2;
                                	        		}       	
                                	        	}
                                	        }
                                	    };                                	  
          
                                	    Backup_ResService.getComInstance().then( function(data) {
                                	    	var comInstance = new Array();
                                			for(var index in data){
                                				if(data[index].comType=='FCAPS'||data[index].comType=='OAM'||data[index].comType=='CM'||data[index].comType=='QOSAC'){
                                					comInstance.push(data[index]);
                                				}
                                			}
                                			$scope.comInstance = comInstance;
                                			$scope.setDefaultInstace();
                                	    });
                                	    
                                	    $scope.fullbackup = function(){
                                	    	$scope.fullbackupConfig.stackName = $scope.installConfig.environment == 'KVM'?$scope.installConfig.deployment_prefix:$scope.installConfig.stackName;
                    
                                	    };
                                	    
                                	    $scope.fullrestore = function(){
                                	    	
                                	    };
  
} );


