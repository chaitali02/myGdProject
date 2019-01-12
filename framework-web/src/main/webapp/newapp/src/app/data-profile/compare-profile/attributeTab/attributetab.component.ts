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

@Component({
  selector: 'app-attributetab',
  templateUrl: './attributetab.template.html',
  styleUrls: []
})

export class AttributeTabComponent {

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

  isSubmitDisable: boolean;

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata,
    private _commonService: CommonService, private _profileService: ProfileService, private _dataQualityService: DataQualityService, private _metadataService: MetadataService, private datePipe: DatePipe) {

    this._activatedRoute.params.subscribe((params: Params) => {
      this.type = (params['type']).toLowerCase();
      this.getAllLatest(this.type);
    });

    this.searchForm = {};

    this.allNameSourceProfileAttribute = [
      { label: "minVal", value: "minVal" },
      { label: "maxVal", value: "maxVal" },
      { label: "avgVal", value: "avgVal" },
      { label: "medianVal", value: "medianVal" },
      { label: "stdDev", value: "stdDev" },
      { label: "numDistinct", value: "numDistinct" },
      { label: "perDistinct", value: "perDistinct" },
      { label: "numNull", value: "numNull" },
      { label: "perNull", value: "perNull" },
      { label: "sixSigma", value: "sixSigma" }];

    this.allNameTargetProfileAttribute = [
      { label: "minVal", value: "minVal" },
      { label: "maxVal", value: "maxVal" },
      { label: "avgVal", value: "avgVal" },
      { label: "medianVal", value: "medianVal" },
      { label: "stdDev", value: "stdDev" },
      { label: "numDistinct", value: "numDistinct" },
      { label: "perDistinct", value: "perDistinct" },
      { label: "numNull", value: "numNull" },
      { label: "perNull", value: "perNull" },
      { label: "sixSigma", value: "sixSigma" }];

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

  onSuccessgetAllLatest(response: any[], type: String): any {

    this.allNameSourceDatapod = [];
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      this.allNameSourceDatapod[i] = ver;
    };

    this.allNameTargetDatapod = [];
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['name'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['name'];
      ver["value"]["uuid"] = response[i]['uuid'];
      ver["value"]["version"] = response[i]['version'];
      this.allNameTargetDatapod[i] = ver;
    }
  }

  onChangeSourceDatapod(selectedSourceDatapodAttribute) {
    this.allNameSourceDatapodAttribute = [];
    this._metadataService.getAttributesByDatapod(selectedSourceDatapodAttribute.uuid, "datapod").subscribe(
      response => { this.onSuccessgetAttributesByDatapod(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetAttributesByDatapod(response: any): any {
    this.allNameSourceDatapodAttribute = [];
    for (let i = 0; i < response.length; i++) {
      let ver = {};
      ver["label"] = response[i].attrName;
      ver["value"] = {};
      ver["value"]["label"] = response[i].attrName;
      ver["value"]["id"] = response[i].attrId;
      this.allNameSourceDatapodAttribute[i] = ver;
    }
  }

  onChangeTargetDatapod(selectedTargetPeriod) {
    console.log("onChangeTargetDatapod call...");
    this.allNameTargetDatapodAttribute = [];
    this._metadataService.getAttributesByDatapod(selectedTargetPeriod.uuid, "datapod").subscribe(
      response => { this.onSuccessgetAttributesByDatapod1(response) },
      error => console.log("Error :: " + error));
  }
  onSuccessgetAttributesByDatapod1(response: any): any {
    this.allNameTargetDatapodAttribute = [];
    for (let i = 0; i < response.length; i++) {
      let ver = {};
      ver["label"] = response[i].attrName;
      ver["value"] = {};
      ver["value"]["label"] = response[i].attrName;
      ver["value"]["id"] = response[i].attrId;
      this.allNameTargetDatapodAttribute[i] = ver;
    }
  }

  submitSearchCriteria() {
    let suuid = this.searchForm.selectedSourceDatapodName.uuid;
    let sversion = this.searchForm.selectedSourceDatapodName.version;
    let sprofileAttrId = this.searchForm.selectedSourceDatapodAttribute.id;
    let speriod = this.searchForm.selectedSourcePeriod.value;
    let sminval = this.searchForm.selectedSourceProfileAttribute.value;

    this._profileService.getProfileResults('profileexec', suuid, sversion, sprofileAttrId, speriod, sminval).subscribe(
      response => { this.onSuccessgetProfileResultse(response) },
      error => console.log("Error : " + error));

    let tuuid = this.searchForm.selectedTargetDatapodName.uuid;
    let tversion = this.searchForm.selectedTargetDatapodName.version;
    let tprofileAttrId = this.searchForm.selectedTargetDatapodAttribute.id;
    let tperiod = this.searchForm.selectedTargetPeriod.value;
    let tminval = this.searchForm.selectedTargetProfileAttribute.value;

    this._profileService.getProfileResults('profileexec', tuuid, tversion, tprofileAttrId, tperiod, tminval).subscribe(
      response => { this.onSuccessgetProfileResultse(response) },
      error => console.log("Error : " + error));

  }

  onSuccessgetProfileResultse(response: any[]): any {
    debugger
    let resp = response;
    console.log("responce : "+response);
  }

  refreshSearchCriteria(){
    this.searchForm = {};
    this.searchForm.selectedSourceProfileAttribute = this.allNameSourceProfileAttribute[0];
    this.searchForm.selectedSourcePeriod = this.allNameSourcePeriod[0];
    this.searchForm.selectedTargetProfileAttribute = this.allNameTargetProfileAttribute[0];
    this.searchForm.selectedTargetPeriod = this.allNameTargetPeriod[0];
    
    this.isSubmitDisable = true;
    this.refreshTrendAnalysis();
  }

  refreshTrendAnalysis(){

  }
}