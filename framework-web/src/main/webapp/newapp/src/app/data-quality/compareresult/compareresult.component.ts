import { MetadataService } from './../../metadata/services/metadata.service';
import { DataQualityService } from './../../metadata/services/dataQuality.services';
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location, DatePipe } from '@angular/common';

import { AppMetadata } from '../../app.metadata';
import { TableRenderComponent } from '../../shared/components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../../metadata/services/common.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../../app.config';
@Component({
  selector: 'app-compareresult',
  templateUrl: './compareresult.template.html',
  styleUrls: []
})

export class CompareResultComponent {

  breadcrumbDataFrom: any;
  selectedType: any;
  startDate: Date;
  endDate: Date;
  allname: any[];
  allsource: any[];
  alltarget: any[];
  searchForm: any;
  selectedTarget: any;
  colsSourcedata: any;
  colsSource: any;
  colsTargetdata: any[];
  colsTarget: any[];
  alltargetTemp: any;
  sourceShowProgress: boolean;

  types = [
    {
      "value": "dq",
      "caption": "Rule"
    },
    {
      "value": "datapod",
      "caption": "Datapod"
    }
  ];

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService, private _dataQualityService: DataQualityService, private _metadataService: MetadataService, private datePipe: DatePipe) {
    this.breadcrumbDataFrom = [
      {
        "caption": "Data Quality",
        "routeurl": "/app/list/batch"
      },
      {
        "caption": "Compare Results",
        "routeurl": "/app/list/batch"
      }
    ];

    this.getAllLatest("dq");
    this.searchForm = {};
    this.allname = [];
    this.allsource = [];
    this.alltarget = [];
    this.sourceShowProgress = false;
  }

  onUpdateStartDate() {
    console.log("start and end date call");

  }

  onRadioBtnChange(type) {
    console.log("Radio button change");
    this.getAllLatest(type);

  }

  getAllLatest(type: any): any {
    this._commonService.getAllLatest(type).subscribe(
      response => {
        this.onSuccessgetAllLatest(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response: any[]): any {

    this.allname = [];
    //this.isSelect = true;
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      this.allname[i] = ver;
    }
  }

  submitSearchCriteria() {
    console.log("submit serach crit" + this.searchForm);
    let uuid = this.searchForm.selectedName.uuid;
    let startDate;
    let endDate;

    if (this.searchForm.startDate) {
      startDate = this.searchForm.startDate;
    }
    else {
      startDate = '';
    }

    if (this.searchForm.endDate) {
      endDate = this.searchForm.endDate;
    }
    else {
      endDate = '';
    }

    if (this.searchForm.selectedType == 'dq') {
      this._dataQualityService.getDataQualExecByDataqual1(uuid, startDate, endDate).subscribe(
        response => {
          this.onSuccessgetDataQualExecByDataqual1(response)
        },
        error => console.log("Error :: " + error));
    }
    else if (this.searchForm.selectedType == 'datapod') {
      this._dataQualityService.getdqExecByDatapod(uuid, startDate, endDate).subscribe(
        response => {
          this.onSuccessgetDataQualExecByDataqual1(response)
        },
        error => console.log("Error :: " + error));
    }

  }

  onSuccessgetDataQualExecByDataqual1(response: any[]): any {
    this.alltargetTemp = response;
    this.allsource = [];
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['createdOn'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['createdOn'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      this.allsource[i] = ver;
    }
    this.alltarget = [];
  }

  onChangeSource(selectedSource: any) {
    this.sourceShowProgress = true;
    this.alltarget = [];
    this.colsTargetdata = [];
    this.colsTarget = [];
    for (const i in this.alltargetTemp) {
      if (this.alltargetTemp[i]['uuid'] !== selectedSource.uuid) {
        let ver = {};
        ver["label"] = this.alltargetTemp[i]['createdOn'];
        ver["value"] = {};
        ver["value"]["label"] = this.alltargetTemp[i]['createdOn'];
        ver["value"]["uuid"] = this.alltargetTemp[i]['uuid'];
        ver["value"]["version"] = this.alltargetTemp[i]['version'];
        this.alltarget.push(ver);
      }
    }
    //console.log(JSON.stringify(this.alltarget));
    this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, "dqexec").subscribe(
      response => {
        this.onSuccessgetNumRowsbyExec(response, selectedSource, "source")
      },
      error => console.log("Error :: " + error));
  }
  onChangeTarget(selectedTarget: any) {
    this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, "dqexec").subscribe(
      response => {
        this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target")
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetNumRowsbyExec(response: any, selectedSourceTarget: any, compareType: String): any {
    // this.getSummary(selectedSourceTarget.uuid, selectedSourceTarget.version, compareType, response.runMode);
    this._dataQualityService.getSummary(selectedSourceTarget.uuid, selectedSourceTarget.version, "dqexec", response.runMode).subscribe(
      response => {
        this.onSuccessGetSummary(response, compareType)
      },
      error => console.log("Error :: " + error));
  }

  getSummary(uuid, version, compareType, mode) {
    // this._dataQualityService.getSummary(uuid, version, "dqexec", mode).subscribe(
    //   response => {
    //     this.onSuccessGetSummary(response, compareType)
    //   },
    //   error => console.log("Error :: " + error));
  }

  onSuccessGetSummary(response: any[], compareType: any): any {
    console.log(response)

    if (compareType == 'source') {
      this.colsSourcedata = response;
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
      this.colsSource = columns
    }

    else if (compareType == 'target') {
      this.colsTargetdata = response;
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
      this.colsTarget = columns
    }
  }


}