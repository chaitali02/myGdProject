ProfileModule = angular.module('ProfileModule');

ProfileModule.controller('DetailProfileController', function (CommonService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, ProfileService, privilegeSvc, CF_SUCCESS_MSG, CF_FILTER) {

	$scope.select = 'Rule';

	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.isDragable = "false";
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
		$scope.isDragable = "true";
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
		$scope.mode = "false";
		$scope.isDragable = "true";
	}

	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.rhsNA=['NULL',"NOT NULL"];
	$scope.isDestoryState = false;
	$scope.profile = {};
	$scope.profile.versions = []
	$scope.showForm = true;
	$scope.porfileTypes = ["datapod"];
	$scope.porfiletype = $scope.porfileTypes[0];
	$scope.isDependencyShow = false;
	$scope.continueCount = 1;
	$scope.backCount;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['profile'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.logicalOperator = ["AND", "OR"];
	$scope.spacialOperator = ['<', '>', '<=', '>=', '=', '!=', 'LIKE', 'NOT LIKE', 'RLIKE'];
	$scope.operator = CF_FILTER.operator;
	$scope.lhsType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "formula", "caption": "formula" }];
	$scope.rhsType = [
		{ "text": "string", "caption": "string", "disabled": false },
		{ "text": "string", "caption": "integer", "disabled": false },
		{ "text": "datapod", "caption": "attribute", "disabled": false },
		{ "text": "formula", "caption": "formula", "disabled": false },
		{ "text": "dataset", "caption": "dataset", "disabled": false },
		{ "text": "paramlist", "caption": "paramlist", "disabled": false },
		{ "text": "function", "caption": "function", "disabled": false }];

	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};

	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['profile'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			$scope.lobTag = response[0].value
		}
	}

	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};

	$scope.getLovByType();

	$scope.$on('$destroy', function () {
		$scope.isDestoryState = true;
	});

	$scope.close = function () {
		if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
			//revertback
			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('viewprofile');
		}
	}
    $scope.formChange=function(){
		$scope.myform1.$dirty=true;
		$scope.myform2.$dirty=true;
	}

	$scope.countContinue = function () {
		$scope.continueCount = $scope.continueCount + 1;
		if ($scope.continueCount >= 4) {
			$scope.isSubmitShow = true;
		} else {
			$scope.isSubmitShow = false;
		}
	}

	$scope.countBack = function () {
		$scope.continueCount = $scope.continueCount - 1;
		$scope.isSubmitShow = false;
	}
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showProfilePage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false;
	}

	$scope.showHome = function (uuid, version, mode) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showProfilePage();
		$state.go('createprofile', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.profileDetail.locked == "Y") {
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showProfilePage()
		$state.go('createprofile', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showProfilePage()
			$state.go('createprofile', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}


	$scope.selectOption = function () {
		$scope.filterTableArray=null;
		ProfileService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
		var onSuccessGetAllAttributeBySource = function (response) {
			$scope.allattribute = response
			$scope.profileTags = null;
			$scope.lhsdatapodattributefilter=response;
		}
	}

	$scope.showProfileGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	};

	$scope.clear = function () {
		$scope.profileTags = null;
		$scope.myform2.$dirty=true;
	}

	$scope.addAll = function () {
		$scope.profileTags = $scope.allattribute;
		$scope.myform2.$dirty=true;
	}

	$scope.SearchAttribute = function (index, type, propertyType) {
		$scope.selectAttr = $scope.filterTableArray[index][propertyType]
		$scope.searchAttr = {};
		$scope.searchAttr.type = type;
		$scope.searchAttr.propertyType = propertyType;
		$scope.searchAttr.index = index;
		CommonService.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = {}
			$scope.allSearchType.options = response;
			$scope.allSearchType.defaultoption = response[0];
			if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
				var defaultoption = {};
				defaultoption.uuid = $scope.selectAttr.uuid;
				defaultoption.name = "";
				$scope.allSearchType.defaultoption = defaultoption;
			}
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			});
			ProfileService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.allAttr = response;
				if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
					var defaultoption = {};
					defaultoption.uuid = $scope.selectAttr.uuid;
					defaultoption.name = "";
					$scope.allSearchType.defaultoption = defaultoption;
				} else {
					$scope.selectAttr = $scope.allAttr[0]
				}

			}
		}
	}

	$scope.onChangeSearchAttr = function () {
		ProfileService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SubmitSearchAttr = function () {
		if ($scope.dataqualitycompare != null) {
			$scope.dataqualitycompare.filterChg = "y"
		}
		$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
		$('#searchAttr').modal('hide')
	}
	$scope.disableRhsType = function (rshTypes, arrayStr) {
		for (var i = 0; i < rshTypes.length; i++) {
			rshTypes[i].disabled = false;
			if (arrayStr.length > 0) {
				var index = arrayStr.indexOf(rshTypes[i].caption);
				if (index != -1) {
					rshTypes[i].disabled = true;
				}
			}
		}
		return rshTypes;
	}

	$scope.onChangeOperator = function (index) {
		if ($scope.filterTableArray[index].operator == 'BETWEEN') {
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, []);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 
		else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist','string','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['dataset']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else if (['IS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].isRhsNA=true;
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['dataset']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
	}

	$scope.checkAllFilterRow = function () {
		angular.forEach($scope.filterTableArray, function (filter) {
			filter.selected = $scope.checkAll;
		});
	}


	function returnRshType(){
		var rTypes = [
			{ "text": "string", "caption": "string", "disabled": false },
			{ "text": "string", "caption": "integer", "disabled": false },
			{ "text": "datapod", "caption": "attribute", "disabled": false },
			{ "text": "formula", "caption": "formula", "disabled": false },
			{ "text": "dataset", "caption": "dataset", "disabled": false },
			{ "text": "paramlist", "caption": "paramlist", "disabled": false },
			{ "text": "function", "caption": "function", "disabled": false }]
	    return rTypes;
	}

	$scope.addRowFilter = function () {
		if ($scope.filterTableArray == null) {
			$scope.filterTableArray = [];
		}
		var filertable = {};
		filertable.islhsDatapod = true;
		filertable.islhsFormula = false;
		filertable.islhsSimple = false;
		filertable.isrhsDatapod = true;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = false;
		filertable.lhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		filertable.rhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		
		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[2]
		filertable.rhstype = $scope.rhsType[2];
		filertable.rhsTypes = returnRshType()
		filertable.rhsTypes = $scope.disableRhsType(filertable.rhsTypes, ['dataset']);
		filertable.rhsvalue;
		filertable.lhsvalue;
		$scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);


	}
	$scope.removeFilterRow = function () {
		var newDataList = [];
		$scope.checkAll = false;
		angular.forEach($scope.filterTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			$scope.fitlerAttrTableSelectedItem=[];
			}
		});
		newDataList[0].logicalOperator = "";
		$scope.filterTableArray = newDataList;
	}

	$scope.onAttrFilterRowDown = function (index) {
		var rowTempIndex = $scope.filterTableArray[index];
		var rowTempIndexPlus = $scope.filterTableArray[index + 1];
		$scope.filterTableArray[index] = rowTempIndexPlus;
		$scope.filterTableArray[index + 1] = rowTempIndex;
		if (index == 0) {
			$scope.filterTableArray[index + 1].logicalOperator = $scope.filterTableArray[index].logicalOperator;
			$scope.filterTableArray[index].logicalOperator = ""
		}
	}

	$scope.onAttrFilterRowUp = function (index) {
		var rowTempIndex = $scope.filterTableArray[index];
		var rowTempIndexMines = $scope.filterTableArray[index - 1];
		$scope.filterTableArray[index] = rowTempIndexMines;
		$scope.filterTableArray[index - 1] = rowTempIndex;
		if (index == 1) {
			$scope.filterTableArray[index].logicalOperator = $scope.filterTableArray[index - 1].logicalOperator;
			$scope.filterTableArray[index - 1].logicalOperator = ""
		}
	}

	$scope.onFilterDrop = function (index) {
		if (index.targetIndex == 0) {
			$scope.filterTableArray[index.sourceIndex].logicalOperator = $scope.filterTableArray[index.targetIndex].logicalOperator;
			$scope.filterTableArray[index.targetIndex].logicalOperator = ""
		}
		if (index.sourceIndex == 0) {
			$scope.filterTableArray[index.targetIndex].logicalOperator = $scope.filterTableArray[index.sourceIndex].logicalOperator;
			$scope.filterTableArray[index.sourceIndex].logicalOperator = ""
		}
	}

	$scope.selectlhsType = function (type, index) {
		if (type == "string") {
			$scope.filterTableArray[index].islhsSimple = true;
			$scope.filterTableArray[index].islhsDatapod = false;
			$scope.filterTableArray[index].lhsvalue;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "datapod") {

			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = true;
			$scope.filterTableArray[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.filterTableArray[index].islhsFormula = true;
			$scope.filterTableArray[index].islhsSimple = false;
			$scope.filterTableArray[index].islhsDatapod = false;
			ProfileService.getFormulaByType($scope.allDatapod.defaultoption.uuid, $scope.porfiletype).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.ruleLodeFormula = response;
			}
		}


	}
	$scope.selectrhsType = function (type, index) {

		if (type == "string") {
			$scope.filterTableArray[index].isrhsSimple = true;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].rhsvalue;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;
		}
		else if (type == "datapod") {
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = true;
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;
		}
		else if (type == "formula") {
			$scope.filterTableArray[index].isrhsFormula = true;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;
			ProfileService.getFormulaByType($scope.allDatapod.defaultoption.uuid, $scope.porfiletype).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.ruleLodeFormula = response;
			}
		}
		else if (type == "function") {

			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = true;
			CommonService.getFunctionByCriteria("", "N", "function").then(function (response) {
				onSuccressGetFunction(response.data)
			});
			var onSuccressGetFunction = function (response) {
				console.log(response)
				$scope.allFunction = response;
			}
		}
		else if (type == "dataset") {
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = true;
			$scope.filterTableArray[index].isrhsParamlist = false;
			$scope.filterTableArray[index].isrhsFunction = false;

		}
		else if (type == "paramlist") {
			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist = true;
			$scope.filterTableArray[index].isrhsFunction = false;
			$scope.getParamByApp();

		}
	}

	$scope.getParamByApp = function () {
		CommonService.getParamByApp($rootScope.appUuidd || "", "application").
			then(function (response) { onSuccessGetParamByApp(response.data) });
		var onSuccessGetParamByApp = function (response) {
			$scope.allparamlistParams = [];
			if (response.length > 0) {
				var paramsArray = [];
				for (var i = 0; i < response.length; i++) {
					var paramjson = {}
					var paramsjson = {};
					paramsjson.uuid = response[i].ref.uuid;
					paramsjson.name = response[i].ref.name + "." + response[i].paramName;
					paramsjson.attributeId = response[i].paramId;
					paramsjson.attrType = response[i].paramType;
					paramsjson.paramName = response[i].paramName;
					paramsjson.caption = "app." + paramsjson.paramName;
					paramsArray[i] = paramsjson
				}
				$scope.allparamlistParams = paramsArray;
			}
		}
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.allDatapod = null;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		ProfileService.getAllVersionByUuid($stateParams.id, "profile").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var profileVersion = {};
				profileVersion.version = response[i].version;
				$scope.profile.versions[i] = profileVersion;
			}
		}
		ProfileService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'profile')
			.then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
		var onsuccess = function (response) {
			$scope.isEditInprogess = false;
			$scope.filterTableArray=response.filterInfo
			$scope.profileDetail = response.profiledata;
			var defaultVersion = {};
			defaultVersion.version = response.profiledata.version;
			defaultVersion.uuid = response.profiledata.uuid;
			$scope.profile.defaultVersion = defaultVersion;
			$scope.tags = response.tags
			$scope.getParamByApp();
			ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allDatapod = response;
				var defaultoption = {};
				defaultoption.uuid = $scope.profileDetail.dependsOn.ref.uuid;
				defaultoption.name = $scope.profileDetail.dependsOn.ref.name;
				$scope.allDatapod.defaultoption = defaultoption;

			}
			var profileAttributeArray = [];
			ProfileService.getAllAttributeBySource(response.profiledata.dependsOn.ref.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response;
				$scope.lhsdatapodattributefilter = response;

			}
       
			for (var i = 0; i < response.profiledata.attributeInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.profiledata.attributeInfo[i].ref.uuid;
				ruleInfo.dname = response.profiledata.attributeInfo[i].ref.name + "." + response.profiledata.attributeInfo[i].attrName;
				ruleInfo.version = response.profiledata.attributeInfo[i].ref.version;
				ruleInfo.attributeId = response.profiledata.attributeInfo[i].attrId;
				ruleInfo.id = response.profiledata.attributeInfo[i].ref.uuid + "_" + response.profiledata.attributeInfo[i].attrId
				profileAttributeArray[i] = ruleInfo;
			}
			$scope.profileTags = profileAttributeArray
			CommonService.getFunctionByCriteria("", "N", "function")
				.then(function (response) {onSuccressGetFunction(response.data)});
			  var onSuccressGetFunction = function (response) {
				$scope.allFunction = response;
			}
			ProfileService.getFormulaByType(response.profiledata.dependsOn.ref.uuid, $scope.porfiletype).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.ruleLodeFormula = response;
			}

		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		}
	}
	else {
		$scope.profileDetail = {};
		$scope.profileDetail.locked = "N";
		ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.allDatapod = response;
			ProfileService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response;
				$scope.lhsdatapodattributefilter = response;

			}
		}
	}

	$scope.selectVersion = function () {
		$scope.myform1.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform3.$dirty = false;
		$scope.allDatapod = null;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		ProfileService.getOneByUuidAndVersion($scope.profile.defaultVersion.uuid, $scope.profile.defaultVersion.version, 'profile')
			.then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
		var onsuccess = function (response) {
			$scope.isEditInprogess = false;
			$scope.filterTableArray=response.filterInfo
			$scope.profileDetail = response.profiledata;
			var defaultVersion = {};
			defaultVersion.version = response.profiledata.version;
			defaultVersion.uuid = response.profiledata.uuid;
			$scope.profile.defaultVersion = defaultVersion;
			$scope.tags = response.profiledata.tags
			$scope.getParamByApp();

			ProfileService.getAllDatapod($scope.porfiletype).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				$scope.allDatapod = response;
				var defaultoption = {};
				defaultoption.uuid = $scope.profileDetail.dependsOn.ref.uuid;
				defaultoption.name = $scope.profileDetail.dependsOn.ref.name;
				$scope.allDatapod.defaultoption = defaultoption;
			}
			var profileAttributeArray = [];
			ProfileService.getAllAttributeBySource(response.profiledata.dependsOn.ref.uuid, "datapod").then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
			var onSuccessGetAllAttributeBySource = function (response) {
				$scope.allattribute = response;
				$scope.lhsdatapodattributefilter = response;

			}

			for (var i = 0; i < response.profiledata.attributeInfo.length; i++) {
				var ruleInfo = {};
				ruleInfo.uuid = response.profiledata.attributeInfo[i].ref.uuid;
				ruleInfo.dname = response.profiledata.attributeInfo[i].ref.name + "." + response.profiledata.attributeInfo[i].attrName;
				ruleInfo.version = response.profiledata.attributeInfo[i].ref.version;
				ruleInfo.attributeId = response.profiledata.attributeInfo[i].attrId;
				ruleInfo.id = response.profiledata.attributeInfo[i].ref.uuid + "_" + response.profiledata.attributeInfo[i].attrId
				profileAttributeArray[i] = ruleInfo;
			}
			$scope.profileTags = profileAttributeArray;
			CommonService.getFunctionByCriteria("", "N", "function")
				.then(function (response) {onSuccressGetFunction(response.data)});
			  var onSuccressGetFunction = function (response) {
				$scope.allFunction = response;
			}
			ProfileService.getFormulaByType(response.profiledata.dependsOn.ref.uuid, $scope.porfiletype).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.ruleLodeFormula = response;
			}

		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		}
	}


	$scope.okProfileSave = function () {
		var hidemode = "yes";
		if (hidemode == 'yes' && $scope.isDestoryState == false) {
			setTimeout(function () { $state.go('viewprofile'); }, 2000);
		}
	}

	$scope.submit = function () {
		var upd_tag = "N"
		var profileJson = {}
		$scope.dataLoading = true;
		$scope.myform1.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform3.$dirty = false;
		var options = {}
		options.execution = $scope.checkboxModelexecution;
		profileJson.uuid = $scope.profileDetail.uuid;
		profileJson.name = $scope.profileDetail.name;
		profileJson.desc = $scope.profileDetail.desc;
		profileJson.active = $scope.profileDetail.active;
		profileJson.locked = $scope.profileDetail.locked;
		profileJson.published = $scope.profileDetail.published;
		profileJson.publicFlag = $scope.profileDetail.publicFlag;

		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
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
			ruleInfo.ref = ref;
			ruleInfo.attrId = $scope.profileTags[i].attributeId
			ruleInfoArray[i] = ruleInfo;
		}
		profileJson.attributeInfo = ruleInfoArray;
		var filterInfoArray = [];

		if ($scope.filterTableArray != null) {
			if ($scope.filterTableArray.length > 0) {
				for (var i = 0; i < $scope.filterTableArray.length; i++) {

					var filterInfo = {};
					var operand = []
					var lhsoperand = {}
					var lhsref = {}
					var rhsoperand = {}
					var rhsref = {};
					filterInfo.display_seq = i;
					if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
						filterInfo.logicalOperator = ""
					}
					else {
						filterInfo.logicalOperator = $scope.filterTableArray[i].logicalOperator
					}
					filterInfo.operator = $scope.filterTableArray[i].operator;
					if ($scope.filterTableArray[i].lhstype.text == "string") {
						lhsref.type = "simple";
						lhsoperand.ref = lhsref;
						lhsoperand.attributeType = $scope.filterTableArray[i].lhstype.caption;
						lhsoperand.value = $scope.filterTableArray[i].lhsvalue;
					}
					else if ($scope.filterTableArray[i].lhstype.text == "datapod") {
						if ($scope.rulsourcetype == "dataset") {
							lhsref.type = "dataset";
						}
						else {
							lhsref.type = "datapod";
						}
						lhsref.uuid = $scope.filterTableArray[i].lhsdatapodAttribute.uuid;
						lhsoperand.ref = lhsref;
						lhsoperand.attributeId = $scope.filterTableArray[i].lhsdatapodAttribute.attributeId;
					}
					else if ($scope.filterTableArray[i].lhstype.text == "formula") {
						lhsref.type = "formula";
						lhsref.uuid = $scope.filterTableArray[i].lhsformula.uuid;
						lhsoperand.ref = lhsref;
					}
					operand[0] = lhsoperand;
					if ($scope.filterTableArray[i].rhstype.text == "string") {
						rhsref.type = "simple";
						rhsoperand.ref = rhsref;
						rhsoperand.attributeType = $scope.filterTableArray[i].rhstype.caption;
						rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
						if ($scope.filterTableArray[i].operator == 'BETWEEN') {
							rhsoperand.value = $scope.filterTableArray[i].rhsvalue1 + "and" + $scope.filterTableArray[i].rhsvalue2;
						}
					}
					else if ($scope.filterTableArray[i].rhstype.text == "datapod") {
						if ($scope.rulsourcetype == "dataset") {
							rhsref.type = "dataset";
						}
						else {
							rhsref.type = "datapod";
						}
						rhsref.uuid = $scope.filterTableArray[i].rhsdatapodAttribute.uuid;
						rhsoperand.ref = rhsref;
						rhsoperand.attributeId = $scope.filterTableArray[i].rhsdatapodAttribute.attributeId;
					}
					else if ($scope.filterTableArray[i].rhstype.text == "formula") {
						rhsref.type = "formula";
						rhsref.uuid = $scope.filterTableArray[i].rhsformula.uuid;
						rhsoperand.ref = rhsref;
					}
					else if ($scope.filterTableArray[i].rhstype.text == "function") {
						rhsref.type = "function";
						rhsref.uuid = $scope.filterTableArray[i].rhsfunction.uuid;
						rhsoperand.ref = rhsref;
					}
					else if ($scope.filterTableArray[i].rhstype.text == "dataset") {
						rhsref.type = "dataset";
						rhsref.uuid = $scope.filterTableArray[i].rhsdataset.uuid;
						rhsoperand.ref = rhsref;
						rhsoperand.attributeId = $scope.filterTableArray[i].rhsdataset.attributeId;
					}
					else if ($scope.filterTableArray[i].rhstype.text == "paramlist") {

						rhsref.type = "paramlist";
						rhsref.uuid = $scope.filterTableArray[i].rhsparamlist.uuid;
						rhsoperand.ref = rhsref;
						rhsoperand.attributeId = $scope.filterTableArray[i].rhsparamlist.attributeId;
					}

					operand[1] = rhsoperand;
					filterInfo.operand = operand;
					filterInfoArray[i] = filterInfo;
				}

				profileJson.filterInfo = filterInfoArray;
			} else {
				profileJson.filterInfo = null;

			}
		} else {
			profileJson.filterInfo = null;

		}
		console.log(JSON.stringify(profileJson))
		ProfileService.submit(profileJson, "profile", upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			if (options.execution == "YES") {
				ProfileService.getOneById(response.data, 'profile').then(function (response) { onSuccessGetOneById(response.data) });
				var onSuccessGetOneById = function (response) {
					ProfileService.executeProfile(response.data.uuid, response.data.version).then(function (response) { onSuccess(response.data)},function (response) { onError(response.data) });
					var onSuccess = function (response) {
						$scope.dataLoading = false;
						$scope.saveMessage = CF_SUCCESS_MSG.profileSaveExecute
						notify.type = 'success',
						notify.title = 'Success',
						notify.content = $scope.saveMessage
						$scope.$emit('notify', notify);
						$scope.okProfileSave();
					}
					var onError =function(response){
						debugger
						$scope.dataLoading = false;
						$scope.okProfileSave();
					}
				}//End onSuccessGetOneById
			}//End If
			else {
				$scope.dataLoading = false;
				$scope.saveMessage = CF_SUCCESS_MSG.profileSave;
				notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.saveMessage
				$scope.$emit('notify', notify);
				$scope.okProfileSave();
			}//End Else
		}//End Submit Api Function
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}//End Submit Function

	$scope.fitlerAttrTableSelectedItem=[];
	$scope.onChangeFilterAttRow=function(index,status){
		if(status ==true){
			$scope.fitlerAttrTableSelectedItem.push(index);
		}
		else{
			let tempIndex=$scope.fitlerAttrTableSelectedItem.indexOf(index);

			if(tempIndex !=-1){
				$scope.fitlerAttrTableSelectedItem.splice(tempIndex, 1);

			}
		}	
	}
	$scope.autoMove=function(index,type){
		if(type=="mapAttr"){
		}
		else{
			var tempAtrr=$scope.filterTableArray[$scope.fitlerAttrTableSelectedItem[0]];
			$scope.filterTableArray.splice($scope.fitlerAttrTableSelectedItem[0],1);
			$scope.filterTableArray.splice(index,0,tempAtrr);
			$scope.fitlerAttrTableSelectedItem=[];
			$scope.filterTableArray[index].selected=false;
			$scope.filterTableArray[0].logicalOperator="";
			if($scope.filterTableArray[index].logicalOperator =="" && index !=0){
				$scope.filterTableArray[index].logicalOperator=$scope.logicalOperator[0];
			}else if($scope.filterTableArray[index].logicalOperator =="" && index ==0){
				$scope.filterTableArray[index+1].logicalOperator=$scope.logicalOperator[0];
			}
		}
	}

	$scope.autoMoveTo=function(index,type){
		if(type =="mapAttr"){
		}
		else{
			if(index <= $scope.filterTableArray.length){
				$scope.autoMove(index-1,'filterAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
	}

});

ProfileModule.controller('DetailProfileGroupController', function (privilegeSvc, CommonService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, ProfileService, CF_SUCCESS_MSG) {
	$scope.select = 'Rule Group';
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
	}
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.mode = " ";
	$scope.isDestoryState = false;
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

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			$scope.lobTag = response[0].value
		}
	}

	$scope.loadTag = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.lobTag, query);
		});
	};
	$scope.getLovByType();
	$scope.$on('$destroy', function () {
		$scope.isDestoryState = true;
	});
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showProfileGroupePage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showProfileGroup = true;
		$scope.showgraphdiv = false;
		$scope.graphDataStatus = false;
		$scope.showProfileGroupForm = true;

	}

	$scope.showHome = function (uuid, version, mode) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showProfileGroupePage();
		$state.go('createprofilegroup', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.profileGroupDetail.locked == "Y") {
			return false;
		}
		if($scope.checkIsInrogess () ==false){
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
        if($scope.checkIsInrogess () ==false){
			return false;
		}
  		$scope.showProfileGroupePage()
		$state.go('createprofilegroup', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}

	$scope.showProfileGroupGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showProfileGroup = false;
		$scope.showgraphdiv = true;
		$scope.graphDataStatus = true;
		$scope.showProfileGroupForm = false;
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




	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		ProfileService.getAllVersionByUuid($stateParams.id, "profilegroup").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var profilegroupversion = {};
				profilegroupversion.version = response[i].version;
				$scope.profilegroup.versions[i] = profilegroupversion;
			}
		}
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'profilegroup')
			.then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
		var onsuccess = function (response) {
			$scope.isEditInprogess = false;
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
		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		}
	} else {
		$scope.profileGroupDetail = {}
		$scope.profileGroupDetail.locked = "N";
	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		CommonService	.getOneByUuidAndVersion($scope.profilegroup.defaultVersion.uuid, $scope.profilegroup.defaultVersion.version, 'profilegroup')
			.then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
		var onsuccess = function (response) {
			$scope.isEditInprogess = false;
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
		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		}
	}

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.profileall, query);
		});
	};

	$scope.clear = function () {
		$scope.profileTags = null;
		$scope.myform.$dirty=true;
	}

	$scope.addAll = function () {
		$scope.profileTags =$scope.profileall;
		$scope.myform.$dirty=true;
	}

	$scope.okProfileGroupSave = function () {
		$('#profilegroupsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes' && $scope.isDestoryState == false) {
			setTimeout(function () { $state.go('viewprofilegroup'); }, 2000);

		}


	}
	$scope.sbumitProfileGroup = function () {
		var upd_tag = "N"
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
		profileGroupJson.publicFlag = $scope.profileGroupDetail.publicFlag;

		var tagArray = [];
		if ($scope.tags != null) {
			for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
				tagArray[counttag] = $scope.tags[counttag].text;
			}
			var result = (tagArray.length === _.intersection(tagArray, $scope.lobTag).length);
			if (result == false) {
				upd_tag = "Y"
			}

		}
		profileGroupJson.tags = tagArray;
		var ruleInfoArray = [];
		for (var i = 0; i < $scope.profileTags.length; i++) {
			var ruleInfo = {}
			var ref = {};
			ref.type = "profile";
			ref.uuid = $scope.profileTags[i].uuid;
			ruleInfo.ref = ref;
			ruleInfoArray[i] = ruleInfo;
		}
		profileGroupJson.ruleInfo = ruleInfoArray;
		profileGroupJson.inParallel = $scope.checkboxModelparallel
		console.log(JSON.stringify(profileGroupJson))
		ProfileService.submit(profileGroupJson, "profilegroup", upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			if (options.execution == "YES") {
				ProfileService.getOneById(response.data, 'profilegroup').then(function (response) { onSuccessGetOneById(response.data) });
				var onSuccessGetOneById = function (response) {
					ProfileService.executeProfileGroup(response.data.uuid, response.data.version).then(function (response) { onSuccess(response.data) });
					var onSuccess = function (response) {
						$scope.dataLoading = false;
						$scope.saveMessage = CF_SUCCESS_MSG.profileGroupSaveExecute//"Profile Rule Groups Saved and Submitted Successfully"
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
				$scope.saveMessage = CF_SUCCESS_MSG.profileGroupSave//"Profile Rule Groups Saved Successfully"
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

ProfileModule.controller('ResultProfileController', function ($http, dagMetaDataService, $timeout, $filter, $state, $stateParams, $location, $rootScope, $scope, ProfileService, CommonService, privilegeSvc, CF_DOWNLOAD) {
	$scope.select = $stateParams.type;
	$scope.type = { text: $scope.select == 'profilegroupexec' ? 'profilegroup' : 'profile' };
	$scope.showprogress = false;
	$scope.isRuleExec = false;
	$scope.isRuleResult = false;
	$scope.showZoom = false;
	$scope.isD3RuleEexecGraphShow = false;
	$scope.isD3RGEexecGraphShow = false;
	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
	$scope.download = {};
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
	$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
	$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
	$scope.$on('privilegesUpdated', function (e, data) {
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

	});
	$scope.metaType = dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType;
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
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
		$scope.isD3RuleEexecGraphShow = false;
		$scope.execDetail = $scope.profileGroupLastParams
		$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
		$scope.$emit('resultExecChanged', false);//Update Breadcrum
	}


	$scope.getProfileExec = function (data) {
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs["profile"].execType;
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
			$scope.execDetail = params;
			$scope.execDetail.uuid = params.id;
			$scope.metaType = dagMetaDataService.elementDefs["profile"].execType;

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
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
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

	$scope.downloadFilePofile = function (data) {
		if ($scope.isD3RuleEexecGraphShow) {
			return false;
		}
		$scope.download.data = data;
		$scope.isDownloadDirective=true;
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="profile";
	
	};
	$scope.onDownloaed=function(data){
		console.log(data);
		$scope.isDownloadInprogess=data.isDownloadInprogess;
		$scope.isDownloadDirective=data.isDownloadDirective;
	}

});
