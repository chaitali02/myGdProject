CommonModule = angular.module('CommonModule');

CommonModule.controller('CommonListController', function ($location, $http, cacheService, dagMetaDataService, uiGridConstants, $state, $stateParams, $rootScope, $scope, $sessionStorage, CommonService, FileSaver, Blob, $filter, cacheService, privilegeSvc, $timeout,$q) {
  $scope.isExec = false;
  $scope.isJobExec = false;
  if($stateParams.type.indexOf("exec") !=-1){
    $scope.select = $stateParams.type.toLowerCase();
    $scope.newType = $stateParams.type.toLowerCase();
   }
   else if($stateParams.type.indexOf("Exec") !=-1){
    $scope.select = $stateParams.type.toLowerCase();
    $scope.newType = $stateParams.type.toLowerCase();
   }
   else{
    $scope.select = dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType;
    $scope.newType = dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType; 
  }
  
  $scope.parantType=$stateParams.parantType 
  $scope.autorefreshcounter = 05
  $scope.isFileNameValid=true;
  $scope.isFileSubmitDisable=true;
  $scope.path = dagMetaDataService.statusDefs
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
 
  $rootScope.isCommentDisabled=true;
  $scope.paramTypes=[{"text":"paramlist","caption":"paramlist","disabled": false  },{"text":"paramset","caption":"paramset" ,"disabled": false }];
  var cached = cacheService.getCache('searchCriteria', $scope.select);
  $scope.isJobExec = $stateParams.isJobExec;
  $scope.isExec = $stateParams.isExec;
  $scope.handleGroup = -1;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges[$scope.select] || [];
  if($scope.select =="ingestexec"){
    $scope.privileges = privilegeSvc.privileges["ingestExec"] || [];
  }
  if($scope.select =="mapexec"){
    $scope.privileges = privilegeSvc.privileges["mapExec"] || [];
  }
  $scope.$on('privilegesUpdated', function (e, data) {
  $scope.privileges = privilegeSvc.privileges[$scope.select] || [];
  if($scope.select =="ingestexec"){
    $scope.privileges = privilegeSvc.privileges["ingestExec"] || [];
  }
  if($scope.select =="mapexec"){
    $scope.privileges = privilegeSvc.privileges["mapExec"] || [];
  }
  });

  $scope.updateStats = function () {
    CommonService.getMetaStats($scope.select).then(function (response) {
      if (response.data && response.data.length && response.data.length > 0) {
        $rootScope.metaStats[$scope.select] = response.data[0];
      }
    });
  }
  if($scope.select !="paramlist")
  $scope.updateStats();
  
  var groups = ['profileexec', 'profilegroupexec', 'dqexec', 'dqgroupexec', 'ruleexec', 'rulegroupexec','reconexec','recongroupexec','ingestexec','ingestgroupexec'];

  if(!$scope.isJobExec) {
    $scope.handleGroup = groups.indexOf($scope.select.toLowerCase());
  }
  $scope.caption = dagMetaDataService.elementDefs[$scope.select].caption;
  $scope.detailState = dagMetaDataService.elementDefs[$scope.select].detailState;
  
  $scope.addMode = function () {
    cacheService.searchCriteria = {};
    var stateName;
    if($stateParams.type.toLowerCase() =="ingest2"){
      stateName = dagMetaDataService.elementDefs['ingest2'].detailState;  
    }else{
      stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
    }
   // var stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
    if($scope.parantType){ //for Paramlist
      stateName=stateName+$scope.parantType
    }
    $state.go(stateName);
  }
  
  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
    //console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams
    $http.pendingRequests.forEach(function(request) { 
      if (request.cancel) {
      request.cancel.resolve();
    }  
    });
  });

  $scope.$on('$destroy',function(){
    // allSitesPromise.abort();
  //  $scope.deferred = $q.defer();
  //  $scope.deferred.reject();
  });
  $scope.nonExecTypes = ['datapod', 'dataset', 'expression', 'filter', 'formula', 'function', 'relation', 'algorithm', 'paramlist', 'paramset', 'activity', 'application', 'datasource', 'datastore', 'group', 'privilege', 'role', 'session', 'user', 'vizpod','model','distribution','operatortype','operator','organization'];
  $scope.isExecutable = $scope.nonExecTypes.indexOf($scope.select);
  $scope.isUpload = ($scope.select == 'datapod' ? 0 : -1)
  
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
  // $scope.gridOptions = $scope.isJobExec ? dagMetaDataService.gridOptionsJobExec : ($scope.isExec ? dagMetaDataService.gridOptionsJobExec : dagMetaDataService.gridOptions);
  
  $scope.gridOptions = $scope.isJobExec ? dagMetaDataService.gridOptionsJobExec : ($scope.isExec ? dagMetaDataService.gridOptionsResults : dagMetaDataService.gridOptions);
  if($scope.isJobExec !=true && $scope.isExec !=true){
    if($scope.gridOptions.columnDefs[0].name !="locked"){
      $scope.gridOptions.columnDefs.splice(0,0,{
        displayName: 'Locked',
        name: 'locked',
        maxWidth: 100,
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        cellTemplate: ['<div class="ui-grid-cell-contents">',
        '<div ng-if="row.entity.locked == \'Y\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Unlock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,true)"><i  title ="Lock" class="icon-lock" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
        '<div  ng-if="row.entity.locked == \'N\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Lock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,false)"><i title ="UnLock" class="icon-lock-open" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
        ].join('')
      });
    }
  }
  //fa fa-lock
  //fa fa-unlock-alt
  $scope.filteredRows = [];
  $scope.gridOptions.onRegisterApi = function (gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.gridOptions.data = [];
  
  $scope.action = function (data, mode, privilege) {
    $scope.setActivity(data.uuid, data.version, $scope.select, mode);
    var stateName;
    if($stateParams.type.toLowerCase() =="ingest2"){
      stateName = dagMetaDataService.elementDefs['ingest2'].detailState;  
    }else{
      stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
    }
    if($scope.parantType){ //for Paramlist
      stateName=stateName+$scope.parantType
    }
   // if (mode != 'view') {
      //clearing cache if edit is called
      cacheService.saveCache('searchCriteria', $scope.select, null);
   // }
    
    if (stateName)
      $state.go(stateName, {
        id: data.uuid,
        version: data.version,
        mode: mode == 'view' ? true : false
      });
  }

  $scope.getExec = function (data) {
    var stateName;
    if($scope.select == "dqexec" || $scope.select=="ruleexec"){
      if($stateParams.isExec2){
        stateName = dagMetaDataService.elementDefs[$scope.select].resultState2;
      }
      else{
      stateName = dagMetaDataService.elementDefs[$scope.select].resultState;
     }
    }else{
    stateName = dagMetaDataService.elementDefs[$scope.select].resultState;
    }
    if (stateName) {
      $state.go(stateName, {
        id: data.uuid,
        version: data.version,
        type: ($scope.newType || $scope.select).toLowerCase(),
        name: data.name
        // mode: mode == 'view' ? true : false
      });
    }
  }
  $scope.setStatus = function (row, status) {
    $scope.selectDetail=row;
    $scope.selectDetail.setStatus=status;
    var tempCaption= dagMetaDataService.elementDefs[$scope.select].caption;
    $scope.confMsg=tempCaption.split("Exec")[0];
    var objJson = {}
    objJson.uuid = row.uuid;
    objJson.version = row.version;
    objJson.name=row.name;
    $scope.objDetail=objJson;
    $('#killmodal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.okKill = function () {
    var api = false;
    switch ($scope.newType.toLowerCase()) {
      case 'dqexec':
        api = 'dataqual';
        break;
      case 'dqgroupexec':
        api = 'dataqual';
        break;
      case 'profileexec':
        api = 'profile';
        break;
      case 'profilegroupexec':
        api = 'profile';
        break;
      case 'ruleexec':
        api = 'rule';
        break;
      case 'rulegroupexec':
        api = 'rule';
        break;
      case 'reconexec':
        api = 'recon';
        break;
      case 'recongroupexec':
        api = 'recon';
        break;
      case 'dagexec':
        api = 'dag';
        break;
      case 'batchexec':
        api = 'batch';
        break;
      case 'ingestexec':
        api = 'ingest';
        break;
      case 'ingestgroupexec':
        api = 'ingest';
        break;
      case 'mapexec':
        api = 'map';
        break;
      case 'reportexec':
        api = 'reprot';
        break;
    }
    if (!api) {
      return
    }
    $('#killmodal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.newType == "dagexec" ? "Pipeline "+$scope.selectDetail.setStatus+" Successfully" :$scope.newType == "batchexec" ? "Batch "+$scope.selectDetail.setStatus+" Successfully": $scope.newType.indexOf("group") != -1 ? "Rule Group "+$scope.selectDetail.setStatus+" Successfully" : "Rule "+$scope.selectDetail.setStatus+" Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.put(url + '' + api + '/setStatus?uuid=' + $scope.selectDetail.uuid + '&version=' + $scope.selectDetail.version + '&type=' + $scope.newType + '&status=' + $scope.selectDetail.setStatus.toUpperCase()).then(function (response) {
      console.log(response);
      $rootScope.refreshRowData()
      $scope.refreshRowData();
    });
  }

  $scope.restartExec = function (row, status) {
    $scope.selectDetail=row;
    var tempCaption= dagMetaDataService.elementDefs[$scope.select].caption;
    $scope.confMsg=tempCaption.split("Exec")[0]
    var objJson = {}
    objJson.uuid = row.uuid;
    objJson.version = row.version;
    objJson.name=row.name;
    $scope.objDetail=objJson;
    $('#restartmodal').modal({
      backdrop: 'static',
      keyboard: false
    });
    // var api = false;
    // switch ($scope.newType) {
    //   case 'dqexec':
    //     api = 'dataqual';
    //     break;
    //   case 'dqgroupExec':
    //     api = 'dataqual';
    //     break;
    //   case 'profileExec':
    //     api = 'profile';
    //     break;
    //   case 'profilegroupExec':
    //     api = 'profile';
    //     break;
    //   case 'ruleExec':
    //     api = 'rule';
    //     break;
    //   case 'rulegroupExec':
    //     api = 'rule';
    //     break;
    //   case 'dagexec':
    //     api = 'dag';
    //     break;
    //   case 'reconExec':
    //     api = 'recon';
    //     break;
    //   case 'recongroupExec':
    //     api = 'recon';
    //     break;
    // }
    // if (!api) {
    //   return
    // }
    // notify.type = 'success',
    // notify.title = 'Success',
    // notify.content = $scope.newType == "dagexec" ? "Pipeline Restarted Successfully" : $scope.newType.indexOf("group") != -1 ? "Rule Group Restarted Successfully" : "Rule Restarted Successfully"
    // $scope.$emit('notify', notify);

    // var url = $location.absUrl().split("app")[0];
    // $http.post(url + '' + api + '/restart?uuid=' + row.uuid + '&version=' + row.version + '&type=' + $scope.newType + '&action=execute').then(function (response) {
    //   //console.log(response);
    // });
  }


   $scope.okRestart=function(){
    var api = false;
    switch ($scope.newType) {
      case 'dqExec':
        api = 'dataqual';
        break;
      case 'dqgroupExec':
        api = 'dataqual';
        break;
      case 'profileExec':
        api = 'profile';
        break;
      case 'profilegroupExec':
        api = 'profile';
        break;
      case 'ruleExec':
        api = 'rule';
        break;
      case 'rulegroupExec':
        api = 'rule';
        break;
      case 'dagexec':
        api = 'dag';
        break;
      case 'reconExec':
        api = 'recon';
        break;
      case 'recongroupExec':
        api = 'recon';
        break;
      case 'batchexec':
        api = 'batch';
        break;
      case 'ingestExec':
        api = 'ingest';
        break;
      case 'ingestgroupExec':
        api = 'ingest';
        break;
      case 'mapexec':
        api = 'map';
        break;
      case 'graphexec':
        api = 'graphpod';
        break;
      case 'reportexec':
        api = 'report';
        break;
    }
    if (!api) {
      return
    }
    $('#restartmodal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.newType == "dagexec" ? "Pipeline Restarted Successfully" : $scope.newType == "batchexec" ? "Batch Restarted Successfully": $scope.newType.indexOf("group") != -1 ? "Rule Group Restarted Successfully" : "Rule Restarted Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.post(url + '' + api + '/restart?uuid=' + $scope.selectDetail.uuid + '&version=' + $scope.selectDetail.version + '&type=' + $scope.newType + '&action=execute').then(function (response) {
      //console.log(response);
      $rootScope.refreshRowData()
      $scope.refreshRowData();
    });
   }
  
  $scope.selectData = function (data) {
    $scope.caption = dagMetaDataService.elementDefs[data.type.toLowerCase()].caption;
    $scope.originalData = []
    $scope.originalData = data.data;
    var changerowarray = [];
    if($scope.handleGroup > -1) {
      $scope.newType = data.type;
    }
    $scope.gridOptions.data = [];
    $scope.gridOptions.data = data.data;
    if($scope.select =="paramlist") {
      var countObj={};
      countObj.type=$scope.select;
      countObj.count=data.data.length;      
      $rootScope.metaStats[$scope.select+$scope.parantType]=countObj;
    }
  }

  $scope.refreshData = function (searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
  };
 
  
  $scope.getDetail = function (data) {
    $scope.setActivity(data.uuid, data.version, $scope.select, "export");
    var uuid = data.uuid;
    $scope.selectuuid = uuid;
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.objDetail=objJson;
    $('#filemodal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  
  $scope.delete = function (data, restore) {
    var action = restore == true ? "restore" : "delete";
    $scope.setActivity(data.uuid, data.version, $scope.select, action);
    var uuid = data.id;
    $scope.selectuuid = uuid;
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.objDetail=objJson;
    $scope.deleteModalMsg = restore ? 'Restore' : 'Delete';
    $scope.onSuccessDelete = function (response) {
      data.active = restore ? 'Y' : 'N';
      $('#DeleteConfModal').modal('hide');
      $scope.message = $scope.caption + (restore ? " Restored" : " Deleted") + " Successfully";
    }
   
    $scope.okDelete = function () {
      CommonService[restore ? 'restore' : 'delete']($scope.selectuuid, $scope.select).then(function (response) {
        $scope.onSuccessDelete(response.data);
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.message
        $scope.$emit('notify', notify);
      });
    }
   
    $('#DeleteConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
 
  
  $scope.publish = function (data, unpublish){
    var action = unpublish == true ? "unpublish" : "publish";
    $scope.setActivity(data.uuid, data.version, $scope.select, action);
    var uuid = data.id;
    $scope.selectuuid = uuid;
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.objDetail=objJson;
    $scope.publishModalMsg = unpublish ? 'Unpublish' : 'Publish';
    $scope.onSuccessPublish = function (response) {
      data.published = unpublish ? 'N' : 'Y';
      $scope.publishmessage = $scope.caption + (unpublish ? " Unpublished" : " Published") + " Successfully";
    }

    $scope.okpublished = function () {
      $('#publishedConfModal').modal('hide');
      CommonService[unpublish ? 'unpublish' : 'publish']($scope.selectuuid, $scope.select).then(function (response) {
      $scope.onSuccessPublish(response.data);
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.publishmessage;
        $scope.$emit('notify', notify);
      });
    }
    $('#publishedConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.lockOrUnLock = function (data, unLock){
    
    var action = unLock== true ? "unLock" : "lock";
    $scope.setActivity(data.uuid, data.version, $scope.select, action);
    var uuid = data.id;
    $scope.selectuuid = uuid;
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.objDetail=objJson;
    $scope.lockModalMsg = unLock ? 'UnLock' : 'Lock';
    $scope.onSuccessLockOrUnLock = function (response) {
      data.locked = unLock ? 'N' : 'Y';
      $scope.lockmessage = $scope.caption + (unLock ? " UnLocked" : " Locked") + " Successfully";
    }

    $scope.okLocked = function () {
      $('#lockedConfModal').modal('hide');
      CommonService[unLock ? 'unLock' : 'lock']($scope.selectuuid, $scope.select).then(function (response) {
      $scope.onSuccessLockOrUnLock(response.data);
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.lockmessage;
        $scope.$emit('notify', notify);
      });
    }
    $('#lockedConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.okFile = function () {
    $('#filemodal').modal('hide');
    CommonService.getLatestByUuid($scope.selectuuid, $scope.select).then(function (response) {
      onSuccessGetUuid(response.data)
    });
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

  $scope.createCopy = function (data) {
    $scope.setActivity(data.uuid, data.version, $scope.select, "clone");
    var uuid = data.uuid;
    var version = data.version;
    $scope.clone = {};
    $scope.clone.uuid = uuid;
    $scope.clone.version = version;
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.objDetail=objJson;
    $('#clonemodal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.okClone = function () {
    $('#clonemodal').modal('hide');
    CommonService.getSaveAS($scope.clone.uuid, $scope.clone.version, $scope.select).then(function (response) {
      onSuccessSaveAs(response.data);
      $scope.updateStats();
    });
    var onSuccessSaveAs = function (response) {
      var onSuccess = function (response) {
      }
      $scope.originalData.splice(0, 0, response);
      $scope.message = $scope.caption + " Cloned Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
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
  
 


  $scope.getExecParamList=function(){
    $scope.attributeTypes=['datapod','dataset','rule'];
    CommonService.getParamListByType($scope.select,$scope.exeDetail.uuid, $scope.exeDetail.version).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
      if(response.length ==0){
        $scope.executeWithParams(null);
      }else{
        $('#executeParamList').modal({
          backdrop: 'static',
          keyboard: false
        });
        $scope.getAllLatest();
        $scope.paramListHolder = response;
      }
    }

  }

  $scope.executeWithExecParamList=function(){
    $('#executeParamList').modal('hide');
    var execParams={};
    var paramListInfo=[];
    if($scope.paramListHolder.length>0){
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
      execParams.paramListInfo=paramListInfo;
    }
    else{
      execParams=null;
    }
    $scope.executeWithParams(execParams);
  }
  
  $scope.executeWithParams=function(data){
    $scope.executionmsg = $scope.caption + " Submited Successfully"
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.executionmsg
    $scope.$emit('notify', notify);
    CommonService.executeWithParams($scope.select,$scope.exeDetail.uuid,$scope.exeDetail.version,data).then(function (response){onSuccessGetExecuteModel(response.data)});
    var onSuccessGetExecuteModel = function (response) {
      console.log(JSON.stringify(response));
    }
  }

  $scope.getExecParamsSet = function () {
    $scope.paramtablecol = null
    $scope.paramtable = null;
    $scope.isTabelShow = false;
    $scope.isPramsetInProgess=true;
    CommonService.getParamSetByType($scope.select, $scope.exeDetail.uuid, $scope.exeDetail.version).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
      $('#responsive').modal({
        backdrop: 'static',
        keyboard: false
      });
      $scope.isPramsetInProgess=false;
      $scope.allparamset = response;
    }
  }
  
  $scope.getParamListByTrainORRule=function(){
    $scope.paramlistdata=null;
    $scope.isPramlistInProgess=true;
    CommonService.getParamListByTrainORRule($scope.exeDetail.uuid, $scope.exeDetail.version,$scope.select).then(function (response){ onSuccesGetParamListByTrain(response.data)});
    var onSuccesGetParamListByTrain = function (response) {
      $scope.allParamList=response;
      $scope.isPramlistInProgess=false;
      if(response.length == 0){
      $scope.isParamListRquired=false;
      }
    }
  }
  
  $scope.onChangeParamType=function(){
    $scope.allparamset=null;
    $scope.allParamList=null;
    $scope.isParamLsitTable=false;
    $scope.selectParamList=null;
    if($scope.selectParamType =="paramlist"){
      $scope.paramlistdata=null;
      $scope.getParamListByTrainORRule();
    }
    else if($scope.selectParamType =="paramset"){
      $scope.getExecParamsSet();
    }
   
  }

  $scope.onChangeParamList=function(){
    $scope.isParamLsitTable=false;
    CommonService.getParamByParamList($scope.paramlistdata.uuid,"paramlist").then(function (response){ onSuccesGetParamListByTrain(response.data)});
    var onSuccesGetParamListByTrain = function (response) {
      $scope.isParamLsitTable=true;
      $scope.selectParamList=response;
      var paramArray=[];
      for(var i=0;i<response.length;i++){
        var paramInfo={}
          paramInfo.paramId=response[i].paramId; 
          paramInfo.paramName=response[i].paramName;
          paramInfo.paramType=response[i].paramType.toLowerCase();
          if(response[i].paramValue !=null && response[i].paramValue.ref.type == "simple"){
            paramInfo.paramValue=response[i].paramValue.value;
            paramInfo.paramValueType="simple"
        }else if(response[i].paramValue !=null){
          var paramValue={};
          paramValue.uuid=response[i].paramValue.ref.uuid;
          paramValue.type=response[i].paramValue.ref.type;
          paramInfo.paramValue=paramValue;
          paramInfo.paramValueType=response[i].paramValue.ref.type;
        }else{
          
        }
        paramArray[i]=paramInfo;
      }
      $scope.selectParamList.paramInfo=paramArray;
    }
  }

  $scope.onSelectparamSet = function () {
    var paramSetjson = {};
    var paramInfoArray = [];
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
      stage.selected = $scope.selectallattribute;
    });
  }
  $scope.executeWithExecParams = function () {
    if($scope.selectParamType =="paramlist"){
      if($scope.paramlistdata){
        var execParams = {};
        var paramListInfo =[];
        var paramInfo={};
        var paramInfoRef={};
        paramInfoRef.uuid=$scope.paramlistdata.uuid;
        paramInfoRef.type="paramlist";
        paramInfo.ref=paramInfoRef;
        paramListInfo[0]=paramInfo;
        execParams.paramListInfo=paramListInfo;
      }else{
        execParams=null;
      }
      $scope.paramlistdata=null;
      $scope.selectParamType=null;
    }else{
      $scope.newDataList = [];
      $scope.selectallattribute = false;
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
        ref.type = "paramset";
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
    $('#responsive').modal('hide');

    $scope.executionmsg = $scope.caption + " Submited Successfully"
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.executionmsg
    $scope.$emit('notify', notify);
    console.log(JSON.stringify(execParams));
    CommonService.executeWithParams($scope.select,$scope.exeDetail.uuid, $scope.exeDetail.version, execParams).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
      console.log(JSON.stringify(response));

    }
  }
  $scope.execute = function (data) {
    $scope.setActivity(data.uuid, data.version, $scope.select, "execute");
    $('#DagConfExModal').modal({
      backdrop: 'static',
      keyboard: false
    });
    var objJson = {}
    objJson.uuid = data.uuid;
    objJson.version = data.version;
    objJson.name=data.name;
    $scope.exeDetail = objJson
    $scope.objDetail=$scope.exeDetail;

  } //End excutionDag

  $scope.ok = function () {
    
    $('#DagConfExModal').modal('hide');
   
    // if($scope.select == 'rule') {  //|| $scope.select == 'train'
    //   $scope.getExecParamsSet();
    // }
    if($scope.select == 'train' || $scope.select == 'rule' || $scope.select == 'dag' || $scope.select == 'report' || $scope.select == 'rule2' ){
      $scope.selectParamType=null;
      $scope.paramtable=null;
      $scope.isTabelShow=false;
      $scope.paramTypes=null;
      $scope.selectParamType=null;
      $scope.isParamLsitTable=false;
      setTimeout(function(){    $scope.paramTypes=[{"text":"paramlist","caption":"paramlist","disabled": false  },{"text":"paramset","caption":"paramset" ,"disabled": false }];
      ; },100);
      if($scope.select =='rule' || $scope.select =='dag' || $scope.select == 'report' || $scope.select =='rule2'){
        $scope.isParamListRquired=false;
        CommonService.getOneByUuidAndVersion($scope.exeDetail.uuid,$scope.exeDetail.version,$scope.select).then(function (response){onSuccessGetOneByUuidAndVersion(response.data)});
        var onSuccessGetOneByUuidAndVersion = function (response) {
          if(response.paramList !=null){
            $('#responsive').modal({
              backdrop: 'static',
              keyboard: false
            });   
          }else{
            $scope.executionmsg = $scope.caption + " Submited Successfully"
            notify.type = 'success',
            notify.title = 'Success',
            notify.content = $scope.executionmsg
            $scope.$emit('notify', notify);
            CommonService.execute($scope.select, $scope.exeDetail.uuid, $scope.exeDetail.version, null).then(function (response){ onSuccessExecute(response.data)});
            var onSuccessExecute = function (response) {
                console.log("RuleExec: " + JSON.stringify(response))
            }
          }
        }
      }
      else{
        $scope.isParamListRquired=true;
        $('#responsive').modal({
          backdrop: 'static',
          keyboard: false
        });
      }
    }

    else if($scope.select == 'simulate' || $scope.select == 'operator' ){
      $scope.getExecParamList();
    }
    else if($scope.select == 'predict'){
      $scope.executionmsg = $scope.caption + " Submited Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg
      $scope.$emit('notify', notify);
      CommonService.executeWithParams($scope.select,$scope.exeDetail.uuid,$scope.exeDetail.version,null).then(function (response){onSuccessGetExecuteModel(response.data)});
      var onSuccessGetExecuteModel = function (response) {
        console.log(JSON.stringify(response));
      }
    } 
    else{
      $scope.executionmsg = $scope.caption + " Submited Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg
      $scope.$emit('notify', notify);
      CommonService.execute($scope.select, $scope.exeDetail.uuid, $scope.exeDetail.version, null).then(function (response){ onSuccessExecute(response.data)});
      var onSuccessExecute = function (response) {
          console.log("DagExec: " + JSON.stringify(response))
      }
    }
  };

  $scope.getDetailForUpload = function (data,index) {
    console.log(data.index)
    $scope.setActivity(data.uuid, data.version, $scope.select, "uplode");
    var uuid = data.uuid
    $scope.uploaaduuid = data.uuid
    var version = data.version
    $scope.uploadDetail=data;
    $(":file").jfilestyle('clear')
    $("#csv_file").val("");
    $('#fileupload').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.fileNameValidate=function(data){
    console.log(data)
    $scope.isFileNameValid=data.valid;
    $scope.isFileSubmitDisable=!data.valid;
  }
  $scope.getGridOptionsDataIndex=function(id){
    var index=-1;
    for(var i=0;i<$scope.gridOptions.data.length;i++){
      if(id == $scope.gridOptions.data[i].id){
       index=i;
       break;
      }
    }
    return index;
  } 

  $scope.uploadFile = function () {
    if($scope.isFileSubmitDisable){
      $scope.msg = "Special character or space not allowed in file name."
      notify.type = 'info',
      notify.title = 'Info',
      notify.content = $scope.msg
      $scope.$emit('notify', notify);
      return false; 
    }
    var iEl = angular.element(document.querySelector('#csv_file'));
    var file = iEl[0].files[0]
    var fd = new FormData();
    fd.append('csvFileName', file);
    $('#fileupload').modal('hide')
    if(!$scope.searchtext){
      $scope.gridOptions.data[$scope.uploadDetail.index].isupload=true;
    }
    else{
      var index=$scope.getGridOptionsDataIndex($scope.uploadDetail.id)
      if(index!=-1){
        $scope.gridOptions.data[index].isupload=true;
      }
    }
    CommonService.uploadFile($scope.uploaaduuid, fd, "datapod",$scope.fileUpladDesc || "").then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
    var onSuccess = function (response) {
      $scope.fileUpladDesc="";
      if(!$scope.searchtext){
        $scope.gridOptions.data[$scope.uploadDetail.index].isupload=false;
      }
      else{
        var index=$scope.getGridOptionsDataIndex($scope.uploadDetail.id)
        if(index!=-1){
          $scope.gridOptions.data[index].isupload=false;
        }
      }
      $scope,uploadDetail=null;
      $scope.executionmsg = "Data Uploaded Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg
      $scope.$emit('notify', notify);

    }
    var onError = function (response) {
      $('#fileupload').modal('hide');
      if(!$scope.searchtext){
        $scope.gridOptions.data[$scope.uploadDetail.index].isupload=false;
      }
      else{
        var index=$scope.getGridOptionsDataIndex($scope.uploadDetail.id)
        if(index!=-1){
          $scope.gridOptions.data[index].isupload=false;
        }
      }
      $scope,uploadDetail=null;
    }
  }

 
  $scope.setActivity = function (uuid, version, type, action) {
    CommonService.setActivity(uuid, version, type, action).then(function (response) { onSuccessSetActivity(response.data) });
    var onSuccessSetActivity = function (response) {
    }
  }

  var myVar;
  $scope.autoRefreshOnChange = function () {
    if ($scope.autorefresh) {
      myVar = setInterval(function () {
        $rootScope.refreshRowData()
        $scope.refreshRowData();
      }, $scope.autorefreshcounter + "000");
    }
    else {
      clearInterval(myVar);
    }
  }
  $scope.$on('$destroy', function () {
    // Make sure that the interval is destroyed too
    clearInterval(myVar);
  })

  $scope.refreshRowData = function () {
    if ($scope.data.length > 0) {
      $scope.gridOptions.data = $scope.data;
      $scope.$watch('data', function (newValue, oldValue) {
        if (newValue != oldValue && $scope.gridOptions.data.length > 0) {
          for (var i = 0; i < $scope.data.length; i++) {
            if ($scope.data[i].status != $scope.gridOptions.data[i].status) {
              $scope.gridOptions.data[i].status = $scope.data[i].status;
            }
          }
        }
        else {
          $scope.gridOptions.data = [];
          $scope.gridOptions.data = $scope.data;
        }
      }, true);
    }
  }
});
