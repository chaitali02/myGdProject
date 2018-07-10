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
  factory.findGraphPodResults = function (uuid,version,filterId,nodeType,degree,type,data) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "graph/getGraphPodResults?action=view&uuid="+uuid+"&version="+version+"&filterId="+filterId+"&nodeType="+nodeType+"&degree="+degree+"&type="+type,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
      data: JSON.stringify(data),
    }).then(function (response) { return response })
  }
  
  return factory;
})

GraphAnalysisModule.service("GraphpodService", function ($http, GraphpodFactory,$q,CF_GRAPHPOD) {
  this.getGraphPodResults = function (uuid,version,filterId,nodeType,degree,type,data) {
    var deferred = $q.defer();
    GraphpodFactory.findGraphPodResults(uuid,version,filterId,nodeType,degree,type,data).then(function (response) { onSuccess(response.data)},function(response){onError(response)});
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
          var nodeSource={}; 
          var nodeName={};
          nodeSource.uuid=response.nodeInfo[i].nodeSource.ref.uuid;
          nodeSource.name=response.nodeInfo[i].nodeSource.ref.name;
          nodeSource.type=response.nodeInfo[i].nodeSource.ref.type;
          nodeJson.nodeSource=nodeSource;
          nodeId.uuid=response.nodeInfo[i].nodeId.ref.uuid;
          nodeId.type=response.nodeInfo[i].nodeId.ref.type;
          nodeId.datapodname=response.nodeInfo[i].nodeId.ref.name;
          nodeId.name=response.nodeInfo[i].nodeId.attrName;
          nodeId.dname=response.nodeInfo[i].nodeId.ref.name+"."+response.nodeInfo[i].nodeId.attrName;
          nodeId.attributeId=response.nodeInfo[i].nodeId.attrId;
          nodeId.attrType=response.nodeInfo[i].nodeId.attrType;
          nodeJson.nodeId=nodeId;
          nodeJson.nodeType=response.nodeInfo[i].nodeType;
          nodeJson.nodeIcon={}
          nodeJson.nodeIcon=CF_GRAPHPOD.nodeIconMap[response.nodeInfo[i].nodeIcon];
          var nodeName={};
          nodeName.uuid=response.nodeInfo[i].nodeName.ref.uuid;
          nodeName.datapodname=response.nodeInfo[i].nodeName.ref.name;
          nodeName.type=response.nodeInfo[i].nodeName.ref.type;
          nodeName.name=response.nodeInfo[i].nodeName.attrName;
          nodeName.dname=response.nodeInfo[i].nodeName.ref.name+"."+response.nodeInfo[i].nodeName.attrName;
          nodeName.attributeId=response.nodeInfo[i].nodeName.attrId;
          nodeName.attrType=response.nodeInfo[i].nodeName.attrType;
          nodeJson.nodeName=nodeName;1
          var nodePropertiesArr=[];
          if(response.nodeInfo[i].nodeProperties !=null){
            for(var j=0;j<response.nodeInfo[i].nodeProperties.length;j++){
              var nodeProperties={};
              nodeProperties.uuid=response.nodeInfo[i].nodeProperties[j].ref.uuid;
              nodeProperties.type=response.nodeInfo[i].nodeProperties[j].ref.type;
              nodeProperties.datapodname=response.nodeInfo[i].nodeProperties[j].ref.name;
              nodeProperties.name=response.nodeInfo[i].nodeProperties[j].attrName;
              nodeProperties.dname=response.nodeInfo[i].nodeProperties[j].ref.name+"."+response.nodeInfo[i].nodeProperties[j].attrName;
              nodeProperties.attributeId=response.nodeInfo[i].nodeProperties[j].attrId;
              nodeProperties.attrType=response.nodeInfo[i].nodeProperties[j].attrType;
              nodeProperties.id = response.nodeInfo[i].nodeProperties[j].ref.uuid+"_"+response.nodeInfo[i].nodeProperties[j].attrId;
              nodePropertiesArr[j]=nodeProperties;
            }
          }
          var highlightInfo={};
          var propertyId={};
          if(response.nodeInfo[i].highlightInfo){
            highlightInfo.selectType=response.nodeInfo[i].highlightInfo.type;
            propertyId.uuid=response.nodeInfo[i].highlightInfo.propertyId.ref.uuid;
            propertyId.type=response.nodeInfo[i].highlightInfo.propertyId.ref.type;
            propertyId.datapodname=response.nodeInfo[i].highlightInfo.propertyId.ref.name;
            propertyId.name=response.nodeInfo[i].highlightInfo.propertyId.attrName;
            propertyId.dname=response.nodeInfo[i].highlightInfo.propertyId.ref.name+"."+response.nodeInfo[i].highlightInfo.propertyId.attrName;
            propertyId.attributeId=response.nodeInfo[i].highlightInfo.propertyId.attrId;
            propertyId.attrType=response.nodeInfo[i].highlightInfo.propertyId.attrType;
            propertyId.id =response.nodeInfo[i].highlightInfo.propertyId.ref.uuid+"_"+response.nodeInfo[i].highlightInfo.propertyId.attrId;
            highlightInfo.propertyId=propertyId;
            highlightInfo.value=response.nodeInfo[i].highlightInfo.type+","+response.nodeInfo[i].highlightInfo.propertyId.attrName;
            highlightInfo.propertyInfoTableArray=response.nodeInfo[i].highlightInfo.propertyInfo;
            nodeJson.highlightInfo=highlightInfo;
          }
          else{
            nodeJson.highlightInfo=null;
          }
          nodeJson.nodeProperties=nodePropertiesArr;
          nodeInfo[i]=nodeJson;
        }
      }
      jsongraphpod.nodeInfo=nodeInfo;
      var edgeInfo=[];
      if(response.edgeInfo !=null){
        for(var i=0;i<response.edgeInfo.length;i++){
          var edgeJson={};
          var edgeSource={};
          var sourceNodeId={};
          var targetNodeId={};
          edgeSource.uuid=response.edgeInfo[i].edgeSource.ref.uuid;
          edgeSource.name=response.edgeInfo[i].edgeSource.ref.name;
          edgeSource.type=response.edgeInfo[i].edgeSource.ref.type;
          edgeJson.edgeSource=edgeSource;
          edgeJson.edgeType=response.edgeInfo[i].edgeType;
          edgeJson.edgeName=response.edgeInfo[i].edgeName;
          sourceNodeId.uuid=response.edgeInfo[i].sourceNodeId.ref.uuid;
          sourceNodeId.type=response.edgeInfo[i].sourceNodeId.ref.type;
          sourceNodeId.datapodname=response.edgeInfo[i].sourceNodeId.ref.name;
          sourceNodeId.name=response.edgeInfo[i].sourceNodeId.attrName;
          sourceNodeId.dname=response.edgeInfo[i].sourceNodeId.ref.name+"."+response.edgeInfo[i].sourceNodeId.attrName;
          sourceNodeId.attributeId=response.edgeInfo[i].sourceNodeId.attrId;
          sourceNodeId.attrType=response.edgeInfo[i].sourceNodeId.attrType;
          edgeJson.sourceNodeId=sourceNodeId;
          edgeJson.sourceNodeType=response.edgeInfo[i].sourceNodeType;
          targetNodeId.uuid=response.edgeInfo[i].targetNodeId.ref.uuid;
          targetNodeId.type=response.edgeInfo[i].targetNodeId.ref.type;
          targetNodeId.datapodname=response.edgeInfo[i].targetNodeId.ref.name;
          targetNodeId.name=response.edgeInfo[i].targetNodeId.attrName;
          targetNodeId.dname=response.edgeInfo[i].targetNodeId.ref.name+"."+response.edgeInfo[i].targetNodeId.attrName;
          targetNodeId.attributeId=response.edgeInfo[i].targetNodeId.attrId;
          targetNodeId.attrType=response.edgeInfo[i].targetNodeId.attrType;
          edgeJson.targetNodeId=targetNodeId;
          edgeJson.targetNodeType=response.edgeInfo[i].targetNodeType;
          var edgePropertiesArr=[];
          if(response.edgeInfo[i].edgeProperties !=null){
            for(var j=0;j<response.edgeInfo[i].edgeProperties.length;j++){
              var edgeProperties={};
              edgeProperties.uuid=response.edgeInfo[i].edgeProperties[j].ref.uuid;
              edgeProperties.type=response.edgeInfo[i].edgeProperties[j].ref.type;
              edgeProperties.datapodname=response.edgeInfo[i].edgeProperties[j].ref.name;
              edgeProperties.name=response.edgeInfo[i].edgeProperties[j].attrName;
              edgeProperties.dname=response.edgeInfo[i].edgeProperties[j].ref.name+"."+response.edgeInfo[i].edgeProperties[j].attrName;
              edgeProperties.attributeId=response.edgeInfo[i].edgeProperties[j].attrId;
              edgeProperties.attrType=response.edgeInfo[i].edgeProperties[j].attrType;
              edgeProperties.id = response.edgeInfo[i].edgeProperties[j].ref.uuid+"_"+response.edgeInfo[i].edgeProperties[j].attrId;
              edgePropertiesArr[j]=edgeProperties;
            }
          }
          edgeJson.edgeProperties=edgePropertiesArr;
          edgeInfo[i]=edgeJson;
        }
      }
      jsongraphpod.edgeInfo=edgeInfo;
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
