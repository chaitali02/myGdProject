import { NgModule, Component, ViewEncapsulation } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import { AppMetadata } from '../app.metadata';
import { FilterPipeDD } from './pipes/search-pipe';
import { OrderBy } from './pipes/orderBy';
import { FilterMetaPipe } from '../metadata-navigator/pipes/search-pipe';
import { DatadiscoveryService } from './datadiscovery.service';

import { DataDiscovery } from './data-discovery';

@Component({
  selector: 'app-login',
  styleUrls: [],
  templateUrl: './datadiscovery.template.html',
  providers: [DatePipe]
})
export class DataDiscoveryComponent {
  currentPage: number;
  totalItems: number;
  optiondata: any;
  locations_temp: Array<DataDiscovery> = [];
  locations: Array<DataDiscovery>;
  optionsort: any;
  breadcrumbDataFrom: any;
  routerUrl: any;
  searchText: any;
  smallnumPages = 0;
  colorclassarray: any[];
  isEditInprogess : boolean;
  isEditError : boolean;
  isInProgress: boolean;
  constructor(private http: Http, private _service: DatadiscoveryService, private datePipe: DatePipe, public metaconfig: AppMetadata, public router: Router, private route: ActivatedRoute) {
    this.isEditInprogess = false;
    this.isEditError = false;
    this.currentPage = 1;
    this.breadcrumbDataFrom = [{
      "caption": "Data Discovery",
      "routeurl": null
    }
    ]
    this.colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"];
    this.optionsort = [
      { 'caption': 'Name A-Z', name: 'title' },
      { 'caption': 'Name Z-A', name: '-title' },
      { 'caption': 'Date Asc', name: 'lastUpdatedOn' },
      { 'caption': 'Date Desc', name: '-lastUpdatedOn' },
    ];
    this.optiondata = this.optionsort[0].name;
  }
  ngOnInit() {
    this.isEditInprogess = true;
    this._service.getDatapodStats().subscribe(
      response => { this.OnSuccesAllMeta(response) },
      error => {console.log('Error :: ' + error)
      this.isEditError = false;}
    )
  }
  OnSuccesAllMeta(response: any) {
    const data = response;
    let count = 0;
    for (const item in data) {
      if (data[item].lastUpdatedOn != null) {
        let date = response[item].lastUpdatedOn.split(" ");
        date.splice(date.length - 2, 1);
        data[item].lastUpdatedOn = new Date(date.toString().replace(/,/g, " "));
      }
      else {
        data[item].lastUpdatedOn = '';
      }
      if (data[item].numRows == null) {
        data[item].numRows = 0;
      }
      var randomno = Math.floor(Math.random() * Math.floor(4));
      data[item].classColor = this.colorclassarray[randomno];

      this.locations_temp.push(new DataDiscovery(
        data[item].ref.name,
        data[item].ref.uuid,
        data[item].ref.type,
        data[item].ref.version,
        data[item].dataSource,
        data[item].numRows,
        data[item].lastUpdatedOn,
        data[item].classColor
      ));
      count++;
    }
    this.totalItems = count;
    console.log(JSON.stringify(this.locations_temp));
    this.getResults(this.locations_temp);
  }

  getResults(data: any) {
    if (this.totalItems > 0) {
      let to = (((this.currentPage - 1) * (10)) + 1);
    }
    else {
      let to = 0;
    }
    if (this.totalItems < (10 * this.currentPage)) {
      let from = this.totalItems;
    } else {
      let from = ((this.currentPage) * 10);
    }
    var limit = (10 * this.currentPage);
    var offset = ((this.currentPage - 1) * 10)
    this.locations = data.slice(offset, limit);
    
    this.isEditInprogess = false;

    // setTimeout(() => {
    //   this.goBack()

    // }, 1000);

    // const arr=[10,12,15,21];
    // for(var i=0;i<arr.length;i++){
    //   setTimeout(function(i_local){
    //     return function(){
    //       console.log("the index of this number = "+i_local)
    //     }
    //   }(i),3000)
    // }
  }

  onShowDetail(event: Event, datadiscovry: any) {
    this.routerUrl = this.metaconfig.getMetadataDefs('datapod')['detailState']
    this.router.navigate(['./dataPreparation/datapod', datadiscovry.uuid, datadiscovry.version, 'true'], { relativeTo: this.route });
  }

  setPage(pageNo: number): void {
    this.currentPage = pageNo;
  }

  pageChanged(event: any): void {
    this.currentPage = event.page;
    this.getResults(this.locations_temp);
    console.log('Page changed to: ' + event.page);
    console.log('Number items per page: ' + event.itemsPerPage);
  }

  refersh() {
    this.isEditInprogess = true;
    this._service.getDatapodStats().subscribe(
      response => { this.OnSuccesAllMeta(response) },
      error => console.log('Error :: ' + error)
    )
  }
}

