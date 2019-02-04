import { AppConfig } from './../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { Component, ViewChild } from "@angular/core";
import { DatePipe,Location } from "@angular/common";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';
import { KnowledgeGraphComponent } from '../../../shared/components';


@Component({
    selector: 'app-modelExec',
    styleUrls: [],
    templateUrl: './modelExec.template.html',
    
  })
  
export class ModelExecComponent{

  breadcrumbDataFrom: any;
  id : any;
  version : any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode : any;
  modelResultData : any;
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
  result : any;
  showResultModel : any;
  isHomeEnable: boolean;
  showGraph: boolean;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
 
  
  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService){
    
    this.isHomeEnable = false;
    this.showGraph = false;
    this.showResultModel = true;
    this.modelResultData = {};
    this.breadcrumbDataFrom=[{
        "caption":"Job Monitoring ",
        "routeurl":"/app/jobMonitoring"
      },
      {
        "caption":"Model",
        "routeurl":"/app/list/modelExec"
  
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

    showMainPage() {
      this.isHomeEnable = false;
      this.showGraph = false;
    }

    showDagGraph(uuid,version){
      this.isHomeEnable = true;
      this.showGraph = true;
      setTimeout(() => {
        this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
      }, 1000); 
    }

    onChangeActive(event) {
      if(event === true) {
        this.modelResultData.active = 'Y';
      }
      else {
        this.modelResultData.active = 'N';
      }
    }

    onChangePublished(event) {
      if(event === true) {
        this.modelResultData.published = 'Y';
      }
      else {
        this.modelResultData.published = 'N';
      }
    }
    
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'modelexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('modelexec',this.id)
      .subscribe(
      response =>{
        this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log("Error :: " + error));
    }
   onSuccessgetOneByUuidAndVersion(response){
      this.modelResultData=response
      this.createdBy=this.modelResultData.createdBy.ref.name;
      this.dependsOn=this.modelResultData.dependsOn.ref.name;
      this.result=this.modelResultData.result.ref.name;
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
   
      this.breadcrumbDataFrom[2].caption=this.modelResultData.name;
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
      this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'modelexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }

    public goBack() {
      this._location.back();
    }
}