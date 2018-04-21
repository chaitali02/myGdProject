ReconModule = angular.module('ReconModule');
ReconModule.controller('DetailRuleController', function($state,$stateParams, $rootScope,$scope,$timeout, $filter,dagMetaDataService,ReconRuleService) {
 
  $scope.mode = "false";
  $scope.rule = {};
  $scope.rule.versions = []
  $scope.originalCompare = null;
  $scope.showForm = true;
  $scope.SourceTypes = ["datapod"];
  //$scope.SourceTypes = ["datapod", "relation", "dataset", "rule"];
  $scope.selectSourceType=$scope.SourceTypes[0];
  $scope.selectTargetType=$scope.SourceTypes[0];
  $scope.logicalOperator = [" ", "OR", "AND"];
  $scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
  $scope.continueCount=1;
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };

  if($stateParams.mode =='true'){
    $scope.isEdit=false;
    $scope.isAdd=false;
  }
  else if($stateParams.mode =='false'){
    $scope.isEdit=true;
    $scope.isversionEnable=true;
    $scope.isAdd=false;
  }
  else{
    $scope.isAdd=true;
  }
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['recon'].listState
      $scope.statedetail.params = {}
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  $scope.countContinue = function() {
    $scope.continueCount = $scope.continueCount + 1;
    if ($scope.continueCount >= 4) {
      $scope.isSubmitShow = true;
    } else {
      $scope.isSubmitShow = false;
    }
  }

  $scope.showPage = function() {
    $scope.showForm = true
    $scope.showGraphDiv = false
  }
  
  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false
    $scope.showGraphDiv = true;
  };
  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createreconerule', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }
  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      var name=dagMetaDataService.elementDefs['recon'].listState
      $state.go('createreconerule', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }
  }

  $scope.countBack = function() {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }

  $scope.selectAllSourceFilterRow = function() {
    angular.forEach($scope.sourceFilterTable, function(filter) {
      filter.selected = $scope.selectAllSourceRow;
    });
  }
  
  $scope.selectAllTargetFilterRow = function() {
    angular.forEach($scope.targetFilterTable, function(filter) {
      filter.selected = $scope.selectAllTargetRow;
    });
  }

  $scope.addSourceFilterRow = function() {
    var filterinfo = {};
    if($scope.sourceFilterTable == null) {
      $scope.sourceFilterTable = [];
    }
    filterinfo.logicalOperator = $scope.logicalOperator[0];
    filterinfo.lhsFilter = $scope.sourceFilterAttribute[0]
    $scope.sourceFilterTable.splice($scope.sourceFilterTable.length, 0, filterinfo);
  }
  $scope.addTargetFilterRow = function() {
    var filterinfo = {};
    if($scope.targetFilterTable == null) {
      $scope.targetFilterTable = [];
    }
    filterinfo.logicalOperator = $scope.logicalOperator[0];
    filterinfo.lhsFilter = $scope.targetFilterAttribute[0]
    $scope.targetFilterTable.splice($scope.targetFilterTable.length, 0, filterinfo);
  }

  $scope.removeSourceFilterRow = function() {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.sourceFilterTable, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    $scope.sourceFilterTable = newDataList;
  }
  $scope.removeTargetFilterRow = function() {
    var newDataList = [];
    $scope.checkAll = false;
    angular.forEach($scope.targetFilterTable, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    $scope.targetFilterTable = newDataList;
  }

  $scope.getAllLatest=function(type,selectType,defaultValue,defaultoption){
    ReconRuleService.getAllLatest(selectType).then(function(response) { onSuccessGetAllLatest(response.data)});
    var onSuccessGetAllLatest = function(response) {
      if(type=="source"){
        console.log(defaultoption)
        $scope.allSource=response;
        if(!defaultValue){
          $scope.allSource.defaultoption={}
          setTimeout(function(){ 
            $scope.allSource.defaultoption=defaultoption; 
            $scope.getAllAttributeBySource(type,$scope.allSource.defaultoption.uuid,selectType,defaultValue);
          }, 100);  
          
        }else{ $scope.getAllAttributeBySource(type,$scope.allSource.defaultoption.uuid,selectType,defaultValue);}
        
        $scope.getFunctionByCategory(type,defaultValue);
      }else{
        $scope.allTarget=response;
        if(!defaultValue){
          $scope.allTarget.defaultoption={}; 
          setTimeout(function(){
            $scope.allTarget.defaultoption=defaultoption;
            $scope.getAllAttributeBySource(type,$scope.allTarget.defaultoption.uuid,selectType,defaultValue);
          
          },100);
        }else{
          $scope.getAllAttributeBySource(type,$scope.allTarget.defaultoption.uuid,selectType,defaultValue);
        }
        $scope.getFunctionByCategory(type,defaultValue);
      }
      
    }
  }
  $scope.getAllAttributeBySource=function(type,uuid,selectType,defaultValue){
    ReconRuleService.getAllAttributeBySource(uuid,selectType).then(function(response) {onSuccessGetAllAttributeBySource(response.data)});
    var onSuccessGetAllAttributeBySource = function(response){
      if(type == 'source'){
        $scope.allSourceAtrribute=response;
        if(defaultValue)
        $scope.selectSourceAtrribute=response[0];
        $scope.sourceFilterAttribute=response;
      }else{
        $scope.allTargetAtrribute=response;
       // console.log(response[0]);
        if(defaultValue)
        $scope.selectTargetAtrribute=response[0];
        $scope.targetFilterAttribute=response;
      }
    }//End getAllAttributeBySources

  }

  $scope.getFunctionByCategory=function(type,defaultValue){
    ReconRuleService.getFunctionByCategory("function").then(function(response) { onSuccessGetFunctionByCategory(response.data)});
    var onSuccessGetFunctionByCategory = function(response) {
      if(type == 'source'){
        $scope.allSourceFunction=response;
        if(defaultValue)
        $scope.selectSoueceFunction=response[0]
      }else{
        $scope.allTargetFunction=response;
        if(defaultValue)
        $scope.selectTargetFunction=response[0]
      }
    }
  }
  $scope.onChangeSourceType = function() {
    $scope.getAllLatest("source",$scope.selectSourceType,true,{});
  } //End onChangeSourceType

  $scope.onChangeTargetType = function() {
    $scope.getAllLatest("target",$scope.selectTargetType,true,{});
  } //End onChangeTargetType

  $scope.onChangeSource=function(){
    $scope.getAllAttributeBySource('source',$scope.allSource.defaultoption.uuid,$scope.selectSourceType,true);
    $scope.getFunctionByCategory('source',true);
  }
  $scope.onChangeTarget=function(){
    $scope.getAllAttributeBySource('target',$scope.allTarget.defaultoption.uuid,$scope.selectTargetType,true);
    $scope.getFunctionByCategory('target',true);
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.showactive = "true";
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    ReconRuleService.getAllVersionByUuid($stateParams.id, "recon").then(function(response) {onGetAllVersionByUuid(response.data)});
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var ruleversion = {};
        ruleversion.version = response[i].version;
        $scope.rule.versions[i] = ruleversion;
      }
    }
    ReconRuleService.getOneByUuidAndVersion($stateParams.id,$stateParams.version, 'reconview').then(function(response) { onSuccess(response.data)});
    var onSuccess = function(response) {
     $scope.reconruledata=response.ruledata;
     $scope.originalCompare=response.ruledata;
     var defaultversion = {};
     defaultversion.version = response.ruledata.version;
     defaultversion.uuid = response.ruledata.uuid;
     $scope.rule.defaultVersion = defaultversion;
     $scope.selectSourceType=$scope.reconruledata.sourceAttr.ref.type;
     $scope.selectTargetType=$scope.reconruledata.targetAttr.ref.type; 
     var source={};
     source.uuid=$scope.reconruledata.sourceAttr.ref.uuid
     source.name=$scope.reconruledata.sourceAttr.ref.name;
   
     
     $scope.getAllLatest("source",$scope.reconruledata.sourceAttr.ref.type,false,source);
     var target={};
     target.uuid=$scope.reconruledata.targetAttr.ref.uuid
     target.name=$scope.reconruledata.targetAttr.ref.name;
     $scope.getAllLatest("target",$scope.reconruledata.targetAttr.ref.type,false,target);
     $scope.selectSourceAtrribute=response.sourceAttr;
     $scope.selectTargetAtrribute=response.targetAttr;

     var selectSourceFunction={};
     selectSourceFunction.uuid=$scope.reconruledata.sourceFunc.ref.uuid
     selectSourceFunction.name=$scope.reconruledata.sourceFunc.ref.name
     $scope.selectSoueceFunction=selectSourceFunction;

     var selectTargetFunction={};
     selectTargetFunction.uuid=$scope.reconruledata.targetFunc.ref.uuid
     selectTargetFunction.name=$scope.reconruledata.targetFunc.ref.name
     $scope.selectTargetFunction=selectTargetFunction;
     $scope.sourceFilterTable=response.sourceFilterInfo
     $scope.targetFilterTable=response.targetFilterInfo


    }
  }else{
    $scope.getAllLatest("source",$scope.selectSourceType,true,{});
    $scope.getAllLatest("target",$scope.selectTargetType,true,{});
  }
  $scope.selectVersion=function(){
    ReconRuleService.getOneByUuidAndVersion($scope.rule.defaultVersion.uuid,$scope.rule.defaultVersion.version, 'reconview').then(function(response) { onSuccess(response.data)});
    var onSuccess = function(response) {
     $scope.reconruledata=response.ruledata;
     $scope.originalCompare=response.ruledata;
     var defaultversion = {};
     defaultversion.version = response.ruledata.version;
     defaultversion.uuid = response.ruledata.uuid;
     $scope.rule.defaultVersion = defaultversion;
     $scope.selectSourceType=$scope.reconruledata.sourceAttr.ref.type;
     $scope.selectTargetType=$scope.reconruledata.targetAttr.ref.type; 
     var source={};
     source.uuid=$scope.reconruledata.sourceAttr.ref.uuid
     source.name=$scope.reconruledata.sourceAttr.ref.name;
     $scope.getAllLatest("source",$scope.reconruledata.sourceAttr.ref.type,false,source);
     var target={};
     target.uuid=$scope.reconruledata.targetAttr.ref.uuid
     target.name=$scope.reconruledata.targetAttr.ref.name;
     $scope.getAllLatest("target",$scope.reconruledata.targetAttr.ref.type,false,target);
     $scope.selectSourceAtrribute=response.sourceAttr;
     $scope.selectTargetAtrribute=response.targetAttr;

     var selectSourceFunction={};
     selectSourceFunction.uuid=$scope.reconruledata.sourceFunc.ref.uuid
     selectSourceFunction.name=$scope.reconruledata.sourceFunc.ref.name
     $scope.selectSoueceFunction=selectSourceFunction;

     var selectTargetFunction={};
     selectTargetFunction.uuid=$scope.reconruledata.targetFunc.ref.uuid
     selectTargetFunction.name=$scope.reconruledata.targetFunc.ref.name
     $scope.selectTargetFunction=selectTargetFunction;
     $scope.sourceFilterTable=response.sourceFilterInfo
     $scope.targetFilterTable=response.targetFilterInfo


    }
  }
  $scope.submit=function(){
    $scope.isSubmitProgess=true;
    $scope.isSubmitDisable=true;
    var jsonObj={}
    jsonObj.targetfilterChg=null;
    jsonObj.uuid=$scope.reconruledata.uuid;
    jsonObj.name = $scope.reconruledata.name;
    jsonObj.desc = $scope.reconruledata.desc;
    jsonObj.active = $scope.reconruledata.active;
    jsonObj.published = $scope.reconruledata.published;
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    jsonObj.tags=tagArray;
    var source = {};
    var sourceref = {};
    sourceref.type = $scope.selectSourceType
    sourceref.uuid = $scope.allSource.defaultoption.uuid;
    source.ref = sourceref;
    source.attrId=$scope.selectSourceAtrribute.attributeId
    jsonObj.sourceAttr = source;

    var target = {};
    var targetref = {};
    targetref.type = $scope.selectTargetType
    targetref.uuid = $scope.allTarget.defaultoption.uuid;
    target.ref = targetref;
    target.attrId=$scope.selectTargetAtrribute.attributeId
    jsonObj.targetAttr = target;
    var sourceFunc={};
    var refSouurceFun={};
    refSouurceFun.type="function";
    refSouurceFun.uuid=$scope.selectSoueceFunction.uuid;
    sourceFunc.ref=refSouurceFun;
    jsonObj.sourceFunc=sourceFunc;

    var targetFunc={};
    var refTargetFun={};
    refTargetFun.type="function";
    refTargetFun.uuid=$scope.selectTargetFunction.uuid;
    targetFunc.ref=refTargetFun;
    jsonObj.targetFunc=targetFunc;

    var sourcefilter = {}
    if ($scope.originalCompare != null && $scope.originalCompare.sourcefilter != null) {
      sourcefilter.uuid = $scope.originalCompare.sourcefilter.uuid;
      sourcefilter.name = $scope.originalCompare.sourcefilter.name;
      sourcefilter.createdBy = $scope.originalCompare.sourcefilter.createdBy;
      sourcefilter.createdOn = $scope.originalCompare.sourcefilter.createdOn;
      sourcefilter.active = $scope.originalCompare.sourcefilter.active;
      sourcefilter.tags = $scope.originalCompare.sourcefilter.tags;
      sourcefilter.desc = $scope.originalCompare.sourcefilter.desc;
      sourcefilter.dependsOn = $scope.originalCompare.sourcefilter.dependsOn;
    }

    var targetfilter = {}
    if ($scope.originalCompare != null && $scope.originalCompare.targetfilter != null) {
      targetfilter.uuid = $scope.originalCompare.targetfilter.uuid;
      targetfilter.name = $scope.originalCompare.targetfilter.name;
      targetfilter.createdBy = $scope.originalCompare.targetfilter.createdBy;
      targetfilter.createdOn = $scope.originalCompare.targetfilter.createdOn;
      targetfilter.active = $scope.originalCompare.targetfilter.active;
      targetfilter.tags = $scope.originalCompare.targetfilter.tags;
      targetfilter.desc = $scope.originalCompare.targetfilter.desc;
      targetfilter.dependsOn = $scope.originalCompare.targetfilter.dependsOn;
    }
    var sourceFilterInfo = [];
    if($scope.sourceFilterTable !=null){
      if($scope.sourceFilterTable.length >0){
        for (var i = 0; i < $scope.sourceFilterTable.length; i++) {
          if ($scope.originalCompare != null && $scope.originalCompare.sourcefilter != null && $scope.originalCompare.sourcefilter.filterInfo.length == $scope.sourceFilterTable.length) {
            if ($scope.originalCompare.sourcefilter.filterInfo[i].operand[0].ref.uuid != $scope.sourceFilterTable[i].lhsFilter.uuid ||$scope.originalCompare.sourcefilter.filterInfo[i].operand[0].attributeId != $scope.sourceFilterTable[i].lhsFilter.attributeId ||
              $scope.sourceFilterTable[i].logicalOperator != $scope.originalCompare.sourcefilter.filterInfo[i].logicalOperator ||
              $scope.sourceFilterTable[i].filtervalue != $scope.originalCompare.sourcefilter.filterInfo[i].operand[1].value ||
              $scope.sourceFilterTable[i].operator != $scope.originalCompare.sourcefilter.filterInfo[i].operator) {

                jsonObj.sourcefilterChg = "y";

            } else {
              jsonObj.sourcefilterChg = "n";
            }
          } else {
            jsonObj.sourcefilterChg = "y";
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
          reffirst.uuid = $scope.sourceFilterTable[i].lhsFilter.uuid
          operandfirst.ref = reffirst;
          operandfirst.attributeId = $scope.sourceFilterTable[i].lhsFilter.attributeId
          operand[0] = operandfirst;
          refsecond.type = "simple";
          operandsecond.ref = refsecond;
          if (typeof $scope.sourceFilterTable[i].filtervalue == "undefined") {
            operandsecond.value = "";
          } else {
            operandsecond.value = $scope.sourceFilterTable[i].filtervalue
          }
          operand[1] = operandsecond;
          if (typeof $scope.sourceFilterTable[i].logicalOperator == "undefined") {
            filterInfo.logicalOperator = ""
          } else {
            filterInfo.logicalOperator = $scope.sourceFilterTable[i].logicalOperator
          }
          filterInfo.operator = $scope.sourceFilterTable[i].operator
          filterInfo.operand = operand;
          sourceFilterInfo[i] = filterInfo;
        }
        sourcefilter.filterInfo = sourceFilterInfo;
        jsonObj.sourcefilter = sourcefilter;

      }else{
        jsonObj.sourcefilter = null;
        jsonObj.sourcefilterChg = "y";
      }
    }else{
      jsonObj.sourcefilter = null;
      jsonObj.sourcefilterChg = "y";
      
    }
    var targetFilterInfo=[];
    if($scope.targetFilterTable !=null){
      if($scope.targetFilterTable.length >0){
        for (var i = 0; i < $scope.targetFilterTable.length; i++) {
          if ($scope.originalCompare != null && $scope.originalCompare.targetfilter != null && $scope.originalCompare.targetfilter.filterInfo.length == $scope.targetFilterTable.length) {
            if ($scope.originalCompare.targetfilter.filterInfo[i].operand[0].ref.uuid != $scope.targetFilterTable[i].lhsFilter.uuid||$scope.originalCompare.targetfilter.filterInfo[i].operand[0].attributeId != $scope.targetFilterTable[i].lhsFilter.attributeId ||
              $scope.targetFilterTable[i].logicalOperator != $scope.originalCompare.targetfilter.filterInfo[i].logicalOperator ||
              $scope.targetFilterTable[i].filtervalue != $scope.originalCompare.targetfilter.filterInfo[i].operand[1].value ||
              $scope.targetFilterTable[i].operator != $scope.originalCompare.targetfilter.filterInfo[i].operator) {

                jsonObj.targetfilterChg = "y";

            } else {
              jsonObj.targetfilterChg = "n";
            }
          } else {
            jsonObj.targetfilterChg = "y";
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
          reffirst.uuid = $scope.targetFilterTable[i].lhsFilter.uuid
          operandfirst.ref = reffirst;
          operandfirst.attributeId = $scope.targetFilterTable[i].lhsFilter.attributeId
          operand[0] = operandfirst;
          refsecond.type = "simple";
          operandsecond.ref = refsecond;
          if (typeof $scope.targetFilterTable[i].filtervalue == "undefined") {
            operandsecond.value = "";
          } else {
            operandsecond.value = $scope.targetFilterTable[i].filtervalue
          }
          operand[1] = operandsecond;
          if (typeof $scope.targetFilterTable[i].logicalOperator == "undefined") {
            filterInfo.logicalOperator = ""
          } else {
            filterInfo.logicalOperator = $scope.targetFilterTable[i].logicalOperator
          }
          filterInfo.operator = $scope.targetFilterTable[i].operator
          filterInfo.operand = operand;
          targetFilterInfo[i] = filterInfo;
        }
        targetfilter.filterInfo = targetFilterInfo;
        jsonObj.targetfilter = targetfilter;
      }else{
        jsonObj.targetfilter = null;
        jsonObj.targetfilterChg = "y";
      }
    }else{
      jsonObj.targetfilter = null;
      jsonObj.targetfilterChg = "y";
      
    }
    console.log("Rule JSON" + JSON.stringify(jsonObj))
    ReconRuleService.submit(jsonObj, 'reconview').then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
      if ($scope.isExecute == "YES") {
        ReconRuleService.getOneById(response.data, "recon").then(function(response) {onSuccessGetOneById(response.data) });
        var onSuccessGetOneById = function(result) {
          ReconRuleService.execute(result.data.uuid,result.data.version).then(function(response){onSuccess(response.data)});
          var onSuccess=function(response){
            $scope.saveMessage="Rule Saved and Submitted Successfully."
            console.log(JSON.stringify(response))
            $scope.isSubmitProgess=false;
            $scope.isSubmitDisable=true;
            notify.type='success',
            notify.title= 'Success',
            notify.content=$scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okrulesave();
          }
        }
      } //End if
      else{
        $scope.isSubmitProgess=false;
        $scope.isSubmitDisable=true;
        $scope.saveMessage = "Rule Saved Successfully" 
        notify.type='success',
        notify.title= 'Success',
        notify.content=$scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okrulesave();
      }
    } //End Submit Api Function
    var onError = function(response) {
      $scope.isSubmitProgess=false;
      $scope.isSubmitDisable=false;
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  }

  $scope.okrulesave = function() {
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go('datareconrule');
      }, 2000);
    }
  }

});

