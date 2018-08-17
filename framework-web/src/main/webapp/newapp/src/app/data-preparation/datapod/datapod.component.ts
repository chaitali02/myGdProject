// import { version } from './../../../../../../../../target/framework/app/bower_components/moment/moment.d';
import { NgModule, Component, ViewEncapsulation, Input } from '@angular/core';
import { MetaDataDataPodService } from './datapod.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { count } from 'rxjs/operators';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';
import { DataPodResource } from './datapod-resource';

import{ Version } from '../../shared/version'

import { CommonService } from '../../metadata/services/common.service';
import { DatapodService } from '../../metadata/services/datapod.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-data-preparation',
  styleUrls: [],
  templateUrl: './datapod.template.html'
})
export class DatapodComponent {
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
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  datapodjson: any;
  
  baseUrl: any;
  locations: any;
  id: any;
  version: any;
  datasource: any;
  isDataError : any;
  isShowSimpleData : any;
  isDataInpogress : any;
  tableclass : any;
  showdatapod : any;
  showgraph : any;
  graphDataStatus : any;
  showgraphdiv : any;
  
  versions : any;
  mode : string;
  datasource1 : any;
  cache :any;
  uuid : any;
  detasource_uuid : any;
  tags : any;
  createdBy : any;
  attributes : any;
  published : any;
  active : any;
  iseditable : boolean;
  datasource_name : any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  isSubmitEnable:any;

  constructor(private _config : AppConfig, private http : Http,private _commonService:CommonService,private _datapodService:DatapodService,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _service: MetaDataDataPodService,private route: ActivatedRoute) {
    this.baseUrl = config.getBaseUrl();
    this.selectVersion={"version":""};
    this.showdatapod = true;
    this.isSubmitEnable=true;
    this.uuid = '';
    this.breadcrumbDataFrom=[{
      "caption":"Data Preparation ",
      "routeurl":"/app/list/datapod"
    },
    {
      "caption":"Datapod",
      "routeurl":"/app/list/datapod"

    },
    {
      "caption":"",
      "routeurl":null

    }
    ]
  }

