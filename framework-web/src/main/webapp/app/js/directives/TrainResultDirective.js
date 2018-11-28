var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('trainResult', function ($timeout,$rootScope,CommonService,dagMetaDataService,CF_META_TYPES) {
    return {
        scope: {
            execjson: "=",
        },
        link: function (scope, element, attrs) {
            
        },
        templateUrl: 'views/train-result-template.html',
    };
});

InferyxApp.directive('barChartHorizontal', function($compile, $rootScope, sortFactory) {
    return {
      scope: {
        data: "=",
        column: "=",
        chartid: "=",
      },
      link: function($scope, element, attrs) {
        $scope.$watch('data', function(newValue, oldValue) {
          $scope.chartcolor = ["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"] //["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
          var title=[];
          var featureImportanceArr = $.map($scope.data.featureImportance,function(el,e) { 
            var obj={};
            obj.label= e;
            if(e.split('').length > 16) {
            obj.title=e.substring(0, 16) + "..";
            title.push( obj.title)
            }else{
                obj.title=e;
                title.push( obj.title)
            }
            obj.value= parseFloat(el.toFixed(2));
            return obj  ; 
            })
         console.log(JSON.stringify(featureImportanceArr));
         var min;
         var max;
         if (featureImportanceArr.length > 1) {
           min = d3.min(featureImportanceArr, function(d) {
             return d['value'];
           });
           max = d3.max(featureImportanceArr, function(d) {
             return  d['value'];
           });
         } else {
           min = 0
           max = d3.max(featureImportanceArr, function(d) {
             return  d['value'];
           });
         }
         console.log(min)
         console.log(max)
          var chart = c3.generate({
            bindto: "#chart"+$scope.chartid,
            
            data: {
              json: featureImportanceArr,
              type: 'bar',
              keys: {
                x: 'label',
                value: ['value']
            }
            },
            zoom: {
              enabled: false
            },
            axis: {
                 rotated: true,
              x: {
                type: 'category',
                tick: {
                    format: function (x) { return title[x]} ,
                    culling: {
                        max: 4 // the number of tick texts will be adjusted to less than this value
                    }
                    // for normal axis, default on
                    // for category axis, default off
                }
              },
             
            },
            tooltip: {
                format: {
                    title: function (d) { return featureImportanceArr[d].label; },
                    // value: function (value, ratio, id) {
                    //     var format = id === 'data1' ? d3.format(',') : d3.format('$');
                    //     return format(value);
                    // }
        //            value: d3.format(',') // apply this format to both y and y2
                }
            }
          });
        }); //End Watch
      } //End link
    }; //End Return
  });