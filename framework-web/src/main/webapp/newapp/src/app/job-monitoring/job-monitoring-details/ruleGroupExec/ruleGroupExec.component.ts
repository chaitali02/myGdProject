import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';

import { AppMetadata } from '../../../app.metadata'

@Component({
  selector: 'app-ruleGroupExec',
  templateUrl: './ruleGroupExec.template.html',
  styleUrls: []
})
export class RuleGroupExecComponent implements OnInit {

  breadcrumbDataFrom: any;
  id : any;
  version : any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode : any;
  rulegroupResultData : any;
  uuid : any;
  name : any;
  createdBy : any;
  createdOn : any;
  tags : any;
  desc : any;
  active : any;
  published : any;
  statusList : any;
  dependsOn : any;
  execList : any;
  results : any;
  showResultModel : any;
  routerUrl : any;

  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig,public metaconfig: AppMetadata, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService){
    this.showResultModel = true;
    this.rulegroupResultData = {};
    this.execList = [];
    this.breadcrumbDataFrom=[{
        "caption":"Job Monitoring ",
        "routeurl":"/app/jobMonitoring"
      },
      {
        "caption":"Rule Group",
        "routeurl":"/app/list/rulegroupExec"
  
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

    onChangeActive(event) {
      if(event === true) {
        this.rulegroupResultData.active = 'Y';
      }
      else {
        this.rulegroupResultData.active = 'N';
      }
    }

    onChangePublish(event) {
      if(event === true) {
        this.rulegroupResultData.published = 'Y';
      }
      else {
        this.rulegroupResultData.published = 'N';
      }
    }
    
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'rulegroupExec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('rulegroupExec',this.id)
      .subscribe(
      response =>{
        this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log("Error :: " + error));
    }
    onSuccessgetOneByUuidAndVersion(response){

    this.rulegroupResultData=response
    this.createdBy=this.rulegroupResultData.createdBy.ref.name;
    this.dependsOn=this.rulegroupResultData.dependsOn.ref.name;

   
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList

    let execListObj = [];
    for(let i=0;i<response.execList.length;i++){

      let ref = {};
        ref["type"] = response.execList[i].ref.type;
        ref["uuid"] = response.execList[i].ref.uuid;
        ref["name"] = response.execList[i].ref.name;
        ref["version"] = response.execList[i].ref.version;
        
        execListObj[i] = ref;        
    }
   
    this.execList =execListObj;

    this.published = response['published'];
    if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];
 
    this.breadcrumbDataFrom[2].caption=this.rulegroupResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'rulegroupexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }
  public goBack() {
    this._location.back();
  }

  showDetails(item){
    console.log('function called');
    // var innerType = innerData.operators[0].operatorInfo.ref.type;
    // var innerUuid = innerData.operators[0].operatorInfo.ref.uuid;
    // var innerVersion = innerData.operators[0].operatorInfo.ref.version;


    this.routerUrl=this.metaconfig.getMetadataDefs(item.type)['detailState'];

    this.router.navigate(['../../../../../JobMonitoring',item.type,item.uuid, item.version,'true'],{ relativeTo: this.activatedRoute });

  }
}
