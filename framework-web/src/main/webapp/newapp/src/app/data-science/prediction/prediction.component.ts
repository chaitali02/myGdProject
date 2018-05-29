import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { PredictionService } from '../../metadata/services/prediction.service';

import { Version } from '../../metadata/domain/version';
import { DependsOn } from '../dependsOn';
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';

@Component({
  selector: 'app-prediction',
  templateUrl: './prediction.template.html',
})
export class PredictionComponent implements OnInit {
  msgs: any[];
  checkboxModelexecution: boolean;
  isTargetNameDisabled: boolean;
  selectTarget: any;
  allTarget: any[];
  selectTargetType: string;
  targetTypes: string[];
  allTargetAttribute: any[];
  featureMapTableArray: any[];
  selectSource: DependsOn;
  allSource: any[];
  selectModel: DependsOn;
  selectSourceType: any;
  sourceTypes: any[];
  allModel: any[];
  name: any;
  version: any;
  breadcrumbDataFrom: any;
  prediction: any;
  createdBy: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  
  
    constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _predictService: PredictionService) {
    this.prediction = {};
    this.prediction["active"] = true;
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    // this.sourceTypes = [{label:"datapod",value:"datapod"},
    // {label:"dataset",value:"dataset"},
    // {label:"rule",value:"rule"},
    // ]
    this.sourceTypes=["datapod","dataset","rule"]
    this.selectSourceType=this.sourceTypes[0];
    this.targetTypes = ["datapod","file"];
    this.selectTargetType=this.targetTypes[0]; 
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/predict"
    },
    {
      "caption": "Prediction",
      "routeurl": "/app/list/predict"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];


  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
      else{
        this.getAllLatestModel()
        this.getAllLatestSource(this.selectSourceType)
        this.getAllLatestTarget(this.selectTargetType)
        //this.getAttribute()
      }
     
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'predict')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));

  }
  getAllLatestModel(){
    this._predictService.getAllModelByType("N","model")
    .subscribe(
    response => {
      this.onSuccessgetAllLatestModel(response)
    },
    error => console.log("Error :: " + error));
  }
  onSuccessgetAllLatestModel(response){
    console.log(response)
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      temp[i] = ver;
    }
    this.allModel = temp
  }
  getAllLatestSource(source){
    this._commonService.getAllLatest(source)
    .subscribe(
    response => {
      this.onSuccessgetAllLatestSource(response)
    },
    error => console.log("Error :: " + error));
  }
  getAllLatestTarget(target){
    this._commonService.getAllLatest(target)
    .subscribe(
    response => {
      this.onSuccessgetAllLatestTarget(response)
    },
    error => console.log("Error :: " + error));
  }
  onSuccessgetAllLatestTarget(response){
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.allTarget = temp
    //this.selectTarget=this.allTarget[0]
  }

  onChangeSourceType(){
    this.getAllLatestSource(this.selectSourceType)
  }
  onSuccessgetAllLatestSource(response){
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.allSource = temp
    //this.selectSource=this.allSource[0]
  }
  public get value(): string {
    return
  }
  getAllVersionByUuid() {
    {
      this._commonService.getAllVersionByUuid('predict', this.id)
        .subscribe(
        response => {
          this.onSuccessgetAllVersionByUuid(response)
        },
        error => console.log("Error ::" + error)
        )
    }

  }

  countContinue() {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";

  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }


  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'paramset')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onChangeActive(event) {
    if(event === true) {
      this.prediction.active = 'Y';
    }
    else {
      this.prediction.active = 'N';
    }
  }
  onChangePublish(event) {
    if(event === true) {
      this.prediction.published = 'Y';
    }
    else {
      this.prediction.published = 'N';
    }
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.prediction = response;
    this.uuid = response.uuid;
    const version: Version = new Version;
    version.label = response["version"];
    version.uuid = response["uuid"];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name;
    this.prediction.active = response["active"] == 'Y' ? true : false;
    this.prediction.published = response["published"] == 'Y' ? true : false;
    this.breadcrumbDataFrom[2].caption = response.name;
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    dependOnTemp.version = response["dependsOn"]["ref"]["version"];
    this.selectModel=dependOnTemp
    // this.selectSourceType.label=response["source"]["ref"]["type"];
    // this.selectSourceType.value=response["source"]["ref"]["type"];
    this.selectSourceType=response["source"]["ref"]["type"];
    this.selectTargetType=response["target"]["ref"]["type"];
    let sourceTemp: DependsOn = new DependsOn();
    sourceTemp.label = response["source"]["ref"]["name"];
    sourceTemp.uuid = response["source"]["ref"]["uuid"];
    this.selectSource=sourceTemp
    let targetTemp: DependsOn = new DependsOn();
    targetTemp.label = response["target"]["ref"]["name"];
    targetTemp.uuid = response["target"]["ref"]["uuid"];
    this.selectTarget=targetTemp
    this.getAllLatestModel()
    this.getAllLatestSource(this.selectSourceType)
    this.getAllLatestTarget(this.selectTargetType)
    this.getAttribute()
    var featureMapTableArray=[];
    for(var i=0;i<response.featureAttrMap.length;i++){
      var featureMap={};
      var sourceFeature={};
      var targetFeature={};
      featureMap["featureMapId"]=response.featureAttrMap[i].featureMapId;
      // sourceFeature.datapodname = response.featureMap[i].sourceFeature.ref.name;
      // sourceFeature.name = response.featureMap[i].sourceFeature.attrName;
      // sourceFeature.attributeId = response.featureMap[i].sourceFeature.attrId;
      // sourceFeature.id = response.featureMap[i].sourceFeature.ref.uuid + "_" + response.featureMap[i].sourceFeature.attrId;
      // sourceFeature.dname = response.featureMap[i].sourceFeature.ref.name + "." + response.featureMap[i].sourceFeature.attrName;
      sourceFeature["uuid"] = response.featureAttrMap[i].feature.ref.uuid;
      sourceFeature["type"] = response.featureAttrMap[i].feature.ref.type;
      sourceFeature["featureId"] = response.featureAttrMap[i].feature.featureId;
      sourceFeature["featureName"] = response.featureAttrMap[i].feature.featureName;
      featureMap["sourceFeature"]=sourceFeature;
      targetFeature["uuid"] = response.featureAttrMap[i].attribute.ref.uuid;
      targetFeature["type"] = response.featureAttrMap[i].attribute.ref.type;
      targetFeature["datapodname"] = response.featureAttrMap[i].attribute.ref.name;
      targetFeature["name"] = response.featureAttrMap[i].attribute.attrName;
      targetFeature["attributeId"] = response.featureAttrMap[i].attribute.attrId;
      targetFeature["id"] = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
      targetFeature["dname"] = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
      featureMap["targetFeature"]=targetFeature;
      featureMapTableArray[i]=featureMap;
    }
    this.featureMapTableArray=featureMapTableArray;
  }
  onChangeSource(){
    this.getAttribute()
  }
  getAttribute(){
    this._commonService.getAllAttributeBySource(this.selectSource.uuid,this.selectSourceType).subscribe(
      response => { 
        this.OnSuccesgetAllLatest(response)},
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response){
    let temp=[]
    for (const n in response) {
      let allname={};
      allname["label"]=response[n]['dname'];
      allname["value"]={};
      allname["value"]["label"]=response[n]['dname'];      
      allname["value"]["id"]=response[n]['id'];
      temp[n]=allname;
    }
    this.allTargetAttribute = temp

  }
  onSuccessgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.VersionList = temp
  }
  onChangeModel(){
    // PredictService.getOneByUuidandVersion(this.selectModel.uuid,this.selectModel.version,"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
    this._commonService.getOneByUuidAndVersion(this.selectModel.uuid, this.selectModel.version, 'model')
    .subscribe(
    response => {
      this.onSuccessonChangeModel(response)
    },
    error => console.log("Error :: " + error));
  }
  onSuccessonChangeModel(response) {
    var featureMapTableArray=[];
    for(var i=0;i<response.features.length;i++){
      var featureMap={};
      var sourceFeature={};
      var targetFeature={};
      featureMap["featureMapId"]=i;
      sourceFeature["uuid"] = response.uuid;
      sourceFeature["type"] = "model";
      sourceFeature["featureId"] = response.features[i].featureId;
      sourceFeature["featureName"] = response.features[i].name;
      featureMap["sourceFeature"]=sourceFeature;
      featureMapTableArray[i]=featureMap;
    }
    this.featureMapTableArray=featureMapTableArray;
}
onChangeTargeType(){
  if(this.selectTargetType =='datapod'){
    this.isTargetNameDisabled=false;
    this.getAllLatestTarget(this.selectTargetType);
    
  }else{
    this.isTargetNameDisabled=true;
    this.allTarget =[];
  }
}
public goBack() {
  //this._location.back();
  this.router.navigate(['app/list/predict']);
  
}
enableEdit(uuid, version) {
  this.router.navigate(['app/dataScience/prediction',uuid,version, 'false']);
}

