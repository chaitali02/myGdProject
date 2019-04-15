MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('MetadataExpressionController', function ($state, $scope, $stateParams, $cookieStore, $sessionStorage, MetadataExpressionSerivce, privilegeSvc, $rootScope, $filter, $timeout, CommonService) {
	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
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
	$scope.mode = "false"
	$scope.isDependonDisabled = false;
	$scope.expressiondata;
	$scope.showForm = true;
	$scope.data = null;
	$scope.showGraphDiv = false
	$scope.expression = {};
	$scope.expression.versions = [];
	$scope.relation = ["relation", "dataset", "datapod"];
	$scope.logicalOperator = ["AND","OR"];
	$scope.operator = ["=", "<", ">", "<=", ">=", "IN", "NOT IN", "BETWEEN"];
	$scope.lshType = [
		{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "formula", "caption": "formula" }]
	$scope.matType = ["string", 'formula'];
	$scope.islhsSimple = true;
	$scope.isrhsSimple = true;
	$scope.islhsDatapod = false;
	$scope.islhsFormula = false;
	$scope.isrhsDatapod = false;
	$scope.isrhsFormula = false;
	$scope.ismetlhsSimple = true;
	$scope.isnotmetlhsSimple = true;
	$scope.isshowmodel = false;
	$scope.isSubmitEnable = true;
	$scope.expressionHasChanged = true;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['expression'] || [];
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges['expression'] || [];
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

	$scope.onChangeName = function (data) {
		$scope.expressiondata.displayName=data;
	}

	$scope.checkIsInrogess = function () {
		if ($scope.isEditInprogess || $scope.isEditVeiwError) {
			return false;
		}
	}

	$scope.showPage = function () {
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}

	$scope.showGraph = function (uuid, version) {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true
	}
	$scope.showHome = function (uuid, version, mode) {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showPage()
		$state.go('metaListexpression', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.expressiondata.locked == "Y") {
			return false;
		}
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showPage()
		$state.go('metaListexpression', {
			id: uuid,
			version: version,
			mode: 'false'
		});
	}
	$scope.showView = function (uuid, version) {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			$state.go('metaListexpression', {
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

	$scope.expressionFormChange = function () {
		if ($scope.mode == "true") {
			$scope.expressionHasChanged = true;
		}
		else {
			$scope.expressionHasChanged = false;
		}
	}
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
		$sessionStorage.fromStateName = fromState.name
		//console.log(fromParams)
		if (fromState.name != "matadata") {
			$sessionStorage.fromParams = fromParams
		}

	});


	$scope.convertUppdercase = function (value) {
		var resultvalue = value.split("_");
		var resultjoint = [];
		for (j = 0; j < resultvalue.length; j++) {
			resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return resultjoint.toString().replace(/,/g, " ");
	}


	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode
		$scope.isDependencyShow = true;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		MetadataExpressionSerivce.getAllVersionByUuid($stateParams.id, "expression").then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var expressionversion = {};
				expressionversion.version = response[i].version;
				$scope.expression.versions[i] = expressionversion;

			}
		}
		MetadataExpressionSerivce.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'expression')
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isEditInprogess = false;
			var defaultversion = {};
			var defaultoption = {};
			$scope.expressiondata = response.expressiondata
			defaultversion.version = response.expressiondata.version;
			defaultversion.uuid = response.expressiondata.uuid;
			$scope.expression.defaultVersion = defaultversion;
			$scope.expressiondata = response.expressiondata
			$scope.expressionmetnotmat = response.expression
			$scope.expressionTableArray = response.expressioninfo
			defaultoption.uuid = response.expressiondata.dependsOn.ref.uuid;
			defaultoption.name = response.expressiondata.dependsOn.ref.name;
			$scope.selectExpression = response.expressiondata.dependsOn.ref.type
			$scope.expressionName = $scope.convertUppdercase($scope.expressiondata.name)
			MetadataExpressionSerivce.getFormulaByType(response.expressiondata.dependsOn.ref.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
			MetadataExpressionSerivce.getAllLatest(response.expressiondata.dependsOn.ref.type).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {

				$scope.expressionRelation = response
				$scope.expressionRelation.defaultoption = defaultoption
				MetadataExpressionSerivce.getAllAttributeBySource($scope.expressiondata.dependsOn.ref.uuid, $scope.expressiondata.dependsOn.ref.type).then(function (response) { onSuccessAttributeBySource(response.data) });
				var onSuccessAttributeBySource = function (response) {
					$scope.expressionDatapod = response
				}
			}

			var tags = [];
			for (var i = 0; i < response.expressiondata.tags.length; i++) {
				var tag = {};
				tag.text = response.expressiondata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};
	}
	else {
		$scope.showactive = "false"
		$scope.expressionmetnotmat = {}
		$scope.expressiondata = {};
		$scope.expressiondata.locked = "N";
		var metinfo = {}
		metinfo.ismetlhsSimple = true;
		$scope.expressionmetnotmat.metinfo = metinfo;
		var notmetinfo = {}
		notmetinfo.isnotmetlhsSimple = true;
		$scope.expressionmetnotmat.notmetinfo = notmetinfo;
		if ($sessionStorage.fromStateName && typeof $sessionStorage.fromStateName != "undefined" && $sessionStorage.fromStateName != "metadata" && $sessionStorage.fromStateName != "metaListexpression") {
			$scope.selectExpression = $sessionStorage.dependon.type
			$scope.isDependonDisabled = true;
			MetadataExpressionSerivce.getAllLatest($scope.selectExpression).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {
				$scope.expressionRelation = response
				var defaultoption = {};
				defaultoption.uuid = $sessionStorage.dependon.uuid;
				defaultoption.name = "";
				$scope.expressionRelation.defaultoption = defaultoption;
				MetadataExpressionSerivce.getAllAttributeBySource($sessionStorage.dependon.uuid, $sessionStorage.dependon.type).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
				var onSuccessGetAllAttributeBySource = function (resposne) {
					$scope.expressionDatapod = resposne;

				}
			}
		}//End Inner If


	}
	$scope.dependsOndd = function () {
		MetadataExpressionSerivce.getAllLatest($scope.selectExpression).then(function (response) { onSuccessRelation(response.data) });
		var onSuccessRelation = function (response) {
			$scope.expressionRelation = response
			MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
			MetadataExpressionSerivce.getAllAttributeBySource($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.expressionDatapod = response;
				$scope.expressionTableArray=null;
				$scope.addRow();

			}
		}
	}
	$scope.changeRelation = function () {
		MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
		var onSuccressGetFormula = function (response) {
			$scope.expressionFormula = response;
		}
		MetadataExpressionSerivce.getAllAttributeBySource($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.expressionDatapod = response;
			$scope.expressionTableArray=null;
				$scope.addRow();

		}
	}



	$scope.selectVersion = function () {
		$scope.expressionRelation = null;
		$scope.myform.$dirty = false;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		MetadataExpressionSerivce.getOneByUuidAndVersion($scope.expression.defaultVersion.uuid, $scope.expression.defaultVersion.version, 'expression')
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			$scope.isEditInprogess = false;
			var defaultversion = {};
			var defaultoption = {};
			$scope.expressiondata = response.expressiondata
			defaultversion.version = response.expressiondata.version;
			defaultversion.uuid = response.expressiondata.uuid;
			$scope.expression.defaultVersion = defaultversion;
			$scope.expressiondata = response.expressiondata
			$scope.expressionmetnotmat = response.expression
			$scope.expressionTableArray = response.expressioninfo
			defaultoption.uuid = response.expressiondata.dependsOn.ref.uuid;
			defaultoption.name = response.expressiondata.dependsOn.ref.name;
			$scope.selectExpression = response.expressiondata.dependsOn.ref.type
			$scope.expressionName = $scope.convertUppdercase($scope.expressiondata.name)
			MetadataExpressionSerivce.getFormulaByType(response.expressiondata.dependsOn.ref.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
			MetadataExpressionSerivce.getAllLatest(response.expressiondata.dependsOn.ref.type).then(function (response) { onSuccessRelation(response.data) });
			var onSuccessRelation = function (response) {

				$scope.expressionRelation = response
				$scope.expressionRelation.defaultoption = defaultoption
				MetadataExpressionSerivce.getAllAttributeBySource($scope.expressiondata.dependsOn.ref.uuid, $scope.expressiondata.dependsOn.ref.type).then(function (response) { onSuccessAttributeBySource(response.data) });
				var onSuccessAttributeBySource = function (response) {
					$scope.expressionDatapod = response
				}
			}

			var tags = [];
			for (var i = 0; i < response.expressiondata.tags.length; i++) {
				var tag = {};
				tag.text = response.expressiondata.tags[i];
				tags[i] = tag
				$scope.tags = tags;
			}
		};
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};

	}


	$scope.selectAllRow = function () {
		angular.forEach($scope.expressionTableArray, function (expression) {
			expression.selected = $scope.selectallrow;
		});
	}
	$scope.addRow = function () {
		if ($scope.expressionTableArray == null) {

			$scope.expressionTableArray = [];
		}
		$scope.myform.$dirty=true;
		var expessioninfo = {}
		expessioninfo.islhsDatapod = false;
		expessioninfo.islhsFormula = false;
		expessioninfo.islhsSimple = true;
		expessioninfo.isrhsDatapod = false;
		expessioninfo.isrhsFormula = false;
		expessioninfo.isrhsSimple = true;
		expessioninfo.operator = $scope.operator[0]
		expessioninfo.lhstype = $scope.lshType[1]
		expessioninfo.rhstype = $scope.lshType[1]
		expessioninfo.rhsvalue;
		expessioninfo.lhsvalue;
		expessioninfo.isrhsSimple = false;
		expessioninfo.isrhsDatapod = true;
		expessioninfoisrhsFormula = false;
		expessioninfo.islhsSimple = false;
		expessioninfo.islhsDatapod = true;
		expessioninfoislhsFormula = false;
		expessioninfo.logicalOperator = $scope.expressionTableArray.length==0?"":$scope.logicalOperator[0];
		$scope.expressionTableArray.splice($scope.expressionTableArray.length, 0, expessioninfo);

	}

	$scope.removeRow = function () {
		var newDataList = [];
		$scope.selectallrow = false;
		angular.forEach($scope.expressionTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.expressionTableArray = newDataList;


	}

	$scope.selectlhsType = function (type, index) {
		if (type == "string") {
			$scope.expressionTableArray[index].islhsSimple = true;
			$scope.expressionTableArray[index].islhsDatapod = false;
			$scope.expressionTableArray[index].lhsvalue = "''";
			$scope.expressionTableArray[index].islhsFormula = false;
		}
		else if (type == "datapod") {

			$scope.expressionTableArray[index].islhsSimple = false;
			$scope.expressionTableArray[index].islhsDatapod = true;
			$scope.expressionTableArray[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.expressionTableArray[index].islhsFormula = true;
			$scope.expressionTableArray[index].islhsSimple = false;
			$scope.expressionTableArray[index].islhsDatapod = false;
			MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
		}


	}
	$scope.selectrhsType = function (type, index) {

		if (type == "string") {
			$scope.expressionTableArray[index].isrhsSimple = true;
			$scope.expressionTableArray[index].isrhsDatapod = false;
			$scope.expressionTableArray[index].isrhsFormula = false;
			$scope.expressionTableArray[index].rhsvalue = "''";
		}
		else if (type == "datapod") {

			$scope.expressionTableArray[index].isrhsSimple = false;
			$scope.expressionTableArray[index].isrhsDatapod = true;
			$scope.expressionTableArray[index].isrhsFormula = false;
		}
		else if (type == "formula") {

			$scope.expressionTableArray[index].isrhsFormula = true;
			$scope.expressionTableArray[index].isrhsSimple = false;
			$scope.expressionTableArray[index].isrhsDatapod = false;
			MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}
		}

	}


	$scope.selectmetlhsType = function (type) {
		//alert(type)
		if (type == "string") {
			$scope.expressionmetnotmat.metinfo.ismetlhsSimple = true;
			$scope.expressionmetnotmat.metinfo.metlhsvalue;
			$scope.expressionmetnotmat.metinfo.ismetFormula = false;
		}
		else if (type == "formula") {
			$scope.expressionmetnotmat.metinfo.ismetlhsSimple = false;
			$scope.expressionmetnotmat.metinfo.ismetFormula = true;
			MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}


		}

	}
	$scope.selectnotmmetlhsType = function (type) {
		//alert(type)
		if (type == "string") {
			$scope.expressionmetnotmat.notmetinfo.isnotmetlhsSimple = true;
			$scope.expressionmetnotmat.notmetinfo.isnotmetFormula = false;
			$scope.expressionmetnotmat.notmetinfo.notmetlhsvalue;
		}
		else if (type == "formula") {
			$scope.expressionmetnotmat.notmetinfo.isnotmetlhsSimple = false;
			$scope.expressionmetnotmat.notmetinfo.isnotmetFormula = true;
			MetadataExpressionSerivce.getFormulaByType($scope.expressionRelation.defaultoption.uuid, $scope.selectExpression).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				$scope.expressionFormula = response;
			}


		}

	}

	$scope.submitexpression = function () {
		var upd_tag = "N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.expressionHasChanged = true;
		$scope.myform.$dirty = false;
		var expressionjson = {}
		expressionjson.uuid = $scope.expressiondata.uuid;
		expressionjson.name = $scope.expressiondata.name;
		expressionjson.displayName = $scope.expressiondata.displayName;
		expressionjson.active = $scope.expressiondata.active;
		expressionjson.locked = $scope.expressiondata.locked;
		expressionjson.desc = $scope.expressiondata.desc;
		expressionjson.published = $scope.expressiondata.published;
		expressionjson.publicFlag = $scope.expressiondata.publicFlag;
    
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
		expressionjson.tags = tagArray;
		var dependsOn = {};
		var ref = {};
		ref.type = $scope.selectExpression;
		ref.uuid = $scope.expressionRelation.defaultoption.uuid;
		dependsOn.ref = ref;
		expressionjson.dependsOn = dependsOn
		var met = {};
		var metref = {};
		if ($scope.expressionmetnotmat.metinfo.mettype == "string") {

			metref.type = "simple";
			met.ref = metref;
			met.value = $scope.expressionmetnotmat.metinfo.metlhsvalue
		}
		else if ($scope.expressionmetnotmat.metinfo.mettype == "formula") {

			metref.type = "formula";
			metref.uuid = $scope.expressionmetnotmat.metinfo.metformula.uuid
			met.ref = metref;

		}
		expressionjson.match = met
		var notMet = {};
		var notMetref = {};
		if ($scope.expressionmetnotmat.notmetinfo.notmettype == "string") {

			notMetref.type = "simple";
			notMet.ref = notMetref;
			notMet.value = $scope.expressionmetnotmat.notmetinfo.notmetlhsvalue
		}
		else if ($scope.expressionmetnotmat.notmetinfo.notmettype == "formula") {

			notMetref.type = "formula";
			notMetref.uuid = $scope.expressionmetnotmat.notmetinfo.notmetformula.uuid
			notMet.ref = notMetref;

		}
		expressionjson.noMatch = notMet

		var expressioninfoArray = [];
		if ($scope.expressionTableArray.length > 0) {
			for (var i = 0; i < $scope.expressionTableArray.length; i++) {
				var expressioninfo = {};
				var operand = []
				var lhsoperand = {}
				var lhsref = {}
				var rhsoperand = {}
				var rhsref = {};
				expressioninfo.logicalOperator = $scope.expressionTableArray[i].logicalOperator;
				expressioninfo.operator = $scope.expressionTableArray[i].operator;
				if ($scope.expressionTableArray[i].lhstype.text == "string") {

					lhsref.type = "simple";
					lhsoperand.ref = lhsref;
					lhsoperand.value = $scope.expressionTableArray[i].lhsvalue;
				}
				else if ($scope.expressionTableArray[i].lhstype.text == "datapod") {
					if ($scope.selectExpression == "dataset") {
						lhsref.type = "dataset";

					}
					else {
						lhsref.type = "datapod";
					}
					lhsref.uuid = $scope.expressionTableArray[i].lhsdatapodAttribute.uuid;

					lhsoperand.ref = lhsref;
					lhsoperand.attributeId = $scope.expressionTableArray[i].lhsdatapodAttribute.attributeId;
				}
				else if ($scope.expressionTableArray[i].lhstype.text == "formula") {

					lhsref.type = "formula";
					lhsref.uuid = $scope.expressionTableArray[i].lhsformula.uuid;
					lhsoperand.ref = lhsref;
				}
				operand[0] = lhsoperand;
				if ($scope.expressionTableArray[i].rhstype.text == "string") {

					rhsref.type = "simple";
					rhsoperand.ref = rhsref;
					rhsoperand.value = $scope.expressionTableArray[i].rhsvalue;
				}
				else if ($scope.expressionTableArray[i].rhstype.text == "datapod") {
					if ($scope.selectExpression == "dataset") {
						rhsref.type = "dataset";

					}
					else {
						rhsref.type = "datapod";
					}
					rhsref.uuid = $scope.expressionTableArray[i].rhsdatapodAttribute.uuid;

					rhsoperand.ref = rhsref;
					rhsoperand.attributeId = $scope.expressionTableArray[i].rhsdatapodAttribute.attributeId;
				}
				else if ($scope.expressionTableArray[i].rhstype.text == "formula") {

					rhsref.type = "formula";
					rhsref.uuid = $scope.expressionTableArray[i].rhsformula.uuid;
					rhsoperand.ref = rhsref;
				}
				operand[1] = rhsoperand;
				expressioninfo.operand = operand;
				expressioninfoArray[i] = expressioninfo
			}

		}
		expressionjson.expressionInfo = expressioninfoArray
		console.log(JSON.stringify(expressionjson))
		MetadataExpressionSerivce.submit(expressionjson, 'expression', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			var dependon = {}
			dependon.type = "expression";
			dependon.id = response;
			$sessionStorage.dependon = dependon;
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.changemodelvalue();

			notify.type = 'success',
				notify.title = 'Success',
				notify.content = 'Expression Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okExpressionSave();
		}
		var onError = function (response) {
			notify.type = 'error',
				notify.title = 'Error',
				notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
		}
	}
	$scope.changemodelvalue = function () {
		$scope.isshowmodel = sessionStorage.isshowmodel
	};
	$scope.okExpressionSave = function () {
		$scope.stageName = $sessionStorage.fromStateName;
		$scope.stageParams = $sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
		$('#expressionsave').css("dispaly", "none");
		var hidemode = "yes";
		if (hidemode == 'yes') {
			if ($scope.stageName == "metaListexpression") {
				setTimeout(function () { $state.go('metadata', { 'type': 'expression' }); }, 2000);
			}
			else if ($scope.stageName != "metadata" && typeof $scope.stageParams.id != "undefined") {

				setTimeout(function () { $state.go($scope.stageName, { 'id': $scope.stageParams.id, 'version': $scope.stageParams.version, 'mode': $scope.stageParams.mode }); }, 2000);
			}
			else {
				setTimeout(function () { $state.go('metadata', { 'type': 'expression' }); }, 2000);
			}
		}

	}
});
