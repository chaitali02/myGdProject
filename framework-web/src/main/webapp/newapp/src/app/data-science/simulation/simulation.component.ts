import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { SimulationService } from '../../metadata/services/simulation.service';
import { CommonListService } from '../../common-list/common-list.service';

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

  attributeInfoTag: any[];
  allAttributeinto: any;
  allDistribution: any[];
  allRule: any[];
  allDataset: any[];
  allDatapod: any;
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
  displayDialogBox: boolean = false;
  paramListHolder: any;
  attributeTypes: any;
  paramListHolderArray: any[];

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _simulateService: SimulationService, private _commonListService: CommonListService) {
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
    this.attributeTypes = [{ 'label': 'datapod', 'value': 'datapod' },
    { 'label': 'dataset', 'value': 'dataset' },
    { 'label': 'rule', 'value': 'rule' }];

    this.selectTargetType = this.targetTypes[0];
    this.checkboxModelexecution = false;
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
      //this.getAllLatest();
    })
  }

  onChangeAtrributes(index) {
    this._commonService.getAllAttributeBySource(this.paramListHolder[index].paramValue.uuid, "datapod").subscribe(
      response => { this.onSuccessgetAttributesByDatapod(response, index) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAttributesByDatapod(response, index) {
    let allAttributeinto = [];
    for (const i in response) {
      let attributeInfoTagObj = {};
      attributeInfoTagObj["label"] = response[i].name;
      attributeInfoTagObj["value"] = {};
      attributeInfoTagObj["value"]["label"] = response[i].name;
      attributeInfoTagObj["value"]["uuid"] = response[i].uuid;
      attributeInfoTagObj["value"]["datapodname"] = response[i].datapodname;
      attributeInfoTagObj["value"]["attributeId"] = response[i].attributeId;
      allAttributeinto[i] = attributeInfoTagObj;
      //make same arrays of attribute n attributes or make array name differnet
    }
    this.paramListHolder[index]["allattributeInfoTag"] = allAttributeinto;
    console.log(JSON.stringify(this.paramListHolder[index]["allattributeInfoTag"]));

  }

  getAllAttribute(index) {
    if (this.paramListHolder[index].paramValue !== null || this.paramListHolder[index].paramType == "attribute") {

      this._commonService.getAllAttributeBySource(this.paramListHolder[index].paramValue.uuid, "datapod").subscribe(
        response => { this.onSuccessgetAllAttributeBySource(response, index) },
        error => console.log("Error :: " + error));
    }
  }

  onSuccessgetAllAttributeBySource(response, index) {
    let allAttributeinto1 = [];
    for (const i in response) {
      let allAttributeintoObj = {};
      allAttributeintoObj["label"] = response[i].dname;
      allAttributeintoObj["value"] = {};
      allAttributeintoObj["value"]["label"] = response[i].dname;
      allAttributeintoObj["value"]["uuid"] = response[i].uuid;
      allAttributeintoObj["value"]["name"] = response[i].name;
      allAttributeintoObj["value"]["attributeId"] = response[i].attributeId;
      allAttributeintoObj["value"]["datapodname"] = response[i].datapodname;
      // allAttributeintoObj["value"]["attributeId"] = response[i].attributeId;


      allAttributeinto1[i] = allAttributeintoObj;
    }
    this.paramListHolder[index]["allAttributeinto"] = allAttributeinto1;
  }

  getAllLatest(type, index) {
    this._commonService.getAllLatest(type || "dataset").subscribe(
      response => { this.onSucceessgetAllLatest(response, type, index) },
      error => console.log("Error :: " + error));
  }

  onSucceessgetAllLatest(response, type, index) {
    // let temp1 = [];
    // let temp2=[];
    if (type == "datapod") {
      this.allDatapod = [];
      for (const i in response) {
        let allDatapodObj = {};
        allDatapodObj["label"] = response[i].name;
        allDatapodObj["value"] = {};
        allDatapodObj["value"]["label"] = response[i].name;
        allDatapodObj["value"]["uuid"] = response[i].uuid;
        this.allDatapod[i] = allDatapodObj;
      }
      //this.getAllAttribute(type,index);
    }
    //this.allDatapod=temp1;

    if (type == "dataset") {
      this.allDataset = [];
      for (const i in response) {
        let allDatasetObj = {};
        allDatasetObj["label"] = response[i].name;
        allDatasetObj["value"] = {};
        allDatasetObj["value"]["label"] = response[i].name;
        allDatasetObj["value"]["uuid"] = response[i].uuid;
        this.allDataset[i] = allDatasetObj;
      }
    }
    //this.allDataset=temp2;

    if (type == "rule") {
      this.allRule = [];
      for (const i in response) {
        let allRuleObj = {};
        allRuleObj["label"] = response[i].name;
        allRuleObj["value"] = {};
        allRuleObj["value"]["label"] = response[i].name;
        allRuleObj["value"]["uuid"] = response[i].uuid;
        this.allRule[i] = allRuleObj;
      }
    }
    if (type == "distribution") {
      this.allDistribution = [];
      for (const i in response) {
        let allDistributionObj = {};
        allDistributionObj["label"] = response[i].name;
        allDistributionObj["value"] = {};
        allDistributionObj["value"]["label"] = response[i].name;
        allDistributionObj["value"]["uuid"] = response[i].uuid;
        this.allDistribution[i] = allDistributionObj;
      }
    }
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'simulate')
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
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.tags = tags;
    }//End If
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
    this.getAllLatestTarget(this.selectTargetType)
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
    this._commonService.getOneByUuidAndVersion(this.selectModel.uuid, this.selectModel.version || " ", 'model')
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


  onChangeRunImmediately() {
    this._simulateService.getParamListByDistribution(this.selectDistributionList.uuid).subscribe(
      response => { this.onSuccessgetParamListByDistribution(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessgetParamListByDistribution(response) {
    this.displayDialogBox = true;
    this.paramListHolder = response;
    this.getParamByParamList();
  }

  getParamByParamList() {
    this._simulateService.getParamByParamList(this.selectParamlist.uuid).subscribe(
      response => { this.onSuccessgetParamByParamList(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessgetParamByParamList(response) {
    let paramList = this.paramListHolder.concat(response);
    this.paramListHolder = paramList;
    console.log("paramListHolder is" + JSON.stringify(this.paramListHolder));

    let paramListHolder1 = [];
    let type = ["ONEDARRAY", "TWODARRAY"];
    var type1 = ['distribution', 'attribute', 'attributes', 'datapod', 'list'];
    if (this.paramListHolder.length > 0) {
      for (var i = 0; i < this.paramListHolder.length; i++) {
        let paramList = {};
        paramList["uuid"] = this.paramListHolder[i].ref.uuid;
        paramList["type"] = this.paramListHolder[i].ref.type;
        paramList["paramId"] = this.paramListHolder[i].paramId;
        paramList["paramType"] = this.paramListHolder[i].paramType.toLowerCase();
        paramList["paramName"] = this.paramListHolder[i].paramName;
        paramList["ref"] = this.paramListHolder[i].ref;
        paramList["attributeInfo"];
        paramList["allAttributeinto"] = [];
        paramList["attributeInfoTag"] = [];
        if (type1.indexOf(this.paramListHolder[i].paramType) == -1) {
          paramList["isParamType"] = "simple";
          paramList["paramValue"] = this.paramListHolder[i].paramValue.value;
          paramList["selectedParamValueType"] = 'simple'
        } else if (type1.indexOf(this.paramListHolder[i].paramType) != -1) {
          paramList["isParamType"] = this.paramListHolder[i].paramType;
          paramList["selectedParamValueType"] = this.paramListHolder[i].paramType == "distribution" ? this.paramListHolder[i].paramType : "datapod";
          paramList["paramValue"] = this.paramListHolder[i].paramValue;
          if (this.paramListHolder[i].paramValue != null && this.paramListHolder[i].paramValue !== 'list') {
            var selectedParamValue = {};
            selectedParamValue["uuid"] = this.paramListHolder[i].paramValue.ref.uuid;
            selectedParamValue["type"] = this.paramListHolder[i].paramValue.ref.type;
            paramList["selectedParamValue"] = selectedParamValue;
          }
          if (this.paramListHolder[i].paramValue && this.paramListHolder[i].paramType == 'list') {
            paramList["selectedParamValueType"] = "list";
            var listvalues = this.paramListHolder[i].paramValue.value.split(',');
            var selectedParamValue = {};
            selectedParamValue["type"] = this.paramListHolder[i].paramValue.ref.type;
            selectedParamValue["value"] = listvalues[0];
            paramList["paramValue"] = selectedParamValue;
            paramList["selectedParamValue"] = selectedParamValue;
            for (const i in listvalues) {
              var listvalues1 = {};
              listvalues1["label"] = listvalues[i];
              listvalues1["value"] = listvalues[i];
              listvalues[i] = listvalues1;
            }
            paramList["allListInfo"] = listvalues;
          }
        } else {
          paramList["isParamType"] = "datapod";
          paramList["selectedParamValueType"] = 'datapod'
          paramList["paramValue"] = this.paramListHolder[i].paramValue;
        }

        paramListHolder1[i] = paramList;

      }
      this.paramListHolder = [];
      this.paramListHolder = paramListHolder1;
    }
  }

  executeWithExecParamList() {
    this.displayDialogBox = false;
  }

  submit() {
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
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    simulateJson['tags'] = tagArray
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
    targetref["uuid"] = this.selectTarget.uuid;
    if (this.selectTargetType == "datapod")
      // targetref["uuid"] = this.selectTarget.uuid;
      target["ref"] = targetref;
    simulateJson["target"] = target;
    var featureMap = [];
    if (this.featureMapTableArray.length > 0) {
      for (var i = 0; i < this.featureMapTableArray.length; i++) {
        var featureInfoObj = {};
        var featureInfoRef = {}

        featureInfoObj["featureId"] = this.featureMapTableArray[i].sourceFeature.featureId;
        featureInfoObj["featureName"] = this.featureMapTableArray[i].sourceFeature.featureName;
        featureInfoRef["type"] = 'model';
        featureInfoRef["uuid"] = this.selectModel.uuid;
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
        response => { this.onSuccessGetOneById(response) },
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
  onSuccessGetOneById(response) {
    let execParams = {};
    let paramListInfo = [];
    if (this.paramListHolder.length > 0) {
      for (let i = 0; i < this.paramListHolder.length; i++) {
        let paramList = {};
        paramList["paramId"] = this.paramListHolder[i].paramId;
        paramList["paramName"] = this.paramListHolder[i].paramName;
        paramList["paramType"] = this.paramListHolder[i].paramType;
        paramList["ref"] = this.paramListHolder[i].ref;
        if (this.paramListHolder[i].paramType == 'attribute') {
          let attributeInfoArray = [];
          let attributeInfo = {};
          let attributeInfoRef = {}
          attributeInfoRef["type"] = this.paramListHolder[i].selectedParamValueType;
          attributeInfoRef["uuid"] = this.paramListHolder[i].attributeInfo.uuid;
          attributeInfoRef["name"] = this.paramListHolder[i].attributeInfo.name
          attributeInfo["ref"] = attributeInfoRef;
          attributeInfo["attrId"] = this.paramListHolder[i].attributeInfo.attributeId;
          attributeInfoArray[0] = attributeInfo
          paramList["attributeInfo"] = attributeInfoArray;
        }
        if (this.paramListHolder[i].paramType == 'attributes') {
          let attributeInfoArray = [];
          for (let j = 0; j < this.paramListHolder[i].attributeInfoTag.length; j++) {
            let attributeInfo = {};
            let attributeInfoRef = {}
            attributeInfoRef["type"] = this.paramListHolder[i].selectedParamValueType;
            attributeInfoRef["uuid"] = this.paramListHolder[i].attributeInfoTag[j].uuid;
            attributeInfoRef["name"] = this.paramListHolder[i].attributeInfoTag[j].datapodname;
            attributeInfo["ref"] = attributeInfoRef;
            attributeInfo["attrId"] = this.paramListHolder[i].attributeInfoTag[j].attributeId;
            attributeInfo["attrName"] = this.paramListHolder[i].attributeInfoTag[j].label;
            attributeInfoArray[j] = attributeInfo
          }
          paramList["attributeInfo"] = attributeInfoArray;
        }
        else if (this.paramListHolder[i].paramType == 'distribution' || this.paramListHolder[i].paramType == 'datapod') {
          let ref = {};
          let paramValue = {};
          ref["type"] = this.paramListHolder[i].selectedParamValueType;
          // if(this.paramListHolder[i].selectedParamValue !== null){
          // ref["uuid"] = this.paramListHolder[i].selectedParamValue.uuid;
          // }
          paramValue["ref"] = ref;
          paramList["paramValue"] = paramValue;
        }
        else if (this.paramListHolder[i].selectedParamValueType == "simple") {
          let ref = {};
          let paramValue = {};
          ref["type"] = this.paramListHolder[i].selectedParamValueType;
          paramValue["ref"] = ref;
          paramValue["value"] = this.paramListHolder[i].paramValue
          paramList["paramValue"] = paramValue;
        }
        else if (this.paramListHolder[i].selectedParamValueType == "list") {
          let ref = {};
          let paramValue = {};
          ref["type"] = 'simple';
          paramValue["ref"] = ref;
          paramValue["value"] = this.paramListHolder[i].paramValue
          paramList["paramValue"] = paramValue;
        }
        paramListInfo[i] = paramList;
      }
      execParams["paramListInfo"] = paramListInfo;
    }
    else {
      execParams = null;
    }
    console.log(JSON.stringify(execParams));

    this._commonListService.executeWithParams(response.uuid, response.version, "simulate", "view", execParams).subscribe(
      response => { this.onSuccessExecute(response) },
      error => { this.onError(error) }
    )
  }

  onSuccessExecute(response) {
    console.log(JSON.stringify(response));
    this.isSubmit = "false";
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'simulate Save Successfully' });
    setTimeout(() => {
      this.goBack();
    }, 1000);
  }

  onError(error) {
    console.log('Error :: ' + error);
    this.isSubmit = "false";
    this.msgs = [];
    this.msgs.push({ severity: 'Failed', summary: 'Error Message', detail: 'simulate execution failed' });
    setTimeout(() => {
      this.goBack();
    }, 1000);
  }
}

