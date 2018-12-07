/**
 *
 */
ProfileModule = angular.module('ProfileModule');

ProfileModule.controller('DetailProfileController', function (CommonService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, ProfileService, privilegeSvc) {
	$scope.select = 'Rule';
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
	$scope.mode = " ";
	$scope.profilegroup = {};
	$scope.profilegroup.versions = []
	$scope.showProfileGroup = true;
	$scope.showProfileGroupForm = true;
	$scope.isshowmodel = false;
	$scope.porfileTypes = ["datapod"];
	$scope.porfiletype = $scope.porfileTypes[0];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['profile'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['profile'] || [];
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

	$scope.close = function () {
		if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
			//revertback
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('viewprofile');
		}
	}

	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.showProfileGroupePage = function () {

		$scope.showProfileGroup = true;
		$scope.showgraphdiv = false;
		$scope.graphDataStatus = false;
		$scope.showProfileGroupForm = true;
	}

    $scope.showHome=function(uuid, version,mode){
		$scope.showProfileGroupePage();
		$state.go('createprofile', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.profileDetail.locked =="Y"){
			return false;
		}
		$scope.showProfileGroupePage()
		$state.go('createprofile', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showProfileGroupePage()
			$state.go('createprofile', {
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

	$scope.selectOption = function () {
		ProfileService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
		var onSuccessGetAllAttributeBySource = function (response) {
			$scope.allattribute = response
			$scope.profileTags = null;
		}
	}

	$scope.showProfileGroupGraph = function (uuid, version) {
		$scope.showProfileGroup = false;
		$scope.showgraphdiv = true;
		$scope.graphDataStatus = true;
		$scope.showProfileGroupForm = false;
		var NewUuid = uuid + "_" + version;
	}


	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.allDatapod = null;
		$scope.isDependencyShow = true;
		ProfileService.getAllVersionByUuid($stateParams.id, "profile").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var profilegroupversion = {};
				profilegroupversion.version = response[i].version;
				$scope.profilegroup.versions[i] = profilegroupversion;
			}
		}

		ProfileService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'profile').then(function (response) { onsuccess(response.data) });
		var onsuccess = function (response) {
			$scope.profileDetail = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.profilegroup.defaultVersion = defaultversion;

			$scope.tags = response.tags
			ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allDatapod = response;
				var defaultoption = {};
				defaultoption.uuid = $scope.profileDetail.dependsOn.ref.uuid;
				defaultoption.name = $scope.profileDetail.dependsOn.ref.name;
				$scope.allDatapod.defaultoption = defaultoption;

			}
			var profileAttributeArray = [];
			ProfileService.getAllAttributeBySource(response.dependsOn.ref.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response
			}

			for (var i = 0; i < response.attributeInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.attributeInfo[i].ref.uuid;
				ruleInfo.dname = response.attributeInfo[i].ref.name + "." + response.attributeInfo[i].attrName;
				ruleInfo.version = response.attributeInfo[i].ref.version;
				ruleInfo.attributeId = response.attributeInfo[i].attrId;
				ruleInfo.id = response.attributeInfo[i].ref.uuid + "_" + response.attributeInfo[i].attrId
				profileAttributeArray[i] = ruleInfo;
			}
			$scope.profileTags = profileAttributeArray
		}
	}
	else {
		$scope.profileDetail={};
		$scope.profileDetail.locked="N";
		ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.allDatapod = response;
			ProfileService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response
			}
		}
	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.allDatapod = null;
		ProfileService.getOneByUuidAndVersion($scope.profilegroup.defaultVersion.uuid, $scope.profilegroup.defaultVersion.version, 'profile').then(function (response) { onsuccess(response.data) });
		var onsuccess = function (response) {
			$scope.profileDetail = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.profilegroup.defaultVersion = defaultversion;

			$scope.tags = response.tags
			ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allDatapod = response;
				var defaultoption = {};
				defaultoption.uuid = $scope.profileDetail.dependsOn.ref.uuid;
				defaultoption.name = $scope.profileDetail.dependsOn.ref.name;
				$scope.allDatapod.defaultoption = defaultoption;
			}
			var profileAttributeArray = [];
			ProfileService.getAllAttributeBySource(response.dependsOn.ref.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response
			}

			for (var i = 0; i < response.attributeInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.attributeInfo[i].ref.uuid;
				ruleInfo.dname = response.attributeInfo[i].ref.name + "." + response.attributeInfo[i].attrName;
				ruleInfo.version = response.attributeInfo[i].ref.version;
				ruleInfo.attributeId = response.attributeInfo[i].attrId;
				ruleInfo.id = response.attributeInfo[i].ref.uuid + "_" + response.attributeInfo[i].attrId
				profileAttributeArray[i] = ruleInfo;
			}
			$scope.profileTags = profileAttributeArray
		}
	}

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	};

	$scope.clear = function () {
		$scope.profileTags = null;
	}
	
	$scope.addAll=function(){
		$scope.profileTags=$scope.allattribute;
	}
	$scope.okProfileGroupSave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('viewprofile'); }, 2000);
		}
	}

	$scope.sbumitProfileGroup = function () {
		var upd_tag="N"
		var profileJson = {}
		$scope.dataLoading = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		var options = {}
		options.execution = $scope.checkboxModelexecution;
		profileJson.uuid = $scope.profileDetail.uuid;
		profileJson.name = $scope.profileDetail.name;
		profileJson.desc = $scope.profileDetail.desc;
		profileJson.active = $scope.profileDetail.active;
		profileJson.locked = $scope.profileDetail.locked;
		profileJson.published = $scope.profileDetail.published;
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
		profileJson.tags = tagArray;
		var dependsOn = {}
		var ref = {};
		ref.type = "datapod";
		ref.uuid = $scope.allDatapod.defaultoption.uuid;
		dependsOn.ref = ref;
		profileJson.dependsOn = dependsOn;
		var ruleInfoArray = [];
		for (var i = 0; i < $scope.profileTags.length; i++) {
			var ruleInfo = {}
			var ref = {};
			ref.type = "datapod";
			ref.uuid = $scope.profileTags[i].uuid;
			// ref.version=$scope.profileTags[i].version;
			ruleInfo.ref = ref;
			ruleInfo.attrId = $scope.profileTags[i].attributeId
			ruleInfoArray[i] = ruleInfo;
		}
		profileJson.attributeInfo = ruleInfoArray;
		console.log(JSON.stringify(profileJson))
		ProfileService.submit(profileJson,"profile",upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.changemodelvalue();
			if (options.execution == "YES") {
				ProfileService.getOneById(response.data, 'profile').then(function (response) { onSuccessGetOneById(response.data) });
				var onSuccessGetOneById = function (response) {
					ProfileService.executeProfile(response.data.uuid, response.data.version).then(function (response) { onSuccess(response.data) });
					var onSuccess = function (response) {
						$scope.dataLoading = false;
						$scope.saveMessage = "Profile Rule Saved and Submitted Successfully"
						notify.type = 'success',
						notify.title = 'Success',
						notify.content = $scope.saveMessage
						$scope.$emit('notify', notify);
						$scope.okProfileGroupSave();
					}
				}//End onSuccessGetOneById
			}//End If
			else {
				$scope.dataLoading = false;
				$scope.saveMessage = "Profile Rule Saved Successfully"
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.saveMessage
				$scope.$emit('notify', notify);
				$scope.okProfileGroupSave();
			}//End Else
		}//End Submit Api Function
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End Submit Function

	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};

});

