/**
 *
 */
var InferyxApp = angular.module("InferyxApp");

InferyxApp.service('LhsService', function ($rootScope,$http,$q,$location) {

	this.getMetadata=function(username,sessionid){
		//var url=$location.absUrl().split("app")[0]
		var url= $rootScope.baseUrl
		   return $http({
		                url:url+"common/getAll?type=meta",
		                method: "get",
		                headers: {
					    	   'sessionId': sessionid
					    	 },
		          }).then(function(response){ return  response})
	   };
    this.getMetaStats=function(username,sessionid){
		//var url=$location.absUrl().split("app")[0]
		var url= $rootScope.baseUrl
		   return $http({
		                url:url+"common/getMetaStats",
		                method: "get",
		                headers: {
					    	   'sessionId': sessionid
					    	 },
		          }).then(function(response){ return  response})
	   };
	   this.getDashboard=function(username,sessionid){
		   //var url=$location.absUrl().split("app")[0]
		   var url= $rootScope.baseUrl
		   return $http({
		                url:url+"common/getAll?type=dashboard",
		                method: "get",
		                headers: {
					    	   'sessionId': sessionid
					    	 },
		          }).then(function(response){ return  response})
	   };
	this.getDatapoddata=function(username,sessionid){
		//var url=$location.absUrl().split("app")[0]
		var url= $rootScope.baseUrl
		 return $http({
		            url:url+"common/getAllLatest?type=datapod",
		            method: "get",
		            headers:{
					    	   'sessionId': sessionid
					},
		          }).then(function(response){ return  response})
	   };
	this.getRelationdata=function(username,sessionid){
		 //var url=$location.absUrl().split("app")[0]
		var url= $rootScope.baseUrl
	      return $http({
			       url:url+"common/getAllLatest?type=relation",
			       method: "get",
			       headers:{
						   'sessionId': sessionid
				   },
			       }).then(function(response){ return  response})
		   };

});



InferyxApp.service('AppRoleFactory', function ($http,$q,$location, $rootScope) {

    this.getUserApp=function(username){
    	var url= $rootScope.baseUrl
	   return $http({
	                url:url+"metadata/getAppByUser?userName="+username,
	                method: "GET",
	          }).then(function(response){ return  response})
   };
   this.getUserRole=function(username,sessionid){
	   var url= $rootScope.baseUrl
	   return $http({
	                url:url+"metadata/getRoleByUser?userName="+username,
	                method: "GET",
	          }).then(function(response){ return  response})
   };
	 this.getAppRole=function(username,sessionid){
		var url= $rootScope.baseUrl
		return $http({
								 url:url+"security/getAppRole?userName="+username,
								 method: "GET",
					 }).then(function(response){ return  response})
	 };
   this.findLatestByUuid=function(uuid,type){
	   var url= $rootScope.baseUrl
 	    return $http({
 		        url:url+"common/getLatestByUuid?uuid="+uuid+"&type="+type+"&action=view",
 		      	method: "GET",
          }).then(function(response){ return  response})
   }
   this.setSecurityAppRole=function(appuuid,roleuuid){

	   var url= $rootScope.baseUrl
	   return $http({
	                url:url+"security/setAppRole?appUUID="+appuuid+"&roleUUID="+roleuuid,
	                method: "GET",
	          }).then(function(response){ return  response})
   };
	 this.findTZ=function() {
				var url=$location.absUrl().split("app")[0]

				return $http({
								 method: 'GET',
								 url:url+"system/getTZ",
								 headers: {
									'Accept':'*/*',
									'content-Type' : "application/json",
									 },
									}).
								 then(function (response,status,headers) {

										 return response;
								 })
	 }
});


InferyxApp.service('AppRoleService', function ($http,$q,$location, $rootScope,AppRoleFactory) {
	this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   AppRoleFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){

			   deferred.resolve({
                   data:response
               })

	     }
	  return deferred.promise;



		};
	 this.getUserApp=function(username,sessionid){
		  var deferred = $q.defer();
		  AppRoleFactory.getUserApp(username).then(function(response){OnSuccess(response.data)});
    	  var OnSuccess=function(response){
    		  //alert(JSON.stringify(response))
    		  var data={};
    	        data.options=[];
    	        var defaultoption={};
    	        if(response.length >0){
    	        //response.sort(sortFactory.sortByProperty("name"));
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


	this.getUserRole=function(username,sessionid){
		var deferred = $q.defer();
		  AppRoleFactory.getUserRole(username).then(function(response){OnSuccess(response.data)});
  	      var OnSuccess=function(response){
  		   var data={};
  	        data.options=[];
  	        var defaultoption={};
  	        if(response.length >0){
  	        //response.sort(sortFactory.sortByProperty("name"));
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
		 this.getAppRole=function(username,sessionid){
			var deferred = $q.defer();
			AppRoleFactory.getAppRole(username,sessionid).then(function(response){OnSuccess(response.data)});
				 var OnSuccess=function(response){

					 deferred.resolve({
											data:response
									})
				 }
				 return deferred.promise;
			};
			this.getTZ = function() {
				var deferred = $q.defer();
				AppRoleFactory.findTZ().then(function(response){onSuccessgetTZ(response)});
				var onSuccessgetTZ = function(response) {
				deferred.resolve({
					data: response.data
				});
			}
			return deferred.promise;
		}
	   this.setSecurityAppRole=function(appuuid,roleuuid){
		   var deferred = $q.defer();
		   AppRoleFactory.setSecurityAppRole(appuuid,roleuuid).then(function(response){OnSuccess(response.data)});
	    	  var OnSuccess=function(response){

	    		  deferred.resolve({
	                     data:response
	                 })
	    	  }
	    	  return deferred.promise;
		   };
  /* this.setSecurityAppRole=function(appuuid,roleuuid,sessionid){
	 // var url=$location.absUrl().split("app")[0]
	   var url= $rootScope.baseUrl
	   return $http({
	                url:url+"security/setAppRole?appUUID="+appuuid+"&roleUUID="+roleuuid,
	                method: "GET",
	                headers: {
					    	   'sessionId': sessionid
					    	 },
	          }).then(function(response){ return  response})
   };*/
});


InferyxApp.service('UnlockService', function ($rootScope,$http,$location) {

    this.getUnlockApp=function(username,password,sessionid){
    	//var url=$location.absUrl().split("app")[0]
    	var url= $rootScope.baseUrl
	   return $http({
	                url :url+"security/unlock?username="+username+"&password="+password,
	                method: "GET",
	                 headers: {
				    	   'sessionId': sessionid
				    	 },
	          }).then(function(response){ return  response})
   };



});
InferyxApp.service('cacheService', function ($rootScope,$http,$location) {

    this.searchCriteria = {};
		this.ruledetails = {};

		this.getCache = function (cacheName, type) {
			return this[cacheName][type] || {}
		}
		this.saveCache = function (cacheName, type, obj) {
				 this[cacheName][type] = obj;
		}

});


InferyxApp.service('LogoutService', function ($http,$location,$rootScope) {

	 this.securitylogoutSession=function(sessionid){
			//var url=$location.absUrl().split("app")[0]
		 var url= $rootScope.baseUrl
		   return $http({
		                url:url+"security/logoutSession",
		                method: "GET",
		                headers: {
					    	   'sessionId': sessionid
					    	 },
		          }).then(function(response){

		        	  return  response})
	   };



});
InferyxApp.factory('sortFactory', function () {
	return{
	  sortByProperty : function (property) {
          return function (x, y) {
             return ((x[property] === y[property]) ? 0 : ((x[property] > y[property]) ? 1 : -1));
         }
      }
   }
});


