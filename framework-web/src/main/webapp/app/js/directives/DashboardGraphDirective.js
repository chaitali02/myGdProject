/**
 *
 */
DatavisualizationModule = angular.module('DatavisualizationModule')
DatavisualizationModule.directive('bubbleChart', function ($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
      onRightClick: "=",
      objdetail: "="
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.chartcolor = ["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"] //["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
        var keyname ;
        if($scope.column.keys[0].ref.type !="formula"){
          keyname = $scope.column.keys[0].attributeName;
        }else{
          keyname = $scope.column.keys[0].ref.name;
        }
        var columnname;
        if ($scope.column.values[0].ref.type == "formula") {
          columnname = $scope.column.values[0].ref.name
        } else {
          columnname = $scope.column.values[0].attributeName
        }
       // console.log(keyname)
        //console.log(columnname)
        $scope.data.sort(sortFactory.sortByProperty(keyname));
        var data = {};
        var key = {};
        var value = [];
        data.self = $scope;
        data.json = $scope.data;
        data.type = "scatter";
        key.x;
        if($scope.column.keys[0].ref.type !="formula"){
          key.x= $scope.column.keys[0].attributeName;
        }else{
          key.x= $scope.column.keys[0].ref.name;
        }
        value[0] = columnname //$scope.column.values[0].attributeName;
        key.value = value;
        data.keys = key;
        data.color = function (color, d) {
          return $scope.chartcolor[Math.floor((Math.random() * 15) + 0)] //'#'+Math.random().toString(16).substr(2,6);
        }
        data.contextmenu = function () {
          var data = {}
          data.vizpod = $scope.objdetail
          data.dataobj = arguments[0]
          console.log(arguments[0]);
          $scope.onRightClick(data);
        };


        var min;
        var max;
        if ($scope.data.length > 1) {
          min = d3.min($scope.data, function (d) {
            return d[columnname] - 100;
          });
          max = d3.max($scope.data, function (d) {
            return d[columnname] + 2000;
          });
        } else {
          min = 0
          max = d3.max($scope.data, function (d) {
            return d[columnname] + 2000;
          });
        }
        var rs = d3.scale.linear()
          .domain([min, max])
          .range([1, 10]);

        var chart = c3.generate({
          bindto: "." + $scope.chartid,
          point: {
            focus: {
              expand: {
                enabled: false
              } //End Expend
            }, //Enf Focus
            r: function (d) {
              return rs(d.value)
            } //End R
          }, //End Point
          axis: {
            x: {
              type: 'category',
              label: {
                text: keyname,
                position: 'outer-center'
              }, //End Lable
              tick: {
                //count : 4,
                culling: {
                  max: 4
                }, //End Culling
                rotate: 10,
                multiline: false
              }, //End Tick
              height: 50,
            } //End X
          }, //End Axis
          data: data,

          /* tooltip: {
               contents: function (d, defaultTitleFormat, defaultValueFormat, color) {
                   alert(color)
                  return ""
               }
           }*/
        }); //End c3.generate
      }); //End Watch
    } //End link
  }; //End Return
});

