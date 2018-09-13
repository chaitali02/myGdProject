DataIngestionModule = angular.module('DataIngestionModule');

DataIngestionModule.controller('IngestRuleDetailController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, IngestRuleService, privilegeSvc,CF_FILTER) {
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
    
    $scope.ruleTypes=[{"text":"FILE-FILE","caption":"File - File"},{"text":"FILE-TABLE","caption":"File - Table"},{"text":"TABLE-TABLE","caption":"Table - Table"},{"text":"TABLE-FILE","caption":"Table - File"}];
    $scope.sourceFormate=["CSV","TSV","PSV"];
    $scope.targetFormate=["CSV","TSV","PSV"];
    $scope.userDetail = {}
    $scope.userDetail.uuid = $rootScope.setUseruuid;
    $scope.userDetail.name = $rootScope.setUserName;
    $scope.tags = null;
    $scope.mode ="false"
    $scope.ingestData;
    $scope.showForm = true;
    $scope.showGraphDiv = false
    $scope.ingest = {};
    $scope.ingest.versions = [];
    $scope.isDependencyShow = false;
    $scope.logicalOperator = ["AND","OR"];
	$scope.spacialOperator = ['<', '>', '<=', '>=', '=', 'LIKE', 'NOT LIKE', 'RLIKE'];
	$scope.operator = CF_FILTER.operator;
    $scope.privileges = [];
    $scope.privileges = privilegeSvc.privileges['ingest'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
    $scope.$on('privilegesUpdated', function (e, data) {
        $scope.privileges = privilegeSvc.privileges['ingest'] || [];
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
		{ "text": "function", "caption": "function", "disabled": false }]
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
    $scope.showPage = function () {
        $scope.showForm = true;
        $scope.showGraphDiv = false
    }
    $scope.enableEdit = function (uuid, version) {
        $scope.showPage()
        $state.go('ingestruledetail', {
            id: uuid,
            version: version,
            mode: 'false'
        });
    }
    $scope.showView = function (uuid, version) {
        if (!$scope.isEdit) {
            $scope.showPage()
            $state.go('ingestruledetail', {
                id: uuid,
                version: version,
                mode: 'true'
            });
        }
    }
    $scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
		$scope.isShowSimpleData = false
	}/*End ShowGraph*/

    var notify = {
        type: 'success',
        title: 'Success',
        content: '',
        timeout: 3000 //time in ms
    };

    
    $scope.close = function () {
        if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
            //revertback
            $state.go($rootScope.previousState.name, $rootScope.previousState.params);
        }
        else {
            $state.go('ingestrulelist');
        }
    }

    
    $scope.getDatasourceForTable=function(sourceType,TargetType){
		IngestRuleService.getDatasourceForTable("datasource").then(function (response) {onSuccessGetDatasourceForTable(response.data)}, function (response) {onError(response.data)});
        var onSuccessGetDatasourceForTable = function (response) {
            if(sourceType =="TABLE"){
                $scope.allSourceDatasource=response;
            }
            if(TargetType == "TABLE"){
                $scope.allTargetDatasource=response;
            }
            
        }
    }

    $scope.getDatasourceForFile=function(sourceType,TargetType){
		IngestRuleService.getDatasourceForFile("datasource").then(function (response) {onSuccessGetDatasourceForFile(response.data)}, function (response) {onError(response.data)});
        var onSuccessGetDatasourceForFile = function (response) {
            if(sourceType =="FILE"){
                $scope.allSourceDatasource=response;
            }
            if(TargetType == "FILE"){
                $scope.allTargetDatasource=response;
            }
            
        }
    }

    $scope.onChangeRuleType=function(){
        $scope.allTargetDatasource=null;
        $scope.allSourceDatasource=null;
        $scope.sourceDetails=null;
        $scope.tagetDetail=null;
        $scope.selectedSourceFormate=null;
        $scope.ingestData.ingestChg="Y";
        $scope.selectedSourceType=$scope.selectedRuleType.split("-")[0];
        $scope.selectedTargetType=$scope.selectedRuleType.split("-")[1];
        $scope.isSourceFormateDisable=$scope.selectedSourceType =='FILE'?false:true;
        $scope.isTargetFormateDisable=$scope.selectedTargetType =='FILE'?false:true
        if($scope.selectedSourceType =='FILE' ||  $scope.selectedTargetType == 'FILE'){
            $scope.getDatasourceForFile($scope.selectedSourceType, $scope.selectedTargetType);  
        }
        if($scope.selectedSourceType =='TABLE' ||  $scope.selectedTargetType == 'TABLE'){
            $scope.getDatasourceForTable($scope.selectedSourceType, $scope.selectedTargetType);  
        }
    }
    
    $scope.onChangeSourceDataSource=function(){
        $scope.ingestData.ingestChg="Y";
        $scope.ingestData.filterChg="Y";
        if($scope.selectedSourceType !="FILE" && $scope.selectedSourceDatasource ){
            $scope.getDatapodByDatasource($scope.selectedSourceDatasource.uuid,"source");
            
        }
    }
    $scope.onChangeTargetDataSource=function(){
        $scope.ingestData.ingestChg="Y";
        if($scope.selectedTargetDatasource){
            $scope.getDatapodByDatasource($scope.selectedTargetDatasource.uuid,"target");
        }
    }

    $scope.onChangeSourceDetail=function(){
        if($scope.selectedSourceType !="FILE"){
            $scope.getAllAttributeBySource();
        }
        $scope.ingestData.ingestChg="Y";
        $scope.ingestData.filterChg="Y";
    }
    
    $scope.onChangeFormate=function(){
        $scope.ingestData.ingestChg="Y";
    }
    $scope.onchangeGroble=function(){
        $scope.ingestData.ingestChg="Y";
    }

    $scope.getDatapodByDatasource=function(uuid,propertyType){
        IngestRuleService.getDatapodByDatasource(uuid).then(function (response) {onSuccessGetDatapodByDatasource(response.data)}, function (response) {onError(response.data)});
        var onSuccessGetDatapodByDatasource = function (response) {
            if(propertyType =="source"){
                $scope.sourceDetails=response;
            }
            if(propertyType =="target"){
                $scope.tagetDetail=response;
            }
        }
    }
    
    $scope.getAllAttributeBySource=function(){
        if($scope.selectedSourceDetail){
            IngestRuleService.getAllAttributeBySource($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) })
            var onSuccessGetAllAttributeBySource = function (response) {
                $scope.sourcedatapodattribute = response;
                $scope.lhsdatapodattributefilter = response;
            }
        }
    }
    
    
    if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		IngestRuleService.getAllVersionByUuid($stateParams.id, "ingest").then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var ingetversion = {};
				ingetversion.version = response[i].version;
				$scope.ingest.versions[i] = ingetversion;
			}
		}
		IngestRuleService.getOneByUuidAndVersion($stateParams.id, $stateParams.version,'ingestview').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
            $scope.ingestData = response.ingestData;
            $scope.ingestCompare = response.ingestData;
			defaultversion.version = response.ingestData.version;
			defaultversion.uuid = response.ingestData.uuid;
            $scope.ingest.defaultVersion = defaultversion;
            $scope.selectedRuleType=$scope.ingestData.type;
            $scope.onChangeRuleType();
            var selectedSourceDatasource={};
            selectedSourceDatasource.type= $scope.ingestData.sourceDatasource.ref.type;
            selectedSourceDatasource.uuid= $scope.ingestData.sourceDatasource.ref.uuid;
            $scope.selectedSourceDatasource=selectedSourceDatasource;
            var selectedTargetDatasource={};
            selectedTargetDatasource.type= $scope.ingestData.targetDatasource.ref.type;
            selectedTargetDatasource.uuid= $scope.ingestData.targetDatasource.ref.uuid;
            $scope.selectedTargetDatasource=selectedTargetDatasource;
            $scope.selectedSourceFormate=$scope.ingestData.sourceFormat;
            $scope.selectedTargetFormate=$scope.ingestData.targetFormat;
            if($scope.selectedSourceType =="FILE"){
                $scope.selectedSourceDetail=$scope.ingestData.sourceDetail.value;
            }
            else{
                $scope.onChangeSourceDataSource();
                var selectedSourceDetail={};
                selectedSourceDetail.type=$scope.ingestData.sourceDetail.ref.type;
                selectedSourceDetail.uuid=$scope.ingestData.sourceDetail.ref.uuid;
                $scope.selectedSourceDetail=selectedSourceDetail;
                
            }
            $scope.onChangeTargetDataSource();
            $scope.filterTableArray = response.filterInfo;
            var selectedTargetDetail={};
            selectedTargetDetail.type=$scope.ingestData.targetDetail.ref.type;
            selectedTargetDetail.uuid=$scope.ingestData.targetDetail.ref.uuid;
            $scope.selectedTargetDetail=selectedTargetDetail;
			var tags = [];
			for (var i = 0; i < response.ingestData.tags.length; i++) {
				var tag = {};
				tag.text = response.ingestData.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
    }//End If
    


    $scope.selectVersion = function () {
        $scope.myform.$dirty = false;
        IngestRuleService.getOneByUuidAndVersion($scope.ingest.defaultVersion.uuid, $scope.ingest.defaultVersion.version,'ingestview').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
            $scope.ingestData = response.ingestData;
            $scope.ingestCompare = response.ingestData;
			defaultversion.version = response.ingestData.version;
			defaultversion.uuid = response.ingestData.uuid;
            $scope.ingest.defaultVersion = defaultversion;
            $scope.selectedRuleType=$scope.ingestData.type;
            $scope.onChangeRuleType();
            var selectedSourceDatasource={};
            $scope.selectedSourceDatasource=null;
            setTimeout(function name(params) {
                selectedSourceDatasource.type= $scope.ingestData.sourceDatasource.ref.type;
                selectedSourceDatasource.uuid= $scope.ingestData.sourceDatasource.ref.uuid;
                $scope.selectedSourceDatasource=selectedSourceDatasource;
            },100);
         
            var selectedTargetDatasource={};
            $scope.selectedTargetDatasource=null;
            setTimeout(function name(params) {
                selectedTargetDatasource.type= $scope.ingestData.targetDatasource.ref.type;
                selectedTargetDatasource.uuid= $scope.ingestData.targetDatasource.ref.uuid;
                $scope.selectedTargetDatasource=selectedTargetDatasource;
                $scope.onChangeTargetDataSource();
            },100);
          
            $scope.selectedSourceFormate=$scope.ingestData.sourceFormat;
            $scope.selectedTargetFormate=$scope.ingestData.targetFormat;
            if($scope.selectedSourceType =="FILE"){
                $scope.selectedSourceDetail=$scope.ingestData.sourceDetail.value;
            }
            else{
                $scope.onChangeSourceDataSource();
                var selectedSourceDetail={};
                $scope.selectedSourceDetail=null;
                setTimeout(function name(params) {
                    selectedSourceDetail.type=$scope.ingestData.sourceDetail.ref.type;
                    selectedSourceDetail.uuid=$scope.ingestData.sourceDetail.ref.uuid;
                    $scope.selectedSourceDetail=selectedSourceDetail;
                },100);
                
                
            }
           
            $scope.selectedTargetDetail=null;
            var selectedTargetDetail={};
            
            setTimeout(function(params) {
                selectedTargetDetail.type=$scope.ingestData.targetDetail.ref.type;
                selectedTargetDetail.uuid=$scope.ingestData.targetDetail.ref.uuid;
                $scope.selectedTargetDetail=selectedTargetDetail;
            },100);
          
            $scope.filterTableArray = response.filterInfo;
			var tags = [];
			for (var i = 0; i < response.ingestData.tags.length; i++) {
				var tag = {};
				tag.text = response.ingestData.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
    }
        
    $scope.SearchAttribute = function (index, type, propertyType) {
		$scope.selectAttr = $scope.filterTableArray[index][propertyType]
		$scope.searchAttr = {};
		$scope.searchAttr.type = type;
		$scope.searchAttr.propertyType = propertyType;
		$scope.searchAttr.index = index;
		IngestRuleService.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
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
			IngestRuleService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
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
		IngestRuleService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SubmitSearchAttr = function () {
		console.log($scope.selectDatasetAttr);
		$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
		$('#searchAttr').modal('hide')
	}

	$scope.onChangeOperator = function (index) {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
		if ($scope.filterTableArray[index].operator == 'BETWEEN') {
			$scope.filterTableArray[index].rhstype = $scope.rhsType[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['EXISTS', 'NOT EXISTS', 'IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
			
			$scope.filterTableArray[index].rhstype = $scope.rhsType[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhstype = $scope.rhsType[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else {
			$scope.filterTableArray[index].rhstype = $scope.rhsType[0];
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
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
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
			IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				response.splice(0, 1);
				$scope.allFormula = response;
			}
		}
	}


	$scope.selectrhsType = function (type, index) {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
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

			IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid, "datapod").then(function (response) { onSuccressGetFormula(response.data) });
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
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = true;

			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
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
			$scope.filterTableArray[index].isrhsParamlist = true;
			$scope.filterTableArray[index].isrhsFunction = false;
			$scope.getParamByApp();
		}

	}
   
	$scope.getParamByApp=function(){
		
		CommonService.getParamByApp($rootScope.appUuidd || "", "application").
		then(function (response) { onSuccessGetParamByApp(response.data)});
		var onSuccessGetParamByApp=function(response){
		  $scope.allparamlistParams=[];
		  if(response.length >0){
			var paramsArray = [];
			for(var i=0;i<response.length;i++){
			  var paramjson={}
			  var paramsjson = {};
			  paramsjson.uuid = response[i].ref.uuid;
			  paramsjson.name = response[i].ref.name + "." + response[i].paramName;
			  paramsjson.attributeId = response[i].paramId;
			  paramsjson.attrType = response[i].paramType;
			  paramsjson.paramName = response[i].paramName;
			  paramsjson.caption = "app."+paramsjson.paramName
			  paramsArray[i] = paramsjson
			}
			$scope.allparamlistParams=paramsArray;
		  }
		}
      }
      

	$scope.onChangeSimple = function () {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
	}

	$scope.onChangeAttribute = function () {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
	}

	$scope.onChangeFromula = function () {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
	}

	$scope.onChangeRhsParamList=function(){
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
	}

    $scope.submit =function(){
        $scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
        var ingestJson={};
        ingestJson.uuid=$scope.ingestData.uuid;
		ingestJson.name = $scope.ingestData.name;
		ingestJson.desc = $scope.ingestData.desc;
		ingestJson.active = $scope.ingestData.active;
        ingestJson.published = $scope.ingestData.published;
        ingestJson.runParams=$scope.ingestData.runParams;
        if ($scope.ingestCompare == null) {
			ingestJson.ingestChg = "Y";
			ingestJson.filterChg = "Y";
		}else{
            if($scope.ingestData.ingestChg=="Y"){
                ingestJson.ingestChg="Y";
            }
            ingestJson.ingestChg = "N";
        }
        var upd_tag="N";
        var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result=(tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y";
			}
		}
        ingestJson.tags = tagArray;
        ingestJson.type=$scope.selectedRuleType;
        ingestJson.sourceFormat=$scope.selectedSourceFormate;
        var sourceDatasource={};
        var sourceDatasourceRef={};
        sourceDatasourceRef.uuid=$scope.selectedSourceDatasource.uuid
        sourceDatasourceRef.type="datasource"; 
        sourceDatasource.ref=sourceDatasourceRef;
        ingestJson.sourceDatasource=sourceDatasource;
        var sourceDetails={};
        var sourceDetailsRef={};
        if($scope.selectedSourceType =="FILE"){
            sourceDetailsRef.type="simple";
            sourceDetails.ref=sourceDetailsRef;
            sourceDetails.value=$scope.selectedSourceDetail;
        }else{
            sourceDetailsRef.type="datapod";
            sourceDetailsRef.uuid=$scope.sourceDetails.uuid;
            sourceDetails.ref=sourceDetailsRef;
        }
        ingestJson.sourceDetail=sourceDetails;
        var targetDatasource={};
        var targetDatasourceRef={};
        targetDatasourceRef.uuid=$scope.selectedTargetDatasource.uuid;
        targetDatasourceRef.type="datasource"; 
        targetDatasource.ref=targetDatasourceRef;
        ingestJson.targetDatasource=targetDatasource;
        ingestJson.targetFormat=$scope.selectedTargetFormate;
        var targetDetails={};
        var targetDetailsRef={};       
        targetDetailsRef.type="datapod";
        targetDetailsRef.uuid=$scope.selectedTargetDetail.uuid;
        targetDetails.ref=targetDetailsRef;
        ingestJson.targetDetail=targetDetails;

        //filterInfo
		var filterInfoArray = [];
		var filter = {};
		if ($scope.ingestCompare != null && $scope.ingestCompare.filter != null) {
			filter.uuid = $scope.ingestCompare.filter.uuid;
			filter.name = $scope.ingestCompare.filter.name;
			filter.version = $scope.ingestCompare.filter.version;
			filter.createdBy = $scope.ingestCompare.filter.createdBy;
			filter.createdOn = $scope.ingestCompare.filter.createdOn;
			filter.active = $scope.ingestCompare.filter.active;
			filter.tags = $scope.ingestCompare.filter.tags;
			filter.desc = $scope.ingestCompare.filter.desc;
			filter.dependsOn = $scope.ingestCompare.filter.dependsOn;
        }
        if ($scope.filterTableArray.length > 0) {
			for (var i = 0; i < $scope.filterTableArray.length; i++) {
				if ($scope.ingestCompare != null && $scope.ingestCompare.filter != null && $scope.ingestCompare.filter.filterInfo.length == $scope.filterTableArray.length) {
					if ($scope.ingestCompare.filterChg == "y") {
						ingestJson.filterChg = "y";
					}
					else {
						ingestJson.filterChg = "n";
					}
				}
				else {
					ingestJson.filterChg = "y";
				}
				var filterInfo = {};
				var operand = []
				var lhsoperand = {};;
				var lhsref = {};
				var rhsoperand = {};
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
					lhsref.type=$scope.filterTableArray[i].lhsdatapodAttribute.type
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
					rhsref.type =$scope.filterTableArray[i].rhsdatapodAttribute.type;
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
			filter.filterInfo = filterInfoArray;
			ingestJson.filter = filter;
		}
		else {
			ingestJson.filter = null;
			ingestJson.filterChg = "y";
        }
        if($scope.ingestData.filterChg=="Y"){
            ingestJson.filterChg = "y";
        }
        
        console.log(JSON.stringify(ingestJson))
        IngestRuleService.submit(ingestJson, 'ingestview',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Rule Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.oksave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}
    
    $scope.oksave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('ingestrulelist'); }, 2000);
		}
	}

});