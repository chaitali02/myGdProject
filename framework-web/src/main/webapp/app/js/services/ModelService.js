/**
 *
 */
DatascienceModule=angular.module('DatascienceModule');

DatascienceModule.factory('ModelFactory',function($http,$location){
    var factory={};
    factory.findAllLatest=function(type) {
    		 var url=$location.absUrl().split("app")[0]
             return $http({
				    method: 'GET',
				    url:url+"common/getAllLatest?action=view&type="+type,
				    }).
				    then(function (response,status,headers) {
			           return response;
			        })
    }
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		      	method: "GET",
           }).then(function(response){ return  response})


   }
   factory.findSaveAs=function(uuid,version,type){
  	  var url=$location.absUrl().split("app")[0]
		  return $http({
			        url:url+"common/saveAs?action=clone&uuid="+uuid+"&version="+version+"&type="+type,
			        method: "GET",
	          }).then(function(response){ return  response})
	  }
    factory.findExecuteModel=function(uuid,version){
      var url=$location.absUrl().split("app")[0]
      return $http({
              url:url+"model/execute?action=execute&uuid="+uuid+"&version="+version,
              headers: {
                'Accept':'*/*',
                'content-Type' : "application/json",
              },
              method:"POST",

            }).then(function(response){ return  response})
    }
    factory.findExecuteModelWithBody=function(uuid,version,data){
      var url=$location.absUrl().split("app")[0]
      return $http({
              url:url+"model/train1?action=execute&modelUUID="+uuid+"&modelVersion="+version,
              headers: {
                'Accept':'*/*',
                'content-Type' : "application/json",
              },
              method:"POST",
              data:JSON.stringify(data),
            }).then(function(response){ return  response})
    }

    factory.findOneByUuidandVersion=function(uuid,version,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
        method: "GET",

        }).then(function(response){ return  response})
    }

    factory.submit=function(data,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/submit?action=edit&type="+type,
          headers: {
            'Accept':'*/*',
            'content-Type' : "application/json",
          },
          method:"POST",
          data:JSON.stringify(data),
          }).success(function(response){return response})
    }
    factory.findGraphData=function(uuid,version,degree){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
          method: "GET"
        }).then(function(response){ return  response})
    };

    factory.findOneById=function(id,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getOneById?action=view&id="+id+"&type="+type,
        method: "GET"
        }).then(function(response){ return  response})
    }
    factory.findAllVersionByUuid=function(uuid,type){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
        method: "GET"
        }).then(function(response){ return  response})
    }

    factory.findModelExecByModel=function(uuid){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"model/getModelExecByModel?action=view&modelUUID="+uuid,
        method: "GET"
        }).then(function(response){ return  response})
    }

    factory.findParamSetByModel=function(uuid,version){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"metadata/getParamSetByModel?action=view&modelUuid="+uuid+"&modelVersion="+version,
        method: "GET"
        }).then(function(response){ return  response})
    };

    factory.findParamSetByAlgorithm=function(uuid,version){
      var url=$location.absUrl().split("app")[0]
      return $http({
        url:url+"metadata/getParamSetByAlgorithm?action=view&algorithmUuid="+uuid+"&algorithmVersion="+version,
        method: "GET"
        }).then(function(response){ return  response})
    };

    factory.findAttributeByDatapod=function(uuid,type) {
      var url=$location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url:url+"metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,
        }).then(function (response,status,headers) {return response;})
    }
    factory.findAttributesByDataset=function(uuid,type) {
      var url=$location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url:url+"metadata/getAttributesByDataset?action=view&uuid="+uuid+"&type=dataset",
          }).then(function (response,status,headers) {return response;})
    }
    factory.findDatapodByRelation=function(uuid,type) {
      var url=$location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url:url+"metadata/getDatapodByRelation?action=view&relationUuid="+uuid+"&type=datapod",
        }).then(function (response,status,headers) {
          return response;
        })
    }
    factory.findModelResult=function(uuid,version) {
      var url=$location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        url:url+"model/getResults?action=view&uuid="+uuid+"&version="+version,
        }).then(function (response,status,headers) {return response;})
    }
    factory.findModelScript=function(uuid,version) {
      var url=$location.absUrl().split("app")[0]
      return $http({
        method: 'GET',
        headers: {
          'Accept':'*/*',
          'content-Type' : "application/text",
        },
        url:url+"model/getModelScript?action=view&uuid="+uuid+"&version="+version,
        }).then(function (response,status,headers) {return response;})
    }
    factory.uploadFile=function(url,data){
    	var fullUrl = $location.absUrl().split("app")[0] + url
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
  return factory;
})

