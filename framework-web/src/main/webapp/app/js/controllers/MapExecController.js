JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailMapExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.isEditInprogess=true;
  $scope.isEditVeiwError=false;
  $scope.state = dagMetaDataService.elementDefs['mapexec'].listState + "({type:'" + dagMetaDataService.elementDefs['mapexec'].execType + "'})"
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
      $scope.statedetail.name = dagMetaDataService.elementDefs['mapexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['mapexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }

  JobMonitoringService.getLatestByUuid($scope.uuid, "mapexec")
  .then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
  var onSuccess = function (response) {
    $scope.isEditInprogess=false;
    $scope.execData = response;
    var statusList = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }

    $scope.statusList = statusList
    /*var refkeylist = [];
    if (response.refKeyList.length > 0) {
      for (j = 0; j < response.refKeyList.length; j++) {
        refkeylist[j] = response.refKeyList[j].type + "-" + response.refKeyList[j].name;

      }
      $scope.refkeylist = refkeylist
      $scope.refkeylistclass = "cross"
    }*/
    var refkeylist = [];
    if (response.refKeyList != null) {
        for (i = 0; i < response.refKeyList.length; i++) {
            var refkey = {};
            refkey.type = response.refKeyList[i].type;
            refkey.name = response.refKeyList[i].type + "-"+response.refKeyList[i].name;
            refkey.uuid = response.refKeyList[i].uuid;
            refkey.version = response.refKeyList[i].version;
            refkeylist[i] = refkey;
        }
    }
    $scope.refkeylist = refkeylist;
  };
  var onError=function(){
    $scope.isEditInprogess=false;
    $scope.isEditVeiwError=true;
  }
  
  $scope.onShowDetail = function (data) { 
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['mapexec'].detailState;
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
  };

  $scope.showGraph = function (uuid, version) {
    $scope.showExec = false;
    $scope.showGraphDiv = true;
  }

  $scope.showExecPage = function () {
    $scope.showExec = true
    $scope.showGraphDiv = false;
  }

  $scope.showSqlFormater = function () {
    $('#sqlFormaterModel').modal({
      backdrop: 'static',
      keyboard: false
    });
    $scope.formateSql = sqlFormatter.format($scope.execData.exec);
  }



});
