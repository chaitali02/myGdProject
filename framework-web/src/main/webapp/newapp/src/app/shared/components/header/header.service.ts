import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from "rxjs/Observable";
import {SharedService } from '../../shared.service';
import {AppConfig} from '../../../app.config';

@Injectable()
export class HeaderService {
  baseUrl: any;
  sessionId:string;
  
  
  constructor(@Inject(Http) private http: Http,config: AppConfig,private _sharedService: SharedService) { 
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }
  private headers = new Headers({'sessionId': this.sessionId});
  logoutSession(): Observable<any> {
    let url ='/security/logoutSession'; 
    return this._sharedService.getCall(url)
    .map((response: Response) => {
        console.log(response)
      return <any>response.json();
  })
   .catch(this.handleError);
  }
  uploadImage(fd){
    var url =this.baseUrl+'metadata/uploadProfileImage';
    let body= fd;
    return this.http                 
    .post( url,body, {headers: this.headers})
  }
  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }
 
}
