BatchModule = angular.module('BatchModule');

BatchModule.controller('DetailBatchController', function($state, $timeout, $filter, $stateParams, $rootScope, $scope, BatchService,privilegeSvc,dagMetaDataService,CommonService,CF_META_TYPES) {
  
  $scope.select = 'batch';
  if($stateParams.mode =='true'){
	  $scope.isEdit=false;
	  $scope.isversionEnable=false;
    $scope.isAdd=false;
    var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if($stateParams.mode =='false'){
	  $scope.isEdit=true;
	  $scope.isversionEnable=true;
    $scope.isAdd=false;
    $scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else{
	$scope.isAdd=true;
	}
  $scope.showForm = true;
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.mode = " ";
  $scope.batch = {};
  $scope.batch.versions = []
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges[CF_META_TYPES.batch] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges[CF_META_TYPES.batch] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag=response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
    $scope.getLovByType();
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs[CF_META_TYPES.batch].listState
      $scope.statedetail.params = {}
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false;
  
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].detailState, {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go(dagMetaDataService.elementDefs['batch'].detailState, {
        id: uuid,
        version: version,
        mode:'true'
      });
   }
  }
 
  $scope.getAllLatest=function(type){
    BatchService.getAllLatest(type).then(function(response) {onSuccess(response.data)});
    var onSuccess = function(response) {
      var metaArray = [];
      for (var i = 0; i < response.data.length; i++) {
        var metajson = {};
        metajson.uuid = response.data[i].uuid;
        metajson.type =type;
        metajson.id = response.data[i].uuid ;
        metajson.name = response.data[i].name;
        metajson.version = response.data[i].version;
        metaArray[i] = metajson;
      }
      $scope.allMeta = metaArray;
    }
  }
 
  $scope.getAllLatest("dag")

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    BatchService.getAllVersionByUuid($stateParams.id, CF_META_TYPES.batch).then(function(response) {onGetAllVersionByUuid(response.data)});
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var BatchVersion = {};
        BatchVersion.version = response[i].version;
        $scope.batch.versions[i] = BatchVersion;
      }
    }
    BatchService.getOneByUuidAndVersion($stateParams.id,$stateParams.version, CF_META_TYPES.batch).then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.batchDetail = response;
      $scope.tags = response.tags
      $scope.checkboxModelparallel = response.inParallel;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagArray = [];
      for (var i = 0; i < response.metaList.length; i++) {
        var metaTags = {};
        metaTags.uuid = response.metaList[i].ref.uuid;
        metaTags.type = response.metaList[i].ref.type;
        metaTags.name = response.metaList[i].ref.name;
        metaTags.id = response.metaList[i].ref.uuid;
        metaTags.version = response.metaList[i].ref.version;
        metaTagArray[i] = metaTags;
      }
      $scope.metaTags = metaTagArray
    }
  }

  $scope.selectVersion = function() {
    $scope.myform.$dirty = false;
    BatchService.getOneByUuidAndVersion($scope.batch.defaultVersion.uuid, $scope.batch.defaultVersion.version, CF_META_TYPES.batch).then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.batchDetail = response;
      $scope.tags = response.tags
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.batch.defaultVersion = defaultversion;
      var metaTagsArray = [];
      for (var i = 0; i < response.metaList.length; i++) {
        var metaTags = {};
        metaTags.uuid = response.metaList[i].ref.uuid;
        metaTags.type = response.metaList[i].ref.type;
        metaTags.name = response.metaList[i].ref.name;
        metaTags.id = response.metaList[i].ref.uuid;
        metaTags.version = response.metaList[i].ref.version;
        metaTagsArray[i] = metaTags;
      }
      $scope.metaTags = metaTagsArray
    }
  }

  $scope.loadMeta = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allMeta, query);
    });
  };

  $scope.oksave = function() {
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go(dagMetaDataService.elementDefs[CF_META_TYPES.batch].listState);
      }, 2000);
    }
  }

  $scope.submit = function() {
    var upd_tag="N"
    $scope.isSubmitProgess = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var batchJson = {}
    batchJson.uuid = $scope.batchDetail.uuid;
    batchJson.name = $scope.batchDetail.name;
    batchJson.desc = $scope.batchDetail.desc;
    batchJson.active = $scope.batchDetail.active;
    batchJson.published = $scope.batchDetail.published;
    batchJson.inParallel= $scope.batchDetail.inParallel;

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
    batchJson.tags = tagArray;
    var metaInfoArray = [];
    for (var i = 0; i < $scope.metaTags.length; i++) {
      var metaInfo = {}
      var ref = {};
      ref.type = $scope.metaTags[i].type
      ref.uuid = $scope.metaTags[i].uuid;
      metaInfo.ref = ref;
      metaInfoArray[i] = metaInfo;
    }
    
    batchJson.metaList = metaInfoArray;
    console.log(JSON.stringify(batchJson))
    BatchService.submit(batchJson, CF_META_TYPES.batch,upd_tag).then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
     
      if (options.execution == "YES") {
        BatchService.getOneById(response.data, CF_META_TYPES.batch).then(function(response) {onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          BatchService.execute(result.data.uuid,result.data.version).then(function(response) { onSuccess(response.data)});
          var onSuccess = function(response) {
            console.log(JSON.stringify(response))
            $scope.isSubmitProgess = false;
            $scope.saveMessage = "Batch Saved and Submitted Successfully"
            notify.type='success',
            notify.title= 'Success',
            notify.content=$scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.oksave();
          }
        }
      } //End If
      else {
        $scope.isSubmitProgess = false;
        $scope.saveMessage = "Batch Saved Successfully"
        notify.title= 'Success',
        notify.content=$scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.oksave();
      } //End else
    } //End Submit Api Function
    var onError = function(response) {
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function
});