DatavisualizationModule.directive('heatMap', function ($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
      objdetail: "="
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.chartcolor = ["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"] //["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
        var keynameY;
        if($scope.column.keys[1].ref.type !='formula'){
          keynameY = $scope.column.keys[1].attributeName;
        }else{
          keynameY = $scope.column.keys[1].ref.name;
        }
        var keynameX;
        if($scope.column.keys[0].ref.type !='formula'){ 
          keynameX= $scope.column.keys[0].attributeName;
        }else{
          keynameX= $scope.column.keys[0].ref.name;
        }
        var columnname;
        $scope.valueColName;
        if ($scope.column.values[0].ref.type == "formula") {
          columnname = $scope.column.values[0].ref.name;
          $scope.valueColName = $scope.column.values[0].ref.name;
        } else {
          columnname = $scope.column.values[0].attributeName;
          $scope.valueColName = $scope.column.values[0].attributeName
        }

        var reA = /[^a-zA-Z]/g;
        var reN = /[^0-9]/g;
        function sortAlphaNum(propName) {
          return function (a, b) {
            var aA = a[propName].replace(reA, "");
            var bA = b[propName].replace(reA, "");
            if (aA === bA) {
              var aN = parseInt(a[propName].replace(reN, ""), 10);
              var bN = parseInt(b[propName].replace(reN, ""), 10);
              return aN === bN ? 0 : aN > bN ? 1 : -1;
            } else {
              return aA > bA ? 1 : -1;
            }
          }
        }

        var data1 = {}
        data1.labels = [];
        data1.datasets = [];
       // console.log(keynameX)
       // console.log(columnname)
        function indexOfDataset(array, value) {
          var index = -1;
          for (var j = 0; j < array.length; j++) {
            if (array[j].label == value) {
              index = j;
              break
            }
          }
          return index;
        }
        //  console.log(JSON.stringify($scope.data));
        var countX = 0;
        for (var i = 0; i < $scope.data.length; i++) {
          if (i == 0) {
            data1.labels[i] = $scope.data[i][keynameX];
            var dataset = {};
            dataset.data = [];
            dataset.label = $scope.data[i][keynameY];
            if ($scope.data[i][columnname] != 0) {
              dataset.data[0] = parseFloat($scope.data[i][columnname].toFixed(2));
            } else {
              dataset.data[0] = $scope.data[i][columnname];
            }
            data1.datasets[0] = dataset;
            countX = countX + 1
          } else {
            var index = data1.labels.indexOf($scope.data[i][keynameX]);
            if (index == -1) {
              data1.labels[countX] = $scope.data[i][keynameX];
              countX = countX + 1;
            }
            var indexY = indexOfDataset(data1.datasets, $scope.data[i][keynameY]);
            if (indexY == -1) {
              var dataset = {};
              dataset.data = [];
              dataset.label = $scope.data[i][keynameY];
              if ($scope.data[i][columnname] != 0) {
                dataset.data[0] = parseFloat($scope.data[i][columnname].toFixed(2));
              }
              else {
                dataset.data[0] = $scope.data[i][columnname];
              }
              data1.datasets[data1.datasets.length] = dataset;
            } else {
              data1.datasets[indexY].label = $scope.data[i][keynameY];
              if ($scope.data[i][columnname] != 0) {
                data1.datasets[indexY].data[data1.datasets[indexY].data.length] = parseFloat($scope.data[i][columnname].toFixed(2));
              }
              else {
                data1.datasets[indexY].data[data1.datasets[indexY].data.length] = $scope.data[i][columnname];
              }
            }
          }


        }
        // console.log(JSON.stringify(data1));
        data1.datasets.sort(sortAlphaNum('label'))
        var options = {
          // String - background color for graph
          backgroundColor: '#fff',

          // Boolean - whether each box in the dataset is outlined
          stroke: true,

          // Number - width of the outline stroke.
          strokePerc: 0.01,

          // String - the outline stroke color.
          strokeColor: "rgb(128,128,128)",

          // String - the outline stroke highlight color.
          highlightStrokeColor: "rgb(192,192,192)",

          // Boolean - whether to draw the heat map boxes with rounded corners
          rounded: true,

          // Number - the radius (as a percentage of size) of the rounded corners
          roundedRadius: 0.1,

          // Number - padding between heat map boxes (as a percentage of box size)
          paddingScale: 0.08,

          // String - "gradient", "palette"
          colorInterpolation: "gradient",

          // Array[String] - the colors used for the active color scheme.
          // Any number of colors is allowed.
          //  colors: ['rgba(255, 255, 255, .8)','rgba(255, 0, 0, 0.8)','rgba(0, 128, 0, 0.8)','rgba(0, 0, 255, 0.8)'],
          colors: ['rgba(255, 255, 255, 0.8)', 'rgba(255, 192, 203, 0.8)', 'rgba(255, 0, 0, 0.8)'],
          // Boolean - whether boxes change color on hover.
          colorHighlight: true,

          // Number - a floating point value which specifies how much lighter or
          // darker a color becomes when hovered, where 1 is no change, 
          // 0.9 is slightly darker, and 1.1 is slightly lighter.
          colorHighlightMultiplier: 0.92,

          // Boolean - Whether to draw labels on the boxes
          showLabels: true,

          // Number - the font size of the label as percentage of box height
          labelScale: 0.4,

          // String - label font family
          labelFontFamily: '"HelveticaNeue-Light", "Helvetica Neue Light", "Helvetica Neue", Helvetica, Arial, "Lucida Grande", sans-serif',

          // String - label font style
          labelFontStyle: "normal",

          // String - label font color
          labelFontColor: "rgba(0,0,0,0.5)",
          responsive: true,

        };
        var ctx = document.getElementById('heatmap').getContext('2d');
        $scope.newChart = new Chart(ctx).HeatMap(data1, options);
        console.log($scope.newChart)
        $(".heatmapid").append($scope.newChart.generateLegend())
      }); //End Watch
    },//End link
    template: `
        <!--
        <div style="transform: rotate(90deg);transform-origin: left bottom 0;margin-left: -15px;">
          <div>
           {{column.keys[1].attributeName}}
          </div>
        </div>
        <div   style="margin-top: -20px;">
          <canvas id="heatmap" width="300" height="140"></canvas>
        </div>
        </div>
        <div style="text-align:center;">
          {{column.keys[0].attributeName}}
        </div>
        <div style="text-align:center; margin-top:10px;" class="heatmapid">
        -->
        <div style="text-align:center;">
        {{column.keys[0].attributeName}}
      </div>
        <div class="row">
        
        <div  class="col-md-11">
          <canvas id="heatmap" width="300" height="140"></canvas>
        </div>
        <div class="col-md-1" style="transform: rotate(90deg);transform-origin:left bottom 0;">
          <div>
           {{column.keys[1].attributeName}}
          </div>
        </div>
        </div>
       
        <div style="text-align:center; margin-top:10px;" class="heatmapid">
        </div>
        
      `
  }; //End Return
});


