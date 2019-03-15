import { BaseEntity } from './../metadata/domain/domain.baseEntity';
import { Http } from '@angular/http';
import * as c3 from 'c3'
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppMetadata } from '../app.metadata';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MetadataService } from '../metadata/services/metadata.service';
// import{ DependsOn } from './dependsOn';
import { SharedService } from '../shared/shared.service';
import { MenuModule, MenuItem } from 'primeng/primeng';
import * as moment from 'moment'
import { CommonListService } from '../common-list/common-list.service';
import { DatePipe } from '@angular/common';
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { MetaType } from '../metadata/enums/metaType';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { BaseEntityStatus } from '../metadata/domain/domain.baseEntityStatus';

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
  metaType = MetaType;
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
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  // showGraph: boolean;
  isHomeEnable: boolean;
  showHome: boolean;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  showForm: boolean = true;
  showDivGraph: boolean;
  isGraphInprogess: boolean;
  isEdit: boolean = false;
  isAdd: boolean;
  isversionEnable: boolean;

  constructor(private _commonListService: CommonListService, private http: Http, public _sharedService: SharedService, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _metadataService: MetadataService, private activeroute: ActivatedRoute, private datePipe: DatePipe) {
    // this.showGraph = false
    this.isHomeEnable = false

    this.breadcrumbDataFrom = [
      {
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
    ];

    this.isEditInprogess = false;
    this.isEditError = false;
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
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;

      if (this.mode !== undefined) {
        this.getExecListByBatchExec();
      } //this.getAllLatestPipeline();
    })

    this.setMode(this.mode);
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getExecListByBatchExec(): any {
    this.isEditInprogess = true;
    this.isEditError = false;

    this._metadataService.getExecListByBatchExec(this.id, this.version, this.metaType.BATCHEXEC)
      .subscribe(
        response => {
          this.onSuccessgetExecListByBatchExec(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }
  onSuccessgetExecListByBatchExec(response: any[]) {
    this.breadcrumbDataFrom[2].caption = response[0].name;
    console.log('getExecListByBatchExec is start');
    this.batchexecData = response;

    for (var i = 0; i < response.length; i++) {
      for (var j = 0; j < response[i].status.length; j++) {
        if (response[i].status[j].stage == "InProgress") {
          this.starttime = this.datePipe.transform(response[i].status[j]["createdOn"], "EEE MMM dd HH:mm:ss yyyy");
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
          this.endtime = this.datePipe.transform(response[i].status[j]["createdOn"], "EEE MMM dd HH:mm:ss yyyy");
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
        this.batchexecData[i]['stage'] = response[i].status[j].stage;
        this.batchexecData[i]['color'] = this.metaconfig.getStatusDefs(response[i].status[j].stage)['color'];
      }
    }

    this.isEditInprogess = false;
  }

  viewPage(uuid, version) {debugger
    let _moduleUrl = this.metaconfig.getMetadataDefs("dagexec")['moduleState']
    let _routerUrl = this.metaconfig.getMetadataDefs("dagexec")['resultState']
    this.router.navigate(["../../../../../" + _moduleUrl + "/" + _routerUrl, uuid, version, 'true'], { relativeTo: this.activeroute });
  }

  onClickMenu(uuid, version, mtype) {
    this.rowUuid = uuid;
    this.rowVersion = version
    this.metaType = mtype;
  }

  public goBack() {
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
}
