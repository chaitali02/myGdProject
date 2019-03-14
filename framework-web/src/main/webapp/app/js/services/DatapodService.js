/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataDatapodFactory', function ($http, $location) {
	var factory = {}
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
	factory.datapodSubmit = function (data, type, upd_tag) {
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
	factory.findDatasourceByType = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getDatasourceByType?action=view&type=" + type,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findDatapodSample = function (uuid, version, rows) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datapod/getDatapodSample?action=view&datapodUUID=" + uuid + "&datapodVersion=" + version + "&rows=" + rows,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findResultByDatastore = function (uuid, version, limit) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datastore/getResult?action=view&uuid=" + uuid + "&version=" + version + "&limit=" + limit,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.findDownloadSample = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datapod/download?action=view&datapodUUID=" + uuid + "&datapodVersion=" + version + "&row=100",
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findDatastoreByDatapod = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "metadata/getDatastoreByDatapod?action=view&uuid=" + uuid + "&version=" + version + "&type" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findCompareMetadata = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datapod/compareMetadata?action=view&uuid=" + uuid + "&version=" + version + "&type" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findSynchronizeMetadata = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datapod/synchronizeMetadata?action=view&uuid=" + uuid + "&version=" + version + "&type" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findAttrHistogram = function (uuid, version, type, attributeId) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "datapod/getAttrHistogram?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type+"&attributeId="+attributeId,
			method: "GET",
		}).then(function (response) { return response })
	}
    
	return factory;
});

MetadataModule.service('MetadataDatapodSerivce', function ($q, sortFactory, MetadataDatapodFactory) {
	
	this.getAttrHistogram = function (uuid, version, type, attributeId) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findAttrHistogram(uuid, version, type, attributeId).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.synchronizeMetadata = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findSynchronizeMetadata(uuid, version, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.compareMetadata = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findCompareMetadata(uuid, version, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.getResultByDatastore = function (uuid, version, limit) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findResultByDatastore(uuid, version, limit).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.getDatastoreByDatapod = function (data, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findDatastoreByDatapod(data.uuid, data.version, type).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.getDatapodSample = function (data, rows) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findDatapodSample(data.uuid, data.version, rows).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			//console.log(response)
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

	this.getDownloadSample = function (data) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findDownloadSample(data.uuid, data.version).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.getLatestDataSourceByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getDatasourceByType = function (type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findDatasourceByType(type).then(function (response) { OnSuccess(response.data) });
		var OnSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}/*End getDatasourceByType*/
	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var datapodjson = {};
			datapodjson.datapodata = response;
			var attributearray = [];
			for (var i = 0; i < response.attributes.length; i++) {
				var attribute = {};
				attribute.attributeId = response.attributes[i].attributeId;
				attribute.name = response.attributes[i].name;
				attribute.isAttributeEnable = true;
				attribute.dispName = response.attributes[i].dispName;
				attribute.type = response.attributes[i].type.toLowerCase();
				attribute.desc = response.attributes[i].desc;
				attribute.active = response.attributes[i].active;
				attribute.length = response.attributes[i].length;
				attribute.unit = response.attributes[i].unit;
				if (response.attributes[i].key != "" && response.attributes[i].key != null) {
					attribute.key = "Y";
				}
				else {
					attribute.key = "N";
				}
				attribute.partition = response.attributes[i].partition;

				attributearray[i] = attribute
			}
			//console.log(JSON.stringify(attributearray))
			datapodjson.attributes = attributearray
			deferred.resolve({
				data: datapodjson
			})
		}
		return deferred.promise;
	}
	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findOneById(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var datapodjson = {};
			datapodjson.datapodata = response;
			var attributearray = [];
			for (var i = 0; i < response.attributes.length; i++) {
				var attribute = {};
				attribute.attributeId = response.attributes[i].attributeId;
				attribute.name = response.attributes[i].name;
				attribute.dispName = response.attributes[i].dispName;
				attribute.type = response.attributes[i].type.toLowerCase();
				attribute.desc = response.attributes[i].desc;
				if (response.attributes[i].key != "" && response.attributes[i].key != null) {
					attribute.key = "Y";
				}
				else {
					attribute.key = "N";
				}
				attribute.partition = response.attributes[i].partition;

				attributearray[i] = attribute
			}
			datapodjson.attributes = attributearray
			deferred.resolve({
				data: datapodjson
			})
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataDatapodFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			var datapodjson = {};
			datapodjson.datapodata = response;
			var attributearray = [];
			for (var i = 0; i < response.attributes.length; i++) {
				var attribute = {};
				attribute.attributeId = response.attributes[i].attributeId;
				attribute.dispName = response.attributes[i].dispName;
				attribute.name = response.attributes[i].name;
				attribute.isAttributeEnable = true;
				attribute.type = response.attributes[i].type.toLowerCase();
				attribute.desc = response.attributes[i].desc;
				attribute.active = response.attributes[i].active;
				attribute.length = response.attributes[i].length;
				attribute.piiFlag = response.attributes[i].piiFlag;
				attribute.cdeFlag = response.attributes[i].cdeFlag;
				if (response.attributes[i].key != "" && response.attributes[i].key != null) {
					attribute.key = "Y";
				}
				else {
					attribute.key = "N";
				}
				attribute.partition = response.attributes[i].partition;
				if(response.attributes[i].domain !=null){
					var selectDomain={};
					selectDomain.uuid=response.attributes[i].domain.ref.uuid;
					selectDomain.type=response.attributes[i].domain.ref.type;
					selectDomain.name=response.attributes[i].domain.ref.name;
					attribute.selectDomain=selectDomain;
				}
				attributearray[i] = attribute
			}
			datapodjson.attributes = attributearray
			deferred.resolve({
				data: datapodjson
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
		MetadataDatapodFactory.datapodSubmit(data, type, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

});
