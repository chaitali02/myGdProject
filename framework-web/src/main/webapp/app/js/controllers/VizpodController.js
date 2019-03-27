
DatavisualizationModule = angular.module('DatavisualizationModule');

/*code for vizpod */
DatavisualizationModule.controller('MetadataVizpodController', function ($filter, $timeout, $state, $rootScope, $scope, $stateParams, $sessionStorage, $q, $location, $http, VizpodSerivce, privilegeSvc, CommonService,CF_DOWNLOAD,dagMetaDataService,CF_SAMPLE,
	COLORPALETTE) {


	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
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
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen = true;
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
	}
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.popoverIsOpen=false;
	$scope.tags = null;
	$scope.mode = "false"
	$scope.vizpoddata;
	$scope.showvizpod = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showgraphdiv = false
	$scope.vizpod = {};
	$scope.vizpodTypes = ["bar-chart", "pie-chart", "line-chart", "donut-chart", "area-chart", "bubble-chart", "world-map", "usa-map", "data-grid", 'network-graph','bar-line-chart','heat-map','score-card']
	$scope.VizpodSourceTypes = ['datapod','dataset','relation'];
	$scope.colorPalette=["Palette 1","Palette 2","Palette 3", "Random"]
	$scope.sortOrders=["ASC","DESC"];
	$scope.vizpod.versions = [];
	$scope.isshowmodel = false;
	$scope.isShowSimpleData=false;
	$scope.keylist;
	$scope.isDependencyShow = false;
	$scope.download={};
	$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates=CF_DOWNLOAD.formate;
	$scope.download.selectFormate=CF_DOWNLOAD.formate[0];
	$scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to=CF_DOWNLOAD.limit_to;
	$scope.sample={};
	$scope.sample.maxrow=CF_SAMPLE.framework_sample_maxrows;
	$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;//CF_SAMPLE.framework_sample_minrows;

	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
	}
    $scope.gridOptions = {
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: false,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}
	$scope.gridOptions.columnDefs = [];
	$scope.filteredRows = [];
	
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

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
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 50 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['vizpod'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['vizpod'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			//console.log(response)
			$scope.lobTag = response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
	$scope.getLovByType();
    $scope.popOverSetFalse=function(){
		for(var i=0;i< $scope.valuelist.length;i++){
			$scope.valuelist[i].popoverIsOpen=false;
		}
	}

	$scope.onClickPopoverOpen=function(index){
		if($scope.valuelist && $scope.valuelist.length >0){
			$scope.popOverSetFalse();
            if($scope.valuelist[index].type !="formula" && $scope.mode =="false"){
				if(!$scope.valuelist[index].popoverIsOpen){
					$scope.valuelist[index].popoverIsOpen=false;
				}
				$scope.valuelist[index].popoverIsOpen=!$scope.valuelist[index].popoverIsOpen;
		    }
			else{
				$scope.valuelist[index].popoverIsOpen=false;
			}
	    }
	}

	$scope.onChangeColorPalette=function(colorPalette){
		if(colorPalette !=null){
			var str=colorPalette.replace(" ", "_");
			$scope.cPCodes=COLORPALETTE[str];
	    }
	}

	$scope.applyFunctionOnValue=function(index,fun){
		$scope.myform.$dirty = true;
		if(fun !="NONE"){
			$scope.valuelist[index].dname=fun+"("+$scope.valuelist[index].name+"."+$scope.valuelist[index].attributeName+")";
			$scope.valuelist[index].function=fun;
			$scope.valuelist[index].popoverIsOpen=false;
		}else{
			$scope.valuelist[index].dname=$scope.valuelist[index].name+"."+$scope.valuelist[index].attributeName;
			$scope.valuelist[index].popoverIsOpen=false;
			$scope.valuelist[index].function=null;

		}
	}

	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showVizpodPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showvizpod = true;
		$scope.showgraph = false
		$scope.showgraphdiv = false;
		$scope.isShowSimpleData =false;

	}//End showVizpodPage
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showVizpodPage()
		$state.go('dvvizpod', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.vizpoddata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showVizpodPage()
		$state.go('dvvizpod', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showVizpodPage()
		$state.go('dvvizpod', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}

	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};
	$scope.orderByValue = function (value) {
		return value;
	};

	$scope.indexOfByMultiplaValue = function (array, data) {
		var result = -1;
		for (var i = 0; i < array.length; i++) {
			if (array[i].uuid == data.uuid && array[i].attributeId == data.attributeId) {
				result = 0;
				break
			}
			else {
				result = -1
			}

		}//End For
		return result;
	}//End indexOfdata

	$scope.indexOfBySingleValue = function (array, data) {
		var result = -1;
		for (var i = 0; i < array.length; i++) {
			if (array[i].uuid == data.uuid) {
				result = 0;
				break
			}
			else {
				result = -1
			}

		}//End For
		return result;
	}//End indexOfdata

	$scope.beforeDropKey = function (event, ui, data) {
		var deferred = $q.defer();
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var isEnable = ($scope.mode == 'true');
		if($scope.vizpodtype !="score-card"){
			if (type == "formula") {
				if ($scope.indexOfBySingleValue($scope.keylist, data) == -1 && isEnable == false) {
					deferred.resolve();
				}
				else {
					deferred.reject();
				}
			}
			else {
				if ($scope.indexOfByMultiplaValue($scope.grouplist, data) == -1 && $scope.indexOfByMultiplaValue($scope.keylist, data) == -1 && isEnable == false && type == "datapod" || type == "dataset") {
					deferred.resolve();
				}
				else {
					deferred.reject();
				}
				
			}
	    } 
		return deferred.promise;
		
	}//End beforeDropKey

	$scope.onDropKey = function (event, ui, index) {
		var data = ui.draggable.scope().item
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
		$scope.allSourceAttribute.splice(index, 0, ui.draggable.scope().item);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//End onDropKey

	$scope.onOverCallBackKey = function (event, ui) {
		/*var data=ui.draggable.scope().item
		$scope.dupblicate=$scope.keylist.indexOf(data)*/
		//call method when droping
	}//End onOverCallBackKey

	$scope.removekey = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.keylist.splice(index, 1)
			$scope.myform.$dirty = true;
		}
	}//End removekey


	$scope.beforeDropGroup = function (event, ui, data) {
		var deferred = $q.defer();
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var isEnable = ($scope.mode == 'true');
		if($scope.vizpodtype !="score-card"){
			if ($scope.indexOfByMultiplaValue($scope.grouplist, data) == -1 && $scope.indexOfByMultiplaValue($scope.keylist, data) == -1 && isEnable == false && type == "datapod" ||  type == "dataset") {
				deferred.resolve();
			}
			else {
				deferred.reject();
			}
	    }
		return deferred.promise;
	}//End beforeDropGroup

	$scope.onDropGroup = function (event, ui, index) {
		var data = ui.draggable.scope().item
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
		$scope.allSourceAttribute.splice(index, 0, ui.draggable.scope().item);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//Emd onDropGroup

	$scope.removeGroup = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.grouplist.splice(index, 1)
			$scope.myform.$dirty = true;
		}
	}//End removeGroup


	$scope.beforeDropValue = function (event, ui, data) {
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var deferred = $q.defer();
		var isEnable = ($scope.mode == 'true');
		if (type == "formula" || type == "expression") {
			if ($scope.indexOfBySingleValue($scope.valuelist, data) == -1 && isEnable == false) {
				deferred.resolve();
			}
			else {
				deferred.reject();
			}
		}
		else {
			if ($scope.indexOfByMultiplaValue($scope.valuelist, data) == -1 && isEnable == false && type == "datapod" || type == "dataset") {
				deferred.resolve();
			}
			else {
				deferred.reject();
			}

		}

		return deferred.promise;
	}//End beforeDropValue

	$scope.onDropValue = function (event, ui, index) {
		var data ={}; 
		data.attributeId=ui.draggable.scope().item.attributeId;
		data.attributeName=ui.draggable.scope().item.attributeName;
		data.class=ui.draggable.scope().item.class;
		data.dname=ui.draggable.scope().item.dname;
		data.id=ui.draggable.scope().item.id;
		data.index=ui.draggable.scope().item.index;
		data.name=ui.draggable.scope().item.name;
		data.type=ui.draggable.scope().item.type;
		data.uuid=ui.draggable.scope().item.uuid;
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
	    $scope.allSourceAttribute.splice(index, 0,data);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//End onDropValue

	$scope.removeValue = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.myform.$dirty = true;
			$scope.valuelist.splice(index, 1)
		}
	}//End removeValue

	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.showVizpodGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showvizpod = false;
		$scope.showgraph = false
		$scope.showgraphdiv = true;
		$scope.isShowSimpleData =false;

	}//End showVizpodGraph
    
	$scope.showSampleTable = function (data) {
		if($scope.isDataInpogress){
			return false;
		};
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showvizpod = false;
		$scope.showgraph = false
		$scope.showgraphdiv = false
		$scope.isDataError = false;
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.spinner = true;
		VizpodSerivce.getVizpodResults(data.uuid,data.version,null).then(function (response) { onSuccessGetDatasourceByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			console.log(data.attributes)
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
			}
			$scope.spinner = false;
			$scope.gridOptions.columnDefs=$scope.getColumns($scope.originalData);
			console.log($scope.gridOptions.columnDefs)

		}

		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = false;
			$scope.spinner = false;
		}
	}
   
	$scope.getFormulaByType = function () {
		VizpodSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetFormulaByType(response.data) });
		var onSuccessGetFormulaByType = function (response) {
			for (var i = 0; i < response.length; i++) {
				var formulajson = {};
				formulajson.index = $scope.allSourceAttribute.length;
				formulajson.id = response[i].uuid+"_"+i;
				formulajson.class = "tagit-choice_formula-dd"
				formulajson.uuid = response[i].uuid
				formulajson.dname = response[i].name
				formulajson.name = response[i].name
				formulajson.type = "formula"
				$scope.allSourceAttribute.push(formulajson)
			}//End For
		}//End onSuccessGetFormulaByType
	}

	$scope.getExpressionByType = function () {
		VizpodSerivce.getExpressionByType($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetExpressionByType(response.data) });
		var onSuccessGetExpressionByType = function (response) {
			for (var i = 0; i < response.length; i++) {
				var expressionjson = {};
				expressionjson.index = $scope.allSourceAttribute.length;
				expressionjson.id = response[i].uuid+"_"+i
				expressionjson.class = "tagit-choice_expression-dd"
				expressionjson.uuid = response[i].uuid
				expressionjson.dname = response[i].name
				expressionjson.name = response[i].name
				expressionjson.type = "expression"
				$scope.allSourceAttribute.push(expressionjson)
			}//End For
		}//End onSuccessGetFormulaByType
	}

	$scope.onChangeSourceTypes = function () {
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
		$scope.filterAttributeTags=[];
		VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
		var onSuccessGetAllLatestBySource = function (response) {
			if (response != null) {
				$scope.allSource = response
				$scope.onChangeSource(); //Call Function
			}
		}
	} //End onChangeSourceTypes

	$scope.onChangeSource = function () {
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
		$scope.filterAttributeTags=[];
		VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
		var onSuccessGetAllAttributeBySourcet = function (response) {
			$scope.allSourceAttribute = response
			$scope.allattribute = response;
			$scope.getFormulaByType()//Call Function
			$scope.getExpressionByType() // Call Function
		}//End onSuccessGetAllAttributeBySourcet
	}//End onChangeSource
	$scope.addAll = function () {
		$scope.filterAttributeTags = $scope.allSourceAttribute;
		$scope.myform.$dirty=true;
	}

	$scope.clearAllDetailAttr=function(){
		$scope.filterAttributeTags=[];
	}
	$scope.checkValue = function () {
		if($scope.valuelist.length > 0 && $scope.vizpodtype =="score-card"){
			$scope.myform.$dirty = true;
		}
		else if ($scope.keylist.length > 0 && $scope.valuelist.length > 0 && $scope.vizpodtype !="") {
			$scope.myform.$dirty = true
		}
		else {
			$scope.myform.$dirty = false;

		}

	}

	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		VizpodSerivce.getAllVersionByUuid($stateParams.id, "vizpod").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var vizpodversion = {};
				vizpodversion.version = response[i].version;
				$scope.vizpod.versions[i] = vizpodversion;
			}
		}//End onGetAllVersionByUuid

		VizpodSerivce.getOneByUuidAndVersionView($stateParams.id, $stateParams.version, "vizpod")
			.then(function (response) { onGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.vizpoddata = response.vizpoddata;
			$scope.onChangeColorPalette($scope.vizpoddata.colorPalette);
			var defaultversion = {};
			defaultversion.version = response.vizpoddata.version;
			defaultversion.uuid = response.vizpoddata.uuid;
			$scope.vizpod.defaultVersion = defaultversion;
			$scope.sourcetype = response.vizpoddata.source.ref.type;
			$scope.vizpodtype = response.vizpoddata.type
			$scope.keylist = response.keys;
			$scope.grouplist = response.groups;
			$scope.valuelist = response.values;
			$scope.filterAttributeTags = response.detailAttr;
			$scope.sortByAttributeTags = response.sortByAttr;
			var tags = [];
			if (response.vizpoddata.tags != null) {
				for (var i = 0; i < response.vizpoddata.tags.length; i++) {
					var tag = {};
					tag.text = response.vizpoddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allSource = response
				var defaultoption = {}
				defaultoption.uuid = $scope.vizpoddata.source.ref.uuid
				defaultoption.name = $scope.vizpoddata.source.ref.name
				$scope.allSource.defaultoption = defaultoption;
				VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
				var onSuccessGetAllAttributeBySourcet = function (response) {
					$scope.allSourceAttribute = response
					$scope.allattribute = response;
					$scope.getFormulaByType()//Call Function
					$scope.getExpressionByType() // Call Function
				}//End onSuccessGetAllAttributeBySourcet
			}//End onSuccessGetAllLatestBySource
		}//End onGetLatestByUuid
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End If

	else {
		$scope.showactive = false;
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
		$scope.vizpoddata={};
		$scope.vizpoddata.locked="N";
	}//End Else

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	}
	$scope.loadAttrAndFormula = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allSourceAttribute, query);
		});
	}

	
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.allSource = null;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		VizpodSerivce.getOneByUuidAndVersionView($scope.vizpod.defaultVersion.uuid, $scope.vizpod.defaultVersion.version, 'vizpod')
			.then(function (response) { onSuccess(response.data) },function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			$scope.vizpoddata = response.vizpoddata;
			$scope.onChangeColorPalette($scope.vizpoddata.colorPalette);
			var defaultversion = {};
			defaultversion.version = response.vizpoddata.version;
			defaultversion.uuid = response.vizpoddata.uuid;
			$scope.vizpod.defaultVersion = defaultversion;
			$scope.sourcetype = response.vizpoddata.source.ref.type;
			$scope.vizpodtype = response.vizpoddata.type
			$scope.keylist = response.keys;
			$scope.grouplist = response.groups;
			$scope.valuelist = response.values;
			$scope.filterAttributeTags = response.detailAttr;
			var tags = [];
			if (response.vizpoddata.tags != null) {
				for (var i = 0; i < response.vizpoddata.tags.length; i++) {
					var tag = {};
					tag.text = response.vizpoddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allSource = response
				var defaultoption = {}
				defaultoption.uuid = $scope.vizpoddata.source.ref.uuid
				defaultoption.name = $scope.vizpoddata.source.ref.name
				$scope.allSource.defaultoption = defaultoption;
				VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
				var onSuccessGetAllAttributeBySourcet = function (response) {
					$scope.allSourceAttribute = response
					$scope.getFormulaByType()//Call Function
					$scope.getExpressionByType() // Call Function
				}//End onSuccessGetAllAttributeBySourcet
			}//End onSuccessGetAllLatestBySource
		}//End getOneByUuidAndVersion
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End selectVersion


	$scope.submitVizpod = function () {
        var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;

		var vizpodjson = {};
		vizpodjson.uuid = $scope.vizpoddata.uuid;
		vizpodjson.name = $scope.vizpoddata.name;
		vizpodjson.limit = $scope.vizpoddata.limit;
		vizpodjson.sortOrder = $scope.vizpoddata.sortOrder;

		var tagArray = [];

		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
		}

		vizpodjson.tags = tagArray;
		vizpodjson.desc = $scope.vizpoddata.desc;
		vizpodjson.active = $scope.vizpoddata.active;
		vizpodjson.locked = $scope.vizpoddata.locked;
		vizpodjson.published = $scope.vizpoddata.published;
		vizpodjson.publicFlag = $scope.vizpoddata.publicFlag;
		vizpodjson.colorPalette=$scope.vizpoddata.colorPalette;
		var sourece = {};
		var ref = {};
		ref.uuid = $scope.allSource.defaultoption.uuid;
		ref.type = $scope.sourcetype;
		sourece.ref = ref;
		vizpodjson.source = sourece
		vizpodjson.type = $scope.vizpodtype
		var attributeInfoArray = [];
		if ($scope.filterAttributeTags != null) {
			for (var i = 0; i < $scope.filterAttributeTags.length; i++) {
				var attributeInfo = {}
				var ref = {};
				ref.type = $scope.filterAttributeTags[i].type;
				ref.uuid = $scope.filterAttributeTags[i].uuid;
				attributeInfo.ref = ref;
				if($scope.filterAttributeTags[i].type !='formual'){
					attributeInfo.attributeId = $scope.filterAttributeTags[i].attributeId;
				}
				attributeInfoArray[i] = attributeInfo;
			}
		}
		vizpodjson.detailAttr = attributeInfoArray;
		var keyarray = [];
		for (var i = 0; i < $scope.keylist.length; i++) {
			var keyjson = {}
			var ref = {}
			ref.uuid = $scope.keylist[i].uuid
			ref.type = $scope.keylist[i].type;
			keyjson.ref = ref;
			if($scope.keylist[i].type !='formula'){
				keyjson.attributeId = $scope.keylist[i].attributeId;
			}
			keyarray[i] = keyjson
		}
		vizpodjson.keys = keyarray;

		var grouparray = [];
		for (var i = 0; i < $scope.grouplist.length; i++) {
			var groupjson = {}
			var ref = {}
			ref.uuid = $scope.grouplist[i].uuid
			ref.type = $scope.grouplist[i].type;
			groupjson.ref = ref;
			groupjson.attributeId = $scope.grouplist[i].attributeId;
			grouparray[i] = groupjson
		}
		vizpodjson.groups = grouparray;

		var valuearray = [];
		for (var i = 0; i < $scope.valuelist.length; i++) {
			var valuejson = {}
			var ref = {}
			ref.uuid = $scope.valuelist[i].uuid
			ref.type = $scope.valuelist[i].type;
			if($scope.valuelist[i].function !=null){
				valuejson.function=	$scope.valuelist[i].function
			}else{
				valuejson.function=null;
			}
			valuejson.ref = ref;
			if ($scope.valuelist[i].type == "datapod" || $scope.valuelist[i].type == "dataset" ) {
				valuejson.attributeId = $scope.valuelist[i].attributeId;
			}

			valuearray[i] = valuejson
		}
		vizpodjson.values = valuearray;
		vizpodjson.filterInfo = [];
		vizpodjson.dimension = [];
		var sortByArray=[];
		if($scope.sortByAttributeTags && $scope.sortByAttributeTags.length){
			for (var i = 0; i < $scope.sortByAttributeTags.length; i++) {
				var sortByjson = {}
				var ref = {}
				ref.uuid = $scope.sortByAttributeTags[i].uuid
				ref.type = $scope.sortByAttributeTags[i].type;
				sortByjson.ref = ref;
				if ($scope.sortByAttributeTags[i].type == "datapod" || $scope.sortByAttributeTags[i].type == "dataset" ) {
					sortByjson.attributeId = $scope.sortByAttributeTags[i].attributeId;
				}

				sortByArray[i] = sortByjson
			}
	    }
		vizpodjson.sortBy=sortByArray;
		console.log(JSON.stringify(vizpodjson));
		VizpodSerivce.submit(vizpodjson, 'vizpod',upd_tag)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.changemodelvalue()
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = "Vizpod Saved Successfully"
			$scope.$emit('notify', notify);
			$scope.okVizpodSave();
		}//End Submt Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);

		}
	}//End SubmitVizpod

	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};

	$scope.okVizpodSave = function () {
		$scope.stageName = $sessionStorage.fromStateName;
		$scope.stageParams = $sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
		//$('#vizpodsave').css("dispaly","none");
		var hidemode = "yes";
		if (hidemode == 'yes') {

			if ($scope.stageName == "dvvizpod") {
				setTimeout(function () { $state.go('vizpodlist'); }, 100);
			}
			else if ($scope.stageName != "dvvizpod" && typeof $scope.stageParams.id != "undefined") {
				setTimeout(function () { $state.go($scope.stageName, { 'id': $scope.stageParams.id }); },100);
			}
			else {
				setTimeout(function () { $state.go("vizpodlist"); }, 100);
			}
		}
	}//End
	$scope.refreshData = function () {
		var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		$scope.gridOptions.data=$scope.getResults(data);
	};
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


	$scope.getColumns = function (data) {
        var columns = [];
        var count = 0;
        if (data.length && data.length > 0) {
          angular.forEach(data[0], function (value, key) {
            count = count + 1;
          });
          angular.forEach(data[0], function (val, key) {
            var width;
            if (count > 3)
              width = key.split('').length + 12 + "%"
            else
			  width = (100 / count) + "%";
			  
            columns.push({ "name": key, "displayName": key.toLowerCase(), width: width, visible: true });
          });
        }
        return columns;
    }
	$scope.downloadFile = function (data) {
		if($scope.isDownloadDatapod)
		  return false;
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDownloadDirective=true;
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="vizpod";
		// $('#downloadSample').modal({
		// 	backdrop: 'static',
		// 	keyboard: false
		// });
	};
	$scope.onDownloaed=function(data){
		console.log(data);
		$scope.isDownloadDatapod=data.isDownloadInprogess;
		$scope.isDownloadDirective=data.isDownloadDirective;
	}
