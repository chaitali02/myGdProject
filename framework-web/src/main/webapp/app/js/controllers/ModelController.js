/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateModelController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, ModelService,$http,$location) {
  $scope.featuureType=["integer","string","double"];
  $scope.mode = "false";

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
  $scope.isSubmitEnable = true;
  $scope.modeldata;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.model = {};
  $scope.model.versions = [];
  $scope.isshowmodel = false;
  //$scope.SourceTypes = ["datapod", "dataset"];
  $scope.dependsOnType= ["algorithm", "formula"];
  $scope.selectedDependsOnType=$scope.dependsOnType[0];
  $scope.type = ["string", "double", "date"];
  $scope.scriptTypes= ["SPARK","PYTHON", "R"];
  $scope.scriptType="SPARK"
  $scope.count = 0;
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isLabelDisable = true;
  $scope.isDependencyShow = false;
  
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 30000 //time in ms
  };
  
  $scope.close = function() {
    if ($stateParams.returnBack == 'true' && $rootScope.previousState) {
      //revertback
      $state.go($rootScope.previousState.name, $rootScope.previousState.params);
    } else {
      $state.go('model');
    }
  }
  
  $scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName = fromState.name
    $sessionStorage.fromParams = fromParams

  });

 
  $scope.countContinue = function() {
    if($scope.modeldata.name!=null || $scope.selectSourceType!=null){
      $scope.continueCount = $scope.continueCount + 1;
      if ($scope.continueCount >= 2) {
        $scope.isSubmitShow = true;
      } else {
        $scope.isSubmitShow = false;
      }
    }
  }
  $scope.countBack = function() {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;

  }//End showFunctionGraph


  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false
  }
  $scope.enableEdit=function (uuid,version) {
    $scope.showPage()
    $state.go('createmodel', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }

  $scope.showview=function (uuid,version) {
    if(!$scope.isEdit){
      $scope.showPage()
      $state.go('createmodel', {
        id: uuid,
        version: version,
        mode:'true'
      });
    }     
  }
  $scope.changeScript= function(){
    $scope.checkboxCustom=$scope.scriptType =="SPARK"?false:true

  }

  // $scope.onChageTrainPercent=function(){
  //   $scope.modeldata.valPercent=100-$scope.modeldata.trainPercent;
  // }

  // $scope.onChageValPercent=function(){
  //   $scope.modeldata.trainPercent=100-$scope.modeldata.valPercent;
  
  // }
  // $scope.maxLengthCheck=function(value) {
    
  //   if (value.length > 2)
  //     value = value.slice(0, 2)
  // }

  $scope.selectAllRow = function() {
    angular.forEach($scope.featureTableArray, function(attribute) {
      attribute.selected = $scope.slectAllRow;
    });
  }

  $scope.addRow=function() {
    if ($scope.featureTableArray == null) {
      $scope.featureTableArray = [];
    }
    var len = $scope.featureTableArray.length + 1
    var feature= {};
    feature.featureId="1"
    feature.name = "";
    feature.type =  $scope.featuureType[0];
    feature.desc = "";
    feature.minVal = "";
    feature.maxVal=""
    feature.paramListInfo={};
    $scope.featureTableArray.splice($scope.featureTableArray.length, 0, feature);
  }

  $scope.removeRow = function() {
    $scope.slectAllRow = false;
    var newDataList = [];
    angular.forEach($scope.featureTableArray, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    $scope.featureTableArray = newDataList;
  }

  $scope.onChangeFeatureType=function(index){
    if($scope.featureTableArray[index].type=="string"){
      $scope.featureTableArray[index].isMinMaxDiabled=true;
    }else{
      $scope.featureTableArray[index].isMinMaxDiabled=false;
    }
  }
  $scope.getAllVersion = function(uuid) {
    ModelService.getAllVersionByUuid(uuid, "model").then(function(response) {
      onGetAllVersionByUuid(response.data)
    });
    var onGetAllVersionByUuid = function(response) {
      for (var i = 0; i < response.length; i++) {
        var modelversion = {};
        modelversion.version = response[i].version;
        $scope.model.versions[i] = modelversion;
      }
    } //End getAllVersionByUuid
  } //End GetAllVersion
  
  $scope.getAllLatest=function(defaultValue){
    ModelService.getAllLatest($scope.selectedDependsOnType).then(function(response) { onGetAllLatest(response.data)});
    var onGetAllLatest = function(response) {
      $scope.allDependsOn= response
      if(defaultValue)
        $scope.selectedDependsOn= $scope.allDependsOn[0];
      $scope.onChangeDependsOn();
    }
  }

  
    $scope.getFormulaByType=function(defaultValue){
      ModelService.getFormulaByType("formula").then(function(response) { onGetFormulaByType(response.data)});
      var onGetFormulaByType = function(response) {
        $scope.allDependsOn = response
        if(defaultValue)
        $scope.selectedDependsOn= $scope.allDependsOn[0]
        $scope.onChangeDependsOn()
       
      }
    }
  
  $scope.getParamListByFormula=function(){
    ModelService.getParamListByFormula($scope.selectedDependsOn.uuid,"paramlsit").then(function(response) { onGetParamListByFormula(response.data )});
    var onGetParamListByFormula = function(response) {
      $scope.allParamlist = response
     
    }
  }
  // $scope.getAllLatestAlgorithm=function(){
  //   ModelService.getAllLatest("algorithm").then(function(response) { onGetAllLatestAlgorithm(response.data )});
  //   var onGetAllLatestAlgorithm = function(response) {
  //     $scope.allalgorithm = response
     
  //   }
  // }
  
  // $scope.selectType = function() {
  //   ModelService.getAllLatest($scope.selectSourceType).then(function(response) { onGetAllLatest(response.data)});
  //   var onGetAllLatest = function(response) {
  //     $scope.allsource = response
  //     $scope.selectSource= $scope.allsource[0]
  //     $scope.selectalgorithm=$scope.allalgorithm[0]
  //     $scope.onChangeSource()
  //   }
  // }

  $scope.onChangeDependsOnType=function(defaultValue){
    if($scope.selectedDependsOnType =='algorithm'){
      $scope.getAllLatest(defaultValue);
    }else{
      $scope.getFormulaByType(defaultValue);
    }
    
  }

  $scope.onChangeDependsOn = function() {
    
      if ($scope.allDependsOn != null && $scope.selectedDependsOn != null && $scope.selectedDependsOnType == "formula") {
       $scope.isParamListShow=true;
       $scope.getParamListByFormula();
      }else{
        $scope.isParamListShow=false;
      }
    }

    $scope.onChangeParamInfo=function(index){
      $scope.featureTableArray[index].name=$scope.featureTableArray[index].paramListInfo.paramName;
      $scope.featureTableArray[index].type=$scope.featureTableArray[index].paramListInfo.paramType.toLowerCase();
      if($scope.featureTableArray[index].type){
        $scope.isMinMaxDiabled=true;
      }
    }

  /*$scope.onChangeSource = function() {
    if ($scope.allsource != null && $scope.selectSource != null) {
      $scope.onChangeAlgorithm()
      $scope.getAllLabel();
    }
  }*/


  /*$scope.getAllAttributeBySource=function(){
    ModelService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function(response) {
      onSuccessGetAllAttributeBySource(response.data)
    });
    var onSuccessGetAllAttributeBySource = function(response) {
      $scope.allsourceLabel = response
    }
    
  }*/

  /*$scope.getAllLabel=function(){
    if($scope.selectSourceType !="datapod"){
      $scope.getAllAttributeBySource();
    }else{
	  ModelService.getLatestByUuid($scope.selectSource.uuid, "datapod").then(function(response) { onSuccessGetLatestByUuidForDatapod(response.data )});
	  var onSuccessGetLatestByUuidForDatapod = function(response) {
	    var attributesArray=[];
	    for (var i = 0; i < response.attributes.length; i++) {
        if (response.attributes[i].type == "integer" || response.attributes[i].type == "double") {
          var attributesJson = {};
          attributesJson.uuid = response.uuid;
          attributesJson.version = response.version;
          attributesJson.name = response.name;
          attributesJson.type = $scope.selectSourceType;
          attributesJson.dname = response.name + "_" + response.attributes[i].name;
          attributesJson.attrName = response.attributes[i].name;
          attributesJson.attributeId = response.attributes[i].attributeId;
          attributesArray.push(attributesJson);
        }
	      $scope.allsourceLabel = attributesArray
	    }
    }
  }
  }*/
  
  $scope.getModelScript=function(uuid,version){
    ModelService.getModelScript(uuid,version).then(function(response) {onGetModelScript(response)});
    var onGetModelScript = function(response) {
     $scope.scriptCode=response.data;
    }
  }
  /*$scope.onChangeAlgorithm = function() {
    $scope.isShowExecutionparam = false;
    $scope.checkboxModelexecution = "NO";
    $scope.allparamset = null;
    console.log($scope.selectalgorithm)
    // ModelService.getLatestByUuid($scope.selectalgorithm.uuid, "algorithm").then(function(response) {
    // onSuccessGetLatestByUuidForAlgorithm(response.data)});
    // var onSuccessGetLatestByUuidForAlgorithm = function(response) {
    //     console.log(JSON.stringify(response));
    // 	if (response.labelRequired == "Y") {
    // 	      $scope.isLabelDisable = false;
    // 	    //  $scope.getAllLabel();
    // 	}
    //     else {
    //     	$scope.allsourceLabel = null;
    //     	$scope.isLabelDisable = true;
    //    }
    //   }
  }*/

  if (typeof $stateParams.id != "undefined") {
    $scope.showactive="true"
    $scope.mode = $stateParams.mode;
    $scope.isDependencyShow = true;
    $scope.getAllVersion($stateParams.id)
    ModelService.getOneByUuidandVersion($stateParams.id,$stateParams.version,"model").then(function(response) {onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.modeldata = response
      $scope.scriptType=response.type
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.model.defaultVersion = defaultversion;
      if($scope.modeldata.type=='SPARK'){
       // $scope.selectSourceType = response.source.ref.type
       // $scope.paramTable = response.execParams;
      // $scope.getAllLatest();
        // var source = {}
        // source.uuid = $scope.modeldata.source.ref.uuid;
        // source.name = $scope.modeldata.source.ref.name;
        // $scope.selectSource = source;  
        // if($scope.modeldata.label !=null){ 
        //   $scope.isLabelDisable = false;
        //   $scope.getAllLabel();
        //   var selectLabel = {};
        //   if($scope.modeldata.label.ref !=null){
        //     selectLabel.uuid = $scope.modeldata.label.ref.uuid;
        //     selectLabel.attributeId = $scope.modeldata.label.attrId;
        //     $scope.selectLabel = selectLabel;
        //   }
        // }//End  If LabelRequired
        $scope.selectLabel = $scope.modeldata.label;
        $scope.selectedDependsOnType=$scope.modeldata.dependsOn.ref.type
        $scope.onChangeDependsOnType(false);
        var selectedDependsOn = {}
        selectedDependsOn.uuid = $scope.modeldata.dependsOn.ref.uuid;
        selectedDependsOn.name = $scope.modeldata.dependsOn.ref.name;
        $scope.selectedDependsOn = selectedDependsOn;
        if($scope.selectedDependsOnType =="formula"){
          $scope.getParamListByFormula();
          $scope.isParamListShow=true
        }
        //$scope.getAllLatestAlgorithm();
        // var algorithm = {}
        // algorithm.uuid = $scope.modeldata.algorithm.ref.uuid;
        // algorithm.name = $scope.modeldata.algorithm.ref.name;
        // $scope.selectalgorithm = algorithm
        $scope.checkboxCustom=false
        $scope.featureTableArray=[];
        for(var i=0;i< $scope.modeldata.features.length;i++){
          var featureObj={};
          featureObj.featureId=$scope.modeldata.features[i].featureId
          featureObj.name=$scope.modeldata.features[i].name
          featureObj.type=$scope.modeldata.features[i].type
          featureObj.desc=$scope.modeldata.features[i].desc
          featureObj.minVal=$scope.modeldata.features[i].type =="string"?"":$scope.modeldata.features[i].minVal
          featureObj.maxVal=$scope.modeldata.features[i].type =="string"?"":$scope.modeldata.features[i].maxVal
          featureObj.isMinMaxDiabled=$scope.modeldata.features[i].type=="string"?true:false;
          if($scope.selectedDependsOnType== "formula" && $scope.modeldata.features[i].paramListInfo !=null){
            var paramListInfo={};
            paramListInfo.uuid=$scope.modeldata.features[i].paramListInfo.ref.uuid;
            paramListInfo.name=$scope.modeldata.features[i].paramListInfo.ref.name;
            paramListInfo.paramId=$scope.modeldata.features[i].paramListInfo.paramId;
            featureObj.paramListInfo=paramListInfo;
          } 
          $scope.featureTableArray[i]=featureObj; 
        }
       
      } //End Type Spark
      else{
        $scope.checkboxCustom=true;
        $scope.getModelScript(response.uuid,response.version)
      }
    }//End 
  } //End If onSuccessGetLatestByUuid
  else {
    $scope.showactive="false"
   // $scope.getAllLatestAlgorithm();
    $scope.onChangeDependsOnType(true);
  }
  

  $scope.selectVersion=function(uuid,version){
    ModelService.getOneByUuidandVersion(uuid,version,"model").then(function(response) {onSuccessGetLatestByUuid(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.modeldata = response
      $scope.scriptType=response.type
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.model.defaultVersion = defaultversion;
      if($scope.modeldata.type=='SPARK'){
        //$scope.selectSourceType = response.source.ref.type
       // $scope.paramTable = response.execParams;
       //$scope.getAllLatest();
        // var source = {}
        // source.uuid = $scope.modeldata.source.ref.uuid;
        // source.name = $scope.modeldata.source.ref.name;
        // $scope.selectSource = source;  
        // if($scope.modeldata.label !=null){ 
        //   $scope.isLabelDisable = false;
        //   $scope.getAllLabel();
        //   var selectLabel = {};
        //   if($scope.modeldata.label.ref !=null){
        //     selectLabel.uuid = $scope.modeldata.label.ref.uuid;
        //     selectLabel.attributeId = $scope.modeldata.label.attrId;
        //     $scope.selectLabel = selectLabel;
        //   }
        // }//End  If LabelRequired
        $scope.selectLabel = $scope.modeldata.label;
        $scope.selectedDependsOnType=$scope.modeldata.dependsOn.ref.type
        $scope.onChangeDependsOnType(false);
        var selectedDependsOn = {}
        selectedDependsOn.uuid = $scope.modeldata.dependsOn.ref.uuid;
        selectedDependsOn.name = $scope.modeldata.dependsOn.ref.name;
        $scope.selectedDependsOn = selectedDependsOn;
        if($scope.selectedDependsOnType =="formula"){
          $scope.getParamListByFormula();
          $scope.isParamListShow=true
        }
        // $scope.getAllLatestAlgorithm();
        // var algorithm = {}
        // algorithm.uuid = $scope.modeldata.algorithm.ref.uuid;
        // algorithm.name = $scope.modeldata.algorithm.ref.name;
        // $scope.selectalgorithm = algorithm
        $scope.checkboxCustom=false
        $scope.featureTableArray=[];
        for(var i=0;i< $scope.modeldata.features.length;i++){
          var featureObj={};
          featureObj.featureId=$scope.modeldata.features[i].featureId
          featureObj.name=$scope.modeldata.features[i].name
          featureObj.type=$scope.modeldata.features[i].type
          featureObj.desc=$scope.modeldata.features[i].desc
          featureObj.minVal=$scope.modeldata.features[i].type =="string"?"":$scope.modeldata.features[i].minVal
          featureObj.maxVal=$scope.modeldata.features[i].type =="string"?"":$scope.modeldata.features[i].maxVal
          featureObj.isMinMaxDiabled=$scope.modeldata.features[i].type [i].type =="string"?true:false;
          if($scope.selectedDependsOnType== "formula" &&  $scope.modeldata.features[i].paramListInfo){
            var paramListInfo={};
            paramListInfo.uuid=$scope.modeldata.features[i].paramListInfo.ref.uuid;
            paramListInfo.name=$scope.modeldata.features[i].paramListInfo.ref.name;
            paramListInfo.paramId=$scope.modeldata.features[i].paramListInfo.paramId;
            featureObj.paramListInfo=paramListInfo;
          }  
          $scope.featureTableArray[i]=featureObj; 
        }
       
      } //End Type Spark
      else{
        $scope.checkboxCustom=true;
        $scope.getModelScript(response.uuid,response.version)
      }
  }
}
  $scope.submitModel = function() {
    $scope.isshowmodel = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var modelJson = {}
    modelJson.uuid = $scope.modeldata.uuid
    modelJson.name = $scope.modeldata.name
    modelJson.desc = $scope.modeldata.desc
    modelJson.active = $scope.modeldata.active;
    modelJson.published=$scope.modeldata.published;
   // modelJson.trainPercent=70//$scope.modeldata.trainPercent
    //modelJson.valPercent=30//$scope.modeldata.valPercent
    
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    modelJson.tags = tagArray;
    if(!$scope.checkboxCustom){
      modelJson.type="SPARK"
      modelJson.customFlag="N"
      //  var source = {};
      // var ref = {};
      // ref.type = $scope.selectSourceType;
      // ref.uuid = $scope.selectSource.uuid;
      // source.ref = ref;
      // modelJson.source = source;

      // var algorithm = {};
      // var ref = {};
      // ref.type = "algorithm";
      // ref.uuid = $scope.selectalgorithm.uuid;
      // algorithm.ref = ref;
      // modelJson.algorithm = algorithm;

      var dependsOn = {};
      var ref = {};
      ref.type = $scope.selectedDependsOnType;
      ref.uuid = $scope.selectedDependsOn.uuid;
      dependsOn.ref = ref;
      modelJson.dependsOn = dependsOn;
      
      
      // if ($scope.isLabelDisable == false) {
      //   var label = {};
      //   var ref = {};
      //   ref.type = $scope.selectSourceType
      //   ref.uuid = $scope.selectLabel.uuid
      //   label.ref = ref;
      //   label.attrId = $scope.selectLabel.attributeId
      //   modelJson.label = label;
      //   modelJson.labelRequired="Y"
      // } 
      // else {
      //   modelJson.label = null;
      //   modelJson.labelRequired="N"
      // }
      modelJson.label=$scope.selectLabel;
      var featureArray=[];
      if($scope.featureTableArray.length >0){
        for(var i=0;i< $scope.featureTableArray.length;i++){
        var featureObj={};
        featureObj.featureId=i
        featureObj.name=$scope.featureTableArray[i].name
        featureObj.type=$scope.featureTableArray[i].type
        featureObj.desc=$scope.featureTableArray[i].desc
        featureObj.minVal=$scope.featureTableArray[i].type =="string"?"":$scope.featureTableArray[i].minVal
        featureObj.maxVal=$scope.featureTableArray[i].type =="string"?"":$scope.featureTableArray[i].maxVal
        if($scope.selectedDependsOnType =="formula" && $scope.allParamlist.length >0){
          var paramListInfo={};
          var ref={};
          ref.uuid=$scope.featureTableArray[i].paramListInfo.uuid;
          ref.type="paramlist";
          paramListInfo.ref=ref;
          paramListInfo.paramId=$scope.featureTableArray[i].paramListInfo.paramId;
          featureObj.paramListInfo=paramListInfo;
        }else{
          featureObj.paramListInfo=null
        }
        featureArray[i]=featureObj;
      }

      }
      modelJson.features=featureArray;
      ModelService.submit(modelJson, 'model').then(function(response) { onSuccess(response.data)},function(response){onError(response.data)});
    }
    else{
      modelJson.customFlag="Y"
      var blob = new Blob([$scope.scriptCode], { type: "text/xml"});
      var fd = new FormData();
      fd.append('file', blob)
      var filetype=$scope.scriptType =="PYTHON"?'py':'R'
      ModelService.uploadFile(filetype,fd,"script").then(function(response){onSuccessUpload(response.data)});
      var onSuccessUpload=function(response){
        modelJson.type=$scope.scriptType
        modelJson.scriptName=response;
        result = response.split("_")
        modelJson.uuid=result[0]
        modelJson.version=result[1].split(".")[0]
        ModelService.submit(modelJson, 'model').then(function(response) {onSuccess(response.data)},function(response){onError(response.data)});
      }
    }
    console.log(JSON.stringify(modelJson));
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = true;
      $scope.changemodelvalue();
      if ($scope.checkboxModelexecution == "YES") {
        ModelService.getOneById(response, "model").then(function(response) { onSuccessGetOneById(response.data)});
        var onSuccessGetOneById = function(result) {
          $scope.modelExecute(result);
        }
      } //End if
      else {
        notify.type='success',
        notify.title= 'Success',
        notify.content='Model Saved Successfully'
        $scope.$emit('notify', notify);
        $scope.okmodelsave();
      }
    }
    var onError = function(response) {
      notify.type='error',
      notify.title= 'Error',
      notify.content="Some Error Occurred"
      $scope.$emit('notify', notify);
    }
  }

  $scope.okmodelsave = function() {
    $('#modelsave').css("dispaly", "none");
    var hidemode = "yes";
    if (hidemode == 'yes') {
      setTimeout(function() {
        $state.go("model");
      }, 2000);
    }
  }
  $scope.changemodelvalue = function() {
    $scope.isshowmodel = sessionStorage.isshowmodel
  };


}); //End CreateModelController







