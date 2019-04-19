import { Response, Http } from '@angular/http';
import { Injectable, Inject, Input } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from './../../shared/shared.service';

@Injectable()
export class FormulaService {

    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
    getExpressionByType(uuid: Number, type: String): Observable<any[]> {
        let url = '/metadata/getExpressionByType?action=view&uuid=' + uuid + '&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getFunctionByFunctionInfo(functioninfo: String): Observable<any[]> {
        let url = '/metadata/getFunctionByFunctionInfo?type=function&action=view&functionInfo=' + functioninfo;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getParamByApp(uuid: any, type: String): Observable<any[]> {
        let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }


}