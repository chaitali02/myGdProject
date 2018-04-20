import { Component, OnInit, ElementRef } from '@angular/core';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import {miserables} from './miserables';
declare var $:any;
@Component({
  selector: 'app-d3',
  styleUrls: ['./d3.style.css'],
  templateUrl: './d3.template.html'
})
export class D3Component {
  private svg: any;
  private width: number;
  private height: number;
  private color: any;
  private d3: D3; // Define the private member which will hold the d3 reference
  private parentNativeElement: any;
  private simulation: any;
  private graph: any;
  private link: any;
  private node: any;
  private linkText: any;
  private texts: any;
  private div: any;
  private g: any;
  private zoom_handler: any;

  constructor(element: ElementRef, d3Service: D3Service) {
    this.d3 = d3Service.getD3();
    this.parentNativeElement = element.nativeElement;
  }

  ngOnInit() {
    let d3 = this.d3;
    this.svg = d3.select('svg'),
      this.width = +this.svg.attr('width'),
      this.height = +this.svg.attr('height');

    this.color = d3.scaleOrdinal(d3.schemeCategory20);

    this.simulation = d3.forceSimulation()
      .force('link', d3.forceLink().id(function(d) { return d['id']; }).distance(150).strength(1))
      .force('charge', d3.forceManyBody())
      .force('center', d3.forceCenter(this.width / 2, this.height / 3));

      this.render(miserables);
  }

  ticked() {
    this.link
      .attr('x1', function(d) { return d.source.x; })
      .attr('y1', function(d) { return d.source.y; })
      .attr('x2', function(d) { return d.target.x; })
      .attr('y2', function(d) { return d.target.y; });

    this.node
      .attr('cx', function(d) { return d.x; })
      .attr('cy', function(d) { return d.y; });

    this.texts.attr('transform', function(d) {
      return 'translate(' + d.x + ',' + d.y + ')';
    });
  }

  render(graph) {
    this.g = this.svg.append('g')
    this.link = this.g.append('g')
      .attr('class', 'links')
      .selectAll('line')
      .data(graph.links)
      .enter()
      .append('line')
      .attr('stroke-width', function(d) { return Math.sqrt(d.value); });

    this.node = this.g.append('g')
      .attr('class', 'nodes')
      .selectAll('circle')
      .data(graph.nodes)
      .enter().append('circle')
      .attr('r', 17)
      .attr('fill', (d) => { return this.color(d.nodeType); })
      .on('mouseover', (d) => { return this.mouseover(d)})
      .on('mouseout', (d) => { return this.mouseout(d)})
      .call(this.d3.drag()
        .on('start', (d) => { return this.dragstarted(d)})
        .on('drag', (d) => { return this.dragged(d)})
        .on('end', (d) => { return this.dragended(d)}));

    this.div = this.d3.select('div.portlet-body').append('div')
      .attr('class', 'tooltipFocus')
      .style('opacity', 0);


    this.node.append('title')
      .text(function(d) { return d.name; });


    this.texts = this.g.append('g')
      .attr('class', 'labels')
      .selectAll('text')
      .data(graph.nodes)
      .enter().append('text')
      .attr('dx', 18)
      .attr('dy', '.35em')
      .style('font-size', '12px')
      .text(function(d) { return d.name });

    this.simulation
      .nodes(graph.nodes)
      .on('tick', ()=>{return this.ticked()});

    this.simulation.force('link')
      .links(graph.links);
  }

  dragstarted(d) {
    if (!this.d3.event.active) this.simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
  }

   dragged(d) {
    d.fx = this.d3.event.x;
    d.fy = this.d3.event.y;
  }

   dragended(d) {
    if (!this.d3.event.active) this.simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
  }

    mouseout(d) {
      this.div.transition()
        .duration(500)
        .style('opacity', 0);
    }

    mouseover(d) {
      d.px = d.x;
      d.py = d.y;

      if ($('.portlet-body').width() <= 726) {
      }
      else {
        this.div.transition().duration(200).style('opacity', .9);
        this.div.html('').style('left', (d.px + 50) + 'px').style('top', (d.py + 90) + 'px');
      }
      this.d3.select('div.portlet-body').select('.tooltipFocus').append('div').attr('class', 'tooltipcustom');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').append('span').attr('class', 'one').style('float', 'left').style('background-color', 'red');;
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').append('div').attr('class', 'header1').html('&nbsp&nbsp' + d.name).style('left', (this.d3.event.pageX) + 'px').style('top', (this.d3.event.pageY - 28) + 'px');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').append('div').attr('class', 'header4').html('').style('left', (this.d3.event.pageX) + 'px').style('top', (this.d3.event.pageY - 28) + 'px');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').select('.header4').append('label').attr('class', 'header2label').html('Id');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').select('.header4').append('span').attr('class', 'header2span').html(d.id);
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').append('div').attr('class', 'header2').html('').style('left', (this.d3.event.pageX) + 'px').style('top', (this.d3.event.pageY - 28) + 'px');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').select('.header2').append('label').attr('class', 'header2label').html('Name');
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').select('.header2').append('span').attr('class', 'header2span').html(d.name);
      this.d3.select('div.portlet-body').select('.tooltipFocus').select('.tooltipcustom').append('div').attr('class', 'header3').html('').style('left', (this.d3.event.pageX) + 'px').style('top', (this.d3.event.pageY - 28) + 'px');
  }

}