//   
//    $scope.submitDownload =function(){
//		$scope.isDownloadDatapod=true;
//		$('#downloadSample').modal("hide");
//		var url = $location.absUrl().split("app")[0]
//		$http({
//			method: 'GET',
//			url: url+$scope.download.type+"/download?action=view&uuid="+$scope.download.uuid+"&version="+$scope.download.version + "&rows="+$scope.download.rows+"&format="+$scope.download.selectFormate,
//			responseType: 'arraybuffer'
//		}).success(function (data, status, headers) {
//			$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
//			$scope.isDownloadDatapod=false;
//			headers = headers();
//			var filename = headers['filename'];
//			var contentType = headers['content-type'];
//			var linkElement = document.createElement('a');
//			try {
//				var blob = new Blob([data], {
//					type: contentType
//				});
//				var url = window.URL.createObjectURL(blob);
//				linkElement.setAttribute('href', url);
//				linkElement.setAttribute("download",filename);
//
//				var clickEvent = new MouseEvent("click", {
//					"view": window,
//					"bubbles": true,
//					"cancelable": false
//				});
//				linkElement.dispatchEvent(clickEvent);
//			} catch (ex) {
//				console.log(ex);
//			}
//		}).error(function (data) {
//			$scope.isDownloadDatapod=false;
//			console.log(data);
//			$('#downloadSample').modal("hide");
//		});
//	}

})/*End of Vizpod Controller*/
