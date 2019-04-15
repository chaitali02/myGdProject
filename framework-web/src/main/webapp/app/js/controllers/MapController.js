MetadataModule = angular.module('MetadataModule');


/*code for map */
MetadataModule.controller('MetadataMapController', function ($rootScope, $state, $scope, $stateParams, 
	$cookieStore, MetadataSerivce, MetadataMapSerivce, privilegeSvc, CommonService, $timeout, $filter, CF_GRID) {

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

	$scope.userDetail = {};
	$scope.allAutoMap = ["By Name", "By Order"];
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.tags = null;
	$scope.name;
	$scope.mode = ""
	$scope.mapdata;
	$scope.showFrom = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.map = {};
	$scope.mapTableArray = [];
	$scope.MapSourceTypes = ['datapod', 'dataset', 'relation', "rule"];
	$scope.MapTargeTypes = ['datapod'];
	//$scope.targetype = $scope.MapTargeTypes[0];
	$scope.map.versions = [];
	$scope.mapHasChanged = true;
	$scope.isshowmodel = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['map'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['map'] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});

	$scope.getLovByType = function () {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			//console.log(response)
			$scope.lobTag = response[0].value
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
		$scope.showFrom = true;
		$scope.showGraphDiv = false
	}
	$scope.showHome = function (uuid, version, mode) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListmap', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.mapdata.locked == "Y") {
			return false;
		}
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage()
		$state.go('metaListmap', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if($scope.checkIsInrogess () ==false){
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListmap', {
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
	$scope.sourceAttributeTypes =
		[{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "expression", "caption": "expression" },
		{ "text": "formula", "caption": "formula" },
		{ "text": "function", "caption": "function" }]
	$scope.selectSourceType = $scope.sourceAttributeTypes[1];
	$scope.isDependencyShow = false;
	$scope.close = function () {
		if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
			//revertback

			$state.go($rootScope.previousState.name, $rootScope.previousState.params);
		}
		else {
			$state.go('metadata', { type: 'map' });
		}
	}

	$scope.mapFormChange = function () {
		if ($scope.mode == "true") {
			$scope.mapHasChanged = true;
		}
		else {
			$scope.mapHasChanged = false;
		}
	}

	$scope.onChangeName = function (data) {
		$scope.mapName = data;
		$scope.mapdata.displayName=data;
	}

	$scope.showGraph = function (uuid, version) {
		$scope.showFrom = false;
		$scope.showGraphDiv = true;
	}

	$scope.onChangeMapSourceTypes = function () {
		MetadataMapSerivce.getAllLatest($scope.sourcetype).then(function (response) { onSuccessGetAllLatestBySource(response.data) });
		var onSuccessGetAllLatestBySource = function (response) {
			if (response != null) {
				$scope.allMapSource = response
				MetadataMapSerivce.getAllAttributeBySource($scope.allMapSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
				var onSuccessGetAllAttributeBySourcet = function (response) {
					$scope.allMapSourceAttribute = response

				}
			}
		}
	}

	$scope.onChangeMapSource = function () {
		MetadataMapSerivce.getAllAttributeBySource($scope.allMapSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
		var onSuccessGetAllAttributeBySourcet = function (response) {
			$scope.allMapSourceAttribute = response

		}
	}
	$scope.onChangeTargetType = function () {
		MetadataMapSerivce.getAllLatest("datapod").then(function (response) { onSuccessGetAllLatestByTarget(response.data) });
		var onSuccessGetAllLatestByTarget = function (response) {
			$scope.allMapTarget = response;
			$scope.allMapTarget.defaultoption = null;
		}
	}

	$scope.onChangeMapTarget = function () {
		MetadataMapSerivce.getAttributesByDatapod($scope.allMapTarget.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
		var onSuccessGetAttributesByDatapod = function (response) {
			$scope.allMapTargetAttribute = response;
			for (var i = 0; i < $scope.allMapTargetAttribute.length; i++) {
				var mapinfo = {};
				var obj = {}
				obj.text = "datapod";
				obj.caption = "attribute";
				mapinfo.isSourceAtributeSimple = false;
				mapinfo.isSourceAtributeDatapod = true;
				mapinfo.isSourceAtributeFormula = false;
				mapinfo.isSourceAtributeExpression = false;
				mapinfo.sourceAttributeType = obj
				$scope.mapTableArray[i] = mapinfo;
			}

		}

	}
	$scope.setDefaultAttributeType = function ($index) {
		//alert($scope.mapTableArray.length)
	}
	$scope.onChangeFormula=function(){
		setTimeout(function(){
			if($scope.mapTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.mapTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.mapTableArray[index].isOnDropDown=true;
			}
		},10);
	}

    $scope.onChangeExpression=function(){
		setTimeout(function(){
			if($scope.mapTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.mapTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.mapTableArray[index].isOnDropDown=true;
			}
		},10);
	}
	$scope.onChangeAttribute=function(index){
		setTimeout(function(){
			if($scope.mapTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.mapTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.mapTableArray[index].isOnDropDown=true;
			}
		},10);
	}

	$scope.onChangeFunction=function(index){
		setTimeout(function(){
			if($scope.mapTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.mapTableArray[index].isOnDropDown=false;
			}
			else{
				$scope.mapTableArray[index].isOnDropDown=true;
			}
		},10);
	}

	$scope.onChangeSourceAttribute = function (type, index) {
		$scope.mapTableArray[index].isOnDropDown=true;
		if (type == "string") {
			$scope.mapTableArray[index].isSourceAtributeSimple = true;
			$scope.mapTableArray[index].isSourceAtributeDatapod = false;
			$scope.mapTableArray[index].isSourceAtributeFormula = false;
			$scope.mapTableArray[index].sourcesimple = "";
			$scope.mapTableArray[index].isSourceAtributeExpression = false;
			$scope.mapTableArray[index].isSourceAtributeFunction = false;

		}
		else if (type == "datapod") {

			$scope.mapTableArray[index].isSourceAtributeSimple = false;
			$scope.mapTableArray[index].isSourceAtributeDatapod = true;
			$scope.mapTableArray[index].isSourceAtributeFormula = false;
			$scope.mapTableArray[index].isSourceAtributeExpression = false;
			$scope.mapTableArray[index].isSourceAtributeFunction = false;
		}
		else if (type == "formula") {

			$scope.mapTableArray[index].isSourceAtributeSimple = false;
			$scope.mapTableArray[index].isSourceAtributeDatapod = false;
			$scope.mapTableArray[index].isSourceAtributeFormula = true;
			$scope.mapTableArray[index].isSourceAtributeExpression = false;
			$scope.mapTableArray[index].isSourceAtributeFunction = false;
			MetadataMapSerivce.getFormulaByType($scope.allMapSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeFormula = response.data
			}
		}
		else if (type == "expression") {

			$scope.mapTableArray[index].isSourceAtributeSimple = false;
			$scope.mapTableArray[index].isSourceAtributeDatapod = false;
			$scope.mapTableArray[index].isSourceAtributeFormula = false;
			$scope.mapTableArray[index].isSourceAtributeExpression = true;
			$scope.mapTableArray[index].isSourceAtributeFunction = false;
			MetadataMapSerivce.getExpressionByType($scope.allMapSource.defaultoption.uuid, $scope.sourcetype).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeExpression = response
			}

		}
		else if (type == "function") {

			$scope.mapTableArray[index].isSourceAtributeSimple = false;
			$scope.mapTableArray[index].isSourceAtributeDatapod = false;
			$scope.mapTableArray[index].isSourceAtributeFormula = false;
			$scope.mapTableArray[index].isSourceAtributeExpression = false;
			$scope.mapTableArray[index].isSourceAtributeFunction = true;
			$scope.mapTableArray[index].isSourceAtributeFunction = true;
			MetadataMapSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFunction(response.data) });
			var onSuccessFunction = function (response) {
				$scope.ruleLodeFunction = response
			}

		}

	}

	$scope.autoMapFeature = function (type) {
		$scope.selectedAutoMode = type
		if ($scope.selectedAutoMode == "By Name") {
			var allMapSourceAttribute = {};
			angular.forEach($scope.allMapSourceAttribute, function (val, key) {
				allMapSourceAttribute[val.name] = val;
			});

			if ($scope.mapTableArray && $scope.mapTableArray.length > 0) {
				for (var i = 0; i < $scope.mapTableArray.length; i++) {
					$scope.mapTableArray[i].sourceattribute = null;
					setTimeout(function (index) {
						var temp = $scope.allMapTargetAttribute[index].name;
						$scope.mapTableArray[index].sourceattribute = allMapSourceAttribute[temp];
						if($scope.mapTableArray.length > CF_GRID.framework_autopopulate_grid)
							$scope.mapTableArray[index].isOnDropDown=false;
						else
							$scope.mapTableArray[index].isOnDropDown=true;
					}, 10, i);
				}
			}
		}
		if ($scope.selectedAutoMode == "By Order") {
			if ($scope.mapTableArray && $scope.mapTableArray.length > 0) {
				for (var i = 0; i < $scope.allMapTargetAttribute.length; i++) {
					$scope.mapTableArray[i].sourceattribute = null;
					setTimeout(function (index) {
						$scope.mapTableArray[index].sourceattribute = $scope.allMapSourceAttribute[index];
						$scope.mapTableArray[index].sourceattribute = allMapSourceAttribute[temp];
						if($scope.allMapTargetAttribute.length > CF_GRID.framework_autopopulate_grid)
							$scope.mapTableArray[index].isOnDropDown=false;
						else
							$scope.mapTableArray[index].isOnDropDown=true;
					}, 10, i);
				}
			}

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
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		MetadataMapSerivce.getAllVersionByUuid($stateParams.id, "map")
			.then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var mapversion = {};
				mapversion.version = response[i].version;
				$scope.map.versions[i] = mapversion;
			}
		}

		MetadataMapSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'map')
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isEditInprogess = false;
			$scope.algorithmData = response;
			var defaultversion = {};
			$scope.mapdata = response.mapdata
			$scope.mapTableArray = response.maptabalearray
			defaultversion.version = response.mapdata.version;
			defaultversion.uuid = response.mapdata.uuid;
			$scope.map.defaultVersion = defaultversion;
			$scope.sourcetype = response.mapdata.source.ref.type;
			$scope.targetype = $scope.MapTargeTypes[0];
			$scope.mapName = $scope.convertUppdercase($scope.mapdata.name)
			MetadataMapSerivce.getAllLatest(response.mapdata.source.ref.type)
				.then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allMapSource = response
				var defaultoption = {};
				defaultoption.uuid = $scope.mapdata.source.ref.uuid;
				defaultoption.name = $scope.mapdata.source.ref.name;
				$scope.allMapSource.defaultoption = defaultoption;
			}

			MetadataMapSerivce.getAllLatest("datapod").then(function (response) { onSuccessGetAllLatestByTarget(response.data) });
			var onSuccessGetAllLatestByTarget = function (response) {
				$scope.allMapTarget = response
				var defaultoption = {};
				defaultoption.uuid = $scope.mapdata.target.ref.uuid;
				defaultoption.name = $scope.mapdata.target.ref.name;
				$scope.allMapTarget.defaultoption = defaultoption;
			}
			MetadataMapSerivce.getAllAttributeBySource(response.mapdata.source.ref.uuid, response.mapdata.source.ref.type).then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
			var onSuccessGetAllAttributeBySourcet = function (response) {
				$scope.allMapSourceAttribute = response

			}
			MetadataMapSerivce.getAttributesByDatapod(response.mapdata.target.ref.uuid, response.mapdata.target.ref.type, $scope.sourcetype).then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.allMapTargetAttribute = response
			}

			MetadataMapSerivce.getExpressionByType(response.mapdata.source.ref.uuid, $scope.sourcetype).then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeExpression = response
			}
			MetadataMapSerivce.getFormulaByType(response.mapdata.source.ref.uuid, $scope.sourcetype).then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.allMapLodeFormula = response.data
			}
			MetadataMapSerivce.getAllLatestFunction("function", "N").then(function (response) { onSuccessFunction(response.data) });
			var onSuccessFunction = function (response) {
				$scope.ruleLodeFunction = response
			}
			var tags = [];
			for (var i = 0; i < response.mapdata.tags.length; i++) {
				var tag = {};
				tag.text = response.mapdata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};
	}//End If
	else {
		$scope.mapdata = {};
		$scope.mapdata.locked = "N";
	}

	$scope.selectVersion = function () {
		$scope.myform.$dirty = false;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		MetadataMapSerivce.getOneByUuidAndVersion($scope.map.defaultVersion.uuid, $scope.map.defaultVersion.version, 'map')
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isEditInprogess = false;
			var defaultversion = {};
			$scope.mapdata = response.mapdata;
			$scope.mapTableArray = response.maptabalearray
			defaultversion.version = response.mapdata.version;
			defaultversion.uuid = response.mapdata.uuid;
			$scope.map.defaultVersion = defaultversion;
			$scope.sourcetype = response.mapdata.source.ref.type;
			$scope.targetype = $scope.MapTargeTypes[0];
			$scope.mapName = $scope.convertUppdercase($scope.mapdata.name)
			MetadataMapSerivce.getAllLatest(response.mapdata.source.ref.type)
				.then(function (response) { onSuccessGetAllLatestBySource(response.data) });
			var onSuccessGetAllLatestBySource = function (response) {
				$scope.allMapSource = response
				var defaultoption = {};
				defaultoption.uuid = $scope.mapdata.source.ref.uuid;
				defaultoption.name = $scope.mapdata.source.ref.name;
				$scope.allMapSource.defaultoption = defaultoption;
			}

			MetadataMapSerivce.getAllLatest("datapod")
				.then(function (response) { onSuccessGetAllLatestByTarget(response.data) });
			var onSuccessGetAllLatestByTarget = function (response) {
				$scope.allMapTarget = response
				var defaultoption = {};
				defaultoption.uuid = $scope.mapdata.target.ref.uuid;
				defaultoption.name = $scope.mapdata.target.ref.name;
				$scope.allMapTarget.defaultoption = defaultoption;
			}
			MetadataMapSerivce.getAllAttributeBySource(response.mapdata.source.ref.uuid, response.mapdata.source.ref.type)
				.then(function (response) { onSuccessGetAllAttributeBySourcet(response.data) });
			var onSuccessGetAllAttributeBySourcet = function (response) {
				$scope.allMapSourceAttribute = response

			}
			MetadataMapSerivce.getAttributesByDatapod(response.mapdata.target.ref.uuid, response.mapdata.target.ref.type, $scope.sourcetype)
				.then(function (response) { onSuccessGetAttributesByDatapod(response.data) });
			var onSuccessGetAttributesByDatapod = function (response) {
				$scope.allMapTargetAttribute = response
			}

			MetadataMapSerivce.getExpressionByType(response.mapdata.source.ref.uuid, $scope.sourcetype)
				.then(function (response) { onSuccessExpression(response.data) });
			var onSuccessExpression = function (response) {

				$scope.allMapLodeExpression = response
			}
			MetadataMapSerivce.getFormulaByType(response.mapdata.source.ref.uuid, $scope.sourcetype)
				.then(function (response) { onSuccessFormula(response.data) });
			var onSuccessFormula = function (response) {
				$scope.allMapLodeFormula = response.data
			}
			MetadataMapSerivce.getAllLatestFunction("function", "N")
				.then(function (response) { onSuccessFunction(response.data) });
			var onSuccessFunction = function (response) {
				$scope.ruleLodeFunction = response
			}
			var tags = [];
			for (var i = 0; i < response.mapdata.tags.length; i++) {
				var tag = {};
				tag.text = response.mapdata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		}
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};
	}

	$scope.submitMap = function () {
		var upd_tag = "N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.mapHasChanged = true;
		$scope.myform.$dirty = false;
		var mapJson = {};
		mapJson.uuid = $scope.mapdata.uuid;
		mapJson.name = $scope.mapdata.name;
		mapJson.displayName = $scope.mapdata.displayName;
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
		mapJson.tags = tagArray;
		mapJson.desc = $scope.mapdata.desc;
		mapJson.active = $scope.mapdata.active;
		mapJson.locked = $scope.mapdata.locked;
		mapJson.published = $scope.mapdata.published;
		mapJson.publicFlag = $scope.mapdata.publicFlag;

		var sourece = {};
		var sourceref = {};
		sourceref.uuid = $scope.allMapSource.defaultoption.uuid;
		sourceref.type = $scope.sourcetype;
		sourece.ref = sourceref;
		mapJson.source = sourece

		var target = {};
		var targetref = {};
		targetref.uuid = $scope.allMapTarget.defaultoption.uuid;
		targetref.type = $scope.targetype;
		target.ref = targetref;
		mapJson.target = target
		var attributemaparray = [];
		for (var i = 0; i < $scope.allMapTargetAttribute.length; i++) {
			var attributemap = {};
			attributemap.attrMapId = i;
			var sourceAttr = {};
			var sourceref = {};
			var targetAttr = {};
			var targetref = {};
			if ($scope.mapTableArray[i].sourceAttributeType.text == "string") {
				sourceref.type = "simple";
				sourceAttr.ref = sourceref;
				if (typeof $scope.mapTableArray[i].sourcesimple == "undefined") {
					sourceAttr.value = "";
				}
				else {
					sourceAttr.value = $scope.mapTableArray[i].sourcesimple;
				}

				attributemap.sourceAttr = sourceAttr;
			}
			else if ($scope.mapTableArray[i].sourceAttributeType.text == "datapod") {
				console.log($scope.mapTableArray[i].sourceattribute)
				sourceref.uuid = $scope.mapTableArray[i].sourceattribute.uuid;
				// if ($scope.sourcetype == "relation") {
				// 	sourceref.type = "datapod";
				// }
				// else {
				// 	sourceref.type = $scope.sourcetype;
				// }
				sourceref.type = $scope.mapTableArray[i].sourceattribute.type;
				sourceAttr.ref = sourceref;
				sourceAttr.attrId = $scope.mapTableArray[i].sourceattribute.attributeId;
				attributemap.sourceAttr = sourceAttr;
			}
			else if ($scope.mapTableArray[i].sourceAttributeType.text == "expression") {

				sourceref.type = "expression";
				sourceref.uuid = $scope.mapTableArray[i].sourceexpression.uuid;
				sourceAttr.ref = sourceref;
				attributemap.sourceAttr = sourceAttr;

			}
			else if ($scope.mapTableArray[i].sourceAttributeType.text == "formula") {

				sourceref.type = "formula";
				sourceref.uuid = $scope.mapTableArray[i].sourceformula.uuid;
				sourceAttr.ref = sourceref;
				attributemap.sourceAttr = sourceAttr;

			}
			else if ($scope.mapTableArray[i].sourceAttributeType.text == "function") {

				sourceref.type = "function";
				sourceref.uuid = $scope.mapTableArray[i].sourcefunction.uuid;
				sourceAttr.ref = sourceref;
				attributemap.sourceAttr = sourceAttr;

			}
			targetref.uuid = $scope.allMapTarget.defaultoption.uuid;
			targetref.type = $scope.targetype;
			targetAttr.ref = targetref;
			targetAttr.attrId = $scope.allMapTargetAttribute[i].attributeId;
			attributemap.targetAttr = targetAttr;
			attributemaparray[i] = attributemap;
		}
		mapJson.attributeMap = attributemaparray;
		console.log(JSON.stringify(mapJson))
		MetadataMapSerivce.submit(mapJson, 'map', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isshowmodel = true;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.mapHasChanged = true;
			notify.type = 'success',
				notify.title = 'Success',
				notify.content = 'Map Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okmapsave();
		}
		var onError = function (response) {
			notify.type = 'error',
				notify.title = 'Error',
				notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}

	$scope.okmapsave = function () {
		$('#mapsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('metadata', { 'type': 'map' }); }, 2000);
		}
	}
})/*end of map*/
