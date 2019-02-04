import { Location } from '@angular/common';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';

import { SelectItem } from 'primeng/primeng';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Version } from '../../shared/version';
import { CommonService } from '../../metadata/services/common.service';
import { DependsOn } from '../dependsOn';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component'
@Component({
  selector: 'app-paramset',
  templateUrl: './paramset.component.html',
  styleUrls: ['./paramset.component.css']
})
export class ParamsetComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  breadcrumbDataFrom: any;
  showParamset: any;
  versions: any[];
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
  selectedVersion: Version;
  paramset: any;
  paramArrayTable: any;
  arrayParamList: any;
  dependsOn: any;
  paramSetVal: any;
  paramTable: any;
  selectAllAttributeRow: any;
  tabledata: DependsOn;
  msgs: any;
  paramTableCol2: any;
  isSubmitEnable: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showParamset = true;
    this.paramset = {};
    this.isHomeEnable = false
    this.showGraph = false;
    this.paramSetVal = [];
    this.paramset["active"] = true;
    this.isSubmitEnable = true;
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/paramset"
    },
    {
      "caption": "Parameter set",
      "routeurl": "/app/list/paramset"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
  }
  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    });
    if (this.mode !== undefined) {
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
    }
    this.getAllLatest();
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'paramset')
      .subscribe(
      response => {//..console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.paramset = response;
    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = this.paramset.createdBy.ref.name
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
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
    this.paramset.published = response["published"] == 'Y' ? true : false
    this.paramset.active = response["active"] == 'Y' ? true : false

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.dependsOn = dependOnTemp;

    this.paramTableCol2 = response.paramInfo[0].paramSetVal;
    this.paramTable = response.paramInfo;

    console.log(JSON.stringify(this.paramTable))
  }

  onChangeActive(event) {
    if (event === true) {
      this.paramset.active = 'Y';
    }
    else {
      this.paramset.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.paramset.published = 'Y';
    }
    else {
      this.paramset.published = 'N';
    }
  }

  addAttribute() {
    if (this.paramTable == null) {
      this.paramTable = [];
    }
    let paramjson = {};
    paramjson["paramSetId"] = this.paramTable.length;
    let paramSetVal = [];
    for (let j = 0; j < this.paramTableCol2.length; j++) {
      let paramSetValjson = {};
      paramSetValjson["paramId"] = this.paramTableCol2[j].paramId;
      paramSetValjson["paramName"] = this.paramTableCol2[j].paramName;
      paramSetValjson["value"] = "";
      paramSetVal[j] = paramSetValjson;
    }
    paramjson["paramSetVal"] = paramSetVal;
    this.paramTable.splice(this.paramTable.length, 0, paramjson);
  }

  removeAttribute() {
    var newDataList = [];
    this.selectAllAttributeRow = false
    this.paramTable.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.paramTable = newDataList;
    console.log(JSON.stringify(this.paramTable))
  }

  checkAllAttributeRow() {
    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    console.log(this.selectAllAttributeRow);
    this.paramTable.forEach(paramjson => {
      paramjson.selected = this.selectAllAttributeRow;
      console.log(JSON.stringify(paramjson))
    });
  }

  onParamlistChange() {
    this._commonService.getLatestByUuid(this.dependsOn.uuid, 'paramlist')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetLatestByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetLatestByUuid(response) {
    let paramArray = [];
    for (let i = 0; i < response.params.length; i++) {
      let paramObj2 = {};
      paramObj2["paramName"] = response.params[i].paramName;
      paramObj2["paramId"] = response.params[i].paramId;
      paramObj2["value"] = response.params[i].paramValue;
      paramArray[i] = paramObj2
    }
    this.paramTableCol2 = paramArray
    this.paramTable = response.paramInfo;
    console.log(JSON.stringify(this.paramTable))
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('paramset', this.id).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'paramset')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }


  getAllLatest() {
    this._commonService.getAllLatest('paramlist')
      .subscribe(
      response => { this.onSuccessgetAllLatest(response) },
      error => console.log('Error :: ' + error)
      )
  }

  onSuccessgetAllLatest(response) {
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();

      dependOnTemp["label"] = response[0]["name"];
      dependOnTemp["uuid"] = response[0]["uuid"];
      dependOnTemp["version"] = response[0]["version"];
      this.tabledata = dependOnTemp
    }
    this.arrayParamList = [];

    for (const i in response) {
      let refParam = {};
      refParam["label"] = response[i]['name'];
      refParam["value"] = {};
      refParam["value"]["label"] = response[i]['name'];
      refParam["value"]["uuid"] = response[i]['uuid'];
      refParam["name"] = response[i]['name'];
      this.arrayParamList[i] = refParam;
    }
    console.log(JSON.stringify(this.arrayParamList))
  }

  submitParamset() {
    this.isSubmitEnable = true;
    let paramsetJson = {};
    paramsetJson["uuid"] = this.paramset.uuid
    paramsetJson["name"] = this.paramset.name


    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    paramsetJson['tags'] = tagArray

    paramsetJson["desc"] = this.paramset.desc;
    paramsetJson["active"] = this.paramset.active == true ? 'Y' : "N"
    paramsetJson["published"] = this.paramset.published == true ? 'Y' : "N"

    let dependsOnTemp = {};
    let refDepend = {};
    refDepend["type"] = "paramlist";
    refDepend["uuid"] = this.dependsOn.uuid;
    dependsOnTemp["ref"] = refDepend;
    paramsetJson["dependsOn"] = dependsOnTemp;

    let paramArray1 = [];
    if (this.paramTable.length > 0) {
      for (let i = 0; i < this.paramTable.length; i++) {
        let paraminfo = {};
        paraminfo["paramSetId"] = this.paramTable[i].paramSetId;
        let paramSetValarray = [];
        for (let j = 0; j < this.paramTable[i].paramSetVal.length; j++) {
          let paramObj2 = {};

          paramObj2["ref"] = refDepend;
          paramObj2["paramName"] = this.paramTable[i].paramSetVal[j].paramName;
          paramObj2["paramId"] = this.paramTable[i].paramSetVal[j].paramId;
          paramObj2["value"] = this.paramTable[i].paramSetVal[j].value;
          paramSetValarray[j] = paramObj2
        }
        paraminfo["paramSetVal"] = paramSetValarray;
        paramArray1[i] = paraminfo;
      }
    }
    console.log(JSON.stringify("paramArray obj is" + paramArray1))
    paramsetJson["paramInfo"] = paramArray1;

    console.log(JSON.stringify(paramsetJson))
    this._commonService.submit("paramset", paramsetJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Paramset Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);

  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/paramset']);
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/paramset', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
    }, 1000);
  }

}
