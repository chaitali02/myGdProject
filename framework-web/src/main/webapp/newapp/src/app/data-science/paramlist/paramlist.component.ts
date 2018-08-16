import { Location } from '@angular/common';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Version } from './../../shared/version';

import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';
import { DependsOn } from '../dependsOn';
import { ParamlistService } from '../../metadata/services/paramlist.service';

@Component({
  selector: 'app-paramlist',
  templateUrl: './paramlist.component.html',
  styleUrls: ['./paramlist.component.css']
})
export class ParamlistComponent implements OnInit {
  allTemplateList: any[];
  isTemplateInfoRequired: boolean = false;
  selectedTemplate: {};
  isUseTemplate: boolean = false;
  isTableDisable: boolean = false;
  defaultDistValue: any;
  distributionListOptions: any[];
  showParamlist: any;
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
  selectedVersion: Version
  breadcrumbDataFrom: any;
  paramlist: any;
  paramId: any;
  paramsArrays: any;
  loop: any;
  type: any;
  value: any;
  types: any;
  selectAllAttributeRow: any;
  msgs: any;
  isSubmitEnable: any;
  allDistribution: any;
  typeSimple: any;
  params: any;
  ref: any;
  paramtableArray: any;
  templateFlg : any;
  template : any;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _paramlistService : ParamlistService) {
    this.showParamlist = true;
    this.paramlist = {};
    this.paramlist["active"] = true;
    this.template = {};
    this.isSubmitEnable = true;
    this.paramtableArray = null;
    this.types = [{ 'value': 'date', 'label': 'date' },
    { 'value': 'double', 'label': 'double' },
    { 'value': 'integer', 'label': 'integer' },
    { 'value': 'string', 'label': 'string' },
    { 'value': 'list', 'label': 'list' },
    { 'value': 'distribution', 'label': 'distribution' },
    { 'value': 'attribute', 'label': 'attribute' },
    { 'value': 'attributes', 'label': 'attribute[s]' },
    { 'value': 'datapod', 'label': 'datapod' }]

    this.typeSimple = ["string", "double", "date", "integer", "list"];
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/paramlist"
    },
    {
      "caption": "Parameter List",
      "routeurl": "/app/list/paramlist"
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
      this.getOneByUuidAndVersion(this.id, this.version);
      this.getAllVersionByUuid();
    }
    else {
      this.getAllLatest1();
    }
  }

  onChangeIsTemplate(event){
    if (event === true) {
      this.templateFlg = 'Y';
    
    this._paramlistService.getAllLatestParamListByTemplate(this.templateFlg,"paramlist","")
    .subscribe(response => {this.onSuccessgetAllLatestParamListByTemplate(response)},
    error => console.log("Error :: " + error));
    }
    else 
      this.templateFlg = 'N';
  }

  onSuccessgetAllLatestParamListByTemplate(response){
    console.log(response)
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      ver["value"]["params"] = response[i]['params'];
      temp[i] = ver;
    }
    this.allTemplateList = temp;
    console.log(JSON.stringify(this.template.params));

    var arrayTemp = [];
    for (const i in this.template.params) {
      let paramtableObj = {};
      paramtableObj["paramName"] = this.template.params[i].paramName;
      paramtableObj["paramType"] = this.template.params[i].paramType;
      //paramtableObj["paramValue"] = this.template.params[i].paramValue;
      
      if (this.typeSimple.indexOf(this.template.params[i].paramType) != -1) {
        paramtableObj["paramValue"] = this.template.params[i].paramValue.value;
      }
      else if (this.template.params[i].paramType == "distribution") {
        let value1Temp: DependsOn = new DependsOn();
        value1Temp.label = this.template.params[i].paramValue.ref.name;
        value1Temp.uuid = this.template.params[i].paramValue.ref.uuid;

        paramtableObj["paramValue"] = value1Temp;
      }
      else if (this.template.params[i].paramValue == null) {
        paramtableObj["paramValue"] = "";
      }
      arrayTemp[i] = paramtableObj;
    }
    this.paramtableArray = arrayTemp;
  }

  onChangeTemplate(){    
    this.templateFlg = 'Y';
    
    this._paramlistService.getAllLatestParamListByTemplate(this.templateFlg,"paramlist","")
    .subscribe(response => {this.onSuccessgetAllLatestParamListByTemplate(response)},
    error => console.log("Error :: " + error));    
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'paramlist')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.paramlist = response
    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = this.paramlist.createdBy.ref.name
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }

    this.templateFlg = response['templateFlg'];
    
    this.tags = response['tags'];
    this.paramlist.published = response["published"] == 'Y' ? true : false
    this.paramlist.active = response["active"] == 'Y' ? true : false

    this.breadcrumbDataFrom[2].caption = response.name;
    var arrayTemp = [];
    for (const i in response.params) {
      let paramtableObj = {};
      paramtableObj["paramName"] = response.params[i].paramName;
      paramtableObj["paramType"] = response.params[i].paramType;

      if (this.typeSimple.indexOf(response.params[i].paramType) != -1) {
        paramtableObj["paramValue"] = response.params[i].paramValue.value;
      }
      else if (response.params[i].paramType == "distribution") {
        let value1Temp: DependsOn = new DependsOn();
        value1Temp.label = response.params[i].paramValue.ref.name;
        value1Temp.uuid = response.params[i].paramValue.ref.uuid;

        paramtableObj["paramValue"] = value1Temp;
      }
      else if (response.params[i].paramValue == null) {
        paramtableObj["paramValue"] = "";
      }
      arrayTemp[i] = paramtableObj;
    }
    this.paramtableArray = arrayTemp;

    this.getAllLatest1();
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid("paramlist", this.id).subscribe(
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
      //allName["uuid"]=response[i]['uuid']/*  */
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  getAllLatest1() {
    this._commonService.getAllLatest("distribution").subscribe(
      response => { this.onSuccessgetAllLatest1(response) },
      error => console.log("Error ::" + error)
    )
  }

  onSuccessgetAllLatest1(response) {
    let distributionListOptions = [];

    for (const i in response) {
      let distributionObj = {};
      distributionObj["label"] = response[i]['name'];
      distributionObj["value"] = {};
      distributionObj["value"]["label"] = response[i]['name'];
      distributionObj["value"]["uuid"] = response[i]['uuid'];
      distributionListOptions[i] = distributionObj;
    }
    this.distributionListOptions = distributionListOptions;
    console.log(this.distributionListOptions);
  }

  onChangeActive(event) {
    if (event === true) {
      this.paramlist.active = 'Y';
    }
    else {
      this.paramlist.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.paramlist.published = 'Y';
    }
    else {
      this.paramlist.published = 'N';
    }
  }

  addAttribute() {
    if (this.paramtableArray == null) {
      this.paramtableArray = [];
    }
    let len = this.paramtableArray.length + 1
    let attrinfo = {};
    attrinfo["name"] = "";
    attrinfo["id"] = len - 1;
    attrinfo["type"] = "";
    attrinfo["value"] = "";
    this.paramtableArray.splice(this.paramtableArray.length, 0, attrinfo);
  }

  removeAttribute(){  
    var newDataList=[];
    this.selectAllAttributeRow=false
    this.paramtableArray.forEach(selected => {
      if(!selected.selected){
        newDataList.push(selected);
      }
    });
    this.paramtableArray = newDataList;
    console.log(JSON.stringify(this.paramtableArray))
  }

  checkAllAttributeRow() {
    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    this.paramtableArray.forEach(attribute => {
      attribute.selected = this.selectAllAttributeRow;
    });
  }

  onVersionChange(){ 
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'paramlist')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  submitParamlist() {
    this.isSubmitEnable = true;
    let paramlistJson = {};
    paramlistJson["uuid"] = this.paramlist.uuid
    paramlistJson["name"] = this.paramlist.name
    var tagArray = [];
    if (this.paramlist.tags != null) {
      for (var counttag = 0; counttag < this.paramlist.tags.length; counttag++) {
        tagArray[counttag] = this.paramlist.tags[counttag];
      }
    }
    paramlistJson["tags"] = tagArray;
    paramlistJson["desc"] = this.paramlist.desc
    paramlistJson["active"] = this.paramlist.active == true ? 'Y' : "N"
    paramlistJson["published"] = this.paramlist.published == true ? 'Y' : "N"

    var paramInfoArray = [];
    for (var i = 0; i < this.paramtableArray.length; i++) {
      var attributemap = {};
      attributemap["paramId"] = i;
      attributemap["paramName"] = this.paramtableArray[i].paramName
      attributemap["paramType"] = this.paramtableArray[i].paramType
      let paramValue = {};
      if (this.typeSimple.indexOf(this.paramtableArray[i].paramType) != -1) {
        let paramRef = {};
        paramRef["type"] = "simple"
        paramValue["ref"] = paramRef;
        paramValue["value"] = this.paramtableArray[i].paramValue;
        attributemap["paramValue"] = paramValue;
        paramInfoArray[i] = attributemap;
      }
      else if (this.paramtableArray[i].paramType == 'distribution') {
        var paramRef = {};
        paramRef["type"] = this.paramtableArray[i].paramType;
        if (this.paramtableArray[i].paramValue != null) {
          paramRef["uuid"] = this.paramtableArray[i].paramValue.uuid;
        }
        paramValue["ref"] = paramRef;
        attributemap["paramValue"] = paramValue;
        paramInfoArray[i] = attributemap;
      }
      else {
        paramValue = null;
        attributemap["paramValue"] = paramValue
        paramInfoArray[i] = attributemap;
      }
    }
    paramlistJson["params"] = paramInfoArray;
    console.log(JSON.stringify(paramlistJson))
    this._commonService.submit("paramlist", paramlistJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Paramlist Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/paramlist']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/paramlist', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataScience/paramlist', uuid, version, 'true']);
  }
}
