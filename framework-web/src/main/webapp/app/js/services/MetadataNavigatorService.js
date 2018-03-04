/**
 *
 */

MetadataNavigatorModule= angular.module('MetadataNavigatorModule');

MetadataNavigatorModule.factory('MetadataNavigatorFactory',function($http,$location){
	var factory={};
	 factory.findMetaStats=function(uuid,type){
	    	var url=$location.absUrl().split("app")[0]
	  	    return $http({
	  		        url:url+"common/getMetaStats",
	  		      	method: "GET",
	           }).then(function(response){ return  response})



	   }

	return factory;

});

MetadataNavigatorModule.service('MetadatanavigatorService',function($q,MetadataNavigatorFactory,sortFactory){

	this.getAllMetaStats=function(){
		 var deferred = $q.defer();
		 MetadataNavigatorFactory.findMetaStats().then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	 var datadiscoveryJson=[
	    		 {type:"datapod",count:"23"}
	    		 ,{type:"dataset",count:"3"},
	    		 {type:"expression","count":"1"},
	    		 {type:"filter","count":"1"},
	    		 {type:"formula","count":"9"},
	    		 {type:"load","count":"12"},
	    		 {type:"map","count":"9"},
	    		 {type:"relation","count":"10"},
	    		 {type:"dashboard","count":"1"},
	    		 {type:"vizpod","count":"7"},
	    		 {type:"profile","count":"21"},
	    		 {type:"profilegroup","count":"2"},
	    		 {type:"dq","count":"180"},
	    		 {type:"dqgroup","count":"21"},
	    		 {type:"rule","count":"9"},
	    		 {type:"algorithm","count":"4"},
	    		 {type:"model","count":"4"},
	    		 {type:"paramlist","count":"4"},
	    		 {type:"paramset","count":"4"},
	    		 {type:"dag","count":"26"},
	    		 {type:"activity","count":"12"},
	    		 {type:"application","count":"2"},
	    		 {type:"datasource","count":"2"},
	    		 {type:"datastore","count":"14"},
	    		 {type:"group","count":"2"},
	    		 {type:"privilege","count":"84"},
	    		 {type:"role","count":"0"},
	    		 {type:"session","count":"9"},
	    		 {type:"user","count":"1"}
	    		 ];

	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }


});
