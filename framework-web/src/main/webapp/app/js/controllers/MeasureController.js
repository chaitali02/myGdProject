MetadataModule= angular.module('MetadataModule');

MetadataModule.controller('MetadataMeasureController',function($scope,$stateParams,$cookieStore,MetadataSerivce,ajaxCallFactory){

	$scope.mode=$stateParams.mode
	$scope.measuredata;
	$scope.showmeasure=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false
	$scope.measure={};
	//$scope.tags=null;
//	$scope.type=["datapod","relation"]
	$scope.measure.versions=[];
	$scope.showMeasureGraph=function(uuid,version){
		$scope.showmeasure=false;
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

	$scope.showMeasurePage=function(){
		$scope.showmeasure=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}
	$scope.convertUppdercase=function(value){
		  var resultvalue =value.split("_");
	      var resultjoint = [];
	      for (j = 0; j < resultvalue.length; j++) {
	        resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
	      }
	      return  resultjoint.toString().replace(/,/g, " ");
	 }

	$scope.allVersion=JSON.parse(ajaxCallFactory.getCall("/common/getAllVersionByUuid?uuid="+$stateParams.id+"&type=measure",$cookieStore.get('userdetail').sessionId));
for(var i=0;i< $scope.allVersion.length;i++){
	var measureversion={};
	measureversion.version=$scope.allVersion[i].version;
	$scope.measure.versions[i]=measureversion;
 }
MetadataSerivce.getByUuid($stateParams.id,'measure',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});
var onSuccess=function(response){
	  var defaultversion={};
	  defaultversion.version=response.version;
	  defaultversion.uuid=response.uuid;
	  $scope.measure.defaultVersion=defaultversion;
    $scope.measuredata=response
	  $scope.measureName=$scope.convertUppdercase($scope.measuredata.name)
	  MetadataSerivce.getAllLatest("datapod").then(function(response){onSuccessMeasure(response.data)});
    var onSuccessMeasure=function(response){
  	 var defaultoption={}
  	  $scope.defaultmeasureInfo={}
       $scope.measureRelation=response
       MetadataSerivce.getAllAttributeBySource($scope.measuredata.measureInfo.ref.uuid,$scope.measuredata.measureInfo.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
	     var onSuccessAttributeBySource=function(response){
	    	 $scope.measureDatapod=response

	     }

       defaultoption.uuid=$scope.measuredata.measureInfo.ref.uuid;
       defaultoption.name=$scope.measuredata.measureInfo.ref.name;
       $scope.defaultmeasureInfo.uuid=$scope.measuredata.measureInfo.ref.uuid;
       $scope.defaultmeasureInfo.name=$scope.measuredata.measureInfo.ref.name;
       $scope.defaultmeasureInfo.attributeId=$scope.measuredata.measureInfo.attributeId;
       $scope.measureRelation.defaultoption=defaultoption
    }

 }

$scope.OnChangeDependsOn=function(){
	  MetadataSerivce.getAllAttributeBySource($scope.measureRelation.defaultoption.uuid,"datapod").then(function(response){onSuccessAttributeBySource(response.data)});
	     var onSuccessAttributeBySource=function(response){
	    	 $scope.measureDatapod=response

	     }

}
$scope.selectVersion=function(){
	  MetadataSerivce.getByOneUuidandVersion($scope.measure.defaultVersion.uuid,$scope.expression.defaultVersion.version,'measure',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});
	  var onSuccess=function(response){
		var defaultversion={};
	  	$scope.measuredata=response
	  	defaultversion.version=response.version;
	  	defaultversion.uuid=response.uuid;
	    $scope.measure.defaultVersion=defaultversion;
	  //	alert(JSON.stringify($scope.expression))
		$scope.measureName=$scope.convertUppdercase($scope.measuredata.name)
		var tags=[];
	  	for(var i=0;i<response.tags.length;i++){
	  		var tag={};
	  		tag.text=response.tags[i];
	  		tags[i]=tag
	  		$scope.tags=tags;
	  	}


	  }
	 // alert($scope.measure.defaultVersion.uuid)
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

		MetadataSerivce.getGraphData(data.uuid,data.version,"-1",$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccess(response.data)});


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
})
//end of measure
