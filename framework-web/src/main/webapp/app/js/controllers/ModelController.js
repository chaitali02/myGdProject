/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateModelController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, ModelService, $http, $location, $anchorScroll, privilegeSvc, CommonService, CommonFactory,CF_ENCODINGTYPE) {
  $scope.featuureType=["integer","string","double","vector"];
  $scope.mode = "false";

  if($stateParams.mode =='true'){
    $scope.isEdit=false;
    $scope.isversionEnable=false;
    $scope.isAdd=false;
    $scope.isDragable="false";
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});  
  }
  else if($stateParams.mode =='false'){
    $scope.isEdit=true;
    $scope.isversionEnable=true;
    $scope.isAdd=false;
    $scope.isDragable="true";
    var privileges = privilegeSvc.privileges['comment'] || [];
		$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
		$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
		$scope.$on('privilegesUpdated', function (e, data) {
			var privileges = privilegeSvc.privileges['comment'] || [];
			$rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
			$rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
			
		});
    
  }
  else{
    $scope.isAdd=true;
    $scope.isDragable="true";
  }
  $scope.userDetail={}
	$scope.userDetail.uuid= $rootScope.setUseruuid;
	$scope.userDetail.name= $rootScope.setUserName;
  $scope.isSubmitEnable = true;
  $scope.modeldata;
  $scope.showForm = true;
  $scope.data = null;
  $scope.showGraphDiv = false
  $scope.model = {};
  $scope.model.versions = [];
  $scope.isshowmodel = false;
  $scope.isAlgorithmPCA=false;
  //$scope.SourceTypes = ["datapod", "modeldata"];
  $scope.imputeTypes=["custom","function"];
  $scope.dependsOnType= ["algorithm", "formula"];
  $scope.selectedDependsOnType=$scope.dependsOnType[0];
  $scope.type = ["string", "double", "date"];
  $scope.scriptTypes= ["SPARK","PYTHON", "R","DL4J","TENSORFLOW"];
  $scope.scriptTypeMapping={"SPARK":"SPARKML","PYTHON":"PYTHON","R":"R","DL4J":"DL4J","TENSORFLOW":"TENSORFLOW"};
  //$scope.scriptType="SPARK"
  $scope.count = 0;
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isLabelDisable = true;
  $scope.isDependencyShow = false;
  $scope.encodingTypes=CF_ENCODINGTYPE.encodingType;//["ORDINAL", "ONEHOT", "BINARY", "BASEN","HASHING"];

  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 30000 //time in ms
  };
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
  
	$scope.onChangeName = function (data) {
		$scope.modeldata.displayName=data;
	}

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

  $scope.focusRow = function(rowId){
    
    $timeout(function() {
      $location.hash(rowId);
      $anchorScroll();
    });
  }

  $scope.showGraph = function(uuid, version) {
    $scope.showForm = false;
    $scope.showGraphDiv = true;

  }//End showFunctionGraph


  $scope.showPage = function() {
    $scope.showForm = true;
    $scope.showGraphDiv = false
  }

  $scope.showHome=function(uuid, version,mode){
		$scope.showPage()
		$state.go('createmodel', {
			id: uuid,
			version: version,
			mode: mode
		});
	}
  $scope.enableEdit=function (uuid,version) {
    
    if($scope.isPrivlage || $scope.modeldata.locked =="Y"){
      return false;
    }   
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
    if($scope.scriptType =="SPARK" || $scope.scriptType =="PYTHON"){
      $scope.checkboxCustom=false;
    }else{
      $scope.checkboxCustom=true;
    }
    $scope.onChangeDependsOnType(true);

  }  
  
  $scope.isDublication = function (arr, field, index, name) {
		var res = -1;
		for (var i = 0; i < arr.length ; i++) {
			if (arr[i][field] == arr[index][field] && i != index) {
			    $scope.myform2[name].$invalid = true;
				  res = i;
				  break
      } 
      else {
				$scope.myform2[name].$invalid = false;	
			}
		}
		return res;
  }
  
  $scope.onChangeFeatureName = function (index) {
      if ($scope.featureTableArray[index].name) {
        var res = $scope.isDublication($scope.featureTableArray, "name", index, "featureName" + index);
        if (res != -1) {
          $scope.isDuplication = true;
        } else {
          $scope.isDuplication = false;
        }
      }
    //	console.log($scope.myform3)
  
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
    feature.featureId="1";
    var temLen=len-1;
    
    if($scope.featureTableArray.length ==0){
      feature.id = $scope.featureTableArray.length ;//len - 1;
		}else{
      feature.id =CommonFactory.getMaxSourceSeqId($scope.featureTableArray,"id")+1;
		}
   
    feature.name = "feature"+len;
    feature.type =  $scope.featuureType[0];
    feature.desc = "";
    feature.minVal = "";
    feature.maxVal=""
    feature.paramListInfo={};
    $scope.featureTableArray.splice($scope.featureTableArray.length, 0, feature);
    $scope.focusRow(len-1)
    // setTimeout(function(){
    //   $scope.myform2["featureName"+temLen].$invalid = false;
    // });
  }
  $scope.ondrop = function(e) {
		console.log(e);
		$scope.myform2.$dirty=true;
	}
  $scope.onAttrRowDown=function(index){
	  var rowTempIndex=$scope.featureTableArray[index];
    var rowTempIndexPlus=$scope.featureTableArray[index+1];
		$scope.featureTableArray[index]=rowTempIndexPlus;
		$scope.featureTableArray[index+1]=rowTempIndex;
  }
  
	$scope.onAttrRowUp=function(index){
		var rowTempIndex=$scope.featureTableArray[index];
    var rowTempIndexMines=$scope.featureTableArray[index-1];
		$scope.featureTableArray[index]=rowTempIndexMines;
		$scope.featureTableArray[index-1]=rowTempIndex;
	}


  $scope.removeRow = function() {
    $scope.slectAllRow = false;
    var newDataList = [];
    angular.forEach($scope.featureTableArray, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
        $scope.attrTableSelectedItem=[];
      }
    });
    $scope.featureTableArray = newDataList;
  }

  $scope.onChangeFeatureType=function(index){
    if($scope.featureTableArray[index].type=="string" || $scope.featureTableArray[index].type=="vector"){
      $scope.featureTableArray[index].isMinMaxDiabled=true;
      $scope.featureTableArray[index].minVal="";
      $scope.featureTableArray[index].maxVal="";
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
    // ModelService.getAllLatest($scope.selectedDependsOnType).then(function(response) { onGetAllLatest(response.data)});
    // var onGetAllLatest = function(response) {
    //   $scope.allDependsOn= response
    //   if(defaultValue)
    //     $scope.selectedDependsOn= $scope.allDependsOn[0];
    //   $scope.onChangeDependsOn();
    // }
    
    ModelService.getAlgorithmByLibrary($scope.scriptTypeMapping[$scope.scriptType],$scope.selectedDependsOnType).then(function(response) { onSuccessGetAlgorithmByLibrary(response.data)});
    var onSuccessGetAlgorithmByLibrary = function(response) {
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
        $scope.isAlgorithmPCA=false;
        $scope.isParamListShow=false;
        if($scope.selectedDependsOn !=null)
          $scope.isAlgorithmPCA=$scope.selectedDependsOn.trainClass.indexOf("PCA") !=-1?true:false;
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
  $scope.getOneByUuidAndVersionAlgorithm=function(data){
    ModelService.getOneByUuidandVersion(data.uuid,"","algorithm")
    .then(function(response) {onSuccessGetLatestByUuid(response.data)}, function(response) {onError(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.selectedDependsOn=response;
      $scope.isAlgorithmPCA=$scope.selectedDependsOn.trainClass.indexOf("PCA") !=-1?true:false;

    }
  }
  $scope.getFunctionByCategory=function(){
    ModelService.getFunctionByCategory("function","aggregate").then(function(response) { onSuccessGetFunctionByCategory(response.data)});
    var onSuccessGetFunctionByCategory = function(response) {
     $scope.allFunction=response
    }
  }
  $scope.onChangeInputeType=function(index,imputeType){
    if(imputeType=="default"){
      $scope.featureTableArray[index].imputeMethod.isModelShow=true;
      $scope.featureTableArray[index].imputeMethod.isSimpleShow=false;
      $scope.featureTableArray[index].imputeMethod.isFunctionShow=false;
    }
    else if(imputeType=="custom"){
      $scope.featureTableArray[index].imputeMethod.isModelShow=false;
      $scope.featureTableArray[index].imputeMethod.isSimpleShow=true;
      $scope.featureTableArray[index].imputeMethod.isFunctionShow=false;
    }
    else if(imputeType=="function"){
      $scope.featureTableArray[index].imputeMethod.isModelShow=false;
      $scope.featureTableArray[index].imputeMethod.isSimpleShow=false;
      $scope.featureTableArray[index].imputeMethod.isFunctionShow=true;
      $scope.getFunctionByCategory();
    }
  }

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
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.getAllVersion($stateParams.id)
    ModelService.getOneByUuidandVersion($stateParams.id,$stateParams.version,"model")
      .then(function(response) {onSuccessGetLatestByUuid(response.data)}, function(response) {onError(response.data)});
    var onSuccessGetLatestByUuid = function(response) {
      $scope.modeldata = response;
      $scope.isEditInprogess=false;
      $scope.scriptType=response.type
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.model.defaultVersion = defaultversion;
      var tags = [];
			if (response.tags != null) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
					$scope.tags = tags;
				}
			}
      if($scope.modeldata.type=='SPARK' || ($scope.modeldata.type=='PYTHON' && $scope.modeldata.customFlag =="N")){
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
        $scope.getOneByUuidAndVersionAlgorithm(selectedDependsOn);
        if($scope.selectedDependsOnType =="formula"){
          $scope.getParamListByFormula();
          $scope.isParamListShow=true
        }
        
        $scope.checkboxCustom=false
        $scope.featureTableArray=[];
        for(var i=0;i< $scope.modeldata.features.length;i++){
          var featureObj={};
          var imputeMethod={};
          featureObj.featureId=$scope.modeldata.features[i].featureId;
          featureObj.id=parseInt($scope.modeldata.features[i].featureId);
          featureObj.name=$scope.modeldata.features[i].name;
          featureObj.type=$scope.modeldata.features[i].type;
          featureObj.desc=$scope.modeldata.features[i].desc;
          featureObj.encodingType=$scope.modeldata.features[i].encodingType;
          featureObj.minVal=($scope.modeldata.features[i].type =="string" || $scope.modeldata.features[i].type =="vector")?"":$scope.modeldata.features[i].minVal
          featureObj.maxVal=($scope.modeldata.features[i].type =="string" || $scope.modeldata.features[i].type =="vector")?"":$scope.modeldata.features[i].maxVal
          featureObj.defaultValue=$scope.modeldata.features[i].defaultValue;
          featureObj.isMinMaxDiabled=($scope.modeldata.features[i].type=="string" || $scope.modeldata.features[i].type=="vector")?true:false;
          if(response.features[i].imputeMethod !=null){
            if(response.features[i].imputeMethod.ref.type =="simple"){
              imputeMethod.imputeType="custom";
              imputeMethod.imputeValue=response.features[i].imputeMethod.value;
              imputeMethod.isSimpleShow=true;
              imputeMethod.isFunctionShow=false;
            }
            else if(response.features[i].imputeMethod.ref.type =="function"){
              imputeMethod.imputeType="function";
              imputeMethod.isSimpleShow=false;
              imputeMethod.isFunctionShow=true;
              $scope.getFunctionByCategory();
              var selectedFunction={};
              selectedFunction.uuid = response.features[i].imputeMethod.ref.uuid;
              selectedFunction.type = response.features[i].imputeMethod.ref.type;
              imputeMethod.selectedFunction=selectedFunction;
            }
            featureObj.imputeMethod=imputeMethod;
          }
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
    var onError =function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    } 
  } //End If onSuccessGetLatestByUuid
  else {
    $scope.modeldata={};
    $scope.modeldata.locked="N";
    $scope.showactive="false"
    $scope.addRow();
   // $scope.getAllLatestAlgorithm();
   // $scope.onChangeDependsOnType(true);
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
      if($scope.modeldata.type=='SPARK' || ($scope.modeldata.type=='PYTHON' && $scope.modeldata.customFlag =="N")){
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
          featureObj.featureId=$scope.modeldata.features[i].featureId;
          featureObj.id=parseInt($scope.modeldata.features[i].featureId);
          featureObj.name=$scope.modeldata.features[i].name;
          featureObj.type=$scope.modeldata.features[i].type;
          featureObj.desc=$scope.modeldata.features[i].desc;
          featureObj.encodingType= $scope.modeldata.features[i].encodingType;
          featureObj.minVal=($scope.modeldata.features[i].type =="string" || $scope.modeldata.features[i].type =="vector")?"":$scope.modeldata.features[i].minVal
          featureObj.maxVal=($scope.modeldata.features[i].type =="string" || $scope.modeldata.features[i].type =="vector")?"":$scope.modeldata.features[i].maxVal
          featureObj.defaultValue=$scope.modeldata.features[i].defaultValue;
          featureObj.isMinMaxDiabled=($scope.modeldata.features[i].type =="string" ||$scope.modeldata.features[i].type =="vector")?true:false;
          if(response.features[i].imputeMethod !=null){
            if(response.features[i].imputeMethod.ref.type =="simple"){
              imputeMethod.imputeType="custom";
              imputeMethod.imputeValue=response.features[i].imputeMethod.value;
              imputeMethod.isSimpleShow=true;
              imputeMethod.isFunctionShow=false;
            }
            else if(response.features[i].imputeMethod.ref.type =="function"){
              imputeMethod.imputeType="function";
              imputeMethod.isSimpleShow=false;
              imputeMethod.isFunctionShow=true;
              $scope.getFunctionByCategory();
              var selectedFunction={};
              selectedFunction.uuid = response.features[i].imputeMethod.ref.uuid;
              selectedFunction.type = response.features[i].imputeMethod.ref.type;
              imputeMethod.selectedFunction=selectedFunction;
            }
            featureObj.imputeMethod=imputeMethod;
  
          }
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
    var upd_tag="N"
    $scope.isshowmodel = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = true;
    var modelJson = {}
    modelJson.uuid = $scope.modeldata.uuid
    modelJson.name = $scope.modeldata.name;
    modelJson.displayName = $scope.modeldata.displayName;
    modelJson.desc = $scope.modeldata.desc
    modelJson.active = $scope.modeldata.active;
    modelJson.locked = $scope.modeldata.locked;
    modelJson.published=$scope.modeldata.published;
    modelJson.publicFlag=$scope.modeldata.publicFlag;

   // modelJson.trainPercent=70//$scope.modeldata.trainPercent
    //modelJson.valPercent=30//$scope.modeldata.valPercent
    
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
    modelJson.tags = tagArray;
    
    modelJson.type=$scope.scriptType;
    if(!$scope.checkboxCustom){
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

      // var dependsOn = {};
      // var ref = {};
      // ref.type = $scope.selectedDependsOnType;
      // ref.uuid = $scope.selectedDependsOn.uuid;
      // dependsOn.ref = ref;
      // modelJson.dependsOn = dependsOn;
      
      
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
      var dependsOn = {};
      var ref = {};
      ref.type = $scope.selectedDependsOnType;
      ref.uuid = $scope.selectedDependsOn.uuid;
      dependsOn.ref = ref;
      modelJson.dependsOn = dependsOn;
      var featureArray=[];
      if($scope.featureTableArray.length >0){
        for(var i=0;i< $scope.featureTableArray.length;i++){
        var featureObj={};
        var imputeMethod={};
        var imputeMethodRef={};
        featureObj.featureId =$scope.featureTableArray[i].id;
			  featureObj.featureDisplaySeq = i;
        featureObj.name=$scope.featureTableArray[i].name;
        featureObj.type=$scope.featureTableArray[i].type;
        featureObj.desc=$scope.featureTableArray[i].desc;
        featureObj.encodingType= $scope.featureTableArray[i].encodingType;
        featureObj.minVal=$scope.featureTableArray[i].type =="string"?"":$scope.featureTableArray[i].minVal
        featureObj.maxVal=$scope.featureTableArray[i].type =="string"?"":$scope.featureTableArray[i].maxVal
        featureObj.defaultValue=$scope.featureTableArray[i].defaultValue;
        if($scope.selectedDependsOnType =="formula" && $scope.allParamlist.length >0){
          var paramListInfo={};
          var ref={};
          ref.uuid=$scope.featureTableArray[i].paramListInfo.uuid;
          ref.type="paramlist";
          paramListInfo.ref=ref;
          paramListInfo.paramId=$scope.featureTableArray[i].paramListInfo.paramId;
          featureObj.paramListInfo=paramListInfo;
        }else{
          //featureObj.paramListInfo=null
        }
        if($scope.featureTableArray[i].imputeMethod){
          if($scope.featureTableArray[i].imputeMethod.imputeType =="function"){
            imputeMethodRef.type="function";
            imputeMethodRef.uuid = $scope.featureTableArray[i].imputeMethod.selectedFunction.uuid;
            imputeMethod.ref = imputeMethodRef;
            featureObj.imputeMethod=imputeMethod;
          }
          else{
            imputeMethodRef.type="simple";
            imputeMethod.ref = imputeMethodRef;
            imputeMethod.value = $scope.featureTableArray[i].imputeMethod.imputeValue;
            featureObj.imputeMethod=imputeMethod;
          }
        }
        
        featureArray[i]=featureObj;
      }

      }
      modelJson.features=featureArray;
      ModelService.submit(modelJson, 'model',upd_tag).then(function(response) { onSuccess(response.data)},function(response){onError(response.data)});
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

  $scope.attrTableSelectedItem=[];
	$scope.onChangeAttrRow=function(index,status){
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
	$scope.autoMove=function(index){
		var tempAtrr=$scope.featureTableArray[$scope.attrTableSelectedItem[0]];
		$scope.featureTableArray.splice($scope.attrTableSelectedItem[0],1);
		$scope.featureTableArray.splice(index,0,tempAtrr);
		$scope.attrTableSelectedItem=[];
		$scope.featureTableArray[index].selected=false;
	
	}

	$scope.autoMoveTo=function(index){
		if(index <= $scope.featureTableArray.length){
			$scope.autoMove(index-1,'mapAttr');
			$scope.moveTo=null;
			$(".actions").removeClass("open");
		}
	}

}); //End CreateModelController







