import { Inject, Injectable, Input } from "@angular/core";
import { Http, Response } from "@angular/http";
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from "../../shared/shared.service";


@Injectable()
export class RegisterSourceService {
    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
    getRegister(uuid: Number, version: any, data: any, type: String): Observable<any[]> {
        let url = '/metadata/register?action=edit&uuid=' + uuid + '&version=' + version + '&type=' + type;
        return this._sharedService.postCall(url, data)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }
}