import { Value } from './../../metadata/domain/domain.value';
import { NgModule, Component, ViewEncapsulation, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { Location } from '@angular/common';
import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service'
import { DataPodResource } from '../datapod/datapod-resource';
import { DataPreparationFormulaInfo } from './formula_Depends_On';
import { DataPreparationFormulaAttributes } from './formula_attributes';
import { MetaDataDataPodService } from '../datapod/datapod.service';

import { Version } from '../../shared/version'
import { DependsOn } from './dependsOn'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { Formula } from '../../metadata/domain/domain.formula';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { AppHelper } from '../../app.helper';
import { SourceAttr } from '../../metadata/domain/domain.sourceAttr';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { FormulaService } from '../../metadata/services/formula.service';
import { AttributeIO } from '../../metadata/domainIO/domain.attributeIO';
@Component({
  selector: 'app-data-preparation-formula',
  styleUrls: ['./formula.component.css'],
  templateUrl: './formula.template.html'
})

export class FormulaComponent {
  DblClcikEditDetail: {};
  attributeinfo: {};
  lodeParamlist: any[];
  isSourceAtributeParamlist: boolean;
  sourcefunction: any;
  sourceexpression: any;
  sourceformula: any;
  sourcesimple: any;
  allExpression: any[];
  allFunction: any[];
  allFormula: any[];
  selectAttribute: any;
  isSourceAtributeSimple: boolean;
  isSourceAtributeFunction: boolean;
  isSourceAtributeExpression: boolean;
  isSourceAtributeFormula: boolean;
  isSourceAtributeDatapod: boolean;
  formulajson: any;
  allNames: any[];
  dependsOn: DependsOn;
  dependsontype: any;
  allAttribute: any[];
  formulatype: any;
  selectVersion: any;
  dataDependsOn: Array<DataPodResource>;
  formulaarray: any[];
  attributes: Array<DataPreparationFormulaAttributes>;
  isSubmitEnable: any;
  breadcrumbDataFrom: any;
  id: any;
  version: any;
  mode: any;
  baseUrl: any;
  uuid: any;
  name: any;
  versions: any;
  createdBy: any;
  createdOn: any;
  tags: any;
  desc: any;
  active: any;
  published: any;
  depandsOnTypes: any;
  dataDependsOnName: any;
  formulaInfo: any;
  formulafuction: any;
  attributeTypes: any;
  allformuladepands: any;
  selectedVersion: Version;
  VersionList: Array<DropDownIO>;
  sourceattribute: any;
  msgs: any;
  isHomeEnable: boolean = false;
  isEditEnable: boolean = true;
  isGraphEnable: boolean = true;
  isRefreshEnable: boolean = false;
  showGraph: boolean = false
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  locked: any;
  sourceparamlist: any;

  constructor(config: AppConfig, public router: Router, private _commonService: CommonService, private activatedRoute: ActivatedRoute, private formulaService: FormulaService, private appHelper: AppHelper, private _service: MetaDataDataPodService, private _location: Location) {
    this.baseUrl = config.getBaseUrl();
    this.selectVersion = { "version": "" };
    this.formulajson = {};
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/formula"
    },
    {
      "caption": "Formula",
      "routeurl": "/app/list/formula"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
  }

  ngOnInit() {
    this.lodeParamlist = []
    this.formulaarray = [];
    this.formulajson["active"] = true;
    this.isSourceAtributeSimple = true;
    this.isSubmitEnable = true;
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
      }
    });
    this.attributeTypes = [
      { "text": "string", "caption": "string" },
      { "text": "datapod", "caption": "attribute" },
      { "text": "expression", "caption": "expression" },
      { "text": "formula", "caption": "formula" },
      { "text": "function", "caption": "function" },
      { "text": "paramlist", "caption": "paramlist" }
    ]
    this.selectAttribute = this.attributeTypes[0].text
    this.depandsOnTypes = ["datapod", "relation", 'dataset', 'rule', 'paramlist'];
    this.formulafuction = [
      { 'attributeType': 'simple', 'value': '+', 'class': 'formula_function btn' },
      { 'attributeType': 'simple', 'value': '-', 'class': 'formula_function btn' },
      { 'attributeType': 'simple', 'value': '*', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '/', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '(', 'class': 'formula_function btn' },
      { 'attributeType': 'simple', 'value': ')', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '%', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '=', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '<', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '>', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '<=', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': '>=', 'class': 'formula_function btn' },
      { "attributeType": "simple", "value": ",", "class": 'formula_button btn btn-icon-only ' },
      { 'attributeType': 'simple', 'value': 'AND', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'OR', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'SUM', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'MIN', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'MAX', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'COUNT', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'AVG', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'CASE', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'WHEN', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'ELSE', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'END', 'class': 'formula_function btn ' },
      { 'attributeType': 'simple', 'value': 'THEN', 'class': 'formula_function btn ' },
      { "attributeType": "simple", "value": "IN", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "ORDER BY", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "PARTITION BY", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "BETWEEN", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "!=", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "ASC", "class": "formula_button btn " },
      { "attributeType": "simple", "value": "DESC", "class": "formula_button btn  " },
    ];
    this.getParamByApp();
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  public goBack() {
    this._location.back();
  }
  clear() {
    this.formulaarray = [];
  }

  undo() {
    this.formulaarray.splice(this.formulaarray.length - 1, 1);
  }

  addAttribute() {
    var len = this.formulaarray.length;
    if (this.DblClcikEditDetail != null) {
      len = this.DblClcikEditDetail["index"];
    }
    var data = { attributeType: "", value: "", ref: { uuid: "" }, attributeId: "", id: "", formulaType: "", category: "" }
    if (this.selectAttribute == "datapod") {
      if (this.sourceattribute != null) {
        data.attributeType = "datapod"
        data.value = this.sourceattribute.label
        data.ref.uuid = this.sourceattribute.uuid
        data.attributeId = this.sourceattribute.attributeId;
        data.id = this.sourceattribute.uuid + "_" + this.sourceattribute.attributeId
        this.sourceattribute = null;
      }
    }

    else if (this.selectAttribute == "paramlist") {debugger
      // if (this.sourceattribute != null) {
        data.attributeType = "paramlist"
        data.value = this.sourceparamlist.label
        data.ref.uuid = this.sourceparamlist.uuid
        data.attributeId = this.sourceparamlist.attributeId;
        data.id = this.sourceparamlist.uuid + "_" + this.sourceparamlist.attributeId
        this.sourceparamlist = null;
      // }
    }

    else if (this.selectAttribute == "string") {
      data.attributeType = "string"
      data.value = this.sourcesimple
      this.sourcesimple = null;
    }

    else if (this.selectAttribute == "formula") {
      data.attributeType = "formula"
      data.value = this.sourceformula.label
      data.ref.uuid = this.sourceformula.uuid;
      data.formulaType = this.sourceformula.formulaType;
      this.sourceformula = null;
    }
    else if (this.selectAttribute == "expression") {
      data.attributeType = "formula"
      data.value = this.sourceexpression.label
      data.ref.uuid = this.sourceexpression.uuid;
      this.sourceexpression = null;
    }
    else if (this.selectAttribute == "function") {
      this._commonService.getLatestByUuid(this.sourcefunction.uuid, 'function').subscribe(
        response => {
          data.attributeType = this.attributeTypes[4].text
          data.value = response.functionInfo[0].name.toUpperCase();
          data.ref.uuid = this.sourcefunction.uuid;
          data.category = response.category
          this.sourcefunction = null;
        },
        error => console.log('Error :: ' + error)
      )
    }
    else {

    }
    //}
    this.formulaarray[len] = data;
    this.DblClcikEditDetail = null;
  }
  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid("formula", this.formulajson.uuid).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllVersionByUuid(response: BaseEntity[]) {
    this.VersionList = [];
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { 'label': '', 'uuid': '' }
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.VersionList[i] = ver;
    }
    console.log(JSON.stringify(this.VersionList));
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.dependsontype).subscribe(
      response => { this.OnSuccesgetAllLatest(response, false) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllLatest(response1, defaultValue) {
    if (defaultValue == true) {
      let dependsOn: DependsOn = new DependsOn();
      dependsOn["uuid"] = response1[0]["uuid"];
      dependsOn["label"] = response1[0]["label"]
      this.dependsOn = dependsOn;
    }
    let temp = []
    for (const n in response1) {
      let allname = {};
      allname["label"] = response1[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response1[n]['name'];
      allname["value"]["uuid"] = response1[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp;
    this.getAllAttributeBySource();
  }

  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.dependsontype).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  clearFormula() {
    this.formulaarray = []
    this.formulaInfo = {}
  }
  OnSuccesgetAllAttributeBySource(response) {
    let temp = []
    for (const n in response) {
      let allname1 = {};
      allname1["label"] = response[n]['dname'];
      allname1["value"] = {};
      allname1["value"]["label"] = response[n]['dname'];
      allname1["value"]["uuid"] = response[n]['uuid'];
      allname1["value"]["attributeId"] = response[n]['attributeId'];
      allname1["value"]["id"] = response[n]['id'];
      temp[n] = allname1;
    }
    //this.formulaarray=[]
    this.allAttribute = temp;
    // this.sourceattribute = this.allAttribute[0];
  }

  getAllFunctions() {
    this._commonService.getAllLatestFunction("function", "N").subscribe(
      response => { this.onSuccessFunction(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessFunction(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allFunction = temp
  }

  getAllExpression() {
    this._commonService.getExpressionByType(this.dependsOn.uuid, this.dependsontype).subscribe(
      response => { this.onSuccessExpression(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessExpression(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allExpression = temp
  }
  getAllFormula() {
    this._commonService.getFormulaByType(this.dependsOn.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormula(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["formulaType"] = response[n]['formulaType'];
      temp[n] = allname;
    }
    this.allFormula = temp;
    this.sourceformula = this.allFormula[0]
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'formula')
      .subscribe(
        response => {
          this.OnSuccesgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesgetOneByUuidAndVersion(response: Formula) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.uuid = response.uuid;
    this.formulajson = response;

    this.getAllVersionByUuid();

    const version: Version = new Version();
    version.label = response.version
    version.uuid = response.uuid;
    this.selectedVersion = version;

    this.createdBy = response.createdBy.ref.name;

    if (response.tags != null) {
      this.tags = response.tags;
    }

    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.active = this.appHelper.convertStringToBoolean(response.active);
    this.locked = this.appHelper.convertStringToBoolean(response.locked);

    this.dependsontype = response.dependsOn.ref.type

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response.dependsOn.ref.name;
    dependOnTemp.uuid = response.dependsOn.ref.uuid;
    this.dependsOn = dependOnTemp

    this.formulaarray = [];
    const functionArray = ['+', '-', '/', '%', '*', '(', ')', '=', '<=', '>=', '<', '>', 'sum', 'max', 'mix', 'count', 'avg', 'case', 'when', 'else', 'end', 'then'];

    for (var i = 0; i < response.formulaInfo.length; i++) {
      var formulainfo = new SourceAttr();
      // attributeId: Number;
      // value: String;
      // attributeName: String;
      // attributeType: String;
      // ref: MetaIdentifier;

      //   private MetaIdentifier ref;
      //   private Integer attributeId;
      // /*	private MetaIdentifierHolder condition;*/
      //   private String value;
      //   private String attributeName;
      //   private String attributeType;


      if (response.formulaInfo[i].ref.type == "simple") {
        if (functionArray.indexOf(response.formulaInfo[i].value.toLowerCase()) > -1) {
          formulainfo.attributeType = response.formulaInfo[i].ref.type;
        }
        else {
          formulainfo.attributeType = "string"
        }
        formulainfo.value = response.formulaInfo[i].value;
      }
      else if (response.formulaInfo[i].ref.type == "datapod" || response.formulaInfo[i].ref.type == "dataset" || response.formulaInfo[i].ref.type == "paramlist") {
        debugger
        formulainfo.attributeType = response.formulaInfo[i].ref.type;

          let ref1 = new MetaIdentifier();
          ref1.uuid = response.formulaInfo[i].ref.uuid;
          formulainfo.ref = ref1;
        formulainfo.attributeId = response.formulaInfo[i].attributeId;
        formulainfo["id"] = response.formulaInfo[i].ref.uuid + "_" + response.formulaInfo[i].attributeId;
        formulainfo.value = response.formulaInfo[i].ref.name + "." + response.formulaInfo[i].attributeName;

        //formulainfo["value"] = response.formulaInfo[i].value;
      }
      else {
        if (response.formulaInfo[i].ref.type == "function") {
          if (response.formulaInfo[i].ref.name != null) {
            formulainfo.value = response.formulaInfo[i].ref.name.split("(")[0].toUpperCase();
          }
        }
        else {
          formulainfo.value = response.formulaInfo[i].ref.name;
        }
        formulainfo.attributeType = response.formulaInfo[i].ref.type;
        let ref1 = new MetaIdentifier();
        ref1.uuid = response.formulaInfo[i].ref.uuid;
        formulainfo.ref = ref1;
      }
      this.formulaarray[i] = formulainfo;
    }
    this.getAllLatest();
  }

  addData(data) {
    const aggrfun = ['SUM', 'MIN', 'MAX', 'COUNT', 'AVG'];
    const len = this.formulaarray.length;
    if (aggrfun.indexOf(data.value) > -1) {
      const api_url = this.baseUrl + 'metadata/getFunctionByFunctionInfo?type=function&action=view&functionInfo=' + data.value.toLowerCase();
      const getFunctionByFunctionInfo = this._service.getAllLatest(api_url).subscribe(
        response => { this.onSuccessGetFunctionByFunctionInfo(response) },
        error => console.log('Error :: ' + error)
      )
    }
    else {
      this.formulaarray[len] = data;
    }
  }

  onChangeAttribute(type) {
    if (type == "string") {
      this.isSourceAtributeSimple = true;
      this.isSourceAtributeDatapod = false;
      this.isSourceAtributeFormula = false;
      this.isSourceAtributeExpression = false;
      this.isSourceAtributeFunction = false;
      this.isSourceAtributeParamlist = false;
    }
    else if (type == "datapod") {
      this.isSourceAtributeSimple = false;
      this.isSourceAtributeDatapod = true;
      this.isSourceAtributeFormula = false;
      this.isSourceAtributeExpression = false;
      this.isSourceAtributeFunction = false;
      this.isSourceAtributeParamlist = false;
      this.getAllAttributeBySource()
    }
    else if (type == "formula") {
      this.isSourceAtributeSimple = false;
      this.isSourceAtributeDatapod = false;
      this.isSourceAtributeFormula = true;
      this.isSourceAtributeExpression = false;
      this.isSourceAtributeFunction = false;
      this.isSourceAtributeParamlist = false;
      this.getAllFormula();
    }
    else if (type == "expression") {
      this.isSourceAtributeSimple = false;
      this.isSourceAtributeDatapod = false;
      this.isSourceAtributeFormula = false;
      this.isSourceAtributeExpression = true;
      this.isSourceAtributeFunction = false;
      this.isSourceAtributeParamlist = false;
      this.getAllExpression();
    }
    else if (type == "function") {
      this.isSourceAtributeSimple = false;
      this.isSourceAtributeDatapod = false;
      this.isSourceAtributeFormula = false;
      this.isSourceAtributeExpression = false;
      this.isSourceAtributeFunction = true;
      this.isSourceAtributeParamlist = false;
      this.getAllFunctions();
    }
    else if (type == "paramlist") {
      this.isSourceAtributeSimple = false;
      this.isSourceAtributeDatapod = false;
      this.isSourceAtributeFormula = false;
      this.isSourceAtributeExpression = false;
      this.isSourceAtributeFunction = false;
      this.isSourceAtributeParamlist = true;
      this.getAllParam()
      // .getParamByParamList(this.allformuladepands.defaultoption.uuid,"paramlist").then(function (response) { onSuccessParamlist(response.data) });
      // var onSuccessParamlist = function (response) {
      // 	if(this.lodeParamlist && this.lodeParamlist.length ==0){
      // 		this.lodeParamlist = response;
      // 	}else if(this.lodeParamlist.length >0  && this.lodeParamlist[0].uuid != response[0].uuid){
      // 		for(var i=0;i<response.length;i++){
      // 			var paramjson={}
      // 			var paramsjson = {};
      // 			paramsjson.uuid = response[i].uuid;
      // 			paramsjson.name = response[i].name 
      // 			paramsjson.dname = response[i].datapodname+"."+response[i].dname;
      // 			paramsjson.attributeId = response[i].attributeId;
      // 			paramsjson.attrType = response[i].paramType;
      // 			paramsjson.paramName = response[i].paramName;
      // 			paramsjson.caption = "formula."+paramsjson.paramName;
      // 			this.lodeParamlist.push(paramsjson);
    }
  }

  getAllParam() {
    debugger
    this._commonService.getParamByParamList(this.dependsOn.uuid, "paramlist")
      .subscribe(
        response => {
          debugger
          // if(this.lodeParamlist && this.lodeParamlist.length ==0){
          //   this.lodeParamlist = response;
          // }else if(this.lodeParamlist.length >0  && this.lodeParamlist[0].uuid != response[0].uuid){


          for (var i = 0; i < response.length; i++) {
            var paramjson = {}
            var paramsjson = {};
            paramsjson["uuid"] = response[i].uuid;
            paramsjson["name"] = response[i].name
            paramsjson["dname"] = response[i].datapodname + "." + response[i].dname;
            paramsjson["attributeId"] = response[i].attributeId;
            paramsjson["attrType"] = response[i].paramType;
            paramsjson["paramName"] = response[i].paramName;
            paramsjson["caption"] = "formula." + paramsjson["paramName"];
            this.lodeParamlist.push(paramsjson);
          }
          // }
        },
        error => console.log("Error :: " + error));
  }

  getParamByApp() {
    this.formulaService.getParamByApp("", "application")
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error::" + error))
  }

  onSuccessgetParamByApp(response) {
    this.lodeParamlist = [];
    if (response.length > 0) {
      let temp = [];
      for (const i in response) {
        let attributeObj = new AttributeIO();
        attributeObj.label = "paramlist_app." + response[i].paramName;
        attributeObj.value = {};
        attributeObj.value.uuid = response[i].ref.uuid;
        attributeObj.value.attributeId = response[i].paramId;
        attributeObj.value.label = "paramlist_app." + response[i].paramName;
        temp[i] = attributeObj
      }
      this.lodeParamlist = temp;
      this.sourceparamlist = this.lodeParamlist[0]
      
      // var paramsArray = [];
      // for (var i = 0; i < response.length; i++) {
      //   paramsjson.uuid = response[i].ref.uuid;
      //   paramsjson.name = response[i].ref.name + "." + response[i].paramName;
      //   paramsjson.dname = response[i].ref.name + "." + response[i].paramName;
      //   paramsjson.attributeId = response[i].paramId;
      //   paramsjson.attrType = response[i].paramType;
      //   paramsjson.paramName = response[i].paramName;
      //   paramsjson.caption = "app." + paramsjson.paramName;
      //   paramsArray[i] = paramsjson
      // }
      // this.lodeParamlist = paramsArray;
    }
  }

  onSuccessGetFunctionByFunctionInfo(response) {
    const len = this.formulaarray.length;
    const fundataArr: Array<DataPreparationFormulaInfo> = [];
    this.formulaarray.push(new DataPreparationFormulaInfo(
      'function', response[0]['uuid'], response[0]['category'], response[0]['functionInfo'][0].name.toUpperCase()
    ));
  }

  selectType() {
    this._commonService.getAllLatest(this.dependsontype).subscribe(
      response => {
        this.onSuccesgetAllLatest(response)
      },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccesgetAllLatest(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp
  }

  changeType() {
    this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.dependsontype).subscribe(
      response => {
        this.OnSuccesgetAllAttributeBySource(response)
      },
      error => console.log('Error :: ' + error)
    )
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  submitFormula() {
    this.isSubmitEnable = true;
    let formulaSubmitJson = {};
    let aggrfun = ["sum", "min", "max", "count", "avg"];
    formulaSubmitJson["uuid"] = this.formulajson.uuid
    formulaSubmitJson["name"] = this.formulajson.name

    formulaSubmitJson["tags"] = this.tags;

    formulaSubmitJson["desc"] = this.formulajson.desc
    let dependsOn = {};
    let ref = {}
    ref["type"] = this.dependsontype
    ref["uuid"] = this.dependsOn.uuid
    dependsOn["ref"] = ref;
    formulaSubmitJson["dependsOn"] = dependsOn;

    formulaSubmitJson["active"] = this.active == true ? 'Y' : "N";
    formulaSubmitJson["published"] = this.published == true ? 'Y' : "N";
    formulaSubmitJson["locked"] = this.locked == true ? 'Y' : "N";

    let formulaArray = [];
    if (this.formulaarray.length > 0) {
      for (let i = 0; i < this.formulaarray.length; i++) {
        let formulainfo = {}
        let ref = {};

        if (this.formulaarray[i].attributeType == "simple") {
          if (aggrfun.indexOf(this.formulaarray[i].value.toLowerCase()) > -1) {
            formulaSubmitJson["formulaType"] = "aggr"
          }
          ref["type"] = this.formulaarray[i].attributeType;
          formulainfo["ref"] = ref;
          formulainfo["value"] = this.formulaarray[i].value;
        }
        else if (this.formulaarray[i].attributeType == "string") {
          ref["type"] = "simple";
          formulainfo["ref"] = ref;
          formulainfo["value"] = this.formulaarray[i].value;
        }

        else if (this.formulaarray[i].attributeType == "datapod" || this.formulaarray[i].attributeType == "dataset" || this.formulaarray[i].attributeType == "paramlist") {
          // if (this.sourceattribute != null) {
          //   data.attributeType = "datapod"
          //   data.value = this.sourceattribute.label
          //   data.ref.uuid = this.sourceattribute.uuid
          //   data.attributeId = this.sourceattribute.attributeId;
          //   data.id=this.sourceattribute.uuid+"_"+this.sourceattribute.attributeId
          //   this.sourceattribute = null;
          // }
          if (this.formulatype == "dataset") {
            ref["type"] = "dataset";
            ref["uuid"] = this.formulaarray[i].ref.uuid;
          }
          else if (this.formulatype == "paramlist") {
            ref["type"] = "paramlist";
            ref["uuid"] = this.formulaarray[i].ref.uuid;
          }
          else {
            ref["type"] = this.formulaarray[i].attributeType;
            ref["uuid"] = this.formulaarray[i].ref.uuid;
          }
          formulainfo["ref"] = ref;
          formulainfo["attributeId"] = this.formulaarray[i].attributeId;
          //// formulainfo["value"] = this.formulaarray[i].value;
        }

        else {
          if (this.formulaarray[i].attributeType == "formula") {
            if (formulaSubmitJson["formulaType"] != "aggr")
              formulaSubmitJson["formulaType"] = this.formulaarray[i].formulatype;
          }
          if (this.formulaarray[i].attributeType == "function") {
            //alert(this.formulainfoarray[i].category)
            if (this.formulaarray[i].category == "aggregate") {
              formulaSubmitJson["formulaType"] = "aggr"
            }
          }
          ref["type"] = this.formulaarray[i].attributeType;
          ref["uuid"] = this.formulaarray[i].ref.uuid;
          formulainfo["ref"] = ref;

        }
        formulaArray[i] = formulainfo;
      }
      formulaSubmitJson["formulaInfo"] = formulaArray;

      console.log(JSON.stringify(formulaSubmitJson))

      this._commonService.submit("formula", formulaSubmitJson).subscribe(
        response => { this.onSuccessSubmit(response) },
        error => console.log('error', +error)
      )
    }
  }

  onSuccessSubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Formula Submitted Successfully' });
    this.goBack();
    console.log('final response is' + JSON.stringify(response));
  }

  enableEdit(uuid, version) {
    this.isRefreshEnable = false;
    this.router.navigate(['app/dataPreparation/formula', uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/formula', uuid, version, 'true']);
  }
  onDbclcikEdit(type, index) {
    // if(!this.isEdit && !this.isAdd ){
    // 	return false;
    // }
    //console.log(this.formulainfoarray[index]);
    if (["datapod", 'dataset', 'rule', 'paramlist'].indexOf(type) != -1) {
      this.attributeinfo = {};
      var type1 = {};
      type1["text"] = type;
      this.selectAttribute = type;
      var attributeInfo = {}
      attributeInfo["uuid"] = this.formulaarray[index].uuid
      attributeInfo["type"] = this.formulaarray[index].type
      attributeInfo["id"] = this.formulaarray[index].id
      attributeInfo["attributeId"] = this.formulaarray[index].attrId;
      attributeInfo["dname"] = this.formulaarray[index].value;
      this.sourceattribute = attributeInfo;
      //setTimeout(function(){ this.sourceattribute=attributeInfo; },10);
      this.DblClcikEditDetail = {};
      this.DblClcikEditDetail["isEdit"] = true;
      this.DblClcikEditDetail["index"] = index;
    }
    else if (type == "string" || type == "simple") {
      var type1 = {};
      type1["text"] = "string"//this.formulainfoarray[index].type;
      this["attributeType"] = type1;
      this.sourcesimple = this.formulaarray[index].value;
      this.DblClcikEditDetail = {};
      this.DblClcikEditDetail["isEdit"] = true;
      this.DblClcikEditDetail["index"] = index;
    } else {
      var type1 = {};
      type1["text"] = this.formulaarray[index].type;
      this.selectAttribute = type;
      this.sourcefunction = {}
      this.sourcefunction.uuid = this.formulaarray[index].uuid;
      this.sourcefunction.name = this.formulaarray[index].name;
      this.DblClcikEditDetail = {};
      this.DblClcikEditDetail["isEdit"] = true;
      this.DblClcikEditDetail["index"] = index;
    }
    this.onChangeAttribute(type1["text"]);
  };

  showMainPage(uuid, version) {
    this.isHomeEnable = false;
    this.showGraph = false;
  }
  showFormulaGraph(uuid, version) {
    console.log("Show Formula Graph call... ");
    this.showGraph = true;
    this.isHomeEnable = true;
  }

}