ProfileModule.controller('DetailProfileGroupController', function (privilegeSvc, CommonService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, ProfileService) {
	$scope.select = 'Rule Group';
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
	$scope.mode = " ";
	$scope.profilegroup = {};
	$scope.profilegroup.versions = []
	$scope.showProfileGroup = true;
	$scope.showProfileGroupForm = true;
	$scope.isshowmodel = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['profilegroup'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['profilegroup'] || [];
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
	
	$scope.showProfileGroupePage = function () {
		$scope.showProfileGroup = true;
		$scope.showgraphdiv = false;
		$scope.graphDataStatus = false;
		$scope.showProfileGroupForm = true;

	}

    $scope.showHome=function(uuid, version,mode){
		$scope.showProfileGroupePage();
		$state.go('createprofilegroup', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.profileGroupDetail.locked =="Y"){
			return false;
		}
		$scope.showProfileGroupePage()
		$state.go('createprofilegroup', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		$scope.showProfileGroupePage()
		$state.go('createprofilegroup', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}
	$scope.close = function () {
		if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
			//revertback
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('viewprofilegroup');
		}
	}
	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};
	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})
	ProfileService.getAllLatest('profile').then(function (response) { onSuccess(response.data) });
	var onSuccess = function (response) {

		var porfileArray = [];
		for (var i = 0; i < response.length; i++) {
			var profilejosn = {};
			profilejosn.uuid = response[i].uuid;
			profilejosn.id = response[i].uuid
			profilejosn.name = response[i].name;
			profilejosn.version = response[i].version;
			porfileArray[i] = profilejosn;
		}

		$scope.profileall = porfileArray;
	}

	$scope.showProfileGroupGraph = function (uuid, version) {
		$scope.showProfileGroup = false;
		$scope.showgraphdiv = true;
		$scope.graphDataStatus = true;
		$scope.showProfileGroupForm = false;
	}



	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;

		$scope.isDependencyShow = true;
		ProfileService.getAllVersionByUuid($stateParams.id, "profilegroup").then(function (response) { onGetAllVersionByUuid(response.data) });

		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var profilegroupversion = {};
				profilegroupversion.version = response[i].version;
				$scope.profilegroup.versions[i] = profilegroupversion;

			}

		}
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'profilegroup').then(function (response) { onsuccess(response.data) });
		var onsuccess = function (response) {
			//console.log(JSON.stringify(response))
			$scope.profileGroupDetail = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.profilegroup.defaultVersion = defaultversion;
			$scope.tags = response.tags
			$scope.checkboxModelparallel = response.inParallel;
			var profileTagArray = [];
			for (var i = 0; i < response.ruleInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.ruleInfo[i].ref.uuid;
				ruleInfo.name = response.ruleInfo[i].ref.name;
				ruleInfo.version = response.ruleInfo[i].ref.version;
				ruleInfo.id = response.ruleInfo[i].ref.uuid

				profileTagArray[i] = ruleInfo;
			}
			$scope.profileTags = profileTagArray
		}
	}else{
		$scope.profileGroupDetail={}
		$scope.profileGroupDetail.locked="N";
	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		ProfileService.getOneByUuidAndVersion($scope.profilegroup.defaultVersion.uuid, $scope.profilegroup.defaultVersion.version, 'profilegroup').then(function (response) { onsuccess(response.data) });
		var onsuccess = function (response) {
			//console.log(JSON.stringify(response))
			$scope.profileGroupDetail = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.profilegroup.defaultVersion = defaultversion;
			$scope.tags = response.tags
			var profileTagArray = [];
			for (var i = 0; i < response.ruleInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.ruleInfo[i].ref.uuid;
				ruleInfo.name = response.ruleInfo[i].ref.name;
				ruleInfo.version = response.ruleInfo[i].ref.version;
				ruleInfo.id = response.ruleInfo[i].ref.uuid

				profileTagArray[i] = ruleInfo;
			}
			$scope.profileTags = profileTagArray
		}
	}
	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.profileall, query);
		});
	};
	$scope.clear = function () {

		$scope.profileTags = null;
	}

	$scope.okProfileGroupSave = function () {
		$('#profilegroupsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('viewprofilegroup'); }, 2000);

		}


	}
	$scope.sbumitProfileGroup = function () {
		var upd_tag="N"
		var profileGroupJson = {}
		$scope.dataLoading = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		var options = {}
		options.execution = $scope.checkboxModelexecution;
		profileGroupJson.uuid = $scope.profileGroupDetail.uuid;
		profileGroupJson.name = $scope.profileGroupDetail.name;
		profileGroupJson.desc = $scope.profileGroupDetail.desc;
		profileGroupJson.active = $scope.profileGroupDetail.active;
		profileGroupJson.locked = $scope.profileGroupDetail.locked;
		profileGroupJson.published = $scope.profileGroupDetail.published;
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
		profileGroupJson.tags = tagArray;
		var ruleInfoArray = [];
		for (var i = 0; i < $scope.profileTags.length; i++) {
			var ruleInfo = {}
			var ref = {};
			ref.type = "profile";
			ref.uuid = $scope.profileTags[i].uuid;
			// ref.version=$scope.profileTags[i].version;
			ruleInfo.ref = ref;
			ruleInfoArray[i] = ruleInfo;
		}
		profileGroupJson.ruleInfo = ruleInfoArray;
		profileGroupJson.inParallel = $scope.checkboxModelparallel
		console.log(JSON.stringify(profileGroupJson))
		ProfileService.submit(profileGroupJson, "profilegroup",upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			if (options.execution == "YES") {
				ProfileService.getOneById(response.data, 'profilegroup').then(function (response) { onSuccessGetOneById(response.data) });
				var onSuccessGetOneById = function (response) {
					ProfileService.executeProfileGroup(response.data.uuid, response.data.version).then(function (response) { onSuccess(response.data) });
					var onSuccess = function (response) {
						$scope.dataLoading = false;
						$scope.saveMessage = "Profile Rule Group Saved and Submitted Successfully"
						notify.type = 'success',
						notify.title = 'Success',
						notify.content = $scope.saveMessage
						$scope.$emit('notify', notify);
						$scope.okProfileGroupSave();
					}
				}//End onSuccessGetOneById
			}//End If
			else {
				$scope.dataLoading = false;
				$scope.saveMessage = "Profile Rule Group Saved Successfully"
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.saveMessage
				$scope.$emit('notify', notify);
				$scope.okProfileGroupSave();
			}//End Else
		}//End Submit Api Function
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End Submit Function

});

