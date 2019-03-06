/**
**/

MetadataModule = angular.module('MetadataModule');
/* Start MetadataDatapodController*/
MetadataModule.controller('MetadataDatapodController', function ($location,$window,$timeout,$http,$filter,$rootScope,$state,$sessionStorage, 
	 $scope, $stateParams,$cookieStore, uiGridConstants,dagMetaDataService,MetadataDatapodSerivce,privilegeSvc,commentService,
	 CommonService,CF_DOWNLOAD,CF_SAMPLE) {

	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.mode="true";
		$scope.isDragable = "false";
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}

	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.mode="false";
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen = true;
		$scope.isDragable = "true";
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else {
		$scope.isAdd = true;
		$scope.mode="false";
		$scope.isDragable = "true";

	}
	$scope.unitTypes=[{"text":"*","caption":"* Text"},{"text":"#","caption":"# Number"},{"text":"$","caption":"$ Currrency"},{"text":"%","caption":"% Percent"}];
	$scope.path = dagMetaDataService.compareMetaDataStatusDefs;
	$scope.download={};
	$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates=CF_DOWNLOAD.formate;
	$scope.download.selectFormate=CF_DOWNLOAD.formate[0];
	$scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to=CF_DOWNLOAD.limit_to;
	$scope.sample={};
	$scope.sample.maxrow=CF_SAMPLE.framework_sample_maxrows;
	$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;//CF_SAMPLE.framework_sample_minrows;
	$scope.sample.limit_to=CF_SAMPLE.limit_to;
	$scope.isAttributeEnable = false;
	
	$scope.dataLoading = false;
	$scope.datapoddata = null;
	$scope.attributetable = null;
	$scope.showFrom = true;
	$scope.isSubmitEnable = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.datapod = {};
	$scope.type = ["string", "float", "bigint", 'double', 'timestamp', 'integer', 'decimal','varchar',"long","vector"];
	$scope.SourceTypes = ["file", "hive", "impala", 'mysql', 'oracle', 'postgres']
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
	$scope.isResizeCompareMetadata=true;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['datapod'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
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
    $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	/*Start showPage*/
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = true;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
	}/*End showPage*/

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.isShowDatastore=false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = true;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;

	}/*End ShowGraph*/
    $scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListdatapod', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.datapoddata.locked =="Y"){
          return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListdatapod', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListdatapod', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}
	
	$scope.gridOptionsHitogram={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}

	$scope.filteredRowsHitogram = [];
	$scope.gridOptionsHitogram.onRegisterApi = function (gridApi) {
		$scope.gridOptionsHitogram = gridApi;
		$scope.filteredRowsHitogram = $scope.gridOptionsHitogram.core.getVisibleRows($scope.gridOptionsHitogram.grid);
	};
	
	$scope.getGridStyleHistogram = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsHitogram && $scope.filteredRowsHitogram.length > 0) {
			style['height'] = (($scope.filteredRowsHitogram.length < 10 ? $scope.filteredRowsHitogram.length * 50 : 400) + 70) + 'px';
		}
		else {
		
			style['height'] = "150px";
		}
		return style;
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

    $scope.gridOptionsCompareMetaData={
		enableGridMenu: true,
		rowHeight: 40,
		enableRowSelection: true,
		enableSelectAll: true,
		headerTemplate: 'views/header-template.html',
		superColDefs: [{
			name: 'sourceParant',
			displayName:'Source'
		}, {
			name: 'targetParant',
			displayName:'Datapod'
		},
		 {
			name: 'statusParant',
			displayName:'Status'
		}
		],
		columnDefs: [{
			name: 'sourceAttribute',
			displayName: 'Atrribute',
			superCol: 'sourceParant'
		}, {
			name: 'sourceType',
			displayName: 'Type',
			superCol: 'sourceParant'
		}, {
			name: 'sourceLength',
			displayName: 'Length',
			superCol: 'sourceParant'
  
		},
		{
			name: 'targetAttribute',
			displayName: 'Atrribute',
			superCol: 'targetParant'
		}, {
			name: 'targetType',
			displayName: 'Type',
			superCol: 'targetParant'
		},
		{
			name: 'targetLength',
			displayName: 'Length',
			superCol: 'targetParant'
  
		},
		{
			name: 'status',
			displayName: '',
			superCol: 'statusParant',
			enableColumnMenu: false,
			cellClass: 'text-center',
			cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: -2px auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{grid.appScope.path[row.entity.status].caption}}</div></div>'
		}],
	};  


	$scope.filteredRowsCompareMetaData = [];
	$scope.gridOptionsCompareMetaData.onRegisterApi = function (gridApi) {
		$scope.gridApiCompareMetaData = gridApi;
		$scope.filteredRowsCompareMetaData = $scope.gridApiCompareMetaData.core.getVisibleRows($scope.gridApiCompareMetaData.grid);
	};
	$window.addEventListener('resize', function(e) {
		$scope.isResizeCompareMetadata=false
		$timeout(function() {
		   $scope.isResizeCompareMetadata=true;
		  },10);
	  });
	  
	$scope.getGridStyleCompareMetaData = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsCompareMetaData && $scope.filteredRowsCompareMetaData.length > 0) {
			style['height'] = (($scope.filteredRowsCompareMetaData.length < 10 ? $scope.filteredRowsCompareMetaData.length * 50 : 400) + 70) + 'px';
		}
		else {
		
			style['height'] = "150px";
		}
		return style;
	}
	$scope.gridOptionsDataStrore={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	};
    $scope.gridOptionsDataStrore.columnDefs = [
		{
			name: 'uuid',
			width: '30%',
			enableCellEdit: false,
			visible: false,
			displayName: 'Uuid',
			headerCellClass: 'text-center'
		},
		{
			name: 'name',
			minWidth: 220,
			displayName: 'Name',
			headerCellClass: 'text-center'
		},
		{
			name: 'version',
			maxWidth:110,
			cellClass: 'text-center',
			visible: true,
			displayName: 'Version',
			headerCellClass: 'text-center',
			sort: {
				direction: uiGridConstants.DESC,
			   // priority: 0,
			  },
		},
		{
			displayName: 'Created By',
			name: 'createdBy.ref.name',
			cellClass: 'text-center',
			maxWidth:100,
			headerCellClass: 'text-center'
		  },
		  {
			displayName: 'Created On',
			name: 'createdOn',
			minWidth: 220,
			cellClass: 'text-center',
			headerCellClass: 'text-center',
	
		  },
		
		{
			name: 'numRows',
			width: '10%',
			enableCellEdit: false,
			displayName: 'Rows',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			name: 'action',
			displayName: 'Action',
			width: '10%',
			cellTemplate: '<input type="radio" name="action" style="margin:11px;width:16px;height:16px;" ng-click="grid.appScope.onSelectDataStore(row.entity,$index)"  ng-disabled="row.entity.isSelectedDatastore"></input>',
			cellEditableCondition:false,
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		}
		
	];

    $scope.filteredRowsDatastore = [];
	$scope.gridOptionsDataStrore.onRegisterApi = function (gridApi) {
		$scope.gridApiDatastore = gridApi;
		$scope.filteredRowsDatastore = $scope.gridApiDatastore.core.getVisibleRows($scope.gridApiDatastore.grid);
	};

	$scope.getGridStyleDatastore = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRowsDatastore && $scope.filteredRowsDatastore.length > 0) {
			style['height'] = (($scope.filteredRowsDatastore.length < 10 ? $scope.filteredRowsDatastore.length * 50 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}
	$scope.gridOptions.columnDefs = [];
	$scope.gridOptionsDatapod.columnDefs = [
		{
			name: 'attributeId',
			displayName: 'Id',
			enableCellEdit: false,
			visible: true,
			width: '5%',
			headerCellClass: 'text-center',
			cellClass: 'text-center'
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
			width: '18%',
			cellEditableCondition: $scope.canEdit,
			headerCellClass: 'text-center'
		},
		//enableCellEdit: false , displayName: 'Display Name',cellTemplate:'<input type="text" style="height: 40px;" ng-model="row.entity.dispName" title="{{row.entity.dispName}}" ng-disabled="{{grid.appScope.mode}}"class="form-control">'},
		{
			name: 'name',
			displayName: 'Name',
			width: '14%',
			cellEditableCondition: $scope.canEdit,
			headerCellClass: 'text-center'
		},
		//, displayName: 'Name', cellTemplate:'<input type="text" style="height: 40px;" ng-model="row.entity.name"  title="{{row.entity.name}}" ng-disabled="row.entity.isAttributeEnable ||{{grid.appScope.mode}}"class="form-control">'},
		{
			name: 'type',
			width: '13%',
			enableCellEdit: false,
			displayName: 'Type',
			cellTemplate: ' <select select2 style="margin:10px;" ng-model="row.entity.type" ng-options="x for x in grid.appScope.type"  ng-disabled="{{grid.appScope.mode}}" class="form-control"></select>',
			cellClass: 'customPadding',
			headerCellClass: 'text-center'
		},
		{
			name: 'length',
			displayName: 'Length',
			width: '10%',
			cellTemplate: ' <input type="number" min="1" style="margin: 7px 0px 0px 3px;height: 72%;width: 90%;" ng-model="row.entity.length" ng-disabled="{{grid.appScope.mode}}"></input>',
			headerCellClass: 'text-center'
		},
		{
			name: 'desc',
			displayName: 'Desc',
			width: '17%',
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

	$scope.canEdit = function () {
		if ($stateParams.mode == "true") {
			return false;
		}
		else {
			return true;
		}
	};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
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
	

	$scope.refreshDataDatastore=function(){
		$scope.gridOptionsDataStrore.data= $filter('filter')($scope.originalDataDatastore, $scope.searchtextDatastore, undefined);
	}
	
	
	$scope.isDisabledDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
			
		}
	}
	$scope.isEnableDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=false;
			if(data !=null && (data.uuid == $scope.gridOptionsDataStrore.data[i].uuid)){
				$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
				 
			}
		}
	}

	$scope.onSelectDataStore=function(data,index){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDisabledDSRB();
		$scope.sample.data=null;
		$scope.datastoreDetail=data
		$scope.sample.data=data;
		//$scope.sample.type="datastore";
		$scope.getResultByDatastore(data);
	}

	$scope.showDatastrores=function(data){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = false;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isDownloadDatapod=true;
		$scope.gridOptionsDataStrore.data=[];
		MetadataDatapodSerivce.getDatastoreByDatapod(data,"datapod").then(function (response) { onSuccessGetDatastoreByDatapode(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatastoreByDatapode = function (response) {
			$scope.isShowDatastore=true;
			$scope.originalDataDatastore=response;
			$scope.gridOptionsDataStrore.data=response;
			console.log(response)
		}
	
	}
	
	$scope.getResultByDatastore = function (data) {
		//$('#SampleModel').modal("hide");
		$scope.isDataError = false;
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		$scope.isDatastoreResult=true;
		MetadataDatapodSerivce.getResultByDatastore(data.uuid,data.version,$scope.sample.rows).then(function (response) { onSuccessGetResultByDatastore(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetResultByDatastore = function (response) {
			$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;//CF_SAMPLE.framework_sample_minrows;
			$scope.isEnableDSRB(data);
		    $scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			var columnDefs=[];
			for (var j = 0; j <$scope.datapoddata.attributes.length; j++) {
				var attribute = {};
				attribute.name = $scope.datapoddata.attributes[j].name;
				attribute.displayName = $scope.datapoddata.attributes[j].dispName;
				attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				columnDefs.push(attribute)
			}
			setTimeout(function(){
				$scope.gridOptions.columnDefs=columnDefs;
			},10);
			$scope.counter = 0;
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
			}
			$scope.spinner = false;

		}

		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = true;
			$scope.spinner = false;
			$scope.isEnableDSRB(data);
		}
	}
	function ConvertTwoDisit(data, propName) {
		// if(isNaN(data[0][propName])){
		 if (data.length > 0 &&  data[0][propName].indexOf(" - ") != -1) {
		   for (var i = 0; i < data.length; i++) {
			 a = data[i][propName].split(' - ')[0];
			 b = data[i][propName].split('-')[1]
			 data[i][propName] = parseFloat(a).toFixed(2) + " - " + parseFloat(b).toFixed(2);
			 // console.log(data[i][propName])
		   }
		 }
		// }
		// console.log(data)
		 return data;
	}
	
	$scope.refreshDataHistogram=function(str){
       console.log(str)
		var data = $filter('filter')($scope.originalDataHistogram, str, undefined);
		$scope.gridOptionsHitogram.data= data;//$scope.getResults(data);
	}

	$scope.onClickChart=function(){
		$scope.isShowDataGrid=false;
		$scope.isShowChart=true;
	}

	$scope.onClickGrid=function(){
		$scope.searchTextHostogram;
		$scope.isShowDataGrid=true;
		$scope.isShowChart=false;
		$scope.gridOptionsHitogram.columnDefs=[];
		$scope.gridOptionsHitogram.columnDefs=[
			{
				name: 'bucket',
				displayName: 'Bucket',
				cellClass: 'text-center',
				headerCellClass: 'text-center'
			},
			{
				name: 'frequency',
				displayName: 'Frequency',
				cellClass: 'text-center',
				headerCellClass: 'text-center'
		    }
		]
		$scope.gridOptionsHitogram.data=$scope.originalDataHistogram//$scope.getResults($scope.originalDataHistogram);
	}

	$scope.calculateHistrogram=function(row){
		$scope.histogramDetail=row.colDef;
		$scope.isShowDataGrid=false;
		$scope.isShowChart=false;
		console.log(row);
		$('#histogamModel').modal({
			backdrop: 'static',
			keyboard: false
		});
		$scope.datacol=null;
		$scope.isHistogramInprogess=true;
		$scope.isHistogramError=false;
		MetadataDatapodSerivce.getAttrHistogram(row.colDef.uuid,row.colDef.version,'datapod',row.colDef.attributeId).then(function (response) { onSuccessGetAttrHistogram(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetAttrHistogram = function (response) {
			console.log(response);
			$scope.isShowDataGrid=false;
			$scope.isShowChart=true;
			if(row.colDef.attrType !="string"){
				ConvertTwoDisit(response, 'bucket');
			}
			$scope.isHistogramInprogess=false;
			$scope.isHistogramError=false;
			$scope.datacol={};
			if(response.length >=20){
				$scope.datacol.datapoints=response.slice(0,20);

			}else{
				$scope.datacol.datapoints=response;
			}
			$scope.originalDataHistogram=response;
			var dataColumn={}
			dataColumn.id="frequency";
			dataColumn.name="frequency"
			dataColumn.type="bar"
			dataColumn.color="#D8A2DE";
			$scope.datacol.datacolumns=[];
			$scope.datacol.datacolumns[0]=dataColumn;
			var datax={};
			datax.id ="bucket";
			$scope.datacol.datax=datax;
			
		}
		var onError =function(){
			$scope.isHistogramInprogess=false;
			$scope.isHistogramError=true;
		}
	}

	$scope.showSampleTable = function (data) {
		if($scope.isDataInpogress){
			return false;
		};
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDataError = false;
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		$scope.isShowDatastore=false;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isDownloadDatapod=false;
	//	$('#SampleModel').modal("hide");
		MetadataDatapodSerivce.getDatapodSample(data,$scope.sample.rows).then(function (response) { onSuccessGetDatasourceByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows//CF_SAMPLE.framework_sample_minrows;	
			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			console.log(data.attributes)
			for (var j = 0; j <data.attributes.length; j++) {
				var attribute = {};
				attribute.name =data.attributes[j].name;
				attribute.dname =data.name;
				attribute.uuid=data.uuid;
				attribute.version=data.version;
				attribute.attributeId=data.attributes[j].attributeId;
                attribute.headerCellTemplate='views/datapod-sample-header-template.html'
				attribute.displayName =data.attributes[j].dispName;
				attribute.attrType =data.attributes[j].type;
				attribute.width = attribute.displayName.split('').length + 5 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				$scope.gridOptions.columnDefs.push(attribute)
			}
			$scope.counter = 0;
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
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

	
	$scope.ondrop = function(e) {
		console.log(e);
		$scope.myform.$dirty=true;
	}
	$scope.selectPage = function (pageNo) {
		$scope.pagination.currentPage = pageNo;
	};

	$scope.onPerPageChange = function () {
		$scope.pagination.currentPage = 1;
		$scope.gridOptions.data=$scope.getResults($scope.originalData)
	}

	$scope.pageChanged = function () {
		$scope.gridOptions.data=$scope.getResults($scope.originalData)
	};
	$scope.pageChangedHistogram=function(){
		$scope.gridOptionsHitogram.data=$scope.getResults($scope.originalDataHistogram);
	}

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
	//	$scope.gridOptions.data = params.slice(offset, limit);
		return params.slice(offset, limit);
		console.log($scope.gridOptions.data)
	}

	$scope.refreshData = function () {
		var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		$scope.gridOptions.data=$scope.getResults(data);
	};
	$scope.refreshCompareMetaData = function (searchtext) {
		$scope.gridOptionsCompareMetaData.data = $filter('filter')($scope.originalCompareMetaData,searchtext, undefined);
		
	};

    $scope.showCompareMetaData=function(data){
		if($scope.isShowCompareMetaData){
			return false
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.gridOptionsCompareMetaData.superColDefs[0].displayName=$scope.selectSourceType.charAt(0).toUpperCase() + $scope.selectSourceType.slice(1);;
		$scope.isShowCompareMetaData=true;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.gridOptionsCompareMetaData.isDataError=false;
		$scope.gridOptionsCompareMetaData.isDataInpogress=true;
		$scope.gridOptionsCompareMetaData.tableclass = "centercontent";
		MetadataDatapodSerivce.compareMetadata(data.uuid,data.version,'datapod').then(function (response) { onSuccessCompareMetadata(response.data) }, function (response) { onError(response.data) })
		var onSuccessCompareMetadata = function (response) {
			$scope.gridOptions.columnDefs.data = [];
			$scope.gridOptionsCompareMetaData.data=response;
			var count=0;
			for(var i=0;i<response.length;i++){
				if(response[i].status == "NOCHANGE"){
					count=count+1;
				}
			}
			if(response.length == count){
				$scope.isMetaSysn=true;
			}else{
				$scope.isMetaSysn=false;
			}

			$scope.originalCompareMetaData=response;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
			$scope.gridOptionsCompareMetaData.tableclass = "";
		}

		var onError = function (response) {
			$scope.gridOptionsCompareMetaData.isDataError=true;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
		}
	}
	
	$scope.synchronousMetadata=function(data){
		if($scope.isMetaSysn || $scope.selectSourceType=='file'){
			return false
		}

		$scope.isShowCompareMetaData=true;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.gridOptionsCompareMetaData.isDataError=false;
		$scope.gridOptionsCompareMetaData.isDataInpogress=true;
		$scope.gridOptionsCompareMetaData.tableclass = "centercontent";
		MetadataDatapodSerivce.synchronizeMetadata(data.uuid,data.version,'datapod').then(function (response) { onSuccessSynchronizeMetadata(response.data) }, function (response) { onError(response.data) })
		var onSuccessSynchronizeMetadata = function (response) {
			$scope.datapoddata=response;
			$scope.showCompareMetaData($scope.datapoddata)
		}
		var onError = function (response) {
			$scope.gridOptionsCompareMetaData.isDataError=true;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
		}
	}
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
			$scope.alldatasource = response
		}
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.isDependencyShow = true;
		$scope.isSimpleRecord = true;
		$scope.mode = $stateParams.mode;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatapodSerivce.getAllVersionByUuid($stateParams.id, 'datapod').then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datapodversion = {};
				datapodversion.version = response[i].version;
				$scope.datapod.versions[i] = datapodversion;
			}
		}
		MetadataDatapodSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'datapod')
			.then(function (response) { onSuccessGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			$scope.datapoddata = response.datapodata
			var tags = [];
			if (response.datapodata.tags != null) {
				for (var i = 0; i < response.datapodata.tags.length; i++) {
					var tag = {};
					tag.text = response.datapodata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			$scope.gridOptionsDatapod.data = response.attributes;	
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
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	} /*End If*/
	else{
		$scope.datapoddata={};
		$scope.datapoddata.locked="N";
	}


	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatapodSerivce.getOneByUuidAndVersion($scope.datapod.defaultVersion.uuid, $scope.datapod.defaultVersion.version, 'datapod')
			.then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.isEditInprogess=false;
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
			if (response.datapodata.tags != null) {
				for (var i = 0; i < response.datapodata.tags.length; i++) {
					var tag = {};
					tag.text = response.datapodata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
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
		var upd_tag = "N"
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
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
			}
		}

		datapodJson.tags = tagArray
		datapodJson.active = $scope.datapoddata.active;
		datapodJson.locked = $scope.datapoddata.locked;
		datapodJson.published = $scope.datapoddata.published;
		datapodJson.cache = $scope.datapoddata.cache;
		datapodJson.publicFlag = $scope.datapoddata.publicFlag;

		var datasource = {};
		var ref = {};
		ref.type = "datasource";
		ref.uuid = $scope.selectDataSource.uuid;
		datasource.ref = ref;
		datapodJson.datasource = datasource;
		var attributesarray = [];
		var count = 0;
		// for (var datapodattr = 0; datapodattr < $scope.gridOptionsDatapod.data.length; datapodattr++) {
			for (var datapodattr = 0; datapodattr < $scope.attributetable.length; datapodattr++) {
			var attributes = {};
			/*attributes.attributeId = datapodattr
			attributes.name = $scope.gridOptionsDatapod.data[datapodattr].name;
			attributes.type = $scope.gridOptionsDatapod.data[datapodattr].type;
			attributes.desc = $scope.gridOptionsDatapod.data[datapodattr].desc;
			attributes.dispName = $scope.gridOptionsDatapod.data[datapodattr].dispName;
			attributes.active = $scope.gridOptionsDatapod.data[datapodattr].active;
			attributes.length = $scope.gridOptionsDatapod.data[datapodattr].length;
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
			}*/
            
			attributes.attributeId = $scope.attributetable[datapodattr].attributeId
			attributes.displaySeq=datapodattr;
			attributes.name = $scope.attributetable[datapodattr].name;
			attributes.type = $scope.attributetable[datapodattr].type;
			attributes.desc = $scope.attributetable[datapodattr].desc;
			attributes.dispName = $scope.attributetable[datapodattr].dispName;
			attributes.active = $scope.attributetable[datapodattr].active;
			attributes.length = $scope.attributetable[datapodattr].length;
			attributes.attrUnitType = $scope.attributetable[datapodattr].attrUnitType;
			if ($scope.attributetable[datapodattr].key == "Y") {
				attributes.key = count;
				count = count + 1;
			}
			else {
				attributes.key = ""
			}
			if ($scope.attributetable[datapodattr].partition == "Y") {
				attributes.partition = $scope.attributetable[datapodattr].partition;
			}
			else {
				attributes.partition = "N"
			}
			attributesarray[datapodattr] = attributes
		}
		datapodJson.attributes = attributesarray;
		console.log(JSON.stringify(datapodJson))
		MetadataDatapodSerivce.submit(datapodJson, 'datapod', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
		if($scope.attributetable ==null){
			$scope.attributetable=[];
		}
		var attributejson = {}
		attributejson.attributeId = $scope.attributetable.length//$scope.attributetable.length;
		//$scope.gridOptionsDatapod.data.push(attributejson);
		$scope.attributetable.push(attributejson)
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.attributetable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}
	$scope.removeRow = function () {
		$scope.iSSubmitEnable = true;
		// angular.forEach($scope.gridApi.selection.getSelectedRows(), function (data, index) {
		// 	$scope.gridOptionsDatapod.data.splice($scope.gridOptionsDatapod.data.lastIndexOf(data), 1);
		// });
		var newDataList = [];
		$scope.selectallattribute = false;
		angular.forEach($scope.attributetable, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.attrTableSelectedItem=[];
		});
		$scope.attributetable = newDataList;
	}

	$scope.onAttrRowDown=function(index){
		var rowTempIndex=$scope.attributetable[index];
        var rowTempIndexPlus=$scope.attributetable[index+1];
		$scope.attributetable[index]=rowTempIndexPlus;
		$scope.attributetable[index+1]=rowTempIndex;

	}
	
	$scope.onAttrRowUp=function(index){
		var rowTempIndex=$scope.attributetable[index];
        var rowTempIndexMines=$scope.attributetable[index-1];
		$scope.attributetable[index]=rowTempIndexMines;
		$scope.attributetable[index-1]=rowTempIndex;
	}
	$scope.downloadFileByDatastore = function (data) {
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="datastore";
		$('#downloadSample').modal({
			backdrop: 'static',
			keyboard: false
		});
	};

	$scope.downloadFile = function (data) {
		if($scope.isDownloadDatapod)
		  return false;
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="datapod";
		$('#downloadSample').modal({
			backdrop: 'static',
			keyboard: false
		});
	};

    $scope.submitDownload =function(){
		$scope.isDownloadDatapod=true;
		$('#downloadSample').modal("hide");
		var url = $location.absUrl().split("app")[0]
		$http({
			method: 'GET',
			url: url+$scope.download.type+"/download?action=view&uuid="+$scope.download.uuid+"&version="+$scope.download.version + "&rows="+$scope.download.rows+"&format="+$scope.download.selectFormate,
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
			$scope.isDownloadDatapod=false;
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
				linkElement.setAttribute("download",filename);

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
			$scope.isDownloadDatapod=false;
			console.log(data);
			$('#downloadSample').modal("hide");
		});
	}

	$scope.attrTableSelectedItem=[];
	$scope.onChangeAttrRow=function(index,status){
		if(status ==true){
			$scope.attrTableSelectedItem.push(index);
		}
		else{
			let tempIndex=$scope.attrTableSelectedItem.indexOf(index);

			if(tempIndex !=-1){
				$scope.attrTableSelectedItem.splice(tempIndex, 1);

			}
		}	
	}
	$scope.autoMove=function(index){
		var tempAtrr=$scope.attributetable[$scope.attrTableSelectedItem[0]];
		$scope.attributetable.splice($scope.attrTableSelectedItem[0],1);
		$scope.attributetable.splice(index,0,tempAtrr);
		$scope.attrTableSelectedItem=[];
		$scope.attributetable[index].selected=false;
	
	}

	$scope.autoMoveTo=function(index){
		if(index <= $scope.attributetable.length){
			$scope.autoMove(index-1,'mapAttr');
			$scope.moveTo=null;
			$(".actions").removeClass("open");
		}
	}
})/* End MetadataDatapodController*/



