/**
 **/
GraphAnalysisModule = angular.module('GraphAnalysisModule');
GraphAnalysisModule.controller('GraphpodDetailController',function($state,$stateParams, 
	$rootScope, $scope, $sessionStorage,$filter,$timeout,GraphpodService,CommonService,privilegeSvc,
	dagMetaDataService,CF_META_TYPES,CF_LOV_TYPES,CF_GRAPHPOD) {
	
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
	}
	else {
		$scope.isAdd = true;
	}
	$scope.metaType=dagMetaDataService.elementDefs[CF_META_TYPES.graphpod].caption;
	$scope.nodeIcons=CF_GRAPHPOD.nodeIcon;
	$scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
	$scope.mode = false;
	$scope.isSubmitInProgress = false;
	$scope.isSubmitEnable = false;
	$scope.graphpodData;
	$scope.showForm = true;
	$scope.showGraphDiv = false
	$scope.graphpod = {};
	$scope.graphpod.versions = [];
	$scope.isDependencyShow = false;
	$scope.selectedAllNodeRow=false;
	$scope.selectedAllEdgeRow=false;
	$scope.selectedAllPropertyRow = false;
	$scope.allType=CF_GRAPHPOD.allType;
	$scope.selectType=$scope.allType[0];
	$scope.allNodeHighlightType=CF_GRAPHPOD.nodeHighlightType;
	
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.graphpod] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.graphpod] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
	}

	$scope.onChangeName = function (data) {
		$scope.graphpodData.displayName=data;	
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
		$state.go('creaetgraphpod', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if($scope.isPrivlage || $scope.graphpodData.locked =="Y"){
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('creaetgraphpod', {
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
			$state.go('creaetgraphpod', {
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
	
	$scope.close =function(){
		$state.go(dagMetaDataService.elementDefs[CF_META_TYPES.graphpod].listState);
	}
	$scope.getLovByType = function() {
		CommonService.getLovByType(CF_LOV_TYPES.tag).then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
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
	

	$scope.getAllVersion = function (uuid) {
		GraphpodService.getAllVersionByUuid(uuid,CF_META_TYPES.graphpod).then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var graphpodVersion = {};
				graphpodVersion.version = response[i].version;
				$scope.graphpod.versions[i] = graphpodVersion;
			}
		}//End getAllVersionByUuid
	}//End GetAllVersion

	if (typeof $stateParams.id != "undefined") {
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.getAllVersion($stateParams.id);
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		GraphpodService.getOneByUuidandVersion($stateParams.id, $stateParams.version,CF_META_TYPES.graphpod)
			.then(function (response) { onSuccessGetLatestByUuid(response.data) },function (response) { onError(response.data)});
		var onSuccessGetLatestByUuid = function (response) {
			$scope.isEditInprogess=false;
			$scope.graphpodData = response.graphpod;
			var defaultversion = {};
			defaultversion.version =$scope.graphpodData.version;
			defaultversion.uuid = $scope.graphpodData.uuid;
			$scope.graphpod.defaultVersion = defaultversion;
			var tags = [];
			$scope.nodeTableArray=response.nodeInfo;
			$scope.edgeTableArray=response.edgeInfo;
			if ($scope.graphpodData .tags != null) {
				for (var i = 0; i < $scope.graphpodData.tags.length; i++) {
					var tag = {};
					tag.text = $scope.graphpodData .tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}//End If
	else {
		$scope.graphpodData={};
		$scope.graphpodData.locked="N"
		
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		GraphpodService.getOneByUuidandVersion(uuid, version,CF_META_TYPES.graphpod)
			.then(function (response) { onGetByOneUuidandVersion(response.data)},function (response) { onError(response.data)});
		var onGetByOneUuidandVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.graphpodData = response.graphpod;
			var defaultversion = {};
			defaultversion.version =$scope.graphpodData.version;
			defaultversion.uuid = $scope.graphpodData.uuid;
			$scope.graphpod.defaultVersion = defaultversion;
			var tags = [];
			$scope.nodeTableArray=response.nodeInfo;
			$scope.edgeTableArray=response.edgeInfo;
			if ($scope.graphpodData .tags != null) {
				for (var i = 0; i < $scope.graphpodData.tags.length; i++) {
					var tag = {};
					tag.text = $scope.graphpodData .tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}

	$scope.allNodeRow = function () {
		angular.forEach($scope.nodeTableArray, function (filter) {
			filter.selected = $scope.selectedAllNodeRow;
		});
	}
    
	$scope.addNodeRow = function () {
		if ($scope.nodeTableArray == null) {
			$scope.nodeTableArray = [];
		}
		var nodeTable = {};
		nodeTable.id=$scope.nodeTableArray.length;
		nodeTable.nodeId;
		nodeTable.nodeProperties=null;
		nodeTable.allAttributeInto=[];
		$scope.nodeTableArray.splice($scope.nodeTableArray.length, 0, nodeTable);
	}
    $scope.addNodeRow();
	$scope.removeNodeRow=function(){
		var newDataList = [];
		$scope.selectedAllNodeRow = false;
		angular.forEach($scope.nodeTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.nodeTableArray = newDataList;
	}
	
	
	$scope.allEdgeRow = function () {
		angular.forEach($scope.nodeTableArray, function (filter) {
			filter.selected = $scope.selectedAllEdgeRow;
		});
	}
    
    $scope.addEdgeRow=function(){		
		if ($scope.edgeTableArray == null) {
			$scope.edgeTableArray = [];
		}
		var edgeTable = {};
		edgeTable.id=$scope.edgeTableArray.length;
		edgeTable.nodeId;
		edgeTable.nodeProperties=null;
		edgeTable.allAttributeInto=[];
		$scope.edgeTableArray.splice($scope.edgeTableArray.length, 0, edgeTable);
	}
    $scope.addEdgeRow();
	$scope.removeEdgeRow=function(){
		var newDataList = [];
		$scope.selectedAllEdgeRow = false;
		angular.forEach($scope.edgeTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.edgeTableArray = newDataList;
	}

	$scope.SearchAttribute=function(index,type,proprety){
		$scope.searchAttr={}
		$scope.searchAttr.index=index;
		$scope.searchAttr.type=type;
		$scope.searchAttr.proprety=proprety;
		$scope.selectAttr=null;
		var selectType=$scope.selectType;
		$scope.selectType=null;
		$('#searchAttr').modal({
			backdrop: 'static',
			keyboard: false
		});	
		if(type =='node'){
			setTimeout(function () {
				$scope.selectAttr=$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety];
				if($scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]){
	            	$scope.selectType=$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety].type;
				}else{
					$scope.selectType=selectType
				}
				$scope.onChangeType();
			},10);
		}
		else{
			setTimeout(function () {
				$scope.selectAttr=$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety];
				if($scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]){
					$scope.selectType=$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety].type;
				}else{
					$scope.selectType=selectType
				}
				$scope.onChangeType();
			},10);
		}
	}

    $scope.onChangeType=function(){
		GraphpodService.getAllLatest($scope.selectType).then(function (response) { onSuccessGetAllLatest(response.data) });
		var onSuccessGetAllLatest = function (response) {
			$scope.allDatapod={}
			$scope.allDatapod.options = response;
			if ($scope.selectAttr){
				var defaultoption={};
				defaultoption.uuid=$scope.selectAttr.uuid;
				defaultoption.name="";
				defaultoption.type=$scope.selectType;
				setTimeout(function () {
					$scope.allDatapod.defaultoption=defaultoption;
				 },10);
				
			}else{
				$scope.allDatapod.defaultoption = response[0];
			}
		}
	}

	$scope.onChangeDatapod=function(){
		CommonService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid,$scope.selectType).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
			if($scope.searchAttr.type =='node'){
		    	$scope.nodeTableArray[$scope.searchAttr.index].allAttributeInto=response;
			}
			else{
		    	$scope.edgeTableArray[$scope.searchAttr.index].allAttributeInto=response;
			}
			console.log(response)
			if(!$scope.selectAttr){
				$scope.selectAttr=$scope.allAttr[0]
			}
		}
	}
    $scope.filterByAttrType = function () {
		return function (item) {
			
			if (item.attrType == 'integer' || item.attrType == 'float' )
			{
				return true;
			}
			return false;
		};
	};
	$scope.getAllAttributeBySource=function(data,index,type){
		if(!data){
			return null;
		}
		CommonService.getAllAttributeBySource(data.uuid,data.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			if(type =='node'){
		    	$scope.nodeTableArray[index].allAttributeInto=response;
			}
			else{
		    	$scope.edgeTableArray[index].allAttributeInto=response;
			}
			
		}
	}
    $scope.onChangePropertyName=function(index,value,type){
		if(type =='category'){
			if(index >0){
				for(var i=0;i<$scope.propertyInfoTableArray.length-1;i++){
					if($scope.propertyInfoTableArray[i].propertyName == value){
						$scope.myHighlightForm['propertyName'+index].$invalid=true;
					}else{
						$scope.myHighlightForm['propertyName'+index].$invalid=false;
					}
				}
			}else{
				$scope.myHighlightForm['propertyName'+index].$invalid=false;
			}
	    }else if(type=='numerical'){
			if(index >0){
				for(var i=0;i<$scope.propertyInfoTableArray.length-1;i++){
					if(parseInt($scope.propertyInfoTableArray[i].propertyName) >= parseInt(value)){
						$scope.myHighlightForm['propertyName'+index].$invalid=true;
					}else{
						$scope.myHighlightForm['propertyName'+index].$invalid=false;
					}
				}
			}else{
				$scope.myHighlightForm['propertyName'+index].$invalid=false;
			}

		}
		
	}

	$scope.addHighlightInfo=function(index,type,propertyType){

		if(type =='node'){
			$scope.nodeColorInfo=propertyType=='nodeBackgroundInfo'?CF_GRAPHPOD.nodeBackGroundColor:CF_GRAPHPOD.nodeHighlightColor;
			if($scope.nodeTableArray[index][propertyType]){
				$scope.highlightInfo=$scope.nodeTableArray[index][propertyType];
				$scope.propertyInfoTableArray=$scope.highlightInfo.propertyInfoTableArray;
				$scope.highlightInfo.type=type;
				$scope.highlightInfo.caption=propertyType=='nodeBackgroundInfo'?'Node Background':'Node Highlight'
				$scope.highlightInfo.propertyType=propertyType;

			
			}else{
				$scope.highlightInfo={};
				$scope.highlightInfo.selectType="category";
				$scope.highlightInfo.type=type;
				$scope.highlightInfo.propertyType=propertyType;
				$scope.highlightInfo.caption=propertyType=='nodeBackgroundInfo'?'Node Background':'Node Highlight'
				$scope.propertyInfoTableArray=[];
				$scope.addPropertyInfoRow();
			}
			
			$scope.highlightInfo.index=index
			$scope.allAttr=$scope.nodeTableArray[index].allAttributeInto;
		}
		if(type =='edge'){
			$scope.nodeColorInfo=CF_GRAPHPOD.edgeHighlightColor;
			if($scope.edgeTableArray[index][propertyType]){
				$scope.highlightInfo=$scope.edgeTableArray[index][propertyType];
				$scope.propertyInfoTableArray=$scope.highlightInfo.propertyInfoTableArray;
				$scope.highlightInfo.type=type;
				$scope.highlightInfo.caption='Edge Highlight'
				$scope.highlightInfo.propertyType=propertyType;
			}else{
				$scope.highlightInfo={};
				$scope.highlightInfo.selectType="category";
				$scope.highlightInfo.type=type;
				$scope.highlightInfo.propertyType=propertyType;
				$scope.highlightInfo.caption='Edge Highlight'
				$scope.propertyInfoTableArray=[];
				$scope.addPropertyInfoRow();
			}
			
			$scope.highlightInfo.index=index
			$scope.allAttr=$scope.edgeTableArray[index].allAttributeInto;

		}
		setTimeout(function(){$('#addHiglightInfo').modal({
			backdrop: 'static',
			keyboard: false
		}); }, 100);
		
	}
	
	$scope.SubmitHighlightInfo=function(){
		$('#addHiglightInfo').modal('hide');
		if($scope.highlightInfo.type=='node'){
			$scope.highlightInfo.value=$scope.highlightInfo.selectType+","+$scope.highlightInfo.propertyId.name;
			$scope.highlightInfo.propertyInfoTableArray=$scope.propertyInfoTableArray;
			$scope.nodeTableArray[$scope.highlightInfo.index][$scope.highlightInfo.propertyType]=$scope.highlightInfo;
		}
		if($scope.highlightInfo.type=='edge'){
			$scope.highlightInfo.value=$scope.highlightInfo.selectType+","+$scope.highlightInfo.propertyId.name;
			$scope.highlightInfo.propertyInfoTableArray=$scope.propertyInfoTableArray;
			$scope.edgeTableArray[$scope.highlightInfo.index][$scope.highlightInfo.propertyType]=$scope.highlightInfo;
		}
	
	}
	$scope.cancelHiighlightInfo=function(){
		if($scope.highlightInfo.type=='node'){
			$scope.nodeTableArray[$scope.highlightInfo.index][$scope.highlightInfo.propertyType]=null;
		}
		if($scope.highlightInfo.type=='edge'){
			$scope.edgeTableArray[$scope.highlightInfo.index][$scope.highlightInfo.propertyType]=null;
		}
	}
	

	$scope.allPropertyRow = function () {
		angular.forEach($scope.propertyInfoTableArray, function (filter) {
			filter.selected = $scope.selectedAllPropertyRow;
		});
	}
	$scope.removePropertyInfoRow=function(){
		var newDataList = [];
		$scope.selectedAllPropertyRow = false;
		angular.forEach($scope.propertyInfoTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		$scope.propertyInfoTableArray = newDataList;
	}

	$scope.addPropertyInfoRow=function(){
		if ($scope.propertyInfoTableArray == null) {
			$scope.propertyInfoTableArray = [];
		}
		var propertyTable = {};
		propertyTable.id=$scope.propertyInfoTableArray.length;
	
		propertyTable.name;
		propertyTable.propertyValue='#61595e';
		$scope.propertyInfoTableArray.splice($scope.propertyInfoTableArray.length, 0, propertyTable);
		//setTimeout(function(){$scope.myHighlightForm['propertyName'+propertyTable.id].$invalid=false; }, 1);
		
	}

    $scope.SubmitSearchAttr=function(){
		console.log($scope.allDatapod.defaultoption);
		if($scope.searchAttr && $scope.searchAttr.type=='node'){
			$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]=$scope.allDatapod.defaultoption//$scope.selectAttr;
			$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety].type=$scope.selectType;
			$scope.nodeTableArray[$scope.searchAttr.index]["nodeProperties"]=null;
			$scope.nodeTableArray[$scope.searchAttr.index].highlightInfo=null;
			$scope.nodeTableArray[$scope.searchAttr.index].nodeBackgroundInfo=null;
			$scope.onChangeDatapod();
		}else{
			$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]=$scope.allDatapod.defaultoption//$scope.selectAttr;
			$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety].type=$scope.selectType;
			$scope.edgeTableArray[$scope.searchAttr.index].highlightInfo=null;
			setTimeout(function () {
				$scope.edgeTableArray[$scope.searchAttr.index]["edgeProperties"]=[];
			});
			
			$scope.onChangeDatapod();
		}
		$('#searchAttr').modal('hide')
	}
	$scope.submit = function () {
	    var upd_tag="N"
		$scope.isSubmitInProgress = true;
		$scope.isSubmitEnable = false;
		$scope.myform.$dirty = true;
		var graphpodJson = {}
		graphpodJson.uuid = $scope.graphpodData.uuid
		graphpodJson.name = $scope.graphpodData.name;
		graphpodJson.displayName = $scope.graphpodData.displayName;
		graphpodJson.desc = $scope.graphpodData.desc
		graphpodJson.active = $scope.graphpodData.active;
		graphpodJson.locked = $scope.graphpodData.locked;
		graphpodJson.published = $scope.graphpodData.published;
		graphpodJson.publicFlag = $scope.graphpodData.publicFlag;

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
		graphpodJson.tags = tagArray;

		var nodeInfo=[];
		if($scope.nodeTableArray){
			for(var i=0;i<$scope.nodeTableArray.length;i++){
				var nodeJson={};
				var nodeSource={}
				var nodeSourceRef={};
				var nodeId={}
				var nodeIdRef={}
				var nodeName={}
				var nodeNameRef={}
				var nodeSize={}
				var nodeSizeRef={}
				var nodePropertiesArry=[];
				nodeSourceRef.uuid=$scope.nodeTableArray[i].nodeSource.uuid;
				nodeSourceRef.type=$scope.nodeTableArray[i].nodeSource.type;
				nodeSource.ref=nodeSourceRef;
				nodeJson.nodeSource=nodeSource;
				nodeIdRef.uuid=$scope.nodeTableArray[i].nodeId.uuid;
				nodeIdRef.type=$scope.nodeTableArray[i].nodeId.type
				nodeId.ref=nodeIdRef;
				nodeId.attrId=$scope.nodeTableArray[i].nodeId.attributeId;
				nodeId.attrType=$scope.nodeTableArray[i].nodeId.attrType;
				nodeJson.nodeId=nodeId;
				nodeJson.nodeType=$scope.nodeTableArray[i].nodeType;
				nodeJson.nodeIcon=$scope.nodeTableArray[i].nodeIcon.value;
				nodeNameRef.uuid=$scope.nodeTableArray[i].nodeName.uuid;
				nodeNameRef.type=$scope.nodeTableArray[i].nodeName.type;
				nodeName.ref=nodeNameRef;
				
				nodeName.attrId=$scope.nodeTableArray[i].nodeName.attributeId;
				nodeName.attrType=$scope.nodeTableArray[i].nodeName.attrType;
				nodeJson.nodeName=nodeName;
				if($scope.nodeTableArray[i].nodeSize){
					nodeSizeRef.uuid=$scope.nodeTableArray[i].nodeSize.uuid;
					nodeSizeRef.type=$scope.nodeTableArray[i].nodeSize.type;
					nodeSize.ref=nodeSizeRef;
					nodeSize.attrId=$scope.nodeTableArray[i].nodeSize.attributeId;
					nodeSize.attrType=$scope.nodeTableArray[i].nodeSize.attrType;
					nodeJson.nodeSize=nodeSize;
				}else{
					nodeJson.nodeSize=null;
				}
				for(var j=0;j<$scope.nodeTableArray[i].nodeProperties.length;j++){
					var nodeProperties={}
					var nodePropertiesRef={}
					nodePropertiesRef.uuid=$scope.nodeTableArray[i].nodeProperties[j].uuid;
					nodePropertiesRef.type=$scope.nodeTableArray[i].nodeProperties[j].type;
					nodeProperties.ref=nodeNameRef;
					nodeProperties.attrId=$scope.nodeTableArray[i].nodeProperties[j].attributeId;
					nodeProperties.attrType=$scope.nodeTableArray[i].nodeProperties[j].attrType;
					nodePropertiesArry[j]=nodeProperties;
				}
				nodeJson.nodeProperties=nodePropertiesArry;

				var highlightInfo={};
				var propertyId={};
				var propertyIdRef={};
				if($scope.nodeTableArray[i].highlightInfo !=null){
					highlightInfo.type=$scope.nodeTableArray[i].highlightInfo.selectType;
					propertyIdRef.type=$scope.nodeTableArray[i].highlightInfo.propertyId.type;
					propertyIdRef.uuid=$scope.nodeTableArray[i].highlightInfo.propertyId.uuid;
					propertyId.ref=propertyIdRef;
					propertyId.attrId=$scope.nodeTableArray[i].highlightInfo.propertyId.attributeId;
					propertyId.attrType=$scope.nodeTableArray[i].highlightInfo.propertyId.attrType;
					highlightInfo.propertyId=propertyId;
					var propertyInfoArray=[];
					if($scope.nodeTableArray[i].highlightInfo.propertyInfoTableArray.length >0){
						for (var j=0;j<$scope.nodeTableArray[i].highlightInfo.propertyInfoTableArray.length;j++){
							var propertyInfo={};
							propertyInfo.propertyName=$scope.nodeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyName;
							propertyInfo.propertyValue=$scope.nodeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyValue;
							propertyInfoArray[j]=propertyInfo;
						}
					}
					highlightInfo.propertyInfo=propertyInfoArray;
					nodeJson.highlightInfo=highlightInfo
			    }else{
					nodeJson.highlightInfo=null;
				}

				var nodeBackgroundInfo={};
				var NBPropertyId={};
				var NBPropertyIdRef={};
				if($scope.nodeTableArray[i].nodeBackgroundInfo !=null){
					nodeBackgroundInfo.type=$scope.nodeTableArray[i].nodeBackgroundInfo.selectType;
					NBPropertyIdRef.type=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyId.type;
					NBPropertyIdRef.uuid=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyId.uuid;
					NBPropertyId.ref=NBPropertyIdRef;
					NBPropertyId.attrId=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyId.attributeId;
					NBPropertyId.attrType=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyId.attrType;
					nodeBackgroundInfo.propertyId=NBPropertyId;
					var NBPropertyInfoArray=[];
					if($scope.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray.length >0){
						for (var j=0;j<$scope.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray.length;j++){
							var propertyInfo={};
							propertyInfo.propertyName=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray[j].propertyName;
							propertyInfo.propertyValue=$scope.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray[j].propertyValue;
							NBPropertyInfoArray[j]=propertyInfo;
						}
					}
					nodeBackgroundInfo.propertyInfo=NBPropertyInfoArray;
					nodeJson.nodeBackgroundInfo=nodeBackgroundInfo;
		 	    }else{
					nodeJson.nodeBackgroundInfo=null;
				}
				nodeInfo[i]=nodeJson
			}
	    }
		graphpodJson.nodeInfo=nodeInfo;
		var edgeInfo=[];
		if($scope.edgeTableArray){
			for(var i=0;i<$scope.edgeTableArray.length;i++){
				var edgeJson={};
				var edgePropertiesArry=[];
				var edgeSource={}
				var edgeSourceRef={};
				var sourceNodeId={};
				var sourceNodeIdRef={};
				var targetNodeIdId={};
				var targetNodeIdRef={};
				edgeSourceRef.uuid=$scope.edgeTableArray[i].edgeSource.uuid;
				edgeSourceRef.type=$scope.edgeTableArray[i].edgeSource.type
				edgeSource.ref=edgeSourceRef;
				edgeJson.edgeSource=edgeSource;
				edgeJson.edgeId=i
				edgeJson.edgeType=$scope.edgeTableArray[i].edgeType;
				edgeJson.edgeName=$scope.edgeTableArray[i].edgeName

				for(var j=0;j<$scope.edgeTableArray[i].edgeProperties.length;j++){
					var edgeProperties={}
					var edgePropertiesRef={}
					edgePropertiesRef.uuid=$scope.edgeTableArray[i].edgeProperties[j].uuid;
					edgePropertiesRef.type=$scope.edgeTableArray[i].edgeProperties[j].type;
					edgeProperties.ref=edgePropertiesRef;
					edgeProperties.attrId=$scope.edgeTableArray[i].edgeProperties[j].attributeId;
					edgeProperties.attrType=$scope.edgeTableArray[i].edgeProperties[j].attrType;
					edgePropertiesArry[j]=edgeProperties;
				}
				edgeJson.edgeProperties=edgePropertiesArry;
                var highlightInfo={};
				var propertyId={};
				var propertyIdRef={};
				if($scope.edgeTableArray[i].highlightInfo !=null){
					highlightInfo.type=$scope.edgeTableArray[i].highlightInfo.selectType;
					propertyIdRef.type=$scope.edgeTableArray[i].highlightInfo.propertyId.type;
					propertyIdRef.uuid=$scope.edgeTableArray[i].highlightInfo.propertyId.uuid;
					propertyId.ref=propertyIdRef;
					propertyId.attrId=$scope.edgeTableArray[i].highlightInfo.propertyId.attributeId;
					propertyId.attrType=$scope.edgeTableArray[i].highlightInfo.propertyId.attrType;
					highlightInfo.propertyId=propertyId;
					var propertyInfoArray=[];
					if($scope.edgeTableArray[i].highlightInfo.propertyInfoTableArray.length >0){
						for (var j=0;j<$scope.edgeTableArray[i].highlightInfo.propertyInfoTableArray.length;j++){
							var propertyInfo={};
							propertyInfo.propertyName=$scope.edgeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyName;
							propertyInfo.propertyValue=$scope.edgeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyValue;
							propertyInfoArray[j]=propertyInfo;
						}
					}
					highlightInfo.propertyInfo=propertyInfoArray;
					edgeJson.highlightInfo=highlightInfo;
			    }else{
					edgeJson.highlightInfo=null;
				}

				sourceNodeIdRef.uuid=$scope.edgeTableArray[i].sourceNodeId.uuid;
				sourceNodeIdRef.type=$scope.edgeTableArray[i].sourceNodeId.type;
				sourceNodeId.ref=sourceNodeIdRef;
				sourceNodeId.attrId=$scope.edgeTableArray[i].sourceNodeId.attributeId;
				sourceNodeId.attrType=$scope.edgeTableArray[i].sourceNodeId.attrType;
				edgeJson.sourceNodeId=sourceNodeId;
				edgeJson.sourceNodeType=$scope.edgeTableArray[i].sourceNodeType;

				targetNodeIdRef.uuid=$scope.edgeTableArray[i].targetNodeId.uuid;
				targetNodeIdRef.type=$scope.edgeTableArray[i].targetNodeId.type;
				targetNodeIdId.ref=targetNodeIdRef;
				targetNodeIdId.attrId=$scope.edgeTableArray[i].targetNodeId.attributeId;
				targetNodeIdId.attrType=$scope.edgeTableArray[i].targetNodeId.attrType;
				edgeJson.targetNodeId=targetNodeIdId;
				edgeJson.targetNodeType=$scope.edgeTableArray[i].targetNodeType;

				edgeInfo[i]=edgeJson;
			}
	    }
		graphpodJson.edgeInfo=edgeInfo;
		console.log(JSON.stringify(graphpodJson));
		GraphpodService.submit(graphpodJson,CF_META_TYPES.graphpod,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isSubmitInProgress = false;
			$scope.isSubmitEnable = true;
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Graphpod Saved Successfully'
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
		setTimeout(function () {$scope.close()}, 2000);
	}
});


GraphAnalysisModule.controller('GraphpodResultController',function($state,$stateParams,$rootScope,$scope,$filter,$timeout,
	GraphpodService,CommonService,privilegeSvc,dagMetaDataService,CF_META_TYPES,CF_LOV_TYPES,CF_GRAPHPOD) {
		$scope.isD3FDGraphShow=true;
		$scope.isD3KnowlageGraphShow=false;
		$scope.graphExecDetail={};
		$scope.graphExecDetail.uuid=$stateParams.id;
		$scope.graphExecDetail.version=$stateParams.version;

		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
		  var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		  $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		  
		});
		
		$scope.metaType=CF_META_TYPES.graphexec;
		$scope.userDetail={}
		$scope.userDetail.uuid= $rootScope.setUseruuid;
		$scope.userDetail.name= $rootScope.setUserName; 
		
		
		CommonService.getOneByUuidAndVersion($scope.graphExecDetail.uuid,$scope.graphExecDetail.version,CF_META_TYPES.graphexec).then(function(response){onSuccessGetByOneUuidAndVersion(response.data)});
		function onSuccessGetByOneUuidAndVersion(response){
			$scope.execDetali=response;
		}
		
		$scope.showFDGraph=function(){
			$scope.isD3FDGraphShow=true;
			$scope.isD3KnowlageGraphShow=false;
		}
 
		$scope.showKGraph=function(){
			$scope.isD3FDGraphShow=false;
			$scope.isD3KnowlageGraphShow=true;
		}

		$scope.applyFilter=function(){
			 $scope.$broadcast('transferUp',null);
		}
});