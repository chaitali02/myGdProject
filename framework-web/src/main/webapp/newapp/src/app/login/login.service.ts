import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import { AppConfig } from '../app.config';
import { Login } from './login';

@Injectable()
export class LoginService {
  baseUrl: any;
  constructor(@Inject(Http) private http: Http, config: AppConfig, ) {
    this.baseUrl = config.getBaseUrl();
  }
  getValidateUser(userName: string, password: string): Promise<Login> {
    let loginUrl = this.baseUrl + '/metadata/validateUser';
    let headers = new Headers({ 'Authorization': 'Basic ' + btoa(userName + ":" + password) });

    return this.http.get(loginUrl, { headers: headers })
      .toPromise()
      .then(this.handleSuccess)
      .catch(this.handleError);
  }
  private handleSuccess(res: Response) {
    let body = res.json();
    return body;
  }
  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
