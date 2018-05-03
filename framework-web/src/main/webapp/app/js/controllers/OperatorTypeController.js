/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('OperatorTypeDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, OperatorTypeService, privilegeSvc) {
	
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
	
	$scope.mode = false;
	$scope.isSubmitInProgress = false;
	$scope.isSubmitEnable = false;
	$scope.OperatorTypeData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.OperatorType = {};
	$scope.OperatorType.versions = [];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['operatortype'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['operatortype'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
	$scope.showPage = function () {
		$scope.showForm = true
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage();
		$state.go('createoperatortype', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('createoperatortype', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.showGraph = function () {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph


	$scope.getAllVersion = function (uuid) {
		OperatorTypeService.getAllVersionByUuid(uuid, "Operatortype").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var OperatorTypeversion = {};
				OperatorTypeversion.version = response[i].version;
				$scope.OperatorType.versions[i] = OperatorTypeversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "Operatortype").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.OperatorTypeData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.OperatorType.defaultVersion = defaultversion;
			if($scope.OperatorTypeData.paramList !=null){
				OperatorTypeService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.OperatorTypeData.paramList.ref.uuid;
					paramlist.name = ""
					$scope.selectedParamlist = paramlist;
				}
		    }
		}
	}//End If
	else {
		OperatorTypeService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
		var onSuccessGetAllLatestParamlist = function (response) {
			$scope.allParamlist = response;
			$scope.selectedParamlist = $scope.allParamlist[0];
		}
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allParamlist = null;
		$scope.selectedParamlist = null;
		$scope.selectedLibrary = null;
		OperatorTypeService.getOneByUuidandVersion(uuid, version, 'Operatortype').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.OperatorTypeData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.OperatorType.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectedLibrary = response.library
			if($scope.OperatorTypeData.paramList !=null){
				OperatorTypeService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.OperatorTypeData.paramList.ref.uuid;
					paramlist.name = ""
					$scope.selectedParamlist = paramlist;
				}
		    }
		}

	}

	$scope.submit = function () {
	
		$scope.isSubmitInProgress = true;
		$scope.isSubmitEnable = false;
		$scope.myform.$dirty = true;
		var OperatorTypeJson = {}
		OperatorTypeJson.uuid = $scope.OperatorTypeData.uuid
		OperatorTypeJson.name = $scope.OperatorTypeData.name
		OperatorTypeJson.desc = $scope.OperatorTypeData.desc
		OperatorTypeJson.active = $scope.OperatorTypeData.active;
		OperatorTypeJson.published = $scope.OperatorTypeData.published;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var countTag = 0; countTag < $scope.tags.length; countTag++) {
				tagArray[countTag] = $scope.tags[countTag].text;
			}
		}
		OperatorTypeJson.tags = tagArray;
		var paramlist = {};
		if($scope.selectedParamlist !=null){
			var ref = {};
			ref.type = "paramlist";
			ref.uuid = $scope.selectedParamlist.uuid;
			paramlist.ref = ref;
			
		}else{
			paramlist=null;
		}
		OperatorTypeJson.paramList = paramlist
		console.log(JSON.stringify(OperatorTypeJson));
		OperatorTypeService.submit(OperatorTypeJson, 'Operatortype').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Operator Type Saved Successfully'
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
			setTimeout(function () { $state.go("operatortype"); }, 2000);
		}
	}
});
