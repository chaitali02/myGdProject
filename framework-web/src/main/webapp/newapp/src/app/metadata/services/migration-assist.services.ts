import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response, Headers } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
;

@Injectable()
export class MigrationAssistService {
  headers: Headers;
  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  exportSubmit(type: any, data: any): Observable<any[]> {
    let url = 'admin/export/submit?action=add&type=' + type;
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any>response.text(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllByMetaList(type: any): Observable<any[]> {
    let url = '/admin/getAllByMetaList?action=view&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  uploadFile(fd, filename, type, fileType) {
    let baseUrl = 'http://localhost:8080'
    var url = baseUrl + '/admin/upload?action=edit&fileName=' + filename + '&type=' + type + '&fileType=' + fileType;
    let body = fd;
    return this.http
      .post(url, body, { headers: this.headers })
  }

  validateDependancy(filename: any, data: any): Observable<any[]> {
    let url = 'admin/import/validate?type=import&action=edit&fileName=' + filename;
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any[]>response.text(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  importSubmit(filename: any, type: any, data: any): Observable<any[]> {
    let url = 'admin/import/submit?action=add' + '&type=' + type + '&fileName=' + filename;
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any[]>response.text(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}