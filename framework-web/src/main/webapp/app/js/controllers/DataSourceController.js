
AdminModule = angular.module('AdminModule');

AdminModule.controller('MetadataDatasourceController', function (privilegeSvc, CommonService, $state,$rootScope,$scope, $stateParams, $sessionStorage, $timeout, MetadataDatasourceSerivce,$filter ) {

	$scope.isHiveFieldDisabled = true;
	$scope.isFileFieldDisabled = true;
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
	$scope.datasourcedata;
	$scope.showFrom = true;
	$scope.data = null;
	$scope.showGraphDiv = false;
	$scope.datasource = {};
	$scope.datasource.versions = [];
	$scope.datasourceTypes = ["HIVE", "FILE","ORACLE","IMPALA","POSTGRES","MYSQL"];
	$scope.datasourceHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.state = "admin";
	$scope.stateparme = { "type": "datasource" };
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['datasource'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
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
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['datasource'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.onChangeName = function (data) {
		$scope.datasourcedata.displayName=data;
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
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}//End showPage

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListdatasource', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.datasourcedata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListdatasource', {
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
			$state.go('adminListdatasource', {
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

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		//console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});

	
	$scope.datasourceFormChange = function () {
		if ($scope.mode == "true") {
			$scope.datasourceHasChanged = true;
		}
		else {
			$scope.datasourceHasChanged = false;
		}
	}

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}//End showFromGraph




	$scope.onChangeDatasoureType = function () {
		if ($scope.datasourcetype == $scope.datasourceTypes[0]) {
			$scope.isHiveFieldDisabled = false;
			$scope.isFileFieldDisabled = true;
			$scope.datasourcedata.path = ""
		}
		else {
			$scope.isFileFieldDisabled = false;
			$scope.isHiveFieldDisabled = true;
			$scope.datasourcedata.password = "";
			$scope.datasourcedata.username = "";
			$scope.datasourcedata.port = "";
			$scope.datasourcedata.dbname = "";
			$scope.datasourcedata.driver = "";
		}
	}//End onChangeDatasoureType

	$scope.getAllVersion = function (uuid) {
		MetadataDatasourceSerivce.getAllVersionByUuid(uuid, "datasource").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datasourceversion = {};
				datasourceversion.version = response[i].version;
				$scope.datasource.versions[i] = datasourceversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion = function (uuid, version) {
		$timeout(function () {
			$scope.myform.$dirty = false;
		}, 0)
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatasourceSerivce.getOneByUuidAndVersion(uuid, version, 'datasource')
			.then(function (response) { onSuccess(response.data) },function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.datasource.defaultVersion = defaultversion;
			$scope.datasourcedata = response
			$scope.datasourcetype = response.type;
			//$scope.onChangeDatasoureType();
			var tags = [];
			$scope.tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End for loop
			}//End Innder If
		};//End getLatestByUuid
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End selectVersion

	/*Start If*/
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		$scope.getAllVersion($stateParams.id)//Call SelectAllVersion Function
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'datasource')
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.datasource.defaultVersion = defaultversion;
			$scope.datasourcedata = response
			$scope.datasourcetype = response.type;
			//$scope.onChangeDatasoureType();
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End for loop
			}//End Innder If
		};//End getLatestByUuid
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
		/*}//End Inner Else */
	}//End If
	else{
		$scope.datasourcedata={};
		$scope.datasourcedata.locked="N";
	}



	/*Start Submit*/
	$scope.submitDatasource = function () {
		var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datasourceHasChanged = true;
		$scope.myform.$dirty = false;
		var datasourceJson = {};
		datasourceJson.uuid = $scope.datasourcedata.uuid;
		datasourceJson.name = $scope.datasourcedata.name;
		datasourceJson.displayName = $scope.datasourcedata.displayName;
		datasourceJson.desc = $scope.datasourcedata.desc;
		datasourceJson.createdOn = $scope.datasourcedata.createdOn
		datasourceJson.type = $scope.datasourcetype
		datasourceJson.port = $scope.datasourcedata.port
		datasourceJson.driver = $scope.datasourcedata.driver
		datasourceJson.username = $scope.datasourcedata.username
		datasourceJson.host = $scope.datasourcedata.host
		datasourceJson.access = $scope.datasourcedata.access
		datasourceJson.dbname = $scope.datasourcedata.dbname
		datasourceJson.password = $scope.datasourcedata.password
		datasourceJson.path = $scope.datasourcedata.path
		datasourceJson.active = $scope.datasourcedata.active;
		datasourceJson.locked = $scope.datasourcedata.locked;
		datasourceJson.published = $scope.datasourcedata.published;
		datasourceJson.publicFlag = $scope.datasourcedata.publicFlag;

		datasourceJson.sessionParameters = $scope.datasourcedata.sessionParameters
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
		datasourceJson.tags = tagArray
		MetadataDatasourceSerivce.submit(datasourceJson, 'datasource',upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			// $scope.changemodelvalue();
			
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Datasource Saved Successfully '
			$scope.$emit('notify', notify);
			$scope.okdatasourcesave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End Submit*/

	
	$scope.okdatasourcesave = function () {
		$('#datasourcesave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'datasource' }); }, 2000);
		}
	}

});
