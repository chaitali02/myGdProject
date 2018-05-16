import { Location } from '@angular/common';
import { AppConfig } from './../../app.config';
import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { DependsOn } from '../dependsOn';

@Component({
  selector: 'app-algorithm',
  templateUrl: './algorithm.template.html',
  styleUrls: []
})
export class AlgorithmComponent implements OnInit {
  breadcrumbDataFrom : any;
  showAlgorithm : any;
  algorithm : any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  trainName : any;
  modelName : any;
  type : any;
  library : any;
  librarytypesOption : {'value' : String , 'label' : String}[];
  typesOption : {'value' : String , 'label' : String}[];
  arrayParamList : any;
  paramList : any;
  msgs : any;
  isSubmitEnable:any;

  constructor(private _location : Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) { 
    this.showAlgorithm = true;
    this.algorithm = {};
    this.algorithm["active"]=true;
    this.algorithm["labelRequired"]=true;
    this.isSubmitEnable=true;
    this.paramList = {};
    this.breadcrumbDataFrom=[{
      "caption":"Data Science",
      "routeurl":"/app/list/algorithm"
    },
    {
      "caption":"Algorithm",
      "routeurl":"/app/list/algorithm"
    },
    {
      "caption":"",
      "routeurl":null      
    }
    ] 

    this.librarytypesOption=[
      {"value" : "sparkML", "label" : "sparkML"},
      {"value" : "R", "label" : "R"},
      {"value" : "java", "label" : "java"}
    ]

    this.typesOption=[
      {"value" : "clustering", "label" : "clustering"},
      {"value" : "classification", "label" : "classification"},
      {"value" : "regression", "label" : "regression"}
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if(this.mode !== undefined){
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();  
      }
      this.getAllLatest();    
    })
  }

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'algorithm')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('algorithm',this.id)
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
    this.algorithm=response;
    this.uuid=response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version 
    this.createdBy=response.createdBy.ref.name;  
    this.algorithm.published=response["published"] == 'Y' ? true : false
    this.algorithm.active=response["active"] == 'Y' ? true : false

    let dependsOn : DependsOn = new DependsOn();
    dependsOn.uuid=response["paramList"]["ref"]["uuid"];
    dependsOn.label=response["paramList"]["ref"]["name"];
    this.paramList=dependsOn;
  
    this.breadcrumbDataFrom[2].caption=this.algorithm.name;
    console.log('Data is' + response);
    
  }

  OnSuccesgetAllVersionByUuid(response) {
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
      let refParam = {};
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}      
      refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['name'];
      refParam["value"]['uuid'] = response[i]['uuid'];
      this.arrayParamList[i] = refParam;
    }    
  }

  onVersionChange(){ 
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'paramset')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.algorithm.active = 'Y';
    }
    else {
      this.algorithm.active = 'N';
    }
  }

  onChangePublished(event) {
    if(event === true) {
      this.algorithm.published = 'Y';
    }
    else {
      this.algorithm.published = 'N';
    }
  }

  onChangeLabel(event) {
    if(event === true) {
      this.algorithm.published = 'Y';
    }
    else {
      this.algorithm.published = 'N';
    }
  }

  submitAlgorithm(){
    this.isSubmitEnable=true;
    let algoJson = {};
    algoJson["uuid"] = this.algorithm.uuid;
    algoJson["name"] = this.algorithm.name;
    // const tagstemp = [];
    // for (const t in this.tags) {
    //  tagstemp.push(this.tags[t]["value"]);
    // }
    // algoJson["tags"] = tagstemp;
    algoJson["desc"]=this.algorithm.desc;
  
    algoJson["active"]=this.algorithm.active == true ?'Y' :"N"
    algoJson["published"]=this.algorithm.published == true ?'Y' :"N"
    algoJson["type"]=this.algorithm.type;
    algoJson["library"]=this.algorithm.library;
    algoJson["trainName"]=this.algorithm.trainName;
    algoJson["modelName"]=this.algorithm.modelName; 
    algoJson["labelRequired"]=this.algorithm.labelRequired; 

    let selectparamList = {};
    let refParam = {};

    refParam["uuid"] = this.paramList.uuid;
    refParam["type"] = "paramlist";
    refParam["name"] = this.paramList.name;
    selectparamList["ref"] = refParam;

    algoJson["paramList"] = selectparamList;

    console.log(JSON.stringify(algoJson));
    this._commonService.submit("algorithm",algoJson).subscribe(
      response => { this.OnSuccessubmit(response)},
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response){
    this.isSubmitEnable=true;
    this.msgs = [];
    this.msgs.push({severity:'success', summary:'Success Message', detail:'Algorithm Submitted Successfully'});
    setTimeout(() => {
      this.goBack()
      }, 1000);
       }

  public goBack() {
     // this._location.back();
    this.router.navigate(['app/list/algorithm']);
     }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/algorithm',uuid,version, 'false']);
  }
  
  showview(uuid, version) {
    this.router.navigate(['app/dataScience/algorithm',uuid,version, 'true']);
  }


}