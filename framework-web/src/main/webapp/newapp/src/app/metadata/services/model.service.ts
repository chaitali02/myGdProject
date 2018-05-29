import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response,Headers } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()
export class ModelService{
  headers: Headers;
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getExecuteModel(uuid:Number,version:String): Observable<any[]> {
    let url ='/model/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getExecuteModelWithBody(uuid:Number,version:String,data:any): Observable<any[]> {
    let url ='/model/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.postCall(url,data)
    .map((response: Response) => {
      return <any>response.json();
  })
   .catch(this.handleError);
  }

  getModelExecByModel(uuid:Number): Observable<any[]> {
    let url ='/model/getModelExecByModel?action=view&modelUUID='+uuid;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getParamSetByModel(uuid:Number,version:String): Observable<any[]> {
    let url ='/metadata/getParamSetByModel?action=view&modelUuid='+uuid+'&modelVersion='+version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getParamSetByAlgorithm(uuid:Number,version:String): Observable<any[]> {
    let url ='/metadata/getParamSetByAlgorithm?action=view&type=algorithm&algorithmUuid='+uuid+'&algorithmVersion='+version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getParamListByFormula(uuid:any,type:String): Observable<any[]> {
    let url ='metadata/getParamListByFormula?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getFormulaByType2(type:any): Observable<any[]> {
    let url ='/metadata/getFormulaByType2?action=view&type='+type+'&formulaType=custom';
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }

  getModelScript(uuid:any,version:any): Observable<any[]> {
    let url ='/model/getModelScript?action=view&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      let body:string = response['_body'];
      return body;
  })
   .catch(this.handleError);
  }
  
  uploadFile(extension, data, fileType) {
    let baseUrl = 'http://localhost:8080'
    var url = baseUrl + '/common/upload?action=edit&extension=' + extension + '&fileType=' + fileType;
    let body = data;
    return this.http
      .post(url, body, { headers: this.headers })
  }

  getModelResults(uuid:any,version:any): Observable<any[]> {
    let url ='/model/getResults?action=view&uuid='+uuid+'&version='+version;
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