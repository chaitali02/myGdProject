SystemMonitoringModule = angular.module('SystemMonitoringModule');
SystemMonitoringModule.directive('systemMonPieChart', function($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      columns: "=",
      chartid: "=",
      type:"="
    },
    link: function($scope, element, attrs) {
      $scope.$watch('data', function(newValue, oldValue) {

        var dataJson={};
        var keys={}
        dataJson.json=$scope.data;
        keys.value=$scope.columns
        dataJson.keys=keys
        dataJson.type=$scope.type
        var chart = c3.generate({
        bindto: "#" + $scope.chartid,
        data:dataJson,
        color: {
          pattern: ["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"]
        },
        pie: {
          label: {
            format: function(value, ratio, id) {
              return value;
            }
          }
        }
      });
      }); //End Watch
    } //End link
  }; //End Return
});

SystemMonitoringModule.directive('systemMonDonutChart', function($compile, $rootScope, sortFactory,dagMetaDataService) {
  return {
    scope: {
      data: "=",
      columns: "=",
      chartid: "=",
      type:"="
    },
    link: function($scope, element, attrs) {
      $scope.$watch('data', function(newValue, oldValue) {
        var dataJson={};
        var keys={}
        dataJson.json=$scope.data;
        keys.value=$scope.columns
        dataJson.keys=keys
        dataJson.type=$scope.type
        dataJson.color = function(color, d) {
          var color;
          try{
            color=dagMetaDataService.statusDefs[d].color
          }
          catch(e){

          }
          return color //'#'+Math.random().toString(16).substr(2,6);
        };
        var chart = c3.generate({
        bindto: "#" + $scope.chartid,
        data:dataJson,
        donut: {
          label: {
            format: function(value, ratio, id) {
              return value;
            }
          }
        }
      });
      }); //End Watch
    } //End link
  }; //End Return
});
