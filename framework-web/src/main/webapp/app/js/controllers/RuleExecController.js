JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailRuleExecController', function($state, $filter, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {

  $scope.uuid = $stateParams.id;
  $scope.mode = $stateParams.mode;
  $scope.showruleexec = true;
  $scope.selectTitle = dagMetaDataService.elementDefs['ruleexec'].caption;
  $scope.state = dagMetaDataService.elementDefs['ruleexec'].listState + "({type:'" + dagMetaDataService.elementDefs['ruleexec'].execType + "'})"
  $rootScope.isCommentVeiwPrivlage=true;
  var privileges = privilegeSvc.privileges['comment'] || [];
  $rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
  $scope.$on('privilegesUpdated', function (e, data) {
    var privileges = privilegeSvc.privileges['comment'] || [];
    $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
    $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;	
  });
  $scope.userDetail={}
  $scope.userDetail.uuid= $rootScope.setUseruuid;
  $scope.userDetail.name= $rootScope.setUserName;
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['ruleexec'].listState
      $scope.statedetail.params = {}
      $scope.statedetail.params.type = dagMetaDataService.elementDefs['ruleexec'].execType;
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  JobMonitoringService.getLatestByUuid($scope.uuid, "ruleexec").then(function(response) {
    onSuccess(response.data)
  });
  var onSuccess = function(response) {
    $scope.ruleexecdata = response;
    var statusList = [];
    for (i = 0; i < response.statusList.length; i++) {
      d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    $scope.statusList = statusList
    var refkeylist = [];
    for (i = 0; i < response.refKeyList.length; i++) {
      refkeylist[i] = response.refKeyList[i].type + "-" + response.refKeyList[i].name;
    }
    $scope.refkeylist = refkeylist
  }

  $scope.showLoadGraph = function(uuid, version) {
    $scope.showruleexec = false;
    $scope.showgraph = false
    $scope.graphDatastatusList = true
    $scope.showgraphdiv = true;

  }

  $scope.showRuleExecPage = function() {
    $scope.showruleexec = true
    $scope.showgraph = false
    $scope.graphDatastatusList = false
    $scope.showgraphdiv = false;
  }



});
