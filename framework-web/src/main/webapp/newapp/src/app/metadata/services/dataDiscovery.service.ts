
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class DataDiscoveryService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getDatapodStats(type: String, inputFlag: String): Observable<any[]> {
    let url = '/datapod/getDatapodStats?action=view';
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}
