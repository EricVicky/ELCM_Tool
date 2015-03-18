var app = angular.module('comoam', [ 'ui.router', 'ui.bootstrap', 'rcWizard',
		'rcForm', 'rest' ]);
app.controller('kvmctr', function($scope, $q, $timeout) {
			$scope.user = {};
			$scope.user = {};
			$scope.saveState = function() {
				var deferred = $q.defer();
				$timeout(function() { 
					deferred.resolve();
				}, 1000);
				return deferred.promise;
			};
			$scope.completeWizard = function() {
				alert('Completed!');
			}
            $scope.com_types = [{
                type: 'FCAPS'
            }, {
                type: 'QoSAC'
            }, {
                type: 'CM'
            }, {
                type: 'OAM'
            }];
            $scope.support_grs = [{
                gr: 'Yes'
            }, {
                gr: 'No'
            }];
            $scope.timezones = [{
                timezone: 'Asia/Shanghai'
            }, {
                timezone: 'American/New York'
            }, {
                timezone: 'London'
            }];
            $scope.oam_cm_images = [{
                image: 'Redhat+orac_client'
            }, {
                image: 'Redhat+orac_server'
            }];
            $scope.db_images = [{
                image: 'Redhat+orac_client'
            }, {
                image: 'Redhat+orac_server'
            }];

			//$('#myModal').modal('show');
<<<<<<< HEAD
} );
=======
		} );
>>>>>>> a0534237c8179937eed085d26950e15bbe846e6c
