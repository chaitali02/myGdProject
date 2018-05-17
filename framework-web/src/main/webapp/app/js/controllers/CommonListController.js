CommonModule = angular.module('CommonModule');

CommonModule.controller('CommonListController', function ($location, $http, cacheService, dagMetaDataService, uiGridConstants, $state, $stateParams, $rootScope, $scope, $sessionStorage, CommonService, FileSaver, Blob, $filter, cacheService, privilegeSvc, $timeout) {
  $scope.isExec = false;
  $scope.isJobExec = false;
  $scope.select = $stateParams.type.toLowerCase();
  $scope.newType = $stateParams.type.toLowerCase();
  $scope.parantType=$stateParams.parantType 
  $scope.autorefreshcounter = 05
  $scope.isFileNameValid=true;
  $scope.isFileSubmitDisable=true;
  $scope.path = dagMetaDataService.statusDefs
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };

  var cached = cacheService.getCache('searchCriteria', $scope.select);
  $scope.isJobExec = $stateParams.isJobExec;
  $scope.isExec = $stateParams.isExec;
  $scope.handleGroup = -1;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges[$scope.select] || [];
  
  $scope.$on('privilegesUpdated', function (e, data) {
  $scope.privileges = privilegeSvc.privileges[$scope.select] || [];
    
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
  
  var groups = ['profileexec', 'profilegroupexec', 'dqexec', 'dqgroupexec', 'ruleexec', 'rulegroupexec','reconexec','recongroupexec'];
  // $scope.pagination={
  //   currentPage:1,
  //   pageSize:10,
  //   paginationPageSizes:[10, 25, 50, 75, 100],
  //   maxSize:5,
  // }

  if (!$scope.isJobExec) {
    $scope.handleGroup = groups.indexOf($scope.select.toLowerCase());
  }
  $scope.caption = dagMetaDataService.elementDefs[$scope.select].caption;
  $scope.detailState = dagMetaDataService.elementDefs[$scope.select].detailState;
  
  $scope.addMode = function () {
    cacheService.searchCriteria = {};
    var stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
    if($scope.parantType){ //for Paramlist
      stateName=stateName+$scope.parantType
    }
    $state.go(stateName);
  }
  
  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
    //console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams
  });

  $scope.nonExecTypes = ['datapod', 'dataset', 'expression', 'filter', 'formula', 'function', 'load', 'relation', 'algorithm', 'paramlist', 'paramset', 'activity', 'application', 'datasource', 'datastore', 'group', 'privilege', 'role', 'session', 'user', 'vizpod','model','distribution','operatortype','operator'];
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
  $scope.filteredRows = [];
  $scope.gridOptions.onRegisterApi = function (gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.gridOptions.data = [];
  
  $scope.action = function (data, mode, privilege) {
    $scope.setActivity(data.uuid, data.version, $scope.select, mode);
    var stateName = dagMetaDataService.elementDefs[$scope.select].detailState;
    if($scope.parantType){ //for Paramlist
      stateName=stateName+$scope.parantType
    }
    if (mode != 'view') {
      //clearing cache if edit is called
      cacheService.saveCache('searchCriteria', $scope.select, null);
    }
    
    if (stateName)
      $state.go(stateName, {
        id: data.uuid,
        version: data.version,
        mode: mode == 'view' ? true : false
      });
  }

  $scope.getExec = function (data) {
    var stateName = dagMetaDataService.elementDefs[$scope.select].resultState;
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
    var api = false;
    switch ($scope.newType) {
      case 'dqexec':
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
      case 'reconExec':
        api = 'recon';
        break;
      case 'recongroupExec':
        api = 'recon';
        break;
      case 'dagexec':
        api = 'dag';
        break;
    }
    if (!api) {
      return
    }
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.newType == "dagexec" ? "Pipeline Killed Successfully" : $scope.newType.indexOf("group") != -1 ? "Rule Group Killed Successfully" : "Rule Killed Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.put(url + '' + api + '/setStatus?uuid=' + row.uuid + '&version=' + row.version + '&type=' + $scope.newType + '&status=' + status).then(function (response) {
      console.log(response);
    });
  }

  $scope.restartExec = function (row, status) {
    var api = false;
    switch ($scope.newType) {
      case 'dqexec':
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
    }
    if (!api) {
      return
    }
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = $scope.newType == "dagexec" ? "Pipeline Restarted Successfully" : $scope.newType.indexOf("group") != -1 ? "Rule Group Restarted Successfully" : "Rule Restarted Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.post(url + '' + api + '/restart?uuid=' + row.uuid + '&version=' + row.version + '&type=' + $scope.newType + '&action=execute').then(function (response) {
      //console.log(response);
    });
  }

  $scope.selectData = function (data) {
    $scope.caption = dagMetaDataService.elementDefs[data.type.toLowerCase()].caption;
    $scope.originalData = []
    $scope.originalData = data.data;
    var changerowarray = [];
    if ($scope.handleGroup > -1) {
      // $scope.newType = data.type+'exec';
      $scope.newType = data.type;
      // if(data.data.length >0){
      //   $scope.gridOptions.data=data.data;
      //   $scope.$watch('originalData',function(newValue, oldValue) {
      //     if(newValue != oldValue  && $scope.gridOptions.data.length > 0) {
      //
      //     for(var i=0;i<$scope.originalData.length;i++) {
      //       if($scope.originalData[i].status != $scope.gridOptions.data[i].status){
      //          $scope.gridOptions.data[i].status=$scope.originalData[i].status;
      //        }
      //     }
      //     }
      //     else{
      //       $scope.gridOptions.data =[];
      //       $scope.gridOptions.data=data.data;
      //     }
      //   },true);
      // }
    }
    //
    // else{
    // $scope.gridOptions.data =[];
    // $scope.gridOptions.data=data.data;
    // }
    $scope.gridOptions.data = [];
    $scope.gridOptions.data = data.data;
    
    if($scope.select =="paramlist") {
      var countObj={};
      countObj.type=$scope.select;
      countObj.count=data.data.length;      
      $rootScope.metaStats[$scope.select+$scope.parantType]=countObj;
    }
    // if($scope.originalData.length >0){
    //   $scope.getResults($scope.originalData);
    // }
  }
  $scope.refreshData = function (searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
  };
  
  // $scope.refreshData = function(s) {
  //   $scope.gridOptions.data
  //    var data= $filter('filter')($scope.originalData, s, undefined);
  //   $scope.getResults(data);
  //
  // };
  
  $scope.getDetail = function (data) {
    $scope.setActivity(data.uuid, data.version, $scope.select, "export");
    var uuid = data.uuid;
    $scope.selectuuid = uuid
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
    $scope.deleteModalMsg = restore ? 'Restore' : 'Delete';
    $scope.onSuccessDelete = function (response) {
      // $rootScope.refreshSearchResults();
      //$scope.originalData.splice($scope.originalData.indexOf(data),1);
      data.active = restore ? 'Y' : 'N';
      $('#DeleteConfModal').modal('hide');
      $scope.message = $scope.caption + (restore ? " Restored" : " Deleted") + " Successfully";
      //  $('#showMsgModel').modal('show');
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
 
  
  $scope.publish = function (data, unpublish) {
    var action = unpublish == true ? "unpublish" : "publish";
    $scope.setActivity(data.uuid, data.version, $scope.select, action);
    var uuid = data.id;
    $scope.selectuuid = uuid;
    $scope.publishModalMsg = unpublish ? 'Unpublish' : 'Publish';
    $scope.onSuccessPublish = function (response) {
      // $rootScope.refreshSearchResults();
      //$scope.originalData.splice($scope.originalData.indexOf(data),1);
      data.published = unpublish ? 'N' : 'Y';
      $scope.publishmessage = $scope.caption + (unpublish ? " Unpublished" : " Published") + " Successfully";
    }

  $scope.okpublished = function () {
    $('#publishedConfModal').modal('hide');
    CommonService[unpublish ? 'unpublish' : 'publish']($scope.selectuuid, $scope.select).then(function (response) {
    $scope.onSuccessPublish(response.data);
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.publishmessage//"Dashboard Deleted Successfully"
      $scope.$emit('notify', notify);
    });
  }
  $('#publishedConfModal').modal({
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
      //$('#showMsgModel').modal('show');
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
      // CommonService.getBaseEntityByCriteria($scope.select, '', '', '', '', '', '').then(function(response) {
      //   onSuccess(response.data)
      // });
      var onSuccess = function (response) {
        // $scope.gridOptions.data = response;
        // $rootScope.refreshSearchResults();

      }
      // $scope.originalData.push(response);
      $scope.originalData.splice(0, 0, response);
      // $scope.getResults($scope.originalData);
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
  $scope.getExecParamsSet = function () {
    $scope.paramtablecol = null
    $scope.paramtable = null;
    $scope.isTabelShow = false;
    CommonService.getParamSetByType($scope.select, $scope.exeDetail.uuid, $scope.exeDetail.version).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
      $('#responsive').modal({
        backdrop: 'static',
        keyboard: false
      });
      $scope.allparamset = response;
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
    var ruleJson = {}
    ruleJson.uuid = data.uuid;
    ruleJson.version = data.version;
    $scope.exeDetail = ruleJson

  } //End excutionDag

  $scope.ok = function () {
    
    $('#DagConfExModal').modal('hide');
   
    if ($scope.select == 'rule' || $scope.select == 'train') {
      $scope.getExecParamsSet();
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

  $scope.getDetailForUpload = function (data) {
    $scope.setActivity(data.uuid, data.version, $scope.select, "uplode");
    var uuid = data.uuid
    $scope.uploaaduuid = data.uuid
    var version = data.version
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

  $scope.uploadFile = function () {
    //var file = $scope.myFile;
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
    CommonService.uploadFile($scope.uploaaduuid, fd, "datapod").then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
    var onSuccess = function (response) {
      //$('#fileupload').modal('hide')
      $scope.executionmsg = "Data Uploaded Successfully"
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.executionmsg//"Dashboard Deleted Successfully"
      $scope.$emit('notify', notify);

    }
    var onError = function (response) {
    	$('#fileupload').modal('hide')
    }
    // CommonService.SaveFile(file.name,fd,"datapod").then(function(response){onSuccess(response.data)});
    // var onSuccess=function(response){
    //   $('#fileupload').modal('hide');
    // 	CommonService.getRegisterFile(response).then(function(response){onSuccessGetRegisterFile(response.data)});
    // 	var onSuccessGetRegisterFile=function(response){
    // 	$scope.executionmsg="Data Uploaded Successfully"
    // 		// $('#executionsubmit').modal({
    // 		// 	backdrop: 'static',
    // 		// 	keyboard: false
    // 		// });
    //     notify.type='success',
    // 		 notify.title= 'Success',
    //     notify.content=$scope.executionmsg//"Dashboard Deleted Successfully"
    //     $scope.$emit('notify', notify);
    // 	}
    // }
  }
  // $scope.selectPage = function(pageNo) {
  //   $scope.pagination.currentPage = pageNo;
  // };
  // $scope.onPerPageChange = function() {
  //     $scope.pagination.currentPage = 1;
  //   $scope.getResults($scope.originalData)
  // }
  // $scope.pageChanged = function() {
  //   $scope.getResults($scope.originalData)
  // };
  // $scope.getResults = function(params) {
  //   $scope.pagination.totalItems=params.length;
  //   if($scope.pagination.totalItems >0){
  //   $scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize))+1);
  //   }
  //   else{
  //     $scope.pagination.to=0;
  //   }
  //   if ($scope.pagination.totalItems < ($scope.pagination.pageSize*$scope.pagination.currentPage)) {
  //     $scope.pagination.from = $scope.pagination.totalItems;
  //   } else {
  //     $scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
  //   }
  //   var limit = ($scope.pagination.pageSize*$scope.pagination.currentPage);
  //   var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
  //    $scope.gridOptions.data=params.slice(offset,limit);
  // }

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
