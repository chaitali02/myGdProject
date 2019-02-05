
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response, Headers, ResponseContentType } from '@angular/http'
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class FileManagerService {
    headers: Headers;
    constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
    private handleError<T>(error: any, result?: T) {
        return throwError(error);
    }
    getDatasourceByType(type: String): Observable<any> {
        let url = "/metadata/getDatasourceByType?action=view&type=" + type;
        return this._sharedService.getCall(url)
            .pipe(
                map(response => { return <any[]>response.json(); }),
                catchError(error => this.handleError<string>(error, "Network Error!")));
    }


    uploadFile(fd, filename, type, uuid, version, fileType, dataSourceUuid) {
        let baseUrl = "http://localhost:8080";
        var url = baseUrl + '/common/upload?action=edit&fileName=' + filename + '&type=' + type + '&uuid=' + uuid + '&version=' + version + '&fileType=' + fileType + '&dataSourceUuid=' + dataSourceUuid;
        let body = fd;
        return this.http
            .post(url, body, { headers: this.headers })
    }

    download(uuid, type, fileType, fileName) {
        let baseUrl = "http://localhost:8080";
        let url = baseUrl + "/common/download?action=view&uuid=" + uuid + "&type=" + type + "&fileType=" + fileType + "&fileName=" + fileName;
        return this.http.get(url, { headers: this.headers, responseType: ResponseContentType.Blob })
    }
}


