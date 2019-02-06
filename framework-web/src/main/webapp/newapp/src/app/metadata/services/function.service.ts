import { Response, Http } from '@angular/http';
import { Injectable,Inject,Input } from '@angular/core';
import { Observable } from 'rxjs';

import { SharedService } from './../../shared/shared.service';

@Injectable()
export class FunctionService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
}