/**
 *
 */
DatadiscoveryModule = angular.module('DatadiscoveryModule');


DatadiscoveryModule.controller('DataDiscoveryController', function ($state,$filter,dagMetaDataService, PagerService, $stateParams, $rootScope, $scope, $sessionStorage, DataDiscoveryService) {
    $scope.optionsort = [
        { "caption": "Name A-Z", name: "name" ,"isAsc":true },
        { "caption": "Name Z-A", name: "-name","isAsc":false  },
        { "caption": "Date Asc", name: "lastUpdatedOn" ,"isAsc":true},
        { "caption": "Date Desc", name: "-lastUpdatedOn", "isAsc":false },
    ]

    $scope.optiondata = { "caption": "Name A-Z", name: "named" };
    $scope.pagination={
        currentPage:1,
        pageSize:10,
        paginationPageSizes:[10, 25, 50, 75, 100],
        maxSize:5,
    }

    $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
        $sessionStorage.fromStateName = fromState.name
        $sessionStorage.fromParams = fromParams

    });


    var vm = this;
    
    $scope.refreshData = function(searchtext) {
        var data = $filter('filter')($scope.originalData,searchtext, undefined);
        $scope.allMetaCount=$scope.getResults($scope.pagination,data);
    };

    $scope.onShowDetail = function (data) {
        $rootScope.previousState = {};
        $rootScope.previousState.name = "datadiscovery"
        $rootScope.previousState.params = {};
        var type = "datapod"
        var uuid = data.uuid
        var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
        var stageparam = {};
        stageparam.id = uuid;
        stageparam.version = data.version;
        stageparam.mode = true;
        stageparam.returnBack = true;
        $state.go(stageName, stageparam);
    };
    DataDiscoveryService.getDatapodStats().then(function (response) { onSuccessGetDatapodStats(response.data) });
    var onSuccessGetDatapodStats = function (response) {
        $scope.allMetaCount = response;
        $scope.originalData=response;
        $scope.allMetaCount=$scope.getResults($scope.pagination,response);
        // vm.pager = {};
        // vm.setPage = setPage;
        // if (response.length > 0) {
        //     // initialize to page 1
        //     vm.setPage(1);
        // }

        // function setPage(page) {
        //     if (page < 1 || page > vm.pager.totalPages) {
        //         return;
        //     }
        //     // get pager object from service
        //     vm.pager = PagerService.GetPager(response.length, page);
        //     // get page of items
        //     $scope.allMetaCount = response.slice(vm.pager.startIndex, vm.pager.endIndex + 1);
        // }

    }//End onSuccessGetDatapodStats

    $scope.onPageChanged = function(){
        $scope.allMetaCount=$scope.getResults($scope.pagination,$scope.originalData);    
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
    $scope.orderByParm=function(){
        $scope.allMetaCount=$filter('orderBy')($scope.allMetaCount,$scope.optiondata.name);
       //  $scope.allMetaCount=$scope.getResults($scope.pagination,$scope.allMetaCount); 
    }
    function sortArrOfObjectsByParam(arrToSort /* array */, strObjParamToSortBy /* string */, sortAscending /* bool(optional, defaults to true) */) {
        if (sortAscending == undefined) sortAscending = true; // default to true

        if (sortAscending) {
          arrToSort.sort(function (a, b) {
            return a[strObjParamToSortBy] > b[strObjParamToSortBy];
          });
        } else {
          arrToSort.sort(function (a, b) {
            return a[strObjParamToSortBy] < b[strObjParamToSortBy];
          });
        }
      }

});
DatadiscoveryModule.filter('isoCurrencyWithK1', ["$filter", "iso4217", function ($filter, iso4217) {
	return function (amount, fraction) {
        return Math.abs(Number(amount)) >= 1.0e+18
        ? (Math.abs(Number(amount)) / 1.0e+18).toFixed(fraction) + "Qui"
       // fifteen Zeroes for Millions 
       : Math.abs(Number(amount)) >= 1.0e+15
        ? (Math.abs(Number(amount)) / 1.0e+15).toFixed(fraction) + "Qua"
       // twelve Zeroes for Millions 
       : Math.abs(Number(amount)) >= 1.0e+12
        ? (Math.abs(Number(amount)) / 1.0e+12).toFixed(fraction) + "T"
       // Nine Zeroes for Millions 
       : Math.abs(Number(amount)) >= 1.0e+9
       ? (Math.abs(Number(amount)) / 1.0e+9).toFixed(fraction) + "B"
       // Six Zeroes for Millions 
       : Math.abs(Number(amount)) >= 1.0e+6
   
       ?  ((Math.abs(Number(amount)) / 1.0e+6)) %1==0 ?(Math.abs(Number(amount)) / 1.0e+6) +"M" :(Math.abs(Number(amount)) / 1.0e+6).toFixed(fraction) + "M"
       // Three Zeroes for Thousands
       : Math.abs(Number(amount)) >= 1.0e+3
   
       ? ((Math.abs(Number(amount)) / 1.0e+3)) %1 ==0 ? (Math.abs(Number(amount)) / 1.0e+3)+ "K" :(Math.abs(Number(amount)) / 1.0e+3).toFixed(fraction) + "K"
   
       : Math.abs(Number(amount));
    }
}])



DatadiscoveryModule.factory('PagerService', PagerService)
function PagerService() {
    // service definition
    var service = {};

    service.GetPager = GetPager;

    return service;

    // service implementation
    function GetPager(totalItems, currentPage, pageSize) {
        // default to first page
        currentPage = currentPage || 1;

        // default page size is 10
        pageSize = pageSize || 10;

        // calculate total pages
        var totalPages = Math.ceil(totalItems / pageSize);

        var startPage, endPage;
        if (totalPages <= 4) {
            // less than 10 total pages so show all
            startPage = 1;
            endPage = totalPages;
        } else {
            // more than 10 total pages so calculate start and end pages
            if (currentPage <= 2) {
                startPage = 1;
                endPage = 4;
            } else if (currentPage + 2 >= totalPages) {
                startPage = totalPages - 3;
                endPage = totalPages;
            } else {
                startPage = currentPage - 2;
                endPage = currentPage + 1;
            }
        }

        // calculate start and end item indexes
        var startIndex = (currentPage - 1) * pageSize;
        var endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

        // create an array of pages to ng-repeat in the pager control
        var pages = _.range(startPage, endPage + 1);

        // return object with all pager properties required by the view
        return {
            totalItems: totalItems,
            currentPage: currentPage,
            pageSize: pageSize,
            totalPages: totalPages,
            startPage: startPage,
            endPage: endPage,
            startIndex: startIndex,
            endIndex: endIndex,
            pages: pages
        };
    }
}
