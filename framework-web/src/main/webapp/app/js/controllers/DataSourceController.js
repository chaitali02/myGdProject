
AdminModule=angular.module('AdminModule');

AdminModule.controller('MetadataDatasourceController',function(privilegeSvc,CommonService,$state,$scope,$stateParams,$sessionStorage,$timeout,MetadataDatasourceSerivce){

	$scope.isHiveFieldDisabled=true;
	$scope.isFileFieldDisabled=true;
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
	$scope.datasourcedata;
	$scope.showdatasource=true;
	$scope.data=null;
	$scope.showgraph=false;
	$scope.showgraphdiv=false;
	$scope.graphDataStatus=false;
	$scope.datasource={};
	$scope.datasource.versions=[];
	$scope.datasourceTypes=["HIVE","FILE"];
  $scope.isshowmodel=false;
  $scope.datasourceHasChanged=true;
	$scope.isSubmitEnable=true;
	$scope.state="admin";
	$scope.stateparme={"type":"datasource"};
	$scope.isDependencyShow=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['datasource'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
		$scope.privileges = privilegeSvc.privileges['datasource'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
	$scope.showDatasourcePage=function(){
		$scope.showdatasource=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}//End showDatasourcePage
	$scope.enableEdit=function (uuid,version) {
		$scope.showDatasourcePage()
		$state.go('adminListdatasource', {
			id: uuid,
			version: version,
			mode:'false'
		});
	}
	$scope.showview=function (uuid,version) {
		$scope.showDatasourcePage()
		$state.go('adminListdatasource', {
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
	$timeout(function () {
       $scope.myform.$dirty=false;
       }, 0);

	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
      	console.log(fromParams)
		$sessionStorage.fromStateName=fromState.name
	    $sessionStorage.fromParams=fromParams

    });

    $scope.$watch("isshowmodel",function(newvalue,oldvalue){
    $scope.isshowmodel=newvalue
    sessionStorage.isshowmodel=newvalue
   })

   $scope.datasourceFormChange=function(){
		if($scope.mode == "true"){
		 	$scope.datasourceHasChanged=true;
	    }
		else{
		 	$scope.datasourceHasChanged=false;
		}
    }


	$scope.showDatasourceGraph=function(uuid,version){
		$scope.showdatasource=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
	}//End showDatasourceGraph




	$scope.onChangeDatasoureType=function(){
		if($scope.datasourcetype == $scope.datasourceTypes[0]){
			$scope.isHiveFieldDisabled=false;
			$scope.isFileFieldDisabled=true;
			$scope.datasourcedata.path=""
		}
		else{
			$scope.isFileFieldDisabled=false;
			$scope.isHiveFieldDisabled=true;
			$scope.datasourcedata.password="";
			$scope.datasourcedata.username="";
			$scope.datasourcedata.port="";
			$scope.datasourcedata.dbname="";
			$scope.datasourcedata.driver="";
		}
	}//End onChangeDatasoureType

	$scope.getAllVersion=function(uuid){
		 MetadataDatasourceSerivce.getAllVersionByUuid(uuid,"datasource").then(function(response){onGetAllVersionByUuid(response.data)});
		 var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
		    	var datasourceversion={};
		    	 datasourceversion.version=response[i].version;
		    	 $scope.datasource.versions[i]=datasourceversion;
		     }
		  }//End getAllVersionByUuid
	}//End GetAllVersion

	$scope.selectVersion=function(uuid,version){
		  $timeout(function () {
               $scope.myform.$dirty=false;
           }, 0)
		  MetadataDatasourceSerivce.getOneByUuidAndVersion(uuid,version,'datasource').then(function (response) {onSuccess(response.data)});
		  var onSuccess=function(response){
		      var defaultversion={};
		      defaultversion.version=response.version;
		      defaultversion.uuid=response.uuid;
		      $scope.datasource.defaultVersion=defaultversion;
		      $scope.datasourcedata=response
		      $scope.datasourcetype=response.type;
		      $scope.onChangeDatasoureType();
		      var tags=[];
	        $scope.tags=[];
		      if(response.tags !=null){
		          for(var i=0;i<response.tags.length;i++){
		            var tag={};
		            tag.text=response.tags[i];
		            tags[i]=tag
		            $scope.tags=tags;
		        }//End for loop
		      }//End Innder If
		    }//End getLatestByUuid
	  }//End selectVersion

  /*Start If*/
  if(typeof $stateParams.id !="undefined"){
	 $scope.mode=$stateParams.mode;

	 $scope.isDependencyShow=true;
	/* if($sessionStorage.fromParams.type !="datasource"){
			$scope.state=$sessionStorage.fromStateName;
			$scope.stateparme=$sessionStorage.fromParams;
			$sessionStorage.showgraph=true;
			var data=$stateParams.id.split("_");
			var uuid=data[0];
		    var version=data[1];
		    $scope.getAllVersion(uuid)//Call SelectAllVersion Function
			$scope.selectVersion(uuid,version);//Call SelectVersion Function

	 }//End Inner If
	 else{*/
		 $scope.getAllVersion($stateParams.id)//Call SelectAllVersion Function
		 CommonService.getOneByUuidAndVersion($stateParams.id,$stateParams.version,'datasource').then(function (response) {onSuccess(response.data)});
	     var onSuccess=function(response){
	      var defaultversion={};
	      defaultversion.version=response.version;
	      defaultversion.uuid=response.uuid;
	      $scope.datasource.defaultVersion=defaultversion;
	      $scope.datasourcedata=response
	      $scope.datasourcetype=response.type;
	      $scope.onChangeDatasoureType();
	      var tags=[];
	      if(response.tags !=null){
	          for(var i=0;i<response.tags.length;i++){
	            var tag={};
	            tag.text=response.tags[i];
	            tags[i]=tag
	            $scope.tags=tags;
	        }//End for loop
	      }//End Innder If
	    }//End getLatestByUuid
	 /*}//End Inner Else */
  }//End If



  /*Start SubmitDatasource*/
	$scope.submitDatasource=function(){
	    $scope.isshowmodel=true;
	    $scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.datasourceHasChanged=true;
		$scope.myform.$dirty=false;
		var datasourceJson={};
	    datasourceJson.uuid=$scope.datasourcedata.uuid
	    datasourceJson.name=$scope.datasourcedata.name
	    datasourceJson.desc=$scope.datasourcedata.desc
	    datasourceJson.createdOn=$scope.datasourcedata.createdOn
	    datasourceJson.type=$scope.datasourcetype
	    datasourceJson.port=$scope.datasourcedata.port
	    datasourceJson.driver=$scope.datasourcedata.driver
	    datasourceJson.username=$scope.datasourcedata.username
	    datasourceJson.host=$scope.datasourcedata.host
	    datasourceJson.access=$scope.datasourcedata.access
	    datasourceJson.dbname=$scope.datasourcedata.dbname
	    datasourceJson.password=$scope.datasourcedata.password
	    datasourceJson.path=$scope.datasourcedata.path
	    datasourceJson.active=$scope.datasourcedata.active;
		datasourceJson.published=$scope.datasourcedata.published;
		datasourceJson.sessionParameters=$scope.datasourcedata.sessionParameters
	    var tagArray=[];
        if($scope.tags !=null){
            for(var counttag=0;counttag<$scope.tags.length;counttag++){
                tagArray[counttag]=$scope.tags[counttag].text;
            }
        }
        datasourceJson.tags=tagArray
	    MetadataDatasourceSerivce.submit(datasourceJson,'datasource').then(function(response){onSuccess(response)},function(response){onError(response.data)});
	    var onSuccess=function(response){
	        $scope.dataLoading=false;
		    $scope.iSSubmitEnable=false;
	 	    $scope.changemodelvalue();
				//    if($scope.isshowmodel == "true"){
	   	  //       $('#datasourcesave').modal({
	 		  //       backdrop: 'static',
	 		  //       keyboard: false
	 	    //     });
	   	  //   }//End If
				notify.type='success',
				notify.title= 'Success',
			 notify.content='Datasource Saved Successfully '
			 $scope.$emit('notify', notify);
			 $scope.okdatasourcesave();
	    }
			var onError = function(response) {
				 notify.type='error',
				 notify.title= 'Error',
				notify.content="Some Error Occurred"
				$scope.$emit('notify', notify);
			}
	}/*End SubmitDatapod*/

	$scope.changemodelvalue=function(){
	   $scope.isshowmodel=sessionStorage.isshowmodel
	};

	$scope.okdatasourcesave=function(){
	    $('#datasourcesave').css("dispaly","none");
	    var hidemode="yes";
	    if(hidemode == 'yes'){
	      setTimeout(function(){  $state.go('admin',{'type':'datasource'});},2000);
	    }
	}

});
