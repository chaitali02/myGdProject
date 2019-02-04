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

  getParamListByRule(type, name, startDate, tags, userName, endDate, active, status): Observable<CommonList[]> {
    
      this.url = this.baseUrl + 'metadata/getParamListByRule?action=view&type=' + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&status=" + status;
   
    return this.http
      .get(this.url, { headers: this.headers })
      .map((response: Response) => {
        return <CommonList[]>response.json();
      })
    // .catch(this.handleError);
  }

  getParamListByModel(type, name, startDate, tags, userName, endDate, active, status): Observable<CommonList[]> {
    
      this.url = this.baseUrl + 'metadata/getParamListByModel?action=view&type=' + type + "&name=" + name + "&userName=" + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&status=" + status;
   
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
      this.url = this.baseUrl + 'dag/execute?action=' + action + '&uuid=' + uuid + '&version=' + version;
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
    if (type == "recon") {
      this.url = this.baseUrl + 'recon/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "recongroup") {
      this.url = this.baseUrl + 'recon/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "batch") {
      this.url = this.baseUrl + 'batch/execute?action=' + action + '&uuid=' + uuid + '&version=' + version;
    }
    if (type == "ingest") {
      this.url = this.baseUrl + 'ingest/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
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
    if (type == "batchexec") {
      this.url = this.baseUrl + 'batch/restart?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
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
    if (type == "batchexec") {
      this.url = this.baseUrl + 'batch/kill?status=' + status + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
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

//   executeWithParams(uuid,version,type,action,execParams){
//     this.headers=null;
//     this.headers=new Headers({'sessionId': this.sessionId});
//     if(type=="rule"){
//       this.url = this.baseUrl+ 'rule/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
//     } 
//     else if(type=="simulate"){
//       this.url ='model/simulate/execute?uuid=' + uuid + '&version=' + version +'&action='+ action ;
//     } 
//     else if(type=="train"){
//           this.url = this.baseUrl + 'model/train/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
//         }
//     else{
//       this.url = this.baseUrl+ 'model/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
//     }
//     let data = execParams;
//    // this.headers.append('Accept','*/*')
//     //this.headers.append('content-Type',"application/json");
//     console.log(this.url);
//     return this._sharedService.postCall(this.url,data)
//         .map((response: Response) => {
//           console.log(response);
//           return <any[]>response["_body"];
//     // return this.http                 
//     // .post( this.url,body, {headers: this.headers})
//     // .map((response: Response) => {
//     // return <any>response.json();
//   })
// }
executeWithParams(type, uuid, version, data): Observable<any[]>{ 
  let url
  if(type=='train'){
    url = "/model/train/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
  }else if(type=='simulate'){
    url ='model/simulate/execute?uuid=' + uuid + '&version=' + version +'&action=view';
  }else{
    url = 'model/execute?uuid=' + uuid + '&version=' + version + '&type=' + type+'action=execute';
  }
    let data1 = data;
    return this._sharedService.postCall(url,data1)
    .map((response: Response) => {
      console.log(response);
      return <any>response["_body"];
  })
   .catch(this.handleError);
  
}

  uploadFile(fd, filename, type,uuid,version,fileType,dataSourceUuid) {

    // common/upload?action=edit&fileName=dim_state.csv&type=null&uuid=null&version=null&fileType=csv
    // &dataSourceUuid=d7c11fd7-ec1a-40c7-ba25-7da1e8b73020

    // var url = this.baseUrl + 'metadata/file?action=edit&fileName=' + filename + '&type=' + type;
    //let baseUrl = "http://localhost:8080"
    var url = this.baseUrl+'/common/upload?action=edit&fileName=' + filename + '&type=' + type +'&uuid=' + uuid + '&version='+version+ '&fileType='+fileType+'&dataSourceUuid='+dataSourceUuid;
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

  getParamListByTrain(executeId,executeVersion,type): Observable<any> {
  
      this.url = this.baseUrl+ 'metadata/getParamListByTrain?action=view&' + '&uuid=' + executeId +'&version='+ executeVersion + '&type=' + type;
 
    return this.http         
    .get( this.url, {headers: this.headers})
    .map((response: Response) => {
      return <any>response.json();
  })
  }

  getParamSetByTrain(executeId,executeVersion,type): Observable<any> {
    
        this.url = this.baseUrl+ 'metadata/getParamSetByTrain?action=view&' + '&trainUuid=' + executeId +'&trainVersion='+ executeVersion + '&type=' + type;
   
      return this.http         
      .get( this.url, {headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
    })
    }
    getParamByParamList(uuid: any, type:any) : Observable<any>{
      let url = "metadata/getParamByParamList?action=view&uuid=" + uuid + "&type="+type ;
      return this._sharedService.getCall(url)
        .map((response: Response) => {
          return <any[]>response.json();
        })
        .catch(this.handleError);
    }
}