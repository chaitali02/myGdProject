import { Response, Http } from '@angular/http';
import { Injectable, Inject, Input } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from './../../shared/shared.service';


@Injectable()
export class MapServices {
    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
    getOneById(type: String, id: Number): Observable<any[]> {
        let url = '/metadata/getOneById?action=view&id=' + id + '&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getAttributesByRule(uuid: Number, type: String): Observable<any[]> {
        let url = '/metadata/getAttributesByRule?action=view&uuid=' + uuid + '&type=' + type;
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