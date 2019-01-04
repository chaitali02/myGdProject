/**
 *
 */

AdminModule = angular.module('AdminModule');

AdminModule.factory('AdminPrivilegeFactory', function ($http, $location) {
	var factory = {};
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })


	}
	factory.findAllVersionByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })


	}
	factory.findAll = function (type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getAll?action=view&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}

	factory.privilegeSubmit = function (data, type, upd_tag) {
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

	factory.findByOneUuidandVersion = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
			method: "GET"
		}).then(function (response) { return response })
	}
	factory.getGraphData = function (uuid, version, degree) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "graph/getGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree,
			method: "GET"

		}).then(function (response) { return response })
	};
	return factory;

});

AdminModule.service('AdminPrivilegeService', function ($q, AdminPrivilegeFactory, sortFactory) {

	this.getLatestByUuid = function (uuid, type) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.findLatestByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.getGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}
	this.getByOneUuidandVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.findByOneUuidandVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
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

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			});
		}
		return deferred.promise;
	}

	this.submit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.privilegeSubmit(data, type, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.getAll = function (type) {
		var deferred = $q.defer();
		AdminPrivilegeFactory.findAll(type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var data = {};
			data.options = [];
			var defaultoption = {};
			response.sort(sortFactory.sortByProperty("name"));
			defaultoption.name = response[0].name;
			defaultoption.uuid = response[0].uuid;
			data.defaultoption = defaultoption;
			for (var i = 0; i < response.length; i++) {
				var datajosn = {}
				datajosn.name = response[i].name;
				datajosn.uuid = response[i].uuid;
				data.options[i] = datajosn
			}
			deferred.resolve({
				data: data
			})
		}
		return deferred.promise;
	}

});
