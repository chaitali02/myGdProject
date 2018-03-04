/**
 *
 */

RuleModule=angular.module('RuleModule');

RuleModule.factory('RuleGroupFactory',function($http,$location){
         var factory={};
    	 factory.findAllLatest=function(metavalue) {
    		 var url=$location.absUrl().split("app")[0]
             return $http({
				    method: 'GET',
				    url:url+"common/getAllLatest?action=view&type="+metavalue,

				    }).
				    then(function (response,status,headers) {
			           return response;
			        })
       }
       factory.findByUuid=function(uuid,type){
        	  var url=$location.absUrl().split("app")[0]
    		  return $http({
    			        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
    			        method: "GET",
    	          }).then(function(response){ return  response})


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
          factory.findByUuid=function(uuid,type){
        	  var url=$location.absUrl().split("app")[0]
    		  return $http({
    			        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
    			        method: "GET",
    	          }).then(function(response){ return  response})


    	  }
          factory.findAllVersionByUuid=function(uuid,type) {
              var url=$location.absUrl().split("app")[0]
              return $http({
                     method: 'GET',
                     url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,

                     }).
                     then(function (response,status,headers) {
                        return response;
                     })
          }
          factory.findOneByUuidAndVersion=function(uuid,version,type) {
              var url=$location.absUrl().split("app")[0]
              return $http({
                     method: 'GET',
                     url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,

                     }).
                     then(function (response,status,headers) {
                        return response;
                     })
          }

         factory.findAllLatest=function(type) {
                var url=$location.absUrl().split("app")[0]
                return $http({
                       method: 'GET',
                       url:url+"common/getAllLatest?action=view&type="+type,

                       }).
                       then(function (response,status,headers) {
                          return response;
                       })
          }
         factory.executeRuleGroup=function(uuid,version) {
             var url=$location.absUrl().split("app")[0]
             return $http({
                    method: 'POST',
                    url:url+"rule/executeGroup?action=execute&uuid="+uuid+"&version="+version,
                    }).
                    then(function (response,status,headers) {
                       return response;
                    })
         }

         factory.ruleSubmit=function(data,type){
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
         factory.findRuleExecByRule=function(uuid) {
             var url=$location.absUrl().split("app")[0]
             return $http({
                      method: 'GET',
                      url:url+"rule/getRuleExecByRule?action=view&ruleUuid="+uuid
                       }).
                      then(function (response,status,headers) {
                          return response;
                      })
        }
         factory.findGraphData=function(uuid,version,degree){
    		  var url=$location.absUrl().split("app")[0]
    		   return $http({
    		                url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
    		                method: "GET"
    		          }).then(function(response){ return  response})
    	  };
    	  factory.findSaveAs=function(uuid,version,type){
        	  var url=$location.absUrl().split("app")[0]
    		  return $http({
    			        url:url+"common/saveAs?action=clone&uuid="+uuid+"&version="+version+"&type="+type,
    			        method: "GET",
    	          }).then(function(response){ return  response})
    	  }
    return factory;
})


RuleModule.service("RuleGroupService",function($q,RuleGroupFactory,sortFactory){

	this.saveAs=function(uuid,version,type){
		 var deferred = $q.defer();
		 RuleGroupFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	 this.getAllLatetsRuleGroup=function(metavalue,sessionid) {
     	var deferred = $q.defer();
     	RuleGroupFactory.findAllLatest(metavalue).then(function(response){onSuccess(response.data)});
         var onSuccess=function(response){

         	var rowDataSet = [];
             var headerColumns=['id','uuid','version','name','createdBy','createdOn']
             for(var i=0;i<response.length;i++){
          	   var rowData = [];

          	   for(var j=0;j<headerColumns.length;j++){
          		   var columnname=headerColumns[j]
          		   if(columnname == "createdBy"){

          			   rowData[j]=response[i].createdBy.ref.name;
          		   }

          		   else{

          			   rowData[j]=response[i][columnname];
          		   }
          	   }
          	   rowDataSet[i]=rowData;

             }

              deferred.resolve({
             	 data:rowDataSet
              })
         }
         return deferred.promise;
     }
	this.getByUuid=function(id,type){
		   var deferred=$q.defer();
		   RuleGroupFactory.findByUuid(id,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
		}

	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   RuleGroupFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	    }
	 this.getAllVersionByUuid=function(uuid,type){
			   var deferred=$q.defer();
			   RuleGroupFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
			     var onSuccess=function(response){

			      deferred.resolve({
			                  data:response
			              })
			     }

			  return deferred.promise;
		    }
	 this.getOneByUuidAndVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   RuleGroupFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
		}
	this.getAllLatest=function(type){
		   var deferred=$q.defer();
		   RuleGroupFactory.findAllLatest(type).then(function(response){onSuccess(response)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	   }
	this.getOneById=function(id,type){
		   var deferred=$q.defer();
		   RuleGroupFactory.findOneById(id,type).then(function(response){onSuccess(response)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	   }


 this.submit=function(data,type){
		   var deferred=$q.defer();
		   RuleGroupFactory.ruleSubmit(data,type).then(function(response){onSuccess(response)},function(response){onError(response.data)});
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
 this.ruleGroupExecute=function(uuid,version){
		   var deferred=$q.defer();
		   RuleGroupFactory.executeRuleGroup(uuid,version).then(function(response){onSuccess(response)});
		     var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	   }
});
