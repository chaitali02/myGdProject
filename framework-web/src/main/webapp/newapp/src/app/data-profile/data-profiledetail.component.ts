import { BaseEntity } from './../metadata/domain/domain.baseEntity';
import { Profile } from './../metadata/domain/domain.profile';

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Location } from '@angular/common';
import { Router, Event as RouterEvent, ActivatedRoute, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';

import { AppMetadata } from '../app.metadata';

import { CommonService } from './../metadata/services/common.service';

import { Version } from './../metadata/domain/version'
import { DependsOn } from './dependsOn'
import { KnowledgeGraphComponent } from '../shared/components/knowledgeGraph/knowledgeGraph.component'
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FilterInfoIO } from '../metadata/domainIO/domain.filterInfoIO';
import { MetaType } from '../metadata/enums/metaType';
import { AttributeIO } from '../metadata/domainIO/domain.attributeIO';
import { ProfileService } from '../metadata/services/profile.service';
import { AppHelper } from '../app.helper';
import { DependsOnIO } from '../metadata/domainIO/domain.dependsOnIO';
import { MultiSelectIO } from '../metadata/domainIO/domain.multiselectIO';
import { RoutesParam } from '../metadata/domain/domain.routeParams';
import { DropDownIO } from '../metadata/domainIO/domain.dropDownIO';
import { MetaIdentifierHolder } from '../metadata/domain/domain.metaIdentifierHolder';
import { MetaIdentifier } from '../metadata/domain/domain.metaIdentifier';
import { AttributeRefHolder } from '../metadata/domain/domain.attributeRefHolder';
import { FilterInfo } from '../metadata/domain/domain.filterInfo';
import { SourceAttr } from '../metadata/domain/domain.sourceAttr';


