
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';


@Injectable()
export class ApplicationService {


    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

    private handleError<T>(error: any, result?: T) {
        return throwError(error);;
    }

    getDatasourceByType(type): Observable<any[]> {
        let url = 'metadata/getDatasourceByType?action=view&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getFunctionByCriteria(type, category): Observable<any[]> {
        let url = 'metadata/getFunctionByCriteria?action=view&type=' + type + '&category=' + category + '&inputReq=N';
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getLatestByUuid(uuid, type): any {
        let url = 'common/getLatestByUuid?action=view&uuid=' + uuid + '&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }
}