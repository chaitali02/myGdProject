import { Observable } from 'rxjs/Observable';
import { SharedService } from './../../shared/shared.service';
import { Http,Response } from '@angular/http';
import { Inject, Input, Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()
export class ExpressionService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
}