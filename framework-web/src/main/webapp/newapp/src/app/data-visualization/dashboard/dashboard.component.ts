import * as c3 from 'c3'
import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { forkJoin } from "rxjs/observable/forkJoin";
import {GridOptions,GridApi} from "ag-grid/main";

import{CommonService}from '../../metadata/services/common.service';
import{DashboardService} from '../../metadata/services/dashboard.service'


@Component({
    selector: 'app-dashboard',
    styleUrls: [],
    templateUrl: './dashboard.template.html'
  })
export class DashboardComponent{  
    _vizpodbody: any;
    _dashbaordUuid: any;
    _dashbaordVersion: any;
    _dashbaordMode: any;
    gridOptions: GridOptions;
    databoardData: any;
    dashboardName: any;
    sectionRows:any[];
    columns:any[];
    filterAttribureIdValues:any[];
    selectedAttributeValue:any[];
    breadcrumbDataFrom: { "caption": string; "routeurl": any; }[];
    color: string[];
    
    constructor(private _location: Location,private _activatedRoute: ActivatedRoute, public _router: Router,private _commonService:CommonService,private _dashboardService:DashboardService){
        this.sectionRows=[]
        this.filterAttribureIdValues=[];
        this.selectedAttributeValue=[];
        this._vizpodbody={};
        this.color=["#d98880", "#f1948a", "#c39bd3", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"];     
        this.breadcrumbDataFrom=[{
            "caption":" Data Visualization",
            "routeurl":"/app/list/dashboard"
          },
          {
            "caption":"Dashboard",
            "routeurl":"/app/list/dashboard"
          },{
              "caption":"",
              "routeurl":null
          }
        ];
    }

    ngOnInit() {
        this._activatedRoute.params.subscribe((params: Params) => {
            this._dashbaordUuid = params['id'];
            this._dashbaordVersion = params['version'];
            this._dashbaordMode = params['mode'];
        });
        this.callGraph();
    }
    
    fullscreen=function(){
        window.dispatchEvent(new Event('resize'));
    }
    
    fullScreenVizpod(parentIndex,childIndex){ 
        if(this.sectionRows[parentIndex].columns[childIndex].vizpodDetails.iconclass == 'fa fa-expand'){    
            for(var i=0;i<this.sectionRows.length;i++){
                for(var j=0;j<this.sectionRows[i].columns.length;j++){
                    this.sectionRows[i].columns[j].vizpodDetails.show=false;
                }
            }
            this.sectionRows[parentIndex].columns[childIndex].vizpodDetails.show=true;
            this.sectionRows[parentIndex].columns[childIndex].vizpodDetails.iconclass='fa fa-compress';   
            
        }
        else{
            for(var i=0;i<this.sectionRows.length;i++){
                for(var j=0;j<this.sectionRows[i].columns.length;j++){
                    this.sectionRows[i].columns[j].vizpodDetails.show=true;
                }
            }
            this.sectionRows[parentIndex].columns[childIndex].vizpodDetails.iconclass='fa fa-expand';
          
          } 
        window.dispatchEvent(new Event('resize'));
    }

    getGridStyle = function(data) {
       let style = {}
       if (data && data.length >1) {
            style['height'] = ((data.length < 10 ? data.length * 40 : 400) + 60) + 'px';
        }
        else if(data.length ==1){
            style['height']="100px";
        }
        else{
            style['height']="100px";
        }
        return style;
    }
    onGridReady(params) {
        params.api.sizeColumnsToFit();
    }

    PrepareDataGrid=function (i,j) {
        this.sectionRows[i].columns[j].vizpodDetails.gridOptions = <GridOptions>{
            enableFilter: true,
            enableSorting: true,
            enableColResize: true,
            suppressMenuHide: false,
            headerHeight: 40,
            rowSelection:'single',
            context: {
                componentParent: this
            }
        };
        this.sectionRows[i].columns[j].vizpodDetails.gridOptions.rowHeight = 40;
    }

    callGraph(){
        let vizpodtypedetail=[];
        let count=0;
        this._commonService.getLatestByUuid(this._dashbaordUuid,'dashboardview')
        .subscribe(
        response =>{this.onSuccessLatestByUuid(response)},
        error => console.log("Error :: " + error));  
    
    }
    onSuccessLatestByUuid (response){
        
        this.databoardData=response;
        this._dashbaordUuid =response.uuid
        this._dashbaordVersion=response.version
        this.dashboardName=response["name"];
        this.breadcrumbDataFrom[2].caption= this.dashboardName;
        this.convertSectionInfo(response["sectionInfo"])
        this.preparColumnData();
        this.getFilterValue(this.databoardData)// Method call for populate filter dropdown data;
        this.getVizpodResut(null);
        
      }//onSuccessLatestByUuid

    convertSectionInfo(sectionInfo){
        let _columns={};
        let _columnsInfo=[];
        _columnsInfo[0]=sectionInfo[0];
        _columns["columns"]=_columnsInfo;
        this.sectionRows[0]=_columns;
        for(let i=0; i<sectionInfo.length;i++){
            let item =sectionInfo[i];
            if(this.sectionRows[item["rowNo"]-1]){
                this.sectionRows[item["rowNo"]-1].columns[item["colNo"]-1]=item
            }
            else {
                this.sectionRows[item["rowNo"]-1] = {columns:[item]}
            }
        }      
    }
    
    preparColumnData(){
        
        var colorcount=0;
        for(var i=0;i<this.sectionRows.length;i++){
            for(var j=0;j<this.sectionRows[i].columns.length;j++){
                let _datax;
                let _dataColumns=[];
                let _vizpoddetailjson={};
                _vizpoddetailjson["id"]="chart"+this.sectionRows[i].columns[j].vizpodInfo.id+i+j
                _vizpoddetailjson["uuid"]=this.sectionRows[i].columns[j].vizpodInfo.uuid
                _vizpoddetailjson["version"]=this.sectionRows[i].columns[j].vizpodInfo.version
                _vizpoddetailjson["name"]=this.sectionRows[i].columns[j].vizpodInfo.name;
                _vizpoddetailjson["type"]=this.sectionRows[i].columns[j].vizpodInfo.type.split("-")[0];
                _vizpoddetailjson["class"]="";
                _vizpoddetailjson["iconclass"]="fa fa-expand";
                _vizpoddetailjson["showtooltiptitle"]="Maximize";
                _vizpoddetailjson["show"]=true;
                _vizpoddetailjson["isChart"]=false;
                this.sectionRows[i].columns[j].vizpodDetails=_vizpoddetailjson;
                _datax=this.sectionRows[i].columns[j].vizpodInfo.keys[0].attributeName+""//x value
                this.sectionRows[i].columns[j].vizpodDetails.dataX=_datax;
                for(var k=0;k<this.sectionRows[i].columns[j].vizpodInfo.values.length;k++){
                    if(this.sectionRows[i].columns[j].vizpodInfo.values[k].ref.type =="datapod"){
                        _dataColumns[k]=this.sectionRows[i].columns[j].vizpodInfo.values[k].attributeName;
                    }//End If Inside For
                    else{
                        _dataColumns[k]=this.sectionRows[i].columns[j].vizpodInfo.values[k].ref.name+"";
                    }           
                    this.sectionRows[i].columns[j].vizpodDetails.dataColumns=_dataColumns
                }//End For K

                if(this.sectionRows[i].columns[j].vizpodInfo.type == "data-grid"){
                    var keyvalueData=null;
                    this.sectionRows[i].columns[j].vizpodDetails.dataColumns=[]
                    this.sectionRows[i].columns[j].gridOptions={}
                    this.PrepareDataGrid(i,j);
                    //this.sectionRows[i].columns[j].gridOptions.columnDefs=[];
                    if(this.sectionRows[i].columns[j].vizpodInfo.groups.length >0){
                      keyvalueData = this.sectionRows[i].columns[j].vizpodInfo.keys.concat(this.sectionRows[i].columns[j].vizpodInfo.values,this.sectionRows[i].columns[j].vizpodInfo.groups);
                    }//End Inner IF
                    else{
                      keyvalueData =this.sectionRows[i].columns[j].vizpodInfo.keys.concat(this.sectionRows[i].columns[j].vizpodInfo.values);
                    }//End Innder Else
                   
                  //  console.log(JSON.stringify(keyvalueData))
                    for(var c=0;c<keyvalueData.length;c++){
                      var attribute={};
                      if(keyvalueData[c].ref.type =="datapod"){
                        attribute["headerName"]=keyvalueData[c].attributeName;
                        attribute["field"]=keyvalueData[c].attributeName;
                      //attribute.width =$scope.keyvalueData[c].attributeName.split('').length + 2 + "%"
                      }
                      else{
                        attribute["headerName"]=keyvalueData[c].ref.name;
                        attribute["field"]=keyvalueData[c].ref.name;
                        //attribute.width=$scope.keyvalueData[c].ref.name;
                      }
          
                      this.sectionRows[i].columns[j].vizpodDetails.dataColumns.push(attribute)
                    }//End C loop
                  }//End Else
            }
        }
        
    }//End preparColumnData

    getVizpodResut(data){
        let observables = new Array();
        let _vizpodtrack=[];
        for(var i=0;i<this.databoardData.sectionInfo.length;i++){ 
            let vizpodtrackJson={};
            observables.push(this._dashboardService.getVizpodResult(this.databoardData.sectionInfo[i].vizpodInfo.uuid,this.databoardData.sectionInfo[i].vizpodInfo.version,"view",data));
            vizpodtrackJson["rowNo"]=this.databoardData.sectionInfo[i].rowNo-1;
            vizpodtrackJson["colNo"]=this.databoardData.sectionInfo[i].colNo-1;
            _vizpodtrack[i]=vizpodtrackJson
           
        }
        forkJoin(observables).subscribe(
            res =>{
                for(var k=0;k<_vizpodtrack.length;k++){
                    this.sectionRows[_vizpodtrack[k].rowNo].columns[_vizpodtrack[k].colNo].isDataError=false;
                    this.sectionRows[_vizpodtrack[k].rowNo].columns[_vizpodtrack[k].colNo].errormsg="";
                    this.sectionRows[_vizpodtrack[k].rowNo].columns[_vizpodtrack[k].colNo].vizpodDetails.isChart=true;
                    this.sectionRows[_vizpodtrack[k].rowNo].columns[_vizpodtrack[k].colNo].vizpodDetails.datapoints=res[k];
                }//End For
                this.preparPieAndDonutChartData();     
               // console.log(JSON.stringify(this.sectionRows))
            },
            error => console.log('Error: ', error)
        );
    }//End getVizpodResut

    preparPieAndDonutChartData=function(){
        for(var i=0;i<this.sectionRows.length;i++){
          for(var j=0;j<this.sectionRows[i].columns.length;j++){
            if(this.sectionRows[i].columns[j].vizpodInfo.type =='pie-chart' || this.sectionRows[i].columns[j].vizpodInfo.type =='donut-chart'){
              let _columnname=this.sectionRows[i].columns[j].vizpodInfo.keys[0].attributeName
              let _columnnamevalue;
              if(this.sectionRows[i].columns[j].vizpodInfo.values[0].ref.type == "datapod"){
                _columnnamevalue=this.sectionRows[i].columns[j].vizpodInfo.values[0].attributeName
              }
              else{
                _columnnamevalue=this.sectionRows[i].columns[j].vizpodInfo.values[0].ref.name
              }
              let _columnarray=[]
              let _dataarray=[]
              for(var k=0;k<this.sectionRows[i].columns[j].vizpodDetails.datapoints.length;k++){
                var datajson={};
                _columnarray[k]=this.sectionRows[i].columns[j].vizpodDetails.datapoints[k][_columnname]+"";    
                datajson[this.sectionRows[i].columns[j].vizpodDetails.datapoints[k][_columnname]]=this.sectionRows[i].columns[j].vizpodDetails.datapoints[k][_columnnamevalue];
                _dataarray[k]=datajson;
              }
              this.sectionRows[i].columns[j].vizpodDetails.dataX="";
              this.sectionRows[i].columns[j].vizpodDetails.datapoints=_dataarray;
              this.sectionRows[i].columns[j].vizpodDetails.dataColumns=_columnarray;
            }
          }
        }
    }//End preparPieAndDonutChartData

    getFilterValue=function(data){
        console.log("dfd"+data);
        let filterAttribureIdValues=[]
        let selectedAttributeValue=[]
        if(data.filterInfo && data.filterInfo.length >0){
          var filterAttribureIdValue=[];
          for(var n=0;n<data.filterInfo.length;n++){
            var filterattributeidvalepromise=this._dashboardService.getAttributeValues(data.filterInfo[n].ref.uuid,data.filterInfo[n].attrId);
            filterAttribureIdValue.push(filterattributeidvalepromise);
          }//End For Loop

          forkJoin(filterAttribureIdValue).subscribe(
            result =>{
                console.log(JSON.stringify(result))
                for(var i=0;i<result.length;i++){
                    var filterAttribureIdvalueJSON={};
                    var defaultvalue={}
                    defaultvalue["id"]=null;
                    defaultvalue["value"]="-select-";
                    filterAttribureIdvalueJSON["index"]=i;  
                    filterAttribureIdvalueJSON["vizpoduuid"]=
                    filterAttribureIdvalueJSON["vizpodversion"]=data.filterInfo[i].ref.uuid;
                    filterAttribureIdvalueJSON["datapoduuid"]=data.filterInfo[i].ref.uuid;
                    filterAttribureIdvalueJSON["datapodattrId"]=data.filterInfo[i].attrId;
                    filterAttribureIdvalueJSON["dname"]=data.filterInfo[i].ref.name+"."+data.filterInfo[i].attrName;
                    filterAttribureIdvalueJSON["values"]=result[i];
                    filterAttribureIdvalueJSON["values"].splice(0,0,defaultvalue)
                    this.selectedAttributeValue[i]=defaultvalue
                    this.filterAttribureIdValues[i]=filterAttribureIdvalueJSON
                    console.log(JSON.stringify(this.filterAttribureIdValues))
                    }      
            },
            error => console.log('Error: ', error)
        );
        
        }//End If
    
      }//End getFilterValue

    onFilterChange(index){
        console.log(JSON.stringify(this.selectedAttributeValue));
        var count=0;
        let _filterListarray=[];
        for(var i=0;i<this.selectedAttributeValue.length;i++){
            var filterList={};
            var ref={};
            if(this.selectedAttributeValue[i].value !="-select-"){
                ref["type"]="datapod";
                ref["uuid"]=this.filterAttribureIdValues[i].datapoduuid
                filterList["ref"]=ref;
                filterList["attrId"]=this.filterAttribureIdValues[i].datapodattrId
                filterList["value"]="'"+this.selectedAttributeValue[i].value+"'";
                _filterListarray[count]=filterList;
                count=count+1;
            }
        }
        console.log(JSON.stringify(_filterListarray));
        this._vizpodbody.filterInfo=_filterListarray
        this.chartSetInprogess();
        this.getVizpodResut(this._vizpodbody);
    }
    chartSetInprogess(){
        for(var i=0;i<this.sectionRows.length;i++){
            for(var j=0;j<this.sectionRows[i].columns.length;j++){
                this.sectionRows[i].columns[j].vizpodDetails.isChart=false;
            }
        }
    }
    refreshDashboard=function(length){
        this.callGraph();
        this.selectedAttributeValue=[]
        for(var i=0;i<length;i++){
          var defaultvalue={}
          defaultvalue["id"]=null;
          defaultvalue["value"]="-select-"
          this.selectedAttributeValue[i]=defaultvalue
          this.filterAttribureIdValues[i].values.splice(0,1);
          this.filterAttribureIdValues[i].values.splice(0,0,defaultvalue);
        }
    }
    public goBack() {
        this._location.back();
      }


    enableEdit(uuid, version) {
        this._router.navigate(['app/dataVisualization/dashboarddetail',this. _dashbaordUuid, this._dashbaordVersion, 'false']);
      }


   

 }