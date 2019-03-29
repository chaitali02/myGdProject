AdminModule = angular.module('AdminModule');

AdminModule.controller('MetadataApplicationController', function ($state, $scope, $stateParams, $rootScope, MetadataApplicationSerivce, $sessionStorage, privilegeSvc, CommonService, $timeout, $filter) {
	
	$scope.SourceTypes = ["file", "hive", "impala", 'mysql', 'oracle'];
	$scope.applicationTypes=[{"caption":"SYSADMIN",'text':'SYSADMIN',"disabled":false},{"caption":"APPADMIN",'text':'APPADMIN',"disabled":false},{"caption":"DEFAULT",'text':'DEFAULT',"disabled":false}];
	$scope.dataLoading = false;
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
	$scope.applicationHasChanged = true;
	$scope.isSubmitEnable = true;
	$scope.applicationdata;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false;
	$scope.application = {};
	$scope.application.versions = [];
	$scope.isshowmodel = false;
	$scope.state = "admin";
	$scope.stateparme = { "type": "application" };
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.typeSimple = ["string", "double", "integer", "list"];
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
	$scope.isTableDisable = false;
	$scope.popup2 = {
    	opened: false
    };
	$scope.dateOptions = {
		dateDisabled: disabled,
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

	$scope.privileges = privilegeSvc.privileges['application'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['application'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
    
	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			console.log(response)
			$scope.lobTag = response[0].value
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

	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.applicationdata.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode: 'true'
		});
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


	$scope.applicationFormChange = function () {
		if ($stateParams.mode == true) {
			$scope.applicationHasChanged = true;
		}
		else {
			$scope.applicationHasChanged = false;
		}
	}
	
	$scope.disabledApplicatoinType=function(applicationType,arrayStr){
		if(applicationType && applicationType.length){
			for(var i=0;i<applicationType.length;i++){
				applicationType[i].disabled=false;
				var index = arrayStr.indexOf(applicationType[i].text);
				if(index !=-1){
					applicationType[i].disabled=true;
				}

			}
		}
		return applicationType;
	}

	$scope.getAllLatestOrgnization = function () {
		CommonService.getAllLatest('organization').then(function (response) { onGetAllLatest(response.data) });
		var onGetAllLatest = function (response) {
			$scope.allOrgnization = response;
		}
	};
	
	$scope.getLatestByUuid=function(){
		MetadataApplicationSerivce.getLatestByUuid($rootScope.appUuid,'application').then(function(response){onSuccessGetLatestByUuid(response.data)});
	    var onSuccessGetLatestByUuid=function(response){
			$scope.applicationOrgDetail=response;
			if($scope.applicationOrgDetail.applicationType =="SYSADMIN"){
				$scope.getAllLatestOrgnization();
				if($scope.applicationdata.applicationType =="SYSADMIN"){
					$scope.applicationTypes=$scope.disabledApplicatoinType($scope.applicationTypes,['DEFAULT']);
				}
				else
				$scope.applicationTypes=$scope.disabledApplicatoinType($scope.applicationTypes,['DEFAULT','SYSADMIN']);
                
			}
			else if($scope.applicationdata.applicationType =="APPADMIN"){
				$scope.applicationTypes=$scope.disabledApplicatoinType($scope.applicationTypes,['SYSADMIN']);

			}
			else{
				$scope.selectOrgInfo={};
				$scope.selectOrgInfo.uuid=$scope.applicationOrgDetail.orgInfo.ref.uuid;
				$scope.applicationTypes=$scope.disabledApplicatoinType($scope.applicationTypes,['APPADMIN','SYSADMIN']);
			}
		}
	}

	
	$scope.selectType = function () {
		MetadataApplicationSerivce.getDatasourceByType($scope.selectSourceType.toUpperCase()).then(function (response) { onSuccessGetDatasourceByType(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			console.log(JSON.stringify(response));
			$scope.alldatasource = response;
			$scope.selectDataSource = response[0];

		}
	}
	
	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;
	}//End showGraph

	$scope.addRow = function () {
		if ($scope.isTableDisable) {
			return false;
		}
		if ($scope.paramtable == null) {
			$scope.paramtable = [];
		}
		var paramjson = {}
		paramjson.paramId = $scope.paramtable.length;
		$scope.paramtable.splice($scope.paramtable.length, 0, paramjson);
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

	$scope.selectAllRow = function () {
		if ($scope.isTableDisable) {
			return false;
		}
		angular.forEach($scope.paramtable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}
   
	
	$scope.removeRow = function () {
		if ($scope.isTableDisable) {
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


	$scope.onChangeParamType = function (type, index) {
		if ($scope.applicationCompare != null) {
			$scope.applicationCompare.paramlistChg = "y";
			$scope.applicationCompare.applicationChg = "y";
		}	
		if ($scope.typeSimple.indexOf(type) != -1) {
			$scope.paramtable[index].paramValueType = 'simple';
		}
		else if (type == 'date') {
			$scope.paramtable[index].paramValueType = 'date';
		}
		else {
			$scope.paramtable[index].paramValueType = type;
		}
	}

	$scope.onChangeSimple=function(){
		if ($scope.applicationCompare != null) {
			$scope.applicationCompare.paramlistChg = "y";
			$scope.applicationCompare.applicationChg = "y";
		}	
	}

    $scope.onChangeDistribution=function(){
		if ($scope.applicationCompare != null) {
			$scope.applicationCompare.paramlistChg = "y";
			$scope.applicationCompare.applicationChg = "y";
		}	
	}

	$scope.onChangeFunction=function(){
		
		if ($scope.applicationCompare != null) {
			$scope.applicationCompare.paramlistChg = "y";
			$scope.applicationCompare.applicationChg = "y";
		}
	}

	$scope.getAllLatest = function (type, index) {
		CommonService.getAllLatest(type).then(function (response) { onGetAllLatest(response.data) });
		var onGetAllLatest = function (response) {
			$scope.allDistribution = response;
			if ($scope.paramtable[index].paramValue == null)
				$scope.paramtable[index].selectedParamValue = response[0];
			else {
				$scope.paramtable[index].selectedParamValue = $scope.paramtable[index].paramValue
			}

		}
	}

	$scope.getFunctionByCriteria = function (type, index) {
		CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccressGetFunction(response.data) });
		var onSuccressGetFunction = function (response) {
			$scope.allFunction = response;
			if ($scope.paramtable[index].paramValue == null)
				$scope.paramtable[index].selectedParamValue = response[0];
			else {
				$scope.paramtable[index].selectedParamValue = $scope.paramtable[index].paramValue
			}
		}
	}
	$scope.onNgInit = function (type, index) {
		if (type == 'distribution')
			$scope.getAllLatest(type, index);
		else
			$scope.getFunctionByCriteria(type, index)

	}

	$scope.getAllVersion = function (uuid) {
		MetadataApplicationSerivce.getAllVersionByUuid(uuid, "application").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var applicationversion = {};
				applicationversion.version = response[i].version;
				$scope.application.versions[i] = applicationversion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion = function (uuid, version) {
		$timeout(function () {
			$scope.myform.$dirty = false;
		}, 0)
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataApplicationSerivce.getOneByUuidAndVersion(uuid, version, 'applicationview')
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.applicationdata = response.application;
			var defaultversion = {};
			defaultversion.version = response.application.version;
			defaultversion.uuid = response.application.uuid;
			$scope.application.defaultVersion = defaultversion;
			var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.application.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			MetadataApplicationSerivce.getLatestDataSourceByUuid($scope.applicationdata.dataSource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataApplicationSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.applicationdata.dataSource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
			$scope.paramtable=response.paramInfo;
			$scope.getLatestByUuid();
			$scope.selectOrgInfo={};
			if($scope.applicationdata.orgInfo !=null){
				$scope.selectOrgInfo.uuid=$scope.applicationdata.orgInfo.ref.uuid;
				$scope.selectOrgInfo.name=$scope.applicationdata.orgInfo.ref.name;

			}
			if($scope.applicationdata.paramList.templateFlg =='Y'){
				$scope.isUseTemlate=false;
				$scope.isTemplageInfoRequired=false;
				$scope.getParamListChilds($scope.applicationdata.paramList.uuid,$scope.applicationdata.paramList.version);
			}

		
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End SelectVersion

    
	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.showactive="true";
		var id;
		id = $stateParams.id;
		$scope.getAllVersion(id)//Call SelectAllVersion Function
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataApplicationSerivce.getOneByUuidAndVersion(id,$stateParams.version || "", "applicationview")
			.then(function (response) { onGetByOneUuidandVersion(response.data) },function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.applicationdata = response.application;
			$scope.applicationCompare=$scope.applicationdata; 
			var defaultversion = {};
			defaultversion.version = response.application.version;
			defaultversion.uuid = response.application.uuid;
			$scope.application.defaultVersion = defaultversion;
			var tags = [];
		
			if (response.tags != null) {
				for (var i = 0; i < response.application.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}

			MetadataApplicationSerivce.getLatestDataSourceByUuid($scope.applicationdata.dataSource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataApplicationSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.applicationdata.dataSource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
			$scope.getLatestByUuid();
			
		
			$scope.selectOrgInfo={};
			if($scope.applicationdata.orgInfo !=null){
				$scope.selectOrgInfo.uuid=$scope.applicationdata.orgInfo.ref.uuid;
				$scope.selectOrgInfo.name=$scope.applicationdata.orgInfo.ref.name;

			}
			$scope.paramtable=response.paramInfo;
			if($scope.applicationdata.paramList !=null && $scope.applicationdata.paramList.templateFlg =='Y'){
				$scope.isUseTemlate=false;
				$scope.isTemplageInfoRequired=false;
				$scope.getParamListChilds($scope.applicationdata.paramList.uuid,$scope.applicationdata.paramList.version);
			}

			
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End IF
	else{
		$scope.getLatestByUuid();
		$scope.applicationdata={};
		$scope.applicationdata.locked="N";
	}
	


	/*Start SubmitAplication*/
	$scope.submitApplication = function () {
		var upd_tag = "N"
		var applicationJson = {};
		
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.applicationHasChanged = true;
		
		if ($scope.applicationCompare == null) {
			applicationJson.paramlistChg = "y";
			applicationJson.applicationChg = "y";
		}else if($scope.myform.$dirty ==true){
			applicationJson.paramlistChg = "y";
			applicationJson.applicationChg = "y";
		}
		else{
			applicationJson.paramlistChg =$scope.applicationCompare.paramlistChg;
			applicationJson.applicationChg = $scope.applicationCompare.applicationChg;
		}
		$scope.myform.$dirty = false;
		applicationJson.uuid = $scope.applicationdata.uuid
		applicationJson.name = $scope.applicationdata.name
		applicationJson.desc = $scope.applicationdata.desc
		applicationJson.active = $scope.applicationdata.active;
		applicationJson.locked = $scope.applicationdata.locked;
		applicationJson.published = $scope.applicationdata.published;
		applicationJson.publicFlag = $scope.applicationdata.publicFlag;

		applicationJson.deployPort = $scope.applicationdata.deployPort;
		applicationJson.applicationType= $scope.applicationdata.applicationType;
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
		applicationJson.tags = tagArray;
        
		var orgInfo={};
		var refOrgInfo={};
		refOrgInfo.uuid=$scope.selectOrgInfo.uuid;
		refOrgInfo.type="organization";	
		orgInfo.ref=refOrgInfo;
		applicationJson.orgInfo=orgInfo;

		var datasource = {};
		var ref = {};
		ref.type = "datasource";
		ref.uuid = $scope.selectDataSource.uuid;
		datasource.ref = ref;
		applicationJson.dataSource = datasource;
		applicationJson.paramList={};
        if($scope.applicationCompare != null && $scope.applicationCompare.paramList !=null ){
			applicationJson.paramList.uuid=$scope.applicationCompare.paramList.uuid;
			applicationJson.paramList.paramListType=$scope.applicationCompare.paramList.paramListType;
			applicationJson.paramList.templateFlg=$scope.applicationCompare.paramList.templateFlg;
		}
		else{
			applicationJson.paramList.paramListType="application";
			applicationJson.paramList.templateFlg="Y";
		}
		var paramInfoArray = [];
		if ($scope.paramtable && $scope.paramtable.length > 0) {
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
		
		applicationJson.paramList.params = paramInfoArray;
		console.log(JSON.stringify(applicationJson));
		MetadataApplicationSerivce.submit(applicationJson, 'applicationview', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Application Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okapplicationsave();
		}//End Submit Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}/*End SubmitApplication*/

	$scope.okapplicationsave = function () {
		$('#applicationsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('admin', { 'type': 'application' }); }, 2000);
		}
	}

});
