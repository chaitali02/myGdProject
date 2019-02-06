

import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class RuleResultService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAll(metavalue: String): Observable<any[]> {
    let url = '/common/getAll?action=view&type=' + metavalue;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllLatest(metavalue: String): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + metavalue;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeRule(version: String, uuid: Number): Observable<any[]> {
    let url = '/rule/executeRule?action=execute&ruleUUID=' + uuid + '&ruleVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeRuleGroup(version: String, uuid: Number): Observable<any[]> {
    let url = '/rule/executeRuleGroup?action=execute&ruleGroupUUID=' + uuid + '&ruleGroupVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRuleExecByRGExec(uuid: Number, version: string): Observable<any[]> {
    let url = '/rule/getRuleExecByRGExec?action=view&ruleGroupExecUUID=' + uuid + '&ruleGroupExecVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRuleGroupExecByRuleGroup(uuid: Number, version: string): Observable<any[]> {
    let url = '/rule/getRuleGroupExecByRuleGroup?action=view&ruleGroupUUID=' + uuid + '&ruleGroupVersion=' + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getBaseEntityStatusByCriteria(uuid: Number, version: string, type: String, name: String, userName: String, startDate: Date, endDate: Date, tags: String, status: String): Observable<any[]> {
    let url = '/metadata/getBaseEntityStatusByCriteria?action=view&type=' + type + '&name=' + name + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&status=' + status;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getNumRowsbyExec(uuid: Number, version: string, type: String): Observable<any[]> {
    let url = '/metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + '&execVersion=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}