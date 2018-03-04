/**
 *
 */

(function(){

	var DatapodModule= angular.module('DatapodModule');
	DatapodModule.factory('jqueryAjaxCallFactory',function($location){
		var base_url=$location.absUrl().split("app")[0];
		var jqueryAjaxCallServiceFactory={};
		jqueryAjaxCallServiceFactory.data;
		jqueryAjaxCallServiceFactory.uploadStatus=false;
		jqueryAjaxCallServiceFactory.getCall=function(url,sessionId,data){
		 url=base_url+url
		  $.ajax({
				  url: url,
				  type: "POST",
				  contentType:false,
				  async: false,
				  data : data,
				  processData:false,
				  beforeSend: function(xhr){xhr.setRequestHeader('sessionId', sessionId);},
				  success : function(response){

					  	jqueryAjaxCallServiceFactory.data=response
					  	return jqueryAjaxCallServiceFactory.data




				  }
				});
		    	return jqueryAjaxCallServiceFactory.data;
		    }
		jqueryAjaxCallServiceFactory.postCall=function(url,sessionId){
			 url=base_url+url
			$.ajax({
				  url: url,
				  type: "GET",
				  contentType: "application/json; charset=utf-8",
				  beforeSend: function(xhr){xhr.setRequestHeader('sessionId',sessionId);},
				  async: false,
				  processData:false,
				  success : function(response){
					   return jqueryAjaxCallServiceFactory.uploadStatus=true;

				  }
				});
			 return jqueryAjaxCallServiceFactory.uploadStatus
		}
		   return jqueryAjaxCallServiceFactory;
		});


	DatapodModule.service("SharedProperties", function (ajaxCallFactory) {
	    var _dataStoreResponse = null;


	    return {


	    	   getData1: function () {
		           var rowDataSet = [];
		           var headerColumns=['metaId','uuid','version','name','createdBy','createdOn']
		           for(var i=0;i<dataStoreResponse.length;i++){
		        	   var rowData ={};
		        	   rowData.metaid=dataStoreResponse[i].metaId.ref.uuid;
		        	   rowData.uuid=dataStoreResponse[i].uuid;
		        	   rowData.version=dataStoreResponse[i].version
		        	   rowData.name=dataStoreResponse[i].name;
		        	   rowData.createdBy=dataStoreResponse[i].createdBy.ref.name;
		        	   rowData.createdOn=dataStoreResponse[i].createdOn;
		        	   rowDataSet[i]=rowData;
		           }

		            return  rowDataSet;

		        },
	        getData: function () {
	           var rowDataSet = [];
	           var headerColumns=['metaId','numRows','uuid','version','name','createdBy','createdOn']
	           for(var i=0;i<dataStoreResponse.length;i++){
	        	   var rowData = [];

	        	   for(var j=0;j<headerColumns.length;j++){
	        		   var columnname=headerColumns[j]
	        		   if(columnname == "createdBy"){

	        			   rowData[j]=dataStoreResponse[i].createdBy.ref.name;
	        		   }
	        		   else if(columnname == "metaId"){

	        			   rowData[j]=dataStoreResponse[i].metaId.ref.uuid;
	        		   }
	        		   else{

	        			   rowData[j]=dataStoreResponse[i][columnname];
	        		   }
	        	   }
	        	   rowDataSet[i]=rowData;
	           }

	            return  rowDataSet;

	        },

	        getColumns: function () {

	        	return [{'sTitle':'datapodId',"bVisible": true},{'sTitle': 'Uuid'},{'sTitle': 'Version'},{'sTitle': 'Name'},{'sTitle': 'CreatedBy'},{'sTitle': 'CreatedOn'},{'sTitle': 'Action'}]
	        },
	        setUuid: function(datapoduuid) {

	            dataStoreResponse=JSON.parse(ajaxCallFactory.getCall("/metadata/getDataStoreByDatapod?datapodUUID="+datapoduuid+"&type=datastore","58424c11b1dfe314262b8e4e"));

	        }
	    }
	});


	DatapodModule.service("datapodService", function (ajaxCallFactory) {

	    var _datapodResponse=null;

	    return {

	        getDatapodColumns: function () {
	            var datapodColumns=[];
	            for(var i=0;i<_datapodResponse.attributes.length;i++){
	            	var datapodcolumn={};
	            	//datapodcolumn.sTitle=_datapodResponse.attributes[i].desc;
	            	//alert(_datapodResponse.attributes[i].dispName)
	            //	if(_datapodResponse.attributes[i].dispName == null){

	            		datapodcolumn.sTitle=_datapodResponse.attributes[i].desc;
	            	//}
	            	//else{

	            	//datapodcolumn.sTitle=_datapodResponse.attributes[i].dispName;
	            	//}
	            	datapodcolumn.sName=_datapodResponse.attributes[i].name;
	            	datapodColumns[i]=datapodcolumn
	            }
	            //alert(JSON.stringify(datapodColumns))
	        	return datapodColumns
	        },
	        setDatapodUuid: function(datapoduuid) {
	        	_datapodResponse=JSON.parse(ajaxCallFactory.getCall("/common/getLatestByUuid?uuid="+datapoduuid+"&type=datapod","58424c11b1dfe314262b8e4e"));

	        }
	    }
	});

	DatapodModule.service("datapodDetailService", function (ajaxCallFactory) {

	    var _datapodDetailResponse=null;
	    var _datapodColumn=null;

	    return {

	        getDatapodDetaildData: function () {
	        	var rowDataSet = [];
		           for(var i=0;i<_datapodDetailResponse.length;i++){
		        	   var rowData = [];

		        	   for(var j=0;j<_datapodColumn.length;j++){
		        		   var columnname=_datapodColumn[j].sName;


		        			   rowData[j]=_datapodDetailResponse[i][columnname];

		        	   }
		        	   rowDataSet[i]=rowData;
		           }

		         return  rowDataSet;

        },

        setDatapodDetatil: function(datastoreuuid,datastoreversion,datapodcolumns) {
	        	_datapodColumn=datapodcolumns
	        	_datapodDetailResponse=JSON.parse(ajaxCallFactory.getCall("/datapod?dataStoreUUID="+datastoreuuid+"&dataStoreVersion="+datastoreversion,"58424c11b1dfe314262b8e4e"));
	          // alert("dfdf"+JSON.stringify(_datapodDetailResponse))
	        }
	    }
	});

	DatapodModule.service('fileUpload', ['$http','jqueryAjaxCallFactory','$location', function ($http,jqueryAjaxCallFactory,$location) {
		var _fileUrl;

		 this.beforSave=function(){

			 return true;
		 }
	    this.saveFileToUrl = function(file, filename){
	        var fd = new FormData();
	        fd.append('file', file);
	        var urlUpload=jqueryAjaxCallFactory.getCall("/metadata/file?fileName="+filename,'58424c11b1dfe314262b8e4e',fd)
	        var uploadstatus=jqueryAjaxCallFactory.postCall('/metadata/registerFile?csvFileName='+urlUpload,'58424c11b1dfe314262b8e4e')

	      return uploadstatus;
	    }
	}]);



	DatapodModule.controller("datapodController",function($filter,$timeout,$scope,$stateParams,$rootScope,SharedProperties,datapodService,datapodDetailService,fileUpload,NgTableParams,datapodDetailSerivce,uuid2){
	     //alert($stateParams.id)
		var self=this;
		$scope.sortdetail=[];
		$scope.colcount=0;
		SharedProperties.setUuid($stateParams.id)
		$scope.data=SharedProperties.getData();
		$scope.data1=SharedProperties.getData1();
		$scope.columns=SharedProperties.getColumns();
		$scope.datapodDetaildColumn=[];
		$scope.datapodDetaildData=[];
		$scope.uplodbutton=true;
		$scope.fileUplodStatus=false;
		$scope.color='black'
		$scope.showprogress=false;
		$rootScope.$on("callDatapodController", function(data) {

			$scope.getDataStoreObject(data);
		});

		var getResult = function(response) {
			angular.forEach(response.data[0], function(value, key) {
				var attribute = {};
				if (key == "rownum") {
					attribute.visible = false
				} else {
					attribute.visible = true
				}
				attribute.name = key
				attribute.displayName = key
				attribute.width = key.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
				$scope.gridOptions.columnDefs.push(attribute)
			});
			$scope.gridOptions.data = response.data;
			$scope.originalData = response.data;
			$scope.testgrid = true;
			$scope.showprogress = false;
		}

		$scope.gridOptions = {
      useExternalPagination: true,
      enableSorting: true,
      useExternalSorting: true,
      enableFiltering: false,
      enableRowSelection: true,
      enableSelectAll: true,
      enableGridMenu: true,
      fastWatch: true,
      columnDefs: [],
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;
        $scope.gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
          if (sortColumns.length > 0) {
            $scope.searchRequestId(sortColumns);
          }
          /* if (sortColumns.length == 0) {
  				 paginationOptions.sort = null;
  			  }
          else {
  				 paginationOptions.sort = sortColumns[0].sort.direction;
  			  }*/
        });
      }
    };
    $scope.refreshData = function() {
      $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
    };


		$scope.getDataStoreObject=function(data){


		var iEl = angular.element( document.querySelector( '#DataTables_Table_'+data.count+'_wrapper' ) );
			  iEl.remove();
			//  alert(data)
			SharedProperties.setUuid($stateParams.id)
			$scope.data1=SharedProperties.getData1();
			$scope.data=SharedProperties.getData();
			$scope.datapodDetaildColumn=[];
			$scope.datapodDetaildData=[];

		}

	   $scope.getDataStopeDetail=function(data){

		   $scope.showprogress=true;
		  var dataStoreDetail=data.split(',')
		   datapodService.setDatapodUuid(dataStoreDetail[0])
		   if(datapodService.getDatapodColumns().length !=0){
			  // alert(JSON.stringify(datapodService.getDatapodColumns()))
		 /*  datapodDetailService.setDatapodDetatil(dataStoreDetail[2],dataStoreDetail[3],datapodService.getDatapodColumns());

		   $scope.$apply(function(){
			   $scope.datapodDetaildColumn=datapodService.getDatapodColumns();
			   });
		   $scope.$apply(function(){
			   $scope.datapodDetaildData=datapodDetailService.getDatapodDetaildData();
			   });*/


		   datapodDetailSerivce.getLatestByUuid(dataStoreDetail[0],"datapod").then(function(response){onSuccessGetLatestByUuid(response.data)});
		   var onSuccessGetLatestByUuid=function(response){
			   $scope.cols=[];
			   $scope.showprogress=false;
			   for(var i=0;i<response.attributes.length;i++){
				   var attribute={};
				   attribute.field=response.attributes[i].name;
				   if(response.attributes[i].dispName == null){
					   attribute.title=response.attributes[i].name;
           	       }
                  else{
                	  attribute.title=response.attributes[i].dispName;
                    }
				   //attribute.title=response.attributes[i].dispName;
				   attribute.show=true
				   attribute.sortable=response.attributes[i].name;
			       //var filter={};
				   //filter[response.attributes[i].name]="text"
				   //attribute.filter=filter
				  // attribute.getValue=$scope.renderedInput()
				   attribute.inputType==response.attributes[i].type
				   $scope.cols[i]=attribute
				  // console.log(JSON.stringify($scope.cols))
			   }
		   }

		   $scope.tableParams = new NgTableParams({page:1,count:10},
					 {
			        total:dataStoreDetail[1],
				    getData:function(params) {
				    	$scope.showprogress=true;
				    	var offset=(params.page() - 1) * params.count();
				    	//alert(offset)
				    	if(angular.equals(params.sorting(), {}) !=true){

				    		//alert(JSON.stringify(params.sorting()))
					    	var sortBy=Object.keys(params.sorting())
					    	var order=params.sorting()[sortBy];
					    	var requestId;
					    	if($scope.sortdetail.length == 0){
					    		//alert("if")
					    		var sortobj={};
					    		sortobj.uuid=uuid2.newuuid();
						    	sortobj.colname=sortBy[0];
						    	sortobj.order=order;
						    	sortobj.limit=params.count()
						    	$scope.sortdetail[$scope.colcount]=sortobj;
						    	$scope.colcount=$scope.colcount+1;
						    	requestId=sortobj.uuid;
						    	offset=0;
						    	$scope.tableParams.page(1);
						    	$scope.tableParams.reload();
					    	}
					    	else{
					    		var idpresent="N";
					    		//alert("else")
					    		for(var i=0;i<$scope.sortdetail.length;i++){

					    	        if($scope.sortdetail[i].colname == sortBy && $scope.sortdetail[i].order == order  &&$scope.sortdetail[i].limit == params.count()  ){
					    	        	requestId=$scope.sortdetail[i].uuid;
					    	        	idpresent="Y"
					    	        	break;
					    	        }
					    	    }
					    		if(idpresent == "N"){
					    			var sortobj={};
					    			sortobj.uuid=uuid2.newuuid();
						    		requestId=sortobj.uuid;
							    	sortobj.colname=sortBy[0];
							    	sortobj.order=order;
							    	sortobj.limit=params.count()
							    	$scope.sortdetail[$scope.colcount]=sortobj;
							    	$scope.colcount=$scope.colcount+1;
							    	offset=0;
							    	$scope.tableParams.page(1);
							    	$scope.tableParams.reload();

					    		}
					    	}

				    	}
				    	else{
				    		var sortBy=null
					    	var order=null;
				    		var requestId="";
				    	}
				    	console.log(JSON.stringify($scope.sortdetail))
				    	console.log(requestId)
		                return datapodDetailSerivce.getDatapodResults(dataStoreDetail[2],dataStoreDetail[3],offset,params.count(),sortBy,order,requestId).then(function(response){
				    	//   $scope.data =params.sorting() ? $filter('orderBy')(response.data, params.orderBy()) :response.data;
				    	  // $scope.data = params.filter() ? $filter('filter')($scope.data, params.filter()) : $scope.data;
				        	//params.total(50);
		                	//console.log(JSON.stringify(response.data))
		                	$scope.showprogress=false;
				    	    $scope.searchtext="";
				        	  return response.data //params.sorting() ? $filter('orderBy')(response.data, params.orderBy()) :response.data



				    	    });


				      }
				        });
		/*   datapodDetailSerivce.getDatapodResults(dataStoreDetail[1],dataStoreDetail[2]).then(function(response){onSuccessGetDatapodResults(response.data)});
		   var onSuccessGetDatapodResults=function(response){
			   $scope.tableParams = new NgTableParams({page:1,count:25}, {
				      dataset: response
				    });

		   }*/

		   }
	   }


	    $scope.uploadFile = function(){

	    	$scope.uplodbutton=true
	        var file = $scope.myFile;
	    	var iEl = angular.element( document.querySelector( '#csv_file' ) );
			var filename= iEl[0].files[0].name
			if(fileUpload.beforSave() == true){

				$scope.fileUplodStatus=true;
				$scope.showClass="fa fa-spinner fa-spin";
			}

			$timeout(function(){

				if(fileUpload.saveFileToUrl(file, filename)==true){
					$scope.showClass="fa fa-check";
					$scope.color='green'
					 $timeout(function(){
						 $scope.color='black'
						 $scope.fileUplodStatus=false
					      },2000);
				}

			}, 1000)

				//

	    };

	})






})();
