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
    this.operators = ["=", "<", ">"];
    this.selectVersion = { "version": "" };
    this.dependsOn = { 'uuid': "", "label": "" }
    this.filterData = {};
    this.active = true;
    this.isSubmitEnable = true;
    this.logicalOperators = [" ", "AND", "OR"]
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
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.dependsOn = dependOnTemp

    this.dependsUuid = response["dependsOn"]["ref"]["uuid"]
    this.dependsname = response["dependsOn"]["ref"]["name"]
    let filterjson = {};
    filterjson["filter"] = response;
    let filterInfoArray = [];
    if (response.filterInfo.length > 0) {
      for (let k = 0; k < response.filterInfo.length; k++) {
        let filterInfo = {};
        let lhsFilter = {};
        lhsFilter["uuid"] = response.filterInfo[k].operand[0].ref.uuid
        lhsFilter["datapodname"] = response.filterInfo[k].operand[0].ref.name
        lhsFilter["attributeId"] = response.filterInfo[k].operand[0].attributeId;
        lhsFilter["name"] = response.filterInfo[k].operand[0].attributeName;
        lhsFilter["dname"] = lhsFilter["datapodname"] + "." + lhsFilter["name"];
        lhsFilter["id"] = lhsFilter["uuid"] + "_" + lhsFilter["attributeId"];
        filterInfo["logicalOperator"] = response.filterInfo[k].logicalOperator
        filterInfo["lhsFilter"] = lhsFilter;
        filterInfo["operator"] = response.filterInfo[k].operator;
        filterInfo["filtervalue"] = response.filterInfo[k].operand[1].value;
        filterInfoArray.push(filterInfo);
      }
    }
    filterjson["filterInfo"] = filterInfoArray
    this.filterTableArray = filterInfoArray
    this._commonService.getAllLatest(this.depends).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )

    this.breadcrumbDataFrom[2].caption = this.filterData.name;

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
    filertable["lhsFilter"] = this.lhsdatapodattributefilter[0]
    filertable["operator"] = this.operators[0]
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
      response => { //this.dependsOn.uuid=response[0]["uuid"]
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
      let operand = [];
      let operandfirst = {};
      let reffirst = {};
      let operandsecond = {};
      let refsecond = {};
      if (this.selectRelation == "dataset") {

        reffirst["type"] = "dataset";
      }
      else {
        reffirst["type"] = "datapod"
      }
      reffirst["uuid"] = this.filterTableArray[i].lhsFilter.uuid
      operandfirst["ref"] = reffirst;
      operandfirst["attributeId"] = this.filterTableArray[i].lhsFilter.attributeId
      operand[0] = operandfirst;
      refsecond["type"] = "simple";
      operandsecond["ref"] = refsecond;
      if (typeof this.filterTableArray[i].filtervalue == "undefined") {
        operandsecond["value"] = "";
      }
      else {

        operandsecond["value"] = this.filterTableArray[i].filtervalue
      }

      operand[1] = operandsecond;
      if (typeof this.filterTableArray[i].logicalOperator == "undefined") {
        filterInfo["logicalOperator"] = ""
      }
      else {
        filterInfo["logicalOperator"] = this.filterTableArray[i].logicalOperator
      }
      filterInfo["operator"] = this.filterTableArray[i].operator
      filterInfo["operand"] = operand;

      filterInfoArray[i] = filterInfo;

    }
    filterJson["filterInfo"] = filterInfoArray;
    console.log(JSON.stringify(filterJson))
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

