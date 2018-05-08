import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import {CommonService} from './common.service';
import {AttributeHolder} from './../../metadata/domain/domain.attributeHolder'
import { BindingFlags } from '@angular/core/src/view';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class DistributionService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
  getAllModelByType(uuid:Number,type:String): Observable<any[]> {
    let url ='/model/getAllModelByType?action=view&customFlag='+uuid+ '&type=model';
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }



  getParamSetByAlgorithm(uuid:Number,version:String): Observable<any[]> {
    let url ='/metadata/getParamSetByAlgorithm?action=view&type=algorithm&algorithmUuid='+uuid+'&algorithmVersion='+version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }
  
  execute(uuid:Number,version:String,data:any): Observable<any> {
    let url ='/model/train/execute?action=execute&uuid='+uuid+'&version='+version;
    return this._sharedService.postCall(url,data)
    .map((response: Response) => {
      return <any>response.json();
  })
   .catch(this.handleError);
  }
  
  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }


}