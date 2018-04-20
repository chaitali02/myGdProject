import { NgModule, Component, ViewEncapsulation } from '@angular/core';
import { DatePipe } from '@angular/common';
import {Router,ActivatedRoute} from '@angular/router';
import {DatadiscoveryService} from './datadiscovery.service';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { DataDiscovery } from './data-discovery';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import { DatapodComponent} from '../data-preparation/datapod/datapod.component';
import {FilterPipeDD} from './pipes/search-pipe';
import {OrderBy} from './pipes/orderBy';
import {AppMetadata} from '../app.metadata';

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
  optionsort:any;
  breadcrumbDataFrom:any;
  routerUrl:any;
  searchText:any;
  smallnumPages = 0;
  constructor(private http: Http, private _service: DatadiscoveryService,private datePipe: DatePipe,public metaconfig: AppMetadata, public router: Router,private route: ActivatedRoute) {
    this.currentPage = 1;
    this.breadcrumbDataFrom=[{
      "caption":"Data Discovery",
      "routeurl":null
    }
    ]
    this.optiondata={'caption':'Name A-Z',name:'title'};
    this.optionsort = [
      {'caption': 'Name A-Z', name: 'title'},
      {'caption': 'Name Z-A', name: '-title'},
      {'caption': 'Date Asc', name: 'lastUpdatedOn'},
      // {'caption': 'Date Desc', name: '-lastUpdatedOn'},
    ];
  }
  ngOnInit() {
    const data1 = this._service.getDatapodStats().subscribe(
      response => { this.OnSuccesAllMeta(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesAllMeta(response) {
    const data = response;
    // console.log(response.length())
    // this.totalItems = response.length();
     
    let count=0;
    for (const item in data) {
        this.locations_temp.push(new DataDiscovery(  
        data[item].ref.name,
        data[item].ref.uuid,
        data[item].ref.type,
        data[item].ref.version,
        data[item].dataSource,
        data[item].numRows,        
        //this.datePipe.transform(new Date(data[item].lastUpdatedOn),"MM dd yyyy")
        //new Date(data[item].lastUpdatedOn.split('IST')[0])
        new Date(null)
      ));
      count++;
    }
    this.totalItems=count;
    //console.log(count);
    this.getResults(this.locations_temp);
    //console.log('locations', JSON.stringify(this.locations));
  }
  getResults(data){
    
    if(this.totalItems >0){
    let to = (((this.currentPage - 1) * (10))+1);
    }
    else{
      let to=0;
    }
    if (this.totalItems < (10*this.currentPage)) {
      let from = this.totalItems;
    } else {
      let from = ((this.currentPage) * 10);
    }
    var limit = (10*this.currentPage);
    var offset = ((this.currentPage - 1) * 10)
     this.locations=data.slice(offset,limit);
  }
  onShowDetail(event, datadiscovry) {
    this.routerUrl=this.metaconfig.getMetadataDefs('datapod')['detailState']

    this.router.navigate(['./dataPreparation/datapod', datadiscovry.uuid, datadiscovry.version, 'true'],{ relativeTo: this.route });
  }

  setPage(pageNo: number): void {
    this.currentPage = pageNo;
  }

  pageChanged(event: any): void {
    this.currentPage=event.page;
    this.getResults(this.locations_temp);
    console.log('Page changed to: ' + event.page);
    console.log('Number items per page: ' + event.itemsPerPage);
  }
}

