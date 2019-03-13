JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDqExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {
    $rootScope.isCommentVeiwPrivlage = true;
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.selectTitle = dagMetaDataService.elementDefs['dqexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['dqexec'].listState + "({type:'" + dagMetaDataService.elementDefs['dqexec'].execType + "'})"
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
        }
        else {
            $scope.statedetail = {};
            $scope.statedetail.name = dagMetaDataService.elementDefs['dqexec'].listState
            $scope.statedetail.params = {}
            $scope.statedetail.params.type = dagMetaDataService.elementDefs['dqexec'].execType;
            $state.go($scope.statedetail.name, $scope.statedetail.params)
        }
    }
    JobMonitoringService.getLatestByUuid($scope.uuid, "dqexec")
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
        var refkeylist = [];
        if (response.refKeyList != null) {
            for (i = 0; i < response.refKeyList.length; i++) {
                refkeylist[i] = response.refKeyList[i].type + "-" + response.refKeyList[i].name;

            }
        }
        $scope.refkeylist = refkeylist

    }
    var onError=function(){
        $scope.isEditInprogess=false;
        $scope.isEditVeiwError=true;
    }

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
    
    $scope.showSqlFormaterSummary=function(){
        debugger
        if($scope.execData.summaryExec !=null){
          $('#sqlFormaterModel').modal({
            backdrop: 'static',
            keyboard: false
          });
          $scope.formateSql=sqlFormatter.format($scope.execData.summaryExec);
        }
      }
    

});
