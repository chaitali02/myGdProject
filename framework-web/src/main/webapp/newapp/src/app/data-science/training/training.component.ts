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
  selector: 'app-training',
  templateUrl: './training.template.html',
})
export class TrainingComponent implements OnInit {
  selectallattribute: any;
  ishowExecutionparam: boolean;
  model :any;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  createdBy: any;
  name:any;
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
  continueCount : any;
  progressbarWidth:any;
  isSubmit:any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  

  constructor( config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _location:Location,private _modelService:TrainingService) {
    this.showTrain = true;
    this.train = {};
    this.train["active"]=true;
    this.continueCount=1;
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

  this.train=response;
  this.uuid=response.uuid;
  const version: Version = new Version();
  version.label = response['version'];
  version.uuid = response['uuid'];
  this.selectedVersion=version ;
  this.createdBy =response.createdBy.ref.name
  this.train.published=response["published"] == 'Y' ? true : false
  this.train.active=response["active"] == 'Y' ? true : false
  this.breadcrumbDataFrom[2].caption=this.train.name
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
