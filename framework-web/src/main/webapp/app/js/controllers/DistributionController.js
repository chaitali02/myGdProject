/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('DistributionDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, DistributionService, privilegeSvc,$timeout,$filter) {
	
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
	$scope.mode = false;
	$scope.isSubmitInProgress = false;
	$scope.isSubmitEnable = false;
	$scope.distributionData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.distribution = {};
	$scope.distribution.versions = [];
	$scope.libraryTypes = ["SPARKML", "R", "JAVA","MATH3"];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['distribution'] || [];

	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['distribution'] || [];
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
	 
	$scope.onChangeName = function (data) {
		$scope.distributionData.displayName=data;
	}
	
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		  return false;
		}
	
	}
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true
		$scope.showGraphDiv = false
	}
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createdistribution', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.distributionData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createdistribution', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if(isEdit){
			return false;
		}
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
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.showGraph = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph


	$scope.getAllVersion = function (uuid) {
		DistributionService.getAllVersionByUuid(uuid, "distribution").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var distributionversion = {};
				distributionversion.version = response[i].version;
				$scope.distribution.versions[i] = distributionversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id);
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "distribution")
			.then(function (response){onSuccessGetLatestByUuid(response.data)},function(response){onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
		    $scope.isEditInprogess=false;
			$scope.distributionData = response;;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.distribution.defaultVersion = defaultversion;
			$scope.selectedLibrary = response.library;
			if($scope.distributionData.paramList !=null){
				DistributionService.getAllLatest("paramlist").
					then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.distributionData.paramList.ref.uuid;
					paramlist.name = "";
					$scope.selectedParamlist = paramlist;
				}
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag;
					$scope.tags = tags;
				}
			}
		}
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End If
	else {
		$scope.distributionData={};
		$scope.distributionData.locked="N";
		DistributionService.getAllLatest("paramlist")
			.then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
		var onSuccessGetAllLatestParamlist = function (response) {
			$scope.allParamlist = response;
		}
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allParamlist = null;
		$scope.selectedParamlist = null;
		$scope.selectedLibrary = null;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		DistributionService.getOneByUuidandVersion(uuid,version,'distribution')
			.then(function (response) { onGetByOneUuidandVersion(response.data) },function(response){onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.distributionData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.distribution.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectedLibrary = response.library
			if($scope.distributionData.paramList !=null){
				DistributionService.getAllLatest("paramlist")
					.then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.distributionData.paramList.ref.uuid;
					paramlist.name = ""
					$scope.selectedParamlist = paramlist;
				}
		    }
		}
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}

	$scope.submit = function () {
	    var upd_tag="N"
		$scope.isSubmitInProgress = true;
		$scope.isSubmitEnable = false;
		$scope.myform.$dirty = false;

		var distributionJson = {}
		distributionJson.uuid = $scope.distributionData.uuid;
		distributionJson.name = $scope.distributionData.name;
		distributionJson.displayName = $scope.distributionData.displayName;
		distributionJson.desc = $scope.distributionData.desc;
		distributionJson.active = $scope.distributionData.active;
		distributionJson.locked = $scope.distributionData.locked;
		distributionJson.published = $scope.distributionData.published;
		distributionJson.publicFlag = $scope.distributionData.publicFlag;

		distributionJson.library = $scope.selectedLibrary;
		distributionJson.className = $scope.distributionData.className;
		distributionJson.methodName = $scope.distributionData.methodName;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var countTag = 0; countTag < $scope.tags.length; countTag++) {
				tagArray[countTag] = $scope.tags[countTag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
		}
		distributionJson.tags = tagArray;

		var paramlist = {};
		if($scope.selectedParamlist !=null){
			var ref = {};
			ref.type = "paramlist";
			ref.uuid = $scope.selectedParamlist.uuid;
			paramlist.ref = ref;
			
		}else{
			paramlist=null;
		}
		distributionJson.paramList = paramlist
		console.log(JSON.stringify(distributionJson));
		DistributionService.submit(distributionJson, 'distribution',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = true;
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
