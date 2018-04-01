  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailLoadExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService) {
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;

    $scope.selectTitle = dagMetaDataService.elementDefs['loadexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['loadexec'].listState + "({type:'" + dagMetaDataService.elementDefs['loadexec'].execType + "'})"
    $scope.close = function() {
      if ($stateParams.returnBack == "true" && $rootScope.previousState) {
        //revertback
        $state.go($rootScope.previousState.name, $rootScope.previousState.params);
      } else {
        $scope.statedetail = {};
        $scope.statedetail.name = dagMetaDataService.elementDefs['loadexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['loadexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }

    JobMonitoringService.getLatestByUuid($scope.uuid, "loadexec").then(function(response) {onSuccess(response.data) });
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
