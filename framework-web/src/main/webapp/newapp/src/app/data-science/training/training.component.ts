import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';  
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { TrainingService } from '../../metadata/services/training.service';

import { Version } from '../../metadata/domain/version';
import{ DependsOn } from '../dependsOn';
import {AttributeHolder} from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';

@Component({
  selector: 'app-model',
  templateUrl: './training.template.html',
})
export class TrainingComponent implements OnInit {
  IsLableSelected: boolean;
  selectallattribute: any;
  isTabelShow: boolean;
  ishowExecutionparam: boolean;
  model :any;
  checkboxtrainexecution: boolean;
  selectedlabel: any;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  allAttribute: any[];
  selectmodel: any;
  sources: string[];
  createdBy: any;
  allNames: any[];
  name:any;
  sourcedata: any;
  version: any;
  breadcrumbDataFrom : any;
  showTrain : any;
  train: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  depends: any;
  continueCount : any;
  progressbarWidth:any;
  isSubmit:any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any[];
  source : string;
  allModel: any[];
  allSourceAttribute : any[];
  featuresArray : any[];
  featuresTags : any[];
  featureResponse : any;
  nameResponse : any;
  labelTags : any;
  labelResponse : any;
  allSourceLabel : any;
  labelArray : any;

  constructor( config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _location:Location,private _modelService:TrainingService) {
    this.showTrain = true;
    this.train = {};
    this.train["active"]=true;
    this.continueCount=1;
    this.isSubmit="false";
    this.IsLableSelected=false
    this.progressbarWidth=25*this.continueCount+"%";
    this.dropdownSettings = { 
      singleSelection: false, 
      text:"Select Attrubutes",
      selectAllText:'Select All',
      unSelectAllText:'UnSelect All',
      enableSearchFilter: true,
      classes:"myclass custom-class",
      maxHeight:110,
      disabled:false
    };   
    this.breadcrumbDataFrom=[{
      "caption":"Data Science",
      "routeurl":"/app/list/train"
    },
    {
      "caption":"Training",
      "routeurl":"/app/list/train"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ];

    this.sources = ["datapod","dataset"];
    this.source=this.sources[0];
   }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if(this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
        this.getAllLatestModel(false);
             }
             else{
              //this.getAllLatest(true);
              this.getAllLatestModel(true);
            }
         })
       }
 getOneByUuidAndVersion()
 {
   this._commonService.getOneByUuidAndVersion(this.id,this.version,'train')
   .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
   
 }
 getAllVersionByUuid()
 {
   this._commonService.getAllVersionByUuid('train',this.id)
   .subscribe(
     response=>{
       this.onSuccessgetAllVersionByUuid(response)},
       error=>console.log("Error ::" +error)
   )
 }

 getAllLatestModel(IsDefault){

    this._commonService.getAllLatest('algorithm')
    .subscribe(
    response =>{
      this.OnSuccessgetAllLatestModel(response,IsDefault)},
    error => console.log("Error :: " + error))
  }

 OnSuccessgetAllLatestModel(response,IsDefault){
  let temp=[]
   
    if(this.mode == undefined || IsDefault == true) {
      let modelTemp: DependsOn = new DependsOn();
      modelTemp.label = response[0]["name"];
      modelTemp.uuid = response[0]["uuid"];
      modelTemp.version = response[0]["version"];
      this.selectmodel=modelTemp;
      this.getLatestModel(response[0]["uuid"],true);
  }
    for (const n in response) {
        let allname={};
        allname["label"]=response[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response[n]['name'];      
        allname["value"]["uuid"]=response[n]['uuid'];
        allname["value"]["version"]=response[n]['version'];
        temp[n]=allname;
    }
    this.allModel = temp
  }

  getLatestModel(uuid,IsDefault){
    this._commonService.getLatestByUuid(uuid,'model')
   .subscribe(
    response =>{
      this.OnSuccesGetLatestModel(response,IsDefault)},
  error => console.log("Error :: " + error));
  }
 
  OnSuccesGetLatestModel(response,IsDefault){

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
changeAlgorithm(){
  this.getLatestModel(this.selectmodel["uuid"],true);
}

 onSuccessgetOneByUuidAndVersion(response){

  this.train=response;
  this.uuid=response.uuid;
  const version: Version = new Version();
  version.label = response['version'];
  version.uuid = response['uuid'];
  this.selectedVersion=version ;
  this.createdBy =response.createdBy.ref.name
  this.train.published=response["published"] == 'Y' ? true : false
  this.train.active=response["active"] == 'Y' ? true : false
  // this.model=response.model.ref.name
  let modeltemp: DependsOn = new DependsOn






  // let algorithmTemp: DependsOn = new DependsOn();
  // algorithmTemp.label = response["algorithm"]["ref"]["name"];
  // algorithmTemp.uuid = response["algorithm"]["ref"]["uuid"];
  // algorithmTemp.version = response["algorithm"]["ref"]["version"];
  // this.selectAlgorithm=algorithmTemp;
  // this.breadcrumbDataFrom[2].caption=this.train.name;
  // console.log('Data is' + response);


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
