import { Injectable, Inject, Input } from "@angular/core";
import { Http, Response, Headers, ResponseContentType } from "@angular/http";
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from "../../shared/shared.service";
import { CommonService } from "./common.service";

@Injectable()

export class ReportService {
	sessionId: string;
	baseUrl: string;
	headers: Headers;
	constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
	private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
	getParamByApp(uuid: any, type: any): Observable<any[]> {
		let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
		return this._sharedService.getCall(url)
		.pipe(
			map(response => { return <any[]>response.json(); }),
			catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	reportExecute(uuid: any, version: any, data: any): Observable<any[]> {
		let url = '/report/execute?action=execute&uuid=' + uuid + '&version=' + version + '&type=report';
		return this._sharedService.postCall(url, data)
		.pipe(
			map(response => { return <any[]>response.json(); }),
			catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	getReportSample(uuid: any, version: any): Observable<any[]> {
		let url = '/report/getReportSample?action=view&uuid=' + uuid + '&version=' + version + '&rows=100';
		return this._sharedService.getCall(url)
		.pipe(
			map(response => { return <any[]>response.json(); }),
			catchError(error => this.handleError<string>(error, "Network Error!")));
	}

	download(uuid: string, version: string, rows: number) {
		let baseUrl = "http://localhost:8080";
		let url = baseUrl + '/report/download?action=view&uuid=' + uuid + '&version=' + version + '&rows=' + rows;
		return this.http.get(url, { headers: this.headers, responseType: ResponseContentType.Blob })
	}

	getAttributeValues(uuid: any, attributeId: any, type: any): Observable<any[]> {
		let url;
		if (type == "datapod") {
			url = "datapod/getAttributeValues1?action=view&datapodUUID=" + uuid + "&attributeId=" + attributeId;
		}
		else {
			url = "dataset/getAttributeValues?action=view&uuid=" + uuid + "&attributeId=" + attributeId;
		}

		return this._sharedService.getCall(url)
		.pipe(
			map(response => { return <any[]>response.json(); }),
			catchError(error => this.handleError<string>(error, "Network Error!")));
	}

}