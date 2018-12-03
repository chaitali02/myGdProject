import { Component, OnInit } from '@angular/core';
import { CommonService } from '../metadata/services/common.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Version } from './../metadata/domain/version'
import { SelectItem } from 'primeng/primeng';
import { Location, DatePipe } from '@angular/common';
import { AppConfig } from '../app.config';
import moment = require('moment');
import { FrequencyType } from './frequencyType';

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
  VersionList: SelectItem[] = [];
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
  isSubmitEnable: any;
  attributes: any[];
  selectallattribute: boolean;
  attrtypes: string[];
  attrtype: string;
  displayDialog: boolean;
  displayDialog1: boolean;
  displayDialog2: boolean;
  frequencyDetail: any = {}
  time: any;
  position: any;
  frequencyDetailCal: any;
  minimumDate = new Date();
  date1: Date;
  startDate: Date;
  endDate: Date;
  checkboxModelexecution: boolean;
  scheduleChg: boolean = true;

  // frequencyTypes: FrequencyType[] = [
  //   { id: 1, name: 'Once' },
  //   { id: 2, name: 'Daily' },
  //   { id: 3, name: 'Weekly' },
  //   { id: 4, name: 'Bi-Weekly' },
  //   { id: 5, name: 'Quarterly' },
  //   { id: 6, name: 'Monthly' },
  //   { id: 7, name: 'Yearly' }
  // ];
  // ftype: any = this.frequencyTypes[0];

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
      { quarter: "Q4", name: "Q4", checked: false, position: "3" }]
  };
  //attributes: any[];
  optionChoose: any[];
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private datePipe: DatePipe) {
    this.privResponse = null;
    this.pipelineInfoTags = null;

    this.showbatch = true;
    this.batch = {};
    this.batch["active"] = true

    this.breadcrumbDataFrom = [{
      "caption": "Batch Scheduler",
      "routeurl": "/app/list/batch"
    },
    {
      "caption": "BatchExec",
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
    this.frequencyDetail.weekDetail = [];
    this.optionChoose = this.attributes;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];

      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion();
        this.dropdownSettingsPipeline.disabled = this.mode == "false" ? false : true
      } this.getAllLatestPipeline();
    })
    this.attrtypes = ["Once", "Daily", "Weekly", "Bi-Weekly", "Quarterly", "Monthly", "Yearly"];
    this.attrtype = this.attrtypes[0];
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'batchview')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('batchview', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    console.log('getOneByUuidAndVersion is start');
    this.batch = response;
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = response.createdBy.ref.name;
    this.batch.published = response["published"] == 'Y' ? true : false
    this.batch.inParallel = response["inParallel"] == 'true' ? true : false
    this.batch.active = response["active"] == 'Y' ? true : false
    this.version = response['version'];
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.batch.tags = tags;
    }//End If
    this.breadcrumbDataFrom[2].caption = this.batch.name;
    let pipelineInfoNew = [];
    for (const i in response.pipelineInfo) {
      let pipelinetag = {};
      pipelinetag["id"] = response.pipelineInfo[i].ref.uuid;
      pipelinetag["itemName"] = response.pipelineInfo[i].ref.name;
      pipelineInfoNew[i] = pipelinetag;
    }
    this.pipelineInfoTags = pipelineInfoNew;

    let attribute: any[] = [];
    var frequencyName = []; 
    for (var i = 0; i < response['scheduleInfo'].length; i++) {
      let obj = {};
      obj["name"] = response['scheduleInfo'][i].name;
      obj["startDate"] = new Date(this.datePipe.transform(response['scheduleInfo'][i].startDate, "EEE MMM dd HH:mm:ss +0530 yyyy"));
      obj["endDate"] = new Date(this.datePipe.transform(response['scheduleInfo'][i].endDate, "EEE MMM dd HH:mm:ss +0530 yyyy"));
      this.attrtype = this.toTitleCase(response['scheduleInfo'][i].frequencyType);
      obj["attrtype"] = this.attrtype;


      for (let j = 0; j < response['scheduleInfo'][i].frequencyDetail.length; j++) {
        if (this.attrtype == "Weekly" || this.attrtype == "Bi-Weekly") {
          //var a = response['scheduleInfo'][i].frequencyDetail[j];
          for (const c in this.weekName.weekAlias) {
            // var b = this.weekName.weekAlias[c].position;
            if (response['scheduleInfo'][i].frequencyDetail[j] == this.weekName.weekAlias[c].position) {
              frequencyName[j] = this.weekName.weekAlias[c].name;
            }
          }
        }
        if (this.attrtype == "Quarterly") {
          //var a = response['scheduleInfo'][i].frequencyDetail[j];
          for (const c in this.weekName.quarters) {
            // var b = this.weekName.weekAlias[c].position;
            if (response['scheduleInfo'][i].frequencyDetail[j] == this.weekName.quarters[c].position) {
              frequencyName[j] = this.weekName.quarters[c].name;
            }
          }
        }
      }
      if (this.attrtype == "Monthly") {
        frequencyName = response['scheduleInfo'][i].frequencyDetail;
      }
      if (this.attrtype == "Once" || this.attrtype == "Daily" || this.attrtype == "Yearly") {
        frequencyName = [];
      }

      var a = response['scheduleInfo'][i]["scheduleChg"]
      if (response['scheduleInfo'][i]["scheduleChg"] == null || response['scheduleInfo'][i]["scheduleChg"] == "N") {
        attribute["scheduleChg"] = "N";
        this.scheduleChg = false;
      }
      else {
        attribute["scheduleChg"] = "Y";
        this.scheduleChg = true;
      }
      //obj["frequencyDetail"]=response['scheduleInfo'][i].frequencyDetail
      obj["frequencyDetail"] = frequencyName;
      attribute[i] = obj;
    }

    this.attributes = attribute;
    //pipelinetag["id"] = response.scheduleInfo[i].ref.uuid;
    console.log(JSON.stringify(this.pipelineInfoTags));
  }
  toTitleCase(str) {
    return str.replace(
      /\w\S*/g,
      function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }
    );
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.VersionList = temp
  }

  getAllLatestPipeline() {
    this._commonService.getAllLatest('dag')
      .subscribe(
        response => {
          this.onSuccessgetAllLatestPipeline(response)
        },
        error => console.log("Error ::" + error));
  }

  onSuccessgetAllLatestPipeline(response) {
    console.log('getAllLatestPipeline is start');
    this.privResponse = response
    this.pipelineInfoArray = [];

    for (const i in response) {
      let pipelineref = {};
      pipelineref["id"] = response[i]['uuid'];
      pipelineref["itemName"] = response[i]['name'];
      pipelineref["version"] = response[i]['version'];

      this.pipelineInfoArray[i] = pipelineref;
    }
    console.log('getAllLatestPipeline is end');
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'batch')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.batch.active = 'Y';
    }
    else {
      this.batch.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.batch.published = 'Y';
    }
    else {
      this.batch.published = 'N';
    }
  }
  onChangeParallel(event) {
    if (event === true) {
      this.batch.inParallel = 'true';
    }
    else {
      this.batch.inParallel = 'false';
    }
  }
  onItemSelect(item: any) {
    console.log(item);
    // console.log(this.selectedItems);
  }
  OnItemDeSelect(item: any) {
    console.log(item);
    // console.log(this.selectedItems);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }

  submitbatch() {
    this.isSubmitEnable = true;
    let batchJson = {};

    //batchJson["uuid"] = this.batch.uuid;
    batchJson["name"] = this.batch.name;
    var tagArray = [];
    if (this.batch.tags != null) {
      for (var counttag = 0; counttag < this.batch.tags.length; counttag++) {
        tagArray[counttag] = this.batch.tags[counttag].value;

      }
    }
    batchJson['tags'] = tagArray
    batchJson["desc"] = this.batch.desc;

    let pipelineInfoArrayNew = [];
    if (this.pipelineInfoTags != null) {
      for (const c in this.pipelineInfoTags) {
        let pipelineInfoRef = {};
        let pipelineRef = {};
        pipelineInfoRef["uuid"] = this.pipelineInfoTags[c].id;
        pipelineInfoRef["type"] = "dag";
        pipelineRef["ref"] = pipelineInfoRef;
        pipelineInfoArrayNew.push(pipelineRef);
      }
    }
    batchJson["pipelineInfo"] = pipelineInfoArrayNew;
    batchJson["active"] = this.batch.active == true ? 'Y' : "N"
    batchJson["published"] = this.batch.published == true ? 'Y' : "N"
    batchJson["inParallel"] = this.batch.inParallel == true ? 'true' : "false"
    batchJson["batchChg"] = "Y";

    let attributesArray = [];
    if (this.attributes != null) {
      if (this.attributes.length > 0) {
        for (let i = 0; i < this.attributes.length; i++) {
          let attribute = {};
          attribute["name"] = this.attributes[i]["name"];
          var a = this.attributes[i]["attrtype"];
          if (a != null)
            attribute["frequencyType"] = a.toUpperCase();
          // attribute["frequencyDetail"] = this.attributes[i].frequencyDetail;

          let indexArr: any = [];
          let frequencyDetails: any[] = []; 
          if (this.attributes[i]["attrtype"] == "Monthly") {
            // let monthName = [];
            // for (const c in this.attributes[i].frequencyDetail) {
            //let monthName = this.datePipe.transform(this.attributes[i].frequencyDetailCal[c], 'dd');
            // monthName = this.attributes[i].frequencyDetail.split(",");
            frequencyDetails = this.attributes[i].frequencyDetail.split(",");
            // }
          }
          else if (this.attributes[i]["attrtype"] == "Weekly" || this.attributes[i]["attrtype"] == "Bi-Weekly") {
            indexArr = this.attributes[i].frequencyDetail.split(",");
            let k = 0;
            for (let j = 0; j < this.weekName.weekAlias.length; j++) {
              var a = indexArr[k];
              var b = this.weekName.weekAlias[j].name;
              if (indexArr[k] == this.weekName.weekAlias[j].name) {
                let positions = this.weekName.weekAlias[j].position;
                frequencyDetails.push(positions);
                k++;
                j = -1;
              }
            }
          }
          else if (this.attributes[i]["attrtype"] == "Quarterly") {
            indexArr = this.attributes[i].frequencyDetail.split(",");
            let k = 0;
            for (let j = 0; j < this.weekName.quarters.length; j++) {
              var a = indexArr[k];
              var b = this.weekName.quarters[j].name;
              if (indexArr[k] == this.weekName.quarters[j].name) {
                let positions = this.weekName.quarters[j].position;
                frequencyDetails.push(positions);
                k++;
                j = -1;
              }
            }
          }
          // else if (this.attributes[i]["attrtype"] == "Once" || this.attributes[i]["attrtype"] == "Daily" || this.attributes[i]["attrtype"] == "Yearly") {
          //   //var positions;
          //   //frequencyDetails.push(positions);
          // }
          attribute["frequencyDetail"] = frequencyDetails; 
          if (this.scheduleChg == false) {
            attribute["scheduleChg"] = "N";
          }
          else {
            attribute["scheduleChg"] = this.scheduleChg == true ? "Y" : "N";
          }
          attribute["startDate"] = this.datePipe.transform(this.attributes[i]["startDate"], 'EEE MMM dd HH:mm:ss +0530 yyyy');
          attribute["endDate"] = this.datePipe.transform(this.attributes[i]["endDate"], 'EEE MMM dd HH:mm:ss +0530 yyyy');
          attributesArray[i] = attribute;
        }
      }
    }
    batchJson["scheduleInfo"] = attributesArray;

    console.log(JSON.stringify(batchJson));
    this._commonService.submit("batchview", batchJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("dag", response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmitEnable = true;
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'batch Submitted Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "dag", "execute").subscribe(
      response => {
        this.showMassage('Pipeline Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()

        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }
  showMassage(msg, msgtype, msgsumary) {
    this.isSubmitEnable = true;
    //this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }

  public goBack() {
    //  this._location.back();
    this.router.navigate(['app/list/batch']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/batchScheduler/batch', uuid, version, 'false']);
    this.dropdownSettingsPipeline = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: true
    };

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
    if (this.attributes == null) {
      this.attributes = [];
    }
    var len = this.attributes.length + 1
    var filertable = {};
    // filertable["key"] = false;
    // filertable["partition"] = false;
    // filertable["active"] = true;
    // filertable["dispName"] = " ";
    // filertable["type"] = this.attrtypes[0];
    // filertable["desc"] = " ";
    this.attributes.splice(this.attributes.length, 0, filertable);
  }
  removeRow() {
    let newDataList = [];
    this.selectallattribute = false;
    this.attributes.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.attributes = newDataList;
  }
  checkAllAttributeRow() {

    if (!this.selectallattribute) {
      this.selectallattribute = true;
    }
    else {
      this.selectallattribute = false;
    }
    this.attributes.forEach(attribute => {
      attribute.selected = this.selectallattribute;
    });
  }
  showDialog(index: any, type: string) {    //this.checkIfOthersAreSelected = true;
    if (type == "Weekly" || type == 'Bi-Weekly') {
      this.displayDialog = true;
    }
    else if (type == "Quarterly") {
      this.displayDialog1 = true;
    }
    else if (type == "Monthly") {
      this.displayDialog2 = true;
    }

    this.frequencyDetail.index = index;
  }

  dialogDone() {
    //console.log("Done Call")
    let weekValues = [];
    for (const c in this.frequencyDetail.weekDetail) {
      weekValues.push(this.frequencyDetail.weekDetail[c]);
    }
    this.attributes[this.frequencyDetail.index].frequencyDetail = weekValues.toString();
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
    this.displayDialog = false;
    this.displayDialog1 = false;
    this.displayDialog2 = false;
  }
  onChange(week: string, isChecked: boolean, index: any) {
    this.weekName.weekAlias[index].checked = isChecked;
    if (isChecked) {
      this.frequencyDetail.weekDetail.push(week);
    } else {
      let index = this.frequencyDetail.weekDetail.indexOf(week);
      this.frequencyDetail.weekDetail.splice(index, 1);
    }
    console.log(this.frequencyDetail.weekDetail);
  }

  dialogDone1(frequencyDetailCal) {
    let weekValues = [];
    for (const c in frequencyDetailCal) {
      weekValues.push(this.datePipe.transform(frequencyDetailCal[c], 'dd'));
    }
    this.attributes[this.frequencyDetail.index].frequencyDetail = weekValues.toString();

    this.displayDialog2 = false;
  }


}
