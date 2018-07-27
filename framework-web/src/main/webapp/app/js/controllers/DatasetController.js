/**
 **/
MetadataModule = angular.module('MetadataModule');
/* Start MetadataDatasetController*/
MetadataModule.controller('MetadataDatasetController', function (dagMetaDataService, $rootScope, $state, $scope, $stateParams, $cookieStore, $timeout, $filter, MetadataSerivce, MetadataDatasetSerivce, $sessionStorage, privilegeSvc,CommonService,CF_FILTER) {
	$rootScope.isCommentVeiwPrivlage=true;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.isDragable="false";
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isDragable="true";
		$scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else {
		$scope.isAdd = true;
		$scope.isDragable="true";
	}
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
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
	$scope.logicalOperator = [" ", "OR", "AND"];
	$scope.SourceTypes = ["datapod", "relation",'dataset']
	$scope.spacialOperator=['<','>','<=','>=','=','LIKE','NOT LIKE','RLIKE'];
    $scope.operator =CF_FILTER.operator;
	$scope.isSubmitEnable = true;
	$scope.attributeTableArray = null;
	$scope.datsetsampledata = null;
	$scope.isShowSimpleData = false;
	$scope.isDependencyShow = false;
	$scope.isSimpleRecord = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['dataset'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['dataset'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.lhsType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer"},
		{ "text": "datapod", "caption": "attribute"},
		{ "text": "formula", "caption": "formula"}];
	$scope.rhsType = [
		{ "text": "string", "caption": "string","disabled":false },
		{ "text": "string", "caption": "integer" ,"disabled":false },
		{ "text": "datapod", "caption": "attribute","disabled":false },
		{ "text": "formula", "caption": "formula","disabled":false },
		{ "text": "dataset", "caption": "dataset" ,"disabled":false },
		{ "text":  "paramlist", "caption": "paramlist" ,"disabled":false },
		{ "text": "function", "caption": "function" ,"disabled":false }]
	/*Start showPage*/
	$scope.showPage = function () {
		$scope.isShowSimpleData = false
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/
	
	
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('metaListdataset', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListdataset', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
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
	
	$scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag=response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
    $scope.getLovByType();
	// $scope.onOverCallBackRow=function(event,ui,index){
	// 	 console.log($scope.indexDragg)
	// 	console.log(index)
	// 		var data=ui.draggable.scope().tabledata
	// 		if(index == 0 ){
	// 			var temp=$scope.filterTableArray[$scope.indexDragg].logicalOperator;
	// 			$scope.filterTableArray[$scope.indexDragg].logicalOperator=" "
	// 			$scope.filterTableArray[index].logicalOperator=temp;
	// 		}
	// 		// else if($scope.indexDragg == 0){
	// 		// 	var temp=$scope.filterTableArray[index].logicalOperator;
	// 		// 	$scope.filterTableArray[index].logicalOperator=" "
	// 		// 	$scope.filterTableArray[$scope.indexDragg].logicalOperator=temp;
	// 		// }
	// }
	// $scope.onDragCallBackRow=function(event,ui,index){
	// 	$scope.indexDragg=null
	// 	$scope.indexDragg=index.index;
	// }
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
		{ "text": "function", "caption": "function" }
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
		// console.log(fromParams)
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



	$scope.showSampleTable = function (data) {
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
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
		MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.datasetRelation = response;
			if($scope.dataset &&  $scope.selectSourceType == "dataset"){
				temp = response.options.filter(function(el) {
					return el.uuid !== $scope.dataset.uuid;
				});
				$scope.datasetRelation.options = temp
			}
			$scope.attributeTableArray=null;
			$scope.addAttribute();
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
		MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
		var onSuccessGetDatapodByRelation = function (response) {
			$scope.sourcedatapodattribute = response;
			$scope.lhsdatapodattributefilter = response;
			$scope.attributeTableArray=null;
			$scope.addAttribute();
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.datasetLodeFormula = response
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
			MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.datasetRelation = response;		
				if($scope.dataset &&  $scope.selectSourceType == "dataset"){
					temp = response.options.filter(function(el) {
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
			MetadataDatasetSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFuntion(response.data) });
			var onSuccessFuntion = function (response) {
				$scope.ruleLodeFunction = response
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
			MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($stateParams.id, $stateParams.version, 'datasetview').then(function (response) { onSuccessResult(response.data) });
			var onSuccessResult = function (response) {
				$scope.dataset = response.dataset;
				$scope.selectSourceType = response.dataset.dependsOn.ref.type
				$scope.datasetCompare = response.dataset;
				var defaultversion = {};
				defaultversion.version = response.dataset.version;
				defaultversion.uuid = response.dataset.uuid;
				$scope.datasetversion.defaultVersion = defaultversion;
				$scope.tags = response.tags
				MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
				var onSuccess = function (response) {
					//console.log(JSON.stringify(response))
					$scope.datasetRelation = response;
					if($scope.dataset &&  $scope.selectSourceType == "dataset"){
						temp = response.options.filter(function(el) {
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
				MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
				var onSuccessFormula = function (response) {
					$scope.datasetLodeFormula = response
					$scope.allFormula = response;
					$scope.allFormula.splice(0, 1);
				}
				MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid, $scope.dataset.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
				var onSuccessGetDatapodByRelation = function (response) {
					$scope.sourcedatapodattribute = response;
					$scope.lhsdatapodattributefilter = response;
				}
				MetadataDatasetSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFuntion(response.data) });
				var onSuccessFuntion = function (response) {
					$scope.ruleLodeFunction = response;
					$scope.allFunction={};
					$scope.allFunction.options=response;
				}
				$scope.attributeTableArray = response.sourceAttributes
				$scope.filterTableArray = response.filterInfo;
				$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);
			}//End onSuccessResult
		}//End Inner Else
	}//End If
	else {
		$scope.showactive = "false"
		if ($sessionStorage.fromStateName && typeof $sessionStorage.fromStateName != "undefined" && $sessionStorage.fromStateName != "metadata" && $sessionStorage.fromStateName != "metaListdataset") {
			$scope.dataset = $sessionStorage.datasetjosn;
			$scope.tags = $sessionStorage.datasetjosn.tags
			$scope.selectSourceType = $sessionStorage.datasetjosn.dependsOn.ref.type
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
			MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.datasetLodeFormula = response;
				$scope.allFormula = response;
				$scope.allFormula.splice(0, 1);
			}
			MetadataDatasetSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFuntion(response.data) });
			var onSuccessFuntion = function (response) {
				
				$scope.ruleLodeFunction = response
				$scope.allFunction={};
				$scope.allFunction.options=response;
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
	}//End Else

	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.datasetRelation = null;
		$scope.selectSourceType = null;
		$scope.myform.$dirty = false;
		$scope.datasetHasChanged = true;
		MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($scope.datasetversion.defaultVersion.uuid, $scope.datasetversion.defaultVersion.version, 'datasetview').then(function (response) { onSuccessResult(response.data) });
		var onSuccessResult = function (response) {
			$scope.dataset = response.dataset;
			$scope.selectSourceType = response.dataset.dependsOn.ref.type
			$scope.datasetCompare = response.dataset;
			var defaultversion = {};
			defaultversion.version = response.dataset.version;
			defaultversion.uuid = response.dataset.uuid;
			$scope.datasetversion.defaultVersion = defaultversion;
			$scope.tags = response.tags
			MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				// console.log(JSON.stringify(response))
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
			var onSuccessFormula = function (response) {
				$scope.datasetLodeFormula = response
				$scope.allFormula = response;
				$scope.allFormula.splice(0, 1);
			}
			MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid, $scope.dataset.dependsOn.ref.type).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
			var onSuccessGetDatapodByRelation = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.lhsdatapodattributefilter = response;
			}
			MetadataDatasetSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFuntion(response.data) });			var onSuccessFuntion = function (response) {
				$scope.ruleLodeFunction = response
				$scope.allFunction={};
				$scope.allFunction.options=response;
			}
			$scope.attributeTableArray = response.sourceAttributes
			$scope.filterTableArray = response.filterInfo;
			$scope.filterOrignal = $scope.original = angular.copy(response.filterInfo);

		}//End onSuccessResult
	}/* End selectVersion*/
	 
	$scope.SearchAttribute=function(index,type,propertyType){
		$scope.selectAttr=$scope.filterTableArray[index][propertyType]
		$scope.searchAttr={};
		$scope.searchAttr.type=type;
		$scope.searchAttr.propertyType=propertyType;
		$scope.searchAttr.index=index;
		MetadataDatasetSerivce.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		$scope.searchAttrIndex=index;
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = response;
			var temp;
			if($scope.selectSourceType == "dataset"){
				temp = $scope.allSearchType.options.filter(function(el) {
					return el.uuid !== $scope.datasetRelation.defaultoption .uuid;
				});
				$scope.allSearchType.options=temp;
				$scope.allSearchType.defaultoption=temp[0]
			}
			if($scope.dataset){
				temp = $scope.allSearchType.options.filter(function(el) {
					return el.uuid !== $scope.dataset.uuid;
				});
				$scope.allSearchType.options=temp;
				$scope.allSearchType.defaultoption=temp[0]
			}
			if(typeof $stateParams.id != "undefined" && $scope.selectAttr){
				var defaultoption={};
				defaultoption.uuid=$scope.selectAttr.uuid;
				defaultoption.name="";
				$scope.allSearchType.defaultoption=defaultoption;
			}
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			  });
			MetadataDatasetSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid,type).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.allAttr = response;
				if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
					var defaultoption={};
					defaultoption.uuid=$scope.selectAttr.uuid;
					defaultoption.name="";
					$scope.allSearchType.defaultoption=defaultoption;
				}else{
					$scope.selectAttr=$scope.allAttr[0]
				}

			}
		}
	}
	
	$scope.onChangeSearchAttr=function(){
		MetadataDatasetSerivce.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid,$scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SubmitSearchAttr=function(){
		console.log($scope.selectDatasetAttr);
		$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType]=$scope.selectAttr;
		$('#searchAttr').modal('hide')
	}

	$scope.onChangeOperator=function(index){
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
		}
		if($scope.filterTableArray[index].operator =='BETWEEN'){
			$scope.filterTableArray[index].rhstype=$scope.rhsType[1];
		//	$scope.disableRhsType(['string','attribute','formula','dataset'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}else if(['EXISTS','NOT EXISTS','IN','NOT IN'].indexOf($scope.filterTableArray[index].operator) !=-1){
			// if(['IN'].indexOf($scope.filterTableArray[index].operator) !=-1){
			// 	$scope.disableRhsType([]);
			// }else{
			// 	$scope.disableRhsType(['string','integer','attribute','formula']);
	 	    // }
			$scope.filterTableArray[index].rhstype=$scope.rhsType[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}else if(['<','>',"<=",'>='].indexOf($scope.filterTableArray[index].operator) !=-1){
           // $scope.disableRhsType(['string','dataset']);
			$scope.filterTableArray[index].rhstype=$scope.rhsType[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}
		else{
			//$scope.disableRhsType(['attribute','formula','dataset']);
			$scope.filterTableArray[index].rhstype=$scope.rhsType[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
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
		filertable.logicalOperator= $scope.filterTableArray.length ==0 ? $scope.logicalOperator[0] :$scope.logicalOperator[1]
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
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				response.splice(0, 1);
				$scope.allFormula = response;
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
			
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.allFormula = response;
				$scope.allFormula.splice(0, 1);
			}
		}
		else if (type == "function") {

			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist=false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = true;

			MetadataDatasetSerivce.getAllLatest("function","N").then(function (response) { onSuccressGetFunction(response.data) });
			var onSuccressGetFunction = function (response) {
				console.log(response)
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
			$scope.filterTableArray[index].isrhsParamlist=true;
			$scope.filterTableArray[index].isrhsFunction = false;
			
		}

	}


	$scope.checkAllAttributeRow = function () {
		angular.forEach($scope.attributeTableArray, function (attribute) {
			attribute.selected = $scope.selectAllAttributeRow;
		});
	}

	$scope.onChangeSimple = function () {
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
		}
	}
	
	// $scope.onChangeOperator=function(){
	// 	if ($scope.datasetCompare != null) {
	// 		$scope.datasetCompare.filterChg = "y"
	// 	}
	// }
  $scope.onChangeAttribute=function(){
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
		}
	}

	$scope.onChangeFromula=function(){
		if ($scope.datasetCompare != null) {
			$scope.datasetCompare.filterChg = "y"
		}
	}
	$scope.addAttribute = function () {
		if ($scope.attributeTableArray == null) {
			$scope.attributeTableArray = [];
		}
		var len = $scope.attributeTableArray.length + 1
		var attrivuteinfo = {};

		attrivuteinfo.name = "attribute" + len;
		attrivuteinfo.id = len - 1;
		attrivuteinfo.sourceAttributeType = $scope.sourceAttributeTypes[0];
		attrivuteinfo.isSourceAtributeSimple = true;
		attrivuteinfo.isSourceAtributeDatapod = false;
		$scope.attributeTableArray.splice($scope.attributeTableArray.length, 0, attrivuteinfo);
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

		}
		else if (type == "datapod") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = true;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
		}
		else if (type == "formula") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = true;
			$scope.attributeTableArray[index].isSourceAtributeExpression = false;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				//alert(JSON.stringify(response))
				$scope.datasetLodeFormula = response

			}
		}
		else if (type == "expression") {

			$scope.attributeTableArray[index].isSourceAtributeSimple = false;
			$scope.attributeTableArray[index].isSourceAtributeDatapod = false;
			$scope.attributeTableArray[index].isSourceAtributeFormula = false;
			$scope.attributeTableArray[index].isSourceAtributeExpression = true;
			$scope.attributeTableArray[index].isSourceAtributeFunction = false;
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
			MetadataDatasetSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.ruleLodeFunction = response
			}

		}

	}
	
	$scope.onChangeSourceName=function(index){
		$scope.attributeTableArray[index].isSourceName=true;
	}

	$scope.onChangeAttributeDatapod = function (data, index) {
		if (data != null && !$scope.attributeTableArray[index].isSourceName) {
			$scope.attributeTableArray[index].name = data.name
		}
	}
	$scope.onChangeFormula = function (data, index) {
		if(!$scope.attributeTableArray[index].isSourceName)
		$scope.attributeTableArray[index].name = data.name
	}

	$scope.onChangeExpression = function (data, index) {
		if(!$scope.attributeTableArray[index].isSourceName)
		$scope.attributeTableArray[index].name = data.name
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
	$scope.submitDataset = function () {
		var upd_tag="N"
		delete $sessionStorage.datasetjosn;
		delete $sessionStorage.index
		delete $sessionStorage.dependon
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datasetHasChanged = true;
		$scope.myform.$dirty = false;
		var dataSetJson = {}
		dataSetJson.uuid = $scope.dataset.uuid
		dataSetJson.name = $scope.dataset.name;
		dataSetJson.desc = $scope.dataset.desc
		dataSetJson.active = $scope.dataset.active;
		dataSetJson.published = $scope.dataset.published;
		dataSetJson.limit = $scope.dataset.limit;
		dataSetJson.srcChg = "y";

		if ($scope.datasetCompare == null) {
			dataSetJson.srcChg = "y";
			dataSetJson.sourceChg = "y";
			dataSetJson.filterChg = "y";
		}
		else {
			dataSetJson.mapInfo = uuid = $scope.datasetCompare.mapInfo
		}


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
		dataSetJson.tags = tagArray;

		//relationInfo
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectSourceType
		ref.uuid = $scope.datasetRelation.defaultoption.uuid
		dependsOn.ref = ref;
		dataSetJson.dependsOn = dependsOn;
		if ($scope.datasetCompare != null && $scope.datasetCompare.dependsOn.ref.uuid != $scope.datasetRelation.defaultoption.uuid) {
			dataSetJson.sourceChg = "y";

		}
		else {

			dataSetJson.sourceChg = "n";

		}


		//filterInfo
		var filterInfoArray = [];
		var filter = {}
		if ($scope.datasetCompare != null && $scope.datasetCompare.filter != null) {
			filter.uuid = $scope.datasetCompare.filter.uuid;
			filter.name = $scope.datasetCompare.filter.name;
			filter.version = $scope.datasetCompare.filter.version;
			filter.createdBy = $scope.datasetCompare.filter.createdBy;
			filter.createdOn = $scope.datasetCompare.filter.createdOn;
			filter.active = $scope.datasetCompare.filter.active;
			filter.tags = $scope.datasetCompare.filter.tags;
			filter.desc = $scope.datasetCompare.filter.desc;
			filter.dependsOn = $scope.datasetCompare.filter.dependsOn;
		}
		if ($scope.filterTableArray.length > 0) {

			for (var i = 0; i < $scope.filterTableArray.length; i++) {

				if ($scope.datasetCompare != null && $scope.datasetCompare.filter != null && $scope.datasetCompare.filter.filterInfo.length == $scope.filterTableArray.length) {
					//  if($scope.datasetCompare.filter.filterInfo[i].operand[0].attributeId != $scope.filterTableArray[i].lhsFilter.attributeId
					// 		 || $scope.filterTableArray[i].logicalOperator !=$scope.datasetCompare.filter.filterInfo[i].logicalOperator
					// 		 || $scope.filterTableArray[i].filtervalue !=$scope.datasetCompare.filter.filterInfo[i].operand[1].value
					// 		 || $scope.filterTableArray[i].operator !=$scope.datasetCompare.filter.filterInfo[i].operator){

					// 	 dataSetJson.filterChg="y";

					//  }
					if ($scope.datasetCompare.filterChg == "y") {
						dataSetJson.filterChg = "y";
					}
					else {
						dataSetJson.filterChg = "n";
					}
				}
				else {

					dataSetJson.filterChg = "y";
				}
				var filterInfo = {};
				var operand = []
				var lhsoperand = {}
				var lhsref = {}
				var rhsoperand = {}
				var rhsref = {};
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
					lhsoperand.value = $scope.filterTableArray[i].lhsvalue;
				}
				else if ($scope.filterTableArray[i].lhstype.text == "datapod") {
					if ($scope.selectSourceType == "dataset") {
						lhsref.type = "dataset";

					}
					else {
						lhsref.type = "datapod";
					}
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
					rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "datapod") {
					if ($scope.selectSourceType == "dataset") {
						rhsref.type = "dataset";

					}
					else {
						rhsref.type = "datapod";
					}
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
				// var filterInfo={};
				// var operand=[];
				// var operandfirst={};
				// var reffirst={};
				// var operandsecond={};
				// var refsecond={};
				// reffirst.type="datapod"

				// reffirst.uuid=$scope.filterTableArray[i].lhsFilter.uuid
				// operandfirst.ref=reffirst;
				// operandfirst.attributeId=$scope.filterTableArray[i].lhsFilter.attributeId
				//   operand[0]=operandfirst;
				// refsecond.type="simple";
				// operandsecond.ref=refsecond;
				// if(typeof $scope.filterTableArray[i].filtervalue == "undefined"){
				// 	operandsecond.value="";
				// }
				// else{

				// 	operandsecond.value=$scope.filterTableArray[i].filtervalue
				// }

				// operand[1]=operandsecond;
				// if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
				// 	filterInfo.logicalOperator=""
				// }
				// else{
				// 	filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
				// }
				// filterInfo.operator=$scope.filterTableArray[i].operator
				// filterInfo.operand=operand;

				filterInfoArray[i] = filterInfo;

			}//End FilterInfo
			filter.filterInfo = filterInfoArray;
			dataSetJson.filter = filter;
		}
		else {
			dataSetJson.filter = null;
			dataSetJson.filterChg = "y";
		}
		var sourceAttributesArray = [];
		for (var l = 0; l < $scope.attributeTableArray.length; l++) {
			attributeinfo = {}
			attributeinfo.attrSourceId = l;
			attributeinfo.attrSourceName = $scope.attributeTableArray[l].name
			//attributeinfo.attributeDesc=$scope.attributeTableArray[l].name
			var ref = {};
			var sourceAttr = {};
			if ($scope.attributeTableArray[l].sourceAttributeType.text == "string") {
				ref.type = "simple";
				sourceAttr.ref = ref;
				sourceAttr.value = $scope.attributeTableArray[l].sourcesimple;
				attributeinfo.sourceAttr = sourceAttr;

			}
			else if ($scope.attributeTableArray[l].sourceAttributeType.text == "datapod") {

				ref.type = $scope.selectSourceType == "relation"?"datapod": $scope.selectSourceType;
				ref.uuid = $scope.attributeTableArray[l].sourcedatapod.uuid;
				sourceAttr.ref = ref;
				sourceAttr.attrId = $scope.attributeTableArray[l].sourcedatapod.attributeId;
				sourceAttr.attrType = $scope.attributeTableArray[l].sourcedatapod.attrType;
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


		MetadataDatasetSerivce.submit(dataSetJson, 'datasetview',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
