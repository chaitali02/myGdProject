/**
 *
 */
DatavisualizationModule = angular.module('DatavisualizationModule');
DatavisualizationModule.factory('DahsboardFactory', function ($http, $location) {
	var factory = {}

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
	// factory.findVizpodResultsWithData = function (uuid, version, data) {
	// 	var url = $location.absUrl().split("app")[0]
	// 	return $http({
	// 		method: 'POST',
	// 		url: url + "vizpod/getVizpodResults/" + uuid + "/" + version + "?action=view&requestId=''",
	// 		headers: {
	// 			'Accept': '*/*',
	// 			'content-Type': "application/json",
	// 		},
	// 		data: JSON.stringify(data),
	// 	}).
	// 		then(function (response, status, headers) {
	// 			return response;
	// 		})
	// }
	factory.findVizpodResultsWithData = function (uuid, version,saveOnRefresh) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "vizpod/getVizpodResults?uuid=" + uuid + "&version=" + version + "&action=view&saveOnRefresh="+saveOnRefresh,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findVizpodDetails = function (uuid, version, data) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "vizpod/getVizpodResultDetails?uuid="+ uuid+ "&version=" + version + "&action=view",
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
	factory.findVizpodResults = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "vizpod/getVizpodResults/" + uuid + "/" + version + "?action=view&requestId=''",

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
	factory.findAllLatestCompleteObjects = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatestCompleteObjects?action=view&type=" + type,

		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findVizpodByType = function (uuid) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getVizpodByType?action=view&uuid=" + uuid,

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

	factory.findAllVersionByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET"
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
	factory.findAttributesByRelation = function (uuid,type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByRelation?action=view&uuid=" + uuid + "&type=" + type,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.executeDashboard = function(uuid,type,data) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'POST',
		  url: url + "dashboard/execute?action=view&uuid="+uuid+"&type="+type,
		  data:JSON.stringify(data),
		  headers: {
			'Accept': '*/*',
			'content-Type': "application/json",
		},
        }).then(function(response, status, headers) {
          return response;
        })
	  }
	  factory.findDasboardExecBySave = function (uuid, type, saveOnRefresh) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "dashboard/getDasboardExecBySave?action=view&uuid=" + uuid + "&type=" + type+"&saveOnRefresh="+saveOnRefresh,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	  
	return factory;
});

DatavisualizationModule.service('DahsboardSerivce', function ($q, sortFactory, DahsboardFactory) {
	
	this.getDasboardExecBySave = function (uuid, type, saveOnRefresh) {
		var deferred = $q.defer();
		DahsboardFactory.findDasboardExecBySave(uuid, type, saveOnRefresh).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		DahsboardFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.executeDashboard = function (uuid, type, data) {
		var deferred = $q.defer();
		DahsboardFactory.executeDashboard(uuid, type, data).then(function (response) { onSuccess(response.data)},function (response) { onError(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		var onError = function (response) {
			deferred.reject({
				data: response,
		    });
	    }
		return deferred.promise;
	};
	
	this.getAttributeValues = function (uuid, attributeId, type) {
		var deferred = $q.defer();

		DahsboardFactory.findAttributeValues(uuid,attributeId,type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getVizpodResults = function (uuid, version, saveOnRefresh, vizpodResuts) {
		var deferred = $q.defer();
		DahsboardFactory.findVizpodResultsWithData(uuid, version, saveOnRefresh).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				success: true,
				data: response,
				vizpodResuts:vizpodResuts
			})
		}
		var onError = function (response) {
			deferred.reject({
				success: false,
				data: response,
				vizpodResuts:vizpodResuts
			});
		}
		//}
		return deferred.promise;
	};
	this.getVizpodDetails = function (uuid, version, data) {
		var deferred = $q.defer();
		DahsboardFactory.findVizpodDetails(uuid, version, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		var onError = function (response) {
			deferred.reject({
				data: response
			});
		}
		return deferred.promise;
	};

	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();
		if (type == "relation") {
			// DahsboardFactory.findDatapodByRelation(uuid, "datapod").then(function (response) { onSuccess(response.data) });
			// var onSuccess = function (response) {
			// 	var attributes = [];
			// 	for (var j = 0; j < response.length; j++) {
			// 		for (var i = 0; i < response[j].attributes.length; i++) {
			// 			var attributedetail = {};
			// 			attributedetail.uuid = response[j].uuid;
			// 			attributedetail.uuid = response[j].type;
			// 			attributedetail.datapodname = response[j].name;
			// 			attributedetail.name = response[j].attributes[i].name;
			// 			attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
			// 			attributedetail.attributeId = response[j].attributes[i].attributeId;
			// 			attributes.push(attributedetail)
			// 		}
			// 	}
			// 	//console.log(JSON.stringify(attributes))
			// 	deferred.resolve({
			// 		data: attributes
			// 	})
			// }
			DahsboardFactory.findAttributesByRelation(uuid, type, "").then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.attributeId = response[j].attrId;
					attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
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
			DahsboardFactory.findAttributesByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {


				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.attributeId = response[j].attrId;
					attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
					attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}


		}
		if (type == "datapod") {
			DahsboardFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
					attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
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
		DahsboardFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getVizpodByType = function (uuid) {
		var deferred = $q.defer();
		DahsboardFactory.findVizpodByType(uuid).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		DahsboardFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		DahsboardFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var dashboardjson = {};
			dashboardjson.dashboarddata = response;
				var promises = [];
			for (var i = 0; i < response.sectionInfo.length; i++) {
				var promise = DahsboardFactory.findLatestByUuid(response.sectionInfo[i].vizpodInfo.ref.uuid, "vizpod")
				promises.push(promise)
			}
			var vizpodarray = [];
			$q.all(promises).then(function (result) {
				for (var i = 0; i < result.length; i++) {
					vizpodarray[i] = result[i].data
					dashboardjson.vizpod = vizpodarray
					deferred.resolve({
						data: dashboardjson
					})
				}//End For
			});
		}
		return deferred.promise;



	};

	this.getLatestByUuidView = function (uuid, type) {
		var deferred = $q.defer();
		DahsboardFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data, type) {
		var deferred = $q.defer();
		DahsboardFactory.submit(data, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}


		return deferred.promise;
	}
	this.getAllLatestCompleteObjects = function (type) {
		var deferred = $q.defer();

		DahsboardFactory.findAllLatestCompleteObjects(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		DahsboardFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

});
