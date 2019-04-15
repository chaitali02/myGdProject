/**
 **/
MetadataModule = angular.module('MetadataModule');
/* Start MetadataDatasetController*/

MetadataModule.controller('MetadataDatasetController', function (dagMetaDataService, $rootScope, $state, $scope, $stateParams, $cookieStore, $timeout, $filter, MetadataSerivce, MetadataDatasetSerivce, $sessionStorage, privilegeSvc, CommonService, CommonFactory, CF_FILTER,$location,$anchorScroll,CF_DOWNLOAD, $http,CF_GRID) {
	$rootScope.isCommentVeiwPrivlage = true;

	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
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
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isDragable = "true";
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
		$scope.isDragable = "true";
	}
	$scope.gridLimt=CF_GRID.framework_autopopulate_grid
	$scope.isAutoMapInprogess=false;
	$scope.continueCount=1;
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.mode = "false";
	$scope.dataLoading = false;
	$scope.iSSubmitEnable = false;
	$scope.datasetversion = {};
	$scope.datasetversion.versions = []
	$scope.showForm = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showGraphDiv = false
	$scope.graphDataStatus = false
	$scope.logicalOperator = ["AND", "OR"];
	$scope.rhsNA=['NULL',"NOT NULL"];
	$scope.SourceTypes = ["datapod", "relation", 'dataset']
	$scope.spacialOperator = ['<', '>', '<=', '>=', '=', '!=', 'LIKE', 'NOT LIKE', 'RLIKE'];
	$scope.operator = CF_FILTER.operator;
	$scope.isSubmitEnable = true;
	$scope.attributeTableArray = null;
	$scope.datsetsampledata = null;
	$scope.isShowSimpleData = false;
	$scope.isDependencyShow = false;
	$scope.isSimpleRecord = false;
	$scope.download={};
	$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates=CF_DOWNLOAD.formate;
	$scope.download.selectFormate=CF_DOWNLOAD.formate[0];
	$scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to=CF_DOWNLOAD.limit_to;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['dataset'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['dataset'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.lhsType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "formula", "caption": "formula" }];
	$scope.rhsType = [
		{ "text": "string", "caption": "string", "disabled": false },
		{ "text": "string", "caption": "integer", "disabled": false },
		{ "text": "datapod", "caption": "attribute", "disabled": false },
		{ "text": "formula", "caption": "formula", "disabled": false },
		{ "text": "dataset", "caption": "dataset", "disabled": false },
		{ "text": "paramlist", "caption": "paramlist", "disabled": false },
		{ "text": "function", "caption": "function", "disabled": false }];
	 
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
	}

	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isShowSimpleData = false
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/


	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.dataset.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListdataset', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListdataset', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('metaListdataset', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
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
    
	$scope.onChangeName = function (data) {
		$scope.dataset.displayName=data;
	}

	$scope.countContinue = function () {
	    if($scope.continueCount == 3){
			if($scope.isDuplication ==true){
				return true;
			}
		}
		$scope.continueCount = $scope.continueCount + 1;
        if ($scope.continueCount >= 4) {
            $scope.isSubmitShow = true;
        } else {
            $scope.isSubmitShow = false;
        }
    }

    $scope.countBack = function () {
        $scope.continueCount = $scope.continueCount - 1;
        $scope.isSubmitShow = false;
    }
    

	$scope.routeForFormula = function (data, index) {
		if (data.uuid == null) {
			$sessionStorage.index = index;
			var jsondata = {};
			jsondata.type = $scope.selectSourceType;
			jsondata.uuid = $scope.datasetRelation.defaultoption.uuid
			$sessionStorage.dependon = jsondata;
			$scope.jsonCode();
			$state.go('metaListformula');
		}
	}
	$scope.routeForExpression = function (data, index) {
		if (data.uuid == null) {
			$sessionStorage.index = index;
			var jsondata = {};
			jsondata.type = $scope.selectSourceType;
			jsondata.uuid = $scope.datasetRelation.defaultoption.uuid
			$sessionStorage.dependon = jsondata;
			$scope.jsonCode();
			$state.go('metaListexpression');
		}
	}
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
	$scope.isSourceDatapoLost = true;
	$scope.tags = null;
	$scope.datasetHasChanged = true;
	$scope.checkalljoin = false
	$scope.isshowmodel = false;
	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.datasetFormChange = function () {
		if ($scope.mode == "true") {
			$scope.datasetHasChanged = true;
		}
		else {
			$scope.datasetHasChanged = false;
		}
	}

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		if (fromState.name != "matadata") {
			$sessionStorage.fromParams = fromParams
		}
	});

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
		$scope.isShowSimpleData = false
	}/*End ShowGraph*/



	$scope.showSampleTable = function (data) {
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.isDataError = false;
		$scope.tableclass = "centercontent";
		$scope.showForm = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		MetadataDatasetSerivce.getDatasetSample(data).then(function (response) { onSuccessGetDatasourceByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			//console.log(JSON.stringify(response))
			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false;
			// if(response.data.length>0){
			//   $scope.datamessage="";
			// }
			// else{
			//   $scope.tableclass="No Results Found";
			//   $scope.msgclass="noResult";
			//   $scope.isDataError=true;
			// }

			for (var j = 0; j < data.attributeInfo.length; j++) {
				var attribute = {};
				attribute.name = data.attributeInfo[j].attrSourceName;
				attribute.displayName = data.attributeInfo[j].attrSourceName;
				attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				$scope.gridOptions.columnDefs.push(attribute)
			}

			//$scope.gridOptions.data = response;
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.getResults($scope.originalData);
			}
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
	$scope.refreshData = function () {
		var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		$scope.getResults(data)
	};
	$scope.selectType = function () {
		$scope.isDublication=false;
		MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.datasetRelation = response;
			if ($scope.dataset && $scope.selectSourceType == "dataset") {
				temp = response.options.filter(function (el) {
					return el.uuid !== $scope.dataset.uuid;
				});
				$scope.datasetRelation.options = temp
			}
			$scope.attributeTableArray = null;
			$scope.filterTableArray = null;
			$scope.addAttribute();
		//	$scope.addRowFilter();
			MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
				MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
				var onSuccessFormula = function (response) {
					$scope.datasetLodeFormula = response
				}
			}
		}
	}
	$scope.selectOption = function () {
		$scope.isDublication=false;
		MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
		var onSuccessGetDatapodByRelation = function (response) {
			$scope.sourcedatapodattribute = response;
			$scope.lhsdatapodattributefilter = response;
			$scope.attributeTableArray = null;
			$scope.filterTableArray = null;
			$scope.addAttribute();
		//	$scope.addRowFilter();
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.datasetLodeFormula = response
			}
		}
	}
	 
	$scope.getFormulaByApp=function(temp1){
		MetadataDatasetSerivce.getFormulaByApp("formula").then(function (response) { onSuccessGetFormulaByApp(response.data) });
		var onSuccessGetFormulaByApp = function (response) {
			if($scope.allFormula && $scope.allFormula.length >0){
				var temp = $scope.allFormula.concat(response);
				$scope.allFormula=temp;
			}
			else{
				$scope.allFormula = response;
			}
			if($scope.datasetLodeFormula && $scope.datasetLodeFormula.length >0){
				var temp = $scope.datasetLodeFormula.concat(response);
				$scope.datasetLodeFormula=temp;
			}
			else{
				$scope.datasetLodeFormula = response;
			}
			console.log(response);
		    console.log($scope.allFormula);
		}
	}
    $scope.getFormulaByType=function(){
		MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
		var onSuccessFormula = function (response) {
			$scope.datasetLodeFormula = response
			$scope.allFormula = response;
			$scope.allFormula.splice(0, 1);
			$scope.getFormulaByApp();
		}
	}
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
	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isSimpleRecord = true;
		MetadataDatasetSerivce.getAllVersionByUuid($stateParams.id, "dataset").then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datasetversion = {};
				datasetversion.version = response[i].version;
				$scope.datasetversion.versions[i] = datasetversion;
			}
		}
		if ($sessionStorage.fromStateName != "metadata" && typeof $sessionStorage.datasetjosn != "undefined" && !$rootScope.previousState) {
			$scope.dataset = $sessionStorage.datasetjosn;
			$scope.tags = $sessionStorage.datasetjosn.tags
			$scope.selectSourceType = $sessionStorage.datasetjosn.dependsOn.ref.type
			$scope.getParamByApp();
			MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.datasetRelation = response;
				if ($scope.dataset && $scope.selectSourceType == "dataset") {
					temp = response.options.filter(function (el) {
						return el.uuid !== $scope.dataset.uuid;
					});
					$scope.datasetRelation.options = temp
				}
				var defaultoption = {};
				defaultoption.type = $sessionStorage.datasetjosn.dependsOn.ref.type
				defaultoption.uuid = $sessionStorage.datasetjosn.dependsOn.ref.uuid
				var defaultversion = {};
				defaultversion.version = $sessionStorage.datasetjosn.version;
				defaultversion.uuid = $sessionStorage.datasetjosn.uuid;
				$scope.datasetversion.defaultVersion = defaultversion;
				$scope.datasetRelation.defaultoption = defaultoption;

				MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
				var onSuccessGetDatapodByRelation = function (response) {
					$scope.sourcedatapodattribute = response;
					$scope.lhsdatapodattributefilter = response;
				}
			}
			MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.datasetLodeExpression = response
			}
			MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.datasetLodeFormula = response
			}
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) });
			var onSuccessFuntion = function (response) {
				$scope.ruleLodeFunction = response;
				$scope.allFunction = response;
			}
			$scope.filterTableArray = $sessionStorage.datasetjosn.filterTableArray;
			$scope.attributeTableArray = $sessionStorage.datasetjosn.attributeTableArray
			MetadataDatasetSerivce.getOneById($sessionStorage.dependon.id, $sessionStorage.dependon.type).then(function (response) { onSuccessGetOneById(response.data) });
			var onSuccessGetOneById = function (response) {
				if ($sessionStorage.dependon.type == "formula") {
					var sourceformula = {};
					sourceformula.uuid = response.uuid;
					sourceformula.name = response.name;
					$scope.attributeTableArray[$sessionStorage.index].sourceformula = sourceformula;
				}
				else if ($sessionStorage.dependon.type == "expression") {
					var sourceexpression = {};
					sourceexpression.uuid = response.uuid;
					sourceexpression.name = response.name;
					$scope.attributeTableArray[$sessionStorage.index].sourceexpression = sourceexpression;
				}
			}
		}
		else {
			$scope.isEditInprogess=true;
   			$scope.isEditVeiwError=false;
			MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($stateParams.id, $stateParams.version, 'dataset')
				.then(function (response) { onSuccessResult(response.data) },function(response) {onError(response.data)});
			var onSuccessResult = function (response) {
			    $scope.isEditInprogess=false;
				$scope.dataset = response.dataset;
				$scope.selectSourceType = response.dataset.dependsOn.ref.type
				$scope.datasetCompare = response.dataset;
				var defaultversion = {};
				defaultversion.version = response.dataset.version;
				defaultversion.uuid = response.dataset.uuid;
				$scope.datasetversion.defaultVersion = defaultversion;
				$scope.tags = response.tags
				$scope.getParamByApp();
				MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
				var onSuccess = function (response) {
					$scope.datasetRelation = response;
					if ($scope.dataset && $scope.selectSourceType == "dataset") {
						temp = response.options.filter(function (el) {
							return el.uuid !== $scope.dataset.uuid;
						});
						$scope.datasetRelation.options = temp
					}
					var defaultoption = {};
					defaultoption.type = $scope.dataset.dependsOn.ref.type
					defaultoption.uuid = $scope.dataset.dependsOn.ref.uuid
					$scope.datasetRelation.defaultoption = defaultoption;
				}
				MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
				var onSuccessExpression = function (response) {
					$scope.datasetLodeExpression = response
				}

				$scope.getFormulaByType();
				MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid, $scope.dataset.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
				var onSuccessGetDatapodByRelation = function (response) {
					$scope.sourcedatapodattribute = response;
					$scope.lhsdatapodattributefilter = response;
				}
				CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) });
				var onSuccessFuntion = function (response) {
					$scope.ruleLodeFunction = response;
					//$scope.allFunction={};
					$scope.allFunction = response;
				}
				$scope.attributeTableArray = response.sourceAttributes
				$scope.filterTableArray = response.filterInfo;
				$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);
			}//End onSuccessResult
			var onError =function(){
				$scope.isEditInprogess=false;
				$scope.isEditVeiwError=true;
			} 
		}//End Inner Else
	}//End If
	else {
		$scope.showactive = "false"
		if ($sessionStorage.fromStateName && typeof $sessionStorage.fromStateName != "undefined" && $sessionStorage.fromStateName != "metadata" && $sessionStorage.fromStateName != "metaListdataset") {
			$scope.dataset = $sessionStorage.datasetjosn;
			$scope.tags = $sessionStorage.datasetjosn.tags
			$scope.selectSourceType = $sessionStorage.datasetjosn.dependsOn.ref.type
			$scope.getParamByApp();
			MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.datasetRelation = response;
				var defaultoption = {};
				defaultoption.type = $sessionStorage.datasetjosn.dependsOn.ref.type
				defaultoption.uuid = $sessionStorage.datasetjosn.dependsOn.ref.uuid
				$scope.datasetRelation.defaultoption = defaultoption;
				MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
				var onSuccessGetDatapodByRelation = function (response) {
					$scope.sourcedatapodattribute = response;
					$scope.lhsdatapodattributefilter = response;
				}
			}
			MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.datasetLodeExpression = response
			}
			
			$scope.getFormulaByType();
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) });
			var onSuccessFuntion = function (response) {

				$scope.ruleLodeFunction = response
				//$scope.allFunction={};
				$scope.allFunction = response;
			}
			$scope.filterTableArray = $sessionStorage.datasetjosn.filterTableArray;
			$scope.attributeTableArray = $sessionStorage.datasetjosn.attributeTableArray
			MetadataDatasetSerivce.getOneById($sessionStorage.dependon.id, $sessionStorage.dependon.type).then(function (response) { onSuccessGetOneById(response.data) });
			var onSuccessGetOneById = function (response) {
				if ($sessionStorage.dependon.type == "formula") {
					var sourceformula = {};
					sourceformula.uuid = response.uuid;
					sourceformula.name = response.name;
					$scope.attributeTableArray[$sessionStorage.index].sourceformula = sourceformula;
				}
				else if ($sessionStorage.dependon.type == "expression") {
					var sourceexpression = {};
					sourceexpression.uuid = response.uuid;
					sourceexpression.name = response.name;
					$scope.attributeTableArray[$sessionStorage.index].sourceexpression = sourceexpression;
				}
			}
			//delete $sessionStorage.datasetjosn;
		}//End Inner If
		else{
			$scope.dataset={};
			$scope.dataset.locked="N";
			$scope.dataset.limit=-1;
		}
	}//End Else

	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.datasetRelation = null;
		$scope.selectSourceType = null;
		$scope.datasetHasChanged = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($scope.datasetversion.defaultVersion.uuid, $scope.datasetversion.defaultVersion.version, 'dataset')
			.then(function (response) { onSuccessResult(response.data) });
		var onSuccessResult = function (response) {
			$scope.isEditInprogess=false;
			$scope.dataset = response.dataset;
			$scope.selectSourceType = response.dataset.dependsOn.ref.type
			$scope.datasetCompare = response.dataset;
			var defaultversion = {};
			defaultversion.version = response.dataset.version;
			defaultversion.uuid = response.dataset.uuid;
			$scope.datasetversion.defaultVersion = defaultversion;
			$scope.tags = response.tags
			$scope.getParamByApp();
			MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.datasetRelation = response;
				var defaultoption = {};
				defaultoption.type = $scope.dataset.dependsOn.ref.type
				defaultoption.uuid = $scope.dataset.dependsOn.ref.uuid
				$scope.datasetRelation.defaultoption = defaultoption;
			}
			MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.datasetLodeExpression = response
			}
			MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			$scope.getFormulaByType();
			MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid, $scope.dataset.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
			}
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) }); var onSuccessFuntion = function (response) {
				$scope.ruleLodeFunction = response
				//$scope.allFunction={};
				$scope.allFunction = response;
			}
			$scope.attributeTableArray = response.sourceAttributes
			$scope.filterTableArray = response.filterInfo;
			$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);

		}//End onSuccessResult
		var onError =function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		} 
	}/* End selectVersion*/

	$scope.SearchAttribute = function (index, type, propertyType) {
		$scope.selectAttr = $scope.filterTableArray[index][propertyType]
		$scope.searchAttr = {};
		$scope.searchAttr.type = type;
		$scope.searchAttr.propertyType = propertyType;
		$scope.searchAttr.index = index;
		MetadataDatasetSerivce.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		$scope.searchAttrIndex = index;
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = response;
			var temp;
			if ($scope.selectSourceType == "dataset") {
				temp = $scope.allSearchType.options.filter(function (el) {
					return el.uuid !== $scope.datasetRelation.defaultoption.uuid;
				});
				$scope.allSearchType.options = temp;
				$scope.allSearchType.defaultoption = temp[0]
			}
			if ($scope.dataset) {
				temp = $scope.allSearchType.options.filter(function (el) {
					return el.uuid !== $scope.dataset.uuid;
				});
				$scope.allSearchType.options = temp;
				$scope.allSearchType.defaultoption = temp[0]
			}
			if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
				var defaultoption = {};
				defaultoption.uuid = $scope.selectAttr.uuid;
				defaultoption.name = "";
				$scope.allSearchType.defaultoption = defaultoption;
			}
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			});
			MetadataDatasetSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.allAttr = response;
				if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
					var defaultoption = {};
					defaultoption.uuid = $scope.selectAttr.uuid;
					defaultoption.name = "";
					$scope.allSearchType.defaultoption = defaultoption;
				} else {
					$scope.selectAttr = $scope.allAttr[0]
				}

			}
		}
	}

	$scope.onChangeSearchAttr = function () {
		MetadataDatasetSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SubmitSearchAttr = function () {
		$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
		$('#searchAttr').modal('hide')
	}

	$scope.disableRhsType = function (rshTypes, arrayStr) {
		for (var i = 0; i < rshTypes.length; i++) {
			rshTypes[i].disabled = false;
			if (arrayStr.length > 0) {
				var index = arrayStr.indexOf(rshTypes[i].caption);
				if (index != -1) {
					rshTypes[i].disabled = true;
				}
			}
		}
		return rshTypes;
	}

	$scope.onChangeOperator = function (index) {
		
		$scope.filterTableArray[index].isRhsNA=false;
		if ($scope.filterTableArray[index].operator == 'BETWEEN') {
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 
		else if (['IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, []);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 
		else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist','string','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 

		else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['dataset']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else if (['IS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].isRhsNA=true;
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['dataset']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
	}

	$scope.checkAllFilterRow = function () {
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
	
	function returnRshType(){
		var rTypes = [
			{ "text": "string", "caption": "string", "disabled": false },
			{ "text": "string", "caption": "integer", "disabled": false },
			{ "text": "datapod", "caption": "attribute", "disabled": false },
			{ "text": "formula", "caption": "formula", "disabled": false },
			{ "text": "dataset", "caption": "dataset", "disabled": false },
			{ "text": "paramlist", "caption": "paramlist", "disabled": false },
			{ "text": "function", "caption": "function", "disabled": false }]
	    return rTypes;
	}
	$scope.addRowFilter = function () {
		if ($scope.filterTableArray == null) {
			$scope.filterTableArray = [];
		}
		var filertable = {};
		filertable.islhsDatapod = true;
		filertable.islhsFormula = false;
		filertable.islhsSimple = false;
		filertable.isrhsDatapod = true;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = false;
		filertable.lhsFilter = $scope.lhsdatapodattributefilter[0];
		filertable.lhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		filertable.rhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		
		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[2]
		filertable.rhstype = $scope.rhsType[2];
		filertable.rhsTypes=returnRshType();
		
		filertable.rhsTypes = $scope.disableRhsType(filertable.rhsTypes, ['dataset']);
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
			$scope.fitlerAttrTableSelectedItem=[];
		});

		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.filterTableArray = newDataList;
	}

	$scope.onAttrFilterRowDown=function(index){	
		var rowTempIndex=$scope.filterTableArray[index];
        var rowTempIndexPlus=$scope.filterTableArray[index+1];
		$scope.filterTableArray[index]=rowTempIndexPlus;
		$scope.filterTableArray[index+1]=rowTempIndex;
		if(index ==0){
			$scope.filterTableArray[index+1].logicalOperator=$scope.filterTableArray[index].logicalOperator;
			$scope.filterTableArray[index].logicalOperator=""
		}
	}

	$scope.onAttrFilterRowUp=function(index){
		var rowTempIndex=$scope.filterTableArray[index];
        var rowTempIndexMines=$scope.filterTableArray[index-1];
		$scope.filterTableArray[index]=rowTempIndexMines;
		$scope.filterTableArray[index-1]=rowTempIndex;
		if(index ==1){
			$scope.filterTableArray[index].logicalOperator=$scope.filterTableArray[index-1].logicalOperator;
			$scope.filterTableArray[index-1].logicalOperator=""
		}
	}  
	
	$scope.onFilterDrop=function(index){
		if(index.targetIndex== 0){
			$scope.filterTableArray[index.sourceIndex].logicalOperator=$scope.filterTableArray[index.targetIndex].logicalOperator;
			$scope.filterTableArray[index.targetIndex].logicalOperator=""
		}
		if(index.sourceIndex == 0){
			$scope.filterTableArray[index.targetIndex].logicalOperator=$scope.filterTableArray[index.sourceIndex].logicalOperator;
			$scope.filterTableArray[index.sourceIndex].logicalOperator=""
		}
	}

	$scope.selectlhsType = function (type, index) {
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
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
			if(typeof $stateParams.id == "undefined") {
				MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
				var onSuccressGetFormula = function (response) {
					response.splice(0, 1);
					$scope.allFormula = response;
					$scope.getFormulaByApp();
				}
		    }
		}
	}


	$scope.selectrhsType = function (type, index) {
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
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
			if(typeof $stateParams.id == "undefined") {
				MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
				var onSuccressGetFormula = function (response) {
					$scope.allFormula = response;
					$scope.allFormula.splice(0, 1);
					$scope.getFormulaByApp();
			    }
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

	}


	$scope.checkAllAttributeRow = function () {
		angular.forEach($scope.attributeTableArray, function (attribute) {
			attribute.selected = $scope.selectAllAttributeRow;
		});
	}

	$scope.onChangeSimple = function () {
		
	}

	$scope.onChangeAttribute = function () {
		
	}

	$scope.onChangeFromula = function () {
	}

	$scope.onChangeRhsParamList = function () {
		
	}
	
	$scope.addAttribute = function () {
		if ($scope.attributeTableArray == null) {
			$scope.attributeTableArray = [];
		}
	
		var len = $scope.attributeTableArray.length + 1
		var attributeinfo = {};
		if($scope.attributeTableArray.length ==0){
			attributeinfo.id=$scope.attributeTableArray.length;
		}else{
			attributeinfo.id =CommonFactory.getMaxSourceSeqId($scope.attributeTableArray,"id")+1;
		}
		attributeinfo.name = "attribute" + len;
		attributeinfo.index = len;
		attributeinfo.sourceAttributeType = $scope.sourceAttributeTypes[0];
		attributeinfo.isSourceAtributeSimple = true;
		attributeinfo.isSourceAtributeDatapod = false;
		$scope.attributeTableArray.splice($scope.attributeTableArray.length, 0, attributeinfo);
		$scope.focusRow(len-1)
	}
    $scope.onAttrRowDown=function(index){
		
		var rowTempIndex=$scope.attributeTableArray[index];
        var rowTempIndexPlus=$scope.attributeTableArray[index+1];
		$scope.attributeTableArray[index]=rowTempIndexPlus;
		$scope.attributeTableArray[index+1]=rowTempIndex;
	}
	$scope.onAttrRowUp=function(index){
		
		var rowTempIndex=$scope.attributeTableArray[index];
        var rowTempIndexMines=$scope.attributeTableArray[index-1];
		$scope.attributeTableArray[index]=rowTempIndexMines;
		$scope.attributeTableArray[index-1]=rowTempIndex;
	}
	$scope.focusRow = function(rowId){
    
		$timeout(function() {
		  $location.hash(rowId);
		  $anchorScroll();
		});
	  }

	$scope.removeAttribute = function () {
		var newDataList = [];
		$scope.selectAllAttributeRow = false
		angular.forEach($scope.attributeTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.attrTableSelectedItem=[];
		});

		$scope.attributeTableArray = newDataList;
		var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				var result=$scope.onChangeSourceName1(index,dupArray);
				if(result.length >0 ){
					$scope.isDuplication = true;
				}else {
					$scope.isDuplication = false;
				}
			},10,(i));
		}
	}

    $scope.autoPopulate=function(){
		$scope.isAutoMapInprogess=true;
		$scope.attributeTableArray=[];
		$scope.attrTableSelectedItem=[];
		var dupArray=[];
		$timeout(function(){
		for(var i=0;i<$scope.sourcedatapodattribute.length;i++){
			var attributeinfo = {};
			attributeinfo.id =i;
			if($scope.sourcedatapodattribute.length >CF_GRID.framework_autopopulate_grid)
				attributeinfo.isOnDropDown=false;
			else
				attributeinfo.isOnDropDown=true;
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
				var result=$scope.onChangeSourceName1(index,dupArray);
				if(result.length >0 ){
					$scope.isDuplication = true;
				}else {
					$scope.isDuplication = false;
				}
			},10,(i));

		}
		if(i== $scope.attributeTableArray.length)
			$scope.isAutoMapInprogess=false;
		},40);
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
			$scope.attributeTableArray[index].isOnDropDown=true;
		}
		else if (type == "formula") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = true;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
			$scope.attributeTableArray[index].isOnDropDown=true;
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.datasetLodeFormula = response;
				$scope.getFormulaByApp();

			}
		}
		else if (type == "expression") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = true;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = false;
			$scope.attributeTableArray[index].isOnDropDown=true;

			MetadataDatasetSerivce.getExpressionByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.datasetLodeExpression = response
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
				$scope.ruleLodeFunction = response
			}
		}
		else if (type == "paramlist") {
			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			$scope.attributeTableArray[index].isSourceAtributeParamList = true;
			$scope.attributeTableArray[index].isOnDropDown=true;

			$scope.getParamByApp();
		}


	}
	
	
	  function isDublication (arr, field, index, name,darray) {
		var res = [];
		for(var i = 0; i < arr.length;i++){
			if (arr[i][field] == arr[index][field] && i != index) {
			    $scope.myform3[name].$invalid = true;
				darray.push(i);
				break
			}
			else {
				$scope.myform3[name].$invalid = false;	
			}
		}
		
		return darray;
	}
	
	$scope.onChangeSourceName1 = function (index,dupArray) {
		$scope.attributeTableArray[index].isSourceName = true;
		if ($scope.attributeTableArray[index].name) {
			var result = isDublication($scope.attributeTableArray, "name", index, "sourceName" + index,dupArray);
		}
		return dupArray
	}

	$scope.onChangeSourceName = function (index) {
		$scope.attributeTableArray[index].isSourceName = true;
		var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res = isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}

	$scope.onChangeAttributeDatapod = function (data, index) {
		if (data != null && !$scope.attributeTableArray[index].isSourceName) {
			$scope.attributeTableArray[index].name = data.name
			//	console.log($filter('unique')($scope.attributeTableArray,"name"));
		}
		var dupArray=[];
		setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}

	$scope.onChangeFormula = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.name;
		var dupArray=[];
		setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}

	$scope.onChangeExpression = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.name;
		setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);	
		var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res = isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}
	$scope.onChangeParamlist = function (data, index) {
		if (!$scope.attributeTableArray[index].isSourceName)
			$scope.attributeTableArray[index].name = data.paramName;
		var dupArray=[];
		setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res = isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}
	
	$scope.ngChangeFunction=function(){
		if (!$scope.attributeTableArray[index].isSourceName)
		$scope.attributeTableArray[index].name = data.paramName;
		var dupArray=[];
		setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res = isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}

	$scope.jsonCode = function () {
		var dataSetJson = {}
		dataSetJson.uuid = $scope.dataset.uuid
		dataSetJson.name = $scope.dataset.name;
		if ($scope.datasetversion.versions.length > 0) {
			dataSetJson.version = $scope.datasetversion.defaultVersion.version
			var ref = {};
			ref.name = $scope.dataset.createdBy.ref.name
			ref.uuid = $scope.dataset.createdBy.ref.uuid;
			var createdBy = {};
			createdBy.ref = ref;
			dataSetJson.createdBy = createdBy;
		}

		dataSetJson.desc = $scope.dataset.desc
		dataSetJson.active = $scope.dataset.active;
		dataSetJson.createdOn = $scope.dataset.createdOn

		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;

			}
		}
		dataSetJson.tags = tagArray;
		//relationInfo
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectSourceType
		ref.uuid = $scope.datasetRelation.defaultoption.uuid
		dependsOn.ref = ref;
		dataSetJson.dependsOn = dependsOn;
		dataSetJson.filterTableArray = $scope.filterTableArray;
		dataSetJson.attributeTableArray = $scope.attributeTableArray
		$sessionStorage.datasetjosn = dataSetJson
	}


	$scope.submit = function () {
		var upd_tag = "N"
		delete $sessionStorage.datasetjosn;
		delete $sessionStorage.index
		delete $sessionStorage.dependon
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datasetHasChanged = true;
		$scope.myform3.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform1.$dirty = false;

		var dataSetJson = {}
		dataSetJson.uuid = $scope.dataset.uuid
		dataSetJson.name = $scope.dataset.name;
		dataSetJson.displayName = $scope.dataset.displayName;
		dataSetJson.desc = $scope.dataset.desc
		dataSetJson.active = $scope.dataset.active;
		dataSetJson.published = $scope.dataset.published;
		dataSetJson.limit = $scope.dataset.limit;
		dataSetJson.locked = $scope.dataset.locked;
		dataSetJson.publicFlag = $scope.dataset.publicFlag;


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
		dataSetJson.tags = tagArray;

		//relationInfo
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectSourceType
		ref.uuid = $scope.datasetRelation.defaultoption.uuid
		dependsOn.ref = ref;
		dataSetJson.dependsOn = dependsOn;
		

		//filterInfo
		var filterInfoArray = [];
		if ( $scope.filterTableArray && $scope.filterTableArray.length > 0) {
			for (var i = 0; i < $scope.filterTableArray.length; i++) {
				var filterInfo = {};
				var operand  = []
				var lhsoperand = {}
				var lhsref = {}
				var rhsoperand = {}
				var rhsref = {};
				filterInfo.display_seq=i;
				if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
					filterInfo.logicalOperator = ""
				}
				else {
					filterInfo.logicalOperator = $scope.filterTableArray[i].logicalOperator
				}
				filterInfo.operator = $scope.filterTableArray[i].operator;
				if ($scope.filterTableArray[i].lhstype.text == "string") {

					lhsref.type = "simple";
					lhsoperand.ref = lhsref;
					lhsoperand.attributeType = $scope.filterTableArray[i].lhstype.caption;
					lhsoperand.value = $scope.filterTableArray[i].lhsvalue;

				}
				else if ($scope.filterTableArray[i].lhstype.text == "datapod") {

					lhsref.type = $scope.filterTableArray[i].lhsdatapodAttribute.type;
					lhsref.uuid = $scope.filterTableArray[i].lhsdatapodAttribute.uuid;
					lhsoperand.ref = lhsref;
					lhsoperand.attributeId = $scope.filterTableArray[i].lhsdatapodAttribute.attributeId;
				}
				else if ($scope.filterTableArray[i].lhstype.text == "formula") {

					lhsref.type = "formula";
					lhsref.uuid = $scope.filterTableArray[i].lhsformula.uuid;
					lhsoperand.ref = lhsref;
				}
				operand[0] = lhsoperand;
				if ($scope.filterTableArray[i].rhstype.text == "string") {

					rhsref.type = "simple";
					rhsoperand.ref = rhsref;
					rhsoperand.attributeType = $scope.filterTableArray[i].rhstype.caption;
					rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
					if ($scope.filterTableArray[i].operator == 'BETWEEN') {
						rhsoperand.value = $scope.filterTableArray[i].rhsvalue1 + "and" + $scope.filterTableArray[i].rhsvalue2;
					}
				}
				else if ($scope.filterTableArray[i].rhstype.text == "datapod") {
		
					rhsref.type = $scope.filterTableArray[i].rhsdatapodAttribute.type;
					rhsref.uuid = $scope.filterTableArray[i].rhsdatapodAttribute.uuid;

					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.filterTableArray[i].rhsdatapodAttribute.attributeId;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "formula") {

					rhsref.type = "formula";
					rhsref.uuid = $scope.filterTableArray[i].rhsformula.uuid;
					rhsoperand.ref = rhsref;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "function") {
					rhsref.type = "function";
					rhsref.uuid = $scope.filterTableArray[i].rhsfunction.uuid;
					rhsoperand.ref = rhsref;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "dataset") {
					rhsref.type = "dataset";
					rhsref.uuid = $scope.filterTableArray[i].rhsdataset.uuid;
					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.filterTableArray[i].rhsdataset.attributeId;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "paramlist") {

					rhsref.type = "paramlist";
					rhsref.uuid = $scope.filterTableArray[i].rhsparamlist.uuid;
					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.filterTableArray[i].rhsparamlist.attributeId;
				}
				operand[1] = rhsoperand;
				filterInfo.operand = operand;
				filterInfoArray[i] = filterInfo;

			}//End FilterInfo
		
			dataSetJson.filterInfo = filterInfoArray;
		}
		else {
			dataSetJson.filterInfo = null;
		}

		var sourceAttributesArray = [];
		for (var l = 0; l < $scope.attributeTableArray.length; l++) {
			attributeinfo = {}
			attributeinfo.attrSourceId =$scope.attributeTableArray[l].id;
			attributeinfo.attrDisplaySeq = l;
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

				ref.type = $scope.attributeTableArray[l].sourcedatapod.type;
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
		dataSetJson.attributeInfo = sourceAttributesArray
		console.log(JSON.stringify(dataSetJson))


		MetadataDatasetSerivce.submit(dataSetJson, 'dataset', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.changemodelvalue();
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Dataset Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okdatasetsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}

		return false;
	}

	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};

	$scope.okdatasetsave = function () {
		$('#datasetsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'dataset' }); }, 2000);

		}
	}
    $scope.ondrop = function(e) {
		console.log(e);
		$scope.myform3.$dirty=true;
	}
	$scope.expandAll = function (expanded) {
		// $scope is required here, hence the injection above, even though we're using "controller as" syntax
		$scope.$broadcast('onExpandAll', { expanded: expanded });
	};

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

	$scope.downloadFile = function (data) {
		if($scope.isDownloadDataset)
		  return false;
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="dataset";
		$('#downloadSample').modal({
			backdrop: 'static',
			keyboard: false
		});
	};

    $scope.submitDownload =function(){
		$scope.isDownloadDataset=true;
		$('#downloadSample').modal("hide");
		var url = $location.absUrl().split("app")[0]
		$http({
			method: 'GET',
			url: url+$scope.download.type+"/download?action=view&uuid="+$scope.download.uuid+"&version="+$scope.download.version + "&rows="+$scope.download.rows+"&format="+$scope.download.selectFormate,
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
			$scope.isDownloadDataset=false;
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
			$scope.isDownloadDataset=false;
			console.log(data);
			$('#downloadSample').modal("hide");
		});
	}
	$scope.attrTableSelectedItem=[];

	$scope.onChangeAttRow=function(index,status){
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
	$scope.fitlerAttrTableSelectedItem=[];
	$scope.onChangeFilterAttRow=function(index,status){
		if(status ==true){
			$scope.fitlerAttrTableSelectedItem.push(index);
		}
		else{
			let tempIndex=$scope.fitlerAttrTableSelectedItem.indexOf(index);

			if(tempIndex !=-1){
				$scope.fitlerAttrTableSelectedItem.splice(tempIndex, 1);

			}
		}	
	}
	$scope.autoMove=function(index,type){
		if(type=="mapAttr"){
			var tempAtrr=$scope.attributeTableArray[$scope.attrTableSelectedItem[0]];
			$scope.attributeTableArray.splice($scope.attrTableSelectedItem[0],1);
			$scope.attributeTableArray.splice(index,0,tempAtrr);
			$scope.attrTableSelectedItem=[];
			$scope.attributeTableArray[index].selected=false;
		}
		else{
			var tempAtrr=$scope.filterTableArray[$scope.fitlerAttrTableSelectedItem[0]];
			$scope.filterTableArray.splice($scope.fitlerAttrTableSelectedItem[0],1);
			$scope.filterTableArray.splice(index,0,tempAtrr);
			$scope.fitlerAttrTableSelectedItem=[];
			$scope.filterTableArray[index].selected=false;
			$scope.filterTableArray[0].logicalOperator="";
			if($scope.filterTableArray[index].logicalOperator =="" && index !=0){
				$scope.filterTableArray[index].logicalOperator=$scope.logicalOperator[0];
			}else if($scope.filterTableArray[index].logicalOperator =="" && index ==0){
				$scope.filterTableArray[index+1].logicalOperator=$scope.logicalOperator[0];
			}
		}
	}

	$scope.autoMoveTo=function(index,type){
		if(type =="mapAttr"){
			if(index <= $scope.attributeTableArray.length){
				$scope.autoMove(index-1,'mapAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
		else{
			if(index <= $scope.filterTableArray.length){
				$scope.autoMove(index-1,'filterAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
	}
});/* End MetadataDatasetController*/


  	/*MetadataModule.directive('expand', function () {
  	    return {
  	        restrict: 'A',
  	        controller: ['$scope', function ($scope) {
  	            $scope.$on('onExpandAll', function (event, args) {
  	                $scope.expanded = args.expanded;
  	            });
  	        }]
  	    };
  	});*/
	
	
	