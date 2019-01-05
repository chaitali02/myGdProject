import { Http } from '@angular/http';
import * as c3 from 'c3'
import { Component, OnInit } from '@angular/core';
import { AppMetadata } from '../app.metadata';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { CommonService } from '../metadata/services/common.service';
// import{ DependsOn } from './dependsOn';
import { SystemMonitoringService } from '../metadata/services/systemMonitoring.service';
import { MenuModule, MenuItem } from 'primeng/primeng';
import { DatePipe } from '@angular/common';
import { DomSanitizer } from '@angular/platform-browser';
import { forkJoin } from "rxjs/observable/forkJoin";
import { SharedService } from '../shared/shared.service';
import { _ } from 'underscore/underscore.js'
@Component({
  selector: 'app-login',
  templateUrl: './system-monitoring.component.html',
  //styleUrls: [''],
  providers: [DatePipe]
})
export class SystemMonitoringComponent implements OnInit {
  metaType: any;
  view: string;
  rowVersion: any;
  rowUuid: any;
  graphJobData: any[];
  color: string[];
  result: any;
  responseUser: any;
  responseStatus: any;
  graphSessionData: any[];
  showDiv: any;
  Alltypes: any[];
  apps: any[];
  breadcrumbDataFrom: any;
  active: any;
  status: any;
  rowData1: any;
  appname: any;
  startDate: any;
  tags: any;
  endDate: any;
  username: any;
  allAppliName: any;
  allUserName: any;
  id: any;
  version: any;
  mode: any;
  type: any;
  gridTitle: any;
  appnameUuid: any;
  allName: any;
  app: any;
  uuid: any;
  appli: any;
  user: any;
  items: any
  moduleType: any = "session";
  cols: any
  allStatus = [
    {
      "caption": "In Progress",
      "name": "InProgress",
      "label": "In Progress",
      "value": "InProgress"
    },
    {
      "caption": "All",
      "name": "",
      "label": "All",
      "value": ""
    },
    {
      "caption": "Not Started",
      "name": "NotStarted",
      "label": "Not Started",
      "value": "NotStarted"
    },
    {
      "caption": "Completed",
      "name": "Completed",
      "label": "Completed",
      "value": "Completed"
    },
    {
      "caption": "Killed",
      "name": "Killed",
      "label": "Killed",
      "value": "Killed"
    },
    {
      "caption": "Failed",
      "name": "Failed",
      "label": "Failed",
      "value": "Failed"
    }
  ];
  allActive = [
    {
      "caption": "All",
      "name": " ",
      "label": "All",
      "value": " "
    },
    {
      "caption": "Active",
      "name": "Y",
      "label": "Active",
      "value": "active"
    },
    {
      "caption": "Expired",
      "name": "N",
      "label": "Expired",
      "value": "expired"
    }
  ];
  tabs = [
    { caption: 'Sessions', type: 'session' },
    { caption: 'Jobs', type: 'jobs' },
    { caption: 'Threads', type: 'threads' }
  ];

