import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { User } from '../../metadata/domain/domain.User';
import { Version } from '../../shared/version';

@Component({
  selector: 'app-privilege',
  templateUrl: './user.template.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  breadcrumbDataFrom: any;
  showUser: any;
  user: any;
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
  password: any;
  firstName: any;
  middleName: any;
  lastName: any;
  emailId: any;
  msgs: any;
  groupInfoArray: any;
  groupInfoTags: any;
  groupInfo: any;
  dropdownSettingsGroup: any;
  len: any;
  roleInfoArray: any;
  roleInfoTags: any;
  roleInfo: any;
  dropdownSettingsRole: any;
  getAllLatestGroupResponse: any;
  getAllLatestRoleResponse: any;
  isSubmitEnable: any;
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showUser = true;
    this.user = {};
    this.user["active"] = true
    this.isHomeEnable = false;
    this.showGraph = false;
    this.groupInfoTags = null
    this.roleInfoTags = null
    this.getAllLatestGroupResponse = null;
    this.getAllLatestRoleResponse = null;
    this.isSubmitEnable = true;
    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/user"
    },
    {
      "caption": "User",
      "routeurl": "/app/list/user"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dropdownSettingsGroup = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: false
    };

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

      this.getAllLatestGroup();
      this.getAllLatestRole();
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
        this.dropdownSettingsGroup.disabled = this.mode == "false" ? false : true
        this.dropdownSettingsRole.disabled = this.mode == "false" ? false : true
      }
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'user')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('user', this.id)
      .subscribe(
      response => {
        this.onSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllLatestGroup() {
    this._commonService.getAllLatest('group')
      .subscribe(
      response => {
        this.onSuccessgetAllLatestGroup(response)
      },
      error => console.log("Error ::" + error));
  }

  getAllLatestRole() {
    this._commonService.getAllLatest('role')
      .subscribe(
      response => {
        this.onSuccessgetAllLatestRole(response)
      },
      error => console.log("Error ::" + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    if (response != null) {
      this.user = response;
      this.uuid = response.uuid;
      this.createdBy = response.createdBy.ref.name;
      const version: Version = new Version();
      version.label = response['version'];
      version.uuid = response['uuid'];
      this.selectedVersion = version
      this.user.locked = response["locked"] == 'Y' ? true : false
      this.user.published = response["published"] == 'Y' ? true : false
      this.user.active = response["active"] == 'Y' ? true : false
      this.version = response['version'];
      // this.groupInfoTags = response.groupInfo;
      var tags = [];
      if (response.tags != null) {
        for (var i = 0; i < response.tags.length; i++) {
          var tag = {};
          tag['value'] = response.tags[i];
          tag['display'] = response.tags[i];
          tags[i] = tag

        }//End For
        this.user.tags = tags;
      }//End If
      let groupInfoNew = [];

      for (const i in response.groupInfo) {
        let grouptag = {};
        grouptag["id"] = response.groupInfo[i].ref.uuid;
        grouptag["itemName"] = response.groupInfo[i].ref.name;
        groupInfoNew[i] = grouptag;
      }
      this.groupInfoTags = groupInfoNew;

      // this.roleInfoTags = response.roleInfo;
      let roleInfoNew = [];

      this.roleInfoTags = [];
      for (const j in response.roleInfo) {
        let roletag = {};
        roletag["id"] = response.roleInfo[j].ref.uuid;
        roletag["itemName"] = response.roleInfo[j].ref.name;
        roleInfoNew[j] = roletag;
      }
      this.roleInfoTags = roleInfoNew;

      this.breadcrumbDataFrom[2].caption = response.name;

      console.log("getOneByUuidAndVersion executed");
    }
  }

  onSuccesgetAllVersionByUuid(response) {
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

  onSuccessgetAllLatestGroup(response) {
    this.getAllLatestGroupResponse = response;
    this.groupInfoArray = [];
    for (const i in response) {
      let groupref = {};
      groupref["id"] = response[i]['uuid'];
      groupref["itemName"] = response[i]['name'];
      groupref["version"] = response[i]['version'];

      this.groupInfoArray[i] = groupref;
    }
    for (const i in response) {
      console.log(JSON.stringify(this.groupInfoArray[i]));
    }
  }

  onSuccessgetAllLatestRole(response) {
    this.getAllLatestRoleResponse = response;
    this.roleInfoArray = [];

    for (const i in response) {
      let roleref = {};
      roleref["id"] = response[i]['uuid'];
      roleref["itemName"] = response[i]['name'];
      roleref["version"] = response[i]['version'];

      this.roleInfoArray[i] = roleref;
    }
    for (const i in response) {
      console.log(JSON.stringify(this.roleInfoArray[i]));
    }
    console.log("getAllLatest is executed");
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'user')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.user.active = 'Y';
    }
    else {
      this.user.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.user.published = 'Y';
    }
    else {
      this.user.published = 'N';
    }
  }

  onChangeLocked(event) {
    if (event === true) {
      this.user.locked = 'Y';
    }
    else {
      this.user.locked = 'N';
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

  submitUser() {
    this.isSubmitEnable = true;
    let userJson = {};
    userJson["uuid"] = this.user.uuid;
    userJson["name"] = this.user.name;
    //let tagArray=[];
    //  const tagstemp = [];
    //  for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    //  }
    // // if(this.tags.length > 0){
    // //   for(let counttag=0;counttag < this.tags.length;counttag++){
    // //     tagArray[counttag]=this.tags[counttag]["value"];
    // //   }
    // // }
    // userJson["tags"]=tagstemp;
    var tagArray = [];
    if (this.user.tags != null) {
      for (var counttag = 0; counttag < this.user.tags.length; counttag++) {
        tagArray[counttag] = this.user.tags[counttag].value;
      }
    }
    userJson['tags'] = tagArray

    userJson["desc"] = this.user.desc;

    let groupInfoArrayNew = [];
    if (this.groupInfoTags != null) {
      for (const c in this.groupInfoTags) {
        let groupInfoRef = {};
        let groupRef = {};
        groupInfoRef["uuid"] = this.groupInfoTags[c].id;
        groupInfoRef["type"] = "group";
        groupRef["ref"] = groupInfoRef;
        groupInfoArrayNew.push(groupRef);
      }
    }

    let roleInfoArrayNew = [];
    if (this.roleInfoTags != null) {
      for (const c in this.roleInfoTags) {
        let roleInfoRef = {};
        let roleRef = {};
        roleInfoRef["uuid"] = this.roleInfoTags[c].id;
        roleInfoRef["type"] = "role";
        roleRef["ref"] = roleInfoRef;
        roleInfoArrayNew.push(roleRef);
      }
    }
    userJson["groupInfo"] = groupInfoArrayNew;
    //  userJson["roleInfo"]=roleInfoArrayNew;
    userJson["active"] = this.user.active == true ? 'Y' : "N"
    userJson["published"] = this.user.published == true ? 'Y' : "N"
    userJson["locked"] = this.user.locked == true ? 'Y' : "N"
    userJson["password"] = this.user.password;
    userJson["firstName"] = this.user.firstName;
    userJson["middleName"] = this.user.middleName;
    userJson["lastName"] = this.user.lastName;
    userJson["emailId"] = this.user.emailId;

    console.log(JSON.stringify(userJson));
    this._commonService.submit("user", userJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'User Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    // this._location.back();
    this.router.navigate(['app/list/user']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/user', uuid, version, 'false']);
    this.dropdownSettingsGroup = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: true
    };

    this.dropdownSettingsRole = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: true
    };
  }

  // showview(uuid, version) {
  //   this.router.navigate(['app/admin/user', uuid, version, 'true']);
  //   this.dropdownSettingsGroup = {
  //     singleSelection: false,
  //     selectAllText: 'Select All',
  //     unSelectAllText: 'UnSelect All',
  //     enableSearchFilter: true,
  //     disabled: false
  //   };

  //   this.dropdownSettingsRole = {
  //     singleSelection: false,
  //     selectAllText: 'Select All',
  //     unSelectAllText: 'UnSelect All',
  //     enableSearchFilter: true,
  //     disabled: false
  //   };
  // }
  showMainPage() {
    this.isHomeEnable = false;
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}