import { Injectable, Inject, Input } from "@angular/core";
import { Http, Response } from "@angular/http";
import { SharedService } from "../../shared/shared.service";
import { Observable } from "rxjs/Observable";
import { CommonService } from "./common.service";

@Injectable()

export class ReportService {
	sessionId: string;
	baseUrl: string;
	headers: Headers;
	constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

	getParamByApp(uuid: any, type: any): Observable<any[]> {
		let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	reportExecute(uuid: any, version: any, data: any): Observable<any[]> {
		let url = '/report/execute?action=execute&uuid=' + uuid + '&version=' + version + '&type=report';
		return this._sharedService.postCall(url, data)
			.map((response: Response) => {
				return <any>response.json();
			})
			.catch(this.handleError);
	}

	getReportSample(uuid: any, version: any): Observable<any[]> {
		let url = '/report/getReportSample?action=view&uuid=' + uuid + '&version=' + version + '&rows=100';
		return this._sharedService.getCall(url)
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
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
			.map((response: Response) => {
				return <any[]>response.json();
			})
			.catch(this.handleError);
	}

	private handleError(error: Response) {
		return Observable.throw(error.statusText);
	}

}