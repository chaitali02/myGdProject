/**
 **/
  DatascienceModule= angular.module('DatascienceModule');
DatascienceModule.controller('CreateParamSetController', function($state,$stateParams,$rootScope,$scope,$sessionStorage,ParamSetService,privilegeSvc) {

	$scope.mode=" ";
	$scope.dataLoading=false;
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
	$scope.isSubmitEnable=true;
	$scope.paramsetdata;
	$scope.showparamset=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.paramset={};
	$scope.paramset.versions=[];
	$scope.isshowmodel=false;
	$scope.paramtable=null;
	$scope.isTabelShow=false;
  $scope.isDependencyShow=false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['paramset'] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['paramset'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });
  	$scope.showParamSetPage=function(){
  		$scope.showparamset=true;
  		$scope.showgraph=false
  		$scope.graphDataStatus=false;
  		$scope.showgraphdiv=false
  	}
    $scope.enableEdit=function (uuid,version) {
      $scope.showParamSetPage()
      $state.go('createparamset', {
        id: uuid,
        version: version,
        mode:'false'
      });
    }
    $scope.showview=function (uuid,version) {
      $scope.showParamSetPage()
      $state.go('createparamset', {
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
		 paramjson.paramSetId=$scope.paramtable.length;
		 var paramSetVal=[];
		 for(var i=0;i<$scope.paramtablecol.length;i++){
		 	var paramSetValjson={};
		 	paramSetValjson.paramId=$scope.paramtablecol[i].paramId;
            paramSetValjson.paramName=$scope.paramtablecol[i].paramName;
         	paramSetValjson.value=""
		 	paramSetVal[i]=paramSetValjson;

		 }
		 paramjson.paramSetVal=paramSetVal
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


    $scope.showParamSetGraph=function(uuid,version){
		$scope.showparamset=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;

	}//End showFunctionGraph



    $scope.onChangeParamSet=function(){
    	ParamSetService.getLatestByUuid($scope.selectparamlist.uuid,"paramlist").then(function(response){onSuccessGetLatestByUuid(response.data)});
        var onSuccessGetLatestByUuid=function(response){
        	var paramarray=[];
        	for(var i=0;i<response.params.length;i++){
         		var paramjson={};
         		paramjson.paramId=response.params[i].paramId;
         		paramjson.paramName=response.params[i].paramName;
         		paramjson.value=response.params[i].paramValue;
         		paramarray.push(paramjson)
        	}
        	$scope.paramtablecol=paramarray;
        	$scope.paramtable=[];
        	console.log(JSON.stringify($scope.paramtablecol))
        	$scope.isTabelShow=true;
        }
    }

    $scope.getAllVersion=function(uuid){
		ParamSetService.getAllVersionByUuid(uuid,"paramset").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
				var paramsetversion={};
				paramsetversion.version=response[i].version;
				$scope.paramset.versions[i]=paramsetversion;
			 }
		}//End getAllVersionByUuid
	}//End GetAllVersion




	if(typeof $stateParams.id != "undefined"){
        $scope.mode=$stateParams.mode;

        $scope.isDependencyShow=true;
        $scope.getAllVersion($stateParams.id)
        ParamSetService.getOneByUuidandVersion($stateParams.id,$stateParams.version,"paramset").then(function(response){onSuccessGetLatestByUuid(response.data)});
        var onSuccessGetLatestByUuid=function(response){
        	console.log(JSON.stringify(response.paramSetValarray))
    	 	$scope.paramsetdata=response.paramsetdata
    	 	var defaultversion={};
    	 	defaultversion.version=response.paramsetdata.version;
 	  	 	defaultversion.uuid=response.paramsetdata.uuid;
 	     	$scope.paramset.defaultVersion=defaultversion;
 	     	$scope.paramtablecol=response.paramInfoArray[0].paramSetVal;
 	     	$scope.paramtable=response.paramInfoArray;
 	     	$scope.isTabelShow=true;
 	     	$scope.tags=response.paramsetdata.tags
 	     	ParamSetService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
            var onSuccessGetAllLatestParamlist=function(response){

                $scope.allparamlist=response;

                var paramlis={};
                paramlis.uuid=$scope.paramsetdata.dependsOn.ref.uuid;
                paramlis.name="";
                $scope.selectparamlist=paramlis;
            }//End onSuccessGetAllLatestParamlist


        }
    }//End If
    else{
    	ParamSetService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
        var onSuccessGetAllLatestParamlist=function(response){
            $scope.allparamlist=response;
            $scope.selectparamlist=$scope.allparamlist[0];
        }
    }

    $scope.selectVersion=function(uuid,version){
        $scope.myform.$dirty=false;
		ParamSetService.getOneByUuidandVersion(uuid,version,'paramset').then(function(response){onGetByOneUuidandVersion(response.data)});
	    var onGetByOneUuidandVersion =function(response){
           console.log(JSON.stringify(response.paramSetValarray))
    	 	$scope.paramsetdata=response.paramsetdata
    	 	var defaultversion={};
    	 	defaultversion.version=response.paramsetdata.version;
 	  	 	defaultversion.uuid=response.paramsetdata.uuid;
 	     	$scope.paramset.defaultVersion=defaultversion;
 	     	$scope.paramtablecol=response.paramInfoArray[0].paramSetVal;
 	     	$scope.paramtable=response.paramInfoArray;
 	     	$scope.isTabelShow=true;
 	     	$scope.tags=response.paramsetdata.tags
 	     	ParamSetService.getAllLatest("paramlist").then(function(response){onSuccessGetAllLatestParamlist(response.data)});
            var onSuccessGetAllLatestParamlist=function(response){
                $scope.allparamlist=response;
                var paramlis={};
                paramlis.uuid=$scope.paramsetdata.dependsOn.ref.uuid;
                paramlis.name="";
                $scope.selectparamlist=paramlis;
            }//End onSuccessGetAllLatestParamlist
	    }

    }

    $scope.submitParamSet=function(){
		$scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.myform.$dirty=false;
		var paramsetJson={}
		paramsetJson.uuid=$scope.paramsetdata.uuid
		paramsetJson.name=$scope.paramsetdata.name
		paramsetJson.desc=$scope.paramsetdata.desc
		paramsetJson.active=$scope.paramsetdata.active;
    paramsetJson.published=$scope.paramsetdata.published;
		var tagArray=[];
	    if($scope.tags !=null){
	     for(var counttag=0;counttag<$scope.tags.length;counttag++){
	     	tagArray[counttag]=$scope.tags[counttag].text;
	     }
	   }
 	   paramsetJson.tags=tagArray;
       var dependsOn={};
       var  ref={};
       ref.type="paramlist";
        ref.uuid=$scope.selectparamlist.uuid;
       dependsOn.ref=ref;
       paramsetJson.dependsOn=dependsOn
       var paramInfoArray=[];
 	   if($scope.paramtable.length >0){
 			for(var i=0;i<$scope.paramtable.length;i++){
 				var paraminfo={};
 				paraminfo.paramSetId=$scope.paramtable[i].paramSetId;
 				var paramSetValarray=[];
 				for(var j=0;j<$scope.paramtable[i].paramSetVal.length;j++){
 					var paramsetvaljson={};
 					paramsetvaljson.ref=ref;
 					paramsetvaljson.paramId=$scope.paramtable[i].paramSetVal[j].paramId;
 					paramsetvaljson.value=$scope.paramtable[i].paramSetVal[j].value;
 				    paramSetValarray[j]=paramsetvaljson;

 				}
 				paraminfo.paramSetVal=paramSetValarray;
 		    	paramInfoArray[i]=paraminfo;
 		    }
 	   }
 	   paramsetJson.paramInfo=paramInfoArray;
 	   console.log(JSON.stringify(paramsetJson));
 	   ParamSetService.submit(paramsetJson,'paramset').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
 	   var onSuccess=function(response){
	      $scope.dataLoading=false;
	      $scope.iSSubmitEnable=false;
 	      $scope.changemodelvalue();
 	   //   if($scope.isshowmodel == "true"){
   	 //        $('#paramsetsave').modal({
 		 //      backdrop: 'static',
 		 //      keyboard: false
 	   //    });
   	 //   }
     notify.type='success',
     notify.title= 'Success',
    notify.content='Paramenter Set Saved Successfully'
    $scope.$emit('notify', notify);
    $scope.okparamsetsave();
      }
      var onError = function(response) {
         notify.type='error',
         notify.title= 'Error',
        notify.content="Some Error Occurred"
        $scope.$emit('notify', notify);
      }
    }

    $scope.okparamsetsave=function(){

	   $('#paramsetsave').css("dispaly","none");
	   var hidemode="yes";
	   if(hidemode == 'yes'){
		  setTimeout(function(){$state.go("paramset");},2000);
		}
	}

    $scope.changemodelvalue=function(){
	  $scope.isshowmodel=sessionStorage.isshowmodel
    };
});
