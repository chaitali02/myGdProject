
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location } from '@angular/common';

import { AppMetadata } from '../../app.metadata';
import { TableRenderComponent } from '../../shared/components/resulttable/resulttable.component'
import { JointjsGroupComponent } from '../../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../../metadata/services/common.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../../app.config';
@Component({
  selector: 'app-compareresult',
  templateUrl: './compareresult.template.html',
  styleUrls: []
})

export class CompareResultComponent {
  
  constructor(private _config : AppConfig, private http : Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService) {

  }

}