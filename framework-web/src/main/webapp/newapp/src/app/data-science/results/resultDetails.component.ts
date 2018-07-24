import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { ModelService } from '../../metadata/services/model.service';
import { CommonListService } from './../../common-list/common-list.service';
import { saveAs } from 'file-saver';
import 'rxjs/add/operator/toPromise';
import { Http, Headers} from '@angular/http';
import { AppConfig } from '../../app.config';
import { ResponseContentType } from '@angular/http';
@Component({
  selector: 'app-results',
  templateUrl: './resultDetails.component.html',
  styleUrls: ['./resultDetails.component.css'],
  
})
export class ResultDetailsComponent {
  blob: Blob;
  filename: string;
  pmmlData: any;
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
  baseUrl: any;
  constructor(private config : AppConfig,private http : Http, private _location:Location,private _activatedRoute: ActivatedRoute,private router: Router,public appMetadata: AppMetadata,private _commonService:CommonService, private _modelService:ModelService, private _commonListService : CommonListService) {
    this.baseUrl = config.getBaseUrl();
    this.breadcrumbDataFrom=[{
      "caption":"Data Science ",
      "routeurl":"/app/dataScience/results"
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

   savePng(){
    const headers = new Headers();
    headers.append('Accept', 'text/plain');
    this.http.get(this.baseUrl+'/model/predict/download?action=view&predictExecUUID='+this.id+'&predictExecVersion='+this.version+'&mode=BATCH',
         { headers: headers , responseType: ResponseContentType.Blob})
      .toPromise()
      .then(response => this.saveToFileSystem(response));
    }
    
    private saveToFileSystem(response) {debugger
    //const gate= response.headers.get('Content-Disposition');
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    //const filename = "abx.xls";
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
  }
  

   showPMMLResult(){
    this.ppml=true;
    var headers = new Headers();
    headers.append('Accept', 'text/plain');
    this.http.get(this.baseUrl+"model/download?modelExecUUID="+this.id+"&modelExecVersion="+this.version,
    { headers: headers })
  .subscribe(response => this.getPmmlResults(response))
   }
   getPmmlResults(response){
     console.log(response["_body"])
     this.pmmlData=response["_body"];
     
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
