import { MetaType } from './../../../metadata/enums/metaType';
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
import { DatapodService } from '../../../metadata/services/datapod.service';
import { RoutesParam } from '../../../metadata/domain/domain.routeParams';
import { AttributeIO } from '../../../metadata/domainIO/domain.attributeIO';
import { DropDownIO } from '../../../metadata/domainIO/domain.dropDownIO';
import { HistogramIO } from '../../../metadata/domainIO/domain.histogramIO';
import { DataxIO } from '../../../metadata/domainIO/domain.dataxIO';

@Component({
  selector: 'app-attributetab',
  templateUrl: './attributetab.template.html',
  styleUrls: []
})

export class AttributeTabComponent {

  metaType = MetaType;
  type: any;
  searchForm: any;
  allNameSourceDatapod: any[];
  allNameTargetDatapod: any[];
  allNameSourceProfileAttribute: any[];
  allNameTargetProfileAttribute: any[];
  allNameSourcePeriod: any[];
  allNameTargetPeriod: any[];
  allNameSourceDatapodAttribute: any[];
  allNameTargetDatapodAttribute: any[];
  //isSubmitDisable: boolean;
  originalDataHistogram: any[];
  isHistogramInprogess: boolean;
  isHistogramError: boolean;
  sdatacol: any;
  tdatacol: any;
  histogramData: any[];
  histogramcols: any[];
  sourceHistrogramSpinner: boolean = false;
  targetHistrogramSpinner: boolean = false;
  sourceHistrogramError: boolean = false;
  targetHistrogramError: boolean = false;
  showSourceGraph: boolean = false;
  showTargetGraph: boolean = false;
  caretdown = 'fa fa-caret-down';

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _profileService: ProfileService, private _datapodService: DatapodService, private _metadataService: MetadataService, private datePipe: DatePipe) {

    this._activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.type = (param.type).toLowerCase();
      this.getAllLatest(this.type);
    });

    this.searchForm = {};

    this.allNameSourceProfileAttribute = [
      { label: "minVal", value: "minval" },
      { label: "maxVal", value: "maxval" },
      { label: "avgVal", value: "avgval" },
      { label: "medianVal", value: "medianval" },
      { label: "stdDev", value: "stddev" },
      { label: "numDistinct", value: "numdistinct" },
      { label: "perDistinct", value: "perdistinct" },
      { label: "numNull", value: "numnull" },
      { label: "perNull", value: "pernull" },
      { label: "sixSigma", value: "sixsigma" }];

    this.allNameTargetProfileAttribute = [
      { label: "minVal", value: "minval" },
      { label: "maxVal", value: "maxval" },
      { label: "avgVal", value: "avgval" },
      { label: "medianVal", value: "medianval" },
      { label: "stdDev", value: "stddev" },
      { label: "numDistinct", value: "numdistinct" },
      { label: "perDistinct", value: "perdistinct" },
      { label: "numNull", value: "numdull" },
      { label: "perNull", value: "perdull" },
      { label: "sixSigma", value: "sixsigma" }];

    this.allNameSourcePeriod = [
      { label: "1 week", value: "7" },
      { label: "1 Month", value: "30" },
      { label: "3 Month", value: "90" },
      { label: "1 Year", value: "365" }];

    this.allNameTargetPeriod = [
      { label: "1 week", value: "7" },
      { label: "1 Month", value: "30" },
      { label: "3 Month", value: "90" },
      { label: "1 Year", value: "365" }];

    this.searchForm.selectedSourceProfileAttribute = this.allNameSourceProfileAttribute[0];
    this.searchForm.selectedSourcePeriod = this.allNameSourcePeriod[0];
    this.searchForm.selectedTargetProfileAttribute = this.allNameTargetProfileAttribute[0];
    this.searchForm.selectedTargetPeriod = this.allNameTargetPeriod[0];

  }

  getAllLatest(type: any): any {
    this._commonService.getAllLatest(type).subscribe(
      response => { this.onSuccessgetAllLatest(response, type) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAllLatest(response: AttributeIO[], type: String): any {

    this.allNameSourceDatapod = [];
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].name;
      ver.value =  { label: String, uuid: String, version: String };
      ver.value.label = response[i].name;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      this.allNameSourceDatapod[i] = ver;
    };

    this.allNameTargetDatapod = [];
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].name;
      ver.value = { label: String, uuid: String, version: String };
      ver.value.label = response[i].name;
      ver.value.uuid = response[i].uuid;
      ver.value.version = response[i].version;
      this.allNameTargetDatapod[i] = ver;
    }
  }

  onChangeSourceDatapod(selectedSourceDatapodAttribute) {
    this.allNameSourceDatapodAttribute = [];
    this._metadataService.getAttributesByDatapod(selectedSourceDatapodAttribute.uuid, this.metaType.DATAPOD).subscribe(
      response => { this.onSuccessgetAttributesByDatapod(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetAttributesByDatapod(response: any): any {
    this.allNameSourceDatapodAttribute = [];
    for (let i = 0; i < response.length; i++) {
      let ver = new DropDownIO();
      ver.label = response[i].attrName;
      ver.value = { label: "", id: "" };
      ver.value.label = response[i].attrName;
      ver.value.id = response[i].attrId;
      this.allNameSourceDatapodAttribute[i] = ver;
    }
  }

  onChangeTargetDatapod(selectedTargetPeriod) {
    console.log("onChangeTargetDatapod call...");
    this.allNameTargetDatapodAttribute = [];
    this._metadataService.getAttributesByDatapod(selectedTargetPeriod.uuid, this.metaType.DATAPOD).subscribe(
      response => { this.onSuccessgetAttributesByDatapod1(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetAttributesByDatapod1(response: any): any {
    this.allNameTargetDatapodAttribute = [];
    for (let i = 0; i < response.length; i++) {
      let ver = new DropDownIO();
      ver.label = response[i].attrName;
      ver.value = { label: "", id: "" };
      ver.value.label = response[i].attrName;
      ver.value.id = response[i].attrId;
      this.allNameTargetDatapodAttribute[i] = ver;
    }
  }

  submitSearchCriteria() {
    this.showSourceGraph = true;
    this.showTargetGraph = true;
    this.sourceHistrogramSpinner = true;
    this.targetHistrogramSpinner = true;

    let suuid = this.searchForm.selectedSourceDatapodName.uuid;
    let sversion = this.searchForm.selectedSourceDatapodName.version;
    let sDatapodAttrId = this.searchForm.selectedSourceDatapodAttribute.id;
    let speriod = this.searchForm.selectedSourcePeriod.value;
    let sprofileAttr = this.searchForm.selectedSourceProfileAttribute.value;

    this._profileService.getProfileResults(this.metaType.PROFILEEXEC, suuid, sversion, sDatapodAttrId, speriod, sprofileAttr).subscribe(
      response => { this.onSuccessgetProfileResults(response, 'source') },
      error => {
        this.sourceHistrogramError = true;
        this.sourceHistrogramSpinner = false;
        console.log("Error : " + error)
      });

    let tuuid = this.searchForm.selectedTargetDatapodName.uuid;
    let tversion = this.searchForm.selectedTargetDatapodName.version;
    let tDatapodAttrId = this.searchForm.selectedTargetDatapodAttribute.id;
    let tperiod = this.searchForm.selectedTargetPeriod.value;
    let tprofileAttr = this.searchForm.selectedTargetProfileAttribute.value;

    this._profileService.getProfileResults(this.metaType.PROFILEEXEC, tuuid, tversion, tDatapodAttrId, tperiod, tprofileAttr).subscribe(
      response => { this.onSuccessgetProfileResults(response, 'target') },
      error => {
      this.targetHistrogramError = true;
      this.targetHistrogramSpinner = false;
      console.log("Error : " + error);
      });
  }

  onSuccessgetProfileResults(response: any[], sourceOrTarget): any {
    if (sourceOrTarget == 'source') {
      this.sdatacol = {};
      if (response.length >= 20) {
        this.sdatacol.datapoints = response.slice(0, 20);
      } else {
        this.sdatacol.datapoints = response;
      }
      var dataColumn = new HistogramIO();
      dataColumn.id = this.searchForm.selectedSourceProfileAttribute.value;
      dataColumn.name = this.searchForm.selectedSourceProfileAttribute.value;
      dataColumn.type = "bar"
      dataColumn.color = "#D8A2DE";
      this.sdatacol.datacolumns = [];
      this.sdatacol.datacolumns[0] = dataColumn;
      var datax = new DataxIO();
      datax.id = "createdOn";
      this.sdatacol.datax = datax;
      this.sourceHistrogramSpinner = false;
    }

    else if (sourceOrTarget == 'target') {
      this.tdatacol = {};
      if (response.length >= 20) {
        this.tdatacol.datapoints = response.slice(0, 20);
      } else {
        this.tdatacol.datapoints = response;
      }
      var dataColumn = new HistogramIO();
      dataColumn.id = this.searchForm.selectedTargetProfileAttribute.value;
      dataColumn.name = this.searchForm.selectedTargetProfileAttribute.value;
      dataColumn.type = "bar"
      dataColumn.color = "#D8A2DE";
      this.tdatacol.datacolumns = [];
      this.tdatacol.datacolumns[0] = dataColumn;
      var datax = new DataxIO();
      datax.id = "createdOn";
      this.tdatacol.datax = datax;      
      this.targetHistrogramSpinner = false;
    }
  }

  refreshSearchCriteria() {
    this.searchForm = {};
    this.searchForm.selectedSourceProfileAttribute = this.allNameSourceProfileAttribute[0];
    this.searchForm.selectedSourcePeriod = this.allNameSourcePeriod[0];
    this.searchForm.selectedTargetProfileAttribute = this.allNameTargetProfileAttribute[0];
    this.searchForm.selectedTargetPeriod = this.allNameTargetPeriod[0];

    //this.isSubmitDisable = true;
    this.refreshTrendAnalysis();
  }

  refreshTrendAnalysis() {
    this.showSourceGraph = false;
    this.showTargetGraph = false;
  }
}