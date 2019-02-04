/****/

RuleModule = angular.module('RuleModule');
RuleModule.controller('RuleComapreController',function($state,$stateParams,$rootScope,$scope,$filter,RuleService,uuid2) {    
    $scope.searchForm={};
    $scope.isSelect=true;
    $scope.allSourceRuleexec;
    $scope.sourcePagination={};
    $scope.targetPagination={};
    $scope.sourcePagination.currentPage = 1;
    $scope.sourcePagination.pageSize = 10;
    $scope.sourcePagination.maxSize = 2;
    $scope.sourcePagination.paginationPageSizes = [10, 25, 50, 75, 100],
    $scope.targetPagination.currentPage = 1;
    $scope.targetPagination.pageSize = 10;
    $scope.targetPagination.maxSize = 2;
    $scope.targetPagination.paginationPageSizes = [10, 25, 50, 75, 100],
    $scope.filteredRows=[];
    $scope.sortdetail=[];
    $scope.isSourceTableShow=false;
    $scope.sourceGridOptions = {
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
        onRegisterApi: function(gridApi) {
          $scope.gridApiResule = gridApi;
          $scope.filteredRows = $scope.gridApiResule.core.getVisibleRows($scope.gridApiResule.grid);
          $scope.gridApiResule.core.on.sortChanged($scope, function(grid, sortColumns) {
            if (sortColumns.length > 0) {
              $scope.searchSoruceRequestId(sortColumns);
            }
          });
        }
    };
    $scope.targetGridOptions = {
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
        onRegisterApi: function(gridApi) {
          $scope.gridApiTargetResule = gridApi;
          $scope.targetFilteredRows = $scope.gridApiTargetResule.core.getVisibleRows($scope.gridApiTargetResule.grid);
          $scope.gridApiTargetResule.core.on.sortChanged($scope, function(grid, sortColumns) {
            if (sortColumns.length > 0) {
              $scope.searchTargetRequestId(sortColumns);
            }
          });
        }
    };
    $scope.getSourceGridStyle=function() {
        var style = {
          'margin-top': '10px',
          'margin-bottom': '10px',
        }
        if ($scope.filteredRows && $scope.filteredRows.length >0 ) {
          style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 80) + 'px';
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
    $scope.getAllLatest=function(type){
        RuleService.getAllLatest(type).then(function(response){onSuccessGetAllLatest(response.data)});
        var onSuccessGetAllLatest=function(response){
            type =="rule"?$scope.allRule=response:$scope.allRuleGroup=response; 
        }
    }
    $scope.getAllLatest("rule");
    $scope.getAllLatest("rulegroup");
    $scope.refreshSearchCriteria=function(){
        $scope.searchForm={};
        $scope.getAllLatest("rule");
        $scope.getAllLatest("rulegroup");
        $scope.refreshCompareResult(false);
    }
    $scope.refreshCompareResult=function(defaultValue){
        $scope.allSourceRuleexec=[];
        $scope.isSourceTableShow=false;
        $scope.isSourceDataError=false;
        $scope.selectTargetRuleexec=null;
        $scope.isTargetTableShow=false;
        $scope.isTargetDataError=false;
        $scope.allTargetRuleexec=[];
        if(!defaultValue)
        $scope.searchCriteria(false);

    }
    $scope.sourceFilerSearch = function(searchtext) {
        $scope.sourceGridOptions.data = $filter('filter')($scope.sourceOriginalData, searchtext, undefined);
    };
    $scope.targetFilerSearch = function(searchtext) {
        $scope.targetGridOptions.data = $filter('filter')($scope.targetOriginalData, searchtext, undefined);
    }
    
    
    $scope.onChangeRuleGroup=function(){
        if($scope.searchForm.ruleGroup !=null){
            RuleService.getOneByUuidAndVersionForGroup($scope.searchForm.ruleGroup.uuid,$scope.searchForm.ruleGroup.version,'rulegroup').then(function(response){onSuccessGetOneByUuidAndVersionForGroup(response.data)});
            var onSuccessGetOneByUuidAndVersionForGroup=function(response){
                var data = {};
                data.options = [];
                var defaultoption = {};
                if(response.ruleInfo.length>0) {
                    defaultoption.name = response.ruleInfo[0].ref.name;
                    defaultoption.uuid = response.ruleInfo[0].ref.uuid;
                    defaultoption.version =response.ruleInfo[0].ref.version;
                    data.defaultoption = defaultoption;
                    for (var i = 0; i < response.ruleInfo.length; i++) {
                        var datajosn = {}
                        datajosn.name = response.ruleInfo[i].ref.name;
                        datajosn.uuid = response.ruleInfo[i].ref.uuid;
                        datajosn.version = response.ruleInfo[i].ref.version;
                        data.options[i] = datajosn
                    }
                }
                $scope.allRule=data;
            }
        }else{
            $scope.getAllLatest("rule");
        }
    }
    $scope.onChangeRule=function(){
        if($scope.searchForm.rule !=null){
            $scope.isSelect=false;
        }else{
            $scope.isSelect=true;
        }
    }
    $scope.onChangeSourceRuleexec=function(){
        console.log($scope.selectSoureRuleexec)
        if($scope.selectSoureRuleexec){
            if($scope.selectTargetRuleexec){
                if($scope.selectSoureRuleexec.uuid == $scope.selectTargetRuleexec.uuid){
                $scope.allTargetRuleexec=[];
                $scope.isTargetTableShow=false;
                }
            }
            $scope.allTargetRuleexec = $scope.allSourceRuleexec.filter(function(el) {
                return el.uuid !== $scope.selectSoureRuleexec.uuid;
            });
            RuleService.getNumRowsbyExec($scope.selectSoureRuleexec.uuid,$scope.selectSoureRuleexec.version,"ruleexec").then(function(response) {onSuccessGetNumRowsbyExec(response.data)});
            var onSuccessGetNumRowsbyExec = function(response) {
                $scope.sourcePagination.totalItems = response.numRows;
                $scope.sourcePagination.to = ((($scope.sourcePagination.currentPage - 1) * $scope.sourcePagination.pageSize)+1);
                if ($scope.sourcePagination.totalItems < ($scope.sourcePagination.pageSize*$scope.sourcePagination.currentPage)) {
                 // $scope.gridOptions.datafrom = $scope.totalItems;
                } else {
                  $scope.sourcePagination.from = (($scope.sourcePagination.currentPage) * $scope.sourcePagination.pageSize);
                }
                
                $scope.sourceShowProgress=true;
                $scope.isSourceDataError = false;
                $scope.getResults(null,$scope.selectSoureRuleexec,$scope.sourcePagination.pageSize,$scope.sourcePagination.currentPage,"source"); 
            }
        }
    }
    $scope.onChangeTargetRuleExec=function(){
        if($scope.selectTargetRuleexec){
            RuleService.getNumRowsbyExec($scope.selectTargetRuleexec.uuid,$scope.selectTargetRuleexec.version,"ruleexec").then(function(response) {onSuccessGetNumRowsbyExec(response.data)});
            var onSuccessGetNumRowsbyExec = function(response) {
                $scope.targetPagination.totalItems = response.numRows;
                $scope.targetPagination.to = ((($scope.targetPagination.currentPage - 1) * $scope.targetPagination.pageSize)+1);
                if ($scope.targetPagination.totalItems < ($scope.targetPagination.pageSize*$scope.targetPagination.currentPage)) {
                }else{
                $scope.targetPagination.from = (($scope.targetPagination.currentPage) * $scope.targetPagination.pageSize);
                }
                $scope.targetShowProgress=true;
                $scope.isTargetDataError = false;
                $scope.getResults(null,$scope.selectTargetRuleexec,$scope.targetPagination.pageSize,$scope.targetPagination.currentPage,"target"); 

            }
        }
    }
    $scope.onSourcepageChanged = function(){
        $scope.sourcePagination.to = ((($scope.sourcePagination.currentPage - 1) * $scope.sourcePagination.pageSize)+1);
        if ($scope.sourcePagination.totalItems < ($scope.sourcePagination.pageSize*$scope.sourcePagination.currentPage)) {
         // $scope.gridOptions.datafrom = $scope.totalItems;
        } else {
          $scope.sourcePagination.from = (($scope.sourcePagination.currentPage) * $scope.sourcePagination.pageSize);
        }
        $scope.sourceShowProgress = true;
        $scope.isSourceTableShow=false;
        $scope.isSourceDataError =false;
        $scope.getResults(null,$scope.selectSoureRuleexec,$scope.sourcePagination.pageSize,$scope.sourcePagination.currentPage,"source")
       // $log.log('Page changed to: ' + (($scope.currentPage - 1) * $scope.pageSize));
      };
      
      $scope.onTargetpageChanged = function(){
        $scope.targetPagination.to = ((($scope.targetPagination.currentPage - 1) * $scope.targetPagination.pageSize)+1);
        if ($scope.targetPagination.totalItems < ($scope.targetPagination.pageSize*$scope.targetPagination.currentPage)) {
         // $scope.gridOptions.datafrom = $scope.totalItems;
        } else {
          $scope.targetPagination.from = (($scope.targetPagination.currentPage) * $scope.targetPagination.pageSize);
        }
        $scope.targetShowProgress = true;
        $scope.isTargetTableShow=false;
        $scope.isTargetDataError = false;
        $scope.getResults(null,$scope.selectTargetRuleexec,$scope.targetPagination.pageSize,$scope.targetPagination.currentPage,"target")
       // $log.log('Page changed to: ' + (($scope.currentPage - 1) * $scope.pageSize));
      };

    
    //$scope.onSourcePerPageChange = function() {
    //     $scope.currentPage = 1;
    //     $scope.getResults(null)
    //}
    $scope.searchSoruceRequestId = function(sortColumns) {
        var sortBy = sortColumns[0].name;
        var order = sortColumns[0].sort.direction;
        var result = {};
        result.sortBy = sortBy;
        result.order = order;
        if ($scope.sortdetail.length == 0) {
            var sortobj = {};
            sortobj.uuid = uuid2.newuuid();
            sortobj.colname = sortColumns[0].name;
            sortobj.order = sortColumns[0].sort.direction;
            sortobj.limit = $scope.sourcePagination.pageSize;
            $scope.sortdetail[$scope.colcount] = sortobj;
            $scope.colcount = $scope.colcount + 1;
            result.requestId = sortobj.uuid;
          //offset = 0;
        } else {
          var idpresent = "N";
            for (var i = 0; i < $scope.sortdetail.length; i++) {
                if($scope.sortdetail[i].colname == sortBy && $scope.sortdetail[i].order == order && $scope.sortdetail[i].limit == $scope.pageSize) {
                    result.requestId = $scope.sortdetail[i].uuid;
                    idpresent = "Y"
                    break;
                }
            } //End For
            if (idpresent == "N") {
                var sortobj = {};
                sortobj.uuid = uuid2.newuuid();
                result.requestId = sortobj.uuid;
                sortobj.colname = sortColumns[0].name;
                sortobj.order = sortColumns[0].sort.direction;
                sortobj.limit = $scope.sourcePagination.pageSize;
                $scope.sortdetail[$scope.colcount] = sortobj;
                $scope.colcount = $scope.colcount + 1;
                offset = 0;
            } //End Else Innder IF
        } //End IF Inner Else
        //console.log(JSON.stringify($scope.sortdetail));
        $scope.showprogress = true;
        $scope.getResults(result,$scope.selectSoureRuleexec,$scope.sourcePagination.pageSize,$scope.sourcePagination.currentPage,"source");
    }

    $scope.searchTargetRequestId = function(sortColumns) {
        var sortBy = sortColumns[0].name;
        var order = sortColumns[0].sort.direction;
        var result = {};
        result.sortBy = sortBy;
        result.order = order;
        if ($scope.sortdetail.length == 0) {
            var sortobj = {};
            sortobj.uuid = uuid2.newuuid();
            sortobj.colname = sortColumns[0].name;
            sortobj.order = sortColumns[0].sort.direction;
            sortobj.limit = $scope.targetPagination.pageSize;
            $scope.sortdetail[$scope.colcount] = sortobj;
            $scope.colcount = $scope.colcount + 1;
            result.requestId = sortobj.uuid;
          //offset = 0;
        } else {
          var idpresent = "N";
            for (var i = 0; i < $scope.sortdetail.length; i++) {
                if($scope.sortdetail[i].colname == sortBy && $scope.sortdetail[i].order == order && $scope.sortdetail[i].limit == $scope.pageSize) {
                    result.requestId = $scope.sortdetail[i].uuid;
                    idpresent = "Y"
                    break;
                }
            } //End For
            if (idpresent == "N") {
                var sortobj = {};
                sortobj.uuid = uuid2.newuuid();
                result.requestId = sortobj.uuid;
                sortobj.colname = sortColumns[0].name;
                sortobj.order = sortColumns[0].sort.direction;
                sortobj.limit = $scope.targetPagination.pageSize;
                $scope.sortdetail[$scope.colcount] = sortobj;
                $scope.colcount = $scope.colcount + 1;
                offset = 0;
            } //End Else Innder IF
        } //End IF Inner Else
        //console.log(JSON.stringify($scope.sortdetail));
        $scope.showprogress = true;
        $scope.getResults(result,$scope.selectTargetRuleexec,$scope.targetPagination.pageSize,$scope.targetPagination.currentPage,"target");
    }

    $scope.onChangeDate=function(){
        if($scope.searchForm.rule !=null){
            $scope.isSelect=false;
        }else{
            $scope.isSelect=true;
        }
        
    }

    $scope.searchCriteria=function(defaultValue){
        if(defaultValue){
            $scope.isSelect=true;
            $scope.isInProgress=true;
            $scope.refreshCompareResult(true);
        }
        var startdate="";
        if($scope.searchForm.startdate != null) {
            startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
            enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy",'UTC');
            enddate = enddate + " UTC";
        }
        console.log(startdate);
      //  console.log($scope.searchForm);
        if($scope.searchForm.rule){
            RuleService.getRuleExecByRulewithDate($scope.searchForm.rule.uuid,startdate,enddate).then(function(response){onSuccessGetRuleExecByRule(response.data)});
            var onSuccessGetRuleExecByRule=function(response){
                $scope.allSourceRuleexec =response; 
                $scope.isInProgress=false;  
            }
        }
        
    }

    $scope.getResults = function(params,ruleExecDetail,pageSize,currentPage,type) {
        var uuid=ruleExecDetail.uuid;
        var version =ruleExecDetail.version;
        var limit =pageSize;
        var offset;
        var requestId;
        var sortBy;
        var order;
        if (params == null) {
            offset = ((currentPage - 1) * pageSize);
            requestId = "";
            sortBy = null
            order = null;
        }else {
            offset = 0;
            requestId = params.requestId;
            sortBy = params.sortBy
            order = params.order;
        }
        RuleService.getRuleResults(uuid, version, offset || 0, limit, requestId, sortBy, order).then(function(response){onSuccessGetRuleResults(response.data)}, function(response){ OnError(response.data)});
        var onSuccessGetRuleResults = function(response) {
            if(type =="source"){
                $scope.sourceGridOptions.columnDefs = [];
                $scope.sourceGridOptions.columnDefs= $scope.getColumnData(response);
                $scope.sourceGridOptions.data = response.data;
                $scope.sourceOriginalData = response.data;
                $scope.sourceShowProgress = false;
                $scope.isSourceTableShow=true;
            }
            if(type =="target"){
                $scope.targetGridOptions.columnDefs= $scope.getColumnData(response);
                $scope.targetGridOptions.data = response.data;
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
    
    $scope.getColumnData=function(response){
        var columnDefs=[];
        var count=0;
        angular.forEach(response.data[0], function(value, key) {
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