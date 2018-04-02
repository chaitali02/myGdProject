/**
 *
 */

AdminModule= angular.module('AdminModule');
AdminModule.factory("RegisterSourceFacoty",function($http,$location){

	var url=$location.absUrl().split("app")[0]
	var factory={};
	factory.findDatasourceByType=function(type){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    method: 'GET',
   			    url:url+"metadata/getDatasourceByType?action=view&type="+type,

   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }
	factory.findDatasourceByApp=function(uuid){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    method: 'GET',
   			    url:url+"metadata/getDatasourceByApp?action=view&type=application&uuid="+uuid,

   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }
	factory.findRegistryByDatasource=function(uuid,status){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    method: 'GET',
   			    url:url+"metadata/getRegistryByDatasource?type=datasource&action=view&datasourceUuid="+uuid+"&status="+status,
   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }
	
/*	factory.findRegisterHiveDB=function(uuid,version){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    method: 'post',
   			    url:url+"metadata/registerHiveDB?action=view&uuid="+uuid+"&version="+version,
   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }*/
	factory.findRegister=function(uuid,version,data,type){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    url:url+"metadata/register?action=edit&uuid="+uuid+"&version="+version+"&type="+type,
   			    headers: {
                 'Accept':'*/*',
                 'content-Type' : "application/json",
                  },
             method:"POST",
             data:JSON.stringify(data),
   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }
	factory.findRegisterHiveDB=function(uuid,version,data){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    url:url+"metadata/registerHiveDB?action=edit&uuid="+uuid+"&version="+version,
   			    headers: {
                 'Accept':'*/*',
                 'content-Type' : "application/json",
                  },
             method:"POST",
             data:JSON.stringify(data),
   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }
	return factory;

});


AdminModule.service("RegisterSourceService",function($q,RegisterSourceFacoty){
	
	this.getDatasourceByApp=function(uuid){
	  	  var deferred = $q.defer();
	  	  RegisterSourceFacoty.findDatasourceByApp(uuid).then(function(response){OnSuccess(response.data)});
	  	  var OnSuccess=function(response){
	  		  deferred.resolve({
				    data:response
		      });
	  	  }
	  	  return deferred.promise;
	  	}/*End getDatasourceByType*/
	this.getDatasourceByType=function(type){
  	  var deferred = $q.defer();
  	  RegisterSourceFacoty.findDatasourceByType(type).then(function(response){OnSuccess(response.data)});
  	  var OnSuccess=function(response){
  		  deferred.resolve({
			    data:response
	      });
  	  }
  	  return deferred.promise;
  	}/*End getDatasourceByType*/
	
	this.getRegister=function(uuid,version,data,type){
	  	  var deferred = $q.defer();

		  	  RegisterSourceFacoty.findRegister(uuid,version,data,type).then(function(response){OnSuccess(response.data)});
		  	  var OnSuccess=function(response){
		  		var result=[]
		  		  for(var i=0;i<response.length;i++){
		  			var resultjson={};
		  			resultjson.id=response[i].id;
		  			resultjson.name=response[i].name;
		  			resultjson.dese=response[i].dese;
					resultjson.registeredOn=response[i].registeredOn;
					resultjson.registeredBy=response[i].registeredBy;
		  			if(response[i].status == "UnRegistered"){

		  				resultjson.status="Not Registered"
		  			}
		  			else{
		  				resultjson.status=response[i].status
		  			}

		  			resultjson.selected="false"
		  				result[i]=resultjson

		  		  }
		  		  deferred.resolve({
					    data:result
			      });
	  	  }

		  	/*if(type == "HIVE"){
		  		 RegisterSourceFacoty.findRegisterHiveDB(uuid,version,data).then(function(response){OnSuccess(response.data)});
			  	  var OnSuccess=function(response){
			  		console.log(JSON.stringify(response))
			  		var result=[]
			  		  for(var i=0;i<response.length;i++){
			  			var resultjson={};
			  			resultjson.id=response[i].id;
			  			resultjson.name=response[i].name;
			  			resultjson.dese=response[i].dese;
			  			resultjson.registeredOn=response[i].registeredOn;
			  			if(response[i].status == "UnRegistered"){

			  				resultjson.status="Not Registered"
			  			}
			  			else{
			  				resultjson.status=response[i].status
			  			}

			  			resultjson.selected="false"
			  				result[i]=resultjson

			  		  }
			  		  deferred.resolve({
						    data:result
				      });
			  		  }
		  	}  */

	  	  return deferred.promise;
	  	}/*End getcreateAndLoad*/

	this.getRegisterHiveDB=function(uuid,version){
	  	  var deferred = $q.defer();
	  	  RegisterSourceFacoty.findRegisterHiveDB(uuid,version).then(function(response){OnSuccess(response.data)});
	  	  var OnSuccess=function(response){
	  		  deferred.resolve({
				    data:response
		      });
	  	  }
	  	  return deferred.promise;
	  	}/*End getDatasourceByType*/
	this.getRegistryByDatasource=function(uuid,status){
	  	  var deferred = $q.defer();
	  	  RegisterSourceFacoty.findRegistryByDatasource(uuid,status).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
	  	  var OnSuccess=function(response){
	  		  //console.log(JSON.stringify(response))
	  		  var result=[]
	  		  for(var i=0;i<response.length;i++){
	  			var resultjson={};
	  			resultjson.id=response[i].id;
	  			resultjson.name=response[i].name;
	  			resultjson.dese=response[i].dese;
				resultjson.registeredOn=response[i].registeredOn;
				resultjson.registeredBy=response[i].registeredBy;
	  			if(response[i].status == "UnRegistered"){

	  				resultjson.status="Not Registered"
	  			}
	  			else{
	  				resultjson.status=response[i].status
	  			}

	  			resultjson.selected="false"
	  				result[i]=resultjson

	  		  }

	  		 /* if(response.length >0){
	  			var rowDataSet = [];
	             var headerColumns=['id','name','desc','registeredOn','status']
	             for(var i=0;i<response.length;i++){
	          	   var rowData = [];

	          	   for(var j=0;j<headerColumns.length;j++){
	          		   var columnname=headerColumns[j]
	          		       if(response[i][columnname] == "null"){
	          			   rowData[j]=" "
	          		       }
	          		       else{
	          		    	 rowData[j]=response[i][columnname];
	          		       }

	          	   }
	          	   rowDataSet[i]=rowData;

	             }

	  		  }*/

	  		  deferred.resolve({
				    data:result
		      });
	  	  }
				var onError=function(response){
			  deferred.reject({
				  data:response
			  })
		  }
	  	  return deferred.promise;
	  	}/*End getRegistryByDatasource*/

});
