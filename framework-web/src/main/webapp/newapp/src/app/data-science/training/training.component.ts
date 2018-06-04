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
import { DependOnExp } from '../../metadata/domain/domain.dependOnExp';

@Component({
  selector: 'app-training  ',
  templateUrl: './training.template.html',
})
export class TrainingComponent implements OnInit {
  msgs: any[];
  checkboxModelexecution: boolean;
  isTargetNameDisabled: boolean;
  selectTarget: any;
  allTarget: any[];
  selectTargetType: string;
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
  train: any;
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
  trainPercent: any;
  valPercent: any;


  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _trainingService: TrainingService) {
    this.train = {};
    this.train["active"] = true;
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.sourceTypes = ["datapod", "dataset", "rule"]
    this.selectSourceType = this.sourceTypes[0];
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
        this.getAllLatestModel();



      }
      else {
        this.getAllLatestModel()
        this.getAllLatestSource(this.selectSourceType)
        this.getAllLatestTarget(this.selectTargetType)

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
  getAllLatestModel() {

    this._trainingService.getAllModelByType("N", "model")
      .subscribe(
      response => {
        this.onSuccessgetAllLatestModel(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatestModel(response) {
    console.log(response)
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['label'];
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      temp[i] = ver;
    }
    this.allModel = temp
  }

  getAllLatestSource(source) {

    this._commonService.getAllLatest(source)
      .subscribe(
      response => {
        this.onSuccessgetAllLatestSource(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllLatestTarget(target) {
    this._commonService.getAllLatest(target)
      .subscribe(
      response => {
        this.onSuccessgetAllLatestTarget(response)
      },
      error => console.log("Error :: " + error));
  }


  onSuccessgetAllLatestTarget(response) {
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
  }
  onChangeSourceType() {

    this.getAllLatestSource(this.selectSourceType)
  }


  onSuccessgetAllLatestSource(response) {

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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'train')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }


  onChangeActive(event) {
    if (event === true) {
      this.train.active = 'Y';
    }
    else {
      this.train.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
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
    this.trainPercent = response.trainPercent;
    this.valPercent = response.valPercent;
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    dependOnTemp.version = response["dependsOn"]["ref"]["version"];
    this.selectModel = dependOnTemp
    this.selectSourceType = response["source"]["ref"]["type"];
    let sourceTemp: DependsOn = new DependsOn();
    sourceTemp.label = response["source"]["ref"]["name"];
    sourceTemp.uuid = response["source"]["ref"]["uuid"];
    this.selectSource = sourceTemp
    this.getAllLatestModel()
    this.getAllLatestSource(this.selectSourceType)
    this.getAllLatestTarget(this.selectTargetType)
    this.getAttribute()
    var featureMapTableArray = [];
    for (var i = 0; i < response.featureAttrMap.length; i++) {
      var featureMap = {};
      var sourceFeature = {};
      var targetFeature = {};
      featureMap["featureMapId"] = response.featureAttrMap[i].featureMapId;
      sourceFeature["uuid"] = response.featureAttrMap[i].feature.ref.uuid;
      sourceFeature["type"] = response.featureAttrMap[i].feature.ref.type;
      sourceFeature["featureId"] = response.featureAttrMap[i].feature.featureId;
      sourceFeature["featureName"] = response.featureAttrMap[i].feature.featureName;
      featureMap["sourceFeature"] = sourceFeature;
      targetFeature["uuid"] = response.featureAttrMap[i].attribute.ref.uuid;
      targetFeature["type"] = response.featureAttrMap[i].attribute.ref.type;
      targetFeature["datapodname"] = response.featureAttrMap[i].attribute.ref.name;
      targetFeature["name"] = response.featureAttrMap[i].attribute.attrName;
      targetFeature["attributeId"] = response.featureAttrMap[i].attribute.attrId;
      targetFeature["id"] = response.featureAttrMap[i].attribute.ref.uuid + "_" + response.featureAttrMap[i].attribute.attrId;
      targetFeature["dname"] = response.featureAttrMap[i].attribute.ref.name + "." + response.featureAttrMap[i].attribute.attrName;
      featureMap["targetFeature"] = targetFeature;
      featureMapTableArray[i] = featureMap;
    }
    this.featureMapTableArray = featureMapTableArray;
  }

  onChangeSource() {

    this.getAttribute()
  }
  getAttribute() {
    this._commonService.getAllAttributeBySource(this.selectSource.uuid, this.selectSourceType).subscribe(
      response => {
        this.OnSuccesgetAllLatest(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response) {

    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['dname'];
      allname["value"] = {};
      allname["value"]["id"] = response[n]['uuid'];
      allname["value"]["label"] = response[n]['dname'];
      allname["value"]["id"] = response[n]['id'];
      temp[n] = allname;
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

  onChangeModel() {

    this._commonService.getOneByUuidAndVersion(this.selectModel.uuid, this.selectModel.version, 'model')
      .subscribe(
      response => {
        this.onSuccessonChangeModel(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessonChangeModel(response) {

    var featureMapTableArray = [];
    for (var i = 0; i < response.features.length; i++) {
      var featureMap = {};
      var sourceFeature = {};
      var targetFeature = {};
      featureMap["featureMapId"] = i;
      sourceFeature["uuid"] = response.uuid;
      sourceFeature["type"] = "model";
      sourceFeature["label"] = response.label
      sourceFeature["featureId"] = response.features[i].featureId;
      sourceFeature["featureName"] = response.features[i].name;
      featureMap["sourceFeature"] = sourceFeature;
      featureMapTableArray[i] = featureMap;
    }
    this.featureMapTableArray = featureMapTableArray;
  }

  onChangeTargeType() {
    if (this.selectTargetType == 'datapod') {
      this.isTargetNameDisabled = false;
      this.getAllLatestTarget(this.selectTargetType);

    } else {
      this.isTargetNameDisabled = true;
      this.allTarget = [];
    }
  }



  public goBack() {
    //this._location.back();
    this.router.navigate(['/app/list/train']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/train', uuid, version, 'false']);
  }


  showview(uuid, version) {
    this.router.navigate(['app/dataScience/train', uuid, version, 'true']);
  }


  onChangeTrainPercent() {
    this.train.valPercent = (100 - this.train.trainPercent);
  }

  onChangeValPercent() {

    this.train.trainPercent = (100 - this.train.valPercent);
  }

  submit() {

    var trainJson = {}
    trainJson["uuid"] = this.train.uuid
    trainJson["name"] = this.train.name
    trainJson["desc"] = this.train.desc
    trainJson["active"] = this.train.active == true ? 'Y' : "N";
    trainJson["published"] = this.train.published == true ? 'Y' : "N";
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
    trainJson["valPercent"] = this.train.valPercent;
    trainJson["trainPercent"] = this.train.trainPercent;
    var dependsOn = {};
    var ref = {};
    ref["type"] = "model";
    ref["uuid"] = this.selectModel.uuid;
    dependsOn["ref"] = ref;
    trainJson["dependsOn"] = dependsOn;
    var source = {};
    var sourceref = {};
    sourceref["type"] = this.selectSourceType;
    sourceref["uuid"] = this.selectSource.uuid;
    source["ref"] = sourceref;
    trainJson["source"] = source;
    // var target={};
    // var targetref={};
    // targetref["type"]=this.selectTargetType;
    // if(this.selectTargetType =="datapod")
    // targetref["uuid"]=this.selectTarget.uuid;
    // target["ref"]=targetref;
    // trainJson["target"]=target;
    var featureMap = [];
    if (this.featureMapTableArray.length > 0) {
      for (var i = 0; i < this.featureMapTableArray.length; i++) {
        var featureMapObj = {};
        featureMapObj["featureMapId"] = i;
        var sourceFeature = {};
        var sourceFeatureRef = {};
        var targetFeature = {};
        var targetFeatureRef = {};
        sourceFeatureRef["uuid"] = this.featureMapTableArray[i].sourceFeature.uuid;
        sourceFeatureRef["type"] = this.featureMapTableArray[i].sourceFeature.type;
        sourceFeature["ref"] = sourceFeatureRef;
        //sourceFeature.attrId = this.featureMapTableArray[i].sourceFeature.attributeId;
        sourceFeature["featureId"] = this.featureMapTableArray[i].sourceFeature.featureId;
        sourceFeature["featureName"] = this.featureMapTableArray[i].sourceFeature.featureName;
        featureMapObj["feature"] = sourceFeature;

        let uuid = this.featureMapTableArray[i].targetFeature.id.split("_")[0]
        var attrid = this.featureMapTableArray[i].targetFeature.id.split("_")[1]
        targetFeatureRef["uuid"] = uuid;
        targetFeatureRef["type"] = this.selectSourceType;
        targetFeature["ref"] = targetFeatureRef
        targetFeature["attrId"] = attrid;

        featureMapObj["attribute"] = targetFeature;
        featureMap[i] = featureMapObj;
      }
    }
    trainJson["featureAttrMap"] = featureMap;

    console.log(JSON.stringify(trainJson))
    this._commonService.submit("train  ", trainJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }



  OnSuccessubmit(response) {
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Train Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);

  }
  30



}
