DataIngestionModule = angular.module('DataIngestionModule');

DataIngestionModule.controller('IngestRuleDetailController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, IngestRuleService, privilegeSvc) {
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
    $scope.showFrom = true;
    $scope.showGraphDiv = false
    $scope.ingest = {};
    $scope.ingest.versions = [];
    $scope.isDependencyShow = false;
    $scope.privileges = [];
    $scope.privileges = privilegeSvc.privileges['ingest'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
    $scope.$on('privilegesUpdated', function (e, data) {
        $scope.privileges = privilegeSvc.privileges['ingest'] || [];
        $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
    });

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
        $scope.showFrom = true;
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
        if($scope.selectedSourceType !="FILE" && $scope.selectedSourceDatasource ){
            $scope.getDatapodByDatasource($scope.selectedSourceDatasource.uuid,"source");
        }
    }
    $scope.onChangeTargetDataSource=function(){
        if($scope.selectedTargetDatasource){
            $scope.getDatapodByDatasource($scope.selectedTargetDatasource.uuid,"target");
        }
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
			$scope.ingestData = response.ingestData
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

    $scope.submit =function(){
        var ingestJson={};
        ingestJson.uuid=$scope.ingestData.uuid;
		ingestJson.name = $scope.ingestData.name;
		ingestJson.desc = $scope.ingestData.desc;
		ingestJson.active = $scope.ingestData.active;
        ingestJson.published = $scope.ingestData.published;
        ingestJson.runParams=$scope.ingestData.runParams;
        var upd_tag="N"
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
        var sourceDetailsRef={}
        debugger
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
        var targetDetailsRef={}       
        targetDetailsRef.type="datapod";
        targetDetailsRef.uuid=$scope.selectedTargetDetail.uuid;
        targetDetails.ref=targetDetailsRef;
        ingestJson.targetDetail=targetDetails;
        console.log(JSON.stringify(ingestJson))
        IngestRuleService.submit(ingestJson, 'ingest',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
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