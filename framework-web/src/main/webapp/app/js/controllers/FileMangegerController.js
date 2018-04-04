AdminModule= angular.module('AdminModule');
AdminModule.controller("FileManagerController",function(uiGridConstants,$state,$filter,$location,$http,$stateParams,dagMetaDataService, $rootScope,FileManagerService,$scope,CommonService) {
    $scope.searchForm={};
    $scope.tz=localStorage.serverTz
    var matches = $scope.tz.match(/\b(\w)/g);
    $scope.timezone=matches.join('');
    $scope.autoRefreshCounter=05;
    $scope.autoRefreshResult=false;
    $scope.searchButtonText="Upload"
    var notify = {
        type: 'success',
        title: 'Success',
        content: 'Dashboard deleted Successfully',
        timeout: 3000 //time in ms
    };
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
  
    $scope.gridOptions ={
        enableGridMenu: true,
        rowHeight: 40,
        exporterSuppressCtiolumns: [ 'action' ],
        exporterMenuPdf: false,
        exporterPdfOrientation: 'landscape',
        exporterPdfPageSize: 'A4',
        exporterPdfDefaultStyle: {fontSize: 9},
        exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
        columnDefs: [
          {
            displayName: 'UUID',
            name: 'uuid',
            visible: false,
            cellClass: 'text-center',
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Name',
            name: 'name',
            minWidth: 220,
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Version',
            name: 'version',
            visible: true,
            maxWidth:100,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            sort: {
              direction: uiGridConstants.DESC,
             // priority: 0,
            },
          },
          {
            displayName: 'Created By',
            name: 'createdBy.ref.name',
            cellClass: 'text-center',
            maxWidth:100,
            headerCellClass: 'text-center'
          },
          {
            displayName: 'Created On',
            name: 'createdOn',
            minWidth: 220,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
    
          },
          {
            displayName: 'Status',
            name: 'active',
            minWidth:100,
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.active == "Y" ? "Active" : "Inactive"}}</div>'
          },
          {
            displayName: 'Published',
            name: 'published',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.published == "Y" ? "Yes" : "No"}}</div>'
          },
          {
            displayName: 'Action',
            name: 'action',
            cellClass: 'text-center',
            headerCellClass: 'text-center',
            minWidth:100,
            cellTemplate: [
              '<div class="ui-grid-cell-contents">',
              '  <div class="dropdown" uib-dropdown dropdown-append-to-body>',
              '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
              '    <i class="fa fa-angle-down"></i></button>',
              '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
              '    <li ><a ><i class="fa fa-download" aria-hidden="true"></i> Download </a></li>',
              '    </ul>',
              '  </div>',
              '</div>'
            ].join('')
          }
        ]
      };
    
   
     
    $scope.gridOptions.onRegisterApi = function(gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };

    $scope.refreshData = function() {
        $scope.gridOptions.data = $filter('filter')($scope.originalData, $scope.searchtext, undefined);
        
      };
    $scope.refresh = function() {
        $scope.searchForm.execname = "";
        $scope.allExecName=[];
        $scope.searchForm.username = "";
        $scope.searchForm.tags = [];
        $scope.searchForm.published = "";
        $scope.searchForm.active = "";
        $scope.searchForm.status = "";
        $scope.searchForm.startdate = null;
        $scope.searchForm.enddate = null;
        $scope.allStatus = [{
            "caption": "Active",
            "name": "Y"
          },
          {
            "caption": "Inactive",
            "name": "N"
          },
          
        ];
        //$scope.getAllLatest();
       // $scope.getBaseEntityStatusByCriteria(true);
    };
    


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
    $scope.getAllLatest=function(type){
        CommonService.getAllLatest(type).then(function(response) { onSuccessGetAllLatestExec(response.data)});
        var onSuccessGetAllLatestExec = function(response) {
            type =="user"?$scope.allUSerName=response:$scope.allExecName = response;
        }
    }
    $scope.getAllLatest("uploadexec");
    $scope.getAllLatest("user");

  
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
        CommonService.getBaseEntityByCriteria("uploadexec", $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '',$scope.searchForm.published || '').then(function(response) {onSuccess(response.data)},function error() {
            $scope.loading = false;});
        var onSuccess = function(response) {
        console.log(response);
        $scope.gridOptions.data = response;
        $scope.originalData = response;
        }
    }

    $scope.getBaseEntityStatusByCriteria(false);
    $scope.refresh();

    $scope.upload=function(){
        $(":file").jfilestyle('clear')
        $("#csv_file").val("");
        $('#fileupload').modal({
            backdrop: 'static',
            keyboard: false
        });
    }

    $scope.uploadFile=function(){
        var iEl = angular.element(document.querySelector('#csv_file'));
        var file = iEl[0].files[0]
        console.log(file)
        var fd = new FormData();
        fd.append('file', file);
        $('#fileupload').modal('hide');
        $scope.searchButtonText="Uploading"
        FileManagerService.SaveFile(file.name,fd,"").then(function(response){onSuccess(response.data)});
        var onSuccess=function(response){
            $scope.searchButtonText="Upload"
            $scope.msg = "CSV Uploaded Successfully"
            notify.type = 'success',
            notify.title = 'Success',
            notify.content = $scope.msg
            $scope.$emit('notify', notify); 
            $scope.getBaseEntityStatusByCriteria(false);
        }
    }
    $scope.fileNameValidate=function(data){
        console.log(data)
    }
    $scope.searchCriteria=function(){
        $scope.getBaseEntityStatusByCriteria(false);
    }
});