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
    return factory;
})


ReconModule.service("ReconRuleService", function ($q, ReconRuleFactory, sortFactory) {
    this.getFormulaByType = function (uuid, type) {
        var deferred = $q.defer();
        ReconRuleFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          deferred.resolve({
            data: response
          })
        }
        return deferred.promise;
    }
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
            if(response.sourcefilter !=null){
				for (i = 0; i <response.sourcefilter.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator =response.sourcefilter.filterInfo[i].logicalOperator;
					filterInfo.operator =response.sourcefilter.filterInfo[i].operator;
					if (response.sourcefilter.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
                        filterInfo.lhsvalue =response.sourcefilter.filterInfo[i].operand[0].value;
                        if(response.sourcefilter.filterInfo[i].operand[0].value.indexOf("'") ==-1){
							obj.caption = "integer";
						}
					}
					else if (response.sourcefilter.filterInfo[i].operand[0].ref.type == "datapod" || response.sourcefilter.filterInfo[i].operand[0].ref.type == "dataset") {
						var lhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = false;
						filterInfo.islhsFormula = false
						filterInfo.islhsDatapod = true;
						lhsdatapodAttribute.uuid =response.sourcefilter.filterInfo[i].operand[0].ref.uuid;
						lhsdatapodAttribute.datapodname =response.sourcefilter.filterInfo[i].operand[0].ref.name;
						lhsdatapodAttribute.name =response.sourcefilter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.dname =response.sourcefilter.filterInfo[i].operand[0].ref.name + "." +response.sourcefilter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.attributeId =response.sourcefilter.filterInfo[i].operand[0].attributeId;
						filterInfo.lhsdatapodAttribute = lhsdatapodAttribute;
					}
					else if (response.sourcefilter.filterInfo[i].operand[0].ref.type == "formula") {
						var lhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.lhstype = obj;
						filterInfo.islhsFormula = true;
						filterInfo.islhsSimple = false;
						filterInfo.islhsDatapod = false;
						lhsformula.uuid =response.sourcefilter.filterInfo[i].operand[0].ref.uuid;
						lhsformula.name =response.sourcefilter.filterInfo[i].operand[0].ref.name;
						filterInfo.lhsformula = lhsformula;
					}
					if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = true;
						filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsFormula = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
                        filterInfo.rhsvalue =response.sourcefilter.filterInfo[i].operand[1].value;
                        if(response.sourcefilter.filterInfo[i].operator =="BETWEEN"){
							obj.caption = "integer";
							filterInfo.rhsvalue1=response.sourcefilter.filterInfo[i].operand[1].value.split("and")[0];
							filterInfo.rhsvalue2=response.sourcefilter.filterInfo[i].operand[1].value.split("and")[1];	
						}else if(['<','>',"<=",'>='].indexOf(response.sourcefilter.filterInfo[i].operator) !=-1){
							obj.caption = "integer";
							
						}else if(response.sourcefilter.filterInfo[i].operator =='=' && response.sourcefilter.filterInfo[i].operand[1].value.indexOf("'") ==-1){
							obj.caption = "integer";
							filterInfo.rhsvalue = response.sourcefilter.filterInfo[i].operand[1].value
						}
						else{
						filterInfo.rhsvalue = response.sourcefilter.filterInfo[i].operand[1].value//.replace(/["']/g, "");
						}
					}
					else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "datapod") {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
                        filterInfo.isrhsDatapod = true;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdatapodAttribute.uuid =response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.sourcefilter.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.sourcefilter.filterInfo[i].operand[1].ref.name + "." +response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.sourcefilter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
                    }
                    else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "dataset" && response.sourcefilter.dependsOn.ref.uuid == response.sourcefilter.filterInfo[i].operand[1].ref.uuid) {
                        var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
                        filterInfo.isrhsDatapod = true;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdatapodAttribute.uuid =response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.sourcefilter.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.sourcefilter.filterInfo[i].operand[1].ref.name + "." +response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.sourcefilter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;

                    }
                   
					else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "formula") {
						var rhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = true;
						filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsformula.uuid =response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsformula.name =response.sourcefilter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
                    }
                    else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "function") {
						var rhsfunction = {}
						var obj = {}
						obj.text = "function"
						obj.caption = "function"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = true;
						rhsfunction.uuid =response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsfunction.name =response.sourcefilter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsfunction = rhsfunction;
                    }
                    else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "dataset" && response.sourcefilter.dependsOn.ref.uuid != response.sourcefilter.filterInfo[i].operand[1].ref.uuid) {
						var rhsdataset = {}
						var obj = {}
						obj.text = "dataset"
						obj.caption = "dataset"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = true;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdataset.uuid = response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsdataset.datapodname = response.sourcefilter.filterInfo[i].operand[1].ref.name;
						rhsdataset.name = response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdataset.dname = response.sourcefilter.filterInfo[i].operand[1].ref.name + "." + response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsdataset.attributeId = response.sourcefilter.filterInfo[i].operand[1].attributeId;
				
						filterInfo.rhsdataset = rhsdataset;
                    }
                    else if (response.sourcefilter.filterInfo[i].operand[1].ref.type == "paramlist") {
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
						rhsparamlist.uuid = response.sourcefilter.filterInfo[i].operand[1].ref.uuid;
						rhsparamlist.datapodname = response.sourcefilter.filterInfo[i].operand[1].ref.name;
						rhsparamlist.name = response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsparamlist.dname = response.sourcefilter.filterInfo[i].operand[1].ref.name + "." + response.sourcefilter.filterInfo[i].operand[1].attributeName;
						rhsparamlist.attributeId = response.sourcefilter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsparamlist = rhsparamlist;
					}
					filterInfoArray[i] = filterInfo
				}
		  }
            // if (response.sourcefilter!= null) {
            //     for (var i = 0; i < response.sourcefilter.filterInfo.length; i++) {
            //         var filterInfo = {};
            //         var lhsFilter = {}
            //         filterInfo.logicalOperator = response.sourcefilter.filterInfo[i].logicalOperator
            //         filterInfo.operator = response.sourcefilter.filterInfo[i].operator;
            //         lhsFilter.uuid = response.sourcefilter.filterInfo[i].operand[0].ref.uuid;
            //         lhsFilter.datapodname = response.sourcefilter.filterInfo[i].operand[0].ref.name;
            //         lhsFilter.name = response.sourcefilter.filterInfo[i].operand[0].attributeName;
            //         lhsFilter.attributeId = response.sourcefilter.filterInfo[i].operand[0].attributeId;
            //         filterInfo.lhsFilter = lhsFilter;
            //         filterInfo.filtervalue = response.sourcefilter.filterInfo[i].operand[1].value;
            //         filterInfoArray[i] = filterInfo
            //     }
            // }
            ruleJSOn.sourceFilterInfo = filterInfoArray;
            var targeFilterInfo = [];
            if(response.targetfilter !=null){
				for (i = 0; i <response.targetfilter.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator =response.targetfilter.filterInfo[i].logicalOperator;
					filterInfo.operator =response.targetfilter.filterInfo[i].operator;
					if (response.targetfilter.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
                        filterInfo.lhsvalue =response.targetfilter.filterInfo[i].operand[0].value;
                        if(response.targetfilter.filterInfo[i].operand[0].value.indexOf("'") ==-1){
							obj.caption = "integer";
						}
					}
					else if (response.targetfilter.filterInfo[i].operand[0].ref.type == "datapod" || response.targetfilter.filterInfo[i].operand[0].ref.type == "dataset") {
						var lhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = false;
						filterInfo.islhsFormula = false
						filterInfo.islhsDatapod = true;
						lhsdatapodAttribute.uuid =response.targetfilter.filterInfo[i].operand[0].ref.uuid;
						lhsdatapodAttribute.datapodname =response.targetfilter.filterInfo[i].operand[0].ref.name;
						lhsdatapodAttribute.name =response.targetfilter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.dname =response.targetfilter.filterInfo[i].operand[0].ref.name + "." +response.targetfilter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.attributeId =response.targetfilter.filterInfo[i].operand[0].attributeId;
						filterInfo.lhsdatapodAttribute = lhsdatapodAttribute;
					}
					else if (response.targetfilter.filterInfo[i].operand[0].ref.type == "formula") {
						var lhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.lhstype = obj;
						filterInfo.islhsFormula = true;
						filterInfo.islhsSimple = false;
						filterInfo.islhsDatapod = false;
						lhsformula.uuid =response.targetfilter.filterInfo[i].operand[0].ref.uuid;
						lhsformula.name =response.targetfilter.filterInfo[i].operand[0].ref.name;
						filterInfo.lhsformula = lhsformula;
                    }
                  
					if (response.targetfilter.filterInfo[i].operand[1].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = true;
						filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsFormula = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.rhsvalue =response.targetfilter.filterInfo[i].operand[1].value;
                        if(response.targetfilter.filterInfo[i].operator =="BETWEEN"){
							obj.caption = "integer";
							filterInfo.rhsvalue1=response.targetfilter.filterInfo[i].operand[1].value.split("and")[0];
							filterInfo.rhsvalue2=response.targetfilter.filterInfo[i].operand[1].value.split("and")[1];	
						}else if(['<','>',"<=",'>='].indexOf(response.targetfilter.filterInfo[i].operator) !=-1){
							obj.caption = "integer";
							
						}else if(response.targetfilter.filterInfo[i].operator =='=' && response.targetfilter.filterInfo[i].operand[1].value.indexOf("'") ==-1){
							obj.caption = "integer";
							filterInfo.rhsvalue = response.targetfilter.filterInfo[i].operand[1].value
						}
						else{
						filterInfo.rhsvalue = response.targetfilter.filterInfo[i].operand[1].value//.replace(/["']/g, "");
						}
					}
					else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "datapod" ) {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
                        filterInfo.isrhsDatapod = true;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdatapodAttribute.uuid =response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.targetfilter.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.targetfilter.filterInfo[i].operand[1].ref.name + "." +response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.targetfilter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
                    }
                    else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "dataset" && response.targetfilter.dependsOn.ref.uuid == response.targetfilter.filterInfo[i].operand[1].ref.uuid) {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
                        filterInfo.isrhsDatapod = true;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdatapodAttribute.uuid =response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.targetfilter.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.targetfilter.filterInfo[i].operand[1].ref.name + "." +response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.targetfilter.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
					}
					else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "formula") {
						var rhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = true;
						filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsformula.uuid =response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsformula.name =response.targetfilter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
                    }
                    else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "function") {
                        
						var rhsfunction = {}
						var obj = {}
						obj.text = "function"
						obj.caption = "function"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
                        filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = true;
						rhsfunction.uuid =response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsfunction.name =response.targetfilter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsfunction = rhsfunction;
					}
                    else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "dataset" && response.targetfilter.dependsOn.ref.uuid != response.targetfilter.filterInfo[i].operand[1].ref.uuid) {
						var rhsdataset = {}
						var obj = {}
						obj.text = "dataset"
						obj.caption = "dataset"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsDatapod = false;
                        filterInfo.isrhsDataset = true;
                        filterInfo.isrhsParamlist = false;
                        filterInfo.isrhsFunction = false;
						rhsdataset.uuid = response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsdataset.datapodname = response.targetfilter.filterInfo[i].operand[1].ref.name;
						rhsdataset.name = response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdataset.dname = response.targetfilter.filterInfo[i].operand[1].ref.name + "." + response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsdataset.attributeId = response.targetfilter.filterInfo[i].operand[1].attributeId;
				
						filterInfo.rhsdataset = rhsdataset;
                    }
                    else if (response.targetfilter.filterInfo[i].operand[1].ref.type == "paramlist") {
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
						rhsparamlist.uuid = response.targetfilter.filterInfo[i].operand[1].ref.uuid;
						rhsparamlist.datapodname = response.targetfilter.filterInfo[i].operand[1].ref.name;
						rhsparamlist.name = response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsparamlist.dname = response.targetfilter.filterInfo[i].operand[1].ref.name + "." + response.targetfilter.filterInfo[i].operand[1].attributeName;
						rhsparamlist.attributeId = response.targetfilter.filterInfo[i].operand[1].attributeId;
				
						filterInfo.rhsparamlist = rhsparamlist;
					}
					targeFilterInfo[i] = filterInfo
				}
		  }
            // if (response.targetfilter    != null) {
            //     for (var i = 0; i < response.targetfilter.filterInfo.length; i++) {
            //         var filterInfo = {};
            //         var lhsFilter = {}
            //         filterInfo.logicalOperator = response.targetfilter.filterInfo[i].logicalOperator
            //         filterInfo.operator = response.targetfilter.filterInfo[i].operator;
            //         lhsFilter.uuid = response.targetfilter.filterInfo[i].operand[0].ref.uuid;
            //         lhsFilter.datapodname = response.targetfilter.filterInfo[i].operand[0].ref.name;
            //         lhsFilter.name = response.targetfilter.filterInfo[i].operand[0].attributeName;
            //         lhsFilter.attributeId = response.targetfilter.filterInfo[i].operand[0].attributeId;
            //         filterInfo.lhsFilter = lhsFilter;
            //         filterInfo.filtervalue = response.targetfilter.filterInfo[i].operand[1].value;
            //         targeFilterInfo[i] = filterInfo
            //     }
            // }
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
        if(type == "paramlist"){
			ReconRuleFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].paramName ;
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
