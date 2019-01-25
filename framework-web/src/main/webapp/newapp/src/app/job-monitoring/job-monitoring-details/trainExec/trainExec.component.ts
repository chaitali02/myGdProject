import { AppConfig } from './../../../app.config';
import { SelectItem } from 'primeng/primeng';
import { Component } from "@angular/core";
import { DatePipe,Location } from "@angular/common";
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Version } from '../../../shared/version';

@Component({
    selector: 'app-trainExec',
    styleUrls: [],
    templateUrl: './trainExec.template.html',    
  })  
export class TrainExecComponent{
  showGraph: boolean;
  isHomeEnable: boolean;

  breadcrumbDataFrom: any;
  id : any;
  version : any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  mode : any;
  trainResultData : any;
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
  showResultTrain : any;
 
  constructor(private datePipe: DatePipe,private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService){
    this.showResultTrain = true;
    this.trainResultData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.breadcrumbDataFrom=[{
        "caption":"Job Monitoring ",
        "routeurl":"/app/jobMonitoring"
      },
      {
        "caption":"train Exec",
        "routeurl":"/app/list/trainExec"
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
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id,this.version);
      }
    }

    onChangeActive(event) {
      if(event === true) {
        this.trainResultData.active = 'Y';
      }
      else {
        this.trainResultData.active = 'N';
      }
    }

    onChangePublished(event) {
      if(event === true) {
        this.trainResultData.published = 'Y';
      }
      else {
        this.trainResultData.published = 'N';
      }
    }
    
    getOneByUuidAndVersion(id,version){
      this._commonService.getOneByUuidAndVersion(id,version,'trainexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }

    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('trainexec',this.id)
      .subscribe(
      response =>{
        this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log("Error :: " + error));
    }

    onSuccessgetOneByUuidAndVersion(response){
      this.trainResultData=response
      this.createdBy=this.trainResultData.createdBy.ref.name;
      this.dependsOn=this.trainResultData.dependsOn.ref.name;
      //this.result=this.trainResultData.result.ref.name;
      if (this.trainResultData.result !== null) {
        this.result = this.trainResultData.result.ref.name;
      }

      var d
      var statusList = [];
      for (let i = 0; i < response.statusList.length; i++) {
        d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
        d = d.toString().replace("GMT+5:30", "IST");
        statusList[i] = response.statusList[i].stage + "-" + d;
      }
      this.statusList = statusList

      this.published = response['published'];
      if(this.published === 'Y') { this.published = true; } else { this.published = false; }
      this.active = response['active'];
      if(this.active === 'Y') { this.active = true; } else { this.active = false; }
      this.tags = response['tags'];
   
      this.breadcrumbDataFrom[2].caption=this.trainResultData.name;
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
      this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'trainexec')
      .subscribe(
      response =>{//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }

    public goBack() {
      this._location.back();
    }
    
    showMainPage() {
      this.isHomeEnable = false;
      this.showGraph = false;
    }
  
    showDagGraph(uuid, version) {
      this.isHomeEnable = true;
      this.showGraph = true;
    }
}