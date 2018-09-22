AdminModule = angular.module('AdminModule');

AdminModule.controller('MigrationAssistController', function($location,$state,$http, uiGridConstants, $stateParams, $rootScope, $scope, MigrationAssistServices, CommonService, $timeout, $filter, dagMetaDataService, FileSaver,Blob,privilegeSvc) {
  $scope.isExec=false;
  $scope.isJobExec=false;
  $scope.handleGroup = -1;

  $scope.select=function (index) {

    if(index==1){
      $scope.type="import";
      $scope.route=dagMetaDataService.elementDefs["import"].detailState;
      $scope.pageheading="Import";
    }
    else {
      $scope.type="export";
      $scope.route=dagMetaDataService.elementDefs["export"].detailState;
      $scope.pageheading="Export";
    }
  }
  if(typeof $stateParams.type !="undefined"){
    if($stateParams.type=='export'){
      $scope.activeForm=0;
    }
    else{
      $scope.activeForm=1;
    }
  }
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };
  $scope.gridOptions = {
    paginationPageSizes: null,
    enableGridMenu: true,
    rowHeight: 40,
    columnDefs: [{
        displayName: 'UUID',
        name: 'uuid',
        minWidth: 250,
        visible: false,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Name',
        name: 'name',
        minWidth: 250,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Version',
        name: 'version',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        sort: {
          direction: uiGridConstants.DESC,
          // priority: 0,
        },
      },
      {
        displayName: 'Created By',
        name: 'createdBy.ref.name',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Created On',
        name: 'createdOn',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },

    ]
  };
  $scope.gridOptionsImport = {
    paginationPageSizes: null,
    enableGridMenu: true,
    rowHeight: 40,
    columnDefs: [{
        displayName: 'UUID',
        name: 'uuid',
        minWidth: 250,
        visible: false,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Name',
        name: 'name',
        minWidth: 250,
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Version',
        name: 'version',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        sort: {
          direction: uiGridConstants.DESC,
          // priority: 0,
        },
      },
      {
        displayName: 'Created By',
        name: 'createdBy.ref.name',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Created On',
        name: 'createdOn',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },

    ]
  };
  $scope.getExport = function(response) {
    $scope.gridOptions.data = response.data;
    $scope.originalDataExport=response.data
  }
  $scope.getImport = function(response) {
    $scope.gridOptionsImport.data = response.data;
    $scope.originalDataImport=response.data
  }
  $scope.gridOptionsImport.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRowsImport = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  $scope.refreshDataImport = function(searchtext) {
    $scope.gridOptionsImport.data = $filter('filter')($scope.originalDataImport, searchtext, undefined);
  };
  $scope.refreshDataExport = function(searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalDataExport, searchtext, undefined);
  };


  $scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRowsExport = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  $scope.getGridStyle = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRowsExport && $scope.filteredRowsExport.length > 0) {
      style['height'] = (($scope.filteredRowsExport.length < 10 ? $scope.filteredRowsExport.length * 50 : 400) + 50) + 'px';
    } else {
      style['height'] = "100px";
    }
    return style;
  }
  $scope.getGridStyleImport = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRowsImport && $scope.filteredRowsImport.length > 0) {
      style['height'] = (($scope.filteredRowsImport.length < 10 ? $scope.filteredRowsImport.length * 50 : 400) + 50) + 'px';
    } else {
      style['height'] = "100px";
    }
    return style;
  }

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
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.exportAction(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.getDetail(row.entity,\'export\')"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
      '    <li ng-hide="grid.appScope.activeForm!=\'0\'" ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.downloadFile(row.entity)"><i class="fa fa-file-archive-o" aria-hidden="true"></i>  Download Archive</a></li>',
      '    </ul>',
      '  </div>',
      '</div>'
    ].join('')

  })
  $scope.gridOptionsImport.columnDefs.push({
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
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.importAction(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.getDetail(row.entity,\'import\')"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
      // '    <li ng-hide="grid.appScope.activeForm!=\'0\'" ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.downloadFile(row.entity)"><i class="fa fa-file-archive-o" aria-hidden="true"></i>  Download Archive</a></li>',
      '    </ul>',
      '  </div>',
      '</div>'
    ].join('')

  })
  $scope.exportAction=function (data) {
    var stateName = dagMetaDataService.elementDefs["export"].detailState;
    if (stateName)
      $state.go(stateName,{
        id: data.uuid,
        version: data.version,
        mode:true
      });
  }
  $scope.importAction=function (data) {
    var stateName = dagMetaDataService.elementDefs["import"].detailState;
    if (stateName)
      $state.go(stateName,{
        id: data.uuid,
        version: data.version,
        mode:true
      });
  }
  $scope.getDetail=function(data,type){
    alert(type)
    $scope.type=type
    var uuid = data.uuid;
    $scope.selectuuid=uuid
    $('#filemodal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  $scope.okFile = function() {
    $('#filemodal').modal('hide');
    CommonService.getLatestByUuid($scope.selectuuid,$scope.type).then(function(response) {
      onSuccessGetUuid(response.data)
    });
    var onSuccessGetUuid = function(response) {
      var jsonobj = angular.toJson(response, true);
      var data = new Blob([jsonobj], {
        type: 'application/json;charset=utf-8'
      });
      FileSaver.saveAs(data, response.name + '.json');
      $scope.message = $scope.pageheading+" Downloaded Successfully";
      notify.type='success',
      notify.title= 'Success',
      notify.content=$scope.message
      $scope.$emit('notify', notify);
    }
  }

  $scope.downloadFile = function(data) {
    var uuid = data.uuid
    var url=$location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url: url+'admin/export/download?uuid=' + uuid,
      responseType: 'arraybuffer'
    }).success(function(data, status, headers) {
      headers = headers();

      var filename = headers['x-filename'];
      var contentType = headers['content-type'];

      var linkElement = document.createElement('a');
      try {
        var blob = new Blob([data], {
          type: contentType
        });
        var url = window.URL.createObjectURL(blob);

        linkElement.setAttribute('href', url);
        linkElement.setAttribute("download", uuid+".zip");

        var clickEvent = new MouseEvent("click", {
          "view": window,
          "bubbles": true,
          "cancelable": false
        });
        linkElement.dispatchEvent(clickEvent);
      } catch (ex) {
        console.log(ex);
      }
    }).error(function(data) {
      console.log(data);
    });
  };
})
AdminModule.controller('DetailExportController',function($state,$stateParams,$rootScope,$scope,MigrationAssistServices,CommonService,$timeout,$filter,dagMetaDataService,privilegeSvc) {
  $rootScope.isCommentVeiwPrivlage=true;
  $scope.showExport=true;
  $scope.showgraphdiv=false;
  $scope.isDependencyShow=false;
  $scope.isSubmitExportEnable=true;
  if($stateParams.mode=="true"){
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
    });  
  }
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
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
  /*Start showExportPage*/
  $scope.showPage=function(){
    $scope.showExport=true;
    $scope.showgraph=false
    $scope.graphDataStatus=false;
    $scope.showgraphdiv=false
  }/*End showExportPage*/

  $scope.showGraph=function(){
    $scope.showExport=false;
    $scope.showgraph=false
    $scope.graphDataStatus=true
    $scope.showgraphdiv=true;
  }/*End ShowDatapodGraph*/

  if(typeof $stateParams.id != "undefined"){
    $scope.mode=$stateParams.mode
    $scope.isDependencyShow=true;
    $scope.isDisabled=true;
    CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"export").then(function(response){onGetOneByUuidAndVersion(response.data)});
    var onGetOneByUuidAndVersion =function(response){
      $scope.exportdata=response
      var metaInfoArray=[]
      for(var i=0;i<response.metaInfo.length;i++){
        var metainfo={};
        metainfo.id=response.metaInfo[i].ref.uuid;
        metainfo.name=response.metaInfo[i].ref.type+"-"+response.metaInfo[i].ref.name;
        metaInfoArray[i]=metainfo;
      }
      $scope.metaNameTags=metaInfoArray;
      var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
    }
  }
  else{
    MigrationAssistServices.getAll().then(function(response){onSuccessGetAll(response.data)},function(response) {onError(response.data)});
    var onSuccessGetAll = function(response) {
      $scope.allmetadata = response
      console.log($scope.allmetadata)
    }
    var onError = function(response) {
      $scope.isDataInpogress = true;
      $scope.isDataError = true;
      $scope.msgclass = "errorMsg";
      $scope.datamessage = "Some Error Occurred";
      $scope.spinner = false;
    }
  }
  // $scope.getAllNames = function() {
  //   $scope.metaNameTags = null;
  //   $scope.isDisabled = true
  //   if($scope.metatype.name != null) {
  //     $scope.spinner = true;
  //     CommonService.getAllLatest($scope.metatype.name).then(function(response){onSuccessgetAllLatest(response.data)},function(response){onError(response.data)});
  //     var onSuccessgetAllLatest = function(response){
  //       $scope.allmetatypename = response
  //       $scope.isDisabled = false;
  //       $scope.isSubmitExportEnable = false;
  //     }
  //     var onError = function(response) {
  //       $scope.isDataInpogress = true;
  //       $scope.isDataError = true;
  //       $scope.msgclass = "errorMsg";
  //       $scope.datamessage = "Some Error Occurred";
  //       $scope.spinner = false;
  //     }
  //   }
  // }

  $scope.getAllLetestByMetaList=function(type){
    MigrationAssistServices.getAllByMetaList(type).then(function(response){onSuccessGetAll(response.data)},function(response) {onError(response.data)});
    var onSuccessGetAll = function(response) {
      $scope.allmetatypename=response;
      $scope.isSubmitExportEnable = false;
      console.log(response)
    }
  }
  $scope.loadAllMetaName = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allmetatypename, query);
    });
  };
  $scope.loadAllMetaType = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allmetadata, query);
    });
  };
  $scope.onAddMetaType=function(event){
    $timeout(function() {
    console.log($scope.metatype)
    var type=[];
    for(var i=0;i<$scope.metatype.length;i++){
      type[i]=$scope.metatype[i].text;
    }
    console.log(type);
    $scope.getAllLetestByMetaList(type);
   });
  }

  $scope.onRemoveMetaType=function(event){
    $timeout(function() {
    console.log($scope.metatype)
    var type=[];
    for(var i=0;i<$scope.metatype.length;i++){
      type[i]=$scope.metatype[i].text;
    }
    console.log(type);
    $scope.getAllLetestByMetaList(type);
    $scope.removeTagMetaInfo(type);
    if(type.length >0){
      
    }
   });
  }

  $scope.removeTagMetaInfo=function(type){
    var newDataList=[];
    angular.forEach($scope.metaNameTags, function(item){
      if(type.indexOf(item.type) !=-1){
          newDataList.push(item);
      }
    });
    $scope.metaNameTags =newDataList;
  }

  $scope.submitExport = function() {
    var upd_tag="N"
    var exportJson = {};
    $scope.isSubmitExportEnable = true;
    $scope.dataLoading=true;
    exportJson.includeDep=$scope.exportdata.includeDep;
    exportJson.name=$scope.exportdata.name;
    exportJson.desc=$scope.exportdata.desc;
    var metainfoarray = [];
    for (var i = 0; i < $scope.metaNameTags.length; i++) {
      var metainfo = {};
      var ref = {}
      ref.type = $scope.metaNameTags[i].type;
      ref.uuid = $scope.metaNameTags[i].uuid;
      ref.version = $scope.metaNameTags[i].version;
      metainfo.ref = ref;
      metainfoarray[i] = metainfo;
    }
    exportJson.metaInfo = metainfoarray;
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
		exportJson.tags = tagArray;
    console.log(JSON.stringify(exportJson));
    MigrationAssistServices.exportSubmit(exportJson, "export",upd_tag).then(function(response) {
      onSuccessSubmit(response.data)
    }, function(response) {
      onError(response.data)
    });
    var onSuccessSubmit = function(response) {
      $scope.dataLoading = false;
      $scope.executionmsg = "Meta Exported Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg
      $scope.$emit('notify', notify);
      setTimeout(function(){  $state.go('migrationassist',{'type':'export'});},2000);
    }
    var onError = function(response) {
      $scope.dataLoading = false;
      notify.type = 'error',
        notify.title = 'Error',
        notify.content = "Some Error Occurred"
      //$scope.$emit('notify', notify);

    }
  }

  $scope.onExportSubmit = function() {
    $scope.isSubmitExportEnable = true;
    if (typeof $scope.metaNameTags !="undefined") {
      $scope.isSubmitExportEnable = false;
    }
  }
});


