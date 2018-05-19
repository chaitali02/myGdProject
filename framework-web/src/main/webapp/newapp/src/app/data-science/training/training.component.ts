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

@Component({
  selector: 'app-train',
  templateUrl: './training.template.html',
})
export class TrainingComponent implements OnInit {
  IsLableSelected: boolean;
  selectallattribute: any;
  isTabelShow: boolean;
  paramtable: any[];
  paramtablecol: any;
  paramsetdata: any;
  isShowExecutionparam: boolean;
  allParameterset: any;
  sourceAlgorithm: any;
  checkboxtrainexecution: boolean;
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
  breadcrumbDataFrom: any;
  showtrain: any;
  train: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  depends: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any[];
  arrayModel: any;
  model: DependsOn;
  source: string;
  allSourceAttribute: any[];
  featuresArray: any[];
  featuresTags: any[];
  featureResponse: any;
  nameResponse: any;
  labelTags: any;
  labelResponse: any;
  allSourceLabel: any;
  labelArray: any;
  selectmodel:DependsOn;
  featureMap :any;
  sourceFeature:any;
  featureMapTableArray :any[];
  modelData :any[];
  selectedRunImmediately :any

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _trainService: TrainingService) {
    //this.showtrain = true;
    this.train = {};
    this.train["active"] = true;
    this.continueCount = 1;


    this.isSubmit = "false";
    // this.IsLableSelected=false
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };
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
    this.sources = ["datapod", "dataset", "rule"];
    this.source = this.sources[0];

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
      this.getAllLatest(true);
      this.getAllModelByType()
    })
  }

  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }
  public goBack() {
    this._location.back();
  }
  changeSoruce() {
    this.featuresTags = null;
    this.getAllAttributeBySource();
    this.getAllAttributeBySourceLabel();
  }
  SourceType() {
    this.featuresTags = null;
    this.getAllLatest(true);
  }
  onChangeModel(){
    debugger
    this._commonService.getAllAttributeBySource(this.selectmodel.uuid,'model').subscribe(
      response => { this.OnSuccesgetAllAttributeByDataPodSourceAdd(response)},
      error => console.log('Error :: ' + error)
    )

  }


  getAllVersionByUuid() {
    debugger
    this._commonService.getAllVersionByUuid('train', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    debugger
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["u_Id"] = response[i]['uuid'] + "_" + response[i]['version']
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  getOneByUuidAndVersion() {
    debugger
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'train')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    debugger
    this.train = response;
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name
    this.train.published = response["published"] == 'Y' ? true : false
    this.train.active = response["active"] == 'Y' ? true : false
    this.breadcrumbDataFrom[2].caption = this.train.name
    let dependOnTemp: DependsOn = new DependsOn()
    dependOnTemp.label = response["model"]["ref"]["name"]
    dependOnTemp.uuid = response["model"]["ref"]["uuid"]
    this.model = dependOnTemp;

    console.log('Data is' + response)



  }



  GetLatestByUuid(){
this._commonService.getLatestByUuid(this.uuid,'model')
.subscribe(
  response=>{
    this.onSucessgetLatestByUuid(response)
  },
  error => console.log("Error :: " + error))

  }

  onSucessgetLatestByUuid(response){
 
    this.modelData = response;
    if (this.selectmodel) {
      this.selectedRunImmediately = "NO";
      this.isShowExecutionparam = false;
      var featureMapTableArray = [];
      for (var i = 0; i < response.features.length; i++) {
        var featureMap = {};
        var sourceFeature = {};
        var targetFeature = {};
        this.featureMap.featureMapId = i;
        this.sourceFeature.uuid = response.uuid;
        this.sourceFeature.type = "model";
        this.sourceFeature.featureId = response.features[i].featureId;
        this.sourceFeature.featureName = response.features[i].name;
        this.featureMap.sourceFeature = sourceFeature;
        this.featureMapTableArray[i] = featureMap;
        this.featureMapTableArray = featureMapTableArray;
      }
    }  
  }


  getAllModelByType() {
    debugger
    this._trainService.getAllModelByType('N', 'model')
      .subscribe(
      response => {
        this.onSuccessgetAllLatestModel(response)
      },
      error => console.log("Error :: " + error))
  }


OnSuccesgetAllAttributeByDataPodSourceAdd(response){
  debugger
  this.model=response;
 
 // this.selectedRunImmediately = "NO";
 // this.isShowExecutionparam = false;
  var featureMapTableArray = [];
  for (var i = 0; i < response.features.length; i++) {
    var featureMap = {};
    var sourceFeature = {};
    var targetFeature = {};
    this.featureMap.featureMapId = i;
    this.sourceFeature.uuid = response.uuid;
    this.sourceFeature.type = "model";
    this.sourceFeature.featureId = response.features[i].featureId;
   this. sourceFeature.featureName = response.features[i].name;
    this.featureMap.sourceFeature = sourceFeature;
    featureMapTableArray[i] = featureMap;
    this.featureMapTableArray = featureMapTableArray;
  
}


  }
  onSuccessgetAllLatestModel(response) {
    debugger

    this.arrayModel = [];
    for (const i in response) {
      let refParam = {}
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}
      refParam["value"]["uuid"] = response[i]["uuid"]
      // refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['name'];
      this.arrayModel[i] = refParam;

    }
  }

  getAllLatest(IsDefault) {
    debugger
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response, IsDefault) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response1, IsDefault) {
    debugger
    let temp = []
    if (this.mode == undefined || IsDefault == true) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0]["name"];
      dependOnTemp.uuid = response1[0]["uuid"];
      this.sourcedata = dependOnTemp
    }
    for (const n in response1) {
      let allname = {};
      allname["label"] = response1[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response1[n]['name'];
      allname["value"]["uuid"] = response1[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource();
    this.getAllAttributeBySourceLabel();

  }
  getAllAttributeBySourceLabel() {
    debugger
    this._commonService.getLatestByUuid(this.sourcedata.uuid, this.source)
      .subscribe(
      response => {
        this.onSuccesgetAllAttributeBySourceLabel(response)
      },
      error => console.log("Error :: " + error));

  }
  onSuccesgetAllAttributeBySourceLabel(response) {
    debugger
    let attribute = [];
    let allname = {};
    allname["label"] = '-select-'
    allname["value"] = null;
    attribute.push(allname);
    for (const n in response.attributes) {
      if (response.attributes[n].type.toLowerCase() == "integer" || response.attributes[n].type.toLowerCase() == "double") {
        let allname = {};
        allname["label"] = response['name'] + "." + response.attributes[n]['name'];
        allname["value"] = {};
        allname["value"]["label"] = response['name'] + "." + response.attributes[n]['name'];
        allname["value"]["uuid"] = response['uuid'];
        allname["value"]["u_Id"] = response['uuid'] + "_" + response.attributes[n]['attributeId'];
        allname["value"]["attrId"] = response.attributes[n]['attributeId'];
        attribute.push(allname)
      }
    }
    this.labelArray = attribute
    if (this.IsLableSelected == true) {
      // let algorithmTemp: DependsOn = new DependsOn();
      // algorithmTemp.label =  this.labelArray[1]["name"];
      // algorithmTemp.uuid =  this.labelArray[1]["uuid"];
      // algorithmTemp.version =  this.labelArray[1]["version"];
      // this.selectedlabel=algorithmTemp;
    }
  }

  getAllAttributeBySource() {
    debugger
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + this.sourcedata.uuid)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    debugger
    this.allSourceAttribute = []
    let attribute = []
    for (const n in response) {
      let allname = {};
      allname["id"] = response[n]['uuid'] + "_" + response[n]['attributeId'];
      allname["itemName"] = response[n]['dname'];
      allname["uuid"] = response[n]['uuid'];
      allname["attrId"] = response[n]['attributeId'];
      attribute[n] = allname
    }
    this.allAttribute = attribute

  }

  submit() {
    this.isSubmit = "true"
    let trainJson = {};
    trainJson["uuid"] = this.train.uuid;
    trainJson["name"] = this.train.name;
    trainJson["desc"] = this.train.desc;
    let tagArray = [];
    if (this.train.tags != null) {
      for (var counttag = 0; counttag < this.train.tags.length; counttag++) {
        tagArray[counttag] = this.train.tags[counttag];
      }
    }
    trainJson["tags"] = tagArray;
    trainJson["active"] = this.train.active == true ? 'Y' : "N"
    trainJson["published"] = this.train.published == true ? 'Y' : "N"

  }
}
