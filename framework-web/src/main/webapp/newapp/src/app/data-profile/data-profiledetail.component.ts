
import {Component,Input,OnInit} from '@angular/core';
import { Location } from '@angular/common';    
import {Router, Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import {AppMetadata} from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import{ Version } from './../metadata/domain/version'
import{ DependsOn } from './dependsOn'
@Component({
  selector: 'app-profile',
  templateUrl: './data-profiledetail.template.html',
  styleUrls: []
})
    
  export class DataProfileDetailComponent {
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  IsProgerssShow: string;
  isSubmitEnable: any;
  msgs: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number,disabled :any};
  selectedItems:any
  dropdownList: any;
    source: any;
    selectedVersion: Version;
    VersionList: SelectItem[] = [];
    allNames: SelectItem[] = [];
    createdBy: any;
    mode: any;
    version: any;
    uuid:any;
    id: any;
    routerUrl: any;
    dataprofile:any
    sources:any;
    sourcedata: DependsOn
    constructor(private activatedRoute: ActivatedRoute,private router: Router,public metaconfig: AppMetadata,private _commonService:CommonService,private _location: Location){
      this.dataprofile={};
      this.dataprofile["active"]=true
      this.isSubmitEnable=true;
      this.IsProgerssShow="false";
      this.sources = ["datapod"];
      this.source=this.sources[0];
      this.selectedItems = [];
      this.dropdownSettings = { 
        singleSelection: false, 
        text:"Select Attrubutes",
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        classes:"myclass custom-class",
        maxHeight:110,
        disabled:false
      };   
      this.breadcrumbDataFrom=[{
          "caption":"Data Profiling",
          "routeurl":"/app/list/profile"
        },
        {
        "caption":"Rule",
        "routeurl":"/app/list/profile"
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
        if(this.mode !== undefined) {
          this.getOneByUuidAndVersion(this.id,this.version);
          this.getAllVersionByUuid(); 
          this.getAllLatest();
          this.dropdownSettings.disabled=this.mode =="false" ? false :true 
        }
        else{
          this.getAllLatest(); 
        }
      });
    }
    getAllLatest(){
      this._commonService.getAllLatest(this.source).subscribe(
        response => { this.OnSuccesgetAllLatest(response)},
        error => console.log('Error :: ' + error)
      ) 
    }
    OnSuccesgetAllLatest(response1){
      let temp=[]
      if(this.mode == undefined) {
        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response1[0]["name"];
        dependOnTemp.uuid = response1[0]["uuid"];
        this.sourcedata=dependOnTemp
       }
      for (const n in response1) {
        let allname={};
        allname["label"]=response1[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response1[n]['name'];      
        allname["value"]["uuid"]=response1[n]['uuid'];
        temp[n]=allname;
      }
      this.allNames = temp
     
      this.getAllAttributeBySource();
    }
  
    changeType(){
      this.selectedItems=[];
      this.getAllAttributeBySource();
    }
    getAllAttributeBySource(){
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
        response => { this.OnSuccesgetAllAttributeBySource(response)},
        error => console.log('Error :: ' + error)
      ) 
    }
    OnSuccesgetAllAttributeBySource(response){    
        let temp=[]
        for (const n in response) {
          let allname={};
          allname["id"]=response[n]['id'];
          allname["itemName"]=response[n]['dname'];
          allname["uuid"]=response[n]['uuid'];
          allname["attributeId"]=response[n]['attributeId'];
          temp[n]=allname
        }
        this.dropdownList=temp
        //this.allMapSourceAttribute = temp
      }
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'profile')
      .subscribe(
      response =>{
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    onSuccessgetOneByUuidAndVersion(response){
      this.breadcrumbDataFrom[2].caption=response.name;
      this.dataprofile=response;
      this.uuid=response.uuid
      this.createdBy=response.createdBy.ref.name
      this.dataprofile.published=response["published"] == 'Y' ? true : false
      this.dataprofile.active=response["active"] == 'Y' ? true : false
      const version: Version = new Version();
      version.label = response['version'];
      version.uuid = response['uuid'];
      this.selectedVersion=version
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response["dependsOn"]["ref"]["name"];
      dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
      this.sourcedata=dependOnTemp
      this.getAllAttributeBySource();
      let tmp=[];
      for(let i=0;i<response.attributeInfo.length;i++){
        let attributeinfo={};
        attributeinfo["id"]=response.attributeInfo[i]["ref"]["uuid"]+"_"+response.attributeInfo[i].attrId;
        attributeinfo["itemName"]=response.attributeInfo[i]["ref"]["name"]+"."+response.attributeInfo[i].attrName;
        attributeinfo["uuid"]=response.attributeInfo[i]["ref"]["uuid"]
        attributeinfo["attributeId"]=response.attributeInfo[i].attrId;
        tmp[i]=attributeinfo;
      }
      this.selectedItems=tmp;
    }

    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('profile',this.id)
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
   
    public goBack() {
     // this._location.back();
      this.router.navigate(['app/list/profile']);
     }

    submit(){
      this.isSubmitEnable=true;
      this.IsProgerssShow="true";
      let profileJson={}
      profileJson["uuid"]=this.dataprofile.uuid
      profileJson["name"]=this.dataprofile.name
      profileJson["desc"]=this.dataprofile.desc
      let tagArray=[];
      if(this.dataprofile.tags !=null){
        for(var counttag=0;counttag<this.dataprofile.tags.length;counttag++){
             tagArray[counttag]=this.dataprofile.tags[counttag];
        }
      }
      profileJson["tags"]=tagArray;
      let dependsOn={};
      let ref={}
      ref["type"]=this.source
      ref["uuid"]=this.sourcedata.uuid
      dependsOn["ref"]=ref;
      profileJson["dependsOn"] = dependsOn;
      profileJson["active"]=this.dataprofile.active == true ?'Y' :"N"
      profileJson["published"]=this.dataprofile.published == true ?'Y' :"N"
      let attributeInfo=[];
      for(let i=0;i<this.selectedItems.length;i++){
       let attributes={}
       let ref={};
       ref["uuid"]=this.selectedItems[i]["uuid"];
       ref["type"]="datapod"; 
       attributes["ref"]=ref;
       attributes["attrId"]=this.selectedItems[i]["attributeId"];
       attributeInfo[i]=attributes;
      }
      profileJson["attributeInfo"]=attributeInfo;
      console.log(profileJson);
      this._commonService.submit("profile",profileJson).subscribe(
        response => { this.OnSuccessubmit(response)},
        error => console.log('Error :: ' + error)
        )
       
    }
    OnSuccessubmit(response){
      if (this.checkboxModelexecution == true) {
        this._commonService.getOneById("profile",response).subscribe(
            response => {this.OnSucessGetOneById(response); },
            error => console.log('Error :: ' + error)
        )
      } //End if
      else{
        this.isSubmitEnable=true;
        this.IsProgerssShow="false";
        this.msgs = [];
        this.msgs.push({severity:'success', summary:'Success Message', detail:'Profile Save Successfully'});
        setTimeout(() => {
        this.goBack();      
        }, 1000);
      }
    }
    OnSucessGetOneById(response){
      this._commonService.execute(response.uuid,response.version,"profile","execute").subscribe(
        response => {
         this.showMassage('Profile Save and Submit Successfully','success','Success Message')
         setTimeout(() => {
         this.goBack()
        
        }, 1000);
        },
        error => console.log('Error :: ' + error)
      )
    }
    
    showMassage(msg,msgtype,msgsumary){
      this.isSubmitEnable=true;
      this.IsProgerssShow="false";
      this.msgs = [];
      this.msgs.push({severity:msgtype, summary:msgsumary, detail:msg});
    }
    enableEdit(uuid, version) {
      this.router.navigate(['app/dataProfiling/profile',uuid,version, 'false']);
      this.dropdownSettings = { 
        singleSelection: false, 
        text:"Select Attrubutes",
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        classes:"myclass custom-class",
        maxHeight:110,
        disabled:false
      };   


    }
    showview(uuid, version) {
      this.router.navigate(['app/dataProfiling/profile',uuid,version, 'true']);
      this.dropdownSettings = { 
        singleSelection: false, 
        text:"Select Attrubutes",
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        classes:"myclass custom-class",
        maxHeight:110,
        disabled:true
      };   

    }


  }
      