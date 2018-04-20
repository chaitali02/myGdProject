import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from 'rxjs/Observable';
import {AppConfig} from '../app.config';
import { jobMonitoring } from './jobMonitoring';
@Injectable()
export class jobMonitoringService {
  baseUrl: any;
  sessionId:string;
  constructor(@Inject(Http) private http: Http,config: AppConfig) {
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }
  private headers = new Headers({'sessionId': this.sessionId});
  getExecStats(): Observable<any> {
    let url = this.baseUrl+'/metadata/getExecStats';
    // this.headers.append("withCredentials","true");
    return this.http
      .get(url,{headers: this.headers})
        .map((response: Response) => {
        return <any>response.json();
      })
    //.catch(this.handleError);
  }

}
