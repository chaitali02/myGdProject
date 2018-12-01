import { CommonService } from './../../metadata/services/common.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Component, OnInit, ViewEncapsulation, Inject } from '@angular/core';
import { Location } from '@angular/common';
import { DatasetService } from '../../metadata/services/dataset.service';
import { NgOption } from '@ng-select/ng-select';
import { Version } from '../../shared/version'
import { SelectItem } from 'primeng/primeng';
import { DependsOn } from './dependsOn'
import { AppMetadata } from '../../app.metadata';
import { SESSION_STORAGE, WebStorageService } from 'angular-webstorage-service'
@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.template.html',
  styleUrls: ['./dataset.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DatasetComponent implements OnInit {
  dialogAttriNameArray: any[];
  dialogSelectName: any;
  dialogAttributeName : any;
  dialogAttriArray: any[];
  displayDialogBox : boolean;
  sessionData: any[];
  lhsFormulaArray: any;
  rhsFormulaArray: any[];
  attributesArray: any[];
  rhsTypeArray: { value: string; label: string }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  columnOptions: any[];
  cols: any[];
  colsdata: any;
  IsError: boolean;
  IsTableShow: boolean;
  showgraphdiv: boolean;
  graphDataStatus: boolean;
  showgraph: boolean;
  showdatapod: boolean;
  tableclass: string;
  isDataInpogress: boolean;
  isShowSimpleData: boolean;
  isDataError: boolean;
  msgs: any[];
  datasetCompare: any;
  selectRelation: string;
  selectAllAttributeRow: any;
  ruleLoadFunction: any;
  allMapExpression: any;
  allMapFormula: any;
  sourceAttributeTypes: { "value": string; "label": string; }[];
  attributeTableArray: any[];
  selectedAllFitlerRow: boolean;
  filterTableArray: any[];
  sources: { 'value': string; 'label': string; }[];
  allNames: SelectItem[] = [];
  sourcedata: any;
  source: any;
  versions: any[];
  dataset: any;
  lhsdatapodattributefilter: any;
  showDataset: boolean;
  logicalOperators: any;
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
  routerUrl: any;
  published: any;
  breadcrumbDataFrom: any;
  depends: any;
  allName: NgOption[]; data
  operators: any;
  allMapSourceAttribute: SelectItem[] = [];
  VersionList: SelectItem[] = [];
  //version: Version
  isSubmitEnable1: any;
  baseUrl: any;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _datasetService: DatasetService, private activeroute: ActivatedRoute, @Inject(SESSION_STORAGE) private storage: WebStorageService) {
    this.baseUrl = config.getBaseUrl();
    this.showdatapod = true;
    this.dataset = {};
    this.dataset["active"] = true
    this.isSubmitEnable1 = true;
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.dataset.filterTableArray = []
    this.sourcedata = {}
    this.dialogSelectName = {}
    this.operators = [
      { 'value': '<', 'label': 'LESS THAN' },
      { 'value': '>', 'label': 'GREATER THAN' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL' },
      { 'value': '=', 'label': 'EQUAL TO(=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]

    // this.dataset.uuid = '';

    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/dataset"
    },
    {
      "caption": "Dataset",
      "routeurl": "/app/list/dataset"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.sources = [
      { 'value': 'datapod', 'label': 'datapod' },
      { 'value': 'relation', 'label': 'relation' }
    ];
    this.sourceAttributeTypes = [
      { "value": "function", "label": "function" },
      { "value": "string", "label": "string" },
      { "value": "datapod", "label": "attribute" },
      { "value": "expression", "label": "expression" },
      { "value": "formula", "label": "formula" }
    ]
    this.attributeTableArray = []
    this.lhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' }
    ];
    this.rhsTypeArray = [
      { value: 'string', label: 'string' },
      { value: 'integer', label: 'integer' },
      { value: 'datapod', label: 'attribute' },
      { value: 'formula', label: 'formula' },
      { value: 'dataset', label: 'dataset' }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      // if (this.mode !== undefined) {
      //   this.getOneByUuidAndVersion(this.id, this.version);
      //   this.getAllVersionByUuid();
      // }
      this.getFromLocal(0);
    })
  }
  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dataset']);
  }
  onChangeActive(event) {
    if (event === true) {
      this.dataset.active = 'Y';
    }
    else {
      this.dataset.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.dataset.published = 'Y';
    }
    else {
      this.dataset.published = 'N';
    }
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'dataset')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('dataset', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid2() {
    this._commonService.getAllVersionByUuid('dataset', this.dataset.uuid)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.dataset = response;
    this.datasetCompare = response;
    this.dataset.uuid = response.uuid;
    this.dataset.createdBy = response.createdBy.ref.name;
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.dataset.tags = tags;
    }//End If
    this.dataset.published = response["published"] == 'Y' ? true : false
    this.dataset.active = response["active"] == 'Y' ? true : false
    // const version: Version = new Version();
    // version.label = response['version'];
    // version.uuid = response['uuid'];
    // this.dataset.version = version

    let version: DependsOn = new DependsOn();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.dataset.version = version

    console.log(JSON.stringify(this.dataset.version));

    this.breadcrumbDataFrom[2].caption = this.dataset.name;
    this.dataset.source = response["dependsOn"]["ref"]["type"]
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.dataset.sourcedata = dependOnTemp
    let filterjson = {};
    filterjson["filter"] = response;

    this._commonService.getAllLatest(this.dataset.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )

    if (response.filterInfo != null) {
      let filterInfoArray = [];
      for (let k = 0; k < response.filterInfo.length; k++) {
        let filterInfo = {};
        let lhsFilter = {};
        filterInfo["logicalOperator"] = response.filterInfo[k].logicalOperator
        filterInfo["lhsType"] = response.filterInfo[k].operand[0].ref.type;
        filterInfo["operator"] = response.filterInfo[k].operator;
        filterInfo["rhsType"] = response.filterInfo[k].operand[1].ref.type;

        if (response.filterInfo[k].operand[0].ref.type == 'formula') {
          this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
            .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
            error => console.log("Error ::", error))

          let lhsAttri1 = {}
          lhsAttri1["uuid"] = response.filterInfo[k].operand[0].ref.uuid;
          lhsAttri1["label"] = response.filterInfo[k].operand[0].ref.name;
          filterInfo["lhsAttribute"] = lhsAttri1;
        }

        else if (response.filterInfo[k].operand[0].ref.type == 'datapod') {

          this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
            error => console.log("Error ::", error))
          let lhsAttri = {}
          lhsAttri["uuid"] = response.filterInfo[k].operand[0].ref.uuid;
          lhsAttri["label"] = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
          lhsAttri["attributeId"] = response.filterInfo[k].operand[0].attributeId;
          filterInfo["lhsAttribute"] = lhsAttri;
        }

        else if (response.filterInfo[k].operand[0].ref.type == 'simple') {
          let stringValue = response.filterInfo[k].operand[0].value;
          let onlyNumbers = /^[0-9]+$/;
          let result = onlyNumbers.test(stringValue);
          if (result == true) {
            filterInfo["lhsType"] = 'integer';
          } else {
            filterInfo["lhsType"] = 'string';
          }
          filterInfo["lhsAttribute"] = response.filterInfo[k].operand[0].value;
        }

        if (response.filterInfo[k].operand[1].ref.type == 'formula') {
          this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
            .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
            error => console.log("Error ::", error))
          //filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].ref.name;
          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.filterInfo[k].operand[1].ref.name;
          filterInfo["rhsAttribute"] = rhsAttri;
        }

        else if (response.filterInfo[k].operand[1].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
            error => console.log("Error ::", error))

          let rhsAttri1 = {}
          rhsAttri1["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri1["label"] = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
          rhsAttri1["attributeId"] = response.filterInfo[k].operand[1].attributeId;
          filterInfo["rhsAttribute"] = rhsAttri1;
        }

        else if (response.filterInfo[k].operand[1].ref.type == 'simple') {
          let stringValue = response.filterInfo[k].operand[1].value;
          let onlyNumbers = /^[0-9]+$/;
          let result = onlyNumbers.test(stringValue);
          if (result == true) {
            filterInfo["rhsType"] = 'integer';
          } else {
            filterInfo["rhsType"] = 'string';
          }
          filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].value;

          let result2 = stringValue.includes("and")
          if (result2 == true) {
            filterInfo["rhsType"] = 'integer';

            let betweenValArray = []
            betweenValArray = stringValue.split("and");
            filterInfo["rhsAttribute1"] = betweenValArray[0];
            filterInfo["rhsAttribute2"] = betweenValArray[1];
          }
        }
        filterInfoArray.push(filterInfo);
        filterjson["filterInfo"] = filterInfoArray
        this.dataset.filterTableArray = filterInfoArray
      }
    }



    let attributeJson = {};
    attributeJson["attributeData"] = response;

    if (response.attributeInfo != null) {
      let attributearray = [];
      for (let i = 0; i < response.attributeInfo.length; i++) {
        let attributeinfojson = {};
        attributeinfojson["name"] = response.attributeInfo[i].attrSourceName
        if (response.attributeInfo[i].sourceAttr.ref.type == "datapod" || response.attributeInfo[i].sourceAttr.ref.type == "dataset" || response.attributeInfo[i].sourceAttr.ref.type == "rule") {
          var sourceattribute = {}
          sourceattribute["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
          sourceattribute["label"] = response.attributeInfo[i].sourceAttr.ref.name;
          sourceattribute["dname"] = response.attributeInfo[i].sourceAttr.ref.name + '.' + response.attributeInfo[i].sourceAttr.attrName;
          sourceattribute["type"] = response.attributeInfo[i].sourceAttr.ref.type;
          sourceattribute["attributeId"] = response.attributeInfo[i].sourceAttr.attrId;
          sourceattribute["id"] = sourceattribute["uuid"] + "_" + sourceattribute["attributeId"]
          let obj = {}
          obj["value"] = "datapod"
          obj["label"] = "attribute"
          attributeinfojson["sourceAttributeType"] = obj;
          attributeinfojson["isSourceAtributeSimple"] = false;
          attributeinfojson["isSourceAtributeDatapod"] = true;
          attributeinfojson["isSourceAtributeFormula"] = false;
          attributeinfojson["isSourceAtributeExpression"] = false;
        }
        else if (response.attributeInfo[i].sourceAttr.ref.type == "simple") {
          let obj = {}
          obj["value"] = "string"
          obj["label"] = "string"
          attributeinfojson["sourceAttributeType"] = obj;
          attributeinfojson["isSourceAtributeSimple"] = true;
          attributeinfojson["sourcesimple"] = response.attributeInfo[i].sourceAttr.value
          attributeinfojson["isSourceAtributeDatapod"] = false;
          attributeinfojson["isSourceAtributeFormula"] = false;
          attributeinfojson["isSourceAtributeExpression"] = false;
          attributeinfojson["isSourceAtributeFunction"] = false;

        }
        if (response.attributeInfo[i].sourceAttr.ref.type == "expression") {
          let sourceexpression = {};
          sourceexpression["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
          sourceexpression["label"] = response.attributeInfo[i].sourceAttr.ref.name
          let obj = {}
          obj["value"] = "expression"
          obj["label"] = "expression"
          attributeinfojson["sourceAttributeType"] = obj;
          attributeinfojson["sourceexpression"] = sourceexpression;
          attributeinfojson["isSourceAtributeSimple"] = false;
          attributeinfojson["isSourceAtributeDatapod"] = false;
          attributeinfojson["isSourceAtributeFormula"] = false;
          attributeinfojson["isSourceAtributeExpression"] = true;
          attributeinfojson["isSourceAtributeFunction"] = false;
          this.getAllExpression(false, 0)
        }
        if (response.attributeInfo[i].sourceAttr.ref.type == "formula") {
          let sourceformula = {};
          sourceformula["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
          sourceformula["label"] = response.attributeInfo[i].sourceAttr.ref.name;
          let obj = {}
          obj["value"] = "formula"
          obj["label"] = "formula"
          attributeinfojson["sourceAttributeType"] = obj;
          attributeinfojson["sourceformula"] = sourceformula;
          attributeinfojson["isSourceAtributeSimple"] = false;
          attributeinfojson["isSourceAtributeDatapod"] = false;
          attributeinfojson["isSourceAtributeFormula"] = true;
          attributeinfojson["isSourceAtributeExpression"] = false;
          attributeinfojson["isSourceAtributeFunction"] = false;
          this.getAllFormula(false, 0);
        }
        if (response.attributeInfo[i].sourceAttr.ref.type == "function") {
          let sourcefunction = {};
          sourcefunction["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
          sourcefunction["label"] = response.attributeInfo[i].sourceAttr.ref.name
          let obj = {}
          obj["value"] = "function"
          obj["label"] = "function"
          attributeinfojson["sourceAttributeType"] = obj;
          attributeinfojson["sourcefunction"] = sourcefunction;
          attributeinfojson["isSourceAtributeSimple"] = false;
          attributeinfojson["isSourceAtributeDatapod"] = false;
          attributeinfojson["isSourceAtributeFormula"] = false;
          attributeinfojson["isSourceAtributeExpression"] = false;
          attributeinfojson["isSourceAtributeFunction"] = true;
          this.getAllFunctions(false, 0);
        }
        attributeinfojson["sourceattribute"] = sourceattribute;
        attributearray[i] = attributeinfojson
      }
      this.dataset.attributeTableArray = attributearray
    }

  }

  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      //allName["uuid"]=response[i]['uuid']
      this.VersionList[i] = ver;
    }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.dataset.version.uuid, this.dataset.version.label);
  }
  selectSourceType() {
    this._commonService.getAllLatest(this.dataset.source).subscribe(
      response => {
        this.OnSuccesgetAllLatest(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  changeType() {
    this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source).subscribe(
      response => {
        this.OnSuccesgetAllAttributeBySource(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response1) {
    // let dependOnTemp: DependsOn = new DependsOn();
    // dependOnTemp.label =response1[0]["name"];
    // dependOnTemp.uuid = response1[0]["uuid"];
    // this.dataset.sourcedata=dependOnTemp
    let temp = []
    for (const n in response1) {
      let allname = {};
      allname["label"] = response1[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response1[n]['name'];
      allname["value"]["uuid"] = response1[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource()
  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    //console.log(response)
    this.lhsdatapodattributefilter = response;
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['dname'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['dname'];
      allname["value"]["id"] = response[n]['id'];
      temp[n] = allname;
    }
    this.allMapSourceAttribute = temp
  }
  onChangeSourceAttribute(type, index) {
    if (type == "string") {
      this.dataset.attributeTableArray[index].isSourceAtributeSimple = true;
      this.dataset.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFormula = false;
      this.dataset.attributeTableArray[index].sourcesimple = "''";
      this.dataset.attributeTableArray[index].isSourceAtributeExpression = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = false;
    }
    else if (type == "datapod") {
      this.dataset.attributeTableArray[index].isSourceAtributeSimple = false;
      this.dataset.attributeTableArray[index].isSourceAtributeDatapod = true;
      this.dataset.attributeTableArray[index].isSourceAtributeFormula = false;
      this.dataset.attributeTableArray[index].isSourceAtributeExpression = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = false;
      this.dataset.attributeTableArray[index].sourceattribute = {}
      this.getAllAttributeBySource();
      if (this.allMapSourceAttribute && this.allMapSourceAttribute.length > 0) {
        let sourceattribute = {}
        sourceattribute["dname"] = this.allMapSourceAttribute[0]["label"]
        sourceattribute["id"] = this.allMapSourceAttribute[0]["value"]["id"];
        this.dataset.attributeTableArray[index].sourceattribute = sourceattribute;
      }
    }
    else if (type == "formula") {
      this.dataset.attributeTableArray[index].isSourceAtributeSimple = false;
      this.dataset.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFormula = true;
      this.dataset.attributeTableArray[index].isSourceAtributeExpression = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = false;
      this.dataset.attributeTableArray[index].sourceformula = {}
      this.getAllFormula(true, index);

    }
    else if (type == "expression") {
      this.dataset.attributeTableArray[index].isSourceAtributeSimple = false;
      this.dataset.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFormula = false;
      this.dataset.attributeTableArray[index].isSourceAtributeExpression = true;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = false;
      this.dataset.attributeTableArray[index].sourceexpression = {}
      this.getAllExpression(true, index);

    }
    else if (type == "function") {
      this.dataset.attributeTableArray[index].isSourceAtributeSimple = false;
      this.dataset.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFormula = false;
      this.dataset.attributeTableArray[index].isSourceAtributeExpression = false;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = true;
      this.dataset.attributeTableArray[index].isSourceAtributeFunction = true;
      this.dataset.attributeTableArray[index].sourcefunction = {}
      this.getAllFunctions(true, index);
    }
  }
  getAllFunctions(defaulfMode, index) {
    this._datasetService.getAllLatestFunction("function", "N").subscribe(
      response => { this.onSuccessFunction(response, defaulfMode, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response, defaulfMode, index) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.ruleLoadFunction = temp
    if (defaulfMode == true) {
      let sourcefunction = {};
      sourcefunction["uuid"] = this.ruleLoadFunction[0]["value"].uuid;
      sourcefunction["label"] = this.ruleLoadFunction[0].label;
      this.dataset.attributeTableArray[index].sourcefunction = sourcefunction;
    }
  }
  getAllExpression(defaulfMode, index) {
    this._datasetService.getExpressionByType(this.dataset.sourcedata.uuid, this.dataset.source).subscribe(
      response => { this.onSuccessExpression(response, defaulfMode, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessExpression(response, defaulfMode, index) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allMapExpression = temp;
    this.allMapExpression.splice(0, 0, {
      "label": "---CreateNew---",
      "value": { "label": "---CreateNew---", "uuid": "01" }
    });
    if (defaulfMode == true) {
      //   let sourceexpression = {};
      //   sourceexpression["uuid"] = this.allMapExpression[0]["value"].uuid;
      //   sourceexpression["label"] = this.allMapExpression[0].label;
      //   this.dataset.attributeTableArray[index].sourceexpression = sourceexpression;
    }
  }

  saveInLocal(key, val): void {
    this.sessionData = [];
    console.log('recieved= key:' + key + 'value:' + val);
    this.storage.set(key, val);
    console.log("data before sesion::" + JSON.stringify(this.storage));
    let a = this.storage.get(key);
    // let abc= this.storage.get(key);
    // this.sessionData[key]= this.storage.get(key);
  }

  onSelectExpression(event) {
    if (event.label == '---CreateNew---') {
      this.saveInLocal(0, this.dataset);
      this.router.navigate(['../../../../expressionDataset', this.dataset.source, this.dataset.sourcedata.label, this.dataset.sourcedata.uuid], { relativeTo: this.activatedRoute });
    }
  }

  getFromLocal(key): void {
    let data = this.storage.get(key);

    this.sessionData = [];
    if (data !== null) {
      this.sessionData[key] = this.storage.get(key);

      this.dataset = this.sessionData[key];
      this.datasetCompare = this.dataset;
      console.log(JSON.stringify(this.dataset.version));

      console.log("data get from session:: " + JSON.stringify(this.dataset));

      this.sessionData[key] = this.storage.remove(key);
      console.log(JSON.stringify(this.storage));
      console.log(JSON.stringify(this.sessionData[key]));

      this.getAllVersionByUuid2();

      let version: DependsOn = new DependsOn();
      version.label = this.dataset.version.label;
      version.uuid = this.dataset.version.uuid;
      this.dataset.version = version

      this._commonService.getAllLatest(this.dataset.source)
        .subscribe(response => { this.OnSuccesgetAllLatest(response) },
        error => console.log('Error :: ' + error))

      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))

      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))


      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
        error => console.log("Error ::", error))

      this.getAllAttributeBySource();
      this.getAllFormula(false, 0);
      this.getAllFunctions(false, 0);
      this.getAllExpression(false, 0)
    }

    else {
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
      }
    }
  }

  getAllFormula(defaulfMode, index) {
    this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormula(response, defaulfMode, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response, defaulfMode, index) {
    //this.allMapFormula = response
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allMapFormula = temp;
    if (defaulfMode == true) {
      let sourceformula = {};
      sourceformula["uuid"] = this.allMapFormula[0]["value"].uuid;
      sourceformula["label"] = this.allMapFormula[0].label;
      this.dataset.attributeTableArray[index].sourceformula = sourceformula;
    }
  }
  addRow() {
    if (this.dataset.filterTableArray == null) {
      this.dataset.filterTableArray = [];
    }
    var len = this.dataset.filterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = ""
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = ""
    filertable["rhsAttribute"] = ""
    this.dataset.filterTableArray.splice(this.dataset.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.dataset.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.dataset.filterTableArray = newDataList;
  }
  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.dataset.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }
  checkAllAttributeRow() {
    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    this.dataset.attributeTableArray.forEach(attribute => {
      attribute.selected = this.selectAllAttributeRow;
    });
  }
  addAttribute() {
    if (this.dataset.attributeTableArray == null) {
      this.dataset.attributeTableArray = [];
    }
    let len = this.dataset.attributeTableArray.length + 1
    let attrinfo = {};
    attrinfo["name"] = "attribute" + len;
    attrinfo["id"] = len - 1;
    attrinfo["sourceAttributeType"] = { "value": "string", "label": "string" };
    attrinfo["isSourceAtributeSimple"] = true;
    attrinfo["isSourceAtributeDatapod"] = false;
    this.dataset.attributeTableArray.splice(this.dataset.attributeTableArray.length, 0, attrinfo);
  }
  removeAttribute() {
    var newDataList = [];
    this.selectAllAttributeRow = false
    this.dataset.attributeTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.dataset.attributeTableArray = newDataList;
  }
  onChangeAttributeDatapod(data, index) {
    if (data != null) {
      this.dataset.attributeTableArray[index].name = data.label.split(".")[1]
    }
  }
  onChangeFormula(data, index) {
    this.dataset.attributeTableArray[index].name = data.name
  }
  onChangeExpression(data, index) {
    this.dataset.attributeTableArray[index].name = data.name
  }
  showDatapodSampleTable(data) {
    this.isDataError = false;
    this.isShowSimpleData = true;
    this.isDataInpogress = true;
    this.tableclass = 'centercontent';
    this.showdatapod = false;
    this.showgraph = false;
    this.graphDataStatus = false;
    this.showgraphdiv = false;
    //const api_url = this.baseUrl + 'datapod/getDatapodSample?action=view&datapodUUID=' + data.uuid + '&datapodVersion=' + data.version + '&row=100';
    const DatapodSampleData = this._datasetService.getDatasetSample(data.uuid, data.version).subscribe(
      response => { this.OnSuccesDatapodSample(response) },
      error => {
        this.IsTableShow = true;
        console.log("Error :: " + error)
        this.IsError = true;
      }
    )
  }

  OnSuccesDatapodSample(response) {
    this.IsTableShow = true;
    this.colsdata = response
    let columns = [];
    if (response.length && response.length > 0) {
      Object.keys(response[0]).forEach(val => {
        if (val != "rownum") {
          let width = ((val.split("").length * 9) + 20) + "px"
          columns.push({ "field": val, "header": val, colwidth: width });
        }
      });
    }
    this.cols = columns
    this.columnOptions = [];
    for (let i = 0; i < this.cols.length; i++) {
      this.columnOptions.push({ label: this.cols[i].header, value: this.cols[i] });
    }
  }

  onSuccessgetFormulaByLhsType(response) {
    this.lhsFormulaArray = []
    for (const i in response) {
      let formulaObj = {};
      formulaObj["label"] = response[i].name;
      formulaObj["value"] = {};
      formulaObj["value"]["uuid"] = response[i].uuid;
      formulaObj["value"]["label"] = response[i].name;
      this.lhsFormulaArray[i] = formulaObj;
    }
  }

  onSuccessgetAllAttributeBySourceLhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = response[i].dname;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].uuid;
      attributeObj["value"]["label"] = response[i].dname;
      attributeObj["value"]["attributeId"] = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  onChangeLhsType(index) {
    this.dataset.filterTableArray[index]["lhsAttribute"] == null;
 
    if (this.dataset.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dataset.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dataset.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsType(index) {
    this.dataset.filterTableArray[index]["rhsAttribute"] == null;
  
    if (this.dataset.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dataset.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dataset.filterTableArray[index]["rhsAttribute"] = null;
    }
  }

  onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [];
    let rhsFormulaObj = {};
    let temp = [];
    for (const i in response) {
      rhsFormulaObj["label"] = response[i].name;
      rhsFormulaObj["value"] = {};
      rhsFormulaObj["value"]["label"] = response[i].name;
      rhsFormulaObj["value"]["uuid"] = response[i].uuid;
      temp[i] = rhsFormulaObj;
    }
    this.rhsFormulaArray = temp
  }

  onSuccessgetAllAttributeBySourceRhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = response[i].dname;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].uuid;
      attributeObj["value"]["label"] = response[i].dname;
      attributeObj["value"]["attributeId"] = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  onChangeOperators(index) {
    this.dataset.filterTableArray[index]["rhsType"] = null;
  }

  enableEdit(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/dataset', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/dataset', uuid, version, 'true']);
  }

  showDatapodPage() {
    this.showdatapod = true;
    this.isShowSimpleData = false;
    this.showgraph = false;
    this.graphDataStatus = false;
    this.showgraphdiv = false
  }

  searchOption(index){
    this.displayDialogBox = true;
    this._commonService.getAllLatest("dataset")
    .subscribe(response => {this.onSuccessgetAllLatest(response)},
    error => console.log("Error ::", error))
  }

  onSuccessgetAllLatest(response){
    this.dialogAttriArray =  [];
    let temp = [];
    for(const i in response){
      let dialogAttriObj = {};
      
      dialogAttriObj["label"] = response[i].name;
      dialogAttriObj["value"] = {};
      dialogAttriObj["value"]["label"] = response[i].name;
      dialogAttriObj["value"]["uuid"] = response[i].uuid;
      temp[i] = dialogAttriObj;
    }
    this.dialogAttriArray = temp
    console.log(JSON.stringify(this.dialogAttriArray));
  }

  onChangeDialogAttribute(){
    this._commonService.getAttributesByDataset("dataset",this.dialogSelectName.uuid)
    .subscribe(response => {this.onSuccessgetAttributesByDataset(response)},
    error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDataset(response){
    this.dialogAttriNameArray =  [];
    for(const i in response){
      let dialogAttriNameObj = {};
      dialogAttriNameObj["label"] = response[i].attrName;
      dialogAttriNameObj["value"] = {};
      dialogAttriNameObj["value"]["label"] = response[i].attrName;
      dialogAttriNameObj["value"]["uuid"] = response[i].attrId;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index){
    this.displayDialogBox = false;
    this.dataset.filterTableArray[index].rhsAttribute = this.dialogAttributeName.label; 
  }

  cancelDialogBox(){
    this.displayDialogBox = false;
  }

  submitDataset() {
    this.isSubmitEnable1 = true;
    let datasetJson = {};
    datasetJson["uuid"] = this.dataset.uuid
    datasetJson["name"] = this.dataset.name

    var tagArray = [];
    if (this.dataset.tags != null) {
      for (var counttag = 0; counttag < this.dataset.tags.length; counttag++) {
        tagArray[counttag] = this.dataset.tags[counttag].value;
      }
    }
    datasetJson['tags'] = tagArray;
    datasetJson["desc"] = this.dataset.desc
    datasetJson["limit"] = this.dataset.limit
    let dependsOn = {};
    let ref = {}
    ref["type"] = this.dataset.source
    ref["uuid"] = this.dataset.sourcedata.uuid
    dependsOn["ref"] = ref;
    datasetJson["dependsOn"] = dependsOn;
    datasetJson["active"] = this.dataset.active == true ? 'Y' : "N"
    datasetJson["published"] = this.dataset.published == true ? 'Y' : "N"

    let filterInfoArray = [];

    if (this.dataset.filterTableArray != null ) {
      for (let i = 0; i < this.dataset.filterTableArray.length; i++) {

        let filterInfo = {};
        filterInfo["logicalOperator"] = this.dataset.filterTableArray[i].logicalOperator;
        filterInfo["operator"] = this.dataset.filterTableArray[i].operator;
        filterInfo["operand"] = [];

        if (this.dataset.filterTableArray[i].lhsType == 'integer' || this.dataset.filterTableArray[i].lhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.dataset.filterTableArray[i].lhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].lhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.dataset.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].lhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.dataset.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo["operand"][0] = operatorObj;
        }
        if (this.dataset.filterTableArray[i].rhsType == 'integer' || this.dataset.filterTableArray[i].rhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.dataset.filterTableArray[i].rhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][1] = operatorObj;

          if (this.dataset.filterTableArray[i].rhsType == 'integer' && this.dataset.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.dataset.filterTableArray[i].rhsAttribute1 + "and" + this.dataset.filterTableArray[i].rhsAttribute2;
            filterInfo["operand"][1] = operatorObj;
          }
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        filterInfoArray[i] = filterInfo;
      }

      datasetJson["filterInfo"] = filterInfoArray;
      console.log(JSON.stringify(filterInfoArray));
    }
    var sourceAttributesArray = [];
    for (var i = 0; i < this.dataset.attributeTableArray.length; i++) {
      var attributemap = {};
      attributemap["attrSourceId"] = i;
      attributemap["attrSourceName"] = this.dataset.attributeTableArray[i].name
      //attributeinfo.attrSourceName=$scope.dataset.attributeTableArray[l].name
      var sourceAttr = {};
      var sourceref = {};
      if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "string") {
        sourceref["type"] = "simple";
        sourceAttr["ref"] = sourceref;
        if (typeof this.dataset.attributeTableArray[i].sourcesimple == "undefined") {
          sourceAttr["value"] = "";
        }
        else {
          sourceAttr["value"] = this.dataset.attributeTableArray[i].sourcesimple;
        }
        attributemap["sourceAttr"] = sourceAttr;
      }
      else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "datapod") {
        let uuid = this.dataset.attributeTableArray[i].sourceattribute.id.split("_")[0]
        var attrid = this.dataset.attributeTableArray[i].sourceattribute.id.split("_")[1]
        sourceref["uuid"] = uuid;
        if (this.dataset.source == "relation") {
          sourceref["type"] = "datapod";
        }
        else {
          sourceref["type"] = this.dataset.source;
        }
        sourceAttr["ref"] = sourceref;
        sourceAttr["attrId"] = attrid;
        sourceAttr["attrType"] = null;
        attributemap["sourceAttr"] = sourceAttr;
      }
      else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "expression") {
        sourceref["type"] = "expression";
        sourceref["uuid"] = this.dataset.attributeTableArray[i].sourceexpression.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr;

      }
      else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "formula") {
        sourceref["type"] = "formula";
        sourceref["uuid"] = this.dataset.attributeTableArray[i].sourceformula.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr;

      }
      else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "function") {
        sourceref["type"] = "function"
        sourceref["uuid"] = this.dataset.attributeTableArray[i].sourcefunction.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr
      }
      sourceAttributesArray[i] = attributemap;
    }
    datasetJson["attributeInfo"] = sourceAttributesArray;
    console.log(JSON.stringify(datasetJson))

    this._commonService.submit("dataset", datasetJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable1 = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Dataset Submitted Successfully' });
    setTimeout(() => {
      this.goBack();
    }, 1000);
  }
  //   enableEdit(uuid, version) {
  //     this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'false']);

  //  }
  //  showview(uuid, version) {
  //     this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'true']);
  // }

}
