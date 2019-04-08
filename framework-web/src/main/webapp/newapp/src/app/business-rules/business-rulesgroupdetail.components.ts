import { BaseEntity } from './../metadata/domain/domain.baseEntity';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { RuleGroup } from '../metadata/domain/domain.ruleGroup';
import { MetaType } from '../metadata/enums/metaType';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { AppHelper } from '../app.helper';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { RoutesParam } from '../metadata/domain/domain.routeParams';

@Component({
  selector: 'app-qualityGroup',
  templateUrl: './business-rulesgroupdetail.template.html',
  styleUrls: []
})

export class BusinessRulesGroupDetailComponent {

  rulegroupdata: any;
  //showGraph: boolean;
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
  datarulegroup: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
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
  metaType = MetaType;
  isGraphInprogess: boolean;
  isGraphError: boolean;
  caretdown = 'fa fa-caret-down';

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata,
    private _commonService: CommonService, private _location: Location, public appHelper: AppHelper) {

    //this.showGraph = false;
    this.rulegroupdata = new RuleGroup();
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
    this.breadcrumbDataFrom = [
      {
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
    ];

    this.isEditInprogess = false;
    // this.isEditError = false;
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
        this.isSubmitEnable = true;
        this.isEditInprogess = false;
        this.isEditError = false;
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
    this.router.navigate(['app/businessRules/rulegroup', uuid, version, 'false']);
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
    this.isHomeEnable = false;
    this.showDivGraph = false;
    this.showForm = true;
  }

  showGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showDivGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }
  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.rulegroupdata.name;
  }
  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/rulegroup']);
  }

  clear() {
    this.selectedItems = []
  }
  getAllLatest() {
    this._commonService.getAllLatest(this.metaType.RULE).subscribe(
      response => { this.OnSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response: BaseEntity[]) {
    let temp = []
    for (const n in response) {
      let allname = new MultiSelectIO();
      allname.id = response[n].uuid.toString();
      allname.itemName = response[n].name.toString();
      allname.uuid = response[n].uuid.toString();
      temp[n] = allname;
    }
    this.dropdownList = temp
  }
  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(id, version, this.metaType.RULEGROUP)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = false;
        });
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.datarulegroup = response;
    this.createdBy = response.createdBy.ref.name;
    this.published = this.appHelper.convertStringToBoolean(this.datarulegroup.published);
    this.active = this.appHelper.convertStringToBoolean(this.datarulegroup.active);
    this.locked = this.appHelper.convertStringToBoolean(this.datarulegroup.locked);

    const version: Version = new Version();
    this.uuid = response.uuid;
    version.label = this.datarulegroup.version;
    version.uuid = this.datarulegroup.uuid;
    this.selectedVersion = version

    let tmp = [];
    for (let i = 0; i < response.ruleInfo.length; i++) {
      let ruleinfo = new MultiSelectIO();
      ruleinfo.id = response.ruleInfo[i].ref.uuid;
      ruleinfo.itemName = response.ruleInfo[i].ref.name;
      ruleinfo.uuid = response.ruleInfo[i].ref.uuid;
      tmp[i] = ruleinfo;
    }
    this.selectedItems = tmp;
    this.isEditInprogess = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.RULEGROUP, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response) {
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
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  

  submitRuleGroup() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let rulegroupJson = new RuleGroup();
    rulegroupJson.uuid = this.datarulegroup.uuid;
    rulegroupJson.name = this.datarulegroup.name;
    rulegroupJson.desc = this.datarulegroup.desc;
    rulegroupJson.tags = this.datarulegroup.tags;
    rulegroupJson.active = this.appHelper.convertBooleanToString(this.active);
    rulegroupJson.published = this.appHelper.convertBooleanToString(this.published);
    rulegroupJson.locked = this.appHelper.convertBooleanToString(this.locked);

    let ruleInfo = [new MetaIdentifierHolder];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = new AttributeRefHolder();
      let ref = new MetaIdentifier();
      ref.uuid = this.selectedItems[i].uuid;
      ref.type = this.metaType.RULE;
      rules.ref = ref;
      ruleInfo[i] = rules;
    }
    rulegroupJson.ruleInfo = ruleInfo;
    rulegroupJson.inParallel = this.datarulegroup.inParallel;

    console.log(rulegroupJson);
    this._commonService.submit(this.metaType.RULEGROUP, rulegroupJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.RULEGROUP, response).subscribe(
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
    this._commonService.execute(response.uuid, response.version, this.metaType.RULEGROUP, "execute").subscribe(
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

  showProfileGroupePage() {
    this.showProfileGroup = true;
    this.showgraphdiv = false;
    this.graphDataStatus = false;
    this.showProfileGroupForm = true;
  }

  onItemSelect(item: any) {
    console.log(item);
    // console.log(this.selectedItems);
  }
  OnItemDeSelect(item: any) {
    console.log(item);
    // console.log(this.selectedItems);
  }
  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any) {
    console.log(items);
  }
}
