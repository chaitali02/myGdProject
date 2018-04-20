import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from "rxjs/Observable";
import {AppConfig} from '../app.config';

@Injectable()
export class SharedService {
  sessionId :string;
  baseUrl:string;
  headers:Headers;
  constructor(@Inject(Http) private http: Http,config: AppConfig) {
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
    this.baseUrl = config.getBaseUrl();
   }

  getCall(apiUrl:string):Observable<any> {
    this.headers = new Headers({'sessionId':this.sessionId});
    // this.headers.append("withCredentials","true");
    let url=this.baseUrl+apiUrl;
    return this.http.get(url,{headers: this.headers});
  }
  
  postCall(apiUrl:string,data:any):Observable<any> {
    this.headers = new Headers({'sessionId':this.sessionId});
    this.headers.append('Accept','*/*')
    this.headers.append('content-Type',"application/json");
    let url=this.baseUrl+apiUrl;
    return this.http.post(url,JSON.stringify(data),{headers: this.headers});

  }
  putCall(apiUrl:string,data:any):Observable<any> {
    this.headers = new Headers({'sessionId':this.sessionId});
    this.headers.append('Accept','*/*')
    this.headers.append('content-Type',"application/json");
    let url=this.baseUrl+apiUrl;
    return this.http.put(url,JSON.stringify(data),{headers: this.headers});

  }
  getGraphData(url){
    return this.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  }
}
