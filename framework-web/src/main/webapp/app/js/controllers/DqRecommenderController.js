DataQualityModule = angular.module('DataQualityModule');

DataQualityModule.controller("DqRecommenderSearchController", function ($state, $filter, $location, $http, dagMetaDataService, $rootScope, $scope , CommonService, RecommenderService) {
	
	$scope.searchForm = {};
	$scope.samplePercent=25;
	$scope.tz = localStorage.serverTz;
    var matches = $scope.tz.match(/\b(\w)/g);
    $scope.timezone = matches.join('');
    $scope.autoRefreshCounter = 05;
    $scope.autoRefreshResult = false;
	$scope.path = dagMetaDataService.statusDefs;
	
    var notify = {
        type: 'success',
        title: 'Success',
        content: '',
        timeout: 3000 //time in ms
	};
	
   
	$scope.getGridStyle = function () {
        var style = {
            'margin-top': '10px',
            'margin-bottom': '10px',
        }
        if ($scope.filteredRows && $scope.filteredRows.length > 0) {
            style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
        } else {
            style['height'] = "100px"
        }
        return style;
	}
	
    $scope.gridOptions = {};
    $scope.gridOptions = angular.copy(dagMetaDataService.gridOptionsResults);
    $scope.gridOptions.onRegisterApi = function (gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
	};
	$scope.gridOptions.columnDefs.splice($scope.gridOptions.columnDefs.length-1,1);
	$scope.gridOptions.columnDefs.push({
		displayName: 'Action',
		name: 'action',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		maxWidth:100,
		cellTemplate: [
		  '<div class="ui-grid-cell-contents">',
		  '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
		  '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
		  '    <i class="fa fa-angle-down"></i></button>',
		  '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
		  '    <li><a ng-click="grid.appScope.getExec(row.entity,\'view\')"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
		 // '    <li><a ng-click="grid.appScope.getDetail(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
		  '    </ul>',
		  '  </div>',
		  '</div>'
		].join('')
	  });

    $scope.refreshData = function () {
        $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);

    };

	$scope.updateStats = function () {
		CommonService.getMetaStats("dqrecExec").then(function (response) {
		if (response.data && response.data.length && response.data.length > 0) {
			$rootScope.metaStats["dqrecExec"] = response.data[0];
		}
		});
	}

  	$scope.updateStats();

    $scope.refresh = function () {
        $scope.searchForm.execname = "";
        $scope.allExecName = [];
        $scope.searchForm.username = "";
        $scope.searchForm.tags = [];
        $scope.searchForm.published = "";
        $scope.searchForm.active = "";
        $scope.searchForm.status = "";
        $scope.searchForm.startdate = null;
        $scope.searchForm.enddate = null;
        $scope.allStatus = [{
            "caption": "PENDING",
            "name": "PENDING"
        },
        {
            "caption": "RUNNING",
            "name": "RUNNING"
        },
        {
            "caption": "COMPLETED",
            "name": "COMPLETED"
        },
        {
            "caption": "KILLED",
            "name": "KILLED"
        },
        {
            "caption": "FAILED",
            "name": "FAILED"
        }
        ];
		$scope.getAllLatestExec();
		$scope.getAllLatestUser();
        $scope.getBaseEntityStatusByCriteria(true);
    };

    var myVar;
    $scope.autoRefreshOnChange = function () {
        if ($scope.autorefresh) {
            myVar = setInterval(function () {
                $scope.getBaseEntityStatusByCriteria(false);
            }, $scope.autoRefreshCounter + "000");
        }
        else {
            clearInterval(myVar);
        }
    }
    $scope.refreshList = function () {
        $scope.getBaseEntityStatusByCriteria(false);
	}
	
    $scope.$on('$destroy', function () {
        // Make sure that the interval is destroyed too
        clearInterval(myVar);
    });

    $scope.startDateOnSetTime = function () {
        $scope.$broadcast('start-date-changed');
    }

    $scope.endDateOnSetTime = function () {
        $scope.$broadcast('end-date-changed');
    }

    $scope.startDateBeforeRender = function ($dates) {
        if ($scope.searchForm.enddate) {
            var activeDate = moment($scope.searchForm.enddate);
            $dates.filter(function (date) {
                return date.localDateValue() >= activeDate.valueOf()
            }).forEach(function (date) {
                date.selectable = false;
            })
        }
    }

    $scope.endDateBeforeRender = function ($view, $dates) {
        if ($scope.searchForm.startdate) {
            var activeDate = moment($scope.searchForm.startdate).subtract(1, $view).add(1, 'minute');
            $dates.filter(function (date) {
                return date.localDateValue() <= activeDate.valueOf()
            }).forEach(function (date) {
                date.selectable = false;
            })
        }
    }
	
	$scope.getAllLatestExec = function () {
        CommonService.getAllLatest(dagMetaDataService.elementDefs["dqrecexec"].metaType).then(function (response) { onSuccessGetAllLatestExec(response.data) });
        var onSuccessGetAllLatestExec = function (response) {
			$scope.allExecName = response;
			var uniqueNames=[];
			if(response && response.length >0){
			var uniqueNames= $.unique(response.map(function (d) {
				return d.name;}));
				$scope.allExecName=uniqueNames;
			}
		}		
	}
	
	$scope.getAllLatestExec();

	$scope.getAllLatestUser = function () {
        CommonService.getAllLatest(dagMetaDataService.elementDefs["user"].metaType).then(function (response) { onSuccessGetAllLatestExec(response.data) });
        var onSuccessGetAllLatestExec = function (response) {
            $scope.allUser = response;
        }
	}
	
	$scope.getAllLatestUser();
    

    $scope.getBaseEntityStatusByCriteria = function () {
        var startdate = ""
        if ($scope.searchForm.startdate != null) {
            startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
            startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
            enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
            enddate = enddate + " UTC";
        }

        var tags = [];
        if ($scope.searchForm.tags) {
            for (i = 0; i < $scope.searchForm.tags.length; i++) {
                tags[i] = $scope.searchForm.tags[i].text;
            }
        }
        tags = tags.toString();
		CommonService.getBaseEntityStatusByCriteria(dagMetaDataService.elementDefs["dqrecexec"].execType, $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '', $scope.searchForm.published || '', $scope.searchForm.status || '')
			.then(function (response) { onSuccess(response.data) }, function error() { $scope.loading = false;});
        var onSuccess = function (response) {
            $scope.gridOptions.data = response;
            $scope.originalData = response;
        }
	}
	

    $scope.getBaseEntityStatusByCriteria(false);
    $scope.refresh();

    $scope.searchCriteria = function () {
        $scope.getBaseEntityStatusByCriteria(false);
	}
	

    $scope.getExec = function (data) {
    var stateName = dagMetaDataService.elementDefs["dqrecexec"].resultState;
        if (stateName) {
            $state.go(stateName, {
                id: data.uuid,
                version: data.version,
                type:"derecexec",
                name: data.name
            });
        }
	}

    $scope.generateDqRecEXec=function(){
		$scope.getAllLatestDatapod();
		$scope.selectedDatapod=null;
		$scope.samplePercent=null;
		$scope.samplePercent=25;
		$('#searchAttr').modal({
			backdrop: 'static',
			keyboard: false
		  });  
	}

	$scope.getAllLatestDatapod = function () {
        CommonService.getAllLatest(dagMetaDataService.elementDefs["datapod"].metaType).then(function (response) { onSuccessGetAllLatestExec(response.data) });
        var onSuccessGetAllLatestExec = function (response) {
            $scope.allDatapod = response;
        }
	}
	
	$scope.genIntelligence=function(){
		$('#searchAttr').modal('hide');
		RecommenderService.genIntelligence("dqrecexec", $scope.selectedDatapod.uuid, $scope.selectedDatapod.version, $scope.samplePercent)
		.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			$scope.originalDataQuality=respone;
			$scope.getBaseEntityStatusByCriteria(false);
			$scope.getAllLatestExec();
		}
		var onError=function(response){
            $scope.dataErrorMsgDQ="Some error occurred";
		}
    }
	
    $scope.setStatus = function (row, status) {
        $scope.execDetail=row;
        $scope.execDetail.setStatus=status;
        $scope.msg =status;
        
        $('#confModal').modal({
          backdrop: 'static',
          keyboard: false
        });  
     
    }
    
    $scope.okSetStatus=function(){
        var api = false;
        var type = dagMetaDataService.elementDefs[$scope.searchForm.modelType].execType;
        var api = false;
        switch (type) {
            case 'trainExec':
                api = 'model/train';
                break;
            case 'predictExec':
                api = 'model/predict';
                break;
            case 'simulateExec':
                api = 'model/simulate';
                break;

        }
        if (!api) {
            return
        }
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = dagMetaDataService.elementDefs[$scope.searchForm.modelType].caption + " Killed Successfully"
        $scope.$emit('notify', notify);
        $('#confModal').modal('hide');
        var url = $location.absUrl().split("app")[0];
        //$http.put(url + '' + api + '/kill?uuid=' + row.uuid + '&version=' + row.version + '&type=' +type + '&status=' + status).then(function (response) {
        $http.put(url + 'model/setStatus?uuid=' + $scope.execDetail.uuid + '&version=' + $scope.execDetail.version + '&type=' + type + '&status=' + $scope.execDetail.setStatus).then(function (response) {

            console.log(response);
        });
    }

    $scope.restartExec = function (row, status) {
        $scope.execDetail=row;
        $scope.msg ="Restart";
        if(row.runMode =="BATCH"){
            notify.type = 'info',
            notify.title = 'Info',
            notify.content ="Please restart using batch module";
            $scope.$emit('notify', notify); 
            return false;
          }
        $('#confModal').modal({
          backdrop: 'static',
          keyboard: false
        });  
    }
    $scope.okRestart=function(){
        var type = dagMetaDataService.elementDefs[$scope.searchForm.modelType].execType;
        var api = false;
        switch (type) {
            case 'trainExec':
                api = 'model/train';
                break;
            case 'predictExec':
                api = 'model/predict';
                break;
            case 'simulateExec':
                api = 'model/simulate';
                break;

        }
        if (!api) {
            return
        }
        notify.type = 'success',
        notify.title = 'Success',
        notify.content = dagMetaDataService.elementDefs[$scope.searchForm.modelType].caption + " Restarted Successfully"
        $scope.$emit('notify', notify);
        $('#confModal').modal('hide');
        var url = $location.absUrl().split("app")[0];
        $http.get(url + '' + api + '/restart?uuid=' + $scope.execDetail.uuid + '&version=' + $scope.execDetail.version + '&type=' + type + '&action=execute').then(function (response) {
            //console.log(response);

        });
    }

    $scope.submitOk = function (action) {
        if (action == "Restart") {
          $scope.okRestart();
        }
        if(action == "Killed"){
            $scope.okSetStatus()
        }
    }
});

