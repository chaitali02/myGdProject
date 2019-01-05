import { OnInit, Component, ViewEncapsulation, ElementRef, Renderer, NgZone, ViewChild, Input } from "@angular/core";

import { Router, ActivatedRoute, Params } from '@angular/router';
import { GridOptions, GridApi } from "ag-grid/main";
import { DomSanitizer } from '@angular/platform-browser';
import { MenuModule, MenuItem } from 'primeng/primeng';

import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { ICellRendererAngularComp } from "ag-grid-angular";
import { FormGroup, FormBuilder } from "@angular/forms";
import { SelectItem } from 'primeng/components/common/api';
import { Message } from 'primeng/components/common/api';
import { MessageService } from 'primeng/components/common/messageservice';
import { saveAs } from 'file-saver/FileSaver';
import { DatapodService } from '../../metadata/services/datapod.service';
import { AppConfig } from '../../app.config';
import { SettingsService } from '../../metadata/services/settings.service';
import { CommonService } from "../../metadata/services/common.service";
import { DatePipe } from '@angular/common';
import { CommonList } from './common-list';
import { CommonListService } from "../../common-list/common-list.service";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.template.html',
  styleUrls: ['./settings.component.css'],
  providers: [DatePipe]
})
export class SettingsComponent implements OnInit {
  isPublish: boolean;
  isActive: boolean;
  showRestore: boolean;
  serverStatus: string;
  statusView: boolean;

