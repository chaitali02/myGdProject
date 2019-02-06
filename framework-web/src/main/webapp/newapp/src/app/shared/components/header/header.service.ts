
import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";



import { SharedService } from '../../shared.service';
import { AppConfig } from '../../../app.config';


@Injectable()
export class HeaderService {
  baseUrl: any;
  sessionId: string;


  constructor(@Inject(Http) private http: Http, config: AppConfig, private _sharedService: SharedService) {
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }

  private headers = new Headers({ 'sessionId': this.sessionId });

  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  logoutSession(): Observable<any> {
    let url = '/security/logoutSession';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  uploadImage(fd) {
    var url = this.baseUrl + 'metadata/uploadProfileImage';
    let body = fd;
    return this.http
      .post(url, body, { headers: this.headers })
  }
  
  // private handleError(error: Response) {
  //   return Observable.throw(error.statusText);
  // }

}
