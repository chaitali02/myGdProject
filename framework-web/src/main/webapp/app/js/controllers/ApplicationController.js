AdminModule=angular.module('AdminModule');

AdminModule.controller('MetadataApplicationController',function($state,$scope,$stateParams,$rootScope,MetadataApplicationSerivce,$sessionStorage,privilegeSvc){
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
	$scope.applicationHasChanged=true;
	$scope.isSubmitEnable=true;
	$scope.applicationdata;
	$scope.showapplication=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.application={};
	$scope.application.versions=[];
	$scope.isshowmodel=false;
	$scope.state="admin";
	$scope.stateparme={"type":"application"};
	$scope.isDependencyShow=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['application'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
		$scope.privileges = privilegeSvc.privileges['application'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
	$scope.showApplicationPage=function(){
		$scope.showapplication=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}
	$scope.enableEdit=function (uuid,version) {
		$scope.showApplicationPage()
		$state.go('adminListapplication', {
			id: uuid,
			version: version,
			mode:'false'
		});
	}
	$scope.showview=function (uuid,version) {
		$scope.showApplicationPage()
		$state.go('adminListapplication', {
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

    $scope.$watch("isshowmodel",function(newvalue,oldvalue){
    	$scope.isshowmodel=newvalue
    	sessionStorage.isshowmodel=newvalue
   })

   $scope.applicationFormChange=function(){
		if($stateParams.mode == true){
	 		$scope.applicationHasChanged=true;
     	}
	 	else{
	 	  $scope.applicationHasChanged=false;
	 	}
    }


	$scope.showApplicationGraph=function(uuid,version){
		$scope.showapplication=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
	}//End showApplicationGraph





	$scope.getAllVersion=function(uuid){
		MetadataApplicationSerivce.getAllVersionByUuid(uuid,"application").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
				var applicationversion={};
				applicationversion.version=response[i].version;
				$scope.application.versions[i]=applicationversion;
			 }
		}//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion=function(uuid,version){
		$timeout(function () {
            $scope.myform.$dirty=false;
        }, 0)
        MetadataApplicationSerivce.getOneByUuidAndVersion(uuid,version,'application').then(function(response){onGetByOneUuidandVersion(response.data)});
        var onGetByOneUuidandVersion =function(response){
	    	$scope.applicationdata=response;
            var defaultversion={};
            defaultversion.version=response.version;
            defaultversion.uuid=response.uuid;
            $scope.application.defaultVersion=defaultversion;
        }

    }//End SelectVersion


	if(typeof $stateParams.id != "undefined"){

		$scope.mode=$stateParams.mode;
		$scope.isDependencyShow=true;
		/*if($sessionStorage.fromParams.type !="application" && $sessionStorage.showgraph !=true){
			$scope.state=$sessionStorage.fromStateName;
			$scope.stateparme=$sessionStorage.fromParams;
			$sessionStorage.showgraph=true;
			var data=$stateParams.id.split("_");
			var uuid=data[0];
		    var version=data[1];
		    $scope.getAllVersion(uuid)//Call SelectAllVersion Function
			$scope.selectVersion(uuid,version);//Call SelectVersion Function

		}*/

		//else{
		/*	var id;
			if($stateParams.id.indexOf("_") > -1){
				id=$stateParams.id.split("_")[0]

			}else{*/
				var id;
				id=$stateParams.id;
			//}
			$scope.getAllVersion(id)//Call SelectAllVersion Function
		    MetadataApplicationSerivce.getAllVersionByUuid(id,"application").then(function(response){onGetAllVersionByUuid(response.data)});
			var onGetAllVersionByUuid=function(response){
				for(var i=0;i< response.length;i++){
					var applicationversion={};
					applicationversion.version=response[i].version;
					$scope.application.versions[i]=applicationversion;
				 }
			}//End getAllVersionByUui
			MetadataApplicationSerivce.getLatestByUuid(id,"application").then(function(response){onGetLatestByUuid(response.data)});
			var onGetLatestByUuid =function(response){
				/*if(sessionStorage.fromStateName !="admin" && $sessionStorage.showgraph == true){
					$scope.showApplicationGraph(response.uuid,response.version);
				}*/
				$scope.applicationdata=response;
				var defaultversion={};
				defaultversion.version=response.version;
	    	   	defaultversion.uuid=response.uuid;
	    	    $scope.application.defaultVersion=defaultversion;
			}
		//}

	}//End IF

   /*Start SubmitAplication*/
	$scope.submitApplication=function(){
		var applicationJson={};
		$scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.applicationHasChanged=true;
		$scope.myform.$dirty=false;
		var applicationJson={}
	    applicationJson.uuid=$scope.applicationdata.uuid
	    applicationJson.name=$scope.applicationdata.name
	    applicationJson.desc=$scope.applicationdata.desc
	    applicationJson.active=$scope.applicationdata.active;
			applicationJson.published=$scope.applicationdata.published;
        MetadataApplicationSerivce.submit(applicationJson,'application').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
 	    var onSuccess=function(response){
	      $scope.dataLoading=false;
	      $scope.iSSubmitEnable=false;
 	      $scope.changemodelvalue();
 	     //  if($scope.isshowmodel == "true"){
   	   //     $('#applicationsave').modal({
 		   //     backdrop: 'static',
 		   //     keyboard: false
 	     //   });
   	   //  }//End If
			 notify.type='success',
			 notify.title= 'Success',
			notify.content='Application Saved Successfully'
			$scope.$emit('notify', notify);
			$scope.okapplicationsave();
      }//End Submit Api
			var onError = function(response) {
				 notify.type='error',
				 notify.title= 'Error',
				notify.content="Some Error Occurred"
				$scope.$emit('notify', notify);
			}
	}/*End SubmitApplication*/

	$scope.changemodelvalue=function(){
	  $scope.isshowmodel=sessionStorage.isshowmodel
    };

    $scope.okapplicationsave=function(){
   	    $('#applicationsave').css("dispaly","none");
   	    var hidemode="yes";
   	    if(hidemode == 'yes'){
   			setTimeout(function(){  $state.go('admin',{'type':'application'});},2000);
       }
    }

});
