/**
 *
 */
MetadataModule=angular.module('MetadataModule');
MetadataModule.factory('MetadataRelationFactory',function($http,$location){
    var factory={}
    factory.getGraphData=function(uuid,version,degree){
    	var url=$location.absUrl().split("app")[0]
		   return $http({
		                url:url+"graph/getGraphResults?action=view&uuid="+uuid+"&version="+version+"&degree="+degree,
		                method: "GET",
		          }).then(function(response){ return  response})
	   };
    factory.findLatestByUuid=function(uuid,type){
    	var url=$location.absUrl().split("app")[0]
  	    return $http({
  		        url:url+"common/getLatestByUuid?action=view&uuid="+uuid+"&type="+type,
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
				       url:url+"metadata/getOneById?action=view&id="+id+"&type="+type,
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
	   factory.findAllVersionByUuid=function(uuid,type){
	    	var url=$location.absUrl().split("app")[0]
	        return $http({
				    method: 'GET',
				    url:url+"common/getAllVersionByUuid?action=view&uuid="+uuid+"&type="+type,
				    }).
				    then(function (response,status,headers) {
			           return response;
			        })

	    }
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
	   factory.findDatapodByRelation=function(uuid,type,version) {
	        var url=$location.absUrl().split("app")[0]
	        return $http({
	               method: 'GET',
	               url:url+"metadata/getDatapodByRelation?action=view&relationUuid="+uuid+"&type="+type+"&version="+version,
	               }).
	               then(function (response,status,headers) {
	                  return response;
	               })
	    }
	    factory.findAttributesByDatapod=function(uuid,type) {
	        var url=$location.absUrl().split("app")[0]
	        return $http({
	               method: 'GET',
	               url:url+"metadata/getAttributesByDatapod?action=view&uuid="+uuid+"&type="+type,

	               }).
	               then(function (response,status,headers) {
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
   return factory;
});

MetadataModule.service('MetadataRelationSerivce',function($q,sortFactory,MetadataRelationFactory){
	 var that = this;
	 this.getAllAttributeBySource=function(uuid,type,version){
		 var deferred = $q.defer();
		 if(type == "relation"){
			 MetadataRelationFactory.findDatapodByRelation(uuid,"datapod",version).then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){
		        	 var allattributes=[];
		        	 var attributes=[];
				   	  for(var j=0;j<response.length;j++){
				   		   var attr=[]
							for(var i=0;i<response[j].attributes.length;i++){
							  var attributedetail={};
							  attributedetail.uuid=response[j].uuid;
							  attributedetail.datapodname=response[j].name;
							  attributedetail.name=response[j].attributes[i].name;
							  attributedetail.dname=response[j].name+"."+response[j].attributes[i].name;
							  attributedetail.attributeId=response[j].attributes[i].attributeId;
							  attributedetail.attrType=response[j].attributes[i].attrType;
							  allattributes.push(attributedetail)
							  attr.push(attributedetail)
							  }

							attributes[j]=attr;
				   	  }
				   	  var relationattribute={}
				   	  relationattribute.allattributes=allattributes;
				   	  relationattribute.attributes=attributes;

				   	  //console.log(JSON.stringify(attributes))
		        	  deferred.resolve({
		                     data:relationattribute
		                 })
		        }
		 }
		 if(type == "dataset"){
			 MetadataRelationFactory.findAttributesByDataset(uuid).then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){


		        		var attributes=[];
					   	  for(var j=0;j<response.length;j++){
								  var attributedetail={};
								  attributedetail.uuid=response[j].ref.uuid;
								  attributedetail.datapodname=response[j].ref.name;
								  attributedetail.name=response[j].attrName;
								  attributedetail.attributeId=response[j].attrId;
								  attributedetail.attrType=response[j].attrType;
								  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
								  attributes.push(attributedetail)
								}

		        	  deferred.resolve({
		                     data:attributes
		                 })
		        }


		 }
		 if(type == "datapod"){
			 MetadataRelationFactory.findAttributesByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
		        var onSuccess=function(response){
		        		var attributes=[];
					   for(var j=0;j<response.length;j++){
								  var attributedetail={};
								  attributedetail.uuid=response[j].ref.uuid;
								  attributedetail.datapodname=response[j].ref.name;
								  attributedetail.name=response[j].attrName;
								  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
								  attributedetail.attributeId=response[j].attrId;
								  attributedetail.attrType=response[j].attrType;
								  attributes.push(attributedetail)
								}

		        	  deferred.resolve({
		                     data:attributes
		                 })
		        }

		 }

	        return deferred.promise;
	}
	 this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataRelationFactory.getGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	    }
	 this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		     var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	    }
	 this.getAllAttributesByDatapod=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findAttributesByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
		    var onSuccess=function(response){


		   	//console.log(JSON.stringify(response))
		      deferred.resolve({
		                  data:response
		              })
		     }

		  return deferred.promise;
	    }
	 this.getAttributesByDatapod=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findAttributesByDatapod(uuid,type).then(function(response){onSuccess(response.data)});
		    var onSuccess=function(response){
		   	var attributes=[];
		   	for(var j=0;j<response.length;j++){
				  var attributedetail={};
				  attributedetail.uuid=response[j].ref.uuid;
				  attributedetail.datapodname=response[j].ref.name;
				  attributedetail.name=response[j].attrName;
				  attributedetail.dname=response[j].ref.name+"."+response[j].attrName;
				  attributedetail.attributeId=response[j].attrId;
				  attributedetail.attrType=response[j].attrType;
				  attributes.push(attributedetail)
				}
		   	//console.log(JSON.stringify(attributes))
		      deferred.resolve({
		                  data:attributes
		              })
		     }

		  return deferred.promise;
	    }
	this.getAllVersionByUuid=function(uuid,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findAllVersionByUuid(uuid,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getLatestByUuid=function(id,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findLatestByUuid(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		   var relationjson={};
		   relationjson.relationdata=response;
		   var relationInfoArray=[];
		   for(var i=0;i<response.relationInfo.length;i++){
			    var relationInfo={};
				if(response.relationInfo[i].joinType == ""){
						  relationInfo.relationJoinType="EQUI JOIN"
				}
				else{
			        relationInfo.relationJoinType=response.relationInfo[i].joinType;
				}

				var ref={};
	            ref.name=response.relationInfo[i].join.ref.name;
	            ref.uuid=response.relationInfo[i].join.ref.uuid;
			    relationInfo.join=ref;
			   // joinarray.push(ref);
				//relationInfo.joinarray=joinarray;
				var joinKeyArray=[];
				for(var l=0;l<response.relationInfo[i].joinKey.length;l++){
					var joinjson={};
					var lhsoperand={};
				    var rhsoperand={};
				    joinjson.logicalOperator=response.relationInfo[i].joinKey[l].logicalOperator
				    joinjson.relationOperator=response.relationInfo[i].joinKey[l].operator
					lhsoperand.uuid=response.relationInfo[i].joinKey[l].operand[0].ref.uuid;
				    lhsoperand.name=response.relationInfo[i].joinKey[l].operand[0].ref.name;
					lhsoperand.attributeId=response.relationInfo[i].joinKey[l].operand[0].attributeId;
					lhsoperand.attrType=response.relationInfo[i].joinKey[l].operand[0].attrType;
					lhsoperand.attributeName=response.relationInfo[i].joinKey[l].operand[0].attributeName;
					rhsoperand.uuid=response.relationInfo[i].joinKey[l].operand[1].ref.uuid;
					rhsoperand.name=response.relationInfo[i].joinKey[l].operand[1].ref.name;
					rhsoperand.attributeId=response.relationInfo[i].joinKey[l].operand[1].attributeId;
					rhsoperand.attrType=response.relationInfo[i].joinKey[l].operand[1].attrType;
					rhsoperand.attributeName=response.relationInfo[i].joinKey[l].operand[1].attributeName;
					joinjson.lhsoperand=lhsoperand
					joinjson.rhsoperand=rhsoperand
					joinKeyArray.push(joinjson);
					relationInfo.joinKey=joinKeyArray;
				}
			    relationInfoArray.push(relationInfo);

			}

		    relationjson.relationInfo=relationInfoArray
			//console.log(JSON.stringify(relationInfoArray))
		    deferred.resolve({
		                  data:relationjson
		           })
		    }
		    return deferred.promise;
		}
	this.getOneById=function(id,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findOneById(id,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){

		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
		}
	this.getGraphData=function(uuid,version,degree){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findGraphData(uuid,version,degree).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
		      deferred.resolve({
		                  data:response
		              })
		     }
		  return deferred.promise;
	   };
	this.getOneByUuidAndVersion=function(uuid,version,type){
		   var deferred=$q.defer();
		   MetadataRelationFactory.findOneByUuidAndVersion(uuid,version,type).then(function(response){onSuccess(response.data)});
		   var onSuccess=function(response){
			   var relationjson={};
			   relationjson.relationdata=response;
			   var relationInfoArray=[];
			   for(var i=0;i<response.relationInfo.length;i++){
				    var relationInfo={};
					if(response.relationInfo[i].joinType == ""){
							  relationInfo.relationJoinType="EQUI JOIN"
					}
					else{
				        relationInfo.relationJoinType=response.relationInfo[i].joinType;
					}

					var ref={};
		            ref.name=response.relationInfo[i].join.ref.name;
		            ref.uuid=response.relationInfo[i].join.ref.uuid;
				    relationInfo.join=ref;
				   // joinarray.push(ref);
					//relationInfo.joinarray=joinarray;
					var joinKeyArray=[];
					for(var l=0;l<response.relationInfo[i].joinKey.length;l++){
						var joinjson={};
						var lhsoperand={};
					    var rhsoperand={};
					    joinjson.logicalOperator=response.relationInfo[i].joinKey[l].logicalOperator
					    joinjson.relationOperator=response.relationInfo[i].joinKey[l].operator
						lhsoperand.uuid=response.relationInfo[i].joinKey[l].operand[0].ref.uuid;
					    lhsoperand.name=response.relationInfo[i].joinKey[l].operand[0].ref.name;
						lhsoperand.attributeId=response.relationInfo[i].joinKey[l].operand[0].attributeId;
						lhsoperand.attrType=response.relationInfo[i].joinKey[l].operand[0].attributeType;
						lhsoperand.attributeName=response.relationInfo[i].joinKey[l].operand[0].attributeName;
						rhsoperand.uuid=response.relationInfo[i].joinKey[l].operand[1].ref.uuid;
						rhsoperand.name=response.relationInfo[i].joinKey[l].operand[1].ref.name;
						rhsoperand.attributeId=response.relationInfo[i].joinKey[l].operand[1].attributeId;
						rhsoperand.attrType=response.relationInfo[i].joinKey[l].operand[1].attributeType;
						rhsoperand.attributeName=response.relationInfo[i].joinKey[l].operand[1].attributeName;
						joinjson.lhsoperand=lhsoperand
						joinjson.rhsoperand=rhsoperand
						joinKeyArray.push(joinjson);
						relationInfo.joinKey=joinKeyArray;
					}
				    relationInfoArray.push(relationInfo);

				}

			    relationjson.relationInfo=relationInfoArray
				//console.log(JSON.stringify(relationInfoArray))
			    deferred.resolve({
			                  data:relationjson
			           })
			    }
		  return deferred.promise;
		}
	this.submit=function(data,type,upd_tag){
		   var deferred=$q.defer();
		   MetadataRelationFactory.submit(data,type,upd_tag).then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
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
	     MetadataRelationFactory.findAllLatest(type).then(function(response){onSuccess(response.data)});
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
});
