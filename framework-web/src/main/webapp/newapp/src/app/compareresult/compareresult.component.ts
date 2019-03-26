import { DropDownIO } from './../metadata/domainIO/domain.dropDownIO';
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
import { DataReconService } from '../metadata/services/dataRecon.services';
import * as MetaTypeEnum from '../metadata/enums/metaType';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { AttributeIO } from '../metadata/domainIO/domain.attributeIO';
import { CompareResult } from '../metadata/domain/domain.compareResult';
import { RuleGroup } from '../metadata/domain/domain.ruleGroup';

@Component({
  selector: 'app-compareresult',
  templateUrl: './compareresult.template.html',
  styleUrls: []
})

export class CompareResultComponent {

  breadcrumbDataFrom: any;
  startDate: Date;
  endDate: Date;
  allNameDq: any[];
  allsource: any[];
  alltarget: any[];
  searchForm: any = {};
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
  sourceDataMessage: string;
  targetDataMessage: string;
  isSourceDataError: boolean;
  isTargetDataError: boolean;
  isSubmitDisable: boolean;
  selectedSource: any;
  selectedTypeRadio: any;

  types = [
    {
      "value": "dq",
      "caption": "Rule"
    },
    {
      "value": "datapod",
      "caption": "Datapod"
    },
    {
      "value": "recon",
      "caption": "Rule"
    }
  ];

  //for rule
  allNameRuleGroup: any[];
  allNameRule: any[];

