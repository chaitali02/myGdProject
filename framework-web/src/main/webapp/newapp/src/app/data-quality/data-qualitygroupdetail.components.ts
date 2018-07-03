
import {Component,Input,OnInit} from '@angular/core';
import { Location } from '@angular/common';    
import {Router, Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import {AppMetadata} from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';
import{DataQualityService}from '../metadata/services/dataQuality.services';

import{ Version } from './../metadata/domain/version'
@Component({
  selector: 'app-qualityGroup',
  templateUrl: './data-qualitygroupdetail.template.html',
  styleUrls: []
})
    
  export class DataQualityGroupDetailComponent {
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  IsProgerssShow: string;
  isSubmitEnable: any;
  msgs: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number,disabled :any};
  selectedItems:any
  dropdownList: any;
   
    selectedVersion: Version;
    VersionList: SelectItem[] = [];
    allNames: SelectItem[] = [];
    createdBy: any;
    mode: any;
    version: any;
    uuid :any;
    id: any;
    routerUrl: any;
    datadqgroup:any
    constructor(private activatedRoute: ActivatedRoute,private router: Router,public metaconfig: AppMetadata,private _commonService:CommonService,private _location: Location,private _dataQualityService:DataQualityService){
      this.datadqgroup={};
      this.datadqgroup["active"]=true
      this.isSubmitEnable=true;
      this.IsProgerssShow="false";
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
          "caption":"Data Quality  ",
          "routeurl":"/app/list/dqgroup"
        },
        {
        "caption":"Data Quality Group ",
        "routeurl":"/app/list/dqgroup"
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
      this._commonService.getAllLatest("dq").subscribe(
        response => { this.OnSuccesgetAllLatest(response)},
        error => console.log('Error :: ' + error)
      ) 
    }
    OnSuccesgetAllLatest(response){
      let temp=[]
      for (const n in response) {
        let allname={};
        allname["id"]=response[n]['uuid'];
        allname["itemName"]=response[n]['name'];
        allname["uuid"]=response[n]['uuid'];
        temp[n]=allname;
      }
      this.dropdownList = temp
    }
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'dqgroup')
      .subscribe(
      response =>{
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    onSuccessgetOneByUuidAndVersion(response){
      this.breadcrumbDataFrom[2].caption=response.name;
      this.datadqgroup=response;
      this.createdBy=response.createdBy.ref.name
      this.datadqgroup.published=response["published"] == 'Y' ? true : false
      this.datadqgroup.active=response["active"] == 'Y' ? true : false
      const version: Version = new Version();
      this.uuid=response.uuid;
      version.label = response['version'];
      version.uuid = response['uuid'];
      this.selectedVersion=version
      let tmp=[];
      for(let i=0;i<response.ruleInfo.length;i++){
        let ruleinfo={};
        ruleinfo["id"]=response.ruleInfo[i]["ref"]["uuid"];
        ruleinfo["itemName"]=response.ruleInfo[i]["ref"]["name"];
        ruleinfo["uuid"]=response.ruleInfo[i]["ref"]["uuid"];   
        tmp[i]=ruleinfo;
      }
      this.selectedItems=tmp;
    }

    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('dqgroup',this.id)
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
      //this._location.back();
      this.router.navigate(['app/list/dqgroup']);
      }

    submit(){
      this.isSubmitEnable=true;
      this.IsProgerssShow="true";
      let dqgroupJson={}
      dqgroupJson["uuid"]=this.datadqgroup.uuid
      dqgroupJson["name"]=this.datadqgroup.name
      dqgroupJson["desc"]=this.datadqgroup.desc
      let tagArray=[];
      if(this.datadqgroup.tags !=null){
        for(var counttag=0;counttag<this.datadqgroup.tags.length;counttag++){
             tagArray[counttag]=this.datadqgroup.tags[counttag];
        }
      }
      dqgroupJson["tags"]=tagArray;
      dqgroupJson["active"]=this.datadqgroup.active == true ?'Y' :"N"
      dqgroupJson["published"]=this.datadqgroup.published == true ?'Y' :"N"
      let ruleInfo=[];
      for(let i=0;i<this.selectedItems.length;i++){
       let rules={}
       let ref={};
       ref["uuid"]=this.selectedItems[i]["uuid"];
       ref["type"]="dq"; 
       rules["ref"]=ref;
       ruleInfo[i]=rules;
      }
      dqgroupJson["ruleInfo"]=ruleInfo;
      dqgroupJson["inParallel"]=this.datadqgroup.inParallel
      console.log(dqgroupJson);
      this._commonService.submit("dqgroup",dqgroupJson).subscribe(
        response => { this.OnSuccessubmit(response)},
        error => console.log('Error :: ' + error)
        )
       
    }
    OnSuccessubmit(response){
      if (this.checkboxModelexecution == true) {
        this._commonService.getOneById("dqgroup",response).subscribe(
            response => {this.OnSucessGetOneById(response);
              this.goBack() },
            error => console.log('Error :: ' + error)
        )
      } //End if
      else{
        this.isSubmitEnable=true;
        this.IsProgerssShow="false";
        this.msgs = [];
        this.msgs.push({severity:'success', summary:'Success Message', detail:'DQ Group Save Successfully'});
        setTimeout(() => {
        this.goBack()
        }, 1000);
      }
    }
    OnSucessGetOneById(response){
      this._commonService.execute(response.uuid,response.version,"dqgroup","execute").subscribe(
        response => {
         this.showMassage('DQ Group Save and Submit Successfully','success','Success Message')
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
      this.router.navigate(['app/dataQuality/dqgroup',uuid,version, 'false']);
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
    showview(uuid, version) {
      this.router.navigate(['app/dataQuality/dqgroup',uuid,version, 'true']);
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
  }
      