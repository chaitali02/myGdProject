/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateSimulateController', function ($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, SimulateService, CommonService, $http, $location,privilegeSvc) {
  $scope.attributeTypes=['datapod','dataset','rule'];
  $scope.mode = "false";
  $scope.isTargetNameDisabled = false;
  $scope.dataLoading = false;
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
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
  }
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.isSubmitEnable = false;
  $scope.simulateData;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.Simulate = {};
  $scope.Simulate.versions = [];
  $scope.isshowSimulate = false;
  $scope.sourceTypes = ["datapod", "dataset", "rule"];
  $scope.selectSourceType = $scope.sourceTypes[0];
  $scope.targetTypes = ["datapod", "file"];
  $scope.selectTargetType = $scope.targetTypes[0];
  $scope.factorTypes = ["datapod"];
  $scope.selectFactorMeanType = $scope.factorTypes[0];
  $scope.selectFactorCovarientType = $scope.factorTypes[0];
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isDependencyShow = false;
  $scope.simulationTypes=["DEFAULT","MONTECARLO"];
  $scope.selectSimulationType=$scope.simulationTypes[0];
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 30000 //time in ms
  };
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

  $scope.onChangeName = function (data) {
		$scope.simulateData.displayName=data;
  }
  
  $scope.close = function () {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('simulate');
    }
  }

  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
   // console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });
  $scope.generateRadomValue = function () {
    $scope.simulateData.seed = Math.floor(1000 + Math.random() * 9000);
    $scope.simulateData.numIterations = 1000;
  }
  $scope.countContinue = function () {
    if ($scope.simulateData.name != null || $scope.selectSourceType != null) {
      $scope.continueCount = $scope.continueCount + 1;
      if ($scope.continueCount >= 3) {
        $scope.isSubmitShow = true;
      } else {
        $scope.isSubmitShow = false;
      }
    }
  }

  $scope.countBack = function () {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }

  $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
  }
  $scope.showGraph = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  } //End showFunctionGraph


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
		$scope.showPage()
		$state.go('createsimulate', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
  $scope.enableEdit = function (uuid, version) {
    if($scope.isPrivlage || $scope.simulateData.locked =="Y"){
      return false;
    }
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showPage()
    $state.go('createsimulate', {
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
      $state.go('createsimulate', {
        id: uuid,
        version: version,
        mode: 'true'
      });
    }
  }

  $scope.getAllLetestModel = function (defaultValue) {
    SimulateService.getAllModelByType("N", "model").then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allModel = response;
      if (defaultValue == true) { }

    }
  }
  $scope.getAllLetestDistribution = function (defaultValue) {
    SimulateService.getAllLatest("distribution").then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allDistribution = response;
      if (typeof $stateParams.id == "undefined") {
        $scope.selectDistributionType = response[0]
      }

    }
  }
  $scope.getAllLatestDatapod = function () {
    CommonService.getAllLatest("datapod").then(function (response) { onSuccessGetAllLatest(response.data) });
    var onSuccessGetAllLatest = function (response) {
      $scope.allDatapod = response;
    }
  }

  // $scope.getAllLetestSource=function(){
  //   SimulateService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
  //   var onGetAllLatest = function(response) {
  //     $scope.allSource = response;
  //     if(typeof $stateParams.id == "undefined") {
  //       $scope.selectSource=response[0];
  //     }
  //   }
  // }

  $scope.getAllLetestParamList=function(defaultValue,selectParamList){
    SimulateService.getAllLatest("paramlist").then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allParamList = response;
      if(defaultValue){
        $scope.selectParamList=selectParamList;
      }
    }
  }

  $scope.getAllLetestTarget = function (defaultValue) {
    SimulateService.getAllLatest($scope.selectTargetType).then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allTarget = response;
      if (typeof $stateParams.id == "undefined" || defaultValue == true) {
        $scope.selectTarget = response[0];
      }
    }
  }
  $scope.getAllAttribute = function () {
    SimulateService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function (response) { onGetAllAttributeBySource(response.data) });
    var onGetAllAttributeBySource = function (response) {
      console.log(response)
      $scope.allTargetAttribute = response;


    }
  }
  $scope.getAllLetestModel();
  $scope.getAllLetestDistribution();
  // $scope.getAllLetestSource();
  $scope.getAllLetestTarget();

  $scope.onChangeModel = function () {
    SimulateService.getOneByUuidandVersion($scope.selectModel.uuid, $scope.selectModel.version || '', "model").then(function (response) { onSuccessGetLatestByUuid(response.data) });
    var onSuccessGetLatestByUuid = function (response) {
      var featureMapTableArray = [];
      for (var i = 0; i < response.features.length; i++) {
        var featureMap = {};
        var sourceFeature = {};
        var targetFeature = {};
        sourceFeature.featureId = response.features[i].featureId;
        sourceFeature.type = response.features[i].type;
        sourceFeature.datapodname = response.features[i].name;
        sourceFeature.name = response.features[i].name;
        sourceFeature.desc = response.features[i].desc;
        sourceFeature.minVal = response.features[i].minVal;
        sourceFeature.maxVal = response.features[i].maxVal;
        featureMap.sourceFeature = sourceFeature;
        featureMapTableArray[i] = featureMap;
      }
      $scope.featureMapTableArray = featureMapTableArray;
    }
  }
  $scope.onChangeTargeType = function () {
    if ($scope.selectTargetType == 'datapod') {
      $scope.isTargetNameDisabled = false;
      $scope.getAllLetestTarget(true);

    } else {
      $scope.isTargetNameDisabled = true;
      $scope.allTarget = [];
    }
  }
  $scope.getAllVersion = function (uuid) {
    SimulateService.getAllVersionByUuid(uuid, "simulate").then(function (response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var Simulateversion = {};
        Simulateversion.version = response[i].version;
        $scope.Simulate.versions[i] = Simulateversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion

  $scope.onChangeSourceType = function () {
    $scope.getAllLetestSource();
  }

  // $scope.onChangeSource = function() {
  //   if ($scope.allSource != null && $scope.selectSource != null) {
  //     $scope.getAllAttribute();
  //   }
  // }
  $scope.getOneByUuidandVersion = function (uuid, version) {
    SimulateService.getOneByUuidandVersion(uuid, version, "simulate").then(function (response) { onSuccessGetLatestByUuid(response.data) } , function (response) { onError(response.data)});
    var onSuccessGetLatestByUuid = function (response) {
      $scope.simulateData = response;
      $scope.isEditInprogess=false;
      var selectModel = {}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.Simulate.defaultVersion = defaultversion;
      selectModel.uuid = response.dependsOn.ref.uuid;
      selectModel.name = response.dependsOn.ref.name;
      $scope.selectModel = selectModel;
      $scope.selectSimulationType=response.type;
      var selectSource = {};
      // $scope.selectSource=null;
      // if(response.source !=null){
      //   $scope.selectSourceType=response.source.ref.type
      //   $scope.getAllLetestSource();
      //   selectSource.uuid=response.source.ref.uuid;
      //   selectSource.name=response.source.ref.name;
      //   $scope.selectSource=selectSource;
      // }
      var selectDistributionType = {};
      if(response.distributionTypeInfo != null) {
        selectDistributionType.uuid = response.distributionTypeInfo.ref.uuid;
        selectDistributionType.name = response.distributionTypeInfo.ref.name;
        $scope.selectDistributionType = selectDistributionType;
      }
      var selectParamList={};
      $scope.allParamList={};
      if(response.paramList !=null){
        selectParamList.uuid=response.paramList.ref.uuid;
        selectParamList.name=response.paramList.ref.name;
        $scope.getAllLetestParamList(true,selectParamList);
      }
      else{
        $scope.getAllLetestParamList(false,null);
      }
      var selectTarget = {};
      $scope.selectTarget = null;
      selectTarget.uuid = response.target.ref.uuid;
      selectTarget.name = response.target.ref.name;
      $scope.selectTargetType = response.target.ref.type
      $scope.selectTargetType == "file" ? $scope.isTargetNameDisabled = true : $scope.isTargetNameDisabled = false
      $scope.selectTarget = selectTarget;
      var featureMapTableArray = [];
      var tags = [];
      if (response.tags != null) {
        for (var i = 0; i < response.tags.length; i++) {
          var tag = {};
          tag.text = response.tags[i];
          tags[i] = tag
          $scope.tags = tags;
        }
      }
      $scope.onChangeModel();
    }
    var onError=function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    }
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.getAllVersion($stateParams.id)
    $scope.getOneByUuidandVersion($stateParams.id, $stateParams.version);
  }else{
    $scope.getAllLetestParamList(false,null);
    $scope.simulateData={};
    $scope.simulateData.locked="N"
  }

  $scope.selectVersion = function (uuid, version) {
    $scope.selectDistributionType = {};
    $scope.allDistribution = null;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.getAllLetestDistribution();
    $scope.getOneByUuidandVersion(uuid, version);
  }

  $scope.onChangeDistribution = function () {
    
    $scope.isShowExecutionParam = false;
    $scope.checkboxSimulateexecution = "No";

  }

  $scope.getAllLatest=function(type,index,defaultValue){  
    CommonService.getAllLatest(type || "datapod").then(function (response) { onSuccessGetAllLatest(response.data) });
    var onSuccessGetAllLatest = function (response) {
      if(type =="datapod"){
        $scope.allDatapod=response;
      }
      else if(type =="relation"){
        $scope.allRelation=response;
      }
      else if(type =="distribution"){
        $scope.allDistribution=response;
      }
      else if(type =="dataset"){
        $scope.allDataset=response;
      }
      else if(type =="rule"){
        $scope.allRule=response;
      }
     
    }
  }
  $scope.onChangeParamList=function(){
    $scope.isShowExecutionParam = false;
    $scope.checkboxSimulateexecution = "No";
  }

  $scope.onChangeForAttributeInfo=function(data,type,index){
    $scope.paramListHolder[index].attributeInfoTag=null;
    $scope.getAllAttributeBySource(data,type,index);

  }
  
  $scope.getAllAttributeBySource=function(data,type,index,defaultValue){ 
    if(data !=null){ 
      CommonService.getAllAttributeBySource(data.uuid,type).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
      var onSuccessGetAllAttributeBySource = function (response) {
        $scope.paramListHolder[index].allAttributeinto=response
      
      }
    }
  }
  $scope.onChangeRunImmediately = function () {
    $scope.isShowExecutionParam = false;
    if ($scope.selectDistributionType != null && $scope.checkboxSimulateexecution == "YES") {
     // $scope.getAllLatestDatapod();
      SimulateService.getParamListByDistribution($scope.selectDistributionType.uuid).then(function (response) {
        onSuccesGetParamListByDistribution(response.data)
      });
      var onSuccesGetParamListByDistribution = function (response) {
        $scope.paramListHolder = response;
        $scope.getParamByParamList();
        if (response.length > 0) {
          $scope.isShowExecutionParam = true;
          $('#executeParamList').modal({
            backdrop: 'static',
            keyboard: false
          });
          
        } else {
          $scope.isShowExecutionParam = false;
          $('#executeParamList').modal('hide');
          $scope.paramListHolder=[];
        }
      }
    } else {
      $scope.paramListHolder = [];
    }

  }

  $scope.getParamByParamList=function(){
    if($scope.selectParamList){
      SimulateService.getParamByParamList($scope.selectParamList.uuid).then(function (response){ onSuccesGetParamByParamList(response.data)});
      var onSuccesGetParamByParamList = function (response) {
        var paramList
        paramList = $scope.paramListHolder.concat(response);
        $scope.paramListHolder=paramList;
        if (response.length > 0 || $scope.paramListHolder.length > 0) {
          $scope.isShowExecutionParam = true; 
          $('#executeParamList').modal({
            backdrop: 'static',
            keyboard: false
          });
        } else {
          $scope.isShowExecutionParam = false;
          $('#executeParamList').modal('hide');
        }
      }
    }
  }

  $scope.showMessage = function (input) {
    var show = input.$invalid && (input.$dirty || input.$touched);
    return show;
  };

  $scope.submitModel = function () {
    var upd_tag="N"
    $scope.isshowSimulate = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var SimulateJson = {}
    SimulateJson.uuid = $scope.simulateData.uuid;
    SimulateJson.name = $scope.simulateData.name;
    SimulateJson.displayName = $scope.simulateData.displayName;
    SimulateJson.desc = $scope.simulateData.desc
    SimulateJson.active = $scope.simulateData.active;
    SimulateJson.locked = $scope.simulateData.locked;
    SimulateJson.published = $scope.simulateData.published;
    SimulateJson.publicFlag = $scope.simulateData.publicFlag;
    SimulateJson.numIterations = $scope.simulateData.numIterations;
    SimulateJson.type=$scope.selectSimulationType;
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
    SimulateJson.tags = tagArray;
    var dependsOn = {};
    var ref = {};
    ref.type = "model";
    ref.uuid = $scope.selectModel.uuid;
    dependsOn.ref = ref;
    SimulateJson.dependsOn = dependsOn;
    // if($scope.selectSource !=null){
    //   var source={};
    //   var sourceRef={};
    //   sourceRef.type=$scope.selectSourceType;
    //   sourceRef.uuid=$scope.selectSource.uuid;
    //   source.ref=sourceRef;
    //   SimulateJson.source=source;
    // }
      var paramList={};
     if($scope.selectParamList !=null){
      var paramListRef={};
      paramListRef.type="paramlist";
      paramListRef.uuid=$scope.selectParamList.uuid;
      paramList.ref=paramListRef;
     
    }else{
      paramList=null;
    }

    SimulateJson.paramList=paramList;
    if ($scope.selectDistributionType != null) {
      var distributionTypeInfo = {};
      var distributionTypeInfoRef = {};
      distributionTypeInfoRef.type = "distribution";
      distributionTypeInfoRef.uuid = $scope.selectDistributionType.uuid;
      distributionTypeInfo.ref = distributionTypeInfoRef;
      SimulateJson.distributionTypeInfo = distributionTypeInfo;
    } else {
      SimulateJson.distributionTypeInfo = null;
    }
    var target = {};
    var targetref = {};
    targetref.type = $scope.selectTargetType;
    if ($scope.selectTargetType == "datapod")
      targetref.uuid = $scope.selectTarget.uuid;
    target.ref = targetref;
    SimulateJson.target = target;
    var featureInfo = [];
    if ($scope.featureMapTableArray.length > 0) {
      for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
        var featureInfoObj = {};
        var featureInfoRef = {}
        featureInfoObj.featureId = $scope.featureMapTableArray[i].sourceFeature.featureId;
        featureInfoObj.featureName = $scope.featureMapTableArray[i].sourceFeature.name;
        featureInfoRef.uuid = $scope.selectModel.uuid;
        featureInfoRef.type = 'model';
        featureInfoObj.ref = featureInfoRef;
        featureInfo[i] = featureInfoObj;
      }
    }
    SimulateJson.featureInfo = featureInfo;
    console.log(JSON.stringify(SimulateJson))
    SimulateService.submit(SimulateJson, 'simulate',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if ($scope.checkboxSimulateexecution == "YES") {
        SimulateService.getOneById(response, "simulate").then(function (response) { onSuccessGetOneById(response.data) });
        var onSuccessGetOneById = function (result) {
          $scope.simulateExecute(result);
        }
      } //End if
      else {
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = 'Configuration Saved Successfully'
        $scope.$emit('notify', notify);
        $scope.okmodelsave();
      }
    }
    var onError = function (response) {
      notify.type = 'error',
        notify.title = 'Error',
        notify.content = "Some Error Occurred"
      $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
  }

  $scope.okmodelsave = function () {
    $('#modelsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function () {
        $state.go("simulate");
      }, 2000);
    }
  }
  $scope.changemodelvalue = function () {
    $scope.isshowSimulate = sessionStorage.isshowmodel
  };

  $scope.executeWithExecParamList=function(){
    $('#executeParamList').modal('hide');
  }
  $scope.simulateExecute = function (response) {
    var execParams = {};
    var paramListInfo = [];
    if ($scope.paramListHolder.length > 0) {
      for(var i=0;i<$scope.paramListHolder.length;i++){
        var paramList={};
        paramList.paramId=$scope.paramListHolder[i].paramId;
        paramList.paramName=$scope.paramListHolder[i].paramName;
        paramList.paramType=$scope.paramListHolder[i].paramType;
        paramList.ref=$scope.paramListHolder[i].ref;
        if($scope.paramListHolder[i].paramType =='attribute'){
          var attributeInfoArray=[];
          var attributeInfo={};
          var attributeInfoRef={}
          attributeInfoRef.type=$scope.paramListHolder[i].selectedParamValueType;
          attributeInfoRef.uuid=$scope.paramListHolder[i].attributeInfo.uuid;
          attributeInfoRef.name=$scope.paramListHolder[i].attributeInfo.name
          attributeInfo.ref=attributeInfoRef;
          attributeInfo.attrId=$scope.paramListHolder[i].attributeInfo.attributeId;
          attributeInfoArray[0]=attributeInfo
          paramList.attributeInfo=attributeInfoArray;

        }
        if($scope.paramListHolder[i].paramType =='attributes'){
          var attributeInfoArray=[];
          for(var j=0;j<$scope.paramListHolder[i].attributeInfoTag.length;j++){
            var attributeInfo={};
            var attributeInfoRef={}
            attributeInfoRef.type=$scope.paramListHolder[i].selectedParamValueType;
            attributeInfoRef.uuid=$scope.paramListHolder[i].attributeInfoTag[j].uuid
            attributeInfoRef.name=$scope.paramListHolder[i].attributeInfoTag[j].datapodname
            attributeInfo.ref=attributeInfoRef;
            attributeInfo.attrId=$scope.paramListHolder[i].attributeInfoTag[j].attributeId;
            attributeInfo.attrName=$scope.paramListHolder[i].attributeInfoTag[j].name;
            attributeInfoArray[j]=attributeInfo
          }
          paramList.attributeInfo=attributeInfoArray;
        }
        else if($scope.paramListHolder[i].paramType=='distribution' || $scope.paramListHolder[i].paramType=='datapod'){
          var ref={};
          var paramValue={};  
          ref.type=$scope.paramListHolder[i].selectedParamValueType;
          ref.uuid=$scope.paramListHolder[i].selectedParamValue.uuid;  
          paramValue.ref=ref;
          paramList.paramValue=paramValue;
        }
        else if($scope.paramListHolder[i].selectedParamValueType =="simple"){
          var ref={};
          var paramValue={};  
          ref.type=$scope.paramListHolder[i].selectedParamValueType;
          paramValue.ref=ref;
          paramValue.value=$scope.paramListHolder[i].paramValue
          paramList.paramValue=paramValue;
          
        }
        else if($scope.paramListHolder[i].selectedParamValueType =="list"){
          var ref={};
          var paramValue={};  
          ref.type='simple';
          paramValue.ref=ref;
          paramValue.value=$scope.paramListHolder[i].paramValue
          paramList.paramValue=paramValue;
        }
       
        paramListInfo[i]=paramList;
      }
      execParams.paramListInfo = paramListInfo;
    }
    else {
      execParams = null;
    }
    CommonService.executeWithParams("simulate", response.uuid, response.version, execParams).then(function (response) { onSuccessExectute() }, function (response) { onError() });
    var onSuccessExectute = function () {
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = 'Configuration Submited and Saved Successfully'
      $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
    var onError = function () {
      // notify.type='success',
      // notify.title= 'Success',
      // notify.content='Configuration Submited and Saved Successfully'
      // $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
  }

}); //End CreateModelController

