import { BaseEntity } from './../metadata/domain/domain.baseEntity';
import { MetaType } from './../metadata/enums/metaType';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { AppMetadata } from '../app.metadata';
import { CommonService } from './../metadata/services/common.service';
import { Version } from './../metadata/domain/version'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { AppHelper } from '../app.helper';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { ProfileGroup } from '../metadata/domain/domain.profileGroup';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';

@Component({
  selector: 'app-profile',
  templateUrl: './data-profilegroupdetail.template.html',
  styleUrls: []
})

export class DataProfileGroupDetailComponent {
  showProfileGroupForm: boolean;
  graphDataStatus: boolean;
  showgraphdiv: boolean;
  showProfileGroup: boolean;
  isHomeEnable: boolean;
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
  profilegroupdata: any;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isGraphError: boolean;
  isGraphInprogess: boolean;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  isEdit: boolean = false;;
  isversionEnable: boolean;
  isAdd: boolean;
  active: boolean;
  locked: boolean;
  published: boolean;
  showDivGraph: boolean;
  showForm: boolean = true;
  metaType = MetaType;
  caretdown = 'fa fa-caret-down';

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata,
    private _commonService: CommonService, private _location: Location, public appHelper: AppHelper) {
    this.profilegroupdata = {};
    this.profilegroupdata.active = true
    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.isHomeEnable = false;
    // this.showGraph = false;
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
        "caption": "Data Profiling ",
        "routeurl": "/app/list/profilegroup"
      },
      {
        "caption": "Rule Group ",
        "routeurl": "/app/list/profilegroup"
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
    //this.showProfileGroupePage()
    this.router.navigate(['app/dataProfiling/profilegroup', uuid, version, 'false']);
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
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.profilegroupdata.name;
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.metaType.PROFILE).subscribe(
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
    this._commonService.getOneByUuidAndVersion(id, version, this.metaType.PROFILEGROUP)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error)
          this.isEditError = true;
        });
  }
  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.profilegroupdata = response;
    this.uuid = this.profilegroupdata.uuid;

    this.published = this.appHelper.convertStringToBoolean(this.profilegroupdata.published);
    this.active = this.appHelper.convertStringToBoolean(this.profilegroupdata.active);
    this.locked = this.appHelper.convertStringToBoolean(this.profilegroupdata.locked);

    const version: Version = new Version();
    version.label = this.profilegroupdata.version;
    version.uuid = this.profilegroupdata.uuid;
    this.selectedVersion = version;

    let tmp = [];
    for (let i = 0; i < this.profilegroupdata.ruleInfo.length; i++) {
      let ruleinfo = new MultiSelectIO();
      ruleinfo.id = this.profilegroupdata.ruleInfo[i].ref.uuid;
      ruleinfo.itemName = this.profilegroupdata.ruleInfo[i].ref.name;
      ruleinfo.uuid = this.profilegroupdata.ruleInfo[i].ref.uuid;
      tmp[i] = ruleinfo;
    }
    this.selectedItems = tmp;
    this.isEditInprogess = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.PROFILEGROUP, this.id)
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
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }
  public goBack() {
    this.router.navigate(['app/list/profilegroup']);
  }

  submitProfileGroup() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let profilegroupJson = new ProfileGroup;
    profilegroupJson.uuid = this.profilegroupdata.uuid
    profilegroupJson.name = this.profilegroupdata.name
    profilegroupJson.desc = this.profilegroupdata.desc
    profilegroupJson.tags = this.profilegroupdata.tags;
    profilegroupJson.active = this.appHelper.convertBooleanToString(this.profilegroupdata.active);
    profilegroupJson.published = this.appHelper.convertBooleanToString(this.profilegroupdata.published);
    profilegroupJson.locked = this.appHelper.convertBooleanToString(this.profilegroupdata.locked);

    let ruleInfo = [new MetaIdentifierHolder];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = new AttributeRefHolder();
      let ref = new MetaIdentifier();
      ref.uuid = this.selectedItems[i].uuid;
      ref.type = this.metaType.PROFILE;
      rules.ref = ref;
      ruleInfo[i] = rules;
    }
    profilegroupJson.ruleInfo = ruleInfo;
    profilegroupJson.inParallel = this.profilegroupdata.inParallel
    console.log(profilegroupJson);

    this._commonService.submit(this.metaType.PROFILEGROUP, profilegroupJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )

  }
  onSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.PROFILEGROUP, response).subscribe(
        response => {
          this.onSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    }
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Profile Group Save Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }
  onSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, this.metaType.PROFILEGROUP, "execute").subscribe(
      response => {
        this.showMassage('Profile Group Save and Submit Successfully', 'success', 'Success Message')
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
