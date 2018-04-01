/****/
AdminModule = angular.module('AdminModule');
AdminModule.controller('RegisterSourceController', function ($stateParams,$filter,$rootScope, $scope, RegisterSourceService,uiGridConstants) {
  $scope.isSearchDisable = true;
  $scope.isRSDisable=true;
  $scope.searchButtonText = "Register";
  $scope.gridOptions = {
    paginationPageSizes: null,
    enableGridMenu: true,
    rowHeight: 45,
      columnDefs: [{
      displayName: 'Id',
      name: 'id',
      minWidth: 250,
      visible: false,
      cellClass: 'text-center',
      headerCellClass: 'text-center'
      },{
        displayName: 'Id',
        name: 'id',
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
        displayName: 'Desc',
        name: 'desc',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        sort: {
          direction: uiGridConstants.DESC,
          // priority: 0,
        },
      },
      {
        displayName: 'RegisteredOn',
        name: 'registeredOn',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Status',
        name: 'status',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        cellTemplate:'<div class="ui-grid-cell-contents text-center" ><i style="margin:3px auto;" ng-show="row.entity.status ==\'Registering\'" class="glyphicon glyphicon-refresh spinning" aria-hidden="true"></i><span ng-show="row.entity.status !=\'Registering\'">{{row.entity.status}}</span></div>'
      },

    ]
  };
  $scope.gridOptions.enableHorizontalScrollbar = uiGridConstants.scrollbars.NEVER;
 
  $scope.gridOptions.data=[];
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };
  $scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    gridApi.selection.on.rowSelectionChanged($scope,function(row){
      $scope.selectButtonClick(row.entity);
   });
   gridApi.selection.on.rowSelectionChangedBatch($scope,function(row){
      $scope.selectButtonClick(row.entity);
    });

    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  $scope.getGridStyle = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 50 : 400) + 50) + 'px';
    } else {
      style['height'] = "100px";
    }
    return style;
  }
  
  $scope.refreshData = function(searchtext) {
    console.log(searchtext)
    $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
  };

  $scope.refresh=function () {
    $scope.selectDataSource= {};
    $scope.allDataSource=[];
    $scope.searchtext=null;
    $scope.getDatasourceByApp();
    $scope.gridOptions.data=[];
  }

  $scope.getDatasourceByApp = function () {
    $scope.selectDataSource = null;
    $scope.allDataSource = null;
    RegisterSourceService.getDatasourceByApp($rootScope.appUuid).then(function (response){ onSuccessGetDatasourceByType(response.data)})
    var onSuccessGetDatasourceByType = function (response) {
      $scope.allDataSource = response
    }
  }
  $scope.getDatasourceByApp();
  
  $scope.onChangeSource=function(){
    console.log($scope.selectDataSource)
    if($scope.selectDataSource !=null){
      $scope.isSearchDisable=false;
    }
    else{
      $scope.isSearchDisable=true;
      $scope.gridOptions.data=[];

    }
  }
  $scope.searchSource=function(){
    $scope.isSearchDisable=true;
    $scope.isDataSourceInpogress = true;
    RegisterSourceService.getRegistryByDatasource($scope.selectDataSource.uuid).then(function (response) {onSuccessGetRegistryByDatasource(response.data)}, function (response) {onError(response.data)});
    var onSuccessGetRegistryByDatasource = function (response) {
     // console.log(JSON.stringify(response))
      $scope.isDataSourceInpogress = false;
      $scope.originalData = response
      $scope.gridOptions.data=response;
    }
    var onError = function (response) {
      $scope.isDataSourceInpogress = false;
      $scope.isDataError = true;
      $scope.msgclass = "errorMsg";
      $scope.datamessage = "Some Error Occurred";
    }
  }

  $scope.selectButtonClick=function(row, $event){
    console.log($scope.gridApi.selection.getSelectedRows())
    if($scope.gridApi.selection.getSelectedRows().length >0){
      $scope.isRSDisable=false;
    }else{$scope.isRSDisable=true;}
  }

   $scope.submitRegisgterSource = function () {
    var registerSourceArray = [];
    $scope.isRSDisable=true;
    var count = 0;
    $scope.searchButtonText = "Registering";
    var selectRegisterSoucre=$scope.gridApi.selection.getSelectedRows()
    for (var i = 0; i < selectRegisterSoucre.length; i++) {
      var registerSourceJson = {};
      $scope.gridOptions.data[selectRegisterSoucre[i].id-1].status="Registering"
      registerSourceJson.id = selectRegisterSoucre[i].id
      registerSourceJson.name = selectRegisterSoucre[i].name;
      registerSourceJson.dese = selectRegisterSoucre[i].dese;
      registerSourceJson.registeredOn = selectRegisterSoucre[i].registeredOn;
      registerSourceJson.status = selectRegisterSoucre[i].status
      registerSourceArray[count] = registerSourceJson;
      count = count + 1;
    }
    console.log(JSON.stringify(registerSourceArray))
    RegisterSourceService.getRegister($scope.selectDataSource.uuid, $scope.selectDataSource.version, registerSourceArray, $scope.selectDataSource.type).then(function (response) {onSuccessGetcreateAndLoad(response.data)});
    var onSuccessGetcreateAndLoad = function (response) {
     // console.log(JSON.stringify(response))
      $scope.searchButtonText = "Register";
      $scope.dataLoading = false;
      for (var i = 0; i < response.length; i++) {
        var id = response[i].id - 1
        $scope.gridOptions.data[id].registeredOn = response[i].registeredOn;
        $scope.gridOptions.data[id].desc = response[i].desc;
        $scope.gridOptions.data[id].status = response[i].status;
        $scope.gridApi.selection.unSelectRow($scope.gridOptions.data[id]);
      }
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = 'Datapod Registered Successfully'
      $scope.$emit('notify', notify);
    }
  }
  

  

  // $scope.selectAllRegisterSource = function () {
  //   $scope.isSubmitEnable = !$scope.allselect;
  //   angular.forEach($scope.sourecedata, function (source) {
  //     source.selected = $scope.allselect;
  //   });
  // }
  // $scope.selectRegisterSource = function (index, data) {
  //   var result = true;
  //   for (var i = 0; i < $scope.sourecedata.length; i++) {
  //     if ($scope.sourecedata[i].selected == true) {

  //       result = false
  //       i = $scope.sourecedata.length;
  //     }
  //   }
  //   $scope.isSubmitEnable = result
  // }

  // $scope.submitRegisgterSource = function () {
  //   $scope.isSubmitEnable = true;
  //   $scope.dataLoading = true;
  //   var sourcearray = [];
  //   var count = 0;
  //   for (var i = 0; i < $scope.sourecedata.length; i++) {
  //     var sourcejson = {};
  //     if ($scope.sourecedata[i].selected == true) {
  //       sourcejson.id = $scope.sourecedata[i].id
  //       sourcejson.name = $scope.sourecedata[i].name;
  //       sourcejson.dese = $scope.sourecedata[i].dese;
  //       sourcejson.registeredOn = $scope.sourecedata[i].registeredOn;
  //       sourcejson.status = $scope.sourecedata[i].status
  //       sourcearray[count] = sourcejson;
  //       count = count + 1;
  //     }

  //   }
  
  //   console.log(JSON.stringify(sourcearray))
  //   RegisterSourceService.getRegister($scope.datasourcedata.uuid, $scope.datasourcedata.version, sourcearray, $scope.datasourcedata.type).then(function (response) {
  //     onSuccessGetcreateAndLoad(response.data)
  //   });
  //   var onSuccessGetcreateAndLoad = function (response) {
  //     console.log(JSON.stringify(response))
  //     $scope.dataLoading = false;

  //     //$scope.selectDataSource();
  //     for (var i = 0; i < response.length; i++) {
  //       var id = response[i].id - 1
  //       $scope.sourecedata[id].selected = "false"
  //       $scope.sourecedata[id].registeredOn = response[i].registeredOn;
  //       $scope.sourecedata[id].desc = response[i].desc;
  //       $scope.sourecedata[id].status = response[i].status;
  //     }
  //     notify.type = 'success',
  //       notify.title = 'Success',
  //       notify.content = 'Source Saved Successfully'
  //     $scope.$emit('notify', notify);
  //   }
  // }
});
