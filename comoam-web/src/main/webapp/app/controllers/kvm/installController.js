angular.module('kvm', [ 'ui.router',
                        'ui.bootstrap', 
                        'rcWizard',
                        'rcForm', 
                        'ghiscoding.validation',
                        'monitor',
                        'dashboard',
                        'validation',
                        'sysconst']).controller('kvmctr', function($scope,  $log, KVMService,
           $state,  $dialogs, monitorService, timezoneService, $modal,validationService) {
                        	
			$scope.submitComtype = function(){
				$scope.reloadimglist();
			};
			$scope.reloadimglist = function(){
				$scope.loadimglist($scope.installConfig.active_host_ip, $scope.installConfig.vm_img_dir);
			};
			$scope.installConfig ={
					vm_img_dir : "/var/images"
					};
            $scope.genExport = function(){
            	$scope.export=!$scope.export;
            	var vms_flavor = $scope.flavorStore[$scope.installConfig.comType];
            	for(var name in vms_flavor){
            		var vm_flavor = vms_flavor[name];
            		for(var index =0; index< vm_flavor.length ; index ++){
            			if(vm_flavor[index].label.indexOf($scope.flavor) != -1){
            				$scope.installConfig.vm_config[name].flavor = vm_flavor[index];
            			}
            		}
            	}
            	$scope.calc_disk();
            	$scope.clean_dirty();
            };
            
            $scope.final_disk = {
            		"oam": { "disk": ""}
            };
            
            $scope.calc_disk = function(){
            		if($scope.installConfig.vm_config.oam.flavor){
            			var temp_disk = $scope.installConfig.vm_config.oam.flavor.disk;
            			if(Number($scope.installConfig.app_install_options.BACKUP_SERVER_DISK_SPACE)
            					+Number($scope.installConfig.app_install_options.CALL_TRACE_DISK_SPACE)
            					+Number($scope.installConfig.app_install_options.CODE_SERVER_DISK_SPACE) > 5000){
            				$scope.final_disk.oam.disk = Math.ceil((Number(temp_disk)*1024
            						+Number($scope.installConfig.app_install_options.BACKUP_SERVER_DISK_SPACE)
            						+Number($scope.installConfig.app_install_options.CALL_TRACE_DISK_SPACE)
            						+Number($scope.installConfig.app_install_options.CODE_SERVER_DISK_SPACE))/1024)-5;
            			}else{
            				$scope.final_disk.oam.disk = Number(temp_disk);
            			}
            		}
            		
            };
  
            $scope.$watchGroup(['installConfig.root_password', 'installConfig.re_root_password','installConfig.axadmin_password','installConfig.re_axadmin_password'], function() {
            	if($scope.installConfig.root_password!=$scope.installConfig.re_root_password||$scope.installConfig.axadmin_password!=$scope.installConfig.re_axadmin_password){
            		$scope.disMatch = true;
            	}else{
            		$scope.disMatch = false;
            	}
            });
            $scope.reset_password = function(){
            	if($scope.disMatch == true){
            		$scope.installConfig.root_password = "";
            		$scope.installConfig.re_root_password = "";
            		$scope.installConfig.axadmin_password = "";
            		$scope.installConfig.re_axadmin_password = "";
            	}
            };
            
            $scope.installConfig.app_install_options = {
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

            $scope.installConfig.vm_config = {
            		"oam": { "nic": [ {"name" : "eth0"} ]},
            		"cm" : { "nic": [ {"name" : "eth0"} ]},
            		"db" : { "nic": [ {"name" : "eth0"} ]}
            };
            $scope.addPort = function(vm){
            	$scope.installConfig.vm_config[vm].nic.push({"name" : "eth".concat($scope.installConfig.vm_config[vm].nic.length)});	
        	};

            $scope.deletePort = function(vm){
            	$scope.installConfig.vm_config[vm].nic.pop($scope.installConfig.vm_config[vm].nic[$scope.installConfig.vm_config[vm].nic.length]);
        	};

            $scope.avaliable_flavors = ["Enterprise", "Low End", "Medium", "High End"];
            $scope.flavor = $scope.avaliable_flavors[2];
            $scope.HostNameChanged = false;
            $scope.initNic = function(){
            	if($scope.installConfig.vm_config['oam'].flavor){
            		for(var index in $scope.avaliable_flavors){
            			if(!$scope.installConfig.vm_config['oam'].flavor.label.indexOf($scope.avaliable_flavors[index])){
                			$scope.flavor = $scope.avaliable_flavors[index];
                		}
            	}
            	}
            	if(!$scope.HostNameChanged){
            		KVMService.getHostNameStore().then(function(data){
                    	$scope.oam_suffix = data[$scope.installConfig.comType].oam;
                        $scope.db_suffix = data[$scope.installConfig.comType].db;
                        $scope.cm_suffix = data[$scope.installConfig.comType].cm;
                        $scope.installConfig.vm_config.oam.hostname = $scope.installConfig.deployment_prefix + $scope.oam_suffix;
                        $scope.installConfig.vm_config.db.hostname = $scope.installConfig.deployment_prefix + $scope.db_suffix;
                        $scope.installConfig.vm_config.cm.hostname = $scope.installConfig.deployment_prefix + $scope.cm_suffix;
                });
            	}
            };
            $scope.changeHostName = function(){
            	$scope.HostNameChanged = true;
            };
                       	
            $scope.Backup_Server_Addr = function(){
            	var vm_config = $scope.installConfig.vm_config;
            	for(var vm in vm_config){
            		if(vm == 'db'){
            			$scope.installConfig.vm_config[vm].imgname = $scope.installConfig.db_image;	
            		}else{
            			$scope.installConfig.vm_config[vm].imgname = $scope.installConfig.oam_cm_image;	
            		}
            	}
            	if($scope.installConfig.vm_config.oam.nic[0]!=null&&$scope.installConfig.vm_config.oam.nic[0].ip_v4!=null){
            		$scope.installConfig.app_install_options.SOFTWARE_SERVER_ADDRESS = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
                    $scope.installConfig.app_install_options.BACKUP_SERVER_ADDRESS = $scope.installConfig.vm_config.oam.nic[0].ip_v4.ipaddress;
            	}
            };
            $scope.nicConfig = function(index, vm){
            	var modalInstance = $modal.open({
            	      animation: true,
            	      backdrop:'static',
            	      templateUrl: 'views/kvm/nicConfig.html',
            	      controller: 'nicctr',
            	      resolve: {
         		         config: function () {
         		        	 if($scope.installConfig.vm_config!=null){
         		        		return angular.copy($scope.installConfig.vm_config[vm].nic[index]);
         		        	 }
         		         },
            			 vm: function() {
            				 return vm;
            			 }
         		      },
            	});
            	modalInstance.result.then(function (item) {
            		$scope.installConfig.vm_config[vm].nic[index] = item;
            	}, function () {
    		    });
            };
			$scope.doDeploy = function (){
				$log.info($scope.installConfig);
				$scope.clean_dirty();
				$scope.installConfig.vm_config.oam.flavor.disk = $scope.final_disk.oam.disk;
            	KVMService.deploy($scope.installConfig).then( function(){
            		monitorService.monitor("KVM", "INSTALL", $scope.installConfig.comType,  $scope.installConfig.deployment_prefix);
         			$state.go("dashboard.monitor");
        		});
            };
            $scope.clean_dirty = function(){
            	var vm_config = $scope.installConfig.vm_config;
				if($scope.installConfig.comType=='OAM'){
            		delete $scope.installConfig.vm_config['cm'];
            	}
            };
            
            $scope.loadimglist = function(host, dir){
            	KVMService.imagelist( { "host":host, "dir":dir}).then(
            			function(data) {
            				$scope.imagelist = data;
            			});   
            };
            $scope.deploy = function(){
            	/*check if it is locked: prevent multiple operation on same comstack
            	 * it may cause log chaos on monitor page
            	 */
            	KVMService.comstackStatus($scope.installConfig.deployment_prefix).then(function(status){
            		var ACTION_IN_PROGRESS = 2;
            		if(status.state == ACTION_IN_PROGRESS){
            			if(window.confirm(status.lastAction.toLowerCase()+" has been proceed on selected VNF instance, go to monitor?")){
            				monitorService.monitor("KVM", "INSTALL", $scope.installConfig.comType,  $scope.installConfig.deployment_prefix);
            				$state.go('dashboard.monitor');
            			}
            		}else{
            			$scope.doDeploy();
            		}
            	});
            };

            KVMService.getFlavorStore().then( function(data) {
            				$scope.flavorStore = data.Flavors;
            			});
            
            KVMService.getComTypeStore().then(function(data){
            				$scope.comTypeStore = data.COMType;
            			 	$scope.installConfig.comType = KVMService.VNFType==''?$scope.comTypeStore[0].Name:KVMService.VNFType;
            			});
            timezoneService.timezonelist().then( function(data) {
            				$scope.timezoneStore = data;
            				return data;
            			}).then(function (timezonelist){
            		           timezoneService.getHostTZ().then( function(hostTimeZone) {
            		        	   for(var i in timezonelist){
            		        		   if( timezonelist[i].id == hostTimeZone.id){
            		        			   $scope.installConfig.timezone = timezonelist[i].id; 
            		        		   }
            		        	   }
            		          });
            			});
            KVMService.hostips().then(function(data) {
            	            for(var i=0;i<data.length;i++){
            	            	data[i].name = data[i].name + "---" +data[i].ip_address;
            	            }
            				$scope.hostIPs = data;
            			});
            
            $scope.check_VT = function(){
            	if($scope.installConfig.active_host_ip){
            		validationService.checkVT($scope.installConfig.active_host_ip).then(function(data){
            			if(data.isValid == false){
            				var modalInstance = $modal.open({
            					animation: true,
            					backdrop:'static',
            					templateUrl: 'views/kvm/VT_message.html',
            					controller: 'VT_ctrl',  
            				});	
            			}
            		});	
            	}
            };

      $scope.$watch("installConfig.comType", function(){
    	        if($scope.installConfig.comType == 'CM'){
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'NO';
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'NO';
    	        }else{
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_FM = 'YES';
    	        	$scope.installConfig.app_install_options.OMCCN_SUPPORT_SP_PM = 'YES';
    	        }
      });
	  $scope.animationsEnabled = true;
	  $scope.NFVTypes = ["FCAPS", "CM", "OAM"];
	  $scope.open = function (size) {

		  var modalInstance = $modal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'views/common/NFVChooseModal.html',
		      controller: 'NFVChooseController',
		      size: size,
		      backdrop: true,
		      resolve: {
		         NFVTypes: function () {
		          return $scope.NFVTypes;
		        }
		      }
		    });
	
		    modalInstance.result.then(function (selectedItem) {
		      $scope.NFV = selectedItem;
		    }, function () {
		      $log.info('Modal dismissed at: ' + new Date());
		    });
	  };
	 //$scope.open('sm');	

})
.controller('NFVChooseController', function($scope, $modalInstance, NFVTypes ){
	$scope.NFVTypes = NFVTypes;
	$scope.NFV = $scope.NFVTypes[0];
	$scope.ok = function(){
		$modalInstance.close($scope.NFV);
	};
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
    };
})
.controller('nicctr', function($scope, $modalInstance,config,vm, validationService){
    $scope.ok = function(){
    	$scope.alert=true;
    	if($scope.nic.ip_v4!=null){
    		if(!$scope.nic.ip_v4.ipaddress||!$scope.nic.ip_v4.prefix||!$scope.nic.bridge){
    			return;
        	}else{
        		$scope.alert=false;
        		$modalInstance.close($scope.nic);
        	}
    	}
	};
	$scope.nic = config;
	$scope.oneAtATime = true;
	$scope.vm = vm;
	if($scope.nic){
		if($scope.nic.ip_v6){
			$scope.open = !status.open;
		}else{
			$scope.open = status.open; 
		}
	}
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
    };
    $scope.ping = function(ip){
    	return validationService.ping(ip);
    };
}).controller('VT_ctrl', function($scope,$state, $modalInstance){
	$scope.ok = function(){
		$state.go('dashboard.kvminstall', {}, {reload: true});
		$modalInstance.dismiss('cancel');
	};
});


