/**
 *
 */
DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.factory('ModelDeployFactory', function ($http, $location) {
    var factory = {};
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

    factory.findAllVersionByUuid = function (uuid, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
            method: "GET"
        }).then(function (response) { return response })
    }
    factory.findTrainExecByModel = function (uuid,version,type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "model/train/getTrainExecByModel?action=view&uuid=" + uuid +"&version="+version +"&type=" + type,
            method: "GET"
        }).then(function (response) { return response })
    }
    factory.findTrainExecViewByCriteria = function (uuid, version, type, trainExecUuidList) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "model/getTrainExecViewByCriteria?action=view&uuid=" + uuid +"&version="+version +"&type=" + type+"&trainExecUuidList="+trainExecUuidList,
            method: "GET"
        }).then(function (response) { return response })
    }
    
    return factory;
})

DatascienceModule.service("ModelDeployService", function ($http, ModelDeployFactory, $q, sortFactory) {
    this.getTrainExecViewByCriteria = function (uuid, version, type, trainExecUuidList) {
        var deferred = $q.defer();
        ModelDeployFactory.findTrainExecViewByCriteria(uuid, version, type, trainExecUuidList).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            });
        }
        return deferred.promise;
    }
    this.getTrainExecByModel = function (uuid, version, type) {
        var deferred = $q.defer();
        ModelDeployFactory.findTrainExecByModel(uuid, version, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            });
        }
        return deferred.promise;
    }
    this.getAllVersionByUuid = function (uuid, type) {
        var deferred = $q.defer();
        ModelDeployFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            });
        }
        return deferred.promise;
    }

    this.getAllLatest = function (type) {
        var deferred = $q.defer();
        ModelDeployFactory.findAllLatest(type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            deferred.resolve({
                data: response
            });
        }
        return deferred.promise;
    }

});
