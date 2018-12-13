/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateTrainController', function ($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, TrainService, $http, $location, CommonService, privilegeSvc) {

  $scope.isTargetNameDisabled = false;
  $scope.dataLoading = false;
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
    $scope.isDragable = "false";
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
    $scope.isDragable = "true";
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
    $scope.isDragable = "true";
  }
  $scope.userDetail = {}
  $scope.userDetail.uuid = $rootScope.setUseruuid;
  $scope.userDetail.name = $rootScope.setUserName;
  $scope.mode = "false"
  $scope.isSubmitEnable = false;
  $scope.trainData;
  $scope.trainData = {};
  $scope.trainData.trainPercent = 70;
  $scope.trainData.valPercent = 30;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.Train = {};
  $scope.Train.versions = [];
  $scope.isshowTrain = false;
  $scope.sourceTypes = ["datapod", "dataset", "rule"];
  $scope.selectSourceType = $scope.sourceTypes[0];
  $scope.isDestoryState = false;
  //$scope.targetTypes = ["datapod", "file"];
  //$scope.selectTargetType = $scope.targetTypes[0];
  $scope.paramTypes = ["paramlist", "paramset"];
  $scope.imputeTypes=["custom","default","function"];
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isDependencyShow = false;
  $scope.allAutoMap = ["By Name", "By Order"];
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 30000 //time in ms
  };
  $scope.pagination = {
    currentPage: 1,
    pageSize: 10,
    usePageSize: 10,
    paginationPageSizes: ["All", 5, 10, 25, 50],
    maxSize: 5,
  }
  $scope.getLovByType = function () {
    CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
    var onSuccessGetLovByType = function (response) {
      $scope.lobTag = response[0].value
    }
  }

  $scope.loadTag = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.lobTag, query);
    });
  };

  $scope.getLovByType();

  $scope.close = function () {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('train');
    }
  }

  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });

  $scope.autoMapFeature = function (type) {
    $scope.selectedAutoMode = type
    if ($scope.selectedAutoMode == "By Name") {
      var allTargetAttribute = {};
      angular.forEach($scope.allTargetAttribute, function (val, key) {
        allTargetAttribute[val.name] = val;
      });

      if ($scope.featureMapTableArray && $scope.featureMapTableArray.length > 0) {
        for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
          $scope.featureMapTableArray[i].targetFeature = allTargetAttribute[$scope.featureMapTableArray[i].sourceFeature.featureName]//$scope.allTargetAttribute[i];
        }
      }
    }
    if ($scope.selectedAutoMode == "By Order") {
      if ($scope.featureMapTableArray && $scope.featureMapTableArray.length > 0) {
        for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
          $scope.featureMapTableArray[i].targetFeature = $scope.allTargetAttribute[i];
        }
      }

    }

  }

  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
    //console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });

  $scope.countContinue = function () {
    if ($scope.trainData.name != null || $scope.selectSourceType != null) {
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


  $scope.showGraph = function (uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  } //End showFunctionGraph


  $scope.showPage = function () {
    $scope.showForm = true;
    $scope.showGraphDiv = false
  }

  $scope.showHome = function (uuid, version, mode) {
    $scope.showPage()
    $state.go('createtrain', {
      id: uuid,
      version: version,
      mode: mode
    });
  }

  $scope.enableEdit = function (uuid, version) {
    if ($scope.isPrivlage || $scope.trainData.locked == "Y") {
      return false;
    }
    $scope.showPage()
    $state.go('createtrain', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }

  $scope.showview = function (uuid, version) {
    if (!$scope.isEdit) {
      $scope.showPage()
      $state.go('createtrain', {
        id: uuid,
        version: version,
        mode: 'true'
      });
    }
  }
  
  $scope.getFunctionByCategory=function(){
    TrainService.getFunctionByCategory("function","aggregate").then(function(response) { onSuccessGetFunctionByCategory(response.data)});
    var onSuccessGetFunctionByCategory = function(response) {
     $scope.allFunction=response
    }
  }

  $scope.getAllLetestModel = function (defaultValue) {
    //TrainService.getAllModelByType('N', "model").then(function (response) { onGetAllLatest(response.data) });
    TrainService.getAllLatest("model").then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allModel = response;
      if (defaultValue == true) {
        $scope.selectModel = response[0];
      }

    }
  }
  $scope.getAllLetestSource = function () {
    TrainService.getAllLatest($scope.selectSourceType).then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allSource = response;
      if (typeof $stateParams.id == "undefined") {
        $scope.selectSource = response[0];
        $scope.getAllAttribute();
      }
    }
  }

  // $scope.getAllAttributeBySource=function(){
  //   TrainService.getAllAttributeBySource($scope.selectSource.uuid,$scope.selectSourceType).then(function(response) {
  //     onSuccessGetAllAttributeBySource(response.data)
  //   });
  //   var onSuccessGetAllAttributeBySource = function(response) {
  //     $scope.allsourceLabel = response
  //   }

  // }

  // $scope.getAllLetestTarget=function(defaultValue){
  //   TrainService.getAllLatest($scope.selectTargetType).then(function(response) { onGetAllLatest(response.data)});
  //   var onGetAllLatest = function(response) {
  //     $scope.allTarget = response;
  //     if(typeof $stateParams.id == "undefined" || defaultValue ==true) {
  //       $scope.selectTarget=response[0];
  //     }
  //   }
  // }
  $scope.loadAttribute = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.allsourceLabel, query);
    });
  }
  $scope.getAllAttribute = function () {
    TrainService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function (response) { onGetAllAttributeBySource(response.data) });
    var onGetAllAttributeBySource = function (response) {
      $scope.allsourceLabel = [];
      $scope.allTargetAttribute = response;
      $scope.allsourceLabel = response
      if (typeof $stateParams.id == "undefined") {
        $scope.selectLabel = response[0];
      }
    }
  }

  $scope.getAllLetestModel(false);
  $scope.getAllLetestSource();
  // $scope.getAllLetestTarget();

  $scope.onChangeModel = function (defaultValue, data) {

    if (data) {
      $scope.selectModel = data;
    }
    if (!$scope.selectModel) {
      return false;
    }
    $scope.selectedRunImmediately = 'No';
    $scope.allparamset = null;
    $scope.allParamList = null;
    $scope.isParamLsitTable = false;
    $scope.selectParamList = null;
    $scope.selectParamType = null;
    TrainService.getOneByUuidandVersion($scope.selectModel.uuid, $scope.selectModel.version, "model").then(function (response) { onSuccessGetLatestByUuid(response.data) });
    var onSuccessGetLatestByUuid = function (response) {
      $scope.modelData = response;
      if (defaultValue) {
        $scope.selectedRunImmediately = "NO";
        $scope.isShowExecutionparam = false;
        var featureMapTableArray = [];
        for (var i = 0; i < response.features.length; i++) {
          var featureMap = {};
          var sourceFeature = {};
          var targetFeature = {};
          var imputeMethod={};
          featureMap.featureMapId = i;
          featureMap.id = i;
          featureMap.index = i;
          sourceFeature.uuid = response.uuid;
          sourceFeature.type = "model";
          sourceFeature.featureId = response.features[i].featureId;
          sourceFeature.featureName = response.features[i].name;
          featureMap.sourceFeature = sourceFeature;
          featureMapTableArray[i] = featureMap;
          imputeMethod.type="model";
          imputeMethod.imputeType="default";
          imputeMethod.imputeValue=response.features[i].defaultValue;
          imputeMethod.featureId = response.features[i].featureId;
          imputeMethod.defaultValue = response.features[i].defaultValue;
          imputeMethod.sourceFeature = sourceFeature;
          featureMap.imputeMethod=imputeMethod;
          $scope.originalFeatureMapTableArray = featureMapTableArray;
          $scope.featureMapTableArray = featureMapTableArray//$scope.getResults($scope.pagination,featureMapTableArray);//featureMapTableArray;
        }
      }
    }
  }
  $scope.ondrop = function (e) {
    $scope.myform3.$dirty = true;
  }

  $scope.onAttrRowDown = function (index) {
    var rowTempIndex = $scope.featureMapTableArray[index];
    var rowTempIndexPlus = $scope.featureMapTableArray[index + 1];
    $scope.featureMapTableArray[index] = rowTempIndexPlus;
    $scope.featureMapTableArray[index + 1] = rowTempIndex;
  }

  $scope.onAttrRowUp = function (index) {
    var rowTempIndex = $scope.featureMapTableArray[index];
    var rowTempIndexMines = $scope.featureMapTableArray[index - 1];
    $scope.featureMapTableArray[index] = rowTempIndexMines;
    $scope.featureMapTableArray[index - 1] = rowTempIndex;
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

  $scope.getAllVersion = function (uuid) {
    TrainService.getAllVersionByUuid(uuid, "train").then(function (response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var Trainversion = {};
        Trainversion.version = response[i].version;
        $scope.Train.versions[i] = Trainversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion

  $scope.onChangeSourceType = function () {
    TrainService.getAllLatest($scope.selectSourceType).then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allSource = response;
      $scope.selectSource = response[0];
      $scope.onChangeSource();
    }
  }

  $scope.onChangeSource = function (data) {
    if (data) {
      $scope.selectSource = data;
    }

    if ($scope.allSource != null && $scope.selectSource != null) {
      $scope.rowIdentifierTags = null;
      $scope.getAllAttribute();
      $scope.clearFeatureTable();
      //$scope.getAllAttributeBySource();
    }
  }

  $scope.clearFeatureTable = function () {
    for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
      $scope.featureMapTableArray[i].targetFeature = null;
    }
  }

  $scope.getOneByUuidandVersion = function (uuid, version) {

    TrainService.getOneByUuidandVersion(uuid, version, "train").then(function (response) { onSuccessGetLatestByUuid(response.data) }, function (response) { onError(response.data) });
    var onSuccessGetLatestByUuid = function (response) {
      $scope.trainData = response;
      $scope.isEditInprogess = false;
      var selectModel = {}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.Train.defaultVersion = defaultversion;
      selectModel.uuid = response.dependsOn.ref.uuid;
      selectModel.name = response.dependsOn.ref.name;
      selectModel.version = " ";
      $scope.selectModel = selectModel;
      $scope.selectSourceType = response.source.ref.type;
      var selectSource = {};
      $scope.selectSource = null;
      selectSource.uuid = response.source.ref.uuid;
      selectSource.name = response.source.ref.name;
      $scope.selectSource = selectSource;
      //  $scope.onChangeSourceType();
      $scope.getAllLetestSource();
      $scope.getAllAttribute();
      var selectLabel = {};
      $scope.selectLabel = null
      selectLabel.uuid = response.labelInfo.ref.uuid;
      selectLabel.attributeId = response.labelInfo.attrId;
      $scope.selectLabel = selectLabel;
      var rowIdentifierTags = [];
      if (response.rowIdentifier != null) {
        for (var i = 0; i < response.rowIdentifier.length; i++) {
          var attrinfo = {};
          attrinfo.uuid = response.rowIdentifier[i].ref.uuid;
          attrinfo.type = response.rowIdentifier[i].ref.type;
          attrinfo.dname = response.rowIdentifier[i].ref.name + "." + response.rowIdentifier[i].attrName;
          attrinfo.attributeId = response.rowIdentifier[i].attrId;
          attrinfo.id = response.rowIdentifier[i].ref.uuid + "_" + response.rowIdentifier[i].attrId
          rowIdentifierTags[i] = attrinfo;
        }
      }
      $scope.rowIdentifierTags = rowIdentifierTags;
  
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

      for (var i = 0; i < response.featureAttrMap.length; i++) {
        var featureAttrMap = {};
        var sourceFeature = {};
        var targetFeature = {};
        var imputeMethod={};
        featureAttrMap.featureAttrMapId = response.featureAttrMap[i].featureMapId;
        featureAttrMap.id = response.featureAttrMap[i].featureMapId;
        sourceFeature.uuid = response.featureAttrMap[i].feature.ref.uuid;
        sourceFeature.type = response.featureAttrMap[i].feature.ref.type;
        sourceFeature.featureId = response.featureAttrMap[i].feature.featureId;
        sourceFeature.featureName = response.featureAttrMap[i].feature.featureName;
        featureAttrMap.sourceFeature = sourceFeature;
        targetFeature.uuid = response.featureAttrMap[i].attribute.ref.uuid;
        targetFeature.type = response.featureAttrMap[i].attribute.ref.type;
        targetFeature.datapodname = response.featureAttrMap[i].attribute.ref.name;
        targetFeature.name = response.featureAttrMap[i].attribute.attrName;
        targetFeature.attributeId = response.featureAttrMap[i].attribute.attrId;
        targetFeature.id = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
        targetFeature.dname = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
        featureAttrMap.targetFeature = targetFeature;
       // console.log(response.featureAttrMap[i].imputeMethod.ref.type)
       if(response.featureAttrMap[i].imputeMethod !=null){
          if(response.featureAttrMap[i].imputeMethod.ref.type =="model"){
            imputeMethod.imputeType="default";
            imputeMethod.imputeValue=response.featureAttrMap[i].imputeMethod.featureDefaultValue;
            imputeMethod.isModelShow=true;
            imputeMethod.isSimpleShow=false;
            imputeMethod.isFunctionShow=false;
            imputeMethod.featureId = response.featureAttrMap[i].feature.featureId;
            imputeMethod.featureName = response.featureAttrMap[i].feature.featureName;
            imputeMethod.id = response.featureAttrMap[i].featureMapId;
          }
          else if(response.featureAttrMap[i].imputeMethod.ref.type =="simple"){
            imputeMethod.imputeType="custom";
            imputeMethod.imputeValue=response.featureAttrMap[i].imputeMethod.value;
            imputeMethod.isModelShow=false;
            imputeMethod.isSimpleShow=true;
            imputeMethod.isFunctionShow=false;
          }
          else if(response.featureAttrMap[i].imputeMethod.ref.type =="function"){
            imputeMethod.imputeType="function";
            imputeMethod.isModelShow=false;
            imputeMethod.isSimpleShow=false;
            imputeMethod.isFunctionShow=true;
            $scope.getFunctionByCategory();
            var selectedFunction={};
            selectedFunction.uuid = response.featureAttrMap[i].imputeMethod.ref.uuid;
            selectedFunction.type = response.featureAttrMap[i].imputeMethod.ref.type;
            imputeMethod.selectedFunction=selectedFunction;
          }
          
          featureAttrMap.imputeMethod=imputeMethod;
          imputeMethod.uuid = response.featureAttrMap[i].imputeMethod.ref.uuid;
          imputeMethod.type = response.featureAttrMap[i].imputeMethod.ref.type;
        }
        featureAttrMap.featureAttrMap=featureAttrMap;
        featureMapTableArray[i] = featureAttrMap;
      }
      $scope.originalFeatureMapTableArray = featureMapTableArray;
      $scope.featureMapTableArray = featureMapTableArray//$scope.getResults($scope.pagination,featureMapTableArray);
      if ($scope.selectModel)
        $scope.onChangeModel(false);
    }
    var onError = function () {
      $scope.isEditInprogess = false;
      $scope.isEditVeiwError = true;
    }
  }
  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.isEditVeiwError = false;
    $scope.isEditInprogess = true;
    $scope.getAllVersion($stateParams.id)
    $scope.getOneByUuidandVersion($stateParams.id, $stateParams.version);
  }
  else {
    $scope.trainData = {};
    $scope.trainData.locked = "N";
    $scope.trainData.trainPercent = 80;
    $scope.trainData.valPercent = 20;
  }
  $scope.selectVersion = function (uuid, version) {

    $scope.allSource = [];
    $scope.allModel = [];
    $scope.allsourceLabel = null;
    $scope.selectLabel = null;
    setTimeout(function () {
      $scope.getAllLetestModel();
      $scope.getAllLetestSource();
      $scope.getOneByUuidandVersion(uuid, version);
    }, 100)
  }

  $scope.submitModel = function () {
    var upd_tag = "N"
    $scope.isshowTrain = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var TrainJson = {}
    TrainJson.uuid = $scope.trainData.uuid
    TrainJson.name = $scope.trainData.name
    TrainJson.desc = $scope.trainData.desc
    TrainJson.active = $scope.trainData.active;
    TrainJson.locked = $scope.trainData.locked;
    TrainJson.published = $scope.trainData.published;
    TrainJson.published = $scope.trainData.published;
    TrainJson.valPercent = $scope.trainData.valPercent;
    TrainJson.trainPercent = $scope.trainData.trainPercent;
    TrainJson.useHyperParams = $scope.trainData.useHyperParams;
    //TrainJson.featureImportance=$scope.trainData.featureImportance;
    TrainJson.includeFeatures = $scope.trainData.includeFeatures;
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
    TrainJson.tags = tagArray;
    var dependsOn = {};
    var ref = {};
    ref.type = "model";
    ref.uuid = $scope.selectModel.uuid;
    dependsOn.ref = ref;
    TrainJson.dependsOn = dependsOn;
    var source = {};
    var sourceref = {};
    sourceref.type = $scope.selectSourceType;
    sourceref.uuid = $scope.selectSource.uuid;
    source.ref = sourceref;
    TrainJson.source = source;
    var labelInfo = {};
    var ref = {};
    ref.type = $scope.selectSourceType
    ref.uuid = $scope.selectLabel.uuid
    labelInfo.ref = ref;
    labelInfo.attrId = $scope.selectLabel.attributeId
    TrainJson.labelInfo = labelInfo;
   
    var rowIdentifierTags = [];
    if ($scope.rowIdentifierTags != null) {
      for (var i = 0; i < $scope.rowIdentifierTags.length; i++) {
        var rowIdentifierInfo = {}
        var ref = {};
        ref.type = $scope.rowIdentifierTags[i].type;
        ref.uuid = $scope.rowIdentifierTags[i].uuid;
        rowIdentifierInfo.ref = ref;
        rowIdentifierInfo.attrId = $scope.rowIdentifierTags[i].attributeId
        rowIdentifierTags[i] = rowIdentifierInfo;
      }
    }

    TrainJson.rowIdentifier = rowIdentifierTags;

    var featureMap = [];
    if ($scope.featureMapTableArray && $scope.featureMapTableArray.length > 0) {
      for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
        var featureMapObj = {};
        featureMapObj.featureMapId = $scope.featureMapTableArray[i].id;
        featureMapObj.featureDisplaySeq = i;
        var sourceFeature = {};
        var sourceFeatureRef = {};
        var targetFeature = {};
        var targetFeatureRef = {};
        var imputeMethod={};
        var imputeMethodRef={};
        sourceFeatureRef.uuid = $scope.featureMapTableArray[i].sourceFeature.uuid;
        sourceFeatureRef.type = $scope.featureMapTableArray[i].sourceFeature.type;
        sourceFeature.ref = sourceFeatureRef;
        sourceFeature.featureId = $scope.featureMapTableArray[i].sourceFeature.featureId;
        //sourceFeature.featureName = $scope.featureMapTableArray[i].sourceFeature.featureName;
        featureMapObj.feature = sourceFeature;
        targetFeatureRef.uuid = $scope.featureMapTableArray[i].targetFeature.uuid;
        targetFeatureRef.type = $scope.selectSourceType;
        targetFeature.ref = targetFeatureRef
        targetFeature.attrId = $scope.featureMapTableArray[i].targetFeature.attributeId;
        featureMapObj.attribute = targetFeature;
        
        if($scope.featureMapTableArray[i].imputeMethod.imputeType =="default"){
          imputeMethodRef.type="model";
          imputeMethodRef.uuid = $scope.featureMapTableArray[i].imputeMethod.uuid;
          imputeMethod.ref = imputeMethodRef;
          imputeMethod.featureId = $scope.featureMapTableArray[i].imputeMethod.featureId;

        }else if($scope.featureMapTableArray[i].imputeMethod.imputeType =="function"){
          imputeMethodRef.type="function";
          imputeMethodRef.uuid = $scope.featureMapTableArray[i].imputeMethod.selectedFunction.uuid;
          imputeMethod.ref = imputeMethodRef;
        }
        else{
          imputeMethodRef.type="simple";
          imputeMethod.ref = imputeMethodRef;
          imputeMethod.value = $scope.featureMapTableArray[i].imputeMethod.imputeValue;
        }
        featureMapObj.imputeMethod = imputeMethod;
        featureMap[i] = featureMapObj;
      }
    }
    TrainJson.featureAttrMap = featureMap;
    console.log(JSON.stringify(TrainJson))
    TrainService.submit(TrainJson, 'train', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      $scope.iSSubmitEnable = true
      $scope.changemodelvalue();
      if ($scope.selectedRunImmediately == "YES") {
        TrainService.getOneById(response, "train").then(function (response) { onSuccessGetOneById(response.data) });
        var onSuccessGetOneById = function (result) {
          $scope.trainExecute(result);
        }
      } //End if
      else {
        $scope.dataLoading = false;
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
    }
  }

  $scope.okmodelsave = function () {
    $('#modelsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes' && $scope.isDestoryState==false) {
      setTimeout(function () {
        $state.go("train");
      }, 2000);
    }
  }

  $scope.changemodelvalue = function () {
    $scope.isshowTrain = sessionStorage.isshowmodel
  };


  $scope.onChageTrainPercent = function () {
    $scope.trainData.valPercent = 100 - $scope.trainData.trainPercent;
  }

  $scope.onChageValPercent = function () {
    $scope.trainData.trainPercent = 100 - $scope.trainData.valPercent;
  }

  $scope.onChangeParamList = function () {
    $scope.isParamLsitTable = false;
    CommonService.getParamByParamList($scope.paramlistdata.uuid, "paramlist").then(function (response) { onSuccesGetParamListByTrain(response.data) });
    var onSuccesGetParamListByTrain = function (response) {
      $scope.isParamLsitTable = true;
      $scope.selectParamList = response;
      var paramArray = [];
      for (var i = 0; i < response.length; i++) {
        var paramInfo = {}
        paramInfo.paramId = response[i].paramId;
        paramInfo.paramName = response[i].paramName;
        paramInfo.paramType = response[i].paramType.toLowerCase();
        if (response[i].paramValue != null && response[i].paramValue.ref.type == "simple") {
          paramInfo.paramValue = response[i].paramValue.value;
          paramInfo.paramValueType = "simple"
        } else if (response[i].paramValue != null) {
          var paramValue = {};
          paramValue.uuid = response[i].paramValue.ref.uuid;
          paramValue.type = response[i].paramValue.ref.type;
          paramInfo.paramValue = paramValue;
          paramInfo.paramValueType = response[i].paramValue.ref.type;
        } else {

        }
        paramArray[i] = paramInfo;
      }
      $scope.selectParamList.paramInfo = paramArray;
    }
  }

  $scope.getParamSetByAlgorithm = function () {
    TrainService.getParamSetByAlgorithm($scope.modelData.dependsOn.ref.uuid, $scope.modelData.dependsOn.ref.version, $scope.trainData.useHyperParams).then(function (response) { onSuccessGetParamSetByAlgorithm(response.data) });
    var onSuccessGetParamSetByAlgorithm = function (response) {
      $scope.allparamset = response
      $scope.isShowExecutionparam = true;
    }
  }

  $scope.getParamListByAlgorithm = function () {
    TrainService.getParamListByAlgorithm($scope.modelData.dependsOn.ref.uuid, $scope.modelData.dependsOn.ref.version || "", "paramlist", $scope.trainData.useHyperParams).then(function (response) { onSuccessGetParamListByAlgorithm(response.data) });
    var onSuccessGetParamListByAlgorithm = function (response) {
      $scope.allParamList = response;
    }
  }

  $scope.onChangeParamType = function () {
    $scope.allparamset = null;
    $scope.allParamList = null;
    $scope.isParamLsitTable = false;
    $scope.selectParamList = null;
    if ($scope.selectParamType == "paramlist") {
      $scope.paramlistdata = null;
      $scope.getParamListByAlgorithm();
    }
    else if ($scope.selectParamType == "paramset") {
      $scope.getParamSetByAlgorithm();
    }
  }

  $scope.onChangeRunImmediately = function () {
    $scope.allparamset = null;
    $scope.allParamList = null;
    $scope.isParamLsitTable = false;
    $scope.selectParamList = null;
    $scope.selectParamType = null;

    if ($scope.selectedRunImmediately == "YES" && $scope.modelData.dependsOn.ref.type == "algorithm") {
      $('#responsive').modal({
        backdrop: 'static',
        keyboard: false
      });
    } else {
      $scope.isShowExecutionparam = false;
      $scope.allparamset = null;
    }
  }

  $scope.executeWithExecParams = function () {
    $('#responsive').modal('hide');
  }

  $scope.trainExecute = function (data) {
    if ($scope.selectParamType == "paramlist") {
      if ($scope.paramlistdata) {
        var execParams = {};
        var paramListInfo = [];
        var paramInfo = {};
        var paramInfoRef = {};
        paramInfoRef.uuid = $scope.paramlistdata.uuid;
        paramInfoRef.type = "paramlist";
        paramInfo.ref = paramInfoRef;
        paramListInfo[0] = paramInfo;
        execParams.paramListInfo = paramListInfo;
      } else {
        execParams = null;
      }
      $scope.paramlistdata = null;
      $scope.selectParamType = null;
    }
    else {
      $scope.newDataList = [];
      angular.forEach($scope.paramtable, function (selected) {
        if (selected.selected) {
          $scope.newDataList.push(selected);
        }
      });
      var paramInfoArray = [];
      if ($scope.newDataList.length > 0) {
        var execParams = {}
        var ref = {}
        ref.uuid = $scope.paramsetdata.uuid;
        ref.version = $scope.paramsetdata.version;
        for (var i = 0; i < $scope.newDataList.length; i++) {
          var paraminfo = {};
          paraminfo.paramSetId = $scope.newDataList[i].paramSetId;
          paraminfo.ref = ref;
          paramInfoArray[i] = paraminfo;
        }
      }
      if (paramInfoArray.length > 0) {
        execParams.paramInfo = paramInfoArray;
      } else {
        execParams = null
      }
    }

    // console.log(JSON.stringify(execParams));
    CommonService.executeWithParams("train", data.uuid, data.version, execParams).then(function (response) { onSuccessExectute() });
    var onSuccessExectute = function () {
      $scope.dataLoading = false;
      $scope.selectAllParam = false;
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = 'Configuration Submited and Saved Successfully'
      $scope.$emit('notify', notify);
      $scope.okmodelsave();
    }
  }

  $scope.onSelectparamSet = function () {
    var paramSetjson = {};
    var paramInfoArray = [];
    $scope.paramtable = [];
    if ($scope.paramsetdata != null) {
      for (var i = 0; i < $scope.paramsetdata.paramInfo.length; i++) {
        var paramInfo = {};
        paramInfo.paramSetId = $scope.paramsetdata.paramInfo[i].paramSetId
        var paramSetValarray = [];
        for (var j = 0; j < $scope.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
          var paramSetValjson = {};
          paramSetValjson.paramId = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
          paramSetValjson.paramName = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
          paramSetValjson.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
          paramSetValjson.ref = $scope.paramsetdata.paramInfo[i].paramSetVal[j].ref;
          paramSetValarray[j] = paramSetValjson;
          paramInfo.paramSetVal = paramSetValarray;
          paramInfo.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
        }
        paramInfoArray[i] = paramInfo;
      }
      $scope.paramtablecol = paramInfoArray[0].paramSetVal;
      $scope.paramtable = paramInfoArray;
      paramSetjson.paramInfoArray = paramInfoArray;
      $scope.isTabelShow = true;
    } else {
      $scope.isTabelShow = false;
    }
  }


  $scope.selectAllRow = function () {
    angular.forEach($scope.paramtable, function (stage) {
      stage.selected = $scope.selectAllParam;
    });
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

DatascienceModule.filter('propsFilter', function () {
  return function (items, props) {
    var out = [];

    if (angular.isArray(items)) {
      var keys = Object.keys(props);

      items.forEach(function (item) {
        var itemMatches = false;

        for (var i = 0; i < keys.length; i++) {
          var prop = keys[i];
          var text = props[prop].toLowerCase();
          if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
            itemMatches = true;
            break;
          }
        }

        if (itemMatches) {
          out.push(item);
        }
      });
    } else {
      // Let the output be the input untouched
      out = items;
    }

    return out;
  };
});