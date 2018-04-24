/****/
AdminModule = angular.module('AdminModule');
AdminModule.controller('AdminActivityController', function ($state, $stateParams, $rootScope, $scope, AdminActivityService, privilegeSvc) {

	$scope.activity = {};
	$scope.activity.versions = [];
	$scope.showForm = true;
	$scope.showGraphDiv = false;
	$scope.isDependencyShow = false;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
	}
	else {
		$scope.isAdd = true;
	}
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['activty'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['activty'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	/*Start showPage*/
	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListactivity', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('adminListactivity', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}
	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}/*End ShowGraph*/

	$scope.mode = " "
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		AdminActivityService.getAllVersionByUuid($stateParams.id, "activity").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var activityversion = {};
				activityversion.version = response[i].version;
				$scope.activity.versions[i] = activityversion;
			}
		}

		AdminActivityService.getLatestByUuid($stateParams.id, "activity").then(function (response) { onGetLatestByUuid(response.data) });
		var onGetLatestByUuid = function (response) {
			$scope.activitydata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.activity.defaultVersion = defaultversion;
			$scope.selectactivityType = response.type
		}
	}

	$scope.selectVersion = function () {
		AdminActivityService.getByOneUuidandVersion($scope.activity.defaultVersion.uuid, $scope.activity.defaultVersion.version, 'activity').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.activitydata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.activity.defaultVersion = defaultversion;
			$scope.selectactivityType = response.type
		}
	}

});
