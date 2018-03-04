/**
 *
 */
MetadataModule=angular.module('MetadataModule');
MetadataModule.factory('MetadataFilterFactory',function($http,$location){
    var factory={};
    factory.findAllLatest=function(type,inputFlag) {
    		 var url=$location.absUrl().split("app")[0]
             return $http({
				    method: 'GET',
				    url:url+"common/getAllLatest?action=view&type="+type+"&inputFlag="+inputFlag,

				    }).
				    then(function (response,status,headers) {
			           return response;
			        })
       }
    factory.findOneById=function(id,type){
    	var url=$location.absUrl().split("app")[0]
        return $http({
			    method: 'GET',
			    url:url+"common/getOneById?action=view&id="+id+"&type="+type,

			    }).
			    then(function (response,status,headers) {
		           return response;
		        })

    }
    factory.findAllVersionByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
        return $http({
			    method: 'GET',
			    url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
			    }).
			    then(function (response,status,headers) {
		           return response;
		        })

    }
    factory.findLatestByUuid=function(uuid,type){
    	  var url=$location.absUrl().split("app")[0]
		  return $http({
			        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
			        method: "GET",
	          }).then(function(response){ return  response})


	  }
    factory.findByUuidandVersion=function(uuid,version,type){
  	  var url=$location.absUrl().split("app")[0]
  	return $http({
		url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
		method: "GET",

	   }).then(function(response){ return  response})

	}
    factory.findDatapodByRelation=function(uuid,type) {
        var url=$location.absUrl().split("app")[0]
        return $http({
               method: 'GET',
               url:url+"metadata/getDatapodByRelation?action=view&relationUuid="+uuid+"&type="+type,
               }).
               then(function (response,status,headers) {
                  return response;
               })
    }
    factory.findAttributesByDatapod=function(uuid,type) {
        var url=$location.absUrl().split("app")[0]
        return $http({
               method: 'GET',
               url:url+"metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,

               }).
               then(function (response,status,headers) {
                  return response;
               })
    }
	factory.findAttributesByDataset=function(uuid,type){
		     var url=$location.absUrl().split("app")[0]
			  return $http({
				        url:url+"metadata/getAttributesByDataset?action=view&uuid="+uuid+"&type="+type,
				        method: "GET",
		          }).then(function(response){ return  response})
		  }
    factory.datasetSubmit=function(data,type){
    	var url=$location.absUrl().split("app")[0]
        return $http({
             url:url+"common/submit?action=edit&type="+type,

               headers: {
                'Accept':'*/*',
                'content-Type' : "application/json",
                 },
            method:"POST",
            data:JSON.stringify(data),
        }).success(function(response){return response})
     }

    factory.findGraphData=function(uuid,version,degree){
    	var url=$location.absUrl().split("app")[0]
		return $http({
		      url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
		      method: "GET"
		    }).then(function(response){ return  response})
	   };
   return factory;
});

