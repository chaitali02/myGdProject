import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Version } from '../../metadata/domain/version';
import { SelectItem } from 'primeng/primeng';
import { DataIngestionService } from '../../metadata/services/dataIngestion.service';

@Component({
  selector: 'app-data-ingestion-detail',
  templateUrl: './data-ingestion-detail.component.html'

})
export class DataIngestionDetailComponent implements OnInit {
  dialogAttributeName: any;
  dialogAttriNameArray: any[];
  dialogSelectName: any;
  dialogAttriArray: any[];
  displayDialogBox: boolean;
  allSourceAttributeForIncrmSplitBy: any[];
  selectedAutoMode: string;
  allAutoMapFile: { value: string; label: string; }[];
  allAutoMapTable: any;
  isAttributeMapDisable: boolean;
  isSubmit: string;
  selectedAllAttributeRow: boolean;
  msgs: any[];
  checkboxModelexecution: boolean;
  targetAttribute: any;
  allSourceAttributeTarget: any[];
  allTableSourceAttribute: any[];
  attributeTableArray: any;
  attributeSourceTypeArray: { value: string; label: string; }[];
  paramlistArray: any[];
  functionArray: any[];
  selectedAllFitlerRow: boolean;
  FormulaArray: any[];
  attributesArray: any[];
  operators: { 'value': string; 'label': string; }[];
  logicalOperators: { 'value': string; 'label': string; }[];
  rhsTypeArray: { value: string; label: string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  filterTableArray: any;
  runParams: any;
  saveModeArrayFile: { "value": string; "label": string; }[];
  saveModeArrayTable: { "value": string; "label": string; }[];
  saveModeArray: any;
  saveMode: any;
  targetNameForTable: any;
  targetNameArray: any[];
  targetName: any;
  streamSourceNameArray: any[];
  allSourceAttribute: any;
  splitBy: any;
  incrementKey: any;
  sourceTypeNameArray: any[];
  sourceTypeName: any;
  sourceTypeArray: { "value": string; "label": string; }[];
  sourceType: any;
  targetExtn: any;
  selectedTargetFormat: any;
  sourceExtn: any;
  sourceName: any;
  selectedSourceFormat: any;
  targetDs: any;
  FormatTypes: { "value": string; "label": string; }[];
  sourceDs: any;
  isTargetFormatDisable: boolean;
  isSourceFormatDisable: boolean;
  allTargetDatasource: any[];
  allSourceDatasource: any[];
  selectedRuleType: any;
  selectedTargetType: any;
  selectedSourceType: any;
  ruleTypes: { "value": string; "label": string; }[];
  tags: any[];
  createdBy: any;
  ingestData: any;
  progressbarWidth: string;
  continueCount: number;
  mode: any;
  version: any;
  id: any;
  uuid: any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataInjectService: DataIngestionService) {
    this.isSubmit = "false"
    this.ingestData = {};
    this.sourceDs = {};
    this.targetDs = {};
    this.sourceTypeName = {};
    this.filterTableArray = [];
    this.allSourceAttribute = []
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.breadcrumbDataFrom = [{
      "caption": "Data Ingestion",
      "routeurl": "/app/list/ingest"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/ingest"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];

    this.ruleTypes = [
      { "value": "FILE-FILE", "label": "File - File" },
      { "value": "FILE-TABLE", "label": "File - Table" },
      { "value": "TABLE-TABLE", "label": "Table - Table" },
      { "value": "TABLE-FILE", "label": "Table - File" },
      { "value": "STREAM-FILE", "label": "Stream - File" },
      { "value": "STREAM-TABLE", "label": "Stream - Table" },
    ]
    this.FormatTypes = [
      { "value": "CSV", "label": "CSV" },
      { "value": "TSV", "label": "TSV" },
      { "value": "PSV", "label": "PSV" },
      { "value": "PARQUET", "label": "PARQUET" }
    ]
    this.sourceTypeArray = [
      { "value": "datapod", "label": "datapod" },
      { "value": "dataset", "label": "dataset" }
    ]
    this.saveModeArrayTable = [
      { "value": "APPEND", "label": "APPEND" },
      { "value": "OVERWRITE", "label": "OVERWRITE" }
    ]
    this.saveModeArrayFile = [
      { "value": "OVERWRITE", "label": "OVERWRITE" },
      { "value": "", "label": "-Select-" }
    ]
    this.operators = [
      { 'value': '<', 'label': 'LESS THAN(<)' },
      { 'value': '>', 'label': 'GREATER THAN(>)' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL(<=)' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL(>=)' },
      { 'value': '=', 'label': 'EQUAL TO(=)' },
      { 'value': '!=', 'label': 'NOT EQUAL(!=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
      { 'value': 'IN', 'label': 'IN' },
      { 'value': 'NOT IN', 'label': 'NOT IN' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]

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
      { value: 'dataset', label: 'dataset' },
      { value: 'function', label: 'function' },
      { value: 'paramlist', label: 'paramlist' }
    ]
    this.attributeSourceTypeArray = [
      { value: 'string', label: 'string' },
      { value: 'datapod', label: 'attribute' },
      { value: 'formula', label: 'formula' },
      { value: 'function', label: 'function' },
      // { value: 'attribute', label: 'attribute' }
    ]
    this.allAutoMapTable = [
      { value: '', label: '-Select-' },
      { value: 'ByName', label: 'By Name' },
      { value: 'ByOrder', label: 'By Order' }
    ]
    this.allAutoMapFile = [
      { value: '', label: '-Select-' },
      { value: 'FromSource', label: 'From Source' },
      { value: 'FromTarget', label: 'From Target' }
    ]

  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
        //this.getAllLatest()
      }
      else {
        //this.getAllLatest()
      }
    });
    //console.log(this.selectedAppUuid)
  }

