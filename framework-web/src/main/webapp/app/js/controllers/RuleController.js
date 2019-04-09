RuleModule = angular.module('RuleModule');
RuleModule.controller('DetailRuleController', function (privilegeSvc, $state, $cookieStore, $stateParams, $rootScope, $scope, $timeout, $filter, RuleService, CommonFactory, dagMetaDataService, CommonService, CF_FILTER, $location, $anchorScroll, CF_SUCCESS_MSG, CF_GRID) {
  $scope.mode = "false";
  $scope.rule = {};
  $scope.rule.versions = []
  $scope.rulecompare = null;
  $scope.expressionAll = false;
  $scope.showFrom = true;
  $scope.showGraphDiv=false;
  $scope.ruleSourceTypes = ["datapod", "relation", "dataset", "rule"];
  $scope.logicalOperator = ["AND","OR"];
  $scope.spacialOperator=['<','>','<=','>=','=','!=','LIKE','NOT LIKE','RLIKE'];
  $scope.paramTypes=["paramlist","paramset"];
  $scope.rhsNA=['NULL',"NOT NULL"];
  $scope.isDestoryState = false; 
  $scope.operator = CF_FILTER.operator;//["=", "<", ">", "<=", ">=", "BETWEEN"];
  $scope.lhsType = [
		{ "text": "string", "caption": "string" },
		{ "text": "string", "caption": "integer"},
		{ "text": "datapod", "caption": "attribute"},
		{ "text": "formula", "caption": "formula"}];
	$scope.rhsType = [
		{ "text": "string", "caption": "string","disabled":false },
		{ "text": "string", "caption": "integer" ,"disabled":false },
		{ "text": "datapod", "caption": "attribute","disabled":false },
		{ "text": "formula", "caption": "formula","disabled":false },
    { "text": "dataset", "caption": "dataset" ,"disabled":false },
    { "text":  "paramlist", "caption": "paramlist" ,"disabled":false },
		{ "text": "function", "caption": "function" ,"disabled":false }]
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

  $scope.lshType = [
    { "text": "string", "caption": "string" },
    { "text": "datapod", "caption": "attribute" },
    { "text": "formula", "caption": "formula" }]
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
  $scope.ruleLodeFormula=null;
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
    $scope.isDragable = "false";
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
  }
  else if ($stateParams.mode == 'false') {
    $scope.isEdit = true;
    $scope.isversionEnable = true;
    $scope.isAdd = false;
    $scope.isPanelActiveOpen=true;
    $scope.isDragable = "true";
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
  }
  else {
    $scope.isAdd = true;
    $scope.isDragable = "true";
  }
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.isshowmodel = false;
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['rule'] || [];
  $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated', function (e, data) {
    $scope.privileges = privilegeSvc.privileges['rule'] || [];
    $scope.isPrivlage = $scope.privileges.indexOf('Edit') == -1;
  });
  $scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
		var onSuccessGetLovByType = function (response) {
			$scope.lobTag=response[0].value
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
  $scope.checkIsInrogess=function(){
		if($scope.isEditInprogess || $scope.isEditVeiwError){
		return false;
		}
  }
  $scope.showPage = function () {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showFrom = true
    $scope.showGraphDiv = false
  }
  $scope.showHome=function(uuid, version,mode){
    if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createrules', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
  $scope.showGraph = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showFrom = false
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit = function (uuid, version) {
    if($scope.isPrivlage || $scope.ruleData.locked =="Y"){
      return false;
    } 
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showPage()
    $state.go('createrules', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }
  $scope.showview = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
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
  
var confirmDialog = function(newVal, yes, no) {
    setTimeout(function() {
      if (typeof $stateParams.id != "undefined") {
        $scope.showModal1 = true;
      }

      $scope.hideOk=function(value){
        $scope.showModal1 = false;
        yes();
      }
      $scope.hideCancel=function(value){
        $scope.showModal1 = false; 
        no();
      }
    }, 0);
  }
   
  $scope.selectSourceTypeGen=function(){
    $scope.attributeTableArray=[];
    $scope.filterTableArray=[];
    $scope.datapodAttributeTags = null;
    $scope.lhsdatapodattributefilter = null;
    RuleService.getAllLatestActive($scope.rulsourcetype).then(function (response) {
      onSuccessRelation(response.data)
    });
    var onSuccessRelation = function (response) {
      $scope.ruleRelation = response
      if($scope.ruleData &&  $scope.rulsourcetype == "rule"){
          temp = response.options.filter(function(el) {
            return el.uuid !== $scope.ruleData.uuid;
        });
        $scope.ruleRelation.options = temp
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
  }
  $scope.selectSourceType = function (oldValue,newValue) {
   if(typeof $stateParams.id != "undefined") {
    confirmDialog($scope.rulsourcetype, function() {
      $scope.selectSourceTypeGen();
      },
      function() {
        $scope.rulsourcetype = oldValue
    //  $scope.$apply(function() {$scope.rulsourcetype = oldValue;});
      });
    }
    else{
      $scope.selectSourceTypeGen();
    }

   
  } //End selectSourceType



  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true";
    $scope.mode = $stateParams.mode;
    $scope.ruleRelation;
    $scope.isDependencyShow = true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
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
    RuleService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'rule')
      .then(function (response) { onSuccess(response.data)},function(response) {onError(response.data)});
    var onSuccess = function (response) {
      $scope.isEditInprogess=false;
      $scope.ruleData = response.ruledata;
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

      RuleService.getAllLatestActive(response.ruledata.source.ref.type).then(function (response) { onSuccessRelation(response.data)});
      var onSuccessRelation = function (response) {
        $scope.ruleRelation = response
        if($scope.ruleData &&  $scope.rulsourcetype == "rule"){
            temp = response.options.filter(function(el) {
              return el.uuid !== $scope.ruleData.uuid;
          });
          $scope.ruleRelation.options = temp
        }  
        var defaultoption = {};
        defaultoption.uuid = $scope.ruleData.source.ref.uuid;
        defaultoption.name = $scope.ruleData.source.ref.name;
        $scope.ruleRelation.defaultoption = defaultoption;
        $scope.getSourceByFormula();
      }

      $scope.getParamByApp();

      CommonService.getAllLatestParamListByTemplate('Y', "paramlist","rule").then(function (response) {
      onSuccessGetAllLatestParamListByTemplate(response.data)});
      var onSuccessGetAllLatestParamListByTemplate = function (response) {
        $scope.allparamlist={};
        $scope.allparamlist.options = response
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

      RuleService.getExpressionByType(response.ruledata.source.ref.uuid, $scope.rulsourcetype).
      then(function (response) { onSuccessExpression(response.data)});
      var onSuccessExpression = function (response) {
        $scope.ruleLodeExpression = response
      }

      $scope.expressionTableArray = response.expressionlist
    
      CommonService.getFunctionByCriteria("", "N","function").
      then(function (response){ onSuccressGetFunction(response.data)});	
      var onSuccressGetFunction = function (response) {
        $scope.ruleLodeFunction = response;
        $scope.allFunction=response;
      }

      RuleService.getAllAttributeBySource(response.ruledata.source.ref.uuid, response.ruledata.source.ref.type).
      then(function (response) {onSuccess(response.data)});
      var onSuccess = function (response) {
        $scope.lhsdatapodattributefilter = response;
        $scope.loadSourceAttribue = response;
        $scope.sourcedatapodattribute = response;
      }

    };
    var onError =function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    } 
  }
  else {
    $scope.showactive = "false"
    CommonService.getAllLatestParamListByTemplate('Y', "paramlist","rule").then(function (response) {
      onSuccessGetAllLatestParamListByTemplate(response.data)
    });
    var onSuccessGetAllLatestParamListByTemplate = function (response) {
      $scope.allparamlist={};
      $scope.allparamlist.options = response
      if (response)
        $scope.allparamlist.defaultoption = null;
      $scope.getOneByUuidParamList();
      $scope.allparamlistParams=[];
      $scope.ruleData={};
      $scope.ruleData.locked="N";
      $scope.ruleData.active="Y";
      $scope.ruleData.published="N";
      
    }
  } //End Else

  $scope.selectVersion = function () {
    $scope.attributeTableArray = null;
    $scope.myform1.$dirty = false;
    $scope.myform2.$dirty = false;
    $scope.myform3.$dirty = false;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    RuleService.getOneByUuidAndVersion($scope.rule.defaultVersion.uuid, $scope.rule.defaultVersion.version, 'rule')
    .then(function (response) { onSuccess(response.data)},function(response) {onError(response.data)});
    var onSuccess = function (response) {
      $scope.isEditInprogess=false;
      $scope.ruleData = response.ruledata;
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

      $scope.attributeTableArray = response.sourceAttributes;
      $scope.datapodAttributeTags = response.sourceAttributes;
      $scope.rulsourcetype = response.ruledata.source.ref.type;
      
      RuleService.getAllLatest(response.ruledata.source.ref.type).then(function (response) {
        onSuccessRelation(response.data)});
      var onSuccessRelation = function (response) {
        $scope.ruleRelation = response
        var defaultoption = {};
        defaultoption.uuid = $scope.ruleData.source.ref.uuid;
        defaultoption.name = $scope.ruleData.source.ref.name;
        $scope.ruleRelation.defaultoption = defaultoption;
        $scope.getSourceByFormula();
      }

      $scope.getParamByApp();

      CommonService.getAllLatestParamListByTemplate('Y', "paramlist","rule").then(function (response) {
        onSuccessGetAllLatestParamListByTemplate(response.data)});
      var onSuccessGetAllLatestParamListByTemplate = function (response) {
        $scope.allparamlist={};
        $scope.allparamlist.options = response
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
        onSuccessExpression(response.data)});
      var onSuccessExpression = function (response) {
        $scope.ruleLodeExpression = response
      }

      $scope.expressionTableArray = response.expressionlist
      CommonService.getFunctionByCriteria("", "N","function").then(function (response) {
        onSuccressGetFunction(response.data)});	
      var onSuccressGetFunction = function (response) {
        $scope.ruleLodeFunction = response
        $scope.allFunction=response;
      }
     
      RuleService.getAllAttributeBySource(response.ruledata.source.ref.uuid, response.ruledata.source.ref.type, $scope.rulsourcetype).then(function (response) {
        onSuccess(response.data)});
      var onSuccess = function (response) {
        $scope.lhsdatapodattributefilter = response;
        $scope.loadSourceAttribue = response;
        $scope.sourcedatapodattribute = response;
      }

    } //End getOneByUuidAndVersion
    var onError =function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    } 
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
    if($scope.continueCount == 3){
			if($scope.isDuplication ==true){
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

  $scope.getSourceByFormula=function(){
    if($scope.ruleRelation.defaultoption){
      RuleService.getFormulaByType($scope.ruleRelation.defaultoption.uuid, $scope.rulsourcetype).then(function (response) { onSuccressGetFormula(response.data) });
      var onSuccressGetFormula = function (response) {
        $scope.ruleLodeFormula = response.data;
        $scope.allSourceFormula=response.data;
        $scope.getFormulaByParamList();  
      }
    }
  }
  $scope.getFormulaByParamList=function(){
    if($scope.allparamlist.defaultoption){
      RuleService.getFormulaByType($scope.allparamlist.defaultoption.uuid,$scope.rulsourcetype).then(function (response) { onSuccressGetFormula(response.data) });
      var onSuccressGetFormula = function (response) {
        if($scope.ruleLodeFormula && $scope.ruleLodeFormula.length >0){
          $scope.ruleLodeFormula=$scope.ruleLodeFormula.concat(response.data)
        }else{
          $scope.allParamlistFormula=response.data
          $scope.ruleLodeFormula=$scope.allParamlistFormula;
        }
      }
    }else{
      $scope.ruleLodeFormula=$scope.allSourceFormula;
    }
  }

  $scope.modelExecute = function (modeldetail) {
    if($scope.selectParamType =="paramlist"){
      if($scope.paramlistdata){
        var execParams = {};
        var paramListInfo =[];
        var paramInfo={};
        var paramInfoRef={};
        paramInfoRef.uuid=$scope.paramlistdata.uuid;
        paramInfoRef.type="paramlist";
        paramInfo.ref=paramInfoRef;
        paramListInfo[0]=paramInfo;
        execParams.paramListInfo=paramListInfo;
      }else{
        execParams=null;
      }
      $scope.paramlistdata=null;
      $scope.selectParamType=null;
    }
    else{
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
    RuleService.executeRuleWithParams(modeldetail.uuid, modeldetail.version, execParams).then(function (response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function (response) {
     $scope.dataLoading = false;
      $scope.saveMessage = CF_SUCCESS_MSG.ruleSaveExecute//"Rule Saved and Submitted Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.saveMessage
      $scope.$emit('notify', notify);
      $scope.okrulesave();
    }
  }
  $scope.onSelectparamSet = function () {
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
     // linkElement.setAttribute("download", filename);
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

  $scope.onChangeParamList=function(){
    $scope.isParamLsitTable=false;
    CommonService.getParamByParamList($scope.paramlistdata.uuid,"paramlist").then(function (response){ onSuccesGetParamListByTrain(response.data)});
    var onSuccesGetParamListByTrain = function (response) {
      $scope.isParamLsitTable=true;
      $scope.selectParamList=response;
      var paramArray=[];
      for(var i=0;i<response.length;i++){
        var paramInfo={}
          paramInfo.paramId=response[i].paramId; 
          paramInfo.paramName=response[i].paramName;
          paramInfo.paramType=response[i].paramType.toLowerCase();
          if(response[i].paramValue !=null && response[i].paramValue.ref.type == "simple"){
            paramInfo.paramValue=response[i].paramValue.value;
            paramInfo.paramValueType="simple"
        }else if(response[i].paramValue !=null){
          var paramValue={};
          paramValue.uuid=response[i].paramValue.ref.uuid;
          paramValue.type=response[i].paramValue.ref.type;
          paramInfo.paramValue=paramValue;
          paramInfo.paramValueType=response[i].paramValue.ref.type;
        }else{
          
        }
        paramArray[i]=paramInfo;
      }
      $scope.selectParamList.paramInfo=paramArray;
    }
  }

  $scope.getParamSetByParamList=function(){
    RuleService.getParamSetByParamList($scope.allparamlist.defaultoption.uuid,"").then(function (response){ onSuccessGetParamSetByParmLsit(response.data)});
    var onSuccessGetParamSetByParmLsit = function (response) {
      $scope.allparamset = response
      $scope.isShowExecutionparam = true;
    }
  }

  $scope.getParamListChilds=function(){
    CommonService.getParamListChilds($scope.allparamlist.defaultoption.uuid,"","paramlist").then(function (response) { onSuccessGetParamListChilds(response.data) });
      var onSuccessGetParamListChilds = function (response) {
        var defaultoption={};
        defaultoption.uuid=$scope.allparamlist.defaultoption.uuid;
        defaultoption.name=$scope.allparamlist.defaultoption.name;
        if(response.length >0){
          $scope.allParamList=response;
          $scope.allParamList.splice(0, 0,defaultoption);
        }else{
          $scope.allParamList=[];
          $scope.allParamList[0]=defaultoption;
        }
      }
  }

  $scope.onChangeParamType=function(){
    $scope.allparamset=null;
    $scope.allParamList=null;
    $scope.isParamLsitTable=false;
    $scope.selectParamList=null;
    if($scope.selectParamType == "paramlist"){
      $scope.paramlistdata=null;
      $scope.getParamListChilds();
    }
    else if($scope.selectParamType =="paramset"){
      $scope.getParamSetByParamList();
    }
  }
  $scope.onChangeParamListOFRule=function(){
    $scope.allparamset=null;
    $scope.allParamList=null;
    $scope.isParamLsitTable=false;
    $scope.selectParamList=null;
    $scope.paramTypes=null;
    $scope.selectParamType=null;
    $scope.ruleLodeFormula ==null;
    $scope.ruleLodeFormula = $scope.allSourceFormula
    $scope.getFormulaByParamList();
    setTimeout(function(){  $scope.paramTypes=["paramlist","paramset"]; },1);
    $scope.checkboxModelexecution="NO";
    $scope.ruleLodeParamList=null;
    $scope.getParamByApp();
  }

  $scope.showParamlistPopup=function(){
    setTimeout(function(){  $scope.paramTypes=["paramlist","paramset"]; },1);
    if($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption != null) {
      $('#responsive').modal({
        backdrop: 'static',
        keyboard: false
      });
    } else {
      $scope.isShowExecutionparam = false;
      $scope.allparamset = null;
      $scope.dataLoading=false;
    }
  }
  $scope.closeParalistPopup=function(){
    $scope.dataLoading = false;
    $scope.checkboxModelexecution="NO";
    $scope.isSubmitDisabled=false;
    $('#responsive').modal('hide');

  }
  $scope.changeCheckboxExecution = function () {
    $scope.allparamset=null;
    $scope.allParamList=null;
    $scope.isParamLsitTable=false;
    $scope.selectParamList=null;
    $scope.paramTypes=null;
    $scope.selectParamType=null;
    // setTimeout(function(){  $scope.paramTypes=["paramlist","paramset"]; },1);
    // if($scope.checkboxModelexecution == "YES" && $scope.allparamlist.defaultoption != null) {
    //   $('#responsive').modal({
    //     backdrop: 'static',
    //     keyboard: false
    //   });
    // } else {
    //   $scope.isShowExecutionparam = false;
    //   $scope.allparamset = null;
    // }
  }
  
  $scope.executeWithExecParams = function () {
    $('#responsive').modal('hide');
    RuleService.getOneById($scope.ruleId, "rule").then(function (response) {
    onSuccessGetOneById(response.data)});
    var onSuccessGetOneById = function (result) {
      $scope.modelExecute(result.data);
    } 
  }
  $scope.ondrop = function(e) {
		console.log(e);
		$scope.myform3.$dirty=true;
	}
  $scope.countBack = function () {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
    $scope.isShowRuleExec = false;
    $scope.isShowRuleResult = false;
  }
  $scope.selectOptionGen=function(){
    $scope.attributeTableArray = [];
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
  $scope.selectOption = function (oldValue,newValue) {
    if (typeof $stateParams.id != "undefined") {
      confirmDialog($scope.ruleRelation, function() {
        $scope.selectOptionGen();
      } ,
      function() {
        $scope.ruleRelation.defaultoption={};
        setTimeout(function(){
          $scope.ruleRelation.defaultoption.uuid=JSON.parse(oldValue).uuid;
          $scope.ruleRelation.defaultoption.name=JSON.parse(oldValue).name;
        },100);
        //$scope.$apply(function() {$scope.select = oldSelect;});
      });
    }else{
      $scope.selectOptionGen();
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

  $scope.SearchAttribute=function(index,type,propertyType){
		$scope.selectAttr=$scope.filterTableArray[index][propertyType]
		$scope.searchAttr={};
		$scope.searchAttr.type=type;
		$scope.searchAttr.propertyType=propertyType;
		$scope.searchAttr.index=index;
		RuleService.getAllLatest(type).then(function (response) { onSuccessGetAllLatest(response.data) });
		$scope.searchAttrIndex=index;
		var onSuccessGetAllLatest = function (response) {
			$scope.allSearchType = response;
			var temp;
			if($scope.selectSourceType == "dataset"){
				temp = $scope.allSearchType.options.filter(function(el) {
					return el.uuid !== $scope.datasetRelation.defaultoption .uuid;
				});
				$scope.allSearchType.options=temp;
				$scope.allSearchType.defaultoption=temp[0]
			}
			if($scope.dataset){
				temp = $scope.allSearchType.options.filter(function(el) {
					return el.uuid !== $scope.dataset.uuid;
				});
				$scope.allSearchType.options=temp;
				$scope.allSearchType.defaultoption=temp[0]
			}
			if(typeof $stateParams.id != "undefined" && $scope.selectAttr){
				var defaultoption={};
				defaultoption.uuid=$scope.selectAttr.uuid;
				defaultoption.name="";
				$scope.allSearchType.defaultoption=defaultoption;
			}
			$('#searchAttr').modal({
				backdrop: 'static',
				keyboard: false
			  });
			RuleService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid,type).then(function (response) { onSuccessAttributeBySource(response.data) });
			var onSuccessAttributeBySource = function (response) {
				$scope.allAttr = response;
				if (typeof $stateParams.id != "undefined" && $scope.selectAttr) {
					var defaultoption={};
					defaultoption.uuid=$scope.selectAttr.uuid;
					defaultoption.name="";
					$scope.allSearchType.defaultoption=defaultoption;
				}else{
					$scope.selectAttr=$scope.allAttr[0]
				}

			}
		}
	}
	
	$scope.onChangeSearchAttr=function(){
		RuleService.getAllAttributeBySource($scope.allSearchType.defaultoption.uuid,$scope.searchAttr.type).then(function (response) { onSuccessAttributeBySource(response.data) });
		var onSuccessAttributeBySource = function (response) {
			$scope.allAttr = response;
		}
	}

	$scope.SubmitSearchAttr=function(){
		$scope.filterTableArray[$scope.searchAttr.index][$scope.searchAttr.propertyType]=$scope.selectAttr;
		$('#searchAttr').modal('hide')
	}
  
  $scope.disableRhsType=function(rshTypes,arrayStr){
		for(var i=0;i<rshTypes.length;i++){
			rshTypes[i].disabled=false;
			if(arrayStr.length >0){
				var index=arrayStr.indexOf(rshTypes[i].caption);
				if(index !=-1){
					rshTypes[i].disabled=true;
				}
		  }
    }
    return rshTypes;
	}

  $scope.onChangeOperator=function(index){
    $scope.filterTableArray[index].isRhsNA=false;
		if($scope.filterTableArray[index].operator =='BETWEEN'){
			$scope.filterTableArray[index].rhstype=	$scope.filterTableArray[index].rhsTypes[1];
		  $scope.filterTableArray[index].rhsTypes=$scope.disableRhsType($scope.filterTableArray[index].rhsTypes,['attribute','formula','dataset','function','paramlist'])
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}else if(['IN','NOT IN'].indexOf($scope.filterTableArray[index].operator) !=-1){
			$scope.filterTableArray[index].rhsTypes=$scope.disableRhsType($scope.filterTableArray[index].rhsTypes,[]);
			$scope.filterTableArray[index].rhstype=	$scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
    }
    else if (['EXISTS', 'NOT EXISTS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'function', 'paramlist','string','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[4];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		} 
    else if(['<','>',"<=",'>='].indexOf($scope.filterTableArray[index].operator) !=-1){
      $scope.filterTableArray[index].rhsTypes=$scope.disableRhsType($scope.filterTableArray[index].rhsTypes,['dataset']);
			$scope.filterTableArray[index].rhstype=	$scope.filterTableArray[index].rhsTypes[1];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
    }
    else if (['IS'].indexOf($scope.filterTableArray[index].operator) != -1) {
			$scope.filterTableArray[index].isRhsNA=true;
			$scope.filterTableArray[index].rhsTypes = $scope.disableRhsType($scope.filterTableArray[index].rhsTypes, ['attribute', 'formula', 'dataset', 'function', 'paramlist','integer']);
			$scope.filterTableArray[index].rhstype = $scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text, index);
		}
		else{
			$scope.filterTableArray[index].rhsTypes=$scope.disableRhsType($scope.filterTableArray[index].rhsTypes,['dataset']);
			$scope.filterTableArray[index].rhstype=	$scope.filterTableArray[index].rhsTypes[0];
			$scope.selectrhsType($scope.filterTableArray[index].rhstype.text,index);
		}
	}
  $scope.checkAllFilterRow = function () {
    angular.forEach($scope.filterTableArray, function (filter) {
      filter.selected = $scope.checkAll;
    });
  }
    
  function returnRshType(){
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
		filertable.lhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		filertable.rhsdatapodAttribute=$scope.lhsdatapodattributefilter[0];
		
		filertable.logicalOperator = $scope.filterTableArray.length == 0 ? "" : $scope.logicalOperator[0]
		filertable.operator = $scope.operator[0].value
		filertable.lhstype = $scope.lhsType[2]
		filertable.rhstype = $scope.rhsType[2];
		filertable.rhsTypes=returnRshType();
    filertable.rhsTypes=$scope.disableRhsType(filertable.rhsTypes,['dataset']);
		filertable.rhstype = filertable.rhsTypes[0];
		filertable.rhsvalue;
		filertable.lhsvalue;
		$scope.filterTableArray.splice($scope.filterTableArray.length, 0, filertable);
  }
  $scope.onAttrFilterRowDown=function(index){	
		var rowTempIndex=$scope.filterTableArray[index];
    var rowTempIndexPlus=$scope.filterTableArray[index+1];
		$scope.filterTableArray[index]=rowTempIndexPlus;
		$scope.filterTableArray[index+1]=rowTempIndex;
		if(index ==0){
			$scope.filterTableArray[index+1].logicalOperator=$scope.filterTableArray[index].logicalOperator;
			$scope.filterTableArray[index].logicalOperator=""
		}
	}

	$scope.onAttrFilterRowUp=function(index){
		var rowTempIndex=$scope.filterTableArray[index];
    var rowTempIndexMines=$scope.filterTableArray[index-1];
		$scope.filterTableArray[index]=rowTempIndexMines;
		$scope.filterTableArray[index-1]=rowTempIndex;
		if(index ==1){
			$scope.filterTableArray[index].logicalOperator=$scope.filterTableArray[index-1].logicalOperator;
			$scope.filterTableArray[index-1].logicalOperator=""
		}
	}  
	
	$scope.onFilterDrop=function(index){
		if(index.targetIndex== 0){
			$scope.filterTableArray[index.sourceIndex].logicalOperator=$scope.filterTableArray[index.targetIndex].logicalOperator;
			$scope.filterTableArray[index.targetIndex].logicalOperator=""
		}
		if(index.sourceIndex == 0){
			$scope.filterTableArray[index.targetIndex].logicalOperator=$scope.filterTableArray[index.sourceIndex].logicalOperator;
			$scope.filterTableArray[index.sourceIndex].logicalOperator=""
		}
	}

  $scope.removeFilterRow = function () {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.filterTableArray, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
        $scope.fitlerAttrTableSelectedItem=[];
      }
    });
    if(newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    $scope.filterTableArray = newDataList;
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
      $scope.getSourceByFormula();
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
      $scope.getSourceByFormula();
    }
    else if (type == "function") {

			$scope.filterTableArray[index].isrhsFormula = false;
			$scope.filterTableArray[index].isrhsSimple = false;
			$scope.filterTableArray[index].isrhsDatapod = false;
			$scope.filterTableArray[index].isrhsDataset = false;
			$scope.filterTableArray[index].isrhsParamlist=false;
			$scope.filterTableArray[index].isrhsParamlist = false;
      $scope.filterTableArray[index].isrhsFunction = true;
      CommonService.getFunctionByCriteria("", "N","function").then(function (response) {
        onSuccressGetFunction(response.data)});		
        var onSuccressGetFunction = function (response) {
				$scope.allFunction = response;
			}
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
			$scope.filterTableArray[index].isrhsParamlist=true;
      $scope.filterTableArray[index].isrhsFunction = false;
      if($scope.allparamlistParams && $scope.allparamlistParams.length ==0){
        $scope.getParamByApp();
      }
			
    }
  }
  

  function isDublication (arr, field, index, name,darray) {
		var res = [];
		for(var i = 0; i < arr.length;i++){
			if (arr[i][field] == arr[index][field] && i != index) {
			    $scope.myform3[name].$invalid = true;
				darray.push(i);
				break
			}
			else {
				$scope.myform3[name].$invalid = false;	
			}
		}
		
		return darray;
	}
	
	$scope.onChangeSourceName1 = function (index,dupArray) {
		$scope.attributeTableArray[index].isSourceName = true;
		if ($scope.attributeTableArray[index].name) {
			var result = isDublication($scope.attributeTableArray, "name", index, "sourceName" + index,dupArray);
		}
		return dupArray
	}

	$scope.onChangeSourceName = function (index) {
		$scope.attributeTableArray[index].isSourceName = true;
		var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res = isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
	}


  $scope.onChangeSourceAttribute = function (type, index) {
    $scope.attributeTableArray[index].isOnDropDown=true;
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
        if($scope.ruleLodeFormula==null)
        $scope.getSourceByFormula();

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
      CommonService.getFunctionByCriteria("", "N","function").then(function (response) {
      onSuccressGetFunction(response.data)});	
      var onSuccressGetFunction = function (response) {
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
      RuleService.getOneByUuid($scope.allparamlist.defaultoption.uuid, "paramlist").
      then(function (response) { onSuccessParamList(response.data)});
      var onSuccessParamList = function (response) {
        var paramsArray = [];
        for (var i = 0; i < response.params.length; i++) {
          var paramsjson = {};
          paramsjson.uuid = response.uuid;
          paramsjson.name = response.name + "." + response.params[i].paramName;
          paramsjson.attributeId = response.params[i].paramId;
          paramsjson.attrType = response.params[i].paramType;
          paramsjson.paramName = response.params[i].paramName;
          paramsjson.caption = "rule."+paramsjson.paramName;
          paramsArray[i] = paramsjson;
        }
        $scope.ruleLodeParamList = paramsArray
        if($scope.allparamlistParams &&  $scope.allparamlistParams.length >0)
        $scope.allparamlistParams=$scope.allparamlistParams.concat( $scope.ruleLodeParamList);
      }
    }
  }
   
  $scope.getParamByApp=function(){
    $scope.allparamlistParams=[];
    CommonService.getParamByApp($rootScope.appUuidd || "", "application").
    then(function (response) { onSuccessGetParamByApp(response.data)});
    var onSuccessGetParamByApp=function(response){
      if(response.length >0){
        var paramsArray = [];
        for(var i=0;i<response.length;i++){
          var paramjson={}
          var paramsjson = {};
          paramsjson.uuid = response[i].ref.uuid;
          paramsjson.name = response[i].ref.name + "." + response[i].paramName;
          paramsjson.dname = response[i].ref.name + "." + response[i].paramName;
          paramsjson.attributeId = response[i].paramId;
          paramsjson.attrType = response[i].paramType;
          paramsjson.paramName = response[i].paramName;
          paramsjson.caption = "app."+paramsjson.paramName;
          paramsArray[i] = paramsjson
        }
        $scope.allparamlistParams=paramsArray;
      }
      // if($scope.ruleLodeParamList &&  $scope.ruleLodeParamList.length >0)
      // $scope.allparamlistParams=$scope.allparamlistParams.concat( $scope.ruleLodeParamList);
    }
    $scope.getOneByUuidParamList();
  }
  
  $scope.onChangeFunction=function(data,index){
    $scope.attributeTableArray[index].name = data.name;
    setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
  }
  $scope.onChangeAttributeDatapod = function (data, index) {
    $scope.attributeTableArray[index].name = data.name;
    setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
  }
  $scope.onChangeFormula = function (data, index) {
    $scope.attributeTableArray[index].name = data.name;
    setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
  }
  $scope.onChangeExpression = function (data, index) {
    $scope.attributeTableArray[index].name = data.name;
    setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
  }

  $scope.onChangeAttributeParamlist = function (data, index) {
    $scope.attributeTableArray[index].name = data.paramName;
    setTimeout(function(){
			if($scope.attributeTableArray.length > CF_GRID.framework_autopopulate_grid){
				$scope.attributeTableArray[index].isOnDropDown=false;
			}	
			else{
				$scope.attributeTableArray[index].isOnDropDown=true;
			}
		},10);
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				if ($scope.attributeTableArray[index].name) {
					var res =isDublication($scope.attributeTableArray, "name", index, "sourceName" +index, dupArray);
					if(res.length >0 ){
						$scope.isDuplication = true;
					}else {
						$scope.isDuplication = false;
					}
				}
			},10,(i));
		}
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
    var attributeinfo = {};
    attributeinfo.name = "attribute" + len;
    if($scope.attributeTableArray.length ==0){
			attributeinfo.id=$scope.attributeTableArray.length;
		}else{
			attributeinfo.id =CommonFactory.getMaxSourceSeqId($scope.attributeTableArray,"id")+1;
		}
		attributeinfo.index = len;
    attributeinfo.sourceAttributeType = $scope.sourceAttributeTypes[0];
    attributeinfo.isSourceAtributeSimple = true;
    attributeinfo.sourcesimple;
    attributeinfo.isSourceAtributeDatapod = false;
    $scope.attributeTableArray.splice($scope.attributeTableArray.length, 0, attributeinfo);
    $scope.focusRow(len-1)
  }

  $scope.autoPopulate=function(){
    $scope.isAutoMapInprogess=true;
    $scope.attributeTableArray=[];
    $scope.attrTableSelectedItem=[];
    var dupArray=[];
    $timeout(function(){
		for(var i=0;i<$scope.sourcedatapodattribute.length;i++){
			var attributeinfo = {};
      attributeinfo.id =i;
      if($scope.sourcedatapodattribute.length >CF_GRID.framework_autopopulate_grid)
				attributeinfo.isOnDropDown=false;
			else
				attributeinfo.isOnDropDown=true;
			attributeinfo.sourcedatapod=$scope.sourcedatapodattribute[i];
			attributeinfo.name=$scope.sourcedatapodattribute[i].name;
			attributeinfo.sourceAttributeType = $scope.sourceAttributeTypes[1];
			attributeinfo.isSourceAtributeSimple = false;
			attributeinfo.isSourceAtributeDatapod = true;
			attributeinfo.isSourceAtributeFormula = false;
			attributeinfo.isSourceAtributeExpression = false;
			attributeinfo.isSourceAtributeFunction = false;
			attributeinfo.isSourceAtributeParamList = false;
      $scope.attributeTableArray.push(attributeinfo);
      setTimeout(function(index){
				var result=$scope.onChangeSourceName1(index,dupArray);
				if(result.length >0 ){
					$scope.isDuplication = true;
				}else {
					$scope.isDuplication = false;
				}
      },10,(i));
      

		}
		if(i== $scope.attributeTableArray.length)
      $scope.isAutoMapInprogess=false;

    },40);
	}

  $scope.onAttrRowDown=function(index){  
    var rowTempIndex=$scope.attributeTableArray[index];
    var rowTempIndexPlus=$scope.attributeTableArray[index+1];
    $scope.attributeTableArray[index]=rowTempIndexPlus;
    $scope.attributeTableArray[index+1]=rowTempIndex;
  }
  $scope.onAttrRowUp=function(index){
    var rowTempIndex=$scope.attributeTableArray[index];
    var rowTempIndexMines=$scope.attributeTableArray[index-1];
    $scope.attributeTableArray[index]=rowTempIndexMines;
    $scope.attributeTableArray[index-1]=rowTempIndex;
  }
  $scope.focusRow = function(rowId){
    $timeout(function() {
      $location.hash(rowId);
      $anchorScroll();
    });
  }

  $scope.removeAttribute = function () {
    $scope.checkAllAttribute = false;
    var newDataList = [];
    angular.forEach($scope.attributeTableArray, function (selected) {
      if (!selected.selected) {
        newDataList.push(selected);
        $scope.attrTableSelectedItem=[];

      }
    });
    $scope.attributeTableArray = newDataList;
    var dupArray=[];
		for(var i=0;i<$scope.attributeTableArray.length;i++){
			setTimeout(function(index){
				var result=$scope.onChangeSourceName1(index,dupArray);
				if(result.length >0 ){
					$scope.isDuplication = true;
				}else {
					$scope.isDuplication = false;
				}
			},10,(i));
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
    var hidemode = "yes";
  if (hidemode == 'yes' && $scope.isDestoryState==false) {
      setTimeout(function () {
        $state.go('viewrule');
      }, 2000);
    }
  }


  $scope.submitRule = function () {
    var ruleJson = {}
    var upd_tag="N"
    $scope.dataLoading = true;
    $scope.isSubmitDisabled=true;
    if ($scope.ruleData != null) {
      ruleJson.uuid = $scope.ruleData.uuid
    }
    ruleJson.name = $scope.ruleData.name;
    ruleJson.desc = $scope.ruleData.desc;
    ruleJson.active = $scope.ruleData.active;
    ruleJson.locked = $scope.ruleData.locked;
    ruleJson.published = $scope.ruleData.published;
    ruleJson.publicFlag = $scope.ruleData.publicFlag;

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
    var source = {};
    var ref = {};
    ref.type = $scope.rulsourcetype
    ref.uuid = $scope.ruleRelation.defaultoption.uuid;
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
    if ($scope.filterTableArray != null) {
      if ($scope.filterTableArray.length > 0) {
        for (var i = 0; i < $scope.filterTableArray.length; i++) {
          var  filterInfo  = {};
          var operand = [];
          var lhsoperand = {};
          var lhsref = {};
          var rhsoperand = {};
          var rhsref = {};
          filterInfo.display_seq=i;
          if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
            filterInfo.logicalOperator=""
          }

          else{
            filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
          }

          filterInfo .operator = $scope.filterTableArray[i].operator;
          
          if($scope.filterTableArray[i].lhstype.text == "string") {
            lhsref.type = "simple";
            lhsoperand.ref = lhsref;
            lhsoperand.attributeType =$scope.filterTableArray[i].lhstype.caption;
            lhsoperand.value = $scope.filterTableArray[i].lhsvalue;
          }

          else if ($scope.filterTableArray[i].lhstype.text == "datapod") {
            
            if ($scope.rulsourcetype == "relation") {
              lhsref.type = "datapod";
            }
            else {
              lhsref.type = $scope.rulsourcetype;
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
            rhsoperand.attributeType =$scope.filterTableArray[i].rhstype.caption;
            rhsoperand.value = $scope.filterTableArray[i].rhsvalue;
            if ($scope.filterTableArray[i].operator == 'BETWEEN') {
              rhsoperand.value = $scope.filterTableArray[i].rhsvalue1 + "and" + $scope.filterTableArray[i].rhsvalue2;
            }  
          }

          else if ($scope.filterTableArray[i].rhstype.text == "datapod") {
            if ($scope.rulsourcetype == "relation") {
              rhsref.type = "datapod";
            }
            else {
              rhsref.type = $scope.rulsourcetype;
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
			  	filterInfo .operand = operand;
          filterInfoArray[i] = filterInfo;
        }
        ruleJson.filterInfo = filterInfoArray;
      } 
      else {
        ruleJson.filterInfo = null;
      }
    } 
    else {
      ruleJson.filterInfo = null; 
    }
    ruleJson.tags = tagArray;

    var options = {}
    options.execution = $scope.checkboxModelexecution;
    var sourceAttributesArray = [];
    if ($scope.attributeTableArray) {
      for (var l = 0; l < $scope.attributeTableArray.length; l++) {
        attributeinfo = {}
        attributeinfo.attrSourceId =$scope.attributeTableArray[l].id;
			  attributeinfo.attrDisplaySeq = l;
        attributeinfo.attrSourceName = $scope.attributeTableArray[l].name
        var ref = {};
        var sourceAttr = {};
        if ($scope.attributeTableArray[l].sourceAttributeType.text == "string") {
          ref.type = "simple";
          sourceAttr.ref = ref;
          sourceAttr.value = $scope.attributeTableArray[l].sourcesimple;
          attributeinfo.sourceAttr = sourceAttr;
        } else if ($scope.attributeTableArray[l].sourceAttributeType.text == "datapod") {
          ref.type = $scope.attributeTableArray[l].sourcedatapod.type;
			  	ref.uuid = $scope.attributeTableArray[l].sourcedatapod.uuid;
          sourceAttr.ref = ref;
          sourceAttr.attrId = $scope.attributeTableArray[l].sourcedatapod.attributeId;
          sourceAttr.attrType = $scope.attributeTableArray[l].sourcedatapod.attrType;
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
          sourceAttr.attrId = $scope.attributeTableArray[l].sourceparamlist.attributeId;
          sourceAttr.attrType = $scope.attributeTableArray[l].sourceparamlist.attrType
          attributeinfo.sourceAttr = sourceAttr;
        }

        sourceAttributesArray[l] = attributeinfo
      }
    }
    ruleJson.attributeInfo = sourceAttributesArray
    console.log("Rule JSON" + JSON.stringify(ruleJson))

    RuleService.submit(ruleJson,'rule',upd_tag).then(function (response) {
      onSuccess(response.data)
    }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      // if(options.execution == "YES" && $scope.allparamlist.defaultoption != null) {
      //   $scope.ruleId=response.data;
      //   $scope.showParamlistPopup();
      // } //End if
      // else 
      if(options.execution == "YES"){
        RuleService.getOneById(response.data, "rule").then(function (response) { onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function (result) {
          if ($scope.allparamlist.defaultoption == null) {
            $scope.modelExecute(result.data);
          } 
          else {
            $scope.isParamModelEnable = true;
            $scope.exeDetail = result.data;
          }
        }
      }
      else {
        $scope.dataLoading = false;
        $scope.saveMessage = CF_SUCCESS_MSG.ruleSave;
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
  
  $scope.onExecute = function (data) {
		$scope.dataLoading = false;
		notify.type = 'success';
		notify.title = 'Success';
		if(data.isExecutionCancel==false){
			notify.content = CF_SUCCESS_MSG.ruleSaveExecute;
		}else{
			notify.content = CF_SUCCESS_MSG.ruleSave
		}
		$scope.$emit('notify', notify);
		$scope.okrulesave();
	}


  $scope.attrTableSelectedItem=[];
	$scope.onChangeAttRow=function(index,status){
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
	$scope.fitlerAttrTableSelectedItem=[];
	$scope.onChangeFilterAttRow=function(index,status){
		if(status ==true){
			$scope.fitlerAttrTableSelectedItem.push(index);
		}
		else{
			let tempIndex=$scope.fitlerAttrTableSelectedItem.indexOf(index);

			if(tempIndex !=-1){
				$scope.fitlerAttrTableSelectedItem.splice(tempIndex, 1);

			}
		}	
	}
	$scope.autoMove=function(index,type){
		if(type=="mapAttr"){
			var tempAtrr=$scope.attributeTableArray[$scope.attrTableSelectedItem[0]];
			$scope.attributeTableArray.splice($scope.attrTableSelectedItem[0],1);
			$scope.attributeTableArray.splice(index,0,tempAtrr);
			$scope.attrTableSelectedItem=[];
			$scope.attributeTableArray[index].selected=false;
		}
		else{
			var tempAtrr=$scope.filterTableArray[$scope.fitlerAttrTableSelectedItem[0]];
			$scope.filterTableArray.splice($scope.fitlerAttrTableSelectedItem[0],1);
			$scope.filterTableArray.splice(index,0,tempAtrr);
			$scope.fitlerAttrTableSelectedItem=[];
			$scope.filterTableArray[index].selected=false;
			$scope.filterTableArray[0].logicalOperator="";
			if($scope.filterTableArray[index].logicalOperator =="" && index !=0){
				$scope.filterTableArray[index].logicalOperator=$scope.logicalOperator[0];
			}else if($scope.filterTableArray[index].logicalOperator =="" && index ==0){
				$scope.filterTableArray[index+1].logicalOperator=$scope.logicalOperator[0];
			}
		}
	}

	$scope.autoMoveTo=function(index,type){
		if(type =="mapAttr"){
			if(index <= $scope.attributeTableArray.length){
				$scope.autoMove(index-1,'mapAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
		else{
			if(index <= $scope.filterTableArray.length){
				$scope.autoMove(index-1,'filterAttr');
				$scope.moveTo=null;
				$(".actions").removeClass("open");
			}
		}
	}

});


RuleModule.controller('DetailRuleGroupController', function ($state, $timeout, $filter, $stateParams, $rootScope, $scope, RuleGroupService, privilegeSvc,CommonService, CF_SUCCESS_MSG) {
  $scope.select = 'rules group';
  if ($stateParams.mode == 'true') {
    $scope.isEdit = false;
    $scope.isversionEnable = false;
    $scope.isAdd = false;
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
  }
  else if ($stateParams.mode == 'false') {
    $scope.isEdit = true;
    $scope.isversionEnable = true;
    $scope.isAdd = false;
    $scope.isPanelActiveOpen=true;
		var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
  }
  else {
    $scope.isAdd = true;
  }
  $scope.isDestoryState = false;
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
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
  
  $scope.getLovByType = function() {
		CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
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

  $scope.$on('$destroy', function () {
    $scope.isDestoryState = true;
  });

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
    $scope.showGraphDiv = false;

  }

  $scope.showGraph = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showForm = false;
    $scope.showGraphDiv =true;
  }

  $scope.enableEdit = function (uuid, version) {
    if($scope.isPrivlage || $scope.ruleGroupDetail.locked =="Y"){
      return false;
    } 
    if($scope.checkIsInrogess () ==false){
			return false;
		}
    $scope.showPage()
    $state.go('createrulesgroup', {
      id: uuid,
      version: version,
      mode: 'false'
    });
  }

  $scope.showHome=function(uuid, version,mode){
    if($scope.checkIsInrogess () ==false){
			return false;
		}
		$scope.showPage();
		$state.go('createrulesgroup', {
			id: uuid,
			version: version,
			mode: mode
		});
  }
  
  $scope.showview = function (uuid, version) {
    if($scope.checkIsInrogess () ==false){
			return false;
		}
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
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    RuleGroupService.getAllVersionByUuid($stateParams.id, "rulegroup").then(function (response) {onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function (response) {
      for (var i = 0; i < response.length; i++) {
        var rulegroupversion = {};
        rulegroupversion.version = response[i].version;
        $scope.rulegroup.versions[i] = rulegroupversion;
      }

    }
    RuleGroupService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, 'rulegroup')
    .then(function (response) { onsuccess(response.data)},function(response) {onError(response.data)});
    var onsuccess = function (response) {
      $scope.isEditInprogess=false;
      $scope.ruleGroupDetail = response;
      if(response.tag)
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
        ruletag.id = response.ruleInfo[i].ref.uuid 
        ruletag.version = response.ruleInfo[i].ref.version;
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
    var onError =function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    } 
  }else{
    $scope.ruleGroupDetail={};
    $scope.ruleGroupDetail.locked="N";
  }

  $scope.selectVersion = function () {
    $scope.myform.$dirty = false;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    RuleGroupService.getOneByUuidAndVersion($scope.rulegroup.defaultVersion.uuid, $scope.rulegroup.defaultVersion.version, 'rulegroup')
    .then(function (response) { onsuccess(response.data)},function(response) {onError(response.data)});
    var onsuccess = function (response) {
      $scope.isEditInprogess=false;
      $scope.ruleGroupDetail = response;
      $scope.tags = response.tags;
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.rulegroup.defaultVersion = defaultversion;
      var ruleTagArray = [];
      for (var i = 0; i < response.ruleInfo.length; i++) {
        var ruletag = {};
        ruletag.uuid = response.ruleInfo[i].ref.uuid;
        ruletag.name = response.ruleInfo[i].ref.name;
        ruletag.id = response.ruleInfo[i].ref.uuid;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    };
    var onError =function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    } 
  }

  $scope.loadRules = function (query) {
    return $timeout(function () {
      return $filter('filter')($scope.rullall, query);
    });
  };
  
  $scope.clear = function () {
		$scope.ruleTags = null;
		$scope.myform.$dirty=true;
	}

	$scope.addAll = function () {
		$scope.ruleTags =$scope.rullall;
		$scope.myform.$dirty=true;
  }
  $scope.okrulesave = function () {

    var hidemode = "yes";
  if (hidemode == 'yes' && $scope.isDestoryState==false) {
      setTimeout(function () {
        $state.go('rulesgroup');
      }, 2000);

    }
  }

  $scope.submitRuleGroup = function () {
    var upd_tag="N"
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
    ruleGroupJson.locked = $scope.ruleGroupDetail.locked;
    ruleGroupJson.published = $scope.ruleGroupDetail.published;
    ruleGroupJson.publicFlag = $scope.ruleGroupDetail.publicFlag;

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
    RuleGroupService.submit(ruleGroupJson, "rulegroup",upd_tag).then(function (response) {
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
            $scope.saveMessage = CF_SUCCESS_MSG.ruleGroupSaveExecute//"Rule Groups Saved and Submitted Successfully"
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
        $scope.saveMessage =  CF_SUCCESS_MSG.ruleGroupSave//"Rule Groups Saved Successfully"
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


RuleModule.controller('ResultRuleController', function ($http, $log, dagMetaDataService, $filter, $state, $cookieStore,$stateParams, $location, $rootScope, $scope, ListRuleService, NgTableParams, uuid2, uiGridConstants, CommonService,privilegeSvc,CF_DOWNLOAD) {
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
  $scope.download={};
  $scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
  $scope.download.formates=CF_DOWNLOAD.formate;
  $scope.download.selectFormate=CF_DOWNLOAD.formate[0];
  $scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
  $scope.download.limit_to=CF_DOWNLOAD.limit_to; 
  $scope.filteredRows = [];
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  
  var privileges = privilegeSvc.privileges['comment'] || [];
  $rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
  $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
  $scope.$on('privilegesUpdated', function (e, data) {
    var privileges = privilegeSvc.privileges['comment'] || [];
    $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
    $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
    
  });
  $scope.metaType=dagMetaDataService.elementDefs[$stateParams.type.toLowerCase()].metaType;
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName; 
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
      $scope.execDetail= $scope.rulegroupdatail;
      $scope.metaType=dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
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
    $scope.execDetail=data;
    $scope.metaType=dagMetaDataService.elementDefs[$scope.type.text.toLowerCase()].execType;
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
    // if(!$scope.isD3RuleEexecGraphShow){
    //   return false;
    // }
    $scope.isD3RuleEexecGraphShow = false;
    $scope.getRuleExec($scope.ruleexecdetail)
  }

  $scope.RuleExecshowGraph = function () {
    $scope.isDataError = false;
    $scope.isD3RuleEexecGraphShow = true;
  }

  $scope.getRuleExec = function (data) {
    $scope.execDetail=data;
    $scope.metaType=dagMetaDataService.elementDefs["rule"].execType;
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
    }
  } //End getRuleExec




  

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
    if($scope.isD3RuleEexecGraphShow){
      return false;
    }
    $scope.download.data=data;
    $scope.isDownloadDirective=true;
		$scope.download.uuid = data.uuid;
		$scope.download.version = data.version;
		$scope.download.type="rule";
	};
	$scope.onDownloaed=function(data){
		console.log(data);
		$scope.isDownloadDatapod=data.isDownloadInprogess;
		$scope.isDownloadDatapod=data.isDownloadInprogess;
		$scope.isDownloadDirective=data.isDownloadDirective;
	}
}); //End RuleViewResultController
