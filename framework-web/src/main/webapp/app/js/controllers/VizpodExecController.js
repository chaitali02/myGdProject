JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailVizpodExecController', function ($state, $filter, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.state = dagMetaDataService.elementDefs['vizexec'].listState + "({type:'" + dagMetaDataService.elementDefs['vizexec'].execType + "'})"
  $rootScope.isCommentVeiwPrivlage = true;
  var privileges = privilegeSvc.privileges['comment'] || [];
  $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
  $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
  $scope.$on('privilegesUpdated', function (e, data) {
    var privileges = privilegeSvc.privileges['comment'] || [];
    $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
    $rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
  });
  $scope.userDetail = {}
  $scope.userDetail.uuid = $rootScope.setUseruuid;
  $scope.userDetail.name = $rootScope.setUserName;
  $scope.close = function () {
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
  JobMonitoringService.getLatestByUuid($scope.uuid, "vizexec").then(function (response) {
    onSuccess(response.data)
  });
  var onSuccess = function (response) {
    $scope.execData = response;
    var dependsOnlist = [];
    if (response.dependsOn != null) {
      var dependsOn = {};
      dependsOn.type = response.dependsOn.ref.type;
      dependsOn.name = response.dependsOn.ref.type + "-"+response.dependsOn.ref.name;
      dependsOn.uuid = response.dependsOn.ref.uuid;
      dependsOn.version = response.dependsOn.ref.version;
      dependsOnlist[0] = dependsOn;
    }
    $scope.dependsOnlist=dependsOnlist;
  }

  $scope.showGraph = function (uuid, version) {
    $scope.showExec = false;
    $scope.showgraph = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;
  }
  $scope.onShowDetailDepOn=function(data){
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['vizexec'].detailState;
    $rootScope.previousState.params = {};
    $rootScope.previousState.params.id = $stateParams.id;
    $rootScope.previousState.params.mode = true;
    var type = data.type
    var uuid = data.uuid
    var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
    var stageparam = {};
    stageparam.id = uuid;
    stageparam.version = data.version;
    stageparam.mode = true;
    stageparam.returnBack = true;
    $state.go(stageName, stageparam);
}

  $scope.showExecPage = function () {
    $scope.showExec = true
    $scope.showgraph = false
    $scope.graphDataStatus = false
    $scope.showgraphdiv = false;
  }
  $scope.showSqlFormater = function () {
    $('#sqlFormaterModel').modal({
      backdrop: 'static',
      keyboard: false
    });
    $scope.formateSql = sqlFormatter.format($scope.execData.sql);
  }


});
