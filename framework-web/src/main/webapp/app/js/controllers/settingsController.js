AdminModule = angular.module('AdminModule');


var notify = {
  type: 'success',
  title: 'Success',
  timeout: 3000 //time in ms
};
AdminModule.controller('settingsController', function (cacheService,$scope,$stateParams,$window, SettingsService,dagMetaDataService,CommonService,FileSaver,Blob,$filter,$state,privilegeSvc) {


  $scope.activeForm = 0;
  $scope.go = function (index) {
    $scope.tabIndex = index
  }
  if (typeof $stateParams.index != "undefined") {
  $scope.activeForm=$stateParams.index;
  $scope.activeForm =  parseInt($stateParams.index);
  $scope.go($stateParams.index);
  }
 


  //*******************************************Start General,Rule,Meta Engin********************************************************** 
  SettingsService.getSettings().then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
  var onSuccess = function (response) {
    $scope.settingData = response;
  }
  var onError = function (response) {
    $scope.onAccess = true
  }

  $scope.addRowGeneral = function () {
    if ($scope.settingData.generalSetting == null) {
      $scope.settingData.generalSetting = [];
    }
    var filertable = {};
    $scope.settingData.generalSetting.splice($scope.settingData.generalSetting.length, 0, filertable);
  }

  $scope.removeRowGeneral = function () {
    var newDataList = [];
    $scope.selectedAllGeneral = false;
    angular.forEach($scope.settingData.generalSetting, function (selectedGeneral) {
      if (!selectedGeneral.selectedGeneral) {
        newDataList.push(selectedGeneral);
      }
    });
    $scope.settingData.generalSetting = newDataList;
  }

  $scope.checkAllGeneralRow = function () {
    if (!$scope.selectedAllGeneral) {
      $scope.selectedAllGeneral = true;
    }
    else {
      $scope.selectedAllGeneral = false;
    }
    angular.forEach($scope.settingData.generalSetting, function (filter) {
      filter.selectedGeneral = $scope.selectedAllGeneral;
    });
  }

  $scope.addRowMeta = function () {
    if ($scope.settingData.metaEngine == null) {
      $scope.settingData.metaEngine = [];
    }
    var filertable = {};
    $scope.settingData.metaEngine.splice($scope.settingData.metaEngine.length, 0, filertable);
  }
  $scope.removeRowMeta = function () {
    var newDataList = [];
    $scope.selectedAllMeta = false;
    angular.forEach($scope.settingData.metaEngine, function (selectedMeta) {
      if (!selectedMeta.selectedMeta) {
        newDataList.push(selectedMeta);
      }
    });
    $scope.settingData.metaEngine = newDataList;
  }

  $scope.checkAllMetaRow = function () {
    if (!$scope.selectedAllMeta) {
      $scope.selectedAllMeta = true;
    }
    else {
      $scope.selectedAllMeta = false;
    }
    angular.forEach($scope.settingData.metaEngine, function (filter) {
      filter.selectedMeta = $scope.selectedAllMeta;
    });
  }

  $scope.addRowRule = function () {
    if ($scope.settingData.ruleEngine == null) {
      $scope.settingData.ruleEngine = [];
    }
    var filertable = {};
    $scope.settingData.ruleEngine.splice($scope.settingData.ruleEngine.length, 0, filertable);
  }

  $scope.removeRowRule = function () {
    var newDataList = [];
    $scope.selectedAllRule = false;
    angular.forEach($scope.settingData.ruleEngine, function (selectedRule) {
      if (!selectedRule.selectedRule) {
        newDataList.push(selectedRule);
      }
    });
    $scope.settingData.ruleEngine = newDataList;
  }

  $scope.checkAllRuleRow = function () {
    if (!$scope.selectedAllRule) {
      $scope.selectedAllRule = true;
    }
    else {
      $scope.selectedAllRule = false;
    }
    angular.forEach($scope.settingData.ruleEngine, function (filter) {
      filter.selectedRule = $scope.selectedAllRule;
    });
  }

  $scope.submitSettings = function () {
    var settingJson = {}
    var generalSettingArray = []
    var metaEngineArray = []
    var ruleEngineArray = []
    for (var i = 0; i < $scope.settingData.generalSetting.length; i++) {
      var generalSetting = {};
      generalSetting.propertyName = $scope.settingData.generalSetting[i].propertyName;
      generalSetting.propertyValue = $scope.settingData.generalSetting[i].propertyValue;
      generalSettingArray[i] = generalSetting;
    }
    settingJson.generalSetting = generalSettingArray;
    for (var j = 0; j < $scope.settingData.metaEngine.length; j++) {
      var metaEngine = {};
      metaEngine.propertyName = $scope.settingData.metaEngine[j].propertyName;
      metaEngine.propertyValue = $scope.settingData.metaEngine[j].propertyValue;
      metaEngineArray[j] = metaEngine;
    }

    settingJson.metaEngine = metaEngineArray;
    for (k = 0; k < $scope.settingData.ruleEngine.length; k++) {
      var ruleEngine = {};
      ruleEngine.propertyName = $scope.settingData.ruleEngine[k].propertyName;
      ruleEngine.propertyValue = $scope.settingData.ruleEngine[k].propertyValue;
      ruleEngineArray[k] = ruleEngine;
    }
    settingJson.ruleEngine = ruleEngineArray;
    SettingsService.setSetting(settingJson).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = 'Settings Saved Successfully'
      $scope.$emit('notify', notify);
    }
  }
  //***********************************End General,Rule,Meta Engin*********************************

  //***********************************Start Graph Engin*******************************************
  $scope.goToPriviousTab = function () {
    $scope.activeForm = 2
  }
  
  $scope.callBulidGraph = function () {
    $scope.isDataLodingBG = true;
    SettingsService.buildGraph().then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = 'Graph Build Successfully'
      $scope.$emit('notify', notify);
      $scope.isDataLodingBG = false;
    }
  }
