/****/
MetadataModule=angular.module('MetadataModule');

MetadataModule.controller('MetadataDashboardController',function($state,$scope,$stateParams,$rootScope,$sessionStorage,$timeout,$filter,MetadataDahsboardSerivce){
	$scope.pageNo = 1;
	$scope.nextPage = function () {
		$scope.pageNo++;
	}
	$scope.prevPage = function () {
		$scope.pageNo--;
	}

	$scope.mode="false";
	$scope.dataLoading=false;
	$scope.isversionEnable=true;
	$scope.isSubmitEnable=true;
	$scope.dashboarddata;
	$scope.showdashboard=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.dashboard={};
	$scope.dashboard.versions=[];
	$scope.isshowmodel=false;
	$scope.dependsOnTypes=["datapod","relation"]
    $scope.sectiontable=null
    $scope.logicalOperator = [" ","OR", "AND"];
    $scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
    $scope.filterTableArray=null;
    $scope.dashboardCompare=null;
    $scope.filterAttributeTags=null;
    $scope.stageName="metadata"
    $scope.stageParams={"type":"dashboard"}
		$scope.isDependencyShow=false;
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

		$scope.sortableOptions = {
    update: function(e, ui) {
			console.log('Sort Updated');
    },
    stop: function(e, ui) {
      // this callback has the changed model
			console.log('Sort Stopped');
			$scope.myform.$dirty = true;
    }
  };

   $scope.showDashboardGraph=function(uuid,version){
		$scope.showdashboard=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
       // var newUuid=uuid+"_"+version;
       /* MetadataDahsboardSerivce.getGraphData(newUuid,version,"1")
	    .then(function (result) {
	    	 $scope.graphDataStatus=false;
	    	 $scope.showgraph=true;
	    	 console.log(JSON.stringify(result.data))
	       	 $scope.data=result.data;
	    	 $scope.graphdata=result.data;
	    });*/
	}//End showDashboardGraph

	$scope.showDashboardPage=function(){
		$scope.showdashboard=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}//End showDashboardPage

/*
	$scope.searchObjet = function(JSONObject,key,value){
	    for(var i=0;i<JSONObject.length;i++){
			if(JSONObject[i][key] == value){
				  return true;
			}
		}
		return false;
	}//End searchObjet

	$scope.searcLinkObjet = function(JSONObject,valuedst,valuesrc){
	    for(var i=0;i<JSONObject.length;i++){
		   if(JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc ){
			 return true;
			}
		}
	 	return false;
	  }//End searcLinkObjet*/

	// Start getNodeData
	/*$scope.getNodeData=function(data){
	 	$scope.graphDataStatus=true;
	 	$scope.showgraph=false;
	 	MetadataDahsboardSerivce.getGraphData(data.uuid,data.version,"1").then(function (response) {onSuccess(response.data)});
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
	 		// $scope.$apply(function(){});
	 	}
	}//End getNodeData
	  */
    $scope.getVizpodByType=function(){
      MetadataDahsboardSerivce.getVizpodByType($scope.alldependsOn.defaultoption.uuid).then(function (response) {onSuccessGetVizpodByType(response.data)});
      var onSuccessGetVizpodByType=function(response){
      	$scope.allVizpodByDependsOn=response;
      }

    }

    $scope.getAllAttributeBySource=function(){
       MetadataDahsboardSerivce.getAllAttributeBySource($scope.alldependsOn.defaultoption.uuid,$scope.selectDependsOnType).then(function(response){onSuccessGetDatapodByRelation(response.data)})
	   var onSuccessGetDatapodByRelation=function(response){
	   	    //console.log(JSON.stringify(response))
		    $scope.sourcedatapodattribute=response;
		    $scope.lhsdatapodattributefilter=response;
		    $scope.allattribute=response;
		}
    }
    $scope.selectType=function(){

      MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) {onSuccessGetAllLatest(response.data)});
      var onSuccessGetAllLatest=function(response){
      	$scope.alldependsOn=response;
      	$scope.filterAttributeTags=null;
      	$scope.getVizpodByType() //Call Function
      	$scope.getAllAttributeBySource()// Call Function
      }

    }//End selectType

    $scope.selectDependsOnOption=function(){
        $scope.filterAttributeTags=null;
    	$scope.getVizpodByType() //Call Function
    	$scope.getAllAttributeBySource()// Call Function
    }

    $scope.loadProfiles = function(query) {
		 return $timeout(function () {
		      return $filter('filter')($scope.allattribute, query);
		    });
		};
    $scope.clear=function(){

	   $scope.profileTags=null;
    }

	if(typeof $stateParams.id != "undefined"){
		$scope.isversionEnable=false;
		$scope.mode=$stateParams.mode;
		$scope.isDependencyShow=true;
		MetadataDahsboardSerivce.getAllVersionByUuid($stateParams.id,"dashboard").then(function(response){onGetAllVersionByUuid(response.data)});
	    var onGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
		    	var dashboardversion={};
		    	dashboardversion.version=response[i].version;
		    	$scope.dashboard.versions[i]=dashboardversion;
		    }
	    }//End onGetAllVersionByUuid

	    MetadataDahsboardSerivce.getLatestByUuidView($stateParams.id,"dashboard").then(function(response){onGetLatestByUuid(response.data)});
	    var onGetLatestByUuid =function(response){
		    $scope.dashboarddata=response.dashboarddata
		    $scope.dashboardCompare=response.dashboarddata;
		    var defaultversion={};
		    defaultversion.version=response.dashboarddata.version;
   	   	    defaultversion.uuid=response.dashboarddata.uuid;
   	        $scope.dashboard.defaultVersion=defaultversion;
   	        $scope.selectDependsOnType=response.dashboarddata.dependsOn.ref.type;
   	        $scope.sectiontable=response.sectionInfo;
   	        $scope.filterAttributeTags=response.filterInfo;
   	        var tags=[];
	        if(response.dashboarddata.tags !=null){
		  		for(var i=0;i<response.dashboarddata.tags.length;i++){
		  			var tag={};
		  			tag.text=response.dashboarddata.tags[i];
		  			tags[i]=tag
		  			$scope.tags=tags;
		  	 	}//End For
	      	}//End If

   	        MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) {onSuccessGetAllLatest(response.data)});
            var onSuccessGetAllLatest=function(response){
      	     	$scope.alldependsOn=response;
      	     	var defaultoption={}
		        defaultoption.uuid=$scope.dashboarddata.dependsOn.ref.uuid
		        defaultoption.name=$scope.dashboarddata.dependsOn.ref.name
		        $scope.alldependsOn.defaultoption=defaultoption;
      	     	$scope.getVizpodByType() //Call Function
      	     	$scope.getAllAttributeBySource()// Call Function
            }//End onSuccessGetAllLatest
		}//End

	}//End IF

	else{


	}//End Else

	$scope.selectVersion=function(){
	 	$scope.myform.$dirty=false;
	  	$scope.alldependsOn=null;
	  	MetadataDahsboardSerivce.getOneByUuidAndVersionView($scope.dashboard.defaultVersion.uuid,$scope.dashboard.defaultVersion.version,'dashboard').then(function (response) {onSuccessGetOneByUuidAndVersion(response.data)});
	  	var onSuccessGetOneByUuidAndVersion=function(response){
        	$scope.dashboarddata=response.dashboarddata
        	$scope.dashboardCompare=response.dashboarddata;
		    var defaultversion={};
		    defaultversion.version=response.dashboarddata.version;
   	   	    defaultversion.uuid=response.dashboarddata.uuid;
   	        $scope.dashboard.defaultVersion=defaultversion;
   	        $scope.selectDependsOnType=response.dashboarddata.dependsOn.ref.type;
   	        $scope.sectiontable=response.sectionInfo
   	        $scope.filterAttributeTags=response.filterInfo;
   	        var tags=[];
	        if(response.dashboarddata.tags !=null){
		  		for(var i=0;i<response.dashboarddata.tags.length;i++){
		  			var tag={};
		  			tag.text=response.dashboarddata.tags[i];
		  			tags[i]=tag
		  			$scope.tags=tags;
		  	 	}//End For
	      	}//End If

   	        MetadataDahsboardSerivce.getAllLatest($scope.selectDependsOnType).then(function (response) {onSuccessGetAllLatest(response.data)});
            var onSuccessGetAllLatest=function(response){
      	     	$scope.alldependsOn=response;
      	     	var defaultoption={}
		        defaultoption.uuid=$scope.dashboarddata.dependsOn.ref.uuid
		        defaultoption.name=$scope.dashboarddata.dependsOn.ref.name
		        $scope.alldependsOn.defaultoption=defaultoption;
      	     	$scope.getVizpodByType() //Call Function
      	     	$scope.getAllAttributeBySource()// Call Function
            }//End onSuccessGetAllLatest
     	}//End onSuccessGetOneByUuidAndVersion
    }//End selectVersion

	$scope.addRow=function(){
		if($scope.sectiontable == null){
			 $scope.sectiontable =[];
		}
	 	var sectionjson={}
	 	var vizpod={}
	 	sectionjson.sectionId=$scope.sectiontable.length;
        sectionjson.vizpod=vizpod;
	 	$scope.sectiontable.splice($scope.sectiontable.length, 0,sectionjson);
 	}

 	$scope.selectAllRow=function(){

	 angular.forEach($scope.sectiontable, function(stage) {
		 stage.selected = $scope.selectallsection;
        });
    }

    $scope.removeRow=function(){
	 	var newDataList=[];
	 	$scope.selectallsection=false;
	 	angular.forEach( $scope.sectiontable, function(selected){
	        if(!selected.selected){
	              newDataList.push(selected);
	        }
	    });
	 	$scope.sectiontable = newDataList;
    }

    /*$scope.checkAllFilterRow = function(){
		 if (!$scope.selectedAllFitlerRow){
	            $scope.selectedAllFitlerRow = true;
	     }
		 else {
	          $scope.selectedAllFitlerRow = false;
	     }
		 angular.forEach($scope.filterTableArray, function(filter) {
			 filter.selected = $scope.selectedAllFitlerRow;
	        });
	 }
    $scope.addRowFilter=function(){
    	if($scope.filterTableArray ==null){
    		$scope.filterTableArray=[];
    	}
    	var filertable={};
    	filertable.logicalOperator=$scope.logicalOperator[0];
    	filertable.lhsFilter=$scope.lhsdatapodattributefilter[0]
    	filertable.operator=$scope.operator[0]
      	$scope.filterTableArray.splice($scope.filterTableArray.length, 0,filertable);
    }
    $scope.removeRowFitler=function(){
    	var newDataList=[];
    	$scope.checkAll=false;
    	angular.forEach($scope.filterTableArray, function(selected){
             if(!selected.selected){
                 newDataList.push(selected);
             }
         });

    	if(newDataList.length >0){
	    	newDataList[0].logicalOperator="";
	    	}
    	$scope.filterTableArray =newDataList;
    }*/

	/*Start SubmitDashboard*/
	$scope.submitDashboard=function(){

		$scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.myform.$dirty=false;

		var dashboardjson={}
		/*dashboardjson.srcChg="n";
		if($scope.dashboardCompare == null){

	    	dashboardjson.srcChg="y";
	    	dashboardjson.filterChg="y";
   	    }*/
	    dashboardjson.uuid=$scope.dashboarddata.uuid
	    dashboardjson.name=$scope.dashboarddata.name
	    dashboardjson.desc=$scope.dashboarddata.desc
	    dashboardjson.active=$scope.dashboarddata.active;
	    var tagArray=[];
	    if($scope.tags !=null){
	        for(var counttag=0;counttag<$scope.tags.length;counttag++){
	     	    tagArray[counttag]=$scope.tags[counttag].text;
	        }
	    }

 	    dashboardjson.tags=tagArray;
 	    var dependson={};
 	    var ref={};
 	    ref.uuid=$scope.alldependsOn.defaultoption.uuid;
 	    ref.type=$scope.selectDependsOnType
 	    dependson.ref=ref;
 	    dashboardjson.dependsOn=dependson;
 	   /* if($scope.dashboardCompare != null && $scope.dashboardCompare.dependsOn.ref.uuid !=$scope.alldependsOn.defaultoption.uuid){
        	 dashboardjson.srcChg="y";

         }*/
        /* else if($scope.dashboardCompare != null){

        	 dashboardjson.srcChg="y";

         }*/

        //SectionInfo
 	    var sectionArray=[];
 	    if($scope.sectiontable.length >0){
          for(var i=0;i<$scope.sectiontable.length;i++){
          	var sectionjson={};
          	var vizpodjson={};
          	var ref={};
          	sectionjson.sectionId=$scope.sectiontable[i].sectionId;
          	sectionjson.name=$scope.sectiontable[i].name;
            ref.type="vizpod";
            ref.uuid=$scope.sectiontable[i].vizpod.uuid;
            vizpodjson.ref=ref;
            sectionjson.vizpodInfo=vizpodjson;
            sectionjson.rowNo=$scope.sectiontable[i].rowNo;
            sectionjson.colNo=$scope.sectiontable[i].colNo;
            sectionArray[i]=sectionjson;
          }
 	    }
 	    dashboardjson.sectionInfo=sectionArray
 	    var filterInfoArray=[];
 	   if($scope.filterAttributeTags !=null ){
        for(var i=0;i<$scope.filterAttributeTags.length;i++){
       	 var filterInfo={}
       	 var ref={};
       	 ref.type="datapod";
       	 ref.uuid=$scope.filterAttributeTags[i].uuid;
       	// ref.version=$scope.profileTags[i].version;
       	 filterInfo.ref=ref;
       	 filterInfo.attrId=$scope.filterAttributeTags[i].attributeId
       	 filterInfoArray[i]=filterInfo;
        }
 	   }
        dashboardjson.filterInfo=filterInfoArray;
        //filterInfo
    	/*var filterInfoArray=[];
    	var filter={}
    	if($scope.dashboardCompare != null && $scope.dashboardCompare.filter !=null ){
	    	filter.uuid=$scope.dashboardCompare.filter.uuid;
	    	filter.name=$scope.dashboardCompare.filter.name;
	    	filter.version=$scope.dashboardCompare.filter.version;
	    	filter.createdBy=$scope.dashboardCompare.filter.createdBy;
	    	filter.createdOn=$scope.dashboardCompare.filter.createdOn;
	    	filter.active=$scope.dashboardCompare.filter.active;
	    	filter.tags=$scope.dashboardCompare.filter.tags;
	    	filter.desc=$scope.dashboardCompare.filter.desc;
	    	filter.dependsOn=$scope.dashboardCompare.filter.dependsOn;
    	}//End If
*/
        /*if($scope.filterTableArray !=null){
	    	if($scope.filterTableArray.length >0 ){

	    	for(var i=0;i<$scope.filterTableArray.length;i++){

	    		if($scope.dashboardCompare != null &&  $scope.dashboardCompare.filter !=null && $scope.dashboardCompare.filter.filterInfo.length == $scope.filterTableArray.length){

	    			if($scope.dashboardCompare.filter.filterInfo[i].operand[0].attributeId != $scope.filterTableArray[i].lhsFilter.attributeId
	    				|| $scope.filterTableArray[i].logicalOperator !=$scope.dashboardCompare.filter.filterInfo[i].logicalOperator
	    				|| $scope.filterTableArray[i].filtervalue !=$scope.dashboardCompare.filter.filterInfo[i].operand[1].value
	    				|| $scope.filterTableArray[i].operator !=$scope.dashboardCompare.filter.filterInfo[i].operator){

	    		        dashboardjson.filterChg="y";
	    		         dashboardjson.srcChg="y";

	    			}//End Inner IF
	    			else{
	    				dashboardjson.filterChg="n";
	    			}//End Inner Else

	 	    	}//End If
	 	    	else{

	 	        	dashboardjson.filterChg="y";
	 	        	 dashboardjson.srcChg="y";
	 	    	}//End Else

	    		var filterInfo={};
	    		var operand=[];
	    		var operandfirst={};
	    		var reffirst={};
	    		var operandsecond={};
	    		var refsecond={};
	    		reffirst.type="datapod"
	    		reffirst.uuid=$scope.filterTableArray[i].lhsFilter.uuid
	    		operandfirst.ref=reffirst;
	    		operandfirst.attributeId=$scope.filterTableArray[i].lhsFilter.attributeId
	    	    operand[0]=operandfirst;
	    		refsecond.type="simple";
	    		operandsecond.ref=refsecond;

	    		if(typeof $scope.filterTableArray[i].filtervalue == "undefined"){
	    			operandsecond.value="";
	    		}

	    		else{
	    			operandsecond.value=$scope.filterTableArray[i].filtervalue
	    		}

	    		operand[1]=operandsecond;

	    		if (typeof $scope.filterTableArray[i].logicalOperator == "undefined"){
	    			filterInfo.logicalOperator=""
	    		}
	    		else{
	    			filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
	    		}
	    		filterInfo.operator=$scope.filterTableArray[i].operator
	    		filterInfo.operand=operand;
	    		filterInfoArray[i]=filterInfo;

	    	}//End FilterInfo
	    	filter.filterInfo=filterInfoArray;
	 	    dashboardjson.filter=filter;
	    	}//End Inner If
	    	else{
	    		 dashboardjson.filter=null;
	    		 dashboardjson.filterChg="y";
	    		  dashboardjson.srcChg="y";
	    	}//End Else

        }//End IF*/
 	    console.log(JSON.stringify(dashboardjson));
	    MetadataDahsboardSerivce.submit(dashboardjson,'dashboard').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
        var onSuccess=function(response){
    	   $scope.dataLoading=false;
    	   $scope.iSSubmitEnable=false;
    	   $scope.changemodelvalue();
      	  //  if($scope.isshowmodel == "true"){
					//    		$('#dashboardsave').modal({
  		    //   		backdrop: 'static',
  		    //   		keyboard: false
  	      // 		});
					//    	}
					notify.type='success',
					notify.title= 'Success',
				 notify.content='Dashboard Saved Successfully'
				 $scope.$emit('notify', notify);
				 $scope.okdashboardsave();
        }
				var onError = function(response) {
					 notify.type='error',
					 notify.title= 'Error',
					notify.content="Some Error Occurred"
					$scope.$emit('notify', notify);
				}
	}//End SubmitDashboard

	$scope.changemodelvalue=function(){
		$scope.isshowmodel=sessionStorage.isshowmodel
    };

    $scope.okdashboardsave=function(){
    	$scope.stageName= $sessionStorage.fromStateName;
		$scope.stageParams=$sessionStorage.fromParams;
		delete $sessionStorage.fromParams;
		delete $sessionStorage.fromStateName;
	    $('#dashboardsave').css("display","none");
	    var hidemode="yes";
	    if(hidemode == 'yes'){
	      if($scope.stageName == "metadata"){
		  	setTimeout(function(){  $state.go('metadata',{'type':'dashboard'});},2000);
	      }
	       else if($scope.stageName != "metadata" && typeof $scope.stageParams.id != "undefined"){

	    		 setTimeout(function(){  $state.go($scope.stageName,{'id': $scope.stageParams.id});},2000);
	    	 }
	      else{
	      	setTimeout(function(){  $state.go($scope.stageName);},2000);
	      }
	    }
	}

});
