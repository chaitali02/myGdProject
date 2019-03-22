
DatavisualizationModule = angular.module('DatavisualizationModule')

DatavisualizationModule.controller('DashboradMenuController2', function ($filter, $rootScope, $scope, $sessionStorage, $state, DahsboardSerivce, CommonService, dagMetaDataService, FileSaver, Blob, privilegeSvc) {
  $scope.isListCard = false;
  $scope.IsVizpodDetailShow = false;
  $scope.optionsort = [
    { "caption": "Name A-Z", name: "name" },
    { "caption": "Name Z-A", name: "-name" },
    { "caption": "Date Asc", name: "createdOn" },
    { "caption": "Date Desc", name: "-createdOn" },
  ]
  $scope.optiondata = { "caption": "Name A-Z", "name": "name" };
  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
   // console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });

  var notify = {
    type: 'info',
    title: 'Info',
    content: 'Dashboard is In-active. Please restore',
    timeout: 3000 //time in ms
  };

  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges["dashboard"] || [];

  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges["dashboard"] || [];

  });

  $scope.pagination = {
    currentPage: 1,
    pageSize: 10,
    paginationPageSizes: [10, 25, 50, 75, 100],
    maxSize: 5,
  }

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
  $scope.gridOptions = angular.copy(dagMetaDataService.gridOptionsDefault);
  if ($scope.gridOptions.columnDefs[0].name != "locked") {
    $scope.gridOptions.columnDefs.splice(0, 0, {
      displayName: 'Locked',
      name: 'locked',
      minWidth: 20,
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      cellTemplate: ['<div class="ui-grid-cell-contents">',
        '<div ng-if="row.entity.locked == \'Y\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Unlock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'UnLock\')"><i  title ="Lock" class="icon-lock" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
        '<div  ng-if="row.entity.locked == \'N\'"><ul style="list-style:none;padding-left:0px"><li ng-disabled="grid.appScope.privileges.indexOf(\'Lock\') == -1" ><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'Lock\')"><i title ="UnLock" class="icon-lock-open" style="color:#a0a0a0;font-size:20px;"></i></a></li></div>',
      ].join('')
    });
  }
  $scope.gridOptions.columnDefs.push({
    displayName: 'Status',
    name: 'active',
    cellClass: 'text-center',
    headerCellClass: 'text-center',
    cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.active == "Y" ? "Active" : "In Active"}}</div>'
  }, {
      displayName: 'Publish',
      name: 'publish',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.published == "Y" ? "Yes" : "No"}}</div>'
    }, {
      displayName: 'Action',
      name: 'action',
      cellClass: 'text-center',
      headerCellClass: 'text-center',
      maxWidth: 110,
      // cellTemplate: [

      //   '<div class="ui-grid-cell-contents"><a class="btn btn-xs btn-primary" name="execbutton"  ng-click="grid.appScope.show_dashboard(row.entity)">View</a></div>',

      // ].join(''),
      cellTemplate: [

        '<div class="ui-grid-cell-contents">',
        '<div class="col-md-12" style="display:inline-flex;;padding-left:0px;padding-right:0px">',
        '  <div class="col-md-10 dropdown" uib-dropdown dropdown-append-to-body>',
        '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
        '    <i class="fa fa-angle-down"></i></button>',
        '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.show_dashboard(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Edit\') != -1 && row.entity.locked ==\'N\'?false:true"><a ng-click="grid.appScope.editDashboard(row.entity)"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Delete\') == -1" ng-if="row.entity.active == \'Y\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Delete\')"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Restore\') == -1" ng-if="row.entity.active == \'N\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Restore\')"><i class="fa fa-retweet" aria-hidden="true"></i>  Restore</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unlock\') == -1" ng-if="row.entity.locked == \'N\'"><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'Lock\')"><i class="icon-lock" aria-hidden="true"></i> Lock</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Lock\') == -1" ng-if="row.entity.locked == \'Y\'"><a ng-click="grid.appScope.lockOrUnLock(row.entity,\'UnLock\')"><i class="icon-lock-open" aria-hidden="true"></i>  Unlock</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Publish\') == -1" ng-if="row.entity.published == \'N\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Publish\')"><i class="fa fa-share-alt" aria-hidden="true"></i>  Publish</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unpublish\') == -1 || row.entity.createdBy.ref.name != grid.appScope.loginUser" ng-if="row.entity.published == \'Y\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Unpublish\')"><i class="fa fa-shield" aria-hidden="true"></i>  Unpublish</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Clone\') == -1"><a ng-click="grid.appScope.createCopy(row.entity)"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>',
        '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.export(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
        '    </ul>',
        '  </div>',
        '</div>'

      ].join('')
    });
  $scope.gridOptions.onRegisterApi = function (gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.updateStats = function () {
    CommonService.getMetaStats("dashboard").then(function (response) {
      if (response.data && response.data.length && response.data.length > 0) {
        $rootScope.metaStats["dashboard"] = response.data[0];
      }
    });
  }
  $scope.updateStats();
  
  $scope.refreshData = function (searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
  }

  $scope.$watchCollection('switchStatus', function () {
    $scope.isListCard = !$scope.isListCard;
  });

  $scope.addMode = function () {

  }
  $scope.selectdashboard = function (response) {
    $scope.selectedmodeldata = true;
    $scope.gridOptions.data = null;
    $scope.gridOptions.data = response.data;
    $scope.originalData = response.data;
  }


  $scope.showIcon = function (index) {
    $scope.alldashboard[index].isIconShow = true;
  }

  $scope.hideIcon = function (index) {
    $scope.alldashboard[index].isIconShow = false;
  }

  $scope.show_dashboard = function (data) {
    if (data.active == 'Y') {
      setTimeout(function () { $state.go("showdashboard", { 'id': data.uuid, 'mode': 'false' }); }, 100);
    }
    else {
      notify.content = "Dashboard is In-active. Please restore"
      notify.type = 'info',
        notify.title = 'Info',
        $scope.$emit('notify', notify);
    }
  }
  $scope.editDashboard = function (data) {
    setTimeout(function () { $state.go("metaListdashboard", { 'id': data.uuid, 'mode': 'false' }); }, 100);
  }

  $scope.createCopy = function (data) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = {};
    $scope.obj.uuid = uuid;
    $scope.obj.version = version;
    $scope.msg = "Clone"
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  $scope.export = function (data) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = {};
    $scope.obj.uuid = uuid;
    $scope.obj.version = version;
    $scope.msg = "Export"
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  $scope.deleteOrRestore = function (data, action) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = data;

    $scope.msg = action;
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.publishOrUnpublish = function (data, action) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = data;
    $scope.msg = action;
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.lockOrUnLock = function (data, action) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = data;
    $scope.msg = action;
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.submitOk = function (action) {
    if (action == "Clone") {
      $scope.okClone();
    }
    else if (action == "Export") {
      $scope.okExport();
    }
    else if (action == "Delete") {
      $scope.okDelete();
    }
    else if (action == "Restore") {
      $scope.okDelete();
    } else if (action == "Publish") {
      $scope.okPublished();
    }
    else if (action == "Unpublish") {
      $scope.okPublished();
    } else if (action == "Lock") {
      $scope.okLocked();
    }
    else if (action == "UnLock") {
      $scope.okLocked();
    }
  }

  $scope.okClone = function () {
    $('#confModal').modal('hide');
    CommonService.getSaveAS($scope.obj.uuid, $scope.obj.version, "dashboard").then(function (response) { onSuccessSaveAs(response.data) });
    var onSuccessSaveAs = function (response) {
      $scope.originalData.splice(0, 0, response);
      $scope.message = "Dashboard Cloned Successfully"
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }


  $scope.okExport = function () {
    $('#confModal').modal('hide');
    CommonService.getLatestByUuid($scope.obj.uuid, "dashboard").then(function (response) {
      onSuccessGetUuid(response.data)
    });
    var onSuccessGetUuid = function (response) {
      var jsonobj = angular.toJson(response, true);
      var data = new Blob([jsonobj], {
        type: 'application/json;charset=utf-8'
      });
      FileSaver.saveAs(data, response.name + '.json');
      $scope.message = "Dashboard Downloaded Successfully";
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }

  $scope.okDelete = function () {
    $('#DeleteConfModal').modal('hide');
    $('#confModal').modal('hide');
    if ($scope.obj.active == 'Y') {
      CommonService.delete($scope.obj.id, 'dashboard').then(function (response) { OnSuccessDelete(response.data) });
      var OnSuccessDelete = function (response) {
        $scope.alldashboard[$scope.obj.index].active = response.active;
        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].active = response.active;
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Deleted Successfully"
        $scope.$emit('notify', notify);
      }
    }
    else {
      CommonService.restore($scope.obj.id, 'dashboard').then(function (response) { OnSuccessDelete(response.data) });
      var OnSuccessDelete = function (response) {
        $scope.alldashboard[$scope.obj.index].active = 'Y'
        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].active = "Y"
          notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Restored Successfully"
        $scope.$emit('notify', notify);
      }
    }

  }
  $scope.metadashboard = function ($event, index, data) {
    $event.stopPropagation();
    $scope.dashboarddatadelete = data;
    $scope.obj = data;
    if (data.active == 'Y') {
      $scope.deletemsg = "Delete Dashboard"
    }
    else {
      $scope.deletemsg = "Restore Dashboard "
    }
    $('#DeleteConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });

  }




  $scope.okPublished = function () {

    $('#confModal').modal('hide');
    if ($scope.obj.published == 'N') {
      CommonService.publish($scope.obj.id, 'dashboard').then(function (response) { OnSuccessPublush(response.data) });
      var OnSuccessPublush = function (response) {
        $scope.alldashboard[$scope.obj.index].published = response.published;
        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].published = response.published;
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Publish Successfully"
        $scope.$emit('notify', notify);
      }
    }
    else {
      CommonService.unpublish($scope.obj.id, 'dashboard').then(function (response) { OnSuccessUnpublush(response.data) });
      var OnSuccessUnpublush = function (response) {
        $scope.alldashboard[$scope.obj.index].published = 'N'
        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].published = "N"
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Unpublish Successfully"
        $scope.$emit('notify', notify);
      }
    }
  }
  $scope.okLocked = function () {
    $('#confModal').modal('hide');
    if ($scope.obj.locked == 'N') {
      CommonService.lock($scope.obj.id, 'dashboard').then(function (response) { OnSuccessLock(response.data) });
      var OnSuccessLock = function (response) {

        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].locked = "Y";
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Lock Successfully"
        $scope.$emit('notify', notify);
      }
    }
    else {
      CommonService.unLock($scope.obj.id, 'dashboard').then(function (response) { OnSuccessUnLock(response.data) });
      var OnSuccessUnLock = function (response) {
        if ($scope.gridOptions.data && $scope.gridOptions.data.length > 0)
          $scope.gridOptions.data[$scope.obj.index].locked = "N"
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = "Dashboard Unpublish Successfully"
        $scope.$emit('notify', notify);
      }
    }
  }


  DahsboardSerivce.getAllLatestCompleteObjects("dashboard").then(function (response) { onSuccessGetAllLatestCompleteObjects(response.data) });
  var onSuccessGetAllLatestCompleteObjects = function (response) {
    if (response.length > 0) {
      var dashbardarray = [];
      var colorarray = ["blue", "green", "purple"];
      var chartclassarray = ["fa fa-bar-chart", "fa fa-area-chart", "fa fa-pie-chart"]
      var count = 0;
      for (var i = 0; i < response.length; i++) {
        var dashbardjson = {};
				/*if(count <=3){
					if(count == 3){
						count=0;
					}
				 dashbardjson.class=colorarray[count];
        // dashbardjson.chartclass=chartclassarray[count]
				 count=count+1;
				}//End If*/
        var randomno = Math.floor((Math.random() * 3) + 0);
        dashbardjson.class = "green";//colorarray[randomno];
        var randomno1 = Math.floor((Math.random() * 3) + 0);
        dashbardjson.tooltip = "Delete";
        dashbardjson.chartclass = chartclassarray[randomno1]
        dashbardjson.color = '#' + Math.random().toString(16).substr(2, 6);
        dashbardjson.isIconShow = false;
        dashbardjson.uuid = response[i].uuid;
        dashbardjson.id = response[i].id;
        dashbardjson.index = i;
        dashbardjson.active = response[i].active;
        if (response[i].name.split('').length > 20) {
          dashbardjson.name = response[i].name.substring(0, 20) + "..";
        }
        else {
          dashbardjson.name = response[i].name;
        }
        dashbardjson.title = response[i].name;
        dashbardjson.desc = response[i].desc;
        var d = response[i].createdOn;
        var dat = d.split(" ");
        dat.splice(dat.length - 2, 1);
        dashbardjson.createdOn = new Date(dat.join().replace(/,/g, " ")).toLocaleDateString('en-US');
        //	dashbardjson.createdOn=new Date(response[i].createdOn.split("IST")[0]+response[i].createdOn.split("IST")[1]).toLocaleDateString('en-US')//response[i].createdOn
        dashbardjson.sectionInfo = response[i].sectionInfo;
        dashbardarray[i] = dashbardjson;
      }//End For
    }//End If
    $scope.alldashboard = dashbardarray
  }//End  onSuccessGetAllLatest

});//End DashboradMenuController



