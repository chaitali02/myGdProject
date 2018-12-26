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
    factory.findTrainExecViewByCriteria = function (uuid, version, type, trainexecuuid,startDate, endDate, active, status) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "model/getTrainExecViewByCriteria?action=view&uuid=" + uuid +"&version="+version +"&type=" + type+"&trainExecUuid="+trainexecuuid+"&startDate="+startDate+"&endDate="+endDate+"&active="+active+"&status="+status,
            method: "GET"
        }).then(function (response) { return response })
    }
    factory.deploy = function (uuid, version, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "datascience/deploy?action=view&uuid=" + uuid +"&version="+version +"&type=" + type,
            method: "GET"
        }).then(function (response) { return response })
    }
    factory.undeploy = function (uuid, version, type) {
        var url = $location.absUrl().split("app")[0]
        return $http({
            url: url + "datascience/undeploy?action=view&uuid=" + uuid +"&version="+version +"&type=" + type,
            method: "GET"
        }).then(function (response) { return response })
    }
    
    
    return factory;
})

DatascienceModule.service("ModelDeployService", function ($http, ModelDeployFactory, $q, sortFactory,$filter) {
    
    this.deploy = function (uuid, version, type) {
        var deferred = $q.defer();
        ModelDeployFactory.deploy(uuid, version, type).then(function (response) { onSuccess(response.data)},function (response) { onError(response.data) });
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
    
    this.undeploy = function (uuid, version, type) {
        var deferred = $q.defer();
        ModelDeployFactory.undeploy(uuid, version, type).then(function (response) { onSuccess(response.data)},function (response) { onError(response.data) });
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
    
    this.getTrainExecViewByCriteria = function (uuid, version, type, trainexecuuid,startdate, enddate,active, status) {
        var deferred = $q.defer();
        ModelDeployFactory.findTrainExecViewByCriteria(uuid, version, type, trainexecuuid,startdate, enddate,active, status).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
            var result=[];
            if(response && response.length >0){
                for(var i=0;i<response.length ;i++){
                 var resultInfo={};
                 resultInfo.isModelDeployExist="N";   
                if (response[i].statusList != null) {
                    response[i].statusList.sort(sortFactory.sortByProperty("createdOn"));
                    var len = response[i].statusList.length - 1;
                }
                
                if(response[i].statusList !=null && response[i].statusList.length > 0){
                    if (response[i].statusList[len].stage == "NotStarted") {
                        resultInfo.tStatus = "Not Started"
                    } else if (response[i].statusList[len].stage == "InProgress") {
                      result.tStatus = "In Progress"
                    } else {
                        resultInfo.tStatus = response[i].statusList[len].stage;
                    }
                  }
                  else{
                    resultInfo.tStatus="-NA-";
                }
                if (response[i].deployExec !=null && response[i].deployExec.statusList != null) {
                    response[i].deployExec.statusList.sort(sortFactory.sortByProperty("createdOn"));
                    var len = response[i].deployExec.statusList.length - 1;
                }
                
                if(response[i].deployExec !=null && response[i].deployExec.statusList !=null && response[i].deployExec.statusList.length > 0){
                    
                    if (response[i].deployExec.statusList[len].stage == "NotStarted") {
                        resultInfo.dStatus = "Not Started"
                    } else if (response[i].deployExec.statusList[len].stage == "InProgress") {
                      result.dStatus = "In Progress"
                    } else {
                        resultInfo.dStatus =response[i].deployExec.statusList[len].stage;
                        if(response[i].deployExec.statusList[len].stage == "Completed")
                            resultInfo.lastDeployedDate=$filter('date')(response[i].deployExec.statusList[len].createdOn, "EEE MMM dd  hh:mm:ss yyyy");
                        else 
                        resultInfo.lastDeployedDate="-NA-";
                    }


                    if(response[i].deployExec.active == "Y" && resultInfo.isModelDeployExist !="Y" &&  response[i].deployExec.statusList[len].stage == "Completed"){
                        resultInfo.isModelDeployExist="Y";   
                    }
                    else
                        resultInfo.isModelDeployExist="N";  
                  }
                  else{
                    resultInfo.dStatus="-NA-";
                    resultInfo.lastDeployedDate="-NA-";
                }
              
                resultInfo.response=response[i];
                resultInfo.name=response[i].name;
                resultInfo.uuid=response[i].uuid;
                resultInfo.version=response[i].version;
                resultInfo.createdBy=response[i].createdBy;
                resultInfo.numFeatures=response[i].trainResultView.numFeatures;
                resultInfo.f1Score= parseFloat(response[i].trainResultView.f1Score.toFixed(2));
                resultInfo.accuracy= parseFloat(response[i].trainResultView.accuracy.toFixed(2));
                resultInfo.recall= parseFloat(response[i].trainResultView.recall.toFixed(2));
                resultInfo.precision= parseFloat(response[i].trainResultView.precision.toFixed(2));
                resultInfo.numFeatures=response[i].trainResultView.numFeatures;
                resultInfo.deployExec=response[i].deployExec;
                result[i]=resultInfo;
                 
                }
            }
            deferred.resolve({
                data: result
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
