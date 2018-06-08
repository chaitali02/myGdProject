/**
 **/
AdminModule = angular.module('AdminModule');
AdminModule.controller('AppConfigDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, AppConfigService, privilegeSvc) {

	$scope.mode = " ";
	$rootScope.isCommentVeiwPrivlage=true;
	$scope.isSubmitProgess = false;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else {
		$scope.isAdd = true;
	}
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
	$scope.isSubmitEnable = true;
	$scope.appConfigData;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.appConfig = {};
	$scope.appConfig.versions = [];
	$scope.isshowmodel = false;
	$scope.configTable = null;
	//$scope.type = ["string", "double", "date", "integer", "row"];
	$scope.type = [
		{"name":"string","caption":"string"},
		{"name":"double","caption":"double"},
	 	{"name":"date","caption":"date"}, 
		{"name":"integer","caption":"integer"},
		//{"name":"ONEDARRAY","caption":"row [ ]"},
		//{"name":"TWODARRAY","caption":"row [ ][ ]"}
	];
	$scope.isDependencyShow = false;
	$scope.privileges = [];

	$scope.privileges = privilegeSvc.privileges['appconfig'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['appconfig'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('createappconfig', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('createappconfig', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.orderByValue = function (value) {
		return value;
	};

	$scope.addRow = function () {
		if ($scope.configTable == null) {
			$scope.configTable = [];
		}
		var paramjson = {}
		paramjson.configId = $scope.configTable.length;
		$scope.configTable.splice($scope.configTable.length, 0, paramjson);
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.configTable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}

	$scope.removeRow = function () {
		var newDataList = [];
		$scope.selectallattribute = false;
		angular.forEach($scope.configTable, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.configTable = newDataList;
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph

	$scope.getAllVersion = function (uuid) {
		AppConfigService.getAllVersionByUuid(uuid, "appConfig").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var appConfigversion = {};
				appConfigversion.version = response[i].version;
				$scope.appConfig.versions[i] = appConfigversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion




	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "appConfig").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.appConfigData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.appConfig.defaultVersion = defaultversion;
			$scope.configTable = response.configInfo;
		}
	}//End If


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		AppConfigService.getOneByUuidandVersion(uuid, version, 'appConfig').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.appConfigData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.appConfig.defaultVersion = defaultversion;
			$scope.configTable = response.configInfo;
		}

	}

	$scope.submit = function () {
		$scope.isshowmodel = true;
		$scope.isSubmitProgess = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;
		var appConfigJson = {}
		appConfigJson.uuid = $scope.appConfigData.uuid
		appConfigJson.name = $scope.appConfigData.name
		appConfigJson.desc = $scope.appConfigData.desc
		appConfigJson.active = $scope.appConfigData.active;
		appConfigJson.published = $scope.appConfigData.published;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
		}
		appConfigJson.tags = tagArray;

		var configInfoArray = [];
		if ($scope.configTable.length > 0) {
			for (var i = 0; i < $scope.configTable.length; i++) {
				var paraminfo = {};
				paraminfo.configId = $scope.configTable[i].configId;
				paraminfo.configName = $scope.configTable[i].configName;
				paraminfo.configType = $scope.configTable[i].configType;
				paraminfo.configVal = $scope.configTable[i].configVal;
				configInfoArray[i] = paraminfo;
			}
		}
		appConfigJson.configInfo = configInfoArray;
		console.log(JSON.stringify(appConfigJson));
		AppConfigService.submit(appConfigJson, 'appConfig').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitProgess = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'App Config List Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okSave();

		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}

	$scope.okSave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go("settings",{'index':4}); }, 2000);
		}
	}
});
