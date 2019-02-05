import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";


import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';


@Injectable()

export class DashboardService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getVizpodByType(type: String, uuid: Number): Observable<any[]> {
    let url = '/metadata/getVizpodByType?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }


  getVizpodResult(uuid, version, action, data) {
    let url = "vizpod/getVizpodResults/" + uuid + "/" + version + "?action=" + action + "&requestId=''"
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAttributeValues(uuid, attributeId) {
    let url = "datapod/getAttributeValues1?action=view&datapodUUID=" + uuid + "&attributeId=" + attributeId;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .pipe(
        map(response => {
          let dashboadjson = response;
          for (let i = 0; i < response["sectionInfo"].length; i++) {
            let vizpodinfo = {};
            vizpodinfo["label"] = response["sectionInfo"][i]["vizpodInfo"].name;
            vizpodinfo["uuid"] = response["sectionInfo"][i]["vizpodInfo"].uuid;
            vizpodinfo["type"] = response["sectionInfo"][i]["vizpodInfo"].type;
            vizpodinfo["name"] = response["sectionInfo"][i]["vizpodInfo"].name;
            dashboadjson["sectionInfo"][i]["vizpodInfo"] = vizpodinfo
          }
          return <any>dashboadjson;
        }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

}