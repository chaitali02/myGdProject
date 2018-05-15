import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';  
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { PredictionService } from '../../metadata/services/prediction.service';

import { Version } from '../../metadata/domain/version';
import{ DependsOn } from '../dependsOn';
import {AttributeHolder} from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';

@Component({
  selector: 'app-prediction',
  templateUrl: './prediction.template.html',
})
export class PredictionComponent implements OnInit {
  name:any;
  version: any;
  breadcrumbDataFrom : any;
  prediction: any;
  createdBy :any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  continueCount : any;
  progressbarWidth:any;
  isSubmit:any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  

  constructor( config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _location:Location,private _modelService:PredictionService) {
    this.prediction ={};
    this.prediction["active"] =true;
    this.continueCount=1;
    this.progressbarWidth=25*this.continueCount+"%";
   
    this.breadcrumbDataFrom=[{
      "caption":"Data Science",
      "routeurl":"/app/list/predict"
    },
    {
      "caption":"Prediction",
      "routeurl":"/app/list/predict"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ];

   
   }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if(this.mode !== undefined) {
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
        
             }
             
         })
        }
    
 getOneByUuidAndVersion()
    {
      this._commonService.getOneByUuidAndVersion(this.id,this.version,'predict')
      .subscribe(
       response =>{
         this.onSuccessgetOneByUuidAndVersion(response)},
       error => console.log("Error :: " + error)); 
      
    }
 
 public get value() : string {
     return 
 }
 getAllVersionByUuid(){
  {
    this._commonService.getAllVersionByUuid('predict',this.id)
    .subscribe(
      response=>{
        this.onSuccessgetAllVersionByUuid(response)},
        error=>console.log("Error ::" +error)
    )
  }
 
 }

countContinue()
 {
this.continueCount=this.continueCount-1 ;
this.progressbarWidth=25*this.continueCount+"%"; 

}

countBack=function(){
  this.continueCount=this.continueCount-1;
  this.progressbarWidth=25*this.continueCount+"%";
}


onVersionChange(){ 
  this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'paramset')
  .subscribe(
  response =>{//console.log(response)},
    this.onSuccessgetOneByUuidAndVersion(response)},
  error => console.log("Error :: " + error)); 
}

 
 onSuccessgetOneByUuidAndVersion(response){
  this.prediction =response;
  this.uuid =response.uuid;
  const  version :Version =new Version;
  version.label =response["version"];
  version.uuid =response["uuid"];
  this.selectedVersion=version;
  this.createdBy =response.createdBy.ref.name;
  this.prediction.active=response["active"] == 'Y' ? true:false;
  this.prediction.published =response["published"] == 'Y' ? true :false;
  this.breadcrumbDataFrom[2].caption=response.name;
  console.log('Data is' + response)
}


onSuccessgetAllVersionByUuid(response){
  var temp=[]
  for (const i in response) {
    let ver={};
    ver["label"]=response[i]['version'];
    ver["value"]={};
    ver["value"]["label"]=response[i]['version'];      
    ver["value"]["uuid"]=response[i]['uuid']; 
    temp[i]=ver;
  }
  this.VersionList=temp
}


}
    
    
    
    