  constructor(private http: Http, public _sharedService: SharedService, public datePipe: DatePipe, public router: Router, public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, private _commonService: CommonService, private systemMonitoringService: SystemMonitoringService, private activeroute: ActivatedRoute) {
    this.breadcrumbDataFrom = [{
      "caption": "System Monitoring",
      "routeurl": null
    }
    ]

    this.clear();
    this.view = this.showDiv == 'true' ? 'graph' : 'all';
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.type = params['type'];
    })

    this.allAppliName = [];
    //this.gridTitle=metaconfig.getMetadataDefs(this.type)['caption']
    //this.routerUrl=metaconfig.getMetadataDefs(this.type)['detailState']

    // if(this.type.indexOf("exec") !=-1){
    //     temp=this.type.split("exec")[0];
    //     this.selectedType=temp;
    // }
  }
  clear() {
    this.active = "";
    this.status = "";
    this.rowData1 = null;
    this.appname = "";
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = ""
    this.appnameUuid;
    this.allAppliName = [];
    this.allName = {}
    this.app = {};
    this.app.uuid = "";
    this.type = {}
    this.type.label = ""
  }
  ngOnInit() {
    //this.rowData1=null;
    this.getAllLatestAppli();
    this.getAllLatestUser();
    this.getAllMeta();

    this.cols = [
      //{field: 'uuid', header: 'UUID'},
      { field: 'appName', header: 'Application' },
      // {field: 'version', header: 'Version'},
      { field: 'createdBy.ref.name', header: 'User' },
      { field: 'createdOn', header: 'Created On' },
      { field: 'status', header: 'Status' },
    ];
    this.gridTitle = "Sessions Details"
    this.getActiveSession();
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
      }]
  }
  onTabChange(event) {

    if (event.index == "0") {
      this.clear()
      this.moduleType = "session"
      if (this.showDiv) {
        this.showDiv = !this.showDiv
      }
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
        }]
      this.cols = [
        //{field: 'uuid', header: 'UUID'},
        { field: 'appName', header: 'Application' },
        //{field: 'version', header: 'Version'},
        { field: 'createdBy.ref.name', header: 'User' },
        { field: 'createdOn', header: 'Created On' },
        { field: 'status', header: 'Status' },
      ];
      this.getActiveSession()
      console.log(this.showDiv)
      this.gridTitle = "Sessions Details"
    }
    else if (event.index == "1") {
      this.clear()
      this.moduleType = "jobs";
      this.status = "InProgress"
      this.items = [
        {
          label: 'View', icon: 'fa fa-eye', command: (onclick) => {
            this.viewPage(this.rowUuid, this.rowVersion)
          }
        }]
      if (this.showDiv) {
        this.showDiv = !this.showDiv
      }
      this.cols = [
        { field: 'appName', header: 'Application' },
        { field: 'type', header: 'Meta' },
        { field: 'name', header: 'Name' },
        //{field: 'version', header: 'Version'},
        { field: 'createdBy.ref.name', header: 'Created By' },
        { field: 'createdOn', header: 'Created On' },
        { field: 'status', header: 'Status' },

      ];
      console.log(this.showDiv)
      this.getActiveJobs();
      this.gridTitle = "Jobs Details"
    }

    else {
      this.clear()
      this.moduleType = "threads";
      this.showDiv = false
      //this.showDiv=!this.showDiv
      this.items = [
        {
          label: 'View', icon: 'fa fa-eye', command: (onclick) => {
            this.viewPage(this.rowUuid, this.rowVersion)
          }
        },
        {
          label: 'Kill', icon: 'fa fa-pencil-square-o', command: (onclick) => {

          }
        }]
      this.cols = [
        { field: 'appName', header: 'Application' },
        { field: 'type', header: 'Meta' },
        { field: 'name', header: 'Name' },
        // {field: 'version', header: 'Version'},
      ];
      this.gridTitle = "Threads Details"
      this.getActiveThreads();
    }
    // console.log(this.moduleType)
  }

  searchCriteria() {
    if (this.moduleType == "session") {
      this.getActiveSession()
    }
    else if (this.moduleType == "jobs") {
      this.getActiveJobs();
    }
    else {
      this.getActiveThreads();
    }
  }
  getAllLatestAppli() {
    this._commonService.getAllLatest('application')
      .subscribe(
      response => { this.OnSucessgetAllLatest(response) },
      error => console.log("Error :: " + error)
      )
  }

  OnSucessgetAllLatest(response) {
    //this.allExecName=response;
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.apps = temp
    this.apps.splice(0, 0, {
      'label': '--Select--',
      'value': ''
    });

  }

  getAllLatestUser() {
    this._commonService.getAllLatest('user')
      .subscribe(
      response => { this.OnSucessgetAllLatestUser(response) },
      error => console.log("Error :: " + error)
      )
  }

  OnSucessgetAllLatestUser(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allUserName = temp
    this.allUserName.splice(0, 0, {
      'label': '--Select--',
      'value': ''
    });
    //console.log(this.allUserName)
  }
  getAllMeta() {
    this.systemMonitoringService.getMetaExecList()
      .subscribe(
      response => { this.OnSucessgetAllMeta(response) },
      error => console.log("Error :: " + error)
      )
  }

  OnSucessgetAllMeta(response) {
    //debugger
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n];
      allname["value"] = {};
      allname["value"]["label"] = response[n];
      temp[n] = allname;
    }

    this.Alltypes = temp
    this.Alltypes.splice(0, 0, {
      'label': '--Select--',
      'value': ''
    });
    //console.log(this.Alltypes)
  }
  getActiveSession() {
    var startDateUtcStr = "";
    var endDateUtcStr = "";
    if (this.startDate != "") {
      console.log(this.startDate);
      let startDateUtc = new Date(this.startDate.getUTCFullYear(), this.startDate.getUTCMonth(), this.startDate.getUTCDate(), this.startDate.getUTCHours(), this.startDate.getUTCMinutes(), this.startDate.getUTCSeconds())
      console.log(startDateUtc);
      startDateUtcStr = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//startDateUtc.toString().split("GMT")[0]+"UTC";
    }
    if (this.endDate != "") {
      let endDateUtc = new Date(this.endDate.getUTCFullYear(), this.endDate.getUTCMonth(), this.endDate.getUTCDate(), this.endDate.getUTCHours(), this.endDate.getUTCMinutes(), this.endDate.getUTCSeconds())
      endDateUtcStr = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//endDateUtc.toString().split("GMT")[0]+"UTC";
    }
    this.systemMonitoringService.getActiveSession(this.username.label || '', this.app.uuid, startDateUtcStr, endDateUtcStr, this.tags, this.active)
      .subscribe(
      response => { this.onSucessgetActiveSession(response) },
      error => console.log("Error :: " + error)
      )
  }

  onSucessgetActiveSession(response) {
    this.rowData1 = response
    // console.log(this.rowData1)
  }
  getActiveJobs() {
    this.systemMonitoringService.getActiveJobByCriteria(this.type.label || '', this.username.label || '', this.app.uuid, this.startDate, this.endDate, this.tags, this.status)
      .subscribe(
      response => { this.onSucessgetActiveJobs(response) },
      error => console.log("Error :: " + error)
      )
  }

  onSucessgetActiveJobs(response) {
    this.rowData1 = response
    //console.log(this.rowData1)
  }
  getActiveThreads() {
    this.systemMonitoringService.getActiveThread()
      .subscribe(
      response => { this.onSucessgetActiveThreads(response) },
      error => console.log("Error :: " + error)
      )
  }

  onSucessgetActiveThreads(response) {
    this.rowData1 = response
    //console.log(this.rowData1)
  }
  getdata(mode, type) {
    if (type == "session") {
      this.color = ["#76d7c4", "#85c1e9", "#84F0BA", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"];
      var startDateUtcStr = "";
      var endDateUtcStr = "";
      if (this.startDate != "") {
        console.log(this.startDate);
        let startDateUtc = new Date(this.startDate.getUTCFullYear(), this.startDate.getUTCMonth(), this.startDate.getUTCDate(), this.startDate.getUTCHours(), this.startDate.getUTCMinutes(), this.startDate.getUTCSeconds())
        console.log(startDateUtc);
        startDateUtcStr = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//startDateUtc.toString().split("GMT")[0]+"UTC";
      }
      if (this.endDate != "") {
        let endDateUtc = new Date(this.endDate.getUTCFullYear(), this.endDate.getUTCMonth(), this.endDate.getUTCDate(), this.endDate.getUTCHours(), this.endDate.getUTCMinutes(), this.endDate.getUTCSeconds())
        endDateUtcStr = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//endDateUtc.toString().split("GMT")[0]+"UTC";
      }
      //this.getSessionByUser();
      //this.getSessionCountByStatus()
      let observables = new Array();
      let _vizpodtrack = [];
      let promises = []
      let apiList = ["system/getSessionCountByUser", "system/getSessionCountByStatus"];
      var jobArray = []
      for (var i = 0; i < apiList.length; i++) {
        var url = apiList[i] + "?type=" + type + "&userName=" + (this.username.label || '') + "&startDate=" + startDateUtcStr + "&endDate=" + endDateUtcStr + "&tags=" + this.tags + "&appuuid=" + this.app.uuid + "&status=" + this.active
        var promise = this._sharedService.getGraphData(url)
        promises.push(promise)
      }
      forkJoin(promises).subscribe(
        result => {
          //console.log(result)
          var resultArray = [];
          for (var i = 0; i < result.length; i++) {
            var jobresult = {};
            var jobgraphcolumnArray = []
            var jobgraphdataArray = []
            var count = 0;
            resultArray[i] = result[i]

            _.map(resultArray[i], function (value, key) {
              var jobgraphcolumn = {};
              var jobgraphData = {};
              jobgraphcolumn["id"] = key
              jobgraphcolumnArray[count] = key;
              jobgraphData[jobgraphcolumn["id"]] = value;
              jobgraphdataArray[count] = jobgraphData
              count = count + 1;
            });
            jobresult["id"] = "chart" + i;
            jobresult["title"] = i == 0 ? "Session-User" : "Session-Status"
            jobresult["type"] = i == 0 ? "pie" : "donut"
            jobresult["datax"] = " "
            jobresult["show"] = true;
            jobresult["showtooltiptitle"] = "Expand";
            jobresult["iconClass"] = "fa fa-expand";
            jobresult["colExp"] = true;
            jobresult["datacolumns"] = jobgraphcolumnArray
            jobresult["datapoints"] = jobgraphdataArray

            jobArray[i] = jobresult
          }

          this.graphSessionData = jobArray
          console.log(this.graphSessionData)
        },
        error => console.log('Error: ', error)
      );
    }
    else if (type == "jobs") {
      this.color = ["#76d7c4", "#85c1e9", "#84F0BA", "#bb8fce", "#7fb3d5", "#85c1e9", "#76d7c4", "#73c6b6", "#7dcea0", "#82e0aa", "#f7dc6f", "#f8c471", "#f0b27a", "#e59866"];
      var startDateUtcStr = "";
      var endDateUtcStr = "";
      if (this.startDate != "") {
        console.log(this.startDate);
        let startDateUtc = new Date(this.startDate.getUTCFullYear(), this.startDate.getUTCMonth(), this.startDate.getUTCDate(), this.startDate.getUTCHours(), this.startDate.getUTCMinutes(), this.startDate.getUTCSeconds())
        console.log(startDateUtc);
        startDateUtcStr = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//startDateUtc.toString().split("GMT")[0]+"UTC";
      }
      if (this.endDate != "") {
        let endDateUtc = new Date(this.endDate.getUTCFullYear(), this.endDate.getUTCMonth(), this.endDate.getUTCDate(), this.endDate.getUTCHours(), this.endDate.getUTCMinutes(), this.endDate.getUTCSeconds())
        endDateUtcStr = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//endDateUtc.toString().split("GMT")[0]+"UTC";
      }
      //this.getSessionByUser();
      //this.getSessionCountByStatus()
      let observables = new Array();
      let _vizpodtrack = [];
      let promises = []
      var apiList = [{ "url": "system/getJobCountByApp", "type": "bar", "title": "Jobs-App" }, { "url": "system/getJobCountByUser", "type": "pie", "title": "Jobs-User" }, { "url": "system/getJobCountByMeta", "type": "pie", title: "Jobs-Meta" }, { "url": "system/getJobCountByStatus", "type": "donut", "title": "Jobs-Status" }];
      var jobArray = []
      for (var i = 0; i < apiList.length; i++) {
        var url = apiList[i].url + "?type=" + (this.type.label || '') + "&userName=" + (this.username.label || '') + "&startDate=" + startDateUtcStr + "&endDate=" + endDateUtcStr + "&tags=" + this.tags + "&appuuid=" + this.app.uuid + "&status=" + this.active
        var promise = this._sharedService.getGraphData(url)
        promises.push(promise)
      }
      forkJoin(promises).subscribe(
        result => {
          console.log(result)
          //   var resultArray=[];
          for (var i = 0; i < result.length; i++) {
            var jobresult = {};
            var resultArray = [];
            var jobgraphcolumnArray = []
            var jobgraphdataArray = []
            var count = 0;
            resultArray[i] = result[i]
            _.map(resultArray[i], function (value, key) {
              var jobgraphcolumn = {};
              var jobgraphData = {};
              if (i == 2) {
                jobgraphcolumn["id"] = key;
                jobgraphcolumnArray[count] = key;
              }

              else {
                jobgraphcolumn["id"] = key
                jobgraphcolumnArray[count] = key;
              }
              jobgraphData[jobgraphcolumn["id"]] = value;
              jobgraphdataArray[count] = jobgraphData
              count = count + 1;
            });
            jobresult["id"] = "chart" + (i + 5)
            jobresult["show"] = true;
            jobresult["showtooltiptitle"] = "Expand";
            jobresult["iconClass"] = "fa fa-expand";
            jobresult["colExp"] = true;
            jobresult["index"] = i + 5;
            jobresult["title"] = apiList[i].title
            jobresult["type"] = apiList[i].type
            jobresult["datax"] = " "
            jobresult["datacolumns"] = jobgraphcolumnArray
            jobresult["datapoints"] = jobgraphdataArray
            jobArray[i] = jobresult
          }
          this.graphJobData = jobArray
          console.log(this.graphJobData)
        },
        error => console.log('Error: ', error)
      );
    }
  }
  fullScreen(index) {
    if (this.graphSessionData[index].iconClass != "fa fa-expand") {
      for (let i = 0; i < this.graphSessionData.length; i++) {
        this.graphSessionData[i].show = true;
        //this.graphSessionData[index]["showtooltiptitle"]="Expand";
        this.graphSessionData[index].iconClass = "fa fa-expand";
        this.graphSessionData[index].width = "";
      }
    } else {
      for (let i = 0; i < this.graphSessionData.length; i++) {
        this.graphSessionData[i].show = false;
      }
      this.graphSessionData[index].show = true;
      //this.graphSessionData[index]["showtooltiptitle"]="Compress";
      this.graphSessionData[index].iconClass = "fa fa-compress";
      this.graphSessionData[index].width = "100%";
    }
    window.dispatchEvent(new Event('resize'));

    // $timeout(function() {
    //   $window.dispatchEvent(new Event("resize"));
    // }, 100);
  }
  collapseExpend(index) {
    this.graphSessionData[index].colExp = !this.graphSessionData[index].colExp
    window.dispatchEvent(new Event('resize'));
    // $timeout(function() {
    //   Window.dispatchEvent(new Event("resize"));
    // }, 100);
  }
  onClickMenu(data) {
    // alert(this.type)
    console.log(data);
    this.rowUuid = data.uuid;
    this.rowVersion = data.version
    if (data.type) {
      this.metaType = data.type
    }
  }
  viewPage(uuid, version) {
    if (this.moduleType == "session") {
      this.router.navigate(["../../admin/session/", uuid, version, 'true'], { relativeTo: this.activeroute });
    }
    // else if(this.moduleType=="jobs"){
    //   this.getActiveJobs();
    // }
    else {
      let _moduleUrl = this.metaconfig.getMetadataDefs(this.metaType)['moduleState']
      let _routerUrl = this.metaconfig.getMetadataDefs(this.metaType)['detailState']
      this.router.navigate(["../../" + _moduleUrl + '/' + _routerUrl, uuid, version, 'true'], { relativeTo: this.activeroute });
    }
  }

  refreshSearchMon() {

    if (this.moduleType == "session") {
      this.rowData1 = null;
      this.getActiveSession()
    }
    else if (this.moduleType == "jobs") {
      this.rowData1 = null;
      this.getActiveJobs();
    }
    else {
      this.rowData1 = null;
      this.getActiveThreads();

    }
  }
  refershGrid() {

    if (this.moduleType == "session") {
      this.app = "";
      this.active = ""
      this.status = "";
      this.rowData1 = null;
      this.startDate = "";
      this.tags = "";
      this.endDate = "";
      this.username = ""
      this.getAllLatestAppli();
      this.getAllLatestUser();
      this.getAllMeta();
      this.getActiveSession()
    }
    else if (this.moduleType == "jobs") {
      this.app = "";
      this.active = "";
      this.status = "";
      this.rowData1 = null;
      this.startDate = "";
      this.tags = "";
      this.endDate = "";
      this.username = ""
      this.getAllLatestAppli();
      this.getAllLatestUser();
      this.getAllMeta();
      this.getActiveJobs();
    }
    else {
      this.app = "";
      this.active = "";
      this.status = "";
      this.rowData1 = null;
      this.startDate = "";
      this.tags = "";
      this.endDate = "";
      this.username = ""
      this.getAllLatestAppli();
      this.getAllLatestUser();
      this.getAllMeta();
      this.getActiveThreads();

    }
  }

}
