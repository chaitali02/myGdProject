/**
 *
 */
AdminModule= angular.module('AdminModule');

AdminModule.controller('AdminPrivilegeController', function(CommonService,$state,$stateParams,$rootScope,$scope,AdminPrivilegeService,privilegeSvc) {

	$scope.privilege={};
	$scope.privilege.versions=[];
	$scope.privilegeTypes=["Add","View","Edit","Delete","Execute","Clone","Export","Restore","Publish","Unpublish"];
	$scope.selectprivilegeType=$scope.privilegeTypes[0]
	$scope.showprivilege=true;
	$scope.showgraphdiv=false;
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
	$scope.mode=$stateParams.mode
	$scope.dataLoading=false;
	$scope.isSubmitEnable=true;
	$scope.privilegeHasChanged=true;
	$scope.isshowmodel=false;
	$scope.isDependencyShow=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['privilege'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
		$scope.privileges = privilegeSvc.privileges['privilege'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
		/*Start showPrivilegePage*/
		$scope.showPrivilegePage=function(){
			$scope.showprivilege=true;
			$scope.showgraph=false
			$scope.graphDataStatus=false;
			$scope.showgraphdiv=false
		}/*End showDatapodPage*/
		$scope.enableEdit=function (uuid,version) {
			$scope.showPrivilegePage()
			$state.go('adminListprivilege', {
				id: uuid,
				version: version,
				mode:'false'
			});
		}
		$scope.showview=function (uuid,version) {
			$scope.showPrivilegePage()
			$state.go('adminListprivilege', {
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
    $scope.$watch("isshowmodel",function(newvalue,oldvalue){
		$scope.isshowmodel=newvalue
		sessionStorage.isshowmodel=newvalue
	});

	$scope.privilegeFormChange=function(){
		 if($scope.mode == "true"){
		 $scope.privilegeHasChanged=true;
		 }
		 else{
			 $scope.privilegeHasChanged=false;
		 }
	}

	$scope.okprivilegesave=function(){
		$('#okprivilegesave').css("dispaly","none");
		var hidemode="yes";
		if(hidemode == 'yes'){
		   setTimeout(function(){  $state.go('admin',{'type':'privilege'});},2000);
		}
	}

    $scope.changemodelvalue=function(){
	    $scope.isshowmodel=sessionStorage.isshowmodel
	};



	$scope.showPrivilegeGraph=function(uuid,version){
		$scope.showprivilege=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;

	}/*End ShowDatapodGraph*/




	if(typeof $stateParams.id != "undefined"){
		$scope.mode=$stateParams.mode

		$scope.isDependencyShow=true;
		AdminPrivilegeService.getAllVersionByUuid($stateParams.id,"privilege").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid =function(response){
			for(var i=0;i<response.length;i++){
				var privilegeversion={};
				privilegeversion.version=response[i].version;
	   	    	$scope.privilege.versions[i]=privilegeversion;
			}
		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"privilege").then(function(response){onGetLatestByUuid(response.data)});
		var onGetLatestByUuid =function(response){
			$scope.privilegedata=response;
			var defaultversion={};
			defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.privilege.defaultVersion=defaultversion;
    	    $scope.selectprivilegeType=response.privType

    	    AdminPrivilegeService.getAll("meta").then(function(response){onGetAll(response.data)});
    		var onGetAll =function(response){
    			$scope.allmeta=response
    			var defaultoption={};
    			defaultoption.uuid=$scope.privilegedata.metaId.ref.uuid;
    			defaultoption.name=$scope.privilegedata.metaId.ref.name;
    			$scope.allmeta.defaultoption=defaultoption;

    		}
			var tags=[];
			if(response.tags!=null){
			  	for(var i=0;i<response.tags.length;i++){
			  		var tag={};
			  		tag.text=response.tags[i];
			  		tags[i]=tag
			  		$scope.tags=tags;
			  	}
			}
		}
	}
	//end of if
	else{

		 AdminPrivilegeService.getAll("meta").then(function(response){onGetAll(response.data)});
 		var onGetAll =function(response){
 			$scope.allmeta=response

 		}
	}
	$scope.selectVersion=function(){
		$scope.allmeta=null;
		$scope.tags=null;
		 $scope.myform.$dirty=false;
		AdminPrivilegeService.getByOneUuidandVersion($scope.privilege.defaultVersion.uuid,$scope.privilege.defaultVersion.version,'privilege').then(function(response){onGetByOneUuidandVersion(response.data)});
		var onGetByOneUuidandVersion =function(response){
			$scope.privilegedata=response;
			var defaultversion={};
			defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.privilege.defaultVersion=defaultversion;
    	    $scope.selectprivilegeType=response.privType
    	    AdminPrivilegeService.getAll("meta").then(function(response){onGetAll(response.data)});
    		var onGetAll =function(response){
    			$scope.allmeta=response
    			var defaultoption={};
    			defaultoption.uuid=$scope.privilegedata.metaId.ref.uuid;
    			defaultoption.name=$scope.privilegedata.metaId.ref.name;
    			$scope.allmeta.defaultoption=defaultoption;
    		}
			var tags=[];
			if(response.tags!=null){
			  	for(var i=0;i<response.tags.length;i++){
			  		var tag={};
			  		tag.text=response.tags[i];
			  		tags[i]=tag
			  		$scope.tags=tags;
			  	}
			}
		}
	}



	/*Start submitPrivilege*/
	$scope.submitPrivilege=function(){
       var privilegeJson={};
       $scope.dataLoading=true;
       $scope.iSSubmitEnable=false;
   	   $scope.privilegeHasChanged=true;
   	   $scope.isshowmodel=true;
   	   $scope.myform.$dirty=false;
       privilegeJson.uuid=$scope.privilegedata.uuid;
       privilegeJson.name=$scope.privilegedata.name;
       privilegeJson.desc=$scope.privilegedata.desc;
       privilegeJson.active=$scope.privilegedata.active;
			 privilegeJson.published=$scope.privilegedata.published;
       privilegeJson.privType=$scope.selectprivilegeType;
       var defaultoption={};
       var metaref={};
       defaultoption.uuid=$scope.allmeta.defaultoption.uuid;
       defaultoption.type="meta";
       $scope.defaultoption=defaultoption;
       metaref.ref=defaultoption;
	   privilegeJson.metaId=metaref;
       var tagArray=[];
       if($scope.tags !=null){
	       for(var counttag=0;counttag<$scope.tags.length;counttag++){
	       	tagArray[counttag]=$scope.tags[counttag].text;
	       }
       }
       privilegeJson.tags=tagArray
       AdminPrivilegeService.submit(privilegeJson,'privilege').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
       var onSuccess=function(response){
    	   $scope.dataLoading=false;
    	   $scope.iSSubmitEnable=false;
    	   $scope.changemodelvalue();
      	//  if($scope.isshowmodel == "true"){
    	  //  $('#privilegesave').modal({
  		  //     backdrop: 'static',
  		  //     keyboard: false
  	    //   });
    	  //  }
				notify.type='success',
				notify.title= 'Success',
			 notify.content='Privilege Saved Successfully'
			 $scope.$emit('notify', notify);
			 $scope.okprivilegesave();

       }
			 var onError = function(response) {
					notify.type='error',
					notify.title= 'Error',
				 notify.content="Some Error Occurred"
				 $scope.$emit('notify', notify);
			 }
	}/*End Submitprivilege*/

});
