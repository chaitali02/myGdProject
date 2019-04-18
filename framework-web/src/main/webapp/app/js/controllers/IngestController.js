DataIngestionModule = angular.module('DataIngestionModule');

DataIngestionModule.controller('IngestRuleDetailController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, IngestRuleService, privilegeSvc, CF_FILTER) {
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

	$scope.ruleTypes = [{ "text": "FILE-FILE", "caption": "File - File" }, { "text": "FILE-TABLE", "caption": "File - Table" }, { "text": "TABLE-TABLE", "caption": "Table - Table" }, { "text": "TABLE-FILE", "caption": "Table - File" },{ "text": "STREAM-FILE", "caption": "Stream - File" },{ "text": "STREAM-TABLE", "caption": "Stream - Table" }];
	$scope.sourceFormate = ["CSV", "TSV", "PSV", "PARQUET"];
	$scope.targetFormate = ["CSV", "TSV", "PSV", "PARQUET"];
	$scope.saveModeTable=["APPEND","OVERWRITE"];
	$scope.saveModeFile=["OVERWRITE"];
	$scope.selectedRuleType=$scope.ruleTypes[0].text;

	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.tags = null;
	$scope.mode = "false"
	$scope.ingestData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.filterTableArray = [];
	$scope.ingest = {};
	$scope.ingest.versions = [];
	$scope.isDependencyShow = false;
	$scope.logicalOperator = ["AND", "OR"];
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
		//if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('ingestruledetail', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		//}
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

    
	$scope.getDatasourceForTable = function (sourceType, TargetType) {
		IngestRuleService.getDatasourceForTable("datasource").then(function (response) { onSuccessGetDatasourceForTable(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatasourceForTable = function (response) {
			if (sourceType == "TABLE") {
				$scope.allSourceDatasource = response;
				if($scope.selectedSourceType == 'TABLE' && $scope.selectedTargetType == 'FILE'){
					if($scope.allSourceDatasource && $scope.allSourceDatasource.length >0){
						for(var i=0;i<$scope.allSourceDatasource.length;i++){
							if($scope.allSourceDatasource[i].type == 'HIVE'){
								if($scope.allTargetDatasource)
									$scope.allTargetDatasource.push($scope.allSourceDatasource[i])
								else{
									$scope.allTargetDatasource=[];
									$scope.allTargetDatasource.push($scope.allSourceDatasource[i])
								}
							}
						}
					}
				}
			}
			if (TargetType == "TABLE") {
				$scope.allTargetDatasource = response;
				if($scope.selectedSourceType == 'FILE' && $scope.selectedTargetType == 'TABLE'){
					if($scope.allTargetDatasource && $scope.allTargetDatasource.length >0){
						for(var i=0;i<$scope.allTargetDatasource.length;i++){
							if($scope.allTargetDatasource[i].type == 'HIVE'){
								if($scope.allSourceDatasource)
									$scope.allSourceDatasource.push($scope.allTargetDatasource[i]);
								else{
									$scope.allSourceDatasource=[];
									$scope.allSourceDatasource.push($scope.allTargetDatasource[i]);
								}
							}
						}
						/*for(var i=0;i<$scope.allSourceDatasource.length;i++){
							if($scope.allSourceDatasource[i].type == 'FILE'){
								if($scope.allSourceDatasource)
									$scope.allTargetDatasource.push($scope.allSourceDatasource[i]);
								else{
									$scope.allSourceDatasource=[];
									$scope.allTargetDatasource.push($scope.allSourceDatasource[i]);
								}
							}
						}*/
					}	
				}
			}
			
			if(sourceType =="STREAM" && TargetType =="FILE"){
				if(response && response.length >0){
					for(var i=0;i<response.length;i++){
						if(response[i].type == 'HIVE'){
							if($scope.allTargetDatasource)
								$scope.allTargetDatasource.push(response[i])
							else{
								$scope.allTargetDatasource=[];
								$scope.allTargetDatasource.push(response[i])
							}
						}
					}
				}
			}


		}
	}
	$scope.getDatasourceForStream = function (sourceType, TargetType) {
		IngestRuleService.getDatasourceForStream("datasource").then(function (response) { onSuccessGetDatasourceForTable(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatasourceForTable = function (response) {
			if (sourceType == "STREAM") {
				$scope.allSourceDatasource = response;
			}
			if (TargetType == "STREAM") {
				$scope.allTargetDatasource = response;
			}
		}
	}
	// $scope.getAllAttributeBySource=function(){
	// 	MetadataMapSerivce.getAllAttributeBySource($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
	// 	var onSuccessGetAllAttributeBySourcet = function (response) {
	// 		
	// 		$scope.allSourceAttribute = response;

	// 	}
	// }
	
	$scope.getDatasourceForFile = function (sourceType, TargetType) {
		IngestRuleService.getDatasourceForFile("datasource").then(function (response) { onSuccessGetDatasourceForFile(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatasourceForFile = function (response) {
			if (sourceType == "FILE") {
				$scope.allSourceDatasource = response;
			}
			if (TargetType == "FILE") {
				$scope.allTargetDatasource = response;
			}
            
		}
	}

	$scope.onChangeRuleType = function () {
		$scope.allTargetDatasource = null;
		$scope.allSourceDatasource = null;
		$scope.sourceDetails = null;
		$scope.tagetDetail = null;
		$scope.selectedSourceFormate = null;
		$scope.selectedTargetFormate = null;
		$scope.selectedSourceAttrDetail=null;
		$scope.ingestData.ingestChg = "Y";
		$scope.selectedSourceType = $scope.selectedRuleType.split("-")[0];
		$scope.selectedTargetType = $scope.selectedRuleType.split("-")[1];
		$scope.isSourceFormateDisable = $scope.selectedSourceType == 'FILE' ? false : true;
		$scope.isTargetFormateDisable = $scope.selectedTargetType == 'FILE' ? false : true
		if ($scope.selectedSourceType == 'FILE' || $scope.selectedTargetType == 'FILE') {
			$scope.getDatasourceForFile($scope.selectedSourceType, $scope.selectedTargetType);
		}
		if ($scope.selectedSourceType == 'TABLE' || $scope.selectedTargetType == 'TABLE') {
			$scope.getDatasourceForTable($scope.selectedSourceType, $scope.selectedTargetType);
			
			
		}
		if ($scope.selectedSourceType == 'STREAM' || $scope.selectedTargetType == 'STREAM') {
			$scope.getDatasourceForStream($scope.selectedSourceType, $scope.selectedTargetType);
		}
		

		if ($scope.selectedSourceType == 'STREAM' && $scope.selectedTargetType == 'FILE') {
			$scope.getDatasourceForTable($scope.selectedSourceType,$scope.selectedTargetType);	
		}
	
	}

	// $scope.getLatestByUuid=function(uuid,type,propertyType){
	// 	IngestRuleService.getLatestByUuid(uuid,type).then(function (response) { onSuccessGetLatestByUuid(response.data) })
	// 	var onSuccessGetLatestByUuid= function (response) {
	// 		if(propertyType =='source'){
	// 			$scope.selectedSourceDatasource.type=response.type;
	// 			if($scope.selectedSourceDatasource && $scope.selectedSourceDatasource.type == 'HIVE'){
	// 				$scope.isSourceFormateDisable=true;
	// 				$scope.selectedSourceFormate = null;
	// 			}else{
	// 				$scope.isSourceFormateDisable=false;
	// 				$scope.selectedSourceFormate = null;
	// 			}
				
	// 		}
	// 		if(propertyType =='target'){
	// 			$scope.selectedTargetDatasource.type=response.type;
	// 			if( $scope.selectedTargetDatasource && $scope.selectedTargetDatasource.type == 'HIVE'){
	// 				$scope.isTargetFormateDisable=true;
	// 				$scope.selectedTargetFormate = null;
	// 			}else{
	// 				$scope.isTargetFormateDisable=false;
	// 				$scope.selectedTargetFormate = null;
	// 			}
	// 		}
	// 	}
	// }

	$scope.onChangeSourceDataSource = function () {
		if(!$scope.selectedSourceDatasource){
			return false;
		}
		$scope.ingestData.ingestChg = "Y";
		$scope.ingestData.filterChg = "Y";
		if ($scope.selectedSourceType != "FILE" && $scope.selectedSourceType != "STREAM" && $scope.selectedSourceDatasource) {
			$scope.getDatapodByDatasource($scope.selectedSourceDatasource.uuid, "source");
            
		}
		else if($scope.selectedSourceType == "STREAM" &&  $scope.selectedSourceDatasource){
			$scope.getTopicList($scope.selectedSourceDatasource.uuid,$scope.selectedSourceDatasource.version, "source");
		}
		else{
			if($scope.selectedSourceDatasource && $scope.selectedSourceDatasource.type != 'FILE'){
				$scope.isSourceFormateDisable=true;
				$scope.selectedSourceFormate = null;
			}else{
				$scope.isSourceFormateDisable=false;
				$scope.selectedSourceFormate = null;
			}
		}
	
	}
	$scope.onChangeTargetDataSource = function () {
		$scope.ingestData.ingestChg = "Y";
		if ($scope.selectedTargetDatasource) {
			$scope.getDatapodByDatasource($scope.selectedTargetDatasource.uuid, "target");
			if( $scope.selectedTargetDatasource && $scope.selectedTargetDatasource.type != 'FILE'){
				$scope.isTargetFormateDisable=true;
				$scope.selectedTargetFormate = null;
			}
			else{
				$scope.isTargetFormateDisable=false;
			    $scope.selectedTargetFormate = null;
			}
		}
		else if($scope.selectedTargetType != "STREAM" &&  $scope.selectedTargetDatasource){
			$scope.getTopicList($scope.selectedTargetDatasource.uuid,$scope.selectedTargetDatasource.version, "target");
		}
	
	
	}

	$scope.onChangeSourceDetail = function () {
		if ($scope.selectedSourceType != "FILE") {
			$scope.getAllAttributeBySource();
		}
		$scope.ingestData.ingestChg = "Y";
		$scope.ingestData.filterChg = "Y";
	}

	$scope.onChangeFormate = function () {
		$scope.ingestData.ingestChg = "Y";
		if($scope.selectedSourceFormate !="PARQUET"){
			$scope.ingestData.sourceExtn=$scope.selectedSourceFormate;
			$scope.ingestData.sourceExtn=$scope.ingestData.sourceExtn.toLowerCase();
		}
		else{
			$scope.ingestData.sourceExtn=null;
		}
	}
	$scope.onChangeTargetFormate = function () {
		$scope.ingestData.ingestChg = "Y";
		if($scope.selectedTargetFormate !="PARQUET"){
			$scope.ingestData.targetExtn=$scope.selectedTargetFormate;
			$scope.ingestData.targetExtn=$scope.ingestData.targetExtn.toLowerCase();
		}
		else{
			$scope.ingestData.targetExtn=null;
		}
	}  
	$scope.onchangeGroble = function () {
		$scope.ingestData.ingestChg = "Y";
	}
	
	
    $scope.getTopicList = function (uuid,version,propertyType) {
		IngestRuleService.getTopicList(uuid,version|| "").then(function (response) { onSuccessGetDatapodByDatasource(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatapodByDatasource = function (response) {
			if (propertyType == "source") {
				$scope.sourceDetails = response;
			}
			if (propertyType == "target") {
				$scope.tagetDetail = response;
			}
		}
	}

	$scope.getDatapodByDatasource = function (uuid, propertyType) {
		IngestRuleService.getDatapodByDatasource(uuid).then(function (response) { onSuccessGetDatapodByDatasource(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatapodByDatasource = function (response) {
			if (propertyType == "source") {
				$scope.sourceDetails = response;
			}
			if (propertyType == "target") {
				$scope.tagetDetail = response;
			}
		}
	}

	$scope.getAllAttributeBySource = function () {
		if ($scope.selectedSourceDetail) {
			IngestRuleService.getAllAttributeBySource($scope.selectedSourceDetail.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) })
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.allSourceAttribute = response;
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
		IngestRuleService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'ingestview').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.ingestData = response.ingestData;
			$scope.ingestCompare = response.ingestData;
			defaultversion.version = response.ingestData.version;
			defaultversion.uuid = response.ingestData.uuid;
			$scope.ingest.defaultVersion = defaultversion;
			$scope.selectedRuleType = $scope.ingestData.type;
			$scope.onChangeRuleType();
			var selectedSourceDatasource = {};
			selectedSourceDatasource.type = $scope.ingestData.sourceDatasource.ref.type;
			selectedSourceDatasource.uuid = $scope.ingestData.sourceDatasource.ref.uuid;
			$scope.selectedSourceDatasource = selectedSourceDatasource;

			var selectedTargetDatasource = {};
			selectedTargetDatasource.type = $scope.ingestData.targetDatasource.ref.type;
			selectedTargetDatasource.uuid = $scope.ingestData.targetDatasource.ref.uuid;
			$scope.selectedTargetDatasource = selectedTargetDatasource;			
			$scope.selectedSourceFormate = $scope.ingestData.sourceFormat;
				if($scope.selectedSourceFormate !=null){
					$scope.isSourceFormateDisable=false;
				}else{
					$scope.isSourceFormateDisable=true;				
				}
			$scope.selectedTargetFormate = $scope.ingestData.targetFormat;
			if ($scope.selectedSourceType == "FILE" || $scope.selectedSourceType == "STREAM") {
				$scope.selectedSourceDetail = $scope.ingestData.sourceDetail.value;
				if($scope.selectedSourceType == "STREAM"){
					$scope.onChangeSourceDataSource();
				}
			}
			else {
				$scope.onChangeSourceDataSource();
				$scope.selectedSourceFormate = $scope.ingestData.sourceFormat;
				if($scope.selectedSourceFormate !=null){
					$scope.isSourceFormateDisable=false;
				}else{
					$scope.isSourceFormateDisable=true;				
				}
				var selectedSourceDetail = {};
				selectedSourceDetail.type = $scope.ingestData.sourceDetail.ref.type;
				selectedSourceDetail.uuid = $scope.ingestData.sourceDetail.ref.uuid;
				$scope.selectedSourceDetail = selectedSourceDetail;
				$scope.getAllAttributeBySource();
				$scope.getParamByApp();
				var selectedSourceAttrDetail={};
				selectedSourceAttrDetail.uuid=$scope.ingestData.incrAttr.ref.uuid;
				selectedSourceAttrDetail.attributeId=$scope.ingestData.incrAttr.attrId;
				$scope.selectedSourceAttrDetail=selectedSourceAttrDetail;
				var selectedSplitBy={};
				selectedSplitBy.uuid=$scope.ingestData.splitBy.ref.uuid;
				selectedSplitBy.attributeId=$scope.ingestData.splitBy.attrId;
				$scope.selectedSplitBy=selectedSplitBy;
			}

			if($scope.selectedTargetFormate !=null){
				$scope.isTargetFormateDisable=false;
			}else{
				$scope.isTargetFormateDisable=true;				
			}


			if ($scope.selectedTargetType == "FILE") {
				$scope.selectedTargetDetail = $scope.ingestData.targetDetail.value;
			}
			else {
				$scope.onChangeTargetDataSource();
				$scope.selectedTargetFormate = $scope.ingestData.targetFormat;
				if($scope.selectedTargetFormate !=null){
					$scope.isTargetFormateDisable=false;
		    	}else{
					$scope.isTargetFormateDisable=true;				
				}
				var selectedTargetDetail = {};
				selectedTargetDetail.type = $scope.ingestData.targetDetail.ref.type;
				selectedTargetDetail.uuid = $scope.ingestData.targetDetail.ref.uuid;
				$scope.selectedTargetDetail = selectedTargetDetail;
			}
			$scope.filterTableArray = response.filterInfo;
			var tags = [];
			for (var i = 0; i < response.ingestData.tags.length; i++) {
				var tag = {};
				tag.text = response.ingestData.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
	}//End If
    else{
		$scope.ingestData={};
		$scope.ingestData.header="N"
		$scope.onChangeRuleType();
	}


	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		IngestRuleService.getOneByUuidAndVersion($scope.ingest.defaultVersion.uuid, $scope.ingest.defaultVersion.version, 'ingestview').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.ingestData = response.ingestData;
			$scope.ingestCompare = response.ingestData;
			defaultversion.version = response.ingestData.version;
			defaultversion.uuid = response.ingestData.uuid;
			$scope.ingest.defaultVersion = defaultversion;
			$scope.selectedRuleType = $scope.ingestData.type;
			$scope.onChangeRuleType();
			var selectedSourceDatasource = {};
			$scope.selectedSourceDatasource = null;
			setTimeout(function name(params) {
				selectedSourceDatasource.type = $scope.ingestData.sourceDatasource.ref.type;
				selectedSourceDatasource.uuid = $scope.ingestData.sourceDatasource.ref.uuid;
				$scope.selectedSourceDatasource = selectedSourceDatasource;
				$scope.onChangeSourceDataSource();
			}, 100);

			var selectedTargetDatasource = {};
			$scope.selectedTargetDatasource = null;
			setTimeout(function name(params) {
				selectedTargetDatasource.type = $scope.ingestData.targetDatasource.ref.type;
				selectedTargetDatasource.uuid = $scope.ingestData.targetDatasource.ref.uuid;
				$scope.selectedTargetDatasource = selectedTargetDatasource;
				$scope.onChangeTargetDataSource();
			}, 100);

			$scope.selectedSourceFormate = $scope.ingestData.sourceFormat;
			$scope.selectedTargetFormate = $scope.ingestData.targetFormat;
			
			if ($scope.selectedSourceType == "FILE" || $scope.selectedSourceType == "STREAM") {
				$scope.selectedSourceDetail=null;
				if($scope.selectedSourceType == "STREAM"){
					$scope.onChangeSourceDataSource();
				}
				setTimeout(function name() {
					$scope.selectedSourceDetail = $scope.ingestData.sourceDetail.value;
				})
				
			}
			else {
			//	$scope.onChangeSourceDataSource();
				var selectedSourceDetail = {};
				$scope.selectedSourceDetail = null;
				$scope.selectedSourceAttrDetail=null;
				setTimeout(function name(params) {
					selectedSourceDetail.type = $scope.ingestData.sourceDetail.ref.type;
					selectedSourceDetail.uuid = $scope.ingestData.sourceDetail.ref.uuid;
					$scope.selectedSourceDetail = selectedSourceDetail;
					$scope.getAllAttributeBySource();
					var selectedSourceAttrDetail={};
					selectedSourceAttrDetail.uuid=$scope.ingestData.incrAttr.ref.uuid;
					selectedSourceAttrDetail.attributeId=$scope.ingestData.incrAttr.attrId;
					$scope.selectedSourceAttrDetail=selectedSourceAttrDetail;
				}, 100);


			}

			$scope.selectedTargetDetail = null;
			var selectedTargetDetail = {};

			setTimeout(function () {
				if ($scope.selectedTargetType == "FILE") {
					$scope.selectedTargetDetail = $scope.ingestData.targetDetail.value;
				}
				else {
					selectedTargetDetail.type = $scope.ingestData.targetDetail.ref.type;
					selectedTargetDetail.uuid = $scope.ingestData.targetDetail.ref.uuid;
					$scope.selectedTargetDetail = selectedTargetDetail;
			    }
			}, 100);

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
			IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid, "datapod").then(function (response) { onSuccressGetFormula(response.data) });
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

	$scope.onChangeRhsParamList = function () {
		if ($scope.ingestCompare != null) {
			$scope.ingestCompare.filterChg = "y"
		}
	}

	$scope.submit = function () {
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		var ingestJson = {};
		ingestJson.uuid = $scope.ingestData.uuid;
		ingestJson.name = $scope.ingestData.name;
		ingestJson.desc = $scope.ingestData.desc;
		ingestJson.active = $scope.ingestData.active;
		ingestJson.published = $scope.ingestData.published;
		ingestJson.runParams = $scope.ingestData.runParams;
		ingestJson.header = $scope.ingestData.header;
		ingestJson.ignoreCase= $scope.ingestData.ignoreCase;
		ingestJson.sourceExtn=$scope.ingestData.sourceExtn;
		ingestJson.targetExtn=$scope.ingestData.targetExtn;
		if($scope.ingestData.saveMode){
			ingestJson.saveMode = $scope.ingestData.saveMode;
		}
		else{
			ingestJson.saveMode=null;
		}
		if ($scope.ingestCompare == null) {
			ingestJson.ingestChg = "Y";
			ingestJson.filterChg = "Y";
		} else {
			if ($scope.ingestData.ingestChg == "Y") {
				ingestJson.ingestChg = "Y";
			}
			ingestJson.ingestChg = "N";
		}
		var upd_tag = "N";
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y";
			}
		}
		ingestJson.tags = tagArray;
		ingestJson.type = $scope.selectedRuleType;
		ingestJson.sourceFormat = $scope.selectedSourceFormate;
		var sourceDatasource = {};
		var sourceDatasourceRef = {};
		sourceDatasourceRef.uuid = $scope.selectedSourceDatasource.uuid
		sourceDatasourceRef.type = "datasource";
		sourceDatasource.ref = sourceDatasourceRef;
		ingestJson.sourceDatasource = sourceDatasource;
		var sourceDetails = {};
		var sourceDetailsRef = {};
		if ($scope.selectedSourceType == "FILE" || $scope.selectedSourceType == "STREAM") {
			sourceDetailsRef.type = "simple";
			sourceDetails.ref = sourceDetailsRef;
			sourceDetails.value = $scope.selectedSourceDetail;
		} else {
			sourceDetailsRef.type = "datapod";
			sourceDetailsRef.uuid = $scope.selectedSourceDetail.uuid;
			sourceDetails.ref = sourceDetailsRef;
		}
		ingestJson.sourceDetail = sourceDetails;
        if ($scope.selectedSourceType != "FILE" && $scope.selectedSourceType != "STREAM") {
			var sourceAttrDetail={};
			var sourceAttrDetailRef={};
			sourceAttrDetailRef.type="datapod";
			sourceAttrDetailRef.uuid=$scope.selectedSourceAttrDetail.uuid;
			sourceAttrDetail.ref=sourceAttrDetailRef;
			sourceAttrDetail.attrId=$scope.selectedSourceAttrDetail.attributeId;
			ingestJson.incrAttr=sourceAttrDetail;
			var splitBy={};
			var splitByRef={};
			splitByRef.type="datapod";
			splitByRef.uuid=$scope.selectedSplitBy.uuid;
			splitBy.ref=splitByRef;
			splitBy.attrId=$scope.selectedSplitBy.attributeId;
			ingestJson.splitBy=splitBy;
		}else{
			ingestJson.incrAttr=null;
			ingestJson.splitBy=null;
		}
		var targetDatasource = {};
		var targetDatasourceRef = {};
		targetDatasourceRef.uuid = $scope.selectedTargetDatasource.uuid;
		targetDatasourceRef.type = "datasource";
		targetDatasource.ref = targetDatasourceRef;
		ingestJson.targetDatasource = targetDatasource;
		ingestJson.targetFormat = $scope.selectedTargetFormate;
		var targetDetails = {};
		var targetDetailsRef = {};
		if ($scope.selectedTargetType == "FILE") {
			targetDetailsRef.type = "simple";
			targetDetails.ref = targetDetailsRef;
			targetDetails.value = $scope.selectedTargetDetail;
		
		} else {
			targetDetailsRef.type = "datapod";
			targetDetailsRef.uuid = $scope.selectedTargetDetail.uuid;
			targetDetails.ref = targetDetailsRef;
			
		}
		ingestJson.targetDetail = targetDetails;

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
					lhsref.type = $scope.filterTableArray[i].lhsdatapodAttribute.type
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
			filter.filterInfo = filterInfoArray;
			ingestJson.filter = filter;
		}
		else {
			ingestJson.filter = null;
			ingestJson.filterChg = "y";
		}
		if ($scope.ingestData.filterChg == "Y") {
			ingestJson.filterChg = "y";
		}

		console.log(JSON.stringify(ingestJson))
		IngestRuleService.submit(ingestJson, 'ingestview', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

DataIngestionModule.controller('DetailRuleGroupController', function ($state, $timeout, $filter, $stateParams, $rootScope, $scope, RuleGroupService, privilegeSvc, dagMetaDataService, CommonService) {

	$scope.select = 'rules group';
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
	$scope.showForm = true;
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.mode = " ";
	$scope.rulegroup = {};
	$scope.rulegroup.versions = []
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['ingestgroup'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['ingestgroup'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
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
	$scope.onChangeName = function (data) {
		$scope.ruleGroupDetail.displayName=data;	
	}

	$scope.close = function () {
		if ($stateParams.returnBack == "true" && $rootScope.previousState) {
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		} else {
			$scope.statedetail = {};
			$scope.statedetail.name = dagMetaDataService.elementDefs['ingestgroup'].listState
			$scope.statedetail.params = {}
			$state.go($scope.statedetail.name, $scope.statedetail.params)
		}
	}

	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false;

	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}
    $scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('ingestrulegroupdetail', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.ruleGroupDetail.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('ingestrulegroupdetail', {
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
			$state.go('ingestrulegroupdetail', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}

	RuleGroupService.getAllLatest('ingest').then(function (response) { onSuccess(response.data) });
	var onSuccess = function (response) {
		var rullArray = [];
		for (var i = 0; i < response.data.length; i++) {
			var rulljosn = {};
			rulljosn.uuid = response.data[i].uuid;
			rulljosn.id = response.data[i].uuid //+ "_" + response.data[i].version
			rulljosn.name = response.data[i].name;
			rulljosn.displayName = response.data[i].displayName;
			rulljosn.version = response.data[i].version;
			rullArray[i] = rulljosn;
		}
		$scope.rullall = rullArray;
	}


	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
        $scope.isEditVeiwError=false;
		RuleGroupService.getAllVersionByUuid($stateParams.id, "ingestgroup")
			.then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var rulegroupversion = {};
				rulegroupversion.version = response[i].version;
				$scope.rulegroup.versions[i] = rulegroupversion;
			}
		}
		RuleGroupService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'ingestgroup')
			.then(function (response) { onsuccess(response.data) },function(response) {onError(response.data)});
		var onsuccess = function (response) {
			$scope.isEditInprogess=false;
			$scope.ruleGroupDetail = response;
			$scope.tags = response.tags
			$scope.checkboxModelparallel = response.inParallel;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.rulegroup.defaultVersion = defaultversion;
			var ruleTagArray = [];
			for (var i = 0; i < response.ruleInfo.length; i++) {
				var ruletag = {};
				ruletag.uuid = response.ruleInfo[i].ref.uuid;
				ruletag.name = response.ruleInfo[i].ref.name;
				ruletag.displayName = response.ruleInfo[i].ref.displayName;
				ruletag.id = response.ruleInfo[i].ref.uuid// + "_" + response.ruleInfo[i].ref.version;
				ruletag.version = response.ruleInfo[i].ref.version;
				ruleTagArray[i] = ruletag;
			}
			$scope.ruleTags = ruleTagArray
		};
		var onError =function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}else{
		$scope.ruleGroupDetail={};
		$scope.ruleGroupDetail.locked="N";
	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
        $scope.isEditVeiwError=false;
		RuleGroupService.getOneByUuidAndVersion($scope.rulegroup.defaultVersion.uuid, $scope.rulegroup.defaultVersion.version, 'ingestgroup')
			.then(function (response) { onsuccess(response.data)},function(response) {onError(response.data)});
		var onsuccess = function (response) {
			$scope.isEditInprogess=false;
			$scope.ruleGroupDetail = response;
			$scope.tags = response.tags
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.rulegroup.defaultVersion = defaultversion;
			var ruleTagArray = [];
			for (var i = 0; i < response.ruleInfo.length; i++) {
				var ruletag = {};
				ruletag.uuid = response.ruleInfo[i].ref.uuid;
				ruletag.name = response.ruleInfo[i].ref.name;
				ruletag.id = response.ruleInfo[i].ref.uuid
				ruletag.version = response.ruleInfo[i].ref.version;
				ruleTagArray[i] = ruletag;
			}
			$scope.ruleTags = ruleTagArray
		};
		var onError =function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}

	$scope.loadRules = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.rullall, query);
		});
	};

	$scope.oksave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () {
				$state.go('ingestrulegrouplist');
			}, 2000);
		}
	}

	$scope.submitRuleGroup = function () {
		var upd_tag = "N"
		$scope.isSubmitProgess = true;
		$scope.myform.$dirty = false;
		var options = {}
		options.execution = $scope.checkboxModelexecution;
		var ruleGroupJson = {}
		ruleGroupJson.uuid = $scope.ruleGroupDetail.uuid;
		ruleGroupJson.name = $scope.ruleGroupDetail.name;
		ruleGroupJson.displayName = $scope.ruleGroupDetail.displayName;
		ruleGroupJson.desc = $scope.ruleGroupDetail.desc;
		ruleGroupJson.active = $scope.ruleGroupDetail.active;
		ruleGroupJson.locked = $scope.ruleGroupDetail.locked;
		ruleGroupJson.published = $scope.ruleGroupDetail.published;
		ruleGroupJson.publicFlag = $scope.ruleGroupDetail.publicFlag;
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
		ruleGroupJson.tags = tagArray;
		var ruleInfoArray = [];
		for (var i = 0; i < $scope.ruleTags.length; i++) {
			var ruleInfo = {}
			var ref = {};
			ref.type = "ingest"
			ref.uuid = $scope.ruleTags[i].uuid;
			ruleInfo.ref = ref;
			ruleInfoArray[i] = ruleInfo;
		}

		ruleGroupJson.ruleInfo = ruleInfoArray;
		ruleGroupJson.inParallel = $scope.checkboxModelparallel
		console.log(JSON.stringify(ruleGroupJson))
		RuleGroupService.submit(ruleGroupJson, "ingestgroup", upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {

			if (options.execution == "YES") {
				RuleGroupService.getOneById(response.data, "ingestgroup").then(function (response) { onSuccessGetOneById(response.data) });
				var onSuccessGetOneById = function (result) {
					RuleGroupService.execute(result.data.uuid, result.data.version).then(function (response) { onSuccess(response.data) });
					var onSuccess = function (response) {
						console.log(JSON.stringify(response))
						$scope.isSubmitProgess = false;
						$scope.saveMessage = "Rule Groups Saved and Submitted Successfully"
						notify.type = 'success',
						notify.title = 'Success',
						notify.content = $scope.saveMessage
						$scope.$emit('notify', notify);
						$scope.oksave();
					}
				}
			} //End If
			else {
				$scope.isSubmitProgess = false;
				$scope.saveMessage = "Rule Groups Saved Successfully"
				notify.title = 'Success',
				notify.content = $scope.saveMessage
				$scope.$emit('notify', notify);
				$scope.oksave();
			} //End else
		} //End Submit Api Function
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	} //End Submit Function
});

DataIngestionModule.controller('IngestResultController', function ($http, dagMetaDataService, $timeout, $filter, $state, $stateParams, $location, $rootScope, $scope, ProfileService, CommonService, privilegeSvc, CF_DOWNLOAD) {
	$scope.select = $stateParams.type;
	$scope.type = { text: $scope.select == 'ingestgroupexec' ? 'ingestgroup' : 'ingest' };
	$scope.showprogress = false;
	$scope.isRuleExec = false;
	$scope.isRuleResult = false;
	$scope.showZoom = false;
	$scope.isD3RuleEexecGraphShow = false;
	$scope.isD3RGEexecGraphShow = false;
	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	$scope.download = {};
	$scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates = CF_DOWNLOAD.formate;
	$scope.download.selectFormate = CF_DOWNLOAD.formate[0];
	$scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to = CF_DOWNLOAD.limit_to;
	// ui grid
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'height': '40px'
		}
		if ($scope.filteredRows) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 80) + 'px';
		}
		return style;
	}
	var privileges = privilegeSvc.privileges['comment'] || [];
	$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
	$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
	$scope.$on('privilegesUpdated', function (e, data) {
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

	});
	$scope.metaType = dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType;
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.filteredRows = [];
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

	$scope.refreshRuleExecData = function () {
		$scope.gridOptionsRule.data = $filter('filter')($scope.orignalRuleExecData, $scope.searchruletext, undefined);
	}

	$scope.refreshRGExecData = function () {
		$scope.gridOptionsRuleGroup.data = $filter('filter')($scope.orignalRGExecData, $scope.searchrGtext, undefined);
	}
	// ui grid

	//For Breadcrum
	$scope.$on('daggroupExecChanged', function (e, groupExecName) {
		$scope.daggroupExecName = groupExecName;
	})

	$scope.$on('resultExecChanged', function (e, resultExecName) {
		$scope.resultExecName = resultExecName;
	})
	//For Breadcrum

	$scope.onClickRuleResult = function () {
		$scope.isRuleExec = true;
		$scope.isRuleResult = false;
		$scope.isD3RuleEexecGraphShow = false;
		$scope.execDetail = $scope.ingestGroupLastParams
		$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
		$scope.$emit('resultExecChanged', false);//Update Breadcrum
	}


	$scope.getIngestExec = function (data) {
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs["ingest"].execType;
		var uuid = data.uuid;
		var version = data.version;
		var name = data.name;
		$scope.ruleExecUuid = uuid;
		$scope.ruleExecVersion = version;
		var params = { "id": uuid, "name": name, "elementType": "ingest", "version": version, "type": "ingest", "typeLabel": "Ingest" }
		window.showResult(params);
	}

	$scope.$watch("zoomSize", function (newData, oldData) {
		$scope.$broadcast('zoomChange', newData);
	});

	window.navigateTo = function (url) {
		var state = JSON.parse(url);
		$rootScope.previousState = { name: $state.current.name, params: $state.params };
		var ispresent = false;
		if (ispresent != true) {
			var stateTab = {};
			stateTab.route = state.state;
			stateTab.param = state.params;
			stateTab.active = false;
			$rootScope.$broadcast('onAddTab', stateTab);
		}
		$state.go(state.state, state.params);
	}

	window.showResult = function (params) {
		App.scrollTop();
		$scope.lastParams = params;
		if (params.type.slice(-5).toLowerCase() == 'group') {
			$scope.isRuleExec = true;
			$scope.isIngestGroupExec = true;
			$scope.$broadcast('generateGroupGraph', params);
		}
		else {
			$scope.isRuleResult = true;
			$scope.isRuleExec = false;
			$scope.isDataInpogress = true;
			$scope.spinner = true;
			$scope.execDetail = params;
			$scope.execDetail.uuid = params.id;
			$scope.metaType = dagMetaDataService.elementDefs["ingest"].execType;

			setTimeout(function () {
				$scope.$apply();
				$scope.ruleExecUuid = params.id;
				$scope.ruleExecVersion = params.version;
				$scope.$broadcast('generateResults', params);
				$scope.$emit('resultExecChanged', params.name);  //For Breadcrum
			}, 100);
		}
	}
	$scope.refreshData = function (searchtext) {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
	};

	window.refreshResultfunction = function () {
		$scope.isD3RuleEexecGraphShow = false;
		window.showResult($scope.lastParams);
	}

	$scope.ruleExecshowGraph = function () {
		$scope.isD3RuleEexecGraphShow = true;
	}

	$scope.rGExecshowGraph = function () {
		$scope.isIngestGroupExec = false;
		$scope.isD3RGEexecGraphShow = true;
	}
	$scope.ingestGroupExec = function (data) {
		if ($scope.type.text == 'ingest') {
			$scope.getIngestExec(data);
			return
		}
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
		$scope.ingestGroupLastParams = data;
		$scope.zoomSize = 7;
		var uuid = data.uuid;
		var version = data.version;
		var name = data.name;
		$scope.rGExecUuid = uuid;
		$scope.rGExecVersion = version;
		$scope.isRuleSelect = false;
		$scope.isRuleGroupExec = false;
		$scope.isRuleExec = true;
		if ($scope.type.text == 'ingestgroup') {
			$scope.isIngestGroupExec = true;
		}
		else {
			$scope.isIngestGroupExec = false;
		}
		var params = { "id": uuid, "name": name, "elementType": "ingestgroup", "version": version, "type": "ingestgroup", "typeLabel": "IngestGroup", "url": "ingest/getIngestExecByRGExec?", "ref": { "type": "ingestgroupExec", "uuid": uuid, "version": version, "name": name } }
		setTimeout(function () {
			$scope.$broadcast('generateGroupGraph', params);
		}, 100);
	}
	$scope.getExec = $scope.ingestGroupExec;
	$scope.getExec({
		uuid: $stateParams.id,
		version: $stateParams.version,
		name: $stateParams.name
	});

	$scope.reGroupExecute = function () {
		$('#reExModal').modal({
			backdrop: 'static',
			keyboard: false
		});

	}
	$scope.okReGroupExecute = function () {
		$('#reExModal').modal('hide');
		$scope.executionmsg = "Rule Group Restarted Successfully"
		notify.type = 'success',
			notify.title = 'Success',
			notify.content = $scope.executionmsg
		$rootScope.$emit('notify', notify);
		CommonService.restartExec("ingestgroupExec", $stateParams.id, $stateParams.version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			//$scope.refreshRuleGroupExecFunction();
		}
		$scope.refreshRuleGroupExecFunction();
	}

	$scope.refreshRuleGroupExecFunction = function () {
		$scope.isD3RGEexecGraphShow = false;
		$scope.ingestGroupExec($scope.ingestGroupLastParams);
	}

	$scope.toggleZoom = function () {
		$scope.showZoom = !$scope.showZoom;
	}

	$scope.submitDownload = function () {
		var uuid = $scope.download.data.uuid;
		var version = $scope.download.data.version;
		var url = $location.absUrl().split("app")[0];
		$('#downloadSample').modal("hide");
		$http({
			method: 'GET',
			url: url + "ingest/download?action=view&uuid=" + uuid + "&version=" + version + "&rows=" + $scope.download.rows,
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			$scope.download.rows = CF_DOWNLOAD.framework_download_minrows;

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

	$scope.downloadFilePofile = function (data) {
		if ($scope.isD3RuleEexecGraphShow) {
			return false;
		}
		$scope.download.data = data;
		$('#downloadSample').modal({
			backdrop: 'static',
			keyboard: false
		});

	};

});