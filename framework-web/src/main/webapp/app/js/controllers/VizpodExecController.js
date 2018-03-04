JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailVizpodExecController', function($state, $filter, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showvizpodexec = true;
  $scope.state = dagMetaDataService.elementDefs['vizexec'].listState + "({type:'" + dagMetaDataService.elementDefs['vizexec'].execType + "'})"
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['vizexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['vizexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "vizexec").then(function(response) {
    onSuccess(response.data)
  });
  var onSuccess = function(response) {
    $scope.vizpodexecdata = response;
    var refkeylist = [];
    for (i = 0; i < response.refKeyList.length; i++) {
      refkeylist[i] = response.refKeyList[i].type + "-" + response.refKeyList[i].name;

    }
    $scope.refkeylist = refkeylist
  }

  $scope.showLoadGraph = function(uuid, version) {
    $scope.showvizpodexec = false;
    $scope.showgraph = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;

  }
  $scope.showVizpodExecPage = function() {
    $scope.showvizpodexec = true
    $scope.showgraph = false
    $scope.graphDataStatus = false
    $scope.showgraphdiv = false;
  }



});
