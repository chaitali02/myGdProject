import { CommonService } from './../../metadata/services/common.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Component, OnInit, ViewEncapsulation, Inject, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { DatasetService } from '../../metadata/services/dataset.service';
import { NgOption } from '@ng-select/ng-select';
import { SelectItem } from 'primeng/primeng';
import { DependsOn } from './dependsOn'
import { NgForm } from '@angular/forms';
import { Version } from './../../metadata/domain/version'
import { AppMetadata } from '../../app.metadata';
import { SESSION_STORAGE, WebStorageService } from 'angular-webstorage-service'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { MetaType } from '../../metadata/enums/metaType';
import { AttributeSourceIO } from '../../metadata/domainIO/domain.AttributeSourceIO';
import { AttributeIO } from '../../metadata/domainIO/domain.attributeIO';
import { DatasetIO } from '../../metadata/domainIO/domain.datasetIO';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { AppHelper } from '../../app.helper';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Dataset } from '../../metadata/domain/domain.dataset';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { FilterInfoIO } from '../../metadata/domainIO/domain.filterInfoIO';
import { SourceAttr } from '../../metadata/domain/domain.sourceAttr';
import { AttributeSource } from '../../metadata/domain/domain.attributeSource';
import { AttributeRefHolder } from '../../metadata/domain/domain.attributeRefHolder';
import { RoutesParam } from '../../metadata/domain/domain.routeParams';
import { ParamListHolder } from '../../metadata/domain/domain.paramListHolder';
import { Function } from '../../metadata/domain/domain.function';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.template.html',
  styleUrls: ['./dataset.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DatasetComponent implements OnInit {
  dropIndexSourceAttr: any;
  dragIndexSourceAttr: any;
  dropIndex: any;
  dragIndex: any;
  isHomeEnable: boolean;
  showGraph: boolean;
  duplicateString: any;
  isDuplication: boolean;
  chkDuplicate: boolean = false;
  progressbarWidth: string;
  continueCount: number;
  paramlistArray: any[];
  functionArray: any[];
  dialogAttriNameArray: any[];
  dialogSelectName: any;
  dialogAttributeName: any;
  dialogAttriArray: any[];
  displayDialogBox: boolean;
  sessionData: any[];
  lhsFormulaArray: any;
  rhsFormulaArray: any[];
  attributesArray: any;
  rhsTypeArray: { value: string; label: string }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  columnOptions: any[];
  cols: any[];
  colsdata: any;
  IsError: boolean = false;
  IsTableShow: boolean = false;
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
  VersionList: Array<DropDownIO>;
  selectedVersion: Version;
  isSubmitEnable1: any;
  baseUrl: any;
  metaType = MetaType;
  // // myNewForm: any
  // @ViewChild('myNewForm') public myNewForm: NgForm;
  // private myNewForm: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  locked: any;

  moveToEnable: boolean;
  count: any[];
  txtQueryChangedFilter: Subject<string> = new Subject<string>();
  resetTableTopBottom: Subject<string> = new Subject<string>();
  topDisabled: boolean;
  bottomDisabled: boolean;
  invalideMinRow: boolean = false;
  invalideMaxRow: boolean = false;
  moveTo: number;

  moveToEnableAttr: boolean;
  countAttr: any[];
  txtQueryChangedAttr: Subject<string> = new Subject<string>();
  resetTableTopBottomAttr: Subject<string> = new Subject<string>();
  topDisabledAttr: boolean;
  bottomDisabledAttr: boolean;
  invalideMinRowAttr: boolean = false;
  invalideMaxRowAttr: boolean = false;
  moveToAttr: number;
  datasetRowIndex: any;
  datasetNotEmpty: boolean = true;

  showForm: boolean;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean = false;
  isAdd: boolean = false;
  isGraphInprogess: boolean;
  isGraphError: boolean;
  checkAll: boolean;
  downloadUuid: any;
  downloadVersion: any;
  showDownloadModel: boolean = false;
  downloadFormat: string;
  numRows: any;
  downloadFormatArray: { "value": string; "label": string; }[];

  constructor(private http: Http, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _datasetService: DatasetService, private activeroute: ActivatedRoute, @Inject(SESSION_STORAGE) private storage: WebStorageService, private appHelper: AppHelper) {
    this.baseUrl = config.getBaseUrl();
    //this.myNewForm = {}
    this.numRows = 100;
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.showdatapod = true;
    this.showGraph = false;
    this.isHomeEnable = false;
    this.dataset = {};
    this.isSubmitEnable1 = true;
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.dialogAttributeName["label"] = "-Select-";
    this.dataset.filterTableArray = []
    this.sourcedata = {}
    this.dialogSelectName = {}
    this.attributesArray = [];
    this.dataset.active = true;

    this.showGraph = false;
    this.showForm = true;

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
      { 'value': 'IS', 'label': 'IS' }
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]

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
      { 'value': 'relation', 'label': 'relation' },
      { 'value': 'dataset', 'label': 'dataset' }
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
      { value: 'dataset', label: 'dataset' },
      { value: 'function', label: 'function' },
      { value: 'paramlist', label: 'paramlist' }
    ]

    this.downloadFormatArray = [
      { "value": "excel", "label": "excel" }
    ];

    this.moveToEnable = false;
    this.count = [];

    this.txtQueryChangedFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.dataset.filterTableArray) {
          if (this.dataset.filterTableArray[i].hasOwnProperty("selected"))
            this.dataset.filterTableArray[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.resetTableTopBottom
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });
    this.invalideMinRow = false;
    this.invalideMaxRow = false;
    this.topDisabled = false;
    this.bottomDisabled = false;

    this.moveToEnableAttr = false;
    this.countAttr = [];

    this.txtQueryChangedAttr
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.dataset.attributeTableArray) {
          if (this.dataset.attributeTableArray[i].hasOwnProperty("selected"))
            this.dataset.attributeTableArray[i].selected = false;
        }
        this.moveToAttr = null;
        this.checkSelectedAttr(false, null);
        this.invalideMinRowAttr = false;
        this.invalideMaxRowAttr = false;
      });

    this.resetTableTopBottomAttr
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.moveToAttr = null;
        this.checkSelectedAttr(false, null);
        this.invalideMinRowAttr = false;
        this.invalideMaxRowAttr = false;
      });
    this.invalideMinRowAttr = false;
    this.invalideMaxRowAttr = false;
    this.topDisabledAttr = false;
    this.bottomDisabledAttr = false;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;
      // if (this.mode !== undefined) {
      //   this.getOneByUuidAndVersion(this.id, this.version);
      //   this.getAllVersionByUuid();
      // }
      this.getFromLocal(0);

      this.setMode(this.mode);
    })
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
    this.breadcrumbDataFrom[2].caption = this.dataset.name;
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dataset']);
  }
  countContinue() {
    if (this.continueCount == 3) {
      if (this.isDuplication == true) {
        return true;
      }
    }
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }
  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._datasetService.getOneByUuidAndVersion(id, version, this.metaType.DATASET)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error::", +error)
          this.isEditError = false;
        })
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.DATASET, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid2() {
    this._commonService.getAllVersionByUuid(this.metaType.DATASET, this.dataset.uuid)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response: DatasetIO) {
    this.dataset = response.datasetData;
    this.datasetCompare = response;
    this.dataset.uuid = response.datasetData.uuid;
    this.dataset.createdBy = response.datasetData.createdBy.ref.name;

    if (response.datasetData.tags != null) {
      this.dataset.tags = response.datasetData.tags;
    }
    this.dataset.published = this.appHelper.convertStringToBoolean(response.datasetData.published);
    this.dataset.active = this.appHelper.convertStringToBoolean(response.datasetData.active);
    this.dataset.locked = this.appHelper.convertStringToBoolean(response.datasetData.locked);

    const version: Version = new Version();
    version.label = response.datasetData.version
    version.uuid = response.datasetData.uuid;
    this.dataset.selectedVersion = version;

    console.log(JSON.stringify(this.dataset.version));

    this.breadcrumbDataFrom[2].caption = this.dataset.name;
    this.dataset.source = response.datasetData.dependsOn.ref.type;
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response.datasetData.dependsOn.ref.name;
    dependOnTemp.uuid = response.datasetData.dependsOn.ref.uuid;
    this.dataset.sourcedata = dependOnTemp;

    this._commonService.getAllLatest(this.dataset.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )

    if (response.filterInfo !== null) {
      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
          error => console.log("Error ::", error))

      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs1(response) },
          error => console.log("Error ::", error))

      this._datasetService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))

      this._datasetService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error))
    }

    this.dataset.filterTableArray = response.filterInfo

    for (const i in response.attributeInfo) {
      if (response.attributeInfo[i].sourceAttributeType.value == "datapod") {
        this.getAllAttributeBySource();
      }
      if (response.attributeInfo[i].sourceAttributeType.value == "expression") {
        this.getAllExpression(false, 0)
      }
      if (response.attributeInfo[i].sourceAttributeType.value == "formula") {
        this.getAllFormula(false, 0);
      }
      if (response.attributeInfo[i].sourceAttributeType.value == "function") {
        this.getAllFunctions(false, 0);
      }
    }
    this.dataset.attributeTableArray = response.attributeInfo

    this.isEditInprogess = false;
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

  OnSuccesgetAllVersionByUuid(response: BaseEntity[]) {
    let ver = new DropDownIO();
    this.VersionList = [];
    for (const i in response) {
      ver.label = response[i].version;
      ver.value = { 'label': '', 'uuid': '' }
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
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
    this.dataset.filterTableArray = [];
    this.dataset.attributeTableArray = [];
    this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source).subscribe(
      response => {
        this.OnSuccesgetAllAttributeBySource(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response1) {
    let temp = []
    for (const i in response1) {
      let obj = new DropDownIO;
      obj.label = response1[i].name;
      obj.value = { 'label': '', 'uuid': '' };
      obj.value.label = response1[i].name;
      obj.value.uuid = response1[i].uuid;
      temp[i] = obj;
    }
    this.allNames = temp
    //this.getAllAttributeBySource()
  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    this.lhsdatapodattributefilter = response;
    let temp = []
    for (const i in response) {

      let attributeObj = new AttributeIO;
      attributeObj.label = response[i].dname;
      attributeObj.value = { 'name': '', 'id': '', 'label': '', 'uuid': '', 'attributeId': '' };
      attributeObj.value.id = response[i].id;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;

      attributeObj.value.name = response[i].name;
      attributeObj.value.datapodname = response[i].datapodname;

      attributeObj.value.uuid = response[i].uuid;
      temp[i] = attributeObj;
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
    this._datasetService.getAllLatestFunction(this.metaType.FUNCTION, "N").subscribe(
      response => { this.onSuccessFunction(response, defaulfMode, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response, defaulfMode, index) {
    let temp = []
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      temp[n] = allname;
    }
    this.ruleLoadFunction = temp
    if (defaulfMode == true) {
      let sourcefunction = new AttributeIO();
      sourcefunction.uuid = this.ruleLoadFunction[0].value.uuid;
      sourcefunction.label = this.ruleLoadFunction[0].label;
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
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
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
  }

  onSelectExpression(event) {
    if (event.label == '---CreateNew---') {
      this.saveInLocal(0, this.dataset);
      this.router.navigate(['../../../../expressionDataset', this.dataset.source, this.dataset.sourcedata.label, this.dataset.sourcedata.uuid], { relativeTo: this.activatedRoute });
    }
  }

  onSelectFormula(event) {
    if (event.label == '---CreateNew---') {
      this.saveInLocal(0, this.dataset);
      this.router.navigate(['../../../../formulaDataset', this.dataset.source, this.dataset.sourcedata.label, this.dataset.sourcedata.uuid], { relativeTo: this.activatedRoute });
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
    this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.metaType.FORMULA).subscribe(
      response => { this.onSuccessgetAllFormula(response, defaulfMode, index) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessgetAllFormula(response, defaultMode, index) {
    //this.allMapFormula = response
    let temp = []
    for (const n in response) {

      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      temp[n] = allname;
    }
    this.allMapFormula = temp;
    if (defaultMode == true) {
      if (this.allMapFormula != null && this.allMapFormula.length > 0) {
        let sourceformula = new AttributeIO();
        sourceformula.uuid = this.allMapFormula[0].value.uuid;
        sourceformula.label = this.allMapFormula[0].label;
        this.dataset.attributeTableArray[index].sourceformula = sourceformula;
      }
    }

    this.allMapFormula.splice(0, 0, {
      "label": "---CreateNew---",
      "value": { "label": "---CreateNew---", "uuid": "01" }
    });
  }

  addRow() {
    var filtertable = new FilterInfoIO;
    if (this.dataset.filterTableArray == null || this.dataset.filterTableArray.length == 0) {
      this.dataset.filterTableArray = [];
      filtertable.logicalOperator = '';
    }
    else {
      filtertable.logicalOperator = this.logicalOperators[1].label;
    }
    var len = this.dataset.filterTableArray.length + 1;
    filtertable.lhsType = "string"
    filtertable.lhsAttribute = null
    filtertable.operator = this.operators[0].value;
    filtertable.rhsType = "string"
    filtertable.rhsAttribute = null
    this.dataset.filterTableArray.splice(this.dataset.filterTableArray.length, 0, filtertable);

    this.checkSelected(false, null);
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

    this.count = [];
    this.checkSelected(false, null);
    this.dataset.filterTableArray = newDataList;
    this.checkAll = false;
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
    var that = this;
    if (this.dataset.attributeTableArray == null) {
      this.dataset.attributeTableArray = [];
    }
    let len = this.dataset.attributeTableArray.length + 1;
    let attrinfo = new AttributeSourceIO();
    attrinfo.name = "attribute" + len;
    attrinfo.id = len - 1;
    attrinfo.sourceAttributeType = { "value": "string", "label": "string" };
    attrinfo.isSourceAtributeSimple = true;
    attrinfo.isSourceAtributeDatapod = false;
    this.isDublication(this.dataset.attributeTableArray, attrinfo, function (err, obj, arr) {

      if (err) {
        console.log(err)
      }
      if (obj) {
        console.log("aaaaa0", obj)
        that.dataset.attributeTableArray.push(obj);
      }
      if (arr) {
        that.dataset.attributeTableArray = arr;
      }
    });
    //this.dataset.attributeTableArray.splice(this.dataset.attributeTableArray.length, 0, attrinfo);
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
    this.onChangeSourceName(index);
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
    this.showGraph = false;
    this.graphDataStatus = false;
    this.showgraphdiv = false;
    this.showForm = false;
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
    this.IsError = false;
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
    for (const n in response) {
      let obj = new AttributeIO();
      obj.label = response[n].name;
      obj.value = {};
      obj.value.label = response[n].name;
      obj.value.uuid = response[n].uuid;
      this.lhsFormulaArray[n] = obj;
    }
  }

  onSuccessgetAllAttributeBySourceLhs(response) {

    let temp1 = [];
    for (const n in response) {
      let obj = new AttributeIO();
      obj.label = response[n].name;
      obj.value = {};
      obj.value.label = response[n].name;
      obj.value.uuid = response[n].uuid;
      obj.value.attributeId = response[n].attributeId;

      temp1[n] = obj
      this.attributesArray = temp1;
    }
  }

  onChangeLhsType(index) {
    this.dataset.filterTableArray[index].lhsAttribute == null;

    if (this.dataset.filterTableArray[index].lhsType == 'formula') {
      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
          error => console.log("Error ::", error))
    }

    else if (this.dataset.filterTableArray[index].lhsType == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
          error => console.log("Error ::", error))
    }

    else {
      this.dataset.filterTableArray[index].lhsAttribute = null;
    }
  }

  onChangeRhsType(index) {
    this.dataset.filterTableArray[index].rhsAttribute == null;

    if (this.dataset.filterTableArray[index].rhsType == 'formula') {
      this._commonService.getFormulaByType(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
          error => console.log("Error ::", error))
    }

    else if (this.dataset.filterTableArray[index].rhsType == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dataset.sourcedata.uuid, this.dataset.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs1(response) },
          error => console.log("Error ::", error))
    }
    else if (this.dataset.filterTableArray[index].rhsType == 'function') {
      this._commonService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))

    }
    else if (this.dataset.filterTableArray[index].rhsType == 'paramlist') {
      this._commonService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error))
    }
    else if (this.dataset.filterTableArray[index].rhsType == 'dataset') {
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.dataset.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else {
      this.dataset.filterTableArray[index].rhsAttribute = null;
    }
  }

  onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [];
    let FormulaObj = new AttributeIO;
    let temp = [];
    for (const i in response) {
      FormulaObj.label = response[i].name;
      FormulaObj.value = {};
      FormulaObj.value.label = response[i].name;
      FormulaObj.value.uuid = response[i].uuid;
      temp[i] = FormulaObj;
    }
    this.rhsFormulaArray = temp
  }

  onSuccessgetAllAttributeBySourceRhs1(response) {
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

  onChangeOperator(index) {
    this.dataset.filterTableArray[index].rhsAttribute = null;
    if (this.dataset.filterTableArray[index].operator == 'EXISTS' || this.dataset.filterTableArray[index].operator == 'NOT EXISTS') {
      this.dataset.filterTableArray[index].rhsType = 'dataset';
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.dataset.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else if (this.dataset.filterTableArray[index].operator == 'IS') {
      this.dataset.filterTableArray[index].rhsType = 'string';
    }
    else {
      this.dataset.filterTableArray[index].rhsType = 'integer';
    }
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

  searchOption(index) {
    this.datasetRowIndex = index
    this.displayDialogBox = true;
    this._commonService.getAllLatest(this.metaType.DATASET)
      .subscribe(response => { this.onSuccessgetAllLatest(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAllLatest(response) {
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
    this._commonService.getAttributesByDataset(this.metaType.DATASET, this.dialogSelectName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDataset(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDataset(response: MetaIdentifierHolder[]) {
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

  submitDialogBox(index) {
    this.displayDialogBox = false;
    let rhsattribute = new AttributeIO()
    rhsattribute.label = this.dialogAttributeName.label;
    rhsattribute.uuid = this.dialogAttributeName.uuid;
    rhsattribute.attributeId = this.dialogAttributeName.attributeId;
    this.dataset.filterTableArray[this.datasetRowIndex].rhsAttribute = rhsattribute;
    this.datasetNotEmpty = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  submitDataset() {
    this.isSubmitEnable1 = true;
    let datasetJson = new Dataset;
    datasetJson.uuid = this.dataset.uuid
    datasetJson.name = this.dataset.name
    datasetJson.tags = this.dataset.tags
    datasetJson.active = this.active == true ? 'Y' : "N"
    datasetJson.published = this.published == true ? 'Y' : "N"
    datasetJson.locked = this.locked == true ? 'Y' : "N"

    datasetJson.desc = this.dataset.desc
    datasetJson.limit = this.dataset.limit
    let dependsOn = new MetaIdentifierHolder();
    let ref = new MetaIdentifier();
    ref.type = this.dataset.source
    ref.uuid = this.dataset.sourcedata.uuid
    dependsOn.ref = ref;
    datasetJson.dependsOn = dependsOn;

    let filterInfoArray = [];

    if (this.dataset.filterTableArray != null) {
      for (let i = 0; i < this.dataset.filterTableArray.length; i++) {

        let filterInfo = new FilterInfoIO;
        filterInfo.logicalOperator = this.dataset.filterTableArray[i].logicalOperator;
        filterInfo.operator = this.dataset.filterTableArray[i].operator;
        filterInfo.operand = [];

        if (this.dataset.filterTableArray[i].lhsType == 'integer' || this.dataset.filterTableArray[i].lhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "simple";
          operatorObj.ref = ref;
          operatorObj.value = this.dataset.filterTableArray[i].lhsAttribute;
          operatorObj.attributeType = "string";
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].lhsType == 'formula') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "formula";
          ref.uuid = this.dataset.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].lhsType == 'datapod') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "datapod";
          ref.uuid = this.dataset.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.dataset.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo.operand[0] = operatorObj;
        }

        if (this.dataset.filterTableArray[i].rhsType == 'integer' || this.dataset.filterTableArray[i].rhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "simple";
          operatorObj.ref = ref;
          operatorObj.value = this.dataset.filterTableArray[i].rhsAttribute;
          operatorObj.attributeType = "string"
          filterInfo.operand[1] = operatorObj;

          if (this.dataset.filterTableArray[i].rhsType == 'integer' && this.dataset.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = "simple";
            operatorObj.ref = ref;
            operatorObj.value = this.dataset.filterTableArray[i].rhsAttribute1 + "and" + this.dataset.filterTableArray[i].rhsAttribute2;
            filterInfo.operand[1] = operatorObj;
          }
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'formula') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "formula";
          ref.uuid = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'datapod') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "datapod";
          ref.uuid = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'function') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "function";
          ref.type = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'paramlist') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "paramlist";
          ref.uuid = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.dataset.filterTableArray[i].rhsType == 'dataset') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = "dataset";
          ref.uuid = this.dataset.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        filterInfoArray[i] = filterInfo;
      }
      datasetJson.filterInfo = filterInfoArray;
      console.log(JSON.stringify(filterInfoArray));
    }
    var sourceAttributesArray = [];
    if (this.dataset.attributeTableArray != null) {
      for (var i = 0; i < this.dataset.attributeTableArray.length; i++) {
        var attributemap = new AttributeSource();
        attributemap.attrSourceId = "i";
        attributemap.attrSourceName = this.dataset.attributeTableArray[i].name
        //attributeinfo.attrSourceName=$scope.dataset.attributeTableArray[l].name
        var sourceAttr = new AttributeRefHolder();
        var sourceref = new MetaIdentifier();
        if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "string") {
          sourceref.type = "simple";
          sourceAttr.ref = sourceref;
          if (typeof this.dataset.attributeTableArray[i].sourcesimple == "undefined") {
            sourceAttr.value = "";
          }
          else {
            sourceAttr.value = this.dataset.attributeTableArray[i].sourcesimple;
          }
          attributemap.sourceAttr = sourceAttr;
        }
        else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "datapod") {
          let uuid = this.dataset.attributeTableArray[i].sourceattribute.id.split("_")[0]
          var attrid = this.dataset.attributeTableArray[i].sourceattribute.id.split("_")[1]
          sourceref.uuid = uuid;
          if (this.dataset.source == "relation") {
            sourceref.type = "datapod";
          }
          else {
            sourceref.type = this.dataset.source;
          }
          sourceAttr.ref = sourceref;
          sourceAttr.attrId = attrid;
          sourceAttr.attrType = null;
          attributemap.sourceAttr = sourceAttr;
        }
        else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "expression") {
          sourceref.type = "expression";
          sourceref.uuid = this.dataset.attributeTableArray[i].sourceexpression.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr;

        }
        else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "formula") {
          sourceref.type = "formula";
          sourceref.uuid = this.dataset.attributeTableArray[i].sourceformula.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr;

        }
        else if (this.dataset.attributeTableArray[i].sourceAttributeType.value == "function") {
          sourceref.type = "function"
          sourceref.uuid = this.dataset.attributeTableArray[i].sourcefunction.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr
        }
        sourceAttributesArray[i] = attributemap;
      }
    }

    datasetJson.attributeInfo = sourceAttributesArray;
    console.log(JSON.stringify(datasetJson))

    this._commonService.submit(this.metaType.DATASET, datasetJson).subscribe(
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
  // enableEdit(uuid, version) {
  //   this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'false']);
  // }
  //  showview(uuid, version) {
  //     this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'true']);
  // }

  showMainPage() {
    this.isShowSimpleData = false
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
    this.IsTableShow = false;
    this.showForm = true;
  }

  showDagGraph(id, version) {
    console.log("showDagGraph Call... ");
    this.isHomeEnable = true;
    this.showGraph = true;
    this.isGraphInprogess = true;
    this.showForm = false
    this.isShowSimpleData = false
    this.IsTableShow = false;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  autoPopulate() {
    this.dataset.attributeTableArray = [];
    for (var i = 0; i < this.allMapSourceAttribute.length; i++) {
      var that = this;
      var attributeinfo = new AttributeSourceIO();
      attributeinfo.id = i + 1;

      attributeinfo.name = (this.allMapSourceAttribute[i].value.label).split(".")[1];

      let sourceAttr = new AttributeIO();
      sourceAttr.uuid = this.allMapSourceAttribute[i].value.uuid;
      sourceAttr.label = this.allMapSourceAttribute[i].value.datapodname;
      sourceAttr.dname = this.allMapSourceAttribute[i].value.label;
      sourceAttr.type = "datapod";
      sourceAttr.attributeId = this.allMapSourceAttribute[i].value.attributeId;
      sourceAttr.id = this.allMapSourceAttribute[i].value.id;

      attributeinfo.sourceattribute = sourceAttr;

      let obj = { "value": "", "label": "" }
      obj.value = "datapod"
      obj.label = "attribute"
      attributeinfo.sourceAttributeType = obj;

      attributeinfo.isSourceAtributeSimple = false;
      attributeinfo.isSourceAtributeDatapod = true;
      attributeinfo.isSourceAtributeFormula = false;
      attributeinfo.isSourceAtributeExpression = false;
      attributeinfo.isSourceAtributeFunction = false;
      attributeinfo.isSourceAtributeParamList = false;
      this.isDublication(this.dataset.attributeTableArray, attributeinfo, function (err, obj, arr) {

        if (err) {
          console.log(err)
        }
        if (obj) {
          console.log("aaaaa0", obj)
          that.dataset.attributeTableArray.push(obj);
        }
        if (arr) {
          that.dataset.attributeTableArray = arr;
        }
      });
    }
  }
  isDublication(arr, newObj, cb) {
    this.chkDuplicate = false;
    let len = arr.length;
    var count = 0;
    if (arr.length == 0) {
      newObj.dup = false
      cb(null, newObj)
    }
    else {
      for (let i = 0; i < len; i++) {
        if (arr[i].name == newObj.name) {
          arr[i].dup = true;
          arr[i].dupIndex = len;
          newObj.dupIndex = i;
          count++;
        }
      }
      if (count > 0) {
        newObj.dup = true
        // newObj.dupIndex=i;
        this.chkDuplicate = true
        cb(null, newObj, arr)
      }
      else {
        newObj.dup = false
        cb(null, newObj)
      }
    }
  }
  onChangeSourceName(ind) {
    var nam = this.dataset.attributeTableArray[ind].name;
    var count = 0;
    console.log(this.dataset.attributeTableArray.length);
    for (var i = 0; i < this.dataset.attributeTableArray.length; i++) {
      if (this.dataset.attributeTableArray[i].name == nam && i != ind) {
        this.dataset.attributeTableArray[i].dupIndex = ind;
        this.dataset.attributeTableArray[ind].dupIndex = i;
        count++;
      }
    }
    if (count > 0) {
      this.dataset.attributeTableArray[ind].dup = true;
      if (this.dataset.attributeTableArray[ind].hasOwnProperty("dupIndex")) {
        var indx = this.dataset.attributeTableArray[ind].dupIndex;
        this.dataset.attributeTableArray[indx].dup = true;
      }
    }
    else {
      this.dataset.attributeTableArray[ind].dup = false;
      if (this.dataset.attributeTableArray[ind].hasOwnProperty("dupIndex")) {
        var indx = this.dataset.attributeTableArray[ind].dupIndex;
        this.dataset.attributeTableArray[indx].dup = false;
      }
    }
    for (var j = 0; j < this.dataset.attributeTableArray.length; j++) {
      var cont = 0
      if (this.dataset.attributeTableArray[j].dup == true) {
        cont++;
      }
      if (cont == 0) {
        this.chkDuplicate = false;
      }
      else {
        this.chkDuplicate = true;
        break;
      }
    }
  }

  onAttrRowDown(index) {
    var rowTempIndex = this.dataset.filterTableArray[index];
    var rowTempIndexPlus = this.dataset.filterTableArray[index + 1];
    this.dataset.filterTableArray[index] = rowTempIndexPlus;
    this.dataset.filterTableArray[index + 1] = rowTempIndex;
    this.isSubmitEnable1 = true;
  }

  onAttrRowUp(index) {
    var rowTempIndex = this.dataset.filterTableArray[index];
    var rowTempIndexMines = this.dataset.filterTableArray[index - 1];
    this.dataset.filterTableArray[index] = rowTempIndexMines;
    this.dataset.filterTableArray[index - 1] = rowTempIndex;
    this.isSubmitEnable1 = true;
  }

  dragStart(event, data) {
    console.log(event)
    console.log(data)
    this.dragIndex = data;
  }

  dragEnd(event) {
    console.log(event);
  }

  drop(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data;
      // console.log(event)
      //  console.log(data)
      var item = this.dataset.filterTableArray[this.dragIndex]
      this.dataset.filterTableArray.splice(this.dragIndex, 1)
      this.dataset.filterTableArray.splice(this.dropIndex, 0, item)
      this.isSubmitEnable1 = true;
    }
  }

  onAttrRowDownSourceAttr(index) {
    var rowTempIndex = this.dataset.attributeTableArray[index];
    var rowTempIndexPlus = this.dataset.attributeTableArray[index + 1];
    this.dataset.attributeTableArray[index] = rowTempIndexPlus;
    this.dataset.attributeTableArray[index + 1] = rowTempIndex;
    this.isSubmitEnable1 = true;
  }

  onAttrRowUpSourceAttr(index) {
    var rowTempIndex = this.dataset.attributeTableArray[index];
    var rowTempIndexMines = this.dataset.attributeTableArray[index - 1];
    this.dataset.attributeTableArray[index] = rowTempIndexMines;
    this.dataset.attributeTableArray[index - 1] = rowTempIndex;
    this.isSubmitEnable1 = true;
  }

  dragStartSourceAttr(event, data) {
    console.log(event)
    console.log(data)
    this.dragIndexSourceAttr = data;
  }

  dragEndSourceAttr(event) {
    console.log(event);
  }

  dropSourceAttr(event, data) {
    if (this.mode == 'false') {
      this.dropIndexSourceAttr = data;
      // console.log(event)
      //  console.log(data)
      var item = this.dataset.attributeTableArray[this.dragIndexSourceAttr]
      this.dataset.attributeTableArray.splice(this.dragIndexSourceAttr, 1)
      this.dataset.attributeTableArray.splice(this.dropIndexSourceAttr, 0, item)
      this.isSubmitEnable1 = true;
    }
  }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.dataset.filterTableArray.length; i++) {
      if (this.dataset.filterTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.dataset.filterTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.dataset.filterTableArray, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.dataset.filterTableArray[0].logicalOperator = "";
              if (!this.dataset.filterTableArray[1].logicalOperator) {
                this.dataset.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            if (new_index == this.dataset.filterTableArray.length - 1) {
              this.dataset.filterTableArray[0].logicalOperator = "";
              if (this.dataset.filterTableArray[new_index].logicalOperator == "") {
                this.dataset.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            this.txtQueryChangedFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.dataset.filterTableArray[0].logicalOperator = "";
            if (!this.dataset.filterTableArray[1].logicalOperator) {
              this.dataset.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.dataset.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          else if (new_index == this.dataset.filterTableArray.length - 1) {
            this.dataset.filterTableArray[0].logicalOperator = "";
            if (this.dataset.filterTableArray[new_index].logicalOperator == "") {
              this.dataset.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            }
            this.dataset.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          break;
        }
      }
    }
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

      if (index == (this.dataset.filterTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }

  updateArrayAttr(new_index, range, event) {
    for (let i = 0; i < this.dataset.attributeTableArray.length; i++) {
      if (this.dataset.attributeTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottomAttr.next(event);
        }
        else if (new_index >= this.dataset.attributeTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottomAttr.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.dataset.attributeTableArray, old_index, new_index);
          if (range) {
            this.checkSelected(false, null);
            this.txtQueryChangedAttr.next(new_index);
          }
          else {
            this.dataset.attributeTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          break;
        }
      }
    }
  }

  checkSelectedAttr(flag: any, index: any) {
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

      if (index == (this.dataset.attributeTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
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

  downloadShow(uuid: any, version: any) {debugger
    this.downloadUuid = uuid;
    this.downloadVersion = version;
    //this.downloadType = type;
    this.showDownloadModel = true
  }

  downloadDatasetResult() {debugger
    const headers = new Headers();
    this.http.get(this.baseUrl + '/dataset/download?action=view&uuid=' + this.downloadUuid + '&version=' + this.downloadVersion + '&rows=' + this.numRows + '&formate=' + this.downloadFormat,
      { headers: headers, responseType: ResponseContentType.Blob })
      .toPromise()
      .then(response => this.saveToFileSystem(response));
  }

  saveToFileSystem(response) {debugger
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
    this.showDownloadModel = false
  }

  cancelDownloadDialogBox(){
    this.showDownloadModel = false
  }

  ngOnDestroy() {
    this.txtQueryChangedFilter.unsubscribe();
    this.resetTableTopBottom.unsubscribe();
    this.txtQueryChangedAttr.unsubscribe();
    this.resetTableTopBottomAttr.unsubscribe();
  }
}

