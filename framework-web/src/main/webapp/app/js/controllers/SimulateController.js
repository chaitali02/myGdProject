/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateSimulateController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, SimulateService,CommonService,$http,$location) {

  $scope.mode = "false";
  $scope.isTargetNameDisabled=false;
  $scope.dataLoading = false;
  if($stateParams.mode =='true'){
    $scope.isEdit=false;
    $scope.isversionEnable=false;
    $scope.isAdd=false;
  }
  else if($stateParams.mode =='false'){
    $scope.isEdit=true;
    $scope.isversionEnable=true;
    $scope.isAdd=false;
  }
  else{
    $scope.isAdd=true;
  }
  $scope.isSubmitEnable = false;
  $scope.simulateData;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.Simulate = {};
  
  $scope.Simulate.versions = [];
  $scope.isshowSimulate = false;
  $scope.sourceTypes = ["datapod", "dataset","rule"];
  $scope.selectSourceType=$scope.sourceTypes[0];
  $scope.targetTypes = ["datapod","file"];
  $scope.selectTargetType=$scope.targetTypes[0];
  $scope.factorTypes = ["datapod"];
  $scope.selectFactorMeanType=$scope.factorTypes[0];
  $scope.selectFactorCovarientType=$scope.factorTypes[0];
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isDependencyShow = false;    
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 30000 //time in ms
  };
  
  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('simulate');
    }
  }
  
  $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });
  $scope.generateRadomValue=function(){
    $scope.simulateData.seed= Math.floor(1000 + Math.random() * 9000);
    $scope.simulateData.numIterations=1000;
  }
  $scope.countContinue = function() {
    if($scope.simulateData.name!=null || $scope.selectSourceType!=null){
      $scope.continueCount = $scope.continueCount + 1;
      if ($scope.continueCount >= 3) {
        $scope.isSubmitShow = true;
      } else {
        $scope.isSubmitShow = false;
      }
    }
  }

  $scope.countBack = function() {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  } //End showFunctionGraph


  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false
  }

  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createsimulate', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createsimulate', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }     
  }

  $scope.getAllLetestModel=function(defaultValue){
    SimulateService.getAllModelByType("N","model").then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allModel = response;
      if(defaultValue ==true){}
      
    }
  }
  $scope.getAllLetestDistribution=function(defaultValue){
    SimulateService.getAllLatest("distribution").then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allDistribution = response;
      if(defaultValue ==true){
        $scope.selectDistributionType=response[0]
      }
      
    }
  }
  $scope.getAllLatestDatapod=function(){
    CommonService.getAllLatest("datapod").then(function (response) { onSuccessGetAllLatest(response.data) });
		var onSuccessGetAllLatest = function (response) {
      $scope.allDatapod=response;
    }
  }

  $scope.getAllLetestSource=function(){
    SimulateService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allSource = response;
      if(typeof $stateParams.id == "undefined") {
        $scope.selectSource=response[0];
      }
    }
  }
  $scope.getAllLetestTarget=function(defaultValue){
    SimulateService.getAllLatest($scope.selectTargetType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allTarget = response;
      if(typeof $stateParams.id == "undefined" || defaultValue ==true) {
        $scope.selectTarget=response[0];
      }
    }
  }
  $scope.getAllAttribute=function(){
    SimulateService.getAllAttributeBySource($scope.selectSource.uuid,$scope.selectSourceType).then(function(response) { onGetAllAttributeBySource(response.data)});
    var onGetAllAttributeBySource = function(response) {
      console.log(response)
      $scope.allTargetAttribute = response;
      
      
    }
  }
  $scope.getAllLetestModel();
  $scope.getAllLetestDistribution();
  $scope.getAllLetestSource();
  $scope.getAllLetestTarget();
  
  $scope.onChangeModel=function(){
    SimulateService.getOneByUuidandVersion($scope.selectModel.uuid,$scope.selectModel.version || '',"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      var featureMapTableArray=[];
      for(var i=0;i<response.features.length;i++){
        var featureMap={};
        var sourceFeature={};
        var targetFeature={};
        sourceFeature.featureId = response.features[i].featureId;
        sourceFeature.type = response.features[i].type  ;
        sourceFeature.datapodname = response.features[i].name;
        sourceFeature.name = response.features[i].name;
        sourceFeature.desc = response.features[i].desc;
        sourceFeature.minVal = response.features[i].minVal;
        sourceFeature.maxVal = response.features[i].maxVal;
        featureMap.sourceFeature=sourceFeature;
        featureMapTableArray[i]=featureMap;
      }
      $scope.featureMapTableArray=featureMapTableArray;
  }
}
  $scope.onChangeTargeType=function(){
    if($scope.selectTargetType =='datapod'){
      $scope.isTargetNameDisabled=false;
      $scope.getAllLetestTarget(true);
      
    }else{
      $scope.isTargetNameDisabled=true;
      $scope.allTarget =[];
    }
  }
  $scope.getAllVersion = function(uuid) {
    SimulateService.getAllVersionByUuid(uuid, "simulate").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var Simulateversion = {};
        Simulateversion.version = response[i].version;
        $scope.Simulate.versions[i] = Simulateversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion
 
  $scope.onChangeSourceType = function() {
    $scope.getAllLetestSource();
  }

  // $scope.onChangeSource = function() {
  //   if ($scope.allSource != null && $scope.selectSource != null) {
  //     $scope.getAllAttribute();
  //   }
  // }
  $scope.getOneByUuidandVersion=function(uuid,version){
    SimulateService.getOneByUuidandVersion(uuid,version,"simulate").then(function(response){ onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.simulateData = response;
      var selectModel={}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.Simulate.defaultVersion = defaultversion;
      selectModel.uuid=response.dependsOn.ref.uuid;
      selectModel.name=response.dependsOn.ref.name;
      $scope.selectModel=selectModel;
      var selectSource={};
      $scope.selectSource=null;
      if(response.source !=null){
        $scope.selectSourceType=response.source.ref.type
        $scope.getAllLetestSource();
        selectSource.uuid=response.source.ref.uuid;
        selectSource.name=response.source.ref.name;
        $scope.selectSource=selectSource;
      }
      var selectDistributionType={};
      if(response.distributionTypeInfo !=null){
        selectDistributionType.uuid=response.distributionTypeInfo.ref.uuid;
        selectDistributionType.name=response.distributionTypeInfo.ref.name;      
        $scope.selectDistributionType=selectDistributionType;        
      }
      var selectTarget={};
      $scope.selectTarget=null;
      selectTarget.uuid=response.target.ref.uuid;
      selectTarget.name=response.target.ref.name;
      $scope.selectTargetType=response.target.ref.type
      $scope.selectTargetType=="file"?$scope.isTargetNameDisabled=true:$scope.isTargetNameDisabled=false
      $scope.selectTarget=selectTarget;
      var featureMapTableArray=[];
      var tags=[];
			if(response.tags!=null){
			  for(var i=0;i<response.tags.length;i++){
			  	var tag={};
			  	tag.text=response.tags[i];
			  	tags[i]=tag
			  	$scope.tags=tags;
			  }
      }
      $scope.onChangeModel();
    }
  }
  
  if(typeof $stateParams.id != "undefined") {
    $scope.showactive="true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.getAllVersion($stateParams.id)
    $scope.getOneByUuidandVersion($stateParams.id,$stateParams.version);
  }
 
  $scope.selectVersion=function(uuid,version){
    $scope.selectDistributionType={};
    $scope.allDistribution=null;
    $scope.getAllLetestDistribution();
    $scope.getOneByUuidandVersion(uuid,version);
  }
  
  $scope.onChangeDistribution=function(){
    if($scope.selectDistributionType !=null){
       $scope.checkboxSimulateexecution="No";
    }else{
      $scope.isShowExecutionParam=false;
      $scope.checkboxSimulateexecution="No";  
    }
  }
  $scope.onChangeRunImmediately=function(){
    $scope.isShowExecutionParam=false;
    if($scope.selectDistributionType !=null){
      $scope.getAllLatestDatapod();
      SimulateService.getParamListByDistribution($scope.selectDistributionType.uuid).then(function(response) {
        onSuccesGetParamListByDistribution(response.data)
      });
      var onSuccesGetParamListByDistribution = function(response) {
        $scope.paramListHolder=response;
        if(response.length >0){
          $scope.isShowExecutionParam=true;
        }else{
          $scope.isShowExecutionParam=false;
        }
      }
    }else{
      $scope.paramListHolder=[];
    }
   
  }
  $scope.showMessage = function(input) {
    var show = input.$invalid && (input.$dirty || input.$touched);
    return show;
  };

  $scope.submitModel = function() {
    $scope.isshowSimulate = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var SimulateJson = {}
    SimulateJson.uuid = $scope.simulateData.uuid
    SimulateJson.name = $scope.simulateData.name
    SimulateJson.desc = $scope.simulateData.desc
    SimulateJson.active = $scope.simulateData.active;
    SimulateJson.published=$scope.simulateData.published; 
    SimulateJson.numIterations=$scope.simulateData.numIterations;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    SimulateJson.tags = tagArray;
    var dependsOn={};
    var ref={};
    ref.type="model";
    ref.uuid=$scope.selectModel.uuid;
    dependsOn.ref=ref;
    SimulateJson.dependsOn=dependsOn;
    if($scope.selectSource !=null){
      var source={};
      var sourceRef={};
      sourceRef.type=$scope.selectSourceType;
      sourceRef.uuid=$scope.selectSource.uuid;
      source.ref=sourceRef;
      SimulateJson.source=source;
    }
    if($scope.selectDistributionType !=null){
      var distributionTypeInfo={};
      var distributionTypeInfoRef={};
      distributionTypeInfoRef.type="distribution";
      distributionTypeInfoRef.uuid=$scope.selectDistributionType.uuid;
      distributionTypeInfo.ref=distributionTypeInfoRef;
      SimulateJson.distributionTypeInfo=distributionTypeInfo;
    }else{
      SimulateJson.distributionTypeInfo=null;
    }
    var target={};
    var targetref={};
    targetref.type=$scope.selectTargetType;
    if($scope.selectTargetType =="datapod")
    targetref.uuid=$scope.selectTarget.uuid;
    target.ref=targetref;
    SimulateJson.target=target;
    var featureInfo=[];
    if($scope.featureMapTableArray.length >0){
      for(var i=0;i<$scope.featureMapTableArray.length;i++){
        var featureInfoObj={};
        var featureInfoRef={}
        featureInfoObj.featureId=$scope.featureMapTableArray[i].sourceFeature.featureId;
        featureInfoObj.featureName=$scope.featureMapTableArray[i].sourceFeature.name;
        featureInfoRef.uuid = $scope.selectModel.uuid;
        featureInfoRef.type = 'model';
        featureInfoObj.ref=featureInfoRef;
        featureInfo[i]=featureInfoObj;
      }
    }
    SimulateJson.featureInfo=featureInfo;
    console.log(JSON.stringify(SimulateJson))
   SimulateService.submit(SimulateJson, 'simulate').then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if($scope.checkboxSimulateexecution == "YES") {
        SimulateService.getOneById(response, "simulate").then(function(response) { onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          $scope.simulateExecute(result);
        }
      } //End if
      else {
        notify.type='success',
        notify.title= 'Success',
        notify.content='Configuration Saved Successfully'
        $scope.$emit('notify', notify);
        $scope.okmodelsave();
      }
    }
    var onError = function(response) {
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  }

  $scope.okmodelsave = function() {
    $('#modelsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go("simulate");
      }, 2000);
    }
  }
  $scope.changemodelvalue = function() {
    $scope.isshowSimulate = sessionStorage.isshowmodel
  };
  $scope.simulateExecute=function(response){
    var execParams={};
    var paramListInfo=[];
    if($scope.paramListHolder.length>0){
      for(var i=0;i<$scope.paramListHolder.length;i++){
        var paramList={};
        paramList.paramId=$scope.paramListHolder[i].paramId;
        paramList.paramName=$scope.paramListHolder[i].paramName;
        paramList.paramType=$scope.paramListHolder[i].paramType;
        var ref={};
        var paramValue={};  
        if($scope.paramListHolder[i].paramType !="row"){
          ref.type="simple";
          paramValue.ref=ref;
          paramValue.value=$scope.paramListHolder[i].paramValue;  
        }
        else{
          ref.type="datapod";
          ref.uuid=$scope.paramListHolder[i].selectedParamValue.uuid;  
          paramValue.ref=ref;
        }
        paramList.paramValue=paramValue;
        paramListInfo[i]=paramList;
      }
      execParams.paramListInfo=paramListInfo;
    }
    else{
      execParams=null;
    }
    CommonService.executeWithParams("simulate", response.uuid, response.version,execParams).then(function(response) { onSuccessExectute()});
    var onSuccessExectute = function(){
      notify.type='success',
      notify.title= 'Success',
      notify.content='Configuration Submited and Saved Successfully'
      $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
  }

}); //End CreateModelController

