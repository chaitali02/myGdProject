import { Injectable } from "@angular/core";
import { Headers } from "@angular/http";
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from "../../shared/shared.service";

@Injectable()
export class BatchService {
	sessionId: string;
	baseUrl: string;
	headers: Headers;

	constructor(private _sharedService: SharedService) { }

	private handleError<T>(error: any, result?: T) {
		return throwError(error);
	}

	setStatus(uuid: any, version: any, type: any, status: any): Observable<any[]> {
		let url = '/batch/setStatus?uuid=' + uuid + '&version=' + version + '&type=' + type + '&status=' + status;
		return this._sharedService.getCall(url)
			.pipe(
				map(response => { return <any[]>response.json(); }),
				catchError(error => this.handleError<string>(error, "Network Error!")));
	}
}