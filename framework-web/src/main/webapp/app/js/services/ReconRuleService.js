/**
 *
 */
ReconModule = angular.module('ReconModule');

ReconModule.factory('ReconRuleFactory', function ($http, $location) {
    var factory = {};

    factory.findLatestByUuid = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
            method: "GET",
        }).then(function (response) { return response })


    }

    factory.findAllVersionByUuid = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.findOneByUuidAndVersion = function (uuid, version, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,

        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.findOneById = function (id, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "common/getOneById?action=view&id=" + id + "&type=" + type,
        }).
            then(function (response, status, headers) {
                return response;
            })

    }

    factory.findDatapodByRelation = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=datapod",
        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.findDatapodByDataset = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid + "&type=" + type,

        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.findDatapodByDatapod = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=" + type,
        }).
            then(function (response, status, headers) {
                return response;
            })
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

    factory.findAllLatest = function (type, inputFlag) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "common/getAllLatest?action=view&type=" + type + "&inputFlag=" + inputFlag,
        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.submit = function (data,type,upd_tag) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "common/submit?action=edit&type=" + type+"&upd_tag="+upd_tag,
            headers: {
                'Accept': '*/*',
                'content-Type': "application/json",
            },
            method: "POST",
            data: JSON.stringify(data),
        }).success(function (response) { return response })
    }
    
    factory.findFunctionByCategory = function (type,category) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getFunctionByCategory?action=view&type=" + type,
        }).
        then(function (response, status, headers) {
            return response;
        })
    }
    factory.execute = function (uuid, version) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'POST',
          url: url + "recon/execute?action=execute&type=recon&uuid=" + uuid + "&version=" + version,
          headers: {
            'Accept': '*/*',
            'content-Type': "application/json",
          },
        }).
          then(function (response, status, headers) {
            return response;
          })
      }
    
    return factory;
})


