import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()

export class RuleGroupService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getexecuteGroup(uuid:Number,version:String): Observable<any[]> {
    let url ='/rule/executeGroup?action=execute&uuid='+uuid+'&version='+version;
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