import { NgModule, Component, ViewEncapsulation, Input } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { AppConfig } from '../../app.config';
import { GridOptions } from 'ag-grid/main';
import { Message } from 'primeng/components/common/api';
import { MessageService } from 'primeng/components/common/messageservice';
import { CommonService } from '../../metadata/services/common.service'
import { Location } from '@angular/common';
import { Version } from '../../shared/version'
import { SelectItem } from 'primeng/primeng';
import { DependsOn } from './dependsOn'
import { debug } from 'util';
@Component({
  selector: 'app-filter',
  styleUrls: [],
  templateUrl: './filter.template.html',
})
export class FilterComponent {
  rhsFormulaArray: any[];
  varBoolean: boolean;
  rhsTypeArray: { 'value': string; 'label': string; 'disabled1': boolean; }[];
  lhsAttribute: any;

  attributesArray: any[];
  formulaArray: any[];
  rhsType: any;
  lhsFormulaArray: any[];
  lhsType: any;
  //rhsTypeArray: any[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  selectVersion: any;
  msgs: Message[] = [];
  dependsOnType: { 'value': string; 'label': string; }[];
  selectedAllFitlerRow: boolean;
  filterTableArray: any;
  lhsdatapodattributefilter: any;
  filterDatapod: any;
  rhsdata: any;
  operators: any;
  lhsdata: any;
  logicalOperators: any;
  logicalOperator: any;
  tableData: any;
  filterData: any;
  allName: any;
  dependsname: any;
  dependsUuid: any;
  allNames: SelectItem[] = [];
  dependsOn: DependsOn
  depends: any;
  selectRelation: any;
  versions: any[];
  showFilter: boolean;
  active: any;
  published: any;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  breadcrumbDataFrom: any;
  isSubmitEnable: any;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
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
    this.varBoolean = false;
    this.selectVersion = { "version": "" };
    this.dependsOn = { 'uuid': "", "label": "" }
    this.filterData = {};
    this.active = true;
    this.isSubmitEnable = true;
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]

    this.showFilter = true;
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/filter"
    },
    {
      "caption": "Filter",
      "routeurl": "/app/list/filter"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dependsOnType = [
      { 'value': 'relation', 'label': 'relation' },
      { 'value': 'dataset', 'label': 'dataset' },
      { 'value': 'datapod', 'label': 'datapod' }
    ];
    this.lhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' }
    ];
    this.rhsTypeArray = [
      { 'value': 'string', 'label': 'string', 'disabled1': false },
      { 'value': 'integer', 'label': 'integer', 'disabled1': true },
      { 'value': 'datapod', 'label': 'attribute', 'disabled1': true },
      { 'value': 'formula', 'label': 'formula', 'disabled1': false },
      { 'value': 'dataset', 'label': 'dataset', 'disabled1': false }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    });
    if (this.mode !== undefined) {
      this.getOneByUuidAndVersion(this.id, this.version);
      this.getAllVersionByUuid();
    }
  }
  public goBack() {
    //  this._location.back();
    this.router.navigate(['app/list/filter',]);

  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'filter')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid("filter", this.id).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.filterData = response
    const version: Version = new Version();
    this.uuid = response.uuid
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = this.filterData.createdBy.ref.name
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.tags = tags;
    }//End If

    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.depends = response["dependsOn"]["ref"]["type"];

    // this.dependsUuid = response["dependsOn"]["ref"]["uuid"]
    // this.dependsname = response["dependsOn"]["ref"]["name"]

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.dependsOn = dependOnTemp
    let filterjson = {};
    filterjson["filter"] = response;
    let filterInfoArray = [];
    if (response.filterInfo.length > 0) {
      for (let k = 0; k < response.filterInfo.length; k++) {
        let filterInfo = {};
        let lhsFilter = {};
        filterInfo["logicalOperator"] = response.filterInfo[k].logicalOperator
        filterInfo["lhsType"] = response.filterInfo[k].operand[0].ref.type;
        filterInfo["operator"] = response.filterInfo[k].operator;
        filterInfo["rhsType"] = response.filterInfo[k].operand[1].ref.type;

        if (response.filterInfo[k].operand[0].ref.type == 'formula') {
          this._commonService.getFormulaByType(this.dependsOn.uuid, this.depends)
            .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
            error => console.log("Error ::", error))

          let lhsAttri1 = {}
          lhsAttri1["uuid"] = response.filterInfo[k].operand[0].ref.uuid;
          lhsAttri1["label"] = response.filterInfo[k].operand[0].ref.name;
          filterInfo["lhsAttribute"] = lhsAttri1;
        }

        else if (response.filterInfo[k].operand[0].ref.type == 'datapod') {

          this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends)
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
          this._commonService.getFormulaByType(this.dependsOn.uuid, this.depends)
            .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
            error => console.log("Error ::", error))
          //filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].ref.name;
          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.filterInfo[k].operand[1].ref.name;
          filterInfo["rhsAttribute"] = rhsAttri;
        }

        else if (response.filterInfo[k].operand[1].ref.type == 'datapod') {
          this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends)
            .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
            error => console.log("Error ::", error))

          let rhsAttri = {}
          rhsAttri["uuid"] = response.filterInfo[k].operand[1].ref.uuid;
          rhsAttri["label"] = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
          rhsAttri["attributeId"] = response.filterInfo[k].operand[0].attributeId;
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
      }
    }
    filterjson["filterInfo"] = filterInfoArray;
    this.filterTableArray = filterInfoArray;
    console.log(JSON.stringify(this.filterTableArray));
    this._commonService.getAllLatest(this.depends).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
    this.breadcrumbDataFrom[2].caption = this.filterData.name;
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

  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllAttributeBySource(response) {
    //console.log(response)
    this.lhsdatapodattributefilter = response
  }

  OnSuccesgetAllLatest(response1) {
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
  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      //allName["uuid"]=response[i]['uuid']
      temp[i] = ver;
    }
    this.VersionList = temp
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  addRow() {
    if (this.filterTableArray == null) {
      this.filterTableArray = [];
    }
    //alert(this.filterTableArray.length)
    let filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = ""
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = ""
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

  selectType() {
    this._commonService.getAllLatest(this.depends).subscribe(
      response => {
        this.OnSuccesgetAllLatest(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  changeType() {
    this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onChangeActive(event) {
    if (event === true) {
      this.filterData.active = 'Y';
    }
    else {
      this.filterData.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.filterData.published = 'Y';
    }
    else {
      this.filterData.published = 'N';
    }
  }

  onChangeLhsType(index) {
    if (this.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.dependsOn.uuid, this.depends)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsType(index) {
    if (this.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.dependsOn.uuid, this.depends)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
        error => console.log("Error ::", error))
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

  submitFilter() {
    this.isSubmitEnable = true;
    let filterJson = {};
    filterJson["uuid"] = this.filterData.uuid
    filterJson["name"] = this.filterData.name
    var tagArray = [];

    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;
      }
    }
    filterJson['tags'] = tagArray;
    //filterJson["tags"]=tagstemp
    filterJson["desc"] = this.filterData.desc
    let dependsOn = {};
    let ref = {}
    ref["type"] = this.depends
    ref["uuid"] = this.dependsOn.uuid
    dependsOn["ref"] = ref;
    filterJson["dependsOn"] = dependsOn;
    filterJson["active"] = this.filterData.active
    filterJson["published"] = this.filterData.published

    let filterInfoArray = [];
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
        filterInfo["operand"][0] = operatorObj;
      }
      else if (this.filterTableArray[i].lhsType == 'formula') {
        let operatorObj = {};
        let ref = {}
        ref["type"] = "formula";
        ref["uuid"] = this.filterTableArray[i].lhsAttribute.uuid;
        operatorObj["ref"] = ref;
        // operatorObj["attributeId"] = this.filterTableArray[i].lhsAttribute;
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
      if (this.filterTableArray[i].rhsType == 'integer' || this.filterTableArray[i].rhsType == 'string') {
        let operatorObj = {};
        let ref = {}
        ref["type"] = "simple";
        operatorObj["ref"] = ref;
        operatorObj["value"] = this.filterTableArray[i].rhsAttribute;
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
      filterInfoArray[i] = filterInfo;
    }

    filterJson["filterInfo"] = filterInfoArray

    console.log(JSON.stringify(filterInfoArray))
    this._commonService.submit("filter", filterJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Filter Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataPreparation/filter', uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/filter', uuid, version, 'true']);
  }
}

