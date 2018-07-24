import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import { Observable } from "rxjs/Observable";
import { AppConfig } from '../app.config';
import { CommonList } from './common-list';
import { SharedService } from '../shared/shared.service';
@Injectable()
export class CommonListService {
  baseUrl: any;
  sessionId: string;
  url: any;
  constructor( @Inject(Http) private http: Http, config: AppConfig, private _sharedService: SharedService) {
    this.baseUrl = config.getBaseUrl();
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.sessionId = userDetail['sessionId'];
  }
  private headers = new Headers({ 'sessionId': this.sessionId });

  getBaseEntityByCriteria(type, name, startDate, tags, userName, endDate, active, status): Observable<CommonList[]> {
    if (type.indexOf("exec") != -1) {
      this.url = this.baseUrl + 'metadata/getBaseEntityStatusByCriteria?action=view&type=' + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&status=" + status;
    }
    else {
      this.url = this.baseUrl + 'metadata/getBaseEntityByCriteria?action=view&type=' + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&active=" + active;
    }
    return this.http
      .get(this.url, { headers: this.headers })
      .map((response: Response) => {
        return <CommonList[]>response.json();
      })
    // .catch(this.handleError);
  }
  getAllLatest(type): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }
  delete(id, type): Observable<any> {
    let body = JSON.stringify({});
    this.url = this.baseUrl + 'common/delete?action=delete&id=' + id + '&type=' + type;
    return this.http
      .put(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  restore(id, type): Observable<any> {
    let body = JSON.stringify({});
    this.url = this.baseUrl + 'common/restore?action=restore&id=' + id + '&type=' + type;
    return this.http
      .put(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  publish(id, type): Observable<any> {
    let body = JSON.stringify({});
    this.url = this.baseUrl + 'common/publish?action=publish&id=' + id + '&type=' + type;
    return this.http
      .put(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  unpublish(id, type): Observable<any> {
    let body = JSON.stringify({});
    this.url = this.baseUrl + 'common/unPublish?action=unpublish&id=' + id + '&type=' + type;
    return this.http
      .put(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  clone(uuid, version, type): Observable<any> {
    let body = JSON.stringify({});
    this.url = this.baseUrl + 'common/saveAs?action=clone&uuid=' + uuid + '&version=' + version + '&type=' + type;
    return this.http
      .post(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  export(uuid, type): Observable<any> {
    this.url = this.baseUrl + 'common/getLatestByUuid?action=export&uuid=' + uuid + '&type=' + type;
    return this.http
      .get(this.url, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  getParamSetByType(executeId, executeVersion, type, action): Observable<any> {
    if (type == "rule") {
      this.url = this.baseUrl + 'metadata/getParamSetByRule?action=' + action + '&ruleUuid=' + executeId + '&type=' + type;
    }
    else {
      this.url = this.baseUrl + 'metadata/getParamSetByTrain?action=' + action + '&trainUuid=' + executeId + '&trainVersion=' + executeVersion + '&type=' + type;
    }
    return this.http
      .get(this.url, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  execute(uuid, version, type, action): Observable<any> {
    if (type == "rule") {
      this.url = this.baseUrl + 'rule/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "rulegroup") {
      this.url = this.baseUrl + 'rule/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profile") {
      this.url = this.baseUrl + 'profile/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profilegroup") {
      this.url = this.baseUrl + 'profile/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dq") {
      this.url = this.baseUrl + 'dataqual/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dqgroup") {
      this.url = this.baseUrl + 'dataqual/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dag") {
      this.url = this.baseUrl + 'dag/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "train") {
      this.url = this.baseUrl + 'model/train/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "predict") {
      this.url = this.baseUrl + 'model/predict/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "map") {
      this.url = this.baseUrl + 'map/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    let body = JSON.stringify({});
    this.headers = null;
    this.headers = new Headers({ 'sessionId': this.sessionId });
    this.headers.append('Accept', '*/*')
    this.headers.append('content-Type', "application/json");
    return this.http
      .post(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  restart(uuid, version, type, action): Observable<any> {
    if (type == "rule") {
      this.url = this.baseUrl + 'rule/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "rulegroup") {
      this.url = this.baseUrl + 'rule/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profile") {
      this.url = this.baseUrl + 'profile/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profilegroup") {
      this.url = this.baseUrl + 'profile/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dq") {
      this.url = this.baseUrl + 'dataqual/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dqgroup") {
      this.url = this.baseUrl + 'dataqual/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dag") {
      this.url = this.baseUrl + 'dag/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "model") {
      this.url = this.baseUrl + 'model/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "map") {
      this.url = this.baseUrl + 'map/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "trainexec") {
      this.url = this.baseUrl + 'model/train/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "predictexec") {
      this.url = this.baseUrl + 'model/predict/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "simulateexec") {
      this.url = this.baseUrl + 'model/simulate/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    let body = JSON.stringify({});
    this.headers = null;
    this.headers = new Headers({ 'sessionId': this.sessionId });
    this.headers.append('Accept', '*/*')
    this.headers.append('content-Type', "application/json");
    return this.http
      .post(this.url, body, { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }
  kill(uuid, version, type, status): Observable<any> {
    if (type == "rule") {
      this.url = this.baseUrl + 'rule/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "rulegroup") {
      this.url = this.baseUrl + 'rule/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profile") {
      this.url = this.baseUrl + 'profile/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profilegroup") {
      this.url = this.baseUrl + 'profile/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dq") {
      this.url = this.baseUrl + 'dataqual/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dqgroup") {
      this.url = this.baseUrl + 'dataqual/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dag") {
      this.url = this.baseUrl + 'dag/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "model") {
      this.url = this.baseUrl + 'model/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "map") {
      this.url = this.baseUrl + 'map/setStatus?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "trainexec") {
      this.url = this.baseUrl + 'model/train/kill?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "predictexec") {
      this.url = this.baseUrl + 'model/predict/kill?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "simulateexec") {
      this.url = this.baseUrl + 'model/simulate/kill?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    let body = {};
    this.headers = null;
    this.headers = new Headers({ 'sessionId': this.sessionId });
    this.headers.append('Accept', '*/*')
    this.headers.append('content-Type', "application/json");
    return this.http
      .put(this.url, JSON.stringify(body), { headers: this.headers })
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }

  executeWithParams(uuid,version,type,action,execParams){
    this.headers=null;
    this.headers=new Headers({'sessionId': this.sessionId});
    if(type=="rule"){
      this.url = this.baseUrl+ 'rule/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
    } 
    else if(type=="simulate"){
      this.url = this.baseUrl+ 'model/simulate/execute?action='+ action +'&uuid=' + uuid + '&version=' + version;
    } 
    else if(type=="train"){
          this.url = this.baseUrl + 'model/train/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
        }
    else{
      this.url = this.baseUrl+ 'model/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    let body
    if(type== "simulate"){
      body= execParams
    }
    else{
      body= JSON.stringify({execParams});
    }
    this.headers.append('Accept','*/*')
    this.headers.append('content-Type',"application/json");
    return this.http                 
    .post( this.url,body, {headers: this.headers})
    .map((response: Response) => {
    return <any>response.json();
  })
}
  // executeWithParams(uuid, version, type, action, execParams) {
  //   this.headers = null;
  //   this.headers = new Headers({ 'sessionId': this.sessionId });
  //   if (type == "rule") {
  //     this.url = this.baseUrl + 'rule/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
  //   }
  //   else {
  //     this.url = this.baseUrl + 'model/train/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
  //   }
  //   let body = JSON.stringify({ execParams });
  //   this.headers.append('Accept', '*/*')
  //   this.headers.append('content-Type', "application/json");
  //   return this.http
  //     .post(this.url, body, { headers: this.headers })
  //     .map((response: Response) => {
  //       return <any>response.json();
  //     })
  // }
  uploadFile(fd, filename, type) {
    var url = this.baseUrl + 'metadata/file?action=edit&fileName=' + filename + '&type=' + type;
    let body = fd;
    return this.http
      .post(url, body, { headers: this.headers })
  }
  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

  downloadFile(id,version){
    var url=this.baseUrl+'/model/predict/download?action=view&predictExecUUID='+id+'&predictExecVersion='+version+'&mode=BATCH';
    // return this.http
    //   .get(url, { headers: this.headers })
    //   .map((response: Response) => {
    //     console.log("response is"+response);
    //     return <any>response.json();
    //   });
    return this.http
    .get(url, { headers: this.headers })
    .map((response: Response) => {
      return <any>response;
    })

    }
    
    // private newFunction(): any {
    //     return this.headers();
    // }

  getParamListByType(executeId,executeVersion,type,action): Observable<any> {
    if(type=="simulate"){
      this.url = this.baseUrl+ 'metadata/getParamListBySimulate?action='+action + '&uuid=' + executeId + '&type=' + type;
    }
    
    return this.http         
    .get( this.url, {headers: this.headers})
    .map((response: Response) => {
      return <any>response.json();
  })
  // .catch(this.handleError);
  }

}