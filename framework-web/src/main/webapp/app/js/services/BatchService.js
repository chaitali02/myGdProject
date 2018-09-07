/**
 *
 */
BatchModule = angular.module('BatchModule');
BatchModule.factory('BatchFactory', function ($http, $location) {
	
	var factory = {};
	factory.findAllLatest = function (metavalue) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllLatest?action=view&type=" + metavalue,
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.findLatestByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
			method: "GET",
		}).then(function (response) { return response })

	}

	factory.findOneById = function (id, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getOneById?action=view&id=" + id + "&type=" + type,
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.findAllVersionByUuid = function (uuid, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getAllVersionByUuid?action=view&uuid=" + uuid + "&type=" + type,
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.findOneByUuidAndVersion = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,

		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.executeBatch= function (uuid, version) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'POST',
			url: url + "batch/execute?action=execute&uuid=" + uuid + "&version=" + version,
		}).
		then(function (response, status, headers) {
			return response;
		})
	}

	factory.submit = function (data,type,upd_tag) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			url: url + "common/submit?action=edit&type=" + type+"&upd_tag="+upd_tag,
			headers: {
				'Accept': '*/*',
				'content-Type': "application/json",
			},
			method: "POST",
			data: JSON.stringify(data),
		}).success(function (response) { return response })
	}
	factory.findExecListByBatchExec = function (uuid, version, type) {
		var url = $location.absUrl().split("app")[0]
		return $http({
			method: 'GET',
			url: url + "metadata/getExecListByBatchExec?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,

		}).
		then(function (response, status, headers) {
			return response;
		})
	}
    
	return factory;
})


