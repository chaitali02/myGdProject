
MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataLoadController', function ($rootScope, $state, $scope, $stateParams, MetadataLoadSerivce, privilegeSvc,CommonService,$timeout,$filter) {

	$scope.mode = "";
	$scope.dataLoading = false;
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
	$scope.loadHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.datastoredata;
	$scope.loaddata;
	$scope.showFrom = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.load = {};
	$scope.load.versions = [];
	$scope.lodeSourceTypes = ["file"];
	$scope.lodesourcetype = $scope.lodeSourceTypes[0];
	$scope.lodeTargetTypes = ["datapod"];
	$scope.lodetargettype = $scope.lodeTargetTypes[0];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['load'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['load'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag=response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
    $scope.getLovByType();
	$scope.showPage = function () {
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}

	$scope.showHome=function(uuid, version,mode){
		$scope.showPage()
		$state.go('metaListload', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.loaddata.locked =="Y"){
			return false;
		}
		$scope.showPage()
		$state.go('metaListload', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('metaListload', {
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
	$scope.close = function () {
		if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
			//revertback
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('metadata', { type: 'load' });
		}
	}

	$scope.loadFormChange = function () {
		if ($scope.mode == "true") {
			$scope.loadHasChanged = true;
		}
		else {
			$scope.loadHasChanged = false;
		}
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}
	
	$scope.onChangeSource=function(){
		MetadataLoadSerivce.getRegistryByDatasource($scope.allDataSource.defaultoption.uuid,"").then(function (response) {onSuccessGetRegistryByDatasource(response.data)}, function (response) {onError(response.data)});
			var onSuccessGetRegistryByDatasource = function (response) {
				$scope.allRegistryDatasource=response;
			}
	}
	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	if (typeof $stateParams.id != "undefined") {

		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		MetadataLoadSerivce.getAllVersionByUuid($stateParams.id, "load").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var loadversion = {};
				loadversion.version = response[i].version;
				$scope.load.versions[i] = loadversion;
			}
		}//End getAllVersionByUuid

		MetadataLoadSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'load').then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.load.defaultVersion = defaultversion;
			$scope.loaddata = response
			$scope.loadName = $scope.convertUppdercase($scope.loaddata.name)
			MetadataLoadSerivce.getAllLatest("datapod").then(function (response) { onSuccessLoad(response.data) });
			var onSuccessLoad = function (response) {
				$scope.allload = response
				var defaultoption = {};
				defaultoption.uuid = $scope.loaddata.target.ref.uuid;
				defaultoption.name = $scope.loaddata.target.ref.name;
				defaultoption.version = $scope.loaddata.target.ref.version;
				$scope.allload.defaultoption = defaultoption;
			}
			MetadataLoadSerivce.getAllLatest("datasource").then(function (response) { onSuccessDataSource(response.data) });
			var onSuccessDataSource = function (response) {
				$scope.allDataSource = response
				var defaultoption = {};
				defaultoption.uuid = $scope.loaddata.source.ref.uuid;
				defaultoption.name = $scope.loaddata.source.ref.name;
				defaultoption.version = $scope.loaddata.source.ref.version;
				$scope.allDataSource.defaultoption = defaultoption;
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Inner If
		}//onSuccessGetLatestByUuid
	}//End IF
	else {
		$scope.loaddata={};
		$scope.loaddata.locked="N";
		MetadataLoadSerivce.getAllLatest("datapod").then(function (response) { onSuccessLoad(response.data) });
		var onSuccessLoad = function (response) {
			$scope.allload = response
		}
		MetadataLoadSerivce.getAllLatest("datasource").then(function (response) { onSuccessDataSource(response.data) });
		var onSuccessDataSource = function (response) {
			$scope.allDataSource= response;
			MetadataLoadSerivce.getRegistryByDatasource($scope.allDataSource.defaultoption.uuid,"").then(function (response) {onSuccessGetRegistryByDatasource(response.data)}, function (response) {onError(response.data)});
			var onSuccessGetRegistryByDatasource = function (response) {
				$scope.allRegistryDatasource=response;
			}
		}
	}//End Else


	$scope.selectVersion = function () {
		$scope.allload = null;
		$scope.myform.$dirty = false;
		MetadataLoadSerivce.getOneByUuidAndVersion($scope.load.defaultVersion.uuid, $scope.load.defaultVersion.version, 'load').then(function (response) { onSuccessGetOneByUuidAndVersion(response.data) });
		var onSuccessGetOneByUuidAndVersion = function (response) {
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.load.defaultVersion = defaultversion;
			$scope.loaddata = response
			$scope.loadName = $scope.convertUppdercase($scope.loaddata.name)
			MetadataLoadSerivce.getAllLatest("datapod").then(function (response) { onSuccessLoad(response.data) });
			var onSuccessLoad = function (response) {
				$scope.allload = response
				var defaultoption = {};
				defaultoption.uuid = $scope.loaddata.target.ref.uuid;
				defaultoption.name = $scope.loaddata.target.ref.name;
				defaultoption.version = $scope.loaddata.target.ref.version;
				$scope.allload.defaultoption = defaultoption;
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Inner If
		}// End onSuccessGetOneByUuidAndVersion

	}//End selectVersion


	$scope.submitLoad = function () {
		var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.loadHasChanged = true;
		$scope.myform.$dirty = false;
		var loadJson = {}
		loadJson.uuid = $scope.loaddata.uuid;
		loadJson.name = $scope.loaddata.name
		loadJson.desc = $scope.loaddata.desc
		loadJson.active = $scope.loaddata.active;
		loadJson.locked = $scope.loaddata.locked;
		loadJson.published = $scope.loaddata.published;
		loadJson.header = $scope.loaddata.header;
		loadJson.append = $scope.loaddata.append;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result=(tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
		}
		loadJson.tags = tagArray;
		var target = {};
		var targetref = {}
		var source = {};
		var sourceref = {}
		targetref.type = $scope.lodetargettype;
		targetref.uuid = $scope.allload.defaultoption.uuid;
		targetref.version = $scope.allload.defaultoption.version;
		target.ref = targetref;
		loadJson.target = target;

		sourceref.type ="datasource";
		sourceref.uuid=$scope.allDataSource.defaultoption.uuid;
		source.ref = sourceref;
		source.value = $scope.SelectRegistryDatasource.path;
		loadJson.source = source;
		console.log(JSON.stringify(loadJson));
		MetadataLoadSerivce.submit(loadJson, 'load',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Load Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okloadsave();
		}//End Submit Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End submitLoad

	
	$scope.okloadsave = function () {
		$('#loadsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'load' }); }, 2000);
		}
	}
});
