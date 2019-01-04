  /**
   *
   */
  RuleModule = angular.module('RuleModule');

  RuleModule.factory('RuleDatatableFactory', function($http, $location) {
    var factory = {};
    factory.findAll = function(metavalue) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "common/getAll?action=view&type=" + metavalue,

      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findAllLatest = function(metavalue) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "common/getAllLatest?action=view&type=" + metavalue,

      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findByUuid = function(uuid, type) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        url: url + "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type,
        method: "GET",
      }).then(function(response) {
        return response
      })


    }
    factory.findOneByUuidAndVersion = function(uuid, version, type) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "metadata/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type,

      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.executeRule = function(uuid, version) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "rule/executeRule?action=execute&ruleUUID=" + uuid + "&ruleVersion=" + version,
      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.executeRuleGroup = function(uuid, version) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'POST',
        url: url + "rule/executeRuleGroup?action=execute&ruleGroupUUID=" + uuid + "&ruleGroupVersion=" + version,

      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    //factory.findRuleResults=function(uuid,version,offset,limit,requestId,sortBy,order) {
    factory.findRuleResults = function(url) {
      var baseurl = $location.absUrl().split("app")[0] + url;
      return $http({
        method: 'GET',
        // url:url+"rule/getRuleResults?action=view&ruleExecUUID="+uuid+"&ruleExecVersion="+version+"&offset="+offset+"&limit="+limit+"&requestId="+requestId+"&sortBy="+sortBy+"&order="+order,
        url: baseurl,
      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findRuleExecByRule = function(uuid) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "rule/getRuleExecByRule?action=view&ruleUuid=" + uuid,
      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findRuleExecByRuleGroupExec = function(uuid, version) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "rule/getRuleExecByRGExec?action=view&ruleGroupExecUUID=" + uuid + "&ruleGroupExecVersion=" + version,
      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findRuleGroupExecByRuleGroup = function(uuid, version) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url: url + "rule/getRuleGroupExecByRuleGroup?action=view&ruleGroupUUID=" + uuid + "&ruleGroupVersion=" + version,
      }).
      then(function(response, status, headers) {
        return response;
      })
    }
    factory.findBaseEntityStatusByCriteria = function(type, name, userName, startDate, endDate, tags, status) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        url: url + "metadata/getBaseEntityStatusByCriteria?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&status=" + status,
        method: "GET",
      }).then(function(response) {
        return response
      })

    }
    factory.findNumRowsbyExec = function(uuid,version,type) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        url: url + "metadata/getNumRowsbyExec?action=view&execUuid="+uuid+"&execVersion="+version+"&type="+type,
        method: "GET",
      }).then(function(response) {
        return response
      })

    }
    return factory;
  })


  RuleModule.factory("ListRuleService", function($q, RuleDatatableFactory, sortFactory) {
    var factory = {};
    factory.getNumRowsbyExec = function(uuid,version,type) {
      var deferred = $q.defer();
      RuleDatatableFactory.findNumRowsbyExec(uuid,version,type).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        })
      }

      return deferred.promise;
    }
    factory.getAllLatest = function(type) {
      var deferred = $q.defer();

      RuleDatatableFactory.findAllLatest(type).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        if (response.length > 0) {
          response.sort(sortFactory.sortByProperty("name"));
        }
        deferred.resolve({
          data: response
        })
      }

      return deferred.promise;
    }
    factory.getBaseEntityStatusByCriteria = function(type, name, userName, startDate, endDate, tags, status) {
      var deferred = $q.defer();
      RuleDatatableFactory.findBaseEntityStatusByCriteria(type, name, userName, startDate, endDate, tags, status).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        var rowDataSet = [];
        var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status', 'action']
        for (var i = 0; i < response.length; i++) {
          var rowData = [];
          if (response[i].status != null) {
            response[i].status.sort(sortFactory.sortByProperty("createdOn"));
            var len = response[i].status.length - 1;
          }
          for (var j = 0; j < headerColumns.length; j++) {
            var columnname = headerColumns[j]
            if (columnname == "createdBy") {
              rowData[j] = response[i].createdBy.ref.name;
            } else if (columnname == "status") {
              if (response[i].status != null) {
                if (response[i].status[len].stage == "InProgress") {
                  rowData[j] = "In Progress";
                } else if (response[i].status[len].stage == "NotStarted") {
                  rowData[j] = "Not Started";
                } else {
                  rowData[j] = response[i].status[len].stage;
                }

              } else {
                rowData[j] = " ";
              }
            } else {
              rowData[j] = response[i][columnname];
            }

          }
          rowDataSet[i] = rowData;
        }

        deferred.resolve({
          data: rowDataSet
        })
      }

      return deferred.promise;
    }
    factory.ruleGroupExecute = function(uuid, version) {
      var deferred = $q.defer();
      RuleDatatableFactory.executeRuleGroup(uuid, version).then(function(response) {
        onSuccess(response)
      });
      var onSuccess = function(response) {

        deferred.resolve({
          data: response
        })
      }

      return deferred.promise;
    }
    factory.getRuleResults = function(uuid, version, offset, limit, requestId, sortBy, order) {
      var deferred = $q.defer();
      var url;
      if (sortBy == null && order == null) {
        url = "rule/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId;
      } else {
        url = "rule/getResults?action=view&uuid=" + uuid + "&version=" + version + "&offset=" + offset + "&limit=" + limit + "&requestId=" + requestId + "&sortBy=" + sortBy + "&order=" + order;
      }
      // RuleDatatableFactory.findRuleResults(uuid,version,offset,limit,requestId,sortBy,order).then(function(response){onSuccess(response)});
      RuleDatatableFactory.findRuleResults(url).then(function(response) {
        onSuccess(response)
      }, function(response) {
        onError(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        })
      }
      var onError = function(response) {
        deferred.reject({
          data: response
        })
      }
      return deferred.promise;
    }
    factory.getAll = function(metavalue, sessionid) {
      var deferred = $q.defer();
      RuleDatatableFactory.findAll(metavalue).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {

        var rowDataSet = [];
        var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn']
        for (var i = 0; i < response.length; i++) {
          var rowData = [];

          for (var j = 0; j < headerColumns.length; j++) {
            var columnname = headerColumns[j]
            if (columnname == "createdBy") {

              rowData[j] = response[i].createdBy.ref.name;
            } else {

              rowData[j] = response[i][columnname];
            }
          }
          rowDataSet[i] = rowData;

        }

        deferred.resolve({
          data: rowDataSet
        })
      }
      return deferred.promise;
    }
    factory.getRuleExecByRule = function(uuid) {

      var deferred = $q.defer();
      RuleDatatableFactory.findRuleExecByRule(uuid).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {

        var rowDataSet = [];
        var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status', 'action']
        for (var i = 0; i < response.length; i++) {
          var rowData = [];
          if (response[i].status != null) {
            response[i].status.sort(sortFactory.sortByProperty("createdOn"));
            var len = response[i].status.length - 1;
          }
          for (var j = 0; j < headerColumns.length; j++) {
            var columnname = headerColumns[j]
            if (columnname == "createdBy") {

              rowData[j] = response[i].createdBy.ref.name;
            } else if (columnname == "status") {
              if (response[i].status != null) {
                if (response[i].status[len].stage == "InProgress") {

                  rowData[j] = "In Progress";
                } else if (response[i].status[len].stage == "NotStarted") {
                  rowData[j] = "Not Started";
                } else {
                  rowData[j] = response[i].status[len].stage;
                }

              } else {

                rowData[j] = " ";
              }
            } else if (columnname == "action") {
              rowData[j] = false;
            } else {

              rowData[j] = response[i][columnname];
            }

          }
          rowDataSet[i] = rowData;

        }

        deferred.resolve({
          data: rowDataSet
        })
      }

      return deferred.promise;
    }
    factory.getRuleGroupExecByRuleGroup = function(uuid, version) {

      var deferred = $q.defer();
      RuleDatatableFactory.findRuleGroupExecByRuleGroup(uuid, version).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {

        var rowDataSet = [];
        var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status', 'action']
        for (var i = 0; i < response.length; i++) {
          var rowData = [];
          if (response[i].status != null) {
            response[i].status.sort(sortFactory.sortByProperty("createdOn"));
            var len = response[i].status.length - 1;
          }
          for (var j = 0; j < headerColumns.length; j++) {
            var columnname = headerColumns[j]
            if (columnname == "createdBy") {

              rowData[j] = response[i].createdBy.ref.name;
            } else if (columnname == "status") {
              if (response[i].status != null) {
                if (response[i].status[len].stage == "InProgress") {

                  rowData[j] = "In Progress";
                } else if (response[i].status[len].stage == "NotStarted") {
                  rowData[j] = "Not Started";
                } else {
                  rowData[j] = response[i].status[len].stage;
                }


              } else {

                rowData[j] = " ";
              }
            } else if (columnname == "action") {
              rowData[j] = false;
            } else {

              rowData[j] = response[i][columnname];
            }

          }
          rowDataSet[i] = rowData;

        }

        deferred.resolve({
          data: rowDataSet
        })
      }

      return deferred.promise;
    }
    factory.getRuleExecByRuleGroupExec = function(uuid, version) {

      var deferred = $q.defer();
      RuleDatatableFactory.findRuleExecByRuleGroupExec(uuid, version).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {

        var rowDataSet = [];
        var headerColumns = ['id', 'uuid', 'version', 'name', 'createdBy', 'createdOn', 'status', 'action']
        for (var i = 0; i < response.length; i++) {
          var rowData = [];
          if (response[i].status != null) {
            response[i].status.sort(sortFactory.sortByProperty("createdOn"));
            var len = response[i].status.length - 1;
          }
          for (var j = 0; j < headerColumns.length; j++) {
            var columnname = headerColumns[j]
            if (columnname == "createdBy") {

              rowData[j] = response[i].createdBy.ref.name;
            } else if (columnname == "status") {
              if (response[i].status != null) {
                if (response[i].status[len].stage == "InProgress") {

                  rowData[j] = "In Progress";
                } else if (response[i].status[len].stage == "NotStarted") {
                  rowData[j] = "Not Started";
                } else {
                  rowData[j] = response[i].status[len].stage;
                }
              } else {

                rowData[j] = " ";
              }
            } else if (columnname == "action") {
              rowData[j] = false;
            } else {

              rowData[j] = response[i][columnname];
            }

          }
          rowDataSet[i] = rowData;

        }

        deferred.resolve({
          data: rowDataSet
        })
      }

      return deferred.promise;
    }
    factory.getOneByUuidAndVersion = function(id, version, type) {
      var deferred = $q.defer();
      RuleDatatableFactory.findOneByUuidAndVersion(id, version, type)
        .then(function(response) {onSuccess(response.data)},function (response) { onError(response.data) });
      var onSuccess = function(response) {
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
    factory.getByUuid = function(id, type) {
      var deferred = $q.defer();
      RuleDatatableFactory.findByUuid(id, type).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {

        deferred.resolve({
          data: response
        })
      }

      return deferred.promise;
    }
    factory.executeRule = function(uuid, version) {
      var deferred = $q.defer();
      RuleDatatableFactory.executeRule(uuid, version).then(function(response) {
        onSuccess(response)
      });
      var onSuccess = function(response) {

        deferred.resolve({
          data: response
        })
      }

      return deferred.promise;
    }
    return factory;
  });

  /*RuleModule.service("RuleDatatableService", function ($http,RuleDatatableFactory,$q) {

      return {
          getAll: function(metavalue,sessionid) {
          	var deferred = $q.defer();
          	RuleDatatableFactory.findAll(metavalue).then(function(response){onSuccess(response.data)});
              var onSuccess=function(response){

              	var rowDataSet = [];
                  var headerColumns=['id','uuid','version','name','createdBy','createdOn']
                  for(var i=0;i<response.length;i++){
               	   var rowData = [];

               	   for(var j=0;j<headerColumns.length;j++){
               		   var columnname=headerColumns[j]
               		   if(columnname == "createdBy"){

               			   rowData[j]=response[i].createdBy.ref.name;
               		   }

               		   else{

               			   rowData[j]=response[i][columnname];
               		   }
               	   }
               	   rowDataSet[i]=rowData;

                  }

                   deferred.resolve({
                  	 data:rowDataSet
                   })
              }
              return deferred.promise;
          }
      }
  });*/
