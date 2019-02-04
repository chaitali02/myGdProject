
DatavisualizationModule = angular.module('DatavisualizationModule')
DatavisualizationModule.controller('ReportListController', function ($filter, $rootScope, $scope, $sessionStorage, $state, CommonService, dagMetaDataService, FileSaver, Blob, privilegeSvc, CF_META_TYPES) {

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});
    $scope.updateStats = function () {
		CommonService.getMetaStats("report").then(function (response) {
		  if (response.data && response.data.length && response.data.length > 0) {
			$rootScope.metaStats["report"] = response.data[0];
		  }
		});
	  }
	 
	$scope.updateStats();
	var notify = {
		type: 'info',
		title: 'Info',
		content: '',
		timeout: 3000 //time in ms
	};

	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.report] || [];

	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.report] || [];

	});
    
	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
	}

	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRows && $scope.filteredRows.length > 0) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
		} else {
			style['height'] = "100px"
		}
		return style;
	}
	$scope.gridOptions = {};
	$scope.gridOptions = angular.copy(dagMetaDataService.gridOptionsDefault);
	$scope.gridOptions.columnDefs.splice(0,0,{
		displayName: 'Locked',
		name: 'locked',
		maxWidth:100,
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		cellTemplate: ['<div class="ui-grid-cell-contents">',
        '<div ng-if="row.entity.locked == \'Y\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Unlock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'UnLock\')"><i  title ="Lock" class="icon-lock" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
        '<div  ng-if="row.entity.locked == \'N\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Lock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'Lock\')"><i title ="UnLock" class="icon-lock-open" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
        ].join('')
	  })
	$scope.gridOptions.columnDefs.push({
		displayName: 'Status',
		name: 'active',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.active == "Y" ? "Active" : "In Active"}}</div>'
	}, {
			displayName: 'Publish',
			name: 'publish',
			cellClass: 'text-center',
			headerCellClass: 'text-center',
			cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.published == "Y" ? "Yes" : "No"}}</div>'
		}, {
			displayName: 'Action',
			name: 'action',
			cellClass: 'text-center',
			headerCellClass: 'text-center',
			maxWidth: 110,
			cellTemplate: [

				'<div class="ui-grid-cell-contents">',
				'<div class="col-md-12" style="display:inline-flex;padding-left:0px;padding-right:0px;">',
				'  <div class="col-md-10 dropdown" uib-dropdown dropdown-append-to-body>',
				'    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
				'    <i class="fa fa-angle-down"></i></button>',
				'    <ul uib-dropdown-menu class="dropdown-menu-grid">',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.view(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Edit\') != -1 && row.entity.locked ==\'N\'?false:true" ><a ng-click="grid.appScope.edit(row.entity)"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Delete\') == -1" ng-if="row.entity.active == \'Y\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Delete\')"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Restore\') == -1" ng-if="row.entity.active == \'N\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Restore\')"><i class="fa fa-retweet" aria-hidden="true"></i>  Restore</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unlock\') == -1" ng-if="row.entity.locked == \'N\'"><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'Lock\')"><i class="icon-lock" aria-hidden="true"></i> Lock</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Lock\') == -1" ng-if="row.entity.locked == \'Y\'"><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'UnLock\')"><i class="icon-lock-open" aria-hidden="true"></i>  Unlock</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Publish\') == -1" ng-if="row.entity.published == \'N\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Publish\')"><i class="fa fa-share-alt" aria-hidden="true"></i>  Publish</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unpublish\') == -1 || row.entity.createdBy.ref.name != grid.appScope.loginUser" ng-if="row.entity.published == \'Y\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Unpublish\')"><i class="fa fa-shield" aria-hidden="true"></i>  Unpublish</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Clone\') == -1"><a ng-click="grid.appScope.createCopy(row.entity)"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>',
				'    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.export(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
				'    </ul>',
				'  </div>',
				'</div>'

			].join('')
		});
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

	$scope.refreshData = function (searchtext) {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
	}


	$scope.addMode = function () {
		var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
		setTimeout(function () { $state.go(state); }, 100);
	}

	$scope.getData = function (response) {
		$scope.gridOptions.data = null;
		$scope.gridOptions.data = response.data;
		$scope.originalData = response.data;
	}


	$scope.view = function (data) {
		var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
		setTimeout(function () { $state.go(state, { 'id': data.uuid, 'version': data.version, 'mode': 'true' }); }, 100);
	}


	$scope.edit = function (data) {
		var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
		setTimeout(function () { $state.go(state, { 'id': data.uuid, 'version': data.version, 'mode': 'false' }); }, 100);
	}

	$scope.createCopy = function (data) {
		var uuid = data.uuid;
		var version = data.version;
		$scope.obj = {};
		$scope.obj.uuid = uuid;
		$scope.obj.version = version;
		$scope.msg = "Clone"
		$('#confModal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}
	$scope.export = function (data) {
		var uuid = data.uuid;
		var version = data.version;
		$scope.obj = {};
		$scope.obj.uuid = uuid;
		$scope.obj.version = version;
		$scope.msg = "Export"
		$('#confModal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.deleteOrRestore = function (data, action) {
		var uuid = data.uuid;
		var version = data.version;
		$scope.obj = data;
		$scope.msg = action;
		$('#confModal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.publishOrUnpublish = function (data, action) {
		var uuid = data.uuid;
		var version = data.version;
		$scope.obj = data;
		$scope.msg = action;
		$('#confModal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.lockOrUnLock = function (data, action) {
		var uuid = data.uuid;
		var version = data.version;
		$scope.obj = data;
		$scope.msg = action;
		$('#confModal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}


	$scope.submitOk = function (action) {
		if (action == "Clone") {
			$scope.okClone();
		}
		else if (action == "Export") {
			$scope.okExport();
		}
		else if (action == "Delete") {
			$scope.okDelete();
		}
		else if (action == "Restore") {
			$scope.okDelete();
		} else if (action == "Publish") {
			$scope.okPublished();
		}
		else if (action == "Unpublish") {
			$scope.okPublished();
		}else if(action == "Lock"){
			$scope.okLocked();
		}
		else if(action == "UnLock"){
			$scope.okLocked();
		}
	}

	$scope.okClone = function () {
		$('#confModal').modal('hide');
		CommonService.getSaveAS($scope.obj.uuid, $scope.obj.version, CF_META_TYPES.report).then(function (response) { onSuccessSaveAs(response.data) });
		var onSuccessSaveAs = function (response) {
			$scope.originalData.splice(0, 0, response);
			$scope.message = "Report Cloned Successfully"
			notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.message
			$scope.$emit('notify', notify);
		}
	}


	$scope.okExport = function () {
		$('#confModal').modal('hide');
		CommonService.getLatestByUuid($scope.obj.uuid, CF_META_TYPES.report).then(function (response) {
			onSuccessGetUuid(response.data)
		});
		var onSuccessGetUuid = function (response) {
			var jsonobj = angular.toJson(response, true);
			var data = new Blob([jsonobj], {
				type: 'application/json;charset=utf-8'
			});
			FileSaver.saveAs(data, response.name + '.json');
			$scope.message = "Report Downloaded Successfully";
			notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.message
			$scope.$emit('notify', notify);
		}
	}

	$scope.okDelete = function () {
		$('#DeleteConfModal').modal('hide');
		$('#confModal').modal('hide');
		if ($scope.obj.active == 'Y') {
			CommonService.delete($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessDelete(response.data) });
			var OnSuccessDelete = function (response) {
			//	$scope.alldashboard[$scope.obj.index].active = response.active;
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].active = response.active;
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = "Report Deleted Successfully"
				$scope.$emit('notify', notify);
			}
		}
		else {
			CommonService.restore($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessRestore(response.data) });
			var OnSuccessRestore = function (response) {
				//$scope.alldashboard[$scope.obj.index].active = 'Y'
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].active = "Y"
				notify.type = 'success',
					notify.title = 'Success',
					notify.content = "Report Restored Successfully"
				$scope.$emit('notify', notify);
			}
		}
	}


	$scope.okPublished = function () {
		$('#confModal').modal('hide');
		if ($scope.obj.published == 'N') {
			CommonService.publish($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessPublush(response.data) });
			var OnSuccessPublush = function (response) {
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].published = response.published;
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = "Report Publish Successfully"
				$scope.$emit('notify', notify);
			}
		}
		else {
			CommonService.unpublish($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessUnpublush(response.data) });
			var OnSuccessUnpublush = function (response) {
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].published = "N"
				notify.type = 'success',
					notify.title = 'Success',
					notify.content = "Report Unpublish Successfully"
				$scope.$emit('notify', notify);
			}
		}
	}
	$scope.okLocked = function () {
		$('#confModal').modal('hide');
		if ($scope.obj.locked == 'N') {
			CommonService.lock($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessLock(response.data) });
			var OnSuccessLock = function (response) {
				
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].locked ="Y";
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = "Report Lock Successfully"
				$scope.$emit('notify', notify);
			}
		}
		else {
			CommonService.unLock($scope.obj.id, CF_META_TYPES.report).then(function (response) { OnSuccessUnLock(response.data) });
			var OnSuccessUnLock = function (response) {
				if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
					$scope.gridOptions.data[$scope.obj.index].locked = "N"
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = "Report Unpublish Successfully"
				$scope.$emit('notify', notify);
			}
		}
	}

});//End ReportListController

