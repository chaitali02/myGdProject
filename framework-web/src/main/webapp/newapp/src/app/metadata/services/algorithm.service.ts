
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";


import { SharedService } from '../../shared/shared.service';

@Injectable()

export class AlgorithmService {
  sessionId: string;
  baseUrl: string;
  headers: Headers;
  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }

  private handleError<T>(error: any, result?: T) {
    return throwError(error);;
  }

  getExecuteModel(uuid: Number, version: String): Observable<any[]> {
    let url = '/model/train?action=execute&modelUUID=' + uuid + '&modelVersion=' + version;
    this.headers = new Headers({ 'sessionId': this.sessionId });
    this.headers.append('Accept', '*/*')
    this.headers.append('content-Type', "application/json");
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  submit(data: any, type: any, upd_tag: any) {
    let url = 'common/submit?action=edit&type=' + type + "&upd_tag=" + upd_tag;
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any>response.text() }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}