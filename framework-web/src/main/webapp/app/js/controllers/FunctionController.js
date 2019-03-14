MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataFunctionController', function ($state, $scope, $stateParams, $rootScope, 
	MetadataFunctionSerivce, privilegeSvc, CommonService, $timeout, $filter, CF_FUNCTION) {
	$scope.mode = "";
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
	$scope.functionTableArray = null;
	$scope.functionHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.functiondata;
	$scope.showForm = true;
	$scope.funcType = ["hive", "impala", "oracle"];
	$scope.allTypes = ["file", "hive", "impala", "mysql", "oracle","postgres"];
	$scope.allParamTypes = ["string", "function"]
    $scope.returnType=CF_FUNCTION.retrunType
  	$scope.catogory = ["MATH", "STRING", "DATE", "CONDITIONAL", "AGGREGATE","WINDOW"];
	$scope.isDependencyShow = false;
	$scope.type = ["string", "float", "bigint", 'double', 'timestamp', 'integer', 'distinct', 'binary', 'number',
		'decimal'
		, 'T', 'col'];
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.function = {};
	$scope.function.versions = [];
	$scope.paramtable = null;
	$scope.privileges = [];

	$scope.privileges = privilegeSvc.privileges['function'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['function'] || [];
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
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}
   
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListfunction', {
			id: uuid,
			version: version,
			mode: mode
		});
	}

	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.functiondata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListfunction', {
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
			$state.go('metaListfunction', {
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


	$scope.okfunctionsave = function () {
		$('#okfunctionsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'function' }); }, 2000);
		}
	}

	$scope.functionFormChange = function () {
		if ($scope.mode == "true") {
			$scope.functionHasChanged = true;
		}
		else {
			$scope.functionHasChanged = false;
		}
	}

	$scope.addrow = function () {
		if ($scope.functionTableArray == null) {
			$scope.functionTableArray = [];
		}
		$scope.expanded = true;
		var functiontable = {};
		var paramInfoHolder = [];
		functiontable.functionType = $scope.funcType[0];
		var paraminfoHolder = {};
		paramInfoHolder.push(paraminfoHolder)
		functiontable.paramInfoHolder = paramInfoHolder
		$scope.functionTableArray.splice($scope.functionTableArray.length, 0, functiontable);

	}
	$scope.removeRow = function (index) {
		var newDataList = [];
		var index = null;
		angular.forEach($scope.functionTableArray, function (selected, key) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			else {
				index = key;
			}
		});
		$scope.functionTableArray = newDataList;
	}

	$scope.selectAllSubRow = function (index) {
		angular.forEach($scope.functionTableArray[index].paramInfoHolder, function (paraminfoHolder) {
			paraminfoHolder.selected = $scope.functionTableArray[index].selectalljoinkey;
		});
	}

	$scope.addJoinSubRow = function (index) {
		var paramInfoHolder = {}
		// 	paramInfoHolder.logicalOperator=$scope.logicalOperator[0];
		// paramInfoHolder.relationOperator=$scope.relationOperator;
		$scope.functionTableArray[index].paramInfoHolder.splice($scope.functionTableArray[index].paramInfoHolder.length, 0, paramInfoHolder);
	}

	$scope.removeJoinSubRow = function (index) {
		$scope.functionTableArray[index].selectalljoinkey = false
		var newDataList = [];
		$scope.functionTableArray[index].checkalljoinKeyrow = false;
		angular.forEach($scope.functionTableArray[index].paramInfoHolder, function (selected) {

			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		//  if(newDataList.length >0){
		//  newDataList[0].logicalOperator="";
		//  }
		$scope.functionTableArray[index].paramInfoHolder = newDataList;
	}
	$scope.selectAllRow = function () {
		angular.forEach($scope.functionTableArray, function (relation) {
			relation.selected = $scope.selectalljoin;
		});
	}

	$scope.expandAll = function (expanded) {
		// $scope is required here, hence the injection above, even though we're using "controller as" syntax
		$scope.$broadcast('onExpandAll', { expanded: expanded });
	};

	$scope.onChangeInputReq=function(){
		if($scope.functiondata.inputReq =="Y"){
			$scope.addrow();
		}else{
			$scope.functionTableArray=[];
		}
		
	}
	

	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	if (typeof $stateParams.id != "undefined") {

		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataFunctionSerivce.getAllVersionByUuid($stateParams.id, "function").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var functionversion = {};
				functionversion.version = response[i].version;
				$scope.function.versions[i] = functionversion;
			}
		}//End onGetAllVersionByUuid

		MetadataFunctionSerivce.getOneByUuidandVersion($stateParams.id, $stateParams.version, "function")
			.then(function (response) { onGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.functiondata = response.functiondata;
			$scope.selectCatogory = response.functiondata.category.toUpperCase();
			$scope.selectFunctionType = response.functiondata.funcType;
			var defaultversion = {};
			defaultversion.version = response.functiondata.version;
			defaultversion.uuid = response.functiondata.uuid;
			$scope.function.defaultVersion = defaultversion;
			$scope.functionTableArray = response.functiondata.functionInfo;
			var tags = [];
			if (response.functiondata.tags != null) {
				for (var i = 0; i < response.functiondata.tags.length; i++) {
					var tag = {};
					tag.text = response.functiondata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			$scope.paramtable = response.paramInfo;
		}//End onGetLatestByUuid
        var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End If
	else{
		$scope.functiondata={};
		$scope.functiondata.locked="N";
		$scope.functiondata.inputReq="N";
		

	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataFunctionSerivce.getOneByUuidandVersion($scope.function.defaultVersion.uuid, $scope.function.defaultVersion.version, 'function')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.functiondata = response.functiondata;
			$scope.selectCatogory = response.functiondata.category.toUpperCase();
			$scope.selectFunctionType = response.functiondata.funcType;
			var defaultversion = {};
			defaultversion.version = response.functiondata.version;
			defaultversion.uuid = response.functiondata.uuid;
			$scope.function.defaultVersion = defaultversion;
			$scope.functionTableArray = response.functiondata.functionInfo;
			var tags = [];
			if (response.functiondata.tags != null) {
				for (var i = 0; i < response.functiondata.tags.length; i++) {
					var tag = {};
					tag.text = response.functiondata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			$scope.paramtable = response.paramInfo;
		}//End getOneByUuidandVersion
        var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}//End selectVersion




	$scope.okfunctionsave = function () {
		$('#functionsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'function' }); }, 2000);
		}
	}

	/*Start SubmitAplication*/
	$scope.submitFunction = function () {
		var upd_tag="N"
		var functionJson = {};
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.functionHasChanged = true;
		$scope.myform.$dirty = false;
		var functionJson = {}
		functionJson.uuid = $scope.functiondata.uuid
		functionJson.name = $scope.functiondata.name
		functionJson.desc = $scope.functiondata.desc
		functionJson.active = $scope.functiondata.active;
		functionJson.locked = $scope.functiondata.locked;
		functionJson.published = $scope.functiondata.published;
		functionJson.functionInfo = $scope.functiondata.functionInfo;
		functionJson.publicFlag = $scope.functiondata.publicFlag;
		functionJson.category = $scope.selectCatogory.toUpperCase();
		functionJson.returnType=$scope.functiondata.returnType;
	// 	functionJson.funcType = $scope.selectFunctionType;
		functionJson.inputReq = $scope.functiondata.inputReq;
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
		functionJson.tags = tagArray;
		var functionInfoArray = [];

		if ($scope.functionTableArray != null) {
			if ($scope.functionTableArray.length > 0) {
				for (j = 0; j < $scope.functionTableArray.length; j++) {
					var functionInfo = {};
					var paramInfoHolder = [];
					functionInfo.name = $scope.functionTableArray[j].name
					functionInfo.type = $scope.functionTableArray[j].type
					for (var i = 0; i < $scope.functionTableArray[j].paramInfoHolder.length; i++) {
						var paramInfoHolderDetail = {};
						paramInfoHolderDetail.paramId = $scope.functionTableArray[j].paramInfoHolder[i].paramId;
						paramInfoHolderDetail.paramName = $scope.functionTableArray[j].paramInfoHolder[i].paramName;
						paramInfoHolderDetail.paramDefVal = $scope.functionTableArray[j].paramInfoHolder[i].paramName;
						paramInfoHolderDetail.paramReq = $scope.functionTableArray[j].paramInfoHolder[i].paramReq;
						paramInfoHolderDetail.paramType = $scope.functionTableArray[j].paramInfoHolder[i].paramType;
						paramInfoHolder[i] = paramInfoHolderDetail;

					}
					functionInfo.paramInfoHolder = paramInfoHolder;
					functionInfoArray[j] = functionInfo

				}

			}
		}
		functionJson.functionInfo = functionInfoArray
		console.log(JSON.stringify(functionJson))
		MetadataFunctionSerivce.submit(functionJson, 'function',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Function Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okfunctionsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitApplication*/

	

});
