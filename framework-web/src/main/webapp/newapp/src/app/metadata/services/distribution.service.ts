import { Observable } from 'rxjs';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'

import { SharedService } from '../../shared/shared.service';
import {CommonService} from './common.service';
import { BindingFlags } from '@angular/core/src/view';


@Injectable()

export class DistributionService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }


}