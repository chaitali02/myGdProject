import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { datasource } from '../../data-preparation/datapod/datapod';
import { Datastore } from '../../metadata/domain/domain.datastore';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-datastore',
  templateUrl: './datastore.template.html',
  styleUrls: ['./datastore.component.css']
})
export class DatastoreComponent implements OnInit {

  breadcrumbDataFrom: any;
  showDatastore: any;
  datastore: any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  locked: any;
  depends: any;
  meta: any;
  metaIdOnType: { 'value': string; 'label': string; }[];
  metaId: any;
  allName: any;
  allNamesMetaId: any;
  allNamesExecId: any;
  datastoreDatapodMeta: any;
  lhsdatapodattributeDatastoreMeta: any;
  datastoreDatapodExec: any;
  lhsdatapodattributeDatastoreExec: any;
  msgs: any;
  execIdOnType: { 'value': string; 'label': string; }[];
  exec: any;
  execId: any;
  metaIdUuid: any;
  execIdUuid: any;
  metaIdname: any;
  execIdname: any;
  datastoreAllMata: any;
  location: any;
  isSubmitEnable: any;

  isHomeEnable: boolean = false
  showGraph: boolean;
  isDependencyGraphEnable: boolean = true;
  isShowReportData: boolean = true;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showDatastore = true;
    this.showGraph = false
    this.datastore = {};
    this.datastore["active"] = true
    this.metaId = {};
    this.execId = {};
    this.isSubmitEnable = true;
    this.breadcrumbDataFrom = [{
      "caption": "admin",
      "routeurl": "/app/list/datastore"
    },
    {
      "caption": "Datastore",
      "routeurl": "/app/list/datastore"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];
    this.metaIdOnType = [
      { 'value': 'rule', 'label': 'rule' },
      { 'value': 'map', 'label': 'map' },
      { 'value': 'datapod', 'label': 'datapod' }
    ];
    this.execIdOnType = [
      { 'value': 'loadExec', 'label': 'loadExec' },
      { 'value': 'mapExec', 'label': 'mapExec' },
      { 'value': 'dqExec', 'label': 'dqExec' },
      { 'value': 'ruleExec', 'label': 'ruleExec' },
      { 'value': 'profileExec', 'label': 'profileExec' }
    ];
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
    })
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'datastore')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('datastore', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.datastore = response;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = response.createdBy.ref.name;
    this.datastore.published = response["published"] == 'Y' ? true : false;
    this.datastore.locked = response["locked"] == 'Y' ? true : false;
    this.datastore.active = response["active"] == 'Y' ? true : false
    this.version = response['version'];

    this.breadcrumbDataFrom[2].caption = this.datastore.name;

    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.datastore.tags = tags;
    }//End I
    this.meta = response["metaId"]["ref"]["type"];
    this.metaId["uuid"] = response["metaId"]["ref"]["uuid"]
    this.metaId["name"] = response["metaId"]["ref"]["name"]

    this.metaIdUuid = response["metaId"]["ref"]["uuid"]
    this.metaIdname = response["metaId"]["ref"]["name"]

    this.exec = response["execId"]["ref"]["type"];
    this.execId["uuid"] = response["execId"]["ref"]["uuid"]
    this.execId["name"] = response["execId"]["ref"]["name"]

    this.execIdUuid = response["execId"]["ref"]["uuid"]
    this.execIdname = response["execId"]["ref"]["name"]

    this._commonService.getAllLatest(this.meta).subscribe(
      response => { this.OnSuccesgetAllLatestMeta(response) },
      error => console.log('Error :: ' + error)
    )
    this._commonService.getAllLatest(this.exec).subscribe(
      response => { this.OnSuccesgetAllLatestExec(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      temp[i] = ver;
    }
    this.VersionList = temp
  }
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'datastore')
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  selectTypeMeta() {

    this._commonService.getAllLatest(this.meta).subscribe(
      response => { this.onSuccessMetaType(response) },
      error => console.log('Error :: ' + error)
    )
  }

  onSuccessMetaType(response) {

    // this.metaId.uuid=response[0]["uuid"];
    this.OnSuccesgetAllLatestMeta(response);
  }

  OnSuccesgetAllLatestMeta(response) {

    this.allNamesMetaId = [];
    for (const i in response) {
      let allNameMeta = {};
      allNameMeta["label"] = response[i]['name'];
      allNameMeta["value"] = response[i]['uuid']
      //allName["uuid"]=response[i]['uuid']
      this.allNamesMetaId[i] = allNameMeta;
    }
  }

  selectTypeExec() {
    this._commonService.getAllLatest(this.exec).subscribe(
      response => {
        console.log(JSON.stringify(response))
        // this.execId.uuid=response[0]["uuid"]
        this.OnSuccesgetAllLatestExec(response)
      },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllLatestExec(response) {
    this.allNamesExecId = [];
    for (const i in response) {
      let allName = {};
      allName["label"] = response[i]['name'];
      allName["value"] = {};
      allName["value"]["name"] = response[i]['name']
      allName["value"]["uuid"] = response[i]['uuid']
      //allName["uuid"]=response[i]['uuid']
      this.allNamesExecId[i] = allName;
    }
  }

  submitDatastore() {
    this.isSubmitEnable = true;
    let datastoreJson = {};
    datastoreJson["uuid"] = this.datastore.uuid
    datastoreJson["name"] = this.datastore.name
    //  //let tagArray=[];
    //  const tagstemp = [];
    //  for (const t in this.tags) {
    //   //tagstemp.push(this.tags[t]["value"]);
    //  }
    // // if(this.tags.length > 0){
    // //   for(let counttag=0;counttag < this.tags.length;counttag++){
    // //     tagArray[counttag]=this.tags[counttag]["value"];
    // //   }
    // // }
    // datastoreJson["tags"]=tagstemp

    var tagArray = [];
    if (this.datastore.tags != null) {
      for (var counttag = 0; counttag < this.datastore.tags.length; counttag++) {
        tagArray[counttag] = this.datastore.tags[counttag].value;

      }
    }
    datastoreJson['tags'] = tagArray
    datastoreJson["desc"] = this.datastore.desc
    let metaId = {};
    let refMetaId = {}
    refMetaId["type"] = this.meta
    refMetaId["uuid"] = this.metaId.uuid
    refMetaId["name"] = this.metaId.name
    metaId["ref"] = refMetaId;
    datastoreJson["metaId"] = metaId;

    let execId = {};
    let refExecId = {}
    refExecId["type"] = this.exec
    refExecId["uuid"] = this.execId.uuid
    refExecId["name"] = this.execId.name
    execId["ref"] = refExecId;
    datastoreJson["execId"] = execId;

    datastoreJson["active"] = this.datastore.active == true ? 'Y' : "N"
    datastoreJson["published"] = this.datastore.published == true ? 'Y' : "N";
    datastoreJson["locked"] = this.datastore.locked == true ? 'Y' : "N";
    datastoreJson["location"] = this.datastore.location

    console.log(JSON.stringify(datastoreJson))
    this._commonService.submit("datastore", datastoreJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Datastore Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    this._location.back();
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  enableEdit(uuid, version) {
    console.log("enableEdit call.....");
  }
}