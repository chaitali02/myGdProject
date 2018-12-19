import { Component, ViewEncapsulation, ElementRef, Renderer, NgZone, ViewChild, Input, } from "@angular/core";
import { DatePipe } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { GridOptions, GridApi } from "ag-grid/main";
import { DomSanitizer } from '@angular/platform-browser';
import { MenuModule, MenuItem } from 'primeng/primeng';
import { CommonListService } from './common-list.service';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { ICellRendererAngularComp } from "ag-grid-angular";
import { FormGroup, FormBuilder } from "@angular/forms";
import { SelectItem } from 'primeng/components/common/api';
import { Message } from 'primeng/components/common/api';
import { MessageService } from 'primeng/components/common/messageservice';
import { saveAs } from 'file-saver/FileSaver';


import { CommonList } from './common-list';
import { AppMetadata } from '../app.metadata';
import { AppHepler } from '../app.helper';
import { CommonService } from "../metadata/services/common.service";
import { DependsOn } from '../common-list/dependsOn';

declare var jQuery: any;
interface FileReaderEventTarget extends EventTarget {
  result: string
}
@Component({
  selector: 'app-common-list-application',
  templateUrl: './common-list.component.html',
  styleUrls: ['./common-list.css'],
  providers: [DatePipe]
})

export class CommonListComponent {
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
  rowUUid: any;
  showExecute: any;
  isExecutable: any;
  isJobExec: boolean;
  selectallattribute: boolean;
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
  breadcrumbDataFrom: { "caption": string; "routeurl": any; }[];
  ModelDataFrom: { "caption": string; "message": any; "functionName": any };
  status: string;
  active: string;
  selectedType: any;
  isExec: any;
  gridOptions: GridOptions;
  gridapi: GridApi;
  columnDefs: any[]
  rowData1: any[];
  rowData: any
  _RowData: CommonList[];
  type: any;
  execname: any;
  startDate: any;
  tags: any;
  endDate: any;
  username: any;
  allExecName: any;
  allUserName: any;
  form: any;
  gridTitle: any
  msgs: Message[] = [];
  items: any
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

  constructor(private sanitizer: DomSanitizer, private http: Http, private _commonListService: CommonListService, private activatedRoute: ActivatedRoute, public router: Router, private fb: FormBuilder, public metaconfig: AppMetadata, public apphelper: AppHepler, private datePipe: DatePipe, private activeroute: ActivatedRoute, private _commonService: CommonService) {

    let temp;
    this.active = " ";
    this.status = " ";
    this.rowData1 = null;
    this.execname = {};
    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = ""
    this.isModel = "false"
    this.isParamModel = "false"
    this.activatedRoute.params.subscribe((params: Params) => {
      if (params['type'].indexOf("dagexec") != -1) {
        this.DagExec = true;
      }
      else {
        this.DagExec = false;
      }
      if (params['type'].indexOf("Exec") != -1) {
        this.isJobExec = true;
      }
      else {
        this.isJobExec = false;
      }
      if (params['type'].indexOf("exec") != -1) {
        this.Exec = true;
      }
      else {
        this.Exec = false;
      }
      this.type = (params['type']).toLowerCase();
      this.parentType = (params['parentType'])
      console.log(this.parentType);
    });
    this.gridTitle = metaconfig.getMetadataDefs((this.type).toLowerCase())['caption']
    //this.routerUrl=metaconfig.getMetadataDefs(this.type)['detailState']
    if (this.type.indexOf("exec") != -1) {
      temp = this.type.split("exec")[0];
      this.selectedType = temp;
    }
    this.attributeTypes = [
      { "label": "datapod", "value": "datapod" },
      { "label": "dataset", "value": "dataset" },
      { "label": "rule", "value": "rule" },
    ];
    this.types = [
      {
        "value": temp,
        "caption": "Rule"
      },
      {
        "value": temp + "group",
        "caption": "Rule Group"
      }
    ];
    this.paramType = [
      { "label": "paramlist", "value": "paramlist" },
      { "label": "paramset", "value": "paramset" }
    ];
    this.selectParamlistName = {};
    this.selectParamlistName["uuid"] = " ";
    this.selectParamsetName = {};

    // this.getBaseEntityByCriteria();

    this.breadcrumbDataFrom = [
      {
        "caption": (metaconfig.getMetadataDefs((this.type))['moduleState']).toUpperCase(),
        "routeurl": ''
      },
      {
        "caption": metaconfig.getMetadataDefs((this.type).toLowerCase())['caption'],
        "routeurl": null
      }
    ]
    this.typeSimple = ["string", "double", "date", "integer", "list"];
    this.gridOptions = <GridOptions>{
      enableFilter: true,
      enableSorting: true,
      enableColResize: true,
      suppressMenuHide: false,
      headerHeight: 40,
      rowSelection: 'single',
      context: {
        componentParent: this
      }
    };
    this.gridOptions.rowHeight = 40;
    this.columnDefs = [
      { headerName: "UUID", field: "uuid", width: 400 },
      { headerName: "Name", field: "name" },
      { headerName: "Version", field: "version", width: 200 },
      { headerName: "Created By", field: "createdBy.ref.name" },
      {
        headerName: "Created On", field: "createdOn", width: 300, filter: 'date', filterParams: {
          comparator: function (filterLocalDateAtMidnight, cellValue) {
            var dateAsString = cellValue;
            var dateParts = dateAsString.split("/");
            var cellDate = new Date(Number(dateParts[2]), Number(dateParts[1]) - 1, Number(dateParts[0]));

            if (filterLocalDateAtMidnight.getTime() == cellDate.getTime()) {
              return 0
            }

            if (cellDate < filterLocalDateAtMidnight) {
              return -1;
            }

            if (cellDate > filterLocalDateAtMidnight) {
              return 1;
            }
          }
        }
      },
    ];
  }
  refershGrid() {
    this.active = "";
    this.status = "";
    this.rowData1 = null;
    this.execname = {};

    this.startDate = "";
    this.tags = "";
    this.endDate = "";
    this.username = ""
    this.getBaseEntityByCriteria();
  }

