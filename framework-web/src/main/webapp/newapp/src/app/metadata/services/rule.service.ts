
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
import { AttributeIO } from '../domainIO/domain.attributeIO';
import { AttributeMapIO } from '../domainIO/domain.attributeMapIO';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import { RuleIO } from '../domainIO/domain.ruleIO';
import { AttributeInfoIO } from '../domainIO/domain.attributeInfoIO';
import * as MetaTypeEnum from '../../metadata/enums/metaType';


@Injectable()

export class RuleService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  executeRule(version: String, uuid: Number): Observable<any[]> {
    let url = '/rule/executeRule?action=execute&ruleUUID=' + uuid + '&ruleVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getResults(version: String, uuid: Number)
  getResults(version: String, uuid: Number, offset?: Number, limit?: Number, requestId?: Number)
  getResults(version: String, uuid: Number, offset?: Number, limit?: Number, requestId?: Number): Observable<any[]> {
    let url = '/rule/getResults?action=view&uuid=' + uuid + '&version=' + version + '&offset=' + offset + '&limit=' + limit + '&requestId=' + requestId;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getExpressionByType(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getExpressionByType?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getSaveAs(uuid: Number, type: String, version: String): Observable<any[]> {
    let url = '/common/saveAs?action=clone&uuid=' + uuid + '&version=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamSetByParamList(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getParamSetByParamList?action=view&paramListUuid=' + uuid + '&type=rule';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  execute(uuid: Number, version: String, data: any): Observable<any> {
    let url = '/rule/execute/?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRuleExecByRule(uuid: Number, startDate: String, endDate: String): Observable<any[]> {
    let url = '/rule/getRuleExecByRule?action=view&ruleUuid=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  // getOneByUuidAndVersion(uuid, version, type): Observable<any> {
  //   return this._commonService.getOneByUuidAndVersion(uuid, version, type)
  //     .pipe(
  //       map(response => {
  //         let ruleJson = {};
  //         ruleJson["ruledata"] = response
  //         var filterInfoArray = [];
  //         if (response != null) {
  //           for (var i = 0; i < response.filterInfo.length; i++) {
  //             let filterInfo = {};
  //             filterInfo["logicalOperator"] = response.filterInfo[i].logicalOperator
  //             filterInfo["operator"] = response.filterInfo[i].operator;
  //             let lhsFilter: AttributeHolder = new AttributeHolder();
  //             lhsFilter.label = response.filterInfo[i].operand[0].ref.name + "." + response.filterInfo[i].operand[0].attributeName;;
  //             lhsFilter.u_Id = response.filterInfo[i].operand[0].ref.uuid + "_" + response.filterInfo[i].operand[0].attributeId
  //             lhsFilter.uuid = response.filterInfo[i].operand[0].ref.uuid;
  //             lhsFilter.attrId = response.filterInfo[i].operand[0].attributeId;
  //             filterInfo["lhsFilter"] = lhsFilter;
  //             filterInfo["filtervalue"] = response.filterInfo[i].operand[1].value;
  //             filterInfoArray[i] = filterInfo
  //           }
  //         }
  //         ruleJson["filterInfo"] = filterInfoArray;
  //         var sourceAttributesArray = [];
  //         for (var n = 0; n < response["attributeInfo"].length; n++) {
  //           var attributeInfo = {};
  //           attributeInfo["name"] = response["attributeInfo"][n].attrSourceName
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "simple") {
  //             var obj = {}
  //             obj["text"] = "string"
  //             obj["caption"] = "string"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["isSourceAtributeSimple"] = true;
  //             attributeInfo["sourcesimple"] = response["attributeInfo"][n].sourceAttr.value
  //             attributeInfo["isSourceAtributeDatapod"] = false;
  //             attributeInfo["isSourceAtributeFormula"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = false;
  //             attributeInfo["isSourceAtributeFunction"] = false;
  //             attributeInfo["isSourceAtributeParamList"] = false;

  //           }
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "datapod" || response["attributeInfo"][n].sourceAttr.ref.type == "dataset" || response["attributeInfo"][n].sourceAttr.ref.type == "rule") {
  //             var sourceattribute: AttributeHolder = new AttributeHolder();
  //             sourceattribute.label = response["attributeInfo"][n].sourceAttr.ref.name + "." + response["attributeInfo"][n].sourceAttr.attrName
  //             sourceattribute.u_Id = response["attributeInfo"][n].sourceAttr.ref.uuid + "_" + response["attributeInfo"][n].sourceAttr.attrId;
  //             sourceattribute.uuid = response["attributeInfo"][n].sourceAttr.ref.uuid;
  //             sourceattribute.attrId = response["attributeInfo"][n].sourceAttr.attrId
  //             sourceattribute["name"] = "";
  //             var obj = {}
  //             obj["text"] = "datapod"
  //             obj["caption"] = "attribute"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["sourceattribute"] = sourceattribute;
  //             attributeInfo["isSourceAtributeSimple"] = false;
  //             attributeInfo["isSourceAtributeDatapod"] = true;
  //             attributeInfo["isSourceAtributeFormula"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = false;
  //             attributeInfo["isSourceAtributeFunction"] = false;
  //             attributeInfo["isSourceAtributeParamList"] = false;
  //           }
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "expression") {
  //             var sourceexpression = {};
  //             sourceexpression["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
  //             sourceexpression["name"] = "";
  //             var obj = {}
  //             obj["text"] = "expression"
  //             obj["caption"] = "expression"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["sourceexpression"] = sourceexpression;
  //             attributeInfo["isSourceAtributeSimple"] = false;
  //             attributeInfo["isSourceAtributeDatapod"] = false;
  //             attributeInfo["isSourceAtributeFormula"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = true;
  //             attributeInfo["isSourceAtributeFunction"] = false;
  //             attributeInfo["isSourceAtributeParamList"] = false;
  //           }
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "formula") {
  //             var sourceformula = {};
  //             sourceformula["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
  //             sourceformula["name"] = "";
  //             var obj = {}
  //             obj["text"] = "formula"
  //             obj["caption"] = "formula"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["sourceformula"] = sourceformula;
  //             attributeInfo["isSourceAtributeSimple"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = false;
  //             attributeInfo["isSourceAtributeDatapod"] = false;
  //             attributeInfo["isSourceAtributeFormula"] = true;
  //             attributeInfo["isSourceAtributeFunction"] = false;
  //             attributeInfo["isSourceAtributeParamList"] = false;
  //           }
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "function") {
  //             var sourcefunction: AttributeHolder = new AttributeHolder();
  //             sourcefunction.u_Id = response["attributeInfo"][n].sourceAttr.ref.uuid;
  //             sourcefunction.label = "";
  //             sourcefunction.uuid = response["attributeInfo"][n].sourceAttr.ref.uuid;

  //             var obj = {}
  //             obj["text"] = "function"
  //             obj["caption"] = "function"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["sourcefunction"] = sourcefunction;
  //             attributeInfo["isSourceAtributeSimple"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = false;
  //             attributeInfo["isSourceAtributeDatapod"] = false;
  //             attributeInfo["isSourceAtributeFormula"] = false;
  //             attributeInfo["isSourceAtributeFunction"] = true;
  //             attributeInfo["isSourceAtributeParamList"] = false;
  //           }
  //           if (response["attributeInfo"][n].sourceAttr.ref.type == "paramlist") {
  //             var sourceparamlist = {};
  //             sourceparamlist["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
  //             sourceparamlist["attrId"] = response["attributeInfo"][n].sourceAttr.attrId
  //             sourceparamlist["name"] = "";
  //             var obj = {}
  //             obj["text"] = "paramlist"
  //             obj["caption"] = "paramlist"
  //             attributeInfo["sourceAttributeType"] = obj;
  //             attributeInfo["sourceparamlist"] = sourceparamlist;
  //             attributeInfo["isSourceAtributeSimple"] = false;
  //             attributeInfo["isSourceAtributeExpression"] = false;
  //             attributeInfo["isSourceAtributeDatapod"] = false;
  //             attributeInfo["isSourceAtributeFormula"] = false;
  //             attributeInfo["isSourceAtributeFunction"] = false;
  //             attributeInfo["isSourceAtributeParamList"] = true;
  //           }
  //           sourceAttributesArray[n] = attributeInfo
  //         }
  //         ruleJson["sourceAttributes"] = sourceAttributesArray
  //         console.log(response)
  //         return <any>ruleJson;
  //       }), catchError(error => this.handleError<string>(error, "Network Error!")));
  // }

  getOneByUuidAndVersion(uuid: any, version: any, type: String) {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(
        map(response => {

          let ruleIO = new RuleIO();
          ruleIO.baseRule = response;

          // Filter Table
          if (response.filterInfo != null) {
            let filterInfoArray = [];
            if (response.filterInfo.length > 0) {
              for (let k = 0; k < response.filterInfo.length; k++) {
                let filterInfo = new FilterInfoIO();
                let lhsFilter = {};
                filterInfo.logicalOperator = response.filterInfo[k].logicalOperator
                filterInfo.lhsType = response.filterInfo[k].operand[0].ref.type;
                filterInfo.operator = response.filterInfo[k].operator;
                filterInfo.rhsType = response.filterInfo[k].operand[1].ref.type;

                if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.FORMULA) {
                  // this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
                  //   .subscribe(response => { this.onSuccessgetFormulaByLhsType(response) },
                  //     error => console.log("Error ::", error))

                  let lhsAttri1 = new AttributeIO();
                  lhsAttri1.uuid = response.filterInfo[k].operand[0].ref.uuid;
                  lhsAttri1.label = response.filterInfo[k].operand[0].ref.name;
                  filterInfo.lhsAttribute = lhsAttri1;

                  ruleIO.isFormulaExits = true;
                }

                else if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.DATAPOD) {

                  // this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
                  //   .subscribe(response => { this.onSuccessgetAllAttributeBySourceLhs(response) },
                  //     error => console.log("Error ::", error))
                  let lhsAttri = new AttributeIO();
                  lhsAttri.uuid = response.filterInfo[k].operand[0].ref.uuid;
                  lhsAttri.label = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
                  lhsAttri.attributeId = response.filterInfo[k].operand[0].attributeId;
                  filterInfo.lhsAttribute = lhsAttri;
                  ruleIO.isAttributeExits = true;
                }

                else if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.SIMPLE) {
                  let stringValue = response.filterInfo[k].operand[0].value;
                  let onlyNumbers = /^[0-9]+$/;
                  let result = onlyNumbers.test(stringValue);
                  if (result == true) {
                    filterInfo.lhsType = 'integer';
                  } else {
                    filterInfo.lhsType = 'string';
                  }
                  filterInfo.lhsAttribute = response.filterInfo[k].operand[0].value;
                }

                if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.FORMULA) {
                  // this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
                  //   .subscribe(response => { this.onSuccessgetFormulaByRhsType(response) },
                  //     error => console.log("Error ::", error))
                  //filterInfo["rhsAttribute"] = response.filterInfo[k].operand[1].ref.name;
                  let rhsAttri = new AttributeIO();
                  rhsAttri.uuid = response.filterInfo[k].operand[1].ref.uuid;
                  rhsAttri.label = response.filterInfo[k].operand[1].ref.name;
                  filterInfo.rhsAttribute = rhsAttri;


                  ruleIO.isFormulaExits = true;
                }

                else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.DATAPOD) {
                  // this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source)
                  //   .subscribe(response => { this.onSuccessgetAllAttributeBySourceRhs(response) },
                  //     error => console.log("Error ::", error))

                  let rhsAttri1 = new AttributeIO();
                  rhsAttri1.uuid = response.filterInfo[k].operand[1].ref.uuid;
                  rhsAttri1.label = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
                  rhsAttri1.attributeId = response.filterInfo[k].operand[1].attributeId;
                  filterInfo.rhsAttribute = rhsAttri1;

                  ruleIO.isAttributeExits = true;
                }

                else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.FUNCTION) {
                  // this._commonService.getFunctionByCriteria("", "N", "function")
                  //   .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
                  //     error => console.log("Error ::", error))

                  let rhsAttri = new AttributeIO();
                  rhsAttri.uuid = response.filterInfo[k].operand[1].ref.uuid;
                  rhsAttri.label = response.filterInfo[k].operand[1].ref.name;
                  filterInfo.rhsAttribute = rhsAttri;


                  ruleIO.isFunctionExits = true;
                }

                else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.PARAMLIST) {
                  
                 let rhsAttri = new AttributeIO(); 
                  
                  if (response.paramList != null && response.paramList != "")
                    rhsAttri.label = "rule." + response.filterInfo[k].operand[1].attributeName;
                  else
                    rhsAttri.label = "app." + response.filterInfo[k].operand[1].ref.type;
                  rhsAttri.uuid = response.filterInfo[k].operand[1].ref.uuid;
                  rhsAttri.attributeId = response.filterInfo[k].operand[1].attributeId.toString();

                  filterInfo.rhsAttribute = rhsAttri;
                  console.log("filterInfo.rhsAttribute : " + JSON.stringify(filterInfo.rhsAttribute));
                  
                  // ruleIO.paramlistArray1 = [{ "label": "rule.id", "value": { "label": "rule.id", "uuid": "12158dec-ab10-49f6-9ce0-6a2805df5a5f", "attributeId": "0" } }];
    
                  // this._commonService.getParamByApp("", MetaTypeEnum.MetaType.APPLICATION)
                  //   .subscribe(response => { this.onSuccessgetParamByApp(response) },
                  //     error => console.log("Error ::", error));
                  ruleIO.isParamlistExits = true;
                }

                else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.DATASET) {
                  let rhsAttri = new AttributeIO();
                  rhsAttri.uuid = response.filterInfo[k].operand[1].ref.uuid;
                  rhsAttri.attributeId = response.filterInfo[k].operand[1].attributeId;
                  rhsAttri.label = response.filterInfo[k].operand[1].attributeName;
                  filterInfo.rhsAttribute = rhsAttri;


                  ruleIO.isDatasetExits = true;
                }

                else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.SIMPLE) {
                  let stringValue = response.filterInfo[k].operand[1].value;
                  let onlyNumbers = /^[0-9]+$/;
                  let result = onlyNumbers.test(stringValue);
                  if (result == true) {
                    filterInfo.rhsType = 'integer';
                  } else {
                    filterInfo.rhsType = 'string';
                  }
                  filterInfo.rhsAttribute = response.filterInfo[k].operand[1].value;

                  let result2 = stringValue.includes("and")
                  if (result2 == true) {
                    filterInfo.rhsType = 'integer';

                    let betweenValArray = []
                    betweenValArray = stringValue.split("and");
                    filterInfo.rhsAttribute1 = betweenValArray[0];
                    filterInfo.rhsAttribute2 = betweenValArray[1];
                  }
                }
                filterInfoArray.push(filterInfo);


              }
            }
            ruleIO.filterInfo = filterInfoArray;
          }


          //updated attributeTableArray
          var attributesArray = [];
          for (var n = 0; n < response.attributeInfo.length; n++) {
            // var attributeInfo = [{
            //   sourceAttributeType: "", isSourceAtributeSimple: "",
            //   isSourceAtributeDatapod: "", isSourceAtributeFormula: "",
            //   isSourceAtributeExpression: "", isSourceAtributeFunction: "",
            //   isSourceAtributeParamList: ""
            // }];
            var attributeInfo = new AttributeInfoIO();
            // var attributeInfo = {name: "", sourcesimple:"", sourceAttributeType: {}, isSourceAtributeSimple: {},
            //   isSourceAtributeDatapod: {}, isSourceAtributeFormula: {},
            //   isSourceAtributeExpression: {}, isSourceAtributeFunction: {},
            //   isSourceAtributeParamList: {}, sourceattribute:{}, sourceexpression:{},
            //   sourceformula:{}, sourcefunction: {}, sourceparamlist:{}};

            attributeInfo.name = response.attributeInfo[n].attrSourceName
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.SIMPLE) {
              var obj = { text: "", caption: "" }
              obj.text = "string"
              obj.caption = "string"
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.isSourceAtributeSimple = true;
              attributeInfo.sourcesimple = response.attributeInfo[n].sourceAttr.value
              attributeInfo.isSourceAtributeDatapod = false;
              attributeInfo.isSourceAtributeFormula = false;
              attributeInfo.isSourceAtributeExpression = false;
              attributeInfo.isSourceAtributeFunction = false;
              attributeInfo.isSourceAtributeParamList = false;

            }
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.DATAPOD || response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.DATASET || response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.RULE) {
              var sourceattribute = new AttributeHolder();
              sourceattribute.label = response.attributeInfo[n].sourceAttr.ref.name + "." + response.attributeInfo[n].sourceAttr.attrName
              sourceattribute.u_Id = response.attributeInfo[n].sourceAttr.ref.uuid + "_" + response.attributeInfo[n].sourceAttr.attrId;
              sourceattribute.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
              sourceattribute.attrId = response.attributeInfo[n].sourceAttr.attrId
              sourceattribute.name = "";
              var obj = { text: "", caption: "" }
              obj.text = MetaTypeEnum.MetaType.DATAPOD
              obj.caption = "attribute"
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.sourceattribute = sourceattribute;
              attributeInfo.isSourceAtributeSimple = false;
              attributeInfo.isSourceAtributeDatapod = true;
              attributeInfo.isSourceAtributeFormula = false;
              attributeInfo.isSourceAtributeExpression = false;
              attributeInfo.isSourceAtributeFunction = false;
              attributeInfo.isSourceAtributeParamList = false;
            }
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.EXPRESSION) {
              var sourceexpression = { uuid: "", name: "" };
              sourceexpression.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
              sourceexpression.name = "";
              var obj = { text: "", caption: "" }
              obj.text = MetaTypeEnum.MetaType.EXPRESSION
              obj.caption = MetaTypeEnum.MetaType.EXPRESSION
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.sourceexpression = sourceexpression;
              attributeInfo.isSourceAtributeSimple = false;
              attributeInfo.isSourceAtributeDatapod = false;
              attributeInfo.isSourceAtributeFormula = false;
              attributeInfo.isSourceAtributeExpression = true;
              attributeInfo.isSourceAtributeFunction = false;
              attributeInfo.isSourceAtributeParamList = false;
            }
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.FORMULA) {
              var sourceformula = { uuid: "", name: "" };
              sourceformula.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
              sourceformula.name = "";
              var obj = { text: "", caption: "" }
              obj.text = MetaTypeEnum.MetaType.FORMULA
              obj.caption = MetaTypeEnum.MetaType.FORMULA
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.sourceformula = sourceformula;
              attributeInfo.isSourceAtributeSimple = false;
              attributeInfo.isSourceAtributeExpression = false;
              attributeInfo.isSourceAtributeDatapod = false;
              attributeInfo.isSourceAtributeFormula = true;
              attributeInfo.isSourceAtributeFunction = false;
              attributeInfo.isSourceAtributeParamList = false;
            }
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.FUNCTION) {
              var sourcefunction: AttributeHolder = new AttributeHolder();
              sourcefunction.u_Id = response.attributeInfo[n].sourceAttr.ref.uuid;
              sourcefunction.label = "";
              sourcefunction.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;

              var obj = { text: "", caption: "" }
              obj.text = MetaTypeEnum.MetaType.FUNCTION;
              obj.caption = MetaTypeEnum.MetaType.FUNCTION;
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.sourcefunction = sourcefunction;
              attributeInfo.isSourceAtributeSimple = false;
              attributeInfo.isSourceAtributeExpression = false;
              attributeInfo.isSourceAtributeDatapod = false;
              attributeInfo.isSourceAtributeFormula = false;
              attributeInfo.isSourceAtributeFunction = true;
              attributeInfo.isSourceAtributeParamList = false;
            }
            if (response.attributeInfo[n].sourceAttr.ref.type == MetaTypeEnum.MetaType.PARAMLIST) {
              var sourceparamlist = { uuid: "", attrId: "", name: "" };
              sourceparamlist.uuid = response.attributeInfo[n].sourceAttr.ref.uuid;
              sourceparamlist.attrId = response.attributeInfo[n].sourceAttr.attrId
              sourceparamlist.name = "";
              var obj = { text: "", caption: "" }
              obj.text = MetaTypeEnum.MetaType.PARAMLIST
              obj.caption = MetaTypeEnum.MetaType.PARAMLIST
              attributeInfo.sourceAttributeType = obj;
              attributeInfo.sourceparamlist = sourceparamlist;
              attributeInfo.isSourceAtributeSimple = false;
              attributeInfo.isSourceAtributeExpression = false;
              attributeInfo.isSourceAtributeDatapod = false;
              attributeInfo.isSourceAtributeFormula = false;
              attributeInfo.isSourceAtributeFunction = false;
              attributeInfo.isSourceAtributeParamList = true;
            }
            attributesArray[n] = attributeInfo
          }
          ruleIO.attributeTableArray = attributesArray

          return <any>ruleIO;
        }), catchError(error => this.handleError<string>(error, "Network Error!")))

  }
  onSuccessgetParamByApp(response: any[]): any {debugger
    let paramlistArray = [{ "label": "rule.id", "value": { "label": "rule.id", "uuid": "12158dec-ab10-49f6-9ce0-6a2805df5a5f", "attributeId": "0" } }];
    
  }



}