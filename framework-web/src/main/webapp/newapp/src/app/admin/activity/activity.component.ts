import { DropDownIO } from './../../metadata/domainIO/domain.dropDownIO';
import { AppHelper } from './../../app.helper';
import { Activity } from './../../metadata/domain/domain.activity';
import { MetaType } from './../../metadata/enums/metaType';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components';
import { RoutesParam } from '../../metadata/domain/domain.routeParams';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.template.html',
  styleUrls: ['./activity.component.css']
})
export class ActivityComponent implements OnInit {

  breadcrumbDataFrom: any;
  showActivity: any;
  activity: any;
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
  locked: any;
  depends: any;
  allName: any;
  status: any;
  userInfo: any;
  sessionInfo: any;
  requestUrl: any;
  metaInfo: any;
  metaInfoName: any;
  metaInfoType: any;
  msgs: any;
  isSubmitEnable: any;

  // isHomeEnable: boolean = false
  // showGraph: boolean = false;
  // isDependencyGraphEnable: boolean = true;
  isShowReportData: boolean = true;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  caretdown = 'fa fa-caret-down';
  metaType = MetaType;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean;
  isAdd: boolean;
  isHomeEnable: boolean;
  showForm: boolean = true;
  showDivGraph: boolean;
  isGraphInprogess: boolean;
  // defaultDate: Date;


  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private appHelper: AppHelper) {
    this.showActivity = true;
    this.activity = {};
    this.active = true
    this.isSubmitEnable = true;
    this.metaInfo = {};
    this.sessionInfo = {};
    this.userInfo = {};

    this.breadcrumbDataFrom = [
      {
        "caption": "Admin",
        "routeurl": "/app/list/activity"
      },
      {
        "caption": "Activity",
        "routeurl": "/app/list/activity"
      },
      {
        "caption": "",
        "routeurl": null
      }];

    this.isEditInprogess = false;
    this.isEditError = false;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();

    });
    this.setMode(this.mode);
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }

  enableEdit(uuid, version) {
    this.isEdit = true;
    console.log("enableEdit call.....");
  }

  showMainPage(uuid, version) {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
    // this.isDependencyGraphEnable = true;
    this.isShowReportData = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getOneByUuidAndVersion() {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(this.id, this.version, this.metaType.ACTIVITY)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.ACTIVITY, this.id)
      .subscribe(
        response => {
          this.onSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    // this.activity = new Activity;
    this.activity = response;

    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version

    this.createdBy = this.activity.createdBy.ref.name;

    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.locked = this.appHelper.convertStringToBoolean(response.locked);
    this.active = this.appHelper.convertStringToBoolean(response.active);

    if (response.userInfo != null) {
      this.userInfo = response.userInfo.ref.name;
    }
    else {
      this.userInfo = "";
    }
    if (response.sessionInfo != null) {
      this.sessionInfo = response.sessionInfo.ref.name;
    }
    else {
      this.sessionInfo = "";
    }
    if (response.metaInfo != null) {
      this.metaInfoType = response.metaInfo.ref.type;
      this.metaInfoName = response.metaInfo.ref.name;
    }
    else {
      this.metaInfo = "";
    }
    this.version = response.version;

    // this.breadcrumbDataFrom[2].caption = this.activity.name;
    console.log('Data is' + response);
    this.isEditInprogess = false;
  }

  onSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metaType.ACTIVITY)
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  submitActivity() {
    this.isSubmitEnable = true;
    let activityJson = new Activity;
    activityJson.uuid = this.activity.uuid
    activityJson.name = this.activity.name
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    activityJson.tags = this.activity.tagstemp;
    activityJson.desc = this.activity.desc
    //activityJson["createdBy"]=this.activity.createdBy
    activityJson.createdOn = this.activity.createdOn
    activityJson.active = this.appHelper.convertBooleanToString(this.active);
    activityJson.published = this.appHelper.convertBooleanToString(this.published);
    activityJson.locked = this.appHelper.convertBooleanToString(this.locked);
    activityJson.status = this.activity.status;
    
    let userInfo = new MetaIdentifierHolder;
    let refUserInfo = new MetaIdentifier;
    refUserInfo.name = this.userInfo;
    userInfo.ref = refUserInfo;
    activityJson.userInfo = userInfo;

    let sessionInfo = new MetaIdentifierHolder;
    let refSessionInfo = new MetaIdentifier;
    refSessionInfo.name = this.sessionInfo;
    sessionInfo.ref = refSessionInfo;
    activityJson.sessionInfo = sessionInfo;

    activityJson.requestUrl = this.activity.requestUrl;

    let metaInfo = new MetaIdentifierHolder;
    let refMetaInfo = new MetaIdentifier;
    refMetaInfo.type = this.metaInfoType
    refMetaInfo.name = this.metaInfoName
    metaInfo.ref = refMetaInfo;

    activityJson.metaInfo = metaInfo;
    console.log(JSON.stringify(activityJson));

    this._commonService.submit(this.metaType.ACTIVITY, activityJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }

  onSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Activity Submitted Successfully' });
    this.goBack()
    console.log('final response is' + response);
  }

  public goBack() {
    this._location.back();
  } 

}