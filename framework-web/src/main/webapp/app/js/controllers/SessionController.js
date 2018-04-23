AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminSessionController', function ($state, $stateParams, $rootScope, $scope, AdminSessionService, privilegeSvc) {
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
	$scope.session = {};
	$scope.session.versions = [];
	$scope.showFrom = true;
	$scope.showGraphDiv = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['session'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['session'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
	/*Start showPage*/
	$scope.showPage = function () {
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListsession', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('adminListsession', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}
	$scope.close = function () {
		if ($stateParams.returnBack == "true" && $rootScope.previousState) {
			//revertback
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('admin', { 'type': 'session' });
		}
	}
	$scope.showGraph = function (uuid, version) {
		$scope.showFrom = false;
		$scope.showGraphDiv = true;

	}/*End ShowGraph*/

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode

		$scope.isDependencyShow = true;
		AdminSessionService.getAllVersionByUuid($stateParams.id, "session").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {

				var sessionversion = {};
				sessionversion.version = response[i].version;
				$scope.session.versions[i] = sessionversion;
			}

		}
		AdminSessionService.getLatestByUuid($stateParams.id, "session").then(function (response) { onGetLatestByUuid(response.data) });
		var onGetLatestByUuid = function (response) {
			$scope.sessiondata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.session.defaultVersion = defaultversion;
			$scope.selectsessionType = response.type
			var status = [];
			for (var j = 0; j < response.statusList.length; j++) {
				status[j] = response.statusList[j].stage
				$scope.statussession = status;

			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		}
	}

	$scope.selectVersion = function () {

		AdminSessionService.getByOneUuidandVersion($scope.session.defaultVersion.uuid, $scope.session.defaultVersion.version, 'session').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.sessiondata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.session.defaultVersion = defaultversion;
			$scope.selectsessionType = response.type

			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		}
	}

});
