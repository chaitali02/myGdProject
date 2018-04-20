import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';  
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { ModelService } from '../../metadata/services/model.service';

import { Version } from '../../metadata/domain/version';
import{ DependsOn } from '../dependsOn';
import {AttributeHolder} from '../../metadata/domain/domain.attributeHolder'

@Component({
  selector: 'app-model',
  templateUrl: './model.template.html',
})
export class ModelComponent implements OnInit {
  IsLableSelected: boolean;
  selectallattribute: any;
  isTabelShow: boolean;
  paramtable: any[];
  paramtablecol: any;
  paramsetdata: any;
  isShowExecutionparam: boolean;
  allParameterset: any;
  sourceAlgorithm: any;
  checkboxModelexecution: boolean;
  selectedlabel: any;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  allAttribute: any[];
  selectAlgorithm: any;
  allAlgorithm: any[];
  sources: string[];
  createdBy: any;
  allNames: any[];
  sourcedata: any;
  version: any;
  breadcrumbDataFrom : any;
  showModel : any;
  model : any;
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
  allSourceAttribute : any[];
  featuresArray : any[];
  featuresTags : any[];
  featureResponse : any;
  nameResponse : any;
  labelTags : any;
  labelResponse : any;
  allSourceLabel : any;
  labelArray : any;

