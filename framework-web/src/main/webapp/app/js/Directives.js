/***
GLobal Directives
***/

// Route State Load Spinner(used on page or content load)
var InferyxApp = angular.module("InferyxApp");
// InferyxApp.directive('ngRightClick', function($parse) {
//     return function(scope, element, attrs) {
//         var fn = $parse(attrs.ngRightClick);
//         element.bind('contextmenu', function(event) {
//             scope.$apply(function() {
//                 event.preventDefault();
//                 fn(scope, {$event:event});
//             });
//         });
//     };
// });
InferyxApp.directive('ngEsc', function () {
  return function (scope, element, attrs) {
      element.bind("keydown keypress keyup", function (event) {
          if(event.which === 27) {
              scope.$apply(function (){
                  scope.$eval(attrs.ngEsc);
              });

              event.preventDefault();
          }
      });
  };
});
InferyxApp.directive('ngSpinnerBar', ['$rootScope', '$state',
  function ($rootScope, $state) {
    return {
      link: function (scope, element, attrs) {
        // by defult hide the spinner bar
        element.addClass('hide'); // hide spinner bar by default

        // display the spinner bar whenever the route changes(the content part started loading)
        $rootScope.$on('$stateChangeStart', function () {
          element.removeClass('hide'); // show spinner bar
        });

        // hide the spinner bar on rounte change success(after the content loaded)
        $rootScope.$on('$stateChangeSuccess', function (event) {
          element.addClass('hide'); // hide spinner bar
          $('body').removeClass('page-on-load'); // remove page loading indicator

          Layout.setAngularJsSidebarMenuActiveLink('match', null, event.currentScope.$state); // activate selected link in the sidebar menu

          // auto scorll to page top
          setTimeout(function () {
            App.scrollTop(); // scroll to the top on content load
          }, $rootScope.settings.layout.pageAutoScrollOnLoad);
        });

        // handle errors
        $rootScope.$on('$stateNotFound', function () {
          element.addClass('hide'); // hide spinner bar
        });

        // handle errors
        $rootScope.$on('$stateChangeError', function () {
          element.addClass('hide'); // hide spinner bar
        });
      }
    };
  }
])

// Handle global LINK click
InferyxApp.directive('a', function () {
  return {
    restrict: 'E',
    link: function (scope, elem, attrs) {
      if (attrs.ngClick || attrs.href === '' || attrs.href === '#') {

        elem.on('click', function (e) {
          e.preventDefault(); // prevent link click for above criteria
        });
      }
    }
  };
});

// Handle Dropdown Hover Plugin Integration
InferyxApp.directive('dropdownMenuHover', function () {
  return {
    link: function (scope, elem) {
      elem.dropdownHover();
    }
  };
});
/*InferyxApp.directive('selectmenu', function() {
    return {
        restrict: 'A',
        link: function(scope, elem, attrs) {

        	 elem.on('click', function(e) {

        		 elem.parent().each(function( index ){

        			 $(this).find("li").removeClass("active");
        		 })
        		elem.attr("class","active");

             });


        }
    };
});*/
InferyxApp.directive('tooltip', function () {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {

      $(element).hover(function () {
        // on mouseenter
        $(element).tooltip('show');
      }, function () {
        //alert("erer")
        // on mouseleave
        $(element).tooltip('hide');
      });
    }
  };
});


InferyxApp.directive('select2', function ($timeout) {
  return {
    restrict: 'AC',
    link: function (scope, element, attrs) {
      $timeout(function () {
        element.removeAttr('style')
        element.show();
        $(element).select2();
      });
    }
  };
})



InferyxApp.directive('graphDirective', function (CommonService, dagMetaDataService) {
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
        }
      }
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
      }

      $scope.searcLinkObjet = function (JSONObject, valuedst, valuesrc) {
        for (var i = 0; i < JSONObject.length; i++) {
          if (JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc) {
            return true;
          }
        }
        return false;
      }
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
          //console.log(sourceNode);
          //debugger;
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

InferyxApp.filter('strReplace', function () {
  return function (input, from, to) {

    input = input || '';
    from = from || '';
    to = to || '';
    return input.replace(new RegExp(from, 'g'), to);
  };
});
InferyxApp.filter('capitalize', function () {
  return function (input) {
    var str = input.split(" ")
    var result = [];
    for (var i = 0; i < str.length; i++) {
      result[i] = (!!str[i]) ? str[i].charAt(0).toUpperCase() + str[i].substr(1).toLowerCase() : '';
    }
    return result.toString().replace(/,/g, " ");
  }
});


