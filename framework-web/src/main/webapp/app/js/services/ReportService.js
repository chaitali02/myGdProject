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
			url: url + "common/submit?action=edit&type=" + type + "&upd_tag=" + upd_tag,

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
	factory.findReportByReportExec = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "report/getReportByReportExec?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.findExecute = function (uuid, version, data) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "report/execute?action=execute&uuid=" + uuid + "&version=" + version + "&type=report",
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
	factory.reSendEMail = function (uuid, version, mode, data) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "report/reSendEMail?action=view&uuid=" + uuid + "&version=" + version + "&type=reportexec&mode="+mode,
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
		else if (type == "formula") {
			url = url + "datapod/getFormulaValues?action=view&uuid=" + uuid + "&type=" + type
		}
		else {
			url = url + "dataset/getAttributeValues?action=view&uuid=" + uuid + "&attributeId=" + attributeId;
		}
		return $http({
			method: 'GET',
			url: url,

		}).then(function (response, status, headers) {
			return response;
	})
	
	}
	factory.findNumRowsbyExec = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=reportexec",
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
     
	factory.disableRhsType = function (arrayStr) {
		var rTypes = [
			{ "text": "string", "caption": "string", "disabled": false },
			{ "text": "string", "caption": "integer", "disabled": false },
			{ "text": "datapod", "caption": "attribute", "disabled": false },
			{ "text": "formula", "caption": "formula", "disabled": false },
			{ "text": "dataset", "caption": "dataset", "disabled": false },
			{ "text": "paramlist", "caption": "paramlist", "disabled": false },
			{ "text": "function", "caption": "function", "disabled": false }]
		for (var i = 0; i < rTypes.length; i++) {
			rTypes[i].disabled = false;
			if (arrayStr.length > 0) {
				var index = arrayStr.indexOf(rTypes[i].caption);
				if (index != -1) {
					rTypes[i].disabled = true;
				}
			}
		}
		return rTypes;
	}
	factory.findReprotExecViewByCriteria = function(type, name, userName, startDate, endDate, tags, active,published,status) {
		var url = $location.absUrl().split("app")[0]
		return $http({
		  url: url + "reprot/getReprotExecViewByCriteria?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&active=" + active +  "&published=" + published +"&status=" + status,
		  method: "GET",
		}).then(function(response) {
		  return response
		})
	  }
	return factory;
});

