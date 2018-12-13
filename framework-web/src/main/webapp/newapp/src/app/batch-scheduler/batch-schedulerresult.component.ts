import { Http } from '@angular/http';
import * as c3 from 'c3'
import { Component, OnInit } from '@angular/core';
import { AppMetadata } from '../app.metadata';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MetadataService } from '../metadata/services/metadata.service';
// import{ DependsOn } from './dependsOn';
import { SharedService } from '../shared/shared.service';
import { MenuModule, MenuItem } from 'primeng/primeng';
import * as moment from 'moment'
import { CommonListService } from '../common-list/common-list.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-batch-schedulerresult-component',
  templateUrl: './batch-schedulerresult.component.html'
  //styleUrls: ['./batch-schedulerresult.component.css']
})
export class BatchSchedulerResultComponent {

  breadcrumbDataFrom: any;
  batchexecData: any;
  //items: any
  rowUuid: any;
  rowVersion: any;
  cols: any
  metaType: any;
  items: MenuItem[];
  attributes: any[];
  id: any;
  version: any;
  mode: any;
  batchStatus: any;
  batchexecData1: any;
  starttime: any = "- N/A -";
  endtime: any = "- N/A -";
  duration: any = "- N/A -";

  constructor(private _commonListService: CommonListService, private http: Http, public _sharedService: SharedService, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _metadataService: MetadataService, private activeroute: ActivatedRoute,private datePipe: DatePipe) {
    this.breadcrumbDataFrom = [{
      "caption": "Batch Scheduler",
      "routeurl": "/app/list/batchexec"
    },
    {
      "caption": "Result",
      "routeurl": "/app/list/batchexec"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
  }

  ngOnInit() {
    this.items = [
      {
        label: 'View', icon: 'fa fa-eye', command: (onclick) => {
          this.viewPage(this.rowUuid, this.rowVersion)
        }
      },
      {
        label: 'Kill', icon: 'fa fa-pencil-square-o', command: (onclick) => {
          //this.edit(this.rowUUid,this.rowVersion)
        }
      },
      {
        label: 'Restart', icon: 'fa fa-pencil-square-o', command: (onclick) => {
          //this.edit(this.rowUUid,this.rowVersion)
        }
      }];

    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];

      if (this.mode !== undefined) {
        this.getExecListByBatchExec();
      } //this.getAllLatestPipeline();
    })

  }
  getExecListByBatchExec(): any {
    this._metadataService.getExecListByBatchExec(this.id, this.version, 'batchexec')
      .subscribe(
        response => {
          this.onSuccessgetExecListByBatchExec(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccessgetExecListByBatchExec(response: any[]): any {


    this.breadcrumbDataFrom[2].caption = response[0]["name"];
    console.log('getExecListByBatchExec is start');
    this.batchexecData = response;

    // let result;
    // for (var i = 0; i < response.length; i++) {
    //   this.starttime = response[i].status[1]["createdOn"];
    //   this.endtime = response[i].status[2]["createdOn"];
    //   result = moment.utc(moment(this.endtime).diff(moment(this.starttime))).format("HH:mm:ss");
    // }
    // this.duration = result;
    // ----------
    
    for (var i = 0; i < response.length; i++) {

      for (var j = 0; j < response[i].status.length; j++) {
        if (response[i].status[j]["stage"] == "InProgress") {
          this.starttime = this.datePipe.transform(response[i].status[j]["createdOn"],"EEE MMM dd HH:mm:ss yyyy");
          //obj["startDate"] = new Date(this.datePipe.transform(response['scheduleInfo'][i].startDate, "EEE MMM dd HH:mm:ss +0530 yyyy"));
      
          this.batchexecData[i]["starttime"] = this.starttime;
          break;
        }
        else {
          this.starttime = "- N/A -";
          this.batchexecData[i]["starttime"] = this.starttime;
        }
      }

      for (var j = 0; j < response[i].status.length; j++) {
        if (response[i].status[j]["stage"] == "Completed") {
          this.endtime = this.datePipe.transform(response[i].status[j]["createdOn"],"EEE MMM dd HH:mm:ss yyyy");
          this.batchexecData[i]["endtime"] = this.endtime;
          this.duration = moment.utc(moment(this.endtime).diff(moment(this.starttime))).format("HH:mm:ss");
          this.batchexecData[i]["duration"] = this.duration;
        }
        else {
          this.endtime = "- N/A -";
          this.duration = "- N/A -";
          this.batchexecData[i]["endtime"] = this.endtime;
          this.batchexecData[i]["duration"] = this.duration;
        }
      }
      
      for (var j = 0; j < response[i].status.length; j++) {
        //if (response[i].status[j]["stage"] != null) {
          this.batchexecData[i]['stage'] = response[i].status[j]["stage"];
          this.batchexecData[i]['color'] = this.metaconfig.getStatusDefs(response[i].status[j]["stage"])['color'];
        //}
      }
      
    }
  }


  viewPage(uuid, version) {


    let _moduleUrl = this.metaconfig.getMetadataDefs("dagexec")['moduleState']
    let _routerUrl = this.metaconfig.getMetadataDefs("dagexec")['resultState']
    //this.router.navigate(["../../" + _moduleUrl + '/' + _routerUrl, uuid, version, 'true']);

    //ORIGINAL 2: 
    //this.router.navigate(["../../../../../../" +_moduleUrl + '/' + _routerUrl, uuid, version, 'true' ], { relativeTo: this.activeroute });

    this.router.navigate(["../../../../../" + _moduleUrl + "/" + _routerUrl, uuid, version, 'true'], { relativeTo: this.activeroute });

    // }
    // let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
    // this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['resultState']
    // this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl, uuid, version, this.type, 'true'], { relativeTo: this.activeroute });


  }

  onClickMenu(uuid, version, mtype) {

    // alert(this.type)
    //console.log(data);
    this.rowUuid = uuid;
    this.rowVersion = version
    this.metaType = mtype;
  }

  public goBack() {
    //  this._location.back();
    this.router.navigate(['app/list/batchexec']);

  }
  refershGrid() {
    this.batchexecData.name = ''
    this.starttime = ''
    this.endtime = ''
    this.duration = ''
    this.batchStatus = ''
    this.getExecListByBatchExec();
  }
  // getBaseEntityStatusByCriteria(): void {
  //   this._commonListService.getBaseEntityByCriteria((("batchexec").toLowerCase()), "", "", "", "", "", "", "")
  //       .subscribe(
  //       response => { this.onSuccessgetExecListByBatchExec(response) },
  //       error => console.log("Error :: " + error)
  //       )
  // }
}
