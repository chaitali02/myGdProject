import { Component, OnInit, ViewChild } from '@angular/core';
import { DatePipe,Location } from '@angular/common';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import * as sqlFormatter from "sql-formatter";
import { KnowledgeGraphComponent } from '../../../shared/components/knowledgeGraph/knowledgeGraph.component';
@Component({
  selector: 'app-report-exec',
  templateUrl: './reportExec.component.html'
})
export class ReportExecComponent implements OnInit {
  isHomeEnable: boolean;
  showGraph : boolean;
  displayDialogBox: boolean = false;
  formattedQuery: any;
  exec: any;
  refKeyList: any[];
  result: any;
  tags: any;
  active: any;
  statusList: any[];
  dependsOn: any;
  createdBy: any;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  mode: any;
  version: any;
  id: any;
  name : any;
  reportData: {};
  selectedVersion: any;
	VersionList: any[];
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private datePipe: DatePipe, private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showGraph = false;
    this.isHomeEnable = false;
    this.reportData = {};
    //this.createdBy = {};
		this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    }); 
    if(this.mode !== undefined) {   
      this.getAllVersionByUuid();
      this.getOneByUuidAndVersion(this.id,this.version)
    }		
		
		this.breadcrumbDataFrom=[{
      "caption":"Job Monitoring ",
      "routeurl":"/app/jobMonitoring"
    },
    {
      "caption":"Report Exec",
      "routeurl":"/app/list/reportExec"
    },
    {
      "caption":"",
      "routeurl":null
		}]		
  }

  ngOnInit() {
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  public goBack() {
    this._location.back();
  }

   getOneByUuidAndVersion(id,version){
    this._commonService.getOneByUuidAndVersion(id,version,'reportexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('reportexec',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response){
    this.reportData = response
    this.createdBy = this.reportData['createdBy']['ref']['name'];
    this.dependsOn = this.reportData['dependsOn']['ref']['name'];
    this.result = this.reportData['result']['ref']['name'];
    var d;
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
		this.statusList = statusList
    var refKeyList = [];
    for (let i = 0; i < response.refKeyList.length; i++) {
      refKeyList[i] = response.refKeyList[i].ref.type + "." +response.refKeyList[i].ref.name;
    }
		this.refKeyList = refKeyList
		this.exec = response.exec;
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags']; 
    this.breadcrumbDataFrom[2].caption=this.reportData["name"];
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
    this.VersionList=temp;
	}
	
  onVersionChange(){
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'reportexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.reportData["active"] = 'Y';
    }
    else {
      this.reportData["active"] = 'N';
    }
	}
	
  onChangePublish(event) {
    if(event === true) {
      this.reportData["published"] = 'Y';
    }
    else {
      this.reportData["published"] = 'N';
    }
	}
	
  showSqlFormater(){
    this.formattedQuery = sqlFormatter.format(this.exec);
    this.displayDialogBox = true;
  }

  cancelDialogBox(){
    this.displayDialogBox = false;
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }


}
