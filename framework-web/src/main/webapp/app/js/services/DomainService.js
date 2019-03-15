/**
 *
 */
AdminModule = angular.module('AdminModule');
AdminModule.factory('DomainFactory', function ($http, $location) {
	var factory = {};
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

	factory.submit = function (data, upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type=domain&upd_tag=" + upd_tag,
			headers: {
				'Accept': '*/*',
				'content-Type': "application/json",
			},
			method: "POST",
			data: JSON.stringify(data),
		}).success(function (response) { return response })
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

AdminModule.service('DomainSerivce', function ($q, DomainFactory) {

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		DomainFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};
	this.getLatestByUuid = function (uuid, type) {
		var deferred = $q.defer();
		DomainFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		DomainFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		};
		var onError = function (response) {
			deferred.reject({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		DomainFactory.submit(data, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
