angular.module('datatable',['ui.grid', 'ui.grid.resizeColumns']).controller('datatablectr', function($scope){
    
	  $scope.myData = [
	                   {
	                       "firstName": "Cox",
	                       "lastName": "Carney",
	                       "company": "Enormo",
	                       "employed": true
	                   },
	                   {
	                       "firstName": "Lorraine",
	                       "lastName": "Wise",
	                       "company": "Comveyer",
	                       "employed": false
	                   },
	                   {
	                       "firstName": "Nancy",
	                       "lastName": "Waters",
	                       "company": "Fuelton",
	                       "employed": false
	                   }
	               ];
	$scope.uiGridOptions = {
			data: 'myData',
            enableGridMenu: true,
            enableRowSelection: false,
            enableSelectAll: false,
            multiSelect: false,
            enableFiltering: true,
			columnDefs: [
			             {displayName: 'DATA1',  name: 'firstName',
			            	 enableFiltering: false,
                        	 enableColumnMenu: false,
			            	 width:100
			             },
			             {displayName: 'DATA2',  name: 'lastName',
			            	 enableFiltering: false,
                        	 enableColumnMenu: false,
			            	 width:100
			             },
			             {displayName: 'DATA3',  name: 'company',
			            	 enableFiltering: false,
                        	 enableColumnMenu: false,
			            	 width:100
			             },
                         {displayName: 'Operation', name: 'Operation', 
                        	 cellTemplate: 'views/dashboard/component/groupbar.html',
                        	 enableColumnMenu: false,
                        	 enableFiltering: false,
                        	 enableSorting: false,
                        	 enableColumnResizing: false
                         }
                        ],
            onRegisterApi: function( gridApi ) { 
                $scope.gridApi = gridApi;
            }
	};
	
	$scope.godelete = function(row){
		  $scope.selectedIns = row.entity.comConfig;
	};
	
}).controller('deleteController', function($scope){
	 $scope.ok = function(){
		 $modalInstance.close();
	 };
	 $scope.cancel = function () {
		 $modalInstance.dismiss('cancel');
     };
});