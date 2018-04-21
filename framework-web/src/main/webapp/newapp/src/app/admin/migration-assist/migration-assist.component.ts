import { BaseEntityStatus } from './../../metadata/domain/domain.baseEntityStatus';
import { CommonListService } from './../../common-list/common-list.service';
import { Location } from '@angular/common';
import { Component, OnInit, NgModule, Injectable, Inject } from '@angular/core';
import { ActivatedRoute, Router, Params, Routes } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { CommonListComponent } from '../../common-list/common-list.component';
import { AppMetadata } from '../../app.metadata';
import { saveAs } from 'file-saver/FileSaver';
import { AppConfig } from '../../app.config';
import { Http } from '@angular/http';

@Component({
  selector: 'app-migration-assist',
  templateUrl: './migration-assist.component.html',
})

export class MigrationAssistComponent implements OnInit {
  apps: any[];
  id: any;
  version: any;
  mode: any;
  breadcrumbDataFrom: any;
  app: any;
  startDate: any;
  tags: any;
  allUserName: any;
  status: any;
  rowData1: any;
  cols: any;
  items: any;
  gridTitle: any;
  username: any;
  endDate: any;
  active: any;
  published: any;
  uuid: any;
  moduleType: any = "export";
  routerUrl: any;
  rowUUid: any;
  rowVersion: any;
  rowID: any;
  rowName: any;
  exportId: any;
  exportname: any;
  isModel: any;
  row: any;
  uuidAPI: any;
  headers:Headers;

  //     var filename = headers['x-filename'];
  //     var contentType = headers['content-type'];
  //     var linkElement = document.createElement('a');
  //     try {
  //       var blob = new Blob([data], {
  //         type: contentType
  //       });
  //       var url = window.URL.createObjectURL(blob);

  //       linkElement.setAttribute('href', url);
  //       linkElement.setAttribute("download", uuid+".zip");

  //       var clickEvent = new MouseEvent("click", {
  //         "view": window,
  //         "bubbles": true,
  //         "cancelable": false
  //       });
  //       linkElement.dispatchEvent(clickEvent);
  //     } catch (ex) {
  //       console.log(ex);
  //     }
  //   }).error(function(data) {
  //     console.log(data);  //   });   // };   // })

  ModelDataFrom: { "caption": string; "message": any; "functionName": any };
  msgs: any;
  tabs = [
    { caption: 'Export', type: 'export' },
    { caption: 'Import', type: 'import' }
  ];

  allStatus = [
    {
      "caption": "All",
      "name": " ",
      "label": "All",
      "value": " "
    },
    {
      "caption": "Not Started",
      "name": "NotStarted",
      "label": "Not Started",
      "value": "NotStarted"
    },
    {
      "caption": "In Progress",
      "name": "InProgress",
      "label": "In Progress",
      "value": "InProgress"
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
      "label": "All",
      "value": ""
    },
    {
      "caption": "Failed",
      "name": "Failed",
      "label": "Failed",
      "value": "Failed"
    }
  ];

