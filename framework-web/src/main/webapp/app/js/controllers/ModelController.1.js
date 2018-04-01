/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('CreateModelController', function($state, $stateParams, $rootScope, $scope, $sessionStorage, $timeout, $filter, ModelService,$http,$location) {

  $scope.mode = "false";
  $scope.dataLoading = false;
  if($stateParams.mode =='true'){
  $scope.isEdit=false;
  $scope.isversionEnable=false;
  }
  else{
  $scope.isEdit=true;
  $scope.isversionEnable=true;
  }
  $scope.isSubmitEnable = true;
  $scope.modeldata;
  $scope.showmodel = true;
  $scope.data = null;
  $scope.showgraph = false
  $scope.showgraphdiv = false
  $scope.graphDataStatus = false
  $scope.model = {};
  $scope.model.versions = [];
  $scope.isshowmodel = false;
  $scope.SourceTypes = ["datapod", "dataset"];
  $scope.paramtable = null;
  $scope.type = ["string", "double", "date"];
  $scope.scriptTypes= ["SPARK","PYTHON", "R"];
  $scope.scriptType="SPARK"
  $scope.count = 0;
  $scope.isSubmitShow = false;
  $scope.continueCount = 1;
  $scope.backCount;
  $scope.isTabelShow = false;
  $scope.isLabelDisable = true;
  $scope.isDependencyShow = false;

  //$scope.scriptCode="testList = ['red', 'blue', 'red', 'green', 'blue', 'blue']testListDict = {}for item in testList:  try:    testListDict[item] += 1  except:    testListDict[item] = 1print(testListDict)"
  
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

  $scope.$watch("isshowmodel", function(newvalue, oldvalue) {
    $scope.isshowmodel = newvalue
    sessionStorage.isshowmodel = newvalue
  });
  $scope.countContinue = function() {
    if($scope.modeldata.name!=null || $scope.selectSourceType!=null){
      $scope.continueCount = $scope.continueCount + 1;
      if ($scope.continueCount >= 3) {
        $scope.isSubmitShow = true;
      } else {
        $scope.isSubmitShow = false;
      }
    }
  }

  $scope.changeCheckboxExecution = function() {
    if ($scope.checkboxModelexecution == "YES" && !$scope.checkboxCustom) {
      ModelService.getParamSetByAlgorithm($scope.selectalgorithm.uuid, $scope.selectalgorithm.version).then(function(response) {onSuccessGetParamSetByAlgorithm(response.data)});
      var onSuccessGetParamSetByAlgorithm = function(response) {
        $scope.allparamset = response
        $scope.isShowExecutionparam = true;

      }
    }else {
      $scope.isShowExecutionparam = false;
      $scope.allparamset = null;
    }
  }
  $scope.onChageTrainPercent=function(){
    $scope.modeldata.valPercent=100-$scope.modeldata.trainPercent;
  }

  $scope.onChageValPercent=function(){
    $scope.modeldata.trainPercent=100-$scope.modeldata.valPercent;
  
  }
  $scope.maxLengthCheck=function(value) {
    
    if (value.length > 2)
      value = value.slice(0, 2)
  }

  $scope.countBack = function() {
    $scope.continueCount = $scope.continueCount - 1;
    $scope.isSubmitShow = false;
  }

  $scope.orderByValue = function(value) {
    return value;
  };

  $scope.onChangeAlgorithm = function() {
    $scope.isShowExecutionparam = false;
    $scope.checkboxModelexecution = "NO";
    $scope.allparamset = null;
    console.log($scope.selectalgorithm)
    ModelService.getLatestByUuid($scope.selectalgorithm.uuid, "algorithm").then(function(response) {
    onSuccessGetLatestByUuidForAlgorithm(response.data)});
    var onSuccessGetLatestByUuidForAlgorithm = function(response) {
        console.log(JSON.stringify(response));
    	if (response.labelRequired == "Y") {
    	      $scope.isLabelDisable = false;
    	      $scope.getAllLabel();
    	}
        else {
        	$scope.allsourceLabel = null;
        	$scope.isLabelDisable = true;
       }
      }
  }
  $scope.getAllLabel=function(){
	  ModelService.getLatestByUuid($scope.selectSource.uuid, "datapod").then(function(response) {
	        onSuccessGetLatestByUuidForDatapod(response.data)
	      });
	      var onSuccessGetLatestByUuidForDatapod = function(response) {
	        console.log(JSON.stringify(response))
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
	            attributesJson.attrId = response.attributes[i].attributeId;
	            attributesArray.push(attributesJson);
	          }
	          $scope.allsourceLabel = attributesArray
	        }

	      }
  }
  $scope.selectAllRow = function() {

    angular.forEach($scope.paramtable, function(stage) {
      stage.selected = $scope.selectallattribute;
    });
  }
  $scope.modelExecute = function(modeldetail) {
    $scope.newDataList = [];
    $scope.selectallattribute = false;
    angular.forEach($scope.paramtable, function(selected) {
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
    console.log(JSON.stringify(execParams));
    ModelService.getExecuteModel(modeldetail.uuid, modeldetail.version, execParams).then(function(response) {
      onSuccessGetExecuteModel(response.data)
    });
    var onSuccessGetExecuteModel = function(response) {
      console.log(JSON.stringify(response));
      $scope.modelmessage = "Model Saved and Submited Successfully"
      notify.type='success',
      notify.title= 'Success',
     notify.content=$scope.modelmessage;
     $scope.$emit('notify', notify);
     $scope.okmodelsave();
    }
  }
  $scope.onSelectparamSet = function() {
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
      $scope.isTabelShow = false;
    }
  }
  $scope.addRow = function() {
    if ($scope.paramtable == null) {
      $scope.paramtable = [];
    }
    var paramjson = {}
    paramjson.paramId = $scope.paramtable.length;
    $scope.paramtable.splice($scope.paramtable.length, 0, paramjson);
  }

  $scope.selectAllRow = function() {
    angular.forEach($scope.paramtable, function(stage) {
      stage.selected = $scope.selectallattribute;
    });
  }
  $scope.removeRow = function() {
    var newDataList = [];
    $scope.selectallattribute = false;
    angular.forEach($scope.paramtable, function(selected) {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    $scope.paramtable = newDataList;
  }

  $scope.showModelGraph = function(uuid, version) {
    $scope.showmodel = false;
    $scope.showgraph = false
    $scope.graphDataStatus = true
    $scope.showgraphdiv = true;

  } //End showFunctionGraph


  $scope.showModelPage = function() {
    $scope.showmodel = true;
    $scope.showgraph = false
    $scope.graphDataStatus = false;
    $scope.showgraphdiv = false
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
  
  $scope.getModelScript=function(uuid,version){
    ModelService.getModelScript(uuid,version).then(function(response) {
      onGetModelScript(response)
    });
    var onGetModelScript = function(response) {
     $scope.scriptCode=response.data;
    }
  }

  $scope.selectType = function() {

    ModelService.getAllLatest($scope.selectSourceType).then(function(response) {
      onGetAllLatest(response.data)
    });
    var onGetAllLatest = function(response) {
      $scope.allsource = response
      $scope.selectSource= $scope.allsource[0]
      $scope.selectalgorithm=$scope.allalgorithm[0]
      $scope.onChangeSource()
    }
  }

  $scope.onChangeSource = function() {

    if ($scope.allsource != null && $scope.selectSource != null) {
      ModelService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function(response) {
        onSuccessGetAllAttributeBySource(response.data)
      });
      var onSuccessGetAllAttributeBySource = function(response) {
        $scope.allattribute = response
        $scope.sourceAttributeTags = null;
        $scope.getAllLabel();

      }
    }
  }

  $scope.loadSourceAttribute = function(query) {
    return $timeout(function() {
      return $filter('filter')($scope.allattribute, query);
    });
  };

  $scope.clear = function() {
    $scope.sourceAttributeTags = null;
  }

  if (typeof $stateParams.id != "undefined") {
    $scope.showactive="true"
    $scope.mode = $stateParams.mode;

    $scope.isDependencyShow = true;
    $scope.getAllVersion($stateParams.id)
    ModelService.getOneByUuidandVersion($stateParams.id,$stateParams.version,"model").then(function(response) {
      onSuccessGetLatestByUuid(response.data)
    });
    var onSuccessGetLatestByUuid = function(response) {
      $scope.modeldata = response
      $scope.scriptType=response.type
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.model.defaultVersion = defaultversion;
      if($scope.modeldata.type=='SPARK'){
      $scope.selectSourceType = response.source.ref.type
      $scope.paramTable = response.execParams;
      
      if ($scope.modeldata.labelRequired== "Y") {  //$scope.modeldata.label != null ||
        $scope.isLabelDisable = false;
        ModelService.getLatestByUuid(response.source.ref.uuid, "datapod").then(function(response) {
          onSuccessGetLatestByUuidForDatapod(response.data)
        });
        var onSuccessGetLatestByUuidForDatapod = function(response) {
          console.log(JSON.stringify(response))
          var attributesArray = [];
          for (var i = 0; i < response.attributes.length; i++) {
            if (response.attributes[i].type == "integer" || response.attributes[i].type == "double") {
              var attributesJson = {};
              attributesJson.uuid = response.uuid;
              attributesJson.version = response.version;
              attributesJson.name = response.name;
              attributesJson.type = $scope.modeldata.source.ref.type
              attributesJson.dname = response.name + "_" + response.attributes[i].name;
              attributesJson.attrName = response.attributes[i].name;
              attributesJson.attrId = response.attributes[i].attributeId;
              attributesArray.push(attributesJson);
            }
            $scope.allsourceLabel = attributesArray
            var selectLabel = {};
            if($scope.modeldata.label.ref !=null){
              selectLabel.uuid = $scope.modeldata.label.ref.uuid;
              selectLabel.attrId = $scope.modeldata.label.attrId;
              $scope.selectLabel = selectLabel;
            }
          }

        }
      }
      ModelService.getAllLatest($scope.selectSourceType).then(function(response) {
        onGetAllLatestSource(response.data)
      });
      var onGetAllLatestSource = function(response) {
        $scope.allsource = response
        var source = {}
        source.uuid = $scope.modeldata.source.ref.uuid;
        source.name = $scope.modeldata.source.ref.name;
        $scope.selectSource = source;
        ModelService.getAllAttributeBySource($scope.selectSource.uuid, $scope.selectSourceType).then(function(response) {
          onSuccessGetAllAttributeBySource(response.data)
        });
        var onSuccessGetAllAttributeBySource = function(response) {
          $scope.allattribute = response
          var sourceAttributeArray = [];
          for (var i = 0; i < $scope.modeldata.features.length; i++) {
            var attributeinfo = {};
            attributeinfo.uuid = $scope.modeldata.features[i].ref.uuid;
            attributeinfo.dname = $scope.modeldata.features[i].ref.name + "." + $scope.modeldata.features[i].attrName;
            attributeinfo.version = $scope.modeldata.features[i].ref.version;
            attributeinfo.attributeId = $scope.modeldata.features[i].attrId;
            attributeinfo.id = $scope.modeldata.features[i].ref.uuid + "_" + $scope.modeldata.features[i].attrId
            sourceAttributeArray[i] = attributeinfo;
          }
          $scope.sourceAttributeTags = sourceAttributeArray;
        }
      }
      ModelService.getAllLatest("algorithm").then(function(response) {
        onGetAllLatestAlgorithm(response.data)
      });
      var onGetAllLatestAlgorithm = function(response) {
        $scope.allalgorithm = response
        var algorithm = {}
        algorithm.uuid = $scope.modeldata.algorithm.ref.uuid;
        algorithm.name = $scope.modeldata.algorithm.ref.name;
        $scope.selectalgorithm = algorithm

      }
      $scope.checkboxCustom=false
    }
    else{
      $scope.checkboxCustom=true;
      $scope.getModelScript(response.uuid,response.version)
    }

    }
  } //End If
  else {
    $scope.showactive="false"
    ModelService.getAllLatest("algorithm").then(function(response) {
      onGetAllLatestAlgorithm(response.data)
    });
    var onGetAllLatestAlgorithm = function(response) {
      $scope.allalgorithm = response
      var algorithm = {}

    }
  }

  $scope.selectVersion = function(uuid, version) {
    $scope.myform.$dirty = false;
    $scope.allsource = null;
    $scope.selectalgorithm = null;
    $scope.allalgorithm = null;
    ModelService.getOneByUuidandVersion(uuid, version, 'model').then(function(response) {
      onGetByOneUuidandVersion(response.data)
    });
    var onGetByOneUuidandVersion = function(response) {
      $scope.modeldata = response
      $scope.scriptType=response.type
      var defaultversion = {};
      defaultversion.version = response.version;
      defaultversion.uuid = response.uuid;
      $scope.model.defaultVersion = defaultversion;
      $scope.selectSourceType = response.source.ref.type
      $scope.paramTable = response.execParams;
      if ($scope.modeldata.label != null) {
        $scope.isLabelDisable = false;
        ModelService.getLatestByUuid(response.source.ref.uuid, "datapod").then(function(response) {
          onSuccessGetLatestByUuidForDatapod(response.data)
        });
        var onSuccessGetLatestByUuidForDatapod = function(response) {
          console.log(JSON.stringify(response))
          var attributesArray = [];
          for (var i = 0; i < response.attributes.length; i++) {
            if (response.attributes[i].type == "integer" || response.attributes[i].type == "double") {
              var attributesJson = {};
              attributesJson.uuid = response.uuid;
              attributesJson.version = response.version;
              attributesJson.type = $scope.modeldata.source.ref.type
              attributesJson.name = response.name;
              attributesJson.dname = response.name + "_" + response.attributes[i].name;
              attributesJson.attrName = response.attributes[i].name;
              attributesJson.attrId = response.attributes[i].attributeId;
              attributesArray.push(attributesJson);
            }
            $scope.allsourceLable = attributesArray
            var selectLabel = {};
            selectLabel.uuid = $scope.modeldata.label.ref.uuid;
            selectLabel.attrId = $scope.modeldata.label.attrId;
            selectLabel.type = $scope.modeldata.label.type;
            $scope.selectLabel = selectLabel;
          }

        }
      }
      ModelService.getAllLatest($scope.selectSourceType).then(function(response) {
        onGetAllLatestSource(response.data)
      });
      var onGetAllLatestSource = function(response) {
        $scope.allsource = response
        var source = {}
        source.uuid = $scope.modeldata.source.ref.uuid;
        source.name = $scope.modeldata.source.ref.name;
        $scope.selectSource = source
        ModelService.getAllAttributeBySource($scope.selectSource.uuid, "datapod").then(function(response) {
          onSuccessGetAllAttributeBySource(response.data)
        });
        var onSuccessGetAllAttributeBySource = function(response) {
          $scope.allattribute = response
          var sourceAttributeArray = [];
          for (var i = 0; i < $scope.modeldata.attributeInfo.length; i++) {
            var attributeinfo = {};
            attributeinfo.uuid = $scope.modeldata.attributeInfo[i].ref.uuid;
            attributeinfo.dname = $scope.modeldata.attributeInfo[i].ref.name + "." + $scope.modeldata.attributeInfo[i].attrName;
            attributeinfo.version = $scope.modeldata.attributeInfo[i].ref.version;
            attributeinfo.attributeId = $scope.modeldata.attributeInfo[i].attrId;
            attributeinfo.id = $scope.modeldata.attributeInfo[i].ref.uuid + "_" + $scope.modeldata.attributeInfo[i].attrId
            sourceAttributeArray[i] = attributeinfo;
          }
          $scope.sourceAttributeTags = sourceAttributeArray;
        } //End onSuccessGetAllAttributeBySource
      } //End onGetAllLatestSource
      ModelService.getAllLatest("algorithm").then(function(response) {
        onGetAllLatestAlgorithm(response.data)
      });
      var onGetAllLatestAlgorithm = function(response) {
        $scope.allalgorithm = response
        var algorithm = {}
        algorithm.uuid = $scope.modeldata.algorithm.ref.uuid;
        algorithm.name = $scope.modeldata.algorithm.ref.name;
        $scope.selectalgorithm = algorithm

      } //End onGetAllLatestAlgorithm
    } //End onGetByOneUuidandVersion
  } // End selectVersion
  $scope.changeScript= function(){
    $scope.checkboxCustom=$scope.scriptType =="SPARK"?false:true
    //$scope.checkboxCustom=false
  }
  $scope.upload=function(){
    //debugger;
   //var file = $scope.myFile;
  //  var iEl = angular.element(document.querySelector('#script_file'));
  //  var file= iEl[0]
  //  var fd = new FormData();
  //  fd.append('file', $scope.trix);
  // var content = angular.element(document.querySelector('#script_file'));
  // console.log(content.text())
  //var test = angular.element(content[0].defaultValue).text()
  console.log($scope.scriptCode)
  var blob = new Blob([$scope.scriptCode], { type: "text/xml"});
  var fd = new FormData();
   fd.append('file', blob)
  //formData.append("file", blob);
   //$scope.extension=file.name.split(".")[1]
   //console.log($scope.trix)
   ModelService.uploadFile('py',fd,"script").then(function(response){onSuccess(response.data)});
   var onSuccess=function(response){
     console.log(response)
     //$('#fileupload').modal('hide')
     $scope.executionmsg="Data Uploaded Successfully"
     notify.type='success',
     notify.title= 'Success',
     notify.content=$scope.executionmsg//"Dashboard Deleted Successfully"
     $scope.$emit('notify', notify);
 
   }
  }
  // var createStorageKey, host, uploadAttachment;
  
  //     $scope.trixAttachmentAdd = function(e) {
  //         var attachment;
  //         attachment = e.attachment;
  //         if (attachment.file) {
  //             return uploadAttachment(attachment);
  //         }
  //     }
  
  //     host = "https://d13txem1unpe48.cloudfront.net/";
  
  //     uploadAttachment = function(attachment) {
  //         var file, form, key, xhr;
  //         file = attachment.file;
  //         key = createStorageKey(file);
  //         form = new FormData;
  //         form.append("key", key);
  //         form.append("Content-Type", file.type);
  //         form.append("file", file);
  //         xhr = new XMLHttpRequest;
  //         xhr.open("POST", host, true);
  //         xhr.upload.onprogress = function(event) {
  //             var progress;
  //             progress = event.loaded / event.total * 100;
  //             return attachment.setUploadProgress(progress);
  //         };
  //         xhr.onload = function() {
  //             var href, url;
  //             if (xhr.status === 204) {
  //                 url = href = host + key;
  //                 return attachment.setAttributes({
  //                     url: url,
  //                     href: href
  //                 });
  //             }
  //         };
  //         return xhr.send(form);
  //     };
  
  //     createStorageKey = function(file) {
  //         var date, day, time;
  //         date = new Date();
  //         day = date.toISOString().slice(0, 10);
  //         time = date.getTime();
  //         return "tmp/" + day + "/" + time + "-" + file.name;
  //     };
  $scope.downloadFile = function() {
    //var uuid = data.uuid
    var url=$location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url: url+'common/download?fileType=script'+"&fileName="+$scope.modeldata.scriptName,
      responseType: 'arraybuffer'
    }).success(function(data, status, headers) {
      headers = headers();
  
      var filename = headers['x-filename'];
      var contentType = headers['content-type'];
  
      var linkElement = document.createElement('a');
      try {
        var blob = new Blob([data], {
          type: contentType
        });
        var url = window.URL.createObjectURL(blob);
  
        linkElement.setAttribute('href', url);
        linkElement.setAttribute("download", $scope.modeldata.scriptName);
  
        var clickEvent = new MouseEvent("click", {
          "view": window,
          "bubbles": true,
          "cancelable": false
        });
        linkElement.dispatchEvent(clickEvent);
      } catch (ex) {
        console.log(ex);
      }
    }).error(function(data) {
      console.log(data);
    });
  };
  $scope.submitModel = function() {
    $scope.isshowmodel = true;
    $scope.dataLoading = true;
    $scope.iSSubmitEnable = false;
    var modelJson = {}
    modelJson.uuid = $scope.modeldata.uuid
    modelJson.name = $scope.modeldata.name
    modelJson.desc = $scope.modeldata.desc
    modelJson.active = $scope.modeldata.active;
    modelJson.published=$scope.modeldata.published;
    modelJson.trainPercent=$scope.modeldata.trainPercent
    modelJson.valPercent=$scope.modeldata.valPercent
    
    var tagArray = [];
    if ($scope.tags != null) {
      for (var counttag = 0; counttag < $scope.tags.length; counttag++) {
        tagArray[counttag] = $scope.tags[counttag].text;
      }
    }
    modelJson.tags = tagArray;
    if(!$scope.checkboxCustom){
    modelJson.type="SPARK"
    var source = {};
    var ref = {};
    ref.type = $scope.selectSourceType;
    ref.uuid = $scope.selectSource.uuid;
    source.ref = ref;
    modelJson.source = source;

    var algorithm = {};
    var ref = {};
    ref.type = "algorithm";
    ref.uuid = $scope.selectalgorithm.uuid;
    algorithm.ref = ref;
    modelJson.algorithm = algorithm;

    var attributeInfoArray = [];
    for (var i = 0; i < $scope.sourceAttributeTags.length; i++) {
      var attributeInfo = {}
      var ref = {};
      ref.type = $scope.selectSourceType;;
      ref.uuid = $scope.sourceAttributeTags[i].uuid;
      // ref.version=$scope.sourceAttributeTags[i].version;
      attributeInfo.ref = ref;
      attributeInfo.attrId = $scope.sourceAttributeTags[i].attributeId
      attributeInfoArray[i] = attributeInfo;
    }
    modelJson.features = attributeInfoArray;

    if ($scope.isLabelDisable == false) {
      var label = {};
      var ref = {};
      ref.type = $scope.selectLabel.type
      ref.uuid = $scope.selectLabel.uuid
      label.ref = ref;
      label.attrId = $scope.selectLabel.attrId
      modelJson.label = label;
      modelJson.labelRequired="Y"
    } else {
      modelJson.label = null;
      modelJson.labelRequired="N"
    }
     ModelService.submit(modelJson, 'model').then(function(response) {
    onSuccess(response.data)
  },function(response){onError(response.data)});
  }
  else{
    // var content = angular.element(document.querySelector('#script_file'));
    // var test = angular.element(content[0].defaultValue).text()
    // var blob = new Blob([test], { type: "text/xml"});
    // var fd = new FormData();
    //  fd.append('file', blob)
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
//       var element=[]
//       element.push(result[0])
//       element.push(result[1])
//       element.push(result[2])
//       element.push(result[3])
//       element.push(result[4])
//       modelJson.uuid=element.join('-')
  ModelService.submit(modelJson, 'model').then(function(response) {
    onSuccess(response.data)
  },function(response){onError(response.data)});
      
     }

  }
  console.log(JSON.stringify(modelJson));
  // ModelService.submit(modelJson, 'model').then(function(response) {
  //   onSuccess(response.data)
  // },function(response){onError(response.data)});
    var onSuccess = function(response) {
      $scope.dataLoading = false;
      $scope.iSSubmitEnable = false;
      $scope.changemodelvalue();
      if ($scope.checkboxModelexecution == "YES") {
        ModelService.getOneById(response, "model").then(function(response) {
          onSuccessGetOneById(response.data)
        });
        var onSuccessGetOneById = function(result) {
          $scope.modelExecute(result);
        }
      } //End if
      else {
        // if ($scope.isshowmodel == "true") {
        //   $scope.modelmessage = "Model Saved Successfully"
        //   $('#modelsave').modal({
        //     backdrop: 'static',
        //     keyboard: false
        //   });
        // }
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

DatascienceModule.controller('ResultModelController', function($filter, $location,
		$http,$stateParams,dagMetaDataService, $rootScope, $scope, ModelService) {


  $scope.selectedmodeldata = false;
  $scope.selectedmodelExecdata = false
  $scope.model = true;
  $scope.modeldata;
  $stateParams;
  $scope.isMoldeSelect = true;
  $scope.autorefreshcounter=05;
  $scope.autoRefreshCounterResult=05
  $scope.path=dagMetaDataService.statusDefs
  if($stateParams.mode =='true'){
	  $scope.isEdit=false;
	  $scope.isversionEnable=false;
	  }
	  else{
	  $scope.isEdit=true;
	  $scope.isversionEnable=true;
	  }
  $scope.pagination={
    currentPage:1,
    pageSize:10,
    paginationPageSizes:[10, 25, 50, 75, 100],
    maxSize:5,
  }

  $scope.getGridStyle = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
    } else {
      style['height'] = "100px"
    }
    return style;
  }
  $scope.gridOptions = dagMetaDataService.gridOptionsResults;
  $scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.refreshData = function() {
    var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
    $scope.getResults(data);
  };

  $scope.selectModel = function(response) {
    $scope.selectedmodeldata = true;
    $scope.gridOptions.data=null;
    $scope.gridOptions.data = response.data;
    $scope.originalData = response.data;
  } //End selectModel

  $scope.getExec = function(data) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.modelDetail={};
    $scope.modelDetail.uuid=uuid;
    $scope.modelDetail.version=version
    $scope.getAlgorithumByModelExec();
    ModelService.getModelResult(uuid, version).then(function(response){ onSuccessGetModelResult(response.data)});
    var onSuccessGetModelResult = function(response) {
      $scope.modelresult = response;
      $scope.model = false;
      $scope.isMoldeSelect = false;
      $scope.selectedmodelExecdata = true;
    } //End onSuccessGetModelResult
  }
  
  $scope.refreshMoldeResult=function(){
    $scope.getExec($scope.modelDetail)
  }
  
  $scope.getAlgorithumByModelExec=function(){
	  
	  ModelService.getAlgorithmByModelExec($scope.modelDetail.uuid, $scope.modelDetail.version,'modelExec').then(function(response){ onSuccessGetModelResult(response.data)});
	    var onSuccessGetModelResult = function(response) {
	     $scope.isPMMLDownload=response.savePmml =="Y" ? false:true; 
	    } 
  }
  
					  
  $scope.downloadMoldeResult = function() {
		var url = $location.absUrl().split("app")[0]

	$http({method : 'GET',
			url : url + "model/download?modelExecUUID="
					+ $scope.modelDetail.uuid
					+ "&modelExecVersion="
					+ $scope.modelDetail.version,
			responseType : 'arraybuffer'
			}).success(
			function(data, status, headers) {
				headers = headers();
				var filename = headers['x-filename'];
				var contentType = headers['content-type'];

				var linkElement = document
						.createElement('a');
				try {
					var blob = new Blob([ data ], {
						type : contentType
					});
					var url = window.URL
							.createObjectURL(blob);

					linkElement.setAttribute('href', url);
					linkElement.setAttribute("download",
							$scope.modelDetail.uuid+ ".pmml");

					var clickEvent = new MouseEvent(
							"click", {
								"view" : window,
								"bubbles" : true,
								"cancelable" : false
							});
					linkElement.dispatchEvent(clickEvent);
				} catch (ex) {
					console.log(ex);
				}
			}).error(function(data) {
		console.log();
	});
  };
  
  
  
  var myVarResult;
  $scope.autoRefreshResultOnChange=function () {
    if($scope.autoRefreshResult){
      myVarResult = setInterval(function(){
      $scope.getExec($scope.modelDetail)
      }, $scope.autoRefreshCounterResult+"000");
    }
    else{
     clearInterval(myVarResult);
    }
  }
  $scope.showModel = function() {
    $scope.model = true;
    $scope.selectedmodelExecdata = false;
    $scope.isMoldeSelect = true;
    $scope.selectedmodeldata = true;
  }

  // $scope.selectPage = function(pageNo) {
  //   $scope.pagination.currentPage = pageNo;
  // };
  // $scope.onPerPageChange = function() {
  //     $scope.pagination.currentPage = 1;
  //   $scope.getResults($scope.originalData)
  // }
  // $scope.pageChanged = function() {
  //   $scope.getResults($scope.originalData)
  // };
  // $scope.getResults = function(params) {
  //   $scope.pagination.totalItems=params.length;
  //   $scope.pagination.to = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize);
  //   if ($scope.pagination.totalItems < ($scope.pagination.pageSize*$scope.pagination.currentPage)) {
  //     $scope.pagination.from = $scope.pagination.totalItems;
  //   } else {
  //     $scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
  //   }
  //   var limit = ($scope.pagination.pageSize*$scope.pagination.currentPage);
  //   var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
  //    $scope.gridOptions.data=params.slice(offset,limit);
  // }
  var myVar;
  $scope.autoRefreshOnChange=function () {
    if($scope.autorefresh){
     myVar = setInterval(function(){
        $rootScope.refreshRowData();

        $scope.refreshRowData();
      }, $scope.autorefreshcounter+"000");
    }
    else{
     clearInterval(myVar);
    }
  }

  $scope.$on('$destroy', function() {
    // Make sure that the interval is destroyed too
    clearInterval(myVar);
  });

  $scope.refreshRowData=function () {
    if($scope.data.length >0){
      $scope.gridOptions.data=$scope.data;
      $scope.$watch('data',function(newValue, oldValue) {
        if(newValue != oldValue  && $scope.gridOptions.data.length > 0) {
          for(var i=0;i<$scope.data.length;i++) {
            if($scope.data[i].status != $scope.gridOptions.data[i].status){
              $scope.gridOptions.data[i].status=$scope.data[i].status;
            }
          }
        }
        else{
          $scope.gridOptions.data =[];
          $scope.gridOptions.data=$scope.data;
        }
      },true);
    }
  }
 

});
