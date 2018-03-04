/**
 *
 */
angular.module('InferyxApp')
   .controller('DagController', ['$rootScope','$scope','MetadataDagSerivce','$stateParams','$state',
    function($rootScope,$scope,MetadataDagSerivce,$stateParams,$state) {
	  $scope.isversionEnable=true;
      $scope.showdag=true;
      $scope.showgraphdiv=false
      $scope.dag={};
      $scope.dagtable=null;
  	  $scope.dag.versions=[];
      $scope.mode="false"
      $scope.dagHasChanged=true;
  	  $scope.isshowmodel=false;
  	  $scope.isSubmitEnable=true;
      var notify = {
        type: 'success',
        title: 'Success',
        content: 'Dashboard deleted Successfully',
        timeout: 3000 //time in ms
    };
  	  $scope.$watch("isshowmodel",function(newvalue,oldvalue){
  	  $scope.isshowmodel=newvalue
  	 $scope.operatorTypes=
		    [{"text":"map","caption":"map"},
			{"text":"dq","caption":"dq"},
			{"text":"dqgroup","caption":"dqgroup"},
			{"text":"load","caption":"load"}
			]
  	  sessionStorage.isshowmodel=newvalue
  	  })
  	  $scope.dagFormChange=function(){
  		 if($scope.mode == "true"){
  		   $scope.dagHasChanged=true;
  		 }
  		 else{
  		   $scope.dagHasChanged=false;

  		 }

  	  }


    $scope.showDagPage=function(){
    	$scope.showdag=true;
    	$scope.showgraphdiv=false;
        $scope.showdagflow=false;

    }/*End showDatapodPage*/

    $scope.showDagGraph=function(uuid,version){
      $scope.showdag=false;
      $scope.showgraphdiv=true;
      $scope.showdagflow=false;
      $scope.graphDataStatus=true;
      $scope.showgraph=false;
      var newUuid=uuid+"_"+version;
      MetadataDagSerivce.getGraphData(newUuid,version,"1")
	     .then(function (result) {
	    	$scope.graphDataStatus=false;
	    	$scope.showgraph=true;
	    	console.log(JSON.stringify(result.data))
	        $scope.data=result.data;
	    	$scope.graphdata=result.data;


	    });
    }/*End ShowDatapodGraph*/

    $scope.showDagWorkFlow=function(){
      $scope.showdag=false;
      $scope.showgraphdiv=false;
      $scope.showdagflow=true;

    }

    $scope.searchObjet = function(JSONObject,key,value){
  for(var i=0;i<JSONObject.length;i++){
    if(JSONObject[i][key] == value){
        return true;
      }
  }
    return false;
   }

 $scope.searcLinkObjet = function(JSONObject,valuedst,valuesrc){
    for(var i=0;i<JSONObject.length;i++){
      if(JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc ){
        return true;
      }
  }
  return false;
 }

 /* Start getNodeData*/
 $scope.getNodeData=function(data){
  $scope.graphDataStatus=true;
  $scope.showgraph=false;
  MetadataDagSerivce.getGraphData(data.uuid,data.version,"1").then(function (response) {onSuccess(response.data)});
    var onSuccess=function(response){
    $scope.graphDataStatus=false;
    $scope.showgraph=true;
    console.log(JSON.stringify(response))
    var nodelen=$scope.graphdata.nodes.length;
    var linklen=$scope.graphdata.links.length;
    for(var j=0;j<$scope.graphdata.nodes.length;j++){
        delete $scope.graphdata.nodes[j].weight
        delete $scope.graphdata.nodes[j].index
        delete $scope.graphdata.nodes[j].x
        delete $scope.graphdata.nodes[j].y
        delete $scope.graphdata.nodes[j].px
        delete $scope.graphdata.nodes[j].py
    }
      var countnode=0;
    var countlink=0;
    for(var i=0;i<response.nodes.length;i++){
        if($scope.searchObjet($scope.graphdata.nodes,"id",response.nodes[i].id) !=true){
          $scope.graphdata.nodes[nodelen+countnode]=response.nodes[i];
        countnode=countnode+1;
      }
        }
    for(var j=0;j<response.links.length;j++){
        if($scope.searchObjet($scope.graphdata.links,response.links[j].dst,response.links[j].src) !=true){
        $scope.graphdata.links[linklen+countlink]=response.links[j];
        countlink=countlink+1;
      }
        }
    console.log(JSON.stringify($scope.graphdata))
    $scope.data=$scope.graphdata
    /* $scope.$apply(function(){}); */
    }
  }/* End getNodeData*/



    if(typeof $stateParams.id != "undefined"){
      $scope.mode=$stateParams.mode;
      $scope.isversionEnable=false;
      MetadataDagSerivce.getAllVersionByUuid($stateParams.id,'dag').then(function (response) {onSuccessGetAllVersionByUuid(response.data)});
	  var onSuccessGetAllVersionByUuid=function(response){
	    	for(var i=0;i<response.length;i++){
	        	var dagversion={};
	        	dagversion.version=response[i].version;
	        	$scope.dag.versions[i]=dagversion;
	        }

	    }
     MetadataDagSerivce.getLatestByUuid($stateParams.id,"dag").then(function(response){onSuccessGetLatestByUuid(response.data)});
     var onSuccessGetLatestByUuid=function(response){
    	 var defaultversion={};
    	 $scope.dagdata=response.dagdata;
    	 $rootScope.dagJsonResponse = response.dagdata;
    	 defaultversion.version=response.dagdata.version;
 	  	 defaultversion.uuid=response.dagdata.uuid;
 	     $scope.dag.defaultVersion=defaultversion;
    	 $scope.tags=response.dagdata.tags;
    	 $scope.dagtable=response.stage;
    	 MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
    	 var onSuccessGetAllLatesMap=function(resposne){

    		 $scope.allMap=resposne;

    	 }

    	 MetadataDagSerivce.getAllLatest("dq").then(function(response){onSuccessGetAllLatesDq(response.data)});
    	 var onSuccessGetAllLatesDq=function(resposne){

    		 $scope.allDq=resposne;

    	 }
    	 MetadataDagSerivce.getAllLatest("dqgroup").then(function(response){onSuccessGetAllLatesDqGroup(response.data)});
    	 var onSuccessGetAllLatesDqGroup=function(resposne){

    		 $scope.allDqGroup=resposne;

    	 }
    	 MetadataDagSerivce.getAllLatest("load").then(function(response){onSuccessGetAllLatesLode(response.data)});
    	 var onSuccessGetAllLatesLode=function(resposne){

    		 $scope.allLoad=resposne;

    	 }
     }
    }//End If

    /* Start selectVersion*/
    $scope.selectVersion=function(){
    	 $scope.myform.$dirty=false;
      MetadataDagSerivce.getOneByUuidAndVersion($scope.dag.defaultVersion.uuid,$scope.dag.defaultVersion.version,'dag').then(function (response) {onSuccess(response.data)});
   	  var onSuccess=function(response){
   		var defaultversion={};
   		$scope.dagdata=response.dagdata;
   		defaultversion.version=response.dagdata.version;
	  	defaultversion.uuid=response.dagdata.uuid;
	    $scope.dag.defaultVersion=defaultversion;
   	 	$scope.tags=response.dagdata.tags;
   	 	$scope.dagtable=response.stage;

   	 	MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
   	 	var onSuccessGetAllLatesMap=function(resposne){
   		 $scope.allMap=resposne;
   	 	}

   	 	MetadataDagSerivce.getAllLatest("dq").then(function(response){onSuccessGetAllLatesDq(response.data)});
   	 	var onSuccessGetAllLatesDq=function(resposne){
   		 $scope.allDq=resposne;
   	 	}

   	 	MetadataDagSerivce.getAllLatest("dqgroup").then(function(response){onSuccessGetAllLatesDqGroup(response.data)});
   	 	var onSuccessGetAllLatesDqGroup=function(resposne){
   		 $scope.allDqGroup=resposne;
   		}
   	 	MetadataDagSerivce.getAllLatest("load").then(function(response){onSuccessGetAllLatesLode(response.data)});
   	 	var onSuccessGetAllLatesLode=function(resposne){
   		   $scope.allLoad=resposne;
   		}

   	  }
    }//End SelectVersin

    $scope.expandAll = function (expanded) {
        // $scope is required here, hence the injection above, even though we're using "controller as" syntax
        $scope.$broadcast('onExpandAll', {expanded: expanded});
    };

    $scope.onChangeOperator=function(type,parentindex,index){

	   if(type == "map"){
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=true;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
	      MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
	      var onSuccessGetAllLatesMap=function(resposne){
	      	$scope.allMap=resposne;

	      }
	  }
	  else if(type == "dq"){
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=true;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
	      MetadataDagSerivce.getAllLatest("dq").then(function(response){onSuccessGetAllLatesDq(response.data)});
	      var onSuccessGetAllLatesDq=function(resposne){
	    		 $scope.allDq=resposne;
	      }
	  }

	  else if(type =="dqgroup"){
    	  $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
   	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
   	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=true;
   	      $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
   	      MetadataDagSerivce.getAllLatest("dqgroup").then(function(response){onSuccessGetAllLatesDqGroup(response.data)});
  	      var onSuccessGetAllLatesDqGroup=function(resposne){
  		    $scope.allDqGroup=resposne;
  		  }
	  }
	 else if(type =="load"){

		 $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
	     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
	     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
	     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=true;
	     MetadataDagSerivce.getAllLatest("load").then(function(response){onSuccessGetAllLatesLode(response.data)});
    	 var onSuccessGetAllLatesLode=function(resposne){
    		 $scope.allLoad=resposne;

    	 }
	   }

   }//End onChangeOperator

   $scope.submiitDag=function(){
	    $scope.isshowmodel=true;
   	    $scope.dataLoading=true;
   	    $scope.iSSubmitEnable=false;
   	    $scope.dagHasChanged=true;
   	    $scope.myform.$dirty=false;

   	    var dagJson={};
	    dagJson.uuid=$scope.dagdata.uuid
	    dagJson.name=$scope.dagdata.name
	    var tagArray=[];
	    if($scope.tags !=null){
	    for(var counttag=0;counttag<$scope.tags.length;counttag++){
	    	tagArray[counttag]=$scope.tags[counttag].text;
	    }
	    }
	    dagJson.tags=tagArray
	    dagJson.active=$scope.dagdata.active;
	    dagJson.desc=$scope.dagdata.desc;
	    var stagesarray=[]
	    for(var i=0;i<$scope.dagtable.length;i++){
	     var stagesjosn={};
	     var taskarray=[];
	     stagesjosn.stageId=i//$scope.dagtable[i].stageId;
	     stagesjosn.name=$scope.dagtable[i].name
	     if(typeof  $scope.dagtable[i].dependsOn == "undefined"){
	    	 stagesjosn.dependsOn=[];
	     }
	     else{
	       stagesjosn.dependsOn=JSON.parse("[" + $scope.dagtable[i].dependsOn + "]");
	     }
	     for(var j=0;j<$scope.dagtable[i].task.length;j++){
	     var taskjson={};
	     var operatorsarray=[];
	     var operatorsjosn={};
	     var operatorinfo={}
	     var ref={};
	     taskjson.taskId=j//$scope.dagtable[i].task[j].taskId;
	     taskjson.name=$scope.dagtable[i].task[j].name;
	     if(typeof $scope.dagtable[i].task[j].dependsOn == "undefined"){
	    	 taskjson.dependsOn=[];
	     }
	     else{
	     taskjson.dependsOn=JSON.parse("[" + $scope.dagtable[i].task[j].dependsOn + "]");
	     }
	     operatorsjosn.dependsOn=[];
	     if($scope.dagtable[i].task[j].operatorinfo.type.text == "map"){
	    	 ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
	    	 ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatormap.uuid;

	     }
	     else if($scope.dagtable[i].task[j].operatorinfo.type.text == "dq"){
	    	 ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
	    	 ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatordq.uuid;

	     }
	     else if($scope.dagtable[i].task[j].operatorinfo.type.text == "dqgroup"){
	    	 ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
	    	 ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatordqgroup.uuid;

	     }
	     else if($scope.dagtable[i].task[j].operatorinfo.type.text == "load"){
	    	 ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
	    	 ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatorload.uuid;

	     }
	     operatorinfo.ref=ref;
	     operatorsjosn.operatorInfo=operatorinfo
	     operatorsarray[0]=operatorsjosn;
	     taskjson.operators=operatorsarray;
	     taskarray[j]=taskjson;
	     }
	     stagesjosn.tasks=taskarray;
	     stagesarray[i]=stagesjosn
	    }
	    dagJson.stages=stagesarray;
	    console.log(JSON.stringify(dagJson));
	    MetadataDagSerivce.submit(dagJson,"dag").then(function(response){onSuccessSubmit(response.data)},function(response){onError(response.data)});
	    var onSuccessSubmit=function(response){
	    	$scope.dataLoading=false;
       	    $scope.iSSubmitEnable=false;
       	    $scope.changemodelvalue();
       // 	    if($scope.isshowmodel == "true"){
       // 	    $('#dagsave').modal({
    		//       backdrop: 'static',
    		//       keyboard: false
    	  //  });
       // 	}
       notify.type='success',
       notify.title= 'Success',
      notify.content=$scope.saveMessage
      $scope.$emit('notify', notify);
      $scope.okdagsave();
	    }
      var onError = function(response) {
         notify.type='error',
         notify.title= 'Error',
        notify.content="Some Error Occurred"
        $scope.$emit('notify', notify);
      }
    }//End SubmitDAG

   $scope.changemodelvalue=function(){
	  	  $scope.isshowmodel=sessionStorage.isshowmodel
   };

   $scope.okdagsave=function(){
	  $('#dagsave').css("dispaly","none");
	  var hidemode="yes";
	  if(hidemode == 'yes'){
	     setTimeout(function(){  $state.go('metadata',{'type':'dag'});},2000);
	   }
	 }
  $scope.addRow=function(){
	    if($scope.dagtable == null){
	    	$scope.dagtable=[];
	    	MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
		    var onSuccessGetAllLatesMap=function(resposne){
		    $scope.allMap=resposne;

		    }
	    }
	    var stagejson={};
	    stagejson.stageId=$scope.dagtable.length;
	    stagejson.task=[];
		$scope.dagtable.splice($scope.dagtable.length, 0,stagejson);
  }
  $scope.addSubRow=function(index){
	    if(typeof $scope.dagtable[index].task == "undefined"){
	    	$scope.dagtable[index].task=[];
	    }
	    var taskjson={};
	    taskjson.taskId=$scope.dagtable[index].task.length;
	    var operatorinfo={};
	    operatorinfo.isOpetatorMap=true;
		operatorinfo.isOpetatorLoad=false;
		operatorinfo.isOpetatorDqGroup=false;
		operatorinfo.isOpetatorDQ=false;
		var obj={};
		obj.text="map";
		obj.caption="map"
		operatorinfo.type=obj;
		taskjson.operatorinfo=operatorinfo
		$scope.dagtable[index].task.splice($scope.dagtable[index].task.length, 0,taskjson);
 }
 $scope.selectAllRow=function(){

	 angular.forEach($scope.dagtable, function(stage) {
		 stage.selected = $scope.selectallstages;
        });
 }
 $scope.removeRow=function(){
	var newDataList=[];
 	$scope.selectallstages=false;
 	angular.forEach($scope.dagtable, function(selected){
          if(!selected.selected){
              newDataList.push(selected);
          }
      });
 	$scope.dagtable = newDataList;
 }
 $scope.selectAllSubRow=function(index){
	 angular.forEach($scope.dagtable[index].task, function(task) {
		 task.selected = $scope.dagtable[index].selectalltask;
        });
 }
 $scope.removeSubRow=function(index){
		var newDataList=[];
		$scope.dagtable[index].selectalltask=false;
	 	angular.forEach($scope.dagtable[index].task, function(selected){
	          if(!selected.selected){
	              newDataList.push(selected);
	          }
	      });
	 	$scope.dagtable[index].task= newDataList;
	 }
 $scope.removeSub=function(index){
	  // alert(index)
	   var newDataList=[];
	 	angular.forEach($scope.dagtable[index].task, function(selected){
	          if(!selected.selected){
	              newDataList.push(selected);
	          }
	      });
	 	$scope.dagtable[index].task= newDataList;
 }
}//End Controller



]);
