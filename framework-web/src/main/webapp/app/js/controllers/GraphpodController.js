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
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.graphpod] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.graphpod] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	
	$scope.showPage = function () {
		$scope.showForm = true
		$scope.showGraphDiv = false
	}
	$scope.enableEdit = function (uuid, version) {
		$scope.showPage();
		$state.go('creaetgraphpod', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
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
		$scope.getAllVersion($stateParams.id)
		GraphpodService.getOneByUuidandVersion($stateParams.id, $stateParams.version,CF_META_TYPES.graphpod).then(function (response) { onSuccessGetLatestByUuid(response.data) });
		var onSuccessGetLatestByUuid = function (response) {
			$scope.graphpodData = response.graphpod
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
		}
	}//End If
	else {
		
	}


	$scope.selectVersion = function (uuid, version) {
		$scope.myform.$dirty = false;
		GraphpodService.getOneByUuidandVersion(uuid, version,CF_META_TYPES.graphpod).then(function (response) { onGetByOneUuidandVersion(response.data) });
		var onGetByOneUuidandVersion = function (response) {
			$scope.graphpodData = response
			var defaultversion = {};
			defaultversion.version = response.version;
			defaultversion.uuid = response.uuid;
			$scope.graphpod.defaultVersion = defaultversion;
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
	$scope.SearchAttribute=function(index,type,proprety){
		$scope.searchAttr={}
		$scope.searchAttr.index=index;
		$scope.searchAttr.type=type;
		$scope.searchAttr.proprety=proprety;
		$scope.selectAttr=null;
		if(type =='node'){
			setTimeout(function () {$scope.selectAttr=$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]; },10);
		}
		else{
			setTimeout(function () {$scope.selectAttr=$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]; },10);
		}
		GraphpodService.getAllLatest(CF_META_TYPES.datapod).then(function (response) { onSuccessGetAllLatest(response.data) });
		var onSuccessGetAllLatest = function (response) {
			$scope.allDatapod={}
			$scope.allDatapod.options = response;
			if ($scope.selectAttr){
				var defaultoption={};
				defaultoption.uuid=$scope.selectAttr.uuid;
				defaultoption.name="";
				setTimeout(function () {
					$scope.allDatapod.defaultoption=defaultoption;
					$scope.onChangeDatapod();
				 },10);
				
			}else{
				$scope.allDatapod.defaultoption = response[0];
				$scope.onChangeDatapod();
			}
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			});	
		}
	}

	$scope.onChangeDatapod=function(){
		CommonService.getAllAttributeBySource($scope.allDatapod.defaultoption.uuid,CF_META_TYPES.datapod).then(function (response) { onSuccessAttributeBySource(response.data) });
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

	$scope.getAllAttributeBySource=function(data,index,type){
		if(!data){
			return null;
		}
		CommonService.getAllAttributeBySource(data.uuid,CF_META_TYPES.datapod).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			if(type =='node'){
		    	$scope.nodeTableArray[index].allAttributeInto=response;
			}
			else{
		    	$scope.edgeTableArray[index].allAttributeInto=response;
			}
			
		}
	}
    $scope.SubmitSearchAttr=function(){
		console.log($scope.selectAttr);
		if($scope.searchAttr && $scope.searchAttr.type=='node'){
			$scope.nodeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]=$scope.selectAttr;
		}else{
			$scope.edgeTableArray[$scope.searchAttr.index][$scope.searchAttr.proprety]=$scope.selectAttr;
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
		graphpodJson.name = $scope.graphpodData.name
		graphpodJson.desc = $scope.graphpodData.desc
		graphpodJson.active = $scope.graphpodData.active;
		graphpodJson.published = $scope.graphpodData.published;
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
				var nodePropertiesArry=[];
				nodeSourceRef.uuid=$scope.nodeTableArray[i].nodeSource.uuid;
				nodeSourceRef.type=CF_META_TYPES.datapod;
				nodeSource.ref=nodeSourceRef;
				nodeJson.nodeSource=nodeSource;
				nodeIdRef.uuid=$scope.nodeTableArray[i].nodeId.uuid;
				nodeIdRef.type=CF_META_TYPES.datapod;
				nodeId.ref=nodeIdRef;
				nodeId.attrId=$scope.nodeTableArray[i].nodeId.attributeId;
				nodeJson.nodeId=nodeId;
				nodeJson.nodeType=$scope.nodeTableArray[i].nodeType;
				nodeJson.nodeIcon=$scope.nodeTableArray[i].nodeIcon;
				nodeNameRef.uuid=$scope.nodeTableArray[i].nodeName.uuid;
				nodeNameRef.type=CF_META_TYPES.datapod;
				nodeName.ref=nodeNameRef;
				nodeName.attrId=$scope.nodeTableArray[i].nodeName.attributeId;
				nodeJson.nodeName=nodeName;
				for(var j=0;j<$scope.nodeTableArray[i].nodeProperties.length;j++){
					var nodeProperties={}
					var nodePropertiesRef={}
					nodePropertiesRef.uuid=$scope.nodeTableArray[i].nodeProperties[j].uuid;
					nodePropertiesRef.type=CF_META_TYPES.datapod;
					nodeProperties.ref=nodeNameRef;
					nodeProperties.attrId=$scope.nodeTableArray[i].nodeProperties[j].attributeId;
					nodePropertiesArry[j]=nodeProperties;
				}
				nodeJson.nodeProperties=nodePropertiesArry
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
				edgeSourceRef.type=CF_META_TYPES.datapod;
				edgeSource.ref=edgeSourceRef;
				edgeJson.edgeSource=edgeSource;
				edgeJson.edgeId=i
				edgeJson.edgeType=$scope.edgeTableArray[i].edgeType;
				edgeJson.edgeName=$scope.edgeTableArray[i].edgeName

				for(var j=0;j<$scope.edgeTableArray[i].edgeProperties.length;j++){
					var edgeProperties={}
					var edgePropertiesRef={}
					edgePropertiesRef.uuid=$scope.edgeTableArray[i].edgeProperties[j].uuid;
					edgePropertiesRef.type=CF_META_TYPES.datapod;
					edgeProperties.ref=edgePropertiesRef;
					edgeProperties.attrId=$scope.edgeTableArray[i].edgeProperties[j].attributeId;
					edgePropertiesArry[j]=edgeProperties;
				}
				edgeJson.edgeProperties=edgePropertiesArry;

				sourceNodeIdRef.uuid=$scope.edgeTableArray[i].sourceNodeId.uuid;
				sourceNodeIdRef.type=CF_META_TYPES.datapod;
				sourceNodeId.ref=sourceNodeIdRef;
				sourceNodeId.attrId=$scope.edgeTableArray[i].sourceNodeId.attributeId;
				edgeJson.sourceNodeId=sourceNodeId;
				edgeJson.sourceNodeType=$scope.edgeTableArray[i].sourceNodeType;

				targetNodeIdRef.uuid=$scope.edgeTableArray[i].targetNodeId.uuid;
				targetNodeIdRef.type=CF_META_TYPES.datapod;
				targetNodeIdId.ref=targetNodeIdRef;
				targetNodeIdId.attrId=$scope.edgeTableArray[i].targetNodeId.attributeId;
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
