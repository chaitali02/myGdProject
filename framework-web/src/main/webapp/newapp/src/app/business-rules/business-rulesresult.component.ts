
import {Component,Input,OnInit,ViewChild,AfterViewInit} from '@angular/core';    
import {Router, Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import { Location } from '@angular/common'; 

import {AppMetadata} from '../app.metadata';
// import{JointjsGroupComponent } from './jointjsgroup.component'
import {TableRenderComponent} from '../shared/components/resulttable/resulttable.component'
import {JointjsGroupComponent} from '../shared/components/jointjsgroup/jointjsgroup.component'
import { CommonService } from '../metadata/services/common.service';

import { Http, Headers } from '@angular/http';
import { ResponseContentType } from '@angular/http';
import { saveAs } from 'file-saver';
import { AppConfig } from '../app.config';
@Component({
  selector: 'app-profile',
  templateUrl: './business-rulesresult.template.html',
  styleUrls: []
})
export class BusinessRulesResultComponent {
  typeJointJs: any;
  versionJointJs: any;
  uuidJointJs: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    _type: any;
    _uuid: any;
    _version: any;
    _mode: any;
    baseUrl:any;
    istableShow: boolean;
    isgraphShow: boolean;
    params:any
    @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
    @ViewChild(TableRenderComponent) d_tableRenderComponent: TableRenderComponent;   
    constructor(private http : Http, private _config : AppConfig, private _location:Location,private _activatedRoute: ActivatedRoute,private router: Router,public appMetadata: AppMetadata,private _commonService:CommonService){      
      this.baseUrl = _config.getBaseUrl();
      this.isgraphShow=false;
      this.istableShow=false;
      this.breadcrumbDataFrom=[{
          "caption":"Business Rules",
          "routeurl":"/app/list/ruleexec"
      },
      {
        "caption":"Result ",
        "routeurl":"/app/list/ruleexec"
      },
      // {
      //   "caption":"Result",
      //   "routeurl":"list/profileexec"
      // },
      {
        "caption":"",
        "routeurl":null
      }
      ]
      this.params ={
        "typeLabel": "RuleGroup",
        "url": "rule/getRuleExecByRGExec?",
        "ref": {}
      } 
      this._activatedRoute.params.subscribe((params: Params) => {
        this._uuid = params['id'];
        this._version = params['version'];
        this._mode = params['mode'];
        this._type = params['type'];
        this.getOneByUuidAndVersion(this._uuid, this._version,this._type)
      });      
    }

    getOneByUuidAndVersion(id,version,type){
      this._commonService.getOneByUuidAndVersion(id,version,type)
      .subscribe(
      response =>{
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
  
    onSuccessgetOneByUuidAndVersion(response){
      this.breadcrumbDataFrom[2].caption=response.name;
      this.params["id"]=this._uuid;
      this.params["uuid"]=this._uuid;
      this.params["name"] =response.name;
      this.params["elementType"]=this._type ;
      this.params["version"]=this._version;
      this.params.ref["id"]=this._uuid;
      this.params.ref["name"] =response.name;
      this.params.ref["type"]=this._type;
      this.params.ref["version"]=this._version;
      if(this._type.slice(-4) == 'Exec' || this._type.slice(-4) == 'exec'){
        if(this._type.slice(-9) == 'groupExec' || this._type.slice(-9) == 'groupexec'){
          this.isgraphShow=true;
        }
        else {
          this.istableShow= true;
        }
      }
      if( this.istableShow== true){ 
        setTimeout(() => {
          this.params["type"]=this.appMetadata.getMetadataDefs(this._type.toLowerCase())['name']
          this.d_tableRenderComponent.renderTable(this.params);
        }, 1000);
      }
      else{
        this.params["type"]=this._type;
      this.isgraphShow=true;
      }
    }
    public goBack() {
      if(this.istableShow ==true){
        this._location.back();
      }
      else{
        if(this.d_JointjsGroupComponent.IsGraphShow==true){
            this._location.back();
        }
        else{
            this.d_JointjsGroupComponent.IsGraphShow=true;
        }
       }
    }    

  downloadRule() {
    this.uuidJointJs = this.d_tableRenderComponent.uuid;
    this.versionJointJs = this.d_tableRenderComponent.version;
    this.typeJointJs = this.d_tableRenderComponent.type;
    
    const headers = new Headers();
    this.http.get(this.baseUrl+'/rule/download?action=view&ruleExecUUID=' + this.uuidJointJs + '&ruleExecVersion=' + this.versionJointJs + '&mode=BATCH',
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
      
     
    
    