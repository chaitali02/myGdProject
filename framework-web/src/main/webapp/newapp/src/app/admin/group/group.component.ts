import { Location } from '@angular/common';
import { Component,OnInit, group }from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';

@Component({
  selector: 'app-group',
  templateUrl: './group.template.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit {

  breadcrumbDataFrom : any;
  showGroup : any;
  group : any;
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
  roleInfoArray : any;
  roleInfoTags : any;
  roleInfo : any;
  dropdownSettingsRole : any;
  roleResponse : any;
  msgs : any;
  booleanRoleInfo : any; 
  roleId : any;
  groupInfoArray : any;
  groupInfoTags : any;
  dropdownSettingsGroup : any;
  appIdArray : any;
  appId : any;
  responseRole : any;
  responseAppli : any;
  roleIdArray : any;
  isSubmitEnable:any;
  constructor(private _location : Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) { 
    this.showGroup = true;
    this.group = {};
    this.group["active"]=true
    this.isSubmitEnable=true;
    this.roleResponse = null;
    this.roleInfoTags=null;
    this.breadcrumbDataFrom=[{
      "caption":"Admin",
      "routeurl":"/app/list/group"
    },
    {
      "caption":"Group",
      "routeurl":"/app/list/group"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
    this.dropdownSettingsRole = { 
      singleSelection: false, 
      selectAllText:'Select All',
      unSelectAllText:'UnSelect All',
      enableSearchFilter: true,
      disabled : false
      
    };
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      
      if(this.mode !== undefined) { 
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
      this.dropdownSettingsRole.disabled = this.mode =="false" ? false :true
      }this.getAllLatestRole();
      this.getAllLatestAppli();
    })
  }

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'group')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('group',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
    
  onSuccessgetOneByUuidAndVersion(response){
    this.breadcrumbDataFrom[2].caption=response.name;
    this.group=response;
    this.uuid=response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    this.createdBy=response.createdBy.ref.name;
    this.group.published=response["published"] == 'Y' ? true : false
    this.group.active=response["active"] == 'Y' ? true : false
    this.version = response['version'];
    this.roleId = response.roleId.ref.name;
    this.appId = response.appId.ref.name;

    let roleInfoNew = [];
    
    for(const i in response.roleInfo)
    {
      let roletag={};
      roletag["id"]=response.roleInfo[i].ref.uuid;
      roletag["itemName"]=response.roleInfo[i].ref.name;
      roleInfoNew[i]=roletag;
    }
      this.roleInfoTags=roleInfoNew;
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

  getAllLatestRole(){
    this._commonService.getAllLatest('role')
    .subscribe(
      response =>{
        this.onSuccessgetAllLatestRole(response)},
        error => console.log("Error ::"+error));  
  }

  onSuccessgetAllLatestRole(response){
    this.responseRole = response;
    console.log('getAllLatestRole is start');
    this.roleResponse=response
    this.roleInfoArray=[];
    
     for(const i in response){
       let roleref = {};
       roleref["id"]=response[i]['uuid'];
       roleref["itemName"]=response[i]['name'];
       roleref["version"]=response[i]['version'];

       this.roleInfoArray[i]=roleref;
     }
     console.log(this.roleInfoArray);

     this.roleIdArray=[];
     for(const j in response){
      let rolerefObj = {};
      rolerefObj["label"]=response[j]['name'];
      rolerefObj["value"]={}
      rolerefObj["value"]["label"]=response[j]['name'];
      rolerefObj["value"]["name"]=response[j]['name'];

      this.roleIdArray[j]=rolerefObj;
    }

     console.log('getAllLatestRole is end');
     console.log(this.roleIdArray);
  }

  getAllLatestAppli()
  {
    this._commonService.getAllLatest('application')
    .subscribe(
      response =>{
        this.onSuccessgetAllLatestAppli(response)},
        error => console.log("Error ::"+error)); 
  }

  onSuccessgetAllLatestAppli(response){
    this.responseAppli = response;
    console.log('getAllLatestAppli is start');
    
    this.appIdArray=[];
    
     for(const i in response){
       let appref = {};
       appref["label"]=response[i]['name'];
       appref["value"]={}
       appref["value"]["label"]=response[i]['name'];
       appref["value"]["name"]=response[i]['name'];

       this.appIdArray[i]=appref;
     }
     console.log('getAllLatestAppli is end');
     console.log(this.appIdArray);
  }
  onVersionChange(){ 
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'group')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.group.active = 'Y';
    }
    else {
      this.group.active = 'N';
    }
  }

  onChangePublished(event) {
    if(event === true) {
      this.group.published = 'Y';
    }
    else {
      this.group.published = 'N';
    }
  }

  onItemSelect(item:any){
    console.log(item);
   // console.log(this.selectedItems);
  }
  OnItemDeSelect(item:any){
    console.log(item);
   // console.log(this.selectedItems);
  }
  onSelectAll(items: any){
    console.log(items);
  }
  onDeSelectAll(items: any){
    console.log(items);
  }

  submitGroup(){
    let groupJson={};
    groupJson["uuid"]=this.group.uuid;
    groupJson["name"]=this.group.name;
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
  groupJson["tags"]=tagstemp;
  groupJson["desc"]=this.group.desc;

   let roleInfoArrayNew=[]; 
    if(this.roleInfoTags != null)
   {
     for(const c in this.roleInfoTags)
     {
        let roleInfoRef = {};
        let roleRef={};
        roleInfoRef["uuid"]=this.roleInfoTags[c].id;
        roleInfoRef["type"]="role";
        roleRef["ref"]=roleInfoRef;
        roleInfoArrayNew.push(roleRef);
     }
     
    }
    groupJson["roleInfo"]=roleInfoArrayNew;

       let roleId = {};
       let roleRef ={};
       roleRef["uuid"]=this.roleId.uuid;
       roleRef["type"]="role";
       roleRef["name"]=this.roleId;
       roleId["ref"]=roleRef;
   
    groupJson["roleId"]=roleId;

    // let metaId={};
    // let refMetaId={}
    // refMetaId["type"]=this.meta
    // refMetaId["uuid"]=this.metaId.uuid
    // refMetaId["name"]=this.metaId.name
    // metaId["ref"]=refMetaId;
    // datastoreJson["metaId"] = metaId;
      let appId = {};
      let appRef ={};
      appRef["uuid"]=this.appId.uuid;
      appRef["type"]="role";
      appRef["name"]=this.appId;
      appId["ref"]=appRef;

    groupJson["appId"]=appId;

    group["active"]=this.group.active == true ?'Y' :"N"
    groupJson["published"]=this.group.published == true ?'Y' :"N"  
    this.isSubmitEnable=true; 
  
    
      console.log(JSON.stringify(groupJson));
      this._commonService.submit("group",groupJson).subscribe(
        response => { this.OnSuccessubmit(response)},
        error => console.log('Error :: ' + error)
      )  
    } 
    OnSuccessubmit(response){
      this.isSubmitEnable=true;
      this.msgs = [];
      this.msgs.push({severity:'success', summary:'Success Message', detail:'Group Submitted Successfully'});
      setTimeout(() => {
        this.goBack()
        }, 1000);     
      }
     
     public goBack() {
      //this._location.back();
      this.router.navigate(['app/list/group']);
        
    }
    enableEdit(uuid, version) {
      this.router.navigate(['app/admin/group',uuid,version, 'false']);
      this.dropdownSettingsRole = { 
        singleSelection: false, 
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        disabled : false
        
      };
      
    }
    showview(uuid, version) {
      this.router.navigate(['app/admin/group',uuid,version, 'true']);
      this.dropdownSettingsRole = { 
        singleSelection: false, 
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        disabled : true
        
      };
    }
  
}
