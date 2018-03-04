SystemMonitoringModule = angular.module('SystemMonitoringModule');

SystemMonitoringModule.controller('systemMonitoringController', function($filter,$state,$stateParams, $rootScope, $scope, $sessionStorage,dagMetaDataService,CommonService,SystemMonitoringService,$window,$timeout) {
  $scope.autorefreshcounter=05;
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
};
  $scope.mode=$scope.showDiv=='true'?'graph':'all';
  $scope.selectType=$stateParams.type;

  $scope.tabs = [
    { caption:'Sessions',type:'session'},
    { caption:'Jobs',type:'jobs' },
    { caption:'Threads',type:'threads'}
  ];
  if($scope.tabs.type=="jobs")
  {
    $scope.isToggleShow=true
  }
  else{
    $scope.isToggleShow=false
  }


  $scope.getdata = function(response){
    $scope.graphData=[]
    if(response.mode=="all"){
    $scope.gridOptions.data=null
      $scope.gridOptions.data = response.data;
      $scope.originalData=response.data
    }
    else if(response.mode=="graph"){
      $scope.graphData=null;
      $scope.graphData=response.data
    }
    else{
      $scope.refreshRowData(response.data)
    }
    }
    $scope.fullScreen=function(index){
      if($scope.graphData[index].showtooltiptitle =="Compress"){
        for(i=0;i<$scope.graphData.length;i++){
         $scope.graphData[i].show=true;
         $scope.graphData[index].showtooltiptitle="Expand";
         $scope.graphData[index].iconClass="fa fa-expand";
         $scope.graphData[index].width="";
        }
      }else{
      for(i=0;i<$scope.graphData.length;i++){
       $scope.graphData[i].show=false;
      }
      $scope.graphData[index].show=true;
      $scope.graphData[index].showtooltiptitle="Compress";
      $scope.graphData[index].iconClass="fa fa-compress";
      $scope.graphData[index].width="100%";
    }
    $timeout(function() {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
    }
    $scope.collapseExpend=function (index) {
      $scope.graphData[index].colExp=!$scope.graphData[index].colExp
      $timeout(function() {
        $window.dispatchEvent(new Event("resize"));
      }, 100);
    }
    $scope.refreshData = function(searchtext) {
      $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
    };


    $scope.gridOptions=dagMetaDataService.gridOptionsDefault;
    $scope.gridOptions.onRegisterApi = function(gridApi){
      $scope.gridApi = gridApi;
      $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };
    if(typeof $stateParams.type !="undefined"){
      if($stateParams.type=='session'){
        $scope.tabActive=0;
      }
      else if($stateParams.type=='jobs'){
        $scope.tabActive=1;
      }
      else{
          $scope.tabActive=2;
      }
    }
    $scope.select=function (type) {
      var parm={}
      parm.type=type
      $state.go('systemmonitering',parm);
      $scope.selectType=type;
      $scope.killSession = function(data) {
        console.log(JSON.stringify(data))
        CommonService.getOneByUuidAndVersion(data.uuid,data.version,"session").then(function(response){onSuccessGetLatestByUuid(response.data)});
        var onSuccessGetLatestByUuid=function(response){
          $scope.sessionId=response.sessionId
          SystemMonitoringService.killSession($scope.sessionId).then(function(response) {
            onSuccesskillSession(response.data)
          });
          var onSuccesskillSession = function (response) {
            notify.type='success',
            notify.title= 'Success',
            notify.content='Session Killed Successfully'
            $scope.$emit('notify', notify);
          }
        }


      }
      $scope.action = function(data, mode) {

        $scope.setActivity(data.uuid,data.version,dagMetaDataService.elementDefs[data.type.toLowerCase()].execType,mode);
        var stateName = dagMetaDataService.elementDefs[data.type.toLowerCase()].detailState;
        $rootScope.previousState={};
        $rootScope.previousState.name='systemmonitering';
        $rootScope.previousState.params={};
        $rootScope.previousState.params.type=type;
        if (stateName)
          $state.go(stateName, {
            id: data.uuid,
            version: data.version,
            mode: true,
            returnBack:true
          });
      }
      $scope.setActivity=function (uuid,version,type,action) {
         CommonService.setActivity(uuid,version,type,action).then(function(response){onSuccessSetActivity(response.data)});
         var onSuccessSetActivity=function (response) {
         }
      }
      if(type =="session"){

      //$scope.gridOptions.columnDefs[0].displayName='User';
      //$scope.gridOptions.columnDefs[0].name= 'createdBy.ref.name';
      $scope.gridOptions.columnDefs=[
        {
          displayName: 'UUID',
          name: 'uuid',
          minWidth: 250,
          visible: false,
          cellClass: 'text-center',
          headerCellClass: 'text-center'
        },
        {
          displayName: 'Application',
          name: 'appInfo[0].ref.name',
          minWidth: 250,
          headerCellClass: 'text-center'
        },
        {
          displayName: 'Version',
          name: 'version',
          visible: false,
          cellClass: 'text-center',
          headerCellClass: 'text-center'
        },
        {
          displayName: 'User',
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
        {
          displayName: 'Status',
          name: 'status',
          cellClass: 'text-center',
          headerCellClass: 'text-center'
        },
        {
          displayName: 'Action',
          name: 'action',
          cellClass: 'text-center',
          headerCellClass: 'text-center',
          maxWidth: 130,
          cellTemplate: [
            '<div class="ui-grid-cell-contents">',
            '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
            '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
            '    <i class="fa fa-angle-down"></i></button>',
            '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
            '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.action(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
            '    <li ng-disabled="grid.appScope.privileges.indexOf(\'kill\') == -1" ><a ng-click="grid.appScope.killSession(row.entity)"><i class="fa fa-times" aria-hidden="true"></i> Kill</a></li>',
            '  </div>',
            '</div>'
          ].join('')
        }
      ]
      }
      else if(type =="jobs"){
        $scope.path=dagMetaDataService.statusDefs
        $scope.gridOptions.columnDefs=[
          {
            displayName: 'UUID',
            name: 'uuid',
            minWidth: 250,
            visible: false,
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Application',
            name: 'appInfo[0].ref.name',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Meta',
            name: 'type',
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Name',
            name: 'name',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Version',
            name: 'version',
            visible: false,
            cellClass: 'text-center',
            headerCellClass: 'text-center'
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
          {
            displayName: 'Status',
            name: 'status',
            maxWidth: 100,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><img title="{{row.entity.status}}" ng-src=\"{{grid.appScope.path[row.entity.status].iconPath}}\" border=\"0\" width=\"15\">'
          },
          {
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
              '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.action(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
              '  </div>',
              '</div>'
            ].join('')
          }
        ]
        // $scope.gridOptions.columnDefs.splice(2,1);
        // $scope.gridOptions.columnDefs.splice(2, 0,
        //   {
        //     displayName: 'Name',
        //     name: 'name',
        //     cellClass: 'text-center',
        //     headerCellClass: 'text-center'
        //   }
        // );
        // $scope.gridOptions.columnDefs[1].displayName='Type';
        // $scope.gridOptions.columnDefs[1].name= 'type';
        // $scope.gridOptions.columnDefs[3].displayName='Created By';


      }
      else{
        $scope.gridOptions.columnDefs=[
          {
            displayName: 'UUID',
            name: 'uuid',
            minWidth: 250,
            visible: false,
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Application',
            name: 'appInfo[0].ref.name',
            headerCellClass: 'text-center'
          },
          {
        	displayName: 'Meta',
            name: 'execInfo.ref.type',
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Name',
            name: 'name',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Version',
            name: 'version',
            visible: false,
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          }]
      }
    }
    $scope.gridOptions.data = [];
    $scope.getGridStyle = function() {
      var style = {
        'margin-top': '10px',
        'margin-bottom': '10px',
      }
      if ($scope.filteredRows && $scope.filteredRows.length >0) {
        style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
      }
      else{
        style['height']="100px";
      }
      return style;
    }
    var myVar;
   $scope.autoRefreshOnChange=function (status) {
     if(status){
       myVar = setInterval(function(){
          $rootScope.refreshRowData1("row")
        //  $scope.refreshRowData1();

        }, $scope.autorefreshcounter+"000");
     }
     else{
       clearInterval(myVar);
     }
   }
   $scope.$on('$destroy', function() {
    // Make sure that the interval is destroyed too
    clearInterval(myVar);
   })
   $scope.refreshRowData=function (response) {
     $scope.data=response
     if($scope.data.length >0){
       $scope.gridOptions.data=$scope.data;
       $scope.$watch('data',function(newValue, oldValue) {
         if(newValue != oldValue  && $scope.gridOptions.data.length > 0) {

         for(var i=0;i<$scope.data.length;i++) {
           if($scope.data[i].status != $scope.gridOptions.data[i].status){
              $scope.gridOptions.data[i].status=$scope.data[i].status;
            }
         }
         }
         else{
           $scope.gridOptions.data =[];
           $scope.gridOptions.data=$scope.data;
         }
       },true);
     }
     else{
       $scope.gridOptions.data =[];
       $scope.gridOptions.data=$scope.data;
     }
   }
   $scope.savePng=function (index) {
      let svg = document.querySelector('#chart'+index).querySelector('svg');
     saveSvgAsPng(svg, 'svg.png',{"backgroundColor":"white"});
   }
});
