import { Component, OnInit, ViewChild } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';
import { KnowledgeGraphComponent } from '../../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-ruleExec',
  templateUrl: './ruleExec.template.html',
  styleUrls: []
})
export class RuleExecComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  breadcrumbDataFrom: any;
  id: any;
  version: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode: any;
  ruleResultData: any;
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
  exec: any;
  showResultModel: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  
  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {

    this.showGraph = false
    this.isHomeEnable = false
    this.showResultModel = true;
    this.ruleResultData = {};
    this.refKeyList = [];
    this.breadcrumbDataFrom = [{
      "caption": "Job Monitoring ",
      "routeurl": "/app/jobMonitoring"
    },
    {
      "caption": "Data Quality",
      "routeurl": "/app/list/dqExec"
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
      this.getAllVersionByUuid()
      this.getOneByUuidAndVersion(this.id, this.version)
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
      this.ruleResultData.active = 'Y';
    }
    else {
      this.ruleResultData.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.ruleResultData.published = 'Y';
    }
    else {
      this.ruleResultData.published = 'N';
    }
  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'ruleexec')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('ruleexec', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.ruleResultData = response
    this.createdBy = this.ruleResultData.createdBy.ref.name;
    this.dependsOn = this.ruleResultData.dependsOn.ref.name;
    if (this.ruleResultData.result !== null) {
      this.result = this.ruleResultData.result.ref.name;
    }
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList

    let refKeyListObj = [];
    if (response.refKeyList != null) {
      for (let i = 0; i < response.refKeyList.length; i++) {

        let ref = {};
        ref["type"] = response.refKeyList[i].type;
        ref["uuid"] = response.refKeyList[i].uuid;
        ref["name"] = response.refKeyList[i].name;

        refKeyListObj[i] = ref["type"] + "-" + ref["name"];
      }

      this.refKeyList = refKeyListObj;
    }
    this.published = response['published'];
    if (this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];

    this.breadcrumbDataFrom[2].caption = this.ruleResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'ruleexec')
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
