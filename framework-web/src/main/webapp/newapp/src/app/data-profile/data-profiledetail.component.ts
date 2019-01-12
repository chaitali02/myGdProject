
import { Component, Input, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
import { DependsOn } from './dependsOn'
import { AttributeHolder } from './../metadata/domain/domain.attributeHolder'
@Component({
  selector: 'app-profile',
  templateUrl: './data-profiledetail.template.html',
  styleUrls: []
})

export class DataProfileDetailComponent {
  filterTableArray: any;
  isNullArray: { 'value': string; 'label': string; }[];
  datatype: { 'value': string; 'label': string; }[];
  rhsTypeArray: { 'value': string; 'label': string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  logicalOperators: { 'value': string; 'label': string; }[];
  operators: { 'value': string; 'label': string; }[];
  dialogAttributeName: any;
  displayDialogBox: boolean;
  dialogSelectName: any;
  dialogAttriArray: any[];
  dialogAttriNameArray: any[];
  selectAttribute: any;
  IsSelectSoureceAttr: boolean;
  rhsFormulaArray: any[];
  paramlistArray: any[];
  functionArray: any[];
  attributesArray: any[];
  lhsFormulaArray: any[];
  selectedAllFitlerRow: boolean;
  progressbarWidth: string;
  continueCount: number;
  showGraph: boolean;
  isHomeEnable: boolean;
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  IsProgerssShow: string;
  isSubmitEnable: any;
  msgs: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number, disabled: any };
  selectedItems: any
  dropdownList: any;
  source: any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  allNames: SelectItem[] = [];
  createdBy: any;
  mode: any;
  version: any;
  uuid: any;
  id: any;
  routerUrl: any;
  dataprofile: any
  sources: any;
  sourcedata: DependsOn
  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, private _commonService: CommonService, private _location: Location) {
    this.dataprofile = {};
    this.dataprofile["active"] = true
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.sources = ["datapod"];
    this.source = this.sources[0];
    this.selectedItems = [];
    this.continueCount = 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };
    this.breadcrumbDataFrom = [{
      "caption": "Data Profiling",
      "routeurl": "/app/list/profile"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/profile"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    
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
    this.filterTableArray = null;
  }


  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
        this.getAllLatest();
        this.dropdownSettings.disabled = this.mode == "false" ? false : true
      }
      else {
        this.getAllLatest();
      }
    });
  }
  getAllLatest() {
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response1) {
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
    
    if (this.mode != 'true') {
      this.getAllAttributeBySource();
    }
  }
  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
  }
  changeType() {
    this.selectedItems = [];
    this.getAllAttributeBySource();
  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["id"] = response[n]['id'];
      allname["itemName"] = response[n]['dname'];
      allname["uuid"] = response[n]['uuid'];
      allname["attributeId"] = response[n]['attributeId'];
      temp[n] = allname
    }
    this.dropdownList = temp
    //this.allMapSourceAttribute = temp
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'profile')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.dataprofile = response;
    this.uuid = response.uuid
    this.createdBy = response.createdBy.ref.name
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.dataprofile.tags = tags;
    }//End If

    this.dataprofile.published = response["published"] == 'Y' ? true : false
    this.dataprofile.active = response["active"] == 'Y' ? true : false
    this.dataprofile.locked = response["locked"] == 'Y' ? true : false
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.sourcedata = dependOnTemp
    this.getAllAttributeBySource();
    let tmp = [];
    for (let i = 0; i < response.attributeInfo.length; i++) {
      let attributeinfo = {};
      attributeinfo["id"] = response.attributeInfo[i]["ref"]["uuid"] + "_" + response.attributeInfo[i].attrId;
      attributeinfo["itemName"] = response.attributeInfo[i]["ref"]["name"] + "." + response.attributeInfo[i].attrName;
      attributeinfo["uuid"] = response.attributeInfo[i]["ref"]["uuid"]
      attributeinfo["attributeId"] = response.attributeInfo[i].attrId;
      tmp[i] = attributeinfo;
    }
    this.selectedItems = tmp;
    if (response["attribute"] != null) {
      this.IsSelectSoureceAttr = true
      let selectattribute: AttributeHolder = new AttributeHolder();
      selectattribute.label = response["attribute"]["ref"]["name"] + "." + response["attribute"]["attrName"];
      selectattribute.u_Id = response["attribute"]["ref"]["uuid"] + "_" + response["attribute"]["attrId"]
      selectattribute.uuid = response["attribute"]["ref"]["uuid"];
      selectattribute.attrId = response["attribute"]["attrId"];
      this.selectAttribute = selectattribute;
    }
    console.log(response.filterInfo)
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
          this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
            error => console.log("Error ::", error))

          let lhsAttri1 = {}
          lhsAttri1["uuid"] = response.filterInfo[k].operand[0].ref.uuid;
          lhsAttri1["label"] = response.filterInfo[k].operand[0].ref.name;
          filterInfo["lhsAttribute"] = lhsAttri1;
        }

        else if (response.filterInfo[k].operand[0].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
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
          this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
            error => console.log("Error ::", error))

          filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].ref.name;
          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.filterInfo[k].operand[1].ref.name;
          filterInfo["rhsAttribute"] = rhsAttri;
        }

        else if (response.filterInfo[k].operand[1].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
            error => console.log("Error ::", error))

          let rhsAttri1 = {}
          rhsAttri1["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri1["label"] = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
          rhsAttri1["attributeId"] = response.filterInfo[k].operand[1].attributeId;
          filterInfo["rhsAttribute"] = rhsAttri1;
        }

        else if (response.filterInfo[k].operand[1].ref.type == 'paramlist') {
          this._commonService.getParamByApp("", "application")
            .subscribe(response => { this.onSuccessgetParamByApp(response) },
            error => console.log("Error ::", error))

          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["attributeId"] = response.filterInfo[k].operand[1].attributeId;
          rhsAttri["label"] = "app." + response.filterInfo[k].operand[1].attributeName;
          filterInfo["rhsAttribute"] = rhsAttri;
        }
        else if (response.filterInfo[k].operand[1].ref.type == 'function') {
          this._commonService.getFunctionByCriteria("", "N", "function")
            .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
            error => console.log("Error ::", error))
          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.filterInfo[k].operand[1].ref.name;
          filterInfo["rhsAttribute"] = rhsAttri;
        }
        else if (response.filterInfo[k].operand[1].ref.type == 'dataset') {
          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["attributeId"] = response.filterInfo[k].operand[1].attributeId;
          rhsAttri["label"] = response.filterInfo[k].operand[1].attributeName;
          filterInfo["rhsAttribute"] = rhsAttri;
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
        this.dataprofile.filterTableArray = filterInfoArray
      }
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
    this.dataprofile.filterTableArray[index].rhsAttribute = rhsattribute;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('profile', this.id)
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

    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'profile')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/profile']);
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

  onSuccessgetAllAttributeBySource(response) {
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
    this.dataprofile.filterTableArray[index]["lhsAttribute"] = null;

    if (this.dataprofile.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dataprofile.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dataprofile.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsType(index) {
    this.dataprofile.filterTableArray[index]["rhsAttribute"] = null;

    if (this.dataprofile.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))
    }
    else if (this.dataprofile.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySource(response) },
        error => console.log("Error ::", error))
    }
    else if (this.dataprofile.filterTableArray[index]["rhsType"] == 'function') {
      this._commonService.getFunctionByCriteria("", "N", "function")
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
    }
    else if (this.dataprofile.filterTableArray[index]["rhsType"] == 'paramlist') {
      this._commonService.getParamByApp("", "application")
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
    }
    else if (this.dataprofile.filterTableArray[index]["rhsType"] == 'dataset') {
      let rhsAttribute = {};
      rhsAttribute["label"] = "-Select-";
      rhsAttribute["uuid"] = "";
      rhsAttribute["attributeId"] = "";
      this.dataprofile.filterTableArray[index]["rhsAttribute"] = rhsAttribute
    }
    else {
      this.dataprofile.filterTableArray[index]["rhsAttribute"] = null;
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

  // onSuccessgetAllAttributeBySource(response) {
  //   this.attributesArray = []
  //   let temp1 = [];
  //   for (const i in response) {
  //     let attributeObj = {};
  //     attributeObj["label"] = response[i].dname;
  //     attributeObj["value"] = {};
  //     attributeObj["value"]["uuid"] = response[i].uuid;
  //     attributeObj["value"]["label"] = response[i].dname;
  //     attributeObj["value"]["attributeId"] = response[i].attributeId;
  //     temp1[i] = attributeObj
  //     this.attributesArray = temp1;
  //   }
  // }

  onChangeOperator(index) {
    this.dataprofile.filterTableArray[index].rhsAttribute = null;
    if (this.dataprofile.filterTableArray[index].operator == 'EXISTS' || this.dataprofile.filterTableArray[index].operator == 'NOT EXISTS') {
      this.dataprofile.filterTableArray[index].rhsType = 'dataset';
      let rhsAttribute = {};
      rhsAttribute["label"] = "-Select-";
      rhsAttribute["uuid"] = "";
      rhsAttribute["attributeId"] = "";
      this.dataprofile.filterTableArray[index]["rhsAttribute"] = rhsAttribute
    }
    else if(this.dataprofile.filterTableArray[index].operator == 'IS'){
			this.dataprofile.filterTableArray[index].rhsType = 'string';
    }
    else{
			this.dataprofile.filterTableArray[index].rhsType = 'integer';
		}
  }
  addRow() {
    if (this.dataprofile.filterTableArray == null) {
      this.dataprofile.filterTableArray = [];
    }
    var len = this.dataprofile.filterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = "integer"
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = "integer"
    filertable["rhsAttribute"] = ""
    this.dataprofile.filterTableArray.splice(this.dataprofile.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.dataprofile.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.dataprofile.filterTableArray = newDataList;
  }
  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.dataprofile.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }
  submit() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let profileJson = {}
    profileJson["uuid"] = this.dataprofile.uuid
    profileJson["name"] = this.dataprofile.name
    profileJson["desc"] = this.dataprofile.desc
    var tagArray = [];
    if (this.dataprofile.tags != null) {
      for (var counttag = 0; counttag < this.dataprofile.tags.length; counttag++) {
        tagArray[counttag] = this.dataprofile.tags[counttag].value;

      }
    }
    profileJson['tags'] = tagArray;
    let dependsOn = {};
    let ref = {}
    ref["type"] = this.source
    ref["uuid"] = this.sourcedata.uuid
    dependsOn["ref"] = ref;
    profileJson["dependsOn"] = dependsOn;
    profileJson["active"] = this.dataprofile.active == true ? 'Y' : "N"
    profileJson["published"] = this.dataprofile.published == true ? 'Y' : "N"
    profileJson["locked"] = this.dataprofile.locked == true ? 'Y' : "N"
    let attributeInfo = [];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let attributes = {}
      let ref = {};
      ref["uuid"] = this.selectedItems[i]["uuid"];
      ref["type"] = "datapod";
      attributes["ref"] = ref;
      attributes["attrId"] = this.selectedItems[i]["attributeId"];
      attributeInfo[i] = attributes;
    }
    profileJson["attributeInfo"] = attributeInfo;
    let filterInfoArray = [];
    if (this.dataprofile.filterTableArray.length > 0) {
      for (let i = 0; i < this.dataprofile.filterTableArray.length; i++) {

        let filterInfo = {};
        filterInfo["logicalOperator"] = this.dataprofile.filterTableArray[i].logicalOperator;
        filterInfo["operator"] = this.dataprofile.filterTableArray[i].operator;
        filterInfo["operand"] = [];

        if (this.dataprofile.filterTableArray[i].lhsType == 'integer' || this.dataprofile.filterTableArray[i].lhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.dataprofile.filterTableArray[i].lhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].lhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.dataprofile.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
          filterInfo["operand"][0] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].lhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.dataprofile.filterTableArray[i].lhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataprofile.filterTableArray[i].lhsAttribute.attributeId;
          filterInfo["operand"][0] = operatorObj;
        }
        if (this.dataprofile.filterTableArray[i].rhsType == 'integer' || this.dataprofile.filterTableArray[i].rhsType == 'string') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "simple";
          operatorObj["ref"] = ref;
          operatorObj["value"] = this.dataprofile.filterTableArray[i].rhsAttribute;
          operatorObj["attributeType"] = "string"
          filterInfo["operand"][1] = operatorObj;

          if (this.dataprofile.filterTableArray[i].rhsType == 'integer' && this.dataprofile.filterTableArray[i].operator == 'BETWEEN') {
            let operatorObj = {};
            let ref = {}
            ref["type"] = "simple";
            operatorObj["ref"] = ref;
            operatorObj["value"] = this.dataprofile.filterTableArray[i].rhsAttribute1 + "and" + this.dataprofile.filterTableArray[i].rhsAttribute2;
            filterInfo["operand"][1] = operatorObj;
          }
        }
        else if (this.dataprofile.filterTableArray[i].rhsType == 'formula') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "formula";
          ref["uuid"] = this.dataprofile.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].rhsType == 'function') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "function";
          ref["uuid"] = this.dataprofile.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].rhsType == 'paramlist') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "paramlist";
          ref["uuid"] = this.dataprofile.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataprofile.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].rhsType == 'dataset') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "dataset";
          ref["uuid"] = this.dataprofile.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataprofile.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        else if (this.dataprofile.filterTableArray[i].rhsType == 'datapod') {
          let operatorObj = {};
          let ref = {}
          ref["type"] = "datapod";
          ref["uuid"] = this.dataprofile.filterTableArray[i].rhsAttribute.uuid;
          operatorObj["ref"] = ref;
          operatorObj["attributeId"] = this.dataprofile.filterTableArray[i].rhsAttribute.attributeId;
          filterInfo["operand"][1] = operatorObj;
        }
        filterInfoArray[i] = filterInfo;
      }
      profileJson["filterInfo"] = filterInfoArray;
      console.log(JSON.stringify(filterInfoArray));
    }
    console.log(profileJson);
    this._commonService.submit("profile", profileJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("profile", response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Profile Save Successfully' });
      setTimeout(() => {
        this.goBack();
      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "profile", "execute").subscribe(
      response => {
        this.showMassage('Profile Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()

        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }

  showMassage(msg, msgtype, msgsumary) {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataProfiling/profile', uuid, version, 'false']);
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };


  }
  showview(uuid, version) {
    this.router.navigate(['app/dataProfiling/profile', uuid, version, 'true']);
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: true
    };

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
  clear(){
    this.selectedItems=[]
  }
  addAll() {
		this.selectedItems = this.dropdownList;
	}
}
