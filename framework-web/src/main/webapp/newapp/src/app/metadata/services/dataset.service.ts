import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { DatasetIO } from '../domainIO/domain.datasetIO';
import { AttributeIO } from '../domainIO/domain.attributeIO';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import { AttributeSourceIO } from '../domainIO/domain.AttributeSourceIO';

@Injectable()
export class DatasetService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getFormulaByType2(uuid: Number, type: any): Observable<any[]> {
    let url = '/metadata/getFormulaByType2?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getExpressionByType2(uuid: Number, type: Number): Observable<any[]> {
    let url = '/metadata/getExpressionByType2?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDatasetSample(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataset/getDatasetSample?action=view&datasetUUID=' + uuid + '&datasetVersion=' + version + '&row=100';
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
  getAllLatestFunction(type: any, inputFlag: any): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type + '&inputFlag=' + inputFlag;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getFunctionByCriteria(category: any, inputReq: any, type: any): Observable<any[]> {
    let url = '/metadata/getFunctionByCriteria?action=view&category=' + category + '&inputReq=' + inputReq + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamByApp(uuid: any, type: any): Observable<any[]> {
    let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getOneByUuidAndVersion(type: any, version: any, id: any) {
    return this._commonService.getOneByUuidAndVersion(type, version, id)
      .pipe(
        map(response => {
          let datasetIO = new DatasetIO();
          datasetIO.datasetData = response;

          if (response.filterInfo != null) {
            let filterInfoArray = [new FilterInfoIO];
            for (let k = 0; k < response.filterInfo.length; k++) {
              let filterInfoIO = new FilterInfoIO();
              filterInfoIO.logicalOperator = response.filterInfo[k].logicalOperator
              filterInfoIO.lhsType = response.filterInfo[k].operand[0].ref.type;
              filterInfoIO.operator = response.filterInfo[k].operator;
              filterInfoIO.rhsType = response.filterInfo[k].operand[1].ref.type;

              if (response.filterInfo[k].operand[0].ref.type == 'formula') {
                let lhsAttribute = new AttributeIO()
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name;
                filterInfoIO.lhsAttribute = lhsAttribute;
              }
              else if (response.filterInfo[k].operand[0].ref.type == 'datapod') {
                let lhsAttribute = new AttributeIO();
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
                lhsAttribute.attributeId = response.filterInfo[k].operand[0].attributeId;
                filterInfoIO.lhsAttribute = lhsAttribute;
              }
              else if (response.filterInfo[k].operand[0].ref.type == 'attribute') {
                filterInfoIO.lhsType = 'datapod';
                filterInfoIO.lhsAttribute = response.filterInfo[k].operand[0].value;
              }
              else if (response.filterInfo[k].operand[0].ref.type == 'simple') {
                let stringValue = response.filterInfo[k].operand[0].value;
                let onlyNumbers = /^[0-9]+$/;
                let result = onlyNumbers.test(stringValue);
                if (result == true) {
                  filterInfoIO.lhsType = 'integer';
                } else {
                  filterInfoIO.lhsType = 'string';
                }
                filterInfoIO.lhsAttribute = response.filterInfo[k].operand[0].value;
              }

              if (response.filterInfo[k].operand[1].ref.type == 'formula') {
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'function') {
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'paramlist') {
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = "app." + response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'dataset') {
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'datapod') {
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'attribute') {
                filterInfoIO.rhsType = 'datapod';
                filterInfoIO.rhsAttribute = response.filterInfo[k].operand[1].value;
              }
              else if (response.filterInfo[k].operand[1].ref.type == 'simple') {
                let stringValue = response.filterInfo[k].operand[1].value;
                let onlyNumbers = /^[0-9]+$/;
                let result = onlyNumbers.test(stringValue);
                if (result == true) {
                  filterInfoIO.rhsType = 'integer';
                } else {
                  filterInfoIO.rhsType = 'string';
                }
                filterInfoIO.rhsAttribute = response.filterInfo[k].operand[1].value;

                let result2 = stringValue.includes("and")
                if (result2 == true) {
                  filterInfoIO.rhsType = 'integer';
                  let betweenValArray = []
                  betweenValArray = stringValue.split("and");
                  filterInfoIO.rhsAttribute1 = betweenValArray[0];
                  filterInfoIO.rhsAttribute2 = betweenValArray[1];
                }
              }
              filterInfoArray[k] = filterInfoIO;
              console.log(filterInfoArray)
              datasetIO.filterInfo = filterInfoArray;
            }
          } else {
            datasetIO.filterInfo = null;
          }

          if (response.attributeInfo != null) {
            let attributearray = [];
            for (let i = 0; i < response.attributeInfo.length; i++) {
              let attributeinfojson = new AttributeSourceIO;
              attributeinfojson.name = response.attributeInfo[i].attrSourceName
              if (response.attributeInfo[i].sourceAttr.ref.type == "datapod" || response.attributeInfo[i].sourceAttr.ref.type == "dataset" || response.attributeInfo[i].sourceAttr.ref.type == "rule") {
                var sourceattribute = new AttributeIO();
                sourceattribute.uuid = response.attributeInfo[i].sourceAttr.ref.uuid;
                sourceattribute.label = response.attributeInfo[i].sourceAttr.ref.name;
                sourceattribute.dname = response.attributeInfo[i].sourceAttr.ref.name + '.' + response.attributeInfo[i].sourceAttr.attrName;
                sourceattribute.type = response.attributeInfo[i].sourceAttr.ref.type;
                sourceattribute.attributeId = response.attributeInfo[i].sourceAttr.attrId;
                sourceattribute.id = sourceattribute.uuid + "_" + sourceattribute.attributeId;
                let obj = { "value": "", "label": "" }
                obj.value = "datapod"
                obj.label = "attribute"
                attributeinfojson.sourceAttributeType = obj;
                attributeinfojson.isSourceAtributeSimple = false;
                attributeinfojson.isSourceAtributeDatapod = true;
                attributeinfojson.isSourceAtributeFormula = false;
                attributeinfojson.isSourceAtributeExpression = false;
                // this.getAllAttributeBySource();

              }
              else if (response.attributeInfo[i].sourceAttr.ref.type == "simple") {
                let obj = { "value": "", "label": "" }
                obj.value = "string"
                obj.label = "string"
                attributeinfojson.sourceAttributeType = obj;
                attributeinfojson.isSourceAtributeSimple = true;
                attributeinfojson.sourcesimple = response.attributeInfo[i].sourceAttr.value
                attributeinfojson.isSourceAtributeDatapod = false;
                attributeinfojson.isSourceAtributeFormula = false;
                attributeinfojson.isSourceAtributeExpression = false;
                attributeinfojson.isSourceAtributeFunction = false;

              }
              if (response.attributeInfo[i].sourceAttr.ref.type == "expression") {
                let sourceexpression = new AttributeIO();;
                sourceexpression.uuid = response.attributeInfo[i].sourceAttr.ref.uuid;
                sourceexpression.label = response.attributeInfo[i].sourceAttr.ref.name
                let obj = { "value": "", "label": "" }
                obj.value = "expression"
                obj.label = "expression"
                attributeinfojson.sourceAttributeType = obj;

                attributeinfojson.sourceexpression = sourceexpression;

                attributeinfojson.isSourceAtributeSimple = false;
                attributeinfojson.isSourceAtributeDatapod = false;
                attributeinfojson.isSourceAtributeFormula = false;
                attributeinfojson.isSourceAtributeExpression = true;
                attributeinfojson.isSourceAtributeFunction = false;
                //this.getAllExpression(false, 0)
              }
              if (response.attributeInfo[i].sourceAttr.ref.type == "formula") {
                let sourceformula = {};
                sourceformula["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                sourceformula["label"] = response.attributeInfo[i].sourceAttr.ref.name;
                let obj = { "value": "", "label": "" }
                obj.value = "formula"
                obj.label = "formula"
                attributeinfojson.sourceAttributeType = obj;
                attributeinfojson.sourceformula = sourceformula;
                attributeinfojson.isSourceAtributeSimple = false;
                attributeinfojson.isSourceAtributeDatapod = false;
                attributeinfojson.isSourceAtributeFormula = true;
                attributeinfojson.isSourceAtributeExpression = false;
                attributeinfojson.isSourceAtributeFunction = false;
                //this.getAllFormula(false, 0);
              }
              if (response.attributeInfo[i].sourceAttr.ref.type == "function") {
                let sourcefunction = {};
                sourcefunction["uuid"] = response.attributeInfo[i].sourceAttr.ref.uuid;
                sourcefunction["label"] = response.attributeInfo[i].sourceAttr.ref.name
                let obj = { "value": "", "label": "" }
                obj.value = "function"
                obj.label = "function"
                attributeinfojson.sourceAttributeType = obj;
                attributeinfojson.sourcefunction = sourcefunction;
                attributeinfojson.isSourceAtributeSimple = false;
                attributeinfojson.isSourceAtributeDatapod = false;
                attributeinfojson.isSourceAtributeFormula = false;
                attributeinfojson.isSourceAtributeExpression = false;
                attributeinfojson.isSourceAtributeFunction = true;
                //this.getAllFunctions(false, 0);
              }
              attributeinfojson.sourceattribute = sourceattribute;
              //attributeinfojson["duplicateString"] = this.duplicateString;
              attributearray[i] = attributeinfojson
            }
            datasetIO.attributeInfo = attributearray
          }
          else {
            datasetIO.attributeInfo = null;
          }


          console.log(datasetIO);
          return datasetIO;

        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}
