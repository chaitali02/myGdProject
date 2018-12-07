/**
 *
 */
ProfileModule = angular.module('ProfileModule');
ProfileModule.factory('ProfileFactory', function ($http, $location) {
	var factory = {};
	factory.findAll = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatest?action=view&type=" + type
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
	factory.findProfileExecByProile = function (uuid) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "profile/getProfileExecByProfile?action=view&profileUUID=" + uuid,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.findProfileExecByPofileGroupExec = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "profile/getProfileExecByProfileGroupExec?action=view&profileGroupExecUuid=" + uuid + "&profileGroupExecVersion=" + version,
			method: "GET",

		}).success(function (response) { return response })
	}
	factory.executeProfiles = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "profile/execute?action=execute&uuid=" + uuid + "&version=" + version,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findResults = function (uuid, version, mode) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "profile/getResults?action=view&uuid=" + uuid + "&version=" + version + "&mode=" + mode,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findprofileGroupExecByProfileGroup = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "profile/getProfileGroupExecByProfileGroup?action=view&profileGroupUUID=" + uuid + "&profileGroupVersion=" + version,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.executeProfileGroup = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "profile/executeGroup?action=view&uuid=" + uuid + "&version=" + version,

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
	factory.findProfileExecByDatapod = function (uuid, startDate, endDate, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "profile/getProfileExecByDatapod?datapodUUID=" + uuid + "&action=view&type=profileexec" + "&startDate=" + startDate + "&endDate=" + endDate,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findNumRowsbyExec = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.findProfileResults = function (datapodUuid, datapodVersion, attributeId, numDays, profileAttrType) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "profile/getProfileResults?action=view&type=profileexec&datapodUuid=" + datapodUuid + "&datapodVersion=" + datapodVersion + "&attributeId=" + attributeId + "&numDays=" + numDays + "&profileAttrType=" + profileAttrType,
			method: "GET",
		}).then(function (response) { return response })
	}

	return factory;
});


