
import { Component, Input, OnInit } from '@angular/core';
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
@Component({
  selector: 'app-data-recon',
  templateUrl: './data-recondetail.template.html',
})
export class DataReconDetailComponent {
  showGraph: boolean;
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
  selectTargetType: DependsOn;
  selectTargetFunction: DependsOn;
  selectSourceAtrribute: DependsOn;
  allSourceFunction: any[];
  allSourceAtrribute: any[];
  selectSoueceFunction: DependsOn;
  selectSourceType: DependsOn;
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
  datatype: string[];
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
  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataReconService: DataReconService) {
    this.recondata = {};
    this.displayDialogBox = false;
    this.displayDialogBoxTarget = false;
    this.dialogAttributeName = {};
    this.dialogAttributeNameTarget = {};
    this.datatype = ["", "String", "Int", "Float", "Double", "Date"];
    this.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
    this.continueCount = 1;
    this.IsSelectSoureceAttr = false;
    this.isSubmit = "false"
    this.sources = ["datapod","dataset"];
    this.source = this.sources[0];
    this.target = this.sources[0];
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.selectDataType = "";
    this.selectdatefromate = "";
    this.dataqualitycompare = null;
    this.sourceTableArray = null;
    this.recondata["active"] = true
    this.breadcrumbDataFrom = [{
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
    }
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
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
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
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/recon']);
  }
  onChangeSource() {
    this.selectAttribute = null;
    this.getAllAttributeBySource();
    this.filterTableArray=null
  }
  onChangeTarget() {
    this.selectAttribute = null;
    this.getAllAttributeByTarget();
    this.targetFilterTableArray=null
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
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatestSource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatestSource(response1) {
    let temp = []
    if (this.mode == undefined) {
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
    if (this.mode !== undefined) {
      this.getAllAttributeBySource();
     // this.getAllAttributeByTarget();
    }
    // if(this.mode != undefined  && this.IsSelectSoureceAttr) {
    //   this.allRefIntegrity=this.allNames;
    //   this.changeRefIntegrity();

    // }
  }
  getAllLatestTarget() {
    this._commonService.getAllLatest(this.target).subscribe(
      response => { this.OnSuccesgetAllLatestTarget(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatestTarget(response1) {
    let temp = []
    if (this.mode == undefined) {
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
    this.allNamesTarget = temp
    if (this.mode !== undefined) {
      
      this.getAllAttributeByTarget();
    }

  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = {};
    allname["label"] = '-select-';
    allname["value"] = null
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["u_Id"] = response[n]['id'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["attrId"] = response[n]['attributeId'];
      temp[n] = allname
      attribute[n] = allname

      //count=count+1;
    }
    this.allSourceAtrribute = temp
    // this.lhsdatapodattributefilter=attribute
    // this.allAttribute.splice(0, 0, allname);
    // this.lhsdatapodattributefilter.splice(0,1);
  }
  getAllAttributeByTarget() {
    this._commonService.getAllAttributeBySource(this.selectTargetType.uuid, this.target).subscribe(
      response => { this.OnSuccesgetAllAttributeByTarget(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeByTarget(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = {};
    allname["label"] = '-select-';
    allname["value"] = null
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["u_Id"] = response[n]['id'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["attrId"] = response[n]['attributeId'];
      temp[n] = allname
      attribute[n] = allname

      //count=count+1;
    }
    this.allTargetAtrribute = temp
    // this.lhsdatapodattributefilter=attribute
    // this.allAttribute.splice(0, 0, allname);
    // this.lhsdatapodattributefilter.splice(0,1);
  }
  getFunctionByCategory() {
    this._dataReconService.getFunctionByCategory("function").subscribe(
      response => { this.OnSuccesgetFunctionByCategory(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetFunctionByCategory(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = {};
    allname["label"] = '-select-';
    allname["value"] = null
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["u_Id"] = response[n]['id'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["attrId"] = response[n]['attributeId'];
      temp[n] = allname
      attribute[n] = allname

      //count=count+1;
    }
    this.allSourceFunction = temp
    // this.lhsdatapodattributefilter=attribute
    // this.allAttribute.splice(0, 0, allname);
    // this.lhsdatapodattributefilter.splice(0,1);
  }
  getOneByUuidAndVersion(id, version) {
    this._dataReconService.getOneByUuidAndVersion(id, version, 'recon')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.recondata.name
    this.recondata = response.recondata;
    this.dataqualitycompare = response.recondata;
    console.log(response.filterInfo);
    this.sourceTableArray = response.filterInfo
    this.createdBy = response.recondata.createdBy.ref.name
    this.recondata.published = response.recondata["published"] == 'Y' ? true : false
    
    this.recondata.active = response.recondata["locked"] == 'Y' ? true : false
    this.recondata.sourceDistinct = response.recondata["sourceDistinct"] == 'Y' ? true : false
    this.recondata.targetDistinct = response.recondata["targetDistinct"] == 'Y' ? true : false
    this.tags = response.recondata['tags'];
    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response.recondata['version'];
    version.uuid = response.recondata['uuid'];
    this.selectedVersion = version
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response.recondata["sourceAttr"]["ref"]["name"];
    dependOnTemp.uuid = response.recondata["sourceAttr"]["ref"]["uuid"];
    //this.sourcedata=dependOnTemp;
    this.selectSourceType = dependOnTemp;
    let dependOn: DependsOn = new DependsOn();
    dependOn.label = response.recondata["sourceFunc"]["ref"]["name"];
    dependOn.uuid = response.recondata["sourceFunc"]["ref"]["uuid"];
    //this.sourcedata=dependOnTemp;
    this.selectSoueceFunction = dependOn;
    let sourceAttr: DependsOn = new DependsOn();
    sourceAttr.label = response.recondata["sourceAttr"]["attrName"];
    sourceAttr["attrId"] = response.recondata["sourceAttr"]["attrId"];
    //this.sourcedata=dependOnTemp;
    this.selectSourceAtrribute = sourceAttr;
    let target: DependsOn = new DependsOn();
    target.label = response.recondata["targetAttr"]["ref"]["name"];
    target.uuid = response.recondata["targetAttr"]["ref"]["uuid"];
    //this.sourcedata=dependOnTemp;
    this.selectTargetType = target;
    let targetFun: DependsOn = new DependsOn();
    targetFun.label = response.recondata["targetFunc"]["ref"]["name"];
    targetFun.uuid = response.recondata["targetFunc"]["ref"]["uuid"];
    //this.sourcedata=dependOnTemp;
    this.selectTargetFunction = targetFun;
    let targetAttr: DependsOn = new DependsOn();
    targetAttr.label = response.recondata["targetAttr"]["attrName"];
    targetAttr["attrId"] = response.recondata["targetAttr"]["attrId"];
    this.source=response.recondata["sourceAttr"]["ref"]["type"];
    this.target=response.recondata["targetAttr"]["ref"]["type"];
    //this.sourcedata=dependOnTemp;
    // this.getAllVersionByUuid();
    this.getAllLatestSource();
    this.getAllLatestTarget();
    this.selectTargetAtrribute = targetAttr;

    //this.selectTargetType=this.reconruledata.targetAttr.ref.type; 
    // if (response.recondata["sourceFilter"]) {
    //   this.sourceTableArray = response.recondata["sourceFilter"]
    // }
    // else{
    //   this.sourceTableArray=[]
    // }
    // if (response.recondata["targetFilter"]) {
    //   this.targettableArray = response.recondata["targetFilter"]
    // }
    // else {

    // }

    this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
      .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
      error => console.log("Error ::", error))

    this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
      .subscribe(response => { this.onSuccessgetFormulaByType(response) },
      error => console.log("Error ::", error))

    this._commonService.getFunctionByCriteria("", "N", "function")
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
      error => console.log("Error ::", error))

    this._commonService.getParamByApp("", "application")
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
      error => console.log("Error ::", error))

    this.filterTableArray = response.filterTableArray
    this.targetFilterTableArray = response.targetFilterTableArray
  }

  onChangeRhsType(index) {
    this.filterTableArray[index]["rhsAttribute"] = null;

    if (this.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'function') {
      this._commonService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'paramlist') {
      this._commonService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
    }
    else if (this.filterTableArray[index]["rhsType"] == 'dataset') {
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
    this.filterTableArray[index]["lhsAttribute"] = null;
    if (this.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }
    else {
      this.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsTypeTarget(index) {
    this.targetFilterTableArray[index]["rhsAttribute"] = null;

    if (this.targetFilterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
        error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index]["rhsType"] == 'function') {
      this._commonService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index]["rhsType"] == 'paramlist') {
      this._commonService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index]["rhsType"] == 'dataset') {
      let rhsAttribute = {};
      rhsAttribute["label"] = "-Select-";
      rhsAttribute["uuid"] = "";
      rhsAttribute["attributeId"] = "";
      this.targetFilterTableArray[index]["rhsAttribute"] = rhsAttribute;
    }
    else {
      this.targetFilterTableArray[index]["rhsAttribute"] = null;
    }
  }

  onChangeLhsTypeTarget(index) {
    this.targetFilterTableArray[index]["lhsAttribute"] = null;
    if (this.targetFilterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByType(response) },
        error => console.log("Error ::", error))
    }
    else if (this.targetFilterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }
    else {
      this.targetFilterTableArray[index]["lhsAttribute"] = null;
    }
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
      attributeObj["value"]["id"] = response[i].uuid+"_"+response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('recon', this.id)
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

  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "recon", "execute").subscribe(
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

  enableEdit(uuid, version) {
    this.router.navigate(['app/recon/createreconerule', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/recon/createreconerule', uuid, version, 'true']);
  }

  getAllAttributeBySourceDrop(defaultValue, index, type) {
    this._commonService.getAllAttributeBySource(this.selectSourceType.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySourceDrop(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  changeSourceType(){
    this.getAllLatestSource()
  }
  changeTargetType(){
    
    this.getAllLatestTarget();
    // this._commonService.getAllLatest(this.target).subscribe(
    //   response => { this.OnSuccesgetAllLatest(response) },
    //   error => console.log('Error :: ' + error)
    // )
  }
  OnSuccesgetAllAttributeBySourceDrop(response2, defaultValue, index, type) {
    let temp = []
    for (const n in response2) {
      let allname1 = {};
      allname1["label"] = response2[n]['dname'];
      allname1["value"] = {};
      allname1["value"]["label"] = response2[n]['dname'];
      allname1["value"]["id"] = response2[n]['id'];
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
    this._commonService.getFormulaByType(this.selectSourceType.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormula(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response, defaultValue, index, type) {
    let temp = []
    if (response[0]) {
      let sourceformula = {};
      sourceformula["label"] = response[0].name;
      sourceformula["uuid"] = response[0].uuid;
      if (type == 'lhs') {
        this.sourceTableArray[index].lhsformula = sourceformula;
      } else {
        this.sourceTableArray[index].rhsformula = sourceformula;

      }
    }
    for (const n in response) {
      let allname1 = {};
      allname1["label"] = response[n]['name'];
      allname1["value"] = {};
      allname1["value"]["label"] = response[n]['name'];
      allname1["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname1;
    }
    this.allFormula = temp
  }

  getAllAttributeBySourceTarget(defaultValue, index, type) {
    this._commonService.getAllAttributeBySource(this.selectTargetType.uuid, this.target).subscribe(
      response => { this.OnSuccesgetAllAttributeBySourceTarget(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySourceTarget(response2, defaultValue, index, type) {
    //   
    let temp = []
    for (const n in response2) {
      let allname1 = {};
      allname1["label"] = response2[n]['dname'];
      allname1["value"] = {};
      allname1["value"]["label"] = response2[n]['dname'];
      allname1["value"]["id"] = response2[n]['id'];
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
    this._commonService.getFormulaByType(this.selectTargetType.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormulaTarget(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormulaTarget(response, defaultValue, index, type) {
    let temp = []
    if (response[0]) {
      let sourceformula = {};
      sourceformula["label"] = response[0].name;
      sourceformula["uuid"] = response[0].uuid;
      if (type == 'lhs') {
        this.targettableArray[index].lhsformula = sourceformula;
      } else {
        this.targettableArray[index].rhsformula = sourceformula;
      }
    }
    for (const n in response) {
      let allname1 = {};
      allname1["label"] = response[n]['name'];
      allname1["value"] = {};
      allname1["value"]["label"] = response[n]['name'];
      allname1["value"]["uuid"] = response[n]['uuid'];
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

  addRowTarget() {
    if (this.targetFilterTableArray == null) {
      this.targetFilterTableArray = [];
    }
    var len = this.targetFilterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = "integer"
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = "integer"
    filertable["rhsAttribute"] = ""
    this.targetFilterTableArray.splice(this.targetFilterTableArray.length, 0, filertable);
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

  onChangeOperator(index){
    this.filterTableArray[index].rhsAttribute = null;
    if(this.filterTableArray[index].operator == 'EXISTS' || this.filterTableArray[index].operator == 'NOT EXISTS'){
      this.filterTableArray[index].rhsType = 'dataset' ;
    }
    else{
			this.filterTableArray[index].rhsType = 'integer';
		}	
  }

  onChangeOperatorTarget(index){
    this.filterTableArray[index].rhsAttribute = null;
    if(this.targetFilterTableArray[index].operator == 'EXISTS' || this.targetFilterTableArray[index].operator == 'NOT EXISTS'){
      this.targetFilterTableArray[index].rhsType = 'dataset' ;
    }
    else{
			this.filterTableArray[index].rhsType = 'integer';
		}	
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
    this.filterTableArray[index].rhsAttribute = rhsattribute;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }


  searchOptionTarget(index) {
    this.displayDialogBoxTarget = true;
    this._commonService.getAllLatest("dataset")
      .subscribe(response => { this.onSuccessgetAllLatestDialogBoxTarget(response) },
      error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBoxTarget(response) {
    this.dialogAttriArrayTarget = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = {};
      dialogAttriObj["label"] = response[i].name;
      dialogAttriObj["value"] = {};
      dialogAttriObj["value"]["label"] = response[i].name;
      dialogAttriObj["value"]["uuid"] = response[i].uuid;
      temp[i] = dialogAttriObj;
    }
    this.dialogAttriArrayTarget = temp
    console.log(JSON.stringify(this.dialogAttriArrayTarget));
  }

  onChangeDialogTargetAttribute() {
    this._commonService.getAttributesByDataset("dataset", this.dialogSelectNameTarget.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBoxTarget(response) },
      error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBoxTarget(response) {
    this.dialogAttriNameArrayTarget = [];
    for (const i in response) {
      let dialogAttriNameObj = {};
      dialogAttriNameObj["label"] = response[i].attrName;
      dialogAttriNameObj["value"] = {};
      dialogAttriNameObj["value"]["label"] = response[i].attrName;
      dialogAttriNameObj["value"]["attributeId"] = response[i].attrId;
      dialogAttriNameObj["value"]["uuid"] = response[i].ref.uuid;

      this.dialogAttriNameArrayTarget[i] = dialogAttriNameObj;
    }
  }

  submitDialogBoxTarget(index) {
    this.displayDialogBoxTarget = false;
    let rhsattribute = {}
    rhsattribute["label"] = this.dialogAttributeNameTarget.label;
    rhsattribute["uuid"] = this.dialogAttributeNameTarget.uuid;
    rhsattribute["attributeId"] = this.dialogAttributeNameTarget.attributeId;
    this.targetFilterTableArray[index].rhsAttribute = rhsattribute;
  }

  cancelDialogBoxTarget() {
    this.displayDialogBoxTarget = false;
  }

  submit() {
    this.isSubmitEnable = true;
    this.isSubmit = "true"
    let dqJson = {};
    dqJson["uuid"] = this.recondata.uuid;
    dqJson["name"] = this.recondata.name;
    dqJson["desc"] = this.recondata.desc;
    let tagArray = [];
    if (this.recondata.tags != null) {
      for (var counttag = 0; counttag < this.recondata.tags.length; counttag++) {
        tagArray[counttag] = this.recondata.tags[counttag];
      }
    }
    dqJson["tags"] = tagArray;
    dqJson["active"] = this.recondata.active == true ? 'Y' : "N"
    dqJson["published"] = this.recondata.published == true ? 'Y' : "N"
    dqJson["locked"] = this.recondata.published == true ? 'Y' : "N"
    dqJson["sourceDistinct"] = this.recondata.sourceDistinct == true ? 'Y' : "N"
    dqJson["targetDistinct"] = this.recondata.targetDistinct == true ? 'Y' : "N"
    
    var sourceattribute = {}
    var ref = {}
    //sourceattribute["attrName"]=this.selectSourceAtrribute["attrName"]
    sourceattribute["attrId"] = this.selectSourceAtrribute["attrId"]
    ref["name"] = this.selectSourceType["name"]
    ref["type"] = this.source
    ref["uuid"] = this.selectSourceType["uuid"]
    sourceattribute["ref"] = ref
    dqJson["sourceAttr"] = sourceattribute
    var targetattribute = {}
    var ref = {}
    //targetattribute["attrName"]=this.selectTargetAtrribute["attrName"]
    targetattribute["attrId"] = this.selectTargetAtrribute["attrId"]
    ref["name"] = this.selectTargetType["name"]
    ref["type"] = this.target
    ref["uuid"] = this.selectTargetType["uuid"]
    targetattribute["ref"] = ref
    dqJson["targetAttr"] = targetattribute
    var sourFunction = {}
    var ref = {}
    ref["name"] = this.selectSoueceFunction["name"]
    ref["type"] = 'function'
    ref["uuid"] = this.selectSoueceFunction["uuid"]
    sourFunction["ref"] = ref
    dqJson["sourceFunc"] = sourFunction
    var targFunction = {}
    var ref = {}
    ref["name"] = this.selectTargetFunction["name"]
    ref["type"] = 'function'
    ref["uuid"] = this.selectTargetFunction["uuid"]
    targFunction["ref"] = ref
    dqJson["targetFunc"] = targFunction


    let filterInfoArray = [];
    if (this.filterTableArray != null) {
      for (let i = 0; i < this.filterTableArray.length; i++) {
        let filterInfo = {};
        filterInfo["logicalOperator"] = this.filterTableArray[i].logicalOperator;
        filterInfo["operator"] = this.filterTableArray[i].operator;
        filterInfo["operand"] = [];
        if (this.filterTableArray[i].lhsType == 'integer' || this.filterTableArray[i].lhsType == 'string') {
          let operatorObj = {};
          let ref = {};
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.filterTableArray[i].lhsAttribute;
          operatorObj["attributeType"] = "string";
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
        else if (this.filterTableArray[i].lhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo["operand"][0] = operatorObj;
        }
        // else if (this.filterTableArray[i].lhsType == 'attribute' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = {};
        //   let ref = {}
        //   ref["type"] = "attribute";
        //   operatorObj["ref"] = ref;
        //   operatorObj["value"] = this.filterTableArray[i].lhsAttribute;
        //   filterInfo["operand"][0] = operatorObj;
        // }

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
        else if (this.filterTableArray[i].rhsType == 'dataset') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "dataset";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.filterTableArray[i].rhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        // else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = {};
        //   let ref = {}
        //   ref["type"] = "attribute";
        //   operatorObj["ref"] = ref;
        //   operatorObj["value"] = this.filterTableArray[i].rhsAttribute;
        //   filterInfo["operand"][1] = operatorObj;
        // }
        filterInfoArray[i] = filterInfo;
      }
      dqJson["sourceFilter"] = filterInfoArray;
    }
    else {
      dqJson["sourceFilter"] = null;
    }

    let targetFilterInfoArray = [];
    if (this.targetFilterTableArray != null) {
      for (let i = 0; i < this.targetFilterTableArray.length; i++) {
        let filterInfo = {};
        filterInfo["logicalOperator"] = this.targetFilterTableArray[i].logicalOperator;
        filterInfo["operator"] = this.targetFilterTableArray[i].operator;
        filterInfo["operand"] = [];
        if (this.targetFilterTableArray[i].lhsType == 'integer' || this.targetFilterTableArray[i].lhsType == 'string') {
          let operatorObj = {};
          let ref = {};
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.targetFilterTableArray[i].lhsAttribute;
          operatorObj["attributeType"] = "string";
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].lhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.targetFilterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].lhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.targetFilterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.targetFilterTableArray[i].lhsAttribute.attributeId;
          filterInfo["operand"][0] = operatorObj;
        }
        // else if (this.filterTableArray[i].lhsType == 'attribute' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = {};
        //   let ref = {}
        //   ref["type"] = "attribute";
        //   operatorObj["ref"] = ref;
        //   operatorObj["value"] = this.filterTableArray[i].lhsAttribute;
        //   filterInfo["operand"][0] = operatorObj;
        // }

        if (this.targetFilterTableArray[i].rhsType == 'integer' || this.targetFilterTableArray[i].rhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.targetFilterTableArray[i].rhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][1] = operatorObj;

          if (this.targetFilterTableArray[i].rhsType == 'integer' && this.targetFilterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.targetFilterTableArray[i].rhsAttribute1 + "and" + this.targetFilterTableArray[i].rhsAttribute2;
            filterInfo["operand"][1] = operatorObj;
          }
        }
        else if (this.targetFilterTableArray[i].rhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == 'function') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "function";
          ref["uuid"] = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == 'paramlist') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "paramlist";
          ref["uuid"] = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == 'dataset') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "dataset";
          ref["uuid"] = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.targetFilterTableArray[i].rhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.targetFilterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.targetFilterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        // else if (this.filterTableArray[i].rhsType == 'datapod' && this.selectedSourceType == 'FILE') {
        //   let operatorObj = {};
        //   let ref = {}
        //   ref["type"] = "attribute";
        //   operatorObj["ref"] = ref;
        //   operatorObj["value"] = this.filterTableArray[i].rhsAttribute;
        //   filterInfo["operand"][1] = operatorObj;
        // }
        targetFilterInfoArray[i] = filterInfo;
      }
      dqJson["targetFilter"] = targetFilterInfoArray;
    }
    else {
      dqJson["targetFilter"] = null;
    }

    console.log(JSON.stringify(dqJson))
    this._commonService.submit("recon", dqJson, 'N').subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("recon", response).subscribe(
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
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Recon Rule Save Successfully' });
      setTimeout(() => {
        this.goBack();

      }, 1000);
    }
  }
  onAttrRowDown(index){
		var rowTempIndex=this.filterTableArray[index];
    var rowTempIndexPlus=this.filterTableArray[index+1];
		this.filterTableArray[index]=rowTempIndexPlus;
    this.filterTableArray[index+1]=rowTempIndex;
    this.iSSubmitEnable=true

	}
	
	onAttrRowUp(index){
		var rowTempIndex=this.filterTableArray[index];
    var rowTempIndexMines=this.filterTableArray[index-1];
		this.filterTableArray[index]=rowTempIndexMines;
    this.filterTableArray[index-1]=rowTempIndex;
    this.iSSubmitEnable=true
  }
  dragStart(event,data){
    console.log(event)
    console.log(data)
    this.dragIndex=data
  }
  dragEnd(event){
    console.log(event)
  }
  drop(event,data){
    if(this.mode=='false'){
      this.dropIndex=data
      // console.log(event)
      // console.log(data)
      var item=this.filterTableArray[this.dragIndex]
      this.filterTableArray.splice(this.dragIndex,1)
      this.filterTableArray.splice(this.dropIndex,0,item)
      this.iSSubmitEnable=true
    }
    
  }
  dropTrgt(event,data){
    if(this.mode=='false'){
      this.dropIndex=data
      // console.log(event)
      // console.log(data)
      var item=this.targetFilterTableArray[this.dragIndex]
      this.targetFilterTableArray.splice(this.dragIndex,1)
      this.targetFilterTableArray.splice(this.dropIndex,0,item)
      this.iSSubmitEnable=true
    } 
    
  }
  onTrgtRowDown(index){
    var rowTempIndex=this.targetFilterTableArray[index];
    var rowTempIndexPlus=this.targetFilterTableArray[index+1];
    this.targetFilterTableArray[index]=rowTempIndexPlus;
    this.targetFilterTableArray[index+1]=rowTempIndex;
    this.iSSubmitEnable=true

  }
  
  onTrgtRowUp(index){
    var rowTempIndex=this.targetFilterTableArray[index];
    var rowTempIndexMines=this.targetFilterTableArray[index-1];
    this.targetFilterTableArray[index]=rowTempIndexMines;
    this.targetFilterTableArray[index-1]=rowTempIndex;
    this.iSSubmitEnable=true
  }
  showMainPage(){
    this.isHomeEnable = false
   // this._location.back();
   this.showGraph = false;
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}

