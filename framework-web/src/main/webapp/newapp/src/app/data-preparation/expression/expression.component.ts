import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { CommonService } from './../../metadata/services/common.service';
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Version } from '../../shared/version'
import { SelectItem } from 'primeng/primeng';
import { DependsOn } from './dependsOn'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { Expression } from '../../metadata/domain/domain.expression';
import { ExpMetIO } from '../../metadata/domainIO/domain.expMetIO';
import { AttributeIO } from '../../metadata/domainIO/domain.attributeIO';
import { ExpNotMetIO } from '../../metadata/domainIO/domain.notMetTypeIO';
import { ExpressionInfoIO } from '../../metadata/domainIO/domain.expressionInfoIO';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { MetaType } from '../../metadata/enums/metaType';
@Component({
  selector: 'app-expression',
  templateUrl: './expression.template.html',
  styleUrls: ['./expression.component.css']
})
export class ExpressionComponent implements OnInit {
  @Input()
  childMessage: string;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  allNames: SelectItem[] = [];
  dependsOn: DependsOn
  msgs: any[];
  selectallrow: boolean;
  matType: string[];
  expressionmetnotmat: any;
  rhsType: { "text": string; "caption": string; }[];
  allFormula: SelectItem[] = [];
  allAttribute: SelectItem[] = [];
  lhsType: { "text": string; "caption": string; }[];
  expressionTableArray: any[];
  selectVersion: any;
  versions: any[];
  showExpression: boolean;
  breadcrumbDataFrom: any;
  dependsOnType: { 'value': string; 'label': string; }[];
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  expression: any;
  depends: any;
  allName: any;
  active: any;
  published: any;
  logicalOperators: any;
  operators: any;
  isSubmitEnable: any;
  isDependonDisabled: boolean;
  isHomeEnable: boolean = false;
  showGraph: boolean;
  isRefreshEnable: boolean = true;
  metaType = MetaType;

  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.uuid = '';
    this.isDependonDisabled = false;
    this.showExpression = true;
    this.isHomeEnable = false
    this.showGraph = false
    this.expression = {};
    this.tags = [];
    this.expression["active"] = true
    this.isSubmitEnable = true;
    this.expressionmetnotmat = { "metinfo": {},"notmetinfo" : {}};
    this.expressionmetnotmat.metinfo = {};
    this.expressionmetnotmat.notmetinfo = {}
    this.dependsOn = { 'uuid': "", "label": "" };
    this.operators = ["=", "<", ">"];
    this.logicalOperators = ["", "AND", "OR"]
    this.selectVersion = { "version": "" };
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/expression"
    },
    {
      "caption": "Expression",
      "routeurl": "/app/list/expression"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dependsOnType = [
      { 'value': 'relation', 'label': 'relation' },
      { 'value': 'dataset', 'label': 'dataset' },
      { 'value': 'datapod', 'label': 'datapod' }
    ];
    this.lhsType = [
      { "text": "string", "caption": "string" },
      { "text": "datapod", "caption": "attribute" },
      { "text": "formula", "caption": "formula" }]
    this.rhsType = [
      { "text": "string", "caption": "string" },
      { "text": "datapod", "caption": "attribute" },
      { "text": "formula", "caption": "formula" }]
    this.matType = ["string", 'formula'];
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.depends = params['depends'];
      this.dependsOn["uuid"] = params['dependsOnUuid'];
      this.dependsOn["label"] = params['dependsOnLabel'];
      if (this.depends != null) {
        this._commonService.getAllLatest(this.depends).subscribe(
          response => {
            this.isDependonDisabled = true;
            this.OnSuccesgetAllLatest(response, false)
          },
          error => console.log('Error :: ' + error)
        )
      }
      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
      }
      else {
        this.expressionmetnotmat.notmetinfo.isnotmetlhsSimple = true;
        this.expressionmetnotmat.metinfo.ismetlhsSimple = true;
      }
    })
  }

  ngOnInit() {
    // this.activatedRoute.params.subscribe((params: Params) => {
    //   this.id = params['id'];
    //   this.version = params['version'];
    //   this.mode = params['mode'];
    //   if (this.mode !== undefined) {
    //     this.getOneByUuidAndVersion(this.id, this.version);
    //     this.getAllVersionByUuid();
    //   }
    //   else {
    //     this.expressionmetnotmat["notmetinfo"]["isnotmetlhsSimple"] = true
    //     this.expressionmetnotmat["metinfo"]["ismetlhsSimple"] = true
    //   }
    // })
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);

  }

  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, this.metaType.EXPRESSION)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.EXPRESSION, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response : Expression) {
    this.expression = response;
    this.createdBy = response.createdBy.ref.name;
    // var tags = [];
    // if (response.tags != null) {
    //   for (var i = 0; i < response.tags.length; i++) {
    //     var tag = {};
    //     tag['value'] = response.tags[i];
    //     tag['display'] = response.tags[i];
    //     tags[i] = tag

    //   }//End For
    //   this.expression.tags = tags;
    // }//End If

    if (response.tags != null) {
      this.expression.tags = response.tags;
    }

    this.expression.published = response.published == 'Y' ? true : false
    this.expression.active = response.active == 'Y' ? true : false

    let version: Version = new Version();
    this.uuid = response.uuid
    version.label = response.version
    version.uuid = response.uuid;
    this.selectedVersion = version

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response.dependsOn.ref.name;
    dependOnTemp.uuid = response.dependsOn.ref.uuid;
    this.dependsOn = dependOnTemp

    this.breadcrumbDataFrom[2].caption = this.expression.name;

    this.depends = response.dependsOn.ref.type;
    this.dependsOn.uuid = response.dependsOn.ref.uuid;
    
    let expressionjson = {"expression":{}, "expressioninfo":{}};
    expressionjson.expression = response;

    let expressionListArray = [];
    let expression = { metinfo:{}, notmetinfo:{} };
    let metinfo = new ExpMetIO();
    let notmetinfo = new ExpNotMetIO();
    if (response.match.ref.type == "simple") {
      metinfo.mettype = "string";
      metinfo.ismetlhsSimple = true;
      metinfo.ismetFormula = false;
      metinfo.metlhsvalue = response.match.value;
    }
    else if (response.match.ref.type == "formula") {
      let metformula = new AttributeIO();
      metinfo.mettype = "formula";
      metinfo.ismetFormula = true;
      metinfo.ismetlhsSimple = false;
      metformula.uuid = response.match.ref.uuid;
      metformula.name = response.match.ref.name;
      metinfo.metFormula = metformula;
    }

    if (response.noMatch.ref.type == "simple") {
      notmetinfo.notmettype = "string";
      notmetinfo.isnotmetlhsSimple = true;
      notmetinfo.isnotmetFormula = false;
      notmetinfo.notmetlhsvalue = response.noMatch.value;
    }
    else if (response.noMatch.ref.type == "formula") {
      let notmetformula = new AttributeIO();;
      notmetinfo.notmettype = "formula";
      notmetinfo.isnotmetFormula = true;
      notmetinfo.isnotmetlhsSimple = false;
      notmetformula.uuid = response.noMatch.ref.uuid;
      notmetformula.name = response.noMatch.ref.name;
      notmetinfo.notmetFormula = notmetformula
    }
    expression.metinfo = metinfo;
    expression.notmetinfo = notmetinfo;
    this.expressionmetnotmat = expression

    let exressionArray = [];
    for (let i = 0; i < response.expressionInfo.length; i++) {
      let expressioninfo = new ExpressionInfoIO();
      expressioninfo.logicalOperator = response.expressionInfo[i].logicalOperator;
      expressioninfo.operator = response.expressionInfo[i].operator;
      if (response.expressionInfo[i].operand[0].ref.type == "simple") {
        let obj = {"text":"", "caption":""};
        obj.text = "string"
        obj.caption = "string"
        expressioninfo.lhstype = obj;
        expressioninfo.islhsSimple = true;
        expressioninfo.islhsDatapod = false;
        expressioninfo.islhsFormula = false;
        expressioninfo.lhsvalue = response.expressionInfo[i].operand[0].value;
      }
      else if (response.expressionInfo[i].operand[0].ref.type == "datapod" || response.expressionInfo[i].operand[0].ref.type == "dataset") {
        let lhsdatapodAttribute = new AttributeIO();
        let obj = {"text":"", "caption":""};
        obj.text = "datapod";
        obj.caption = "attribute";
        expressioninfo.lhstype = obj;
        expressioninfo.islhsSimple = false;
        expressioninfo.islhsFormula = false
        expressioninfo.islhsDatapod = true;
        lhsdatapodAttribute.label = response.expressionInfo[i].operand[0].ref.name + "." + response.expressionInfo[i].operand[0].attributeName;
        lhsdatapodAttribute.id = response.expressionInfo[i].operand[0].ref.uuid + "_" + response.expressionInfo[i].operand[0].attributeId;
        lhsdatapodAttribute.uuid = response.expressionInfo[i].operand[0].ref.uuid;
        lhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[0].ref.name;
        lhsdatapodAttribute.name = response.expressionInfo[i].operand[0].attributeName;
        lhsdatapodAttribute.dname = response.expressionInfo[i].operand[0].ref.name + "." + response.expressionInfo[i].operand[0].attributeName;
        lhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[0].attributeId;

        expressioninfo.lhsdatapodAttribute = lhsdatapodAttribute;
      }
      else if (response.expressionInfo[i].operand[0].ref.type == "formula") {
        let lhsformula = new AttributeIO();
        let obj = {"text":"", "caption":""};
        obj.text = "formula"
        obj.caption = "formula"
        expressioninfo.lhstype = obj;
        expressioninfo.islhsFormula = true;
        expressioninfo.islhsSimple = false;
        expressioninfo.islhsDatapod = false;
        lhsformula.uuid = response.expressionInfo[i].operand[0].ref.uuid;
        lhsformula.name = response.expressionInfo[i].operand[0].ref.name;
        lhsformula.label = response.expressionInfo[i].operand[0].ref.name;
        expressioninfo.lhsformula = lhsformula;
      }
      if (response.expressionInfo[i].operand[1].ref.type == "simple") {
        let obj = {"text":"", "caption":""};
        obj.text = "string"
        obj.caption = "string"
        expressioninfo.rhstype = obj;
        expressioninfo.isrhsSimple = true;
        expressioninfo.isrhsDatapod = false;
        expressioninfo.isrhsFormula = false;
        expressioninfo.rhsvalue = response.expressionInfo[i].operand[1].value;
      }
      else if (response.expressionInfo[i].operand[1].ref.type == "datapod" || response.expressionInfo[i].operand[1].ref.type == "dataset") {
        let rhsdatapodAttribute = new AttributeIO();
        let obj = {"text":"", "caption":""};
        obj.text = "datapod"
        obj.caption = "attribute"
        expressioninfo.rhstype = obj;
        expressioninfo.isrhsSimple = false;
        expressioninfo.isrhsFormula = false
        expressioninfo.isrhsDatapod = true;
        rhsdatapodAttribute.id = response.expressionInfo[i].operand[1].ref.uuid + "_" + response.expressionInfo[i].operand[1].attributeId;
        rhsdatapodAttribute.uuid = response.expressionInfo[i].operand[1].ref.uuid;
        rhsdatapodAttribute.datapodname = response.expressionInfo[i].operand[1].ref.name;
        rhsdatapodAttribute.name = response.expressionInfo[i].operand[1].attributeName;
        rhsdatapodAttribute.dname = response.expressionInfo[i].operand[1].ref.name + "." + response.expressionInfo[i].operand[1].attributeName;
        rhsdatapodAttribute.label = response.expressionInfo[i].operand[1].ref.name + "." + response.expressionInfo[i].operand[1].attributeName;
        rhsdatapodAttribute.attributeId = response.expressionInfo[i].operand[1].attributeId;
        expressioninfo.rhsdatapodAttribute = rhsdatapodAttribute;
      }
      else if (response.expressionInfo[i].operand[1].ref.type == "formula") {
        let rhsformula = new AttributeIO();
        let obj = {"text":"", "caption":""};
        obj.text = "formula"
        obj.caption = "formula"
        expressioninfo.rhstype = obj;
        expressioninfo.isrhsFormula = true;
        expressioninfo.isrhsSimple = false;
        expressioninfo.isrhsDatapod = false;
        rhsformula.uuid = response.expressionInfo[i].operand[1].ref.uuid;
        rhsformula.name = response.expressionInfo[i].operand[1].ref.name;
        rhsformula.label = response.expressionInfo[i].operand[1].ref.name;
        expressioninfo.rhsformula = rhsformula;
      }
      exressionArray[i] = expressioninfo
    }
    expressionjson.expression = expression;
    expressionjson.expressioninfo = exressionArray;

    this.expressionTableArray = exressionArray

    this._commonService.getAllLatest(this.depends).subscribe(
      response => { this.OnSuccesgetAllLatest(response, false) },
      error => console.log('Error :: ' + error)
    )
  }
  public goBack() {
    this._location.back();
    //this.router.navigate(['app/list/expression']);
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

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  OnSuccesgetAllLatest(response1, defaultValue) {
    if (defaultValue == true) {
      let dependsOn: DependsOn = new DependsOn();;
      dependsOn.uuid = response1[0].uuid;
      dependsOn.label = response1[0].label;
      this.dependsOn = dependsOn;
    }
    let temp = []
    for (const n in response1) {
      let allname = new DropDownIO();
      allname.label = response1[n].name;
      allname.value = {"label":"","uuid":""};
      allname.value.label = response1[n].name;
      allname.value.uuid = response1[n].uuid;
      temp[n] = allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource(false, null, null);
  }
  getAllAttributeBySource(defaultValue, index, type) {
    this._commonService.getAllAttributeBySource(this.dependsOn.uuid, this.depends).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response2, defaultValue, index, type) {
    let temp = []
    for (const n in response2) {
      let allname1 = new AttributeIO();
      allname1.label = response2[n].dname;
      allname1.value = {};
      allname1.value.label = response2[n].dname;
      allname1.value.id = response2[n].id;
      temp[n] = allname1;
    }
    this.allAttribute = temp;
    if (defaultValue == true && index != null) {
      let lhsdatapodAttribute = {"label":"","id":""}
      lhsdatapodAttribute.label = this.allAttribute[0].label;
      lhsdatapodAttribute.id = this.allAttribute[0].value.id;
      if (type == 'lhs') {
        this.expressionTableArray[index].lhsdatapodAttribute = lhsdatapodAttribute;
      } else {
        this.expressionTableArray[index].rhsdatapodAttribute = lhsdatapodAttribute;
      }
    }

  }
  getAllFormula(defaultValue, index, type) {
    this._commonService.getFormulaByType(this.dependsOn.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormula(response, defaultValue, index, type) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response, defaultValue, index, type) {
    let temp = []
    if (defaultValue == true) {
      let sourceformula = {"label":"","uuid":""};
      sourceformula.label = response[0].name;
      sourceformula.uuid = response[0].uuid;
      if (type == 'lhs') {
        this.expressionTableArray[index].lhsformula = sourceformula;
      } else {
        this.expressionTableArray[index].rhsformula = sourceformula;

      }
    }
    for (const n in response) {
      let allname1 = new DropDownIO();
      allname1.label = response[n].name;
      allname1.value = {"label":"","uuid":""};
      allname1.value.label = response[n].name;
      allname1.value.uuid = response[n].uuid;
      temp[n] = allname1;
    }
    this.allFormula = temp
  }
  onChangeActive(event) {
    if (event === true) {
      this.expression.active = 'Y';
    }
    else {
      this.expression.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.expression.published = 'Y';
    }
    else {
      this.expression.published = 'N';
    }
  }
  selectType() {
    this._commonService.getAllLatest(this.depends).subscribe(
      response => {
        this.OnSuccesgetAllLatest(response, true)
      },
      error => console.log('Error :: ' + error)
    )

  }
  changeType() {
    this.getAllAttributeBySource(false, null, null)
    this.getAllFormula(false, null, null)
  }
  selectmetlhsType(type) {
    //alert(type)
    if (type == "string") {
      console.log(JSON.stringify(this.expressionmetnotmat));
      this.expressionmetnotmat.metinfo.ismetlhsSimple = true;
      this.expressionmetnotmat.metinfo.metlhsvalue = "''"
      this.expressionmetnotmat.metinfo.ismetFormula = false;
    }
    else if (type == "formula") {
      this.expressionmetnotmat.metinfo.ismetlhsSimple = false;
      this.expressionmetnotmat.metinfo.ismetFormula = true;
      this.getAllFormula(false, null, null)
    }

  }
  selectnotmmetlhsType(type) {
    if (type == "string") {
      this.expressionmetnotmat.notmetinfo.isnotmetlhsSimple = true;
      this.expressionmetnotmat.notmetinfo.isnotmetFormula = false;
      this.expressionmetnotmat.notmetinfo.notmetlhsvalue = "''"
    }
    else if (type == "formula") {
      this.expressionmetnotmat.notmetinfo.isnotmetlhsSimple = false;
      this.expressionmetnotmat.notmetinfo.isnotmetFormula = true;
    }
  }
  selectlhsType(type, index) {
    if (type == "string") {
      this.expressionTableArray[index].islhsSimple = true;
      this.expressionTableArray[index].islhsDatapod = false;
      this.expressionTableArray[index].lhsvalue = "''";
      this.expressionTableArray[index].islhsFormula = false;
    }
    else if (type == "datapod") {

      this.expressionTableArray[index].islhsSimple = false;
      this.expressionTableArray[index].islhsDatapod = true;
      this.expressionTableArray[index].islhsFormula = false;
      this.getAllAttributeBySource(true, index, "lhs");
    }
    else if (type == "formula") {

      this.expressionTableArray[index].islhsFormula = true;
      this.expressionTableArray[index].islhsSimple = false;
      this.expressionTableArray[index].islhsDatapod = false;
      //this.expressionTableArray[index].sourceformula={}
      this.getAllFormula(true, index, 'lhs')
    }


  }
  selectrhsType(type, index) {

    if (type == "string") {
      this.expressionTableArray[index].isrhsSimple = true;
      this.expressionTableArray[index].isrhsDatapod = false;
      this.expressionTableArray[index].isrhsFormula = false;
      this.expressionTableArray[index].rhsvalue = "''";
    }
    else if (type == "datapod") {

      this.expressionTableArray[index].isrhsSimple = false;
      this.expressionTableArray[index].isrhsDatapod = true;
      this.expressionTableArray[index].isrhsFormula = false;
      this.expressionTableArray[index].sourceattribute = {}
      this.getAllAttributeBySource(true, index, 'rhs');
    }
    else if (type == "formula") {

      this.expressionTableArray[index].isrhsFormula = true;
      this.expressionTableArray[index].isrhsSimple = false;
      this.expressionTableArray[index].isrhsDatapod = false;
      //this.expressionTableArray[index].sourceformula={}
      this.getAllFormula(true, index, 'rhs')
    }

  }
  selectAllRow() {
    this.expressionTableArray.forEach(expression => {
      expression.selected = this.selectallrow;
    });
  }

  addRow() {
    if (this.expressionTableArray == null) {

      this.expressionTableArray = [];
    }
    var expessioninfo = new ExpressionInfoIO()
    expessioninfo.islhsDatapod = false;
    expessioninfo.islhsFormula = false;
    expessioninfo.islhsSimple = true;
    expessioninfo.isrhsDatapod = false;
    expessioninfo.isrhsFormula = false;
    expessioninfo.isrhsSimple = true;
    expessioninfo.operator = this.operators[0]
    expessioninfo.lhstype = this.lhsType[0]
    expessioninfo.rhstype = this.rhsType[0]
    expessioninfo.rhsvalue = "''";
    expessioninfo.lhsvalue = "''";
    expessioninfo.logicalOperator = this.logicalOperators[0];
    this.expressionTableArray.splice(this.expressionTableArray.length, 0, expessioninfo);

  }
  removeRow() {
    let newDataList = [];
    this.selectallrow = false;
    this.expressionTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.expressionTableArray = newDataList;
  }
  submitExpression() {
    this.isSubmitEnable = true;
    var expressionjson = {}
    expressionjson["uuid"] = this.expression.uuid;
    expressionjson["name"] = this.expression.name;
    //expressionjson["active"]=this.expression.active;
    expressionjson["desc"] = this.expression.desc;
    //expressionjson["published"]=this.expression.published;
    expressionjson["active"] = this.expression.active == true ? 'Y' : "N"
    expressionjson["published"] = this.expression.published == true ? 'Y' : "N"
  
    expressionjson['tags'] = this.expression.tags

    var dependsOn = {};
    var ref = {};
    ref["type"] = this.depends;
    ref["uuid"] = this.dependsOn.uuid;
    dependsOn["ref"] = ref;
    expressionjson["dependsOn"] = dependsOn
    var met = {};
    var metref = {};
    if (this.expressionmetnotmat["metinfo"]["mettype"] == "string") {

      metref["type"] = "simple";
      met["ref"] = metref;
      met["value"] = this.expressionmetnotmat["metinfo"]["metlhsvalue"]
    }
    else if (this.expressionmetnotmat["metinfo"]["mettype"] == "formula") {

      metref["type"] = "formula";
      metref["uuid"] = this.expressionmetnotmat["metinfo"]["metformula"].uuid
      met["ref"] = metref;

    }
    expressionjson["match"] = met
    var notMet = {};
    var notMetref = {};
    if (this.expressionmetnotmat["notmetinfo"]["notmettype"] == "string") {

      notMetref["type"] = "simple";
      notMet["ref"] = notMetref;
      notMet["value"] = this.expressionmetnotmat["notmetinfo"]["notmetlhsvalue"]
    }
    else if (this.expressionmetnotmat["notmetinfo"]["notmettype"] == "formula") {

      notMetref["type"] = "formula";
      notMetref["uuid"] = this.expressionmetnotmat["notmetinfo"]["notmetformula"].uuid
      notMet["ref"] = notMetref;

    }
    expressionjson["noMatch"] = notMet

    var expressioninfoArray = [];
    if (this.expressionTableArray.length > 0) {
      for (var i = 0; i < this.expressionTableArray.length; i++) {
        var expressioninfo = {};
        var operand = []
        var lhsoperand = {}
        var lhsref = {}
        var rhsoperand = {}
        var rhsref = {};
        expressioninfo["logicalOperator"] = this.expressionTableArray[i].logicalOperator;
        expressioninfo["operator"] = this.expressionTableArray[i].operator;
        if (this.expressionTableArray[i].lhstype.text == "string") {

          lhsref["type"] = "simple";
          lhsoperand["ref"] = lhsref;
          lhsoperand["value"] = this.expressionTableArray[i].lhsvalue;
        }
        else if (this.expressionTableArray[i].lhstype.text == "datapod") {
          if (this.depends == "dataset") {
            lhsref["type"] = "dataset";

          }
          else {
            lhsref["type"] = "datapod";
          }
          let uuid = this.expressionTableArray[i].lhsdatapodAttribute.id.split("_")[0]
          var attrid = this.expressionTableArray[i].lhsdatapodAttribute.id.split("_")[1]
          lhsref["uuid"] = uuid
          //this.expressionTableArray[i].lhsdatapodAttribute.uuid;
          lhsoperand["ref"] = lhsref;
          lhsoperand["attributeId"] = attrid
        }
        else if (this.expressionTableArray[i].lhstype.text == "formula") {

          lhsref["type"] = "formula";
          lhsref["uuid"] = this.expressionTableArray[i].lhsformula.uuid;
          lhsoperand["ref"] = lhsref;
        }
        operand[0] = lhsoperand;
        if (this.expressionTableArray[i].rhstype.text == "string") {

          rhsref["type"] = "simple";
          rhsoperand["ref"] = rhsref;
          rhsoperand["value"] = this.expressionTableArray[i].rhsvalue;
        }
        else if (this.expressionTableArray[i].rhstype.text == "datapod") {
          if (this.depends == "dataset") {
            rhsref["type"] = "dataset";

          }
          else {
            rhsref["type"] = "datapod";
          }
          rhsref["uuid"] = this.expressionTableArray[i].rhsdatapodAttribute.uuid;

          rhsoperand["ref"] = rhsref;
          rhsoperand["attributeId"] = this.expressionTableArray[i].rhsdatapodAttribute.attributeId;
        }
        else if (this.expressionTableArray[i].rhstype.text == "formula") {

          rhsref["type"] = "formula";
          rhsref["uuid"] = this.expressionTableArray[i].rhsformula.uuid;
          rhsoperand["ref"] = rhsref;
        }
        operand[1] = rhsoperand;
        expressioninfo["operand"] = operand;
        expressioninfoArray[i] = expressioninfo
      }

    }
    expressionjson["expressionInfo"] = expressioninfoArray
    console.log(JSON.stringify(expressionjson))

    this._commonService.submit("expression", expressionjson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Expression Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }
  enableEdit(uuid, version) {
    this.isRefreshEnable = false;
    this.router.navigate(['app/dataPreparation/expression', uuid, version, 'false']);
  }
  // showview(uuid, version) {
  //   this.router.navigate(['app/dataPreparation/expression', uuid, version, 'true']);
  // }
  showMainPage(uuid, version) {
    this.isHomeEnable = false;
    this.showGraph = false;
  }

}