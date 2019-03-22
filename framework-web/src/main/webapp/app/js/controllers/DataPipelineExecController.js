JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDataPipelineExecController', function ($filter, $stateParams, $rootScope, $state, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
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
  $scope.isEditInprogess=true;
  $scope.isEditVeiwError=false;
  $scope.showExec = true;
  $scope.selectTitle = dagMetaDataService.elementDefs['dagexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['dagexec'].listState + "({type:'" + dagMetaDataService.elementDefs['dagexec'].execType + "'})"
  
  $scope.onTaskShowDetail = function (data) {
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['dagexec'].detailState;
    $rootScope.previousState.params = {};
    $rootScope.previousState.params.id = $stateParams.id;
    $rootScope.previousState.params.mode = true;
    $rootScope.previousState.params.returnBack = true;
    $rootScope.previousState.params.name = $scope.execData.name;
    $rootScope.previousState.params.version = $scope.execData.version;
    $rootScope.previousState.params.type = "dagexec";
    var stateTabPrevious={};
    stateTabPrevious.route=$rootScope.previousState.name;
    stateTabPrevious.param= $rootScope.previousState.params;
    stateTabPrevious.active=false;
    var type = data.operators[0].operatorInfo[0].ref.type
    var uuid = data.operators[0].operatorInfo[0].ref.uuid
    var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
    var stageparam = {};
    stageparam.id = uuid;
    stageparam.mode = true;
    stageparam.returnBack = true;
    stageparam.name=data.operators[0].operatorInfo[0].ref.name;
    stageparam.type=data.operators[0].operatorInfo[0].ref.type;
    stageparam.version=data.operators[0].operatorInfo[0].ref.version;
    $state.go(stageName, stageparam);
    var stateTabNew={};
    stateTabNew.route=stageName;
    stateTabNew.param= stageparam;
    stateTabNew.active=false;
    $rootScope.$broadcast('isTabAvailable',stateTabPrevious,stateTabNew);

  }
  $scope.close = function () {
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

  JobMonitoringService.getLatestByUuid($scope.uuid, "dagexec")
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
    $scope.statusList = statusList;
    var dependsOnlist = [];
    if (response.dependsOn != null) {
        var dependsOn = {};
        dependsOn.type = response.dependsOn.type;
        dependsOn.name = response.dependsOn.type + "-"+response.dependsOn.name;
        dependsOn.uuid = response.dependsOn.uuid;
        dependsOn.version = response.dependsOn.version;
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
    $rootScope.previousState.name = dagMetaDataService.elementDefs['dagexec'].detailState;
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
  $scope.expandAll = function (expanded) {
    $scope.$broadcast('onExpandAll', {
      expanded: expanded
    });
  };

  $scope.getStagestatusList = function (index, statusList) {
    $scope.execData.stages[index].imgPath = dagMetaDataService.statusDefs[statusList].iconPath;
  }
  $scope.getTaskstatusList = function (parentIndex, index, statusList) {
    $scope.execData.stages[parentIndex].tasks[index].imgPathTask = dagMetaDataService.statusDefs[statusList].iconPath;
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
