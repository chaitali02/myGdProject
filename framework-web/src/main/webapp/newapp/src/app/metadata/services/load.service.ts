import { Response, Http } from '@angular/http';
import { Injectable, Inject, Input } from '@angular/core';

import { SharedService } from './../../shared/shared.service';



@Injectable()
export class LoadService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }

}