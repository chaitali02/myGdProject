    /***/
    DatavisualizationModule = angular.module('DatavisualizationModule');
    DatavisualizationModule.factory('VizpodFactory', function($http, $location) {
      var factory = {}
      factory.findLatestByUuid = function(uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
          method: "GET",
        }).then(function(response) {
          return response
        })
      }

      factory.findOneByUuidAndVersion = function(uuid, version, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
          method: "GET",

        }).then(function(response) {
          return response
        })
      }

      factory.submit = function(data,type,upd_tag){
        var url = $location.absUrl().split("app")[0]
        return $http({
          url: url + "common/submit?action=edit&type="+type+"&upd_tag="+upd_tag,
          headers: {
            'Accept': '*/*',
            'content-Type': "application/json",
          },
          method: "POST",
          data: JSON.stringify(data),
        }).success(function(response) {
          return response
        })
      }

      factory.findOneById = function(id, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          url: url + "metadata/getOneById?action=view&id=" + id + "&type=" + type,
          method: "GET"
        }).then(function(response) {
          return response
        })
      }

      factory.findAllVersionByUuid = function(uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
          method: "GET"
        }).then(function(response) {
          return response
        })

      }

      factory.findAllLatest = function(type, inputFlag) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "common/getAllLatest?action=view&type=" + type + "&inputFlag=" + inputFlag,
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findDatapodByDatapod = function(uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findDatapodByDataset = function(uuid) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid,
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findDatapodByRule = function(uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getAttributesByRule?action=view&uuid="+uuid+"&type="+type,
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findDatapodByRelation = function(uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=datapod",
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findFormulaByType = function(uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getFormulaByType?action=view&uuid="+uuid+"&type="+type,
        }).then(function(response, status, headers) {
          return response;
        })
      }

      factory.findExpressionByType = function(uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getExpressionByType?action=view&uuid="+uuid+"&type="+type
        }).then(function(response, status, headers) {
          return response;
        })
      }
      factory.findAttributesByRelation = function (uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getAttributesByRelation?action=view&uuid=" + uuid + "&type=" + type,
        }).
          then(function (response, status, headers) {
            return response;
          })
      }
      return factory;
    });

    DatavisualizationModule.service('VizpodSerivce', function($q, sortFactory, VizpodFactory) {

      this.getFormulaByType = function(uuid,type) {
        var deferred = $q.defer();
        VizpodFactory.findFormulaByType(uuid,type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      }

      this.getExpressionByType = function(uuid,type) {
        var deferred = $q.defer();
        VizpodFactory.findExpressionByType(uuid,type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      }

      this.getAllAttributeBySource = function(uuid, type) {
        var deferred = $q.defer();
        if (type == "relation") {
          // VizpodFactory.findDatapodByRelation(uuid, type).then(function(response) {
          //   onSuccess(response.data)
          // });
          // var onSuccess = function(response) {
          //   var attributes = [];
          //   for (var j = 0; j < response.length; j++) {
          //     for (var i = 0; i < response[j].attributes.length; i++) {
          //       var attributedetail = {};
          //       attributedetail.id = j
          //       attributedetail.class = "tagit-choice-default-dd";
          //       attributedetail.type = "datapod";
          //       attributedetail.uuid = response[j].uuid;
          //       attributedetail.name = response[j].name;
          //       attributedetail.attributeName = response[j].attributes[i].name;
          //       attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
          //       attributedetail.attributeId = response[j].attributes[i].attributeId;
          //       attributes.push(attributedetail)
          //     }
          //   }
          //   deferred.resolve({
          //     data: attributes
          //   })
          // }

          VizpodFactory.findAttributesByRelation(uuid, type, "").then(function (response) { onSuccess(response.data) });
          var onSuccess = function (response) {
            var attributes = [];
            for (var j = 0; j < response.length; j++) {
              var attributedetail = {};
              attributedetail.class = "tagit-choice-default-dd";
              attributedetail.type = response[j].ref.type
              attributedetail.uuid = response[j].ref.uuid;
              attributedetail.name = response[j].ref.name;
              attributedetail.attributeName = response[j].attrName;
              attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
              attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
              attributedetail.attributeId = response[j].attrId;
              attributes.push(attributedetail)
            }
    
            deferred.resolve({
              data: attributes
            })
          }
        }

        if (type == "dataset") {
          VizpodFactory.findDatapodByDataset(uuid,type).then(function(response) {
            onSuccess(response.data)
          });
          var onSuccess = function(response) {
            var attributes = [];
            for (var j = 0; j < response.length; j++) {
              var attributedetail = {};
              attributedetail.class = "tagit-choice-default-dd";
              attributedetail.type = response[j].ref.type
              attributedetail.uuid = response[j].ref.uuid;
              attributedetail.name = response[j].ref.name;
              attributedetail.attributeName = response[j].attrName;
              attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
              attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
              attributedetail.attributeId = response[j].attrId;
              attributes.push(attributedetail)
            }
            deferred.resolve({
              data: attributes
            })
          }

        }

        if (type == "datapod") {
          VizpodFactory.findDatapodByDatapod(uuid,type).then(function(response) {
            onSuccess(response.data)
          });
          var onSuccess = function(response) {
            var attributes = [];
            for (var j = 0; j < response.length; j++) {
              var attributedetail = {};
              attributedetail.id = j
              attributedetail.class = "tagit-choice-default-dd";
              attributedetail.type = response[j].ref.type
              attributedetail.uuid = response[j].ref.uuid;
              attributedetail.name = response[j].ref.name;
              attributedetail.attributeName = response[j].attrName;
              attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
              attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
              attributedetail.attributeId = response[j].attrId;
              attributes.push(attributedetail)
            }
            deferred.resolve({
              data: attributes
            })
          }

        }

        if (type == "rule") {
          VizpodFactory.findDatapodByRule(uuid,type).then(function(response) {
            onSuccess(response.data)
          });
          var onSuccess = function(response) {
            var attributes = [];
            for (var j = 0; j < response.length; j++) {
              var attributedetail = {};
              attributedetail.class = "tagit-choice-default-dd";
              attributedetail.type = response[j].ref.type
              attributedetail.uuid = response[j].ref.uuid;
              attributedetail.name = response[j].ref.name;
              attributedetail.attributeName = response[j].attrName;
              attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
              attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
              attributedetail.attributeId = response[j].attrId;
              attributes.push(attributedetail)
            }
            deferred.resolve({
              data: attributes
            })
          }

        }
        return deferred.promise;
      }
      this.getAttributesByDatapod = function(uuid,type) {
        var deferred = $q.defer();
        VizpodFactory.findDatapodByDatapod(uuid,type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          var attributes = [];
          for (var j = 0; j < response.length; j++) {
            var attributedetail = {};
            attributedetail.uuid = response[j].ref.uuid;
            attributedetail.datapodname = response[j].ref.name;
            attributedetail.name = response[j].attrName;
            attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
            attributedetail.attributeId = response[j].attrId;
            attributes.push(attributedetail)
          }
          deferred.resolve({
            data: attributes
          })
        }
        return deferred.promise;
      }

      this.getAllVersionByUuid = function(uuid, type) {
        var deferred = $q.defer();
        VizpodFactory.findAllVersionByUuid(uuid, type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      };

      this.getLatestByUuidView = function(id, type) {
        var deferred = $q.defer();
        VizpodFactory.findLatestByUuid(id, type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          var vizpodjosn = {};
          vizpodjosn.vizpoddata = response;
          var detailAttrInfoArray=[];
		 	    for(var i=0;i<response.detailAttr.length;i++){
		    		var attrinfo={};
		    		attrinfo.uuid=response.detailAttr[i].ref.uuid;
		    		attrinfo.dname=response.detailAttr[i].ref.name+"."+response.detailAttr[i].attributeName;
		    	  attrinfo.attributeId=response.detailAttr[i].attrId;
		    		attrinfo.id=response.detailAttr[i].ref.uuid+"_"+response.detailAttr[i].attributeId

		    		detailAttrInfoArray[i]=attrinfo;
		   }
           vizpodjosn.detailAttr=detailAttrInfoArray
		 	 
          var keyArray = [];
          for (var i = 0; i < response.keys.length; i++) {
            var keyjson = {};
            keyjson.id = i;
            keyjson.name = response.keys[i].ref.name;
            keyjson.type = response.keys[i].ref.type;
            keyjson.uuid = response.keys[i].ref.uuid;
            keyjson.attributeId = response.keys[i].attributeId;
            keyjson.attributeName = response.keys[i].attributeName;
            keyArray[i] = keyjson;
          }
          vizpodjosn.keys = keyArray;
          var groupArray = [];
          for (var i = 0; i < response.groups.length; i++) {
            var groupjson = {};
            groupjson.id = i;
            groupjson.name = response.groups[i].ref.name;
            groupjson.type = response.groups[i].ref.type;
            groupjson.uuid = response.groups[i].ref.uuid;
            groupjson.attributeId = response.groups[i].attributeId;
            groupjson.attributeName = response.groups[i].attributeName;
            groupArray[i] = groupjson;
          }
          vizpodjosn.groups = groupArray;
          var valueArray = [];
          for (var i = 0; i < response.values.length; i++) {
            var valuejson = {};
            valuejson.id = i;
            valuejson.name = response.values[i].ref.name;
            valuejson.type = response.values[i].ref.type;
            valuejson.uuid = response.values[i].ref.uuid;
            if (response.values[i].ref.type == "datapod") {
              valuejson.dname = response.values[i].ref.name + "." + response.values[i].attributeName;
              valuejson.attributeId = response.values[i].attributeId;
              valuejson.attributeName = response.values[i].attributeName;
            } else {
              valuejson.dname = response.values[i].ref.name;
            }
            valueArray[i] = valuejson;
          }
          vizpodjosn.values = valueArray;
          deferred.resolve({
            data: vizpodjosn
          })
        }
        return deferred.promise;
      }

      this.getOneById = function(id, type) {
        var deferred = $q.defer();
        VizpodFactory.findOneById(id, type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      }

      this.getLatestByUuid = function(id, type) {
        var deferred = $q.defer();
        VizpodFactory.findLatestByUuid(id, type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      }

      this.getAllLatestFunction = function(type, inputFlag) {
        var deferred = $q.defer();
        VizpodFactory.findAllLatest(type, inputFlag).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
      }

      this.getOneByUuidAndVersionView = function(uuid, version, type) {
        var deferred = $q.defer();
        VizpodFactory.findOneByUuidAndVersion(uuid, version, type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          var vizpodjosn = {};
          vizpodjosn.vizpoddata = response;
          var detailAttrInfoArray=[];
          if(response.detailAttr !=null){
            for(var i=0;i<response.detailAttr.length;i++){
              var attrinfo={};
              attrinfo.uuid=response.detailAttr[i].ref.uuid;
              attrinfo.type=response.detailAttr[i].ref.type;
              attrinfo.dname=response.detailAttr[i].ref.name+"."+response.detailAttr[i].attributeName;
              attrinfo.attributeId=response.detailAttr[i].attrId;
              attrinfo.id=response.detailAttr[i].ref.uuid+"_"+response.detailAttr[i].attributeId

              detailAttrInfoArray[i]=attrinfo;
            }
          }
		 	    vizpodjosn.detailAttr=detailAttrInfoArray
          var keyArray = [];
          for (var i = 0; i < response.keys.length; i++) {
            var keyjson = {};
            keyjson.id = i;
            keyjson.name = response.keys[i].ref.name;
            keyjson.type = response.keys[i].ref.type;
            keyjson.uuid = response.keys[i].ref.uuid;
            keyjson.attributeId = response.keys[i].attributeId;
            keyjson.attributeName = response.keys[i].attributeName;
            keyArray[i] = keyjson;
          }
          vizpodjosn.keys = keyArray;
          var groupArray = [];
          for (var i = 0; i < response.groups.length; i++) {
            var groupjson = {};
            groupjson.id = i;
            groupjson.name = response.groups[i].ref.name;
            groupjson.type = response.groups[i].ref.type;
            groupjson.uuid = response.groups[i].ref.uuid;
            groupjson.attributeId = response.groups[i].attributeId;
            groupjson.attributeName = response.groups[i].attributeName;
            groupArray[i] = groupjson;
          }
          vizpodjosn.groups = groupArray;
          var valueArray = [];
          for (var i = 0; i < response.values.length; i++) {
            var valuejson = {};
            valuejson.id = i;
            valuejson.name = response.values[i].ref.name;
            valuejson.type = response.values[i].ref.type;
            valuejson.uuid = response.values[i].ref.uuid;
            if (response.values[i].ref.type == "datapod" || response.values[i].ref.type == "dataset") {
              valuejson.dname = response.values[i].ref.name + "." + response.values[i].attributeName;
              valuejson.attributeId = response.values[i].attributeId;
              valuejson.attributeName = response.values[i].attributeName;
            } else {
              valuejson.dname = response.values[i].ref.name;
            }

            valueArray[i] = valuejson;
          }
          vizpodjosn.values = valueArray;
          deferred.resolve({
            data: vizpodjosn
          })
        }
        return deferred.promise;
      }

      this.submit = function(data,type,upd_tag) {
        var deferred = $q.defer();
        VizpodFactory.submit(data,type,upd_tag).then(function(response) {
          onSuccess(response.data)},
          function(response){
            onError(response.data)
          });
        var onSuccess = function(response) {
          deferred.resolve({
            data: response
          })
        }
        var onError=function(response){
        deferred.reject({
          data:response
        })
      }
        return deferred.promise;
      }

      this.getAllLatest = function(type) {
        var deferred = $q.defer();
        VizpodFactory.findAllLatest(type).then(function(response) {
          onSuccess(response.data)
        });
        var onSuccess = function(response) {
          var data = null;
          if (response.length > 0) {
            var data = {};
            data.options = [];
            var defaultoption = {};
            response.sort(sortFactory.sortByProperty("name"));
            defaultoption.name = response[0].name;
            defaultoption.uuid = response[0].uuid;
            data.defaultoption = defaultoption;
            for (var i = 0; i < response.length; i++) {
              var datajosn = {}
              datajosn.name = response[i].name;
              datajosn.uuid = response[i].uuid;
              data.options[i] = datajosn
            }
          }
          deferred.resolve({
            data: data
          })
        }
        return deferred.promise;
      }
    });
