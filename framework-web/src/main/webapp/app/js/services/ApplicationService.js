/**
 *
 */
AdminModule = angular.module('AdminModule');
AdminModule.factory('MetadataApplicationFactory', function ($http, $location) {
	var factory = {}
	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })
	}
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

	factory.applicationSubmit = function (data,upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type=application&upd_tag="+upd_tag,

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

	factory.findDatasourceByType=function(type){
		var url=$location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url:url+"metadata/getDatasourceByType?action=view&type="+type,
		}).then(function (response,status,headers) { return response;})
	}
	return factory;
});

AdminModule.service('MetadataApplicationSerivce', function ($q, sortFactory, MetadataApplicationFactory) {

	this.getLatestDataSourceByUuid=function(id,type){
		var deferred=$q.defer();
		MetadataApplicationFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		var onSuccess=function(response){
		   deferred.resolve({
				data:response
			});
		}
	   return deferred.promise;
	 }
	this.getDatasourceByType=function(type){
		var deferred = $q.defer();
		MetadataApplicationFactory.findDatasourceByType(type).then(function(response){OnSuccess(response.data)});
		var OnSuccess=function(response){
				deferred.resolve({
				data:response
			});
		}
		return deferred.promise;
	}/*End getDatasourceByType*/

	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		MetadataApplicationFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getGraphData = function (uuid, version, degree) {
		var deferred = $q.defer();
		MetadataApplicationFactory.findGraphData(uuid, version, degree).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	};

	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		MetadataApplicationFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;



	};

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		MetadataApplicationFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		MetadataApplicationFactory.applicationSubmit(data,upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
