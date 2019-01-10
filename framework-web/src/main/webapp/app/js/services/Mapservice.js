/*
 *
 */

MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataMapFactory', function ($http, $location) {
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
			url: url + "metadata/getOneById?action=view&id=" + id + "&type=" + type,
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
	factory.findDatapodByDatapod = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=datapod",

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
	factory.findDatapodByRule = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByRule?action=view&uuid=" + uuid + "&type=" + type,

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
	factory.findFormulaByType = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getFormulaByType?action=view&uuid=" + uuid + "&type=formula",
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

	factory.findResults = function (uuid, version,mode) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "map/getResults?action=view&uuid=" + uuid + "&version=" + version+ "&mode=" + mode + "&requestId="
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.findNumRowsbyExec = function (uuid, version,type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version+ "&type=" + type
		}).
		then(function (response, status, headers) {
			return response;
		})
	}
	
	return factory;
});

MetadataModule.service('MetadataMapSerivce', function ($q, sortFactory, MetadataMapFactory) {
	this.getNumRowsbyExec = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findNumRowsbyExec(uuid, version, type)
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		};
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		};
		return deferred.promise;
	}
	this.getResults = function (uuid, version, mode) {
		var deferred = $q.defer();
		MetadataMapFactory.findResults(uuid, version, mode)
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		};
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		};
		return deferred.promise;
	}
	this.getFormulaByType = function (uuid) {
		var deferred = $q.defer();
		MetadataMapFactory.findFormulaByType(uuid).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getExpressionByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();

		if (type == "relation") {
			// MetadataMapFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
			// var onSuccess = function (response) {
			// 	var attributes = [];
			// 	for (var j = 0; j < response.length; j++) {
			// 		for (var i = 0; i < response[j].attributes.length; i++) {
			// 			var attributedetail = {};
			// 			attributedetail.uuid = response[j].uuid;
			// 			attributedetail.datapodname = response[j].name;
			// 			attributedetail.name = response[j].attributes[i].name;
			// 			attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
			// 			attributedetail.attributeId = response[j].attributes[i].attributeId;
			// 			attributes.push(attributedetail)
			// 		}
			// 	}

			// 	console.log(JSON.stringify(attributes))
			// 	deferred.resolve({
			// 		data: attributes
			// 	})
			// }
			MetadataMapFactory.findAttributesByRelation(uuid, "relation", "").then(function (response) { onSuccess(response.data) });
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
			MetadataMapFactory.findDatapodByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataMapFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
					attributes.push(attributedetail)
				}
				deferred.resolve({
					data: attributes
				})
			}

		}
		if (type == "rule") {

			MetadataMapFactory.findDatapodByRule(uuid, type).then(function (response) { onSuccess(response.data) });
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
					attributes.push(attributedetail)
				}
				deferred.resolve({
					data: attributes
				})
				//console.log(JSON.stringify(response))
			}

		}

		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataMapFactory.getGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}



	this.getAttributesByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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

		return deferred.promise;
	}
	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var mapjson = {};
			mapjson.mapdata = response;
			var maparray = [];
			for (var i = 0; i < response.attributeMap.length; i++) {
				var attributemapjson = {};
				if (response.attributeMap[i].sourceAttr.ref.type == "datapod" || response.attributeMap[i].sourceAttr.ref.type == "dataset" || response.attributeMap[i].sourceAttr.ref.type == "rule") {
					var sourceattribute = {}
					sourceattribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
					sourceattribute.type = response.attributeMap[i].sourceAttr.ref.type;
					sourceattribute.attributeId = response.attributeMap[i].sourceAttr.attrId;
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
				var targetattribute = {}
				targetattribute.uuid = response.attributeMap[i].targetAttr.ref.uuid;
				targetattribute.type = response.attributeMap[i].targetAttr.ref.type;
				targetattribute.attributeId = response.attributeMap[i].targetAttr.attrId;
				attributemapjson.targetattribute = targetattribute;
				maparray[i] = attributemapjson
			}
			mapjson.maptabalearray = maparray
			deferred.resolve({
				data: mapjson
			})
		}
		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getAllLatestFunction = function (type, inputFlag) {
		var deferred = $q.defer();
		MetadataMapFactory.findAllLatest(type, inputFlag).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataMapFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
		var onSuccess = function (response) {
			var mapjson = {};
			mapjson.mapdata = response;
			var maparray = [];
			for (var i = 0; i < response.attributeMap.length; i++) {
				var attributemapjson = {};
				if (response.attributeMap[i].sourceAttr.ref.type == "datapod" || response.attributeMap[i].sourceAttr.ref.type == "dataset" || response.attributeMap[i].sourceAttr.ref.type == "rule") {
					var sourceattribute = {}
					sourceattribute.uuid = response.attributeMap[i].sourceAttr.ref.uuid;
					sourceattribute.type = response.attributeMap[i].sourceAttr.ref.type;
					sourceattribute.attributeId = response.attributeMap[i].sourceAttr.attrId;
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
				var targetattribute = {}
				targetattribute.uuid = response.attributeMap[i].targetAttr.ref.uuid;
				targetattribute.type = response.attributeMap[i].targetAttr.ref.type;
				targetattribute.attributeId = response.attributeMap[i].targetAttr.attrId;
				attributemapjson.targetattribute = targetattribute;
				maparray[i] = attributemapjson
			}
			mapjson.maptabalearray = maparray
			deferred.resolve({
				data: mapjson
			})
		};
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		};
		return deferred.promise;
	}
	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		MetadataMapFactory.submit(data, type, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
		MetadataMapFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
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
