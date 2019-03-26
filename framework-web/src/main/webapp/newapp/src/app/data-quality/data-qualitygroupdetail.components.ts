
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { AppMetadata } from '../app.metadata';
import { CommonService } from './../metadata/services/common.service';
import { DataQualityService } from '../metadata/services/dataQuality.services';
import { Version } from './../metadata/domain/version'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component';
import * as MetaTypeEnum from '../metadata/enums/metaType';
import { AppHelper } from '../app.helper';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { DataQualityGroup } from '../metadata/domain/domain.dataQualityGroup';
import { BaseEntity } from '../metadata/domain/domain.baseEntity';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { RoutesParam } from '../metadata/domain/domain.routeParams';

@Component({
  selector: 'app-qualityGroup',
  templateUrl: './data-qualitygroupdetail.template.html',
  styleUrls: []
})
export class DataQualityGroupDetailComponent {
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
  dqgroupdata: any;
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
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  isGraphError: boolean;
  isGraphInprogess: boolean;

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, private _commonService: CommonService, private _location: Location,
    private _dataQualityService: DataQualityService, public appHelper: AppHelper) {
    this.dqgroupdata = new DataQualityGroup();
    this.isHomeEnable = false;
    this.dqgroupdata["active"] = true
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
        "caption": "Data Quality  ",
        "routeurl": "/app/list/dqgroup"
      },
      {
        "caption": "Data Quality Group ",
        "routeurl": "/app/list/dqgroup"
      },
      {
        "caption": "",
        "routeurl": null
      }
    ],

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
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
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
    this.router.navigate(['app/dataQuality/dqgroup', uuid, version, 'false']);
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
    this.breadcrumbDataFrom[2].caption = this.dqgroupdata.name;
  }
  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/dqgroup']);
  }

  clear() {
    this.selectedItems = []
  }

  getAllLatest() {
    this._commonService.getAllLatest(MetaTypeEnum.MetaType.DQ).subscribe(
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
    this._commonService.getOneByUuidAndVersion(id, version, MetaTypeEnum.MetaType.DQGROUP)
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
    this.dqgroupdata = response;
    this.active == this.appHelper.convertStringToBoolean(this.dqgroupdata.active);
    this.locked == this.appHelper.convertStringToBoolean(this.dqgroupdata.locked);
    this.published == this.appHelper.convertStringToBoolean(this.dqgroupdata.published);

    const version: Version = new Version();
    version.label = this.dqgroupdata.version;
    version.uuid = this.dqgroupdata.uuid;
    this.selectedVersion = version;

    let tmp = [];
    for (let i = 0; i < this.dqgroupdata.ruleInfo.length; i++) {
      let ruleinfo = new MultiSelectIO();
      ruleinfo.id = this.dqgroupdata.ruleInfo[i].ref.uuid;
      ruleinfo.itemName = this.dqgroupdata.ruleInfo[i].ref.name;
      ruleinfo.uuid = this.dqgroupdata.ruleInfo[i].ref.uuid;
      tmp[i] = ruleinfo;
    }
    this.selectedItems = tmp;
    this.isEditInprogess = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(MetaTypeEnum.MetaType.DQGROUP, this.id)
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, MetaTypeEnum.MetaType.DQGROUP)
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  submitDqGroup() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let dqgroupJson = new DataQualityGroup();
    dqgroupJson.uuid = this.dqgroupdata.uuid;
    dqgroupJson.name = this.dqgroupdata.name;
    dqgroupJson.desc = this.dqgroupdata.desc;
    dqgroupJson.tags = this.dqgroupdata.tags;

    dqgroupJson.active = this.appHelper.convertBooleanToString(this.active);
    dqgroupJson.locked = this.appHelper.convertBooleanToString(this.locked);
    dqgroupJson.published = this.appHelper.convertBooleanToString(this.published);

    let ruleInfo = [new MetaIdentifierHolder];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let rules = new AttributeRefHolder();
      let ref = new MetaIdentifier();
      ref.uuid = this.selectedItems[i].uuid;
      ref.type = MetaTypeEnum.MetaType.DQ;
      rules.ref = ref;
      ruleInfo[i] = rules;
    }
    dqgroupJson.ruleInfo = ruleInfo;

    dqgroupJson.inParallel = this.dqgroupdata.inParallel
    console.log(dqgroupJson);
    this._commonService.submit(MetaTypeEnum.MetaType.DQGROUP, dqgroupJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(MetaTypeEnum.MetaType.DQGROUP, response).subscribe(
        response => {
          this.onSucessGetOneById(response);
          // this.goBack()
        },
        error => console.log('Error :: ' + error)
      )
    } //End if
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'DQ Group Save Successfully' });
      setTimeout(() => {
        this.goBack()
      }, 1000);
    }
  }
  onSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, MetaTypeEnum.MetaType.DQGROUP, "execute").subscribe(
      response => {
        this.IsProgerssShow = "false";
        this.showMassage('DQ Group Save and Submit Successfully', 'success', 'Success Message')
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


}
