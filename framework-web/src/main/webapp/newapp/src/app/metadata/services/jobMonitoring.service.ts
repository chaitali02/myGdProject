import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()

export class JobMonitoringService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  
}