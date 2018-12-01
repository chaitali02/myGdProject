
import { Component, Input, OnInit } from '@angular/core';
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
@Component({
  selector: 'app-data-pipeli',
  templateUrl: './data-qualitydetail.template.html',
})

export class DataQualityDetailComponent {
  rhsTypeArray: { 'value': string; 'label': string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  rhsFormulaArray: any[];
  attributesArray: any[];
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
  lhsdatapodattributefilter: any[];
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
  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataQualityService: DataQualityService) {
    this.dqdata = {};
    this.selectRefIntegrity = {}
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
      { 'value': 'dataset', 'label': 'dataset' }
    ];  
    this.datatype = [
    { 'value': '', 'label': '' },
    { 'value': 'String', 'label': 'String' },
    { 'value': 'Int', 'label': 'Int' },
    { 'value': 'Float', 'label': 'Float' },
    { 'value': 'Double', 'label': 'Double' },
    { 'value': 'Date', 'label': 'Date' }
  ];
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
    this.dqdata["active"] = true
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
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
        this.getAllLatest()
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
    if (this.selectDataType == "Date") {
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
  OnSuccesgetAllAttributeBySource(response) {
    let temp = [];
    let attribute = []
    let count = 1
    let allname = {};
    allname["label"] = '-select-';
    allname["value"] = null
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['dname'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['dname'];
      allname["value"]["u_Id"] = response[n]['id'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["attrId"] = response[n]['attributeId'];
      temp[n] = allname
      attribute[n] = allname

      //count=count+1;
    }
    this.allAttribute = temp
    this.lhsdatapodattributefilter = attribute
    this.allAttribute.splice(0, 0, allname);
    // this.lhsdatapodattributefilter.splice(0,1);
  }
  getOneByUuidAndVersion(id, version) {
    this._dataQualityService.getOneByUuidAndVersion(id, version, 'dq')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.dqdata.name
    this.dqdata = response.dqdata;
    this.dataqualitycompare = response.dqdata;
    this.createdBy = response.dqdata.createdBy.ref.name
    this.dqdata.published = response.dqdata["published"] == 'Y' ? true : false
    this.dqdata.active = response.dqdata["active"] == 'Y' ? true : false
    var tags = [];
    if (response.dqdata.tags != null) {
      for (var i = 0; i < response.dqdata.tags.length; i++) {
        var tag = {};
        tag['value'] = response.dqdata.tags[i];
        tag['display'] = response.dqdata.tags[i];
        tags[i] = tag
      }//End For
      this.tags = tags;
    }//En

    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response.dqdata['version'];
    version.uuid = response.dqdata['uuid'];
    this.selectedVersion = version
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response.dqdata["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response.dqdata["dependsOn"]["ref"]["uuid"];
    this.sourcedata = dependOnTemp;
    if (response.dqdata["attribute"] != null) {
      this.IsSelectSoureceAttr = true
      let selectattribute: AttributeHolder = new AttributeHolder();
      selectattribute.label = response.dqdata["attribute"]["ref"]["name"] + "." + response.dqdata["attribute"]["attrName"];
      selectattribute.u_Id = response.dqdata["attribute"]["ref"]["uuid"] + "_" + response.dqdata["attribute"]["attrId"]
      selectattribute.uuid = response.dqdata["attribute"]["ref"]["uuid"];
      selectattribute.attrId = response.dqdata["attribute"]["attrId"];
      this.selectAttribute = selectattribute;
    }
    console.log(response.dqdata.filterInfo)
    if (response.dqdata.filterInfo != null) {
      let filterInfoArray = [];
      for (let k = 0; k < response.dqdata.filterInfo.length; k++) {
        let filterInfo = {};
        let lhsFilter = {};
        filterInfo["logicalOperator"] = response.dqdata.filterInfo[k].logicalOperator
        filterInfo["lhsType"] = response.dqdata.filterInfo[k].operand[0].ref.type;
        filterInfo["operator"] = response.dqdata.filterInfo[k].operator;
        filterInfo["rhsType"] = response.dqdata.filterInfo[k].operand[1].ref.type;

        if (response.dqdata.filterInfo[k].operand[0].ref.type == 'formula') {
          this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
            error => console.log("Error ::", error))

          let lhsAttri1 = {}
          lhsAttri1["uuid"] = response.dqdata.filterInfo[k].operand[0].ref.uuid;
          lhsAttri1["label"] = response.dqdata.filterInfo[k].operand[0].ref.name;
          filterInfo["lhsAttribute"] = lhsAttri1;
        }

        else if (response.dqdata.filterInfo[k].operand[0].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
            error => console.log("Error ::", error))
          let lhsAttri = {}
          lhsAttri["uuid"] = response.dqdata.filterInfo[k].operand[0].ref.uuid;
          lhsAttri["label"] = response.dqdata.filterInfo[k].operand[0].ref.name + "." + response.dqdata.filterInfo[k].operand[0].attributeName;
          lhsAttri["attributeId"] = response.dqdata.filterInfo[k].operand[0].attributeId;
          filterInfo["lhsAttribute"] = lhsAttri;
        }

        else if (response.dqdata.filterInfo[k].operand[0].ref.type == 'simple') {
          let stringValue = response.dqdata.filterInfo[k].operand[0].value;
          let onlyNumbers = /^[0-9]+$/;
          let result = onlyNumbers.test(stringValue);
          if (result == true) {
            filterInfo["lhsType"] = 'integer';
          } else {
            filterInfo["lhsType"] = 'string';
          }
          filterInfo["lhsAttribute"] = response.dqdata.filterInfo[k].operand[0].value;
        }

        if (response.dqdata.filterInfo[k].operand[1].ref.type == 'formula') {
          this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
            error => console.log("Error ::", error))

          filterInfo["rhsAttribute"] = response.dqdata.filterInfo[k].operand[1].ref.name;
          let rhsAttri = {}
          rhsAttri["uuid"] = response.dqdata.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.dqdata.filterInfo[k].operand[1].ref.name;
          filterInfo["rhsAttribute"] = rhsAttri;
        }

        else if (response.dqdata.filterInfo[k].operand[1].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
            .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
            error => console.log("Error ::", error))

          let rhsAttri1 = {}
          rhsAttri1["uuid"] = response.dqdata.filterInfo[k].operand[1].ref.uuid;
          rhsAttri1["label"] = response.dqdata.filterInfo[k].operand[1].ref.name + "." + response.dqdata.filterInfo[k].operand[1].attributeName;
          rhsAttri1["attributeId"] = response.dqdata.filterInfo[k].operand[1].attributeId;
          filterInfo["rhsAttribute"] = rhsAttri1;
        }

        else if (response.dqdata.filterInfo[k].operand[1].ref.type == 'simple') {
          let stringValue = response.dqdata.filterInfo[k].operand[1].value;
          let onlyNumbers = /^[0-9]+$/;
          let result = onlyNumbers.test(stringValue);
          if (result == true) {
            filterInfo["rhsType"] = 'integer';
          } else {
            filterInfo["rhsType"] = 'string';
          }
          filterInfo["rhsAttribute"] = response.dqdata.filterInfo[k].operand[1].value;

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
        this.dqdata.filterTableArray = filterInfoArray
      }
    }

    this.dqdata.selectDataType = response["dqdata"]["dataTypeCheck"];

    let valueCheck = [];
    if (response.dqdata.valueCheck != null) {
      for (var i = 0; i < response.dqdata.valueCheck.length; i++) {
        var valueCheck1 = {};
        valueCheck1['value'] = response.dqdata.valueCheck[i];
        valueCheck1['display'] = response.dqdata.valueCheck[i];
        valueCheck[i] = valueCheck1
      }//End For
      this.valueCheck = valueCheck;
    }//En
   
    this.dqdata.duplicateKeyCheck = response.dqdata["duplicateKeyCheck"] == "Y" ? true : false;
    this.dqdata.nullCheck = response.dqdata["nullCheck"] == "Y" ? true : false;
    this.dqdata.upperBound = response.dqdata.rangeCheck.upperBound;
    this.dqdata.lowerBound = response.dqdata.rangeCheck.lowerBound;
    this.dqdata.maxLength = response.dqdata.lengthCheck.maxLength;
    this.dqdata.minLength = response.dqdata.lengthCheck.minLength;
    if (response.dqdata.refIntegrityCheck.ref != null) {
      let selectrefIntegrity: DependsOn = new DependsOn();
      selectrefIntegrity.label = response.dqdata.refIntegrityCheck.ref.name;
      selectrefIntegrity.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
      this.selectRefIntegrity = selectrefIntegrity

      let selectintegrityattribute: AttributeHolder = new AttributeHolder();
      selectintegrityattribute.label = response.dqdata.refIntegrityCheck.ref.name;
      selectintegrityattribute.u_Id = response.dqdata.refIntegrityCheck.ref.uuid + "_" + response.dqdata.refIntegrityCheck.attrId;
      selectintegrityattribute.uuid = response.dqdata.refIntegrityCheck.ref.uuid
      selectintegrityattribute.attrId = response.dqdata.refIntegrityCheck.attrId
      this.selectIntegrityAttribute = selectintegrityattribute;
    }
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
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
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
    this.dqdata.filterTableArray[index]["lhsAttribute"] = null;
    
    if (this.dqdata.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dqdata.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dqdata.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsType(index) {
    this.dqdata.filterTableArray[index]["rhsAttribute"] = null;
   
    if (this.dqdata.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dqdata.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dqdata.filterTableArray[index]["rhsAttribute"] = null;
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
    this.dqdata.filterTableArray[index]["rhsType"] = null;
  }

  addRow() {
    if (this.dqdata.filterTableArray == null) {
      this.dqdata.filterTableArray = [];
    }
    var len = this.dqdata.filterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = ""
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = ""
    filertable["rhsAttribute"] = ""
    this.dqdata.filterTableArray.splice(this.dqdata.filterTableArray.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.dqdata.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.dqdata.filterTableArray = newDataList;
  }
  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.dqdata.filterTableArray.forEach(filter => {
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

    dqJson["active"] = this.dqdata.active == true ? 'Y' : "N"
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
}
