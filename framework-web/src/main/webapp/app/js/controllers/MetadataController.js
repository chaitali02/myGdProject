/**
**/

MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataDatapodController', function ($location,$window,$timeout,$http,$filter,$rootScope,$state,$sessionStorage, 
	 $scope, $stateParams, uiGridConstants,dagMetaDataService,MetadataDatapodSerivce,privilegeSvc,
	 CommonService,CF_DOWNLOAD,CF_SAMPLE) {

	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.mode="true";
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
		$scope.mode="false";
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
		$scope.mode="false";
		$scope.isDragable = "true";

	}

	$scope.unitTypes=[{"text":"*","caption":"* Text"},{"text":"#","caption":"# Number"},{"text":"$","caption":"$ Currrency"},{"text":"%","caption":"% Percent"}];
	$scope.path = dagMetaDataService.compareMetaDataStatusDefs;
	$scope.download={};
	$scope.sample={};
	$scope.sample.maxrow=CF_SAMPLE.framework_sample_maxrows;
	$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;
	$scope.sample.limit_to=CF_SAMPLE.limit_to;
	$scope.isAttributeEnable = false;
	
	$scope.dataLoading = false;
	$scope.datapoddata = null;
	$scope.attributetable = null;
	$scope.showFrom = true;
	$scope.isSubmitEnable = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.datapod = {};
	$scope.type = ["string", "float", "bigint", 'double', 'timestamp', 'integer', 'decimal','varchar',"long","vector","date"];
	$scope.SourceTypes = ["file", "hive", "impala", 'mysql', 'oracle', 'postgres']
	$scope.datapod.versions = [];
	$scope.datasetHasChanged = true;
	$scope.isShowSimpleData = false
	$scope.datapodsampledata = null;
	$scope.isDependencyShow = false;
	$scope.StateName = "metadata({'type':'datapod'})"
	$scope.isUploadingDatapod=false;
	$scope.isFileNameValid=true;
    $scope.isFileSubmitDisable=true
	$scope.isDependencyShow = false;
	$scope.isSimpleRecord = false;
	$scope.searchtext = '';
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['datapod'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.isResizeCompareMetadata=true;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['datapod'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;


	$scope.pagination = {
		currentPage: 1,
		pageSize: 10,
		paginationPageSizes: [5,10, 25, 50, 75, 100],
		maxSize: 5,
	}
	$scope.gridOptions = dagMetaDataService.gridOptionsDefault;
    $scope.gridOptions = {
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}
	
	$scope.gridOptions = {
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}



	$scope.gridOptions.columnDefs = [];
	$scope.filteredRows = [];
	$scope.gridOptions.onRegisterApi = function (gridApi) {
		$scope.gridApi = gridApi;
		$scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};

	$scope.getGridStyle = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRows && $scope.filteredRows.length > 0) {
			style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 50 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}

    
	$window.addEventListener('resize', function(e) {
		$scope.isResizeCompareMetadata=false
		$timeout(function() {
		   $scope.isResizeCompareMetadata=true;
		  },10);
	});
	

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams
	});


	if ($stateParams.mode == 'true') {
		if ($sessionStorage.fromStateName == "metadata") {
			$scope.StateName = "metadata({'type':'datapod'})"
		}
		else {
			$scope.StateName = $sessionStorage.fromStateName;
		}
	}


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
	 

    $scope.getAllLatestDomain = function () {
		CommonService.getAllLatest("domain").then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
		$scope.allDomain = response;
		}
    }

	$scope.getAllLatestDomain(); 
	
	$scope.selectType = function () {
		MetadataDatapodSerivce.getDatasourceByType($scope.selectSourceType.toUpperCase()).then(function (response) { onSuccessGetDatasourceByType(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			$scope.alldatasource = response
		}
	}
	
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
    }
	
	/*Start showPage*/
	$scope.showPage = function () {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = true;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;

	}/*End showPage*/

	$scope.showGraph = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.isShowDatastore=false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = true;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;

	}/*End ShowGraph*/
	
	$scope.showHome=function(uuid, version,mode){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListdatapod', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.datapoddata.locked =="Y"){
          return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListdatapod', {
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
			$scope.showPage()
			$state.go('metaListdatapod', {
				id: uuid,
				version: version,
				mode: 'true'
			});
		}
	}

	$scope.datapodFormChange = function () {
		if ($scope.mode == "true") {
			$scope.datapodHasChanged = true;
		}
		else {
			$scope.datapodHasChanged = false;
		}
	}

	$scope.onChangeName = function (data) {
		$scope.datapodName = data;
	}


	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		//var resultvalue=value.split(/[\_]?/);
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}

	if (typeof $stateParams.id != "undefined") {
		$scope.isDependencyShow = true;
		$scope.isSimpleRecord = true;
		$scope.mode = $stateParams.mode;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatapodSerivce.getAllVersionByUuid($stateParams.id, 'datapod').then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var datapodversion = {};
				datapodversion.version = response[i].version;
				$scope.datapod.versions[i] = datapodversion;
			}
		}
		MetadataDatapodSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'datapod')
			.then(function (response) { onSuccessGetLatestByUuid(response.data)},function (response) { onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			var defaultversion = {};
			$scope.datapoddata = response.datapodata
			var tags = [];
			if (response.datapodata.tags != null) {
				for (var i = 0; i < response.datapodata.tags.length; i++) {
					var tag = {};
					tag.text = response.datapodata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
			$scope.attributetable = response.attributes
			defaultversion.version = response.datapodata.version;
			defaultversion.uuid = response.datapodata.uuid;
			$scope.datapod.defaultVersion = defaultversion;
			$scope.datapodName = $scope.convertUppdercase($scope.datapoddata.name)
			MetadataDatapodSerivce.getLatestDataSourceByUuid($scope.datapoddata.datasource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataDatapodSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.datapoddata.datasource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	} /*End If*/
	else{
		$scope.datapoddata={};
		$scope.datapoddata.locked="N";
	}


	/* Start selectVersion*/
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		MetadataDatapodSerivce.getOneByUuidAndVersion($scope.datapod.defaultVersion.uuid, $scope.datapod.defaultVersion.version, 'datapod')
			.then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var defaultversion = {};
			$scope.isEditInprogess=false;
			$scope.datapoddata = response.datapodata
			$scope.attributetable = response.attributes
			defaultversion.version = response.datapodata.version;
			defaultversion.uuid = response.datapodata.uuid;
			$scope.datapod.defaultVersion = defaultversion;
			$scope.datapodName = $scope.convertUppdercase($scope.datapoddata.name)
			MetadataDatapodSerivce.getLatestDataSourceByUuid($scope.datapoddata.datasource.ref.uuid, "datasource").then(function (response) { onSuccessGetLatestDataSourceByUuid(response.data) });
			var onSuccessGetLatestDataSourceByUuid = function (response) {
				$scope.selectSourceType = response.type.toLowerCase();
				MetadataDatapodSerivce.getDatasourceByType(response.type).then(function (response) { onSuccessGetDatasourceByType(response.data) })
				var onSuccessGetDatasourceByType = function (response) {
					$scope.alldatasource = response
					var selectDataSource = {};
					selectDataSource.uuid = $scope.datapoddata.datasource.ref.uuid;
					selectDataSource.name = "";
					$scope.selectDataSource = selectDataSource
				}
			}
			var tags = [];
			if (response.datapodata.tags != null) {
				for (var i = 0; i < response.datapodata.tags.length; i++) {
					var tag = {};
					tag.text = response.datapodata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		};
	}/* End selectVersion*/


/************************************************************************Start SubmitDatapod****************************************/
	$scope.okdatapodsave = function () {
		$('#datapodsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'datapod' }); }, 2000);
		}
	}
	$scope.getPrefix=function(datapodJson,upd_tag){
		debugger
		MetadataDatapodSerivce.getPrefix('datapod',datapodJson.name).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			datapodJson.prefix=response;
			$scope.callSubmit(datapodJson,upd_tag);
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}	
	}

	$scope.callSubmit=function(datapodJson,upd_tag){
		MetadataDatapodSerivce.submit(datapodJson, 'datapod', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Datapod Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okdatapodsave();
		}
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}	
	}
	$scope.submitDatapod = function () {
		if($scope.isDuplication ==true){
			return false;
		}
		var datapodJson = {};
		var upd_tag = "N"
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.datapodHasChanged = true;
		$scope.isshowmodel = true;
		$scope.myform.$dirty = false;
		datapodJson.uuid = $scope.datapoddata.uuid;
		datapodJson.name = $scope.datapoddata.name;
		datapodJson.desc = $scope.datapoddata.desc;
		datapodJson.prefix = $scope.datapoddata.prefix
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

		datapodJson.tags = tagArray
		datapodJson.active = $scope.datapoddata.active;
		datapodJson.locked = $scope.datapoddata.locked;
		datapodJson.published = $scope.datapoddata.published;
		datapodJson.cache = $scope.datapoddata.cache;
		datapodJson.publicFlag = $scope.datapoddata.publicFlag;

		var datasource = {};
		var ref = {};
		ref.type = "datasource";
		ref.uuid = $scope.selectDataSource.uuid;
		datasource.ref = ref;
		datapodJson.datasource = datasource;
		var attributesarray = [];
		var count = 0;
		for (var datapodattr = 0; datapodattr < $scope.attributetable.length; datapodattr++) {
			var attributes = {};   
			attributes.attributeId = $scope.attributetable[datapodattr].attributeId
			attributes.displaySeq=datapodattr;
			attributes.name = $scope.attributetable[datapodattr].name;
			attributes.type = $scope.attributetable[datapodattr].type;
			attributes.desc = $scope.attributetable[datapodattr].desc;
			attributes.dispName = $scope.attributetable[datapodattr].dispName;
			attributes.active = $scope.attributetable[datapodattr].active;
			attributes.length = $scope.attributetable[datapodattr].length;
			attributes.piiFlag = $scope.attributetable[datapodattr].piiFlag;
			attributes.cdeFlag = $scope.attributetable[datapodattr].cdeFlag;
			attributes.nullFlag = $scope.attributetable[datapodattr].nullFlag;
			attributes.attrUnitType = $scope.attributetable[datapodattr].attrUnitType;
			if ($scope.attributetable[datapodattr].key == "Y") {
				attributes.key = count;
				count = count + 1;
			}
			else {
				attributes.key = ""
			}
			if ($scope.attributetable[datapodattr].partition == "Y") {
				attributes.partition = $scope.attributetable[datapodattr].partition;
			}
			else {
				attributes.partition = "N"
			}
			if($scope.attributetable[datapodattr].selectDomain){
				var selectDomain={};
				var refDomain={};
				refDomain.uuid=$scope.attributetable[datapodattr].selectDomain.uuid;
				refDomain.type="domain";
				selectDomain.ref=refDomain;
				attributes.domain=selectDomain;

			}else{
				attributes.domain=null;
			}
			attributesarray[datapodattr] = attributes
		}
		datapodJson.attributes = attributesarray;
		console.log(JSON.stringify(datapodJson));
		if($scope.isAdd){
			$scope.getPrefix(datapodJson,upd_tag);
		}else{
			$scope.callSubmit(datapodJson,upd_tag);
		}
	}
/******************************************************************End SubmitDatapod****************************************/
	
	
/******************************************************************Start Attribute Table****************************************/

	function isDublication (arr, field, index, name,darray) {
		var res = [];
		for(var i = 0; i < arr.length;i++){
			if (arr[i][field] == arr[index][field] && i != index) {
			    $scope.myform[name].$invalid = true;
				darray.push(i);
				break
			}
			else {
				$scope.myform[name].$invalid = false;	
			}
		}
		
		return darray;
	}
	$scope.onChangeDisplayName = function (index) {
		var dupArray=[];
		for(var i=0;i<$scope.attributetable.length;i++){
			setTimeout(function(index){
				if ($scope.attributetable[index].dispName) {
					var res = isDublication($scope.attributetable, "dispName", index, "dispName"+index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}


	$scope.addRow = function () {
		if($scope.attributetable ==null){
			$scope.attributetable=[];
		}
		var attributejson = {}
		attributejson.attributeId = $scope.attributetable.length
		attributejson.active="Y";
		$scope.attributetable.push(attributejson)
	}

	$scope.selectAllRow = function () {
		angular.forEach($scope.attributetable, function (stage) {
			stage.selected = $scope.selectallattribute;
		});
	}
	$scope.removeRow = function () {
		$scope.iSSubmitEnable = true;
		var newDataList = [];
		$scope.selectallattribute = false;
		angular.forEach($scope.attributetable, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.attrTableSelectedItem=[];
		});
		$scope.attributetable = newDataList;
	}

	$scope.attrTableSelectedItem=[];
	$scope.onChangeAttrRow=function(index,status){
		if(status ==true){
			$scope.attrTableSelectedItem.push(index);
		}
		else{
			let tempIndex=$scope.attrTableSelectedItem.indexOf(index);

			if(tempIndex !=-1){
				$scope.attrTableSelectedItem.splice(tempIndex, 1);

			}
		}	
	}
	$scope.autoMove=function(index){
		var tempAtrr=$scope.attributetable[$scope.attrTableSelectedItem[0]];
		$scope.attributetable.splice($scope.attrTableSelectedItem[0],1);
		$scope.attributetable.splice(index,0,tempAtrr);
		$scope.attrTableSelectedItem=[];
		$scope.attributetable[index].selected=false;
	
	}

	$scope.autoMoveTo=function(index){
		if(index <= $scope.attributetable.length){
			$scope.autoMove(index-1,'mapAttr');
			$scope.moveTo=null;
			$(".actions").removeClass("open");
		}
	}

	$scope.ondrop = function(e) {
		$scope.myform.$dirty=true;
	}
/******************************************************************End Attribute Table****************************************/

/******************************************************************Start Download*********************************************/
	
	$scope.downloadFileByDatastore = function (data) {
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="datastore";
		$scope.isDownloadDirective=true;
	
	};
	$scope.downloadFile = function (data) {
		if($scope.isDownloadDatapod)
		  return false;
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDownloadDirective=true;
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="datapod";
	};
	$scope.onDownloaed=function(data){
		$scope.isDownloadDatapod=data.isDownloadInprogess;
		$scope.isDownloadDatapod=data.isDownloadInprogess;
		$scope.isDownloadDirective=data.isDownloadDirective;
	}

   
/******************************************************************End  Download*********************************************/

/******************************************************************Start Upload*********************************************/

	$scope.getDetailForUpload = function (data) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if($scope.showFrom==true && $scope.isUploadingDatapod==false){
			$scope.uploaaduuid = data.uuid
			$scope.uploadDetail=data;
			$(":file").jfilestyle('clear')
			$("#csv_file").val("");
			$('#fileupload').modal({
			backdrop: 'static',
			keyboard: false
			});
	    }
	}
	
	$scope.fileNameValidate=function(data){
		console.log(data)
		$scope.isFileNameValid=data.valid;
		$scope.isFileSubmitDisable=!data.valid;
	}
	$scope.uploadFile = function () {
		if($scope.isFileSubmitDisable){
		  $scope.msg = "Special character or space not allowed in file name."
		  notify.type = 'info',
		  notify.title = 'Info',
		  notify.content = $scope.msg
		  $scope.$emit('notify', notify);
		  return false; 
		}
		$scope.isUploadingDatapod=true;
		var iEl = angular.element(document.querySelector('#csv_file'));
		var file = iEl[0].files[0]
		var fd = new FormData();
		fd.append('csvFileName', file);
		$('#fileupload').modal('hide');
		CommonService.uploadFile($scope.uploaaduuid, fd, "datapod",$scope.fileUpladDesc || "").then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
		var onSuccess = function (response) {
		  $scope.isUploadingDatapod=false;
		  $scope.fileUpladDesc="";
		  $scope.uploadDetail=null;
		  $scope.isFileNameValid=true;
          $scope.isFileSubmitDisable=true
		  $scope.executionmsg = "Data Uploaded Successfully"
		  notify.type = 'success',
		  notify.title = 'Success',
		  notify.content = $scope.executionmsg
		  $scope.$emit('notify', notify);
	
		}
		var onError = function (response) {
		  $('#fileupload').modal('hide');
		  $scope.uploadDetail=null;
		  $scope.isUploadingDatapod=false;
		  $scope.isFileNameValid=true;
          $scope.isFileSubmitDisable=true
		  notify.type = 'error',
		  notify.title = 'Error',
		  notify.content = "Some Error Occurred"
		 // $scope.$emit('notify', notify);
		}
	}
/******************************************************************End Upload*********************************************/

/******************************************************************Start Histogram*********************************************/
	$scope.gridOptionsHitogram={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	}

	$scope.filteredRowsHitogram = [];
	$scope.gridOptionsHitogram.onRegisterApi = function (gridApi) {
		$scope.gridOptionsHitogram = gridApi;
		$scope.filteredRowsHitogram = $scope.gridOptionsHitogram.core.getVisibleRows($scope.gridOptionsHitogram.grid);
	};

	$scope.getGridStyleHistogram = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsHitogram && $scope.filteredRowsHitogram.length > 0) {
			style['height'] = (($scope.filteredRowsHitogram.length < 10 ? $scope.filteredRowsHitogram.length * 50 : 400) + 70) + 'px';
		}
		else {
		
			style['height'] = "150px";
		}
		return style;
	}

	function ConvertTwoDisit(data, propName) {
		// if(isNaN(data[0][propName])){
		if (data.length > 0 &&  data[0][propName].indexOf(" - ") != -1) {
			for (var i = 0; i < data.length; i++) {
				a = data[i][propName].split(' - ')[0];
				b = data[i][propName].split('-')[1]
				data[i][propName] = parseFloat(a).toFixed(2) + " - " + parseFloat(b).toFixed(2);
				// console.log(data[i][propName])
			}
		}
		return data;
	}

	$scope.refreshDataHistogram=function(str){
		var data = $filter('filter')($scope.originalDataHistogram, str, undefined);
		$scope.gridOptionsHitogram.data= data;
	}

	$scope.onClickChart=function(){
		$scope.isShowDataGrid=false;
		$scope.isShowChart=true;
	}

	$scope.onClickGrid=function(){
		$scope.searchTextHostogram;
		$scope.isShowDataGrid=true;
		$scope.isShowChart=false;
		$scope.gridOptionsHitogram.columnDefs=[];
		$scope.gridOptionsHitogram.columnDefs=[
			{
				name: 'bucket',
				displayName: 'Bucket',
				cellClass: 'text-center',
				headerCellClass: 'text-center'
			},
			{
				name: 'frequency',
				displayName: 'Frequency',
				cellClass: 'text-center',
				headerCellClass: 'text-center'
			}
		]
		$scope.gridOptionsHitogram.data=$scope.originalDataHistogram;
	}

	$scope.calculateHistrogram=function(row){
		$('#histogamModel').modal({
			backdrop: 'static',
			keyboard: false
		});
		$scope.histogramDetail=row.colDef;
		$scope.isShowDataGrid=false;
		$scope.isShowChart=false;
		$scope.datacol=null;
		$scope.isHistogramInprogess=true;
		$scope.isHistogramError=false;
		MetadataDatapodSerivce.getAttrHistogram(row.colDef.uuid,row.colDef.version,'datapod',row.colDef.attributeId).then(function (response) { onSuccessGetAttrHistogram(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetAttrHistogram = function (response) {
			$scope.isShowDataGrid=false;
			$scope.isShowChart=true;
	//			if(row.colDef.attrType !="string"){
	//				ConvertTwoDisit(response, 'bucket');
	//			}
			$scope.isHistogramInprogess=false;
			$scope.isHistogramError=false;
			$scope.datacol={};
			if(response.length >=20){
				$scope.datacol.datapoints=response.slice(0,20);

			}else{
				$scope.datacol.datapoints=response;
			}
			$scope.originalDataHistogram=response;
			var dataColumn={}
			dataColumn.id="frequency";
			dataColumn.name="frequency"
			dataColumn.type="bar"
			dataColumn.color="#D8A2DE";
			$scope.datacol.datacolumns=[];
			$scope.datacol.datacolumns[0]=dataColumn;
			var datax={};
			datax.id ="bucket";
			$scope.datacol.datax=datax;
			
		}
		var onError =function(){
			$scope.isHistogramInprogess=false;
			$scope.isHistogramError=true;
		}
	}
/******************************************************************End Histogram*********************************************/

/******************************************************************Start DatapodSample*********************************************/
	$scope.refreshData = function () {
		var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
		$scope.gridOptions.data=$scope.getResults(data);
	};
	$scope.showSampleTable = function (data) {
		if($scope.isDataInpogress){
			return false;
		};
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDataError = false;
		$scope.isShowSimpleData = true
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		$scope.isShowDatastore=false;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isDownloadDatapod=false;
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;
		MetadataDatapodSerivce.getDatapodSample(data,$scope.sample.rows).then(function (response) { onSuccessGetDatasourceByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatasourceByType = function (response) {
			$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;	
			$scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			$scope.pagination.pageSize=10;

			for (var j = 0; j <data.attributes.length; j++) {
				var attribute = {};
				attribute.name =data.attributes[j].name;
				attribute.dname =data.name;
				attribute.uuid=data.uuid;
				attribute.version=data.version;
				attribute.attributeId=data.attributes[j].attributeId;
				attribute.headerCellTemplate='views/datapod-sample-header-template.html'
				attribute.displayName =data.attributes[j].dispName;
				attribute.attrType =data.attributes[j].type;
				attribute.width = attribute.displayName.split('').length + 5 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				$scope.gridOptions.columnDefs.push(attribute)
			}
			$scope.counter = 0;
			$scope.originalData = response;
			if ($scope.originalData.length > 0) {
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
			}
			$scope.spinner = false;

		}

		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = false;
			$scope.spinner = false;
		}
	}

	$scope.selectPage = function (pageNo) {
		$scope.pagination.currentPage = pageNo;
	};

	$scope.onPerPageChange = function () {
		$scope.pagination.currentPage = 1;
		$scope.gridOptions.data=$scope.getResults($scope.originalData)
	}

	$scope.pageChanged = function () {
		$scope.gridOptions.data=$scope.getResults($scope.originalData)
	};

	$scope.pageChangedHistogram=function(){
		$scope.gridOptionsHitogram.data=$scope.getResults($scope.originalDataHistogram);
	}

	$scope.getResults = function (params) {
		$scope.pagination.totalItems = params.length;
		if ($scope.pagination.totalItems > 0) {
			$scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize)) + 1);
		}
		else {
			$scope.pagination.to = 0;
		}
		if ($scope.pagination.totalItems < ($scope.pagination.pageSize * $scope.pagination.currentPage)) {
			$scope.pagination.from = $scope.pagination.totalItems;
		} else {
			$scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
		}
		var limit = ($scope.pagination.pageSize * $scope.pagination.currentPage);
		var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
		return params.slice(offset, limit);
	}
/******************************************************************End DatapodSample*********************************************/

/******************************************************************Start DatastoreSample*********************************************/
	$scope.gridOptionsDataStrore={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: true,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
	};

	$scope.gridOptionsDataStrore.columnDefs = [
		{
			name: 'uuid',
			width: '30%',
			enableCellEdit: false,
			visible: false,
			displayName: 'Uuid',
			headerCellClass: 'text-center'
		},
		{
			name: 'name',
			minWidth: 220,
			displayName: 'Name',
			headerCellClass: 'text-center',
			cellTemplate:'<div class="grid-tooltip" title="{{row.entity.name}}" ><div class="ui-grid-cell-contents">{{ COL_FIELD }}</div></div>',

		},
		{
			name: 'version',
			maxWidth:110,
			cellClass: 'text-center',
			visible: true,
			displayName: 'Version',
			headerCellClass: 'text-center',
			sort: {
				direction: uiGridConstants.DESC,
			// priority: 0,
			},
		},
		{
			displayName: 'Created By',
			name: 'createdBy.ref.name',
			cellClass: 'text-center',
			maxWidth:100,
			headerCellClass: 'text-center'
		},
		{
			displayName: 'Created On',
			name: 'createdOn',
			minWidth: 220,
			cellClass: 'text-center',
			headerCellClass: 'text-center',

		},
		
		{
			name: 'numRows',
			width: '10%',
			enableCellEdit: false,
			displayName: 'Rows',
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		},
		{
			name: 'action',
			displayName: 'Action',
			width: '10%',
			cellTemplate: '<input type="radio" name="action" style="margin:11px;width:16px;height:16px;" ng-click="grid.appScope.onSelectDataStore(row.entity,$index)"  ng-disabled="row.entity.isSelectedDatastore"></input>',
			cellEditableCondition:false,
			cellClass: 'text-center',
			headerCellClass: 'text-center'
		}
		
	];

	$scope.filteredRowsDatastore = [];
	$scope.gridOptionsDataStrore.onRegisterApi = function (gridApi) {
		$scope.gridApiDatastore = gridApi;
		$scope.filteredRowsDatastore = $scope.gridApiDatastore.core.getVisibleRows($scope.gridApiDatastore.grid);
	};

	$scope.getGridStyleDatastore = function () {
		console.log($scope.filteredRowsDatastore.length)
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRowsDatastore && $scope.filteredRowsDatastore.length > 0) {
			style['height'] = (($scope.filteredRowsDatastore.length < 5 ? $scope.filteredRowsDatastore.length * 40 : 200) + 40) + 'px';
		}
		else {
			style['height'] = "100px";
		}
		return style;
	}
	$scope.refreshDataDatastore=function(){
		$scope.gridOptionsDataStrore.data= $filter('filter')($scope.originalDataDatastore, $scope.searchtextDatastore, undefined);
	}
	
	
	$scope.isDisabledDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
			
		}
	}
	$scope.isEnableDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=false;
			if(data !=null && (data.uuid == $scope.gridOptionsDataStrore.data[i].uuid)){
				$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
				 
			}
		}
	}

	$scope.onSelectDataStore=function(data,index){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDisabledDSRB();
		$scope.sample.data=null;
		$scope.datastoreDetail=data
		$scope.sample.data=data;
		$scope.getResultByDatastore(data);
	}

	$scope.showDatastrores=function(data){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = false;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isDownloadDatapod=true;
		$scope.gridOptionsDataStrore.data=[];
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;
		MetadataDatapodSerivce.getDatastoreByDatapod(data,"datapod").then(function (response) { onSuccessGetDatastoreByDatapode(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatastoreByDatapode = function (response) {
			$scope.isShowDatastore=true;
			$scope.originalDataDatastore=response;
			$scope.gridOptionsDataStrore.data=response;
			console.log(response)
		}
	
	}
	
	$scope.getResultByDatastore = function (data) {
		$scope.isDataError = false;
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		$scope.isDatastoreResult=true;
		MetadataDatapodSerivce.getResultByDatastore(data.uuid,data.version,$scope.sample.rows).then(function (response) { onSuccessGetResultByDatastore(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetResultByDatastore = function (response) {
			$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;
			$scope.isEnableDSRB(data);
		    $scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			$scope.pagination.pageSize=5;
			var columnDefs=[];
			for (var j = 0; j <$scope.datapoddata.attributes.length; j++) {
				var attribute = {};
				attribute.name = $scope.datapoddata.attributes[j].name;
				attribute.displayName = $scope.datapoddata.attributes[j].dispName;
				attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				columnDefs.push(attribute)
			}
			setTimeout(function(){
				$scope.gridOptions.columnDefs=columnDefs;
			},10);
			$scope.counter = 0;
			$scope.originalData = response;
			$scope.gridOptions.data=[];
			
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
			
			$scope.spinner = false;

		}

		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = true;
			$scope.spinner = false;
			$scope.isEnableDSRB(data);
		}
	}


	$scope.refreshDataDatastore=function(){
		$scope.gridOptionsDataStrore.data= $filter('filter')($scope.originalDataDatastore, $scope.searchtextDatastore, undefined);
	}
	
	
	$scope.isDisabledDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
			
		}
	}
	$scope.isEnableDSRB=function(data){
		for(var i=0;i<$scope.gridOptionsDataStrore.data.length;i++){
			$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=false;
			if(data !=null && (data.uuid == $scope.gridOptionsDataStrore.data[i].uuid)){
				$scope.gridOptionsDataStrore.data[i].isSelectedDatastore=true;
				 
			}
		}
	}

	$scope.onSelectDataStore=function(data,index){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isDisabledDSRB();
		$scope.sample.data=null;
		$scope.datastoreDetail=data
		$scope.sample.data=data;
		$scope.getResultByDatastore(data);
	}

	$scope.showDatastrores=function(data){
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showFrom = false;
		$scope.isShowSimpleData = false;
		$scope.showGraphDiv = false;
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.isDownloadDatapod=true;
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;
		$scope.gridOptionsDataStrore.data=[];
		MetadataDatapodSerivce.getDatastoreByDatapod(data,"datapod").then(function (response) { onSuccessGetDatastoreByDatapode(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetDatastoreByDatapode = function (response) {
			$scope.isShowDatastore=true;
			$scope.originalDataDatastore=response;
			$scope.gridOptionsDataStrore.data=response;
			console.log(response)
		}
	
	}
	
	$scope.getResultByDatastore = function (data) {
		$scope.isDataError = false;
		$scope.isDataInpogress = true
		$scope.tableclass = "centercontent";
		$scope.showFrom = false;
		$scope.showGraphDiv = false;
		$scope.spinner = true;
		$scope.isDatastoreResult=true;
		MetadataDatapodSerivce.getResultByDatastore(data.uuid,data.version,$scope.sample.rows).then(function (response) { onSuccessGetResultByDatastore(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetResultByDatastore = function (response) {
			$scope.sample.rows=CF_SAMPLE.framework_sample_maxrows;
			$scope.isEnableDSRB(data);
		    $scope.gridOptions.columnDefs = [];
			$scope.isDataInpogress = false;
			$scope.tableclass = "";
			$scope.spinner = false
			$scope.pagination.pageSize=5;
			var columnDefs=[];
			for (var j = 0; j <$scope.datapoddata.attributes.length; j++) {
				var attribute = {};
				attribute.name = $scope.datapoddata.attributes[j].name;
				attribute.displayName = $scope.datapoddata.attributes[j].dispName;
				attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				columnDefs.push(attribute)
			}
			setTimeout(function(){
				$scope.gridOptions.columnDefs=columnDefs;
			},10);
			$scope.counter = 0;
			$scope.originalData = response;
			$scope.gridOptions.data=[];
			
				$scope.gridOptions.data=$scope.getResults($scope.originalData);
			
			$scope.spinner = false;

		}

		var onError = function (response) {
			$scope.isDataError = true;
			$scope.msgclass = "errorMsg"
			$scope.datamessage = "Some Error Occurred";
			$scope.isDataInpogress = true;
			$scope.spinner = false;
			$scope.isEnableDSRB(data);
		}
	}

/******************************************************************End DatastoreSample*********************************************/

/******************************************************************Start CompareMeta*********************************************/
	$scope.gridOptionsCompareMetaData={
		enableGridMenu: true,
		rowHeight: 40,
		enableRowSelection: true,
		enableSelectAll: true,
		headerTemplate: 'views/header-template.html',
		superColDefs: [{
			name: 'sourceParant',
			displayName:'Source'
		}, {
			name: 'targetParant',
			displayName:'Datapod'
		},
		{
			name: 'statusParant',
			displayName:'Status'
		}
		],
		columnDefs: [{
			name: 'sourceAttribute',
			displayName: 'Atrribute',
			superCol: 'sourceParant'
		}, {
			name: 'sourceType',
			displayName: 'Type',
			superCol: 'sourceParant'
		}, {
			name: 'sourceLength',
			displayName: 'Length',
			superCol: 'sourceParant'

		},
		{
			name: 'targetAttribute',
			displayName: 'Atrribute',
			superCol: 'targetParant'
		}, {
			name: 'targetType',
			displayName: 'Type',
			superCol: 'targetParant'
		},
		{
			name: 'targetLength',
			displayName: 'Length',
			superCol: 'targetParant'

		},
		{
			name: 'status',
			displayName: '',
			superCol: 'statusParant',
			enableColumnMenu: false,
			cellClass: 'text-center',
			cellTemplate: '<div class=\"ui-grid-cell-contents ng-scope ng-binding\"><div class="label-sm" style=" width: 88%;font-size: 13px;padding: 2px;color: white;margin: -2px auto;font-weight: 300;background-color:{{grid.appScope.path[row.entity.status].color}} !important" ng-style="">{{grid.appScope.path[row.entity.status].caption}}</div></div>'
		}],
	};  


	$scope.filteredRowsCompareMetaData = [];
	$scope.gridOptionsCompareMetaData.onRegisterApi = function (gridApi) {
		$scope.gridApiCompareMetaData = gridApi;
		$scope.filteredRowsCompareMetaData = $scope.gridApiCompareMetaData.core.getVisibleRows($scope.gridApiCompareMetaData.grid);
	};

	$scope.getGridStyleCompareMetaData = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsCompareMetaData && $scope.filteredRowsCompareMetaData.length > 0) {
			style['height'] = (($scope.filteredRowsCompareMetaData.length < 10 ? $scope.filteredRowsCompareMetaData.length * 40 : 400) + 40) + 'px';
		}
		else {
		
			style['height'] = "100px";
		}
		return style;
	}

	$scope.refreshCompareMetaData = function (searchtext) {
		$scope.gridOptionsCompareMetaData.data = $filter('filter')($scope.originalCompareMetaData,searchtext, undefined);
		
	};

	$scope.showCompareMetaData=function(data , isRefersh){
		if($scope.isShowCompareMetaData && isRefersh==false){
			return false
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.gridOptionsCompareMetaData.superColDefs[0].displayName=$scope.selectSourceType.charAt(0).toUpperCase() + $scope.selectSourceType.slice(1);;
		$scope.isShowCompareMetaData=true;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.isShowProfile=false;
		$scope.isShowQuality=false;
		$scope.gridOptionsCompareMetaData.isDataError=false;
		$scope.gridOptionsCompareMetaData.isDataInpogress=true;
		$scope.gridOptionsCompareMetaData.tableclass = "centercontent";
		MetadataDatapodSerivce.compareMetadata(data.uuid,data.version,'datapod').then(function (response) { onSuccessCompareMetadata(response.data) }, function (response) { onError(response.data) })
		var onSuccessCompareMetadata = function (response) {
			$scope.gridOptions.columnDefs.data = [];
			$scope.gridOptionsCompareMetaData.data=response;
			var count=0;
			for(var i=0;i<response.length;i++){
				if(response[i].status == "NOCHANGE"){
					count=count+1;
				}
			}
			if(response.length == count){
				$scope.isMetaSysn=true;
			}else{
				$scope.isMetaSysn=false;
			}

			$scope.originalCompareMetaData=response;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
			$scope.gridOptionsCompareMetaData.tableclass = "";
		}

		var onError = function (response) {
			$scope.gridOptionsCompareMetaData.isDataError=true;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
		}
	}

	$scope.synchronousMetadata=function(data){
		if($scope.isMetaSysn || $scope.selectSourceType=='file'){
			return false
		}
		$scope.isShowCompareMetaData=true;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.gridOptionsCompareMetaData.isDataError=false;
		$scope.gridOptionsCompareMetaData.isDataInpogress=true;
		$scope.gridOptionsCompareMetaData.tableclass = "centercontent";
		MetadataDatapodSerivce.synchronizeMetadata(data.uuid,data.version,'datapod').then(function (response) { onSuccessSynchronizeMetadata(response.data) }, function (response) { onError(response.data) })
		var onSuccessSynchronizeMetadata = function (response) {
			$scope.datapoddata=response;
			$scope.isShowCompareMetaData=false;
			$scope.showCompareMetaData($scope.datapoddata)
		}
		var onError = function (response) {
			$scope.gridOptionsCompareMetaData.isDataError=true;
			$scope.gridOptionsCompareMetaData.isDataInpogress=false;
		}
	}
/******************************************************************End CompareMeta*******************************************************/

/******************************************************************Start Dataprofile*******************************************************/
	
	$scope.gridOptionsDataProfile={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: false,
	};

	$scope.gridOptionsDataProfile.columnDefs = [];
	$scope.filteredRowsDataProfile = [];
	$scope.gridOptionsDataProfile.onRegisterApi = function (gridApi) {
		$scope.gridApiDataProfile = gridApi;
		$scope.filteredRowsDataProfile = $scope.gridApiDataProfile.core.getVisibleRows($scope.gridApiDataProfile.grid);
	};

	$scope.getGridStyleDataProfile = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsDataProfile && $scope.filteredRowsDataProfile.length > 0) {
			style['height'] = (($scope.filteredRowsDataProfile.length < 10 ? $scope.filteredRowsDataProfile.length * 40 : 400) + 50) + 'px';
		}
		else {
		
			style['height'] = "100px";
		}
		return style;
	}
	
	$scope.refreshDataProfile=function(searchtext){
		$scope.gridOptionsDataProfile.data = $filter('filter')($scope.originalDataProfile,searchtext, undefined);
	}

    /*Start showProfile*/
	$scope.showProfile = function (isRefersh) {
		if($scope.isShowProfile && isRefersh==false){
			return false
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isShowProfile=true;
		$scope.isShowQuality=false;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		$scope.getProfileResult();
	}/*End showProfile*/
	
	$scope.getProfileResult=function(){
		$scope.isProfileInprogres=true;
		$scope.isProfileDataError = false;
		$scope.tableClassDP = "centercontent";
		$scope.dataErrorMsgDP="";
		MetadataDatapodSerivce.getProfileResults("profile", $scope.datapoddata.uuid, $scope.datapoddata.version)
		.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			$scope.getColumnDetail("profile","",respone);
		}
		var onError=function(response){
			$scope.isProfileDataError=true;
			$scope.isProfileInprogres=false;
			$scope.dataErrorMsgDP="Some error occurred";
		}
	}

	$scope.getColumnDetail = function (type,resultType,result) {
		CommonService.getColunmDetail(type, resultType)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
		    $scope.gridOptionsDataProfile.columnDefs = [];
		    $scope.ColumnDetails = respone;
			$scope.isProfileDataError = false;
			$scope.isProfileInprogres=false;
			$scope.tableClassDP = "";
		    if($scope.ColumnDetails && $scope.ColumnDetails.length > 0) {
				for (var i = 0; i < $scope.ColumnDetails.length; i++) {
					var attribute = {};
					var hiveKey = ["rownum", "DatapodUUID", "DatapodVersion"]
					if (hiveKey.indexOf($scope.ColumnDetails[i].name) != -1) {
						attribute.visible = false
					} else {
						attribute.visible = true
					}
					attribute.name = $scope.ColumnDetails[i].name
					attribute.displayName = $scope.ColumnDetails[i].displayName
					attribute.width = $scope.ColumnDetails[i].name.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
					$scope.gridOptionsDataProfile.columnDefs.push(attribute)	
				}
			}
			
			$scope.gridOptionsDataProfile.data = result;
			$scope.originalDataProfile =result;
		}
		var onError=function(respone){
			$scope.isProfileDataError=true;
			$scope.isProfileInprogres=false;
			$scope.dataErrorMsgDP="Some error occurred";
		}
	}

	$scope.runDataprofile=function(){
		$scope.isProfileInprogres=true;
		$scope.isProfileDataError = false;
		$scope.tableClassDP = "centercontent";
		$scope.dataErrorMsgDP="";
		MetadataDatapodSerivce.generateProfile("profile", $scope.datapoddata.uuid, $scope.datapoddata.version)
		.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			$scope.getProfileResult();
		}
		var onError=function(response){
			$scope.isProfileDataError=true;
			$scope.isProfileInprogres=false;
			$scope.dataErrorMsgDP="Some error occurred";
		}
	}
/******************************************************************End Dataprofile*******************************************************/
/******************************************************************Start DataQuality*******************************************************/
	$scope.gridOptionsDataQuality={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: false,
	};

	$scope.gridOptionsDataQuality.columnDefs = [
		{
			name: "selected",
			maxWidth: 40,
			visible: true,
			headerCellTemplate:'<div class="ui-grid-cell-contents" style="padding-top:9px;"><input  type="checkbox" style="width: 30px;height:16px;" ng-model="grid.appScope.selectedQualityAllRow" ng-change="grid.appScope.OnSelectQualityAllRow()"/></div>',
			cellTemplate:'<div class="ui-grid-cell-contents"  style="padding-top:2px;padding-left:4px;"><input type="checkbox" style="width:20px;height:16px;" ng-model="row.entity.selected" ng-change="grid.appScope.onSelectQualityRow()"/></div>'
		},
		{
			name: 'attrName',
			width: '30%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Attribute Name',
			headerCellClass: 'text-center'
		},
		{
			name: 'checkType',
			width: '30%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Check Type',
			headerCellClass: 'text-center'
		},
		{
			name: 'checkValue',
			width: '30%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Check Value',
			headerCellClass: 'text-center'
		},
	];
	
	$scope.filteredRowsDataQuality = [];
	$scope.gridOptionsDataQuality.onRegisterApi = function (gridApi) {
		$scope.gridApiDataQuality = gridApi;
		$scope.filteredRowsDataQuality = $scope.gridApiDataQuality.core.getVisibleRows($scope.gridApiDataQuality.grid);
	};

	$scope.getGridStyleDataQuality = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsDataQuality && $scope.filteredRowsDataQuality.length > 0) {
			style['height'] = (($scope.filteredRowsDataQuality.length < 10 ? $scope.filteredRowsDataQuality.length * 40 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
	    }
		return style;
	}
	
	$scope.OnSelectQualityAllRow = function() {
		angular.forEach($scope.gridOptionsDataQuality.data, function(source) {
		  if(source.status !="Registered")
		  source.selected = $scope.selectedQualityAllRow;
		});
		if($scope.selectedQualityAllRow){
			$scope.isAttrForDqSelected=false
		}else{
			$scope.isAttrForDqSelected=true;
		}
	}
	$scope.onSelectQualityRow=function(){
		if($scope.getSelectedRow().length > 0){
			$scope.isAttrForDqSelected=false
		}
		else{
			$scope.isAttrForDqSelected=true;
		}
	}

	$scope.getSelectedRow= function() {
		var newDataList = [];
		angular.forEach($scope.gridOptionsDataQuality.data, function(selected) {
		  if (selected.selected) {
			newDataList.push(selected);
		  }
		});
		return newDataList;
	}
	/*Start showQuality*/
	$scope.showQuality = function (isRefersh) {
		if($scope.isShowQuality && isRefersh==false){
			return false
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.isShowQuality=true;
		$scope.isShowProfile=false;
		$scope.showFrom = false;
		$scope.isShowSimpleData = false
		$scope.isShowDatastore=false;
		$scope.showGraphDiv = false
		$scope.isDatastoreResult=false;
		$scope.isShowCompareMetaData=false;
		//$scope.genIntelligence();
	}/*End showQuality*/
	
	$scope.genIntelligence=function(){
		$scope.isQualityInprogres=true;
		$scope.isQualityDataError = false;
		$scope.tableClassDQ = "centercontent";
		$scope.dataErrorMsgDQ="";
		MetadataDatapodSerivce.getProfileResults("profile", $scope.datapoddata.uuid, $scope.datapoddata.version)
		.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			$scope.isQualityInprogres=false;
			$scope.gridOptionsDataQuality.data=respone;
		}
		var onError=function(response){
			$scope.isQualityDataError=true;
			$scope.isQualityDataError=false;
			$scope.dataErrorMsgDQ="Some error occurred";
		}
	}
	$scope.attrForDqGenerated=function(){
		console.log($scope.getSelectedRow());
		
	}


/******************************************************************End DataQuality*******************************************************/



})/* End MetadataDatapodController*/



