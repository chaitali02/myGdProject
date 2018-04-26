/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('OperatorDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, OperatorService, privilegeSvc) {
	
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
	$scope.OperatorData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.Operator = {};
	$scope.Operator.versions = [];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['operator'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['operator'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
	$scope.showPage = function () {
		$scope.showForm = true
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage();
		$state.go('createoperator', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('createoperator', {
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
		OperatorService.getAllVersionByUuid(uuid, "operator").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var Operatorversion = {};
				Operatorversion.version = response[i].version;
				$scope.Operator.versions[i] = Operatorversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "operator").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.OperatorData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.Operator.defaultVersion = defaultversion;
			if($scope.OperatorData.operatortype !=null){
				OperatorService.getAllLatest("operatortype").then(function (response) { onSuccessGetAllLatestoperatortype(response.data) });
				var onSuccessGetAllLatestoperatortype = function (response) {
					$scope.allOperatorType = response;
					var operatorType = {};
					operatorType.uuid = $scope.OperatorData.operatorType.ref.uuid;
					operatorType.name = ""
					$scope.selectedOperatorType = operatorType;
				}
		    }
		}
	}//End If
	else {
		OperatorService.getAllLatest("operatortype").then(function (response) { onSuccessGetAllLatestoperatortype(response.data) });
		var onSuccessGetAllLatestoperatortype = function (response) {
			$scope.allOperatorType = response;
			$scope.selectedOperatorType = $scope.allOperatorType[0];
		}
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allOperatorType = null;
		$scope.selectedOperatorType = null;
		$scope.selectedLibrary = null;
		OperatorService.getOneByUuidandVersion(uuid, version, 'Operator').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.OperatorData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.Operator.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			if($scope.OperatorData.operatortype !=null){
				OperatorService.getAllLatest("operatortype").then(function (response) { onSuccessGetAllLatestoperatortype(response.data) });
				var onSuccessGetAllLatestoperatortype = function (response) {
					$scope.allOperatorType = response;
					var operatortype = {};
					operatortype.uuid = $scope.OperatorData.operatortype.ref.uuid;
					operatortype.name = ""
					$scope.selectedOperatorType = operatortype;
				}
		    }
		}

	}

	$scope.submit = function () {
	
		$scope.isSubmitInProgress = true;
		$scope.isSubmitEnable = false;
		$scope.myform.$dirty = true;
		var OperatorJson = {}
		OperatorJson.uuid = $scope.OperatorData.uuid
		OperatorJson.name = $scope.OperatorData.name
		OperatorJson.desc = $scope.OperatorData.desc
		OperatorJson.active = $scope.OperatorData.active;
		OperatorJson.published = $scope.OperatorData.published;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var countTag = 0; countTag < $scope.tags.length; countTag++) {
				tagArray[countTag] = $scope.tags[countTag].text;
			}
		}
		OperatorJson.tags = tagArray;
		var operatortype = {};
		if($scope.selectedOperatorType !=null){
			var ref = {};
			ref.type = "operatortype";
			ref.uuid = $scope.selectedOperatorType.uuid;
			operatortype.ref = ref;
			
		}else{
			operatortype=null;
		}
		OperatorJson.operatorType = operatortype
		console.log(JSON.stringify(OperatorJson));
		OperatorService.submit(OperatorJson, 'operator').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Operator Saved Successfully'
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
			setTimeout(function () { $state.go("operator"); }, 2000);
		}
	}
});