  onChangeAttributeTableType(index) {
    this.attributeTableArray[index]["sourceAttribute"] = null;
  }

  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('ingest', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      this.VersionList[i] = ver;
    }
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  getOneByUuidAndVersion(id, version) {
    this._dataInjectService.getOneByUuidAndVersion(id, version, "ingest").subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error::", +error)
    )
  }

  getDatasourceForFile(sourceType, TargetType) {
    this._dataInjectService.getDatasourceForFile("datasource").subscribe(
      response => {
        this.onSuccessgetDatasourceForFile(response)
      },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForFile(response) {
    if (this.selectedSourceType == "FILE") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        obj["value"]["version"] = response[i]['version'];
        temp[i] = obj;
      }
      this.allSourceDatasource = temp;
      console.log(JSON.stringify(this.allSourceDatasource));
    }
    if (this.selectedTargetType == "FILE") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        obj["value"]["version"] = response[i]['version'];
        temp[i] = obj;
      }
      this.allTargetDatasource = temp;
      console.log(JSON.stringify(this.allSourceDatasource));
    }
  }

  getDatasourceForTable() {
    this._dataInjectService.getDatasourceForTable("datasource").subscribe(
      response => { this.onSuccessgetDatasourceForTable(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForTable(response) {
    if (this.selectedSourceType == "TABLE") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        obj["value"]["version"] = response[i]['version'];
        temp[i] = obj;
      }
      this.allSourceDatasource = temp;

      if (this.selectedSourceType == 'TABLE' && this.selectedTargetType == 'FILE') {
        if (this.allSourceDatasource && this.allSourceDatasource != null && this.allSourceDatasource.length > 0) {
          for (var i = 0; i < this.allSourceDatasource.length; i++) {
            if (response[i].type == 'HIVE') {
              if (this.allTargetDatasource)
                this.allTargetDatasource.push(this.allSourceDatasource[i])
              else {
                this.allTargetDatasource = [];
                this.allTargetDatasource.push(this.allSourceDatasource[i])
              }
            }
          }
        }
      }
    }
    if (this.selectedTargetType == "TABLE") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        temp[i] = obj;
      }
      this.allTargetDatasource = temp;

      if (this.selectedSourceType == 'FILE' && this.selectedTargetType == 'TABLE') {
        if (this.allTargetDatasource && this.allTargetDatasource != null && this.allTargetDatasource.length > 0) {
          for (var i = 0; i < this.allTargetDatasource.length; i++) {
            if (response[i].type == 'HIVE') {
              if (this.allSourceDatasource)
                this.allSourceDatasource.push(this.allTargetDatasource[i]);
              else {
                this.allSourceDatasource = [];
                this.allSourceDatasource.push(this.allTargetDatasource[i]);
              }
            }
          }
        }
      }
    }
    if (this.selectedSourceType == "STREAM" && this.selectedTargetType == "FILE") {
      if (response != null && response.length > 0) {
        for (const i in response) {
          if (response[i].type == 'HIVE') {

            if (this.allTargetDatasource) {
              let temp = [];
              for (const j in response) {
                let obj = {};
                obj["label"] = response[j]['name'];
                obj["value"] = {};
                obj["value"]["label"] = response[j]['name'];
                obj["value"]["uuid"] = response[j]['uuid'];
                temp[j] = obj;
              }
              this.allTargetDatasource.push(temp[i])
            }
            else {
              this.allTargetDatasource = [];
              let temp = [];
              for (const j in response) {
                let obj = {};
                obj["label"] = response[j]['name'];
                obj["value"] = {};
                obj["value"]["label"] = response[j]['name'];
                obj["value"]["uuid"] = response[j]['uuid'];
                temp[j] = obj;
              }
              this.allTargetDatasource.push(temp[i])
            }
          }
        }
      }
    }
  }

  getDatasourceForStream() {
    this._dataInjectService.getDatasourceForStream("datasource").subscribe(
      response => { this.onSuccessgetDatasourceForStream(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForStream(response) {
    if (this.selectedSourceType == "STREAM") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        obj["value"]["version"] = response[i]['version'];
        temp[i] = obj;
      }
      this.allSourceDatasource = temp;
    }
    if (this.selectedTargetType == "STREAM") {
      let temp = [];
      for (const i in response) {
        let obj = {};
        obj["label"] = response[i]['name'];
        obj["value"] = {};
        obj["value"]["label"] = response[i]['name'];
        obj["value"]["uuid"] = response[i]['uuid'];
        temp[i] = obj;
      }
      this.allTargetDatasource = temp;
    }
  }

  onChangeRuleType() {
    this.targetDs = null;
    this.sourceDs = null;
    this.sourceName = null;
    this.targetName = null;
    this.incrementKey = null;
    this.attributeTableArray = [];

    this.selectedTargetFormat = null;
    this.selectedSourceFormat = null;
    this.ingestData["ingestChg"] = "Y";
    this.selectedSourceType = this.selectedRuleType.split("-")[0];
    this.selectedTargetType = this.selectedRuleType.split("-")[1];
    this.isSourceFormatDisable = "this.selectedSourceType == 'FILE'" ? false : true;
    this.isTargetFormatDisable = "this.selectedTargetType == 'FILE'" ? false : true;

    if (this.selectedSourceType == 'FILE' && this.selectedTargetType == 'FILE' && this.mode == "true") {
      this.isAttributeMapDisable = true;
    }
    else if (this.selectedSourceType == 'FILE' && this.selectedTargetType == 'FILE' && this.mode == "false") {
      this.isAttributeMapDisable = false;
    }
    else {
      if (this.selectedSourceType != 'FILE' || this.selectedTargetType != 'FILE') {
        this.isAttributeMapDisable = true;
      }
      else if (this.mode == "true") {
        this.isAttributeMapDisable = false;
      } else {
        this.isAttributeMapDisable = true;
      }
    }

    if (this.selectedSourceType == 'FILE' || this.selectedTargetType == 'FILE') {
      this.getDatasourceForFile(this.selectedSourceType, this.selectedTargetType);
    }
    if (this.selectedSourceType == 'TABLE' || this.selectedTargetType == 'TABLE') {
      this.getDatasourceForTable();
    }
    if (this.selectedSourceType == 'STREAM' || this.selectedTargetType == 'STREAM') {
      this.getDatasourceForStream();
    }
    if (this.selectedSourceType == 'STREAM' && this.selectedTargetType == 'FILE') {
      this.getDatasourceForTable();
    }
  }

  onChangeSourceFormat() {
    this.sourceExtn = this.selectedSourceFormat != 'PARQUET' ? this.selectedSourceFormat.toLowerCase() : null;
  }

  onChangeTargetFormat() {
    this.targetExtn = this.selectedTargetFormat != 'PARQUET' ? this.selectedTargetFormat.toLowerCase() : null;
  }

  onChangeSourceType() {
    if (this.sourceType == "datapod") {
      this._dataInjectService.getDatapodByDatasource("datasource", this.sourceDs.uuid).subscribe(
        response => { this.onSuccessgetDatapodByDatasource(response) },
        error => console.log("Error::", +error)
      )
    }
    if (this.sourceType == "dataset") {
      this._commonService.getAllLatest("dataset").subscribe(
        response => { this.onSuccessgetAllLatest(response) },
        error => console.log("Error::", +error)
      )
    }
  }

  onSuccessgetDatapodByDatasource(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['name'];
      obj["value"] = {};
      obj["value"]["label"] = response[i]['name'];
      obj["value"]["uuid"] = response[i]['uuid'];
      temp[i] = obj;
    }
    this.sourceTypeNameArray = temp;
  }

  onSuccessgetAllLatest(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['name'];
      obj["value"] = {};
      obj["value"]["label"] = response[i]['name'];
      obj["value"]["uuid"] = response[i]['uuid'];
      temp[i] = obj;
    }
    this.sourceTypeNameArray = temp;
  }

  onChangeSourceTypeName() {
    this.getAllAttributeBySource();
    this.getAllAttributeBySource1();
  }

  getAllAttributeBySource() {
    if (this.sourceType == 'datapod') {
      this._dataInjectService.getAttributesByDatapod(this.sourceType, this.sourceTypeName.uuid).subscribe(
        response => { this.onSuccessgetAttributesByDatapod(response) },
        error => console.log("Error::", +error)
      )
    }
    else if (this.sourceType == 'dataset') {
      this._dataInjectService.getAttributesByDataset(this.sourceType, this.sourceTypeName.uuid).subscribe(
        response => { this.onSuccessgetAttributesByDataset(response) },
        error => console.log("Error::", +error)
      )
    }
  }
  getAllAttributeBySource1() {
    if (this.sourceType == 'datapod') {
      this._dataInjectService.getAttributesByDatapod(this.sourceType, this.sourceTypeName.uuid).subscribe(
        response => { this.onSuccessgetAttributesByDatapod1(response) },
        error => console.log("Error::", +error)
      )
    }
    else if (this.sourceType == 'dataset') {
      this._dataInjectService.getAttributesByDataset(this.sourceType, this.sourceTypeName.uuid).subscribe(
        response => { this.onSuccessgetAttributesByDataset1(response) },
        error => console.log("Error::", +error)
      )
    }
  }
  onSuccessgetAttributesByDatapod(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["attributeId"] = response[i]['attrId'];
      obj["value"]["attrName"] = response[i]['attrName'];
      //  obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      //  obj["value"]["datapodname"] = response[i]['ref']['name'];
      obj["value"]["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];

      temp[i] = obj;
    }
    this.allSourceAttribute = temp;

    if(this.attributeTableArray.length == 0  && this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE"){
      for(let i =0;i<this.allSourceAttribute.length;i++){

        var attributemapjson = {};
        var obj = {}
        attributemapjson["attrMapId"] = i;
        attributemapjson["sourceType"] = "datapod";

        var sourceattribute = {}
        sourceattribute["attributeId"] = this.allSourceAttribute[i].value.attributeId;
        sourceattribute["attrName"] = this.allSourceAttribute[i].value.attrName;
        sourceattribute["uuid"] = this.allSourceAttribute[i].value.uuid;
        sourceattribute["label"] = this.allSourceAttribute[i].value.label;
        attributemapjson["sourceAttribute"] = sourceattribute;

        attributemapjson["targetAttribute"] = "";

        this.attributeTableArray[i] = attributemapjson;
        this.attributeTableArray[i]["IsTargetAttributeSimple"] = 'true';
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onSuccessgetAttributesByDataset(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"]["attributeId"] = response[i]['attrId'];
      // obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      obj["value"]["attrName"] = response[i]['attrName'];
      //  obj["value"]["dname"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttribute = temp;

    if(this.attributeTableArray.length == 0  && this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE"){
      for(let i =0;i<this.allSourceAttribute.length;i++){

        var attributemapjson = {};
        var obj = {}
        attributemapjson["attrMapId"] = i;
        attributemapjson["sourceType"] = "datapod";

        var sourceattribute = {}
        sourceattribute["attributeId"] = this.allSourceAttribute[i].value.attributeId;
        sourceattribute["attrName"] = this.allSourceAttribute[i].value.attrName;
        sourceattribute["uuid"] = this.allSourceAttribute[i].value.uuid;
        sourceattribute["label"] = this.allSourceAttribute[i].value.label;
        attributemapjson["sourceAttribute"] = sourceattribute;

        attributemapjson["targetAttribute"] = "";

        this.attributeTableArray[i] = attributemapjson;
        this.attributeTableArray[i]["IsTargetAttributeSimple"] = 'true';
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onSuccessgetAttributesByDatapod1(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["label"] = response[i]['attrName'];
      obj["value"]["attributeId"] = response[i]['attrId'];
      // obj["value"]["attrName"] = response[i]['attrName'];
      obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      obj["value"]["datapodname"] = response[i]['ref']['name'];
      // obj["value"]["dname"] = response[i]['ref']['name'] + "." + response[i]['attrName'];

      temp[i] = obj;
    }
    this.allSourceAttributeForIncrmSplitBy = temp;
  }

  onSuccessgetAttributesByDataset1(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["label"] = response[i]['attrName'];
      obj["value"]["attributeId"] = response[i]['attrId'];

      obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      obj["value"]["datapodname"] = response[i]['ref']['name'];
      // obj["value"]["dname"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttributeForIncrmSplitBy = temp;
  }

  onChangeSourceDs() {
    this.sourceType = "";
    if (this.selectedSourceType == 'STREAM') {
      this._dataInjectService.getTopicList(this.sourceDs.uuid, this.sourceDs.version).subscribe(
        response => { this.onSuccessgetTopicList(response) },
        error => console.log("Error::", +error))
    }
  }

  onSuccessgetTopicList(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["value"] = response[i]
      obj["label"] = response[i]
      temp[i] = obj;
    }
    this.streamSourceNameArray = temp;
  }

  onChangeTargetDs() {
    this._dataInjectService.getDatapodByDatasource("datasource", this.targetDs.uuid).subscribe(
      response => { this.onSuccessgetDatapodByDatasourceTarget(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatapodByDatasourceTarget(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['name'];
      obj["value"] = {}
      obj["value"]["label"] = response[i]['name'];
      obj["value"]["uuid"] = response[i]['uuid'];
      temp[i] = obj;
    }
    this.targetNameArray = temp;
  }

  onSuccessgetFormulaByType(response) {

    let FormulaObj = {};
    let temp = [];
    for (const i in response) {
      FormulaObj["label"] = response[i].name;
      FormulaObj["value"] = {};
      FormulaObj["value"]["label"] = response[i].name;
      FormulaObj["value"]["uuid"] = response[i].uuid;
      temp[i] = FormulaObj;
    }
    this.FormulaArray = temp
  }

  onSuccessgetAllAttributeBySource(response) {
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

  addRow() {
    if (this.filterTableArray == null) {
      this.filterTableArray = [];
    }
    var len = this.filterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = "integer"
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = "integer"
    filertable["rhsAttribute"] = ""
    this.filterTableArray.splice(this.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.filterTableArray = newDataList;
  }
  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }

  addAttributeRow() {
    if (this.attributeTableArray == null) {
      this.attributeTableArray = [];
    }
    var len = this.attributeTableArray.length + 1
    var attributetable = {};
    attributetable["attrMapId"] = len - 1;
    attributetable["sourceType"] = "string";
    attributetable["sourceAttribute"] = ""
    attributetable["targetAttribute"] = ""
    this.attributeTableArray.splice(this.attributeTableArray.length, 0, attributetable);
    this.attributeTableArray[len - 1]["IsTargetAttributeSimple"] = "true"
  }
  removeAttributeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.attributeTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.attributeTableArray = newDataList;
  }
  checkAllAttributeRow() {
    if (!this.selectedAllAttributeRow) {
      this.selectedAllAttributeRow = true;
    }
    else {
      this.selectedAllAttributeRow = false;
    }
    this.attributeTableArray.forEach(attribute => {
      attribute.selected = this.selectedAllAttributeRow;
    });
  }

  onChangeRhsType(index) {
    this.filterTableArray[index]["rhsAttribute"] == null;

    if (this.filterTableArray[index]["rhsType"] == 'formula') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetFormulaByType(response) },
          error => console.log("Error ::", error))
      }
    }
    else if (this.filterTableArray[index]["rhsType"] == 'datapod') {debugger
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
      }
    }
    else if (this.filterTableArray[index]["rhsType"] == 'function') {
      this._dataInjectService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'paramlist') {
      this._dataInjectService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'dataset') {debugger
      let rhsAttribute = {};
      rhsAttribute["label"] = "-Select-";
      rhsAttribute["uuid"] = "";
      rhsAttribute["attributeId"] = "";
      this.filterTableArray[index]["rhsAttribute"] = rhsAttribute
    }
    else {
      this.filterTableArray[index]["rhsAttribute"] = null;
    }
  }

  onChangeLhsType(index) {
    this.filterTableArray[index]["lhsAttribute"] == null;

    if (this.filterTableArray[index]["lhsType"] == 'formula') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetFormulaByType(response) },
          error => console.log("Error ::", error))

      }
    }

    else if (this.filterTableArray[index]["lhsType"] == 'datapod') {debugger
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
      }
    }

    else {
      this.filterTableArray[index]["lhsAttribute"] = null;
    }
  }
  onSuccessgetFunctionByCriteria(response) {
    let temp = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = response[i].name;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].uuid;
      attributeObj["value"]["label"] = response[i].name;
      temp[i] = attributeObj
    }
    this.functionArray = temp;
  }
  onSuccessgetParamByApp(response) {
    let temp = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = "app." + response[i].paramName;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].ref.uuid;
      attributeObj["value"]["attributeId"] = response[i].paramId;
      attributeObj["value"]["label"] = "app." + response[i].paramName;
      temp[i] = attributeObj
    }
    this.paramlistArray = temp;
  }

  onSuccessgetAttributesByDatapodTarget(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      obj["value"]["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["attributeId"] = response[i]['attrId'];
      obj["value"]["attrName"] = response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttributeTarget = temp;
  }

  onChangeTargetNameTable() {
    this._dataInjectService.getAttributesByDatapod("datapod", this.targetNameForTable.uuid).subscribe(
      response => {
        this.onSuccessgetAttributesByDatapodTable(response),
          error => console.log("Error ::,+error")
      }
    )
  }

  onSuccessgetAttributesByDatapodTable(response) {
    let temp = [];
    for (const i in response) {
      let obj = {};
      obj["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"] = {};
      obj["value"]["uuid"] = response[i]['ref']['uuid'];
      obj["value"]["label"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      obj["value"]["type"] = response[i]['ref']['type'];
      obj["value"]["attributeId"] = response[i]['attrId'];
      obj["value"]["attrName"] = response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttributeTarget = temp;

    this.allTableSourceAttribute = this.allSourceAttribute;

    if (this.attributeTableArray.length == 0 && this.selectedSourceType != "STREAM") {
      this.attributeTableArray = [];
      //this.attributeTableArray = this.allSourceAttributeTarget;
      for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
        var attributemapjson = {};
        var obj = {}
        attributemapjson["attrMapId"] = i;
        attributemapjson["sourceType"] = "datapod";
        attributemapjson["sourceAttribute"] = "";

        var targetattribute = {}

        targetattribute["uuid"] = this.allSourceAttributeTarget[i].value.uuid;
        targetattribute["label"] = this.allSourceAttributeTarget[i].value.label;
        targetattribute["type"] = this.allSourceAttributeTarget[i].value.type;
        targetattribute["attrName"] = this.allSourceAttributeTarget[i].value.attrName;
        targetattribute["attributeId"] = this.allSourceAttributeTarget[i].value.attributeId;
        attributemapjson["targetAttribute"] = targetattribute;

        this.attributeTableArray[i] = attributemapjson;
        this.attributeTableArray[i]["IsTargetAttributeSimple"] = 'false';
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onChangeAutoMode() {
    if (this.attributeTableArray != null) {
      let temp = this.attributeTableArray;
      this.attributeTableArray = [];
      if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "ByOrder") {
        if (this.allSourceAttributeTarget) {
          for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
            var mapInfo = {};
            mapInfo["attrMapId"] = i;
            mapInfo["sourceType"] = "datapod";

            let sourceObj = {};
            if (this.allSourceAttribute.length > i) {
              sourceObj["attributeId"] = this.allSourceAttribute[i]["value"]["attributeId"];
              sourceObj["attrName"] = this.allSourceAttribute[i]["value"]["attrName"];
              sourceObj["uuid"] = this.allSourceAttribute[i]["value"]["uuid"];
              sourceObj["label"] = this.sourceTypeName["label"] + "." + this.allSourceAttribute[i]["value"]["attrName"];
            }
            else {
              sourceObj = {};
            }
            mapInfo["sourceAttribute"] = sourceObj;

            let obj = {};
            obj["uuid"] = temp[i]["targetAttribute"]["uuid"];
            obj["label"] = temp[i]["targetAttribute"]["label"];
            obj["type"] = temp[i]["targetAttribute"]["type"];
            obj["attrName"] = temp[i]["targetAttribute"]["attrName"];
            obj["attributeId"] = temp[i]["targetAttribute"]["attributeId"];
            mapInfo["targetAttribute"] = obj;
            mapInfo["IsTargetAttributeSimple"] = "false";
            this.attributeTableArray[i] = mapInfo;
          }
          console.log(JSON.stringify(this.attributeTableArray));
        }
      }

      else if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "ByName") {
        for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = "datapod";

          let obj = {};
          obj["uuid"] = temp[i]["targetAttribute"]["uuid"];
          obj["label"] = temp[i]["targetAttribute"]["label"];
          obj["type"] = temp[i]["targetAttribute"]["type"];
          obj["attrName"] = temp[i]["targetAttribute"]["attrName"];
          obj["attributeId"] = temp[i]["targetAttribute"]["attributeId"];
          mapInfo["targetAttribute"] = obj;
          mapInfo["IsTargetAttributeSimple"] = "false";

          let sourceObj = {};
          for (let j = 0; j < this.allSourceAttribute.length; j++) {
            if (this.allSourceAttribute[j]["value"]["attrName"] == temp[i]["targetAttribute"]["attrName"]) {
              sourceObj["attributeId"] = this.allSourceAttribute[j]["value"]["attributeId"];
              sourceObj["attrName"] = this.allSourceAttribute[j]["value"]["attrName"];
              sourceObj["uuid"] = this.allSourceAttribute[j]["value"]["uuid"];
              sourceObj["label"] = this.sourceTypeName["label"] + "." + this.allSourceAttribute[j]["value"]["attrName"];
              break;
            }
            else {
              sourceObj = {};
            }
          }
          mapInfo["sourceAttribute"] = sourceObj;
          this.attributeTableArray[i] = mapInfo;
        }
        console.log(JSON.stringify(this.attributeTableArray));
      }

      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "FromTarget") {
        for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = "datapod";
          mapInfo["sourceAttribute"] = temp[i]["targetAttribute"]["attrName"];

          let obj = {};
          obj["uuid"] = temp[i]["targetAttribute"]["uuid"];
          obj["label"] = temp[i]["targetAttribute"]["label"];
          obj["type"] = temp[i]["targetAttribute"]["type"];
          obj["attrName"] = temp[i]["targetAttribute"]["attrName"];
          obj["attributeId"] = temp[i]["targetAttribute"]["attributeId"];
          mapInfo["targetAttribute"] = obj;
          mapInfo["IsTargetAttributeSimple"] = "false";

          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < this.allSourceAttribute.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = "datapod";
          let obj = {}
          obj["attributeId"] = temp[i]["sourceAttribute"]["attributeId"];
          obj["attrName"] = temp[i]["sourceAttribute"]["attrName"];
          obj["uuid"] = temp[i]["sourceAttribute"]["uuid"];
          obj["label"] = temp[i]["sourceAttribute"]["label"];
          mapInfo["sourceAttribute"] = obj;

          mapInfo["targetAttribute"] = temp[i]["sourceAttribute"]["attrName"];
          mapInfo["IsTargetAttributeSimple"] = "true";

          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = temp[i]["sourceType"];
          if(temp[i]["sourceType"]=='string' || temp[i]["sourceType"]=='datapod'){
            mapInfo["sourceAttribute"] = temp[i]["sourceAttribute"];
            mapInfo["targetAttribute"] = temp[i]["sourceAttribute"];
          } else{
            let obj = {}
            //obj["attributeId"] = temp[i]["sourceAttribute"]["attributeId"];
           // obj["attrName"] = temp[i]["sourceAttribute"]["attrName"];
            obj["uuid"] = temp[i]["sourceAttribute"]["uuid"];
            obj["label"] = temp[i]["sourceAttribute"]["label"];
            mapInfo["sourceAttribute"] = obj;
            
            mapInfo["targetAttribute"] = temp[i]["sourceAttribute"].label;
          }
         
          mapInfo["IsTargetAttributeSimple"] = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromTarget") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = "datapod";
          mapInfo["sourceAttribute"] = temp[i]["targetAttribute"];
          mapInfo["targetAttribute"] = temp[i]["targetAttribute"];
          mapInfo["IsTargetAttributeSimple"] = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "STREAM" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = {};
          mapInfo["attrMapId"] = i;
          mapInfo["sourceType"] = "datapod";
          mapInfo["sourceAttribute"] = temp[i]["sourceAttribute"];
          mapInfo["targetAttribute"] = temp[i]["sourceAttribute"];
          mapInfo["IsTargetAttributeSimple"] = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
    }
  }

  searchOption(index){debugger
    this.displayDialogBox = true;
    this._commonService.getAllLatest("dataset")
    .subscribe(response => {this.onSuccessgetAllLatestDialogBox(response)},
    error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBox(response){debugger
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
    .subscribe(response => {this.onSuccessgetAttributesByDatasetDialogBox(response)},
    error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response){debugger
    this.dialogAttriNameArray =  [];
    for(const i in response){
      let dialogAttriNameObj = {};
      dialogAttriNameObj["label"] = response[i].attrName;
      dialogAttriNameObj["value"] = {};
      dialogAttriNameObj["value"]["label"] = response[i].attrName;
      dialogAttriNameObj["value"]["attributeId"] = response[i].attrId;
      dialogAttriNameObj["value"]["uuid"] = response[i].ref.uuid;

      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index){debugger
    this.displayDialogBox = false;
    let rhsattribute = {}
    rhsattribute["label"] = this.dialogAttributeName.label;
    rhsattribute["uuid"] = this.dialogAttributeName.uuid;
    rhsattribute["attributeId"] = this.dialogAttributeName.attributeId;
    this.filterTableArray[index].rhsAttribute = rhsattribute;
  }

  cancelDialogBox(){
    this.displayDialogBox = false;
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.ingestData = response;
    this.createdBy = response.createdBy.ref.name;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version;
    if (response.tags != null) {
      let temp = []
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        temp[i] = tag
      }
      this.tags = temp;
    }
    this.ingestData.published = response["published"] == 'Y' ? true : false;
    this.ingestData.active = response["active"] == 'Y' ? true : false;
    this.selectedRuleType = response.type;
    this.onChangeRuleType();

    let sourceObj = {};
    sourceObj["uuid"] = response.sourceDatasource.ref.uuid;
    sourceObj["label"] = response.sourceDatasource.ref.name;
    sourceObj["version"] = response.sourceDatasource.ref.version;
    this.sourceDs = sourceObj;
    if (this.sourceDs.label == 'Stream') {
      this._dataInjectService.getTopicList(this.sourceDs.uuid, this.sourceDs.version).subscribe(
        response => { this.onSuccessgetTopicList(response) },
        error => console.log("Error::", +error))
    }
    if (response.sourceFormat != null) {
      this.selectedSourceFormat = response.sourceFormat
    }
    //if(response.sourceDetail.value != null){
    this.sourceName = response.sourceDetail.value;
    //}
    if (response.sourceExtn != null) {
      this.sourceExtn = response.sourceExtn.toLowerCase();
    }
    else
      this.sourceExtn = null;
    this.ingestData.sourceHeader = response["sourceHeader"] == 'Y' ? true : false;

    this.sourceType = response.sourceDetail.ref.type;

    let sourceTypeNameObj = {};
    sourceTypeNameObj["uuid"] = response.sourceDetail.ref.uuid;
    sourceTypeNameObj["label"] = response.sourceDetail.ref.name;
    this.sourceTypeName = sourceTypeNameObj
    this.onChangeSourceType();

    this.getAllAttributeBySource1();

    if (response.incrAttr != null) {
      let incrementKeyObj = {};

      incrementKeyObj["attributeId"] = response.incrAttr.attrId;
      incrementKeyObj["label"] = response.incrAttr.attrName;
      incrementKeyObj["type"] = response.incrAttr.ref.type
      incrementKeyObj["uuid"] = response.incrAttr.ref.uuid;
      incrementKeyObj["datapodname"] = response.incrAttr.ref.name;
      // incrementKeyObj["dname"] = response.incrAttr.ref.name + "." + response.incrAttr.attrName;
      this.incrementKey = incrementKeyObj;
    }
    this.getAllAttributeBySource();
    if (response.splitBy != null) {
      let splitByObj = {};
      splitByObj["attributeId"] = response.splitBy.attrId;
      splitByObj["attrName"] = response.splitBy.attrName;
      splitByObj["type"] = response.splitBy.ref.type;
      splitByObj["uuid"] = response.splitBy.ref.uuid;
      splitByObj["datapodname"] = response.splitBy.ref.name;
      splitByObj["dname"] = response.splitBy.ref.name + "." + response.splitBy.attrName;
      this.splitBy = splitByObj;
    }

    let targetObj = {};
    targetObj["uuid"] = response.targetDatasource.ref.uuid;
    targetObj["label"] = response.targetDatasource.ref.name;
    this.targetDs = targetObj;

    if (response.targetFormat != null) {
      this.selectedTargetFormat = response.targetFormat
    }
    this.targetName = response.targetDetail.value;

    let targetNameObj = {}
    targetNameObj["uuid"] = response.targetDetail.ref.uuid;
    targetNameObj["label"] = response.targetDetail.ref.name;
    this.targetNameForTable = targetNameObj;
    this.onChangeTargetDs();

    if (response.targetExtn != null) {
      this.targetExtn = response.targetExtn.toLowerCase();
    }
    else
      this.targetExtn = null;
    this.ingestData.targetHeader = response["targetHeader"] == 'Y' ? true : false;

    this.saveMode = response.saveMode;

    this.runParams = response.runParams;

    if (this.sourceTypeName != null && this.sourceType != null) {
      this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
        error => console.log("Error ::", error))

      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
      }
    }
    this._dataInjectService.getFunctionByCriteria("", "N", "function")
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
      error => console.log("Error ::", error))

    this._dataInjectService.getParamByApp("", "application")
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
      error => console.log("Error ::", error))

    this.filterTableArray = response.filterTableArray

    this._dataInjectService.getAttributesByDatapod(this.sourceType, this.sourceTypeName.uuid).subscribe(
      response => {
        this.onSuccessgetAttributesByDatapod(response)
      },
      error => console.log("Error::", +error))

    this._dataInjectService.getAttributesByDatapod("datapod", this.targetNameForTable.uuid).subscribe(
      response => { this.onSuccessgetAttributesByDatapodTarget(response) },
      error => console.log("Error::", +error))

    // if("selectedSourceType !='STREAM' || selectedTargetType !='TABLE'"){
    //   this.targetAttribute1 = +"."+this.targetAttribute;
    // }    
    this.attributeTableArray = response.attributeTableArray;
  }

  ingestSubmit() {
    this.isSubmit = "true"
    let ingestJson = {}
    ingestJson["uuid"] = this.ingestData.uuid
    ingestJson["name"] = this.ingestData.name
    ingestJson["desc"] = this.ingestData.desc;
    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;
      }
    }
    ingestJson['tags'] = tagArray
    ingestJson["active"] = this.ingestData.active == true ? 'Y' : "N"
    ingestJson["published"] = this.ingestData.published == true ? 'Y' : "N"
    ingestJson['type'] = this.selectedRuleType;
    ingestJson["runParams"] = this.runParams;
    this.selectedSourceType = this.selectedRuleType.split("-")[0];
    this.selectedTargetType = this.selectedRuleType.split("-")[1];
    //if(this.selectedSourceType == "File"){
    ingestJson["sourceExtn"] = this.sourceExtn;
    ingestJson["sourceHeader"] = this.ingestData.sourceHeader == true ? 'Y' : "N";
    ingestJson["ignoreCase"] = this.ingestData.ignoreCase;
    // }
    // if(this.selectedTargetType == "File"){
    ingestJson["targetHeader"] = this.ingestData.targetHeader == true ? 'Y' : "N";
    ingestJson["targetExtn"] = this.targetExtn;
    // }

    let sourceDatasource = {};
    let sourceDataRef = {};

    sourceDataRef["uuid"] = this.sourceDs.uuid;
    sourceDataRef["type"] = "datasource";
    sourceDatasource["ref"] = sourceDataRef;
    ingestJson["sourceDatasource"] = sourceDatasource;
    ingestJson["sourceFormat"] = this.selectedSourceFormat;

    let sourceDetail = {};
    let sourceDetailRef = {};
    if (this.selectedSourceType == "FILE" || this.selectedSourceType == "STREAM") {
      sourceDetailRef["type"] = "simple";
      sourceDetail["ref"] = sourceDetailRef;
      sourceDetail["value"] = this.sourceName;
    } else {
      sourceDetailRef["type"] = this.sourceType
      sourceDetailRef["uuid"] = this.sourceTypeName.uuid;
      sourceDetail["ref"] = sourceDetailRef;
    }
    ingestJson["sourceDetail"] = sourceDetail;

    let targetDatasource = {};
    let targetDatasourceRef = {};
    targetDatasourceRef["uuid"] = this.targetDs.uuid;
    targetDatasourceRef["type"] = "datasource";
    targetDatasource["ref"] = targetDatasourceRef;
    ingestJson["targetDatasource"] = targetDatasource;
    ingestJson["targetFormat"] = this.selectedTargetFormat;

    let targetDetails = {};
    let targetDetailsRef = {};
    if (this.selectedTargetType == "FILE") {
      targetDetailsRef["type"] = "simple";
      targetDetails["ref"] = targetDetailsRef;
      targetDetails["value"] = this.targetName;
    }
    else {
      targetDetailsRef["type"] = "datapod";
      targetDetailsRef["uuid"] = this.targetNameForTable.uuid;
      targetDetails["ref"] = targetDetailsRef;
    }
    ingestJson["targetDetail"] = targetDetails;

    if (this.saveMode) {
      ingestJson["saveMode"] = this.saveMode;
    }
    else {
      ingestJson["saveMode"] = null;
    }

    if (this.selectedSourceType !== "FILE" && this.selectedSourceType !== "STREAM") {
      let incrAttrObj = {};
      let refIncrAttrObj = {};
      refIncrAttrObj["uuid"] = this.incrementKey.uuid;
      refIncrAttrObj["type"] = this.incrementKey.type;
      incrAttrObj["ref"] = refIncrAttrObj;
      incrAttrObj["attributeId"] = this.incrementKey.attributeId;
      ingestJson["incrAttr"] = incrAttrObj;

      let splitByObj = {};
      let refSplitByObj = {};
      refSplitByObj["uuid"] = this.splitBy.uuid;
      refSplitByObj["type"] = this.splitBy.type;
      splitByObj["ref"] = refSplitByObj;
      splitByObj["attributeId"] = this.splitBy.attributeId;
      ingestJson["splitBy"] = splitByObj;
    }

    let filterInfoArray = [];
    if (this.filterTableArray != null) {
      for (let i = 0; i < this.filterTableArray.length; i++) {
        let filterInfo = {};
        filterInfo["logicalOperator"] = this.filterTableArray[i].logicalOperator;
        filterInfo["operator"] = this.filterTableArray[i].operator;
        filterInfo["operand"] = [];
        if (this.filterTableArray[i].lhsType == 'integer' || this.filterTableArray[i].lhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.filterTableArray[i].lhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'datapod' && this.selectedSourceType !== 'FILE') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'attribute' && this.selectedSourceType == 'FILE') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "attribute";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.filterTableArray[i].lhsAttribute;
          filterInfo["operand"][0] = operatorObj;
        }

        if (this.filterTableArray[i].rhsType == 'integer' || this.filterTableArray[i].rhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.filterTableArray[i].rhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][1] = operatorObj;

          if (this.filterTableArray[i].rhsType == 'integer' && this.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.filterTableArray[i].rhsAttribute1 + "and" + this.filterTableArray[i].rhsAttribute2;
            filterInfo["operand"][1] = operatorObj;
          }
        }
        else if (this.filterTableArray[i].rhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'function') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "function";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'paramlist') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "paramlist";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'dataset') {debugger
          let operatorObj = {};
          let ref = {}
          ref["type"] = "dataset";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType !== 'FILE') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "attribute";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        filterInfoArray[i] = filterInfo;
      }
    }
    ingestJson["filterInfo"] = filterInfoArray;

    let attributeTableArray = [];
    for (let i = 0; i < this.attributeTableArray.length; i++) {
      let attributeInfo = {};
      attributeInfo["attrMapId"] = this.attributeTableArray[i].attrMapId;

      let sourceAttrObj = {};
      let refObj = {};
      if (this.attributeTableArray[i].sourceType == 'string') {
        refObj["type"] = "simple";
        sourceAttrObj["ref"] = refObj;
        sourceAttrObj["value"] = this.attributeTableArray[i].sourceAttribute;
      }
      if (this.attributeTableArray[i].sourceType == 'datapod' && this.selectedSourceType !== "TABLE") {
        refObj["type"] = "attribute";
        sourceAttrObj["ref"] = refObj;
        sourceAttrObj["value"] = this.attributeTableArray[i].sourceAttribute;
      }
      else if (this.attributeTableArray[i].sourceType == 'datapod' && this.selectedSourceType !== "TABLE") {
        refObj["type"] = this.attributeTableArray[i].sourceType = "datapod";
        refObj["uuid"] = this.attributeTableArray[i].sourceAttribute.uuid;
        sourceAttrObj["ref"] = refObj;
        sourceAttrObj["attrId"] = this.attributeTableArray[i].sourceAttribute.attributeId;
      }
      else if (this.attributeTableArray[i].sourceType == 'formula') {
        refObj["type"] = this.attributeTableArray[i].sourceType = "formula";
        refObj["uuid"] = this.attributeTableArray[i].sourceAttribute.uuid;
        sourceAttrObj["ref"] = refObj;
      }
      else if (this.attributeTableArray[i].sourceType == 'function') {
        refObj["type"] = this.attributeTableArray[i].sourceType = "function";
        refObj["uuid"] = this.attributeTableArray[i].sourceAttribute.uuid;
        sourceAttrObj["ref"] = refObj;
      }
      attributeInfo["sourceAttr"] = sourceAttrObj;

      let targetAttr = {};
      let targetref = {};
      if (this.selectedTargetType != "FILE") {
        targetref["uuid"] = this.attributeTableArray[i].targetAttribute.uuid;
        targetref["type"] = this.attributeTableArray[i].targetAttribute.type;
        targetAttr["ref"] = targetref;
        targetAttr["attrId"] = this.attributeTableArray[i].targetAttribute.attributeId;
      }
      else {
        targetref["type"] = "attribute";
        targetAttr["ref"] = targetref;
        targetAttr["value"] = this.attributeTableArray[i].targetAttribute;
      }
      attributeInfo["targetAttr"] = targetAttr;
      attributeTableArray[i] = attributeInfo;
    }
    ingestJson["attributeMap"] = attributeTableArray;

    console.log(JSON.stringify(ingestJson));
    this._commonService.submit("ingest", ingestJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("ingest", response).subscribe(
        response => { this.onSuccessgetOneById(response); },
        error => console.log('Error :: ' + error))
    }
    else {
      this.msgs = [];
      this.isSubmit = "true"
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Ingest Rule Saved Successfully' });
      setTimeout(() => { this.goBack() }, 1000);
    }
  }

  onSuccessgetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "ingest", "execute").subscribe(
      response => { this.onSuccessExecute(response) },
      error => this.showError(error))
  }

  onSuccessExecute(response) {
    this.msgs = [];
    this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Saved and Submited Successfully' });
    setTimeout(() => { this.goBack() }, 1000);
  }

  showError(error) {
    console.log('Error::', + error);
    this.msgs = [];
    this.msgs.push({ severity: 'Failed', summary: 'Failed Message', detail: 'Rule Saved and failed' });
    setTimeout(() => { this.goBack() }, 1000);
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/ingest'])
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataIngestion/ingest', uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/dataIngestion/ingest', uuid, version, 'true']);
  }
}
