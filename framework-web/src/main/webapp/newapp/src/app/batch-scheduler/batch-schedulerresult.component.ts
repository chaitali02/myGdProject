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
  starttime: any;
  endtime: any;
  duration: any;

  constructor(private http: Http, public _sharedService: SharedService, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _metadataService: MetadataService, private activeroute: ActivatedRoute) {
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
    debugger
    this.breadcrumbDataFrom[2].caption = response[0]["name"];
    console.log('getExecListByBatchExec is start');
    this.batchexecData = response;

    let result;
    for (var i = 0; i < response.length; i++) {
      this.starttime = response[i].status[1]["createdOn"];
      this.endtime = response[i].status[2]["createdOn"];
      result = moment.utc(moment(this.endtime).diff(moment(this.starttime))).format("HH:mm:ss");
    }
    this.duration = result;

    this.batchStatus;
    for (var i = 0; i < response.length; i++) {
      this.batchStatus = response[i].status.slice(-1).pop();
    }

    // this.batch = response;
    // this.uuid = response.uuid;
    // const version: Version = new Version();
    // version.label = response['version'];
    // version.uuid = response['uuid'];
    // this.selectedVersion = version
    // this.createdBy = response.createdBy.ref.name;
    // this.batch.published = response["published"] == 'Y' ? true : false
    // this.batch.active = response["active"] == 'Y' ? true : false
    // this.version = response['version'];
    // var tags = [];
    // if (response.tags != null) {
    //   for (var i = 0; i < response.tags.length; i++) {
    //     var tag = {};
    //     tag['value'] = response.tags[i];
    //     tag['display'] = response.tags[i];
    //     tags[i] = tag

    //   }//End For
    //   this.batch.tags = tags;
    // }//End If


    // this.breadcrumbDataFrom[2].caption = this.batch.name;

    // let pipelineInfoNew = [];

    // for (const i in response.pipelineInfo) {
    //   let pipelinetag = {};
    //   pipelinetag["id"] = response.pipelineInfo[i].ref.uuid;
    //   pipelinetag["itemName"] = response.pipelineInfo[i].ref.name;
    //   pipelineInfoNew[i] = pipelinetag;
    // }
    // this.pipelineInfoTags = pipelineInfoNew;
    // this.attributes = response.attributes;
    // console.log(JSON.stringify(this.pipelineInfoTags));
  }


  viewPage(uuid, version) {
    debugger

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


}
