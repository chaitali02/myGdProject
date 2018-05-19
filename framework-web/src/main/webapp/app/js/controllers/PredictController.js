/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreatePredictController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, PredictService,$http,$location) {

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
  $scope.mode="false"
  
  $scope.isSubmitEnable = false;
  $scope.predictData;
  $scope.showFrom = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.predict = {};
  $scope.predict.versions = [];
  $scope.isshowPredict = false;
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
  $scope.pagination={
    currentPage:1,
    pageSize:10,
    usePageSize:10,
    paginationPageSizes:["All",5,10,25,50],
    maxSize:5,
  }  

  
  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('predict');
    }
  }
  
  $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
    //console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });
   
  $scope.countContinue = function() {
    if($scope.predictData.name!=null || $scope.selectSourceType!=null){
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
    $scope.showFrom = false;
    $scope.showGraphDiv = true;
  } //End showFunctionGraph


  $scope.showPage = function() {
    $scope.showFrom = true;
    $scope.showGraphDiv = false
  }
  
  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createpredict', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createpredict', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }     
  }

  $scope.getAllLetestModel=function(defaultValue){
    PredictService.getAllModelByType("N","model").then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allModel = response;
      if(defaultValue ==true){}
      
    }
  }
  $scope.getAllLetestSource=function(){
    PredictService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allSource = response;
      if(typeof $stateParams.id == "undefined") {
        $scope.selectSource=response[0];
        $scope.getAllAttribute();
      }
    }
  }
  $scope.getAllLetestTarget=function(defaultValue){
    PredictService.getAllLatest($scope.selectTargetType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allTarget = response;
      if(typeof $stateParams.id == "undefined" || defaultValue ==true) {
        $scope.selectTarget=response[0];
      }
    }
  }
  $scope.getAllAttribute=function(){
    PredictService.getAllAttributeBySource($scope.selectSource.uuid,$scope.selectSourceType).then(function(response) { onGetAllAttributeBySource(response.data)});
    var onGetAllAttributeBySource = function(response) {
      //console.log(response)
      $scope.allTargetAttribute = response;
      
      
    }
  }
  $scope.getAllLetestModel();
  $scope.getAllLetestSource();
  $scope.getAllLetestTarget();
  
  $scope.onChangeModel=function(){
    PredictService.getOneByUuidandVersion($scope.selectModel.uuid,$scope.selectModel.version,"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
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
      $scope.originalFeatureMapTableArray=featureMapTableArray;
      $scope.featureMapTableArray =$scope.getResults($scope.pagination,featureMapTableArray);
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
    PredictService.getAllVersionByUuid(uuid, "predict").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var predictversion = {};
        predictversion.version = response[i].version;
        $scope.predict.versions[i] = predictversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion
 
  $scope.onChangeSourceType = function() {
    PredictService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
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
    PredictService.getOneByUuidandVersion(uuid,version,"predict").then(function(response){ onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.predictData = response;
      var selectModel={}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.predict.defaultVersion = defaultversion;
      selectModel.uuid=response.dependsOn.ref.uuid;
      selectModel.name=response.dependsOn.ref.name;
      $scope.selectModel=selectModel;
      var selectSource={};
      $scope.selectSource=null;
      selectSource.uuid=response.source.ref.uuid;
      selectSource.name=response.source.ref.name;
      $scope.selectSource=selectSource;
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
      $scope.getAllAttribute();
      for(var i=0;i<response.featureAttrMap.length;i++){
        var featureMap={};
        var sourceFeature={};
        var targetFeature={};
        featureMap.featureMapId=response.featureAttrMap[i].featureMapId;
        sourceFeature.uuid = response.featureAttrMap[i].feature.ref.uuid;
        sourceFeature.type = response.featureAttrMap[i].feature.ref.type;
        sourceFeature.featureId = response.featureAttrMap[i].feature.featureId;
        sourceFeature.featureName = response.featureAttrMap[i].feature.featureName;
        featureMap.sourceFeature=sourceFeature;
        targetFeature.uuid = response.featureAttrMap[i].attribute.ref.uuid;
        targetFeature.type = response.featureAttrMap[i].attribute.ref.type;
        targetFeature.datapodname = response.featureAttrMap[i].attribute.ref.name;
        targetFeature.name = response.featureAttrMap[i].attribute.attrName;
        targetFeature.attributeId = response.featureAttrMap[i].attribute.attrId;
        targetFeature.id = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
        targetFeature.dname = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
        featureMap.targetFeature=targetFeature;
        featureMapTableArray[i]=featureMap;
      }
      $scope.originalFeatureMapTableArray=featureMapTableArray;
      $scope.featureMapTableArray =$scope.getResults($scope.pagination,featureMapTableArray);
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
    $scope.isshowPredict = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var predictJson = {}
    predictJson.uuid = $scope.predictData.uuid
    predictJson.name = $scope.predictData.name
    predictJson.desc = $scope.predictData.desc
    predictJson.active = $scope.predictData.active;
    predictJson.published=$scope.predictData.published; 
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    predictJson.tags = tagArray;
    var dependsOn={};
    var ref={};
    ref.type="model";
    ref.uuid=$scope.selectModel.uuid;
    dependsOn.ref=ref;
    predictJson.dependsOn=dependsOn;
    var source={};
    var sourceref={};
    sourceref.type=$scope.selectSourceType;
    sourceref.uuid=$scope.selectSource.uuid;
    source.ref=sourceref;
    predictJson.source=source;
    var target={};
    var targetref={};
    targetref.type=$scope.selectTargetType;
    if($scope.selectTargetType =="datapod")
    targetref.uuid=$scope.selectTarget.uuid;
    target.ref=targetref;
    predictJson.target=target;
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
    predictJson.featureAttrMap=featureMap;
    //console.log(JSON.stringify(predictJson))
    PredictService.submit(predictJson, 'predict').then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if($scope.checkboxPredictexecution == "YES") {
        PredictService.getOneById(response, "predict").then(function(response) { onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          $scope.predictExecute(result);
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
        $state.go("predict");
      }, 2000);
    }
  }
  $scope.changemodelvalue = function() {
    $scope.isshowPredict = sessionStorage.isshowmodel
  };
  $scope.predictExecute=function(response){
    PredictService.getOneById(response, "predict").then(function(response) { onSuccessGetOneById(response.data)});
    var onSuccessGetOneById = function(result) {
      PredictService.getExecutePredict(response.uuid,response.version,null).then(function(response) { onSuccessGetExecutePredict(response.data)});
      var onSuccessGetExecutePredict = function(result) {
        notify.type='success',
        notify.title= 'Success',
        notify.content='Configuration Submited and Saved Successfully'
        $scope.$emit('notify', notify);
        $scope.okmodelsave();
      }
    }
  }

  $scope.getResults = function(pagination,params) {
    pagination.totalItems=params.length;
    if(pagination.totalItems >0){
      pagination.to = (((pagination.currentPage - 1) * (pagination.usePageSize))+1);
    }
    else{
      pagination.to=0;
    }
    if(pagination.totalItems < (pagination.usePageSize*pagination.currentPage)) {
        pagination.from = pagination.totalItems;
    } else {
      pagination.from = ((pagination.currentPage) * pagination.usePageSize);
    }
    var limit = (pagination.usePageSize* pagination.currentPage);
    var offset = ((pagination.currentPage - 1) * pagination.usePageSize)
    return params.slice(offset,limit);
  }

  $scope.onPageChanged = function(){
    $scope.featureMapTableArray =$scope.getResults($scope.pagination,$scope.originalFeatureMapTableArray);
  };
  $scope.onPerPageChange=function(){
    if($scope.pagination.pageSize == 'All'){
      $scope.pagination.usePageSize=$scope.originalFeatureMapTableArray.length;
    }else{
      $scope.pagination.usePageSize=$scope.pagination.pageSize;
    }
    $scope.featureMapTableArray =$scope.getResults($scope.pagination,$scope.originalFeatureMapTableArray);
  }  

}); //End CreateModelController


