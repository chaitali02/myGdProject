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
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
declare var jQuery: any;
@Component({
  selector: 'app-graph-analysis',
  templateUrl: './graph-analysis.template.html',


})

export class GraphAnalysisComponent {
  selectedAllEdgeRow: any;
  selectedAllNodeRow: boolean;
  response: any;
  edgeTableArray: any;
  heading: string;
  dropdownSettingsPrivilege: { singleSelection: boolean; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; disabled: boolean; };
  nodeIcon: { "label": string; "value": string; "class": string; }[];
  nodeBackGroundColor: any;
  selectedAllPropertyRow: boolean;
  nodeHighlightType: any;
  showBgModel: boolean;
  propertyInfoTableArray: any[];
  highlightInfo: any;
  nodeColorInfo: ["#ef9a9a", "#B39DDB", "#80DEEA", "#BCAAA4", "#B0BEC5"] | ["#b71c1c", "#004D40", "#FF9800", "#BF360C", "#0D47A1", "#263238", "#000000"];
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
  nodeTableArray:any;
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
  
  nodeIconMap:{"home":{"caption":"Home","value":"home","class":"fa fa-home","code":"\uf015","color":"#5C9BD1"},
  "user":{"caption":"User","value":"user","class":"fa fa-user","code":"\uf007","color":"#8877a9"},
  "bank":{"caption":"Bank","value":"bank","class":"fa fa-university","code":"\uf19c","color":"#2ab4c0"},
  "office":{"caption":"Office","value":"office","class":"fa fa-building","code":"\uf1ad","color":"#f36a5a"},
  "dollar":{"caption":"Dollar","value":"dollar","class":"fa fa-usd","code":"\f155","color":"#0db7ed"}}
  
  nodeHighlightColor:["#b71c1c","#004D40","#FF9800","#BF360C","#0D47A1","#263238","#000000"]
  
  edgeHighlightColor:["#b71c1c","#004D40","#FF9800","#BF360C","#0D47A1","#263238","#000000"]
  @ViewChild('searchAttrModel') searchAttrModel: ElementRef;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isHomeEnable: boolean;
  showGraph: boolean;
  // ngOnInit() {
  //   this.selectType='datapod'
  //   this.onChangeType()
  //   this.calTest()
  // }
  // calTest(){
  //   setTimeout(() => {
  //     this.onChangeDatapod()
  //   }, 1000);
  //   //this.onChangeDatapod()