BatchModule.service("BatchService", function ($q, BatchFactory, sortFactory,$filter) {
    this.getExecListByBatchExec = function (uuid, version, type) {
		var deferred = $q.defer();
		BatchFactory.findExecListByBatchExec(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function(response) {
			var results = []
			for (var i = 0; i < response.length; i++) {
			  var result = {};
			  if (response[i].status != null) {
				response[i].status.sort(sortFactory.sortByProperty("createdOn"));
				var len = response[i].status.length - 1;
			  }
			  result.index=i;
			  result.id = response[i].id;
			  result.uuid = response[i].uuid;
			  result.version = response[i].version;
			  result.name = response[i].name;
			  result.createdBy = response[i].createdBy;
			  result.createdOn = response[i].createdOn;
			  result.active = response[i].active;
			  result.type = response[i].type;
			  result.startTime="-NA-";
			  result.endTime="-NA-";
			  result.duration="-NA-"
			  response[i].statusList=response[i].status;
			  if(response[i].statusList !=null && response[i].statusList.length > 1){
				for(var j=0;j<response[i].statusList.length;j++){
					if(response[i].statusList[j].stage == "NotStarted"){
						result.startTime=$filter('date')(new Date(response[i].statusList[j].createdOn), "EEE MMM dd HH:mm:ss yyyy");
						break;
					}
				}
				for(var j=0;j<response[i].statusList.length;j++){
					if(response[i].statusList[j].stage == "InProgress"){
						result.InProgressTime=$filter('date')(new Date(response[i].statusList[j].createdOn), "EEE MMM dd HH:mm:ss yyyy");
						break;
					}
				}
				if(response[i].status[len].stage == "Completed"){
					result.endTime=$filter('date')(new Date(response[i].statusList[len].createdOn), "EEE MMM dd HH:mm:ss yyyy");
					var date1 = new Date(result.InProgressTime)
         			var date2 = new Date(result.endTime)
					result.duration= moment.utc(moment(date2).diff(moment(date1))).format("HH:mm:ss")
         			
				}

			  }
			  if(response[i].status !=null && response[i].status.length > 0){
				if (response[i].status[len].stage == "NotStarted") {
				  result.status = "Not Started"
				} else if (response[i].status[len].stage == "InProgress") {
				  result.status = "In Progress"
				} else {
				  result.status = response[i].status[len].stage;
				}
			  }
			  else{
				result.status="-NA-";
			  }
			  results[i] = result
			}
			deferred.resolve({
			  data: results
			})
		  }
		return deferred.promise;
	}
	this.getLatestByUuid = function (id, type) {
		var deferred = $q.defer();
		BatchFactory.findLatestByUuid(id, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.getAllVersionByUuid = function (uuid, type) {
		var deferred = $q.defer();
		BatchFactory.findAllVersionByUuid(uuid, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {

			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneByUuidAndVersion = function (uuid, version, type) {
		var deferred = $q.defer();
		BatchFactory.findOneByUuidAndVersion(uuid, version, type).then(function (response) { onSuccess(response.data) });
		var onSuccess = function (response) {
			var batchResult={};
			var weekNumToDays={"0":"SUN","1":"MON","2":"TUE","3":"WED","4":"THU","5":"FRI","6":"SAT"};
			batchResult.batch=response;
			var scheduleInfoArray=[];
            if(response.scheduleInfo !=null){
				for(var i=0;i<response.scheduleInfo.length;i++){
					var scheduleInfo={};
					scheduleInfo.name=response.scheduleInfo[i].name;
					scheduleInfo.uuid=response.scheduleInfo[i].uuid;
					scheduleInfo.startDate= moment(response.scheduleInfo[i].startDate);
					scheduleInfo.endDate=moment(response.scheduleInfo[i].endDate);
					scheduleInfo.frequencyType=response.scheduleInfo[i].frequencyType;
					scheduleInfo.frequencyDetail=[];
					response.scheduleInfo[i].isStartDateChange="N";
					response.scheduleInfo[i].isEndDateChange="N";
					if(response.scheduleInfo[i].frequencyDetail){
						for(var j=0;j<response.scheduleInfo[i].frequencyDetail.length;j++){
							if(response.scheduleInfo[i].frequencyType !='Monthly'){
								scheduleInfo.frequencyDetail[j]=weekNumToDays[response.scheduleInfo[i].frequencyDetail[j]];
							}else{
								scheduleInfo.frequencyDetail[j]=response.scheduleInfo[i].frequencyDetail[j];
							}
						}
				    }
				  	
					// scheduleInfo.recurring=response.scheduleInfo[i].recurring=='Y' ?true:false;
					// if(response.scheduleInfo[i].frequencyDetail.length >0){
					// 	for(var j=0;j<response.scheduleInfo[i].frequencyDetail.length;j++){
					// 		var dd=$filter('date')(new Date(response.scheduleInfo[i].frequencyDetail[j]), "dd");
					// 		scheduleInfo.frequencyDetail[j]=moment().date(dd);
					// 		var date=moment(scheduleInfo.frequencyDetail[j])._d
					// 		scheduleInfo.frequencyDetail[j]=($filter('date')(date, "MM-dd-yyyy"));
					// 	}
					// }
					
					scheduleInfoArray[i]=scheduleInfo;
				}
			}
			batchResult.scheduleInfoArray=scheduleInfoArray;
			deferred.resolve({
				data: batchResult
			})
		}
		return deferred.promise;
	}

	this.getAllLatest = function (type) {
		var deferred = $q.defer();
		BatchFactory.findAllLatest(type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}

	this.getOneById = function (id, type) {
		var deferred = $q.defer();
		BatchFactory.findOneById(id, type).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}


	this.submit = function (data,type,upd_tag) {
		var deferred = $q.defer();
		BatchFactory.submit(data,type,upd_tag).then(function (response) { onSuccess(response) }, function (response) { onError(response.data) });
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

	this.execute = function (uuid, version) {
		var deferred = $q.defer();
		BatchFactory.executeBatch(uuid, version).then(function (response) { onSuccess(response) });
		var onSuccess = function (response) {
			deferred.resolve({
				data: response
			})
		}
		return deferred.promise;
	}
});
