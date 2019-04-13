/**
 *
 */

DataQualityModule = angular.module('DataQualityModule');
DataQualityModule.factory('RecommenderFactory', function ($http, $location) {
	var factory = {};
	factory.genIntelligence = function (type, uuid, version, samplePercent) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "dataqual/genIntelligence?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type+"&samplePercent="+samplePercent,
			method: "GET",
		}).then(function (response) { return response })
	}
	factory.generateDq = function (type, uuid, version,data) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "dataqual/generateDq?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,
			method: "POST",
			data:JSON.stringify(data),
			headers: {
				'Accept': '*/*',
				'content-Type': "application/json",
			}
		}).then(function (response) { return response })
	}
	return factory;
});


DataQualityModule.service("RecommenderService", function ($q, RecommenderFactory, sortFactory) {
	this.generateDq = function (type, uuid, version, data) {
		var deferred = $q.defer();
		RecommenderFactory.generateDq(type, uuid, version, data).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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

	this.genIntelligence = function (type, uuid, version, samplePercent) {
		var deferred = $q.defer();
		RecommenderFactory.genIntelligence(type, uuid, version, samplePercent).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
