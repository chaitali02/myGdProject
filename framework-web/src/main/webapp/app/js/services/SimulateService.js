/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.factory('SimulateFactory', function ($http, $location) {
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
  factory.findAllModelByType = function (flag,type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "model/getAllModelByType?action=view&customFlag="+flag+"&type=" + type,
      method: "GET",
      data: '',
      headers: {
          "Content-Type": "application/json"
      }
    }).then(function (response) { return response })


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
  factory.findParamListByDistribution = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getParamListByDistribution?action=view&uuid="+uuid+"&type=paramlist",
    }).then(function (response, status, headers) {
      return response;
    });
  }
  factory.findParamByParamList = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getParamByParamList?action=view&uuid=" + uuid + "&type=" + type,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
  return factory;
})

DatascienceModule.service("SimulateService", function ($http, SimulateFactory, $q, sortFactory) {
  
  this.getParamByParamList = function (uuid, type) {
		var deferred = $q.defer();
		SimulateFactory.findParamByParamList(uuid,type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function(response) {
      var paramListHolder=[];
      var type=["ONEDARRAY","TWODARRAY"];
      var type1=['distribution','attribute','attributes','datapod','list'];
      if(response.length >0){
        for(var i=0;i<response.length;i++){
          var paramList={};
          paramList.uuid=response[i].ref.uuid;
          paramList.type=response[i].ref.type;
          paramList.paramId=response[i].paramId;
          paramList.paramType=response[i].paramType.toLowerCase();
          paramList.paramName=response[i].paramName;
          paramList.ref=response[i].ref;
          paramList.attributeInfo;
          paramList.allAttributeinto=[];
          paramList.attributeInfoTag=[];
          if(type1.indexOf(response[i].paramType) == -1 ){
            paramList.isParamType="simple";
            paramList.paramValue=response[i].paramValue.value;
            paramList.selectedParamValueType='simple'
          }else if(type1.indexOf(response[i].paramType) != -1){
            paramList.isParamType=response[i].paramType;
            paramList.selectedParamValueType=response[i].paramType=="distribution" ?response[i].paramType:"datapod";
            paramList.paramValue=response[i].paramValue; 
            if(response[i].paramValue !=null && response[i].paramValue !='list'){
              var selectedParamValue={};
              selectedParamValue.uuid=response[i].paramValue.ref.uuid;
              selectedParamValue.type=response[i].paramValue.ref.type;
              paramList.selectedParamValue=selectedParamValue;
              }
              if( response[i].paramValue && response[i].paramType =='list'){
                paramList.selectedParamValueType="list";
                var listvalues=response[i].paramValue.value.split(',');
                var selectedParamValue={};
                selectedParamValue.type=response[i].paramValue.ref.type;
                selectedParamValue.value=listvalues[0];
                paramList.paramValue=selectedParamValue;
                paramList.selectedParamValue=selectedParamValue;
                paramList.allListInfo=listvalues;
              }   
        
          }else{
            paramList.isParamType="datapod";
            paramList.selectedParamValueType='datapod'
            paramList.paramValue=response[i].paramValue;    
          }
         
          paramListHolder[i]=paramList;
        }
      }
      deferred.resolve({
        data: paramListHolder
      });
    }
		return deferred.promise;
	}
  this.getAllModelByType = function (flag, type) {
    var deferred = $q.defer();
    SimulateFactory.findAllModelByType(flag, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getParamListByDistribution = function(uuid, version) {
    var deferred = $q.defer();
    SimulateFactory.findParamListByDistribution(uuid,version).then(function(response){ onSuccess(response.data)});
    var onSuccess = function(response) {
      var paramListHolder=[];
      var type=["ONEDARRAY","TWODARRAY"];
      var type1=['distribution','attribute','attributes','datapod','list'];
      if(response.length >0){
        for(var i=0;i<response.length;i++){
          var paramList={};
          paramList.uuid=response[i].ref.uuid;
          paramList.type=response[i].ref.type;
          paramList.paramId=response[i].paramId;
          paramList.paramType=response[i].paramType.toLowerCase();
          paramList.paramName=response[i].paramName;
          paramList.ref=response[i].ref;
          paramList.attributeInfo;
          paramList.allAttributeinto=[];
          paramList.attributeInfoTag=[];
          if(type1.indexOf(response[i].paramType) == -1 ){
            paramList.isParamType="simple";
            paramList.paramValue=response[i].paramValue.value;
            paramList.selectedParamValueType='simple'
          }else if(type1.indexOf(response[i].paramType) != -1){
            paramList.isParamType=response[i].paramType;
            paramList.selectedParamValueType=response[i].paramType=="distribution" ?response[i].paramType:"datapod";
            paramList.paramValue=response[i].paramValue;
            if(response[i].paramValue !=null && response[i].paramValue !='list'){
              var selectedParamValue={};
              selectedParamValue.uuid=response[i].paramValue.ref.uuid;
              selectedParamValue.type=response[i].paramValue.ref.type;
              paramList.selectedParamValue=selectedParamValue;
              }
              if( response[i].paramValue && response[i].paramType =='list'){
                paramList.selectedParamValueType="list";
                var listvalues=response[i].paramValue.value.split(',');
                var selectedParamValue={};
                selectedParamValue.type=response[i].paramValue.ref.type;
                selectedParamValue.value=listvalues[0];
                paramList.paramValue=selectedParamValue;
                paramList.selectedParamValue=selectedParamValue;
                paramList.allListInfo=listvalues;
              }    
        
          }else{
            paramList.isParamType="datapod";
            paramList.selectedParamValueType='datapod'
            paramList.paramValue=response[i].paramValue;    
          }
         
          paramListHolder[i]=paramList;
        }
      }
      deferred.resolve({
        data: paramListHolder
      });
    }
    return deferred.promise;
  }

  this.getOneById = function (uuid, type) {
    var deferred = $q.defer();
    SimulateFactory.findOneById(uuid, type).then(function (response) { onSuccess(response.data) });
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
      SimulateFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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
      SimulateFactory.findAttributesByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
      SimulateFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
    SimulateFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.saveAs = function (uuid, version, type) {
    var deferred = $q.defer();
    SimulateFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getLatestByUuid = function (uuid, type) {
    var deferred = $q.defer();
    SimulateFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {

      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  this.getOneByUuidandVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    SimulateFactory.findOneByUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

  this.getAllLatest = function (type) {
    var deferred = $q.defer();
    SimulateFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.getGraphData = function (uuid, version, degree) {
    var deferred = $q.defer();
    SimulateFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }


  this.submit = function (data,type,upd_tag){
    var deferred = $q.defer();
    SimulateFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
