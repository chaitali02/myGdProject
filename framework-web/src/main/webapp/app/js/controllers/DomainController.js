AdminModule = angular.module('AdminModule');

AdminModule.controller('DomainDetailController', function ($state, $scope, $stateParams, $rootScope, DomainSerivce, $sessionStorage, privilegeSvc, CommonService, $timeout, $filter) {
	
	$scope.dataLoading = false;
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen = true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else {
		$scope.isAdd = true;
		$scope.isEdit = true;
	}
	$scope.mode = "false";
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.isSubmitEnable = true;
	$scope.domainData;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false;
	$scope.domain = {};
	$scope.domain.versions = [];
	$scope.isshowmodel = false;
	$scope.state = "admin";
	$scope.stateparme = { "type": "domain" };
	$scope.isDependencyShow = false;
	$scope.privileges = [];

	$scope.privileges = privilegeSvc.privileges['domain'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['domain'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag = response[0].value
		}
	}

	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};

	$scope.getLovByType();

	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
	}
	
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createdomain', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.domainData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createdomain', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createdomain', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph


	$scope.getAllVersion = function (uuid) {
		DomainSerivce.getAllVersionByUuid(uuid, "domain").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var domainVersion = {};
				domainVersion.version = response[i].version;
				$scope.domain.versions[i] = domainVersion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion = function (uuid, version) {
		$timeout(function () {
			$scope.myform.$dirty = false;
		}, 0)
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		DomainSerivce.getOneByUuidAndVersion(uuid, version, 'domain')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.domainData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.domain.defaultVersion = defaultversion;
			var tags = [];
			$scope.tags=[];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End SelectVersion

    
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.showactive="true";
		var id;
		id = $stateParams.id;
		$scope.getAllVersion(id)//Call SelectAllVersion Function
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		DomainSerivce.getOneByUuidAndVersion(id,$stateParams.version || "", "domain")
			.then(function (response) { onGetByOneUuidandVersion(response.data) },function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.domainData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.domain.defaultVersion = defaultversion;
			var tags = [];
			$scope.tags=[];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		}
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End IF
	else{
		$scope.domainData={};
		$scope.domainData.locked="N";
	}
	


	/*Start Submit*/
	$scope.submit = function () {
		var upd_tag = "N"
		var domainJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;

		domainJson.uuid = $scope.domainData.uuid
		domainJson.name = $scope.domainData.name
		domainJson.desc = $scope.domainData.desc
		domainJson.active = $scope.domainData.active;
		domainJson.locked = $scope.domainData.locked;
		domainJson.published = $scope.domainData.published;
		domainJson.publicFlag = $scope.domainData.publicFlag;
		domainJson.regEx =$scope.domainData.regEx;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
			}
		}
		domainJson.tags = tagArray;
		console.log(JSON.stringify(domainJson));
		DomainSerivce.submit(domainJson, 'domain', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Domain Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.Close();
		}//End Submit Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submitdomain*/

	$scope.Close = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'domain' }); }, 2000);
		}
	}

});
