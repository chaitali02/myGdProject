import { Injectable, Inject } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs';

import { AppConfig } from '../app.config';

@Injectable()
export class LoginService {
  baseUrl: any;
  constructor( @Inject(Http) private http: Http, config: AppConfig) {
    this.baseUrl = config.getBaseUrl();
  }
 
  getValidateUser(userName: string, password: string): Observable<any>{
    let loginUrl = this.baseUrl + '/metadata/validateUser';
    let headers = new Headers({ 'Authorization': 'Basic ' + btoa(userName + ":" + password) });
    return this.http.get(loginUrl, { headers: headers });
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

}
