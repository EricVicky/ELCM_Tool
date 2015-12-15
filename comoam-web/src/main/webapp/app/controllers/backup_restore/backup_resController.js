angular.module('backup_restore', ['ui.router',
                                  'ui.bootstrap', 
                                  'dialogs',
                                  'rcWizard',
                                  'rcForm', 
                                  'ghiscoding.validation',
                                  'monitor',
                                  'ngResource']).controller('backup_resctr', function($scope,  $log, KVMService
		, Backup_ResService, monitorService,DashboardService, $dialogs,$modal, $state,$translate,validationService) {
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
    }
    
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
    }
    
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

    $scope.checkname = function(){
    	var filename = $scope.backupConfig.backupLocation.local_backup_file;
    	if(filename.indexOf(".tgz") < 0){
    		filename = filename + ".tgz";
    	}
    	$scope.backupConfig.backupLocation.local_backup_file = filename;
    };
    
    $scope.init = function(){
    	$scope.checkmessage = false;
    	$scope.showmessage = false;
    	$scope.valid = true;
    };
    
    $scope.backup = function(){
    	$scope.showmessage = false;
    	$scope.checkmessage = true;
    	if($scope.remote_server==true){
    		var remoteip = $scope.backupConfig.backupLocation.remote_server_ip + ":";
    		var remotedir = $scope.backupConfig.backupLocation.remote_server_dir;
    	}else{
    		var remoteip = "";
    	    var remotedir = "";
    	}
    	if($scope.installConfig.environment=='KVM'){
    		oamip = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
    		dbip = $scope.installConfig.vm_config.db.nic[0].ip_v4.ipaddress;
    		if($scope.installConfig.comType == "OAM"){
        		cmip = "";
        	}else{
        		cmip = $scope.installConfig.vm_config.cm.nic[0].ip_v4.ipaddress;
        	}
    	}else{
    		oamip = $scope.installConfig.vm_config.oam.provider_ip_address;
    		dbip = $scope.installConfig.vm_config.db.provider_ip_address;
    		if($scope.installConfig.comType == "OAM"){
        		cmip = "";
        	}else{
        		cmip = $scope.installConfig.vm_config.cm.provider_ip_address;
        	}
    	}
    	validationService.databackupPreCheck(oamip,dbip,cmip,$scope.backupConfig.backupLocation.local_backup_dir,
    			             $scope.backupConfig.backupLocation.local_backup_file,remoteip,remotedir).then( function(data){
            $scope.showmessage = true;
            $scope.checkmessage = false;
            $scope.valid = data.isValid;
            $scope.message = data.message;
            if($scope.valid == true){
            	if($scope.message[0].indexOf("Warning") != -1){
					$scope.showmessage = false;
					var modalInstance = $modal.open({
						animation: true,
						backdrop:'static',
						templateUrl: 'views/backup_restore/databackup_message.html',
						controller: 'datamessage_ctrl',
						resolve: {
							msg: function() {
								return $scope.message;
							},
							config: function() {
								return $scope.installConfig;
							}
						},   
					});	
					modalInstance.result.then(function (res) {
					    $scope.result = res;
					    if($scope.result == true){
    						$scope.doBackup();
    					}
					}, function () {
					});
				}else{
					$scope.doBackup();    					
				}
            }else{
            	var VMmessage = new Array();
            	for(var index in $scope.message){
            		if($scope.message[index].indexOf("Warning")!=-1){
            			continue;
            		}
            		VMmessage.push($scope.message[index]);
            	}
            	if($scope.installConfig.comType != "QOSAC"){
            		$scope.message_oam = VMmessage[0] == null?"Timeout when mounting server.":VMmessage[0];
            		$scope.message_db = VMmessage[1] == null?"Timeout when mounting server.":VMmessage[1];
            		if($scope.installConfig.comType != "OAM"){
            			$scope.message_cm = VMmessage[2] == null?"Timeout when mounting server.":VMmessage[2];			
            		}
            	}else{
            		$scope.message_ovm = VMmessage[0] == null?"Timeout when mounting server.":VMmessage[0];
            	}
            }      	
        });
    };
    
    $scope.restore = function(){
    	$scope.showmessage = false;
    	$scope.checkmessage = true;
    	if($scope.remote_server==true){
    		var remoteip = $scope.backupConfig.backupLocation.remote_server_ip + ":";
    		var remotedir = $scope.backupConfig.backupLocation.remote_server_dir;
    	}else{
    		var remoteip = "";
    	    var remotedir = "";
    	}
    	if($scope.installConfig.environment=='KVM'){
    		oamip = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
    		dbip = $scope.installConfig.vm_config.db.nic[0].ip_v4.ipaddress;
    		if($scope.installConfig.comType == "OAM"){
        		cmip = "";
        	}else{
        		cmip = $scope.installConfig.vm_config.cm.nic[0].ip_v4.ipaddress;
        	}
    	}else{
    		oamip = $scope.installConfig.vm_config.oam.provider_ip_address;
    		dbip = $scope.installConfig.vm_config.db.provider_ip_address;
    		if($scope.installConfig.comType == "OAM"){
        		cmip = "";
        	}else{
        		cmip = $scope.installConfig.vm_config.cm.provider_ip_address;
        	}
    	}
    	validationService.datarestorePreCheck(oamip,dbip,cmip,$scope.backupConfig.backupLocation.local_backup_dir,
    			                             $scope.backupConfig.backupLocation.local_backup_file, remoteip,remotedir).then( function(data){
               $scope.showmessage = true;
               $scope.checkmessage = false;
               $scope.valid = data.isValid;
               $scope.message = data.message;
               if($scope.valid == true){
            	   $scope.doRestore();
               }else{
            	   var VMmessage = new Array();
               	   for(var index in $scope.message){
               	       if($scope.message[index].indexOf("Warning")!=-1){
            			  continue;
            		  }
            		  VMmessage.push($scope.message[index]);
               	   }
               	   if($scope.installConfig.comType != "QOSAC"){
               		   $scope.message_oam = VMmessage[0] == null?"Timeout when mounting server.":VMmessage[0];
               		   $scope.message_db = VMmessage[1] == null?"Timeout when mounting server.":VMmessage[1];
               		   if($scope.installConfig.comType != "OAM"){
               			   $scope.message_cm = VMmessage[2] == null?"Timeout when mounting server.":VMmessage[2];			
               		   }
               	   }else{
               		   $scope.message_ovm = VMmessage[0] == null?"Timeout when mounting server.":VMmessage[0];
               	   }
               }                                   	 
        });
    };

    $scope.doBackup = function(){

    	$scope.backupConfig.config = $scope.installConfig;
    	if($scope.backupConfig.config.environment=='KVM'){
    		Backup_ResService.kvmbackup($scope.backupConfig).then( function(){
    			monitorService.monitorKVMBackup($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
             	$state.go("dashboard.monitor");
    		});
    	}else{
    		Backup_ResService.osbackup($scope.backupConfig).then( function(){
    			monitorService.monitorOSBackup($scope.installConfig.stack_name);
     			$state.go("dashboard.monitor");
    		});
    	}
    }
    $scope.doRestore = function(){
    	
    	$scope.backupConfig.config = $scope.installConfig;
    	if($scope.backupConfig.config.environment=='KVM'){
    		Backup_ResService.kvmrestore($scope.backupConfig).then( function(){
    		monitorService.monitorKVMRestore($scope.installConfig.deployment_prefix, $scope.installConfig.comType);
            $state.go("dashboard.monitor");
    		});
    	}else{
    		Backup_ResService.osrestore($scope.backupConfig).then( function(){
    			monitorService.monitorOSRestore($scope.installConfig.stack_name);
     			$state.go("dashboard.monitor");
    		});
    	}
    };
} ).controller('datamessage_ctrl', function($scope, $modalInstance,$state,msg,config){
		$scope.ok = function(){
				$modalInstance.close(true);
		};
		$scope.message = msg;
		$scope.installConfig = config;
		var VMmessage = new Array();
    	for(var index in $scope.message){
    		if($scope.message[index].indexOf("Warning")!=-1){
    			VMmessage.push($scope.message[index]);
    		}
    	}
    	if($scope.installConfig.comType != "QOSAC"){
    		$scope.message_oam = VMmessage[0]== null?"Timeout when mounting server.":VMmessage[0];
    		$scope.message_db = VMmessage[1]== null?"Timeout when mounting server.":VMmessage[1];
    		if($scope.installConfig.comType != "OAM"){
    			$scope.message_cm = VMmessage[2]== null?"Timeout when mounting server.":VMmessage[2];			
    		}
    	}else{
    		$scope.message_ovm = VMmessage[0]== null?"Timeout when mounting server.":VMmessage[0];
    	}	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	});


