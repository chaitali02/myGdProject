import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response, Headers } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()
export class MigrationAssistService {
  headers: Headers;
  constructor( @Inject(Http) private http: Http, private _sharedService: SharedService) { }

  exportSubmit(type: any, data: any): Observable<any[]> {
    let url = 'admin/export/submit?action=add&type=' + type;
    return this._sharedService.postCall(url, data)
      .map((response: Response) => {
        return <any>response.text();
      })
      .catch(this.handleError);
  }
 
  getAllByMetaList(type:any): Observable<any[]> {
    let url = '/admin/getAllByMetaList?action=view&type=' + type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  uploadFile(fd, filename, type, fileType) {
    let baseUrl = 'http://localhost:8080'
    var url = baseUrl + '/admin/upload?action=edit&fileName=' + filename + '&type=' + type + '&fileType=' + fileType;
    let body = fd;
    return this.http
      .post(url, body, { headers: this.headers })
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }
}