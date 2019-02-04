
AdminModule = angular.module('AdminModule');
AdminModule.factory("MigrationAssistFactory", function ($http, $location) {
	var url = $location.absUrl().split("app")[0]
	var factory = {};
	factory.findAll = function () {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAll?type=meta",
		}).
			then(function (response, status, headers) {
				return response;
			})
	}

	return factory;
});
AdminModule.service("MigrationAssistServices", function ($q, MigrationAssistFactory, CommonFactory) {
	this.getAll = function () {
		var deferred = $q.defer();
		MigrationAssistFactory.findAll().then(function (response) { OnSuccess(response.data) }, function (response) { onError(response.data) });
		var OnSuccess = function (response) {
			var metaList = []
			for (var i = 0; i < response.length; i++) {
				var metaObj = {};
				metaObj.uuid = response[i].uuid;
				metaObj.text = response[i].name;
				metaList[i] = metaObj;
			}
			deferred.resolve({
				data: metaList
			});
		}
		var onError = function (response) {
			deferred.reject({
				data: response
			})
		}
		return deferred.promise;
	}/*End getRegistryByDatasource*/
	
	this.validateDependancy = function (data, filename) {
		var deferred = $q.defer();
		var url = "admin/import/validate?type=import&action=edit&fileName=" + filename
		CommonFactory.httpPost(url, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.getAllByMetaList = function (type) {
		var deferred = $q.defer();
		var url = "admin/getAllByMetaList?action=view&type=" + type
		CommonFactory.httpGet(url).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
		var onSuccess = function (response) {
			var allMataList = []
			var count = 0;
			for (var i = 0; i < type.length; i++) {
				var result = response[type[i]]
				for (var j = 0; j < result.length; j++) {
					var metaList = {};
					metaList.type = type[i];
					metaList.name = metaList.type + "-" + result[j].name;
					metaList.uuid = result[j].uuid
					metaList.id = result[j].uuid
					allMataList[count] = metaList;
					count = count + 1
				}
			}
			console.log(allMataList);
			deferred.resolve({
				data: allMataList
			});
		}
		var onError = function (response) {
			deferred.reject({
				data: response
			})
		}
		return deferred.promise;
	}
	this.importSubmit = function (data, type, filename, upd_tag) {
		var deferred = $q.defer();
		var url = "admin/import/submit?action=add" + "&type=" + type + "&fileName=" + filename + "&upd_tag=" + upd_tag;
		CommonFactory.httpPost(url, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
	this.exportSubmit = function (data, type, upd_tag) {
		var deferred = $q.defer();
		var url = "admin/export/submit?action=add" + "&type=" + type + "&upd_tag=" + upd_tag;
		CommonFactory.httpPost(url, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.submit = function (data, type) {
		var deferred = $q.defer();
		var url = "common/submit?action=add" + "&type=" + type;
		CommonFactory.httpPost(url, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

});