DatavisualizationModule.directive('worldMap', function ($compile, $filter, $rootScope, sortFactory, $window) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
      onRightClick: "=",
      objdetail: "="
    },
    link: function ($scope, element, attrs) {

      var mapRender = function () {

        var keyname ;
        if($scope.column.keys[0].ref.type !='formula'){
          keyname= $scope.column.keys[0].attributeName;
        }else{
          keyname= $scope.column.keys[0].ref.name;
        }
        var columnname = [];
        for (var i = 0; i < $scope.column.values.length; i++) {
          if ($scope.column.values[i].ref.type == "formula") {
            columnname[i] = $scope.column.values[0].ref.name
          }
          else {
            columnname[i] = $scope.column.values[i].attributeName
          }
        }
        
        //alert(columnname[0])
        var minValue;
        var maxValue;
        if ($scope.data.length > 1) {
          minValue = d3.min($scope.data, function (d) {
            return d[columnname[0]];
          });
          maxValue = d3.max($scope.data, function (d) {
            return d[columnname[0]] + 100;
          });
        } else {
          minValue = 0
          maxValue = d3.max($scope.data, function (d) {
            return d[columnname[0]];
          });
        }

        var paletteScale = d3.scale.linear()
          .domain([minValue, maxValue])
          .range(["#EFEFFF", "#02386F"]); // blue color

        var dataset = {};
        $scope.data.forEach(function (item) {
          var iso = item[keyname]
          var coljson = {};
          var value = item[columnname[0]]
          coljson.fillColor = paletteScale(value)
          columnname.forEach(function (col) {
            coljson[col] = item[col]//isNaN(item[col])==false?$filter('number')(item[col]):item[col]
          });
          dataset[iso] = coljson //{ col: value,fillColor: paletteScale(value)};
        });
        //console.log(JSON.stringify(dataset));
        var map = new Datamap({
          element: document.getElementById($scope.chartid),
          scope: $scope.column.type == 'world-map' ? 'world' : 'usa',
          responsive: true,
          fills: {
            'USA': '#95A5A6',
            'IND': '#8c564b',
            'GBR': '#8c564b',
            'FRA': '#909CAE',
            'IRL': '#7f7f7f',
            defaultFill: '#B2D496'
          },
          data: dataset,
          done: function (datamap) {
            datamap.svg.selectAll('.datamaps-subunit').on('contextmenu', function (geography, data) {
             // console.log(geography.id);
            //  console.log(data);
           //   console.log(datamap.options.data[geography.id] || "");
              var d = {}
              d.vizpod = $scope.objdetail;
              d.dataobj = {};
              var value = datamap.options.data[geography.id] || ""
              d.dataobj.x = geography.id;
              if (value != "")
                d.dataobj.value = datamap.options.data[geography.id][columnname[0]] || "";
              else
                d.dataobj.value = "";
              $scope.onRightClick(d);
            });
          },
          geographyConfig: {
            popupOnHover: true,
            highlightOnHover: true,
            highlightFillColor: '#FC8D59',
            highlightBorderColor: 'rgba(250, 15, 160, 0.2)',
            highlightBorderWidth: 2,
            highlightBorderOpacity: 1,
            popupTemplate: function (geo, data) {
              var html = ['<div class="hoverinfo" style="padding:15px;"><ul class="list-inline"><strong>' + geo.properties.name + "</strong><br>"];
              columnname.forEach(function (col) {
                html.push('<li class="key" style="padding-left:0px;">' + col + '</li><li>' + $filter('number')(data[col]) + '</li><br>');
                //console.log(col)
              });
              html.push('</ul></div>');
              return html.join('');
            }
          },
        });
        //  map.legend();
        if ($scope.column.type != 'world-map')
          map.labels();
      }
      $scope.$watch('data', function (newValue, oldValue) {
        mapRender();
      });//End Watch
    },//End link

  }; //End Return
});


