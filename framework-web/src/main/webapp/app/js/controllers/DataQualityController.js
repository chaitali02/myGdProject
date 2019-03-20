/****/
DataQualityModule = angular.module('DataQualityModule');

DataQualityModule.controller('DetailDataQualityController', function ($state, $stateParams, $rootScope, $scope,
  DataqulityService, privilegeSvc, CommonService, $timeout, $filter, CF_FILTER, CF_SUCCESS_MSG, CF_THRESHOLDTYPE) {

  $scope.dataqualitydata = {};
  $scope.mode = "false";
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

  $scope.isDestoryState = false;
  $scope.thresholdType = CF_THRESHOLDTYPE.thresholdType
  $scope.rhsNA = ['NULL', "NOT NULL"];
  $scope.userDetail = {}
  $scope.userDetail.uuid = $rootScope.setUseruuid;
  $scope.userDetail.name = $rootScope.setUserName;
  $scope.dq = {};
  $scope.dq.versions = []
  $scope.dataqualitycompare = null;
  $scope.datatype = ["DATE", "DOUBLE", "FLOAT", "INTEGER", "STRING", "TIMESTAMP"];
  $scope.blankSpaceTypes = ["LEADING", "TRAILING", "IN_BETWEEN", "ALL"];
  $scope.CaseCheckType = ["UPPER", "LOWER", "INITCAP"];
  $scope.abortConditionTypes = ["HIGH", "MEDIUM", "LOW"];
  $scope.selectDataType;// = $scope.datatype[0];
  $scope.sourceType = ["datapod"];
  $scope.refIntegrityTypes = ["datapod", "dataset", "relation"];
  $scope.dataqualitysourceType = $scope.sourceType[0];
  $scope.logicalOperator = ["AND", "OR"];
  $scope.spacialOperator = ['<', '>', '<=', '>=', '=', '!=', 'LIKE', 'NOT LIKE', 'RLIKE'];
  $scope.operator = CF_FILTER.operator;
  $scope.lhsType = [
    { "text": "string", "caption": "string" },
    { "text": "string", "caption": "integer" },
    { "text": "datapod", "caption": "attribute" },
    { "text": "formula", "caption": "formula" }];
  $scope.rhsType = [
    { "text": "string", "caption": "string", "disabled": false },
    { "text": "string", "caption": "integer", "disabled": false },
    { "text": "datapod", "caption": "attribute", "disabled": false },
    { "text": "formula", "caption": "formula", "disabled": false },
    { "text": "dataset", "caption": "dataset", "disabled": false },
    { "text": "paramlist", "caption": "paramlist", "disabled": false },
    { "text": "function", "caption": "function", "disabled": false }];

  $scope.selectType = true;
  $scope.isDependencyShow = false;
  $scope.isSelectSoureceAttr = false;
  $scope.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
  $scope.showRule = true;
  $scope.showRuleForm = true;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isshowmodel = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['dq'] || [];
  $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges['dq'] || [];
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

  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });

  $scope.checkIsInrogess = function () {
    if ($scope.isEditInprogess || $scope.isEditVeiwError) {
      return false;
    }
  }

  $scope.showRulePage = function () {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showgraph = false;
    $scope.showRule = true;
    $scope.showRuleForm = true
    $scope.graphDataStatus = false;
    $scope.showgraphdiv = false
  }

  $scope.showHome = function (uuid, version, mode) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRulePage();
    $state.go('createdataquality', {
      id: uuid,
      version: version,
      mode: mode
    });
  }

  $scope.enableEdit = function (uuid, version) {
    if ($scope.isPrivlage || $scope.dataqualitydata.locked == "Y") {
      return false;
    }
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRulePage()
    $state.go('createdataquality', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }

  $scope.showview = function (uuid, version) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    if (!$scope.isEdit) {
      $scope.showRulePage()
      $state.go('createdataquality', {
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

  $scope.close = function () {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('viewdataquality')
    }
  }

  $scope.$watch("isshowmodel", function (newvalue, oldvalue) {
    $scope.isshowmodel = newvalue
    sessionStorage.isshowmodel = newvalue
  })

  $scope.OnselectType = function () {
    if ($scope.selectDataType == "Date") {
      $scope.selectType = false;
    } else {
      $scope.selectType = true;
    }
  }

  $scope.showRuleGraph = function (uuid, version) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRule = false;
    $scope.showRuleForm = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;
  }

  $scope.countContinue = function () {
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

  $scope.getAllLatestActiveDependsOn = function (type) {
    DataqulityService.getAllLatestActive(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      $scope.allDependsOn = response
      if (typeof $stateParams.id == "undefined") {
        $scope.selectDependsOn = $scope.allDependsOn[0]
      }
      $scope.getAllAttributeBySource();
    }
  }



  $scope.onChangeTLow = function () {
    $scope.thresholdInfo.mediumMin = $scope.thresholdInfo.low;
  }

  $scope.onChangeTMedium = function () {
    $scope.thresholdInfo.lowMax = $scope.thresholdInfo.medium;
    $scope.thresholdInfo.highMin = $scope.thresholdInfo.medium;
  }

  $scope.onChangeTHigh = function () {
    $scope.thresholdInfo.mediumMax = $scope.thresholdInfo.high;
  }


  $scope.getAllLatestDomain = function () {
    DataqulityService.getAllLatest("domain").then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      $scope.allDomain = response;
    }
  }

  $scope.getAllLatestDomain();

  $scope.getExpressionByType = function () {
    if ($scope.selectDependsOn) {
      DataqulityService.getExpressionByType($scope.selectDependsOn.uuid, $scope.selectDependsOn).then(function (response) { onSuccessExpression(response.data) });
      var onSuccessExpression = function (response) {
        $scope.allExpress = response
      }
    }
  }

  $scope.getFormulaByType = function () {
    if ($scope.selectDependsOn) {
      DataqulityService.getFormulaBytype($scope.selectDependsOn.uuid, $scope.dataqualitysourceType)
        .then(function (response) { onSuccessFormula(response.data) });
      var onSuccessFormula = function (response) {
        $scope.allFromula = response;
      }
    }
  }

  $scope.getFunctionByCriteria = function () {
    CommonService.getFunctionByCriteria("", "N", "function")
      .then(function (response) { onSuccressGetFunction(response.data) });
    var onSuccressGetFunction = function (response) {
      $scope.allFunction = response;
    }
  }

  $scope.getAllAttributeBySource = function () {
    if ($scope.selectDependsOn) {
      DataqulityService.getAllAttributeBySource($scope.selectDependsOn.uuid, $scope.dataqualitysourceType)
        .then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        $scope.dataqualityoptions = response;
        $scope.lhsdatapodattributefilter = response;
      }
    }
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.showactive = "true"
    $scope.isSelectSoureceAttr = false
    $scope.isDependencyShow = true;
    $scope.isEditInprogess = true;
    $scope.isEditVeiwError = false;
    DataqulityService.getAllVersionByUuid($stateParams.id, "dq")
      .then(function (response) { onGetAllVersionByUuid(response.data) });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var dqversion = {};
        dqversion.version = response[i].version;
        $scope.dq.versions[i] = dqversion;
      }
    }

    DataqulityService.getOneByUuidAndVersionDQView($stateParams.id, $stateParams.version, "dq")
      .then(function (response) { onGetSuccess(response.data) }), function (response) { onError(response.data) };
    var onGetSuccess = function (response) {
      $scope.isEditInprogess = false;
      $scope.dataqualitydata = response.dqdata;
      $scope.tags = response.dqdata.tags;
      var defaultversion = {};
      defaultversion.version = response.dqdata.version;
      defaultversion.uuid = response.dqdata.uuid;
      $scope.uuid = response.uuid;
      $scope.version = response.version;
      $scope.dq.defaultVersion = defaultversion;
      $scope.dataqualitydata.upperBound = response.dqdata.rangeCheck.upperBound;
      $scope.dataqualitydata.lowerBound = response.dqdata.rangeCheck.lowerBound;

      $scope.getParamByApp();

      if (response.dqdata.dataTypeCheck != null) {
        $scope.selectDataType = response.dqdata.dataTypeCheck.toUpperCase();
      }

      $scope.selectdatefromate = response.dqdata.dateFormatCheck;
      $scope.dataqualitydata.maxLength = response.dqdata.lengthCheck.maxLength
      $scope.dataqualitydata.minLength = response.dqdata.lengthCheck.minLength
      $scope.dataqualitysourceType = response.dqdata.dependsOn.ref.type;

      var selectDependsOn = {}
      selectDependsOn.uuid = response.dqdata.dependsOn.ref.uuid;
      selectDependsOn.name = response.dqdata.dependsOn.ref.name
      $scope.selectDependsOn = selectDependsOn

      var selectrefIntegrityCheck = {};
      if (response.dqdata.refIntegrityCheck != null && response.dqdata.refIntegrityCheck.dependsOn != null) {
        $scope.selectedRIType = response.dqdata.refIntegrityCheck.dependsOn.ref.type;
        selectrefIntegrityCheck.uuid = response.dqdata.refIntegrityCheck.dependsOn.ref.uuid;
        selectrefIntegrityCheck.name = response.dqdata.refIntegrityCheck.dependsOn.ref.name;
        var refIntegrityCheckoption = {};
        refIntegrityCheckoption.uuid = response.dqdata.refIntegrityCheck.targetAttr.ref.uuid;
        refIntegrityCheckoption.datapodname = response.dqdata.refIntegrityCheck.targetAttr.ref.name;
        refIntegrityCheckoption.name = response.dqdata.refIntegrityCheck.targetAttr.attrName;
        refIntegrityCheckoption.attributeId = response.dqdata.refIntegrityCheck.targetAttr.attrId;
        $scope.refIntegrityCheckoption = refIntegrityCheckoption;
        $scope.selectrefIntegrityCheck = selectrefIntegrityCheck;
        $scope.OnChangeRIType();
      }

      $scope.getFunctionByCriteria();

      if (response.dqdata.attribute != null) {
        $scope.isSelectSoureceAttr = true
        var dataqualityoption = {};
        dataqualityoption.uuid = response.dqdata.attribute.ref.uuid;
        dataqualityoption.datapodname = response.dqdata.attribute.ref.name;
        dataqualityoption.name = response.dqdata.attribute.attrName
        dataqualityoption.attributeId = response.dqdata.attribute.attrId
        $scope.dataqualityoption = dataqualityoption;
      }

      if (response.dqdata.thresholdInfo != null) {
        $scope.thresholdInfo = {};
        $scope.thresholdInfo.lowMin = 1;
        $scope.thresholdInfo.lowMax = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.mediumMin = parseInt(response.dqdata.thresholdInfo.low);
        $scope.thresholdInfo.mediumMax = parseInt(response.dqdata.thresholdInfo.high);
        $scope.thresholdInfo.highMin = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.highMax = 100;
        $scope.thresholdInfo.low = parseInt(response.dqdata.thresholdInfo.low);
        $scope.thresholdInfo.medium = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.high = parseInt(response.dqdata.thresholdInfo.high);
        $scope.thresholdInfo.type = response.dqdata.thresholdInfo.type;
        $scope.thresholdInfo.isPresent = true;

      } else {
        $scope.thresholdInfo = {};
        $scope.thresholdInfo.lowMin = 1;
        $scope.thresholdInfo.lowMax = 100;
        $scope.thresholdInfo.mediumMin = 1;
        $scope.thresholdInfo.mediumMax = 100;
        $scope.thresholdInfo.highMin = 1;
        $scope.thresholdInfo.highMax = 100;
        $scope.thresholdInfo.isPresent = false;
      }
      $scope.filterTableArray = response.filterInfo;
      $scope.getAllLatestActiveDependsOn($scope.dataqualitysourceType);

      $scope.getExpressionByType();

      if (response.dqdata.domainCheck != null) {
        var domainCheck = {};
        domainCheck.uuid = response.dqdata.domainCheck.ref.uuid;
        domainCheck.name = response.dqdata.domainCheck.attrName;
        $scope.selectedDomain = domainCheck;
      }

      if (response.dqdata.expressionCheck != null) {
        var expressionCheck = {};
        expressionCheck.uuid = response.dqdata.expressionCheck.ref.uuid;
        expressionCheck.name = response.dqdata.expressionCheck.ref.name;
        $scope.selectedExpression = expressionCheck;
      }

    };
    var onError = function () {
      $scope.isEditInprogess = false;
      $scope.isEditVeiwError = true;
    }
  }
  else {
    $scope.showactive = "false";
    $scope.dataqualitydata.locked = "N";
    $scope.thresholdInfo = {};
    $scope.thresholdInfo.low = 25;
    $scope.thresholdInfo.medium = 50;
    $scope.thresholdInfo.high = 75;
    $scope.thresholdInfo.lowMin = 1;
    $scope.thresholdInfo.lowMax = $scope.thresholdInfo.medium;
    $scope.thresholdInfo.mediumMin = $scope.thresholdInfo.low;
    $scope.thresholdInfo.mediumMax = $scope.thresholdInfo.high;
    $scope.thresholdInfo.highMin = $scope.thresholdInfo.medium;
    $scope.thresholdInfo.highMax = 100;
    $scope.thresholdInfo.type = $scope.thresholdType[0];
    $scope.thresholdInfo.isPresent = true;
    $scope.getAllLatestActiveDependsOn("datapod");
  }



  $scope.selectVersion = function () {
    $scope.isSelectSoureceAttr = false
    $scope.myform1.$dirty = false;
    $scope.isEditInprogess = true;
    $scope.isEditVeiwError = false;
    DataqulityService.getOneByUuidAndVersionDQView($scope.dq.defaultVersion.uuid, $scope.dq.defaultVersion.version, "dq")
      .then(function (response) { onGetSuccess(response.data) }, function (response) { onError(response.data) });
    var onGetSuccess = function (response) {
      $scope.isEditInprogess = false;
      $scope.dataqualitydata = response.dqdata;
      if (response.dqdata.tags.length > 0) {
        $scope.tags = response.dqdata.tags;

      }
      var defaultversion = {};
      defaultversion.version = response.dqdata.version;
      defaultversion.uuid = response.dqdata.uuid;
      $scope.dq.defaultVersion = defaultversion;

      $scope.dataqualitydata.upperBound = response.dqdata.rangeCheck.upperBound;
      $scope.dataqualitydata.lowerBound = response.dqdata.rangeCheck.lowerBound;

      $scope.getParamByApp();
      if (response.dqdata.dataTypeCheck != null) {
        $scope.selectDataType = response.dqdata.dataTypeCheck.toUpperCase();
      }

      $scope.selectdatefromate = response.dqdata.dateFormatCheck;
      $scope.dataqualitydata.maxLength = response.dqdata.lengthCheck.maxLength
      $scope.dataqualitydata.minLength = response.dqdata.lengthCheck.minLength
      $scope.dataqualitysourceType = response.dqdata.dependsOn.ref.type;

      var selectDependsOn = {}
      selectDependsOn.uuid = response.dqdata.dependsOn.ref.uuid;
      selectDependsOn.name = response.dqdata.dependsOn.ref.name
      $scope.selectDependsOn = selectDependsOn;

      var selectrefIntegrityCheck = {};
      if (response.dqdata.refIntegrityCheck != null && response.dqdata.refIntegrityCheck.dependsOn != null) {
        $scope.selectedRIType = response.dqdata.refIntegrityCheck.dependsOn.ref.type;
        selectrefIntegrityCheck.uuid = response.dqdata.refIntegrityCheck.dependsOn.ref.uuid;
        selectrefIntegrityCheck.name = response.dqdata.refIntegrityCheck.dependsOn.ref.name;
        var refIntegrityCheckoption = {};
        refIntegrityCheckoption.uuid = response.dqdata.refIntegrityCheck.targetAttr.ref.uuid;
        refIntegrityCheckoption.datapodname = response.dqdata.refIntegrityCheck.targetAttr.ref.name;
        refIntegrityCheckoption.name = response.dqdata.refIntegrityCheck.targetAttr.attrName;
        refIntegrityCheckoption.attributeId = response.dqdata.refIntegrityCheck.targetAttr.attrId;
        $scope.refIntegrityCheckoption = refIntegrityCheckoption;
        $scope.selectrefIntegrityCheck = selectrefIntegrityCheck;
        $scope.OnChangeRIType();
      }

      $scope.getAllLatestActiveDependsOn($scope.dataqualitysourceType);
      $scope.getFunctionByCriteria();

      if (response.dqdata.attribute != null) {
        $scope.isSelectSoureceAttr = true
        var dataqualityoption = {};
        dataqualityoption.uuid = response.dqdata.attribute.ref.uuid;
        dataqualityoption.datapodname = response.dqdata.attribute.ref.name;
        dataqualityoption.name = response.dqdata.attribute.attrName
        dataqualityoption.attributeId = response.dqdata.attribute.attrId
        $scope.dataqualityoption = dataqualityoption;
      }
      else {
        $scope.dataqualityoptions = null;
      }

      $scope.filterTableArray = response.filterInfo;
      $scope.getExpressionByType();

      if (response.dqdata.domainCheck != null) {
        var domainCheck = {};
        domainCheck.uuid = response.dqdata.domainCheck.ref.uuid;
        domainCheck.name = response.dqdata.domainCheck.attrName;
        $scope.selectedDomain = domainCheck;
      }

      if (response.dqdata.expressionCheck != null) {
        var expressionCheck = {};
        expressionCheck.uuid = response.dqdata.expressionCheck.ref.uuid;
        expressionCheck.name = response.dqdata.expressionCheck.attrName;
        $scope.selectedExpression = expressionCheck;
      }


      if (response.dqdata.thresholdInfo != null) {
        $scope.thresholdInfo = {};
        $scope.thresholdInfo.lowMin = 1;
        $scope.thresholdInfo.lowMax = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.mediumMin = parseInt(response.dqdata.thresholdInfo.low);
        $scope.thresholdInfo.mediumMax = parseInt(response.dqdata.thresholdInfo.high);
        $scope.thresholdInfo.highMin = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.highMax = 100;
        $scope.thresholdInfo.low = parseInt(response.dqdata.thresholdInfo.low);
        $scope.thresholdInfo.medium = parseInt(response.dqdata.thresholdInfo.medium);
        $scope.thresholdInfo.high = parseInt(response.dqdata.thresholdInfo.high);
        $scope.thresholdInfo.type = response.dqdata.thresholdInfo.type;
        $scope.thresholdInfo.isPresent = true;

      } else {
        $scope.thresholdInfo = {};
        $scope.thresholdInfo.lowMin = 1;
        $scope.thresholdInfo.lowMax = 100;
        $scope.thresholdInfo.mediumMin = 1;
        $scope.thresholdInfo.mediumMax = 100;
        $scope.thresholdInfo.highMin = 1;
        $scope.thresholdInfo.highMax = 100;
        $scope.thresholdInfo.isPresent = false;
      }
    };
    var onError = function () {
      $scope.isEditInprogess = false;
      $scope.isEditVeiwError = true;
    }
  }



  $scope.dependsOnDataQuality = function () {
    $scope.dataqualityoptions;
    if (!$scope.selectDependsOn) {
      return false;
    }
    $scope.getAllAttributeBySource();
    $scope.getFormulaByType();
    $scope.getExpressionByType();
  }


  $scope.onSourceAttributeChagne = function () {
    if ($scope.dataqualityoption != null) {
      $scope.isSelectSoureceAttr = true
      $scope.dataqualitydata.nullCheck = 'Y';
    }
    else {
      $scope.isSelectSoureceAttr = false
      $scope.dataqualitydata.nullCheck = 'N';
      $scope.dataqualitydata.valueCheck = ""
      $scope.dataqualitydata.lowerBound = "";
      $scope.dataqualitydata.upperBound = "";
      $scope.selectDataType = "";
      $scope.selectdatefromate = "";
      $scope.dataqualitydata.minLength = ""
      $scope.dataqualitydata.maxLength = "";
      $scope.selectrefIntegrityCheck = "";
      $scope.refIntegrityCheckoption = "";
      $scope.dataqualitydata.stdDevCheck = "";
    }
  }



  $scope.SearchAttribute = function (index, type, propertyType) {
    $scope.selectAttr = $scope.filterTableArray[index][propertyType]
    $scope.searchAttr = {};
    $scope.searchAttr.type = type;
    $scope.searchAttr.propertyType = propertyType;
    $scope.searchAttr.index = index;
    CommonService.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
    var onSuccessGetAllLatest = function (response) {
      $scope.allSearchType = {}
      $scope.allSearchType.options = response;
      $scope.allSearchType.defaultoption = response[0];
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
      DataqulityService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, type).then(function (response) { onSuccessAttributeBySource(response.data) });
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
    DataqulityService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid, $scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
    var onSuccessAttributeBySource = function (response) {
      $scope.allAttr = response;
    }
  }

  $scope.SubmitSearchAttr = function () {
    if ($scope.dataqualitycompare != null) {
      $scope.dataqualitycompare.filterChg = "y"
    }
    $scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType] = $scope.selectAttr;
    $('#searchAttr').modal('hide')
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
    if ($scope.rulecompare != null) {
      $scope.rulecompare.filterChg = "y"
    }
    if ($scope.filterTableArray[index].operator == 'BETWEEN') {
      $scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[1];
      $scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist'])
      $scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
    } else if (['IN', 'NOT IN'].indexOf($scope.filterTableArray[index].operator) != -1) {
      $scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, []);
      $scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
      $scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
    }
    else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
      $scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist', 'string', 'integer']);
      $scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
      $scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
    } else if (['<', '>', "<=", '>='].indexOf($scope.filterTableArray[index].operator) != -1) {
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
    angular.forEach($scope.filterTableArray, function (filter) {
      filter.selected = $scope.checkAll;
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
    var filertable = {};
    filertable.islhsDatapod = true;
    filertable.islhsFormula = false;
    filertable.islhsSimple = false;
    filertable.isrhsDatapod = true;
    filertable.isrhsFormula = false;
    filertable.isrhsSimple = false;
    filertable.lhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];
    filertable.rhsdatapodAttribute = $scope.lhsdatapodattributefilter[0];
    filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
    filertable.operator = $scope.operator[0].value
    filertable.lhstype = $scope.lhsType[2]
    filertable.rhstype = $scope.rhsType[2];
    filertable.rhsTypes = returnRshType();
    filertable.rhsTypes = $scope.disableRhsType(filertable.rhsTypes, ['dataset']);
    filertable.rhsvalue;
    filertable.lhsvalue;
    $scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);
  }

  $scope.removeFilterRow = function () {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.filterTableArray, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
        $scope.fitlerAttrTableSelectedItem = [];
      }
    });
    if (newDataList && newDataList.length > 0)
      newDataList[0].logicalOperator = "";
    $scope.filterTableArray = newDataList;
  }

  /*$scope.onAttrFilterRowDown=function(index){	
		var rowTempIndex=$scope.filterTableArray[index];
    var rowTempIndexPlus=$scope.filterTableArray[index+1];
		$scope.filterTableArray[index]=rowTempIndexPlus;
		$scope.filterTableArray[index+1]=rowTempIndex;
		if(index ==0){
			$scope.filterTableArray[index+1].logicalOperator=$scope.filterTableArray[index].logicalOperator;
			$scope.filterTableArray[index].logicalOperator=""
		}
	}*/

	/*$scope.onAttrFilterRowUp=function(index){
		var rowTempIndex=$scope.filterTableArray[index];
    var rowTempIndexMines=$scope.filterTableArray[index-1];
		$scope.filterTableArray[index]=rowTempIndexMines;
		$scope.filterTableArray[index-1]=rowTempIndex;
		if(index ==1){
			$scope.filterTableArray[index].logicalOperator=$scope.filterTableArray[index-1].logicalOperator;
			$scope.filterTableArray[index-1].logicalOperator=""
		}
	}*/

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
      $scope.getFormulaByType();

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
      $scope.getFormulaByType();
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
      $scope.getParamByApp();

    }
  }

  $scope.getParamByApp = function () {
    CommonService.getParamByApp($rootScope.appUuidd || "", "application").
      then(function (response) { onSuccessGetParamByApp(response.data) });
    var onSuccessGetParamByApp = function (response) {
      $scope.allparamlistParams = [];
      if (response.length > 0) {
        var paramsArray = [];
        for (var i = 0; i < response.length; i++) {
          var paramjson = {}
          var paramsjson = {};
          paramsjson.uuid = response[i].ref.uuid;
          paramsjson.name = response[i].ref.name + "." + response[i].paramName;
          paramsjson.attributeId = response[i].paramId;
          paramsjson.attrType = response[i].paramType;
          paramsjson.paramName = response[i].paramName;
          paramsjson.caption = "app." + paramsjson.paramName;
          paramsArray[i] = paramsjson
        }
        $scope.allparamlistParams = paramsArray;
      }
    }
  }


  $scope.OnChangeRIType = function () {
    if ($scope.selectedRIType) {
      if ($scope.selectedRIType == "relation") {
        DataqulityService.findRelationByDatapod($scope.selectDependsOn.uuid, $scope.selectedRIType).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          $scope.refIntegrityCheck = response;
          if (typeof $stateParams.id != "undefined") {
            $scope.onRefIntegrityCheck();
          }
        }
      } else {
        DataqulityService.getAllLatestActive($scope.selectedRIType).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          $scope.refIntegrityCheck = response;
          if (typeof $stateParams.id != "undefined") {
            $scope.onRefIntegrityCheck();
          }
        }
      }
    } else {
      $scope.refIntegrityCheck = null;
      $scope.refIntegrityCheckoptions = null;
    }
  }

  $scope.onRefIntegrityCheck = function () {
    if ($scope.selectrefIntegrityCheck) {
      DataqulityService.getAllAttributeBySource($scope.selectrefIntegrityCheck.uuid, $scope.selectedRIType).then(function (response) { onSuccessAttributeBySource(response.data) });
      var onSuccessAttributeBySource = function (response) {
        $scope.refIntegrityCheckoptions = response;
      }
    }
  }


  $scope.okDQRuleSave = function () {
    var hidemode = "yes";
    if (hidemode == 'yes' && $scope.isDestoryState == false) {
      setTimeout(function () {
        $state.go('viewdataquality');
      }, 2000);

    }


  }

  $scope.sbumitDataqulity = function () {
    var upd_tag = "N"
    var options = {}
    $scope.dataLoading = true;
    $scope.isshowmodel = true;
    options.execution = $scope.checkboxModelexecution;
    var dataqualityjosn = {}

    dataqualityjosn.uuid = $scope.dataqualitydata.uuid;
    dataqualityjosn.name = $scope.dataqualitydata.name;
    dataqualityjosn.desc = $scope.dataqualitydata.desc;
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
    dataqualityjosn.tags = tagArray;
    dataqualityjosn.active = $scope.dataqualitydata.active;
    dataqualityjosn.locked = $scope.dataqualitydata.locked;
    dataqualityjosn.published = $scope.dataqualitydata.published
    dataqualityjosn.publicFlag = $scope.dataqualitydata.publicFlag;

    var attributeref = {}
    var attribute = {};
    var ref = {};
    var dependsOn = {};
    ref.type = $scope.dataqualitysourceType;
    ref.uuid = $scope.selectDependsOn.uuid;
    dependsOn.ref = ref;
    dataqualityjosn.dependsOn = dependsOn;

    if (typeof $scope.dataqualityoption != "undefined" && $scope.dataqualityoption != null) {
      attributeref.type = "datapod";
      attributeref.uuid = $scope.dataqualityoption.uuid;
      attribute.ref = attributeref;
      attribute.attrId = $scope.dataqualityoption.attributeId;
      dataqualityjosn.attribute = attribute;
      dataqualityjosn.blankSpaceCheck = $scope.dataqualitydata.blankSpaceCheck;
    } else {
      dataqualityjosn.attribute = null;
    }

    dataqualityjosn.duplicateKeyCheck = $scope.dataqualitydata.duplicateKeyCheck
    dataqualityjosn.nullCheck = $scope.dataqualitydata.nullCheck
    var tagArrayvaluecheck = [];
    if ($scope.dataqualitydata.valueCheck != null) {
      for (var counttag = 0; counttag < $scope.dataqualitydata.valueCheck.length; counttag++) {
        tagArrayvaluecheck[counttag] = $scope.dataqualitydata.valueCheck[counttag].text;
      }
    }
    dataqualityjosn.valueCheck = tagArrayvaluecheck
    var rengeCheck = {};
    rengeCheck.lowerBound = $scope.dataqualitydata.lowerBound;
    rengeCheck.upperBound = $scope.dataqualitydata.upperBound;
    dataqualityjosn.rangeCheck = rengeCheck;
    dataqualityjosn.dataTypeCheck = $scope.selectDataType
    dataqualityjosn.dateFormatCheck = $scope.selectdatefromate
    if (typeof $scope.dataqualitydata.customFormatCheck != "undefined") {
      dataqualityjosn.customFormatCheck = $scope.dataqualitydata.customFormatCheck
    }

    var lengthCheck = {}
    lengthCheck.minLength = $scope.dataqualitydata.minLength;
    lengthCheck.maxLength = $scope.dataqualitydata.maxLength;
    dataqualityjosn.lengthCheck = lengthCheck;

    var refIntegrityCheck = {};
    var rIDependsOn = {};
    var refRIDependsOn = {};
    var rITargetAttr = {};
    var refRITargetAttr = {};
    if ($scope.selectedRIType && $scope.selectrefIntegrityCheck && $scope.refIntegrityCheckoption) {
      refRIDependsOn.type = $scope.selectedRIType;
      refRIDependsOn.uuid = $scope.selectrefIntegrityCheck.uuid;
      rIDependsOn.ref = refRIDependsOn;
      refIntegrityCheck.dependsOn = rIDependsOn;

      refRITargetAttr.type = $scope.refIntegrityCheckoption.type;
      refRITargetAttr.uuid = $scope.refIntegrityCheckoption.uuid;
      rITargetAttr.ref = refRITargetAttr;
      rITargetAttr.attrId = $scope.refIntegrityCheckoption.attributeId;
      refIntegrityCheck.targetAttr = rITargetAttr;
      dataqualityjosn.refIntegrityCheck = refIntegrityCheck;
    }
    else {
      dataqualityjosn.refIntegrityCheck = null;
    }

    /*if (typeof $scope.refIntegrityCheckoption != "undefined" && $scope.refIntegrityCheckoption != null && $scope.refIntegrityCheckoption != "") {
      ref.type = "datapod";
      ref.uuid = $scope.selectrefIntegrityCheck.uuid;
      refIntegrityCheck.ref = ref;
      refIntegrityCheck.attrId = $scope.refIntegrityCheckoption.attributeId;
      dataqualityjosn.refIntegrityCheck = refIntegrityCheck;

    } else {
      dataqualityjosn.refIntegrityCheck = {};
    }*/

    var filterInfoArray = [];
    if ($scope.filterTableArray != null) {
      if ($scope.filterTableArray.length > 0) {
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
            if ($scope.rulsourcetype == "dataset") {
              lhsref.type = "dataset";
            }
            else {
              lhsref.type = "datapod";
            }
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
            if ($scope.rulsourcetype == "dataset") {
              rhsref.type = "dataset";
            }
            else {
              rhsref.type = "datapod";
            }
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
        }

        dataqualityjosn.filterInfo = filterInfoArray;
      } else {
        dataqualityjosn.filterInfo = null;

      }
    } else {
      dataqualityjosn.filterInfo = null;
    }

    var thresholdInfo = {};
    if ($scope.thresholdInfo.isPresent = true) {
      thresholdInfo.type = $scope.thresholdInfo.type;
      thresholdInfo.low = $scope.thresholdInfo.low;
      thresholdInfo.medium = $scope.thresholdInfo.medium;
      thresholdInfo.high = $scope.thresholdInfo.high;
      dataqualityjosn.thresholdInfo = thresholdInfo;
    } else {
      dataqualityjosn.thresholdInfo = null;
    }


    if ($scope.selectedExpression) {
      var expressionInfo = {};
      var ref = {};
      ref.type = "expression";
      ref.uuid = $scope.selectedExpression.uuid;
      expressionInfo.ref = ref;
      dataqualityjosn.expressionCheck = expressionInfo;
    }
    dataqualityjosn.caseCheck = $scope.dataqualitydata.caseCheck;
    if ($scope.selectedDomain) {
      var domainInfo = {};
      var ref = {};
      ref.type = "domain"
      ref.uuid = $scope.selectedDomain.uuid;
      domainInfo.ref = ref;
      dataqualityjosn.domainCheck = domainInfo;
    }
    dataqualityjosn.abortCondition = $scope.dataqualitydata.abortCondition;
    console.log(JSON.stringify(dataqualityjosn))
    DataqulityService.submit(dataqualityjosn, "dq", upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      $scope.dataLoading = false;
      $scope.changemodelvalue()
      if (options.execution == "YES") {
        DataqulityService.getOneById(response.data, "dq").then(function (response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function (result) {
          DataqulityService.executeDQRule(result.data.uuid, result.data.version).then(function (response) {
            onSuccess(response.data)
          });
          var onSuccess = function (response) {
            $scope.saveMessage = CF_SUCCESS_MSG.dqSaveExecute;
            notify.type = 'success',
              notify.title = 'Success',
              notify.content = $scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okDQRuleSave();
          }
        } /*end onSuccessGetOneById */
      } /*End If*/
      else {
        $scope.saveMessage = CF_SUCCESS_MSG.dqSave;
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = $scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okDQRuleSave();
      } //End Else
    } //End Submit Api Function
    var onError = function (response) {
      notify.type = 'error',
        notify.title = 'Error',
        notify.content = "Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function


  $scope.changemodelvalue = function () {
    $scope.isshowmodel = sessionStorage.isshowmodel
  };


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
});


DataQualityModule.controller('DetailDataqualityGroupController', function ($state, $timeout, $filter, privilegeSvc, $stateParams, $location, $rootScope, $scope, DataqulityService, CommonService, CF_SUCCESS_MSG) {
  $scope.select = 'Rule Group';
  $scope.isDestoryState = false;
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
  $scope.showgraphdiv = false;
  $scope.mode = " ";
  $scope.dqgroup = {};
  $scope.dqgroup.versions = []
  $scope.showRuleGroup = true;
  $scope.showRuleGroupForm = true;
  $scope.isshowmodel = false;
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['dqgroup'] || [];
  $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges['dqgroup'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  });

  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };

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
  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });
  $scope.checkIsInrogess = function () {
    if ($scope.isEditInprogess || $scope.isEditVeiwError) {
      return false;
    }
  }
  $scope.showRulGroupePage = function () {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRuleGroup = true;
    $scope.showgraphdiv = false;
    $scope.graphDataStatus = false;
    $scope.showRuleGroupForm = true;
  }
  $scope.showHome = function (uuid, version, mode) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRulGroupePage();
    $state.go('createdataqualitygroup', {
      id: uuid,
      version: version,
      mode: mode
    });
  }
  $scope.enableEdit = function (uuid, version) {
    if ($scope.isPrivlage || $scope.dqruleGroupDetail.locked == "Y") {
      return false;
    }
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRulGroupePage()
    $state.go('createdataqualitygroup', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }

  $scope.showview = function (uuid, version) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    if (!$scope.isEdit) {
      $scope.showRulGroupePage()
      $state.go('createdataqualitygroup', {
        id: uuid,
        version: version,
        mode: 'true'
      });
    }
  }

  $scope.close = function () {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('viewdataqualitygroup')
    }
  }

  $scope.$watch("isshowmodel", function (newvalue, oldvalue) {
    $scope.isshowmodel = newvalue
    sessionStorage.isshowmodel = newvalue
  })

  DataqulityService.getAllLatest('dq').then(function (response) {
    onSuccess(response.data)
  });
  var onSuccess = function (response) {
    var dqArray = [];
    for (var i = 0; i < response.length; i++) {
      var dqjosn = {};
      dqjosn.uuid = response[i].uuid;
      dqjosn.id = response[i].uuid
      dqjosn.name = response[i].name;
      dqjosn.version = response[i].version;
      dqArray[i] = dqjosn;
    }
    $scope.dqall = dqArray;
  }

  $scope.showRuleGroupGraph = function (uuid, version) {
    if ($scope.checkIsInrogess() == false) {
      return false;
    }
    $scope.showRuleGroup = false;
    $scope.showgraphdiv = true;
    $scope.graphDataStatus = true;
    $scope.showRuleGroupForm = false;
  }


  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.isEditInprogess = true;
    $scope.isEditVeiwError = false;
    DataqulityService.getAllVersionByUuid($stateParams.id, "dqgroup").then(function (response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var dqgroupversion = {};
        dqgroupversion.version = response[i].version;
        $scope.dqgroup.versions[i] = dqgroupversion;
      }

    }
    DataqulityService.getOneByUuidAndVersion1($stateParams.id, $stateParams.version, 'dqgroup')
      .then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
    var onsuccess = function (response) {
      $scope.isEditInprogess = false;
      $scope.dqruleGroupDetail = response;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.dqgroup.defaultVersion = defaultversion;
      $scope.checkboxModelparallel = response.inParallel;
      $scope.uuid = response.uuid;
      $scope.version = response.version;
      $scope.tags = response.tags
      var ruleTagArray = [];
      for (var i = 0; i < response.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.ruleInfo[i].ref.uuid;
        ruletag.name = response.ruleInfo[i].ref.name;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruletag.id = response.ruleInfo[i].ref.uuid
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    };
    var onError = function () {
      $scope.isEditInprogess = false;
      $scope.isEditVeiwError = true;
    }
  } else {
    $scope.dqruleGroupDetail = {};
    $scope.dqruleGroupDetail.locked = "N";
  }

  $scope.selectVersion = function () {
    $scope.myform.$dirty = false;
    $scope.isEditInprogess = true;
    $scope.isEditVeiwError = false;
    DataqulityService.getOneByUuidAndVersion($scope.dqgroup.defaultVersion.uuid, $scope.dqgroup.defaultVersion.version, 'dqgroup')
      .then(function (response) { onsuccess(response.data) }, function (response) { onError(response.data) });
    var onsuccess = function (response) {
      $scope.isEditInprogess = false;
      $scope.dqruleGroupDetail = response.data;
      var defaultversion = {};
      defaultversion.version = response.data.version;
      defaultversion.uuid = response.data.uuid;
      $scope.dqgroup.defaultVersion = defaultversion;
      $scope.tags = response.data.tags
      var ruleTagArray = [];
      for (var i = 0; i < response.data.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.data.ruleInfo[i].ref.uuid;
        ruletag.name = response.data.ruleInfo[i].ref.name;
        ruletag.version = response.data.ruleInfo[i].ref.version;
        ruletag.id = response.data.ruleInfo[i].ref.uuid

        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
    var onError = function () {
      $scope.isEditInprogess = false;
      $scope.isEditVeiwError = true;
    }
  }

  $scope.loadRules = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.dqall, query);
    });
  };

  $scope.clear = function () {
    $scope.ruleTags = null;
  }

  $scope.okDqGroupSave = function () {
    $('#dqrulegroupsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes' && $scope.isDestoryState == false) {
      setTimeout(function () {
        $state.go('viewdataqualitygroup');
      }, 2000);

    }


  }
  $scope.sbumitRuleGroup = function () {
    var dqruleGroupJson = {};
    var upd_tag = "N"
    $scope.dataLoading = true;
    $scope.isshowmodel = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    dqruleGroupJson.uuid = $scope.dqruleGroupDetail.uuid;
    dqruleGroupJson.name = $scope.dqruleGroupDetail.name;
    dqruleGroupJson.desc = $scope.dqruleGroupDetail.desc;
    dqruleGroupJson.active = $scope.dqruleGroupDetail.active;
    dqruleGroupJson.locked = $scope.dqruleGroupDetail.locked;
    dqruleGroupJson.published = $scope.dqruleGroupDetail.published;
    dqruleGroupJson.publicFlag = $scope.dqruleGroupDetail.publicFlag;

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

    dqruleGroupJson.tags = tagArray;
    var ruleInfoArray = [];
    for (var i = 0; i < $scope.ruleTags.length; i++) {
      var ruleInfo = {}
      var ref = {};
      ref.type = "dq";
      ref.uuid = $scope.ruleTags[i].uuid;
      ruleInfo.ref = ref;
      ruleInfoArray[i] = ruleInfo;
    }
    dqruleGroupJson.ruleInfo = ruleInfoArray;
    dqruleGroupJson.inParallel = $scope.checkboxModelparallel
    console.log(JSON.stringify(dqruleGroupJson))
    DataqulityService.submit(dqruleGroupJson, "dqgroup", upd_tag).then(function (response) {
      onSuccess(response.data)
    }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      $scope.changemodelvalue();

      if (options.execution == "YES") {
        DataqulityService.getOneById(response.data, 'dqgroup').then(function (response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function (response) {
          DataqulityService.executeDQGroup(response.data.uuid, response.data.version).then(function (response) {
            onSuccess(response.data)
          });
          var onSuccess = function (response) {
            $scope.dataLoading = false;
            $scope.saveMessage = CF_SUCCESS_MSG.dqGroupSaveExecute;//"DQ Rule Groups Saved and Submitted Successfully"
            // if ($scope.isshowmodel == "true") {
            //   $('#dqrulegroupsave').modal({
            //     backdrop: 'static',
            //     keyboard: false
            //   });
            // } //End Inner If
            notify.type = 'success',
              notify.title = 'Success',
              notify.content = $scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okDqGroupSave();
          }
        } //End onSuccessGetOneById
      } //End If
      else {
        $scope.dataLoading = false;
        $scope.saveMessage = CF_SUCCESS_MSG.dqGroupSave;//"DQ Rule Groups Saved Successfully"
        // if ($scope.isshowmodel == "true") {
        //   $('#dqrulegroupsave').modal({
        //     backdrop: 'static',
        //     keyboard: false
        //   });
        // } //End Inner If
        notify.type = 'success',
          notify.title = 'Success',
          notify.content = $scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okDqGroupSave();
      } //End Else
    }
    var onError = function (response) {
      notify.type = 'error',
        notify.title = 'Error',
        notify.content = "Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function

  $scope.changemodelvalue = function () {
    $scope.isshowmodel = sessionStorage.isshowmodel
  };
});


DataQualityModule.controller('ResultDQController', function ($http, dagMetaDataService, $state, $timeout, $filter, $stateParams, $location, $rootScope, $scope, NgTableParams, DataqulityService, uuid2, CommonService, privilegeSvc, CF_DOWNLOAD) {

  $scope.select = $stateParams.type;
  $scope.type = {
    text: $scope.select == 'dqgroupexec' ? 'dqgroup' : 'dq'
  };

  $scope.setType = function () {
    $scope.select = $stateParams.type;
    $scope.type = {
      text: $scope.select == 'dqgroupexec' ? 'dqgroup' : 'dq'
    };
    if ($scope.type == "dq") {
      $scope.obj = {};
      $scope.obj.uuid = $stateParams.id;
      $scope.obj.version = $stateParams.version;
      $scope.obj.name = $stateParams.name;
    } else {

    }
  }


  $scope.sortdetail = [];
  $scope.colcount = 0;
  $scope.showprogress = false;
  $scope.isRuleExec = false;
  $scope.isRuleResult = false;
  $scope.zoomSize = 7;
  $scope.isGraphRuleGroupExec = false;
  $scope.isD3RuleEexecGraphShow = false;
  $scope.isD3RGEexecGraphShow = false;
  $scope.currentPage = 1;
  $scope.pageSize = 100;
  $scope.paginationPageSizes = [10, 25, 50, 75, 100],
  $scope.testgrid = false;
  $scope.filteredRowsDetail = [];
  $scope.filteredRowsSummary = [];
  $scope.download = {};
  $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
  $scope.download.formates = CF_DOWNLOAD.formate;
  $scope.download.selectFormate = CF_DOWNLOAD.formate[0];
  $scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
  $scope.download.resultType = "summary";
  $scope.download.limit_to = CF_DOWNLOAD.limit_to;

  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
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
    useExternalPagination: true,
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

  $scope.getGridStyle = function () {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRowsDetail && $scope.filteredRowsDetail.length > 0) {
      style['height'] = (($scope.filteredRowsDetail.length < 10 ? $scope.filteredRowsDetail.length * 40 : 400) + 40) + 'px';
    }
    else {
      style['height'] = "100px";
    }
    return style;
  }

  $scope.gridOptionsSummary = {
    rowHeight: 40,
    useExternalPagination: true,
    exporterMenuPdf: false,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: { fontSize: 9 },
    exporterPdfTableHeaderStyle: { fontSize: 10, bold: true, italics: true, color: 'red' },
    useExternalPagination: true,
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

  $scope.getGridStyleSummary = function () {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRowsSummary && $scope.filteredRowsSummary.length > 0) {
      style['height'] = (($scope.filteredRowsSummary.length < 10 ? $scope.filteredRowsSummary.length * 40 : 400) + 50) + 'px';
    }
    else {
      style['height'] = "100px";
    }
    return style;
  }

  $scope.refreshDataSummary = function (searchtextSummary) {
    $scope.gridOptionsSummary.data = $filter('filter')($scope.originalDataSummary, searchtextSummary, undefined);
  };

  $scope.refreshDataDetail = function (searchtextDetail) {
    $scope.gridOptionsDetail.data = $filter('filter')($scope.originalDataDetail, searchtextDetail, undefined);
  };

  $scope.selectPage = function (pageNo) {
    $scope.currentPage = pageNo;
  };

  $scope.pageChanged = function () {
    $scope.getResults(null)

  };
  $scope.pageChangedSummary = function () {
    $scope.getColumnDetail('dq', $scope.originalDataSummary, "summary");
  }

  $scope.onPerPageChange = function () {
    $scope.currentPage = 1;
    $scope.getResults(null)
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

  $scope.toggleZoom = function () {
    $scope.showZoom = !$scope.showZoom;
  }

  $scope.onClickRuleResult = function () {
    $scope.isRuleExec = true;
    $scope.isRuleResult = false;
    $scope.isD3RuleEexecGraphShow = false;
    $scope.activeTabIndex = 0;
    if ($scope.type.text == "dqgroup") {
      $scope.isGraphRuleExec = false;
      $scope.execDetail = $scope.rulegroupdatail;
      $scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;

    } else {
      $scope.isRuleTitle = false;
      $scope.isRuleSelect = true;
    }
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
      $scope.testgrid = true;
      $scope.showprogress = false;
    }
  }

  $scope.getResults = function (params, resultType) {
    $scope.showprogress = true;
    $scope.isDataError = false;
    $scope.to = (($scope.currentPage - 1) * $scope.pageSize);
    if ($scope.totalItems < ($scope.pageSize * $scope.currentPage)) {
      $scope.from = $scope.totalItems;
    } else {
      $scope.from = (($scope.currentPage) * $scope.pageSize);
    }
    $scope.gridOptionsDetail.columnDefs = [];
    var uuid = $scope.dqexecdetail.uuid
    var version = $scope.dqexecdetail.version;
    var limit = $scope.pageSize;
    var offset;
    var requestId;
    var sortBy;
    var order;
    if (params == null) {
      offset = (($scope.currentPage - 1) * $scope.pageSize) //(($scope.pagination.pageNumber - 1) * $scope.pagination.pageSize);
      requestId = "";
      sortBy = null
      order = null;
    } else {
      offset = 0;
      requestId = params.requestId;
      sortBy = params.sortBy
      order = params.order;

    }

    DataqulityService.getDataQualResults2(uuid, version, offset || 0, limit, requestId, sortBy, order, resultType)
      .then(function (response) { getResult(response.data) }, function (response) { OnError(response.data) });
    var getResult = function (response) {
      $scope.getColumnDetail('dq', response.data, resultType);
      $scope.showprogress = false;

    }
    var OnError = function (response) {
      $scope.showprogress = false;
      $scope.isDataError = true;
      $scope.datamessage = "Some Error Occurred"
    }
  }

  $scope.go = function (index) {
    if ($scope.type.text == "dq" || $scope.selectResult == "dq") {
      $scope.setType();
      if (index == 1) {
        $scope.download.resultType = "detail";
        $scope.getDqExec({
          uuid: $scope.obj.id,
          version: $scope.obj.version
        }, "detail");
      }
      else {
        $scope.download.resultType = "summary";
        $scope.getDqExec({
          uuid: $scope.obj.id,
          version: $scope.obj.version
        }, "summary");
      }
    }
  }

  window.showResult = function (params) {
    App.scrollTop();
    $scope.testgrid = false;
    $scope.selectGraphRuleExec = params.name
    $scope.isGraphRuleExec = true;
    $scope.isRuleGroupTitle = true;
    $scope.isRuleTitle = false;
    $scope.selectResult = "dq";
    $scope.obj = {};
    $scope.obj.id = params.id;
    $scope.obj.version = params.version;
    $scope.obj.name = params.name;
    $scope.getDqExec({
      uuid: params.id,
      version: params.version
    }, "summary");
  }


  $scope.refreshResultFunction = function () {
    $scope.isD3RuleEexecGraphShow = false;
    $scope.testgrid = false;
    if ($scope.activeTabIndex == 0) {
      $scope.getDqExec($scope.ruleexecdetail, "summary");
    } else {
      $scope.getDqExec($scope.ruleexecdetail, "detail");
    }
  }
  $scope.ruleExecshowGraph = function () {
    $scope.isD3RuleEexecGraphShow = true;
  }

  $scope.getDqExec = function (data, resultType) {
    $scope.execDetail = data;
    $scope.metaType = dagMetaDataService.elementDefs["dq"].execType;
    $scope.ruleexecdetail = data
    $scope.isRuleResult = true;
    $scope.isRuleExec = false;
    $scope.isRuleSelect = false;
    $scope.isData = false;
    $scope.showprogress = true;
    if ($scope.type.text == 'dqgroup') {
      $scope.isRuleGroupTitle = true;
    } else {
      $scope.isRuleTitle = true;
      $scope.isRuleGroupTitle = false;
    }
    var dqexecjson = {}
    dqexecjson.uuid = data.uuid;
    dqexecjson.version = data.version;
    $scope.ruledata = data.name;
    $scope.dqexecdetail = dqexecjson
    DataqulityService.getNumRowsbyExec(data.uuid, data.version, "dqexec").then(function (response) {
      onSuccessGetNumRowsbyExec(response.data)
    });
    var onSuccessGetNumRowsbyExec = function (response) {
      // $scope.totalItems = response.numRows;
      $scope.getResults(null, resultType);
    }
  } //End getDqExec Method

  $scope.refreshRuleGroupExecFunction = function () {
    $scope.isD3RGEexecGraphShow = false;
    $scope.dqGroupExec($scope.rulegroupdatail);
  }

  $scope.rGExecshowGraph = function () {
    $scope.isGraphRuleGroupExec = false;
    $scope.isD3RGEexecGraphShow = true;
  }

  $scope.dqGroupExec = function (data) {
    $scope.setType();
    if ($scope.type.text == 'dq') {
      $scope.obj = {};
      $scope.obj.id = data.uuid;
      $scope.obj.version = data.version;
      $scope.obj.name = data.name
      $scope.getDqExec(data, "summary");
      return;
    }
    $scope.execDetail = data;
    $scope.metaType = dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
    $scope.rulegroupdatail = data
    $scope.rGExecUuid = data.uuid;
    $scope.rGExecVersion = data.version;
    $scope.ruleGrpupName = data.name;
    $scope.isRuleGroupExec = false;
    $scope.isRuleSelect = false;
    $scope.isRuleExec = true;
    $scope.isRuleGroupTitle = true;
    if ($scope.type.text == 'dqgroup') {
      $scope.isGraphRuleGroupExec = true;
    } else {
      $scope.isGraphRuleGroupExec = false;
    }
    var params = {
      "id": data.uuid,
      "name": data.name,
      "elementType": "dqgroup",
      "version": data.version,
      "type": "dataQualGroup",
      "typeLabel": "DataQualGroup",
      "url": "dataqual/getdqExecBydqGroupExec?",
      "ref": {
        "type": "dqgroupExec",
        "uuid": data.uuid,
        "version": data.version,
        "name": data.name
      }
    };
    setTimeout(function () {
      $scope.$broadcast('generateGroupGraph', params);
    }, 500);
  } //End dqGroupExec

  $scope.getExec = $scope.dqGroupExec;
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
    $scope.executionmsg = "DQ Group Restarted Successfully"
    notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.executionmsg
    $rootScope.$emit('notify', notify);
    CommonService.restartExec("dqgroupExec", $stateParams.id, $stateParams.version).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
    }
    $scope.refreshRuleGroupExecFunction();
  }

  $scope.submitDownload = function () {
    var uuid = $scope.download.data.uuid;
    var version = $scope.download.data.version;
    var url = $location.absUrl().split("app")[0];
    $('#downloadSample').modal("hide");
    $http({
      method: 'GET',
      url: url + "dataqual/download?action=view&dataQualExecUUID=" + uuid + "&dataQualExecVersion=" + version + "&rows=" + $scope.download.rows + "&format=" + $scope.download.selectFormate + "&resultType=" + $scope.download.resultType,
      responseType: 'arraybuffer'
    }).success(function (data, status, headers) {
      headers = headers();
      $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;

      var filename = headers['filename'];
      var contentType = headers['content-type'];

      var linkElement = document.createElement('a');
      try {
        var blob = new Blob([data], {
          type: contentType
        });
        var url = window.URL.createObjectURL(blob);
        linkElement.setAttribute('href', url);
        linkElement.setAttribute("download", filename);
        var clickEvent = new MouseEvent("click", {
          "view": window,
          "bubbles": true,
          "cancelable": false
        });
        linkElement.dispatchEvent(clickEvent);
      } catch (ex) {
        console.log(ex);
      }
    }).error(function (data) {
      console.log(data);
    });
  }

  $scope.downloadFile = function (data) {
    if ($scope.isD3RuleEexecGraphShow) {
      return false;
    }
    $scope.download.data = data;
    $('#downloadSample').modal({
      backdrop: 'static',
      keyboard: false
    });
  };


}); //End DQRuleResultController