import { NgModule, Component, ViewEncapsulation } from '@angular/core';
import {Router,ActivatedRoute} from '@angular/router';
import {metadataNavigatorService} from './metadataNavigator.service';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { metadataNavigator } from './metadataNavigator';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import {FilterMetaPipe} from './pipes/search-pipe';
import {OrderByMeta} from './pipes/orderBy';
import {AppMetadata} from '../app.metadata';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-login',
  styleUrls: [],
  templateUrl: './metadataNavigator.template.html',
  providers: [DatePipe]
})
export class metadataNavigatorComponent {
  optiondata: { 'caption': string; name: string; };
  allMetaCount: any[];

  locations: Array<metadataNavigator>;
  optionsort:any;
  breadcrumbDataFrom:any;
  routerUrl:any;
  searchText:any;
  constructor(private http: Http, private datePipe: DatePipe, private _service: metadataNavigatorService,public metaconfig: AppMetadata, public router: Router,private route: ActivatedRoute) {
    this.breadcrumbDataFrom=[{
      "caption":"Metadata Navigator",
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

    this._service.getMetaStats()
    .subscribe(
      response =>{
        this.OnSuccesAllMeta(response)
    },
      error => console.log("Error :: " + error)
    )
  }
  OnSuccesAllMeta(response) {
    const data = response;
    let colorclassarray = ["blue-sharp", "green-sharp", "purple-soft", "red-haze"]
    let metaarray = []
    let count=0;
    for (let i = 0; i < response.length; i++) {
      let patt = new RegExp("exec");
      let res = patt.exec(response[i].type);
      if (res == null && response[i].type!='condition' && response[i].type!='dimension'&& response[i].type!='message' && response[i].type!='log' && response[i].type!='measure') {
            let metajson = {};
            metajson["type"] = response[i].type;
            metajson["count"] = response[i].count;
            metajson["lastUpdatedBy"] = response[i].lastUpdatedBy;
            if(response[i].lastUpdatedOn!=null){
              //let date=new Date(response[i].lastUpdatedOn.split("IST")[0])
             // metajson["lastUpdatedOn"] = this.datePipe.transform(date, 'MM-dd-yyyy');
             metajson["lastUpdatedOn"] = new Date(response[i].lastUpdatedOn.split("IST")[0]);
            }
            else{
              metajson["lastUpdatedOn"] = response[i].lastUpdatedOn;
            }
            let randomno = Math.floor((Math.random() * 4) + 0);
            metajson["class"] = colorclassarray[randomno];

            metajson["caption"] = this.metaconfig.getMetadataDefs(response[i].type)['caption'];
            metajson["icon"] = this.metaconfig.getMetadataDefs(response[i].type)['class']
            metaarray[count] = metajson;
            count=count+1;

      }
   
  }
  this.allMetaCount = metaarray;
  console.log(this.allMetaCount)
}
}
