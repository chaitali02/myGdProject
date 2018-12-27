DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller('VartifAnalysisController', function (CommonService, $state, $stateParams, $rootScope, $scope, $sessionStorage, VartifAnalysisService, privilegeSvc,$filter,$timeout,dagMetaDataService,uiGridConstants) {
    
    // $scope.minRangeSlider = {
    //     floor: 0.0,
    //     ceil: 100,
    //     precision: 2,
    //     step: 0.01,
    //     showSelectionBar: true,
    //     }
    $scope.maxRangeSlider = {
        value: 3,
        options: {
            floor: 0,
            ceil: 6,
            step: 0.01,
            precision:2
           
        }
    };
     $scope.zoomSize=100;
  
    var notify = {
        type: 'success',
        title: 'Success',
        content: '',
        timeout: 3000 //time in ms
    };
    
   
    $scope.$on('$destroy', function () {
        // Make sure that the interval is destroyed too
    });
    
    $scope.reset=function(){
        $scope.featureMapTableArray=[];
        $scope.searchForm=null;
        $scope.allTrainExecInfo=null;
        $scope.allModel=null;
        $scope.selectedModel=null;
        setTimeout(function(){
            $scope.searchForm={};
            $scope.allTrainExecInfo=null;
            $scope.allModel=null;
            $scope.getAllLetestModel();
        },10);
    }

    $scope.getAllLetestModel = function () {
        VartifAnalysisService.getAllLatest("model").then(function (response) { onGetAllLatest(response.data) });
        var onGetAllLatest = function (response) {
          $scope.allModel = response;
          $scope.iSSubmitEnable=false;
        }
    };

    $scope.getAllLetestModel();
    

    $scope.getModelOneByUuidAndVersion=function(){
        CommonService.getOneByUuidAndVersion($scope.selectedModel.uuid, $scope.selectedModel.version, "model").then(function (response) { onSuccessGetLatestByUuid(response.data) });
        var onSuccessGetLatestByUuid = function (response) {
          $scope.modelData = response;
            var featureMapTableArray = [];
            if(response.features && response.features.length){
                for (var i = 0; i < response.features.length; i++) {
                    var featureMap = {};
                    var sourceFeature = {};
                    featureMap.featureMapId = i;
                    featureMap.id = i;
                    featureMap.index = i;
                    sourceFeature.uuid = response.uuid;
                    sourceFeature.type = "model";
                    sourceFeature.featureId = response.features[i].featureId;
                    sourceFeature.featureName = response.features[i].name;
                    featureMap.sourceFeature = sourceFeature;
                    
                    featureMap.minRangeSlider = {}
                    if(response.features[i].minVal >0){
                        featureMap.minRangeSlider.floor=response.features[i].minVal;
                    }else{
                        featureMap.minRangeSlider.floor=0.0;
                    }
                    if(response.features[i].maxVal >0){
                        featureMap.minRangeSlider.ceil=response.features[i].maxVal;
                    }else{
                        featureMap.minRangeSlider.ceil=1;
                        
                    }
                    featureMap.minRangeSlider.precision=2;
                    featureMap.minRangeSlider.step=0.01;
                    
                    featureMap.minRangeSlider.showSelectionBar=true;
                    var temValue=(featureMap.minRangeSlider.floor+ featureMap.minRangeSlider.ceil)/2;
                    featureMap.selectValue=temValue;
                    featureMapTableArray[i] = featureMap;
                    $scope.featureMapTableArray = featureMapTableArray;
                }
            } 
          
        }
    }

    $scope.getTrainExecViewByCriteria=function(uuid,version){
        VartifAnalysisService.getTrainExecViewByCriteria($scope.selectedModel.uuid, $scope.selectedModel.version, "model","",'' ,"",'', "").then(function (response) { onGetTrainExecByModel(response.data) });
        var onGetTrainExecByModel = function (response) {
            console.log(response);
            var trainExecInfo=[];
            $scope.iSSubmitEnable=false;
            if(response && response.length >0){
                for(var i=0;i<response.length; i++){
                 var execInfo={};
                 execInfo.uuid=response[i].uuid;
                 execInfo.version=response[i].version;
                 execInfo.name=response[i].name;
                 execInfo.displayname="accuracy | "+response[i].accuracy+" | "+response[i].createdOn;
                 trainExecInfo[i]=execInfo;                                 
                }
            }

          $scope.allTrainExecInfo = trainExecInfo;
        }
    }; 
    
    $scope.getTrainExecByModel=function(uuid,version){
        VartifAnalysisService.getTrainExecByModel(uuid, version, "model").then(function (response) { onGetTrainExecByModel(response.data) });
        var onGetTrainExecByModel = function (response) {
            var trainExecInfo=[];
            $scope.iSSubmitEnable=false;
            if(response && response.length >0){
                for(var i=0;i<response.length; i++){
                 var execInfo={};
                 execInfo.uuid=response[i].uuid;
                 execInfo.version=response[i].version;
                 execInfo.name=response[i].name;
                 execInfo.displayname=response[i].name+" [ "+response[i].createdOn+" ]";
                 trainExecInfo[i]=execInfo;                                 
                }
            }

          $scope.allTrainExecInfo = trainExecInfo;
        }
    }
    $scope.onChangeModel=function(){
        if($scope.selectedModel){
            $scope.isInprogess=false;
            $scope.getTrainExecViewByCriteria($scope.selectedModel.uuid,$scope.selectedModel.version);
            //$scope.getTrainExecByModel($scope.selectedModel.uuid,$scope.selectedModel.version);
            $scope.getModelOneByUuidAndVersion();
        }else{
            $scope.iSSubmitEnable=true;
            $scope.featureMapTableArray=[];
            $scope.searchForm=null;
            $scope.allTrainExecInfo=null;
            setTimeout(function(){
                $scope.searchForm={};
                $scope.allTrainExecInfo=null;
            },10);
            
        }
    };


    
   
   $scope.getPrediction=function(){
    $scope.predictionData=null;
    $scope.iSSubmitEnable=true;
    $scope.isInprogess=true;
    var featureList=[];
    var predictionJson={};
    var featureInfo={};
    for(var i=0;i<$scope.featureMapTableArray.length;i++){
        
        featureInfo.name
        featureInfo[$scope.featureMapTableArray[i].sourceFeature.featureName]=$scope.featureMapTableArray[i].selectValue;
      
    }
    featureList[0]=featureInfo;
    predictionJson.featureList=featureList;
    console.log(JSON.stringify(predictionJson));
    VartifAnalysisService.getPrediction($scope.searchForm.trainexec.uuid, $scope.searchForm.trainexec.version, "trainexec",predictionJson)
        .then(function (response) { onGetPrediction(response.data) },function(response){onError(respone)});
    var onGetPrediction = function (response) {
        console.log(response);
        $scope.predictionData=response;
        $scope.iSSubmitEnable=false;
        $scope.isInprogess=false;
    }
    var onError=function(response){
        $scope.iSSubmitEnable=false;
        $scope.isInprogess=false;
    }

   }
})