BatchModule.controller('ResultBatchController', function( $location,$http,uiGridConstants,$state,$timeout, $filter, $stateParams, $rootScope, $scope, BatchService,privilegeSvc,dagMetaDataService,CommonService,CF_META_TYPES,FileSaver,Blob) {
  
  $scope.autoRefreshCounter=05;
  $scope.autoRefreshResult=false;
  $scope.path = dagMetaDataService.statusDefs
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
 
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

  $scope.gridOptions ={
    // paginationPageSizes: [10, 25, 50, 75],
    // paginationPageSize: 10,
    // enableFiltering: true,
    enableGridMenu: true,
    rowHeight: 40,
    exporterSuppressCtiolumns: [ 'action' ],
    exporterMenuPdf: true,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    columnDefs: [
      {
        displayName: 'UUID',
        name: 'uuid',
        visible: false,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Name',
        name: 'name',
        minWidth: 220,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Version',
        name: 'version',
        visible: false,
        maxWidth:110,
        cellClass: 'text-center',
        headerCellClass: 'text-center',
       
      },
      {
        displayName: 'Created By',
        name: 'createdBy.ref.name',
        visible: false,
        cellClass: 'text-center',
        maxWidth:100,
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Created On',
        visible: false,
        name: 'createdOn',
        minWidth: 160,
        cellClass: 'text-center',
        headerCellClass: 'text-center',

      }
    ]
  };
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'Start Time',
      name: 'startTime',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 190,
      sort: {
        direction: uiGridConstants.ASC,
       // priority: 0,
      },
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.startTime}}</div></div>'
    }
  )
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'End Time',
      name: 'endtime',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 190,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.endTime}}</div></div>'
    }
  )
  $scope.gridOptions .columnDefs.push(
    {
      displayName: 'Duration',
      name: 'duration',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 110,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.duration}}</div></div>'
    }
  )
  $scope.gridOptions.columnDefs.push(
{
      displayName: 'Status',
      name: 'status',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 100,
      cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm label-success" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{row.entity.status}}</div></div>'
    }
  );
  $scope.gridOptions.columnDefs.push({
    displayName: 'Action',
    name: 'action',
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    maxWidth: 100,
    cellTemplate: [
      '<div class="ui-grid-cell-contents">',
      '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
      '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
      '    <i class="fa fa-angle-down"></i></button>',
      '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
      '       <li><a ng-disabled="row.entity.type.toLowerCase().indexOf(\'batchexec\')!=-1?[\'Completed\',\'In Progress\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'trainexec\')!=-1?[\'Completed\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'dagexec\')!=-1?[\'Completed\',\'Not Started\',\'Terminating\',\'Failed\',\'In Progress\',\'Killed\'].indexOf(row.entity.status)==-1:row.entity.type.toLowerCase().indexOf(\'group\')==-1?[\'Completed\',\'Killed\'].indexOf(row.entity.status)==-1:[\'Completed\',\'In Progress\',\'Killed\',\'Failed\',\'Terminating\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.action(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
      '       <li><a ng-disabled="[\'In Progress\',\'Resume\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.setStatus(row.entity,\'Killed\')"><i class="fa fa-times" aria-hidden="true"></i> Kill </a></li>',
      '       <li><a ng-disabled="[\'Killed\',\'Failed\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.restartExec(row.entity)"><i class="fa fa-repeat" aria-hidden="true"></i> Restart </a></li>',
      '    </ul>',
      '  </div>',
      '</div>'
    ].join('')
  });

  $scope.gridOptions.data=[];
  $scope.gridOptions.onRegisterApi = function (gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  $scope.getExecListByBatchExec=function(){
    BatchService.getExecListByBatchExec($stateParams.id,$stateParams.version,CF_META_TYPES.batchexec).then(function (response) { onSuccessGetExecListByBatchExec(response.data) });
    var onSuccessGetExecListByBatchExec = function (response) {
      $scope.gridOptions.data=response;
      $scope.originalData=response;
    }
  }
  $scope.getExecListByBatchExec();
  $scope.refreshData = function () {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
	};

  $scope.refresh=function(){
    $scope.getExecListByBatchExec();
  }
  $scope.action = function (data, mode, privilege) {
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['batchexec'].resultState;
    $rootScope.previousState.params = {};
    $rootScope.previousState.params.id = $stateParams.id;
    $rootScope.previousState.params.version = $stateParams.version;
    $rootScope.previousState.params.mode = true;
    var stateName = dagMetaDataService.elementDefs[data.type.toLowerCase()].resultState;
    if (stateName)
      $state.go(stateName, {
        id: data.uuid,
        version: data.version,
        type:data.type.toLowerCase(),
        returnBack : true,
        mode: mode == 'view' ? true : false
      });
  }
  
  $scope.close=function(){
    var stateName ="batchexeclist"; 
    $state.go(stateName);
  }

  $scope.getDetail = function (data) {
    $scope.obj = {};
    $scope.obj=data
    $scope.msg = "Export"
    $scope.type= dagMetaDataService.elementDefs[data.type.toLowerCase().split("exec")[0]].caption
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.submitOk=function(msg){
   if(msg =="Export"){
    $scope.okExport();
   }
   if(msg =="Killed"){
    $scope.okKill();
   }
   if(msg=="Restart"){
    $scope.okRestart();
   }
  }

  $scope.okExport = function () {
    $('#confModal').modal('hide');
    CommonService.getLatestByUuid($scope.obj.uuid,$scope.obj.type).then(function (response) {
      onSuccessGetUuid(response.data)
    });
    var onSuccessGetUuid = function (response) {
      var jsonobj = angular.toJson(response, true);
      var data = new Blob([jsonobj], {
        type: 'application/json;charset=utf-8'
      });
      FileSaver.saveAs(data, response.name + '.json');
      $scope.message = "Batch Exec Downloaded Successfully";
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }

  $scope.setStatus = function (row, status) {
    $scope.obj=row
    $scope.msg = "Killed"
    $scope.type= dagMetaDataService.elementDefs[data.type.toLowerCase().split("exec")[0]].caption
    $scope.obj.setStatus=status
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.okKill = function () {
    var api = false;
    switch ($scope.obj.type.toLowerCase()) {
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
      case 'batchexec':
        api = 'batch';
        break;
    }
    if (!api) {
      return
    }
    $('#confModal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content ="Pipeline Killed Successfully"
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.put(url + '' + api + '/setStatus?uuid=' + $scope.obj.uuid + '&version=' +  $scope.obj.version + '&type='+$scope.obj.type+'&status=' +  $scope.obj.setStatus).then(function (response) {
      console.log(response);
    });
  }

  $scope.restartExec = function (row, status) {
    $scope.obj=row;
    $scope.msg = "Restart"
    $scope.type= dagMetaDataService.elementDefs[$scope.obj.type.toLowerCase().split("exec")[0]].caption
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }


   $scope.okRestart=function(){
    var api = false;
    switch ($scope.obj.type.toLowerCase()) {
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
      case 'batchExec':
        api = 'batch';
        break;
    }
    if (!api) {
      return
    }
    $('#confModal').modal('hide');
    notify.type = 'success',
    notify.title = 'Success',
    notify.content = "Pipeline Restarted Successfully";
    $scope.$emit('notify', notify);

    var url = $location.absUrl().split("app")[0];
    $http.post(url + '' + api + '/restart?uuid=' + $scope.obj.uuid + '&version=' + $scope.obj.version + '&type='+$scope.obj.type+'&action=execute').then(function (response) {
      //console.log(response);
    });
   }
  var myVar;
  $scope.autoRefreshOnChange=function () {
    if($scope.autorefresh){
        myVar = setInterval(function(){
            $scope.getExecListByBatchExec();
        }, $scope.autoRefreshCounter+"000");
    }
    else{
        clearInterval(myVar);
    }
  }

  $scope.$on('$destroy', function() {
  // Make sure that the interval is destroyed too
      clearInterval(myVar);
      $scope.gridOptions=null;
  });
  
});

