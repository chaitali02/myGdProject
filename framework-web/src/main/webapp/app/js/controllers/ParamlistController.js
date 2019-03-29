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
DatascienceModule.controller('CreateParamListController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, ParamListService, privilegeSvc,$timeout,$filter) {

	$scope.mode=false;
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
		$scope.isEdit = true;
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
	$scope.isUseTemlate=false;
	$scope.isTemplageInfoRequired=false;
	$scope.typeSimple = ["string", "double", "integer", "list","decimal"];
	$scope.type = [
		{"name":"string","caption":"STRING"},
		{"name":"double","caption":"DOUBLE"},
	 	{"name":"date","caption":"DATE"}, 
		{"name":"integer","caption":"INTEGER"},
		{"name":"decimal","caption":"DECIMAL"},
		{"name":"attribute","caption":"ATTRIBUTE"},
		{"name":"attributes","caption":"ATTRIBUTE[S]"},
		{"name":"distribution","caption":"DISTRIBUTION"},
		{"name":"datapod","caption":"DATAPOD"},
		{"name":"function","caption":"FUNCTION"},
		{"name":"list","caption":"LIST"},
		{"name":"array","caption":"ARRAY"}];
	$scope.isDependencyShow = false;
	$scope.isTableDisable=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['paramlist'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
    
    $scope.popup2 = {
    	opened: false
    };
	$scope.dateOptions = {
		//dateDisabled: disabled,
		formatYear: 'yy',
	//	maxDate: new Date(2020, 5, 22),
	//	minDate: new Date(),
		startingDay: 1
	};
	function disabled(data) {
		var date = data.date,
		  mode = data.mode;
		return mode === 'day' && (date.getDay() === 0 || date.getDay() === 6);
	}
	
	$scope.open2 = function() {
		$scope.popup2.opened = true;
	};
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['paramlist'] || [];
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
	$scope.ValidationKeyPress=function(e){
		if((e.which <47)|| (e.which > 57)) {
			 e.preventDefault();
		 }
	 }

	$scope.close=function(){
		$scope.parantType=$stateParams.parantType;
		$scope.type=$stateParams.type;
		var state=$scope.type
		if($scope.parantType){
			state=state+$scope.parantType
		}
		$state.go(state); 
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
		$scope.showForm = true;
		$scope.showgraph = false
		$scope.graphDataStatus = false;
		$scope.showGraphDiv = false
	}
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createparamlist'+$stateParams.parantType, {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.paramlistData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createparamlist'+$stateParams.parantType, {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showview = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createparamlist'+$stateParams.parantType, {
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

	$scope.getAllLatestParamListByTemplate=function(){
		ParamListService.getAllLatestParamListByTemplate('Y', "paramlist","").then(function (response) { onSuccessGetAllLatestParamListByTemplate(response.data) });
		var onSuccessGetAllLatestParamListByTemplate = function (response) {
			$scope.allParamList=response;

		}//End getAllVersionByUuid
	}
	$scope.getParamListChilds=function(uuid,version){
		CommonService.getParamListChilds(uuid,version,"paramlist").then(function (response) { onSuccessGetParamListChilds(response.data) });
		var onSuccessGetParamListChilds = function (response) {
			if(response.length >0){
				$scope.isTableDisable=true;
				$scope.isUseTemlateText=true;
			}else{
				$scope.isUseTemlateText=false;
			}

		}//End getAllVersionByUuid
	}
	
	$scope.onChangeIsTemplate=function(){
		$scope.isUseTemlate=!$scope.isUseTemlate;
		if($scope.isUseTemlate){
			$scope.getAllLatestParamListByTemplate();
			$scope.isTemplageInfoRequired=true;
			$scope.isTableDisable=true;
		}else{
			$scope.isTemplageInfoRequired=false;
			$scope.allParamList=null;
			$scope.paramtable=null;
			$scope.isTableDisable=false;
			
		}
	}
    $scope.onChangeTemplateInfo=function(){
		
		var paramArray=[];
		for(var i=0;i<$scope.selectedTemplate.params.length;i++){
		  var paramInfo={}
			paramInfo.paramId=$scope.selectedTemplate.params[i].paramId; 
			paramInfo.paramName=$scope.selectedTemplate.params[i].paramName;
			paramInfo.paramDesc=$scope.selectedTemplate.params[i].paramDesc;
            paramInfo.paramDispName=$scope.selectedTemplate.params[i].paramDispName;
			paramInfo.paramType=$scope.selectedTemplate.params[i].paramType.toLowerCase();
			if($scope.selectedTemplate.params[i].paramValue !=null && $scope.selectedTemplate.params[i].paramValue.ref.type == "simple"){
			  paramInfo.paramValue=$scope.selectedTemplate.params[i].paramValue.value;
			  paramInfo.paramValueType="simple"
		  }else if($scope.selectedTemplate.params[i].paramValue !=null){
			var paramValue={};
			paramValue.uuid=$scope.selectedTemplate.params[i].paramValue.ref.uuid;
			paramValue.type=$scope.selectedTemplate.params[i].paramValue.ref.type;
			paramInfo.paramValue=paramValue;
			paramInfo.paramValueType=$scope.selectedTemplate.params[i].paramValue.ref.type;
		  }
		  paramArray[i]=paramInfo;
		}
		$scope.paramtable = paramArray;
	}
	$scope.addRow = function () {
		if($scope.isTableDisable){
			return false;
		}
		if ($scope.paramtable == null) {
			$scope.paramtable = [];
		}
		var paramjson = {}
		paramjson.paramId = $scope.paramtable.length;
		$scope.paramtable.splice($scope.paramtable.length, 0, paramjson);
	}

	$scope.selectAllRow = function () {
		if($scope.isTableDisable){
			return false;
		}
		angular.forEach($scope.paramtable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}

	$scope.removeRow = function () {
		if($scope.isTableDisable){
			return false;
		}
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
		if($scope.checkIsInrogess () ==false){
			return false;
		}
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
		$scope.getAllVersion($stateParams.id);
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		ParamListService.getOneByUuidandVersion($stateParams.id, $stateParams.version, "paramlist")
			.then(function (response) { onSuccessGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.paramlistData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.paramlist.defaultVersion = defaultversion;
			$scope.paramtable = response.paramInfo;
			if($scope.paramlistData.templateFlg =='N'){
				$scope.isTableDisable=true;
				$scope.getAllLatestParamListByTemplate();
				var selectedTemplate={};
				selectedTemplate.uuid=$scope.paramlistData.templateInfo.ref.uuid;
				$scope.selectedTemplate=selectedTemplate;
				$scope.isUseTemlate=true;
				$scope.isTemplageInfoRequired=true;
			}
			else{
				$scope.isUseTemlate=false;
				$scope.isTemplageInfoRequired=false;
				$scope.getParamListChilds($scope.paramlistData.uuid,$scope.paramlistData.version)
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
	else{
		$scope.paramlistData={};
        $scope.paramlistData.locked="N"		
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		ParamListService.getOneByUuidandVersion(uuid, version, 'paramlist')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.paramlistData = response;
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.paramlist.defaultVersion = defaultversion;
			$scope.paramtable = response.params;
			if($scope.paramlistData.templateFlg =='N'){
				$scope.isTableDisable=true;
				$scope.getAllLatestParamListByTemplate();
				var selectedTemplate={};
				selectedTemplate.uuid=$scope.paramlistData.templateInfo.ref.uuid;
				$scope.selectedTemplate=selectedTemplate;
				$scope.isUseTemlate=true;
				$scope.isTemplageInfoRequired=true;
			}else{
				$scope.isUseTemlate=false;
				$scope.isTemplageInfoRequired=false;
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

	}
    $scope.getAllLatest=function(type,index){
		ParamListService.getAllLatest(type).then(function (response) { onGetAllLatest(response.data) });
		var onGetAllLatest = function (response) {
			$scope.allDistribution=response;
			if($scope.paramtable[index].paramValue ==null)
			$scope.paramtable[index].selectedParamValue=response[0];
			else{
				$scope.paramtable[index].selectedParamValue=$scope.paramtable[index].paramValue
			}

		}
	}
	
	$scope.getFunctionByCriteria=function(type,index){
		CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
		var onSuccressGetFunction = function (response) {
			$scope.allFunction = response;
			if($scope.paramtable[index].paramValue ==null)
			$scope.paramtable[index].selectedParamValue=response[0];
			else{
				$scope.paramtable[index].selectedParamValue=$scope.paramtable[index].paramValue
			}
		}
	}
    $scope.onNgInit=function(type,index){
		if(type =='distribution')
			$scope.getAllLatest(type,index);
		else
		$scope.getFunctionByCriteria(type,index)
		  
	}

	$scope.onChangeParamType=function(type,index){
		if($scope.typeSimple.indexOf(type) !=-1){
			$scope.paramtable[index].paramValueType='simple';
		}
		else if(type =='date'){
			$scope.paramtable[index].paramValueType='date';
		}
		else{
			$scope.paramtable[index].paramValueType=type;
		}
	}

	$scope.submitParamList = function () {
		var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;
		var paramlistJson = {}
		paramlistJson.uuid = $scope.paramlistData.uuid
		paramlistJson.name = $scope.paramlistData.name
		paramlistJson.desc = $scope.paramlistData.desc
		paramlistJson.active = $scope.paramlistData.active;
		paramlistJson.locked = $scope.paramlistData.locked;
		paramlistJson.published = $scope.paramlistData.published;
		paramlistJson.publicFlag = $scope.paramlistData.publicFlag;
		paramlistJson.templateFlg = $scope.paramlistData.templateFlg;
		var templateInfo={};
		if($scope.paramlistData.templateFlg =='N'){
			var templateInfoRef={};
			templateInfoRef.type="paramlist"
			templateInfoRef.uuid=$scope.selectedTemplate.uuid;	
			templateInfo.ref=templateInfoRef;
	    }else{
			templateInfo=null;
		}
		
		paramlistJson.templateInfo = templateInfo;
		if($scope.parantType){
	    	paramlistJson.paramListType = $scope.parantType;
		}
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
		paramlistJson.tags = tagArray;

		var paramInfoArray = [];
		if ($scope.paramtable.length > 0) {
			for (var i = 0; i < $scope.paramtable.length; i++) {
				var paraminfo = {};
				paraminfo.paramId = $scope.paramtable[i].paramId;
				paraminfo.paramName = $scope.paramtable[i].paramName;
				paraminfo.paramType = $scope.paramtable[i].paramType;
				paraminfo.paramDesc = $scope.paramtable[i].paramDesc;
				paraminfo.paramDispName = $scope.paramtable[i].paramDispName;
				var paramValue={}
				if($scope.typeSimple.indexOf($scope.paramtable[i].paramType) !=-1){
				 var paramRef={}	 
				 paramRef.type="simple";
				 paramValue.ref=paramRef;
				 paramValue.value=$scope.paramtable[i].paramValue;
				 paraminfo.paramValue =paramValue
				 paramInfoArray[i] = paraminfo; 
				}
				else if($scope.paramtable[i].paramType =='distribution' || $scope.paramtable[i].paramType =='function'){
					var paramRef={};
					paramRef.type=$scope.paramtable[i].paramType;
					if($scope.paramtable[i].selectedParamValue !=null)
						paramRef.uuid=$scope.paramtable[i].selectedParamValue.uuid;
					paramValue.ref=paramRef;
					paraminfo.paramValue =paramValue
					paramInfoArray[i] = paraminfo; 
				}
				else if($scope.paramtable[i].paramType =='date'){
					var paramRef={}	 
					paramRef.type="simple";
					paramValue.ref=paramRef;
					paramValue.value=$filter('date')($scope.paramtable[i].paramValue, "yyyy-MM-dd");
					paraminfo.paramValue=paramValue
					paramInfoArray[i] = paraminfo; 

				}
				else if($scope.paramtable[i].paramType =='array'){
					var paramArrayTags=[];
					if($scope.paramtable[i].paramArrayTags && $scope.paramtable[i].paramArrayTags.length >0){
						for(var j=0;j<$scope.paramtable[i].paramArrayTags.length;j++){
							paramArrayTags[j]=$scope.paramtable[i].paramArrayTags[j].text
						}
					}
					
					var paramRef={}	 
					paramRef.type="simple";
					paramValue.ref=paramRef;
					paramValue.value=paramArrayTags.toString();;
					paraminfo.paramValue=paramValue
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
		ParamListService.submit(paramlistJson, 'paramlist',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
