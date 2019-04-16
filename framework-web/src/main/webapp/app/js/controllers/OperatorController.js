/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('OperatorDetailController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, OperatorService, privilegeSvc,$filter,$timeout) {
	
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

	$scope.operatorType=['GenerateData','GenDataAttr','Transpose','CloneData','genDataValList','Matrix','Histogram','PCA','dataSampling',"CustomDq"];
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
	$scope.mode = false;
	$scope.isSubmitInProgress = false;
	$scope.isSubmitEnable = false;
	$scope.OperatorData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.Operator = {};
	$scope.Operator.versions = [];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['operator'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['operator'] || [];
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
		$scope.OperatorData.displayName=data;
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
		$state.go('createoperator', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.OperatorData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createoperator', {
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
			$state.go('createoperator', {
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
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
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
		OperatorService.getAllVersionByUuid(uuid, "operator").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var Operatorversion = {};
				Operatorversion.version = response[i].version;
				$scope.Operator.versions[i] = Operatorversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		$scope.getAllVersion($stateParams.id)
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "operator").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.OperatorData = response;
			$scope.isEditInprogess=false;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.Operator.defaultVersion = defaultversion;
			$scope.selectedOperatorType=response.operatorType;
			if($scope.OperatorData.paramList !=null){
				OperatorService.getAllLatest("paramlist").then(function (response){onSuccessGetAllLatestParamlist(response.data)}, function (response){onError(response.data)});
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.OperatorData.paramList.ref.uuid;
					paramlist.name = ""
					$scope.selectedParamlist = paramlist;
				}
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
		}
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End If
	else {
		$scope.OperatorData={};
		$scope.OperatorData.locked="N";
		OperatorService.getAllLatest("paramlist")
			.then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
		var onSuccessGetAllLatestParamlist = function (response) {
			$scope.allParamlist = response;
			$scope.selectedParamlist = $scope.allParamlist[0];
		}
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allOperatorType = null;
		$scope.selectedOperatorType = null;
		$scope.selectedLibrary = null;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		OperatorService.getOneByUuidandVersion(uuid, version, 'Operator')
			.then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.OperatorData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.Operator.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectedOperatorType=response.operatorType;
			if($scope.OperatorData.paramList !=null){
				OperatorService.getAllLatest("paramlist")
					.then(function (response) { onSuccessGetAllLatestParamlist(response.data) });
				var onSuccessGetAllLatestParamlist = function (response) {
					$scope.allParamlist = response;
					var paramlist = {};
					paramlist.uuid = $scope.OperatorData.paramList.ref.uuid;
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
		$scope.myform.$dirty = true;
		var OperatorJson = {}
		OperatorJson.uuid = $scope.OperatorData.uuid;
		OperatorJson.name = $scope.OperatorData.name;
		OperatorJson.displayName = $scope.OperatorData.displayName;
		OperatorJson.desc = $scope.OperatorData.desc;
		OperatorJson.active = $scope.OperatorData.active;
		OperatorJson.locked = $scope.OperatorData.locked;
		OperatorJson.published = $scope.OperatorData.published;
		OperatorJson.publicFlag = $scope.OperatorData.publicFlag;
  		OperatorJson.operatorType=$scope.selectedOperatorType;
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
		OperatorJson.tags = tagArray;
		var paramlist = {};
		if($scope.selectedParamlist !=null){
			var ref = {};
			ref.type = "paramlist";
			ref.uuid = $scope.selectedParamlist.uuid;
			paramlist.ref = ref;
			
		}else{
			paramlist=null;
		}
		OperatorJson.paramList = paramlist
		console.log(JSON.stringify(OperatorJson));
		OperatorService.submit(OperatorJson, 'operator',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Operator Saved Successfully'
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
			setTimeout(function () { $state.go("operator"); }, 2000);
		}
	}
});
