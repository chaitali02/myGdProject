import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from "rxjs/Observable";
import {SharedService } from '../shared/shared.service';

@Injectable()
export class LayoutService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getAppRole(): Observable<any[]> {
    let url ='/security/getAppRole?userName=ypalrecha'; 
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }
 setAppRole(appuuid:string,roleuuid:string) : Observable<any>{
    let url ='/security/setAppRole?appUUID='+appuuid+'&roleUUID='+roleuuid;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any>response.json();
  }).catch((error: Response) => {
    return Observable.throw(error.statusText);
});
}

}
 