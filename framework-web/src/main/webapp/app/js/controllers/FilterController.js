MetadataModule= angular.module('MetadataModule');

MetadataModule.controller('MetadataFilterController',function($state,$scope,$stateParams,MetadataFilterSerivce,privilegeSvc){
	$scope.mode="false";
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
	$scope.filterHasChanged=true;
	$scope.isSubmitEnable=true;
	$scope.filterdata;
	$scope.showfilter=true;
	$scope.data=null;
	$scope.showgraph=false
	$scope.showgraphdiv=false
	$scope.graphDataStatus=false;
	$scope.logicalOperator = ["OR", "AND"];
	$scope.relation = ["relation", "dataset","datapod"];
	$scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
	$scope.filter={};
	$scope.filter.versions=[];
	$scope.filterTableArray=null;
  $scope.isshowmodel=false;
	$scope.isDependencyShow=false;
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['filter'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
		$scope.privileges = privilegeSvc.privileges['filter'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
	$scope.showFilterPage=function(){
			$scope.showfilter=true;
			$scope.showgraph=false
			$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}
	$scope.enableEdit=function (uuid,version) {
		$scope.showFilterPage()
		$state.go('metaListfilter', {
			id: uuid,
			version: version,
			mode:'false'
		});
	}
	$scope.showview=function (uuid,version) {
		$scope.showFilterPage()
		$state.go('metaListfilter', {
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

   $scope.filterFormChange=function(){
	 if($scope.mode == "true"){
	 	$scope.filterHasChanged=true;
     }
	 else{
	 	  $scope.filterHasChanged=false;
	 }
    }




	$scope.showFilterGraph=function(uuid,version){
		$scope.showfilter=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;

	}

	$scope.dependsOndd = function(){
		MetadataFilterSerivce.getAllLatest($scope.selectRelation).then(function(response){onSuccessRelation(response.data)});
	    var onSuccessRelation=function(response){
			$scope.filterRelation=response
			MetadataFilterSerivce.getAllAttributeBySource($scope.filterRelation.defaultoption.uuid,$scope.selectRelation).then(function(response){onSuccessAttributeBySource(response.data)});
		    var onSuccessAttributeBySource=function(response){
			    $scope.filterDatapod=response;
			    $scope.lhsdatapodattributefilter=response;
			}
	    }
	};

    $scope.changeRelation = function(){
	    MetadataFilterSerivce.getAllAttributeBySource($scope.filterRelation.defaultoption.uuid,$scope.selectRelation).then(function(response){onSuccessAttributeBySource(response.data)});
	    var onSuccessAttributeBySource=function(response){
			$scope.filterDatapod=response
			$scope.lhsdatapodattributefilter=response;
	    }
	}

    $scope.checkAllFilterRow = function(){
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
    $scope.addRow=function(){
	    if($scope.filterTableArray == null){
	    	  $scope.filterTableArray=[];
	    }
	   	var filertable={};
	   	filertable.lhsFilter=$scope.lhsdatapodattributefilter[0]
	   	filertable.operator=$scope.operator[0]
	    $scope.filterTableArray.splice($scope.filterTableArray.length, 0,filertable);
    }

    $scope.removeRow=function(){
	   	var newDataList=[];
	   	$scope.selectedAllFitlerRow=false;
	   	angular.forEach($scope.filterTableArray, function(selected){
	        if(!selected.selected){
	                newDataList.push(selected);
	        }
	    });
	   	if(newDataList.length >0){
	    	newDataList[0].logicalOperator="";
	    }
    	$scope.filterTableArray = newDataList;
    }

    $scope.convertUppdercase=function(value){
	    var resultvalue =value.split("_");
		var resultjoint = [];
		for (j=0;j<resultvalue.length;j++) {
		    resultjoint[j] = resultvalue[j].charAt(0).toUpperCase() + resultvalue[j].slice(1);
		}
		return  resultjoint.toString().replace(/,/g, " ");
    }

	if(typeof $stateParams.id !="undefined"){
		$scope.showactive="true"
		$scope.mode=$stateParams.mode;

		$scope.isDependencyShow=true;
	    MetadataFilterSerivce.getAllVersionByUuid($stateParams.id,"filter").then(function(response){onSuccessGetAllVersionByUuid(response.data)});
		var onSuccessGetAllVersionByUuid=function(response){
			for(var i=0;i< response.length;i++){
				var filterversion={};
				filterversion.version=response[i].version;
		    	$scope.filter.versions[i]=filterversion;
		     }
		}//End  getAllVersionByUuid

		MetadataFilterSerivce.getOneByUuidandVersion($stateParams.id,$stateParams.version,'filter').then(function (response) {onSuccess(response.data)});
	    var onSuccess=function(response){
	  	 	var defaultversion={};
	  	 	var defaultoption={};
	  	 	defaultversion.version=response.filter.version;
	  	 	defaultversion.uuid=response.filter.uuid;
	  	 	$scope.filter.defaultVersion=defaultversion;
	     	$scope.filterdata=response.filter
	     	defaultoption.uuid=response.filter.dependsOn.ref.uuid;
	     	defaultoption.name=response.filter.dependsOn.ref.name;
	      	$scope.selectRelation=response.filter.dependsOn.ref.type
	      	$scope.filterTableArray=response.filterInfo;
	      	MetadataFilterSerivce.getAllLatest(response.filter.dependsOn.ref.type).then(function(response){onSuccessRelation(response.data)});
	       	var onSuccessRelation=function(response){
	    	   	$scope.filterRelation=response
	    	   	$scope.filterRelation.defaultoption=defaultoption
	    	   	MetadataFilterSerivce.getAllAttributeBySource($scope.filterdata.dependsOn.ref.uuid,$scope.filterdata.dependsOn.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
			    var onSuccessAttributeBySource=function(response){
			    	 $scope.filterDatapod=response
			    	 $scope.lhsdatapodattributefilter=response;
			    }
	        }
	        $scope.selectRelation=response.filter.dependsOn.ref.type
	        for(var j=0;j< $scope.filterdata.filterInfo.length;j++){
	   	     	var lhsoperand={};
	   	     	lhsoperand.uuid= $scope.filterdata.filterInfo[j].operand[0].ref.uuid
	   	     	lhsoperand.datapodname= $scope.filterdata.filterInfo[j].operand[0].ref.name;
	   	     	lhsoperand.name= $scope.filterdata.filterInfo[j].operand[0].attributeName;
	   	     	lhsoperand.attributeId= $scope.filterdata.filterInfo[j].operand[0].attributeId;
	   	     	$scope.filterdata.filterInfo[j].lhsoperand=lhsoperand;
	   	    }
	   	    $scope.filterName=$scope.convertUppdercase($scope.filterdata.name)
	  	    var tags=[];
	        for(var i=0;i<response.filter.tags.length;i++){
	    		var tag={};
	    		tag.text=response.filter.tags[i];
	    		tags[i]=tag
	    		$scope.tags=tags;
	    	}

	     }

	}//End IF
else{
	$scope.showactive="false"
}



	$scope.selectVersion=function(){
		 $scope.myform.$dirty=false;
		MetadataFilterSerivce.getOneByUuidandVersion($scope.filter.defaultVersion.uuid,$scope.filter.defaultVersion.version,'filter').then(function (response) {onSuccess(response.data)});
	 	var onSuccess=function(response){
	 		var defaultversion={};
	  	 	var defaultoption={};
	  	 	defaultversion.version=response.filter.version;
	  	 	defaultversion.uuid=response.filter.uuid;
	  	 	$scope.filter.defaultVersion=defaultversion;
	     	$scope.filterdata=response.filter
	     	defaultoption.uuid=response.filter.dependsOn.ref.uuid;
	     	defaultoption.name=response.filter.dependsOn.ref.name;
	      	$scope.selectRelation=response.filter.dependsOn.ref.type
	      	$scope.filterTableArray=response.filterInfo;
	      	MetadataFilterSerivce.getAllLatest(response.filter.dependsOn.ref.type).then(function(response){onSuccessRelation(response.data)});
	       	var onSuccessRelation=function(response){
	    	   	$scope.filterRelation=response
	    	   	$scope.filterRelation.defaultoption=defaultoption
	    	   	MetadataFilterSerivce.getAllAttributeBySource($scope.filterdata.dependsOn.ref.uuid,$scope.filterdata.dependsOn.ref.type).then(function(response){onSuccessAttributeBySource(response.data)});
			    var onSuccessAttributeBySource=function(response){
			    	 $scope.filterDatapod=response
			    	 $scope.lhsdatapodattributefilter=response;
			    }
	        }
	        $scope.selectRelation=response.filter.dependsOn.ref.type
	        for(var j=0;j< $scope.filterdata.filterInfo.length;j++){
	   	     	var lhsoperand={};
	   	     	lhsoperand.uuid= $scope.filterdata.filterInfo[j].operand[0].ref.uuid
	   	     	lhsoperand.datapodname= $scope.filterdata.filterInfo[j].operand[0].ref.name;
	   	     	lhsoperand.name= $scope.filterdata.filterInfo[j].operand[0].attributeName;
	   	     	lhsoperand.attributeId= $scope.filterdata.filterInfo[j].operand[0].attributeId;
	   	     	$scope.filterdata.filterInfo[j].lhsoperand=lhsoperand;
	   	    }
	   	    $scope.filterName=$scope.convertUppdercase($scope.filterdata.name)
	  	    var tags=[];
	        for(var i=0;i<response.filter.tags.length;i++){
	    		var tag={};
	    		tag.text=response.filter.tags[i];
	    		tags[i]=tag
	    		$scope.tags=tags;
	    	}
	 	}
	}//End selectVersion



	/*Start  code of SubmitFilter*/
  $scope.submitFilter=function(){
        $scope.isshowmodel=true;
		$scope.dataLoading=true;
		$scope.iSSubmitEnable=false;
		$scope.filterHasChanged=true;
		$scope.myform.$dirty=false;
        var filterJson={};
        filterJson.uuid=$scope.filterdata.uuid
        filterJson.name=$scope.filterdata.name
   		var tagArray=[];
			if($scope.tags.length1!=0){
   		for(var counttag=0;counttag<$scope.tags.length;counttag++){
    		tagArray[counttag]=$scope.tags[counttag].text;
   		}
		}
   		filterJson.tags=tagArray
   		filterJson.desc=$scope.filterdata.desc
   		var dependsOn={};
   		var ref={}
   		ref.type=$scope.selectRelation
   		ref.uuid=$scope.filterRelation.defaultoption.uuid
   		dependsOn.ref=ref;
   		filterJson.dependsOn = dependsOn;
  		filterJson.active=$scope.filterdata.active
			filterJson.published=$scope.filterdata.published

   		var filterInfoArray=[];
  		for(var i=0;i<$scope.filterTableArray.length;i++){
  			var filterInfo={};
    		var operand=[];
    		var operandfirst={};
    		var reffirst={};
    		var operandsecond={};
    		var refsecond={};
    		if($scope.selectRelation == "dataset"){

    			reffirst.type="dataset";
    		}
    		else{
    		reffirst.type="datapod"
    		}
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
    		if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
    			filterInfo.logicalOperator=""
    		}
    		else{
    			filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
    		}
    		filterInfo.operator=$scope.filterTableArray[i].operator
    		filterInfo.operand=operand;

    		filterInfoArray[i]=filterInfo;

  		}
  		filterJson.filterInfo=filterInfoArray;
   		console.log(JSON.stringify(filterJson))
        MetadataFilterSerivce.submit(filterJson,'filter').then(function(response){onSuccess(response)},function(response){onError(response.data)});
        var onSuccess=function(response){
        	$scope.dataLoading=false;
   	        $scope.iSSubmitEnable=false;
    	    $scope.changemodelvalue();
    	    // if($scope.isshowmodel == "true"){
      	  //      $('#filtersave').modal({
    		  //      backdrop: 'static',
    		  //      keyboard: false
    	    //    });
      	  //   }//End If
					notify.type='success',
					notify.title= 'Success',
				 notify.content='Filter Saved Successfully'
				 $scope.$emit('notify', notify);
				 $scope.okfiltersave();
	   }
		 var onError = function(response) {
				notify.type='error',
				notify.title= 'Error',
			 notify.content="Some Error Occurred"
			 $scope.$emit('notify', notify);
		 }
 	}/*End SubmitFilter*/

	$scope.changemodelvalue=function(){
			$scope.isshowmodel=sessionStorage.isshowmodel
	};
    $scope.okfiltersave=function(){
   	  $('#filtersave').css("dispaly","none");
   	  var hidemode="yes";
   	  if(hidemode == 'yes'){
   		setTimeout(function(){  $state.go('metadata',{'type':'filter'});},2000);
      }
    }
});
