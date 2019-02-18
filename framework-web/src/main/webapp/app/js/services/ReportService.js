/**
 *
 */
DatavisualizationModule = angular.module('DatavisualizationModule')
DatavisualizationModule.factory('ReportFactory', function ($http, $location) {
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
	factory.findOneByUuidAndVersion = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=report",
			method: "GET",

		}).then(function (response) { return response })

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

	factory.Submit = function (data, type, upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type="+type+"&upd_tag=" + upd_tag,

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
	factory.findReportSample = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "report/getReportSample?action=view&uuid=" + uuid + "&version=" + version + "&rows=100",
			method: "GET",
		}).then(function (response) { return response })
	}
    factory.findReportByReportExec = function (uuid,type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "report/getReportByReportExec?action=view&uuid=" + uuid+"&type="+type,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.findExecute = function (uuid, version, data) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "report/execute?action=execute&uuid="+uuid+"&version="+version+"&type=report",
			headers: {
				'Accept': '*/*',
				'content-Type': "application/json",
			},
			data: JSON.stringify(data),
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findAttributeValues = function (uuid, attributeId, type) {
		var url = $location.absUrl().split("app")[0]
		if (type == "datapod") {
			url = url + "datapod/getAttributeValues1?action=view&datapodUUID=" + uuid + "&attributeId=" + attributeId;
		}
		else if(type == "formula"){
			url = url + "datapod/getFormulaValues?action=view&uuid=" + uuid+"&type="+type
		}
		else {
			url = url + "dataset/getAttributeValues?action=view&uuid=" + uuid + "&attributeId=" + attributeId;
		}
		return $http({
			method: 'GET',
			url: url,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	return factory;
});

DatavisualizationModule.service('ReportSerivce', function ($http, $q, sortFactory, ReportFactory) {
	this.getReportByReportExec = function (uuid, type) {
		var deferred = $q.defer();
		ReportFactory.findReportByReportExec(uuid,type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	
	this.getAttributeValues = function (uuid, attributeId, type) {
		var deferred = $q.defer();

		ReportFactory.findAttributeValues(uuid,attributeId,type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.reportExecute = function (uuid,version,data) {
		var deferred = $q.defer();
		ReportFactory.findExecute(uuid,version,data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.getReportSample = function (data) {
		var deferred = $q.defer();
		ReportFactory.findReportSample(data.uuid, data.version).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
			ReportFactory.findAttributesByRelation(uuid, "relation", "").then(function (response) { onSuccess(response.data) });
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
					attributedetail.id=response[j].ref.uuid+"_"+j;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			ReportFactory.findAttributesByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
					attributedetail.id=response[j].ref.uuid+"_"+j;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}


		}
		if (type == "datapod") {
			ReportFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
					attributedetail.id=response[j].ref.uuid+"_"+j;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}

		}
		if (type == "paramlist") {
			ReportFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
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
					attributedetail.id=response[j].ref.uuid+"_"+j;
					attributes.push(attributedetail);
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
		ReportFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		ReportFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var formulaarray = [];
			for (var i = 0; i < response.length; i++) {
				var formulajson = {}
				formulajson.name = response[i].ref.name;
				formulajson.uuid = response[i].ref.uuid;
				formulajson.id = response[i].ref.uuid;
				formulajson.class = "";
				formulajson.iconclass = "";
				formulajson.dname = "formula"+"."+response[i].ref.name
				formulajson.name = response[i].ref.name
				formulajson.type = "formula"
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
		ReportFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var expressionaarray = [];
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
		ReportFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
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


	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		ReportFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		ReportFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		ReportFactory.Submit(data, type, upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

	this.getOneByUuidAndVersion = function (id, version) {
		var deferred = $q.defer();
		ReportFactory.findOneByUuidAndVersion(id, version).then(function (response) { onSuccess(response.data) },function (response) { onError(response.data)});
		var onSuccess = function (response) {
			var reportViewJson = {};
			reportViewJson.report = response;
			var tags = [];
			if (response.tags) {
				for (var i = 0; i < response.tags.length; i++) {
					var tag = {};
					tag.text = response.tags[i];
					tags[i] = tag
				}
			}
			reportViewJson.tags = tags;
			var filterInfoArray = [];
			if(response.filterInfo !=null){
				for (var i = 0; i < response.filterInfo.length; i++) {
					var filterinfo = {};
					filterinfo.uuid = response.filterInfo[i].ref.uuid;
					filterinfo.type = response.filterInfo[i].ref.type;
					if(response.filterInfo[i].ref.type !="formula"){
						filterinfo.attributeId = response.filterInfo[i].attrId;
						filterinfo.dname = response.filterInfo[i].ref.name + "." + response.filterInfo[i].attrName;
						filterinfo.id = response.filterInfo[i].ref.uuid + "_" + response.filterInfo[i].attrId;
					}
					else{
						filterinfo.dname = "formula"+"."+response.filterInfo[i].ref.name;
						filterinfo.id = response.filterInfo[i].ref.uuid;
					}
					filterInfoArray[i] = filterinfo;
				}
		    }
			reportViewJson.filterInfo = filterInfoArray
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
				if (response.attributeInfo[n].sourceAttr.ref.type == "datapod" || response.attributeInfo[n].sourceAttr.ref.type == "dataset") {
					var sourcedatapod = {};
					sourcedatapod.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourcedatapod.type = response.attributeInfo[n].sourceAttr.ref.type;
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
				if (response.attributeInfo[n].sourceAttr.ref.type == "paramlist") {
					var sourceparamlist = {};
					sourceparamlist.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
					sourceparamlist.type = response.attributeInfo[n].sourceAttr.ref.type;
					sourceparamlist.attributeId = response.attributeInfo[n].sourceAttr.attrId;
					sourceparamlist.attrType = response.attributeInfo[n].sourceAttr.attrType
					sourceparamlist.name = "";
					var obj = {}
					obj.text = "paramlist"
					obj.caption = "paramlist"
					attributeInfo.sourceAttributeType = obj;
					attributeInfo.sourceparamlist = sourceparamlist;
					attributeInfo.isSourceAtributeSimple = false;
					attributeInfo.isSourceAtributeDatapod = false;
					attributeInfo.isSourceAtributeFormula = false;
					attributeInfo.isSourceAtributeExpression = false;
					attributeInfo.isSourceAtributeFunction = false;
					attributeInfo.isSourceAtributeParamList = true;
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
					attributeInfo.isSourceAtributeParamList = false;

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
					attributeInfo.isSourceAtributeParamList = false;

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
					attributeInfo.isSourceAtributeParamList = false;

				}
				sourceAttributesArray[n] = attributeInfo
			}
			reportViewJson.sourceAttributes = sourceAttributesArray
			deferred.resolve({
				data: reportViewJson
			})
		};
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		  }

		return deferred.promise;
	}

	this.getAllLatestFunction = function (metavalue, inputFlag) {
		var deferred = $q.defer();
		ReportFactory.findAllLatest(metavalue, inputFlag).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}



});
