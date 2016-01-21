angular.module('gr', [ 'ui.router',
                        'ui.bootstrap', 
                        'dialogs',
                        'rcWizard',
                        'rcForm', 
                        'ghiscoding.validation',
                        'ngResource'])
     .controller('grInstallController', function($scope, $q, $timeout, $log, KVMService,
            $state, GRService,monitorService,$modal) {
    	 $scope.ip_types = ["ipv4", "ipv6"];
    	 GRService.getComInstance().then(function( comstacks ){
    		 if(comstacks.length > 0){
    		    var instances = new Array();
    		    for(var i=0; i < comstacks.length; i++){
    		    	if(comstacks[i].comType == 'OAM'||comstacks[i].comType == 'FCAPS'||comstacks[i].comType == 'CM'){
    		    		var temp_instance = JSON3.parse(comstacks[i].comConfig);
    		    		if(temp_instance.stackName){
    		    			instances.push(temp_instance);
    		    		}else{
    		    			temp_instance.stackName =temp_instance.deployment_prefix;
    		    			instances.push(temp_instance);
    		    		}
    		    	}
    		    }
    		    $scope.COMStack = comstacks;
    		    $scope.instances = instances;
    		 }
    	 }); 
    	 $scope.invalideIPv6 = false;
    	 $scope.all_gr_traffic_types = [ {"label":"Simple", "value": "SIMPLE", "index": 0}, 
    	                                 {"label":"Traffic Separation", "value":"SEPARATION", "index" :1},
    	                                 { "label":"Traffic Redundency", "value": "REDUDENCY", "index" :2}];
    	 $scope.grTrafficTypeMap = {
    			 "SIMPLE":"1",
    			 "SEPARATION":"2",
    			 "REDUDENCY":"3"
    	 };
    	 $scope.getIPNum = function(gr_traffic){
    		 for(var indexofTypes=0;indexofTypes<$scope.all_gr_traffic_types.length;indexofTypes++){
    			 if($scope.all_gr_traffic_types[indexofTypes].value == gr_traffic){
    				 return $scope.all_gr_traffic_types[indexofTypes].index;
    			 }
    		 }
    	 };
    	 $scope.primarycfg = function(){
    		 GRService.checkInstalled({"name":$scope.gr_config.pri.stackName}).then(function(data){
    			 $scope.installedPriGR = (data.succeed == true?"Installed":"Not Installed");
        	 });
    		 if($scope.gr_config.pri.environment == "KVM"){
    			 $scope.priOAMRowspan = $scope.gr_config.pri.vm_config.oam.nic.length * 2+2;
    			 $scope.priDBRowspan = $scope.gr_config.pri.vm_config.db.nic.length * 2+2;
    			 if($scope.gr_config.pri.comType != "OAM"){
    				 $scope.priCMRowspan = $scope.gr_config.pri.vm_config.cm.nic.length * 2+2; 	    		 
    			 }
    		 }
    	 };
    	 $scope.secondarycfg = function(){
    		 GRService.checkInstalled({"name":$scope.gr_config.sec.stackName}).then(function(data){
    			 $scope.installedSecGR = (data.succeed == true?"Installed":"Not Installed");
        	 });
    		 if($scope.gr_config.sec.environment == "KVM"){
    			 $scope.secOAMRowspan = $scope.gr_config.sec.vm_config.oam.nic.length * 2+2;
    			 $scope.secDBRowspan = $scope.gr_config.sec.vm_config.db.nic.length * 2+2;
    			 if($scope.gr_config.sec.comType != "OAM"){
    				 $scope.secCMRowspan = $scope.gr_config.sec.vm_config.cm.nic.length * 2+2; 	    		 
    			 }
    		 }
    	 };
    	 
    	 $scope.gr_IP = {"pri":{}, "sec": {}};
    	 $scope.gr_ip_setup = function(role){
    		 var vm_config = $scope.gr_config[role].vm_config;
    		 var IPNum =  $scope.getIPNum($scope.gr_config.gr_traffic);
    		 for(var vm in vm_config){
    			 //single vm comfig
    			 var cfg = vm_config[vm];
    			 if($scope.gr_config[role].environment == "KVM"){
    				 if(vm_config[vm].nic.length-1 < IPNum){
        				 var IPNum = vm_config[vm].nic.length-1;
        			 }
    				 for(var i =0; i <= IPNum; i++){
    					 if(i==0){
    						 $scope.gr_IP[role][vm] = [];
    					 }
    					 if($scope.gr_config.gr_ip_type == 'ipv4'){
    						 $scope.gr_IP[role][vm].push(cfg.nic[i].ip_v4.ipaddress);
    					 }else{
    						 if(cfg.nic[i].ip_v6){
    							 $scope.gr_IP[role][vm].push(cfg.nic[i].ip_v6.ipaddress);
    						 }
    					 }
    				 }
    			 }else{
    				 $scope.gr_IP[role][vm] = [];
    				 $scope.gr_IP[role][vm].push(cfg.provider_ip_address);
    			 }
    		 }
    		 
    	 };
    	 $scope.gr_ip_changed = function(){
    		 if($scope.gr_config.pri.environment == "KVM" && $scope.gr_config.sec.environment == "KVM"){
    			 if($scope.gr_config.gr_traffic && $scope.gr_config.gr_ip_type){
    				 $scope.gr_ip_setup('pri');
    				 $scope.gr_ip_setup('sec');
    			 }
    			 $log.info($scope.gr_IP);
    			 
    			 if($scope.gr_config.gr_ip_type == 'ipv6'){
    				 for(var role in $scope.gr_IP){
    					 for(var vm in $scope.gr_IP[role]){
    						 if($scope.gr_IP[role][vm].length != $scope.grTrafficTypeMap[$scope.gr_config.gr_traffic]){
    							 $scope.invalideIP = true;
    							 return;
    						 }
    					 }
    				 }
    			 }else{
    				 $scope.invalideIP = false;
    			 }
    		 }else{
    			 $scope.gr_ip_setup('pri');
				 $scope.gr_ip_setup('sec');
    		 }
    	 };
    	 $scope.getMinTraffic = function(){
    		 var vm_config = $scope.gr_config.pri.vm_config;
    		 for(var vm in vm_config){
    			 if(!angular.equals($scope.gr_config.pri.comType,$scope.gr_config.sec.comType)){
    				 var Msg = "'VNF Type' must match!!!";
    				 var modalInstance = $modal.open({
   	      	         	animation: true,
   	      	            backdrop:'static',
   	      	         	templateUrl: 'views/gr/grcheck.html',
   	      	         	controller: 'grcheck',
   	      	            resolve: {
   	      	            	msg: function() {
   	      	            		return Msg;
          			        },
          			        e_cancel: function() {
          				 	return false;
          			 	},
   	      	            },
   				     });
    				 break;
    			 }else if(!angular.equals($scope.gr_config.pri.oam_cm_image,$scope.gr_config.sec.oam_cm_image)||
        			      !angular.equals($scope.gr_config.pri.db_image,$scope.gr_config.sec.db_image)||
        			      !angular.equals($scope.gr_config.pri.vm_config[vm].flavor,$scope.gr_config.sec.vm_config[vm].flavor)){
    				 var Msg = "'flavor' and 'version' not match!Reconfigure?";
    				 var modalInstance = $modal.open({
    	      	         	animation: true,
    	      	            backdrop:'static',
    	      	         	templateUrl: 'views/gr/grcheck.html',
    	      	         	controller: 'grcheck',
    	      	         	resolve: {
        						msg: function() {
                   				 	return Msg;
                   			 	},
                   			 	e_cancel: function() {
                				 	return true;
                			 	},
                		    },
    				     });
    				 break;
    			 }
    		 }
    		 if($scope.gr_config.pri.environment == "KVM" && $scope.gr_config.sec.environment == "KVM"){
    			 var priNic = $scope.gr_config.pri.vm_config.oam.nic.length;
    			 var secNic = $scope.gr_config.sec.vm_config.oam.nic.length;
    			 var minNic = priNic<secNic?priNic:secNic;
    			 $scope.gr_traffic_types = [minNic];
    			 for(var i=0;i<minNic;i++){
    				 $scope.gr_traffic_types[i] = $scope.all_gr_traffic_types[i];
    			 }
    		 }
    		 $scope.gr_ip_changed();
    	 };
    	 $scope.installGR = function(){
 	    	KVMService.comstackStatus($scope.gr_config.pri.deployment_prefix).then(function(status){
         		var ACTION_IN_PROGRESS = 2;
         		if(status.state == ACTION_IN_PROGRESS){
         			window.confirm(status.lastAction.toLowerCase()+" has been proceed on selected VNF instance, please wait!");
         		}else{
         			KVMService.comstackStatus($scope.gr_config.sec.deployment_prefix).then(function(status){
                 		var ACTION_IN_PROGRESS = 2;
                 		if(status.state == ACTION_IN_PROGRESS){
                 			window.confirm(status.lastAction.toLowerCase()+" has been proceed on selected VNF instance, please wait!");
                 		}else{
                             $scope.doInstallGR();
                 		}
                 	});
         		}
         	});
 	     };
    	 $scope.doInstallGR = function() {
    		 if($scope.COMStack.length > 0){
    			 for(var i=0; i < $scope.COMStack.length; i++){
     		    	if($scope.COMStack[i].name == $scope.gr_config.pri.stackName){
     		    		if($scope.COMStack[i].status == "GRINSTALLED"){
     		    			$scope.gr_config.gr_install_active = false;
     		    			if($scope.gr_config.sec.environment == "KVM"){
     		    				GRService.kvminstall($scope.gr_config).then( function(){
         		    				monitorService.monitor("KVM", "GR_SEC_INSTALL", $scope.gr_config.sec.comType,  $scope.gr_config.sec.deployment_prefix);
         		         			$state.go("dashboard.monitor");
         		    			});
     		    			}else{
     		    				GRService.osinstall($scope.gr_config).then( function(){
         		    				monitorService.monitor("Openstack", "GR_SEC_INSTALL", $scope.gr_config.sec.comType,  $scope.gr_config.sec.stackName);
         		         			$state.go("dashboard.monitor");
         		    			});
     		    			}
     		    		}else{
     		    			$scope.gr_config.gr_install_active = true;
     		    			if($scope.gr_config.pri.environment == "KVM"){
     		    				GRService.kvminstall($scope.gr_config).then( function(){
         		    				monitorService.monitor("KVM", "GR_PRI_INSTALL", $scope.gr_config.pri.comType,  $scope.gr_config.pri.deployment_prefix);
         		         			$state.go("dashboard.monitor");
         		    			});
     		    			}else{
     		    				GRService.osinstall($scope.gr_config).then( function(){
         		    				monitorService.monitor("Openstack", "GR_PRI_INSTALL", $scope.gr_config.pri.comType,  $scope.gr_config.pri.stackName);
         		         			$state.go("dashboard.monitor");
         		    			});
     		    			}
     		    		}
     		    	}
     		    }
    		 }
    	 };
    	 
}).controller('grcheck', function($scope, $modalInstance,$state,msg,e_cancel){
	$scope.ok = function () {
		$state.go('dashboard.grinstall', {}, {reload: true});
		$modalInstance.dismiss('cancel');
    };
    $scope.message = msg;
    $scope.can = e_cancel;
    $scope.cancel = function(){
    	$modalInstance.dismiss('cancel');
    };
}).controller('grUnInstallController', function($scope, $q, $timeout, $log, 
             $state, GRService,monitorService, $modal) {
	GRService.getComInstance().then(function( comstacks ){
		 if(comstacks.length > 0){
		    var instances = new Array();
		    for(var i=0; i < comstacks.length; i++){
		    	if(comstacks[i].status == "GRINSTALLED"){
		    		var temp_instance = JSON3.parse(comstacks[i].comConfig);
		    		if(temp_instance.stackName){
		    			instances.push(temp_instance);
		    		}else{
		    			temp_instance.stackName =temp_instance.deployment_prefix;
		    			instances.push(temp_instance);
		    		}
		    	}
		    }
		    $scope.instances = instances;
		 }
	 });
	 
	$scope.UnInstallGR = function(){
		 if($scope.gr_config.comConfig.environment == "KVM"){
			 GRService.kvmuninstall($scope.gr_config).then( function(){
					monitorService.monitor("KVM", "GR_UNINSTALL", $scope.gr_config.comConfig.comType,  $scope.gr_config.comConfig.deployment_prefix);
		         	$state.go("dashboard.monitor");
				});
			}else{
				GRService.osuninstall($scope.gr_config).then( function(){
					monitorService.monitor("Openstack", "GR_UNINSTALL", $scope.gr_config.comConfig.comType,  $scope.gr_config.comConfig.stackName);
		         	$state.go("dashboard.monitor");
				});
			}
	 };
	 $scope.translateSelectedGR = function(){
		 if($scope.gr_config.comConfig.environment == "KVM"){
			 $scope.priOAMRowspan = $scope.gr_config.comConfig.vm_config.oam.nic.length * 2;
				$scope.priDBRowspan = $scope.gr_config.comConfig.vm_config.db.nic.length * 2;
				if($scope.gr_config.comConfig.comType != "OAM"){
					$scope.priCMRowspan = $scope.gr_config.comConfig.vm_config.cm.nic.length * 2;
				}
		 }
	};
	$scope.changeForce = function(){
		if($scope.gr_config.force){
		      var modalInstance = $modal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'views/gr/forceConfirm.html',
		      controller: 'forceController',
		      size: "sm",
		      backdrop: true
		    });	
		    modalInstance.result.then(function () {
		    	$scope.gr_config.forced = true;
		    }, function () {
		    });	
		}else{
			$scope.gr_config.forced = false;
		}
	};
})
.controller('forceController', function($scope, $modalInstance){
	$scope.ok = function(){
		$modalInstance.close();
	};
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
    };
});
;




