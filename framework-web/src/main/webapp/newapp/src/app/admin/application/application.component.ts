import { Param } from './../../metadata/domain/domain.param';
import { MetaIdentifier } from './../../metadata/domain/domain.metaIdentifier';
import { BaseEntity } from './../../metadata/domain/domain.baseEntity';
import { Application } from './../../metadata/domain/domain.application';
import { DropDownIO } from './../../metadata/domainIO/domain.dropDownIO';
import { ParamInfoIO } from './../../metadata/domainIO/domain.paramInfoIO';
import { AppHelper } from './../../app.helper';
import { KnowledgeGraphComponent } from './../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { Location, DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { ApplicationService } from '../../metadata/services/application.service';
import { ApplicationResource } from './application-resource';

import { Version } from '../../shared/version';
import { MetadataService } from '../../metadata/services/metadata.service';
import { MetaType } from '../../metadata/enums/metaType';
import { MetaIdentifierHolder } from '../../metadata/domain/domain.metaIdentifierHolder';
import { ParamList } from '../../metadata/domain/domain.paramList';

@Component({
  selector: 'app-application',
  templateUrl: './application.template.html',
  styleUrls: ['./application.component.css']
})
export class ApplicationComponent implements OnInit {

  breadcrumbDataFrom: any;
  showApplication: any;
  application: any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  selectdatasourceType: any;
  datasourceType: { value: string; name: string; }[];
  attrtypes: string[];
  detasource_uuid: any;
  datasource1: any;
  datasource: any;
  tags: any;
  desc: any;
  createdOn: any;
  deployPort: any;
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
  allName: any;
  msgs: any;
  isSubmitEnable: any;

  isShowReportData: boolean = true;

  paramName: any;
  paramtable: any[];
  selectallattribute: boolean;
  types: any[];
  date9: Date;
  allMapFormula: any;
  typeDistributions: any[];
  typeFunctions: any[];
  // applicationType: { label: string; value: string }[];
  applicationType: SelectItem[] = [
    { label: 'DEFAULT', value: 'DEFAULT' },
    { label: 'SYSADMIN', value: 'SYSADMIN', disabled: true },
    { label: 'APPADMIN', value: 'APPADMIN', disabled: true }
  ];

  selectedApplicationType: any;
  orgName: any = {};

  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;
  caretdown = 'fa fa-caret-down';
  metaType = MetaType;
  isEditInprogess: boolean = false;
  isEditError: boolean = false;
  isEdit: boolean = false;
  isversionEnable: boolean;
  isAdd: boolean;
  isHomeEnable: boolean;
  showForm: boolean = true;
  showDivGraph: boolean;
  isGraphInprogess: boolean;
  paramListuuid: any;

  constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute,
    public router: Router, private _commonService: CommonService, private _applicationService: ApplicationService,
    private datePipe: DatePipe, private appHelper: AppHelper) {

    this.showApplication = true;
    this.application = {};
    this.active = true

    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [
      {
        "caption": "Admin",
        "routeurl": "/app/list/application"
      },
      {
        "caption": "Application",
        "routeurl": "/app/list/application"
      },
      {
        "caption": "",
        "routeurl": null
      }
    ]

    this.paramName = {};
    this.selectallattribute = false;

    this.isEditInprogess = false;
    this.isEditError = false;
  }

  ngOnInit() {
    this.attrtypes = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
    this.datasourceType = [
      { value: 'FILE', name: 'file' },
      { value: 'HIVE', name: 'hive' },
      { value: 'IMPALA', name: 'impala' },
      { value: 'MYSQL', name: 'mysql' },
      { value: 'ORACLE', name: 'oracle' }
    ];

    this.selectdatasourceType = this.datasourceType[0].value
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
      else {
        this.getLatestByUuidOrg("d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd"); //get uuid from headerComponent.
        this.getDatasourceByType(this.selectdatasourceType);
      }
    });

    this.types = [
      { "name": "string", "caption": "string" },
      { "name": "double", "caption": "double" },
      { "name": "date", "caption": "date" },
      { "name": "integer", "caption": "integer" },
      { "name": "decimal", "caption": "decimal" },
      { "name": "attribute", "caption": "attribute" },
      { "name": "attributes", "caption": "attribute[s]" },
      { "name": "distribution", "caption": "distribution" },
      { "name": "datapod", "caption": "datapod" },
      { "name": "function", "caption": "function" },
      { "name": "list", "caption": "list" },
      { "name": "array", "caption": "array" }
    ];

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
    this.isEdit = true;
    this.router.navigate(['app/admin/application', uuid, version, 'false']);
  }

  showMainPage(uuid, version) {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
    // this.isDependencyGraphEnable = true;
    this.isShowReportData = true;
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

  showview(uuid, version) {
    this.router.navigate(['app/admin/application', uuid, version, 'true']);
  }

  getLatestByUuidOrg(uuid): any {
    this._applicationService.getLatestByUuid(uuid, this.metaType.APPLICATION)
      .subscribe(
        response => {
          this.onSuccesGetLatestByUuidOrg(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccesGetLatestByUuidOrg(response) {
    let orgNameTemp = { name: "", uuid: "" }
    orgNameTemp.name = response.orgInfo.ref.name;
    orgNameTemp.uuid = response.orgInfo.ref.uuid;
    this.orgName = orgNameTemp;
  }

  getOneByUuidAndVersion() {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(this.id, this.version, this.metaType.APPLICATIONVIEW)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => {
          console.log("Error :: " + error);
          this.isEditError = true;
        });
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid(this.metaType.APPLICATION, this.id)
      .subscribe(
        response => {
          this.onSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {

    this.breadcrumbDataFrom[2].caption = response.name;
    this.application = response;
    // this.createdBy = response.createdBy.ref.name;
    this.uuid = response.uuid;

    // var tags = [];
    // if (response.tags != null) {
    //   for (var i = 0; i < response.tags.length; i++) {
    //     var tag = {};
    //     tag['value'] = response.tags[i];
    //     tag['display'] = response.tags[i];
    //     tags[i] = tag
    //   }//End For
    //   this.tags = tags;
    // }//End If

    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version

    // this.version = response['version'];
    // this.published = response['published'];
    this.published = this.appHelper.convertStringToBoolean(response.published);
    this.locked = this.appHelper.convertStringToBoolean(response.locked);
    this.active = this.appHelper.convertStringToBoolean(response.active);

    this.detasource_uuid = response.dataSource.ref.uuid;
    this.deployPort = response.deployPort;
    this.getLatestByUuid(response.dataSource.ref.uuid);


    // this.orgName = response.orgInfo;
    let orgNameTemp = { name: "", uuid: "" }
    orgNameTemp.name = response.orgInfo.ref.name;
    orgNameTemp.uuid = response.orgInfo.ref.uuid;
    this.orgName = orgNameTemp;

    let appInfoUuid = response.appInfo[0].ref.uuid;
    this.getAllApplicationType(appInfoUuid);

    var paramArray = [];
    if (response.paramList != null) {
      for (var i = 0; i < response.paramList.params.length; i++) {
        var paramInfo = new ParamInfoIO;
        paramInfo.id = i + 1;
        this.paramListuuid = response.paramList.uuid;
        paramInfo.name = response.paramList.params[i].paramName;
        paramInfo.type = response.paramList.params[i].paramType;
        paramInfo.desc = response.paramList.params[i].paramDesc;
        paramInfo.dispName = response.paramList.params[i].paramDispName;
        paramInfo.paramType = response.paramList.params[i].paramType.toLowerCase();

        if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == this.metaType.SIMPLE && ["string", "double", "integer", "list"].indexOf(response.paramList.params[i].paramType) != -1) {
          paramInfo.doubleType = response.paramList.params[i].paramValue.value;
        }
        else if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == this.metaType.SIMPLE && ["date"].indexOf(response.paramList.params[i].paramType) != -1) {
          var temp = response.paramList.params[i].paramValue.value.replace(/["']/g, "")
          paramInfo.doubleType = new Date(temp);
        }
        else if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == this.metaType.SIMPLE && ["array"].indexOf(response.paramList.params[i].paramType) != -1) {
          var temp = response.paramList.params[i].paramValue.value.split(",");
          // let allname = [];
          // for (let i = 0; i < temp.length; i++) {
          //   let allnametemp = {display: "", value: ""};
          //   allnametemp.display = temp[i];
          //   allnametemp.value = temp[i];
          //   allname[i] = allnametemp;
          // }
          paramInfo.doubleType = temp;
        }
        else if (response.paramList.params[i].paramValue != null && [this.metaType.FUNCTION].indexOf(response.paramList.params[i].paramType) != -1) {
          this.getFunctionByCriteria(i);
          let allname = { label: "", uuid: "" };
          allname.label = response.paramList.params[i].paramValue.ref.name;
          allname.uuid = response.paramList.params[i].paramValue.ref.uuid;
          paramInfo.doubleType = allname;
        }
        else if (response.paramList.params[i].paramValue != null && [this.metaType.DISTRIBUTION].indexOf(response.paramList.params[i].paramType) != -1) {
          this.getAllDistribution(i);
          let allname = { label: "", uuid: "" };
          allname.label = response.paramList.params[i].paramValue.ref.name;
          allname.uuid = response.paramList.params[i].paramValue.ref.uuid;
          paramInfo.doubleType = allname;
        }
        else if (response.paramList.params[i].paramValue != null) {
          var paramValue = { uuid: "", type: "" };
          paramValue.uuid = response.paramList.params[i].paramValue.ref.uuid;
          paramValue.type = response.paramList.params[i].paramValue.ref.type;
          paramInfo.paramValue = paramValue;
          paramInfo.type = response.paramList.params[i].paramValue.ref.type;
        } else {

        }
        paramArray[i] = paramInfo;
      }
    }
    this.paramtable = paramArray;
    this.isEditInprogess = false;
  }

  getAllApplicationType(uuid) {
    this._commonService.getLatestByUuid(uuid, this.metaType.APPLICATION).subscribe(
      response => { this.onSuccessgetAllApplicationType(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllApplicationType(response): any {
    this.selectedApplicationType = response.applicationType;
    // getAllLatestOrgnization() For admin user.
  }
  getAllLatestOrgnization() {
    this._commonService.getAllLatest(this.metaType.ORGANIZATION).subscribe(
      response => { this.onSuccessgetAllLatestOrgnization(response) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllLatestOrgnization(response: any[]): any {
    let asd = response
  }

  getLatestByUuid(uuid): any {
    this._applicationService.getLatestByUuid(uuid, this.metaType.DATASOURCE)
      .subscribe(
        response => {
          this.onSuccesGetLatestByUuid(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccesGetLatestByUuid(response: any): any {
    this.selectdatasourceType = response.type;
    this.getDatasourceByType(this.selectdatasourceType);
  }

  onSuccesgetAllVersionByUuid(response) {
    var temp = []
    for (const i in response) {
      let ver = new DropDownIO();
      ver.label = response[i].version;
      ver.value = { label: "", uuid: "" };
      ver.value.label = response[i].version;
      ver.value.uuid = response[i].uuid;
      temp[i] = ver;
    }
    this.VersionList = temp
  }
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metaType.APPLICATION)
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }


  getDatasourceByType(val) {
    this._applicationService.getDatasourceByType(val)
      .subscribe(
        response => {
          this.onSuccesDatasourceByType(response)
        },
        error => console.log("Error :: " + error));
  }
  onSuccesDatasourceByType(response) {
    const datasource2: Array<ApplicationResource> = [];
    for (const i in response) {
      datasource2.push(new ApplicationResource(
        response[i].uuid, response[i].name
      ));
    }
    this.datasource1 = datasource2;
  }

  submitApplication() {
    let applicationJson = new Application;
    this.isSubmitEnable = true;
    applicationJson.paramlistChg = 'y';
    applicationJson.applicationChg = 'y';
    applicationJson.uuid = this.application.uuid
    applicationJson.name = this.application.name;
    applicationJson.desc = this.application.desc;
    applicationJson.active = this.appHelper.convertBooleanToString(this.active);
    applicationJson.published = this.appHelper.convertBooleanToString(this.published);
    applicationJson.locked = this.appHelper.convertBooleanToString(this.locked);
    applicationJson.deployPort = this.application.deployPort;
    applicationJson.applicationType = this.selectedApplicationType;

    applicationJson.tags = this.application.tags;

    let orginfo = new MetaIdentifierHolder;
    let orginforef = new MetaIdentifier;
    orginforef.type = this.metaType.ORGANIZATION;
    orginforef.uuid = this.orgName.uuid;
    orginfo.ref = orginforef;
    applicationJson.orgInfo = orginfo;

    let datasource = new MetaIdentifierHolder;
    let ref = new MetaIdentifier;
    ref.type = this.metaType.DATASOURCE;
    ref.uuid = this.detasource_uuid;
    datasource.ref = ref;
    applicationJson.dataSource = datasource;

    let paramList = new ParamList();
    if (this.paramListuuid) {
      paramList.uuid = this.paramListuuid;
    }

    paramList.paramListType = this.metaType.APPLICATION;
    paramList.templateFlg = "Y";
    paramList.params = [];
    for (let i = 0; i < this.paramtable.length; i++) {
      let paramsObj = new Param;
      paramsObj.paramId = i.toString();
      paramsObj.paramName = this.paramtable[i].name;
      paramsObj.paramType = this.paramtable[i].type;
      paramsObj.paramDesc = this.paramtable[i].desc;
      paramsObj.paramDispName = this.paramtable[i].dispName;

      let paramValue = new MetaIdentifierHolder;
      let ref1 = new MetaIdentifier;
      if (this.paramtable[i].type == this.metaType.FUNCTION) {
        ref1.type = this.metaType.FUNCTION;
        ref1.uuid = this.paramtable[i].doubleType.uuid;
        paramValue.ref = ref1;
      }
      else if (this.paramtable[i].type == this.metaType.DISTRIBUTION) {
        ref1.type = this.metaType.DISTRIBUTION;
        ref1.uuid = this.paramtable[i].doubleType.uuid;
        paramValue.ref = ref1;
      }
      else if (this.paramtable[i].type == 'double' || this.paramtable[i].type == 'integer' ||
        this.paramtable[i].type == 'list' || this.paramtable[i].type == 'string') {
        ref1.type = this.metaType.SIMPLE;
        paramValue.ref = ref1;
        paramValue.value = this.paramtable[i].doubleType;
      }
      else if (this.paramtable[i].type == 'date') {
        ref1.type = this.metaType.SIMPLE;
        paramValue.ref = ref1;
        paramValue.value = "'" + this.datePipe.transform(this.paramtable[i].doubleType, "MM/dd/yyyy") + "'";
      }
      else if (this.paramtable[i].type == 'array') {
        ref1.type = this.metaType.SIMPLE;
        paramValue.ref = ref1;
        // let tagArray = [];
        // if (this.paramtable[i].doubleType != null) {
        //   for (let counttag = 0; counttag < this.paramtable[i].doubleType.length; counttag++) {
        //     tagArray[counttag] = this.paramtable[i].doubleType[counttag].value;
        //   }
        // }
        paramValue.value = this.paramtable[i].doubleType.toString();;
      }
      
      if (paramValue.value == undefined && paramValue.ref == undefined) {
        paramsObj.paramValue = null;
      }
      else {
        paramsObj.paramValue = paramValue;
      }
      paramList.params.push(paramsObj);
    }
    applicationJson.paramList = paramList;

    console.log(JSON.stringify(applicationJson));
    this._commonService.submit(this.metaType.APPLICATIONVIEW, applicationJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error ::' + error)
    )
  }

  onSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Activity Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
    console.log('final response is' + response);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/application']);
  }


  addRow() {
    if (this.paramtable == null) {
      this.paramtable = [];
    }
    var len = this.paramtable.length + 1
    var filertable = {};
    this.paramtable.splice(this.paramtable.length, 0, filertable);
  }

  removeRow() {
    let newDataList = [];
    this.selectallattribute = false;
    this.paramtable.forEach(selected => {
      if (!selected.selected) {
        newDataList.push(selected);
      }
    });
    this.paramtable = newDataList;
  }

  onChangeType(type, index) {debugger
    console.log("onChangeType ...." + this.paramtable);
    if (type == this.metaType.DISTRIBUTION) {
      // this.paramtable["type"] = {}
      this.getAllDistribution(index);
    }
    else if (type == this.metaType.FUNCTION) {
      // this.paramtable["type"] = {}
      this.getFunctionByCriteria(index);
    }
  }

  getAllDistribution(index) {
    this._commonService.getAllLatest(this.metaType.DISTRIBUTION).subscribe(
      response => { this.onSuccessgetAllDistribution(response, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllDistribution(response: BaseEntity[], index) {
    let temp = []
    if (response.length > 0) {
      for (const n in response) {
        let allname = new DropDownIO();
        allname.label = response[n].name;
        allname.value = { label: "", uuid: "" };
        allname.value.label = response[n].name;
        allname.value.uuid = response[n].uuid;
        temp[n] = allname;
      }
      this.typeDistributions = temp;
    }
  }

  getFunctionByCriteria(index) {
    this._applicationService.getFunctionByCriteria(this.metaType.FUNCTION, '').subscribe(
      response => { this.onSuccessFunction(response, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response: BaseEntity[], index) {
    let temp = []
    for (const n in response) {
      let allname = new DropDownIO;
      allname.label = response[n].name;
      allname.value = { label: "", uuid: "" };
      allname.value.label = response[n].name;
      allname.value.uuid = response[n].uuid;
      temp[n] = allname;
    }
    this.typeFunctions = temp;
  }

  selectType(){
    console.log("Application type call...");
  }

}