DatavisualizationModule.controller('ReportDetailController', function ($q, dagMetaDataService, $location, $http, $rootScope, $state, $scope, $stateParams, $cookieStore, $timeout, $filter, ReportSerivce, $sessionStorage, privilegeSvc, CommonService, CF_FILTER, CF_META_TYPES, CF_DOWNLOAD) {
	$rootScope.isCommentVeiwPrivlage = true;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.isDragable = "false";
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isDragable = "true";
		$scope.isPanelActiveOpen = true;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else {
		$scope.isAdd = true;
		$scope.isDragable = "true";
	}
	$scope.userDetail = {}
	$scope.alignType = ["left", "right", "center"]
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.mode = "false";
	$scope.dataLoading = false;
	$scope.iSSubmitEnable = false;
	$scope.reportVersion = {};
	$scope.reportVersion.versions = []
	$scope.showForm = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showGraphDiv = false
	$scope.graphDataStatus = false
	//	$scope.logicalOperator = ["AND","OR"];
	$scope.SourceTypes = ["datapod", "relation", 'dataset']
	//	$scope.spacialOperator = ['<', '>', '<=', '>=', '=', 'LIKE', 'NOT LIKE', 'RLIKE'];
	$scope.operator = CF_FILTER.operator;
	$scope.download = {};
	$scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates = CF_DOWNLOAD.formate;
	$scope.download.selectFormate = CF_DOWNLOAD.formate[0];
	$scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to = CF_DOWNLOAD.limit_to;
	$scope.isSubmitEnable = true;
	$scope.attributeTableArray = null;
	$scope.datsetsampledata = null;
	$scope.isShowSimpleData = false;
	$scope.isDependencyShow = false;
	$scope.isSimpleRecord = false;
	$scope.vizpodbody = {};
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.report] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.report] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	// $scope.lhsType = [
	// 	{ "text": "string", "caption": "string" },
	// 	{ "text": "string", "caption": "integer" },
	// 	{ "text": "datapod", "caption": "attribute" },
	// 	{ "text": "formula", "caption": "formula" }];
	// $scope.rhsType = [
	// 	{ "text": "string", "caption": "string", "disabled": false },
	// 	{ "text": "string", "caption": "integer", "disabled": false },
	// 	{ "text": "datapod", "caption": "attribute", "disabled": false },
	// 	{ "text": "formula", "caption": "formula", "disabled": false },
	// 	{ "text": "dataset", "caption": "dataset", "disabled": false },
	// 	{ "text": "paramlist", "caption": "paramlist", "disabled": false },
	//   { "text": "function", "caption": "function", "disabled": false }]

	/*Start showPage*/
	$scope.showPage = function () {
		$scope.isShowSimpleData = false
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.showHome=function(uuid, version,mode){
		$scope.showPage()
		var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
		$state.go(state, {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.report.locked =="Y"){
			return false;
		}
		$scope.showPage()
		var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
		setTimeout(function () { $state.go(state, { 'id': uuid, 'version': version, 'mode': 'false' }); }, 100);
	}

	$scope.showView = function (uuid, version) {
		if (!$scope.isEdit) {
			$scope.showPage()
			var state = dagMetaDataService.elementDefs[CF_META_TYPES.report].detailState
			setTimeout(function () { $state.go(state, { 'id': uuid, 'version': version, 'mode': 'false' }); }, 100);
		}
	}

	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};


	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag = response[0].value
		}
	}

	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
	$scope.getLovByType();

	$scope.sourceAttributeTypes = [
		{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "expression", "caption": "expression" },
		{ "text": "formula", "caption": "formula" },
		{ "text": "function", "caption": "function" },
		{ "text": "paramlist", "caption": "paramlist" }
	];

	$scope.defalutType = ["Simple", 'Formula', 'Expression'];
	$scope.filterTableArray = [];
	$scope.tags = null;
	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
	}
	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	$scope.gridOptions = {
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}
	$scope.gridOptions.columnDefs = [];
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRows && $scope.filteredRows.length > 0) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}


	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		if (fromState.name != "matadata") {
			$sessionStorage.fromParams = fromParams
		}
	});

	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
		$scope.isShowSimpleData = false
	}/*End ShowGraph*/

	$scope.openFilterPopup = function () {
		if ($scope.filterAttribureIdValues == null) {
			$scope.getFilterValue($scope.report);
		}
		$('#attrFilter').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.onChipsRemove = function (index, filterIndex) {

		$scope.filterTag.splice(index, 1);
		$scope.selectedAttributeValue[filterIndex] = null;
		var noSelect = { "id": null, "value": "-select-" }
		setTimeout(function () {
			$scope.selectedAttributeValue[filterIndex] = noSelect;
			$scope.applyFilter();
		}, 100);

	}
	
	
    $scope.autoPopulate=function(){
		$scope.attributeTableArray=[];
		for(var i=0;i<$scope.sourcedatapodattribute.length;i++){
			var attributeinfo = {};
			attributeinfo.id =i;
			attributeinfo.sourcedatapod=$scope.sourcedatapodattribute[i];
			attributeinfo.name=$scope.sourcedatapodattribute[i].name;
			attributeinfo.sourceAttributeType = $scope.sourceAttributeTypes[1];
			attributeinfo.isSourceAtributeSimple = false;
			attributeinfo.isSourceAtributeDatapod = true;
			attributeinfo.isSourceAtributeFormula = false;
			attributeinfo.isSourceAtributeExpression = false;
			attributeinfo.isSourceAtributeFunction = false;
			attributeinfo.isSourceAtributeParamList = false;
			attributeinfo.isSourceName=true;
			$scope.attributeTableArray.push(attributeinfo);
			setTimeout(function(index){
			//	console.log(index);
				$scope.onChangeSourceName(index);
			},10,(i));

		}
		
	}
	$scope.ondrop = function(e) {
		console.log(e);
		$scope.myform.$dirty=true;
	}

	$scope.onTagRemove = function (e) {
		console.log(e);
		console.log(JSON.stringify($scope.filterTag));
		$scope.selectedAttributeValue[e.index] = null;
		var noSelect = { "id": null, "value": "-select-" }
		setTimeout(function () {
			$scope.selectedAttributeValue[e.index] = noSelect;
			$scope.applyFilter();
		}, 100);

		// for(var i=e.index;i<$scope.filterTag.length;i++){
		// 	$scope.filterTag[i].index=$scope.filterTag[i].index-1;
		// }

	}
	// var reA = /[^a-zA-Z]/g;
	// var reN = /[^0-9]/g;
	// function sortAlphaNum(propName) {
	// 	return function(a,b){
	// 		if(isNaN(a[propName])){
	// 			var aA = a[propName].replace(reA, "");
	// 			var bA =b[propName].replace(reA, "");	
	// 		}else{
	// 			var aA = parseFloat(a[propName]).toFixed(2).replace(reA, "");
	// 			var bA =parseFloat(b[propName]).toFixed(2).replace(reA, "");	
	// 		}
	// 		if(aA === bA) {
	// 		  if(isNaN(a[propName])){
	// 				var aN = parseFloat(a[propName].replace(reN, ""), 10);
	// 				var bN = parseFloat(b[propName].replace(reN, ""), 10);
	// 			}else{	
	// 				var aN = parseFloat(a[propName].toFixed(2).replace(reN, ""), 10);
	// 				var bN = parseFloat(b[propName].toFixed(2).replace(reN, ""), 10);
	// 			}
	// 			return aN === bN ? 0 : aN > bN ? 1 : -1;
	// 		} else {
	// 			return aA > bA ? 1 : -1;
	// 		}
	// 	}
	// }
	$scope.getFilterValue = function (data) {
		$scope.filterAttribureIdValues = []
		$scope.selectedAttributeValue = []
		if (data.filterInfo && data.filterInfo.length > 0) {
			var filterAttribureIdValue = [];
			for (var n = 0; n < data.filterInfo.length; n++) {
				var filterattributeidvalepromise = ReportSerivce.getAttributeValues(data.filterInfo[n].ref.uuid, data.filterInfo[n].attrId, data.filterInfo[n].ref.type);
				filterAttribureIdValue.push(filterattributeidvalepromise);
			}//End For Loop
			$q.all(filterAttribureIdValue).then(function (result) {
				for (var i = 0; i < result.length; i++) {
					var filterAttribureIdvalueJSON = {};
					var defaultvalue = {}
					defaultvalue.id = null;
					defaultvalue.value = "-select-"
					filterAttribureIdvalueJSON.vizpoduuid =
						filterAttribureIdvalueJSON.vizpodversion = data.filterInfo[i].ref.uuid;
					filterAttribureIdvalueJSON.datapoduuid = data.filterInfo[i].ref.uuid;
					filterAttribureIdvalueJSON.type = data.filterInfo[i].ref.type;
					if(data.filterInfo[i].ref.type !="formula"){
						filterAttribureIdvalueJSON.datapodattrId = data.filterInfo[i].attrId;
						filterAttribureIdvalueJSON.attrName =data.filterInfo[i].attrName;
						filterAttribureIdvalueJSON.dname = data.filterInfo[i].ref.name + "." + data.filterInfo[i].attrName;
					  }
					  else{
						filterAttribureIdvalueJSON.attrName =data.filterInfo[i].ref.name;
						filterAttribureIdvalueJSON.dname = "formula"+"." +data.filterInfo[i].ref.name;
					  }
					//filterAttribureIdvalueJSON.datapodattrId = data.filterInfo[i].attrId;
				//	filterAttribureIdvalueJSON.dname = data.filterInfo[i].ref.name + "." + data.filterInfo[i].attrName;
					filterAttribureIdvalueJSON.name = data.filterInfo[i].ref.name
				//	filterAttribureIdvalueJSON.attrName = data.filterInfo[i].attrName;

					// if(result[i].data.length >0)
					// result[i].data.sort(sortAlphaNum('value'));
					filterAttribureIdvalueJSON.values = result[i].data
					filterAttribureIdvalueJSON.values.splice(0, 0, defaultvalue)
					$scope.selectedAttributeValue[i] = defaultvalue
					$scope.filterAttribureIdValues[i] = filterAttribureIdvalueJSON
					//console.log(JSON.stringify($scope.filterAttribureIdValues))
				}
			}, function (response) {
				$('#attrFilter').modal("hide");
				$scope.isDataInpogress = true;
				$scope.isDataError = true;
				$scope.msgclass = "errorMsg";
				$scope.datamessage = "Some Error Occurred";
				$scope.spinner = false;
			});//End $q.all
		}//End If
	}//End getFilterValue

	$scope.applyFilter = function (index) {
		console.log(JSON.stringify($scope.selectedAttributeValue));
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.isDataError = false;
		$scope.tableclass = "centercontent";
		$scope.showForm = false;
		$scope.showGraphDiv = false;
		var count = 0;
		$scope.filterListarray = [];
		$scope.filterTag = [];
		for (var i = 0; i < $scope.selectedAttributeValue.length; i++) {
			var filterList = {};
			var ref = {};
			var filterTag = {};
			if ($scope.selectedAttributeValue[i].value != "-select-") {
				ref.type = $scope.filterAttribureIdValues[i].type;
				ref.uuid = $scope.filterAttribureIdValues[i].datapoduuid
				filterList.ref = ref;
				if($scope.filterAttribureIdValues[i].type !="formual"){
					filterList.attrId = $scope.filterAttribureIdValues[i].datapodattrId;
					filterTag.text = $scope.filterAttribureIdValues[i].attrName + " - " + $scope.selectedAttributeValue[i].value;
				  }
				  else{
					filterTag.text = $scope.filterAttribureIdValues[i].name + " - " + $scope.selectedAttributeValue[i].value;
				  }
			//	filterList.attrId = $scope.filterAttribureIdValues[i].datapodattrId
			//	filterTag.text = $scope.filterAttribureIdValues[i].attrName + " - " + $scope.selectedAttributeValue[i].value;
				filterTag.index = i;
				filterTag.value = $scope.selectedAttributeValue[i].value;
				filterList.value = $scope.selectedAttributeValue[i].value;//"'"+$scope.selectedAttributeValue[i].value+"'";
				$scope.filterListarray[count] = filterList;
				$scope.filterTag[count] = filterTag;
				count = count + 1;
			}
		}
		console.log(JSON.stringify($scope.filterListarray));
		if ($scope.filterListarray.length > 0) {
			$scope.vizpodbody = {};
			$scope.vizpodbody.filterInfo = $scope.filterListarray;
		} else {
			$scope.vizpodbody = null;
		}
		$('#attrFilter').modal("hide");
		$scope.reportExecute($scope.report, $scope.vizpodbody);
	}
	$scope.CancleFitler = function () {
		// $scope.isShowSimpleData = true
		// $scope.isDataInpogress = true
		// $scope.isDataError = false;
		// $scope.tableclass = "centercontent";
		// $scope.showForm = false;
		// $scope.showGraphDiv = false;
		// $scope.reportExecute($scope.report,null);	

	}

	$scope.getSample = function (data) {
		ReportSerivce.getReportSample(data).then(function (response) { onSuccessGetSample(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetSample = function (response) {
			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false;
			var columns = [];
			var count = 0;
			angular.forEach(response[0], function (value, key) {
				count = count + 1;
			})
			angular.forEach(response[0], function (val, key) {
				var width;
				if (count > 3) {
					width = key.split('').length + 12 + "%"
				}
				else {
					width = (100 / count) + "%";
				}
				columns.push({ "name": key, "displayName": key.toLowerCase(), width: width, visible: true });
			});
			$scope.gridOptions.columnDefs = columns;
			$scope.originalData = response;
			$scope.gridOptions.data = response;
			// if ($scope.originalData.length > 0) {
			// 	//$scope.getResults($scope.originalData);

			// }
			$scope.spinner = false;
		}
		var onError = function (response) {
			$scope.isDataInpogress = true;
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg";
			$scope.datamessage = "Some Error Occurred";
			$scope.spinner = false;
		}
	}
	$scope.reportExecute = function (data, filterData) {
		$scope.spinner = true;
		ReportSerivce.reportExecute(data.uuid, data.version, filterData).then(function (response) { onSuccessReportExecute(response.data) }, function (response) { onError(response.data) })
		var onSuccessReportExecute = function (response) {
			$scope.reportExec = response;
			$scope.getSample(response);
		}
		var onError = function (response) {
			$scope.isDataInpogress = true;
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg";
			$scope.datamessage = "Some Error Occurred";
			$scope.spinner = false;
		}
	}

	$scope.showSampleTable = function (data) {
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.isDataError = false;
		$scope.tableclass = "centercontent";
		$scope.showForm = false;
		$scope.showGraphDiv = false;
		$scope.reportExecute(data, null);
		if ($scope.report.filterInfo.length > 0) {
			//$scope.getFilterValue($scope.report);
			// $('#attrFilter').modal({
			// 	backdrop: 'static',
			// 	keyboard: false
			// });
		}
		//else{
		// 	$scope.spinner = true;

		// }
	}

	$scope.refreshData = function () {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		//$scope.getResults(data)
	};




	$scope.selectType = function () {
		ReportSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.allSource = response;
			$scope.attributeTableArray = null;
			$scope.filterAttributeTags = null;
			$scope.addAttribute();
			ReportSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
				$scope.allattribute = response;
				$scope.getFormulaByType();
			}
		}
	}

	$scope.selectOption = function () {
		ReportSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
		var onSuccessGetDatapodByRelation = function (response) {
			$scope.sourcedatapodattribute = response;
			$scope.lhsdatapodattributefilter = response;
			$scope.allattribute = response;
			$scope.attributeTableArray = null;
			$scope.filterAttributeTags = null;
			$scope.addAttribute();
			$scope.getFormulaByType();
		}
	}
    $scope.getFormulaByType = function () {
		ReportSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
		var onSuccessFormula = function (response) {
			$scope.allSourceFormula = response;;
			$scope.allFilterormula = response;
			//$scope.allFilterormula.splice(0, 1);
			debugger
			if(response && response.length >0){
				$scope.allattribute=$scope.allattribute.concat(response);
			}

			// for (var i = 0; i < response.length; i++) {
			// 	var formulajson = {};
			// 	formulajson.index = $scope.sourcedatapodattribute.length;
			// 	formulajson.id = response[i].ref.uuid;
			// 	formulajson.uuid = response[i].ref.uuid;
			// 	formulajson.dname = "formula"+"."+response[i].ref.name
			// 	formulajson.name = response[i].ref.name
			// 	formulajson.type = "formula"
			// 	$scope.sourcedatapodattribute.push(formulajson)
			// }//End For
		}//End onSuccessGetFormulaByType
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		ReportSerivce.getAllVersionByUuid($stateParams.id, CF_META_TYPES.report).then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var reportVersion = {};
				reportVersion.version = response[i].version;
				$scope.reportVersion.versions[i] = reportVersion;
			}
		}


		ReportSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, CF_META_TYPES.report)
			.then(function (response) { onSuccessResult(response.data) },function (response) { onError(response.data)});
		var onSuccessResult = function (response) {
			$scope.isEditInprogess=false;
			$scope.report = response.report;
			$scope.selectSourceType = response.report.dependsOn.ref.type
			$scope.reposrtCompare = response.report;
			var defaultversion = {};
			defaultversion.version = response.report.version;
			defaultversion.uuid = response.report.uuid;
			$scope.reportVersion.defaultVersion = defaultversion;
			$scope.tags = response.tags
			$scope.getParamByApp();
			ReportSerivce.getAllLatest(response.report.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allSource = response;
				var defaultoption = {};
				defaultoption.type = $scope.report.dependsOn.ref.type
				defaultoption.uuid = $scope.report.dependsOn.ref.uuid
				$scope.allSource.defaultoption = defaultoption;
				$scope.getFormulaByType();
			}

			ReportSerivce.getExpressionByType($scope.report.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.allExpress = response
			}

			// ReportSerivce.getFormulaByType($scope.report.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			// var onSuccessFormula = function (response) {
			// 	$scope.allSourceFormula = response
			// 	$scope.allFilterormula = response;
			// 	$scope.allFilterormula.splice(0, 1);
			// }
			

			ReportSerivce.getAllAttributeBySource($scope.report.dependsOn.ref.uuid, $scope.report.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
				$scope.allattribute = response;
			}

			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) });
			var onSuccessFuntion = function (response) {
				$scope.allSourceFunction = response;
				$scope.allFunction = response;
			}

			$scope.attributeTableArray = response.sourceAttributes;
			$scope.filterAttributeTags = response.filterInfo;
			//  $scope.filterTableArray = response.filterInfo;
			//$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);

		}//End onSuccessResult
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End If
    else{
		$scope.report={};
		$scope.report.locked="N";
	}

	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.allSource = null;
		$scope.selectSourceType = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		ReportSerivce.getOneByUuidAndVersion($scope.reportVersion.defaultVersion.uuid, $scope.reportVersion.defaultVersion.version, CF_META_TYPES.report)
			.then(function (response) { onSuccessResult(response.data) },function (response) { onError(response.data)});
		var onSuccessResult = function (response) {
			$scope.isEditInprogess=false;
			$scope.report = response.report;
			$scope.selectSourceType = response.report.dependsOn.ref.type
			$scope.reposrtCompare = response.report;
			var defaultversion = {};
			defaultversion.version = response.report.version;
			defaultversion.uuid = response.report.uuid;
			$scope.reportVersion.defaultVersion = defaultversion;
			$scope.tags = response.tags
			$scope.getParamByApp();
			ReportSerivce.getAllLatest(response.report.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allSource = response;
				var defaultoption = {};
				defaultoption.type = $scope.report.dependsOn.ref.type
				defaultoption.uuid = $scope.report.dependsOn.ref.uuid
				$scope.allSource.defaultoption = defaultoption;
			}
			ReportSerivce.getExpressionByType($scope.report.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.allExpress = response
			}
			// ReportSerivce.getFormulaByType($scope.report.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			// var onSuccessFormula = function (response) {
			// 	$scope.allSourceFormula = response
			// 	$scope.allFilterormula = response;
			// }
			$scope.getFormulaByType();
			ReportSerivce.getAllAttributeBySource($scope.report.dependsOn.ref.uuid, $scope.report.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
				$scope.allattribute = response;
			}
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) }); var onSuccessFuntion = function (response) {
				$scope.allSourceFunction = response
				$scope.allFunction = response;
			}
			$scope.attributeTableArray = response.sourceAttributes;
			$scope.filterAttributeTags = response.filterInfo;
			//	$scope.filterTableArray = response.filterInfo;
			//	$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);

		}//End onSuccessResult
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};

	}/* End selectVersion*/

	// $scope.SearchAttribute = function (index, type, propertyType) {
	// 	$scope.selectAttr = $scope.filterTableArray[index][propertyType]
	// 	$scope.searchAttr = {};
	// 	$scope.searchAttr.type = type;
	// 	$scope.searchAttr.propertyType = propertyType;
	// 	$scope.searchAttr.index = index;
	// 	ReportSerivce.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
	// 	$scope.searchAttrIndex = index;
	// 	var onSuccessGetAllLatest = function (response) {
	// 		$scope.allSearchType = response;
	// 		var temp;
	// 		if ($scope.selectSourceType == "dataset") {
	// 			temp = $scope.allSearchType.options.filter(function (el) {
	// 				return el.uuid !== $scope.allSource.defaultoption.uuid;
	// 			});
	// 			$scope.allSearchType.options = temp;
	// 			$scope.allSearchType.defaultoption = temp[0]
	// 		}
	// 		if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
	// 			var defaultoption = {};
	// 			defaultoption.uuid = $scope.selectAttr.uuid;
	// 			defaultoption.name = "";
	// 			$scope.allSearchType.defaultoption = defaultoption;
	// 		}
	// 		$('#searchAttr').modal({
	// 			backdrop: 'static',
	// 			keyboard: false
	// 		});
	// 		ReportSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
	// 		var onSuccessAttributeBySource = function (response) {
	// 			$scope.allAttr = response;
	// 			if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
	// 				var defaultoption = {};
	// 				defaultoption.uuid = $scope.selectAttr.uuid;
	// 				defaultoption.name = "";
	// 				$scope.allSearchType.defaultoption = defaultoption;
	// 			} else {
	// 				$scope.selectAttr = $scope.allAttr[0]
	// 			}

	// 		}
	// 	}
	// }

	// $scope.onChangeSearchAttr = function () {
	// 	ReportSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
	// 	var onSuccessAttributeBySource = function (response) {
	// 		$scope.allAttr = response;
	// 	}
	// }

	// $scope.SubmitSearchAttr = function () {
	// 	$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
	// 	$('#searchAttr').modal('hide')
	// }

	// $scope.onChangeOperator = function (index) {
	// 	if ($scope.reposrtCompare != null) {
	// 		$scope.reposrtCompare.filterChg = "y"
	// 	}
	// 	if ($scope.filterTableArray[index].operator == 'BETWEEN') {
	// 		$scope.filterTableArray[index].rhstype = $scope.rhsType[1];
	// 		$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
	// 	} else if (['EXISTS', 'NOT EXISTS', 'IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
	// 		$scope.filterTableArray[index].rhstype = $scope.rhsType[4];
	// 		$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
	// 	} else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
	// 		$scope.filterTableArray[index].rhstype = $scope.rhsType[1];
	// 		$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
	// 	}
	// 	else {
	// 		$scope.filterTableArray[index].rhstype = $scope.rhsType[0];
	// 		$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
	// 	}
	// }

	/*$scope.checkAllFilterRow = function () {
		if (!$scope.selectedAllFitlerRow) {
			$scope.selectedAllFitlerRow = true;
		}
		else {
			$scope.selectedAllFitlerRow = false;
		}
		angular.forEach($scope.filterTableArray, function (filter) {
			filter.selected = $scope.selectedAllFitlerRow;
		});
	}

	$scope.addRowFilter = function () {
		if ($scope.filterTableArray == null) {
			$scope.filterTableArray = [];
		}
		var filertable = {};
		filertable.islhsDatapod = false;
		filertable.islhsFormula = false;
		filertable.islhsSimple = true;
		filertable.isrhsDatapod = false;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = true;
		filertable.lhsFilter = $scope.lhsdatapodattributefilter[0];
		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[0]
		filertable.rhstype = $scope.rhsType[0]
		filertable.rhsvalue;
		filertable.lhsvalue;
		$scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);
  }
  
	$scope.removeRowFitler = function () {
		var newDataList = [];
		$scope.checkAll = false;
		angular.forEach($scope.filterTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});

		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.filterTableArray = newDataList;
	}


	$scope.selectlhsType = function (type, index) {
		if ($scope.reposrtCompare != null) {
			$scope.reposrtCompare.filterChg = "y"
		}
		if (type == "string") {
			$scope.filterTableArray[index].islhsSimple = true;
			$scope.filterTableArray[index].islhsDatapod = false;
			$scope.filterTableArray[index].lhsvalue;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "datapod") {

			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = true;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.filterTableArray[index].islhsFormula = true;
			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = false;
			ReportSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				response.splice(0, 1);
				$scope.allFilterormula = response;
			}
		}
	}


	$scope.selectrhsType = function (type, index) {  
    if ($scope.reposrtCompare != null) {
			$scope.reposrtCompare.filterChg = "y"
    }
    
		if (type == "string") {
			$scope.filterTableArray[index].isrhsSimple = true;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].rhsvalue;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;

		}
		else if (type == "datapod") {

			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = true;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;

		}
		else if (type == "formula") {

			$scope.filterTableArray[index].isrhsFormula = true;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;

			ReportSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.allFilterormula = response;
				$scope.allFilterormula.splice(0, 1);
			}
		}
		else if (type == "function") {

			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = true;

			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
			var onSuccressGetFunction = function (response) {
				$scope.allFunction = response;
			}
		}
		else if (type == "dataset") {
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = true;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;

		}
		else if (type == "paramlist") {
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = true;
			$scope.filterTableArray[index].isrhsFunction = false;
			$scope.getParamByApp();
		}

	}*/


	$scope.getParamByApp = function () {
		CommonService.getParamByApp($rootScope.appUuidd || "", "application").
			then(function (response) { onSuccessGetParamByApp(response.data) });
		var onSuccessGetParamByApp = function (response) {
			$scope.allparamlistParams = [];
			if (response.length > 0) {
				var paramsArray = [];
				for (var i = 0; i < response.length; i++) {
					var paramjson = {}
					var paramsjson = {};
					paramsjson.uuid = response[i].ref.uuid;
					paramsjson.name = response[i].ref.name + "." + response[i].paramName;
					paramsjson.attributeId = response[i].paramId;
					paramsjson.attrType = response[i].paramType;
					paramsjson.paramName = response[i].paramName;
					paramsjson.caption = "app." + paramsjson.paramName
					paramsArray[i] = paramsjson
				}
				$scope.allparamlistParams = paramsArray;
			}
		}
	}



	// $scope.onChangeSimple = function () {
	// 	if ($scope.reposrtCompare != null) {
	// 		$scope.reposrtCompare.filterChg = "y"
	// 	}
	// }

	// $scope.onChangeAttribute = function () {
	// 	if ($scope.reposrtCompare != null) {
	// 		$scope.reposrtCompare.filterChg = "y"
	// 	}
	// }

	// $scope.onChangeFromula = function () {
	// 	if ($scope.reposrtCompare != null) {
	// 		$scope.reposrtCompare.filterChg = "y"
	// 	}
	// }

	// $scope.onChangeRhsParamList=function(){
	// 	if ($scope.reposrtCompare != null) {
	// 		$scope.reposrtCompare.filterChg = "y"
	// 	}
	// }
	$scope.loadAttribute = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	};

	$scope.clear = function () {
		$scope.filterAttributeTags = null;
	}

	$scope.checkAllAttributeRow = function () {
		angular.forEach($scope.attributeTableArray, function (attribute) {
			attribute.selected = $scope.selectAllAttributeRow;
		});
	}

	$scope.addAttribute = function () {
		if ($scope.attributeTableArray == null) {
			$scope.attributeTableArray = [];
		}
		var len = $scope.attributeTableArray.length + 1
		var attributeinfo = {};
		attributeinfo.name = "attribute" + len;
		attributeinfo.id = len - 1;
		attributeinfo.sourceAttributeType = $scope.sourceAttributeTypes[0];
		attributeinfo.isSourceAtributeSimple = true;
		attributeinfo.isSourceAtributeDatapod = false;
		$scope.attributeTableArray.splice($scope.attributeTableArray.length, 0, attributeinfo);
	}

	$scope.removeAttribute = function () {
		var newDataList = [];
		$scope.selectAllAttributeRow = false
		angular.forEach($scope.attributeTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.attributeTableArray = newDataList;
	}


	$scope.onChangeSourceAttribute = function (type, index) {
		if (type == "string") {
			$scope.attributeTableArray[index].isSourceAtributeSimple = true;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;

		}
		else if (type == "datapod") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = true;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
		}
		else if (type == "formula") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = true;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
			ReportSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.allSourceFormula = response

			}
		}
		else if (type == "expression") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = true;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
			ReportSerivce.getExpressionByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.allExpress = response
			}

		}
		else if (type == "function") {
			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = true;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
			var onSuccressGetFunction = function (response) {
				$scope.allSourceFunction = response
			}
		}
		else if (type == "paramlist") {
			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = true;
			$scope.getParamByApp();
		}


	}

	$scope.isDublication = function (arr, field, index, name) {
		var res = -1;
		for (var i = 0; i < arr.length - 1; i++) {
			if (arr[i][field] == arr[index][field] && i != index) {
				$scope.myform[name].$invalid = true;
				res = i;
				break
			} else {
				$scope.myform[name].$invalid = false;
			}
		}
		return res;
	}


	$scope.onChangeSourceName = function (index) {
		$scope.attributeTableArray[index].isSourceName = true;
		if ($scope.attributeTableArray[index].name) {
			var res = $scope.isDublication($scope.attributeTableArray, "name", index, "sourceName" + index);
			if (res != -1) {
				$scope.isDuplication = true;
			} else {
				$scope.isDuplication = false;
			}
		}

	}

	$scope.onChangeAttributeDatapod = function (data, index) {
		if (data != null && !$scope.attributeTableArray[index].isSourceName) {
			$scope.attributeTableArray[index].name = data.name
		}
		setTimeout(function () {
			if ($scope.attributeTableArray[index].name) {
				var res = $scope.isDublication($scope.attributeTableArray, "name", index, "sourceName" + index);
				if (res != -1) {
					$scope.isDuplication = true;
				} else {
					$scope.isDuplication = false;
				}
			}
		}, 1)
	}

	$scope.onChangeFormula = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.name;
		setTimeout(function () {
			if ($scope.attributeTableArray[index].name) {
				var res = $scope.isDublication($scope.attributeTableArray, "name", index, "sourceName" + index);
				if (res != -1) {
					$scope.isDuplication = true;
				} else {
					$scope.isDuplication = false;
				}
			}
		}, 1)
	}

	$scope.onChangeExpression = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.name;
		setTimeout(function () {
			if ($scope.attributeTableArray[index].name) {
				var res = $scope.isDublication($scope.attributeTableArray, "name", index, "sourceName" + index);
				if (res != -1) {
					$scope.isDuplication = true;
				} else {
					$scope.isDuplication = false;
				}
			}
		}, 1)
	}

	$scope.onChangeParamlist = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.paramName;
		setTimeout(function () {
			if ($scope.attributeTableArray[index].name) {
				var res = $scope.isDublication($scope.attributeTableArray, "name", index, "sourceName" + index);
				if (res != -1) {
					$scope.isDuplication = true;
				} else {
					$scope.isDuplication = false;
				}
			}
		}, 1)
	}



	$scope.submit = function () {
		var upd_tag = "N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;
		var reportJson = {}
		reportJson.uuid = $scope.report.uuid
		reportJson.name = $scope.report.name;
		reportJson.desc = $scope.report.desc
		reportJson.active = $scope.report.active;
		reportJson.locked = $scope.report.locked;
		reportJson.published = $scope.report.published;
		reportJson.title = $scope.report.title;
		reportJson.header = $scope.report.header;
		reportJson.footer = $scope.report.footer;
		reportJson.headerAlign = $scope.report.headerAlign;
		reportJson.footerAlign = $scope.report.footerAlign;
		
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
			}
		}

		reportJson.tags = tagArray;
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectSourceType
		ref.uuid = $scope.allSource.defaultoption.uuid
		dependsOn.ref = ref;
		reportJson.dependsOn = dependsOn;

		var filterInfoArray = [];
		if ($scope.filterAttributeTags != null) {
			for (var i = 0; i < $scope.filterAttributeTags.length; i++) {
				var filterInfo = {}
				var ref = {};
				ref.type = $scope.filterAttributeTags[i].type;
				ref.uuid = $scope.filterAttributeTags[i].uuid;
				filterInfo.ref = ref;
				if($scope.filterAttributeTags[i].type !="formula"){
					filterInfo.attrId = $scope.filterAttributeTags[i].attributeId;
				}
				filterInfoArray[i] = filterInfo;
			}
		}
		reportJson.filterInfo = filterInfoArray;
		var sourceAttributesArray = [];
		for (var l = 0; l < $scope.attributeTableArray.length; l++) {
			attributeinfo = {}
			attributeinfo.attrSourceId = l;
			attributeinfo.attrSourceName = $scope.attributeTableArray[l].name
			var ref = {};
			var sourceAttr = {};
			if ($scope.attributeTableArray[l].sourceAttributeType.text == "string") {
				ref.type = "simple";
				sourceAttr.ref = ref;
				sourceAttr.value = $scope.attributeTableArray[l].sourcesimple;
				attributeinfo.sourceAttr = sourceAttr;

			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "datapod") {

				ref.type = $scope.attributeTableArray[l].sourcedatapod.type
				ref.uuid = $scope.attributeTableArray[l].sourcedatapod.uuid;
				sourceAttr.ref = ref;
				sourceAttr.attrId = $scope.attributeTableArray[l].sourcedatapod.attributeId;
				sourceAttr.attrType = $scope.attributeTableArray[l].sourcedatapod.attrType;
				attributeinfo.sourceAttr = sourceAttr;
			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "paramlist") {

				ref.type = "paramlist";
				ref.uuid = $scope.attributeTableArray[l].sourceparamlist.uuid;
				sourceAttr.ref = ref;
				sourceAttr.attrId = $scope.attributeTableArray[l].sourceparamlist.attributeId;
				attributeinfo.sourceAttr = sourceAttr;
			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "expression") {

				ref.type = "expression";
				ref.uuid = $scope.attributeTableArray[l].sourceexpression.uuid;
				sourceAttr.ref = ref;
				attributeinfo.sourceAttr = sourceAttr;

			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "formula") {

				ref.type = "formula";
				ref.uuid = $scope.attributeTableArray[l].sourceformula.uuid;
				sourceAttr.ref = ref;
				attributeinfo.sourceAttr = sourceAttr;

			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "function") {

				ref.type = "function";
				ref.uuid = $scope.attributeTableArray[l].sourcefunction.uuid;
				sourceAttr.ref = ref;
				attributeinfo.sourceAttr = sourceAttr;

			}

			sourceAttributesArray[l] = attributeinfo

		}
		reportJson.attributeInfo = sourceAttributesArray
		console.log(JSON.stringify(reportJson))
		ReportSerivce.submit(reportJson, 'report', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
				notify.title = 'Success',
				notify.content = 'Report Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.close();
		}
		var onError = function (response) {
			notify.type = 'error',
				notify.title = 'Error',
				notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}

		return false;
	}

	$scope.close = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('reportlist'); }, 2000);

		}
	}


	$scope.submitDownload = function () {
		var uuid = $scope.download.data.uuid;
		var version = $scope.download.data.version;
		var url = $location.absUrl().split("app")[0];
		$('#downloadSample').modal("hide");
		$http({
			method: 'GET',
			url: url + "report/download?action=view&uuid=" + uuid + "&version=" + version + "&rows=" + $scope.download.rows,
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			headers = headers();
			var filename = headers['filename'];
			var contentType = headers['content-type'];

			var linkElement = document.createElement('a');
			try {
				var blob = new Blob([data], {
					type: contentType
				});
				var url = window.URL.createObjectURL(blob);

				linkElement.setAttribute('href', url);
				linkElement.setAttribute("download", filename);

				var clickEvent = new MouseEvent("click", {
					"view": window,
					"bubbles": true,
					"cancelable": false
				});
				linkElement.dispatchEvent(clickEvent);
			} catch (ex) {
				console.log(ex);
			}
		}).error(function (data) {
			console.log(data);
		});

	}
	$scope.downloadFile = function (data) {

		if ($scope.gridOptions.data.length > 0 && $scope.isShowSimpleData == true) {
			$scope.download.data = data;
			$('#downloadSample').modal({
				backdrop: 'static',
				keyboard: false
			});
		}

	};

});
