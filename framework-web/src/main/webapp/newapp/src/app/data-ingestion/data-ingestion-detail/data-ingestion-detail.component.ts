import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Version } from '../../metadata/domain/version';
import { DataIngestionService } from '../../metadata/services/dataIngestion.service';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { AttributeIO } from '../../metadata/domainIO/domain.attributeIO';
//import { IngestRule } from '../../metadata/domain/domain.ingestRule';
import { AppHelper } from '../../app.helper';
import { DataIngestRuleIO } from '../../metadata/domainIO/domain.dataIngestRuleIO';
import * as MetaTypeEnum from '../../metadata/enums/metaType';
import { IngestRule } from '../../metadata/domain/domain.ingestRule';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { Datasource } from '../../metadata/domain/domain.datasource';
import { Datapod } from '../../metadata/domain/domain.datapod';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { AttributeMapIO } from '../../metadata/domainIO/domain.attributeMapIO';
import { Formula } from '../../metadata/domain/domain.formula';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { FilterInfo } from '../../metadata/domain/domain.filterInfo';
import { FilterInfoIO } from '../../metadata/domainIO/domain.filterInfoIO';
import { Function } from '../../metadata/domain/domain.function';
import { ParamListHolder } from '../../metadata/domain/domain.paramListHolder';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AttributeRefHolder } from '../../metadata/domain/domain.attributeRefHolder';
import { SourceAttr } from '../../metadata/domain/domain.sourceAttr';
import { AttributeMap } from '../../metadata/domain/domain.attributeMap';
import { RoutesParam } from '../../metadata/domain/domain.routeParams';
@Component({
  selector: 'app-data-ingestion-detail',
  templateUrl: './data-ingestion-detail.component.html'
})
export class DataIngestionDetailComponent implements OnInit {
  dropIndex: any;
  dragIndex: any;
  showGraph: boolean;
  isHomeEnable: boolean;
  isNullArray: { 'value': string; 'label': string; }[];
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
  selectedAllFilterRow: boolean;
  FormulaArray: any[];
  attributesArray: any[];
  operators: { 'value': string; 'label': string; }[];
  logicalOperators: { 'value': string; 'label': string; }[];
  rhsTypeArray: { value: string; label: string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  filterTableArray: Array<FilterInfoIO>;
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
  allTargetDatasource: any;
  allSourceDatasource: any[];
  selectedRuleType: any;
  selectedTargetType: any;
  selectedSourceType: any;
  ruleTypes: { "value": string; "label": string; }[];
  tags: any[];
  createdBy: any;
  ingestData: IngestRule;
  progressbarWidth: string;
  continueCount: number;
  mode: any;
  version: any;
  id: any;
  uuid: any;
  selectedVersion: Version;
  VersionList: Array<DropDownIO>;
  breadcrumbDataFrom: { "caption": String; "routeurl": String; }[];
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  published: any;
  active: any;
  locked: any;
  targetHeader: any;
  metaType: any;
  moveToEnable: boolean;
  count: any[];
  txtQueryChanged: Subject<string> = new Subject<string>();
  txtQueryChanged1: Subject<string> = new Subject<string>();
  topDisabled: boolean;
  bottomDisabled: boolean;
  invalideRowNo0: boolean = false;
  invalideRowNo1: boolean = false;
  moveTo: number;

  showForm: boolean;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean = false;
  isAdd: boolean = false;
  isGraphInprogess: boolean;
  isGraphError: boolean;
  
  sourceHeader: boolean;
  datasetNotEmpty: boolean = true;
  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataInjectService: DataIngestionService, private appHelper: AppHelper) {
    this.metaType = MetaTypeEnum.MetaType;
    this.isSubmit = "false"
    this.ingestData = new IngestRule();
    this.sourceDs = {};
    this.targetDs = {};
    this.sourceTypeName = {};
    this.allSourceAttribute = []
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.topDisabled = false;
    this.bottomDisabled = false;

    this.showGraph = false;
    this.showForm = true;
    this.isHomeEnable = false;
    
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

    this.moveToEnable = false;
    this.count = [];

    this.txtQueryChanged
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.filterTableArray[index].selected = "";
        this.checkSelected(false, null);
        this.moveTo = null;
        this.invalideRowNo0 = false;
        this.invalideRowNo1 = false;
      });

    this.txtQueryChanged1
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.checkSelected(false, null);
        this.moveTo = null;
        this.invalideRowNo0 = false;
        this.invalideRowNo1 = false;
      });

    this.invalideRowNo0 = false;
    this.invalideRowNo1 = false;

    this.ruleTypes = [
      { "value": "FILE-FILE", "label": "File - File" },
      { "value": "FILE-TABLE", "label": "File - Table" },
      { "value": "TABLE-TABLE", "label": "Table - Table" },
      { "value": "TABLE-FILE", "label": "Table - File" },
      { "value": "STREAM-FILE", "label": "Stream - File" },
      { "value": "STREAM-TABLE", "label": "Stream - Table" }
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
      { 'value': '=', 'label': 'EQUAL(=)' },
      { 'value': '!=', 'label': 'NOT EQUAL(!=)' },
      { 'value': '<', 'label': 'LESS THAN(<)' },
      { 'value': '>', 'label': 'GREATER THAN(>)' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL(<=)' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL(>=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
      { 'value': 'IN', 'label': 'IN' },
      { 'value': 'NOT IN', 'label': 'NOT IN' },
      { 'value': 'IS', 'label': 'IS' }
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
    this.isNullArray = [
      { 'value': 'NULL', 'label': 'NULL' },
      { 'value': 'NOT NULL', 'label': 'NOT NULL' }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.uuid = param.id;
      this.version = param.version;
      this.mode = param.mode;
      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.uuid, this.version);
      }
      else {
        // this.isSubmitEnable = true;
        this.isEditInprogess = false;
        this.isEditError = false;
        this.ingestData = new IngestRule();
      }
      this.setMode(this.mode);
    });
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.ingestData.name;
  }

  onChangeAttributeTableType(index: any) {
    this.attributeTableArray[index].sourceAttribute = null;
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
    this._commonService.getAllVersionByUuid(MetaTypeEnum.MetaType.INGEST, this.uuid)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response: BaseEntity[]) {
    for (const i in response) {
      this.VersionList = [new DropDownIO];
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { 'label': '', 'uuid': '' }
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.VersionList[i] = ver;
    }
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  getOneByUuidAndVersion(uuid: any, version: any) {

    this.isEditInprogess = true;
    this.isEditError = false;
    this._dataInjectService.getOneByUuidAndVersion(uuid, version, MetaTypeEnum.MetaType.INGEST).subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response);
      },
      error => {
        console.log("Error::", +error)
        this.isEditError = false;
      }
    )
  }

  getDatasourceForFile(sourceType: any, TargetType: any) {
    this._dataInjectService.getDatasourceForFile(MetaTypeEnum.MetaType.DATASOURCE).subscribe(
      response => {
        this.onSuccessgetDatasourceForFile(response)
      },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForFile(response: Datasource[]) {
    if (this.selectedSourceType == "FILE") {
      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' }
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
        obj.value.version = response[i].version;
        temp[i] = obj;
      }
      this.allSourceDatasource = temp;
      console.log(JSON.stringify(this.allSourceDatasource));
    }
    if (this.selectedTargetType == "FILE") {
      this.allTargetDatasource = [new DropDownIO];
      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' };
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
        obj.value.version = response[i].version;
        temp[i] = obj;
      }
      this.allTargetDatasource = temp;
      console.log(JSON.stringify(this.allSourceDatasource));
    }
  }

  getDatasourceForTable() {
    this._dataInjectService.getDatasourceForTable(MetaTypeEnum.MetaType.DATASOURCE).subscribe(
      response => { this.onSuccessgetDatasourceForTable(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForTable(response: Datasource[]) {
    if (this.selectedSourceType == "TABLE") {
      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' }
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
        obj.value.version = response[i].version;
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
      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' }
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
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
              let temp = [new DropDownIO];
              for (const j in response) {
                let obj = new DropDownIO;
                obj.label = response[i].name;
                obj.value = { 'label': '', 'uuid': '' }
                obj.value.label = response[i].name;
                obj.value.uuid = response[i].uuid;
                temp[j] = obj;
              }
              this.allTargetDatasource.push(temp[i])
            }
            else {
              this.allTargetDatasource = [];
              let temp = [new DropDownIO];
              for (const j in response) {
                let obj = new DropDownIO;
                obj.label = response[i].name;
                obj.value = { 'label': '', 'uuid': '' }
                obj.value.label = response[i].name;
                obj.value.uuid = response[i].uuid;
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
    this._dataInjectService.getDatasourceForStream(MetaTypeEnum.MetaType.DATASOURCE).subscribe(
      response => { this.onSuccessgetDatasourceForStream(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatasourceForStream(response: Datasource[]) {
    if (this.selectedSourceType == "STREAM") {

      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' }
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
        obj.value.version = response[i].version;
        temp[i] = obj;
      }
      this.allSourceDatasource = temp;
    }
    if (this.selectedTargetType == "STREAM") {
      let temp = [new DropDownIO];
      for (const i in response) {
        let obj = new DropDownIO;
        obj.label = response[i].name;
        obj.value = { 'label': '', 'uuid': '' }
        obj.value.label = response[i].name;
        obj.value.uuid = response[i].uuid;
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
    //this.ingestData.ingestChg = "Y";
    this.selectedSourceType = this.selectedRuleType.split("-")[0];
    this.selectedTargetType = this.selectedRuleType.split("-")[1];
    this.isSourceFormatDisable = "this.selectedSourceType == 'FILE'" ? false : true;
    this.isTargetFormatDisable = "this.selectedTargetType == 'FILE'" ? false : true;

    if (this.selectedSourceType == 'FILE' && this.selectedTargetType == 'FILE' && this.mode == "true") {
      this.isAttributeMapDisable = true;
    }
    else if (this.selectedSourceType == 'FILE' && this.selectedTargetType == 'FILE' && this.mode !== "true") {
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
      this._dataInjectService.getDatapodByDatasource(MetaTypeEnum.MetaType.DATASOURCE, this.sourceDs.uuid).subscribe(
        response => { this.onSuccessgetDatapodByDatasource(response) },
        error => console.log("Error::", +error)
      )
    }
    if (this.sourceType == "dataset") {
      this._commonService.getAllLatest(MetaTypeEnum.MetaType.DATASET).subscribe(
        response => { this.onSuccessgetAllLatest(response) },
        error => console.log("Error::", +error)
      )
    }
  }

  onSuccessgetDatapodByDatasource(response: Datapod[]) {
    let temp = [new DropDownIO];
    for (const i in response) {
      let obj = new DropDownIO;
      obj.label = response[i].name;
      obj.value = { 'label': '', 'uuid': '' };
      obj.value.label = response[i].name;
      obj.value.uuid = response[i].uuid;
      temp[i] = obj;
    }
    this.sourceTypeNameArray = temp;
  }

  onSuccessgetAllLatest(response: BaseEntity[]) {
    let temp = [new DropDownIO];
    for (const i in response) {
      let obj = new DropDownIO;
      obj.label = response[i].name;
      obj.value = { 'label': '', 'uuid': '' };
      obj.value.label = response[i].name;
      obj.value.uuid = response[i].uuid;
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
  onSuccessgetAttributesByDatapod(response: MetaIdentifierHolder[]) {
    //this.attributeTableArray = [];
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO();
      obj.label = response[i].ref.name + "." + response[i].attrName;
      obj.value = {};
      obj.value.attributeId = response[i].attrId;
      obj.value.attrName = response[i].attrName;
      //  obj["value"]["type"] = response[i]['ref']['type'];
      obj.value.uuid = response[i].ref.uuid;
      //  obj["value"]["datapodname"] = response[i]['ref']['name'];
      obj.value.label = response[i].ref.name + "." + response[i].attrName;

      temp[i] = obj;
    }
    this.allSourceAttribute = temp;

    if (this.attributeTableArray.length == 0 && this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE") {
      for (let i = 0; i < this.allSourceAttribute.length; i++) {
        // var attributemapjson = {};

        var attributemapjson = new AttributeMapIO
        var obj = {}
        attributemapjson.attrMapId = i;
        attributemapjson.sourceType = "datapod";

        var sourceattribute = new AttributeIO
        sourceattribute.attributeId = this.allSourceAttribute[i].value.attributeId;
        sourceattribute.attrName = this.allSourceAttribute[i].value.attrName;
        sourceattribute.uuid = this.allSourceAttribute[i].value.uuid;
        sourceattribute.label = this.allSourceAttribute[i].value.label;
        attributemapjson.sourceAttribute = sourceattribute;

        attributemapjson.targetAttribute = null;
        attributemapjson.IsTargetAttributeSimple = 'true';
        this.attributeTableArray[i] = attributemapjson;
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onSuccessgetAttributesByDataset(response: MetaIdentifierHolder[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO();
      obj.label = response[i].ref.name + "." + response[i].attrName;
      obj.value = {};
      obj.value.label = response[i].ref.name + "." + response[i].attrName;
      obj.value.attributeId = response[i].attrId;
      // obj["value"]["type"] = response[i]['ref']['type'];
      obj.value.uuid = response[i].ref.uuid;
      obj.value.attrName = response[i].attrName;
      //  obj["value"]["dname"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttribute = temp;

    if (this.attributeTableArray.length == 0 && this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE") {
      for (let i = 0; i < this.allSourceAttribute.length; i++) {

        var attributemapjson = new AttributeMapIO;
        var obj = {}
        attributemapjson.attrMapId = i;
        attributemapjson.sourceType = "datapod";

        var sourceattribute = new AttributeIO;
        sourceattribute.attributeId = this.allSourceAttribute[i].value.attributeId;
        sourceattribute.attrName = this.allSourceAttribute[i].value.attrName;
        sourceattribute.uuid = this.allSourceAttribute[i].value.uuid;
        sourceattribute.label = this.allSourceAttribute[i].value.label;
        attributemapjson.sourceAttribute = sourceattribute;

        attributemapjson.targetAttribute = null;
        attributemapjson.IsTargetAttributeSimple = 'true';
        this.attributeTableArray[i] = attributemapjson;
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onSuccessgetAttributesByDatapod1(response: MetaIdentifierHolder[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO
      obj.label = response[i].attrName;
      obj.value = {};
      obj.value.label = response[i].attrName;
      obj.value.attributeId = response[i].attrId;
      // obj["value"]["attrName"] = response[i]['attrName'];
      obj.value.type = response[i].ref.type;
      obj.value.uuid = response[i].ref.uuid;
      obj.value.datapodname = response[i].ref.name;
      // obj["value"]["dname"] = response[i]['ref']['name'] + "." + response[i]['attrName'];
      temp[i] = obj;
    }
    this.allSourceAttributeForIncrmSplitBy = temp;
  }

  onSuccessgetAttributesByDataset1(response: MetaIdentifierHolder[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO
      obj.label = response[i].attrName;
      obj.value = {};
      obj.value.label = response[i].attrName;
      obj.value.attributeId = response[i].attrId;
      // obj["value"]["attrName"] = response[i]['attrName'];
      obj.value.type = response[i].ref.type;
      obj.value.uuid = response[i].ref.uuid;
      obj.value.datapodname = response[i].ref.name;
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

  onSuccessgetTopicList(response: any) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO
      obj.value = response[i]
      obj.label = response[i]
      temp[i] = obj;
    }
    this.streamSourceNameArray = temp;
  }

  onChangeTargetDs() {
    this._dataInjectService.getDatapodByDatasource(MetaTypeEnum.MetaType.DATASOURCE, this.targetDs.uuid).subscribe(
      response => { this.onSuccessgetDatapodByDatasourceTarget(response) },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetDatapodByDatasourceTarget(response: Datapod[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO
      obj.label = response[i].name;
      obj.value = { 'label': '', 'uuid': '' }
      obj.value.label = response[i].name;
      obj.value.uuid = response[i].uuid;
      temp[i] = obj;
    }
    this.targetNameArray = temp;
  }

  onSuccessgetFormulaByType(response: Formula[]) {
    let FormulaObj = new AttributeIO;
    let temp = [];
    for (const i in response) {
      FormulaObj.label = response[i].name;
      FormulaObj.value = {};
      FormulaObj.value.label = response[i].name;
      FormulaObj.value.uuid = response[i].uuid;
      temp[i] = FormulaObj;
    }
    this.FormulaArray = temp
  }

  onSuccessgetAllAttributeBySource(response: AttributeIO[]) {
    let temp1 = [];
    for (const i in response) {
      let attributeObj = new AttributeIO;
      attributeObj.label = response[i].dname;
      attributeObj.value = { 'label': '', 'uuid': '', 'attributeId': '' };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  addRow() {
    var filertable = new FilterInfoIO;
    if (this.filterTableArray == null || this.filterTableArray.length == 0) {
      this.filterTableArray = [];
      filertable.logicalOperator = '';
    }
    else {
      filertable.logicalOperator = this.logicalOperators[1].label;
    }
    var len = this.filterTableArray.length + 1;
    filertable.lhsType = "string"
    filertable.lhsAttribute = null
    filertable.operator = this.operators[0].value;
    filertable.rhsType = "string"
    filertable.rhsAttribute = null
    this.filterTableArray.splice(this.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [new FilterInfoIO];
    this.selectedAllFilterRow = false;
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
    if (!this.selectedAllFilterRow) {
      this.selectedAllFilterRow = true;
    }
    else {
      this.selectedAllFilterRow = false;
    }
    this.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFilterRow;
    });
  }

  addAttributeRow() {
    if (this.attributeTableArray == null) {
      this.attributeTableArray = [new AttributeMapIO];
    }
    var len = this.attributeTableArray.length + 1
    var attributetable = new AttributeMapIO();
    attributetable.attrMapId = len - 1;
    attributetable.sourceType = "string";
    attributetable.sourceAttribute = null
    attributetable.targetAttribute = null
    attributetable.IsTargetAttributeSimple = "true"
    this.attributeTableArray.splice(this.attributeTableArray.length, 0, attributetable);
  }
  removeAttributeRow() {
    let newDataList = [];
    this.selectedAllFilterRow = false;
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

  onChangeRhsType(index: any) {
    this.filterTableArray[index].rhsAttribute == null;
    if (this.filterTableArray[index].rhsType == 'formula') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetFormulaByType(response) },
            error => console.log("Error ::", error))
      }
    }
    else if (this.filterTableArray[index].rhsType == 'datapod') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
            error => console.log("Error ::", error))
      }
    }
    else if (this.filterTableArray[index].rhsType == 'function') {
      this._dataInjectService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == 'paramlist') {
      this._dataInjectService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == 'dataset') {
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.filterTableArray[index].rhsAttribute = rhsAttribute;
    }
    else {
      this.filterTableArray[index].rhsAttribute = null;
    }
  }

  onChangeLhsType(index: any) {
    this.filterTableArray[index].lhsAttribute == null;
    if (this.filterTableArray[index].lhsType == 'formula') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetFormulaByType(response) },
            error => console.log("Error ::", error))
      }
    }
    else if (this.filterTableArray[index].lhsType == 'datapod') {
      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
            error => console.log("Error ::", error))
      }
    }
    else {
      this.filterTableArray[index].lhsAttribute = null;
    }
  }
  onSuccessgetFunctionByCriteria(response: Function[]) {
    let temp = [];
    for (const i in response) {
      let attributeObj = new DropDownIO
      attributeObj.label = response[i].name;
      attributeObj.value = { 'uuid': '', 'label': '' };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].name;
      temp[i] = attributeObj
    }
    this.functionArray = temp;
  }
  onSuccessgetParamByApp(response: ParamListHolder[]) {
    let temp = [];
    for (const i in response) {
      let attributeObj = new AttributeIO();
      attributeObj.label = "app." + response[i].paramName;
      attributeObj.value = {};
      attributeObj.value.uuid = response[i].ref.uuid;
      attributeObj.value.attributeId = response[i].paramId;
      attributeObj.value.label = "app." + response[i].paramName;
      temp[i] = attributeObj
    }
    this.paramlistArray = temp;
  }

  onSuccessgetAttributesByDatapodTarget(response: MetaIdentifierHolder[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO;
      obj.label = response[i].ref.name + "." + response[i].attrName;
      obj.value = {};
      obj.value.uuid = response[i].ref.uuid;
      obj.value.label = response[i].ref.name + "." + response[i].attrName;
      obj.value.type = response[i].ref.type;
      obj.value.attributeId = response[i].attrId;
      obj.value.attrName = response[i].attrName;
      temp[i] = obj;
    }
    this.allSourceAttributeTarget = temp;
  }

  onChangeTargetNameTable() {
    this._dataInjectService.getAttributesByDatapod(MetaTypeEnum.MetaType.DATAPOD, this.targetNameForTable.uuid).subscribe(
      response => {
        this.onSuccessgetAttributesByDatapodTable(response),
          error => console.log("Error ::", +error)
      }
    )
  }

  onSuccessgetAttributesByDatapodTable(response: MetaIdentifierHolder[]) {
    let temp = [];
    for (const i in response) {
      let obj = new AttributeIO();
      obj.label = response[i].ref.name + "." + response[i].attrName;
      obj.value = {};
      obj.value.uuid = response[i].ref.uuid;
      obj.value.label = response[i].ref.name + "." + response[i].attrName;
      obj.value.type = response[i].ref.type;
      obj.value.attributeId = response[i].attrId;
      obj.value.attrName = response[i].attrName;
      temp[i] = obj;
    }
    this.allSourceAttributeTarget = temp;

    this.allTableSourceAttribute = this.allSourceAttribute;

    if (this.attributeTableArray.length == 0 && this.selectedSourceType != "STREAM") {
      // this.attributeTableArray = [];
      //this.attributeTableArray = this.allSourceAttributeTarget;
      for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
        var attributemapjson = new AttributeMapIO();
        var obj = {};
        attributemapjson.attrMapId = i;
        attributemapjson.sourceType = "datapod";
        attributemapjson.sourceAttribute = null;

        let targetattribute = new AttributeIO();
        targetattribute.uuid = this.allSourceAttributeTarget[i].value.uuid;
        targetattribute.label = this.allSourceAttributeTarget[i].value.label;
        targetattribute.type = this.allSourceAttributeTarget[i].value.type;
        targetattribute.attrName = this.allSourceAttributeTarget[i].value.attrName;
        targetattribute.attributeId = this.allSourceAttributeTarget[i].value.attributeId;
        attributemapjson.targetAttribute = targetattribute;

        attributemapjson.IsTargetAttributeSimple = "false";

        this.attributeTableArray[i] = attributemapjson;
      }
      this.attributeTableArray = this.attributeTableArray;
    }
  }

  onChangeOperator(index: any) {
    this.filterTableArray[index].rhsAttribute = null;
    if (this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS' ||
      this.filterTableArray[index].operator == 'IN' || this.filterTableArray[index].operator == 'NOT IN') {
      this.filterTableArray[index].rhsType = 'dataset';
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else if (this.filterTableArray[index].operator == 'IS') {
      this.filterTableArray[index].rhsType = 'string';
    }
    else {
      this.filterTableArray[index].rhsType = 'integer';
    }
  }

  onChangeAutoMode() {
    if (this.attributeTableArray != null) {
      let temp: Array<AttributeMapIO> = this.attributeTableArray;
      this.attributeTableArray = [];
      if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "ByOrder") {
        if (this.allSourceAttributeTarget) {
          for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
            let mapInfo = new AttributeMapIO();
            mapInfo.attrMapId = i;
            mapInfo.sourceType = "datapod";

            let sourceObj = new AttributeIO();
            if (this.allSourceAttribute.length > i) {
              mapInfo.sourceAttribute = new AttributeIO();
              mapInfo.sourceAttribute.attributeId = this.allSourceAttribute[i].value.attributeId;
              mapInfo.sourceAttribute.attrName = this.allSourceAttribute[i].value.attrName;
              mapInfo.sourceAttribute.uuid = this.allSourceAttribute[i].value.uuid;
              mapInfo.sourceAttribute.label = this.sourceTypeName.label + "." + this.allSourceAttribute[i].value.attrName;
            }
            else {
              mapInfo.sourceAttribute = null;
            }
            mapInfo.targetAttribute = new AttributeIO();
            mapInfo.targetAttribute.uuid = temp[i].targetAttribute.uuid;
            mapInfo.targetAttribute.label = temp[i].targetAttribute.label;
            mapInfo.targetAttribute.type = temp[i].targetAttribute.type;
            mapInfo.targetAttribute.attrName = temp[i].targetAttribute.attrName;
            mapInfo.targetAttribute.attributeId = temp[i].targetAttribute.attributeId;

            mapInfo.IsTargetAttributeSimple = "false";
            this.attributeTableArray[i] = mapInfo;
          }
          console.log(JSON.stringify(this.attributeTableArray));
        }
      }

      else if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "ByName") {
        for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
          let mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = "datapod";

          mapInfo.targetAttribute = new AttributeIO();
          mapInfo.targetAttribute.uuid = temp[i].targetAttribute.uuid;
          mapInfo.targetAttribute.label = temp[i].targetAttribute.label;
          mapInfo.targetAttribute.type = temp[i].targetAttribute.type;
          mapInfo.targetAttribute.attrName = temp[i].targetAttribute.attrName;
          mapInfo.targetAttribute.attributeId = temp[i].targetAttribute.attributeId;

          mapInfo.IsTargetAttributeSimple = "false";

          for (let j = 0; j < this.allSourceAttribute.length; j++) {
            if (this.allSourceAttribute[j].value.attrName == temp[i].targetAttribute.attrName) {
              mapInfo.sourceAttribute = new AttributeIO();
              mapInfo.sourceAttribute.attributeId = this.allSourceAttribute[j].value.attributeId;
              mapInfo.sourceAttribute.attrName = this.allSourceAttribute[j].value.attrName;
              mapInfo.sourceAttribute.uuid = this.allSourceAttribute[j].value.uuid;
              mapInfo.sourceAttribute.label = this.sourceTypeName.label + "." + this.allSourceAttribute[j].value.attrName;
              break;
            }
            else {
              mapInfo.sourceAttribute = null;
            }
          }
          this.attributeTableArray[i] = mapInfo;
        }
        console.log(JSON.stringify(this.attributeTableArray));
      }

      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "TABLE" && this.selectedAutoMode == "FromTarget") {
        for (var i = 0; i < this.allSourceAttributeTarget.length; i++) {
          var mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = "datapod";

          mapInfo.sourceAttribute = temp[i].targetAttribute.attrName;
          mapInfo.targetAttribute.uuid = temp[i].targetAttribute.uuid;
          mapInfo.targetAttribute.label = temp[i].targetAttribute.label;
          mapInfo.targetAttribute.type = temp[i].targetAttribute.type;
          mapInfo.targetAttribute.attrName = temp[i].targetAttribute.attrName;
          mapInfo.targetAttribute.attributeId = temp[i].targetAttribute.attributeId;

          mapInfo.IsTargetAttributeSimple = "false";

          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "TABLE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < this.allSourceAttribute.length; i++) {
          var mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = "datapod";

          mapInfo.sourceAttribute = new AttributeIO();
          mapInfo.sourceAttribute.uuid = temp[i].sourceAttribute.uuid;
          mapInfo.sourceAttribute.label = temp[i].sourceAttribute.label;
          mapInfo.sourceAttribute.attrName = temp[i].sourceAttribute.attrName;
          mapInfo.sourceAttribute.attributeId = temp[i].sourceAttribute.attributeId;

          mapInfo.targetAttribute = temp[i].sourceAttribute.attrName;
          mapInfo.IsTargetAttributeSimple = "true";

          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = temp[i].sourceType;

          if (temp[i].sourceType == 'string' || temp[i].sourceType == 'datapod') {
            mapInfo.sourceAttribute = temp[i].sourceAttribute;
            mapInfo.targetAttribute = temp[i].sourceAttribute;
          } else {
            let sourceAttribute = new AttributeIO();
            sourceAttribute.uuid = temp[i].sourceAttribute.uuid;
            sourceAttribute.label = temp[i].sourceAttribute.label;
            mapInfo.sourceAttribute = sourceAttribute;

            mapInfo.targetAttribute = temp[i].sourceAttribute.label;
          }
          mapInfo.IsTargetAttributeSimple = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "FILE" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromTarget") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = "datapod";
          mapInfo.sourceAttribute = temp[i].targetAttribute;
          mapInfo.targetAttribute = temp[i].targetAttribute;
          mapInfo.IsTargetAttributeSimple = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
      else if (this.selectedSourceType == "STREAM" && this.selectedTargetType == "FILE" && this.selectedAutoMode == "FromSource") {
        for (var i = 0; i < temp.length; i++) {
          var mapInfo = new AttributeMapIO();
          mapInfo.attrMapId = i;
          mapInfo.sourceType = "datapod";
          mapInfo.sourceAttribute = temp[i].sourceAttribute;
          mapInfo.targetAttribute = temp[i].sourceAttribute;
          mapInfo.IsTargetAttributeSimple = "true";
          this.attributeTableArray[i] = mapInfo;
        }
      }
    }
  }

  searchOption(index: any) {
    this.displayDialogBox = true;
    this._commonService.getAllLatest(MetaTypeEnum.MetaType.DATASET)
      .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBox(response: BaseEntity[]) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = new AttributeIO()
      dialogAttriObj.label = response[i].name;
      dialogAttriObj.value = {};
      dialogAttriObj.value.label = response[i].name;
      dialogAttriObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriObj;
    }
    this.dialogAttriArray = temp
    console.log(JSON.stringify(this.dialogAttriArray));
  }

  onChangeDialogAttribute() {
    this._commonService.getAttributesByDataset(MetaTypeEnum.MetaType.DATASET, this.dialogSelectName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response: MetaIdentifierHolder[]) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = {};
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index: any) {
    this.displayDialogBox = false;
    let rhsattribute = new AttributeIO()
    rhsattribute.label = this.dialogAttributeName.label;
    rhsattribute.uuid = this.dialogAttributeName.uuid;
    rhsattribute.attributeId = this.dialogAttributeName.attributeId;
    this.filterTableArray[index].rhsAttribute = rhsattribute;
    this.datasetNotEmpty = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  onSuccessgetOneByUuidAndVersion(response: DataIngestRuleIO) {
    this.ingestData = response.ingestRule;
    this.breadcrumbDataFrom[2].caption = response.ingestRule.name;
    this.createdBy = response.ingestRule.createdBy.ref.name;
    const version: Version = new Version();
    version.label = response.ingestRule.version
    version.uuid = response.ingestRule.uuid;
    this.selectedVersion = version;

    if (response.ingestRule.tags != null) {
      this.ingestData.tags = response.ingestRule.tags;
    }

    this.published = this.appHelper.convertStringToBoolean(response.ingestRule.published);
    this.active = this.appHelper.convertStringToBoolean(response.ingestRule.active);
    this.locked = this.appHelper.convertStringToBoolean(response.ingestRule.locked);

    this.selectedRuleType = response.ingestRule.type;
    this.onChangeRuleType();

    let sourceObj = new AttributeIO;
    sourceObj.uuid = response.ingestRule.sourceDatasource.ref.uuid;
    sourceObj.label = response.ingestRule.sourceDatasource.ref.name;
    sourceObj.version = response.ingestRule.sourceDatasource.ref.version;
    this.sourceDs = sourceObj;
    if (this.sourceDs.label == 'Stream') {
      this._dataInjectService.getTopicList(this.sourceDs.uuid, this.sourceDs.version).subscribe(
        response => { this.onSuccessgetTopicList(response) },
        error => console.log("Error::", +error))
    }
    if (response.ingestRule.sourceFormat != null) {
      this.selectedSourceFormat = response.ingestRule.sourceFormat
    }
    //if(response.sourceDetail.value != null){
    this.sourceName = response.ingestRule.sourceDetail.value;
    //}
    if (response.ingestRule.sourceExtn != null) {
      this.sourceExtn = response.ingestRule.sourceExtn.toLowerCase();
    }
    else{
      this.sourceExtn = null;
    }
     
    this.sourceHeader = this.appHelper.convertStringToBoolean(response.ingestRule.sourceHeader);
    this.sourceType = response.ingestRule.sourceDetail.ref.type;
    let sourceTypeNameObj = new AttributeIO();
    sourceTypeNameObj.uuid = response.ingestRule.sourceDetail.ref.uuid;
    sourceTypeNameObj.label = response.ingestRule.sourceDetail.ref.name;
    this.sourceTypeName = sourceTypeNameObj
    this.onChangeSourceType();

    this.getAllAttributeBySource1();

    if (response.ingestRule.incrAttr != null) {
      let incrementKeyObj = new AttributeIO;

      incrementKeyObj.attributeId = response.ingestRule.incrAttr.attrId;
      incrementKeyObj.label = response.ingestRule.incrAttr.attrName;
      incrementKeyObj.type = response.ingestRule.incrAttr.ref.type;
      incrementKeyObj.uuid = response.ingestRule.incrAttr.ref.uuid;
      incrementKeyObj.datapodname = response.ingestRule.incrAttr.ref.name;
      // incrementKeyObj["dname"] = response.incrAttr.ref.name + "." + response.incrAttr.attrName;
      this.incrementKey = incrementKeyObj;
    }
    this.getAllAttributeBySource();
    if (response.ingestRule.splitBy != null) {
      let splitByObj = new AttributeIO;
      splitByObj.attributeId = response.ingestRule.splitBy.attrId;
      splitByObj.attrName = response.ingestRule.splitBy.attrName;
      splitByObj.type = response.ingestRule.splitBy.ref.type;
      splitByObj.uuid = response.ingestRule.splitBy.ref.uuid;
      splitByObj.datapodname = response.ingestRule.splitBy.ref.name;
      splitByObj.dname = response.ingestRule.splitBy.ref.name + "." + response.ingestRule.splitBy.attrName;
      this.splitBy = splitByObj;
    }

    let targetObj = new AttributeIO;
    targetObj.uuid = response.ingestRule.targetDatasource.ref.uuid;
    targetObj.label = response.ingestRule.targetDatasource.ref.name;
    this.targetDs = targetObj;

    if (response.ingestRule.targetFormat != null) {
      this.selectedTargetFormat = response.ingestRule.targetFormat
    }
    this.targetName = response.ingestRule.targetDetail.value;

    let targetNameObj = new AttributeIO;
    targetNameObj.uuid = response.ingestRule.targetDetail.ref.uuid;
    targetNameObj.label = response.ingestRule.targetDetail.ref.name;
    this.targetNameForTable = targetNameObj;
    this.onChangeTargetDs();

    if (response.ingestRule.targetExtn != null) {
      this.targetExtn = response.ingestRule.targetExtn.toLowerCase();
    }
    else
      this.targetExtn = null;
    this.targetHeader = this.appHelper.convertStringToBoolean(response.ingestRule.targetHeader);

    this.saveMode = response.ingestRule.saveMode;

    this.runParams = response.ingestRule.runParams;

    if (this.sourceTypeName.uuid != null && this.sourceType != null) {
      this._commonService.getFormulaByType(this.sourceTypeName.uuid, this.sourceType)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
          error => console.log("Error ::", error))

      if (this.selectedSourceType == 'TABLE') {
        this._commonService.getAllAttributeBySource(this.sourceTypeName.uuid, this.sourceType)
          .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
            error => console.log("Error ::", error))
      }
    }
    if (response.filterInfo !== null) {
      this._dataInjectService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))

      this._dataInjectService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error))
    }

    this.filterTableArray = response.filterInfo
    if (response.attributeMap !== null) {
      this._dataInjectService.getAttributesByDatapod(this.sourceType, this.sourceTypeName.uuid)
        .subscribe(response => { this.onSuccessgetAttributesByDatapod(response) },
          error => console.log("Error::", +error))

      this._dataInjectService.getAttributesByDatapod(MetaTypeEnum.MetaType.DATAPOD, this.targetNameForTable.uuid)
        .subscribe(response => { this.onSuccessgetAttributesByDatapodTarget(response) },
          error => console.log("Error::", +error))
    }
    this.attributeTableArray = response.attributeMap;
    console.log(JSON.stringify(this.attributeTableArray))
    this.isEditInprogess = false;
  }
  
  submitIngest() {
    this.isSubmit = "true"
    var upd_tag = 'N'
    let ingestJson = new IngestRule();
    ingestJson.uuid = this.ingestData.uuid
    ingestJson.name = this.ingestData.name
    ingestJson.desc = this.ingestData.desc;
    ingestJson.tags = this.ingestData.tags
    ingestJson.active = this.active == true ? 'Y' : "N"
    ingestJson.published = this.published == true ? 'Y' : "N"
    ingestJson.locked = this.locked == true ? 'Y' : "N"
    ingestJson.type = this.selectedRuleType;
    ingestJson.runParams = this.runParams;
    this.selectedSourceType = this.selectedRuleType.split("-")[0];
    this.selectedTargetType = this.selectedRuleType.split("-")[1];
    //if(this.selectedSourceType == "File"){
    ingestJson.sourceExtn = this.sourceExtn;
    ingestJson.sourceHeader = this.sourceHeader == true ? 'Y' : "N";
    ingestJson.ignoreCase = this.ingestData.ignoreCase;
    // }
    // if(this.selectedTargetType == "File"){
    ingestJson.targetHeader = this.targetHeader == true ? 'Y' : "N";
    ingestJson.targetExtn = this.targetExtn;
    // }

    let sourceDatasource = new MetaIdentifierHolder();
    let sourceDataRef = new MetaIdentifier();

    sourceDataRef.uuid = this.sourceDs.uuid;
    sourceDataRef.type = "datasource";
    sourceDatasource.ref = sourceDataRef;
    ingestJson.sourceDatasource = sourceDatasource;
    ingestJson.sourceFormat = this.selectedSourceFormat;

    let sourceDetail = new MetaIdentifierHolder();
    let sourceDetailRef = new MetaIdentifier();
    if (this.selectedSourceType == "FILE" || this.selectedSourceType == "STREAM") {
      sourceDetailRef.type = "simple";
      sourceDetail.ref = sourceDetailRef;
      sourceDetail.value = this.sourceName;
    } else {
      sourceDetailRef.type = this.sourceType
      sourceDetailRef.uuid = this.sourceTypeName.uuid;
      sourceDetail.ref = sourceDetailRef;
    }
    ingestJson.sourceDetail = sourceDetail;

    let targetDatasource = new MetaIdentifierHolder();
    let targetDatasourceRef = new MetaIdentifier();
    targetDatasourceRef.uuid = this.targetDs.uuid;
    targetDatasourceRef.type = "datasource";
    targetDatasource.ref = targetDatasourceRef;
    ingestJson.targetDatasource = targetDatasource;
    ingestJson.targetFormat = this.selectedTargetFormat;

    let targetDetails = new MetaIdentifierHolder();
    let targetDetailsRef = new MetaIdentifier();
    if (this.selectedTargetType == "FILE") {
      targetDetailsRef.type = "simple";
      targetDetails.ref = targetDetailsRef;
      targetDetails.value = this.targetName;
    }
    else {
      targetDetailsRef.type = "datapod";
      targetDetailsRef.uuid = this.targetNameForTable.uuid;
      targetDetails.ref = targetDetailsRef;
    }
    ingestJson.targetDetail = targetDetails;

    if (this.saveMode) {
      ingestJson.saveMode = this.saveMode;
    }
    else {
      ingestJson.saveMode = null;
    }

    if (this.selectedSourceType !== "FILE" && this.selectedSourceType !== "STREAM") {
      let incrAttrObj = new AttributeRefHolder();
      let refIncrAttrObj = new MetaIdentifier();
      refIncrAttrObj.uuid = this.incrementKey.uuid;
      refIncrAttrObj.type = this.incrementKey.type;
      incrAttrObj.ref = refIncrAttrObj;
      incrAttrObj.attrId = this.incrementKey.attributeId;
      ingestJson.incrAttr = incrAttrObj;

      let splitByObj = new AttributeRefHolder();
      let refSplitByObj = new MetaIdentifier();
      refSplitByObj.uuid = this.splitBy.uuid;
      refSplitByObj.type = this.splitBy.type;
      splitByObj.ref = refSplitByObj;
      splitByObj.attrId = this.splitBy.attributeId;
      ingestJson.splitBy = splitByObj;
    }
    else {
      ingestJson.incrAttr = null;
      ingestJson.splitBy = null;
    }

    let filterInfoArray = []; 
    if (this.filterTableArray != null) {
      for (let i = 0; i < this.filterTableArray.length; i++) {
        let filterInfo = new FilterInfo();
        filterInfo.display_seq = "i";
        filterInfo.logicalOperator = this.filterTableArray[i].logicalOperator;
        filterInfo.operator = this.filterTableArray[i].operator;
        filterInfo.operand = [];
        if (this.filterTableArray[i].lhsType == 'integer' || this.filterTableArray[i].lhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "simple";
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].lhsAttribute;
          operatorObj.attributeType = "string";
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'formula') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "formula";
          ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'datapod' && this.selectedSourceType !== 'FILE') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "datapod";
          ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == 'datapod' && this.selectedSourceType == 'FILE') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "attribute";
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].lhsAttribute;
          filterInfo.operand[0] = operatorObj;
        }

        if (this.filterTableArray[i].rhsType == 'integer' || this.filterTableArray[i].rhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "simple";
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].rhsAttribute;
          operatorObj.attributeType = "string"
          filterInfo.operand[1] = operatorObj;

          if (this.filterTableArray[i].rhsType == 'integer' && this.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = "simple";
            operatorObj.ref = ref;
            operatorObj.value = this.filterTableArray[i].rhsAttribute1 + "and" + this.filterTableArray[i].rhsAttribute2;
            filterInfo.operand[1] = operatorObj;
          }
        }
        else if (this.filterTableArray[i].rhsType == 'formula') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "formula";
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'function') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "function";
          ref.type = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'paramlist') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "paramlist";
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'dataset') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "dataset";
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType !== 'FILE') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "datapod";
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "attribute";
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        filterInfoArray[i] = filterInfo;
      }
    }
    ingestJson["filterInfo"] = filterInfoArray;

    let attributeTableArray = []; 
    if (this.attributeTableArray != null) {
      for (let i = 0; i < this.attributeTableArray.length; i++) {
        let attributeInfo = new AttributeMap();
        attributeInfo.attrMapId = this.attributeTableArray[i].attrMapId;

        let sourceAttrObj;
        let refObj;
        if (this.attributeTableArray[i].sourceType == 'string') {
          sourceAttrObj = new SourceAttr();
          refObj = new MetaIdentifier();
          refObj.type = "simple";
          sourceAttrObj.ref = refObj;
          sourceAttrObj.value = this.attributeTableArray[i].sourceAttribute;
        }
        if (this.attributeTableArray[i].sourceType == 'datapod' && this.selectedSourceType !== "TABLE") {
           sourceAttrObj = new SourceAttr();
           refObj = new MetaIdentifier();
          refObj.type = "attribute";
          sourceAttrObj.ref = refObj;
          sourceAttrObj.value = this.attributeTableArray[i].sourceAttribute;
        }
        else if (this.attributeTableArray[i].sourceType == 'datapod' && this.selectedSourceType !== "TABLE") {
           sourceAttrObj = new AttributeRefHolder();
           refObj = new MetaIdentifier();
          refObj.type = this.attributeTableArray[i].sourceType = "datapod";
          refObj.uuid = this.attributeTableArray[i].sourceAttribute.uuid;
          sourceAttrObj.ref = refObj;
          sourceAttrObj.attrId = this.attributeTableArray[i].sourceAttribute.attributeId;
        }
        else if (this.attributeTableArray[i].sourceType == 'formula') {
           sourceAttrObj = new SourceAttr();
           refObj = new MetaIdentifier();
          refObj.type = this.attributeTableArray[i].sourceType = "formula";
          refObj.uuid = this.attributeTableArray[i].sourceAttribute.uuid;
          sourceAttrObj.ref = refObj;
        }
        else if (this.attributeTableArray[i].sourceType == 'function') {
           sourceAttrObj = new SourceAttr();
           refObj = new MetaIdentifier();
          refObj.type = this.attributeTableArray[i].sourceType = "function";
          refObj.uuid = this.attributeTableArray[i].sourceAttribute.uuid;
          sourceAttrObj.ref = refObj;

        }
        attributeInfo["sourceAttr"] = sourceAttrObj;

        let targetAttr = new AttributeRefHolder();;
        let targetref = new MetaIdentifier();
        if (this.selectedTargetType != "FILE") {
          targetref.uuid = this.attributeTableArray[i].targetAttribute.uuid;
          targetref.type = this.attributeTableArray[i].targetAttribute.type;
          targetAttr.ref = targetref;
          targetAttr.attrId = this.attributeTableArray[i].targetAttribute.attributeId;
        }
        else {
          targetref.type = "attribute";
          targetAttr.ref = targetref;
          targetAttr.value = this.attributeTableArray[i].targetAttribute;
        }
        attributeInfo["targetAttr"] = targetAttr;
        attributeTableArray[i] = attributeInfo;
      }
    }

    ingestJson.attributeMap = attributeTableArray;

    console.log(JSON.stringify(ingestJson));
    this._commonService.submit("ingest", ingestJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response: any) {
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
    this.router.navigate(['app/list/ingest'])
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataIngestion/ingest', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
    this.showForm = true;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    this.showForm = false
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.uuid, this.version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  // onAttrRowDown(index) {
  //   var rowTempIndex = this.filterTableArray[index];
  //   var rowTempIndexPlus = this.filterTableArray[index + 1];
  //   this.filterTableArray[index] = rowTempIndexPlus;
  //   this.filterTableArray[index + 1] = rowTempIndex;
  //   this.isSubmit = "true"

  // }

  // onAttrRowUp(index) {
  //   var rowTempIndex = this.filterTableArray[index];
  //   var rowTempIndexMines = this.filterTableArray[index - 1];
  //   this.filterTableArray[index] = rowTempIndexMines;
  //   this.filterTableArray[index - 1] = rowTempIndex;
  //   this.isSubmit = "true"
  // }
  // dragStart(event, data) {
  //   console.log(event)
  //   console.log(data)
  //   this.dragIndex = data
  // }
  // dragEnd(event) {
  //   console.log(event)
  // }
  // drop(event, data) {
  //   if (this.mode == 'false') {
  //     this.dropIndex = data
  //     // console.log(event)
  //     // console.log(data)
  //     var item = this.filterTableArray[this.dragIndex]
  //     this.filterTableArray.splice(this.dragIndex, 1)
  //     this.filterTableArray.splice(this.dropIndex, 0, item)
  //     this.isSubmit = "true"
  //   }

  // }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {
        // let old_index = i;
        // this.array_move(this.filterTableArray, old_index, new_index);
        // if (range) {
        //   this.txtQueryChanged.next(event);
        // }
        // else if (new_index == 0 || new_index == 1) {
        //   this.filterTableArray[0].logicalOperator = "";
        //   if (!this.filterTableArray[1].logicalOperator) {
        //     this.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
        //   }
        //   this.filterTableArray[new_index].selected = "";
        //   this.checkSelected(false,old_index);
        // }
        // else if (new_index == this.filterTableArray.length - 1) {
        //   this.filterTableArray[0].logicalOperator = "";
        //   this.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
        //   this.filterTableArray[i].selected = "";
        //   this.checkSelected(false,old_index);
        // }
        // break;

        if (new_index < 0) {
          this.invalideRowNo0 = true;
          this.txtQueryChanged1.next(event);
          // this.filterTableArray[i].selected = "";
        }
        else if (new_index >= this.filterTableArray.length) {
          this.invalideRowNo1 = true;
          this.txtQueryChanged1.next(event);
          // this.filterTableArray[i].selected = "";
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.filterTableArray, old_index, new_index);

          if (range) {
            this.txtQueryChanged.next(event);
          }
          else if (new_index == 0 || new_index == 1) {
            this.filterTableArray[0].logicalOperator = "";
            if (!this.filterTableArray[1].logicalOperator) {
              this.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.filterTableArray[new_index].selected = "";
            this.checkSelected(false, old_index);
          }
          else if (new_index == this.filterTableArray.length - 1) {
            this.filterTableArray[0].logicalOperator = "";
            this.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            this.filterTableArray[i].selected = "";
            this.checkSelected(false, old_index);
          }
          break;
        }
      }
    }
  }
  array_move(arr, old_index, new_index) {
    while (old_index < 0) {
      old_index += arr.length;
    }
    while (new_index < 0) {
      new_index += arr.length;
    }
    if (new_index >= arr.length) {
      var k = new_index - arr.length + 1;
      while (k--) {
        arr.push(undefined);
      }
    }
    arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
    return arr;
  }
  checkSelected(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
    }
    else {
      this.count.pop();
    }
    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.filterTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }
}
