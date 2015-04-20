var app = angular.module('kvminstall', [ 'ui.router', 'ui.bootstrap', 'rcWizard',
		'rcForm', 'rest', 'websocket', 'ghiscoding.validation', 'mgo-angular-wizard']);

app.controller('kvmctr', function($scope, $q, $timeout, $log, KVMService,
		$state, websocketService, validationService, WizardHandler) {
			var logviewer = $('#logviewer');
			var task = $('#task');
			$scope.editing = true;
			$scope.detaillog = false;
			$scope.user = {};
			//$scope.installConfig.vm_config.oam.ip_address = "";
			$scope.submitComtype = function(){
				$scope.loadimglist($scope.installConfig.active_host_ip.ip_address, $scope.installConfig.vm_img_dir);
			}
			$scope.imagelist= [];
			$scope.completeWizard = function() {
				$scope.deploy();
			};
			$scope.support_ars = [ 'True', 'False' ];
            $scope.installConfig ={
            		vm_img_dir:"/var/images",
            		vm_config: {
            		  oam:{
            		    ip_address: "10.223.0.50",
            		  },
            		  db:{
            		    ip_address: "10.223.0.54",
            		  },
            		  cm:{
            		    ip_address: "135.251.236.105",
            		  }
            		}
            };
            $scope.nextstep = null;
            $scope.logtail = function(data){
        		$scope.socket = websocketService.connect("/oam", function(socket) {
        			socket.stomp.subscribe('/log/tail', $scope.showlog);
        		});
            }
            
            $scope.showDetailLog= function(){
            	$scope.detaillog= !$scope.detaillog;
            }
            
            $scope.showlog= function(data){
            //	$log.info(data);
            	var log =  JSON3.parse(data.body);
            	if( $scope.nextstep != log.step){
            		$scope.nextstep = log.step;
            		$scope.$apply(function(){
        				WizardHandler.wizard().next();
            		})
            	}
            	if(log.task!=null && log.task!=""){
            		task.text(log.task);
            	}
            	logviewer.append(log.logMsg + "\n");
            	logviewer.scrollTop(logviewer[0].scrollHeight - logviewer.height());
            }
			$scope.deploy = function (){
				if($scope.installConfig.comType=='FCAPS' || $scope.installConfig.comType=='OAM' || $scope.installConfig.comType=='CM' || $scope.installConfig.comType=='QoSAC'){
					$scope.installConfig.vm_config.oam.netmask = $scope.netmask;
					$scope.installConfig.vm_config.oam.gateway = $scope.gateway;
				}
				if($scope.installConfig.comType=='FCAPS' || $scope.installConfig.comType=='OAM' || $scope.installConfig.comType=='CM'){
					$scope.installConfig.vm_config.db.netmask = $scope.netmask;
					$scope.installConfig.vm_config.db.gateway = $scope.gateway;
				}
				if($scope.installConfig.comType=='FCAPS' || $scope.installConfig.comType=='CM'){
					$scope.installConfig.vm_config.cm.netmask = $scope.netmask;
					$scope.installConfig.vm_config.cm.gateway = $scope.gateway;
				}
				$scope.netmask = null;
				$scope.gateway = null;
            	KVMService.deploy(
                 		$scope.installConfig,
            			function(data){
                 			$scope.logtail(data); 
                 			$scope.editing = false;
                 			//$state.go("dashboard.installpregress");
            			}, 
            			function(response){
            					$log.info(response);
            			});
            };
            $scope.loadimglist = function(host, dir){
            	KVMService.imagelist(
            			{ "host":host, "dir":dir},
            			function(data) {
            				$log.info(data);
            				$scope.imagelist = data;
            				$scope.installConfig.oam_cm_image = $scope.imagelist[0];
            				$scope.installConfig.db_image = $scope.imagelist[1];
            			}, 
            			function(response){
            				$log.error(response);
            			}
            	);
            };
			(function (){
            	KVMService.getFlavorStore(
            			function(data) {
            				$scope.flavorStore = data.Flavors;
            			}, 
            			function(response){
            				$log.error(response);
            			});
            })();
            (function (){
            	KVMService.getComTypeStore(
            			function(data) {
            				$scope.comTypeStore = data;
            				$scope.installConfig.comType = $scope.comTypeStore[0].Name;
            			}, 
            			function(response){
            				$log.error(response);
            			}
            	);
            })();
            (function (){
            	KVMService.getTimezoneStore(
            			function(data) {
            				$scope.timezoneStore = data;
            				$scope.installConfig.timezone = $scope.timezoneStore[0].Time;
            			}, 
            			function(response){
            				$log.error(response);
            			}
            	);
            })();
            (function (){
            	KVMService.hostips(
            			function(data) {
            				$log.info(data);
            				$scope.hostIPs = data;
            				$scope.installConfig.active_host_ip = $scope.hostIPs[0];
            			}, 
            			function(response){
            				$log.error(response);
            			}
            	);
            })();
} );

