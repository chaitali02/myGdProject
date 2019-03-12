import { NgModule, Component, ViewEncapsulation, Input, ViewChild } from '@angular/core';
//import { MetaDataDataPodService } from './datapod.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { count } from 'rxjs/operators';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';
import { DataPodResource } from './datapod-resource';
import { AppMetadata } from '../../app.metadata';
import { AppHelper } from '../../app.helper';

import { Version } from '../../shared/version';

import { CommonService } from '../../metadata/services/common.service';
import { DatapodService } from '../../metadata/services/datapod.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

import { DatapodIO } from '../../metadata/domainIO/domain.datapodIO';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { DatapodAttributeIO } from '../../metadata/domainIO/domain.datapodAttributeIO';

@Component({
  selector: 'app-data-preparation',
  styleUrls: [],
  templateUrl: './datapod.template.html'
})
export class DatapodComponent {
  dropIndex: any;
  dragIndex: any;
  iSSubmitEnable: boolean;
  histogramcols: any[];
  histogramData: any[];
  showHistogramModel: boolean;
  originalDataHistogram: any[];
  isHistogramError: boolean;
  isHistogramInprogess: boolean;
  datacol: any;
  isShowChart: boolean;
  isShowDataGrid: boolean;
  histogramDetail: any;
  datastoreDetail: any;
  downloadversion: any;
  downloaduuid: any;
  source: any;
  isDownloadModel: boolean;
  IsTableShow1: boolean;
  isMetaSysn: boolean;
  metaCompareData: any;
  isShowCompareMetaData: any;
  length: string;
  isDisabled: boolean;
  resultcols: any[];
  resultData: any;
  showgetResults: boolean;
  rowData1: any;
  rowData: any;
  isShowDatastore: boolean;
  selectdatasourceName: any;
  runMode: any;
  IsError: boolean;
  columnOptions: any[];
  cols: any[];
  colsdata: any;
  IsTableShow: boolean;
  msgs: any;
  selectallattribute: boolean;
  attrtypes: string[];
  selectdatasourceType: any;
  datasourceType: { 'value': string; name: string; }[];
  selectVersion: { "version": string; };
  selectedVersion: Version
  datapodjson: any;

  baseUrl: any;
  locations: any;
  id: any;
  version: any;
  datasource: any;
  isDataError: any;
  isShowSimpleData: any;
  isDataInpogress: any;
  tableclass: any;
  showdatapod: any;
  showgraph: any;
  graphDataStatus: any;
  showGraph: any;

  versions: any;
  mode: string;
  datasource1: any;
  cache: any;
  uuid: any;
  detasource_uuid: any;
  tags: any;
  createdBy: any;
  attributes: any;
  published: any;
  active: any;
  iseditable: boolean;
  breadcrumbDataFrom: { "caption": String; "routeurl": String; }[];
  isSubmitEnable: any;
  isHomeEnable: boolean;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  locked: boolean;
  VersionList: Array<DropDownIO>;
  dataSourceNameArray: any[];
  datasourceName: any;
  download: { "format": string[]; "rows": any; "selectFormat": string; };

  constructor(private _config: AppConfig, public metaconfig: AppMetadata, public appHelper: AppHelper, private http: Http, private _commonService: CommonService, private _datapodService: DatapodService, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private route: ActivatedRoute) {
    this.baseUrl = config.getBaseUrl();
    this.isHomeEnable = false
    this.selectVersion = { "version": "" };
    this.showdatapod = true;
    this.isSubmitEnable = true;
    this.showGraph = false;
    this.uuid = '';
    //this.download = {"format" :"","rows":"","selectFormat":""}
    this.download.format = ["excel"]
    this.download.rows = 100
    this.download.selectFormat = 'excel'
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/datapod"
    },
    {
      "caption": "Datapod",
      "routeurl": "/app/list/datapod"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
  }

