import { FilterInfoIO } from './../metadata/domainIO/domain.filterInfoIO';
import { FilterInfo } from './../metadata/domain/domain.filterInfo';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
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
@Component({
  selector: 'app-data-pipeli',
  templateUrl: './data-qualitydetail.template.html',
})
export class DataQualityDetailComponent {
  dropIndex: any;
  dragIndex: any;
  showGraph: boolean;
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
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  dataqualitycompare: any;
  valueCheck: any;
  allRefIntegrity: any[];
  selectdatefromate: any;
  selectDataType: any;
  selectedAllFitlerRow: boolean;
  //lhsdatapodattributefilter: any[];
  operators: any;
  logicalOperators: any;
  filterTableArray: any;
  allIntegrityAttribute: any[];
  selectIntegrityAttribute: any;
  selectRefIntegrity: any;
  datefromate: string[];
  datatype: any;
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
  isEditInprogess: boolean;
  isEditError: boolean;
  showForm: boolean;
  fitlerAttrTableSelectedItem: any[] = [];
  allname: Array<DropDownIO>;
  published: boolean;
  active: boolean;
  locked: boolean;

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _dataQualityService: DataQualityService, public appHelper: AppHelper) {
    this.dqdata = new DataQuality();
    this.showGraph = false
    this.isHomeEnable = false
    this.displayDialogBox = false;

    this.isEditInprogess = false;
    this.isEditError = false;
    this.showForm = true;

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
    this.filterTableArray = null;
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
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dq']);
  }
  changeType() {
    this.selectAttribute = null;
    this.getAllAttributeBySource();
  }
  OnselectType = function () {
    if (this.dqdata.selectDataType == "Date") {
      this.IsSelectDataType = true;
    }
    else {
      this.IsSelectDataType = false;
    }
  }
  onSourceAttributeChagne = function () {
    if (this.selectAttribute != null) {
      this.IsSelectSoureceAttr = true
      this.dqdata.nullCheck = true;
      this.allRefIntegrity = this.allNames;
      this.allIntegrityAttribute = this.allAttribute;
    }
    else {
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
  }
  changeRefIntegrity() {
    this.allIntegrityAttribute = []
    this._commonService.getAllAttributeBySource(this.selectRefIntegrity.uuid, this.source).subscribe(
      response => {
        let temp = [];
        for (const n in response) {
          let allname = {};
          allname["label"] = response[n]['dname'];
          allname["value"] = {};
          allname["value"]["label"] = response[n]['dname'];
          allname["value"]["u_Id"] = response[n]['id'];
          allname["value"]["uuid"] = response[n]['uuid'];
          allname["value"]["attrId"] = response[n]['attributeId'];
          temp[n] = allname
          //count=count+1;
        }
        this.allIntegrityAttribute = temp
      },
      error => console.log('Error :: ' + error)
    )
  }
  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response1: BaseEntity[]) {
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
    }

    var allname = [new DropDownIO]
    for (const i in response1) {
      let name = new DropDownIO();
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
    this.allAttribute = response;
  }

  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;

    this._dataQualityService.getOneByUuidAndVersion(id, version, 'dq')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.dataQuality.name;
    this.dqdata = response.dataQuality;

    const version: Version = new Version();
    version.label = this.dqdata.version;
    version.uuid = this.dqdata.uuid;
    this.selectedVersion = version;

    // if (response.tags != null) {
    //   this.dqdata.tags =response.tags;
    // }//End If

    this.active == this.appHelper.convertStringToBoolen(this.dqdata.active);
    this.locked == this.appHelper.convertStringToBoolen(this.dqdata.locked);
    this.published == this.appHelper.convertStringToBoolen(this.dqdata.published);

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

    console.log(response.isFunctionExits);
    if (response.isFormualExits == true) {
      this.getFormulaByType("lhsType");
    }
    if (response.isFormualExits == true) {
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

    // let valueCheck = [];
    // if (this.dqdata.valueCheck != null) {
    //   for (var i = 0; i < this.dqdata.valueCheck.length; i++) {
    //     var valueCheck1 = {};
    //     valueCheck1['value'] = this.dqdata.valueCheck[i];
    //     valueCheck1['display'] = this.dqdata.valueCheck[i];
    //     valueCheck[i] = valueCheck1
    //   }//End For
    //   this.valueCheck = valueCheck;
    // }

    this.dqdata.duplicateKeyCheck = this.appHelper.convertStringToBoolen(this.dqdata.duplicateKeyCheck);
    this.dqdata.nullCheck = this.appHelper.convertStringToBoolen(this.dqdata.nullCheck);
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
    //this.showForm = false;
  }

  searchOption(index) {
    this.displayDialogBox = true;
    this._commonService.getAllLatest("dataset")
      .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBox(response) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
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

  onChangeDialogAttribute() {
    this._commonService.getAttributesByDataset("dataset", this.dialogSelectName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = {};
      dialogAttriNameObj["label"] = response[i].attrName;
      dialogAttriNameObj["value"] = {};
      dialogAttriNameObj["value"]["label"] = response[i].attrName;
      dialogAttriNameObj["value"]["attributeId"] = response[i].attrId;
      dialogAttriNameObj["value"]["uuid"] = response[i].ref.uuid;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index) {
    this.displayDialogBox = false;
    let rhsattribute = {}
    rhsattribute["label"] = this.dialogAttributeName.label;
    rhsattribute["uuid"] = this.dialogAttributeName.uuid;
    rhsattribute["attributeId"] = this.dialogAttributeName.attributeId;
    this.dqdata.filterTableArray[index].rhsAttribute = rhsattribute;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('dq', this.id)
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
    // var VersionList = [new DropDownIO]
    // for (const i in response) {
    //   let verObj = new DropDownIO();
    //   verObj.label = response[i].version;
    //   verObj.value.label = response[i].version;
    //   verObj.value.uuid = response[i].uuid;
    //   this.VersionList[i] = verObj;
    // }
    // // this.VersionList = VersionList

  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }



  onSuccessgetAllAttributeBySource(response: AttributeIO[]) {
    // this.dialogAttriArray = [];
    // let temp = [];
    // for (const i in response) {
    // let dialogAttriObj = {};
    // dialogAttriObj["label"] = response[i].name;
    // dialogAttriObj["value"] = {};
    // dialogAttriObj["value"]["label"] = response[i].name;
    // dialogAttriObj["value"]["uuid"] = response[i].uuid;
    // temp[i] = dialogAttriObj;
    // }
    // this.dialogAttriArray = temp
    // console.log(JSON.stringify(this.dialogAttriArray));
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
    console.log(JSON.stringify(this.attributesArray));
  }

  onChangeLhsType(index) {

    this.filterTableArray[index].lhsAttribute = null;

    if (this.filterTableArray[index].lhsType == 'formula') {

      this.getFormulaByType("lhsType");
      // this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
      //   .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
      //     error => console.log("Error ::", error))
    }

    else if (this.filterTableArray[index].lhsType == 'datapod') {
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

    if (this.filterTableArray[index].rhsType == 'formula') {
      this.getFormulaByType("rhsType");

    }
    else if (this.filterTableArray[index].rhsType == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
          error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index].rhsType == 'function') {
      this.getFunctionByCriteria();

    }
    else if (this.filterTableArray[index].rhsType == 'paramlist') {
      this.getParamByApp();
    }
    else if (this.filterTableArray[index].rhsType == 'dataset') {
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
    this._commonService.getFunctionByCriteria("", "N", "function")
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetFunctionByCriteria(response: AttributeIO[]) {

    // let temp = [];
    // for (const i in response) {
    //   let attributeObj = {};
    //   attributeObj["label"] = response[i].name;
    //   attributeObj["value"] = {};
    //   attributeObj["value"]["uuid"] = response[i].uuid;
    //   attributeObj["value"]["label"] = response[i].name;
    //   temp[i] = attributeObj
    // }
    // this.functionArray = temp;
    // 
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
    this._commonService.getParamByApp("", "application")
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetParamByApp(response) {
    // let temp = [];
    // for (const i in response) {
    //   let attributeObj = {};
    //   attributeObj["label"] = "app." + response[i].paramName;
    //   attributeObj["value"] = {};
    //   attributeObj["value"]["uuid"] = response[i].ref.uuid;
    //   attributeObj["value"]["attributeId"] = response[i].paramId;
    //   attributeObj["value"]["label"] = "app." + response[i].paramName;
    //   temp[i] = attributeObj
    // }
    // this.paramlistArray = temp;
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
    // this.rhsFormulaArray = [];
    // let rhsFormulaObj = {};
    // let temp = [];
    // for (const i in response) {
    //   rhsFormulaObj["label"] = response[i].name;
    //   rhsFormulaObj["value"] = {};
    //   rhsFormulaObj["value"]["label"] = response[i].name;
    //   rhsFormulaObj["value"]["uuid"] = response[i].uuid;
    //   temp[i] = rhsFormulaObj;
    // }
    // this.rhsFormulaArray = temp
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
    // this.lhsFormulaArray = []
    // for (const i in response) {
    //   let formulaObj = {};
    //   formulaObj["label"] = response[i].name;
    //   formulaObj["value"] = {};
    //   formulaObj["value"]["uuid"] = response[i].uuid;
    //   formulaObj["value"]["label"] = response[i].name;
    //   this.lhsFormulaArray[i] = formulaObj;
    // }
    this.lhsFormulaArray = []
    for (const i in response) {
      let formulaObj = new DropDownIO();
      formulaObj.label = response[i].version;
      formulaObj.value = { label: "", uuid: "" };
      formulaObj.value.label = response[i].version;
      formulaObj.value.uuid = response[i].uuid;
      //VersionList[i] = verObj;
      this.lhsFormulaArray[i] = formulaObj;
    }
  }

  onChangeOperator(index) {

    this.filterTableArray[index].rhsAttribute = null;
    if (this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS') {
      this.filterTableArray[index].rhsType = 'dataset';
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

  dagSubmit() {
    this.isSubmit = "true"
    let dqJson = {};
    dqJson["uuid"] = this.dqdata.uuid;
    dqJson["name"] = this.dqdata.name;
    dqJson["desc"] = this.dqdata.desc;
    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;
      }
    }
    dqJson['tags'] = tagArray;

    var valueCheckArr = [];
    if (this.valueCheck != null) {
      for (var counttag = 0; counttag < this.valueCheck.length; counttag++) {
        valueCheckArr[counttag] = this.valueCheck[counttag].value;
      }
    }
    dqJson['valueCheck'] = valueCheckArr;

    dqJson["active"] = this.dqdata.active == true ? 'Y' : "N";
    dqJson["locked"] = this.appHelper.convertBoolenToString(this.locked);
    dqJson["published"] = this.dqdata.published == true ? 'Y' : "N"
    let dependsOn = {};
    let ref = {};
    ref["type"] = this.source
    ref["uuid"] = this.sourcedata.uuid;
    dependsOn["ref"] = ref;
    dqJson["dependsOn"] = dependsOn;
    if (this.selectAttribute != null) {
      let attributeref = {};
      let attribute = {};
      attributeref["type"] = "datapod";
      attributeref["uuid"] = this.selectAttribute.uuid;
      attribute["ref"] = attributeref;
      attribute["attrId"] = this.selectAttribute.attrId;
      dqJson["attribute"] = attribute;
    }
    else {
      dqJson["attribute"] = null;
    }
    dqJson["duplicateKeyCheck"] = this.dqdata.duplicateKeyCheck == true ? 'Y' : 'N';
    dqJson["nullCheck"] = this.dqdata.nullCheck == true ? 'Y' : 'N';
    var tagArrayvaluecheck = [];
    if (this.valueCheck && this.valueCheck.length > 0) {
      for (var counttag = 0; counttag < this.valueCheck.length; counttag++) {
        tagArrayvaluecheck[counttag] = this.valueCheck[counttag]
      }
    }

    var rangeCheck = {};
    if (typeof this.dqdata.lowerBound != "undefined" && typeof this.dqdata.upperBound != "undefined") {
      rangeCheck["lowerBound"] = this.dqdata.lowerBound;
      rangeCheck["upperBound"] = this.dqdata.upperBound;
    }
    dqJson["rangeCheck"] = rangeCheck;
    dqJson["dataTypeCheck"] = this.dqdata.selectDataType;
    dqJson["dateFormatCheck"] = this.selectdatefromate;
    dqJson["customFormatCheck"] = this.dqdata.customFormatCheck
    var lengthCheck = {}
    if (typeof this.dqdata.minLength != "undefined" && typeof this.dqdata.minLength != "undefined") {
      lengthCheck["minLength"] = this.dqdata.minLength;
      lengthCheck["maxLength"] = this.dqdata.maxLength;

    }
    dqJson["lengthCheck"] = lengthCheck
    let refIntegrityCheck = {};
    let refInte = {};
    if (typeof this.selectRefIntegrity != "undefined" && typeof this.selectIntegrityAttribute != "undefined") {
      ref["type"] = "datapod";
      ref["uuid"] = this.selectRefIntegrity.uuid;
      refIntegrityCheck["ref"] = ref;
      refIntegrityCheck["attrId"] = this.selectIntegrityAttribute.attrId;
      dqJson["refIntegrityCheck"] = refIntegrityCheck;
    } else {
      dqJson["refIntegrityCheck"] = {};
    }

    let filterInfoArray = [];
    if (this.dqdata.filterTableArray != null) {
      if (this.dqdata.filterTableArray.length > 0) {
        for (let i = 0; i < this.dqdata.filterTableArray.length; i++) {
          let filterInfo = {};
          filterInfo["logicalOperator"] = this.dqdata.filterTableArray[i].logicalOperator;
          filterInfo["operator"] = this.dqdata.filterTableArray[i].operator;
          filterInfo["operand"] = [];

          if (this.dqdata.filterTableArray[i].lhsType == 'integer' || this.dqdata.filterTableArray[i].lhsType == 'string') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.dqdata.filterTableArray[i].lhsAttribute;
            operatorObj["attributeType"] = "string"
            filterInfo["operand"][0] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].lhsType == 'formula') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "formula";
            ref["uuid"] = this.dqdata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj["ref"] = ref;
            // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
            filterInfo["operand"][0] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].lhsType == 'datapod') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "datapod";
            ref["uuid"] = this.dqdata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj["ref"] = ref;
            operatorObj["attributeId"] = this.dqdata.filterTableArray[i].lhsAttribute.attributeId;
            filterInfo["operand"][0] = operatorObj;
          }
          if (this.dqdata.filterTableArray[i].rhsType == 'integer' || this.dqdata.filterTableArray[i].rhsType == 'string') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.dqdata.filterTableArray[i].rhsAttribute;
            operatorObj["attributeType"] = "string"
            filterInfo["operand"][1] = operatorObj;

            if (this.dqdata.filterTableArray[i].rhsType == 'integer' && this.dqdata.filterTableArray[i].operator == 'BETWEEN') {
              let operatorObj = {};
              let ref = {}
              ref["type"] = "simple";
              operatorObj["ref"] = ref;
              operatorObj["value"] = this.dqdata.filterTableArray[i].rhsAttribute1 + "and" + this.dqdata.filterTableArray[i].rhsAttribute2;
              filterInfo["operand"][1] = operatorObj;
            }
          }
          else if (this.dqdata.filterTableArray[i].rhsType == 'formula') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "formula";
            ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj["ref"] = ref;
            //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
            filterInfo["operand"][1] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].rhsType == 'function') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "function";
            ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj["ref"] = ref;
            //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
            filterInfo["operand"][1] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].rhsType == 'paramlist') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "paramlist";
            ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj["ref"] = ref;
            operatorObj["attributeId"] = this.dqdata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo["operand"][1] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].rhsType == 'dataset') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "dataset";
            ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj["ref"] = ref;
            operatorObj["attributeId"] = this.dqdata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo["operand"][1] = operatorObj;
          }
          else if (this.dqdata.filterTableArray[i].rhsType == 'datapod') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "datapod";
            ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj["ref"] = ref;
            operatorObj["attributeId"] = this.dqdata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo["operand"][1] = operatorObj;
          }
          filterInfoArray[i] = filterInfo;
        }
        dqJson["filterInfo"] = filterInfoArray;
        console.log(JSON.stringify(filterInfoArray));
      }
    }

    console.log(JSON.stringify(dqJson));
    this._commonService.submit("dq", dqJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("dq", response).subscribe(
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
    this._commonService.execute(response.uuid, response.version, "dq", "execute").subscribe(
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

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataQuality/dq', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataQuality/dq', uuid, version, 'true']);
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showGraph = false;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
  }

  onAttrRowDown() {
    //this.shiftingRow(this.filterTableArray.length);
    for (let i = 0; this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {
        this.filterTableArray.splice(this.filterTableArray.length, 0, this.filterTableArray[i]);
        if (i > 1) {
          this.filterTableArray.splice(i, 1);
        }
        break;
      }
    }
    // var rowTempIndex = this.dqdata.filterTableArray[index];
    // var rowTempIndexPlus = this.dqdata.filterTableArray[index + 1];
    // this.dqdata.filterTableArray[index] = rowTempIndexPlus;
    // this.dqdata.filterTableArray[index + 1] = rowTempIndex;
    // this.isSubmit = true
  }

  onAttrRowUp() {
    //this.shiftingRow(0);
    for (let i = 0; this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {
        this.filterTableArray.splice(0, 0, this.filterTableArray[i]);
        if (i > 1) {
          this.filterTableArray.splice(i+1, 1);
        }
        break;
      }
    }
    // var rowTempIndex = this.filterTableArray[index];
    // var rowTempIndexMines = this.filterTableArray[0];
    // this.filterTableArray[index] = rowTempIndexMines;
    // this.filterTableArray[0] = rowTempIndex;
    this.isSubmit = true
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

  autoMoveTo(index) {
    
    // if (type == "mapAttr") {
    // }
    // else {
    //   if (index <= this.filterTableArray.length) {
    //     this.autoMove(index - 1, 'filterAttr');
    //     this.moveTo = null;
    //     //	$(".actions").removeClass("open");
    //   }
    // }
    for (let i = 0; this.filterTableArray.length; i++) {
      if (this.filterTableArray[i].selected) {
        this.filterTableArray.splice(0, 0, this.filterTableArray[i]);
        if (i > 1) {
          this.filterTableArray.splice(index, 1);
        }
        break;
      }
    }

  }


}