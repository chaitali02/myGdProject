import { AppConfig } from './../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { Component, ViewChild } from "@angular/core";
import { DatePipe, Location } from "@angular/common";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';
import { KnowledgeGraphComponent } from '../../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-trainExec',
  styleUrls: [],
  templateUrl: './predictExec.template.html',
})
export class PredictExecComponent {
  showGraph: boolean;
  isHomeEnable: boolean;
  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  predictResultData: any;
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
  result: any;
  showResultPredict: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showResultPredict = true;
    this.predictResultData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Prediction Exec",
      "routeurl": "/app/list/predictExec"
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
      this.getAllVersionByUuid();
      this.getOneByUuidAndVersion(this.id, this.version);
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
      this.predictResultData.active = 'Y';
    }
    else {
      this.predictResultData.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.predictResultData.published = 'Y';
    }
    else {
      this.predictResultData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'predictexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('predictexec', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.predictResultData = response
    this.createdBy = this.predictResultData.createdBy.ref.name;
    this.dependsOn = this.predictResultData.dependsOn.ref.name;
    // this.result=this.predictResultData.result.ref.name;
    if (this.predictResultData.result !== null) {
      this.result = this.predictResultData.result.ref.name;
    }
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

    this.breadcrumbDataFrom[2].caption = this.predictResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'predictexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  public goBack() {
    this._location.back();
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showGraph = false;
  }

}