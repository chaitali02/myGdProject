import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';



@Injectable()

export class SimulationService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAllModelByType(flag: string, type: String): Observable<any[]> {
    let url = "model/getAllModelByType?action=view&customFlag=" + flag + "&type=" + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamListByDistribution(uuid: any) {
    let url = "metadata/getParamListByDistribution?action=view&uuid=" + uuid + "&type=paramlist";
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamByParamList(uuid: any) {
    let url = "metadata/getParamByParamList?action=view&uuid=" + uuid;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}