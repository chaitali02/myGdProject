/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.factory('PredictFactory', function ($http, $location) {
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
  factory.findAllModelByType = function (flag, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "model/getAllModelByType?action=view&customFlag=" + flag + "&type=" + type,
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

  factory.submit = function (data, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/submit?action=edit&type=" + type,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
      data: JSON.stringify(data),
    }).success(function (response) { return response })
  }
  factory.findGraphData = function (uuid, version, degree) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "graph/getGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree,
      method: "GET"
    }).then(function (response) { return response })
  };

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
  factory.findDatapodByRelation = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=datapod",
    }).then(function (response, status, headers) {
      return response;
    })
  }
  factory.findExecutePredict = function (uuid, version) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "model/predict/execute?action=execute&uuid=" + uuid + "&version=" + version,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",

    }).then(function (response) { return response })
  }
  factory.findExecutePredictWithBody = function (uuid, version, data) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "model/predict/execute?action=execute&uuid=" + uuid + "&version=" + version,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
      data: JSON.stringify(data),
    }).then(function (response) { return response })
  }
  factory.findAttributesByRule = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getAttributesByRule?action=view&uuid=" + uuid + "&type=" + type,

    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  factory.findTrainByModel = function (uuid,version,type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "model/getTrainByModel?action=view&uuid=" + uuid +"&version="+version+"&type=" + type,

    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  return factory;
})

DatascienceModule.service("PredictService", function ($http, PredictFactory, $q, sortFactory) {

  this.getAllModelByType = function (flag, type) {
    var deferred = $q.defer();
    PredictFactory.findAllModelByType(flag, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getTrainByModel = function (uuid,version,type) {
    var deferred = $q.defer();
    PredictFactory.findTrainByModel(uuid,version,type).then(function (response) { onSuccess(response.data) },function(response){onError(response.data)});
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
  this.getExecutePredict = function (uuid, version, data) {
    var deferred = $q.defer();
    if (data != null) {
      PredictFactory.findExecutePredictWithBody(uuid, version, data).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        deferred.resolve({
          data: response
        });
      }
    }
    else {
      PredictFactory.findExecutePredict(uuid, version).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        deferred.resolve({
          data: response
        });
      }

    }

    return deferred.promise;
  }

  this.getOneById = function (uuid, type) {
    var deferred = $q.defer();
    PredictFactory.findOneById(uuid, type).then(function (response) { onSuccess(response.data) });
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
      PredictFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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
      PredictFactory.findAttributesByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
    if (type == "datapod") {
      PredictFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
    if (type == "rule") {
      PredictFactory.findAttributesByRule(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
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
    PredictFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.saveAs = function (uuid, version, type) {
    var deferred = $q.defer();
    PredictFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getLatestByUuid = function (uuid, type) {
    var deferred = $q.defer();
    PredictFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getOneByUuidandVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    PredictFactory.findOneByUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getAllLatest = function (type) {
    var deferred = $q.defer();
    PredictFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getGraphData = function (uuid, version, degree) {
    var deferred = $q.defer();
    PredictFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.submit = function (data, type) {
    var deferred = $q.defer();
    PredictFactory.submit(data, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