  // }

  
  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _jointjsService: JointjsService, private _sharedDataService: SharedDataService) {
    
    this.showGraph = false;
    this.isHomeEnable = false;
    this.graphData = {};
    this.nodeTableArray=[]
    this.nodeIconMap={"home":{"caption":"Home","value":"home","class":"fa fa-home","code":"\uf015","color":"#5C9BD1"},
    "user":{"caption":"User","value":"user","class":"fa fa-user","code":"\uf007","color":"#8877a9"},
    "bank":{"caption":"Bank","value":"bank","class":"fa fa-university","code":"\uf19c","color":"#2ab4c0"},
    "office":{"caption":"Office","value":"office","class":"fa fa-building","code":"\uf1ad","color":"#f36a5a"},
    "dollar":{"caption":"Dollar","value":"dollar","class":"fa fa-usd","code":"\f155","color":"#0db7ed"}}
    this.edgeTableArray=[]
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
      "caption": "Graph Analysis",
      "routeurl": "/app/graphAnalysis/graphpod"
    },
    {
      "caption": "Graph",
      "routeurl": "app/graphAnalysis/graphpod"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.nodeHighlightType=[
      {"label":"Category","value":"category"},
      {"label":"Numerical","value":"numerical"}]
      this.nodeBackGroundColor=[
        {"label":"#ef9a9a","value":"#ef9a9a"},
        {"label":"#B39DDB","value":"#B39DDB"},
        {"label":"#80DEEA","value":"#80DEEA"},
        {"label":"#BCAAA4","value":"#BCAAA4"},
        {"label":"#B0BEC5","value":"#B0BEC5"}
      ]
      this.nodeIcon=[{"label":"Home","value":"home","class":"fa fa-home"},
      {"label":"User","value":"user","class":"fa fa-user"},
      {"label":"Bank","value":"bank","class":"fa fa-university"},
      {"label":"Office","value":"office","class":"fa fa-building"},
      {"label":"Dollar","value":"doller","class":"fa fa-usd"}]
      this.dropdownSettingsPrivilege = { 
        singleSelection: false, 
        selectAllText:'Select All',
        unSelectAllText:'UnSelect All',
        enableSearchFilter: true,
        disabled :false
      }; 
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }
  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/graphpod']);
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
  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('graphpod',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      //allName["uuid"]=response[i]['uuid']
      temp[i] = ver;
    }
    this.VersionList = temp
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
    this._commonService.getOneByUuidAndVersion(id, version, 'graphpod')
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
    //this.nodeTableArray=response["nodeInfo"];
   // this.edgeTableArray=response["edgeInfo"];
    this.graphData.published = response["published"] == 'Y' ? true : false
    this.graphData.active = response["active"] == 'Y' ? true : false
    // this.selectType="datapod"
    // this.onChangeType()
    // setTimeout(() => {
    //   this.onChangeDatapod()
    // }, 1000);
    var nodeInfo=[];
    if(response.nodeInfo !=null){
      for(var i=0;i<response.nodeInfo.length;i++){
        var nodeJson={};
        var nodeId={};
        var nodeSource={}; 
        var nodeName={};
        var nodePropertiesArr=[];
        if(response.nodeInfo[i].nodeProperties !=null){
          for(var j=0;j<response.nodeInfo[i].nodeProperties.length;j++){
            var nodeProperties={};
            nodeProperties["uuid"]=response.nodeInfo[i].nodeProperties[j].ref.uuid;
            nodeProperties["type"]=response.nodeInfo[i].nodeProperties[j].ref.type;
            nodeProperties["datapodname"]=response.nodeInfo[i].nodeProperties[j].ref.name;
            nodeProperties["name"]=response.nodeInfo[i].nodeProperties[j].attrName;
            nodeProperties["itemName"]=response.nodeInfo[i].nodeProperties[j].attrName;
            nodeProperties["dname"]=response.nodeInfo[i].nodeProperties[j].ref.name+"."+response.nodeInfo[i].nodeProperties[j].attrName;
            nodeProperties["attributeId"]=response.nodeInfo[i].nodeProperties[j].attrId;
            nodeProperties["attrType"]=response.nodeInfo[i].nodeProperties[j].attrType;
            nodeProperties["id"] = response.nodeInfo[i].nodeProperties[j].ref.uuid+"_"+response.nodeInfo[i].nodeProperties[j].attrId;
            nodePropertiesArr[j]=nodeProperties;
            this.getAllAttributeBySource(nodePropertiesArr[j],i,'node')
          }
        }
        nodeJson["nodeProperties"]=nodePropertiesArr;
        nodeSource["uuid"]=response.nodeInfo[i].nodeSource.ref.uuid;
        nodeSource["name"]=response.nodeInfo[i].nodeSource.ref.name;
        nodeSource["label"]=response.nodeInfo[i].nodeSource.ref.name;
        nodeSource["type"]=response.nodeInfo[i].nodeSource.ref.type;
        nodeJson["nodeSource"]=nodeSource;
        
        nodeId["uuid"]=response.nodeInfo[i].nodeId.ref.uuid;
        nodeId["type"]=response.nodeInfo[i].nodeId.ref.type;
        nodeId["datapodname"]=response.nodeInfo[i].nodeId.ref.name;
        nodeId["id"]=response.nodeInfo[i].nodeId.ref.uuid+"_"+response.nodeInfo[i].nodeId.attrId
        nodeId["name"]=response.nodeInfo[i].nodeId.attrName;
        nodeId["label"]=response.nodeInfo[i].nodeId.attrName;
        nodeId["dname"]=response.nodeInfo[i].nodeId.ref.name+"."+response.nodeInfo[i].nodeId.attrName;
        nodeId["attributeId"]=response.nodeInfo[i].nodeId.attrId;
        nodeId["attrType"]=response.nodeInfo[i].nodeId.attrType;
        nodeJson["nodeId"]=nodeId;
        
        nodeJson["nodeType"]=response.nodeInfo[i].nodeType;
        
        nodeJson["nodeIcon"]={}
        nodeJson["nodeIcon"]=this.nodeIconMap[response.nodeInfo[i].nodeIcon];
        var nodeName={};
        nodeName["uuid"]=response.nodeInfo[i].nodeName.ref.uuid;
        nodeName["datapodname"]=response.nodeInfo[i].nodeName.ref.name;
        nodeName["type"]=response.nodeInfo[i].nodeName.ref.type;
        nodeName["name"]=response.nodeInfo[i].nodeName.attrName;
        nodeName["id"]=response.nodeInfo[i].nodeName.ref.uuid+"_"+response.nodeInfo[i].nodeName.attrId
        nodeName["dname"]=response.nodeInfo[i].nodeName.ref.name+"."+response.nodeInfo[i].nodeName.attrName;
        nodeName["attributeId"]=response.nodeInfo[i].nodeName.attrId;
        nodeName["attrType"]=response.nodeInfo[i].nodeName.attrType;
        nodeJson["nodeName"]=nodeName;
        
        var nodeSize={};
        if(response.nodeInfo[i].nodeSize !=null){
          nodeSize["uuid"]=response.nodeInfo[i].nodeSize.ref.uuid;
          nodeSize["datapodname"]=response.nodeInfo[i].nodeSize.ref.name;
          nodeSize["type"]=response.nodeInfo[i].nodeSize.ref.type;
          nodeSize["name"]=response.nodeInfo[i].nodeSize.attrName;
          nodeSize["id"]=response.nodeInfo[i].nodeSize.ref.uuid+"_"+response.nodeInfo[i].nodeSize.attrId
          nodeSize["dname"]=response.nodeInfo[i].nodeSize.ref.name+"."+response.nodeInfo[i].nodeSize.attrName;
          nodeSize["attributeId"]=response.nodeInfo[i].nodeSize.attrId;
          nodeSize["attrType"]=response.nodeInfo[i].nodeSize.attrType;
          nodeJson["nodeSize"]=nodeSize;
        }else{
          nodeJson["nodeSize"]=[];
        }
        

        var highlightInfo={};
        var propertyId={};
        if(response.nodeInfo[i].highlightInfo){
          highlightInfo["selectType"]=response.nodeInfo[i].highlightInfo.type;
          propertyId["uuid"]=response.nodeInfo[i].highlightInfo.propertyId.ref.uuid;
          propertyId["type"]=response.nodeInfo[i].highlightInfo.propertyId.ref.type;
          propertyId["datapodname"]=response.nodeInfo[i].highlightInfo.propertyId.ref.name;
          propertyId["name"]=response.nodeInfo[i].highlightInfo.propertyId.attrName;
          propertyId["dname"]=response.nodeInfo[i].highlightInfo.propertyId.ref.name+"."+response.nodeInfo[i].highlightInfo.propertyId.attrName;
          propertyId["attributeId"]=response.nodeInfo[i].highlightInfo.propertyId.attrId;
          propertyId["attrType"]=response.nodeInfo[i].highlightInfo.propertyId.attrType;
          propertyId["id"] =response.nodeInfo[i].highlightInfo.propertyId.ref.uuid+"_"+response.nodeInfo[i].highlightInfo.propertyId.attrId;
          highlightInfo["propertyId"]=propertyId;
          highlightInfo["value"]=response.nodeInfo[i].highlightInfo.type+","+response.nodeInfo[i].highlightInfo.propertyId.attrName;
          highlightInfo["propertyInfoTableArray"]=response.nodeInfo[i].highlightInfo.propertyInfo;
          nodeJson["highlightInfo"]=highlightInfo;
        }
        else{
          nodeJson["highlightInfo"]=[{'value':''}];
        }
        var nodeBackgroundInfo={};
        var NBPropertyId={};
        if(response.nodeInfo[i].nodeBackgroundInfo){
          nodeBackgroundInfo["selectType"]=response.nodeInfo[i].nodeBackgroundInfo.type;
          NBPropertyId["uuid"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.ref.uuid;
          NBPropertyId["type"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.ref.type;
          NBPropertyId["datapodname"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.ref.name;
          NBPropertyId["name"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrName;
          NBPropertyId["dname"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.ref.name+"."+response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrName;
          NBPropertyId["attributeId"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrId;
          NBPropertyId["attrType"]=response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrType;
          NBPropertyId["id"] =response.nodeInfo[i].nodeBackgroundInfo.propertyId.ref.uuid+"_"+response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrId;
          nodeBackgroundInfo["propertyId"]=NBPropertyId;
          nodeBackgroundInfo["value"]=response.nodeInfo[i].nodeBackgroundInfo.type+","+response.nodeInfo[i].nodeBackgroundInfo.propertyId.attrName;
          nodeBackgroundInfo["propertyInfoTableArray"]=response.nodeInfo[i].nodeBackgroundInfo.propertyInfo;
          nodeJson["nodeBackgroundInfo"]=nodeBackgroundInfo;
        }
        else{
          nodeJson["nodeBackgroundInfo"]=[];
        }
         nodeJson["allAttributeInto"]=[]
        nodeInfo[i]=nodeJson;
        
      }
      
      this.nodeTableArray=nodeInfo;
      
    } 

    
    var edgeInfo=[];
    if(response.edgeInfo !=null){
      for(var i=0;i<response.edgeInfo.length;i++){
        var edgeJson={};
        var edgeSource={};
        var sourceNodeId={};
        var targetNodeId={};
        edgeSource["uuid"]=response.edgeInfo[i].edgeSource.ref.uuid;
        edgeSource["name"]=response.edgeInfo[i].edgeSource.ref.name;
        edgeSource["label"]=response.edgeInfo[i].edgeSource.ref.name;
        edgeSource["type"]=response.edgeInfo[i].edgeSource.ref.type;
        edgeJson["edgeSource"]=edgeSource;
        edgeJson["edgeType"]=response.edgeInfo[i].edgeType;
        edgeJson["edgeName"]=response.edgeInfo[i].edgeName;
        sourceNodeId["uuid"]=response.edgeInfo[i].sourceNodeId.ref.uuid;
        sourceNodeId["type"]=response.edgeInfo[i].sourceNodeId.ref.type;
        sourceNodeId["datapodname"]=response.edgeInfo[i].sourceNodeId.ref.name;
        sourceNodeId["name"]=response.edgeInfo[i].sourceNodeId.attrName;
        sourceNodeId["dname"]=response.edgeInfo[i].sourceNodeId.ref.name+"."+response.edgeInfo[i].sourceNodeId.attrName;
        sourceNodeId["attributeId"]=response.edgeInfo[i].sourceNodeId.attrId;
        sourceNodeId["attrType"]=response.edgeInfo[i].sourceNodeId.attrType;
        edgeJson["sourceNodeId"]=sourceNodeId;
        edgeJson["sourceNodeType"]=response.edgeInfo[i].sourceNodeType;
        targetNodeId["uuid"]=response.edgeInfo[i].targetNodeId.ref.uuid;
        targetNodeId["type"]=response.edgeInfo[i].targetNodeId.ref.type;
        targetNodeId["datapodname"]=response.edgeInfo[i].targetNodeId.ref.name;
        targetNodeId["name"]=response.edgeInfo[i].targetNodeId.attrName;
        targetNodeId["dname"]=response.edgeInfo[i].targetNodeId.ref.name+"."+response.edgeInfo[i].targetNodeId.attrName;
        targetNodeId["attributeId"]=response.edgeInfo[i].targetNodeId.attrId;
        targetNodeId["attrType"]=response.edgeInfo[i].targetNodeId.attrType;
        edgeJson["targetNodeId"]=targetNodeId;
        edgeJson["targetNodeType"]=response.edgeInfo[i].targetNodeType;
        var edgePropertiesArr=[];
        if(response.edgeInfo[i].edgeProperties !=null){
          for(var j=0;j<response.edgeInfo[i].edgeProperties.length;j++){
            var edgeProperties={};
            edgeProperties["uuid"]=response.edgeInfo[i].edgeProperties[j].ref.uuid;
            edgeProperties["type"]=response.edgeInfo[i].edgeProperties[j].ref.type;
            edgeProperties["datapodname"]=response.edgeInfo[i].edgeProperties[j].ref.name;
            edgeProperties["name"]=response.edgeInfo[i].edgeProperties[j].attrName;
            edgeProperties["dname"]=response.edgeInfo[i].edgeProperties[j].ref.name+"."+response.edgeInfo[i].edgeProperties[j].attrName;
            edgeProperties["attributeId"]=response.edgeInfo[i].edgeProperties[j].attrId;
            edgeProperties["attrType"]=response.edgeInfo[i].edgeProperties[j].attrType;
            edgeProperties["id"] = response.edgeInfo[i].edgeProperties[j].ref.uuid+"_"+response.edgeInfo[i].edgeProperties[j].attrId;
            edgePropertiesArr[j]=edgeProperties;
            this.getAllAttributeBySource(edgePropertiesArr[j],i,'edge')
          }
        }
        var highlightInfo={};
        var propertyId={};
        if(response.edgeInfo[i].highlightInfo){
          highlightInfo["selectType"]=response.edgeInfo[i].highlightInfo.type;
          propertyId["uuid"]=response.edgeInfo[i].highlightInfo.propertyId.ref.uuid;
          propertyId["type"]=response.edgeInfo[i].highlightInfo.propertyId.ref.type;
          propertyId["datapodname"]=response.edgeInfo[i].highlightInfo.propertyId.ref.name;
          propertyId["name"]=response.edgeInfo[i].highlightInfo.propertyId.attrName;
          propertyId["dname"]=response.edgeInfo[i].highlightInfo.propertyId.ref.name+"."+response.edgeInfo[i].highlightInfo.propertyId.attrName;
          propertyId["attributeId"]=response.edgeInfo[i].highlightInfo.propertyId.attrId;
          propertyId["attrType"]=response.edgeInfo[i].highlightInfo.propertyId.attrType;
          propertyId["id"] =response.edgeInfo[i].highlightInfo.propertyId.ref.uuid+"_"+response.edgeInfo[i].highlightInfo.propertyId.attrId;
          highlightInfo["propertyId"]=propertyId;
          highlightInfo["value"]=response.edgeInfo[i].highlightInfo.type+","+response.edgeInfo[i].highlightInfo.propertyId.attrName;
          highlightInfo["propertyInfoTableArray"]=response.edgeInfo[i].highlightInfo.propertyInfo;
          edgeJson["highlightInfo"]=highlightInfo;
        }
        else{
          edgeJson["highlightInfo"]=[{'value':''}];
        }
        edgeJson["allAttributeInto"]=[]
        edgeJson["edgeProperties"]=edgePropertiesArr;
        edgeInfo[i]=edgeJson;
       
      }
      this.edgeTableArray=edgeInfo;
      
    }

  }
  getAllAttributeBySource(data,index,type2){
    
      this._commonService.getAllAttributeBySource(data.uuid,data.type)
      .subscribe(
      response => {
        this.searchAttr={}
        this.searchAttr["type"]=type2
        this.searchAttr["index"]=index
        this.response=response
        this.onSuccessAttributeBySource(response)
      },
      error => console.log("Error :: " + error));    

  }
  
  submit() {
    var upd_tag="N"
		// this.isSubmitInProgress = true;
		// this.isSubmitEnable = false;
		// this.myform.$dirty = true;
		var graphpodJson = {}
		graphpodJson["uuid"] = this.graphData.uuid
		graphpodJson["name"] = this.graphData.name
		graphpodJson["desc"] = this.graphData.desc
		graphpodJson["active"] = this.graphData.active==true?'Y':'N';
		graphpodJson["published"] = this.graphData.published==true?'Y':'N';
		var tagArray = [];
    
        if (this.tags != null) {
          for (var counttag = 0; counttag < this.tags.length; counttag++) {
            tagArray[counttag] = this.tags[counttag].value;
          }
        }
        graphpodJson['tags'] = tagArray;

		var nodeInfo=[];
		if(this.nodeTableArray){
			for(var i=0;i<this.nodeTableArray.length;i++){
				var nodeJson={};
				var nodeSource={}
				var nodeSourceRef={};
				var nodeId={}
				var nodeIdRef={}
				var nodeName={}
				var nodeNameRef={}
				var nodeSize={}
				var nodeSizeRef={}
				var nodePropertiesArry=[];
				nodeSourceRef["uuid"]=this.nodeTableArray[i].nodeSource.uuid;
				nodeSourceRef["type"]=this.nodeTableArray[i].nodeSource.type;
				nodeSource["ref"]=nodeSourceRef;
				nodeJson["nodeSource"]=nodeSource;
				nodeIdRef["uuid"]=this.nodeTableArray[i].nodeId.uuid;
				nodeIdRef["type"]=this.nodeTableArray[i].nodeId.type
				nodeId["ref"]=nodeIdRef;
				nodeId["attrId"]=this.nodeTableArray[i].nodeId.attributeId;
				nodeId["attrType"]=this.nodeTableArray[i].nodeId.attrType;
        nodeJson["nodeId"]=nodeId;
        
				nodeJson["nodeType"]=this.nodeTableArray[i].nodeType;
				nodeJson["nodeIcon"]=this.nodeTableArray[i].nodeIcon;
				nodeNameRef["uuid"]=this.nodeTableArray[i].nodeName.uuid;
				nodeNameRef["type"]=this.nodeTableArray[i].nodeName.type;
				nodeName["ref"]=nodeNameRef;
				
				nodeName["attrId"]=this.nodeTableArray[i].nodeName.attributeId;
				nodeName["attrType"]=this.nodeTableArray[i].nodeName.attrType;
				nodeJson["nodeName"]=nodeName;
				if(this.nodeTableArray[i].nodeSize){
					nodeSizeRef["uuid"]=this.nodeTableArray[i].nodeSize.uuid;
					nodeSizeRef["type"]=this.nodeTableArray[i].nodeSize.type;
					nodeSize["ref"]=nodeSizeRef;
					nodeSize["attrId"]=this.nodeTableArray[i].nodeSize.attributeId;
					nodeSize["attrType"]=this.nodeTableArray[i].nodeSize.attrType;
					nodeJson["nodeSize"]=nodeSize;
				}else{
					nodeJson["nodeSize="]=null;
				}
				for(var j=0;j<this.nodeTableArray[i].nodeProperties.length;j++){
					var nodeProperties={}
					var nodePropertiesRef={}
					nodePropertiesRef["uuid"]=this.nodeTableArray[i].nodeProperties[j].uuid;
					nodePropertiesRef["type"]=this.nodeTableArray[i].nodeProperties[j].type;
					nodeProperties["ref"]=nodeNameRef;
          nodeProperties["attrId"]=this.nodeTableArray[i].nodeProperties[j].attributeId;
          
          nodeProperties["attrType"]=this.nodeTableArray[i].nodeProperties[j].attrType;
          
					nodePropertiesArry[j]=nodeProperties;
				}
				nodeJson["nodeProperties"]=nodePropertiesArry;

				var highlightInfo={};
				var propertyId={};
				var propertyIdRef={};
				highlightInfo["type"]=this.nodeTableArray[i].highlightInfo.selectType;
				propertyIdRef["type"]=this.nodeTableArray[i].highlightInfo.propertyId.type;
				propertyIdRef["uuid"]=this.nodeTableArray[i].highlightInfo.propertyId.uuid;
				propertyId["ref"]=propertyIdRef;
				propertyId["attrId"]=this.nodeTableArray[i].highlightInfo.propertyId.attributeId;
				propertyId["attrType"]=this.nodeTableArray[i].highlightInfo.propertyId.attrType;
				highlightInfo["propertyId"]=propertyId;
				var propertyInfoArray=[];
				if(this.nodeTableArray[i].highlightInfo.propertyInfoTableArray.length >0){
					for (var j=0;j<this.nodeTableArray[i].highlightInfo.propertyInfoTableArray.length;j++){
						var propertyInfo={};
						propertyInfo["propertyName"]=this.nodeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyName;
						propertyInfo["propertyValue"]=this.nodeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyValue;
					    propertyInfoArray[j]=propertyInfo;
					}
				}
				highlightInfo["propertyInfo"]=propertyInfoArray;
				nodeJson["highlightInfo"]=highlightInfo

				var nodeBackgroundInfo={};
				var NBPropertyId={};
				var NBPropertyIdRef={};
				nodeBackgroundInfo["type"]=this.nodeTableArray[i].nodeBackgroundInfo.selectType;
				NBPropertyIdRef["type"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyId.type;
				NBPropertyIdRef["uuid"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyId.uuid;
				NBPropertyId["ref"]=NBPropertyIdRef;
				NBPropertyId["attrId"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyId.attributeId;
				NBPropertyId["attrType"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyId.attrType;
				nodeBackgroundInfo["propertyId"]=NBPropertyId;
				var NBPropertyInfoArray=[];
				if(this.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray.length >0){
					for (var j=0;j<this.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray.length;j++){
						var propertyInfo={};
						propertyInfo["propertyName"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray[j].propertyName;
						propertyInfo["propertyValue"]=this.nodeTableArray[i].nodeBackgroundInfo.propertyInfoTableArray[j].propertyValue;
					    NBPropertyInfoArray[j]=propertyInfo;
					}
				}
				nodeBackgroundInfo["propertyInfo"]=NBPropertyInfoArray;
				nodeJson["nodeBackgroundInfo"]=nodeBackgroundInfo
				nodeInfo[i]=nodeJson
			}
	    }
		graphpodJson["nodeInfo"]=nodeInfo;
		var edgeInfo=[];
		if(this.edgeTableArray){
			for(var i=0;i<this.edgeTableArray.length;i++){
				var edgeJson={};
				var edgePropertiesArry=[];
				var edgeSource={}
				var edgeSourceRef={};
				var sourceNodeId={};
				var sourceNodeIdRef={};
				var targetNodeIdId={};
				var targetNodeIdRef={};
				edgeSourceRef["uuid"]=this.edgeTableArray[i].edgeSource.uuid;
				edgeSourceRef["type"]=this.edgeTableArray[i].edgeSource.type
				edgeSource["ref"]=edgeSourceRef;
				edgeJson["edgeSource"]=edgeSource;
				edgeJson["edgeId"]=i
				edgeJson["edgeType"]=this.edgeTableArray[i].edgeType;
				edgeJson["edgeName"]=this.edgeTableArray[i].edgeName

				for(var j=0;j<this.edgeTableArray[i].edgeProperties.length;j++){
					var edgeProperties={}
					var edgePropertiesRef={}
					edgePropertiesRef["uuid"]=this.edgeTableArray[i].edgeProperties[j].uuid;
					edgePropertiesRef["type"]=this.edgeTableArray[i].edgeProperties[j].type;
					edgeProperties["ref"]=edgePropertiesRef;
					edgeProperties["attrId"]=this.edgeTableArray[i].edgeProperties[j].attributeId;
					edgeProperties["attrType"]=this.edgeTableArray[i].edgeProperties[j].attrType;
					edgePropertiesArry[j]=edgeProperties;
				}
				edgeJson["edgeProperties"]=edgePropertiesArry;
                var highlightInfo={};
				var propertyId={};
				var propertyIdRef={};
				highlightInfo["type"]=this.edgeTableArray[i].highlightInfo.selectType;
				propertyIdRef["type"]=this.edgeTableArray[i].highlightInfo.propertyId.type;
				propertyIdRef["uuid"]=this.edgeTableArray[i].highlightInfo.propertyId.uuid;
				propertyId["ref"]=propertyIdRef;
				propertyId["attrId"]=this.edgeTableArray[i].highlightInfo.propertyId.attributeId;
				propertyId["attrType"]=this.edgeTableArray[i].highlightInfo.propertyId.attrType;
				highlightInfo["propertyId"]=propertyId;
				var propertyInfoArray=[];
				if(this.edgeTableArray[i].highlightInfo.propertyInfoTableArray.length >0){
					for (var j=0;j<this.edgeTableArray[i].highlightInfo.propertyInfoTableArray.length;j++){
						var propertyInfo={};
						propertyInfo["propertyName"]=this.edgeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyName;
						propertyInfo["propertyValue"]=this.edgeTableArray[i].highlightInfo.propertyInfoTableArray[j].propertyValue;
					    propertyInfoArray[j]=propertyInfo;
					}
				}
				highlightInfo["propertyInfo"]=propertyInfoArray;
				edgeJson["highlightInfo"]=highlightInfo

				sourceNodeIdRef["uuid"]=this.edgeTableArray[i].sourceNodeId.uuid;
				sourceNodeIdRef["type"]=this.edgeTableArray[i].sourceNodeId.type;
				sourceNodeId["ref"]=sourceNodeIdRef;
				sourceNodeId["attrId"]=this.edgeTableArray[i].sourceNodeId.attributeId;
				sourceNodeId["attrType"]=this.edgeTableArray[i].sourceNodeId.attrType;
				edgeJson["sourceNodeId"]=sourceNodeId;
				edgeJson["sourceNodeType"]=this.edgeTableArray[i].sourceNodeType;

				targetNodeIdRef["uuid"]=this.edgeTableArray[i].targetNodeId.uuid;
				targetNodeIdRef["type"]=this.edgeTableArray[i].targetNodeId.type;
				targetNodeIdId["ref"]=targetNodeIdRef;
				targetNodeIdId["attrId"]=this.edgeTableArray[i].targetNodeId.attributeId;
				targetNodeIdId["attrType"]=this.edgeTableArray[i].targetNodeId.attrType;
				edgeJson["targetNodeId"]=targetNodeIdId;
				edgeJson["targetNodeType"]=this.edgeTableArray[i].targetNodeType;

				edgeInfo[i]=edgeJson;
			}
	    }
		graphpodJson["edgeInfo"]=edgeInfo;
		console.log(JSON.stringify(graphpodJson));
    this._commonService.submit("graphpod", graphpodJson,upd_tag).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.msgs = [];
    this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Graphpod Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/graphAnalysis/graphpod', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/graphAnalysis/graphpod', uuid, version, 'true']);
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
  addEdgeRow(){
    if (this.edgeTableArray == null) {
      this.edgeTableArray = [];
      //this.nodeTableArray["nodeSource"]={}
		}
		var nodeTable = {};
		nodeTable["id"]=this.edgeTableArray.length;
    nodeTable["edgeType"];
    nodeTable["edgeName"];
		nodeTable["edgeProperties"]=null;
    nodeTable["allAttributeInto"]=[];
    nodeTable["sourceNodeId"]=[];
    nodeTable["targetNodeId"]=[];
    nodeTable["edgeSource"]={}
    nodeTable["highlightInfo"]={};

		this.edgeTableArray.splice(this.edgeTableArray.length, 0, nodeTable);
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
		else{
      this.selectType="datapod"
				// this.selectAttr=this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety];
				// if(this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety]){
				// 	this.selectType=this.edgeTableArray[this.searchAttr.index][this.searchAttr.proprety].type;
				// }else{
				// 	this.selectType=selectType
				// }
				this.onChangeType();

		}
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
      allname["value"]["attrType"] = response[n]['attrType'];
      allname["value"]["attributeId"] = response[n]['attributeId'];
      allname["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
      allname["value"]["type"] = 'datapod';
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
  addHighlightInfo(index,type,propertyType){
    this.heading=propertyType=='nodeBackgroundInfo'?'Node Details':'Highlight Details'
        if(type =='node'){
          this.nodeColorInfo=(propertyType=='nodeBackgroundInfo'?this.nodeBackGroundColor:this.nodeHighlightColor);
          if(this.nodeTableArray[index][propertyType]){
            this.highlightInfo=this.nodeTableArray[index][propertyType];
            this.propertyInfoTableArray=this.highlightInfo.propertyInfoTableArray;
            this.highlightInfo.type=type;
            this.highlightInfo.caption=propertyType=='nodeBackgroundInfo'?'Node Background':'Node Highlight'
            this.highlightInfo.propertyType=propertyType;
    
          
          }else{
            this.highlightInfo={};
            this.highlightInfo.selectType="category";
            this.highlightInfo.type=type;
            this.highlightInfo.propertyType=propertyType;
            this.highlightInfo.caption=propertyType=='nodeBackgroundInfo'?'Node Background':'Node Highlight'
            this.propertyInfoTableArray=[];
            this.addPropertyInfoRow();
          }
          
          this.highlightInfo.index=index
          this.allAttr=this.nodeTableArray[index].allAttributeInto;
          console.log(this.nodeHighlightType)
        }
        if(type =='edge'){
          this.nodeColorInfo=this.edgeHighlightColor;
          if(this.edgeTableArray[index][propertyType]){
            this.highlightInfo=this.edgeTableArray[index][propertyType];
            this.propertyInfoTableArray=this.highlightInfo.propertyInfoTableArray;
            this.highlightInfo.type=type;
            this.highlightInfo.caption='Edge Highlight'
            this.highlightInfo.propertyType=propertyType;
          }else{
            this.highlightInfo={};
            this.highlightInfo.selectType="category";
            this.highlightInfo.type=type;
            this.highlightInfo.propertyType=propertyType;
            this.highlightInfo.caption='Edge Highlight'
            this.propertyInfoTableArray=[];
            this.addPropertyInfoRow();
          }
          
          this.highlightInfo.index=index
          this.allAttr=this.edgeTableArray[index].allAttributeInto;
    
        }
        // setTimeout(function(){$('#addHiglightInfo').modal({
        //   backdrop: 'static',
        //   keyboard: false
        // }); }, 100);
        this.showBgModel=true
      }
      close(){
        this.showBgModel=false
        this.showAttrModel=false
      }
      // removePropertyInfoRow(){
      //   var newDataList = [];
      //   this.selectedAllPropertyRow = false;
      //   angular.forEach(this.propertyInfoTableArray, function (selected) {
      //     if (!selected.selected) {
      //       newDataList.push(selected);
      //     }
      //   });
      //   this.propertyInfoTableArray = newDataList;
      // }
    
      addPropertyInfoRow(){
        if (this.propertyInfoTableArray == null) {
          this.propertyInfoTableArray = [];
        }
        var propertyTable = {};
        propertyTable["id"]=this.propertyInfoTableArray.length;
      
        propertyTable["name"];
        propertyTable["propertyValue"]='#61595e';
        this.propertyInfoTableArray.splice(this.propertyInfoTableArray.length, 0, propertyTable);
        //setTimeout(function(){this.myHighlightForm['propertyName'+propertyTable.id].$invalid=false; }, 1);
        
      }
  SubmitSearchAttr(){
    this.showAttrModel=false
		console.log(this.nodeTableArray);
		if(this.searchAttr && this.searchAttr["type"]=='node'){
			this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]]=this.allDatapod["defaultoption"]//this.selectAttr;
      this.nodeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]].type=this.selectType;
      
			this.nodeTableArray[this.searchAttr["index"]]["nodeProperties"]=null;
			this.onChangeDatapod();
		}else{
			this.edgeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]]=this.allDatapod["defaultoption"]//this.selectAttr;
			this.edgeTableArray[this.searchAttr["index"]][this.searchAttr["proprety"]].type=this.selectType;
		//	setTimeout(function () {
				this.edgeTableArray[this.searchAttr["index"]]["edgeProperties"]=null;
		//	});
			
			this.onChangeDatapod();
		}
		
  }
  // hideDropdown(test){
  //   document.querySelector('ui-dropdown-label').style.Background=test
  // }
  onChangeDatapod(){
    this._commonService.getAllAttributeBySource(this.allDatapod["defaultoption"]["uuid"],this.selectType)
    .subscribe(
    response => {
      this.onSuccessAttributeBySource(response)
    },
    error => console.log("Error :: " + error));

  }
  SubmitHighlightInfo(){
    //$('#addHiglightInfo').modal('hide');
    this.showBgModel=false
		if(this.highlightInfo.type=='node'){
			this.highlightInfo.value=this.highlightInfo.selectType+","+this.highlightInfo.propertyId.label;
			this.highlightInfo.propertyInfoTableArray=this.propertyInfoTableArray;
			this.nodeTableArray[this.highlightInfo.index][this.highlightInfo.propertyType]=this.highlightInfo;
		}
		if(this.highlightInfo.type=='edge'){
			this.highlightInfo.value=this.highlightInfo.selectType+","+this.highlightInfo.propertyId.label;
			this.highlightInfo.propertyInfoTableArray=this.propertyInfoTableArray;
			this.edgeTableArray[this.highlightInfo.index][this.highlightInfo.propertyType]=this.highlightInfo;
		}
	
	}
  onSuccessAttributeBySource(response){
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      allname["value"]["attrType"] = response[n]['attrType'];
      allname["value"]["attributeId"] = response[n]['attributeId'];
      allname["value"]["type"] = 'datapod';
      allname["value"]["datapodname"] = response[n]['name'];
      allname["value"]["name"] = response[n]['attrName'];
      allname["value"]["dname"] = response[n]['name']+"."+response[n]['attrName'];
      allname["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
      temp[n] = allname;
    }
    this.allAttr = temp
     
    if(this.searchAttr["type"] =='node'){
      let temp1 = []
      let tempFilter1=[]
      let tempMultiselect1=[]
      for (const n in response) {
        let allname1 = {};
        let allnameFilter1={}
        let addMultiselect1={}
        allname1["label"] = response[n]['name'];
        allname1["value"] = {};
        allname1["value"]["label"] = response[n]['name'];
        allname1["value"]["uuid"] = response[n]['uuid'];
        allname1["value"]["attrType"] = response[n]['attrType'];
        allname1["value"]["attributeId"] = response[n]['attributeId'];
        allname1["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
        allname1["value"]["type"] = 'datapod';
        temp1[n] = allname1;
        if (response[n]["attrType"] == 'integer' || response[n]["attrType"] == 'float' )
        {
          allnameFilter1["label"] = response[n]['name'];
          allnameFilter1["value"] = {};
          allnameFilter1["value"]["label"] = response[n]['name'];
          allnameFilter1["value"]["uuid"] = response[n]['uuid'];
          allnameFilter1["value"]["attrType"] = response[n]['attrType'];
          allnameFilter1["value"]["attributeId"] = response[n]['attributeId'];
          allnameFilter1["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
          allnameFilter1["value"]["type"] = 'datapod';
          tempFilter1[n] = allnameFilter1;
        }
        addMultiselect1["id"]=response[n]['uuid']+"_"+response[n]['attributeId']
        addMultiselect1["itemName"]=response[n]['name']
        addMultiselect1["uuid"] = response[n]['uuid'];    
        addMultiselect1["type"] = 'datapod';    
        addMultiselect1["datapodname"] = response[n]['name'];
        addMultiselect1["name"] = response[n]['attrName'];
        addMultiselect1["attributeId"] = response[n]['attributeId'];
        addMultiselect1["attrType"] = response[n]['attrType'];
        
        tempMultiselect1[n]=addMultiselect1
      }
      this.nodeTableArray[this.searchAttr["index"]].allAttributeInto = temp1
      this.nodeTableArray[this.searchAttr["index"]].size = tempFilter1
      this.nodeTableArray[this.searchAttr["index"]].NodePropertiers = tempMultiselect1
        //this.nodeTableArray[this.searchAttr["index"]].allAttributeInto=response;
    }
    else{
      let temp = []
      let tempFilter=[]
      let tempMultiselect=[]
      for (const n in response) {
        let allname2 = {};
        let allnameFilter={}
        let addMultiselect={}
       
       
        allname2["label"] = response[n]['name'];
        allname2["value"] = {};
        allname2["value"]["datapodname"] = response[n]['name'];
        allname2["value"]["name"] = response[n]['attrName'];
        allname2["value"]["dname"] = response[n]['name']+"."+response[n]['attrName'];
        allname2["value"]["label"] = response[n]['name'];
        allname2["value"]["uuid"] = response[n]['uuid'];
        allname2["value"]["attrType"] = response[n]['attrType'];
        allname2["value"]["attributeId"] = response[n]['attributeId'];
        allname2["value"]["type"] = 'datapod';
        allname2["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
        temp[n] = allname2;
        if (response[n]["attrType"] == 'integer' || response[n]["attrType"] == 'float' )
        {
          
          allnameFilter["label"] = response[n]['name'];
          allnameFilter["value"] = {};
          allnameFilter["value"]["datapodname"] = response[n]['name'];
          allnameFilter["value"]["name"] = response[n]['attrName'];
          allnameFilter["value"]["dname"] = response[n]['name']+"."+response[n]['attrName'];
          allnameFilter["value"]["label"] = response[n]['name'];
          allnameFilter["value"]["uuid"] = response[n]['uuid'];
          allnameFilter["value"]["attrType"] = response[n]['attrType'];
          allnameFilter["value"]["attributeId"] = response[n]['attributeId'];
          allnameFilter["value"]["type"] = 'datapod';
          allnameFilter["value"]["id"] = response[n]['uuid']+"_"+response[n]['attributeId'];
          tempFilter[n] = allnameFilter;
        }
        addMultiselect["id"]=n
        addMultiselect["itemName"]=response[n]['name']
        addMultiselect["uuid"] = response[n]['uuid'];
        tempMultiselect[n]=addMultiselect
      }
      this.edgeTableArray[this.searchAttr["index"]].allAttributeInto = temp
      this.edgeTableArray[this.searchAttr["index"]].size = tempFilter
      this.edgeTableArray[this.searchAttr["index"]].NodePropertiers = tempMultiselect
     // this.edgeTableArray[this.searchAttr.index].allAttributeInto=response;
    }
   // console.log(response)
    // if(!this.selectAttr){
    //   this.selectAttr=this.allAttr[0]
    // }
    console.log(JSON.stringify(this.nodeTableArray))
    console.log(JSON.stringify(this.edgeTableArray))
  }
  removeNodeRow(){
    let newDataList = [];
    this.selectedAllNodeRow = false;
    this.nodeTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    
    this.nodeTableArray = newDataList;
  }
  allNodeRow() {
    if (!this.selectedAllNodeRow) {
      this.selectedAllNodeRow = true;
    }
    else {
      this.selectedAllNodeRow = false;
    }
    this.nodeTableArray.forEach(filter => {
      filter.selected = this.selectedAllNodeRow;
    });
  }
  removeEdgeRow(){
    let newDataList = [];
    this.selectedAllEdgeRow = false;
    this.edgeTableArray.forEach(selectedEdge => {
      if (!selectedEdge.selectedEdge) {
        newDataList.push(selectedEdge);
      }
    });
    
    this.edgeTableArray = newDataList;
  }
  allEdgeRow() {
    if (!this.selectedAllEdgeRow) {
      this.selectedAllEdgeRow = true;
    }
    else {
      this.selectedAllEdgeRow = false;
    }
    this.edgeTableArray.forEach(filter1 => {
      filter1.selectedEdge = this.selectedAllEdgeRow;
    });
  }
  removePropertyInfoRow(){
    let newDataList = [];
    this.selectedAllPropertyRow = false;
    this.propertyInfoTableArray.forEach(selectedPinfo => {
      if (!selectedPinfo.selectedPinfo) {
        newDataList.push(selectedPinfo);
      }
    });
    
    this.propertyInfoTableArray = newDataList;
    
  }
  allPropertyRow() {
    if (!this.selectedAllPropertyRow) {
      this.selectedAllPropertyRow = true;
    }
    else {
      this.selectedAllPropertyRow = false;
    }
    this.propertyInfoTableArray.forEach(filter1 => {
      filter1.selectedPinfo = this.selectedAllPropertyRow;
    });
  }
  // filterByAttrType = function () {
	// 	return function (item) {
			
	// 		if (item.attrType == 'integer' || item.attrType == 'float' )
	// 		{
	// 			return true;
	// 		}
	// 		return false;
	// 	};
	// };
}

