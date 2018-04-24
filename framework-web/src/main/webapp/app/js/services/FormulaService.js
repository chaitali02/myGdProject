/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataFormulaFactory', function ($http, $location) {
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
			url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=" + type,

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
	factory.findFormulaByType = function (uuid) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getFormulaByType?type=formula&action=view&uuid=" + uuid
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

	factory.findFunctionByFunctionInfo = function (functioninfo) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getFunctionByFunctionInfo?type=function&action=view&functionInfo=" + functioninfo
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	return factory;
});

MetadataModule.service('MetadataFormulaSerivce', function ($q, sortFactory, MetadataFormulaFactory) {

	this.getFunctionByFunctionInfo = function (functioninfo) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findFunctionByFunctionInfo(functioninfo).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getAllLatestFunction = function (metavalue, inputFlag) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findAllLatest(metavalue, inputFlag).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

	this.getExpressionByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getParamByParamList = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var attributes = [];
			for (var j = 0; j < response.length; j++) {
				var attributedetail = {};
				attributedetail.uuid = response[j].ref.uuid;
				attributedetail.datapodname = response[j].ref.name;
				attributedetail.name = response[j].attrName;
				attributedetail.dname = response[j].paramName //response[j].ref.name + "." + response[j].paramName;
				attributedetail.attributeId = response[j].paramId;
				attributes.push(attributedetail);
			}
			deferred.resolve({
				data: attributes
			})
		}

		return deferred.promise;
	}

	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();

		if (type == "relation") {
			MetadataFormulaFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataFormulaFactory.findDatapodByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
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
			MetadataFormulaFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
		// if(type == 'paramlist'){
		// 	MetadataFormulaFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
		// 	var onSuccess = function (response) {
		// 		var attributes = [];
		// 		for (var j = 0; j < response.length; j++) {
		// 			var attributedetail = {};
		// 			attributedetail.uuid = response[j].ref.uuid;
		// 			attributedetail.datapodname = response[j].ref.name;
		// 			attributedetail.name = response[j].attrName;
		// 			attributedetail.dname =response[j].paramName //response[j].ref.name + "." + response[j].paramName;
		// 			attributedetail.attributeId = response[j].paramId;
		// 			attributes.push(attributedetail)
		// 		}
		// 		deferred.resolve({
		// 			data: attributes
		// 		})
		// 	}
		// }

		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataFormulaFactory.getGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

	this.getAllAttributesByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {


			//console.log(JSON.stringify(response))
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}

	this.getAttributesByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findDatapodByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getLatestByUuidForFunction = function (id, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		var functionArray = ["+", '-', "/", "%", "*", '(', ")", "=", "<=", ">=", "<", ">", "sum", "max", "mix", "count", "avg", "case", "when", "else", "end", "then"]
		MetadataFormulaFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var formulajosn = {};
			formulajosn.formuladata = response;
			var formulaarray = [];
			for (var i = 0; i < response.formulaInfo.length; i++) {
				var formulainfo = {};
				if (response.formulaInfo[i].ref.type == "simple") {
					if (functionArray.indexOf(response.formulaInfo[i].value.toLowerCase()) > -1) {
						formulainfo.type = response.formulaInfo[i].ref.type;
					}
					else {
						formulainfo.type = "string"
					}
					formulainfo.value = response.formulaInfo[i].value;
				}
				else if (response.formulaInfo[i].ref.type == "datapod" || response.formulaInfo[i].ref.type == "dataset" || response.formulaInfo[i].ref.type == "paramlist") {
					formulainfo.type = response.formulaInfo[i].ref.type;
					formulainfo.uuid = response.formulaInfo[i].ref.uuid;
					formulainfo.attrId = response.formulaInfo[i].attributeId;
					formulainfo.value = response.formulaInfo[i].ref.name + "." + response.formulaInfo[i].attributeName;
				}
				else {

					if (response.formulaInfo[i].ref.type == "function") {
						formulainfo.value = "";
					}
					else {

						formulainfo.value = response.formulaInfo[i].ref.name;
					}
					formulainfo.type = response.formulaInfo[i].ref.type;
					formulainfo.uuid = response.formulaInfo[i].ref.uuid;

				}
				formulaarray[i] = formulainfo;
			}
			//console.log(JSON.stringify(formulaarray))
			formulajosn.formulainfoarray = formulaarray;
			deferred.resolve({
				data: formulajosn
			})
		}
		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();

		MetadataFormulaFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		var functionArray = ["+", '-', "/", "%", "*", '(', ")", "=", "<=", ">=", "<", ">", "sum", "max", "mix", "count", "avg", "case", "when", "else", "end", "then"]
		MetadataFormulaFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var formulajosn = {};
			formulajosn.formuladata = response;
			var formulaarray = [];
			for (var i = 0; i < response.formulaInfo.length; i++) {
				var formulainfo = {};
				if (response.formulaInfo[i].ref.type == "simple") {
					if (functionArray.indexOf(response.formulaInfo[i].value.toLowerCase()) > -1) {
						formulainfo.type = response.formulaInfo[i].ref.type;
					}
					else {
						formulainfo.type = "string"
					}
					formulainfo.value = response.formulaInfo[i].value;

				}
				else if (response.formulaInfo[i].ref.type == "datapod" || response.formulaInfo[i].ref.type == "dataset") {

					formulainfo.type = response.formulaInfo[i].ref.type;
					formulainfo.uuid = response.formulaInfo[i].ref.uuid;
					formulainfo.attrId = response.formulaInfo[i].attributeId;
					formulainfo.value = response.formulaInfo[i].ref.name + "." + response.formulaInfo[i].attributeName;
				}
				else if (response.formulaInfo[i].ref.type == "paramlist") {
					formulainfo.type = response.formulaInfo[i].ref.type;
					formulainfo.uuid = response.formulaInfo[i].ref.uuid;
					formulainfo.attrId = response.formulaInfo[i].attributeId;
					formulainfo.value = response.formulaInfo[i].attributeName;

				}
				else {

					formulainfo.type = response.formulaInfo[i].ref.type;
					formulainfo.uuid = response.formulaInfo[i].ref.uuid;
					formulainfo.value = response.formulaInfo[i].ref.name;
				}
				formulaarray[i] = formulainfo;
			}
			//console.log(JSON.stringify(formulaarray))
			formulajosn.formulainfoarray = formulaarray;
			deferred.resolve({
				data: formulajosn
			})
		}
		return deferred.promise;
	}
	this.submit = function (data, type) {
		var deferred = $q.defer();
		MetadataFormulaFactory.submit(data, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
		MetadataFormulaFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
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