DatavisualizationModule.service('ReportSerivce', function ($q, sortFactory, ReportFactory, CF_GRID) {
	
	this.getReprotExecViewByCriteria = function(type, name, userName, startDate, endDate, tags, active,published,status) {
		var deferred = $q.defer();
		CommonFactory.findReprotExecViewByCriteria(type, name, userName, startDate, endDate, tags, active,published,status).then(function(response) {
		  onSuccess(response.data)
		});
		var onSuccess = function(response) {
		  var results = []
		  for (var i = 0; i < response.length; i++) {
			var result = {};
			if (response[i].statusList != null) {
			  response[i].statusList.sort(sortFactory.sortByProperty("createdOn"));
			  var len = response[i].statusList.length - 1;
			}
			result.index=i;
			result.id = response[i].id;
			result.uuid = response[i].uuid;
			result.version = response[i].version;
			result.name = response[i].name;
			result.createdBy = response[i].createdBy;
			result.createdOn = response[i].createdOn;
			result.active = response[i].active;
			
			result.startTime="-NA-";
			result.endTime="-NA-";
			result.duration="-NA-"
			if(type =="reportExec" ){
			  result.numRows=response[i].numRows;
			  if(response[i].sizeMB!=null){
				if(response[i].sizeMB < 1){
				  result.sizeMB=" < 1"
				}else{
				  result.sizeMB=response[i].sizeMB
				}
			  }else{
				result.sizeMB="-NA-"
			  }
			}
			if(response[i].statusList !=null && response[i].statusList.length > 1){
			  for(var j=0;j<response[i].statusList.length;j++){
				if(response[i].statusList[j].stage == "PENDING"){
				  result.startTime=$filter('date')(new Date(response[i].statusList[j].createdOn), "EEE MMM dd HH:mm:ss yyyy");
				  break;
				}
			  }
			 
			  if(response[i].statusList[len].stage == "COMPLETED"){
				result.endTime=$filter('date')(new Date(response[i].statusList[len].createdOn), "EEE MMM dd HH:mm:ss yyyy");
				var date1 = new Date(result.startTime)
				var date2 = new Date(result.endTime)
				result.duration= moment.utc(moment(date2).diff(moment(date1))).format("HH:mm:ss")
					 
			  }
	  
			  }
			if(response[i].statusList !=null && response[i].statusList.length > 0){
			  if (response[i].statusList[len].stage == "PENDING") {
				result.status = "PENDING"
			  } else if (response[i].statusList[len].stage == "RUNNING") {
				result.status = "RUNNING"
			  } else {
				result.status = response[i].statusList[len].stage;
			  }
			}
			else{
			  result.status="-NA-";
			}
			results[i] = result
		  }
		  deferred.resolve({
			data: results
		  })
		}
		return deferred.promise;
	} /*
	this.getNumRowsbyExec = function (uuid, type) {
		var deferred = $q.defer();
		ReportFactory.findNumRowsbyExec(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getReportByReportExec = function (uuid, type) {
		var deferred = $q.defer();
		ReportFactory.findReportByReportExec(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getAttributeValues = function (uuid, attributeId, type) {
		var deferred = $q.defer();

		ReportFactory.findAttributeValues(uuid, attributeId, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.reSendEMail = function (uuid, version, mode ,data) {
		var deferred = $q.defer();
		ReportFactory.reSendEMail(uuid, version, mode ,data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.reportExecute = function (uuid, version, data) {
		var deferred = $q.defer();
		ReportFactory.findExecute(uuid, version, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
					attributedetail.id = response[j].ref.uuid + "_" + j;
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
					attributedetail.id = response[j].ref.uuid + "_" + j;
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
					attributedetail.id = response[j].ref.uuid + "_" + j;
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
					attributedetail.id = response[j].ref.uuid + "_" + j;
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
				formulajson.dname = "formula" + "." + response[i].ref.name
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
		ReportFactory.findOneByUuidAndVersion(id, version).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
			/*var filterInfoArray = [];
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
			reportViewJson.filterInfo = filterInfoArray*/
			var filterInfoArray = [];
			if (response.filterInfo != null) {
				for (i = 0; i < response.filterInfo.length; i++) {
					var filterInfo = {};
					filterInfo.logicalOperator = response.filterInfo[i].logicalOperator;
					filterInfo.operator = response.filterInfo[i].operator;
					filterInfo.isRhsNA = false;
					var rhsTypes = null;
					filterInfo.rhsTypes = null;
					if (filterInfo.operator == 'BETWEEN') {
						filterInfo.rhsTypes = ReportFactory.disableRhsType(['attribute', 'formula', 'dataset', 'function', 'paramlist'])
					}
					else if (['IN', 'NOT IN'].indexOf(filterInfo.operator) != -1) {
						filterInfo.rhsTypes = ReportFactory.disableRhsType([]);
					} else if (['<', '>', "<=", '>='].indexOf(filterInfo.operator) != -1) {
						filterInfo.rhsTypes = ReportFactory.disableRhsType(['string', 'dataset']);
					}
					else if (['EXISTS', 'NOT EXISTS'].indexOf(filterInfo.operator) != -1) {
						filterInfo.rhsTypes = ReportFactory.disableRhsType(['attribute', 'formula', 'function', 'paramlist', 'string', 'integer']);
					}
					else if (['IS'].indexOf(filterInfo.operator) != -1) {

						filterInfo.rhsTypes = ReportFactory.disableRhsType(['attribute', 'formula', 'dataset', 'function', 'paramlist', 'integer']);
					}
					else {
						filterInfo.rhsTypes = ReportFactory.disableRhsType(['dataset']);
					}

					if (response.filterInfo[i].operand[0].ref.type == "simple") {
						var obj = {}
						obj.text = "string"
						obj.caption = "string"
						filterInfo.lhstype = obj;
						filterInfo.islhsSimple = true;
						filterInfo.islhsDatapod = false;
						filterInfo.islhsFormula = false;
						filterInfo.lhsvalue = response.filterInfo[i].operand[0].value;
						if (response.filterInfo[i].operand[0].attributeType == "integer") {
							obj.caption = "integer";
						}
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
						lhsdatapodAttribute.type = response.filterInfo[i].operand[0].ref.type;
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
						lhsformula.type = response.filterInfo[i].operand[0].ref.type;
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
						filterInfo.isrhsParamlist = false;
						filterInfo.isrhsFunction = false;
						filterInfo.rhsvalue = response.filterInfo[i].operand[1].value;
						var temp = response.filterInfo[i].operator;
						temp = temp.replace(/ /g, '');
						if (response.filterInfo[i].operator == "BETWEEN") {
							obj.caption = "integer";
							filterInfo.rhsvalue1 = response.filterInfo[i].operand[1].value.split("and")[0];
							filterInfo.rhsvalue2 = response.filterInfo[i].operand[1].value.split("and")[1];
						} else if (['<', '>', "<=", '>='].indexOf(response.filterInfo[i].operator) != -1) {
							obj.caption = "integer";
							filterInfo.rhsvalue = response.filterInfo[i].operand[1].value

						} else if (response.filterInfo[i].operator == '=' && response.filterInfo[i].operand[1].attributeType == "integer") {
							obj.caption = "integer";
							filterInfo.rhsvalue = response.filterInfo[i].operand[1].value
						}

						else if (temp == "ISNULL" || temp == "ISNOTNULL") {
							filterInfo.isRhsNA = true;
						}
						else {
							filterInfo.rhsvalue = response.filterInfo[i].operand[1].value//.replace(/["']/g, "");
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
						filterInfo.isrhsParamlist = false;
						filterInfo.isrhsFunction = false;
						rhsdatapodAttribute.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.type = response.filterInfo[i].operand[1].ref.type;
						rhsdatapodAttribute.datapodname = response.filterInfo[i].operand[1].ref.name;
						rhsdatapodAttribute.name = response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
						rhsdatapodAttribute.attributeId = response.filterInfo[i].operand[1].attributeId;
						filterInfo.rhsdatapodAttribute = rhsdatapodAttribute;
					}
					else if (response.filterInfo[i].operand[1].ref.type == "dataset" && response.dependsOn.ref.uuid == response.filterInfo[i].operand[1].ref.uuid) {
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
						rhsdatapodAttribute.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsdatapodAttribute.type = response.filterInfo[i].operand[1].ref.type;
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
						filterInfo.isrhsParamlist = false;
						filterInfo.isrhsFunction = false;
						rhsformula.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsformula.type = response.filterInfo[i].operand[1].ref.type;
						rhsformula.name = response.filterInfo[i].operand[1].ref.name;
						filterInfo.rhsformula = rhsformula;
					}
					else if (response.filterInfo[i].operand[1].ref.type == "function") {
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
						rhsfunction.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsfunction.type = response.filterInfo[i].operand[1].ref.type;
						rhsfunction.name = response.filterInfo[i].operand[1].ref.name;
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
						filterInfo.isrhsParamlist = false;
						filterInfo.isrhsFunction = false;
						rhsdataset.uuid = response.filterInfo[i].operand[1].ref.uuid;
						rhsdataset.type = response.filterInfo[i].operand[1].ref.type;
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
						rhsparamlist.type = response.filterInfo[i].operand[1].ref.type;
						rhsparamlist.datapodname = response.filterInfo[i].operand[1].ref.name;
						rhsparamlist.name = response.filterInfo[i].operand[1].attributeName;
						rhsparamlist.dname = response.filterInfo[i].operand[1].ref.name + "." + response.filterInfo[i].operand[1].attributeName;
						rhsparamlist.attributeId = response.filterInfo[i].operand[1].attributeId;

						filterInfo.rhsparamlist = rhsparamlist;
					}

					filterInfoArray[i] = filterInfo
				}
			}
			reportViewJson.filterInfo = filterInfoArray;

			var sourceAttributesArray = [];
			for (var n = 0; n < response.attributeInfo.length; n++) {
				var attributeInfo = {};
				attributeInfo.name = response.attributeInfo[n].attrSourceName;
				if (response.attributeInfo.length > CF_GRID.framework_autopopulate_grid)
					attributeInfo.isOnDropDown = false;
				else
					attributeInfo.isOnDropDown = true;
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
					sourcedatapod.name = response.attributeInfo[n].sourceAttr.attrName;
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
					sourceparamlist.name = response.attributeInfo[n].sourceAttr.ref.name;
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
