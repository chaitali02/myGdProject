/**
 *
 */
DatascienceModule=angular.module('DatascienceModule');

DatascienceModule.factory('ParamSetFactory',function($http,$location){
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
        url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
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

DatascienceModule.service("ParamSetService", function ($http,ParamSetFactory,$q) {

  this.getAllVersionByUuid=function(uuid,type){
    var deferred = $q.defer();
    ParamSetFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }


  this.saveAs=function(uuid,version,type){
     var deferred = $q.defer();
     ParamSetFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
    }
    this.getLatestByUuidView=function(uuid,type){
      var deferred = $q.defer();
      ParamSetFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
          var paramSetjson={};
          paramSetjson.paramsetdata=response;
          var paramInfoArray=[];
          for(var i=0;i<response.paramInfo.length;i++){
            var paramInfo={};
            paramInfo.paramSetId=response.paramInfo[i].paramSetId
            var paramSetValarray=[];
            for(var j=0;j<response.paramInfo[i].paramSetVal.length;j++){
              var paramSetValjson={};
              paramSetValjson.paramId=response.paramInfo[i].paramSetVal[j].paramId;
              paramSetValjson.paramName=response.paramInfo[i].paramSetVal[j].paramName;
              paramSetValjson.value=response.paramInfo[i].paramSetVal[j].value;
              paramSetValarray[j]=paramSetValjson;
              paramInfo.paramSetVal=paramSetValarray;
              paramInfo.value=response.paramInfo[i].paramSetVal[j].value;
            }
             paramInfoArray[i]=paramInfo;
          }
          paramSetjson.paramSetValarray=paramSetValarray;
          paramSetjson.paramInfoArray=paramInfoArray;
          deferred.resolve({
                data:paramSetjson
            });
       }
       return deferred.promise;
   }
    this.getLatestByUuid=function(uuid,type){
      var deferred = $q.defer();
      ParamSetFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
         ;
          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

   this.getOneByUuidandVersion=function(uuid,version,type){
     var deferred = $q.defer();
     ParamSetFactory.findOneByUuidandVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){
          var paramSetjson={};
          paramSetjson.paramsetdata=response;
          var paramInfoArray=[];
          for(var i=0;i<response.paramInfo.length;i++){
            var paramInfo={};
            paramInfo.paramSetId=response.paramInfo[i].paramSetId
            var paramSetValarray=[];
            for(var j=0;j<response.paramInfo[i].paramSetVal.length;j++){
              var paramSetValjson={};
              paramSetValjson.paramId=response.paramInfo[i].paramSetVal[j].paramId;
              paramSetValjson.paramName=response.paramInfo[i].paramSetVal[j].paramName;
              paramSetValjson.value=response.paramInfo[i].paramSetVal[j].value;
              paramSetValarray[j]=paramSetValjson;
              paramInfo.paramSetVal=paramSetValarray;
              paramInfo.value=response.paramInfo[i].paramSetVal[j].value;
            }
             paramInfoArray[i]=paramInfo;
          }
          paramSetjson.paramSetValarray=paramSetValarray;
          paramSetjson.paramInfoArray=paramInfoArray;
          deferred.resolve({
                data:paramSetjson
            });
       }
       return deferred.promise;
   }

  this.getAllLatest=function(type){
   var deferred = $q.defer();
    ParamSetFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getGraphData=function(uuid,version,degree){
   var deferred = $q.defer();
    ParamSetFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }

  this.getAllLatestList=function(type) {
    var deferred = $q.defer();
    ParamSetFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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
     ParamSetFactory.submit(data,type).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