  refreshSearchResults() {
    this.rowData1 = null;
    this.getBaseEntityByCriteria();
  }

  ngOnInit() {
    let temp;
    this.activatedRoute.params.subscribe((params: Params) => {
      this.parentType = params['parentType'];
      this.type = params['type'];
      this.getBaseEntityByCriteria();
      this.rowData1 = null;
      this.gridTitle = this.metaconfig.getMetadataDefs((this.type).toLowerCase())['caption']
      if (this.type.indexOf("exec") != -1) {
        temp = this.type.split("exec")[0];
        this.selectedType = temp;
      }
      this.types = [
        {
          "value": temp,
          "caption": "Rule"
        },
        {
          "value": temp + "group",
          "caption": "Rule Group"
        }
      ];
      if (this.type == "paramlist") {
        if (this.parentType == "rule") {
          this.breadcrumbDataFrom = [
            {
              "caption": "BusinessRules",
              "routeurl": "./"
            },
            {
              "caption": this.metaconfig.getMetadataDefs((this.type).toLowerCase())['caption'],
              "routeurl": null
            }]
        } else if (this.parentType == "model") {
          this.breadcrumbDataFrom = [
            {
              "caption": "DataScience",
              "routeurl": "./"
            },
            {
              "caption": this.metaconfig.getMetadataDefs((this.type).toLowerCase())['caption'],
              "routeurl": null
            }]
        }
      }
      else {
        this.breadcrumbDataFrom = [
          {
            "caption": this.metaconfig.getMetadataDefs((this.type))['moduleCaption'],
            "routeurl": "./"
          },
          {
            "caption": this.metaconfig.getMetadataDefs((this.type).toLowerCase())['caption'],
            "routeurl": null
          }]
      }
    });
  }

