JobMonitoringModule= angular.module('JobMonitoringModule');
JobMonitoringModule.factory('JobMonitoringFactory',function($http,$location){
	var factory={};
	factory.findAllExecStats=function(uuid,type){
	    var url=$location.absUrl().split("app")[0]
	  	return $http({
	  		        url:url+"metadata/getExecStats?action=view",
	  		      	method: "GET",
	    }).then(function(response){ return  response})

	}
	factory.findLatestByUuid = function(uuid, type) {
    var url = $location.absUrl().split("app")[0]
    return $http({
      url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
      method: "GET",
    }).then(function(response) {
      return response
    })
  }
	return factory;
});


JobMonitoringModule.service('JobMonitoringService',function($q,JobMonitoringFactory,sortFactory){
	this.getAllExecStats=function(){
		 var deferred = $q.defer();
		 JobMonitoringFactory.findAllExecStats().then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	deferred.resolve({
	            data:response
	        });
	    }
	    return deferred.promise;
	}
	this.getLatestByUuid = function(id, type) {
    var deferred = $q.defer();
    JobMonitoringFactory.findLatestByUuid(id, type).then(function(response) {
      onSuccess(response.data)
    });
    var onSuccess = function(response) {
      deferred.resolve({
        data: response
      })
    }
    return deferred.promise;
  };
});
