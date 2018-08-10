import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class DataPipelineService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getDagByDagExec(uuid:Number): Observable<any[]> {
    let url ='/dag/getDagByDagExec?action=view&DagExecUuid='+uuid;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }
  getDagExecByDag(uuid:Number,type:String): Observable<any> {
    let url ='/metadata/getDagExecByDag?action=view&dagUUID='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any>response.json();
    })
    .catch(this.handleError);
  } 
  
  excutionDag(uuid:Number,version:String): Observable<any[]> {
    let url ='/dag/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
      return <any[]>response.json();
    })
    .catch(this.handleError);
  }
  getStatusByDagExec(uuid:Number): Observable<any[]> {
    let url ="dag/getStatusByDagExec?action=view&DagExecUuid="+uuid;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
      return <any[]>response.json();
    })
    .catch(this.handleError);
  }
  setStatus(uuid,version,status,stageId,taskId): Observable<any> {
    let url ='dag/setStatus?uuid='+uuid+'&version='+version+'&status='+status+"&stageId="+stageId+"&taskId="+taskId
    return this._sharedService.putCall(url,null)
      .map((response: Response) => {
      return <any>response.json();
    })
    .catch(this.handleError);
  }

  getMetaIdByExecId(uuid,version,type): Observable<any> {
    let url = "metadata/getMetaIdByExecId?action=view&execUuid="+uuid+"&execVersion="+version+"&type="+type;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
      return <any>response.json();
    })
    .catch(this.handleError);
  }

  getResults(uuid,version,type): Observable<any> {
    let url =type+"/getResults?action=view&uuid="+uuid+"&version="+version+"&requestId=";
    return this._sharedService.getCall(url)
      .map((response: Response) => {
      return <any>response.json();
    })
    .catch(this.handleError);
  }
}