import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { AppConfig } from '../../../app.config';
import { Sidebar } from './sidebar';

@Injectable()
export class SidebarService {
  baseUrl: any;
  sessionId: string;
  constructor(@Inject(Http) private http: Http, config: AppConfig) {
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }
  private headers = new Headers({ 'sessionId': this.sessionId });
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  getAll(): Observable<Sidebar[]> {
    let url = this.baseUrl + '/common/getAll?type=meta';
    return this.http
      .get(url, { headers: this.headers })
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getMetaStats(): Observable<any> {
    let url = this.baseUrl + '/common/getMetaStats';
    return this.http
      .get(url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
