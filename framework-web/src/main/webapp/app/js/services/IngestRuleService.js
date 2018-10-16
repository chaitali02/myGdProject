/**
 *
 */
DataIngestionModule = angular.module('DataIngestionModule');

DataIngestionModule.factory('IngestRuleFactory', function ($http, $location) {
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

    factory.submit = function (data, type, upd_tag) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "common/submit?action=edit&type=" + type + "&upd_tag=" + upd_tag,
            headers: {
                'Accept': '*/*',
                'content-Type': "application/json",
            },
            method: "POST",
            data: JSON.stringify(data),
        }).success(function (response) { return response })
    }

    factory.findFunctionByCategory = function (type, category) {
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
            url: url + "ingest/execute?action=execute&type=ingest&uuid=" + uuid + "&version=" + version,
            headers: {
                'Accept': '*/*',
                'content-Type': "application/json",
            },
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findFormulaByType = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getFormulaByType?action=view&uuid=" + uuid + "&type=" + type
        }).
            then(function (response, status, headers) {
                return response;
            })
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
    factory.findReconExecByReconwithParms = function (uuid, startDate, endDate) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "recon/getReconExecByRecon?action=view&uuid=" + uuid + "&startDate=" + startDate + "&endDate=" + endDate
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findReconExecByDatapod = function (uuid, startdate, enddate) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "recon/getReconExecByDatapod?action=view&uuid=" + uuid + "&startDate=" + startdate + "&endDate=" + enddate
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findNumRowsbyExec = function (uuid, version, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=" + type,
            method: "GET",
        }).then(function (response) {
            return response
        })

    }
    factory.getResults = function (uuid, version, type, mode) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "recon/getResults?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type + "&mode=" + mode + "&requestId=",
            method: "GET",
        }).then(function (response) {
            return response
        })
    }
    factory.findDatasourceByType = function (type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getDatasourceByType?action=view&&type=" + type
        }).
            then(function (response, status, headers) {
                return response;
            })
    }

    factory.findDatasourceForFile = function (type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getDatasourceForFile?type="+type+"&action=view",
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findDatasourceForStream = function (type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getDatasourceForStream?type="+type+"&action=view",
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findDatasourceForTable = function (type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url: url + "metadata/getDatasourceForTable?type=" + type + "&action=view",
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.findDatapodByDatasource = function (uuid) {
        var url = $location.absUrl().split("app")[0]
        console.log(url)
        return $http({
            method: 'GET',
            url: url + "metadata/getDatapodByDatasource?type=datasource&action=view&uuid=" + uuid,
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.getIngestByIngestExec = function (uuid,version) {
        var url = $location.absUrl().split("app")[0]
        console.log(url)
        return $http({
            method: 'GET',
            url: url + "ingest/getIngestByIngestExec?type=ingestexec&action=view&uuid=" + uuid+"&version="+version,
        }).
            then(function (response, status, headers) {
                return response;
            })
    }
    factory.getTopicList = function (uuid,version) {
        var url = $location.absUrl().split("app")[0]
        console.log(url)
        return $http({
            method: 'GET',
            url: url + "ingest/getTopicList?type=datasource&action=view&uuid=" + uuid+"&version="+version,
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
    return factory;
})


DataIngestionModule.service("IngestRuleService", function ($q, IngestRuleFactory, sortFactory) {
    this.getAllLatestFunction = function (type, inputFlag) {
		var deferred = $q.defer();
		IngestRuleFactory.findAllLatest(type, inputFlag).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
    this.getIngestByIngestExec = function (uuid,version) {
        var deferred = $q.defer();
        IngestRuleFactory.getIngestByIngestExec(uuid,version).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
        var OnSuccess = function (response) {
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
    }/*End*/
    this.getTopicList = function (uuid,version) {
        var deferred = $q.defer();
        IngestRuleFactory.getTopicList(uuid,version).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
        var OnSuccess = function (response) {
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
    }/*End*/
    this.getDatapodOrDatasetByDatasource = function (uuid,type) {
        var deferred = $q.defer();
        if(type =="datapod"){
            IngestRuleFactory.findDatapodByDatasource(uuid).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
            var OnSuccess = function (response) {
                deferred.resolve({
                    data: response
                });
            }
            var onError = function (response) {
                deferred.reject({
                    data: response
                })
            }
        }
        else if(type =="dataset"){
            IngestRuleFactory.findAllLatest(type,null).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                deferred.resolve({
				data: response
			})
		    }
        }
        return deferred.promise;
    }/*End*/

    this.getDatasourceForTable = function (type) {
        var deferred = $q.defer();
        IngestRuleFactory.findDatasourceForTable(type).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
        var OnSuccess = function (response) {
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
    }/*End*/
    this.getDatasourceForStream= function (type) {
        var deferred = $q.defer();
        IngestRuleFactory.findDatasourceForStream(type).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
        var OnSuccess = function (response) {
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
    }/*End*/

    this.getDatasourceForFile = function (type) {
        var deferred = $q.defer();
        IngestRuleFactory.findDatasourceForFile(type).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
        var OnSuccess = function (response) {
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
    }/*End*/

    this.getDatasourceByType = function (type) {
        var deferred = $q.defer();
        IngestRuleFactory.findDatasourceByType(type).then(function (response) {
            onSuccess(response.data)
        });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }

        return deferred.promise;
    }
    this.getResults = function (uuid, version, type, mode) {
        var deferred = $q.defer();
        IngestRuleFactory.getResults(uuid, version, type, mode).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
    this.getNumRowsbyExec = function (uuid, version, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findNumRowsbyExec(uuid, version, type).then(function (response) {
            onSuccess(response.data)
        });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }

        return deferred.promise;
    }
    this.getReconExecByReconwithParms = function (uuid, startDate, endDate) {
        var deferred = $q.defer();
        IngestRuleFactory.findReconExecByReconwithParms(uuid, startDate, endDate).then(function (response) {
            onSuccess(response.data)
        });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.getReconExecByDatapod = function (uuid, startDate, endDate) {
        var deferred = $q.defer();
        IngestRuleFactory.findReconExecByDatapod(uuid, startDate, endDate).then(function (response) {
            onSuccess(response.data)
        });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }

        return deferred.promise;
    }

    this.getFormulaByType = function (uuid, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.execute = function (uuid, version) {
        var deferred = $q.defer();
        IngestRuleFactory.execute(uuid, version).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.getFunctionByCategory = function (type, category) {
        var deferred = $q.defer();
        IngestRuleFactory.findFunctionByCategory(type, category).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.getLatestByUuid = function (uuid, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }

    this.getAttributesByDatapod = function (uuid, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
        IngestRuleFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }

    this.getOneByUuidAndVersion = function (uuid, version, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            var ingestJSOn = {};
            ingestJSOn.ingestData = response;
            if(ingestJSOn.ingestData.sourceExtn !=null){
            ingestJSOn.ingestData.sourceExtn=ingestJSOn.ingestData.sourceExtn.toLowerCase();
            }
            if(ingestJSOn.ingestData.targetExtn !=null){
                ingestJSOn.ingestData.targetExtn=ingestJSOn.ingestData.targetExtn.toLowerCase();
            }
            var filterInfoArray = [];
            if (response.filter != null) {
                for (i = 0; i < response.filter.filterInfo.length; i++) {
                    var filterInfo = {};
                    filterInfo.logicalOperator = response.filter.filterInfo[i].logicalOperator;
                    filterInfo.operator = response.filter.filterInfo[i].operator;
                    if (response.filter.filterInfo[i].operand[0].ref.type == "simple") {
                        var obj = {}
                        obj.text = "string"
                        obj.caption = "string"
                        filterInfo.lhstype = obj;
                        filterInfo.islhsSimple = true;
                        filterInfo.islhsDatapod = false;
                        filterInfo.islhsFormula = false;
                        filterInfo.lhsvalue = response.filter.filterInfo[i].operand[0].value;
                    }
                    else if (response.filter.filterInfo[i].operand[0].ref.type == "datapod" || response.filter.filterInfo[i].operand[0].ref.type == "dataset") {
                        var lhsdatapodAttribute = {}
                        var obj = {}
                        obj.text = "datapod"
                        obj.caption = "attribute"
                        filterInfo.lhstype = obj;
                        filterInfo.islhsSimple = false;
                        filterInfo.islhsFormula = false
                        filterInfo.islhsDatapod = true;
                        lhsdatapodAttribute.uuid = response.filter.filterInfo[i].operand[0].ref.uuid;
                        lhsdatapodAttribute.type = response.filter.filterInfo[i].operand[0].ref.type;
                        lhsdatapodAttribute.datapodname = response.filter.filterInfo[i].operand[0].ref.name;
                        lhsdatapodAttribute.name = response.filter.filterInfo[i].operand[0].attributeName;
                        lhsdatapodAttribute.dname = response.filter.filterInfo[i].operand[0].ref.name + "." + response.filter.filterInfo[i].operand[0].attributeName;
                        lhsdatapodAttribute.attributeId = response.filter.filterInfo[i].operand[0].attributeId;
                        filterInfo.lhsdatapodAttribute = lhsdatapodAttribute;
                    }
                    else if (response.filter.filterInfo[i].operand[0].ref.type == "formula") {
                        var lhsformula = {}
                        var obj = {}
                        obj.text = "formula"
                        obj.caption = "formula"
                        filterInfo.lhstype = obj;
                        filterInfo.islhsFormula = true;
                        filterInfo.islhsSimple = false;
                        filterInfo.islhsDatapod = false;
                        lhsformula.uuid = response.filter.filterInfo[i].operand[0].ref.uuid;
                        lhsformula.type = response.filter.filterInfo[i].operand[0].ref.type;
                        lhsformula.name = response.filter.filterInfo[i].operand[0].ref.name;
                        filterInfo.lhsformula = lhsformula;
                    }
                    if (response.filter.filterInfo[i].operand[1].ref.type == "simple") {
                        var obj = {}
                        obj.text = "string"
                        obj.caption = "string"
                        filterInfo.rhstype = obj;
                        filterInfo.isrhsSimple = true;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsFormula = false;
                        filterInfo.rhsvalue = response.filter.filterInfo[i].operand[1].value;
                    }
                    else if (response.filter.filterInfo[i].operand[1].ref.type == "datapod") {
                        var rhsdatapodAttribute = {}
                        var obj = {}
                        obj.text = "datapod"
                        obj.caption = "attribute"
                        filterInfo.rhstype = obj;
                        filterInfo.isrhsSimple = false;
                        filterInfo.isrhsFormula = false
                        filterInfo.isrhsDatapod = true;
                        rhsdatapodAttribute.uuid = response.filter.filterInfo[i].operand[1].ref.uuid;
                        rhsdatapodAttribute.type = response.filter.filterInfo[i].operand[1].ref.type;
                        rhsdatapodAttribute.datapodname = response.filter.filterInfo[i].operand[1].ref.name;
                        rhsdatapodAttribute.name = response.filter.filterInfo[i].operand[1].attributeName;
                        rhsdatapodAttribute.dname = response.filter.filterInfo[i].operand[1].ref.name + "." + response.filter.filterInfo[i].operand[1].attributeName;
                        rhsdatapodAttribute.attributeId = response.filter.filterInfo[i].operand[1].attributeId;
                        filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
                    }
                    else if (response.filter.filterInfo[i].operand[1].ref.type == "dataset" && response.filter.dependsOn.ref.uuid == response.filter.filterInfo[i].operand[1].ref.uuid) {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
						filterInfo.isrhsDatapod = true;
						filterInfo.isrhsDataset = false;
						rhsdatapodAttribute.uuid =response.filter.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.filter.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.filter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.filter.filterInfo[i].operand[1].ref.name + "." +response.filter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.filter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
					}
                    else if (response.filter.filterInfo[i].operand[1].ref.type == "formula") {
                        var rhsformula = {}
                        var obj = {}
                        obj.text = "formula"
                        obj.caption = "formula"
                        filterInfo.rhstype = obj;
                        filterInfo.isrhsFormula = true;
                        filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        rhsformula.uuid = response.filter.filterInfo[i].operand[1].ref.uuid;
                        rhsformula.type = response.filter.filterInfo[i].operand[1].ref.type;
                        rhsformula.name = response.filter.filterInfo[i].operand[1].ref.name;
                        filterInfo.rhsformula = rhsformula;
                    }
                    else if (response.filter.filterInfo[i].operand[1].ref.type == "dataset") {
						var rhsdataset = {}
						var obj = {}
						obj.text = "dataset"
						obj.caption = "dataset"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsDatapod = false;
						filterInfo.isrhsDataset = true;
						rhsdataset.uuid = response.filter.filterInfo[i].operand[1].ref.uuid;
						rhsdataset.datapodname = response.filter.filterInfo[i].operand[1].ref.name;
						rhsdataset.name = response.filter.filterInfo[i].operand[1].attributeName;
						rhsdataset.dname = response.filter.filterInfo[i].operand[1].ref.name + "." + response.filter.filterInfo[i].operand[1].attributeName;
						rhsdataset.attributeId = response.filter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdataset = rhsdataset;
                    }
                    else if (response.filter.filterInfo[i].operand[1].ref.type == "paramlist") {
                        var rhsparamlist = {}
                        var obj = {}
                        obj.text = "paramlist"
                        obj.caption = "paramlist"
                        filterInfo.rhstype = obj;
                        filterInfo.isrhsFormula = false;
                        filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = true;
                        filterInfo.isrhsFunction = false;
                        rhsparamlist.uuid = response.filter.filterInfo[i].operand[1].ref.uuid;
                        rhsparamlist.datapodname = response.filter.filterInfo[i].operand[1].ref.name;
                        rhsparamlist.name = response.filter.filterInfo[i].operand[1].attributeName;
                        rhsparamlist.dname = response.filter.filterInfo[i].operand[1].ref.name + "." + response.filter.filterInfo[i].operand[1].attributeName;
                        rhsparamlist.attributeId = response.filter.filterInfo[i].operand[1].attributeId;
                    
                        filterInfo.rhsparamlist = rhsparamlist;
                      }
                    filterInfoArray[i] = filterInfo
                }
            }

            ingestJSOn.filterInfo = filterInfoArray
            var attributeArray = [];
            if(response.attributeMap){
                for (var i = 0; i < response.attributeMap.length; i++) {
                    var attributemapjson = {};
                    if (response.attributeMap[i].sourceAttr.ref.type == "datapod" || response.attributeMap[i].sourceAttr.ref.type == "dataset" || response.attributeMap[i].sourceAttr.ref.type == "rule") {
                        var sourceattribute = {}
                        sourceattribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
                        sourceattribute.type = response.attributeMap[i].sourceAttr.ref.type;
                        sourceattribute.attributeId = response.attributeMap[i].sourceAttr.attrId;
                        attributemapjson.sourceattribute=sourceattribute;
                        var obj = {}
                        obj.text = "datapod"
                        obj.caption = "attribute"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.isSourceAtributeSimple = false;
                        attributemapjson.isSourceAtributeDatapod = true;
                        attributemapjson.isSourceAtributeFormula = false;
                        attributemapjson.isSourceAtributeExpression = false;

                    }
                    else if (response.attributeMap[i].sourceAttr.ref.type == "simple") {
                        var obj = {}
                        obj.text = "string"
                        obj.caption = "string"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.isSourceAtributeSimple = true;
                        attributemapjson.sourcesimple = response.attributeMap[i].sourceAttr.value
                        attributemapjson.isSourceAtributeDatapod = false;
                        attributemapjson.isSourceAtributeFormula = false;
                        attributemapjson.isSourceAtributeExpression = false;
                        attributemapjson.isSourceAtributeFunction = false;

                    }
                    if (response.attributeMap[i].sourceAttr.ref.type == "expression") {
                        var sourceexpression = {};
                        sourceexpression.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
                        sourceexpression.name = "";
                        var obj = {}
                        obj.text = "expression"
                        obj.caption = "expression"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.sourceexpression = sourceexpression;
                        attributemapjson.isSourceAtributeSimple = false;
                        attributemapjson.isSourceAtributeDatapod = false;
                        attributemapjson.isSourceAtributeFormula = false;
                        attributemapjson.isSourceAtributeExpression = true;
                        attributemapjson.isSourceAtributeFunction = false;
                    }
                    if (response.attributeMap[i].sourceAttr.ref.type == "formula") {
                        var sourceformula = {};
                        sourceformula.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
                        sourceformula.name = "";
                        var obj = {}
                        obj.text = "formula"
                        obj.caption = "formula"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.sourceformula = sourceformula;
                        attributemapjson.isSourceAtributeSimple = false;
                        attributemapjson.isSourceAtributeDatapod = false;
                        attributemapjson.isSourceAtributeFormula = true;
                        attributemapjson.isSourceAtributeExpression = false;
                        attributemapjson.isSourceAtributeFunction = false;
                    }
                    if (response.attributeMap[i].sourceAttr.ref.type == "function") {
                        var sourcefunction = {};
                        sourcefunction.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
                        sourcefunction.name = "";
                        var obj = {}
                        obj.text = "function"
                        obj.caption = "function"
                        attributemapjson.sourceAttributeType = obj;
                        attributemapjson.sourcefunction = sourcefunction;
                        attributemapjson.isSourceAtributeSimple = false;
                        attributemapjson.isSourceAtributeDatapod = false;
                        attributemapjson.isSourceAtributeFormula = false;
                        attributemapjson.isSourceAtributeExpression = false;
                        attributemapjson.isSourceAtributeFunction = true;
                    }
                    attributemapjson.sourceattribute = sourceattribute;
                    
                    if(response.attributeMap[i].targetAttr.ref.type !="simple"){
                        var targetattribute = {}
                        targetattribute.uuid = response.attributeMap[i].targetAttr.ref.uuid;
                        targetattribute.name = response.attributeMap[i].targetAttr.ref.name;
                        targetattribute.dname = response.attributeMap[i].targetAttr.ref.name+"."+response.attributeMap[i].targetAttr.attrName;
                        targetattribute.type = response.attributeMap[i].targetAttr.ref.type;
                        targetattribute.attributeId = response.attributeMap[i].targetAttr.attrId;
                        targetattribute.attrName=response.attributeMap[i].targetAttr.attrName;
                        attributemapjson.targetattribute = targetattribute;
                        attributemapjson.isTargetAtributeSimple = false;
                        attributemapjson.isTargetAtributeDatapod = true;
                    }else{
                        var targetsimple=response.attributeMap[i].targetAttr.value;
                        attributemapjson.targetsimple = targetsimple;
                        attributemapjson.isTargetAtributeSimple = true;
                        attributemapjson.isTargetAtributeDatapod = false;

                    }
                    
                    attributeArray[i] = attributemapjson
                }
            }
            ingestJSOn.ingesttabalearray = attributeArray;
            console.log(ingestJSOn.ingesttabalearray)
            deferred.resolve({
                data: ingestJSOn
            })
        }
        return deferred.promise;
    }

    this.getOneById = function (id, type) {
        var deferred = $q.defer();
        IngestRuleFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    }
    this.submit = function (data, type, upd_tag) {
        var deferred = $q.defer();
        IngestRuleFactory.submit(data, type, upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
            IngestRuleFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                var attributes = [];
                for (var j = 0; j < response.length; j++) {
                    for (var i = 0; i < response[j].attributes.length; i++) {
                        var attributedetail = {};
                        attributedetail.uuid = response[j].uuid;
                        attributedetail.type = response[j].type;
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
            IngestRuleFactory.findDatapodByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                var attributes = [];
                for (var j = 0; j < response.length; j++) {
                    var attributedetail = {};
                    attributedetail.uuid = response[j].ref.uuid;
                    attributedetail.type = response[j].ref.type;
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
            IngestRuleFactory.findAttributesByRule(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                var attributes = [];
                for (var j = 0; j < response.length; j++) {
                    var attributedetail = {};
                    attributedetail.uuid = response[j].ref.uuid;
                    attributedetail.type = response[j].ref.type;
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
            IngestRuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                var attributes = [];
                for (var j = 0; j < response.length; j++) {
                    var attributedetail = {};
                    attributedetail.uuid = response[j].ref.uuid;
                    attributedetail.type = response[j].ref.type;
                    attributedetail.datapodname = response[j].ref.name;
                    attributedetail.name = response[j].attrName;
                    attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
                    attributedetail.attrName =response[j].attrName;
                    attributedetail.attributeId = response[j].attrId;
                    attributes.push(attributedetail)
                }
                deferred.resolve({
                    data: attributes
                })
            }
        }
        if (type == "paramlist") {
            IngestRuleFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
            var onSuccess = function (response) {
                var attributes = [];
                for (var j = 0; j < response.length; j++) {
                    var attributedetail = {};
                    attributedetail.uuid = response[j].ref.uuid;
                    attributedetail.type = response[j].ref.type;
                    attributedetail.datapodname = response[j].ref.name;
                    attributedetail.name = response[j].paramName;
                    attributedetail.dname = response[j].paramName //response[j].ref.name + "." + response[j].paramName;
                    attributedetail.attributeId = response[j].paramId;
                    attributes.push(attributedetail);
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
        IngestRuleFactory.findAllLatest(type).then(function (response) { onSuccessRelation(response.data) });
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
