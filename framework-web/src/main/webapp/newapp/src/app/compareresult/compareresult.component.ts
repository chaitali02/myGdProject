import { MetadataService } from './../metadata/services/metadata.service';
import { DataQualityService } from './../metadata/services/dataQuality.services';
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location, DatePipe } from '@angular/common';
import { AppMetadata } from '../app.metadata';
import { CommonService } from '../metadata/services/common.service';
import { Http } from '@angular/http';
import { AppConfig } from '../app.config';
import { RuleService } from '../metadata/services/rule.service';

@Component({
  selector: 'app-compareresult',
  templateUrl: './compareresult.template.html',
  styleUrls: []
})

export class CompareResultComponent {

  breadcrumbDataFrom: any;
  radioSelectedType: any;
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
  sourceShowProgress: boolean = false;
  isInProgress: boolean = false;
  isSourceTableShow: boolean
  isTargetTableShow: boolean;
  targetShowProgress: boolean;
  type;
  parentType;
  sourceDataMessage: string;
  targetDataMessage: string;
  isSourceDataError: boolean;
  isTargetDataError: boolean;

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

  //for rule
  allNameRuleGroup: any[];
  allNameRule: any[];



  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _dataQualityService: DataQualityService, private _metadataService: MetadataService, private _ruleService: RuleService, private datePipe: DatePipe) {

    this._activatedRoute.params.subscribe((params: Params) => {
      this.type = (params['type']).toLowerCase();
      this.parentType = (params['parentType']);
      this.populateBreadCrumb();
      console.log(this.parentType);
    });

    // for dq
    this.searchForm = {};
    this.allname = [];
    this.allsource = [];
    this.alltarget = [];
    this.sourceShowProgress = false;
    this.isInProgress = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.targetShowProgress = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;

    // for rule
    this.allNameRuleGroup = [];
    this.allNameRule = [];
  }

  populateBreadCrumb(): any {
    if (this.type == 'dq') {
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
      this.getAllLatest(this.type);
    }
    else if (this.type == 'rule') {
      this.breadcrumbDataFrom = [
        {
          "caption": "Business Rules",
          "routeurl": "/app/list/rule"
        },
        {
          "caption": "Compare Results",
          "routeurl": "/app/list/rule"
        }
      ];
      this.getAllLatest(this.type);
      this.getAllLatest(this.type + "group");
    }
  }

  onUpdateStartDate() {
    console.log("start and end date call");
    this.searchForm.endDate = '';
    this.allsource = [];
    this.alltarget = [];
  }

  onRadioBtnChange(type) {
    console.log("Radio button change");
    this.getAllLatest(type);
  }

  getAllLatest(type: any): any {
    this._commonService.getAllLatest(type).subscribe(
      response => { this.onSuccessgetAllLatest(response, type) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response: any[], type: String): any {
    if (type == 'dq') {
      this.allname = [];
      for (const i in response) {
        let ver = {};
        ver["label"] = response[i]['name'];
        ver["value"] = {};
        ver["value"]["label"] = response[i]['name'];
        ver["value"]["uuid"] = response[i]['uuid'];
        this.allname[i] = ver;
      }
    }
    else if (type == 'rulegroup') {
      this.allNameRuleGroup = [];
      for (const i in response) {
        let ver = {};
        ver["label"] = response[i]['name'];
        ver["value"] = {};
        ver["value"]["label"] = response[i]['name'];
        ver["value"]["uuid"] = response[i]['uuid'];
        ver["value"]["version"] = response[i]['version'];
        this.allNameRuleGroup[i] = ver;
      }
    }
    else if (type == 'rule') {
      this.allNameRule = [];
      for (const i in response) {
        let ver = {};
        ver["label"] = response[i]['name'];
        ver["value"] = {};
        ver["value"]["label"] = response[i]['name'];
        ver["value"]["uuid"] = response[i]['uuid'];
        this.allNameRule[i] = ver;
      }
    }

  }

  submitSearchCriteria() {
    this.isInProgress = true;
    this.alltarget = [];
    this.colsTargetdata = [];
    this.colsTarget = [];
    this.allsource = [];
    this.colsSourcedata = [];
    this.colsSource = [];


    let startDate;
    let endDate;
    if (this.searchForm.startDate) {
      let startDateUtc = new Date(this.searchForm.startDate.getUTCFullYear(), this.searchForm.startDate.getUTCMonth(), this.searchForm.startDate.getUTCDate(), this.searchForm.startDate.getUTCHours(), this.searchForm.startDate.getUTCMinutes(), this.searchForm.startDate.getUTCSeconds())
      startDate = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC";
    }
    else {
      startDate = '';
    }

    if (this.searchForm.endDate) {
      let endDateUtc = new Date(this.searchForm.endDate.getUTCFullYear(), this.searchForm.endDate.getUTCMonth(), this.searchForm.endDate.getUTCDate(), this.searchForm.endDate.getUTCHours(), this.searchForm.endDate.getUTCMinutes(), this.searchForm.endDate.getUTCSeconds())
      endDate = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC";
    }
    else {
      endDate = '';
    }
    
    if (this.searchForm.radioSelectedType == 'dq') {
      this._dataQualityService.getDataQualExecByDataqual1(this.searchForm.selectedName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetDataQualExec(response) },
        error => console.log("Error :: " + error));
    }
    else if (this.searchForm.radioSelectedType == 'datapod') {
      this._dataQualityService.getdqExecByDatapod(this.searchForm.selectedName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetDataQualExec(response) },
        error => console.log("Error :: " + error));
    }
    else if (this.type == 'rule') {
      this._ruleService.getRuleExecByRule(this.searchForm.selectedRuleName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetRuleExecByRule(response) },
        error => console.log("Error :: " + error));
    }
    // else if (this.searchForm.radioSelectedType == 'rule') {
    //   this._ruleService.getRuleExecByRule(this.searchForm.selectedName.uuid, startDate, endDate).subscribe(
    //     response => { this.onSuccessgetDataQualExecByDataqual1(response) },
    //     error => console.log("Error :: " + error));
    // }

  }

  onSuccessgetDataQualExec(response: any[]): any {
    this.isInProgress = false;
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
    this.isSourceTableShow = false;
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

    if (this.type == "rule") {
      this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, "ruleexec").subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == "dq") {
      this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, "dqexec").subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
        error => console.log("Error :: " + error));
    }
  }

  onChangeTarget(selectedTarget: any) {
    this.targetShowProgress = true;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;

    if (this.type == "rule") {
      this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, "ruleexec").subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == "dq") {
      this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, "dqexec").subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
        error => console.log("Error :: " + error));
    }
  }

  onSuccessgetNumRowsbyExec(response: any, selectedSourceTarget: any, compareType: String): any {

    if (this.type == "rule") {
      this._ruleService.getResults(selectedSourceTarget.version, selectedSourceTarget.uuid, 0, 100).subscribe(
        response => { this.onSuccessGetSummary(response, compareType) },
        error => console.log("Error :: " + error));
    }
    else if (this.type == "dq") {
      this._dataQualityService.getSummary(selectedSourceTarget.uuid, selectedSourceTarget.version, "dqexec", response.runMode).subscribe(
        response => { this.onSuccessGetSummary(response, compareType) },
        error => {
          if (compareType == 'source') {
            this.sourceShowProgress = false;
            this.isSourceDataError = true;
            this.sourceDataMessage = 'No data available';
          }
          else if(compareType == 'target'){
            this.targetShowProgress = false;
            this.isTargetDataError = true;
            this.targetDataMessage = 'No data available';
          }
          console.log("Error :: " + error)
        });
    }
  }

  onSuccessGetSummary(response: any[], compareType: any): any {
    console.log(response)
    if (compareType == 'source') {
      this.sourceShowProgress = false;
      this.isSourceDataError = false;
      this.isSourceTableShow = true;
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
      this.targetShowProgress = false;
      this.isTargetTableShow = true;
      this.isTargetDataError = false;
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

  onChangeRuleGroup(selectedRuleName) {
    this._commonService.getOneByUuidAndVersion(selectedRuleName.uuid, selectedRuleName.version, "rulegroup").subscribe(
      response => { this.onSuccessgetOneByUuidAndVersion(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response: any): any {
    console.log("onSuccessgetOneByUuidAndVersion call...");
    var b = response;
  }

  onSuccessgetRuleExecByRule(response: any[]): any {
    this.isInProgress = false;
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

}