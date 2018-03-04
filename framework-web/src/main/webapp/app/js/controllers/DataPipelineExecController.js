JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDataPipelineExecController', function($filter, $stateParams, $rootScope, $state, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showdagexec = true;
  $scope.selectTitle = dagMetaDataService.elementDefs['dagexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['dagexec'].listState + "({type:'" + dagMetaDataService.elementDefs['dagexec'].execType + "'})"
  $scope.onTaskShowDetail = function(data) {
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['dagexec'].detailState;
    $rootScope.previousState.params = {};
    $rootScope.previousState.params.id = $stateParams.id;
    $rootScope.previousState.params.mode = true;
    var type = data.operators[0].operatorInfo.ref.type
    var uuid = data.operators[0].operatorInfo.ref.uuid
    var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
    var stageparam = {};
    stageparam.id = uuid;
    stageparam.mode = true;
    stageparam.returnBack = true;
    $state.go(stageName, stageparam);
  }
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['dagexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['dagexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }

  JobMonitoringService.getLatestByUuid($scope.uuid, "dagexec").then(function(response) {
    onSuccess(response.data)
  });
  var onSuccess = function(response) {

    $scope.dagexecdata = response;
    var statusList = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    $scope.statusList = statusList

  }

  $scope.expandAll = function(expanded) {
    $scope.$broadcast('onExpandAll', {
      expanded: expanded
    });
  };

  $scope.getStagestatusList=function (index,statusList) {
      $scope.dagexecdata.stages[index].imgPath=dagMetaDataService.statusDefs[statusList].iconPath;
  }
  $scope.getTaskstatusList=function (parentIndex,index,statusList) {
      //alert(statusList)
      $scope.dagexecdata.stages[parentIndex].tasks[index].imgPathTask=dagMetaDataService.statusDefs[statusList].iconPath;
      //alert($scope.dagexecdata.stages[parentIndex].tasks[index].imgPathTask)
  }
  $scope.showLoadGraph = function(uuid, version) {
    $scope.showdagexec = false;
    $scope.showgraph = false
    $scope.graphDatastatusList = true
    $scope.showgraphdiv = true;

  }

  $scope.showDagExecPage = function() {
    $scope.showdagexec = true
    $scope.showgraph = false
    $scope.graphDatastatusList = false
    $scope.showgraphdiv = false;
  }

});