//***************************************End Graph Engin************************************************

//***************************************Start Application Engin****************************************
$scope.isUpload=-1;
$scope.selectType= dagMetaDataService.elementDefs['appconfig'].metaType;
$scope.caption= dagMetaDataService.elementDefs['appconfig'].caption;
$scope.detailState = dagMetaDataService.elementDefs[$scope.selectType.toLowerCase()].detailState;
$scope.gridOptions = dagMetaDataService.gridOptions;
$scope.privileges = [];
$scope.privileges = privilegeSvc.privileges[$scope.selectType.toLowerCase()] || [];
 
$scope.$on('privilegesUpdated', function (e, data) {
  $scope.privileges = privilegeSvc.privileges[$scope.selectType.toLowerCase()] || [];  
});

$scope.filteredRows = [];
$scope.gridOptions.onRegisterApi = function (gridApi) {
  $scope.gridApi = gridApi;
  $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
};
$scope.getGridStyle = function () {
  var style = {
    'margin-top': '10px',
    'margin-bottom': '10px',
  }
  if ($scope.filteredRows && $scope.filteredRows.length > 0) {
    style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
  }
  else {
    style['height'] = "100px";
  }
  return style;
}

$scope.setActivity = function (uuid, version, type, action) {
  CommonService.setActivity(uuid, version, type, action).then(function (response) { onSuccessSetActivity(response.data) });
  var onSuccessSetActivity = function (response) {
  }
}
$scope.updateStats = function () {
  CommonService.getMetaStats($scope.selectType).then(function (response) {
    if (response.data && response.data.length && response.data.length > 0) {
      $rootScope.metaStats[$scope.select] = response.data[0];
    }
  });
}
$scope.refreshData = function (searchtext) {
  $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
};
$scope.addMode = function () {
  cacheService.searchCriteria = {};
  $state.go($scope.detailState);
}

$scope.action = function (data, mode, privilege) {
  cacheService.searchCriteria = {};
  $scope.setActivity(data.uuid, data.version, $scope.selectType, mode);
  if($scope.detailState)
    $state.go($scope.detailState, {
      id: data.uuid,
      version: data.version,
      mode: mode == 'view' ? true : false
    });
}

