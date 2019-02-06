import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonList } from './common-list';


@Injectable()

export class SettingsService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  get(type: String, action: String): Observable<any[]> {
    let url = 'admin/settings/get?type=' + type + '&action=' + action;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  submit(type: any, action: any): Observable<any[]> {
    let url = 'admin/settings/submit?type=setting&action=edit';
    return this._sharedService.postCall(url, action)
      .pipe(
        map(response => { return <any[]>response.text(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  buildGraph(): Observable<any[]> {
    let url = 'graph/buildGraph';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getBaseEntityByCriteria(type, name, startDate, tags, userName, endDate, active, status, published): Observable<CommonList[]> {
    let url = 'metadata/getBaseEntityByCriteria?action=view&type=' + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&active=" + active + "&published=" + published;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getProcessStatus(): Observable<any[]> {
    let url = 'datascience/getProcessStatus';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  startProcess(): Observable<any[]> {
    let url = 'datascience/startProcess';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  stopProcess(): Observable<any[]> {
    let url = 'datascience/stopProcess';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }


}