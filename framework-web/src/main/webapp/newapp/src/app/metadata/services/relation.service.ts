import { Response, Http } from '@angular/http';
import { Injectable, Inject, Input } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from './../../shared/shared.service';


@Injectable()
export class mapServices {
    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
    getOneById(id: Number, type: String): Observable<any[]> {
        let url = '/metadata/getOneById?action=view&id=' + id + '&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

}