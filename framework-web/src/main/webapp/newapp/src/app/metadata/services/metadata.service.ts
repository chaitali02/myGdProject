import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class MetadataService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }

  getFile(filename: String): Observable<any[]> {
    let url = '/metadata/file?action=edit&fileName=' + filename;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getRegisterFile(urlUpload: String): Observable<any[]> {
    let url = '/metadata/registerFile?action=view&csvFileName=' + urlUpload;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  excutionDag(): Observable<any[]> {
    let url = '/dag';
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  executeMap(uuid: Number, version: String): Observable<any[]> {
    let url = 'map/executeMap?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

  getExecListByBatchExec(uuid:Number, version:String, type:String): Observable<any[]> {
    let url ='metadata/getExecListByBatchExec?action=view&uuid='+uuid+'&version='+version+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
}
