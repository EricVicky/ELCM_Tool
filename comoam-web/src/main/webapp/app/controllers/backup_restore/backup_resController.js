angular.module('backup_restore', ['ui.router',
                                  'ui.bootstrap', 
                                  'dialogs',
                                  'rcWizard',
                                  'rcForm', 
                                  'ghiscoding.validation',
                                  'monitor',
                                  'ngResource']).controller('backup_resctr', function($scope,  $log, KVMService
		, Backup_ResService, monitorService,DashboardService, $dialogs, $state,$translate,validationService) {
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
    
    $scope.preNFSCheck = function(){
    	if($scope.remote_server){
    		$scope.valid_nfs = false;
        	$scope.message_nfs = "";
        	nfsip = $scope.backupConfig.backupLocation.remote_server_ip;
        	if($scope.installConfig.environment == 'KVM'){
        		if($scope.installConfig.comType == 'QOSAC'){
        			oamip = $scope.installConfig.vm_config.ovm.ip_address;
        		}else{
        			oamip = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
        		}
        	}else{
        		if($scope.installConfig.comType == 'QOSAC'){
        			oamip = $scope.installConfig.vm_config.ovm.ip_address;
        		}else{
        			oamip = $scope.installConfig.vm_config.oam.provider_ip_address;
        		}
        	}
        	validationService.backupNfsPrecheck($scope.backupConfig.backupLocation.remote_server_dir,nfsip,oamip).then( function(data) {
        		$scope.valid_nfs = data.isValid;
        		if($scope.valid_nfs!=true){
        			//$scope.message_nfs = data.message.split("mount.nfs:")[1].split("\r\n")[0];
        			$scope.message_nfs = data.message.split("\r\n")[3]; 
        		}else{
        			$scope.message_nfs = data.message;
        		}
        	}); 
    	}
    };
    
    $scope.preCheck = function(){
    	if(!$scope.remote_server){
    		$scope.valid = false;
        	$scope.message = "";
        	dbip = null;
        	cmip = null;
        	if($scope.installConfig.environment == 'KVM'){
        		if($scope.installConfig.comType == 'QOSAC'){
        			oamip = $scope.installConfig.vm_config.ovm.ip_address;
        		}else if($scope.installConfig.comType == 'FCAPS'||$scope.installConfig.comType == 'CM'){
        			oamip = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
        			dbip = $scope.installConfig.vm_config.db.nic[0].ip_v4.ipaddress;
        			cmip = $scope.installConfig.vm_config.cm.nic[0].ip_v4.ipaddress;
        		}else if($scope.installConfig.comType == 'OAM'){
        			oamip = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
        			dbip = $scope.installConfig.vm_config.db.nic[0].ip_v4.ipaddress;
        		}
        	}else{
        		if($scope.installConfig.comType == 'QOSAC'){
        			oamip = $scope.installConfig.vm_config.ovm.ip_address;
        		}else if($scope.installConfig.comType == 'FCAPS'||$scope.installConfig.comType == 'CM'){
        			oamip = $scope.installConfig.vm_config.oam.provider_ip_address;
        			dbip = $scope.installConfig.vm_config.db.provider_ip_address;
        			cmip = $scope.installConfig.vm_config.cm.provider_ip_address;
        		}else if($scope.installConfig.comType == 'OAM'){
        			oamip = $scope.installConfig.vm_config.oam.provider_ip_address;
        			dbip = $scope.installConfig.vm_config.db.provider_ip_address;
        		}
        	}
        	
        	validationService.backupPrecheck($scope.backupConfig.backupLocation.local_backup_dir,oamip,dbip,cmip).then( function(data) {
        		$scope.valid = data.isValid;
        		$scope.message = data.message; 
        		$scope.message_oam = $scope.message.split("\r\n")[0];
        		$scope.message_db = $scope.message.split("\r\n")[1];
        		$scope.message_cm = $scope.message.split("\r\n")[2];
        	}); 
    	}else{
    		
    	}
    }
    
    $scope.checkname = function(){
    	var filename = $scope.backupConfig.backupLocation.local_backup_file;
    	if(filename.indexOf(".tgz") < 0){
    		filename = filename + ".tgz";
    	}
    	$scope.backupConfig.backupLocation.local_backup_file = filename;
    }

    $scope.backup = function(){

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
    $scope.restore = function(){
    	
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
    }
} );


