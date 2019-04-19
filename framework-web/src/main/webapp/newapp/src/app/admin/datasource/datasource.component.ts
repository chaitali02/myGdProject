import { BaseEntity } from './../../metadata/domain/domain.baseEntity';
import { Datasource } from './../../metadata/domain/domain.datasource';
import { RoutesParam } from './../../metadata/domain/domain.routeParams';
import { AppHelper } from './../../app.helper';
import { MetaType } from './../../metadata/enums/metaType';

import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem, DataGridModule } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';
import { DropDownIO } from '../../metadata/domainIO/domain.dropDownIO';


@Component({
  selector: 'app-datasource',
  templateUrl: './datasource.template.html',
  styleUrls: ['./datasource.component.css']
})
export class DatasourceComponent implements OnInit {

  breadcrumbDataFrom: any;
  showDatasource: any;
  datasource: any;
  versions: any[];
  VersionList: SelectItem[] = [];
  selectedVersion: Version;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  published: any;
  depends: any;
  allName: any;
  access: any;
  driver: any;
  host: any;
  dbname: any;
  port: any;
  username: any;
  password: any;
  path: any;
  msgs: any;
  datasourceOnType: any;
  type: any;
  typeOn: any;
  allNames: any;
  isSubmitEnable: any;
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
  locked: any;
  // paramListuuid: any;


  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router,
    private _commonService: CommonService, private appHelper: AppHelper) {
    this.showDatasource = true;
    this.isHomeEnable = false;
    this.datasource = {};
    this.active = true;
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [
      {
        "caption": "Admin",
        "routeurl": "/app/list/datasource"
      },
      {
        "caption": "Datasource",
        "routeurl": "/app/list/datasource"
      },
      {
        "caption": "",
        "routeurl": null
      }]

    this.datasourceOnType = ["File", "Hive"];

    this.isEditInprogess = false;
    this.isEditError = false;
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      let param = <RoutesParam>params;
      this.id = param.id;
      this.version = param.version;
      this.mode = param.mode;
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
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
    this.isEdit = true;
    this.router.navigate(['app/admin/datasource', uuid, version, 'false']);
  }

  showMainPage() {
    this.isHomeEnable = false
    this.showDivGraph = false;
    this.showForm = true;
    // this.isDependencyGraphEnable = true;
    // this.isShowReportData = true;
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
    this.router.navigate(['app/admin/datasource', uuid, version, 'true']);
  }

  getOneByUuidAndVersion() {
    this.isEditInprogess = true;
    this.isEditError = false;
    this._commonService.getOneByUuidAndVersion(this.id, this.version, this.metaType.DATASOURCE)
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
    this._commonService.getAllVersionByUuid(this.metaType.DATASOURCE, this.id)
      .subscribe(
        response => {
          this.onSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response: Datasource) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.datasource = response;
    this.uuid = response.uuid;

    const version: Version = new Version();
    version.label = response.version;
    version.uuid = response.uuid;
    this.selectedVersion = version
    // var tags = [];
    // if (response.tags != null) {
    //   for (var i = 0; i < response.tags.length; i++) {
    //     var tag = {};
    //     tag['value'] = response.tags[i];
    //     tag['display'] = response.tags[i];
    //     tags[i] = tag

    //   }
    //   this.datasource.tags = tags;
    // }
    // this.createdBy = response.createdBy.ref.name;
    // this.version = response['version'];
    this.datasource.published = this.appHelper.convertStringToBoolean(response.published);
    this.datasource.active = this.appHelper.convertStringToBoolean(response.active);
    this.datasource.type = response.type;

    // this.published = response['published'];
    // if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    // this.active = response['active'];
    // if(this.active === 'Y') { this.active = true; } else { this.active = false; }

    this.isEditInprogess = false;
  }

  onSuccesgetAllVersionByUuid(response: BaseEntity[]) {
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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, this.metaType.DATASOURCE)
      .subscribe(
        response => {//console.log(response)},
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  submitDatasource() {
    this.isSubmitEnable = true;
    let datasourceJson = new Datasource;
    datasourceJson.name = this.datasource.name;
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    // // if(this.tags.length > 0){
    // //   for(let counttag=0;counttag < this.tags.length;counttag++){
    // //     tagArray[counttag]=this.tags[counttag]["value"];
    // //   }
    // // }
    // datasourceJson["tags"] = tagstemp
    // var tagArray = [];
    // if (this.datasource.tags != null) {
    //   for (var counttag = 0; counttag < this.datasource.tags.length; counttag++) {
    //     tagArray[counttag] = this.datasource.tags[counttag].value;

    //   }
    // }
    datasourceJson.tags = this.datasource.tags;
    datasourceJson.desc = this.datasource.desc;
    datasourceJson.active = this.appHelper.convertBooleanToString(this.active);
    datasourceJson.locked = this.appHelper.convertBooleanToString(this.locked);
    datasourceJson.published = this.appHelper.convertBooleanToString(this.published);

    let attributeInfo = [];
    datasourceJson.type = this.datasource.type;
    datasourceJson.access = this.datasource.access;
    datasourceJson.driver = this.datasource.driver;
    datasourceJson.host = this.datasource.host;
    datasourceJson.dbname = this.datasource.dbname;
    datasourceJson.port = this.datasource.port;
    datasourceJson.username = this.datasource.username;
    datasourceJson.password = this.datasource.password;
    datasourceJson.path = this.datasource.path;
    datasourceJson.sessionParameters = this.datasource.sessionParameters;
    console.log(JSON.stringify(datasourceJson))
    this._commonService.submit(this.metaType.DATASOURCE, datasourceJson).subscribe(
      response => { this.onSuccessubmit(response) },
      error => console.log('Error :: ', +error)
    )
  }

  onSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Datasource Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
    console.log('final response is' + JSON.stringify(response));
  }

  public goBack() {
    //this._location.back(); 
    this.router.navigate(['app/list/datasource']);
  }

}
