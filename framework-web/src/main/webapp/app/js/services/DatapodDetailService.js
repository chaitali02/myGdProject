/**
 *
 */

var DatapodModule= angular.module('DatapodModule');
DatapodModule.factory('datapodDetailFactory',function($http,$location){
    var factory={}
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		      	method: "GET",
           }).then(function(response){ return response})
    }
    //factory.findDatapodResults=function(uuid,version,offset,limit,sortBy,order,requestId){
    factory.findDatapodResults=function(url){
    	var baseurl=$location.absUrl().split("app")[0]+url

  	    return $http({
  		       // url:url+"datapod?action=view&dataStoreUUID="+uuid+"&dataStoreVersion="+version+"&offset="+offset+"&limit="+limit+"&sortBy="+sortBy+"&order="+order+"&requestId="+requestId,
  		        url:baseurl,
  	    	  method: "GET",
           }).then(function(response){ return response})
    }
    return factory;
});

DatapodModule.service('datapodDetailSerivce',function($q,sortFactory,datapodDetailFactory){
	this.getLatestByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   datapodDetailFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   }
	this.getDatapodResults=function(uuid,version,offset,limit,sortBy,order,requestId){
		   var deferred=$q.defer();
		   var url
		   if(sortBy ==null && order ==null){
		    url="datapod?action=view&dataStoreUUID="+uuid+"&dataStoreVersion="+version+"&requestId="+requestId+"&offset="+offset+"&limit="+limit
		   }
		   else{
			   url="datapod?action=view&dataStoreUUID="+uuid+"&dataStoreVersion="+version+"&requestId="+requestId+"&offset="+offset+"&limit="+limit+"&sortBy="+sortBy+"&order="+order;

		   }
		   //datapodDetailFactory.findDatapodResults(uuid,version,offset,limit,sortBy,order,requestId).then(function(response){onSuccess(response.data)});/*,function(response){onError(response.data)}*/
		   datapodDetailFactory.findDatapodResults(url).then(function(response){onSuccess(response.data)});/*,function(response){onError(response.data)}*/
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		 /* var onError=function(response){
			  deferred.reject({
				  data:response
			  })
		  } */
		  return deferred.promise;
	   }
});
