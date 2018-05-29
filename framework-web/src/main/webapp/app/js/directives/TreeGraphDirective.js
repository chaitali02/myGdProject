var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('treeGraphDirective', function ($timeout, CommonService, dagMetaDataService) {
    return {
        scope: {
            uuid: "=",
            version: "="
        },
        link: function (scope, element, attrs) {
            var menus = ["Knowledge Graph"];
            var root;
            var margin = {
                top: 20,
                right: 120,
                bottom: 20,
                left: 120
            },
            width = 1060 - margin.right - margin.left,
            height =800 - margin.top - margin.bottom;
            var i = 0,
            duration = 750;
            var tree;
            var svg ;
         
            scope.getGraphData = function () {
                if (scope.uuid && scope.version) {
                    var newUuid = scope.uuid
                    $('#graphloader').show();
                    $('.show-graph-body').hide();
                    $('#errorMsg').hide();
                    CommonService.getTreeGraphResults(newUuid, scope.version, "1").then(function (result){
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
                            if(scope.graphdata.children.length >20){
                                height=height+30;
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
                scope.getGraphData();
              });
            var diagonal = d3.svg.diagonal()
            .projection(function (d) {
                return [d.y, d.x];
            });
            function createSvg(){
                d3.selectAll("div.show-graph-body #network-graph-wrapper svg.tree-graph").remove();
                svg = d3.selectAll("div.show-graph-body #network-graph-wrapper").append("svg")
                    .attr("width", width + margin.right + margin.left)
                    .attr("height", height + margin.top + margin.bottom)
                    .attr('class','tree-graph')
                    .append("g")
                    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
                   
                d3.select(self.frameElement).style("height", "800px");
            }
         
            
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
                    d.y = d.depth * 180;
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
                    .on("mouseover",mouseoverNode)
                    .on("mouseout", function (d) {
                        $(".tooltipcustom").css("display", "none");
                        scope.nodeDetail = null;
                    })
                    .on('contextmenu',rightClickNode);

                nodeEnter.append("circle")
                    .attr("r", 10)
                    .style("stroke-width", 0.5) // set the stroke width //jitender
                    .style("stroke", "73879C") //jitender 73879C
                    .style("fill", function (d) { return d._children ? "#ccff99" : getColorCode(d); })
                    // .call(d3.behavior.zoom().on("zoom", function () {
                    //     svg.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")")
                    //   }))
                nodeEnter.append("text")
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
                    .text(function (d) {
                        return d.name;
                    })
                    .style("fill-opacity", 1e-6);

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


            // Toggle children on click.
            function toggleChildren(d) {
                if (d.children) {

                    d._children = d.children;
                    d.children = null;
                    for (var i = 0; i <  d._children.length; i++) {
                        if(d._children.length >20){
                            height=height-30;
                        }
                    }
                    if(d._children.length >20){
                        tree = d3.layout.tree()
                        .size([height, width]);
                        update(d);
                    }else{
                        update(d);
                    }
                    
                }
                else {	
                    if (d.metaRef.ref.type == null || d.metaRef.ref.type == "simple") {
                        return false;
                    }
                    $('.show-graph-body').hide();
                    $('#graphloader').show();
                    $('#errorMsg').hide();
                    CommonService.getTreeGraphResults(d.metaRef.ref.uuid, d.version | "", "1").then(function (result) {
                       $('.show-graph-body').show();
                       $('#graphloader').hide();

                       for (var i = 0; i <  result.data.children.length; i++) {
                        result.data.children[i].index = i;
                            if(result.data.children.length >20){
                                height=height+30;
                            }
                        }
                        var r;
                        r = result.data;
                        r.parent = d.name
                        d._children = r.children
                        d.children = d._children;
                        d._children = null;
                        if(result.data.children.length >20){
                            tree = d3.layout.tree()
                            .size([height, width]);
                            update(d);
                        }else{
                            update(d);
                        }
                        
                    });
                }
            }//End ToggleChildren
            
            function mouseoverNode(d){
                var e = d3.event;
                scope.nodeDetail=d
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
                if(scope.nodeDetail.caption)
                 $(".tooltipcustom").css("display", "block");
            }
            
            function rightClickNode(d,i){
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

                finally {}
                return caption
            }

            function getColorCode(d) {
                try {
                    var color = dagMetaDataService.elementDefs[d.nodeType.toLowerCase()].color
                } 
                catch (e) {
                    if (d.nodeType.toLowerCase().indexOf("from_base") != -1) {
                        var color = dagMetaDataService.elementDefs["from_base"].color
                    }
                    else {
                        var color = '#000000'
                    }
                }
                finally {}
                return color;
            }

            function navigateTo(data, d) {
                if (d == "Knowledge Graph" && data.metaRef.ref.type != null) {
                    data.metaRef.ref.name = data.name
                    data.metaRef.ref.type = data.nodeType
                    dagMetaDataService.navigateTo(data.metaRef);
                }
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
      <div id="network-graph-wrapper" style="height:500px;"></div>
      `
    };
})