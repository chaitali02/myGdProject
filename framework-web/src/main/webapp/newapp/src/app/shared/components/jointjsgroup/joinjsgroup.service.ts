

import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../../shared/shared.service';


@Injectable()

export class jointjsGroupService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
    private handleError(error: Response) {
        return Observable.throw(error.statusText);
    }
    
    getExecByGroupExec(url): Observable<any[]> {
        return this._sharedService.getCall(url)
        .map((response: Response) => {
        return <any[]>response.json();
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

    getResults(type,uuid,version): Observable<any> {
        let url =type+"/getResults?action=view&uuid="+uuid+"&version="+version+"&requestId=";
        return this._sharedService.getCall(url)
        .map((response: Response) => {
        return <any>response.json();
        })
        .catch(this.handleError);
    }

    getGroupExecStatus(uuid,version,type):Observable<any> {
        let url ='metadata/getGroupExecStatus?action=view&type='+type+'&uuid='+uuid+'&version='+version    
        return this._sharedService.getCall(url)
        .map((response: Response) => {
        return <any>response.json();
        })
        .catch(this.handleError);
    }

    setStatus(api,uuid,version,type,status):Observable<any> {
        let url =api+'/setStatus?uuid='+uuid+'&version='+version+'&type='+type+'&status='+status
        return this._sharedService.putCall(url,null)
        .map((response: Response) => {
        return <any>response.json();
        })
        .catch(this.handleError);
    }

    
  

}