/**
 **/
  DatascienceModule= angular.module('DatascienceModule');
  DatascienceModule.controller('CreateAlgorithmController', function(CommonService,$state,$stateParams,$rootScope,$scope,$sessionStorage,AlgorithmService,privilegeSvc) {
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
	$scope.mode=" ";
	$scope.dataLoading=false;
	$scope.isSubmitEnable=true;
	$scope.algorithmdata;
	$scope.showalgorithm=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.algorithm={};
	$scope.algorithm.versions=[];
	$scope.isshowmodel=false;
	$scope.librarytypes=["sparkML","R","Java"];
	$scope.paramtable=null;
	$scope.types =["clustering","classification","regression"];
  $scope.isDependencyShow=false;
  $scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['algorithm'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['algorithm'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  $scope.showAlgorithmPage=function(){
		$scope.showalgorithm=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}
  $scope.enableEdit=function (uuid,version) {
    $scope.showAlgorithmPage()
    $state.go('createalgorithm', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }
  $scope.showview=function (uuid,version) {
    $scope.showAlgorithmPage()
    $state.go('createalgorithm', {
      id: uuid,
      version: version,
      mode:'true'
    });
  }
  var notify = {
    type: 'success',
    title: 'Success',
    content: 'Dashboard deleted Successfully',
    timeout: 3000 //time in ms
};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
      	console.log(fromParams)
		$sessionStorage.fromStateName=fromState.name
	    $sessionStorage.fromParams=fromParams

    });

    $scope.orderByValue = function (value) {
        return value;
    };
    $scope.$watch("isshowmodel",function(newvalue,oldvalue){
   		$scope.isshowmodel=newvalue
        sessionStorage.isshowmodel=newvalue
    });

    $scope.addRow=function(){
		 if($scope.paramtable == null){
			 $scope.paramtable =[];
		 }
		 var paramjson={}
		 paramjson.paramId=$scope.paramtable.length;
		 $scope.paramtable.splice($scope.paramtable.length, 0,paramjson);
	 }

	 $scope.selectAllRow=function(){

		 angular.forEach($scope.paramtable, function(stage) {
			 stage.selected = $scope.selectallattribute;
	        });
	 }
	 $scope.removeRow=function(){
		 var newDataList=[];
		 	$scope.selectallattribute=false;
		 	angular.forEach( $scope.paramtable, function(selected){
		          if(!selected.selected){
		              newDataList.push(selected);
		          }
		      });
		 	 $scope.paramtable = newDataList;
	 }


    $scope.showAlgorithmGraph=function(uuid,version){
		$scope.showalgorithm=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
    }//End showFunctionGraph





    $scope.getAllVersion=function(uuid){
		AlgorithmService.getAllVersionByUuid(uuid,"algorithm").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
				var algorithmversion={};
				algorithmversion.version=response[i].version;
				$scope.algorithm.versions[i]=algorithmversion;
			 }
		}//End getAllVersionByUuid
	}//End GetAllVersion




	if(typeof $stateParams.id != "undefined"){
        $scope.mode=$stateParams.mode
        $scope.isDependencyShow=true;
        $scope.getAllVersion($stateParams.id)
        CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"algorithm").then(function(response){onSuccessGetLatestByUuid(response.data)});
        var onSuccessGetLatestByUuid=function(response){
    	 	$scope.algorithmdata=response
    	 	var defaultversion={};
    	 	defaultversion.version=response.version;
 	  	 	defaultversion.uuid=response.uuid;
 	     	$scope.algorithm.defaultVersion=defaultversion;
 	     	$scope.selecttype=response.type
 	     	$scope.selectlibrary=response.library
 	     	AlgorithmService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
            var onSuccessGetAllLatestParamlist=function(response){
                $scope.allparamlist=response;
                var paramlist={};
                paramlist.uuid=$scope.algorithmdata.paramList.ref.uuid;
                paramlist.name=""
                $scope.selectparamlist=paramlist;

            }
        }
    }//End If
    else{
    	AlgorithmService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
            var onSuccessGetAllLatestParamlist=function(response){
                $scope.allparamlist=response;
                  $scope.selectparamlist=$scope.allparamlist[0];
            }
    }


    $scope.selectVersion=function(uuid,version){
        $scope.myform.$dirty=false;
        $scope.allparamlist=null;
        $scope.selectparamlist=null;
        $scope.selecttype=null;
 	    $scope.selectlibrary=null;
		AlgorithmService.getOneByUuidandVersion(uuid,version,'algorithm').then(function(response){onGetByOneUuidandVersion(response.data)});
	    var onGetByOneUuidandVersion =function(response){
            $scope.algorithmdata=response
    	 	var defaultversion={};
    	 	defaultversion.version=response.version;
 	  	 	defaultversion.uuid=response.uuid;
 	     	$scope.algorithm.defaultVersion=defaultversion;
 	     	$scope.selecttype=response.type
 	     	$scope.selectlibrary=response.library
 	     	AlgorithmService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
            var onSuccessGetAllLatestParamlist=function(response){
                $scope.allparamlist=response;
                var paramlist={};
                paramlist.uuid=$scope.algorithmdata.paramList.ref.uuid;
                paramlist.name=""
                $scope.selectparamlist=paramlist;

            }
	    }

    }

    $scope.submitAlgorithm=function(){
		$scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.myform.$dirty=false;
		var algorithmJson={}
		algorithmJson.uuid=$scope.algorithmdata.uuid
		algorithmJson.name=$scope.algorithmdata.name
		algorithmJson.desc=$scope.algorithmdata.desc
		algorithmJson.active=$scope.algorithmdata.active;
    algorithmJson.published=$scope.algorithmdata.published;
		algorithmJson.type=$scope.selecttype;
		algorithmJson.library=$scope.selectlibrary;
		algorithmJson.trainName=$scope.algorithmdata.trainName;
		algorithmJson.modelName=$scope.algorithmdata.modelName;
		algorithmJson.labelRequired=$scope.algorithmdata.labelRequired;
		var tagArray=[];
	    if($scope.tags !=null){
	     for(var counttag=0;counttag<$scope.tags.length;counttag++){
	     	tagArray[counttag]=$scope.tags[counttag].text;
	     }
	   }
 	   algorithmJson.tags=tagArray;
       var paramlist={};
       var ref={};
       ref.type="paramlist";
       ref.uuid=$scope.selectparamlist.uuid;
       paramlist.ref=ref;
 	   algorithmJson.paramList=paramlist
 	   console.log(JSON.stringify(algorithmJson));
 	   AlgorithmService.submit(algorithmJson,'algorithm').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
 	   var onSuccess=function(response){
	      $scope.dataLoading=false;
	      $scope.iSSubmitEnable=false;
 	      $scope.changemodelvalue();
 	   //   if($scope.isshowmodel == "true"){
   	 //        $('#algorithmsave').modal({
 		 //      backdrop: 'static',
 		 //      keyboard: false
 	   //    });
   	 //   }
     notify.type='success',
     notify.title= 'Success',
    notify.content='Algorithm Saved Successfully'
    $scope.$emit('notify', notify);
    $scope.okalgorithmsave();
      }
      var onError = function(response) {
         notify.type='error',
         notify.title= 'Error',
        notify.content="Some Error Occurred"
        $scope.$emit('notify', notify);
      }
    }

    $scope.okalgorithmsave=function(){
	   //$('#algorithmsave').css("dispaly","none");
	   var hidemode="yes";
	   if(hidemode == 'yes'){
		  setTimeout(function(){  $state.go("algorithm");},2000);
		}
	}

    $scope.changemodelvalue=function(){
	  $scope.isshowmodel=sessionStorage.isshowmodel
    };
});