  rowUUid: any;
  startDate: any;
  execname: any;
  isDataLodingBG: boolean;
  graphView: boolean;
  ShowView: boolean = true;
  _RowData: CommonList[];
  published: string = ''
  private id: any;
  private version: any;
  private mode: any;
  private breadcrumbDataFrom: any;
  private cols: any;
  private title: any;
  private type: "setting"
  private action: "view"
  private generalSetting: any[]
  private metaEngine: any[]
  private ruleEngine: any[]
  private propertyName: any
  private propertyValue: any
  private tableData: any
  private selectallattribute: boolean;
  parentType: any;
  selectAllAttributeRow: any;
  paramtableArray: any[];
  paramType: { "label": string; "value": string; }[];
  paramsetArrayTable: any;
  paramTableCol2: any;
  selectParamlistName: {};
  paramsetData: any[];
  paramlistData: any[];
  selectParamsetName: {};
  selectParamType: string;
  displayDialogBox: boolean = false;
  attributeTypes: any[];
  paramListHolder: any[];
  displayDialogBoxSimulation: boolean = false;
  imagePath: string;
  fileData: any;
  file: File;
  showUploadModel: boolean;
  uploadName: any;
  UploadId: any;
  rowStatus: any;
  killStatus: any;
  restartVersion: any;
  restartId: any;
  killVersion: any;
  killId: any;
  DagExec: boolean;
  showDelete: boolean;
  showActive: boolean;
  Exec: boolean;
  rowName: any;
  rowID: any;
  rowVersion: any;
  showExecute: any;
  isExecutable: any;
  isJobExec: boolean;
  newDataList: any[];
  isParamModel: string;
  paramsetdata: any;
  allparamset: SelectItem[] = [];
  //allparamset: [{label}];
  isTabelShow: boolean;
  paramtable: any;
  paramtablecol: any;
  @ViewChild('ParamsModel') ParamsModel: ElementRef;
  @ViewChild('fileupload') fileupload: ElementRef;
  executeVersion: any;
  executeId: any;
  exportname: any;
  exportId: any;
  cloneVersion: any;
  cloneId: any;
  publishId: any;
  unpublishId: any;
  restoreId: any;
  deleteId: any;
  isModel: string;
  routerUrl: any;
  ModelDataFrom: { "caption": string; "message": any; "functionName": any };
  status: string;
  active: string;
  selectedType: any;
  isExec: any;
  columnDefs: any[];
  rowData1: any[];
  rowData: any;
  tags: any;
  endDate: any;
  username: any;
  allExecName: any;
  allUserName: any;
  form: any;
  gridTitle: any;
  items: any;
  typeSimple: string[];
  nonExecTypes: any = ['datapod', 'dataset', 'expression', 'filter', 'formula', 'function', 'load', 'relation', 'algorithm', 'distribution', 'paramlist', 'paramset', 'training', 'prediction', 'operator', 'activity', 'application', 'datasource', 'datastore', 'group', 'privilege', 'role', 'session', 'user', 'vizpod', 'dashboard', 'profileexec', 'profilegroupexec', 'ruleexec', 'rulegroupexec', 'dqexec', 'dqgroupexec', 'dagexec', 'mapexec', 'loadexec', 'vizexec', 'trainexec', 'predictexec', 'simulateexec', 'downloadexec', 'uploadexec'];
  // nonExecTypes:any = ['datapod','dataset','expression','filter','formula','function','load','relation','algorithm','paramlist','paramset','training','activity','application','datasource','datastore','group','privilege','role','session','user','vizpod','dashboard','profileexec','profilegroupexec','ruleexec','rulegroupexec','dqexec','dqgroupexec','dagexec','mapexec','loadexec','vizexec','trainexec'];
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
      "value": "Y"
    },
    {
      "caption": "Expired",
      "name": "N",
      "label": "Expired",
      "value": "N"
    }
  ];
  types: { "value": string; "caption": string; }[];
  isSubmit: any;
  msgs: any;

  tabs = [
    { caption: 'General', title: 'General Settings' },
    { caption: 'Meta Engine', title: 'Meta Engine Settings' },
    { caption: 'Rule Engine', title: 'Rule Engine Settings' },
    { caption: 'Graph Engine', title: 'Graph Engine Settings' },
    { caption: 'Application Engine', title: 'Application Engine Settings' },
    { caption: 'Prediction Engine', title: 'Prediction Engine Settings' }
  ];
  constructor(private _datapodService: DatapodService, private datePipe: DatePipe, config: AppConfig, private _commonService: CommonService, private activatedRoute: ActivatedRoute, public router: Router, private _settingsService: SettingsService, private _commonListService: CommonListService) {
    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": '/app/admin/settings'
    },
    {
      "caption": "Settings",
      "routeurl": null
    },
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id']
      this.version = params['version']
      this.mode = params['mode']
      this.get();
      this.selectallattribute = false
    })
    this.execname = {};
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = "";
    this.type = "setting";

    this.statusView = false;
    this.serverStatus = "Not Running";
  }

  public goBack() {
    //this._location.back(); 
    this.router.navigate(['app/DataDiscovery']);
  }
  get() {
    this._settingsService.get("setting", "view")
      .subscribe(
      response => {
        this.onSuccessgetData(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetData(response) {
    this.generalSetting = response['generalSetting'];
    this.metaEngine = response['metaEngine'];
    this.ruleEngine = response['ruleEngine'];
    this.tableData = this.generalSetting
  }

  onTabChange(event) {
    if (event.index == "0") {
      this.tableData = this.generalSetting
      this.ShowView = true
      this.statusView = false
    }
    else if (event.index == "1") {
      this.tableData = this.metaEngine
      this.ShowView = true
      this.statusView = false
    }
    else if (event.index == "2") {
      this.tableData = this.ruleEngine
      this.ShowView = true
      this.statusView = false
    }
    else if (event.index == "3") {
      //this.tableData = this.metaEngine
      this.ShowView = false
      this.graphView = true
      this.statusView = false
    }
    else if (event.index == "4") {
      //this.tableData = this.ruleEngine
      this.onSearchCriteria()
      this.ShowView = false
      this.graphView = false
      this.statusView = false
    }
    else if (event.index == "5") {
      this.ShowView = false
      this.graphView = false
      this.statusView = true
    }
  }
  addRow() {
    if (this.tableData == null) {
      this.tableData = [];
    }
    var len = this.tableData.length + 1
    var filertable = {};
    filertable["propertyName"] = " ";
    filertable["propertyValue"] = " ";
    this.tableData.splice(this.tableData.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectallattribute = false;
    this.tableData.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.tableData = newDataList;
  }
  checkAllAttributeRow() {
    if (!this.selectallattribute) {
      this.selectallattribute = true;
    }
    else {
      this.selectallattribute = false;
    }
    this.tableData.forEach(attribute => {
      attribute.selected = this.selectallattribute;
    });
  }

  submitSettings() {
    let newSetting = {};
    this.isSubmit = "true";
    newSetting["generalSetting"] = this.generalSetting;
    newSetting["metaEngine"] = this.metaEngine;
    newSetting["ruleEngine"] = this.ruleEngine;
    console.log(JSON.stringify(newSetting));

    this._settingsService.submit("datapod", newSetting).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmit = "false"
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Settings Submitted Successfully' });
    setTimeout(() => {
      //this.goBack()
    }, 1000);
  }
  callBulidGraph() {
    this.isDataLodingBG = true;
    this._settingsService.buildGraph().subscribe(
      response => { this.onSuccesscallBulidGraph(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesscallBulidGraph = function (response) {
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Graph Build Successfully' });
    this.isDataLodingBG = false;
  }
  onSearchCriteria() {
    let startDateUtcStr = "";
    let endDateUtcStr = "";
    if (this.startDate != "") {
      let startDateUtc = new Date(this.startDate.getUTCFullYear(), this.startDate.getUTCMonth(), this.startDate.getUTCDate(), this.startDate.getUTCHours(), this.startDate.getUTCMinutes(), this.startDate.getUTCSeconds())
      startDateUtcStr = this.datePipe.transform(startDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//startDateUtc.toString().split("GMT")[0]+"UTC";
    }
    if (this.endDate != "") {
      let endDateUtc = new Date(this.endDate.getUTCFullYear(), this.endDate.getUTCMonth(), this.endDate.getUTCDate(), this.endDate.getUTCHours(), this.endDate.getUTCMinutes(), this.endDate.getUTCSeconds())
      endDateUtcStr = this.datePipe.transform(endDateUtc, "EEE MMM dd hh:mm:ss yyyy").toString() + " UTC"//endDateUtc.toString().split("GMT")[0]+"UTC";
    }
    this._settingsService.getBaseEntityByCriteria('appconfig', this.execname.label || "", startDateUtcStr, this.tags, this.username.label || '', endDateUtcStr, this.active || '', this.status, this.published)
      .subscribe(
      response => { this.getGrid(response) },
      error => console.log("Error :: " + error)
      )

  }
  onClickMenu(data) {
    console.log(data);
    this.rowUUid = data.uuid;
    this.rowVersion = data.version;
    this.rowName = data.name;
    this.rowID = data.id;
    if (data.active == 'Y') {
      this.isActive = true;
    }
    else {
      this.isActive = false;
    }

    if (data.publish == 'Y') {
      this.isPublish = true;
    }
    else {
      this.isPublish = false;
    }
  }

  getGrid(response) {
    this.items = [
      {
        label: 'View', icon: 'fa fa-eye', command: (onclick) => {
          this.view(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Edit', icon: 'fa fa-pencil-square-o', command: (onclick) => {
          this.edit(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Delete', icon: 'fa fa-times', visible: (this.isActive), command: (onclick) => {
          this.delete(this.rowID)
        }
      },
      {
        label: 'Restore', icon: 'fa fa-retweet', visible: (this.isActive), command: (onclick) => {
          this.restore(this.rowID)
        }
      },
      {
        label: 'Publish', icon: 'fa fa-share-alt', visible: (this.isPublish), command: (onclick) => {
          this.publish(this.rowID)
        }
      },
      {
        label: 'Unpublish', icon: 'fa fa-shield', visible: (this.isPublish), command: (onclick) => {
          this.unpublish(this.rowID)
        }
      },
      {
        label: 'Clone', icon: 'fa fa-clone', command: (onclick) => {
          this.clone(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Export', icon: 'fa fa-file-pdf-o', command: (onclick) => {
          this.export(this.rowUUid, this.rowName)
        }
      },
    ]
    this.isExec = "false";
    this._RowData = response;
    this.rowData1 = this._RowData;
    this.getAllLatest();
    this.getAllLatestUser();
  }
  getAllLatest(): void {
    this._commonService.getAllLatest('appconfig')
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
    this._commonService.getAllLatest('user').subscribe(
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
  formatActive(data) {
    if (data == "Y") {
      if (((this.type).toLowerCase()).indexOf("exec") == -1) {
        this.items[2].visible = true
        this.items[3].visible = false
        this.items[6].disabled = false
      }
      return "Active"
    }
    else {
      if (((this.type).toLowerCase()).indexOf("exec") == -1) {
        this.items[2].visible = false
        this.items[3].visible = true
        this.items[6].disabled = true
      }
      return "Inactive"
    }
  }
  formatPublish(data) {
    if (data == "Y") {
      if (((this.type).toLowerCase()).indexOf("exec") == -1) {
        this.items[4].visible = false
        this.items[5].visible = true
      }
      return "Yes"
    }
    else {
      if (((this.type).toLowerCase()).indexOf("exec") == -1) {
        this.items[4].visible = true
        this.items[5].visible = false
      }
      return "No"
    }
  }
  view(rowUUid, rowVersion) {
    this.router.navigate([rowUUid, rowVersion, 'true'], { relativeTo: this.activatedRoute });
  }
  edit(rowUUid, rowVersion) {
    this.router.navigate([rowUUid, rowVersion, 'false'], { relativeTo: this.activatedRoute });
  }
  delete(rowID) {
    this._commonListService.delete(rowID, 'appconfig')
      .subscribe(
      response => {
        this.onSearchCriteria();
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Deleted Successfully' });
      },
      error => console.log("Error :: " + error))
  }
  restore(rowID) {
    this._commonListService.restore(rowID, 'appconfig')
      .subscribe(
      response => {
        this.onSearchCriteria();
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Restored Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  publish(rowID) {
    this._commonListService.publish(rowID, 'appconfig')
      .subscribe(
      response => {
        this.onSearchCriteria();
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Published Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  unpublish(rowID) {
    this._commonListService.unpublish(rowID, 'appconfig')
      .subscribe(
      response => {
        this.onSearchCriteria();
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Unpublished Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  clone(rowUUid, rowVersion) {
    this._commonListService.clone(rowUUid, rowVersion, 'appconfig')
      .subscribe(
      response => {
        this.onSearchCriteria()
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Cloned Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  export(rowUUid, rowName) {
    this._commonListService.export(rowUUid, 'appconfig')
      .subscribe(
      response => {
        var jsonobj = JSON.stringify(response);
        const filename = rowName + '.json';
        const blob = new Blob([jsonobj], { type: 'application/json;charset=utf-8' });
        saveAs(blob, filename);
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'AppConfig Downloaded Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
}
