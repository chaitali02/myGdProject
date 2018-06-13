import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { SimulationService } from '../../metadata/services/simulation.service';

import { Version } from '../../metadata/domain/version';
import { DependsOn } from '../dependsOn';
import { AttributeHolder } from '../../metadata/domain/domain.attributeHolder'
import { ResponseOptions } from '@angular/http/src/base_response_options';
import { Simulation } from '../../metadata/domain/domain.simulation';
import { TypeaheadOptions } from 'ngx-bootstrap/typeahead/typeahead-options.class';
import { Dependson } from '../../metadata/domain/domain.dependson';
import { sourceUrl } from '@angular/compiler';

@Component({
  selector: 'app-simulation',
  templateUrl: './simulation.template.html',
})
export class SimulationComponent implements OnInit {
  msgs: any[];
  checkboxModelexecution: boolean;
  isTargetNameDisabled: boolean;
  selectTarget: any;
  allTarget: any[];
  selectTargetType: string;
  targetTypes: string[];
  allTargetAttribute: any[];
  featureMapTableArray: any[];
  featureInfo: any[];
  selectSource: DependsOn;
  allSource: any[];
  selectModel: DependsOn;
  selectSourceType: any;
  sourceTypes: any[];
  allModel: any[];
  type: { 'value': String, 'label': String }[];
  simulationtypesOption: any;
  name: any;
  version: any;
  arrayParamList: any[];
  arrayDistribution: any[];
  breadcrumbDataFrom: any;
  simulation: any;
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
  paramlist: any;
  selectParamlist: DependsOn;
  distribution: any;
  selectDistributionList: DependsOn;
  numIterations: any;

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _simulateService: SimulationService) {
    this.simulation = {};
    this.simulation["active"] = true;
    this.featureMapTableArray = [];
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    // this.sourceTypes = [{label:"datapod",value:"datapod"},
    // {label:"dataset",value:"dataset"},
    // {label:"rule",value:"rule"},
    // ]
    // this.sourceTypes = ["datapod", "dataset", "rule"]
    //this.selectSourceType = this.sourceTypes[0];
    this.targetTypes = ["datapod", "file"];
    this.selectTargetType = this.targetTypes[0];
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/simulate"
    },
    {
      "caption": "Simulation",
      "routeurl": "/app/list/simulate"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];

    this.simulationtypesOption = [
      { "value": "DEFAULT", "label": "DEFAULT" },
      { "value": "MONTECARLO", "label": "MONTECARLO" }

    ]
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
        this.getAllLatestParamlist();
        this.getAllLatestDistribution();
        //this.onChangeModel()


      }
      else {

        this.getAllLatestModel()
        this.getAllLatestParamlist()
        this.getAllLatestDistribution()
        // this.getAllLatestSource(this.selectSourceType)
        // this.getAllLatestTarget(this.selectTargetType)
        //this.getAttribute()
        //  this.onChangeModel()
      }

    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'simulate')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));

  }
  getAllLatestModel() {
    this._simulateService.getAllModelByType("N", "model")
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
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];

      temp[i] = ver;
    }
    this.allModel = temp

  }

  getAllLatestDistribution() {
    this._commonService.getAllLatest('distribution')
      .subscribe(
      response => {
        this.onSuccessgetAllLatestDistribution(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatestDistribution(response) {
    this.arrayDistribution = [];
    for (const i in response) {
      let refParam = {}
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}      
      refParam["value"]['label'] = response[i]['name'];
      refParam["value"]["uuid"] = response[i]['uuid']
      this.arrayDistribution[i] = refParam;

    }
  }


  getAllLatestParamlist() {
    this._commonService.getAllLatest('paramList')
      .subscribe(
      response => {
        this.onSuccessgetAllLatestParamList(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatestParamList(response) {
    this.arrayParamList = [];
    for (const i in response) {
      let refParam = {}
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}
      refParam["value"]["uuid"] = response[i]["uuid"]
      // refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['name'];
      this.arrayParamList[i] = refParam;

    }
  }


  // getAllLatestSource(source) {
  //   this._commonService.getAllLatest(source)
  //     .subscribe(
  //     response => {
  //       this.onSuccessgetAllLatestSource(response)
  //     },
  //     error => console.log("Error :: " + error));
  // }
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
    //this.selectTarget=this.allTarget[0]
  }


  public get value(): string {
    return
  }


  getAllVersionByUuid() {
    {
      this._commonService.getAllVersionByUuid('simulate', this.id)
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
    if (event === true) {
      this.simulation.active = 'Y';
    }
    else {
      this.simulation.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.simulation.published = 'Y';
    }
    else {
      this.simulation.published = 'N';
    }
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.simulation = response;
    this.uuid = response.uuid;
    const version: Version = new Version;
    version.label = response["version"];
    version.uuid = response["uuid"];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name;
    this.simulation.active = response["active"] == 'Y' ? true : false;
    this.simulation.published = response["published"] == 'Y' ? true : false;
    this.simulation.type = response.type
    this.simulation.numIterations = response["numIterations"]
    this.breadcrumbDataFrom[2].caption = response.name;

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    dependOnTemp.version = response["dependsOn"]["ref"]["version"];
    this.selectModel = dependOnTemp
debugger
    let dependOnParamlist: DependsOn = new DependsOn();
    dependOnParamlist.label = response["distributionTypeInfo"]["ref"]["name"];
    dependOnParamlist.uuid = response["distributionTypeInfo"]["ref"]["uuid"];
    this.selectDistributionList = dependOnParamlist;

    // let dependOnDistribution: DependsOn = new DependsOn();
    // dependOnDistribution.label = response["dependsOn"]["ref"]["name"];
    // dependOnDistribution.uuid = response["dependsOn"]["ref"]["uuid"];
    // this.selectDistributionList = dependOnDistribution;

    let selectParamlist: DependsOn = new DependsOn();
    selectParamlist.label = response["paramList"]["ref"]["name"];
    selectParamlist.uuid = response["paramList"]["ref"]["uuid"];
    this.selectParamlist = selectParamlist;

    let targetTemp: DependsOn = new DependsOn();
    targetTemp.label = response["target"]["ref"]["name"];
    targetTemp.uuid = response["target"]["ref"]["uuid"];
    this.selectTarget = targetTemp
    this.getAllLatestModel()
    this.getAllLatestTarget(this.selectTargetType)

    var features = [];
    for (var i = 0; i < response.featureInfo.length; i++) {
      var featureMap = {};
      var sourceFeature = {};
      var targetFeature = {};
      sourceFeature["featureId"] = i;
      //sourceFeature["featureName"] = response.featureInfo[i].featureName;
      // sourceFeature["uuid"] = response.featureInfo[i].ref.uuid;
      //  sourceFeature["type"] = "model";
      //   sourceFeature["type"////ourceFeature["datapodename"]=response.featureInfo[i].features.datapodename;
      //sourceFeature["desc"]=response.featureInfo[i].features.desc;
      //sourceFeature["maxVal"]=response.featureInfo[i].features.maxVal;
      //  sourceFeature["minVal"]=response.featureInfo[i].features.minVal;

      featureMap["sourceFeature"] = sourceFeature;

      //  featureMap["targetFeature"]=targetFeature;

      featureMap["sourceFeature"] = sourceFeature;
      features[i] = featureMap;
    }
    this.featureMapTableArray = features;

    this.onChangeModel();

  }
















  OnSuccesgetAllLatest(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['dname'];
      allname["value"] = {};
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
    debugger
    // simulateService.getOneByUuidandVersion(this.selectModel.uuid,this.selectModel.version,"model").then(function(response) { onSuccessGetLatestByUuid(response.data)});
    this._commonService.getOneByUuidAndVersion(this.selectModel.uuid, this.selectModel.version || " ", 'model')
      .subscribe(
      response => {
        this.onSuccessonChangeModel(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessonChangeModel(response) {


    debugger
    var featureMapTableArray = [];
    for (var i = 0; i < response.features.length; i++) {
      var featureMap = {};
      var sourceFeature = {};
     // var targetFeature = {};
      sourceFeature["featureId"] = response.features[i].featureId;
      sourceFeature["type"] = response.features[i].type;
      sourceFeature["datapodname"] = response.features[i].name;
      sourceFeature["name"] = response.features[i].name;
      sourceFeature["desc"] = response.features[i].desc;
      sourceFeature["minVal"] = response.features[i].minVal;
      sourceFeature["maxVal"] = response.features[i].maxVal;
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
    this.router.navigate(['app/list/simulate']);

  }

  generateRadomValue() {
    this.simulation.seed = Math.floor(1000 + Math.random() * 9000);
    this.simulation.numIterations = 1000;
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/simulation', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataScience/simulation', uuid, version, 'true']);
  }
  submit() {
    debugger
    var simulateJson = {}
    simulateJson["uuid"] = this.simulation.uuid
    simulateJson["name"] = this.simulation.name
    simulateJson["desc"] = this.simulation.desc
    simulateJson["active"] = this.simulation.active == true ? 'Y' : "N";
    simulateJson["published"] = this.simulation.published == true ? 'Y' : "N";
    simulateJson["numIterations"] = this.simulation.numIterations;
    simulateJson["type"] = this.simulation.type;
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
    simulateJson["tags"] = tagArray;
    var dependsOn = {};
    var ref = {};
    ref["type"] = "model";
    ref["uuid"] = this.selectModel.uuid;
    dependsOn["ref"] = ref;
    simulateJson["dependsOn"] = dependsOn;



    let distribution = {};
    let refdistribution = {};
    if (this.selectDistributionList != null) {
      refdistribution["uuid"] = this.selectDistributionList.uuid;
      refdistribution["type"] = "distribution";
      // refParam["name"] = this.paramList.name;
      distribution["ref"] = refdistribution;
    }
    simulateJson["distributionTypeInfo"] = distribution;
    let paramlist = {};
    let refParam = {};
    if (this.selectParamlist != null) {
      refParam["uuid"] = this.selectParamlist.uuid;
      refParam["type"] = "paramlist";
      paramlist["ref"] = refParam;
    }
    simulateJson["paramList"] = paramlist;
    var target = {};
    var targetref = {};
    targetref["type"] = this.selectTargetType;
    if (this.selectTargetType == "datapod")
      targetref["uuid"] = this.selectTarget.uuid;
    target["ref"] = targetref;
    simulateJson["target"] = target;
    var featureMap = [];
    if (this.featureMapTableArray.length > 0) {
      for (var i = 0; i < this.featureMapTableArray.length; i++) {
        var featureInfoObj = {};
        var featureInfoRef = {}
        featureInfoObj["featureId"] = this.featureMapTableArray[i].sourceFeature.featureId;
        featureInfoObj["featureName"] = this.featureMapTableArray[i].sourceFeature.featureName;
        // featureInfoObj ["featureType"] = this.featureMapTableArray[i].sourceFeature.type;
        // featureInfoObj["featureDesc"] = this.featureMapTableArray[i].sourceFeature.desc;
        // featureInfoObj["featureminVal"] = this.featureMapTableArray[i].sourceFeature.minVal;
        // featureInfoObj["featuremaxVal"] =this.featureMapTableArray[i].sourceFeature.maxVal
        featureInfoRef["uuid"] = this.selectModel.uuid;
        featureInfoRef["type"] = 'model';
        featureInfoObj["ref"] = featureInfoRef;
        featureMap[i] = featureInfoObj;
      }
    }
    simulateJson["featureInfo"] = featureMap;
    console.log(JSON.stringify(simulateJson))
    this._commonService.submit("simulate", simulateJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("simulate", response).subscribe(
        response => { this.OnSucessGetOneById(response); },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmit = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'simulate Save Successfully' });
      setTimeout(() => {
        this.goBack();

      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "simulate", "execute").subscribe(
      response => {
        this.showMassage('simulate Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()
        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }
  showMassage(msg, msgtype, msgsumary) {
    this.isSubmit = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }
}



