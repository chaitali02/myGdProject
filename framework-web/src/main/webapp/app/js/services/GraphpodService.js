/**
 *
 */
GraphAnalysisModule = angular.module('GraphAnalysisModule');

GraphAnalysisModule.factory('GraphpodFactory', function ($http, $location) {
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

  factory.submit = function(data,type,upd_tag) {
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
      url: url + "common/getOneById?action=view&id=" + id + "&type=" + type,
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

  return factory;
})

GraphAnalysisModule.service("GraphpodService", function ($http, GraphpodFactory, $q) {

  this.getAllVersionByUuid = function (uuid, type) {
    var deferred = $q.defer();
    GraphpodFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getLatestByUuid = function (uuid, type) {
    var deferred = $q.defer();
    GraphpodFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getOneByUuidandVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    GraphpodFactory.findOneByUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var jsongraphpod={}
      jsongraphpod.graphpod=response;
      var nodeInfo=[];
      if(response.nodeInfo !=null){
        for(var i=0;i<response.nodeInfo.length;i++){
          var nodeJson={};
          var nodeId={};
          var nodeName={};
          nodeId.uuid=response.nodeInfo[i].nodeId.ref.uuid;
          nodeId.datapodname=response.nodeInfo[i].nodeId.ref.name;
          nodeId.name=response.nodeInfo[i].nodeId.attrName;
          nodeId.dname=response.nodeInfo[i].nodeId.ref.name+"."+response.nodeInfo[i].nodeId.attrName;
          nodeId.attributeId=response.nodeInfo[i].nodeId.attrId;
          nodeJson.nodeId=nodeId;
          nodeJson.nodeType=response.nodeInfo[i].nodeType;
          nodeJson.nodeIcon=response.nodeInfo[i].nodeIcon;
          var nodeName={};
          nodeName.uuid=response.nodeInfo[i].nodeName.ref.uuid;
          nodeName.datapodname=response.nodeInfo[i].nodeName.ref.name;
          nodeName.name=response.nodeInfo[i].nodeName.attrName;
          nodeName.dname=response.nodeInfo[i].nodeName.ref.name+"."+response.nodeInfo[i].nodeName.attrName;
          nodeName.attributeId=response.nodeInfo[i].nodeName.attrId;
          nodeJson.nodeName=nodeName;1
          var nodePropertiesArr=[];
          if(response.nodeInfo[i].nodeProperties !=null){
            for(var j=0;j<response.nodeInfo[i].nodeProperties.length;j++){
              var nodeProperties={};
              nodeProperties.uuid=response.nodeInfo[i].nodeProperties[j].ref.uuid;
              nodeProperties.datapodname=response.nodeInfo[i].nodeProperties[j].ref.name;
              nodeProperties.name=response.nodeInfo[i].nodeProperties[j].attrName;
              nodeProperties.dname=response.nodeInfo[i].nodeProperties[j].ref.name+"."+response.nodeInfo[i].nodeProperties[j].attrName;
              nodeProperties.attributeId=response.nodeInfo[i].nodeProperties[j].attrId;
              nodeProperties.id = response.nodeInfo[i].nodeProperties[j].ref.uuid+"_"+response.nodeInfo[i].nodeProperties[j].attrId;
              nodePropertiesArr[j]=nodeProperties;
            }
          }
          nodeJson.nodeProperties=nodePropertiesArr;
          nodeInfo[i]=nodeJson;
        }
      }
      jsongraphpod.nodeInfo=nodeInfo;
      deferred.resolve({
        data: jsongraphpod
      });
    }
    return deferred.promise;
  }
  this.getAllLatest = function (type) {
    var deferred = $q.defer();
    GraphpodFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  
  this.submit = function (data,type,upd_tag) {
    var deferred = $q.defer();
    GraphpodFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
