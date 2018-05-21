

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { TrainingService } from '../../metadata/services/training.service';

import { Version } from '../../metadata/domain/version';
import { DependsOn } from '../dependsOn';
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';

@Component({
  selector: 'app-train',
  templateUrl: './training.template.html',
})
export class TrainingComponent implements OnInit {
  msgs: any[];
  checkboxModelexecution: boolean;
  isTargetNameDisabled: boolean;
  selectTarget: any;
  allTarget: any[];
  selectTargetType: string;
  targetTypes: string[];
  //allTargetAttribute: any[];
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
  train: any;
  createdBy: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  allAttribute: any[];

  published: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  
  
    constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _trainService: TrainingService) {
    this.train = {};
    this.train["active"] = true;
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
      "routeurl": "/app/list/train"
    },
    {
      "caption": "Training",
      "routeurl": "/app/list/train"
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
       // this.getAttribute()
      }
     
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'train')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));

  }
  getAllLatestModel(){
    this._trainService.getAllModelByType("N","model")
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
      this._commonService.getAllVersionByUuid('train', this.id)
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
      this.train.active = 'Y';
    }
    else {
      this.train.active = 'N';
    }
  }
  onChangePublish(event) {
    if(event === true) {
      this.train.published = 'Y';
    }
    else {
      this.train.published = 'N';
    }
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.train = response;
    this.uuid = response.uuid;
    const version: Version = new Version;
    version.label = response["version"];
    version.uuid = response["uuid"];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name;
    this.train.active = response["active"] == 'Y' ? true : false;
    this.train.published = response["published"] == 'Y' ? true : false;
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
    this.allAttribute = temp

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
  this.router.navigate(['app/list/train']);
  
}
enableEdit(uuid, version) {
  this.router.navigate(['app/dataScience/train',uuid,version, 'false']);
}

