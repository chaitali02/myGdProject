import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { AppConfig } from '../app.config';


@Injectable()
export class metadataNavigatorService {
  baseUrl: any;
  sessionId: string;
  constructor(@Inject(Http) private http: Http, config: AppConfig) {
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  private headers = new Headers({ 'sessionId': this.sessionId });
  getMetaStats(): Observable<any> {
    let url = this.baseUrl + '/common/getMetaStats';
    return this.http
      .get(url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
