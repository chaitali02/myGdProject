import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { OperatorService } from '../../metadata/services/operator.service';

import { Version } from '../../metadata/domain/version';
import { error } from 'util';
import { DependsOn } from '../dependsOn';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component'
@Component({
  selector: 'app-operator',
  templateUrl: './operator.template.html',
})
export class OperatorComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  selectallattribute: any;
  ishowExecutionparam: boolean;
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
  arrayParamList: any;
  createdBy: any;
  name: any;
  version: any;
  breadcrumbDataFrom: any;
  operator: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmitEnable: any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any;
  paramList: DependsOn;
  operatortypesOption: { 'value': String, 'label': String }[];
  operatorType: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _OperatorService: OperatorService) {
    this.operator = true;
    this.operator = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    //this.paramList ={'label':"","uuid":""}
    this.operator["active"] = true;

    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/operator"
    },
    {
      "caption": "Operator",
      "routeurl": "/app/list/operator"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];

    this.operatortypesOption = [
      { "value": "GenerateData", "label": "GenerateData" },
      { "value": "Transpose", "label": "Transpose" },
      { "value": "CloneData", "label": "CloneData" }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();

      }
      this.getAllLatest();
    })
  }
  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'operator')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('operator', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.operator = response
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = response.createdBy.ref.name;
    this.operator.published = response["published"] == 'Y' ? true : false
    this.operator.active = response["active"] == 'Y' ? true : false
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.operator.tags = tags;
    }//End If

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["paramList"]["ref"]["name"];
    dependOnTemp.uuid = response["paramList"]["ref"]["uuid"];
    this.paramList = dependOnTemp;
    this.breadcrumbDataFrom[2].caption = this.operator.name
    console.log('Data is' + response);

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


  getAllLatest() {
    this._commonService.getAllLatest('paramList')
      .subscribe(
      response => {
        this.onSuccessgetAllLatest(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response) {
    this.arrayParamList = [];
    for (const i in response) {
      let refParam = {}
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}
      refParam["value"]["uuid"] = response[i]["uuid"]
      // refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['name'];
      this.arrayParamList[i] = refParam;

    }
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'operator')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/operator']);
  }

  submitoperator() {
    this.isSubmitEnable = true;
    let operatorJson = {};
    operatorJson["uuid"] = this.operator.uuid;
    operatorJson["name"] = this.operator.name;
    // //let tagArray=[];
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    var tagArray = [];
    if (this.operator.tags != null) {
      for (var counttag = 0; counttag < this.operator.tags.length; counttag++) {
        tagArray[counttag] = this.operator.tags[counttag].value;

      }
    }
    operatorJson['tags'] = tagArray
    operatorJson["desc"] = this.operator.desc;
    operatorJson["active"] = this.operator.active == true ? "Y" : "N";
    operatorJson["published"] = this.operator.published == true ? "Y" : "N";
    operatorJson["operatorType"] = this.operator.operatorType;

    let paramlist = {};
    let refParam = {};
    if (this.paramList != null) {
      refParam["uuid"] = this.paramList.uuid;
      refParam["type"] = "paramlist";
      // refParam["name"] = this.paramList.name;
      paramlist["ref"] = refParam;
    }
    operatorJson["paramList"] = paramlist;
    console.log(JSON.stringify(operatorJson));
    this._commonService.submit("operator", operatorJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    console.log(response)
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Ditr Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/operator', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false;
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



