/**
**/
MetadataModule = angular.module('MetadataModule');
MetadataModule.controller('MetadataController', function ($location, $filter, dagMetaDataService, uiGridConstants, $state, $sessionStorage, $rootScope, $cookieStore, $stateParams, $scope, MetadataDatatableService, MetadataSerivce, CommonService, FileSaver, Blob) {

	console.log("metadatacontrollerjs" + $rootScope.baseUrl)
	$scope.select = $stateParams.type

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams
	});
	$scope.action = function (data, mode) {
		console.log(data);
		var uuid = data.uuid;
		var stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
		if (stateName)
			$state.go(stateName, {
				id: uuid,
				mode: mode == 'view' ? true : false
			});
	}
	$scope.selectData = function (data) {
		$scope.gridOptions.data = data.data;
		$scope.originalData = data.data;
	}
	$scope.gridOptions = {
		paginationPageSizes: null,
		columnDefs: [{
			displayName: 'datapodId',
			name: 'id',
			visible: false,
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			displayName: 'UUID',
			name: 'uuid',
			minWidth: 250,
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			displayName: 'Version',
			name: 'version',
			cellClass: 'text-center',
			headerCellClass: 'text-center',
			sort: {
				direction: uiGridConstants.DESC,
				// priority: 0,
			},
		},
		{
			displayName: 'Name',
			name: 'name',
			minWidth: 250,
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			displayName: 'Created By',
			name: 'createdBy.ref.name',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			displayName: 'Created On',
			name: 'createdOn',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			displayName: 'Action',
			name: 'action',
			cellClass: 'text-center',
			headerCellClass: 'text-center',
			maxWidth: 100,
			cellTemplate: [
				'<div class="ui-grid-cell-contents">',
				'  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
				'    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
				'    <i class="fa fa-angle-down"></i></button>',
				'    <ul uib-dropdown-menu>',
				'    <li><a ng-click="grid.appScope.action(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
				'    <li><a ng-click="grid.appScope.action(row.entity,\'edit\')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>',
				'    <li><a href="javascript:;"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>',
				'    <li><a ng-click="grid.appScope.createCopy(row.entity)"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>',
				'    <li><a ng-click="grid.appScope.getDetail(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
				'    </ul>',
				'  </div>',
				'</div>'
			].join('')
		}
		]
	};
	$scope.refreshData = function () {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
	};

	//MetadataDatatableService.setUuid($stateParams.type,$cookieStore.get('userdetail').sessionId).then(function(response){onSuccess(response.data)});
	CommonService.getBaseEntityByCriteria($stateParams.type, '', '', '', '', '', '').then(function (response) { onSuccess(response.data) });
	var onSuccess = function (response) {
		$scope.gridOptions.data = response;
		$scope.originalData = response;
	}

	$scope.getDetail = function (data) {
		$scope.selectuuid = data.split(",")[1]
		$('#matadatafilemodal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.okMetadataFile = function () {
		$('#matadatafilemodal').modal('hide');
		MetadataSerivce.getByUuid($scope.selectuuid, $stateParams.type).then(function (response) { onSuccessGetUuid(response.data) });
		var onSuccessGetUuid = function (response) {
			var jsonobj = angular.toJson(response, true);
			var data = new Blob([jsonobj], { type: 'application/json;charset=utf-8' });
			FileSaver.saveAs(data, response.name + '.json');
		}
	}

	$scope.createCopy = function (data) {
		var uuid = data.split(",")[1]
		var version = data.split(",")[2]
		$scope.clone = {};
		$scope.clone.uuid = uuid;
		$scope.clone.version = version;
		$('#clonemodal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}
	$scope.okClone = function () {
		$('#clonemodal').modal('hide');
		MetadataSerivce.saveAs($scope.clone.uuid, $scope.clone.version, $scope.select).then(function (response) { onSuccessSaveAs(response.data) });
		var onSuccessSaveAs = function (response) {
			MetadataDatatableService.setUuid($stateParams.type, $cookieStore.get('userdetail').sessionId).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.data = response;
				$scope.message = $scope.select + " Cloned Successfully"
				$('#showMsgModel').modal({
					backdrop: 'static',
					keyboard: false
				});
			}
		}
	}

	$scope.excutionDag = function (data) {
		var uuid = data.split(",")[1]
		var version = data.split(",")[2]
		if ($stateParams.type == "dag") {
			MetadataSerivce.getByUuid(uuid, "dag").then(function (response) { onSuccessGetUuid(response.data) });
			var onSuccessGetUuid = function (response) {
				MetadataSerivce.excutionDag(response).then(function (response) { onSuccessExecutionDag(response.data) });
				var onSuccessExecutionDag = function (response) {
					console.log("DagExec: " + JSON.stringify(response))
					$scope.executionmsg = "Pipeline Submited Successfully"
					$('#executionsubmit').modal({
						backdrop: 'static',
						keyboard: false
					});
				}
			}
		}//End If
		else if ($stateParams.type == "map") {
			MetadataSerivce.executeMap(uuid, version).then(function (response) { onSuccessExecutionMap(response.data) });
			var onSuccessExecutionMap = function (response) {
				$scope.executionmsg = "Map Submited Successfully"
				console.log("MapExec: " + JSON.stringify(response))
				$('#executionsubmit').modal({
					backdrop: 'static',
					keyboard: false
				});
			}// emd onSuccessExecutionMap
		} //End ElseIf
	}//End excutionDag

	$scope.getDetailForUpload = function (data) {
		var uuid = data.split(",")[1]
		var version = data.split(",")[2]
		$(":file").jfilestyle('clear')
		$("#csv_file").val("");
		$('#fileupload').modal({
			backdrop: 'static',
			keyboard: false
		});
	}

	$scope.uploadFile = function () {
		var file = $scope.myFile;
		var iEl = angular.element(document.querySelector('#csv_file'));
		var filename = iEl[0].files[0].name
		var fd = new FormData();
		fd.append('file', file)
		MetadataSerivce.getFile(filename, fd).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			MetadataSerivce.getRegisterFile(response).then(function (response) { onSuccessGetRegisterFile(response.data) });
			var onSuccessGetRegisterFile = function (response) {
				$('#fileupload').modal('hide');
				MetadataDatatableService.setUuid($stateParams.type, $cookieStore.get('userdetail').sessionId).then(function (response) { onSuccess(response.data) });
				var onSuccess = function (response) {
					$scope.data = response;
				}

				$scope.executionmsg = "CSV Uploaded Successfully"
				$('#executionsubmit').modal({
					backdrop: 'static',
					keyboard: false
				});
			}
		}
	}
});


