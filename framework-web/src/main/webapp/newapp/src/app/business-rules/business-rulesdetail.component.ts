import { AttributeRefHolder } from './../metadata/domain/domain.attributeRefHolder';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';
import { SelectItem } from 'primeng/primeng';
import { MessageService } from 'primeng/components/common/messageservice';

import { CommonService } from '../metadata/services/common.service';
import { RuleService } from '../metadata/services/rule.service';

import { Version } from './../metadata/domain/version'
import { DependsOn } from './dependsOn'
import { AttributeHolder } from './../metadata/domain/domain.attributeHolder'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { MetaType } from '../metadata/enums/metaType';
import { AppHelper } from '../app.helper';
import { Rule } from '../metadata/domain/domain.rule';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { FilterInfo } from '../metadata/domain/domain.filterInfo';
import { SourceAttr } from '../metadata/domain/domain.sourceAttr';
import { AttributeMapIO } from '../metadata/domainIO/domain.attributeMapIO';
import { AttributeMap } from '../metadata/domain/domain.attributeMap';
import { AttributeInfoIO } from '../metadata/domainIO/domain.attributeInfoIO';
import { AttributeIO } from '../metadata/domainIO/domain.attributeIO';
import { ParamInfoIO } from '../metadata/domainIO/domain.paramInfoIO';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { FilterInfoIO } from '../metadata/domainIO/domain.filterInfoIO';
import { Paramset } from '../metadata/domain/domain.paramset';
import { ParamInfo } from '../metadata/domain/domain.paramInfo';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { RuleIO } from '../metadata/domainIO/domain.ruleIO';

@Component({
  selector: 'app-business-rulesdetail',
  templateUrl: './business-rulesdetail.templete.html',
})

