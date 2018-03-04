/**
 *
 */
MetadataModule=angular.module('MetadataModule');

MetadataModule.factory('MetadataDatatableFactory',function($http,$location){
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

   return factory;
})

MetadataModule.service("MetadataDatatableService", function (MetadataDatatableFactory,$q) {
        this.setUuid=function(metavalue) {
        	var deferred = $q.defer();
            MetadataDatatableFactory.findAllLatest(metavalue).then(function(response){onSuccess(response.data)});
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

MetadataModule.factory('MetadataFactory',function($http,$location){
    var factory={}
    factory.findAllLatest=function(type) {
        var url=$location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url:url+"common/getAllLatest?action=view&type="+type,
            }).then(function (response,status,headers) {
                return response;
                })
    }

    factory.findAttributesByDataset=function(uuid,type){
        var url=$location.absUrl().split("app")[0]
		return $http({
			url:url+"metadata/getAttributesByDataset?action=view&uuid="+uuid,
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

    factory.findAttributesByDatapod=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
		return $http({
			url:url+"metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,
			method: "GET",
	        }).then(function(response){ return  response})
	}

   factory.findDatapodByRelation=function(uuid,type){
        var url=$location.absUrl().split("app")[0]
        return $http({
			url:url+"metadata/getDatapodByRelation?action=view&relationUuid="+uuid+"&type=datapod",
			method: "GET"
	        }).then(function(response){return  response})
	}

    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		    url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
  		    method: "GET",
            }).then(function(response){ return  response})
    }

    factory.findFormulaByType=function(uuid,type) {
        var url=$location.absUrl().split("app")[0]
        return $http({
                method: 'GET',
                url:url+"metadata/getFormulaByType?action=view&uuid="+uuid+"&type="+type,
                }).then(function (response,status,headers) {return response;})
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
    factory.findFile=function(filename,data){
    	var url=$location.absUrl().split("app")[0]
        return $http({
            url:url+"metadata/file?action=edit&fileName="+filename,
               headers: {
               	'Accept':'*/*',
                'content-Type' :undefined,
                 },
            method:"POST",
            transformRequest: angular.identity,
            data:data,
        }).success(function(response){return response})
    }
    factory.findRegisterFile=function(urlUpload) {
        var url=$location.absUrl().split("app")[0]
        return $http({
            method: 'GET',
            url:url+"metadata/registerFile?action=view&csvFileName="+urlUpload,
            }).then(function (response,status,headers) {return response;})
    }
    return factory;
})



MetadataModule.service('MetadataSerivce',function($http,$location,$q,sortFactory,MetadataFactory){
	var url=$location.absUrl().split("app")[0]
	this.getRegisterFile=function(urlUpload){
		var deferred = $q.defer();
	    MetadataFactory.findRegisterFile(urlUpload).then(function(response){onSuccess(response.data)});
  	    var onSuccess=function(response){
    	    deferred.resolve({
              data:response
           });
        }
       return deferred.promise;
	}
	this.getFile=function(filename,data){
		var deferred = $q.defer();
	    MetadataFactory.findFile(filename,data).then(function(response){onSuccess(response.data)});
  	    var onSuccess=function(response){

    	    deferred.resolve({
              data:response
           });
        }
       return deferred.promise;
	}
	this.getGraphData=function(uuid,version,degree,sessionid){
		   return $http({
		                url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
		                method: "GET",
		                headers: {
					    	   'sessionId':sessionid
					    	 },
		          }).then(function(response){ return  response})
	   };

	this.getAttributesByDataset=function(uuid,type){
	    return $http({
		    url:url+"metadata/getAttributesByDataset?action=view&uuid="+uuid,
				method: "GET",
		        }).then(function(response){ return  response})
    }

	this.getAttributesByDatapod=function(uuid,type){
	    return $http({
		    url:url+"metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,
		    method: "GET",
		    }).then(function(response){ return  response})
    }

	this.getDatapodByRelation=function(uuid,type){
		return $http({
		   url:url+"metadata/getDatapodByRelation?action=view&relationUuid="+uuid+"&type="+type,
	       method: "GET",
		    }).then(function(response){ return  response})
    }

	this.getByUuid=function(uuid,type,sessionid){
		return $http({
			url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
			method: "GET",
	        }).then(function(response){ return  response})
	}

	this.excutionDag=function(data){
		return $http({
            url:url+"dag",
            headers: {
                'Accept':'*/*',
                'content-Type' : "application/json",
            },
            method:"POST",
            data:JSON.stringify(data),
        }).success(function(response){return response})
    }

	this.executeMap=function(uuid,version){
		return $http({
            url:url+"map/executeMap?action=execute&uuid="+uuid+"&version="+version,
            method:"POST",
        }).success(function(response){return response})
    }

    this.metaSubmit=function(data,type){
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
   this.saveAs=function(uuid,version,type){
	    var deferred = $q.defer();
	    MetadataFactory.findSaveAs(uuid,version,type).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
    	    deferred.resolve({
              data:response
           });
        }
       return deferred.promise;
    }
    this.getFormulaByType=function(uuid,type){
	    var deferred = $q.defer();
	    MetadataFactory.findFormulaByType(uuid,type).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
    	    deferred.resolve({
            data:response
            });
        }
        return deferred.promise;
    }

    this.getAllLatest=function(type) {
        var deferred = $q.defer();
        MetadataFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
            var data={};
            data.options=[];
            var defaultoption={};
            response.sort(sortFactory.sortByProperty("name"));
            defaultoption.name=response[0].name;
            defaultoption.uuid=response[0].uuid;
            data.defaultoption=defaultoption;
            for(var i=0;i<response.length;i++){
               var datajosn={}
               datajosn.name=response[i].name;
               datajosn.uuid=response[i].uuid;
               data.options[i]=datajosn
            }
            deferred.resolve({
                data:data
            })
        }
        return deferred.promise;
    }

    this.getAllAttributeBySource=function(uuid,type){
	    var deferred = $q.defer();
	    if(type == "relation"){
		    MetadataFactory.findDatapodByRelation(uuid,type).then(function(response){onSuccess(response.data)});
	        var onSuccess=function(response){
	            var attributes=[];
			   	for(var j=0;j<response.length;j++){
				    for(var i=0;i<response[j].attributes.length;i++){
						var attributedetail={};
						attributedetail.uuid=response[j].uuid;
						attributedetail.datapodname=response[j].name;
						attributedetail.name=response[j].attributes[i].name;
						attributedetail.dname=response[j].name+"."+response[j].attributes[i].name;
						attributedetail.attributeId=response[j].attributes[i].attributeId
						/* attributedetail.type=response[j].attributes[i].type;
						attributedetail.desc=response[j].attributes[i].desc;*/
						attributes.push(attributedetail)
				    }
			   	}
			   	//console.log(JSON.stringify(attributes))
	        	deferred.resolve({
	                data:attributes
	            })
	        }
	    }
	    if(type == "dataset"){
		    MetadataFactory.findAttributesByDataset(uuid).then(function(response){onSuccess(response.data)});
	        var onSuccess=function(response){
	        	var attributes=[];
				for(var j=0;j<response.length;j++){
				    var attributedetail={};
					attributedetail.uuid=response[j].ref.uuid;
					attributedetail.datapodname=response[j].ref.name;
				    attributedetail.name=response[j].attrName;
				    attributedetail.attributeId=response[j].attrId;
				    attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
				    attributes.push(attributedetail)
				}
			   	//console.log(JSON.stringify(attributes))
	        	deferred.resolve({
	                data:attributes
	            })
	        }
	    }
	    if(type == "datapod"){
		// alert(type)
		    MetadataFactory.findAttributesByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
	        var onSuccess=function(response){
	            var attributes=[];
				for(var j=0;j<response.length;j++){
				    var attributedetail={};
				    attributedetail.uuid=response[j].ref.uuid;
				    attributedetail.datapodname=response[j].ref.name;
				    attributedetail.name=response[j].attrName;
				    attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
					attributedetail.attributeId=response[j].attrId;
					attributes.push(attributedetail)
			    }
			   	//console.log(JSON.stringify(attributes))
	        	deferred.resolve({
	                data:attributes
	            })
	        }
	    }
        return deferred.promise;
    }

});