/* Start MetadataDatapodController*/
MetadataModule.controller('MetadataDatapodController', function ($location, $http, $filter, dagMetaDataService, $state, $scope, $stateParams, $cookieStore, MetadataDatapodSerivce, $sessionStorage, privilegeSvc, $rootScope) {
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
	}
	else {
		$scope.isAdd = true;
	}
	$scope.isAttributeEnable = false;
	$scope.mode = ""
	$scope.dataLoading = false;
	$scope.datapoddata = null;
	$scope.attributetable = null;
	$scope.showFrom = true;
	$scope.isSubmitEnable = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.datapod = {};
	$scope.type = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
	$scope.SourceTypes = ["file", "hive", "impala", 'mysql', 'oracle']
	$scope.datapod.versions = [];
	$scope.datasetHasChanged = true;
	$scope.isShowSimpleData = false
	$scope.datapodsampledata = null;
	$scope.isDependencyShow = false;
	$scope.StateName = "metadata({'type':'datapod'})"
	$scope.isDependencyShow = false;
	$scope.isSimpleRecord = false;
	$scope.searchtext = '';
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['datapod'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['datapod'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};

	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
	}

	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	
	/*Start showPage*/
	$scope.showPage = function () {
		$scope.showFrom = true;
		$scope.isShowSimpleData = false
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.showGraph = function (uuid, version) {
		$scope.showFrom = false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = true;

	}/*End ShowGraph*/
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('metaListdatapod', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	
	$scope.showview = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('metaListdatapod', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}
	
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
	
	$scope.gridOptionsDatapod = {
		enableGridMenu: true,
		rowHeight: 40,
		enableRowSelection: true,
		enableSelectAll: true
	};
	
	$scope.canEdit = function () {
		if ($stateParams.mode == "true") {
			return false;
		}
		else {
			return true;
		}
	};
	
	$scope.gridOptionsDatapod.columnDefs = [
		{
			name: 'attributeId',
			enableCellEdit: false,
			visible: false,
			headerCellClass: 'text-center'
		},
		{
			name: 'key',
			width: '5%',
			enableCellEdit: false,
			displayName: 'Key',
			cellTemplate: '<input type="checkbox" ng-model="row.entity.key"  style="width: 16px;height:16px;"   ng-disabled="{{grid.appScope.mode}}"  ng-true-value="\'Y\'" ng-false-value="\'N\'"></input>',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			name: 'partition',
			width: '9%',
			enableCellEdit: false,
			displayName: 'Partition',
			cellTemplate: '<input type="checkbox" ng-model="row.entity.partition" style="width: 16px;height:16px;"  ng-disabled="{{grid.appScope.mode}}"  ng-true-value="\'Y\'" ng-false-value="\'N\'"></input>',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			name: 'active',
			width: '7%',
			enableCellEdit: false,
			displayName: 'Active',
			cellTemplate: '<input type="checkbox" ng-model="row.entity.active" style="width: 16px;height:16px;"  ng-disabled="{{grid.appScope.mode}}"  ng-true-value="\'Y\'" ng-false-value="\'N\'"></input>',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			name: 'dispName',
			displayName: 'Display Name',
			width: '19%',
			cellEditableCondition: $scope.canEdit,
			headerCellClass: 'text-center'
		},
		//enableCellEdit: false , displayName: 'Display Name',cellTemplate:'<input type="text" style="height: 40px;" ng-model="row.entity.dispName" title="{{row.entity.dispName}}" ng-disabled="{{grid.appScope.mode}}"class="form-control">'},
		{
			name: 'name',
			displayName: 'Name',
			width: '19%',
			cellEditableCondition: $scope.canEdit,
			headerCellClass: 'text-center'
		},
		//, displayName: 'Name', cellTemplate:'<input type="text" style="height: 40px;" ng-model="row.entity.name"  title="{{row.entity.name}}" ng-disabled="row.entity.isAttributeEnable ||{{grid.appScope.mode}}"class="form-control">'},
		{
			name: 'type',
			width: '18%',
			enableCellEdit: false,
			displayName: 'Type',
			cellTemplate: ' <select select2 style="margin:10px;" ng-model="row.entity.type" ng-options="x for x in grid.appScope.type"  ng-disabled="{{grid.appScope.mode}}" class="form-control"></select>',
			cellClass: 'customPadding',
			headerCellClass: 'text-center'
		},
		{
			name: 'desc',
			displayName: 'Desc',
			width: '19%',
			cellEditableCondition: $scope.canEdit,
			headerCellClass: 'text-center'
		}
		//enableCellEdit: false , displayName: 'Desc',cellTemplate:'<input type="text" style="height: 40px;" ng-model="row.entity.desc" title="{{row.entity.desc}}" ng-disabled="{{grid.appScope.mode}}" class="form-control">'}
	];


	$scope.gridOptions.columnDefs = [];
	$scope.filteredRows = [];
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};
	$scope.gridOptionsDatapod.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};
	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRows && $scope.filteredRows.length > 0) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 50 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams
	});


	if ($stateParams.mode == 'true') {
		if ($sessionStorage.fromStateName == "metadata") {
			$scope.StateName = "metadata({'type':'datapod'})"
		}
		else {
			$scope.StateName = $sessionStorage.fromStateName;
		}
	}

	

	$scope.datapodFormChange = function () {
		if ($scope.mode == "true") {
			$scope.datapodHasChanged = true;
		}
		else {
			$scope.datapodHasChanged = false;
		}
	}

	$scope.onChangeName = function (data) {
		$scope.datapodName = data;
	}



	$scope.showSampleTable = function (data) {
		$scope.isDataError = false;
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		MetadataDatapodSerivce.getDatapodSample(data).then(function (response) { onSuccessGetDatasourceByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			console.log(response)
			//$scope.datapodsampledata=response;

			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false

			for (var j = 0; j < data.attributes.length; j++) {
				var attribute = {};
				attribute.name = data.attributes[j].name;
				attribute.displayName = data.attributes[j].dispName;
				attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				$scope.gridOptions.columnDefs.push(attribute)
			}
			$scope.counter = 0;
			angular.forEach(response[0], function (value, key) {
				if (key != "rownum" && key != "version") {
					$scope.gridOptions.columnDefs[$scope.counter].name = key;
					console.log(key, value)
					$scope.counter++
				}
			});
			//$scope.gridOptions.data = response;
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.getResults($scope.originalData);
			}
			$scope.spinner = false;

		}
		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = true;
			$scope.spinner = false;
		}
	}

	$scope.selectPage = function (pageNo) {
		$scope.pagination.currentPage = pageNo;
	};
	$scope.onPerPageChange = function () {
		$scope.pagination.currentPage = 1;
		$scope.getResults($scope.originalData)
	}
	$scope.pageChanged = function () {
		$scope.getResults($scope.originalData)
	};
	$scope.getResults = function (params) {
		$scope.pagination.totalItems = params.length;
		if ($scope.pagination.totalItems > 0) {
			$scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize)) + 1);
		}
		else {
			$scope.pagination.to = 0;
		}
		if ($scope.pagination.totalItems < ($scope.pagination.pageSize * $scope.pagination.currentPage)) {
			$scope.pagination.from = $scope.pagination.totalItems;
		} else {
			$scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
		}
		var limit = ($scope.pagination.pageSize * $scope.pagination.currentPage);
		var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
		$scope.gridOptions.data = params.slice(offset, limit);
	}
	$scope.refreshData = function () {
		var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		$scope.getResults(data);
	};


	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		//var resultvalue=value.split(/[\_]?/);
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	$scope.selectType = function () {
		MetadataDatapodSerivce.getDatasourceByType($scope.selectSourceType.toUpperCase()).then(function (response) { onSuccessGetDatasourceByType(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			console.log(JSON.stringify(response))
			$scope.alldatasource = response
		}
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.isDependencyShow = true;
		$scope.isSimpleRecord = true;
		$scope.mode = $stateParams.mode;
		MetadataDatapodSerivce.getAllVersionByUuid($stateParams.id, 'datapod').then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datapodversion = {};
				datapodversion.version = response[i].version;
				$scope.datapod.versions[i] = datapodversion;
			}
		}
		MetadataDatapodSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'datapod').then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			var defaultversion = {};
			$scope.datapoddata = response.datapodata
			//console.log(JSON.stringify($scope.datapoddata))
			$scope.gridOptionsDatapod.data = response.attributes;
			/*if($sessionStorage.showgraph == true && $sessionStorage.fromStateName !="metadata"){
			$scope.showFromGraph($scope.datapoddata.uuid,$scope.datapoddata.version);
			delete $sessionStorage.showgraph
		}
		else{
		delete $sessionStorage.showgraph
	}*/
			$scope.attributetable = response.attributes
			defaultversion.version = response.datapodata.version;
			defaultversion.uuid = response.datapodata.uuid;
			$scope.datapod.defaultVersion = defaultversion;
			$scope.datapodName = $scope.convertUppdercase($scope.datapoddata.name)
			MetadataDatapodSerivce.getLatestDataSourceByUuid($scope.datapoddata.datasource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataDatapodSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.datapoddata.datasource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
		}
	} /*End If*/

	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		MetadataDatapodSerivce.getOneByUuidAndVersion($scope.datapod.defaultVersion.uuid, $scope.datapod.defaultVersion.version, 'datapod').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.datapoddata = response.datapodata
			$scope.gridOptionsDatapod.data = $scope.datapoddata.attributes;
			$scope.attributetable = response.attributes
			defaultversion.version = response.datapodata.version;
			defaultversion.uuid = response.datapodata.uuid;
			$scope.datapod.defaultVersion = defaultversion;
			$scope.datapodName = $scope.convertUppdercase($scope.datapoddata.name)
			MetadataDatapodSerivce.getLatestDataSourceByUuid($scope.datapoddata.datasource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataDatapodSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.datapoddata.datasource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.datapodata.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		}
	}/* End selectVersion*/

	$scope.okdatapodsave = function () {
		$('#datapodsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'datapod' }); }, 2000);
		}
	}

	/*Start SubmitDatapod*/
	$scope.submitDatapod = function () {
		var datapodJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datapodHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		datapodJson.uuid = $scope.datapoddata.uuid
		datapodJson.name = $scope.datapoddata.name
		datapodJson.desc = $scope.datapoddata.desc
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
		}
		datapodJson.tags = tagArray
		datapodJson.active = $scope.datapoddata.active;
		datapodJson.published = $scope.datapoddata.published;
		datapodJson.cache = $scope.datapoddata.cache;
		var datasource = {};
		var ref = {};
		ref.type = "datasource";
		ref.uuid = $scope.selectDataSource.uuid;
		datasource.ref = ref;
		datapodJson.datasource = datasource;
		var attributesarray = [];
		var count = 0;
		// for(var datapodattr=0;datapodattr<$scope.attributetable.length;datapodattr++){
		// 	var attributes={};
		// 	attributes.attributeId=datapodattr
		// 	attributes.name=$scope.attributetable[datapodattr].name;
		// 	attributes.type=$scope.attributetable[datapodattr].type;
		// 	attributes.desc=$scope.attributetable[datapodattr].desc;
		// 	attributes.dispName=$scope.attributetable[datapodattr].dispName;
		// 	attributes.active=$scope.attributetable[datapodattr].active;
		// 	if($scope.attributetable[datapodattr].key == "Y"){
		// 		attributes.key=count;
		// 		count=count+1;
		// 	}
		// 	else{
		// 		attributes.key=""
		// 	}
		// 	if($scope.attributetable[datapodattr].partition == "Y"){
		// 		attributes.partition=$scope.attributetable[datapodattr].partition;
		// 	}
		// 	else{
		// 		attributes.partition="N"
		// 	}
		// 	attributesarray[datapodattr]=attributes
		// }

		for (var datapodattr = 0; datapodattr < $scope.gridOptionsDatapod.data.length; datapodattr++) {

			var attributes = {};
			attributes.attributeId = datapodattr
			attributes.name = $scope.gridOptionsDatapod.data[datapodattr].name;
			attributes.type = $scope.gridOptionsDatapod.data[datapodattr].type;
			attributes.desc = $scope.gridOptionsDatapod.data[datapodattr].desc;
			attributes.dispName = $scope.gridOptionsDatapod.data[datapodattr].dispName;
			attributes.active = $scope.gridOptionsDatapod.data[datapodattr].active;
			if ($scope.gridOptionsDatapod.data[datapodattr].key == "Y") {
				attributes.key = count;
				count = count + 1;
			}
			else {
				attributes.key = ""
			}
			if ($scope.gridOptionsDatapod.data[datapodattr].partition == "Y") {
				attributes.partition = $scope.gridOptionsDatapod.data[datapodattr].partition;
			}
			else {
				attributes.partition = "N"
			}
			attributesarray[datapodattr] = attributes
		}
		datapodJson.attributes = attributesarray;
		console.log(JSON.stringify(datapodJson))
		MetadataDatapodSerivce.submit(datapodJson, 'datapod').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Datapod Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okdatapodsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitDatapod*/
	
	
	$scope.addRow = function () {
		// if($scope.attributetable == null){
		// 	$scope.attributetable =[];
		// }
		var attributejson = {}
		attributejson.attributeId = $scope.gridOptionsDatapod.data.length//$scope.attributetable.length;
		// $scope.attributetable.splice($scope.attributetable.length, 0,attributejson);

		$scope.gridOptionsDatapod.data.push(attributejson);
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.attributetable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}
	$scope.removeRow = function () {
		/*var len=$scope.attributetable.length
		$scope.attributetable.splice(len-1, 1);*/
		// var newDataList=[];
		// $scope.selectallattribute=false;
		// angular.forEach( $scope.attributetable, function(selected){
		// 	if(!selected.selected){
		// 		newDataList.push(selected);
		// 	}
		// });
		// $scope.attributetable = newDataList;
		angular.forEach($scope.gridApi.selection.getSelectedRows(), function (data, index) {
			$scope.gridOptionsDatapod.data.splice($scope.gridOptionsDatapod.data.lastIndexOf(data), 1);
		});
	}
	$scope.downloadFile = function (data) {
		var uuid = data.uuid;
		var version = data.version;
		var url = $location.absUrl().split("app")[0]
		$http({
			method: 'GET',
			url: url + "datapod/download?action=view&datapodUUID=" + uuid + "&datapodVersion=" + version + "&row=100",
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			headers = headers();

			var filename = headers['x-filename'];
			var contentType = headers['content-type'];

			var linkElement = document.createElement('a');
			try {
				var blob = new Blob([data], {
					type: contentType
				});
				var url = window.URL.createObjectURL(blob);

				linkElement.setAttribute('href', url);
				linkElement.setAttribute("download", uuid + ".xls");

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
	};

})/* End MetadataDatapodController*/



