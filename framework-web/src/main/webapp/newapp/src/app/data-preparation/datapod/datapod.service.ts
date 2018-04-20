import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from 'rxjs/Observable';

import { Hero } from './hero';
@Injectable()
export class MetaDataDataPodService {
  sessionId: string;

  constructor(@Inject(Http) private http: Http) {
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }

  private headers = new Headers({'sessionId': this.sessionId, 'Content-Type': 'application/json'});


  getOneByUuidAndVersion(api_url): Observable<any> {
    return this.http
      .get(api_url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  private getResult(res: Response){
    let body = res.json();
    return body;
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

  getDatapodSample(api_url): Observable<any> {
    return this.http
      .get(api_url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  getAllVersionByUuid(api_url): Observable<any> {
    return this.http
      .get(api_url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  getDatasourceByType(api_url): Observable<any> {
    return this.http
      .get(api_url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  getAllLatest(api_url): Observable<any> {
    return this.http
      .get(api_url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  datapodSubmit(api_url, data): Observable<any> {
    return this.http
      .post(api_url, JSON.stringify(data), {headers: this.headers})
      .map((response: Response) => {
        return <any>response.text();
      })
    // .catch(this.handleError);
  }

}
