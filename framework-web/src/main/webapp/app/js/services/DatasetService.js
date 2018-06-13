/**
 *
 */

MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataDatasetFactory', function ($http, $location) {
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
	factory.findByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })


	}
	factory.findByUuidandVersion = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=datasetview",
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
			url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.datasetSubmit = function (data,type,upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type=datasetview&upd_tag="+upd_tag,

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
			url: url + "metadata/getFormulaByType2?action=view&uuid=" + uuid + "&type=" + type
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findExpressionByType = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getExpressionByType2?action=view&uuid=" + uuid + "&type=" + type
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findDatasetSample = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "dataset/getDatasetSample?action=view&datasetUUID=" + uuid + "&datasetVersion=" + version + "&row=100",
			method: "GET",
		}).then(function (response) { return response })
	}
	return factory;
});

MetadataModule.service('MetadataDatasetSerivce', function ($http, $q, sortFactory, MetadataDatasetFactory) {
	this.getDatasetSample = function (data) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findDatasetSample(data.uuid, data.version).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();
		if (type == "relation") {
			MetadataDatasetFactory.findDatapodByRelation(uuid, "datapod").then(function (response) { onSuccess(response.data) });
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
				//console.log(JSON.stringify(attributes))
				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			MetadataDatasetFactory.findAttributesByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
			MetadataDatasetFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
		MetadataDatasetFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var formulaarray = [];
			var formulajson = {}
			formulajson.name = "Create New";
			formulajson.uuid = null;
			formulajson.class = "changefirstoption";
			formulajson.iconclass = "fa fa-plus customcolor"
			formulaarray[0] = formulajson;
			for (var i = 0; i < response.length; i++) {
				var formulajson = {}
				formulajson.name = response[i].ref.name;
				formulajson.uuid = response[i].ref.uuid;
				formulajson.class = "";
				formulajson.iconclass = "";
				formulaarray.push(formulajson);

			}

			deferred.resolve({
				data: formulaarray
			})
		}

		return deferred.promise;
	}
	this.getExpressionByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var expressionaarray = [];
			var expressionjson = {}
			expressionjson.name = "Create New";
			expressionjson.uuid = null;
			expressionjson.class = "changefirstoption";
			expressionjson.iconclass = "fa fa-plus customcolor"
			expressionaarray[0] = expressionjson;
			for (var i = 0; i < response.length; i++) {
				var expressionjson = {}
				expressionjson.name = response[i].ref.name;
				expressionjson.uuid = response[i].ref.uuid;

				expressionjson.class = "";
				expressionjson.iconclass = "";
				expressionaarray.push(expressionjson);

			}
			deferred.resolve({
				data: expressionaarray
			})
		}

		return deferred.promise;
	}
	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
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

	this.getDatapodByRelation = function (uuid, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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

		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.findByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		MetadataDatasetFactory.datasetSubmit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
	this.getDatasetDataByOneUuidandVersion = function (id, version) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findByUuidandVersion(id, version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var datasetviewjson = {};
			datasetviewjson.dataset = response;
			var tags = [];
			for (var i = 0; i < response.tags.length; i++) {
				var tag = {};
				tag.text = response.tags[i];
				tags[i] = tag
			}
			datasetviewjson.tags = tags; 
            var filterInfoArray = [];
			if(response.filter !=null){
				for (i = 0; i <response.filter.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator =response.filter.filterInfo[i].logicalOperator;
					filterInfo.operator =response.filter.filterInfo[i].operator;
					if (response.filter.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
						filterInfo.lhsvalue =response.filter.filterInfo[i].operand[0].value;
					}
					else if (response.filter.filterInfo[i].operand[0].ref.type == "datapod" ||response.filter.filterInfo[i].operand[0].ref.type == "dataset") {
						var lhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = false;
						filterInfo.islhsFormula = false
						filterInfo.islhsDatapod = true;
						lhsdatapodAttribute.uuid =response.filter.filterInfo[i].operand[0].ref.uuid;
						lhsdatapodAttribute.datapodname =response.filter.filterInfo[i].operand[0].ref.name;
						lhsdatapodAttribute.name =response.filter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.dname =response.filter.filterInfo[i].operand[0].ref.name + "." +response.filter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.attributeId =response.filter.filterInfo[i].operand[0].attributeId;
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
						lhsformula.uuid =response.filter.filterInfo[i].operand[0].ref.uuid;
						lhsformula.name =response.filter.filterInfo[i].operand[0].ref.name;
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
						filterInfo.rhsvalue =response.filter.filterInfo[i].operand[1].value;
					}
					else if (response.filter.filterInfo[i].operand[1].ref.type == "datapod" || response.filter.filterInfo[i].operand[1].ref.type == "dataset") {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
						filterInfo.isrhsDatapod = true;
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
						rhsformula.uuid =response.filter.filterInfo[i].operand[1].ref.uuid;
						rhsformula.name =response.filter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
					}
					filterInfoArray[i] = filterInfo
				}
		    }
			// var filterInfoArray = [];
			// if (response.filter != null) {
			// 	for (var k = 0; k < response.filter.filterInfo.length; k++) {
			// 		var filterInfo = {};
			// 		var lhsFilter = {};
			// 		lhsFilter.uuid = response.filter.filterInfo[k].operand[0].ref.uuid
			// 		lhsFilter.datapodname = response.filter.filterInfo[k].operand[0].ref.name
			// 		lhsFilter.attributeId = response.filter.filterInfo[k].operand[0].attributeId;
			// 		lhsFilter.name = response.filter.filterInfo[k].operand[0].attributeName;
			// 		filterInfo.logicalOperator = response.filter.filterInfo[k].logicalOperator
			// 		filterInfo.lhsFilter = lhsFilter;
			// 		filterInfo.operator = response.filter.filterInfo[k].operator;
			// 		filterInfo.filtervalue = response.filter.filterInfo[k].operand[1].value;
			// 		filterInfoArray.push(filterInfo);
			// 	}
			// }
			datasetviewjson.filterInfo = filterInfoArray
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
					attributeInfo.isSourceAtributeFunction = false

				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "datapod") {
					var sourcedatapod = {};
					sourcedatapod.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourcedatapod.attributeId = response.attributeInfo[n].sourceAttr.attrId
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
					attributeInfo.isSourceAtributeFunction = false
				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "expression") {
					var sourceexpression = {};
					sourceexpression.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourceexpression.name = response.attributeInfo[n].sourceAttr.ref.name;
					var obj = {}
					obj.text = "expression"
					obj.caption = "expression"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourceexpression = sourceexpression;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = false;
					attributeInfo.isSourceAtributeExpression = true;
					attributeInfo.isSourceAtributeFunction = false
				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "formula") {
					var sourceformula = {};
					sourceformula.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourceformula.name = response.attributeInfo[n].sourceAttr.ref.name;
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourceformula = sourceformula;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = true;
					attributeInfo.isSourceAtributeExpression = false;
					attributeInfo.isSourceAtributeFunction = false
				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "function") {
					var sourcefunction = {};
					sourcefunction.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourcefunction.name = response.attributeInfo[n].sourceAttr.ref.name;
					var obj = {}
					obj.text = "function"
					obj.caption = "function"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourcefunction = sourcefunction;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = false;
					attributeInfo.isSourceAtributeFunction = true
					attributeInfo.isSourceAtributeExpression = false;
				}
				sourceAttributesArray[n] = attributeInfo
			}
			datasetviewjson.sourceAttributes = sourceAttributesArray
			//			  console.log("filertIfnfo"+JSON.stringify(datasetviewjson.filterInfo))
			deferred.resolve({
				data: datasetviewjson
			})
		}

		return deferred.promise;
	}
	this.getDatasetDataByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var datasetviewjson = {};
			datasetviewjson.dataset = response;
			var tags = [];
			for (var i = 0; i < response.tags.length; i++) {
				var tag = {};
				tag.text = response.tags[i];
				tags[i] = tag
			}
			datasetviewjson.tags = tags;
			var filterInfoArray = [];
			if(response.filter !=null){
				for (i = 0; i <response.filter.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator =response.filter.filterInfo[i].logicalOperator;
					filterInfo.operator =response.filter.filterInfo[i].operator;
					if (response.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
						filterInfo.lhsvalue =response.filter.filterInfo[i].operand[0].value;
					}
					else if (response.filter.filterInfo[i].operand[0].ref.type == "datapod" ||response.filter.filterInfo[i].operand[0].ref.type == "dataset") {
						var lhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = false;
						filterInfo.islhsFormula = false
						filterInfo.islhsDatapod = true;
						lhsdatapodAttribute.uuid =response.filter.filterInfo[i].operand[0].ref.uuid;
						lhsdatapodAttribute.datapodname =response.filter.filterInfo[i].operand[0].ref.name;
						lhsdatapodAttribute.name =response.filter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.dname =response.filter.filterInfo[i].operand[0].ref.name + "." +response.filter.filterInfo[i].operand[0].attributeName;
						lhsdatapodAttribute.attributeId =response.filter.filterInfo[i].operand[0].attributeId;
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
						lhsformula.uuid =response.filter.filterInfo[i].operand[0].ref.uuid;
						lhsformula.name =response.filter.filterInfo[i].operand[0].ref.name;
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
						filterInfo.rhsvalue =response.filter.filterInfo[i].operand[1].value;
					}
					else if (response.filter.filterInfo[i].operand[1].ref.type == "datapod" || response.filter.filterInfo[i].operand[1].ref.type == "dataset") {
						var rhsdatapodAttribute = {}
						var obj = {}
						obj.text = "datapod"
						obj.caption = "attribute"
						filterInfo.rhstype = obj;
						filterInfo.isrhsSimple = false;
						filterInfo.isrhsFormula = false
						filterInfo.isrhsDatapod = true;
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
						rhsformula.uuid =response.filter.filterInfo[i].operand[1].ref.uuid;
						rhsformula.name =response.filter.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
					}
					filterInfoArray[i] = filterInfo
				}
		    }
			// var filterInfoArray = [];
			// if (response.filter != null) {
			// 	for (var k = 0; k < response.filter.filterInfo.length; k++) {
			// 		var filterInfo = {};
			// 		var lhsFilter = {};
			// 		lhsFilter.uuid = response.filter.filterInfo[k].operand[0].ref.uuid
			// 		lhsFilter.datapodname = response.filter.filterInfo[k].operand[0].ref.name
			// 		lhsFilter.attributeId = response.filter.filterInfo[k].operand[0].attributeId;
			// 		lhsFilter.name = response.filter.filterInfo[k].operand[0].attributeName;
			// 		filterInfo.logicalOperator = response.filter.filterInfo[k].logicalOperator
			// 		filterInfo.lhsFilter = lhsFilter;
			// 		filterInfo.operator = response.filter.filterInfo[k].operator;
			// 		filterInfo.filtervalue = response.filter.filterInfo[k].operand[1].value;
			// 		filterInfoArray.push(filterInfo);
			// 	}
			// }
			datasetviewjson.filterInfo = filterInfoArray
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
					attributeInfo.isSourceAtributeFunction = false

				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "datapod") {
					var sourcedatapod = {};
					sourcedatapod.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourcedatapod.attributeId = response.attributeInfo[n].sourceAttr.attrId
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
					attributeInfo.isSourceAtributeFunction = false
				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "expression") {
					var sourceexpression = {};
					sourceexpression.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourceexpression.name = response.attributeInfo[n].sourceAttr.ref.name;
					var obj = {}
					obj.text = "expression"
					obj.caption = "expression"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourceexpression = sourceexpression;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = false;
					attributeInfo.isSourceAtributeExpression = true;
					attributeInfo.isSourceAtributeFunction = false
				}
				if (response.attributeInfo[n].sourceAttr.ref.type == "formula") {
					var sourceformula = {};
					sourceformula.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourceformula.name = response.attributeInfo[n].sourceAttr.ref.name;
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourceformula = sourceformula;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = true;
					attributeInfo.isSourceAtributeExpression = false;
					attributeInfo.isSourceAtributeFunction = false
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
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = false;
					attributeInfo.isSourceAtributeFunction = true
					attributeInfo.isSourceAtributeExpression = false;
				}
				sourceAttributesArray[n] = attributeInfo
			}
			datasetviewjson.sourceAttributes = sourceAttributesArray
			//			  console.log("filertIfnfo"+JSON.stringify(datasetviewjson.filterInfo))
			deferred.resolve({
				data: datasetviewjson
			})
		}
		return deferred.promise;
	}

	this.getAllLatestFunction = function (metavalue, inputFlag) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findAllLatest(metavalue, inputFlag).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataDatasetFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

});
