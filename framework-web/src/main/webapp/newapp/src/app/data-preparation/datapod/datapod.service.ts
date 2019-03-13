import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

@Injectable()
export class MetaDataDataPodService {
  sessionId: string;

  constructor(@Inject(Http) private http: Http) {
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }

  private headers = new Headers({ 'sessionId': this.sessionId, 'Content-Type': 'application/json' });
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  getOneByUuidAndVersion(api_url): Observable<any> {
    return this.http
      .get(api_url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDatapodSample(api_url): Observable<any> {
    return this.http
      .get(api_url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllVersionByUuid(api_url): Observable<any> {
    return this.http
      .get(api_url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDatasourceByType(api_url): Observable<any> {
    return this.http
      .get(api_url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllLatest(api_url): Observable<any> {
    return this.http
      .get(api_url, { headers: this.headers })
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  datapodSubmit(api_url, data): Observable<any> {
    return this.http
      .post(api_url, JSON.stringify(data), { headers: this.headers })
      .pipe(
        map(response => { return <any>response.text(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