ReconModule.controller('DetailRuleGroupController', function($state, $timeout, $filter, $stateParams, $rootScope, $scope, RuleGroupService,privilegeSvc,dagMetaDataService) {
  
  $scope.select = 'rules group';
  if($stateParams.mode =='true'){
	  $scope.isEdit=false;
	  $scope.isversionEnable=false;
	  $scope.isAdd=false;
	}
	else if($stateParams.mode =='false'){
	  $scope.isEdit=true;
	  $scope.isversionEnable=true;
	  $scope.isAdd=false;
	}
	else{
	$scope.isAdd=true;
	}
  $scope.showForm = true;

  $scope.mode = " ";
  $scope.rulegroup = {};
  $scope.rulegroup.versions = []
  $scope.isDependencyShow = false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['rulegroup'] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['rulegroup'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.close = function() {
    if ($stateParams.returnBack == "true" && $rootScope.previousState) {
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $scope.statedetail = {};
      $scope.statedetail.name = dagMetaDataService.elementDefs['recongroup'].listState
      $scope.statedetail.params = {}
      $state.go($scope.statedetail.name, $scope.statedetail.params)
    }
  }
  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false;
  
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;
  }

  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createreconerulegroup', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createreconerulegroup', {
        id: uuid,
        version: version,
        mode:'true'
      });
   }
  }

  RuleGroupService.getAllLatest('recon').then(function(response) {onSuccess(response.data)});
  var onSuccess = function(response) {
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
    RuleGroupService.getAllVersionByUuid($stateParams.id, "recongroup").then(function(response) {onGetAllVersionByUuid(response.data)});
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var rulegroupversion = {};
        rulegroupversion.version = response[i].version;
        $scope.rulegroup.versions[i] = rulegroupversion;
      }
    }
    RuleGroupService.getOneByUuidAndVersion($stateParams.id,$stateParams.version, 'recongroup').then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.ruleGroupDetail = response;
      $scope.tags = response.tags
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
        ruletag.id = response.ruleInfo[i].ref.uuid// + "_" + response.ruleInfo[i].ref.version;
        ruletag.version = response.ruleInfo[i].ref.version;
        ruleTagArray[i] = ruletag;
      }
      $scope.ruleTags = ruleTagArray
    }
  }

  $scope.selectVersion = function() {
    $scope.myform.$dirty = false;
    RuleGroupService.getOneByUuidAndVersion($scope.rulegroup.defaultVersion.uuid, $scope.rulegroup.defaultVersion.version, 'recongroup').then(function(response) {onsuccess(response.data)});
    var onsuccess = function(response) {
      $scope.ruleGroupDetail = response;
      $scope.tags = response.tags
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
  }

  $scope.loadRules = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.rullall, query);
    });
  };

  $scope.okrulesave = function() {
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go('datareconrulegroup');
      }, 2000);
    }
  }

  $scope.submitRuleGroup = function() {
    $scope.isSubmitProgess = true;
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
      ref.type = "recon"
      ref.uuid = $scope.ruleTags[i].uuid;
      ruleInfo.ref = ref;
      ruleInfoArray[i] = ruleInfo;
    }
    
    ruleGroupJson.ruleInfo = ruleInfoArray;
    ruleGroupJson.inParallel = $scope.checkboxModelparallel
    console.log(JSON.stringify(ruleGroupJson))
    RuleGroupService.submit(ruleGroupJson, "recongroup").then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
    var onSuccess = function(response) {
     
      if (options.execution == "YES") {
        RuleGroupService.getOneById(response.data, "recongroup").then(function(response) {onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          RuleGroupService.execute(result.data.uuid,result.data.version).then(function(response) { onSuccess(response.data)});
          var onSuccess = function(response) {
            console.log(JSON.stringify(response))
            $scope.isSubmitProgess = false;
            $scope.saveMessage = "Rule Group Saved and Submitted Successfully"
            notify.type='success',
            notify.title= 'Success',
            notify.content=$scope.saveMessage
            $scope.$emit('notify', notify);
            $scope.okrulesave();
          }
        }
      } //End If
      else {
        $scope.isSubmitProgess = false;
        $scope.saveMessage = "Rule Group Saved Successfully"
        notify.title= 'Success',
        notify.content=$scope.saveMessage
        $scope.$emit('notify', notify);
        $scope.okrulesave();
      } //End else
    } //End Submit Api Function
    var onError = function(response) {
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  } //End Submit Function
});




