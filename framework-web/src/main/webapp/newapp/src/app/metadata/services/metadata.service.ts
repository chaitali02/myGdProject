import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class MetadataService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getFile(filename: String): Observable<any[]> {
    let url = '/metadata/file?action=edit&fileName=' + filename;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRegisterFile(urlUpload: String): Observable<any[]> {
    let url = '/metadata/registerFile?action=view&csvFileName=' + urlUpload;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  excutionDag(): Observable<any[]> {
    let url = '/dag';
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeMap(uuid: Number, version: String): Observable<any[]> {
    let url = 'map/executeMap?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

 

  getExecListByBatchExec(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = 'metadata/getExecListByBatchExec?action=view&uuid=' + uuid + '&version=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getNumRowsbyExec(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = 'metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + '&execVersion=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAttributesByDatapod(uuid: number, type: string): Observable<any[]> {
    let url = 'metadata/getAttributesByDatapod?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
