var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('treeGraphDirective', function ($timeout, CommonService, dagMetaDataService) {
    return {
        scope: {
            uuid: "=",
            version: "="
        },
        link: function (scope, element, attrs) {
            var menus = ["Show Details"];
            var root, zoom;
            var MOVE_STEP = 100;
            scope.zoomSize =10;
            var margin = {
                top: 20,
                right: 120,
                bottom: 20,
                left: 120
            },

                width = 1060 - margin.right - margin.left,
                height = 600 - margin.top - margin.bottom;
            var i = 0,
                duration = 750;
            var tree;
            var svg;
            scope.degree = "1";
            scope.onChangeDegree = function (degree) {
                scope.degree = degree
                console.log(scope.degree);
                height = 600 - margin.top - margin.bottom;
                scope.getGraphData();

            }
            //Create the drag and drop behavior to set for the objects crated
            // var drag = d3.behavior.drag()
            //     .origin(function (d) { return d; })
            //     .on("dragstart", dragstarted)
            //     .on("drag", dragged);

            //Called when drag event starts. It stop the propagation of the click event
            // function dragstarted(d) {
            //     d3.event.sourceEvent.stopPropagation();
            // }

            //Called when the drag event occurs (object should be moved)
            // function dragged(d) {
            //     d.x = d3.event.x;
            //     d.y = d3.event.y;
            //     //Translate the object on the actual moved point
            //     d3.select(this).attr({
            //         transform: "translate(" + d.x + "," + d.y + ")"
            //     });
            // }

            scope.getGraphData = function () {
                if (scope.uuid && scope.version) {
                    var newUuid = scope.uuid
                    $('#graphloader').show();
                    $('.show-graph-body').hide();
                    $('#errorMsg').hide();
                    CommonService.getTreeGraphResults(newUuid, scope.version, scope.degree).then(function (result) {
                        $('#graphloader').hide();
                        if (!result.data) {
                            $('#errorMsg').text('No Results Found').show();
                            $('.show-graph-body').hide();
                        } else {
                            $('.show-graph-body').show();
                        }
                        scope.graphdata = result.data;
                        for (var i = 0; i < scope.graphdata.children.length; i++) {
                            scope.graphdata.children[i].index = i;
                            if (scope.graphdata.children.length > 20) {
                                height = height + 30;
                            }
                        }
                        tree = d3.layout.tree()
                            .size([height, width]);
                        createSvg();
                        root = scope.graphdata;
                        root.x0 = height / 2;
                        root.y0 = 0;
                        root.children.forEach(collapse);
                        update(root);
                        scope.degree = scope.degree;
                    }, function () {
                        $('#errorMsg').text('Some Error Occured').show();
                        $('#graphloader').hide();
                        $('.show-graph-body[graph-directive]').hide();
                    });
                }
            }

            function collapse(d) {
                if (d.children) {
                    d._children = d.children;
                    d._children.forEach(collapse);
                    d.children = null;
                }
            }

            scope.getGraphData();

            scope.$on('refreshData', function () {
                scope.degree = "1";
                height = 600 - margin.top - margin.bottom;
                scope.getGraphData();
            });
            var diagonal = d3.svg.diagonal()
                .projection(function (d) {
                    return [d.y, d.x];
                });
            function createSvg() {
                d3.selectAll("div.show-graph-body #network-graph-wrapper svg.tree-graph").remove();
                svg = d3.selectAll("div.show-graph-body #network-graph-wrapper").append("svg")
                    .attr("width", width + margin.right + margin.left)
                    .attr("height", height + margin.top + margin.bottom)
                    .attr('class', 'tree-graph')
                    .append("g")
                    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
                     
                d3.select(self.frameElement).style("height", "800px");
              //  svg.selectAll(".draggable").call(drag)
              //  zoom = d3.behavior.zoom().scaleExtent([2.0, 0.2]).on('zoom', zoomed);

            }
            // function zoomed() {
            //     svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
            // }

            function update(source) {
                if (scope.graphdata == null) {
                    return false;
                }

                var w = d3.select("svg.tree-graph").attr("width");
                d3.select("svg.tree-graph").attr("width", parseInt(w) + 100);
                d3.select("svg.tree-graph").attr("height", height);

                // Compute the new tree layout.
                var nodes = tree.nodes(root).reverse(),
                    links = tree.links(nodes);

                // Normalize for fixed-depth.
                nodes.forEach(function (d) {
                    d.y = d.depth * 250; //180 jitender
                });

                // Update the nodes…
                var node = svg.selectAll("g.node")
                    .data(nodes, function (d) {
                        return d.id || (d.id = ++i);
                    });

                // Enter any new nodes at the parent's previous position.
                var nodeEnter = node.enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function (d) {
                        return "translate(" + source.y0 + "," + source.x0 + ")";
                    })
                    .on("click", toggleChildren)
                    .on("mouseover", mouseoverNode)
                    .on("mouseout", function (d) {
                        scope.nodeDetail = null;
                        $(".tooltipcustom").css("display", "none");

                    })
                    .on('contextmenu', rightClickNode);
                
                
                nodeEnter.append("circle")
                    .attr("r", 10)
                    .style("stroke-width", 0.5) // set the stroke width //jitender
                    .style("stroke", "73879C") //jitender 73879C
                    .style("fill", function (d) { return d._children ? "#ccff99" : getColorCode(d); })
                // .call(d3.behavior.zoom().on("zoom", function () {
                //     svg.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")")
                //   }))
                nodeEnter
                    .insert("g")
                    .attr("class", "nodetext")
                    .append("text")
                    .attr("x", function (d) {
                        return d.children || d._children ? -17 : 17;
                    })
                    .attr("dy", ".35em")
                    .attr("font-family", "Arial, Helvetica, sans-serif")
                    .attr("fill", "73879C")
                    .style("font", "normal 11px Arial")
                    .attr("text-anchor", function (d) {
                        return d.children || d._children ? "end" : "start";
                    })
                    .style("fill-opacity", 1e-6)
                    .text(function (d) {
                        if (d.parent == null) {
                            return d.name.length > 20 ? d.name.substring(0, 15) + "...." : d.name
                        } else {
                            return  d.name.length > 15 ? d.name.substring(0, 15) + ".." : d.name;
                        }
                    })
                    .append("text:title")
                    .text(function (d) {
                        return d.name;
                    });

                    nodeEnter.append("svg:foreignObject")
                    .attr("width", 20)
                    .attr("height", 20)
                    .attr("y", "-10")
                    .attr("x", function(d){
                        var x="100px";
                        return x
                    })
                    .append("xhtml:img")
                        .attr("class","node-refresh")
                        .style("display","none")
                       .attr("src","lib/images/loadingimg.gif")
                    // .style("-webkit-animation","node-refesh 2s linear infinite")
                    // .style("font-family","FontAwesome")
                    //    .attr("class", "control fa-refresh  node-refesh");
                    
             
                // Transition nodes to their new position.
                var nodeUpdate = node.transition()
                    .duration(duration)
                    .attr("transform", function (d) { return "translate(" + d.y + "," + d.x + ")"; });

                nodeUpdate.select("circle")
                    .attr("r", 10)
                    .style("stroke-width", 0.5) // set the stroke width //jitender
                    .style("stroke", "73879C") //jitender 73879C
                    .style("fill", function (d) { return d._children ? "#ccff99" : getColorCode(d); });

                nodeUpdate.select("text")
                    .style("fill-opacity", 1);
                
                // Transition exiting nodes to the parent's new position.
                var nodeExit = node.exit().transition()
                    .duration(duration)
                    .attr("transform", function (d) { return "translate(" + source.y + "," + source.x + ")"; })
                    .remove();

                nodeExit.select("circle")
                    .style("stroke-width", 0.5) // set the stroke width //jitender
                    .style("stroke", "73879C") //jitender 73879C
                    .attr("r", 1e-6);

                nodeExit.select("text")
                    .style("fill-opacity", 1e-6)
                    .attr("font-family", "Arial, Helvetica, sans-serif")
                    .attr("fill", "73879C")
                    .style("font", "normal 11px Arial");

                // Transition exiting nodes to the parent's new position.
                var nodeExit = node.exit().transition()
                    .duration(duration)
                    .attr("transform", function (d) {
                        return "translate(" + source.y + "," + source.x + ")";
                    })
                    .remove();

                nodeExit.select("circle")
                    .attr("r", 1e-6);

                nodeExit.select("text")
                    .style("fill-opacity", 1e-6)

                // Update the links…
                var link = svg.selectAll("path.link")
                    .data(links, function (d) {
                        return d.target.id;
                    });

                // Enter any new links at the parent's previous position.
                link.enter().insert("path", "g")
                    .attr("class", "link")
                    .attr("d", function (d) {
                        var o = {
                            x: source.x0,
                            y: source.y0
                        };
                        return diagonal({
                            source: o,
                            target: o
                        });
                    });

                // Transition links to their new position.
                link.transition()
                    .duration(duration)
                    .attr("d", diagonal);

                //  Transition exiting nodes to the parent's new position.
                link.exit().transition()
                    .duration(duration)
                    .attr("d", function (d) {
                        var o = {
                            x: source.x,
                            y: source.y
                        };
                        return diagonal({
                            source: o,
                            target: o
                        });
                    })
                    .remove();

                // Update the link text
                var linktext = svg.selectAll("g.link")
                    .data(links, function (d) {
                        return d.target.id
                    });

                linktext.enter()
                    .insert("g")
                    .attr("class", "link")
                    .append("text")
                    .attr("text-anchor", "middle")
                    .attr("font-family", "Arial, Helvetica, sans-serif")
                    .attr("fill", "73879C")
                    .style("font", "normal 11px Arial")
                    .attr("x", 25) //add new for link text
                    .text(function (d) {
                        return d.target.nodeType;
                    })
                    .append("text:title")
                    .text(function (d) {
                        return d.target.nodeType;
                    });

                // Transition link text to their new positions

                linktext.transition()
                    .duration(duration)
                    .attr("transform", function (d) {
                        return "translate(" + ((d.source.y + d.target.y) / 2) + "," + ((d.source.x + d.target.x) / 2) + ")";
                    });

                //Transition exiting link text to the parent's new position.
                linktext.exit().transition()
                    .remove();


                // Stash the old positions for transition.
                nodes.forEach(function (d) {
                    d.x0 = d.x;
                    d.y0 = d.y;
                });
            }

            scope.$watch('zoomSize', function (data, oldValue) {
                var tempsize = data / 10;
                $('#network-graph-wrapper svg').css({
                    '-webkit-transform': 'scale(' + tempsize + ')',
                    '-moz-transform': 'scale(' + tempsize + ')',
                    '-ms-transform': 'scale(' + tempsize + ')',
                    '-o-transform': 'scale(' + tempsize + ')',
                    'transform': 'scale(' + tempsize + ')',
                    'transform-origin': 'center'
                });
            });

            scope.changeSliderForward = function () {
                scope.zoomSize = scope.zoomSize + 1;
                // moveDrawRight();
            }
            scope.changeSliderBack = function () {
                if(scope.zoomSize >=5)
                scope.zoomSize = scope.zoomSize - 1;
            }

            scope.resize=function(){
                scope.zoomSize =10;
              }

            // function moveDrawLeft(){
            // 	var xPosition = d3.transform(container.attr("transform")).translate[0];
            // 	var yPosition = d3.transform(container.attr("transform")).translate[1];
            // 	svg.attr("transform", "translate(" + (xPosition - MOVE_STEP) + ", " + yPosition + ")scale(" + zoom.scale() + ")");
            // }
            // function moveDrawRight(){
            // 	var xPosition = d3.transform(svg.attr("transform")).translate[0];
            // 	var yPosition = d3.transform(svg.attr("transform")).translate[1];
            // 	svg.attr("transform", "translate(" + (xPosition + MOVE_STEP) + ", " + yPosition + ")scale(" + zoom.scale() + ")");
            // }

            // Toggle children on click.
            function toggleChildren(d) {
                if (d.children) {

                    d._children = d.children;
                    d.children = null;
                    for (var i = 0; i < d._children.length; i++) {
                        if (d._children.length > 20) {
                            height = height - 30;
                        }
                    }
                    if (d._children.length > 20) {
                        tree = d3.layout.tree()
                            .size([height, width]);
                        update(d);
                    } else {
                        update(d);
                    }

                }
                else {
                    if (d.metaRef.ref.type == null || d.metaRef.ref.type == "simple") {
                        return false;
                    }
                   
                  //  $('.show-graph-body').hide();
                 //   $('#graphloader').show();
                 console.log(this)
                    var this_node=this 
                    $(this_node).find(".node-refresh").css("display","block");
                    $('#errorMsg').hide();
                    CommonService.getTreeGraphResults(d.metaRef.ref.uuid, d.metaRef.ref.version | "", scope.degree).then(function (result) {
                        $(this_node).find(".node-refresh").css("display","none");
                        $('.show-graph-body').show();
                        $('#graphloader').hide();
                      
                        for (var i = 0; i < result.data.children.length; i++) {
                            result.data.children[i].index = i;
                            result.data.children[i].isProgess=false;
                            if (result.data.children.length > 20) {
                                height = height + 30;
                            }
                        }
                        var r;
                        r = result.data;
                        r.parent = d.name
                        d._children = r.children
                        d.children = d._children;
                        d._children = null;
                        if (result.data.children.length > 20) {
                            tree = d3.layout.tree()
                                .size([height, width]);
                            update(d);
                        } else {
                            update(d);
                        }

                    });
                }
            }//End ToggleChildren

            function mouseoverNode(d) {
                var e = d3.event;
                scope.nodeDetail = d
                scope.nodeDetail.caption = getCaption(d)
                scope.nodeDetail.color = getColorCode(d);
                $("#colorID").css("background-color", scope.nodeDetail.color);
                var xPercent = e.clientX / $(window).width() * 100;
                var left;
                var top;
                if (parseInt(xPercent) > 50) {
                    left = (e.clientX - 400) + "px";
                    top = e.clientY + "px";
                }
                else {
                    left = (e.clientX + 40) + "px";
                    top = e.clientY + "px";
                }
                $(".tooltipcustom").css("left", left);
                $(".tooltipcustom").css("top", top);
                if (scope.nodeDetail.caption && scope.nodeDetail.color)
                    $(".tooltipcustom").css("display", "block");
            }

            function rightClickNode(d, i) {
                d3.event.preventDefault();
                var Nodedata = d;
                if(Nodedata.metaRef.ref.uuid.indexOf("_") !=-1){
                    return false;
                }
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
                    
                        navigateTo(Nodedata, d);
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
            }

            function getCaption(d) {
                try {
                    var caption = dagMetaDataService.elementDefs[d.nodeType.toLowerCase()].caption;
                }
                catch (e) {
                    var caption = d.name
                }

                finally { }
                return caption
            }

            function getColorCode(d) {
                try {
                    var color = dagMetaDataService.elementDefs[d.metaRef.ref.type.toLowerCase()].color
                }
                catch (e) {
                    if (d.nodeType.toLowerCase().indexOf("from_base") != -1) {
                        var color = dagMetaDataService.elementDefs["from_base"].color
                    }
                    else {
                        var color = '#000000'
                    }
                }
                finally { }
                return color;
            }

            function navigateTo(data, d) {
            
                if (d == "Show Details" && data.metaRef.ref.type != null && data.metaRef.ref.type != "attributes") {
                    data.metaRef.ref.name = data.name
                    data.metaRef.ref.type = data.metaRef.ref.type
                    dagMetaDataService.navigateTo(data.metaRef.ref);
                } else if (d == "Show Details" && data.metaRef.ref.type != null && data.metaRef.ref.type == "attributes") {
                    data.metaRef.ref.name = data.name
                    data.metaRef.ref.type = data.metaRef.ref.type
                    dagMetaDataService.navigateTo(data.parent.metaRef.ref);
                }
            }
        },

        template: `
      <div class="network-graph-zoom-slider col-md-1 col-md-offset-11">
        <div class="col-md-1" style="height:100px; width:100px;">
          <a ng-click="changeSliderForward()"  tooltip-placement="top" uib-tooltip="Zoom in"><i class="fa fa-search-plus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
          <rzslider rz-slider-model="zoomSize" rz-slider-options="{floor: 1, ceil: 20,minLimit:1,maxLimit:20,hidePointerLabels:true,hideLimitLabels:true,vertical: true}"></rzslider>
            <a ng-click="changeSliderBack()"  tooltip-placement="top" uib-tooltip="Zoom out"><i class="fa fa-search-minus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
            <span class="fa-stack" style="margin-left:16px; margin-top:15px; z-index: 980;" ng-click="resize()" tooltip-placement="top" uib-tooltip="Reset">
                <i class="fa fa-circle fa-stack-2x" style="color: #999;font-size: 21px;"></i>
                <i class="fa fa-undo fa-stack-1x fa-inverse" style="margin:-4px 0px 0px 0px;font-size: 10px;"></i>
    </span>
        </div>
      </div>
      
       <!-- <div class="col-md-3 col-md-offset-9" style="margin-top-3%;position: absolute;right: 11px;margin: -6px -10px;">-->
        <!--<div class="col-md-2 col-md-offset-10" style="margin-top-3%;position: absolute;right: 11px;margin: -6px -10px;">--> 
      
        <div class="col-md-2" style="margin-top-3%;position:absolute;margin:16px -9px;z-index:100;"> 
            <div class="form-group">
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="btn-group" ng-init="degree='1'">
                        <button type="button" class="btn btn-circle btn-default"  tooltip-placement="top" uib-tooltip="Direction" ng-model="degree" ng-change="onChangeDegree(degree)" uib-btn-radio="'1'" ng-hide="degree == 1?true:false" uncheckable style="height: 40px;width:40px;"><i class="fa fa-arrow-down" aria-hidden="true"></i></button>   
                        <button type="button" class="btn btn-circle btn-default" ng-model="degree" tooltip-placement="top" uib-tooltip="Direction" ng-change="onChangeDegree(degree)" uib-btn-radio="'-1'" ng-hide="degree == -1?true:false" uncheckable style="height: 40px;width:40px;"><i class="fa fa-arrow-up" aria-hidden="true"></i></button>
                    </div>
                </div>
            </div>
        </div>
      <div class="tooltipcustom" id="divtoshow" style="position: fixed;display:none;z-index:9999;min-width:320px;min-height: 80px;opacity: 0.8;
        font-family: Roboto,Helvetica Neue,Helvetica,Arial,sans-serif;">
  
        <div style="margin-top: 5px;margin-right: 15px">
          <div class="row">
            <div class="col-md-1">
              <div class="one" id="colorID"style="margin-left:5px;margin-bottom: 10px"></div>
              
              </div>
            <div class="col-md-5" id="elementTypeText">{{nodeDetail.caption}}</div>
          </div>
  
          <div class="row" style="margin-top: 5px">
            <span style="margin-left:20px;">Id</span>
            <span id="task_Id" style="margin-left:58px">{{nodeDetail.metaRef.ref.uuid}}</span>
          </div>
            <div class="row" style="margin-top: 5px">
            <span style="margin-left:20px;">Type</span>
            <span id="task_Id" style="margin-left:40px">{{nodeDetail.metaRef.ref.type}}</span>
          </div>
          <div class="row" style="margin-top: 5px">
            <span style="margin-left:20px">Name</span>
            <span id="task_Name" style="margin-left:33px">{{nodeDetail.name}}</span>
          </div>
        </div>
      </div>
      <br>
      <div id="network-graph-wrapper" style="height:500px;"></div>
      `
    };
})