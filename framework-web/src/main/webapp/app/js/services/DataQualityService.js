/**
 *
 */

DataQualityModule = angular.module('DataQualityModule');
DataQualityModule.factory('DataQualityFactory', function ($http, $location) {
	var factory = {};
	factory.findAll = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatest?action=view&type=" + type,

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
	factory.findAllLatestActive = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatest?action=view&active=Y&type=" + type,

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
	factory.findAttributeByDatapod = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=" + type,

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
	factory.findDatapodByDataset = function (uuid) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findFormulaBytype = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getFormulaByType?action=view&uuid=" + uuid + "&type=" + type,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.dqSubmit = function (data, type, upd_tag) {
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
	factory.findOneById = function (id, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getOneById?action=view&id=" + id + "&type=" + type,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.findDataQualExecByDataqual = function (uuid) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "dataQual/getDataQualExecByDataqual?action=view&dataQualUUID=" + uuid,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.finddqGroupExecBydqGroup = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "dataqual/getdqGroupExecBydqGroup?action=view&dqGroupUUID=" + uuid + "&dqGroupVersion=" + version,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.executeRule = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "dataqual/execute?action=execute&uuid=" + uuid + "&version=" + version,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	//factory.findDataQualResults=function(uuid,version,offset,limit,requestId,sortBy,order) {
	factory.findDataQualResults = function (url) {
		var baseurl = $location.absUrl().split("app")[0] + url
		return $http({
			url: baseurl,
			method: 'GET',
			// url:url+"dataQual/getDataQualResults?action=view&dataQualExecUUID="+uuid+"&dataQualExecVersion="+version+"&offset="+offset+"&limit="+limit+"&requestId="+requestId+"&order="+order+"&sortBy="+sortBy,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.finddqExecBydqGroupExec = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "dataqual/getdqExecBydqGroupExec?action=view&dataQualGroupExecUuid=" + uuid + "&dataQualGroupExecVersion=" + version,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.executeDQGroup = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "dataqual/executeGroup?action=execute&uuid=" + uuid + "&version=" + version,

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

	factory.findNumRowsbyExec = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=" + type,
			method: "GET",
		}).then(function (response) {
			return response
		})

	}
	factory.findDqExecByDatapod = function (uuid, startdate, enddate) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "dataqual/getdqExecByDatapod?action=view&datapodUUID=" + uuid + "&startDate=" + startdate + "&endDate=" + enddate
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findDqExecByDqwithParms = function (uuid, startDate, endDate) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "dataqual/getDataQualExecByDataqual?action=view&dataQualUUID=" + uuid + "&startDate=" + startDate + "&endDate=" + endDate
		}).
			then(function (response, status, headers) {
				return response;
			})
	}

	factory.findSummary = function (uuid, version, type, mode) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "dataqual/getSummary?action=view&dataQualExecUUID=" + uuid + "&dataQualExecVersion=" + version + "&type=" + type + "&mode=" + mode
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
	factory.findRelationByDatapod = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getRelationByDatapod?action=view&datapodUUID=" + uuid + "&type=" + type,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}

	

	return factory;
});