DataQualityModule.controller('DqRecommenderController', function (CommonService, $scope ,$stateParams , RecommenderService ,$filter) {
 
    var notify = {
        type: 'success',
        title: 'Success',
        content: '',
        timeout: 3000 //time in ms
    };
    $scope.gridOptionsDataQuality={
		rowHeight: 40,
		enableGridMenu: true,
		useExternalPagination: true,
		exporterMenuPdf: false,
	};
	$scope.isCreateDqSelected=false;
	$scope.isRuleCreateInprogess=false;
    $scope.selectedQualityAllRow=false;
	$scope.gridOptionsDataQuality.columnDefs = [
		{
			name: "selected",
			maxWidth: 40,
			visible: true,
			headerCellTemplate:'<div class="ui-grid-cell-contents" style="padding-top:9px;"><input  type="checkbox" style="width: 30px;height:16px;" ng-model="grid.appScope.selectedQualityAllRow" ng-change="grid.appScope.OnSelectQualityAllRow()"/></div',
			cellTemplate:'<div class="ui-grid-cell-contents" style="padding-top:2px;padding-left:4px;"><input type="checkbox" style="width:20px;height:16px;" ng-model="row.entity.selected" ng-change="grid.appScope.onSelectQualityRow()"/></div>'
		},
		{
			name: 'attributeNameValue',
			width: '20%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Attribute Name',
			headerCellClass: 'text-center'
		},
		{
			name: 'checkType',
			width: '20%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Check Type',
			headerCellClass: 'text-center'
		},
		{
			name: 'checkValueName',
			width: '20%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Check Value',
			headerCellClass: 'text-center',
			CellClass: 'text-center'
		},
		{
			name: 'sampleScore',
			width: '20%',
			enableCellEdit: false,
			visible: true,
			displayName: 'Sample Score',
			headerCellClass: 'text-center'
		},
	];
	
	$scope.filteredRowsDataQuality = [];
	$scope.gridOptionsDataQuality.onRegisterApi = function (gridApi) {
		$scope.gridApiDataQuality = gridApi;
		$scope.filteredRowsDataQuality = $scope.gridApiDataQuality.core.getVisibleRows($scope.gridApiDataQuality.grid);
	};

	$scope.getGridStyleDataQuality = function () {
		var style = {
			'margin-top': '10px',
			'margin-bottom': '10px',
			'width':'100%;'
		}
		if ($scope.filteredRowsDataQuality && $scope.filteredRowsDataQuality.length > 0) {
			style['height'] = (($scope.filteredRowsDataQuality.length < 10 ? $scope.filteredRowsDataQuality.length * 40 : 400) + 50) + 'px';
		}
		else {
			style['height'] = "100px";
	    }
		return style;
	}
   
    $scope.$on('$destroy', function () {
        // Make sure that the interval is destroyed too
    });
    

    
    $scope.refreshDataQuality=function(searchtext){
        $scope.gridOptionsDataQuality.data = $filter('filter')($scope.originalDataQuality,searchtext, undefined);
	}
	


	if (typeof $stateParams.id != "undefined") {
		$scope.isEditInprogess=true;
		$scope.isEditVeiwError=false;
		CommonService.getOneByUuidAndVersion($stateParams.id, $stateParams.version, "dqrecexec")
			.then(function (response) { onSuccesGetOneByUuidAndVersion(response.data)},function (response) { onError(response.data)});
		var onSuccesGetOneByUuidAndVersion = function (response) {
			$scope.isEditInprogess=false;
			$scope.exeecData = response;
			$scope.gridOptionsDataQuality.data=response.intelligenceResult;
			var intelligenceResultArray=[];
			if(response && response.intelligenceResult && response.intelligenceResult.length){
				for(var i=0;i<response.intelligenceResult.length;i++){
					var intelligenceResult={};
					intelligenceResult.attributeName=response.intelligenceResult[i].attributeName;
					intelligenceResult.attributeNameValue="-NA-";
					if(response.intelligenceResult[i].attributeName !=null){
						intelligenceResult.attributeNameValue=response.intelligenceResult[i].attributeName.attrName;
					}
					intelligenceResult.checkType=response.intelligenceResult[i].checkType;
					intelligenceResult.checkValue=response.intelligenceResult[i].checkValue;
					if (response.intelligenceResult[i].checkValue
							&& response.intelligenceResult[i].checkValue.length > 0) {
						if (response.intelligenceResult[i].checkValue.length == 1) {
							if (response.intelligenceResult[i].checkValue[0].ref.type == "simple") {
								intelligenceResult.checkValueName = response.intelligenceResult[i].checkValue[0].value;
							} else {
								intelligenceResult.checkValueName = response.intelligenceResult[i].checkValue[0].ref.name;
							}
						} else {
							intelligenceResult.checkValueName = "["
							for (var j = 0; j < response.intelligenceResult[i].checkValue.length; j++) {
								if (response.intelligenceResult[i].checkValue[j].ref.type == "simple") {
									intelligenceResult.checkValueName += response.intelligenceResult[i].checkValue[j].value
									if(j<response.intelligenceResult[i].checkValue.length-1)
										intelligenceResult.checkValueName +=" , "

								} else {
									intelligenceResult.checkValueName = response.intelligenceResult[i].checkValue[j].ref.name;
									if(j<response.intelligenceResult[i].checkValue.length-1)
										intelligenceResult.checkValueName +=" , "
								}
							}
							intelligenceResult.checkValueName += "]";

						}
					}
					
					intelligenceResult.checkValue=response.intelligenceResult[i].checkValue;
					intelligenceResult.sampleScore=response.intelligenceResult[i].sampleScore;
                    intelligenceResultArray[i]=intelligenceResult;
				}
				$scope.gridOptionsDataQuality.data=intelligenceResultArray;
			}
		};
		var onError=function(){
			$scope.isEditInprogess=false;
			$scope.isEditVeiwError=true;
		}
	}
   
    
	$scope.OnSelectQualityAllRow = function() {
		angular.forEach($scope.gridOptionsDataQuality.data, function(source) {
		  source.selected = $scope.selectedQualityAllRow;
		});
		if($scope.selectedQualityAllRow){
			$scope.isCreateDqSelected=true
		}else{
			$scope.isCreateDqSelected=false;
		}
    }
    
	$scope.onSelectQualityRow=function(){
		if($scope.getSelectedRow().length > 0){
			$scope.isCreateDqSelected=true
		}
		else{
			$scope.isCreateDqSelected=false;
		}
	}

	$scope.getSelectedRow= function() {
		var newDataList = [];
		angular.forEach($scope.gridOptionsDataQuality.data, function(selected) {
		  if (selected.selected) {
			newDataList.push(selected);
		  }
		});
		return newDataList;
	}

    
    $scope.ConfirmCreateRule=function(){
		if($scope.isCreateDqSelected ==false){
			return false;
		}
		$('#confCRModal').modal({
			backdrop: 'static',
			keyboard: false
		}); 
	}

	$scope.cancleCreateRule=function(){
		$scope.selectedQualityAllRow=false;
		$scope.OnSelectQualityAllRow ();
	}

    $scope.createRule=function(){
		$('#confCRModal').modal('hide');
		console.log($scope.getSelectedRow());
		var attrDqlist=$scope.getSelectedRow();
		var attrdqArray=[];
		if(attrDqlist && attrDqlist.length >0){
			for (var i=0;i<attrDqlist.length;i++){
				var attrDq={};
				attrDq.attributeName=attrDqlist[i].attributeName;
				attrDq.checkType=attrDqlist[i].checkType;
				attrDq.checkValue=attrDqlist[i].checkValue;
				attrDq.sampleScore=attrDqlist[i].sampleScore;
				attrdqArray[i]=attrDq;
			}
			$scope.generateDq(attrdqArray);
		}
    }
    
	$scope.generateDq=function(data){
		$scope.isRuleCreateInprogess=true;
		RecommenderService.generateDq("dqrecexec", $scope.exeecData.uuid, $scope.exeecData.version, data)
		.then(function (response) { onSuccess(response.data) }, function (response) { onError(response.data) })
		var onSuccess = function (respone) {
			notify.type = 'success',
			notify.title = 'Success',
			notify.content = 'Rule Create Successfully'
			$scope.$emit('notify', notify);
			$scope.selectedQualityAllRow=false;
			$scope.OnSelectQualityAllRow ();
			$scope.isRuleCreateInprogess=false;
		}
		var onError=function(response){
			notify.type = 'error',
			notify.title = 'Error',
			notify.content = "Some Error Occurred"
			$scope.$emit('notify', notify);
			$scope.isRuleCreateInprogess=false;
		}
	}
})
