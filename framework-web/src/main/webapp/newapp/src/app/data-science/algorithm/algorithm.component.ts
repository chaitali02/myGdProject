import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';

import { SelectItem } from 'primeng/primeng';
import { TagInputModule } from 'ngx-chips';


import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component'


import { AlgorithmService } from '../../metadata/services/algorithm.service';
import { CommonService } from '../../metadata/services/common.service';

import * as MetaTypeEnum from '../../metadata/enums/metaType';
import { AppConfig } from './../../app.config';
import {AppHelper} from '../../app.helper';
import {DropDownIO} from '../../metadata/domainIO/domain.dropDownIO';
import { Algorithm } from '../../metadata/domain/domain.algorithm';
import { Version } from '../../shared/version';
import { DependsOn } from '../dependsOn';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { setCheckNoChangesMode } from '@angular/core/src/render3/state';

@Component({
  selector: 'app-algorithm',
  templateUrl: './algorithm.template.html',
  styleUrls: []
})


export class AlgorithmComponent implements OnInit {
  showDivGraph: boolean;
  isHomeEnable: boolean;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  showForm: boolean = true;
  isSubmitEnable: boolean;
  customFlag: boolean;
  savePmml: boolean;
  published: boolean;
  active: boolean;
  locked: boolean;
  isEdit :boolean= false;
	isversionEnable:boolean = false;
	isAdd:boolean = false;
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
  isGraphInprogess: boolean;
  isGraphError: boolean;
  isSubmitInprogess: boolean;
  

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _algorithmService: AlgorithmService, public config: AppConfig, public appHelper:AppHelper) {
    this.metaType = MetaTypeEnum.MetaType;
    this.isEditInprogess=false;

    this.algorithm=new Algorithm();
    this.active =true;
    this.labelRequired =true;
    this.customFlag = false;
    this.locked=false;
    this.published=false;
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
      }else{
        this.isSubmitEnable = true;
        this.isEditInprogess=false;
        this.isEditError=false;
        this.algorithm=new Algorithm();
      }
      this.setMode(this.mode);
      this.getAllLatestParamListByTemplate();
    });
  }
  
  setMode(mode:any){
   if(mode =='true'){
    this.isEdit = false;
		this.isversionEnable = false;
		this.isAdd = false;
   }else if(mode =='false'){
    this.isEdit = true;
		this.isversionEnable = true;
		this.isAdd = false;
   }else{
    this.isAdd=true;
    this.isEdit =false;

   }
  }
  enableEdit(uuid: any, version: any) {
    this.router.navigate(['app/dataScience/algorithm', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm=true;
  }

  showGraph(uuid: any, version: any) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm=false;
    this.isGraphInprogess=true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess= this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError=this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeName(){
    this.breadcrumbDataFrom[2].caption = this.algorithm.name;
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, MetaTypeEnum.MetaType.ALGORITHM )
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error)
      );
  }

  onChangeCusFlg(event:any){
    if(event == true){
     this.algorithm.trainClass="";
     this.algorithm.modelClass="";
    }else{
      this.algorithm.scriptName=null;
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

  onSuccessGetAllLatestParamListByTemplate(response:BaseEntity[]) {
    this.allParamlist = [new DropDownIO];
    for (const i in response) {
      let refParam = new DropDownIO ();
      refParam.label = response[i].name;
      refParam.value = {label: "", uuid: ""}
      refParam.value.label= response[i].name;
      refParam.value.uuid = response[i].uuid;
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


    this.savePmml = this.appHelper.convertStringToBoolean(response.savePmml);
    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.active = this.appHelper.convertStringToBoolean(response.active);
    this.locked==this.appHelper.convertStringToBoolean(response.locked);
    this.customFlag =this.appHelper.convertStringToBoolean(response.customFlag);  
    this.labelRequired =this.appHelper.convertStringToBoolean(response.labelRequired)

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
      verObj.value = {label: "", uuid: ""}
      verObj.value.label= response[i].version;
      verObj.value.uuid = response[i].uuid;
      VersionList[i] = verObj;
    }
    this.VersionList = VersionList
  }


  submitAlgorithm(form) {
    debugger;
    var upd_tag = 'N'
    this.isSubmitEnable = true;
    this.isSubmitInprogess=true;
    let algoJson = new Algorithm();
    algoJson.uuid =this.algorithm.uuid;
    algoJson.name = this.algorithm.name;
    algoJson.tags= this.algorithm.tags
    algoJson.desc= this.algorithm.desc;
    algoJson.savePmml = this.appHelper.convertBooleanToString(this.savePmml);
    algoJson.active =this.appHelper.convertBooleanToString(this.active);
    algoJson.published = this.appHelper.convertBooleanToString(this.published);
    algoJson.locked = this.appHelper.convertBooleanToString(this.locked);
    algoJson.customFlag = this.appHelper.convertBooleanToString(this.customFlag);
    algoJson.labelRequired = this.appHelper.convertBooleanToString(this.labelRequired);

    algoJson.type= this.algorithm.type;
    algoJson.libraryType = this.libraryType;

    if(this.customFlag){
      algoJson.scriptName = this.algorithm.scriptName;
      algoJson.trainClass =null;
      algoJson.modelClass = null;
     }
    else{
        algoJson.scriptName =null;
        algoJson.trainClass = this.algorithm.trainClass;
        algoJson.modelClass = this.algorithm.modelClass;
    
    }

    let paramListWHParam = new MetaIdentifierHolder();
    let paramListWHParamRef = new MetaIdentifier ();

    if (this.paramListWH != null) {
      paramListWHParamRef.uuid = this.paramListWH.uuid;
      paramListWHParamRef.type = MetaTypeEnum.MetaType.PARAMLIST;
      paramListWHParam.ref = paramListWHParamRef;
      algoJson.paramListWH = paramListWHParam;
    }else{
      algoJson.paramListWH = null;
    }
    
    let paramListWOHParam =new MetaIdentifierHolder();
    let paramListWOHParamRef = new MetaIdentifier ();
    if (this.paramListWoH != null) {
      paramListWOHParamRef.uuid = this.paramListWoH.uuid;
      paramListWOHParamRef.type= MetaTypeEnum.MetaType.PARAMLIST;;
      paramListWOHParam.ref = paramListWOHParamRef;
      algoJson.paramListWoH= paramListWOHParam;
    }
    else{
      algoJson.paramListWoH=null;
    }
    algoJson.summaryMethods =this.summaryMethods;
    console.log(JSON.stringify(algoJson));
    // this._algorithmService.submit(algoJson, MetaTypeEnum.MetaType.ALGORITHM, upd_tag).subscribe(
    //   response => { this.OnSuccessubmit(response) },
    //   error => {
    //             console.log('Error :: ' + error);
    //             this.isSubmitInprogess=false;
    //            }
    // )
  }

  OnSuccessubmit(response: any) {
    this.isSubmitEnable = true;
    this.isSubmitInprogess=false;
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