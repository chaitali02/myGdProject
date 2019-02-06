import { Response, Http } from '@angular/http';
import { Observable } from 'rxjs';
import { Injectable,Inject,Input } from '@angular/core';

import { SharedService } from './../../shared/shared.service';

@Injectable()
export class FilterService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
 
}