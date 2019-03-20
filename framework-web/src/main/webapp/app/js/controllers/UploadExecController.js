JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailUploadExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {
  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.isEditInprogess=true;
  $scope.isEditVeiwError=false;
  $scope.selectTitle = dagMetaDataService.elementDefs['uploadexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['uploadexec'].listState + "({type:'" + dagMetaDataService.elementDefs['uploadexec'].execType + "'})"
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
      $scope.statedetail.name = dagMetaDataService.elementDefs['uploadexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['uploadexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "uploadexec")
  .then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
  var onSuccess = function (response) {
    $scope.isEditInprogess=false;
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

  var onError=function(){
    $scope.isEditInprogess=false;
    $scope.isEditVeiwError=true;
  }

  $scope.onShowDetailDepOn=function(data){
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['uploadexec'].detailState;
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
  $scope.showGraph = function (uuid, version) {
    $scope.showExec = false;
    $scope.showgraph = false;
  }

  $scope.showExecPage = function () {
    $scope.showExec = true;
    $scope.showgraph = false;
  }


});
