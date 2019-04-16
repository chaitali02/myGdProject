AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminRoleController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, AdminRoleService, privilegeSvc) {
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
	$scope.role = {};
	$scope.role.versions = [];
	$scope.showFrom = true;
	$scope.showGraphDiv = false;
	$scope.isversionEnable = true;
	$scope.mode = $stateParams.mode
	$scope.dataLoading = false;
	$scope.isSubmitEnable = true;
	$scope.roleHasChanged = true;
	$scope.isshowmodel = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['role'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			$scope.lobTag=response[0].value
		}
	}
	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
    $scope.getLovByType();
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['role'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.onChangeName = function (data) {
		$scope.roledata.displayName=data;
	}

	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	/*Start showPage*/
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListrole', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.roledata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListrole', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('adminListrole', {
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

	$scope.roleFormChange = function () {
		if ($scope.mode == "true") {
			$scope.roleHasChanged = true;
		}
		else {
			$scope.roleHasChanged = false;
		}
	}

	AdminRoleService.getAllLatest('privilege').then(function (response) { onSuccess(response.data) });
	var onSuccess = function (response) {
		var privilegeInfoArray = [];
		for (var i = 0; i < response.data.length; i++) {
			var privilegeref = {};
			privilegeref.uuid = response.data[i].uuid;
			privilegeref.type = response.data[i].type;
			privilegeref.id = response.data[i].uuid
			privilegeref.name = response.data[i].name;
			privilegeref.version = response.data[i].version;
			privilegeInfoArray[i] = privilegeref;
		}
		$scope.privilegeall = privilegeInfoArray;
	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}/*End showgraph*/


	/*start If*/
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		if ($scope.mode == 'true') {
			$scope.isversionEnable = false;
		}
		else {
			$scope.isversionEnable = true;
		}
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminRoleService.getAllVersionByUuid($stateParams.id, "role")
			.then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var roleversion = {};
				roleversion.version = response[i].version;
				$scope.role.versions[i] = roleversion;
			}

		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "role")
			.then(function (response) { onGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.roledata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.role.defaultVersion = defaultversion;
			$scope.privilegeInfoTags = response.privilegeInfo;
			var privilegeInfo = [];
			for (var j = 0; j < response.privilegeInfo.length; j++) {
				var privilagetag = {};
				privilagetag.uuid = response.privilegeInfo[j].ref.uuid;
				privilagetag.type = response.privilegeInfo[j].ref.type;
				privilagetag.name = response.privilegeInfo[j].ref.name;
				privilagetag.id = response.privilegeInfo[j].ref.uuid;
				privilegeInfo[j] = privilagetag
			}
			$scope.privilegeInfoTags = privilegeInfo;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}/*End If*/
    else{
		$scope.roledata={};
		$scope.roledata.locked="N";
	}

	


	

	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminRoleService.getOneByUuidAndVersion($scope.role.defaultVersion.uuid, $scope.role.defaultVersion.version, 'role')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.roledata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.role.defaultVersion = defaultversion;
			$scope.privilegeInfoTags = response.privilegeInfo;
			var privilegeInfo = [];
			for (var j = 0; j < response.privilegeInfo.length; j++) {
				var privilagetag = {};
				privilagetag.uuid = response.privilegeInfo[j].ref.uuid;
				privilagetag.type = response.privilegeInfo[j].ref.type;
				privilagetag.name = response.privilegeInfo[j].ref.name;
				privilagetag.id = response.privilegeInfo[j].ref.uuid;
				privilegeInfo[j] = privilagetag
			}
			$scope.privilegeInfoTags = privilegeInfo;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	} /* end selectVersion*/

	$scope.loadprivilege = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.privilegeall, query);
		});
	};

	$scope.okrolesave = function () {
		$('#okrolesave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'role' }); }, 2000);
		}
	}


	/*Start SubmitRole*/
	$scope.submitRole = function () {
		var upd_tag="N"
		var roleJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.roleHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		roleJson.uuid = $scope.roledata.uuid;
		roleJson.name = $scope.roledata.name;
		roleJson.displayName = $scope.roledata.displayName;
		roleJson.desc = $scope.roledata.desc;
		roleJson.active = $scope.roledata.active;
		roleJson.locked = $scope.roledata.locked;
		roleJson.published = $scope.roledata.published;
		roleJson.publicFlag = $scope.roledata.publicFlag;

		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
		}
		roleJson.tags = tagArray
		var privilegeInfoArray = [];
		if ($scope.privilegeInfoTags != null) {
			for (var c = 0; c < $scope.privilegeInfoTags.length; c++) {
				var privilegeinforef = {};
				var privilegeref = {};
				privilegeinforef.uuid = $scope.privilegeInfoTags[c].uuid;
				privilegeinforef.type = "privilege";
				privilegeref.ref = privilegeinforef
				privilegeInfoArray.push(privilegeref);
			}
		}
		roleJson.privilegeInfo = privilegeInfoArray
		console.log(privilegeInfoArray)
		AdminRoleService.submit(roleJson, 'role',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Role Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okrolesave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitRole*/

});//End RoleController
