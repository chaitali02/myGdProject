import { MetaType } from './../metadata/enums/metaType';

import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';

import { AppMetadata } from '../app.metadata';
// import{JointjsGroupComponent } from './jointjsgroup.component'
import { TableRenderComponent } from '../shared/components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../metadata/services/common.service';
import { Message } from 'primeng/components/common/api';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../app.config';
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { CommonListService } from '../common-list/common-list.service';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
@Component({
  selector: 'app-profile',
  templateUrl: './data-profileresult.template.html',
  styleUrls: []
})
export class DataProfileresultComponent {
  isResultTable: boolean;
  //showHome: boolean = true;
  showKnowledgeGraph: boolean;
  isHomeEnable: boolean;
  isDownloadModel: boolean;
  runMode: any;
  download: {};
  downloadType: any;
  downloadVersion: any;
  downloadUuid: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  _type: any;
  _uuid: any;
  _version: any;
  _mode: any;
  istableShow: boolean;
  isgraphShow: boolean;
  params: any
  baseUrl: any;
  metaType = MetaType;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
  @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;
  restartDialogBox: boolean;
  msgs: Message[] = [];
  restartStatus: any;

  constructor(private http: Http, private _config: AppConfig, private _location: Location,
    private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _commonListService: CommonListService) {
    this.baseUrl = _config.getBaseUrl();
    this.isgraphShow = false;
    this.istableShow = false;
    this.download = {}
    this.isHomeEnable = false;
    this.showKnowledgeGraph = false;
    this.download["format"] = ["excel"]
    this.download["rows"] = 100
    this.download["selectFormat"] = 'excel'
    this.breadcrumbDataFrom = [{
      "caption": "Data Profiling ",
      "routeurl": "/app/list/profileexec"
    },
    {
      "caption": "Result",
      "routeurl": "/app/list/profileexec"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.params = {
      "typeLabel": "RuleGroup",
      "url": "profile/getProfileExecByProfileGroupExec?",
      "ref": {}
    }
    this._activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this._uuid = param.id;
      this._version = param.version;
      this._mode = param.mode;
      this._type = param.type;
      this.getOneByUuidAndVersion(this._uuid, this._version, this._type)
    });
  }

  getOneByUuidAndVersion(id, version, type) {
    this._commonService.getOneByUuidAndVersion(id, version, type)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.params.id = this._uuid;
    this.params.uuid = this._uuid;
    this.params.name = response.name;
    this.params.elementType = this._type;
    this.params.version = this._version;
    this.params.ref.id = this._uuid;
    this.params.ref.name = response.name;
    this.params.ref.type = this._type;
    this.params.ref.version = this._version;
    if (this._type.slice(-4) == 'Exec' || this._type.slice(-4) == 'exec') {
      if (this._type.slice(-9) == 'groupExec' || this._type.slice(-9) == 'groupexec') {
        this.isgraphShow = true;
      }
      else {
        this.istableShow = true;
        this.isResultTable = true
      }
    }
    if (this.istableShow == true) {
      setTimeout(() => {
        this.params.type = this.appMetadata.getMetadataDefs(this._type.toLowerCase()).name
        this.d_tableRenderComponent.renderTable(this.params);
        this.downloadUuid = this.params.uuid;
        this.downloadVersion = this.params.version;
        this.downloadType = this.params.type;
      }, 1000);
    }
    else {
      this.params.type = this._type;
      this.isgraphShow = true;
    }
  }
  public goBack() {
    this.isResultTable = false
    if (this.istableShow == true) {
      this._location.back();
    }
    else {
      if (this.d_JointjsGroupComponent.IsGraphShow == true) {
        this._location.back();
      }
      else {
        this.d_JointjsGroupComponent.IsGraphShow = true;
      }
    }
  }
  opendownloadResult() {
    if (this.isHomeEnable == false) {
      this.isDownloadModel = true;
    }
  }
  downloadProfileResult() {
    this.downloadResult()
  }

  onSuccessgetNumRowsbyExec(response) {
    this.runMode = response.runMode;
    this.downloadResult();
  }

  downloadResult() {
    const headers = new Headers();
    this.http.get(this.baseUrl + '/profile/download?action=view&profileExecUUID=' + this.downloadUuid + '&profileExecVersion=' + this.downloadVersion + '&mode=' + this.runMode + '&row=' + this.download["rows"] + '&formate=' + this.download["selectFormat"],
      { headers: headers, responseType: ResponseContentType.Blob })
      .toPromise()
      .then(response => this.saveToFileSystem(response));
  }

  saveToFileSystem(response) {
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
    this.isDownloadModel = false
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showKnowledgeGraph = false;
    if (this._type == this.metaType.PROFILEEXEC) {
      setTimeout(() => {
        this.params.type = this.appMetadata.getMetadataDefs(this._type.toLowerCase()).name
        this.d_tableRenderComponent.renderTable(this.params);
        this.downloadUuid = this.params.uuid;
        this.downloadVersion = this.params.version;
        this.downloadType = this.params.type;
      }, 1000);
    }
    this.isResultTable = true

  }

  showDagGraph(uuid, version, graphFlag) {
    if (graphFlag) {
      this.isHomeEnable = true;
      this.showKnowledgeGraph = true;
      this.isResultTable = false;
      setTimeout(() => {
        this.d_KnowledgeGraphComponent.getGraphData(this._uuid, this._version);
      }, 1000);
    }
    else {
      if (this._type == this.metaType.PROFILEEXEC) {
        this.showMainPage();
      }
      this.d_JointjsGroupComponent.generateGroupGraph(this.params);
    }
  }

  downloadShow(param: any) {
    this.isResultTable = true;
    console.log(param)
    this.downloadUuid = param.uuid;
    this.downloadVersion = param.version;
    this.downloadType = param.type;
  }

  reGroupExecute() {
    this.restartDialogBox = true;
  }
  submitRestartDialogBox() {
    this._commonListService.restart(this._uuid, this._version, this.metaType.PROFILEGROUPEXEC, "execute")
      .subscribe(
        response => {
          this.msgs = [];
          this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'profile Restarted Successfully' });
          console.log("Success....");
          this.onSuccessrestart(this._uuid, this._version, response)
        },
        error => console.log("Error :: " + error)
      );
    this.restartDialogBox = false;
  }

  onSuccessrestart(uuid, version, response) {
    this.showDagGraph(uuid,version, false)
  }
  cancelRestartDialogBox() {
    this.restartDialogBox = false;
  }

  receiveJointJSRunStatus(status) {
    this.restartStatus = status
    console.log(this.restartStatus);
  }

  close() {
    this.isDownloadModel = false
  }

}




