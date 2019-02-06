import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class TrainingService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAllModelByType(flag: string, type: String): Observable<any[]> {
    let url = "model/getAllModelByType?action=view&customFlag=" + flag + "&type=" + type + "&modelType=algorithm";
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamSetByAlgorithm(uuid: Number, version: String, isHyperParam: any) {
    let url = "metadata/getParamSetByAlgorithm?action=view&algorithmUuid=" + uuid + "&algorithmVersion=null&isHyperParam=" + isHyperParam;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamListByAlgorithm(uuid: any, version: any, type: any, isHyperParam: any) {
    let url = "metadata/getParamListByAlgorithm?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type + "&isHyperParam=" + isHyperParam;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamByParamList(uuid: any, type: any): Observable<any> {
    let url = "metadata/getParamByParamList?action=view&uuid=" + uuid + "&type=" + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  executeWithParams(type, uuid, version, data) {
    let url
    if (type == 'train') {
      url = "model/train/execute?uuid=" + uuid + "&version=" + version + '&action=view';
      let body = null
      return this._sharedService.postCall(url, data)
        .pipe(
          map(response => { return <any[]>response.json(); }),
          catchError(error => this.handleError<string>(error, "Network Error!")));
    }
  }
}