  constructor( config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _location:Location,private _modelService:ModelService) {
    this.showModel = true;
    this.model = {};
    this.model["active"]=true;
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
      "routeurl":"/app/list/model"
    },
    {
      "caption":"Model",
      "routeurl":"/app/list/model"
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
        this.getAllLatesAlogrithm(false);
        
 
      }
      else{
        this.getAllLatest(true);
        this.getAllLatesAlogrithm(true);
      }
    });
  }
  
  countContinue=function(){
    this.continueCount=this.continueCount+1;
    this.progressbarWidth=25*this.continueCount+"%"; 
  }

  countBack=function(){
    this.continueCount=this.continueCount-1;
    this.progressbarWidth=25*this.continueCount+"%";
  }
  public goBack() {
    this._location.back();
  }
  SourceType(){
    this.featuresTags=null;
    this.getAllLatest(true);
  }
  changeSoruce(){
    this.featuresTags=null;
    this.getAllAttributeBySource();
    this.getAllAttributeBySourceLabel();
  }
  changeAlgorithm(){
    this.getLatestAlgorithm(this.selectAlgorithm["uuid"],true);
  }
  changeCheckboxExecution() {
    if (this.checkboxModelexecution == true) {
        this._modelService.getParamSetByAlgorithm(this.selectAlgorithm.uuid,this.selectAlgorithm.version)
        .subscribe(
            response =>{
                this.onSuccessGetParamSetByParmLsit(response)},
            error => console.log("Error :: " + error)); 
    } else {
      this.isShowExecutionparam = false;
      this.allParameterset = null;
    }
  }
  onSelectparamSet(){
    
    var paramSetjson = {};
    var paramInfoArray = [];
    if (this.paramsetdata &&  this.paramsetdata != null) {
      for (var i = 0; i < this.paramsetdata.paramInfo.length; i++) {
        var paramInfo = {};
        paramInfo["paramSetId"] = this.paramsetdata.paramInfo[i].paramSetId
        paramInfo["selected"] = false
        var paramSetValarray = [];
        for (var j = 0; j < this.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
          var paramSetValjson = {};
          paramSetValjson["paramId"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
          paramSetValjson["paramName"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
          paramSetValjson["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
          paramSetValjson["ref"] = this.paramsetdata.paramInfo[i].paramSetVal[j].ref;
          paramSetValarray[j] = paramSetValjson;
          paramInfo["paramSetVal"] = paramSetValarray;
          paramInfo["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
        }
        paramInfoArray[i] = paramInfo;
      }
      this.paramtablecol = paramInfoArray[0].paramSetVal;
      this.paramtable = paramInfoArray;
      paramSetjson["paramInfoArray"] = paramInfoArray;
      this.isTabelShow = true;
    } else {
        this.isTabelShow = false;
    }
  }
  selectAllRow () {
    
      if (!this.selectallattribute){
          this.selectallattribute = true;
      }
      else {
          this.selectallattribute = false;
      }
      this.paramtable.forEach(stage => {
         
        stage.selected = this.selectallattribute;
      });
  }
onSuccessGetParamSetByParmLsit(response) {
    this.allParameterset = response
    this.isShowExecutionparam = true;

}
  getAllAttributeBySourceLabel(){
    
    this._commonService.getLatestByUuid(this.sourcedata.uuid,this.source)
    .subscribe(
    response =>{
      this.onSuccesgetAllAttributeBySourceLabel(response)},
    error => console.log("Error :: " + error));

  }
  
  onSuccesgetAllAttributeBySourceLabel(response){
    let attribute=[];
    let allname={};
    allname["label"]='-select-'
    allname["value"]=null;
    attribute.push(allname);
    for (const n in response.attributes) {
        if( response.attributes[n].type.toLowerCase() =="integer" || response.attributes[n].type.toLowerCase() =="double"){
          let allname={};
          allname["label"]=response['name']+"."+response.attributes[n]['name'];
          allname["value"]={};
          allname["value"]["label"]=response['name']+"."+response.attributes[n]['name'];     
          allname["value"]["uuid"]=response['uuid'];
          allname["value"]["u_Id"]=response['uuid']+"_"+response.attributes[n]['attributeId'];
          allname["value"]["attrId"]=response.attributes[n]['attributeId'];
          attribute.push(allname)
        }
    }
    this.labelArray=attribute
    if( this.IsLableSelected == true){
      // let algorithmTemp: DependsOn = new DependsOn();
      // algorithmTemp.label =  this.labelArray[1]["name"];
      // algorithmTemp.uuid =  this.labelArray[1]["uuid"];
      // algorithmTemp.version =  this.labelArray[1]["version"];
      // this.selectedlabel=algorithmTemp;
    }
  }

  getAllLatesAlogrithm(IsDefault){
    this._commonService.getAllLatest('algorithm')
    .subscribe(
    response =>{
      this.OnSuccessgetAllLatestAlgorithm(response,IsDefault)},
    error => console.log("Error :: " + error))
  }

  OnSuccessgetAllLatestAlgorithm(response,IsDefault){
    let temp=[]
   
    if(this.mode == undefined || IsDefault == true) {
      let algorithmTemp: DependsOn = new DependsOn();
      algorithmTemp.label = response[0]["name"];
      algorithmTemp.uuid = response[0]["uuid"];
      algorithmTemp.version = response[0]["version"];
      this.selectAlgorithm=algorithmTemp;
      this.getLatestAlgorithm(response[0]["uuid"],true);
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
    this.allAlgorithm = temp
  }
  getLatestAlgorithm(uuid,IsDefault){
    this._commonService.getLatestByUuid(uuid,"algorithm")
    .subscribe(
        response =>{
            this.OnSuccesGetLatestAlgorithm(response,IsDefault)},
        error => console.log("Error :: " + error));
  }
  OnSuccesGetLatestAlgorithm(response,IsDefault){
    this.selectedlabel=null

    if(response.labelRequired == 'Y'){
      this.IsLableSelected=true
    }
    else{
      this.IsLableSelected=false;
    }

  }
  getAllLatest(IsDefault){
    this._commonService.getAllLatest(this.source).subscribe(
        response => { this.OnSuccesgetAllLatest(response,IsDefault)},
        error => console.log('Error :: ' + error)
    ) 
}

  OnSuccesgetAllLatest(response1,IsDefault){
    let temp=[]
    if(this.mode == undefined || IsDefault == true) {
        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response1[0]["name"];
        dependOnTemp.uuid = response1[0]["uuid"];
        this.sourcedata=dependOnTemp
    }
    for (const n in response1) {
        let allname={};
        allname["label"]=response1[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response1[n]['name'];      
        allname["value"]["uuid"]=response1[n]['uuid'];
        temp[n]=allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource();
    this.getAllAttributeBySourceLabel();
 
  }

  getAllAttributeBySource(){
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
        response => { this.OnSuccesgetAllAttributeBySource(response)},
        error => console.log('Error :: ' + this.sourcedata.uuid)
    ) 
  }

  OnSuccesgetAllAttributeBySource(response){ 
    this.allSourceAttribute=[]  
    let attribute=[]
    for (const n in response) {
        let allname={};
        allname["id"]=response[n]['uuid']+"_"+response[n]['attributeId'];
        allname["itemName"]=response[n]['dname'];
        allname["uuid"]=response[n]['uuid'];
        allname["attrId"]=response[n]['attributeId'];
        attribute[n]=allname
    }
    this.allAttribute=attribute
    
  }
  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('model',this.id)
    .subscribe(
        response =>{
            this.OnSuccesgetAllVersionByUuid(response)},
        error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp=[]
    for (const i in response) {
        let ver={};
        ver["label"]=response[i]['version'];
        ver["value"]={};
        ver["value"]["label"]=response[i]['version'];      
        ver["value"]["uuid"]=response[i]['uuid'];
        ver["value"]["u_Id"]=response[i]['uuid']+"_"+response[i]['version']
        temp[i]=ver;
    }
    this.VersionList=temp
  }  

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'model')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onSuccessgetOneByUuidAndVersion(response){
    this.breadcrumbDataFrom[2].caption=response.name; 
    this.model=response;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    version.u_Id=response['uuid']+"_"+response['version'];
    this.selectedVersion=version;
    this.createdBy=response.createdBy.ref.name
    this.model.published=response["published"] == 'Y' ? true : false
    this.model.active=response["active"] == 'Y' ? true : false
    this.tags = response['tags'];
    this.source=response["source"]["ref"].type
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["source"]["ref"]["name"];
    dependOnTemp.uuid = response["source"]["ref"]["uuid"];
    this.sourcedata=dependOnTemp;
    this.getAllLatest(false);
    let algorithmTemp: DependsOn = new DependsOn();
    algorithmTemp.label = response["algorithm"]["ref"]["name"];
    algorithmTemp.uuid = response["algorithm"]["ref"]["uuid"];
    algorithmTemp.version = response["algorithm"]["ref"]["version"];
    this.selectAlgorithm=algorithmTemp;
    let featureNew = [];
    for(const i in response.features){
      let featuretag={};
      featuretag["id"]=response.features[i]["ref"]["uuid"]+"_"+response.features[i].attrId;
      featuretag["itemName"]=response.features[i]["ref"]["name"]+"."+response.features[i]["attrName"];
      featuretag["uuid"]=response.features[i]["ref"]["uuid"];
      featuretag["attrId"]=response.features[i]["attrId"];
      featureNew[i]=featuretag;
      featuretag["type"]=response.features[i]["ref"]["type"];
    }
    this.featuresTags = featureNew;
    if(response.label.ref !=null){
      this.IsLableSelected=true
      let  labeltemp : AttributeHolder = new AttributeHolder();
      labeltemp.label=response.label.ref.name+"."+response.label.attrName;
      labeltemp.u_Id=response.label.ref.uuid+"_"+response.label.attrId;
      labeltemp.uuid=response.label.ref.uuid;
      labeltemp.attrId=response.label.attrId;
      this.selectedlabel=labeltemp;
    }
    console.log(this.selectedlabel)
  }


  submit(){
    this.isSubmit="true"
    let modelJson={};
    modelJson["uuid"]=this.model.uuid;
    modelJson["name"]=this.model.name;
    modelJson["desc"]=this.model.desc;
    let tagArray=[];
    if(this.model.tags !=null){
        for(var counttag=0;counttag<this.model.tags.length;counttag++){
            tagArray[counttag]=this.model.tags[counttag];
        }   
    }
    modelJson["tags"]=tagArray;
    modelJson["active"]=this.model.active == true ?'Y' :"N"
    modelJson["published"]=this.model.published == true ?'Y' :"N"
    let  source = {};
    let ref={};
    ref["type"] = this.source
    ref["uuid"] = this.sourcedata.uuid;
    source["ref"] = ref;
    modelJson["source"]=source;      
    let attributeInfo=[]
    if(this.featuresTags.length >0){
      for(let i=0;i< this.featuresTags.length;i++){
        let featuresinfo={};
        let ref={};
        ref["type"]=this.source;
        ref["uuid"]=this.featuresTags[i]["uuid"];
        featuresinfo["ref"]=ref;
        featuresinfo["attrId"]=this.featuresTags[i]["attrId"];
        attributeInfo[i]=featuresinfo;
      }
    }
    modelJson["features"]=attributeInfo;
    let algorithm={};
    let algoritmref={};
    algoritmref["type"]="algorithm";
    algoritmref["uuid"]=this.selectAlgorithm.uuid;
    algorithm["ref"]=algoritmref
    modelJson["algorithm"]=algorithm;
    if(this.IsLableSelected == true){
      let label={};
      let ref={};
      ref["type"]=this.source;
      ref["uuid"]=this.selectedlabel.uuid;
      label["ref"]=ref;
      label["attrId"]=this.selectedlabel.attrId.toString();
      modelJson["label"]=label;
    }
    else{
      modelJson["label"]=null;
    }
    console.log(modelJson);
    this._commonService.submit("model",modelJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
  )
}  

OnSuccessubmit(response){
    if (this.checkboxModelexecution == true) {
        this._commonService.getOneById("model",response).subscribe(
            response => {  this.modelExecute(response);},
            error => console.log('Error :: ' + error)
        )
    } //End if
    else{
        this.msgs = [];
        this.isSubmit="true"
        this.msgs.push({severity:'success', summary:'Success Message', detail:'Rule Saved Successfully'});
        setTimeout(() => {
          this.goBack()
        }, 1000);
     
    }
}
modelExecute(modeldetail){
  let newDataList = [];
  this.selectallattribute = false;
  let  execParams = {}
  if(this.paramtable){
  this.paramtable.forEach(selected => {
      if(selected.selected) {
          newDataList.push(selected);
      }
  });

  let paramInfoArray = [];
  
  if ( this.paramtable && newDataList.length > 0) {
      let  ref = {}
      ref["uuid"] = this.paramsetdata.uuid;
      ref["version"] = this.paramsetdata.version;
      for (var i = 0; i < newDataList.length; i++) {
          var paraminfo = {};
          paraminfo["paramSetId"] = newDataList[i].paramSetId;
          paraminfo["ref"] = ref;
          paramInfoArray[i] = paraminfo;
      }
  }

  if (paramInfoArray.length > 0) {
      execParams["paramInfo"] = paramInfoArray;
  } 
  else {
  execParams = null
  } 
}
  console.log(JSON.stringify(execParams));
  this._modelService.getExecuteModelWithBody(modeldetail["uuid"],modeldetail["version"],execParams).subscribe(
      response => {this.onSuccessExecute(response)},
      error => console.log('Error :: ' + error)
  ) 
}

onSuccessExecute(response){
  this.msgs = [];
  this.isSubmit="true"
  this.msgs.push({severity:'success', summary:'Success Message', detail:'Model Saved and Submited Successfully'});
  setTimeout(() => {
      this.goBack()
  }, 1000);
}
  
}
