AdminModule = angular.module('AdminModule');

AdminModule.controller('MetadataApplicationController', function ($state, $scope, $stateParams, $rootScope, MetadataApplicationSerivce, $sessionStorage, privilegeSvc) {
	$scope.mode = " ";
	$scope.SourceTypes = ["file", "hive", "impala", 'mysql', 'oracle']
	$scope.dataLoading = false;
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
	$scope.applicationHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.applicationdata;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false;
	$scope.application = {};
	$scope.application.versions = [];
	$scope.isshowmodel = false;
	$scope.state = "admin";
	$scope.stateparme = { "type": "application" };
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['application'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['application'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode: 'true'
		});
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


	$scope.applicationFormChange = function () {
		if ($stateParams.mode == true) {
			$scope.applicationHasChanged = true;
		}
		else {
			$scope.applicationHasChanged = false;
		}
	}

	$scope.selectType = function () {
		MetadataApplicationSerivce.getDatasourceByType($scope.selectSourceType.toUpperCase()).then(function (response) { onSuccessGetDatasourceByType(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			console.log(JSON.stringify(response));
			$scope.alldatasource = response;
			$scope.selectDataSource = response[0];

		}
	}
	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph





	$scope.getAllVersion = function (uuid) {
		MetadataApplicationSerivce.getAllVersionByUuid(uuid, "application").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var applicationversion = {};
				applicationversion.version = response[i].version;
				$scope.application.versions[i] = applicationversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion = function (uuid, version) {
		$timeout(function () {
			$scope.myform.$dirty = false;
		}, 0)
		MetadataApplicationSerivce.getOneByUuidAndVersion(uuid, version, 'application').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.applicationdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.application.defaultVersion = defaultversion;
		}

	}//End SelectVersion


	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		/*if($sessionStorage.fromParams.type !="application" && $sessionStorage.showgraph !=true){
			$scope.state=$sessionStorage.fromStateName;
			$scope.stateparme=$sessionStorage.fromParams;
			$sessionStorage.showgraph=true;
			var data=$stateParams.id.split("_");
			var uuid=data[0];
		    var version=data[1];
		    $scope.getAllVersion(uuid)//Call SelectAllVersion Function
			$scope.selectVersion(uuid,version);//Call SelectVersion Function

		}*/

		//else{
		/*	var id;
			if($stateParams.id.indexOf("_") > -1){
				id=$stateParams.id.split("_")[0]

			}else{*/
		var id;
		id = $stateParams.id;
		//}
		$scope.getAllVersion(id)//Call SelectAllVersion Function
		MetadataApplicationSerivce.getAllVersionByUuid(id, "application").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var applicationversion = {};
				applicationversion.version = response[i].version;
				$scope.application.versions[i] = applicationversion;
			}
		}//End getAllVersionByUui
		MetadataApplicationSerivce.getLatestByUuid(id, "application").then(function (response) { onGetLatestByUuid(response.data) });
		var onGetLatestByUuid = function (response) {
			$scope.applicationdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.application.defaultVersion = defaultversion;
			MetadataApplicationSerivce.getLatestDataSourceByUuid($scope.applicationdata.dataSource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataApplicationSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.applicationdata.dataSource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
		}
	}//End IF

	/*Start SubmitAplication*/
	$scope.submitApplication = function () {
		var applicationJson = {};
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.applicationHasChanged = true;
		$scope.myform.$dirty = false;
		var applicationJson = {}
		applicationJson.uuid = $scope.applicationdata.uuid
		applicationJson.name = $scope.applicationdata.name
		applicationJson.desc = $scope.applicationdata.desc
		applicationJson.active = $scope.applicationdata.active;
		applicationJson.published = $scope.applicationdata.published;
		var datasource = {};
		var ref = {};
		ref.type = "datasource";
		ref.uuid = $scope.selectDataSource.uuid;
		datasource.ref = ref;
		applicationJson.dataSource = datasource;

		MetadataApplicationSerivce.submit(applicationJson, 'application').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Application Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okapplicationsave();
		}//End Submit Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitApplication*/

	$scope.okapplicationsave = function () {
		$('#applicationsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'application' }); }, 2000);
		}
	}

});
