import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../../shared/shared.service';


@Injectable()

export class jointjsGroupService {

    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }

    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }

    getExecByGroupExec(url): Observable<any[]> {
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getNumRows(uuid, version, type): Observable<any> {
        let url = "metadata/getNumRowsbyExec?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=" + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getMetaIdByExecId(uuid, version, type): Observable<any> {
        let url = "metadata/getMetaIdByExecId?action=view&execUuid=" + uuid + "&execVersion=" + version + "&type=" + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getResults(type, uuid, version, mode): Observable<any> {
        let url = type + "/getResults?action=view&uuid=" + uuid + "&version=" + version + "&mode=" + mode + "&requestId=";
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getGroupExecStatus(uuid, version, type): Observable<any> {
        let url = 'metadata/getGroupExecStatus?action=view&type=' + type + '&uuid=' + uuid + '&version=' + version
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    setStatus(api, uuid, version, type, status): Observable<any> {
        let url = api + '/setStatus?uuid=' + uuid + '&version=' + version + '&type=' + type + '&status=' + status
        return this._sharedService.putCall(url, null)
            .pipe(
                map(response => { return <any>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

}