AdminModule.controller('DetailImportController',function($state,$stateParams,$rootScope,$scope,MigrationAssistServices,CommonService,$timeout,$filter,dagMetaDataService,privilegeSvc) {
  $rootScope.isCommentVeiwPrivlage=true;
  $scope.showImport=true;
  $scope.showgraphdiv=false;
  $scope.isDependencyShow=false;
  $scope.isSubmitEnable=false
  $scope.myFile;
  $scope.isValidate=true;

  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };
  if($stateParams.mode=="true"){
    $scope.selectFeature=false;
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
  }
  else{
    $scope.selectFeature=true;

  }
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.gridOptionsDatapod = {
    enableGridMenu: true,
    rowHeight: 40,
    enableRowSelection:true,
    enableSelectAll:$scope.selectFeature,
    enableFullRowSelection:$scope.selectFeature==true?false:true,
  };

  $scope.gridOptionsDatapod.columnDefs = [
  {
    name: 'uuid',
    displayName: 'uuid',
    visible:false,
    headerCellClass: 'text-center'
  },
  ,{
    name: 'type',
    displayName: 'Type',
    headerCellClass: 'text-center'
  },
  {
    name: 'name',
    displayName: 'Name',
    headerCellClass: 'text-center'
  },
  {
    name: 'version',
    cellClass:'text-center',
    displayName:'Version',
    headerCellClass: 'text-center'
  },

  ];
  if($stateParams.mode!="true"){
    $scope.gridOptionsDatapod.columnDefs[5]={
      name: 'status',
      displayName: 'Validate Status',
      headerCellClass: 'text-center',
      cellTemplate:'<div class="ui-grid-cell-contents text-center" ng-show="row.entity.status"><i style="color:{{row.entity.status==\'true\'?\'green\':row.entity.status==\'false\'?\'red\':\'blue\'}};font-size:16px;" class="{{row.entity.status!=\'true\' && row.entity.status!=\'false\'  ? \'fa fa-minus-circle\' : row.entity.status!=\'false\' ? \'fa fa-check-circle\' : \'fa fa-times-circle\'}}" aria-hidden="true"></i></div>'
    }
  }

  $scope.gridOptionsDatapod.onRegisterApi = function(gridApi){
  $scope.gridApi = gridApi;
  gridApi.selection.on.rowSelectionChanged($scope,function(row){
        $scope.selectButtonClick(row.entity);
    });

    gridApi.selection.on.rowSelectionChangedBatch($scope,function(row){
    	$scope.selectButtonClick(row[0].entity);
    });

  $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.getGridStyle = function() {
  var style = {
    'margin-top': '10px',
    'margin-bottom': '10px',
  }
  if ($scope.filteredRows && $scope.filteredRows.length >0) {
    style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 50 : 400) + 50) + 'px';
  }
  else{
    style['height']="100px";
  }
  return style;
  }
  
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
  /*Start showImportPage*/
  $scope.showPage=function(){
    $scope.showImport=true;
    $scope.showgraph=false
    $scope.graphDataStatus=false;
    $scope.showgraphdiv=false
  }/*End showImportPage*/

  $scope.showGraph=function(){
    $scope.showImport=false;
    $scope.showgraph=false
    $scope.graphDataStatus=true
    $scope.showgraphdiv=true;
  }/*End ShowDatapodGraph*/

  if(typeof $stateParams.id != "undefined"){
    $scope.mode=$stateParams.mode
    $scope.isDependencyShow=true;
    CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"import").then(function(response){onGetOneByUuidAndVersion(response.data)});
    var onGetOneByUuidAndVersion =function(response){
      $scope.isSubmitEnable=true
      $scope.importdata=response;
      var metainfoarray = [];
      for (var i = 0; i < response.metaInfo.length; i++) {
        var metainfo = {};
        metainfo.type = response.metaInfo[i].ref.type
        metainfo.uuid = response.metaInfo[i].ref.uuid;
        metainfo.name = response.metaInfo[i].ref.name;
        metainfo.version = response.metaInfo[i].ref.version;
        metainfo.status = response.metaInfo[i].status;
        metainfoarray[i] = metainfo;
      }
      $scope.gridOptionsDatapod.data=metainfoarray;
      var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
    }
  }

  $scope.exportUpload = function(data) {
    $scope.data = data
    $scope.isSubmitImportEnable = false;
    var metaInfoArray = [];
    $scope.allExportMetaInfo = null;
    for (var i = 0; i < data.data.metaInfo.length; i++) {
      var metaInfo = {}
      metaInfo.id = i;
      metaInfo.uuid = data.data.metaInfo[i].ref.uuid;
      metaInfo.version = data.data.metaInfo[i].ref.version;
      metaInfo.name = data.data.metaInfo[i].ref.name;
      metaInfo.type = data.data.metaInfo[i].ref.type;
      metaInfo.status=" "
      metaInfoArray[i] = metaInfo;
    }
    $scope.allExportMetaInfo = metaInfoArray;
    $scope.gridOptionsDatapod.data=metaInfoArray;
  }

  $scope.validate=function(){
    var importJson={}
    $scope.isSubmitEnable=false;
    importJson.includeDep = $scope.importdata.includeDep;
    importJson.name = $scope.importdata.name
    var filename=$scope.data.name.split(".zip")[0];
    var metainfoarray = [];
    $scope.importTags=$scope.gridApi.selection.getSelectedRows();
    for (var i = 0; i < $scope.importTags.length; i++) {
      var metainfo = {};
      var ref = {}
      ref.type = $scope.importTags[i].type
      ref.uuid = $scope.importTags[i].uuid;
      ref.name = $scope.importTags[i].name;
      ref.version = $scope.importTags[i].version;
      metainfo.ref = ref;
      metainfoarray[i] = metainfo;
    }
    importJson.metaInfo = metainfoarray;
    console.log(JSON.stringify(importJson));
    MigrationAssistServices.validateDependancy(importJson,filename).then(function(response){onSuccessSubmit(response.data)},function(response){onError(response.data)});
    var onSuccessSubmit = function(response) {

      for(var i=0;i<response.metaInfo.length;i++) {
        var result = $filter('filter')($scope.gridOptionsDatapod.data, {uuid:response.metaInfo[i].ref.uuid})[0];
        $scope.gridOptionsDatapod.data[result.id].status=response.metaInfo[i].status;
        if($scope.gridOptionsDatapod.data[result.id].status =="false"){
          $scope.gridApi.selection.unSelectRow($scope.gridOptionsDatapod.data[result.id]);
            $scope.gridOptionsDatapod.data[result.id].status=response.metaInfo[i].status;
        }
      }
      if($scope.gridApi.selection.getSelectedRows().length>0){
        $scope.isSubmitEnable=true;
      }
    }
    var onError = function(response) {
    }
  }

  $scope.onIncludeDep=function () {
    $scope.isSubmitEnable=false;
  }

  $scope.selectButtonClick=function(row, $event){
    if(typeof $stateParams.id != "undefined"){
      return false;
    }
    $scope.isValidate=true;
    $scope.isSubmitEnable=false;
    $scope.gridOptionsDatapod.data[row.id].status=" "
    if($scope.gridApi.selection.getSelectedRows().length >=1){
      $scope.isValidate=false;
    }

  }

  $scope.submitImport = function() {
    var upd_tag="N"
    var importJson = {};
    $scope.dataLoading=true;
    $scope.isSubmitImportEnable = true;
    $scope.importDataLoading = true;
    importJson.includeDep = $scope.importdata.includeDep;
    importJson.name = $scope.importdata.name;
    var filename=$scope.data.name.split(".zip")[0];
    $scope.importTags=$scope.gridApi.selection.getSelectedRows();
    var metainfoarray = [];
    $scope.importTags=$scope.gridOptionsDatapod.data
    for (var i = 0; i < $scope.importTags.length; i++) {
      var metainfo = {};
      var ref = {}
      ref.type = $scope.importTags[i].type
      ref.uuid = $scope.importTags[i].uuid;
      ref.version = $scope.importTags[i].version;
      metainfo.ref = ref;
      metainfoarray[i] = metainfo;
    }
    importJson.metaInfo = metainfoarray;
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
    importJson.tags = tagArray;
    console.log(JSON.stringify(importJson));
    MigrationAssistServices.importSubmit(importJson,"import",filename,upd_tag).then(function(response) {
      onSuccessSubmit(response.data)
    }, function(response) {
      onError(response.data)
    });
    var onSuccessSubmit = function(response) {
        $scope.dataLoading=false;
      $scope.executionmsg = "Meta Imported Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg //"Dashboard Deleted Successfully"
      $scope.$emit('notify', notify);
      setTimeout(function(){  $state.go('migrationassist',{'type':'import'});},2000);

    }
    var onError = function(response) {
      $scope.dataLoading=false;
    }
  }

});
