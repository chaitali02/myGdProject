JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailRuleExecController', function ($state, $filter, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showExec = true;
  $scope.isEditInprogess=true;
  $scope.isEditVeiwError=false;
  $scope.selectTitle = dagMetaDataService.elementDefs['ruleexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['ruleexec'].listState + "({type:'" + dagMetaDataService.elementDefs['ruleexec'].execType + "'})"
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
      if($rootScope.previousState.name!="resultgraphwf"){
        var stateTabPrevious={};
        stateTabPrevious.route=dagMetaDataService.elementDefs['ruleexec'].detailState;;
        stateTabPrevious.param={};
        stateTabPrevious.param.id = $stateParams.id;
        stateTabPrevious.param.mode = true;
        stateTabPrevious.param.returnBack = true;
        stateTabPrevious.param.name = $scope.execData.name;
        stateTabPrevious.param.version = $scope.execData.version;
        stateTabPrevious.param.type = dagMetaDataService.elementDefs['ruleexec'].execType;
        stateTabPrevious.active=false;
        var stateTabNew={};
        stateTabNew.route=$rootScope.previousState.name;
        stateTabNew.param= $rootScope.previousState.params;
        stateTabNew.active=false;
        $rootScope.$broadcast('isTabAvailable',stateTabPrevious,stateTabNew);
      }
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['ruleexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['ruleexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "ruleexec")
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
    for (i = 0; i < response.refKeyList.length; i++) {
      refkeylist[i] = response.refKeyList[i].type + "-" + response.refKeyList[i].name;
    }
    $scope.refkeylist = refkeylist*/
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

  $scope.onShowDetail = function (data) { 
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['ruleexec'].detailState;
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
  $scope.onShowDetailDepOn=function(data){
    $rootScope.previousState = {};
    $rootScope.previousState.name = dagMetaDataService.elementDefs['ruleexec'].detailState;
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
    $scope.showGraphDiv = true
  }

  $scope.showExecPage = function () {
    $scope.showExec = true
    $scope.showGraphDiv = false
  }
  $scope.showSqlFormater=function(){
    $('#sqlFormaterModel').modal({
      backdrop: 'static',
      keyboard: false
    });
    $scope.formateSql=sqlFormatter.format($scope.execData.exec);
  }

  $scope.showSqlFormaterSummary=function(){
    if($scope.execData.summaryExec !=null){
      $('#sqlFormaterModel').modal({
        backdrop: 'static',
        keyboard: false
      });
      $scope.formateSql=sqlFormatter.format($scope.execData.summaryExec);
    }
  }

});
