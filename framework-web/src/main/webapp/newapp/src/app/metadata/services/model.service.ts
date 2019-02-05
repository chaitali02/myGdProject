import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response,Headers } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()
export class ModelService{
  headers: Headers;
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getExecuteModel(uuid:Number,version:String): Observable<any[]> {
    let url ='/model/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getExecuteModelWithBody(uuid:Number,version:String,data:any): Observable<any[]> {
    let url ='/model/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.postCall(url,data)
    .pipe(
      map(response => { return <any>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getModelExecByModel(uuid:Number): Observable<any[]> {
    let url ='/model/getModelExecByModel?action=view&modelUUID='+uuid;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamSetByModel(uuid:Number,version:String): Observable<any[]> {
    let url ='/metadata/getParamSetByModel?action=view&modelUuid='+uuid+'&modelVersion='+version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamSetByAlgorithm(uuid:Number,version:String): Observable<any[]> {
    let url ='/metadata/getParamSetByAlgorithm?action=view&type=algorithm&algorithmUuid='+uuid+'&algorithmVersion='+version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamListByFormula(uuid:any,type:String): Observable<any[]> {
    let url ='metadata/getParamListByFormula?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getFormulaByType2(type:any): Observable<any[]> {
    let url ='/metadata/getFormulaByType2?action=view&type='+type+'&formulaType=custom';
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getModelScript(uuid:any,version:any): Observable<any[]> {
    let url ='/model/getModelScript?action=view&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  
  uploadFile(extension, data, fileType) {
    let baseUrl = 'http://localhost:8080'
    var url = baseUrl + '/model/upload?action=edit&extension=' + extension + '&fileType=' + fileType;
    let body = data;
    return this.http
      .post(url, body, { headers: this.headers })
  }

  getModelResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/train/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getPredictResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/predict/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getSimulateResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/simulate/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
 
}