showview(uuid, version) {
  this.router.navigate(['app/dataScience/train',uuid,version, 'true']);
}
submit(){
  var trainJson = {}
  trainJson["uuid"] = this.train.uuid
  trainJson["name"] = this.train.name
  trainJson["desc"] = this.train.desc
  trainJson["active"] = this.train.active == true ?'Y' :"N";
  trainJson["published"]=this.train.published == true ?'Y' :"N"; 
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
  trainJson["tags"] = tagArray;
  var dependsOn={};
  var ref={};
  ref["type"]="model";
  ref["uuid"]=this.selectModel.uuid;
  dependsOn["ref"]=ref;
  trainJson["dependsOn"]=dependsOn;
  var source={};
  var sourceref={};
  sourceref["type"]=this.selectSourceType;
  sourceref["uuid"]=this.selectSource.uuid;
  source["ref"]=sourceref;
  trainJson["source"]=source;
  var target={};
  var targetref={};
  targetref["type"]=this.selectTargetType;
  if(this.selectTargetType =="datapod")
  targetref["uuid"]=this.selectTarget.uuid;
  target["ref"]=targetref;
  trainJson["target"]=target;
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
  trainJson["featureAttrMap"]=featureMap;
  console.log(JSON.stringify(trainJson))
  this._commonService.submit("train",trainJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
  )
}
OnSuccessubmit(response){
  if (this.checkboxModelexecution == true) {
    this._commonService.getOneById("train",response).subscribe(
        response => {this.OnSucessGetOneById(response); },
        error => console.log('Error :: ' + error)
    )
  } //End if
  else{
    this.isSubmit="false";
   // this.IsProgerssShow="false";
    this.msgs = [];
    this.msgs.push({severity:'success', summary:'Success Message', detail:'DQ Save Successfully'});
    setTimeout(() => {
    this.goBack();
    
    }, 1000);
  }
}
OnSucessGetOneById(response){
  this._commonService.execute(response.uuid,response.version,"train","execute").subscribe(
    response => {
     this.showMassage('train Save and Submit Successfully','success','Success Message')
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






// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute, Router, Params } from '@angular/router';
// import { Location } from '@angular/common';
// import { SelectItem } from 'primeng/primeng';

// import { AppConfig } from '../../app.config';

// import { CommonService } from '../../metadata/services/common.service';
// import { TrainingService } from '../../metadata/services/training.service';

// import { Version } from '../../metadata/domain/version';
// import { DependsOn } from '../dependsOn';
// import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'

// @Component({
//   selector: 'app-train',
//   templateUrl: './training.template.html',
// })
// export class TrainingComponent implements OnInit {
//   IsLableSelected: boolean;
//   selectallattribute: any;
//   isTabelShow: boolean;
//   paramtable: any[];
//   paramtablecol: any;
//   paramsetdata: any;
//   isShowExecutionparam: boolean;
//   allParameterset: any;
//   sourceAlgorithm: any;
//   checkboxtrainexecution: boolean;
//   selectedlabel: any;
//   dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
//   allAttribute: any[];
//   selectAlgorithm: any;
//   allAlgorithm: any[];
//   sources: string[];
//   createdBy: any;
//   allNames: any[];
//   sourcedata: any;
//   version: any;
//   breadcrumbDataFrom: any;
//   showtrain: any;
//   train: any;
//   tags: any;
//   id: any;
//   mode: any;
//   uuid: any;
//   active: any;
//   published: any;
//   depends: any;
//   continueCount: any;
//   progressbarWidth: any;
//   isSubmit: any
//   selectedVersion: Version;
//   VersionList: SelectItem[] = [];
//   msgs: any[];
//   arrayModel: any;
//   model: DependsOn;
//   source: string;
//   allSourceAttribute: any[];
//   featuresArray: any[];
//   featuresTags: any[];
//   featureResponse: any;
//   nameResponse: any;
//   labelTags: any;
//   labelResponse: any;
//   allSourceLabel: any;
//   labelArray: any;
//   selectmodel:DependsOn;
//   featureMap :any;
//   sourceFeature:any;
//   featureMapTableArray :any[];
//   modelData :any[];
//   selectedRunImmediately :any

//   constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _trainService: TrainingService) {
//     //this.showtrain = true;
//     this.train = {};
//     this.train["active"] = true;
//     this.continueCount = 1;


//     this.isSubmit = "false";
//     // this.IsLableSelected=false
//     this.progressbarWidth = 25 * this.continueCount + "%";
//     this.dropdownSettings = {
//       singleSelection: false,
//       text: "Select Attrubutes",
//       selectAllText: 'Select All',
//       unSelectAllText: 'UnSelect All',
//       enableSearchFilter: true,
//       classes: "myclass custom-class",
//       maxHeight: 110,
//       disabled: false
//     };
//     this.breadcrumbDataFrom = [{
//       "caption": "Data Science",
//       "routeurl": "/app/list/train"
//     },
//     {
//       "caption": "Training",
//       "routeurl": "/app/list/train"
//     },
//     {
//       "caption": "",
//       "routeurl": null
//     }
//     ];
//     this.sources = ["datapod", "dataset", "rule"];
//     this.source = this.sources[0];

//   }

//   ngOnInit() {
//     this.activatedRoute.params.subscribe((params: Params) => {
//       this.id = params['id'];
//       this.version = params['version'];
//       this.mode = params['mode'];
//       if (this.mode !== undefined) {
//         this.getOneByUuidAndVersion();
//         this.getAllVersionByUuid();
//       }
//       this.getAllLatest(true);
//       this.getAllModelByType()
//     })
//   }

//   countContinue = function () {
//     this.continueCount = this.continueCount + 1;
//     this.progressbarWidth = 25 * this.continueCount + "%";
//   }

//   countBack = function () {
//     this.continueCount = this.continueCount - 1;
//     this.progressbarWidth = 25 * this.continueCount + "%";
//   }
//   public goBack() {
//     this._location.back();
//   }
//   changeSoruce() {
//     this.featuresTags = null;
//     this.getAllAttributeBySource();
//     this.getAllAttributeBySourceLabel();
//   }
//   SourceType() {
//     this.featuresTags = null;
//     this.getAllLatest(true);
//   }
//   onChangeModel(){
//     debugger
//     this._commonService.getAllAttributeBySource(this.selectmodel.uuid,'model').subscribe(
//       response => { this.OnSuccesgetAllAttributeByDataPodSourceAdd(response)},
//       error => console.log('Error :: ' + error)
//     )

//   }


//   getAllVersionByUuid() {
//     debugger
//     this._commonService.getAllVersionByUuid('train', this.id)
//       .subscribe(
//       response => {
//         this.OnSuccesgetAllVersionByUuid(response)
//       },
//       error => console.log("Error :: " + error));
//   }

//   OnSuccesgetAllVersionByUuid(response) {
//     debugger
//     var temp = []
//     for (const i in response) {
//       let ver = {};
//       ver["label"] = response[i]['version'];
//       ver["value"] = {};
//       ver["value"]["label"] = response[i]['version'];
//       ver["value"]["uuid"] = response[i]['uuid'];
//       ver["value"]["u_Id"] = response[i]['uuid'] + "_" + response[i]['version']
//       temp[i] = ver;
//     }
//     this.VersionList = temp
//   }

//   getOneByUuidAndVersion() {
//     debugger
//     this._commonService.getOneByUuidAndVersion(this.id, this.version, 'train')
//       .subscribe(
//       response => {
//         this.onSuccessgetOneByUuidAndVersion(response)
//       },
//       error => console.log("Error :: " + error));
//   }

//   onSuccessgetOneByUuidAndVersion(response) {
//     debugger
//     this.train = response;
//     this.uuid = response.uuid;
//     const version: Version = new Version();
//     version.label = response['version'];
//     version.uuid = response['uuid'];
//     this.selectedVersion = version;
//     this.createdBy = response.createdBy.ref.name
//     this.train.published = response["published"] == 'Y' ? true : false
//     this.train.active = response["active"] == 'Y' ? true : false
//     this.breadcrumbDataFrom[2].caption = this.train.name
//     ame;
//     let dependOnTemp: DependsOn = new DependsOn();
//     dependOnTemp.label = response["dependsOn"]["ref"]["name"];
//     dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
//     dependOnTemp.version = response["dependsOn"]["ref"]["version"];
//     this.selectModel=dependOnTemp
//     // this.selectSourceType.label=response["source"]["ref"]["type"];
//     // this.selectSourceType.value=response["source"]["ref"]["type"];
//     this.selectSourceType=response["source"]["ref"]["type"];
//     this.selectTargetType=response["target"]["ref"]["type"];
//     let sourceTemp: DependsOn = new DependsOn();
//     sourceTemp.label = response["source"]["ref"]["name"];
//     sourceTemp.uuid = response["source"]["ref"]["uuid"];
//     this.selectSource=sourceTemp
//     let targetTemp: DependsOn = new DependsOn();
//     targetTemp.label = response["target"]["ref"]["name"];
//     targetTemp.uuid = response["target"]["ref"]["uuid"];
//     this.selectTarget=targetTemp
//     this.getAllLatestModel()
//     this.getAllLatestSource(this.selectSourceType)
//     this.getAllLatestTarget(this.selectTargetType)
//     this.getAttribute()
//     var featureMapTableArray=[];
//     for(var i=0;i<response.featureAttrMap.length;i++){
//       var featureMap={};
//       var sourceFeature={};
//       var targetFeature={};
//       featureMap["featureMapId"]=response.featureAttrMap[i].featureMapId;
//       // sourceFeature.datapodname = response.featureMap[i].sourceFeature.ref.name;
//       // sourceFeature.name = response.featureMap[i].sourceFeature.attrName;
//       // sourceFeature.attributeId = response.featureMap[i].sourceFeature.attrId;
//       // sourceFeature.id = response.featureMap[i].sourceFeature.ref.uuid + "_" + response.featureMap[i].sourceFeature.attrId;
//       // sourceFeature.dname = response.featureMap[i].sourceFeature.ref.name + "." + response.featureMap[i].sourceFeature.attrName;
//       sourceFeature["uuid"] = response.featureAttrMap[i].feature.ref.uuid;
//       sourceFeature["type"] = response.featureAttrMap[i].feature.ref.type;
//       sourceFeature["featureId"] = response.featureAttrMap[i].feature.featureId;
//       sourceFeature["featureName"] = response.featureAttrMap[i].feature.featureName;
//       featureMap["sourceFeature"]=sourceFeature;
//       targetFeature["uuid"] = response.featureAttrMap[i].attribute.ref.uuid;
//       targetFeature["type"] = response.featureAttrMap[i].attribute.ref.type;
//       targetFeature["datapodname"] = response.featureAttrMap[i].attribute.ref.name;
//       targetFeature["name"] = response.featureAttrMap[i].attribute.attrName;
//       targetFeature["attributeId"] = response.featureAttrMap[i].attribute.attrId;
//       targetFeature["id"] = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
//       targetFeature["dname"] = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
//       featureMap["targetFeature"]=targetFeature;
//       featureMapTableArray[i]=featureMap;
//     }
//     this.featureMapTableArray=featureMapTableArray;

//     console.log('Data is' + response)



//   }



//   GetLatestByUuid(){
// this._commonService.getLatestByUuid(this.uuid,'model')
// .subscribe(
//   response=>{
//     this.onSucessgetLatestByUuid(response)
//   },
//   error => console.log("Error :: " + error))

//   }

//   onSucessgetLatestByUuid(response){
 
//     this.modelData = response;
//     if (this.selectmodel) {
//       this.selectedRunImmediately = "NO";
//       this.isShowExecutionparam = false;
//       var featureMapTableArray = [];
//       for (var i = 0; i < response.features.length; i++) {
//         var featureMap = {};
//         var sourceFeature = {};
//         var targetFeature = {};
//         this.featureMap.featureMapId = i;
//         this.sourceFeature.uuid = response.uuid;
//         this.sourceFeature.type = "model";
//         this.sourceFeature.featureId = response.features[i].featureId;
//         this.sourceFeature.featureName = response.features[i].name;
//         this.featureMap.sourceFeature = sourceFeature;
//         this.featureMapTableArray[i] = featureMap;
//         this.featureMapTableArray = featureMapTableArray;
//       }
//     }  
//   }


//   getAllModelByType() {
//     debugger
//     this._trainService.getAllModelByType('N', 'model')
//       .subscribe(
//       response => {
//         this.onSuccessgetAllLatestModel(response)
//       },
//       error => console.log("Error :: " + error))
//   }


// OnSuccesgetAllAttributeByDataPodSourceAdd(response){
//   debugger
//   this.model=response;
 
//  // this.selectedRunImmediately = "NO";
//  // this.isShowExecutionparam = false;
//   var featureMapTableArray = [];
//   for (var i = 0; i < response.features.length; i++) {
//     var featureMap = {};
//     var sourceFeature = {};
//     var targetFeature = {};
//     this.featureMap.featureMapId = i;
//     this.sourceFeature.uuid = response.uuid;
//     this.sourceFeature.type = "model";
//     this.sourceFeature.featureId = response.features[i].featureId;
//    this. sourceFeature.featureName = response.features[i].name;
//     this.featureMap.sourceFeature = sourceFeature;
//     featureMapTableArray[i] = featureMap;
//     this.featureMapTableArray = featureMapTableArray;
  
// }


//   }
//   onSuccessgetAllLatestModel(response) {
//     debugger

//     this.arrayModel = [];
//     for (const i in response) {
//       let refParam = {}
//       refParam["label"] = response[i]['name'];
//       refParam["value"] = {}
//       refParam["value"]["uuid"] = response[i]["uuid"]
//       // refParam["value"]['name'] = response[i]['name'];
//       refParam["value"]['label'] = response[i]['name'];
//       this.arrayModel[i] = refParam;

//     }
//   }

//   getAllLatest(IsDefault) {
//     debugger
//     this._commonService.getAllLatest(this.source).subscribe(
//       response => { this.OnSuccesgetAllLatest(response, IsDefault) },
//       error => console.log('Error :: ' + error)
//     )
//   }
//   OnSuccesgetAllLatest(response1, IsDefault) {
//     debugger
//     let temp = []
//     if (this.mode == undefined || IsDefault == true) {
//       let dependOnTemp: DependsOn = new DependsOn();
//       dependOnTemp.label = response1[0]["name"];
//       dependOnTemp.uuid = response1[0]["uuid"];
//       this.sourcedata = dependOnTemp
//     }
//     for (const n in response1) {
//       let allname = {};
//       allname["label"] = response1[n]['name'];
//       allname["value"] = {};
//       allname["value"]["label"] = response1[n]['name'];
//       allname["value"]["uuid"] = response1[n]['uuid'];
//       temp[n] = allname;
//     }
//     this.allNames = temp
//     this.getAllAttributeBySource();
//     this.getAllAttributeBySourceLabel();

//   }
//   getAllAttributeBySourceLabel() {
//     debugger
//     this._commonService.getLatestByUuid(this.sourcedata.uuid, this.source)
//       .subscribe(
//       response => {
//         this.onSuccesgetAllAttributeBySourceLabel(response)
//       },
//       error => console.log("Error :: " + error));

//   }
//   onSuccesgetAllAttributeBySourceLabel(response) {
//     debugger
//     let attribute = [];
//     let allname = {};
//     allname["label"] = '-select-'
//     allname["value"] = null;
//     attribute.push(allname);
//     for (const n in response.attributes) {
//       if (response.attributes[n].type.toLowerCase() == "integer" || response.attributes[n].type.toLowerCase() == "double") {
//         let allname = {};
//         allname["label"] = response['name'] + "." + response.attributes[n]['name'];
//         allname["value"] = {};
//         allname["value"]["label"] = response['name'] + "." + response.attributes[n]['name'];
//         allname["value"]["uuid"] = response['uuid'];
//         allname["value"]["u_Id"] = response['uuid'] + "_" + response.attributes[n]['attributeId'];
//         allname["value"]["attrId"] = response.attributes[n]['attributeId'];
//         attribute.push(allname)
//       }
//     }
//     this.labelArray = attribute
//     if (this.IsLableSelected == true) {
//       // let algorithmTemp: DependsOn = new DependsOn();
//       // algorithmTemp.label =  this.labelArray[1]["name"];
//       // algorithmTemp.uuid =  this.labelArray[1]["uuid"];
//       // algorithmTemp.version =  this.labelArray[1]["version"];
//       // this.selectedlabel=algorithmTemp;
//     }
//   }

//   getAllAttributeBySource() {
//     debugger
//     this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
//       response => { this.OnSuccesgetAllAttributeBySource(response) },
//       error => console.log('Error :: ' + this.sourcedata.uuid)
//     )
//   }
//   OnSuccesgetAllAttributeBySource(response) {
//     debugger
//     this.allSourceAttribute = []
//     let attribute = []
//     for (const n in response) {
//       let allname = {};
//       allname["id"] = response[n]['uuid'] + "_" + response[n]['attributeId'];
//       allname["itemName"] = response[n]['dname'];
//       allname["uuid"] = response[n]['uuid'];
//       allname["attrId"] = response[n]['attributeId'];
//       attribute[n] = allname
//     }
//     this.allAttribute = attribute

//   }

//   submit() {
//     this.isSubmit = "true"
//     let trainJson = {};
//     trainJson["uuid"] = this.train.uuid;
//     trainJson["name"] = this.train.name;
//     trainJson["desc"] = this.train.desc;
//     let tagArray = [];
//     if (this.train.tags != null) {
//       for (var counttag = 0; counttag < this.train.tags.length; counttag++) {
//         tagArray[counttag] = this.train.tags[counttag];
//       }
//     }
//     trainJson["tags"] = tagArray;
//     trainJson["active"] = this.train.active == true ? 'Y' : "N"
//     trainJson["published"] = this.train.published == true ? 'Y' : "N"

//   }
// }
