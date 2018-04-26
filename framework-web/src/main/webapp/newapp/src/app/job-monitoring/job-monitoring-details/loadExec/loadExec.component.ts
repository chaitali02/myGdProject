import { NgModule, Component, ViewEncapsulation, Input } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { AppConfig } from '../../../app.config';
import { GridOptions } from 'ag-grid/main';
import {Message} from 'primeng/components/common/api';
import {MessageService} from 'primeng/components/common/messageservice';
import{CommonService} from '../../../metadata/services/common.service'
import { Location } from '@angular/common';
import{ Version } from '../../../shared/version'
import { SelectItem } from 'primeng/primeng';
import{ DependsOn } from './dependsOn'
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-loadExec',
  styleUrls: [],
  templateUrl: './loadExec.template.html',
  
})
export class LoadExecComponent {
  statusList: any[];
  dependsOn: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  selectVersion: any;
  append: any;
  header: any;
  allNames: SelectItem[] = [];
  targets: DependsOn
  msgs: Message[] = [];
  LoadSourceType: any;
  selectedAllFitlerRow: boolean;
  loadData: any;
  allName: any;
  versions: any[];
  showLoad: boolean;
  active: any;
  published: any;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  loadSourceType:any
  breadcrumbDataFrom:any;
  source:any
  LoadTargetType:any;
  loadTargetType:any;

  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
  this.loadData={};
  this.targets={'uuid':"","label":""}
  this.selectVersion={"version":""};
    this.showLoad = true;
    this.breadcrumbDataFrom=[{
      "caption":"Job Monitoring ",
      "routeurl":"/app/jobMonitoring"
    },
    {
      "caption":"Load",
      "routeurl":"/app/list/loadExec"

    },
    {
      "caption":"",
      "routeurl":null

    }
    ]
    
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    }); 
    if(this.mode !== undefined) {   
      this.getOneByUuidAndVersion(this.id,this.version)
      this.getAllVersionByUuid()
      
    }
  }
  public goBack() {
    this._location.back();
  }

  getOneByUuidAndVersion(id,version){
    this._commonService.getOneByUuidAndVersion(id,version,'loadexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('loadexec',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }


  onSuccessgetOneByUuidAndVersion(response){
    this.loadData=response
    this.createdBy=this.loadData.createdBy.ref.name;
    this.dependsOn=this.loadData.dependsOn.ref.name;
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList
    this.published = response['published'];
    if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];
 
    this.breadcrumbDataFrom[2].caption=this.loadData.name;
  }
  OnSuccesgetAllVersionByUuid(response) {
    var temp=[]
    for (const i in response) {
      let ver={};
      ver["label"]=response[i]['version'];
      ver["value"]={};
      ver["value"]["label"]=response[i]['version'];      
      ver["value"]["uuid"]=response[i]['uuid']; 
      temp[i]=ver;
    }
    this.VersionList=temp
  }
  onVersionChange(){ 
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'loadexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.loadData.active = 'Y';
    }
    else {
      this.loadData.active = 'N';
    }
  }
  onChangePublish(event) {
    if(event === true) {
      this.loadData.published = 'Y';
    }
    else {
      this.loadData.published = 'N';
    }
  }
  
}