DatavisualizationModule.directive('graphDirective', function (CommonService, dagMetaDataService) {
  return {
    scope: {
      uuid: "=",
      version: "="
    },
    link: function ($scope, element, attrs) {
      var linkDistance = 140;
      var radiusVar = 8;
      $scope.zoomSize = 10;
      setTimeout(function () {
        $scope.$broadcast('rzSliderForceRender');
      }, 1000);

      $scope.getGraphData = function () {
        if ($scope.uuid && $scope.version) {
          $('#graphloader').show();
          $('.show-graph-body[graph-directive]').hide();
          $('#errorMsg').hide();
          var newUuid = $scope.uuid //+ "_" + $scope.version;
          CommonService.getGraphData(newUuid, $scope.version, "1")
            .then(function (result) {
              $('#graphloader').hide();
              if (result.data && result.data.nodes && result.data.nodes.length == 0) {
                $('#errorMsg').text('No Results Found').show();
                $('.show-graph-body[graph-directive]').hide();
              } else {
                $('.show-graph-body[graph-directive]').show();
              }
              $scope.graphDataStatus = false;
              $scope.showgraph = true;
              console.log(JSON.stringify(result.data))
              $scope.graphdata = result.data;
            }, function () {
              $('#errorMsg').text('Some Error Occured').show();
              $('#graphloader').hide();
              $('.show-graph-body[graph-directive]').hide();
            });
        }//End If
      }//End getGraphData();

      $scope.getGraphData();
      $scope.$on('refreshData', function () {
        $scope.getGraphData();
      });

      $scope.searchObjet = function (JSONObject, key, value) {
        for (var i = 0; i < JSONObject.length; i++) {
          if (JSONObject[i][key] == value) {
            return true;
          }
        }
        return false;
      }//End searchObjet();

      $scope.searcLinkObjet = function (JSONObject, valuedst, valuesrc) {
        for (var i = 0; i < JSONObject.length; i++) {
          if (JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc) {
            return true;
          }
        }
        return false;
      } //End searcLinkObjet(); 

      /* Start getNodeData*/
      $scope.getNodeData = function (data) {
        $('.show-graph-body[graph-directive]').hide();
        $('#graphloader').show();
        $('#errorMsg').hide();
        CommonService.getGraphData(data.uuid, data.version, "1").then(function (response) {
          onSuccess(response.data)
        }, function error(error) {
          $('#errorMsg').html('Some Error Occured').show();
          $('.show-graph-body[graph-directive]').hide();
          $('#graphloader').hide();
        });
        var onSuccess = function (response) {
          $('.show-graph-body[graph-directive]').show();
          $('#graphloader').hide();
          $scope.showgraph = true;
          $scope.graphDataStatus = false;
          console.log(JSON.stringify(response))
          var nodelen = $scope.graphdata.nodes.length;
          var linklen = $scope.graphdata.links.length;
          for (var j = 0; j < $scope.graphdata.nodes.length; j++) {
            delete $scope.graphdata.nodes[j].weight
            delete $scope.graphdata.nodes[j].index
            delete $scope.graphdata.nodes[j].x
            delete $scope.graphdata.nodes[j].y
            delete $scope.graphdata.nodes[j].px
            delete $scope.graphdata.nodes[j].py
          }
          var countnode = 0;
          var countlink = 0;
          for (var i = 0; i < response.nodes.length; i++) {
            if ($scope.searchObjet($scope.graphdata.nodes, "id", response.nodes[i].id) != true) {
              $scope.graphdata.nodes[nodelen + countnode] = response.nodes[i];
              countnode = countnode + 1;
            }
          }
          for (var j = 0; j < response.links.length; j++) {
            if ($scope.searchObjet($scope.graphdata.links, response.links[j].dst, response.links[j].src) != true) {
              $scope.graphdata.links[linklen + countlink] = response.links[j];
              countlink = countlink + 1;
            }
          }
          console.log(JSON.stringify($scope.graphdata))
          $scope.data = $scope.graphdata
        }
      } /* End getNodeData*/

      var drawGraph = function () {

        if ($scope.graphdata == null) {
          return false;
        }
        element.css('width', '100%');
        var menus = ["Show Details"];
        width = 1000,
          height = 500;
        color = d3.scale.category20();
        radius = d3.scale.sqrt()
          .range([0, 6]);
        d3.select("div.show-graph-body #network-graph-wrapper svg").remove();
        d3.select(".tooltipFocus").remove();
        svg = d3.select("div.show-graph-body #network-graph-wrapper").append("svg")
          .attr("class", ".dependancy")
          .attr("class", "graph-svg")
          //.attr("height", height)
          .on('click', function (e) {
            d3.selectAll(".vz-weighted_tree-tip").remove()
          });
        var div = d3.select("div.portlet-body").append("div")
          .attr("class", "tooltipFocus")
          .style("opacity", 0);
        var force = d3.layout.force()
          .size([width, height])
          .charge(-400)
          .linkDistance(function (d) {
            return radius(d.src.size) + radius(d.dst.size) + 20;
          });
        var graph = $scope.graphdata;
        var edges = [];
        graph.links.forEach(function (e) {
          // Get the source and target nodes
          var sourceNode = graph.nodes.filter(function (n) {
            return n.id === e.src;
          })[0],
            targetNode = graph.nodes.filter(function (n) {
              return n.id === e.dst;
            })[0];
          // Add the edge to the array
          edges.push({ source: sourceNode, target: targetNode, relationType: e.relationType });
        });
        force
          .nodes(graph.nodes)
          .links(edges)
          .linkDistance(linkDistance) //jitender200
          .on("tick", tick)
          .start();
        link = svg.selectAll(".link")
          .data(edges)
          .enter().append("g")
          .attr("class", "link")
        linkText = svg.selectAll(".link")
          .append("text")
          .attr("class", "link-label")
          .attr("font-family", "Arial, Helvetica, sans-serif")
          .attr("fill", "73879C")
          .style("font", "normal 11px Arial")
          .attr("dy", ".35em")
          .attr("text-anchor", "middle")
          .text(function (d) {
            return d.relationType;
          });
        link.append("line")
          .style("stroke-width", function (d) {
            return (d.bond * 2 - 1) * 2 + "px";
          })
        node = svg.selectAll(".dependancy .node5837fd0e27505225aa48bc3b")
          .data(graph.nodes)
          .enter().append("g")
          .attr("class", "node")
          .call(force.drag);
        node.append("circle")
          .attr("r", function (d) {
            return d.nodeType == "datapod1" ? radius(radiusVar > 15 ? radiusVar : 15) : radius(radiusVar);
          })
          .style("stroke-width", 0.5) // set the stroke width //jitender
          .style("stroke", "73879C") //jitender 73879C
          .style('fill', function (d) {
            return getColorCode(d) //function call for color code;
          })
          .on('contextmenu', function (d, i) {
            d3.event.preventDefault();
            var Nodedata = d
            d3.selectAll('.context-menu').data([1])
              .enter()
              .append('div')
              .attr('class', 'context-menu');
            // close menu
            d3.select('body').on('click.context-menu', function () {

              d3.select('.context-menu').style('display', 'none');
            });
            // this gets executed when a contextmenu event occurs
            d3.selectAll('.context-menu')
              .html('')
              .append('ul')
              .selectAll('li')
              .data(menus).enter()
              .append('li')
              .on('click', function (d) {
                createRouting(Nodedata, d);
                d3.select('.context-menu').style('display', 'none');

              })

              .text(function (d) {
                return d;
              });
            d3.select('.context-menu').style('display', 'none');
            // show the context menu
            d3.select('.context-menu')
              .style('left', (d3.event.pageX - 2) + 'px')
              .style('top', (d3.event.pageY - 2) + 'px')
              .style('display', 'block');
            d3.event.preventDefault();

          })

          .on("dblclick", function (d) {
            div.transition()
              .duration(500)
              .style("opacity", 0);
            createDataTip(d3.event.pageX, d3.event.pageY, d.nodeType, d.name, d.id, d.version, d.desc, d.createdOn, d.createdBy, d.active);
            d3.event.preventDefault();
          })
          .on("click", function (d) {
            div.transition()
              .duration(500)
              .style("opacity", 0);


          })
          .on("mouseover", function (d) {
            if ($('.portlet-body').width() <= 726) {

              div.transition()
                .duration(200)
                .style("opacity", .9);
              div.html("")
                .style("left", (d3.event.pageX - 90) + "px")
                .style("top", (d3.event.pageY - 128) + "px");
            } else {
              div.transition()
                .duration(200)
                .style("opacity", .9);
              div.html("")
                .style("left", (d3.event.pageX - 290) + "px")
                .style("top", (d3.event.pageY - 128) + "px");

            }

            d3.select("div.portlet-body").select(".tooltipFocus").append("div").attr("class", "tooltipcustom");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("span").attr("class", "one").style("float", "left").style("background-color", getColorCode(d));
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class", "header1").html("&nbsp&nbsp" + getCaption(d)).style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class", "header4").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header4").append("label").attr("class", "header2label").html("Id");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header4").append("span").attr("class", "header2span").html(d.id);
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class", "header2").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header2").append("label").attr("class", "header2label").html("Name");
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header2").append("span").attr("class", "header2span").html(d.name);
            d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class", "header3").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
          })
          .on("mouseout", function (d) {

            div.transition()
              .duration(500)
              .style("opacity", 0);
          });

        // .style("fill", function(d) { return color(d.title); });
        node.append("text")
          .attr("dy", ".35em")
          .attr("dx", "2.35em")
          .attr("text-anchor", "right")
          .attr('class', 'abe')
          //.style('fill',function(d){return (d.nodeType == 'datapod' ? 'white':'#4a6c8c')}) jitender
          .text(function (d) {

            // return getCaption(d);
            return d.name
          })
          .style('font-size', function (d) {
            return (d.nodeType == 'datapod1' ? 'larger' : '')
          });

        function getCaption(d) {
          try {
            var caption = dagMetaDataService.elementDefs[d.nodeType.toLowerCase()].caption;
          } catch (e) {
            var caption = d.name
          } finally {

          }
          return caption
        }
        function tick() {
          link.selectAll("line")
            .attr("x1", function (d) {
              return d.source.x;
            })
            .attr("y1", function (d) {
              return d.source.y;
            })
            .attr("x2", function (d) {
              return d.target.x;
            })
            .attr("y2", function (d) {
              return d.target.y;
            });
          linkText
            .attr("x", function (d) {
              return ((d.source.x + d.target.x) / 2);
            })
            .attr("y", function (d) {
              return ((d.source.y + d.target.y) / 2);
            });
          node.attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
          });
        }

        function getColorCode(d) {
          try {
            var color = dagMetaDataService.elementDefs[d.nodeType.toLowerCase()].color
          } catch (e) {
            if (d.nodeType.toLowerCase().indexOf("from_base") != -1) {
              var color = dagMetaDataService.elementDefs["from_base"].color
            }
            else {
              var color = '#000000'
            }


          } finally {

          }
          return color;
        }

        function findElement(arr, propName, propValue) {
          for (var i = 0; i < arr.length; i++) {
            if (arr[i][propName] == propValue) {
              result = arr[i];
              break;
            } else {
              result = -1;
            }

          }
          return result;
        }

        function createRouting(data, d) {
          if (d == "Show Details") {
            data.metaRef.name = data.name
            data.metaRef.type = data.nodeType
            dagMetaDataService.navigateTo(data.metaRef);

          }
        }
        function createDataTip(x, y, nodetype, h2, uuid, version, h5, h6, h7, h8) {
          var data = {};
          data.uuid = uuid;
          data.version = version;
          data.nodetype = nodetype;
          $scope.getNodeData(data);
        }

      };
      $scope.$watch('graphdata', function (newValue, oldValue) {
        drawGraph();
      }, true);

      $scope.$watch('zoomSize', function (data, oldValue) {
        var tempsize = data / 10;
        $('#network-graph-wrapper').css({
          '-webkit-transform': 'scale(' + tempsize + ')',
          '-moz-transform': 'scale(' + tempsize + ')',
          '-ms-transform': 'scale(' + tempsize + ')',
          '-o-transform': 'scale(' + tempsize + ')',
          'transform': 'scale(' + tempsize + ')'
        });
      });


      $scope.changeSliderForward = function () {
        $scope.zoomSize = $scope.zoomSize + 1;
      }
      $scope.changeSliderBack = function () {
        $scope.zoomSize = $scope.zoomSize - 1;
      }
    },

    template: `
        <div class="network-graph-zoom-slider col-md-1 col-md-offset-11">
          <div class="col-md-1" style="height:100px">
            <a ng-click="changeSliderForward()"><i class="fa fa-search-plus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
            <rzslider rz-slider-model="zoomSize" rz-slider-options="{floor: 1, ceil: 20,minLimit:1,maxLimit:20,hidePointerLabels:true,hideLimitLabels:true,vertical: true}"></rzslider>
              <a ng-click="changeSliderBack()"><i class="fa fa-search-minus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
          </div>
        </div>
        <div id="network-graph-wrapper"></div>
        `
  };
});


