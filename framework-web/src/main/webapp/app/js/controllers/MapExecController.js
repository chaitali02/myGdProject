JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailMapExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showmapexec = true;
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

  JobMonitoringService.getLatestByUuid($scope.uuid, "mapexec").then(function (response) {
    onSuccess(response.data)
  });
  var onSuccess = function (response) {
    $scope.mapexecdata = response;
   
    var statusList = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }

    $scope.statusList = statusList
    var refkeylist = [];
    if (response.refKeyList.length > 0) {
      for (j = 0; j < response.refKeyList.length; j++) {
        refkeylist[j] = response.refKeyList[j].type + "-" + response.refKeyList[j].name;

      }
      $scope.refkeylist = refkeylist
      $scope.refkeylistclass = "cross"
    }
  }


  $scope.showLoadGraph = function (uuid, version) {
    $scope.showmapexec = false;
    $scope.showgraph = false
    $scope.graphDatastatusList = true
    $scope.showgraphdiv = true;
    var newUuid = uuid + "_" + version;



  }
  $scope.showMapExecPage = function () {
    $scope.showmapexec = true
    $scope.showgraph = false
    $scope.graphDatastatusList = false
    $scope.showgraphdiv = false;
  }
  
  $scope.showSqlFormater=function(){
    $('#sqlFormaterModel').modal({
      backdrop: 'static',
      keyboard: false
    });
    $scope.formateSql=sqlFormatter.format($scope.mapexecdata.exec);
  }



});
