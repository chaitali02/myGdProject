import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { ApplicationService } from '../../metadata/services/application.service';
import { ApplicationResource } from './application-resource';

import { Version } from '../../shared/version';

@Component({
  selector: 'app-application',
  templateUrl: './application.template.html',
  styleUrls: ['./application.component.css']
})
export class ApplicationComponent implements OnInit {

  breadcrumbDataFrom: any;
  showApplication: any;
  application: any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  selectdatasourceType: any;
  datasourceType: { 'value': string; name: string; }[];
  attrtypes: string[];
  detasource_uuid: any;
  datasource1: any;
  datasource: any;
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
  msgs: any;
  isSubmitEnable: any;
  constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _applicationService: ApplicationService) {
    this.showApplication = true;
    this.application = {};
    this.application["active"] = true

    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/application"
    },
    {
      "caption": "Application",
      "routeurl": "/app/list/application"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
  }

  ngOnInit() {
    this.attrtypes = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
    this.datasourceType = [{ 'value': 'FILE', name: 'file' },
    { 'value': 'HIVE', name: 'hive' },
    { 'value': 'IMPALA', name: 'impala' },
    { 'value': 'MYSQL', name: 'mysql' },
    { 'value': 'ORACLE', name: 'oracle' }];
    this.selectdatasourceType = this.datasourceType[0].value
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
      else {
        this.selectType(this.selectdatasourceType);
      }
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'application')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('application', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.application = response;
    this.createdBy = response.createdBy.ref.name;
    this.uuid = response.uuid;

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

    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.version = response['version'];
    this.published = response['published'];
    this.application.published = response["published"] == 'Y' ? true : false
    this.application.active = response["active"] == 'Y' ? true : false
    this.detasource_uuid = response.dataSource.ref.uuid
    this.selectdatasourceType = (response.dataSource.ref.name).toUpperCase();
    this.selectType(this.selectdatasourceType);

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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'application')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onChangeActive(event) {

    if (event === true) {
      this.application.active = 'Y';
    }
    else {
      this.application.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.application.published = 'Y';
    }
    else {
      this.application.published = 'N';
    }
  }

  selectType(val) {
    this._applicationService.getDatasourceByType(val)
      .subscribe(
      response => {
        this.OnSuccesDatasourceByType(response)
      },
      error => console.log("Error :: " + error));
  }
  OnSuccesDatasourceByType(response) {
    this.detasource_uuid = response[0]['uuid'];
    const datasource2: Array<ApplicationResource> = [];
    for (const i in response) {
      datasource2.push(new ApplicationResource(
        response[i]['uuid'], response[i]['name']
      ));
    }
    this.datasource1 = datasource2;
  }


  submitApplication() {
    let applicationJson = {};
    this.isSubmitEnable = true;
    applicationJson["uuid"] = this.application.uuid
    applicationJson["name"] = this.application.name
    applicationJson["desc"] = this.application.desc
    applicationJson["active"] = this.application.active == true ? 'Y' : "N"
    applicationJson["published"] = this.application.published == true ? 'Y' : "N"

    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    applicationJson['tags'] = tagArray
    let datasource = {}
    let ref = {};
    ref["type"] = "datasource";
    ref["uuid"] = this.detasource_uuid;
    datasource["ref"] = ref;
    applicationJson["dataSource"] = datasource;
    console.log(JSON.stringify(applicationJson));
    this._commonService.submit("application", applicationJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error ::' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Activity Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
    console.log('final response is' + response);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/application']);
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/application', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/admin/application', uuid, version, 'true']);
  }

}
