import { Observable } from 'rxjs';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'

import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';



@Injectable()

export class OperatorService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }


}