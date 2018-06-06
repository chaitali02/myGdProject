  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailUploadExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showuploadexec = true;
    $scope.selectTitle = dagMetaDataService.elementDefs['uploadexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['uploadexec'].listState + "({type:'" + dagMetaDataService.elementDefs['uploadexec'].execType + "'})"
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
        $scope.statedetail.name = dagMetaDataService.elementDefs['uploadexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['uploadexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }
    JobMonitoringService.getLatestByUuid($scope.uuid, "uploadexec").then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      $scope.uploadexecdata = response;
    }

    
    $scope.showGraph = function(uuid, version) {
      $scope.showuploadexec = false;
      $scope.showgraph = false
      $scope.graphDatastatusList = true
      $scope.showgraphdiv = true;

    }

    $scope.showuploadexecPage = function() {
      $scope.showuploadexec = true
      $scope.showgraph = false
      $scope.graphDatastatusList = false
      $scope.showgraphdiv = false;
    }


  });
