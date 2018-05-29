import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { OperatorService } from '../../metadata/services/operator.service';

import { Version } from '../../metadata/domain/version';
import { error } from 'util';
import { DependsOn } from '../dependsOn';


@Component({
  selector: 'app-operator',
  templateUrl: './operator.template.html',
})
export class OperatorComponent implements OnInit {
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

  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _modelService: OperatorService) {
    this.operator = true;
    this.operator = {};
    //this.paramList ={'label':"","uuid":""}
    this.operator["active"] = true;

    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/operatortype"
    },
    {
      "caption": "Operator",
      "routeurl": "/app/list/operatortype"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];



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
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'operatortype')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }


  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('operatortype', this.id)
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'operatortype')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }



  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/operatortype']);
  }

  submitOperatorType() {
    
    this.isSubmitEnable = true;
    let operatorJson = {};
    operatorJson["uuid"] = this.operator.uuid;
    operatorJson["name"] = this.operator.name;
    //let tagArray=[];
    const tagstemp = [];
    for (const t in this.tags) {
      tagstemp.push(this.tags[t]["value"]);
    }
    // if(this.tags.length > 0){
    //   for(let counttag=0;counttag < this.tags.length;counttag++){
    //     tagArray[counttag]=this.tags[counttag]["value"];
    //   }
    // }
    operatorJson["tags"] = tagstemp;
    operatorJson["desc"] = this.operator.desc;
    operatorJson["active"] = this.operator.active == true ? "Y" : "N";
    operatorJson["published"] = this.operator.published == true ? "Y" : "N";
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
    this._commonService.submit("operatortype", operatorJson).subscribe(
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


}



