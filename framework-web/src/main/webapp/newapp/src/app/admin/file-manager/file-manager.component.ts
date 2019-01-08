import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { DatePipe } from '@angular/common';
import { CommonService } from '../../metadata/services/common.service';
import { CommonListService } from '../../common-list/common-list.service';
import { AppHepler } from '../../app.helper';
import { FileManagerService } from '../../metadata/services/fileManager.service';
import { AppConfig } from '../../app.config';
import { ResponseContentType, Http, Headers } from '@angular/http';
import { saveAs } from 'file-saver/FileSaver';
import { Location } from '@angular/common';

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html'
})
export class FileManagerComponent implements OnInit {
  msgs: any[];
  baseUrl: any;
  fileData: FormData;
  fileName: any;
  fd: FormData;
  dataSource: any;
  dialogBoxVisible: boolean = false;
  items: any[];
  rowData1: any;
  _RowData: any;
  columnDefs: any;
  rowName: any;
  rowID: any;
  rowVersion: any;
  rowUUid: any;
  username: any;
  tags: any;
  status: any;
  active: any;
  endDate: any;
  startDate: any;
  allUserName: any[];
  allExecName: any[];
  execname: any;
  allStatus: any[];
  uploadfileName: any;
  dataSourceArray: any;
  breadcrumbDataFrom : any[];
  constructor(private _location: Location, private activatedRoute: ActivatedRoute, private http: Http, private config: AppConfig, public router: Router, public metaconfig: AppMetadata, public apphelper: AppHepler, private datePipe: DatePipe, private activeroute: ActivatedRoute, private _commonService: CommonService, private _commonListService: CommonListService, private _fileManagerService: FileManagerService) {
    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/admin/fileManager"
    },
    {
      "caption": "File Manager",
      "routeurl": "/app/admin/fileManager"
    }
    ];
    this.active = " ";
    this.status =null;
    this.rowData1 = null;
    this.execname = {};
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = {};
    this.baseUrl = config.getBaseUrl();
    this.allStatus = [
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
  }

  ngOnInit() {
    this.getBaseEntityByCriteria();
    this.getAllLatest();
    this.getAllLatestUser()
  }

  getBaseEntityByCriteria() {
    this._commonListService.getBaseEntityByCriteria("uploadexec", "", "", "", "", "", "", "")
      .subscribe(
      response => { this.getGrid(response) },
      error => console.log("Error :: " + error)
      )
  }

  getAllLatest(): void {
    this._commonListService.getAllLatest("uploadexec")
      .subscribe(
      response => { this.OnSucessgetAllLatest(response) },
      error => console.log("Error :: " + error)
      )
  }
  OnSucessgetAllLatest(response) {
    this.allExecName = []
    let temp = []
    for (const i in response) {
      let allName = {};
      allName["label"] = response[i]['name'];
      allName["value"] = {};
      allName["value"]["label"] = response[i]['name'];
      allName["value"]["uuid"] = response[i]['uuid'];
      temp[i] = allName;
    }
    this.allExecName = temp

    this.allExecName.splice(0, 0, {
      'label': 'Select',
      'value': ''
    });
  }
  getAllLatestUser(): void {
    this._commonListService.getAllLatest('user')
      .subscribe(
      response => { this.OnSucessgetAllLatestUser(response) },
      error => console.log("Error :: " + error)
      )
  }
  OnSucessgetAllLatestUser(response) {
    this.allUserName = []
    let temp = []
    for (const i in response) {
      let allName = {};
      allName["label"] = response[i]['name'];
      allName["value"] = {};
      allName["value"]["label"] = response[i]['name'];
      allName["value"]["uuid"] = response[i]['uuid'];
      temp[i] = allName;
    }
    this.allUserName = temp

    this.allUserName.splice(0, 0, {
      'label': 'Select',
      'value': ''
    });
  }

  onSearchCriteria(form) {
    let startDateUtcStr = "";
    let endDateUtcStr = "";
    console.log(this.execname.label);
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
    console.log(endDateUtcStr);
    console.log(startDateUtcStr);
    console.log(this.tags);
    console.log(this.username.label);
    console.log(this.execname.label);
    console.log(this.active);
    this._commonListService.getBaseEntityByCriteria("uploadexec", this.execname.label || "", startDateUtcStr, this.tags, this.username.label, endDateUtcStr, this.active, this.status)
      .subscribe(
      response => { this.getGrid(response) },
      error => console.log("Error :: " + error)
      )
  }

  onClickMenu(data) {
    this.rowUUid = data.uuid
    this.rowVersion = data.version
    this.rowID = data.id
    this.rowName = data.name
  }

  getGrid(response) {
    this.items = [
      {
        label: 'download', icon: 'fa fa-download', command: (onclick) => {
          this.download(this.rowUUid)
        }
      }
    ]
    for (let i = 0; i < response.length; i++) {
      if (response[i]["status"] != null) {
        response[i]["status"] = this.apphelper.sortByProperty(response[i]["status"], "createdOn");
        let status = response[i]["status"];
        response[i]["status"] = {};
        response[i]["status"].stage = this.apphelper.getStatus(status)["stage"];
        response[i]["status"].color = this.metaconfig.getStatusDefs(response[i]["status"].stage)['color'];
      }
    }
    this._RowData = response;
    this.rowData1 = this._RowData;
  }
  formatPublish(data) {
    if (data == "Y") {
      return "Yes"
    }
    else {
      return "No"
    }
  }

  upload() {
    this.dialogBoxVisible = true;
    this._fileManagerService.getDatasourceByType("FILE").subscribe(
      response => { this.onSuccessgetDatasourceByType(response) },
      error => { console.log("Error ::", +error) }
    )
  }

  onSuccessgetDatasourceByType(response) {
    this.dataSourceArray = [];
    for (const i in response) {
      let dataSourceObj = {};
      dataSourceObj["label"] = response[i].name;
      dataSourceObj["value"] = {};
      dataSourceObj["value"]["label"] = response[i].name;
      dataSourceObj["value"]["uuid"] = response[i].uuid;
      this.dataSourceArray[i] = dataSourceObj;
    }
  }

  cancelDialogBox() {
    this.dialogBoxVisible = false;
  }

  fileChange(files: any) {
    var f = files[0];
    console.log(f);
    this.fileName = f.name
    var fd = new FormData();
    fd.append('file', f);
    console.log(fd);
    this.fileData = fd;
  }

  submitDialogBox() {
    this._fileManagerService.uploadFile(this.fileData, this.fileName, null, null, null, "csv", this.dataSource.uuid).subscribe(
      response => {debugger
        this.getBaseEntityByCriteria();
        if (response["code"] !== 404) {
          this.msgs = [];
          this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'file uploaded Successfully' });
          setTimeout(() => { this.goBack() }, 1000);
        }
        else if(response["code"] == 404){
          this.msgs = [];
          this.msgs.push({ severity: 'info', summary: 'Failed Message', detail: 'file already uploaded' });
          setTimeout(() => { this.goBack() }, 1000);
        }
      },
      error => { console.log("Error ::",error) }
    )
    this.dialogBoxVisible = false;
  }

  download(rowUUid) {
    const headers = new Headers();
    this.fileName = this.rowName;
    this._fileManagerService.download(rowUUid, "downloadexec", "csv", this.fileName)
      .toPromise()
      .then(response => this.onSucessdownloadExport(response));
  }

  onSucessdownloadExport(response) {
    debugger
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    console.log(contentDispositionHeader);
    // const filename= response.headers.get('filename') + ".csv";
    const parts: string[] = contentDispositionHeader.split(';');
    const blob = new Blob([response._body], { type: parts[0] });
    saveAs(blob, this.fileName + ".csv");
  }

  goBack() {
    this.router.navigate(['/app/admin/fileManager']);
    //this._location.back();
  }
}
