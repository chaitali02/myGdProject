/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataExpressionFactory', function ($http, $location) {
	var factory = {}
	factory.getGraphData = function (uuid, version, degree) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "graph/getGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree,
			method: "GET",
		}).then(function (response) { return response })
	};
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findOneByUuidAndVersion = function (uuid, version, type) {
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
	factory.getAttributesByDataset = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.getDatapodByRelation = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getDatapodByRelation?action=view&relationUuid=" + uuid + "&type=datapod",
			method: "GET",
		}).then(function (response) { return response })
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

MetadataModule.service('MetadataExpressionSerivce', function ($q, sortFactory, MetadataExpressionFactory) {
	var that = this;
	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();
		if (type == "relation") {
			MetadataExpressionFactory.getDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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
				//
				//console.log(JSON.stringify(attributes))
				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			MetadataExpressionFactory.getAttributesByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataExpressionFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataExpressionFactory.getGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

	this.getAttributesByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
			//console.log(JSON.stringify(attributes))
			deferred.resolve({
				data: attributes
			})
		}

		return deferred.promise;
	}

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var expressionjson = {};
			expressionjson.expressiondata = response;
			var expressionListArray = [];

			var expression = {};
			var metinfo = {};
			var notmetinfo = {};
			if (response.match.ref.type == "simple") {

				metinfo.mettype = "string";
				metinfo.ismetlhsSimple = true;
				metinfo.ismetFormula = false;
				metinfo.metlhsvalue = response.match.value;
			}
			else if (response.match.ref.type == "formula") {
				var metformula = {};
				metinfo.mettype = "formula";
				metinfo.ismetFormula = true;
				metinfo.ismetlhsSimple = false;
				metformula.uuid = response.match.ref.uuid;
				metformula.name = response.match.ref.name;
				metinfo.metformula = metformula
			}
			if (response.noMatch.ref.type == "simple") {

				notmetinfo.notmettype = "string";
				notmetinfo.isnotmetlhsSimple = true;
				notmetinfo.isnotmetFormula = false;
				notmetinfo.notmetlhsvalue = response.noMatch.value;
			}
			else if (response.noMatch.ref.type == "formula") {
				var notmetformula = {};
				notmetinfo.notmettype = "formula";
				notmetinfo.isnotmetFormula = true;
				notmetinfo.isnotmetlhsSimple = false;
				notmetformula.uuid = response.noMatch.ref.uuid;
				notmetformula.name = response.noMatch.ref.name;
				notmetinfo.notmetformula = notmetformula
			}
			expression.metinfo = metinfo;
			expression.notmetinfo = notmetinfo;
			var exressionArray = [];
			for (i = 0; i < response.expressionInfo.length; i++) {
				var expressioninfo = {};
				expressioninfo.logicalOperator = response.expressionInfo[i].logicalOperator;
				expressioninfo.operator = response.expressionInfo[i].operator;
				if (response.expressionInfo[i].operand[0].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsSimple = true;
					expressioninfo.islhsDatapod = false;
					expressioninfo.islhsFormula = false;
					expressioninfo.lhsvalue = response.expressionInfo[i].operand[0].value;
				}
				else if (response.expressionInfo[i].operand[0].ref.type == "datapod" || response.expressionInfo[i].operand[0].ref.type == "dataset") {
					var lhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsSimple = false;
					expressioninfo.islhsFormula = false
					expressioninfo.islhsDatapod = true;
					lhsdatapodAttribute.uuid = response.expressionInfo[i].operand[0].ref.uuid;
					lhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[0].ref.name;
					lhsdatapodAttribute.name = response.expressionInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.dname = response.expressionInfo[i].operand[0].ref.name + "." + response.expressionInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[0].attributeId;
					expressioninfo.lhsdatapodAttribute = lhsdatapodAttribute;
				}
				else if (response.expressionInfo[i].operand[0].ref.type == "formula") {
					var lhsformula = {}
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsFormula = true;
					expressioninfo.islhsSimple = false;
					expressioninfo.islhsDatapod = false;
					lhsformula.uuid = response.expressionInfo[i].operand[0].ref.uuid;
					lhsformula.name = response.expressionInfo[i].operand[0].ref.name;
					expressioninfo.lhsformula = lhsformula;
				}
				if (response.expressionInfo[i].operand[1].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsSimple = true;
					expressioninfo.isrhsDatapod = false;
					expressioninfo.isrhsFormula = false;
					expressioninfo.rhsvalue = response.expressionInfo[i].operand[1].value;
				}
				else if (response.expressionInfo[i].operand[1].ref.type == "datapod" || response.expressionInfo[i].operand[1].ref.type == "dataset") {
					var rhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsSimple = false;
					expressioninfo.isrhsFormula = false
					expressioninfo.isrhsDatapod = true;
					rhsdatapodAttribute.uuid = response.expressionInfo[i].operand[1].ref.uuid;
					rhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[1].ref.name;
					rhsdatapodAttribute.name = response.expressionInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.dname = response.expressionInfo[i].operand[1].ref.name + "." + response.expressionInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[1].attributeId;
					expressioninfo.rhsdatapodAttribute = rhsdatapodAttribute;
				}
				else if (response.expressionInfo[i].operand[1].ref.type == "formula") {
					var rhsformula = {}
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsFormula = true;
					expressioninfo.isrhsSimple = false;
					expressioninfo.isrhsDatapod = false;
					rhsformula.uuid = response.expressionInfo[i].operand[1].ref.uuid;
					rhsformula.name = response.expressionInfo[i].operand[1].ref.name;
					expressioninfo.rhsformula = rhsformula;
				}
				exressionArray[i] = expressioninfo
			}
			expressionjson.expression = expression;
			expressionjson.expressioninfo = exressionArray;
			deferred.resolve({
				data: expressionjson
			});
		}
		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var expressionjson = {};
			expressionjson.expressiondata = response;
			var expressionListArray = [];

			var expression = {};
			var metinfo = {};
			var notmetinfo = {};
			if (response.match.ref.type == "simple") {

				metinfo.mettype = "string";
				metinfo.ismetlhsSimple = true;
				metinfo.ismetFormula = false;
				metinfo.metlhsvalue = response.match.value;
			}
			else if (response.match.ref.type == "formula") {
				var metformula = {};
				metinfo.mettype = "formula";
				metinfo.ismetFormula = true;
				metinfo.ismetlhsSimple = false;
				metformula.uuid = response.match.ref.uuid;
				metformula.name = response.match.ref.name;
				metinfo.metformula = metformula
			}
			if (response.noMatch.ref.type == "simple") {

				notmetinfo.notmettype = "string";
				notmetinfo.isnotmetlhsSimple = true;
				notmetinfo.isnotmetFormula = false;
				notmetinfo.notmetlhsvalue = response.noMatch.value;
			}
			else if (response.noMatch.ref.type == "formula") {
				var notmetformula = {};
				notmetinfo.notmettype = "formula";
				notmetinfo.isnotmetFormula = true;
				notmetinfo.isnotmetlhsSimple = false;
				notmetformula.uuid = response.noMatch.ref.uuid;
				notmetformula.name = response.noMatch.ref.name;
				notmetinfo.notmetformula = notmetformula
			}
			expression.metinfo = metinfo;
			expression.notmetinfo = notmetinfo;
			var exressionArray = [];
			for (i = 0; i < response.expressionInfo.length; i++) {
				var expressioninfo = {};
				expressioninfo.logicalOperator = response.expressionInfo[i].logicalOperator;
				expressioninfo.operator = response.expressionInfo[i].operator;
				if (response.expressionInfo[i].operand[0].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsSimple = true;
					expressioninfo.islhsDatapod = false;
					expressioninfo.islhsFormula = false;
					expressioninfo.lhsvalue = response.expressionInfo[i].operand[0].value;
				}
				else if (response.expressionInfo[i].operand[0].ref.type == "datapod" || response.expressionInfo[i].operand[0].ref.type == "dataset") {
					var lhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsSimple = false;
					expressioninfo.islhsFormula = false
					expressioninfo.islhsDatapod = true;
					lhsdatapodAttribute.uuid = response.expressionInfo[i].operand[0].ref.uuid;
					lhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[0].ref.name;
					lhsdatapodAttribute.name = response.expressionInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.dname = response.expressionInfo[i].operand[0].ref.name + "." + response.expressionInfo[i].operand[0].attributeName;
					lhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[0].attributeId;
					expressioninfo.lhsdatapodAttribute = lhsdatapodAttribute;
				}
				else if (response.expressionInfo[i].operand[0].ref.type == "formula") {
					var lhsformula = {}
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					expressioninfo.lhstype = obj;
					expressioninfo.islhsFormula = true;
					expressioninfo.islhsSimple = false;
					expressioninfo.islhsDatapod = false;
					lhsformula.uuid = response.expressionInfo[i].operand[0].ref.uuid;
					lhsformula.name = response.expressionInfo[i].operand[0].ref.name;
					expressioninfo.lhsformula = lhsformula;
				}
				if (response.expressionInfo[i].operand[1].ref.type == "simple") {
					var obj = {}
					obj.text = "string"
					obj.caption = "string"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsSimple = true;
					expressioninfo.isrhsDatapod = false;
					expressioninfo.isrhsFormula = false;
					expressioninfo.rhsvalue = response.expressionInfo[i].operand[1].value;
				}
				else if (response.expressionInfo[i].operand[1].ref.type == "datapod" || response.expressionInfo[i].operand[1].ref.type == "dataset") {
					var rhsdatapodAttribute = {}
					var obj = {}
					obj.text = "datapod"
					obj.caption = "attribute"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsSimple = false;
					expressioninfo.isrhsFormula = false
					expressioninfo.isrhsDatapod = true;
					rhsdatapodAttribute.uuid = response.expressionInfo[i].operand[1].ref.uuid;
					rhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[1].ref.name;
					rhsdatapodAttribute.name = response.expressionInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.dname = response.expressionInfo[i].operand[1].ref.name + "." + response.expressionInfo[i].operand[1].attributeName;
					rhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[1].attributeId;
					expressioninfo.rhsdatapodAttribute = rhsdatapodAttribute;
				}
				else if (response.expressionInfo[i].operand[1].ref.type == "formula") {
					var rhsformula = {}
					var obj = {}
					obj.text = "formula"
					obj.caption = "formula"
					expressioninfo.rhstype = obj;
					expressioninfo.isrhsFormula = true;
					expressioninfo.isrhsSimple = false;
					expressioninfo.isrhsDatapod = false;
					rhsformula.uuid = response.expressionInfo[i].operand[1].ref.uuid;
					rhsformula.name = response.expressionInfo[i].operand[1].ref.name;
					expressioninfo.rhsformula = rhsformula;
				}
				exressionArray[i] = expressioninfo
			}
			expressionjson.expression = expression;
			expressionjson.expressioninfo = exressionArray;
			deferred.resolve({
				data: expressionjson
			});
		}
		return deferred.promise;
	}
	this.submit = function (data, type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.submit(data, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		MetadataExpressionFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

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
			deferred.resolve({
				data: data
			})
		}
		return deferred.promise;
	}
});
