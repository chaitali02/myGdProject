/**
 *
 */
AdminModule=angular.module('AdminModule');
AdminModule.factory('MetadataApplicationFactory',function($http,$location){
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
    		url:url+"metadata/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
    		method: "GET",

  	   	}).then(function(response){ return  response})
  	  }

    factory.applicationSubmit=function(data){
    	var url=$location.absUrl().split("app")[0]
        return $http({
             url:url+"common/submit?action=edit&type=application",

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

AdminModule.service('MetadataApplicationSerivce',function($q,sortFactory,MetadataApplicationFactory){


	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataApplicationFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };

	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataApplicationFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };

	   this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataApplicationFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   deferred.resolve({
	                  data:response
	              })
	     }
	  return deferred.promise;



		};

	 this.getOneByUuidAndVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataApplicationFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}


	this.submit=function(data,type){
		   var deferred=$q.defer();
		   MetadataApplicationFactory.applicationSubmit(data,type).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
