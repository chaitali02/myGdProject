JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailPredictExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.selectTitle = dagMetaDataService.elementDefs['predictexec'].caption;

  $scope.state = dagMetaDataService.elementDefs['predictexec'].listState + "({type:'" + dagMetaDataService.elementDefs['modelexec'].execType + "'})"
  $scope.close = function () {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['predictexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['predictexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "predictexec").then(function (response) {
    onSuccess(response.data)
  });
  var onSuccess = function (response) {
    $scope.execData = response;
    var status = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      status[i] = response.statusList[i].stage + "-" + d;
    }
    $scope.status = status
    var refkeylist = [];
    if (response.refKeyList.length > 0) {
      for (i = 0; i < response.refKeyList.length; i++) {
        refkeylist[i] = response.refKeyList[i].type + "-" + response.refKeyList[i].name;
      }
      $scope.refkeylist = refkeylist
      $scope.refkeylistclass = "cross"
    }
  }

  $scope.showGraph = function (uuid, version) {
    $scope.showExec = false;
    $scope.showGraphDiv = true;

  }
  $scope.showExecPage = function () {
    $scope.showExec = true
    $scope.showGraphDiv = false;
  }

});
