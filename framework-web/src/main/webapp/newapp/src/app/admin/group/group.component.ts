import { Location } from '@angular/common';
import { Component, OnInit, group, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { DependsOn } from './dependsOn'
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';

@Component({
  selector: 'app-group',
  templateUrl: './group.template.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit {
  breadcrumbDataFrom: any;
  showGroup: any;
  group: any;
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
  locked: any;
  depends: any;
  allName: any;
  roleInfoArray: any;
  roleInfoTags: any;
  roleInfo: any;
  dropdownSettingsRole: any;
  roleResponse: any;
  msgs: any;
  booleanRoleInfo: any;
  roleIdName: any;
  roleIdUuid: any;
  groupInfoArray: any;
  groupInfoTags: any;
  dropdownSettingsGroup: any;
  appIdArray: any;
  roleId: any;
  appId: any;
  responseRole: any;
  responseAppli: any;
  roleIdArray: any;
  isSubmitEnable: any;
  appIdName: any;
  appIdUuid: any;
  //depenDependsOn : DependsOn;

  isHomeEnable: boolean = false
  showGraph: boolean = false;
  isShowReportData: boolean = true;
  @ViewChild(KnowledgeGraphComponent) d_KnowledgeGraphComponent: KnowledgeGraphComponent;

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showGroup = true;
    this.group = {};
    this.appId = {};
    this.roleId = {};
    this.group["active"] = true
    this.isSubmitEnable = true;
    this.roleResponse = null;
    this.roleInfoTags = null;

    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/group"
    },
    {
      "caption": "Group",
      "routeurl": "/app/list/group"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dropdownSettingsRole = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: false

    };
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];

      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
        this.dropdownSettingsRole.disabled = this.mode == "false" ? false : true
      } this.getAllLatestRole();
      this.getAllLatestAppli();
    })
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id, this.version);
    }, 1000);
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'group')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('group', this.id)
      .subscribe(
        response => {
          this.OnSuccesgetAllVersionByUuid(response)
        },
        error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.group = response;
    this.uuid = response.uuid;
    this.name = response.name;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    this.createdBy = response.createdBy.ref.name;
    this.published = response["published"] == 'Y' ? true : false;
    this.group.locked = response["locked"] == 'Y' ? true : false;
    this.active = response["active"] == 'Y' ? true : false
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.group.tags = tags;
    }
    let dependOnTemp1: DependsOn = new DependsOn();
    dependOnTemp1.uuid = response["roleId"]["ref"]["uuid"]
    dependOnTemp1.label = response["roleId"]["ref"]["name"]
    this.roleId = dependOnTemp1;

    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.uuid = response["appId"]["ref"]["uuid"]
    dependOnTemp.label = response["appId"]["ref"]["name"]
    this.appId = dependOnTemp;
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

  getAllLatestRole() {
    this._commonService.getAllLatest('role')
      .subscribe(
        response => {
          this.onSuccessgetAllLatestRole(response)
        },
        error => console.log("Error ::" + error));
  }

  onSuccessgetAllLatestRole(response) {
    this.responseRole = response;
    console.log('getAllLatestRole is start');
    this.roleResponse = response

    this.roleIdArray = [];
    for (const j in response) {
      let rolerefObj = {};
      rolerefObj["label"] = response[j]['name'];
      rolerefObj["value"] = {}
      rolerefObj["value"]["label"] = response[j]['name'];
      rolerefObj["value"]["name"] = response[j]['name'];
      rolerefObj["value"]["uuid"] = response[j]['uuid'];

      this.roleIdArray[j] = rolerefObj;
    }

    console.log('getAllLatestRole is end');
    console.log(this.roleIdArray);
  }

  getAllLatestAppli() {
    this._commonService.getAllLatest('application')
      .subscribe(
        response => {
          this.onSuccessgetAllLatestAppli(response)
        },
        error => console.log("Error ::" + error));
  }

  onSuccessgetAllLatestAppli(response) {
    this.responseAppli = response;
    console.log('getAllLatestAppli is start');

    this.appIdArray = [];

    for (const i in response) {
      let appref = {};
      appref["label"] = response[i]['name'];
      appref["value"] = {}
      appref["value"]["label"] = response[i]['name'];
      appref["value"]["name"] = response[i]['name'];
      appref["value"]["uuid"] = response[i]['uuid'];

      this.appIdArray[i] = appref;
    }
    console.log('getAllLatestAppli is end');
    console.log(this.appIdArray);
  }
  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'group')
      .subscribe(
        response => {
          this.onSuccessgetOneByUuidAndVersion(response)
        },
        error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.group.active = 'Y';
    }
    else {
      this.group.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.group.published = 'Y';
    }
    else {
      this.group.published = 'N';
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

  submitGroup() {
    this.isSubmitEnable = true;
    let groupJson = {};
    groupJson["uuid"] = this.group.uuid;
    groupJson["name"] = this.group.name;
    // //let tagArray=[];
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    // // if(this.tags.length > 0){
    // //   for(let counttag=0;counttag < this.tags.length;counttag++){
    // //     tagArray[counttag]=this.tags[counttag]["value"];
    // //   }
    // // }
    // groupJson["tags"] = tagstemp;
    var tagArray = [];
    if (this.group.tags != null) {
      for (var counttag = 0; counttag < this.group.tags.length; counttag++) {
        tagArray[counttag] = this.group.tags[counttag].value;

      }
    }
    groupJson['tags'] = tagArray
    groupJson["desc"] = this.group.desc;
    let roleIdObj = {};
    let roleRef = {};
    roleRef["uuid"] = this.roleId.uuid;
    roleRef["type"] = "role";
    roleRef["name"] = this.roleId.name;
    roleIdObj["ref"] = roleRef;
    groupJson["roleId"] = roleIdObj;
    let appId1 = {};
    let appRef = {};
    appRef["uuid"] = this.appId.uuid;
    appRef["type"] = "role";
    appRef["name"] = this.appId.name;
    appId1["ref"] = appRef;

    groupJson["appId"] = appId1;

    groupJson["active"] = this.group.active == true ? 'Y' : "N"
    groupJson["published"] = this.group.published == true ? 'Y' : "N";
    groupJson["locked"] = this.group.locked == true ? 'Y' : "N";


    console.log(JSON.stringify(groupJson));
    this._commonService.submit("group", groupJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Group Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/group']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/group', uuid, version, 'false']);


  }
  showview(uuid, version) {
    this.router.navigate(['app/admin/group', uuid, version, 'true']);

  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

}
