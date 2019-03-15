
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonService } from '../metadata/services/common.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Version } from './../metadata/domain/version'
import { Location, DatePipe } from '@angular/common';
import { AppConfig } from '../app.config';
// import moment = require('moment');
declare var require: any;
const moment=  require('moment');
import { FrequencyType } from './frequencyType';
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { MetaType } from './../metadata/enums/metaType';
import { BatchView } from '../metadata/domain/domain.batchView';
import { Schedule } from '../metadata/domain/domain.schedule';
import { AppHelper } from '../app.helper';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { ScheduleIO } from './../metadata/domainIO/domain.scheduleIO';
import { Batch } from '../metadata/domain/domain.batch';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { BaseEntity } from '../metadata/domain/domain.baseEntity';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';

@Component({
  selector: 'app-batch-scheduler',
  templateUrl: './batch-scheduler.component.html'
  // styleUrls: ['./batch-scheduler.component.css']

})
export class BatchSchedulerComponent implements OnInit {

  breadcrumbDataFrom: any;
  showbatch: any;
  batch: any;
  versions: any[];
  versionList: Array<DropDownIO> = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  inParallel: any;
  depends: any;
  allName: any;
  pipelineInfoArray: any;
  pipelineInfoTags: any;
  pipelineInfo: any;
  dropdownSettingsPipeline: any;
  privResponse: any;
  msgs: any;
  booleanPrivInfo: any;
  isSubmitEnable: boolean;
  scheduleInfoArray: Array<ScheduleIO>;
  selectallattribute: boolean;
  attrtypes: string[];
  attrtype: string;
  displayDialog: boolean;
  displayDialog1: boolean;
  displayDialog2: boolean;
  displayDialog3: boolean;
  frequencyDetail: any = {}
  time: any;
  position: any;
  frequencyDetailCal: any;
  today = new Date();
  date1: Date;
  endDate: Date;
  checkboxModelexecution: boolean;
  IsProgerssShow: string;
  selectedDate: any[] = [];
  rowNo = 0;
  batchChg: any = "N";
  scheduleChg: any = "N";
  isReadOnly: boolean = false;
  weekName = {
    weekAlias: [
      { alias: "S", name: "SUN", checked: false, position: "0" },
      { alias: "M", name: "MON", checked: false, position: "1" },
      { alias: "T", name: "TUE", checked: false, position: "2" },
      { alias: "W", name: "WED", checked: false, position: "3" },
      { alias: "T", name: "THU", checked: false, position: "4" },
      { alias: "F", name: "FRI", checked: false, position: "5" },
      { alias: "S", name: "SAT", checked: false, position: "6" }],
    quarters: [
      { quarter: "Q1", name: "Q1", checked: false, position: "0" },
      { quarter: "Q2", name: "Q2", checked: false, position: "1" },
      { quarter: "Q3", name: "Q3", checked: false, position: "2" },
      { quarter: "Q4", name: "Q4", checked: false, position: "3" }],
    hour: [
      { hour: "0", name: "0", checked: false, position: 0 },
      { hour: "1", name: "1", checked: false, position: 1 },
      { hour: "2", name: "2", checked: false, position: 2 },
      { hour: "3", name: "3", checked: false, position: 3 },
      { hour: "4", name: "4", checked: false, position: 4 },
      { hour: "5", name: "5", checked: false, position: 5 },
      { hour: "6", name: "6", checked: false, position: 6 },
      { hour: "7", name: "7", checked: false, position: 7 },
      { hour: "8", name: "8", checked: false, position: 8 },
      { hour: "9", name: "9", checked: false, position: 9 },
      { hour: "10", name: "10", checked: false, position: 10 },
      { hour: "11", name: "11", checked: false, position: 11 },
      { hour: "12", name: "12", checked: false, position: 12 },
      { hour: "13", name: "13", checked: false, position: 13 },
      { hour: "14", name: "14", checked: false, position: 14 },
      { hour: "15", name: "15", checked: false, position: 15 },
      { hour: "16", name: "16", checked: false, position: 16 },
      { hour: "17", name: "17", checked: false, position: 17 },
      { hour: "18", name: "18", checked: false, position: 18 },
      { hour: "19", name: "19", checked: false, position: 19 },
      { hour: "20", name: "20", checked: false, position: 20 },
      { hour: "21", name: "21", checked: false, position: 21 },
      { hour: "22", name: "22", checked: false, position: 22 },
      { hour: "23", name: "23", checked: false, position: 23 }
    ]
  };

