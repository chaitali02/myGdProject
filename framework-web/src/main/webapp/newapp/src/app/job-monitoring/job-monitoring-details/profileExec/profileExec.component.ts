import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';


@Component({
  selector: 'app-profileExec',
  templateUrl: './profileExec.template.html',
  styleUrls: []
})
export class ProfileExecComponent implements OnInit {

  breadcrumbDataFrom: any;
  id : any;
  version : any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode : any;
  profileResultData : any;
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
  exec : any;
  results : any;
  showResultModel : any;

  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService){
    this.showResultModel = true;
    this.profileResultData = {};
    this.breadcrumbDataFrom=[{
        "caption":"Job Monitoring ",
        "routeurl":"/app/jobMonitoring"
      },
      {
        "caption":"Profile",
        "routeurl":"/app/list/profileExec"
  
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
        this.profileResultData.active = 'Y';
      }
      else {
        this.profileResultData.active = 'N';
      }
    }

    onChangePublished(event) {
      if(event === true) {
        this.profileResultData.published = 'Y';
      }
      else {
        this.profileResultData.published = 'N';
      }
    }
    
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'profileexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('profileexec',this.id)
      .subscribe(
      response =>{
        this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log("Error :: " + error));
    }

    onSuccessgetOneByUuidAndVersion(response){
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version  
    this.profileResultData=response
    this.createdBy=this.profileResultData.createdBy.ref.name;
    this.dependsOn=this.profileResultData.dependsOn.ref.name;
    if(this.profileResultData.result !== null){
    this.results=this.profileResultData.result.ref.name;
    }

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
 
    this.breadcrumbDataFrom[2].caption=this.profileResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'profileexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  public goBack() {
    this._location.back();
  }
}
