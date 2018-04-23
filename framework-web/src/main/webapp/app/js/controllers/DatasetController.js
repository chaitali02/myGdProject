  /**
   **/
  MetadataModule=angular.module('MetadataModule');
  /* Start MetadataDatasetController*/
  MetadataModule.controller('MetadataDatasetController',function(dagMetaDataService,$rootScope,$state,$scope,$stateParams,$cookieStore,$timeout,$filter,MetadataSerivce,MetadataDatasetSerivce,$sessionStorage,privilegeSvc){
    if($stateParams.mode =='true'){
  	$scope.isEdit=false;
  	$scope.isversionEnable=false;
  	$scope.isAdd=false;
  	}
  	else if($stateParams.mode =='false'){
  	$scope.isEdit=true;
  	$scope.isversionEnable=true;
  	$scope.isAdd=false;
  	}
  	else{
  	$scope.isAdd=true;
  	}
  	$scope.mode="false";
  	$scope.dataLoading=false;
  	$scope.iSSubmitEnable=false;
  	$scope.datasetversion={};
  	$scope.datasetversion.versions=[]
  	$scope.showForm=true;
    $scope.data=null;
    $scope.showgraph=false
  	$scope.showGraphDiv=false
  	$scope.graphDataStatus=false
  	$scope.logicalOperator = [" ","OR", "AND"];
    $scope.SourceTypes=["datapod","relation"]
    $scope.operator = ["=", "<", ">", "<=", ">=", "BETWEEN"];
    $scope.isSubmitEnable=true;
  	$scope.attributeTableArray=null;
  	$scope.datsetsampledata=null;
  	$scope.isShowSimpleData=false;
    $scope.isDependencyShow=false;
		$scope.isSimpleRecord=false;
    $scope.privileges = [];
		$scope.privileges = privilegeSvc.privileges['dataset'] || [];
		$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
    $scope.$on('privilegesUpdated',function (e,data) {
      $scope.privileges = privilegeSvc.privileges['dataset'] || [];
      $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
    });
		
		/*Start showPage*/
    $scope.showPage=function(){
      $scope.isShowSimpleData=false
      $scope.showForm=true;
      $scope.showGraphDiv=false
		}/*End showPage*/
		
    $scope.enableEdit=function (uuid,version) {
      $scope.showPage()
      $state.go('metaListdataset', {
        id: uuid,
        version: version,
        mode:'false'
      });
    }
    $scope.showView=function (uuid,version) {
			if(!$scope.isEdit){
				$scope.showPage()
				$state.go('metaListdataset', {
					id: uuid,
					version: version,
					mode:'true'
				});
		  }
    }
    var notify = {
      type: 'success',
      title: 'Success',
      content: '',
      timeout: 3000 //time in ms
  };
    $scope.pagination={
      currentPage:1,
      pageSize:10,
      paginationPageSizes:[10, 25, 50, 75, 100],
      maxSize:5,
		}
		
    $scope.gridOptions = dagMetaDataService.gridOptionsDefault;
    $scope.gridOptions={
      rowHeight: 40,
      enableGridMenu: true,
      useExternalPagination: true,
      exporterMenuPdf: true,
      exporterPdfOrientation: 'landscape',
      exporterPdfPageSize: 'A4',
      exporterPdfDefaultStyle: {fontSize: 9},
      exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    }
		$scope.gridOptions.columnDefs = [];
    $scope.gridOptions.onRegisterApi = function(gridApi){
      $scope.gridApi = gridApi;
      $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };

		$scope.getGridStyle = function() {
      var style = {
        'margin-top': '10px',
        'margin-bottom': '10px',
      }
      if ($scope.filteredRows && $scope.filteredRows.length >0) {
        style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 50) + 'px';
      }
      else{
        style['height']="100px";
      }
      return style;
    }

  	$scope.routeForFormula=function(data,index){
  		if(data.uuid == null){
  			$sessionStorage.index=index;
  			var jsondata={};
  			jsondata.type=$scope.selectSourceType;
  			jsondata.uuid=$scope.datasetRelation.defaultoption.uuid
  			$sessionStorage.dependon=jsondata;
  			$scope.jsonCode();
  			$state.go('metaListformula');
  		}
  	}
  	$scope.routeForExpression=function(data,index){
  		if(data.uuid == null){
  			$sessionStorage.index=index;
  			var jsondata={};
  			jsondata.type=$scope.selectSourceType;
  			jsondata.uuid=$scope.datasetRelation.defaultoption.uuid
  			$sessionStorage.dependon=jsondata;
  			$scope.jsonCode();
  			$state.go('metaListexpression');
  		}
    }
  	$scope.sourceAttributeTypes=[
      {"text":"string","caption":"string"},
  		{"text":"datapod","caption":"attribute"},
  		{"text":"expression","caption":"expression"},
  		{"text":"formula","caption":"formula"},
  		{"text":"function","caption":"function"}
    ];

  	$scope.defalutType=["Simple",'Formula','Expression'];
  	$scope.filterTableArray=[];
  	$scope.isSourceDatapoLost=true;
  	$scope.tags=null;
  	$scope.datasetHasChanged=true;
  	$scope.checkalljoin=false
  	$scope.isshowmodel=false;
  	$scope.$watch("isshowmodel",function(newvalue,oldvalue){
  		$scope.isshowmodel=newvalue
  		sessionStorage.isshowmodel=newvalue
  	})

    $scope.datasetFormChange=function(){
  		if($scope.mode == "true"){
  			$scope.datasetHasChanged=true;
  		}
  		else{
  			$scope.datasetHasChanged=false;
  		}
  	}

  	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
  	 // console.log(fromParams)
  		$sessionStorage.fromStateName=fromState.name
  		if(fromState.name !="matadata"){
  			$sessionStorage.fromParams=fromParams
  		}
  	});

  	$scope.showGraph=function(uuid,version){
  	   $scope.showForm=false;
    	 $scope.showGraphDiv=true;
  	   $scope.isShowSimpleData=false
  	}/*End ShowGraph*/



    $scope.showSampleTable=function(data){
      $scope.isShowSimpleData=true
      $scope.isDataInpogress=true
      $scope.tableclass="centercontent";
      $scope.showForm=false;
      $scope.showGraphDiv=false;
      $scope.spinner=true;
      MetadataDatasetSerivce.getDatasetSample(data).then(function(response){onSuccessGetDatasourceByType(response.data)},function(response){onError(response.data)})
  		var onSuccessGetDatasourceByType=function(response){
  			//console.log(JSON.stringify(response))
  			$scope.gridOptions.columnDefs=[];
  			$scope.isDataInpogress=false;
  			$scope.tableclass="";
        $scope.spinner=false;
        // if(response.data.length>0){
  			//   $scope.datamessage="";
  			// }
  			// else{
        //   $scope.tableclass="No Results Found";
        //   $scope.msgclass="noResult";
        //   $scope.isDataError=true;
  			// }

        for(var j=0;j<data.attributeInfo.length;j++){
          var attribute = {};
          attribute.name = data.attributeInfo[j].attrSourceName;
          attribute.displayName = data.attributeInfo[j].attrSourceName;
          attribute.width = attribute.displayName.split('').length + 2 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
          $scope.gridOptions.columnDefs.push(attribute)
        }

        //$scope.gridOptions.data = response;
        $scope.originalData = response;
        if($scope.originalData.length >0){
          $scope.getResults($scope.originalData);
        }
        $scope.spinner=false;
  		}
  		var onError=function(response){
  			$scope.isDataInpogress=true;
  			$scope.isDataError=true;
  			$scope.msgclass="errorMsg";
  			$scope.datamessage="Some Error Occurred";
        $scope.spinner=false;
  		}
    }
    $scope.refreshData = function() {
      var data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
        $scope.getResults(data)
    };
  	$scope.selectType=function(){
  		MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function(response){onSuccess(response.data)});
  		var onSuccess=function(response){
  			$scope. datasetRelation=response;
  			MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessGetDatapodByRelation(response.data)})
        var onSuccessGetDatapodByRelation=function(response){
  				$scope.sourcedatapodattribute=response;
  				$scope.lhsdatapodattributefilter=response;
  				MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  				var onSuccessFormula=function(response){
  					$scope.datasetLodeFormula=response
  			  }
        }
  		}
  	}
    $scope.selectOption=function(){
      MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessGetDatapodByRelation(response.data)})
  	  var onSuccessGetDatapodByRelation=function(response){
  			$scope.sourcedatapodattribute=response;
  			$scope.lhsdatapodattributefilter=response;
  			MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  			var onSuccessFormula=function(response){
  			  $scope.datasetLodeFormula=response
  			}
  		}
    }

  	if(typeof $stateParams.id != "undefined"){
      $scope.showactive="true"
  		$scope.mode=$stateParams.mode;
      $scope.isDependencyShow=true;
			$scope.isSimpleRecord=true;
  		MetadataDatasetSerivce.getAllVersionByUuid($stateParams.id,"dataset").then(function(response){onSuccessGetAllVersionByUuid(response.data)});
  		var onSuccessGetAllVersionByUuid=function(response){
  			for(var i=0;i< response.length;i++){
  		    var datasetversion={};
  		    datasetversion.version=response[i].version;
  		    $scope.datasetversion.versions[i]=datasetversion;
  		  }
  		}
  		if($sessionStorage.fromStateName !="metadata" && typeof $sessionStorage.datasetjosn !="undefined" && !$rootScope.previousState){
  			$scope.dataset=$sessionStorage.datasetjosn;
  			$scope.tags=$sessionStorage.datasetjosn.tags
  			$scope.selectSourceType=$sessionStorage.datasetjosn.dependsOn.ref.type
  			MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function(response){onSuccess(response.data)});
  			var onSuccess=function(response){
  				$scope.datasetRelation=response;
  				var defaultoption={};
  				defaultoption.type=$sessionStorage.datasetjosn.dependsOn.ref.type
  				defaultoption.uuid=$sessionStorage.datasetjosn.dependsOn.ref.uuid
  				var defaultversion={};
  				defaultversion.version=$sessionStorage.datasetjosn.version;
  				defaultversion.uuid=$sessionStorage.datasetjosn.uuid;
  				$scope.datasetversion.defaultVersion=defaultversion;
  				$scope.datasetRelation.defaultoption=defaultoption;
  				MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessGetDatapodByRelation(response.data)})
  				var onSuccessGetDatapodByRelation=function(response){
  				  $scope.sourcedatapodattribute=response;
  					$scope.lhsdatapodattributefilter=response;
  				}
  			}
  			MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
  			var onSuccessExpression=function(response){
  			     $scope.datasetLodeExpression=response
  			}
  			MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  			var onSuccessFormula=function(response){
  			  $scope.datasetLodeFormula=response
  			}
  			MetadataDatasetSerivce.getAllLatestFunction("function","N").then(function(response){onSuccessFuntion(response.data)});
  		  var onSuccessFuntion=function(response){
  		  	$scope.ruleLodeFunction=response
  		  }
  			$scope.filterTableArray=$sessionStorage.datasetjosn.filterTableArray;
  		  $scope.attributeTableArray=$sessionStorage.datasetjosn.attributeTableArray
  			MetadataDatasetSerivce.getOneById($sessionStorage.dependon.id,$sessionStorage.dependon.type).then(function(response){onSuccessGetOneById(response.data)});
  		  var onSuccessGetOneById=function(response){
  		  	if($sessionStorage.dependon.type =="formula"){
  		  		var sourceformula={};
  		  		sourceformula.uuid=response.uuid;
  		  		sourceformula.name=response.name;
  		  		$scope.attributeTableArray[$sessionStorage.index].sourceformula=sourceformula;
  		  	}
  		    else if($sessionStorage.dependon.type =="expression"){
  		  	  var sourceexpression={};
  		  		sourceexpression.uuid=response.uuid;
  		  		sourceexpression.name=response.name;
  		  		$scope.attributeTableArray[$sessionStorage.index].sourceexpression=sourceexpression;
  		  	}
  		  }
  		}
  	  else{
        MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($stateParams.id,$stateParams.version,'datasetview').then(function(response){onSuccessResult(response.data)});
  		  var onSuccessResult=function(response){
  				$scope.dataset=response.dataset;
  				$scope.selectSourceType=response.dataset.dependsOn.ref.type
  				$scope.datasetCompare=response.dataset;
  				var defaultversion={};
  				defaultversion.version=response.dataset.version;
  				defaultversion.uuid=response.dataset.uuid;
  				$scope.datasetversion.defaultVersion=defaultversion;
  				$scope.tags=response.tags
  				MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function(response){onSuccess(response.data)});
  				var onSuccess=function(response){
  					//console.log(JSON.stringify(response))
  					$scope.datasetRelation=response;
  					var defaultoption={};
  					defaultoption.type=$scope.dataset.dependsOn.ref.type
  					defaultoption.uuid=$scope.dataset.dependsOn.ref.uuid
  					$scope.datasetRelation.defaultoption=defaultoption;
  				}
  				MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
  				var onSuccessExpression=function(response){
  				  $scope.datasetLodeExpression=response
  				}
  				MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  				var onSuccessFormula=function(response){
  				  $scope.datasetLodeFormula=response
  				}
  				MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid,$scope.dataset.dependsOn.ref.type).then(function(response){onSuccessGetDatapodByRelation(response.data)})
  			  var onSuccessGetDatapodByRelation=function(response){
  					$scope.sourcedatapodattribute=response;
  					$scope.lhsdatapodattributefilter=response;
  				}
  				MetadataDatasetSerivce.getAllLatestFunction("function","N").then(function(response){onSuccessFuntion(response.data)});
  			  var onSuccessFuntion=function(response){
  			  	$scope.ruleLodeFunction=response
  			  }
  				$scope.attributeTableArray=response.sourceAttributes
  				$scope.filterTableArray=response.filterInfo;
  				$scope.filterOrignal=$scope.original= angular.copy(response.filterInfo);
  			}//End onSuccessResult
  		}//End Inner Else
  	}//End If
  	else{
      $scope.showactive="false"
  		if(typeof $sessionStorage.fromStateName != "undefined" &&  $sessionStorage.fromStateName !="metadata" && $sessionStorage.fromStateName !="metaListdataset"){
  		  $scope.dataset=$sessionStorage.datasetjosn;
  			$scope.tags=$sessionStorage.datasetjosn.tags
  			$scope.selectSourceType=$sessionStorage.datasetjosn.dependsOn.ref.type
  			MetadataDatasetSerivce.getAllLatest($scope.selectSourceType).then(function(response){onSuccess(response.data)});
  			var onSuccess=function(response){
  				$scope.datasetRelation=response;
  				var defaultoption={};
  				defaultoption.type=$sessionStorage.datasetjosn.dependsOn.ref.type
  				defaultoption.uuid=$sessionStorage.datasetjosn.dependsOn.ref.uuid
  				$scope.datasetRelation.defaultoption=defaultoption;
  				MetadataDatasetSerivce.getAllAttributeBySource($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessGetDatapodByRelation(response.data)})
  				var onSuccessGetDatapodByRelation=function(response){
  					$scope.sourcedatapodattribute=response;
  					$scope.lhsdatapodattributefilter=response;
  				}
  			}
  			MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
  			var onSuccessExpression=function(response){
  			  $scope.datasetLodeExpression=response
  			}
  			MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  			var onSuccessFormula=function(response){
  			  $scope.datasetLodeFormula=response
  			}
  			MetadataDatasetSerivce.getAllLatestFunction("function","N").then(function(response){onSuccessFuntion(response.data)});
  		  var onSuccessFuntion=function(response){
  		  	$scope.ruleLodeFunction=response
  		  }
  		  $scope.filterTableArray=$sessionStorage.datasetjosn.filterTableArray;
  			$scope.attributeTableArray=$sessionStorage.datasetjosn.attributeTableArray
  		  MetadataDatasetSerivce.getOneById($sessionStorage.dependon.id,$sessionStorage.dependon.type).then(function(response){onSuccessGetOneById(response.data)});
  		  var onSuccessGetOneById=function(response){
  		  	if($sessionStorage.dependon.type =="formula"){
  		  		var sourceformula={};
  		  		sourceformula.uuid=response.uuid;
  		  		sourceformula.name=response.name;
  		  		$scope.attributeTableArray[$sessionStorage.index].sourceformula=sourceformula;
  		  	}
  		  	else if($sessionStorage.dependon.type =="expression"){
  		  		var sourceexpression={};
  		  		sourceexpression.uuid=response.uuid;
  		  		sourceexpression.name=response.name;
  		  		$scope.attributeTableArray[$sessionStorage.index].sourceexpression=sourceexpression;
  		  	}
  		  }
  			//delete $sessionStorage.datasetjosn;
  		}//End Inner If
  	}//End Else

  	 /* Start selectVersion*/
  	$scope.selectVersion=function(){
  		$scope.datasetRelation=null;
  		$scope.selectSourceType=null;
  		$scope.myform.$dirty=false;
  		$scope.datasetHasChanged=true;
  		MetadataDatasetSerivce.getDatasetDataByOneUuidandVersion($scope.datasetversion.defaultVersion.uuid,$scope.datasetversion.defaultVersion.version,'datasetview',$cookieStore.get('userdetail').sessionId).then(function (response) {onSuccessResult(response.data)});
  		var onSuccessResult=function(response){
  			$scope.dataset=response.dataset;
  			$scope.selectSourceType=response.dataset.dependsOn.ref.type
  			 $scope.datasetCompare=response.dataset;
  			 var defaultversion={};
  			 defaultversion.version=response.dataset.version;
  			 defaultversion.uuid=response.dataset.uuid;
  			 $scope.datasetversion.defaultVersion=defaultversion;
  			 $scope.tags=response.tags
  			 MetadataDatasetSerivce.getAllLatest(response.dataset.dependsOn.ref.type).then(function(response){onSuccess(response.data)});
  			 var onSuccess=function(response){
  				// console.log(JSON.stringify(response))
  				 $scope.datasetRelation=response;
  				 var defaultoption={};
  				 defaultoption.type=$scope.dataset.dependsOn.ref.type
  				 defaultoption.uuid=$scope.dataset.dependsOn.ref.uuid
  				 $scope.datasetRelation.defaultoption=defaultoption;
  			 }
  			 MetadataDatasetSerivce.getExpressionByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
  			 var onSuccessExpression=function(response){

  			     $scope.datasetLodeExpression=response
  			   }
  			 MetadataDatasetSerivce.getFormulaByType($scope.dataset.dependsOn.ref.uuid,$scope.selectSourceType).then(function(response){onSuccessFormula(response.data)});
  			 var onSuccessFormula=function(response){
  			     $scope.datasetLodeFormula=response
  			   }
  			 MetadataDatasetSerivce.getAllAttributeBySource($scope.dataset.dependsOn.ref.uuid,$scope.dataset.dependsOn.ref.type).then(function(response){onSuccessGetDatapodByRelation(response.data)})
  		     var onSuccessGetDatapodByRelation=function(response){
  				 $scope.sourcedatapodattribute=response;
  				 $scope.lhsdatapodattributefilter=response;
  			 }
  			 MetadataDatasetSerivce.getAllLatestFunction("function","N").then(function(response){onSuccessFuntion(response.data)});
  		  	   var onSuccessFuntion=function(response){

  		  	     $scope.ruleLodeFunction=response
  		  	   }
  			 $scope.attributeTableArray=response.sourceAttributes
  			 $scope.filterTableArray=response.filterInfo;
  			 $scope.filterOrignal=$scope.original= angular.copy(response.filterInfo);

  		 }//End onSuccessResult
  	  }/* End selectVersion*/


  	 $scope.checkAllFilterRow = function(){
  		 if (!$scope.selectedAllFitlerRow){
  	            $scope.selectedAllFitlerRow = true;
  	     }
  		 else {
  	          $scope.selectedAllFitlerRow = false;
  	     }
  		 angular.forEach($scope.filterTableArray, function(filter) {
  			 filter.selected = $scope.selectedAllFitlerRow;
  	        });
  	 }
      $scope.addRowFilter=function(){
      	var filertable={};
      	filertable.logicalOperator=$scope.logicalOperator[0];
      	filertable.lhsFilter=$scope.lhsdatapodattributefilter[0]
      	filertable.operator=$scope.operator[0]
        	$scope.filterTableArray.splice($scope.filterTableArray.length, 0,filertable);
      }
      $scope.removeRowFitler=function(){
      	var newDataList=[];
      	$scope.checkAll=false;
      	angular.forEach($scope.filterTableArray, function(selected){
               if(!selected.selected){
                   newDataList.push(selected);
               }
           });

      	if(newDataList.length >0){
  	    	newDataList[0].logicalOperator="";
  	    	}
      	$scope.filterTableArray =newDataList;
      }

      $scope.checkAllAttributeRow = function(){
  		 angular.forEach($scope.attributeTableArray, function(attribute) {
  			 attribute.selected = $scope.selectAllAttributeRow;
  	        });
  	 }

      $scope.addAttribute=function(){
     	   if($scope.attributeTableArray == null){
     			  $scope.attributeTableArray=[];
     	   }
     	   var len=$scope.attributeTableArray.length+1
     	   var attrivuteinfo={};

     	   attrivuteinfo.name="attribute"+len;
     	   attrivuteinfo.id=len-1;
     	   attrivuteinfo.sourceAttributeType=$scope.sourceAttributeTypes[0];
     	   attrivuteinfo.isSourceAtributeSimple=true;
     	   attrivuteinfo.isSourceAtributeDatapod=false;
     	   $scope.attributeTableArray.splice($scope.attributeTableArray.length, 0,attrivuteinfo);
      }

      $scope.removeAttribute=function(){
     	    var newDataList=[];
     	    $scope.selectAllAttributeRow=false
     	   	angular.forEach($scope.attributeTableArray, function(selected){
     	       if(!selected.selected){
     	                newDataList.push(selected);
     	       }
     	    });
     	   	$scope.attributeTableArray = newDataList;
        }

        $scope.onChangeSourceAttribute=function(type,index){
     	       if(type == "string"){
     	    	   $scope.attributeTableArray[index].isSourceAtributeSimple=true;
     	    	   $scope.attributeTableArray[index].isSourceAtributeDatapod=false;
     	    	   $scope.attributeTableArray[index].isSourceAtributeFormula=false;
     	    	   $scope.attributeTableArray[index].isSourceAtributeExpression=false;
     	    	   $scope.attributeTableArray[index].isSourceAtributeFunction=false;

     	    }
     	    else if(type == "datapod"){

     		     $scope.attributeTableArray[index].isSourceAtributeSimple=false;
     		     $scope.attributeTableArray[index].isSourceAtributeDatapod=true;
     		     $scope.attributeTableArray[index].isSourceAtributeFormula=false;
     		     $scope.attributeTableArray[index].isSourceAtributeExpression=false;
     		  $scope.attributeTableArray[index].isSourceAtributeFunction=false;
     	   }
      else if(type =="formula"){

     		   $scope.attributeTableArray[index].isSourceAtributeSimple=false;
     		   $scope.attributeTableArray[index].isSourceAtributeDatapod=false;
     		   $scope.attributeTableArray[index].isSourceAtributeFormula=true;
     		   $scope.attributeTableArray[index].isSourceAtributeExpression=false;
     		$scope.attributeTableArray[index].isSourceAtributeFunction=false;
     		   MetadataDatasetSerivce.getFormulaByType($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
     		   var onSuccessExpression=function(response){
     			  //alert(JSON.stringify(response))
     		     $scope.datasetLodeFormula=response
     		   }
     	   }
       else if(type =="expression"){

     		   $scope.attributeTableArray[index].isSourceAtributeSimple=false;
     		   $scope.attributeTableArray[index].isSourceAtributeDatapod=false;
     		   $scope.attributeTableArray[index].isSourceAtributeFormula=false;
     		   $scope.attributeTableArray[index].isSourceAtributeExpression=true;
     		   $scope.attributeTableArray[index].isSourceAtributeFunction=false;
     		   MetadataDatasetSerivce.getExpressionByType($scope.datasetRelation.defaultoption.uuid,$scope.selectSourceType).then(function(response){onSuccessExpression(response.data)});
     		   var onSuccessExpression=function(response){
     		     $scope.datasetLodeExpression=response
     		   }

     	   }
       else if(type =="function"){

    	   $scope.attributeTableArray[index].isSourceAtributeSimple=false;
    	   $scope.attributeTableArray[index].isSourceAtributeDatapod=false;
    	   $scope.attributeTableArray[index].isSourceAtributeFormula=false;
    	   $scope.attributeTableArray[index].isSourceAtributeExpression=false;
    	   $scope.attributeTableArray[index].isSourceAtributeFunction=true;
    	   MetadataDatasetSerivce.getAllLatestFunction("function","N").then(function(response){onSuccessExpression(response.data)});
    	   var onSuccessExpression=function(response){
    	     $scope.ruleLodeFunction=response
    	   }

      }

        }

        $scope.onChangeAttributeDatapod=function(data,index){

      	  if(data !=null){
      	  $scope.attributeTableArray[index].name=data.name
      	  }
        }
        $scope.onChangeFormula=function(data,index){
      	  $scope.attributeTableArray[index].name=data.name
        }

        $scope.onChangeExpression=function(data,index){
      	  $scope.attributeTableArray[index].name=data.name
        }

        $scope.jsonCode=function(){
      	var dataSetJson={}
        	dataSetJson.uuid=$scope.dataset.uuid
        	dataSetJson.name=$scope.dataset.name;
      	if($scope.datasetversion.versions.length >0){
      		dataSetJson.version=$scope.datasetversion.defaultVersion.version
      		var ref={};
          	ref.name=$scope.dataset.createdBy.ref.name
          	ref.uuid=$scope.dataset.createdBy.ref.uuid;
          	var createdBy={};
          	createdBy.ref=ref;
          	dataSetJson.createdBy=createdBy;
      	}

        	dataSetJson.desc=$scope.dataset.desc
        	dataSetJson.active=$scope.dataset.active;
      	dataSetJson.createdOn=$scope.dataset.createdOn

        	var tagArray=[];
      	if($scope.tags !=null){
          for(var counttag=0;counttag<$scope.tags.length;counttag++){
          	tagArray[counttag]=$scope.tags[counttag].text;

          }
      	 }
          dataSetJson.tags=tagArray;

      	//relationInfo
          var dependsOn={};
          var ref={};
          ref.type=$scope.selectSourceType
          ref.uuid=$scope.datasetRelation.defaultoption.uuid
          dependsOn.ref=ref;
          dataSetJson.dependsOn=dependsOn;
          dataSetJson.filterTableArray=$scope.filterTableArray;
          dataSetJson.attributeTableArray=$scope.attributeTableArray
        	$sessionStorage.datasetjosn=dataSetJson
        }
      $scope.submitDataset=function(){
      	delete $sessionStorage.datasetjosn;
      	delete $sessionStorage.index
      	delete $sessionStorage.dependon
      	$scope.isshowmodel=true;
      	$scope.dataLoading=true;
      	$scope.iSSubmitEnable=false;
      	$scope.datasetHasChanged=true;
      	$scope.myform.$dirty=false;
      	var dataSetJson={}
      	dataSetJson.uuid=$scope.dataset.uuid
      	dataSetJson.name=$scope.dataset.name;
      	dataSetJson.desc=$scope.dataset.desc
      	dataSetJson.active=$scope.dataset.active;
        dataSetJson.published=$scope.dataset.published;
      	dataSetJson.srcChg="y";

      	if($scope.datasetCompare == null){
  	    	dataSetJson.srcChg="y";
  	    	dataSetJson.sourceChg="y";
  	    	dataSetJson.filterChg="y";
     	    }
      	else{
      		dataSetJson.mapInfo=uuid=$scope.datasetCompare.mapInfo
      	}


      	var tagArray=[];
      	if($scope.tags !=null){
          for(var counttag=0;counttag<$scope.tags.length;counttag++){
          	tagArray[counttag]=$scope.tags[counttag].text;

          }
      	 }
          dataSetJson.tags=tagArray;

      	//relationInfo
          var dependsOn={};
          var ref={};
          ref.type=$scope.selectSourceType
          ref.uuid=$scope.datasetRelation.defaultoption.uuid
          dependsOn.ref=ref;
          dataSetJson.dependsOn=dependsOn;
           if( $scope.datasetCompare != null && $scope.datasetCompare.dependsOn.ref.uuid !=$scope.datasetRelation.defaultoption.uuid){
          	 dataSetJson.sourceChg="y";

           }
           else{

          	 dataSetJson.sourceChg="n";

           }


      	//filterInfo
      	var filterInfoArray=[];
      	var filter={}
      	if($scope.datasetCompare != null && $scope.datasetCompare.filter !=null ){
      	filter.uuid=$scope.datasetCompare.filter.uuid;
      	filter.name=$scope.datasetCompare.filter.name;
      	filter.version=$scope.datasetCompare.filter.version;
      	filter.createdBy=$scope.datasetCompare.filter.createdBy;
      	filter.createdOn=$scope.datasetCompare.filter.createdOn;
      	filter.active=$scope.datasetCompare.filter.active;
      	filter.tags=$scope.datasetCompare.filter.tags;
      	filter.desc=$scope.datasetCompare.filter.desc;
      	filter.dependsOn=$scope.datasetCompare.filter.dependsOn;
      	}
      	if($scope.filterTableArray.length >0 ){

      	for(var i=0;i<$scope.filterTableArray.length;i++){

      		 if($scope.datasetCompare != null &&  $scope.datasetCompare.filter !=null && $scope.datasetCompare.filter.filterInfo.length == $scope.filterTableArray.length){
      			 if($scope.datasetCompare.filter.filterInfo[i].operand[0].attributeId != $scope.filterTableArray[i].lhsFilter.attributeId
      					 || $scope.filterTableArray[i].logicalOperator !=$scope.datasetCompare.filter.filterInfo[i].logicalOperator
      					 || $scope.filterTableArray[i].filtervalue !=$scope.datasetCompare.filter.filterInfo[i].operand[1].value
      					 || $scope.filterTableArray[i].operator !=$scope.datasetCompare.filter.filterInfo[i].operator){

      				 dataSetJson.filterChg="y";

      			 }
      			 else{

      				 dataSetJson.filterChg="n";
      			 }

   	    	}
   	    	else{

   	        	dataSetJson.filterChg="y";
   	    	}
      		var filterInfo={};
      		var operand=[];
      		var operandfirst={};
      		var reffirst={};
      		var operandsecond={};
      		var refsecond={};
      		reffirst.type="datapod"

      		reffirst.uuid=$scope.filterTableArray[i].lhsFilter.uuid
      		operandfirst.ref=reffirst;
      		operandfirst.attributeId=$scope.filterTableArray[i].lhsFilter.attributeId
      	    operand[0]=operandfirst;
      		refsecond.type="simple";
      		operandsecond.ref=refsecond;
      		if(typeof $scope.filterTableArray[i].filtervalue == "undefined"){
      			operandsecond.value="";
      		}
      		else{

      			operandsecond.value=$scope.filterTableArray[i].filtervalue
      		}

      		operand[1]=operandsecond;
      		if (typeof $scope.filterTableArray[i].logicalOperator == "undefined") {
      			filterInfo.logicalOperator=""
      		}
      		else{
      			filterInfo.logicalOperator=$scope.filterTableArray[i].logicalOperator
      		}
      		filterInfo.operator=$scope.filterTableArray[i].operator
      		filterInfo.operand=operand;

      		filterInfoArray[i]=filterInfo;

      	}//End FilterInfo
      	filter.filterInfo=filterInfoArray;
   	    dataSetJson.filter=filter;
      	}
      	else{
      		 dataSetJson.filter=null;
      		 dataSetJson.filterChg="y";
      	}
      	 var sourceAttributesArray=[];
           for(var l=0;l<$scope.attributeTableArray.length;l++){
          	 attributeinfo={}
          	 attributeinfo.attrSourceId=l;
          	 attributeinfo.attrSourceName=$scope.attributeTableArray[l].name
          	 //attributeinfo.attributeDesc=$scope.attributeTableArray[l].name
          	 var ref={};
          	 var sourceAttr={};
          	 if($scope.attributeTableArray[l].sourceAttributeType.text == "string"){
          		 ref.type="simple";
          		 sourceAttr.ref=ref;
          		 sourceAttr.value=$scope.attributeTableArray[l].sourcesimple;
          		 attributeinfo.sourceAttr=sourceAttr;

          	 }
          	 else if($scope.attributeTableArray[l].sourceAttributeType.text == "datapod"){

          		 ref.type="datapod";
          		 ref.uuid=$scope.attributeTableArray[l].sourcedatapod.uuid;
          		 sourceAttr.ref=ref;
          		 sourceAttr.attrId=$scope.attributeTableArray[l].sourcedatapod.attributeId;
          		 attributeinfo.sourceAttr=sourceAttr;


          	 }
          	 else if($scope.attributeTableArray[l].sourceAttributeType.text == "expression"){

          		 ref.type="expression";
          		 ref.uuid=$scope.attributeTableArray[l].sourceexpression.uuid;
          		 sourceAttr.ref=ref;
          		 attributeinfo.sourceAttr=sourceAttr;

          	 }
              else if($scope.attributeTableArray[l].sourceAttributeType.text == "formula"){

          		 ref.type="formula";
          		 ref.uuid=$scope.attributeTableArray[l].sourceformula.uuid;
          		 sourceAttr.ref=ref;
          		 attributeinfo.sourceAttr=sourceAttr;

              }
              else if($scope.attributeTableArray[l].sourceAttributeType.text == "function"){

         		 ref.type="function";
         		 ref.uuid=$scope.attributeTableArray[l].sourcefunction.uuid;
         		 sourceAttr.ref=ref;
         		 attributeinfo.sourceAttr=sourceAttr;

             }

          	 sourceAttributesArray[l]=attributeinfo

           }
           dataSetJson.attributeInfo=sourceAttributesArray
      	console.log(JSON.stringify(dataSetJson))


      MetadataDatasetSerivce.submit(dataSetJson,'datasetview').then(function(response){onSuccess(response.data)},function(response){onError(response.data)});
          var onSuccess=function(response){
          	 $scope.dataLoading=false;
          	 $scope.iSSubmitEnable=false;
          	 $scope.changemodelvalue();
          // 	 if($scope.isshowmodel == "true"){
          // 	$('#datasetsave').modal({
       	// 	      backdrop: 'static',
       	// 	      keyboard: false
       	 //   });
          // 	}
          notify.type='success',
          notify.title= 'Success',
         notify.content='Dataset Saved Successfully'
         $scope.$emit('notify', notify);
         $scope.okdatasetsave();
        }
        var onError = function(response) {
  				 notify.type='error',
  				 notify.title= 'Error',
  				notify.content="Some Error Occurred"
  				$scope.$emit('notify', notify);
  			}

       return false;
      }

      $scope.changemodelvalue=function(){
    	  $scope.isshowmodel=sessionStorage.isshowmodel
       };

      $scope.okdatasetsave=function(){
  		   $('#datasetsave').css("dispaly","none");
  	       var hidemode="yes";
  		   if(hidemode == 'yes'){
  			   setTimeout(function(){  $state.go('metadata',{'type':'dataset'});},2000);

  		   }
  	}

      $scope.expandAll = function (expanded) {
          // $scope is required here, hence the injection above, even though we're using "controller as" syntax
          $scope.$broadcast('onExpandAll', {expanded: expanded});
      };

      $scope.selectPage = function(pageNo) {
        $scope.pagination.currentPage = pageNo;
      };
      $scope.onPerPageChange = function() {
          $scope.pagination.currentPage = 1;
        $scope.getResults($scope.originalData)
      }
      $scope.pageChanged = function() {
        $scope.getResults($scope.originalData)
      };
      $scope.getResults = function(params) {
        $scope.pagination.totalItems=params.length;
        if($scope.pagination.totalItems >0){
        $scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize))+1);
        }
        else{
          $scope.pagination.to=0;
        }
        if ($scope.pagination.totalItems < ($scope.pagination.pageSize*$scope.pagination.currentPage)) {
          $scope.pagination.from = $scope.pagination.totalItems;
        } else {
          $scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
        }
        var limit = ($scope.pagination.pageSize*$scope.pagination.currentPage);
        var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
         $scope.gridOptions.data=params.slice(offset,limit);
      }

   });/* End MetadataDatasetController*/


  	/*MetadataModule.directive('expand', function () {
  	    return {
  	        restrict: 'A',
  	        controller: ['$scope', function ($scope) {
  	            $scope.$on('onExpandAll', function (event, args) {
  	                $scope.expanded = args.expanded;
  	            });
  	        }]
  	    };
  	});*/
