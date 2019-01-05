import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Version } from '../../../shared/version';

@Component({
  selector: 'app-settings-details',
  templateUrl: './settings-details.component.html'
})
export class SettingsDetailsComponent implements OnInit {
  selectedAllFitlerRow: boolean;
  desc: any;
  configInfo: any;
  msgs: any[];
  createdOn: any;
  name: string;
  tags: any[];
  active: any;
  published: any;
  createdBy: any;
  selectedVersion: any;
  uuid: any;
  settingsData: any;
  VersionList: any[];
  mode: any;
  version: any;
  id: any;
  isSubmitEnable:any; 
  types :any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string }[];

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/settings"
    },
    {
      "caption": "settings",
      "routeurl": "/app/list/settings"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.types = [
      {"label": "string","value": "string"},
      {"label": "double","value": "double"},
      {"label": "date","value": "date"},
      {"label": "integer","value": "integer"},
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion();
      }
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'appconfig')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('appconfig', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
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

  onSuccessgetOneByUuidAndVersion(response) {
    this.isSubmitEnable=true;
    this.uuid = response.uuid;
    this.name = response.name;
    this.desc = response.desc;

    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version;

    this.createdBy = response.createdBy.ref.name;
    this.createdOn = response.createdOn;
    this.published = response["published"] == 'Y' ? true : false;
    this.active = response["active"] == 'Y' ? true : false
    this.version = response['version'];

    if (response.tags != null) {
      let temp = []
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        temp[i] = tag
      }
      this.tags = temp;
    }
    this.breadcrumbDataFrom[2].caption = this.name;
    this.configInfo = response.configInfo;
  }

  submitSettings() {
    this.isSubmitEnable = true;
    let settingsJson = {};
    settingsJson["uuid"] = this.uuid
    settingsJson["name"] = this.name


    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;
      }
    }
    settingsJson['tags'] = tagArray

    settingsJson["desc"] = this.desc;
    settingsJson["active"] = this.active == true ? 'Y' : "N"
    settingsJson["published"] = this.published == true ? 'Y' : "N"

    let configInfo = [];
    if (this.configInfo != null) {
      for (let i = 0; i < this.configInfo.length; i++) {
        let configInfoObj = {};
        configInfoObj["configId"] = this.configInfo[i].configId;
        configInfoObj["configName"] = this.configInfo[i].configName;
        configInfoObj["configType"] = this.configInfo[i].configType;
        configInfoObj["configVal"] = this.configInfo[i].configVal;
        configInfo[i] = configInfoObj;
      }
      settingsJson["configInfo"] = configInfo;
    }
    console.log(JSON.stringify(settingsJson));
    this._commonService.submit("appconfig", settingsJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable=true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'App config list Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);


    // this.msgs = [];
    // this.isSubmit = "true"
    // this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Ingest Rule Saved Successfully' });
    // setTimeout(() => { this.goBack() }, 1000);

  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'appconfig')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.active = 'Y';
    }
    else {
      this.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.published = 'Y';
    }
    else {
      this.published = 'N';
    }
  }

  public goBack() {
   // this._location.back();
     this.router.navigate(['app/admin/settings']);
  }

  addRow() {
    if (this.configInfo == null) {
      this.configInfo = [];
    }
    var len = this.configInfo.length + 1
    var configInfoObj = {};
    configInfoObj["configId"] = len-1;
    configInfoObj["configName"] = ""
    configInfoObj["configType"] = ""
    configInfoObj["configVal"] = ""
    this.configInfo.splice(this.configInfo.length, 0, configInfoObj);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.configInfo.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.configInfo = newDataList;
  }
  checkAllAppConfigRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.configInfo.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }
  enableEdit(uuid, version) {
    //this.router.navigate(['app/dataIngestion/ingest', uuid, version, 'false']);
    this.router.navigate([ 'app/admin/settings',uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/admin/settings', uuid, version, 'true']);
  }
}
