import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import {CommonService} from './common.service';


@Injectable()
export class ApplicationService{

    constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
    private handleError(error: Response) {
    return Observable.throw(error.statusText);
    }

   
    
    getDatasourceByType(type): Observable<any[]> {
        let url ='metadata/getDatasourceByType?action=view&type='+type;
        return this._sharedService.getCall(url)
        .map((response: Response) => {
          return <any[]>response.json();
      })
      .catch(this.handleError);
    }
  


}