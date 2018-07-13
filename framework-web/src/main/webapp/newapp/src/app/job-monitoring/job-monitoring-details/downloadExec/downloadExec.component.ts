import { AppConfig } from './../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { Component } from "@angular/core";
import { DatePipe, Location } from "@angular/common";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';


@Component({
  selector: 'app-downloadExec',
  styleUrls: [],
  templateUrl: './downloadExec.template.html',

})

export class DownloadExecComponent {

  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  downloadData: any;
  uuid: any;
  name: any;
  createdBy: any;
  createdOn: any;
  tags: any;
  desc: any;
  active: any;
  published: any;
  statusList: any;
  dependsOn: any;
  refKeyList: any;
  location: any;
  showResultTrain: any;


  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showResultTrain = true;
    this.downloadData = {};
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "download Exec",
      "routeurl": "/app/list/downloadExec"

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
      this.getOneByUuidAndVersion(this.id, this.version)
      this.getAllVersionByUuid()

    }
  }

  onChangeActive(event) {
    if (event === true) {
      this.downloadData.active = 'Y';
    }
    else {
      this.downloadData.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.downloadData.published = 'Y';
    }
    else {
      this.downloadData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'downloadexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('downloadexec', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.downloadData = response
    this.createdBy = this.downloadData.createdBy.ref.name;
    this.dependsOn = this.downloadData.dependsOn.ref.name;
    this.location = this.downloadData.location
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];

    this.breadcrumbDataFrom[2].caption = this.downloadData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'downloadexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  public goBack() {
    this._location.back();
  }
}