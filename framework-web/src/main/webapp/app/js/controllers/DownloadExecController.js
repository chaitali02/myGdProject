  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailDownloadExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showdownloadexec = true;
   
    $scope.selectTitle = dagMetaDataService.elementDefs['downloadexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['downloadexec'].listState + "({type:'" + dagMetaDataService.elementDefs['downloadexec'].execType + "'})"
    $rootScope.isCommentVeiwPrivlage=true;
    $scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
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
        $scope.statedetail.name = dagMetaDataService.elementDefs['downloadexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['downloadexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }
    JobMonitoringService.getLatestByUuid($scope.uuid, "downloadexec").then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      $scope.downloadexecdata = response;
    }

    
    $scope.showGraph = function(uuid, version) {
      $scope.showdownloadexec = false;
      $scope.showgraph = false
      $scope.graphDatastatusList = true
      $scope.showgraphdiv = true;

    }

    $scope.showdownloadexecPage = function() {
      $scope.showdownloadexec = true
      $scope.showgraph = false
      $scope.graphDatastatusList = false
      $scope.showgraphdiv = false;
    }


  });
