DataQualityModule = angular.module('DataQualityModule');

DataQualityModule.controller('DataQualityCompareController', function($state,$filter, $stateParams, $location, $rootScope, $scope, DataqulityService,privilegeSvc) {
    $scope.searchForm={};
    $scope.isSelect=true;
    $scope.types = [
        {"text":"dq","caption":"Rule"},
        {"text":"datapod","caption":"Datapod"}
       
    ]

    $scope.sourceFilteredRows = [];
    $scope.targetFilteredRows = [];
    $scope.sourcePagination={
        currentPage:1,
        pageSize:10,
        paginationPageSizes:[10, 25, 50, 75, 100],
        maxSize:5,
    }
    $scope.targetPagination={
        currentPage:1,
        pageSize:10,
        paginationPageSizes:[10, 25, 50, 75, 100],
        maxSize:5,
    }  
    $scope.sourceGridOptions = {
        rowHeight: 40,
        useExternalPagination: true,
        exporterMenuPdf: false,
        exporterPdfOrientation: 'landscape',
        exporterPdfPageSize: 'A4',
        exporterPdfDefaultStyle: {fontSize: 9},
        exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
        enableGridMenu: true,
        rowHeight: 40,
        onRegisterApi:  function(gridApi){
            $scope.sourceGridApi = gridApi;
            $scope.sourceFilteredRows = $scope.sourceGridApi.core.getVisibleRows($scope.sourceGridApi.grid);
        }
    }
    $scope.targetGridOptions = {
        rowHeight: 40,
        useExternalPagination: true,
        exporterMenuPdf: false,
        exporterPdfOrientation: 'landscape',
        exporterPdfPageSize: 'A4',
        exporterPdfDefaultStyle: {fontSize: 9},
        exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
        enableGridMenu: true,
        rowHeight: 40,
        onRegisterApi:  function(gridApi){
            $scope.targetGridApi = gridApi;
            $scope.targetFilteredRows = $scope.targetGridApi.core.getVisibleRows($scope.targetGridApi.grid);
        }
    }

          
    $scope.getSourceGridStyle=function() {
        var style = {
            'margin-top': '10px',
            'margin-bottom': '10px',
        }
        if ($scope.sourceFilteredRows && $scope.sourceFilteredRows.length >0 ) {
            style['height'] = (($scope.sourceFilteredRows.length < 10 ? $scope.sourceFilteredRows.length * 40 : 400) + 80) + 'px';
        }
        else{
            style['height']="100px"
        }    
        return style;
    }
    $scope.getTargetGridStyle=function() {
        var style = {
            'margin-top': '10px',
            'margin-bottom': '10px',
        }
        if ($scope.targetFilteredRows && $scope.targetFilteredRows.length >0 ) {
            style['height'] = (($scope.targetFilteredRows.length < 10 ? $scope.targetFilteredRows.length * 40 : 400) + 80) + 'px';
        }
        else{
            style['height']="100px"
        }    
        return style;
    }
    
    $scope.refreshCompareResult=function(defaultValue){
        $scope.allSourceDqexec=[];
        $scope.isSourceTableShow=false;
        $scope.isSourceDataError=false;
        $scope.selectTargetDqexec=null;
        $scope.isTargetTableShow=false;
        $scope.isTargetDataError=false;
        $scope.allTargetDqexec=[];
        if(!defaultValue)
        $scope.searchCriteria(false);

    }
    $scope.filterSearch =function (s) {
        var data = $filter('filter')($scope.orignalData, s, undefined);
        $scope.getResults(data)
    }

    $scope.searchForm.type=$scope.types[0].text;
    $scope.getAllLatest=function(type){
        DataqulityService.getAllLatest(type).then(function(response){onSuccessGetAllLatest(response.data)});
        var onSuccessGetAllLatest=function(response){   
            $scope.allname=response;
            $scope.isSelect=true;
        }
    }
    
    $scope.getAllLatest($scope.searchForm.type);

    $scope.refreshSearchCriteria=function(){
        $scope.searchForm={};
        $scope.searchForm.type=$scope.types[0].text;
        $scope.allname=[];
        $scope.getAllLatest($scope.searchForm.type); 
        $scope.refreshCompareResult(true);
    }

    $scope.onChangeType=function(type){
        $scope.refreshCompareResult(true)
        $scope.getAllLatest(type);
    }

    $scope.onChangeRule=function(){
        if($scope.searchForm.selectedobj !=null){
            $scope.isSelect=false;
        }else{
            $scope.isSelect=true;
        }
    }

    $scope.onChangeDate=function(){
       
        if($scope.searchForm.selectedobj !=null){
            $scope.isSelect=false;
        }else{
            $scope.isSelect=true;
        }
        
    }
    $scope.onChangeSourceDQexec=function(){
        if($scope.selectSoureDqexec){
            if($scope.selectTargetDqexec){
                if($scope.selectSoureDqexec.uuid == $scope.selectTargetDqexec.uuid){
                $scope.allTargetDqexec=[];
                $scope.isTargetTableShow=false;
                }
            }
            $scope.allTargetDqexec = $scope.allSourceDqexec.filter(function(el) {
                return el.uuid !== $scope.selectSoureDqexec.uuid;
            });
            DataqulityService.getNumRowsbyExec($scope.selectSoureDqexec.uuid,$scope.selectSoureDqexec.version,"dqexec").then(function(response) {onSuccessGetNumRowsbyExec(response.data)});
            var onSuccessGetNumRowsbyExec = function(response) { 
                $scope.sourceShowProgress = true;
                $scope.isSourceTableShow=false;
                $scope.getSummary($scope.selectSoureDqexec.uuid,$scope.selectSoureDqexec.version,"source",response.runMode)   
            }
        }
    }
    $scope.onChangeTargetDqExec=function(){
        if($scope.selectTargetDqexec){
            DataqulityService.getNumRowsbyExec($scope.selectTargetDqexec.uuid,$scope.selectTargetDqexec.version,"dqexec").then(function(response) {onSuccessGetNumRowsbyExec(response.data)});
            var onSuccessGetNumRowsbyExec = function(response) { 
                $scope.targetShowProgress = true;
                $scope.getSummary($scope.selectTargetDqexec.uuid,$scope.selectTargetDqexec.version,"target",response.runMode)   
            }
        }
    }

    $scope.onSourcepageChanged = function(){
        
        $scope.sourceShowProgress = true;
        $scope.isSourceTableShow=false;
        $scope.isSourceDataError =false;
        $scope.getResults($scope.sourcePagination,$scope.sourceOriginalData)
      };
      
      $scope.onTargetpageChanged = function(){ 
        $scope.targetShowProgress = true;
        $scope.isTargetTableShow=false;
        $scope.isTargetDataError = false;
        $scope.getResults($scope.targetPagination,$scope.targetOriginalData)
       
      };
    
    $scope.getSummary=function(uuid,version,type,mode){
        DataqulityService.getSummary(uuid,version,"dqexec",mode).then(function(response) {onSuccessGetSummary(response.data)},function(response){OnError(response.data)});
        var onSuccessGetSummary = function(response) { 
            console.log(response)
            if(type == "source"){  
                $scope.sourceGridOptions.data=$scope.getResults($scope.sourcePagination,response);
                $scope.sourceGridOptions.columnDefs=$scope.getColumnData(response);
                $scope.sourceOriginalData = response.data;
                $scope.sourceShowProgress = false;
                $scope.isSourceTableShow=true;
            }
            if(type == "target"){  
                $scope.targetGridOptions.data=$scope.getResults($scope.targetPagination,response);
                $scope.targetGridOptions.columnDefs=$scope.getColumnData(response);
                $scope.targetOriginalData = response.data;
                $scope.targetShowProgress = false;
                $scope.isTargetTableShow=true;
            }   
        }
        var OnError = function(response){
            if(type =="source"){
                $scope.sourceShowProgress = false;
                $scope.isSourceTableShow=false;
                $scope.isSourceDataError = true;
                $scope.sourceDataMessage = "Some Error Occurred"
            }
            if(type =="target"){
                $scope.targetShowProgress = false;
                $scope.isTargetTableShow=false;
                $scope.isTargetDataError = true;
                $scope.targetDataMessage = "Some Error Occurred"
            }
        }
    }
    $scope.searchCriteria=function(defaultValue){    
        var startdate="";
        $scope.isSelect=true;
        $scope.isInProgress=true;
        if(defaultValue)
        $scope.refreshCompareResult(true)
        if($scope.searchForm.startdate != null) {
            startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
            enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            enddate = enddate + " UTC";
        }
        DataqulityService[$scope.searchForm.type=="dq" ? 'getDqExecByDqwithParms' : 'getDqExecByDatapod']($scope.searchForm.selectedobj.uuid,startdate,enddate).then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
            $scope.allSourceDqexec =response; 
            $scope.isInProgress=false;
            $scope.isSelect=true;  
        }
    }

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
            var attribute = {};
            if (key == "rownum") {
                attribute.visible = false
            } else {
                attribute.visible = true
            }
            attribute.name = key
            attribute.displayName = key
            attribute.width = key.split('').length + 12 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150
            columnDefs.push(attribute)
        });
        return columnDefs;
    }
}); 