//Start ShowDashboradController
DatavisualizationModule.controller('ShowDashboradController2', function ($location, privilegeSvc, $http, $filter, dagMetaDataService, $window, $timeout, $rootScope, $scope, $state, $stateParams, $q, NgTableParams, $sessionStorage, DahsboardSerivce, CF_DOWNLOAD) {
  $scope.showmap = true;
  $scope.isApplyFilter = true
  $scope.datax = [];
  $scope.datacolumns = [];
  $scope.datapoints = [];
  $scope.datapointsgrid = [];
  $scope.vizpoddetail = []
  $scope.vizpodtypedetail = {};
  $scope.vizpodtypedetail1 = [];
  $scope.tabledata = [];
  var count = 0;
  $scope.showdashboard = true;
  $scope.showgraph = false
  $scope.isDashboardInprogess=false 
  $scope.isDashboardError=false;
  $scope.inprogressdata=true;
  $scope.showgraphdiv = false
  $scope.graphDataStatus = false
  $scope.showtooltip = 'top'
  $scope.showtooltiptitle = "Maximize";
  $scope.chartcolor = ["#73c6b6", "#f8c471", "#d98880", "#7dcea0", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#82e0aa", "#f7dc6f", "#f0b27a", "#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
  $scope.vizpodbody = {};
  $scope.filterListarray = [];
  $scope.hideIcon = true
  var notify = {
    type: 'info',
    title: 'Info',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.sectionRows = [];
  $scope.gridOptions = dagMetaDataService.gridOptionsDefault;
  $scope.download = {};
  $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
  $scope.download.formates = CF_DOWNLOAD.formate;
  $scope.download.selectFormate = CF_DOWNLOAD.formate[0];
  $scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
  $scope.download.limit_to = CF_DOWNLOAD.limit_to;
  // ui grid
  $scope.pagination = {
    currentPage: 1,
    pageSize: 10,
    paginationPageSizes: [10, 25, 50, 75, 100],
    maxSize: 5,
  }
  $scope.gridOptions = {
    rowHeight: 40,
    useExternalPagination: true,
    exporterMenuPdf: true,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: { fontSize: 9 },
    exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
    enableGridMenu: true,
    rowHeight: 40,
    onRegisterApi: function (gridApi) {
      $scope.gridApi = gridApi;
      $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    }
  }

  $scope.filteredRows = [];
  $scope.getGridStyleDetail = function () {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 60) + 'px';
    }
    else {
      style['height'] = "100px";
    }
    return style;
  }
  $scope.privilegesDashboard = privilegeSvc.privileges['dashboard'] || [];
  $scope.isPrivlageDashboard = $scope.privilegesDashboard.indexOf('Edit') == -1;
  $scope.privilegesVizpod = privilegeSvc.privileges['vizpod'] || [];
  $scope.isPrivlageVizpod = $scope.privilegesVizpod.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privilegesDashboard = privilegeSvc.privileges['dashboard'] || [];
    $scope.isPrivlageDashboard = $scope.privilegesDashboard.indexOf('Edit') == -1;
    $scope.privilegesVizpod = privilegeSvc.privileges['vizpod'] || [];
    $scope.isPrivlageVizpod = $scope.privilegesVizpod.indexOf('Edit') == -1;
  });
  $scope.filterSearch = function (s) {
    var data = $filter('filter')($scope.orignalData, s, undefined);
    $scope.getResults(data)
  }

  $window.addEventListener('resize', function (e) {
    $scope.showmap = false
    $timeout(function () {
      $scope.showmap = true;
    }, 100);
  });
  
  $scope.checkIsInrogess=function(){
		if($scope.inprogressdata || $scope.isDashboardInprogess || $scope.isDashboardError){
		return false;
		}
  }
  
  $scope.Preparedatagrid = function (i, j) {
    $scope.sectionRows[i].columns[j].filteredRows = [];
    $scope.sectionRows[i].columns[j].gridOptions = {
      rowHeight: 40,
      enableGridMenu: true,
      useExternalPagination: true,
      exporterMenuPdf: true,
      exporterPdfOrientation: 'landscape',
      exporterPdfPageSize: 'A4',
      exporterPdfDefaultStyle: { fontSize: 9 },
      exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },

    }

    $scope.sectionRows[i].columns[j].gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;
      $scope.sectionRows[i].columns[j].filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };
  }
  $scope.getGridStyle = function (data) {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
      'max-height': '297px'
    }
    // if (data &&  data.length >0) {
    //     style['height'] = (( data.length < 10 ? data.length * 40 : 400) + 40) + 'px';
    //   }
    //  else{
    //     style['height']="100px";
    //   }
    return style;
  }

  $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
   // console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });
  $scope.convertSectionInfo = function (sectionInfo) {
    $scope.sectionRows=[];
    if (!sectionInfo[0].rowNo) {
      var row = 0;
      angular.forEach(sectionInfo, function (val, key) {
        if (key > 0 && key % 2 == 0) {
          row++;
        }
        if ($scope.sectionRows[row])
          $scope.sectionRows[row].columns.push(val);
        else
          $scope.sectionRows[row] = { columns: [val] };

      });

    }
    else {

      angular.forEach(sectionInfo, function (val, key) {
        if ($scope.sectionRows[val.rowNo - 1]) {
          $scope.sectionRows[val.rowNo - 1].columns[val.colNo - 1] = val
        }
        else {
          $scope.sectionRows[val.rowNo - 1] = { columns: [val] }
        }
      })
    }
    //console.log(JSON.stringify($scope.sectionRows));
  }
  $scope.getColWidth = function (row) {
    var count = 0;
    angular.forEach(row.columns, function (val) {
      if (!val.fullWidth)
        count++;
    })
    return (count < 3 ? 12 / (count || 1) : '4')
  }
  $scope.showDashboardGraph = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showdashboard = false;
    $scope.showgraph = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;
    $scope.vizpodtypedetail1 = [];
  }//End showDashboardGraph


  $scope.showDashboardPage = function () {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showdashboard = true;
    $scope.showgraph = false
    $scope.graphDataStatus = false;
    $scope.showgraphdiv = false
    $scope.vizpodtypedetail1 = [];
    //  $scope.callGraph();
  }//End showDashboardPage


  $scope.fullscreen = function () {
    if ($scope.showtooltip == 'top') {
      $scope.showtooltip = 'bottom'
      $scope.showtooltiptitle = "Minimize"
      $scope.hideIcon = false
    }
    else {
      $scope.showtooltip = 'top'
      $scope.showtooltiptitle = "Maximize"
      $scope.hideIcon = true
    }
    $timeout(function () {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }

  $scope.fullScreenVizpod = function (parentIndex, index) {
    if ($scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass == 'fa fa-expand') {
      //$(".fullscreendashobard").addClass("portlet-fullscreen");
      for (var i = 0; i < $scope.sectionRows.length; i++) {
        for (var j = 0; j < $scope.sectionRows[i].columns.length; j++) {
          $scope.sectionRows[i].columns[j].vizpodDetails.show = false;
        }
      }
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.show = true;
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass = 'fa fa-compress';
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.showtooltiptitle = "Minimize"
    }
    else {
      //  $(".fullscreendashobard").removeClass("portlet-fullscreen");
      for (var i = 0; i < $scope.sectionRows.length; i++) {
        for (var j = 0; j < $scope.sectionRows[i].columns.length; j++) {
          $scope.sectionRows[i].columns[j].vizpodDetails.show = true
        }
      }
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass = 'fa fa-expand';
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.showtooltiptitle = "Maximize"

    }
    //$scope.fullscreen();

    $timeout(function () {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }

  $scope.onClickChart = function (parentIndex, index) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.sectionRows[parentIndex].columns[index].isDataGridShow = false;
    $scope.sectionRows[parentIndex].columns[index].isChartShow = true;
  }

  $scope.onClickGrid = function (parentIndex, index) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.sectionRows[parentIndex].columns[index].isChartShow = false;
    $scope.sectionRows[parentIndex].columns[index].isDataGridShow = true;

  }
  $scope.time_format = function (timestamp) {
    //  console.log(timestamp)
    return timestamp.toFixed(2);
  };
  $scope.time_formatY2 = function (timestamp) {
   // console.log(timestamp)
    return "$"
  };


  $scope.refreshDashboard = function (length) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.callGraph();
    $scope.isApplyFilter = true;
    $scope.selectedAttributeValue = []
    for (var i = 0; i < length; i++) {
      var defaultvalue = {}
      defaultvalue.id = null;
      defaultvalue.value = "-select-"
      $scope.selectedAttributeValue[i] = defaultvalue
      $scope.filterAttribureIdValues[i].values.splice(0, 1);
      $scope.filterAttribureIdValues[i].values.splice(0, 0, defaultvalue);
    }


  }

  $scope.onChange = function () {
    $scope.isApplyFilter = false;
  }

  $scope.openFilterPopup = function () {
    if($scope.checkIsInrogess () ==false){
			return false;
    }
    if($scope.dashboarddata.filterInfo !=null && $scope.dashboarddata.filterInfo.length ==0){
      return false
    }
    if ($scope.filterAttribureIdValues == null) {
      $scope.getFilterValue($scope.dashboarddata);
    }
    else{
      $scope.populateFilers();
    }
    $('#attrFilter').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.onClickEditDashboard = function (uuid, lock) {
    if (lock == 'Y' || $scope.isPrivlageDashboard) {
      return false;
    }
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $state.go('metaListdashboard', {
      id: uuid,
      mode: 'false'
    });
  }

  $scope.onClickEditVizpod = function (uuid, version, lock) {
    if (lock == 'Y' || $scope.isPrivlageVizpod) {
      return false;
    }
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $state.go('dvvizpod', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }
  
  $scope.onFilterChange = function (index) {
    // console.log(JSON.stringify($scope.filterAttribureIdValues[index].dname))
    // console.log(JSON.stringify($scope.selectedAttributeValue))
    var count = 0;
    $scope.filterListarray = [];
    $scope.filterTag = [];
    for (var i = 0; i < $scope.selectedAttributeValue.length; i++) {
      var filterList = {};
      var filterTag = {};
      var ref = {};
      if ($scope.selectedAttributeValue[i].value != "-select-") {
        ref.type = $scope.filterAttribureIdValues[i].type;
        ref.uuid = $scope.filterAttribureIdValues[i].datapoduuid
        filterList.ref = ref;
        if ($scope.filterAttribureIdValues[i].type != "formual") {
          filterList.attrId = $scope.filterAttribureIdValues[i].datapodattrId;
          filterTag.text = $scope.filterAttribureIdValues[i].attrName + " - " + $scope.selectedAttributeValue[i].value;
        }
        else {
          filterTag.text = $scope.filterAttribureIdValues[i].name + " - " + $scope.selectedAttributeValue[i].value;
        }
        filterList.value = $scope.selectedAttributeValue[i].value;//"'"+$scope.selectedAttributeValue[i].value+"'";
        $scope.filterListarray[count] = filterList;
       // filterTag.text = $scope.filterAttribureIdValues[i].attrName + " - " + $scope.selectedAttributeValue[i].value;
        filterTag.index = i;
        filterTag.value = $scope.selectedAttributeValue[i].value;
        $scope.filterTag[count] = filterTag;
        count = count + 1;
      }
    }
   // console.log(JSON.stringify($scope.filterListarray));
    $scope.vizpodbody.filterInfo = $scope.filterListarray
    $('#attrFilter').modal("hide");
    $scope.executeDashboard($scope.vizpodbody);
  }

  $scope.onChipsRemove = function (index, filterIndex) {
    

    $scope.filterTag.splice(index, 1);
    $scope.selectedAttributeValue[filterIndex] = null;
    var noSelect = { "id": null, "value": "-select-" }
    setTimeout(function () {
      $scope.selectedAttributeValue[filterIndex] = noSelect;
      $scope.onFilterChange();
    }, 100);

  }
  $scope.getFilterValue = function (data) {
    $scope.filterAttribureIdValues = []
    $scope.selectedAttributeValue = [];
    $scope.atttArrayId=[];
    if (data.filterInfo && data.filterInfo.length > 0) {
      $scope.isFilterAttrInProges = true;
      var filterAttribureIdValue = [];
      for (var n = 0; n < data.filterInfo.length; n++) {
        var filterattributeidvalepromise = DahsboardSerivce.getAttributeValues(data.filterInfo[n].ref.uuid, data.filterInfo[n].attrId || "", data.filterInfo[n].ref.type);
        filterAttribureIdValue.push(filterattributeidvalepromise);
      }//End For Loop
      $q.all(filterAttribureIdValue).then(function (result) {
        $scope.isFilterAttrInProges = false;
        for (var i = 0; i < result.length; i++) {
          var filterAttribureIdvalueJSON = {};
          var defaultvalue = {}
          defaultvalue.id = null;
          defaultvalue.value = "-select-"
          filterAttribureIdvalueJSON.vizpoduuid =
          filterAttribureIdvalueJSON.vizpodversion = data.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapoduuid = data.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.name = data.filterInfo[i].ref.name;
          filterAttribureIdvalueJSON.type = data.filterInfo[i].ref.type;
          var tempId;
          if (data.filterInfo[i].ref.type != "formula") {
            filterAttribureIdvalueJSON.datapodattrId = data.filterInfo[i].attrId;
            filterAttribureIdvalueJSON.attrName = data.filterInfo[i].attrName;
            filterAttribureIdvalueJSON.dname = data.filterInfo[i].ref.name + "." + data.filterInfo[i].attrName;
            tempId=data.filterInfo[i].ref.uuid+"_"+data.filterInfo[i].attrId;
          }
          else {
            filterAttribureIdvalueJSON.attrName = data.filterInfo[i].ref.name;
            filterAttribureIdvalueJSON.dname = "formula" + "." + data.filterInfo[i].ref.name;
            tempId=data.filterInfo[i].ref.uuid
          }
          $scope.atttArrayId.push(tempId);
          filterAttribureIdvalueJSON.values = result[i].data
          filterAttribureIdvalueJSON.values.splice(0, 0, defaultvalue)
          $scope.selectedAttributeValue[i] = defaultvalue

          $scope.filterAttribureIdValues[i] = filterAttribureIdvalueJSON
         // console.log(JSON.stringify($scope.filterAttribureIdValues))
        }
      });//End $q.all
    }//End If

  }//End getFilterValue

  $scope.preparColumnData = function () {
    var colorcount = 0;
    for (var i = 0; i < $scope.sectionRows.length; i++) {
      for (var j = 0; j < $scope.sectionRows[i].columns.length; j++) {
        var datax = {}
        var vizpoddetailjson = {};
        vizpoddetailjson.id = "chart" + $scope.sectionRows[i].columns[j].vizpodInfo.id + i + j
        vizpoddetailjson.uuid = $scope.sectionRows[i].columns[j].vizpodInfo.uuid
        vizpoddetailjson.version = $scope.sectionRows[i].columns[j].vizpodInfo.version
        vizpoddetailjson.name = $scope.sectionRows[i].columns[j].vizpodInfo.name;
        vizpoddetailjson.type = $scope.sectionRows[i].columns[j].vizpodInfo.type;
        vizpoddetailjson.locked = $scope.sectionRows[i].columns[j].vizpodInfo.locked;
        vizpoddetailjson.class = "";
        vizpoddetailjson.iconclass = "fa fa-expand";
        vizpoddetailjson.showtooltiptitle = "Maximize";
        vizpoddetailjson.show = true;
        $scope.sectionRows[i].columns[j].vizpodDetails = vizpoddetailjson;
        $scope.sectionRows[i].columns[j].isChartDataGrid = true;
        $scope.sectionRows[i].columns[j].isChartShow = true;
        $scope.sectionRows[i].columns[j].isDataGridShow = false;
        if($scope.sectionRows[i].columns[j].vizpodInfo.keys[0].ref.type !='formula'){
          datax.id = $scope.sectionRows[i].columns[j].vizpodInfo.keys[0].attributeName;//x value
        }else{
          datax.id = $scope.sectionRows[i].columns[j].vizpodInfo.keys[0].ref.name;
        }
        $scope.sectionRows[i].columns[j].vizpodDetails.datax = datax;
        var datacolumnsarray = [];
        var columnName = []
        for (var k = 0; k < $scope.sectionRows[i].columns[j].vizpodInfo.values.length; k++) {
          var datacolumnsjson = {};
          if ($scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.type == "datapod" || $scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.type == "dataset") {
            datacolumnsjson.id = $scope.sectionRows[i].columns[j].vizpodInfo.values[k].attributeName;
            datacolumnsjson.name = $scope.sectionRows[i].columns[j].vizpodInfo.values[k].attributeName;
            columnName[k] = datacolumnsjson.name
          }//End If Inside For
          else {
            datacolumnsjson.id = $scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.name + "";
            datacolumnsjson.name = $scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.name + "";
            columnName[k] = datacolumnsjson.name
          }

          datacolumnsjson.type = $scope.sectionRows[i].columns[j].vizpodInfo.type.split("-")[0];

          if (k == 0 && $scope.sectionRows[i].columns[j].vizpodInfo.type == "bar-line-chart") {
            datacolumnsjson.type = "line"

          }
          else if (k >= 1 && $scope.sectionRows[i].columns[j].vizpodInfo.type == "bar-line-chart") {
            datacolumnsjson.type = "bar"

          }

          if (colorcount <= 16) {
            if (colorcount == 16) {
              colorcount = 0;
            }
            datacolumnsjson.color = $scope.chartcolor[colorcount];
            colorcount = colorcount + 1;
          }//End If
          datacolumnsarray[k] = datacolumnsjson
          $scope.sectionRows[i].columns[j].vizpodDetails.datacolumns = datacolumnsarray
          //console.log(JSON.stringify($scope.sectionRows[i].columns[j].vizpodDetails.datacolumns));
        }//End For K
        $scope.sectionRows[i].columns[j].vizpodDetails.columnNameY2 = columnName[0];
        columnName.splice(0, 1);
        $scope.sectionRows[i].columns[j].vizpodDetails.columnNameY1 = columnName.toString();


        if ($scope.sectionRows[i].columns[j].vizpodInfo.type == "data-grid" || $scope.sectionRows[i].columns[j].isChartDataGrid) {
          var keyvalueData = null;
          if ($scope.sectionRows[i].columns[j].vizpodInfo.type == "data-grid") {
            $scope.sectionRows[i].columns[j].isChartShow = false;
            $scope.sectionRows[i].columns[j].isDataGridShow = false;
          }
          $scope.sectionRows[i].columns[j].gridOptions = {}
          $scope.Preparedatagrid(i, j);
          $scope.sectionRows[i].columns[j].gridOptions.columnDefs = [];
          if ($scope.sectionRows[i].columns[j].vizpodInfo.groups.length > 0) {
            keyvalueData = $scope.sectionRows[i].columns[j].vizpodInfo.keys.concat($scope.sectionRows[i].columns[j].vizpodInfo.values, $scope.sectionRows[i].columns[j].vizpodInfo.groups);
          }//End Inner IF
          else {
            keyvalueData = $scope.sectionRows[i].columns[j].vizpodInfo.keys.concat($scope.sectionRows[i].columns[j].vizpodInfo.values);
          }//End Innder Else
          //console.log(JSON.stringify(keyvalueData))
          for (var c = 0; c < keyvalueData.length; c++) {
            var attribute = {};
            if (keyvalueData[c].ref.type == "datapod" || keyvalueData[c].ref.type == "dataset") {
              attribute.name = keyvalueData[c].attributeName;
              attribute.displayName = keyvalueData[c].attributeName;
              //attribute.width =$scope.keyvalueData[c].attributeName.split('').length + 2 + "%"
            }
            else {
              attribute.name = keyvalueData[c].ref.name;
              attribute.displayName = keyvalueData[c].ref.name;
              //attribute.width=$scope.keyvalueData[c].ref.name;
            }

            $scope.sectionRows[i].columns[j].gridOptions.columnDefs.push(attribute)
          }//End C loop
        }//End Else
      }
    }//End For I
    // console.log("data_grid"+JSON.stringify($scope.sectionRows));
  }//End preparColumnData

  $scope.selectPage = function (pageNo) {
    $scope.pagination.currentPage = pageNo;
  };
  $scope.onPerPageChange = function () {
    $scope.pagination.currentPage = 1;
    $scope.getResults($scope.orignalData)
  }
  $scope.pageChanged = function () {
    $scope.getResults($scope.orignalData)
  };
  $scope.getResults = function (params) {
    $scope.pagination.totalItems = params.length;
    if ($scope.pagination.totalItems > 0) {
      $scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize)) + 1);
    }
    else {
      $scope.pagination.to = 0;
    }
    if ($scope.pagination.totalItems < ($scope.pagination.pageSize * $scope.pagination.currentPage)) {
      $scope.pagination.from = $scope.pagination.totalItems;
    } else {
      $scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
    }
    var limit = ($scope.pagination.pageSize * $scope.pagination.currentPage);
    var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
    $scope.gridOptions.data = params.slice(offset, limit);
  }

  $scope.filterSearchVizpodDetail = function (s) {
    $scope.gridOptions.data = $filter('filter')($scope.orignalData, s, undefined);
    //$scope.getResults(data)
  }
  $scope.backVizpodSummary = function () {
    $scope.IsVizpodDetailShow = !$scope.IsVizpodDetailShow;
    $timeout(function () {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }
  $scope.selectData = function (data) {
    var menu = [{
      title: 'Show Detail',
      action: $scope.actionEvent,
      disabled: false // optional, defaults to false
    }];
    console.log(data);
    $scope.contextMenu1(menu, data);
  }
  $scope.showClick = function (data) {
    var menu = [{
      title: 'Show Detail',
      action: $scope.actionEvent,
      disabled: false // optional, defaults to false
    }]
    console.log(data)
    $scope.contextMenu1(menu, data);
  }



  $scope.getVizpodResultDetails = function (uuid, version, vizpodbody, data) {
    $scope.IsVizpodDetailShow = true;
    $scope.inprogressdatavizpodetail = true;
    $scope.VizpodDetail = data.vizpod;
    $scope.isDataErrorvizpodetail = false;
    DahsboardSerivce.getVizpodDetails(uuid, version, vizpodbody).then(function (response) { onSuccessGetVizpodDetails(response.data) }, function (response) { onError(response.data) });
    var onSuccessGetVizpodDetails = function (response) {
      $scope.isDataErrorvizpodetail = false;
      $scope.inprogressdatavizpodetail = false;
      var columns = [];
      $scope.orignalData = response;
      if (response.length && response.length > 0) {
        angular.forEach(response[0], function (val, key) {
          if (key != 'rownum') {
            var w = key.split('').length + 2 + "%"
            columns.push({ "name": key, "displayName": key.toLowerCase(), width: w + "%" });
          }
        });
      }
      $scope.gridOptions.columnDefs = columns;
      console.log(response);
      $scope.getResults(response);
    }//End onSuccessLatestByUuid 
    var onError = function () {
      $scope.isDataErrorvizpodetail = true;
      $scope.inprogressdatavizpodetail = false;
    }
  }

  // $scope.actionEvent = function (d, i, data) {

  //   var filterinfoArray = []
  //   var vizpodbody = {}
  //   var filterInfo = {};
  //   var ref = {}
  //   if (data.dataobj.value != "") {
  //     ref.uuid = data.vizpod.vizpodInfo.values[0].ref.uuid;
  //     ref.version = null;
  //     ref.type = data.vizpod.vizpodInfo.values[0].ref.type
  //     filterInfo.ref = ref;
  //     filterInfo.attrId = data.vizpod.vizpodInfo.values[0].attributeId;
  //     for (var i = 0; i < data.vizpod.vizpodInfo.values.length; i++) {
  //       if(data.vizpod.vizpodInfo.values[i].ref =="datapod" ||data.vizpod.vizpodInfo.values[i].ref =="dataset"){
  //         atttrName=data.vizpod.vizpodInfo.values[i].attributeName;
  //        }
  //        else{
  //         atttrName=data.vizpod.vizpodInfo.values[i].ref.name;
  //        }
  //       if (atttrName.indexOf(data.dataobj.id) != -1) {
  //         filterInfo.attrId = data.vizpod.vizpodInfo.values[i].attributeId;
  //         break;
  //       }
  //     }
  //     filterInfo.value = data.dataobj.value
  //     filterinfoArray.push(filterInfo);
  //     vizpodbody.filterInfo = filterinfoArray
  //   }
  //   else {
  //     vizpodbody = null;
  //   }

  //   $scope.getVizpodResultDetails(data.vizpod.vizpodInfo.uuid, data.vizpod.vizpodInfo.version, vizpodbody,data)
  // }


  $scope.actionEvent = function (d, i, data) {
    debugger
    var filterinfoArray = []
    var vizpodbody = {}
    var filterInfo = {};
    var ref = {}
    if (data.dataobj.value != "") {
      ref.uuid = data.vizpod.vizpodInfo.keys[0].ref.uuid;
      ref.version = null;
      ref.type = data.vizpod.vizpodInfo.keys[0].ref.type;
      filterInfo.ref = ref;
      if(data.vizpod.vizpodInfo.keys[0].ref.type !="formual"){
        filterInfo.attrId = data.vizpod.vizpodInfo.keys[0].attributeId;
      }
      if (data.vizpod.vizpodInfo.type == "world-map" || data.vizpod.vizpodInfo.type == "usa-map") {
        filterInfo.value = data.dataobj.x;
        filterinfoArray.push(filterInfo);
      } else if (["world-map", "usa-map", "pie-chart", "donut-chart"].indexOf(data.vizpod.vizpodInfo.type) == -1) {
        filterInfo.value = data.vizpod.vizpodDetails.datapoints[data.dataobj.x][data.vizpod.vizpodDetails.datax.id];
        filterinfoArray.push(filterInfo);
        // if(data.vizpod.vizpodInfo.groups.length >0 &&  data.vizpod.vizpodInfo.type =="bar-chart"){
        //   var filterInfo1={};
        //   filterInfo1=filterInfo;
        //   filterInfo1.attrId=data.vizpod.vizpodInfo.groups[0].attributeId;
        //   filterInfo1.value=data.vizpod.dataPoint[data.dataobj.x][data.vizpod.vizpodInfo.groups[0].attributeName];
        //   filterinfoArray.push(filterInfo1);
        // }
        //filterInfo.value = filterInfo.value.replace(/0 -/g, ' -');
        //filterInfo.value = filterInfo.value.replace(/0$/g, '');
      } else if (["pie-chart", "donut-chart"].indexOf(data.vizpod.vizpodInfo.type) != -1) {
        filterInfo.value = data.dataobj.id;
        filterinfoArray.push(filterInfo);
      }
     
      vizpodbody.filterInfo = filterinfoArray
    }
    else {
      vizpodbody = null;
    }

    $scope.getVizpodResultDetails(data.vizpod.vizpodInfo.uuid, data.vizpod.vizpodInfo.version, vizpodbody, data)

  }
  $scope.contextMenu1 = function (menu, vizpodbody) {
    d3.select(".jitu").selectAll('.context-menu').data([1])
      .enter()
      .append('div')
      .attr('class', 'context-menu')

    // close menu
    d3.select('body').on('click.context-menu', function () {
      d3.selectAll('.context-menu').style('display', 'none');
    });


    var elm = this;
    d3.selectAll('.context-menu')
      .html('')
      .append('ul')
      .selectAll('li')
      .data(menu).enter()
      .append('li')
      .html(function (d) {
        return d.title;
      })
      .on('click', function (d, index) {
        d.action(elm, d, vizpodbody);
        d3.selectAll('.context-menu').style('display', 'none');
      })
    //console.log(event)
    // show the context menu
    d3.selectAll('.context-menu')
      .style('left', (d3.event["pageX"] - 2) + 'px')
      .style('top', (d3.event["pageY"] - 30) + 'px')
      .style('display', 'block');
    d3.event.preventDefault();

  };


  $scope.setDefault = function (inProgess, isDataError) {
    for (var i = 0; i < $scope.sectionRows.length; i++) {
      for (var j = 0; j < $scope.sectionRows[i].columns.length; j++) {
        $scope.sectionRows[i].columns[j].isDataError = isDataError;
        $scope.sectionRows[i].columns[j].isInprogess = inProgess;
        $scope.sectionRows[i].columns[j].vizpodDetails.datapoints = [];
      }
    }
  }
  $scope.convertResultTwoDisit = function (data, columnName) {
    if (data.length > 0) {
      for (var i = 0; i < data.length; i++) {
        data[i][columnName] = parseFloat(data[i][columnName].toFixed(2));
      }
    }

    return data;
  }

  // function ConvertTwoDisit(data, propName) {
  //   // if(isNaN(data[0][propName])){
  //   if (data.length > 0 && data[0][propName].indexOf("-") != -1) {
  //     for (var i = 0; i < data.length; i++) {
  //       a = data[i][propName].split('-')[0];
  //       b = data[i][propName].split('-')[1]
  //       data[i][propName] = parseFloat(a).toFixed(2) + " - " + parseFloat(b).toFixed(2);
  //       // console.log(data[i][propName])
  //     }
  //   }
  //   // }
  //   //console.log(data)
  //   return data;
  // }
  var reA = /[^a-zA-Z]/g;
  var reN = /[^0-9]/g;
  function sortAlphaNum(propName) {
    return function (a, b) {

      var aA = a[propName].replace(reA, "");
      var bA = b[propName].replace(reA, "");
      if (aA === bA) {
        var aN = parseFloat(a[propName].replace(reN, ""), 10);
        var bN = parseFloat(b[propName].replace(reN, ""), 10);
        return aN === bN ? 0 : aN > bN ? 1 : -1;
      } else {
        return aA > bA ? 1 : -1;
      }
    }
  }
  $scope.refreshVizpod = function (rowNo, colNo) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    var parentIndex = rowNo - 1;
    var index = colNo - 1;
    $scope.sectionRows[parentIndex].columns[index].isDataGridShow = false;
    $scope.sectionRows[parentIndex].columns[index].isDataError = false;
    $scope.sectionRows[parentIndex].columns[index].isInprogess = true;
    $scope.sectionRows[parentIndex].columns[index].vizpodDetails.datapoints = [];
    var vizpodResuts = {};
    vizpodResuts.rowNo = parentIndex;
    vizpodResuts.colNo = index;
    vizpodResuts.type = $scope.dashboardExecData.sectionViewInfo[parentIndex].vizpodInfo.type;
    DahsboardSerivce.getVizpodResults($scope.sectionRows[parentIndex].columns[index].vizExecInfo.uuid, $scope.sectionRows[parentIndex].columns[index].vizExecInfo.version, $scope.dashboarddata.saveOnRefresh, vizpodResuts).then(function (response) { onSuccessGetVizpodResult(response) }, function (response) { onError(response) });
    var onSuccessGetVizpodResult = function (result) {
      if (result.vizpodResuts != "network-graph") {
        $scope.inprogressdata = false
        $scope.isUserNotification = false;
        $scope.sectionRows[parentIndex].columns[index].isChartShow = true;
        $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isDataError = false;
        $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isInprogess = false;
        $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].errormsg = "";
        if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.type == "bar-line-chart") {
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = $scope.convertResultTwoDisit(result.data, $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.columnNameY2);
        } else {
          var propName;
          if($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].ref.type !="formula"){
            propName=$scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].attributeName;
          }else{
            propName=$scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].ref.name;
          }
          if (isNaN(result.data[0][propName])) {
            if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.type == "bar-chart") {
             // ConvertTwoDisit(result.data, $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].attributeName);
              result.data.sort(sortAlphaNum(propName))
            }

          }
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = result.data;
        }
        if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.type == "data-grid" || $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isChartDataGrid) {
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].gridOptions.data = result.data;
        }
      }
      $scope.preparColumnDataFromResult(result.vizpodResuts.rowNo, result.vizpodResuts.colNo);

    }

    var onError = function (result) {
      console.log(result)
      $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isDataError = true;
      $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isInprogess = false;
      $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = [];
      $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].errormsg = result.data.message || "Some Error Occurred";
      $scope.inprogressdata = false;

    }
  }

  $scope.getVizpodResut = function (data) {
    $scope.isDataError = false;
    $scope.vizpodResutsArray = [];
    $scope.vizpodtrack = []
    $scope.isUserNotification = true;
    $scope.inprogressdata = true
    $scope.setDefault($scope.inprogressdata, $scope.isDataError);
    for (var i = 0; i < $scope.dashboardExecData.sectionViewInfo.length; i++) {
      var vizpodResuts = {};
      if ($scope.dashboardExecData.sectionViewInfo[i].vizpodInfo.type != 'network-graph') {
        vizpodResuts.rowNo = $scope.dashboardExecData.sectionViewInfo[i].rowNo - 1;
        vizpodResuts.colNo = $scope.dashboardExecData.sectionViewInfo[i].colNo - 1;
        vizpodResuts.type = $scope.dashboardExecData.sectionViewInfo[i].vizpodInfo.type;
        if($scope.dashboardExecData.sectionViewInfo[i].vizExecInfo !=null){
	        var vizpodresultpromise = DahsboardSerivce.getVizpodResults($scope.dashboardExecData.sectionViewInfo[i].vizExecInfo.uuid, $scope.dashboardExecData.sectionViewInfo[i].vizExecInfo.version, $scope.dashboarddata.saveOnRefresh, vizpodResuts);
	        $scope.vizpodtrack.push(vizpodResuts);
	        $scope.vizpodResutsArray.push(vizpodresultpromise);
        }
        else{
        	   // console.log(result)
            $scope.sectionRows[ vizpodResuts.rowNo].columns[vizpodResuts.colNo].isDataError = true;
            $scope.sectionRows[ vizpodResuts.rowNo].columns[vizpodResuts.colNo].isInprogess = false;
            $scope.sectionRows[ vizpodResuts.rowNo].columns[vizpodResuts.colNo].vizpodDetails.datapoints = [];
            $scope.sectionRows[ vizpodResuts.rowNo].columns[vizpodResuts.colNo].errormsg = "Some Error Occurred";
            //$scope.inprogressdata = false;
        }
      }
      else {
        console.log("network Graph");
        $scope.inprogressdata = false
      }
    }
    //  console.log($scope.vizpodResutsArray);

    $q.all($scope.vizpodResutsArray.map(function (value) {
      return $q.resolve(value)
        .then(function (result) {
          if (result.vizpodResuts != "network-graph") {
            $scope.inprogressdata = false
            $scope.isUserNotification = false;
            $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isDataError = false;
            $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isInprogess = false;
            $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].errormsg = "";
            //  console.log($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo)
            if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.type == "bar-line-chart") {
              $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = $scope.convertResultTwoDisit(result.data, $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.columnNameY2);
            } else {
              var propName;
              if($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].ref.type !="formula"){
                propName=$scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].attributeName;
              }else{
                propName=$scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].ref.name;
              }
              if (isNaN(result.data[0][propName])) {
                if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.type == "bar-chart") {
               //   ConvertTwoDisit(result.data, $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodInfo.keys[0].attributeName);
                  result.data.sort(sortAlphaNum(propName))
                }

              }
              $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = result.data;
            }
            if ($scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.type == "data-grid" || $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isChartDataGrid) {
              $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].gridOptions.data = result.data;
            }
          }
          $scope.preparColumnDataFromResult(result.vizpodResuts.rowNo, result.vizpodResuts.colNo);
          //return { state: "fulfilled", value: result };
        })
        .catch(function (result) {
          // console.log(result)
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isDataError = true;
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].isInprogess = false;
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].vizpodDetails.datapoints = [];
          $scope.sectionRows[result.vizpodResuts.rowNo].columns[result.vizpodResuts.colNo].errormsg = result.data.message || "Some Error Occurred";
          $scope.inprogressdata = false;
          // return { state: "rejected", reason: error };
        });
    }));
    //console.log(JSON.stringify($scope.sectionRows))
  }

  $scope.preparColumnDataFromResult = function (rowNo, colNo) {
    if ($scope.sectionRows[rowNo].columns[colNo].vizpodInfo.type == 'pie-chart' || $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.type == 'donut-chart') {
      var columnname;
      if($scope.sectionRows[rowNo].columns[colNo].vizpodInfo.keys[0].ref.type !="formula"){
        columnname= $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.keys[0].attributeName;
      }else{
        columnname= $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.keys[0].ref.name;
      }
      
      if ($scope.sectionRows[rowNo].columns[colNo].vizpodInfo.values[0].ref.type == "datapod" || $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.values[0].ref.type == "dataset") {
        columnnamevalue = $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.values[0].attributeName
      }
      else {
        columnnamevalue = $scope.sectionRows[rowNo].columns[colNo].vizpodInfo.values[0].ref.name
      }
      var columnarray = []
      var dataarray = []
      var colorcount = 0;
      for (var k = 0; k < $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datapoints.length; k++) {
        var datacolumnsjson = {};
        var datajson = {};
        datacolumnsjson.id = $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datapoints[k][columnname] + "";
        datacolumnsjson.ref = "jitu"
        datacolumnsjson.type = $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.type.split("-")[0];
        if (colorcount <= 15) {
          if (colorcount == 15) {
            colorcount = 0;
          }
          datacolumnsjson.color = $scope.chartcolor[colorcount];
          colorcount = colorcount + 1;
        }//End If
        datajson[$scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datapoints[k][columnname]] = $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datapoints[k][columnnamevalue];
        columnarray[k] = datacolumnsjson;
        dataarray[k] = datajson;

      }
      //  console.log(JSON.stringify(dataarray));
      $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datax = "";
      $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datapoints = dataarray;
      $scope.sectionRows[rowNo].columns[colNo].vizpodDetails.datacolumns = columnarray;
    }
    //  console.log(JSON.stringify($scope.sectionRows));
  }


  $scope.getOneByUuidAndVersionDashboardExec = function (data) {
    
    if(data !=null){
      DahsboardSerivce.getOneByUuidAndVersion(data.uuid, data.version, "dashboardexecview").then(function (response) { onSuccessLatestByUuid(response.data) });
      var onSuccessLatestByUuid = function (response) {
      // console.log(response);
        $scope.dashboardExecData = response;
        $scope.convertSectionInfo(response.sectionViewInfo)
        $scope.preparColumnData();
        $scope.getVizpodResut(null);
        $scope.filterTag = [];
        $scope.populateFilers();
      

      }
    }
  }
  $scope.getLatestByUuidDashboard = function () {
    DahsboardSerivce.getLatestByUuidView($stateParams.id, "dashboard").then(function (response) { onSuccessLatestByUuid(response.data) });
    var onSuccessLatestByUuid = function (response) {
      $scope.dashboarddata = response;
      $scope.uuid = response.uuid;
      $scope.version = response.version;
      $scope.getFilterValue($scope.dashboarddata)// Method call for populate filter dropdown data;

    }//End onSuccessLatestByUuid
  }

  $scope.callGraph = function () {
    $scope.vizpodtypedetail1 = [];
    $scope.filterTag = [];
    count = 0;
    $scope.getLatestByUuidDashboard();
  }//End Call Graph()
   
  $scope.resetFilter=function(){
    if($scope.filterAttribureIdValues && $scope.filterAttribureIdValues.length>0){
      for(let i=0;i<$scope.filterAttribureIdValues.length;i++){
        $scope.selectedAttributeValue[i].value=null;
        setTimeout(function(){
          var defaultvalue = {}
          defaultvalue.id = null;
          defaultvalue.value = "-select-"
          $scope.selectedAttributeValue[i].value=defaultvalue.value;
        },100)
        

      }
    }
  }
  
  $scope.populateFilers=function(){
    if($scope.dashboardExecData !=null && $scope.dashboardExecData.filterInfo !=null &&  $scope.dashboardExecData.filterInfo.length >0){
      for(var i=0;i<$scope.dashboardExecData.filterInfo.length;i++){
        var filterTag={};
        var tempId=$scope.dashboardExecData.filterInfo[i].ref.uuid;
        filterTag.value=$scope.dashboardExecData.filterInfo[i].value;
        filterTag.text=$scope.dashboardExecData.filterInfo[i].ref.name+" - "+$scope.dashboardExecData.filterInfo[i].value;
        filterTag.index=i;
        if($scope.dashboardExecData.filterInfo[i].ref.type !="formula"){
          tempId=$scope.dashboardExecData.filterInfo[i].ref.uuid+"_"+$scope.dashboardExecData.filterInfo[i].attrId;
          filterTag.text=$scope.dashboardExecData.filterInfo[i].attrName+" - "+$scope.dashboardExecData.filterInfo[i].value;;
        }
        $scope.filterTag[i]=filterTag;
        if($scope.atttArrayId.indexOf(tempId) !=-1){
          setTimeout(function(){
            var defaultvalue = {}
            defaultvalue.id = null;
            defaultvalue.value = "-select-"
            $scope.filterAttribureIdValues[$scope.atttArrayId.indexOf(tempId)].values[0]=defaultvalue;
          },100)
          $scope.selectedAttributeValue[$scope.atttArrayId.indexOf(tempId)].value=$scope.dashboardExecData.filterInfo[i].value
          
        }  
      }
    }
  }

  $scope.onChangeDashboardExec = function () {
    $scope.resetFilter();
    $scope.getOneByUuidAndVersionDashboardExec($scope.selectedDExec);
  }

  $scope.reRunDashboard=function(){
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.filterTag=[];
    //$scope.executeDashboard(null); 
    $scope.getAllLatestDashboardExec("reRun"); 
  }

  $scope.submitDownload = function () {
    var uuid = $scope.download.data.uuid;
    var version = $scope.download.data.version;
    var url = $location.absUrl().split("app")[0];
    $('#downloadSample').modal("hide");
    $http({
      method: 'GET',
      url: url + "vizpod/download?action=view&uuid=" + uuid + "&version=" + version + "&rows=" + $scope.download.rows+"&saveOnRefresh="+$scope.dashboardExecData.dashboard.saveOnRefresh+"&format="+$scope.download.selectFormate,
      responseType: 'arraybuffer'
    }).success(function (data, status, headers) {

      $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;

        headers = headers();
		var filename = headers['filename'];
		var contentType = headers['content-type'];
		var linkElement = document.createElement('a');
		try {
			var blob = new Blob([data], {
				type: contentType
			});
			var url = window.URL.createObjectURL(blob);
			linkElement.setAttribute('href', url);
			linkElement.setAttribute("download",filename);
            var clickEvent = new MouseEvent("click", {
          "view": window,
          "bubbles": true,
          "cancelable": false
        });
        linkElement.dispatchEvent(clickEvent);
      } catch (ex) {
        console.log(ex);
      }
    }).error(function (data) {
      console.log(data);
    });
  }


  $scope.downloadFile = function (data) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.download.data = data;
    $('#downloadSample').modal({
      backdrop: 'static',
      keyboard: false
    });
  };

  $scope.executeDashboard = function (data) {
    var id = $stateParams.id;
    $scope.isDashboardInprogess = true;
    $scope.isDashboardError = false;
    DahsboardSerivce.executeDashboard(id, "dashboard", data)
      .then(function (response) { onSuccessExecuteDashboard(response.data) }, function (response) { onError(response.data) });
    var onSuccessExecuteDashboard = function (response) {
      $scope.isDashboardInprogess = false;
      $scope.isDashboardError = false;
      $scope.dashboardExec = response;
      $scope.selectedDExec = response;
      if ($scope.allExecDetail && $scope.allExecDetail.length > 0) {
        $scope.allExecDetail.push(response);
      } else {
        $scope.allExecDetail = [];
        $scope.allExecDetail.push(response);
      }
      //console.log(response);
      $scope.getOneByUuidAndVersionDashboardExec(response);
    }

    var onError = function () {
      $scope.isDashboardInprogess = false;
      $scope.isDashboardError = true;
    }

  }

  $scope.getAllLatestDashboardExec = function (action) {
    DahsboardSerivce.getDasboardExecBySave($stateParams.id,"dashboard","Y").then(function (response) { onSuccessGetAllLatest(response.data) });
    var onSuccessGetAllLatest = function (response) {
      $scope.allExecDetail = response;
      if(response && response.length ==0){
        $scope.executeDashboard(null);  
      }else if(action !=null && action=='reRun'){
        $scope.executeDashboard(null);
      }
      else{
        let len=response.length-1;
        $scope.selectedDExec = response[0];
        $scope.getOneByUuidAndVersionDashboardExec($scope.selectedDExec);
      }
    }
  }

  $scope.callGraph();
  $scope.getAllLatestDashboardExec(null);
  
})//End ShowDashboradController
