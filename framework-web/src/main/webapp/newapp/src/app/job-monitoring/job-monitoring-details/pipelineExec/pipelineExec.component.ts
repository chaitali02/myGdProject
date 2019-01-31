import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { AppConfig } from '../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { AppMetadata } from '../../../app.metadata';
import { Version } from '../../../shared/version';
import { AppHepler } from '../../../app.helper';

@Component({
  selector: 'app-pipelineExec',
  templateUrl: './pipelineExec.template.html',
  styleUrls: []
})
export class PipelineExecComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;

  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  dagResultData: any;
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
  stages: any[];
  showResultModel: any;
  operatorsType: any;
  operatorsName: any;
  operatorType: any;
  operatorName: any;
  routerUrl: any;
  operatorInfo: any;
  ref: any;

  constructor(private datePipe: DatePipe, public apphelper: AppHepler, private _location: Location, public statusDefs: AppMetadata, public metaconfig: AppMetadata, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showResultModel = true;
    this.dagResultData = {};
    this.operatorInfo = {};
    this.operatorType = null;
    this.operatorName = null;
    this.isHomeEnable = false;
    this.showGraph = false;
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Pipeline",
      "routeurl": "/app/list/dagExec"
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
      this.dagResultData.active = 'Y';
    }
    else {
      this.dagResultData.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.dagResultData.published = 'Y';
    }
    else {
      this.dagResultData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'dagexec')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('dagexec', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.dagResultData = response
    this.createdBy = this.dagResultData.createdBy.ref.name;
    this.dependsOn = this.dagResultData.dependsOn.name;
  
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList

    let stagesObj = [];
    for (let i = 0; i < response.stages.length; i++) {
      let ref = {};
      ref["dependsOn"] = response.stages[i].dependsOn;
      ref["stageId"] = response.stages[i].stageId;
      stagesObj[i] = ref;
    }
    this.stages = stagesObj;

    console.log(JSON.stringify(this.stages));
    for (let i = 0; i < this.stages.length; i++) {
      if (this.stages[i]["status"] != null) {
        this.stages[i]["status"] = this.apphelper.sortByProperty(response[i]["status"], "createdOn");
        let status = response[i]["status"];
        this.stages[i]["status"] = {};
        this.stages[i]["status"].stage = this.apphelper.getStatus(status)["stage"];
        this.stages[i]["status"].color = this.metaconfig.getStatusDefs(response[i]["status"].stage)['color'];
      }
    }
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];

    this.breadcrumbDataFrom[2].caption = this.dagResultData.name;
    for (let i = 0; i < this.dagResultData.stages.length; i++) {

      if (this.dagResultData.stages[i]["statusList"] != null) {
        this.dagResultData.stages[i]["statusList"]["stage"] = this.dagResultData.stages[i]["statusList"][(statusList.length - 1)].stage
        let status = this.dagResultData.stages[i]["statusList"]["stage"];
        this.dagResultData.stages[i]["statusList"] = { 'stage': '', 'color': '' };
        this.dagResultData.stages[i]["statusList"].stage = status;
        this.dagResultData.stages[i]["statusList"].color = this.metaconfig.getStatusDefs(this.dagResultData.stages[i]["statusList"]["stage"])['color'];
      }
      for (let j = 0; j < this.dagResultData.stages[i]["tasks"].length; j++) {
        if (this.dagResultData.stages[i]["tasks"][j]["statusList"] != null) {
          this.dagResultData.stages[i]["tasks"][j]["statusList"]["stage"] = this.dagResultData.stages[i]["tasks"][j]["statusList"][(statusList.length - 1)].stage
          let status = this.dagResultData.stages[i]["tasks"][j]["statusList"]["stage"];
          this.dagResultData.stages[i]["tasks"][j]["statusList"] = { 'stage': '', 'color': '' };
          this.dagResultData.stages[i]["tasks"][j]["statusList"].stage = status;
          this.dagResultData.stages[i]["tasks"][j]["statusList"].color = this.metaconfig.getStatusDefs(this.dagResultData.stages[i]["tasks"][j]["statusList"]["stage"])['color'];
        }
      }
    }
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'dagexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  public goBack() {
    this._location.back();
  }

  onShowDetail(innerData) {
    console.log('function called');
    var innerType = innerData.operators[0].operatorInfo[0].ref.type;
    var innerUuid = innerData.operators[0].operatorInfo[0].ref.uuid;
    var innerVersion = innerData.operators[0].operatorInfo[0].ref.version;

    this.routerUrl = this.metaconfig.getMetadataDefs(innerType)['detailState']

    this.router.navigate(['../../../../../JobMonitoring', innerType, innerUuid, innerVersion, 'true'], { relativeTo: this.activatedRoute });
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}
