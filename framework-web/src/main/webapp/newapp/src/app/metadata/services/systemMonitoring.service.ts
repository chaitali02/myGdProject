
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class SystemMonitoringService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getMetaExecList(): Observable<any[]> {
    let url = '/metadata/getMetaExecList?type=session&action=view';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getActiveSession(userName: String, appuuid: Number, startDate: String, endDate: String, tags: any, active: String): Observable<any[]> {
    let url = '/system/getActiveSession?appUuid=' + appuuid + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&status=' + active;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => {
          return this.modifyActiveSessions(response.json());
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getActiveJobByCriteria(type: String, userName: String, appuuid: Number, startDate: String, endDate: String, tags: any, status: String): Observable<any[]> {
    let url = '/system/getActiveJobByCriteria?type=' + type + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&appuuid=' + appuuid + '&status=' + status;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => {
          return this.modifyActiveSessions(response.json());
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getActiveThread(): Observable<any[]> {
    let url = '/system/getActiveThread';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => {
          return this.modifyThreadSessions(response.json());
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  killSession(sessionId: Number): Observable<any[]> {
    let url = '/system/killSession?sessionId=' + sessionId;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getSessionCountByUser(appuuid, userName, startDate, endDate, tags, statusr): Observable<any[]> {
    let url = 'system/getSessionCountByUser?type=session&userName=' + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getSessionCountByStatus(appuuid, userName, startDate, endDate, tags, statusr): Observable<any[]> {
    let url = 'system/getSessionCountByStatus?type=session&userName=' + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  modifyActiveSessions(response) {
    for (var j = 0; j < response.length; j++) {
      response[j]["appName"] = response[j].appInfo[0].ref.name
      response[j]["status"] = response[j].status[response[j].status.length - 1].stage
    }
    return response;
  }
  modifyThreadSessions(response) {
    for (var j = 0; j < response.length; j++) {
      response[j]["appName"] = response[j].appInfo[0].ref.name
      response[j]["type"] = response[j].execInfo.ref.type
    }
    return response;
  }
}