ProfileModule.service("ProfileService", function ($q, ProfileFactory, sortFactory, CommonFactory) {

	this.getProfileResults = function (datapodUuid, datapodVersion, attributeId, numDays, profileAttrType) {
		var deferred = $q.defer();
		ProfileFactory.findProfileResults(datapodUuid, datapodVersion, attributeId, numDays, profileAttrType).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}

	this.getNumRowsbyExec = function (uuid, version, type) {
		var deferred = $q.defer();
		ProfileFactory.findNumRowsbyExec(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getProfileExecByDatapod = function (uuid, startDate, endDate, type) {
		var deferred = $q.defer();
		ProfileFactory.findProfileExecByDatapod(uuid, startDate, endDate, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			response.sort(sortFactory.sortByProperty("version", "desc"));
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.saveAs = function (uuid, version, type) {
		var deferred = $q.defer();
		ProfileFactory.findSaveAs(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		ProfileFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getProfileExecByPofileGroupExec = function (uuid, version) {
		var deferred = $q.defer();
		ProfileFactory.findProfileExecByPofileGroupExec(uuid, version).then(function (response) { onSuccess(response.data) });
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
							if (response[i].status[len].stage == "InProgress") {

								rowData[j] = "In Progress";
							}
							else if (response[i].status[len].stage == "NotStarted") {
								rowData[j] = "Not Started";
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
	this.getprofileGroupExecByProfileGroup = function (uuid, version) {
		var deferred = $q.defer();
		ProfileFactory.findprofileGroupExecByProfileGroup(uuid, version).then(function (response) { onSuccess(response.data) });
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
							if (response[i].status[len].stage == "InProgress") {

								rowData[j] = "In Progress";
							}
							else if (response[i].status[len].stage == "NotStarted") {
								rowData[j] = "Not Started";
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
	this.getProfileExecByProile = function (uuid) {
		var deferred = $q.defer();
		ProfileFactory.findProfileExecByProile(uuid).then(function (response) { onSuccess(response.data) });
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
							if (response[i].status[len].stage == "InProgress") {

								rowData[j] = "In Progress";
							}
							else if (response[i].status[len].stage == "NotStarted") {
								rowData[j] = "Not Started";
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
	this.getResults = function (uuid, version, mode) {
		var deferred = $q.defer();
		ProfileFactory.findResults(uuid, version, mode).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.executeProfile = function (uuid, version) {
		var deferred = $q.defer();
		ProfileFactory.executeProfiles(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.executeProfileGroup = function (uuid, version) {
		var deferred = $q.defer();
		ProfileFactory.executeProfileGroup(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		ProfileFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getOneByUuidAndVersionDQView = function (uuid, version, type) {
		var deferred = $q.defer();
		ProfileFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var dqJson = {};
			dqJson.dqdata = response
			dqJson.upperBound = response.rangeCheck.upperBound;
			dqJson.lowerBound = response.rangeCheck.lowerBound
			dqJson.maxLength = response.lengthCheck.maxLength
			dqJson.minLength = response.lengthCheck.minLength
			var filterInfoArray = [];
			if (response.filter != null) {
				for (var i = 0; i < response.filter.filterInfo.length; i++) {
					var filterInfo = {};
					var lhsFilter = {}
					filterInfo.logicalOperator = response.filter.filterInfo[i].logicalOperator
					filterInfo.operator = response.filter.filterInfo[i].operator;
					lhsFilter.uuid = response.filter.filterInfo[i].operand[0].ref.uuid;
					lhsFilter.datapodname = response.filter.filterInfo[i].operand[0].ref.name;
					lhsFilter.name = response.filter.filterInfo[i].operand[0].attributeName;
					lhsFilter.attributeId = response.filter.filterInfo[i].operand[0].attributeId;
					filterInfo.lhsFilter = lhsFilter;
					filterInfo.filtervalue = response.filter.filterInfo[i].operand[1].value;
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
	this.getLatestByUuidDQView = function (uuid, type) {
		var deferred = $q.defer();
		ProfileFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			var dqJson = {};
			dqJson.dqdata = response
			dqJson.upperBound = response.rangeCheck.upperBound;
			dqJson.lowerBound = response.rangeCheck.lowerBound
			dqJson.maxLength = response.lengthCheck.maxLength
			dqJson.minLength = response.lengthCheck.minLength
			var filterInfoArray = [];
			if (response.filter != null) {
				for (var i = 0; i < response.filter.filterInfo.length; i++) {
					var filterInfo = {};
					var lhsFilter = {}
					filterInfo.logicalOperator = response.filter.filterInfo[i].logicalOperator
					filterInfo.operator = response.filter.filterInfo[i].operator;
					lhsFilter.uuid = response.filter.filterInfo[i].operand[0].ref.uuid;
					lhsFilter.datapodname = response.filter.filterInfo[i].operand[0].ref.name;
					lhsFilter.name = response.filter.filterInfo[i].operand[0].attributeName;
					lhsFilter.attributeId = response.filter.filterInfo[i].operand[0].attributeId;
					filterInfo.lhsFilter = lhsFilter;
					filterInfo.filtervalue = response.filter.filterInfo[i].operand[1].value;
					filterInfoArray[i] = filterInfo
				}
			}
			dqJson.filterInfo = filterInfoArray;
			console.log(JSON.stringify(filterInfoArray))
			deferred.resolve({
				data: dqJson
			})
		}

		return deferred.promise;
	}
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		ProfileFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
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
		ProfileFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		ProfileFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}

		return deferred.promise;
	}
	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		ProfileFactory.dqSubmit(data, type, upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
		ProfileFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var result = response.sort(sortFactory.sortByProperty("name"));
			deferred.resolve({
				data: result
			})
		}
		return deferred.promise;
	}
	this.getAllLatestActive = function (type) {
		var deferred = $q.defer();
		ProfileFactory.findAllLatestActive(type).then(function (response) { onSuccess(response.data) });
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
		ProfileFactory.findFormulaBytype(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getAttributeByDatapod = function (uuid, type) {
		var deferred = $q.defer();
		ProfileFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
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
			ProfileFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				//console.log(JSON.stringify(response))
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					for (var i = 0; i < response[j].attributes.length; i++) {
						var attributedetail = {};
						attributedetail.uuid = response[j].uuid;
						attributedetail.datapodname = response[j].name;
						attributedetail.name = response[j].attributes[i].name;
						attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
						attributedetail.attributeId = response[j].attributes[i].attributeId;
						/* attributedetail.type=response[j].attributes[i].type;
						attributedetail.desc=response[j].attributes[i].desc;*/
						attributes.push(attributedetail)
					}
				}
				//
				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			ProfileFactory.findDatapodByDataset(uuid).then(function (response) { onSuccess(response.data) });
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
			ProfileFactory.findAttributeByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.attributeId = response[j].attrId;
					attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
					attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
					attributes.push(attributedetail)
				}
				deferred.resolve({
					data: attributes
				})
			}

		}

		return deferred.promise;
	}

	this.getAllDatapod = function (type) {
		var deferred = $q.defer();
		ProfileFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var data = {};
			data.options = [];
			var defaultoption = {};
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
			deferred.resolve({
				data: data
			})
		}
		return deferred.promise;
	}
	this.getAll = function (type) {
		var deferred = $q.defer();
		ProfileFactory.findAll(type).then(function (response) { onSuccess(response.data) });
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


	this.getAttrProfileResults = function (attrSourceSearchForm, attrTargetSearchForm) {
		var chartcolor = ["#c9c9ff", "#ffbdbd", "#f1cbff", "#95af5c", "#b6ccb1", "#c0cfaf", "#a3d0b5", "#31402f", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
		var deferred = $q.defer();
		console.log(attrTargetSearchForm)
		console.log(attrSourceSearchForm)
		//var baseUrl=$location.absUrl().split("app")[0]
		var promises = [];
		var apiParmList = ["profile/getProfileResults?action=view&type=profileexec&datapodUuid=" + attrSourceSearchForm.sourceDatapod.uuid + "&datapodVersion=" + attrSourceSearchForm.sourceDatapod.version + "&attributeId=" + attrSourceSearchForm.sourceAttribute.attributeId + "&numDays=" + attrSourceSearchForm.selectSourceProfilePriode + "&profileAttrType=" + attrSourceSearchForm.selectSourceProfileAttr, "profile/getProfileResults?action=view&type=profileexec&datapodUuid=" + attrTargetSearchForm.targetDatapod.uuid + "&datapodVersion=" + attrTargetSearchForm.targetDatapod.version + "&attributeId=" + attrTargetSearchForm.targetAttribute.attributeId + "&numDays=" + attrTargetSearchForm.selectTargetProfilePriode + "&profileAttrType=" + attrTargetSearchForm.selectTargetProfileAttr];
		for (var i = 0; i < apiParmList.length; i++) {
			var url = apiParmList[i]
			var promise = CommonFactory.httpGet(url)
			promises.push(promise)
		}
		var jobArray = []
		$q.all(promises).then(function (result) {

			deferred.resolve({
				data: result
			})
		});

		return deferred.promise;
	}
});