  ngOnInit() {
    this.datapodjson={};
    this.active = true;
    this.published = false;
    this.cache = true;
    this.selectallattribute=false
    this.attrtypes = ["string", "float","bigint",'double','timestamp','integer'];
    this.datasourceType = [{'value': 'FILE', name: 'file'},{'value': 'HIVE', name: 'hive'},
    {'value': 'IMPALA', name: 'impala'},{'value': 'MYSQL', name: 'mysql'},{'value': 'POSTGRES', name: 'postgres'}];
    this.selectdatasourceType= this.datasourceType[0].value
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if(this.mode !== undefined) {
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id,this.version);
       }
      else {
        this.selectType(this.selectdatasourceType); 
      }
    });
  }
  
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/datapod',]);
    }
  
  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('datapod',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver={};
      ver["label"]=response[i]['version'];
      ver["value"]={};
      ver["value"]["label"]=response[i]['version'];      
      ver["value"]["uuid"]=response[i]['uuid']; 
      this.VersionList[i]=ver;
    }
  }  
  onVersionChange(){
    this.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label);
  }

  getOneByUuidAndVersion(id,version){
    this._datapodService.getOneByUuidAndVersion(id,version,'datapod')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onSuccessgetOneByUuidAndVersion(response){
    
    this.breadcrumbDataFrom[2].caption=response.datapoddata.name;
    this.uuid=response.datapoddata.uuid
    this.datapodjson=response.datapoddata;
    const version: Version = new Version();
    version.label = response.datapoddata['version'];
    version.uuid = response.datapoddata['uuid'];
    this.selectedVersion=version
    this.createdBy = response.datapoddata['createdBy']['ref']['name'];
    var tags = [];
    if (response.datapoddata.tags != null) {
      for (var i = 0; i < response.datapoddata.tags.length; i++) {
        var tag = {};
        tag['value'] = response.datapoddata.tags[i];
        tag['display'] = response.datapoddata.tags[i];
        tags[i] = tag
        
              }//End For
              this.datapodjson.tags = tags;
    }//End If

    this.attributes = response.attributes;
    this.locations = response.datapoddata;
    this.detasource_uuid = response.datapoddata['datasource']['ref']['uuid'];
    this.cache = response.datapoddata['cache'];
    if(this.cache === 'Y' ) { this.cache = true; } else { this.cache = false; }
    this.published = response.datapoddata['published'];
    if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response.datapoddata['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];
    this.selectdatasourceType = (response.datapoddata['datasource']['ref']['name']) .toUpperCase();
    this.selectType(this.selectdatasourceType);   
  }
 
  showDatapodSampleTable(data) {
    this.isDataError = false;
    this.isShowSimpleData = true;
    this.isDataInpogress = true;
    this.tableclass = 'centercontent';
    this.showdatapod = false;
    this.showgraph = false;
    this.graphDataStatus = false;
    this.showgraphdiv = false;
    const api_url = this.baseUrl + 'datapod/getDatapodSample?action=view&datapodUUID=' + data.uuid + '&datapodVersion=' + data.version + '&row=100';
    const DatapodSampleData = this._service.getDatapodSample(api_url).subscribe(
      response => { this.OnSuccesDatapodSample(response)},
      error => {
        this.IsTableShow=true; 
        console.log("Error :: " + error)
        this.IsError=true;   }
    )
  }

  OnSuccesDatapodSample(response) {
    this.IsTableShow=true;
    this.colsdata=response
    let columns=[];
    console.log(response)
    if(response.length && response.length > 0){
      Object.keys(response[0]).forEach(val=>{
      if (val != "rownum"){
        let width=((val.split("").length * 9)+20)+"px"
        columns.push({"field":val, "header":val,colwidth:width});
      }
    });
    }
    this.cols=columns
    this.columnOptions = [];
    for(let i = 0; i < this.cols.length; i++) {
      this.columnOptions.push({label: this.cols[i].header, value: this.cols[i]});
    }   
  }

  selectType(val) {
    this._datapodService.getDatasourceByType(val)
    .subscribe(
    response =>{
      this.OnSuccesDatasourceByType(response)},
    error => console.log("Error :: " + error));
  }

  OnSuccesDatasourceByType(response) {
    this.detasource_uuid = response[0]['uuid'];
    const datasource2: Array<DataPodResource> = [];
    for (const i in response) {
      datasource2.push(new DataPodResource(
        response[i]['uuid'], response[i]['name']
      ));
    }
    this.datasource1 = datasource2;
  }

  submitDatapod() {
    let datapod={};
    this.isSubmitEnable=true;
    datapod["uuid"]=this.datapodjson.uuid;
    datapod["name"]=this.datapodjson.name;
    datapod["desc"]=this.datapodjson.desc;
    datapod["active"]=this.active==true ?"Y":"N";
    datapod["published"]=this.published==true ?"Y":"N";
    datapod["cache"]=this.cache==true ?"Y":"N";
    var tagArray=[];
    if(this.datapodjson.tags !=null){
     for(var counttag=0;counttag<this.datapodjson.tags.length;counttag++){
      tagArray[counttag]=this.datapodjson.tags[counttag].value;
  
     }
     }
     datapod['tags'] = tagArray;
    let datasource={}
    let ref={};
    ref["type"]="datasource";
    ref["uuid"]=this.detasource_uuid;
    datasource["ref"]=ref;
    datapod["datasource"]=datasource;
    let attributesArray=[];
    let count=1;
    if(this.attributes.length >0){
      for(let i=0;i<this.attributes.length;i++){
        let attribute={};
        if(this.attributes[i]["key"] ==true){
          attribute["key"]=count;
          count=count+1;
        }
        else{
          attribute["key"]=null;
        }
        attribute["partition"]=this.attributes[i]["partition"]==true?"Y":"N";
        attribute["active"]=this.attributes[i]["active"] ==true?"Y":"N";
        attribute["dispName"]=this.attributes[i]["dispName"];
        attribute["type"]=this.attributes[i]["type"];
        attribute["name"]=this.attributes[i]["name"];
        attribute["desc"]=this.attributes[i]["desc"];
        attributesArray[i]=attribute;
      }
    }
    datapod["attributes"]=attributesArray;
    console.log(JSON.stringify(datapod));
    this._commonService.submit("datapod",datapod).subscribe(
      response => { this.OnSuccessubmit(response)},
      error => console.log('Error :: ' + error)
    )
  }
  
  OnSuccessubmit(response){
    this.isSubmitEnable=true;
    this.msgs = [];
    this.msgs.push({severity:'success', summary:'Success Message', detail:'Datapod Submitted Successfully'});
    setTimeout(() => {
      this.goBack()
       }, 1000);
  } 
  
  enableEdit(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/datapod',uuid,version, 'false']);
  }

  showview(uuid, version) {
    this.showDatapodPage();
    this.router.navigate(['app/dataPreparation/datapod',uuid,version, 'true']);
  }

  showDatapodPage() {
    this.showdatapod= true;
    this.isShowSimpleData= false;
    this.showgraph= false;
    this.graphDataStatus= false;
    this.showgraphdiv= false
  }

  showDatapodGraph() {
    this.showdatapod=false;
    this.showgraph=false;
    this.isShowSimpleData=false;
    this.graphDataStatus=true;
    this.showgraphdiv=true;
  }
 
  addRow(){
    if(this.attributes == null){
      this.attributes=[];
    }
    var len=this.attributes.length+1
    var filertable={};
    filertable["key"]=false;
    filertable["partition"]=false;
    filertable["active"]=true;
    filertable["dispName"]=" ";
    filertable["type"]=this.attrtypes[0];
    filertable["desc"]=" ";
    this.attributes.splice(this.attributes.length, 0,filertable);
  }
  removeRow(){
    let newDataList=[];
    this.selectallattribute=false;
    this.attributes.forEach(selected => {
      if(!selected.selected){
        newDataList.push(selected);
      }
    });
   this.attributes = newDataList;
  }
  checkAllAttributeRow(){
  
    if (!this.selectallattribute){
      this.selectallattribute = true;
      }
    else {
      this.selectallattribute = false;
      }
     this.attributes.forEach(attribute => {
      attribute.selected = this.selectallattribute;
    });
  }

  downloadDatapodResult(){
    const headers = new Headers();
    this.http.get(this.baseUrl+'datapod/download?action=view&datapodUUID=' + this.uuid + '&datapodVersion=' + this.version + '&row=100',
    { headers: headers, responseType: ResponseContentType.Blob })
    .toPromise()
    .then(response => this.saveToFileSystem(response));
  }

  saveToFileSystem(response){
    const contentDispositionHeader: string = response.headers.get('Content-Type');
    const parts: string[] = contentDispositionHeader.split(';');
    const filename = parts[1];
    const blob = new Blob([response._body], { type: 'application/vnd.ms-excel' });
    saveAs(blob, filename);
  }
}
