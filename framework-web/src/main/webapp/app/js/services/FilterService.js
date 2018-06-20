/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataFilterFactory', function ($http, $location) {
	var factory = {};
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
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })


	}
	factory.findByUuidandVersion = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
			method: "GET",

		}).then(function (response) { return response })

	}
	factory.findDatapodByRelation = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=" + type,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findAttributesByDatapod = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=" + type,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findAttributesByDataset = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.datasetSubmit = function(data,type,upd_tag) {
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
	return factory;
});

MetadataModule.service('MetadataFilterSerivce', function ($http, $q, sortFactory, MetadataFilterFactory) {
	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataFilterFactory.findDatapodByRelation(uuid, "datapod").then(function (response) { onSuccess(response.data) });
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
			MetadataFilterFactory.findAttributesByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataFilterFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
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

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var filterjson = {};
			filterjson.filter = response;
			var filterInfoArray = [];
			for (i = 0; i < response.filterInfo.length; i++) {
				var filterInfo = {};
				filterInfo.logicalOperator = response.filterInfo[i].logicalOperator;
				filterInfo.operator = response.filterInfo[i].operator;
				if (response.filterInfo[i].operand[0].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					filterInfo.lhstype = obj;
					filterInfo.islhsSimple = true;
					filterInfo.islhsDatapod = false;
					filterInfo.islhsFormula = false;
					filterInfo.lhsvalue = response.filterInfo[i].operand[0].value;
				}
				else if (response.filterInfo[i].operand[0].ref.type == "datapod" || response.filterInfo[i].operand[0].ref.type == "dataset") {
					var lhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					filterInfo.lhstype = obj;
					filterInfo.islhsSimple = false;
					filterInfo.islhsFormula = false
					filterInfo.islhsDatapod = true;
					lhsdatapodAttribute.uuid = response.filterInfo[i].operand[0].ref.uuid;
					lhsdatapodAttribute.datapodname = response.filterInfo[i].operand[0].ref.name;
					lhsdatapodAttribute.name = response.filterInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.dname = response.filterInfo[i].operand[0].ref.name + "." + response.filterInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.attributeId = response.filterInfo[i].operand[0].attributeId;
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
					lhsformula.uuid = response.filterInfo[i].operand[0].ref.uuid;
					lhsformula.name = response.filterInfo[i].operand[0].ref.name;
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
					filterInfo.rhsvalue = response.filterInfo[i].operand[1].value;
				}
				else if (response.filterInfo[i].operand[1].ref.type == "datapod" || response.filterInfo[i].operand[1].ref.type == "dataset") {
					var rhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					filterInfo.rhstype = obj;
					filterInfo.isrhsSimple = false;
					filterInfo.isrhsFormula = false
					filterInfo.isrhsDatapod = true;
					rhsdatapodAttribute.uuid = response.filterInfo[i].operand[1].ref.uuid;
					rhsdatapodAttribute.datapodname = response.filterInfo[i].operand[1].ref.name;
					rhsdatapodAttribute.name = response.filterInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.attributeId = response.filterInfo[i].operand[1].attributeId;
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
					rhsformula.uuid = response.filterInfo[i].operand[1].ref.uuid;
					rhsformula.name = response.filterInfo[i].operand[1].ref.name;
					filterInfo.rhsformula = rhsformula;
				}
				filterInfoArray[i] = filterInfo
			}

			filterjson.filterInfo = filterInfoArray
			deferred.resolve({
				data: filterjson
			})
		}

		return deferred.promise;
	}
	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		MetadataFilterFactory.datasetSubmit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

	this.getOneByUuidandVersion = function (id, version, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findByUuidandVersion(id, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var filterjson = {};
			filterjson.filter = response;
			var filterInfoArray = [];
			for (i = 0; i < response.filterInfo.length; i++) {
				var filterInfo = {};
				filterInfo.logicalOperator = response.filterInfo[i].logicalOperator;
				filterInfo.operator = response.filterInfo[i].operator;
				if (response.filterInfo[i].operand[0].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					filterInfo.lhstype = obj;
					filterInfo.islhsSimple = true;
					filterInfo.islhsDatapod = false;
					filterInfo.islhsFormula = false;
					filterInfo.lhsvalue = response.filterInfo[i].operand[0].value.replace(/["']/g, "");
				}
				else if (response.filterInfo[i].operand[0].ref.type == "datapod" || response.filterInfo[i].operand[0].ref.type == "dataset") {
					var lhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					filterInfo.lhstype = obj;
					filterInfo.islhsSimple = false;
					filterInfo.islhsFormula = false
					filterInfo.islhsDatapod = true;
					lhsdatapodAttribute.uuid = response.filterInfo[i].operand[0].ref.uuid;
					lhsdatapodAttribute.datapodname = response.filterInfo[i].operand[0].ref.name;
					lhsdatapodAttribute.name = response.filterInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.dname = response.filterInfo[i].operand[0].ref.name + "." + response.filterInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.attributeId = response.filterInfo[i].operand[0].attributeId;
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
					
					lhsformula.uuid = response.filterInfo[i].operand[0].ref.uuid;
					lhsformula.name = response.filterInfo[i].operand[0].ref.name;
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
					if(response.filterInfo[i].operator =="BETWEEN"){
						obj.caption = "integer";
					//	filterInfo.rhsvalue = response.filterInfo[i].operand[1].value;
						filterInfo.rhsvalue1=response.filterInfo[i].operand[1].value.split("and")[0];
						filterInfo.rhsvalue2=response.filterInfo[i].operand[1].value.split("and")[1];	
					}else if(['<','>',"<=",'>='].indexOf(response.filterInfo[i].operator) !=-1){
						obj.caption = "integer";
					}
					else{
					filterInfo.rhsvalue = response.filterInfo[i].operand[1].value.replace(/["']/g, "");
				    }
				}
				else if (response.filterInfo[i].operand[1].ref.type == "datapod") {
					var rhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					filterInfo.rhstype = obj;
					filterInfo.isrhsSimple = false;
					filterInfo.isrhsFormula = false
					filterInfo.isrhsDatapod = true;
					filterInfo.isrhsDataset = false;
					rhsdatapodAttribute.uuid = response.filterInfo[i].operand[1].ref.uuid;
					rhsdatapodAttribute.datapodname = response.filterInfo[i].operand[1].ref.name;
					rhsdatapodAttribute.name = response.filterInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.attributeId = response.filterInfo[i].operand[1].attributeId;
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
					rhsformula.uuid = response.filterInfo[i].operand[1].ref.uuid;
					rhsformula.name = response.filterInfo[i].operand[1].ref.name;
					filterInfo.rhsformula = rhsformula;
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
				filterInfoArray[i] = filterInfo
			}

			filterjson.filterInfo = filterInfoArray
			deferred.resolve({
				data: filterjson
			})
		}

		return deferred.promise;
	}
	this.getDatasetDataByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var datasetviewjson = {};
			datasetviewjson.dataset = response;
			var filterInfoArray = [];
			if (response.filter != null) {
				for (var k = 0; k < response.filter.filterInfo.length; k++) {
					var filterInfo = {};
					var lhsFilter = {};
					lhsFilter.uuid = response.filter.filterInfo[k].operand[0].ref.uuid
					lhsFilter.datapodname = response.filter.filterInfo[k].operand[0].ref.name
					lhsFilter.attributeId = response.filter.filterInfo[k].operand[0].attributeId;
					lhsFilter.name = response.filter.filterInfo[k].operand[0].attributeName;
					filterInfo.logicalOperator = response.filter.filterInfo[k].logicalOperator
					filterInfo.lhsFilter = lhsFilter;
					filterInfo.operator = response.filter.filterInfo[k].operator;
					filterInfo.filtervalue = response.filter.filterInfo[k].operand[1].value;
					filterInfoArray.push(filterInfo);
				}
			}
			datasetviewjson.filterInfo = filterInfoArray

			deferred.resolve({
				data: datasetviewjson
			})
		}

		return deferred.promise;
	}

	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataFilterFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

});
