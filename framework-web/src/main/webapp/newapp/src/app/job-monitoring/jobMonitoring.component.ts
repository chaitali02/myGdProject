import { NgModule, Component, ViewEncapsulation } from '@angular/core';
import {Router,ActivatedRoute} from '@angular/router';
import {jobMonitoringService} from './jobMonitoring.service';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { jobMonitoring } from './jobMonitoring';

import {AppMetadata} from '../app.metadata';
import {FilterJobPipe} from './pipes/search-pipe';
import {OrderByJob} from './pipes/orderBy';

@Component({
  selector: 'app-login',
  styleUrls: [],
  templateUrl: './jobMonitoring.template.html'
})
export class jobMonitoringComponent {
  optiondata: { 'caption': string; name: string; };
  allMetaCount: any[];

  locations: Array<jobMonitoring>;
  optionsort:any;
  breadcrumbDataFrom:any;
  routerUrl:any;
  searchTextJob:any;
  constructor(private http: Http, private _service: jobMonitoringService,public metaconfig: AppMetadata, public router: Router,private route: ActivatedRoute) {
    this.breadcrumbDataFrom=[{
      "caption":"Job Monitoring",
      "routeurl":null
    }
    ]

  }
  ngOnInit() {

    this.optiondata={'caption':'Name A-Z',name:'caption'};
    this.optionsort = [
      {'caption': 'Name A-Z', name: 'caption'},
      {'caption': 'Name Z-A', name: '-caption'},
      {'caption': 'Date Asc', name: 'lastUpdatedOn'},
      {'caption': 'Date Desc', name: '-lastUpdatedOn'},
    ];

    this._service.getExecStats()
    .subscribe(
      response =>{
        this.OnSuccesAllExecMeta(response)
    },
      error => console.log("Error :: " + error)
    )
  }
  OnSuccesAllExecMeta(response) {
    const data = response;
    let colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"]
    let metaarray = []
    let count=0;
    for (let i = 0; i < response.length; i++) {
      let metajson = {};
      metajson["type"] = response[i].type;
      metajson["count"] = response[i].count;
      metajson["lastUpdatedBy"] = response[i].lastUpdatedBy;
      if(response[i].lastUpdatedOn!=null){
        metajson["lastUpdatedOn"] = new Date(response[i].lastUpdatedOn.split("IST")[0]);
      }
      else{
         metajson["lastUpdatedOn"] = response[i].lastUpdatedOn;
      }
      let randomno = Math.floor((Math.random() * 4) + 0);
      metajson["class"] = colorclassarray[randomno];
      metajson["caption"] = this.metaconfig.getMetadataDefs((response[i].type).toLowerCase())['caption'];
      metajson["icon"] = this.metaconfig.getMetadataDefs((response[i].type).toLowerCase())['class']
      metaarray[count] = metajson;
      count=count+1;   
    }
    this.allMetaCount = metaarray;
  }
}