ReconModule.controller('ResultReconController', function( $http,dagMetaDataService,$timeout,$filter,$state,$stateParams,$location,$rootScope,$scope,ReconRuleService,CommonService) {
  $scope.select = $stateParams.type;
  $scope.type = {text : $scope.select == 'recongroupexec' ? 'recongroup' : 'recon'};
  $scope.showprogress=false;
  $scope.isRuleExec=false;
  $scope.isRuleResult=false;
  $scope.showZoom=false;
  $scope.isD3RuleEexecGraphShow=false;
  $scope.isD3RGEexecGraphShow=false;
  $scope.gridOptions = dagMetaDataService.gridOptionsDefault;
  // ui grid
  var notify = {
    type: 'success',
    title: 'Success',
    content: '',
    timeout: 3000 //time in ms
  };
  $scope.getGridStyle = function () {
    var style = {
      'margin-top':'10px',
      'height':'40px'
    }
    if($scope.filteredRows){
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 80)+'px';
    }
    return style;
  }

  $scope.filteredRows = [];
  $scope.gridOptions.onRegisterApi =function(gridApi){
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
  
  $scope.refreshRuleExecData=function () {
    $scope.gridOptionsRule.data = $filter('filter')($scope.orignalRuleExecData, $scope.searchruletext, undefined);
  }
  
  $scope.refreshRGExecData=function () {
    $scope.gridOptionsRuleGroup.data = $filter('filter')($scope.orignalRGExecData, $scope.searchrGtext, undefined);
  }
  // ui grid
  
  //For Breadcrum
  $scope.$on('daggroupExecChanged',function (e,groupExecName) {
    $scope.daggroupExecName = groupExecName;
  })

  $scope.$on('resultExecChanged',function (e,resultExecName) {
    $scope.resultExecName = resultExecName;
  })
  //For Breadcrum

  $scope.onClickRuleResult=function(){
    $scope.isRuleExec=true;
    $scope.isRuleResult=false;
      $scope.$emit('resultExecChanged',false);//Update Breadcrum
  }


  $scope.getProfileExec=function(data){
    var uuid=data.uuid;
    var version=data.version;
    var name=data.name;
    $scope.ruleExecUuid=uuid;
    $scope.ruleExecVersion=version;
    var params = {"id":uuid,"name":name,"elementType":"recon","version":version,"type":"recon","typeLabel":"Recon"}
    window.showResult(params);
  }

  $scope.$watch("zoomSize", function(newData,oldData) {
    $scope.$broadcast('zoomChange',newData);
  });

  window.navigateTo = function(url){
    var state = JSON.parse(url);
    $rootScope.previousState = {name : $state.current.name, params : $state.params};
    var ispresent=false;
    if(ispresent !=true){
      var stateTab={};
      stateTab.route=state.state;
      stateTab.param=state.params;
      stateTab.active=false;
      $rootScope.$broadcast('onAddTab',stateTab);
    }
    $state.go(state.state,state.params);
  }

  window.showResult = function(params){
    App.scrollTop();
    $scope.lastParams=params;
    if(params.type.slice(-5).toLowerCase() == 'group'){
      $scope.isRuleExec = true;
      $scope.isProfileGroupExec = true;
      $scope.$broadcast('generateGroupGraph',params);
    }
    else {
      $scope.isRuleResult = true;
      $scope.isRuleExec = false;
      $scope.isDataInpogress=true;
      $scope.spinner=true;
      setTimeout(function () {
        $scope.$apply();
        $scope.ruleExecUuid=params.id;
        $scope.ruleExecVersion=params.version;
        $scope.$broadcast('generateResults',params);
        $scope.$emit('resultExecChanged',params.name);  //For Breadcrum
      }, 100);
    }
  
  }
  $scope.refreshData = function(searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalData, searchtext, undefined);
  };

  window.refreshResultfunction=function(){
    $scope.isD3RuleEexecGraphShow=false;
    window.showResult($scope.lastParams);
  }

  $scope.ruleExecshowGraph=function(){
    $scope.isD3RuleEexecGraphShow=true;
  }

  $scope.rGExecshowGraph=function(){
    $scope.isProfileGroupExec=false;
    $scope.isD3RGEexecGraphShow=true;
  }

  $scope.profileGroupExec=function(data){
    if($scope.type.text=='recon'){
      $scope.getProfileExec(data);
      return
    }
    $scope.profileGroupLastParams = data;
    $scope.zoomSize = 7;
    var uuid=data.uuid;
    var version=data.version;
    var name=data.name;
    $scope.rGExecUuid=uuid;
    $scope.rGExecVersion=version;
    $scope.isRuleSelect=false;
    $scope.isRuleGroupExec=false;
    $scope.isRuleExec=true;
    if($scope.type.text == 'recongroup'){
      $scope.isProfileGroupExec = true;
    }
    else {
      $scope.isProfileGroupExec = false;
    }
    var params = {"id":uuid,"name":name,"elementType":"recongroup","version":version,"type":"recongroup","typeLabel":"RconGroup","url":"recon/getReconExecByRGExec?","ref":{"type":"recongroupExec","uuid":uuid,"version":version,"name":name}}
    setTimeout(function () {
      $scope.$broadcast('generateGroupGraph',params);
    }, 100);
  }

  $scope.getExec = $scope.profileGroupExec;
  $scope.getExec({
    uuid: $stateParams.id,
    version: $stateParams.version,
    name: $stateParams.name
  });

  $scope.reGroupExecute=function() {
    $('#reExModal').modal({
      backdrop: 'static',
      keyboard: false
    });

  }
  $scope.okReGroupExecute=function () {
    $('#reExModal').modal('hide');
    $scope.executionmsg = "Recon Group Restarted Successfully"
    notify.type='success',
    notify.title= 'Success',
    notify.content=$scope.executionmsg
    $rootScope.$emit('notify', notify);
    CommonService.restartExec("recongroupExec",$stateParams.id,$stateParams.version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response) {
    //$scope.refreshRuleGroupExecFunction();
    }
    $scope.refreshRuleGroupExecFunction();
  }

  $scope.refreshRuleGroupExecFunction= function () {
    $scope.isD3RGEexecGraphShow=false;
    $scope.profileGroupExec($scope.profileGroupLastParams);
  }

  $scope.toggleZoom = function(){
    $scope.showZoom = !$scope.showZoom;
  }

  $scope.downloadFile = function(data) {
    if($scope.isD3RuleEexecGraphShow){
      return false;
    }
    var uuid = data.uuid;
    var version=data.version;
    var url=$location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url:url+"recon/download?action=view&reconExecUUID="+uuid+"&reconExecVersion="+version,
      responseType: 'arraybuffer'
    }).success(function(data, status, headers) {
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
        linkElement.setAttribute("download",filename);
        var clickEvent = new MouseEvent("click", {
          "view": window,
          "bubbles": true,
          "cancelable": false
        });
        linkElement.dispatchEvent(clickEvent);
      } 
      catch (ex) {
        console.log(ex);
      }
    }).error(function(data) {
      console.log(data);
    });
  };

});
