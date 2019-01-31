
import { Component, Input, ViewEncapsulation,ElementRef} from '@angular/core';
import { D3Service, D3, Selection } from 'd3-ng2-service';
// import * as d3 from "d3"; 
import * as $ from 'jquery';
import { AppMetadata } from '../../../app.metadata';
import { CommonService } from '../../../metadata/services/common.service';
@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'knowledge-graph',
    styleUrls: ['./knowledgeGraph.css'],
    templateUrl: './knowledgeGraph.templete.html'
})
export class KnowledgeGraphComponent {
    showButton: boolean;
    version: any;
    uuid: any;
    treeOrignaldata: any;
    treeData: any;
    showTooltip: boolean;

    angularThis = this
    treemap: any;
    height: number;
    width: number;
    degree: any;
    graphdata: { "parent": any; "dataType": any; "name": string; "color": any; "active": string; "id": any; "nodeType": string; "version": string; "createdOn": string; "metaRef": { "ref": { "type": string; "uuid": string; "version": string; "name": string; }; "value": any; }; "children": any[]; };


    private d3: D3 // Define the private member which will hold the d3 reference
    menus = ["Show Details"];
    root
    zoom
    MOVE_STEP = 100;
    zoomSize = 10;
    margin={}


    i = 0
    duration = 750;
    tree;
    svg;
    // degree = "1";
    constructor(private element: ElementRef,private d3Service: D3Service, public metaconfig: AppMetadata, public _commonService: CommonService) {
        this.d3 = d3Service.getD3();
    }
    ngOnInit() {
        this.margin = {
            top: 20,
            right: 180,
            bottom: 20,
            left: 240
        }
        this.width = 1060 - this.margin["right"] - this.margin["left"],
        this.height = 600 - this.margin["top"] - this.margin["bottom"];
        this.degree = 1
        
        //this.getGraphData()

    }
    getGraphData(uuid, version) {
        this.uuid=uuid
        this.version=version
        this._commonService.getGraphResults(version, this.degree, uuid)
            .subscribe(
            response => {
                this.onSuccessgetGraphResults(response)
            },
            error => console.log("Error :: " + error));



    }
    onSuccessgetGraphResults(response) {

        this.treeData = response;
        if (response.children && response.children.length > 0) {
            response.index = 0;
            response.depth=0;
            for (let i = 0; i < response.children.length; i++) {
                response.children[i].index = i;
                response.children[i].depth=1;
            }
        }
        this.createSvg(this.height, this.width)
        this.treeOrignaldata = response;
        this.treemap = this.d3.tree().size([this.height, this.width]);
        this.root = this.d3.hierarchy(this.treeData, function (d) { return d.children; });
        this.root.x0 = this.height / 2;
        this.root.y0 = 0;

        // Collapse after the second level
        this.root.children.forEach((item) => this.collapse(item));

        this.update(this.root, this.angularThis);
        
    }
    collapse(d: any) {

        if (d.children) {
            d._children = d.children;
            //var clps = this.collapse;
            d._children.forEach((item) => this.collapse(item));
            d.children = null;
        }
    }

