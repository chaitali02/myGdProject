/****/
AdminModule = angular.module('AdminModule');
AdminModule.controller('RegisterSourceController', function ($stateParams,$filter,$rootScope, $scope, RegisterSourceService,uiGridConstants,dagMetaDataService,CommonService) {
  $scope.isSearchDisable = true;
  $scope.isSelectAllDisabled=true;
  $scope.isRSDisable=true;
  $scope.path = dagMetaDataService.compareMetaDataStatusDefs
  $scope.status = dagMetaDataService.statusDefs

  $scope.searchButtonText = "Register";
  $scope.gridOptions = {
    paginationPageSizes: null,
    enableGridMenu: true,
    rowHeight: 40,
      columnDefs: [
      {
        name: "selected",
        maxWidth: 40,
        visible: true,
        headerCellTemplate:'<div class="ui-grid-cell-contents" style="padding-top:9px;"><input  type="checkbox" style="width: 30px;height:16px;" ng-disabled="grid.appScope.isSelectAllDisabled" ng-model="grid.appScope.selectedAllRow" ng-change="grid.appScope.OnSelectAllRow()"/></div>',
        cellTemplate:'<div class="ui-grid-cell-contents"  style="padding-top:2px;padding-left:4px;"><input type="checkbox"  ng-disabled="row.entity.isDisabled" style="width:20px;height:16px;" ng-model="row.entity.selected" ng-change="grid.appScope.onSelectRow()"/></div>'
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
        cellClass: 'text-right',
        headerCellClass: 'text-center',
        cellTemplate:'<div class="grid-tooltip" title="{{row.entity.name}}" ><div class="ui-grid-cell-contents">{{ COL_FIELD }}</div></div>',
        sort: {
          direction: uiGridConstants.ASC,
          // priority: 0,
        },
      },
      {
        displayName: 'Desc',
        name: 'desc',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
       
      },
      {
        displayName: 'Registered On',
        name: 'registeredOn',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        cellTemplate:'<div class="grid-tooltip" title="{{row.entity.registeredOn}}" ><div class="ui-grid-cell-contents">{{ COL_FIELD }}</div></div>',
      },
      {
        displayName: 'Registered By',
        name: 'registeredBy',
        cellClass: 'text-center',
        headerCellClass: 'text-center'
      },
      {
        displayName: 'Status',
        name: 'status',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        // cellTemplate:'<div class="ui-grid-cell-contents text-center" ><i style="margin:3px auto;" ng-show="row.entity.status ==\'Registering\'" class="glyphicon glyphicon-refresh spinning" aria-hidden="true"></i><span ng-show="row.entity.status !=\'Registering\'">{{row.entity.status  }}</span> <span><i  ng-if="row.entity.isSuccessShow || row.entity.isErrorShow"style="color:{{row.entity.status==\'Not Registered\'?\'#DF0000\':\'#71F354\'}};font-size:14px;" class="{{row.entity.status!=\'Not Registered\' ? \'icon-check\' :  \'icon-close\'}}" aria-hidden="true"></i></span></div>'
       // cellTemplate:'<div class="ui-grid-cell-contents text-center" ><span ng-show="row.entity.status ==\'Registering\'"> In Progess <i style="margin:3px auto;"  class="glyphicon glyphicon-refresh spinning" aria-hidden="true"></i></span><span ng-show="row.entity.status !=\'Registering\'">{{row.entity.status }} </span> <img  ng-if="row.entity.isSuccessShow || row.entity.isErrorShow" ng-src="{{row.entity.status!=\'FAILED\' ? \'assets/layouts/layout/img/new_status/COMPLETED.svg\' :  \'assets/layouts/layout/img/new_status/KILLED.svg\'}}"  width="20" height="20"></div>'
      //  cellTemplate:'<div class="ui-grid-cell-contents text-center" ><span ng-show="row.entity.status ==\'Registering\'"> Running <i style="margin:3px auto;"  class="glyphicon glyphicon-refresh spinning" aria-hidden="true"></i></span><span ng-show="row.entity.status !=\'Registering\'">{{row.entity.status }} </span> </div>'
        cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: -2px auto;font-weight: 300;background-color:{{grid.appScope.status[row.entity.status].color}} !important" ng-style="">{{grid.appScope.status[row.entity.status].caption}}</div></div>'


      },
      {
        displayName: 'Compare Status',
        name: 'compareStatus',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
        cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: -2px auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.compareStatus].color}} !important" ng-style="">{{grid.appScope.path[row.entity.compareStatus].caption}}</div></div>'
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
  //   gridApi.selection.on.rowSelectionChanged($scope,function(row){
  //     $scope.selectButtonClick(row.entity);
  //  });
  //  gridApi.selection.on.rowSelectionChangedBatch($scope,function(row){
  //     $scope.selectButtonClick(row.entity);
  //   });

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
    //$scope.getDatasourceByApp();
    $scope.getAllLatestDatasource(); 
    $scope.gridOptions.data=[];
  }

  // $scope.getDatasourceByApp = function () {
  //   $scope.selectDataSource = null;
  //   $scope.allDataSource = null;
  //   RegisterSourceService.getDatasourceByApp($rootScope.appUuid).then(function (response){ onSuccessGetDatasourceByType(response.data)})
  //   var onSuccessGetDatasourceByType = function (response) {
  //     $scope.allDataSource = response
  //   }
  // }
  // $scope.getDatasourceByApp();
  $scope.getAllLatestDatasource = function () {
    $scope.selectDataSource = null;
    $scope.allDataSource = null;
    CommonService.getAllLatest("datasource").then(function (response){ onSuccessGetAllLatest(response.data)})
    var onSuccessGetAllLatest = function (response) {
      $scope.allDataSource = response
    }
  }
  $scope.getAllLatestDatasource(); 
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
  $scope.onChangeStatus=function(){
    
    if($scope.selectDataSource !=null){
      $scope.isSearchDisable=false;
    }else{
      $scope.isSearchDisable=true;
    }
  }
  $scope.searchSource=function(){
    $scope.gridOptions.data=[];
    $scope.isSearchDisable=true;
    $scope.searchButtonText = "Register";
    $scope.isDataSourceInpogress = true;
    RegisterSourceService.getRegistryByDatasource($scope.selectDataSource.uuid,$scope.selectStatus).then(function (response) {onSuccessGetRegistryByDatasource(response.data)}, function (response) {onError(response.data)});
    var onSuccessGetRegistryByDatasource = function (response) {
      $scope.isDataSourceInpogress = false;
      $scope.originalData = response
      $scope.gridOptions.data=response;
      if($scope.selectStatus =="Registered" || response.length ==0)
        $scope.isSelectAllDisabled=true;
      else
        $scope.isSelectAllDisabled=false;
    }
    var onError = function (response) {
      $scope.isDataSourceInpogress = false;
      $scope.isDataError = true;
      $scope.msgclass = "errorMsg";
      $scope.datamessage = "Some Error Occurred";
    }
  }

  // $scope.selectButtonClick=function(row, $event){
  //   console.log($scope.gridApi.selection.getSelectedRows())
  //   if($scope.gridApi.selection.getSelectedRows().length >0){
  //     $scope.isRSDisable=false;
  //   }else{$scope.isRSDisable=true;}
  // }
  $scope.OnSelectAllRow = function() {
    angular.forEach($scope.gridOptions.data, function(source) {
      if(source.status !="Registered")
      source.selected = $scope.selectedAllRow;
    });
    console.log($scope.getSelectedRow());
    if($scope.getSelectedRow().length > 0){
      $scope.isRSDisable=false;
    }else{
      $scope.isRSDisable=true;
      //$scope.selectedAllRow = false
    }
  }
  
  $scope.getSelectedRow= function() {
    //$scope.selectedAllRow = false;
    var newDataList = [];
    angular.forEach($scope.gridOptions.data, function(selected) {
      if (selected.selected) {
        newDataList.push(selected);
      }
    });
    return newDataList;
  }
  $scope.onSelectRow = function(index, data) {
    console.log($scope.getSelectedRow());
    if($scope.getSelectedRow().length > 0){
      $scope.isRSDisable=false;
    }else{
      $scope.isRSDisable=true;
      //$scope.selectedAllRow = false
    }
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

  $scope.okRegiter=function(){
    $('#confExModal').modal('hide');
    var registerSourceArray = [];
    $scope.isRSDisable=true;
    var count = 0;
    $scope.searchButtonText = "Registering";
    //var selectRegisterSoucre=$scope.gridApi.selection.getSelectedRows()
    
    var selectRegisterSoucre=$scope.getSelectedRow();
    for (var i = 0; i < selectRegisterSoucre.length; i++) {
      var registerSourceJson = {};
      if(!$scope.searchtext){
      $scope.gridOptions.data[selectRegisterSoucre[i].id-1].status="Registering"
      }else{
        var index=$scope.getGridOptionsDataIndex(selectRegisterSoucre[i].id)
        if(index!=-1){
        $scope.gridOptions.data[index].status="Registering"
      }
      }
      registerSourceJson.id = selectRegisterSoucre[i].id
      registerSourceJson.name = selectRegisterSoucre[i].name;
      registerSourceJson.dese = selectRegisterSoucre[i].dese;
      registerSourceJson.registeredOn = selectRegisterSoucre[i].registeredOn;
      registerSourceJson.status = selectRegisterSoucre[i].status
      registerSourceArray[count] = registerSourceJson;
      count = count + 1;
    }
    console.log(JSON.stringify(registerSourceArray))
    RegisterSourceService.getRegister($scope.selectDataSource.uuid, $scope.selectDataSource.version, registerSourceArray, $scope.selectDataSource.type).then(function (response) {onSuccessGetcreateAndLoad(response.data)},function (response) {onError(response.data)});
    var onSuccessGetcreateAndLoad = function (response) {
     // console.log(JSON.stringify(response))
      $scope.searchButtonText = "Register";
      $scope.dataLoading = false;
      $scope.selectedAllRow = false;
      for (var i = 0; i < response.length; i++) {
        if(!$scope.searchtext){
          var id = response[i].id - 1
          $scope.gridOptions.data[id].registeredOn = response[i].registeredOn;
          $scope.gridOptions.data[id].desc = response[i].desc;
          $scope.gridOptions.data[id].status = response[i].status;
          $scope.gridOptions.data[id].selected= false;
          $scope.gridOptions.data[id].isDisabled=true;
          $scope.gridOptions.data[id].isSuccessShow=true;
          $scope.gridOptions.data[id].isErrorShow=false;
          $scope.gridOptions.data[id].registeredBy=response[i].registeredBy;
          $scope.gridOptions.data[id].compareStatus=response[i].compareStatus
        }
        else{
          var index=$scope.getGridOptionsDataIndex(selectRegisterSoucre[i].id)
          if(index!=-1){
            $scope.gridOptions.data[index].registeredOn = response[i].registeredOn;
            $scope.gridOptions.data[index].desc = response[i].desc;
            $scope.gridOptions.data[index].status = response[i].status;
            $scope.gridOptions.data[index].selected= false;
            $scope.gridOptions.data[index].isDisabled=true;
            $scope.gridOptions.data[id].isSuccessShow=true;
            $scope.gridOptions.data[id].isErrorShow=false;
            $scope.gridOptions.data[index].registeredBy=response[i].registeredBy;
            $scope.gridOptions.data[index].compareStatus=response[i].compareStatus
        }
        }
        //$scope.gridOptions.data.splice(i,1);
       // $scope.gridApi.selection.unSelectRow($scope.gridOptions.data[id]);
      }
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = 'Datapod Registered Successfully'
      $scope.$emit('notify', notify);
    }
    var onError=function(response){
      $scope.searchButtonText = "Register";
    	var selectRegisterSoucre=$scope.getSelectedRow();
    	$scope.dataLoading = false;
      $scope.dataLoading = false;
      $scope.selectedAllRow = false;
      for (var i = 0; i < selectRegisterSoucre.length; i++) {
        if(!$scope.searchtext){
          var id = selectRegisterSoucre[i].id - 1
          $scope.gridOptions.data[id].status = "FAILED";
          $scope.gridOptions.data[id].selected= false;
          $scope.gridOptions.data[id].isDisabled=false;
          $scope.gridOptions.data[id].isSuccessShow=false;
          $scope.gridOptions.data[id].isErrorShow=true;
        }
        else{
          var index=$scope.getGridOptionsDataIndex(selectRegisterSoucre[i].id)
          if(index!=-1){
            $scope.gridOptions.data[index].status ="FAILED";
            $scope.gridOptions.data[index].selected= false;
            $scope.gridOptions.data[index].isDisabled=false;
            $scope.gridOptions.data[id].isSuccessShow=false;
            $scope.gridOptions.data[id].isErrorShow=true;
          }
        }
      //$scope.gridOptions.data.splice(i,1);
      // $scope.gridApi.selection.unSelectRow($scope.gridOptions.data[id]);
      }

    }
  }

  $scope.submitRegisgterSource = function () {
    $('#confExModal').modal({
      backdrop: 'static',
      keyboard: false
    });
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
