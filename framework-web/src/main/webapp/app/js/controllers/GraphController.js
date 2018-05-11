
angular.module('InferyxApp')
.controller('GraphResourcesController', ['$rootScope','privilegeSvc','$scope','MetadataDagSerivce','$stateParams','$state','graphService','dagValidationSvc','dagMetaDataService','$http','$filter','uibButtonConfig',
 function($rootScope,privilegeSvc,$scope,MetadataDagSerivce,$stateParams,$state,graphService,dagValidationSvc,dagMetaDataService,$http,$filter,buttonConfig) {
   
    $scope.isTemplate=true;
    $scope.isUseTemplate=false;
    $scope.editMode = false;
    $scope.addMode=false;
    $scope.editMode = $stateParams.mode == 'true' ? false : true;
    $scope.createMode = $stateParams.action == 'add';
    $scope.selectedDagTemplate=null;
    $scope.isDependencyShow=false;
    var yourDateObject = new Date();
    var convertedDate = $filter('date')( yourDateObject , "EEE MMM dd hh:mm:ss Z yyyy", "EDT");
    var notify = {
     type: 'success',
     title: 'Success',
     content: 'Dashboard deleted Successfully',
     timeout: 3000 //time in ms
    };
    $scope.toggleZoom = function(){
      $scope.showZoom = !$scope.showZoom;
    }

    $scope.zoomSize = 7;
    $scope.$watch('zoomSize',function () {
      try {
        $scope.$broadcast('zoomChange',$scope.zoomSize);
      }catch (e) {
     
      }finally{
      }
    });

    $scope.$on('refreshData',function (e,data) {
      if($scope.showdag){
        $scope.$broadcast('createGraph',$scope.dagdata);
      }
    });

    $scope.isversionEnable=true;
    $scope.showdag=true;
    $scope.showgraphdiv=false
    $scope.dag={};
    $scope.dagtable=null;
    $scope.dag.versions=[];
    $scope.mode=""
    $scope.dagHasChanged=true;
    $scope.isshowmodel=false;
    $scope.isSubmitEnable=true;
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
    $scope.privileges = [];
    $scope.privileges = privilegeSvc.privileges['dag'] || [];
    $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
    $scope.$on('privilegesUpdated',function (e,data) {
      $scope.privileges = privilegeSvc.privileges['dag'] || [];
      $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
    });

    $scope.showDagPage=function(){
      $scope.showdag=true;
      $scope.showgraphdiv=false;
      $scope.showdagflow=false;
    }/*End showDatapodPage*/
   
    $scope.enableEdit=function (uuid,version) {
      $scope.showDagPage()
      $state.go('createwf', {
        id: uuid,
        version: version,
        mode:'false'
      });
    }

    $scope.showview=function (uuid,version) {
      if(!$scope.isEdit){
        $scope.showDagPage()
        $state.go('createwf', {
          id: uuid,
          version: version,
          mode:'true'
        });
      }     
    }
   
    $scope.$watch("isshowmodel",function(newvalue,oldvalue){
      $scope.isshowmodel=newvalue
      $scope.operatorTypes=[
       {"text":"map","caption":"map"},
       {"text":"dq","caption":"dq"},
       {"text":"dqgroup","caption":"dqgroup"},
       {"text":"load","caption":"load"}
     ];
     sessionStorage.isshowmodel=newvalue;
    });

    $scope.dagFormChange=function(){
      if($scope.mode == "true"){
        $scope.dagHasChanged=true;
      }
      else{
        $scope.dagHasChanged=false;
      }
    }

    $scope.showDagGraph=function(uuid,version){
      $scope.showdag=false;
      $scope.showgraphdiv=true;
      $scope.showdagflow=false;
      $scope.graphDataStatus=true;
      $scope.showgraph=false;
    }/*End ShowDatapodGraph*/

   $scope.showDagWorkFlow = function(){
     $scope.showdag=true;
     $scope.showgraphdiv=false;
     $scope.showdagflow=true;
   }

   if(typeof $stateParams.id != "undefined"){
     $scope.mode=$stateParams.mode;
   //  $scope.isversionEnable=false;
     $scope.isDependencyShow=true;
     MetadataDagSerivce.getAllVersionByUuid($stateParams.id,'dag').then(function (response) {onSuccessGetAllVersionByUuid(response.data)});
     var onSuccessGetAllVersionByUuid=function(response){
       for(var i=0;i<response.length;i++){
         var dagversion={};
         dagversion.version=response[i].version;
         $scope.dag.versions[i]=dagversion;
       }
     }//End getAllVersionByUuid

     MetadataDagSerivce.getOneByUuidAndVersion($stateParams.id,$stateParams.version,"dag").then(function(response){onSuccessGetLatestByUuid(response.data)});
     var onSuccessGetLatestByUuid=function(response){
       var defaultversion={};
       $scope.dagdata=response.dagdata;
       $scope.pipelineName = response.dagdata.name;
       defaultversion.version=response.dagdata.version;
       defaultversion.uuid=response.dagdata.uuid;
       $scope.uuid = response.dagdata.uuid;
       $scope.version = response.dagdata.version;
       $scope.dag.defaultVersion=defaultversion;
       $scope.tags=response.dagdata.tags;
       $scope.dagtable=response.stage;
       if(response.dagdata.templateInfo != null){

         $scope.allDagTemplate=[];
         $scope.isUseTemplate=true;
         MetadataDagSerivce.getDagTemplates('dag').then(function (response) {onSuccessGetDagTemplates(response.data)});
         var onSuccessGetDagTemplates=function(response){
         console.log(response)
         for(var i=0;i<response.length;i++){
           var dagtemplate={};
           dagtemplate.version=response[i].version;
           dagtemplate.uuid=response[i].uuid;
           dagtemplate.name=response[i].name;
           $scope.allDagTemplate[i]=dagtemplate
         }
       }//End getDagTemplates
         $scope.selectedDagTemplate={};
         $scope.selectedDagTemplate.uuid=response.dagdata.templateInfo.ref.uuid;
         $scope.selectedDagTemplate.version=response.dagdata.templateInfo.ref.version;
         $scope.selectedDagTemplate.name=response.dagdata.templateInfo.ref.name;
       }
       setTimeout(function () {
         if($stateParams.tab){
           $scope.continueCount =  $stateParams.tab - 1;
             $('.button-next').click();
         }
       }, 500);

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
     } //End getLatestByUuid
   }//End If
   else{
     //$scope.mode="false";
     $scope.addMode=true;
     $scope.allDagTemplate=[];
     MetadataDagSerivce.getDagTemplates('dag').then(function (response) {onSuccessGetDagTemplates(response.data)});
     var onSuccessGetDagTemplates=function(response){
       console.log(response)
       for(var i=0;i<response.length;i++){
         var dagtemplate={};
         dagtemplate.version=response[i].version;
         dagtemplate.uuid=response[i].uuid;
         dagtemplate.name=response[i].name;
         $scope.allDagTemplate[i]=dagtemplate
       }
     }//End getDagTemplates
   }
   /* Start selectVersion*/
   $scope.selectVersion=function(uuid,version){
    MetadataDagSerivce.getOneByUuidAndVersion(uuid,version,"dag").then(function(response){onSuccessGetOneByUuidAndVersion(response.data)});
    var onSuccessGetOneByUuidAndVersion=function(response){
      $scope.isGraphRenderEdit=false;
      var defaultversion={};
      $scope.dagdata=response.dagdata;
      $scope.pipelineName = response.dagdata.name;
      defaultversion.version=response.dagdata.version;
      defaultversion.uuid=response.dagdata.uuid;
      $scope.uuid = response.dagdata.uuid;
      $scope.version = response.dagdata.version;
      $scope.dag.defaultVersion=defaultversion;
      $scope.tags=response.dagdata.tags;
     // $scope.dagtable=response.stage;
      // if(response.dagdata.templateInfo != null){

      //   $scope.allDagTemplate=[];
      //   $scope.isUseTemplate=true;
      //   MetadataDagSerivce.getDagTemplates('dag').then(function (response) {onSuccessGetDagTemplates(response.data)});
      //   var onSuccessGetDagTemplates=function(response){
      //   console.log(response)
      //   for(var i=0;i<response.length;i++){
      //     var dagtemplate={};
      //     dagtemplate.version=response[i].version;
      //     dagtemplate.uuid=response[i].uuid;
      //     dagtemplate.name=response[i].name;
      //     $scope.allDagTemplate[i]=dagtemplate
      //   }
      // }//End getDagTemplates
      //   $scope.selectedDagTemplate={};
      //   $scope.selectedDagTemplate.uuid=response.dagdata.templateInfo.ref.uuid;
      //   $scope.selectedDagTemplate.version=response.dagdata.templateInfo.ref.version;
      //   $scope.selectedDagTemplate.name=response.dagdata.templateInfo.ref.name;
      // }
      // setTimeout(function () {
      //   if($stateParams.tab){
      //     $scope.continueCount =  $stateParams.tab - 1;
      //       $('.button-next').click();
      //   }
      // }, 500);

      // MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
      // var onSuccessGetAllLatesMap=function(resposne){
      //   $scope.allMap=resposne;
      // }

      // MetadataDagSerivce.getAllLatest("dq").then(function(response){onSuccessGetAllLatesDq(response.data)});
      // var onSuccessGetAllLatesDq=function(resposne){
      //   $scope.allDq=resposne;
      // }
      // MetadataDagSerivce.getAllLatest("dqgroup").then(function(response){onSuccessGetAllLatesDqGroup(response.data)});
      // var onSuccessGetAllLatesDqGroup=function(resposne){
      //     $scope.allDqGroup=resposne;
      // }

      // MetadataDagSerivce.getAllLatest("load").then(function(response){onSuccessGetAllLatesLode(response.data)});
      // var onSuccessGetAllLatesLode=function(resposne){
      //   $scope.allLoad=resposne;
      // }
    } //End getLatestByUuid
   }//End SelectVersin



   // $scope.expandAll = function (expanded) {
   //   $scope.$broadcast('onExpandAll', {expanded: expanded});
   // };

 //   $scope.onChangeOperator=function(type,parentindex,index){
 //   if(type == "map"){
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=true;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
 //     MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
 //     var onSuccessGetAllLatesMap=function(resposne){
 //       $scope.allMap=resposne;

 //     }
 //   }
 //   else if(type == "dq"){
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=true;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
 //     MetadataDagSerivce.getAllLatest("dq").then(function(response){onSuccessGetAllLatesDq(response.data)});
 //     var onSuccessGetAllLatesDq=function(resposne){
 //        $scope.allDq=resposne;
 //     }
 //   }

 //   else if(type =="dqgroup"){
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
 //       $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
 //       $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=true;
 //       $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=false;
 //       MetadataDagSerivce.getAllLatest("dqgroup").then(function(response){onSuccessGetAllLatesDqGroup(response.data)});
 //       var onSuccessGetAllLatesDqGroup=function(resposne){
 //       $scope.allDqGroup=resposne;
 //     }
 //   }
 //   else if(type =="load"){
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorMap=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDQ=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorDqGroup=false;
 //     $scope.dagtable[parentindex].task[index].operatorinfo.isOpetatorLoad=true;
 //     MetadataDagSerivce.getAllLatest("load").then(function(response){onSuccessGetAllLatesLode(response.data)});
 //     var onSuccessGetAllLatesLode=function(resposne){
 //       $scope.allLoad=resposne;
 //     }
 //   }
 // }//End onChangeOperator

 // $scope.submiitDag=function(){
 //     $scope.isshowmodel=true;
 //     $scope.dataLoading=true;
 //     $scope.iSSubmitEnable=false;
 //     $scope.dagHasChanged=true;
 //     var dagJson={};
 //     dagJson.uuid=$scope.dagdata.uuid
 //     dagJson.name=$scope.dagdata.name
 //     var tagArray=[];
 //     if($scope.tags !=null){
 //       for(var counttag=0;counttag<$scope.tags.length;counttag++){
 //         tagArray[counttag]=$scope.tags[counttag].text;
 //       }
 //     }
 //     dagJson.tags=tagArray
 //     dagJson.active=$scope.dagdata.active;
 //     dagJson.published=$scope.dagdata.published;
 //     dagJson.desc=$scope.dagdata.desc;
 //     var stagesarray=[]
 //     for(var i=0;i<$scope.dagtable.length;i++){
 //       var stagesjosn={};
 //       var taskarray=[];
 //       stagesjosn.stageId=$scope.dagtable[i].stageId;
 //       if(typeof  $scope.dagtable[i].dependsOn == "undefined"){
 //         stagesjosn.dependsOn=[];
 //       }
 //       else{
 //         stagesjosn.dependsOn=JSON.parse("[" + $scope.dagtable[i].dependsOn + "]");
 //       }
 //       for(var j=0;j<$scope.dagtable[i].task.length;j++){
 //         var taskjson={};
 //         var operatorsarray=[];
 //         var operatorsjosn={};
 //         var operatorinfo={}
 //         var ref={};
 //         taskjson.taskId=$scope.dagtable[i].task[j].taskId;
 //         taskjson.name=$scope.dagtable[i].task[j].name;
 //         if(typeof $scope.dagtable[i].task[j].dependsOn == "undefined"){
 //           taskjson.dependsOn=[];
 //       }
 //       else{
 //         taskjson.dependsOn=JSON.parse("[" + $scope.dagtable[i].task[j].dependsOn + "]");
 //       }
 //       operatorsjosn.dependsOn=[];
 //       if($scope.dagtable[i].task[j].operatorinfo.type.text == "map"){
 //         ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
 //         ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatormap.uuid;
 //       }
 //       else if($scope.dagtable[i].task[j].operatorinfo.type.text == "dq"){
 //         ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
 //         ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatordq.uuid;
 //       }
 //       else if($scope.dagtable[i].task[j].operatorinfo.type.text == "dqgroup"){
 //         ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
 //         ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatordqgroup.uuid;
 //       }
 //       else if($scope.dagtable[i].task[j].operatorinfo.type.text == "load"){
 //         ref.type=$scope.dagtable[i].task[j].operatorinfo.type.text;
 //         ref.uuid=$scope.dagtable[i].task[j].operatorinfo.operatorload.uuid;
 //       }
 //       operatorinfo.ref=ref;
 //       operatorsjosn.operatorInfo=operatorinfo
 //       operatorsarray[0]=operatorsjosn;
 //       taskjson.operators=operatorsarray;
 //       taskarray[j]=taskjson;
 //     }
 //     stagesjosn.tasks=taskarray;
 //     stagesarray[i]=stagesjosn
 //   }
 //   dagJson.stages=stagesarray;
 //   console.log(JSON.stringify(dagJson));
 //   MetadataDagSerivce.submit(dagJson,"dag").then(function(response){onSuccessSubmit(response.data)});
 //   var onSuccessSubmit=function(response){
 //     $scope.dataLoading=false;
 //     $scope.iSSubmitEnable=false;
 //     $scope.changemodelvalue();
 //     if($scope.isshowmodel == "true"){
 //       $('#dagsave').modal({
 //         backdrop: 'static',
 //         keyboard: false
 //      });
 //     }
 //   }
 // }//End SubmitDAG

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
 
 // $scope.addRow=function(){
 //   if($scope.dagtable == null){
 //     $scope.dagtable=[];
 //     MetadataDagSerivce.getAllLatest("map").then(function(response){onSuccessGetAllLatesMap(response.data)});
 //     var onSuccessGetAllLatesMap=function(resposne){
 //       $scope.allMap=resposne;
 //     }
 //   }
 //   var stagejson={};
 //   stagejson.stageId=$scope.dagtable.length;
 //   stagejson.task=[];
 //   $scope.dagtable.splice($scope.dagtable.length, 0,stagejson);
 // }

//   $scope.addSubRow=function(index){
//     if(typeof $scope.dagtable[index].task == "undefined"){
//       $scope.dagtable[index].task=[];
//     }
//     var taskjson={};
//     taskjson.taskId=$scope.dagtable[index].task.length;
//     var operatorinfo={};
//     operatorinfo.isOpetatorMap=true;
//     operatorinfo.isOpetatorLoad=false;
//     operatorinfo.isOpetatorDqGroup=false;
//     operatorinfo.isOpetatorDQ=false;
//     var obj={};
//     obj.text="map";
//     obj.caption="map"
//     operatorinfo.type=obj;
//     taskjson.operatorinfo=operatorinfo
//     $scope.dagtable[index].task.splice($scope.dagtable[index].task.length, 0,taskjson);
//  }

 // $scope.selectAllRow=function(){
 //   angular.forEach($scope.dagtable, function(stage) {
 //     stage.selected = $scope.selectallstages;
 //   });
 // }

 // $scope.removeRow=function(){
 //   var newDataList=[];
 //   $scope.selectallstages=false;
 //   angular.forEach($scope.dagtable, function(selected){
 //     if(!selected.selected){
 //       newDataList.push(selected);
 //     }
 //   });
 //   $scope.dagtable = newDataList;
 // }

 // $scope.selectAllSubRow=function(index){
 //   angular.forEach($scope.dagtable[index].task, function(task) {
 //     task.selected = $scope.dagtable[index].selectalltask;
 //   });
 // }

 // $scope.removeSubRow=function(index){
 //   var newDataList=[];
 //   $scope.dagtable[index].selectalltask=false;
 //   angular.forEach($scope.dagtable[index].task, function(selected){
 //     if(!selected.selected){
 //       newDataList.push(selected);
 //     }
 //   });
 //   $scope.dagtable[index]= newDataList;
 // }


$scope.saveDagJsonData = function(){
 $scope.isshowmodel=true;
 $scope.dataLoading=true;
 $scope.iSSubmitEnable=false;
 $scope.dagHasChanged=true;
 var options={}
 options.execution=$scope.checkboxModelexecution;
 var cells = window.getAllCell();//$scope.graph.getCells();
 var inArrayFormat = graphService.convertGraphToDag(cells);
 var dagJson={};
 dagJson.uuid=$scope.dagdata.uuid
 dagJson.name=$scope.pipelineName//$scope.dagdata.name
 var tagArray=[];
 if($scope.tags !=null){
   for(var counttag=0;counttag<$scope.tags.length;counttag++){
     tagArray[counttag]=$scope.tags[counttag].text;
   }
 }
 dagJson.tags=tagArray
 dagJson.active=$scope.dagdata.active;
 dagJson.published=$scope.dagdata.published;
 dagJson.desc=$scope.dagdata.desc;
 dagJson.stages = inArrayFormat[0].stages;
 dagJson.xPos = inArrayFormat[0].xPos;
 dagJson.yPos = inArrayFormat[0].yPos;
 dagJson.templateFlg = $scope.dagdata.templateFlg
 if($scope.isUseTemplate == true && $scope.selectedDagTemplate !=null){
  
   var templateInfo={};
   var ref={}
   ref.type="dag";
   ref.uuid=$scope.selectedDagTemplate.uuid;
   templateInfo.ref=ref;
   dagJson.templateInfo=templateInfo;
 }
 // else if($scope.dagdata.templateInfo != null){
 //   var templateInfo={};
 //   dagJson.templateFlg = "N"
 //   var ref={}
 //   ref.type="dag";
 //   ref.uuid=$scope.selectedDagTemplate.uuid;
 //   templateInfo.ref=ref;
 //   dagJson.templateInfo=templateInfo;
 // }
 else{
   
   dagJson.templateInfo=null;
 }
 
 console.log("JSON",dagJson);
 MetadataDagSerivce.submit(dagJson,"dag").then(function(response){onSuccessSubmit(response.data)},function(response){onError(response.data)});
 var onSuccessSubmit=function(response){
   $scope.dataLoading=false;
   $scope.changemodelvalue();
   if(options.execution == "YES"){
     MetadataDagSerivce.getOneById(response,"dag").then(function(response){onSuccessGetOneById(response.data)});
     var onSuccessGetOneById=function(result){
       MetadataDagSerivce.excutionDag(result.uuid,result.version).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
         console.log(JSON.stringify(response))
         $scope.saveMessage="Pipeline Saved and Submitted Successfully"
         notify.type='success',
         notify.title= 'Success',
         notify.content=$scope.saveMessage
         $scope.$emit('notify', notify);
         $scope.okWorkflowsave();
       }
     }
   }//End If
   else{
     $scope.saveMessage="Pipeline Saved Successfully"
     notify.type='success',
     notify.title= 'Success',
     notify.content=$scope.saveMessage
     $scope.$emit('notify', notify);
     $scope.okWorkflowsave();
   }//End else
 }//EndSubmit Api
 var onError = function(response) {
   notify.type='error',
   notify.title= 'Error',
   notify.content="Some Error Occurred"
   $scope.$emit('notify', notify);
 }
}

//jitu new code
$scope.okWorkflowsave=function(){
 $('.modal-backdrop').hide();
 setTimeout(function(){  $state.go('listwf');},2000);   
}

$scope.showRule=true;
$scope.showdagForm=true;
$scope.continueCount= 1;
$scope.backCount;
$scope.showRuleGraph=function(uuid,version){
 $scope.showRule=false;
 $scope.showRuleForm=false
 $scope.graphDataStatus=true
 $scope.showgraphdiv=true;
}

$scope.showRulePage=function(){
 $scope.showgraph=false;
 $scope.showRule=true;
 $scope.showRuleForm=true
 $scope.graphDataStatus=false;
 $scope.showgraphdiv=false
}



$scope.countContinue=function(){
 $scope.continueCount=$scope.continueCount+1;
 if($scope.continueCount == 2 && $scope.isGraphRenderEdit !=true){
  
 
   setTimeout(function () {
     $scope.isGraphRenderEdit=true;
     $scope.dagdata.name=$scope.pipelineName;
     //console.log('scope dagResp',$scope.dagdata);
     $scope.$broadcast('createGraph',$scope.dagdata);
   },0);
 }
 if( $scope.isGraphRenderEdit==true){
   console.log($scope.graph.getCells());
   //$scope.graph.addCells($scope.graph.getCells())
 }
 if($scope.continueCount >= 3){
   $scope.isSubmitShow=true;
 }else{
   $scope.isSubmitShow=false;
 }

}

$scope.countBack=function(){
 if($scope.graph){
   var cells = $scope.graph.getCells();
   var inArrayFormat = graphService.convertGraphToDag(cells);
   $scope.dagdata.stages = inArrayFormat[0].stages;
   $scope.dagdata.xPos = inArrayFormat[0].xPos;
   $scope.dagdata.yPos = inArrayFormat[0].yPos;
   $scope.graphReady=true;
 }else{
   setTimeout(function () {
     console.log('scope dagResp',$scope.dagdata);
     $scope.$broadcast('createGraph',$scope.dagdata);
   },0);
 }
 $scope.continueCount=$scope.continueCount-1;
 $scope.isSubmitShow=false;
}

// $scope.OnChangeTemplate=function(){
//  if($scope.dagdata.templateFlg == 'N'){
//    $scope.isTemplate=true;
//    $scope.isUseTemplate=false;
//    $scope.allDagTemplate=[];
//    MetadataDagSerivce.getDagTemplates('dag').then(function (response) {onSuccessGetDagTemplates(response.data)});
//    var onSuccessGetDagTemplates=function(response){
//      console.log(response)
//      for(var i=0;i<response.length;i++){
//        var dagtemplate={};
//        dagtemplate.version=response[i].version;
//        dagtemplate.uuid=response[i].uuid;
//        dagtemplate.name=response[i].name;
//        $scope.allDagTemplate[i]=dagtemplate
//      }
//    }//End getDagTemplates
//  }
//  else
//    $scope.isTemplate=false;
//   // $scope.isUseTemplate=true;
//    $scope.selectedDagTemplate=null;
//    $scope.allDagTemplate=[];
   
// }
$scope.selectTemplateDag=function(){
 console.log($scope.selectedDagTemplate)
 if($scope.selectedDagTemplate !=null){
   $scope.isUseTemplate=true;
   MetadataDagSerivce.getOneByUuidAndVersion($scope.selectedDagTemplate.uuid,$scope.selectedDagTemplate.version,'dag').then(function (response) {onSuccess(response.data)});
   var onSuccess=function(response){
     $scope.dagdata=response.dagdata;
     $scope.dagdata.uuid="";
     //console.log($scope.pipelineName)
     if(typeof $scope.pipelineName == "undefined"){
       $scope.pipelineName=response.dagdata.name;
     }
     $scope.isGraphRenderEdit=false;
     $scope.dagdata.name="";
     $scope.dagdata.desc='';
     $scope.dagdata.version='';
     $scope.dagdata.tag=[];
     $scope.dagdata.templateFlg='N';
     $scope.dagdata.action='Y';
   }
 }else{
   $scope.isUseTemplate=false;
   $scope.dagdata.stages=null;
 }

}
//  buttonConfig.toggleEvent=$scope.OnChangeTemplate();
$scope.sbumitDag=function(){
 $scope.isshowmodel=true;
 $scope.dataLoading=true;
 $scope.iSSubmitEnable=false;
 $scope.dagHasChanged=true;
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
 console.log(JSON.stringify(dagJson));
}
$scope.fullscreen = false;

$scope.requestFullscreen = function() {
 if($scope.fullscreen){
   $("#form_wizard_1").removeAttr('style');
 }
 else {
   $("#form_wizard_1").css(
     {
       "z-index": "999",
       "width": "100%",
       "height": "100%",
       "position": "fixed",
       "overflow" : "scroll",
       "top": "0",
       "left": "0",
     }
   );
 }
 $scope.fullscreen = !$scope.fullscreen;
};

}//End Controller
]);
