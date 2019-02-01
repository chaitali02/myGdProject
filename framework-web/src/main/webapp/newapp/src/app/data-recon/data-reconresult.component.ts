
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';

import { AppMetadata } from '../app.metadata';
// import{JointjsGroupComponent } from './jointjsgroup.component'
import { TableRenderComponent } from '../shared/components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../metadata/services/common.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../app.config';
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
@Component({
  selector: 'app-recon',
  templateUrl: './data-reconresult.template.html',
  styleUrls: []
})

export class DataReconresultComponent {
  isResultTable: boolean;
  download: {};
  isDownloadModel: boolean;
  showKnowledgeGraph: boolean;
  isHomeEnable: boolean;
  showHome: boolean;
  runMode: any;

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

  showGraph: boolean;

  @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
  @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private http: Http, private _config: AppConfig, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService) {
    
    this.showGraph = false;
    this.baseUrl = _config.getBaseUrl();
    this.isgraphShow = false;
    this.istableShow = false;
    this.download = {}
    this.download["format"] = ["excel"]
    this.download["rows"] = 100
    this.download["selectFormat"] = 'excel'
    this.breadcrumbDataFrom = [{
      "caption": " Data Reconciliation",
      "routeurl": "/app/list/recon"
    },
    {
      "caption": "Result",
      "routeurl": "/app/list/reconexec"
    },
    // {
    //   "caption":"Result",
    //   "routeurl":"list/profileexec"
    // },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    //http://localhost:8080/recon/getReconExecByRGExec?reconGroupExecUuid=8282bdc2-e583-4e5a-a67e-e5bb491f7134&reconGroupExecVersion=1536399849&action=view
    this.params = {
      "typeLabel": "RuleGroup",
      "url": "recon/getReconExecByRGExec?",
      "ref": {}
    }
    this._activatedRoute.params.subscribe((params: Params) => {
      this._uuid = params['id'];
      this._version = params['version'];
      this._mode = params['mode'];
      this._type = params['type'];
      this.getOneByUuidAndVersion(this._uuid, this._version, this._type)
    });

  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }
  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this._uuid, this._version);
    }, 2000);
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
    this.params["id"] = this._uuid;
    this.params["uuid"] = this._uuid;
    this.params["name"] = response.name;
    this.params["elementType"] = this._type;
    this.params["version"] = this._version;
    this.params.ref["id"] = this._uuid;
    this.params.ref["name"] = response.name;
    this.params.ref["type"] = this._type;
    this.params.ref["version"] = this._version;
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
        this.params["type"] = this.appMetadata.getMetadataDefs(this._type.toLowerCase())['name']
        this.d_tableRenderComponent.renderTable(this.params);
        this.downloadUuid = this.params.uuid;
        this.downloadVersion = this.params.version;
        this.downloadType = this.params.type;
      }, 1000);
    }
    else {
      this.params["type"] = this._type;
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
    this.isDownloadModel = true
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
    this.http.get(this.baseUrl + '/recon/download?action=view&reconExecUUID=' + this.downloadUuid + '&reconExecVersion=' + this.downloadVersion + '&mode=' + this.runMode + '&row=' + this.download["rows"] + '&formate=' + this.download["selectFormat"],
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
  // showMainPage() {
  //   this.showHome = true
  //   this.isHomeEnable = false
  //   // this._location.back();
  //   this.showKnowledgeGraph = false;
  // }

  // showGraph(uuid,version){
  //   this.showHome=false
  //   this.isHomeEnable = true;
  //   this.showKnowledgeGraph = true;
  // }
  downloadShow(param: any) {

    this.isResultTable = true;
    console.log(param)
    this.downloadUuid = param.uuid;
    this.downloadVersion = param.version;
    this.downloadType = param.type;
  }
  close() {
    this.isDownloadModel = false
  }
}