DatavisualizationModule.directive('multiSeriesChart', function ($compile, $rootScope, sortFactory) {
  return {
    scope: {
      data: "=",
      column: "=",
      chartid: "=",
      onRightClick: "=",
      objdetail: "="
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.chartcolor = ["#73c6b6", "#f8c471", "#d98880", "#7dcea0", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#82e0aa", "#f7dc6f", "#f0b27a", "#e59866"];
        var data =$scope.data;
        $scope.objdetail.dataPoint=data;
        var keyPro ;
        if($scope.column.keys[0].ref.type !='formula'){
          keyPro= $scope.column.keys[0].attributeName;
        }else{
          keyPro= $scope.column.keys[0].ref.name;
        }
        var valuePro ="";
        var groupPro =$scope.column.groups[0].attributeName;
        if($scope.column.values[0].ref.type == "formula") {
          valuePro = $scope.column.values[0].ref.name
        } 
        else {
          valuePro = $scope.column.values[0].attributeName
        }
       // $scope.data.sort(sortFactory.sortByProperty(groupPro));

        function isValuePresent(array, value) {
          var result = -1;
          if (array && array.length > 0) {
            for (var i = 0; i < array.length; i++) {
              if (array[i][0] == value) {
                result = i;
                break;
              }
            }
          }
          return result;
        }
        
        function prepareXs(ColArray,Col2Array){
          var xs={};
          if(ColArray && ColArray.length){
            for(var i=0;i<ColArray.length;i++){
             xs[ColArray[i][0]]=Col2Array[i][0];
            }
          }
         return xs;
        }

        var chartData = {};
        chartData.xs = {};
        chartData.columns = [];
        chartData.columns2=[];
        chartData.type =$scope.objdetail.vizpodInfo.type.split("-")[0];
        function prepareChartData(){	
           if(data && data.length >0){
             for(var i=0;i<data.length;i++){
               var keyArray=new Array();
               var groupArray=new Array();
               var keyIndex =isValuePresent(chartData.columns,data[i][groupPro]);
               if(keyIndex !=-1){ 
                 chartData.columns[keyIndex].push(data[i][valuePro]);
                 chartData.columns2[keyIndex].push(data[i][keyPro])
                }
               else{
                 var len=chartData.columns.length;
                 groupArray[0]=data[i][groupPro];
                 groupArray[1]=data[i][valuePro];
                 chartData.columns[len]=groupArray;
                 keyArray[0]="x"+len;
                 keyArray[1]=data[i][keyPro];
                 chartData.columns2[len]=keyArray;  
               }
             }
            }
          }
        prepareChartData();
        chartData.xs=prepareXs(chartData.columns,chartData.columns2);
        chartData.contextmenu = function (e) {
        //  console.log(chartData);
          var data = {}
          data.vizpod = $scope.objdetail
          data.dataobj = arguments[0]
        //  console.log(arguments[0]);
          $scope.onRightClick(data);
        };
        if(chartData.columns.length >0){
          chartData.columns=chartData.columns.concat(chartData.columns2);
          delete chartData.columns2;
        }
        console.log(JSON.stringify(chartData));
        var chart = c3.generate({
          bindto: "." + $scope.chartid,
          axis: {
            x: {
              type: 'category',
              tick: {
                //count : 4,
                culling: {
                  max: 4
                }, //End Culling
                rotate: 10,
                multiline: false
              }, //End Tick
            } //End X
          }, //End Axis
          color: {
            pattern: $scope.chartcolor
          },
          data: chartData
        }); //End c3.generate

      }); //End Watch

    } //End link

  }; //End Return

});

