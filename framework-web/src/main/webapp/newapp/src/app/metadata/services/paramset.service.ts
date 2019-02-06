import { Observable } from 'rxjs';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class ParamsetService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
}