  //for recon
  allNameRecon: any[];

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _dataQualityService: DataQualityService, private _metadataService: MetadataService, private _ruleService: RuleService, private _reconService: DataReconService, private datePipe: DatePipe) {

    this._activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.type = (param.type).toLowerCase();
      this.populateBreadCrumb();
      this.isSubmitDisable = true;
    });

    // for dq
    this.searchForm = {};
    this.allNameDq = [];
    this.allsource = [];
    this.alltarget = [];
    this.sourceShowProgress = false;
    this.isInProgress = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.targetShowProgress = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;
    this.selectedSource = '';

    // for rule
    this.allNameRuleGroup = [];
    this.allNameRule = [];

    //for recon
    this.allNameRecon = [];


  }

  populateBreadCrumb(): any {
    if (this.type == MetaTypeEnum.MetaType.DQ) {
      this.breadcrumbDataFrom = [
        {
          "caption": "Data Quality",
          "routeurl": "/app/list/dq"
        },
        {
          "caption": "Compare Results",
          "routeurl": "/app/list/dq"
        }
      ];
      this.searchForm.selectedTypeRadio = this.type;
      this.getAllLatest(MetaTypeEnum.MetaType.DQ);
    }
    else if (this.type == MetaTypeEnum.MetaType.RULE) {
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
      this.getAllLatest(MetaTypeEnum.MetaType.RULE);
      this.getAllLatest(MetaTypeEnum.MetaType.RULEGROUP);
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this.breadcrumbDataFrom = [
        {
          "caption": "Data Reconcilliation",
          "routeurl": "/app/list/recon"
        },
        {
          "caption": "Compare Results",
          "routeurl": "/app/list/recon"
        }
      ];
      this.getAllLatest(MetaTypeEnum.MetaType.RECON);
    }
  }

  onUpdateStartDate() {
    this.searchForm.endDate = '';
    this.isSubmitDisable = false;
    this.allsource = [];
    this.alltarget = [];
    this.isSourceTableShow = false;
    this.isSourceDataError = false;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;
  }
  onUpdateEndDate() {
    this.isSubmitDisable = false;
    this.allsource = [];
    this.alltarget = [];
    this.isSourceTableShow = false;
    this.isSourceDataError = false;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;
  }

  onRadioBtnChange(type) {
    this.searchForm.selectedTypeRadio = type;
    this.allNameDq = [];
    this.getAllLatest(type);
  }

  getAllLatest(type: any): any {
    this._commonService.getAllLatest(type).subscribe(
      response => { this.onSuccessgetAllLatest(response, type) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response: AttributeIO[], type: String): any {
    if (type == MetaTypeEnum.MetaType.DQ || type == MetaTypeEnum.MetaType.DATAPOD) {
      this.allNameDq = [];
      // this.allNameDq = this.fillArrayName(response);
      for (const i in response) {
        let ver = new DropDownIO();
        response.sort((a, b) => a.name.localeCompare(b.name.toString()));
        ver.label = response[i].name;
        ver.value = { label: String, uuid: String, version: String };
        ver.value.label = response[i].name;
        ver.value.uuid = response[i].uuid;
        ver.value.version = response[i].version;
        this.allNameDq[i] = ver;
      }
    }

    else if (type == MetaTypeEnum.MetaType.RULEGROUP) {
      this.allNameRuleGroup = [];
      // this.allNameRuleGroup = this.fillArrayName(response);
      for (const i in response) {
        let ver = new DropDownIO();
        ver.label = response[i].name;
        ver.value = { label: String, uuid: String, version: String };
        ver.value.label = response[i].name;
        ver.value.uuid = response[i].uuid;
        ver.value.version = response[i].version;
        this.allNameRuleGroup[i] = ver;
      }
    }
    else if (type == MetaTypeEnum.MetaType.RULE) {
      this.allNameRule = [];
      // this.allNameRule = this.fillArrayName(response);
      for (const i in response) {
        let ver = new DropDownIO();
        ver.label = response[i].name;
        ver.value = { label: String, uuid: String, version: String };
        ver.value.label = response[i].name;
        ver.value.uuid = response[i].uuid;
        ver.value.version = response[i].version;
        this.allNameRule[i] = ver;
      }
    }
    else if (type == MetaTypeEnum.MetaType.RECON) {
      this.allNameRecon = [];
      // this.allNameRecon = this.fillArrayName(response);
      for (const i in response) {
        let ver = new DropDownIO();
        ver.label = response[i].name;
        ver.value = { label: String, uuid: String, version: String };
        ver.value.label = response[i].name;
        ver.value.uuid = response[i].uuid;
        ver.value.version = response[i].version;
        this.allNameRecon[i] = ver;
      }
    }
  }
  fillArrayName(response) {
    let mainArray = [];
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].name;
      ver.value = { label: String, uuid: String, version: String };
      ver.value.label = response[i].name;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      mainArray[i] = ver;
    }
    return mainArray;
  }
  fillArrayCreatedOn(response) {
    let mainArray = [];
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].createdOn;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response[i].createdOn;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      mainArray[i] = ver;
    }
    return mainArray;
  }
  submitSearchCriteria() {
    this.isInProgress = true;
    this.alltarget = [];
    this.colsTargetdata = [];
    this.colsTarget = [];
    this.allsource = [];
    this.colsSourcedata = [];
    this.colsSource = [];
    this.isSubmitDisable = true;

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

    if (this.type == MetaTypeEnum.MetaType.DQ) {
      this._dataQualityService.getDataQualExecByDataqual1(this.searchForm.selectedName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetDataQualExec(response) },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.DATAPOD) {
      this._dataQualityService.getdqExecByDatapod(this.searchForm.selectedName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetDataQualExec(response) },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.RULE) {
      this._ruleService.getRuleExecByRule(this.searchForm.selectedRuleName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetRuleExecByRule(response) },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this._reconService.getReconExecByRecon(this.searchForm.selectedReconName.uuid, startDate, endDate).subscribe(
        response => { this.onSuccessgetReconExecByRecon(response) },
        error => console.log("Error :: " + error));
    }

  }
  onSuccessgetReconExecByRecon(response: CompareResult[]): any {
    this.isInProgress = false;
    this.alltargetTemp = response;
    this.allsource = [];
    // this.allsource = this.fillArrayCreatedOn(response);
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].createdOn;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response[i].createdOn;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      this.allsource[i] = ver;
    }
    this.alltarget = [];
  }

  onSuccessgetDataQualExec(response: CompareResult[]): any {
    this.isInProgress = false;
    this.alltargetTemp = response;
    this.allsource = [];
    // this.allsource = this.fillArrayCreatedOn(response);
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].createdOn;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response[i].createdOn;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      this.allsource[i] = ver;
    }
    this.alltarget = [];
  }

  onChangeSource(selectedSource: any) {
    this.sourceShowProgress = true;
    this.isSourceTableShow = false;
    this.selectedTarget = ''
    this.isTargetTableShow = false;
    this.isTargetDataError = false;
    this.alltarget = [];
    for (const i in this.alltargetTemp) {
      if (this.alltargetTemp[i].uuid !== selectedSource.uuid) {
        let ver = new DropDownIO();
        ver.label = this.alltargetTemp[i].createdOn;
        ver.value = { label: String, uuid: String, version: "" };
        ver.value.label = this.alltargetTemp[i].createdOn;
        ver.value.uuid = this.alltargetTemp[i].uuid;
        ver.value.version = this.alltargetTemp[i].version;
        this.alltarget.push(ver);
      }
    }

    if (this.type == MetaTypeEnum.MetaType.RULE) {
      this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, MetaTypeEnum.MetaType.RULEEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.DQ) {
      this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, MetaTypeEnum.MetaType.DQEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, MetaTypeEnum.MetaType.RECONEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
        error => console.log("Error :: " + error));
    }
  }

  onChangeTarget(selectedTarget: any) {
    this.targetShowProgress = true;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;

    if (this.type == MetaTypeEnum.MetaType.RULE) {
      this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, MetaTypeEnum.MetaType.RULEEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.DQ) {
      this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, MetaTypeEnum.MetaType.DQEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
        error => console.log("Error :: " + error));
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, MetaTypeEnum.MetaType.RECONEXEC).subscribe(
        response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
        error => console.log("Error :: " + error));
    }
  }

  onSuccessgetNumRowsbyExec(response: any, selectedSourceTarget: any, compareType: String): any {

    if (this.type == MetaTypeEnum.MetaType.RULE) {
      this._ruleService.getResults(selectedSourceTarget.version, selectedSourceTarget.uuid, 0, 100).subscribe(
        response => { this.onSuccessGetSummary(response, compareType) },
        error => {
          if (compareType == 'source') {
            this.sourceShowProgress = false;
            this.isSourceDataError = true;
            this.sourceDataMessage = 'No data available';
          }
          else if (compareType == 'target') {
            this.targetShowProgress = false;
            this.isTargetDataError = true;
            this.targetDataMessage = 'No data available';
          }
          console.log("Error :: " + error)
        });
    }
    else if (this.type == MetaTypeEnum.MetaType.DQ) {
      this._dataQualityService.getSummary(selectedSourceTarget.uuid, selectedSourceTarget.version, MetaTypeEnum.MetaType.DQEXEC, response.runMode).subscribe(
        response => { this.onSuccessGetSummary(response, compareType) },
        error => {
          if (compareType == 'source') {
            this.sourceShowProgress = false;
            this.isSourceDataError = true;
            this.sourceDataMessage = 'No data available';
          }
          else if (compareType == 'target') {
            this.targetShowProgress = false;
            this.isTargetDataError = true;
            this.targetDataMessage = 'No data available';
          }
          console.log("Error :: " + error)
        });
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this._reconService.getResults(selectedSourceTarget.uuid, selectedSourceTarget.version, MetaTypeEnum.MetaType.RECONEXEC, response.runMode).subscribe(
        response => { this.onSuccessGetSummary(response, compareType) },
        error => {
          if (compareType == 'source') {
            this.sourceShowProgress = false;
            this.isSourceDataError = true;
            this.sourceDataMessage = 'No data available';
          }
          else if (compareType == 'target') {
            this.targetShowProgress = false;
            this.isTargetDataError = true;
            this.targetDataMessage = 'No data available';
          }
          console.log("Error :: " + error)
        });
    }
  }

  onSuccessGetSummary(response: any[], compareType: any): any {
    if (compareType == 'source') {
      this.sourceShowProgress = false;
      this.isSourceDataError = false;
      this.isSourceTableShow = true;
      this.colsSourcedata = response;
      let columns = [];

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
    this.allNameRule = [];
    this._commonService.getOneByUuidAndVersion(selectedRuleName.uuid, selectedRuleName.version, MetaTypeEnum.MetaType.RULEGROUP).subscribe(
      response => { this.onSuccessgetOneByUuidAndVersion(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response: RuleGroup): any {
    this.allNameRule = [];
    // this.allNameRule = this.fillArrayName(response);
    for (const i in response.ruleInfo) {
      let ver = new DropDownIO();
      ver.label = response.ruleInfo[i].ref.name;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response.ruleInfo[i].ref.name;
      ver.value.uuid = response.ruleInfo[i].ref.uuid;
      this.allNameRule[i] = ver;
    }
  }

  onSuccessgetRuleExecByRule(response: CompareResult[]): any {
    this.isInProgress = false;
    this.alltargetTemp = response;
    this.allsource = [];
    // this.allsource = this.fillArrayCreatedOn(response);
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].createdOn;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response[i].createdOn;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      this.allsource[i] = ver;
    }
    this.alltarget = [];
  }

  refreshSearchCriteria() {
    this.searchForm = {};
    this.searchForm.type = this.types[0].value;
    this.allsource = [];

    if (this.type == MetaTypeEnum.MetaType.DQ) {
      this.allNameDq = [];
      this.getAllLatest(MetaTypeEnum.MetaType.DQ);
    }
    else if (this.type == MetaTypeEnum.MetaType.RULE) {
      this.searchForm.selectedRuleName = '';
      this.getAllLatest(MetaTypeEnum.MetaType.RULE);
      this.getAllLatest(MetaTypeEnum.MetaType.RULEGROUP);
    }
    else if (this.type == MetaTypeEnum.MetaType.RECON) {
      this.searchForm.selectedReconName = [];
      this.getAllLatest(MetaTypeEnum.MetaType.RECON);
    }

    this.isSubmitDisable = true;
    this.refreshCompareResult();
  }

  refreshCompareResult() {
    this.alltarget = [];
    this.selectedSource = '';
    this.isSourceTableShow = false;
    this.isSourceDataError = false;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;
  }

  onDqNameChange() {
    this.isSubmitDisable = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;
  }
  onRuleNameChange() {
    this.isSubmitDisable = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;
  }
  onReconNameChange() {
    this.isSubmitDisable = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;
  }
}