import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class ProfileService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getProfileExecByDatapod(datapodUUID: any, type: any, startDate: any, endDate: any): Observable<any[]> {
    let url = 'profile/getProfileExecByDatapod?action=view&datapodUUID=' + datapodUUID + "&type=" + type + "&startDate=" + startDate + "&endDate=" + endDate;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getResults(uuid: number, version: string, mode: string): Observable<any[]> {
    let url = 'profile/getResults?action=view&uuid=' + uuid + '&version=' + version + '&mode=' + mode;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getProfileResults(type: string, uuid: number, version: string, attributeId: number, numDays: number, profileAttrType: string): Observable<any[]> {
    let url = 'profile/getProfileResults?action=view&type=' + type + '&datapodUuid=' + uuid + '&datapodVersion=' + version + '&attributeId=' + attributeId + '&numDays=' + numDays + '&profileAttrType=' + profileAttrType;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }


}
