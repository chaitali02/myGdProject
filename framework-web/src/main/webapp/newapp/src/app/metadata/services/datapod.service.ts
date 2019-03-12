import { Inject, Injectable, Input } from '@angular/core';
import { Http, Headers, Response } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { Datapod } from '../domain/domain.datapod';
import { DatapodIO } from '../domainIO/domain.datapodIO';
import { DatapodAttributeIO } from '../domainIO/domain.datapodAttributeIO';
//import { Datapod } from '../../data-preparation/datapod/datapod';
//import { Datapod } from 'src/app/data-preparation/datapod/datapod';


@Injectable()
export class DatapodService {
    sessionId: any;

    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { 
        let userDetail = JSON.parse(localStorage.getItem('userDetail'));
        this.sessionId = userDetail['sessionId'];
    }

    private headers = new Headers({ 'sessionId': this.sessionId, 'Content-Type': 'application/json' });
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }

    getOneByUuidAndVersion(uuid:any, version:any, type:any): Observable<any> {
        return this._commonService.getOneByUuidAndVersion(uuid, version, type)
            .pipe(
                map(response => {
                    let datapodjson = new DatapodIO();
                    datapodjson.datapoddata = response;
                    var attributearray = [];
                    for (var i = 0; i < response.attributes.length; i++) {
                        var attribute = new DatapodAttributeIO;
                        attribute.attributeId = response.attributes[i].attributeId;
                        attribute.name = response.attributes[i].name;
                        attribute.isAttributeEnable = true;
                        attribute.selected = false;
                        attribute.dispName = response.attributes[i].dispName;
                        attribute.type = response.attributes[i].type.toLowerCase();
                        attribute.desc = response.attributes[i].desc;
                        if (response.attributes[i].key != "" && response.attributes[i].key != null) {
                            attribute.key = true;
                        }
                        else {
                            attribute.key = false;
                        }
                        if (response.attributes[i].partition == "Y") {
                            attribute.partition = true;
                        } else {
                            attribute.partition = false;
                        }

                        if (response.attributes[i].active == "Y") {
                            attribute.active = true;
                        } else {
                            attribute.active = false;
                        }
                        attributearray[i] = attribute;
                    }
                    //console.log(JSON.stringify(attributearray))
                    datapodjson.attributes = attributearray
                    return <any>datapodjson;
                }), catchError(error => this.handleError<string>(error, "Network Error!")));
    }
    
    getDatasourceByType(type): Observable<any[]> {
        let url = 'metadata/getDatasourceByType?action=view&type=' + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getDatasourceByApp(type): Observable<any[]> {
        let url = "metadata/getDatasourceByApp?action=view&type=application&uuid=" + type
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getLatestDataSourceByUuid(id, type): Observable<any[]> {
        let url = "common/getLatestByUuid?action=view&uuid=" + id + "&type=" + type
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }
    
    getDatastoreByDatapod(data, type): Observable<any[]> {
        let url = "metadata/getDatastoreByDatapod?action=view&uuid=" + data.uuid + "&version=" + data.version + "&type=" + type
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getResult(uuid, version): Observable<any[]> {
        let url = "datastore/getResult?action=view&uuid=" + uuid + "&version=" + version + "&limit=100"
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    compareMetadata(uuid, version, type): Observable<any[]> {
        let url = "datapod/compareMetadata?action=view&uuid=" + uuid + "&version=" + version + "&type" + type
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    synchronizeMetadata(uuid, version, type): Observable<any[]> {
        let url = "datapod/synchronizeMetadata?action=view&uuid=" + uuid + "&version=" + version + "&type" + type
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getAttrHistogram(uuid, version, type, attributeId): Observable<any[]> {
        let url = "datapod/getAttrHistogram?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type + "&attributeId=" + attributeId
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }

    getDatapodSample(api_url): Observable<any> {
        return this.http
          .get(api_url, { headers: this.headers })
          .pipe(
            map(response => { return <any>response.json(); }),
            catchError(error => this.handleError<string>(error, "Network Error!")));
      }
    
}