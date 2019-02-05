import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';



@Injectable()
export class DatasetService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getFormulaByType2(uuid: Number, type: any): Observable<any[]> {
    let url = '/metadata/getFormulaByType2?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getExpressionByType2(uuid: Number, type: Number): Observable<any[]> {
    let url = '/metadata/getExpressionByType2?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDatasetSample(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataset/getDatasetSample?action=view&datasetUUID=' + uuid + '&datasetVersion=' + version + '&row=100';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getExpressionByType(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getExpressionByType?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getAllLatestFunction(type: any, inputFlag: any): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type + '&inputFlag=' + inputFlag;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
