var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('trainResult', function ($timeout, $rootScope, CommonService, dagMetaDataService, CF_META_TYPES, ModelService) {
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
        useExternalSorting: true,
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
        cellClass: 'text-center',
        headerCellClass: 'text-center',
      },
      {
        displayName: 'Feature Importance',
        name: 'value',
        cellClass: 'text-center',
        headerCellClass: 'text-center',
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
        } //End onSuccessGetModelResult
      }

      $scope.getTrainResult({ uuid: $scope.data.uuid, version: $scope.data.version });
      $scope.go = function (index) {
        $scope.activeTabIndex = index;
        if (index == 1) {
          $timeout(function () {
            $scope.showChart = true;
          }, 100);
        } else {
          $scope.showChart = false;
        }
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
          var val = parseFloat(el.toFixed(2));
          obj.Importance = val * 100;
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
            height: 500
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