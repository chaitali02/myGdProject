/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateTrainController', function ($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, TrainService, $http, $location, CommonService) {

  $scope.isTargetNameDisabled = false;
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
  $scope.mode = "false"
  $scope.isSubmitEnable = false;
  $scope.trainData;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.Train = {};
  $scope.Train.versions = [];
  $scope.isshowTrain = false;
  $scope.sourceTypes = ["datapod", "dataset", "rule"];
  $scope.selectSourceType = $scope.sourceTypes[0];
  //$scope.targetTypes = ["datapod", "file"];
  //$scope.selectTargetType = $scope.targetTypes[0];
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isDependencyShow = false;
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 30000 //time in ms
  };

  $scope.close = function () {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('train');
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
  
  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createtrain', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createtrain', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }     
  }

  $scope.getAllLetestModel = function (defaultValue) {
    TrainService.getAllModelByType('N', "model").then(function (response) { onGetAllLatest(response.data) });
    var onGetAllLatest = function (response) {
      $scope.allModel = response;
      if (defaultValue == true) { }

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
  // $scope.getAllLetestTarget=function(defaultValue){
  //   TrainService.getAllLatest($scope.selectTargetType).then(function(response) { onGetAllLatest(response.data)});
  //   var onGetAllLatest = function(response) {
  //     $scope.allTarget = response;
  //     if(typeof $stateParams.id == "undefined" || defaultValue ==true) {
  //       $scope.selectTarget=response[0];
  //     }
  //   }
  // }
  $scope.getAllAttribute = function () {
    TrainService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function (response) { onGetAllAttributeBySource(response.data) });
    var onGetAllAttributeBySource = function (response) {
      //console.log(response)
      $scope.allTargetAttribute = response;


    }
  }
  $scope.getAllLetestModel();
  $scope.getAllLetestSource();
  // $scope.getAllLetestTarget();

  $scope.onChangeModel = function (defaultValue) {
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
          featureMap.featureMapId = i;
          sourceFeature.uuid = response.uuid;
          sourceFeature.type = "model";
          sourceFeature.featureId = response.features[i].featureId;
          sourceFeature.featureName = response.features[i].name;
          featureMap.sourceFeature = sourceFeature;
          featureMapTableArray[i] = featureMap;
          $scope.featureMapTableArray = featureMapTableArray;
        }
      }
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
      $scope.allSource = response
      $scope.selectSource = response[0];
      $scope.onChangeSource();
    }
  }

  $scope.onChangeSource = function () {
    if ($scope.allSource != null && $scope.selectSource != null) {
      $scope.getAllAttribute();
    }
  }

  $scope.getOneByUuidandVersion = function (uuid, version) {
    TrainService.getOneByUuidandVersion(uuid, version, "train").then(function (response) { onSuccessGetLatestByUuid(response.data) });
    var onSuccessGetLatestByUuid = function (response) {
      $scope.trainData = response;
      var selectModel = {}
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.Train.defaultVersion = defaultversion;

      selectModel.uuid = response.dependsOn.ref.uuid;
      selectModel.name = response.dependsOn.ref.name;
      selectModel.version = " ";
      $scope.selectModel = selectModel;

      var selectSource = {};
      $scope.selectSource = null;
      selectSource.uuid = response.source.ref.uuid;
      selectSource.name = response.source.ref.name;
      $scope.selectSource = selectSource;
      // var selectTarget={};
      // $scope.selectTarget=null;
      // selectTarget.uuid=response.target.ref.uuid;
      // selectTarget.name=response.target.ref.name;
      // $scope.selectTargetType=response.target.ref.type
      // $scope.selectTargetType=="file"?$scope.isTargetNameDisabled=true:$scope.isTargetNameDisabled=false
      // $scope.selectTarget=selectTarget;
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
      $scope.getAllAttribute();
      for (var i = 0; i < response.featureAttrMap.length; i++) {
        var featureAttrMap = {};
        var sourceFeature = {};
        var targetFeature = {};
        featureAttrMap.featureAttrMapId = response.featureAttrMap[i].featureMapId;
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
        featureMapTableArray[i] = featureAttrMap;
      }
      $scope.featureMapTableArray = featureMapTableArray;
      $scope.onChangeModel(false);
    }
  }
  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;

    $scope.getAllVersion($stateParams.id)
    $scope.getOneByUuidandVersion($stateParams.id, $stateParams.version);
  }

  $scope.selectVersion = function (uuid, version) {
    $scope.allSource = [];
    $scope.allTarget = [];
    $scope.allModel = [];
    $scope.getAllLetestModel();
    $scope.getAllLetestSource();
    $scope.getAllLetestTarget();
    $scope.getOneByUuidandVersion(uuid, version);
  }

  $scope.submitModel = function () {
    $scope.isshowTrain = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var TrainJson = {}
    TrainJson.uuid = $scope.trainData.uuid
    TrainJson.name = $scope.trainData.name
    TrainJson.desc = $scope.trainData.desc
    TrainJson.active = $scope.trainData.active;
    TrainJson.published = $scope.trainData.published;
    TrainJson.valPercent = $scope.trainData.valPercent;
    TrainJson.trainPercent = $scope.trainData.trainPercent;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
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
    // var target={};
    // var targetref={};
    // targetref.type=$scope.selectTargetType;
    // if($scope.selectTargetType =="datapod")
    // targetref.uuid=$scope.selectTarget.uuid;
    // target.ref=targetref;
    // TrainJson.target=target;
    var featureMap = [];
    if ($scope.featureMapTableArray.length > 0) {
      for (var i = 0; i < $scope.featureMapTableArray.length; i++) {
        var featureMapObj = {};
        featureMapObj.featureMapId = i;
        var sourceFeature = {};
        var sourceFeatureRef = {};
        var targetFeature = {};
        var targetFeatureRef = {};
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
        featureMap[i] = featureMapObj;
      }
    }
    TrainJson.featureAttrMap = featureMap;
    console.log(JSON.stringify(TrainJson))
    TrainService.submit(TrainJson, 'train').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
    if (hidemode == 'yes') {
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


  $scope.onChangeRunImmediately = function () {

    if ($scope.selectedRunImmediately == "YES" && $scope.modelData.dependsOn.ref.type == "algorithm") {
      TrainService.getParamSetByAlgorithm($scope.modelData.dependsOn.ref.uuid, $scope.modelData.dependsOn.ref.version).then(function (response) { onSuccessGetParamSetByAlgorithm(response.data) });
      var onSuccessGetParamSetByAlgorithm = function (response) {
        $scope.allparamset = response
        $scope.isShowExecutionparam = true;
      }
    } else {
      $scope.isShowExecutionparam = false;
      $scope.allparamset = null;
    }
  }


  $scope.trainExecute = function (data) {
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

}); //End CreateModelController
