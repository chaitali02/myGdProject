/**
 *
 */
RuleModule = angular.module('RuleModule');

RuleModule.factory('RuleFactory', function ($http, $location) {
  var factory = {};

  factory.findAllLatestActive = function (metavalue) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "common/getAllLatest?action=view&active=Y&type=" + metavalue,

    }).
      then(function (response, status, headers) {
        return response;
      })
  }

  factory.findByUuid = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
      method: "GET",
    }).then(function (response) { return response })


  }
  factory.executeRule = function (uuid, version) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'POST',
      url: url + "rule/executeRule?action=execute&ruleUUID=" + uuid + "&ruleVersion=" + version,
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
    }).
      then(function (response, status, headers) {
        return response;
      })
  }

  factory.findRuleResults = function (uuid, version) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "rule/getResults?action=view&uuid=" + uuid + "&version=" + version,

    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  factory.findRuleExecByRule = function (uuid) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "rule/getRuleExecByRule?action=view&ruleUuid=" + uuid,
    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  factory.findRuleByRuleGroup = function (uuid) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "rule/getRuleByRuleGroup?type=rule&action=view&ruleGroupUuid="+uuid,
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
  factory.findByUuid = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
      method: "GET",
    }).then(function (response) { return response })


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
  factory.ruleSubmit = function (data,type,upd_tag) {
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
  factory.findRuleExecByRule = function (uuid) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "rule/getRuleExecByRule?action=view&ruleUuid=" + uuid
    }).
      then(function (response, status, headers) {
        return response;
      })
  }
  factory.findRuleExecByRuleWithDate = function (uuid,startdate,enddate) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "rule/getRuleExecByRule?action=view&ruleUuid="+uuid+"&startDate="+startdate+"&endDate="+enddate
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
  factory.findExpressionByType = function (uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      method: 'GET',
      url: url + "metadata/getExpressionByType?action=view&uuid=" + uuid + "&type=" + type
    }).
      then(function (response, status, headers) {
        return response;
      })
  }

  factory.findGraphData = function (uuid, version, degree) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "graph/getGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree,
      method: "GET"
    }).then(function (response) { return response })
  };

  factory.findSaveAs = function (uuid, version, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/saveAs?action=clone&uuid=" + uuid + "&version=" + version + "&type=" + type,
      method: "GET",
    }).then(function (response) { return response })
  }

  factory.findParamSetByParamList = function (uuid, version) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getParamSetByParamList?action=view&paramListUuid=" + uuid + "&type=rule",
      method: "GET"
    }).then(function (response) { return response })
  };
  
  factory.findParamSetByRule = function (uuid, version) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getParamSetByRule?action=view&ruleUuid=" + uuid,
      method: "GET"
    }).then(function (response) { return response })
  };
  factory.findNumRowsbyExec = function (uuid, version,type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "metadata/getNumRowsbyExec?action=view&execUuid="+uuid+"&execVersion="+version+"&type="+type,
      method: "GET"
    }).then(function (response) { return response })
  };
  factory.findexecuteRuleWithParams = function (uuid, version, data) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "rule/execute/?action=execute&uuid=" + uuid + "&version=" + version,
      method: "POST",
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
    }).then(function (response) { return response })
  };
  factory.findexecuteRuleWithParamsBody = function (uuid, version, data) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "rule/execute/?action=execute&uuid=" + uuid + "&version=" + version,
      method: "POST",
      headers: {
        'Accept': '*/*',
        'content-Type': "application/json",
      },
      method: "POST",
      data: JSON.stringify(data),
    }).then(function (response) { return response })
  };
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
  factory.findRuleResults = function(url) {
    var baseurl = $location.absUrl().split("app")[0] + url;
    return $http({
      method: 'GET',
      url: baseurl,
    }).
    then(function(response, status, headers) {
      return response;
    })
  }
  factory.findAttributesByRelation = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByRelation?action=view&uuid=" + uuid + "&type=" + type,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
  factory.disableRhsType=function(arrayStr){
    var rTypes=[
      { "text": "string", "caption": "string","disabled":false },
      { "text": "string", "caption": "integer" ,"disabled":false },
      { "text": "datapod", "caption": "attribute","disabled":false },
      { "text": "formula", "caption": "formula","disabled":false },
      { "text": "dataset", "caption": "dataset" ,"disabled":false },
      { "text":  "paramlist", "caption": "paramlist" ,"disabled":false },
      { "text": "function", "caption": "function" ,"disabled":false }]
    for(var i=0;i<rTypes.length;i++){
      rTypes[i].disabled=false;
      if(arrayStr.length >0){
        var index=arrayStr.indexOf(rTypes[i].caption);
        if(index !=-1){
          rTypes[i].disabled=true;
        }
      }
    }
    return rTypes;
  }
  return factory;
})


