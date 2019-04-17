import { MetaType } from './../enums/metaType';
import { Observable, throwError } from 'rxjs';
import { Inject, Injectable, Input } from '@angular/core';
import { map, catchError } from "rxjs/operators";

import { Http, Response } from '@angular/http'

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
import { ReconIO } from '../domainIO/domain.reconIO';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import { AttributeIO } from '../domainIO/domain.attributeIO';


@Injectable()

export class DataReconService {

  metaType = MetaType;

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  // private handleError(error: Response) {
  //   return Observable.throw(error.statusText);
  // }

  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  
  getDataQualExecByDataqual(uuid: Number): Observable<any[]> {
    let url = '/dataQual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))

  }

  getdqGroupExecBydqGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqGroupExecBydqGroup?action=view&dqGroupUUID=' + uuid + '&dqGroupVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  executeRule(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/execute?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  getdqExecBydqGroupExec(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqExecBydqGroupExec?action=view&dataQualGroupExecUuid=' + uuid + '&dataQualGroupExecVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  executeDQGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/executeGroup?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  getNumRowsbyExec(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = '/metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + '&execVersion=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }
  getFunctionByCategory(type) {
    let url = "metadata/getFunctionByCategory?action=view&type=" + type
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }


  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(map(response => {
        let reconJson = new ReconIO();
        reconJson.recon = response;
        let filterInfoIOArray: Array<FilterInfoIO> = [];
        if (response.sourceFilter) {
          for (let k = 0; k < response.sourceFilter.length; k++) {
            let filterInfoIO = new FilterInfoIO();
            filterInfoIO.logicalOperator = response.sourceFilter[k].logicalOperator
            filterInfoIO.lhsType = response.sourceFilter[k].operand[0].ref.type;
            filterInfoIO.operator = response.sourceFilter[k].operator;
            filterInfoIO.rhsType = response.sourceFilter[k].operand[1].ref.type;

            if (response.sourceFilter[k].operand[0].ref.type == this.metaType.FORMULA) {
              let lhsAttribute = new AttributeIO();
              lhsAttribute.uuid = response.sourceFilter[k].operand[0].ref.uuid;
              lhsAttribute.label = response.sourceFilter[k].operand[0].ref.name;
              filterInfoIO.lhsAttribute = lhsAttribute;
            }
            else if (response.sourceFilter[k].operand[0].ref.type == this.metaType.DATAPOD) {
              let lhsAttribute = new AttributeIO();
              lhsAttribute.uuid = response.sourceFilter[k].operand[0].ref.uuid;
              lhsAttribute.label = response.sourceFilter[k].operand[0].ref.name + "." + response.sourceFilter[k].operand[0].attributeName;
              lhsAttribute.attributeId = response.sourceFilter[k].operand[0].attributeId;
              lhsAttribute.id = response.sourceFilter[k].operand[0].ref.uuid + "_" + response.sourceFilter[k].operand[0].attributeId;
              filterInfoIO.lhsAttribute = lhsAttribute;
            }
            else if (response.sourceFilter[k].operand[0].ref.type == 'attribute') {
              filterInfoIO.lhsType = 'attribute';
              filterInfoIO.lhsAttribute = response.sourceFilter[k].operand[0].value;
            }

            else if (response.sourceFilter[k].operand[0].ref.type == this.metaType.SIMPLE) {
              let stringValue = response.sourceFilter[k].operand[0].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfoIO.lhsType = 'integer';
              } else {
                filterInfoIO.lhsType = 'string';
              }
              filterInfoIO.lhsAttribute = response.sourceFilter[k].operand[0].value;
            }

            if (response.sourceFilter[k].operand[1].ref.type == this.metaType.FORMULA) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.sourceFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.sourceFilter[k].operand[1].ref.name;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == this.metaType.FUNCTION) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.sourceFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.sourceFilter[k].operand[1].ref.name;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == this.metaType.PARAMLIST) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.sourceFilter[k].operand[1].ref.uuid;
              rhsAttribute.attributeId = response.sourceFilter[k].operand[1].attributeId;
              rhsAttribute.label = "app." + response.sourceFilter[k].operand[1].attributeName;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == this.metaType.DATASET) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.sourceFilter[k].operand[1].ref.uuid;
              rhsAttribute.attributeId = response.sourceFilter[k].operand[1].attributeId;
              rhsAttribute.label = response.sourceFilter[k].operand[1].attributeName;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == this.metaType.DATAPOD) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.sourceFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.sourceFilter[k].operand[1].ref.name + "." + response.sourceFilter[k].operand[1].attributeName;
              rhsAttribute.attributeId = response.sourceFilter[k].operand[1].attributeId;
              rhsAttribute.id = response.sourceFilter[k].operand[1].ref.uuid + "_" + response.sourceFilter[k].operand[1].attributeId;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == 'attribute') {
              filterInfoIO.rhsType = this.metaType.DATAPOD;
              filterInfoIO.rhsAttribute = response.sourceFilter[k].operand[1].value;
            }
            else if (response.sourceFilter[k].operand[1].ref.type == this.metaType.SIMPLE) {
              let stringValue = response.sourceFilter[k].operand[1].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfoIO.rhsType = 'integer';
              } else {
                filterInfoIO.rhsType = 'string';
              }
              filterInfoIO.rhsAttribute = response.sourceFilter[k].operand[1].value;

              let result2 = stringValue.includes("and")
              if (result2 == true) {
                filterInfoIO.rhsType = 'integer';

                let betweenValArray = []
                betweenValArray = stringValue.split("and");
                filterInfoIO.rhsAttribute1 = betweenValArray[0];
                filterInfoIO.rhsAttribute2 = betweenValArray[1];
              }
            }
            filterInfoIOArray.push(filterInfoIO);
            console.log(filterInfoIOArray);
            reconJson.filterTableArray = filterInfoIOArray;
          }
        }
        else {
          reconJson.filterTableArray = null;
        }

        if (response.targetFilter != null) {
          let filterInfoIOArray: Array<FilterInfoIO> = [];
          for (let k = 0; k < response.targetFilter.length; k++) {
            let filterInfoIO = new FilterInfoIO;
            filterInfoIO.logicalOperator = response.targetFilter[k].logicalOperator
            filterInfoIO.lhsType = response.targetFilter[k].operand[0].ref.type;
            filterInfoIO.operator = response.targetFilter[k].operator;
            filterInfoIO.rhsType = response.targetFilter[k].operand[1].ref.type;

            if (response.targetFilter[k].operand[0].ref.type == this.metaType.FORMULA) {
              let lhsAttribute = new AttributeIO();
              lhsAttribute.uuid = response.targetFilter[k].operand[0].ref.uuid;
              lhsAttribute.label = response.targetFilter[k].operand[0].ref.name;
              filterInfoIO.lhsAttribute = lhsAttribute;
            }
            else if (response.targetFilter[k].operand[0].ref.type == this.metaType.DATAPOD) {
              let lhsAttribute = new AttributeIO();
              lhsAttribute.uuid = response.targetFilter[k].operand[0].ref.uuid;
              lhsAttribute.label = response.targetFilter[k].operand[0].ref.name + "." + response.targetFilter[k].operand[0].attributeName;
              lhsAttribute.attributeId = response.targetFilter[k].operand[0].attributeId;
              lhsAttribute.id = response.targetFilter[k].operand[0].ref.uuid + "_" + response.targetFilter[k].operand[0].attributeId;
              filterInfoIO.lhsAttribute = lhsAttribute;
            }
            else if (response.targetFilter[k].operand[0].ref.type == 'attribute') {
              filterInfoIO.lhsType = 'attribute';
              filterInfoIO.lhsAttribute = response.targetFilter[k].operand[0].value;
            }

            else if (response.targetFilter[k].operand[0].ref.type == this.metaType.SIMPLE) {
              let stringValue = response.targetFilter[k].operand[0].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfoIO.lhsType = 'integer';
              } else {
                filterInfoIO.lhsType = 'string';
              }
              filterInfoIO.lhsAttribute = response.targetFilter[k].operand[0].value;
            }

            if (response.targetFilter[k].operand[1].ref.type == this.metaType.FORMULA) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.targetFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.targetFilter[k].operand[1].ref.name;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.targetFilter[k].operand[1].ref.type == this.metaType.FUNCTION) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.targetFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.targetFilter[k].operand[1].ref.name;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.targetFilter[k].operand[1].ref.type == this.metaType.PARAMLIST) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.targetFilter[k].operand[1].ref.uuid;
              rhsAttribute.attributeId = response.targetFilter[k].operand[1].attributeId;
              rhsAttribute.label = "app." + response.targetFilter[k].operand[1].attributeName;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.targetFilter[k].operand[1].ref.type == this.metaType.DATASET) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.targetFilter[k].operand[1].ref.uuid;
              rhsAttribute.attributeId = response.targetFilter[k].operand[1].attributeId;
              rhsAttribute.label = response.targetFilter[k].operand[1].attributeName;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.targetFilter[k].operand[1].ref.type == this.metaType.DATAPOD) {
              let rhsAttribute = new AttributeIO();
              rhsAttribute.uuid = response.targetFilter[k].operand[1].ref.uuid;
              rhsAttribute.label = response.targetFilter[k].operand[1].ref.name + "." + response.targetFilter[k].operand[1].attributeName;
              rhsAttribute.attributeId = response.targetFilter[k].operand[1].attributeId;
              rhsAttribute.id = response.targetFilter[k].operand[1].ref.uuid + "_" + response.targetFilter[k].operand[1].attributeId;
              filterInfoIO.rhsAttribute = rhsAttribute;
            }
            else if (response.targetFilter[k].operand[1].ref.type == 'attribute') {
              filterInfoIO.rhsType = this.metaType.DATAPOD;
              filterInfoIO.rhsAttribute = response.targetFilter[k].operand[1].value;
            }
            else if (response.targetFilter[k].operand[1].ref.type == this.metaType.SIMPLE) {
              let stringValue = response.targetFilter[k].operand[1].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfoIO.rhsType = 'integer';
              } else {
                filterInfoIO.rhsType = 'string';
              }
              filterInfoIO.rhsAttribute = response.targetFilter[k].operand[1].value;

              let result2 = stringValue.includes("and")
              if (result2 == true) {
                filterInfoIO.rhsType = 'integer';

                let betweenValArray = []
                betweenValArray = stringValue.split("and");
                filterInfoIO.rhsAttribute1 = betweenValArray[0];
                filterInfoIO.rhsAttribute2 = betweenValArray[1];
              }
            }
            filterInfoIOArray.push(filterInfoIO);
            console.log(filterInfoIOArray);
            reconJson.targetFilterTableArray = filterInfoIOArray;
          }
        }
        else {
          reconJson.targetFilterTableArray = null;
        }
        console.log(response)
        return <any>reconJson;
      }), catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  getReconExecByRecon(uuid: number, startDate: string, endDate: string): Observable<any[]> {
    let url = '/recon/getReconExecByRecon?action=view&uuid=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }

  getResults(uuid: Number, version: String, type: String, mode: String, requestId?: Number): Observable<any[]> {
    let url = '/recon/getResults?action=view&uuid=' + uuid + '&version=' + version + '&type=' + type + '&mode=' + mode + '&requestId=' + requestId;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")))
  }



}