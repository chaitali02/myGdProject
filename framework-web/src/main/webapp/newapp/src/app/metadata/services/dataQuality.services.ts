import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';



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
          let dqJson = {};
          dqJson["dqdata"] = response;
          return <any>dqJson;
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}