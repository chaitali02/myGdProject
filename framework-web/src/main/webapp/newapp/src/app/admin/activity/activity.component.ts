import { Component,OnInit }from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Version } from '../../shared/version';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.template.html',
  styleUrls: ['./activity.component.css']
})
export class ActivityComponent implements OnInit {

  breadcrumbDataFrom : any;
  showActivity : any;
  activity : any;
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
  depends: any;
  allName : any;
  status : any;
  userInfo : any;
  sessionInfo : any;
  requestUrl : any;
  metaInfo : any;
  metaInfoName : any;
  metaInfoType : any;
  msgs : any;
  isSubmitEnable: any;
  

  constructor(private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) { 
    this.showActivity = true;
    this.activity = {};
    this.activity["active"]=true
    this.isSubmitEnable=true; 
    this.metaInfo={};
    this.sessionInfo={};
    this.userInfo={};

    this.breadcrumbDataFrom=[{
      "caption":"Admin",
      "routeurl":"/app/list/activity"
    },
    {
      "caption":"Activity",
      "routeurl":"/app/list/activity"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
      
    })
  }

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'activity')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('activity',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
  
  onSuccessgetOneByUuidAndVersion(response){
    this.breadcrumbDataFrom[2].caption=response.name
    this.activity=response;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    this.createdBy=this.activity.createdBy.ref.name;
    this.activity.published=response["published"] == 'Y' ? true : false
    this.activity.active=response["active"] == 'Y' ? true : false
    if(response.userInfo != null){
    this.userInfo=response.userInfo.ref.name;
    }
    else{
      this.userInfo="";
    }
    if(response.sessionInfo != null){
    this.sessionInfo=response.sessionInfo.ref.name;
    }
    else{
      this.sessionInfo="";
    }
    if(response.metaInfo != null){
    this.metaInfoType=response.metaInfo.ref.type;
    this.metaInfoName=response.metaInfo.ref.name;
    }
    else{
      this.metaInfo="";
    }
    this.version = response['version'];
    
    this.breadcrumbDataFrom[2].caption=this.activity.name;
    console.log('Data is' + response);
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'activity')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  submitActivity(){
  this.isSubmitEnable=true; 
  let activityJson={};
  activityJson["uuid"]=this.activity.uuid
  activityJson["name"]=this.activity.name
 //let tagArray=[];
 const tagstemp = [];
 for (const t in this.tags) {
  tagstemp.push(this.tags[t]["value"]);
 }
// if(this.tags.length > 0){
//   for(let counttag=0;counttag < this.tags.length;counttag++){
//     tagArray[counttag]=this.tags[counttag]["value"];
//   }
// }
 activityJson["tags"]=tagstemp
 activityJson["desc"]=this.activity.desc
 //activityJson["createdBy"]=this.activity.createdBy
 activityJson["createdOn"]=this.activity.createdOn
 activityJson["active"]=this.activity.active == true ?'Y' :"N"
 activityJson["published"]=this.activity.published == true ?'Y' :"N"
 activityJson["status"]=this.activity.status
 let userInfo={};
 let refUserInfo={};
 refUserInfo["name"]=this.userInfo;
 userInfo["ref"]=refUserInfo;
 activityJson["userInfo"]=userInfo;

 let sessionInfo={};
 let refSessionInfo={};
 refSessionInfo["name"]=this.sessionInfo;
 sessionInfo["ref"]=refSessionInfo;
 activityJson["sessionInfo"]=sessionInfo;

 activityJson["requestUrl"]=this.activity.requestUrl

//  let dependsOn={};
//  let ref={}
//  ref["type"]=this.depends
//  ref["uuid"]=this.dependsOn.uuid
//  dependsOn["ref"]=ref;
//  filterJson["dependsOn"] = dependsOn;

 let metaInfo={};
 let refMetaInfo={}
 refMetaInfo["type"]=this.metaInfoType
 refMetaInfo["name"]=this.metaInfoName
 metaInfo["ref"]=refMetaInfo;
 activityJson["metaInfo"] = metaInfo;
debugger
 console.log(JSON.stringify(activityJson))
 this._commonService.submit("activity",activityJson).subscribe(
   response => { this.OnSuccessubmit(response)},
   error => console.log('Error :: ' + error)
 )
 
}

OnSuccessubmit(response){    
  this.isSubmitEnable=true;   
  this.msgs = [];
  this.msgs.push({severity:'success', summary:'Success Message', detail:'Activity Submitted Successfully'});
  this.goBack()
  console.log('final response is'+response);
}

public goBack() {
    this._location.back();  
}
}