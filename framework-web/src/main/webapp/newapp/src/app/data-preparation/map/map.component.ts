import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { MapServices } from '../../metadata/services/map.service';
import { Location } from '@angular/common';
import { Message } from 'primeng/components/common/api';
import { MessageService } from 'primeng/components/common/messageservice';
import { Version } from '../../shared/version'
import { SelectItem } from 'primeng/primeng';
import { DependsOn } from './dependsOn'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-map',
  templateUrl: './map.template.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  showDropdown: boolean;
  allAutoMapTable: { value: string; label: string; }[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  selectVersion: any;
  allMapSourceAttribute: SelectItem[] = [];
  tabledata: any;
  mapTableArray: any;
  msgs: Message[] = [];
  sourceAttributeTypes: { "value": string; "label": string; }[];
  count: number;
  allMapTargetAttribute: any;
  allTargetData: any;
  allTargetNames: SelectItem[] = [];
  targets: { 'value': string; 'label': string; }[];
  target: any;
  targetdata: DependsOn;
  sourcedata: DependsOn;
  allNames: SelectItem[] = [];
  source: any;
  showMapData: boolean;
  versions: any[];
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  mapData: any;
  depends: any;
  allName: any;
  active: any;
  published: any;
  sources: any
  ruleLoadFunction: SelectItem[] = [];;
  allMapExpression: SelectItem[] = [];;
  allMapFormula: SelectItem[] = [];
  breadcrumbDataFrom: any;
  isSubmitEnable: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _mapService: MapServices) {
    this.showDropdown = false
    this.showMapData = true;
    this.mapData = {};
    this.showGraph = false
    this.isHomeEnable = false
    this.mapData["active"] = true;
    this.isSubmitEnable = true;
    this.selectVersion = { "version": "" };
    this.breadcrumbDataFrom = [{
      "caption": "Data Preparation ",
      "routeurl": "/app/list/map"
    },
    {
      "caption": "Map",
      "routeurl": "/app/list/map"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.sources = [
      { 'value': 'datapod', 'label': 'datapod' },
      { 'value': 'dataset', 'label': 'dataset' },
      { 'value': 'relation', 'label': 'relation' },
      { 'value': 'rule', 'label': 'rule' }
    ];
    this.targets = [
      { 'value': 'datapod', 'label': 'datapod' }
    ];
    this.sourceAttributeTypes = [
      { "value": "function", "label": "function" },
      { "value": "string", "label": "string" },
      { "value": "datapod", "label": "attribute" },
      { "value": "expression", "label": "expression" },
      { "value": "formula", "label": "formula" }
    ]
    this.mapTableArray = []
    this.allAutoMapTable = [
      { value: 'ByName', label: 'By Name' },
      { value: 'ByOrder', label: 'By Order' }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.sourcedata = { 'uuid': "", "label": "" }
      this.targetdata = { 'uuid': "", "label": "" }
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
      }
      else {
        this._commonService.getAllLatest("datapod").subscribe(
          response => { this.OnSuccesgetAddTarget(response) },
          error => console.log('Error :: ' + error)
        )
      }
    })
  }
  OnSuccesgetAddTarget(response) {
    //this.allTargetNames = response;
    //this.allNames=response
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp
    this.allTargetNames = temp
    this.targetdata.uuid = this.allTargetNames[0]["value"]["uuid"]
    this.sourcedata.uuid = this.allNames[0]["value"]["uuid"]
    this.target = "datapod";
    this.source = "datapod"
    this._commonService.getAllAttributeBySource(this.targetdata.uuid, this.target).subscribe(
      response => { this.OnSuccesgetAllAttributeByDataPodSourceAdd(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeByDataPodSourceAdd(response) {
    this.allMapTargetAttribute = response
    for (var i = 0; i < this.allMapTargetAttribute.length; i++) {
      var mapinfo = {};
      var obj = {}
      obj["value"] = "string";
      obj["label"] = "string";
      mapinfo["isSourceAtributeSimple"] = true;
      mapinfo["isSourceAtributeDatapod"] = false;
      mapinfo["isSourceAtributeFormula"] = false;
      mapinfo["isSourceAtributeExpression"] = false;
      mapinfo["sourceAttributeType"] = obj
      this.mapTableArray[i] = mapinfo;
      //this.mapTableArray[i]["sourceattribute"]["trackName"]=""
    }
    // this.getAllAttributeBySource();
  }
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/map']);

  }
  onChangeActive(event) {
    if (event === true) {
      this.mapData.active = 'Y';
    }
    else {
      this.mapData.active = 'N';
    }
  }
  onChangePublish(event) {
    if (event === true) {
      this.mapData.published = 'Y';
    }
    else {
      this.mapData.published = 'N';
    }
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'map')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('map', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.mapData = response;
    this.createdBy = response.createdBy.ref.name;
    this.uuid = response.uuid
    this.mapData.published = response["published"] == 'Y' ? true : false
    this.mapData.active = response["active"] == 'Y' ? true : false
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
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
    this.source = response["source"]["ref"]["type"]
    // this.sourcedata["uuid"]=response["source"]["ref"]["uuid"]
    // this.sourcedata["name"]=response["source"]["ref"]["name"]
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["source"]["ref"]["name"];
    dependOnTemp.uuid = response["source"]["ref"]["uuid"];
    this.sourcedata = dependOnTemp
    this.target = response["target"]["ref"]["type"]
    let targetData: DependsOn = new DependsOn();
    targetData.label = response["target"]["ref"]["name"];
    targetData.uuid = response["target"]["ref"]["uuid"];
    this.targetdata = targetData
    // this.targetdata["uuid"]=response["target"]["ref"]["uuid"]
    // this.targetdata["name"]=response["target"]["ref"]["name"]
    this.breadcrumbDataFrom[2].caption = this.mapData.name;
    let mapjson = {};
    mapjson["mapData"] = response;
    let maparray = [];
    for (let i = 0; i < response.attributeMap.length; i++) {
      let attributemapjson = {};
      if (response.attributeMap[i].sourceAttr.ref.type == "datapod" || response.attributeMap[i].sourceAttr.ref.type == "dataset" || response.attributeMap[i].sourceAttr.ref.type == "rule") {
        var sourceattribute = {}
        sourceattribute["label"] = response.attributeMap[i].sourceAttr.ref.name + "." + response.attributeMap[i].sourceAttr.attrName;
        sourceattribute["id"] = response.attributeMap[i].sourceAttr.ref.uuid + "_" + response.attributeMap[i].sourceAttr.attrId;
        sourceattribute["uuid"] = response.attributeMap[i].sourceAttr.ref.uuid;
        sourceattribute["type"] = response.attributeMap[i].sourceAttr.ref.type;
        sourceattribute["attributeId"] = response.attributeMap[i].sourceAttr.attrId;
        sourceattribute["dname"] = response.attributeMap[i].sourceAttr.ref.name + "." + response.attributeMap[i].sourceAttr.attrName;
        sourceattribute["trackName"] = sourceattribute["uuid"] + "_" + sourceattribute["attributeId"]
        let obj = {}
        obj["value"] = "datapod"
        obj["label"] = "attribute"
        attributemapjson["sourceAttributeType"] = obj;
        attributemapjson["isSourceAtributeSimple"] = false;
        attributemapjson["isSourceAtributeDatapod"] = true;
        attributemapjson["isSourceAtributeFormula"] = false;
        attributemapjson["isSourceAtributeExpression"] = false;
      }
      else if (response.attributeMap[i].sourceAttr.ref.type == "simple") {
        let obj = {}
        obj["value"] = "string"
        obj["label"] = "string"
        attributemapjson["sourceAttributeType"] = obj;
        attributemapjson["isSourceAtributeSimple"] = true;
        attributemapjson["sourcesimple"] = response.attributeMap[i].sourceAttr.value
        attributemapjson["isSourceAtributeDatapod"] = false;
        attributemapjson["isSourceAtributeFormula"] = false;
        attributemapjson["isSourceAtributeExpression"] = false;
        attributemapjson["isSourceAtributeFunction"] = false;

      }
      if (response.attributeMap[i].sourceAttr.ref.type == "expression") {
        let sourceexpression = {};
        sourceexpression["uuid"] = response.attributeMap[i].sourceAttr.ref.uuid;
        sourceexpression["name"] = response.attributeMap[i].sourceAttr.ref.name;
        let obj = {}
        obj["value"] = "expression"
        obj["label"] = "expression"
        attributemapjson["sourceAttributeType"] = obj;
        attributemapjson["sourceexpression"] = sourceexpression;
        attributemapjson["isSourceAtributeSimple"] = false;
        attributemapjson["isSourceAtributeDatapod"] = false;
        attributemapjson["isSourceAtributeFormula"] = false;
        attributemapjson["isSourceAtributeExpression"] = true;
        attributemapjson["isSourceAtributeFunction"] = false;
        this.getAllExpression()
      }
      if (response.attributeMap[i].sourceAttr.ref.type == "formula") {
        let sourceformula = {};
        sourceformula["uuid"] = response.attributeMap[i].sourceAttr.ref.uuid;
        sourceformula["name"] = response.attributeMap[i].sourceAttr.ref.name;
        let obj = {}
        obj["value"] = "formula"
        obj["label"] = "formula"
        attributemapjson["sourceAttributeType"] = obj;
        attributemapjson["sourceformula"] = sourceformula;
        attributemapjson["isSourceAtributeSimple"] = false;
        attributemapjson["isSourceAtributeDatapod"] = false;
        attributemapjson["isSourceAtributeFormula"] = true;
        attributemapjson["isSourceAtributeExpression"] = false;
        attributemapjson["isSourceAtributeFunction"] = false;
        this.getAllFormula()
      }
      if (response.attributeMap[i].sourceAttr.ref.type == "function") {
        let sourcefunction = {};
        sourcefunction["uuid"] = response.attributeMap[i].sourceAttr.ref.uuid;
        sourcefunction["name"] = response.attributeMap[i].sourceAttr.ref.name;
        let obj = {}
        obj["value"] = "function"
        obj["label"] = "function"
        attributemapjson["sourceAttributeType"] = obj;
        attributemapjson["sourcefunction"] = sourcefunction;
        attributemapjson["isSourceAtributeSimple"] = false;
        attributemapjson["isSourceAtributeDatapod"] = false;
        attributemapjson["isSourceAtributeFormula"] = false;
        attributemapjson["isSourceAtributeExpression"] = false;
        attributemapjson["isSourceAtributeFunction"] = true;
        this.getAllFunctions()
      }
      attributemapjson["sourceattribute"] = sourceattribute;
      let targetattribute = {}
      targetattribute["uuid"] = response.attributeMap[i].targetAttr.ref.uuid;
      targetattribute["type"] = response.attributeMap[i].targetAttr.ref.type;
      targetattribute["attributeId"] = response.attributeMap[i].targetAttr.attrId;
      attributemapjson["targetattribute"] = targetattribute;
      maparray[i] = attributemapjson
    }
    this.mapTableArray = maparray
    //console.log(JSON.stringify(this.mapTableArray))
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
    this._commonService.getAllLatest(this.target).subscribe(
      response => { this.OnSuccesgetAllLatestTarget(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response) {
    //this.allNames = response
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource()
  }
  getAllAttributeBySource() {
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeBySource(response) {
    //console.log(response)
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['dname'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['dname'];
      allname["value"]["id"] = response[n]['id'];
      temp[n] = allname;
    }
    this.allMapSourceAttribute = temp
    //this.allMapSourceAttribute = response
  }
  OnSuccesgetAllLatestTarget(response) {
    //this.allTargetNames = response
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allTargetNames = temp
    this._commonService.getAllAttributeBySource(this.targetdata.uuid, this.target).subscribe(
      response => { this.OnSuccesgetAllAttributeByDataPodSource(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllAttributeByDataPodSource(response) {
    this.allMapTargetAttribute = response
  }
  OnSuccesgetAllVersionByUuid(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['version'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['version'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.VersionList = temp
    // for (const i in response) {
    //   let ver={};
    //   ver["label"]=response[i]['version'];
    //   ver["value"]={};
    //   ver["value"]["label"]=response[i]['version'];      
    //   ver["value"]["uuid"]=response[i]['uuid']; 
    //   //allName["uuid"]=response[i]['uuid']
    //   this.VersionList[i]=ver;
    // }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  onChangeSource() {
    this.getAllAttributeBySource()
  }
  onChangeTarget() {
    //this.allMapTargetAttribute=[]

    this._commonService.getAllAttributeBySource(this.targetdata.uuid, this.target).subscribe(
      response => { this.OnSuccesgetAllAttributeByDataPodSourceAdd(response) },
      error => console.log('Error :: ' + error)
    )
  }
  selectSourceType() {
    this._commonService.getAllLatest(this.source).subscribe(
      response => {
        this.sourcedata.uuid = response[0]["uuid"]
        this.OnSuccesgetAllLatest(response)
      },
      error => console.log('Error :: ' + error)
    )

  }
  onChangeSourceAttribute(type, index) {
    if (type == "string") {
      this.mapTableArray[index].isSourceAtributeSimple = true;
      this.mapTableArray[index].isSourceAtributeDatapod = false;
      this.mapTableArray[index].isSourceAtributeFormula = false;
      this.mapTableArray[index].sourcesimple = "''";
      this.mapTableArray[index].isSourceAtributeExpression = false;
      this.mapTableArray[index].isSourceAtributeFunction = false;

    }
    else if (type == "datapod") {

      this.mapTableArray[index].isSourceAtributeSimple = false;
      this.mapTableArray[index].isSourceAtributeDatapod = true;
      this.mapTableArray[index].isSourceAtributeFormula = false;
      this.mapTableArray[index].isSourceAtributeExpression = false;
      this.mapTableArray[index].isSourceAtributeFunction = false;
      this.mapTableArray[index].sourceattribute = {}
      this.getAllAttributeBySource();
      if (this.allMapSourceAttribute && this.allMapSourceAttribute.length > 0) {
        let sourceattribute = {}
        sourceattribute["dname"] = this.allMapSourceAttribute[0]["label"]
        sourceattribute["id"] = this.allMapSourceAttribute[0]["value"]["id"];
        this.mapTableArray[index].sourceattribute = sourceattribute;
      }
    }
    else if (type == "formula") {

      this.mapTableArray[index].isSourceAtributeSimple = false;
      this.mapTableArray[index].isSourceAtributeDatapod = false;
      this.mapTableArray[index].isSourceAtributeFormula = true;
      this.mapTableArray[index].isSourceAtributeExpression = false;
      this.mapTableArray[index].isSourceAtributeFunction = false;
      // MetadataMapSerivce.getFormulaByType(this.allMapSource.defaultoption.uuid,this.sourcetype).then(function(response){onSuccessExpression(response.data)});
      // var onSuccessExpression=function(response){

      //   this.allMapLodeFormula=response.data
      // }
      this.mapTableArray[index].sourceformula = {}
      this.getAllFormula()
    }
    else if (type == "expression") {

      this.mapTableArray[index].isSourceAtributeSimple = false;
      this.mapTableArray[index].isSourceAtributeDatapod = false;
      this.mapTableArray[index].isSourceAtributeFormula = false;
      this.mapTableArray[index].isSourceAtributeExpression = true;
      this.mapTableArray[index].isSourceAtributeFunction = false;
      // MetadataMapSerivce.getExpressionByType(this.allMapSource.defaultoption.uuid,this.sourcetype).then(function(response){onSuccessExpression(response.data)});
      // var onSuccessExpression=function(response){

      //   this.allMapLodeExpression=response
      // }
      this.mapTableArray[index].sourceexpression = {}
      this.getAllExpression()
    }
    else if (type == "function") {
      this.mapTableArray[index].isSourceAtributeSimple = false;
      this.mapTableArray[index].isSourceAtributeDatapod = false;
      this.mapTableArray[index].isSourceAtributeFormula = false;
      this.mapTableArray[index].isSourceAtributeExpression = false;
      this.mapTableArray[index].isSourceAtributeFunction = true;
      this.mapTableArray[index].isSourceAtributeFunction = true;
      this.mapTableArray[index].sourcefunction = {}
      this.getAllFunctions()
    }
    // if(this.mode == undefined) {

    // }
  }
  getAllFunctions() {
    this._mapService.getAllLatestFunction("function", "N").subscribe(
      response => { this.onSuccessFunction(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.ruleLoadFunction = temp
    //this.ruleLoadFunction = response
  }
  getAllExpression() {
    this._mapService.getExpressionByType(this.sourcedata.uuid, this.source).subscribe(
      response => { this.onSuccessExpression(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessExpression(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allMapExpression = temp
    // this.allMapExpression = response
  }
  getAllFormula() {
    this._commonService.getFormulaByType(this.sourcedata.uuid, "formula").subscribe(
      response => { this.onSuccessgetAllFormula(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllFormula(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.allMapFormula = temp
    //this.allMapFormula = response
  }
  submitMap() {

    this.isSubmitEnable = true;
    var mapJson = {};
    mapJson["uuid"] = this.mapData.uuid;
    mapJson["name"] = this.mapData.name;

    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    mapJson['tags'] = tagArray
    mapJson["desc"] = this.mapData.desc;
    //mapJson["active"]=this.mapData.active;
    mapJson["active"] = this.mapData.active == true ? 'Y' : "N"
    mapJson["published"] = this.mapData.published == true ? 'Y' : "N"


    mapJson["published"] = this.mapData.published;

    var sourece = {};
    var sourceref = {};
    sourceref["uuid"] = this.sourcedata.uuid;
    sourceref["type"] = this.source;
    sourece["ref"] = sourceref;
    mapJson["source"] = sourece

    var target = {};
    var targetref = {};
    targetref["uuid"] = this.targetdata.uuid;
    targetref["type"] = this.target;
    target["ref"] = targetref;
    mapJson["target"] = target
    var attributemaparray = [];
    for (var i = 0; i < this.allMapTargetAttribute.length; i++) {
      var attributemap = {};
      attributemap["attrMapId"] = i;
      var sourceAttr = {};
      var sourceref = {};
      var targetAttr = {};
      var targetref = {};
      if (this.mapTableArray[i].sourceAttributeType.value == "string") {
        sourceref["type"] = "simple";
        sourceAttr["ref"] = sourceref;
        if (typeof this.mapTableArray[i].sourcesimple == "undefined") {
          sourceAttr["value"] = "";
        }
        else {
          sourceAttr["value"] = this.mapTableArray[i].sourcesimple;
        }

        attributemap["sourceAttr"] = sourceAttr;
      }
      else if (this.mapTableArray[i].sourceAttributeType.value == "datapod") {
        let uuid = this.mapTableArray[i].sourceattribute.id.split("_")[0]
        var attrid = this.mapTableArray[i].sourceattribute.id.split("_")[1]
        sourceref["uuid"] = uuid
        //this.mapTableArray[i].sourceattribute.uuid;
        if (this.source == "relation") {
          sourceref["type"] = "datapod";
        }
        else {
          sourceref["type"] = this.source;
        }
        sourceAttr["ref"] = sourceref;
        sourceAttr["attrId"] = attrid
        attributemap["sourceAttr"] = sourceAttr;
      }
      else if (this.mapTableArray[i].sourceAttributeType.value == "expression") {

        sourceref["type"] = "expression";
        sourceref["uuid"] = this.mapTableArray[i].sourceexpression.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr;

      }
      else if (this.mapTableArray[i].sourceAttributeType.value == "formula") {

        sourceref["type"] = "formula";
        sourceref["uuid"] = this.mapTableArray[i].sourceformula.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr;

      }
      else if (this.mapTableArray[i].sourceAttributeType.value == "function") {

        sourceref["type"] = "function";
        sourceref["uuid"] = this.mapTableArray[i].sourcefunction.uuid;
        sourceAttr["ref"] = sourceref;
        attributemap["sourceAttr"] = sourceAttr;

      }
      targetref["uuid"] = this.targetdata.uuid;
      targetref["type"] = this.target;
      targetAttr["ref"] = targetref;
      targetAttr["attrId"] = this.allMapTargetAttribute[i].attributeId;
      attributemap["targetAttr"] = targetAttr;
      attributemaparray[i] = attributemap;
    }
    mapJson["attributeMap"] = attributemaparray;
    console.log(JSON.stringify(mapJson))
    this._commonService.submit("map", mapJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Load Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);


  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataPreparation/map', uuid, version, 'false']);
  }
  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/map', uuid, version, 'true']);
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


  autoMapFeature(selectedAutoMode) {

    if (selectedAutoMode == "By Order") {
      this.emptyAttribure();
      for (let i = 0; i < this.allMapSourceAttribute.length; i++) {
        if (this.allMapSourceAttribute && this.allMapSourceAttribute.length > 0) {
          let sourceObj = {}
          this.fillSourceAttr(sourceObj, i);
          this.mapTableArray[i].sourceattribute = sourceObj;
          this.fillSourceType(i);
        }
      }
    }
    else if (selectedAutoMode == "By Name") {
      this.emptyAttribure();
      let index = 0;
      for (let i = 0; i < this.allMapSourceAttribute.length; i++) {
        var mapInfo = {};
        let sourceObj = {};
        for (let j = 0; j < this.allMapTargetAttribute.length; j++) {
          if (this.allMapTargetAttribute[j]["name"] == (this.allMapSourceAttribute[i]["label"]).split(".")[1]) {
            this.fillSourceAttr(sourceObj, i);
            index = j;
            break;
          }
          else {
            sourceObj["id"] = "";
            sourceObj["dname"] = "";
            sourceObj["label"] = "";
            sourceObj["value"] = {};
            sourceObj["value"]["label"] = "";
            sourceObj["value"]["id"] = "";
            index = i;
          }
        }
       // mapInfo["sourceattribute"] = sourceObj;
        this.mapTableArray[index]["sourceattribute"] = sourceObj;
        this.fillSourceType(index);
      }
      console.log(JSON.stringify(this.mapTableArray));
    }
  }

  fillSourceAttr(sourceObj, i) {
    sourceObj["id"] = this.allMapSourceAttribute[i]["value"]["id"];
    sourceObj["dname"] = (this.allMapSourceAttribute[i]["label"]).split(".")[1];
    sourceObj["label"] = this.allMapSourceAttribute[i]["label"];
    sourceObj["value"] = {};
    sourceObj["value"]["label"] = this.allMapSourceAttribute[i]["label"];
    sourceObj["value"]["id"] = this.allMapSourceAttribute[i]["value"]["id"];
  }
  emptyAttribure() {
    for (let i = 0; i < this.allMapTargetAttribute.length; i++) {
      let mapinfo = {};
      let obj = {}
      obj["value"] = "string";
      obj["label"] = "string";
      mapinfo["isSourceAtributeSimple"] = false;
      mapinfo["isSourceAtributeDatapod"] = true;
      mapinfo["isSourceAtributeFormula"] = false;
      mapinfo["isSourceAtributeExpression"] = false;
      mapinfo["sourceAttributeType"] = obj
      this.mapTableArray[i] = mapinfo;
    }
  }
  fillSourceType(i) {
    let obj = {};
    obj["value"] = "datapod";
    obj["label"] = "attribute";
    this.mapTableArray[i].sourceAttributeType = obj;
  }
}