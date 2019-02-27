import { FilterInfoIO } from './../metadata/domainIO/domain.filterInfoIO';
import { FilterInfo } from './../metadata/domain/domain.filterInfo';
import { Component, Input, OnInit, ViewChild, HostListener } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';
import { SelectItem } from 'primeng/primeng';
import { MessageService } from 'primeng/components/common/messageservice';
import { CommonService } from '../metadata/services/common.service';
import { DataQualityService } from '../metadata/services/dataQuality.services';
import { Version } from './../metadata/domain/version'
import { DependsOn } from './dependsOn'
import { AttributeHolder } from './../metadata/domain/domain.attributeHolder'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { DataQuality } from '../metadata/domain/domain.dataQuality';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { BaseEntity } from '../metadata/domain/domain.baseEntity';
import { AttributeIO } from '../metadata/domainIO/domain.attributeIO';
import { AppHelper } from '../app.helper';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import * as MetaTypeEnum from '../metadata/enums/metaType';
import { SourceAttr } from '../metadata/domain/domain.sourceAttr';
import { Subject, fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { DataQualityIO } from '../metadata/domainIO/domain.dataQualityIO';
@Component({
  selector: 'app-data-pipeli',
  templateUrl: './data-qualitydetail.template.html',
})
export class DataQualityDetailComponent {
  dropIndex: any;
  dragIndex: any;
  isHomeEnable: boolean;
  attributesArray: Array<AttributeIO>;
  attributesArrayRhs: any;
  attributesArrayLhs: any;
  isNullArray: { 'value': string; 'label': string; }[];
  paramlistArray: any[];
  functionArray: any[];
  dialogAttributeName: any;
  dialogAttriNameArray: any[];
  dialogSelectName: any;
  dialogAttriArray: any[];
  displayDialogBox: boolean;
  rhsTypeArray: { 'value': string; 'label': string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  rhsFormulaArray: any[];
  lhsFormulaArray: any[];
  IsProgerssShow: string;
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": String; "routeurl": String; }[];
  dataqualitycompare: any;
  valueCheck: any;
  allRefIntegrity: any[];
  selectdatefromate: any;
  selectDataType: any;
  selectedAllFitlerRow: boolean;
  //lhsdatapodattributefilter: any[];
  // customInput: Subject<string> = new Subject();
  operators: any;
  logicalOperators: any;
  filterTableArray: any[];
  allIntegrityAttribute: any[];
  selectIntegrityAttribute: any;
  selectRefIntegrity: any;
  datefromate: string[];
  datatype: any;
  selectAttribute: any;
  allAttribute: any[] = [];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  dropdownList: any[];
  allNames: any[];
  sourcedata: DependsOn;
  source: string;
  sources: string[];
  selectedVersion: Version;
  versionList: SelectItem[] = [];
  msgs: any[];
  tags: any;
  createdBy: any;
  dqdata: any;
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
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  showForm: boolean = true;
  fitlerAttrTableSelectedItem: any[] = [];
  allname: Array<DropDownIO>;
  published: boolean;
  active: boolean;
  locked: boolean;
  metaType: any;
  moveTo: number;
  moveToEnable: boolean;
  count: any[];
  txtQueryChanged: Subject<string> = new Subject<string>();
  rowIndex: any;
  showDivGraph: boolean;
  isGraphInprogess: boolean;
  isGraphError: boolean;
  isEdit: boolean = false;
  isAdd: boolean;
  isversionEnable: boolean;

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _dataQualityService: DataQualityService, public appHelper: AppHelper) {
    this.metaType = MetaTypeEnum.MetaType;
    this.dqdata = new DataQuality();
    this.isHomeEnable = false
    this.displayDialogBox = false;

    this.isEditInprogess = false;
    this.isEditError = false;

    this.dialogAttributeName = {};
    this.selectRefIntegrity = {};
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
    ]
    this.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
    this.continueCount = 1;
    this.IsSelectSoureceAttr = false
    this.isSubmit = "false"
    this.sources = ["datapod"];
    this.source = this.sources[0];
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.selectDataType = {}
    this.selectdatefromate = "";
    this.dataqualitycompare = null;
    this.filterTableArray = [];
    //this.dqdata["active"] = true;
    this.active = true;
    this.locked = false;
    this.published = false;
    this.sourcedata = { 'uuid': "", "label": "" }
    this.breadcrumbDataFrom = [{
      "caption": "Data Quality",
      "routeurl": "/app/list/dq"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/dq"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
      }
      else {
        this.getAllLatest()
      }
    });
    this.moveToEnable = false;
    this.count = [];

    this.txtQueryChanged
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.filterTableArray[index].selected = "";
        this.checkSelected(false);
      });
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
    this.router.navigate(['app/dataQuality/dq', uuid, version, 'false']);
  }

  showMainPage(uuid, version) {
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
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.dqdata.name;
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dq']);
  }
  changeType() {
    this.selectAttribute = null;
    this.filterTableArray = [];
    this.getAllAttributeBySource();
  }
  OnselectType() {
    if (this.dqdata.selectDataType == "Date") {
      this.IsSelectDataType = true;
    }
    else {
      this.selectdatefromate = "";
      this.IsSelectDataType = false;
    }
  }
  onSourceAttributeChagne() {
    if (this.selectAttribute != null) {
      this.IsSelectSoureceAttr = true
      this.dqdata.nullCheck = true;
      this.allRefIntegrity = this.allNames;
      this.allIntegrityAttribute = this.allAttribute;
    }
    else {
      this.disableFields();
    }
    if (this.selectAttribute.label == '-Select-') {
      this.disableFields();
    }
  }
  disableFields() {
    this.IsSelectSoureceAttr = false
    this.dqdata.nullCheck = false;
    this.dqdata.valueCheck = ""
    this.dqdata.lowerBound = "";
    this.dqdata.upperBound = "";
    this.selectDataType = {};
    this.selectdatefromate = "";
    this.dqdata.minLength = ""
    this.dqdata.maxLength = "";
    this.allRefIntegrity = [];
    this.selectRefIntegrity = "";
    this.allIntegrityAttribute = [];
    this.selectIntegrityAttribute = "";
  }

  changeRefIntegrity() {
    this.allIntegrityAttribute = []
    this._commonService.getAllAttributeBySource(this.selectRefIntegrity.uuid, this.source).subscribe(
      response => {
        let temp = [];
        for (const n in response) {
          let allname = new AttributeIO();
          allname.label = response[n].dname;
          allname.value = {};
          allname.value.label = response[n].dname;
          allname.value.u_Id = response[n].id;
          allname.value.uuid = response[n].uuid;
          allname.value.attrId = response[n].attributeId;
          temp[n] = allname
          //count=count+1;
        }
        this.allIntegrityAttribute = temp
      },
      error => console.log('Error :: ' + error)
    )
  }
  countContinue() {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack() {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.onSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllLatest(response1: BaseEntity[]) {    
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
    }

    var allname = [new DropDownIO]
    for (const i in response1) {
      let name = new DropDownIO();
      response1.sort((a,b)=>a.name.localeCompare(b.name.toString()));
      name.label = response1[i].name;
      name.value = { label: "", uuid: "" };
      name.value.label = response1[i].name;
      name.value.uuid = response1[i].uuid;
      allname[i] = name;
    }
    this.allNames = allname

    this.getAllAttributeBySource();
    if (this.mode != undefined && this.IsSelectSoureceAttr) {
      this.allRefIntegrity = this.allNames;
      this.changeRefIntegrity();
    }
  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response: AttributeIO[]) {
    let firstObj = new AttributeIO();
    firstObj.label = "-Select-"
    firstObj.value = { label: "-Select-", value: "" }
    this.allAttribute.push(firstObj);

    for (const i in response) {
      let name = new AttributeIO();
      name.label = response[i].name;
      name.value = { label: "", value: "" };
      name.value.label = response[i].name;
      name.value.uuid = response[i].uuid;
      this.allAttribute.push(name);
    }
  }

  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;

    this._dataQualityService.getOneByUuidAndVersion(id, version, MetaTypeEnum.MetaType.DQ)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }
  onSuccessgetOneByUuidAndVersion(response:DataQualityIO) {

    this.breadcrumbDataFrom[2].caption = response.dataQuality.name;
    this.dqdata = response.dataQuality;

    const version: Version = new Version();
    version.label = this.dqdata.version;
    version.uuid = this.dqdata.uuid;
    this.selectedVersion = version;

    this.active == this.appHelper.convertStringToBoolean(this.dqdata.active);
    this.locked == this.appHelper.convertStringToBoolean(this.dqdata.locked);
    this.published == this.appHelper.convertStringToBoolean(this.dqdata.published);

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = this.dqdata.dependsOn.ref.name;
    dependOnTemp.uuid = this.dqdata.dependsOn.ref.uuid;
    this.sourcedata = dependOnTemp;
    this.getAllLatest()
    if (this.dqdata.attribute != null) {
      this.IsSelectSoureceAttr = true
      let selectattribute: AttributeHolder = new AttributeHolder();
      selectattribute.label = this.dqdata.attribute.ref.name + "." + this.dqdata.attribute.attrName;
      selectattribute.u_Id = this.dqdata.attribute.ref.uuid + "_" + this.dqdata.attribute.attrId;
      selectattribute.uuid = this.dqdata.attribute.ref.uuid;
      selectattribute.attrId = this.dqdata.attribute.attrId;
      this.selectAttribute = selectattribute;
    }

    if (response.isFormulaExits == true) {
      this.getFormulaByType("lhsType");
    }
    if (response.isFormulaExits == true) {
      this.getFormulaByType("rhsType");
    }
    if (response.isAttributeExits == true) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error));
    }
    if (response.isSimpleExits == true) {
    }
    if (response.isParamlistExits == true) {
      this.getParamByApp();
    }
    if (response.isFunctionExits == true) {

      this.getFunctionByCriteria();
    }

    this.filterTableArray = response.filterInfoIo;

    this.dqdata.duplicateKeyCheck = this.appHelper.convertStringToBoolean(this.dqdata.duplicateKeyCheck);
    this.dqdata.nullCheck = this.appHelper.convertStringToBoolean(this.dqdata.nullCheck);
    this.dqdata.upperBound = this.dqdata.rangeCheck.upperBound;
    this.dqdata.lowerBound = this.dqdata.rangeCheck.lowerBound;
    this.dqdata.selectDataType = this.dqdata.dataTypeCheck;
    this.dqdata.maxLength = this.dqdata.lengthCheck.maxLength;
    this.dqdata.minLength = this.dqdata.lengthCheck.minLength;
    if (this.dqdata.refIntegrityCheck.ref != null) {
      let selectrefIntegrity: DependsOn = new DependsOn();
      selectrefIntegrity.label = this.dqdata.refIntegrityCheck.ref.name;
      selectrefIntegrity.uuid = this.dqdata.refIntegrityCheck.ref.uuid;
      this.selectRefIntegrity = selectrefIntegrity

      let selectintegrityattribute: AttributeHolder = new AttributeHolder();
      selectintegrityattribute.label = this.dqdata.refIntegrityCheck.ref.name;
      selectintegrityattribute.u_Id = this.dqdata.refIntegrityCheck.ref.uuid + "_" + this.dqdata.refIntegrityCheck.attrId;
      selectintegrityattribute.uuid = this.dqdata.refIntegrityCheck.ref.uuid
      selectintegrityattribute.attrId = this.dqdata.refIntegrityCheck.attrId
      this.selectIntegrityAttribute = selectintegrityattribute;
    }
    this.isEditInprogess = false;
  }

  searchOption(index) {
    if (this.filterTableArray) {
      let values = this.filterTableArray[index].rhsAttribute;
      this.dialogAttriArray = [];
      let temp = [];
      for (const i in this.filterTableArray) {
        let dialogAttriObj = new DropDownIO();
        dialogAttriObj.label = this.filterTableArray[i].name;
        dialogAttriObj.value = { label: "", uuid: "" };
        dialogAttriObj.value.label = this.filterTableArray[i].name;
        dialogAttriObj.value.uuid = this.filterTableArray[i].uuid;
        temp[i] = dialogAttriObj;
      }
      this.dialogAttriArray = temp;

      this.dialogAttriNameArray = [];
      for (const i in this.filterTableArray) {
        let dialogAttriNameObj = new AttributeIO();
        dialogAttriNameObj.label = this.filterTableArray[i].attrName;
        dialogAttriNameObj.value = { label: "", attributeId: "", uuid: "" };
        dialogAttriNameObj.value.label = this.filterTableArray[i].attrName;
        dialogAttriNameObj.value.attributeId = this.filterTableArray[i].attrId;
        dialogAttriNameObj.value.uuid = this.filterTableArray[i].ref.uuid;
        this.dialogAttriNameArray[i] = dialogAttriNameObj;
      }
    }
    this.rowIndex = index;
    this.displayDialogBox = true;
    this._commonService.getAllLatest(MetaTypeEnum.MetaType.DATASET)
      .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBox(response) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = new DropDownIO();
      dialogAttriObj.label = response[i].name;
      dialogAttriObj.value = { label: "", uuid: "" };
      dialogAttriObj.value.label = response[i].name;
      dialogAttriObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriObj;
    }
    this.dialogAttriArray = temp
  }

  onChangeDialogAttribute() {
    this._commonService.getAttributesByDataset(MetaTypeEnum.MetaType.DATASET, this.dialogSelectName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response) {
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
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(MetaTypeEnum.MetaType.DQ, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.versionList[i] = ver;
    }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  onSuccessgetAllAttributeBySource(response: AttributeIO[]) {
    this.attributesArray = [new AttributeIO()]
    let temp1 = [];
    for (const i in response) {
      let attributeIO = new AttributeIO();
      attributeIO.label = response[i].dname;
      attributeIO.value = { label: "", uuid: "" };
      attributeIO.value.uuid = response[i].uuid;
      attributeIO.value.label = response[i].dname;
      attributeIO.value.attributeId = response[i].attributeId;
      temp1[i] = attributeIO;
      this.attributesArray = temp1;
    }
  }

  onChangeLhsType(index) {

    this.filterTableArray[index].lhsAttribute = null;

    if (this.filterTableArray[index].lhsType == MetaTypeEnum.MetaType.FORMULA) {
      this.getFormulaByType("lhsType");
    }
    else if (this.filterTableArray[index].lhsType == MetaTypeEnum.MetaType.DATAPOD) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
    }
    else {
      this.filterTableArray[index].lhsAttribute = null;
    }
  }

  onChangeRhsType(index) {
    this.filterTableArray[index].rhsAttribute = null;

    if (this.filterTableArray[index].rhsType == MetaTypeEnum.MetaType.FORMULA) {
      this.getFormulaByType("rhsType");
    }
    else if (this.filterTableArray[index].rhsType == MetaTypeEnum.MetaType.DATAPOD) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == MetaTypeEnum.MetaType.FUNCTION) {
      this.getFunctionByCriteria();
    }
    else if (this.filterTableArray[index].rhsType == MetaTypeEnum.MetaType.PARAMLIST) {
      this.getParamByApp();
    }
    else if (this.filterTableArray[index].rhsType == MetaTypeEnum.MetaType.DATASET) {
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "-Select-";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.filterTableArray[index].rhsAttribute = rhsAttribute;
    }
    else {
      this.filterTableArray[index].rhsAttribute = null;
    }
  }

  getFunctionByCriteria() {
    this._commonService.getFunctionByCriteria("", "N", MetaTypeEnum.MetaType.FUNCTION)
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetFunctionByCriteria(response: AttributeIO[]) {
    let functionArray = [new DropDownIO]
    for (const i in response) {
      let attribute = new DropDownIO();
      attribute.label = response[i].name;
      attribute.value = { label: "", uuid: "" };
      attribute.value.uuid = response[i].uuid;
      attribute.value.label = response[i].name;
      functionArray[i] = attribute
    }
    this.functionArray = functionArray;
  }

  getParamByApp() {
    this._commonService.getParamByApp("", MetaTypeEnum.MetaType.APPLICATION)
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetParamByApp(response) {
    let paramlistArray = [new AttributeIO]
    for (const i in response) {
      let attribute = new AttributeIO();
      attribute.label = "app." + response[i].paramName;
      attribute.value = { label: "", uuid: "", attributeId: "" };
      attribute.value.uuid = response[i].ref.uuid;
      attribute.value.attributeId = response[i].paramId;
      attribute.value.label = "app." + response[i].paramName;
      paramlistArray[i] = attribute;
    }
    this.paramlistArray = paramlistArray
  }

  getFormulaByType(type) {
    if (type == "lhsType") {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
          error => console.log("Error ::", error))
    }
    else if (type == "rhsType") {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
          error => console.log("Error ::", error));
    }
  }
  onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [];
    let rhsFormula = new DropDownIO();
    let RhsFormulaArray = [new DropDownIO]
    for (const i in response) {
      rhsFormula.label = response[i].name;
      rhsFormula.value = { label: "", uuid: "" };
      rhsFormula.value.label = response[i].name;
      rhsFormula.value.uuid = response[i].uuid;
      RhsFormulaArray[i] = rhsFormula;
    }
    this.rhsFormulaArray = RhsFormulaArray;
  }
  onSuccessgetFormulaByLhsType(response) {
    this.lhsFormulaArray = []
    for (const i in response) {
      let formulaObj = new DropDownIO();
      formulaObj.label = response[i].version;
      formulaObj.value = { label: "", uuid: "" };
      formulaObj.value.label = response[i].version;
      formulaObj.value.uuid = response[i].uuid;
      this.lhsFormulaArray[i] = formulaObj;
    }
  }

  onChangeOperator(index) {
    this.filterTableArray[index].rhsAttribute = null;
    if (this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS'
      || this.filterTableArray[index].operator == 'IN' || this.filterTableArray[index].operator == 'NOT IN') {
      this.filterTableArray[index].rhsType = MetaTypeEnum.MetaType.DATASET;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "-Select-";
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
  addRow() {
    if (this.filterTableArray == null) {
      this.filterTableArray = [];
    }
    var len = this.filterTableArray.length + 1
    var filertable = { logicalOperator: "", lhsType: "", lhsAttribute: "", operator: "", rhsType: "", rhsAttribute: "" };
    filertable.logicalOperator = ""
    filertable.lhsType = "integer"
    filertable.lhsAttribute = ""
    filertable.operator = ""
    filertable.rhsType = "integer"
    filertable.rhsAttribute = ""
    this.filterTableArray.splice(this.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.fitlerAttrTableSelectedItem = [];
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

  submitDq() {
    this.isSubmit = "true"
    let dqJson = new DataQuality();
    dqJson.uuid = this.dqdata.uuid;
    dqJson.name = this.dqdata.name;
    dqJson.desc = this.dqdata.desc;
    dqJson.tags = this.dqdata.tags;

    var valueCheckArr = [];
    if (this.valueCheck != null) {
      for (var counttag = 0; counttag < this.valueCheck.length; counttag++) {
        valueCheckArr[counttag] = this.valueCheck[counttag].value;
      }
    }
    dqJson.valueCheck = this.dqdata.valueCheck;

    dqJson.active = this.appHelper.convertBooleanToString(this.dqdata.active);
    dqJson.locked = this.appHelper.convertBooleanToString(this.locked);
    dqJson.published = this.appHelper.convertBooleanToString(this.dqdata.published);

    let dependsOn = new MetaIdentifierHolder();
    let ref = new MetaIdentifier();
    ref.type = this.source;
    ref.uuid = this.sourcedata.uuid;
    dependsOn.ref = ref;
    dqJson.dependsOn = dependsOn;

    if (this.selectAttribute != null) {
      let attributeref = new MetaIdentifierHolder();
      let attribute = new AttributeRefHolder();
      attributeref.type = MetaTypeEnum.MetaType.DATAPOD;
      attributeref.uuid = this.selectAttribute.uuid;
      attribute.ref = attributeref;
      attribute.attrId = this.selectAttribute.attrId;
      dqJson.attribute = attribute;
    }
    else {
      dqJson.attribute = null;
    }

    dqJson.duplicateKeyCheck = this.appHelper.convertBooleanToString(this.dqdata.duplicateKeyCheck);
    dqJson.nullCheck = this.appHelper.convertBooleanToString(this.dqdata.nullCheck);
    var tagArrayvaluecheck = [];
    if (this.valueCheck && this.valueCheck.length > 0) {
      for (var counttag = 0; counttag < this.valueCheck.length; counttag++) {
        tagArrayvaluecheck[counttag] = this.valueCheck[counttag]
      }
    }

    var rangeCheck = { lowerBound: "", upperBound: "" };
    if (typeof this.dqdata.lowerBound != "undefined" && typeof this.dqdata.upperBound != "undefined") {
      rangeCheck.lowerBound = this.dqdata.lowerBound;
      rangeCheck.upperBound = this.dqdata.upperBound;
    }
    dqJson.rangeCheck = rangeCheck;

    dqJson.dataTypeCheck = this.dqdata.selectDataType;
    dqJson.dateFormatCheck = this.selectdatefromate;
    dqJson.customFormatCheck = this.dqdata.customFormatCheck

    var lengthCheck = { minLength: "", maxLength: "" }
    if (typeof this.dqdata.minLength != "undefined" && typeof this.dqdata.minLength != "undefined") {
      lengthCheck.minLength = this.dqdata.minLength.toString();
      lengthCheck.maxLength = this.dqdata.maxLength.toString();
    }
    dqJson.lengthCheck = lengthCheck;

    let refIntegrityCheck = new AttributeRefHolder();
    if (typeof this.selectRefIntegrity != "undefined" && typeof this.selectIntegrityAttribute != "undefined") {
      ref.type = MetaTypeEnum.MetaType.DATAPOD;
      ref.uuid = this.selectRefIntegrity.uuid;
      refIntegrityCheck.ref = ref;
      refIntegrityCheck.attrId = this.selectIntegrityAttribute.attrId;
      dqJson.refIntegrityCheck = refIntegrityCheck;
    } else {
      dqJson.refIntegrityCheck = new AttributeRefHolder();
    }

    let filterInfoArray = [];
    if (this.filterTableArray != null) {
      if (this.filterTableArray.length > 0) {
        for (let i = 0; i < this.filterTableArray.length; i++) {
          let filterInfo = new FilterInfo();
          filterInfo.logicalOperator = this.filterTableArray[i].logicalOperator;
          filterInfo.operator = this.filterTableArray[i].operator;
          filterInfo.operand = [];

          if (this.filterTableArray[i].lhsType == 'integer' || this.filterTableArray[i].lhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.filterTableArray[i].lhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.filterTableArray[i].lhsType == MetaTypeEnum.MetaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.FORMULA;
            ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.filterTableArray[i].lhsType == MetaTypeEnum.MetaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.DATAPOD;
            ref.uuid = this.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.filterTableArray[i].lhsAttribute.attributeId;
            filterInfo.operand[0] = operatorObj;
          }
          if (this.filterTableArray[i].rhsType == 'integer' || this.filterTableArray[i].rhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.filterTableArray[i].rhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[1] = operatorObj;
            if (this.filterTableArray[i].rhsType == 'integer' && this.filterTableArray[i].operator == 'BETWEEN') {
              let operatorObj = new SourceAttr();
              let ref = new MetaIdentifier();
              ref.type = MetaTypeEnum.MetaType.SIMPLE;
              operatorObj.ref = ref;
              operatorObj.value = this.filterTableArray[i].rhsAttribute1 + "and" + this.filterTableArray[i].rhsAttribute2;
              filterInfo.operand[1] = operatorObj;
            }
          }
          else if (this.filterTableArray[i].rhsType == MetaTypeEnum.MetaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.FORMULA;
            ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.filterTableArray[i].rhsType == MetaTypeEnum.MetaType.FUNCTION) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.FUNCTION;
            ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.filterTableArray[i].rhsType == MetaTypeEnum.MetaType.PARAMLIST) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.PARAMLIST;
            ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.filterTableArray[i].rhsType == MetaTypeEnum.MetaType.DATASET) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.DATASET;
            ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.filterTableArray[i].rhsType == MetaTypeEnum.MetaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = MetaTypeEnum.MetaType.DATAPOD;
            ref.uuid = this.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          filterInfoArray[i] = filterInfo;
        }
        dqJson.filterInfo = filterInfoArray;
      }
    }
    this._commonService.submit(MetaTypeEnum.MetaType.DQ, dqJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(MetaTypeEnum.MetaType.DQ, response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmit = "false";
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'DQ Save Successfully' });
      setTimeout(() => {
        this.goBack();
      }, 1000);
    }
  }

  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, MetaTypeEnum.MetaType.DQ, "execute").subscribe(
      response => {
        this.showMessage('DQ Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()
        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }

  showMessage(msg, msgtype, msgsumary) {
    this.isSubmit = "false";
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }


  showview(uuid, version) {
    this.router.navigate(['app/dataQuality/dq', uuid, version, 'true']);
  }



  // onAttrRowDown() {
  //   for (let i = 0; i < this.filterTableArray.length; i++) {
  //     if (this.filterTableArray[i].selected) {
  //       this.filterTableArray.splice(this.filterTableArray.length, 0, this.filterTableArray[i]);
  //       this.filterTableArray[i].selected = false;
  //       if (i > 1) {
  //         this.filterTableArray.splice(i, 1);
  //       }
  //       break;
  //     }
  //   }
  //   this.isSubmit = true
  // }

  // onAttrRowUp() {
  //   for (let i = 0; i < this.filterTableArray.length; i++) {
  //     if (this.filterTableArray[i].selected) {
  //       this.filterTableArray.splice(0, 0, this.filterTableArray[i]);
  //       this.filterTableArray[0].logicalOperator = ""
  //       this.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
  //       if (i > 1) {
  //         this.filterTableArray.splice(i + 1, 1);
  //         this.filterTableArray[i].selected = false;
  //       }
  //       break;
  //     }
  //   }
  //   this.isSubmit = true
  // }

  dragStart(event, data) {
    // console.log(event)
    // console.log(data)
    this.dragIndex = data
  }
  dragEnd(event) {
    // console.log(event)
  }
  drop(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data
      // console.log(event)
      // console.log(data)
      var item = this.dqdata.filterTableArray[this.dragIndex]
      this.dqdata.filterTableArray.splice(this.dragIndex, 1)
      this.dqdata.filterTableArray.splice(this.dropIndex, 0, item)
      this.isSubmit = true
    }
  }

  onChangeFilterAttRow = function (index, status) {
    this.fitlerAttrTableSelectedItem = [];
    if (status == true) {
      this.fitlerAttrTableSelectedItem.push(index);
    }
    else {
      let tempIndex = this.fitlerAttrTableSelectedItem.indexOf(index);
      if (tempIndex != -1) {
        this.fitlerAttrTableSelectedItem.splice(tempIndex, 1);
      }
    }
  }

  autoMove = function (index, type) {
    if (type == "mapAttr") {
    }
    else {
      var tempAtrr = this.fitlerAttrTableSelectedItem[0];
      this.filterTableArray.splice(this.fitlerAttrTableSelectedItem[0], 1);
      this.filterTableArray.splice(index, 0, tempAtrr);
      this.fitlerAttrTableSelectedItem = [];
      this.filterTableArray[index].selected = false;
      this.filterTableArray[0].logicalOperator = "";
      if (this.filterTableArray[index].logicalOperator == "" && index != 0) {
        this.filterTableArray[index].logicalOperator = this.logicalOperator[0];
      } else if (this.filterTableArray[index].logicalOperator == "" && index == 0) {
        this.filterTableArray[index + 1].logicalOperator = this.logicalOperator[0];
      }
    }
  }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {
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
          this.checkSelected(false);
        }
        else if (new_index == this.filterTableArray.length - 1) {
          this.filterTableArray[0].logicalOperator = "";
          this.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
          this.filterTableArray[i].selected = "";
          this.checkSelected(false);
        }
        break;
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
  checkSelected(flag) {
    if (flag == true) {
      this.count.push(flag);
    }
    else
      this.count.pop();

    this.moveToEnable = (this.count.length == 1) ? true : false;
  }


}