InferyxApp.directive('modal', function () {
  return {
    template: '<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true"><div class="modal-dialog modal-sm"><div class="modal-content" ng-transclude><div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button><h4 class="modal-title" id="myModalLabel">Modal title</h4></div></div></div></div>',
    restrict: 'E',
    transclude: true,
    replace: true,
    scope: {
      visible: '=',
      onSown: '&',
      onHide: '&'
    },
    link: function postLink(scope, element, attrs) {

      $(element).modal({
        show: false,
        keyboard: attrs.keyboard,
        backdrop: attrs.backdrop
      });

      scope.$watch(function () {
        return scope.visible;
      }, function (value) {

        if (value == true) {
          $(element).modal('show');
        } else {
          $(element).modal('hide');
        }
      });

      $(element).on('shown.bs.modal', function () {
        scope.$apply(function () {
          scope.$parent[attrs.visible] = true;
        });
      });

      $(element).on('shown.bs.modal', function () {
        scope.$apply(function () {
          scope.onSown({});
        });
      });

      $(element).on('hidden.bs.modal', function () {
        scope.$apply(function () {
          scope.$parent[attrs.visible] = false;
        });
      });

      $(element).on('hidden.bs.modal', function () {
        scope.$apply(function () {
          scope.onHide({});
        });
      });
    }
  };
});

InferyxApp.directive('modalHeader', function () {
  return {
    template: '<div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button><h4 >{{title}}</h4></div>',
    replace: true,
    restrict: 'E',
    scope: {
      title: '@'
    }
  };
});

InferyxApp.directive('modalBody', function () {
  return {
    template: '<div class="modal-body" ng-transclude></div>',
    replace: true,
    restrict: 'E',
    transclude: true
  };
});

InferyxApp.directive('modalFooter', function () {
  return {
    template: '<div class="modal-footer" ng-transclude></div>',
    replace: true,
    restrict: 'E',
    transclude: true
  };
});

