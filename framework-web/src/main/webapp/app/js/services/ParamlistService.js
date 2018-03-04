/**
 *
 */
DatascienceModule=angular.module('DatascienceModule');

DatascienceModule.factory('ParamListFactory',function($http,$location){
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
   factory.findSaveAs=function(uuid,version,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
              url:url+"common/saveAs?action=clone&uuid="+uuid+"&version="+version+"&type="+type,
              method: "GET",
            }).then(function(response){ return  response})
    }
    factory.findExecuteModel=function(uuid,version){
      var url=$location.absUrl().split("app")[0]
      return $http({
              url:url+"model/train?action=execute&modelUUID="+uuid+"&modelVersion="+version,
              method: "GET",
            }).then(function(response){ return  response})
    }

    factory.findOneByUuidandVersion=function(uuid,version,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"metadata/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
        method: "GET",

        }).then(function(response){ return  response})
    }

    factory.submit=function(data,type){
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
    factory.findGraphData=function(uuid,version,degree){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
          method: "GET"
        }).then(function(response){ return  response})
    };

    factory.findOneById=function(id,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"metadata/getOneById?action=view&id="+id+"&type="+type,
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

DatascienceModule.service("ParamListService", function ($http,ParamListFactory,$q) {

  this.getAllVersionByUuid=function(uuid,type){
    var deferred = $q.defer();
    ParamListFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }


  this.saveAs=function(uuid,version,type){
     var deferred = $q.defer();
     ParamListFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }
   this.getLatestByUuid=function(uuid,type){
     var deferred = $q.defer();
     ParamListFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){

          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

   this.getOneByUuidandVersion=function(uuid,version,type){
     var deferred = $q.defer();
     ParamListFactory.findOneByUuidandVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){

          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

  this.getAllLatest=function(type){
   var deferred = $q.defer();
    ParamListFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getGraphData=function(uuid,version,degree){
   var deferred = $q.defer();
    ParamListFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }

  this.getAllLatestList=function(type) {
    var deferred = $q.defer();
    ParamListFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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

    this.submit=function(data,type){
     var deferred = $q.defer();
     ParamListFactory.submit(data,type).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
