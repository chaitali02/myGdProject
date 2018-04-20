import { Component, OnInit } from '@angular/core';
import { DatePipe, Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';


@Component({
  selector: 'app-dqExec',
  templateUrl: './dqExec.template.html',
  styleUrls: []
})
export class DqExecComponent implements OnInit {

  breadcrumbDataFrom: any;
  id : any;
  version : any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode : any;
  dqResultData : any;
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
  refKeyList : any;
  results : any;
  exec : any;
  showResultModel : any;

  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService){

    this.showResultModel = true;
    this.dqResultData = {};
    this.refKeyList = [];
    this.breadcrumbDataFrom=[{
        "caption":"Job Monitoring ",
        "routeurl":"/app/jobMonitoring"
      },
      {
        "caption":"Data Quality",
        "routeurl":"/app/list/dqExec"
  
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
        this.dqResultData.active = 'Y';
      }
      else {
        this.dqResultData.active = 'N';
      }
    }

    onChangePublished(event) {
      if(event === true) {
        this.dqResultData.published = 'Y';
      }
      else {
        this.dqResultData.published = 'N';
      }
    }
    

    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'dqexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }

    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('dqexec',this.id)
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
    this.dqResultData=response
    this.createdBy=this.dqResultData.createdBy.ref.name;
    this.dependsOn=this.dqResultData.dependsOn.ref.name;

   
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("+0530", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
    this.statusList = statusList
  
    let refKeyListObj = [];
    for(let i=0;i<response.refKeyList.length;i++){

      let ref = {};
        ref["type"] = response.refKeyList[i].type;
        ref["uuid"] = response.refKeyList[i].uuid;
        ref["name"] = response.refKeyList[i].name;
        
        refKeyListObj[i] = ref["type"]+"-"+ref["name"];        
    }
   
    this.refKeyList =refKeyListObj;

    this.published = response['published'];
    if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags'];
 
    this.breadcrumbDataFrom[2].caption=this.dqResultData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'dqexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }


  public goBack() {
    this._location.back();
  }
}
