

MetadataModule= angular.module('MetadataModule');
MetadataModule.controller('MetadataConditionController',function($scope,sortFactory,$stateParams,$cookieStore,MetadataSerivce,ajaxCallFactory){

	$scope.mode=$stateParams.mode
	$scope.datasetCompare=null;
	$scope.conditiondata=null;
	$scope.showcondition=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.logicalOperator = ["OR", "AND"];
	$scope.relation = ["relation", "dataset","datapod"];
	$scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
	$scope.condition={};
	$scope.condition.versions=[];
	$scope.showConditionGraph=function(uuid,version){
		$scope.showcondition=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
		var newUuid=uuid+"_"+version;


		MetadataSerivce.getGraphData(newUuid,version,"1",$cookieStore.get('userdetail').sessionId)
	    .then(function (result) {
	    	 $scope.graphDataStatus=false;
	    	 $scope.showgraph=true;
	    	 console.log(JSON.stringify(result.data))
	       	  $scope.data=result.data;
	    	  $scope.graphdata=result.data;
	    });
	}
	if(typeof $stateParams.id != "undefined"){
	$scope.allVersion=JSON.parse(ajaxCallFactory.getCall("/common/getAllVersionByUuid?uuid="+$stateParams.id+"&type=condition",$cookieStore.get('userdetail').sessionId));
	       for(var i=0;i< $scope.allVersion.length;i++){
	    	var conditionversion={};
	    	conditionversion.version=$scope.allVersion[i].version;
	    	$scope.condition.versions[i]=conditionversion;
	}
	}
	$scope.dependsOndd = function(){
		MetadataSerivce.getAllLatest($scope.selectRelation).then(function(response){onSuccessRelation(response.data)});
	      var onSuccessRelation=function(response){
			$scope.conditiontRelation=response
			MetadataSerivce.getAllAttributeBySource($scope.conditiontRelation.defaultoption.uuid,$scope.selectRelation).then(function(response){onSuccessAttributeBySource(response.data)});
		     var onSuccessAttributeBySource=function(response){
		    	 $scope.conditionDatapod=response

		     }
		  }
    }


	$scope.changeRelation = function(){
		MetadataSerivce.getAllAttributeBySource($scope.conditiontRelation.defaultoption.uuid,$scope.selectRelation).then(function(response){onSuccessAttributeBySource(response.data)});
	     var onSuccessAttributeBySource=function(response){
	    	 $scope.conditionDatapod=response

	     }
	}

	//Start SubmitCondition
	$scope.submitCondition=function(){
		var conditionJson={};
	       conditionJson.uuid=$scope.conditiondata.uuid
	       conditionJson.name=$scope.conditiondata.name

	       var tagArray=[];
	       for(var counttag=0;counttag<$scope.tags.length;counttag++){
	       	tagArray[counttag]=$scope.tags[counttag].text;
	       }
	       conditionJson.tags=tagArray
	       conditionJson.desc=$scope.conditiondata.desc
	       var dependsOn={};
	       var ref={}
	       //ref.type="relation"
	       ref.type=$scope.selectRelation
	       ref.uuid=$scope.conditiontRelation.defaultoption.uuid
	       dependsOn.ref=ref;
	       conditionJson.dependsOn = dependsOn;
	       conditionJson.active=$scope.conditiondata.active
	       var conditionInfo={}
	       var operand={}
	       operand.type='Datapod';
	       operand.uuid=$scope.conditiondata.dependsOn.ref.uuid
	       conditionInfo.logicalOperator="";
	       conditionInfo.operand=operand;
	       conditionJson.conditionInfo=conditionInfo
	       var conditionInfoArray=[];
	       for(var i=0;i<$scope.conditiondata.conditionInfo.length;i++){
	    		var conditionInfo={};
	    		var operand=[];
	    		var operandfirst={};
	    		var reffirst={};
	    		var operandsecond={};
	    		var refsecond={};
	    		//alert(JSON.stringify($scope.conditiondata.conditionInfo[i].lhsoperand))
	    		reffirst.type="datapod";
	    		reffirst.uuid=$scope.conditiondata.conditionInfo[i].lhsoperand.uuid;
	    		//alert($scope.conditiondata.conditionInfo[i].operand[0].ref.uuid)
	    		operandfirst.ref=reffirst;
	    		operandfirst.attributeId=$scope.conditiondata.conditionInfo[i].lhsoperand.attributeId;
	    		operand[0]=operandfirst;
	    		//refsecond.type=$scope.conditiondata.conditionInfo[i].operand[1].ref.type;
	    		refsecond.type="simple";
	    		operandsecond.ref=refsecond;
	    		operandsecond.value=$scope.conditiondata.conditionInfo[i].operand[1].value;
	    		operand[1]=operandsecond;
	    		conditionInfo.logicalOperator=$scope.conditiondata.conditionInfo[i].logicalOperator
	    		conditionInfo.operator=$scope.conditiondata.conditionInfo[i].operator
	    		conditionInfo.operand=operand;
	    		conditionInfoArray[i]=conditionInfo
	    		conditionJson.conditionInfo=conditionInfoArray;
	    	}
	       console.log(JSON.stringify(conditionJson))
	      MetadataSerivce.metaSubmit(conditionJson,'condition').success(function(response){onSuccess(response)});
	       var onSuccess=function(response){
	    	   MetadataSerivce.getById(response,'condition',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});
	    	   var onSuccess=function(response){
	    	   $scope.allVersion=JSON.parse(ajaxCallFactory.getCall("/common/getAllVersionByUuid?uuid="+$stateParams.id+"&type=condition",$cookieStore.get('userdetail').sessionId));
	       	     for(var i=0;i< $scope.allVersion.length;i++){
	       	    	var conditionversion={};
	       	    	conditionversion.version=$scope.allVersion[i].version;
	       	     	$scope.condition.versions[i]=conditionversion;
	       	      }

	    	   var defaultversion={};
	    		 var defaultoption={};
	    		 defaultversion.version=response.version;
	    		 defaultversion.uuid=response.uuid;
	    		 $scope.condition.defaultVersion=defaultversion;
	    	     $scope.conditiondata=response
	    	     defaultoption.uuid=response.dependsOn.ref.uuid;
	    	     defaultoption.name=response.dependsOn.ref.name;
	    	     $scope.selectRelation=response.dependsOn.ref.type
	    	     MetadataSerivce.getAllLatest(response.dependsOn.ref.type).then(function(response){onSuccessRelation(response.data)});
	    	     var onSuccessRelation=function(response){
	    	    	 $scope.conditiontRelation=response
	    	    	 $scope.conditiontRelation.defaultoption=defaultoption
	    	    	 MetadataSerivce.getAllAttributeBySource($scope.conditiondata.dependsOn.ref.uuid,$scope.conditiondata.dependsOn.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
	    			     var onSuccessAttributeBySource=function(response){
	    			    	 $scope.conditionDatapod=response
	    			     }
	    	     }
	    	     for(var j=0;j< $scope.conditiondata.conditionInfo.length;j++){
	    	    	 var lhsoperand={};
	    	    	 lhsoperand.uuid= $scope.conditiondata.conditionInfo[j].operand[0].ref.uuid
	    	    	 lhsoperand.datapodname= $scope.conditiondata.conditionInfo[j].operand[0].ref.name;
	    	    	 lhsoperand.name= $scope.conditiondata.conditionInfo[j].operand[0].attributeName;
	    	    	 lhsoperand.attributeId= $scope.conditiondata.conditionInfo[j].operand[0].attributeId;
	    	    	 $scope.conditiondata.conditionInfo[j].lhsoperand=lhsoperand;
	    	     }
	    		 $scope.conditionName=$scope.convertUppdercase($scope.conditiondata.name)
	    		 var tags=[];
	    	      for(var i=0;i<response.tags.length;i++){
	    	  		var tag={};
	    	  		tag.text=response.tags[i];
	    	  		tags[i]=tag
	    	  		$scope.tags=tags;
	    	  	}
	   }//End getById


	  }//End metaSubmit
	}/*End SubmitCondition*/


	$scope.addRow=function(){
		if(conditionInfo !=null){
		  var conditionInfo=[];
		  $scope.conditiondata={}
		  $scope.conditiondata.conditionInfo=conditionInfo;
		}

	$scope.conditiondata.conditionInfo.splice($scope.conditiondata.conditionInfo.length, 0, {});
	}
	$scope.removeRow=function(){
			   //alert(JSON.stringify($scope.conditiondata.conditionInfo))
	 var len=$scope.conditiondata.conditionInfo.length
     $scope.conditiondata.conditionInfo.splice(len-1, 1);
}

	$scope.showConditionPage=function(){
		$scope.showcondition=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}

	$scope.convertUppdercase=function(value){
		 var resultvalue =value.split("_");
	     var resultjoint = [];
	     for (j=0;j<resultvalue.length;j++) {
	       resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
	     }
	     return  resultjoint.toString().replace(/,/g, " ");
	}


	if(typeof $stateParams.id != "undefined"){
	$scope.allVersion=JSON.parse(ajaxCallFactory.getCall("/common/getAllVersionByUuid?uuid="+$stateParams.id+"&type=condition",$cookieStore.get('userdetail').sessionId));
    for(var i=0;i< $scope.allVersion.length;i++){
    	var conditionversion={};
    	conditionversion.version=$scope.allVersion[i].version;
    	$scope.condition.versions[i]=conditionversion;
     }

    MetadataSerivce.getByUuid($stateParams.id,'condition',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});
    var onSuccess=function(response){
	 var defaultversion={};
	 var defaultoption={};
	 defaultversion.version=response.version;
	 defaultversion.uuid=response.uuid;
	 $scope.condition.defaultVersion=defaultversion;
     $scope.conditiondata=response
     defaultoption.uuid=response.dependsOn.ref.uuid;
     defaultoption.name=response.dependsOn.ref.name;
     $scope.selectRelation=response.dependsOn.ref.type
     MetadataSerivce.getAllLatest(response.dependsOn.ref.type).then(function(response){onSuccessRelation(response.data)});
     var onSuccessRelation=function(response){
    	 $scope.conditiontRelation=response
    	 $scope.conditiontRelation.defaultoption=defaultoption
    	 MetadataSerivce.getAllAttributeBySource($scope.conditiondata.dependsOn.ref.uuid,$scope.conditiondata.dependsOn.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
		     var onSuccessAttributeBySource=function(response){
		    	 $scope.conditionDatapod=response
		     }
     }
     for(var j=0;j< $scope.conditiondata.conditionInfo.length;j++){
    	 var lhsoperand={};
    	 lhsoperand.uuid= $scope.conditiondata.conditionInfo[j].operand[0].ref.uuid
    	 lhsoperand.datapodname= $scope.conditiondata.conditionInfo[j].operand[0].ref.name;
    	 lhsoperand.name= $scope.conditiondata.conditionInfo[j].operand[0].attributeName;
    	 lhsoperand.attributeId= $scope.conditiondata.conditionInfo[j].operand[0].attributeId;
    	 $scope.conditiondata.conditionInfo[j].lhsoperand=lhsoperand;
     }
	 $scope.conditionName=$scope.convertUppdercase($scope.conditiondata.name)
	 var tags=[];
      for(var i=0;i<response.tags.length;i++){
  		var tag={};
  		tag.text=response.tags[i];
  		tags[i]=tag
  		$scope.tags=tags;
  	}


   }// End getConditondata
 }

   $scope.selectVersion=function(){
	 MetadataSerivce.getByOneUuidandVersion($scope.condition.defaultVersion.uuid,$scope.condition.defaultVersion.version,'condition',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});
	 var onSuccess=function(response){
		 var defaultversion={};
		 var defaultoption={};
		 defaultversion.version=response.version;
		 defaultversion.uuid=response.uuid;
		 $scope.condition.defaultVersion=defaultversion;
	     $scope.conditiondata=response
	     defaultoption.uuid=response.dependsOn.ref.uuid;
	     defaultoption.name=response.dependsOn.ref.name;
	     $scope.selectRelation=response.dependsOn.ref.type
	     MetadataSerivce.getAllLatest(response.dependsOn.ref.type).then(function(response){onSuccessRelation(response.data)});
	     var onSuccessRelation=function(response){
	    	 $scope.conditiontRelation=response
	    	 $scope.conditiontRelation.defaultoption=defaultoption
	    	 MetadataSerivce.getAllAttributeBySource($scope.conditiondata.dependsOn.ref.uuid,$scope.conditiondata.dependsOn.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
			     var onSuccessAttributeBySource=function(response){
			    	 $scope.conditionDatapod=response
			     }
	     }
	     for(var j=0;j< $scope.conditiondata.conditionInfo.length;j++){
	    	 var lhsoperand={};
	    	 lhsoperand.uuid= $scope.conditiondata.conditionInfo[j].operand[0].ref.uuid
	    	 lhsoperand.datapodname= $scope.conditiondata.conditionInfo[j].operand[0].ref.name;
	    	 lhsoperand.name= $scope.conditiondata.conditionInfo[j].operand[0].attributeName;
	    	 lhsoperand.attributeId= $scope.conditiondata.conditionInfo[j].operand[0].attributeId;
	    	 $scope.conditiondata.conditionInfo[j].lhsoperand=lhsoperand;
	     }
		 $scope.conditionName=$scope.convertUppdercase($scope.conditiondata.name)
		 var tags=[];
	      for(var i=0;i<response.tags.length;i++){
	  		var tag={};
	  		tag.text=response.tags[i];
	  		tags[i]=tag
	  		$scope.tags=tags;
	  	}
	 } //End getByOneUuidandVersion

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
			  alert(JSON.stringify(JSONObject))
			  if(JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc ){

				  return true;
			  }
		  }
		return false;
	}

  $scope.getNodeData=function(data){
		$scope.graphDataStatus=true;
		$scope.showgraph=false;

		MetadataSerivce.getGraphData(data.uuid,data.version,"1",$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});

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
		   /* $scope.$apply(function(){

				 }); */
	  }
	}
});
