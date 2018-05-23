/**
 *
 */
(function() {
  'use strict';
  var CommonModule = angular.module('CommonModule');
  CommonModule.factory('CommonFactory', function($http, $location) {
    var url = $location.absUrl().split("app")[0]
    var factory = {};
    var baseUrl = $location.absUrl().split("app")[0];
    factory.httpGet = function(url) {
      var fullUrl = baseUrl + url
      return $http({
        method: 'GET',
        url:fullUrl,
      }).then(function(response, status, headers) {
        return response;
      })
    }
    factory.httpPost = function(url, data) {
      var fullUrl = baseUrl + url
      return $http({
        url: fullUrl,
        headers: {
          'Accept': '*/*',
          'content-Type': "application/json",
        },
        method: "POST",
        data: JSON.stringify(data),
      }).success(function(response) {
        return response
      })
    }
    factory.httpPut = function(url, data) {
      var fullUrl = baseUrl + url
      return $http({
        url: fullUrl,
        headers: {
          'Accept': '*/*',
          'content-Type': "application/json",
        },
        method: "PUT",
        data: JSON.stringify(data),
      }).success(function(response) {
        return response
      })
    }
    factory.SaveFile=function(url,data){
    	var fullUrl = baseUrl + url
      return $http({
        url:fullUrl,
        headers: {
          'Accept':'*/*',
          'content-Type' :undefined,
        },
        method:"POST",
        transformRequest: angular.identity,
        data:data,
        }).success(function(response){return response})
    }
    factory.uploadFile=function(url,data){
    	var fullUrl = baseUrl + url
      return $http({
        url:fullUrl,
        headers: {
          'Accept':'*/*',
          'content-Type' :undefined,
        },
        method:"POST",
        transformRequest: angular.identity,
        data:data,
        }).success(function(response){return response})
    }
    factory.findBaseEntityStatusByCriteria = function(type, name, userName, startDate, endDate, tags, active,published,status) {
      var url = $location.absUrl().split("app")[0]
      return $http({
        url: url + "metadata/getBaseEntityStatusByCriteria?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&active=" + active +  "&published=" + published +"&status=" + status,
        method: "GET",
      }).then(function(response) {
        return response
      })
    }
    return factory;
  });

  CommonModule.service('CommonService', function($http, CommonFactory, $q, sortFactory, $location) {

    /*Start MetaStats*/
    this.getMetaStats = function(type) {
      var deferred = $q.defer();
      var url = "common/getMetaStats?action=view";
      if(type && type!= ''){
        url = url + "&type=" + type;
      }
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End MetaStats*/

    /*Start Delete*/
    this.delete = function(id, type) {
      var deferred = $q.defer();
      var url = $location.absUrl().split("app")[0];
      url =  url + "common/delete?action=delete&id=" + id + "&type=" + type;
       $http.put(url).then(function(response) {
         OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End Delete*/

    /*Start Restore*/
    this.restore = function(id, type) {
      var deferred = $q.defer();
      var url = $location.absUrl().split("app")[0];
      url =  url + "common/restore?action=restore&id=" + id + "&type=" + type;
      $http.put(url).then(function(response) {
      OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End Restore*/
    this.publish = function(id, type) {
      var deferred = $q.defer();
      var url = $location.absUrl().split("app")[0];
      url =  url + "common/publish?action=publish&id=" + id + "&type=" + type;
       $http.put(url).then(function(response) {
         OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End Delete*/

    /*Start Restore*/
    this.unpublish = function(id, type) {
      var deferred = $q.defer();
      var url = $location.absUrl().split("app")[0];
      url =  url + "common/unPublish?action=unpublish&id=" + id + "&type=" + type;
      $http.put(url).then(function(response) {
      OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    }
    /*Start getOneById*/
    this.getOneById = function(id, type) {
      var deferred = $q.defer();
      var url = "common/getOneById?action=view&id=" + id + "&type=" + type;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getOneById*/

    /*Start getLatestByUuid*/
    this.getLatestByUuid = function(uuid, type) {
      var deferred = $q.defer();
      var url = "common/getLatestByUuid?action=view&uuid=" + uuid + "&type=" + type;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getLatestByUuid*/
    // start getOneByUuidAndVersion
    this.getOneByUuidAndVersion = function(uuid, version, type) {
      var deferred = $q.defer();
      var url = "common/getOneByUuidAndVersion?action=view&uuid=" + uuid + "&version="+version + "&type=" + type;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getOneByUuidAndVersion*/
    /*Start getAllLatest*/
    this.getAllLatest = function(type) {
      var deferred = $q.defer();
      var url = "common/getAllLatest?action=view&type=" + type;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getAllLatest*/

    this.getMetaExecList = function(type) {
      var deferred = $q.defer();
      var url = "metadata/getMetaExecList?action=view&type=session";
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    }

    this.SaveFile=function(filename,data,type){
      var url="admin/upload?action=edit&fileName="+filename+"&type="+type+"&fileType=zip"
  		var deferred = $q.defer();
  	    CommonFactory.SaveFile(url,data).then(function(response){onSuccess(response.data)});
    	    var onSuccess=function(response){
      	    deferred.resolve({
                data:response
             });
          }
         return deferred.promise;
  	}
    this.uploadFile=function(dataUuid,data,type){
      var url="datapod/upload?action=edit&datapodUuid="+dataUuid+"&type="+type
  		var deferred = $q.defer();
  	    CommonFactory.uploadFile(url,data).then(function(response){onSuccess(response.data)});
    	    var onSuccess=function(response){
      	    deferred.resolve({
                data:response
             });
          }
         return deferred.promise;
  	}
    this.getRegisterFile = function(urlUpload) {
      var url = "metadata/registerFile?action=view&csvFileName="+urlUpload+"&type=datapod"
      var deferred = $q.defer();
      CommonFactory.httpGet(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        })
      }
      return deferred.promise;
    };
    this.getSaveAS = function(uuid, version, type) {
      var url = "common/saveAs?action=clone&uuid=" + uuid + "&version=" + version + "&type=" + type
      var deferred = $q.defer();
      CommonFactory.httpPost(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        })
      }
      return deferred.promise;
    };
    this.setActivity = function(uuid,version,type,action) {
      var deferred = $q.defer();
      var url = "security/setActivity?uuid="+uuid+"&version="+version+"&type="+type+"&action="+action;
      CommonFactory.httpPost(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getOneById*/
    
    this.getBaseEntityByCriteria = function(type, name, userName, startDate, endDate, tags, active, published) {
      var deferred = $q.defer();
      var url = "metadata/getBaseEntityByCriteria?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&published=" + published + "&active=" + active;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getBaseEntityByCriteria*/
    
    this.getParamListByRule = function(type, name, userName, startDate, endDate, tags, active, published) {
      var deferred = $q.defer();
      var url = "metadata/getParamListByRule?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&published=" + published + "&active=" + active;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getParamListByRule*/

    this.getParamListByModel = function(type, name, userName, startDate, endDate, tags, active, published) {
      var deferred = $q.defer();
      var url = "metadata/getParamListByModel?action=view&type=" + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&published=" + published + "&active=" + active;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getParamListByModel*/

    /*Start getAll*/
    this.getAll = function(type) {
      var deferred = $q.defer();
      var url = "common/getAll?action=view&type=" + type;
      CommonFactory.httpGet(url).then(function(response) {
        OnSuccess(response.data)
      });
      var OnSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getAll*/

    /*Start getGraphResults*/
    this.getGraphData = function(uuid, version, degree) {
      var deferred = $q.defer();
      var url = "graph/getGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree;
      CommonFactory.httpGet(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getGraphResults*/

     /*Start getTreeGraphResults*/
     this.getTreeGraphResults = function(uuid, version, degree) {
      var deferred = $q.defer();
      var url = "graph/getTreeGraphResults?action=view&uuid=" + uuid + "&version=" + version + "&degree=" + degree;
      CommonFactory.httpGet(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    } /*End getTreeGraphResults*/

    this.execute = function(type, uuid, version, data) {
      var url;
      if (type.substr(-5) == 'group') {
        var plainType = type.slice(0, -5);
      } else {
        var plainType = type;
      }
      url = "" + (plainType == "dq" ? "dataqual" : plainType) + "/execute";
      if (type.substr(-5) == 'group') {
        url = url + 'Group?uuid=' + uuid + "&version=" + version;
      } else {
        url = url + "?uuid=" + uuid + "&version=" + version;
      }
      url += '&action=execute'
      var deferred = $q.defer();
      CommonFactory.httpPost(url, data).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        })
      }
      return deferred.promise;
    };

    /*Start getBaseEntityStatusByCriteria*/
    this.getBaseEntityStatusByCriteria = function(type, name, userName, startDate, endDate, tags, active,published,status) {
      var deferred = $q.defer();
      CommonFactory.findBaseEntityStatusByCriteria(type, name, userName, startDate, endDate, tags, active,published,status).then(function(response) {
        onSuccess(response.data)
      });
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
    } /*End getBaseEntityStatusByCriteria*/

    this.getParamSetByType = function(type, uuid, version) {
      var deferred = $q.defer();
      var url;
      if (type == "rule") {
        url = "metadata/getParamSetByRule?ruleUuid=" + uuid+"&type="+type;
      } else {
        url = "metadata/getParamSetByTrain?trainUuid=" + uuid + "&trainVersion=" + version+"&type="+type;
      }
      url += '&action=view'
      CommonFactory.httpGet(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      return deferred.promise;
    }
    this.getParamListByType = function(type, uuid, version) {
      var deferred = $q.defer();
      var url;
      if (type == "simulate") {
        url = "metadata/getParamListBySimulate?uuid=" + uuid+"&type="+type;
      }
      else if(type == "operator"){
        url = "metadata/getParamListByOperator?uuid=" + uuid+"&type="+type;
      }
     
      else if(type == "operatortype"){
        url = "metadata/getParamListByOperatorType?uuid=" + uuid+"&type="+type;
      }
      else if(type =='distribution'){
        url="metadata/getParamListByDistribution?uuid=" + uuid+"&type="+type;
      }
      url += '&action=view'
      CommonFactory.httpGet(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        var paramListHolder=[];
        var type=["ONEDARRAY","TWODARRAY"];
        var type1=['distribution','attribute','attributes','datapod'];
        if(response.length >0){
          for(var i=0;i<response.length;i++){
            var paramList={};
            paramList.uuid=response[i].ref.uuid;
            paramList.type=response[i].ref.type;
            paramList.paramId=response[i].paramId;
            paramList.paramType=response[i].paramType.toLowerCase();
            paramList.paramName=response[i].paramName;
            paramList.ref=response[i].ref;
            paramList.attributeInfo;
            paramList.allAttributeinto=[];
            paramList.attributeInfoTag=[];
            if(type1.indexOf(response[i].paramType) == -1 ){
              paramList.isParamType="simple";
              paramList.paramValue=response[i].paramValue.value;
              paramList.selectedParamValueType='simple'
            }else if(type1.indexOf(response[i].paramType) != -1){
              paramList.isParamType=response[i].paramType;
              paramList.selectedParamValueType=response[i].paramType=="distribution" ?response[i].paramType:"datapod";
              paramList.paramValue=response[i].paramValue;
              if(response[i].paramValue !=null){
              var selectedParamValue={};
              selectedParamValue.uuid=response[i].paramValue.ref.uuid;
              selectedParamValue.type=response[i].paramValue.ref.type;
              paramList.selectedParamValue=selectedParamValue;
              }
            }else{
              paramList.isParamType="datapod";
              paramList.selectedParamValueType='datapod'
              paramList.paramValue=response[i].paramValue;    
            }
           
            paramListHolder[i]=paramList;
          }
        }
        deferred.resolve({
          data: paramListHolder
        });
      }
      return deferred.promise;
    }
    this.executeWithParams = function(type, uuid, version, data) {
      var deferred = $q.defer();
      var url
      if(type=='model' || type== "train"){
        url = "model/train/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      }
      else if(type=='predict'){
        url = "model/predict/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      }
      else if(type=='simulate'){
        url = "model/simulate/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      }
      else if ( type =='operator'){
        url = "model/operator/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      }
      else{
        url = "" + type + "/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      }

     
      CommonFactory.httpPost(url, data).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }
      var onError = function (response) {
        deferred.reject({
          data: response
        })
      }

      // else{
      //   RuleFactory.findexecuteRuleWithParams(uuid,version).then(function(response){onSuccess(response.data)});
      //   var onSuccess=function(response){
      //     deferred.resolve({
      //       data:response
      //     });
      //   }
      // }
      return deferred.promise;
    }

    this.restartExec = function(type,uuid,version) {
      var deferred = $q.defer();
      var api = false;
      switch (type) {
        case 'dqexec':
          api = 'dataqual';
          break;
        case 'dqgroupExec':
          api = 'dataqual';
          break;
        case 'profileExec':
          api = 'profile';
          break;
        case 'profilegroupExec':
          api = 'profile';
          break;
        case 'ruleExec':
          api = 'rule';
          break;
        case 'rulegroupExec':
          api = 'rule';
          break;
        case 'dagexec':
          api = 'dag';
          break;
      }
      if(!api){
        return
      }
      var url=api+'/restart?uuid='+uuid+'&version='+version+'&type='+type+'&action=execute'
      CommonFactory.httpPost(url).then(function(response) {
        onSuccess(response.data)
      });
      var onSuccess = function(response) {
        deferred.resolve({
          data: response
        });
      }


      return deferred.promise;
    }

    this.getAllAttributeBySource = function (uuid, type) {
      var deferred = $q.defer();
  
      if (type == "relation") {
        CommonFactory.findDatapodByRelation(uuid, type).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          var attributes = [];
          for (var j = 0; j < response.length; j++) {
            for (var i = 0; i < response[j].attributes.length; i++) {
              var attributedetail = {};
              attributedetail.uuid = response[j].uuid;
              attributedetail.datapodname = response[j].name;
              attributedetail.name = response[j].attributes[i].name;
              attributedetail.dname = response[j].name + "." + response[j].attributes[i].name;
              attributedetail.attributeId = response[j].attributes[i].attributeId;
              attributes.push(attributedetail)
            }
          }
  
          console.log(JSON.stringify(attributes))
          deferred.resolve({
            data: attributes
          })
        }
      }
      if (type == "dataset") {
        var url= "metadata/getAttributesByDataset?action=view&uuid=" + uuid + "&type=dataset"
        CommonFactory.httpGet(url).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          var attributes = [];
          for (var j = 0; j < response.length; j++) {
            var attributedetail = {};
            attributedetail.uuid = response[j].ref.uuid;
            attributedetail.datapodname = response[j].ref.name;
            attributedetail.name = response[j].attrName;
            attributedetail.attributeId = response[j].attrId;
            attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
            attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
            attributes.push(attributedetail)
          }
          deferred.resolve({
            data: attributes
          })
        }
  
  
      }
      if (type == "datapod") {
        var url= "metadata/getAttributesByDatapod?action=view&uuid=" + uuid + "&type=datapod"
        CommonFactory.httpGet(url).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          var attributes = [];
          for (var j = 0; j < response.length; j++) {
            var attributedetail = {};
            attributedetail.uuid = response[j].ref.uuid;
            attributedetail.datapodname = response[j].ref.name;
            attributedetail.name = response[j].attrName;
            attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
            attributedetail.attributeId = response[j].attrId;
            attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
            attributes.push(attributedetail)
          }
          deferred.resolve({
            data: attributes
          })
        }
  
      }
      if (type == "rule") {
        var url= "metadata/getAttributesByRule?action=view&uuid=" + uuid + "&type=rule"
        CommonFactory.httpGet(url).then(function (response) { onSuccess(response.data) });
        var onSuccess = function (response) {
          var attributes = [];
          for (var j = 0; j < response.length; j++) {
            var attributedetail = {};
            attributedetail.uuid = response[j].ref.uuid;
            attributedetail.datapodname = response[j].ref.name;
            attributedetail.name = response[j].attrName;
            attributedetail.dname = response[j].ref.name + "." + response[j].attrName;
            attributedetail.attributeId = response[j].attrId;
            attributedetail.id = response[j].ref.uuid+"_"+response[j].attrId;
            attributes.push(attributedetail)
          }
          deferred.resolve({
            data: attributes
          })
          //console.log(JSON.stringify(response))
        }
  
      }
  
      return deferred.promise;
    }
  });

})();
