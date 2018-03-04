/**
 *
 */

AdminModule=angular.module('AdminModule');

AdminModule.factory('AdminActivityFactory',function($http,$location){
	var factory={};
	 factory.findLatestByUuid=function(uuid,type){
	    	var url=$location.absUrl().split("app")[0]
	  	    return $http({
	  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
	  		      	method: "GET",
	           }).then(function(response){ return  response})



	   }
	   factory.findAllVersionByUuid=function(uuid,type){
	    	var url=$location.absUrl().split("app")[0]
	  	    return $http({
	  		        url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
	  		      	method: "GET",
	           }).then(function(response){ return  response})


	   }
	   factory.findAll=function(type){
	    	var url=$location.absUrl().split("app")[0]
	  	    return $http({
	  		        url:url+"common/getAll?action=view&type="+type,
	  		      	method: "GET",
	           }).then(function(response){ return  response})


	   }
	   factory.findByOneUuidandVersion=function(uuid,version,type){
		   var url=$location.absUrl().split("app")[0]
			return $http({
				url:url+"metadata/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
				method: "GET"
			   }).then(function(response){ return  response})
		  }
	   factory.getGraphData=function(uuid,version,degree){
		   var url=$location.absUrl().split("app")[0]
		   return $http({
		                url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
		                method: "GET"

		          }).then(function(response){ return  response})
	   };
	return factory;

});

AdminModule.service('AdminActivityService',function($q,AdminActivityFactory,sortFactory){

	this.getLatestByUuid=function(uuid,type){
		 var deferred = $q.defer();
		 AdminActivityFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	this.getGraphData=function(uuid,version,degree){
		 var deferred = $q.defer();
		 AdminActivityFactory.getGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	this.getByOneUuidandVersion=function(uuid,version,type){
		 var deferred = $q.defer();
		 AdminActivityFactory.findByOneUuidandVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	this.getAllVersionByUuid=function(uuid,type){
		 var deferred = $q.defer();
		 AdminActivityFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	 this.getAll=function(type) {
	     var deferred = $q.defer();
	     AdminActivityFactory.findAll(type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){

	     var data={};
	     data.options=[];
	     var defaultoption={};

	     response.sort(sortFactory.sortByProperty("name"));
	     defaultoption.name=response[0].name;
	     defaultoption.uuid=response[0].uuid;
	     data.defaultoption=defaultoption;
	     for(var i=0;i<response.length;i++){
		      var datajosn={}
		      datajosn.name=response[i].name;
		      datajosn.uuid=response[i].uuid;
		      data.options[i]=datajosn
	     }
	      deferred.resolve({
	                  data:data
	              })
	     }
	 return deferred.promise;
	 }

});
