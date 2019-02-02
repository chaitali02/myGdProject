import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { ModelService } from '../../metadata/services/model.service';
import { CommonListService } from './../../common-list/common-list.service';
import { saveAs } from 'file-saver';
//import 'rxjs/add/operator/toPromise';
import { Http, Headers } from '@angular/http';
import { AppConfig } from '../../app.config';
import { ResponseContentType } from '@angular/http';
import { DataScienceResultService } from '../../metadata/services/dataScienceResult.service';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
//import { NgxJsonViewerModule } from 'ngx-json-viewer';
//import JSONFormatter from 'json-formatter-js';
@Component({
  selector: 'app-results',
  templateUrl: './resultDetails.component.html',
  styleUrls: ['./resultDetails.component.css'],

})
export class ResultDetailsComponent {
  modelData: any;
  blob: Blob;
  filename: string;
  pmmlData: any;
  tableHeading: string;
  cols: any[];
  columnOptions: any[];
  colsdata: any;
  IsError: boolean;
  IsTableShow: boolean;
  showDiv: boolean;
  ppml: boolean
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  type: any;
  uuid: any;
  version: any;
  mode: any;
  istableShow: boolean;
  params: any;
  modelResult: any;
  id: any;
  uid: any;
  baseUrl: any;
  showGraph: boolean;
  isHomeEnable: boolean;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  constructor(private http: Http, private config: AppConfig, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService, private _resultService: DataScienceResultService, private _commonListService: CommonListService) {
    
    this.showGraph = false;
    this.isHomeEnable = false;
    this.baseUrl = config.getBaseUrl();

    this.modelResult = {};
    this.breadcrumbDataFrom = [{
      "caption": "Data Science ",
      "routeurl": "/app/dataScience/results"
    },
    // {
    //   "caption":"Results",
    //   "routeurl":"/app/list/modelexec"
    // },
    {
      "caption": "Model Results",
      "routeurl": null
    }
    ]

    this._activatedRoute.params.subscribe((params: Params) => {
      this.version = params['version'];
      this.mode = params['mode'];
      this.type = params['type'];
      this.id = params['id'];
    });
    if (this.type == "training") {
      this.getModelByTrainExec();
      this.getTrainResults();
      this.tableHeading = "Training Result";
      this.showDiv = true;
      this.ppml = false;
    }
    else if (this.type == "prediction") {
      this.getPredictResults();
      this.showDiv = false
      this.tableHeading = "Prediction Result";
    }
    else {
      this.getSimulateResults();
      this.showDiv = false
      this.tableHeading = "Simulation Result";
    }
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
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getModelByTrainExec() {
    this._resultService.getModelByTrainExec(this.id, this.version).subscribe(
      response => { this.onSuccessgetModelByTrainExec(response) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetModelByTrainExec(response) {
    this.modelData = response;
  }

  savePng() {
    const headers = new Headers();
    headers.append('Accept', 'text/plain');
    if (this.type == "predict") {
      this.http.get(this.baseUrl + '/model/predict/download?action=view&predictExecUUID=' + this.id + '&predictExecVersion=' + this.version + '&mode=BATCH',
        { headers: headers, responseType: ResponseContentType.Blob })
        .toPromise()
        .then(response => this.saveToFileSystem(response));
    }
    else if (this.type == "simulate") {
      this.http.get(this.baseUrl + '/model/simulate/download?action=view&simulateExecUUID=' + this.id + '&simulateExecVersion=' + this.version + '&mode=" "',
        { headers: headers, responseType: ResponseContentType.Blob })
        .toPromise()
        .then(response => this.saveToFileSystem(response));
    }
    else if (this.type == "training") {
      if (this.modelData.customFlag == 'N') {
        let a = this.downloadTrainData();
        return;
      }
      this.http.get(this.baseUrl + '/model/train/download?action=view&trainExecUUID=' + this.id + '&trainExecVersion=' + this.version + '&mode=" "',
        { headers: headers, responseType: ResponseContentType.Blob })
        .toPromise()
        .then(response => this.saveToFileSystem(response));
    }
  }

  private saveToFileSystem(response) {
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
  }

  downloadTrainData() {
    let filename = this.id + ".txt";
    console.log(JSON.stringify(this.modelResult));
    console.log(this.modelResult);
    const blob = new Blob([JSON.stringify(this.modelResult)], { type: 'text/xml' });
    saveAs(blob, filename);
  }

  showPMMLResult() {
    this.ppml = true;
    var headers = new Headers();
    headers.append('Accept', 'text/plain');
    this.http.get(this.baseUrl + "model/download?modelExecUUID=" + this.id + "&modelExecVersion=" + this.version,
      { headers: headers })
      .subscribe(response => this.getPmmlResults(response))
  }

  getPmmlResults(response) {
    console.log(response["_body"])
    this.pmmlData = response["_body"];
  }

  getTrainResults() {
    this._resultService.getTrainResults(this.id, this.version)
      .subscribe(
        response => {
          this.onSuccessgetModelResults(response)
        },
        error => console.log("Error :: " + error));
  }
  getPredictResults() {
    this._resultService.getPredictResults(this.id, this.version)
      .subscribe(
        response => {
          this.onSuccessgetPredictResults(response)
        },
        error => {
          this.IsTableShow = true;
          console.log("Error :: " + error)
          this.IsError = true;
        });
  }
  getSimulateResults() {
    this._resultService.getSimulateResults(this.id, this.version)
      .subscribe(
        response => {
          this.onSuccessgetPredictResults(response)
        },
        error => {
          this.IsTableShow = true;
          console.log("Error :: " + error)
          this.IsError = true;
        });

  }
  onSuccessgetModelResults(response) {
    this.modelResult = response;
  }
  onSuccessgetPredictResults(response) {
    this.IsTableShow = true;
    this.colsdata = response
    let columns = [];
    console.log(response)
    if (response.length && response.length > 0) {
      Object.keys(response[0]).forEach(val => {
        if (val != "rownum") {
          let width = ((val.split("").length * 9) + 20) + "px"
          columns.push({ "field": val, "header": val, colwidth: width });
        }
      });
    }

    this.cols = columns
    this.columnOptions = [];
    for (let i = 0; i < this.cols.length; i++) {
      this.columnOptions.push({ label: this.cols[i].header, value: this.cols[i] });
    }
  }
  public goBack() {
    this._location.back();
  }
}
