AdminModule = angular.module('AdminModule');

AdminModule.controller('AdminUserController', function (CommonService, $state, $timeout, $filter, $stateParams, $rootScope, $scope, $sessionStorage, AdminUserService, privilegeSvc) {
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
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['user'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	/*Start showPage*/
	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListuser', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
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

	AdminUserService.getAllLatest('role').then(function (response) { onSuccessGetAllLatestRole(response.data) });
	var onSuccessGetAllLatestRole = function (response) {
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
	}

	AdminUserService.getAllLatest('group').then(function (response) { onSuccessGetAllLatestGroup(response.data) });
	var onSuccessGetAllLatestGroup = function (response) {
		var groupInfoArray = [];
		for (var i = 0; i < response.data.length; i++) {
			var groupref = {};
			groupref.uuid = response.data[i].uuid;
			groupref.type = response.data[i].type;
			groupref.id = response.data[i].uuid
			groupref.name = response.data[i].name;
			groupref.version = response.data[i].version;
			groupInfoArray[i] = groupref;
		}
		$scope.groupall = groupInfoArray;
	}



	/*start showGraph*/
	$scope.showGraph = function (uuid, version) {
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
		AdminUserService.getOneByUuidAndVersion(uuid, version, 'user').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.userdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.user.defaultVersion = defaultversion;
			//  $scope.roleInfoTags=response.roleInfo;
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

			var roleInfo = [];
			//    	    for(var j=0;j<response.roleInfo.length;j++){
			//    	    	var roletag={};
			//      	    	roletag.uuid=response.roleInfo[j].ref.uuid;
			//    	    	roletag.type=response.roleInfo[j].ref.type;
			//    	    	roletag.name=response.roleInfo[j].ref.name;
			//    	    	roletag.id=response.roleInfo[j].ref.uuid;
			//    	    	roleInfo[j]=roletag
			//    	    }
			//$scope.roleInfoTags=roleInfo;

			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If

		}
	} /* end selectVersion*/

	/*start If*/
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		//if(typeof $sessionStorage.fromParams != "undefined"){
		/*//if($sessionStorage.fromParams.type !="user"){
			$scope.state=$sessionStorage.fromStateName;
			$scope.stateparme=$sessionStorage.fromParams;
			$sessionStorage.showgraph=true;
			var data=$stateParams.id.split("_");
			var uuid=data[0];
			var version=data[1]
			$scope.getAllVersion(uuid)//Call SelectAllVersion Function
			$scope.selectVersion(uuid,version);//Call SelectVersion Function
		//}//End Inner If*/
		// }
		//else{
		$scope.getAllVersion($stateParams.id)//Call SelectAllVersion Function
		if (!$stateParams.version) {
			AdminUserService.getLatestByUuid($stateParams.id, "user").then(function (response) { onGetLatestByUuid(response.data) });
		} else {
			CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "user").then(function (response) { onGetLatestByUuid(response.data) });
		}
		var onGetLatestByUuid = function (response) {
			$scope.userdata = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.user.defaultVersion = defaultversion;
			//   $scope.roleInfoTags=response.roleInfo;
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

			//	    	    var roleInfo=[];
			//	    	    for(var j=0;j<response.roleInfo.length;j++){
			//	    	    	var roletag={};
			//	      	    	roletag.uuid=response.roleInfo[j].ref.uuid;
			//	    	    	roletag.type=response.roleInfo[j].ref.type;
			//	    	    	roletag.name=response.roleInfo[j].ref.name;
			//	    	    	roletag.id=response.roleInfo[j].ref.uuid;
			//	    	    	roleInfo[j]=roletag
			//	    	    }
			//	    	    $scope.roleInfoTags=roleInfo;

			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter If
		}//End getLatestByUuid
		//}//End Inner Else
	}/*End If*/




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
		var userJson = {};
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.userHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		userJson.uuid = $scope.userdata.uuid;
		userJson.name = $scope.userdata.name;
		userJson.desc = $scope.userdata.desc;
		userJson.active = $scope.userdata.active;
		userJson.published = $scope.userdata.published;
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
		}
		userJson.tags = tagArray

		//        var roleInfoArray=[];
		//        if($scope.roleInfoTags!=null){
		//	   		for(var c=0;c<$scope.roleInfoTags.length;c++){
		//		   		var roleinforef={};
		//		   		var roleref={};
		//		     	roleinforef.uuid=$scope.roleInfoTags[c].uuid;
		//		     	roleinforef.type="role";
		//	         	roleref.ref=roleinforef
		//		     	roleInfoArray.push(roleref);
		//		   }
		//		}
		//       	userJson.roleInfo=roleInfoArray

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
		console.log(JSON.stringify(groupInfoArray))
		AdminUserService.submit(userJson, 'user').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
