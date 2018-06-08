AdminModule = angular.module('AdminModule');

AdminModule.controller('MetadataDatastoreController', function (CommonService, $state,$rootScope, $scope, $stateParams, $timeout, MetadataDatastoreSerivce, privilegeSvc) {
	$scope.mode = " ";
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
	$scope.datastoreHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.datastoredata;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.datastore = {};
	$scope.datastore.versions = [];
	$scope.datastoremetaTypes = ["datapod", "map", "rule"];
	$scope.datastoreexecTypes = ["loadExec", "mapExec", "dqExec", "ruleExec", "profileExec"];
	$scope.isshowmodel = false;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['datastore'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['datastore'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('adminListdatastore', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('adminListdatastore', {
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
	$timeout(function () {
		$scope.myform.$dirty = false;
	}, 0);



	$scope.datastoreFormChange = function () {
		if ($scope.mode == "true") {
			$scope.datastoreHasChanged = true;
		}
		else {
			$scope.datastoreHasChanged = false;
		}
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;

	}



	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	$scope.onChangeDatastoreMeta = function () {
		MetadataDatastoreSerivce.getAllLatest($scope.datastoremetatype).then(function (response) { onSuccessMetaType(response.data) });
		var onSuccessMetaType = function (response) {
			$scope.datastoreAllMata = response
		}
	}//end onChangeDatastoreMeta

	$scope.onChangeDatastoreExec = function () {
		MetadataDatastoreSerivce.getAllLatest($scope.datastoreexectype).then(function (response) { onSuccessExecType(response.data) });
		var onSuccessExecType = function (response) {
			$scope.datastoreAllExec = response
		}
	}//end onChangeDatastoreExec

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode

		$scope.isDependencyShow = true;
		MetadataDatastoreSerivce.getAllVersionByUuid($stateParams.id, "datastore").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datastoreversion = {};
				datastoreversion.version = response[i].version;
				$scope.datastore.versions[i] = datastoreversion;
			}
		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'datastore').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.datastoredata = response
			$scope.datastoremetatype = response.metaId.ref.type;
			$scope.datastoreexectype = response.execId.ref.type
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.datastore.defaultVersion = defaultversion;
			$scope.datastoreName = $scope.convertUppdercase($scope.datastoredata.name)
			MetadataDatastoreSerivce.getAllLatest(response.metaId.ref.type).then(function (response) { onSuccessMetaType(response.data) });
			var onSuccessMetaType = function (response) {
				$scope.datastoreAllMata = response
				var defaultoption = {};
				defaultoption.uuid = $scope.datastoredata.metaId.ref.uuid;
				defaultoption.name = $scope.datastoredata.metaId.ref.name;
				defaultoption.version = $scope.datastoredata.metaId.ref.version;
				$scope.datastoreAllMata.defaultoption = defaultoption;
			}
			MetadataDatastoreSerivce.getAllLatest(response.execId.ref.type).then(function (response) { onSuccessExecType(response.data) });
			var onSuccessExecType = function (response) {
				$scope.datastoreAllExec = response
				var defaultoption = {};
				defaultoption.uuid = $scope.datastoredata.execId.ref.uuid;
				defaultoption.name = $scope.datastoredata.execId.ref.name;
				defaultoption.version = $scope.datastoredata.execId.ref.version;
				$scope.datastoreAllExec.defaultoption = defaultoption;
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter if
		}
	}//End If


	$scope.selectVersion = function () {
		$scope.datastoreAllMata = null;
		$scope.datastoreAllExec = null;
		$scope.datastoremetatype = null;
		$scope.datastoreexectype = null;
		$scope.tags = null;
		$timeout(function () {
			$scope.myform.$dirty = false;
		}, 0);

		MetadataDatastoreSerivce.getOneByUuidAndVersion($scope.datastore.defaultVersion.uuid, $scope.datastore.defaultVersion.version, 'datastore').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.datastoredata = response
			$scope.datastoremetatype = response.metaId.ref.type;
			$scope.datastoreexectype = response.execId.ref.type
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.datastore.defaultVersion = defaultversion;
			$scope.datastoreName = $scope.convertUppdercase($scope.datastoredata.name)
			MetadataDatastoreSerivce.getAllLatest(response.metaId.ref.type).then(function (response) { onSuccessMetaType(response.data) });
			var onSuccessMetaType = function (response) {
				$scope.datastoreAllMata = response
				var defaultoption = {};
				defaultoption.uuid = $scope.datastoredata.metaId.ref.uuid;
				defaultoption.name = $scope.datastoredata.metaId.ref.name;
				$scope.datastoreAllMata.defaultoption = defaultoption;
			}
			MetadataDatastoreSerivce.getAllLatest(response.execId.ref.type).then(function (response) { onSuccessExecType(response.data) });
			var onSuccessExecType = function (response) {
				$scope.datastoreAllExec = response
				var defaultoption = {};
				defaultoption.uuid = $scope.datastoredata.execId.ref.uuid;
				defaultoption.name = $scope.datastoredata.execId.ref.name;
				$scope.datastoreAllExec.defaultoption = defaultoption;
			}
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}//End Innter if
		}

	}//End selectVersion


	/*Start SubmitDatastore*/
	$scope.submitDatastore = function () {
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datastoreHasChanged = true;
		$scope.myform.$dirty = false;

		var datastroreJson = {}
		datastroreJson.uuid = $scope.datastoredata.uuid;
		datastroreJson.name = $scope.datastoredata.name
		datastroreJson.desc = $scope.datastoredata.desc
		datastroreJson.active = $scope.datastoredata.active;
		datastroreJson.published = $scope.datastoredata.published;
		datastroreJson.location = $scope.datastoredata.location;
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
		}
		datastroreJson.tags = tagArray;
		var datastoremeta = {};
		var metaref = {}
		var datastoreexec = {};
		var execref = {}
		metaref.type = $scope.datastoremetatype;
		metaref.uuid = $scope.datastoreAllMata.defaultoption.uuid;
		metaref.version = $scope.datastoreAllMata.defaultoption.version;
		datastoremeta.ref = metaref;
		datastroreJson.metaId = datastoremeta;

		execref.type = $scope.datastoreexectype;
		execref.uuid = $scope.datastoreAllExec.defaultoption.uuid;
		execref.version = $scope.datastoreAllExec.defaultoption.version;
		datastoreexec.ref = execref;
		datastroreJson.execId = datastoreexec;
		console.log(JSON.stringify(datastroreJson))
		MetadataDatastoreSerivce.submit(datastroreJson, 'datastore').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Datastore Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okdatastoresave();
		}//End Submit Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitDatastore*/

	

	$scope.okdatastoresave = function () {
		$('#datastoresave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'datastore' }); }, 2000);
		}
	}

})
