JobMonitoringModule = angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDqGroupExecController', function ($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService, privilegeSvc) {
    $rootScope.isCommentVeiwPrivlage = true;
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;
    $scope.selectTitle = dagMetaDataService.elementDefs['dqgroupexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['dqgroupexec'].listState + "({type:'" + dagMetaDataService.elementDefs['dqgroupexec'].execType + "'})"
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
            $scope.statedetail.name = dagMetaDataService.elementDefs['dqgroupexec'].listState
            $scope.statedetail.params = {}
            $scope.statedetail.params.type = dagMetaDataService.elementDefs['dqgroupexec'].execType;
            $state.go($scope.statedetail.name, $scope.statedetail.params)
        }
    }
    $scope.onShowDetail = function (data) {
        $rootScope.previousState = {};
        $rootScope.previousState.name = dagMetaDataService.elementDefs['dqgroupexec'].detailState;
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
    JobMonitoringService.getLatestByUuid($scope.uuid, "dqgroupexec").then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
        $scope.execData = response;
        var statusList = [];
        for (i = 0; i < response.statusList.length; i++) {
            var statusListjson = {}
            d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
            d = d.toString().replace("+0530", "IST");
            statusListjson.id = i + 2;
            statusListjson.text = response.statusList[i].stage + "-" + d;
            statusList[i] = statusListjson
        }
        $scope.statusList = statusList
        var execList = [];
        for (i = 0; i < response.execList.length; i++) {
            var pdataqualexec = {};
            pdataqualexec.type = response.execList[i].ref.type;
            pdataqualexec.name = response.execList[i].ref.name;
            pdataqualexec.uuid = response.execList[i].ref.uuid;
            execList[i] = pdataqualexec;
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
