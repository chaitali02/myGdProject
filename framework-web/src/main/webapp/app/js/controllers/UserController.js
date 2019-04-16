AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminUserController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, $sessionStorage, AdminUserService, privilegeSvc) {
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
	$scope.user = {};
	$scope.user.versions = [];
	$scope.showForm = true;
	$scope.showGraphDiv = false;
	$scope.mode = $stateParams.mode
	$scope.dataLoading = false;
	$scope.isSubmitEnable = true;
	$scope.userHasChanged = true;
	$scope.isshowmodel = false;
	$scope.state = "admin";
	$scope.stateparme = { "type": "user" };
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['user'] || [];
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
		$scope.userdata.displayName=data;
	}

	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['user'] || [];
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
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/
	
	
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListuser', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.userdata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListuser', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if(!isEdit){
			$scope.showPage()
			$state.go('adminListuser', {
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
	/* $timeout(function () {
       $scope.myform.$dirty=false;
    }, 0);*/


	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	

	$scope.userFormChange = function () {
		if ($scope.mode == "true") {
			$scope.userHasChanged = true;
		}
		else {
			$scope.userHasChanged = false;
		}
	}

	$scope.getAllLatestOrgnization = function () {
		CommonService.getAllLatest('organization').then(function (response) { onGetAllLatest(response.data) });
		var onGetAllLatest = function (response) {
			$scope.allOrgnization = response;
		}
	}
	
	$scope.getLatestByUuid=function(){
		AdminUserService.getLatestByUuid($rootScope.appUuid,'application').then(function(response){onSuccessGetLatestByUuid(response.data)});
	    var onSuccessGetLatestByUuid=function(response){
			$scope.applicationOrgDetail=response;
			if($scope.applicationOrgDetail.applicationType =="SYSADMIN"){
				$scope.getAllLatestOrgnization();
				

			}
			else{
				$scope.selectOrgInfo={};
				$scope.selectOrgInfo.uuid=$scope.applicationOrgDetail.orgInfo.ref.uuid;
				$scope.getGroupsByOrg($scope.selectOrgInfo.uuid);

			}
		}
	}
	// AdminUserService.getAllLatest('role').then(function (response) { onSuccessGetAllLatestRole(response.data) });
	// var onSuccessGetAllLatestRole = function (response) {
	// 	var roleInfoArray = [];
	// 	for (var i = 0; i < response.data.length; i++) {
	// 		var roleref = {};
	// 		roleref.uuid = response.data[i].uuid;
	// 		roleref.type = response.data[i].type;
	// 		roleref.id = response.data[i].uuid
	// 		roleref.name = response.data[i].name;
	// 		roleref.version = response.data[i].version;
	// 		roleInfoArray[i] = roleref;
	// 	}
	// 	$scope.roleall = roleInfoArray;
	// }

	// AdminUserService.getAllLatest('group').then(function (response) { onSuccessGetAllLatestGroup(response.data) });
	// var onSuccessGetAllLatestGroup = function (response) {
	// 	var groupInfoArray = [];
	// 	for (var i = 0; i < response.data.length; i++) {
	// 		var groupref = {};
	// 		groupref.uuid = response.data[i].uuid;
	// 		groupref.type = response.data[i].type;
	// 		groupref.id = response.data[i].uuid
	// 		groupref.name = response.data[i].name;
	// 		groupref.version = response.data[i].version;
	// 		groupInfoArray[i] = groupref;
	// 	}
	// 	$scope.groupall = groupInfoArray;
	// }

	$scope.getGroupsByOrg=function(uuid){
		AdminUserService.getGroupsByOrg(uuid,'organization').then(function (response) { onSuccessGetGroupsByOrg(response.data) });
		var onSuccessGetGroupsByOrg = function (response) {
			var groupInfoArray = [];
			for (var i = 0; i < response.length; i++) {
				var groupref = {};
				groupref.uuid = response[i].uuid;
				groupref.type = response[i].type;
				groupref.id = response[i].uuid
				groupref.name = response[i].name;
				groupref.version = response[i].version;
				groupInfoArray[i] = groupref;
			}
			$scope.groupall = groupInfoArray;
	   }
	}

    $scope.onChangeOrg=function(){
		if($scope.selectOrgInfo){
			$scope.groupInfoTags=null;
			$scope.groupall=null;	
			$scope.getGroupsByOrg($scope.selectOrgInfo.uuid);
			
		}
	}


	/*start showGraph*/
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;

	}/*End showGraph*/


	$scope.getAllVersion = function (uuid) {
		AdminUserService.getAllVersionByUuid(uuid, "user").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var userversion = {};
				userversion.version = response[i].version;
				$scope.user.versions[i] = userversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	/* Start selectVersion*/
	$scope.selectVersion = function (uuid, version) {
		$scope.tags = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		AdminUserService.getOneByUuidAndVersion(uuid, version, 'user')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.userdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.user.defaultVersion = defaultversion;
			$scope.groupInfoTags = response.groupInfo;

			var groupInfo = [];
			for (var j = 0; j < response.groupInfo.length; j++) {
				var grouptag = {};
				grouptag.uuid = response.groupInfo[j].ref.uuid;
				grouptag.type = response.groupInfo[j].ref.type;
				grouptag.name = response.groupInfo[j].ref.name;
				grouptag.id = response.groupInfo[j].ref.uuid;
				groupInfo[j] = grouptag
			}
			$scope.groupInfoTags = groupInfo;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
			$scope.getLatestByUuid();
           // $scope.getAllLatestOrgnization();
			$scope.selectOrgInfo={};
			
			if(response.orgInfo !=null){
				$scope.getGroupsByOrg(response.orgInfo.ref.uuid);
				$scope.selectOrgInfo.uuid=response.orgInfo.ref.uuid;
				$scope.selectOrgInfo.name=response.orgInfo.ref.name;
			}
			
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	} /* end selectVersion*/

	/*start If*/
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		$scope.getAllVersion($stateParams.id)//Call SelectAllVersion Function
		if (!$stateParams.version) {
			AdminUserService.getLatestByUuid($stateParams.id, "user").then(function (response) { onGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		} else {
			CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "user")
			.then(function (response) { onGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		}
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.userdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.user.defaultVersion = defaultversion;
			$scope.groupInfoTags = response.groupInfo;

			var groupInfo = [];
			for (var j = 0; j < response.groupInfo.length; j++) {
				var grouptag = {};
				grouptag.uuid = response.groupInfo[j].ref.uuid;
				grouptag.type = response.groupInfo[j].ref.type;
				grouptag.name = response.groupInfo[j].ref.name;
				grouptag.id = response.groupInfo[j].ref.uuid;
				groupInfo[j] = grouptag
			}
			$scope.groupInfoTags = groupInfo;
			$scope.selectDefaultGroup={};
			$scope.selectDefaultGroup.uuid=response.defaultGroup.ref.uuid;
			$scope.selectDefaultGroup.name=response.defaultGroup.ref.name;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
			$scope.getLatestByUuid();

			//$scope.getAllLatestOrgnization();
			$scope.selectOrgInfo={};
			if(response.orgInfo !=null){
				$scope.getGroupsByOrg(response.orgInfo.ref.uuid);	
				$scope.selectOrgInfo.uuid=response.orgInfo.ref.uuid;
				$scope.selectOrgInfo.name=response.orgInfo.ref.name;

			}
		};//End getLatestByUuid
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
		//}//End Inner Else
	}/*End If*/
	else{
		$scope.userdata={};
		$scope.userdata.locked="N";
		$scope.getLatestByUuid();
		//$scope.getAllLatestOrgnization();
	}




	$scope.loadrole = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.roleall, query);
		});
	};

	$scope.loadgroup = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.groupall, query);
		});
	};

	$scope.okusersave = function () {
		$('#okrolesave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'user' }); }, 2000);
		}
	}


	/*Start submituser*/
	$scope.submitUser = function () {
		var upd_tag="N"
		var userJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.userHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		userJson.uuid = $scope.userdata.uuid;
		userJson.name = $scope.userdata.name;
		userJson.displayName = $scope.userdata.displayName;
		userJson.desc = $scope.userdata.desc;
		userJson.active = $scope.userdata.active;
		userJson.locked = $scope.userdata.locked;
		userJson.published = $scope.userdata.published;
		userJson.publicFlag = $scope.userdata.publicFlag;

		userJson.password = $scope.userdata.password;
		userJson.firstName = $scope.userdata.firstName;
		userJson.middleName = $scope.userdata.middleName;
		userJson.lastName = $scope.userdata.lastName;
		userJson.emailId = $scope.userdata.emailId;
        
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
		userJson.tags = tagArray;

		var orgInfo={};
		var refOrgInfo={};
		refOrgInfo.uuid=$scope.selectOrgInfo.uuid;
		refOrgInfo.type="organization";	
		orgInfo.ref=refOrgInfo;
		userJson.orgInfo=orgInfo;


		var groupInfoArray = [];
		if ($scope.groupInfoTags != null) {
			for (var c = 0; c < $scope.groupInfoTags.length; c++) {
				var groupinforef = {};
				var groupref = {};
				groupinforef.uuid = $scope.groupInfoTags[c].uuid;
				groupinforef.type = "group";
				groupref.ref = groupinforef
				groupInfoArray.push(groupref);

			}
		}
		userJson.groupInfo = groupInfoArray

		var defaultGroup={};
		var defaultGroupRef={};
		defaultGroupRef.uuid=$scope.selectDefaultGroup.uuid;
		defaultGroupRef.type="group";
		defaultGroup.ref=defaultGroupRef
		userJson.defaultGroup=defaultGroup
		AdminUserService.submit(userJson, 'user',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'User Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okusersave();

		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submituser*/


});
