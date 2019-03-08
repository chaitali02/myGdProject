import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';

import { DataQualityIO } from '../domainIO/domain.dataQualityIO';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import * as MetaTypeEnum from '../../metadata/enums/metaType';
import { AttributeIO } from '../domainIO/domain.attributeIO';



@Injectable()

export class DataQualityService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  getDataQualExecByDataqual(uuid: Number): Observable<any[]> {
    let url = '/dataQual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDataQualExecByDataqual1(uuid: Number, startDate: Date, endDate: Date): Observable<any[]> {
    let url = '/dataqual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getdqExecByDatapod(uuid: Number, startDate: Date, endDate: Date): Observable<any[]> {
    let url = '/dataqual/getdqExecByDatapod?action=view&datapodUUID=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getSummary(uuid: Number, version: String, type: String, mode: String): Observable<any[]> {
    let url = '/dataqual/getSummary?action=view&dataQualExecUUID=' + uuid + '&dataQualExecVersion=' + version + '&type=' + type + '&mode=' + mode;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getdqGroupExecBydqGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqGroupExecBydqGroup?action=view&dqGroupUUID=' + uuid + '&dqGroupVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeRule(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/execute?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getdqExecBydqGroupExec(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqExecBydqGroupExec?action=view&dataQualGroupExecUuid=' + uuid + '&dataQualGroupExecVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeDQGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/executeGroup?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getNumRowsbyExec(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = '/metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + '&execVersion=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }



  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(
        map(response => {
          let dataQualityIO = new DataQualityIO();
          dataQualityIO.dataQuality = response;
          let filterInfoIOArray = [new FilterInfoIO];
          if (response.filterInfo) {
            for (let k = 0; k < response.filterInfo.length; k++) {
              let filterInfoIO = new FilterInfoIO();
              filterInfoIO.logicalOperator = response.filterInfo[k].logicalOperator;
              filterInfoIO.lhsType = response.filterInfo[k].operand[0].ref.type;
              filterInfoIO.operator = response.filterInfo[k].operator;
              filterInfoIO.rhsType = response.filterInfo[k].operand[1].ref.type;

              if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.FORMULA) {
                let lhsAttribute = new AttributeIO();
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name;
                filterInfoIO.lhsAttribute = lhsAttribute;
                dataQualityIO.isFormulaExits = true;
              }

              else if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.DATAPOD) {
                let lhsAttribute = new AttributeIO();
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
                lhsAttribute.attributeId = response.filterInfo[k].operand[0].attributeId.toString();
                filterInfoIO.lhsAttribute = lhsAttribute;
                dataQualityIO.isAttributeExits = true;
              }

              else if (response.filterInfo[k].operand[0].ref.type == MetaTypeEnum.MetaType.SIMPLE) {
                dataQualityIO.isSimpleExits = true;

                let stringValue: String = response.filterInfo[k].operand[0].value;
                let onlyNumbers = /^[0-9]+$/;
                let result = onlyNumbers.test(stringValue.toString());
                if (result == true) {
                  filterInfoIO.lhsType = 'integer';
                }
                else {
                  filterInfoIO.lhsType = 'string';
                }
                filterInfoIO.lhsAttribute = response.filterInfo[k].operand[0].value;
              }


              if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.FORMULA) {
                dataQualityIO.isFormulaExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute = response.filterInfo[k].operand[1].ref.name;
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.DATAPOD) {
                dataQualityIO.isAttributeExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId.toString();
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.PARAMLIST) {
                dataQualityIO.isParamlistExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = "app." + response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.FUNCTION) {
                dataQualityIO.isFunctionExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.DATASET) {
                dataQualityIO.isDatasetExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == MetaTypeEnum.MetaType.SIMPLE) {
                let stringValue = response.filterInfo[k].operand[1].value;
                let onlyNumbers = /^[0-9]+$/;
                let result = onlyNumbers.test(stringValue.toString());
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
              filterInfoIOArray[k] = filterInfoIO;
              dataQualityIO.filterInfoIo = filterInfoIOArray;
            }
          }
          console.log(dataQualityIO);
          return <any>dataQualityIO;
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}