InferyxApp.directive('contextMenu', function ($parse) {
  var renderContextMenu = function ($scope, event, options) {
    if (!$) {
      var $ = angular.element;
    }
    $(event.currentTarget).addClass('context');
    var $contextMenu = $('<div>');
    $contextMenu.addClass('dropdown clearfix');
    var $ul = $('<ul>');
    $ul.addClass('dropdown-menu');
    $ul.attr({
      'role': 'menu'
    });
    $ul.css({
      display: 'block',
      position: 'absolute',
      left: event.pageX + 'px',
      top: event.pageY + 'px'
    });
    angular.forEach(options, function (item, i) {
      var $li = $('<li>');
      if (item === null) {
        $li.addClass('divider');
      } else {
        $a = $('<a>');
        $a.attr({
          tabindex: '-1',
          href: '#'
        });
        $a.text(typeof item[0] == 'string' ? item[0] : item[0].call($scope, $scope));
        $li.append($a);
        $li.on('click', function ($event) {
          $event.preventDefault();
          $scope.$apply(function () {
            $(event.currentTarget).removeClass('context');
            $contextMenu.remove();
            item[1].call($scope, $scope);
          });
        });
      }
      $ul.append($li);
    });
    $contextMenu.append($ul);
    var height = Math.max(
      document.body.scrollHeight, document.documentElement.scrollHeight,
      document.body.offsetHeight, document.documentElement.offsetHeight,
      document.body.clientHeight, document.documentElement.clientHeight
    );
    $contextMenu.css({
      width: '100%',
      height: height + 'px',
      position: 'absolute',
      top: 0,
      left: 0,
      zIndex: 9999
    });
    $(document).find('body').append($contextMenu);
    $contextMenu.on("mousedown", function (e) {
      if ($(e.target).hasClass('dropdown')) {
        $(event.currentTarget).removeClass('context');
        $contextMenu.remove();
      }
    }).on('contextmenu', function (event) {
      $(event.currentTarget).removeClass('context');
      event.preventDefault();
      $contextMenu.remove();
    });
  };
  return function ($scope, element, attrs) {
    element.on('contextmenu', function (event) {
      $scope.$apply(function () {
        event.preventDefault();
        var options = $scope.$eval(attrs.contextMenu);
        if (options instanceof Array) {
          renderContextMenu($scope, event, options);
        } else {
          throw '"' + attrs.contextMenu + '" not an array';
        }
      });
    });
  };
});
InferyxApp.directive('datetimez', function ($rootScope) {
  return {
    restrict: 'A',
    require: 'ngModel',
    scope: {
      flag: "="
    },
    link: function ($scope, element, attrs, ngModelCtrl) {
      element.datetimepicker({
        todayBtn: 1,
        autoclose: 1,
        todayHighlight: 1,
        forceParse: true,
        startView: 2,
        showMeridian: 1,
        format: "D M dd hh:ii:ss yyyy",
        pickerPosition: "bottom-left",
        toggleActive: true,
        pick12HourFormat: false
      }).on('changeDate', function (e) {
        ngModelCtrl.$setViewValue(e.date);
        $scope.$apply();
        $rootScope.validate($scope.flag);
      });

      // setTimeout(function () {
      //   ngModelCtrl.$setViewValue(new Date());
      //   scope.$apply();
      // }, 100);
    }
  };
});
InferyxApp.directive('searchCriteriaMonitoring', function (cacheService, CommonService, SystemMonitoringService, $filter, $timeout,$rootScope, dagMetaDataService) {
  return {
    restrict: 'AE',
    scope: {
      moduleType: "=",
      onSubmit: "=",
      data: "=",
      mode: "="
    },
    link: function ($scope, element, attrs) {
      $scope.searchForm = {};
      $rootScope.refreshSearchMon = function (data) {
        $scope.searchCriteria($scope.mode);
      }
      $rootScope.refreshRowData1 = function (data) {
        $scope.searchCriteria(data);
      }
      $rootScope.getGraphResult = function (data) {
        $scope.mode = data
        $scope.searchCriteria(data);
      }
      $scope.endDateBeforeRender = endDateBeforeRender
      $scope.endDateOnSetTime = endDateOnSetTime
      $scope.startDateBeforeRender = startDateBeforeRender
      $scope.startDateOnSetTime = startDateOnSetTime
      $scope.getLovByType = function() {
        CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
        var onSuccessGetLovByType = function (response) {
          console.log(response)
          $scope.lobTag=response[0].value
        }
      }
      $scope.loadTag = function (query) {
        return $timeout(function () {
          return $filter('filter')($scope.lobTag, query);
        });
      };
        $scope.getLovByType();
      function startDateOnSetTime() {
        $scope.$broadcast('start-date-changed');
      }

      function endDateOnSetTime() {
        $scope.$broadcast('end-date-changed');
      }

      function startDateBeforeRender($dates) {
        // var activeDate = moment($scope.searchForm.enddate);
        // const todaySinceMidnight = new Date();
        // $dates.filter(function (date) {
        //   return date.utcDateValue > todaySinceMidnight.getTime();
        // }).forEach(function (date) {
        //   date.selectable = false;
        // });
        if ($scope.searchForm.enddate) {
          var activeDate = moment($scope.searchForm.enddate);

          $dates.filter(function (date) {
            return date.localDateValue() >= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
      }

      function endDateBeforeRender($view, $dates) {
        if ($scope.searchForm.startdate) {
          var activeDate = moment($scope.searchForm.startdate).subtract(1, $view).add(1, 'minute');

          $dates.filter(function (date) {
            return date.localDateValue() <= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
      }
      $scope.refreshMonitoring = function () {
        $scope.searchForm.active = " "
        $scope.searchForm.status = "InProgress"
        $scope.searchForm.app = {}
        $scope.searchForm.app.uuid = "";
        $scope.searchForm.type = "";
        $scope.searchForm.username = "";
        $scope.searchForm.startdate = null;
        $scope.searchForm.enddate = null;
        $scope.searchForm.tags = [];
        $scope.allStatus = [
          {
            "caption": "All",
            "name": " "
          },
          {
            "caption": "Not Started",
            "name": "NotStarted"
          },
          {
            "caption": "In Progress",
            "name": "InProgress"
          },
          {
            "caption": "Completed",
            "name": "Completed"
          },
          {
            "caption": "Killed",
            "name": "Killed"
          },
          {
            "caption": "Failed",
            "name": "Failed"
          }
        ];
        $scope.allActive = [
          {
            "caption": "All",
            "name": " "
          },
          {
            "caption": "Active",
            "name": "active"
          },
          {
            "caption": "Expired",
            "name": "expired"
          }];
        $(".form_meridian_datetime").find("input").val("");
        var d = new Date(); // or whatever date you have
        var tzName = d.toLocaleString('en', {
          timeZoneName: 'short'
        }).split(' ').pop();
        $scope.currentTimezone = tzName;
        CommonService.getAllLatest("user").then(function (response) {
          onSuccessGetAllLatestUser(response.data)
        });
        var onSuccessGetAllLatestUser = function (response) {
          $scope.allUSerName = response;
        }
        CommonService.getAllLatest("application").then(function (response) {
          onSuccessGetAllLatestRole(response.data)
        });
        var onSuccessGetAllLatestRole = function (response) {
          $scope.apps = response;

        }
        CommonService.getMetaExecList().then(function (response) {
          onSuccessgetMetaExecList(response.data)
        });
        var onSuccessgetMetaExecList = function (response) {
          var typesarray = [];
          for (i = 0; i < response.length; i++) {
            var types = {};
            //alert(response[i])
            types.name = dagMetaDataService.elementDefs[response[i].toLowerCase()].name;
            //  types.name=response[i];
            //alert(JSON.stringify(types));
            typesarray[i] = types
          }
          $scope.Alltypes = typesarray;
        }
        try {
          $scope.searchCriteria($scope.mode);
        } catch (e) {

        } finally {

        }

      }

      $scope.searchCriteria = function (data) {
        $scope.onSubmit({
          'type': $scope.select,
          'data': [],
          'mode': data
        });
        var startdate = ""
        if ($scope.searchForm.startdate != null) {
          startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
          startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
          enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
          enddate = enddate + " UTC";
        }

        var tags = [];
        for (i = 0; i < $scope.searchForm.tags.length; i++) {
          tags[i] = $scope.searchForm.tags[i].text;
        }
        tags = tags.toString();
        //console.log(new Date($scope.startdate).toUTCString())
        console.log(startdate)
        console.log(enddate)
        $scope.loading = true;
        if ($scope.moduleType == "session") {
          if (data != "graph") {

            SystemMonitoringService.getActiveSession($scope.searchForm.app.uuid, $scope.searchForm.username, startdate, enddate, tags, $scope.searchForm.active).then(function (response) {
              onSuccessgetActiveSession(response.data)
            });
            var onSuccessgetActiveSession = function (response) {

              $scope.onSubmit({
                'type': $scope.select,
                'data': response,
                'mode': data
              });
            }
          }
          else {
            SystemMonitoringService.sessionGraph($scope.searchForm.app.uuid, 'session', $scope.searchForm.username, startdate, enddate, tags, $scope.searchForm.active).then(function (response) { onSuccessJobGraph(response.data) });
            var onSuccessJobGraph = function (response) {
              console.log(JSON.stringify(response));
              $scope.onSubmit({
                'type': $scope.select,
                'data': response,
                'mode': data
              });
            }
          }
        }
        else if ($scope.moduleType == "jobs") {

          $scope.ExecType = ""
          if ($scope.searchForm.type != "" && $scope.searchForm.type != null) {
            $scope.ExecType = dagMetaDataService.elementDefs[$scope.searchForm.type].execType;

          }
          if (data != "graph") {
            SystemMonitoringService.getActiveJobs($scope.searchForm.app.uuid, $scope.ExecType || "", $scope.searchForm.username, startdate, enddate, tags, $scope.searchForm.status).then(function (response) {
              onSuccessgetActiveJobs(response.data)
            });
            var onSuccessgetActiveJobs = function (response) {
              for (var i = 0; i < response.length; i++) {
                try {
                  var type = dagMetaDataService.elementDefs[response[i].type.toLowerCase()].name;
                  response[i].type = type
                } catch (e) {

                }
              }
              $scope.onSubmit({
                'type': $scope.select,
                'data': response,
                'mode': data
              });
            }
          }
          else {
            SystemMonitoringService.jobGraph($scope.searchForm.app.uuid, $scope.ExecType || "", $scope.searchForm.username, startdate, enddate, tags, $scope.searchForm.status).then(function (response) { onSuccessJobGraph(response.data) });
            var onSuccessJobGraph = function (response) {
              console.log(JSON.stringify(response));
              $scope.onSubmit({
                'type': $scope.select,
                'data': response,
                'mode': data
              });
            }
          }

        }
        else {

          SystemMonitoringService.getActiveThread().then(function (response) {
            onSuccessgetActiveJobs(response.data)
          });
          var onSuccessgetActiveJobs = function (response) {
            for (var i = 0; i < response.length; i++) {
              try {
                var type = dagMetaDataService.elementDefs[response[i].execInfo.ref.type.toLowerCase()].name;
                response[i].execInfo.ref.type = type
              } catch (e) {

              }
            }
            $scope.onSubmit({

              'type': $scope.select,
              'data': response,
              'mode': data
            });
          }
        }
      }
      $scope.refreshMonitoring();
    },
    templateUrl: 'views/search-criteria-monitoring.html'
  }
});
InferyxApp.directive('searchCriteria', function (cacheService, CommonService, $filter, $timeout, $rootScope, dagMetaDataService) {
  return {
    restrict: 'AE',
    scope: {
      moduleType: "=",
      handleGroup: "=",
      onSubmit: "=",
      noExec: "=",
      loading: '=?',
      data: "=?",
      parantType: '=?',
    },
    link: function ($scope, element, attrs) {

      $scope.searchForm = {};
      $rootScope.refreshSearchResults = function () {
        $scope.searchCriteria();
      }
      $scope.tz = localStorage.serverTz;
      var matches = $scope.tz.match(/\b(\w)/g);
      $scope.timezone = matches.join('')
      $scope.endDateBeforeRender = endDateBeforeRender
      $scope.endDateOnSetTime = endDateOnSetTime
      $scope.startDateBeforeRender = startDateBeforeRender
      $scope.startDateOnSetTime = startDateOnSetTime
      $scope.getLovByType = function() {
        CommonService.getLovByType("TAG").then(function (response) { onSuccessGetLovByType(response.data) }, function (response) { onError(response.data) })
        var onSuccessGetLovByType = function (response) {
          console.log(response)
          $scope.lobTag=response[0].value
        }
      }
      $scope.loadTag = function (query) {
        return $timeout(function () {
          return $filter('filter')($scope.lobTag, query);
        });
      };
        $scope.getLovByType();
      function startDateOnSetTime() {
        $scope.$broadcast('start-date-changed');
      }

      function endDateOnSetTime() {
        $scope.$broadcast('end-date-changed');
      }

      function startDateBeforeRender($dates) {
        if ($scope.searchForm.enddate) {
          var activeDate = moment($scope.searchForm.enddate);
          $dates.filter(function (date) {
            return date.localDateValue() >= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
      }

      function endDateBeforeRender($view, $dates) {
        if ($scope.searchForm.startdate) {
          var activeDate = moment($scope.searchForm.startdate).subtract(1, $view).add(1, 'minute');
          $dates.filter(function (date) {
            return date.localDateValue() <= activeDate.valueOf()
          }).forEach(function (date) {
            date.selectable = false;
          })
        }
      }

      $rootScope.refreshRowData = function () {
        $scope.searchCriteria();
      }

      $scope.searchForm.newType = $scope.moduleType;
      if ($scope.noExec) {
        $scope.select = $scope.searchForm.newType;
      } else {
        $scope.select = dagMetaDataService.elementDefs[$scope.moduleType].execType; //$scope.moduleType + 'exec';
      }
      if ($scope.handleGroup) {
        //$scope.select = $scope.moduleType;
        $scope.temp = $scope.moduleType.split('exec')[0];
        $scope.searchForm.newType = $scope.temp;
        $scope.types = [{
          "text": $scope.temp,
          "caption": "Rule"
        }, {
          "text": $scope.temp + 'group',
          "caption": "Rule Group"
        }];
        $scope.onChangeType = function (newType) {
          $scope.searchForm.newType = newType //dagMetaDataService.elementDefs[newType].name.replace(/\s/g, "") ;//newType
          if ($scope.noExec) {
            $scope.select = newType;
          } else {
            $scope.select = dagMetaDataService.elementDefs[newType].execType; //newType+ 'exec' ;
          }

          $scope.refresh();
          $scope.onSubmit({
            'type': $scope.select,
            'data': []
          });
        }
      }//End If 
      else{
        if ($scope.moduleType != 'vizexec' && $scope.moduleType != 'downloadexec' && $scope.moduleType != 'uploadexec' && $scope.moduleType != 'graphexec' ) {
          $scope.temp = $scope.moduleType.split('exec')[0];
          $scope.searchForm.newType = $scope.temp;
        }
        else if ($scope.moduleType == 'downloadexec' || $scope.moduleType == 'uploadexec') {
          $scope.searchForm.newType = $scope.moduleType;
        }
        else if($scope.moduleType == 'graphexec'){
          $scope.searchForm.newType='graphpod'
        }
        else {
          $scope.searchForm.newType = "vizpod"
        }
      }//End ELSE

      $scope.refresh = function () {
        $scope.searchForm.execname = "";
        $scope.searchForm.username = "";
        $scope.searchForm.tags = [];
        $scope.searchForm.published = "";
        $scope.searchForm.active = "";
        $scope.searchForm.status = "";
        $scope.searchForm.startdate = null;
        $scope.searchForm.enddate = null;
        $scope.showEndErrorMessage = false;
        $scope.showStartErrorMessage = false;
        $scope.allStatus = [{
          "caption": "Not Started",
          "name": "NotStarted"
        },
        {
          "caption": "In Progress",
          "name": "InProgress"
        },
        {
          "caption": "Completed",
          "name": "Completed"
        },
        {
          "caption": "Killed",
          "name": "Killed"
        },
        {
          "caption": "Failed",
          "name": "Failed"
        }
        ];

        $scope.allActive = [
          {
            "caption": "Active",
            "name": "Y"
          },
          {
            "caption": "Inactive",
            "name": "N"
          }
        ];

        $scope.allPublish = [
          {
            "caption": "Yes",
            "name": "Y"
          },
          {
            "caption": "No",
            "name": "N"
          }
        ];

        $(".form_meridian_datetime").find("input").val("");
        CommonService.getAllLatest($scope.searchForm.newType).then(function (response) {
          onSuccessGetAllLatestExec(response.data)
        });
        var onSuccessGetAllLatestExec = function (response) {
          $scope.allExecName = response;
        }
        CommonService.getAllLatest("user").then(function (response) {
          onSuccessGetAllLatestUser(response.data)
        });
        var onSuccessGetAllLatestUser = function (response) {
          $scope.allUSerName = response;
        }
        try {
          $scope.searchCriteria();
        } catch (e) {

        } finally {

        }
      }
      var cached = cacheService.getCache('searchCriteria', $scope.moduleType);
      if (cached && cached.searchForm) {
        $scope.searchForm = cached.searchForm;
        if ($scope.noExec) {
          $scope.select = $scope.searchForm.newType;
        }
        else {
          $scope.select = dagMetaDataService.elementDefs[$scope.searchForm.newType].execType;//newType+ 'exec' ;
        }
        $("#start_date").find("input").val($filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyyZ", "GMT"));
        $("#end_date").find("input").val($filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyyZ", "GMT"));
        CommonService.getAllLatest($scope.searchForm.newType).then(function (response) {
          onSuccessGetAllLatestExec(response.data)
        });
        var onSuccessGetAllLatestExec = function (response) {
          $scope.allExecName = response;
        }
        CommonService.getAllLatest("user").then(function (response) {
          onSuccessGetAllLatestUser(response.data)
        });
        var onSuccessGetAllLatestUser = function (response) {
          $scope.allUSerName = response;
        }
      } else {
        $scope.refresh();
      }
      $scope.searchCriteria = function () {
        $scope.onSubmit({
          'type': $scope.select,
          'data': []
        });
        var startdate = ""
        if ($scope.searchForm.startdate != null) {
          startdate = $filter('date')($scope.searchForm.startdate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
          startdate = startdate + " UTC"
        }
        var enddate = "";
        if ($scope.searchForm.enddate != null) {
          enddate = $filter('date')($scope.searchForm.enddate, "EEE MMM dd HH:mm:ss yyyy", 'UTC');
          enddate = enddate + " UTC";
        }

        var tags = [];
        for (i = 0; i < $scope.searchForm.tags.length; i++) {
          tags[i] = $scope.searchForm.tags[i].text;
        }
        tags = tags.toString();
        $scope.loading = true;
        var url = '';
        if ($scope.parantType == 'rule') {
          url = 'getParamListByRule'
        } else if ($scope.parantType == 'model') {
          url = 'getParamListByModel'
        }
        else if ($scope.parantType == 'dag') {
          url = 'getParamListByDag'
        }
        else if ($scope.noExec) {
          url = 'getBaseEntityByCriteria'
        }
        else {
          url = 'getBaseEntityStatusByCriteria'
        }
        CommonService[url]($scope.select, $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '', $scope.searchForm.published || '', $scope.searchForm.status || '').then(function (response) {

          // CommonService[$scope.noExec ? 'getBaseEntityByCriteria' : 'getBaseEntityStatusByCriteria']($scope.select, $scope.searchForm.execname || '', $scope.searchForm.username || "", startdate, enddate, tags, $scope.searchForm.active || '',$scope.searchForm.published || '', $scope.searchForm.status || '').then(function(response) {
          onSuccess(response.data)
        }, function error() {
          $scope.loading = false;
        });
        var onSuccess = function (response) {
          // cacheService.saveCache('searchCriteria', $scope.moduleType, {
          //   searchForm: $scope.searchForm,
          //   data: response
          // });
          $scope.data = response;
          //$scope.dagExec=response;
          //$scope.$emit('searchCriteriaChanged',{'type':$scope.searchForm.newType,'data' :response});
          $scope.onSubmit({
            'type': $scope.select,
            'data': response
          });
          $scope.loading = false;
        }

      }

      if (cached && cached.data && cached.data.length > 0) {
        if ($scope.noExec == false) {
          $scope.searchCriteria();
        }
        else {
          $scope.onSubmit({
            'type': $scope.select,
            'data': cached.data
          });
        }
      } else
        $scope.searchCriteria();

    },
    templateUrl: 'views/search-criteria.html',

  };

});
InferyxApp.filter('unique', function () {
  return function (arr, field) {
    return _.uniq(arr, function (a) {
      return a[field];
    });
  };
});

InferyxApp.directive('expand', function () {
  return {
    restrict: 'A',
    controller: ['$scope', function ($scope) {
      $scope.$on('onExpandAll', function (event, args) {
        $scope.expanded = args.expanded;
      });
    }]
  };
});

InferyxApp.directive('fileModel', ['$parse', 'CommonService', function ($parse, CommonService) {
  return {
    restrict: 'A',
    scope: {
      onSubmit: "="
    },
    link: function (scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;
      element.bind('change', function () {
        scope.$apply(function () {
          var file = element[0].files[0]
          var fd = new FormData();
          fd.append('file', file)
          CommonService.SaveFile(file.name, fd, "import").then(function (response) { onSuccess(response.data) });
          var onSuccess = function (response) {
            modelSetter(scope, response);
            scope.onSubmit({
              'name': file.name,
              'data': response
            });
          }
        });
      });
    }
  };
}]);
InferyxApp.directive('fileModelChange', ['$parse', function ($parse) {
  return {
    restrict: 'A',
    scope: {
      onSubmit: "="
    },
    link: function (scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;
      element.bind('change', function () {
        var file = element[0].files[0]
        scope.$apply(function () {
          var patt1 = new RegExp(/^[0-9a-zA-Z\._]*$/);
          var result = patt1.test(file.name);
          scope.onSubmit({
            'fileName': file.name,
            'valid': result
          });
        });
      });
    }
  };
}]);

InferyxApp.directive('notification', function ($timeout) {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      ngModel: '='
    },
    template: '<div class="alert fade" bs-alert="ngModel"></div>',
    link: function (scope, element, attrs) {
      // $timeout(function(){
      //   element.hide();
      // }, 3000);
    }
  }
});

InferyxApp.directive("limitTo", [function () {
  return {
    restrict: "A",
    link: function (scope, elem, attrs) {
      var limit = parseInt(attrs.limitTo);
      angular.element(elem).on("keypress", function (e) {
        if (this.value.length == limit) e.preventDefault();
      });
    }
  }
}]);

InferyxApp.directive('focusMe', function($timeout, $parse) {
  return {
    link: function(scope, element, attrs) {
      var model = $parse(attrs.focusMe);
      scope.$watch(model, function(value) {
        console.log('value=',value);
        if(value === true) { 
          $timeout(function() {
            element[0].focus(); 
          });
        }
      });
      element.bind('blur', function() {
        console.log('blur')
       // scope.$apply(model.assign(scope, false));
      })
    }
  };
});


InferyxApp.directive('preventEnterSubmit', function () {
  return function (scope, el, attrs) {
    el.bind('keydown', function (event) {
      if (13 == event.which) {
          event.preventDefault(); // Doesn't work at all
         // window.stop(); // Works in all browsers but IE...
         // document.execCommand('Stop'); // Works in IE
          return false; // Don't even know why it's here. Does nothing.
      }
    });
  };
});


InferyxApp.filter('unique', function() {

  return function (arr, field) {
    var o = {}, i, l = arr.length, r = [];
    for(i=0; i<l;i+=1) {
      o[arr[i][field]] = arr[i];
    }
    for(i in o) {
      r.push(o[i]);
    }
    return r;
  };
})

InferyxApp.directive("dragDrop", ["$parse",
function($parse) {
var sourceParent = "";
var sourceIndex = -1;
return {
  link: function($scope, elm, attr, ctrl) {

  // #region Initialization

  // Get TBODY of a element  
  var tbody = elm.parent();
  // Set draggable true
  //elm.attr("draggable", true);jitender

  // If id of TBODY of current element already set then it won't set again
  tbody.attr('drag-id') ? void 0 : tbody.attr("drag-id", $scope.$id);
  // This add drag pointer 
  elm.css("cursor", "move");

  // Events of element :- dragstart | dragover | drop | dragend
  elm.on("dragstart", onDragStart);
  elm.on("dragover", onDragOver);
  elm.on("drop", onDrop);
  elm.on("dragend", onDragEnd);

  // #endregion

  // This will trigger when user pick e row
  function onDragStart(e) {

    //Mozilla Hack
//	  e.dataTransfer.setData("Text", "");

    if (!sourceParent) {

    // Set selected element's parent id
    sourceParent = tbody.attr('drag-id') ? tbody.attr('drag-id') : void 0;

    // Set selected element's index
    sourceIndex = $scope.$index;

    // This don't support in IE but other browser support it
    // This will set drag Image with it's position
    // IE automically set image by himself
    // typeof e.dataTransfer.setDragImage !== "undefined" ?
    //   e.dataTransfer.setDragImage(e.target, -10, -10) : void 0;

    // This element will only drop to the element whose have drop effect 'move'
  //	e.dataTransfer.effectAllowed = 'move';
    }
    return true;
  }

  // This will trigger when user drag source element on another element
  function onDragOver(e) {

    // Prevent Default actions
    e.preventDefault ? e.preventDefault() : void 0;
    e.stopPropagation ? e.stopPropagation() : void 0;

    // This get current elements parent id
    var targetParent = tbody.attr('drag-id') ? tbody.attr('drag-id') : void 0;


    // If user drag elemnt from its boundary then cursor will show block icon else it will show move icon [ i.e : this effect work perfectly in google chrome]
  //  e.dataTransfer.dropEffect = sourceParent !== targetParent || typeof attr.ngRepeat === "undefined" ? 'none' : 'move';

    return false;
  }

  //This will Trigger when user drop source element on target element
  function onDrop(e) {

    // Prevent Default actions
    e.preventDefault ? e.preventDefault() : void 0;
    e.stopPropagation ? e.stopPropagation() : void 0;

    if (typeof attr.ngRepeat === "undefined")
    return false;
    // Get this item List
    var itemList = $parse(attr.ngRepeat.split("in")[1].trim())($scope);


    // Get target element's index
    var targetIndex = $scope.$index;

    // Get target element's parent id
    var targetParent = tbody.attr('drag-id') ? tbody.attr('drag-id') : void 0;

    // Get properties names which will be changed during the drag and drop
    var elements = attr.dragDrop ? attr.dragDrop.trim().split(",") : void 0;

    // If user dropped element into it's boundary and on another source not himself
    if (sourceIndex !== targetIndex && targetParent === sourceParent) {

    // If user provide element list by ',' 
    typeof elements !== "undefined" ? elements.forEach(function(element) {
      element = element.trim();
      typeof itemList[targetIndex][element] !== "undefined" ?
      itemList[targetIndex][element] = [itemList[sourceIndex][element], itemList[sourceIndex][element] = itemList[targetIndex][element]][0] : void 0;
    }) : void 0;
    // Visual row change 
    itemList[targetIndex] = [itemList[sourceIndex], itemList[sourceIndex] = itemList[targetIndex]][0];
    // After completing the task directive send changes to the controller 
    $scope.$apply(function() {
      typeof attr.afterDrop != "undefined" ?
      $parse(attr.afterDrop)($scope)({
        sourceIndex: sourceIndex,
        sourceItem: itemList[sourceIndex],
        targetIndex: targetIndex,
        targetItem: itemList[targetIndex]
      }) : void 0;

    });
    }
  }
  // This will trigger after drag and drop complete
  function onDragEnd(e) {

    //clearing the source
    sourceParent = "";
    sourceIndex = -1;
  }

  }
}
}
]);
