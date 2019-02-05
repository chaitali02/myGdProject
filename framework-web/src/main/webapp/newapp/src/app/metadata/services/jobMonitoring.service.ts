
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'

import { SharedService } from '../../shared/shared.service';


@Injectable()

export class JobMonitoringService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }


}