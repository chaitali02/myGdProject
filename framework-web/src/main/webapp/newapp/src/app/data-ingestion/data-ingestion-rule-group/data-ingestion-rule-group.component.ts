import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Version } from '../../shared/version';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { DataIngestionService } from '../../metadata/services/dataIngestion.service';
import { Location } from '@angular/common';
import { MetaType } from '../../metadata/enums/metaType';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { BaseEntity } from '../../metadata/domain/domain.baseEntity';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';
import { IngestGroup } from '../../metadata/domain/domain.ingestGroup';
import { AppHelper } from '../../app.helper';
import { AttributeRefHolder } from '../../metadata/domain/domain.attributeRefHolder';
import { MetaIdentifier } from '../../metadata/domain/domain.metaIdentifier';
import { MultiSelectIO } from '../../metadata/domainIO/domain.multiselectIO';
import { RoutesParam } from '../../metadata/domain/domain.routeParams';
@Component({
  selector: 'app-data-ingestion-rule-group',
  templateUrl: './data-ingestion-rule-group.component.html'
})
export class DataIngestionRuleGroupComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  isSubmit: any;
  msgs: any[];
  IsProgerssShow: string;
  checkboxModelexecution: boolean;
  runInParallel: boolean;
  ruleInfoArray: any;
  dropdownSettingsRuleInfo: {
    singleSelection: boolean;
    selectAllText: string;
    unSelectAllText: string;
    enableSearchFilter: boolean;
    disabled: boolean;
  };
  ruleInfo: Array<MultiSelectIO>;
  tags: any[];
  createdBy: any;
  ingestGroupData: any;
  mode: any;
  version: any;
  id: any;
  uuid: any;
  selectedVersion: Version;
  VersionList: Array<DropDownIO>;
  breadcrumbDataFrom: { "caption": any; "routeurl": any; }[];
  metaType = MetaType;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  published: any;
  active: any;
  locked: any;

  showForm: boolean;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean = false;
  isAdd: boolean = false;
  isGraphInprogess: boolean;
  isGraphError: boolean;

  constructor(private appHelper: AppHelper, private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataInjectService: DataIngestionService) {
    this.IsProgerssShow = "false";
    this.isSubmit = "false"
    this.ingestGroupData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.ruleInfoArray = [];

    this.showGraph = false;
    this.showForm = true;
    this.isHomeEnable = false;

    this.breadcrumbDataFrom = [{
      "caption": "Data Ingestion",
      "routeurl": "/app/list/ingestgroup"
    },
    {
      "caption": "Rule Group",
      "routeurl": "/app/list/ingestgroup"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];
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
      }
      else {
        this.getAllVersionByUuid();
        this.getAllLatest();
        this.isEditInprogess = false;
        this.isEditError = false;
        this.ingestGroupData = new IngestGroup();
      }
      this.setMode(this.mode);
    });
  }

  setMode(mode: any) {
    if (mode == 'true') {
      this.isEdit = false;
      this.isversionEnable = false;
      this.isAdd = false;

      this.dropdownSettingsRuleInfo = {
        singleSelection: false,
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        enableSearchFilter: true,
        disabled: true
      }
    } else if (mode == 'false') {
      this.isEdit = true;
      this.isversionEnable = true;
      this.isAdd = false;

      this.dropdownSettingsRuleInfo = {
        singleSelection: false,
        selectAllText: 'Select All',
        unSelectAllText: 'UnSelect All',
        enableSearchFilter: true,
        disabled: false
      }
    } else {
      this.isAdd = true;
      this.isEdit = false;
    }
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

  getAllLatest() {
    this._commonService.getAllLatest(this.metaType.INGEST)
      .subscribe(
        response => {
          this.OnSuccesgetgetAllLatest(response)
        },
        error => console.log("Error :: " + error));
  }

  OnSuccesgetgetAllLatest(response: BaseEntity[]) {
    for (const i in response) {
      let obj = new MultiSelectIO();
      obj.itemName = response[i].name;
      obj.id = response[i].uuid;
      this.ruleInfoArray[i] = obj;
    }
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.INGESTGROUP, this.id)
      .subscribe(
        response => { this.OnSuccesgetAllVersionByUuid(response) },
        error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response:BaseEntity[]) {
    var VersionList = [new DropDownIO]
    for (const i in response) {
      let verObj = new DropDownIO();
      verObj.label= response[i].version;
      verObj.value = {label: "", uuid: ""}
      verObj.value.label= response[i].version;
      verObj.value.uuid = response[i].uuid;
      VersionList[i] = verObj;
    }
    this.VersionList = VersionList
  }
  
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.ingestGroupData.name;
  }

  getOneByUuidAndVersion(id: any, version: any) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(id, version, this.metaType.INGESTGROUP).subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => {
        console.log("Error::", +error)
        this.isEditError = false;
      }
    )
  }

  onSuccessgetOneByUuidAndVersion(response: IngestGroup) {
    this.ingestGroupData = response;
    this.breadcrumbDataFrom[2].caption = response.name;
    this.createdBy = response.createdBy.ref.name;
    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version;

    if (response.tags != null) {
      this.tags = response.tags;
    }

    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.active = this.appHelper.convertStringToBoolean(response.active);
    this.locked = this.appHelper.convertStringToBoolean(response.locked);

    let ruleInfo = [new MultiSelectIO];
    for (const i in response.ruleInfo) {
      {
        let ruleInfotag = new MultiSelectIO();
        ruleInfotag.id = response.ruleInfo[i].ref.uuid;
        ruleInfotag.itemName = response.ruleInfo[i].ref.name;
        ruleInfo[i] = ruleInfotag;
      }
    }
    this.ruleInfo = ruleInfo;
    console.log(JSON.stringify(this.ruleInfo));
    this.runInParallel = this.appHelper.convertStringToBoolean(response.inParallel);
    this.isEditInprogess = false;
  }

  submitIngestGroup() {
    this.isSubmit = "true";

    this.IsProgerssShow = "true";
    let ingestGroupJson = new IngestGroup();
    ingestGroupJson.uuid = this.ingestGroupData.uuid;
    ingestGroupJson.name = this.ingestGroupData.name;
    ingestGroupJson.desc = this.ingestGroupData.desc;

    ingestGroupJson.tags = this.tags;
    ingestGroupJson.active = this.active == true ? 'Y' : "N"
    ingestGroupJson.published = this.published == true ? 'Y' : "N"
    ingestGroupJson.locked = this.locked == true ? 'Y' : "N"

    let ruleInfo = [];
    for (const i in this.ruleInfo) {
      let ruleInfoObj = new AttributeRefHolder();
      let ref = new MetaIdentifier();
      ref.uuid = this.ruleInfo[i].id
      ref.type = this.metaType.INGEST;
      ruleInfoObj.ref = ref;
      ruleInfo[i] = ruleInfoObj
    }
    ingestGroupJson.ruleInfo = ruleInfo;
    ingestGroupJson.inParallel = this.runInParallel == true ? 'Y' : "N"

    console.log(JSON.stringify(ingestGroupJson));
    this._commonService.submit(this.metaType.INGESTGROUP, ingestGroupJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error))
  }

  OnSuccessubmit(response: any) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.INGESTGROUP, response).subscribe(
        response => { this.onSuccessgetOneById(response) },
        error => console.log('Error :: ' + error))
    }
    else {
      this.msgs = [];
     // this.isSubmit = "false"
      this.IsProgerssShow = "false";
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Ingest Rule Group Saved Successfully' });
      setTimeout(() => { this.goBack() }, 1000);
    }
  }

  onSuccessgetOneById(response: any) {
    this._commonService.execute(response.uuid, response.version, this.metaType.INGESTGROUP, "execute").subscribe(
      response => { this.onSuccessExecute(response) },
      error => this.showError(error))
  }

  onSuccessExecute(response: any) {
    this.IsProgerssShow = "false";
    this.msgs = [];
    this.isSubmit = "false"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Group Saved and Submited Successfully' });
    setTimeout(() => { this.goBack() }, 1000);
  }

  showError(error: any) {
    this.IsProgerssShow = "false";
    console.log('Error::', + error);
    this.msgs = [];
    this.msgs.push({ severity: 'Failed', summary: 'Failed Message', detail: 'Rule Group Saved and failed' });
    setTimeout(() => { this.goBack() }, 1000);
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/ingestgroup'])
  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataIngestion/ingestgroup', this.ingestGroupData.uuid, this.ingestGroupData.version, 'false']);
    this.dropdownSettingsRuleInfo = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: true
    };
  }

  clear() {
    this.ruleInfo = []
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
    this.showForm = true;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    this.showForm = false;
    this.isGraphInprogess = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }
}
