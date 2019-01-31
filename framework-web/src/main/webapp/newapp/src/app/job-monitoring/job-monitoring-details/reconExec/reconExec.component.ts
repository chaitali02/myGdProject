import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import * as sqlFormatter from "sql-formatter";
@Component({
  selector: 'app-recon-exec',
  templateUrl: './reconExec.component.html'
})
export class ReconExecComponent implements OnInit {
  isHomeEnable: boolean;
  showGraph: boolean;
  displayDialogBox: boolean = false;
  formattedQuery: any;
  exec: any;
  refKeyList: any[];
  result: any;
  tags: any;
  active: any;
  statusList: any[];
  dependsOn: any;
  createdBy: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  mode: any;
  version: any;
  id: any;
  name: any;
  reconData: {};
  selectedVersion: any;
  VersionList: any[];

  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showGraph = false;
    this.isHomeEnable = false;
    this.reconData = {};
    //this.createdBy = {};
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    });
    if (this.mode !== undefined) {
      this.getAllVersionByUuid();
      this.getOneByUuidAndVersion(this.id, this.version)
    }

    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Recon Exec",
      "routeurl": "/app/list/reconExec"
    },
    {
      "caption": "",
      "routeurl": null
    }]

  }

  ngOnInit() {
  }

  public goBack() {
    this._location.back();
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'reconexec')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('reconexec', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.reconData = response
    this.createdBy = this.reconData['createdBy']['ref']['name'];
    this.dependsOn = this.reconData['dependsOn']['ref']['name'];
    this.result = this.reconData['result']['ref']['name'];
    var d;
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }

    this.statusList = statusList
    var refKeyList = [];
    for (let i = 0; i < response.refKeyList.length; i++) {
      refKeyList[i] = response.refKeyList[i].ref.type + "." + response.refKeyList[i].ref.name;
    }
    this.refKeyList = refKeyList
    this.exec = response.exec;
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];
    this.breadcrumbDataFrom[2].caption = this.reconData["name"];
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'reconexec')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.reconData["active"] = 'Y';
    }
    else {
      this.reconData["active"] = 'N';
    }
  }

  onChangePublish(event) {
    if (event === true) {
      this.reconData["published"] = 'Y';
    }
    else {
      this.reconData["published"] = 'N';
    }
  }

  showSqlFormater() {
    console.log(sqlFormatter.format("SELECT * FROM table1"));
    this.formattedQuery = sqlFormatter.format(this.exec);
    this.displayDialogBox = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
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
