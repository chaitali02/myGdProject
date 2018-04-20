import { Response, Http } from '@angular/http';
import { SharedService } from './../../shared/shared.service';
import { Observable } from 'rxjs/Observable';
import { Injectable,Inject,Input } from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()
export class FilterService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
 
}