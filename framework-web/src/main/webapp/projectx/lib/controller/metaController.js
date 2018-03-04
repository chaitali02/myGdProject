(function() {

	'use strict';
	var metaModule = angular.module('meta_model', [ 'datatables' ]);

	metaModule.controller('meta_controller', meta_controller);

	function meta_controller(DTOptionsBuilder, DTColumnBuilder, $scope,$rootScope) {

		$rootScope.$on("CallParentMethod", function() {
			$scope.changeData();
		});

		$scope.dtOptions = DTOptionsBuilder.fromSource("/framework/metadata/getAll?type=condition")

		$scope.dtColumns = [
				DTColumnBuilder.newColumn('id').withTitle('ID').notVisible(),
				DTColumnBuilder.newColumn('uuid').withTitle('UUID'),
				DTColumnBuilder.newColumn('name').withTitle('Name'),
				DTColumnBuilder.newColumn('version').withTitle('Version'),
				DTColumnBuilder.newColumn('createdBy.ref.name').withTitle('CreatedBy'),
				DTColumnBuilder.newColumn('createdOn').withTitle('CreatedOn'),
				DTColumnBuilder.newColumn(null).withTitle('Action').notSortable().renderWith(actionsHtml)

		];

		$scope.dtInstance = {};
		$scope.changeData = changeData;
		function changeData() { 

			$scope.dtInstance.changeData('/framework/metadata/getAll?type='+ $rootScope.menuvalue);
		}

		function actionsHtml(data, type, full, meta) { //alert($rootScope.menuvalue);

			return '<div class="btn-group pull-right">'
					+ '<button class="btn green btn-xs btn-outline dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Action'
					+ '<i class="fa fa-angle-down"></i></button>'
					+ '<ul class="dropdown-menu pull-right">'
					+ '<li><a href="javascript:;"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>'
					+ '<li><a href="../admin_1_rounded/formula.html"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>'
					+ '<li><a href="javascript:;"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>'
					+ ' </ul></div>';
		}

	}

	metaModule.controller('LHS_controller', function($scope, $rootScope) {

		$scope.selectMeta = function(metadata) {
			$rootScope.menuvalue = metadata;
			$rootScope.$emit("CallParentMethod", {});

		};

	});

})();