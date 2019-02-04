import { debug } from 'util';
import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response, Headers, ResponseContentType } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import 'rxjs/add/operator/map';
// impo  [x: string]: any;
import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class FileManagerService {
    headers: Headers;
    constructor( @Inject(Http) private http: Http, private _sharedService: SharedService) { }
    getDatasourceByType(type: String): Observable<any> {
        let url = "/metadata/getDatasourceByType?action=view&type=" + type;
        return this._sharedService.getCall(url)
            .map((response: Response) => {
                return <any[]>response.json();
            })
            .catch(this.handleError);
    }
    private handleError(error: Response) {
        return Observable.throw(error.statusText);
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


