  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailGraphExecController', function($filter, $state, $stateParams, $rootScope, $scope, 
    $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc,CF_META_TYPES) {
    
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;

    $scope.selectTitle = dagMetaDataService.elementDefs[CF_META_TYPES.graphexec.toLowerCase()].caption;
    $scope.state = dagMetaDataService.elementDefs[CF_META_TYPES.graphexec.toLowerCase()].listState + "({type:'" + dagMetaDataService.elementDefs[CF_META_TYPES.graphexec.toLowerCase()].execType + "'})"
    $rootScope.isCommentVeiwPrivlage=true;
    var privileges = privilegeSvc.privileges[CF_META_TYPES.graphexec.toLowerCase()] || [];
	  $rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
	  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
	  $scope.$on('privilegesUpdated', function (e, data) {
		  var privileges = privilegeSvc.privileges[CF_META_TYPES.graphexec.toLowerCase()] || [];
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
        $scope.statedetail.name = dagMetaDataService.elementDefs[CF_META_TYPES.graphexec.toLowerCase()].joblistState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs[CF_META_TYPES.graphexec.toLowerCase()].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }

    JobMonitoringService.getLatestByUuid($scope.uuid,CF_META_TYPES.graphexec).then(function(response) {onSuccess(response.data) });
    var onSuccess = function(response) {
      $scope.execData = response;
      var statusList = [];
      for (i = 0; i < response.statusList.length; i++) {
        d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
        d = d.toString().replace("+0530", "IST");
        statusList[i] = response.statusList[i].stage + "-" + d;
      }
      $scope.statusList = statusList
    }

    $scope.showGraph = function(uuid, version) {
      $scope.showExec = false;
      $scope.showGraphDiv = true;
    }

    $scope.showExecPage = function() {
      $scope.showExec = true
      $scope.showGraphDiv = false;
    }


  });