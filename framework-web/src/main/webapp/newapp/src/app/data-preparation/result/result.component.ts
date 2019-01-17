import { Component,Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { TableRenderComponent } from '../../shared/components/resulttable/resulttable.component';
import { Location } from '@angular/common';

import { saveAs } from 'file-saver';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html'
})
export class ResultComponent implements OnInit {
  params: any[];
  baseUrl: string;
  typeJointJs: any;
  versionJointJs: any;
  uuidJointJs: any;
  _type: any;
  _mode: any;
  _version: any;
  _uuid: any;
  downloadFormatArray: { "value": string; "label": string; }[];
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  istableShow: boolean;
  isgraphShow: boolean;
  displayDialogBox: boolean;
  isHomeEnable: boolean;
  numRows: number;
  showKnowledgeGraph: boolean;

  @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;
  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService) {
    this.baseUrl = _config.getBaseUrl();
    this.showKnowledgeGraph = false;
    this.numRows = 100;
    this.isHomeEnable = false;
    this.displayDialogBox = false
    this.isgraphShow = false;
    this.istableShow = false;
    this.params = []
    this.breadcrumbDataFrom = [
      {
        "caption": "Data Preparation ",
        "routeurl": "/app/list/mapexec"
      },
      {
        "caption": "Result ",
        "routeurl": "/app/list/mapexec"
      },
      {
        "caption": "",
        "routeurl": null
      }
    ]
    this.downloadFormatArray = [
      {"value" : "excel", "label" : "excel"}]
   }

  ngOnInit() {
    this._activatedRoute.params.subscribe((params: Params) => {
      this._uuid = params['id'];
      this._version = params['version'];
      this._mode = params['mode'];
      //this._type = params['type'];
      this.getOneByUuidAndVersion(this._uuid, this._version, "mapexec")
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
    this.params["id"] = response.id;
    this.params["uuid"] = response.uuid;
    this.params["name"] = response.name;
    this.params["elementType"] = "mapexec";
    this.params["type"] = "map";
    this.params["version"] = response.version;
    this.istableShow = true;
    // if (this.isHomeEnable == true){
      setTimeout(() => {
        this.d_tableRenderComponent.renderTable(this.params);
      }, 1000)
    // }
       
    }
  public goBack() {
      this._location.back();
    }

  downloadResult(){
    if(this.isHomeEnable == false){
      this.displayDialogBox = true;
    }
  }

  submitDialogBox() {
    this.displayDialogBox = false;
    this.uuidJointJs = this.d_tableRenderComponent.uuid;
    this.versionJointJs = this.d_tableRenderComponent.version;
    this.typeJointJs = this.d_tableRenderComponent.type;

    const headers = new Headers();
    this.http.get(this.baseUrl + '/map/download?action=view&mapExecUUID=' + this.uuidJointJs + '&mapExecVersion=' + this.versionJointJs + '&rows='+this.numRows,
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

  showMainPage(){
    //this.istableShow = true;
    this.isHomeEnable = false;
   this.showKnowledgeGraph = false;
  }

  showDagGraph(uuid,version){
    //this.istableShow = false;
    this.isHomeEnable = true;
    this.showKnowledgeGraph = true;
  }

  cancelDialogBox(){
    this.displayDialogBox = false;
  }

}
