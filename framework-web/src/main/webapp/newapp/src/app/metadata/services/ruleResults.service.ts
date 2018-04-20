import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()

export class RuleResultService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getAll(metavalue:String): Observable<any[]> {
    let url ='/common/getAll?action=view&type=' + metavalue;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getAllLatest(metavalue:String): Observable<any[]> {
    let url ='/common/getAllLatest?action=view&type=' + metavalue;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  executeRule(version:String,uuid:Number): Observable<any[]> {
    let url ='/rule/executeRule?action=execute&ruleUUID=' + uuid + '&ruleVersion=' + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  executeRuleGroup(version:String,uuid:Number): Observable<any[]> {
    let url ='/rule/executeRuleGroup?action=execute&ruleGroupUUID=' + uuid + '&ruleGroupVersion=' + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getRuleExecByRGExec(uuid:Number,version:string): Observable<any[]> {
    let url ='/rule/getRuleExecByRGExec?action=view&ruleGroupExecUUID=' + uuid + '&ruleGroupExecVersion=' + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getRuleGroupExecByRuleGroup(uuid:Number,version:string): Observable<any[]> {
    let url ='/rule/getRuleGroupExecByRuleGroup?action=view&ruleGroupUUID=' + uuid + '&ruleGroupVersion=' + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getBaseEntityStatusByCriteria(uuid:Number,version:string,type:String,name:String,userName:String,startDate:Date,endDate:Date,tags:String,status:String): Observable<any[]> {
    let url ='/metadata/getBaseEntityStatusByCriteria?action=view&type=' + type + '&name=' + name + '&userName=' + userName + '&startDate=' + startDate + '&endDate=' + endDate + '&tags=' + tags + '&status=' + status;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getNumRowsbyExec(uuid:Number,version:string,type:String): Observable<any[]> {
    let url ='/metadata/getNumRowsbyExec?action=view&execUuid='+uuid+'&execVersion='+version+'&type='+type;
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