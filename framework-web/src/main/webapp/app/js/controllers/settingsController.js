AdminModule = angular.module('AdminModule');
var notify = {
  type: 'success',
  title: 'Success',
  timeout: 3000 //time in ms
};

AdminModule.controller('settingsController', function ($rootScope,$scope,$stateParams, SettingsService,dagMetaDataService,CommonService,FileSaver,Blob,$filter,$state,privilegeSvc) {


  $scope.activeForm = 0;
  $scope.go = function (index) {
    $scope.tabIndex = index;
    if(index ==5){
      $scope.getProcessStatus();
    }
    if(index == 3){
      $scope.refresh();
    }
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

  $scope.searchForm = {};
  $scope.tz = localStorage.serverTz;
  var matches = $scope.tz.match(/\b(\w)/g);
  $scope.timezone = matches.join('');
  $scope.autoRefreshCounter = 05;
  $scope.autoRefreshResult = false;
  $scope.path = dagMetaDataService.statusDefs;
   
  $scope.getGridStyle = function () {
      var style = {
          'margin-top': '10px',
          'margin-bottom': '10px',
      }
      if ($scope.filteredRows && $scope.filteredRows.length > 0) {
          style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
      } else {
          style['height'] = "100px"
      }
      return style;
  }
  $scope.gridOptionsGraphEngin = {};
  $scope.gridOptionsGraphEngin = dagMetaDataService.gridOptionsResultDefault;
  $scope.gridOptionsGraphEngin.columnDefs.push( {
    displayName: 'Status',
    name: 'status',
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    maxWidth: 100,
    cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm label-success" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{grid.appScope.path[row.entity.status].caption}}</div></div>'
  });
  
  $scope.gridOptionsGraphEngin.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;
      $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  
  $scope.refreshDataGraphEngin = function (searchtext) {
      $scope.gridOptionsGraphEngin.data = $filter('filter')($scope.originalDataGraphEngin,searchtext, undefined);
  };
   
  $scope.refresh = function () {
    $scope.searchForm.execname = "";
    $scope.allExecName = [];
    $scope.searchForm.username = "";
    $scope.searchForm.tags = [];
    $scope.searchForm.status = "";
    $scope.searchForm.startdate = null;
    $scope.searchForm.enddate = null;
    $scope.allStatus = [{
        "caption": "PENDING",
        "name": "PENDING"
    },
    {
        "caption": "RUNNING",
        "name": "RUNNING"
    },
    {
        "caption": "COMPLETED",
        "name": "COMPLETED"
    },
    {
        "caption": "FAILED",
        "name": "FAILED"
    }
    ];
    $scope.getAllLatest();
    $scope.getBaseEntityStatusByCriteria();
  };
  
  var myVar;
  $scope.autoRefreshOnChange = function (autorefresh) {
    $scope.autorefresh=autorefresh;
    if ($scope.autorefresh) {
        myVar = setInterval(function () {
            $scope.getBaseEntityStatusByCriteria();
        }, $scope.autoRefreshCounter + "000");
    }
    else {
        clearInterval(myVar);
    }
  }

 
  $scope.$on('$destroy', function () {
      // Make sure that the interval is destroyed too
      clearInterval(myVar);
      
  });
 
  $scope.getAllLatest = function () {
    CommonService.getAllLatest("user").then(function (response) { onSuccessGetAllLatestExec(response.data) });
    var onSuccessGetAllLatestExec = function (response) {
        $scope.allUser = response;
    }
  }

  $scope.getBaseEntityStatusByCriteria = function () {
    var startdate = ""
    if ($scope.searchForm.startdate != null) {
        startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
        startdate = startdate + " UTC"
    }
    var enddate = "";
    if ($scope.searchForm.enddate != null) {
        enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
        enddate = enddate + " UTC";
    }

    var tags = [];
    if ($scope.searchForm.tags) {
        for (i = 0; i < $scope.searchForm.tags.length; i++) {
            tags[i] = $scope.searchForm.tags[i].text;
        }
    }
    tags = tags.toString();
    CommonService.getBaseEntityStatusByCriteria(dagMetaDataService.elementDefs['processexec'].execType, $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '', $scope.searchForm.published || '', $scope.searchForm.status || '').then(function (response) { onSuccess(response.data) }, function error() {
        $scope.loading = false;
    });
    var onSuccess = function (response) {
        // console.log(response);
        $scope.gridOptionsGraphEngin.data = response;
        $scope.originalDataGraphEngin = response;
    }
  }
  
  
  $scope.goToPriviousTab = function () {
    $scope.activeForm = 2
  }
  
  $scope.callBulidGraph = function () {
    //$scope.isDataLodingBG = true;
    $scope.autorefresh=true;
    $scope.autoRefreshOnChange ($scope.autorefresh);
    SettingsService.buildGraph().then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      $scope.autorefresh=false;
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = 'Graph Refreshed Successfully'
      $scope.$emit('notify', notify);
      //$scope.isDataLodingBG = false;
      $scope.getBaseEntityStatusByCriteria();
    }
  }


//***************************************End Graph Engin************************************************



//***************************************Start Application Engin****************************************
$scope.selectedCheckBox={};
$scope.selectedCheckBox.selectedAllRowAppConfig=false;
$scope.type = [
  {"name":"string","caption":"string"},
  {"name":"double","caption":"double"},
   {"name":"date","caption":"date"}, 
  {"name":"integer","caption":"integer"}]
$scope.addRowAppConfig = function () {
  if ($scope.configTable == null) {
    $scope.configTable = [];
  }
  var paramjson = {}
  paramjson.configId = $scope.configTable.length;
  $scope.configTable.splice($scope.configTable.length, 0, paramjson);
}

$scope.selectAllRowAppConfig = function () {
  angular.forEach($scope.configTable, function (stage) {
    stage.selected = $scope.selectedCheckBox.selectedAllRowAppConfig;
  });
}

$scope.getAppConfigByApp=function(){
  SettingsService.getAppConfigByApp().then(function (response) { onSuccessGetAppConfigByApp(response.data) });
  var onSuccessGetAppConfigByApp = function (response) {
    $scope.configTable = response;
  }
}
$scope.getAppConfigByApp();
$scope.removeRowAppConfig = function () {
  var newDataList = [];
  angular.forEach($scope.configTable, function (selected) {
    if (!selected.selected) {
      newDataList.push(selected);
    }
  });
  $scope.configTable = newDataList;
  $scope.selectedCheckBox.selectedAllRowAppConfig=false;
}

$scope.submitAppConfig=function(){
  $scope.dataLoading=true;
  CommonService.getLatestByUuid($rootScope.appUuid,"application").then(function (response) { onSuccessGetLatestByUuid(response.data) });
  var onSuccessGetLatestByUuid = function (response) {
      $scope.applicationData = response;
      var appconfig={}
      appconfig.name=response.name;
      appconfig.active=response.active;
      appconfig.publicFlag=response.publicFlag;
      var dependsOn={};
      var ref={};
      ref.uuid=$rootScope.appUuid;
      ref.type="application";
      dependsOn.ref=ref;
      appconfig.dependsOn=dependsOn;
      appconfig.tags=response.tags;
      var configInfoArray = [];
		  if ($scope.configTable &&$scope.configTable.length > 0) {
        for (var i = 0; i < $scope.configTable.length; i++) {
          var paraminfo = {};
          paraminfo.configId = $scope.configTable[i].configId;
          paraminfo.configName = $scope.configTable[i].configName;
          paraminfo.configType = $scope.configTable[i].configType;
          paraminfo.configVal = $scope.configTable[i].configVal;
          configInfoArray[i] = paraminfo;
        }
		  }
		  appconfig.configInfo = configInfoArray;
      CommonService.submit(appconfig,"appconfig").then(function (response) { onSuccessSubmit(response.data) });
      var onSuccessSubmit = function (response) {
        $scope.dataLoading=false
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = 'App Config Saved Successfully'
        $scope.$emit('notify', notify);
     }
  }

}


//***************************************End Application Engin****************************************

//***************************************Start Prediction Engin****************************************
$scope.getProcessStatus=function(){
  $scope.isCheckingStatus=true;
  $scope.serverStatus="Checking Status"
  SettingsService.getProcessStatus().then(function (response) {onSuccessGetProcessStatus(response.data)},function(response){ OnError(response)});
  var onSuccessGetProcessStatus= function (response) {
    $scope.isCheckingStatus=false;
    $scope.serverStatus="Running"
  }
  var OnError=function(response){
    $scope.isCheckingStatus=false;
    $scope.serverStatus="Not Running"
  }
}

$scope.startServer=function(){
  $scope.isInprogessStatus=true;
  SettingsService.startProcess().then(function (response) {onSuccessGetStartProcess(response.data)},function(response){ OnError(response)});
  var onSuccessGetStartProcess= function (response) {
    $scope.isInprogessStatus=false;
    $scope.serverStatus="Checking Status"
    $scope.getProcessStatus();
  }
  var OnError=function(response){
    $scope.isInprogessStatus=false;
    $scope.getProcessStatus();
  }
}

  $scope.stopeServer=function(){
    $scope.isInprogessStatus=true;
    SettingsService.stopProcess().then(function (response) {onSuccessGetStopProcess(response.data)},function(response){ OnError(response)});
    var onSuccessGetStopProcess= function (response) {
      $scope.isInprogessStatus=false;
      $scope.getProcessStatus();
    }
    var OnError=function(response){
      $scope.isInprogessStatus=false;
      $scope.getProcessStatus();
  }
}
//***************************************End Prediction Engin******************************************


});
