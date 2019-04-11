import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { FilterInfoIO } from '../domainIO/domain.filterInfoIO';
import { ProfileIO } from '../domainIO/domain.profileIO';
import { MetaType } from '../enums/metaType';
import { AttributeIO } from '../domainIO/domain.attributeIO';


@Injectable()

export class ProfileService {

  metaType = MetaType;

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getProfileExecByDatapod(datapodUUID: any, type: any, startDate: any, endDate: any): Observable<any[]> {
    let url = 'profile/getProfileExecByDatapod?action=view&datapodUUID=' + datapodUUID + "&type=" + type + "&startDate=" + startDate + "&endDate=" + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getResults(uuid: number, version: string, mode: string): Observable<any[]> {
    let url = 'profile/getResults?action=view&uuid=' + uuid + '&version=' + version + '&mode=' + mode;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getProfileResults(type: string, uuid: number, version: string, attributeId: number, numDays: number, profileAttrType: string): Observable<any[]> {
    let url = 'profile/getProfileResults?action=view&type=' + type + '&datapodUuid=' + uuid + '&datapodVersion=' + version + '&attributeId=' + attributeId + '&numDays=' + numDays + '&profileAttrType=' + profileAttrType;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(
        map(response => {
          let profileIO = new ProfileIO();
          profileIO.profile = response;
          let filterInfoIOArray = [new FilterInfoIO];
          if (response.filterInfo) {
            for (let k = 0; k < response.filterInfo.length; k++) {
              let filterInfoIO = new FilterInfoIO();
              filterInfoIO.logicalOperator = response.filterInfo[k].logicalOperator;
              filterInfoIO.lhsType = response.filterInfo[k].operand[0].ref.type;
              filterInfoIO.operator = response.filterInfo[k].operator;
              filterInfoIO.rhsType = response.filterInfo[k].operand[1].ref.type;

              if (response.filterInfo[k].operand[0].ref.type == this.metaType.FORMULA) {
                let lhsAttribute = new AttributeIO();
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name;
                filterInfoIO.lhsAttribute = lhsAttribute;
                profileIO.isFormulaExits = true;
              }

              else if (response.filterInfo[k].operand[0].ref.type == this.metaType.DATAPOD) {
                let lhsAttribute = new AttributeIO();
                lhsAttribute.uuid = response.filterInfo[k].operand[0].ref.uuid;
                lhsAttribute.label = response.filterInfo[k].operand[0].ref.name + "." + response.filterInfo[k].operand[0].attributeName;
                lhsAttribute.attributeId = response.filterInfo[k].operand[0].attributeId.toString();
                filterInfoIO.lhsAttribute = lhsAttribute;
                profileIO.isAttributeExits = true;
              }

              else if (response.filterInfo[k].operand[0].ref.type == this.metaType.SIMPLE) {
                profileIO.isSimpleExits = true;

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


              if (response.filterInfo[k].operand[1].ref.type == this.metaType.FORMULA) {
                profileIO.isFormulaExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute = response.filterInfo[k].operand[1].ref.name;
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == this.metaType.DATAPOD) {
                profileIO.isAttributeExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name + "." + response.filterInfo[k].operand[1].attributeName;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId.toString();
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == this.metaType.PARAMLIST) {
                profileIO.isParamlistExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = "app." + response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == this.metaType.FUNCTION) {
                profileIO.isFunctionExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.label = response.filterInfo[k].operand[1].ref.name;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == this.metaType.DATASET) {
                profileIO.isDatasetExits = true;
                let rhsAttribute = new AttributeIO();
                rhsAttribute.uuid = response.filterInfo[k].operand[1].ref.uuid;
                rhsAttribute.attributeId = response.filterInfo[k].operand[1].attributeId;
                rhsAttribute.label = response.filterInfo[k].operand[1].attributeName;
                filterInfoIO.rhsAttribute = rhsAttribute;
              }
              else if (response.filterInfo[k].operand[1].ref.type == this.metaType.SIMPLE) {
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
              profileIO.filterInfoIo = filterInfoIOArray;
            }
          }
          console.log(profileIO);
          return <any>profileIO;
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
