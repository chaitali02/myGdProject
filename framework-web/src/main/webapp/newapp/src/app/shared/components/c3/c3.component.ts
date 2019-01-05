import * as c3 from 'c3'
import { Component, Input } from '@angular/core';
import { D3Service, D3, Selection } from 'd3-ng2-service';

@Component({
    selector: 'c3-chart',
    styleUrls: [],
    templateUrl: './c3.templete.html'
  })
export class C3Component{    
    @Input()
    chartId : any;
    @Input()
    chartData:any;
    @Input()
    ChartColumns:any;
    @Input()
    chartColor:any;
    @Input()
    chartType:any;
    @Input()
    chartDatax
    chart:any;    
    chartclass:any=null;
    private d3: D3; // Define the private member which will hold the d3 reference

    constructor(private d3Service: D3Service){ 
      this.d3 = d3Service.getD3();    
    this.chartclass=null
    }
    ngOnInit() {
    //this.ChartRender(this.chartData,this.ChartColumns,this.chartColor);    
    }
    
    ChartRender(data,columns,color,type,datax){
      
      let d3 = this.d3;
      let _min;
      let _max;

      let chartOption={
        bindto:"#"+this.chartId,
        data: {
          json:data,
          keys: {
            x: datax, // it's possible to specify 'x' when category axis
            value:columns,
          },
          type: type,
        },
        axis:{
          x:{
            type: 'category',
              tick: {
                rotate:10,
                multiline: false,
                // count:5,
                culling: {
                  max: 5 // the number of tick texts will be adjusted to less than this value
                }
              },
            height: 50
          }
        },
        color: {
          pattern:color //["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"]  
        }
      }
      if(type == "scatter"){
      if (data.length > 1) {
        _min = d3.min(data, function(d) {
          return d[columns[0]] - 100;
        });
        _max = d3.max(data, function(d) {
          return d[columns[0]] + 2000;
        });
      }
      else {
        _min = 0
        _max = d3.max(data, function(d) {
          return d[columns[0]] + 2000;
        });
      }
      var rs = d3.scaleLinear()
      .domain([_min, _max])
      .range([1, 10]);
      chartOption["point"]={
        focus: {
          expand: {
            enabled: false
          } //End Expend
        }, //Enf Focus
        r: function(d) {
          return rs(d.value)
        } //End R
        } //End Point
      }    
      this.chart = c3.generate(chartOption);
    }

  // Checks for the changes made in the data and re-renders the charts accordingly
    private ngOnChanges( ): void {

        //console.log("ngOnChanges"+this.chartId);
        this.chartclass=this.chartId;
        //console.log("nochage"+this.chartData)
        try {
        setTimeout(()=>{ //<<<--- using ()=> syntax
            this.ChartRender(this.chartData,this.ChartColumns,this.chartColor,this.chartType,this.chartDatax);
            },2000);
        // let intervalId = setInterval(() => {  
        //     console.log('hello');
        //        }, 2000);
    
        } catch(err) {
            console.log(err);
        }
    }

}