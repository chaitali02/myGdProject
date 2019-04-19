/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataDahsboardFactory', function ($http, $location) {
	var factory = {}
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

	factory.findVizpodByType = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getVizpodByType?action=view&uuid=" + uuid + "&type=" + type,

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
			url: url + "metadata/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
			method: "GET",

		}).then(function (response) { return response })
	}

	factory.submit = function (data,type,upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type="+type+"&upd_tag="+upd_tag,

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
			url: url + "metadata/getAttributesByDataset?action=view&uuid=" + uuid,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findAttributesByRelation = function (uuid,type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getAttributesByRelation?action=view&uuid=" + uuid + "&type=" + type,
		}).then(function (response, status, headers) {
			return response;
		})
	}

	factory.findFormulaByType = function(uuid,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
          method: 'GET',
          url: url + "metadata/getFormulaByType?action=view&uuid="+uuid+"&type="+type,
        }).then(function(response, status, headers) {
          return response;
        })
	}
	
	  
	return factory;
});

MetadataModule.service('MetadataDahsboardSerivce', function ($q, sortFactory, MetadataDahsboardFactory) {
	

	this.getFormulaByType = function(uuid,type) {
        var deferred = $q.defer();
        MetadataDahsboardFactory.findFormulaByType(uuid,type).then(function(response) {
          onSuccess(response.data)},function(response){onError(response)});
        var onSuccess = function(response) {
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
	this.getAllAttributeBySource = function (uuid, type) {
		var deferred = $q.defer();
		if (type == "relation") {
			MetadataDahsboardFactory.findAttributesByRelation(uuid, "relation", "").then(function (response) { onSuccess(response.data) });
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
					attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
					attributedetail.dname = response[j].ref.displayName + "." + response[j].attrName;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}
		}
		if (type == "dataset") {
			MetadataDahsboardFactory.findAttributesByDataset(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {


				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.attributeId = response[j].attrId;
					attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
					attributedetail.dname = response[j].ref.displayName + "." + response[j].attrName;
					attributes.push(attributedetail)
				}

				deferred.resolve({
					data: attributes
				})
			}


		}
		if (type == "datapod") {
			MetadataDahsboardFactory.findAttributesByDatapod(uuid, type).then(function (response) { onSuccess(response.data) });
			var onSuccess = function (response) {
				var attributes = [];
				for (var j = 0; j < response.length; j++) {
					var attributedetail = {};
					attributedetail.uuid = response[j].ref.uuid;
					attributedetail.type = response[j].ref.type;
					attributedetail.datapodname = response[j].ref.name;
					attributedetail.name = response[j].attrName;
					attributedetail.dname = response[j].ref.displayName + "." + response[j].attrName;
					attributedetail.id = response[j].ref.uuid + "_" + response[j].attrId;
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
		MetadataDahsboardFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getVizpodByType = function (uuid, type) {
		var deferred = $q.defer();
		MetadataDahsboardFactory.findVizpodByType(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataDahsboardFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getLatestByUuidView = function (id, type) {
		var deferred = $q.defer();
		MetadataDahsboardFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) },function (response) { onError(response.data)});
		var onSuccess = function (response) {
			var dashboardjson = {};
			dashboardjson.dashboarddata = response;
			var sectionArray = [];
			if (response.sectionInfo != null) {
				if (response.sectionInfo.length > 0) {
					for (var i = 0; i < response.sectionInfo.length; i++) {
						var sectionjson = {};
						var vizpodjson = {};
						sectionjson.sectionId = response.sectionInfo[i].sectionId;
						sectionjson.name = response.sectionInfo[i].name;
						vizpodjson.type = response.sectionInfo[i].vizpodInfo.ref.type
						vizpodjson.uuid = response.sectionInfo[i].vizpodInfo.ref.uuid;
						sectionjson.vizpod = vizpodjson;
						sectionjson.rowNo = response.sectionInfo[i].rowNo;
						sectionjson.colNo = response.sectionInfo[i].colNo;
						sectionArray[i] = sectionjson;
					}//End For
				}//End Inner IF
			}//End IF
			dashboardjson.sectionInfo = sectionArray
			var filterInfoArray = [];
			for (var i = 0; i < response.filterInfo.length; i++) {
				var filterinfo = {};
				filterinfo.uuid = response.filterInfo[i].ref.uuid;
				filterinfo.type = response.filterInfo[i].ref.type;
				filterinfo.name = response.filterInfo[i].ref.name;
				if(response.filterInfo[i].ref.type !="formula"){
					filterinfo.attributeId = response.filterInfo[i].attrId;
					filterinfo.dname = response.filterInfo[i].ref.displayName + "." + response.filterInfo[i].attrName;
					filterinfo.id = response.filterInfo[i].ref.uuid + "_" + response.filterInfo[i].attrId
				}
				else{
					filterinfo.dname = "formula"+"."+response.filterInfo[i].ref.displayName;
					filterinfo.id = response.filterInfo[i].ref.uuid;
				}
			

				filterInfoArray[i] = filterinfo;
			}
			dashboardjson.filterInfo = filterInfoArray
			deferred.resolve({
				data: dashboardjson
			})
		}
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		}
		return deferred.promise;



	};

	this.getOneByUuidAndVersionView = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataDahsboardFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) },function (response) { onError(response.data)});
		var onSuccess = function (response) {
			var dashboardjson = {};
			dashboardjson.dashboarddata = response;
			var sectionArray = [];
			if (response.sectionInfo != null) {
				if (response.sectionInfo.length > 0) {
					for (var i = 0; i < response.sectionInfo.length; i++) {
						var sectionjson = {};
						var vizpodjson = {};
						sectionjson.sectionId = response.sectionInfo[i].sectionId;
						sectionjson.name = response.sectionInfo[i].name;
						vizpodjson.type = response.sectionInfo[i].vizpodInfo.ref.type
						vizpodjson.uuid = response.sectionInfo[i].vizpodInfo.ref.uuid;
						sectionjson.vizpod = vizpodjson;
						sectionjson.rowNo = response.sectionInfo[i].rowNo;
						sectionjson.colNo = response.sectionInfo[i].colNo;
						sectionArray[i] = sectionjson;
					}//End For
				}//End Inner IF
			}//End IF
			dashboardjson.sectionInfo = sectionArray
			var filterInfoArray = [];
			for (var i = 0; i < response.filterInfo.length; i++) {
				var filterinfo = {};
				filterinfo.uuid = response.filterInfo[i].ref.uuid;
				filterinfo.type = response.filterInfo[i].ref.type;
				filterinfo.name = response.filterInfo[i].ref.name;
				if(response.filterInfo[i].ref.type !="formula"){
					filterinfo.attributeId = response.filterInfo[i].attrId;
					filterinfo.dname = response.filterInfo[i].ref.displayName + "." + response.filterInfo[i].attrName;
					filterinfo.id = response.filterInfo[i].ref.uuid + "_" + response.filterInfo[i].attrId;
				}
				else{
					filterinfo.dname = "formula"+"."+response.filterInfo[i].ref.displayName;
					filterinfo.id = response.filterInfo[i].ref.uuid;
				}
				filterInfoArray[i] = filterinfo;
			}
			dashboardjson.filterInfo = filterInfoArray
			deferred.resolve({
				data: dashboardjson
			})
		}
		var onError = function (response) {
			deferred.reject({
			  data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		MetadataDahsboardFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
		MetadataDahsboardFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var data = null;
			if (response.length > 0) {
				var data = {};
				data.options = [];
				var defaultoption = {};
				response.sort(sortFactory.sortByProperty("name"));
				defaultoption.name = response[0].name;
				defaultoption.displayName = response[0].displayName;
				defaultoption.uuid = response[0].uuid;
				data.defaultoption = defaultoption;
				for (var i = 0; i < response.length; i++) {
					var datajosn = {}
					datajosn.name = response[i].name;
					datajosn.displayName = response[i].displayName;
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
