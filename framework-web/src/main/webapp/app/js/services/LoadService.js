/**
 *
 */
MetadataModule = angular.module('MetadataModule');
MetadataModule.factory('MetadataLoadFactory', function ($http, $location) {
	var factory = {}
	factory.findAllLatest = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getAllLatest?action=view&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
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

	factory.findRegistryByDatasource = function (uuid, status) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getRegistryByDatasource?type=datasource&action=view&datasourceUuid=" + uuid + "&status=" + status,
		}).
			then(function (response, status, headers) {
				return response;
			})
	}
	factory.findAllVersionByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET"
		}).then(function (response) { return response })
	}
	return factory;
});

MetadataModule.service('MetadataLoadSerivce', function ($q, sortFactory, MetadataLoadFactory) {
	this.getRegistryByDatasource = function (uuid, status) {
		var deferred = $q.defer();
		MetadataLoadFactory.findRegistryByDatasource(uuid, status).then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
		var OnSuccess = function (response) {
			var result = []
			for (var i = 0; i < response.length; i++) {
				var resultjson = {};
				resultjson.id = response[i].id;
				resultjson.name = response[i].name;
				resultjson.dese = response[i].dese;
				resultjson.path = response[i].path;
				resultjson.registeredOn = response[i].registeredOn;
				resultjson.registeredBy = response[i].registeredBy;
				if (response[i].status == "UnRegistered") {
					resultjson.isDisabled = false;
					resultjson.status = "Not Registered"
				}
				else {
					resultjson.isDisabled = true;
					resultjson.status = response[i].status
				}
				resultjson.selected = false;
				result[i] = resultjson
			}
			deferred.resolve({
				data: result
			});
		}
		var onError = function (response) {
			deferred.reject({
				data: response
			})
		}
		return deferred.promise;
	}/*End getRegistryByDatasource*/

	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		MetadataLoadFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
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
	};

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataLoadFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};



	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataLoadFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;



	};

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataLoadFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		MetadataLoadFactory.submit(data, type, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
