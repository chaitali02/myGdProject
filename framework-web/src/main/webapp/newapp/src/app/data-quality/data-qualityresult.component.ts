import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';

import { AppMetadata } from '../app.metadata';
import { TableRenderComponent } from '../shared/components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../metadata/services/common.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../app.config';
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { GraphParamIO } from '../metadata/domainIO/domain.graphParamIO';
import { DataQualityService } from '../metadata/services/dataQuality.services';
import { CommonListService } from '../common-list/common-list.service';

@Component({
  selector: 'app-dataqualityresult',
  templateUrl: './data-qualityresult.template.html',
  styleUrls: []
})
export class DataQualityResultComponent {
  isResultTable: boolean;
  //showHome: boolean;
  showKnowledgeGraph: boolean;
  isHomeEnable: boolean;
  numRows: any;
  downloadFormatArray: any[];
  displayDialogBox: boolean;
  runMode: any;
  baseUrl: any;
  typeJointJs: any;
  versionJointJs: any;
  uuidJointJs: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  _type: any;
  _uuid: any;
  _version: any;
  _mode: any;
  istableShow: boolean;
  isgraphShow: boolean;
  graphParams: any
  index: 0;

  @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
  @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isGraphInprogess: boolean;
  isGraphError: boolean;
  restartDialogBox: boolean;
  msgs: Message[] = [];

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute,
    private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService, private _dataQualityService: DataQualityService,
    private _commonListService: CommonListService, ) {

    this.graphParams = new GraphParamIO();
    this.baseUrl = _config.getBaseUrl();
    this.showKnowledgeGraph = false;
    this.numRows = 100;
    this.isHomeEnable = false;
    this.displayDialogBox = false
    this.isgraphShow = false;
    this.istableShow = false;
    this.breadcrumbDataFrom = [
      {
        "caption": "Data Quality ",
        "routeurl": "/app/list/dqexec"
      },
      {
        "caption": "Result ",
        "routeurl": "/app/list/dqexec"
      },
      {
        "caption": "",
        "routeurl": null
      }
    ]
    this.downloadFormatArray = [
      { "value": "excel", "label": "excel" }
    ];
    this.graphParams = {
      "typeLabel": "RuleGroup",
      "url": "dataqual/getdqExecBydqGroupExec?",
      "ref": {}
    };
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
    this.graphParams.id = this._uuid;
    this.graphParams.uuid = this._uuid;
    this.graphParams.name = response.name;
    this.graphParams.elementType = this._type;
    this.graphParams.version = this._version;
    this.graphParams.ref.id = this._uuid;
    this.graphParams.ref.name = response.name;
    this.graphParams.ref.type = this._type;
    this.graphParams.ref.version = this._version;
    if (this._type.slice(-4) == 'Exec' || this._type.slice(-4) == 'exec') {
      if (this._type.slice(-9) == 'groupExec' || this._type.slice(-9) == 'groupexec') {
        this.isgraphShow = true;
      }
      else {
        this.istableShow = true;
        this.isResultTable = true;
      }
    }
    if (this.istableShow == true) {
      setTimeout(() => {
        this.graphParams.type = this.appMetadata.getMetadataDefs(this._type.toLowerCase()).name;
        this.d_tableRenderComponent.renderTable(this.graphParams);
      }, 1000);
    }
    else {
      this.graphParams.type = this._type;
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

  downloadResult() {
    if (this.isHomeEnable == false) {
      this.displayDialogBox = true;
    }
  }

  submitDialogBox() {
    this.displayDialogBox = false;
    this.uuidJointJs = this.d_tableRenderComponent.uuid;
    this.versionJointJs = this.d_tableRenderComponent.version;
    this.typeJointJs = this.d_tableRenderComponent.type;

    const headers = new Headers();
    this.http.get(this.baseUrl + '/dataqual/download?action=view&dataQualExecUUID=' + this.uuidJointJs + '&dataQualExecVersion=' + this.versionJointJs + '&rows=' + this.numRows,
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
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showKnowledgeGraph = false;
    if (this._type == 'dqexec') {
      setTimeout(() => {
        this.graphParams.type = this.appMetadata.getMetadataDefs(this._type.toLowerCase()).name;
        this.d_tableRenderComponent.renderTable(this.graphParams);
      }, 1000);
    }
  }

  showDagGraph(uuid, version, graphFlag) {
    if (graphFlag) {
      this.isHomeEnable = true;
      this.showKnowledgeGraph = true;
      setTimeout(() => {
        this.d_KnowledgeGraphComponent.getGraphData(this._uuid, this._version);
        this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
        this.isGraphError = this.d_KnowledgeGraphComponent.isError;
      }, 1000);
    }
    else {
      if (this._type == 'dqexec') {
        this.showMainPage();
      }
      this.d_JointjsGroupComponent.generateGroupGraph(this.graphParams);
    }
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  downloadShow(param: any) {
    this.isResultTable = true;
    console.log(param)
    this.uuidJointJs = param.uuid;
    this.versionJointJs = param.version;
    this.typeJointJs = param.type;
  }

  reGroupExecute() {
    this.restartDialogBox = true;
  }

  submitRestartDialogBox() {
    debugger
    console.log("submitRestartDialogBox() call...");
    this._commonListService.restart(this._uuid, this._version, "dqgroupExec", "execute")
      .subscribe(
        response => {
          // this.getBaseEntityByCriteria()
          // this.isModel = "false";
          this.msgs = [];
          this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'dqx Restarted Successfully' });
          console.log("Success....");
        },
        error => console.log("Error :: " + error)
      );
    this.restartDialogBox = false;
  }

  cancelRestartDialogBox() {
    this.restartDialogBox = false;
  }

}



