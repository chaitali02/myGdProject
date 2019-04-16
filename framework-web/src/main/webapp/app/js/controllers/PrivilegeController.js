/**
 *
 */
AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminPrivilegeController', function (CommonService, $state, $stateParams, $rootScope, $scope, AdminPrivilegeService, privilegeSvc,$timeout,$filter) {

	$scope.privilege = {};
	$scope.privilege.versions = [];
	$scope.privilegeTypes = ["Add", "View", "Edit", "Delete", "Execute", "Clone", "Export", "Restore", "Publish", "Unpublish","Lock","Unlock"];
	$scope.selectprivilegeType = $scope.privilegeTypes[0]
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
	$scope.privilegeHasChanged = true;
	$scope.isshowmodel = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['privilege'] || [];
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
		$scope.privilegedata.displayName=data;
	}
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['privilege'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
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
		$state.go('adminListprivilege', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.privilegedata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('adminListprivilege', {
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
			$state.go('adminListprivilege', {
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
	
	$scope.privilegeFormChange = function () {
		if ($scope.mode == "true") {
			$scope.privilegeHasChanged = true;
		}
		else {
			$scope.privilegeHasChanged = false;
		}
	}

	$scope.okprivilegesave = function () {
		$('#okprivilegesave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'privilege' }); }, 2000);
		}
	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}/*End ShowDatapodGraph*/




	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminPrivilegeService.getAllVersionByUuid($stateParams.id, "privilege").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var privilegeversion = {};
				privilegeversion.version = response[i].version;
				$scope.privilege.versions[i] = privilegeversion;
			}
		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "privilege")
			.then(function (response) { onGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.privilegedata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.privilege.defaultVersion = defaultversion;
			$scope.selectprivilegeType = response.privType

			AdminPrivilegeService.getAll("meta").then(function (response) { onGetAll(response.data) });
			var onGetAll = function (response) {
				$scope.allmeta = response
				var defaultoption = {};
				defaultoption.uuid = $scope.privilegedata.metaId.ref.uuid;
				defaultoption.name = $scope.privilegedata.metaId.ref.name;
				$scope.allmeta.defaultoption = defaultoption;

			}
			var tags = [];
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
	}
	//end of if
	else {
		$scope.privilegedata={};
		$scope.privilegedata.locked="N"
		AdminPrivilegeService.getAll("meta").then(function (response) { onGetAll(response.data) });
		var onGetAll = function (response) {
			$scope.allmeta = response

		}
	}
	$scope.selectVersion = function () {
		$scope.allmeta = null;
		$scope.tags = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminPrivilegeService.getByOneUuidandVersion($scope.privilege.defaultVersion.uuid, $scope.privilege.defaultVersion.version, 'privilege')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.privilegedata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.privilege.defaultVersion = defaultversion;
			$scope.selectprivilegeType = response.privType
			AdminPrivilegeService.getAll("meta").then(function (response) { onGetAll(response.data) });
			var onGetAll = function (response) {
				$scope.allmeta = response
				var defaultoption = {};
				defaultoption.uuid = $scope.privilegedata.metaId.ref.uuid;
				defaultoption.name = $scope.privilegedata.metaId.ref.name;
				$scope.allmeta.defaultoption = defaultoption;
			}
			var tags = [];
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
	}



	/*Start submitPrivilege*/
	$scope.submitPrivilege = function () {
		var upd_tag="N"
		var privilegeJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.privilegeHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		privilegeJson.uuid = $scope.privilegedata.uuid;
		privilegeJson.name = $scope.privilegedata.name;
		privilegeJson.displayName = $scope.privilegedata.displayName;
		privilegeJson.desc = $scope.privilegedata.desc;
		privilegeJson.active = $scope.privilegedata.active;
		privilegeJson.locked = $scope.privilegedata.locked;
		privilegeJson.published = $scope.privilegedata.published;
		privilegeJson.privType = $scope.selectprivilegeType;
		privilegeJson.publicFlag = $scope.privilegedata.publicFlag;

		var defaultoption = {};
		var metaref = {};
		defaultoption.uuid = $scope.allmeta.defaultoption.uuid;
		defaultoption.type = "meta";
		$scope.defaultoption = defaultoption;
		metaref.ref = defaultoption;
		privilegeJson.metaId = metaref;
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
		privilegeJson.tags = tagArray
		AdminPrivilegeService.submit(privilegeJson, 'privilege',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Privilege Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okprivilegesave();

		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submitprivilege*/

});