DatavisualizationModule.directive('scoreCard', function (COLORPALETTE) {
  return {
    scope: {
      data:"="
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
       var colorCodeArray= COLORPALETTE.Random_4;
       if($scope.data.vizpodInfo.colorPalette !=null && $scope.data.vizpodInfo.colorPalette !="Random" ){
          var str=$scope.data.vizpodInfo.colorPalette.replace(" ", "_");
          colorCodeArray=COLORPALETTE[str];
        }
        $scope.sCDetail={};
        if($scope.data.vizpodDetails.datapoints.length >0 && $scope.data.vizpodDetails.datapoints.length ==1){
          $scope.sCDetail.isSingleValue=true;
          $scope.sCDetail.name=$scope.data.vizpodInfo.values[0].attributeName;
          if($scope.data.vizpodInfo.values[0].ref.type =="formula"){
            $scope.sCDetail.name=$scope.data.vizpodInfo.values[0].ref.name;            
          }
          $scope.sCDetail.value=$scope.data.vizpodDetails.datapoints[0][$scope.sCDetail.name];
         // var randomnoCode = Math.floor((Math.random() * 17) + 0);
          var objStyle={
            "background-color":colorCodeArray[$scope.data.colNo-1],
            "color":"white"
          };
          $scope.sCDetail.objStyle=objStyle
        }
        else{
          $scope.sCDetail.isSingleValue=false;
        }
      }); //End Watch
    }, //End link
    templateUrl: 'views/score-card-template.html',
  }; //End Return
});