  add() {
    if (this.parentType == "rule") {
      this.router.navigate(["../../../businessRules/paramlist", this.parentType], { relativeTo: this.activeroute });
    }
    else if (this.parentType == "model") {
      this.router.navigate(["../../../dataScience/paramlist", this.parentType], { relativeTo: this.activeroute });
    }
    else if (this.type != "activity" && this.parentType !== "rule") {
      let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
      this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['detailState']
      this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl], { relativeTo: this.activeroute });
    }
  }
  view(uuid, version) {
    if (this.parentType == "rule") {
      this.router.navigate(["../../../businessRules/paramlist", this.parentType, uuid, version, 'true'], { relativeTo: this.activeroute });
    }
    else if (this.parentType == "model") {
      this.router.navigate(["../../../dataScience/paramlist", this.parentType, uuid, version, 'true'], { relativeTo: this.activeroute });
    }
    else if (this.type == "dashboard") {
      let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
      this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['graphState']
      this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl, uuid, version, 'true'], { relativeTo: this.activeroute });
    }
    else if (this.type == "dagexec" || this.type == "profileexec" || this.type == "profilegroupexec" || this.type == 'ruleexec' || this.type == 'rulegroupexec' || this.type == 'dqexec' || this.type == "dqgroupexec" || this.type == "trainexec" || this.type == "recongroupexec" || this.type == "reconexec") {

      let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
      this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['resultState']
      this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl, uuid, version, this.type, 'true'], { relativeTo: this.activeroute });
    }
    else {
      let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
      this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['detailState']
      this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl, uuid, version, 'true'], { relativeTo: this.activeroute });
    }
  }
  edit(uuid, version) {
    if (this.parentType == "rule") {
      this.router.navigate(["../../../businessRules/paramlist", this.parentType, uuid, version, 'false'], { relativeTo: this.activeroute });
    }
    else if (this.parentType == "model") {
      this.router.navigate(["../../../businessRules/paramlist", this.parentType, uuid, version, 'false'], { relativeTo: this.activeroute });
    }
    else {
      let _moduleUrl = this.metaconfig.getMetadataDefs(this.type)['moduleState']
      this.routerUrl = this.metaconfig.getMetadataDefs(this.type)['detailState']
      this.router.navigate(["./" + _moduleUrl + "/" + this.routerUrl, uuid, version, 'false'], { relativeTo: this.activeroute });
    }
  }
  delete(id) {
    this.deleteId = id
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Delete",
      "functionName": "okDelete()"
    }
  }
  restore(id) {
    this.restoreId = id
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Restore",
      "functionName": "okRestore()"
    }
  }
  publish(id) {
    this.publishId = id
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Publish",
      "functionName": "okPublish()"
    }
  }
  unpublish(id) {
    this.unpublishId = id
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Unpublish",
      "functionName": "okUnpublish()"
    }

  }
  clone(uuid, version) {
    this.cloneId = uuid
    this.cloneVersion = version
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Clone",
      "functionName": "okClone()"
    }
  }
  export(uuid, name) {
    this.exportId = uuid
    this.exportname = name;
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Export",
      "functionName": "okExport()"
    }
  }
  execute(uuid, version) {
    this.executeId = uuid
    this.executeVersion = version;
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Execute",
      "functionName": "okExecute()"
    }
  }
  kill(uuid, version, status) {
    this.killId = uuid
    this.killVersion = version;
    this.killStatus = status
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Kill",
      "functionName": "okKill()"
    }
  }
  restart(uuid, version) {
    this.restartId = uuid
    this.restartVersion = version;
    this.isModel = "true"
    this.ModelDataFrom = {
      "caption": this.metaconfig.getMetadataDefs(this.type)['caption'],
      "message": "Restart",
      "functionName": "okRestart()"
    }
  }
  upload(uuid, name) {
    this.UploadId = uuid;
    this.uploadName = name;
    jQuery(this.fileupload.nativeElement).modal('show');
    // this.ModelDataFrom={
    // "caption":this.metaconfig.getMetadataDefs(this.type)['caption'],
    // "message":"Upload",
    // "functionName":"okRestart()"
    // }
  }
  readURL(event) {
    var files = event.srcElement.files;
    this.fileData = files;
    if (files && files[0]) {
      var reader = new FileReader();
      reader.onload = function (e: any) {
        jQuery('#avatar-preview')
          .attr('src', e.target.result)
          .show();
      };
      reader.readAsDataURL(files[0]);
    }
  }
  uploadFile(file) {
    var f = file[0];
    console.log(f);
    var type = f.type.split('/')[1];
    console.log(type);
    this.imagePath = '';
    var fd = new FormData();
    fd.append('file', f);
    fd.append('fileName', this.UploadId);
    console.log(fd);

    this._commonListService.uploadFile(fd, fd["name"], "datapod")
      .subscribe(
      response => {
        // alert("hii")
        jQuery(this.fileupload.nativeElement).modal('hide');
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Datapod Uploaded Successfully' });
        //       setTimeout(()=>{    //<<<---    using ()=> syntax
        //         this.call()
        //    },10000);
      })
  }
  close() {
    jQuery(this.fileupload.nativeElement).modal('hide');
  }
  ok(data) {
    if (data == "okDelete()") {
      this.okDelete();
    }
    if (data == "okRestore()") {
      this.okRestore();
    }
    if (data == "okPublish()") {
      this.okPublish();
    }
    if (data == "okUnpublish()") {
      this.okUnpublish();
    }
    if (data == "okClone()") {
      this.okClone();
    }
    if (data == "cancel") {
      this.isModel = "false"
    }
    if (data == "okExport()") {
      this.okExport();
    }
    if (data == "okExecute()") {

      if (this.type == "rule") {
        this.getParams()
      } else if (this.type == "train") {
        this.getParamListORParamset();
      }
      else if (this.type == "simulate") {
        this.getExecParamList();
      }
      else {
        this.okExecute();
      }
    }
    if (data == "okKill()") {
      this.okKill();
    }
    if (data == "okRestart()") {
      this.okRestart();
    }
  }

  okDelete() {
    this._commonListService.delete(this.deleteId, this.type)
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Deleted Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  okRestore() {
    this._commonListService.restore(this.restoreId, this.type)
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Restored Successfully' });
      },
      error => console.log("Error :: " + error)
      )

  }
  okPublish() {
    this._commonListService.publish(this.publishId, this.type)
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Published Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  okUnpublish() {
    this._commonListService.unpublish(this.unpublishId, this.type)
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Unpublished Successfully' });
      },
      error => console.log("Error :: " + error)
      )

  }
  okClone() {
    this._commonListService.clone(this.cloneId, this.cloneVersion, this.type)
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Cloned Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
  okExport() {
    this._commonListService.export(this.exportId, this.type)
      .subscribe(
      response => {
        var jsonobj = JSON.stringify(response);
        const filename = this.exportname + '.json';
        const blob = new Blob([jsonobj], { type: 'application/json;charset=utf-8' });
        saveAs(blob, filename);
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Downloaded Successfully' });
      },
      error => console.log("Error :: " + error)
      )

  }
  okExecute() {
    this._commonListService.execute(this.executeId, this.executeVersion, this.type, "execute")
      .subscribe(
      response => {
        //this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Executed Successfully' });

        setTimeout(() => {

        }, 1000);
      },
      error => console.log("Error :: " + error)
      )

  }
  okRestart() {
    let type = this.type.split("exec")[0]
    this._commonListService.restart(this.restartId, this.restartVersion, type, "execute")
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Restarted Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }

  okKill() {
    let type = this.type.split("exec")[0]
    this._commonListService.kill(this.killId, this.killVersion, type, "Killed")
      .subscribe(
      response => {
        this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Killed Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }

  getBaseEntityByCriteria(): void {
    if (this.parentType == "rule") {
      this._commonListService.getParamListByRule(((this.type).toLowerCase()), "", "", "", "", "", "", "")
        .subscribe(
        response => { this.getGrid(response) },
        error => console.log("Error :: " + error)
        )
    }
    else if (this.parentType == "model") {
      this._commonListService.getParamListByModel(((this.type).toLowerCase()), "", "", "", "", "", "", "")
        .subscribe(
        response => { this.getGrid(response) },
        error => console.log("Error :: " + error)
        )
    }
    else {
      this._commonListService.getBaseEntityByCriteria(((this.type).toLowerCase()), "", "", "", "", "", "", "")
        .subscribe(
        response => { this.getGrid(response) },
        error => console.log("Error :: " + error)
        )
    }
  }
  getGrid(response) {
    this.isExecutable = this.nonExecTypes.indexOf((this.type).toLowerCase());
    if (this.isExecutable != -1) {
      this.showExecute = false
    }
    else {
      this.showExecute = true
    }
    if (((this.type).toLowerCase()).indexOf("exec") != -1) {
      this.Exec = false;
    }
    else {
      this.Exec = true;
    }
    this.items = [
      {
        label: 'View', icon: 'fa fa-eye', command: (onclick) => {
          this.view(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Edit', icon: 'fa fa-pencil-square-o', visible: (this.Exec), disabled: (this.type == "session" || this.type == "load"), command: (onclick) => {
          this.edit(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Delete', icon: 'fa fa-times', visible: (this.Exec), disabled: (this.type == "session"), command: (onclick) => {
          this.delete(this.rowID)
        }
      },
      {
        label: 'Restore', icon: 'fa fa-retweet', visible: (this.Exec), disabled: (this.type == "session"), command: (onclick) => {
          this.restore(this.rowID)
        }
      },
      {
        label: 'Publish', icon: 'fa fa-share-alt', visible: (this.Exec), disabled: (this.type == "session"), command: (onclick) => {
          this.publish(this.rowID)
        }
      },
      {
        label: 'Unpublish', icon: 'fa fa-shield', visible: (this.Exec), disabled: (this.type == "session"), command: (onclick) => {
          this.unpublish(this.rowID)
        }
      },
      {
        label: 'Execute', icon: 'fa fa-tasks', visible: (this.showExecute), command: (onclick) => {
          this.execute(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Clone', icon: 'fa fa-clone', visible: (this.Exec), disabled: (this.type == "session"), command: (onclick) => {
          this.clone(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Export', icon: 'fa fa-file-pdf-o', visible: (this.Exec || this.isJobExec), command: (onclick) => {
          this.export(this.rowUUid, this.rowName)
        }
      },
      {
        label: 'Kill', icon: 'fa fa-times', visible: (!this.Exec && !this.isJobExec), command: (onclick) => {
          this.kill(this.rowUUid, this.rowVersion, this.rowStatus)
        }
      },
      {
        label: 'Restart', icon: 'fa fa-repeat', visible: (!this.Exec && !this.isJobExec), command: (onclick) => {
          this.restart(this.rowUUid, this.rowVersion)
        }
      },
      {
        label: 'Upload', icon: 'fa-download', visible: (this.type == 'datapod' ? true : false), command: (onclick) => {
          this.upload(this.rowUUid, this.rowName)
        }
      },
      //{label: 'Upload', icon: 'fa-download'}
    ]
    //this.onRowSelect(event)                                                 
    if (((this.type).toLowerCase()).indexOf("exec") != -1) {
      this.isExec = "true";
      this.columnDefs.splice(5, 1);
      this.columnDefs.splice(5, 0, {
        headerName: "Status",
        field: "status",
        cellRenderer: function (params) {
          //console.log(params)
          return '<div class="label-sm label-success" style=" text-align:center;width: 88%;font-size: 13px;padding: 2px;color: white;margin: 0 auto;font-weight: 300;background-color:' + params.value.color + ' !important">' + params.value.stage + '</div>'
        }
      })
      this.columnDefs.splice(6, 1);
      this.columnDefs.splice(6, 1);
      for (let i = 0; i < response.length; i++) {
        if (response[i]["status"] != null) {
          response[i]["status"] = this.apphelper.sortByProperty(response[i]["status"], "createdOn");
          let status = response[i]["status"];
          response[i]["status"] = {};
          response[i]["status"].stage = this.apphelper.getStatus(status)["stage"];
          response[i]["status"].color = this.metaconfig.getStatusDefs(response[i]["status"].stage)['color'];
        }
      }

    }
    else {
      for (let i = 0; i < response.length; i++) {
        response[i]["status"] = {};

      }
      this.selectedType = this.type
      this.isExec = "false";
      // if(this.columnDefs.length >5){
      //     this.columnDefs.splice(6,1);
      // }
      // this.columnDefs.splice(5,1);
      // this.columnDefs.splice(5,0,{
      //     headerName: "Active",
      //     field: "active",
      //     cellRenderer: function(params){
      //         let active:string
      //         if(params.value=="Y"){
      //             active="Active"
      //         }
      //         else{
      //             active="Inactive"
      //         }
      //         return '<div style=" text-align:center;">'+active+'</div>'}
      //     })
      // this.columnDefs.splice(6,1);
      // this.columnDefs.splice(6,0,{
      //     headerName: "Published",
      //     field: "published",
      //     cellRenderer: function(params){
      //         let published:string
      //         if(params.value=="Y"){
      //             published="Yes"
      //         }
      //         else{
      //             published="No"
      //         }
      //          return '<div style=" text-align:center;">'+published+'</div>'
      //     }
      // })
      // this.columnDefs.splice(7,1);
    }

    // this.columnDefs.push({
    //     headerName: "Action",
    //     field: "action",
    //     cellClass: 'dropdownCustom',
    //     cellRendererFramework: DropdownComponent
    // });

    this._RowData = response;
    this.rowData1 = this._RowData;
    this.getAllLatest();
    this.getAllLatestUser();

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
  onRowSelect(event) {
    console.log(event.data);
  }

  onClickMenu(data) {
    // alert(this.type)
    console.log(data);

    if (this.type.indexOf('exec') != -1) {
      if (data.status != null) {
        this.items[0].disabled = this.type.indexOf('dagexec') != -1 ? ['Completed', 'NotStarted', 'Terminating', 'Failed', 'InProgress', 'Killed'].indexOf(data.status.stage) == -1 : this.type.indexOf('group') == -1 ? ['Completed'].indexOf(data.status.stage) == -1 : ['Completed', 'InProgress', 'Killed', 'Failed', 'Terminating'].indexOf(data.status.stage) == -1;
        this.items[9].disabled = ['InProgress'].indexOf(data.status.stage) == -1
        this.items[10].disabled = ['Completed', 'NotStarted', 'Terminating', 'InProgress'].indexOf(data.status.stage) != -1
      }

    }
    if (this.isJobExec == true) {
      this.items[0].disabled = false
    }
    this.rowUUid = data.uuid
    this.rowVersion = data.version
    this.rowID = data.id
    this.rowName = data.name
    this.rowStatus = data.status
  }

  // getGridStyle = function(data) {

  //     let style = {}
  //     if (data && data.length >1) {
  //       style['height'] = ((data.length < 10 ? data.length * 40 : 400) + 60) + 'px';
  //     }
  //     else if(data.length ==1){
  //         style['height']="100px";
  //     }
  //     else{
  //       style['height']="100px";
  //     }
  //     return style;
  //   }
  getAllLatest(): void {
    this._commonListService.getAllLatest(this.selectedType)
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

  onGridReady(params) {
    params.api.sizeColumnsToFit();
  }

  selectAllRows() {
    this.gridOptions.api.selectAll();
  }
  onSearchCriteria() {

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
    this._commonListService.getBaseEntityByCriteria(this.type, this.execname.label || "", startDateUtcStr, this.tags, this.username.label, endDateUtcStr, this.active, this.status)
      .subscribe(
      response => { this.getGrid(response) },
      error => console.log("Error :: " + error)
      )

  }
  onChange(value) {
    this.selectedType = value;
    this.gridTitle = this.metaconfig.getMetadataDefs((this.selectedType).toLowerCase())['caption']
    this.breadcrumbDataFrom[1].caption = this.gridTitle + ' Exec'
    //this.breadcrumbDataFrom[2].caption=this.expression.name;
    this.type = value + "exec"
    this.getBaseEntityByCriteria();
  }
  onSelectionChanged(event: any) { console.log("selection", event); }
  public onQuickFilterChanged($event) {
    this.gridOptions.api.setQuickFilter($event.target.value);
    //this.gridOptions.onModelUpdated()
  }

  //code for training execution
  getParamListORParamset() {
    this.displayDialogBox = true;
    this.onChangeType()
  }

  onChangeType() {
    if (this.selectParamType == "paramlist") {
      this.selectParamsetName = {};
      this._commonListService.getParamListByTrain(this.executeId, this.executeVersion, this.type)
        .subscribe(response => { this.onSuccessgetParamListByTrain(response) },
        error => console.log("Error ::" + error));
    }
    else if (this.selectParamType == "paramset") {
      this.selectParamlistName = {};
      this._commonListService.getParamSetByTrain(this.executeId, this.executeVersion, this.type)
        .subscribe(response => { this.onSuccessgetParamSetByTrain(response) },
        error => console.log("Error ::" + error));
    }
  }

  onSuccessgetParamListByTrain(response) {
    let paramlistArray = [];
    for (const i in response) {
      let paramlistObj = {}
      paramlistObj["label"] = response[i].ref.name;
      paramlistObj["value"] = {};
      paramlistObj["value"]["uuid"] = response[i].ref.uuid;
      paramlistObj["value"]["name"] = response[i].ref.name;
      paramlistArray[i] = paramlistObj;
    }
    this.paramlistData = paramlistArray;
  }

  onSuccessgetParamSetByTrain(response) {
    let paramsetArray = [];
    for (const i in response) {
      let paramsetObj = {}
      paramsetObj["label"] = response[i].name;
      paramsetObj["value"] = {};
      paramsetObj["value"]["uuid"] = response[i].uuid;
      paramsetObj["value"]["name"] = response[i].name;
      paramsetObj["value"]["version"] = response[i].version;
      paramsetArray[i] = paramsetObj;
    }
    this.paramsetData = paramsetArray;
    this.paramsetData.splice(0, 0, {
      'label': 'USE DEFAULT VALUE',
      'value': ''
    });
    this.paramTableCol2 = response[0].paramInfo[0].paramSetVal;
    this.paramsetArrayTable = response[0].paramInfo;
  }

  onChangeParamlist() {
    if (this.selectParamlistName["uuid"] !== " ") {
      this._commonListService.getParamByParamList(this.selectParamlistName["uuid"], "paramlist")
        .subscribe(response => {
          this.onSuccessgetParamByParamList(response),
            error => console.log("Error ::" + error)
        })
    }
  }

  onSuccessgetParamByParamList(response) {
    var arrayTemp = [];
    for (const i in response) {
      let paramtableObj = {};
      paramtableObj["paramName"] = response[i].paramName;
      paramtableObj["paramType"] = response[i].paramType;

      if (this.typeSimple.indexOf(response[i].paramType) != -1) {
        paramtableObj["paramValue"] = response[i].paramValue.value;
      }
      else if (response[i].paramType == "distribution") {
        let value1Temp: DependsOn = new DependsOn();
        value1Temp.label = response[i].paramValue.ref.name;
        value1Temp.uuid = response[i].paramValue.ref.uuid;

        paramtableObj["paramValue"] = value1Temp;
      }
      else if (response[i].paramValue == null) {
        paramtableObj["paramValue"] = "";
      }
      arrayTemp[i] = paramtableObj;
    }
    this.paramtableArray = arrayTemp;
  }

  cancelDialogbox() {
    this.displayDialogBox = false;
  }

  checkAllAttributeRow() {
    if (!this.selectAllAttributeRow) {
      this.selectAllAttributeRow = true;
    }
    else {
      this.selectAllAttributeRow = false;
    }
    console.log(this.selectAllAttributeRow);
    this.paramsetArrayTable.forEach(paramjson => {
      paramjson.selected = this.selectAllAttributeRow;
      console.log(JSON.stringify(paramjson))
    });
  }

  executeWithExecParamListTraining() {
    this.displayDialogBox = false; if (this.selectParamType == "paramlist") {
      if (this.selectParamlistName) {
        var execParams = {};
        var paramListInfo = [];
        var paramInfo = {};
        var paramInfoRef = {};
        paramInfoRef["uuid"] = this.selectParamlistName["uuid"];
        paramInfoRef["type"] = "paramlist";
        paramInfo["ref"] = paramInfoRef;
        paramListInfo[0] = paramInfo;
        execParams["paramListInfo"] = paramListInfo;
      } else {
        execParams = null;
      }
      this.paramlistData = null;
      this.selectParamType = null;
    }
    else {
      let newDataList = [];
      //this.selectallattribute = false;
      let execParams = {}
      if (this.paramsetArrayTable) {
        this.paramsetArrayTable.forEach(selected => {
          if (selected.selected) {
            newDataList.push(selected);
          }
        });
        let paramInfoArray = [];
        if (this.paramsetArrayTable && newDataList.length > 0) {
          let ref = {}
          ref["uuid"] = this.selectParamsetName["uuid"];
          ref["version"] = this.selectParamsetName["version"];
          for (var i = 0; i < newDataList.length; i++) {
            var paraminfo = {};
            paraminfo["paramSetId"] = newDataList[i].paramSetId;
            paraminfo["ref"] = ref;
            paramInfoArray[i] = paraminfo;
          }
        }

        if (paramInfoArray.length > 0) {
          execParams["paramInfo"] = paramInfoArray;
        }
        else {
          execParams = null;
        }
      }
    }
    this._commonService.executeWithParams("train", this.executeId, this.executeVersion, execParams)
      .subscribe(response => {
        this.onSuccessExecuteTraining(response)
      },
      error => {
        console.log('Error :: ' + error)
        this.onErrorTraining()
      }
      )
  }

  onSuccessExecuteTraining(response) {
    this.showMessage('Configuration Submited and Saved Successfully', 'success', 'Success Message')
    setTimeout(() => {
      // this.goBack()
    }, 1000);
  }

  showMessage(msg, msgtype, msgsumary) {
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }

  onErrorTraining() {
    this.msgs = [];
    this.msgs.push({ severity: 'failed', summary: 'failed message', detail: 'execution failed' });
  }

  //code for simulation execution 
  onChangeAtrributes(index) {
    this._commonService.getAllAttributeBySource(this.paramListHolder[index].paramValue.uuid, "datapod").subscribe(
      response => { this.onSuccessgetAttributesByDatapod(response, index) },
      error => console.log("Error :: " + error));
  }

  onSuccessgetAttributesByDatapod(response, index) {
    let allAttributeinto = [];
    for (const i in response) {
      let attributeInfoTagObj = {};
      attributeInfoTagObj["label"] = response[i].name;
      attributeInfoTagObj["value"] = {};
      attributeInfoTagObj["value"]["label"] = response[i].name;
      attributeInfoTagObj["value"]["uuid"] = response[i].uuid;
      attributeInfoTagObj["value"]["datapodname"] = response[i].datapodname;
      attributeInfoTagObj["value"]["attributeId"] = response[i].attributeId;
      allAttributeinto[i] = attributeInfoTagObj;
      //make same arrays of attribute n attributes or make array name differnet
    }
    this.paramListHolder[index]["allattributeInfoTag"] = allAttributeinto;
    console.log(JSON.stringify(this.paramListHolder[index]["allattributeInfoTag"]));
  }

  getAllAttribute(index) {
    if (this.paramListHolder[index].paramValue !== null || this.paramListHolder[index].paramType == "attribute") {
      this._commonService.getAllAttributeBySource(this.paramListHolder[index].paramValue.uuid, "datapod").subscribe(
        response => { this.onSuccessgetAllAttributeBySource(response, index) },
        error => console.log("Error :: " + error));
    }
  }

  onSuccessgetAllAttributeBySource(response, index) {
    let allAttributeinto1 = [];
    for (const i in response) {
      let allAttributeintoObj = {};
      allAttributeintoObj["label"] = response[i].dname;
      allAttributeintoObj["value"] = {};
      allAttributeintoObj["value"]["label"] = response[i].dname;
      allAttributeintoObj["value"]["uuid"] = response[i].uuid;
      allAttributeintoObj["value"]["name"] = response[i].name;
      allAttributeintoObj["value"]["attributeId"] = response[i].attributeId;
      allAttributeintoObj["value"]["datapodname"] = response[i].datapodname;
      // allAttributeintoObj["value"]["attributeId"] = response[i].attributeId;

      allAttributeinto1[i] = allAttributeintoObj;
    }
    this.paramListHolder[index]["allAttributeinto"] = allAttributeinto1;
  }

  getAllLatestSimulation(type, index) {
    this._commonListService.getAllLatest(type || "dataset").subscribe(
      response => { this.onSucceessgetAllLatest(response, type, index) },
      error => console.log("Error :: " + error));
  }

  onSucceessgetAllLatest(response, type, index) {
    if (type == "datapod") {
      let allDatapod = [];
      for (const i in response) {
        let allDatapodObj = {};
        allDatapodObj["label"] = response[i].name;
        allDatapodObj["value"] = {};
        allDatapodObj["value"]["label"] = response[i].name;
        allDatapodObj["value"]["uuid"] = response[i].uuid;
        allDatapod[i] = allDatapodObj;
      }
      //this.getAllAttribute(type,index);
      this.paramListHolder[index]["allDatapod"] = allDatapod;
    }
    //this.allDatapod=temp1;

    if (type == "dataset") {
      let allDataset = [];
      for (const i in response) {
        let allDatasetObj = {};
        allDatasetObj["label"] = response[i].name;
        allDatasetObj["value"] = {};
        allDatasetObj["value"]["label"] = response[i].name;
        allDatasetObj["value"]["uuid"] = response[i].uuid;
        allDataset[i] = allDatasetObj;
      }
      this.paramListHolder[index]["allDataset"] = allDataset;
    }
    //this.allDataset=temp2;

    if (type == "rule") {
      let allRule = [];
      for (const i in response) {
        let allRuleObj = {};
        allRuleObj["label"] = response[i].name;
        allRuleObj["value"] = {};
        allRuleObj["value"]["label"] = response[i].name;
        allRuleObj["value"]["uuid"] = response[i].uuid;
        allRule[i] = allRuleObj;
      }
      this.paramListHolder[index]["allRule"] = allRule;
    }
    if (type == "distribution") {
      let allDistribution = [];
      for (const i in response) {
        let allDistributionObj = {};
        allDistributionObj["label"] = response[i].name;
        allDistributionObj["value"] = {};
        allDistributionObj["value"]["label"] = response[i].name;
        allDistributionObj["value"]["uuid"] = response[i].uuid;
        allDistribution[i] = allDistributionObj;
      }
      this.paramListHolder[index]["allDistribution"] = allDistribution;
    }
  }
  getExecParamList() {
    this._commonListService.getParamListByType(this.executeId, this.executeVersion, this.type, "view")
      .subscribe(
      response => {
        this.onSuccessgetParamListByType(response)
      },
      error => console.log("Error :: " + error)
      )
  }
  onSuccessgetParamListByType(response) {
    this.paramListHolder = response;
    let paramListHolder1 = [];
    let type = ["ONEDARRAY", "TWODARRAY"];
    var type1 = ['distribution', 'attribute', 'attributes', 'datapod', 'list'];
    if (this.paramListHolder.length > 0) {
      for (var i = 0; i < this.paramListHolder.length; i++) {
        let paramList = {};
        paramList["uuid"] = this.paramListHolder[i].ref.uuid;
        paramList["type"] = this.paramListHolder[i].ref.type;
        paramList["paramId"] = this.paramListHolder[i].paramId;
        paramList["paramType"] = this.paramListHolder[i].paramType.toLowerCase();
        paramList["paramName"] = this.paramListHolder[i].paramName;
        paramList["ref"] = this.paramListHolder[i].ref;
        paramList["attributeInfo"];
        paramList["allAttributeinto"] = [];
        paramList["attributeInfoTag"] = [];
        if (type1.indexOf(this.paramListHolder[i].paramType) == -1) {
          paramList["isParamType"] = "simple";
          paramList["paramValue"] = this.paramListHolder[i].paramValue.value;
          paramList["selectedParamValueType"] = 'simple'
        } else if (type1.indexOf(this.paramListHolder[i].paramType) != -1) {
          paramList["isParamType"] = this.paramListHolder[i].paramType;
          paramList["selectedParamValueType"] = this.paramListHolder[i].paramType == "distribution" ? this.paramListHolder[i].paramType : "datapod";
          paramList["paramValue"] = this.paramListHolder[i].paramValue;
          if (this.paramListHolder[i].paramValue != null && this.paramListHolder[i].paramValue !== 'list') {

            var selectedParamValue = {};
            selectedParamValue["uuid"] = this.paramListHolder[i].paramValue.ref.uuid;
            selectedParamValue["type"] = this.paramListHolder[i].paramValue.ref.type;
            paramList["selectedParamValue"] = selectedParamValue;
          }
          if (this.paramListHolder[i].paramValue && this.paramListHolder[i].paramType == 'list') {

            paramList["selectedParamValueType"] = "list";
            var listvalues = this.paramListHolder[i].paramValue.value.split(',');
            var selectedParamValue = {};
            selectedParamValue["type"] = this.paramListHolder[i].paramValue.ref.type;
            selectedParamValue["value"] = listvalues[0];
            paramList["paramValue"] = selectedParamValue;
            paramList["selectedParamValue"] = selectedParamValue;
            for (const i in listvalues) {
              var listvalues1 = {};
              listvalues1["label"] = listvalues[i];
              listvalues1["value"] = listvalues[i];
              listvalues[i] = listvalues1;
            }
            paramList["allListInfo"] = listvalues;
          }
        } else {
          paramList["isParamType"] = "datapod";
          paramList["selectedParamValueType"] = 'datapod'
          paramList["paramValue"] = this.paramListHolder[i].paramValue;
        }
        paramListHolder1[i] = paramList;

      }
      this.paramListHolder = [];
      this.paramListHolder = paramListHolder1;
      this.displayDialogBoxSimulation = true;
      console.log(JSON.stringify(this.paramListHolder))
    }
  }
  cancelDialogBox() {
    this.displayDialogBoxSimulation = false;
  }
  executeWithExecParamList() {
    this.displayDialogBoxSimulation = false;
    let execParams = {};
    let paramListInfo = [];
    if (this.paramListHolder.length > 0) {
      for (let i = 0; i < this.paramListHolder.length; i++) {
        let paramList = {};
        paramList["paramId"] = this.paramListHolder[i].paramId;
        paramList["paramName"] = this.paramListHolder[i].paramName;
        paramList["paramType"] = this.paramListHolder[i].paramType;
        paramList["ref"] = this.paramListHolder[i].ref;
        if (this.paramListHolder[i].paramType == 'attribute') {
          let attributeInfoArray = [];
          let attributeInfo = {};
          let attributeInfoRef = {}
          attributeInfoRef["type"] = this.paramListHolder[i].selectedParamValueType;
          attributeInfoRef["uuid"] = this.paramListHolder[i].attributeInfo.uuid;
          attributeInfoRef["name"] = this.paramListHolder[i].attributeInfo.name
          attributeInfo["ref"] = attributeInfoRef;
          attributeInfo["attrId"] = this.paramListHolder[i].attributeInfo.attributeId;
          attributeInfoArray[0] = attributeInfo
          paramList["attributeInfo"] = attributeInfoArray;

        }
        if (this.paramListHolder[i].paramType == 'attributes') {
          let attributeInfoArray = [];
          for (let j = 0; j < this.paramListHolder[i].attributeInfoTag.length; j++) {
            let attributeInfo = {};
            let attributeInfoRef = {}
            attributeInfoRef["type"] = this.paramListHolder[i].selectedParamValueType;
            attributeInfoRef["uuid"] = this.paramListHolder[i].attributeInfoTag[j].uuid;
            attributeInfoRef["name"] = this.paramListHolder[i].attributeInfoTag[j].datapodname;
            attributeInfo["ref"] = attributeInfoRef;
            attributeInfo["attrId"] = this.paramListHolder[i].attributeInfoTag[j].attributeId;
            attributeInfo["attrName"] = this.paramListHolder[i].attributeInfoTag[j].label;
            attributeInfoArray[j] = attributeInfo
          }
          paramList["attributeInfo"] = attributeInfoArray;
        }
        else if (this.paramListHolder[i].paramType == 'distribution' || this.paramListHolder[i].paramType == 'datapod') {

          let ref = {};
          let paramValue = {};
          ref["type"] = this.paramListHolder[i].selectedParamValueType;
          ref["uuid"] = this.paramListHolder[i].paramValue.uuid;

          paramValue["ref"] = ref;
          paramList["paramValue"] = paramValue;
        }
        else if (this.paramListHolder[i].selectedParamValueType == "simple") {
          let ref = {};
          let paramValue = {};
          ref["type"] = this.paramListHolder[i].selectedParamValueType;
          paramValue["ref"] = ref;
          paramValue["value"] = this.paramListHolder[i].paramValue
          paramList["paramValue"] = paramValue;
        }
        else if (this.paramListHolder[i].selectedParamValueType == "list") {
          let ref = {};
          let paramValue = {};
          ref["type"] = 'simple';
          paramValue["ref"] = ref;
          paramValue["value"] = this.paramListHolder[i].paramValue
          paramList["paramValue"] = paramValue;
        }
        paramListInfo[i] = paramList;
      }
      execParams["paramListInfo"] = paramListInfo;
    }
    else {
      execParams = null;
    }
    console.log(JSON.stringify(execParams));
    this._commonListService.executeWithParams("simulate", this.executeId, this.executeVersion, execParams).subscribe(
      response => { this.onSuccessExecute(response) },
      error => { this.onError(error) }
    )
  }

  onSuccessExecute(response) {
    console.log(JSON.stringify(response));
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'simulate Save Successfully' });
    // setTimeout(() => {
    //   this.goBack();
    // }, 1000);
  }

  onError(error) {
    console.log('Error :: ' + error);
    this.msgs = [];
    this.msgs.push({ severity: 'Failed', summary: 'Error Message', detail: 'simulate execution failed' });
    // setTimeout(() => {
    //   this.goBack();
    // }, 1000);
  }

  getParams() {
    this.isParamModel = "true"
    this.paramtablecol = null
    this.paramtable = null;
    this.isTabelShow = false;
    jQuery(this.ParamsModel.nativeElement).modal('show');
    this._commonListService.getParamSetByType(this.executeId, this.executeVersion, this.type, "view")
      .subscribe(
      response => {
        this.onSuccessGetParamSetByType(response)
      },
      error => console.log("Error :: " + error)
      )
  }
  onSuccessGetParamSetByType(response) {

    // let temp = []
    // temp.push({
    //     label: 'Use Default Value', value: { label: 'Use Default Value', uuid: '' }
    // });
    // for (const n in response) {
    //     let allname = {};
    //     allname["label"] = response[n]['name'];
    //     allname["value"] = {};
    //     allname["value"]["label"] = response[n]['name'];
    //     allname["value"]["uuid"] = response[n]['uuid'];
    //     temp[n] = allname;
    // }
    // this.allparamset = temp
    this.allparamset = response

  }
  onSelectparamSet() {
    var paramSetjson = {};
    var paramInfoArray = [];
    if (this.paramsetdata && this.paramsetdata != null) {
      for (var i = 0; i < this.paramsetdata.paramInfo.length; i++) {
        var paramInfo = {};
        paramInfo["paramSetId"] = this.paramsetdata.paramInfo[i].paramSetId
        paramInfo["selected"] = false
        var paramSetValarray = [];
        for (var j = 0; j < this.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
          var paramSetValjson = {};
          paramSetValjson["paramId"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
          paramSetValjson["paramName"] = this.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
          paramSetValjson["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
          paramSetValjson["ref"] = this.paramsetdata.paramInfo[i].paramSetVal[j].ref;
          paramSetValarray[j] = paramSetValjson;
          paramInfo["paramSetVal"] = paramSetValarray;
          paramInfo["value"] = this.paramsetdata.paramInfo[i].paramSetVal[j].value;
        }
        paramInfoArray[i] = paramInfo;
      }
      this.paramtablecol = paramInfoArray[0].paramSetVal;
      this.paramtable = paramInfoArray;
      paramSetjson["paramInfoArray"] = paramInfoArray;
      this.isTabelShow = true;
    } else {
      this.isTabelShow = false;
    }
  }
  selectAllRow() {
    // this.paramtable.forEach((stage) => { // foreach statement
    //     stage.selected = this.selectallattribute;
    // })

    if (!this.selectallattribute) {
      this.selectallattribute = true;
    }
    else {
      this.selectallattribute = false;
    }
    this.paramtable.forEach(stage => {

      stage.selected = this.selectallattribute;
    });
  }

  executeWithExecParams() {
    let newDataList = [];
    this.selectallattribute = false;
    let execParams = {}
    if (this.paramtable) {
      this.paramtable.forEach(selected => {
        if (selected.selected) {
          newDataList.push(selected);
        }
      });

      let paramInfoArray = [];

      if (this.paramtable && newDataList.length > 0) {
        let ref = {}
        ref["uuid"] = this.paramsetdata.uuid;
        ref["version"] = this.paramsetdata.version;
        for (var i = 0; i < newDataList.length; i++) {
          var paraminfo = {};
          paraminfo["paramSetId"] = newDataList[i].paramSetId;
          paraminfo["ref"] = ref;
          paramInfoArray[i] = paraminfo;
        }
      }

      if (paramInfoArray.length > 0) {
        execParams["paramInfo"] = paramInfoArray;
      }
      else {
        execParams = null
      }
    }
    jQuery(this.ParamsModel.nativeElement).modal('hide');
    this._commonListService.executeWithParams(this.type, this.executeId, this.executeVersion, execParams)
      .subscribe(
      response => {
        //this.getBaseEntityByCriteria()
        this.isModel = "false";
        this.msgs = [];
        this.msgs.push({ severity: 'success', summary: 'Success Message', detail: this.gridTitle + ' Executed Successfully' });
      },
      error => console.log("Error :: " + error)
      )
  }
}

