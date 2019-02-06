import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';

import { SelectItem } from 'primeng/primeng';
import { TagInputModule } from 'ngx-chips';


import { AppConfig } from './../../app.config';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component'


import { AlgorithmService } from '../../metadata/services/algorithm.service';
import { CommonService } from '../../metadata/services/common.service';

import * as MetaTypeEnum from '../../metadata/enums/metaType';
import {DropDownIO} from '../../metadata/domainIO/domain.dropDownIO';
import { Algorithm } from '../../metadata/domain/domain.algorithm';
import { Version } from '../../shared/version';
import { DependsOn } from '../dependsOn';

@Component({
  selector: 'app-algorithm',
  templateUrl: './algorithm.template.html',
  styleUrls: []
})


export class AlgorithmComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  showForm: boolean = true;
  isSubmitEnable: boolean;

  summaryMethods: any[]
  allParamlist: any[];
  breadcrumbDataFrom: any;
  algorithm: Algorithm;
  VersionList: Array<DropDownIO>;
  selectedVersion: Version;
  tags: any;
  mode: any;
  version: any;
  type: any;
  libraryType: any;
  librarytypesOption: { 'value': String, 'label': String }[];
  typesOption: { 'value': String, 'label': String }[];
  arrayParamList: any;
  paramListWoH: DependsOn;
  paramListWH: DependsOn;
  msgs: any;
  labelRequired: any;
  metaType: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _algorithmService: AlgorithmService, public config: AppConfig) {
    this.metaType = MetaTypeEnum.MetaType;
    console.log(this.metaType)
   // this.algorithm.active = "Y";
   // this.algorithm.labelRequired = "Y";
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [
      {
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
      let id = params['id'];
      let version = params['version'];
      this.mode = params['mode'];
      this.summaryMethods = []
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(id, version);
        this.getAllVersionByUuid(id);
      }
      this.getAllLatestParamListByTemplate();
    });
  }

  enableEdit(uuid: any, version: any) {
    this.router.navigate(['app/dataScience/algorithm', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showGraph = false;
  }

  showDagGraph(uuid: any, version: any) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
    }, 1000);
  }


  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'algorithm')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error)
      );
  }

  onChangeActive(event: any) {
    if (event === true) {
      this.algorithm.active = 'Y';
    }
    else {
      this.algorithm.active = 'N';
    }
  }

  onChangePublished(event: any) {
    if (event === true) {
      this.algorithm.published = 'Y';
    }
    else {
      this.algorithm.published = 'N';
    }
  }

  onChangeLabel(event: any) {
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
        error => console.log("Error :: " + error)
      );
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

  getOneByUuidAndVersion(id: any, version: any) {

    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(id, version, MetaTypeEnum.MetaType.ALGORITHM)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response);
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = false;

        }
      );
  }

  onSuccessgetOneByUuidAndVersion(response: Algorithm) {
    this.algorithm = response;

    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version


    this.algorithm.savePmml = response.savePmml == 'Y' ? (true).valueOf.toString() : false.valueOf.toString()
    this.algorithm.published = response.published == 'Y' ? true.valueOf.toString() : false.valueOf.toString()
    this.algorithm.active = response.active == 'Y' ? true.valueOf.toString() : false.valueOf.toString()

    if (response.tags != null) {
      this.algorithm.tags =response.tags;
    }//End If

    if (response.paramListWoH !== null) {
      let paramListWoH: DependsOn = new DependsOn();
      paramListWoH.uuid = response.paramListWoH.ref.uuid;
      paramListWoH.label = response.paramListWoH.ref.name;
      this.paramListWoH = paramListWoH;
    }
    if (response.paramListWH !== null) {
      let paramListWH: DependsOn = new DependsOn();
      paramListWH.uuid = response.paramListWH.ref.uuid;
      paramListWH.label = response.paramListWH.ref.name;
      this.paramListWH = paramListWH;
    }

    var summaryMethods = [];
    if (response.summaryMethods != null) {
      this.summaryMethods = response.summaryMethods;
    }//End If

    this.breadcrumbDataFrom[2].caption = this.algorithm.name;
    this.isEditInprogess = false;
  }

  getAllVersionByUuid(id: any) {
    this._commonService.getAllVersionByUuid(MetaTypeEnum.MetaType.ALGORITHM, id)
      .subscribe(
        response => {
          this.OnSuccessgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error)
      );
  }

  OnSuccessgetAllVersionByUuid(response:Algorithm[]) {
    var VersionList = [new DropDownIO]
    for (const i in response) {
      let verObj = new DropDownIO();
      verObj.label= response[i].version;
      verObj.value.label= response[i].version;
      verObj.value.uuid = response[i].uuid;
      VersionList[i] = verObj;
    }
    this.VersionList = VersionList
  }


  submitAlgorithm() {
    var upd_tag = 'N'
    this.isSubmitEnable = true;
    let algoJson = {};
    algoJson["uuid"] = this.algorithm.uuid;
    algoJson["name"] = this.algorithm.name;
    var tagArray = [];
    // if (this.algorithm.tags != null) {
    //   for (var counttag = 0; counttag < this.algorithm.tags.length; counttag++) {
    //     tagArray[counttag] = this.algorithm.tags[counttag].value;

    //   }
    // }
    algoJson['tags'] = tagArray
    algoJson["summaryMethods"] = summaryMethods;
    algoJson["desc"] = this.algorithm.desc;
    algoJson["savePmml"] = this.algorithm.savePmml == "" + true ? 'Y' : "N"
    algoJson["active"] = this.algorithm.active == "" + true ? 'Y' : "N"
    algoJson["published"] = this.algorithm.published == "" + true ? 'Y' : "N"
    algoJson["type"] = this.algorithm.type;
    algoJson["libraryType"] = this.libraryType;
    algoJson["trainClass"] = this.algorithm.trainClass;
    algoJson["modelClass"] = this.algorithm.modelClass;
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

  OnSuccessubmit(response: any) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Algorithm Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }


  public goBack() {
    this.router.navigate(['app/list/algorithm']);
  }

}