/**
 *
 */
MetadataModule=angular.module('MetadataModule');
MetadataModule.factory('MetadataFunctionFactory',function($http,$location){
    var factory={}
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }
    var factory={}
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }

    factory.findOneByUuidAndVersion=function(uuid,version,type){
    	 var url=$location.absUrl().split("app")[0]
    	 return $http({
    		url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
    		method: "GET",

  	   	}).then(function(response){ return  response})
  	  }

    factory.functionSubmit=function(data){
    	var url=$location.absUrl().split("app")[0]
        return $http({
             url:url+"common/submit?action=edit&type=function",

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

	   factory.findAllVersionByUuid=function(uuid,type){
		     var url=$location.absUrl().split("app")[0]
			  return $http({
				       url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
				       method: "GET"
		          }).then(function(response){ return  response})


		  }


   return factory;
});

MetadataModule.service('MetadataFunctionSerivce',function($q,sortFactory,MetadataFunctionFactory){


	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataFunctionFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };

	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataFunctionFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };

	   this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataFunctionFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   var functionjson={};
			   functionjson.functiondata=response;
			   var paramInfoArray=[];
			   if(response.paramInfo !=null){
				   for(var i=0;i<response.paramInfo.length;i++){
					   var paraminfo={};
					    paraminfo.paramId=response.paramInfo[i].paramId;
					    paraminfo.paramName=response.paramInfo[i].paramName;
					    paraminfo.paramType=response.paramInfo[i].paramType;
					    paraminfo.paramDefVal=response.paramInfo[i].paramDefVal;
					    paraminfo.paramReq=response.paramInfo[i].paramReq;
			 		    paramInfoArray[i]=paraminfo;
				   }
			   }
			   functionjson.paramInfo=paramInfoArray;
			   console.log(JSON.stringify(functionjson.paramInfo));
			   deferred.resolve({
	                  data:functionjson
	              })
	     }
	  return deferred.promise;

	};

    this.getOneByUuidandVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataFunctionFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   var functionjson={};
			   functionjson.functiondata=response;
			   var paramInfoArray=[];
			   if(response.paramInfo !=null){
				   for(var i=0;i<response.paramInfo.length;i++){
					   var paraminfo={};
					    paraminfo.paramId=response.paramInfo[i].paramId;
					    paraminfo.paramName=response.paramInfo[i].paramName;
					    paraminfo.paramType=response.paramInfo[i].paramType;
					    paraminfo.paramDefVal=response.paramInfo[i].paramDefVal;
					    paraminfo.paramReq=response.paramInfo[i].paramReq;
			 		    paramInfoArray[i]=paraminfo;
				   }
			   }
			   functionjson.paramInfo=paramInfoArray;
			   console.log(JSON.stringify(functionjson.paramInfo));
			   deferred.resolve({
	                  data:functionjson
	              })
		     }
		  return deferred.promise;
		}


	this.submit=function(data,type){
		   var deferred=$q.defer();
		   MetadataFunctionFactory.functionSubmit(data,type).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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

});
