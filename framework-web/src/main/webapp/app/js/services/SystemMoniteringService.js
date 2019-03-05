SystemMonitoringModule= angular.module('SystemMonitoringModule');
SystemMonitoringModule.service("SystemMonitoringService",function($q,$http,$location,CommonFactory,sortFactory,dagMetaDataService){
  this.getActiveSession = function(appuuid, userName, startDate, endDate, tags, active) {
    var deferred = $q.defer();
    var url = "system/getActiveSession?appUuid="+ appuuid + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&status=" + active;
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
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
        result.type = response[i].type;
        result.appInfo = response[i].appInfo;
        if(response[i].status !=null){
        result.status=response[i].status[len].stage
        }
        else{
          result.status="-NA-";
        }
        results[i] = result
      }
      deferred.resolve({
        data: results
      });
    }
    var onError=function(response){
        deferred.reject({
          data:response
        });
      }
    return deferred.promise;
  }
  this.getActiveJobs = function(appuuid, type, userName, startDate, endDate, tags,status) {
    var deferred = $q.defer();
    var url = "system/getActiveJobByCriteria?type="+ type + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status;
    CommonFactory.httpGet(url).then(function(response){OnSuccess(response.data)},function(response){onError(response.data)});
    var OnSuccess = function(response) {
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
        result.type = response[i].type;
        result.appInfo = response[i].appInfo;
        if(response[i].status && response[i].status.length >0){
          if (response[i].status[len].stage == "PENDING") {
            result.status = "PENDING"
          } else if (response[i].status[len].stage == "RUNNING") {
            result.status = "RUNNING"
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
      });
    }
    var onError=function(response){
        deferred.reject({
          data:response
        });
      }
    return deferred.promise;
  }
  this.getActiveThread = function() {
    var deferred = $q.defer();
    var url = "system/getActiveThread"
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
  this.killSession = function(sessionId) {
    var deferred = $q.defer();
    var url = "system/killSession?sessionId="+ sessionId ;
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
  this.jobGraph=function(appuuid, type, userName, startDate, endDate, tags,status){
    var chartcolor=["#c9c9ff","#ffbdbd","#f1cbff","#95af5c","#b6ccb1","#c0cfaf","#a3d0b5","#31402f","#7dcea0","#82e0aa","#f7dc6f","#f8c471","#f0b27a","#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
    var deferred=$q.defer();
    var baseUrl=$location.absUrl().split("app")[0]
    var promises =[];
    var apiList = [{"url":"system/getJobCountByApp","type":"bar","title":"Jobs-App"},{"url":"system/getJobCountByUser","type":"pie","title":"Jobs-User"},{"url":"system/getJobCountByMeta","type":"pie",title:"Jobs-Meta"},{"url":"system/getJobCountByStatus","type":"donut","title":"Jobs-Status"}];
     for(var i=0;i<apiList.length;i++){
      var url =apiList[i].url+"?type="+ type + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status
      var promise =  CommonFactory.httpGet(url)
      promises.push(promise)
     }
     var jobArray=[]
     $q.all(promises).then(function(result){
     for(var i=0;i<result.length;i++){
        var jobresult={};
        var jobgraphcolumnArray=[]
          var jobgraphdataArray=[]
        var count=0;
        angular.forEach(result[i].data,function(value,key) {
          var jobgraphcolumn={};
          var jobgraphData={};
          if(i==2){
            jobgraphcolumn.id=dagMetaDataService.elementDefs[key.toLowerCase()].name;
            jobgraphcolumnArray[count]=dagMetaDataService.elementDefs[key.toLowerCase()].name;
          }

          else{
          jobgraphcolumn.id=key
          jobgraphcolumnArray[count]=key;
          }
        //  jobgraphcolumn.type="pie"
          //jobgraphcolumn.color=chartcolor[count]
          //jobgraphcolumnArray[count]=jobgraphcolumn

          jobgraphData[jobgraphcolumn.id]=value;
          jobgraphdataArray[count]=jobgraphData
          count=count+1;
       });
       jobresult.id="chart"+i;
       jobresult.show=true;
       jobresult.showtooltiptitle="Expand";
       jobresult.iconClass="fa fa-expand";
       jobresult.colExp=true;
       jobresult.index=i;
       jobresult.title=apiList[i].title
       jobresult.type=apiList[i].type
       jobresult.datax=" "
       jobresult.datacolumns=jobgraphcolumnArray
       jobresult.datapoints=jobgraphdataArray
       jobArray[i]=jobresult
     }
     deferred.resolve({
             data:jobArray
         })
    });

  return deferred.promise;
  }
  this.sessionGraph=function(appuuid,type,userName,startDate,endDate,tags,status){
  var deferred=$q.defer();
    var baseUrl=$location.absUrl().split("app")[0]
    var promises =[];
    var apiList =["system/getSessionCountByUser","system/getSessionCountByStatus"];
     for(var i=0;i<apiList.length;i++){
      var url =apiList[i]+"?type="+ type + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status
      var promise =  CommonFactory.httpGet(url)
      promises.push(promise)
     }
     var jobArray=[]

     $q.all(promises).then(function(result){
     for(var i=0;i<result.length;i++){
        var jobresult={};
        var jobgraphcolumnArray=[]
          var jobgraphdataArray=[]
        var count=0;
        angular.forEach(result[i].data,function(value,key) {
          var jobgraphcolumn={};
          var jobgraphData={};
          jobgraphcolumn.id=key
          jobgraphcolumnArray[count]=key;

        //  jobgraphcolumn.type="pie"
          //jobgraphcolumn.color=chartcolor[count]
          //jobgraphcolumnArray[count]=jobgraphcolumn

          jobgraphData[jobgraphcolumn.id]=value;
          jobgraphdataArray[count]=jobgraphData
          count=count+1;
       });
       jobresult.id="chart"+i;
       jobresult.title=i==0?"Session-User":"Session-Status"
       jobresult.type=i==0?"pie":"donut"
       jobresult.datax=" "
       jobresult.show=true;
       jobresult.showtooltiptitle="Expand";
       jobresult.iconClass="fa fa-expand";
       jobresult.colExp=true;
       jobresult.datacolumns=jobgraphcolumnArray
       jobresult.datapoints=jobgraphdataArray

       jobArray[i]=jobresult
     }
     deferred.resolve({
             data:jobArray
         })
    });

  return deferred.promise;
  }
});
