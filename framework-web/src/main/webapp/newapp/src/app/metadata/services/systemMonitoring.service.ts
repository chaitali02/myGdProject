import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class SystemMonitoringService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  getMetaExecList(): Observable<any[]> {
    let url ='/metadata/getMetaExecList?type=session&action=view';
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
  getActiveSession(userName:String,appuuid:Number,startDate:String,endDate:String,tags:any,active:String): Observable<any[]> {
    let url ='/system/getActiveSession?appUuid='+ appuuid + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&status=' + active;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return this.modifyActiveSessions(response.json());
  })
   .catch(this.handleError);
  }
   
  getActiveJobByCriteria(type:String,userName:String,appuuid:Number,startDate:String,endDate:String,tags:any,status:String): Observable<any[]> {
    let url ='/system/getActiveJobByCriteria?type='+ type + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&appuuid=' + appuuid + '&status=' + status;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return this.modifyActiveSessions(response.json());
  })
   .catch(this.handleError);
  }

  getActiveThread(): Observable<any[]> {
    let url ='/system/getActiveThread';
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  killSession(sessionId:Number): Observable<any[]> {
    let url ='/system/killSession?sessionId='+ sessionId;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
  getSessionCountByUser(appuuid,userName,startDate,endDate,tags,statusr): Observable<any[]> {
    let url ='system/getSessionCountByUser?type=session&userName=' + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
  getSessionCountByStatus(appuuid,userName,startDate,endDate,tags,statusr): Observable<any[]> {
    let url ='system/getSessionCountByStatus?type=session&userName=' + userName + "&startDate=" + startDate + "&endDate=" + endDate + "&tags=" + tags + "&appuuid=" + appuuid + "&status=" + status;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
private handleError(error: Response) {
    return Observable.throw(error.statusText);
}
modifyActiveSessions(response){
  for(var j=0;j<response.length;j++){
    response[j]["appName"]=response[j].appInfo[0].ref.name
    response[j]["status"]=response[j].status[response[j].status.length-1].stage
    // for(var i=0;i<response[j].status.length;i++){
    //   response[j]["status"]=response[j].status[i].stage
    // }
   
  }
  return response;
}
}