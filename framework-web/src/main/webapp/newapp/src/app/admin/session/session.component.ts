import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-session',
  templateUrl: './session.template.html',
  styleUrls: ['./session.component.css']
})
export class SessionComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;

  breadcrumbDataFrom: any;
  showSession: any;
  session: any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  depends: any;
  allName: any;
  userInfo: any;
  roleInfo: any;
  statusList: any;
  msgs: any;
  isSubmitEnable: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showSession = true;
    this.session = {};
    //this.userInfo = {};
    // this.roleInfo = {};
    // this.statusList = {};
    // this.roleInfo = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.session["active"] = true;
    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/session"
    },
    {
      "caption": "Session",
      "routeurl": "/app/list/session"
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
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
    })
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'session')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('session', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.session = response;
    this.createdBy = response.createdBy.ref.name;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.session.locked = response["locked"] == 'Y' ? true : false
    this.session.published = response["published"] == 'Y' ? true : false
    this.session.active = response["active"] == 'Y' ? true : false
    this.version = response['version'];
    if (response.userInfo !== null) {
      this.userInfo = response.userInfo.ref.name;
    }
    if (response.roleInfo !== null) {
      this.roleInfo = response.roleInfo.ref.name;
    }
    if (response.statusList !== null) {
      this.statusList = response.statusList.stage;
    }
    this.breadcrumbDataFrom[2].caption = this.session.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'session')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onChangeActive(event) {

    if (event === true) {
      this.session.active = 'Y';
    }
    else {
      this.session.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.session.published = 'Y';
    }
    else {
      this.session.published = 'N';
    }
  }

  onChangeLocked(event) {
    if (event === true) {
      this.session.locked = 'Y';
    }
    else {
      this.session.locked = 'N';
    }
  }

  submitSession() {
    this.isSubmitEnable = true;
    let sessionJson = {};
    sessionJson["name"] = this.session.name;
    const tagstemp = [];
    for (const t in this.tags) {
      tagstemp.push(this.tags[t]["value"]);
    }
    // if(this.tags.length > 0){
    //   for(let counttag=0;counttag < this.tags.length;counttag++){
    //     tagArray[counttag]=this.tags[counttag]["value"];
    //   }
    // }
    sessionJson["tags"] = tagstemp
    sessionJson["desc"] = this.session.desc;
    sessionJson["active"] = this.session.active == true ? 'Y' : "N"
    sessionJson["published"] = this.session.published == true ? 'Y' : "N"
    sessionJson["locked"] = this.session.locked == true ? 'Y' : "N"
    let userInfo = {}
    let refUserInfo = {}
    refUserInfo["name"] = this.userInfo;
    userInfo["ref"] = refUserInfo;
    sessionJson["userInfo"] = userInfo;

    let statusList = {}
    let status = []
    statusList["stage"] = this.statusList;
    status[0] = statusList
    sessionJson["statusList"] = status;

    let roleInfo = {}
    let refRoleInfo = {}
    refRoleInfo["name"] = this.roleInfo;
    roleInfo["ref"] = refRoleInfo;
    sessionJson["roleInfo"] = roleInfo;

    console.log(JSON.stringify(sessionJson))
    this._commonService.submit("session", sessionJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ', +error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Session Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
    console.log('final response is' + JSON.stringify(response));
  }

  public goBack() {
    this._location.back();
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

}