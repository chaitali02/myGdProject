/**
 *
 */
AdminModule = angular.module('AdminModule');

AdminModule.factory('AppConfigFactory',function($http,$location){
    var factory={};
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
    factory.findLatestByUuid=function(uuid,type){
      var url=$location.absUrl().split("app")[0]
        return $http({
              url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
              method: "GET",
           }).then(function(response){ return  response})


   }
    factory.findOneByUuidandVersion=function(uuid,version,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
        method: "GET",

        }).then(function(response){ return  response})
    }

    factory.submit=function(data,type,upd_tag){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/submit?action=edit&type="+type +"&upd_tag="+upd_tag,
          headers: {
            'Accept':'*/*',
            'content-Type' : "application/json",
          },
          method:"POST",
          data:JSON.stringify(data),
          }).success(function(response){return response})
    }
    
    factory.findOneById=function(id,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getOneById?action=view&id="+id+"&type="+type,
        method: "GET"
        }).then(function(response){ return  response})
    }
    factory.findAllVersionByUuid=function(uuid,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
        method: "GET"
        }).then(function(response){ return  response})
    }

  return factory;
})

AdminModule.service("AppConfigService", function ($http,AppConfigFactory,$q) {

  this.getAllVersionByUuid=function(uuid,type){
    var deferred = $q.defer();
    AppConfigFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }


  this.saveAs=function(uuid,version,type){
     var deferred = $q.defer();
     AppConfigFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }
   this.getLatestByUuid=function(uuid,type){
     var deferred = $q.defer();
     AppConfigFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

   this.getOneByUuidandVersion=function(uuid,version,type){
     var deferred = $q.defer();
     AppConfigFactory.findOneByUuidandVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

  this.getAllLatest=function(type){
   var deferred = $q.defer();
    AppConfigFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  

  
    this.submit=function(data,type,upd_tag){
     var deferred = $q.defer();
     AppConfigFactory.submit(data,type,upd_tag).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
