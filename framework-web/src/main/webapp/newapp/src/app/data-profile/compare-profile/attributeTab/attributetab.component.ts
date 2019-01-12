import { MetadataService } from '../../../metadata/services/metadata.service';
import { DataQualityService } from '../../../metadata/services/dataQuality.services';
import { Component, Input, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { Location, DatePipe } from '@angular/common';
import { AppMetadata } from '../../../app.metadata';
import { CommonService } from '../../../metadata/services/common.service';
import { Http } from '@angular/http';
import { AppConfig } from '../../../app.config';

@Component({
  selector: 'app-attributetab',
  templateUrl: './attributetab.template.html',
  styleUrls: []
})

export class AttributeTabComponent {

  tabs: any[] = [
    { caption: 'Results', title: 'Results Settings' },
    { caption: 'Atttributes', title: 'Atttributes Engine Settings' }
  ];
  type: any;

  constructor(private _config: AppConfig, private http: Http, private _location: Location, private _activatedRoute: ActivatedRoute, private router: Router, public appMetadata: AppMetadata, private _commonService: CommonService, private _dataQualityService: DataQualityService, private _metadataService: MetadataService, private datePipe: DatePipe) {
    
    this._activatedRoute.params.subscribe((params: Params) => {
      this.type = (params['type']).toLowerCase();
      
    });
    
  }

  onTabChange(event) {
    if (event.index == "0") {
      console.log("00000000");
      
    }
    else if (event.index == "1") {
      console.log("111111111111");
    }
  }

}