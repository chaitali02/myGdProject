import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";
import { SharedService } from '../shared/shared.service';

@Injectable()
export class LayoutService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAppRole(): Observable<any[]> {
    let url = '/security/getAppRole?userName=ypalrecha';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }


  setAppRole(appuuid: string, roleuuid: string): Observable<any> {
    let url = '/security/setAppRole?appUUID=' + appuuid + '&roleUUID=' + roleuuid;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
