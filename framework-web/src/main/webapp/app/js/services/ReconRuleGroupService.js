/**
 *
 */
ReconModule = angular.module('ReconModule');

ReconModule.factory('RuleGroupFactory', function ($http, $location) {
	
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

	factory.findByUuid = function (uuid, type) {
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
	
	factory.findByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
    
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

	factory.executeRuleGroup = function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "recon/executeGroup?action=execute&uuid=" + uuid + "&version=" + version,
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


ReconModule.service("RuleGroupService", function ($q, RuleGroupFactory, sortFactory) {

	this.getByUuid = function (id, type) {
		var deferred = $q.defer();
		RuleGroupFactory.findByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		RuleGroupFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		RuleGroupFactory.findOneByUuidAndVersion(uuid, version, type)
			.then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
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

	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		RuleGroupFactory.findAllLatest(type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		RuleGroupFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		RuleGroupFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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
		RuleGroupFactory.executeRuleGroup(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
});
