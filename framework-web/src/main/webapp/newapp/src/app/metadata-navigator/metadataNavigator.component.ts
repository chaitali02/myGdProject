import { NgModule, Component, ViewEncapsulation } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router, ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';

import { AppMetadata } from '../app.metadata';

import { metadataNavigatorService } from './metadataNavigator.service';
import { MetadataNavigator } from '../metadata/domain/metadataNavigator';

import { MetadataIO } from './../../app/metadata/domainIO/domain.metadataIO';

import { FilterMetaPipe } from './pipes/search-pipe';
import { OrderByMeta } from './pipes/orderBy';

@Component({
  selector: 'app-login',
  templateUrl: './metadataNavigator.template.html',
  providers: [DatePipe]
})
export class metadataNavigatorComponent {
  optiondata: { 'caption': string; name: string; };
  allMetaCount: any[];
  locations: Array<MetadataNavigator>;
  optionsort: any;
  breadcrumbDataFrom: any;
  metadata: MetadataNavigator;
  routerUrl: any;
  searchText: any;
  metajson: any;
  isEditInprogess: any;
  c: any;
  constructor(private http: Http, private datePipe: DatePipe, private _service: metadataNavigatorService, public metaconfig: AppMetadata, public router: Router, private route: ActivatedRoute) {
    this.breadcrumbDataFrom = [
      {
        "caption": "Metadata Navigator",
        "routeurl": null
      }
    ]
    this.isEditInprogess = false;
    this.metadata = new MetadataNavigator();
  }
  ngOnInit() {
    
    this.optionsort = [
      { 'caption': 'Name A-Z', name: 'caption' },
      { 'caption': 'Name Z-A', name: '-caption' },
      { 'caption': 'Date Asc', name: 'lastUpdatedOn' },
      { 'caption': 'Date Desc', name: '-lastUpdatedOn' },
    ];
    this.optiondata = this.optionsort[0].name;
    this.c = { 'caption': '', name: '' },
    this.c.caption = "Name A-Z";
    this.isEditInprogess= true
    this._service.getMetaStats().subscribe(
      response => {
        this.OnSuccesAllMeta(response)
      },
      error => console.log("Error :: " + error)
    )
  }

  OnSuccesAllMeta(response : MetadataNavigator) {
    this.metadata = response;
    let colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"]
    let metaarray = []
    let count = 0;
    for(const i in response){
      let patt = new RegExp("exec");
      let res = patt.exec(response[i].type);
      if (res == null && response[i].type != 'paramlistmodel' && response[i].type != 'paramlistdag' && response[i].type != 'condition' &&
        response[i].type != 'paramlistrule' && response[i].type != 'organization' && response[i].type != 'lov' &&
        response[i].type != 'appconfig' && response[i].type != 'dimension' && response[i].type != 'message' && response[i].type != 'log' && response[i].type != 'measure') {

        if (response[i].lastUpdatedOn != null) {
          let date = response[i].lastUpdatedOn.split(" ");
          date.splice(date.length - 2, 1);
          this.metadata[i].lastUpdatedOn = new Date(date.toString().replace(/,/g, " "));
        }
        else {
          this.metadata[i].lastUpdatedOn = response[i].lastUpdatedOn;
        }

        //for color
        let randomno = Math.floor((Math.random() * 4) + 0);
        this.metadata[i].colorClass = colorclassarray[randomno];
        
        //fetch icon & caption from app.metatdata.ts
        let type = response[i].type;

              let metaType = new MetadataIO();
              console.log(JSON.stringify(this.metaconfig.getMetadataDefs(response[i].type)));
              metaType = this.metaconfig.getMetadataDefs(response[i].type);

        this.metadata[i].caption = metaType.caption;
        this.metadata[i].icon= metaType.class;//contains icon name
        metaarray[count] = this.metadata[i];
        count = count + 1;
      }
    }
    this.allMetaCount = metaarray;
    console.log(this.allMetaCount)
    this.isEditInprogess= false
  }

  refersh() {
    this.isEditInprogess= true
    this.allMetaCount = [];
    this._service.getMetaStats().subscribe(
      response => {
        this.OnSuccesAllMeta(response)
      },
      error => console.log("Error :: " + error)
    )
  }
}
