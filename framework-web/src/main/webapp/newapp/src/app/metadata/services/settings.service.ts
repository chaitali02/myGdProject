import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class SettingsService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  get(type:String,action:String): Observable<any[]> {
    let url ='admin/settings/get?type='+type+'&action='+action;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  submit(type:any,action:any): Observable<any[]> {
    //let url ='/common/submit?action=edit&type='+type;
    let url ='admin/settings/submit?type=setting&action=edit';
    return this._sharedService.postCall(url,action)
    .map((response: Response) => {
      return <any>response.text();
  })
   .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }
}