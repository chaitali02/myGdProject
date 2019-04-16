AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminGroupController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, AdminGroupService, privilegeSvc) {
	$scope.mode = ""
	$scope.group = {};
	$scope.group.versions = [];
	$scope.showFrom = true;
	$scope.showGraphDiv = false;
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
	$scope.mode = $stateParams.mode
	$scope.dataLoading = false;
	$scope.isSubmitEnable = true;
	$scope.groupHasChanged = true;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['group'] || [];
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
	
	$scope.onChangeName = function (data) {
		$scope.groupdata.displayName=data;
	}

	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['group'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	/*Start showPage*/
	$scope.showPage = function () {
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListgroup', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.groupdata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListgroup', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('adminListgroup', {
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

	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.groupFormChange = function () {
		if ($scope.mode == "true") {
			$scope.groupHasChanged = true;
		}
		else {
			$scope.groupHasChanged = false;

		}
	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.showGraphDiv = true;

	}/*End showGraph*/

	AdminGroupService.getAllLatest('role').then(function (response) { onSuccessRole(response.data) });
	var onSuccessRole = function (response) {
		var roleInfoArray = [];
		for (var i = 0; i < response.data.length; i++) {
			var roleref = {};
			roleref.uuid = response.data[i].uuid;
			roleref.type = response.data[i].type;
			roleref.id = response.data[i].uuid
			roleref.name = response.data[i].name;
			roleref.version = response.data[i].version;
			roleInfoArray[i] = roleref;
		}
		$scope.roleall = roleInfoArray;
		//alert(JSON.stringify(response))

	}
	AdminGroupService.getAllLatest('application').then(function (response) { onSuccessApp(response.data) });
	var onSuccessApp = function (response) {
		var appInfoArray = [];
		for (var i = 0; i < response.data.length; i++) {
			var appref = {};
			appref.uuid = response.data[i].uuid;
			appref.type = response.data[i].type;
			appref.id = response.data[i].uuid
			appref.name = response.data[i].name;
			appref.version = response.data[i].version;
			appInfoArray[i] = appref;
		}
		$scope.appall = appInfoArray;
		//alert(JSON.stringify(response))
	}

	/*start If*/
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
        $scope.showactive="true"
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminGroupService.getAllVersionByUuid($stateParams.id, "group").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var groupversion = {};
				groupversion.version = response[i].version;
				$scope.group.versions[i] = groupversion;
			}

		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "group")
			.then(function (response) { onGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.groupdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.group.defaultVersion = defaultversion;
			$scope.appId = response.appId.ref
			$scope.roleId = response.roleId.ref
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
		};//End getLatestByUuid
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}/*End If*/
	else {
		$scope.groupdata={};
		$scope.groupdata.locked="N";

	}//End Else


	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.tags = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminGroupService.getOneByUuidAndVersion($scope.group.defaultVersion.uuid, $scope.group.defaultVersion.version, 'group')
			.then(function (response) { onGetByOneUuidandVersion(response.data) },function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.groupdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.group.defaultVersion = defaultversion;
			$scope.appId = response.appId.ref
			$scope.roleId=null;
			setTimeout(() => {
				$scope.roleId = response.roleId.ref	
			},100);
			
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

	$scope.loadrole = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.roleall, query);
		});
	};

	$scope.okgroupsave = function () {
		$('#okrolesave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'group' }); }, 2000);
		}
	}


	/*Start submitGroup*/
	$scope.submitGroup = function () {
		var upd_tag="N"
		var groupJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.groupHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		groupJson.uuid = $scope.groupdata.uuid;
		groupJson.name = $scope.groupdata.name;
		groupJson.displayName = $scope.groupdata.displayName;
		groupJson.desc = $scope.groupdata.desc;
		groupJson.active = $scope.groupdata.active;
		groupJson.locked = $scope.groupdata.locked;
		groupJson.published = $scope.groupdata.published;
		groupJson.publicFlag = $scope.groupdata.publicFlag;

		var Appid = {};
		var refAppid = {};
		refAppid.uuid = $scope.appId.uuid;
		refAppid.type = "application";
		Appid.ref = refAppid;
		groupJson.appId = Appid
		var Roleid = {};
		var refRoleid = {};
		refRoleid.uuid = $scope.roleId.uuid;
		refRoleid.type = "role";
		Roleid.ref = refRoleid;
		groupJson.roleId = Roleid
		var tagArray = [];
		if ($scope.tags != null) {
			for (var c = 0; c < $scope.tags.length; c++) {
				tagArray[c] = $scope.tags[c].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if(result ==false){
				upd_tag="Y"	
			}
		}
		groupJson.tags = tagArray
		AdminGroupService.submit(groupJson, 'group',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Group Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okgroupsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submitgroup*/


});
