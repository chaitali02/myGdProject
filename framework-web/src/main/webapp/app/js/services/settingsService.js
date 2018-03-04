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

})
