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

@Component({
  selector: 'app-load',
  styleUrls: [],
  templateUrl: './load.template.html',

})
export class LoadComponent {
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  selectVersion: any;
  append: any;
  header: any;
  allNames: SelectItem[] = [];
  targets: DependsOn
  msgs: Message[] = [];
  LoadSourceType: any;
  selectedAllFitlerRow: boolean;
  loadData: any;
  allName: any;
  versions: any[];
  showLoad: boolean;
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
  loadSourceType: any
  breadcrumbDataFrom: any;
  source: any
  LoadTargetType: any;
  loadTargetType: any;
  isSubmitEnable: any;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.loadData = {};
    this.loadData["active"] = true;
    this.loadData["header"] = true;
    this.loadData["append"] = true;
    this.isSubmitEnable = true;
    this.targets = { 'uuid': "", "label": "" }
    this.selectVersion = { "version": "" };
    this.showLoad = true;
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/load"
    },
    {
      "caption": "Load",
      "routeurl": "/app/list/load"

    },
    {
      "caption": "",
      "routeurl": null

    }
    ]
    this.LoadSourceType = ["file"];
    this.loadSourceType = "file";
    this.LoadTargetType = ["datapod"];
    this.loadTargetType = "datapod";

  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    });
    if (this.mode !== undefined) {
      this.getOneByUuidAndVersion(this.id, this.version)
      this.getAllVersionByUuid();
    }
    else {

      this.selectType()
    }
  }
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/load',]);

  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'load')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid("load", this.id).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.loadData = response
    this.createdBy = this.loadData.createdBy.ref.name;
    const version: Version = new Version();
    this.uuid = response.uuid
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.source = this.loadData.source.value;
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = this.loadData.target.ref.name
    dependOnTemp.uuid = this.loadData.target.ref.uuid
    this.targets = dependOnTemp
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.loadData.active = response["active"] == 'Y' ? true : false
    this.loadData.header = response["header"] == 'Y' ? true : false
    this.loadData.append = response["append"] == 'Y' ? true : false

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

    this._commonService.getAllLatest('datapod').subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
    this.breadcrumbDataFrom[2].caption = this.loadData.name;
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
  }
  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      //allName["uuid"]=response[i]['uuid']
      this.VersionList[i] = ver;
    }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  selectType() {
    this._commonService.getAllLatest('datapod').subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )

  }
  changeType() {

  }
  onChangeActive(event) {
    if (event === true) {
      this.loadData.active = 'Y';
    }
    else {
      this.loadData.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.loadData.published = 'Y';
    }
    else {
      this.loadData.published = 'N';
    }
  }
  onChangeAppend(event) {
    if (event === true) {
      this.loadData.append = 'Y';
    }
    else {
      this.loadData.append = 'N';
    }
  }
  onChangeHeader(event) {
    if (event === true) {
      this.loadData.header = 'Y';
    }
    else {
      this.loadData.header = 'N';
    }
  }
  submitLoad() {
    this.isSubmitEnable = true;
    let loadJson = {};
    loadJson["uuid"] = this.loadData.uuid
    loadJson["name"] = this.loadData.name
    // //let tagArray=[];
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    // loadJson["tags"] = tagstemp
    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    loadJson['tags'] = tagArray
    loadJson["desc"] = this.loadData.desc
    let target = {};
    let ref = {}
    ref["name"] = this.targets["name"]
    ref["uuid"] = this.targets["uuid"]
    ref["type"] = this.loadTargetType
    target["ref"] = ref;
    loadJson["target"] = target;
    let source = {}
    let ref1 = {}
    ref1["type"] = this.loadSourceType;
    source["ref"] = ref1;
    source["value"] = this.source;
    loadJson["source"] = source;
    loadJson["active"] = this.loadData.active == true ? 'Y' : "N"
    loadJson["published"] = this.loadData.published
    loadJson["header"] = this.loadData.header == true ? 'Y' : "N"
    loadJson["append"] = this.loadData.append == true ? 'Y' : "N"
    console.log(JSON.stringify(loadJson))
    this._commonService.submit("load", loadJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Load Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);

  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataPreparation/load', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/load', uuid, version, 'true']);
  }

}



