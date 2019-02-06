import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'


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

  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(
        map(response => {
          let dqJson = {};
          dqJson["dqdata"] = response
          var filterInfoArray = [];
          if (response["filter"] != null) {
            for (var i = 0; i < response["filter"].filterInfo.length; i++) {
              let filterInfo = {};
              filterInfo["logicalOperator"] = response["filter"].filterInfo[i].logicalOperator
              filterInfo["operator"] = response["filter"].filterInfo[i].operator;
              let lhsFilter: AttributeHolder = new AttributeHolder();
              lhsFilter.label = response["filter"].filterInfo[i].operand[0].ref["name"] + "." + response["filter"].filterInfo[i].operand[0].attributeName;;
              lhsFilter.u_Id = response["filter"].filterInfo[i].operand[0].ref.uuid + "_" + response["filter"].filterInfo[i].operand[0].attributeId
              lhsFilter.uuid = response["filter"].filterInfo[i].operand[0].ref.uuid;
              lhsFilter.attrId = response["filter"].filterInfo[i].operand[0].attributeId;
              filterInfo["lhsFilter"] = lhsFilter;
              filterInfo["filtervalue"] = response["filter"].filterInfo[i].operand[1].value;
              filterInfoArray[i] = filterInfo
            }
          }
          dqJson["filterInfo"] = filterInfoArray;
          var sourceAttributesArray = [];
          for (var n = 0; n < response["attributeInfo"].length; n++) {
            var attributeInfo = {};
            attributeInfo["name"] = response["attributeInfo"][n].attrSourceName
            if (response["attributeInfo"][n].sourceAttr.ref.type == "simple") {
              var obj = {}
              obj["text"] = "string"
              obj["caption"] = "string"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["isSourceAtributeSimple"] = true;
              attributeInfo["sourcesimple"] = response["attributeInfo"][n].sourceAttr.value
              attributeInfo["isSourceAtributeDatapod"] = false;
              attributeInfo["isSourceAtributeFormula"] = false;
              attributeInfo["isSourceAtributeExpression"] = false;
              attributeInfo["isSourceAtributeFunction"] = false;
              attributeInfo["isSourceAtributeParamList"] = false;

            }
            if (response["attributeInfo"][n].sourceAttr.ref.type == "datapod" || response["attributeInfo"][n].sourceAttr.ref.type == "dataset" || response["attributeInfo"][n].sourceAttr.ref.type == "rule") {
              var sourceattribute: AttributeHolder = new AttributeHolder();
              sourceattribute.label = response["attributeInfo"][n].sourceAttr.ref.name + "." + response["attributeInfo"][n].sourceAttr.attrName
              sourceattribute.u_Id = response["attributeInfo"][n].sourceAttr.ref.uuid + "_" + response["attributeInfo"][n].sourceAttr.attrId;
              sourceattribute.uuid = response["attributeInfo"][n].sourceAttr.ref.uuid;
              sourceattribute.attrId = response["attributeInfo"][n].sourceAttr.attrId
              sourceattribute["name"] = "";
              var obj = {}
              obj["text"] = "datapod"
              obj["caption"] = "attribute"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["sourceattribute"] = sourceattribute;
              attributeInfo["isSourceAtributeSimple"] = false;
              attributeInfo["isSourceAtributeDatapod"] = true;
              attributeInfo["isSourceAtributeFormula"] = false;
              attributeInfo["isSourceAtributeExpression"] = false;
              attributeInfo["isSourceAtributeFunction"] = false;
              attributeInfo["isSourceAtributeParamList"] = false;
            }
            if (response["attributeInfo"][n].sourceAttr.ref.type == "expression") {
              var sourceexpression = {};
              sourceexpression["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
              sourceexpression["name"] = "";
              var obj = {}
              obj["text"] = "expression"
              obj["caption"] = "expression"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["sourceexpression"] = sourceexpression;
              attributeInfo["isSourceAtributeSimple"] = false;
              attributeInfo["isSourceAtributeDatapod"] = false;
              attributeInfo["isSourceAtributeFormula"] = false;
              attributeInfo["isSourceAtributeExpression"] = true;
              attributeInfo["isSourceAtributeFunction"] = false;
              attributeInfo["isSourceAtributeParamList"] = false;
            }
            if (response["attributeInfo"][n].sourceAttr.ref.type == "formula") {
              var sourceformula = {};
              sourceformula["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
              sourceformula["name"] = "";
              var obj = {}
              obj["text"] = "formula"
              obj["caption"] = "formula"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["sourceformula"] = sourceformula;
              attributeInfo["isSourceAtributeSimple"] = false;
              attributeInfo["isSourceAtributeExpression"] = false;
              attributeInfo["isSourceAtributeDatapod"] = false;
              attributeInfo["isSourceAtributeFormula"] = true;
              attributeInfo["isSourceAtributeFunction"] = false;
              attributeInfo["isSourceAtributeParamList"] = false;
            }
            if (response["attributeInfo"][n].sourceAttr.ref.type == "function") {
              var sourcefunction: AttributeHolder = new AttributeHolder();
              sourcefunction.u_Id = response["attributeInfo"][n].sourceAttr.ref.uuid;
              sourcefunction.label = "";
              sourcefunction.uuid = response["attributeInfo"][n].sourceAttr.ref.uuid;

              var obj = {}
              obj["text"] = "function"
              obj["caption"] = "function"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["sourcefunction"] = sourcefunction;
              attributeInfo["isSourceAtributeSimple"] = false;
              attributeInfo["isSourceAtributeExpression"] = false;
              attributeInfo["isSourceAtributeDatapod"] = false;
              attributeInfo["isSourceAtributeFormula"] = false;
              attributeInfo["isSourceAtributeFunction"] = true;
              attributeInfo["isSourceAtributeParamList"] = false;
            }
            if (response["attributeInfo"][n].sourceAttr.ref.type == "paramlist") {
              var sourceparamlist = {};
              sourceparamlist["uuid"] = response["attributeInfo"][n].sourceAttr.ref.uuid;
              sourceparamlist["attrId"] = response["attributeInfo"][n].sourceAttr.attrId
              sourceparamlist["name"] = "";
              var obj = {}
              obj["text"] = "paramlist"
              obj["caption"] = "paramlist"
              attributeInfo["sourceAttributeType"] = obj;
              attributeInfo["sourceparamlist"] = sourceparamlist;
              attributeInfo["isSourceAtributeSimple"] = false;
              attributeInfo["isSourceAtributeExpression"] = false;
              attributeInfo["isSourceAtributeDatapod"] = false;
              attributeInfo["isSourceAtributeFormula"] = false;
              attributeInfo["isSourceAtributeFunction"] = false;
              attributeInfo["isSourceAtributeParamList"] = true;
            }
            sourceAttributesArray[n] = attributeInfo
          }
          dqJson["sourceAttributes"] = sourceAttributesArray
          console.log(response)
          return <any>dqJson;
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRuleExecByRule(uuid: Number, startDate: String, endDate: String): Observable<any[]> {
    let url = '/rule/getRuleExecByRule?action=view&ruleUuid=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}