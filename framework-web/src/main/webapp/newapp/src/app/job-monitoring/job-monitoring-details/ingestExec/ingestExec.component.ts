import { Component, ViewChild } from "@angular/core";
import { AppConfig } from "../../../app.config";
import { ActivatedRoute, Router, Params } from "@angular/router";
import { CommonService } from "../../../metadata/services/common.service";
import { Location, DatePipe } from '@angular/common';
import { KnowledgeGraphComponent } from "../../../shared/components/knowledgeGraph/knowledgeGraph.component";

@Component({
	selector: 'app-ingestExec',
	templateUrl: './ingestExec.template.html'
})
export class IngestExecComponent {
  showGraph: boolean;
  isHomeEnable: boolean;
	statusList: any[];
	selectedVersion: any;
	VersionList: any[];
	tags: any;
	active: any;
	dependsOn: any;
	ingestData: any;
	createdBy: any;
	published: boolean;
	id: any;
	name: any;
	mode: any;
	version: any;
	breadcrumbDataFrom: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  
	constructor(private datePipe: DatePipe,private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.ingestData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
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
      "caption":"Ingest Exec",
      "routeurl":"/app/list/ingestExec"
    },
    {
      "caption":"",
      "routeurl":null
		}]		
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
    this._commonService.getOneByUuidAndVersion(id,version,'ingestexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('ingestexec',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }

	onSuccessgetOneByUuidAndVersion(response){
    this.ingestData=response
    this.createdBy=this.ingestData.createdBy.ref.name;
    this.dependsOn=this.ingestData.dependsOn.ref.name;
    var d
    var statusList = [];
    for (let i = 0; i < response.statusList.length; i++) {
      d = this.datePipe.transform(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
      d = d.toString().replace("GMT+5:30", "IST");
      statusList[i] = response.statusList[i].stage + "-" + d;
    }
		this.statusList = statusList
		
    // this.published = response['published'];
    // if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags']; 
    this.breadcrumbDataFrom[2].caption=this.ingestData.name;
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label,'ingestexec')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onChangeActive(event) {
    if(event === true) {
      this.ingestData.active = 'Y';
    }
    else {
      this.ingestData.active = 'N';
    }
	}
	
  onChangePublish(event) {
    if(event === true) {
      this.ingestData.published = 'Y';
    }
    else {
      this.ingestData.published = 'N';
    }
	}
  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

}