@Component({
  selector: 'app-profile',
  templateUrl: './data-profiledetail.template.html',
  styleUrls: []
})
export class DataProfileDetailComponent {
  dropIndex: any;
  dragIndex: any;
  iSSubmitEnable: boolean;
  filterTableArray: any;
  isNullArray: { 'value': string; 'label': string; }[];
  datatype: { 'value': string; 'label': string; }[];
  rhsTypeArray: { 'value': string; 'label': string; }[];
  lhsTypeArray: { 'value': string; 'label': string; }[];
  logicalOperators: { 'value': string; 'label': string; }[];
  operators: { 'value': string; 'label': string; }[];
  dialogAttributeName: any;
  displayDialogBox: boolean;
  dialogSelectName: any;
  dialogAttriArray: any[];
  dialogAttriNameArray: any[];
  IsSelectSoureceAttr: boolean;
  rhsFormulaArray: any[];
  paramlistArray: any[];
  functionArray: any[];
  attributesArray: any[];
  lhsFormulaArray: any[];
  selectedAllFitlerRow: boolean;
  progressbarWidth: string;
  continueCount: number;
  isHomeEnable: boolean;
  checkboxModelexecution: boolean;
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  IsProgerssShow: string;
  isSubmitEnable: any;
  msgs: any[];
  dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number, disabled: any };
  selectedItems: any
  dropdownList: any = [];
  source: any;
  selectedVersion: Version;
  versionList: Array<DropDownIO>;
  allNames: SelectItem[] = [];
  createdBy: any;
  mode: any;
  version: any;
  uuid: any;
  id: any;
  routerUrl: any;
  sources: any;
  sourcedata: DependsOn;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  showForm: boolean = true;
  isGraphInprogess: boolean;
  showDivGraph: boolean;
  isGraphError: boolean;
  isversionEnable: boolean;
  isAdd: boolean;
  isEdit: boolean = false;
  profiledata: any;
  isEditError: boolean = false;
  isEditInprogess: boolean = false;
  metaType = MetaType;
  moveTo: number;
  moveToEnable: boolean;
  count: any[];
  invalideMinRow: boolean = false;
  invalideMaxRow: boolean = false;
  txtQueryChangedFilter: Subject<string> = new Subject<string>();
  resetTableTopBottom: Subject<string> = new Subject<string>();
  rowIndex: any;
  topDisabled: boolean;
  bottomDisabled: boolean;
  active: any;
  locked: any;
  published: any;
  datasetNotEmpty: boolean = true;
  caretdown = 'fa fa-caret-down';

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata,
    private _commonService: CommonService, private _profileService: ProfileService, private _location: Location, public appHelper: AppHelper) {
    this.profiledata = {};
    this.active = true;
    this.locked = false;
    this.published = false;
    this.isEditInprogess = false;
    this.isEditError = false;

    this.isSubmitEnable = true;
    this.IsProgerssShow = "false";
    this.sources = ["datapod"];
    this.source = this.sources[0];
    this.selectedItems = [];
    this.continueCount = 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
    this.dropdownList = [];
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
      "caption": "Data Profiling",
      "routeurl": "/app/list/profile"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/profile"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.displayDialogBox = false;
    this.dialogAttributeName = {};
    this.profiledata.filterTableArray = [FilterInfoIO];

    this.operators = [
      { 'value': '=', 'label': 'EQUAL TO(=)' },
      { 'value': '!=', 'label': 'NOT EQUAL(!=)' },
      { 'value': '<', 'label': 'LESS THAN(<)' },
      { 'value': '>', 'label': 'GREATER THAN(>)' },
      { 'value': '<=', 'label': 'LESS OR  EQUAL(<=)' },
      { 'value': '>=', 'label': 'GREATER OR EQUAL(>=)' },
      { 'value': 'BETWEEN', 'label': 'BETWEEN' },
      { 'value': 'LIKE', 'label': 'LIKE' },
      { 'value': 'NOT LIKE', 'label': 'NOT LIKE' },
      { 'value': 'RLIKE', 'label': 'RLIKE' },
      { 'value': 'EXISTS', 'label': 'EXISTS' },
      { 'value': 'NOT EXISTS', 'label': 'NOT EXISTS' },
      { 'value': 'IN', 'label': 'IN' },
      { 'value': 'NOT IN', 'label': 'NOT IN' },
      { 'value': 'IS', 'label': 'IS' },
    ];
    this.logicalOperators = [
      { 'value': '', 'label': '' },
      { 'value': 'AND', 'label': 'AND' },
      { 'value': 'OR', 'label': 'OR' }
    ];
    this.lhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' }
    ];
    this.rhsTypeArray = [
      { 'value': 'string', 'label': 'string' },
      { 'value': 'integer', 'label': 'integer' },
      { 'value': 'datapod', 'label': 'attribute' },
      { 'value': 'formula', 'label': 'formula' },
      { 'value': 'dataset', 'label': 'dataset' },
      { 'value': 'paramlist', 'label': 'paramlist' },
      { 'value': 'function', 'label': 'function' }
    ];
    this.datatype = [
      { 'value': '', 'label': '' },
      { 'value': 'String', 'label': 'String' },
      { 'value': 'Int', 'label': 'Int' },
      { 'value': 'Float', 'label': 'Float' },
      { 'value': 'Double', 'label': 'Double' },
      { 'value': 'Date', 'label': 'Date' }
    ];
    this.isNullArray = [
      { 'value': 'NULL', 'label': 'NULL' },
      { 'value': 'NOT NULL', 'label': 'NOT NULL' }
    ]
    this.profiledata.filterTableArray = null;

    this.moveToEnable = false;
    this.count = [];
    this.txtQueryChangedFilter
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        console.log(parseInt(index) - 1);
        for (const i in this.profiledata.filterTableArray) {
          if (this.profiledata.filterTableArray[i].hasOwnProperty("selected"))
            this.profiledata.filterTableArray[i].selected = false;
        }
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.resetTableTopBottom
      .pipe(debounceTime(3000), distinctUntilChanged())
      .subscribe(index => {
        this.moveTo = null;
        this.checkSelected(false, null);
        this.invalideMinRow = false;
        this.invalideMaxRow = false;
      });

    this.invalideMinRow = false;
    this.invalideMaxRow = false;
    this.topDisabled = false;
    this.bottomDisabled = false;
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
    this.router.navigate(['app/dataProfiling/profile', uuid, version, 'false']);
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
    this.isEdit = true;
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataProfiling/profile', uuid, version, 'true']);
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
  showMainPage() {
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
      this.d_KnowledgeGraphComponent.getGraphData(uuid, version);
      this.isGraphInprogess = this.d_KnowledgeGraphComponent.isInprogess;
      this.isGraphError = this.d_KnowledgeGraphComponent.isError;
    }, 1000);
  }

  onChangeName() {
    this.breadcrumbDataFrom[2].caption = this.profiledata.name;
  }

  public goBack() {
    this.router.navigate(['app/list/profile']);
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.onSuccesgetAllLatest(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllLatest(response1: BaseEntity[]) {
    let temp = []
    if (this.mode == undefined) {
      let dependOnTemp: DependsOn = new DependsOn();
      response1.sort((a, b) => a.name.localeCompare(b.name.toString()));
      dependOnTemp.label = response1[0].name;
      dependOnTemp.uuid = response1[0].uuid;
      this.sourcedata = dependOnTemp
      this.getAllAttributeBySource("multiSelect");
    }
    for (const n in response1) {
      let allname = new DropDownIO();
      allname.label = response1[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response1[n].name;
      allname.value.uuid = response1[n].uuid;
      temp[n] = allname;
    }
    this.allNames = temp
  }
  countContinue = function () {
    this.continueCount = this.continueCount + 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 33.33 * this.continueCount + "%";
  }
  changeType() {
    this.selectedItems = [];
    this.getAllAttributeBySource("multiSelect");
    this.profiledata.filterTableArray = [];
  }

  getOneByUuidAndVersion(id, version) {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._profileService.getOneByUuidAndVersion(id, version, this.metaType.PROFILE)
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
    this.breadcrumbDataFrom[2].caption = response.profile.name;
    this.profiledata = response.profile;

    const version: Version = new Version();
    version.label = this.profiledata.version;
    version.uuid = this.profiledata.uuid;
    this.selectedVersion = version;

    this.active == this.appHelper.convertStringToBoolean(this.profiledata.active);
    this.locked == this.appHelper.convertStringToBoolean(this.profiledata.locked);
    this.published == this.appHelper.convertStringToBoolean(this.profiledata.published);

    let dependOnTemp: DependsOnIO = new DependsOnIO();
    dependOnTemp.label = this.profiledata.dependsOn.ref.name;
    dependOnTemp.uuid = this.profiledata.dependsOn.ref.uuid;
    this.sourcedata = dependOnTemp;

    this.getAllAttributeBySource("multiSelect");
    let tmp = [];
    for (let i = 0; i < this.profiledata.attributeInfo.length; i++) {
      let attributeinfo = new MultiSelectIO();
      attributeinfo.id = this.profiledata.attributeInfo[i].ref.uuid + "_" + this.profiledata.attributeInfo[i].attrId;
      attributeinfo.itemName = this.profiledata.attributeInfo[i].ref.name + "." + this.profiledata.attributeInfo[i].attrName;
      attributeinfo.uuid = this.profiledata.attributeInfo[i].ref.uuid
      attributeinfo.attributeId = this.profiledata.attributeInfo[i].attrId;
      tmp[i] = attributeinfo;
    }
    this.selectedItems = tmp;

    if (response.isFormulaExits == true) {
      this.getFormulaByType("lhsType");
    }
    else if (response.isFormulaExits == true) {
      this.getFormulaByType("rhsType");
    }
    else if (response.isAttributeExits == true) {
      this.getAllAttributeBySource("singleSelect");
    }
    else if (response.isSimpleExits == true) {
    }
    if (response.isParamlistExits == true) {
      this.getParamByApp();
    }
    if (response.isFunctionExits == true) {

      this.getFunctionByCriteria();
    }

    this.profiledata.filterTableArray = response.filterInfoIo;
    this.isEditInprogess = false;
  }

  getFormulaByType(type: String) {
    this._commonService.getFormulaByType(this.sourcedata.uuid, this.source)
      .subscribe(response => {
        if (type == "rhsType") {
          this.onSuccessgetFormulaByLhsType(response);
        }
        else if (type == "lhsType") {
          this.onSuccessgetFormulaByRhsType(response);
        }
      },
        error => console.log("Error ::", error))
  }
  onSuccessgetFormulaByLhsType(response) {
    this.lhsFormulaArray = []
    for (const i in response) {
      let formulaObj = new DropDownIO();
      formulaObj.label = response[i].name;
      formulaObj.value = { label: "", uuid: "" };
      formulaObj.value.uuid = response[i].uuid;
      formulaObj.value.label = response[i].name;
      this.lhsFormulaArray[i] = formulaObj;
    }
  }
  onSuccessgetFormulaByRhsType(response) {
    this.rhsFormulaArray = [new DropDownIO];
    let rhsFormulaObj = new DropDownIO();
    let temp = [];
    for (const i in response) {
      rhsFormulaObj.label = response[i].name;
      rhsFormulaObj.value = { label: "", uuid: "" };
      rhsFormulaObj.value.label = response[i].name;
      rhsFormulaObj.value.uuid = response[i].uuid;
      temp[i] = rhsFormulaObj;
    }
    this.rhsFormulaArray = temp
  }

  getAllAttributeBySource(dropDownType: String) {
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid, this.source).subscribe(
      response => {
        if (dropDownType == "multiSelect") {
          this.onSuccesgetAllAttributeBySourceForMultiselect(response);
        }
        else if (dropDownType == "singleSelect") {
          this.onSuccessgetAllAttributeBySourceForSingleSelect(response);
        }
      },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccesgetAllAttributeBySourceForMultiselect(response) {
    let temp = []
    for (const n in response) {
      let allname = new MultiSelectIO();
      allname.id = response[n].id;
      allname.itemName = response[n].dname;
      allname.uuid = response[n].uuid;
      allname.attributeId = response[n].attributeId;
      temp[n] = allname
    }
    this.dropdownList = temp
  }
  onSuccessgetAllAttributeBySourceForSingleSelect(response) {
    this.attributesArray = []
    let temp1 = [];
    for (const i in response) {
      let attributeObj = new AttributeIO();
      attributeObj.label = response[i].dname;
      attributeObj.value = {};
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].dname;
      attributeObj.value.attributeId = response[i].attributeId;
      temp1[i] = attributeObj
      this.attributesArray = temp1;
    }
  }

  getParamByApp() {
    this._commonService.getParamByApp("", this.metaType.APPLICATION)
      .subscribe(response => { this.onSuccessgetParamByApp(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetParamByApp(response) {
    let tempParamlistArray = [new AttributeIO];
    for (const i in response) {
      let attributeObj = new AttributeIO();
      attributeObj.label = "app." + response[i].paramName;
      attributeObj.value = { label: "", uuid: "", attributeId: "" };
      attributeObj.value.uuid = response[i].ref.uuid;
      attributeObj.value.attributeId = response[i].paramId;
      attributeObj.value.label = "app." + response[i].paramName;
      tempParamlistArray[i] = attributeObj
    }
    this.paramlistArray = tempParamlistArray;
  }

  getFunctionByCriteria() {
    this._commonService.getFunctionByCriteria("", "N", this.metaType.FUNCTION)
      .subscribe(response => { this.onSuccessgetFunctionByCriteria(response) },
        error => console.log("Error ::", error))
  }
  onSuccessgetFunctionByCriteria(response) {
    let functionArray = [new DropDownIO];
    for (const i in response) {
      let attributeObj = new DropDownIO();
      attributeObj.label = response[i].name;
      attributeObj.value = { label: "", uuid: "" };
      attributeObj.value.uuid = response[i].uuid;
      attributeObj.value.label = response[i].name;
      functionArray[i] = attributeObj
    }
    this.functionArray = functionArray;
  }

  searchOption(data, index) {
    this.rowIndex = index;
    this.displayDialogBox = true;
    if (!data.uuid) {
      this.dialogSelectName = "";
      this.dialogAttributeName = "";
    }
    this._commonService.getAllLatest(MetaType.DATASET)
      .subscribe(response => { this.onSuccessgetAllLatestDialogBox(response, data) },
        error => console.log("Error ::", error));    
  }
  onSuccessgetAllLatestDialogBox(response, data) {
    this.dialogAttriArray = [];
    let temp = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].name;
      dialogAttriNameObj.value = { label: "", attributeId: "", uuid: "" };
      dialogAttriNameObj.value.label = response[i].name;
      dialogAttriNameObj.value.attributeId = response[i].uuid;
      dialogAttriNameObj.value.uuid = response[i].uuid;
      temp[i] = dialogAttriNameObj;

      if (data.uuid != null) {
        if (data.uuid.toString() == temp[i].value.uuid) {
          this.dialogSelectName = temp[i].value;
          this.dialogAttributeName = dialogAttriNameObj.value;
          this.onChangeDialogAttribute(data);
        }
      }
    }
    this.dialogAttriArray = temp;
  }

  onChangeDialogAttribute(dialogSelectName) {
    let datasetuuid;
    if (dialogSelectName.uuid) {
      datasetuuid = dialogSelectName.uuid;
    }
    else {
      datasetuuid = this.dialogSelectName.uuid;
    }
    this._commonService.getAttributesByDataset(this.metaType.DATASET, datasetuuid)
      .subscribe(response => { this.onSuccessgetAttributesByDatasetDialogBox(response) },
        error => console.log("Error ::", error))
  }

  onSuccessgetAttributesByDatasetDialogBox(response) {
    this.dialogAttriNameArray = [];
    for (const i in response) {
      let dialogAttriNameObj = new AttributeIO();
      dialogAttriNameObj.label = response[i].attrName;
      dialogAttriNameObj.value = { label: "", attributeId: "", uuid: "" };
      dialogAttriNameObj.value.label = response[i].attrName;
      dialogAttriNameObj.value.attributeId = response[i].attrId;
      dialogAttriNameObj.value.uuid = response[i].ref.uuid;
      this.dialogAttriNameArray[i] = dialogAttriNameObj;
    }
  }

  submitDialogBox(index) {
    this.displayDialogBox = false;
    let rhsattribute = new AttributeIO();
    rhsattribute.label = this.dialogAttributeName.label;
    rhsattribute.uuid = this.dialogAttributeName.uuid;
    rhsattribute.attributeId = this.dialogAttributeName.attributeId;
    this.profiledata.filterTableArray[index].rhsAttribute = rhsattribute;
    this.datasetNotEmpty = true;
  }

  cancelDialogBox() {
    this.displayDialogBox = false;
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.PROFILE, this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesgetAllVersionByUuid(response: BaseEntity[]) {
    for (const i in response) {
      this.versionList = [new DropDownIO];
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      this.versionList[i] = ver;
    }
  }
  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label)
  }

  onChangeLhsType(index: any) {
    this.profiledata.filterTableArray[index].lhsAttribute = null;

    if (this.profiledata.filterTableArray[index].lhsType == this.metaType.FORMULA) {
      this.getFormulaByType("lhsType");
    }
    else if (this.profiledata.filterTableArray[index].lhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySource("singleSelect");
    }
    else {
      this.profiledata.filterTableArray[index].lhsAttribute = null;
    }
  }

  onChangeRhsType(index) {
    this.profiledata.filterTableArray[index].rhsAttribute = null;

    if (this.profiledata.filterTableArray[index].rhsType == this.metaType.FORMULA) {
      this.getFormulaByType("lhsType");
    }
    else if (this.profiledata.filterTableArray[index].rhsType == this.metaType.DATAPOD) {
      this.getAllAttributeBySource("singleSelect");
    }
    else if (this.profiledata.filterTableArray[index].rhsType == this.metaType.FUNCTION) {
      this.getFunctionByCriteria();
    }
    else if (this.profiledata.filterTableArray[index].rhsType == this.metaType.PARAMLIST) {
      this.getParamByApp();
    }
    else if (this.profiledata.filterTableArray[index].rhsType == this.metaType.DATASET) {
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.profiledata.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else {
      this.profiledata.filterTableArray[index].rhsAttribute = null;
    }
  }

  onChangeOperator(index) {
    this.profiledata.filterTableArray[index].rhsAttribute = null;
    if (this.profiledata.filterTableArray[index].operator == 'EXISTS' || this.profiledata.filterTableArray[index].operator == 'NOT EXISTS') {
      this.profiledata.filterTableArray[index].rhsType = this.metaType.DATASET;
      this.datasetNotEmpty = false;
      let rhsAttribute = new AttributeIO();
      rhsAttribute.label = "";
      rhsAttribute.uuid = "";
      rhsAttribute.attributeId = "";
      this.profiledata.filterTableArray[index].rhsAttribute = rhsAttribute
    }
    else if (this.profiledata.filterTableArray[index].operator == 'IS') {
      this.profiledata.filterTableArray[index].rhsType = 'string';
    }
    else {
      this.profiledata.filterTableArray[index].rhsType = 'integer';
    }
  }
  addRow() {
    var filertable = new FilterInfoIO;

    if (this.profiledata.filterTableArray == null || this.profiledata.filterTableArray.length == 0) {
      this.profiledata.filterTableArray = [];
      filertable.logicalOperator = '';
    }
    else {
      filertable.logicalOperator = this.logicalOperators[1].label;
    }
    filertable.lhsType = "string";
    filertable.lhsAttribute = null;
    filertable.operator = this.operators[0].value;
    filertable.rhsType = "string"
    filertable.rhsAttribute = null
    this.profiledata.filterTableArray.splice(this.profiledata.filterTableArray.length, 0, filertable);
    this.count = [];
    this.checkSelected(false, this.profiledata.filterTableArray.length - 1);
  }
  removeRow() {
    let newDataList = [];
    this.selectedAllFitlerRow = false;
    this.profiledata.filterTableArray.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    if (newDataList.length > 0) {
      newDataList[0].logicalOperator = "";
    }
    this.count = [];
    this.checkSelected(false, null);
    this.profiledata.filterTableArray = newDataList;
  }
  checkAllFilterRow() {
    if (!this.selectedAllFitlerRow) {
      this.selectedAllFitlerRow = true;
    }
    else {
      this.selectedAllFitlerRow = false;
    }
    this.checkSelected(false, null);
    this.profiledata.filterTableArray.forEach(filter => {
      filter.selected = this.selectedAllFitlerRow;
    });
  }
  submitProfile() {
    this.isSubmitEnable = true;
    this.IsProgerssShow = "true";
    let profileJson = new Profile();
    profileJson.uuid = this.profiledata.uuid;
    profileJson.name = this.profiledata.name;
    profileJson.desc = this.profiledata.desc;
    profileJson.tags = this.profiledata.tags;

    let dependsOn = new MetaIdentifierHolder();
    let ref = new MetaIdentifier();
    ref.type = this.source
    ref.uuid = this.sourcedata.uuid
    dependsOn.ref = ref;
    profileJson.dependsOn = dependsOn;

    profileJson.active = this.appHelper.convertBooleanToString(this.active);
    profileJson.published = this.appHelper.convertBooleanToString(this.published);
    profileJson.locked = this.appHelper.convertBooleanToString(this.locked);

    let attributeInfo = [];
    for (let i = 0; i < this.selectedItems.length; i++) {
      let attributes = new AttributeRefHolder();
      let ref = new MetaIdentifierHolder();
      ref.uuid = this.selectedItems[i].uuid;
      ref.type = this.metaType.DATAPOD;
      attributes.ref = ref;
      attributes.attrId = this.selectedItems[i].attributeId;
      attributeInfo[i] = attributes;
    }
    profileJson.attributeInfo = attributeInfo;
    let filterInfoArray = [];
    if (this.profiledata.filterTableArray != null) {
      if (this.profiledata.filterTableArray.length > 0) {
        for (let i = 0; i < this.profiledata.filterTableArray.length; i++) {

          let filterInfo = new FilterInfo();
          filterInfo.logicalOperator = this.profiledata.filterTableArray[i].logicalOperator;
          filterInfo.operator = this.profiledata.filterTableArray[i].operator;
          filterInfo.operand = [];

          if (this.profiledata.filterTableArray[i].lhsType == 'integer' || this.profiledata.filterTableArray[i].lhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.profiledata.filterTableArray[i].lhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].lhsType == this.metaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FORMULA;
            ref.uuid = this.profiledata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[0] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].lhsType == this.metaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATAPOD;
            ref.uuid = this.profiledata.filterTableArray[i].lhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.profiledata.filterTableArray[i].lhsAttribute.attributeId;
            filterInfo.operand[0] = operatorObj;
          }
          if (this.profiledata.filterTableArray[i].rhsType == 'integer' || this.profiledata.filterTableArray[i].rhsType == 'string') {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.SIMPLE;
            operatorObj.ref = ref;
            operatorObj.value = this.profiledata.filterTableArray[i].rhsAttribute;
            operatorObj.attributeType = "string"
            filterInfo.operand[1] = operatorObj;

            if (this.profiledata.filterTableArray[i].rhsType == 'integer' && this.profiledata.filterTableArray[i].operator == 'BETWEEN') {
              let operatorObj = new SourceAttr();
              let ref = new MetaIdentifier();
              ref.type = this.metaType.SIMPLE;
              operatorObj.ref = ref;
              operatorObj.value = this.profiledata.filterTableArray[i].rhsAttribute1 + "and" + this.profiledata.filterTableArray[i].rhsAttribute2;
              filterInfo.operand[1] = operatorObj;
            }
          }
          else if (this.profiledata.filterTableArray[i].rhsType == this.metaType.FORMULA) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FORMULA;
            ref.uuid = this.profiledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].rhsType == this.metaType.FUNCTION) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.FUNCTION;
            ref.uuid = this.profiledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].rhsType == this.metaType.PARAMLIST) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.PARAMLIST;
            ref.uuid = this.profiledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.profiledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].rhsType == this.metaType.DATASET) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATASET;
            ref.uuid = this.profiledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.profiledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          else if (this.profiledata.filterTableArray[i].rhsType == this.metaType.DATAPOD) {
            let operatorObj = new SourceAttr();
            let ref = new MetaIdentifier();
            ref.type = this.metaType.DATAPOD;
            ref.uuid = this.profiledata.filterTableArray[i].rhsAttribute.uuid;
            operatorObj.ref = ref;
            operatorObj.attributeId = this.profiledata.filterTableArray[i].rhsAttribute.attributeId;
            filterInfo.operand[1] = operatorObj;
          }
          filterInfoArray[i] = filterInfo;
        }
        profileJson.filterInfo = filterInfoArray;
        console.log(JSON.stringify(filterInfoArray));
      }
    }
    console.log(profileJson);
    this._commonService.submit(this.metaType.PROFILE, profileJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById(this.metaType.PROFILE, response).subscribe(
        response => {
          this.OnSucessGetOneById(response);
          this.goBack()
        },
        error => console.log('Error :: ' + error)
      )} 
    else {
      this.isSubmitEnable = true;
      this.IsProgerssShow = "false";
      this.msgs = [];
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Profile Save Successfully' });
      setTimeout(() => {
        this.goBack();
      }, 1000);
    }
  }
  OnSucessGetOneById(response) {
    this._commonService.execute(response.uuid, response.version, this.metaType.PROFILE, "execute").subscribe(
      response => {
        this.showMassage('Profile Save and Submit Successfully', 'success', 'Success Message')
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
  addAll() {
    this.selectedItems = this.dropdownList;
  }
  onAttrRowDown(index) {
    var rowTempIndex = this.profiledata.filterTableArray[index];
    var rowTempIndexPlus = this.profiledata.filterTableArray[index + 1];
    this.profiledata.filterTableArray[index] = rowTempIndexPlus;
    this.profiledata.filterTableArray[index + 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }

  onAttrRowUp(index) {
    var rowTempIndex = this.profiledata.filterTableArray[index];
    var rowTempIndexMines = this.profiledata.filterTableArray[index - 1];
    this.profiledata.filterTableArray[index] = rowTempIndexMines;
    this.profiledata.filterTableArray[index - 1] = rowTempIndex;
    this.iSSubmitEnable = true
  }
  dragStart(event, data) {
    console.log(event)
    console.log(data)
    this.dragIndex = data
  }
  dragEnd(event) {
    console.log(event)
  }
  drop(event, data) {
    if (this.mode == 'false') {
      this.dropIndex = data
      var item = this.profiledata.filterTableArray[this.dragIndex]
      this.profiledata.filterTableArray.splice(this.dragIndex, 1)
      this.profiledata.filterTableArray.splice(this.dropIndex, 0, item)
      this.iSSubmitEnable = true
    }
  }


  updateArray(new_index, range, event) {
    for (let i = 0; i < this.profiledata.filterTableArray.length; i++) {
      if (this.profiledata.filterTableArray[i].selected) {

        if (new_index < 0) {
          this.invalideMinRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index >= this.profiledata.filterTableArray.length) {
          this.invalideMaxRow = true;
          this.resetTableTopBottom.next(event);
        }
        else if (new_index == null) { }
        else {
          let old_index = i;
          this.array_move(this.profiledata.filterTableArray, old_index, new_index);
          if (range) {

            if (new_index == 0 || new_index == 1) {
              this.profiledata.filterTableArray[0].logicalOperator = "";
              if (!this.profiledata.filterTableArray[1].logicalOperator) {
                this.profiledata.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            if (new_index == this.profiledata.filterTableArray.length - 1) {
              this.profiledata.filterTableArray[0].logicalOperator = "";
              if (this.profiledata.filterTableArray[new_index].logicalOperator == "") {
                this.profiledata.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
              }
              this.checkSelected(false, null);
            }
            this.txtQueryChangedFilter.next(new_index);
          }
          else if (new_index == 0 || new_index == 1) {
            this.profiledata.filterTableArray[0].logicalOperator = "";
            if (!this.profiledata.filterTableArray[1].logicalOperator) {
              this.profiledata.filterTableArray[1].logicalOperator = this.logicalOperators[1].label;
            }
            this.profiledata.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          else if (new_index == this.profiledata.filterTableArray.length - 1) {
            this.profiledata.filterTableArray[0].logicalOperator = "";
            if (this.profiledata.filterTableArray[new_index].logicalOperator == "") {
              this.profiledata.filterTableArray[new_index].logicalOperator = this.logicalOperators[1].label;
            }
            this.profiledata.filterTableArray[new_index].selected = "";
            this.checkSelected(false, null);
          }
          break;
        }
      }
    }
  }

  array_move(arr, old_index, new_index) {

    while (old_index < 0) {
      old_index += arr.length;
    }
    while (new_index < 0) {
      new_index += arr.length;
    }
    if (new_index >= arr.length) {
      var k = new_index - arr.length + 1;
      while (k--) {
        arr.push(undefined);
      }
    }
    arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
    return arr;
  }

  checkSelected(flag: any, index: any) {
    if (flag == true) {
      this.count.push(flag);
    }
    else
      this.count.pop();

    this.moveToEnable = (this.count.length == 1) ? true : false;

    if (index != null) {
      if (index == 0 && flag == true) {
        this.topDisabled = true;
      }
      else {
        this.topDisabled = false;
      }

      if (index == (this.profiledata.filterTableArray.length - 1) && flag == true) {
        this.bottomDisabled = true;
      }
      else {
        this.bottomDisabled = false;
      }
    }
  }

  ngOnDestroy() {
    this.txtQueryChangedFilter.unsubscribe();
    this.resetTableTopBottom.unsubscribe();
  }
}
