import { Location } from '@angular/common';
import { Component,OnInit }from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';

@Component({
  selector: 'app-role',
  templateUrl: './role.template.html',
  styleUrls: ['./role.component.css']
})
export class RoleComponent implements OnInit {

  breadcrumbDataFrom : any;
  showRole : any;
  role : any;
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
  privilegeInfoArray : any;
  privilegeInfoTags : any;
  privilegeInfo : any;
  dropdownSettingsPrivilege : any;
  privResponse : any;
  msgs : any;
  booleanPrivInfo : any; 
  isSubmitEnable:any;
  
  constructor(private _location : Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) { 
    this.privResponse = null;
    this.privilegeInfoTags=null;
  
    this.showRole = true;
    this.role = {};
    this.role["active"]=true
  
    this.breadcrumbDataFrom=[{
      "caption":"Admin",
      "routeurl":"/app/list/role"
    },
    {
      "caption":"Role",
      "routeurl":"/app/list/role"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
    this.dropdownSettingsPrivilege = { 
      singleSelection: false, 
      selectAllText:'Select All',
      unSelectAllText:'UnSelect All',
      enableSearchFilter: true,
      disabled :false
    };  
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];      

      if(this.mode !== undefined) { 
      this.getAllVersionByUuid();
      this.getOneByUuidAndVersion();
      this.dropdownSettingsPrivilege.disabled=this.mode =="false" ? false :true
      }this.getAllLatestPrivilege();
    })
  }

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'role')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('role',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
    
  onSuccessgetOneByUuidAndVersion(response){
    console.log('getOneByUuidAndVersion is start');
    this.role=response;
    this.uuid=response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    this.createdBy=response.createdBy.ref.name;
    this.role.published=response["published"] == 'Y' ? true : false
    this.role.active=response["active"] == 'Y' ? true : false
    this.version = response['version'];
    
    this.breadcrumbDataFrom[2].caption=this.role.name;

    let privilegeInfoNew = [];
    
    for(const i in response.privilegeInfo)
    {
      let privilegetag={};
      privilegetag["id"]=response.privilegeInfo[i].ref.uuid;
      privilegetag["itemName"]=response.privilegeInfo[i].ref.name;
      privilegeInfoNew[i]=privilegetag;
    }
      this.privilegeInfoTags=privilegeInfoNew;
      console.log('getOneByUuidAndVersion is end');
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

  getAllLatestPrivilege(){
    this._commonService.getAllLatest('privilege')
    .subscribe(
      response =>{
        this.onSuccessgetAllLatestPrivilege(response)},
        error => console.log("Error ::"+error));  
  }

  onSuccessgetAllLatestPrivilege(response){
    console.log('getAllLatestPrivilege is start');
    this.privResponse=response
    this.privilegeInfoArray=[];
    
     for(const i in response){
       let privilegeref = {};
       privilegeref["id"]=response[i]['uuid'];
       privilegeref["itemName"]=response[i]['name'];
       privilegeref["version"]=response[i]['version'];

       this.privilegeInfoArray[i]=privilegeref;
     }
     console.log('getAllLatestPrivilege is end');
  }

  onVersionChange(){ 
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'role')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.role.active = 'Y';
    }
    else {
      this.role.active = 'N';
    }
  }

  onChangePublished(event) {
    if(event === true) {
      this.role.published = 'Y';
    }
    else {
      this.role.published = 'N';
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

  submitRole(){
    this.isSubmitEnable=true;
    let roleJson={};
    
    roleJson["uuid"]=this.role.uuid;
    roleJson["name"]=this.role.name;
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
  roleJson["tags"]=tagstemp;
  roleJson["desc"]=this.role.desc;

    let privilegeInfoArrayNew=[]; 
      if(this.privilegeInfoTags != null)
    {
      for(const c in this.privilegeInfoTags)
      {
          let privilegeInfoRef = {};
          let privilegeRef={};
          privilegeInfoRef["uuid"]=this.privilegeInfoTags[c].id;
          privilegeInfoRef["type"]="privilege";
          privilegeRef["ref"]=privilegeInfoRef;
          privilegeInfoArrayNew.push(privilegeRef);
      }    
    }
   roleJson["privilegeInfo"]=privilegeInfoArrayNew;
   roleJson["active"]=this.role.active == true ?'Y' :"N"
   roleJson["published"]=this.role.published == true ?'Y' :"N"
      console.log(JSON.stringify(roleJson));
      this._commonService.submit("role",roleJson).subscribe(
        response => { this.OnSuccessubmit(response)},
        error => console.log('Error :: ' + error)
      )  
    } 
    OnSuccessubmit(response){
      this.isSubmitEnable=true;
      this.msgs = [];
      this.msgs.push({severity:'success', summary:'Success Message', detail:'Role Submitted Successfully'});
      setTimeout(() => {
        this.goBack()
        }, 1000);
   
      }
     
   public goBack() {
     //  this._location.back();
     this.router.navigate(['app/list/role']);
     
    }
   enableEdit(uuid, version) {
     this.router.navigate(['app/admin/role',uuid,version, 'false']);
     this.dropdownSettingsPrivilege = { 
      singleSelection: false, 
      selectAllText:'Select All',
      unSelectAllText:'UnSelect All',
      enableSearchFilter: true,
      disabled :true
    }; 
       
    }

   showview(uuid, version) {
      this.router.navigate(['app/admin/role',uuid,version, 'true']);
      this.dropdownSettingsPrivilege = { 
        singleSelection: false, 
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        disabled :false
      }; 
    }
}
