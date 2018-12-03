var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('trainResult', function ( $filter,$timeout, $rootScope, CommonService, dagMetaDataService, CF_META_TYPES, ModelService) {
  return {
    scope: {

      data: "=",

    },
    link: function ($scope, element, attrs) {
      $scope.isTrainResultProgess = false;
      $scope.filteredRows;
      $scope.gridOptions = {
        rowHeight: 40,
        useExternalPagination: true,
        exporterMenuPdf: false,
        enableSorting: true,
        useExternalSorting: false,
        enableFiltering: false,
        enableRowSelection: true,
        enableSelectAll: true,
        enableGridMenu: true,
        fastWatch: true,
        columnDefs: [],
      };
      $scope.gridOptions.columnDefs = [{
        displayName: 'Feature Name',
        name: 'label',
        width:'30%',
        // cellClass: 'text-center',
    //    headerCellClass: 'text-center',
      },
      {
        displayName: 'Feature Importance',
        name: 'value',
        width:'20%',
        cellClass: 'text-center',
      //  headerCellClass: 'text-center',
      }];

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
      $scope.gridOptions.onRegisterApi = function (gridApi) {
        $scope.gridApi = gridApi;
        $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
      };

      $scope.paginationTestSet = {
        currentPage: 1,
        pageSize: 10,
        paginationPageSizes: [10, 25, 50, 75, 100],
        maxSize: 5,
      }

      $scope.filteredRowsTestSet;
      $scope.gridOptionsTestSet = {
        rowHeight: 40,
        useExternalPagination: true,
        exporterMenuPdf: false,
        enableSorting: true,
        useExternalSorting: false,
        enableFiltering: false,
        enableRowSelection: true,
        enableSelectAll: true,
        enableGridMenu: true,
        fastWatch: true,
        columnDefs: [],
        data:[],
      };
     
   
      $scope.getGridStyleTestSet = function () {
        var style = {
          'margin-top': '10px',
          'margin-bottom': '10px',
        }
        if ($scope.filteredRowsTestSet && $scope.filteredRowsTestSet.length > 0) {
          style['height'] = (($scope.filteredRowsTestSet.length < 10 ? $scope.filteredRowsTestSet.length * 40 : 400) + 40) + 'px';
        } else {
          style['height'] = "100px"
        }
        return style;
      }
      $scope.gridOptionsTestSet.onRegisterApi = function (gridApi) {
        $scope.gridApiTestSet = gridApi;
        $scope.filteredRowsTestSet = $scope.gridApiTestSet.core.getVisibleRows($scope.gridApiTestSet.grid);
      };
     
      $rootScope.refreshMoldeResult = function () {
        $scope.modelresult = null;
        $scope.getTrainResult({ uuid: $scope.data.uuid, version: $scope.data.version });
      }
      $scope.refreshData = function (searchtext) {
        var data = $filter('filter')($scope.originalData, searchtext, undefined);
        $scope.featureImportanceArr = data;
        $scope.gridOptions.data = data;
      };


      $scope.getTrainResult = function (data) {
        var uuid = data.uuid;
        var version = data.version;
        $scope.isTrainResultProgess = true;
        ModelService.getTrainResultByTrainExec(uuid, version, 'trainresult').then(function (response) { onSuccessGetTrainResultByTrainExec(response.data) });
        var onSuccessGetTrainResultByTrainExec = function (response) {
          $scope.modelresult = response;
          $scope.isTrainResultProgess = false;
          $scope.featureImportanceArr = $.map($scope.modelresult.featureImportance, function (el, e) {
            var obj = {};
            var val = parseFloat(el.toFixed(2) * 100)
            if (val == 0) {
              obj.value = parseFloat(val.toFixed(2));
            } else {
              obj.value = parseFloat(val.toFixed(2)) + " %";
            }

            obj.label = e;
            return obj;
          });
          $scope.originalData = $scope.featureImportanceArr;
          $scope.gridOptions.data = $scope.featureImportanceArr;
          var conMatrix=[];
          var y=[];
          //$scope.modelresult.confusionMatrix;
          if(  $scope.modelresult.confusionMatrix !=null && $scope.modelresult.confusionMatrix.length ==2){
            for(var i=0;i<$scope.modelresult.confusionMatrix.length;i++){
              var x = new Array();
              for(var j=0;j<$scope.modelresult.confusionMatrix[i].length;j++){
                var total;
                if(j==0){
                  total=$scope.modelresult.confusionMatrix[i][j]
                }else{
                  total=total+$scope.modelresult.confusionMatrix[i][j];
                }
                x.push($scope.modelresult.confusionMatrix[i][j]);
                if(j==$scope.modelresult.confusionMatrix[i].length-1){
                  x.push(total);
                }
              }
              $scope.modelresult.confusionMatrix[i]=x;
             
            }
          }
          console.log($scope.modelresult.confusionMatrix)
          var sum = function(arr) {
            console.log(arr)
            return arr.reduce(function(a, b){ return a + b; }, 0);
          };

          // vertical sums      
          $scope.modelresult.confusionMatrix.map(function(row, i1) {
            y.push((sum($scope.modelresult.confusionMatrix.map(function(row) { return row[i1]; }))));
          });
          function getSum(total, num) {
            return total + num;
          } 
          y.push(y.reduce(getSum));
          $scope.modelresult.confusionMatrix.push(y)
        } //End onSuccessGetModelResult
      }
      $scope.getClassAcutalTrue=function(data,matrixArr,index){
        if(index ==0){
          return'actual-true';
        }
        if(index !=matrixArr.length-1 && data >0){
          return'actual-false';
        }
      }
      $scope.getClassAcutalFalse=function(data,matrixArr,index){
        if(index ==0 &&  data >0){
          return'actual-false';
        }
        if(index !=matrixArr.length-1){
          return'actual-true';
        }
      }
      $scope.getTrainResult({ uuid: $scope.data.uuid, version: $scope.data.version});
      $scope.refreshDataTestSet = function (searchtext) {
        var data = $filter('filter')($scope.originalDataTestSet, searchtext, undefined);
        $scope.featureImportanceArr = data;
        $scope.gridOptionsTestSet.data = data;
      };
      $scope.getTestSet = function (data) {
        var uuid = data.uuid;
        var version = data.version;
        $scope.isProgessTrainSet = true;
        $scope.isErrorTrainTestSet = false;
        ModelService.getTestSet(uuid, version, 'trainexec').then(function (response) { onSuccessGetTestSet(response.data) },function (response) { onError(response.data) });
        var onSuccessGetTestSet = function (response) {
          $scope.isProgessTrainSet = false;
          $scope.isErrorTrainTestSet = false;
          $scope.gridOptionsTestSet.data = $scope.getResults($scope.paginationTestSet,response);
          $scope.gridOptionsTestSet.columnDefs = $scope.getColumnData(response);
          $scope.originalDataTestSet = response;
        } //End onSuccessGetModelResult
        var onError=function(){
          $scope.isProgessTrainSet = false;
          $scope.isErrorTrainTestSet = true;
        }
        
      }
     
      $scope.go = function (index) {
        $scope.activeTabIndex = index;
        
        if(index == 1){
          $timeout(function () {
            $scope.showChart = true;
          }, 100);
        } 
        else{
          $scope.showChart = false;
        }

        if(index == 3 && ($scope.gridOptionsTestSet.data.length ==0)){
          $scope.getTestSet({ uuid: $scope.data.uuid, version: $scope.data.version});
        }
        if([0,1,2].indexOf(index) !=-1 && ($scope.modelresult ==null)){
          $scope.getTrainResult({ uuid: $scope.data.uuid, version: $scope.data.version});
        }
      }

      $scope.selectPage = function (pageNo) {
        $scope.paginationTestSet.currentPage = pageNo;
      };

      $scope.onPerPageChange = function () {
        $scope.$scope.paginationTestSet.currentPage = 1;
        $scope.gridOptions.data = $scope.getResults($scope.paginationTestSet, $scope.originalDataTestSet)
      }

      $scope.onPageChanged = function () {
        $scope.gridOptionsTestSet.data = $scope.getResults($scope.paginationTestSet, $scope.originalDataTestSet);
        console.log($scope.gridOptionsTestSet.data);
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
        });
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
    },
    templateUrl: 'views/train-result-template.html',
  };
});

