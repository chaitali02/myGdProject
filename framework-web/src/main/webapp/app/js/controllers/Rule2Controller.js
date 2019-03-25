
RuleModule = angular.module('RuleModule');
RuleModule.controller('RuleDetailController', function (dagMetaDataService, $rootScope, $state, $scope, $stateParams, $timeout, $filter, Rule2Service, privilegeSvc, CommonService, CF_FILTER, CF_META_TYPES, CF_DOWNLOAD) {
	$rootScope.isCommentVeiwPrivlage = true;
	$scope.paramTypes = ["paramlist", "paramset"];
	$scope.allFormats = CF_DOWNLOAD.formate;

	if ($stateParams.mode == 'true') {
		$scope.isEdit = false;
		$scope.isversionEnable = false;
		$scope.isAdd = false;
		$scope.isDragable = "false";
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else if ($stateParams.mode == 'false') {
		$scope.isEdit = true;
		$scope.isversionEnable = true;
		$scope.isAdd = false;
		$scope.isDragable = "true";
		$scope.isPanelActiveOpen = true;
		var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges[CF_META_TYPES.comment] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled = $rootScope.isCommentVeiwPrivlage;

		});
	}
	else {
		$scope.isAdd = true;
		$scope.isDragable = "true";
		$scope.isEditInprogess = false;
		$scope.isEditVeiwError = false;

	}
	$scope.ruleParamListParam=[];
	$scope.expanded = false;
	$scope.userDetail = {}
	$scope.userDetail.uuid = $rootScope.setUseruuid;
	$scope.userDetail.name = $rootScope.setUserName;
	$scope.mode = "false";
	$scope.dataLoading = false;
	$scope.iSSubmitEnable = false;
	$scope.continueCount = 1;
	$scope.ruleVersion = {};
	$scope.ruleVersion.versions = [];
	$scope.rule = {};
	$scope.showForm = true;
	$scope.data = null;
	$scope.showgraph = false
	$scope.showGraphDiv = false
	$scope.graphDataStatus = false;
	$scope.logicalOperator = ["AND", "OR"];
	$scope.SourceTypes = ["datapod", "relation", 'dataset'];
	$scope.spacialOperator = ['<', '>', '<=', '>=', '=', 'LIKE', 'NOT LIKE', 'RLIKE'];
	$scope.rhsNA = ['NULL', "NOT NULL"];
	$scope.operator = CF_FILTER.operator;
	$scope.isSubmitEnable = true;
	$scope.criteriaTableArray = null;
	$scope.isDependencyShow = false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['rule2'] || [];
	console.log($scope.privileges.indexOf('Edit'))
	$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated', function (e, data) {
		$scope.privileges = privilegeSvc.privileges[CF_META_TYPES.rule2] || [];
		$scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
	});
	$scope.lhsType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "formula", "caption": "formula" }];

	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};
	$scope.sourceAttributeTypes = [
		{ "text": "string", "caption": "string" },
		{ "text": "datapod", "caption": "attribute" },
		{ "text": "expression", "caption": "expression" },
		{ "text": "formula", "caption": "formula" },
		{ "text": "function", "caption": "function" },
		{ "text": "paramlist", "caption": "paramlist" }
	];

	$scope.checkIsInrogess = function () {
		if ($scope.isEditInprogess || $scope.isEditVeiwError) {
			return false;
		}
	}

	/*Start showPage*/
	$scope.showPage = function () {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showForm = true;
		$scope.showGraphDiv = false
	}/*End showPage*/

	$scope.showHome = function (uuid, version, mode) {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showPage()
		var state = dagMetaDataService.elementDefs["rule2"].detailState
		$state.go(state, {
			id: uuid,
			version: version,
			mode: mode
		});
	}
	$scope.showGraph = function () {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showForm = false;
		$scope.showGraphDiv = true;

	}/*End ShowGraph*/

	$scope.enableEdit = function (uuid, version) {
		if ($scope.isPrivlage || $scope.rule.locked == "Y") {
			return false;
		}
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		$scope.showPage()
		var state = dagMetaDataService.elementDefs["rule2"].detailState
		setTimeout(function () { $state.go(state, { 'id': uuid, 'version': version, 'mode': 'false' }); }, 100);
	}

	$scope.showView = function (uuid, version) {
		if ($scope.checkIsInrogess() == false) {
			return false;
		}
		if (!$scope.isEdit) {
			$scope.showPage()
			var state = dagMetaDataService.elementDefs["rule2"].detailState
			setTimeout(function () { $state.go(state, { 'id': uuid, 'version': version, 'mode': 'false' }); }, 100);
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

	$scope.countContinue = function () {
		if ($scope.continueCount == 3) {
			if ($scope.isDuplication == true) {
				return true;
			}
		}
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

	$scope.ondrop = function (e) {
		$scope.myform.$dirty = true;
	}

	$scope.getFunctionByCriteria = function () {
		CommonService.getFunctionByCriteria("", "N", "function").then(function (response) { onSuccessFuntion(response.data) });
		var onSuccessFuntion = function (response) {
			$scope.allSourceFunction = response;
			$scope.allFunction = response;
		}
	}

	$scope.getExpressionByType = function () {
		Rule2Service.getExpressionByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessExpression(response.data) });
		var onSuccessExpression = function (response) {
			$scope.allExpress = response
		}
	}

	$scope.getFormulaByType = function () {
		Rule2Service.getFormulaByType($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessFormula(response.data) });
		var onSuccessFormula = function (response) {
			$scope.allSourceFormula = response.data;
			$scope.allFilterFormula = response.data;
			$scope.getFormulaByParamList();
		}
	}

	$scope.getFormulaByParamList = function () {
		if ($scope.allparamlist.defaultoption) {
			Rule2Service.getFormulaByType($scope.allparamlist.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccressGetFormula(response.data) });
			var onSuccressGetFormula = function (response) {
				if ($scope.allFilterFormula && $scope.allFilterFormula.length > 0) {
					$scope.allFilterFormula = $scope.allFilterFormula.concat(response.data)
				} else {
					$scope.allParamlistFormula = response.data
					$scope.allFilterFormula = $scope.allParamlistFormula;
				}
			}
		} else {
			$scope.allFilterFormula = $scope.allSourceFormula;
		}
	}

	$scope.getAllAttributeBySource = function (isAddRowCriteria) {
		Rule2Service.getAllAttributeBySource($scope.allSource.defaultoption.uuid, $scope.selectSourceType).then(function (response) { onSuccessGetDatapodByRelation(response.data) })
		var onSuccessGetDatapodByRelation = function (response) {
			$scope.sourcedatapodattribute = response;
			$scope.lhsdatapodattributefilter = response;
			$scope.allattribute = response;
			if (isAddRowCriteria == true) {
				$scope.addRowCriteria();
			}
		}
	}
	$scope.getAllLatest = function (type, defaultvalue, isAddRowCriteria) {
		Rule2Service.getAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			$scope.allSource = response;
			if (defaultvalue != null) {
				var defaultoption = {};
				defaultoption.type = defaultvalue.ref.type
				defaultoption.uuid = defaultvalue.ref.uuid
				$scope.allSource.defaultoption = defaultoption;
			}
			$scope.getAllAttributeBySource(isAddRowCriteria);
			$scope.getFormulaByType();
			$scope.getExpressionByType();

		}
	}
	$scope.selectType = function () {
		$scope.getAllLatest($scope.selectSourceType, null, true);
		$scope.filterTableArray = null;
		$scope.criteriaTableArray = null;
		//$scope.addRowCriteria();
		$scope.rule.entityType = null;
	}

	$scope.selectOption = function () {
		$scope.getAllAttributeBySource(true);
		$scope.filterTableArray = null;
		$scope.getFormulaByParamList();
		$scope.criteriaTableArray = null;
		//	$scope.addRowCriteria();
		$scope.rule.entityType = null;
		$scope.getFormulaByType();
	}

	$scope.getParamByApp = function () {
		CommonService.getParamByApp($rootScope.appUuidd || "", "application").
			then(function (response) { onSuccessGetParamByApp(response.data) });
		var onSuccessGetParamByApp = function (response) {
			//$scope.allparamlistParams = [];
			if (response.length > 0) {
				var paramsArray = [];
				for (var i = 0; i < response.length; i++) {
					var paramsjson = {};
					paramsjson.uuid = response[i].ref.uuid;
					paramsjson.name = response[i].ref.name + "." + response[i].paramName;
					paramsjson.attributeId = response[i].paramId;
					paramsjson.attrType = response[i].paramType;
					paramsjson.paramName = response[i].paramName;
					paramsjson.caption = "app." + paramsjson.paramName
					paramsArray[i] = paramsjson
				}
				$scope.allparamlistParams = angular.copy(paramsArray);
			}
				$scope.getOneByUuidParamList();
		    
		}
	
		
	}

	$scope.onChangeParamListOFRule = function () {
		$scope.allFilterFormula == null;
		$scope.allFilterFormula = $scope.allSourceFormula;
		$scope.getFormulaByParamList();
		setTimeout(function () { $scope.paramTypes = ["paramlist", "paramset"]; }, 1);
		$scope.getParamByApp();
	}

	$scope.getOneByUuidParamList = function () {
		console.log($scope.allparamlistParams)
		if ($scope.allparamlist && $scope.allparamlist.defaultoption != null) {
			Rule2Service.getLatestByUuid($scope.allparamlist.defaultoption.uuid, "paramlist").
				then(function (response) { onSuccessParamList(response.data) });
			var onSuccessParamList = function (response) {
				let paramsArray = [];
				for (let i = 0; i < response.params.length; i++) {
					let paramsjson = {};
					paramsjson.uuid = response.uuid;
					paramsjson.name = response.name + "." + response.params[i].paramName;
					paramsjson.attributeId = response.params[i].paramId;
					paramsjson.attrType = response.params[i].paramType;
					paramsjson.paramName = response.params[i].paramName;
					paramsjson.caption = "rule." + paramsjson.paramName;
					paramsArray[i] = paramsjson;
				}
				$scope.ruleParamListParam = paramsArray;
				
				if ($scope.allparamlistParams && $scope.allparamlistParams.length > 0)
					$scope.allparamlistParams = $scope.allparamlistParams.concat($scope.ruleParamListParam);
			}
		}
	}

	$scope.getAllLatestParamListByTemplate = function () {
		CommonService.getAllLatestParamListByTemplate('Y', "paramlist", "rule").then(function (response) {
			onSuccessGetAllLatestParamListByTemplate(response.data)
		});
		var onSuccessGetAllLatestParamListByTemplate = function (response) {
			$scope.allparamlist = {};

			if ($scope.rule.paramList != null && $scope.allparamlist != null) {
				var defaultoption = {};
				defaultoption.uuid = $scope.rule.paramList.ref.uuid;
				defaultoption.name = $scope.rule.paramList.ref.name;
				$scope.allparamlist.defaultoption = defaultoption;
				$scope.getOneByUuidParamList();

			} else {
				$scope.allparamlist.defaultoption = null;
			}
			$scope.allparamlist.options = response;
		}
	}

	$scope.getAllLatestParamListByTemplate();
	if (typeof $stateParams.id != "undefined") {
		$scope.showactive = "true"
		$scope.mode = $stateParams.mode;
		$scope.isDependencyShow = true;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		Rule2Service.getAllVersionByUuid($stateParams.id, CF_META_TYPES.rule2).then(function (response) { onSuccessGetAllVersionByUuid(response.data) });
		var onSuccessGetAllVersionByUuid = function (response) {
			for (var i = 0; i < response.length; i++) {
				var ruleVersion = {};
				ruleVersion.version = response[i].version;
				$scope.ruleVersion.versions[i] = ruleVersion;
			}
		}

		Rule2Service.getOneByUuidAndVersion($stateParams.id, $stateParams.version, CF_META_TYPES.rule2)
			.then(function (response) { onSuccessResult(response.data) }, function (response) { onError(response.data) });
		var onSuccessResult = function (response) {
			$scope.isEditInprogess = false;
			$scope.rule = response.rule;
			$scope.selectSourceType = response.rule.sourceInfo.ref.type
			var defaultversion = {};
			defaultversion.version = response.rule.version;
			defaultversion.uuid = response.rule.uuid;
			$scope.ruleVersion.defaultVersion = defaultversion;
			$scope.tags = response.tags;

			if ($scope.rule.paramList != null && $scope.allparamlist != null) {
				var defaultoption = {};
				defaultoption.uuid = $scope.rule.paramList.ref.uuid;
				defaultoption.name = $scope.rule.paramList.ref.name;
				$scope.allparamlist.defaultoption = defaultoption;
			}
			$scope.getParamByApp();
			$scope.getAllLatest($scope.selectSourceType, response.rule.sourceInfo, false);
			$scope.getFunctionByCriteria();
			$scope.filterTableArray = response.filterInfo;
			$scope.criteriaTableArray = response.criteriaInfo;
			$scope.selectEntityId = {};
			$scope.selectEntityId.uuid = $scope.rule.entityId.ref.uuid;
			$scope.selectEntityId.type = $scope.rule.entityId.ref.type;
			$scope.selectEntityId.attributeId = $scope.rule.entityId.attrId;


		}//End onSuccessResult
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};
	}//End If
	else {
		$scope.rule = {};
		$scope.rule.locked = "N";
		$scope.rule.limit = -1;
		$scope.rule.format = CF_DOWNLOAD.formate[0];
		$scope.rule.senderInfo = {};
		$scope.rule.senderInfo.sendAttachment = "Y";
		$scope.rule.senderInfo.notifyOnSuccess = "Y";
		$scope.rule.senderInfo.notifyOnFailure = "Y";
	}

	$scope.selectVersion = function () {
		$scope.allSource = null;
		$scope.selectSourceType = null;
		$scope.myform1.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform3.$dirty = false;
		$scope.isEditInprogess = true;
		$scope.isEditVeiwError = false;
		Rule2Service.getOneByUuidAndVersion($scope.ruleVersion.defaultVersion.uuid, $scope.ruleVersion.defaultVersion.version, CF_META_TYPES.rule2)
			.then(function (response) { onSuccessResult(response.data) }, function (response) { onError(response.data) });
		var onSuccessResult = function (response) {
			$scope.isEditInprogess = false;
			$scope.rule = response.rule;
			$scope.selectSourceType = response.rule.sourceInfo.ref.type
			var defaultversion = {};
			defaultversion.version = response.rule.version;
			defaultversion.uuid = response.rule.uuid;
			$scope.ruleVersion.defaultVersion = defaultversion;

			$scope.tags = response.tags;

			$scope.getParamByApp();
			$scope.getAllLatest($scope.selectSourceType, response.rule.sourceInfo, false);
			$scope.getFunctionByCriteria();

			$scope.filterTableArray = response.filterInfo;
			$scope.criteriaTableArray = response.criteriaInfo;
			$scope.selectEntityId = {};
			$scope.selectEntityId.uuid = $scope.rule.entityId.ref.uuid;
			$scope.selectEntityId.type = $scope.rule.entityId.ref.type;
			$scope.selectEntityId.attributeId = $scope.rule.entityId.attrId;


		}//End onSuccessResult
		var onError = function () {
			$scope.isEditInprogess = false;
			$scope.isEditVeiwError = true;
		};
	}

	$scope.SearchAttribute = function (index, type, propertyType) {
		$scope.allSearchType = {};
		$scope.searchAttr = {};
		setTimeout(function () {
			$scope.allSearchType = null;
			$scope.selectAttr = $scope.filterTableArray[index][propertyType]
		}, 10)

		$scope.searchAttr.type = type;
		$scope.searchAttr.sourceType = "filter";
		$scope.searchAttr.propertyType = propertyType;
		$scope.searchAttr.index = index;
		Rule2Service.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		$scope.searchAttrIndex = index;
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = [];
			$scope.allSearchType = response;

			var temp;
			if ($scope.selectSourceType == "dataset") {
				temp = $scope.allSearchType.options.filter(function (el) {
					return el.uuid !== $scope.allSource.defaultoption.uuid;
				});
				$scope.allSearchType.options = temp;
				$scope.allSearchType.defaultoption = temp[0]
			}

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
			Rule2Service.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
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
		Rule2Service.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SearchCFAttribute = function (parentIndex, index, type, propertyType) {
		$scope.searchAttr = {};
		$scope.allSearchType = {};
		setTimeout(function () {
			$scope.allSearchType = null;
			$scope.selectAttr = $scope.criteriaTableArray[parentIndex].filterInfo[index][propertyType]
		}, 10);

		$scope.searchAttr.type = type;
		$scope.searchAttr.sourceType = "CF";
		$scope.searchAttr.propertyType = propertyType;
		$scope.searchAttr.index = index;
		$scope.searchAttr.parentIndex = parentIndex;
		Rule2Service.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		$scope.searchAttrIndex = index;
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = []
			$scope.allSearchType = response;
			var temp;
			if ($scope.selectSourceType == "dataset") {
				temp = $scope.allSearchType.options.filter(function (el) {
					return el.uuid !== $scope.allSource.defaultoption.uuid;
				});
				$scope.allSearchType.options = temp;
				$scope.allSearchType.defaultoption = temp[0]
			}

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
			Rule2Service.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
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
	$scope.SubmitSearchAttr = function () {
		if ($scope.searchAttr.sourceType == "filter") {
			$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
		} else {
			$scope.criteriaTableArray[$scope.searchAttr.parentIndex].filterInfo[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
		}
		$scope.selectAttr = null;
		$('#searchAttr').modal('hide')
	}

	$scope.onCloseSearchAttr = function () {
		$scope.selectAttr = null;

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
		$scope.filterTableArray[index].isRhsNA = false;
		if ($scope.filterTableArray[index].operator == 'BETWEEN') {
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else if (['IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, []);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist', 'string', 'integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}

		else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['dataset']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else if (['IS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].isRhsNA = true;
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist', 'integer']);
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
		if (!$scope.selectedAllFitlerRow) {
			$scope.selectedAllFitlerRow = true;
		}
		else {
			$scope.selectedAllFitlerRow = false;
		}
		angular.forEach($scope.filterTableArray, function (filter) {
			filter.selected = $scope.selectedAllFitlerRow;
		});
	}

	function returnRshType() {
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
		$scope.myform2.$dirty = true;
		var filertable = {};
		filertable.islhsDatapod = true;
		filertable.islhsFormula = false;
		filertable.islhsSimple = false;
		filertable.isrhsDatapod = true;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = false;
		filertable.lhsFilter = $scope.lhsdatapodattributefilter[0];
		filertable.lhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];
		filertable.rhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];

		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[2]
		filertable.rhstype = returnRshType()[2];
		filertable.rhsTypes = returnRshType();

		filertable.rhsTypes = $scope.disableRhsType(filertable.rhsTypes, ['dataset']);
		filertable.rhsvalue;
		filertable.lhsvalue;
		$scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);
	}

	$scope.removeRowFitler = function () {
		var newDataList = [];
		$scope.checkAll = false;
		angular.forEach($scope.filterTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.fitlerAttrTableSelectedItem = [];
		});

		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.filterTableArray = newDataList;
	}

	$scope.fitlerAttrTableSelectedItem = [];
	$scope.onChangeFilterAttRow = function (index, status) {
		if (status == true) {
			$scope.fitlerAttrTableSelectedItem.push(index);
		}
		else {
			let tempIndex = $scope.fitlerAttrTableSelectedItem.indexOf(index);

			if (tempIndex != -1) {
				$scope.fitlerAttrTableSelectedItem.splice(tempIndex, 1);

			}
		}
	}

	$scope.autoMove = function (index, type) {
		if (type == "mapAttr") {
		}
		else {
			var tempAtrr = $scope.filterTableArray[$scope.fitlerAttrTableSelectedItem[0]];
			$scope.filterTableArray.splice($scope.fitlerAttrTableSelectedItem[0], 1);
			$scope.filterTableArray.splice(index, 0, tempAtrr);
			$scope.fitlerAttrTableSelectedItem = [];
			$scope.filterTableArray[index].selected = false;
			$scope.filterTableArray[0].logicalOperator = "";
			if ($scope.filterTableArray[index].logicalOperator == "" && index != 0) {
				$scope.filterTableArray[index].logicalOperator = $scope.logicalOperator[0];
			} else if ($scope.filterTableArray[index].logicalOperator == "" && index == 0) {
				$scope.filterTableArray[index + 1].logicalOperator = $scope.logicalOperator[0];
			}
		}
	}

	$scope.autoMoveTo = function (index, type) {
		if (type == "mapAttr") {
		}
		else {
			if (index <= $scope.filterTableArray.length) {
				$scope.autoMove(index - 1, 'filterAttr');
				$scope.moveTo = null;
				$(".actions").removeClass("open");
			}
		}
	}
	// $scope.onAttrFilterRowDown = function (index) {
	// 	var rowTempIndex = $scope.filterTableArray[index];
	// 	var rowTempIndexPlus = $scope.filterTableArray[index + 1];
	// 	$scope.filterTableArray[index] = rowTempIndexPlus;
	// 	$scope.filterTableArray[index + 1] = rowTempIndex;
	// 	if (index == 0) {
	// 		$scope.filterTableArray[index + 1].logicalOperator = $scope.filterTableArray[index].logicalOperator;
	// 		$scope.filterTableArray[index].logicalOperator = ""
	// 	}
	// }

	// $scope.onAttrFilterRowUp = function (index) {
	// 	var rowTempIndex = $scope.filterTableArray[index];
	// 	var rowTempIndexMines = $scope.filterTableArray[index - 1];
	// 	$scope.filterTableArray[index] = rowTempIndexMines;
	// 	$scope.filterTableArray[index - 1] = rowTempIndex;
	// 	if (index == 1) {
	// 		$scope.filterTableArray[index].logicalOperator = $scope.filterTableArray[index - 1].logicalOperator;
	// 		$scope.filterTableArray[index - 1].logicalOperator = ""
	// 	}
	// }

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
			if (typeof $stateParams.id == "undefined") {
				$scope.getFormulaByType();
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

			if (typeof $stateParams.id == "undefined") {
				$scope.getFormulaByType();
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
			$scope.getFunctionByCriteria();

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
			if (typeof $stateParams.id == "undefined") {
				$scope.getParamByApp();
			}
			
		}

	}

	$scope.onCriteriaDrop = function (index) {
		var tempParentIndex = $scope.criteriaTableArray[index.sourceIndex].filterInfo[0].parentIndex;
		if ($scope.criteriaTableArray[index.sourceIndex].filterInfo && $scope.criteriaTableArray[index.sourceIndex].filterInfo.length) {
			for (var i = 0; i < $scope.criteriaTableArray[index.sourceIndex].filterInfo.length; i++) {
				$scope.criteriaTableArray[index.sourceIndex].filterInfo[i].parentIndex = $scope.criteriaTableArray[index.targetIndex].filterInfo[0].parentIndex;
			}
		}
		if ($scope.criteriaTableArray[index.targetIndex].filterInfo[0].parentIndex && $scope.criteriaTableArray[index.targetIndex].filterInfo[0].parentIndex.length) {
			for (var j = 0; j < $scope.criteriaTableArray[index.targetIndex].filterInfo.length; j++) {
				$scope.criteriaTableArray[index.targetIndex].filterInfo[j].parentIndex = tempParentIndex;
			}
		}
		$scope.myform3.$dirty = true;

	}

	$scope.onCFilterDrop = function (index) {
		if (index.targetIndex == 0) {
			$scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.sourceIndex].logicalOperator = $scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.targetIndex].logicalOperator;
			$scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.targetIndex].logicalOperator = ""
		}
		if (index.sourceIndex == 0) {
			$scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.targetIndex].logicalOperator = $scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.sourceIndex].logicalOperator;
			$scope.criteriaTableArray[index.sourceItem.parentIndex].filterInfo[index.sourceIndex].logicalOperator = ""
		}
		$scope.myform3.$dirty = true;
	}

	$scope.seletAllCriteriaRow = function () {
		angular.forEach($scope.criteriaTableArray, function (filter) {
			filter.selected = $scope.selectCRAll;
		});
	}

	$scope.removeRowCriteria = function () {
		var newDataList = [];
		$scope.selectCRAll = false;
		angular.forEach($scope.criteriaTableArray, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.fitlerAttrTableSelectedItem = [];
		});

		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.criteriaTableArray = newDataList;
	}

	$scope.addRowCriteria = function () {
		if ($scope.criteriaTableArray == null) {
			$scope.criteriaTableArray = [];
		}
		$scope.expanded = true;
	    $scope.myform3.$dirty=true;
		var len = $scope.criteriaTableArray.length;
		var criteriaInfo = {};
		criteriaInfo.criteriaId = len;
		var tempLen = len + 1;
		criteriaInfo.criteriaName = "criteria" + tempLen;
		criteriaInfo.score = 1;
		criteriaInfo.criteriaWeight =1;
		criteriaInfo.activeFlag = true;
		//	criteriaInfo.expanded=true;
		$scope.criteriaTableArray.splice(len, 0, criteriaInfo);
		$scope.criteriaTableArray[len].filterInfo = [];
		$scope.addSubRowCriteria(len);
	}


	$scope.seletAllCFRow = function (index) {
		if (!$scope.selectCFRAll) {
			$scope.selectCFRAll = true;
		}
		else {
			$scope.selectCFRAll = false;
		}
		angular.forEach($scope.criteriaTableArray[index].filterInfo, function (filter) {
			filter.selected = $scope.selectCFRAll;
		});
	}

	$scope.removeSubRowCriteria = function (index) {
		var newDataList = [];
		$scope.selectCFRAll = false;
		angular.forEach($scope.criteriaTableArray[index].filterInfo, function (selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}
			$scope.fitlerAttrTableSelectedItem = [];
		});

		if (newDataList.length > 0) {
			newDataList[0].logicalOperator = "";
		}
		$scope.criteriaTableArray[index].filterInfo = newDataList;
	}

	$scope.addSubRowCriteria = function (index) {
		if ($scope.criteriaTableArray[index].filterInfo == null) {
			$scope.criteriaTableArray[index].filterInfo = [];
		}
		var filertable = {};
		filertable.islhsDatapod = true;
		filertable.islhsFormula = false;
		filertable.islhsSimple = false;
		filertable.isrhsDatapod = true;
		filertable.isrhsFormula = false;
		filertable.isrhsSimple = false;
		if ($scope.lhsdatapodattributefilter) {
			filertable.lhsFilter = $scope.lhsdatapodattributefilter[0];
			filertable.lhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];
			filertable.rhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];
		}
		filertable.logicalOperator = $scope.criteriaTableArray[index].filterInfo.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[2]
		filertable.rhstype = returnRshType()[2];
		filertable.rhsTypes = returnRshType();
		filertable.rhsTypes = $scope.disableRhsType(filertable.rhsTypes, ['dataset']);
		filertable.rhsvalue;
		filertable.lhsvalue;
		$scope.criteriaTableArray[index].filterInfo.splice($scope.criteriaTableArray[index].filterInfo.length, 0, filertable);

	}
	$scope.onChangeCFOperator = function (parentIndex, index) {
		$scope.criteriaTableArray[parentIndex].filterInfo[index].isRhsNA = false;
		if ($scope.criteriaTableArray[parentIndex].filterInfo[index].operator == 'BETWEEN') {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[1];
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}
		else if (['IN', 'NOT IN'].indexOf($scope.criteriaTableArray[parentIndex].filterInfo[index].operator) != -1) {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, []);
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[4];
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}
		else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.criteriaTableArray[parentIndex].filterInfo[index].operator) != -1) {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist', 'string', 'integer']);
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[4];
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}

		else if (['<', '>', "<=", '>='].indexOf($scope.criteriaTableArray[parentIndex].filterInfo[index].operator) != -1) {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, ['dataset']);
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[1];
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}
		else if (['IS'].indexOf($scope.criteriaTableArray[parentIndex].filterInfo[index].operator) != -1) {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isRhsNA = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist', 'integer']);
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[0];
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}
		else {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes = $scope.disableRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes, ['dataset']);
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype = $scope.criteriaTableArray[parentIndex].filterInfo[index].rhsTypes[0];
			$scope.selectCFRhsType($scope.criteriaTableArray[parentIndex].filterInfo[index].rhstype.text, parentIndex, index);
		}
	}
	$scope.selectCFLhsType = function (type, parentIndex, index) {
		if (type == "string") {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsSimple = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].lhsvalue;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsFormula = false;
		}
		else if (type == "datapod") {

			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsDatapod = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsFormula = false;
		}
		else if (type == "formula") {

			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsFormula = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].islhsDatapod = false;
			if (typeof $stateParams.id == "undefined") {
				$scope.getFormulaByType();
			}
		}
	}


	$scope.selectCFRhsType = function (type, parentIndex, index) {
		if (type == "string") {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].rhsvalue;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = false;

		}
		else if (type == "datapod") {

			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = false;
		}

		else if (type == "formula") {

			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = false;
			if (typeof $stateParams.id == "undefined") {
				$scope.getFormulaByType();
			}

		}
		else if (type == "function") {

			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = true;
			$scope.getFunctionByCriteria();

		}
		else if (type == "dataset") {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = false;

		}
		else if (type == "paramlist") {
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFormula = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsSimple = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDatapod = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsDataset = false;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsParamlist = true;
			$scope.criteriaTableArray[parentIndex].filterInfo[index].isrhsFunction = false;
			if (typeof $stateParams.id == "undefined") {
				$scope.getParamByApp();
		    }
		}

	}

	$scope.expandAll = function (expanded) {
		// $scope is required here, hence the injection above, even though we're using "controller as" syntax
		$scope.$broadcast('onExpandAll', { expanded: expanded });
	};
	$scope.submit = function () {
		var upd_tag = "N"
		$scope.isshowmodel = true;
		$scope.dataLoading = true;
		$scope.iSSubmitEnable = false;
		$scope.myform1.$dirty = false;
		$scope.myform2.$dirty = false;
		$scope.myform3.$dirty = false;
		$scope.myform4.$dirty = false;

		var ruleJson = {}
		ruleJson.uuid = $scope.rule.uuid
		ruleJson.name = $scope.rule.name;
		ruleJson.desc = $scope.rule.desc
		ruleJson.active = $scope.rule.active;
		ruleJson.locked = $scope.rule.locked;
		ruleJson.published = $scope.rule.published;
		ruleJson.publicFlag = $scope.rule.publicFlag;
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

		ruleJson.tags = tagArray;
		var sourceInfo = {};
		var ref = {};
		ref.type = $scope.selectSourceType
		ref.uuid = $scope.allSource.defaultoption.uuid
		sourceInfo.ref = ref;
		ruleJson.sourceInfo = sourceInfo;
		ruleJson.entityType = $scope.rule.entityType;

		if ($scope.allparamlist && $scope.allparamlist.defaultoption != null) {
			var paramlist = {};
			var ref = {};
			ref.type = "paramlist";
			ref.uuid = $scope.allparamlist.defaultoption.uuid;
			paramlist.ref = ref;
			ruleJson.paramList = paramlist;
		}
		else {
			ruleJson.paramList = null;
		}
		var entityId = {}
		var entityIdRef = {};
		entityIdRef.uuid = $scope.allSource.defaultoption.uuid;
		entityIdRef.type = $scope.selectSourceType;
		entityId.ref = entityIdRef;
		entityId.attrId = $scope.selectEntityId.attributeId;
		ruleJson.entityId = entityId;
		//filterInfo
		var filterInfoArray = [];
		if ($scope.filterTableArray && $scope.filterTableArray.length > 0) {
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

					lhsref.type = $scope.filterTableArray[i].lhsdatapodAttribute.type;
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

					rhsref.type = $scope.filterTableArray[i].rhsdatapodAttribute.type;
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

			}//End FilterInfo

			ruleJson.filterInfo = filterInfoArray;
		}
		else {
			ruleJson.filterInfo = null;
		}

		var criteriaInfoArray = [];
		if ($scope.criteriaTableArray && $scope.criteriaTableArray.length > 0) {
			for (var i = 0; i < $scope.criteriaTableArray.length; i++) {
				var criteriaInfo = {};
				criteriaInfo.criteriaId = i;
				criteriaInfo.criteriaName = $scope.criteriaTableArray[i].criteriaName;
				criteriaInfo.activeFlag = $scope.criteriaTableArray[i].activeFlag == true ? "Y" : "N";
				criteriaInfo.criteriaWeight = $scope.criteriaTableArray[i].criteriaWeight;
				criteriaInfo.score = $scope.criteriaTableArray[i].score;

				var filterInfoArray = [];
				if ($scope.criteriaTableArray[i].filterInfo && $scope.criteriaTableArray[i].filterInfo.length > 0) {
					for (var j = 0; j < $scope.criteriaTableArray[i].filterInfo.length; j++) {
						var filterInfo = {};
						var operand = []
						var lhsoperand = {}
						var lhsref = {}
						var rhsoperand = {}
						var rhsref = {};
						filterInfo.display_seq = j;
						if (typeof $scope.criteriaTableArray[i].filterInfo[j].logicalOperator == "undefined") {
							filterInfo.logicalOperator = ""
						}
						else {
							filterInfo.logicalOperator = $scope.criteriaTableArray[i].filterInfo[j].logicalOperator
						}
						filterInfo.operator = $scope.criteriaTableArray[i].filterInfo[j].operator;
						if ($scope.criteriaTableArray[i].filterInfo[j].lhstype.text == "string") {

							lhsref.type = "simple";
							lhsoperand.ref = lhsref;
							lhsoperand.attributeType = $scope.criteriaTableArray[i].filterInfo[j].lhstype.caption;
							lhsoperand.value = $scope.criteriaTableArray[i].filterInfo[j].lhsvalue;

						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].lhstype.text == "datapod") {

							lhsref.type = $scope.criteriaTableArray[i].filterInfo[j].lhsdatapodAttribute.type;
							lhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].lhsdatapodAttribute.uuid;
							lhsoperand.ref = lhsref;
							lhsoperand.attributeId = $scope.criteriaTableArray[i].filterInfo[j].lhsdatapodAttribute.attributeId;
						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].lhstype.text == "formula") {

							lhsref.type = "formula";
							lhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].lhsformula.uuid;
							lhsoperand.ref = lhsref;
						}
						operand[0] = lhsoperand;

						if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "string") {

							rhsref.type = "simple";
							rhsoperand.ref = rhsref;
							rhsoperand.attributeType = $scope.criteriaTableArray[i].filterInfo[j].rhstype.caption;
							rhsoperand.value = $scope.criteriaTableArray[i].filterInfo[j].rhsvalue;
							if ($scope.criteriaTableArray[i].filterInfo[j].operator == 'BETWEEN') {
								rhsoperand.value = $scope.criteriaTableArray[i].filterInfo[j].rhsvalue1 + "and" + $scope.criteriaTableArray[i].filterInfo[j].rhsvalue2;
							}
						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "datapod") {

							rhsref.type = $scope.criteriaTableArray[i].filterInfo[j].rhsdatapodAttribute.type;
							rhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].rhsdatapodAttribute.uuid;
							rhsoperand.ref = rhsref;
							rhsoperand.attributeId = $scope.criteriaTableArray[i].filterInfo[j].rhsdatapodAttribute.attributeId;

						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "formula") {

							rhsref.type = "formula";
							rhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].rhsformula.uuid;
							rhsoperand.ref = rhsref;

						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "function") {
							rhsref.type = "function";
							rhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].rhsfunction.uuid;
							rhsoperand.ref = rhsref;
						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "dataset") {
							rhsref.type = "dataset";
							rhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].rhsdataset.uuid;
							rhsoperand.ref = rhsref;
							rhsoperand.attributeId = $scope.criteriaTableArray[i].filterInfo[j].rhsdataset.attributeId;
						}
						else if ($scope.criteriaTableArray[i].filterInfo[j].rhstype.text == "paramlist") {

							rhsref.type = "paramlist";
							rhsref.uuid = $scope.criteriaTableArray[i].filterInfo[j].rhsparamlist.uuid;
							rhsoperand.ref = rhsref;
							rhsoperand.attributeId = $scope.criteriaTableArray[i].filterInfo[j].rhsparamlist.attributeId;
						}
						operand[1] = rhsoperand;
						filterInfo.operand = operand;
						filterInfoArray[j] = filterInfo;
						criteriaInfo.criteriaFilter = filterInfoArray;
					}
				}
				criteriaInfoArray[i] = criteriaInfo;
			}
			ruleJson.criteriaInfo = criteriaInfoArray;
		}

		console.log(JSON.stringify(ruleJson))

		Rule2Service.submit(ruleJson, 'rule2', upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			if ($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption != null) {
				$scope.ruleId = response.data;
				$scope.showParamlistPopup();
			} //End if
			else if ($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption == null) {
				Rule2Service.getOneById(response.data, "rule2").then(function (response) {
					onSuccessGetOneById(response.data)
				});
				var onSuccessGetOneById = function (result) {
					$scope.ruleExecute(result.data);
				}
			}
			else {
				$scope.dataLoading = false;
				notify.type = 'success',
					notify.title = 'Success',
					notify.content = 'Rule Saved Successfully'
				$scope.$emit('notify', notify);
				$scope.close();
			}
		}
		var onError = function (response) {
			notify.type = 'error',
				notify.title = 'Error',
				notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
			$scope.iSSubmitEnable = false;

		}

		return false;
	}

	$scope.close = function () {
		var hidemode = "yes";
		if (hidemode == 'yes') {
			setTimeout(function () { $state.go('viewrule2'); }, 2000);
		}
	}

	$scope.changeCheckboxExecution = function () {
		$scope.allparamset = null;
		$scope.allParamList = null;
		$scope.isParamLsitTable = false;
		$scope.selectParamList = null;
		$scope.paramTypes = null;
		$scope.selectParamType = null;
	}

	$scope.ruleExecute = function (modeldetail) {
		if ($scope.selectParamType == "paramlist") {
			if ($scope.paramlistdata) {
				var execParams = {};
				var paramListInfo = [];
				var paramInfo = {};
				var paramInfoRef = {};
				paramInfoRef.uuid = $scope.paramlistdata.uuid;
				paramInfoRef.type = "paramlist";
				paramInfo.ref = paramInfoRef;
				paramListInfo[0] = paramInfo;
				// for (var i = 0; i < $scope.selectParamList.paramInfo.length; i++) {
				// 	var paramListObj = {};
				// 	var ref = {};
				// 	ref.uuid = $scope.paramlistdata.uuid;
				// 	ref.type = "paramlist";
				// 	paramListObj.ref = ref;
				// 	paramListObj.paramId = $scope.selectParamList.paramInfo[i].paramId;
				// 	paramListObj.paramName = $scope.selectParamList.paramInfo[i].paramName;
				// 	paramListObj.paramType = $scope.selectParamList.paramInfo[i].paramType;
				// 	paramListObj.paramValue = {};
				// 	var refParamValue = {};
				// 	refParamValue.type = $scope.selectParamList.paramInfo[i].paramValueType;
				// 	paramListObj.paramValue.ref = refParamValue;
				// 	paramListObj.paramValue.value = $scope.selectParamList.paramInfo[i].paramValue.replace(/["']/g, "");
				// 	paramListInfo[i] = paramListObj;

				// }
				execParams.paramListInfo = paramListInfo;
			} else {
				execParams = null;
			}
			$scope.paramlistdata = null;
			$scope.selectParamType = null;
		}
		else {
			$scope.newDataList = [];
			$scope.selectallattribute = false;
			angular.forEach($scope.paramtable, function (selected) {
				if (selected.selected) {
					$scope.newDataList.push(selected);
				}
			});
			var paramInfoArray = [];
			if ($scope.newDataList.length > 0) {
				var execParams = {}
				var ref = {}
				ref.uuid = $scope.paramsetdata.uuid;
				ref.version = $scope.paramsetdata.version;
				for (var i = 0; i < $scope.newDataList.length; i++) {
					var paraminfo = {};
					paraminfo.paramSetId = $scope.newDataList[i].paramSetId;
					paraminfo.ref = ref;
					paramInfoArray[i] = paraminfo;
				}
			}
			if (paramInfoArray.length > 0) {
				execParams.paramInfo = paramInfoArray;
			} else {
				execParams = null
			}
		}
		Rule2Service.executeRule(modeldetail.uuid, modeldetail.version, execParams)
			.then(function (response) { onSuccessGetReportExecute(response.data) }, function (response) { onError(response.data) });
		var onSuccessGetReportExecute = function (response) {
			$scope.dataLoading = false;
			$scope.saveMessage = "Rule Saved and Submitted Successfully";
			notify.type = 'success',
				notify.title = 'Success',
				notify.content = $scope.saveMessage
			$scope.$emit('notify', notify);
			$scope.close();
		}
		var onError = function (response) {
			$scope.dataLoading = false;
			$scope.iSSubmitEnable = false;
			$scope.close();
		}
	}

	$scope.showParamlistPopup = function () {
		setTimeout(function () { $scope.paramTypes = ["paramlist", "paramset"]; }, 1);
		if ($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption != null) {
			$('#responsive').modal({
				backdrop: 'static',
				keyboard: false
			});
		} else {
			$scope.isShowExecutionparam = false;
			$scope.allparamset = null;
			$scope.dataLoading = false;
		}
	}

	$scope.closeParalistPopup = function () {
		$scope.dataLoading = false;
		$scope.checkboxModelexecution = "NO";
		$scope.isSubmitDisabled = false;
		$('#responsive').modal('hide');
	}

	$scope.executeWithExecParams = function () {
		$('#responsive').modal('hide');
		Rule2Service.getOneById($scope.ruleId, "rule2").then(function (response) {
			onSuccessGetOneById(response.data)
		});
		var onSuccessGetOneById = function (result) {
			$scope.ruleExecute(result.data);
		}
	}

	$scope.getParamSetByParamList = function () {
		Rule2Service.getParamSetByParamList($scope.allparamlist.defaultoption.uuid, "").then(function (response) { onSuccessGetParamSetByParmLsit(response.data) });
		var onSuccessGetParamSetByParmLsit = function (response) {
			$scope.allparamset = response
			$scope.isShowExecutionparam = true;
		}
	}

	$scope.getParamListChilds = function () {
		CommonService.getParamListChilds($scope.allparamlist.defaultoption.uuid, "", "paramlist").then(function (response) { onSuccessGetParamListChilds(response.data) });
		var onSuccessGetParamListChilds = function (response) {
			var defaultoption = {};
			defaultoption.uuid = $scope.allparamlist.defaultoption.uuid;
			defaultoption.name = $scope.allparamlist.defaultoption.name;
			if (response.length > 0) {
				$scope.allParamListResult = response;
				$scope.allParamListResult.splice(0, 0, defaultoption);
			} else {
				$scope.allParamListResult = [];
				$scope.allParamListResult[0] = defaultoption;
			}
		}
	}

	$scope.onChangeParamType = function () {
		$scope.allparamset = null;
		$scope.allParamList = null;
		$scope.isParamLsitTable = false;
		$scope.selectParamList = null;
		if ($scope.selectParamType == "paramlist") {
			$scope.paramlistdata = null;
			$scope.getParamListChilds();
		}
		else if ($scope.selectParamType == "paramset") {
			$scope.getParamSetByParamList();
		}
	}

	$scope.onChangeParamList = function () {
		$scope.isParamLsitTable = false;
		debugger
		CommonService.getParamByParamList($scope.paramlistdata.uuid, "paramlist").then(function (response) { onSuccesGetParamListByTrain(response.data) });
		var onSuccesGetParamListByTrain = function (response) {
			$scope.isParamLsitTable = true;
			$scope.selectParamList = response;
			var paramArray = [];
			for (var i = 0; i < response.length; i++) {
				var paramInfo = {}
				paramInfo.paramId = response[i].paramId;
				paramInfo.paramName = response[i].paramName;
				paramInfo.paramType = response[i].paramType.toLowerCase();
				if (response[i].paramValue != null && response[i].paramValue.ref.type == "simple") {
					paramInfo.paramValue = response[i].paramValue.value.replace(/["']/g, "");;
					paramInfo.paramValueType = "simple"
				} else if (response[i].paramValue != null) {
					var paramValue = {};
					paramValue.uuid = response[i].paramValue.ref.uuid;
					paramValue.type = response[i].paramValue.ref.type;
					paramInfo.paramValue = paramValue;
					paramInfo.paramValueType = response[i].paramValue.ref.type;
				} else {

				}
				paramArray[i] = paramInfo;
			}
			$scope.selectParamList.paramInfo = paramArray;
		}
	}


});

