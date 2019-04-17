import { MetadataService } from '../../../metadata/services/metadata.service';
import { DataQualityService } from '../../../metadata/services/dataQuality.services';
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location, DatePipe } from '@angular/common';
import { AppMetadata } from '../../../app.metadata';
import { CommonService } from '../../../metadata/services/common.service';
import { Http } from '@angular/http';
import { AppConfig } from '../../../app.config';
import { ProfileService } from '../../../metadata/services/profile.service';
import { AttributeIO } from '../../../metadata/domainIO/domain.attributeIO';
import { DropDownIO } from '../../../metadata/domainIO/domain.dropDownIO';
import { CompareResult } from '../../../metadata/domain/domain.compareResult';
import { MetaType } from '../../../metadata/enums/metaType';
import { RoutesParam } from '../../../metadata/domain/domain.routeParams';

@Component({
  selector: 'app-resultstab',
  templateUrl: './resultstab.template.html',
  styleUrls: []
})

export class ResultsTabComponent {

  metaType = MetaType;
  type: any;
  searchForm: any;
  allNameDatapod: any[];
  allNameAttribute: any[];
  startDate: Date;
  endDate: Date;
  allsource: any[];
  alltarget: any[];
  colsTargetdata: any[];
  colsTarget: any[];
  colsSourcedata: any[];
  colsSource: any[];
  alltargetTemp: any;


  sourceDataMessage: string;
  targetDataMessage: string;
  isInProgress: boolean;
  isSourceDataError: boolean;
  isTargetDataError: boolean;
  isSubmitDisable: boolean;

  sourceShowProgress: boolean;
  targetShowProgress: boolean;
  isSourceTableShow: boolean;
  isTargetTableShow: boolean;
  selectedSource: any;
  selectedTarget: any;

  caretdown = 'fa fa-caret-down';

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _profileService: ProfileService, private _metadataService: MetadataService, private datePipe: DatePipe) {

    this._activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.type = (param.type).toLowerCase();
      this.getAllLatest(this.type);
      this.isSubmitDisable = true;
    });

    this.searchForm = {};
    this.allsource = [];
    this.alltarget = [];
    this.allNameDatapod = [];
    this.allNameAttribute = [];

    this.isInProgress = false;

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
  getAllLatest(type: any): any {
    this._commonService.getAllLatest(type).subscribe(
      response => { this.onSuccessgetAllLatest(response, type) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response: AttributeIO[], type: String): any {
    if (type == this.type) {
      this.allNameDatapod = [];
      for (const i in response) {
        let ver = new DropDownIO();
        response.sort((a, b) => a.name.localeCompare(b.name.toString()));
        ver.label = response[i].name;
        ver.value = { label: String, uuid: String, version: String };
        ver.value.label = response[i].name;
        ver.value.uuid = response[i].uuid;
        ver.value.version = response[i].version;
        this.allNameDatapod[i] = ver;
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
    this.isSubmitDisable = true;

    let startDate;
    let endDate;
    if (this.searchForm.startDate) {
      let startDateUtc = new Date(this.searchForm.startDate.getUTCFullYear(), this.searchForm.startDate.getUTCMonth(), this.searchForm.startDate.getUTCDate(), this.searchForm.startDate.getUTCHours(), this.searchForm.startDate.getUTCMinutes(), this.searchForm.startDate.getUTCSeconds())
      // startDate = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC";
      startDate = this.datePipe.transform(startDateUtc, "EEE MMM dd HH:mm:ss yyyy").toString() + " UTC"; 
    }
    else {
      startDate = '';
    }

    if (this.searchForm.endDate) {
      let endDateUtc = new Date(this.searchForm.endDate.getUTCFullYear(), this.searchForm.endDate.getUTCMonth(), this.searchForm.endDate.getUTCDate(), this.searchForm.endDate.getUTCHours(), this.searchForm.endDate.getUTCMinutes(), this.searchForm.endDate.getUTCSeconds())
      // endDate = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC";
      endDate = this.datePipe.transform(endDateUtc, "EEE MMM dd HH:mm:ss yyyy").toString() + " UTC";
    }
    else {
      endDate = '';
    }

    this._profileService.getProfileExecByDatapod(this.searchForm.selectedDatapodName.uuid, this.metaType.PROFILEEXEC, startDate, endDate).subscribe(
      response => { this.onSuccessgetRuleExecByRule(response) },
      error => console.log("Error : " + error));

  }

  onSuccessgetRuleExecByRule(response: CompareResult[]): any {
    this.isInProgress = false;
    this.alltargetTemp = response;
    this.allsource = [];
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
    debugger

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

    this._metadataService.getNumRowsbyExec(selectedSource.uuid, selectedSource.version, this.metaType.PROFILEEXEC).subscribe(
      response => { this.onSuccessgetNumRowsbyExec(response, selectedSource, "source") },
      error => console.log("Error :: " + error));
  }

  onChangeTarget(selectedTarget: any) {
    this.targetShowProgress = true;
    this.isTargetTableShow = false;
    this.isTargetDataError = false;

    this._metadataService.getNumRowsbyExec(selectedTarget.uuid, selectedTarget.version, this.metaType.PROFILEEXEC).subscribe(
      response => { this.onSuccessgetNumRowsbyExec(response, selectedTarget, "target") },
      error => console.log("Error :: " + error));
  }

  onSuccessgetNumRowsbyExec(response: any, selectedSourceTarget: any, compareType: String): any {

    this._profileService.getResults(selectedSourceTarget.uuid, selectedSourceTarget.version, response.runMode).subscribe(
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

  onChangeDatapod(selectedDatapodName) {
    this.allNameAttribute = [];
    this._metadataService.getAttributesByDatapod(selectedDatapodName.uuid, this.metaType.DATAPOD).subscribe(
      response => { this.onSuccessgetAttributesByDatapod(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetAttributesByDatapod(response: any): any {
    this.allNameAttribute = [];
    for (let i = 0; i < response.length; i++) {
      let ver = new DropDownIO();
      ver.label = response[i].attrName;
      ver.value = { label: String, uuid: String, version: "" };
      ver.value.label = response[i].attrName;
      ver.value.uuid = response[i].attrId;
      this.allNameAttribute[i] = ver;
    }
  }

  refreshSearchCriteria() {
    this.searchForm = {};
    this.searchForm.type = this.type;
    this.allsource = [];

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

  onAttributeNameChange() {
    this.isSubmitDisable = false;
    this.isSourceTableShow = false;
    this.isTargetTableShow = false;
    this.isSourceDataError = false;
    this.isTargetDataError = false;
  }

}