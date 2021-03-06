  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailModelExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {

    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showmodelexec = true;
    $scope.selectTitle = dagMetaDataService.elementDefs['modelexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['modelexec'].listState + "({type:'" + dagMetaDataService.elementDefs['modelexec'].execType + "'})"
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
        $scope.statedetail.name = dagMetaDataService.elementDefs['modelexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['modelexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }
    JobMonitoringService.getLatestByUuid($scope.uuid, "modelexec").then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      $scope.modelexecdata = response;
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

    $scope.showLoadGraph = function(uuid, version) {
      $scope.showmodelexec = false;
      $scope.showgraph = false
      $scope.graphDataStatus = true
      $scope.showgraphdiv = true;

    }
    $scope.showModelExecPage = function() {
      $scope.showmodelexec = true
      $scope.showgraph = false
      $scope.graphDataStatus = false
      $scope.showgraphdiv = false;
    }
    
  });