ProfileModule.controller('ResultProfileController', function ($http, dagMetaDataService, $timeout, $filter, $state, $stateParams, $location, $rootScope, $scope, ProfileService, CommonService,privilegeSvc,CF_DOWNLOAD) {
	$scope.select = $stateParams.type;
	$scope.type = { text: $scope.select == 'profilegroupexec' ? 'profilegroup' : 'profile' };
	$scope.showprogress = false;
	$scope.isRuleExec = false;
	$scope.isRuleResult = false;
	$scope.showZoom = false;
	$scope.isD3RuleEexecGraphShow = false;
	$scope.isD3RGEexecGraphShow = false;
	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	$scope.download={};
    $scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
    $scope.download.formates=CF_DOWNLOAD.formate;
    $scope.download.selectFormate=CF_DOWNLOAD.formate[0];
    $scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
    $scope.download.limit_to=CF_DOWNLOAD.limit_to; 
	// ui grid
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'height': '40px'
		}
		if ($scope.filteredRows) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 80) + 'px';
		}
		return style;
	}
	var privileges = privilegeSvc.privileges['comment'] || [];
	$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
	$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
	$scope.$on('privilegesUpdated', function (e, data) {
	  var privileges = privilegeSvc.privileges['comment'] || [];
	  $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
	  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
	  
	});
	$scope.metaType=dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType;
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName; 
	$scope.filteredRows = [];
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

	$scope.refreshRuleExecData = function () {
		$scope.gridOptionsRule.data = $filter('filter')($scope.orignalRuleExecData, $scope.searchruletext, undefined);
	}

	$scope.refreshRGExecData = function () {
		$scope.gridOptionsRuleGroup.data = $filter('filter')($scope.orignalRGExecData, $scope.searchrGtext, undefined);
	}
	// ui grid
	
	//For Breadcrum
	$scope.$on('daggroupExecChanged', function (e, groupExecName) {
		$scope.daggroupExecName = groupExecName;
	})

	$scope.$on('resultExecChanged', function (e, resultExecName) {
		$scope.resultExecName = resultExecName;
	})
	//For Breadcrum

	// Not required instead genericClose

	// $scope.onClickRuleExec=function(){
	//   $scope.isRuleSelect=true;
	//   if($scope.isRuleResult){
	//       $scope.onClickRuleResult();
	//   }
	//     $scope.$emit('daggroupExecChanged',false);//Update Breadcrum
	//     $('#grouploader').show();
	//     if($scope.type.text == "profilegroup"){
	//        	$scope.isRuleGroupExec=true;
	//        	$scope.isRuleExec=false;
	//        		//$scope.getAllProfileGroupExec();
	//     }
	//     else{
	//      	$scope.isRuleSelect=true;
	//      	$scope.isRuleExec=false;
	//      	$scope.ruledata="";
	//     }
	// }

	$scope.onClickRuleResult = function () {
		$scope.isRuleExec = true;
		$scope.isRuleResult = false;
		$scope.isD3RuleEexecGraphShow=false;
		$scope.execDetail=$scope.profileGroupLastParams
		$scope.metaType=dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType; 
		$scope.$emit('resultExecChanged', false);//Update Breadcrum
	}


	$scope.getProfileExec = function (data) {
		$scope.execDetail=data;
		$scope.metaType=dagMetaDataService.elementDefs["profile"].execType; 
		var uuid = data.uuid;
		var version = data.version;
		var name = data.name;
		$scope.ruleExecUuid = uuid;
		$scope.ruleExecVersion = version;
		var params = { "id": uuid, "name": name, "elementType": "profile", "version": version, "type": "profile", "typeLabel": "Profile" }
		window.showResult(params);
	}

	$scope.$watch("zoomSize", function (newData, oldData) {
		$scope.$broadcast('zoomChange', newData);
	});

	window.navigateTo = function (url) {
		var state = JSON.parse(url);
		$rootScope.previousState = { name: $state.current.name, params: $state.params };
		var ispresent = false;
		if (ispresent != true) {
			var stateTab = {};
			stateTab.route = state.state;
			stateTab.param = state.params;
			stateTab.active = false;
			$rootScope.$broadcast('onAddTab', stateTab);
		}
		$state.go(state.state, state.params);
	}

	window.showResult = function (params) {
		App.scrollTop();
		$scope.lastParams = params;
		if (params.type.slice(-5).toLowerCase() == 'group') {
			$scope.isRuleExec = true;
			$scope.isProfileGroupExec = true;
			$scope.$broadcast('generateGroupGraph', params);
		}
		else {
			$scope.isRuleResult = true;
			$scope.isRuleExec = false;
			$scope.isDataInpogress = true;
			$scope.spinner = true;
			$scope.execDetail=params;
			$scope.execDetail.uuid=params.id;
			$scope.metaType=dagMetaDataService.elementDefs["profile"].execType; 
		
			setTimeout(function () {
				$scope.$apply();
				$scope.ruleExecUuid = params.id;
				$scope.ruleExecVersion = params.version;
				$scope.$broadcast('generateResults', params);
				$scope.$emit('resultExecChanged', params.name);  //For Breadcrum
			}, 100);
		}
	}
	$scope.refreshData = function (searchtext) {
		$scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
	};

	window.refreshResultfunction = function () {
		$scope.isD3RuleEexecGraphShow = false;
		window.showResult($scope.lastParams);
	}

	$scope.ruleExecshowGraph = function () {
		$scope.isD3RuleEexecGraphShow = true;
	}

	$scope.rGExecshowGraph = function () {
		$scope.isProfileGroupExec = false;
		$scope.isD3RGEexecGraphShow = true;
	}
	$scope.profileGroupExec = function (data) {
		if ($scope.type.text == 'profile') {
			$scope.getProfileExec(data);
			return
		}
		$scope.execDetail=data;
		$scope.metaType=dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType; 
		$scope.profileGroupLastParams = data;
		$scope.zoomSize = 7;
		var uuid = data.uuid;
		var version = data.version;
		var name = data.name;
		$scope.rGExecUuid = uuid;
		$scope.rGExecVersion = version;
		$scope.isRuleSelect = false;
		$scope.isRuleGroupExec = false;
		$scope.isRuleExec = true;
		if ($scope.type.text == 'profilegroup') {
			$scope.isProfileGroupExec = true;
		}
		else {
			$scope.isProfileGroupExec = false;
		}
		var params = { "id": uuid, "name": name, "elementType": "profilegroup", "version": version, "type": "profilegroup", "typeLabel": "ProfileGroup", "url": "profile/getProfileExecByProfileGroupExec?", "ref": { "type": "profilegroupExec", "uuid": uuid, "version": version, "name": name } }
		setTimeout(function () {
			$scope.$broadcast('generateGroupGraph', params);
		}, 100);
	}
	$scope.getExec = $scope.profileGroupExec;
	$scope.getExec({
		uuid: $stateParams.id,
		version: $stateParams.version,
		name: $stateParams.name
	});

	$scope.reGroupExecute = function () {
		$('#reExModal').modal({
			backdrop: 'static',
			keyboard: false
		});

	}
	$scope.okReGroupExecute = function () {
		$('#reExModal').modal('hide');
		$scope.executionmsg = "Profile Group Restarted Successfully"
		notify.type = 'success',
		notify.title = 'Success',
		notify.content = $scope.executionmsg
		$rootScope.$emit('notify', notify);
		CommonService.restartExec("profilegroupExec", $stateParams.id, $stateParams.version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			//$scope.refreshRuleGroupExecFunction();
		}
		$scope.refreshRuleGroupExecFunction();
	}

	$scope.refreshRuleGroupExecFunction = function () {
		$scope.isD3RGEexecGraphShow = false;
		$scope.profileGroupExec($scope.profileGroupLastParams);
	}

	$scope.toggleZoom = function () {
		$scope.showZoom = !$scope.showZoom;
	}
   
	$scope.submitDownload=function(){
		var uuid = $scope.download.data.uuid;
		var version = $scope.download.data.version;
		var url = $location.absUrl().split("app")[0];
		$('#downloadSample').modal("hide");
		$http({
			method: 'GET',
			url: url + "profile/download?action=view&profileExecUUID=" + uuid + "&profileExecVersion=" + version+"&rows="+$scope.download.rows,
			responseType: 'arraybuffer'
		}).success(function (data, status, headers) {
			$scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
		 
			headers = headers();
			var filename = headers['filename'];
			var contentType = headers['content-type'];
			var linkElement = document.createElement('a');
			try {
				var blob = new Blob([data], {
					type: contentType
				});
				var url = window.URL.createObjectURL(blob);
				linkElement.setAttribute('href', url);
				linkElement.setAttribute("download", filename);
				var clickEvent = new MouseEvent("click", {
					"view": window,
					"bubbles": true,
					"cancelable": false
				});
				linkElement.dispatchEvent(clickEvent);
			} catch (ex) {
				console.log(ex);
			}
		}).error(function (data) {
			console.log(data);
		});
	}
	
	$scope.downloadFilePofile = function (data) {
        if($scope.isD3RuleEexecGraphShow){
			return false;
		}
		$scope.download.data=data;
        $('#downloadSample').modal({
          backdrop: 'static',
          keyboard: false
        });

	};

});