DataQualityModule.service("DataqulityService", function ($q, DataQualityFactory, sortFactory) {
	
	this.findRelationByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findRelationByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getFormulaByType = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findFormulaByType(uuid, type).then(function (response) { onSuccess(response.data) });
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
	this.getSummary = function (uuid, version, type, mode) {
		var deferred = $q.defer();
		DataQualityFactory.findSummary(uuid, version, type, mode).then(function (response) {
			onSuccess(response.data)
		});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getDqExecByDqwithParms = function (uuid, startDate, endDate) {
		var deferred = $q.defer();
		DataQualityFactory.findDqExecByDqwithParms(uuid, startDate, endDate).then(function (response) {
			onSuccess(response.data)
		});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getDqExecByDatapod = function (uuid, startDate, endDate) {
		var deferred = $q.defer();
		DataQualityFactory.findDqExecByDatapod(uuid, startDate, endDate).then(function (response) {
			onSuccess(response.data)
		});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}


	this.getNumRowsbyExec = function (uuid, version, type) {
		var deferred = $q.defer();
		DataQualityFactory.findNumRowsbyExec(uuid, version, type).then(function (response) {
			onSuccess(response.data)
		});
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.saveAs = function (uuid, version, type) {
		var deferred = $q.defer();
		DataQualityFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		DataQualityFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getdqExecBydqGroupExec = function (uuid, version) {
		var deferred = $q.defer();
		DataQualityFactory.finddqExecBydqGroupExec(uuid, version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var rowDataSet = [];
			var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status']
			for (var i = 0; i < response.length; i++) {

				var rowData = [];
				if (response[i].status != null) {
					response[i].status.sort(sortFactory.sortByProperty("createdOn"));
					var len = response[i].status.length - 1;
				}
				for (var j = 0; j < headerColumns.length; j++) {
					var columnname = headerColumns[j]
					if (columnname == "createdBy") {

						rowData[j] = response[i].createdBy.ref.name;
					}

					else if (columnname == "status") {
						if (response[i].status != null) {
							if (response[i].status[len].stage == "RUNNING") {

								rowData[j] = "RUNNING";
							}
							else if (response[i].status[len].stage == "PENDING") {
								rowData[j] = "PENDING";
							}
							else {
								rowData[j] = response[i].status[len].stage;
							}
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
	this.getdqGroupExecBydqGroup = function (uuid, version) {
		var deferred = $q.defer();
		DataQualityFactory.finddqGroupExecBydqGroup(uuid, version).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var rowDataSet = [];
			var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status']
			for (var i = 0; i < response.length; i++) {

				var rowData = [];
				if (response[i].status != null) {
					response[i].status.sort(sortFactory.sortByProperty("createdOn"));
					var len = response[i].status.length - 1;
				}
				for (var j = 0; j < headerColumns.length; j++) {
					var columnname = headerColumns[j]
					if (columnname == "createdBy") {

						rowData[j] = response[i].createdBy.ref.name;
					}

					else if (columnname == "status") {
						if (response[i].status != null) {
							if (response[i].status[len].stage == "RUNNING") {

								rowData[j] = "RUNNING";
							}
							else if (response[i].status[len].stage == "PENDING") {
								rowData[j] = "PENDING";
							}
							else {
								rowData[j] = response[i].status[len].stage;
							}
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
	this.getDataQualExecByDataqual = function (uuid) {
		var deferred = $q.defer();
		DataQualityFactory.findDataQualExecByDataqual(uuid).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var rowDataSet = [];
			var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status']
			for (var i = 0; i < response.length; i++) {

				var rowData = [];
				if (response[i].status != null) {
					response[i].status.sort(sortFactory.sortByProperty("createdOn"));
					var len = response[i].status.length - 1;
				}
				for (var j = 0; j < headerColumns.length; j++) {
					var columnname = headerColumns[j]
					if (columnname == "createdBy") {

						rowData[j] = response[i].createdBy.ref.name;
					}

					else if (columnname == "status") {
						if (response[i].status != null) {
							if (response[i].status[len].stage == "RUNNING") {

								rowData[j] = "RUNNING";
							}
							else if (response[i].status[len].stage == "PENDING") {
								rowData[j] = "PENDING";
							}
							else {
								rowData[j] = response[i].status[len].stage;
							}
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
	this.getDataQualResults = function (uuid, version, offset, limit, requestId, sortBy, order) {
		var deferred = $q.defer();
		var url;

		if (sortBy == null && order == null) {
			url = "dataqual/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId;
		}
		else {
			url = "dataqual/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId + "&order=" + order + "&sortBy=" + sortBy;

		}
		//DataQualityFactory.findDataQualResults(uuid,version,offset,limit,requestId,sortBy,order).then(function(response){onSuccess(response)});
		DataQualityFactory.findDataQualResults(url).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
	this.getDataQualResults2 = function (uuid, version, offset, limit, requestId, sortBy, order, resultType) {
		var deferred = $q.defer();
		var url;
		if (resultType == "detail") {
			if (sortBy == null && order == null) {
				url = "dataqual/getResultDetail?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId;
			}
			else {
				url = "dataqual/getResultDetail?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId + "&order=" + order + "&sortBy=" + sortBy;

			}
		} else {
			if (sortBy == null && order == null) {
				url = "dataqual/getResultSummary?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId;
			}
			else {
				url = "dataqual/getResultSummary?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId + "&order=" + order + "&sortBy=" + sortBy;

			}
		} DataQualityFactory.findDataQualResults(url).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

	this.executeDQRule = function (uuid, version) {
		var deferred = $q.defer();
		DataQualityFactory.executeRule(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.executeDQGroup = function (uuid, version) {
		var deferred = $q.defer();
		DataQualityFactory.executeDQGroup(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getOneByUuidAndVersionDQView = function (uuid, version, type) {
		var deferred = $q.defer();
		DataQualityFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			if(response){
				var dqJson = {};
				dqJson.dqdata = response
				dqJson.upperBound = response.rangeCheck.upperBound;
				dqJson.lowerBound = response.rangeCheck.lowerBound
				dqJson.maxLength = response.lengthCheck.maxLength
				dqJson.minLength = response.lengthCheck.minLength
				var filterInfoArray = [];
				if (response.filterInfo != null) {
					for (i = 0; i < response.filterInfo.length; i++) {
						var filterInfo = {};
						filterInfo.logicalOperator = response.filterInfo[i].logicalOperator;
						filterInfo.operator = response.filterInfo[i].operator;
						var rhsTypes = null;
						filterInfo.rhsTypes = null;
						if (filterInfo.operator == 'BETWEEN') {
							filterInfo.rhsTypes = DataQualityFactory.disableRhsType(['attribute', 'formula', 'dataset', 'function', 'paramlist'])
						} else if (['IN', 'NOT IN'].indexOf(filterInfo.operator) != -1) {
							filterInfo.rhsTypes = DataQualityFactory.disableRhsType([]);
						} else if (['<', '>', "<=", '>='].indexOf(filterInfo.operator) != -1) {
							filterInfo.rhsTypes = DataQualityFactory.disableRhsType(['dataset']);
						}
						else if (['EXISTS', 'NOT EXISTS'].indexOf(filterInfo.operator) != -1) {
							filterInfo.rhsTypes = DataQualityFactory.disableRhsType(['attribute', 'formula', 'function', 'paramlist', 'string', 'integer']);
						}
						else if (['IS'].indexOf(filterInfo.operator) != -1) {

							filterInfo.rhsTypes = DataQualityFactory.disableRhsType(['attribute', 'formula', 'dataset', 'function', 'paramlist', 'integer']);
						}
						else {
							filterInfo.rhsTypes = DataQualityFactory.disableRhsType(['dataset']);
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
							if (response.filterInfo[i].operator == "BETWEEN") {
								obj.caption = "integer";
								filterInfo.rhsvalue1 = response.filterInfo[i].operand[1].value.split("and")[0];
								filterInfo.rhsvalue2 = response.filterInfo[i].operand[1].value.split("and")[1];
							} else if (['<', '>', "<=", '>='].indexOf(response.filterInfo[i].operator) != -1) {
								obj.caption = response.filterInfo[i].operand[1].attributeType;
								filterInfo.rhsvalue = response.filterInfo[i].operand[1].value

							} else if (response.filterInfo[i].operator == '=' && response.filterInfo[i].operand[1].attributeType == "integer") {
								obj.caption = "integer";
								filterInfo.rhsvalue = response.filterInfo[i].operand[1].value
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
							rhsdatapodAttribute.uuid = response.filterInfo[i].operand[1].ref.uuid;
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
				dqJson.filterInfo = filterInfoArray;

				deferred.resolve({
					data: dqJson
				});
		    }
		};
		var onError = function (response) {
			deferred.reject({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getLatestByUuidDQView = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var dqJson = {};
			dqJson.dqdata = response
			dqJson.upperBound = response.rangeCheck.upperBound;
			dqJson.lowerBound = response.rangeCheck.lowerBound
			dqJson.maxLength = response.lengthCheck.maxLength
			dqJson.minLength = response.lengthCheck.minLength
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
			dqJson.filterInfo = filterInfoArray;
			deferred.resolve({
				data: dqJson
			})
		}

		return deferred.promise;
	}
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		DataQualityFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
	this.getOneByUuidAndVersion1 = function (uuid, version, type) {
		var deferred = $q.defer();
		DataQualityFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.getLatestByUuid = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		DataQualityFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		DataQualityFactory.dqSubmit(data, type, upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
		DataQualityFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var result = [];
			if (response != null) {
				result = response.sort(sortFactory.sortByProperty("name"));
			}
			deferred.resolve({
				data: result
			})
		}
		return deferred.promise;
	}
	this.getExpressionByType = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findExpressionByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var result = [];
			for (var i = 0; i < response.length; i++) {
				var obj = {}
				obj.name = response[i].ref.name;
				obj.uuid = response[i].ref.uuid;
				result.push(obj);
			}
			deferred.resolve({
				data: result
			})
		}
		return deferred.promise;
	}
	this.getAllLatestActive = function (type) {
		var deferred = $q.defer();
		DataQualityFactory.findAllLatestActive(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var result = response.sort(sortFactory.sortByProperty("name"));
			deferred.resolve({
				data: result
			})
		}
		return deferred.promise;
	}
	this.getFormulaBytype = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findFormulaBytype(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getAttributeByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		DataQualityFactory.findAttributeByDatapod(uuid, 'datapod').then(function (response) { onSuccess(response.data) });
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
		return deferred.promise;
	}
	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();
		if (type == "relation") {
			DataQualityFactory.findAttributesByRelation(uuid, "relation", "").then(function (response) { onSuccess(response.data) });
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
					attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			DataQualityFactory.findDatapodByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
				// console.log(JSON.stringify(attributes))
				deferred.resolve({
					data: attributes
				})
			}


		}
		if (type == "datapod") {

			DataQualityFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
		if (type == "paramlist") {
			DataQualityFactory.findParamByParamList(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
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
	this.getAll = function (type) {
		var deferred = $q.defer();
		DataQualityFactory.findAll(type).then(function (response) { onSuccess(response.data) });
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
});
