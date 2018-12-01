import {
  Component,
  ViewEncapsulation,
  ElementRef, Renderer,
  Input,
  OnInit,
  ViewChild,
  AfterViewInit
} from '@angular/core';

import {
  Router,
  Event as RouterEvent,
  NavigationStart,
  NavigationEnd,
  NavigationCancel,
  NavigationError,
  ActivatedRoute,
  Params
} from '@angular/router';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';
import { MessageService } from 'primeng/components/common/messageservice';

import { JointjsComponent } from './jointjs.component';

import { CommonService } from '../metadata/services/common.service';
import { JointjsService } from './jointjsservice'
import { SharedDataService } from './shareddata.service'
import { SelectItem } from 'primeng/primeng';
import { Version } from './../metadata/domain/version'
declare var jQuery: any;
@Component({
  selector: 'app-graph-analysis',
  templateUrl: './graph-analysis.template.html',


})

export class GraphAnalysisComponent {
  allAttr: any;
  allType: {"label": string; "value": string; }[];
  showAttrModel: boolean;
  allDatapod: {};
  selectType: any;
  selectAttr: any;
  searchAttr: {};
  
  msgs: any[];
  tem: object;
  tags: any;
  createdBy: any;
  result: string;
  resulte: boolean;
  graphData: any;
  nodeTableArray:any[]=[];
  mode: any;
  version: any;
  uuid: any;
  id: any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  
  @ViewChild('searchAttrModel') searchAttrModel: ElementRef;

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _jointjsService: JointjsService, private _sharedDataService: SharedDataService) {
    this.graphData = {};
    this.nodeTableArray=[]
    this.continueCount = 1;
    this.isSubmit = "false"
    this.progressbarWidth = 33.33 * this.continueCount + "%";
    this.graphData["active"] = "true";
    // this.dagdata["published"]= "false";
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this. getAllVersionByUuid()
      }

    });
    this.breadcrumbDataFrom = [{
      "caption": "Data Pipeline",
      "routeurl": "/app/list/dag"
    },
    {
      "caption": "Pipeline",
      "routeurl": "/app/list/dag"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dag']);
  }
  onChangeActive(event) {
    if (event === true) {
      this.graphData.active = 'Y';
    }
    else {
      this.graphData.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.graphData.published = 'Y';
    }
    else {
      this.graphData.published = 'N';
    }
  }
  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('dag',this.id)
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
  onVersionChange() {
      
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'dag')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'dag')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.graphData = response;
    this.breadcrumbDataFrom[2].caption = response.name;
    this.result = "true";
    this.uuid = response.uuid;
    this.createdBy = this.graphData.createdBy.ref.name
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.tags = tags;
    }//End If
    this.graphData.published = response["published"] == 'Y' ? true : false
    this.graphData.active = response["active"] == 'Y' ? true : false
  }
  dagSubmit() {
    this.isSubmit = "true"
    let dagJson = {};
    let temp = this._jointjsService.convertGraphToDag(this._sharedDataService.getData());
    dagJson["uuid"] = this.graphData.uuid;
    dagJson["name"] = this.graphData.name;
    dagJson["desc"] = this.graphData.desc;
    // dagJson["active"]=this.graphData.active == true ?'Y' :"N"
    // dagJson["published"]=this.graphData.published == true ?'Y' :"N"
    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    dagJson['tags'] = tagArray
    dagJson["stages"] = temp["stages"];
    dagJson["xPos"] = temp["xPos"];
    dagJson["yPos"] = temp["yPos"];
    console.log(dagJson);
    this._commonService.submit("dag", dagJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.msgs = [];
    this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Dag Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/dataPipeline/dag', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataPipeline/dag', uuid, version, 'true']);
  }
  addNodeRow(){
    if (this.nodeTableArray == null) {
      this.nodeTableArray = [];
      //this.nodeTableArray["nodeSource"]={}
		}
		var nodeTable = {};
		nodeTable["id"]=this.nodeTableArray.length;
		nodeTable["nodeId"];
		nodeTable["nodeProperties"]=null;
    nodeTable["allAttributeInto"]=[];
    nodeTable["nodeSource"]={}
    nodeTable["highlightInfo"]={};
    nodeTable["nodeBackgroundInfo"]={}
		this.nodeTableArray.splice(this.nodeTableArray.length, 0, nodeTable);
  }
  SearchAttribute(index,type,proprety){
    this.allType=[
      {
        "label": "Datapod",
        "value": "datapod"
      },
      {      
        "label": "Dataset",
        "value": "dataset"
      }
    ]
    this.selectType=this.allType[0];
		this.searchAttr={}
		this.searchAttr["index"]=index;
		this.searchAttr["type"]=type;
		this.searchAttr["proprety"]=proprety;
		this.selectAttr=null;
		var selectType=this.selectType;
    this.selectType=null;
    // jQuery(this.searchAttrModel.nativeElement).modal('show');
		// $('#searchAttr').modal({
		// 	backdrop: 'static',
		// 	keyboard: false
		// });	
		if(type =='node'){
      this.selectType="datapod"
				// this.selectAttr=this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]];
				// if(this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]]){
	      //   this.selectType=this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]].type;
				// }else{
				// 	this.selectType=selectType
				// }
				this.onChangeType();

		}
		// else{
		// 		this.selectAttr=this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety];
		// 		if(this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety]){
		// 			this.selectType=this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety].type;
		// 		}else{
		// 			this.selectType=selectType
		// 		}
		// 		this.onChangeType();

		// }
  }
  
  onChangeType() {
    this._commonService.getAllLatest(this.selectType)
      .subscribe(
      response => {
        this.onSuccessGetAllLatest(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessGetAllLatest(response){
    this.allDatapod={}
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allDatapod["options"] = temp
		//	this.allDatapod["options"] = response;
			if (this.selectAttr){
				var defaultoption={};
				defaultoption["uuid"]=this.selectAttr.uuid;
				defaultoption["name"]="";
				defaultoption["type"]=this.selectType;
				setTimeout(function () {
					this.allDatapod["defaultoption"]=defaultoption;
				 },10);
				
			}else{
        
				this.allDatapod["defaultoption"] = response[0];
      }
      this.showAttrModel=true
     // jQuery(this.searchAttrModel.nativeElement).modal('hide'); 
  }
  SubmitSearchAttr(){
    this.showAttrModel=false
		console.log(this.allDatapod["defaultoption"]);debugger
		if(this.searchAttr && this.searchAttr["type"]=='node'){
			this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]]=this.allDatapod["defaultoption"]//this.selectAttr;
			this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]].type=this.selectType;
			this.nodeTableArray[this.searchAttr["index"]]["nodeProperties"]=null;
			this.onChangeDatapod();
		}else{
			// this.edgeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]]=this.allDatapod["defaultoption"]//this.selectAttr;
			// this.edgeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]].type=this.selectType;
			// setTimeout(function () {
			// 	this.edgeTableArray[this.searchAttr.index]["edgeProperties"]=[];
			// });
			
			this.onChangeDatapod();
		}
		
	}
  onChangeDatapod(){
    this._commonService.getAllAttributeBySource(this.allDatapod["defaultoption"]["uuid"],this.selectType)
    .subscribe(
    response => {
      this.onSuccessAttributeBySource(response)
    },
    error => console.log("Error :: " + error));

  }
  onSuccessAttributeBySource(response){debugger
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allAttr = temp
     
    if(this.searchAttr["type"] =='node'){
      let temp = []
      for (const n in response) {
        let allname = {};
        allname["label"] = response[n]['name'];
        allname["value"] = {};
        allname["value"]["label"] = response[n]['name'];
        allname["value"]["uuid"] = response[n]['uuid'];
        temp[n] = allname;
      }
      this.nodeTableArray[this.searchAttr["index"]].allAttributeInto = temp
        //this.nodeTableArray[this.searchAttr["index"]].allAttributeInto=response;
    }
    else{
     // this.edgeTableArray[this.searchAttr.index].allAttributeInto=response;
    }
    console.log(response)
    if(!this.selectAttr){
      this.selectAttr=this.allAttr[0]
    }
  }
}

