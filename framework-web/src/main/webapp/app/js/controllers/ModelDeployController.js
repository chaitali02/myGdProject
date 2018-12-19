DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('ModelDeployController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, ModelDeployService, privilegeSvc,$filter,$timeout,dagMetaDataService,uiGridConstants) {
    

    $scope.iSSubmitEnable=true;
    $scope.gridOptions={};
    $scope.gridOptions = dagMetaDataService.gridOptionsDefault;
    $scope.privileges = privilegeSvc.privileges['trainExec'] || [];
    $scope.isPrivlageDeploy = $scope.privileges.indexOf('Deploy') == -1;
    $scope.isPrivlageUnDeploy = $scope.privileges.indexOf('Undeploy') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['trainExec'] || [];
        $scope.isPrivlageDeploy = $scope.privileges.indexOf('Deploy') == -1;
        $scope.isPrivlageUnDeploy = $scope.privileges.indexOf('Undeploy') == -1;
	});
    
    $scope.gridOptions.columnDefs=[];
    $scope.gridOptions.columnDefs=[
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
            minWidth: 100,
            headerCellClass: 'text-center',
            cellTemplate:'<div class="grid-tooltip" title="{{row.entity.name}}" ><div class="ui-grid-cell-contents">{{ COL_FIELD }}</div></div>',
            
        },
        {
            displayName: 'Version',
            name: 'version',
            visible: false,
            maxWidth:110,
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
            maxWidth:100,
            headerCellClass: 'text-center'
        },
        {
            displayName: 'Created On',
            visible: false,
            name: 'createdOn',
            minWidth: 220,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
  
        },
        {
            displayName: 'Accuracy',
            visible: true,
            name: 'trainResultView.accuracy',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
  
        },
        {
            displayName: 'F1 Score',
            visible: true,
            name: 'trainResultView.f1Score',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
  
        },
        {
            displayName: 'Recall',
            visible: true,
            name: 'trainResultView.recall',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
  
        },
        {
            displayName: 'State',
            visible: true,
            name: 'state',
            minWidth: 100,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div>{{row.entity.deployExec ==null?\'Not Deployed\':\'Deployed\'}}</div></div>'
        },
        {
            displayName: 'Status',
            name: 'status',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            maxWidth: 100,
            cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{row.entity.status}}</div></div>'
      
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
            //   '       <li><a  ng-show ="grid.appScope.newType.indexOf(\'ingestExec\') != -1?false:true" ng-disabled="grid.appScope.newType.indexOf(\'batchexec\')!=-1?[\'Completed\',\'Not Started\',\'Terminating\',\'Failed\',\'In Progress\',\'Killed\'].indexOf(row.entity.status)==-1:grid.appScope.newType.indexOf(\'train\')!=-1?[\'Completed\'].indexOf(row.entity.status)==-1:grid.appScope.newType.indexOf(\'dagexec\')!=-1?[\'Completed\',\'Not Started\',\'Terminating\',\'Failed\',\'In Progress\',\'Killed\'].indexOf(row.entity.status)==-1:grid.appScope.newType.indexOf(\'group\')==-1?[\'Completed\',\'Killed\'].indexOf(row.entity.status)==-1:[\'Completed\',\'In Progress\',\'Killed\',\'Failed\',\'Terminating\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.getExec(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
            //   '       <li><a  ng-show="grid.appScope.newType.indexOf(\'ingestExec\') != -1?true:false" ng-disabled="true"  ng-click="grid.appScope.getExec(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
            //   '       <li><a ng-disabled="grid.appScope.privileges.indexOf(\'Execute\') == -1 || [\'In Progress\',\'Resume\'].indexOf(row.entity.status)==-1"  ng-click="grid.appScope.setStatus(row.entity,\'Killed\')"><i class="fa fa-times" aria-hidden="true"></i> Kill </a></li>',
            //   '       <li><a ng-disabled="[\'Killed\',\'Failed\'].indexOf(row.entity.status)==-1 || grid.appScope.privileges.indexOf(\'Execute\') == -1"  ng-click="grid.appScope.restartExec(row.entity)"><i class="fa fa-repeat" aria-hidden="true"></i> Restart </a></li>',
              '          <li><a ng-disabled="grid.appScope.privileges.indexOf(\'Deploy\') == -1"  ng-if="row.entity.deployExec ==null" ng-click="grid.appScope.setState(row.entity)"><i class="fa fa-plus-square-o" aria-hidden="true"></i>Deploy</a></li>',
              '          <li><a ng-disabled="grid.appScope.privileges.indexOf(\'Undeploy\') == -1" ng-if="row.entity.deployExec !=null" ng-click="grid.appScope.setState(row.entity)"><i class="fa fa-minus-square-o" aria-hidden="true"></i>Un Deploy</a></li>',
              '    </ul>',
              '  </div>',
              '</div>'
            ].join('')
        }
    ]
   
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

    $scope.reset=function(){
        $scope.selectedModel={};
        $scope.allModel=null;
        setTimeout(function(){
            $scope.selectedModel=null;
            $scope.getAllLetestModel();
        },100)
        $scope.trainInfoTags=[];
        $scope.gridOptions.data=[];
        $scope.iSSubmitEnable=true;
    }

    $scope.gridOptions.onRegisterApi = function (gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };

    $scope.refreshData = function () {
        $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);

    };
     
    $scope.getAllLetestModel = function () {
        ModelDeployService.getAllLatest("model").then(function (response) { onGetAllLatest(response.data) });
        var onGetAllLatest = function (response) {
          $scope.allModel = response;
          $scope.iSSubmitEnable=false;
        }
    };

    $scope.getAllLetestModel();
    $scope.getTrainExecByModel=function(uuid,version){
        ModelDeployService.getTrainExecByModel(uuid, version, "model").then(function (response) { onGetTrainExecByModel(response.data) });
        var onGetTrainExecByModel = function (response) {
            var trainExecInfo=[];
            $scope.iSSubmitEnable=false;
            if(response && response.length >0){
                for(var i=0;i<response.length; i++){
                 var execInfo={};
                 execInfo.uuid=response[i].uuid;
                 execInfo.version=response[i].version;
                 execInfo.name=response[i].name;
                 execInfo.displayname=response[i].createdOn;
                 trainExecInfo[i]=execInfo;                                 
                }
            }

          $scope.allTrainExecInfo = trainExecInfo;
        }
    }
    $scope.onChangeModel=function(){
        if($scope.selectedModel){
            $scope.trainInfoTags=[];
            $scope.gridOptions.data=[];
            $scope.isInprogess=false;
            $scope.getTrainExecByModel($scope.selectedModel.uuid,$scope.selectedModel.version);
        }else{
            $scope.trainInfoTags=[];
            $scope.gridOptions.data=[];
            $scope.iSSubmitEnable=true;
        }
    };

    $scope.loadTrainInfo = function (query) {
        return $timeout(function () {
          return $filter('filter')($scope.allTrainExecInfo, query);
        });
    }
   
    $scope.search=function(isShowProgess){
        var trainInfoTags=[];
        $scope.iSSubmitEnable=true;
        if(isShowProgess){
            $scope.isInprogess=true;
        }
        
        if($scope.trainInfoTags && $scope.trainInfoTags.length >0){
            for(var i=0;i<$scope.trainInfoTags.length;i++){
                trainInfoTags[i]=$scope.trainInfoTags[i].uuid;
            }
            ModelDeployService.getTrainExecViewByCriteria($scope.selectedModel.uuid, $scope.selectedModel.version, "model",trainInfoTags.toString()).then(function (response) { onGetTrainExecByModel(response.data) });
            var onGetTrainExecByModel = function (response) {
             console.log(response);
             $scope.isInprogess=false;
             $scope.gridOptions.data=response;
           }
        }else{
            $scope.isInprogess=false;
            $scope.iSSubmitEnable=false;

        }
    };

    $scope.refreshResult=function(){
        $scope.search(false);
    }
})