  optionChoose: any[];
  sortArr: any;
  minimumDate: Date;
  startDate= new Date();
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isHomeEnable: boolean;
  metaType = MetaType;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  showForm: boolean = true;
  showDivGraph: boolean;
  isGraphInprogess: boolean;
  isEdit: boolean = false;
  isAdd: boolean;
  isversionEnable: boolean;
  

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, 
    private _commonService: CommonService, private datePipe: DatePipe, public appHelper: AppHelper) {
    
    // this.showGraph = false
    this.isHomeEnable = false;
    this.privResponse = null;
    this.pipelineInfoTags = null;

    this.showbatch = true;
    this.batch = {};
    this.batch["active"] = true
    this.IsProgerssShow = "false";

    this.breadcrumbDataFrom = [{
      "caption": "Batch Scheduler",
      "routeurl": "/app/list/batch"
    },
    {
      "caption": "Batch",
      "routeurl": "/app/list/batch"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dropdownSettingsPipeline = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: false
    };
    this.selectallattribute = false
    this.displayDialog = false;
    this.displayDialog1 = false;
    this.displayDialog2 = false;
    this.displayDialog3 = false;
    this.frequencyDetail.weekDetail = [];
    this.optionChoose = this.scheduleInfoArray;
    this.isSubmitEnable = false;

    this.minimumDate = this.startDate;
    this.isEditInprogess = false;
    this.isEditError = false;
  }

  ngOnInit() {

    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;

      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion();
        this.dropdownSettingsPipeline.disabled = this.mode == "false" ? false : true
      } this.getAllLatestPipeline();
    })
    this.attrtypes = ["Once", "Daily", "Weekly", "Bi-Weekly", "Hourly", "Quarterly", "Monthly", "Yearly"];
    this.attrtype = this.attrtypes[0];
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

  enableEdit(uuid, version) {
    this.router.navigate(['app/batchScheduler/batch', uuid, version, 'false']);
    this.isEdit = true;
    this.dropdownSettingsPipeline = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: true
    };
  }
  
  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid,version){

    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.batch.name;
  }
  

  getOneByUuidAndVersion() {
    this.isEditInprogess = true;
    this.isEditError = false;

    this._commonService.getOneByUuidAndVersion(this.id, this.version, this.metaType.BATCHVIEW)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error =>{ console.log("Error :: " + error);
        this.isEditError = true;      
      });
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.BATCH, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response: BatchView) {    
    this.batch = response;
    this.breadcrumbDataFrom[2].caption = response.name;
    this.uuid = response.uuid;

    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version

    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.inParallel = this.appHelper.convertStringToBoolean(response.inParallel);
    this.active = this.appHelper.convertStringToBoolean(response.active);

    let pipelineInfoNew = [];
    for (const i in response.pipelineInfo) {
      let pipelinetag = new MultiSelectIO();
      pipelinetag.id = response.pipelineInfo[i].ref.uuid;
      pipelinetag.itemName = response.pipelineInfo[i].ref.name;
      pipelineInfoNew[i] = pipelinetag;
    }
    this.pipelineInfoTags = pipelineInfoNew;

    let attribute: any[] = [];
    for (var i = 0; i < response.scheduleInfo.length; i++) {

      let obj = new ScheduleIO();
      obj.uuid = response.scheduleInfo[i].uuid;
      obj.name = response.scheduleInfo[i].name;
      obj.startDate = new Date(this.datePipe.transform(response.scheduleInfo[i].startDate, "EEE MMM dd HH:mm:ss +0530 yyyy"));
      obj.endDate = new Date(this.datePipe.transform(response.scheduleInfo[i].endDate, "EEE MMM dd HH:mm:ss +0530 yyyy"));
      obj.attrtype = this.toTitleCase(response.scheduleInfo[i].frequencyType);
      obj.frequencyDetail = [];

      if (obj.attrtype == "Weekly" || obj.attrtype == "Bi-Weekly") {
        let p = 0;
        for (let k = 0; k < this.weekName.weekAlias.length; k++) {
          if (response.scheduleInfo[i].frequencyDetail[p] == this.weekName.weekAlias[k].position) {
            obj.frequencyDetail[p] = this.weekName.weekAlias[k].name;
            p++;
            k = -1;
          }
        }
      }

      if (obj.attrtype == "Quarterly") {
        let p = 0;
        for (let k = 0; k < this.weekName.quarters.length; k++) {
          if (response.scheduleInfo[i].frequencyDetail[p] == this.weekName.quarters[k].position) {
            obj.frequencyDetail[p] = this.weekName.quarters[k].name;
            p++;
            k = -1;
          }
        }
      }

      if (obj.attrtype == "Hourly") {
        let p = 0;
        for (let k = 0; k < this.weekName.hour.length; k++) {
          // add toString() to last check strickly
          if (response.scheduleInfo[i].frequencyDetail[p] == this.weekName.hour[k].position.toString()) {
            obj.frequencyDetail[p] = this.weekName.hour[k].name;
            p++;
            k = -1;
          }
        }
      }

      if (obj.attrtype == "Monthly") {
        let p = 0;
        obj.frequencyDetail = response.scheduleInfo[i].frequencyDetail;
        p++;
      }

      if (obj.attrtype == "Once" || obj.attrtype == "Daily" || obj.attrtype == "Yearly") {
        obj.frequencyDetail = [];
      }

      obj.scheduleChg = "N"
      attribute[i] = obj;
    }

    this.scheduleInfoArray = attribute;
    console.log(JSON.stringify(this.pipelineInfoTags));
    this.isEditInprogess = false;
  }

  toTitleCase(str) {
    return str.replace(
      /\w\S*/g,
      function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }
    );
  }

  OnSuccesgetAllVersionByUuid(response: BaseEntity[]) {
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.versionList[i] = ver;
    }
  }

  getAllLatestPipeline() {
    this._commonService.getAllLatest(this.metaType.DAG)
      .subscribe(
        response => {
          this.onSuccessgetAllLatestPipeline(response)
        },
        error => console.log("Error ::" + error));
  }

  onSuccessgetAllLatestPipeline(response: BaseEntity[]) {
    this.privResponse = response
    this.pipelineInfoArray = [];

    for (const i in response) {
      let pipelineref = new MultiSelectIO();
      pipelineref.id = response[i].uuid;
      pipelineref.itemName = response[i].name;
      pipelineref.version = response[i].version;

      this.pipelineInfoArray[i] = pipelineref;
    }
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metaType.BATCH)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  // onChangeActive(event) {
  //   if (event === true) {
  //     this.batch.active = 'Y';
  //   }
  //   else {
  //     this.batch.active = 'N';
  //   }
  // }

  // onChangePublished(event) {
  //   if (event === true) {
  //     this.batch.published = 'Y';
  //   }
  //   else {
  //     this.batch.published = 'N';
  //   }
  // }
  // onChangeParallel(event) {
  //   if (event === true) {
  //     this.batch.inParallel = 'true';
  //   }
  //   else {
  //     this.batch.inParallel = 'false';
  //   }
  //   this.batchChg = "Y"
  // }
  onItemSelect(item: any) {
    console.log(item);
    this.batchChg = "Y"
  }
  OnItemDeSelect(item: any) {
    console.log(item);
    this.batchChg = "Y"
  }
  onSelectAll(items: any) {
    this.batchChg = "Y"
    console.log(items);
  }
  onDeSelectAll(items: any) {
    this.batchChg = "Y"
    console.log(items);
  }

  submitbatch() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let batchJson = new BatchView();
    batchJson.uuid = this.batch.uuid;
    batchJson.id = this.batch.id;
    if (batchJson.uuid == null) {
      this.batchChg = "Y"
    }

    batchJson.batchChg = this.batchChg;
    batchJson.name = this.batch.name;

    batchJson.tags = this.batch.tags;
    batchJson.desc = this.batch.desc;
    
    let pipelineInfoArrayNew = [];
    if (this.pipelineInfoTags != null) {
      for (const c in this.pipelineInfoTags) {
        let pipelineInfoRef = new MetaIdentifier();
        let pipelineRef = new MetaIdentifierHolder();
        pipelineInfoRef.uuid = this.pipelineInfoTags[c].id;
        pipelineInfoRef.type = this.metaType.DAG;
        pipelineRef.ref = pipelineInfoRef;
        pipelineInfoArrayNew.push(pipelineRef);
      }
    }
    batchJson.pipelineInfo = pipelineInfoArrayNew;

    batchJson.active =  this.appHelper.convertBooleanToString(this.batch.active);
    batchJson.published =  this.appHelper.convertBooleanToString(this.batch.published);
    batchJson.inParallel =  this.appHelper.convertBooleanToString(this.batch.inParallel);

    let scheduleArray = [];    
    if (this.scheduleInfoArray != null) {
      if (this.scheduleInfoArray.length > 0) {
        for (let i = 0; i < this.scheduleInfoArray.length; i++) {
          let schedule = new ScheduleIO();
          schedule.uuid = this.scheduleInfoArray[i].uuid;

          if (this.scheduleInfoArray[i].uuid == null) {
            this.scheduleInfoArray[i].scheduleChg = "Y";
          }

          schedule.scheduleChg = this.scheduleInfoArray[i].scheduleChg
          schedule.name = this.scheduleInfoArray[i].name;
          //var a = this.scheduleInfoArray[i].attrtype;
          if (this.scheduleInfoArray[i].attrtype != null)
          schedule.frequencyType = this.scheduleInfoArray[i].attrtype.toUpperCase();

          // let indexArr: any = [];
          let frequencyDetails: any[] = []; 
          if (this.scheduleInfoArray[i].attrtype == "Once" || this.scheduleInfoArray[i].attrtype == "Daily" || this.scheduleInfoArray[i].attrtype == "Yearly") {
            frequencyDetails = this.scheduleInfoArray[i].frequencyDetail;
            console.log("asfghg");
          }
          else if (this.scheduleInfoArray[i].attrtype == "Monthly") {
            frequencyDetails = this.scheduleInfoArray[i].frequencyDetail;
          }

          else if (this.scheduleInfoArray[i].attrtype == "Weekly" || this.scheduleInfoArray[i].attrtype == "Bi-Weekly") {
            let k = 0;
            for (let j = 0; j < this.weekName.weekAlias.length; j++) {
              if (this.scheduleInfoArray[i].frequencyDetail[k] == this.weekName.weekAlias[j].name) {
                let positions = this.weekName.weekAlias[j].position;
                frequencyDetails.push(positions);
                k++;
                j = -1;
              }
            }
          }

          else if (this.scheduleInfoArray[i].attrtype == "Quarterly") {
            let k = 0;
            for (let j = 0; j < this.weekName.quarters.length; j++) {
              if (this.scheduleInfoArray[i].frequencyDetail[k] == this.weekName.quarters[j].name) {
                let positions = this.weekName.quarters[j].position;
                frequencyDetails.push(positions);
                k++;
                j = -1;
              }
            }
          }

          else if (this.scheduleInfoArray[i].attrtype == "Hourly") {
            let k = 0;
            for (let j = 0; j < this.weekName.hour.length; j++) {
              if (this.scheduleInfoArray[i].frequencyDetail[k] == this.weekName.hour[j].name) {
                let positions = this.weekName.hour[j].position;
                frequencyDetails.push(positions);
                k++;
                j = -1;
              }
            }
          }

          schedule.frequencyDetail = frequencyDetails;
          schedule.startDate = this.datePipe.transform(this.scheduleInfoArray[i]["startDate"], 'EEE MMM dd HH:mm:ss +0530 yyyy');
          schedule.endDate = this.datePipe.transform(this.scheduleInfoArray[i]["endDate"], 'EEE MMM dd HH:mm:ss +0530 yyyy');
          scheduleArray[i] = schedule;
        }
      }
    }
    batchJson.scheduleInfo = scheduleArray;

    console.log(JSON.stringify(batchJson));
    this._commonService.submit(this.metaType.BATCHVIEW, batchJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.BATCH, response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Batch Submitted Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, this.metaType.BATCH, "execute").subscribe(
      response => {
        this.showMassage('Batch Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()

        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }
  showMassage(msg, msgtype, msgsumary) {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }

  public goBack() {
    this.router.navigate(['app/list/batch']);
  }  

  showview(uuid, version) {
    this.router.navigate(['app/batchScheduler/batch', uuid, version, 'true']);
    this.dropdownSettingsPipeline = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: false
    };
  }

  addRow() {
    if (this.scheduleInfoArray == null) {
      this.scheduleInfoArray = [];
    }
    var len = this.scheduleInfoArray.length + 1
    var filertable = new ScheduleIO;
    this.scheduleInfoArray.splice(this.scheduleInfoArray.length, 0, filertable);
  }

  removeRow() {
    let newDataList = [];
    this.selectallattribute = false;
    this.scheduleInfoArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.scheduleInfoArray = newDataList;
  }

  checkAllAttributeRow() {
    if (!this.selectallattribute) {
      this.selectallattribute = true;
    }
    else {
      this.selectallattribute = false;
    }
    this.scheduleInfoArray.forEach(attribute => {
      attribute.selected = this.selectallattribute;
    });
  }

  showDialog(index: any, type: string) {
    if (type == "Weekly" || type == 'Bi-Weekly') {
      if (this.scheduleInfoArray[index].frequencyDetail) {
        //let o = this.scheduleInfoArray[index].frequencyDetail;

        if (this.scheduleInfoArray[index].frequencyDetail != null) {
          //let wName = this.scheduleInfoArray[index].frequencyDetail;//.split(",");
          for (let p = 0; p < this.scheduleInfoArray.length; p++) {
           // let frequencyDetailValue = this.scheduleInfoArray[index].frequencyDetail[p];

            for (let k = 0; k < this.weekName.weekAlias.length; k++) {
              if (this.weekName.weekAlias[k].name == this.scheduleInfoArray[index].frequencyDetail[p]) {
                this.onChange(this.scheduleInfoArray[index].frequencyDetail[p], true, this.weekName.weekAlias[k].position);
                break;
              }
            }
          }
        }
      }
      this.displayDialog = true;
    }

    else if (type == "Quarterly") {
      if (this.scheduleInfoArray[index].frequencyDetail) {
        // let o = this.scheduleInfoArray[index].frequencyDetail;

        if (this.scheduleInfoArray[index].frequencyDetail != null) {
          //let wName = this.scheduleInfoArray[index].frequencyDetail;//.split(",");
          for (let p = 0; p < this.scheduleInfoArray[index].frequencyDetail.length; p++) {
            // let frequencyDetailValue = this.scheduleInfoArray[index].frequencyDetail[p];

            for (let k = 0; k < this.weekName.quarters.length; k++) {
              if (this.weekName.quarters[k].name == this.scheduleInfoArray[index].frequencyDetail[p]) {
                this.onChange(this.scheduleInfoArray[index].frequencyDetail[p], true, this.weekName.quarters[k].position);
                break;
              }
            }
          }
        }
      }
      this.displayDialog1 = true;
    }

    else if (type == "Hourly") {
      if (this.scheduleInfoArray[index].frequencyDetail) {
        //let o = this.scheduleInfoArray[index].frequencyDetail;
        if (this.scheduleInfoArray[index].frequencyDetail != null) {
          //let wName = this.scheduleInfoArray[index].frequencyDetail;//.split(",");
          for (let p = 0; p < this.scheduleInfoArray[index].frequencyDetail.length; p++) {
            //let frequencyDetailValue = this.scheduleInfoArray[index].frequencyDetail[p];
            for (let k = 0; k < this.weekName.hour.length; k++) {
              if (this.weekName.hour[k].name == this.scheduleInfoArray[index].frequencyDetail[p]) {
                this.onChange(this.scheduleInfoArray[index].frequencyDetail[p], true, this.weekName.hour[k].position);
                break;
              }
            }
          }
        }
      }
      this.displayDialog3 = true;
    }

    else if (type == "Monthly") {
      if (this.scheduleInfoArray[index].frequencyDetail) {
        this.frequencyDetailCal = [];
        this.frequencyDetailCal = this.selectedDate[index];
      }
      else {
        //this.frequencyDetailCal = [];
      }
      this.displayDialog2 = true;
    }
    this.isSubmitEnable = true;
    this.scheduleInfoArray[index].scheduleChg = "Y";
    this.frequencyDetail.index = index;
  }

  dialogDone() {
    let weekValues = [];
    for (const c in this.frequencyDetail.weekDetail) {
      for (let i = 0; i < this.weekName.weekAlias.length; i++) {
        if (this.weekName.weekAlias[i].name == this.frequencyDetail.weekDetail[c]) {
          weekValues[i] = this.frequencyDetail.weekDetail[c];
          break;
        }
      }
      for (let i = 0; i < this.weekName.quarters.length; i++) {
        if (this.weekName.quarters[i].name == this.frequencyDetail.weekDetail[c]) {
          weekValues[i] = this.frequencyDetail.weekDetail[c];
          break;
        }
      }
      for (let i = 0; i < this.weekName.hour.length; i++) {
        if (this.weekName.hour[i].name == this.frequencyDetail.weekDetail[c]) {
          weekValues[i] = this.frequencyDetail.weekDetail[c];
          break;
        }
      }
    }
    weekValues = weekValues.filter(i => i);
    console.log("old element : " + weekValues);
    this.scheduleInfoArray[this.frequencyDetail.index].frequencyDetail = weekValues
    this.deselectFunction();
  }

  dialogCancel() {
    console.log("Cancel Call");
    this.deselectFunction();
  }

  deselectFunction() {
    this.frequencyDetail.weekDetail = [];
    let tem: any = this.weekName.weekAlias;
    this.weekName.weekAlias = [];
    this.weekName.weekAlias = tem;
    for (let i = 0; i < this.weekName.weekAlias.length; i++) {
      this.weekName.weekAlias[i].checked = false;
    }
    for (let i = 0; i < this.weekName.quarters.length; i++) {
      this.weekName.quarters[i].checked = false;
    }
    for (let i = 0; i < this.weekName.hour.length; i++) {
      this.weekName.hour[i].checked = false;
    }

    this.displayDialog = false;
    this.displayDialog1 = false;
    this.displayDialog2 = false;
    this.displayDialog3 = false;
  }

  onChange(week: any, isChecked: boolean, index: any) {
    if (week == "Q1" || week == "Q2" || week == "Q3" || week == "Q4") {
      this.weekName.quarters[index].checked = isChecked;
    }
    else if (week == "SUN" || week == "MON" || week == "TUE" || week == "WED" || week == "THU" || week == "FRI" || week == "SAT") {
      this.weekName.weekAlias[index].checked = isChecked;
    }
    else {
      this.weekName.hour[index].checked = isChecked;
    }

    if (isChecked) {
      this.frequencyDetail.weekDetail.push(week);
    } else {
      let index = this.frequencyDetail.weekDetail.indexOf(week);
      this.frequencyDetail.weekDetail.splice(index, 1);
    }
    console.log(this.frequencyDetail.weekDetail);
  }

  dialogDoneMonth(frequencyDetailCal, index) {
    let weekValues = [];
    for (const c in frequencyDetailCal) {
      weekValues.push(this.datePipe.transform(frequencyDetailCal[c], 'd'));
    }
    this.selectedDate[index] = frequencyDetailCal;
    this.scheduleInfoArray[this.frequencyDetail.index].frequencyDetail = weekValues
    this.displayDialog2 = false;
  }
  onChangeDesc() {
    this.batchChg = "Y"
  }

  onChangeAttrtype(index) {
    this.scheduleInfoArray[index].frequencyDetail = [];
    this.scheduleInfoArray[index].scheduleChg = "Y";
    //this.minimumDate = this.startDate;
  }

  onUpdateStartDate(startdate: Date, index: any) {
    this.scheduleInfoArray[index].endDate = "";
    this.scheduleInfoArray[index].scheduleChg = "Y";
    this.minimumDate = startdate;
  }

}
