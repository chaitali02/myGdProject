/**
 *
 */
BatchModule = angular.module('BatchModule');
BatchModule.factory('BatchFactory', function ($http, $location) {
	
	var factory = {};
	factory.findAllLatest = function (metavalue) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatest?action=view&type=" + metavalue,
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

	factory.executeBatch= function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "batch/execute?action=execute&uuid=" + uuid + "&version=" + version,
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.submit = function (data,type,upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type=" + type+"&upd_tag="+upd_tag,
			headers: {
				'Accept': '*/*',
				'content-Type': "application/json",
			},
			method: "POST",
			data: JSON.stringify(data),
		}).success(function (response) { return response })
	}

	return factory;
})


BatchModule.service("BatchService", function ($q, BatchFactory, sortFactory) {

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		BatchFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		BatchFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		BatchFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		BatchFactory.findAllLatest(type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		BatchFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		BatchFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

	this.execute = function (uuid, version) {
		var deferred = $q.defer();
		BatchFactory.executeBatch(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
});
