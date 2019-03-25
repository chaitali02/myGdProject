MetadataModule = angular.module('MetadataModule');

MetadataModule.controller('ResultMapController', function ($filter, $state, $location, $http, $stateParams, dagMetaDataService, $rootScope, $scope, MetadataMapSerivce, CF_DOWNLOAD) {


    $scope.caption = dagMetaDataService.elementDefs[$stateParams.type].caption;
    $scope.type = $stateParams.type
    $scope.autoRefreshCounterResult = 05;
    $scope.autoRefreshResult = false;
    $scope.download = {};
    $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
    $scope.download.formates = CF_DOWNLOAD.formate;
    $scope.download.selectFormate = CF_DOWNLOAD.formate[0];
    $scope.download.maxrow = CF_DOWNLOAD.framework_download_maxrow;
    $scope.download.limit_to = CF_DOWNLOAD.limit_to;
    $scope.pagination = {
        currentPage: 1,
        pageSize: 10,
        paginationPageSizes: [10, 25, 50, 75, 100],
        maxSize: 5,
    }

    $scope.showGraph = function () {
        $scope.isGraphShow = true;
    }
    $scope.showResultPage = function () {
        $scope.isGraphShow = false;
    }

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

    $scope.gridOptions = {
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

    $scope.gridOptions.onRegisterApi = function (gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };

    $scope.close = function () {
        $state.go('maprestult');
    }


    $scope.getNumRowsbyExec = function (uuid, version) {
        $scope.showProgress = true;
        $scope.isTableShow = false;;
        $scope.maplDetail = {};
        $scope.maplDetail.uuid = uuid;
        $scope.maplDetail.version = version
        MetadataMapSerivce.getNumRowsbyExec(uuid, version, "mapexec").then(function (response) { onSuccessGetPredictResult(response.data) });
        var onSuccessGetPredictResult = function (response) {
            var mode = response.runMode;
            $scope.getResult(uuid, version, mode);
        }

    }

    $scope.getResult = function (uuid, version, mode) {
        MetadataMapSerivce.getResults(uuid, version, mode).then(function (response) { onSuccessGetPredictResult(response.data) });
        var onSuccessGetPredictResult = function (response) {
            $scope.showProgress = false;
            $scope.isTableShow = true;
            $scope.isDataError = false;
            $scope.gridOptions.data = $scope.getResults($scope.pagination, response);
            $scope.gridOptions.columnDefs = $scope.getColumnData(response);
            $scope.originalData = response;
        }
    }

    $scope.refreshData = function (searchtext) {
        var data = $filter('filter')($scope.originalData, searchtext, undefined);
        $scope.gridOptions.data = $scope.getResults($scope.pagination, data);
    };


    $scope.getNumRowsbyExec($stateParams.id, $stateParams.version);

    $scope.refreshMapResult = function () {
        $scope.getNumRowsbyExec($stateParams.id, $stateParams.version);
    }

    $scope.downloadMapResult = function () {
        $scope.isDownloadDirective=true;
		$scope.download.uuid=$scope.maplDetail.uuid;
		$scope.download.version =$scope.maplDetail.version;
		$scope.download.type="map";
		// $('#downloadSample').modal({
		// 	backdrop: 'static',
		// 	keyboard: false
		// });
	};
	$scope.onDownloaed=function(data){
		console.log(data);
		$scope.isDownloadDirective=data.isDownloadDirective;
	}
    

    // $scope.submitDownload = function () {
    //     var baseurl = $location.absUrl().split("app")[0];
    //     var url;
    //     url = baseurl + "map/download?action=view&mapExecUUID=" + $scope.maplDetail.uuid + "&mapExecVersion=" + $scope.maplDetail.version + "&mode=BATCH" + "&rows=" + $scope.download.rows+"&format="+$scope.download.selectFormate;
    //     $('#downloadSample').modal("hide");
    //     $http({
    //         method: 'GET',
    //         url: url,
    //         responseType: 'arraybuffer'
    //     })
    //     .success(function (data, status, headers) {
    //         $scope.download.rows = CF_DOWNLOAD.framework_download_minrows;
    //         headers = headers();
    //        // console.log(typeof (data))
    //         var filename = headers['filename'];
    //         var contentType = headers['content-type'];
    //         var linkElement = document.createElement('a');
    //         try {
    //             var blob = new Blob([data], {
    //                 type: contentType
    //             });
    //             var url = window.URL.createObjectURL(blob);
    //             linkElement.setAttribute('href', url);
    //             linkElement.setAttribute("download", filename);
    //             var clickEvent = new MouseEvent(
    //                 "click", {
    //                     "view": window,
    //                     "bubbles": true,
    //                     "cancelable": false
    //                 });
    //             linkElement.dispatchEvent(clickEvent);
    //         } catch (ex) {
    //             console.log(ex);
    //         }
    //     })
    //     .error(function (data) {
    //         console.log();
    //     });
    // };



    $scope.$on('$destroy', function () {
        // Make sure that the interval is destroyed too
    });

    $scope.selectPage = function (pageNo) {
        $scope.pagination.currentPage = pageNo;
    };

    $scope.onPerPageChange = function () {
        $scope.pagination.currentPage = 1;
        $scope.gridOptions.data = $scope.getResults($scope.pagination, $scope.originalData)
    }

    $scope.onPageChanged = function () {
        $scope.gridOptions.data = $scope.getResults($scope.pagination, $scope.originalData);
    };

    $scope.getResults = function (pagination, params) {
        pagination.totalItems = params.length;
        if (pagination.totalItems > 0) {
            pagination.to = (((pagination.currentPage - 1) * (pagination.pageSize)) + 1);
        }
        else {
            pagination.to = 0;
        }
        if (pagination.totalItems < (pagination.pageSize * pagination.currentPage)) {
            pagination.from = pagination.totalItems;
        } else {
            pagination.from = ((pagination.currentPage) * pagination.pageSize);
        }
        var limit = (pagination.pageSize * pagination.currentPage);
        var offset = ((pagination.currentPage - 1) * pagination.pageSize)
        return params.slice(offset, limit);
    }


    $scope.getColumnData = function (response) {
        var columnDefs = [];
        var count = 0;
        angular.forEach(response[0], function (value, key) {
            count = count + 1;
        })
        angular.forEach(response[0], function (value, key) {
            var attribute = {};
            if (key == "rownum") {
                attribute.visible = false
            } else {
                attribute.visible = true
            }
            attribute.name = key
            attribute.displayName = key
            if (count > 3) {
                attribute.width = key.split('').length + 12 + "%" // Math.floor(Math.random() * (120 - 50 + 1)) + 150]
            } else {
                attribute.width = (100 / count) + "%";
            }
            columnDefs.push(attribute)
        });
        return columnDefs;
    }

});