    update(source, angularThis) {
        var w = this.d3.select("svg.tree-graph").attr("width");
        this.d3.select("svg.tree-graph").attr("width", parseInt(w) + 100);
        this.d3.select("svg.tree-graph").attr("height", this.height);

        var treeData = this.treemap(source);
        var nodes = treeData.descendants(),
            links = treeData.descendants().slice(1);
        console.log(nodes)
        nodes.forEach(function (d) { d.y = d.depth * 250 });

        var node = this.svg.selectAll('g.node')
            .data(nodes, function (d) { return d.id || (d.id = ++angularThis.i); });

        var nodeEnter = node.enter().append('g')
            .attr('class', 'node')
            .attr("transform", function (d) {
                return "translate(" + source.y0 + "," + source.x0 + ")";
            })
            .on('click', function (d) { angularThis.click(d, angularThis) })
            .on("mouseover", function (d) { angularThis.mouseoverNode(d, angularThis) })
            .on("mouseout", function (d) {
                $(".tooltipcustom").css("display", "none");
                this.nodeDetail = null;
            })
            .on('contextmenu', function (d) { angularThis.rightClickNode(d,this.i,angularThis) })


        nodeEnter.append('circle')
            .attr('class', 'node')
            .attr('r', 1e-6)
            .style("fill", function (d) {
                return d._children ? "#ccff99" : angularThis.getColorCode(d);;
            });

        nodeEnter.append('text')
            .attr("dy", ".35em")
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
            // .style("fill-opacity", 1e-6)
            .text(function (d) {
                if (d.parent == null) {
                    return d.data.name.length > 20 ? d.data.name.substring(0, 15) + "...." : d.data.name
                } else {
                    return d.data.name.length > 15 ? d.data.name.substring(0, 15) + ".." : d.data.name;
                }
            })
        // .text(function(d) { return d.data.name; });
        nodeEnter.append("svg:foreignObject")
        .attr("width", 20)
        .attr("height", 20)
        .attr("y", "-10")
        .attr("x", function(d){
            var x="100px";
            return x
        })
        .append("xhtml:img")
        .attr("class",function(d){
           // alert(d)
           return "node-refresh"+d.depth+d.data.index
           // console.log(d)
            // var x="100px";
            // return x
        })
        .style("display","none")
        .attr("src","assets/img/loadingimg.gif")


        let nodeUpdate = nodeEnter.merge(node);
        console.log(nodeUpdate)

        nodeUpdate.transition()
            .duration(this.duration)
            .attr("transform", function (d) {
                return "translate(" + d.y + "," + d.x + ")";
            });

        nodeUpdate.select('circle.node')
            .attr('r', 10)
            .style("fill", function (d) {
                return d._children ? "#ccff99" : angularThis.getColorCode(d);
            })
            .attr('cursor', 'pointer');

        var nodeExit = node.exit().transition()
            .duration(this.duration)
            .attr("transform", function (d) {
                return "translate(" + source.y + "," + source.x + ")";
            })
            .remove();

        nodeExit.select('circle')
            .attr('r', 1e-6);

        var link = this.svg.selectAll('path.link')
            .data(links, function (d) { return d.id; });


        var linkEnter = link.enter().insert('path', "g")
            .attr("class", "link")

            .attr('d', function (d) {
                var o = { x: source.x0, y: source.y0 }
                return angularThis.diagonal(o, o)
            });

        var linkUpdate = linkEnter.merge(link);

        linkUpdate.transition()
            .duration(this.duration)
            .attr('d', function (d) { return angularThis.diagonal(d, d.parent) });

        var linkExit = link.exit().transition()
            .duration(this.duration)
            .attr('d', function (d) {
                var o = { x: source.x, y: source.y }
                return angularThis.diagonal(o, o)
            })
            .remove();

        var linktext = angularThis.svg.selectAll("g.link")
            .data(links, function (d) {
                return d.id
            });

        var linktextEnter = linktext.enter()
            .insert("g")
            .attr("class", "link")
            .append("text")
            .attr("text-anchor", "middle")
            .attr("font-family", "Arial, Helvetica, sans-serif")
            .attr("fill", "73879C")
            .style("font", "normal 11px Arial")
            .attr("x", 25) //add new for link text
            .text(function (d) {
                return d.data.nodeType;
            })
        //  .append("text:title")
        //  .text(function (d) {
        //      return d.data.nodeType;
        //  });

        // Transition link text to their new positions
        var linkTextUpdate = linktextEnter.merge(linktext);
        linkTextUpdate.transition()
            .duration(angularThis.duration)
            .attr("transform", function (d) {
                return "translate(" + ((d.parent.y + d.y) / 2) + "," + ((d.parent.x + d.x) / 2) + ")";
            });

        //Transition exiting link text to the parent's new position.
        var linkTextExit = linktext.exit().transition().remove();



        nodes.forEach(function (d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
        this.showButton=true

    }
    diagonal(s, d) {
        var path = `M ${s.y} ${s.x}
                  C ${(s.y + d.y) / 2} ${s.x},
                    ${(s.y + d.y) / 2} ${d.x},
                    ${d.y} ${d.x}`
        return path
    }

    click(d, angularThis) {
        
        if (d.children) {
            if(d.depth==0){
                angularThis.treeData = angularThis.treeOrignaldata;
                angularThis.treeData._children=d.children
                angularThis.treeData.children=null;
                angularThis.treeData = angularThis.d3.hierarchy(angularThis.treeData, function (d) { return d.children; });
                angularThis.treeData.x0 = angularThis.height / 2;
                angularThis.treeData.y0 = 0;
            }
            else{
                angularThis.treeData = angularThis.treeOrignaldata;
                angularThis.findCollpaseChild(angularThis.treeData.children,d,angularThis)               
                angularThis.treeData = angularThis.d3.hierarchy(angularThis.treeData, function (d) { return d.children; });
                angularThis.treeData.x0 = angularThis.height / 2;
                angularThis.treeData.y0 = 0;
            }
            
            // Collapse after the second level
    
            angularThis.update(angularThis.treeData, angularThis);
        }
        else if(d.depth==0 && d.data._children && d.data._children.length >0){
            $(".node-refresh"+d.depth+d.data.index).css("display", "block");
            this.getGraphData(this.uuid, this.version)
        }
        else {
            if (d.data.metaRef.ref.type == null || d.data.metaRef.ref.type == "simple") {
                return false;
            }        
            console.log(this.element)  
            //var this_node = this
            $(".node-refresh"+d.depth+d.data.index).css("display", "block");
            this._commonService.getGraphResults(d.data.metaRef.ref.version, this.degree, d.data.metaRef.ref.uuid)
                .subscribe(
                response => {
                    this.onSuccessgetGraphResultsHeiraracy(response, angularThis, d)
                },
                error => console.log("Error :: " + error));           
        }
    }
    onSuccessgetGraphResultsHeiraracy(response, angularThis, d) {    
        if (response.children && response.children.length > 0) {
            for (let i = 0; i < response.children.length; i++) {
                response.children[i].index = i;
                if(d.parent !=null)
                response.children[i].depth=d.parent.children[0].depth+1;
            }
        }
        var r;
        r = response;
        r.parent = d.name;
        d.active = r.active;
        d.createdOn = r.createdOn;
        d.dataType = r.dataType;
        d.desc = r.desc;
        d.id = r.id;
        d.metaRef = r.metaRef
        d.name = r.name;
        d.nodeType = r.nodeType;
        d._children = r.children
        d.children = d._children;
        d._children = null;
        angularThis.treeData = angularThis.treeOrignaldata;        
        angularThis.findChild(angularThis.treeData.children,d,angularThis)
        angularThis.treeData = angularThis.d3.hierarchy(angularThis.treeData, function (d) { return d.children; });
        angularThis.treeData.x0 = angularThis.height / 2;
        angularThis.treeData.y0 = 0;
        angularThis.update(angularThis.treeData, angularThis);
    }
     findChild(child,d,angularThis){
         for(var i=0;i<child.length;i++){
                 if(d.depth ==child[i].depth){
                  console.log(child[i]+"Yes");
                  if(child[d.data.index].children){
                  child[d.data.index].children=d.children;
                  }
                  break;            
              
            }else{
                console.log(child[i]);
                if(child[i].children && child[i].children.length >0)
                angularThis.findChild(child[i].children,d,angularThis);
            }
            
          }
          return  child;
        }
        findCollpaseChild(child,d,angularThis){
            for(var i=0;i<child.length;i++){
                    if(d.depth ==child[i].depth){
                     console.log(child[i]+"Yes");
                     if(child[d.data.index].children){
                        child[d.data.index]._children=child[d.data.index].children;
                     child[d.data.index].children=[];                     
                     }
                     break;
                     
                 
               }else{
                   console.log(child[i]);
                   if(child[i].children && child[i].children.length >0)
                   angularThis.findCollpaseChild(child[i].children,d,angularThis);
               }
               
             }
             return  child;
           }

    createSvg(height,width) {
        this.d3.select("#network-graph-wrapper svg.tree-graph").remove();
        this.svg = this.d3.select("#network-graph-wrapper").append("svg")
            .attr("width", width + this.margin["right"] + this.margin["left"])
            .attr("height", height + this.margin["top"] + this.margin["bottom"])
            .attr('class', 'tree-graph')
            .append("g")
            .attr("transform", "translate(" + this.margin["left"] + "," + this.margin["top"] + ")")

        this.d3.select(self.frameElement).style("height", "800px");
    }
    getColorCode(d) {
        console.log(d)
        let color
        try {
            color = this.metaconfig.getMetadataDefs(d.data.metaRef.ref.type.toLowerCase())["color"]
        }
        catch (e) {
            if (d.data.nodeType.toLowerCase().indexOf("from_base") != -1) {
                color = this.metaconfig.getMetadataDefs("from_base")["color"]
            }
            else {
                color = '#000000'
            }
        }
        finally { }
        return color;
    }
    getCaption(d) {
        try {
            var caption = this.metaconfig.getMetadataDefs(d.data.metaRef.ref.type.toLowerCase())["caption"];
        }
        catch (e) {
            var caption = d.name
        }

        finally { }
        return caption
    }
    mouseoverNode(d, angularThis) {
        this.showTooltip = true
        var e = this.d3.event;
        angularThis.nodeDetail = d
        angularThis.nodeDetail.caption = angularThis.getCaption(d)
        angularThis.nodeDetail.color = angularThis.getColorCode(d);
        angularThis.nodeDetail.type = d.data.metaRef.ref.type
        angularThis.nodeDetail.uuid = d.data.metaRef.ref.uuid
        angularThis.nodeDetail.name = d.data.name
        $("#colorID").css("background-color", angularThis.nodeDetail.color);
        var xPercent = e.clientX / $(window).width() * 100;
        var left;
        var top;
        if (xPercent > 50) {
            left = (e.clientX - 400) + "px";
            top = e.clientY + "px";
        }
        else {
            left = (e.clientX + 40) + "px";
            top = e.clientY + "px";
        }
        $(".tooltipcustom").css("left", left);
        $(".tooltipcustom").css("top", top);
        if (angularThis.nodeDetail.caption && angularThis.nodeDetail.color)
            $(".tooltipcustom").css("display", "block");
    }
    onChangeDegree(flag){
        if(flag==true){
            this.degree=1
            this.getGraphData(this.uuid, this.version)
        }
        else{
            this.degree=-1  
            this.getGraphData(this.uuid, this.version)
        }
    }
    rightClickNode(d, i,angularThis) {
        angularThis.d3.event.preventDefault();
        var Nodedata = d;
        if(Nodedata.data.metaRef.ref.uuid.indexOf("_") !=-1){
            return false;
        }
        angularThis.d3.selectAll('.context-menu').data([1])
            .enter()
            .append('div')
            .attr('class', 'context-menu');

        // close menu
        angularThis.d3.select('body').on('click.context-menu', function () {
            angularThis.d3.select('.context-menu').style('display', 'none');
        });
        
        // this gets executed when a contextmenu event occurs
        angularThis.d3.selectAll('.context-menu')
            .html('')
            .append('ul')
            .selectAll('li')
            .data(angularThis.menus).enter()
            .append('li')
            .on('click', function (d) {
            
                angularThis.navigateTo(Nodedata, d);
                angularThis.d3.select('.context-menu').style('display', 'none');
            })
            .text(function (d) {
                return d;
            });

            angularThis.d3.select('.context-menu').style('display', 'none');

        // show the context menu
        angularThis.d3.select('.context-menu')
            .style('left', (this.d3.event.pageX - 2) + 'px')
            .style('top', (this.d3.event.pageY - 2) + 'px')
            .style('display', 'block');
            angularThis.d3.event.preventDefault();
    }
    navigateTo(data, d) {debugger
        
            if (d == "Show Details" && data.metaRef.ref.type != null && data.metaRef.ref.type != "attributes") {
                data.metaRef.ref.name = data.name
                data.metaRef.ref.type = data.metaRef.ref.type
                // dagMetaDataService.navigateTo(data.metaRef.ref);
            } else if (d == "Show Details" && data.metaRef.ref.type != null && data.metaRef.ref.type == "attributes") {
                data.metaRef.ref.name = data.name
                data.metaRef.ref.type = data.metaRef.ref.type
                // dagMetaDataService.navigateTo(data.parent.metaRef.ref);
            }
        }
}