showview(uuid, version) {
  this.router.navigate(['app/dataScience/prediction',uuid,version, 'true']);
}
submit(){
  var predictJson = {}
  predictJson["uuid"] = this.prediction.uuid
  predictJson["name"] = this.prediction.name
  predictJson["desc"] = this.prediction.desc
  predictJson["active"] = this.prediction.active == true ?'Y' :"N";
  predictJson["published"]=this.prediction.published == true ?'Y' :"N"; 
  // let tagArray=[];
  // if(this.dqdata.tags !=null){
  //   for(var counttag=0;counttag<this.dqdata.tags.length;counttag++){
  //        tagArray[counttag]=this.dqdata.tags[counttag];
  //   }
  // }
  var tagArray = [];
  if (this.tags != null) {
    for (var counttag = 0; counttag < this.tags.length; counttag++) {
      tagArray[counttag] = this.tags[counttag].text;
    }
  }
  predictJson["tags"] = tagArray;
  var dependsOn={};
  var ref={};
  ref["type"]="model";
  ref["uuid"]=this.selectModel.uuid;
  dependsOn["ref"]=ref;
  predictJson["dependsOn"]=dependsOn;
  var source={};
  var sourceref={};
  sourceref["type"]=this.selectSourceType;
  sourceref["uuid"]=this.selectSource.uuid;
  source["ref"]=sourceref;
  predictJson["source"]=source;
  var target={};
  var targetref={};
  targetref["type"]=this.selectTargetType;
  if(this.selectTargetType =="datapod")
  targetref["uuid"]=this.selectTarget.uuid;
  target["ref"]=targetref;
  predictJson["target"]=target;
  var featureMap=[];
  if(this.featureMapTableArray.length >0){
    for(var i=0;i<this.featureMapTableArray.length;i++){
      var featureMapObj={};
      featureMapObj["featureMapId"]=i;
      var sourceFeature={};
      var sourceFeatureRef={};
      var targetFeature={};
      var targetFeatureRef={};
      sourceFeatureRef["uuid"] = this.featureMapTableArray[i].sourceFeature.uuid;
      sourceFeatureRef["type"] = this.featureMapTableArray[i].sourceFeature.type;
      sourceFeature["ref"]=sourceFeatureRef;
      //sourceFeature.attrId = this.featureMapTableArray[i].sourceFeature.attributeId;
      sourceFeature["featureId"] = this.featureMapTableArray[i].sourceFeature.featureId;
      sourceFeature["featureName"] = this.featureMapTableArray[i].sourceFeature.featureName;
      featureMapObj["feature"]=sourceFeature;

      let  uuid=this.featureMapTableArray[i].targetFeature.id.split("_")[0]
      var attrid=this.featureMapTableArray[i].targetFeature.id.split("_")[1]
      targetFeatureRef["uuid"] = uuid;
      targetFeatureRef["type"] =this.selectSourceType;
      targetFeature["ref"]=targetFeatureRef
      targetFeature["attrId"] =attrid;
      
      featureMapObj["attribute"]=targetFeature;
      featureMap[i]=featureMapObj;
    }
  }
  predictJson["featureAttrMap"]=featureMap;
  console.log(JSON.stringify(predictJson))
  this._commonService.submit("predict",predictJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
  )
}
OnSuccessubmit(response){
  if (this.checkboxModelexecution == true) {
    this._commonService.getOneById("predict",response).subscribe(
        response => {this.OnSucessGetOneById(response); },
        error => console.log('Error :: ' + error)
    )
  } //End if
  else{
    this.isSubmit="false";
   // this.IsProgerssShow="false";
    this.msgs = [];
    this.msgs.push({severity:'success', summary:'Success Message', detail:'Predict Save Successfully'});
    setTimeout(() => {
    this.goBack();
    
    }, 1000);
  }
}
OnSucessGetOneById(response){
  this._commonService.execute(response.uuid,response.version,"predict","execute").subscribe(
    response => {
     this.showMassage('Predict Save and Submit Successfully','success','Success Message')
     setTimeout(() => {
      this.goBack()
    }, 1000);
    },
    error => console.log('Error :: ' + error)
  )
}
showMassage(msg,msgtype,msgsumary){
  this.isSubmit="false";
  //this.IsProgerssShow="false";
  this.msgs = [];
  this.msgs.push({severity:msgtype, summary:msgsumary, detail:msg});
}
}



