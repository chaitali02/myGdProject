JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailProfileGroupExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {

    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;
    $scope.selectTitle = dagMetaDataService.elementDefs['profilegroupexec'].caption;
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
    $scope.onShowDetail = function (data) {
        $rootScope.previousState = {};
        $rootScope.previousState.name = dagMetaDataService.elementDefs['profilegroupexec'].detailState;
        $rootScope.previousState.params = {};
        $rootScope.previousState.params.id = $stateParams.id;
        $rootScope.previousState.params.mode = true;
        var type = data.type
        var uuid = data.uuid
        var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
        var stageparam = {};
        stageparam.id = uuid;
        stageparam.mode = true;
        stageparam.returnBack = true;
        $state.go(stageName, stageparam);
    };

    $scope.menuOptions = [
        ['Show Details', function ($itemScope) {
            $scope.onShowDetail($itemScope.item)
        }],
        //null,

    ];

    $scope.state = dagMetaDataService.elementDefs['profilegroupexec'].listState + "({type:'" + dagMetaDataService.elementDefs['profilegroupexec'].execType + "'})"
    $scope.close = function () {
        if ($stateParams.returnBack == "true" && $rootScope.previousState) {
            //revertback
            $state.go($rootScope.previousState.name, $rootScope.previousState.params);
        }
        else {
            $scope.statedetail = {};
            $scope.statedetail.name = dagMetaDataService.elementDefs['profilegroupexec'].listState
            $scope.statedetail.params = {}
            $scope.statedetail.params.type = dagMetaDataService.elementDefs['profilegroupexec'].execType;
            $state.go($scope.statedetail.name, $scope.statedetail.params)
        }
    }


    JobMonitoringService.getLatestByUuid($scope.uuid, "profilegroupexec").then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
        $scope.execData = response;
        var statusList = [];
        for (i = 0; i < response.statusList.length; i++) {
            d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
            d = d.toString().replace("+0530", "IST");
            statusList[i] = response.statusList[i].stage + "-" + d;
        }
        $scope.statusList = statusList
        var execList = [];
        for (i = 0; i < response.execList.length; i++) {
            var profileexec = {};

            profileexec.type = response.execList[i].ref.type;
            profileexec.name = response.execList[i].ref.name;
            profileexec.uuid = response.execList[i].ref.uuid;
            execList[i] = profileexec;
        }

        $scope.execList = execList
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