  ngOnInit() {
    this.datapodjson = {};
    this.active = true;
    this.published = false;
    this.cache = true;
    this.selectallattribute = false;
    this.attrtypes = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
    this.datasourceType = [
      { 'value': 'FILE', name: 'file' },
      { 'value': 'HIVE', name: 'hive' },
      { 'value': 'IMPALA', name: 'impala' },
      { 'value': 'MYSQL', name: 'mysql' },
      { 'value': 'POSTGRES', name: 'postgres' }
    ];
    this.selectdatasourceType = this.datasourceType[0].value

    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
      }
      else {
        this.selectType(this.selectdatasourceType);
      }
    });
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/datapod',]);
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('datapod', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response:BaseEntity[]) {
    var VersionList = [new DropDownIO]
    for (const i in response) {
      let verObj = new DropDownIO();
      verObj.label= response[i].version;
      verObj.value = {label: "", uuid: ""}
      verObj.value.label= response[i].version;
      verObj.value.uuid = response[i].uuid;
      VersionList[i] = verObj;
    }
    this.VersionList = VersionList
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  getOneByUuidAndVersion(id, version) {
    this._datapodService.getOneByUuidAndVersion(id, version, 'datapod')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response: DatapodIO) {
    this.breadcrumbDataFrom[2].caption = response.datapoddata.name;
    this.uuid = response.datapoddata.uuid
    this.datapodjson = response.datapoddata;
    const version: Version = new Version();
    version.label = response.datapoddata.version;
    version.uuid = response.datapoddata.uuid;
    this.selectedVersion = version

    if (response.datapoddata.tags != null) {
      this.tags = response.datapoddata.tags;
    }
    this.createdBy = response.datapoddata.createdBy.ref.name;

    this.attributes = response.attributes;
    this.locations = response.datapoddata;
    
    let datasourceNameObj = new DropDownIO();
    datasourceNameObj.uuid = response.datapoddata.datasource.ref.uuid;
    datasourceNameObj.label = response.datapoddata.datasource.ref.name;
    this.datasourceName = datasourceNameObj;

    this.cache = response.datapoddata.cache;

    this.published = this.appHelper.convertStringToBoolean(response.datapoddata.published);
    this.active = this.appHelper.convertStringToBoolean(response.datapoddata.active);
    this.locked = this.appHelper.convertStringToBoolean(response.datapoddata.locked);
    this.cache = this.appHelper.convertStringToBoolean(response.datapoddata.cache);

    this.selectdatasourceName = (response.datapoddata.datasource.ref.name).toUpperCase();
    // this.selectType(this.selectdatasourceName);
    this._datapodService.getLatestDataSourceByUuid(response.datapoddata.datasource.ref.uuid, response.datapoddata.datasource.ref.type)
      .subscribe(
        response => {
          this.OnSuccesLatestDatasourceByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesLatestDatasourceByUuid(response: any) {
    debugger
    this.selectdatasourceType = response.type;
    this.selectType(this.selectdatasourceType);
  }

  showDatapodSampleTable(data) {
    this.showgetResults = false
    this.isDataError = false;
    this.isShowSimpleData = true;
    this.isDataInpogress = true;
    this.tableclass = 'centercontent';
    this.showdatapod = false;
    this.showgraph = false;
    this.graphDataStatus = false;
    this.showGraph = false;
    this.IsTableShow = false;
    this.isShowDatastore = false
    this.isShowCompareMetaData = false
    const api_url = this.baseUrl + 'datapod/getDatapodSample?action=view&datapodUUID=' + data.uuid + '&datapodVersion=' + data.version + '&row=100';
    const DatapodSampleData = this._datapodService.getDatapodSample(api_url).subscribe(
      response => { this.OnSuccesDatapodSample(response) },
      error => {
        this.IsTableShow = true;
        console.log("Error :: " + error)
        this.IsError = true;
      }
    )
  }

  OnSuccesDatapodSample(response) {
    this.IsTableShow = true;

    this.colsdata = response
    let columns = [];
    console.log(response)
    for (var j = 0; j < this.datapodjson.attributes.length; j++) {
      var attribute = {};
      attribute["field"] = this.datapodjson.attributes[j].name;
      attribute["header"] = this.datapodjson.attributes[j].name;
      attribute["colwidth"] = ((attribute["header"].length * 9) + 40) + "px"
      attribute["name"] = this.datapodjson.attributes[j].name;
      attribute["dname"] = this.datapodjson.name;
      attribute["uuid"] = this.datapodjson.uuid;
      attribute["version"] = this.datapodjson.version;
      attribute["attributeId"] = this.datapodjson.attributes[j].attributeId;

      columns.push(attribute)
    }
    // if (response.length && response.length > 0) {
    //   Object.keys(response[0]).forEach(val => {
    //     if (val != "rownum") {
    //       let width = ((val.split("").length * 9) + 40) + "px"
    //       columns.push({ "field": val, "header": val, colwidth: width });
    //     }
    //   });
    // }
    this.cols = columns
    this.columnOptions = [];
    for (let i = 0; i < this.cols.length; i++) {
      this.cols["attrId"] = i
      this.columnOptions.push({ label: this.cols[i].header, value: this.cols[i] });
    }
  }

  selectType(val) {

    this._datapodService.getDatasourceByType(val)
      .subscribe(
        response => {
          this.OnSuccesDatasourceByType(response)
        },
        error => console.log("Error :: " + error));
  }

  OnSuccesDatasourceByType(response) {
    debugger
    // this.detasource_uuid = response[0]['uuid'];
    // const datasource2: Array<DataPodResource> = [];
    // for (const i in response) {
    //   datasource2.push(new DataPodResource(
    //     response[i]['uuid'], response[i]['name']
    //   ));
    // }
    // this.datasource1 = datasource2;

    this.dataSourceNameArray = []
    for (const i in response) {
      let dataSourceArrayObj = new DropDownIO();
      dataSourceArrayObj.label = response[i].name;
      dataSourceArrayObj.value = {'uuid':"",'label':""};
      dataSourceArrayObj.value.uuid = response[i].uuid;
      dataSourceArrayObj.value.label = response[i].name;
      this.dataSourceNameArray[i] = dataSourceArrayObj
    }
  }

  submitDatapod() {
    let datapod = {};
    this.isSubmitEnable = true;
    datapod["uuid"] = this.datapodjson.uuid;
    datapod["name"] = this.datapodjson.name;
    datapod["desc"] = this.datapodjson.desc;
    datapod["active"] = this.active == true ? "Y" : "N";
    datapod["published"] = this.published == true ? "Y" : "N";
    datapod["cache"] = this.cache == true ? "Y" : "N";

    //datapod.tags = this.tags
    let datasource = {}
    let ref = {};
    ref["type"] = "datasource";
    ref["uuid"] = this.detasource_uuid;
    datasource["ref"] = ref;
    datapod["datasource"] = datasource;
    let attributesArray = [];
    let count = 1;
    if (this.attributes.length > 0) {
      for (let i = 0; i < this.attributes.length; i++) {
        let attribute = {};
        if (this.attributes[i]["key"] == true) {
          attribute["key"] = count;
          count = count + 1;
        }
        else {
          attribute["key"] = null;
        }
        attribute["partition"] = this.attributes[i]["partition"] == true ? "Y" : "N";
        attribute["active"] = this.attributes[i]["active"] == true ? "Y" : "N";
        attribute["dispName"] = this.attributes[i]["dispName"];
        attribute["type"] = this.attributes[i]["type"];
        attribute["name"] = this.attributes[i]["name"];
        attribute["desc"] = this.attributes[i]["desc"];
        attributesArray[i] = attribute;
      }
    }
    datapod["attributes"] = attributesArray;
    console.log(JSON.stringify(datapod));
    this._commonService.submit("datapod", datapod).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Datapod Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  enableEdit(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/datapod', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/datapod', uuid, version, 'true']);
  }

  showDatapodPage() {
    this.isShowCompareMetaData = false
    this.showdatapod = true;
    this.isShowSimpleData = false;
    this.showgraph = false;
    this.graphDataStatus = false;
    this.showGraph = false
    this.isShowDatastore = false
    this.showgetResults = false
  }

  showDatapodGraph() {
    this.isShowCompareMetaData = false
    this.showdatapod = false;
    this.showgraph = false;
    this.isShowSimpleData = false;
    this.graphDataStatus = true;
    this.showGraph = true;
    this.isShowDatastore = false
    this.showgetResults = false
  }

  addRow() {
    if (this.attributes == null) {
      this.attributes = [];
    }
    var len = this.attributes.length + 1
    var datapodtable = new DatapodAttributeIO;
    datapodtable.key = false;
    datapodtable.partition = false;
    datapodtable.active = true;
    datapodtable.dispName = " ";
    datapodtable.type = this.attrtypes[0];
    datapodtable.desc = " ";
    this.attributes.splice(this.attributes.length, 0, datapodtable);
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

  downloadDatapodResult(source) {
    this.source = source
    this.isDownloadModel = true
    this.downloaduuid = this.uuid
    this.downloadversion = this.version
  }
  downloadDatastoreResult(source, datastoreDetail) {
    this.resultcols
    this.source = source
    this.isDownloadModel = true
    this.downloaduuid = datastoreDetail.uuid
    this.downloadversion = datastoreDetail.version
  }
  SubmitDownload(source) {
    const headers = new Headers();
    this.http.get(this.baseUrl + source + '/download?action=view&uuid=' + this.downloaduuid + '&version=' + this.downloadversion + '&row=' + this.download.rows + '&formate=' + this.download.selectFormat,
      { headers: headers, responseType: ResponseContentType.Blob })
      .toPromise()
      .then(response => this.saveToFileSystem(response));
  }
  saveToFileSystem(response) {
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
    this.isDownloadModel = false
  }
  close() {
    this.isDownloadModel = false
  }
  showDatastrores = function (data) {
    this.IsTableShow = false
    this.isShowCompareMetaData = false
    this.showFrom = false;
    this.isShowSimpleData = false;
    this.showGraphDiv = false;
    this.isDatastoreResult = false;
    this.isShowCompareMetaData = false;
    this.isDownloadDatapod = true;
    this.showdatapod = false
    this.showgetResults = false
    this._datapodService.getDatastoreByDatapod(data, "datapod").subscribe(
      response => { this.OnSuccesgetDatastoreByDatapod(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetDatastoreByDatapod(response) {
    console.log(response)
    this.IsTableShow = true
    this.isShowDatastore = true;
    this.rowData1 = response
    this.isDisabled = false
  }
  onChangeRadio(data) {
    console.log(data)
    this.IsTableShow1 = false
    this.datastoreDetail = data
    this._datapodService.getResult(data.uuid, data.version).subscribe(
      response => { this.OnSuccesgetResult(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetResult(response) {
    this.IsTableShow1 = true
    this.isDisabled = true
    this.showgetResults = true
    this.resultData = response
    let columns = [];
    console.log(response)
    if (response.length && response.length > 0) {
      Object.keys(response[0]).forEach(val => {
        if (val != "rownum") {
          let width = ((val.split("").length * 9) + 20) + "px"
          columns.push({ "field": val, "header": val, colwidth: width });
        }
      });
    }

    this.length = ((response.length < 10 ? response.length * 50 : 435) + 193) + 'px'
    this.resultcols = columns
    this.columnOptions = [];
    for (let i = 0; i < this.resultcols.length; i++) {
      this.columnOptions.push({ label: this.resultcols[i].header, value: this.resultcols[i] });
    }
  }
  onRowSelect(event) {
    console.log(event.data);
  }
  showCompareMetaData(data) {
    if (this.isShowCompareMetaData) {
      return false
    }
    this.IsTableShow = false
    this.isShowCompareMetaData = true
    this.showdatapod = false;
    this.showgraph = false;
    this.isShowSimpleData = false;
    this.graphDataStatus = false;
    this.showGraph = false;
    this.isShowDatastore = false
    this.showgetResults = false
    this._datapodService.compareMetadata(data.uuid, data.version, 'datapod').subscribe(
      response => { this.OnSuccescompareMetadata(response) },
      error => console.log('Error :: ' + error)
    )

  }
  OnSuccescompareMetadata(response) {
    this.IsTableShow = true
    for (let i = 0; i < response.length; i++) {
      if (response[i].status != null) {
        let status = response[i].status;
        let count
        response[i].status = {};
        response[i].status.value = this.metaconfig.getStatusDefs(status)['caption']
        //  response[i]["status"].stage = this.appHelper.getStatus(status)["stage"];
        response[i].status.color = this.metaconfig.getStatusDefs(status)['color'];
        if (response[i].status == "NOCHANGE") {
          count = count + 1;
        }
      }
    }
    if (response.length == count) {
      this.isMetaSysn = true;
    } else {
      this.isMetaSysn = false;
    }
    this.metaCompareData = response

  }
  synchronousMetadata(data) {
    if (this.isMetaSysn || this.selectdatasourceType == 'file') {
      return false
    }
    this._datapodService.synchronizeMetadata(data.uuid, data.version, 'datapod').subscribe(
      response => {
        this.datapodjson = response;
        this.showCompareMetaData(this.datapodjson)
      },
      error => console.log('Error :: ' + error)
    )

  }
  calculateHistrogram(row) {
    this.showHistogramModel = true
    this.histogramDetail = row;
    this.isShowDataGrid = false;
    this.isShowChart = false;
    console.log(row);
    this.datacol = null;
    this.isHistogramInprogess = true;
    this.isHistogramError = false;
    this._datapodService.getAttrHistogram(row.uuid, row.version, 'datapod', row.attributeId).subscribe(
      response => { this.onSuccessgetAttrHistogram(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAttrHistogram(response) {
    console.log(response);
    this.isShowDataGrid = false;
    this.isShowChart = true;
    this.ConvertTwoDisit(response, 'bucket');
    this.isHistogramInprogess = false;
    this.isHistogramError = false;
    this.datacol = {};
    if (response.length >= 20) {
      this.datacol.datapoints = response.slice(0, 20);

    } else {
      this.datacol.datapoints = response;
    }
    this.originalDataHistogram = response;
    var dataColumn = {"id":"","name":"","type":"","color":""}
    dataColumn.id = "frequency";
    dataColumn.name = "frequency"
    dataColumn.type = "bar"
    dataColumn.color = "#D8A2DE";
    this.datacol.datacolumns = [];
    this.datacol.datacolumns[0] = dataColumn;
    var datax = {"id":""};
    datax.id = "bucket";
    this.datacol.datax = datax;
    this.histogramData = response
    let columns = [];
    console.log(response)
    this.histogramcols = response
  }
  ConvertTwoDisit(data, propName) {
    // if(isNaN(data[0][propName])){
    if (data.length > 0 && data[0].propName.indexOf(" - ") != -1) {
      for (var i = 0; i < data.length; i++) {
        let a = data[i].propName.split(' - ')[0];
        let b = data[i].propName.split('-')[1]
        data[i].propName = parseFloat(a).toFixed(2) + " - " + parseFloat(b).toFixed(2);
        // console.log(data[i][propName])
      }
    }
    // }
    // console.log(data)
    return data;
  }
  onClickGrid() {
    this.isShowDataGrid = true;
    this.isShowChart = false;
  }
  onClickChart() {
    this.isShowDataGrid = false;
    this.isShowChart = true;
  }
  closeHistogram() {
    this.showHistogramModel = false
  }
  onAttrRowDown(index) {
    var rowTempIndex = this.attributes[index];
    var rowTempIndexPlus = this.attributes[index + 1];
    this.attributes[index] = rowTempIndexPlus;
    this.attributes[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }

  onAttrRowUp(index) {
    var rowTempIndex = this.attributes[index];
    var rowTempIndexMines = this.attributes[index - 1];
    this.attributes[index] = rowTempIndexMines;
    this.attributes[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }
  dragStart(event, data) {
    console.log(event)
    console.log(data)
    this.dragIndex = data
  }
  dragEnd(event) {
    console.log(event)
  }
  drop(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data
      // console.log(event)
      // console.log(data)
      var item = this.attributes[this.dragIndex]
      this.attributes.splice(this.dragIndex, 1)
      this.attributes.splice(this.dropIndex, 0, item)
      this.iSSubmitEnable = true
    }
  }
}
