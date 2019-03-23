import { NgModule, Component, ViewEncapsulation, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { count, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';

import { AppConfig } from '../../app.config';
import { AppMetadata } from '../../app.metadata';
import { AppHelper } from '../../app.helper';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

import { CommonService } from '../../metadata/services/common.service';
import { DatapodService } from '../../metadata/services/datapod.service';

import { DatapodAttributeIO } from '../../metadata/domainIO/domain.datapodAttributeIO';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { DatapodIO } from '../../metadata/domainIO/domain.datapodIO';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { Datapod } from '../../metadata/domain/domain.datapod';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { Attribute } from '../../metadata/domain/domain.attribute';
import { RoutesParam } from '../../metadata/domain/domain.routeParams';
import { MetadataIO } from '../../metadata/domainIO/domain.metadataIO';
import { MetaType } from '../../metadata/enums/metaType';

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
  graphDataStatus: any;
  showGraph: any;

  versions: any;
  mode: any;
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
  download: { "format": any; "rows": any; "selectFormat": string; };

  isEdit: boolean = false;
  isversionEnable: boolean = false;
  isAdd: boolean = false;
  showForm: boolean;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  attrUnitTypeArray: { 'value': String; 'label': String; }[];

  moveToEnable: boolean;
  count: any[];
  txtQueryChangedFilter: Subject<string> = new Subject<string>();
  resetTableTopBottom: Subject<string> = new Subject<string>();
  topDisabled: boolean;
  bottomDisabled: boolean;
  invalideMinRow: boolean = false;
  invalideMaxRow: boolean = false;
  moveTo: number;
  metaType = MetaType;

  constructor(private _config: AppConfig, public metaconfig: AppMetadata, public appHelper: AppHelper, private http: Http, private _commonService: CommonService, private _datapodService: DatapodService, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private route: ActivatedRoute) {
    this.baseUrl = config.getBaseUrl();
    this.selectVersion = { "version": "" };
    this.showdatapod = true;
    this.isSubmitEnable = true;
    this.showGraph = false;
    this.uuid = '';
    this.download = {"format" :"","rows":"","selectFormat":""}
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

    this.showForm = true

    this.moveToEnable = false;
    this.count = [];
    
    this.txtQueryChangedFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.attributes) {
          if (this.attributes[i].hasOwnProperty("selected"))
            this.attributes[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.resetTableTopBottom
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });
    this.invalideMinRow = false;
    this.invalideMaxRow = false;
    this.topDisabled = false;
    this.bottomDisabled = false;
  }

  ngOnInit() {
    this.datapodjson = {};
    this.active = true;
    this.published = false;
    this.cache = true;
    this.selectallattribute = false;
    this.attrtypes = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
    this.attrUnitTypeArray = [
      { "value": "*", "label": "* Text" },
      { "value": "#", "label": "# Number" },
      { "value": "$", "label": "$ Currency" },
      { "value": "%", "label": "% Percent" }
    ]
    this.datasourceType = [
      { 'value': 'FILE', name: 'file' },
      { 'value': 'HIVE', name: 'hive' },
      { 'value': 'IMPALA', name: 'impala' },
      { 'value': 'MYSQL', name: 'mysql' },
      { 'value': 'POSTGRES', name: 'postgres' }
    ];
    this.selectdatasourceType = this.datasourceType[0].value

    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;

      if (this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
      }
      else {
        this.selectType(this.selectdatasourceType);
        this.isEditInprogess = false;
        this.isEditError = false;
      }
      this.setMode(this.mode);
    });
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

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.datapodjson.name;
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
    this.showForm = true
    this.isShowCompareMetaData = false
    this.showdatapod = false
    this.isShowSimpleData = false;
    this.isShowDatastore = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    this.showForm = false;
    this.isShowCompareMetaData = false
    this.showdatapod = false
    this.isShowSimpleData = false;
    this.isShowDatastore = false;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/datapod',]);
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.DATAPOD, this.id)
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

    this.isEditInprogess = true;
    this._datapodService.getOneByUuidAndVersion(id, version, this.metaType.DATAPOD)
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
    this._datapodService.getLatestDataSourceByUuid(response.datapoddata.datasource.ref.uuid, response.datapoddata.datasource.ref.type)
      .subscribe(
        response => {
          this.OnSuccesLatestDatasourceByUuid(response)
        },
        error => console.log("Error :: " + error));
        this.isEditInprogess = false;
  }
  OnSuccesLatestDatasourceByUuid(response: any) {
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
    this.graphDataStatus = false;
    this.showGraph = false;
    this.IsTableShow = false;
    this.isShowDatastore = false
    this.isShowCompareMetaData = false
    this.showForm = false;
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
      var attribute = {"field":"","header":"","colwidth":"","name":"","dname":"","uuid":"","version":"","attributeId":""};
      attribute.field = this.datapodjson.attributes[j].name;
      attribute.header = this.datapodjson.attributes[j].name;
      attribute.colwidth = ((attribute.header.length * 9) + 40) + "px"
      attribute.name = this.datapodjson.attributes[j].name;
      attribute.dname = this.datapodjson.name;
      attribute.uuid = this.datapodjson.uuid;
      attribute.version = this.datapodjson.version;
      attribute.attributeId = this.datapodjson.attributes[j].attributeId;

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
    let datapod = new Datapod();
    this.isSubmitEnable = true;
    datapod.uuid = this.datapodjson.uuid;
    datapod.name = this.datapodjson.name;
    datapod.desc = this.datapodjson.desc;
    datapod.active = this.active == true ? "Y" : "N";
    datapod.published = this.published == true ? "Y" : "N";
    datapod.cache = this.cache == true ? "Y" : "N";

    datapod.tags = this.tags
    let datasource = new MetaIdentifierHolder();
    let ref = new MetaIdentifier();
    ref.type = "datasource";
    ref.uuid = this.datasourceName.uuid;
    datasource.ref = ref;
    datapod.datasource = datasource;
    let attributesArray = [];
    let count = 1;
    if (this.attributes.length > 0) {
      for (let i = 0; i < this.attributes.length; i++) {
        let attribute = new Attribute();
        if (this.attributes[i].key == true) {
          attribute.key = count;
          count = count + 1;
        }
        else {
          attribute.key = null;
        }
        attribute.partition = this.attributes[i].partition == true ? "Y" : "N";
        attribute.active = this.attributes[i].active == true ? "Y" : "N";
        attribute.dispName = this.attributes[i].dispName;
        attribute.type = this.attributes[i].type;
        attribute.name = this.attributes[i].name;
        attribute.length = this.attributes[i].length;
        attribute.attrUnitType = this.attributes[i].attrUnitType;
        attribute.desc = this.attributes[i].desc;
        attributesArray[i] = attribute;
      }
    }
    datapod.attributes = attributesArray;
    console.log(JSON.stringify(datapod));
    this._commonService.submit(this.metaType.DATAPOD, datapod).subscribe(
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

  showDatapodPage() {
    this.isShowCompareMetaData = false
    this.showdatapod = true;
    this.isShowSimpleData = false;
    this.graphDataStatus = false;
    this.showGraph = false
    this.isShowDatastore = false
    this.showgetResults = false
  }

  showDatapodGraph() {
    this.isShowCompareMetaData = false;
    this.showdatapod = false;
    this.isShowSimpleData = false;
    this.graphDataStatus = true;
    this.showGraph = true;
    this.isShowDatastore = false;
    this.showgetResults = false;
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
    this.showForm = false;
    this._datapodService.getDatastoreByDatapod(data, this.metaType.DATAPOD).subscribe(
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
    this.isShowSimpleData = false;
    this.graphDataStatus = false;
    this.showGraph = false;
    this.isShowDatastore = false
    this.showgetResults = false
    this.showForm = false;
    this._datapodService.compareMetadata(data.uuid, data.version, this.metaType.DATAPOD).subscribe(
      response => { this.OnSuccescompareMetadata(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccescompareMetadata(response) {
    this.IsTableShow = true
    for (let i = 0; i < response.length; i++) {
      if (response[i].status != null) {
        let status = response[i].status;

        let metadata = new MetadataIO();
        metadata = this.metaconfig.getStatusDefs(status);

        let count;
        response[i].status = {};
        response[i].status.value = metadata.caption
        //  response[i]["status"].stage = this.appHelper.getStatus(status)["stage"];
        response[i].status.color = metadata.color;
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
    this._datapodService.synchronizeMetadata(data.uuid, data.version, this.metaType.DATAPOD).subscribe(
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
    this._datapodService.getAttrHistogram(row.uuid, row.version, this.metaType.DATAPOD, row.attributeId).subscribe(
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
      var item = this.attributes[this.dragIndex]
      this.attributes.splice(this.dragIndex, 1)
      this.attributes.splice(this.dropIndex, 0, item)
      this.iSSubmitEnable = true
    }
  }

  updateArray(new_index, range, event) {
    for (let i = 0; i < this.attributes.length; i++) {
      if (this.attributes[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.attributes.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.attributes, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.checkSelected(false, null);
            }
            if (new_index == this.attributes.length - 1) {
              this.checkSelected(false, null);
            }
            this.txtQueryChangedFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.attributes[new_index].selected = "";
            this.checkSelected(false, null);
          }
          else if (new_index == this.attributes.length - 1) {
            this.attributes[new_index].selected = "";
            this.checkSelected(false, null);
          }
          break;
        }
      }
    }
  }

  array_move(arr, old_index, new_index) {
    while (old_index < 0) {
      old_index += arr.length;
    }
    while (new_index < 0) {
      new_index += arr.length;
    }
    if (new_index >= arr.length) {
      var k = new_index - arr.length + 1;
      while (k--) {
        arr.push(undefined);
      }
    }
    arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
    return arr;
  }

  checkSelected(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
    }
    else {
      this.count.pop();
    }
    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.attributes.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }

  ngOnDestroy() {
    this.txtQueryChangedFilter.unsubscribe();
    this.resetTableTopBottom.unsubscribe();
  }
}
