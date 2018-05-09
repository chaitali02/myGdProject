import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';  
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { DistributionService } from '../../metadata/services/distribution.service';

import { Version } from '../../metadata/domain/version';
import{ DependsOn } from '../dependsOn';
import {AttributeHolder} from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';

@Component({
  selector: 'app-distribution',
  templateUrl: './distribution.template.html',
})
export class DistributionComponent implements OnInit {
  selectallattribute: any;
  ishowExecutionparam: boolean;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  createdBy: any;
  name:any;
  version: any;
  breadcrumbDataFrom : any;
  showdistribution : any;
  distribution: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  continueCount : any;
  progressbarWidth:any;
  isSubmitEnable : any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  library : any;
  librarytypesOption : {'value' : String , 'label' : String}[];
  msgs:any;
  arrayParamList : any;
  paramList :any;

  

  constructor( config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _location:Location,private _modelService:DistributionService) {
    this.distribution = true;
    this.distribution = {};
    this.distribution["active"]=true;
    this.isSubmitEnable =true
    
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
      "routeurl":"/app/list/distribution"
    },
    {
      "caption":"Distribution",
      "routeurl":"/app/list/distribution"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ];
    this.librarytypesOption=[
      {"value" : "sparkML", "label" : "sparkML"},
      {"value" : "R", "label" : "R"},
      {"value" : "java", "label" : "java"}
    ]

   
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
             this.getAllLatest(); 
             
         })
       }
getOneByUuidAndVersion(){
        this._commonService.getOneByUuidAndVersion(this.id,this.version,'distribution')
        .subscribe(
        response =>{
          this.onSuccessgetOneByUuidAndVersion(response)},
        error => console.log("Error :: " + error)); 
      }

      
 getAllVersionByUuid(){
        this._commonService.getAllVersionByUuid('distribution',this.id)
        .subscribe(
        response =>{
          this.OnSuccesgetAllVersionByUuid(response)},
        error => console.log("Error :: " + error));
      }

 getAllLatest(){
        this._commonService.getAllLatest('paramList')
        .subscribe(
        response =>{
          this.onSuccessgetAllLatest(response)},
        error => console.log("Error :: " + error));  
      }
       
    
   
 onSuccessgetOneByUuidAndVersion(response){
   this.distribution=response
   this.uuid=response.uuid;
   const version: Version = new Version();
   version.label = response['version'];
   version.uuid = response['uuid'];
   this.selectedVersion=version 
   this.createdBy=response.createdBy.ref.name;
   this.distribution.published=response["published"] == 'Y' ? true : false
   this.distribution.active=response["active"] == 'Y' ? true : false 
   this.distribution.paramList =response.paramList.ref.name
   this.breadcrumbDataFrom[2].caption=this.distribution.name
   console.log('Data is' + response);
    
 }
      
 OnSuccesgetAllVersionByUuid(response){

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

  onSuccessgetAllLatest(response){
    this.arrayParamList = [];
    for(const i in response){
      let refParam={}
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}      
      refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['label'];
      this.arrayParamList[i] = refParam;
      
    }
  }


  public goBack() {
    // this._location.back();
   this.router.navigate(['app/list/distribution']);
    }

submitDistribution()
{
  this.isSubmitEnable=true;
  let distributionJson ={};
  distributionJson["uuid"] =this.distribution.uuid;
  distributionJson["name"] =this.distribution.name
  distributionJson["desc"] =this.distribution.desc
  distributionJson["active"] =this.distribution.active ==true ? "Y": "N";
  distributionJson["Published"] =this.distribution.published ==true ? "Y" :"N";
  distributionJson["library"] =this.distribution.library;
  distributionJson["className"] =this.distribution.className;

  let selectparamList = {};
  let refParam = {};
  refParam["uuid"] = this.paramList.uuid;
  refParam["type"] = "paramlist";
  refParam["name"] = this.paramList;
  selectparamList["ref"] = refParam;

  distributionJson["paramList"] = selectparamList;

  console.log(JSON.stringify(distributionJson));
  this._commonService.submit("algorithm",distributionJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
  )


}
OnSuccessubmit(response){
  this.isSubmitEnable=true;
  this.msgs = [];
  this.msgs.push({severity:'success', summary:'Success Message', detail:'DitributionSubmitted Successfully'});
  setTimeout(() => {
    this.goBack()
    }, 1000);
     }


 }
    
      
      
    