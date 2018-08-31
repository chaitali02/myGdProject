DatascienceModule = angular.module('DatascienceModule');

DatascienceModule.controller("ModelResultSearchController",function($state,$filter,$location,$http,$stateParams,dagMetaDataService, $rootScope, $scope, ModelService ,CommonService) {
    $scope.searchForm={};
    $scope.tz=localStorage.serverTz;
    var matches = $scope.tz.match(/\b(\w)/g);
    $scope.timezone=matches.join('');
    $scope.autoRefreshCounter=05;
    $scope.autoRefreshResult=false;
    $scope.path = dagMetaDataService.statusDefs
    $scope.allModelType=[{
        "index":"0",
        "name":"train",
        "caption":"Training"
    },
    {   "index":"1",
        "name":"predict",
        "caption":"Prediction"
    },
    {   "index":"2",
        "name":"simulate",
        "caption":"Simulation"
    }
    ];
    var notify = {
        type: 'success',
        title: 'Success',
        content: '',
        timeout: 3000 //time in ms
      };
    $scope.searchForm.modelType=$scope.allModelType[0].name;
    $scope.searchForm.modelTypeObj=$scope.allModelType[0]
    $scope.getGridStyle = function() {
        var style = {
          'margin-top': '10px',
          'margin-bottom': '10px',
        }
        if( $scope.filteredRows && $scope.filteredRows.length > 0) {
            style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
        }else {
            style['height'] = "100px"
        }
        return style;
    }
  
    $scope.gridOptions = dagMetaDataService.gridOptionsResults;
        $scope.gridOptions.onRegisterApi = function(gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };
    $scope.refreshData = function() {
        $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
        
    };
    $scope.refresh = function() {
        $scope.searchForm.execname = "";
        //$scope.searchForm.modelTypeObj=""
        $scope.allExecName=[];
        $scope.searchForm.username = "";
        $scope.searchForm.tags = [];
        $scope.searchForm.published = "";
        $scope.searchForm.active = "";
        $scope.searchForm.status = "";
        $scope.searchForm.startdate = null;
        $scope.searchForm.enddate = null;
        $scope.allStatus = [{
            "caption": "Not Started",
            "name": "NotStarted"
          },
          {
            "caption": "In Progress",
            "name": "InProgress"
          },
          {
            "caption": "Completed",
            "name": "Completed"
          },
          {
            "caption": "Killed",
            "name": "Killed"
          },
          {
            "caption": "Failed",
            "name": "Failed"
          }
        ];
        $scope.getAllLatest();
        $scope.getBaseEntityStatusByCriteria(true);
    };
    
    var myVar;
    $scope.autoRefreshOnChange=function () {
        if($scope.autorefresh){
            myVar = setInterval(function(){
                $scope.getBaseEntityStatusByCriteria(false);
            }, $scope.autoRefreshCounter+"000");
        }
        else{
            clearInterval(myVar);
        }
    }
    $scope.refreshList=function(){
        $scope.getBaseEntityStatusByCriteria(false);
    }
   $scope.$on('$destroy', function() {
    // Make sure that the interval is destroyed too
        clearInterval(myVar);
    });

    $scope.startDateOnSetTime =function() {
        $scope.$broadcast('start-date-changed');
    }

    $scope.endDateOnSetTime =function() {
        $scope.$broadcast('end-date-changed');
    }
   
    $scope.startDateBeforeRender=function($dates) { 
        if ($scope.searchForm.enddate) {
          var activeDate = moment($scope.searchForm.enddate);
          $dates.filter(function (date) {
            return date.localDateValue() >= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
    }

    $scope.endDateBeforeRender=function ($view, $dates) {
        if ($scope.searchForm.startdate) {
          var activeDate = moment($scope.searchForm.startdate).subtract(1, $view).add(1, 'minute');
          $dates.filter(function (date) {
            return date.localDateValue() <= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
    }
    $scope.getAllLatest=function(){
        CommonService.getAllLatest($scope.searchForm.modelType).then(function(response) { onSuccessGetAllLatestExec(response.data)});
        var onSuccessGetAllLatestExec = function(response) {
            $scope.allExecName = response;
        }
    }
    $scope.getAllLatest();
    $scope.onChangeModelType=function(type){
        $scope.searchForm.modelType=type
        $scope.getAllLatest();
        $scope.getBaseEntityStatusByCriteria(false);
        
    }
     
    $scope.getBaseEntityStatusByCriteria=function(){
        var startdate = ""
        if ($scope.searchForm.startdate != null) {
          startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
          startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
          enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
          enddate = enddate + " UTC";
        }

        var tags = [];
        if($scope.searchForm.tags){
        for (i = 0; i < $scope.searchForm.tags.length; i++) {
          tags[i] = $scope.searchForm.tags[i].text;
        }
        }
        tags = tags.toString();
        CommonService.getBaseEntityStatusByCriteria($scope.searchForm.modelType+"exec", $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '',$scope.searchForm.published || '', $scope.searchForm.status || '').then(function(response) {onSuccess(response.data)},function error() {
            $scope.loading = false;});
        var onSuccess = function(response) {
       // console.log(response);
        $scope.gridOptions.data = response;
        $scope.originalData = response;
        }
    }
    $scope.getBaseEntityStatusByCriteria(false);
    $scope.refresh();

    $scope.searchCriteria=function(){
        $scope.getBaseEntityStatusByCriteria(false);
    }
    $scope.getExec = function(data) {
     var stateName = dagMetaDataService.elementDefs[$scope.searchForm.modelType+"exec"].resultState;
     if (stateName) {
       $state.go(stateName, {
         id: data.uuid,
         version: data.version,
         type: ($scope.searchForm.modelType).toLowerCase(),
         name: data.name
       });
     }
    }
    $scope.setStatus = function (row, status) {
        var api = false;
        var type  =dagMetaDataService.elementDefs[ $scope.searchForm.modelType].execType;
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
        notify.content = dagMetaDataService.elementDefs[ $scope.searchForm.modelType].caption+" Killed Successfully"
        $scope.$emit('notify', notify);
    
        var url = $location.absUrl().split("app")[0];
        $http.put(url + '' + api + '/kill?uuid=' + row.uuid + '&version=' + row.version + '&type=' +type + '&status=' + status).then(function (response) {
          console.log(response);
        });
      }
    $scope.restartExec = function (row, status) {
        var type  =dagMetaDataService.elementDefs[ $scope.searchForm.modelType].execType;
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
        notify.content =dagMetaDataService.elementDefs[ $scope.searchForm.modelType].caption+" Restarted Successfully"
        $scope.$emit('notify', notify);
    
        var url = $location.absUrl().split("app")[0];
        $http.get(url + '' + api + '/restart?uuid=' + row.uuid + '&version=' + row.version + '&type=' + type + '&action=execute').then(function (response) {
          //console.log(response);
        });
    }
    

});


DatascienceModule.controller('ResultModelController', function($filter, $state, $location,$http,$stateParams,dagMetaDataService, $rootScope, $scope, ModelService,CF_DOWNLOAD) {
  //  $scope.toClipboard = ngClipboard.toClipboard;
    $scope.isShowPMML=false;
    $scope.caption = dagMetaDataService.elementDefs[$stateParams.type].caption;
    $scope.type=$stateParams.type
    $scope.autoRefreshCounterResult=05;
    $scope.autoRefreshResult=false;
    $scope.download={};
    $scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
    $scope.download.formates=CF_DOWNLOAD.formate;
    $scope.download.selectFormate=CF_DOWNLOAD.formate[0];
    $scope.download.maxrow=CF_DOWNLOAD.framework_download_maxrow;
    $scope.download.limit_to=CF_DOWNLOAD.limit_to; 
    $scope.pagination={
        currentPage:1,
        pageSize:10,
        paginationPageSizes:[10, 25, 50, 75, 100],
        maxSize:5,
    }

    $scope.getGridStyle = function() {
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

    $scope.gridOptions={
        rowHeight: 40,
        useExternalPagination: true,
        exporterMenuPdf: false,
        enableSorting: true,
        useExternalSorting: true,
        enableFiltering: false,
        enableRowSelection: true,
        enableSelectAll: true,
        enableGridMenu: true,
        fastWatch: true,
        columnDefs: [],
    };
    $scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };

    $scope.close=function(){
        $state.go('resultmodelmodel');
    }


    $scope.getAlgorithumByTrainExec=function(){
        ModelService.getAlgorithmByTrainExec($scope.modelDetail.uuid, $scope.modelDetail.version,'modelExec').then(function(response){ onSuccessGetModelResult(response.data)});
        var onSuccessGetModelResult = function(response) {
            $scope.isPMMLDownload=response.savePmml =="Y" ? false:true; 
        } 
    }
    
    $scope.getModelByTrainExec=function(){
        ModelService.getModelByTrainExec($scope.modelDetail.uuid, $scope.modelDetail.version,'modelExec').then(function(response){ onSuccessGetMdoelByTrainExec(response.data)});
        var onSuccessGetMdoelByTrainExec = function(response) {
            $scope.modelData=response; 
        } 
    }

    $scope.getTrainResult = function(data) {
        var uuid = data.uuid;
        var version = data.version;
        $scope.modelDetail={};
        $scope.modelDetail.uuid=uuid;
        $scope.modelDetail.version=version;
        $scope.getAlgorithumByTrainExec();
        $scope.getModelByTrainExec();
        ModelService.getModelResult(uuid, version).then(function(response){ onSuccessGetModelResult(response.data)});
        var onSuccessGetModelResult = function(response) {
            $scope.modelresult1={}
            $scope.modelresult = response;
            $scope.modelresult1.data=response;
           
            $scope.model = false;
            $scope.isMoldeSelect = false;
            $scope.selectedmodelExecdata = true;
        } //End onSuccessGetModelResult
    }
    $scope.getPredictResult=function(data){
        $scope.showProgress = true;
        $scope.isTableShow=false;
        var uuid = data.uuid;
        var version = data.version;
        $scope.modelDetail={};
        $scope.modelDetail.uuid=uuid;
        $scope.modelDetail.version=version
        ModelService.getPredictResult(uuid, version).then(function(response){ onSuccessGetPredictResult(response.data)});
        var onSuccessGetPredictResult = function(response) {
            $scope.showProgress = false;
            $scope.isTableShow=true;
            $scope.isDataError =false;
            $scope.gridOptions.data=$scope.getResults($scope.pagination,response);
            $scope.gridOptions.columnDefs=$scope.getColumnData(response);
            $scope.originalData = response;
        }
    }

    $scope.getSubultateResult=function(data){
        $scope.showProgress = true;
        $scope.isTableShow=false;
        var uuid = data.uuid;
        var version = data.version;
        $scope.modelDetail={};
        $scope.modelDetail.uuid=uuid;
        $scope.modelDetail.version=version;

        ModelService.getSimulateResult(uuid, version).then(function(response){ onSuccessGetSimulateResult(response.data)},function(response){OnError(response.data)});
        var onSuccessGetSimulateResult = function(response) {
            $scope.showProgress = false;
            $scope.isTableShow=true;
            $scope.isDataError =false;
            $scope.gridOptions.data=$scope.getResults($scope.pagination,response);
            $scope.gridOptions.columnDefs=$scope.getColumnData(response);
            $scope.originalData = response;
        }
        var OnError = function(response){
            scope.showProgress = false;
            $scope.isTableShow=false;
            $scope.isDataError =true;
            $scope.dataMessage = "Some Error Occurred"
        }
    }

    $scope.getOperatorResult=function(data){
        $scope.showProgress = true;
        $scope.isTableShow=false;
        var uuid = data.uuid;
        var version = data.version;
        $scope.modelDetail={};
        $scope.modelDetail.uuid=uuid;
        $scope.modelDetail.version=version;

        ModelService.getOperatorResult(uuid, version).then(function(response){ onSuccessGetOperatorResult(response.data)},function(response){OnError(response.data)});
        var onSuccessGetOperatorResult= function(response) {
            $scope.showProgress = false;
            $scope.isTableShow=true;
            $scope.isDataError =false;
            $scope.gridOptions.data=$scope.getResults($scope.pagination,response);
            $scope.gridOptions.columnDefs=$scope.getColumnData(response);
            $scope.originalData = response;
        }
        var OnError = function(response){
            scope.showProgress = false;
            $scope.isTableShow=false;
            $scope.isDataError =true;
            $scope.dataMessage = "Some Error Occurred"
        }
    }


    $scope.refreshData = function(searchtext) {
       var data = $filter('filter')($scope.originalData,searchtext, undefined);
       $scope.gridOptions.data=$scope.getResults($scope.pagination,data);
    };

    if($stateParams.type =="train"){
        $scope.getTrainResult({uuid:$stateParams.id,version:$stateParams.version});
    }
    else if($stateParams.type =="predict"){
        $scope.getPredictResult({uuid:$stateParams.id,version:$stateParams.version});
    }
    else if($stateParams.type =="simulate"){
        $scope.getSubultateResult({uuid:$stateParams.id,version:$stateParams.version});
    }
    else if($stateParams.type == 'operator'){
        $scope.getOperatorResult({uuid:$stateParams.id,version:$stateParams.version})
    }
    $scope.refreshMoldeResult=function(){
       
    }
    $scope.showPMMLResult=function(){
        var url = $location.absUrl().split("app")[0]
        $http({method : 'GET',
            url : url + "model/download?modelExecUUID="+$scope.modelDetail.uuid+"&modelExecVersion="+ $scope.modelDetail.version,
            responseType : 'arraybuffer'
            }).success(
            function(data, status, headers) {
                $scope.isShowPMML=true;
                headers = headers(); 
                var filename = headers['filename'];
                var contentType = headers['content-type'];
                var linkElement = document.createElement('a');
                try {
                    var blob = new Blob([ data ], {
                        type : contentType
                    });
                 
                    var url = window.URL.createObjectURL(blob);
                    var c=LoadXML("showPMML",url);
                } catch (ex) {
                    console.log(ex);
                }
                var burl = $location.absUrl().split("app")[0]
                $http({method : 'GET',
                url : burl   + "model/download?modelExecUUID="+$scope.modelDetail.uuid+"&modelExecVersion="+ $scope.modelDetail.version,
               // responseType : 'arraybuffer'
                }).success(function(data, status, headers){
                    $scope.pMMLResult=data
                })    
            }).error(function(data) {
            console.log();
        });
    }               
    $scope.downloadPMMLResult = function() {
        var url = $location.absUrl().split("app")[0]
        $http({method : 'GET',
            url : url + "model/download?modelExecUUID="+$scope.modelDetail.uuid+"&modelExecVersion="+ $scope.modelDetail.version,
            responseType : 'arraybuffer'
            }).success(
            function(data, status, headers) {
                headers = headers();

               console.log(typeof(data))
 
                var filename = headers['filename'];
                var contentType = headers['content-type'];
                var linkElement = document.createElement('a');
                try {
                    var blob = new Blob([ data ], {
                        type : contentType
                    });
                    var url = window.URL.createObjectURL(blob);
                    linkElement.setAttribute('href', url);
                    linkElement.setAttribute("download",filename);
                    //LoadXML("showPMML",url);
                    var clickEvent = new MouseEvent(
                        "click", {
                            "view" : window,
                            "bubbles" : true,
                            "cancelable" : false
                        });
                    linkElement.dispatchEvent(clickEvent);
                } catch (ex) {
                    console.log(ex);
                }
            }).error(function(data) {
            console.log();
        });
    };
    
    $scope.downloadMoldeResult = function() {
        $('#downloadSample').modal({
          backdrop: 'static',
          keyboard: false
        });
    }

    $scope.submitDownload = function() {
        var baseurl = $location.absUrl().split("app")[0];
        var url;
       
        if($stateParams.type =="predict"){
            url=baseurl+"model/predict/download?action=view&predictExecUUID="+$scope.modelDetail.uuid+"&predictExecVersion="+$scope.modelDetail.version+"&mode=BATCH"+"&rows="+$scope.download.rows;
        }
        else if($stateParams.type =="simulate"){
            url=baseurl+"model/simulate/download?action=view&simulateExecUUID="+$scope.modelDetail.uuid+"&simulateExecVersion="+$scope.modelDetail.version+"&mode=''"+"&rows="+$scope.download.rows;
        }
        else if($stateParams.type =="train"){
            if($scope.modelData.customFlag =="N"){
                $scope.downloadTrainData();
            return ;
            } 
            url=baseurl+"model/train/download?action=view&trainExecUUID="+$scope.modelDetail.uuid+"&trainExecVersion="+$scope.modelDetail.version+"&mode=''";
        }
        $('#downloadSample').modal("hide");
        $http({method : 'GET',
            url : url,
            responseType : 'arraybuffer'
            }).success(
            function(data, status, headers) {
                $scope.download.rows=CF_DOWNLOAD.framework_download_minrows;
                headers = headers();
               console.log(typeof(data))
                var filename = headers['filename'];
                var contentType = headers['content-type'];
                var linkElement = document.createElement('a');
                try {
                    var blob = new Blob([ data ], {
                        type : contentType
                    });
                    var url = window.URL.createObjectURL(blob);
                    linkElement.setAttribute('href', url);
                    linkElement.setAttribute("download",filename);
                    var clickEvent = new MouseEvent(
                        "click", {
                            "view" : window,
                            "bubbles" : true,
                            "cancelable" : false
                        });
                    linkElement.dispatchEvent(clickEvent);
                } catch (ex) {
                    console.log(ex);
                }
            }).error(function(data) {
            console.log();
        });
    };

    $scope.downloadTrainData=function(){
        var linkElement = document.createElement('a');
        try {
            var jsonobj = angular.toJson($scope.modelresult, true);
            var blob = new Blob([ jsonobj ], {
                type: "text/xml"
            });
            var url = window.URL.createObjectURL(blob);
            linkElement.setAttribute('href', url);
            linkElement.setAttribute("download", $scope.modelDetail.uuid+".json");
            var clickEvent = new MouseEvent(
                "click", {
                    "view" : window,
                    "bubbles" : true,
                    "cancelable" : false
                });
            linkElement.dispatchEvent(clickEvent);

        } catch (ex) {
            console.log(ex);
        }
    }

    var myVarResult;
    $scope.autoRefreshResultOnChange=function () {
        if($scope.autoRefreshResult){
            myVarResult = setInterval(function(){
             $scope.getTrainResult({uuid:$stateParams.id,version:$stateParams.version});
    
            },$scope.autoRefreshCounterResult+"000");
        }
        else{
            clearInterval(myVarResult);
        }
    }

    $scope.showModel = function() {
        $scope.model = true;
        $scope.selectedmodelExecdata = false;
        $scope.isMoldeSelect = true;
        $scope.selectedmodeldata = true;
    }

    $scope.$on('$destroy', function() {
        // Make sure that the interval is destroyed too
            clearInterval(myVarResult);
    });
    $scope.onPageChanged = function(){
        $scope.gridOptions.data=$scope.getResults($scope.pagination,$scope.originalData);
        console.log($scope.gridOptions.data);
      };
    $scope.getResults = function(pagination,params) {
        pagination.totalItems=params.length;
        if(pagination.totalItems >0){
          pagination.to = (((pagination.currentPage - 1) * (pagination.pageSize))+1);
        }
        else{
          pagination.to=0;
        }
        if(pagination.totalItems < (pagination.pageSize*pagination.currentPage)) {
            pagination.from = pagination.totalItems;
        } else {
          pagination.from = ((pagination.currentPage) * pagination.pageSize);
        }
        var limit = (pagination.pageSize* pagination.currentPage);
        var offset = ((pagination.currentPage - 1) * pagination.pageSize)
        return params.slice(offset,limit);
    }
    $scope.getColumnData=function(response){
        var columnDefs=[];
        var count=0;
        angular.forEach(response[0], function(value, key) {
         count=count+1;
        })
        angular.forEach(response[0], function(value, key) {
            var attribute = {};
            if (key == "rownum") {
                attribute.visible = false
            } else {
                attribute.visible = true
            }
            attribute.name = key
            attribute.displayName = key
            if(count >3){
            attribute.width = key.split('').length + 12 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150]
            }else{
                attribute.width=(100/count)+"%";
            }
            columnDefs.push(attribute)
        });
        return columnDefs;
    }

});