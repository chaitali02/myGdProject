JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailProfileExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {
  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.selectTitle = dagMetaDataService.elementDefs['profileexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['profileexec'].listState + "({type:'" + dagMetaDataService.elementDefs['profileexec'].execType + "'})"
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
    }
    else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['profileexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['profileexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "profileexec").then(function (response) { onSuccess(response.data) });
  var onSuccess = function (response) {
    $scope.execData = response;
    var statusList = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    $scope.statusList = statusList;
  }

  $scope.showGraph = function (uuid, version) {
    $scope.showExec = false;
    $scope.showGraphDiv = true;

  }

  $scope.showExecPage = function () {
    $scope.showExec = true
    $scope.showGraphDiv = false;
  }
  $scope.showSqlFormater=function(){
    $('#sqlFormaterModel').modal({
      backdrop: 'static',
      keyboard: false
    });
    $scope.formateSql=sqlFormatter.format($scope.execData.exec);
  }


});
