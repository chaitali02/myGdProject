
DatavisualizationModule = angular.module('DatavisualizationModule');

/*code for vizpod */
DatavisualizationModule.controller('MetadataVizpodController', function ($filter, $timeout, $state, $rootScope, $scope, $stateParams, $sessionStorage, $q, VizpodSerivce, privilegeSvc, CommonService) {


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
	$scope.tags = null;
	$scope.mode = "false"
	$scope.vizpoddata;
	$scope.showvizpod = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showgraphdiv = false
	$scope.graphDataStatus = false
	$scope.vizpod = {};
	$scope.vizpodTypes = ["bar-chart", "pie-chart", "line-chart", "donut-chart", "area-chart", "bubble-chart", "world-map", "usa-map", "data-grid", 'network-graph','bar-line-chart','heat-map']
	$scope.VizpodSourceTypes = ['datapod','dataset','relation'];
	$scope.vizpod.versions = [];
	$scope.isshowmodel = false;
	$scope.keylist;
	$scope.isDependencyShow = false;
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		console.log(fromParams)
		$sessionStorage.fromStateName = fromState.name
		$sessionStorage.fromParams = fromParams

	});
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['vizpod'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['vizpod'] || [];
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
	$scope.showVizpodPage = function () {
		$scope.showvizpod = true;
		$scope.showgraph = false
		$scope.graphDataStatus = false;
		$scope.showgraphdiv = false
	}//End showVizpodPage

	$scope.enableEdit = function (uuid, version) {
		$scope.showVizpodPage()
		$state.go('dvvizpod', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}

	$scope.showview = function (uuid, version) {
		$scope.showVizpodPage()
		$state.go('dvvizpod', {
			id: uuid,
			version: version,
			mode: 'true'
		});
	}

	var notify = {
		type: 'success',
		title: 'Success',
		content: 'Dashboard deleted Successfully',
		timeout: 3000 //time in ms
	};
	$scope.orderByValue = function (value) {
		return value;
	};

	$scope.indexOfByMultiplaValue = function (array, data) {
		var result = -1;
		for (var i = 0; i < array.length; i++) {
			if (array[i].uuid == data.uuid && array[i].attributeId == data.attributeId) {
				result = 0;
				break
			}
			else {
				result = -1
			}

		}//End For
		return result;
	}//End indexOfdata

	$scope.indexOfBySingleValue = function (array, data) {
		var result = -1;
		for (var i = 0; i < array.length; i++) {
			if (array[i].uuid == data.uuid) {
				result = 0;
				break
			}
			else {
				result = -1
			}

		}//End For
		return result;
	}//End indexOfdata

	$scope.beforeDropKey = function (event, ui, data) {
		var deferred = $q.defer();
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var isEnable = ($scope.mode == 'true');
		if ($scope.indexOfByMultiplaValue($scope.grouplist, data) == -1 && $scope.indexOfByMultiplaValue($scope.keylist, data) == -1 && isEnable == false && type == "datapod" || type == "dataset") {
			deferred.resolve();
		}
		else {
			deferred.reject();
		}
		return deferred.promise;
	}//End beforeDropKey

	$scope.onDropKey = function (event, ui, index) {
		var data = ui.draggable.scope().item
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
		$scope.allSourceAttribute.splice(index, 0, ui.draggable.scope().item);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//End onDropKey

	$scope.onOverCallBackKey = function (event, ui) {
		/*var data=ui.draggable.scope().item
		$scope.dupblicate=$scope.keylist.indexOf(data)*/
		//call method when droping
	}//End onOverCallBackKey

	$scope.removekey = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.keylist.splice(index, 1)
			$scope.myform.$dirty = true;
		}
	}//End removekey


	$scope.beforeDropGroup = function (event, ui, data) {
		var deferred = $q.defer();
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var isEnable = ($scope.mode == 'true');
		if ($scope.indexOfByMultiplaValue($scope.grouplist, data) == -1 && $scope.indexOfByMultiplaValue($scope.keylist, data) == -1 && isEnable == false && type == "datapod" ||  type == "dataset") {
			deferred.resolve();
		}
		else {
			deferred.reject();
		}
		return deferred.promise;
	}//End beforeDropGroup

	$scope.onDropGroup = function (event, ui, index) {
		var data = ui.draggable.scope().item
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
		$scope.allSourceAttribute.splice(index, 0, ui.draggable.scope().item);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//Emd onDropGroup

	$scope.removeGroup = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.grouplist.splice(index, 1)
			$scope.myform.$dirty = true;
		}
	}//End removeGroup


	$scope.beforeDropValue = function (event, ui, data) {
		var data = ui.draggable.scope().item
		var type = ui.draggable.scope().item.type
		var deferred = $q.defer();
		var isEnable = ($scope.mode == 'true');
		if (type == "formula" || type == "expression") {
			if ($scope.indexOfBySingleValue($scope.valuelist, data) == -1 && isEnable == false) {
				deferred.resolve();
			}
			else {
				deferred.reject();
			}
		}
		else {
			if ($scope.indexOfByMultiplaValue($scope.valuelist, data) == -1 && isEnable == false && type == "datapod" || type == "dataset") {
				deferred.resolve();
			}
			else {
				deferred.reject();
			}

		}

		return deferred.promise;
	}//End beforeDropValue

	$scope.onDropValue = function (event, ui, index) {
		var data = ui.draggable.scope().item
		var index = ui.draggable.scope().item.index;
		$scope.myform.$dirty = true;
	    $scope.allSourceAttribute.splice(index, 0, ui.draggable.scope().item);
		//$scope.allSourceAttribute[index].class='tagit-choice-select-dd'
	}//End onDropValue

	$scope.removeValue = function (index) {
		var isEnable = ($scope.mode == 'true');
		if (isEnable == false) {
			$scope.myform.$dirty = true;
			$scope.valuelist.splice(index, 1)
		}
	}//End removeValue

	$scope.$watch("isshowmodel", function (newvalue, oldvalue) {
		$scope.isshowmodel = newvalue
		sessionStorage.isshowmodel = newvalue
	})

	$scope.showVizpodGraph = function (uuid, version) {
		$scope.showvizpod = false;
		$scope.showgraph = false
		$scope.graphDataStatus = true
		$scope.showgraphdiv = true;

	}//End showVizpodGraph


	$scope.getFormulaByType = function () {
		VizpodSerivce.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetFormulaByType(response.data) });
		var onSuccessGetFormulaByType = function (response) {
			for (var i = 0; i < response.length; i++) {
				var formulajson = {};
				formulajson.index = $scope.allSourceAttribute.length;
				formulajson.id = response[i].uuid+"_"+i;
				formulajson.class = "tagit-choice_formula-dd"
				formulajson.uuid = response[i].uuid
				formulajson.dname = response[i].name
				formulajson.name = response[i].name
				formulajson.type = "formula"
				$scope.allSourceAttribute.push(formulajson)
			}//End For
		}//End onSuccessGetFormulaByType
	}

	$scope.getExpressionByType = function () {
		VizpodSerivce.getExpressionByType($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetExpressionByType(response.data) });
		var onSuccessGetExpressionByType = function (response) {
			for (var i = 0; i < response.length; i++) {
				var expressionjson = {};
				expressionjson.index = $scope.allSourceAttribute.length;
				expressionjson.id = response[i].uuid+"_"+i
				expressionjson.class = "tagit-choice_expression-dd"
				expressionjson.uuid = response[i].uuid
				expressionjson.dname = response[i].name
				expressionjson.name = response[i].name
				expressionjson.type = "expression"
				$scope.allSourceAttribute.push(expressionjson)
			}//End For
		}//End onSuccessGetFormulaByType
	}

	$scope.onChangeSourceTypes = function () {
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
		$scope.filterAttributeTags=[];
		VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
		var onSuccessGetAllLatestBySource = function (response) {
			if (response != null) {
				$scope.allSource = response
				$scope.onChangeSource(); //Call Function
			}
		}
	} //End onChangeSourceTypes

	$scope.onChangeSource = function () {
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
		$scope.filterAttributeTags=[];
		VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
		var onSuccessGetAllAttributeBySourcet = function (response) {
			$scope.allSourceAttribute = response
			$scope.allattribute = response;
			$scope.getFormulaByType()//Call Function
			$scope.getExpressionByType() // Call Function
		}//End onSuccessGetAllAttributeBySourcet
	}//End onChangeSource

	$scope.checkValue = function () {
		if ($scope.keylist.length > 0 && $scope.valuelist.length > 0) {
			$scope.myform.$dirty = true
		}
		else {
			$scope.myform.$dirty = false;

		}

	}

	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = true;

		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		VizpodSerivce.getAllVersionByUuid($stateParams.id, "vizpod").then(function (response) { onGetAllVersionByUuid(response.data) });
		var onGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var vizpodversion = {};
				vizpodversion.version = response[i].version;
				$scope.vizpod.versions[i] = vizpodversion;
			}
		}//End onGetAllVersionByUuid

		VizpodSerivce.getOneByUuidAndVersionView($stateParams.id, $stateParams.version, "vizpod").then(function (response) { onGetLatestByUuid(response.data) });
		var onGetLatestByUuid = function (response) {
			$scope.vizpoddata = response.vizpoddata;
			var defaultversion = {};
			defaultversion.version = response.vizpoddata.version;
			defaultversion.uuid = response.vizpoddata.uuid;
			$scope.vizpod.defaultVersion = defaultversion;
			$scope.sourcetype = response.vizpoddata.source.ref.type;
			$scope.vizpodtype = response.vizpoddata.type
			$scope.keylist = response.keys;
			$scope.grouplist = response.groups;
			$scope.valuelist = response.values;
			$scope.filterAttributeTags = response.detailAttr;
			var tags = [];
			if (response.vizpoddata.tags != null) {
				for (var i = 0; i < response.vizpoddata.tags.length; i++) {
					var tag = {};
					tag.text = response.vizpoddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allSource = response
				var defaultoption = {}
				defaultoption.uuid = $scope.vizpoddata.source.ref.uuid
				defaultoption.name = $scope.vizpoddata.source.ref.name
				$scope.allSource.defaultoption = defaultoption;
				VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
				var onSuccessGetAllAttributeBySourcet = function (response) {
					$scope.allSourceAttribute = response
					$scope.allattribute = response;
					$scope.getFormulaByType()//Call Function
					$scope.getExpressionByType() // Call Function
				}//End onSuccessGetAllAttributeBySourcet
			}//End onSuccessGetAllLatestBySource
		}//End onGetLatestByUuid
	}//End If

	else {
		$scope.showactive = false;
		$scope.keylist = [];
		$scope.valuelist = [];
		$scope.grouplist = [];
	}//End Else

	$scope.loadProfiles = function (query) {
		return $timeout(function () {
			return $filter('filter')($scope.allattribute, query);
		});
	}
	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.allSource = null;
		VizpodSerivce.getOneByUuidAndVersionView($scope.vizpod.defaultVersion.uuid, $scope.vizpod.defaultVersion.version, 'vizpod').then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.vizpoddata = response.vizpoddata;
			var defaultversion = {};
			defaultversion.version = response.vizpoddata.version;
			defaultversion.uuid = response.vizpoddata.uuid;
			$scope.vizpod.defaultVersion = defaultversion;
			$scope.sourcetype = response.vizpoddata.source.ref.type;
			$scope.vizpodtype = response.vizpoddata.type
			$scope.keylist = response.keys;
			$scope.grouplist = response.groups;
			$scope.valuelist = response.values;
			$scope.filterAttributeTags = response.detailAttr;
			var tags = [];
			if (response.vizpoddata.tags != null) {
				for (var i = 0; i < response.vizpoddata.tags.length; i++) {
					var tag = {};
					tag.text = response.vizpoddata.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}//End For
			}//End If

			VizpodSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allSource = response
				var defaultoption = {}
				defaultoption.uuid = $scope.vizpoddata.source.ref.uuid
				defaultoption.name = $scope.vizpoddata.source.ref.name
				$scope.allSource.defaultoption = defaultoption;
				VizpodSerivce.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
				var onSuccessGetAllAttributeBySourcet = function (response) {
					$scope.allSourceAttribute = response
					$scope.getFormulaByType()//Call Function
					$scope.getExpressionByType() // Call Function
				}//End onSuccessGetAllAttributeBySourcet
			}//End onSuccessGetAllLatestBySource
		}//End getOneByUuidAndVersion
	}//End selectVersion


	$scope.submitVizpod = function () {
        var upd_tag="N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform.$dirty = false;

		var vizpodjson = {};
		vizpodjson.uuid = $scope.vizpoddata.uuid;
		vizpodjson.name = $scope.vizpoddata.name;
		vizpodjson.limit = $scope.vizpoddata.limit;
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

		vizpodjson.tags = tagArray;
		vizpodjson.desc = $scope.vizpoddata.desc;
		vizpodjson.active = $scope.vizpoddata.active;
		vizpodjson.published = $scope.vizpoddata.published;

		var sourece = {};
		var ref = {};
		ref.uuid = $scope.allSource.defaultoption.uuid;
		ref.type = $scope.sourcetype;
		sourece.ref = ref;
		vizpodjson.source = sourece
		vizpodjson.type = $scope.vizpodtype
		var attributeInfoArray = [];
		if ($scope.filterAttributeTags != null) {
			for (var i = 0; i < $scope.filterAttributeTags.length; i++) {
				var attributeInfo = {}
				var ref = {};
				ref.type = $scope.filterAttributeTags[i].type;
				ref.uuid = $scope.filterAttributeTags[i].uuid;
				attributeInfo.ref = ref;
				attributeInfo.attributeId = $scope.filterAttributeTags[i].attributeId
				attributeInfoArray[i] = attributeInfo;
			}
		}
		vizpodjson.detailAttr = attributeInfoArray;
		var keyarray = [];
		for (var i = 0; i < $scope.keylist.length; i++) {
			var keyjson = {}
			var ref = {}
			ref.uuid = $scope.keylist[i].uuid
			ref.type = $scope.keylist[i].type;
			keyjson.ref = ref;
			keyjson.attributeId = $scope.keylist[i].attributeId;
			keyarray[i] = keyjson
		}
		vizpodjson.keys = keyarray;

		var grouparray = [];
		for (var i = 0; i < $scope.grouplist.length; i++) {
			var groupjson = {}
			var ref = {}
			ref.uuid = $scope.grouplist[i].uuid
			ref.type = $scope.grouplist[i].type;
			groupjson.ref = ref;
			groupjson.attributeId = $scope.grouplist[i].attributeId;
			grouparray[i] = groupjson
		}
		vizpodjson.groups = grouparray;

		var valuearray = [];
		for (var i = 0; i < $scope.valuelist.length; i++) {
			var valuejson = {}
			var ref = {}
			ref.uuid = $scope.valuelist[i].uuid
			ref.type = $scope.valuelist[i].type;
			valuejson.ref = ref;
			if ($scope.valuelist[i].type == "datapod" || $scope.valuelist[i].type == "dataset" ) {
				valuejson.attributeId = $scope.valuelist[i].attributeId;
			}

			valuearray[i] = valuejson
		}
		vizpodjson.values = valuearray;
		vizpodjson.filterInfo = [],
			vizpodjson.dimension = [],
			console.log(JSON.stringify(vizpodjson));
		VizpodSerivce.submit(vizpodjson, 'vizpod',upd_tag).then(function (response) { onSuccess(response.data) }, function (response) {
				onError(response.data)
			});
		var onSuccess = function (response) {
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.changemodelvalue()
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = "Vizpod Saved Successfully"
			$scope.$emit('notify', notify);
			$scope.okVizpodSave();
		}//End Submt Api
		var onError = function (response) {
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);

		}
	}//End SubmitVizpod

	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};

	$scope.okVizpodSave = function () {
		$scope.stageName = $sessionStorage.fromStateName;
		$scope.stageParams = $sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
		//$('#vizpodsave').css("dispaly","none");
		var hidemode = "yes";
		if (hidemode == 'yes') {

			if ($scope.stageName == "dvvizpod") {
				setTimeout(function () { $state.go('vizpodlist'); }, 100);
			}
			else if ($scope.stageName != "dvvizpod" && typeof $scope.stageParams.id != "undefined") {
				setTimeout(function () { $state.go($scope.stageName, { 'id': $scope.stageParams.id }); },100);
			}
			else {
				setTimeout(function () { $state.go("vizpodlist"); }, 100);
			}
		}
	}//End

})/*End of Vizpod Controller*/
