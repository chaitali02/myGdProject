/**
 **/
  DatascienceModule= angular.module('DatascienceModule');
  DatascienceModule.directive('lowercase', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {

            function fromUser(text) {
                return (text || '').toUpperCase();
            }

            function toUser(text) {
                return (text || '').toLowerCase();
            }
            ngModel.$parsers.push(fromUser);
            ngModel.$formatters.push(toUser);
        }
    };
});
  DatascienceModule.controller('CreateParamListController', function(CommonService,$state,$stateParams,$rootScope,$scope,$sessionStorage,ParamListService,privilegeSvc) {

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
	$scope.paramlistdata;
	$scope.showparamlist=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.paramlist={};
	$scope.paramlist.versions=[];
	$scope.isshowmodel=false;
	$scope.paramtable=null;
	$scope.type = ["string","double","date","integer","row"];
  $scope.isDependencyShow=false;
  $scope.privileges = [];
  $scope.privileges = privilegeSvc.privileges['paramlist'] || [];
  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  $scope.$on('privilegesUpdated',function (e,data) {
    $scope.privileges = privilegeSvc.privileges['paramlist'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
  });

  	$scope.showParamListPage=function(){
  		$scope.showparamlist=true;
  		$scope.showgraph=false
  		$scope.graphDataStatus=false;
  		$scope.showgraphdiv=false
  	}
  $scope.enableEdit=function (uuid,version) {
    $scope.showParamListPage()
    $state.go('createparamlist', {
      id: uuid,
      version: version,
      mode:'false'
    });
  }
  $scope.showview=function (uuid,version) {
    $scope.showParamListPage()
    $state.go('createparamlist', {
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


    $scope.showParamListGraph=function(uuid,version){
		$scope.showparamlist=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;

	}//End showFunctionGraph




    $scope.getAllVersion=function(uuid){
		ParamListService.getAllVersionByUuid(uuid,"paramlist").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
				var paramlistversion={};
				paramlistversion.version=response[i].version;
				$scope.paramlist.versions[i]=paramlistversion;
			 }
		}//End getAllVersionByUuid
	}//End GetAllVersion




	if(typeof $stateParams.id != "undefined"){
        $scope.mode=$stateParams.mode;

        $scope.isDependencyShow=true;
        $scope.getAllVersion($stateParams.id)
        CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"paramlist").then(function(response){onSuccessGetLatestByUuid(response.data)});
        var onSuccessGetLatestByUuid=function(response){
    	 	$scope.paramlistdata=response
    	 	var defaultversion={};
    	 	defaultversion.version=response.version;
 	  	 	defaultversion.uuid=response.uuid;
 	     	$scope.paramlist.defaultVersion=defaultversion;
 	     	$scope.paramtable=response.params;
        }
    }//End If


    $scope.selectVersion=function(uuid,version){
        $scope.myform.$dirty=false;
		ParamListService.getOneByUuidandVersion(uuid,version,'paramlist').then(function(response){onGetByOneUuidandVersion(response.data)});
	    var onGetByOneUuidandVersion =function(response){
            $scope.paramlistdata=response
    	 	var defaultversion={};
    	 	defaultversion.version=response.version;
 	  	 	defaultversion.uuid=response.uuid;
 	     	$scope.paramlist.defaultVersion=defaultversion;
 	     	$scope.paramtable=response.params;
	    }

    }

    $scope.submitParamList=function(){
		$scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.myform.$dirty=false;
		var paramlistJson={}
		paramlistJson.uuid=$scope.paramlistdata.uuid
		paramlistJson.name=$scope.paramlistdata.name
		paramlistJson.desc=$scope.paramlistdata.desc
		paramlistJson.active=$scope.paramlistdata.active;
    paramlistJson.published=$scope.paramlistdata.published;
		var tagArray=[];
	    if($scope.tags !=null){
	     for(var counttag=0;counttag<$scope.tags.length;counttag++){
	     	tagArray[counttag]=$scope.tags[counttag].text;
	     }
	   }
 	   paramlistJson.tags=tagArray;

       var paramInfoArray=[];
 	   if($scope.paramtable.length >0){
 			for(var i=0;i<$scope.paramtable.length;i++){
 				var paraminfo={};
 				paraminfo.paramId=$scope.paramtable[i].paramId;
 		    	paraminfo.paramName=$scope.paramtable[i].paramName;
 		    	paraminfo.paramType=$scope.paramtable[i].paramType;
 		    	paraminfo.paramValue=$scope.paramtable[i].paramValue;
 		    	paramInfoArray[i]=paraminfo;
 		    }
 	   }
 	   paramlistJson.params=paramInfoArray;
 	   console.log(JSON.stringify(paramlistJson));
 	   ParamListService.submit(paramlistJson,'paramlist').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
 	   var onSuccess=function(response){
	      $scope.dataLoading=false;
	      $scope.iSSubmitEnable=false;
 	      $scope.changemodelvalue();
 	   //   if($scope.isshowmodel == "true"){
   	 //        $('#paramlistsave').modal({
 		 //      backdrop: 'static',
 		 //      keyboard: false
 	   //    });
   	 //   }
     notify.type='success',
     notify.title= 'Success',
    notify.content='Parameter List Saved Successfully'
    $scope.$emit('notify', notify);
    $scope.okparamlsitsave();

      }
      var onError = function(response) {
         notify.type='error',
         notify.title= 'Error',
        notify.content="Some Error Occurred"
        $scope.$emit('notify', notify);
      }
    }

    $scope.okparamlsitsave=function(){

	   $('#paramlistsave').css("dispaly","none");
	   var hidemode="yes";
	   if(hidemode == 'yes'){
		  setTimeout(function(){$state.go("paramlist");},2000);
		}
	}

    $scope.changemodelvalue=function(){
	  $scope.isshowmodel=sessionStorage.isshowmodel
    };
});
