import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { ModelService } from '../../metadata/services/model.service';

@Component({
  selector: 'app-results',
  templateUrl: './resultDetails.component.html',
  styleUrls: ['./resultDetails.component.css'],
  
})
export class ResultDetailsComponent {
  tableHeading: string;
  cols: any[];
  columnOptions: any[];
  colsdata: any;
  IsError: boolean;
  IsTableShow: boolean;
  showDiv: boolean;
  ppml:boolean
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  type: any;
  uuid: any;
  version: any;
  mode: any;
  istableShow: boolean;
  params:any;
  modelResult : any;
  id: any;
  uid: any;
  constructor(private _location:Location,private _activatedRoute: ActivatedRoute,private router: Router,public appMetadata: AppMetadata,private _commonService:CommonService, private _modelService:ModelService) {
    
    this.breadcrumbDataFrom=[{
      "caption":"Data Science ",
      "routeurl":"/app/list/trainexec"
    },
    // {
    //   "caption":"Results",
    //   "routeurl":"/app/list/modelexec"
    // },
    {
      "caption":"Model Results",
      "routeurl":null 
    }
    ]
    
    this._activatedRoute.params.subscribe((params: Params) => {
    
      this.version = params['version'];
      this.mode = params['mode'];
      this.type = params['type'];
      this.id = params['id'];
      
    });
    if(this.type=="training"){
      this.getModelResults();
      this.showDiv=true;
      this.ppml=false;
    }
    else if(this.type=="prediction"){
      this.getPredictResults();
      this.showDiv=false
      this.tableHeading="Prediction"
    }
    else{
      this.getSimulateResults();
      this.showDiv=false
      this.tableHeading="Simulation"
    }
   }
   showPMMLResult(){
    this.ppml=true;
   }
   getModelResults(){
    this._modelService.getModelResults(this.id,this.version)
    .subscribe(
    response =>{
      this.onSuccessgetModelResults(response)},
    error => console.log("Error :: " + error)); 
   }
   getPredictResults(){
    this._modelService.getPredictResults(this.id,this.version)
    .subscribe(
    response =>{
      this.onSuccessgetPredictResults(response)},
      error => {
        this.IsTableShow=true; 
        console.log("Error :: " + error)
        this.IsError=true;    
    
    }); 
   
   }
   getSimulateResults(){
    this._modelService.getSimulateResults(this.id,this.version)
    .subscribe(
    response =>{
      this.onSuccessgetPredictResults(response)},
      error => {
        this.IsTableShow=true; 
        console.log("Error :: " + error)
        this.IsError=true;    
    
    }); 
   
   }
   onSuccessgetModelResults(response){
    this.modelResult = response;
    
   }
   onSuccessgetPredictResults(response){
    this.IsTableShow=true;
    this.colsdata=response
    let columns=[];
    console.log(response)
    if(response.length && response.length > 0){
      Object.keys(response[0]).forEach(val=>{
          if (val != "rownum"){
          let width=((val.split("").length * 9)+20)+"px"
          columns.push({"field":val, "header":val,colwidth:width});
          }
      });
    }
    
    this.cols=columns
    this.columnOptions = [];
    for(let i = 0; i < this.cols.length; i++) {
        this.columnOptions.push({label: this.cols[i].header, value: this.cols[i]});
    }
    
   }
   public goBack() {
    this._location.back();
}
}