RuleModule.controller('ResultRule2Controller', function ($http, $log, dagMetaDataService, $filter, $state, $cookieStore, $stateParams, $location, $rootScope, $scope, Rule2Service, NgTableParams, uuid2, uiGridConstants, CommonService, privilegeSvc, CF_DOWNLOAD) {
	$scope.select = $stateParams.type;
	$scope.type = {
		text: $scope.select == 'rulegroupexec' ? 'rulegroup' : 'rule'
	};

	$scope.setType = function () {
		$scope.select = $stateParams.type;
		$scope.type = {
			text: $scope.select == 'rulegroupexec' ? 'rulegroup' : 'rule'
		};
		if ($scope.type == "rule") {
			$scope.obj = {};
			$scope.obj.uuid = $stateParams.id;
			$scope.obj.version = $stateParams.version;
		} else {

		}
	}
	$scope.pagination = {
		currentPage: 1,
		pageSize: 100,
		pageSizes: [10, 25, 50, 75, 100],
		maxSize: 5,
		to: null,
		from: null,
		totalItems: null,
	}
	$scope.sortdetail = [];
	$scope.isRuleGroupExec = false;
	$scope.showprogress = false;
	$scope.isRuleExec = false;
	$scope.isRuleResult = false;
	$scope.isRuleTitle = false;
	$scope.isRuleGroupTitle = false;
	$scope.zoomSize = 7;
	$scope.isGraphRuleGroupExec = false;
	$scope.isdisabled = true
	$scope.isD3RuleEexecGraphShow = false;
	$scope.isD3RGEexecGraphShow = false;
	$scope.download = {};
	$scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
	$scope.download.formates = CF_DOWNLOAD.formate;
	$scope.download.selectFormate = CF_DOWNLOAD.formate[0];
	$scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
	$scope.download.limit_to = CF_DOWNLOAD.limit_to;
	$scope.download.resultType = "summary";
	$scope.filteredRowsDetail = [];
	$scope.filteredRowsSummary = [];
	var notify = {
		type: 'success',
		title: 'Success',
		content: '',
		timeout: 3000 //time in ms
	};

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

	$scope.gridOptionsDetail = {
		rowHeight: 40,
		useExternalPagination: true,
		exporterMenuPdf: false,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
		enableSorting: true,
		useExternalSorting: true,
		enableFiltering: false,
		enableRowSelection: true,
		enableSelectAll: true,
		enableGridMenu: true,
		fastWatch: true,
		columnDefs: [],
		onRegisterApi: function (gridApi) {
			$scope.gridApiResultDetail = gridApi;
			$scope.filteredRowsDetail = $scope.gridApiResultDetail.core.getVisibleRows($scope.gridApiResultDetail.grid);
			$scope.gridApiResultDetail.core.on.sortChanged($scope, function (grid, sortColumns) {
				if (sortColumns.length > 0) {
					$scope.searchRequestId(sortColumns);
				}
			});
		}
	};


	$scope.gridOptionsSummary = {
		rowHeight: 40,
		useExternalPagination: true,
		exporterMenuPdf: false,
		exporterPdfOrientation: 'landscape',
		exporterPdfPageSize: 'A4',
		exporterPdfDefaultStyle: { fontSize: 9 },
		exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
		enableSorting: true,
		useExternalSorting: true,
		enableFiltering: false,
		enableRowSelection: true,
		enableSelectAll: true,
		enableGridMenu: true,
		fastWatch: true,
		columnDefs: [],
		onRegisterApi: function (gridApi) {
			$scope.gridApiResultSummary = gridApi;
			$scope.filteredRowsSummary = $scope.gridApiResultSummary.core.getVisibleRows($scope.gridApiResultSummary.grid);
			$scope.gridApiResultSummary.core.on.sortChanged($scope, function (grid, sortColumns) {
				if (sortColumns.length > 0) {
					$scope.searchRequestId(sortColumns);
				}
			});
		}
	};

	$scope.getGridStyleDetail = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRowsDetail && $scope.filteredRowsDetail.length > 0) {
			style['height'] = (($scope.filteredRowsDetail.length < 10 ? $scope.filteredRowsDetail.length * 40 : 400) + 80) + 'px';
		}
		else {
			style['height'] = "100px"
		}
		return style;
	}

	$scope.getGridStyleSummary = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
		}
		if ($scope.filteredRowsSummary && $scope.filteredRowsSummary.length > 0) {
			style['height'] = (($scope.filteredRowsSummary.length < 10 ? $scope.filteredRowsSummary.length * 40 : 400) + 80) + 'px';
		}
		else {
			style['height'] = "100px"
		}
		return style;
	}


	$scope.go = function (index) {
		if ($scope.type.text == "rule" || $scope.selectResult == "rule") {
			$scope.setType();
			if (index == 1) {
				$scope.download.resultType = "detail";
				$scope.getRuleExec({
					uuid: $scope.obj.id,
					version: $scope.obj.version
				}, "detail");
			}
			else {
				$scope.download.resultType = "summary";
				$scope.getRuleExec({
					uuid: $scope.obj.id,
					version: $scope.obj.version
				}, "summary");
			}
		}
	}

	$scope.onClickRuleResult = function () {
		$scope.isRuleExec = true;
		$scope.isRuleResult = false;
		$scope.isD3RuleEexecGraphShow = false;
		if ($scope.type.text == "rulegroup") {
			$scope.isGraphRuleExec = false;
			$scope.execDetail = $scope.rulegroupdatail;
			$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
		} else {
			$scope.isRuleTitle = false;
			$scope.isRuleSelect = true;
		}
	}

	$scope.onClickRuleExec = function () {
		$scope.refreshSearchResults();
		$scope.isRuleSelect = true;
		if ($scope.type.text == "rulegroup") {
			$scope.isRuleGroupExec = true;
			$scope.isRuleExec = false;
			$scope.isRuleGroupTitle = false;
		} else {
			$scope.isRulExec = false;
			$scope.ruleData = "";
		}
	}

	$scope.$watch("zoomSize", function (newData, oldData) {
		$scope.$broadcast('zoomChange', newData);
	});


	window.navigateTo = function (url) {
		var state = JSON.parse(url);
		$rootScope.previousState = {
			name: $state.current.name,
			params: $state.params
		};
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
		$scope.selectGraphRuleExec = params.name
		$scope.isGraphRuleExec = true;
		$scope.isRuleGroupTitle = true;
		$scope.isRuleTitle = false;
		$scope.obj = {};
		$scope.obj.id = params.id;
		$scope.obj.version = params.version;
		$scope.getRuleExec({
			uuid: params.id,
			version: params.version
		}, "summary");
	}

	$scope.toggleZoom = function () {
		$scope.showZoom = !$scope.showZoom;
	}

	$scope.refreshRuleGroupExecFunction = function () {
		$scope.isD3RGEexecGraphShow = false;
		$scope.getRuleGroupExec($scope.rulegroupdatail)
	}

	$scope.rGExecshowGraph = function () {
		$scope.isGraphRuleGroupExec = false;
		$scope.isD3RGEexecGraphShow = true;
	}

	$scope.getRuleGroupExec = function (data) {
		$scope.setType();
		if ($scope.type.text == 'rule') {
			$scope.obj = {};
			$scope.obj.id = data.uuid;
			$scope.obj.version = data.version
			$scope.getRuleExec(data, "summary");
			return;
		}
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
		$scope.rulegroupdatail = data;
		$scope.isRuleGroupExec = false;
		$scope.isRuleSelect = false;
		$scope.isRuleExec = true;
		var uuid = data.uuid;
		var version = data.version;
		var name = data.name;
		$scope.rGExecUuid = uuid;
		$scope.rGExecVersion = version;
		$scope.isRuleGroupTitle = true;
		$scope.ruleGrpupName = data.name;
		if ($scope.type.text == 'rulegroup') {
			$scope.isGraphRuleGroupExec = true;
		} else {
			$scope.isGraphRuleGroupExec = false;
		}
		var params = {
			"id": uuid,
			"name": name,
			"elementType": "rulegroup",
			"version": version,
			"type": "rulegroup",
			"typeLabel": "RuleGroup",
			"url": "rule/getRuleExecByRGExec?",
			"ref": {
				"type": "rulegroupExec",
				"uuid": uuid,
				"version": version,
				"name": name
			}
		}
		setTimeout(function () {
			$scope.$broadcast('generateGroupGraph', params);
		}, 500);
	} //End getRuleGroupExec

	$scope.getExec = $scope.getRuleGroupExec;

	$scope.refreshResultFunction = function () {
		$scope.isD3RuleEexecGraphShow = false;
		if ($scope.activeTabIndex == 0) {
			$scope.getRuleExec($scope.ruleexecdetail, "summary");
		} else {
			$scope.getRuleExec($scope.ruleexecdetail, "detail");
		}
	}

	$scope.RuleExecshowGraph = function () {
		$scope.isDataError = false;
		$scope.isD3RuleEexecGraphShow = true;
	}

	$scope.getRuleExec = function (data, resultType) {
		$scope.execDetail = data;
		$scope.metaType = dagMetaDataService.elementDefs["rule"].execType;
		$scope.testgrid = false;
		$scope.ruleexecdetail = data;
		$scope.showprogress = true;
		$scope.isRuleResult = true;
		$scope.isRuleExec = false;
		$scope.isRuleSelect = false;
		$scope.isDataError = false;
		if ($scope.type.text == 'rulegroup') {
			$scope.isRuleGroupTitle = true;
		} else {
			$scope.isRuleTitle = true;
			$scope.isRuleGroupTitle = false;
		}
		var uuid = data.uuid;
		var version = data.version;
		$scope.ruleExecUuid = uuid;
		$scope.ruleExecVersion = version;
		$scope.ruleData = data.name;
		$scope.isData = false;
		Rule2Service.getNumRowsbyExec(uuid, version, "ruleexec")
			.then(function (response) { onSuccessGetNumRowsbyExec(response.data) });
		var onSuccessGetNumRowsbyExec = function (response) {
			$scope.pagination.totalItems = response.numRows;
			$scope.getResults(null, resultType);
		}
	} //End getRuleExec


	$scope.refreshDataDetail = function (searchtextDetail) {
		$scope.gridOptionsDetail.data = $filter('filter')($scope.originalDataDetail, searchtextDetail, undefined);
	};

	$scope.refreshDataSummary = function (searchtextSummary) {
		$scope.gridOptionsSummary.data = $filter('filter')($scope.originalDataSummary, searchtextSummary, undefined);
	};

	$scope.onPerPageChange = function (resultType) {
		$scope.pagination.currentPage = 1;
		var data = resultType == "summary" ? $scope.originalDataSummary : $scope.originalDataDetail;
		$scope.getResultsClientSide(data, resultType);
	}

	$scope.getColumnDetail = function (type, result, resultType) {
		CommonService.getColunmDetail(type, resultType).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			$scope.gridOptionsDetail.columnDefs = [];
			$scope.ColumnDetails = respone;
			$scope.isDataError = false;
			if ($scope.ColumnDetails && $scope.ColumnDetails.length > 0) {
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
					if (resultType == "detail") {

						$scope.gridOptionsDetail.columnDefs.push(attribute);
					} else {

						$scope.gridOptionsSummary.columnDefs.push(attribute);
					}
				}
			}
			else {
				angular.forEach(response.data[0], function (value, key) {
					var attribute = {};
					var hiveKey = ["rownum", "DatapodUUID", "DatapodVersion"]
					if (hiveKey.indexOf(key) != -1) {
						attribute.visible = false
					} else {
						attribute.visible = true
					}
					attribute.name = key
					attribute.displayName = key
					attribute.width = key.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
					if (resultType == "detail") {

						$scope.gridOptionsDetail.columnDefs.push(attribute)
					} else {

						$scope.gridOptionsSummary.columnDefs.push(attribute)
					}
				});
			}

			if (resultType == "detail") {
				$scope.gridOptionsDetail.data = result;
				$scope.totalItems = result.length;
				$scope.originalDataDetail = result;
			} else {
				$scope.gridOptionsSummary.data = result;
				$scope.totalItems = result.length;
				$scope.originalDataSummary = result;
			}
			console.log($scope.gridOptionsSummary.columnDefs)
			$scope.testgrid = true;
			$scope.showprogress = false;
		}
	}

	$scope.getResults = function (params, resultType) {
		$scope.gridOptionsDetail.columnDefs = [];
		var uuid = $scope.ruleExecUuid
		var version = $scope.ruleExecVersion;
		var limit = $scope.pagination.totalItems;
		var offset;
		var requestId;
		var sortBy;
		var order;
		if (params == null) {
			offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize);
			requestId = "";
			sortBy = null
			order = null;
		} else {
			offset = 0;
			requestId = params.requestId;
			sortBy = params.sortBy
			order = params.order;

		}
		Rule2Service.getRuleResults2(uuid, version, offset || 0, limit, requestId, sortBy, order, resultType)
			.then(function (response) { getResult(response.data) }, function (response) { OnError(response.data) });
		var getResult = function (response) {
			$scope.getColumnDetail('rule2', response.data, resultType);
			$scope.showprogress = false;
			$scope.testgrid = true;
		}
		var OnError = function (response) {
			$scope.showprogress = false;
			$scope.isDataError = true;
			$scope.datamessage = "Some Error Occurred"
		}
	}

	$scope.getResultsClientSide = function (params, resultType) {
		$scope.pagination.totalItems = params.length;
		if ($scope.pagination.totalItems > 0) {
			$scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize)) + 1);
		}
		else {
			$scope.pagination.to = 0;
		}
		if ($scope.pagination.totalItems < ($scope.pagination.pageSize * $scope.pagination.currentPage)) {
			$scope.pagination.from = $scope.pagination.totalItems;
		}
		else {
			$scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
		}

		var limit = ($scope.pagination.pageSize * $scope.pagination.currentPage);
		var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize);
		if (resultType == "summary") {
			$scope.gridOptionsSummary.data = params.slice(offset, limit);
		} else {
			$scope.gridOptionsDetail.data = params.slice(offset, limit);
		}

	}
	$scope.getExec = $scope.getRuleGroupExec;

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
		$scope.executionmsg = "Rule Group Restarted Successfully"
		notify.type = 'success',
			notify.title = 'Success',
			notify.content = $scope.executionmsg
		$rootScope.$emit('notify', notify);
		CommonService.restartExec("rulegroupExec", $stateParams.id, $stateParams.version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
		}
		$scope.refreshRuleGroupExecFunction();
	}



	// $scope.submitDownload = function () {
	// 	var uuid = $scope.download.data.uuid;
	// 	var version = $scope.download.data.version;
	// 	var url = $location.absUrl().split("app")[0];
	// 	$('#downloadSample').modal("hide");
	// 	$http({
	// 		method: 'GET',
	// 		url: url + "rule2/download?action=view&ruleExecUUID=" + uuid + "&ruleExecVersion=" + version + "&rows=" + $scope.download.rows + "&format=" + $scope.download.selectFormate + "&resultType=" + $scope.download.resultType,
	// 		responseType: 'arraybuffer'
	// 	}).success(function (data, status, headers) {
	// 		headers = headers();
	// 		$scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
	// 		var filename = headers['filename'];
	// 		var contentType = headers['content-type'];
	// 		var linkElement = document.createElement('a');
	// 		try {
	// 			var blob = new Blob([data], {
	// 				type: contentType
	// 			});
	// 			var url = window.URL.createObjectURL(blob);
	// 			linkElement.setAttribute('href', url);
	// 			linkElement.setAttribute("download", filename);
	// 			var clickEvent = new MouseEvent("click", {
	// 				"view": window,
	// 				"bubbles": true,
	// 				"cancelable": false
	// 			});
	// 			linkElement.dispatchEvent(clickEvent);
	// 		} catch (ex) {
	// 			console.log(ex);
	// 		}
	// 	}).error(function (data) {
	// 		console.log(data);
	// 	});
	// }


	$scope.downloadFile = function (data) {
		if ($scope.isD3RuleEexecGraphShow) {
			return false;
		}
		$scope.download.data = data;
		$scope.isDownloadDirective=true;
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="rule2";
		// $('#downloadSample').modal({
		// 	backdrop: 'static',
		// 	keyboard: false
		// });
	};
	$scope.onDownloaed=function(data){
		console.log(data);
		$scope.isDownloadDirective=data.isDownloadDirective;
	}
}); 