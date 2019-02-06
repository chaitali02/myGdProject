import { Inject, Injectable, Input } from '@angular/core';
import { Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()
export class DataScienceResultService {
  constructor(private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getModelByTrainExec(uuid: any, version: any): Observable<any[]> {
    let url = "model/getModelByTrainExec?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getTrainResults(uuid: any, version: any): Observable<any[]> {
    let url = "model/train/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getPredictResults(uuid: any, version: any): Observable<any[]> {
    let url = "model/predict/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getSimulateResults(uuid: any, version: any): Observable<any[]> {
    let url = "model/simulate/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}