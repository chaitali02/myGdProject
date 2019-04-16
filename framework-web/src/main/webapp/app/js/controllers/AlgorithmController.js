/**
 **/
DatascienceModule = angular.module('DatascienceModule');
DatascienceModule.controller('CreateAlgorithmController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, AlgorithmService, privilegeSvc,$timeout,$filter) {
	
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
	$scope.dataLoading = false;
	$scope.isSubmitEnable = true;
	$scope.algorithmData;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.algorithm = {};
	$scope.algorithm.versions = [];
	$scope.isshowmodel = false;
	//$scope.librarytypes = ["sparkML", "R", "Java"];
	$scope.librarytypes = ["SPARKML", "R", "JAVA","PYTHON","DL4J","TENSORFLOW"];
	$scope.paramtable = null;
	//$scope.types = ["clustering", "classification", "regression", "simulation"];
	$scope.types = ["CLUSTERING", "CLASSIFICATION", "REGRESSION", "SIMULATION"];
	$scope.isDependencyShow = false;
	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['algorithm'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['algorithm'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

    $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

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
		$scope.algorithmData.displayName=data;
	}
	
    $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph

	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}

    $scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		
		$state.go('createalgorithm', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.algorithmData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('createalgorithm', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if(!$scope.isEdit){
			$scope.showPage()
			$state.go('createalgorithm', {
				id: uuid,
				version: version,
				mode: 'true'
			});
	    }
	}
	$scope.onChangeCusFlg=function(){
		if($scope.algorithmData.customFlag =='Y'){
			$scope.algorithmData.scriptName="";
		}else{
			$scope.algorithmData.trainClass="";
			$scope.algorithmData.modelClass="";
		}
	}
	$scope.getAllLatestParamListByTemplate=function(){
		CommonService.getAllLatestParamListByTemplate('Y', "paramlist","model").then(function (response) { onSuccessGetAllLatestParamListByTemplate(response.data) });
		var onSuccessGetAllLatestParamListByTemplate = function (response) {
			$scope.allParamList=response;
		}//End getAllLatestParamListByTemplate
	}

	$scope.getAllVersion = function (uuid) {
		AlgorithmService.getAllVersionByUuid(uuid, "algorithm").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var algorithmversion = {};
				algorithmversion.version = response[i].version;
				$scope.algorithm.versions[i] = algorithmversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id)
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "algorithm")
			.then(function (response) { onSuccessGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.algorithmData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.algorithm.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectlibrary = response.libraryType
	
			$scope.getAllLatestParamListByTemplate();
			var selectParamlistWithoutHype={};
			selectParamlistWithoutHype.uuid=response.paramListWoH.ref.uuid;
			selectParamlistWithoutHype.name=response.paramListWoH.ref.name;
			$scope.selectParamlistWithoutHyper=selectParamlistWithoutHype;
			var selectParamlistWithHype={};
			selectParamlistWithHype.uuid=response.paramListWH.ref.uuid;
			selectParamlistWithHype.name=response.paramListWH.ref.name;
			$scope.selectParamlistWithHyper=selectParamlistWithHype;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			var summaryMethodsArray=[];
			if(response.summaryMethods !=null){
				for(var i=0;i<response.summaryMethods.length;i++){
					var summaryMethods={};
					summaryMethods.text=response.summaryMethods[i];
					summaryMethodsArray[i]=summaryMethods;

				}
			}
			$scope.summaryMethods=summaryMethodsArray;
		}
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End If
	else {
		$scope.getAllLatestParamListByTemplate();
		$scope.algorithmData={};
		$scope.algorithmData.locked="N";
		$scope.algorithmData.customFlag="N";
		
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.allparamlist = null;
		$scope.selectparamlist = null;
		$scope.selecttype = null;
		$scope.selectlibrary = null;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		CommonService.getOneByUuidAndVersion(uuid, version, 'algorithm')
			.then(function (response) { onGetByOneUuidandVersion(response.data) }, function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.algorithmData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.algorithm.defaultVersion = defaultversion;
			$scope.selecttype = response.type
			$scope.selectlibrary = response.libraryType
			$scope.getAllLatestParamListByTemplate();
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			var summaryMethodsArray=[];
			if(response.summaryMethods !=null){
				for(var i=0;i<response.summaryMethods.length;i++){
					var summaryMethods={};
					summaryMethods.text=response.summaryMethods[i];
					summaryMethodsArray[i]=summaryMethods;

				}
			}
			$scope.summaryMethods=summaryMethodsArray;
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};

	}

	$scope.submitAlgorithm = function () {
		var upd_tag="N"
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;
		var algorithmJson = {}
		algorithmJson.uuid = $scope.algorithmData.uuid
		algorithmJson.name = $scope.algorithmData.name;
		algorithmJson.displayName = $scope.algorithmData.displayName;
		algorithmJson.desc = $scope.algorithmData.desc
		algorithmJson.active = $scope.algorithmData.active;
		algorithmJson.locked = $scope.algorithmData.locked;
		algorithmJson.customFlag = $scope.algorithmData.customFlag;
		algorithmJson.scriptName = $scope.algorithmData.scriptName;
		algorithmJson.savePmml = $scope.algorithmData.savePmml;
		algorithmJson.published = $scope.algorithmData.published;
		algorithmJson.publicFlag = $scope.algorithmData.publicFlag;
		algorithmJson.type = $scope.selecttype;
		algorithmJson.libraryType = $scope.selectlibrary;
		algorithmJson.trainClass = $scope.algorithmData.trainClass;
		algorithmJson.modelClass = $scope.algorithmData.modelClass;
		algorithmJson.labelRequired = $scope.algorithmData.labelRequired;
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
		algorithmJson.tags=tagArray;
		var summaryMethodsArray = [];
		if ($scope.summaryMethods != null) {
			for (var counttag = 0; counttag < $scope.summaryMethods.length; counttag++) {
				summaryMethodsArray[counttag] = $scope.summaryMethods[counttag].text;
			}
			
		}
		algorithmJson.summaryMethods = summaryMethodsArray;
		// var paramlist = {};
		// var ref = {};
		// ref.type = "paramlist";
		// ref.uuid = $scope.selectparamlist.uuid;
		// paramlist.ref = ref;
		// algorithmJson.paramList = paramlist

		var paramListWHParam={}
		var paramListWHParamRef={};
		paramListWHParamRef.uuid=$scope.selectParamlistWithHyper.uuid;
		paramListWHParamRef.type="paramlist";
		paramListWHParam.ref=paramListWHParamRef;
		algorithmJson.paramListWH=paramListWHParam;
		
		var paramListWOHParam={}
		var paramListWOHParamRef={};
		paramListWOHParamRef.uuid=$scope.selectParamlistWithoutHyper.uuid;
		paramListWOHParamRef.type="paramlist";
		paramListWOHParam.ref=paramListWOHParamRef;
		algorithmJson.paramListWoH=paramListWOHParam;

		console.log(JSON.stringify(algorithmJson));
		AlgorithmService.submit(algorithmJson, 'algorithm',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Algorithm Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okalgorithmsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}

	$scope.okalgorithmsave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go("algorithm"); }, 2000);
		}
	}

});
