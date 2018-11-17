
import { Component, Input, OnInit } from '@angular/core';
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

@Component({
	selector: 'app-business-rulesdetail',
	templateUrl: './business-rulesdetail.templete.html',
})

export class BusinessRulesDetailComponent {
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
	attributeTableArray: any[];
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
	dqdata: any;
	mode: any;
	version: any;
	uuid: any;
	id: any;
	continueCount: any;
	progressbarWidth: any;
	isSubmit: any
	constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _ruleService: RuleService) {
		this.dqdata = {};
		this.continueCount = 1;
		this.isSubmit = "false"
		this.sources = ["datapod", "relation", "dataset", "rule"];
		this.source = this.sources[0];
		this.progressbarWidth = 25 * this.continueCount + "%";
		this.dataqualitycompare = null;
		this.filterTableArray = null;
		this.dqdata["active"] = true
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
			this.id = params['id'];
			this.version = params['version'];
			this.mode = params['mode'];
			if (this.mode !== undefined) {
				this.getOneByUuidAndVersion(this.id, this.version);
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
      { value: 'dataset', label: 'dataset' }
		]
		this.operators = [
      { 'value': '<', 'label': 'LESS THAN' },
      { 'value': '>', 'label': 'GREATER THAN' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL' },
      { 'value': '=', 'label': 'EQUAL TO(=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }]

	}

	public goBack() {
		// this._location.back();
		this.router.navigate(['app/list/rule'])
	}

	changeType() {
		this.filterTableArray = null;
		this.attributeTableArray = null;
		this.getAllLatest(true)
	}
	changeSourceType() {
		this.filterTableArray = null;
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
		else if (type == "datapod") {
			this.attributeTableArray[index].isSourceAtributeSimple = false;
			this.attributeTableArray[index].isSourceAtributeDatapod = true;
			this.attributeTableArray[index].isSourceAtributeFormula = false;
			this.attributeTableArray[index].isSourceAtributeExpression = false;
			this.attributeTableArray[index].isSourceAtributeFunction = false;
			this.attributeTableArray[index].sourceattribute = {}
			this.getAllAttributeBySource()
		}
		else if (type == "formula") {
			this.attributeTableArray[index].isSourceAtributeSimple = false;
			this.attributeTableArray[index].isSourceAtributeDatapod = false;
			this.attributeTableArray[index].isSourceAtributeFormula = true;
			this.attributeTableArray[index].isSourceAtributeExpression = false;
			this.attributeTableArray[index].isSourceAtributeFunction = false;
			this.attributeTableArray[index].sourceformula = {}
			this.getAllFormula()
		}
		else if (type == "expression") {
			this.attributeTableArray[index].isSourceAtributeSimple = false;
			this.attributeTableArray[index].isSourceAtributeDatapod = false;
			this.attributeTableArray[index].isSourceAtributeFormula = false;
			this.attributeTableArray[index].isSourceAtributeExpression = true;
			this.attributeTableArray[index].isSourceAtributeFunction = false;
			this.attributeTableArray[index].sourceexpression = {}
			this.getAllExpression()
		}
		else if (type == "function") {
			this.attributeTableArray[index].isSourceAtributeSimple = false;
			this.attributeTableArray[index].isSourceAtributeDatapod = false;
			this.attributeTableArray[index].isSourceAtributeFormula = false;
			this.attributeTableArray[index].isSourceAtributeExpression = false;
			this.attributeTableArray[index].isSourceAtributeFunction = true;
			this.attributeTableArray[index].isSourceAtributeFunction = true;
			this.attributeTableArray[index].sourcefunction = {}
			this.getAllFunctions()
		}
	}

	checkAllAttributeRow() {

		if (!this.selectAllAttributeRow) {
			this.selectAllAttributeRow = true;
		}
		else {
			this.selectAllAttributeRow = false;
		}
		this.attributeTableArray.forEach(attribute => {
			attribute.selected = this.selectAllAttributeRow;
		});
	}
	addAttribute() {
		if (this.attributeTableArray == null) {
			this.attributeTableArray = [];
		}
		let len = this.attributeTableArray.length + 1
		let attrinfo = {};
		attrinfo["name"] = "attribute" + len;
		attrinfo["id"] = len - 1;
		attrinfo["sourceAttributeType"] = { "value": "string", "label": "string" };
		attrinfo["isSourceAtributeSimple"] = true;
		attrinfo["isSourceAtributeDatapod"] = false;
		this.attributeTableArray.splice(this.attributeTableArray.length, 0, attrinfo);
	}

	removeAttribute() {
		var newDataList = [];
		this.selectAllAttributeRow = false
		this.attributeTableArray.forEach(selected => {
			if (!selected.selected) {
				newDataList.push(selected);
			}
		});
		this.attributeTableArray = newDataList;
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

		var paramSetjson = {};
		var paramInfoArray = [];
		if (this.paramsetdata && this.paramsetdata != null) {
			for (var i = 0; i < this.paramsetdata.paramInfo.length; i++) {
				var paramInfo = {};
				paramInfo["paramSetId"] = this.paramsetdata.paramInfo[i].paramSetId
				paramInfo["selected"] = false
				var paramSetValarray = [];
				for (var j = 0; j < this.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
					var paramSetValjson = {};
					paramSetValjson["paramId"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
					paramSetValjson["paramName"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
					paramSetValjson["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
					paramSetValjson["ref"] = this.paramsetdata.paramInfo[i].paramSetVal[j].ref;
					paramSetValarray[j] = paramSetValjson;
					paramInfo["paramSetVal"] = paramSetValarray;
					paramInfo["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
				}
				paramInfoArray[i] = paramInfo;
			}
			this.paramtablecol = paramInfoArray[0].paramSetVal;
			this.paramtable = paramInfoArray;
			paramSetjson["paramInfoArray"] = paramInfoArray;
			this.isTabelShow = true;
		} else {
			this.isTabelShow = false;
		}
	}
	changeCheckboxExecution() {
		if (this.checkboxModelexecution == true && this.selectParameterlist != null) {
			this._ruleService.getParamSetByParamList(this.selectParameterlist.uuid, 'rule')
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

	changeParamertLsitType() {
		if (this.selectParameterlist == null) {
			this.isShowExecutionparam = false;
			//this.allParameterList = null;
		}
	}

	modelExecute(modeldetail) {
		let newDataList = [];
		this.selectallattribute = false;
		let execParams = {}
		if (this.paramtable) {
			this.paramtable.forEach(selected => {
				if (selected.selected) {
					newDataList.push(selected);
				}
			});

			let paramInfoArray = [];

			if (this.paramtable && newDataList.length > 0) {
				let ref = {}
				ref["uuid"] = this.paramsetdata.uuid;
				ref["version"] = this.paramsetdata.version;
				for (var i = 0; i < newDataList.length; i++) {
					var paraminfo = {};
					paraminfo["paramSetId"] = newDataList[i].paramSetId;
					paraminfo["ref"] = ref;
					paramInfoArray[i] = paraminfo;
				}
			}

			if (paramInfoArray.length > 0) {
				execParams["paramInfo"] = paramInfoArray;
			}
			else {
				execParams = null
			}
		}
		console.log(JSON.stringify(execParams));
		this._ruleService.execute(modeldetail["uuid"], modeldetail["version"], execParams).subscribe(
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
			allname["value"]["u_Id"] = response[n]['uuid'];
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
			let allname = {};
			allname["label"] = response[n]['name'];
			allname["value"] = {};
			allname["value"]["label"] = response[n]['name'];
			allname["value"]["uuid"] = response[n]['uuid'];
			allname["value"]["u_Id"] = response[n]['uuid'];
			temp[n] = allname;
		}
		this.allExpression = temp
	}

	getAllFormula() {
		this._commonService.getFormulaByType(this.sourcedata.uuid, "formula").subscribe(
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
			allname["value"]["u_Id"] = response[n]['uuid'];
			temp[n] = allname;
		}
		this.allFormula = temp
	}
	getAllParamertLsit() {
		this._commonService.getAllLatest("paramlist").subscribe(
			response => { this.OnSuccesgetAllParameter(response) },
			error => console.log('Error :: ' + error)
		)
	}
	OnSuccesgetAllParameter(response) {
		let temp = []
		let allname = {}
		allname["label"] = '-select-'
		allname["value"] = null;
		for (const n in response) {
			let allname = {};
			allname["label"] = response[n]['name'];
			allname["value"] = {};
			allname["value"]["label"] = response[n]['name'];
			allname["value"]["uuid"] = response[n]['uuid'];
			temp[n] = allname;
		}
		this.allParameterList = temp
		this.allParameterList.splice(0, 0, allname);

	}
	getAllLatest(IsDefault) {
		this._commonService.getAllLatest(this.source).subscribe(
			response => { this.OnSuccesgetAllLatest(response, IsDefault) },
			error => console.log('Error :: ' + error)
		)
	}

	OnSuccesgetAllLatest(response1, IsDefault) {
		let temp = []
		if (this.mode == undefined || IsDefault == true) {
			let dependOnTemp: DependsOn = new DependsOn();
			dependOnTemp.label = response1[0]["name"];
			dependOnTemp.uuid = response1[0]["uuid"];
			this.sourcedata = dependOnTemp
		}
		for (const n in response1) {
			let allname = {};
			allname["label"] = response1[n]['name'];
			allname["value"] = {};
			allname["value"]["label"] = response1[n]['name'];
			allname["value"]["uuid"] = response1[n]['uuid'];
			temp[n] = allname;
		}
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
			let allname = {};
			allname["label"] = response[n]['dname'];
			allname["value"] = {};
			allname["value"]["label"] = response[n]['dname'];
			allname["value"]["u_Id"] = response[n]['id'];
			allname["value"]["uuid"] = response[n]['uuid'];
			allname["value"]["attrId"] = response[n]['attributeId'];
			attribute[n] = allname
		}
		this.allSourceAttribute = attribute
		this.lhsdatapodattributefilter = attribute
	}

	getOneByUuidAndVersion(id, version) {
		this._ruleService.getOneByUuidAndVersion(id, version, 'rule')
			.subscribe(
			response => {
				this.onSuccessgetOneByUuidAndVersion(response)
			},
			error => console.log("Error :: " + error));
	}

	onSuccessgetOneByUuidAndVersion(response) {
		this.breadcrumbDataFrom[2].caption = response.dqdata.name
		this.dqdata = response.dqdata;
		this.dataqualitycompare = response.dqdata;
		this.attributeTableArray = response.sourceAttributes
		this.createdBy = response.dqdata.createdBy.ref.name
		this.dqdata.published = response.dqdata["published"] == 'Y' ? true : false
		this.dqdata.active = response.dqdata["active"] == 'Y' ? true : false
		var tags = [];
		if (response.dqdata.tags != null) {
			for (var i = 0; i < response.dqdata.tags.length; i++) {
				var tag = {};
				tag['value'] = response.dqdata.tags[i];
				tag['display'] = response.dqdata.tags[i];
				tags[i] = tag
			}//End For
			this.tags = tags;
		}//End If
		const version: Version = new Version();
		this.uuid = response.uuid
		version.label = response.dqdata['version'];
		version.uuid = response.dqdata['uuid'];
		version.u_Id = response.dqdata['uuid'] + "_" + response.dqdata['version'];
		this.selectedVersion = version
		this.source = response.dqdata["source"]["ref"].type
		let dependOnTemp: DependsOn = new DependsOn();
		dependOnTemp.label = response.dqdata["source"]["ref"]["name"];
		dependOnTemp.uuid = response.dqdata["source"]["ref"]["uuid"];
		this.sourcedata = dependOnTemp;
		this.getAllLatest(false);
		this.getAllFunctions()

    if(response.dqdata.filterInfo != null){
      let filterInfoArray = [];
      if (response.dqdata.filterInfo.length > 0) {
        for (let k = 0; k < response.dqdata.filterInfo.length; k++) {
          let filterInfo = {};
          let lhsFilter = {};
          filterInfo["logicalOperator"] = response.dqdata.filterInfo[k].logicalOperator
          filterInfo["lhsType"] = response.dqdata.filterInfo[k].operand[0].ref.type;
          filterInfo["operator"] = response.dqdata.filterInfo[k].operator;
          filterInfo["rhsType"] = response.dqdata.filterInfo[k].operand[1].ref.type;
  
          if (response.dqdata.filterInfo[k].operand[0].ref.type == 'formula') {
            this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
              .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
              error => console.log("Error ::", error))
  
            let lhsAttri1 = {}
            lhsAttri1["uuid"] = response.dqdata.filterInfo[k].operand[0].ref.uuid;
            lhsAttri1["label"] = response.dqdata.filterInfo[k].operand[0].ref.name;
            filterInfo["lhsAttribute"] = lhsAttri1;
          }
  
          else if (response.dqdata.filterInfo[k].operand[0].ref.type == 'datapod') {
  
            this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
              .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
              error => console.log("Error ::", error))
            let lhsAttri = {}
            lhsAttri["uuid"] = response.dqdata.filterInfo[k].operand[0].ref.uuid;
            lhsAttri["label"] = response.dqdata.filterInfo[k].operand[0].ref.name + "." + response.dqdata.filterInfo[k].operand[0].attributeName;
            lhsAttri["attributeId"] = response.dqdata.filterInfo[k].operand[0].attributeId;
            filterInfo["lhsAttribute"] = lhsAttri;
          }
  
          else if (response.dqdata.filterInfo[k].operand[0].ref.type == 'simple') {
            let stringValue = response.dqdata.filterInfo[k].operand[0].value;
            let onlyNumbers = /^[0-9]+$/;
            let result = onlyNumbers.test(stringValue);
            if (result == true) {
              filterInfo["lhsType"] = 'integer';
            } else {
              filterInfo["lhsType"] = 'string';
            }
            filterInfo["lhsAttribute"] = response.dqdata.filterInfo[k].operand[0].value;
          }
  
          if (response.dqdata.filterInfo[k].operand[1].ref.type == 'formula') {
            this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
              .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
              error => console.log("Error ::", error))
            //filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].ref.name;
            let rhsAttri = {}
            rhsAttri["uuid"] = response.dqdata.filterInfo[k].operand[1].ref.uuid;
            rhsAttri["label"] = response.dqdata.filterInfo[k].operand[1].ref.name;
            filterInfo["rhsAttribute"] = rhsAttri;
          }
  
          else if (response.dqdata.filterInfo[k].operand[1].ref.type == 'datapod') {
            this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
              .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
              error => console.log("Error ::", error))
  
            let rhsAttri1 = {}
            rhsAttri1["uuid"] = response.dqdata.filterInfo[k].operand[1].ref.uuid;
            rhsAttri1["label"] = response.dqdata.filterInfo[k].operand[1].ref.name + "." + response.dqdata.filterInfo[k].operand[1].attributeName;
            rhsAttri1["attributeId"] = response.dqdata.filterInfo[k].operand[1].attributeId;
            filterInfo["rhsAttribute"] = rhsAttri1;
          }
  
          else if (response.dqdata.filterInfo[k].operand[1].ref.type == 'simple') {
            let stringValue = response.dqdata.filterInfo[k].operand[1].value;
            let onlyNumbers = /^[0-9]+$/;
            let result = onlyNumbers.test(stringValue);
            if (result == true) {
              filterInfo["rhsType"] = 'integer';
            } else {
              filterInfo["rhsType"] = 'string';
            }
            filterInfo["rhsAttribute"] = response.dqdata.filterInfo[k].operand[1].value;
  
            let result2 = stringValue.includes("and")
            if (result2 == true) {
              filterInfo["rhsType"] = 'integer';
  
              let betweenValArray = []
              betweenValArray = stringValue.split("and");
              filterInfo["rhsAttribute1"] = betweenValArray[0];
              filterInfo["rhsAttribute2"] = betweenValArray[1];
            }
          }
          filterInfoArray.push(filterInfo);
        }
      }
      this.dqdata.filterTableArray = filterInfoArray
    }
	}

	getAllVersionByUuid() {
		this._commonService.getAllVersionByUuid('rule', this.id)
			.subscribe(
			response => {
				this.OnSuccesgetAllVersionByUuid(response)
			},
			error => console.log("Error :: " + error));
	}

	OnSuccesgetAllVersionByUuid(response) {
		var temp = []
		for (const i in response) {
			let ver = {};
			ver["label"] = response[i]['version'];
			ver["value"] = {};
			ver["value"]["label"] = response[i]['version'];
			ver["value"]["uuid"] = response[i]['uuid'];
			ver["value"]["u_Id"] = response[i]['uuid'] + "_" + response[i]['version']
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
      let formulaObj = {};
      formulaObj["label"] = response[i].name;
      formulaObj["value"] = {};
      formulaObj["value"]["uuid"] = response[i].uuid;
      formulaObj["value"]["label"] = response[i].name;
      this.lhsFormulaArray[i] = formulaObj;
    }
	}
	
	onSuccessgetAllAttributeBySourceLhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = response[i].dname;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].uuid;
      attributeObj["value"]["label"] = response[i].dname;
      attributeObj["value"]["attributeId"] = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
	}
	
	onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [];
    let rhsFormulaObj = {};
    let temp = [];
    for (const i in response) {
      rhsFormulaObj["label"] = response[i].name;
      rhsFormulaObj["value"] = {};
      rhsFormulaObj["value"]["label"] = response[i].name;
      rhsFormulaObj["value"]["uuid"] = response[i].uuid;
      temp[i] = rhsFormulaObj;
    }
    this.rhsFormulaArray = temp
	}
	
	onSuccessgetAllAttributeBySourceRhs(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = {};
      attributeObj["label"] = response[i].dname;
      attributeObj["value"] = {};
      attributeObj["value"]["uuid"] = response[i].uuid;
      attributeObj["value"]["label"] = response[i].dname;
      attributeObj["value"]["attributeId"] = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
	}
	
	onChangeLhsType(index) {
    this.dqdata.filterTableArray[index]["lhsAttribute"] = null;
    if (this.dqdata.filterTableArray[index]["lhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dqdata.filterTableArray[index]["lhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dqdata.filterTableArray[index]["lhsAttribute"] = null;
    }
  }

  onChangeRhsType(index) {
    this.dqdata.filterTableArray[index]["rhsAttribute"] = null;
    if (this.dqdata.filterTableArray[index]["rhsType"] == 'formula') {
      this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
        error => console.log("Error ::", error))
    }

    else if (this.dqdata.filterTableArray[index]["rhsType"] == 'datapod') {
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
        .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
        error => console.log("Error ::", error))
    }

    else {
      this.dqdata.filterTableArray[index]["rhsAttribute"] = null;
    }
	}
	
	addRow() {
    if (this.dqdata.filterTableArray == null) {
      this.dqdata.filterTableArray = [];
    }
    var len = this.dqdata.filterTableArray.length + 1
    var filertable = {};
    filertable["logicalOperator"] = ""
    filertable["lhsType"] = ""
    filertable["lhsAttribute"] = ""
    filertable["operator"] = ""
    filertable["rhsType"] = ""
    filertable["rhsAttribute"] = ""
    this.dqdata.filterTableArray.splice(this.dqdata.filterTableArray.length, 0, filertable);
	}
	
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.dqdata.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.dqdata.filterTableArray = newDataList;
	}

	checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.dqdata.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }
	
	ruleSubmit() {
		this.isSubmit = "true"
		let dqJson = {};
		dqJson["uuid"] = this.dqdata.uuid;
		dqJson["name"] = this.dqdata.name;
		dqJson["desc"] = this.dqdata.desc;
		var tagArray = [];
		if (this.tags != null) {
			for (var counttag = 0; counttag < this.tags.length; counttag++) {
				tagArray[counttag] = this.tags[counttag].value;
			}
		}
		dqJson['tags'] = tagArray
		dqJson["active"] = this.dqdata.active == true ? 'Y' : "N"
		dqJson["published"] = this.dqdata.published == true ? 'Y' : "N"
		let source = {};
		let ref = {};
		ref["type"] = this.source
		ref["uuid"] = this.sourcedata.uuid;
		source["ref"] = ref;
		dqJson["source"] = source;
		if(this.selectParameterlist != null){
			let paramlist = {}
			let ref = {}
			ref["uuid"] = this.selectParameterlist.uuid;
			ref["label"] = this.selectParameterlist.label;
			paramlist["ref"] = ref
			dqJson["paramList"] = paramlist;
		}
		else
		dqJson["paramList"] = null;

		let filterInfoArray = [];
		if (this.dqdata.filterTableArray != null) {
			for (let i = 0; i < this.dqdata.filterTableArray.length; i++) {

				let filterInfo = {};
				filterInfo["logicalOperator"] = this.dqdata.filterTableArray[i].logicalOperator;
				filterInfo["operator"] = this.dqdata.filterTableArray[i].operator;
				filterInfo["operand"] = [];

				if (this.dqdata.filterTableArray[i].lhsType == 'integer' || this.dqdata.filterTableArray[i].lhsType == 'string') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "simple";
					operatorObj["ref"] = ref;
					operatorObj["value"] = this.dqdata.filterTableArray[i].lhsAttribute;
					operatorObj["attributeType"] = "string"
					filterInfo["operand"][0] = operatorObj;
				}
				else if (this.dqdata.filterTableArray[i].lhsType == 'formula') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "formula";
					ref["uuid"] = this.dqdata.filterTableArray[i].lhsAttribute.uuid;
					operatorObj["ref"] = ref;
					// operatorObj["attributeId"] = this.dataset.filterTableArray[i].lhsAttribute;
					filterInfo["operand"][0] = operatorObj;
				}
				else if (this.dqdata.filterTableArray[i].lhsType == 'datapod') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "datapod";
					ref["uuid"] = this.dqdata.filterTableArray[i].lhsAttribute.uuid;
					operatorObj["ref"] = ref;
					operatorObj["attributeId"] = this.dqdata.filterTableArray[i].lhsAttribute.attributeId;
					filterInfo["operand"][0] = operatorObj;
				}
				if (this.dqdata.filterTableArray[i].rhsType == 'integer' || this.dqdata.filterTableArray[i].rhsType == 'string') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "simple";
					operatorObj["ref"] = ref;
					operatorObj["value"] = this.dqdata.filterTableArray[i].rhsAttribute;
					operatorObj["attributeType"] = "string"
					filterInfo["operand"][1] = operatorObj;

					if (this.dqdata.filterTableArray[i].rhsType == 'integer' && this.dqdata.filterTableArray[i].operator == 'BETWEEN') {
						let operatorObj = {};
						let ref = {}
						ref["type"] = "simple";
						operatorObj["ref"] = ref;
						operatorObj["value"] = this.dqdata.filterTableArray[i].rhsAttribute1 + "and" + this.dqdata.filterTableArray[i].rhsAttribute2;
						filterInfo["operand"][1] = operatorObj;
					}
				}
				else if (this.dqdata.filterTableArray[i].rhsType == 'formula') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "formula";
					ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
					operatorObj["ref"] = ref;
					//operatorObj["attributeId"] = this.dataset.filterTableArray[i].rhsAttribute;
					filterInfo["operand"][1] = operatorObj;
				}
				else if (this.dqdata.filterTableArray[i].rhsType == 'datapod') {
					let operatorObj = {};
					let ref = {}
					ref["type"] = "datapod";
					ref["uuid"] = this.dqdata.filterTableArray[i].rhsAttribute.uuid;
					operatorObj["ref"] = ref;
					operatorObj["attributeId"] = this.dqdata.filterTableArray[i].rhsAttribute.attributeId;
					filterInfo["operand"][1] = operatorObj;
				}
				filterInfoArray[i] = filterInfo;
			}
			dqJson["filterInfo"] = filterInfoArray;
			console.log(JSON.stringify(filterInfoArray));
		}
    if(this.attributeTableArray != null){
      var sourceAttributesArray = [];
      for (var i = 0; i < this.attributeTableArray.length; i++) {
        let attributemap = {};
        attributemap["attrSourceId"] = i;
        attributemap["attrSourceName"] = this.attributeTableArray[i].name
        let sourceAttr = {};
        let sourceref = {};
        if (this.attributeTableArray[i].sourceAttributeType.text == "string") {
          sourceref["type"] = "simple";
          sourceAttr["ref"] = sourceref;
          if (typeof this.attributeTableArray[i].sourcesimple == "undefined") {
            sourceAttr["value"] = "";
          }
          else {
            sourceAttr["value"] = this.attributeTableArray[i].sourcesimple;
          }
          attributemap["sourceAttr"] = sourceAttr;
        }// End Simple IF
  
        else if (this.attributeTableArray[i].sourceAttributeType.text == "datapod") {
          let uuid = this.attributeTableArray[i].sourceattribute.uuid
          let attrid = this.attributeTableArray[i].sourceattribute.attrId
          sourceref["uuid"] = uuid;
          if (this.source == "relation") {
            sourceref["type"] = "datapod";
          }
          else {
            sourceref["type"] = this.source;
          }
          sourceAttr["ref"] = sourceref;
          sourceAttr["attrId"] = attrid;
          attributemap["sourceAttr"] = sourceAttr;
        } // End Datapod ELSE IF
  
        else if (this.attributeTableArray[i].sourceAttributeType.text == "expression") {
          sourceref["type"] = "expression";
          sourceref["uuid"] = this.attributeTableArray[i].sourceexpression.uuid;
          sourceAttr["ref"] = sourceref;
          attributemap["sourceAttr"] = sourceAttr;
        }// End Expresson ELSEIF
  
        else if (this.attributeTableArray[i].sourceAttributeType.text == "formula") {
          sourceref["type"] = "formula";
          sourceref["uuid"] = this.attributeTableArray[i].sourceformula.uuid;
          sourceAttr["ref"] = sourceref;
          attributemap["sourceAttr"] = sourceAttr;
  
        } // End Fromula ELSEIF
        else if (this.attributeTableArray[i].sourceAttributeType.text == "function") {
          sourceref["type"] = "function";
          sourceref["uuid"] = this.attributeTableArray[i].sourcefunction.uuid;
          sourceAttr["ref"] = sourceref;
          attributemap["sourceAttr"] = sourceAttr
        }
        sourceAttributesArray[i] = attributemap;
      }//End FOR Loop
  
      dqJson["attributeInfo"] = sourceAttributesArray;  
    }
		console.log(dqJson);
		this._commonService.submit("rule", dqJson).subscribe(
			response => { this.OnSuccessubmit(response) },
			error => console.log('Error :: ' + error)
		)
	}

	OnSuccessubmit(response) {
		if (this.checkboxModelexecution == true) {
			this._commonService.getOneById("rule", response).subscribe(
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
	enableEdit(uuid, version) {
		this.router.navigate(['app/businessRules/rule', uuid, version, 'false']);
	}

	showview(uuid, version) {
		this.router.navigate(['app/businessRules/rule', uuid, version, 'true']);
	}
}