$scope.getDetail = function (data) {
  $scope.actionType='export'
  $scope.modelMsg='Save File ?'
  $scope.setActivity(data.uuid, data.version, $scope.selectType, "export");
  var uuid = data.uuid;
  $scope.selectTypeUuid =uuid
  $('#ConfModal').modal({
    backdrop: 'static',
    keyboard: false
  });
}
$scope.okSubmit=function(actionType){
  if(actionType == 'export'){
    $scope.export();
  }
  else if(actionType == 'delete'){
    $scope.okDelete();
  }
  else if(actionType=='publish'){
    $scope.okpublished();
  }
  else if(actionType == 'clone'){
    $scope.okClone();
  }
}

$scope.export = function () {
  $('#ConfModal').modal('hide');
  CommonService.getLatestByUuid($scope.selectTypeUuid, $scope.selectType).then(function (response) {onSuccessGetUuid(response.data)});
  var onSuccessGetUuid = function (response) {
    var jsonobj = angular.toJson(response, true);
    var data = new Blob([jsonobj], {
      type: 'application/json;charset=utf-8'
    });
    FileSaver.saveAs(data, response.name + '.json');
    $scope.message = $scope.caption + " Downloaded Successfully";
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.message
    $scope.$emit('notify', notify);
  }
}

$scope.delete = function (data, restore) {
  var action = restore == true ? "restore" : "delete";
  $scope.actionType='delete'
  $scope.setActivity(data.uuid, data.version, $scope.selectType, action);
  var uuid = data.id;
  $scope.selectuuid = uuid;
  $scope.modelMsg = restore ? 'Restore ? ' : 'Delete ?';
  $('#ConfModal').modal({
    backdrop: 'static',
    keyboard: false
  });
  $scope.onSuccessDelete = function (response) { 
    data.active = restore ? 'Y' : 'N';
    $('#ConfModal').modal('hide');
    $scope.message = $scope.caption + (restore ? " Restored" : " Deleted") + " Successfully";
  }
  $scope.okDelete = function () {
    CommonService[restore ? 'restore' : 'delete']($scope.selectuuid, $scope.selectType).then(function (response) {
      $scope.onSuccessDelete(response.data);
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.message
      $scope.$emit('notify', notify);
    });
  }
}

$scope.publish = function (data, unpublish) {
  var action = unpublish == true ? "unpublish" : "publish";
  $scope.actionType='publish'
  $scope.setActivity(data.uuid, data.version, $scope.selectType, action);
  var uuid = data.id;
  $scope.selectuuid = uuid;
  $scope.modelMsg = unpublish ? 'Unpublish ? ' : 'Publish ?';
  $('#ConfModal').modal({
    backdrop: 'static',
    keyboard: false
  });
  $scope.onSuccessPublish = function (response) {
    data.published = unpublish ? 'N' : 'Y';
    $scope.publishmessage = $scope.caption + (unpublish ? " Unpublished" : " Published") + " Successfully";
  }

$scope.okpublished = function () {
  $('#ConfModal').modal('hide');
  CommonService[unpublish ? 'unpublish' : 'publish']($scope.selectuuid, $scope.selectType).then(function (response) {
  $scope.onSuccessPublish(response.data);
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.publishmessage;
    $scope.$emit('notify', notify);
  });
}
}
$scope.createCopy = function (data) {
  $scope.setActivity(data.uuid, data.version, $scope.selectType, "clone");
  $scope.actionType='clone'
  var uuid = data.uuid;
  var version = data.version;
  $scope.clone = {};
  $scope.clone.uuid = uuid;
  $scope.clone.version = version;
  $scope.modelMsg = 'Clone ?';
  $('#ConfModal').modal({
    backdrop: 'static',
    keyboard: false
  });
}

$scope.okClone = function () {
  $('#ConfModal').modal('hide');
  CommonService.getSaveAS($scope.clone.uuid, $scope.clone.version, $scope.selectType).then(function (response) {
    onSuccessSaveAs(response.data);
    $scope.updateStats();
  });
  var onSuccessSaveAs = function (response) {
    $scope.originalData.splice(0, 0, response);
    $scope.message = $scope.caption + " Cloned Successfully"
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.message
    $scope.$emit('notify', notify);
  }
}
$scope.gridOptions.data = [];
$scope.selectData=function(data){
  $scope.gridOptions.data = data.data;
  $scope.originalData=$scope.gridOptions.data 
}

//***************************************End Application Engin****************************************


});
