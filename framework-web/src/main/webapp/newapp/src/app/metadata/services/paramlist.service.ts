
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';

@Injectable()

export class ParamlistService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAllLatestParamListByTemplate(templateFlg: any, type: any, paramListType: any): Observable<any[]> {
    let url = 'common/getAllLatestParamListByTemplate?action=view&templateFlg=' + templateFlg + "&type=" + type + "&paramListType=" + paramListType;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
