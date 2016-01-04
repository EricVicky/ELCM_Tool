angular.module('kvm').controller('upgradectr', function($scope, $filter,  $log, KVMService
		,  monitorService, DashboardService, $dialogs, $state, validationService, $modal) {
	
	$scope.submitComtype = function(){
		$scope.loadimglist($scope.cl_installConfig.active_host_ip, $scope.cl_installConfig.vm_img_dir);
	};
	
	$scope.loadimglist = function(host, dir){
        	KVMService.imagelist({ "host":host, "dir":dir}).then(
                	function(data) {
                			$scope.imagelist = data;
                	});
    };
    var default_app_install_options = {
			BACKUP_SERVER_DISK_SPACE:'2000',
			CALL_TRACE_DISK_SPACE:'1000',
			CODE_SERVER_DISK_SPACE:'2000',
			OMCCN_SUPPORT_WEBSSO_SANE:'false',
			NTP_SERVER:'COM_LOCAL_CLOCK',
			SEC_UNIX_ENABLE:'YES',
			OMCCN_SUPPORT_COM_GR:'true',
			OMCCN_SUPPORT_SP_FM:'YES',
			OMCCN_SUPPORT_SP_PM:'YES',
			OMCCN_SUPPORT_SP_HVP:'NO',
			OMCCN_SUPPORT_SP_LVP:'NO',
			BACKUP_SERVER_IS_LOCAL:'YES',
			SOFTWARE_SERVER_IS_LOCAL:'YES',
			OMCCN_SUPPORT_3GPP:'true',
			OMCCN_SUPPORT_SNMP_N_ITF:'true',
			OMCCN_SUPPORT_NE_TYPES:'all',
			INSTALL_ETHEREAL:'YES'

	};
    
    $scope.reloadimglist = function(){
    	if($scope.com_instance != null){
    		$scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
    		$scope.loadimglist($scope.installConfig.active_host_ip, $scope.installConfig.vm_img_dir);
    		//clone
    		$scope.clone_installConfig();
            $scope.initistoption();
    	}

    };
    
    $scope.initistoption = function(){
    	//set default value if not set
    	for(var attr in default_app_install_options){
    		if(!$scope.cl_installConfig.app_install_options[attr]){
    			$scope.cl_installConfig.app_install_options[attr] = default_app_install_options[attr];
    			$scope.installConfig.app_install_options[attr] = default_app_install_options[attr];
    		}
    	}
    	if($scope.installConfig.comType == 'CM'){
        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'NO';
        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'NO';
        	$scope.cl_installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'NO';
        	$scope.cl_installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'NO';
        }else{
        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'YES';
        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'YES';
        	$scope.cl_installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'YES';
        	$scope.cl_installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'YES';
        }

    };
    
    $scope.setDefaultInstace = function(){
    	var selectedKVMInstance = DashboardService.getSelectedInstance();
    	if(selectedKVMInstance == null){
    		return;
    	}
        $scope.installConfig = $scope.com_instance;
        for(var inst in $scope.kvmcomInstance){
        		var com_config = JSON3.parse($scope.kvmcomInstance[inst].comConfig);
        		if(angular.equals(com_config,selectedKVMInstance)){
        		   $scope.com_instance = $scope.kvmcomInstance[inst];
        		   $scope.kvmcomInstance = [];
        		   $scope.kvmcomInstance.push($scope.com_instance);	
        		   $scope.installConfig = com_config;
        		   break;
        		}
        }
        $scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
        //clone
        $scope.clone_installConfig();

    	$scope.initistoption();
    };
   
	$scope.doUpgrade = function (){
		KVMService.upgrade($scope.cl_installConfig, $scope.enable_full_backup).then( function(){
            monitorService.monitor("KVM", $scope.enable_full_backup?"UPGRADE_FULLBACKUP":"UPGRADE", $scope.cl_installConfig.comType, $scope.cl_installConfig.deployment_prefix);
     		$state.go("dashboard.monitor");
		});
    };
    
    $scope.clone_installConfig = function(){
    	$scope.cl_installConfig = angular.copy($scope.installConfig);
    	//$scope.cl_installConfig.vm_img_dir = 'var/images';
    	$scope.cl_installConfig.vm_config.oam.imgname = '';
    	$scope.cl_installConfig.vm_config.db.imgname = '';
    };
    
    $scope.clone_installoptions = function(){
    	$scope.cl_installConfig.app_install_options = $scope.installConfig.app_install_options;
    	if($scope.cl_installConfig.comType!='OAM'){
    		$scope.cl_installConfig.vm_config.cm.imgname = $scope.cl_installConfig.vm_config.oam.imgname;	
    	}
    	$scope.cl_installConfig.db_image = $scope.cl_installConfig.vm_config.db.imgname
		$scope.cl_installConfig.oam_cm_image = 	$scope.cl_installConfig.vm_config.oam.imgname
    };

    KVMService.getComInstance().then( function(data) {
		$log.info(data);
		$scope.comInstance = data;
		$scope.kvmcomInstance = [];
		for(var ci=0;ci<$scope.comInstance.length;ci++){
			if(JSON3.parse($scope.comInstance[ci].comConfig).environment ==  'KVM'){
				if(KVMService.VNFType && $scope.comInstance[ci].comType != KVMService.VNFType){
					continue;
				}
				if($scope.comInstance[ci].comType!='ATC'&&$scope.comInstance[ci].comType!='HPSIM'&&$scope.comInstance[ci].comType!='QOSAC'){
					$scope.kvmcomInstance.push($scope.comInstance[ci]);		
				}
			}
		}
		$scope.setDefaultInstace();
		
    });
    
    $scope.upgrade = function(){
            	KVMService.comstackStatus($scope.cl_installConfig.deployment_prefix).then(function(status){
            		var ACTION_IN_PROGRESS = 2;
            		if(status.state == ACTION_IN_PROGRESS){
            			if(window.confirm("some operation  proceed on selected VNF instance, go to monitor?")){
            				monitorService.monitor("KVM", status.lastaction, $scope.cl_installConfig.comType, $scope.cl_installConfig.deployment_prefix);
            				$state.go('dashboard.monitor');
            			}
            		}else{
            			$scope.doUpgrade();
            		}
            	});
    };
    
    $scope.fullBackupPreCheck = function(){
    	if(!$scope.enable_full_backup) return;
        var fullbackupConfig = {                         	    					
				full_backup_dir : $scope.cl_installConfig.vm_img_dir+"/"+ $scope.cl_installConfig.deployment_prefix
		};
	    fullbackupConfig.stackName = $scope.cl_installConfig.deployment_prefix;
	    validationService.fullbackupPreCheck(fullbackupConfig).then(function(rs){
    		if(!rs.succeed){
    			$scope.enable_full_backup = false;
				var modalInstance = $modal.open({
						animation: true,
						backdrop:'static',
						controller: function($scope, $modalInstance, dir){
							$scope.dir = dir;
							$scope.ok = function(){
								$modalInstance.close(true);
							}
						},
						resolve: {
							dir: function() {
								return  $scope.cl_installConfig.vm_img_dir+"/"+ $scope.cl_installConfig.deployment_prefix
							}
						},  
						templateUrl: 'views/kvm/nospaceleft.html',
				});	
    		}
    	});
    };
} );


