/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateTrainController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, TrainService,$http,$location,CommonService) {

  $scope.isTargetNameDisabled=false;
  $scope.dataLoading = false;
  if($stateParams.mode =='true'){
    $scope.isEdit=false;
    $scope.isversionEnable=false;
  }
  else{
    $scope.isEdit=true;
    $scope.isversionEnable=true;
  }
  
  $scope.mode="false"
  
  $scope.isSubmitEnable = false;
  $scope.Traindata;
  $scope.showTrain = true;
  $scope.data = null;
  $scope.showgraph = false
  $scope.showgraphdiv = false
  $scope.graphDataStatus = false
  $scope.Train = {};
  $scope.Train.versions = [];
  $scope.isshowTrain = false;
  $scope.sourceTypes = ["datapod", "dataset","rule"];
  $scope.selectSourceType=$scope.sourceTypes[0];
  $scope.targetTypes = ["datapod","file"];
  $scope.selectTargetType=$scope.targetTypes[0]; 
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
      $state.go('train');
    }
  }
  
  $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });
   
  $scope.countContinue = function() {
    if($scope.Traindata.name!=null || $scope.selectSourceType!=null){
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

 
  $scope.showTrainGraph = function(uuid, version) {
    $scope.showTrain = false;
    $scope.showgraph = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;
  } //End showFunctionGraph


  $scope.showTrainPage = function() {
    $scope.showTrain = true;
    $scope.showgraph = false
    $scope.graphDataStatus = false;
    $scope.showgraphdiv = false
  }
  

  $scope.getAllLetestModel=function(defaultValue){
    TrainService.getAllLatest("model").then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allModel = response;
      if(defaultValue ==true){}
      
    }
  }
  $scope.getAllLetestSource=function(){
    TrainService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allSource = response;
      if(typeof $stateParams.id == "undefined") {
        $scope.selectSource=response[0];
        $scope.getAllAttribute();
      }
    }
  }
  // $scope.getAllLetestTarget=function(defaultValue){
  //   TrainService.getAllLatest($scope.selectTargetType).then(function(response) { onGetAllLatest(response.data)});
  //   var onGetAllLatest = function(response) {
  //     $scope.allTarget = response;
  //     if(typeof $stateParams.id == "undefined" || defaultValue ==true) {
  //       $scope.selectTarget=response[0];
  //     }
  //   }
  // }
  $scope.getAllAttribute=function(){
    TrainService.getAllAttributeBySource($scope.selectSource.uuid,$scope.selectSourceType).then(function(response) { onGetAllAttributeBySource(response.data)});
    var onGetAllAttributeBySource = function(response) {
      console.log(response)
      $scope.allTargetAttribute = response;
      
      
    }
  }
  $scope.getAllLetestModel();
  $scope.getAllLetestSource();
  // $scope.getAllLetestTarget();
  
  $scope.onChangeModel=function(){
    TrainService.getOneByUuidandVersion($scope.selectModel.uuid,$scope.selectModel.version,"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      var featureMapTableArray=[];
      for(var i=0;i<response.features.length;i++){
        var featureMap={};
        var sourceFeature={};
        var targetFeature={};
        featureMap.featureMapId=i;
        // sourceFeature.uuid = response.features[i].ref.uuid;
        // sourceFeature.type = response.features[i].ref.type;
        // sourceFeature.datapodname = response.features[i].ref.name;
        // sourceFeature.name = response.features[i].attrName;
        // sourceFeature.attributeId = response.features[i].attrId;
        // sourceFeature.id = response.features[i].ref.uuid + "_" + response.features[i].attrId;
        // sourceFeature.dname = response.features[i].ref.name + "." + response.features[i].attrName;
        sourceFeature.uuid = response.uuid;
        sourceFeature.type = "model";
        sourceFeature.featureId = response.features[i].featureId;
        sourceFeature.featureName = response.features[i].name;
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
    TrainService.getAllVersionByUuid(uuid, "train").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var Trainversion = {};
        Trainversion.version = response[i].version;
        $scope.Train.versions[i] = Trainversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion
 
  $scope.onChangeSourceType = function() {
    TrainService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allSource = response
      $scope.selectSource=response[0];
      $scope.onChangeSource();
    }
  }

  $scope.onChangeSource = function() {
    if ($scope.allSource != null && $scope.selectSource != null) {
      $scope.getAllAttribute();
    }
  }
  $scope.getOneByUuidandVersion=function(uuid,version){
    TrainService.getOneByUuidandVersion(uuid,version,"train").then(function(response){ onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.Traindata = response;
      var selectModel={}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.Train.defaultVersion = defaultversion;
      selectModel.uuid=response.dependsOn.ref.uuid;
      selectModel.name=response.dependsOn.ref.name;
      $scope.selectModel=selectModel;
      var selectSource={};
      $scope.selectSource=null;
      selectSource.uuid=response.source.ref.uuid;
      selectSource.name=response.source.ref.name;
      $scope.selectSource=selectSource;
      // var selectTarget={};
      // $scope.selectTarget=null;
      // selectTarget.uuid=response.target.ref.uuid;
      // selectTarget.name=response.target.ref.name;
      // $scope.selectTargetType=response.target.ref.type
     // $scope.selectTargetType=="file"?$scope.isTargetNameDisabled=true:$scope.isTargetNameDisabled=false
     // $scope.selectTarget=selectTarget;
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
      $scope.getAllAttribute();
      for(var i=0;i<response.featureAttrMap.length;i++){
        var featureAttrMap={};
        var sourceFeature={};
        var targetFeature={};
        featureAttrMap.featureAttrMapId=response.featureAttrMap[i].featureMapId;
        // sourceFeature.datapodname = response.featureMap[i].sourceFeature.ref.name;
        // sourceFeature.name = response.featureMap[i].sourceFeature.attrName;
        // sourceFeature.attributeId = response.featureMap[i].sourceFeature.attrId;
        // sourceFeature.id = response.featureMap[i].sourceFeature.ref.uuid + "_" + response.featureMap[i].sourceFeature.attrId;
        // sourceFeature.dname = response.featureMap[i].sourceFeature.ref.name + "." + response.featureMap[i].sourceFeature.attrName;
        sourceFeature.uuid = response.featureAttrMap[i].feature.ref.uuid;
        sourceFeature.type = response.featureAttrMap[i].feature.ref.type;
        sourceFeature.featureId = response.featureAttrMap[i].feature.featureId;
        sourceFeature.featureName = response.featureAttrMap[i].feature.featureName;
        featureAttrMap.sourceFeature=sourceFeature;
        targetFeature.uuid = response.featureAttrMap[i].attribute.ref.uuid;
        targetFeature.type = response.featureAttrMap[i].attribute.ref.type;
        targetFeature.datapodname = response.featureAttrMap[i].attribute.ref.name;
        targetFeature.name = response.featureAttrMap[i].attribute.attrName;
        targetFeature.attributeId = response.featureAttrMap[i].attribute.attrId;
        targetFeature.id = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
        targetFeature.dname = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
        featureAttrMap.targetFeature=targetFeature;
        featureMapTableArray[i]=featureAttrMap;
      }
      $scope.featureMapTableArray=featureMapTableArray;
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
    $scope.allSource=[];
    $scope.allTarget=[];
    $scope.allModel =[];
    $scope.getAllLetestModel();
    $scope.getAllLetestSource();
    $scope.getAllLetestTarget();
    $scope.getOneByUuidandVersion(uuid,version);
  }

  $scope.submitModel = function() {
    $scope.isshowTrain = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var TrainJson = {}
    TrainJson.uuid = $scope.Traindata.uuid
    TrainJson.name = $scope.Traindata.name
    TrainJson.desc = $scope.Traindata.desc
    TrainJson.active = $scope.Traindata.active;
    TrainJson.published=$scope.Traindata.published; 
    TrainJson.valPercent=$scope.Traindata.valPercent; 
    TrainJson.trainPercent=$scope.Traindata.trainPercent; 
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    TrainJson.tags = tagArray;
    var dependsOn={};
    var ref={};
    ref.type="model";
    ref.uuid=$scope.selectModel.uuid;
    dependsOn.ref=ref;
    TrainJson.dependsOn=dependsOn;
    var source={};
    var sourceref={};
    sourceref.type=$scope.selectSourceType;
    sourceref.uuid=$scope.selectSource.uuid;
    source.ref=sourceref;
    TrainJson.source=source;
    // var target={};
    // var targetref={};
    // targetref.type=$scope.selectTargetType;
    // if($scope.selectTargetType =="datapod")
    // targetref.uuid=$scope.selectTarget.uuid;
    // target.ref=targetref;
    // TrainJson.target=target;
    var featureMap=[];
    if($scope.featureMapTableArray.length >0){
      for(var i=0;i<$scope.featureMapTableArray.length;i++){
        var featureMapObj={};
        featureMapObj.featureMapId=i;
        var sourceFeature={};
        var sourceFeatureRef={};
        var targetFeature={};
        var targetFeatureRef={};
        sourceFeatureRef.uuid = $scope.featureMapTableArray[i].sourceFeature.uuid;
        sourceFeatureRef.type = $scope.featureMapTableArray[i].sourceFeature.type;
        sourceFeature.ref=sourceFeatureRef;
        //sourceFeature.attrId = $scope.featureMapTableArray[i].sourceFeature.attributeId;
        sourceFeature.featureId = $scope.featureMapTableArray[i].sourceFeature.featureId;
        sourceFeature.featureName = $scope.featureMapTableArray[i].sourceFeature.featureName;
        featureMapObj.feature=sourceFeature;
        targetFeatureRef.uuid = $scope.featureMapTableArray[i].targetFeature.uuid;
        targetFeatureRef.type =$scope.selectSourceType;
        targetFeature.ref=targetFeatureRef
        targetFeature.attrId =$scope.featureMapTableArray[i].targetFeature.attributeId;
        
        featureMapObj.attribute=targetFeature;
        featureMap[i]=featureMapObj;
      }
    }
    TrainJson.featureAttrMap=featureMap;
    console.log(JSON.stringify(TrainJson))
    TrainService.submit(TrainJson, 'train').then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if($scope.checkboxTrainexecution == "YES") {
        TrainService.getOneById(response, "train").then(function(response) { onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          $scope.trainExecute(result);
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
        $state.go("train");
      }, 2000);
    }
  }
  $scope.changemodelvalue = function() {
    $scope.isshowTrain = sessionStorage.isshowmodel
  };
  $scope.trainExecute=function(response){
    CommonService.executeWithParams("train", response.uuid, response.version,null).then(function(response) { onSuccessExectute()});
    var onSuccessExectute = function(){
      notify.type='success',
      notify.title= 'Success',
      notify.content='Configuration Submited and Saved Successfully'
      $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
    
  }
  $scope.onChageTrainPercent=function(){
    $scope.Traindata.valPercent=100-$scope.Traindata.trainPercent;
  }

  $scope.onChageValPercent=function(){
    $scope.Traindata.trainPercent=100-$scope.Traindata.valPercent;
  
  }
}); //End CreateModelController
