import { Location } from '@angular/common';
import { AppConfig } from './../../app.config';
import { Component, OnInit, ViewChild } from '@angular/core';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { DependsOn } from '../dependsOn';
import { AlgorithmService } from '../../metadata/services/algorithm.service';
import { TagInputModule } from 'ngx-chips';
import { Observable } from 'rxjs/Observable';
import { filter, map } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component'
@Component({
  selector: 'app-algorithm',
  templateUrl: './algorithm.template.html',
  styleUrls: []
})
export class AlgorithmComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  alltags: any;
  savePmml: any;
  summaryMethods: any[]
  allParamlist: any[];
  breadcrumbDataFrom: any;
  showAlgorithm: any;
  algorithm: any;
  // versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  trainClass: any;
  modelClass: any;
  type: any;
  libraryType: any;
  librarytypesOption: { 'value': String, 'label': String }[];
  typesOption: { 'value': String, 'label': String }[];
  arrayParamList: any;
  paramListWoH: DependsOn;
  paramListWH: DependsOn;
  msgs: any;
  isSubmitEnable: any;
  text: any;
  labelRequired: any
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _algorithmService: AlgorithmService) {
    this.showAlgorithm = true;
    this.algorithm = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.algorithm["active"] = true;
    this.algorithm["labelRequired"] = true;
    this.isSubmitEnable = true;
    // this.algorithm['summaryMethods'] =null
    //  this.selectParamlistWithoutHype = {};
    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/algorithm"
    },
    {
      "caption": "Algorithm",
      "routeurl": "/app/list/algorithm"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]

    this.librarytypesOption = [
      { "value": "SPARKML", "label": "SPARKML" },
      { "value": "R", "label": "R" },
      { "value": "JAVA", "label": "JAVA" }
    ]

    this.typesOption = [
      { "value": "CLUSTERING", "label": "CLUSTERING" },
      { "value": "CLASSIFICATION", "label": "CLASSIFICATION" },
      { "value": "REGRESSION", "label": "REGRESSION" },
      { "value": "SIMULATION", "label": "SIMULATION" }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.summaryMethods = []
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
      this.getAllLatestParamListByTemplate();
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'algorithm')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('algorithm', this.id)
      .subscribe(
      response => {
        this.OnSuccessgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.algorithm = response;
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = response.createdBy.ref.name;
    this.savePmml = response["savePmml"] == 'Y' ? true : false
    this.algorithm.published = response["published"] == 'Y' ? true : false
    this.algorithm.active = response["active"] == 'Y' ? true : false
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.algorithm.tags = tags;
    }//End If


    this.libraryType = response.libraryType
    this.trainClass = response.trainClass
    this.modelClass = response.modelClass
    if (response.paramListWoH !== null) {
      let paramListWoH: DependsOn = new DependsOn();
      paramListWoH.uuid = response["paramListWoH"]["ref"]["uuid"];
      paramListWoH.label = response["paramListWoH"]["ref"]["name"];
      this.paramListWoH = paramListWoH;
    }
    if (response.paramListWH !== null) {
      let paramListWH: DependsOn = new DependsOn();
      paramListWH.uuid = response["paramListWH"]["ref"]["uuid"];
      paramListWH.label = response["paramListWH"]["ref"]["name"];
      this.paramListWH = paramListWH;
    }

    var summaryMethods = [];
    if (response.summaryMethods != null) {
      for (var i = 0; i < response.summaryMethods.length; i++) {
        var summaryMethod = {};
        summaryMethod['value'] = response.summaryMethods[i];
        summaryMethod['display'] = response.summaryMethods[i];
        summaryMethods[i] = summaryMethod

      }//End For
      this.summaryMethods = summaryMethods;
    }//End If
    this.breadcrumbDataFrom[2].caption = this.algorithm.name;
    console.log('Data is' + response);
  }

  OnSuccessgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'algorithm')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.algorithm.active = 'Y';
    }
    else {
      this.algorithm.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.algorithm.published = 'Y';
    }
    else {
      this.algorithm.published = 'N';
    }
  }

  onChangeLabel(event) {
    if (event === true) {
      this.algorithm.published = 'Y';
    }
    else {
      this.algorithm.published = 'N';
    }
  }
  getAllLatestParamListByTemplate() {

    this._commonService.getAllLatestParamListByTemplate('Y', 'paramlist', 'model')
      .subscribe(
      response => {
        this.onSuccessGetAllLatestParamListByTemplate(response)
      },
      error => console.log("Error :: " + error));

  }
  onSuccessGetAllLatestParamListByTemplate(response) {
    this.allParamlist = [];
    for (const i in response) {
      let refParam = {};
      refParam["label"] = response[i]['name'];
      refParam["value"] = {}
      refParam["value"]['name'] = response[i]['name'];
      refParam["value"]['label'] = response[i]['name'];
      refParam["value"]['uuid'] = response[i]['uuid'];
      this.allParamlist[i] = refParam;
    }
  }

  submitAlgorithm() {
    var upd_tag = 'N'
    this.isSubmitEnable = true;
    let algoJson = {};
    algoJson["uuid"] = this.algorithm.uuid;
    algoJson["name"] = this.algorithm.name;
    var tagArray = [];
    if (this.algorithm.tags != null) {
      for (var counttag = 0; counttag < this.algorithm.tags.length; counttag++) {
        tagArray[counttag] = this.algorithm.tags[counttag].value;

      }
    }
    algoJson['tags'] = tagArray
    algoJson["summaryMethods"] = summaryMethods;
    algoJson["desc"] = this.algorithm.desc;
    algoJson["savePmml"] = this.algorithm.savePmml == true ? 'Y' : "N"
    algoJson["active"] = this.algorithm.active == true ? 'Y' : "N"
    algoJson["published"] = this.algorithm.published == true ? 'Y' : "N"
    algoJson["type"] = this.algorithm.type;
    algoJson["libraryType"] = this.libraryType;
    algoJson["trainClass"] = this.trainClass;
    algoJson["modelClass"] = this.modelClass;
    algoJson["labelRequired"] = this.labelRequired;

    let paramListWHParam = {};
    let paramListWHParamRef = {};
    if (this.paramListWH != null) {
      paramListWHParamRef["uuid"] = this.paramListWH.uuid;
      paramListWHParamRef["type"] = "paramlist";
      paramListWHParam["ref"] = paramListWHParamRef;
    }
    algoJson["paramListWH"] = paramListWHParam;

    let paramListWOHParam = {};
    let paramListWOHParamRef = {};
    if (this.paramListWoH != null) {
      paramListWOHParamRef["uuid"] = this.paramListWoH.uuid;
      paramListWOHParamRef["type"] = "paramlist";
      paramListWOHParam["ref"] = paramListWOHParamRef;
    }
    algoJson["paramListWoH"] = paramListWOHParam;

    var summaryMethods = [];
    if (this.summaryMethods != null) {
      for (var counttag = 0; counttag < this.summaryMethods.length; counttag++) {
        summaryMethods[counttag] = this.summaryMethods[counttag].value;

      }
    }
    algoJson["summaryMethods"] = summaryMethods;
    console.log(JSON.stringify(algoJson));
    this._algorithmService.submit(algoJson, 'algorithm', upd_tag).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )


  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Algorithm Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/algorithm']);
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataScience/algorithm', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
    }, 1000);
  }

}