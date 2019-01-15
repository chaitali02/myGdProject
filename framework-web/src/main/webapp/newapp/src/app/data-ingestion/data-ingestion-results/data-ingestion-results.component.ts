import { Component, OnInit, ViewChild } from '@angular/core';
import { Params, ActivatedRoute, Router } from '@angular/router';
import { AppConfig } from '../../app.config';
import { Http, Headers} from '@angular/http';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { JointjsGroupComponent } from '../../shared/components/jointjsgroup/jointjsgroup.component';

import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-data-ingestion-results',
  templateUrl: './data-ingestion-results.component.html'
})
export class DataIngestionResultsComponent implements OnInit {
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
  isgraphShow: boolean;
  params: any;
  _type: any;
  _mode: any;
  _version: any;
  _uuid: any;
  @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService) {
    this.baseUrl = _config.getBaseUrl();
    this.showKnowledgeGraph = false;
    this.numRows = 100;
    this.isHomeEnable = false;
    this.displayDialogBox = false
    this.isgraphShow = false;
    this.breadcrumbDataFrom = [
      {
        "caption": "Data Ingestion ",
        "routeurl": "/app/list/ingestexec"
      },
      {
        "caption": "Result ",
        "routeurl": "/app/list/ingestexec"
      },
      {
        "caption": "",
        "routeurl": null
      }
    ]

    this.downloadFormatArray = [
      {"value" : "excel", "label" : "excel"}]
    this.params = {
      "typeLabel": "RuleGroup",
      "url": "ingest/getIngestExecByRGExec?",
      "ref": {}
    }
  }

  ngOnInit() {
    this._activatedRoute.params.subscribe((params: Params) => {
      this._uuid = params['id'];
      this._version = params['version'];
      this._mode = params['mode'];
      this._type = params['type'];
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
    }
    this.params["type"] = this._type;
  }
  public goBack() {
    this._location.back();
  }

  showMainPage() {
    this.isHomeEnable = false;
    this.showKnowledgeGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showKnowledgeGraph = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }
}
