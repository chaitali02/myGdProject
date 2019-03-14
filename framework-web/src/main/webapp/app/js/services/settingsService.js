AdminModule= angular.module('AdminModule');
AdminModule.service("SettingsService",function($q,$http,$location,CommonFactory){
  this.getSettings = function(id, type) {
    var deferred = $q.defer();
    var url = "admin/settings/get?type=setting&action=view";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
        deferred.reject({
          data:response
        });
      }
    return deferred.promise;
  }
  this.setSetting = function(data) {
    var deferred = $q.defer();
    var url = "admin/settings/submit?type=setting&action=edit";
    CommonFactory.httpPost(url,data).then(function(response) {
      OnSuccess(response.data)
    });
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    return deferred.promise;
  }
  this.buildGraph = function(id, type) {
    var deferred = $q.defer();
    var url = "graph/buildGraph";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
        deferred.reject({
          data:response
        });
      }
    return deferred.promise;
  }
   
  this.getProcessStatus=function(){
    var deferred = $q.defer();
    var url = "datascience/getProcessStatus";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
      deferred.reject({
        data:response
      });
    }
    return deferred.promise;
  }
  this.startProcess=function(){
    var deferred = $q.defer();
    var url = "datascience/startProcess";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
      deferred.reject({
        data:response
      });
    }
    return deferred.promise;
  }
  this.stopProcess=function(){
    var deferred = $q.defer();
    var url = "datascience/stopProcess";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
      deferred.reject({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getAppConfigByApp=function(){
    var deferred = $q.defer();
    var url = "metadata/getAppConfigByApp?type=appconfig&action=view";
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
      deferred.resolve({
        data: response
      });
    }
    var onError=function(response){
      deferred.reject({
        data:response
      });
    }
    return deferred.promise;
  }
  

})
