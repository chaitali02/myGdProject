/**
 *
 */
AdminModule=angular.module('AdminModule');

AdminModule.factory('MetadataDatastoreFactory',function($http,$location){
    var factory={}
    factory.findAllLatest=function(type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getAllLatest?action=view&type="+type,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }
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
      factory.submit=function(data,type,upd_tag){
     	  var url=$location.absUrl().split("app")[0]
     	  return $http({
               url:url+"common/submit?action=edit&type="+type+"&upd_tag="+upd_tag,
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

AdminModule.service('MetadataDatastoreSerivce',function($q,sortFactory,MetadataDatastoreFactory){
	this.getAllLatest=function(type){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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
	   };
	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getOneById=function(id,type){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findOneById(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getOneByUuidAndVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.submit=function(data,type,upd_tag){
		   var deferred=$q.defer();
		   MetadataDatastoreFactory.submit(data,type,upd_tag).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