DatavisualizationModule.filter('isoCurrencyWithK1', ["$filter", "iso4217", function ($filter, iso4217) {
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

                          ? ((Math.abs(Number(amount)) / 1.0e+6)) % 1 == 0 ? (Math.abs(Number(amount)) / 1.0e+6) + "M" : (Math.abs(Number(amount)) / 1.0e+6).toFixed(fraction) + "M"
                          // Three Zeroes for Thousands
                          : Math.abs(Number(amount)) >= 1.0e+3

                              ? ((Math.abs(Number(amount)) / 1.0e+3)) % 1 == 0 ? (Math.abs(Number(amount)) / 1.0e+3) + "K" : (Math.abs(Number(amount)) / 1.0e+3).toFixed(fraction) + "K"

                              : Math.abs(Number(amount));
  }
}])

DatavisualizationModule.directive('formCard', function (COLORPALETTE) {
  return {
    scope: {
      data:"="
    },
    link: function ($scope, element, attrs) {
      $scope.$watch('data', function (newValue, oldValue) {
        $scope.fCDetail={};
        if($scope.data.vizpodDetails.datapoints.length >0 && $scope.data.vizpodDetails.datapoints.length ==1){
          $scope.fCDetail.isSingleValue=true;
          var fCData=[];
          for(var i=0;i<$scope.data.vizpodInfo.values.length;i++){
            var fCDataObj={};
            fCDataObj.colName=$scope.data.vizpodInfo.values[i].attributeName;
            if($scope.data.vizpodInfo.values[0].ref.type =="formula"){
              fCDataObj.colName=$scope.data.vizpodInfo.values[i].ref.name;            
            }
            fCDataObj.colValue=$scope.data.vizpodDetails.datapoints[0][fCDataObj.colName];
            fCData[i]=fCDataObj;
          } 
          $scope.fCDetail.fCData=fCData;
        }
        else{
          $scope.fCDetail.isSingleValue=false;
        }
      }); //End Watch
    }, //End link
    templateUrl: 'views/from-card-template.html',
  }; //End Return
});