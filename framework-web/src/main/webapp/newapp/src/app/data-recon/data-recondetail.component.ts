import { AppHelper } from './../app.helper';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';
import { SelectItem } from 'primeng/primeng';
import { MessageService } from 'primeng/components/common/messageservice';

import { CommonService } from '../metadata/services/common.service';
import { DataReconService } from '../metadata/services/dataRecon.services';
import { Version } from './../metadata/domain/version'
import { DependsOn } from './dependsOn'
import { AttributeHolder } from './../metadata/domain/domain.attributeHolder'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { Subject } from 'rxjs';
import { distinctUntilChanged, debounceTime } from 'rxjs/operators';
import { FilterInfoIO } from '../metadata/domainIO/domain.filterInfoIO';
import { MetaType } from '../metadata/enums/metaType';
import { BaseEntity } from '../metadata/domain/domain.baseEntity';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { AttributeIO } from '../metadata/domainIO/domain.attributeIO';
import { Recon } from '../metadata/domain/domain.recon';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { SourceAttr } from '../metadata/domain/domain.sourceAttr';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { FilterInfo } from '../metadata/domain/domain.filterInfo';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
@Component({
  selector: 'app-data-recon',
  templateUrl: './data-recondetail.template.html',
})
export class DataReconDetailComponent {
  isHomeEnable: boolean;
  dropIndex: any;
  dragIndex: any;
  iSSubmitEnable: boolean;
  allNamesTarget: any[];
  dialogAttributeNameTarget: any;
  dialogAttriNameArrayTarget: any[];
  dialogSelectNameTarget: any;
  dialogAttriArrayTarget: any[];
  displayDialogBoxTarget: boolean;

  selectedAllFitlerRow1: boolean;
  targetFilterTableArray: any;

  dialogAttributeName: any;
  dialogAttriNameArray: any[];
  dialogSelectName: any;
  dialogAttriArray: any[];
  displayDialogBox: boolean;