DatascienceModule.service("ModelService", function ($http,ModelFactory,$q,sortFactory) {

  this.getOneById=function(uuid,type){
    var deferred = $q.defer();
    ModelFactory.findOneById(uuid,type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getModelScript=function(uuid,version){
    var deferred = $q.defer();
    ModelFactory.findModelScript(uuid,version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getModelResult=function(uuid,version){
    var deferred = $q.defer();
    ModelFactory.findModelResult(uuid,version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getAllAttributeBySource=function(uuid,type){
     var deferred = $q.defer();
     if(type == "relation"){
       ModelFactory.findDatapodByRelation(uuid,type).then(function(response){onSuccess(response.data)});
            var onSuccess=function(response){
               var attributes=[];
              for(var j=0;j<response.length;j++){
              for(var i=0;i<response[j].attributes.length;i++){
                var attributedetail={};
                attributedetail.uuid=response[j].uuid;
                attributedetail.datapodname=response[j].name;
                attributedetail.name=response[j].attributes[i].name;
                attributedetail.dname=response[j].name+"."+response[j].attributes[i].name;
                attributedetail.attributeId=response[j].attributes[i].attributeId;
                attributes.push(attributedetail)
              }
              }

              console.log(JSON.stringify(attributes))
                deferred.resolve({
                         data:attributes
                     })
            }
     }
     if(type == "dataset"){
       ModelFactory.findAttributesByDataset(uuid).then(function(response){onSuccess(response.data)});
            var onSuccess=function(response){
                var attributes=[];
                for(var j=0;j<response.length;j++){
                  var attributedetail={};
                  attributedetail.uuid=response[j].ref.uuid;
                  attributedetail.datapodname=response[j].ref.name;
                  attributedetail.name=response[j].attrName;
                  attributedetail.attributeId=response[j].attrId;
                  attributedetail.id=response[j].ref.uuid+"_"+response[j].attrId;
                  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
                  attributes.push(attributedetail)
                }
                deferred.resolve({
                         data:attributes
                     })
            }


     }
     if(type == "datapod"){
       ModelFactory.findAttributeByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
            var onSuccess=function(response){
                var attributes=[];
                for(var j=0;j<response.length;j++){
                  var attributedetail={};
                  attributedetail.uuid=response[j].ref.uuid;
                  attributedetail.datapodname=response[j].ref.name;
                  attributedetail.name=response[j].attrName;
                  attributedetail.attributeId=response[j].attrId;
                  attributedetail.id=response[j].ref.uuid+"_"+response[j].attrId;
                  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
                  attributes.push(attributedetail)
                }
                deferred.resolve({
                         data:attributes
                     })
            }

     }

          return deferred.promise;
  }

  this.getModelExecByModel=function(uuid){
    var deferred = $q.defer();
    ModelFactory.findModelExecByModel(uuid).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      var rowDataSet = [];
      var headerColumns=['id','uuid','version','name','createdBy','createdOn','status','action']
      for(var i=0;i<response.length;i++){
        var rowData = [];
        if(response[i].status !=null){
          response[i].status.sort(sortFactory.sortByProperty("createdOn"));
          var len=response[i].status.length-1;
        }
        for(var j=0;j<headerColumns.length;j++){
          var columnname=headerColumns[j]
          if(columnname == "createdBy"){
            rowData[j]=response[i].createdBy.ref.name;
          }
          else if(columnname == "status"){
            if(response[i].status !=null){
              if(response[i].status[len].stage == "InProgress"){
                rowData[j]="In Progress";
              }
              else if(response[i].status[len].stage == "NotStarted"){
                rowData[j]="Not Started";
              }
              else{
                rowData[j]=response[i].status[len].stage;
              }
            }
            else{
              rowData[j]=" ";
            }
          }
          else if(columnname == "action"){
            rowData[j]=false;
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
  this.getParamSetByModel=function(uuid,version){
    var deferred = $q.defer();
    ModelFactory.findParamSetByModel(uuid,version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }

  this.getParamSetByAlgorithm=function(uuid,version){
    var deferred = $q.defer();
    ModelFactory.findParamSetByAlgorithm(uuid,version).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }

  this.getAllVersionByUuid=function(uuid,type){
    var deferred = $q.defer();
    ModelFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getExecuteModel=function(uuid,version,data){
    var deferred = $q.defer();
    if(data !=null){
      ModelFactory.findExecuteModelWithBody(uuid,version,data).then(function(response){onSuccess(response.data)});
      var onSuccess=function(response){
        deferred.resolve({
           data:response
        });
      }
    }
    else{
      ModelFactory.findExecuteModel(uuid,version).then(function(response){onSuccess(response.data)});
      var onSuccess=function(response){
        deferred.resolve({
           data:response
        });
      }

    }

    return deferred.promise;
  }

  this.saveAs=function(uuid,version,type){
		 var deferred = $q.defer();
		 ModelFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }
	 this.getLatestByUuid=function(uuid,type){
		 var deferred = $q.defer();
		 ModelFactory.findLatestByUuid(uuid,type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){

	    	  deferred.resolve({
	              data:response
	          });
	     }
	     return deferred.promise;
	 }

   this.getOneByUuidandVersion=function(uuid,version,type){
     var deferred = $q.defer();
     ModelFactory.findOneByUuidandVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
       var onSuccess=function(response){

          deferred.resolve({
                data:response
            });
       }
       return deferred.promise;
   }

  this.getAllLatest=function(type){
   var deferred = $q.defer();
    ModelFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }
  this.getGraphData=function(uuid,version,degree){
   var deferred = $q.defer();
    ModelFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
      deferred.resolve({
        data:response
      });
    }
    return deferred.promise;
  }

  this.getAllLatestList=function(type) {
    var deferred = $q.defer();
    ModelFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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

    this.submit=function(data,type){
     var deferred = $q.defer();
     ModelFactory.submit(data,type).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
       var onSuccess=function(response){
          deferred.resolve({
                data:response
            });
       }
       var onError=function(response){
       deferred.reject({
         data:response
       })
     }
       return deferred.promise;
   }
   this.uploadFile=function(extension,data,fileType){
    var url="common/upload?action=edit&extension="+extension+"&fileType="+fileType
    var deferred = $q.defer();
    ModelFactory.uploadFile(url,data).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
          deferred.resolve({
              data:response
           });
        }
       return deferred.promise;
  }
});
