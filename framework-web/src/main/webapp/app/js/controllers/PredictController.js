/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreatePredictController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, 
  $timeout, $filter, PredictService, $http, $location, privilegeSvc, CommonService, CF_ENCODINGTYPE, CF_GRID) {

  $scope.isTargetNameDisabled=false;
  $scope.dataLoading = false;
  $scope.isDestoryState = false;
  if($stateParams.mode =='true'){
    $scope.isEdit=false;
    $scope.isversionEnable=false;
    $scope.isAdd=false;
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
  else if($stateParams.mode =='false'){
    $scope.isEdit=true;
    $scope.isversionEnable=true;
    $scope.isAdd=false;
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
  else{
    $scope.isAdd=true;
    $scope.isDragable="true";
  }
  $scope.mode="false"
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.isSubmitEnable = false;
  $scope.predictData;
  $scope.showFrom = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.predict = {};
  $scope.predict.versions = [];
  $scope.isshowPredict = false;
  $scope.sourceTypes = ["datapod", "dataset","rule"];
  $scope.encodingTypes=CF_ENCODINGTYPE.encodingType;//["ORDINAL", "ONEHOT", "BINARY", "BASEN","HASHING"];
  $scope.selectSourceType=$scope.sourceTypes[0];
  $scope.targetTypes = ["datapod","file"];
  $scope.selectTargetType=$scope.targetTypes[0]; 
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isDependencyShow = false;    
  $scope.allAutoMap=["By Name","By Order"];
  $scope.imputeTypes=["custom","default","function"];

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
  
  $scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
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
		$scope.predictData.displayName=data;
  }
  
  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('predict');
    }
  }
  
  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });  

  $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
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
  $scope.getFunctionByCategory=function(){
    PredictService.getFunctionByCategory("function","aggregate").then(function(response) { onSuccessGetFunctionByCategory(response.data)});
    var onSuccessGetFunctionByCategory = function(response) {
     $scope.allFunction=response
    }
  }
  $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
  }
  $scope.showGraph = function(uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showFrom = false;
    $scope.showGraphDiv = true;
  } //End showFunctionGraph


  $scope.showPage = function() {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showFrom = true;
    $scope.showGraphDiv = false
  }
  $scope.showHome=function(uuid, version,mode){
    if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createpredict', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
  $scope.enableEdit=function (uuid,version) {
    if($scope.isPrivlage || $scope.predictData.locked =="Y"){
      return false;
    }
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showPage()
    $state.go('createpredict', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createpredict', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }     
  }

  $scope.autoReset=function(){
    var featureMapTableArray=[]
    for(var i=0;i < $scope.modelData.features.length;i++){
      var featureMap = {};
      var sourceFeature = {};
      var targetFeature = {};
      var imputeMethod={};
      featureMap.featureMapId = i;
      featureMap.id = i;
      featureMap.index = i;
      sourceFeature.uuid = $scope.modelData.uuid;
      sourceFeature.type = "model";
      sourceFeature.featureId = $scope.modelData.features[i].featureId;
      sourceFeature.featureName = $scope.modelData.features[i].name;
      featureMap.sourceFeature = sourceFeature;
      imputeMethod.type="model";
      imputeMethod.imputeType="default";
      imputeMethod.imputeValue=$scope.modelData.features[i].defaultValue;
      imputeMethod.featureId = $scope.modelData.features[i].featureId;
      imputeMethod.defaultValue = $scope.modelData.features[i].defaultValue;
      imputeMethod.isModelShow=true;
      imputeMethod.isSimpleShow=false;
      imputeMethod.isFunctionShow=false;
      imputeMethod.sourceFeature = sourceFeature;
      featureMap.imputeMethod=imputeMethod;
      $scope.originalFeatureMapTableArray[i] = featureMap;
      $scope.featureMapTableArray[i] = featureMap
    }
  }

  $scope.autoMapFeature=function(type){
    $scope.selectedAutoMode=type
    if($scope.selectedAutoMode =="By Name"){
      var allTargetAttribute={};
      angular.forEach($scope.allTargetAttribute, function (val, key) {
        allTargetAttribute[val.name] = val;
      });

      if($scope.featureMapTableArray && $scope.featureMapTableArray.length >0){
        var featureMapTableArray= $scope.featureMapTableArray;
        for(var i=0;i<$scope.featureMapTableArray.length;i++){
          featureMapTableArray[i].targetFeature=allTargetAttribute[$scope.featureMapTableArray[i].sourceFeature.featureName]//$scope.allTargetAttribute[i];
        }
        $scope.featureMapTableArray=null;
        setTimeout(function(){
          $scope.featureMapTableArray=featureMapTableArray;
        },10);
      }
    }
    if($scope.selectedAutoMode =="By Order"){
      var featureMapTableArray= $scope.featureMapTableArray;
      if(featureMapTableArray && featureMapTableArray.length >0){
        for(var i=0;i<featureMapTableArray.length;i++){
          featureMapTableArray[i].targetFeature=null;
          featureMapTableArray[i].targetFeature=$scope.allTargetAttribute[i];
        }
        $scope.featureMapTableArray=null;
        setTimeout(function(){
          $scope.featureMapTableArray=featureMapTableArray;
        },10);
      }
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
       // $scope.selectSource=response[0];
       // $scope.getAllAttribute();
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
      $scope.allsourceLabel=[];
      $scope.allTargetAttribute = response;
      $scope.allsourceLabel = response
      if (typeof $stateParams.id == "undefined"){
        $scope.selectLabel=response[0];
      }
    }
  }

  $scope.getAllLetestModel();
  $scope.getAllLetestSource();
  $scope.getAllLetestTarget();
  
  $scope.onChangeModel=function(defaultValue,data){
    if(data){
      $scope.selectModel=data;
    }
    if(!$scope.selectModel){
      return false;
    }
    PredictService.getOneByUuidandVersion($scope.selectModel.uuid,$scope.selectModel.version || "" ,"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {

      $scope.modelData=response;
      if (defaultValue) {
        var featureMapTableArray=[];
        for(var i=0;i<response.features.length;i++){
          var featureMap={};
          var sourceFeature={};
          var targetFeature={};
          var imputeMethod={};
          featureMap.featureMapId = i;
          featureMap.id =i;
          featureMap.index =i;
          if(response.features.length > CF_GRID.framework_autopopulate_grid){
            featureMap.isOnDropDown=false;
          }	
          else{
            featureMap.isOnDropDown=true;
          }
          sourceFeature.uuid = response.uuid;
          sourceFeature.type = "model";
          sourceFeature.featureId = response.features[i].featureId;
          sourceFeature.featureName = response.features[i].name;
          featureMap.sourceFeature=sourceFeature;
          featureMapTableArray[i]=featureMap;
          // imputeMethod.type="model";
          // imputeMethod.imputeType="default";
          // imputeMethod.imputeValue=response.features[i].defaultValue;
          // imputeMethod.featureId = response.features[i].featureId;
          // imputeMethod.defaultValue = response.features[i].defaultValue;
          // imputeMethod.isModelShow=true;
          // imputeMethod.isSimpleShow=false;
          // imputeMethod.isFunctionShow=false;
          // imputeMethod.sourceFeature = sourceFeature;
          // featureMap.imputeMethod=imputeMethod;
        }
        $scope.originalFeatureMapTableArray=featureMapTableArray;
        $scope.featureMapTableArray =featureMapTableArray//$scope.getResults($scope.pagination,featureMapTableArray);
        $scope.getTrainByModel(true);
      }
    } 
  }

  $scope.getTrainByModel=function(defaultValue){
    PredictService.getTrainByModel($scope.selectModel.uuid,$scope.selectModel.version,"train").then(function(response) { onSuccessGetTrainByModel(response.data)},function(response){onError(response.data)});
    var onSuccessGetTrainByModel = function(response) {
      $scope.allTrain=response;
      if(response && response.length ==0){
        $scope.selectTrain=null;
      }
      if(defaultValue){
       // $scope.selectTrain=response[0];
      }
    }
    var onError=function(response){
      $scope.selectTrain=null;
    }
  }
  
  $scope.onChangeTrain=function(){
    $scope.getTrainByModel(true);
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

  $scope.onChangeInputeType=function(index,imputeType){
    if(imputeType=="default"){
      $scope.featureMapTableArray[index].imputeMethod.isModelShow=true;
      $scope.featureMapTableArray[index].imputeMethod.isSimpleShow=false;
      $scope.featureMapTableArray[index].imputeMethod.isFunctionShow=false;
    }
    else if(imputeType=="custom"){
      $scope.featureMapTableArray[index].imputeMethod.isModelShow=false;
      $scope.featureMapTableArray[index].imputeMethod.isSimpleShow=true;
      $scope.featureMapTableArray[index].imputeMethod.isFunctionShow=false;
    }
    else if(imputeType=="function"){
      $scope.featureMapTableArray[index].imputeMethod.isModelShow=false;
      $scope.featureMapTableArray[index].imputeMethod.isSimpleShow=false;
      $scope.featureMapTableArray[index].imputeMethod.isFunctionShow=true;
      $scope.getFunctionByCategory();
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
  $scope.loadAttribute = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allsourceLabel, query);
		});
	}
  $scope.onChangeSourceType = function() {
    $scope.rowIdentifierTags=null;
    PredictService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allSource = response
      $scope.selectSource=response[0];
      $scope.onChangeSource();
    }
  }

  $scope.onChangeSource = function(data) {
    if(data){
      $scope.selectSource=data;
    }
    if ($scope.allSource != null && $scope.selectSource != null) {
      $scope.rowIdentifierTags=null;
      $scope.getAllAttribute();
    }
  }

  $scope.onChangeSourceAttribute=function(data,index){
    setTimeout(function(){
			if($scope.featureMapTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.featureMapTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.featureMapTableArray[index].isOnDropDown=true;
			}
		},10);
  }
  $scope.getOneByUuidandVersion=function(uuid,version){
    PredictService.getOneByUuidandVersion(uuid,version,"predict").then(function(response){ onSuccessGetLatestByUuid(response.data)} ,function(response){ onError(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.predictData = response;
      $scope.isEditInprogess=false;
      var selectModel={}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.predict.defaultVersion = defaultversion;
      selectModel.uuid=response.dependsOn.ref.uuid;
      selectModel.name=response.dependsOn.ref.name;
      $scope.selectModel=selectModel;
      $scope.getTrainByModel(false);
      $scope.selectTrain=null;
      var selectTrain=null;
      if(response.trainInfo !=null){
        selectTrain={};
        selectTrain.uuid=response.trainInfo.ref.uuid;
        selectTrain.name=response.trainInfo.ref.name;
      }
      $scope.selectTrain=selectTrain;
      $scope.selectSourceType=response.source.ref.type;
      var selectSource={};
      $scope.selectSource=null;
      selectSource.uuid=response.source.ref.uuid;
      selectSource.name=response.source.ref.name;
      $scope.selectSource=selectSource;

      $scope.getAllLetestSource();
      $scope.getAllAttribute();
      // var selectLabel = {};
      // $scope.selectLabel=null
      // if(response.labelInfo !=null){
      //   selectLabel.uuid = response.labelInfo.ref.uuid;
      //   selectLabel.attributeId = response.labelInfo.attrId;
      //   $scope.selectLabel = selectLabel;
      // }
     // $scope.selectEncodingType=response.encodingType;    
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
      var rowIdentifierTags=[];
      if(response.rowIdentifier !=null){
        for(var i=0;i<response.rowIdentifier.length;i++){
          var attrinfo={};
          attrinfo.uuid=response.rowIdentifier[i].ref.uuid;
          attrinfo.type=response.rowIdentifier[i].ref.type;
          attrinfo.name=response.rowIdentifier[i].attrName;
          attrinfo.dname=response.rowIdentifier[i].ref.name+"."+response.rowIdentifier[i].attrName;
          attrinfo.attributeId=response.rowIdentifier[i].attrId;
          attrinfo.id=response.rowIdentifier[i].ref.uuid+"_"+response.rowIdentifier[i].attrId
          rowIdentifierTags[i]=attrinfo;
        }
      }
      $scope.rowIdentifierTags=rowIdentifierTags;
      if ($scope.selectModel)
        $scope.onChangeModel(false);
     // $scope.getAllAttribute();
      for(var i=0;i<response.featureAttrMap.length;i++){
        var featureMap={};
        var sourceFeature={};
        var targetFeature={};
        var imputeMethod={};
        featureMap.featureMapId=response.featureAttrMap[i].featureMapId;
        featureMap.id = response.featureAttrMap[i].featureMapId;
        if(response.featureAttrMap.length > CF_GRID.framework_autopopulate_grid){
          featureMap.isOnDropDown=false;
        }	
        else{
          featureMap.isOnDropDown=true;
        }
       // featureMap.encodingType= response.featureAttrMap[i].encodingType;
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
        // if(response.featureAttrMap[i].imputeMethod !=null){
        //   if(response.featureAttrMap[i].imputeMethod.ref.type =="model"){
        //     imputeMethod.imputeType="default";
        //     imputeMethod.imputeValue=response.featureAttrMap[i].imputeMethod.featureDefaultValue;
        //     imputeMethod.isModelShow=true;
        //     imputeMethod.isSimpleShow=false;
        //     imputeMethod.isFunctionShow=false;
        //     imputeMethod.featureId = response.featureAttrMap[i].feature.featureId;
        //     imputeMethod.featureName = response.featureAttrMap[i].feature.featureName;
        //     imputeMethod.id = response.featureAttrMap[i].featureMapId;
        //   }
        //   else if(response.featureAttrMap[i].imputeMethod.ref.type =="simple"){
        //     imputeMethod.imputeType="custom";
        //     imputeMethod.imputeValue=response.featureAttrMap[i].imputeMethod.value;
        //     imputeMethod.isModelShow=false;
        //     imputeMethod.isSimpleShow=true;
        //     imputeMethod.isFunctionShow=false;
        //   }
        //   else if(response.featureAttrMap[i].imputeMethod.ref.type =="function"){
        //     imputeMethod.imputeType="function";
        //     imputeMethod.isModelShow=false;
        //     imputeMethod.isSimpleShow=false;
        //     imputeMethod.isFunctionShow=true;
        //     $scope.getFunctionByCategory();
        //     var selectedFunction={};
        //     selectedFunction.uuid = response.featureAttrMap[i].imputeMethod.ref.uuid;
        //     selectedFunction.type = response.featureAttrMap[i].imputeMethod.ref.type;
        //     imputeMethod.selectedFunction=selectedFunction;
        //   }
          
        //   featureMap.imputeMethod=imputeMethod;
        //   imputeMethod.uuid = response.featureAttrMap[i].imputeMethod.ref.uuid;
        //   imputeMethod.type = response.featureAttrMap[i].imputeMethod.ref.type;
        // }
        featureMapTableArray[i]=featureMap;
      }
      $scope.originalFeatureMapTableArray=[];
      $scope.featureMapTableArray=[];
      $scope.originalFeatureMapTableArray=featureMapTableArray;
      $scope.featureMapTableArray =featureMapTableArray//$scope.getResults($scope.pagination,featureMapTableArray);
      
    }
    var onError=function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    }
  }
  if(typeof $stateParams.id != "undefined") {
    $scope.showactive="true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.getAllVersion($stateParams.id)
    $scope.getOneByUuidandVersion($stateParams.id,$stateParams.version);
  } else{
    $scope.predictData={};
    $scope.predictData.locked="N"
  }
 
  $scope.selectVersion=function(uuid,version){
    $scope.allSource=[];
    $scope.allTarget=[];
    $scope.allModel =[];
    $scope.allsourceLabel=null;
    $scope.selectLabel=null;
   // $scope.selectEncodingType=null;
    setTimeout(function () {
      $scope.getAllLetestModel();
      $scope.getAllLetestSource();
      $scope.getAllLetestTarget();
      $scope.getOneByUuidandVersion(uuid,version);
    },100)
  }

  $scope.ondrop = function(e) {
		console.log(e);
		$scope.myform3.$dirty=true;
	}
  $scope.onAttrRowDown=function(index){
		var rowTempIndex=$scope.featureMapTableArray[index];
    var rowTempIndexPlus=$scope.featureMapTableArray[index+1];
		$scope.featureMapTableArray[index]=rowTempIndexPlus;
		$scope.featureMapTableArray[index+1]=rowTempIndex;
	}
	$scope.onAttrRowUp=function(index){
		var rowTempIndex=$scope.featureMapTableArray[index];
    var rowTempIndexMines=$scope.featureMapTableArray[index-1];
		$scope.featureMapTableArray[index]=rowTempIndexMines;
		$scope.featureMapTableArray[index-1]=rowTempIndex;
  }
  $scope.submitModel = function() {
    var upd_tag="N"
    $scope.isshowPredict = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var predictJson = {}
    predictJson.uuid = $scope.predictData.uuid;
    predictJson.name = $scope.predictData.name;
    predictJson.displayName = $scope.predictData.displayName;
    predictJson.desc = $scope.predictData.desc;
    predictJson.active = $scope.predictData.active;
    predictJson.locked = $scope.predictData.locked;
    predictJson.published=$scope.predictData.published;
    predictJson.publicFlag=$scope.predictData.publicFlag;  
    predictJson.includeFeatures=$scope.predictData.includeFeatures;
  //  predictJson.encodingType =$scope.selectEncodingType;
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
    predictJson.tags = tagArray;
    var dependsOn={};
    var ref={};
    ref.type="model";
    ref.uuid=$scope.selectModel.uuid;
    dependsOn.ref=ref;
    predictJson.dependsOn=dependsOn;
    if($scope.selectTrain){
      var trainInfo={};
      var ref={};
      ref.type="train";
      ref.uuid=$scope.selectTrain.uuid;
      trainInfo.ref=ref;
      predictJson.trainInfo=trainInfo;
    }else{
      predictJson.trainInfo=trainInfo;
    }

    var source={};
    var sourceref={};
    sourceref.type=$scope.selectSourceType;
    sourceref.uuid=$scope.selectSource.uuid;
    source.ref=sourceref;
    predictJson.source=source;

    // var labelInfo = {};
    // var ref = {};
    // ref.type = $scope.selectSourceType
    // ref.uuid = $scope.selectLabel.uuid
    // labelInfo.ref = ref;
    // labelInfo.attrId = $scope.selectLabel.attributeId
    // predictJson.labelInfo = labelInfo;

    var target={};
    var targetref={};
    targetref.type=$scope.selectTargetType;
    if($scope.selectTargetType =="datapod")
    targetref.uuid=$scope.selectTarget.uuid;
    target.ref=targetref;
    predictJson.target=target;

    var rowIdentifierTags= [];
		if($scope.rowIdentifierTags != null) {
			for(var i = 0; i < $scope.rowIdentifierTags.length; i++) {
				var rowIdentifierInfo = {}
				var ref = {};
				ref.type = $scope.rowIdentifierTags[i].type;
				ref.uuid = $scope.rowIdentifierTags[i].uuid;
				rowIdentifierInfo.ref = ref;
				rowIdentifierInfo.attrId = $scope.rowIdentifierTags[i].attributeId
				rowIdentifierTags[i] = rowIdentifierInfo;
			}
    }

    predictJson.rowIdentifier=rowIdentifierTags;

    var featureMap=[];
    if($scope.featureMapTableArray.length >0){
      for(var i=0;i<$scope.featureMapTableArray.length;i++){
        var featureMapObj={};
        featureMapObj.featureMapId =$scope.featureMapTableArray[i].id;
        featureMapObj.featureDisplaySeq =i;
       // featureMapObj.encodingType= $scope.featureMapTableArray[i].encodingType;
        var sourceFeature={};
        var sourceFeatureRef={};
        var targetFeature={};
        var targetFeatureRef={};
        var imputeMethod={};
        var imputeMethodRef={};
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
        // if($scope.featureMapTableArray[i].imputeMethod.imputeType =="default"){
        //   imputeMethodRef.type="model";
        //   imputeMethodRef.uuid = $scope.featureMapTableArray[i].sourceFeature.uuid;
        //   imputeMethod.ref = imputeMethodRef;
        //   imputeMethod.featureId = $scope.featureMapTableArray[i].imputeMethod.featureId;

        // }else if($scope.featureMapTableArray[i].imputeMethod.imputeType =="function"){
        //   imputeMethodRef.type="function";
        //   imputeMethodRef.uuid = $scope.featureMapTableArray[i].imputeMethod.selectedFunction.uuid;
        //   imputeMethod.ref = imputeMethodRef;
        // }
        // else{
        //   imputeMethodRef.type="simple";
        //   imputeMethod.ref = imputeMethodRef;
        //   imputeMethod.value = $scope.featureMapTableArray[i].imputeMethod.imputeValue;
        // }
        // featureMapObj.imputeMethod = imputeMethod;
        featureMap[i]=featureMapObj;
      }
    }
    predictJson.featureAttrMap=featureMap;
    //console.log(JSON.stringify(predictJson))
    PredictService.submit(predictJson, 'predict',upd_tag).then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if($scope.checkboxPredictexecution == "YES") {
          $scope.dataLoading = true;

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
    var hidemode = "yes";
    
    if (hidemode == 'yes' && $scope.isDestoryState==false) { 
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
    	  $scope.dataLoading = false;
        notify.type='success',
        notify.title= 'Success',
        notify.content='Configuration Submited and Saved Successfully'
        $scope.$emit('notify', notify);
        $scope.okmodelsave();
      }
    }
  }
  
  $scope.attrTableSelectedItem=[];
	$scope.onChangeAttrRow=function(index,status){
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
	$scope.autoMove=function(index){
		var tempAtrr=$scope.featureMapTableArray[$scope.attrTableSelectedItem[0]];
		$scope.featureMapTableArray.splice($scope.attrTableSelectedItem[0],1);
		$scope.featureMapTableArray.splice(index,0,tempAtrr);
		$scope.attrTableSelectedItem=[];
		$scope.featureMapTableArray[index].selected=false;
	
	}

	$scope.autoMoveTo=function(index){
		if(index <= $scope.featureMapTableArray.length){
			$scope.autoMove(index-1,'mapAttr');
			$scope.moveTo=null;
			$(".btn-group ").removeClass("open");
		}
	}
  // $scope.getResults = function(pagination,params) {
  //   pagination.totalItems=params.length;
  //   if(pagination.totalItems >0){
  //     pagination.to = (((pagination.currentPage - 1) * (pagination.usePageSize))+1);
  //   }
  //   else{
  //     pagination.to=0;
  //   }
  //   if(pagination.totalItems < (pagination.usePageSize*pagination.currentPage)) {
  //       pagination.from = pagination.totalItems;
  //   } else {
  //     pagination.from = ((pagination.currentPage) * pagination.usePageSize);
  //   }
  //   var limit = (pagination.usePageSize* pagination.currentPage);
  //   var offset = ((pagination.currentPage - 1) * pagination.usePageSize)
  //   return params.slice(offset,limit);
  // }

  // $scope.onPageChanged = function(){
  //   $scope.featureMapTableArray =$scope.getResults($scope.pagination,$scope.originalFeatureMapTableArray);
  // };
  // $scope.onPerPageChange=function(){
  //   if($scope.pagination.pageSize == 'All'){
  //     $scope.pagination.usePageSize=$scope.originalFeatureMapTableArray.length;
  //   }else{
  //     $scope.pagination.usePageSize=$scope.pagination.pageSize;
  //   }
  //   $scope.featureMapTableArray =$scope.getResults($scope.pagination,$scope.originalFeatureMapTableArray);
  // }  

}); //End CreateModelController


