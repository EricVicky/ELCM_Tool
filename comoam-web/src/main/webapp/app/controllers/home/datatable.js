angular.module('datatable',['ui.grid', 'ui.grid.resizeColumns']).controller('datatablectr', function($scope){

	$scope.comGridOptions = {
			data: '',
            enableGridMenu: true,
            enableRowSelection: false,
            enableSelectAll: false,
            multiSelect: false,
            enableFiltering: true,
			columnDefs: [
			             {displayName: 'DATA',  name: 'name',
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