InferyxApp.directive('barChartHorizontal', function ($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.chartcolor = ['#C28CC8']
        var title = [];
        var featureImportanceArr = $.map($scope.data.featureImportance, function (el, e) {
          var obj = {};
          var val = parseFloat(el.toFixed(2)*100);
          obj.Importance =parseFloat(val.toFixed(2));
          obj.label = e;
          if (e.split('').length > 16) {
            obj.title = e.substring(0, 16) + "..";
            title.push(obj.title)
          }
          else {
            obj.title = e;
            title.push(obj.title)
          }
          return obj;
        })
        console.log(JSON.stringify(featureImportanceArr));
        var featureImportanceChartArr = [];
        var count = 0;
        if (featureImportanceArr && featureImportanceArr.length > 0) {
          for (var i = 0; i < featureImportanceArr.length; i++) {
            if (featureImportanceArr[i].Importance >= 1) {
              featureImportanceChartArr[count] = featureImportanceArr[i]
              count = count + 1;
            }
          }
        }
        var min;
        var max;
        if (featureImportanceArr.length > 1) {

          min = d3.min(featureImportanceArr, function (d) {
            return d['Importance'];
          });

          max = d3.max(featureImportanceArr, function (d) {
            return d['Importance'];
          });
        }
        else {
          min = 0
          max = d3.max(featureImportanceArr, function (d) {
            return d['Importance'];
          });
        }
        var chart = c3.generate({
          bindto: "#chart" + $scope.chartid,
          size: {
            height: 400
          },
          data: {
            json: featureImportanceChartArr,
            type: 'bar',
            keys: {
              x: 'label',
              value: ['Importance']
            }
          },
          color: {
            pattern: $scope.chartcolor
          },
          zoom: {
            enabled: false
          },
          axis: {
            rotated: true,
            x: {
              type: 'category',
              tick: {
                format: function (x) { return title[x] },
                // culling: {
                //     max: 10 // the number of tick texts will be adjusted to less than this value
                // }
              }
            },
            y: {
              tick: {
                // format: d3.format(" ,% ")   
                format: function (d) { return d + " %" }
              }
            }
          },
          tooltip: {
            format: {
              title: function (d) { return featureImportanceChartArr[d].label },
              // value: function (value, ratio, id) {
              //     var format = id === 'data1' ? d3.format(',') : d3.format('$');
              //     return format(value);
              // }
              //value: d3.format(',') // apply this format to both y and y2
            }
          }
        });
      }); //End Watch
    } //End link
  }; //End Return
});