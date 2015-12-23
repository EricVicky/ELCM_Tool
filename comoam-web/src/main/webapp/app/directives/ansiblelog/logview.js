'use strict';

angular.module('monitor').directive( 'ansiblelog', function($log, WizardHandler, websocketService, monitorService) {
					return {
						restrict : 'EA',
						replace : true,
						scope : {
							ansibleSteps : '=',
							channel: '=',
							endponit: '@'
						},
						controller : [ '$scope', '$element', '$log', 'WizardHandler', 'websocketService', 'monitorService', function($scope, $element, $log, WizardHandler, websocketService, monitorService) {
									var taskgroup = new Array();
									$scope.nologshow = true;
									$scope.loadingshow = true;
									$scope.isfailed = false;
									$scope.issucceed = false;
									$scope.nextstep = "Start";
									$scope.$on('$destroy', function() {
										$log.info("on destroy monitor");
										websocketService.disconnect();
									});
									$scope.logtail = function(data) {
										$scope.socket = websocketService.connect($scope.endponit, function( socket) { socket.stomp.subscribe(
															$scope.channel,
															$scope.showlog);
														$log.info("connected");
														monitorService.runAnsibleTask();	
												});
									};
									$scope.errorHandle = function(){
										
									};
									$scope.showlog = function(data) {
										var tasks = $('#tasks');
										var task = $('#task');
										var loadpos = $('#loadpos');
										//$log.info(data);
										var log = JSON3.parse(data.body);
										if (log.result != ''){
											if (log.result == "succeed") {
												$scope.$apply(function() {
													$scope.loadingshow = false;
													var realStepNumber = $scope.ansibleSteps.length;
													var maxNext = 10;
													var i =0;
													while(WizardHandler.wizard().currentStepNumber() < realStepNumber && i < maxNext){
														WizardHandler.wizard().next();
														i++;
													}
													WizardHandler.wizard().next();
													WizardHandler.wizard().finish();
												});
											}
											$scope.$apply(function() {
												$scope.result = log.result;
												if(log.result == 'succeed'){
													$scope.issucceed = true;
												}else{
													$scope.isfailed = true;
												}
												$scope.loadingshow = false;
												$scope.result_message = monitorService.getEndMsg(log.result);
											});
											$log.info("try to disconnect websocket in 10 second")
											//setTimeout(websocketService.disconnect,10000);
											return;
										}
										if ($scope.nextstep != log.step && log.step) {
											$scope.nextstep = log.step;
											$scope.$apply(function() {
                                                var realStepNumber = $scope.ansibleSteps.indexOf(log.step) + 1;
                                                while(WizardHandler.wizard().currentStepNumber() < realStepNumber){
                                                    WizardHandler.wizard().next();
                                                }
											})
										}
										if (log.task != null && log.task != "") {
											$scope.$apply(function() {
												$scope.nologshow = false;
											});
											tasks.append("<i class=\"fa fa-check\" style=\"color:green\"></i>"
															+ "&nbsp;&nbsp;"
															+ log.task + "<br>");
											taskgroup.push(log.task);
											var taskhtml = "";
											var loadinghtml="";
											var startIndex = taskgroup.length > 10 ? taskgroup.length - 10
													: 0;
											for (var n = startIndex; n < taskgroup.length; n++) {
												taskhtml = taskhtml
														+ "<i class=\"fa fa-check\" style=\"color:green\"></i>"
														+ "&nbsp;&nbsp;"
														+ taskgroup[n] + "<br>";
											}
											task.html(taskhtml);
											var position;
											if($('#task').height()!=0){
												position = $('#task').height();
											}else{
												position = 210;
											}
											loadinghtml = "<img src=\"images/spinner.gif\" style=\"padding-left:280px;padding-top:"+(position-8)+"px\"/>";
											loadpos.html(loadinghtml);
										}
										var logviewer = $('#logviewer');
										logviewer.append(log.logMsg + "<br>");
										logviewer.scrollTop(logviewer[0].scrollHeight - logviewer.height());
									};
									if ($scope.channel != null && $scope.channel!=''){
										$scope.logtail();
									}
								} ],
						templateUrl : "app/directives/ansiblelog/logviewer.html",
						link : function(scope, ele, attrs, ctrl) {
						}
					}
				});
