/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('DistributionDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, DistributionService, privilegeSvc) {
	
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
	
	$scope.mode = " ";
	$scope.isSubmitInProgress = false;
	$scope.isSubmitEnable = true;
	$scope.distributionData;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showgraphdiv = false
	$scope.graphDataStatus = false
	$scope.distribution = {};
	$scope.distribution.versions = [];
	$scope.librarytypes = ["SPARKML", "R", "JAVA"];
	$scope.paramtable = null;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['distribution'] || [];

	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['distribution'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
	$scope.showPage = function () {
		$scope.showForm = true
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('createdistribution', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		$scope.showPage()
		$state.go('createdistribution', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}
	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.showGraph = function () {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph


	$scope.getAllVersion = function (uuid) {
		DistributionService.getAllVersionByUuid(uuid, "distribution").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var distributionversion = {};
				distributionversion.version = response[i].version;
				$scope.algorithm.versions[i] = distributionversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "distribution").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.distributionData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.distribution.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectlibrary = response.library
			DistributionService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
			var onSuccessGetAllLatestParamlist = function (response) {
				$scope.allparamlist = response;
				var paramlist = {};
				paramlist.uuid = $scope.distributionData.paramList.ref.uuid;
				paramlist.name = ""
				$scope.selectparamlist = paramlist;

			}
		}
	}//End If
	else {
		DistributionService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
		var onSuccessGetAllLatestParamlist = function (response) {
			$scope.allparamlist = response;
			$scope.selectparamlist = $scope.allparamlist[0];
		}
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allparamlist = null;
		$scope.selectparamlist = null;
		$scope.selecttype = null;
		$scope.selectlibrary = null;
		DistributionService.getOneByUuidandVersion(uuid, version, 'distribution').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.distributionData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.distribution.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectlibrary = response.library
			DistributionService.getAllLatest("paramlist").then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
			var onSuccessGetAllLatestParamlist = function (response) {
				$scope.allparamlist = response;
				var paramlist = {};
				paramlist.uuid = $scope.distributionData.paramList.ref.uuid;
				paramlist.name = ""
				$scope.selectparamlist = paramlist;

			}
		}

	}

	$scope.submit = function () {
	
		$scope.isSubmitInProgress = true;
		$scope.isSubmitEnable = false;
		$scope.myform.$dirty = false;
		
		var distributionJson = {}
		distributionJson.uuid = $scope.distributionData.uuid
		distributionJson.name = $scope.distributionData.name
		distributionJson.desc = $scope.distributionData.desc
		distributionJson.active = $scope.distributionData.active;
		distributionJson.published = $scope.distributionData.published;
		distributionJson.type = $scope.selecttype;
		distributionJson.library = $scope.selectlibrary;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
		}
		distributionJson.tags = tagArray;
		var paramlist = {};
		var ref = {};
		ref.type = "paramlist";
		ref.uuid = $scope.selectparamlist.uuid;
		paramlist.ref = ref;
		distributionJson.paramList = paramlist
		console.log(JSON.stringify(distributionJson));
		DistributionService.submit(distributionJson, 'distribution').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Distribution Saved Successfully'
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
			setTimeout(function () { $state.go("distribution"); }, 2000);
		}
	}

	

});
