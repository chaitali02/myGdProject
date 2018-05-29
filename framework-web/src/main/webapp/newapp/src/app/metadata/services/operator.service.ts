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

export class OperatorService {
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }


}