RuleModule.factory("RuleService", function ($q, RuleFactory, sortFactory,CF_FILTER, CF_GRID) {
  var factory = {};

  factory.executeRuleWithParams = function (uuid, version, data) {
    var deferred = $q.defer();
    if (data != null) {
      RuleFactory.findexecuteRuleWithParamsBody(uuid, version, data).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        deferred.resolve({
          data: response
        });
      }
    }
    else {
      RuleFactory.findexecuteRuleWithParams(uuid, version).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        deferred.resolve({
          data: response
        });
      }
    }
    return deferred.promise;
  }
  
  factory.getNumRowsbyExec = function (uuid,version,type) {
    var deferred = $q.defer();
    RuleFactory.findNumRowsbyExec(uuid,version,type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  factory.getRuleByRuleGroup = function (uuid) {
    var deferred = $q.defer();
    RuleFactory.findRuleByRuleGroup(uuid).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  factory.getParamSetByRule = function (uuid, version) {
    var deferred = $q.defer();
    RuleFactory.findParamSetByRule(uuid, version).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  factory.getParamSetByParamList = function (uuid, version) {
    var deferred = $q.defer();
    RuleFactory.findParamSetByParamList(uuid, version).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  factory.saveAs = function (uuid, version, type) {
    var deferred = $q.defer();
    RuleFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }

  factory.getAllLatestRule = function (metavalue) {
    var deferred = $q.defer();
    RuleFactory.findAllLatest(metavalue).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var rowDataSet = [];
      var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn']
      for (var i=0;i<response.length;i++) {
        var rowData = [];
        for (var j = 0; j < headerColumns.length; j++) {
          var columnname = headerColumns[j]
          if (columnname == "createdBy") {
            rowData[j] = response[i].createdBy.ref.name;
          }
          else {
            rowData[j] = response[i][columnname];
          }
        }
        rowDataSet[i] = rowData;
      }
      deferred.resolve({
        data: rowDataSet
      })
    }
    return deferred.promise;
  }

  factory.getAll = function (type) {
    var deferred = $q.defer();
    RuleFactory.findAll(type).then(function (response) { onSuccess(response) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }
  
  factory.getRuleExecByRulewithDate = function (uuid,startDate,endDate) {
    var deferred = $q.defer();
    RuleFactory.findRuleExecByRuleWithDate(uuid,startDate,endDate).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.getRuleExecByRule = function (uuid) {
    var deferred = $q.defer();
    RuleFactory.findRuleExecByRule(uuid).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }


  factory.getOneByUuid = function (id, type) {
    var deferred = $q.defer();
    RuleFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.executeRule = function (uuid, version) {
    var deferred = $q.defer();
    RuleFactory.executeRule(uuid, version).then(function (response) { onSuccess(response) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  this.getAll = function (metavalue, sessionid) {
    var deferred = $q.defer();
    RuleFactory.findAll(metavalue).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var rowDataSet = [];
      var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn']
      for (var i = 0; i < response.length; i++) {
        var rowData = [];
        for (var j = 0; j < headerColumns.length; j++) {
          var columnname = headerColumns[j]
          if (columnname == "createdBy") {
            rowData[j] = response[i].createdBy.ref.name;
          }
          else {
            rowData[j] = response[i][columnname];
          }
        }
        rowDataSet[i] = rowData;
      }
      deferred.resolve({
        data: rowDataSet
      })
    }
    return deferred.promise;
  }

  factory.getAttributesByDatapod = function (uuid, type) {
    var deferred = $q.defer();
    RuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var attributes = [];
      for (var j = 0; j < response.length; j++) {
        var attributedetail = {};
        attributedetail.uuid = response[j].datapodRef.uuid;
        attributedetail.uuid = response[j].datapodRef.type;
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
  

  factory.getFormulaByType = function (uuid, type) {
    var deferred = $q.defer();
    RuleFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.getExpressionByType = function (uuid, type) {
    var deferred = $q.defer();
    RuleFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.getAllVersionByUuid = function (uuid, type) {
    var deferred = $q.defer();
    RuleFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }
  
  factory.getOneByUuidAndVersion = function (uuid, version, type) {
    var deferred = $q.defer();
    RuleFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
     
     
      var ruleJSOn = {};
      ruleJSOn.ruledata = response;
      var filterInfoArray = [];
      if(response.filterInfo !=null){
				for (i = 0; i <response.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator =response.filterInfo[i].logicalOperator;
          filterInfo.operator =response.filterInfo[i].operator;
          var rhsTypes=null;
          filterInfo.rhsTypes=null;
          if(filterInfo.operator =='BETWEEN'){
            filterInfo.rhsTypes =RuleFactory.disableRhsType(['attribute','formula','dataset','function','paramlist'])
          }else if(['IN','NOT IN'].indexOf(filterInfo.operator) !=-1){
            filterInfo.rhsTypes=RuleFactory.disableRhsType([]);
          }else if(['<','>',"<=",'>='].indexOf(filterInfo.operator) !=-1){
            filterInfo.rhsTypes=RuleFactory.disableRhsType(['dataset']);
          }
          else if (['EXISTS', 'NOT EXISTS'].indexOf(filterInfo.operator) != -1) {
						filterInfo.rhsTypes = RuleFactory.disableRhsType(['attribute', 'formula', 'function', 'paramlist','string','integer']);
					}
					else if (['IS'].indexOf(filterInfo.operator) != -1){
						
						filterInfo.rhsTypes = RuleFactory.disableRhsType(['attribute', 'formula', 'dataset', 'function', 'paramlist']);
					}
          else{
            filterInfo.rhsTypes=RuleFactory.disableRhsType(['dataset']);
          }
          
					if (response.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
            filterInfo.lhsvalue =response.filterInfo[i].operand[0].value;
            if(response.filterInfo[i].operand[0].attributeType =="integer"){
							obj.caption = "integer";
						}
					}
					else if (response.filterInfo[i].operand[0].ref.type == "datapod" || response.filterInfo[i].operand[0].ref.type == "dataset" || response.filterInfo[i].operand[0].ref.type == "rule") {
						var lhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = false;
						filterInfo.islhsFormula = false
						filterInfo.islhsDatapod = true;
						lhsdatapodAttribute.uuid =response.filterInfo[i].operand[0].ref.uuid;
						lhsdatapodAttribute.datapodname =response.filterInfo[i].operand[0].ref.name;
						lhsdatapodAttribute.name =response.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.dname =response.filterInfo[i].operand[0].ref.name + "." +response.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.attributeId =response.filterInfo[i].operand[0].attributeId;
						filterInfo.lhsdatapodAttribute = lhsdatapodAttribute;
					}
					else if (response.filterInfo[i].operand[0].ref.type == "formula") {
						var lhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.lhstype = obj;
						filterInfo.islhsFormula = true;
						filterInfo.islhsSimple = false;
						filterInfo.islhsDatapod = false;
						lhsformula.uuid =response.filterInfo[i].operand[0].ref.uuid;
						lhsformula.name =response.filterInfo[i].operand[0].ref.name;
						filterInfo.lhsformula = lhsformula;
					}
					if (response.filterInfo[i].operand[1].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = true;
						filterInfo.isrhsDatapod = false;
            filterInfo.isrhsFormula = false;
            filterInfo.isrhsDataset = false;
            filterInfo.rhsvalue =response.filterInfo[i].operand[1].value;
            var temp=response.filterInfo[i].operator;
						temp=temp.replace(/ /g,'');
            if(response.filterInfo[i].operator =="BETWEEN"){
							obj.caption = "integer";
							filterInfo.rhsvalue1=response.filterInfo[i].operand[1].value.split("and")[0];
							filterInfo.rhsvalue2=response.filterInfo[i].operand[1].value.split("and")[1];	
						}else if(['<','>',"<=",'>='].indexOf(response.filterInfo[i].operator) !=-1){
							obj.caption =response.filterInfo[i].operand[1].attributeType;
              filterInfo.rhsvalue = response.filterInfo[i].operand[1].value

						}else if(response.filterInfo[i].operator =='=' && response.filterInfo[i].operand[1].attributeType =="integer"){
							obj.caption = "integer";
							filterInfo.rhsvalue = response.filterInfo[i].operand[1].value
            }
            else if(temp == "ISNULL" || temp == "ISNOTNULL" ){
							filterInfo.isRhsNA = true;
						}
						else{
						filterInfo.rhsvalue = response.filterInfo[i].operand[1].value//.replace(/["']/g, "");
						}
					}
					else if(response.filterInfo[i].operand[1].ref.type == "datapod" || response.filterInfo[i].operand[1].ref.type == "rule") {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
            filterInfo.isrhsDatapod = true;
            filterInfo.isrhsDataset = false;
						rhsdatapodAttribute.uuid =response.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.filterInfo[i].operand[1].ref.name + "." +response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
          }
          else if (response.filterInfo[i].operand[1].ref.type == "dataset" && response.source.ref.uuid == response.filterInfo[i].operand[1].ref.uuid) {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
						filterInfo.isrhsDatapod = true;
						filterInfo.isrhsDataset = false;
						rhsdatapodAttribute.uuid =response.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.datapodname =response.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name =response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname =response.filterInfo[i].operand[1].ref.name + "." +response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId =response.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
					}
					else if (response.filterInfo[i].operand[1].ref.type == "formula") {
						var rhsformula = {}
						var obj = {}
						obj.text = "formula"
						obj.caption = "formula"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = true;
						filterInfo.isrhsSimple = false;
            filterInfo.isrhsDatapod = false;
            filterInfo.isrhsDataset = false;
						rhsformula.uuid =response.filterInfo[i].operand[1].ref.uuid;
						rhsformula.name =response.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
          }
          else if (response.filterInfo[i].operand[1].ref.type == "function") {
						var rhsfunction = {}
						var obj = {}
						obj.text = "function"
						obj.caption = "function"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula =   false;
						filterInfo.isrhsSimple =    false;
						filterInfo.isrhsDatapod =   false;
						filterInfo.isrhsDataset =   false;
						filterInfo.isrhsParamlist = false;
					    filterInfo.isrhsFunction =  true;
						rhsfunction.uuid =response.filterInfo[i].operand[1].ref.uuid;
						rhsfunction.name =response.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsfunction = rhsfunction;
					}
          else if (response.filterInfo[i].operand[1].ref.type == "dataset") {
						var rhsdataset = {}
						var obj = {}
						obj.text = "dataset"
						obj.caption = "dataset"
						filterInfo.rhstype = obj;
						filterInfo.isrhsFormula = false;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsDatapod = false;
						filterInfo.isrhsDataset = true;
						rhsdataset.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsdataset.datapodname = response.filterInfo[i].operand[1].ref.name;
						rhsdataset.name = response.filterInfo[i].operand[1].attributeName;
						rhsdataset.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
						rhsdataset.attributeId = response.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdataset = rhsdataset;
          }
          else if (response.filterInfo[i].operand[1].ref.type == "paramlist") {
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
            rhsparamlist.uuid = response.filterInfo[i].operand[1].ref.uuid;
            rhsparamlist.datapodname = response.filterInfo[i].operand[1].ref.name;
            rhsparamlist.name = response.filterInfo[i].operand[1].attributeName;
            rhsparamlist.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
            rhsparamlist.attributeId = response.filterInfo[i].operand[1].attributeId;
        
            filterInfo.rhsparamlist = rhsparamlist;
          }
         
					filterInfoArray[i] = filterInfo
				}
		  }
      // if (response.filter != null) {
      //   for (var i = 0; i < response.filterInfo.length; i++) {
      //     var filterInfo = {};
      //     var lhsFilter = {}
      //     filterInfo.logicalOperator = response.filterInfo[i].logicalOperator
      //     filterInfo.operator = response.filterInfo[i].operator;
      //     lhsFilter.uuid = response.filterInfo[i].operand[0].ref.uuid;
      //     lhsFilter.datapodname = response.filterInfo[i].operand[0].ref.name;
      //     lhsFilter.name = response.filterInfo[i].operand[0].attributeName;
      //     lhsFilter.attributeId = response.filterInfo[i].operand[0].attributeId;
      //     filterInfo.lhsFilter = lhsFilter;
      //     filterInfo.filtervalue = response.filterInfo[i].operand[1].value;
      //     filterInfoArray[i] = filterInfo
      //   }
      // }
      ruleJSOn.filterInfo = filterInfoArray;

      var sourceAttributesArray = [];
      for (var n = 0; n < response.attributeInfo.length; n++) {
        var attributeInfo = {};
        attributeInfo.name = response.attributeInfo[n].attrSourceName;
        attributeInfo.id = response.attributeInfo[n].attrSourceId;
        if(response.attributeInfo.length >CF_GRID.framework_autopopulate_grid)
          attributeInfo.isOnDropDown=false;
        else
          attributeInfo.isOnDropDown=true;
        if (response.attributeInfo[n].sourceAttr.ref.type == "simple") {
          var obj = {}
          obj.text = "string"
          obj.caption = "string";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId);
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.isSourceAtributeSimple = true;
          attributeInfo.sourcesimple = response.attributeInfo[n].sourceAttr.value
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }

        if (response.attributeInfo[n].sourceAttr.ref.type == "datapod" || response.attributeInfo[n].sourceAttr.ref.type == "dataset" || response.attributeInfo[n].sourceAttr.ref.type == "rule") {
          var sourcedatapod = {};
          sourcedatapod.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourcedatapod.type = response.attributeInfo[n].sourceAttr.ref.type;
          sourcedatapod.attributeId = response.attributeInfo[n].sourceAttr.attrId;
          sourcedatapod.attrType = response.attributeInfo[n].sourceAttr.attrType;
          sourcedatapod.name = response.attributeInfo[n].sourceAttr.attrName;
          debugger
          var obj = {}
          obj.text = "datapod"
          obj.caption = "attribute";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId);
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourcedatapod = sourcedatapod;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeDatapod = true;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }

        if (response.attributeInfo[n].sourceAttr.ref.type == "expression") {
          var sourceexpression = {};
          sourceexpression.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceexpression.type = response.attributeInfo[n].sourceAttr.ref.type;
					sourceexpression.name = response.attributeInfo[n].sourceAttr.ref.name;
          var obj = {}
          obj.text = "expression"
          obj.caption = "expression";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId);
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceexpression = sourceexpression;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = true;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "formula") {
          var sourceformula = {};
          sourceformula.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceformula.type = response.attributeInfo[n].sourceAttr.ref.type;
          sourceformula.name = response.attributeInfo[n].sourceAttr.ref.name;
          var obj = {}
          obj.text = "formula"
          obj.caption = "formula";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId)
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceformula = sourceformula;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = true;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "function") {
          var sourcefunction = {};
          sourcefunction.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourcefunction.type = response.attributeInfo[n].sourceAttr.ref.type;
          sourcefunction.name = response.attributeInfo[n].sourceAttr.ref.name;
          var obj = {}
          obj.text = "function"
          obj.caption = "function";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId);
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourcefunction = sourcefunction;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeFunction = true;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "paramlist") {
          var sourceparamlist = {};
          sourceparamlist.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceparamlist.type = response.attributeInfo[n].sourceAttr.ref.type;
          sourceparamlist.attributeId = response.attributeInfo[n].sourceAttr.attrId;
          sourceparamlist.attrType = response.attributeInfo[n].sourceAttr.attrType;
          sourceparamlist.name =response.attributeInfo[n].sourceAttr.attrName;
          var obj = {}
          obj.text = "paramlist"
          obj.caption = "paramlist";
          attributeInfo.id=parseInt(response.attributeInfo[n].attrSourceId);
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceparamlist = sourceparamlist;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = true;
        }
        sourceAttributesArray[n] = attributeInfo
      }
      console.log(sourceAttributesArray);
      ruleJSOn.sourceAttributes = sourceAttributesArray
      deferred.resolve({
        data: ruleJSOn
      })
    }
    return deferred.promise;
  }

  factory.getOneByUuidAndVersionForGroup = function (uuid, version, type) {
    var deferred = $q.defer();
    RuleFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
     
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  // factory.getRuleExecByRule = function (id) {
  //   var deferred = $q.defer();
  //   RuleFactory.findRuleExecByRule(id).then(function (response) { onSuccess(response.data) });
  //   var onSuccess = function (response) {
  //     // var rowDataSet = [];
  //     // var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status']
  //     // for (var i = 0; i < response.length; i++) {
  //     //   var rowData = [];
  //     //   if (response[i].status != null) {
  //     //     var len = response[i].status.length - 1;
  //     //   }
  //     //   for (var j = 0; j < headerColumns.length; j++) {
  //     //     var columnname = headerColumns[j]
  //     //     if (columnname == "createdBy") {
  //     //       rowData[j] = response[i].createdBy.ref.name;
  //     //     }
  //     //     else if (columnname == "status") {
  //     //       if (response[i].status != null) {
  //     //         rowData[j] = response[i].status[len].stage;
  //     //       }
  //     //       else {
  //     //         rowData[j] = " ";
  //     //       }
  //     //     }
  //     //     else {
  //     //       rowData[j] = response[i][columnname];
  //     //     }
  //     //   }
  //     //   rowDataSet[i] = rowData;
  //     // }
  //     deferred.resolve({
  //       data: response
  //     })
  //   }
  //   return deferred.promise;
  // }

  factory.executeRule = function (uuid, version) {
    var deferred = $q.defer();
    RuleFactory.executeRule(uuid, version).then(function (response) { onSuccess(response) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }
  factory.getRuleResults = function(uuid, version, offset, limit, requestId, sortBy, order) {
    var deferred = $q.defer();
    var url;
    if (sortBy == null && order == null) {
      url = "rule/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId;
    } else {
      url = "rule/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId + "&sortBy=" + sortBy + "&order=" + order;
    }
    RuleFactory.findRuleResults(url).then(function(response) {
      onSuccess(response)
    }, function(response) {
      onError(response.data)
    });
    var onSuccess = function(response) {
      deferred.resolve({
        data: response
      })
    }
    var onError = function(response) {
      deferred.reject({
        data: response
      })
    }
    return deferred.promise;
  }
  // factory.getRuleResults = function (uuid, version) {
  //   var deferred = $q.defer();
  //   RuleFactory.findRuleResults(uuid, version).then(function (response) { onSuccess(response) });
  //   var onSuccess = function (response) {
  //     deferred.resolve({
  //       data: response
  //     })
  //   }
  //   return deferred.promise;
  // }

  factory.getOneById = function (id, type) {
    var deferred = $q.defer();
    RuleFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.getByUuid = function (id, type) {
    var deferred = $q.defer();
    RuleFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var ruleJSOn = {};
      ruleJSOn.ruledata = response;
      var filterInfoArray = [];
      if (response.filter != null) {
        for (var i = 0; i < response.filterInfo.length; i++) {
          var filterInfo = {};
          var lhsFilter = {}
          filterInfo.logicalOperator = response.filterInfo[i].logicalOperator
          filterInfo.operator = response.filterInfo[i].operator;
          lhsFilter.uuid = response.filterInfo[i].operand[0].ref.uuid;
          lhsFilter.datapodname = response.filterInfo[i].operand[0].ref.name;
          lhsFilter.name = response.filterInfo[i].operand[0].attributeName;
          lhsFilter.attributeId = response.filterInfo[i].operand[0].attributeId;
          filterInfo.lhsFilter = lhsFilter;
          filterInfo.filtervalue = response.filterInfo[i].operand[1].value;
          filterInfoArray[i] = filterInfo
        }
      }
      ruleJSOn.filterInfo = filterInfoArray;

      var sourceAttributesArray = [];
      for (var n = 0; n < response.attributeInfo.length; n++) {
        var attributeInfo = {};
        attributeInfo.name = response.attributeInfo[n].attrSourceName
        if (response.attributeInfo[n].sourceAttr.ref.type == "simple") {
          var obj = {}
          obj.text = "string"
          obj.caption = "string"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.isSourceAtributeSimple = true;
          attributeInfo.sourcesimple = response.attributeInfo[n].sourceAttr.value
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;

        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "datapod" || response.attributeInfo[n].sourceAttr.ref.type == "dataset" || response.attributeInfo[n].sourceAttr.ref.type == "rule") {
          var sourcedatapod = {};
          sourcedatapod.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourcedatapod.attributeId = response.attributeInfo[n].sourceAttr.attrId;
          sourcedatapod.attrType = response.attributeInfo[n].sourceAttr.attrType

          sourcedatapod.name = "";
          var obj = {}
          obj.text = "datapod"
          obj.caption = "attribute"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourcedatapod = sourcedatapod;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeDatapod = true;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "expression") {
          var sourceexpression = {};
          sourceexpression.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceexpression.name = "";
          var obj = {}
          obj.text = "expression"
          obj.caption = "expression"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceexpression = sourceexpression;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeExpression = true;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "formula") {
          var sourceformula = {};
          sourceformula.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceformula.name = "";
          var obj = {}
          obj.text = "formula"
          obj.caption = "formula"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceformula = sourceformula;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = true;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "function") {
          var sourcefunction = {};
          sourcefunction.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourcefunction.name = "";
          var obj = {}
          obj.text = "function"
          obj.caption = "function"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourcefunction = sourcefunction;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeFunction = true;
          attributeInfo.isSourceAtributeParamList = false;
        }
        if (response.attributeInfo[n].sourceAttr.ref.type == "paramlist") {
          var sourceparamlist = {};
          sourceparamlist.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
          sourceparamlist.attributeId = response.attributeInfo[n].sourceAttr.attrId;
          sourceparamlist.attrType = response.attributeInfo[n].sourceAttr.attrType
          sourceparamlist.name = "";
          var obj = {}
          obj.text = "paramlist"
          obj.caption = "paramlist"
          attributeInfo.sourceAttributeType = obj;
          attributeInfo.sourceparamlist = sourceparamlist;
          attributeInfo.isSourceAtributeSimple = false;
          attributeInfo.isSourceAtributeExpression = false;
          attributeInfo.isSourceAtributeDatapod = false;
          attributeInfo.isSourceAtributeFormula = false;
          attributeInfo.isSourceAtributeFunction = false;
          attributeInfo.isSourceAtributeParamList = true;
        }
        sourceAttributesArray[n] = attributeInfo
      }
      ruleJSOn.sourceAttributes = sourceAttributesArray
      deferred.resolve({
        data: ruleJSOn
      })
    }
    return deferred.promise;
  }

  factory.submit = function(data,type,upd_tag) {
    var deferred = $q.defer();
    RuleFactory.ruleSubmit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

  factory.getAllLatestFunction = function (metavalue, inputFlag) {
    var deferred = $q.defer();
    RuleFactory.findAllLatest(metavalue, inputFlag).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  }

  factory.getAllLatestRullExec = function (metavalue, sessionid) {
    var deferred = $q.defer();
    RuleFactory.findAllLatest(metavalue).then(function (response) { onSuccess(response.data) });
    var onSuccess = function (response) {
      var rowDataSet = [];
      var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status']
      for (var i = 0; i < response.length; i++) {
        var rowData = [];
        if (response[i].status != null) {
          var len = response[i].status.length - 1;
        }
        for (var j = 0; j < headerColumns.length; j++) {
          var columnname = headerColumns[j]
          if (columnname == "createdBy") {
            rowData[j] = response[i].createdBy.ref.name;
          }
          else if (columnname == "status") {
            if (response[i].status != null) {
              rowData[j] = response[i].status[len].stage;
            }
            else {
              rowData[j] = " ";
            }
          }
          else {
            rowData[j] = response[i][columnname];
          }
        }
        rowDataSet[i] = rowData;
      }
      deferred.resolve({
        data: rowDataSet
      })
    }
    return deferred.promise;
  }

  factory.getAllAttributeBySource = function (uuid, type) {
    var deferred = $q.defer();
    if (type == "relation") {
      // RuleFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
      // var onSuccess = function (response) {
      //   var attributes = [];
      //   for (var j = 0; j < response.length; j++) {
      //     for (var i = 0; i < response[j].attributes.length; i++) {
      //       var attributedetail = {};
      //       attributedetail.uuid = response[j].uuid;
      //       attributedetail.datapodname = response[j].name;
      //       attributedetail.name = response[j].attributes[i].name;
      //       attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
      //       attributedetail.attributeId = response[j].attributes[i].attributeId;
      //       attributedetail.attrType = response[j].attributes[i].attrType;
      //       attributes.push(attributedetail)
      //     }
      //   }
      //   //console.log(JSON.stringify(attributes))
      //   deferred.resolve({
      //     data: attributes
      //   })
      // }
      RuleFactory.findAttributesByRelation(uuid, "relation", "").then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.attributeId = response[j].attrId;
					attributedetail.attrType = response[j].attrType;
					attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}
    }
    if (type == "dataset") {
      RuleFactory.findDatapodByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.type = response[j].ref.type;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.attrType = response[j].attrType;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }
    }
    if (type == "rule") {
      RuleFactory.findAttributesByRule(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.type = response[j].ref.type;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.attrType = response[j].attrType;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }
    }

    if (type == "datapod") {
      RuleFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
      var onSuccess = function (response) {
        var attributes = [];
        for (var j = 0; j < response.length; j++) {
          var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.type = response[j].ref.type;
          attributedetail.datapodname = response[j].ref.name;
          attributedetail.name = response[j].attrName;
          attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
          attributedetail.attributeId = response[j].attrId;
          attributedetail.attrType = response[j].attrType;
          attributes.push(attributedetail)
        }
        deferred.resolve({
          data: attributes
        })
      }
    }
    if(type == "paramlist"){
			RuleFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
          attributedetail.uuid = response[j].ref.uuid;
          attributedetail.type = response[j].ref.type;
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

  factory.getAllLatest = function (type) {
    var deferred = $q.defer();
    RuleFactory.findAllLatest(type).then(function (response) { onSuccessRelation(response.data) });
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

  factory.getAllLatestActive = function (type) {
    var deferred = $q.defer();
    RuleFactory.findAllLatestActive(type).then(function (response) { onSuccessRelation(response.data) });
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

  factory.getAllLatestFormulaTag = function (type) {
    var deferred = $q.defer();
    RuleFactory.findAllLatest(type).then(function (response) { onSuccessRelation(response.data) });
    var onSuccessRelation = function (response) {
      var expressionTag = [];
      response.sort(sortFactory.sortByProperty("name"));
      for (var i = 0; i < response.length; i++) {
        var tag = {}
        tag.name = response[i].name;
        tag.uuid = response[i].uuid;
        tag.version = response[i].version
        expressionTag[i] = tag;
      }
      deferred.resolve({
        data: expressionTag
      })
    }
    return deferred.promise;
  }
  return factory;
});
