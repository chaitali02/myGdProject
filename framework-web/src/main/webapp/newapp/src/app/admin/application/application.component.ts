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
  datasourceType: { 'value': string; name: string; }[];
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

  isHomeEnable: boolean = false
  showGraph: boolean = false;
  isDependencyGraphEnable: boolean = true;
  isShowReportData: boolean = true;

  paramName: any;
  paramtable: any[];
  selectallattribute: boolean;
  types: any[];
  date9: Date;
  allMapFormula: any;
  typeDistributions: any[];
  typeFunctions: any[];
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute,
    public router: Router, private _commonService: CommonService, private _applicationService: ApplicationService,
    private datePipe: DatePipe) {

    this.showApplication = true;
    this.application = {};
    this.application["active"] = true

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
  }

  ngOnInit() {
    this.attrtypes = ["string", "float", "bigint", 'double', 'timestamp', 'integer'];
    this.datasourceType = [{ 'value': 'FILE', name: 'file' },
    { 'value': 'HIVE', name: 'hive' },
    { 'value': 'IMPALA', name: 'impala' },
    { 'value': 'MYSQL', name: 'mysql' },
    { 'value': 'ORACLE', name: 'oracle' }];
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
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'applicationview')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('application', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.application = response;
    this.createdBy = response.createdBy.ref.name;
    this.uuid = response.uuid;

    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag
      }//End For
      this.tags = tags;
    }//End If

    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.version = response['version'];
    this.published = response['published'];
    this.application.published = response["published"] == 'Y' ? true : false;
    this.application.locked = response["locked"] == 'Y' ? true : false;
    this.application.active = response["active"] == 'Y' ? true : false
    this.detasource_uuid = response.dataSource.ref.uuid;
    this.deployPort = response.deployPort;
    this.getLatestByUuid(response.dataSource.ref.uuid);

    var paramArray = [];
    if (response.paramList != null) {
      for (var i = 0; i < response.paramList.params.length; i++) {
        var paramInfo = {}
        paramInfo["id"] = i + 1;
        paramInfo["name"] = response.paramList.params[i].paramName;
        paramInfo["type"] = response.paramList.params[i].paramType;
        paramInfo["desc"] = response.paramList.params[i].paramDesc;
        paramInfo["dispName"] = response.paramList.params[i].paramDispName;
        paramInfo["paramType"] = response.paramList.params[i].paramType.toLowerCase();

        if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == "simple" && ["string", "double", "integer", "list"].indexOf(response.paramList.params[i].paramType) != -1) {
          paramInfo["doubleType"] = response.paramList.params[i].paramValue.value;
        }
        else if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == "simple" && ["date"].indexOf(response.paramList.params[i].paramType) != -1) {
          var temp = response.paramList.params[i].paramValue.value.replace(/["']/g, "")
          paramInfo["doubleType"] = new Date(temp);
        }
        else if (response.paramList.params[i].paramValue != null && response.paramList.params[i].paramValue.ref.type == "simple" && ["array"].indexOf(response.paramList.params[i].paramType) != -1) {
          var temp = response.paramList.params[i].paramValue.value.split(",");
          let allname = [];
          for (let i = 0; i < temp.length; i++) {
            let allnametemp = {};
            allnametemp["display"] = temp[i];
            allnametemp["value"] = temp[i];
            allname[i] = allnametemp;
          }
          paramInfo["doubleType"] = allname;
        }
        else if (response.paramList.params[i].paramValue != null && ["function"].indexOf(response.paramList.params[i].paramType) != -1) {
          this.getFunctionByCriteria(i);
          let allname = {};
          allname["label"] = response.paramList.params[i].paramValue.ref.name;
          allname["uuid"] = response.paramList.params[i].paramValue.ref.uuid;
          paramInfo["doubleType"] = allname;
        }
        else if (response.paramList.params[i].paramValue != null && ["distribution"].indexOf(response.paramList.params[i].paramType) != -1) {
          this.getAllDistribution(i);
          let allname = {};
          allname["label"] = response.paramList.params[i].paramValue.ref.name;
          allname["uuid"] = response.paramList.params[i].paramValue.ref.uuid;
          paramInfo["doubleType"] = allname;
        }
        else if (response.paramList.params[i].paramValue != null) {
          var paramValue = {};
          paramValue["uuid"] = response.paramList.params[i].paramValue.ref.uuid;
          paramValue["type"] = response.paramList.params[i].paramValue.ref.type;
          paramInfo["paramValue"] = paramValue;
          paramInfo["type"] = response.paramList.params[i].paramValue.ref.type;
        } else {

        }
        paramArray[i] = paramInfo;
      }
    }
    this.paramtable = paramArray;
  }
  getLatestByUuid(uuid): any {
    this._applicationService.getLatestByUuid(uuid, "datasource")
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'application')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }
  onChangeActive(event) {

    if (event === true) {
      this.application.active = 'Y';
    }
    else {
      this.application.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.application.published = 'Y';
    }
    else {
      this.application.published = 'N';
    }
  }

  getDatasourceByType(val) {
    this._applicationService.getDatasourceByType(val)
      .subscribe(
        response => {
          this.OnSuccesDatasourceByType(response)
        },
        error => console.log("Error :: " + error));
  }
  OnSuccesDatasourceByType(response) {
    const datasource2: Array<ApplicationResource> = [];
    for (const i in response) {
      datasource2.push(new ApplicationResource(
        response[i]['uuid'], response[i]['name']
      ));
    }
    this.datasource1 = datasource2;
  }

  submitApplication() {
    let applicationJson = {};
    this.isSubmitEnable = true;
    applicationJson["paramlistChg"] = 'y';
    applicationJson["applicationChg"] = 'y';
    applicationJson["uuid"] = this.application.uuid
    applicationJson["name"] = this.application.name;
    applicationJson["desc"] = this.application.desc;
    applicationJson["active"] = this.application.active == true ? 'Y' : "N";
    applicationJson["published"] = this.application.published == true ? 'Y' : "N";
    applicationJson["locked"] = this.application.locked == true ? 'Y' : "N";
    applicationJson["deployPort"] = this.application.deployPort;

    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;

      }
    }
    applicationJson['tags'] = tagArray

    let datasource = {}
    let ref = {};
    ref["type"] = "datasource";
    ref["uuid"] = this.detasource_uuid;
    datasource["ref"] = ref;
    applicationJson["dataSource"] = datasource;

    let paramList = {}
    paramList["paramListType"] = "application";
    paramList["templateFlg"] = "Y";
    paramList["params"] = [];

    for (let i = 0; i < this.paramtable.length; i++) {
      let paramsObj = {};
      paramsObj["paramId"] = i;
      paramsObj["paramName"] = this.paramtable[i]["name"];
      paramsObj["paramType"] = this.paramtable[i]["type"];
      paramsObj["paramDesc"] = this.paramtable[i]["desc"];
      paramsObj["paramDispName"] = this.paramtable[i]["dispName"];;

      let paramValue = {}
      let ref1 = {};
      if (this.paramtable[i]["type"] == 'function') {
        ref1["type"] = "function";
        ref1["uuid"] = this.detasource_uuid;
        paramValue["ref"] = ref1;
      }
      else if (this.paramtable[i]["type"] == 'distribution') {
        ref1["type"] = "distribution";
        ref1["uuid"] = this.detasource_uuid;
        paramValue["ref"] = ref1;
      }
      else if (this.paramtable[i]["type"] == 'double' || this.paramtable[i]["type"] == 'integer' ||
        this.paramtable[i]["type"] == 'list' || this.paramtable[i]["type"] == 'string') {
        ref1["type"] = "simple";
        paramValue["ref"] = ref1;
        paramValue["value"] = this.paramtable[i]["doubleType"];
      }
      else if (this.paramtable[i]["type"] == 'date') {
        ref1["type"] = "simple";
        paramValue["ref"] = ref1;
        paramValue["value"] = this.datePipe.transform(this.paramtable[i]["doubleType"], "MM/dd/yyyy");
      }
      else if (this.paramtable[i]["type"] == 'array') {
        ref1["type"] = "simple";
        paramValue["ref"] = ref1;
        let tagArray = [];
        if (this.paramtable[i]["doubleType"] != null) {
          for (let counttag = 0; counttag < this.paramtable[i]["doubleType"].length; counttag++) {
            tagArray[counttag] = this.paramtable[i]["doubleType"][counttag].value;
          }
        }
        paramValue["value"] = tagArray.toString();;
      }
      paramsObj["paramValue"] = paramValue;
      paramList["params"].push(paramsObj);
    }
    applicationJson["paramList"] = paramList;

    console.log(JSON.stringify(applicationJson));
    this._commonService.submit("applicationview", applicationJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error ::' + error)
    )
  }

  OnSuccessubmit(response) {
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
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/application', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/admin/application', uuid, version, 'true']);
  }

  showMainPage(uuid, version) {
    this.isHomeEnable = false
    this.showGraph = false;
    this.isDependencyGraphEnable = true;
    this.isShowReportData = true;
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

  onChangeType(type, index) {
    console.log("onChangeType ...." + this.paramtable);
    let g = this.paramtable;
    if (type == "distribution") {
      this.paramtable["type"] = {}
      this.getAllDistribution(index);
    }
    else if (type == "function") {
      this.paramtable["type"] = {}
      this.getFunctionByCriteria(index);
    }
  }

  getAllDistribution(index) {
    this._commonService.getAllLatest("distribution").subscribe(
      response => { this.onSuccessgetAllDistribution(response, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessgetAllDistribution(response, index) {
    let temp = []
    if (response.length > 0) {
      for (const n in response) {
        let allname = {};
        allname["label"] = response[n]['name'];
        allname["value"] = {};
        allname["value"]["label"] = response[n]['name'];
        allname["value"]["uuid"] = response[n]['uuid'];
        temp[n] = allname;
      }
      this.typeDistributions = temp;
    }
  }

  getFunctionByCriteria(index) {
    this._applicationService.getFunctionByCriteria("function", '').subscribe(
      response => { this.onSuccessFunction(response, index) },
      error => console.log('Error :: ' + error)
    )
  }
  onSuccessFunction(response, index) {
    let temp = []
    for (const n in response) {
      let allname = {};
      allname["label"] = response[n]['name'];
      allname["value"] = {};
      allname["value"]["label"] = response[n]['name'];
      allname["value"]["uuid"] = response[n]['uuid'];
      temp[n] = allname;
    }
    this.typeFunctions = temp;
  }

}
