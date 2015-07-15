'use strict';

angular.module('monitor').directive( 'ansiblelog', function($log, WizardHandler, websocketService ) {
					return {
						restrict : 'EA',
						replace : true,
						scope : {
							ansibleSteps : '=',
							channel: '=',
							endponit: '@'
						},
						controller : [ '$scope', '$element', '$log', 'WizardHandler', 'websocketService', function($scope, $element, $log, WizardHandler, websocketService) {
									var taskgroup = new Array();
									$scope.loadingshow = true;
									$scope.nextstep = "Start";
									$scope.$on('$destroy', function() {
										websocketService.disconnect();
									});
									$scope.logtail = function(data) {
										$scope.socket = websocketService.connect($scope.endponit, function( socket) { socket.stomp.subscribe(
															$scope.channel,
															$scope.showlog);
												});
									};
									$scope.showlog = function(data) {
										var tasks = $('#tasks');
										var task = $('#task');
										var loadpos = $('#loadpos');
										$log.info(data);
										if (data.body == "end") {
											$scope.$apply(function() {
														$scope.loadingshow = false;
														WizardHandler.wizard().finish();
													});
											return;
										}
										var log = JSON3.parse(data.body);
										if ($scope.nextstep != log.step) {
											$scope.nextstep = log.step;
											$log.info("nextstep=" + $scope.nextstep);
											$scope.$apply(function() {
												WizardHandler.wizard().next();
											})
										}
										if (log.task != null && log.task != "") {
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
											if(taskgroup.length<10){
												$scope.loadingpos=10+(taskgroup.length-1)*20;
											}else{
												$scope.loadingpos=190;
											}
											loadinghtml = "<img src=\"images/spinner.gif\" style=\"padding-left:280px;padding-top:"+$scope.loadingpos+"px\"/>";
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