MetadataModule.service('MetadataFilterSerivce',function($http,$q,sortFactory,MetadataFilterFactory){
	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataFilterFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getAllAttributeBySource=function(uuid,type){
		 var deferred = $q.defer();
		 if(type == "relation"){
			 MetadataFilterFactory.findDatapodByRelation(uuid,"datapod").then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){
		        	 var attributes=[];
				   	  for(var j=0;j<response.length;j++){
							for(var i=0;i<response[j].attributes.length;i++){
							  var attributedetail={};
							  attributedetail.uuid=response[j].uuid;
							  attributedetail.datapodname=response[j].name;
							  attributedetail.name=response[j].attributes[i].name;
							  attributedetail.dname=response[j].name+"."+response[j].attributes[i].name;
							  attributedetail.attributeId=response[j].attributes[i].attributeId;
							  attributes.push(attributedetail)
							}
				   	  }
				   	  //console.log(JSON.stringify(attributes))
		        	  deferred.resolve({
		                     data:attributes
		                 })
		        }
		 }
		 if(type == "dataset"){
			 MetadataFilterFactory.findAttributesByDataset(uuid,type).then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){


		        		var attributes=[];
					   	  for(var j=0;j<response.length;j++){
								  var attributedetail={};
								  attributedetail.uuid=response[j].ref.uuid;
								  attributedetail.datapodname=response[j].ref.name;
								  attributedetail.name=response[j].attrName;
								  attributedetail.attributeId=response[j].attrId;
								  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
								  attributes.push(attributedetail)
								}

		        	  deferred.resolve({
		                     data:attributes
		                 })
		        }


		 }
		 if(type == "datapod"){
			 MetadataFilterFactory.findAttributesByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){
		        		var attributes=[];
					   for(var j=0;j<response.length;j++){
								  var attributedetail={};
								  attributedetail.uuid=response[j].ref.uuid;
								  attributedetail.datapodname=response[j].ref.name;
								  attributedetail.name=response[j].attrName;
								  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
								  attributedetail.attributeId=response[j].attrId;
								  attributes.push(attributedetail)
								}

		        	  deferred.resolve({
		                     data:attributes
		                 })
		        }

		 }

	        return deferred.promise;
	}
	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataFilterFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	    }

	this.getAllLatest=function(type) {
	     var deferred = $q.defer();
	     MetadataFilterFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	       var data={};
	       data.options=[];
	       var defaultoption={};
	       if(response.length >0){
	         response.sort(sortFactory.sortByProperty("name"));
	         defaultoption.name=response[0].name;
	         defaultoption.uuid=response[0].uuid;
	         defaultoption.version=response[0].version;
	         data.defaultoption=defaultoption;
	         for(var i=0;i<response.length;i++){
		          var datajosn={}
		          datajosn.name=response[i].name;
		          datajosn.uuid=response[i].uuid;
		          datajosn.version=response[i].version;
		          data.options[i]=datajosn
	         }
	       }
	       else{
	         	data=null;

	      }

	      deferred.resolve({
	                  data:data
	              })
	     }
	 return deferred.promise;
	 }

	this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataFilterFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){
		    	 var filterjson={};
		    	 filterjson.filter=response;
				   var filterInfoArray=[];
				   if(response.filterInfo.length >0){
				     for(var k=0;k<response.filterInfo.length;k++){
					  var filterInfo={};
					  var lhsFilter={};
					  lhsFilter.uuid=response.filterInfo[k].operand[0].ref.uuid
					  lhsFilter.datapodname=response.filterInfo[k].operand[0].ref.name
					  lhsFilter.attributeId=response.filterInfo[k].operand[0].attributeId;
					  lhsFilter.name=response.filterInfo[k].operand[0].attributeName;
					  filterInfo.logicalOperator=response.filterInfo[k].logicalOperator
					  filterInfo.lhsFilter=lhsFilter;
					  filterInfo.operator=response.filterInfo[k].operator;
					  filterInfo.filtervalue=response.filterInfo[k].operand[1].value;
					  filterInfoArray.push(filterInfo);
				     }
				  }
				   filterjson.filterInfo=filterInfoArray
				   console.log(JSON.stringify(filterInfoArray));
				    deferred.resolve({
				                  data:filterjson
				              })
		     }

		  return deferred.promise;
		}
	this.submit=function(data,type){
		   var deferred=$q.defer();
		   MetadataFilterFactory.datasetSubmit(data,type).then(function(response){onSuccess(response)},function(response){onError(response.data)});
		     var onSuccess=function(response){


			      deferred.resolve({
			                  data:response
			              })
		     }
         var onError=function(response){
         deferred.reject({
           data:response
         })
         }
		  return deferred.promise;
		}
	this.getOneByUuidandVersion=function(id,version,type){
		 var deferred=$q.defer();
		   MetadataFilterFactory.findByUuidandVersion(id,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   var filterjson={};
		    	 filterjson.filter=response;
				   var filterInfoArray=[];
				   if(response.filterInfo.length >0){
				     for(var k=0;k<response.filterInfo.length;k++){
					  var filterInfo={};
					  var lhsFilter={};
					  lhsFilter.uuid=response.filterInfo[k].operand[0].ref.uuid
					  lhsFilter.datapodname=response.filterInfo[k].operand[0].ref.name
					  lhsFilter.attributeId=response.filterInfo[k].operand[0].attributeId;
					  lhsFilter.name=response.filterInfo[k].operand[0].attributeName;
					  filterInfo.logicalOperator=response.filterInfo[k].logicalOperator
					  filterInfo.lhsFilter=lhsFilter;
					  filterInfo.operator=response.filterInfo[k].operator;
					  filterInfo.filtervalue=response.filterInfo[k].operand[1].value;
					  filterInfoArray.push(filterInfo);
				     }
				  }
				   filterjson.filterInfo=filterInfoArray
				   console.log(JSON.stringify(filterInfoArray));
				    deferred.resolve({
				                  data:filterjson
				              })
		     }

		  return deferred.promise;
	}
	this.getDatasetDataByUuid=function(id,type){
		 var deferred=$q.defer();
		   MetadataFilterFactory.findByUuid(id,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){
		       var datasetviewjson={};
			   datasetviewjson.dataset=response;
			   var filterInfoArray=[];
			   if(response.filter !=null){
			     for(var k=0;k<response.filter.filterInfo.length;k++){
				  var filterInfo={};
				  var lhsFilter={};
				  lhsFilter.uuid=response.filter.filterInfo[k].operand[0].ref.uuid
				  lhsFilter.datapodname=response.filter.filterInfo[k].operand[0].ref.name
				  lhsFilter.attributeId=response.filter.filterInfo[k].operand[0].attributeId;
				  lhsFilter.name=response.filter.filterInfo[k].operand[0].attributeName;
				  filterInfo.logicalOperator=response.filter.filterInfo[k].logicalOperator
				  filterInfo.lhsFilter=lhsFilter;
				  filterInfo.operator=response.filter.filterInfo[k].operator;
				  filterInfo.filtervalue=response.filter.filterInfo[k].operand[1].value;
				  filterInfoArray.push(filterInfo);
			  }
			  }
			  datasetviewjson.filterInfo=filterInfoArray

		      deferred.resolve({
		                  data:datasetviewjson
		              })
		     }

		  return deferred.promise;
	}

	this.getOneById=function(id,type){
		 var deferred=$q.defer();
		   MetadataFilterFactory.findOneById(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	}

});