  rhsTypeArray: { value: string; label: string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  filterTableArray: any;
  paramlistArray: any[];
  functionArray: any[];
  attributesArray: any[];
  FormulaArray: any[];
  lhsTypes: { "text": string; "caption": string; }[];
  rhsTypes: { "text": string; "caption": string; }[];
  isSubmitEnable: boolean;
  allAttributeTarget: any[];
  allFormulaTarget: any[];
  targettableArray: any;
  allFormula: any[];
  selectallrow: boolean;
  rhsType: { "text": string; "caption": string; }[];
  lhsType: { "text": string; "caption": string; }[];
  allTargetAtrribute: any[];
  target: string;
  selectTargetAtrribute: DependsOn;
  selectTargetType: any;
  selectTargetFunction: any;
  selectSourceAtrribute: DependsOn;
  allSourceFunction: any[];
  allSourceAtrribute: any[];
  selectSoueceFunction: any;
  selectSourceType: any;
  IsProgerssShow: string;
  checkboxModelexecution: boolean;
  logicalOperators: { 'value': string; 'label': string; }[];
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  dataqualitycompare: any;
  valueCheck: any;
  allRefIntegrity: any[];
  selectdatefromate: any;
  selectDataType: any;
  selectedAllFitlerRow: boolean;
  lhsdatapodattributefilter: any[];
  operators: any[];
  sourceTableArray: any;
  allIntegrityAttribute: any[];
  selectIntegrityAttribute: any;
  selectRefIntegrity: any;
  datefromate: string[];
  isNullArray: { 'value': string; 'label': string; }[];
  datatype: { 'value': string; 'label': string; }[];

  selectAttribute: any;
  allAttribute: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  dropdownList: any[];
  allNames: any[];
  sourcedata: DependsOn;
  source: string;
  sources: string[];
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any[];
  tags: any;
  createdBy: any;
  recondata: any;
  mode: any;
  version: any;
  id: any;
  uuid: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  IsSelectDataType: any
  IsSelectSoureceAttr: any
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  showForm: boolean = true;
  isGraphInprogess: boolean;
  showDivGraph: boolean;
  // isGraphError: boolean;
  isversionEnable: boolean;
  isAdd: boolean;
  isEdit: boolean = false;
  // profiledata: any;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  metaType = MetaType;
  moveTo: number;
  moveToEnable: boolean;
  count: any[];
  invalideMinRow: boolean = false;
  invalideMaxRow: boolean = false;
  txtQueryChangedFilter: Subject<string> = new Subject<string>();
  resetTableTopBottom: Subject<string> = new Subject<string>();
  txtQueryChangedTargetFilter: Subject<string> = new Subject<string>();
  // rowIndex: any;
  topDisabled: boolean;
  bottomDisabled: boolean;
  active: any;
  locked: any;
  published: any;
  caretdown = 'fa fa-caret-down';
  datasetNotEmpty: boolean = true;
  datasetNotEmptyTarget: boolean = true;
  rowIndex: any;
  rowIndexTarget: any;


  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _dataReconService: DataReconService, private appHelper: AppHelper) {

    this.isHomeEnable = false;
    this.recondata = {};
    this.active = true;
    this.locked = false;
    this.published = false;
    this.isEditInprogess = false;
    this.isEditError = false;

    this.displayDialogBox = false;
    this.displayDialogBoxTarget = false;
    this.dialogAttributeName = {};
    this.dialogAttributeNameTarget = {};
    // this.datatype = ["", "String", "Int", "Float", "Double", "Date"];
    this.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
    this.continueCount = 1;
    this.IsSelectSoureceAttr = false;
    this.isSubmit = "false"
    this.sources = ["datapod", "dataset"];
    this.source = this.sources[0];
    this.target = this.sources[0];
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.selectDataType = "";
    this.selectdatefromate = "";
    this.dataqualitycompare = null;
    this.sourceTableArray = null;
    this.active = true;
    this.breadcrumbDataFrom = [
      {
        "caption": "Data Recon",
        "routeurl": "/app/list/recon"
      },
      {
        "caption": "Rule",
        "routeurl": "/app/list/recon"
      },
      {
        "caption": "",
        "routeurl": null
      }]
    this.operators = [
      { 'value': '=', 'label': 'EQUAL TO(=)' },
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
      { 'value': 'IS', 'label': 'IS' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }
    ];
    this.lhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' }
    ];
    this.rhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' },
      { 'value': 'dataset', 'label': 'dataset' },
      { 'value': 'paramlist', 'label': 'paramlist' },
      { 'value': 'function', 'label': 'function' }
    ];
    this.datatype = [
      { 'value': '', 'label': '' },
      { 'value': 'String', 'label': 'String' },
      { 'value': 'Int', 'label': 'Int' },
      { 'value': 'Float', 'label': 'Float' },
      { 'value': 'Double', 'label': 'Double' },
      { 'value': 'Date', 'label': 'Date' }
    ];
    this.isNullArray = [
      { 'value': 'NULL', 'label': 'NULL' },
      { 'value': 'NOT NULL', 'label': 'NOT NULL' }
    ];
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;
      this.getFunctionByCategory();
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
      }
      else {
        this.getAllLatestSource();
        this.getAllLatestTarget();
      }
      this.getAllVersionByUuid();
    });


    this.moveToEnable = false;
    this.count = [];
    this.txtQueryChangedFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.filterTableArray) {
          if (this.filterTableArray[i].hasOwnProperty("selected"))
            this.filterTableArray[i].selected = false;
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
        this.checkSelectedTarget(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.txtQueryChangedTargetFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.targetFilterTableArray) {
          if (this.targetFilterTableArray[i].hasOwnProperty("selected"))
            this.targetFilterTableArray[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelectedTarget(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.invalideMinRow = false;
    this.invalideMaxRow = false;
    this.topDisabled = false;
    this.bottomDisabled = false;

  }

  ngOnInit() {
    this.setMode(this.mode);
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

  enableEdit(uuid, version) {
    this.router.navigate(['app/recon/createreconerule', uuid, version, 'false']);
    this.isEdit = true;
  }

  showview(uuid, version) {
    this.router.navigate(['app/recon/createreconerule', uuid, version, 'true']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.recondata.name;
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/recon']);
  }
  onChangeSource() {
    this.selectAttribute = null;
    this.getAllAttributeBySource();
    this.filterTableArray = null
  }
  onChangeTarget() {
    this.selectAttribute = null;
    this.getAllAttributeByTarget();
    this.targetFilterTableArray = null
  }
  OnselectType = function () {
    if (this.selectDataType == "Date") {
      this.IsSelectDataType = true;
    }
    else {
      this.IsSelectDataType = false;
    }
  }

  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  // checkAllFilterRow() {
  //   if (!this.selectedAllFitlerRow) {
  //     this.selectedAllFitlerRow = true;
  //   }
  //   else {
  //     this.selectedAllFitlerRow = false;
  //   }
  //   this.sourceTableArray.forEach(filter => {
  //     filter.selected = this.selectedAllFitlerRow;
  //   });
  // }
  getAllLatestSource() {
    this.getAllLatest(this.source, "source");
    // this._commonService.getAllLatest(this.source).subscribe(
    //   response => { this.onSuccesgetAllLatestSource(response) },
    //   error => console.log('Error :: ' + error)
    // )
  }

  getAllLatestTarget() {
    this.getAllLatest(this.target, "target");
    // this._commonService.getAllLatest(this.target).subscribe(
    //   response => { this.onSuccesgetAllLatestTarget(response) },
    //   error => console.log('Error :: ' + error)
    // )
  }
  getAllLatest(sourceOrTarget, type) {

    this._commonService.getAllLatest(sourceOrTarget).subscribe(
      response => {
        if (type == "source") {
          this.onSuccesgetAllLatestSource(response);
        }
        else if (type == "target") {
          this.onSuccesgetAllLatestTarget(response);
        }
      },
      error => console.log('Error :: ' + error)
    );

  }
  onSuccesgetAllLatestSource(response1: BaseEntity[]) {
    let temp = []
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
    }
    for (const n in response1) {
      let allname = new DropDownIO();
      response1.sort((a, b) => a.name.localeCompare(b.name.toString()));
      allname.label = response1[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response1[n].name;
      allname.value.uuid = response1[n].uuid;
      temp[n] = allname;
    }
    this.allNames = temp;

    if (this.mode == undefined) {
      setTimeout(() => {
        this.selectSourceType = this.allNames[0].value;
        this.getAllAttributeBySource();
      }, 1000);
    }
    if (this.mode !== undefined) {
      setTimeout(() => {
        this.getAllAttributeBySource();
      }, 1000);
    }
  }
  onSuccesgetAllLatestTarget(response1: BaseEntity[]) {
    let temp = []
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
    }
    for (const n in response1) {
      let allname = new DropDownIO();
      response1.sort((a, b) => a.name.localeCompare(b.name.toString()));
      allname.label = response1[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response1[n].name;
      allname.value.uuid = response1[n].uuid;
      temp[n] = allname;
    }
    this.allNamesTarget = temp; 
    if (this.mode == undefined) {
      setTimeout(() => {
        this.selectTargetType = this.allNamesTarget[0].value;
        this.getAllAttributeByTarget();
      }, 1000);
    }
    if (this.mode !== undefined) {
      setTimeout(() => {
        this.getAllAttributeByTarget();
      }, 1000);
    }

  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source).subscribe(
      response => { this.onSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllAttributeBySource(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = new AttributeIO();
    allname.label = '-select-';
    allname.value = null
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.u_Id = response[n].id;
      allname.value.uuid = response[n].uuid;
      allname.value.attrId = response[n].attributeId;
      temp[n] = allname
      attribute[n] = allname;
    }
    this.allSourceAtrribute = temp; 
    if (this.mode == undefined) {
      this.selectSourceAtrribute = this.allSourceAtrribute[0];
    }
  }
  getAllAttributeByTarget() {
    this._commonService.getAllAttributeBySource(this.selectTargetType.uuid, this.target).subscribe(
      response => { this.onSuccesgetAllAttributeByTarget(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllAttributeByTarget(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = new AttributeIO();
    allname.label = '-select-';
    allname.value = null
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.u_Id = response[n].id;
      allname.value.uuid = response[n].uuid;
      allname.value.attrId = response[n].attributeId;
      temp[n] = allname
      attribute[n] = allname;
    }
    this.allTargetAtrribute = temp; 

    if (this.mode == undefined) {
      this.selectTargetAtrribute = this.allTargetAtrribute[0].value;
    }
  }
  getFunctionByCategory() {
    this._dataReconService.getFunctionByCategory("function").subscribe(
      response => { this.onSuccesgetFunctionByCategory(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetFunctionByCategory(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = new AttributeIO();
    // allname.label = '-select-';
    // allname.value = null
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = {};
      allname.value.label = response[n].name;
      allname.value.u_Id = response[n].id;
      allname.value.uuid = response[n].uuid;
      allname.value.attrId = response[n].attributeId;
      temp[n] = allname;
      attribute[n] = allname;
    }
    this.allSourceFunction = temp;
    this.selectSoueceFunction = this.allSourceFunction[0].value;
    this.selectTargetFunction = this.allSourceFunction[0].value;
  }
  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._dataReconService.getOneByUuidAndVersion(id, version, this.metaType.RECON)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error)
          this.isEditError = true;
        });
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.recondata = response.recon;
    this.breadcrumbDataFrom[2].caption = this.recondata.name
    this.uuid = response.uuid;

    this.dataqualitycompare = response.recon;

    const version: Version = new Version();
    version.label = this.recondata.version;
    version.uuid = this.recondata.uuid;
    this.selectedVersion = version

    // this.createdBy = this.recondata.createdBy.ref.name
    this.published = this.appHelper.convertStringToBoolean(this.recondata.published);
    this.locked = this.appHelper.convertStringToBoolean(this.recondata.locked);
    this.active = this.appHelper.convertStringToBoolean(this.recondata.active);
    this.recondata.sourceDistinct = this.appHelper.convertStringToBoolean(this.recondata.sourceDistinct);
    this.recondata.targetDistinct = this.appHelper.convertStringToBoolean(this.recondata.targetDistinct);
    // this.tags = this.recondata['tags'];

    this.sourceTableArray = response.filterInfo;

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = this.recondata.sourceAttr.ref.name;
    dependOnTemp.uuid = this.recondata.sourceAttr.ref.uuid;
    this.selectSourceType = dependOnTemp;

    let dependOn: DependsOn = new DependsOn();
    dependOn.label = this.recondata.sourceFunc.ref.name;
    dependOn.uuid = this.recondata.sourceFunc.ref.uuid;
    this.selectSoueceFunction = dependOn;

    let sourceAttr: DependsOn = new DependsOn();
    sourceAttr.label = this.recondata.sourceAttr.attrName;
    sourceAttr.attrId = this.recondata.sourceAttr.attrId;
    this.selectSourceAtrribute = sourceAttr;

    let target: DependsOn = new DependsOn();
    target.label = this.recondata.targetAttr.ref.name;
    target.uuid = this.recondata.targetAttr.ref.uuid;
    this.selectTargetType = target;

    let targetFun: DependsOn = new DependsOn();
    targetFun.label = this.recondata.targetFunc.ref.name;
    targetFun.uuid = this.recondata.targetFunc.ref.uuid;
    this.selectTargetFunction = targetFun;

    let targetAttr: DependsOn = new DependsOn();
    targetAttr.label = this.recondata.targetAttr.attrName;
    targetAttr.attrId = this.recondata.targetAttr.attrId;

    this.source = this.recondata.sourceAttr.ref.type;
    this.target = this.recondata.targetAttr.ref.type;

    //this.sourcedata=dependOnTemp;
    // this.getAllVersionByUuid();
    this.getAllLatestSource();
    this.getAllLatestTarget();
    this.selectTargetAtrribute = targetAttr;

    //this.selectTargetType=this.reconruledata.targetAttr.ref.type; 
    // if (this.recondata["sourceFilter"]) {
    //   this.sourceTableArray = this.recondata["sourceFilter"]
    // }
    // else{
    //   this.sourceTableArray=[]
    // }
    // if (this.recondata["targetFilter"]) {
    //   this.targettableArray = this.recondata["targetFilter"]
    // }
    // else {

    // }


    this.getAllAttributeBySourcerhs();
    // this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
    //   .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
    //     error => console.log("Error ::", error))

    this.getFormulaByType();
    // this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
    //   .subscribe(response => { this.onSuccessgetFormulaByType(response) },
    //     error => console.log("Error ::", error))

    this.getFunctionByCriteria();
    // this._commonService.getFunctionByCriteria("", "N", "function")
    //   .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
    //     error => console.log("Error ::", error));

    this.getParamByApp();

    this.filterTableArray = response.filterTableArray
    this.targetFilterTableArray = response.targetFilterTableArray
    this.isEditInprogess = false;
  }

  onChangeRhsType(index) {
    this.filterTableArray[index].rhsAttribute = null;

    if (this.filterTableArray[index].rhsType == this.metaType.FORMULA) {
      this.getFormulaByType();
      // this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetFormulaByType(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySourcerhs();
      // this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == this.metaType.FUNCTION) {
      this.getFunctionByCriteria();
      // this._commonService.getFunctionByCriteria("", "N", "function")
      //   .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == this.metaType.PARAMLIST) {
      this.getParamByApp();
      // this._commonService.getParamByApp("", "application")
      //   .subscribe(response => { this.onSuccessgetParamByApp(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == this.metaType.DATASET) {
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else {
      this.filterTableArray[index].rhsAttribute = null;
    }
  }

  onChangeLhsType(index) {
    this.filterTableArray[index].lhsAttribute = null;
    if (this.filterTableArray[index].lhsType == this.metaType.FORMULA) {
      this.getFormulaByType();
      // this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetFormulaByType(response) },
      //     error => console.log("Error ::", error))
    }

    else if (this.filterTableArray[index].lhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySourcerhs();
      // this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
      //     error => console.log("Error ::", error))
    }
    else {
      this.filterTableArray[index].lhsAttribute = null;
    }
  }

  onChangeRhsTypeTarget(index) {
    this.targetFilterTableArray[index].rhsAttribute = null;

    if (this.targetFilterTableArray[index].rhsType == this.metaType.FORMULA) {
      this.getFormulaByType();
      // this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetFormulaByType(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index].rhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySourcerhs();
      // this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index].rhsType == this.metaType.FUNCTION) {
      this.getFunctionByCriteria();
      // this._commonService.getFunctionByCriteria("", "N", "function")
      //   .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index].rhsType == this.metaType.PARAMLIST) {
      this.getParamByApp();
      // this._commonService.getParamByApp("", "application")
      //   .subscribe(response => { this.onSuccessgetParamByApp(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index].rhsType == this.metaType.DATASET) {
      this.datasetNotEmptyTarget = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.targetFilterTableArray[index].rhsAttribute = rhsAttribute;
    }
    else {
      this.targetFilterTableArray[index].rhsAttribute = null;
    }
  }

  onChangeLhsTypeTarget(index) {
    this.targetFilterTableArray[index].lhsAttribute = null;
    if (this.targetFilterTableArray[index].lhsType == this.metaType.FORMULA) {
      this.getFormulaByType();
      // this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetFormulaByType(response) },
      //     error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index].lhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySourcerhs();
      // this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
      //     error => console.log("Error ::", error))
    }
    else {
      this.targetFilterTableArray[index].lhsAttribute = null;
    }
  }


  getFormulaByType() {
    if (this.selectSourceType) {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
          error => console.log("Error ::", error));
    }
  }
  onSuccessgetFormulaByType(response: BaseEntity[]) {
    let FormulaObj = new DropDownIO();
    let temp = [];
    for (const i in response) {
      FormulaObj.label = response[i].name;
      FormulaObj.value = { label: "", uuid: "" };
      FormulaObj.value.label = response[i].name;
      FormulaObj.value.uuid = response[i].uuid;
      temp[i] = FormulaObj;
    }
    this.FormulaArray = temp
  }

  getAllAttributeBySourcerhs() {
    if (this.selectSourceType) {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error));
    }
  }
  onSuccessgetAllAttributeBySource(response) {
    let temp1 = [];
    for (const i in response) {
      let attributeObj = new AttributeIO();
      attributeObj.label = response[i].dname;
      attributeObj.value = {};
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;
      attributeObj.value.id = response[i].uuid + "_" + response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.RECON, this.id)
      .subscribe(
        response => {
          this.onSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.VersionList[i] = ver;
    }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  onSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, this.metaType.RECON, "execute").subscribe(
      response => {
        this.showMassage('Recon Rule Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()
        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }
  showMassage(msg, msgtype, msgsumary) {
    this.isSubmit = "false";
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }



  getAllAttributeBySourceDrop(defaultValue, index, type) {
    this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySourceDrop(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  changeSourceType() {
    this.getAllLatestSource()
    this.filterTableArray = []
  }
  changeTargetType() {

    this.getAllLatestTarget();
    // this._commonService.getAllLatest(this.target).subscribe(
    //   response => { this.OnSuccesgetAllLatest(response) },
    //   error => console.log('Error :: ' + error)
    // )
    this.targetFilterTableArray = []
  }
  OnSuccesgetAllAttributeBySourceDrop(response2, defaultValue, index, type) {
    let temp = []
    for (const n in response2) {
      let allname1 = new AttributeIO();
      allname1.label = response2[n].dname;
      allname1.value = { label: "", id: "" };
      allname1.value.label = response2[n].dname;
      allname1.value.id = response2[n].id;
      temp[n] = allname1;
    }
    this.allAttribute = temp;
    // if (defaultValue == true && index != null) {
    //   let lhsdatapodAttribute = {}
    //   lhsdatapodAttribute["label"] = this.allAttribute[0]["label"];
    //   lhsdatapodAttribute["id"] = this.allAttribute[0]["value"]['id'];
    //   if (type == 'lhs') {
    //     this.sourceTableArray[index].lhsdatapodAttribute = lhsdatapodAttribute;
    //   } else {
    //     this.sourceTableArray[index].rhsdatapodAttribute = lhsdatapodAttribute;
    //   }
    // }

  }
  getAllFormula(defaultValue, index, type) {
    this._commonService.getFormulaByType(this.selectSourceType.uuid, this.metaType.FORMULA).subscribe(
      response => { this.onSuccessgetAllFormula(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response, defaultValue, index, type) {
    let temp = []
    if (response[0]) {
      let sourceformula = new AttributeIO();
      sourceformula.label = response[0].name;
      sourceformula.uuid = response[0].uuid;
      if (type == 'lhs') {
        this.sourceTableArray[index].lhsformula = sourceformula;
      } else {
        this.sourceTableArray[index].rhsformula = sourceformula;

      }
    }
    for (const n in response) {
      let allname1 = new AttributeIO();
      allname1.label = response[n].name;
      allname1.value = {};
      allname1.value.label = response[n].name;
      allname1.value.uuid = response[n].uuid;
      temp[n] = allname1;
    }
    this.allFormula = temp
  }

  getAllAttributeBySourceTarget(defaultValue, index, type) {
    this._commonService.getAllAttributeBySource(this.selectTargetType.uuid, this.target).subscribe(
      response => { this.onSuccesgetAllAttributeBySourceTarget(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllAttributeBySourceTarget(response2, defaultValue, index, type) {
    //   
    let temp = []
    for (const n in response2) {
      let allname1 = new AttributeIO();
      allname1.label = response2[n].dname;
      allname1.value = {};
      allname1.value.label = response2[n].dname;
      allname1.value.id = response2[n].id;
      temp[n] = allname1;
    }
    this.allAttributeTarget = temp;
    // if (defaultValue == true && index != null) {
    //   let lhsdatapodAttribute = {}
    //   lhsdatapodAttribute["label"] = this.allAttributeTarget[0]["label"];
    //   lhsdatapodAttribute["id"] = this.allAttributeTarget[0]["value"]['id'];
    //   if (type == 'lhs') {
    //     this.targettableArray[index].lhsdatapodAttribute = lhsdatapodAttribute;
    //   } else {
    //     this.targettableArray[index].rhsdatapodAttribute = lhsdatapodAttribute;
    //   }
    // }

  }
  getAllFormulaTarget(defaultValue, index, type) {
    this._commonService.getFormulaByType(this.selectTargetType.uuid, this.metaType.FORMULA).subscribe(
      response => { this.onSuccessgetAllFormulaTarget(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormulaTarget(response: BaseEntity[], defaultValue, index, type) {
    let temp = []
    if (response[0]) {
      let sourceformula = new AttributeIO();
      sourceformula.label = response[0].name;
      sourceformula.uuid = response[0].uuid;
      if (type == 'lhs') {
        this.targettableArray[index].lhsformula = sourceformula;
      } else {
        this.targettableArray[index].rhsformula = sourceformula;
      }
    }
    for (const n in response) {
      let allname1 = new AttributeIO();
      allname1.label = response[n].name;
      allname1.value = {};
      allname1.value.label = response[n].name;
      allname1.value.uuid = response[n].uuid;
      temp[n] = allname1;
    }
    this.allFormulaTarget = temp
  }
  selectAllRowTarget() {
    this.targettableArray.forEach(expression => {
      expression.selected = this.selectallrow;
    });
  }


  // selectAllRow() {
  //   this.sourceTableArray.forEach(expression => {
  //     expression.selected = this.selectallrow;
  //   });
  // }

  addRow() {
    var filertable = new FilterInfoIO;
    if (this.filterTableArray == null || this.filterTableArray.length == 0) {
      this.filterTableArray = [];
      filertable.logicalOperator = '';
    }
    else {
      filertable.logicalOperator = this.logicalOperators[1].label;
    }
    // var len = this.filterTableArray.length + 1
    filertable.lhsType = "string";
    filertable.lhsAttribute = null;
    filertable.operator = this.operators[0].value;
    filertable.rhsType = "string"
    filertable.rhsAttribute = null;
    this.filterTableArray.splice(this.filterTableArray.length, 0, filertable);
    this.count = [];
    this.checkSelected(false, this.filterTableArray.length - 1);
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

    this.count = [];
    this.checkSelected(false, null);
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

  addRowTarget() {
    var filertable = new FilterInfoIO;
    if (this.targetFilterTableArray == null || this.targetFilterTableArray.length == 0) {
      this.targetFilterTableArray = [];
      filertable.logicalOperator = ""
    }
    else {
      filertable.logicalOperator = this.logicalOperators[1].label;
    }
    filertable.lhsType = "string"
    filertable.lhsAttribute = null
    filertable.operator = this.operators[0].value;
    filertable.rhsType = "string"
    filertable.rhsAttribute = null;
    this.targetFilterTableArray.splice(this.targetFilterTableArray.length, 0, filertable);
    this.count = [];
    this.checkSelectedTarget(false, this.targetFilterTableArray.length - 1);
  }
  removeRowTarget() {
    let newDataList = [];
    this.selectedAllFitlerRow1 = false;
    this.targetFilterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }

    this.count = [];
    this.checkSelectedTarget(false, null);
    this.targetFilterTableArray = newDataList;
  }

  checkAllFilterRowTarget() {
    if (!this.selectedAllFitlerRow1) {
      this.selectedAllFitlerRow1 = true;
    }
    else {
      this.selectedAllFitlerRow1 = false;
    }
    this.targetFilterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow1;
    });
  }

  getFunctionByCriteria() {
    this._commonService.getFunctionByCriteria("", "N", this.metaType.FUNCTION)
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error));
  }
  onSuccessgetFunctionByCriteria(response) {
    let temp = [];
    for (const i in response) {
      let attributeObj = new AttributeIO();
      attributeObj.label = response[i].name;
      attributeObj.value = {};
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].name;
      temp[i] = attributeObj
    }
    this.functionArray = temp;
  }

  getParamByApp() {
    this._commonService.getParamByApp("", this.metaType.APPLICATION)
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetParamByApp(response) {
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

  onChangeOperator(index) {
    this.filterTableArray[index].rhsAttribute = null;
    if (this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS') {
      this.filterTableArray[index].rhsType = this.metaType.DATASET;
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
    // this.filterTableArray[index].rhsAttribute = null;
    // if(this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS'){
    //   this.filterTableArray[index].rhsType = 'dataset' ;
    // }
    // else{
    // 	this.filterTableArray[index].rhsType = 'integer';
    // }	
  }

  onChangeOperatorTarget(index) {
    this.targetFilterTableArray[index].rhsAttribute = null;
    if (this.targetFilterTableArray[index].operator == 'EXISTS' || this.targetFilterTableArray[index].operator == 'NOT EXISTS') {
      this.targetFilterTableArray[index].rhsType = this.metaType.DATASET;
      this.datasetNotEmptyTarget = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.targetFilterTableArray[index].rhsAttribute = rhsAttribute
    }
    else if (this.targetFilterTableArray[index].operator == 'IS') {
      this.targetFilterTableArray[index].rhsType = 'string';
    }
    else {
      this.targetFilterTableArray[index].rhsType = 'integer';
    }
    // this.filterTableArray[index].rhsAttribute = null;
    // if(this.targetFilterTableArray[index].operator == 'EXISTS' || this.targetFilterTableArray[index].operator == 'NOT EXISTS'){
    //   this.targetFilterTableArray[index].rhsType = 'dataset' ;
    // }
    // else{
    // 	this.filterTableArray[index].rhsType = 'integer';
    // }	
  }

  searchOption(data, index) {
    this.rowIndex = index;
    this.displayDialogBox = true;
    if (!data.uuid) {
      this.dialogSelectName = "";
      this.dialogAttributeName = "";
      this._commonService.getAllLatest(this.metaType.DATASET)
        .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response, data) },
          error => console.log("Error ::", error))
    }
    else {
      this._commonService.getAllLatest(MetaType.DATASET)
        .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response, data) },
          error => console.log("Error ::", error));
    }
  }

  onSuccessgetAllLatestDialogBox(response, data) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = new DropDownIO();
      dialogAttriObj.label = response[i].name;
      dialogAttriObj.value = { label: "", uuid: "" };
      dialogAttriObj.value.label = response[i].name;
      dialogAttriObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriObj;

      if (data.uuid && data.uuid == response[i].uuid) {
        this.dialogSelectName = data;
        this._commonService.getAttributesByDataset(MetaType.DATASET, data.uuid)
          .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response, data) },
            error => console.log("Error ::", error));
      }
    }
    this.dialogAttriArray = temp
    console.log(JSON.stringify(this.dialogAttriArray));
  }
  onSuccessgetAttributesByDatasetDialogBox(response, data) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = {};
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;

      console.log(response[i].attrId);
      if (data) {
        if (data.attributeId.toString()) {
          if (data.attributeId.toString() == response[i].attrId) {
            this.dialogAttributeName = dialogAttriNameObj.value;
          }
        }
      }
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }
  onChangeDialogAttribute(dialogSelectDatasetName) {
    this._commonService.getAttributesByDataset(this.metaType.DATASET, dialogSelectDatasetName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetChangeDialogAttr(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetAttributesByDatasetChangeDialogAttr(response) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = { label: "", attributeId: "", uuid: "" };
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index) {
    this.displayDialogBox = false;
    let rhsattribute = new AttributeIO();
    rhsattribute.label = this.dialogAttributeName.label;
    rhsattribute.uuid = this.dialogAttributeName.uuid;
    rhsattribute.attributeId = this.dialogAttributeName.attributeId;
    this.filterTableArray[index].rhsAttribute = rhsattribute;
    this.datasetNotEmpty = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }


  searchOptionTarget(data, index) {
    this.rowIndexTarget = index;
    this.displayDialogBoxTarget = true;
    if (!data.uuid) {
      this.dialogSelectNameTarget = "";
      this.dialogAttributeNameTarget = "";
      this._commonService.getAllLatest(this.metaType.DATASET)
        .subscribe(response => { this.onSuccessgetAllLatestDialogBoxTarget(response, data) },
          error => console.log("Error ::", error))

    } else {
      this._commonService.getAllLatest(MetaType.DATASET)
        .subscribe(response => { this.onSuccessgetAllLatestDialogBoxTarget(response, data) },
          error => console.log("Error ::", error));
    }
  }

  onSuccessgetAllLatestDialogBoxTarget(response, data) {
    this.dialogAttriArrayTarget = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = new AttributeIO();
      dialogAttriObj.label = response[i].name;
      dialogAttriObj.value = {}
      dialogAttriObj.value.label = response[i].name;
      dialogAttriObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriObj;

      if (data.uuid && data.uuid == response[i].uuid) {
        this.dialogSelectNameTarget = data;
        this._commonService.getAttributesByDataset(MetaType.DATASET, data.uuid)
          .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBoxTarget(response, data) },
            error => console.log("Error ::", error));
      }
    }
    this.dialogAttriArrayTarget = temp
    console.log(JSON.stringify(this.dialogAttriArrayTarget));
  }

  onSuccessgetAttributesByDatasetDialogBoxTarget(response, data) {
    this.dialogAttriNameArrayTarget = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = {};
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;

      if (data) {
        if (data.attributeId.toString()) {
          if (data.attributeId.toString() == response[i].attrId) {
            this.dialogAttributeNameTarget = dialogAttriNameObj.value;
          }
        }
      }
      this.dialogAttriNameArrayTarget[i] = dialogAttriNameObj;
    }
  }

  onChangeDialogTargetAttribute(dialogSelectDatasetNameTarget) {
    this._commonService.getAttributesByDataset(this.metaType.DATASET, dialogSelectDatasetNameTarget.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetChangeDialogAttrTarget(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetChangeDialogAttrTarget(response) {
    this.dialogAttriNameArrayTarget = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = { label: "", attributeId: "", uuid: "" };
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;
      this.dialogAttriNameArrayTarget[i] = dialogAttriNameObj;
    }
  }


  submitDialogBoxTarget(index) {
    this.displayDialogBoxTarget = false;
    let rhsattribute = new AttributeIO();
    rhsattribute.label = this.dialogAttributeNameTarget.label;
    rhsattribute.uuid = this.dialogAttributeNameTarget.uuid;
    rhsattribute.attributeId = this.dialogAttributeNameTarget.attributeId;
    this.targetFilterTableArray[index].rhsAttribute = rhsattribute;
    this.datasetNotEmptyTarget = true;
  }

  cancelDialogBoxTarget() {
    this.displayDialogBoxTarget = false;
  }

  submitRecon() {
    this.isSubmitEnable = true;
    this.isSubmit = "true"
    let reconJson = new Recon();
    reconJson.uuid = this.recondata.uuid;
    reconJson.name = this.recondata.name;
    reconJson.desc = this.recondata.desc;
    reconJson.tags = this.recondata.tags;
    // let tagArray = [];
    // if (this.recondata.tags != null) {
    //   for (var counttag = 0; counttag < this.recondata.tags.length; counttag++) {
    //     tagArray[counttag] = this.recondata.tags[counttag];
    //   }
    // }
    // reconJson["tags"] = tagArray;
    reconJson.active = this.appHelper.convertBooleanToString(this.recondata.active);
    reconJson.published = this.appHelper.convertBooleanToString(this.recondata.published == true);
    reconJson.locked = this.appHelper.convertBooleanToString(this.recondata.locked == true);
    reconJson.sourceDistinct = this.appHelper.convertBooleanToString(this.recondata.sourceDistinct);
    reconJson.targetDistinct = this.appHelper.convertBooleanToString(this.recondata.targetDistinct);

    var sourceAttr = new AttributeRefHolder();
    var ref = new MetaIdentifier();
    //sourceAttr["attrName"]=this.selectSourceAtrribute["attrName"]
    sourceAttr.attrId = this.selectSourceAtrribute.attrId;
    ref.name = this.selectSourceType.name;
    ref.type = this.source;
    ref.uuid = this.selectSourceType.uuid
    sourceAttr.ref = ref
    reconJson.sourceAttr = sourceAttr;

    var targetattribute = new AttributeRefHolder();
    var ref = new MetaIdentifier();
    //targetattribute["attrName"]=this.selectTargetAtrribute["attrName"]
    targetattribute.attrId = this.selectTargetAtrribute.attrId;
    ref.name = this.selectTargetType.name;
    ref.type = this.target;
    ref.uuid = this.selectTargetType.uuid;
    targetattribute.ref = ref
    reconJson.targetAttr = targetattribute

    var sourFunction = new AttributeRefHolder();
    var ref = new MetaIdentifier();
    ref.name = this.selectSoueceFunction.name;
    ref.type = this.metaType.FUNCTION;
    ref.uuid = this.selectSoueceFunction.uuid;
    sourFunction.ref = ref;
    reconJson.sourceFunc = sourFunction;

    var targFunction = new AttributeRefHolder();
    var ref = new MetaIdentifier();
    ref.name = this.selectTargetFunction.name;
    ref.type = this.metaType.FUNCTION;
    ref.uuid = this.selectTargetFunction.uuid;
    targFunction.ref = ref
    reconJson.targetFunc = targFunction;


    let filterInfoArray = [];
    if (this.filterTableArray != null) {
      for (let i = 0; i < this.filterTableArray.length; i++) {
        let filterInfo = new FilterInfo();
        filterInfo.display_seq = i;
        filterInfo.logicalOperator = this.filterTableArray[i].logicalOperator;
        filterInfo.operator = this.filterTableArray[i].operator;
        filterInfo.operand = [];
        if (this.filterTableArray[i].lhsType == 'integer' || this.filterTableArray[i].lhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = this.metaType.SIMPLE;
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].lhsAttribute;
          operatorObj.attributeType = "string";
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == this.metaType.FORMULA) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FORMULA;
          ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          // operatorObj.attributeId = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.filterTableArray[i].lhsType == this.metaType.DATAPOD) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATAPOD;
          ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo.operand[0] = operatorObj;
        }
        // else if (this.filterTableArray[i].lhsType == 'attribute' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = new SourceAttr();
        //   let ref = new MetaIdentifier()
        //   ref.type = "attribute";
        //   operatorObj.ref = ref;
        //   operatorObj.value = this.filterTableArray[i].lhsAttribute;
        //   filterInfo.operand[0] = operatorObj;
        // }

        if (this.filterTableArray[i].rhsType == 'integer' || this.filterTableArray[i].rhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.SIMPLE;
          operatorObj.ref = ref;
          operatorObj.value = this.filterTableArray[i].rhsAttribute;
          operatorObj.attributeType = "string"
          filterInfo.operand[1] = operatorObj;

          if (this.filterTableArray[i].rhsType == 'integer' && this.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier()
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.filterTableArray[i].rhsAttribute1 + "and" + this.filterTableArray[i].rhsAttribute2;
            filterInfo.operand[1] = operatorObj;
          }
        }
        else if (this.filterTableArray[i].rhsType == this.metaType.FORMULA) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FORMULA;
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == this.metaType.FUNCTION) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FUNCTION;
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == this.metaType.PARAMLIST) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.PARAMLIST;
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == this.metaType.DATASET) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATASET;
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == this.metaType.DATAPOD) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATAPOD;
          ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        // else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = new SourceAttr();
        //   let ref = new MetaIdentifier()
        //   ref.type = "attribute";
        //   operatorObj.ref = ref;
        //   operatorObj.value = this.filterTableArray[i].rhsAttribute;
        //   filterInfo.operand[1] = operatorObj;
        // }
        filterInfoArray[i] = filterInfo;
      }
      reconJson.sourceFilter = filterInfoArray;
    }
    else {
      reconJson.sourceFilter = null;
    }

    let targetFilterInfoArray = [];
    if (this.targetFilterTableArray != null) {
      for (let i = 0; i < this.targetFilterTableArray.length; i++) {
        let filterInfo = new FilterInfo();
        filterInfo.display_seq = i;
        filterInfo.logicalOperator = this.targetFilterTableArray[i].logicalOperator;
        filterInfo.operator = this.targetFilterTableArray[i].operator;
        filterInfo.operand = [];
        if (this.targetFilterTableArray[i].lhsType == 'integer' || this.targetFilterTableArray[i].lhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier();
          ref.type = this.metaType.SIMPLE;
          operatorObj.ref = ref;
          operatorObj.value = this.targetFilterTableArray[i].lhsAttribute;
          operatorObj.attributeType = "string";
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].lhsType == this.metaType.FORMULA) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FORMULA;
          ref.uuid = this.targetFilterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          // operatorObj.attributeId = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo.operand[0] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].lhsType == this.metaType.DATAPOD) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATAPOD;
          ref.uuid = this.targetFilterTableArray[i].lhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.targetFilterTableArray[i].lhsAttribute.attributeId;
          filterInfo.operand[0] = operatorObj;
        }
        // else if (this.filterTableArray[i].lhsType == 'attribute' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = new FilterInfo();
        //   let ref = new MetaIdentifier()
        //   ref.type = "attribute";
        //   operatorObj.ref = ref;
        //   operatorObj.value = this.filterTableArray[i].lhsAttribute;
        //   filterInfo.operand[0] = operatorObj;
        // }

        if (this.targetFilterTableArray[i].rhsType == 'integer' || this.targetFilterTableArray[i].rhsType == 'string') {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.SIMPLE;
          operatorObj.ref = ref;
          operatorObj.value = this.targetFilterTableArray[i].rhsAttribute;
          operatorObj.attributeType = "string"
          filterInfo.operand[1] = operatorObj;

          if (this.targetFilterTableArray[i].rhsType == 'integer' && this.targetFilterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier()
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.targetFilterTableArray[i].rhsAttribute1 + "and" + this.targetFilterTableArray[i].rhsAttribute2;
            filterInfo.operand[1] = operatorObj;
          }
        }
        else if (this.targetFilterTableArray[i].rhsType == this.metaType.FORMULA) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FORMULA;
          ref.uuid = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == this.metaType.FUNCTION) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.FUNCTION;
          ref.uuid = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          //operatorObj.attributeId = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == this.metaType.PARAMLIST) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.PARAMLIST;
          ref.uuid = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == this.metaType.DATASET) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATASET;
          ref.uuid = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == this.metaType.DATAPOD) {
          let operatorObj = new SourceAttr();
          let ref = new MetaIdentifier()
          ref.type = this.metaType.DATAPOD;
          ref.uuid = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj.ref = ref;
          operatorObj.attributeId = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo.operand[1] = operatorObj;
        }
        // else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = new FilterInfo();
        //   let ref = new MetaIdentifier()
        //   ref.type = "attribute";
        //   operatorObj.ref = ref;
        //   operatorObj.value = this.filterTableArray[i].rhsAttribute;
        //   filterInfo.operand[1] = operatorObj;
        // }
        targetFilterInfoArray[i] = filterInfo;
      }
      reconJson["targetFilter"] = targetFilterInfoArray;
    }
    else {
      reconJson["targetFilter"] = null;
    }

    console.log(JSON.stringify(reconJson))
    this._commonService.submit(this.metaType.RECON, reconJson, 'N').subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.RECON, response).subscribe(
        response => {
          this.onSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmit = "false";
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Recon Rule Save Successfully' });
      setTimeout(() => {
        this.goBack();

      }, 1000);
    }
  }
  onAttrRowDown(index) {
    var rowTempIndex = this.filterTableArray[index];
    var rowTempIndexPlus = this.filterTableArray[index + 1];
    this.filterTableArray[index] = rowTempIndexPlus;
    this.filterTableArray[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true

  }

  onAttrRowUp(index) {
    var rowTempIndex = this.filterTableArray[index];
    var rowTempIndexMines = this.filterTableArray[index - 1];
    this.filterTableArray[index] = rowTempIndexMines;
    this.filterTableArray[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }
  dragStart(event, data) {
    console.log(event)
    console.log(data)
    this.dragIndex = data
  }
  dragEnd(event) {
    console.log(event)
  }
  drop(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data
      // console.log(event)
      // console.log(data)
      var item = this.filterTableArray[this.dragIndex]
      this.filterTableArray.splice(this.dragIndex, 1)
      this.filterTableArray.splice(this.dropIndex, 0, item)
      this.iSSubmitEnable = true
    }

  }
  dropTrgt(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data
      // console.log(event)
      // console.log(data)
      var item = this.targetFilterTableArray[this.dragIndex]
      this.targetFilterTableArray.splice(this.dragIndex, 1)
      this.targetFilterTableArray.splice(this.dropIndex, 0, item)
      this.iSSubmitEnable = true
    }

  }
  onTrgtRowDown(index) {
    var rowTempIndex = this.targetFilterTableArray[index];
    var rowTempIndexPlus = this.targetFilterTableArray[index + 1];
    this.targetFilterTableArray[index] = rowTempIndexPlus;
    this.targetFilterTableArray[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true

  }

  onTrgtRowUp(index) {
    var rowTempIndex = this.targetFilterTableArray[index];
    var rowTempIndexMines = this.targetFilterTableArray[index - 1];
    this.targetFilterTableArray[index] = rowTempIndexMines;
    this.targetFilterTableArray[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.filterTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.filterTableArray, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.filterTableArray[0].logicalOperator = "";
              if (!this.filterTableArray[1].logicalOperator) {
                this.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            if (new_index == this.filterTableArray.length - 1) {
              this.filterTableArray[0].logicalOperator = "";
              if (this.filterTableArray[new_index].logicalOperator == "") {
                this.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            this.txtQueryChangedFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.filterTableArray[0].logicalOperator = "";
            if (!this.filterTableArray[1].logicalOperator) {
              this.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          else if (new_index == this.filterTableArray.length - 1) {
            this.filterTableArray[0].logicalOperator = "";
            if (this.filterTableArray[new_index].logicalOperator == "") {
              this.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            }
            this.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
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
    else
      this.count.pop();

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

  updateArrayTarget(new_index, range, event) {
    for (let i = 0; i < this.targetFilterTableArray.length; i++) {
      if (this.targetFilterTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.targetFilterTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.targetFilterTableArray, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.targetFilterTableArray[0].logicalOperator = "";
              if (!this.targetFilterTableArray[1].logicalOperator) {
                this.targetFilterTableArray[1].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelectedTarget(false, null);
            }
            if (new_index == this.targetFilterTableArray.length - 1) {
              this.targetFilterTableArray[0].logicalOperator = "";
              if (this.targetFilterTableArray[new_index].logicalOperator == "") {
                this.targetFilterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelectedTarget(false, null);
            }
            this.txtQueryChangedTargetFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.targetFilterTableArray[0].logicalOperator = "";
            if (!this.targetFilterTableArray[1].logicalOperator) {
              this.targetFilterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.targetFilterTableArray[new_index].selected = "";
            this.checkSelectedTarget(false, null);
          }
          else if (new_index == this.targetFilterTableArray.length - 1) {
            this.targetFilterTableArray[0].logicalOperator = "";
            if (this.targetFilterTableArray[new_index].logicalOperator == "") {
              this.targetFilterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            }
            this.targetFilterTableArray[new_index].selected = "";
            this.checkSelectedTarget(false, null);
          }
          break;
        }
      }
    }
  }


  checkSelectedTarget(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
    }
    else
      this.count.pop();

    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.targetFilterTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }

  ngOnDestroy() {
    this.txtQueryChangedFilter.unsubscribe();
    this.resetTableTopBottom.unsubscribe();
    this.txtQueryChangedTargetFilter.unsubscribe();
  }

}

