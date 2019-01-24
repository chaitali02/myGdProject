import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';
import { AppMetadata } from '../../../app.metadata';


@Component({
  selector: 'app-reconGroupExec',
  templateUrl: './reconGroupExec.template.html',
  styleUrls: []
})
export class ReconGroupExecComponent implements OnInit {

  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  recongroupResultData: any;
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
  execList: any;
  results: any;
  showResultModel: any;
  routerUrl: any;
  isHomeEnable: boolean;
  showGraph: boolean;

  constructor(private datePipe: DatePipe, private _location: Location, public metaconfig: AppMetadata, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showGraph = false;
    this.isHomeEnable = false;
    this.showResultModel = true;
    this.recongroupResultData = {};
    this.execList = [];
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Recon Group Exec",
      "routeurl": "/app/list/recongroupExec"

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
      this.recongroupResultData.active = 'Y';
    }
    else {
      this.recongroupResultData.active = 'N';
    }
  }

  onChangePublish(event) {
    if (event === true) {
      this.recongroupResultData.published = 'Y';
    }
    else {
      this.recongroupResultData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'recongroupexec')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('recongroupexec', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {

    this.recongroupResultData = response
    this.createdBy = this.recongroupResultData.createdBy.ref.name;
    this.dependsOn = this.recongroupResultData.dependsOn.ref.name;


    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList

    let execListObj = [];
    for (let i = 0; i < response.execList.length; i++) {

      let ref = {};
      ref["type"] = response.execList[i].ref.type;
      ref["uuid"] = response.execList[i].ref.uuid;
      ref["name"] = response.execList[i].ref.name;
      ref["version"] = response.execList[i].ref.version;

      execListObj[i] = ref;
    }

    this.execList = execListObj;

    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];

    this.breadcrumbDataFrom[2].caption = this.recongroupResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'recongroupexec')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  public goBack() {
    this._location.back();
  }

  showDetails(item) {
    console.log('function called');
    // var innerType = innerData.operators[0].operatorInfo.ref.type;
    // var innerUuid = innerData.operators[0].operatorInfo.ref.uuid;
    // var innerVersion = innerData.operators[0].operatorInfo.ref.version;


    this.routerUrl = this.metaconfig.getMetadataDefs(item.type)['detailState']

    this.router.navigate(['../../../../../JobMonitoring', item.type, item.uuid, item.version, 'true'], { relativeTo: this.activatedRoute });

  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}
