import { Recon } from './../metadata/domain/domain.recon';
import { BaseEntity } from './../metadata/domain/domain.baseEntity';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { MetaType } from '../metadata/enums/metaType';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { AppHelper } from '../app.helper';
import { ReconGroup } from '../metadata/domain/domain.reconGroup';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';

@Component({
  selector: 'app-recon',
  templateUrl: './data-reconGroupdetail.template.html',
  styleUrls: []
})

export class DataReconGroupDetailComponent {
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
  versionList: SelectItem[] = [];
  allNames: SelectItem[] = [];
  createdBy: any;
  mode: any;
  version: any;
  uuid: any;
  id: any;
  routerUrl: any;
  reconGroupData: any
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isGraphError: boolean;
  isGraphInprogess: boolean;
  caretdown = 'fa fa-caret-down';
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean;
  isAdd: boolean;
  showForm: boolean = true;
  showDivGraph: boolean;
  published: boolean;
  locked: boolean;
  active: boolean;
  metatype = MetaType;

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, 
    private _commonService: CommonService, private _location: Location, public appHelper: AppHelper) {

    this.isHomeEnable = false;
    this.reconGroupData = {};
    this.reconGroupData["active"] = true
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
    this.breadcrumbDataFrom = [
      {
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
      }];

    this.isEditInprogess = false;
    this.isEditError = false;
    this.active = true;
    this.locked = false;
    this.published = false;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;

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

    this.setMode(this.mode);
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/recongroup/createreconerulegroup', uuid, version, 'false']);
    this.isEdit = true;
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

  showMainPage(uuid, version) {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.reconGroupData.name;
  }


  getAllLatest() {
    this._commonService.getAllLatest(this.metatype.RECON).subscribe(
      response => { this.onSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllLatest(response: BaseEntity[]) {
    let temp = []
    for (const n in response) {
      let allname = new MultiSelectIO();
      allname.id = response[n].uuid;
      allname.itemName = response[n].name;
      allname.uuid = response[n].uuid;
      temp[n] = allname;
    }
    this.dropdownList = temp
  }
  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(id, version, this.metatype.RECONGROUP)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.reconGroupData = response;

    this.active = this.appHelper.convertStringToBoolean(this.reconGroupData.active);
    this.locked == this.appHelper.convertStringToBoolean(this.reconGroupData.locked);
    this.published = this.appHelper.convertStringToBoolean(this.reconGroupData.published);

    const version: Version = new Version();
    this.uuid = this.reconGroupData.uuid;
    version.label = this.reconGroupData.version;
    version.uuid = this.reconGroupData.uuid;
    this.selectedVersion = version;

    let tmp = [];
    for (let i = 0; i < this.reconGroupData.ruleInfo.length; i++) {
      let ruleinfo = new MultiSelectIO();
      ruleinfo.id = this.reconGroupData.ruleInfo[i].ref.uuid;
      ruleinfo.itemName = this.reconGroupData.ruleInfo[i].ref.name;
      ruleinfo.uuid = this.reconGroupData.ruleInfo[i].ref.uuid;
      tmp[i] = ruleinfo;
    }
    this.selectedItems = tmp;
    this.isEditInprogess = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metatype.RECONGROUP, this.id)
      .subscribe(
        response => {
          this.onSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.versionList[i] = ver;
    }
  }
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metatype.RECONGROUP)
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

  submitReconGroup() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let reconJson = new ReconGroup();
    reconJson.uuid = this.reconGroupData.uuid;
    reconJson.name = this.reconGroupData.name;
    reconJson.desc = this.reconGroupData.desc;
    reconJson.tags = this.reconGroupData.tags;
    reconJson.active = this.appHelper.convertBooleanToString(this.reconGroupData.active);
    reconJson.locked = this.appHelper.convertBooleanToString(this.locked);
    reconJson.published = this.appHelper.convertBooleanToString(this.reconGroupData.published);

    let ruleInfo = [new MetaIdentifierHolder];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = new AttributeRefHolder();
      let ref = new MetaIdentifier();
      ref.uuid = this.selectedItems[i].uuid;
      ref.type = this.metatype.RECON;
      rules.ref = ref;
      ruleInfo[i] = rules;
    }
    reconJson.ruleInfo = ruleInfo;

    reconJson.inParallel = this.reconGroupData.inParallel;
    console.log(reconJson);
    this._commonService.submit(this.metatype.RECONGROUP, reconJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  onSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metatype.RECONGROUP, response).subscribe(
        response => {
          this.onSucessGetOneById(response);
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
  onSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, this.metatype.RECONGROUP, "execute").subscribe(
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
  clear() {
    this.selectedItems = []
  }
  showProfileGroupePage() {
    this.showProfileGroup = true;
    this.showgraphdiv = false;
    this.graphDataStatus = false;
    this.showProfileGroupForm = true;

  }

}
