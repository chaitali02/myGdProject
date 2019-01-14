AdminModule = angular.module("AdminModule");

AdminModule.controller('OrganizationDetailController', function ($state, $stateParams, $scope, $rootScope, $timeout, $filter, $sessionStorage, OrganizationSerivce, privilegeSvc, CommonService, CF_ORGANIZATION) {

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
	$scope.organizationData;
	$scope.showForm = true;
	$scope.showGraphDiv = false;
	$scope.organization = {};
	$scope.organization.versions = [];
	$scope.isshowmodel = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.contactTitle = CF_ORGANIZATION.contactTitle;
	$scope.phoneType = CF_ORGANIZATION.phoneType;
	$scope.emailType = CF_ORGANIZATION.emailType;
	$scope.addressType = CF_ORGANIZATION.addressType;
	$scope.selectedAllPhone=false;
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.privileges = privilegeSvc.privileges['organization'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['organization'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});


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

	$scope.getLovByType();

	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};

	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}

	$scope.showGraph = function () {
		$scope.showForm = false;
		$scope.showGraphDiv = true;

	}/*End ShowGraph*/

	$scope.showHome = function (uuid, version, mode) {
		$scope.showPage()
		$state.go('organizationdetail', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.organizationData.locked == "Y") {
			return false;
		}
		$scope.showPage()
		$state.go('organizationdetail', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showView = function (uuid, version) {
		$scope.showPage()
		$state.go('organizationdetail', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}


	$scope.getAllVersion = function (uuid) {
		OrganizationSerivce.getAllVersionByUuid(uuid, "organization").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var organizationVersion = {};
				organizationVersion.version = response[i].version;
				$scope.organization.versions[i] = organizationVersion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion
	
	$scope.getOneByUuidAndVersion=function(uuid,version){
		OrganizationSerivce.getOneByUuidAndVersion(uuid,version, "organization")
			.then(function (response) { onSuccessGetOneByUuidAndVersion(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetOneByUuidAndVersion = function (response) {
			$scope.organizationData = response;
			var defaultVersion = {};
			defaultVersion.version = response.version;
			defaultVersion.uuid = response.uuid;
			$scope.organization.defaultVersion = defaultVersion;
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = false;
			$scope.contactTableInfo = response.contact;
			$scope.phoneTableInfo = response.phone;
			$scope.emailTableInfo = response.email;
			$scope.addressTableInfo = response.address;
		}
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		}
	}


	if (typeof $stateParams.id != 'undifined') {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.showactive = "true";
		var id;
		id = $stateParams.id;
		$scope.getAllVersion(id)//Call SelectAllVersion Function
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		$scope.getOneByUuidAndVersion($stateParams.id, $stateParams.version);
		
	}
	else {
		$scope.organizationData = {};
		$scope.organizationData.locked = "N";
	}

	 $scope.selectVersion=function(uuid,version){
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		$scope.getOneByUuidAndVersion(uuid,version);
	 }

	
	$scope.selectAllContactRow = function () {
		if (!$scope.selectedAllContact) {
			$scope.selectedAllContact = true;
		}
		else {
			$scope.selectedAllContact = false;
		}
		angular.forEach($scope.contactTableInfo, function (filter) {
			filter.selected = $scope.selectedAllContact;
		});
	}
	$scope.addRowContact = function () {
		if ($scope.contactTableInfo == null) {
			$scope.contactTableInfo = [];
		}
		var contactTableInfo = {};
		var len = $scope.contactTableInfo.length;
		$scope.contactTableInfo.splice(len, 0, contactTableInfo);
	}

	$scope.removeRowContact = function () {
		var newDataList = [];
		$scope.selectedAllContact = false;
		angular.forEach($scope.contactTableInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});

		$scope.contactTableInfo = newDataList;
	}

	$scope.selectAllPhoneRow = function () {
		if (!$scope.selectedAllPhone) {
			$scope.selectedAllPhone = true;
		}
		else {
			$scope.selectedAllPhone = false;
		}
		angular.forEach($scope.phoneTableInfo, function (filter) {
			filter.selected = $scope.selectedAllPhone;
		});
	}
	$scope.addRowPhone = function () {
		if ($scope.phoneTableInfo == null) {
			$scope.phoneTableInfo = [];
		}
		var phoneTableInfo = {};
		var len = $scope.phoneTableInfo.length;
		$scope.phoneTableInfo.splice(len, 0, phoneTableInfo);
	}

	$scope.removeRowPhone = function () {
		
		var newDataList = [];
		angular.forEach($scope.phoneTableInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.phoneTableInfo = newDataList;
		$scope.selectedAllPhone ="";
		setTimeout(function(){
			$scope.selectedAllPhone = false;
		},10);

	}

	$scope.selectAllEmailRow = function () {
		if (!$scope.selectedAllEmail) {
			$scope.selectedAllEmail = true;
		}
		else {
			$scope.selectedAllEmail = false;
		}
		angular.forEach($scope.emailTableInfo, function (filter) {
			filter.selected = $scope.selectedAllEmail;
		});
	}

	$scope.addRowEmail = function () {
		$scope.selectedAllEmail = false;
		if ($scope.emailTableInfo == null) {
			$scope.emailTableInfo = [];
		}
		var emailTableInfo = {};
		var len = $scope.emailTableInfo.length;
		$scope.emailTableInfo.splice(len, 0, emailTableInfo);
	}

	$scope.removeRowEmail = function () {
		var newDataList = [];
		$scope.selectedAllEmail = false;
		angular.forEach($scope.emailTableInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});

		$scope.emailTableInfo = newDataList;
	}

	$scope.selectAllAddressRow = function () {
		if (!$scope.selectedAllAddress) {
			$scope.selectedAllAddress = true;
		}
		else {
			$scope.selectedAllAddress = false;
		}
		angular.forEach($scope.addressTableInfo, function (filter) {
			filter.selected = $scope.selectedAllAddress;
		});
	}

	$scope.addRowAddress = function () {
		if ($scope.addressTableInfo == null) {
			$scope.addressTableInfo = [];
		}
		var addressTableInfo = {};
		var len = $scope.addressTableInfo.length;
		$scope.addressTableInfo.splice(len, 0, addressTableInfo);
	}

	$scope.removeRowAddress = function () {
		var newDataList = [];
		$scope.selectedAllAddress = false;
		angular.forEach($scope.addressTableInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.addressTableInfo = newDataList;
	}


	$scope.submit = function () {
		$scope.dataLoading=true;
		$scope.myform.$dirty = false;
		$scope.iSSubmitEnable = true;
		var orgJson = {};
		orgJson.uuid = $scope.organizationData.uuid
		orgJson.name = $scope.organizationData.name
		orgJson.desc = $scope.organizationData.desc
		orgJson.active = $scope.organizationData.active;
		orgJson.locked = $scope.organizationData.locked;
		orgJson.published = $scope.organizationData.published;
		var tagArray = [];
		var upd_tag = "N";
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
			}
		}
		orgJson.tags = tagArray;
		var contactArrayTable = [];
		if ($scope.contactTableInfo && $scope.contactTableInfo.length > 0) {
			for (var i = 0; i < $scope.contactTableInfo.length; i++) {
				var contactTableInfo = {};
				contactTableInfo.title = $scope.contactTableInfo[i].title;
				contactTableInfo.name = $scope.contactTableInfo[i].name;
				contactTableInfo.phone = $scope.contactTableInfo[i].phone;
				contactTableInfo.emailId = $scope.contactTableInfo[i].emailId;
				contactArrayTable[i] = contactTableInfo;
			}
		}

		orgJson.contact = contactArrayTable;

		var phoneArrayTable = [];
		if ($scope.phoneTableInfo && $scope.phoneTableInfo.length > 0) {
			for (var j = 0; j < $scope.phoneTableInfo.length; j++) {
				var phoneTableInfo = {};
				phoneTableInfo.type = $scope.phoneTableInfo[j].type;
				phoneTableInfo.number = $scope.phoneTableInfo[j].number;
				phoneArrayTable[j] = phoneTableInfo;
			}
		}

		orgJson.phone = phoneArrayTable;

		var emailArrayTable = [];
		if ($scope.emailTableInfo && $scope.emailTableInfo.length > 0) {
			for (var j = 0; j < $scope.emailTableInfo.length; j++) {
				var emailTableInfo = {};
				emailTableInfo.type = $scope.emailTableInfo[j].type;
				emailTableInfo.emailId = $scope.emailTableInfo[j].emailId;
				emailArrayTable[j] = emailTableInfo;
			}
		}

		orgJson.email = emailArrayTable;
		var addressArrayTable = [];
		if ($scope.addressTableInfo && $scope.addressTableInfo.length > 0) {
			for (var j = 0; j < $scope.addressTableInfo.length; j++) {
				var addresTableInto = {};
				addresTableInto.type = $scope.addressTableInfo[j].type;
				addresTableInto.addressLine1 = $scope.addressTableInfo[j].addressLine1;
				addresTableInto.addressLine2 = $scope.addressTableInfo[j].addressLine2;
				addresTableInto.city = $scope.addressTableInfo[j].city;
				addresTableInto.state = $scope.addressTableInfo[j].state;
				addresTableInto.country = $scope.addressTableInfo[j].country;
				addresTableInto.zipcode = $scope.addressTableInfo[j].zipcode;
				addressArrayTable[j] = addresTableInto;
			}
		}

		orgJson.address = addressArrayTable;
		console.log(JSON.stringify(orgJson));
		OrganizationSerivce.submit(orgJson, 'organization', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Organization Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okSave();
		}//End Submit Api
		var onError = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submit*/




	$scope.close = function () {
		$state.go('admin', { 'type': 'organization' });
	}

	$scope.okSave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'organization' }); }, 2000);
		}
	}


});