/**
 *
 */
DatadiscoveryModule= angular.module('DatadiscoveryModule');


DatadiscoveryModule.controller('DataDiscoveryController', function($state,dagMetaDataService,PagerService,$stateParams,$rootScope,$scope,$sessionStorage,DataDiscoveryService) {
       $scope.optionsort=[
		{"caption":"Name A-Z",name:"name"},
		{"caption":"Name Z-A",name:"-name"},
		{"caption":"Date Asc",name:"lastUpdatedOn"},
		{"caption":"Date Desc",name:"-lastUpdatedOn"},
		]
	$scope.optiondata={"caption":"Name A-Z",name:"named"};
	$scope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
      	console.log(fromParams)
		$sessionStorage.fromStateName=fromState.name
	    $sessionStorage.fromParams=fromParams

    });
	var vm = this;
  $scope.onShowDetail = function(data){
      $rootScope.previousState={};
      $rootScope.previousState.name="datadiscovery"
      $rootScope.previousState.params={};
      var type="datapod"
      var uuid= data.uuid
      var stageName=dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
      var stageparam={};
      stageparam.id=uuid;
      stageparam.version=data.version;
      stageparam.mode=true;
      stageparam.returnBack=true;
      $state.go(stageName,stageparam);
  };
	DataDiscoveryService.getDatapodStats().then(function(response){onSuccessGetDatapodStats(response.data)});
    var onSuccessGetDatapodStats=function(response){
    	    //console.log(JSON.stringify(response))
	    	$scope.allMetaCount=response;
            vm.pager = {};
            vm.setPage = setPage;
            if(response.length >0){
            // initialize to page 1
             vm.setPage(1);
            }

            function setPage(page) {
                if (page < 1 || page > vm.pager.totalPages) {
                   return;
                }
                // get pager object from service
                vm.pager=PagerService.GetPager(response.length, page);
                // get page of items
                $scope.allMetaCount=response.slice(vm.pager.startIndex, vm.pager.endIndex + 1);
            }

    }//End onSuccessGetDatapodStats


});

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
