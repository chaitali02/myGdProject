var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('trainResult', function ( $filter,$timeout, $rootScope, CommonService, dagMetaDataService, CF_META_TYPES, ModelService,uiGridConstants,sortFactory) {
  return {
    scope: {

      data: "=",

    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.$emit("dowloadAction",{ uuid: $scope.data.uuid, version: $scope.data.version,tab:0});
      $scope.isTrainResultProgess = false;
      $scope.isTabDisabled=false;
      $scope.activeTabIndex=0;
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
        displayName: 'Feature Importance  (%)',
        name: 'value',
        width:'22%',
        cellClass: 'text-center',
        sort: {
          direction: uiGridConstants.DESC,
         // priority: 0,
        },
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

      $scope.paginationTrainSet = {
        currentPage: 1,
        pageSize: 10,
        paginationPageSizes: [10, 25, 50, 75, 100],
        maxSize: 5,
      }

      $scope.filteredRowsTestSet;
      $scope.filteredRowsTrainSet;
      $scope.gridOptionsTrainSet = {
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
          style['height'] = (($scope.filteredRowsTestSet.length < 10 ? $scope.filteredRowsTestSet.length * 40 : 400) + 60) + 'px';
        } else {
          style['height'] = "200px"
        }
        return style;
      }
      $scope.getGridStyleTrainSet = function () {
        var style = {
          'margin-top': '10px',
          'margin-bottom': '10px',
        }
        if ($scope.filteredRowsTrainSet && $scope.filteredRowsTrainSet.length > 0) {
          style['height'] = (($scope.filteredRowsTrainSet.length < 10 ? $scope.filteredRowsTrainSet.length * 40 : 400) + 60) + 'px';
        } else {
          style['height'] = "200px"
        }
        return style;
      }
      $scope.gridOptionsTestSet.onRegisterApi = function (gridApi) {
        $scope.gridApiTestSet = gridApi;
        $scope.filteredRowsTestSet = $scope.gridApiTestSet.core.getVisibleRows($scope.gridApiTestSet.grid);
      };
      $scope.gridOptionsTrainSet.onRegisterApi = function (gridApi) {
        $scope.gridApiTrainSet = gridApi;
        $scope.filteredRowsTrainSet = $scope.gridApiTrainSet.core.getVisibleRows($scope.gridApiTrainSet.grid);
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
     
      $scope.calculateFeatureImportanceNonZero=function(data){
        $scope.title = [];
        var featureImportanceArr = $.map(data.featureImportance, function (el, e) {
          var obj = {};
          var val = parseFloat(el.toFixed(2)*100);
          obj.Importance =parseFloat(val.toFixed(2));
          obj.label = e;
          if (e.split('').length > 16) {
            obj.title = e.substring(0, 16) + "..";
            $scope.title.push(obj.title)
          }
          else {
            obj.title = e;
            if(obj.Importance >=1)
            	$scope.title.push(obj.title)
          }
          return obj;
        })
       // console.log(JSON.stringify(featureImportanceArr));
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
        $scope.featureImportanceNonZero=featureImportanceChartArr;
       /// console.log($scope.featureImportanceNonZero);
        //$scope.featureImportanceNonZero.sort(sortFactory.sortByProperty("Importance",'desc'));
        // console.log($scope.featureImportanceNonZero);
        

      }

      $scope.getTrainResult = function (data) {
        var uuid = data.uuid;
        var version = data.version;
        $scope.isTrainResultProgess = true;
        ModelService.getTrainResultByTrainExec(uuid, version, 'trainresult').then(function (response) { onSuccessGetTrainResultByTrainExec(response.data) });
        var onSuccessGetTrainResultByTrainExec = function (response) {
          $scope.modelresult = response;
          $scope.isTrainResultProgess = false;
          if($scope.modelresult.algoType == "SPARK" && $scope.modelresult.trainClass.indexOf("PCA") !=-1){
            $scope.isTabDisabled=true;
          }
          $scope.calculateFeatureImportanceNonZero(response);
          $scope.featureImportanceArr = $.map($scope.modelresult.featureImportance, function (el, e) {
            var obj = {};
            var val = parseFloat(el.toFixed(2) * 100)
            if (val == 0) {
              obj.value = parseFloat(val.toFixed(2));
            } else {
              obj.value = parseFloat(val.toFixed(2)) //+ "%";
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
          if($scope.modelresult.confusionMatrix){ 
            $scope.modelresult.confusionMatrix.map(function(row, i1) {
              y.push((sum($scope.modelresult.confusionMatrix.map(function(row) { return row[i1]; }))));
            });
          
            function getSum(total, num) {
              return total + num;
            } 
            y.push(y.reduce(getSum));
            $scope.modelresult.confusionMatrix.push(y)
          }
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
      $scope.refreshDataTrainSet = function (searchtext) {
        var data = $filter('filter')($scope.originalDataTrainSet, searchtext, undefined);
        $scope.featureImportanceArr = data;
        $scope.gridOptionsTrainSet.data = data;
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
      
      $scope.getTrainSet = function (data) {
        var uuid = data.uuid;
        var version = data.version;
        $scope.isProgessTrainTrainSet = true;
        $scope.isErrorTrainTrainSet = false;
        ModelService.getTrainSet(uuid, version, 'trainexec').then(function (response) { onSuccessGetTestSet(response.data) },function (response) { onError(response.data) });
        var onSuccessGetTestSet = function (response) {
          $scope.isProgessTrainTrainSet = false;
          $scope.isErrorTrainTrainSet = false;
          $scope.gridOptionsTrainSet.data = $scope.getResults($scope.paginationTrainSet,response);
          $scope.gridOptionsTrainSet.columnDefs = $scope.getColumnData(response);
          $scope.originalDataTrainSet = response;
        } //End onSuccessGetModelResult
        var onError=function(){
          $scope.isProgessTrainTrainSet = false;
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
      
        $scope.$emit("dowloadAction",{ uuid: $scope.data.uuid, version: $scope.data.version,tab:index});
        if(index == 3 && ($scope.gridOptionsTestSet.data.length ==0)){
        
          $scope.getTestSet({ uuid: $scope.data.uuid, version: $scope.data.version});
        }
        if([0,1,2].indexOf(index) !=-1 && ($scope.modelresult ==null)){
          $scope.getTrainResult({ uuid: $scope.data.uuid, version: $scope.data.version});
        }
        if(index == 4 &&$scope.gridOptionsTrainSet.data.length ==0){
          $scope.getTrainSet({ uuid: $scope.data.uuid, version: $scope.data.version});

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
      $scope.onPageChangedTrainSet = function () {
        $scope.gridOptionsTrainSet.data = $scope.getResults($scope.paginationTrainSet, $scope.originalDataTrainSet);
        console.log($scope.gridOptionsTrainSet.data);
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
    }); //End Watch
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
      title:"=",
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.chartcolor = ['#C28CC8']
        var title=$scope.title;
        // var featureImportanceArr = $.map($scope.data.featureImportance, function (el, e) {
        //   var obj = {};
        //   var val = parseFloat(el.toFixed(2)*100);
        //   obj.Importance =parseFloat(val.toFixed(2));
        //   obj.label = e;
        //   if (e.split('').length > 16) {
        //     obj.title = e.substring(0, 16) + "..";
        //     title.push(obj.title)
        //   }
        //   else {
        //     obj.title = e;
        //     title.push(obj.title)
        //   }
        //   return obj;
        // })
        // console.log(JSON.stringify(featureImportanceArr));
        // var featureImportanceChartArr = [];
        // var count = 0;
        // if (featureImportanceArr && featureImportanceArr.length > 0) {
        //   for (var i = 0; i < featureImportanceArr.length; i++) {
        //     if (featureImportanceArr[i].Importance >= 1) {
        //       featureImportanceChartArr[count] = featureImportanceArr[i]
        //       count = count + 1;
        //     }
        //   }
        // }
        var featureImportanceChartArr=$scope.data;
        var min;
        var max;
        if (featureImportanceChartArr.length > 1) {

          min = d3.min(featureImportanceChartArr, function (d) {
            return d['Importance'];
          });

          max = d3.max(featureImportanceChartArr, function (d) {
            return d['Importance'];
          });
        }
        else {
          min = 0
          max = d3.max(featureImportanceChartArr, function (d) {
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


InferyxApp.directive('rocCurveChart', function ($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {

        var margin = {top: 10, right: 61, bottom: 70, left: 61}, 
        width = 470 - margin.left - margin.right,
        height = 350 - margin.top - margin.bottom;

        // fpr for "false positive rate"
        // tpr for "true positive rate"
        var data=$scope.data.rocCurve
        // [{"specificity":"0","sensitivity":"0",},
        // {"specificity":"0.001","sensitivity":"0.0483871",},
        // {"specificity":"0.002","sensitivity":"0.0483871",},
        // {"specificity":"0.01","sensitivity":"0.23893805",},
        // {"specificity":"0.99","sensitivity":"1",},
        // {"specificity":"0.991","sensitivity":"1",},
        // {"specificity":"0.992","sensitivity":"1",},
        // {"specificity":"0.993","sensitivity":"1",},
        // {"specificity":"0.994","sensitivity":"1",},
        // {"specificity":"0.995","sensitivity":"1",},
        // {"specificity":"0.996","sensitivity":"1",},
        // {"specificity":"0.997","sensitivity":"1",},
        // {"specificity":"0.998","sensitivity":"1",},
        // {"specificity":"0.999","sensitivity":"1",},
        // {"specificity":"1","sensitivity":"1"}]
        
        var rocChartOptions = {
          "margin": margin,
          "width": width,
          "height": height,
          "interpolationMode": "basis",
          "fpr": "specificity",
          "tprVariables": [
            {
              "name": "sensitivity",
              "label": "Sensitivity"
            }
            
          ], 
          "animate": true,
          "smooth": true
        }
        rocChart("#roc", data, rocChartOptions)
        function rocChart(id, data, options) {

          // set default configuration
          var cfg = {
            "margin": {top: 30, right: 20, bottom: 70, left: 61},
            "width": 470,
            "height": 450,
            "interpolationMode": "basis",
            "ticks": undefined,
            "tickValues": [0, .1, .25, .5, .75, .9, 1],
            "fpr": "fpr",
            "tprVariables": [{
              "name": "tpr0",
            }], 
            "animate": true
          }
        
          //Put all of the options into a variable called cfg
          if('undefined' !== typeof options){
            for(var i in options){
              if('undefined' !== typeof options[i]){ cfg[i] = options[i]; }
            }//for i
          }//if
        
          var tprVariables = cfg["tprVariables"];
          // if values for labels are not specified
          // set the default values for the labels to the corresponding
          // true positive rate variable name
          tprVariables.forEach(function(d, i) {
            if('undefined' === typeof d.label){
              d.label = d.name;
            }
        
          })
        
          console.log("tprVariables", tprVariables);
        
        
          var interpolationMode = cfg["interpolationMode"],
              fpr = cfg["fpr"],
              width = cfg["width"],
              height = cfg["height"],
              animate = cfg["animate"]
        
          var format = d3.format('.2');
          var aucFormat = d3.format('.4r')
          
          var x = d3.scale.linear().range([0, width]);
          var y = d3.scale.linear().range([height, 0]);
          var color = d3.scale.category10() // d3.scale.ordinal().range(["steelblue", "red", "green", "purple"]);
        
          var xAxis = d3.svg.axis()
            .scale(x)
            .orient("top")
            .outerTickSize(0);
        
          var yAxis = d3.svg.axis()
            .scale(y)
            .orient("right")
            .outerTickSize(0);
        
          // set the axis ticks based on input parameters,
          // if ticks or tickValues are specified
          if('undefined' !== typeof cfg["ticks"]) {
            xAxis.ticks(cfg["ticks"]);
            yAxis.ticks(cfg["ticks"]);
          } else if ('undefined' !== typeof cfg["tickValues"]) {
            xAxis.tickValues(cfg["tickValues"]);
            yAxis.tickValues(cfg["tickValues"]);
          } else {
            xAxis.ticks(5);
            yAxis.ticks(5);
          }
        
          // apply the format to the ticks we chose
          xAxis.tickFormat(format);
          yAxis.tickFormat(format);
          
          // a function that returns a line generator
          function curve(data, tpr) {
        
             var lineGenerator = d3.svg.line()
              .interpolate(interpolationMode)
              .x(function(d) { return x(d[fpr]); })
              .y(function(d) { return y(d[tpr]); });
        
            return lineGenerator(data);
          }
        
          // a function that returns an area generator
          function areaUnderCurve(data, tpr) {
        
            var areaGenerator = d3.svg.area()
              .x(function(d) { return x(d[fpr]); })
              .y0(height)
              .y1(function(d) { return y(d[tpr]); });
        
            return areaGenerator(data);
          }
        
          var svg = d3.select("#roc")
            .append("svg")
              .attr("width", width + margin.left + margin.right)
              .attr("height", height + margin.top + margin.bottom)
              .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        
          x.domain([0, 1]);
          y.domain([0, 1]);
        
          svg.append("g")
              .attr("class", "x axis")
              .attr("transform", "translate(0," + height + ")")
              .call(xAxis)
              .append("text")            
                .attr("x", width / 2)
                .attr("y", 40 )
                .style("text-anchor", "middle")
                .text(" 1-Specificity ( False Positive Rate )")

          svg.append("g")
                .attr("class", "x axis1")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis)
                .append("text")            
                  .attr("x", width / 2)
                  .attr("font-weight","bold")
                  .attr("y", 70 )
                  .style("text-anchor", "middle")
                  .text("ROC Curve")
                  
          var xAxisG = svg.select("g.x.axis");
                    
          // draw the top boundary line
          xAxisG.append("line")
            .attr({
              "x1": -1,
              "x2": width + 1,
              "y1": -height,
              "y2": -height
            });
        
          // draw a bottom boundary line over the existing
          // x-axis domain path to make even corners
          xAxisG.append("line")
            .attr({
              "x1": -1,
              "x2": width + 1,
              "y1": 0,
              "y2": 0
            });
        
        
          // position the axis tick labels below the x-axis
          xAxisG.selectAll('.tick text')
            .attr('transform', 'translate(0,' + 25 + ')');
        
          // hide the y-axis ticks for 0 and 1
          xAxisG.selectAll("g.tick line")
            .style("opacity", function(d) {
              // if d is an integer
              return d % 1 === 0 ? 0 : 1;
            });
        
          svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")            
              .attr("transform", "rotate(-90)")
              .attr("y", -45)
              // manually configured so that the label is centered vertically 
              .attr("x", -35 - height/1.56)  
              .style("font-size","12px")              
              .style("text-anchor", "left")
              .text("Sensitivity ( True Positive Rate )");
        
          yAxisG = svg.select("g.y.axis");
        
          // add the right boundary line
          yAxisG.append("line")
            .attr({
              "x1": width,
              "x2": width,
              "y1": 0,
              "y2": height
            })
        
          // position the axis tick labels to the right of 
          // the y-axis and
          // translate the first and the last tick labels
          // so that they are right aligned
          // or even with the 2nd digit of the decimal number
          // tick labels
          yAxisG.selectAll("g.tick text")
            .attr('transform', function(d) {
              if(d % 1 === 0) { // if d is an integer
                return 'translate(' + -22 + ',0)';
              } else if((d*10) % 1 === 0) { // if d is a 1 place decimal
                return 'translate(' + -32 + ',0)';
              } else {
                return 'translate(' + -42 + ',0)';
              }
          })
        
          // hide the y-axis ticks for 0 and 1
          yAxisG.selectAll("g.tick line")
            .style("opacity", function(d) {
              // if d is an integer
              return d % 1 === 0 ? 0 : 1;
            });
        
          // draw the random guess line
          svg.append("line") 
            .attr("class", "curve")         
            .style("stroke", "black")
            .attr({
              "x1": 0,
              "x2": width,
              "y1": height,
              "y2": 0
            })
            .style({
              "stroke-width": 2,
              "stroke-dasharray": "8",
              "opacity": 0.4
            })
        
          // draw the ROC curves
          function drawCurve(data, tpr, stroke){
        
            svg.append("path")
              .attr("class", "curve")
              .style("stroke", stroke)
              .attr("d", curve(data, tpr))
              .on('mouseover', function(d) {
        
                var areaID = "#" + tpr + "Area";
                svg.select(areaID)
                  .style("opacity", .4)
        
                var aucText = "." + tpr + "text"; 
                svg.selectAll(aucText)
                  .style("opacity", .9)
              })
              .on('mouseout', function(){
                var areaID = "#" + tpr + "Area";
                svg.select(areaID)
                  .style("opacity", 0)
        
                var aucText = "." + tpr + "text"; 
                svg.selectAll(aucText)
                  .style("opacity", 0)
        
        
              });
          }
        
          // draw the area under the ROC curves
          function drawArea(data, tpr, fill) {
            svg.append("path")
              .attr("class", "area")
              .attr("id", tpr + "Area")
              .style({
                "fill": fill,
                "opacity": 0
              })
              .attr("d", areaUnderCurve(data, tpr))
          }
        
          function drawAUCText(auc, tpr, label) {
        
            svg.append("g")
              .attr("class", tpr + "text")
              .style("opacity", 0)
              .attr("transform", "translate(" + .5*width + "," + .79*height + ")")
              .append("text")
                .text(label)
                .style({
                  "fill": "black",
                  "font-size": 18
                });
        
            svg.append("g")
              .attr("class", tpr + "text")
              .style("opacity", 0)
              
              .attr("transform", "translate(" + .5*width + "," + .88*height + ")")
              .append("text")
                .text("AUROC = " + aucFormat(auc))
                .style({
                  "fill": "black",
                  "font-size": 18
                });
        
          }
          
          // calculate the area under each curve
          tprVariables.forEach(function(d){
            var tpr = d.name;
            var points = generatePoints(data, fpr, tpr);
            var auc = calculateArea(points);
            d["auc"] = auc;
          })
        
          console.log("tprVariables", tprVariables);
        
          // draw curves, areas, and text for each 
          // true-positive rate in the data
          tprVariables.forEach(function(d, i){
            console.log("drawing the curve for", d.label)
            console.log("color(", i, ")", color(i));
            var tpr = d.name;
            drawArea(data, tpr, color(i))
            drawCurve(data, tpr, color(i));
            drawAUCText(d.auc, tpr, d.label);
          })
        
          ///////////////////////////////////////////////////
          ////// animate through areas for each curve ///////
          ///////////////////////////////////////////////////
        
          if(animate && animate !== "false") {
            //sort tprVariables ascending by AUC
            var tprVariablesAscByAUC = tprVariables.sort(function(a, b) {
              return a.auc - b.auc;
            })
        
            console.log("tprVariablesAscByAUC", tprVariablesAscByAUC);
            
            for(var i = 0; i < tprVariablesAscByAUC.length; i++) {
              areaID = "#" + tprVariablesAscByAUC[i]["name"] + "Area";
              svg.select(areaID)
                .transition()
                  .delay(2000 * (i+1))
                  .duration(250)
                  .style("opacity", .4)
                .transition()
                  .delay(2000 * (i+2))
                  .duration(250)
                  .style("opacity", 0)
        
              textClass = "." + tprVariablesAscByAUC[i]["name"] + "text";
              svg.selectAll(textClass)
                .transition()
                  .delay(2000 * (i+1))
                  .duration(250)
                  .style("opacity", .9)
                .transition()
                  .delay(2000 * (i+2))
                  .duration(250)
                  .style("opacity", 0)
            }  
          }
        
          ///////////////////////////////////////////////////
          ///////////////////////////////////////////////////
          ///////////////////////////////////////////////////
        
          function generatePoints(data, x, y) {
            var points = [];
            data.forEach(function(d){
              points.push([ Number(d[x]), Number(d[y]) ])
            })
            return points;
          }
        
          // numerical integration
          function calculateArea(points) {
            var area = 0.0;
            var length = points.length;
            if (length <= 2) {
              return area;
            }
            points.forEach(function(d, i) {
              var x = 0,
                  y = 1;
        
              if('undefined' !== typeof points[i-1]){
                area += (points[i][x] - points[i-1][x]) * (points[i-1][y] + points[i][y]) / 2;
              }
              
            });
            return area;
          }
        
        } // rocChart
      }); //End Watch
    } //End link
  }; //End Return
});