/**
 *
 */
AdminModule = angular.module('AdminModule');
AdminModule.factory('OrganizationFactory', function ($http, $location) {
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
            url: url + "common/submit?action=edit&type=organization&upd_tag=" + upd_tag,
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
    factory.uploadLogo = function (url, data) {
        var fullUrl = $location.absUrl().split("app")[0] + url
        return $http({
          url: fullUrl,
          headers: {
            'Accept': '*/*',
            'content-Type': undefined,
          },
          method: "POST",
          transformRequest: angular.identity,
          data: data,
        }).success(function (response) { return response })
      }
   
    return factory;
});

AdminModule.service('OrganizationSerivce', function ($q, sortFactory, OrganizationFactory) {
    
    this.uploadLogo = function (uuid, data, fileName,type) {
        var url = "metadata/uploadOrgLogo?action=edit&type=" + type + "&fileName=" + fileName+"&uuid="+uuid;
        var deferred = $q.defer();
        OrganizationFactory.uploadLogo(url, data).then(function (response) { onSuccess(response.data) },function (response) { onError(response.data) });
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
        OrganizationFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    };

    this.getLatestByUuid = function (id, type) {
        var deferred = $q.defer();
        OrganizationFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            })
        }
        return deferred.promise;
    };

    this.getOneByUuidAndVersion = function (uuid, version, type) {
        var deferred = $q.defer();
        OrganizationFactory.findOneByUuidAndVersion(uuid, version, type)
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
        OrganizationFactory.submit(data, upd_tag).then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) });
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
