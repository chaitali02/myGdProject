AdminModule = angular.module('AdminModule');


var notify = {
  type: 'success',
  title: 'Success',
  timeout: 3000 //time in ms
};
AdminModule.controller('settingsController', function($scope, $window,SettingsService) {
  
  $scope.activeForm=0
  SettingsService.getSettings().then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
  var onSuccess=function(response){
      $scope.settingData=response;
  }
  var onError=function(response){
    $scope.onAccess=true
  }

  $scope.addRowGeneral=function(){
    if($scope.settingData.generalSetting == null){
        $scope.settingData.generalSetting=[];
    }
    var filertable={};
    $scope.settingData.generalSetting.splice($scope.settingData.generalSetting.length, 0,filertable);
  }
    
  $scope.removeRowGeneral=function(){
    var newDataList=[];
    $scope.selectedAllGeneral=false;
    angular.forEach($scope.settingData.generalSetting, function(selectedGeneral){
      if(!selectedGeneral.selectedGeneral){
        newDataList.push(selectedGeneral);
      }
    });
    $scope.settingData.generalSetting = newDataList;
  }
    
  $scope.checkAllGeneralRow = function(){
    if (!$scope.selectedAllGeneral){
      $scope.selectedAllGeneral = true;
    }
    else {
      $scope.selectedAllGeneral = false;
    }
    angular.forEach($scope.settingData.generalSetting, function(filter) {
      filter.selectedGeneral = $scope.selectedAllGeneral;
    });
  }

  $scope.addRowMeta=function(){
    if($scope.settingData.metaEngine == null){
      $scope.settingData.metaEngine=[];
    }
    var filertable={};
    $scope.settingData.metaEngine.splice($scope.settingData.metaEngine.length, 0,filertable);
  }
  $scope.removeRowMeta=function(){
    var newDataList=[];
    $scope.selectedAllMeta=false;
    angular.forEach($scope.settingData.metaEngine, function(selectedMeta){
      if(!selectedMeta.selectedMeta){
        newDataList.push(selectedMeta);
      }
    });
    $scope.settingData.metaEngine = newDataList;
  }
  
  $scope.checkAllMetaRow = function(){
    if (!$scope.selectedAllMeta){
      $scope.selectedAllMeta = true;
    }
    else {
      $scope.selectedAllMeta = false;
    }
    angular.forEach($scope.settingData.metaEngine, function(filter) {
      filter.selectedMeta = $scope.selectedAllMeta;
    });
  }
    
  $scope.addRowRule=function(){
    if($scope.settingData.ruleEngine == null){
      $scope.settingData.ruleEngine=[];
    }
    var filertable={};
    $scope.settingData.ruleEngine.splice($scope.settingData.ruleEngine.length, 0,filertable);
  }
  
  $scope.removeRowRule=function(){
    var newDataList=[];
    $scope.selectedAllRule=false;
    angular.forEach($scope.settingData.ruleEngine, function(selectedRule){
      if(!selectedRule.selectedRule){
        newDataList.push(selectedRule);
      }
    });
  $scope.settingData.ruleEngine = newDataList;
  }
  
  $scope.checkAllRuleRow = function(){
    if (!$scope.selectedAllRule){
      $scope.selectedAllRule = true;
    }
    else {
      $scope.selectedAllRule = false;
    }
    angular.forEach($scope.settingData.ruleEngine, function(filter) {
      filter.selectedRule = $scope.selectedAllRule;
    });
  }
  
  $scope.submitSettings=function() {
    var settingJson={}
    var generalSettingArray=[]
    var metaEngineArray=[]
    var ruleEngineArray=[]
    for(var i=0;i< $scope.settingData.generalSetting.length;i++){
      var generalSetting={};
      generalSetting.propertyName=$scope.settingData.generalSetting[i].propertyName;
      generalSetting.propertyValue=$scope.settingData.generalSetting[i].propertyValue;
      generalSettingArray[i]=generalSetting;
    }
    settingJson.generalSetting=generalSettingArray;
      for(var j=0;j< $scope.settingData.metaEngine.length;j++){
      var metaEngine={};
      metaEngine.propertyName=$scope.settingData.metaEngine[j].propertyName;
      metaEngine.propertyValue=$scope.settingData.metaEngine[j].propertyValue;
      metaEngineArray[j]=metaEngine;
    }
    
    settingJson.metaEngine=metaEngineArray;
    for(k=0;k< $scope.settingData.ruleEngine.length;k++){
      var ruleEngine={};
      ruleEngine.propertyName=$scope.settingData.ruleEngine[k].propertyName;
      ruleEngine.propertyValue=$scope.settingData.ruleEngine[k].propertyValue;
      ruleEngineArray[k]=ruleEngine;
    }
    settingJson.ruleEngine=ruleEngineArray;
    SettingsService.setSetting(settingJson).then(function (response) {onSuccess(response.data)});
    var onSuccess=function(response){
      notify.type='success',
      notify.title= 'Success',
      notify.content='Settings Saved Successfully'
      $scope.$emit('notify', notify);
    }
  }
  $scope.go=function(index){
   $scope.tabIndex=index
  }
  $scope.goToPriviousTab=function(){
    $scope.activeForm=2
  }
  $scope.callBulidGraph=function(){
    $scope.isDataLodingBG=true;
    SettingsService.buildGraph().then(function (response) {onSuccess(response.data)});
    var onSuccess=function(response){
      notify.type='success',
      notify.title= 'Success',
      notify.content='Graph Build Successfully'
      $scope.$emit('notify', notify);
      $scope.isDataLodingBG=false;
    }
  }
});
