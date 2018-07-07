/**
 *
 */
MetadataModule=angular.module('MetadataModule');
MetadataModule.factory('MetadataDagFactory',function($http,$location){
    var factory={}
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }
    factory.findLatestDagByUuidDagExec=function(uuid){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"dag/getDagByDagExec?action=view&DagExecUuid="+uuid,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }
    factory.getStatusByDagExec=function(uuid){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"dag/getStatusByDagExec?action=view&DagExecUuid="+uuid,
  		      	method: "GET",
           }).then(function(response){ return  response})
    }
    factory.findOneByUuidAndVersion=function(uuid,version,type){
    	 var url=$location.absUrl().split("app")[0]
    	 return $http({
    		url:url+"common/getOneByUuidAndVersion?action=view&uuid="+uuid+"&version="+version+"&type="+type,
    		method: "GET",

  	   	}).then(function(response){ return  response})
  	  }
      factory.submit=function(data,type,upd_tag){
     	  var url=$location.absUrl().split("app")[0]
     	  return $http({
               url:url+"common/submit?action=edit&type="+type+"&upd_tag="+upd_tag,
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
	    factory.findAllLatest=function(type,inputFlag) {
           var url=$location.absUrl().split("app")[0]
           return $http({
                  method: 'GET',
                  url:url+"common/getAllLatest?action=view&type="+type+"&inputFlag="+inputFlag,

                  }).
                  then(function (response,status,headers) {
                     return response;
                  })
       }
	   factory.excutionDag=function(uuid,version,data){
		   var url=$location.absUrl().split("app")[0]
			  return $http({
	              url:url+"dag/execute?action=execute&uuid="+uuid+"&version="+version,
	                headers: {
	                 'Accept':'*/*',
	                 'content-Type':"application/json",
	                },
	              method:"POST",
	              data:JSON.stringify(data),
	         }).success(function(response){return response})
	      }
	   factory.findSaveAs=function(uuid,version,type){
	    	  var url=$location.absUrl().split("app")[0]
			  return $http({
				        url:url+"common/saveAs?action=clone&uuid="+uuid+"&version="+version+"&type="+type,
				        method: "GET",
		          }).then(function(response){ return  response})
		  }
	   factory.findDagTemplates=function(type){
	    	  var url=$location.absUrl().split("app")[0]
			  return $http({
				        url:url+"dag/getDagTemplates?action=view&type="+type,
				        method: "GET",
		          }).then(function(response){ return  response})
		  }
        factory.findDagExecByDag=function(uuid,type){
			var url=$location.absUrl().split("app")[0]
			return $http({
					  url:url+"metadata/getDagExecByDag?action=view&dagUUID="+uuid+"&type="+type,
					  method: "GET",
				}).then(function(response){ return  response})
		}
      factory.findBaseEntityStatusByCriteria=function(type,name,userName,startDate,endDate,tags,active,status){
    	    	var url=$location.absUrl().split("app")[0]
    	  	    return $http({
    	  		        url:url+"metadata/getBaseEntityStatusByCriteria?action=view&type="+type+"&name="+name+"&userName="+userName+"&startDate="+startDate+"&endDate="+endDate+"&tags="+tags+"&active="+active+"&status="+status,
    	  		      	method: "GET",
    	        }).then(function(response){ return  response})

    	}
   return factory;
});

MetadataModule.service('MetadataDagSerivce',function($q,sortFactory,MetadataDagFactory){
    this.getDagTemplates=function(type){
		var deferred=$q.defer();
		MetadataDagFactory.findDagTemplates(type).then(function(response){onSuccess(response.data)});
		var onSuccess=function(response){
		   deferred.resolve({
					   data:response
				   })
		  }
		return deferred.promise;
	};
	this.getDagExecByDag=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findDagExecByDag(uuid,type).then(function(response){onSuccess(response.data)});
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
	   };
	this.getSaveAS=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		   return deferred.promise;
	   };
	this.excutionDag=function(uuid,version,data){
		   var deferred=$q.defer();
		   MetadataDagFactory.excutionDag(uuid,version,data).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		   	  if(response !=null){
			  var dagjson={};
			  var stagesarray=[];
			  dagjson.dagdata=response;
			  for(var i=0;i<response.stages.length;i++){
			   var stagesjson={};
			   var tasksarray=[];
			   stagesjson.stageId=response.stages[i].stageId;
			   stagesjson.name=response.stages[i].name;
			   stagesjson.dependsOn=response.stages[i].dependsOn;
			   for(var j=0;j< response.stages[i].tasks.length;j++){
				 var taskjson={};
				 taskjson.taskId=response.stages[i].tasks[j].taskId;
				 taskjson.name=response.stages[i].tasks[j].name;
				 taskjson.dependsOn=response.stages[i].tasks[j].dependsOn;
				 taskjson.operatorId=response.stages[i].tasks[j].operators[0].operatorId;
				 taskjson.operatorId=response.stages[i].tasks[j].operators[0].operatorId;
				 var operatorinfo={}
				 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "map"){
					 var obj={};
					 var operatormap={}
					 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 operatorinfo.type=obj;
					 operatorinfo.isOpetatorMap=true;
					 operatorinfo.isOpetatorDqGroup=false;
					 operatorinfo.isOpetatorDQ=false;
					 operatorinfo.isOpetatorLoad=false;
					 operatormap.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
					 operatormap.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
					 operatorinfo.operatormap=operatormap
				 }
				 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "dq"){
					 var obj={};
					 var operatordq={}
					 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 operatorinfo.type=obj;
					 operatorinfo.isOpetatorMap=false;
					 operatorinfo.isOpetatorDqGroup=false;
					 operatorinfo.isOpetatorLoad=false;
					 operatorinfo.isOpetatorDQ=true;
					 operatordq.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
					 operatordq.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
					 operatorinfo.operatordq=operatordq
				 }
				 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "dqgroup"){
					 var obj={};
					 var operatordqgroup={}
					 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 operatorinfo.type=obj;
					 operatorinfo.isOpetatorMap=false;
					 operatorinfo.isOpetatorLoad=false;
					 operatorinfo.isOpetatorDqGroup=true;
					 operatorinfo.isOpetatorDQ=false;
					 operatordqgroup.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
					 operatordqgroup.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
					 operatorinfo.operatordqgroup=operatordqgroup
				 }
				 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "load"){
					 var obj={};
					 var operatorload={}
					 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
					 operatorinfo.type=obj;
					 operatorinfo.isOpetatorMap=false
					 operatorinfo.isOpetatorLoad=true;
					 operatorinfo.isOpetatorDqGroup=false;
					 operatorinfo.isOpetatorDQ=false;
					 operatorload.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
					 operatorload.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
					 operatorinfo.operatorload=operatorload
				 }

				 taskjson.operatorinfo=operatorinfo;
				 tasksarray[j]=taskjson
			   }
			   stagesjson.task=tasksarray;
			   stagesarray[i]=stagesjson;
			  }
			  dagjson.stage=stagesarray;
			 console.log(JSON.stringify(dagjson.stage))
		      deferred.resolve({
		                  data:dagjson
		              })
		  }
		     }
		  return deferred.promise;
		}
	this.getLatestByUuidDagExec=function(id,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getLatestDagByUuidDagExec=function(id){
		   var deferred=$q.defer();
		   MetadataDagFactory.findLatestDagByUuidDagExec(id).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getStatusByDagExec=function(id){
		   var deferred=$q.defer();
		   MetadataDagFactory.getStatusByDagExec(id).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getOneById=function(id,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findOneById(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataDagFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getOneByUuidAndVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataDagFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
				  var dagjson={};
				  var stagesarray=[];
				  dagjson.dagdata=response;
				  for(var i=0;i<response.stages.length;i++){
				   var stagesjson={};
				   var tasksarray=[];
				   stagesjson.stageId=response.stages[i].stageId;
				   stagesjson.name=response.stages[i].name;
				   stagesjson.dependsOn=response.stages[i].dependsOn;
				   for(var j=0;j< response.stages[i].tasks.length;j++){
					 var taskjson={};
					 taskjson.taskId=response.stages[i].tasks[j].taskId;
					 taskjson.name=response.stages[i].tasks[j].name;
					 taskjson.dependsOn=response.stages[i].tasks[j].dependsOn;
					 taskjson.operatorId=response.stages[i].tasks[j].operators[0].operatorId;
					 taskjson.operatorId=response.stages[i].tasks[j].operators[0].operatorId;
					 var operatorinfo={}
					 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref && response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "map"){
						 var obj={};
						 var operatormap={}
						 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 operatorinfo.type=obj;
						 operatorinfo.isOpetatorMap=true;
						 operatorinfo.isOpetatorDqGroup=false;
						 operatorinfo.isOpetatorDQ=false;
						 operatorinfo.isOpetatorLoad=false;
						 operatormap.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
						 operatormap.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
						 operatorinfo.operatormap=operatormap
					 }
					 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref && response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "dq"){
						 var obj={};
						 var operatordq={}
						 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 operatorinfo.type=obj;
						 operatorinfo.isOpetatorMap=false;
						 operatorinfo.isOpetatorDqGroup=false;
						 operatorinfo.isOpetatorLoad=false;
						 operatorinfo.isOpetatorDQ=true;
						 operatordq.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
						 operatordq.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
						 operatorinfo.operatordq=operatordq
					 }
					 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref && response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "dqgroup"){
						 var obj={};
						 var operatordqgroup={}
						 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 operatorinfo.type=obj;
						 operatorinfo.isOpetatorMap=false;
						 operatorinfo.isOpetatorLoad=false;
						 operatorinfo.isOpetatorDqGroup=true;
						 operatorinfo.isOpetatorDQ=false;
						 operatordqgroup.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
						 operatordqgroup.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
						 operatorinfo.operatordqgroup=operatordqgroup
					 }
					 if(response.stages[i].tasks[j].operators[0].operatorInfo.ref && response.stages[i].tasks[j].operators[0].operatorInfo.ref.type == "load"){
						 var obj={};
						 var operatorload={}
						 obj.text=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 obj.caption=response.stages[i].tasks[j].operators[0].operatorInfo.ref.type;
						 operatorinfo.type=obj;
						 operatorinfo.isOpetatorMap=false
						 operatorinfo.isOpetatorLoad=true;
						 operatorinfo.isOpetatorDqGroup=false;
						 operatorinfo.isOpetatorDQ=false;
						 operatorload.uuid=response.stages[i].tasks[j].operators[0].operatorInfo.ref.uuid;
						 operatorload.name=response.stages[i].tasks[j].operators[0].operatorInfo.ref.name;
						 operatorinfo.operatorload=operatorload
					 }

					 taskjson.operatorinfo=operatorinfo;
					 tasksarray[j]=taskjson
				   }
				   stagesjson.task=tasksarray;
				   stagesarray[i]=stagesjson;
				  }
				  dagjson.stage=stagesarray;
				// console.log(JSON.stringify(dagjson.stage))
			      deferred.resolve({
			                  data:dagjson
			              })
			     }
		  return deferred.promise;
		}
  this.getBaseEntityStatusByCriteria=function(type,name,userName,startDate,endDate,tags,active,status){
		var deferred = $q.defer();
    	MetadataDagFactory.findBaseEntityStatusByCriteria(type,name,userName,startDate,endDate,tags,active,status).then(function(response){onSuccess(response.data)});
	    var onSuccess=function(response){
            var rowDataSet = [];
            var headerColumns=['id','uuid','version','name','createdBy','createdOn','status']
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
	this.submit=function(data,type,upd_tag){
		   var deferred=$q.defer();
		   MetadataDagFactory.submit(data,type,upd_tag).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
         var onError=function(response){
         deferred.reject({
           data:response
         })
       }

		  return deferred.promise;
		}
    this.getAllLatest=function(type) {
	     var deferred = $q.defer();
	     MetadataDagFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
	     var onSuccess=function(response){
	     if(response){
         response.sort(sortFactory.sortByProperty("name"));
       }
	      deferred.resolve({
	                  data:response
	              })
	     }
	 return deferred.promise;
	 }

    this.getAllLatestGrid=function(type) {
    	var deferred = $q.defer();
    	MetadataDagFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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
});
