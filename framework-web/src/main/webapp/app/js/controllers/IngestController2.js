DataIngestionModule = angular.module('DataIngestionModule');
DataIngestionModule.controller('IngestRuleDetailController2', function ($state, $stateParams, $rootScope, $scope, $timeout, $filter, dagMetaDataService, privilegeSvc,IngestRuleService, CommonService, CF_FILTER) {

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
	$scope.continueCount=1;
	$scope.rhsNA=['NULL',"NOT NULL"];
    $scope.ruleTypes = [{ "text": "FILE-FILE", "caption": "File - File" }, { "text": "FILE-TABLE", "caption": "File - Table" }, { "text": "TABLE-TABLE", "caption": "Table - Table" }, { "text": "TABLE-FILE", "caption": "Table - File" }, { "text": "STREAM-FILE", "caption": "Stream - File" }, { "text": "STREAM-TABLE", "caption": "Stream - Table" }];
    $scope.sourceFormate = ["CSV", "TSV", "PSV", "PARQUET"];
    $scope.targetFormate = ["CSV", "TSV", "PSV", "PARQUET"];
    $scope.saveModeTable = ["APPEND", "OVERWRITE"];
    $scope.saveModeFile = ["OVERWRITE"];
	$scope.allAutoMapTable=["By Name","By Order"];
	$scope.allAutoMapFile=["From Source","From Target"];
	$scope.sourceTypes = ["datapod","dataset"];
    $scope.sourceAttributeTypes =
		[{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "formula", "caption": "formula" },
		{ "text": "function", "caption": "function" }];
  //  $scope.selectedRuleType = $scope.ruleTypes[0].text;
    $scope.streamColumn=["kye","value","topic","partition","offset","timestamp"];
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

	$scope.onChangeName = function (data) {
		$scope.ingestData.displayName=data;	
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
		$scope.showGraphDiv = false
	}

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('ingestruledetail2', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.ingestData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('ingestruledetail2', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		//if (!$scope.isEdit) {
			if($scope.checkIsInrogess () ==false){
				return false;
			}
			$scope.showPage()
			$state.go('ingestruledetail2', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		//}
	}
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
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
			$state.go('ingestrulelist2');
		}
	}
    $scope.countContinue = function () {
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
	

	// $scope.getOneByUuidAndVersionDatasource=function(data,type){
	// 	CommonService.getOneByUuidAndVersion(data.uuid,"", 'datasource')
	// 	.then(function (response) { onSuccess(response.data) },function(response) {onError(response.data)});
	// 	var onSuccess = function (response) {
	// 		if(type="source"){
	// 			$scope.selectedTargetDatasource=response;
	// 		}
	// 		if(type=="target"){
	// 			$scope.selectedTargetDatasource=response;
	// 		}
	// 	}
	// }
    
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
				if($scope.allSourceDatasource && $scope.allSourceDatasource.length ==0)
					$scope.allTargetDatasource = response;
				else
				if($scope.allTargetDatasource== null){
					$scope.allTargetDatasource=[];
				}
				$scope.allTargetDatasource=$scope.allTargetDatasource.concat(response);
				//setTimeout(function(){
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
						}
					}
				//},100);
			
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
	// 	IngestRuleService.getAllAttributeBySource($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
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

			if (TargetType == "TABLE") {
				if($scope.allSourceDatasource && $scope.allSourceDatasource.length >0){
					for(var i=0;i<$scope.allSourceDatasource.length;i++){
						if($scope.allSourceDatasource[i].type == 'FILE'){
							if($scope.allTargetDatasource)
								$scope.allTargetDatasource.push($scope.allSourceDatasource[i]);
							else{
								$scope.allTargetDatasource=[];
								$scope.allTargetDatasource.push($scope.allSourceDatasource[i]);
							}
						}
					}
			    }
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
		$scope.ingestTableArray=[];
		$scope.ingestData.ingestChg = "Y";
		$scope.selectedSourceType = $scope.selectedRuleType.split("-")[0];
		$scope.selectedTargetType = $scope.selectedRuleType.split("-")[1];
		$scope.isSourceFormateDisable = $scope.selectedSourceType == 'FILE' ? false : true;
		$scope.isTargetFormateDisable = $scope.selectedTargetType == 'FILE' ? false : true;
		
		if($scope.selectedSourceType == 'FILE'  && $scope.selectedTargetType == 'FILE' && $scope.mode =="true"){
			$scope.isAttributeMapDisable=true;
		}
		else if($scope.selectedSourceType == 'FILE'  && $scope.selectedTargetType == 'FILE' && $scope.mode =="false"){
			$scope.isAttributeMapDisable=false;
		}
		else{
			if($scope.selectedSourceType != 'FILE'  || $scope.selectedTargetType != 'FILE'){
				$scope.isAttributeMapDisable=true;
			}
			else if($scope.mode =="true"){
				$scope.isAttributeMapDisable=false;
			}else{
				$scope.isAttributeMapDisable=true;
			}
			
		}
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
	
	$scope.onChangeSourceType=function(){
		if(!$scope.selectedSourceDatasource){
			return false;
		}
		$scope.ingestData.ingestChg = "Y";
		$scope.ingestData.filterChg = "Y";
		if ($scope.selectedSourceType != "FILE" && $scope.selectedSourceType != "STREAM" && $scope.selectedSourceDatasource) {
			$scope.getDatapodOrDatasetByDatasource($scope.selectedSourceDatasource.uuid, "source",$scope.selectedSourceDetailType);
            
		}
	}

	$scope.onChangeSourceDataSource = function () {
		if(!$scope.selectedSourceDatasource){
			return false;
		}
		$scope.ingestData.ingestChg = "Y";
		$scope.ingestData.filterChg = "Y";
		if ($scope.selectedSourceType != "FILE" && $scope.selectedSourceType != "STREAM" && $scope.selectedSourceDatasource) {
			//$scope.getDatapodOrDatasetByDatasource($scope.selectedSourceDatasource.uuid, "source",$scope.selectedSourceDetailType);
            
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

		if($scope.selectedSourceType =="STREAM" && $scope.selectedTargetType =="FILE" && $scope.ingestTableArray.length ==0){
			for(var i=0;i<$scope.streamColumn.length;i++){
				var attributemapjson={};
				var obj = {}				
				obj.text = "datapod"
				obj.caption = "attribute"
				attributemapjson.isSourceAtributeDatapod = false;
				attributemapjson.isSourceAtributeSimple = true;
				attributemapjson.sourceAttributeType = obj;
				attributemapjson.isSourceAtributeFormula = false;
				attributemapjson.isSourceAtributeExpression = false;
				$scope.ingestTableArray[i]=attributemapjson;
				attributemapjson.sourcesimple=$scope.streamColumn[i];
				attributemapjson.targetsimple="";
				$scope.ingestTableArray[i].isTargetAtributeSimple = true;
				$scope.ingestTableArray[i].isTargetAtributeDatapod =false;
			}
			$scope.ingestTableInfo=$scope.ingestTableArray;
			
		}
		if($scope.selectedSourceType =="STREAM" && $scope.selectedTargetType =="TABLE" && $scope.ingestTableArray.length ==0){
			for(var i=0;i<$scope.streamColumn.length;i++){
				var attributemapjson={};
				var obj = {}				
				obj.text = "datapod"
				obj.caption = "attribute"
				attributemapjson.isSourceAtributeDatapod = false;
				attributemapjson.isSourceAtributeSimple = true;
				attributemapjson.sourceAttributeType = obj;
				attributemapjson.isSourceAtributeFormula = false;
				attributemapjson.isSourceAtributeExpression = false;
				$scope.ingestTableArray[i]=attributemapjson;
				attributemapjson.sourcesimple=$scope.streamColumn[i];
				attributemapjson.targetsimple="";
				$scope.ingestTableArray[i].isTargetAtributeSimple = false;
				$scope.ingestTableArray[i].isTargetAtributeDatapod =true;
			}
			$scope.ingestTableInfo=$scope.ingestTableArray;
			
		}
	
	}
	$scope.onChangeTargetDataSource = function () {
		$scope.ingestData.ingestChg = "Y";
		if ($scope.selectedTargetDatasource) {
			$scope.getDatapodOrDatasetByDatasource($scope.selectedTargetDatasource.uuid, "target","datapod");
			if( $scope.selectedTargetDatasource && $scope.selectedTargetDatasource.type != 'FILE'){
				$scope.isTargetFormateDisable=true;
				$scope.selectedTargetFormate = null;
			}
			else{
				if($scope.selectedTargetType=="TABLE")
					$scope.isTargetFormateDisable=true;
				else{
					$scope.isTargetFormateDisable=false;
				}

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

	$scope.getDatapodOrDatasetByDatasource = function (uuid, propertyType,type) {
		IngestRuleService.getDatapodOrDatasetByDatasource(uuid,type).then(function (response) { onSuccessGetDatapodByDatasource(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetDatapodByDatasource = function (response) {
			if (propertyType == "source") {
				$scope.sourceDetails = response;
			}
			if (propertyType == "target") {
				$scope.tagetDetail = response;
			}
		}
	}

	$scope.getAllAttributeBySource = function (data) {
		if ($scope.selectedSourceDetail) {
			IngestRuleService.getAllAttributeBySource($scope.selectedSourceDetail.uuid,$scope.selectedSourceDetailType).then(function (response) { onSuccessGetAllAttributeBySource(response.data) })
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.sourcedatapodattribute = response;
				$scope.allSourceAttribute = response;
				$scope.lhsdatapodattributefilter = response;
				if ($scope.ingestTableArray.length == 0  && $scope.selectedSourceType == "TABLE" && $scope.selectedTargetType == "FILE") {
					$scope.ingestTableArray=[];
					$scope.ingestTableInfo=$scope.allSourceAttribute;
					for(var i=0;i <$scope.allSourceAttribute.length;i++){
						var attributemapjson={};
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.isSourceAtributeSimple = false;
                        attributemapjson.isSourceAtributeDatapod = true;
                        attributemapjson.isSourceAtributeFormula = false;
						attributemapjson.isSourceAtributeExpression = false;
						attributemapjson.sourceattribute=$scope.allSourceAttribute[i];
						$scope.ingestTableArray[i]=attributemapjson;
						
						$scope.ingestTableArray[i].isTargetAtributeSimple = true;
						$scope.ingestTableArray[i].isTargetAtributeDatapod = false;
					}
					$scope.ingestTableInfo=$scope.ingestTableArray;
                }
			}
		}
    }
    $scope.getAllAttributeByTarget = function (data) {
		if ($scope.selectedTargetDetail) {
			IngestRuleService.getAllAttributeBySource($scope.selectedTargetDetail.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) })
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allTargetAttribute = response;
				if(data==null){
				   $scope.ingestTableArray=[];
			    }
                if ($scope.ingestTableArray.length == 0 &&   $scope.selectedSourceType != "STREAM") {
					$scope.ingestTableInfo=$scope.allTargetAttribute;
					for(var i=0;i <$scope.allTargetAttribute.length;i++){
						var attributemapjson={};
						var obj = {}
						if($scope.selectedSourceType == "TABLE"){
                        	obj.text = "datapod"
							obj.caption = "attribute"
							attributemapjson.isSourceAtributeDatapod = true;
							attributemapjson.isSourceAtributeSimple = false;
						}
						else{
							obj.text = "datapod"
							obj.caption = "attribute"
							attributemapjson.isSourceAtributeDatapod = false;
							attributemapjson.isSourceAtributeSimple = true;
						}
                        attributemapjson.sourceAttributeType = obj;
                      
                        attributemapjson.isSourceAtributeFormula = false;
						attributemapjson.isSourceAtributeExpression = false;
						$scope.ingestTableArray[i]=attributemapjson;
						var targetattribute = {}
                        targetattribute.uuid = $scope.allTargetAttribute[i].uuid;
                        targetattribute.name =  $scope.allTargetAttribute[i].name;
                        targetattribute.dname =  $scope.allTargetAttribute[i].dname;
                        targetattribute.type =  $scope.allTargetAttribute[i].type;
                        targetattribute.attributeId =  $scope.allTargetAttribute[i].attributeId;
						targetattribute.attrName= $scope.allTargetAttribute[i].attrName;
						$scope.ingestTableArray[i].targetattribute = targetattribute;
						$scope.ingestTableArray[i].isTargetAtributeSimple = false;
						$scope.ingestTableArray[i].isTargetAtributeDatapod = true;
					}
					$scope.ingestTableInfo=$scope.ingestTableArray;
                }
		
			}
		}
	}

    $scope.onChangeAutoMode=function(){
        $scope.ingestTableArray=[];
        if($scope.selectedSourceType == "TABLE" && $scope.selectedTargetType == "TABLE" &&  $scope.selectedAutoMode == "By Order"){
            if($scope.allTargetAttribute){
                $scope.ingestTableArray=[];
               // $scope.ingestTableInfo=$scope.allTargetAttribute;
                for(var i=0;i<$scope.ingestTableInfo.length;i++){
                    var mapInfo = {};
                    var obj = {}
                    obj.text = "datapod";
                    obj.caption = "attribute";
                    mapInfo.isSourceAtributeSimple = false;
                    mapInfo.isSourceAtributeDatapod = true;
                    mapInfo.isSourceAtributeFormula = false;
                    mapInfo.isSourceAtributeExpression = false;
                    mapInfo.sourceAttributeType = obj;
					mapInfo.sourceattribute=$scope.allSourceAttribute[i];
					mapInfo.targetattribute = $scope.ingestTableInfo[i].targetattribute;
					mapInfo.isTargetAtributeSimple = false;
					mapInfo.isTargetAtributeDatapod = true;
                    $scope.ingestTableArray[i] = mapInfo;
                }
            }
        }
        else if($scope.selectedSourceType == "TABLE" && $scope.selectedTargetType == "TABLE" &&  $scope.selectedAutoMode == "By Name"){
            var allSourceAttribute={};
            $scope.ingestTableArray=[];
          //  $scope.ingestTableInfo=$scope.allTargetAttribute;
            angular.forEach($scope.allSourceAttribute, function (val, key) {
                allSourceAttribute[val.name] = val;
            });
            for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
                var obj = {}
                obj.text = "datapod";
                obj.caption = "attribute";
                mapInfo.isSourceAtributeSimple = false;
                mapInfo.isSourceAtributeDatapod = true;
                mapInfo.isSourceAtributeFormula = false;
                mapInfo.isSourceAtributeExpression = false;
                mapInfo.sourceAttributeType = obj;
				mapInfo.sourceattribute=allSourceAttribute[$scope.ingestTableInfo[i].targetattribute.attrName];
				mapInfo.targetattribute = $scope.ingestTableInfo[i].targetattribute;
				mapInfo.isTargetAtributeSimple = false;
				mapInfo.isTargetAtributeDatapod = true;
                $scope.ingestTableArray[i] = mapInfo;
            }
          //  console.log(allSourceAttribute);
		}
		else if($scope.selectedSourceType == "FILE" && $scope.selectedTargetType == "TABLE" && $scope.selectedAutoMode == "From Target"){
			var allSourceAttribute={};
            $scope.ingestTableArray=[];
		//	$scope.ingestTableInfo=$scope.allTargetAttribute;
			for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
                var obj = {}
				obj.text = "datapod";
                obj.caption = "attribute";
                mapInfo.isSourceAtributeSimple = true;
                mapInfo.isSourceAtributeDatapod = false;
                mapInfo.isSourceAtributeFormula = false;
                mapInfo.isSourceAtributeExpression = false;
                mapInfo.sourceAttributeType = obj;
				mapInfo.sourcesimple=$scope.allTargetAttribute[i].name;
				mapInfo.targetattribute = $scope.ingestTableInfo[i].targetattribute;
				mapInfo.isTargetAtributeSimple = false;
				mapInfo.isTargetAtributeDatapod = true;
                $scope.ingestTableArray[i] = mapInfo;
            }
		}
		else if($scope.selectedSourceType == "TABLE" && $scope.selectedTargetType == "FILE" && $scope.selectedAutoMode == "From Source"){
			
            $scope.ingestTableArray=[];
			//$scope.ingestTableInfo=$scope.allSourceAttribute;
			for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
                var obj = {}
				obj.text = "datapod";
                obj.caption = "attribute";
                mapInfo.isSourceAtributeSimple = false;
                mapInfo.isSourceAtributeDatapod = true;
                mapInfo.isSourceAtributeFormula = false;
                mapInfo.isSourceAtributeExpression = false;
                mapInfo.sourceAttributeType = obj;
				mapInfo.targetsimple=$scope.ingestTableInfo[i].sourceattribute.attrName
				mapInfo.sourceattribute=$scope.ingestTableInfo[i].sourceattribute;
				mapInfo.isTargetAtributeSimple = true;
				mapInfo.isTargetAtributeDatapod = false;
                $scope.ingestTableArray[i] = mapInfo;
            }
		}
	
		else if($scope.selectedSourceType == "FILE" && $scope.selectedTargetType == "FILE" && $scope.selectedAutoMode == "From Source"){

			for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
				mapInfo.isSourceAtributeSimple = false;
                mapInfo.isSourceAtributeDatapod = false;
                mapInfo.isSourceAtributeFormula = false;
				mapInfo.isSourceAtributeExpression = false;
				mapInfo.sourceAttributeType =$scope.ingestTableInfo[i].sourceAttributeType;
				if($scope.ingestTableInfo[i].sourceAttributeType.text =="string"){
					mapInfo.targetsimple=$scope.ingestTableInfo[i].sourcesimple;
					mapInfo.sourcesimple=$scope.ingestTableInfo[i].sourcesimple;
					mapInfo.isSourceAtributeSimple = true;
				}
				else if($scope.ingestTableInfo[i].sourceAttributeType.text =="datapod"){
					mapInfo.targetsimple=$scope.ingestTableInfo[i].sourcesimple;
					mapInfo.sourcesimple=$scope.ingestTableInfo[i].sourcesimple;
					mapInfo.isSourceAtributeSimple = true;
				}else{
					mapInfo.sourcefunction=$scope.ingestTableInfo[i].sourcefunction;
					mapInfo.targetsimple=$scope.ingestTableInfo[i].sourcefunction.name;
					mapInfo.isSourceAtributeFunction=true
				}
				
				mapInfo.isTargetAtributeSimple = true;
				mapInfo.isTargetAtributeDatapod = false;
				
                $scope.ingestTableArray[i] = mapInfo;
            }
		}
		else if($scope.selectedSourceType == "FILE" && $scope.selectedTargetType == "FILE" && $scope.selectedAutoMode == "From Target"){
			for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
                var obj = {}
				var mapInfo = {};
                var obj = {}
				obj.text = "datapod";
                obj.caption = "attribute";
                mapInfo.isSourceAtributeSimple = true;
                mapInfo.isSourceAtributeDatapod = false;
                mapInfo.isSourceAtributeFormula = false;
				mapInfo.isSourceAtributeExpression = false;
				mapInfo.sourceAttributeType = obj;
				mapInfo.targetsimple=$scope.ingestTableInfo[i].targetsimple;
				mapInfo.isTargetAtributeSimple = true;
				mapInfo.isTargetAtributeDatapod = false;
				mapInfo.sourcesimple=$scope.ingestTableInfo[i].targetsimple;
                $scope.ingestTableArray[i] = mapInfo;
            }
		}
		else if($scope.selectedSourceType == "STREAM" && $scope.selectedTargetType == "FILE" && $scope.selectedAutoMode == "From Source"){
			for(var i=0;i<$scope.ingestTableInfo.length;i++){
                var mapInfo = {};
                var obj = {}
				var mapInfo = {};
                var obj = {}
				obj.text = "datapod";
                obj.caption = "attribute";
                mapInfo.isSourceAtributeSimple = true;
                mapInfo.isSourceAtributeDatapod = false;
                mapInfo.isSourceAtributeFormula = false;
				mapInfo.isSourceAtributeExpression = false;
				mapInfo.sourceAttributeType = obj;
				mapInfo.targetsimple=$scope.ingestTableInfo[i].sourcesimple;
				mapInfo.isTargetAtributeSimple = true;
				mapInfo.isTargetAtributeDatapod = false;
				mapInfo.sourcesimple=$scope.ingestTableInfo[i].sourcesimple;
                $scope.ingestTableArray[i] = mapInfo;
            }
		}
		
        else{
            $scope.ingestTableArray=[];
        }
	}
	
	
    $scope.checkAllAttributeRow = function () {
		angular.forEach($scope.ingestTableInfo, function (filter) {
			filter.selected = $scope.selectedAllAttributeRow;
		});
		$scope.ingestTableArray=$scope.ingestTableInfo;
	}

	$scope.addAttribute = function () {
		if ($scope.ingestTableArray == null) {
			$scope.ingestTableArray = [];
		}else{

		}
		var mapInfo = {};
		var obj = {}
		obj.text = "string";
		obj.caption = "string";
		mapInfo.isSourceAtributeSimple = true;
		mapInfo.isSourceAtributeDatapod = false;
		mapInfo.isSourceAtributeFormula = false;
		mapInfo.isSourceAtributeExpression = false;
		mapInfo.isTargetAtributeSimple = true;
		mapInfo.isTargetAtributeDatapod = false;
		mapInfo.sourceAttributeType = obj;
		mapInfo.targetsimple;
		$scope.ingestTableArray.splice($scope.ingestTableArray.length, 0, mapInfo);
		$scope.ingestTableInfo=$scope.ingestTableArray;
		console.log($scope.ingestTableArray);
	}
	$scope.removeAttribute = function () {
		var newDataList = [];
		$scope.selectedAllAttributeRow=false;
		$scope.checkAll = false;
		angular.forEach($scope.ingestTableInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});

	
		$scope.ingestTableInfo = newDataList;
		$scope.ingestTableArray=$scope.ingestTableInfo;
	}
	
	$scope.getAllFunctionWithNOInput=function(){
		CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
		var onSuccressGetFunction = function (response) {
			console.log(response)
			$scope.allFunction = response;
			$scope.ruleLodeFunction = response
		}
    } 
   
	$scope.getFormulaByType=function(){
		IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {
				$scope.allMapLodeFormula = response;
				$scope.allFormula=response;
		}
	}
	
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.showactive = "true";
		$scope.isEditInprogess=true;
    	$scope.isEditVeiwError=false;
		IngestRuleService.getAllVersionByUuid($stateParams.id, "ingest")
			.then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var ingetversion = {};
				ingetversion.version = response[i].version;
				$scope.ingest.versions[i] = ingetversion;
			}
		}
		if($stateParams.version =="")
			$stateParams.version="";
		IngestRuleService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'ingest')
			.then(function (response) { onSuccess(response.data) },function(response) {onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			$scope.ingestData = response.ingestData;
			$scope.ingestCompare = response.ingestData;
			defaultversion.version = response.ingestData.version;
			defaultversion.uuid = response.ingestData.uuid;
			$scope.ingest.defaultVersion = defaultversion;
			$scope.selectedRuleType = $scope.ingestData.type;
			$scope.onChangeRuleType();
			$scope.getAllFunctionWithNOInput();
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
				$scope.selectedSourceDetailType=$scope.ingestData.sourceDetail.ref.type;
				$scope.onChangeSourceType();
				$scope.getAllAttributeBySource(response.ingesttabalearray);
				$scope.getFormulaByType();
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
                $scope.getAllAttributeByTarget(response.ingesttabalearray);
			}
            $scope.filterTableArray = response.filterInfo;
			$scope.ingestTableArray=response.ingesttabalearray;
			$scope.ingestTableInfo=$scope.ingestTableArray;
			if($scope.selectedSourceType == "FILE" && $scope.selectedTargetType == "FILE"){
				$scope.ingestTableInfo=$scope.ingestTableArray;
			}
            
			var tags = [];
			for (var i = 0; i < response.ingestData.tags.length; i++) {
				var tag = {};
				tag.text = response.ingestData.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		};
		var onError =function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}; 
	}//End If
    else{
		
		$scope.ingestData={};
		$scope.ingestData.sourceHeader="Y";
		$scope.ingestData.targetHeader="N"
		$scope.ingestData.locked="N";
	//	$scope.onChangeRuleType();
	}


	$scope.selectVersion = function () {
		$scope.myform1.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform3.$dirty = false;
		$scope.isEditInprogess=true;
    	$scope.isEditVeiwError=false;
		IngestRuleService.getOneByUuidAndVersion($scope.ingest.defaultVersion.uuid, $scope.ingest.defaultVersion.version, 'ingest')
			.then(function (response) { onSuccess(response.data) },function(response) {onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
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
		};
		var onError =function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
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
		if ($scope.rulecompare != null) {
			$scope.rulecompare.filterChg = "y"
		}
		if ($scope.filterTableArray[index].operator == 'BETWEEN') {
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, []);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 
		else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist','string','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
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
		filertable.islhsDatapod = false;
		filertable.islhsFormula = false;
		filertable.islhsSimple = true;
		filertable.isrhsDatapod = false;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = true;
		
		//filertable.lhsFilter = $scope.lhsdatapodattributefilter[0];
		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[0]
		filertable.rhstype = $scope.rhsType[0]
		filertable.rhsTypes = returnRshType();
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
			
            if($scope.selectedSourceType =="FILE"){
				$scope.filterTableArray[index].islhsSimple = true;
				$scope.filterTableArray[index].islhsDatapod = false;
			}
            else{
				$scope.filterTableArray[index].islhsSimple = false;
				$scope.filterTableArray[index].islhsDatapod = true;
			}
			//$scope.filterTableArray[index].islhsSimple = false;
			//$scope.filterTableArray[index].islhsDatapod = true;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.filterTableArray[index].islhsFormula = true;
			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = false;
			IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid, "datapod").then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				//response.splice(0, 1);
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
			if($scope.selectedSourceType =="FILE"){
				$scope.filterTableArray[index].isrhsSimple = true;
				$scope.filterTableArray[index].isrhsDatapod = false;
			}
            else{
				$scope.filterTableArray[index].isrhsSimple = false;
				$scope.filterTableArray[index].isrhsDatapod = true;
			}
		//	$scope.filterTableArray[index].isrhsSimple = false;
			//$scope.filterTableArray[index].isrhsDatapod = true;
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
				//$scope.allFormula.splice(0, 1);
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
    $scope.onChangeSourceAttribute = function (type, index) {
		
		$scope.ingestTableArray[index].sourcesimple=null;
		if (type == "string") {
			$scope.ingestTableArray[index].isSourceAtributeSimple = true;
			$scope.ingestTableArray[index].isSourceAtributeDatapod = false;
			$scope.ingestTableArray[index].isSourceAtributeFormula = false;
			$scope.ingestTableArray[index].sourcesimple;
			$scope.ingestTableArray[index].isSourceAtributeExpression = false;
			$scope.ingestTableArray[index].isSourceAtributeFunction = false;
            
		}
		else if (type == "datapod") {
            if($scope.selectedSourceType =="FILE" ||$scope.selectedSourceType =="STREAM"){
				$scope.ingestTableArray[index].isSourceAtributeSimple = true;
				$scope.ingestTableArray[index].isSourceAtributeDatapod =false;
			}else{
				$scope.ingestTableArray[index].isSourceAtributeSimple = false;
				$scope.ingestTableArray[index].isSourceAtributeDatapod =true;
			}
			$scope.ingestTableArray[index].isSourceAtributeFormula = false;
			$scope.ingestTableArray[index].isSourceAtributeExpression = false;
			$scope.ingestTableArray[index].isSourceAtributeFunction = false;
		}
		else if (type == "formula") {

			$scope.ingestTableArray[index].isSourceAtributeSimple = false;
			$scope.ingestTableArray[index].isSourceAtributeDatapod = false;
			$scope.ingestTableArray[index].isSourceAtributeFormula = true;
			$scope.ingestTableArray[index].isSourceAtributeExpression = false;
			$scope.ingestTableArray[index].isSourceAtributeFunction = false;
			IngestRuleService.getFormulaByType($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeFormula = response.data
			}
		}
		else if (type == "expression") {

			$scope.ingestTableArray[index].isSourceAtributeSimple = false;
			$scope.ingestTableArray[index].isSourceAtributeDatapod = false;
			$scope.ingestTableArray[index].isSourceAtributeFormula = false;
			$scope.ingestTableArray[index].isSourceAtributeExpression = true;
			$scope.ingestTableArray[index].isSourceAtributeFunction = false;
			IngestRuleService.getExpressionByType($scope.selectedSourceDetail.uuid,"datapod").then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeExpression = response
			}

		}
		else if (type == "function") {

			$scope.ingestTableArray[index].isSourceAtributeSimple = false;
			$scope.ingestTableArray[index].isSourceAtributeDatapod = false;
			$scope.ingestTableArray[index].isSourceAtributeFormula = false;
			$scope.ingestTableArray[index].isSourceAtributeExpression = false;
			$scope.ingestTableArray[index].isSourceAtributeFunction = true;
			$scope.ingestTableArray[index].isSourceAtributeFunction = true;
			$scope.getAllFunctionWithNOInput()

		}

	}
	$scope.submit = function () {
		$scope.isSubmitProgess = true;
		$scope.isSubmitDisable = true;
		var ingestJson = {};
		ingestJson.uuid = $scope.ingestData.uuid;
		ingestJson.name = $scope.ingestData.name;
		ingestJson.displayName = $scope.ingestData.displayName;
		ingestJson.desc = $scope.ingestData.desc;
		ingestJson.active = $scope.ingestData.active;
		ingestJson.locked = $scope.ingestData.locked;
		ingestJson.published = $scope.ingestData.published;
		ingestJson.publicFlag = $scope.ingestData.publicFlag;
		ingestJson.runParams = $scope.ingestData.runParams;
		ingestJson.sourceHeader = $scope.ingestData.sourceHeader;
		ingestJson.targetHeader = $scope.ingestData.targetHeader;
		ingestJson.ignoreCase= $scope.ingestData.ignoreCase;
		ingestJson.sourceExtn=$scope.ingestData.sourceExtn;
		ingestJson.targetExtn=$scope.ingestData.targetExtn;
		if($scope.ingestData.saveMode){
			ingestJson.saveMode = $scope.ingestData.saveMode;
		}
		else{
			ingestJson.saveMode=null;
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
			sourceDetailsRef.type =$scope.selectedSourceDetailType;
			sourceDetailsRef.uuid = $scope.selectedSourceDetail.uuid;
			sourceDetails.ref = sourceDetailsRef;
		}
		ingestJson.sourceDetail = sourceDetails;
        if ($scope.selectedSourceType != "FILE" && $scope.selectedSourceType != "STREAM") {
			var sourceAttrDetail={};
			var sourceAttrDetailRef={};
			sourceAttrDetailRef.type=$scope.selectedSourceDetailType;;
			sourceAttrDetailRef.uuid=$scope.selectedSourceAttrDetail.uuid;
			sourceAttrDetail.ref=sourceAttrDetailRef;
			sourceAttrDetail.attrId=$scope.selectedSourceAttrDetail.attributeId;
			ingestJson.incrAttr=sourceAttrDetail;
			var splitBy={};
			var splitByRef={};
			splitByRef.type=$scope.selectedSourceDetailType;;
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
	
	
		if ($scope.filterTableArray.length > 0) {
			for (var i = 0; i < $scope.filterTableArray.length; i++) {
			
				var filterInfo = {};
				var operand = []
				var lhsoperand = {};;
				var lhsref = {};
				var rhsoperand = {};
				var rhsref = {};
				filterInfo.display_seq=i;
				if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
					filterInfo.logicalOperator = ""
				}
				else {
					filterInfo.logicalOperator = $scope.filterTableArray[i].logicalOperator
				}
				filterInfo.operator = $scope.filterTableArray[i].operator;
				if ($scope.filterTableArray[i].lhstype.text == "string" &&  $scope.filterTableArray[i].lhsvalue) {
					lhsref.type = "simple";
					lhsoperand.ref = lhsref;
					lhsoperand.attributeType = $scope.filterTableArray[i].lhstype.caption;
					lhsoperand.value = $scope.filterTableArray[i].lhsvalue;
				}
				if ($scope.filterTableArray[i].lhstype.text == "datapod" &&  $scope.filterTableArray[i].lhsvalue) {
					lhsref.type = "attribute";
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
				if ($scope.filterTableArray[i].rhstype.text == "string" &&  $scope.filterTableArray[i].rhsvalue) {

					rhsref.type = "simple";
					rhsoperand.ref = rhsref;
					rhsoperand.attributeType = $scope.filterTableArray[i].rhstype.caption;
					rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
				}
				if ($scope.filterTableArray[i].rhstype.text == "string" &&  $scope.filterTableArray[i].rhsvalue1 &&  $scope.filterTableArray[i].rhsvalue2) {

					rhsref.type = "simple";
					rhsoperand.ref = rhsref;
					if ($scope.filterTableArray[i].operator == 'BETWEEN') {
						rhsoperand.value = $scope.filterTableArray[i].rhsvalue1 + "and" + $scope.filterTableArray[i].rhsvalue2;
					}
				}
				if ($scope.filterTableArray[i].rhstype.text == "datapod" &&  $scope.filterTableArray[i].rhsvalue) {

					rhsref.type = "attribute";
					rhsoperand.ref = rhsref;
					rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
				}
				else if ($scope.filterTableArray[i].rhstype.text == "datapod" && $scope.filterTableArray[i].rhsdatapodAttribute) {
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
			
			ingestJson.filterInfo = filterInfoArray;
		}
		else {
			ingestJson.filterInfo = null;
			
		}
	
        
        var attributemaparray = [];
        if($scope.ingestTableArray && $scope.ingestTableInfo){
            for (var i = 0; i < $scope.ingestTableInfo.length; i++) {
                var attributemap = {};
                attributemap.attrMapId = i;
                var sourceAttr = {};
                var sourceref = {};
                var targetAttr = {};
				var targetref = {};
				
                if ($scope.ingestTableArray[i].sourceAttributeType.text == "string" && typeof $scope.ingestTableArray[i].sourcesimple != "undefined") {
                    sourceref.type = "simple";
                    sourceAttr.ref = sourceref;
                    sourceAttr.value = $scope.ingestTableArray[i].sourcesimple;
                    attributemap.sourceAttr = sourceAttr;
				}
				
				if ($scope.ingestTableArray[i].sourceAttributeType.text == "datapod" &&  $scope.ingestTableArray[i].sourcesimple && typeof $scope.ingestTableArray[i].sourcesimple != "undefined") {
                    sourceref.type = "attribute";
                    sourceAttr.ref = sourceref;
                    sourceAttr.value = $scope.ingestTableArray[i].sourcesimple;
                    attributemap.sourceAttr = sourceAttr;
                }
                else if ($scope.ingestTableArray[i].sourceAttributeType.text == "datapod" &&  $scope.ingestTableArray[i].sourceattribute) {
                    sourceref.uuid = $scope.ingestTableArray[i].sourceattribute.uuid;
                    sourceref.type=$scope.ingestTableArray[i].sourceattribute.type;
                    sourceAttr.ref = sourceref;
                    sourceAttr.attrId = $scope.ingestTableArray[i].sourceattribute.attributeId;
                    attributemap.sourceAttr = sourceAttr;
                }
                else if ($scope.ingestTableArray[i].sourceAttributeType.text == "expression") {

                    sourceref.type = "expression";
                    sourceref.uuid = $scope.ingestTableArray[i].sourceexpression.uuid;
                    sourceAttr.ref = sourceref;
                    attributemap.sourceAttr = sourceAttr;

                }
                else if ($scope.ingestTableArray[i].sourceAttributeType.text == "formula") {

                    sourceref.type = "formula";
                    sourceref.uuid = $scope.ingestTableArray[i].sourceformula.uuid;
                    sourceAttr.ref = sourceref;
                    attributemap.sourceAttr = sourceAttr;

                }
                else if ($scope.ingestTableArray[i].sourceAttributeType.text == "function") {

                    sourceref.type = "function";
                    sourceref.uuid = $scope.ingestTableArray[i].sourcefunction.uuid;
                    sourceAttr.ref = sourceref;
                    attributemap.sourceAttr = sourceAttr;

				}

				 if ($scope.selectedTargetType != "FILE") {
					targetref.uuid = $scope.ingestTableArray[i].targetattribute.uuid;
                	targetref.type = $scope.ingestTableArray[i].targetattribute.type;
                	targetAttr.ref = targetref;
					targetAttr.attrId =  $scope.ingestTableArray[i].targetattribute.attributeId;
                }
				else{
				
					targetref.type ="attribute";
                	targetAttr.ref = targetref;
					targetAttr.value =$scope.ingestTableArray[i].targetsimple;
				}
                attributemap.targetAttr = targetAttr;
                attributemaparray[i] = attributemap;
            }
		}
		
		ingestJson.attributeMap = attributemaparray;

		console.log(JSON.stringify(ingestJson))
		IngestRuleService.submit(ingestJson, 'ingest', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			
			if ($scope.isExecute == "YES") {
				IngestRuleService.getOneById(response.data, "ingest").then(function(response) {onSuccessGetOneById(response.data)});
				var onSuccessGetOneById = function(result) {
					IngestRuleService.execute(result.data.uuid,result.data.version).then(function(response) { onSuccess(response.data)},function (response) { onError(response.data) });
					var onSuccess = function(response) {
						console.log(JSON.stringify(response))
						$scope.isSubmitProgess = false;
						$scope.isSubmitDisable = true;
						$scope.saveMessage = "Rule Saved and Submitted Successfully"
						notify.type='success',
						notify.title= 'Success',
						notify.content=$scope.saveMessage
						$scope.$emit('notify', notify);
						$scope.oksave();
					}
					var onError = function (response) {
						$scope.isSubmitProgess = false;
						$scope.isSubmitDisable = false;
						$scope.oksave();
					}
				}
			  } //End If
			else {
				$scope.isSubmitProgess = false;
				$scope.isSubmitDisable = true;
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = 'Rule Saved Successfully'
				$scope.$emit('notify', notify);
				$scope.oksave();
		    }
		}
		var onError = function (response) {
			$scope.isSubmitProgess = false;
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
			$scope.isSubmitDisable = false;
		}
	}

	$scope.oksave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('ingestrulelist2'); }, 2000);
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
		}
		else{
			if(index <= $scope.filterTableArray.length){
				$scope.autoMove(index-1,'filterAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
	}


});
