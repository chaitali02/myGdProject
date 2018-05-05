import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()
export class TrainingService{
    
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