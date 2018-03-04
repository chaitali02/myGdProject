
AdminModule= angular.module('AdminModule');
AdminModule.factory("MigrationAssistFactory",function($http,$location){
  var url=$location.absUrl().split("app")[0]
  var factory={};
  factory.findAll=function(){
       	var url=$location.absUrl().split("app")[0]
           return $http({
   			    method: 'GET',
   			    url:url+"common/getAll?type=meta",
   			    }).
   			    then(function (response,status,headers) {
   		           return response;
   		        })
       }

  	return factory;
});
AdminModule.service("MigrationAssistServices",function($q,MigrationAssistFactory,CommonFactory){
  this.getAll=function(){
        var deferred = $q.defer();
        MigrationAssistFactory.findAll().then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
        var OnSuccess=function(response){
          deferred.resolve({
            data:response
          });
        }
        var onError=function(response){
        deferred.reject({
          data:response
        })
      }
        return deferred.promise;
      }/*End getRegistryByDatasource*/

      this.validateDependancy=function(data,filename){
    	     var deferred = $q.defer();
    	     var url="admin/import/validate?type=import&action=edit&fileName="+filename
    	     CommonFactory.httpPost(url,data).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
    	      var onSuccess=function(response){
    	        deferred.resolve({
    	          data:response
    	        });
    	      }
    	      var onError=function(response){
    	        deferred.reject({
    	         data:response
    	        })
    	     }
    	       return deferred.promise;
    	   }
         this.importSubmit=function(data,type,filename){
       	     var deferred = $q.defer();
       	     var url="admin/import/submit?action=add"+"&type="+type+"&fileName="+filename;
       	     CommonFactory.httpPost(url,data).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
       	      var onSuccess=function(response){
       	        deferred.resolve({
       	          data:response
       	        });
       	      }
       	      var onError=function(response){
       	        deferred.reject({
       	         data:response
       	        })
       	     }
       	       return deferred.promise;
       	   }
           this.exportSubmit=function(data,type){
         	     var deferred = $q.defer();
         	     var url="admin/export/submit?action=add"+"&type="+type;
         	     CommonFactory.httpPost(url,data).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
         	      var onSuccess=function(response){
         	        deferred.resolve({
         	          data:response
         	        });
         	      }
         	      var onError=function(response){
         	        deferred.reject({
         	         data:response
         	        })
         	     }
         	       return deferred.promise;
         	   }
  this.submit=function(data,type){
	     var deferred = $q.defer();
	     var url="common/submit?action=add"+"&type="+type;
	     CommonFactory.httpPost(url,data).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
	      var onSuccess=function(response){
	        deferred.resolve({
	          data:response
	        });
	      }
	      var onError=function(response){
	        deferred.reject({
	         data:response
	        })
	     }
	       return deferred.promise;
	   }

});