  constructor(private activatedRoute: ActivatedRoute, private config: AppConfig, public router: Router, private _commonService: CommonService, private _location: Location, private _commonListService: CommonListService, public metaconfig: AppMetadata, private activeroute: ActivatedRoute) {

    this.breadcrumbDataFrom = [
      {
        "caption": "Admin",
        "routeurl": "/app/admin/migration-assist"
      },
      {
        "caption": "Migration Assist",
        "routeurl": ""
      }
    ]
    this.cols = [
      //  {field: 'uuid', header: 'UUID'},
      { field: 'name', header: 'Name' },
      { field: 'version', header: 'Version' },
      { field: 'createdBy.ref.name', header: 'Created By' },
      { field: 'createdOn', header: 'Created On' },
      // {field: 'status', header: 'Status'},
    ];
    this.items = [
      {
        label: 'View', icon: 'fa fa-eye', command: (onclick) => {
          this.view(this.rowUUid, this.rowVersion);
        }
      },
      {
        label: 'Export', icon: 'fa fa-file-pdf-o', command: (onclick) => {
          this.export(this.rowUUid, this.rowName)
        }
      },
      {
        label: 'Download Archive', icon: 'fa fa-file-pdf-o', command: (onclick) => {
          this.download(this.row)
        }
      }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.uuid = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    })
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = "";
    this.active = " ";
    this.status = " ";
    this.app = "";
    this.getAllLatestAppli();
    this.getAllLatestUser();
    this.getBaseEntityByCriteria("export");
    this.gridTitle = "Export Details"
  }

  onClickMenu(data) {
    this.row = data;
    this.rowUUid = data.uuid
    this.rowVersion = data.version
    this.rowID = data.id
    this.rowName = data.name

  }

  view(uuid, version) {
    let _moduleUrl = this.metaconfig.getMetadataDefs(this.moduleType)['moduleState']
    this.routerUrl = this.metaconfig.getMetadataDefs(this.moduleType)['detailState']
    this.router.navigate(["./../../" + _moduleUrl + "/" + this.routerUrl, this.moduleType, uuid, version, 'true'], { relativeTo: this.activeroute });
  }

  export(uuid, name) {
    this.exportId = uuid
    this.exportname = name;

    this.okExport()
  }

  okExport() {
    this._commonListService.export(this.exportId, this.moduleType)
      .subscribe(
      response => 
      {
        var jsonobj = JSON.stringify(response);
        const filename = this.exportname + '.json';
        const blob = new Blob([jsonobj], { type: 'application/json;charset=utf-8' });
        saveAs(blob, filename);
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Downloaded Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
 
  download(rowDownload) {
    this.uuidAPI = rowDownload.uuid;
   
    alert(this.uuidAPI)
    this._commonService.downloadExport(this.uuidAPI)
    .subscribe(
    response => { this.onSucessdownloadExport(response) 
  
      // var jsonobj = JSON.stringify(this.row);
      // const filename = this.uuidAPI + '.zip';
      // const blob = new Blob([jsonobj], { type: 'application/json;charset=utf-8' });
      // saveAs(blob, filename);
      // this.msgs = [];
      // this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Downloaded Successfully' });
    },
      
    error => console.log("Error :: error found" + error)
    )
  }
 
  onSucessdownloadExport(response) {
    console.log('api call success')
    console.log(response.headers)
   // headers = headers();


    // let filename = 's.z'//this.headers['filename'];
    // let contentType ="application/json"// this.headers['content-type'];
    // let linkElement = document.createElement('a');
    // try {
    //   let blob = new Blob([this.row], {
    //     type: contentType
    //   });
    //   let url = window.URL.createObjectURL(blob);

    //   linkElement.setAttribute('href', url);
    //   linkElement.setAttribute("download", this.uuidAPI + ".zip");

    //   let clickEvent = new MouseEvent("click", {
    //     "view": window,
    //     "bubbles": true,
    //     "cancelable": false
    //   });
    //   linkElement.dispatchEvent(clickEvent);
    // }catch (ex) {
    //   console.log(ex);
    // }
  }


  add() {
    this.router.navigate(["./", this.moduleType], { relativeTo: this.activeroute });
  }

  getAllLatestAppli() {
    this._commonService.getAllLatest('export')
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

  clear() {
    this.active = " ";
    this.status = " ";
    this.rowData1 = null;
    // this.appname="";
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = ""
    // this.appnameUuid;
    // this.allAppliName = [];
    //  this.allName = {}
    this.app = "";
    //  this.app.uuid = " ";
    //    this.type={}
    // this.type.label=" "
  }

  onTabChange(event) {
    if (event.index == "0") {
      this.clear();
      this.moduleType = "export"
      this.cols = [
        // {field: 'appName', header: 'Name'},
        // {field: 'type', header: 'Meta'},
        { field: 'name', header: 'Name' },
        { field: 'version', header: 'Version' },
        { field: 'createdBy.ref.name', header: 'Created By' },
        { field: 'createdOn', header: 'Created On' },
        //{field: 'status', header: 'Status'},        
      ];
      this.getBaseEntityByCriteria(this.moduleType);
      this.gridTitle = "Export Details"
      console.log('hello1')
    }
    else if (event.index == "1") {

      this.clear()
      this.moduleType = "import"
      this.cols = [
        //{field: 'appName', header: 'Name'},
        //{field: 'type', header: 'Meta'},
        { field: 'app', header: 'Name' },
        { field: 'version', header: 'Version' },
        { field: 'createdBy.ref.name', header: 'Created By' },
        { field: 'createdOn', header: 'Created On' },
        //{field: 'status', header: 'Status'},
      ];
      this.getBaseEntityByCriteria(this.moduleType);
      this.gridTitle = "Import Details"
      console.log('hello2')
    }
    else {
      this.clear()
    }
  }

  getBaseEntityByCriteria(tabtype): void {
    
    this._commonListService.getBaseEntityByCriteria(((tabtype).toLowerCase()), this.app, this.startDate, this.tags, this.username, this.endDate, this.active, this.status)
      .subscribe(
      response => {
        this.onSuccessgetBaseEntityByCriteria(response)
        //console.log(JSON.stringify(response))
      },
      error => console.log("Error :: " + error)
      )
  }

  onSuccessgetBaseEntityByCriteria(response) {
    this.rowData1 = response;
   // console.log(JSON.stringify(this.rowData1));
  }
}
