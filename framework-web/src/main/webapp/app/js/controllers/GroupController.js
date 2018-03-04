AdminModule= angular.module('AdminModule');

AdminModule.controller('AdminGroupController', function(CommonService,$state,$timeout,$filter,$stateParams,$rootScope,$scope,AdminGroupService,privilegeSvc) {
	$scope.mode=""
	$scope.group={};
	$scope.group.versions=[];
	$scope.showgroup=true;
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
	$scope.groupHasChanged=true;
	$scope.isshowmodel=false;
	$scope.isDependencyShow=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['group'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
		$scope.privileges = privilegeSvc.privileges['group'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
	/*Start showGroupPage*/
	$scope.showGroupPage=function(){
		$scope.showgroup=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}/*End showgroupPage*/
	$scope.enableEdit=function (uuid,version) {
		$scope.showGroupPage()
		$state.go('adminListgroup', {
			id: uuid,
			version: version,
			mode:'false'
		});
	}
	$scope.showview=function (uuid,version) {
		$scope.showGroupPage()
		$state.go('adminListgroup', {
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
	})
	$scope.groupFormChange=function(){
		if($scope.mode == "true"){
		    $scope.groupHasChanged=true;
		}
		else{
			$scope.groupHasChanged=false;

		}
	}



	$scope.showGroupGraph=function(uuid,version){
		$scope.showgroup=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;

	}/*End ShowGroupGraph*/

	AdminGroupService.getAllLatest('role').then(function(response){onSuccessRole(response.data)});
    var onSuccessRole=function(response){
        var roleInfoArray=[];
    	for(var i=0;i<response.data.length;i++){
    	   var roleref={};
    	   roleref.uuid=response.data[i].uuid;
    	   roleref.type=response.data[i].type;
    	   roleref.id=response.data[i].uuid
    	   roleref.name=response.data[i].name;
    	   roleref.version=response.data[i].version;
    	   roleInfoArray[i]=roleref;
        }
    	$scope.roleall=roleInfoArray;
			//alert(JSON.stringify(response))

    }
		AdminGroupService.getAllLatest('application').then(function(response){onSuccessApp(response.data)});
			var onSuccessApp=function(response){
					var appInfoArray=[];
				for(var i=0;i<response.data.length;i++){
					 var appref={};
					 appref.uuid=response.data[i].uuid;
					 appref.type=response.data[i].type;
					 appref.id=response.data[i].uuid
					 appref.name=response.data[i].name;
					 appref.version=response.data[i].version;
					 appInfoArray[i]=appref;
					}
					$scope.appall=appInfoArray;
				//alert(JSON.stringify(response))
			}

	/*start If*/
	if(typeof $stateParams.id != "undefined"){
		$scope.mode=$stateParams.mode

		$scope.isDependencyShow=true;
		AdminGroupService.getAllVersionByUuid($stateParams.id,"group").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid =function(response){
			for(var i=0;i<response.length;i++){
				var groupversion={};
				groupversion.version=response[i].version;
	   	    	$scope.group.versions[i]=groupversion;
			}

		}//End getAllVersionByUuid

		CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"group").then(function(response){onGetLatestByUuid(response.data)});
		var onGetLatestByUuid =function(response){
			$scope.groupdata=response;
			var defaultversion={};
					defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.group.defaultVersion=defaultversion;
    	    //$scope.roleInfoTags=response.roleInfo;
			$scope.appId=response.appId.ref
			$scope.roleId=response.roleId.ref
//    	    var roleInfo=[];
//    	    for(var j=0;j<response.roleInfo.length;j++){
//    	    	var roletag={};
//    	    	roletag.uuid=response.roleInfo[j].ref.uuid;
//    	    	roletag.type=response.roleInfo[j].ref.type;
//    	    	roletag.name=response.roleInfo[j].ref.name;
//    	    	roletag.id=response.roleInfo[j].ref.uuid;
//    	    	roleInfo[j]=roletag
//    	    }

    	   // $scope.roleInfoTags=roleInfo;
			var tags=[];
			if(response.tags!=null){
			  	for(var i=0;i<response.tags.length;i++){
			  		var tag={};
			  		tag.text=response.tags[i];
			  		tags[i]=tag
			  		$scope.tags=tags;
			  	}
			}//End Innter If
		}//End getLatestByUuid
	}/*End If*/
	else{


	}//End Else


	 /* Start selectVersion*/
	$scope.selectVersion=function(){
		$scope.tags=null;
		$scope.myform.$dirty=false;
		AdminGroupService.getOneByUuidAndVersion($scope.group.defaultVersion.uuid,$scope.group.defaultVersion.version,'group').then(function(response){onGetByOneUuidandVersion(response.data)});
		var onGetByOneUuidandVersion =function(response){
			$scope.groupdata=response;
			var defaultversion={};
			defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.group.defaultVersion=defaultversion;
//    	    var roleInfo=[];
//    	    for(var j=0;j<response.roleInfo.length;j++){
//    	    	var roletag={};
//    	    	roletag.uuid=response.roleInfo[j].ref.uuid;
//    	    	roletag.type=response.roleInfo[j].ref.type;
//    	    	roletag.name=response.roleInfo[j].ref.name;
//    	    	roletag.id=response.roleInfo[j].ref.uuid;
//    	    	roleInfo[j]=roletag
//    	    }

    	   // $scope.roleInfoTags=roleInfo;
			var tags=[];
			if(response.tags!=null){
			  	for(var i=0;i<response.tags.length;i++){
			  		var tag={};
			  		tag.text=response.tags[i];
			  		tags[i]=tag
			  		$scope.tags=tags;
			  	}
			}//End Innter If
		}
	} /* end selectVersion*/

	$scope.loadrole = function(query) {
		return $timeout(function () {
		    return $filter('filter')($scope.roleall, query);
		    });
    };

	$scope.okgroupsave=function(){
		$('#okrolesave').css("dispaly","none");
		var hidemode="yes";
		if(hidemode == 'yes'){
			setTimeout(function(){  $state.go('admin',{'type':'group'});},2000);
		}
    }


	   /*Start submitGroup*/
	$scope.submitGroup=function(){

       	var groupJson={};
       	$scope.dataLoading=true;
       	$scope.iSSubmitEnable=false;
   	   	$scope.groupHasChanged=true;
   	   	$scope.isshowmodel=true;
   	    $scope.myform.$dirty=false;
   	    groupJson.uuid=$scope.groupdata.uuid;
	   		groupJson.name=$scope.groupdata.name;
	   		groupJson.desc=$scope.groupdata.desc;
	   		groupJson.active=$scope.groupdata.active;
				groupJson.published=$scope.groupdata.published;
				var Appid={};
				var refAppid={};
				refAppid.uuid=$scope.appId.uuid;
				refAppid.type="application";
				Appid.ref=refAppid;
				groupJson.appId=Appid
				var Roleid={};
				var refRoleid={};
				refRoleid.uuid=$scope.roleId.uuid;
				refRoleid.type="role";
				Roleid.ref=refRoleid;
				groupJson.roleId=Roleid
				// var refAppid={}
				// refAppid=$scope.appId;
				// groupJson.appId.ref=refAppid;
				// var refRoleid={}
				// refRoleid=$scope.roleId;
				// groupJson.roleId.ref=refRoleid;
       	var tagArray=[];
        if($scope.tags !=null){
            for(var c=0;c<$scope.tags.length;c++){
       	        tagArray[c]=$scope.tags[c].text;
            }
        }
        groupJson.tags=tagArray

//       var roleInfoArray=[];
//        if($scope.roleInfoTags!=null){
//	        for(var c=0;c<$scope.roleInfoTags.length;c++){
//		   		var roleinforef={};
//		   		var roleref={};
//		     	roleinforef.uuid=$scope.roleInfoTags[c].uuid;
//		     	roleinforef.type="role";
//	         	roleref.ref=roleinforef
//		     	roleInfoArray.push(roleref);
//		   	}
//		}
//       	groupJson.roleInfo=roleInfoArray
      	AdminGroupService.submit(groupJson,'group').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
       	var onSuccess=function(response){
    	   $scope.dataLoading=false;
    	   $scope.iSSubmitEnable=false;
    	   $scope.changemodelvalue();
      	  //  if($scope.isshowmodel == "true"){
					//    		$('#groupsave').modal({
  		    //  	 	backdrop: 'static',
  		    //   		keyboard: false
  	      // 		});
					//    	}
					notify.type='success',
					notify.title= 'Success',
					notify.content='Group Saved Successfully'
					$scope.$emit('notify', notify);
					$scope.okgroupsave();
       	}
				var onError = function(response) {
					 notify.type='error',
					 notify.title= 'Error',
					notify.content="Some Error Occurred"
					$scope.$emit('notify', notify);
				}
	}/*End Submitgroup*/

	$scope.changemodelvalue=function(){
		$scope.isshowmodel=sessionStorage.isshowmodel
	};

});
