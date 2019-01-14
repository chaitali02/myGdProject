import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class ProfileService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }

  getProfileExecByDatapod(datapodUUID: any, type: any, startDate: any, endDate: any): Observable<any[]> {
    let url = 'profile/getProfileExecByDatapod?action=view&datapodUUID=' + datapodUUID + "&type=" + type + "&startDate=" + startDate + "&endDate=" + endDate;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getResults(uuid: number, version: string, mode: string): Observable<any[]> {
    let url = 'profile/getResults?action=view&uuid=' + uuid + '&version=' + version + '&mode=' + mode;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getProfileResults(type: string, uuid: number, version: string, attributeId: number, numDays: number, profileAttrType: string): Observable<any[]> {
    let url = 'profile/getProfileResults?action=view&type=' + type + '&datapodUuid=' + uuid + '&datapodVersion=' + version + '&attributeId=' + attributeId + '&numDays=' + numDays + '&profileAttrType=' + profileAttrType;
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
