import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class DataPipelineService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
		return throwError(error);
	}
  getDagByDagExec(uuid:Number): Observable<any[]> {
    let url ='/dag/getDagByDagExec?action=view&DagExecUuid='+uuid;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

 
  getDagExecByDag(uuid:Number,type:String): Observable<any> {
    let url ='/metadata/getDagExecByDag?action=view&dagUUID='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  } 
  
  excutionDag(uuid:Number,version:String): Observable<any[]> {
    let url ='/dag/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getStatusByDagExec(uuid:Number): Observable<any[]> {
    let url ="dag/getStatusByDagExec?action=view&DagExecUuid="+uuid;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  setStatus(uuid,version,status,stageId,taskId): Observable<any> {
    let url ='dag/setStatus?uuid='+uuid+'&version='+version+'&status='+status+"&stageId="+stageId+"&taskId="+taskId
    return this._sharedService.putCall(url,null)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getMetaIdByExecId(uuid,version,type): Observable<any> {
    let url = "metadata/getMetaIdByExecId?action=view&execUuid="+uuid+"&execVersion="+version+"&type="+type;
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getResults(uuid,version,type): Observable<any> {
    let url =type+"/getResults?action=view&uuid="+uuid+"&version="+version+"&requestId=";
    return this._sharedService.getCall(url)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}