	/**
	 *
	 */
	DatadiscoveryModule = angular.module('DatadiscoveryModule');
	DatadiscoveryModule.factory('DataDiscoveryFactory', function($http, $location) {
	  var factory = {};
	  factory.findDatapodStats = function(uuid, type) {
	    var url = $location.absUrl().split("app")[0]
	    return $http({
	      url: url + "datapod/getDatapodStats?action=view",
	      method: "GET",
	    }).then(function(response) {
	      return response
	    })



	  }

	  return factory;

	});

	DatadiscoveryModule.service('DataDiscoveryService', function($q, DataDiscoveryFactory, sortFactory) {

	  this.getDatapodStats = function() {
	    var deferred = $q.defer();
	    DataDiscoveryFactory.findDatapodStats().then(function(response) {
	      onSuccess(response.data)
	    });
	    var onSuccess = function(response) {

	      var colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"];
	      var datapodarray = [];
	      for (var i = 0; i < response.length; i++) {
	        var datapodJSon = {};
	        if (response[i].ref.name.split('').length > 16) {
	          datapodJSon.name = response[i].ref.name.substring(0, 16) + "..";
	        } else {
	          datapodJSon.name = response[i].ref.name;
	        }
	        datapodJSon.title = response[i].ref.name;
	        datapodJSon.uuid = response[i].ref.uuid;
	        datapodJSon.type = response[i].ref.type;
	        datapodJSon.version = response[i].ref.version;
	        datapodJSon.dataSource = response[i].dataSource;
	        if(response[i].numRows != null) {
	        	datapodJSon.numRows = response[i].numRows;	        	
	        }
	        else {

	        	datapodJSon.numRows =0;
	        }
	        
	        if(response[i].lastUpdatedOn != null) {
	        	datapodJSon.lastUpdatedOn = new Date(response[i].lastUpdatedOn.split("IST")[0]);	        	
	        }
	        else {
	        	datapodJSon.lastUpdatedOn = "NA";
	        }
	        var randomno = Math.floor((Math.random() * 4) + 0);
	        datapodJSon.class = colorclassarray[randomno];
	        datapodarray[i] = datapodJSon
	      }
				datapodarray.sort(sortFactory.sortByProperty("name"));
				console.log(datapodarray)
	      deferred.resolve({
	        data: datapodarray
	      });
	    }
	    return deferred.promise;
	  }


	});
