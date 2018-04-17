RuleModule = angular.module('RuleModule');
RuleModule.controller('DetailRuleController', function (privilegeSvc, $state, $cookieStore, $stateParams, $rootScope, $scope, $timeout, $filter, RuleService, dagMetaDataService) {
  $scope.mode = "false";
  $scope.rule = {};
  $scope.rule.versions = []
  $scope.rulecompare = null;
  $scope.expressionAll = false;
  $scope.showFrom = true;
  $scope.showGraphDiv=false;
  $scope.ruleSourceTypes = ["datapod", "relation", "dataset", "rule"];
  $scope.logicalOperator = [" ", "OR", "AND"];
  $scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
  $scope.lshType = ["string", "datapod", 'formula'];
  $scope.matType = ["string", 'formula'];
  $scope.sourceTypes = ["datapod", "expression"]
  $scope.sourceAttributeTypes = [{
    "text": "string",
    "caption": "string"
  },
  {
    "text": "datapod",
    "caption": "attribute"
  },
  {
    "text": "expression",
    "caption": "expression"
  },
  {
    "text": "formula",
    "caption": "formula"
  }, {
    "text": "function",
    "caption": "function"
  },
  {
    "text": "paramlist",
    "caption": "paramlist"
  },
  ]

  $scope.islhsSimple = true;
  $scope.isrhsSimple = true;
  $scope.islhsDatapod = false;
  $scope.islhsFormula = false;
  $scope.isrhsDatapod = false;
  $scope.isrhsFormula = false;
  $scope.filterTableArray = null;
  $scope.expressionTableArray = null;
  $scope.attributeTableArray = null;
  $scope.isButtonEnaple = true;
  $scope.checkAll = false;
  $scope.showModal1 = false;
  $scope.isData = false;
  $scope.ruleData = null
  $scope.isShowRuleExec = false;
  $scope.isShowRuleResult = false;
  $scope.ruleRelation = {};
  $scope.datapodDetaildColumn = [];
  $scope.count = 0;
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
  }
  else if ($stateParams.mode == 'false') {
    $scope.isEdit = true;
    $scope.isversionEnable = true;
    $scope.isAdd = false;
  }
  else {
    $scope.isAdd = true;
  }
  $scope.isshowmodel = false;
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['rule'] || [];
  $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges['rule'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  });

  $scope.showPage = function () {
    $scope.showFrom = true
    $scope.showGraphDiv = false
  }

  $scope.showGraph = function (uuid, version) {
    $scope.showFrom = false
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit = function (uuid, version) {
    $scope.showPage()
    $state.go('createrules', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }
  $scope.showview = function (uuid, version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createrules', {
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
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['rule'].listState
      $scope.statedetail.params = {}
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  $scope.onChangeTargetDatapod = function () {
    $scope.ruletargetdatapodattribute = null;
    if ($scope.ruletargetdatapod.uuid != "1") {
      RuleService.getAttributesByDatapod($scope.ruletargetdatapod.uuid, "datapod").then(function (response) {
        onSuccess(response.data)
      })
      var onSuccess = function (response) {
        $scope.ruletargetdatapodattribute = response
      }

    }
  }

 


  $scope.selectSourceType = function () {
    $scope.attributeTableArray = null
    if ($scope.filterTableArray != null) {
      $scope.showModal1 = true;
    }
    $scope.datapodAttributeTags = null;
    $scope.lhsdatapodattributefilter = null;
    RuleService.getAllLatestActive($scope.rulsourcetype).then(function (response) {
      onSuccessRelation(response.data)
    });
    var onSuccessRelation = function (response) {
      $scope.ruleRelation = response
      if ($scope.filterTableArray != null) {
        $scope.showModal1 = true;
      }
      if ($scope.ruleRelation != null) {
        RuleService.getAllAttributeBySource($scope.ruleRelation.defaultoption.uuid, $scope.rulsourcetype).then(function (response) {
          onSuccess(response.data)
        });
        var onSuccess = function (response) {
          $scope.isButtonEnaple = false;
          $scope.lhsdatapodattributefilter = response;
          $scope.sourcedatapodattribute = response
          $scope.loadSourceAttribue = response;
          if ($scope.filterTableArray != null) {
            for (var i = 0; i < $scope.filterTableArray.length; i++) {
              var filterinfo = {};
              filterinfo.logicalOperator = $scope.logicalOperator[1];
              filterinfo.operator = $scope.operator[0];
              filterinfo.lhsFilter = $scope.lhsdatapodattributefilter[i]
              $scope.filterTableArray[i] = filterinfo
            }
          }
        } //End getAllAttributeBySources
      } //End If
    } //End getAllLatestActive
  } //End selectSourceType



  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true";
    $scope.mode = $stateParams.mode;
    $scope.ruleRelation;
    $scope.isDependencyShow = true;
    RuleService.getAllVersionByUuid($stateParams.id, "rule").then(function (response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var ruleversion = {};
        ruleversion.version = response[i].version;
        $scope.rule.versions[i] = ruleversion;
      }

    }
    RuleService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'ruleview').then(function (response) {
      onSuccess(response.data)
    });
    var onSuccess = function (response) {
      $scope.ruleData = response.ruledata
      $scope.rulecompare = response.ruledata;
      $scope.tags = response.ruledata.tags

      var defaultversion = {};
      defaultversion.version = response.ruledata.version;
      defaultversion.uuid = response.ruledata.uuid;
      $scope.rule.defaultVersion = defaultversion;
      if (response.filterInfo.length > 0) {
        $scope.filterTableArray = response.filterInfo
      }
      $scope.attributeTableArray = response.sourceAttributes
      $scope.datapodAttributeTags = response.sourceAttributes
      $scope.rulsourcetype = response.ruledata.source.ref.type;
      RuleService.getAllLatestActive(response.ruledata.source.ref.type).then(function (response) {
        onSuccessRelation(response.data)
      });
      var onSuccessRelation = function (response) {
        $scope.ruleRelation = response
        var defaultoption = {};
        defaultoption.uuid = $scope.ruleData.source.ref.uuid;
        defaultoption.name = $scope.ruleData.source.ref.name;
        $scope.ruleRelation.defaultoption = defaultoption;
      }
      RuleService.getAllLatest("paramlist").then(function (response) {
        onSuccessGetAllLatestParamList(response.data)
      });
      var onSuccessGetAllLatestParamList = function (response) {
        $scope.allparamlist = response
        if ($scope.ruleData.paramList != null) {
          var defaultoption = {};
          defaultoption.uuid = $scope.ruleData.paramList.ref.uuid;
          defaultoption.name = $scope.ruleData.paramList.ref.name;
          $scope.allparamlist.defaultoption = defaultoption;
          $scope.getOneByUuidParamList();
        } else {
          $scope.allparamlist.defaultoption = null;
        }
      }

      RuleService.getExpressionByType(response.ruledata.source.ref.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessExpression(response.data)
      });
      var onSuccessExpression = function (response) {
        $scope.ruleLodeExpression = response
      }
      $scope.expressionTableArray = response.expressionlist
      RuleService.getFormulaByType(response.ruledata.source.ref.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessFormula(response.data)
      });
      var onSuccessFormula = function (response) {
        $scope.ruleLodeFormula = response.data
      }
      RuleService.getAllLatestFunction("function", "N").then(function (response) {
        onSuccessFunction(response.data)
      });
      var onSuccessFunction = function (response) {
        $scope.ruleLodeFunction = response
      }
      RuleService.getAllAttributeBySource(response.ruledata.source.ref.uuid, response.ruledata.source.ref.type).then(function (response) {
        onSuccess(response.data)
      })
      var onSuccess = function (response) {
        $scope.lhsdatapodattributefilter = response;
        $scope.loadSourceAttribue = response;
        $scope.sourcedatapodattribute = response;
      }

    }
  }
  else {
    $scope.showactive = "false"
    RuleService.getAllLatest("paramlist").then(function (response) {
      onSuccessGetAllLatestParamList(response.data)
    });
    var onSuccessGetAllLatestParamList = function (response) {
      $scope.allparamlist = response
      if (response)
        $scope.allparamlist.defaultoption = null;
      $scope.getOneByUuidParamList();
    }
  } //End Else

  $scope.selectVersion = function () {
    $scope.attributeTableArray = null;
    $scope.myform.$dirty = false;
    RuleService.getOneByUuidAndVersion($scope.rule.defaultVersion.uuid, $scope.rule.defaultVersion.version, 'ruleview').then(function (response) {
      onSuccess(response.data)
    });
    var onSuccess = function (response) {
      $scope.ruleData = response.ruledata
      $scope.rulecompare = response.ruledata;
      $scope.tags = response.ruledata.tags
      var defaultversion = {};
      defaultversion.version = response.ruledata.version;
      defaultversion.uuid = response.ruledata.uuid;
      $scope.rule.defaultVersion = defaultversion;
      if (response.filterInfo.length > 0) {
        $scope.filterTableArray = response.filterInfo
      } else {
        $scope.filterTableArray = null;
      }
      $scope.attributeTableArray = response.sourceAttributes
      //consoleconsole.log(JSON.stringify($scope.attributeTableArray))
      $scope.datapodAttributeTags = response.sourceAttributes
      $scope.rulsourcetype = response.ruledata.source.ref.type;
      RuleService.getAllLatest(response.ruledata.source.ref.type).then(function (response) {
        onSuccessRelation(response.data)
      });
      var onSuccessRelation = function (response) {
        $scope.ruleRelation = response
        var defaultoption = {};
        defaultoption.uuid = $scope.ruleData.source.ref.uuid;
        defaultoption.name = $scope.ruleData.source.ref.name;
        $scope.ruleRelation.defaultoption = defaultoption;
      }
      RuleService.getAllLatest("paramlist").then(function (response) {
        onSuccessGetAllLatestParamList(response.data)
      });
      var onSuccessGetAllLatestParamList = function (response) {
        $scope.allparamlist = response
        if ($scope.ruleData.paramList != null) {
          var defaultoption = {};
          defaultoption.uuid = $scope.ruleData.paramList.ref.uuid;
          defaultoption.name = $scope.ruleData.paramList.ref.name;
          $scope.allparamlist.defaultoption = defaultoption;
          $scope.getOneByUuidParamList();
        } else {
          $scope.allparamlist.defaultoption = null;
        }
      }
      RuleService.getExpressionByType(response.ruledata.source.ref.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessExpression(response.data)
      });
      var onSuccessExpression = function (response) {
        $scope.ruleLodeExpression = response
      }
      $scope.expressionTableArray = response.expressionlist
      RuleService.getAllLatestFunction("function", "N").then(function (response) {
        onSuccessFunction(response.data)
      });
      var onSuccessFunction = function (response) {
        $scope.ruleLodeFunction = response
      }
      RuleService.getFormulaByType(response.ruledata.source.ref.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessFormula(response.data)
      });
      var onSuccessFormula = function (response) {
        $scope.ruleLodeFormula = response.data
      }
      RuleService.getAllAttributeBySource(response.ruledata.source.ref.uuid, response.ruledata.source.ref.type, $scope.rulsourcetype).then(function (response) {
        onSuccess(response.data)
      })
      var onSuccess = function (response) {
        $scope.lhsdatapodattributefilter = response;
        $scope.loadSourceAttribue = response;
        $scope.sourcedatapodattribute = response;
      }

    } //End getOneByUuidAndVersion
  } //End selectVersion

  $scope.hideInputbox = function (index) {
    $scope.expressionTableArray[index].showInputbox = false;
    $scope.expressionTableArray[index].tddetail = "true";
  }

  $scope.onEnterhide = function (e, index) {
    if (e.which === 13) {
      $scope.expressionTableArray[index].showInputbox = false;
      $scope.expressionTableArray[index].tddetail = "true"
    }

  }

  $scope.toggleTooltip = function (e, index) {
    e.stopPropagation();
    $scope.expressionTableArray[index].showInputbox = !$scope.expressionTableArray[index].showInputbox;
    $scope.expressionTableArray[index].tddetail = !$scope.expressionTableArray[index].tddetail;
  }

  $scope.countContinue = function () {
    $scope.continueCount = $scope.continueCount + 1;
    if ($scope.continueCount >= 4) {
      $scope.isSubmitShow = true;
    } else {
      $scope.isSubmitShow = false;
    }
  }
  $scope.modelExecute = function (modeldetail) {
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
    //console.log(JSON.stringify(execParams));
    RuleService.executeRuleWithParams(modeldetail.uuid, modeldetail.version, execParams).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
     // console.log(JSON.stringify(response));
     $scope.dataLoading = false;
      $scope.saveMessage = "Rule Saved and Submited Successfully"
      notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.saveMessage
      $scope.$emit('notify', notify);
      $scope.okrulesave();
    }
  }
  $scope.onSelectparamSet = function () {
    //console.log(JSON.stringify($scope.paramsetdata));
    var paramSetjson = {};
    var paramInfoArray = [];
    if ($scope.paramsetdata != null) {
      for (var i = 0; i < $scope.paramsetdata.paramInfo.length; i++) {
        var paramInfo = {};
        paramInfo.paramSetId = $scope.paramsetdata.paramInfo[i].paramSetId
        var paramSetValarray = [];
        for (var j = 0; j < $scope.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
          var paramSetValjson = {};
          paramSetValjson.paramId = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
          paramSetValjson.paramName = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
          paramSetValjson.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
          paramSetValjson.ref = $scope.paramsetdata.paramInfo[i].paramSetVal[j].ref;
          paramSetValarray[j] = paramSetValjson;
          paramInfo.paramSetVal = paramSetValarray;
          paramInfo.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
        }
        paramInfoArray[i] = paramInfo;
      }
      $scope.paramtablecol = paramInfoArray[0].paramSetVal;
      $scope.paramtable = paramInfoArray;
      paramSetjson.paramInfoArray = paramInfoArray;
      $scope.isTabelShow = true;
    } else {
      linkElement.setAttribute("download", filename);
      $scope.isTabelShow = false;
    }
  }

  $scope.addRow = function () {
    if ($scope.paramtable == null) {
      $scope.paramtable = [];
    }
    var paramjson = {}
    paramjson.paramId = $scope.paramtable.length;
    $scope.paramtable.splice($scope.paramtable.length, 0, paramjson);
  }

  $scope.selectAllRow = function () {
    angular.forEach($scope.paramtable, function (stage) {
      stage.selected = $scope.selectallattribute;
    });
  }
  $scope.removeRow = function () {
    var newDataList = [];
    $scope.selectallattribute = false;
    angular.forEach($scope.paramtable, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    $scope.paramtable = newDataList;
  }

  $scope.changeCheckboxExecution = function () {
    if ($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption != null) {
      RuleService.getParamSetByParamList($scope.allparamlist.defaultoption.uuid, "").then(function (response) {
        onSuccessGetParamSetByParmLsit(response.data)
      });
      var onSuccessGetParamSetByParmLsit = function (response) {
        $scope.allparamset = response
        $scope.isShowExecutionparam = true;

      }
    } else {
      $scope.isShowExecutionparam = false;
      $scope.allparamset = null;
    }
  }

  $scope.countBack = function () {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
    $scope.isShowRuleExec = false;
    $scope.isShowRuleResult = false;
  }

  $scope.selectOption = function () {
    $scope.attributeTableArray = [];
    if ($scope.filterTableArray != null) {
      $scope.showModal1 = true;
    }
    $scope.datapodAttributeTags = null;
    RuleService.getAllAttributeBySource($scope.ruleRelation.defaultoption.uuid, $scope.rulsourcetype).then(function (response) {
      onSuccess(response.data)
    });
    var onSuccess = function (response) {
      $scope.isButtonEnaple = false;
      $scope.lhsdatapodattributefilter = response;
      $scope.sourcedatapodattribute = response
      $scope.loadSourceAttribue = response;
      if ($scope.filterTableArray != null) {
        for (var i = 0; i < $scope.filterTableArray.length; i++) {
          var filterinfo = {};
          filterinfo.logicalOperator = $scope.logicalOperator[1];
          filterinfo.operator = $scope.operator[0];
          filterinfo.lhsFilter = $scope.lhsdatapodattributefilter[i]
          $scope.filterTableArray[i] = filterinfo
        }
      } 
    }
  }


  $scope.clear = function () {
    $scope.datapodAttributeTags = null;
  };

  $scope.loadDatapodAttributes = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.loadSourceAttribue, query);
    });
  };

  $scope.checkAllFilterRow = function () {
    angular.forEach($scope.filterTableArray, function (filter) {
      filter.selected = $scope.checkAll;
    });
  }

  $scope.addRowFilter = function () {
    var filterinfo = {};
    if ($scope.filterTableArray == null) {
      $scope.filterTableArray = [];
    }
    filterinfo.logicalOperator = $scope.logicalOperator[0];
    filterinfo.lhsFilter = $scope.lhsdatapodattributefilter[0]
    $scope.filterTableArray.splice($scope.filterTableArray.length, 0, filterinfo);
  }

  $scope.removeFilterRow = function () {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.filterTableArray, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    $scope.filterTableArray = newDataList;
  }

  $scope.onChangeSourceAttribute = function (type, index) {
    if (type == "string") {
      $scope.attributeTableArray[index].isSourceAtributeSimple = true;
      $scope.attributeTableArray[index].sourcesimple = "''";
      $scope.attributeTableArray[index].isSourceAtributeDatapod = false;
      $scope.attributeTableArray[index].isSourceAtributeFormula = false;
      $scope.attributeTableArray[index].isSourceAtributeExpression = false;
      $scope.attributeTableArray[index].isSourceAtributeFunction = false;
      $scope.attributeTableArray[index].isSourceAtributeParamList = false;
    } else if (type == "datapod") {
      $scope.attributeTableArray[index].isSourceAtributeSimple = false;
      $scope.attributeTableArray[index].isSourceAtributeDatapod = true;
      $scope.attributeTableArray[index].isSourceAtributeFormula = false;
      $scope.attributeTableArray[index].isSourceAtributeExpression = false;
      $scope.attributeTableArray[index].isSourceAtributeFunction = false;
      $scope.attributeTableArray[index].isSourceAtributeParamList = false;
    } else if (type == "formula") {

      $scope.attributeTableArray[index].isSourceAtributeSimple = false;
      $scope.attributeTableArray[index].isSourceAtributeDatapod = false;
      $scope.attributeTableArray[index].isSourceAtributeFormula = true;
      $scope.attributeTableArray[index].isSourceAtributeExpression = false;
      $scope.attributeTableArray[index].isSourceAtributeFunction = false;
      $scope.attributeTableArray[index].isSourceAtributeParamList = false;
      RuleService.getFormulaByType($scope.ruleRelation.defaultoption.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessExpression(response.data)
      });
      var onSuccessExpression = function (response) {
        $scope.ruleLodeFormula = response.data
      }
    } else if (type == "expression") {
      $scope.attributeTableArray[index].isSourceAtributeSimple = false;
      $scope.attributeTableArray[index].isSourceAtributeDatapod = false;
      $scope.attributeTableArray[index].isSourceAtributeFormula = false;
      $scope.attributeTableArray[index].isSourceAtributeExpression = true;
      $scope.attributeTableArray[index].isSourceAtributeFunction = false;
      $scope.attributeTableArray[index].isSourceAtributeParamList = false;
      RuleService.getExpressionByType($scope.ruleRelation.defaultoption.uuid, $scope.rulsourcetype).then(function (response) {
        onSuccessExpression(response.data)
      });
      var onSuccessExpression = function (response) {
        $scope.ruleLodeExpression = response
      }
    } else if (type == "function") {
      $scope.attributeTableArray[index].isSourceAtributeSimple = false;
      $scope.attributeTableArray[index].isSourceAtributeDatapod = false;
      $scope.attributeTableArray[index].isSourceAtributeFormula = false;
      $scope.attributeTableArray[index].isSourceAtributeExpression = false;
      $scope.attributeTableArray[index].isSourceAtributeFunction = true;
      $scope.attributeTableArray[index].isSourceAtributeParamList = false;
      RuleService.getAllLatestFunction("function", "N").then(function (response) {
        onSuccessExpression(response.data)
      });
      var onSuccessExpression = function (response) {
        $scope.ruleLodeFunction = response
      }
    } else if (type == "paramlist") {
      $scope.attributeTableArray[index].isSourceAtributeSimple = false;
      $scope.attributeTableArray[index].isSourceAtributeDatapod = false;
      $scope.attributeTableArray[index].isSourceAtributeFormula = false;
      $scope.attributeTableArray[index].isSourceAtributeExpression = false;
      $scope.attributeTableArray[index].isSourceAtributeFunction = false;
      $scope.attributeTableArray[index].isSourceAtributeParamList = true;
      $scope.getOneByUuidParamList();
    }
  }

  $scope.getOneByUuidParamList = function () {

    if ($scope.allparamlist && $scope.allparamlist.defaultoption != null) {
      RuleService.getOneByUuid($scope.allparamlist.defaultoption.uuid, "paramlist").then(function (response) {
        onSuccessParamList(response.data)
      });

      var onSuccessParamList = function (response) {
        var paramsArray = [];
        for (var i = 0; i < response.params.length; i++) {
          var paramsjson = {};
          paramsjson.uuid = response.uuid;
          paramsjson.name = response.name + "." + response.params[i].paramName;
          paramsjson.attrId = response.params[i].paramId
          paramsjson.paramName = response.params[i].paramName
          paramsArray[i] = paramsjson
        }
        $scope.ruleLodeParamList = paramsArray
      }
    }
  }

  $scope.onChangeAttributeDatapod = function (data, index) {
    $scope.attributeTableArray[index].name = data.name
  }
  $scope.onChangeFormula = function (data, index) {
    $scope.attributeTableArray[index].name = data.name
  }
  $scope.onChangeExpression = function (data, index) {
    $scope.attributeTableArray[index].name = data.name
  }

  $scope.onChangeAttributeParamlist = function (data, index) {
    $scope.attributeTableArray[index].name = data.paramName
  }

  $scope.checkAllAttributeRow = function () {
    angular.forEach($scope.attributeTableArray, function (attribute) {
      attribute.selected = $scope.checkAllAttribute;
    });
  }

  $scope.addAttribute = function () {
    if ($scope.attributeTableArray == null) {
      $scope.attributeTableArray = [];
    }
    var len = $scope.attributeTableArray.length + 1
    var attrivuteinfo = {};
    attrivuteinfo.name = "attribute" + len;
    attrivuteinfo.sourceAttributeType = $scope.sourceAttributeTypes[0];
    attrivuteinfo.isSourceAtributeSimple = true;
    attrivuteinfo.sourcesimple = "''"
    attrivuteinfo.isSourceAtributeDatapod = false;
   // console.log(JSON.stringify(attrivuteinfo))
    $scope.attributeTableArray.splice($scope.attributeTableArray.length, 0, attrivuteinfo);
  }

  $scope.removeAttribute = function () {
    $scope.checkAllAttribute = false;
    var newDataList = [];
    angular.forEach($scope.attributeTableArray, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    $scope.attributeTableArray = newDataList;
  }

  $scope.hideOk = function (m) {
    if (m === 1) {
      $scope.showModal1 = false;
      $scope.filterTableArray = [];
    }
  }

  $scope.hideCancel = function (m) {
    if (m === 1) {
      $scope.showModal1 = false;
    }
  }

  $scope.modalOneShown = function () {
    //console.log('model one shown');
  }

  $scope.modalOneHide = function () {
   // console.log('model one hidden');
  }

  $scope.ruleExecutionOk = function () {
    $('#RoleModal').modal('hide');
    $('#rulecreateExecute').modal('hide');
  }

  $scope.okrulesave = function () {
    $('#rulesave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function () {
        $state.go('viewrule');
      }, 2000);
    }
  }


  $scope.submitRule = function () {
    var ruleJson = {}
    $scope.dataLoading = true;
    $scope.isSubmitDisabled=true;
    if ($scope.ruleData != null) {
      ruleJson.uuid = $scope.ruleData.uuid
    }
    ruleJson.name = $scope.ruleData.name;
    ruleJson.desc = $scope.ruleData.desc;
    ruleJson.active = $scope.ruleData.active;
    ruleJson.published = $scope.ruleData.published;


    if ($scope.rulecompare == null) {
      ruleJson.filterChg = "y";
    } else {
      ruleJson.mapInfo = $scope.rulecompare.mapInfo
    }
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    var source = {};
    var ref = {};
    ref.type = $scope.rulsourcetype
    ref.uuid = $scope.ruleRelation.defaultoption.uuid;
    ref.version = $scope.ruleRelation.defaultoption.version;
    source.ref = ref;
    ruleJson.source = source;
    if ($scope.allparamlist && $scope.allparamlist.defaultoption != null) {
      var paramlist = {};
      var ref = {};
      ref.type = "paramlist";
      ref.uuid = $scope.allparamlist.defaultoption.uuid;
      paramlist.ref = ref;
      ruleJson.paramList = paramlist;
    } else {
      ruleJson.paramList = null;
    }
    var filterInfoArray = [];
    var filter = {}
    if ($scope.rulecompare != null && $scope.rulecompare.filter != null) {
      filter.uuid = $scope.rulecompare.filter.uuid;
      filter.name = $scope.rulecompare.filter.name;
      filter.createdBy = $scope.rulecompare.filter.createdBy;
      filter.createdOn = $scope.rulecompare.filter.createdOn;
      filter.active = $scope.rulecompare.filter.active;
      filter.tags = $scope.rulecompare.filter.tags;
      filter.desc = $scope.rulecompare.filter.desc;
      filter.dependsOn = $scope.rulecompare.filter.dependsOn;
    }
    if ($scope.filterTableArray != null) {
      if ($scope.filterTableArray.length > 0) {
        for (var i = 0; i < $scope.filterTableArray.length; i++) {
          if ($scope.rulecompare != null && $scope.rulecompare.filter != null && $scope.rulecompare.filter.filterInfo.length == $scope.filterTableArray.length) {
            if ($scope.rulecompare.filter.filterInfo[i].operand[0].attributeId != $scope.filterTableArray[i].lhsFilter.attributeId ||
              $scope.filterTableArray[i].logicalOperator != $scope.rulecompare.filter.filterInfo[i].logicalOperator ||
              $scope.filterTableArray[i].filtervalue != $scope.rulecompare.filter.filterInfo[i].operand[1].value ||
              $scope.filterTableArray[i].operator != $scope.rulecompare.filter.filterInfo[i].operator) {

              ruleJson.filterChg = "y";

            } else {
              ruleJson.filterChg = "n";
            }
          } else {
            ruleJson.filterChg = "y";
          }
          var filterInfo = {};
          var operand = [];
          var operandfirst = {};
          var reffirst = {};
          var operandsecond = {};
          var refsecond = {};
          if ($scope.rulsourcetype == "dataset") {
            reffirst.type = "dataset";
          } else {
            reffirst.type = "datapod"
          }
          reffirst.uuid = $scope.filterTableArray[i].lhsFilter.uuid
          operandfirst.ref = reffirst;
          operandfirst.attributeId = $scope.filterTableArray[i].lhsFilter.attributeId
          operand[0] = operandfirst;
          refsecond.type = "simple";
          operandsecond.ref = refsecond;
          if (typeof $scope.filterTableArray[i].filtervalue == "undefined") {
            operandsecond.value = "";
          } else {
            operandsecond.value = $scope.filterTableArray[i].filtervalue
          }
          operand[1] = operandsecond;
          if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
            filterInfo.logicalOperator = ""
          } else {
            filterInfo.logicalOperator = $scope.filterTableArray[i].logicalOperator
          }
          filterInfo.operator = $scope.filterTableArray[i].operator
          filterInfo.operand = operand;
          filterInfoArray[i] = filterInfo;
        }
        filter.filterInfo = filterInfoArray;
        ruleJson.filter = filter;
      } else {
        ruleJson.filter = null;
        ruleJson.filterChg = "y";
      }
    } else {
      ruleJson.filter = null;
      ruleJson.filterChg = "y";
    }
    ruleJson.tags = tagArray;

    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var sourceAttributesArray = [];
    if ($scope.attributeTableArray) {
      for (var l = 0; l < $scope.attributeTableArray.length; l++) {
        attributeinfo = {}
        attributeinfo.attrSourceId = l;
        attributeinfo.attrSourceName = $scope.attributeTableArray[l].name
        var ref = {};
        var sourceAttr = {};
        if ($scope.attributeTableArray[l].sourceAttributeType.text == "string") {
          ref.type = "simple";
          sourceAttr.ref = ref;
          sourceAttr.value = $scope.attributeTableArray[l].sourcesimple;
          attributeinfo.sourceAttr = sourceAttr;
        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "datapod") {
          if ($scope.rulsourcetype == "dataset") {
            ref.type = "dataset";
            ref.uuid = $scope.ruleRelation.defaultoption.uuid;
          } else if ($scope.rulsourcetype == "rule") {
            ref.type = "rule";
            ref.uuid = $scope.ruleRelation.defaultoption.uuid;
          } else {
            ref.type = "datapod";
            ref.uuid = $scope.attributeTableArray[l].sourcedatapod.uuid;
          }
          sourceAttr.ref = ref;
          sourceAttr.attrId = $scope.attributeTableArray[l].sourcedatapod.attributeId;
          attributeinfo.sourceAttr = sourceAttr;
        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "expression") {

          ref.type = "expression";
          ref.uuid = $scope.attributeTableArray[l].sourceexpression.uuid;
          sourceAttr.ref = ref;
          attributeinfo.sourceAttr = sourceAttr;
        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "formula") {

          ref.type = "formula";
          ref.uuid = $scope.attributeTableArray[l].sourceformula.uuid;
          sourceAttr.ref = ref;
          attributeinfo.sourceAttr = sourceAttr;

        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "function") {
          ref.type = "function";
          ref.uuid = $scope.attributeTableArray[l].sourcefunction.uuid;
          sourceAttr.ref = ref;
          attributeinfo.sourceAttr = sourceAttr;
        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "paramlist") {
          ref.type = "paramlist";
          ref.uuid = $scope.attributeTableArray[l].sourceparamlist.uuid;
          sourceAttr.ref = ref;
          sourceAttr.attrId = $scope.attributeTableArray[l].sourceparamlist.attrId
          attributeinfo.sourceAttr = sourceAttr;
        }

        sourceAttributesArray[l] = attributeinfo
      }
    }
    ruleJson.attributeInfo = sourceAttributesArray
    console.log("Rule JSON" + JSON.stringify(ruleJson))

    RuleService.submit(ruleJson, 'ruleview').then(function (response) {
      onSuccess(response.data)
    }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      if (options.execution == "YES") {
        RuleService.getOneById(response.data, "rule").then(function (response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function (result) {
          $scope.modelExecute(result.data);
        }
      } //End if
      else {
        $scope.dataLoading = false;
        $scope.saveMessage = "Rule Saved Successfully"
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okrulesave();
      } //End else
    } //End Submit Api Function
    var onError = function (response) {
      notify.type = 'error',
      notify.title = 'Error',
      notify.content = "Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  }

});


RuleModule.controller('DetailRuleGroupController', function ($state, $timeout, $filter, $stateParams, $rootScope, $scope, RuleGroupService, privilegeSvc) {
  $scope.select = 'rules group';
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
  }
  else if ($stateParams.mode == 'false') {
    $scope.isEdit = true;
    $scope.isversionEnable = true;
    $scope.isAdd = false;
  }
  else {
    $scope.isAdd = true;
  }
  $scope.showForm = true;
  $scope.showGraphDiv=false;
  $scope.mode = " ";
  $scope.rulegroup = {};
  $scope.rulegroup.versions = []
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.privileges = privilegeSvc.privileges['rulegroup'] || [];
  $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges['rulegroup'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  });

  $scope.showPage = function () {
    $scope.showForm = true;
    $scope.showGraphDiv = false;

  }

  $scope.showGraph = function (uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv =true;
  }

  $scope.enableEdit = function (uuid, version) {
    $scope.showPage()
    $state.go('createrulesgroup', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }
  $scope.showview = function (uuid, version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createrulesgroup', {
        id: uuid,
        version: version,
        mode: 'true'
      });
    }
  }
 
  
  RuleGroupService.getAllLatest('rule').then(function (response) {
    onSuccess(response.data)
  });
  var onSuccess = function (response) {
    var rullArray = [];
    for (var i = 0; i < response.data.length; i++) {
      var rulljosn = {};
      rulljosn.uuid = response.data[i].uuid;
      rulljosn.id = response.data[i].uuid //+ "_" + response.data[i].version
      rulljosn.name = response.data[i].name;
      rulljosn.version = response.data[i].version;
      rullArray[i] = rulljosn;
    }
    $scope.rullall = rullArray;
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    RuleGroupService.getAllVersionByUuid($stateParams.id, "rulegroup").then(function (response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var rulegroupversion = {};
        rulegroupversion.version = response[i].version;
        $scope.rulegroup.versions[i] = rulegroupversion;
      }

    }
    RuleGroupService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'rulegroup').then(function (response) {
      onsuccess(response.data)
    });
    var onsuccess = function (response) {
      //console.log(JSON.stringify(response))
      $scope.ruleGroupDetail = response;
      $scope.tags = response.tags;
      $scope.checkboxModelparallel = response.inParallel;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.rulegroup.defaultVersion = defaultversion;
      var ruleTagArray = [];
      for (var i = 0; i < response.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.ruleInfo[i].ref.uuid;
        ruletag.name = response.ruleInfo[i].ref.name;
        ruletag.id = response.ruleInfo[i].ref.uuid //+ "_" + response.ruleInfo[i].ref.version;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
  }

  $scope.selectVersion = function () {
    $scope.myform.$dirty = false;
    RuleGroupService.getOneByUuidAndVersion($scope.rulegroup.defaultVersion.uuid, $scope.rulegroup.defaultVersion.version, 'rulegroup').then(function (response) {
      onsuccess(response.data)
    });
    var onsuccess = function (response) {
      //console.log(JSON.stringify(response))
      $scope.ruleGroupDetail = response;
      $scope.tags = response.tags;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.rulegroup.defaultVersion = defaultversion;
      // $scope.checkboxModelparallel=
      var ruleTagArray = [];
      for (var i = 0; i < response.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.ruleInfo[i].ref.uuid;
        ruletag.name = response.ruleInfo[i].ref.name;
        ruletag.id = response.ruleInfo[i].ref.uuid //+ "_" + response.ruleInfo[i].ref.version;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
  }

  $scope.loadRules = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.rullall, query);
    });
  };

  $scope.okrulesave = function () {
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function () {
        $state.go('rulesgroup');
      }, 2000);

    }
  }

  $scope.submitRuleGroup = function () {
    $scope.dataLoading = true;
    $scope.isshowmodel = true;
    $scope.myform.$dirty = false;
    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var ruleGroupJson = {}
    ruleGroupJson.uuid = $scope.ruleGroupDetail.uuid;
    ruleGroupJson.name = $scope.ruleGroupDetail.name;
    ruleGroupJson.desc = $scope.ruleGroupDetail.desc;
    ruleGroupJson.active = $scope.ruleGroupDetail.active;
    ruleGroupJson.published = $scope.ruleGroupDetail.published;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;

      }
    }
    ruleGroupJson.tags = tagArray;
    var ruleInfoArray = [];
    for (var i = 0; i < $scope.ruleTags.length; i++) {
      var ruleInfo = {}
      var ref = {};
      ref.type = "rule"
      ref.uuid = $scope.ruleTags[i].uuid;
      /// ref.version=$scope.ruleTags[i].version;
      ruleInfo.ref = ref;
      ruleInfoArray[i] = ruleInfo;

    }
    ruleGroupJson.ruleInfo = ruleInfoArray;
    ruleGroupJson.inParallel = $scope.checkboxModelparallel
    console.log(JSON.stringify(ruleGroupJson))
    RuleGroupService.submit(ruleGroupJson, "rulegroup").then(function (response) {
      onSuccess(response.data)
    }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
     
      if (options.execution == "YES") {
        RuleGroupService.getOneById(response.data, "rulegroup").then(function (response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function (result) {
          RuleGroupService.ruleGroupExecute(result.data.uuid, result.data.version).then(function (response){onSuccess(response.data)},function (response){onError(response.data)});
          var onSuccess = function (response) {
            console.log(JSON.stringify(response))
            $scope.dataLoading = false;
            $scope.saveMessage = "Rule Group Saved and Submitted Successfully"
            notify.type = 'success',
            notify.title = 'Success',
            notify.content = $scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okrulesave();
          }
          var onError=function(){
            $scope.dataLoading = false;
            $scope.okrulesave();
          }
        }
      } //End If
      else {
        $scope.dataLoading = false;
        $scope.saveMessage = "Rule Group Saved Successfully"
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = $scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okrulesave();
      } //End else

    } //End Submit Api Function
    var onError = function (response) {
      notify.type = 'error',
      notify.title = 'Error',
      notify.content = "Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function
});


RuleModule.controller('ResultRuleController', function ($http, $log, dagMetaDataService, $filter, $state, $cookieStore, $stateParams, $location, $rootScope, $scope, ListRuleService, NgTableParams, uuid2, uiGridConstants, CommonService) {
  $scope.select = $stateParams.type;
  $scope.type = {
    text: $scope.select == 'rulegroupexec' ? 'rulegroup' : 'rule'
  };
  $scope.currentPage = 1;
  $scope.pageSize = 10;
  $scope.paginationPageSizes = [10, 25, 50, 75, 100],
    $scope.maxSize = 5;
  $scope.bigTotalItems = 175;
  $scope.bigCurrentPage = 1;
  $scope.sortdetail = [];
  $scope.colcount = 0;
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
  $scope.filteredRows = [];
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
  };
  $scope.getGridStyle = function () {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 80) + 'px';
    }
    else {
      style['height'] = "100px"
    }


    return style;
  }
  // $scope.gridOptionsRule.onRegisterApi = $scope.gridOptionsRuleGroup.onRegisterApi = function(gridApi) {
  //   $scope.gridApi = gridApi;
  //   $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  // };
  $scope.onClickRuleResult = function () {
    $scope.isRuleExec = true;
    $scope.isRuleResult = false;
    $scope.isD3RuleEexecGraphShow = false;
    if ($scope.type.text == "rulegroup") {
      $scope.isGraphRuleExec = false;
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
    $scope.getRuleExec({
      uuid: params.id,
      version: params.version
    });
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
    if ($scope.type.text == 'rule') {
      $scope.getRuleExec(data);
      return;
    }
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
    $scope.tableParams = new NgTableParams({}, {
      data: ""
    });
    $scope.cols = []
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
    $scope.getRuleExec($scope.ruleexecdetail)
  }

  $scope.RuleExecshowGraph = function () {
    $scope.isDataError = false;
    $scope.isD3RuleEexecGraphShow = true;
  }


  $scope.getRuleExec = function (data) {
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
    ListRuleService.getNumRowsbyExec(uuid, version, "ruleexec").then(function (response) {
      onSuccessGetNumRowsbyExec(response.data)
    });
    var onSuccessGetNumRowsbyExec = function (response) {
      $scope.totalItems = response.numRows;
      $scope.getResults(null);
      // $scope.pagination.totalItems = response.numRows;
      // if ($scope.pagination.ddlpageSize > response.numRows) {
      //   $scope.pagination.ddlpageSize = response.numRows
      // } else {
      //   $scope.pagination.ddlpageSize = 10;
      // }
    }
  } //End getRuleExec




  // $scope.pagination = {
  //   paginationPageSizes: [10, 25, 50, 75, 100, "All"],
  //   ddlpageSize: 10,
  //   pageNumber: 1,
  //   pageSize: 10,
  //   totalItems: 0,
  //   getTotalPages: function() {
  //     return Math.ceil(this.totalItems / this.pageSize);
  //   },
  //   pageSizeChange: function() {
  //     if (this.ddlpageSize == "All")
  //       this.pageSize = $scope.pagination.totalItems;
  //     else
  //       this.pageSize = this.ddlpageSize;
  //     this.pageNumber = 1
  //     $scope.getResults(null);
  //   },
  //   firstPage: function() {
  //     if (this.pageNumber > 1) {
  //       this.pageNumber = 1
  //       $scope.getResults(null);
  //     }
  //   },
  //   nextPage: function() {
  //     if (this.pageNumber < this.getTotalPages()) {
  //       this.pageNumber++;
  //       $scope.getResults(null);
  //     }
  //   },
  //   previousPage: function() {
  //     if (this.pageNumber > 1) {
  //       this.pageNumber--;
  //       $scope.getResults(null);
  //     }
  //   },
  //   lastPage: function() {
  //     if (this.pageNumber >= 1) {
  //       this.pageNumber = this.getTotalPages();
  //       $scope.getResults(null);
  //     }
  //   }
  // };

  $scope.gridOptions = {
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
      $scope.gridApiResule = gridApi;
      $scope.filteredRows = $scope.gridApiResule.core.getVisibleRows($scope.gridApiResule.grid);
      $scope.gridApiResule.core.on.sortChanged($scope, function (grid, sortColumns) {
        if (sortColumns.length > 0) {
          $scope.searchRequestId(sortColumns);
        }
        /* if (sortColumns.length == 0) {
         paginationOptions.sort = null;
        }
        else {
         paginationOptions.sort = sortColumns[0].sort.direction;
        }*/
      });
    }
  };
  $scope.refreshData = function () {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
  };

  $scope.searchRequestId = function (sortColumns) {
    var sortBy = sortColumns[0].name;

    var order = sortColumns[0].sort.direction;
    var result = {};
    result.sortBy = sortBy;
    result.order = order;
    if ($scope.sortdetail.length == 0) {

      var sortobj = {};
      sortobj.uuid = uuid2.newuuid();
      sortobj.colname = sortColumns[0].name;
      sortobj.order = sortColumns[0].sort.direction;
      sortobj.limit = $scope.pageSize;
      $scope.sortdetail[$scope.colcount] = sortobj;
      $scope.colcount = $scope.colcount + 1;
      result.requestId = sortobj.uuid;
      //offset = 0;
    } else {
      var idpresent = "N";
      for (var i = 0; i < $scope.sortdetail.length; i++) {
        if ($scope.sortdetail[i].colname == sortBy && $scope.sortdetail[i].order == order && $scope.sortdetail[i].limit == $scope.pageSize) {
          result.requestId = $scope.sortdetail[i].uuid;
          idpresent = "Y"
          break;
        }
      } //End For
      if (idpresent == "N") {
        var sortobj = {};
        sortobj.uuid = uuid2.newuuid();
        result.requestId = sortobj.uuid;
        sortobj.colname = sortColumns[0].name;
        sortobj.order = sortColumns[0].sort.direction;
        sortobj.limit = $scope.pageSize;
        $scope.sortdetail[$scope.colcount] = sortobj;
        $scope.colcount = $scope.colcount + 1;
        offset = 0;
      } //End Else Innder IF
    } //End IF Inner Else
    console.log(JSON.stringify($scope.sortdetail));
    $scope.showprogress = true;
    $scope.testgrid = false;
    $scope.getResults(result);
  }
  $scope.testgrid = false;
  $scope.pageChanged = function () {
    $scope.getResults(null)
    $log.log('Page changed to: ' + (($scope.currentPage - 1) * $scope.pageSize));
  };

  $scope.onPerPageChange = function () {
    $scope.currentPage = 1;
    $scope.getResults(null)
  }
  $scope.getResults = function (params) {
    $scope.to = ((($scope.currentPage - 1) * $scope.pageSize) + 1);
    if ($scope.totalItems < ($scope.pageSize * $scope.currentPage)) {
      $scope.gridOptions.datafrom = $scope.totalItems;
    } else {
      $scope.from = (($scope.currentPage) * $scope.pageSize);
    }

    $scope.gridOptions.columnDefs = [];
    var uuid = $scope.ruleExecUuid
    var version = $scope.ruleExecVersion;
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
    ListRuleService.getRuleResults(uuid, version, offset || 0, limit, requestId, sortBy, order).then(function (response) {
      getResult(response.data)
    }, function (response) {
      OnError(response.data)
    });
    var getResult = function (response) {
      angular.forEach(response.data[0], function (value, key) {
        var attribute = {};
        if (key == "rownum") {
          attribute.visible = false
        } else {
          attribute.visible = true
        }
        attribute.name = key
        attribute.displayName = key
        attribute.width = key.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
        $scope.gridOptions.columnDefs.push(attribute)
      });
      $scope.gridOptions.data = response.data;
      $scope.originalData = response.data;
      $scope.testgrid = true;
      $scope.showprogress = false;
    }
    var OnError = function (response) {
      $scope.showprogress = false;
      $scope.isDataError = true;
      $scope.datamessage = "Some Error Occurred"
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
      //$scope.refreshRuleGroupExecFunction();
    }
    $scope.refreshRuleGroupExecFunction();
  }
  $scope.downloadFile = function (data) {

    var uuid = data.uuid;
    var version = data.version;
    var url = $location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url: url + "rule/download?action=view&ruleExecUUID=" + uuid + "&ruleExecVersion=" + version,
      responseType: 'arraybuffer'
    }).success(function (data, status, headers) {
      headers = headers();
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
  };
}); //End RuleViewResultController