export class BusinessRulesDetailComponent {
  isNullArray: { 'value': string; 'label': string; }[];
  iSSubmitEnable: boolean;
  dragIndex: any;
  dropIndex: any;
  isHomeEnable: boolean;
  dialogAttributeName: any;
  dialogAttriNameArray: any[];
  dialogAttriArray: any[];
  displayDialogBox: boolean;
  dialogSelectName: any;
  paramlistArray: any = [];
  functionArray: any[];
  rhsTypeArray: { value: string; label: string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  rhsFormulaArray: any[];
  attributesArray: any[];
  lhsFormulaArray: any[];
  allParameterset: any;
  selectallattribute: any;
  isTabelShow: boolean;
  paramtable: any[];
  paramtablecol: any;
  paramsetdata: any;
  isShowExecutionparam: boolean;
  selectParameterlist: any;
  checkboxModelexecution: any;
  allParameterList: any[];
  selectAllAttributeRow: any;
  allFormula: any[];
  allExpression: any[];
  allFunction: any[];
  allSourceAttribute: any;
  sourceAttributeTypes: { "text": string; "label": string; }[];
  attributeTableArray: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  dataqualitycompare: any;
  selectedAllFitlerRow: boolean;
  lhsdatapodattributefilter: any[];
  operators: any[];
  logicalOperators: any[];
  filterTableArray: any;
  datatype: string[];
  allNames: any[];
  sourcedata: DependsOn;
  source: string;
  sources: string[];
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  msgs: any[];
  tags: any;
  createdBy: any;
  ruledata: any;
  mode: any;
  version: any;
  uuid: any;
  id: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  showForm: boolean = true;
  isGraphInprogess: boolean;
  showDivGraph: boolean;
  isGraphError: boolean;
  isversionEnable: boolean;
  isAdd: boolean;
  isEdit: boolean = false;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  isFilterInprogess: boolean = false;
  metaType = MetaType;
  moveTo: number;
  moveToEnable: boolean;
  count: any[];
  invalideMinRow: boolean = false;
  invalideMaxRow: boolean = false;
  txtQueryChangedFilter: Subject<string> = new Subject<string>();
  resetTableTopBottom: Subject<string> = new Subject<string>();
  txtQueryChangedAttribute: Subject<string> = new Subject<string>();
  rowIndex: any;
  topDisabled: boolean;
  bottomDisabled: boolean;
  datasetNotEmpty: boolean = true;

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private _ruleService: RuleService, public appHelper: AppHelper) {

    this.isHomeEnable = false;
    this.ruledata = {};
    this.continueCount = 1;
    this.isSubmit = "false";
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.dialogSelectName = {};
    this.sources = ["datapod", "relation", "dataset", "rule"];
    //this.source = this.sources[0];
    this.progressbarWidth = 25 * this.continueCount + "%";
    this.dataqualitycompare = null;
    this.filterTableArray = null;
    this.attributeTableArray = null;
    this.ruledata["active"] = true;
    this.isEditError = false;
    this.isEditInprogess = false;
    this.isFilterInprogess = false;
    this.breadcrumbDataFrom = [{
      "caption": "Business Rules",
      "routeurl": "/app/list/rule"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/rule"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.sourceAttributeTypes = [
      { "text": "function", "label": "function" },
      { "text": "string", "label": "string" },
      { "text": "datapod", "label": "attribute" },
      { "text": "expression", "label": "expression" },
      { "text": "formula", "label": "formula" }
    ]
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.uuid = param.id;
      this.version = param.version;
      this.mode = param.mode;
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.uuid, this.version);
        this.getAllVersionByUuid();
        this.getAllParamertLsit();
      }
      else {
        this.getAllLatest(true)
        this.getAllParamertLsit();
      }
    });
    this.lhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' }
    ];
    this.rhsTypeArray = [
      { value: 'string', label: 'string' },
      { value: 'integer', label: 'integer' },
      { value: 'datapod', label: 'attribute' },
      { value: 'formula', label: 'formula' },
      { value: 'dataset', label: 'dataset' },
      { value: 'function', label: 'function' },
      { value: 'paramlist', label: 'paramlist' }
    ]
    this.operators = [
      { 'value': '=', 'label': 'EQUAL TO(=)' },
      { 'value': '!=', 'label': 'NOT EQUAL(!=)' },
      { 'value': '<', 'label': 'LESS THAN(<)' },
      { 'value': '>', 'label': 'GREATER THAN(>)' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL(<=)' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL(>=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
      { 'value': 'IN', 'label': 'IN' },
      { 'value': 'NOT IN', 'label': 'NOT IN' },
      { 'value': 'IS', 'label': 'IS' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]
    this.isNullArray = [
      { 'value': 'NULL', 'label': 'NULL' },
      { 'value': 'NOT NULL', 'label': 'NOT NULL' }
    ];

    this.moveToEnable = false;
    this.count = [];
    this.txtQueryChangedFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.ruledata.filterTableArray) {
          if (this.ruledata.filterTableArray[i].hasOwnProperty("selected"))
            this.ruledata.filterTableArray[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.resetTableTopBottom
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.moveTo = null;
        this.checkSelected(false, null);
        this.checkSelectedAttribute(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });
    this.invalideMinRow = false;
    this.invalideMaxRow = false;
    this.topDisabled = false;
    this.bottomDisabled = false;

    this.txtQueryChangedAttribute
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.attributeTableArray) {
          if (this.attributeTableArray[i].hasOwnProperty("selected"))
            this.attributeTableArray[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelectedAttribute(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

  }

  ngOnInit() {
    this.setMode(this.mode);
  }
  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/businessRules/rule', uuid, version, 'false']);
    this.isEdit = true;
  }

  showview(uuid, version) {
    this.router.navigate(['app/businessRules/rule', uuid, version, 'true']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {debugger
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.ruledata.name;
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/rule']);
  }

  changeType() {
    this.ruledata.filterTableArray = [];
    this.attributeTableArray = [];
    this.getAllLatest(true)
  }
  changeSourceType() {
    this.ruledata.filterTableArray = null;
    this.attributeTableArray = null;
    this.getAllAttributeBySource();
  }

  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }

  onChangeSourceAttribute(type, index) {
    if (type == "string") {
      this.attributeTableArray[index].isSourceAtributeSimple = true;
      this.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.attributeTableArray[index].isSourceAtributeFormula = false;
      this.attributeTableArray[index].sourcesimple = "''";
      this.attributeTableArray[index].isSourceAtributeExpression = false;
      this.attributeTableArray[index].isSourceAtributeFunction = false;
    }
    else if (type == this.metaType.DATAPOD) {
      this.attributeTableArray[index].isSourceAtributeSimple = false;
      this.attributeTableArray[index].isSourceAtributeDatapod = true;
      this.attributeTableArray[index].isSourceAtributeFormula = false;
      this.attributeTableArray[index].isSourceAtributeExpression = false;
      this.attributeTableArray[index].isSourceAtributeFunction = false;
      this.attributeTableArray[index].sourceattribute = {}
      this.getAllAttributeBySource()
    }
    else if (type == this.metaType.FORMULA) {
      this.attributeTableArray[index].isSourceAtributeSimple = false;
      this.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.attributeTableArray[index].isSourceAtributeFormula = true;
      this.attributeTableArray[index].isSourceAtributeExpression = false;
      this.attributeTableArray[index].isSourceAtributeFunction = false;
      this.attributeTableArray[index].sourceformula = {}
      this.getAllFormula()
    }
    else if (type == this.metaType.EXPRESSION) {
      this.attributeTableArray[index].isSourceAtributeSimple = false;
      this.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.attributeTableArray[index].isSourceAtributeFormula = false;
      this.attributeTableArray[index].isSourceAtributeExpression = true;
      this.attributeTableArray[index].isSourceAtributeFunction = false;
      this.attributeTableArray[index].sourceexpression = {}
      this.getAllExpression()
    }
    else if (type == this.metaType.FUNCTION) {
      this.attributeTableArray[index].isSourceAtributeSimple = false;
      this.attributeTableArray[index].isSourceAtributeDatapod = false;
      this.attributeTableArray[index].isSourceAtributeFormula = false;
      this.attributeTableArray[index].isSourceAtributeExpression = false;
      this.attributeTableArray[index].isSourceAtributeFunction = true;
      this.attributeTableArray[index].sourcefunction = {}
      this.getAllFunctions()
    }
  }

  addAttribute() {
    if (this.attributeTableArray == null) {
      this.attributeTableArray = [];
    }
    let len = this.attributeTableArray.length + 1
    let attrinfo = new AttributeInfoIO();
    attrinfo.name = "attribute" + len;
    attrinfo.id = len - 1;
    attrinfo.sourceAttributeType = { "value": "string", "label": "string" };
    attrinfo.isSourceAtributeSimple = true;
    attrinfo.isSourceAtributeDatapod = false;
    this.attributeTableArray.splice(this.attributeTableArray.length, 0, attrinfo);
    // this.checkSelectedAttribute(false, this.attributeTableArray.length - 1);
  }

  removeAttribute() {
    var newDataList = [];
    this.selectAllAttributeRow = false
    this.attributeTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.checkSelectedAttribute(false, null);
    this.attributeTableArray = newDataList;
  }

  checkAllAttributeRow() {

    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    this.checkSelectedAttribute(false, null);
    this.attributeTableArray.forEach(attribute => {
      attribute.selected = this.selectAllAttributeRow;
    });
  }

  onChangeAttributeDatapod(data, index) {
    if (data != null) {
      this.attributeTableArray[index].name = data.label.split(".")[1]
    }
  }
  onChangeFormula(data, index) {
    this.attributeTableArray[index].name = data.name
  }
  onChangeExpression(data, index) {
    this.attributeTableArray[index].name = data.name
  }

  onSelectparamSet() {
    var paramSetjson = { paramInfoArray: [] };
    var paramInfoArray = [];
    if (this.paramsetdata && this.paramsetdata != null) {
      for (var i = 0; i < this.paramsetdata.paramInfo.length; i++) {
        var paramInfo = new ParamInfoIO();
        paramInfo.paramSetId = this.paramsetdata.paramInfo[i].paramSetId
        paramInfo.selected = false
        var paramSetValarray = [];
        for (var j = 0; j < this.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
          var paramSetValjson = { paramId: "", paramName: "", value: "", ref: "" };
          paramSetValjson.paramId = this.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
          paramSetValjson.paramName = this.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
          paramSetValjson.value = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
          paramSetValjson.ref = this.paramsetdata.paramInfo[i].paramSetVal[j].ref;
          paramSetValarray[j] = paramSetValjson;
          paramInfo.paramSetVal = paramSetValarray;
          paramInfo.value = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
        }
        paramInfoArray[i] = paramInfo;
      }
      this.paramtablecol = paramInfoArray[0].paramSetVal;
      this.paramtable = paramInfoArray;
      paramSetjson.paramInfoArray = paramInfoArray;
      this.isTabelShow = true;
    } else {
      this.isTabelShow = false;
    }
  }
  changeCheckboxExecution() {
    if (this.checkboxModelexecution == true && this.selectParameterlist != null) {
      this._ruleService.getParamSetByParamList(this.selectParameterlist.uuid, this.metaType.RULE)
        .subscribe(
          response => {
            this.onSuccessGetParamSetByParmLsit(response)
          },
          error => console.log("Error :: " + error));
    } else {
      this.isShowExecutionparam = false;
      this.allParameterset = null;
    }
  }

  onSuccessGetParamSetByParmLsit(response) {
    this.allParameterset = response
    this.isShowExecutionparam = true;
  }

  selectAllRow() {
    if (!this.selectallattribute) {
      this.selectallattribute = true;
    }
    else {
      this.selectallattribute = false;
    }
    this.paramtable.forEach(stage => {

      stage.selected = this.selectallattribute;
    });
  }

  changeParamertLsitType(selectParameterlist) {

    if (this.selectParameterlist.uuid == null || this.selectParameterlist.uuid == "") {
      this.isShowExecutionparam = false;
      // this.allParameterList = null;
      this.paramlistArray = [];
      this._commonService.getParamByApp("", this.metaType.APPLICATION)
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error));
    }
    else if (selectParameterlist) {
      this._commonService.getParamByApp("", this.metaType.APPLICATION)
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error));

      this._commonService.getLatestByUuid(selectParameterlist.uuid, this.metaType.PARAMLIST)
        .subscribe(response => { this.onSuccessgetLatestByUuid(response) },
          error => console.log("Error ::", error))
    }
  }



  modelExecute(modeldetail) {
    let newDataList = [];
    this.selectallattribute = false;
    let execParams = new Paramset();
    if (this.paramtable) {
      this.paramtable.forEach(selected => {
        if (selected.selected) {
          newDataList.push(selected);
        }
      });

      let paramInfoArray = [];

      if (this.paramtable && newDataList.length > 0) {
        let ref = { uuid: "", version: "" }
        ref.uuid = this.paramsetdata.uuid;
        ref.version = this.paramsetdata.version;
        for (var i = 0; i < newDataList.length; i++) {
          var paraminfo = new ParamInfoIO();
          paraminfo.paramSetId = newDataList[i].paramSetId;
          paraminfo.ref = ref;
          paramInfoArray[i] = paraminfo;
        }
      }

      if (paramInfoArray.length > 0) {
        execParams.paramInfo = paramInfoArray;
      }
      else {
        execParams = null
      }
    }
    console.log(JSON.stringify(execParams));
    this._ruleService.execute(modeldetail.uuid, modeldetail.version, execParams).subscribe(
      response => { this.onSuccessExecute(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessExecute(response) {
    this.msgs = [];
    this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Saved and Submited Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  onChangeOperator(index) {
    this.ruledata.filterTableArray[index].rhsAttribute = null;
    if (this.ruledata.filterTableArray[index].operator == 'EXISTS' || this.ruledata.filterTableArray[index].operator == 'NOT EXISTS' ||
      this.ruledata.filterTableArray[index].operator == 'IN' || this.ruledata.filterTableArray[index].operator == 'NOT IN') {
      this.ruledata.filterTableArray[index].rhsType = this.metaType.DATASET;
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.ruledata.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else if (this.ruledata.filterTableArray[index].operator == 'IS') {
      this.ruledata.filterTableArray[index].rhsType = 'string';
    }
    else {
      this.ruledata.filterTableArray[index].rhsType = 'integer';
    }
    // this.ruledata.filterTableArray[index].rhsAttribute = null;
    // if (this.ruledata.filterTableArray[index].operator == 'EXISTS' || this.ruledata.filterTableArray[index].operator == 'NOT EXISTS') {
    // 	this.ruledata.filterTableArray[index].rhsType = 'dataset';
    // }
    // else{
    // 	this.ruledata.filterTableArray[index].rhsType = 'integer';
    // }	
  }

  getAllFunctions() {
    this._commonService.getAllLatestFunction(this.metaType.FUNCTION, "N").subscribe(
      response => { this.onSuccessFunction(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response) {
    let temp = []
    for (const n in response) {
      let allname = new DropDownIO();
      allname.label = response[n].name;
      allname.value = { label: "", uuid: "", u_Id: "" };
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      allname.value.u_Id = response[n].uuid;
      temp[n] = allname;
    }
    this.allFunction = temp
  }
  getAllExpression() {
    this._commonService.getExpressionByType(this.sourcedata.uuid, this.source).subscribe(
      response => { this.onSuccessExpression(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessExpression(response) {
    let temp = []
    for (const n in response) {
      let allname = new DropDownIO();
      allname.label = response[n].name;
      allname.value = { label: "", uuid: "", u_Id: "" };
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      allname.value.u_Id = response[n].uuid;
      temp[n] = allname;
    }
    this.allExpression = temp
  }

  getAllFormula() {
    this._commonService.getFormulaByType(this.sourcedata.uuid, this.metaType.FORMULA).subscribe(
      response => { this.onSuccessgetAllFormula(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessgetAllFormula(response) {
    let temp = []
    for (const n in response) {
      let allname = new DropDownIO();
      allname.label = response[n].name;
      allname.value = { label: "", uuid: "", u_Id: "" };
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      allname.value.u_Id = response[n].uuid;
      temp[n] = allname;
    }
    this.allFormula = temp
  }
  getAllParamertLsit() {
    this._commonService.getAllLatestParamListByTemplate('Y', this.metaType.PARAMLIST, this.metaType.RULE).subscribe(
      response => { this.OnSuccesgetAllParameter(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllParameter(response) {
    let temp = []
    let allname = new AttributeIO();
    allname.label = '-select-'
    allname.value = { label: "", uuid: "" };
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      temp[n] = allname;
    }
    this.allParameterList = temp
    this.allParameterList.splice(0, 0, allname);

  }
  getAllLatest(IsDefault) {
    if (this.source) {
      this._commonService.getAllLatest(this.source).subscribe(
        response => { this.OnSuccesgetAllLatest(response, IsDefault) },
        error => console.log('Error :: ' + error)
      )
    }
  }

  OnSuccesgetAllLatest(response1, IsDefault) {
    let temp = []
    if (this.mode == undefined || IsDefault == true) {
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
    }

    for (const n in response1) {
      let allname = new DropDownIO();
      // response1.sort((a, b) => a.name.localeCompare(b.name.toString()));
      allname.label = response1[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response1[n].name;
      allname.value.uuid = response1[n].uuid;
      temp[n] = allname;
    }
    temp.sort((a, b) => a.label.localeCompare(b.label.toString()));
    this.allNames = temp
    this.getAllAttributeBySource();
  }

  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + this.sourcedata.uuid)
    )
  }

  OnSuccesgetAllAttributeBySource(response) {
    this.allSourceAttribute = []
    this.lhsdatapodattributefilter = null
    let attribute = []
    for (const n in response) {
      let allname = new AttributeIO();
      allname.label = response[n].dname;
      allname.value = {};
      allname.value.label = response[n].dname;
      allname.value.u_Id = response[n].id;
      allname.value.uuid = response[n].uuid;
      allname.value.attrId = response[n].attributeId;
      attribute[n] = allname
    }
    this.allSourceAttribute = attribute
    this.lhsdatapodattributefilter = attribute

    //---- new
    // this.allSourceAttribute = []
    // this.lhsdatapodattributefilter = null;
    // let attribute = []
    // let firstObj = new AttributeIO();

    // firstObj.label = "-Select-"
    // firstObj.value = { label: "-Select-", u_Id: "", uuid: "", attrId: "" }
    // this.allSourceAttribute.push(firstObj);

    // for (const n in response) {
    // 	let allname = new AttributeIO();
    // 	allname.label = response[n].dname;
    // 	allname.value = {};
    // 	allname.value.label = response[n].dname;
    // 	allname.value.u_Id = response[n].id;
    // 	allname.value.uuid = response[n].uuid;
    // 	allname.value.attrId = response[n].attributeId;
    // 	attribute[n] = allname
    // }
    // this.allSourceAttribute = attribute
    // this.lhsdatapodattributefilter = attribute
  }

  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this.isFilterInprogess = true;
    this._ruleService.getOneByUuidAndVersion(id, version, this.metaType.RULE)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }

  onSuccessgetFunctionByCriteria(response) {
    let functionArray = [new DropDownIO];
    for (const i in response) {
      let attributeObj = new DropDownIO();
      attributeObj.label = response[i].name;
      attributeObj.value = { label: "", uuid: "" };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].name;
      functionArray[i] = attributeObj
    }
    this.functionArray = functionArray;
  }

  onSuccessgetParamByApp(response) {
    let paramlistArray = [];
    if (this.paramlistArray.length <= 0) {
      paramlistArray = [new AttributeIO];
      for (const i in response) {
        let attributeObj = new AttributeIO();
        attributeObj.label = "app." + response[i].paramName;
        attributeObj.value = { label: "", uuid: "", attributeId: "" };
        attributeObj.value.uuid = response[i].ref.uuid;
        attributeObj.value.attributeId = response[i].paramId;
        attributeObj.value.label = "app." + response[i].paramName;
        paramlistArray[i] = attributeObj
      }
      this.paramlistArray = paramlistArray;
    }
  }

  onSuccessgetLatestByUuid(response: any) {
    let attributeObj = new AttributeIO();
    if (response) {
      for (const i in response.params) {
        attributeObj.label = "rule." + response.params[i].paramName;
        attributeObj.value = { label: "", uuid: "", attributeId: "" };
        attributeObj.value.uuid = response.uuid;
        attributeObj.value.attributeId = response.params[i].paramId;
        attributeObj.value.label = "rule." + response.params[i].paramName;

        this.paramlistArray.push(attributeObj);
        // this.paramlistArray[i] = attributeObj;
      }
    }
    this.isFilterInprogess = false;
  }

  onSuccessgetOneByUuidAndVersion(response: RuleIO) {

    this.ruledata = response;
    this.breadcrumbDataFrom[2].caption = this.ruledata.name;

    this.dataqualitycompare = response;
    this.attributeTableArray = response.attributeTableArray;
    //this.createdBy = response.createdBy.ref.name

    this.ruledata.published = this.appHelper.convertStringToBoolean(this.ruledata.published);
    this.ruledata.active = this.appHelper.convertStringToBoolean(this.ruledata.active);
    this.ruledata.locked = this.appHelper.convertStringToBoolean(this.ruledata.locked);

    const version: Version = new Version();
    // this.uuid = response.uuid
    version.label = this.ruledata.version;
    version.uuid = this.ruledata.uuid;
    version.u_Id = this.ruledata.uuid + "_" + this.ruledata.version;
    this.selectedVersion = version;

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = this.ruledata.source.ref.name;
    dependOnTemp.uuid = this.ruledata.source.ref.uuid;
    this.sourcedata = dependOnTemp;

    if (this.ruledata.paramList != null && this.ruledata.paramList != "") {
      let selectedParameterlist: DependsOn = new DependsOn();
      selectedParameterlist.label = this.ruledata.paramList.ref.name;
      selectedParameterlist.uuid = this.ruledata.paramList.ref.uuid;
      this.selectParameterlist = selectedParameterlist;
    }

    this.source = this.ruledata.source.ref.type;
    this.getAllLatest(false);
    this.getAllFunctions();

    if (response.isFormulaExits == true) {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
          error => console.log("Error ::", error))
    }
    if (response.isFormulaExits == true) {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
          error => console.log("Error ::", error))
    }
    if (response.isAttributeExits == true) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
          error => console.log("Error ::", error))
    }
    if (response.isSimpleExits == true) {
    }
    if (response.isParamlistExits == true) {
      this._commonService.getParamByApp("", this.metaType.APPLICATION)
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error));

      this._commonService.getLatestByUuid(this.ruledata.paramList.ref.uuid, this.metaType.PARAMLIST)
        .subscribe(response => { this.onSuccessgetLatestByUuid(response) },
          error => console.log("Error ::", error));
    }
    else
      this.isFilterInprogess = false;
    if (response.isFunctionExits == true) {

      this._commonService.getFunctionByCriteria("", "N", this.metaType.FUNCTION)
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))
    }

    this.ruledata.filterTableArray = response.filterInfo;

    this.isEditInprogess = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.RULE, this.uuid)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "", u_Id: "" }
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      ver.value.u_Id = response[i].uuid + "_" + response[i].version
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  onSuccessgetFormulaByLhsType(response) {
    this.lhsFormulaArray = []
    for (const i in response) {
      let formulaObj = new DropDownIO();
      formulaObj.label = response[i].name;
      formulaObj.value = { label: "", uuid: "" }
      formulaObj.value.uuid = response[i].uuid;
      formulaObj.value.label = response[i].name;
      this.lhsFormulaArray[i] = formulaObj;
    }
  }

  onSuccessgetAllAttributeBySourceLhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = new DropDownIO();
      attributeObj.label = response[i].dname;
      attributeObj.value = { label: "", uuid: "", attributeId: "" };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [];
    let rhsFormulaObj = new DropDownIO();
    let RhsFormulaArray = [new DropDownIO];
    for (const i in response) {
      rhsFormulaObj.label = response[i].name;
      rhsFormulaObj.value = { label: "", uuid: "" };
      rhsFormulaObj.value.label = response[i].name;
      rhsFormulaObj.value.uuid = response[i].uuid;
      RhsFormulaArray[i] = rhsFormulaObj;
    }
    this.rhsFormulaArray = RhsFormulaArray
  }

  onSuccessgetAllAttributeBySourceRhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = new DropDownIO();
      attributeObj.label = response[i].dname;
      attributeObj.value = { label: "", uuid: "", attributeId: "" };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  onChangeLhsType(index) {
    this.ruledata.filterTableArray[index].lhsAttribute = null;
    if (this.ruledata.filterTableArray[index].lhsType == this.metaType.FORMULA) {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
          error => console.log("Error ::", error))
    }

    else if (this.ruledata.filterTableArray[index].lhsType == this.metaType.DATAPOD) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
          error => console.log("Error ::", error))
    }

    else {
      this.ruledata.filterTableArray[index].lhsAttribute = null;
    }

  }

  onChangeRhsType(index) {
    this.ruledata.filterTableArray[index].rhsAttribute = null;
    if (this.ruledata.filterTableArray[index].rhsType == this.metaType.FORMULA) {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
          error => console.log("Error ::", error))
    }

    else if (this.ruledata.filterTableArray[index].rhsType == this.metaType.DATAPOD) {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
          error => console.log("Error ::", error))
    }

    else if (this.ruledata.filterTableArray[index].rhsType == this.metaType.FUNCTION) {
      this._commonService.getFunctionByCriteria("", "N", this.metaType.FUNCTION)
        .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
          error => console.log("Error ::", error))
    }

    else if (this.ruledata.filterTableArray[index].rhsType == this.metaType.PARAMLIST) {
      this._commonService.getParamByApp("", this.metaType.APPLICATION)
        .subscribe(response => { this.onSuccessgetParamByApp(response) },
          error => console.log("Error ::", error))
    }

    else if (this.ruledata.filterTableArray[index].rhsType == this.metaType.DATASET) {
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.ruledata.filterTableArray[index].rhsAttribute = rhsAttribute;
    }

    else {
      this.ruledata.filterTableArray[index].rhsAttribute = null;
    }
  }

  addRow() {

    // var len = this.ruledata.filterTableArray.length + 1
    var filertable = new FilterInfoIO;
    // filertable.logicalOperator = "";
    if (this.ruledata.filterTableArray == null || this.ruledata.filterTableArray.length == 0) {
      this.ruledata.filterTableArray = [];
      filertable.logicalOperator = '';
    }
    else {
      filertable.logicalOperator = this.logicalOperators[1].label;
    }
    filertable.lhsType = "string"
    filertable.lhsAttribute = null;
    filertable.operator = this.operators[0].label;
    filertable.rhsType = "string"
    filertable.rhsAttribute = null;
    this.ruledata.filterTableArray.splice(this.ruledata.filterTableArray.length, 0, filertable);
    this.count = [];
    this.checkSelected(false, this.ruledata.filterTableArray.length - 1);
  }

  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.ruledata.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }

    this.count = [];
    this.checkSelected(false, null);
    this.ruledata.filterTableArray = newDataList;
  }

  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.checkSelected(false, null);
    this.ruledata.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }

  searchOption(data, index) {
    this.rowIndex = index;
    this.displayDialogBox = true;
    this._commonService.getAllLatest(this.metaType.DATASET)
      .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAllLatestDialogBox(response) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriObj = new DropDownIO();

      dialogAttriObj.label = response[i].name;
      dialogAttriObj.value = { label: "", uuid: "" };
      dialogAttriObj.value.label = response[i].name;
      dialogAttriObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriObj;
    }
    this.dialogAttriArray = temp
  }

  onChangeDialogAttribute() {
    this._commonService.getAttributesByDataset(this.metaType.DATASET, this.dialogSelectName.uuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = {};
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index) {
    this.displayDialogBox = false;
    let rhsattribute = new AttributeIO();
    rhsattribute.label = this.dialogAttributeName.label;
    rhsattribute.uuid = this.dialogAttributeName.uuid;
    rhsattribute.attributeId = this.dialogAttributeName.attributeId;
    this.ruledata.filterTableArray[index].rhsAttribute = rhsattribute;
    this.datasetNotEmpty = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }
  autoPopulate() {
    // this.attributeTableArray = [];
    // for (var i = 0; i < this.allSourceAttribute.length; i++) {
    //   let attributeinfo = new AttributeInfoIO();
    //   attributeinfo.id = i+1;
    //   attributeinfo.name = this.allSourceAttribute[i].value.label
    //   // attributeinfo.sourceattribute = {};
    //   let sourceattribute1 = new AttributeHolder();
    //   sourceattribute1 = {attrId: "", uuid: "", u_Id: "", label: ""};
    //   sourceattribute1.attrId = this.allSourceAttribute[i].value.attrId;
    //   sourceattribute1.uuid = this.allSourceAttribute[i].value.uuid;
    //   sourceattribute1.u_Id = this.allSourceAttribute[i].value.u_Id
    //   sourceattribute1.label = this.allSourceAttribute[i].value.label;
    //   attributeinfo.sourceattribute = sourceattribute1;

    //   attributeinfo.sourceAttributeType = this.sourceAttributeTypes[2];
    //   attributeinfo.isSourceAtributeSimple = false;
    //   attributeinfo.isSourceAtributeDatapod = true;
    //   attributeinfo.isSourceAtributeFormula = false;
    //   attributeinfo.isSourceAtributeExpression = false;
    //   attributeinfo.isSourceAtributeFunction = false;
    //   attributeinfo.isSourceAtributeParamList = false;
    this.attributeTableArray = [];
    for (var i = 0; i < this.allSourceAttribute.length; i++) {
      var that = this;
      var attributeinfo = new AttributeInfoIO();
      attributeinfo.id = i + 1;

      attributeinfo.name = (this.allSourceAttribute[i].value.label).split(".")[1];

      attributeinfo.sourceattribute = { attrId: "", uuid: "", u_Id: "", label: "" };
      attributeinfo.sourceattribute.uuid = this.allSourceAttribute[i].value.uuid;
      attributeinfo.sourceattribute.label = this.allSourceAttribute[i].value.label;
      attributeinfo.sourceattribute.u_Id = this.allSourceAttribute[i].value.u_Id;
      // attributeinfo.sourceattribute.type = "datapod";
      attributeinfo.sourceattribute.attrId = this.allSourceAttribute[i].value.attrId;
      // attributeinfo.sourceattribute.id = this.allSourceAttribute[i].value.id;

      // { "text": "datapod", "label": "attribute" },
      let obj = { text: "", label: "" }
      obj.text = this.metaType.DATAPOD
      obj.label = "attribute"
      attributeinfo.sourceAttributeType = obj;

      attributeinfo.isSourceAtributeSimple = false;
      attributeinfo.isSourceAtributeDatapod = true;
      attributeinfo.isSourceAtributeFormula = false;
      attributeinfo.isSourceAtributeExpression = false;
      attributeinfo.isSourceAtributeFunction = false;
      attributeinfo.isSourceAtributeParamList = false;
      this.attributeTableArray.push(attributeinfo);
    }
  }
  ruleSubmit() {
    this.isSubmit = "true"
    let baseRuleJson = new Rule();
    baseRuleJson.uuid = this.ruledata.uuid;
    baseRuleJson.name = this.ruledata.name;
    baseRuleJson.desc = this.ruledata.desc;
    baseRuleJson.tags = this.ruledata.tags;
    baseRuleJson.active = this.appHelper.convertBooleanToString(this.ruledata.active);
    baseRuleJson.published = this.appHelper.convertBooleanToString(this.ruledata.published);
    baseRuleJson.locked = this.appHelper.convertBooleanToString(this.ruledata.locked);

    let source = new MetaIdentifierHolder();
    let ref = new MetaIdentifier();
    ref.type = this.source
    ref.uuid = this.sourcedata.uuid;
    source.ref = ref;
    baseRuleJson.source = source;


    if (this.selectParameterlist) {
      if (this.selectParameterlist.uuid != null && this.selectParameterlist.uuid != "") {
        let paramlist = new MetaIdentifierHolder();
        let ref = new MetaIdentifier();
        ref.uuid = this.selectParameterlist.uuid;
        ref.type = this.metaType.PARAMLIST;
        paramlist.ref = ref
        baseRuleJson.paramList = paramlist;
      }
      else
        baseRuleJson.paramList = null;
    }

    let filterInfoArray = [];
    if (this.ruledata.filterTableArray != null) {
      if (this.ruledata.filterTableArray.length > 0) {
        for (let i = 0; i < this.ruledata.filterTableArray.length; i++) {

          let filterInfo = new FilterInfo();
          filterInfo.display_seq = i;
          filterInfo.logicalOperator = this.ruledata.filterTableArray[i].logicalOperator;
          filterInfo.operator = this.ruledata.filterTableArray[i].operator;
          filterInfo.operand = [];

          if (this.ruledata.filterTableArray[i].lhsType == 'integer' || this.ruledata.filterTableArray[i].lhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.ruledata.filterTableArray[i].lhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].lhsType == this.metaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FORMULA;
            ref.uuid = this.ruledata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            // operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].lhsType == this.metaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATAPOD;
            ref.uuid = this.ruledata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.ruledata.filterTableArray[i].lhsAttribute.attributeId;
            filterInfo.operand[0] = operatorObj;
          }
          if (this.ruledata.filterTableArray[i].rhsType == 'integer' || this.ruledata.filterTableArray[i].rhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.ruledata.filterTableArray[i].rhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[1] = operatorObj;

            if (this.ruledata.filterTableArray[i].rhsType == 'integer' && this.ruledata.filterTableArray[i].operator == 'BETWEEN') {
              let operatorObj = new SourceAttr();
              let ref = new MetaIdentifier();
              ref.type = this.metaType.SIMPLE;
              operatorObj.ref = ref;
              operatorObj.value = this.ruledata.filterTableArray[i].rhsAttribute1 + "and" + this.ruledata.filterTableArray[i].rhsAttribute2;
              filterInfo.operand[1] = operatorObj;
            }
          }
          else if (this.ruledata.filterTableArray[i].rhsType == this.metaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FORMULA;
            ref.uuid = this.ruledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].rhsType == this.metaType.FUNCTION) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FUNCTION;
            ref.uuid = this.ruledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            //operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].rhsType == this.metaType.PARAMLIST) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.PARAMLIST;
            ref.uuid = this.ruledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.ruledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].rhsType == this.metaType.DATASET) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATASET;
            ref.uuid = this.ruledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.ruledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.ruledata.filterTableArray[i].rhsType == this.metaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATAPOD;
            ref.uuid = this.ruledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.ruledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          filterInfoArray[i] = filterInfo;
        }
        baseRuleJson.filterInfo = filterInfoArray;
      }
    }
    if (this.attributeTableArray != null) {
      var attributesArray = [];
      for (var i = 0; i < this.attributeTableArray.length; i++) {
        let attributemap = new AttributeMapIO();
        attributemap.attrSourceId = i;
        attributemap.attrDisplaySeq = i;
        attributemap.attrSourceName = this.attributeTableArray[i].name
        let sourceAttr = new SourceAttr();
        let sourceref = new MetaIdentifier();
        if (this.attributeTableArray[i].sourceAttributeType.text == "string") {
          sourceref.type = this.metaType.SIMPLE;
          sourceAttr.ref = sourceref;
          if (typeof this.attributeTableArray[i].sourcesimple == "undefined") {
            sourceAttr.value = "";
          }
          else {
            sourceAttr.value = this.attributeTableArray[i].sourcesimple;
          }
          attributemap.sourceAttr = sourceAttr;
        }// End Simple IF

        else if (this.attributeTableArray[i].sourceAttributeType.text == this.metaType.DATAPOD) {
          //let attributemap = new AttributeMap();
          let sourceAttr = new AttributeRefHolder();
          // let uuid = this.attributeTableArray[i].sourceattribute.uuid
          // let attrid = this.attributeTableArray[i].sourceattribute.attrId
          sourceref.uuid = this.attributeTableArray[i].sourceattribute.uuid;
          if (this.source == this.metaType.RELATION) {
            sourceref.type = this.metaType.DATAPOD;
          }
          else {
            sourceref.type = this.source;
          }
          sourceAttr.ref = sourceref;
          sourceAttr.attrId = this.attributeTableArray[i].sourceattribute.attrId;
          sourceAttr.attrType = null;
          attributemap.sourceAttr = sourceAttr;
        } // End Datapod ELSE IF

        else if (this.attributeTableArray[i].sourceAttributeType.text == this.metaType.EXPRESSION) {
          sourceref.type = this.metaType.EXPRESSION;
          sourceref.uuid = this.attributeTableArray[i].sourceexpression.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr;
        }// End Expresson ELSEIF

        else if (this.attributeTableArray[i].sourceAttributeType.text == this.metaType.FORMULA) {
          sourceref.type = this.metaType.FORMULA;
          sourceref.uuid = this.attributeTableArray[i].sourceformula.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr;

        } // End Fromula ELSEIF
        else if (this.attributeTableArray[i].sourceAttributeType.text == this.metaType.FUNCTION) {
          sourceref.type = this.metaType.FUNCTION;
          sourceref.uuid = this.attributeTableArray[i].sourcefunction.uuid;
          sourceAttr.ref = sourceref;
          attributemap.sourceAttr = sourceAttr
        }
        attributesArray[i] = attributemap;
      }//End FOR Loop

      baseRuleJson.attributeInfo = attributesArray;
    }
    this._commonService.submit(this.metaType.RULE, baseRuleJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.RULE, response).subscribe(
        response => {
          this.modelExecute(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.msgs = [];
      this.isSubmit = "true"
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Saved Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);

    }
  }

  onFilterRowDown(index) {
    var rowTempIndex = this.ruledata.filterTableArray[index];
    var rowTempIndexPlus = this.ruledata.filterTableArray[index + 1];
    this.ruledata.filterTableArray[index] = rowTempIndexPlus;
    this.ruledata.filterTableArray[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true

  }

  onFilterRowUp(index) {
    var rowTempIndex = this.ruledata.filterTableArray[index];
    var rowTempIndexMines = this.ruledata.filterTableArray[index - 1];
    this.ruledata.filterTableArray[index] = rowTempIndexMines;
    this.ruledata.filterTableArray[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }
  // dragStart(event, data) {
  //   console.log(event)
  //   console.log(data)
  //   this.dragIndex = data
  // }
  // dragEnd(event) {
  //   console.log(event)
  // }
  // drop(event, data) {
  //   if (this.mode == 'false') {
  //     this.dropIndex = data
  //     // console.log(event)
  //     // console.log(data)
  //     var item = this.ruledata.filterTableArray[this.dragIndex]
  //     this.ruledata.filterTableArray.splice(this.dragIndex, 1)
  //     this.ruledata.filterTableArray.splice(this.dropIndex, 0, item)
  //     this.iSSubmitEnable = true
  //   }

  // }
  onAttrRowDown(index) {
    var rowTempIndex = this.attributeTableArray[index];
    var rowTempIndexPlus = this.attributeTableArray[index + 1];
    this.attributeTableArray[index] = rowTempIndexPlus;
    this.attributeTableArray[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true

  }

  onAttrRowUp(index) {
    var rowTempIndex = this.attributeTableArray[index];
    var rowTempIndexMines = this.attributeTableArray[index - 1];
    this.attributeTableArray[index] = rowTempIndexMines;
    this.attributeTableArray[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }
  // dragAttrStart(event, data) {
  //   console.log(event)
  //   console.log(data)
  //   this.dragIndex = data
  // }
  // dragAttrEnd(event) {
  //   console.log(event)
  // }
  // dropAttr(event, data) {
  //   if (this.mode == 'false') {
  //     this.dropIndex = data
  //     // console.log(event)
  //     // console.log(data)
  //     var item = this.attributeTableArray[this.dragIndex]
  //     this.attributeTableArray.splice(this.dragIndex, 1)
  //     this.attributeTableArray.splice(this.dropIndex, 0, item)
  //     this.iSSubmitEnable = true
  //   }
  // }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.ruledata.filterTableArray.length; i++) {
      if (this.ruledata.filterTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.ruledata.filterTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.ruledata.filterTableArray, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.ruledata.filterTableArray[0].logicalOperator = "";
              if (!this.ruledata.filterTableArray[1].logicalOperator) {
                this.ruledata.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            if (new_index == this.ruledata.filterTableArray.length - 1) {
              this.ruledata.filterTableArray[0].logicalOperator = "";
              if (this.ruledata.filterTableArray[new_index].logicalOperator == "") {
                this.ruledata.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            this.txtQueryChangedFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.ruledata.filterTableArray[0].logicalOperator = "";
            if (!this.ruledata.filterTableArray[1].logicalOperator) {
              this.ruledata.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.ruledata.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          else if (new_index == this.ruledata.filterTableArray.length - 1) {
            this.ruledata.filterTableArray[0].logicalOperator = "";
            if (this.ruledata.filterTableArray[new_index].logicalOperator == "") {
              this.ruledata.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            }
            this.ruledata.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          break;
        }
      }
    }
  }


  array_move(arr, old_index, new_index) {
    while (old_index < 0) {
      old_index += arr.length;
    }
    while (new_index < 0) {
      new_index += arr.length;
    }
    if (new_index >= arr.length) {
      var k = new_index - arr.length + 1;
      while (k--) {
        arr.push(undefined);
      }
    }
    arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
    return arr;
  }

  checkSelected(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
      // console.log("Flag: " + flag + " /Count : " + this.count);
    }
    else
      this.count.pop();

    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.ruledata.filterTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }

  updateArrayAttribute(new_index, range, event) {
    for (let i = 0; i < this.attributeTableArray.length; i++) {
      if (this.attributeTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.attributeTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.attributeTableArray, old_index, new_index);
          if (range) {

            // if (new_index == 0 || new_index == 1) {
            //   this.attributeTableArray[0].logicalOperator = "";
            //   if (!this.attributeTableArray[1].logicalOperator) {
            //     this.attributeTableArray[1].logicalOperator = this.logicalOperators[1].label;
            //   }
            //   this.checkSelectedAttribute(false, null);
            // }
            // if (new_index == this.attributeTableArray.length - 1) {
            //   this.attributeTableArray[0].logicalOperator = "";
            //   if (this.attributeTableArray[new_index].logicalOperator == "") {
            //     this.attributeTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            //   }
            this.checkSelectedAttribute(false, null);
            // }
            this.txtQueryChangedAttribute.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            // this.attributeTableArray[0].logicalOperator = "";
            // if (!this.attributeTableArray[1].logicalOperator) {
            //   this.attributeTableArray[1].logicalOperator = this.logicalOperators[1].label;
            // }
            this.attributeTableArray[new_index].selected = "";
            this.checkSelectedAttribute(false, null);
          }
          else if (new_index == this.attributeTableArray.length - 1) {
            // this.attributeTableArray[0].logicalOperator = "";
            // if (this.attributeTableArray[new_index].logicalOperator == "") {
            //   this.attributeTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            // }
            this.attributeTableArray[new_index].selected = "";
            this.checkSelectedAttribute(false, null);
          }
          break;
        }
      }
    }
  }


  checkSelectedAttribute(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
    }
    else
      this.count.pop();

    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.attributeTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }


  ngOnDestroy() {
    this.txtQueryChangedFilter.unsubscribe();
    this.resetTableTopBottom.unsubscribe();
    this.txtQueryChangedAttribute.unsubscribe();
  }

}

