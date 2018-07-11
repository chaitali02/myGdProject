var InferyxApp = angular.module("InferyxApp");
InferyxApp.directive('fdGraphDirective', function ($timeout,$rootScope,CommonService,GraphpodService,dagMetaDataService,CF_META_TYPES,CF_GRAPHPOD) {
    return {
        scope: {
            uuid: "=",
            version: "="
        },
        link: function (scope, element, attrs) {
            var graph;
            var menus = ["Show Details"];
            scope.selectedAllEdgeRow=false;
            scope.selectedAllNodeRow=false;
            scope.isFilterSelect=true;
            scope.filter=null;
            scope.operator=[
                {"caption":"EQUAL TO (=)","value":"="},
                {"caption":"LESS THAN","value":"<"},
                {"caption":"GREATER THAN","value":">"},
                {"caption":"LESS OR EQUAL","value":"<="},
                {"caption":"GREATER OR EQUAL (>=)  ","value":">="},
                {"caption":"BETWEEN","value":"BETWEEN"},
            ];
            scope.logicalOperator = ["OR", "AND"];
            var notify = {
                type: 'success',
                title: 'Success',
                content: '',
                timeout: 5000 //time in ms
            };
            scope.spacialOperator=['<','>','<=','>='];
            scope.noRecordFound=false;
            scope.isGraphInProgess=false;
            scope.getGraphpodObj=function(){
                CommonService.getOneByUuidAndVersion(scope.uuid,scope.version,CF_META_TYPES.graphexec).then(function(response){onSuccessGetByOneUuidAndVersion(response.data)});
                function onSuccessGetByOneUuidAndVersion(response){
                    CommonService.getOneByUuidAndVersion(response.dependsOn.ref.uuid,response.dependsOn.ref.version,CF_META_TYPES.graphpod).then(function(response){onSuccessGetByOneUuidAndVersion(response.data)});
                    function onSuccessGetByOneUuidAndVersion(response){
                        scope.graphpodData=response;
                    }
                }
            }
            scope.getGraphpodObj();
            scope.getFilterData=function(){
                var graphFilerBody=null;
                if(scope.filter !=null){
                    graphFilerBody={};
                    var graphFilter={};
                    var edgeFilter=[];
                    var nodeFilter=[];
                    if(scope.filter.nodeTableArray && scope.filter.nodeTableArray.length >0){
                        for(var i=0;i<scope.filter.nodeTableArray.length;i++){
                            var nodeFilterObj={};
                            var operand={};
                            
                            nodeFilterObj.logicalOperator=scope.filter.nodeTableArray[i].logicalOperator;
                            nodeFilterObj.operator=scope.filter.nodeTableArray[i].operator;
                            operand.propertyName=scope.filter.nodeTableArray[i].selectAttribute.attributeName;
                            if(scope.filter.nodeTableArray[i].operator =="BETWEEN"){
                                var value1=scope.filter.nodeTableArray[i].rhsvalue1;//"'"+scope.filter.nodeTableArray[i].rhsvalue1+"'";
                                var value2=scope.filter.nodeTableArray[i].rhsvalue2;//"'"+scope.filter.nodeTableArray[i].rhsvalue2+"'";
                                operand.propertyValue=value1+" and "+value2;
                            }else if(scope.filter.nodeTableArray[i].operator =="="){
                                var value=scope.filter.nodeTableArray[i].rhsvalue.replace(/["']/g, "");
                                operand.propertyValue="'"+value+"'"
                            }
                            else{
                                var value=scope.filter.nodeTableArray[i].rhsvalue.replace(/["']/g, "");
                                operand.propertyValue=value;//"'"+value+"'"
                            }
                            nodeFilterObj.operand=operand;
                            nodeFilter[i]=nodeFilterObj
                        }
                    }
                    if(scope.filter.edgeTableArray && scope.filter.edgeTableArray.length >0){
                        for(var i=0;i<scope.filter.edgeTableArray.length;i++){
                            var edgeFilterObj={};
                            var operand={};
                            edgeFilterObj.logicalOperator=scope.filter.edgeTableArray[i].logicalOperator;
                            edgeFilterObj.operator=scope.filter.edgeTableArray[i].operator;
                            operand.propertyName=scope.filter.edgeTableArray[i].selectAttribute.attributeName;
                            if(scope.filter.edgeTableArray[i].operator =="BETWEEN"){
                                var value1=scope.filter.edgeTableArray[i].rhsvalue1;//"'"+scope.filter.edgeTableArray[i].rhsvalue1+"'"
                                var value2=scope.filter.edgeTableArray[i].rhsvalue2;//"'"+scope.filter.edgeTableArray[i].rhsvalue2+"'"
                                operand.propertyValue=value1+" and "+value2;
                            }else if(scope.filter.edgeTableArray[i].operator =="="){
                                var value=scope.filter.edgeTableArray[i].rhsvalue.replace(/["']/g, "");
                                operand.propertyValue="'"+value+"'"

                            }
                            else{
                                var value=scope.filter.edgeTableArray[i].rhsvalue.replace(/["']/g, "");
                                operand.propertyValue=value;//"'"+value+"'"
                            }
                            edgeFilterObj.operand=operand;
                            edgeFilter[i]=edgeFilterObj
                        }
                    }
                    graphFilter.nodeFilter=nodeFilter;
                    graphFilter.edgeFilter=edgeFilter;
                    graphFilerBody.graphFilter=graphFilter;
                }
                return graphFilerBody;
            }

            scope.search=function(){
                scope.isGraphShow=false;
                scope.isGraphInProgess=true;
                scope.noRecordFound=false;
                scope.isError=false;
                scope.isFilterSelect=false;
                var graphFilerBody= scope.getFilterData();
               
                console.log(JSON.stringify(graphFilerBody));
                scope.getGraphPodResults(scope.uuid,scope.version,scope.nodeId,scope.nodeType,"1","graphpod",graphFilerBody);
              
            }

            scope.getGraphPodResults=function(uuid,version,nodeId,nodeType,degree,type,data){
                GraphpodService.getGraphPodResults(uuid,version,nodeId,nodeType,degree,type,data).then(function (response) {onSuccessGetGraphPodResults(response.data)},function(response){onError(response.data)});
                var onSuccessGetGraphPodResults=function(response){
                    scope.isGraphInProgess=false;
                    if(response.edges.length >0){    
                        scope.graphData=response;
                        scope.isGraphShow=true;
                    }
                    else{
                        scope.noRecordFound=true;
                    }
                    drawGraph();
                }
                var onError=function(response){
                    scope.isGraphInProgess=false;
                    scope.isError=true;

                }
            }
            
            $rootScope.refreshFDGraph=function(){
                scope.search(); 
            }
            function myGraph() {
                this.addNode = function (n) {
                    if (!findNode(n.id)) {
                        nodes.push({ "id": n.id, "label": n.label,"nodeName":n.nodeName,"nodeType":n.nodeType,"nodeProperties":n.nodeProperties,"nodeIcon":n.nodeIcon,"propertyId":n.propertyId,"propertyInfo":n.propertyInfo,"type":n.type});
                        update();
                    }
                };

                this.addLink = function (d) {
                    links.push({ "source": findNode(d.source.id), "target": findNode(d.target.id), "value": d.value.replace(/["\"]/g, ""),"edgeName":d.edgeName,"edgeType":d.edgeType,"edgeProperties":d.edgeProperties});
                    update();
                };

                this.initialize = function (data) {
                    data.edges.forEach(function (d) {
                        graph.addNode(d.source);
                        graph.addNode(d.target);
                        graph.addLink(d);
                    });
                };

                var findNode = function (nodeId) {
                    for (var i in nodes) {
                        if (nodes[i].id === nodeId) {
                            return nodes[i];
                        }
                    };
                };

                var countSiblingLinks = function (source, target) {
                    var count = 0;
                    for (var i = 0; i < links.length; ++i) {
                        if ((links[i].source.id == source.id && links[i].target.id == target.id) || (links[i].source.id == target.id && links[i].target.id == source.id))
                            count++;
                    };
                    return count;
                };

                var getSiblingLinks = function (source, target) {
                    var siblings = [];
                    for (var i = 0; i < links.length; ++i) {
                        if ((links[i].source.id == source.id && links[i].target.id == target.id) || (links[i].source.id == target.id && links[i].target.id == source.id))
                            siblings.push(links[i].value);
                    };
                    return siblings;
                };

                var w = window.innerWidth - 20,
                    h =800,
                    middle = w / 2;
                var linkDistance = 300;

                var colors = d3.scale.category20();
                d3.select("#fDGraph").select("svg").remove();
                var svg = d3.select("#fDGraph")
                    .append("svg:svg")
                    .attr("width", w)
                    .attr("height", h)
                    .style("z-index", -10)
                    .attr("id", "svg");
                  
                svg.append('svg:defs').selectAll('marker')
                    .data([0])
                    .enter()
                    .append('svg:marker')
                    .attr({
                        'id': "arrowhead",
                        'viewBox': '0 -5 10 10',
                        'refX': 17,
                        'refY': 0,
                        'orient': 'auto',
                        'markerWidth': 10,
                        'markerHeight': 10,
                        'markerUnits': "strokeWidth",
                        'xoverflow': 'visible'
                    })
                    .append('svg:path')
                    .attr('d', 'M0,-5L10,0L0,5')
                    .attr('fill', '#ccc')
                    .attr("marker-end", "url(#end)");
                    svg.append('svg:defs').selectAll('marker')
                    .data([1])
                    .enter()
                    .append('svg:marker')
                    .attr({
                        'id': "arrowheadhover",
                        'viewBox': '0 -5 10 10',
                        'refX': 15,
                        'refY': 0,
                        'orient': 'auto',
                        'markerWidth': 10,
                        'markerHeight':10,
                        'markerUnits': "strokeWidth",
                        'xoverflow': 'visible'
                    })
                    .append('svg:path')
                    .attr('d', 'M0,-5L10,0L0,5')
                    .attr('fill', '#32c5d2')
                    .attr("marker-end", "url(#end)");

                var force = d3.layout.force();

                var nodes = force.nodes(),
                    links = force.links();

                var update = function () {

                    var path = svg.selectAll("path.link")
                        .data(force.links());

                    path.enter().append("svg:path")
                        .attr("id", function (d) {
                            return d.source.id + "-" + d.value + "-" + d.target.id;
                        })
                        .attr("class", "link")
                        .attr('marker-end', 'url(#arrowhead)')
                        .on("click", onClickEdge)
                        // .on("mouseover",function(d){
                        //     d3.select(this).attr({
                                
                        //     })
                        // })
                        // .on("mouseout", function (d) {
                    
                        // });;
                    path.exit().remove();

                    var pathInvis = svg.selectAll("path.invis")
                        .data(force.links());

                    pathInvis.enter().append("svg:path")
                        .attr("id", function (d) {
                            return "invis_" + d.source.id + "-" + d.value + "-" + d.target.id;
                        })
                        .attr("class", "invis");

                    pathInvis.exit().remove();

                    var pathLabel = svg.selectAll(".pathLabel")
                        .data(force.links());

                    pathLabel.enter().append("g").append("svg:text")
                        .attr("class", "pathLabel")
                        .append("svg:textPath")
                        .attr("startOffset", "50%")
                        .attr("text-anchor", "middle")
                        .attr("xlink:href", function (d) { return "#invis_" + d.source.id + "-" + d.value + "-" + d.target.id; })
                      //  .style("fill", "#cccccc")
                        .style("font-size", 11)
                        .text(function (d) { return d.value; })  
                         //.on("click", onClickEdge);
                         .on("contextmenu", rightClickEdge);

                    var node = svg.selectAll("g.node1")
                        .data(force.nodes());

                    var nodeEnter = node.enter().append("g")
                        .attr("class", "node1")
                        .call(force.drag)
                        .on("dblclick", onDbClickNode)
                        .on('contextmenu', rightClickNode)
                        // .on("mouseover", mouseoverNode)
                        .on("mouseout", function (d) {
                            scope.nodeDetail = null;
                            $(".tooltipcustom").css("display", "none");
    
                        });
                        
                  
                    nodeEnter.append("svg:circle")
                        .attr("r", 15)
                        .attr("id", function (d) {
                            return "Node;" + d.id;
                        })
                        .attr("stroke",function(d){
                            var temp=d;
                            var result=null;
                            if(temp.propertyInfo !=null &&  typeof temp.propertyInfo=== 'string' ) {
                                temp=temp.propertyInfo.replace('{', ' ');
                                temp=temp.replace('}', ' ');
                                var tempPInfo=temp.split(",");
                                for(var i=0;i<tempPInfo.length;i++){
                                    var temp=tempPInfo[i].split(":");
                                    if(d.propertyId ==temp[0].trim()){
                                        result =temp[1];
                                        break;
                                    }                                
                                }
                                if(result !=null){
                                    return result;
                                }else{
                                    return 'white';
                                }
                                
                            }
                        })
                        .attr("class", "nodeStrokeClass")
                        //.attr("fill", "#0db7ed")
                        .attr("fill", function(d) { try{ return CF_GRAPHPOD.nodeIconMap[d.nodeIcon].color}catch(e){ return"#0db7ed"}})
                    nodeEnter.append('text')
                        .attr('font-family', 'FontAwesome')
                        .attr("class", "iconNode")
                        .attr('text-anchor', 'middle')
                        .attr('dominant-baseline', 'central')
                        .attr('font-size', function(d) { return 15+'px'} )
                        .text(function(d) {  try{return CF_GRAPHPOD.nodeIconMap[d.nodeIcon].code}catch(e){ return""}}); 
                    nodeEnter.append("svg:text")
                        .attr("class", "textClass")
                        .attr("x", 20)
                        .attr("y", ".31em")
                        .text(function (d) {
                            return d.label;
                        });
                    nodeEnter.append("svg:foreignObject")
                        .attr("width", 20)
                        .attr("height", 20)
                        .attr("y", "10")
                        .attr("x","5")
                        .append("xhtml:img")
                            .attr("class","node-refresh")
                            .style("display","none")
                            .attr("src","lib/images/loadingimg.gif")
                    node.exit().remove();

                    function arcPath(leftHand, d) {
                        var x1 = leftHand ? d.source.x : d.target.x,
                            y1 = leftHand ? d.source.y : d.target.y,
                            x2 = leftHand ? d.target.x : d.source.x,
                            y2 = leftHand ? d.target.y : d.source.y,
                            dx = x2 - x1,
                            dy = y2 - y1,
                            dr = Math.sqrt(dx * dx + dy * dy),
                            drx = dr,
                            dry = dr,
                            sweep = leftHand ? 0 : 1;
                            siblingCount = countSiblingLinks(d.source, d.target)
                            xRotation = 0,
                            largeArc = 0;

                        if (siblingCount > 1) {
                             if(siblingCount >10){
                                largeArc=1;
                             }
                           
                            var siblings = getSiblingLinks(d.source, d.target);
                           // console.log(siblings);
                            var arcScale = d3.scale.ordinal()
                                .domain(siblings)
                                .rangePoints([-1, siblingCount]);
                            drx = drx / (1 + (1 / siblingCount) * (arcScale(d.value) - 1));
                            dry = dry / (1 + (1 / siblingCount) * (arcScale(d.value) - 1));
                        }

                        return "M" + x1 + "," + y1 + "A" + drx + ", " + dry + " " + xRotation + ", " + largeArc + ", " + sweep + " " + x2 + "," + y2;
                    }

                    force.on("tick", function (e) {
                        var q = d3.geom.quadtree(nodes),
                            i = 0,
                            n = nodes.length,
                            k = .1 * e.alpha;

                        while (++i < n) q.visit(collide(nodes[i]));

                        node.attr("transform", function (d) {
                            return "translate(" + d.x + "," + d.y + ")";
                        });

                        path.attr("d", function (d) {
                            return arcPath(true, d);
                        });

                        pathInvis.attr("d", function (d) {
                            return arcPath(d.source.x < d.target.x, d);
                        });
                    });

                    force
                        .charge(-10000)
                        .friction(0.1)
                        .linkDistance(linkDistance)
                        .size([w, h])
                        .start();
                    keepNodesOnTop();

                }

                update();

                function collide(node) {
                    var r = node.radius + 16,
                        nx1 = node.x - r,
                        nx2 = node.x + r,
                        ny1 = node.y - r,
                        ny2 = node.y + r;
                    return function (quad, x1, y1, x2, y2) {
                        if (quad.point && (quad.point !== node)) {
                            var x = node.x - quad.point.x,
                                y = node.y - quad.point.y,
                                l = Math.sqrt(x * x + y * y),
                                r = node.radius + quad.point.radius;
                            if (l < r) {
                                l = (l - r) / l * .5;
                                node.x -= x *= l;
                                node.y -= y *= l;
                                quad.point.x += x;
                                quad.point.y += y;
                            }
                        }
                        return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
                    };
                }
            }

            function drawGraph() {
                graph = new myGraph();
                graph.initialize(scope.graphData);
            }


            function keepNodesOnTop() {
                $(".nodeStrokeClass").each(function (index) {
                    var gNode = this.parentNode;
                    gNode.parentNode.appendChild(gNode);
                });
            }
            function mouseoverNode(d){
                console.log(d)
                var e = d3.event;
                scope.nodeDetail = d;
                scope.nodeDetail.caption="Node Detail"
             //   $("#colorID").css("background-color","#0bb7ed");
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
                $(".tooltipcustom").css("display", "block");
            }
            function onClickNode(d){
                //console.log(d);
                scope.nodeDetailModel=null;
                var nodeProperties=[];
                scope.nodeDetailModel=d;
                var temp=d;
                if( typeof temp.nodeProperties=== 'string' ) {
                    temp=temp.nodeProperties.replace('{', ' ');
                    temp=temp.replace('}', ' ');
                    var tempNPS=temp.split(",");
                    for(var i=0;i<tempNPS.length;i++){
                        var temp=tempNPS[i].split(":");
                        var npObj={}
                        npObj.name=temp[0].replace(/["\"]/g, "");
                        npObj.value=temp[1].replace(/["\"]/g, "");
                        nodeProperties[i]=npObj;
                    }
                    scope.nodeDetailModel.nodeProperties=nodeProperties;
                }
                $('#nodeDetail').modal({
                    backdrop: 'static',
                    keyboard: false
                });	
            }

            function onDbClickNode(d){
                console.log(d);
                var this_node=this;
                $(this_node).find(".node-refresh").css("display","block");
                var graphFilerBody= scope.getFilterData();
                GraphpodService.getGraphPodResults(scope.uuid,scope.version,d.id,d.nodeType,'1',"graphpod",graphFilerBody).then(function (response) {onSuccessGetGraphPodResults(response.data)},function(response){onError(response.data)});
                var onSuccessGetGraphPodResults=function(response){
                    $(this_node).find(".node-refresh").css("display","none");
                    if(response.edges.length >0){    
                        scope.graphData=response;
                        graph.initialize(response);
                    }else{
                        notify.type = 'info',
			            notify.title = 'Info',
			            notify.content = 'No Record Found'
			            scope.$emit('notify', notify);
                    }
                }
                var onError=function(response){ 
                    $(this_node).find(".node-refresh").css("display","none");
                    notify.type = 'error',
			        notify.title = 'Error',
			        notify.content = "Some Error Occurred"
			        scope.$emit('notify', notify);  
                }
            }


            
            function rightClickEdge(d, i) {
                d3.event.preventDefault();
                var this_node=this ;
                var Nodedata = d;
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
                       
                        onClickEdge(Nodedata,this_node);
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
            function rightClickNode(d, i) {
                d3.event.preventDefault();
                var this_node=this ;
                var Nodedata = d;
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
                       
                        onClickNode(Nodedata,this_node);
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
            function mouseoverEdge(d){
             // console.log(d)
            //     var e = d3.event;
            //     scope.edgeDetail = d;
            //     scope.edgeDetail.caption="Edge Detail"
            //    // $("#colorID").css("background-color","#0bb7ed");
            //     var xPercent = e.clientX / $(window).width() * 100;
            //     var left;
            //     var top;
            //     if (parseInt(xPercent) > 50) {
            //         left = (e.clientX - 400) + "px";
            //         top = e.clientY + "px";
            //     }
            //     else {
            //         left = (e.clientX + 40) + "px";
            //         top = e.clientY + "px";
            //     }
            //     $(".tooltipcustom").css("left", left);
            //     $(".tooltipcustom").css("top", top);
            //     $(".tooltipcustom").css("display", "block");
            }
            function onClickEdge(d){
                console.log(d);
                scope.edgeDetailModel=null;
                var edgeProperties=[];
                scope.edgeDetailModel=d;
                var temp=d;
                if( typeof temp.edgeProperties=== 'string' ) {
                    temp=temp.edgeProperties.replace('{', ' ');
                    temp=temp.replace('}', ' ');
                    var tempEP=temp.split(",");
                    for(var i=0;i<tempEP.length;i++){
                        var temp=tempEP[i].split(":");
                        var epObj={}
                        epObj.name=temp[0].replace(/["\"]/g, "");
                        epObj.value=temp[1].replace(/["\"]/g, "");
                        edgeProperties[i]=epObj;
                    }
                    scope.edgeDetailModel.edgeProperties=edgeProperties;
                }
                
                $('#edgeDetail').modal({
                    backdrop: 'static',
                    keyboard: false
                });	

            }
            scope.getAttributesByNodeSource=function(nodeIndex,index){
                allAttributeInto=[];
                for(var j=0;j<scope.graphpodData.nodeInfo[nodeIndex].nodeProperties.length;j++){
                    var nodePropertiesObj={};
                    nodePropertiesObj.uuid=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].ref.uuid;
                    nodePropertiesObj.type=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].ref.type;
                    nodePropertiesObj.name=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].ref.name;
                    nodePropertiesObj.attributeId=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].attrId;
                    nodePropertiesObj.attrType=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].attrType;
                    nodePropertiesObj.attributeName=scope.graphpodData.nodeInfo[nodeIndex].nodeProperties[j].attrName;
                    allAttributeInto[j]=nodePropertiesObj;
                }
                scope.nodeTableArray[index].allAttributeInto=allAttributeInto;
            }

            scope.allNodeRow = function () {
                angular.forEach(scope.nodeTableArray, function (filter) {
                    filter.selected = scope.selectedAllNodeRow;
                });
            }
            
            scope.addNodeRow = function () {
                if (scope.nodeTableArray == null) {
                    scope.nodeTableArray = [];
                }
                
                var source=[];
                for(var i=0;i<scope.graphpodData.nodeInfo.length;i++){
                    var sourceObj={};
                    sourceObj.index=i;
                    sourceObj.uuid=scope.graphpodData.nodeInfo[i].nodeSource.ref.uuid;
                    sourceObj.type=scope.graphpodData.nodeInfo[i].nodeSource.ref.type;
                    sourceObj.name=scope.graphpodData.nodeInfo[i].nodeSource.ref.name
                    source[i]=sourceObj;
                }
                
                var nodeTable = {};
                nodeTable.id=scope.nodeTableArray.length;
                nodeTable.nodeId;
                nodeTable.logicalOperator=scope.nodeTableArray.length >0 ?scope.logicalOperator[0]:"";
              //  nodeTable.operator=scope.operator[0];
                nodeTable.source=source;
                nodeTable.allAttributeInto=[]
                scope.nodeTableArray.splice(scope.nodeTableArray.length, 0, nodeTable);
            }
            
            scope.removeNodeRow=function(){
                var newDataList = [];
                scope.selectedAllNodeRow = false;
                angular.forEach(scope.nodeTableArray, function (selected) {
                    if (!selected.selected) {
                        newDataList.push(selected);
                    }
                });
                if (newDataList.length > 0) {
                    newDataList[0].logicalOperator = "";
                }
                scope.nodeTableArray = newDataList;
            }
            
            scope.allEdgeRow = function () {
                angular.forEach(scope.edgeTableArray, function (filter) {
                    filter.selected = scope.selectedAllEdgeRow;
                });
            } 
            scope.getAttributesByEdgeSource=function(edgeIndex,index){
                allAttributeInto=[];
                for(var j=0;j<scope.graphpodData.edgeInfo[edgeIndex].edgeProperties.length;j++){
                    var edgePropertiesObj={};
                    edgePropertiesObj.uuid=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].ref.uuid;
                    edgePropertiesObj.type=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].ref.type;
                    edgePropertiesObj.name=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].ref.name;
                    edgePropertiesObj.attributeId=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].attrId;
                    edgePropertiesObj.attrType=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].attrType;
                    edgePropertiesObj.attributeName=scope.graphpodData.edgeInfo[edgeIndex].edgeProperties[j].attrName;
                    allAttributeInto[j]=edgePropertiesObj;
                }
                scope.edgeTableArray[index].allAttributeInto=allAttributeInto;
            }
            
            scope.addEdgeRow=function(){		
                if (scope.edgeTableArray == null) {
                    scope.edgeTableArray = [];
                }
                var source=[];
                for(var i=0;i<scope.graphpodData.edgeInfo.length;i++){
                    var sourceObj={};
                    sourceObj.index=i;
                    sourceObj.uuid=scope.graphpodData.edgeInfo[i].edgeSource.ref.uuid;
                    sourceObj.type=scope.graphpodData.edgeInfo[i].edgeSource.ref.type;
                    sourceObj.name=scope.graphpodData.edgeInfo[i].edgeSource.ref.name
                    source[i]=sourceObj;
                }
                var edgeTable = {};
                edgeTable.id=scope.edgeTableArray.length;
                edgeTable.nodeId;
                edgeTable.logicalOperator=scope.edgeTableArray.length >0 ?scope.logicalOperator[0]:"";
             //   edgeTable.operator=scope.operator[0];
                edgeTable.source=source;
                edgeTable.allAttributeInto=[];
                scope.edgeTableArray.splice(scope.edgeTableArray.length, 0, edgeTable);
            }
                       
            scope.removeEdgeRow=function(){
                var newDataList = [];
                scope.selectedAllEdgeRow = false;
                angular.forEach(scope.edgeTableArray, function (selected) {
                    if (!selected.selected) {
                        newDataList.push(selected);
                    }
                });
                if (newDataList.length > 0) {
                    newDataList[0].logicalOperator = "";
                }
                scope.edgeTableArray = newDataList;
            }
            
            scope.$on('transferUp',function(event,data){
                console.log('on working');
                scope.applyFilter();
            });

            scope.applyFilter=function(){
                $('#applyFilter').modal({
                    backdrop: 'static',
                    keyboard: false
                });	
            }
            
            scope.submitFilter=function(){
                scope.filter={};
                scope.filter.nodeTableArray=scope.nodeTableArray;
                scope.filter.edgeTableArray=scope.edgeTableArray;
                $('#applyFilter').modal('hide');
                scope.search(); 
            }
        },
        
        templateUrl: 'views/fd-template.html',
    };
})