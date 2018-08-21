import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';


@Component({
  selector: 'app-privilege',
  templateUrl: './privilege.template.html',
  styleUrls: ['./privilege.component.css']
})
export class PrivilegeComponent implements OnInit {

  breadcrumbDataFrom: any;
  showPrivilege: any;
  privilege: any;
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
  privType: any;
  meta: any;
  typeSelect: { 'value': string; 'label': string; }[];
  metaOptions: any;
  msgs: any;
  isSubmitEnable: any;
  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showPrivilege = true;
    this.privilege = {};
    this.privilege["active"] = true
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [{
      "caption": "admin",
      "routeurl": "/app/list/privilege"
    },
    {
      "caption": "Privilege",
      "routeurl": "/app/list/privilege"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.typeSelect = [{ 'value': 'Add', 'label': 'Add' },
    { 'value': 'View', 'label': 'View' },
    { 'value': 'Edit', 'label': 'Edit' },
    { 'value': 'Delete', 'label': 'Delete' },
    { 'value': 'Execute', 'label': 'Execute' },
    { 'value': 'Clone', 'label': 'Clone' },
    { 'value': 'Export', 'label': 'Export' },
    { 'value': 'Restore', 'label': 'Restore' },
    { 'value': 'Publish', 'label': 'Publish' },
    { 'value': 'Unpublish', 'label': 'Unpublish' }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();

      } this.changeMeta();
    })
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'privilege')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('privilege', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.privilege = response;
    this.uuid = response.uuid;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version
    var tags = [];
    if (response.tags != null) {
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        tags[i] = tag

      }//End For
      this.privilege.tags = tags;
    }//End If
    this.createdBy = response.createdBy.ref.name;
    this.privilege.published = response["published"] == 'Y' ? true : false
    this.privilege.active = response["active"] == 'Y' ? true : false
    this.version = response['version'];
    this.meta = response.metaId.ref.name;
    this.privType = response.privType;

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

  changeMeta() {
    this._commonService.getAll('meta')
      .subscribe(
      response => {
        this.OnSuccesgetAll(response)
      },
      error => console.log("Error :: " + error));
  }

  OnSuccesgetAll(response) {
    this.metaOptions = [];

    for (const i in response) {
      let meta = {};
      meta["label"] = response[i]['name'];
      meta["value"] = {}
      meta["value"]["uuid"] = response[i]['uuid'];

      meta["value"]["name"] = response[i]['name'];
      meta["value"]["label"] = response[i]['label'];
      this.metaOptions[i] = meta;
    }
  }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'privilege')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  onChangeActive(event) {
    if (event === true) {
      this.privilege.active = 'Y';
    }
    else {
      this.privilege.active = 'N';
    }
  }

  onChangePublished(event) {
    if (event === true) {
      this.privilege.published = 'Y';
    }
    else {
      this.privilege.published = 'N';
    }
  }

  submitPrivilege() {
    let privilegeJson = {}
    privilegeJson["name"] = this.privilege.name;
    privilegeJson["uuid"] = this.privilege.uuid;
    // const tagstemp = [];
    // for (const t in this.tags) {
    //   tagstemp.push(this.tags[t]["value"]);
    // }
    // privilegeJson["tags"] = tagstemp


    var tagArray = [];
    if (this.privilege.tags != null) {
      for (var counttag = 0; counttag < this.privilege.tags.length; counttag++) {
        tagArray[counttag] = this.privilege.tags[counttag].value;

      }
    }
    privilegeJson['tags'] = tagArray
    privilegeJson["desc"] = this.privilege.desc;
    privilegeJson["active"] = this.privilege.active == true ? 'Y' : "N"
    privilegeJson["published"] = this.privilege.published == true ? 'Y' : "N"
    privilegeJson["privType"] = this.privType;

    let metaId = {};
    let refMetaId = {};
    metaId["uuid"] = this.meta.uuid
    metaId["type"] = "meta"
    refMetaId["ref"] = metaId
    privilegeJson["metaId"] = refMetaId;
    this.isSubmitEnable = true;
    console.log(JSON.stringify(privilegeJson));
    this._commonService.submit("privilege", privilegeJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccessubmit(response) {
    this.isSubmitEnable = true;
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Privilege Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
    console.log('final response is' + response);
  }

  public goBack() {
    //this._location.back();
    this.router.navigate(['app/list/privilege']);

  }
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/privilege', uuid, version, 'false']);

  }
  showview(uuid, version) {
    this.router.navigate(['app/admin/privilege', uuid, version, 'true']);
  }

}