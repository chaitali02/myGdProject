/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.directive('lowercase', function () {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function (scope, element, attr, ngModel) {

			function fromUser(text) {
				return (text || '').toUpperCase();
			}

			function toUser(text) {
				return (text || '').toLowerCase();
			}
			ngModel.$parsers.push(fromUser);
			ngModel.$formatters.push(toUser);
		}
	};
});
DatascienceModule.controller('CreateParamListController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, ParamListService, privilegeSvc) {

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
	$scope.parantType=$stateParams.parantType;
	$scope.isSubmitEnable = true;
	$scope.paramlistData;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.paramlist = {};
	$scope.paramlist.versions = [];
	$scope.isshowmodel = false;
	$scope.paramtable = null;
	$scope.typeSimple = ["string", "double", "date", "integer", "list"];
	$scope.type = [
		{"name":"string","caption":"string"},
		{"name":"double","caption":"double"},
	 	{"name":"date","caption":"date"}, 
		{"name":"integer","caption":"integer"},
		// {"name":"ONEDARRAY","caption":"double [ ]"},
		// {"name":"TWODARRAY","caption":"double [ ][ ]"},
		{"name":"attribute","caption":"attribute"},
		{"name":"attributes","caption":"attribute[s]"},
		{"name":"distribution","caption":"distribution"},
		{"name":"datapod","caption":"datapod"},
	    {"name":"list","caption":"list"}, ];
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['paramlist'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['paramlist'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
   
	$scope.close=function(){
		$scope.parantType=$stateParams.parantType;
		$scope.type=$stateParams.type;
		var state=$scope.type
		if($scope.parantType){
			state=state+$scope.parantType
		}
		$state.go(state); 
	}

	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showgraph = false
		$scope.graphDataStatus = false;
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage()
		$state.go('createparamlist', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		$scope.showPage()
		$state.go('createparamlist', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	$scope.orderByValue = function (value) {
		return value;
	};

	$scope.addRow = function () {
		if ($scope.paramtable == null) {
			$scope.paramtable = [];
		}
		var paramjson = {}
		paramjson.paramId = $scope.paramtable.length;
		$scope.paramtable.splice($scope.paramtable.length, 0, paramjson);
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.paramtable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}

	$scope.removeRow = function () {
		var newDataList = [];
		$scope.selectallattribute = false;
		angular.forEach($scope.paramtable, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.paramtable = newDataList;
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph

	$scope.getAllVersion = function (uuid) {
		ParamListService.getAllVersionByUuid(uuid, "paramlist").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var paramlistversion = {};
				paramlistversion.version = response[i].version;
				$scope.paramlist.versions[i] = paramlistversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion




	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		ParamListService.getOneByUuidandVersion($stateParams.id, $stateParams.version, "paramlist").then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.paramlistData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.paramlist.defaultVersion = defaultversion;
			$scope.paramtable = response.paramInfo;
		}
	}//End If


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		ParamListService.getOneByUuidandVersion(uuid, version, 'paramlist').then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.paramlistData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.paramlist.defaultVersion = defaultversion;
			$scope.paramtable = response.params;
		}

	}
    $scope.getAllLatest=function(type,index){
		ParamListService.getAllLatest(type).then(function (response) { onGetAllLatest(response.data) });
		var onGetAllLatest = function (response) {
			$scope.allDistribution=response;
			$scope.paramtable[index].selectedParamValue=response[0];
		}
	}

	$scope.onChangeParamType=function(type,index){
		if($scope.typeSimple.indexOf(type) !=-1){
			$scope.paramtable[index].paramValueType='simple';
		}else{
			$scope.paramtable[index].paramValueType=type;
		}
	}

	$scope.submitParamList = function () {
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;
		var paramlistJson = {}
		paramlistJson.uuid = $scope.paramlistData.uuid
		paramlistJson.name = $scope.paramlistData.name
		paramlistJson.desc = $scope.paramlistData.desc
		paramlistJson.active = $scope.paramlistData.active;
		paramlistJson.published = $scope.paramlistData.published;
		if($scope.parantType){
	    	paramlistJson.paramListType = $scope.parantType;
			
		}
		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
		}
		paramlistJson.tags = tagArray;

		var paramInfoArray = [];
		if ($scope.paramtable.length > 0) {
			for (var i = 0; i < $scope.paramtable.length; i++) {
				var paraminfo = {};
				paraminfo.paramId = $scope.paramtable[i].paramId;
				paraminfo.paramName = $scope.paramtable[i].paramName;
				paraminfo.paramType = $scope.paramtable[i].paramType;
				var paramValue={}
				if($scope.typeSimple.indexOf($scope.paramtable[i].paramType) !=-1){
				 var paramRef={}	 
				 paramRef.type="simple";
				 paramValue.ref=paramRef;
				 paramValue.value=$scope.paramtable[i].paramValue;
				 paraminfo.paramValue =paramValue
				 paramInfoArray[i] = paraminfo; 
				}
				else if($scope.paramtable[i].paramType =='distribution'){
					var paramRef={};
					paramRef.type=$scope.paramtable[i].paramType;
					if($scope.paramtable[i].selectedParamValue !=null)
						paramRef.uuid=$scope.paramtable[i].selectedParamValue.uuid;
					paramValue.ref=paramRef;
					paraminfo.paramValue =paramValue
					paramInfoArray[i] = paraminfo; 
				}
				else {
					paramValue=null;
					paraminfo.paramValue =paramValue
					paramInfoArray[i] = paraminfo; 
				}	
			}	
			
			
		}

		paramlistJson.params = paramInfoArray;
		console.log(JSON.stringify(paramlistJson));
		ParamListService.submit(paramlistJson, 'paramlist').then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Parameter List Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okparamlsitsave();

		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}

	$scope.okparamlsitsave = function () {
		$('#paramlistsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			
			setTimeout(function () { $scope.close(); }, 2000);
		}
	}
});
