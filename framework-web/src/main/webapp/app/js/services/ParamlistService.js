/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.factory('ParamListFactory', function ($http, $location) {
  var factory = {};
  factory.findAllLatest = function (type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "common/getAllLatest?action=view&type=" + type,
    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  factory.findLatestByUuid = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
      method: "GET",
    }).then(function (response) { return response })


  }


  factory.findOneByUuidandVersion = function (uuid, version, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
      method: "GET",

    }).then(function (response) { return response })
  }

  factory.submit = function (data,type,upd_tag) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/submit?action=edit&type="+type+"&upd_tag="+upd_tag,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
      data: JSON.stringify(data),
    }).success(function (response) { return response })
  }

  factory.findOneById = function (id, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getOneById?action=view&id=" + id + "&type=" + type,
      method: "GET"
    }).then(function (response) { return response })
  }
  factory.findAllVersionByUuid = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
      method: "GET"
    }).then(function (response) { return response })
  }

  factory.findAllLatestParamListByTemplate = function (templateFlg,type,paramListType) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getAllLatestParamListByTemplate?action=view&templateFlg=" + templateFlg + "&type=" + type+"&paramListType="+paramListType,
      method: "GET"
    }).then(function (response) { return response })
  }

  return factory;
})

DatascienceModule.service("ParamListService", function ($http, ParamListFactory, $q) {

  this.getAllLatestParamListByTemplate = function (templateFlg, type,paramListType) {
    var deferred = $q.defer();
    ParamListFactory.findAllLatestParamListByTemplate(templateFlg, type,paramListType).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getAllVersionByUuid = function (uuid, type) {
    var deferred = $q.defer();
    ParamListFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getLatestByUuid = function (uuid, type) {
    var deferred = $q.defer();
    ParamListFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getOneByUuidandVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    ParamListFactory.findOneByUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
    var onSuccess = function (response) {
      var paramArray=[];
      for(var i=0;i<response.params.length;i++){
        var paramInfo={}
          paramInfo.paramId=response.params[i].paramId; 
          paramInfo.paramName=response.params[i].paramName;
          paramInfo.paramDesc=response.params[i].paramDesc;
          paramInfo.paramDispName=response.params[i].paramDispName;
          paramInfo.paramType=response.params[i].paramType.toLowerCase();
          if(response.params[i].paramValue !=null && response.params[i].paramValue.ref.type == "simple" && ["string", "double", "integer", "list","decimal"].indexOf(response.params[i].paramType) != -1){
            paramInfo.paramValue=response.params[i].paramValue.value;
            paramInfo.paramValueType="simple"
        }
        else if(response.params[i].paramValue !=null && response.params[i].paramValue.ref.type == "simple" && ["date"].indexOf(response.params[i].paramType) !=-1){
          var temp=response.params[i].paramValue.value.replace(/["']/g, "")
          paramInfo.paramValue=new Date(temp+":00:00:00");
          paramInfo.paramValueType="date"
        }
        else if(response.params[i].paramValue !=null && response.params[i].paramValue.ref.type == "simple" && ["array"].indexOf(response.params[i].paramType) !=-1){
          var temp=response.params[i].paramValue.value.split(",");
          paramInfo.paramArrayTags=temp;
          paramInfo.paramValueType="array"
        }
        else if(response.params[i].paramValue !=null){
          var paramValue={};
          paramValue.uuid=response.params[i].paramValue.ref.uuid;
          paramValue.type=response.params[i].paramValue.ref.type;
          paramInfo.paramValue=paramValue;
          paramInfo.paramValueType=response.params[i].paramValue.ref.type;
        }else{
          
        }
        paramArray[i]=paramInfo;
      }
      response.paramInfo=paramArray;
      deferred.resolve({
        data: response
      });
    }
    var onError = function (response) {
      deferred.reject({
        data: response
      })
    }
    return deferred.promise;
  }

  this.getAllLatest = function (type) {
    var deferred = $q.defer();
    ParamListFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.submit = function (data,type,upd_tag) {
    var deferred = $q.defer();
    ParamListFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    var onError = function (response) {
      deferred.reject({
        data: response
      })
    }
    return deferred.promise;
  }
});
