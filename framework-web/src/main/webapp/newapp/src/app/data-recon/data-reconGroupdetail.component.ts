
import { Component, Input, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
@Component({
  selector: 'app-recon',
  templateUrl: './data-reconGroupdetail.template.html',
  styleUrls: []
})

export class DataReconGroupDetailComponent {
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
  datarecongroup: any
  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, private _commonService: CommonService, private _location: Location) {
    this.datarecongroup = {};
    this.datarecongroup["active"] = true
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
      "caption": " Data Reconciliation",
      "routeurl": "/app/list/recongroup"
    },
    {
      "caption": "Rule Group",
      "routeurl": "/app/list/recongroup"
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
  getAllLatest() {
    this._commonService.getAllLatest("recon").subscribe(
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
    this._commonService.getOneByUuidAndVersion(id, version, 'recongroup')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.datarecongroup = response;
    this.createdBy = response.createdBy.ref.name
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.datarecongroup.tags = tags;
    }//End If

    this.datarecongroup.published = response["published"] == 'Y' ? true : false
    this.datarecongroup.active = response["active"] == 'Y' ? true : false
    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
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
    this._commonService.getAllVersionByUuid('recongroup', this.id)
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
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'recongroup')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/recongroup']);
  }

  submit() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let reconJson = {}
    reconJson["uuid"] = this.datarecongroup.uuid
    reconJson["name"] = this.datarecongroup.name
    reconJson["desc"] = this.datarecongroup.desc
    var tagArray = [];
    if (this.datarecongroup.tags != null) {
      for (var counttag = 0; counttag < this.datarecongroup.tags.length; counttag++) {
        tagArray[counttag] = this.datarecongroup.tags[counttag].value;

      }
    }
    reconJson['tags'] = tagArray;
    reconJson["active"] = this.datarecongroup.active == true ? 'Y' : "N"
    reconJson["published"] = this.datarecongroup.published == true ? 'Y' : "N"
    let ruleInfo = [];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = {}
      let ref = {};
      ref["uuid"] = this.selectedItems[i]["uuid"];
      ref["type"] = "recon";
      rules["ref"] = ref;
      ruleInfo[i] = rules;
    }
    reconJson["ruleInfo"] = ruleInfo;
    reconJson["inParallel"] = this.datarecongroup.inParallel
    console.log(reconJson);
    this._commonService.submit("recongroup", reconJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("recongroup", response).subscribe(
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
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Recon Group Save Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "recongroup", "execute").subscribe(
      response => {
        this.showMassage('Recon Group Save and Submit Successfully', 'success', 'Success Message')
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
    this.router.navigate(['app/recongroup/createreconerulegroup', uuid, version, 'false']);
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
    this.router.navigate(['app/recongroup/createreconerulegroup', uuid, version, 'true']);
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
  clear(){
    this.selectedItems=[]
  }
  showProfileGroupePage() {
		this.showProfileGroup = true;
		this.showgraphdiv = false;
		this.graphDataStatus = false;
		this.showProfileGroupForm = true;

	}

	showMainPage(){
    this.isHomeEnable = false
   // this._location.back();
   this.showGraph = false;
  }
  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}