app.controller('upgradectr', function($scope, $q, $timeout, $log, KVMService, $state, websocketService, validationService, WizardHandler) {
	var logviewer = $('#logviewer');
	var task = $('#task');
	var tasks = $('#tasks');
    var taskgroup = new Array();
	$scope.editing = true;
	$scope.detaillog = false;
	$scope.buttonlog = true;
	$scope.loadingshow = true;
	$scope.user = {};
	$scope.saveState = function() {
		var deferred = $q.defer();
		$timeout(function() { 
			deferred.resolve();
		}, 1);
		return deferred.promise;
	};
	$scope.completeWizard = function() {
		$scope.upgrade();
	};
	
	$scope.nextstep = null;
    $scope.logtail = function(data){
		$scope.socket = websocketService.connect("/oam", function(socket) {
			socket.stomp.subscribe('/log/tail', $scope.showlog);
		});
    }
    
    $scope.showDetailLog= function(){
    	$scope.detaillog= !$scope.detaillog;
    }	
    $scope.showButton= function(){
    	$scope.buttonlog= !$scope.buttonlog;
    }
    
    $scope.showlog= function(data){
    	$log.info(data);
    	if(data.body == "end"){
    		$scope.$apply(function(){
    			$scope.loadingshow = false;
    		});
    		return;
    	}
    	var log =  JSON3.parse(data.body);
    	if( $scope.nextstep != log.step){
    		$scope.nextstep = log.step;
    		$scope.$apply(function(){
				WizardHandler.wizard().next();
    		})
    	}
    	if(log.task!=null && log.task!=""){
    		tasks.append("<i class=\"fa fa-check\" style=\"color:green\"></i>" + "&nbsp;&nbsp;" + log.task + "<br>");
    		taskgroup.push(log.task);
    		var taskhtml = "";
    		var startIndex = taskgroup.length >10?taskgroup.length-10:0;
    		for(var n=startIndex; n<taskgroup.length; n++){
            		taskhtml = taskhtml + "<i class=\"fa fa-check\" style=\"color:green\"></i>" + "&nbsp;&nbsp;" + taskgroup[n] + "<br>";
            }
    		task.html(taskhtml);
    	}
    	logviewer.append(log.logMsg + "<br>");
    	logviewer.scrollTop(logviewer[0].scrollHeight - logviewer.height());
    }
	$scope.loadimglist = function(host, dir) {
		KVMService.imagelist({
			"host" : host,
			"dir" : dir
		}, function(data) {
			$log.info(data);
			$scope.imagelist = data;
			$scope.$parent.oam_cm_image = $scope.imagelist[0];
			$scope.$parent.db_image = $scope.imagelist[1];
		}, function(response) {
			$log.error(response);
		});

	};
    $scope.reloadimglist = function(){
    	if($scope.com_instance != null){
        	$scope.installConfig = JSON3.parse($scope.com_instance.comConfig);
    	}
        $scope.vm_img_dir = $scope.installConfig.vm_img_dir;
    	$scope.loadimglist($scope.installConfig.active_host_ip.ip_address, $scope.vm_img_dir);
    }
	$scope.upgrade = function (){
		var installConfig = JSON3.parse($scope.com_instance.comConfig);
		installConfig.oam_cm_image = $scope.oam_cm_image;
		installConfig.db_image = $scope.db_image;
		installConfig.com_iso = $scope.com_iso;
		installConfig.vm_img_dir = $scope.vm_img_dir;
		KVMService.upgrade(
         		$scope.installConfig,
    			function(data){
         			$scope.logtail(data);
         			$scope.editing = false;
         			//$state.go("dashboard.upgradepregress");
    			}, 
    			function(response){
    					$log.info(response);
    			});
    };
    (function (){
    	KVMService.getComInstance(
    			function(data) {
    				$log.info(data);
    				$scope.comInstance = data;
    			}, 
    			function(response){
    				$log.error(response);
    			}
    	);
    })();
} );


