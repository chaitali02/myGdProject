/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.factory('TrainFactory', function ($http, $location) {
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
  factory.findSaveAs = function (uuid, version, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/saveAs?action=clone&uuid=" + uuid + "&version=" + version + "&type=" + type,
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

  factory.findAttributeByDatapod = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=" + type,
    }).then(function (response, status, headers) { return response; })
  }
  factory.findAttributesByDataset = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid + "&type=dataset",
    }).then(function (response, status, headers) { return response; })
  }
  factory.getAttributesByRule = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getAttributesByRule?action=view&uuid=" + uuid + "&type=rule",
    }).then(function (response, status, headers) { return response; })
  }
  
  factory.findDatapodByRelation = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=datapod",
    }).then(function (response, status, headers) {
      return response;
    })
  }

  factory.findAllModelByType = function (flag,type){
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "model/getAllModelByType?action=view&customFlag="+flag+"&type=" + type+"&modelType=algorithm",
      method: "GET",
  }).then(function (response) { return response })
  }

  factory.findParamSetByAlgorithm = function (uuid, version,isHyperParam) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getParamSetByAlgorithm?action=view&algorithmUuid=" + uuid + "&algorithmVersion=" + version+"&isHyperParam="+isHyperParam,
      method: "GET"
    }).then(function (response) { return response })
  };
  factory.findParamListByAlgorithm = function (uuid,version,type,isHyperParam) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getParamListByAlgorithm?action=view&uuid=" + uuid + "&version=" + version+"&type="+type+"&isHyperParam="+isHyperParam,
      method: "GET"
    }).then(function (response) { return response })
  };
 
  return factory;
})

DatascienceModule.service("TrainService", function ($http, TrainFactory, $q, sortFactory) {
  
  this.getParamListByAlgorithm = function (uuid,version,type,isHyperParam) {
    var deferred = $q.defer();
    TrainFactory.findParamListByAlgorithm(uuid,version,type,isHyperParam).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var result=[];
      if(response && response.length >0){
      // response[0].ref.name=response[0].ref.name//+ " (default)"
        for(var i=0;i<response.length;i++){
          var res={};
          res.uuid=response[i].ref.uuid;
          res.name=response[i].ref.name;
          result[i]=res;
        }
      }
      deferred.resolve({
        data: result
      });
    }
    return deferred.promise;
  }
  this.getParamSetByAlgorithm = function (uuid, version,isHyperParam) {
    var deferred = $q.defer();
    TrainFactory.findParamSetByAlgorithm(uuid, version,isHyperParam).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getAllModelByType = function (flag, type) {
    var deferred = $q.defer();
    TrainFactory.findAllModelByType(flag, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getOneById = function (uuid, type) {
    var deferred = $q.defer();
    TrainFactory.findOneById(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.getAllAttributeBySource = function (uuid, type) {
    var deferred = $q.defer();
    if (type == "relation") {
      TrainFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          for (var i = 0; i < response[j].attributes.length; i++) {
            var attributedetail = {};
            attributedetail.uuid = response[j].uuid;
            attributedetail.datapodname = response[j].name;
            attributedetail.name = response[j].attributes[i].name;
            attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
            attributedetail.attributeId = response[j].attributes[i].attributeId;
            attributes.push(attributedetail)
          }
        }

        console.log(JSON.stringify(attributes))
        deferred.resolve({
          data: attributes
        })
      }
    }
    if (type == "dataset") {
      TrainFactory.findAttributesByDataset(uuid).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.attrType=response[j].attrType;
          attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }


    }
    if (type == "datapod") {
      TrainFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.attrType=response[j].attrType;
          attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }

    }
    if (type == "rule") {
      TrainFactory.getAttributesByRule(uuid,type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }

    }

    return deferred.promise;
  }



  this.getAllVersionByUuid = function (uuid, type) {
    var deferred = $q.defer();
    TrainFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.saveAs = function (uuid, version, type) {
    var deferred = $q.defer();
    TrainFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getLatestByUuid = function (uuid, type) {
    var deferred = $q.defer();
    TrainFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getOneByUuidandVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    TrainFactory.findOneByUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getAllLatest = function (type) {
    var deferred = $q.defer();
    TrainFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getGraphData = function (uuid, version, degree) {
    var deferred = $q.defer();
    TrainFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.submit = function (data,type,upd_tag) {
    var deferred = $q.defer();
    TrainFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
