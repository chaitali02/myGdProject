
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
@Component({
  selector: 'app-qualityGroup',
  templateUrl: './business-rulesgroupdetail.template.html',
  styleUrls: []
})

export class BusinessRulesGroupDetailComponent {
  showGraph: boolean;
  isHomeEnable: boolean;
  graphDataStatus: boolean;
  showProfileGroupForm: boolean;
  showgraphdiv: boolean;
  showProfileGroup: boolean;
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  IsProgerssShow: string;
  isSubmitEnable: any;
  msgs: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number, disabled: any };
  selectedItems: any
  dropdownList: any;

  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  allNames: SelectItem[] = [];
  createdBy: any;
  mode: any;
  version: any;
  uuid: any;
  id: any;
  routerUrl: any;
  datarulegroup: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, private _commonService: CommonService, private _location: Location) {
    
    this.showGraph = false;
    this.isHomeEnable = false;
    this.datarulegroup = {};
    this.datarulegroup["active"] = true
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };
    this.breadcrumbDataFrom = [{
      "caption": "Business Rules",
      "routeurl": "/app/list/rulegroup"
    },
    {
      "caption": "Rule Group ",
      "routeurl": "/app/list/rulegroup"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]


  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllVersionByUuid();
        this.getAllLatest();
        this.dropdownSettings.disabled = this.mode == "false" ? false : true
      }
      else {
        this.getAllLatest();
      }
    });
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getAllLatest() {
    this._commonService.getAllLatest("rule").subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["id"] = response[n]['uuid'];
      allname["itemName"] = response[n]['name'];
      allname["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.dropdownList = temp
  }
  getOneByUuidAndVersion(id, version) {
    this._commonService.getOneByUuidAndVersion(id, version, 'rulegroup')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.datarulegroup = response;
    this.createdBy = response.createdBy.ref.name
    this.datarulegroup.published = response["published"] == 'Y' ? true : false
    this.datarulegroup.active = response["active"] == 'Y' ? true : false
    this.datarulegroup.locked = response["locked"] == 'Y' ? true : false
    const version: Version = new Version();
    this.uuid = response.uuid;
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
      this.datarulegroup.tags = tags;
    }//End If
    let tmp = [];
    for (let i = 0; i < response.ruleInfo.length; i++) {
      let ruleinfo = {};
      ruleinfo["id"] = response.ruleInfo[i]["ref"]["uuid"];
      ruleinfo["itemName"] = response.ruleInfo[i]["ref"]["name"];
      ruleinfo["uuid"] = response.ruleInfo[i]["ref"]["uuid"];
      tmp[i] = ruleinfo;
    }
    this.selectedItems = tmp;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('rulegroup', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      this.VersionList[i] = ver;
    }
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/rulegroup']);
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  submit() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let rulegroupJson = {}
    rulegroupJson["uuid"] = this.datarulegroup.uuid
    rulegroupJson["name"] = this.datarulegroup.name
    rulegroupJson["desc"] = this.datarulegroup.desc

    var tagArray = [];
    if (this.datarulegroup.tags != null) {
      for (var counttag = 0; counttag < this.datarulegroup.tags.length; counttag++) {
        tagArray[counttag] = this.datarulegroup.tags[counttag].value;

      }
    }
    rulegroupJson['tags'] = tagArray
    rulegroupJson["active"] = this.datarulegroup.active == true ? 'Y' : "N"
    rulegroupJson["published"] = this.datarulegroup.published == true ? 'Y' : "N"
    rulegroupJson["locked"] = this.datarulegroup.locked == true ? 'Y' : "N"
    let ruleInfo = [];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = {}
      let ref = {};
      ref["uuid"] = this.selectedItems[i]["uuid"];
      ref["type"] = "rule";
      rules["ref"] = ref;
      ruleInfo[i] = rules;
    }
    rulegroupJson["ruleInfo"] = ruleInfo;
    rulegroupJson["inParallel"] = this.datarulegroup.inParallel
    console.log(rulegroupJson);
    this._commonService.submit("rulegroup", rulegroupJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("rulegroup", response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Group Save Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }

  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "rulegroup", "execute").subscribe(
      response => {
        this.showMassage('Rule Group Save and Submit Successfully', 'success', 'Success Message')
        setTimeout(() => {
          this.goBack()

        }, 1000);
      },
      error => console.log('Error :: ' + error)
    )
  }

  showMassage(msg, msgtype, msgsumary) {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.msgs.push({ severity: msgtype, summary: msgsumary, detail: msg });
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/businessRules/rulegroup', uuid, version, 'false']);
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: true
    };

  }

  showview(uuid, version) {
    this.router.navigate(['app/businessRules/rulegroup', uuid, version, 'true']);
    this.dropdownSettings = {
      singleSelection: false,
      text: "Select Attrubutes",
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      classes: "myclass custom-class",
      maxHeight: 110,
      disabled: false
    };

  }
  clear() {
    this.selectedItems = []
  }
  showProfileGroupePage() {
    this.showProfileGroup = true;
    this.showgraphdiv = false;
    this.graphDataStatus = false;
    this.showProfileGroupForm = true;

  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }
}
