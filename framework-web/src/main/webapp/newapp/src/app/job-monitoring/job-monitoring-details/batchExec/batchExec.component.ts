import { Component, OnInit, ViewChild } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { AppConfig } from '../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { AppMetadata } from '../../../app.metadata';
import { Version } from '../../../shared/version';
import { KnowledgeGraphComponent } from '../../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-batchExec',
  templateUrl: './batchExec.template.html',
  styleUrls: []
})
export class BatchExecComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;

  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  batchResultData: any;
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
  execList: any[] = [];
  showExec: boolean = false;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private datePipe: DatePipe, private _location: Location, public statusDefs: AppMetadata, public metaconfig: AppMetadata, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showResultModel = true;
    this.batchResultData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.operatorInfo = {};
    this.operatorType = null;
    this.operatorName = null;
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Batch Exec",
      "routeurl": "/app/list/batchExec"
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

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  onChangeActive(event) {
    if (event === true) {
      this.batchResultData.active = 'Y';
    }
    else {
      this.batchResultData.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.batchResultData.published = 'Y';
    }
    else {
      this.batchResultData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'batchexec')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('batchexec', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.batchResultData = response
    this.createdBy = this.batchResultData.createdBy.ref.name;
    this.dependsOn = this.batchResultData.dependsOn.ref.name;

    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList

    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];

    this.breadcrumbDataFrom[2].caption = this.batchResultData.name;

    var execList = [];
    for (let i = 0; i < response.execList.length; i++) {
      var execlist = {};
      execlist["type"] = response.execList[i].ref.type;
      execlist["name"] = response.execList[i].ref.name;
      execlist["uuid"] = response.execList[i].ref.uuid;
      execlist["version"] = response.execList[i].ref.version;
      execList[i] = execlist;
    }
    this.execList = execList;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'batchexec')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  public goBack() {
    this._location.back();
  }

  onShowDetail(innerData) {
    var innerType = innerData.type;
    var innerUuid = innerData.uuid;
    var innerVersion = innerData.version;
    this.routerUrl = this.metaconfig.getMetadataDefs(innerType)['detailState']
    this.router.navigate(["../../../../../list/dagExec/JobMonitoring/" + innerType, innerUuid, innerVersion, 'true'], { relativeTo: this.activatedRoute });
  }

  showview(uuid: string, version: string) {
    this.showExec = true
  }

  refershGrid() {
    console.log("refresh Call...");
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

}