ReconModule.service("ReconRuleService", function ($q, ReconRuleFactory, sortFactory) {
    
    this.execute = function (uuid,version) {
        var deferred = $q.defer();
        ReconRuleFactory.execute(uuid,version).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.getFunctionByCategory = function (type,category) {
        var deferred = $q.defer();
        ReconRuleFactory.findFunctionByCategory(type,category).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.getLatestByUuid = function (uuid, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }

    this.getAttributesByDatapod = function (uuid, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            var attributes = [];
            for (var j = 0; j < response.length; j++) {
                var attributedetail = {};
                attributedetail.uuid = response[j].datapodRef.uuid;
                attributedetail.datapodname = response[j].datapodRef.name;
                attributedetail.name = response[j].attributeName;
                attributedetail.dname = response[j].datapodRef.name + "." + response[j].attributeName;
                attributedetail.attributeId = response[j].attributeId;
                attributes.push(attributedetail)
            }
            deferred.resolve({
                data: attributes
            })
        }

        return deferred.promise;
    }

    this.getAllVersionByUuid = function (uuid, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }

    this.getOneByUuidAndVersion = function (uuid, version, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            var ruleJSOn = {};
            ruleJSOn.ruledata = response;
            var sourceAttr={};
            sourceAttr.uuid=response.sourceAttr.ref.uuid;
            sourceAttr.datapodname=response.sourceAttr.ref.name;
            sourceAttr.name=response.sourceAttr.attrName;
            sourceAttr.dname=response.sourceAttr.ref.name+"."+response.sourceAttr.attrName;
            sourceAttr.attributeId=response.sourceAttr.attrId;
            ruleJSOn.sourceAttr=sourceAttr;

            var targetAttr={};
            targetAttr.uuid=response.targetAttr.ref.uuid;
            targetAttr.datapodname=response.targetAttr.ref.name;
            targetAttr.name=response.targetAttr.attrName;
            targetAttr.dname=response.targetAttr.ref.name+"."+response.targetAttr.attrName;
            targetAttr.attributeId=response.targetAttr.attrId;
            ruleJSOn.targetAttr=targetAttr;
            var filterInfoArray = [];
            if (response.sourcefilter    != null) {
                for (var i = 0; i < response.sourcefilter.filterInfo.length; i++) {
                    var filterInfo = {};
                    var lhsFilter = {}
                    filterInfo.logicalOperator = response.sourcefilter.filterInfo[i].logicalOperator
                    filterInfo.operator = response.sourcefilter.filterInfo[i].operator;
                    lhsFilter.uuid = response.sourcefilter.filterInfo[i].operand[0].ref.uuid;
                    lhsFilter.datapodname = response.sourcefilter.filterInfo[i].operand[0].ref.name;
                    lhsFilter.name = response.sourcefilter.filterInfo[i].operand[0].attributeName;
                    lhsFilter.attributeId = response.sourcefilter.filterInfo[i].operand[0].attributeId;
                    filterInfo.lhsFilter = lhsFilter;
                    filterInfo.filtervalue = response.sourcefilter.filterInfo[i].operand[1].value;
                    filterInfoArray[i] = filterInfo
                }
            }
            ruleJSOn.sourceFilterInfo = filterInfoArray;

            var targeFilterInfo = [];
            if (response.targetfilter    != null) {
                for (var i = 0; i < response.targetfilter.filterInfo.length; i++) {
                    var filterInfo = {};
                    var lhsFilter = {}
                    filterInfo.logicalOperator = response.targetfilter.filterInfo[i].logicalOperator
                    filterInfo.operator = response.targetfilter.filterInfo[i].operator;
                    lhsFilter.uuid = response.targetfilter.filterInfo[i].operand[0].ref.uuid;
                    lhsFilter.datapodname = response.targetfilter.filterInfo[i].operand[0].ref.name;
                    lhsFilter.name = response.targetfilter.filterInfo[i].operand[0].attributeName;
                    lhsFilter.attributeId = response.targetfilter.filterInfo[i].operand[0].attributeId;
                    filterInfo.lhsFilter = lhsFilter;
                    filterInfo.filtervalue = response.targetfilter.filterInfo[i].operand[1].value;
                    targeFilterInfo[i] = filterInfo
                }
            }
            ruleJSOn.targetFilterInfo = targeFilterInfo;
            deferred.resolve({
                data: ruleJSOn
            })
        }
        return deferred.promise;
    }

    this.getOneById = function (id, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.submit = function (data,type,upd_tag) {
        var deferred = $q.defer();
        ReconRuleFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        var onError = function (response) {
            deferred.reject({
                data: response
            })
        }
        return deferred.promise;
    }

    this.getAllAttributeBySource = function (uuid, type) {
        var deferred = $q.defer();
        if (type == "relation") {
            ReconRuleFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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
                deferred.resolve({
                    data: attributes
                })
            }
        }
        if (type == "dataset") {
            ReconRuleFactory.findDatapodByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
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
        if (type == "rule") {
            ReconRuleFactory.findAttributesByRule(uuid, type).then(function (response) { onSuccess(response.data) });
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

        if (type == "datapod") {
            ReconRuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
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
        }
        return deferred.promise;
    }

    this.getAllLatest = function (type) {
        var deferred = $q.defer();
        ReconRuleFactory.findAllLatest(type).then(function (response) { onSuccessRelation(response.data) });
        var onSuccessRelation = function (response) {
            var data = {};
            data.options = [];
            var defaultoption = {};
            if (response.length > 0) {
                response.sort(sortFactory.sortByProperty("name"));
                defaultoption.name = response[0].name;
                defaultoption.uuid = response[0].uuid;
                defaultoption.version = response[0].version;
                data.defaultoption = defaultoption;
                for (var i = 0; i < response.length; i++) {
                    var datajosn = {}
                    datajosn.name = response[i].name;
                    datajosn.uuid = response[i].uuid;
                    datajosn.version = response[i].version;
                    data.options[i] = datajosn
                }
            }
            else {
                data = null;
            }
            deferred.resolve({
                data: data
            })
        }
        return deferred.promise;
    }

});
