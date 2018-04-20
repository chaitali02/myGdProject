//import { version } from './../../../../../bower_components/moment/moment.d';
import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Observable } from 'rxjs/Observable';
@Component({
  selector: 'app-migration-assist-export',
  templateUrl: './migration-assist-export.component.html',
  styleUrls: ['./migration-assist-export.component.css']
})
export class MigrationAssistExportComponent implements OnInit {

  breadcrumbDataFrom: any;
  showExportData: any;
  exportData: any;
  versions: any[];
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
  msgs: any;
  isSubmitEnable: any;
  metaTypes: any[];
  metaType: any
  metaInfoArray: any[];
  dropdownSettingsMeta: any;
  metaInfo: any;
  includeDep: any;
  location: any;

  constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showExportData = true;
    this.exportData = {};
    this.exportData["active"] = true
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list/application"
    },
    {
      "caption": "Application",
      "routeurl": "/app/list/migration-assist"
    },
    {
      "caption": "Export",
      "routeurl": "/app/list/migration-assist/export"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ]
    this.dropdownSettingsMeta = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disable: true
    };
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];

      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.dropdownSettingsMeta.disabled = "this.mode !== undefined" ? false : true
        // this.getAllVersionByUuid();
      }
      else {
        this.getAll();
      }
    })
    this.metaType = {};
    this.metaInfoArray = null;
  }

  getAll() {
    this._commonService.getAll('meta')
      .subscribe(
      response => { this.onSuccessGetAll(response) },
      error => console.log("Error :: " + error)
      )
  }

  onSuccessGetAll(response) {
    var temp = []
    for (const i in response) {
      let meta = {};
      meta["label"] = response[i]['name'];
      meta["value"] = {};
      meta["value"]["label"] = response[i]['name'];
      meta["value"]["uuid"] = response[i]['uuid'];
      //allName["uuid"]=response[i]['uuid']
      temp[i] = meta;
    }
    this.metaTypes = temp
    this.getAllLatest();
  }

  getAllLatest() {
    this._commonService.getAllLatest(this.metaType.label)
      .subscribe(
      response => {
        this.onSuccessGetAllLatest(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessGetAllLatest(response) {
    this.metaInfoArray = [];

    for (const i in response) {
      let metaref = {};
      metaref["id"] = response[i]['uuid'];
      metaref["itemName"] = response[i]['name'];
      metaref["version"] = response[i]['version'];
     // metaref["type"] = response[i]['type'];
      this.metaInfoArray[i] = metaref;
    }
  }

  onItemSelect(item: any) {
    //console.log(item);
    // console.log(this.selectedItems);
  }
  OnItemDeSelect(item: any) {
    // console.log(item);
    // console.log(this.selectedItems);
  }
  onSelectAll(items: any) {
    // console.log(items);
  }
  onDeSelectAll(items: any) {
    // console.log(items);
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'export')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('export', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.breadcrumbDataFrom[2].caption = response.name;
    this.exportData = response;
    this.createdBy = response.createdBy.ref.name;
    this.uuid = response.uuid;
    this.version = response['version'];
    // this.published = response['published'];
    // if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if (this.active === 'Y') { this.active = true; } else { this.active = false; }

    //this.application.published=response["published"] == 'Y' ? true : false
    this.exportData.active = response["active"] == 'Y' ? true : false
    let metaInfoArray1 = [];

    for (const i in response.metaInfo) {
      let metaInfo = {}
      metaInfo["id"] = response.metaInfo[i].ref.uuid;
      metaInfo["itemName"] = response.metaInfo[i].ref.name;
      metaInfo["version"] = response.metaInfo[i].ref.version;
      metaInfo["type"] = response.metaInfo[i].ref.type;
      metaInfoArray1[i] = metaInfo;
    }
    this.metaInfo = metaInfoArray1;

    this.location = response.location;
    this.includeDep = response.includeDep;
    this.getAll();
  }

  OnSuccesgetAllVersionByUuid(response) {
    this.versions = [];
    for (const i in response) {
      let version = {};
      version["label"] = response[i]['version'];
      version["value"] = response[i]['version'];
      version["uuid"] = response[i]['uuid'];
      this.versions[i] = version;
    }
  }

  onChangeActive(event) {
    if (event === true) {
      this.exportData.active = 'Y';
    }
    else {
      this.exportData.active = 'N';
    }
  }

  onChangeCheckbox(event) {
    if (event === true) {
      this.exportData.includeDep = 'Y';
    }
    else {
      this.exportData.includeDep = 'N';
    }
  }

  submitExport() {
    let exportJson = {};

    // exportJson["uuid"]=this.exportData.uuid;
    exportJson["name"] = this.exportData.name;
    //let tagArray=[];
    const tagstemp = [];
    for (const t in this.tags) {
      tagstemp.push(this.tags[t]["value"]);
    }
    // if(this.tags.length > 0){
    //   for(let counttag=0;counttag < this.tags.length;counttag++){
    //     tagArray[counttag]=this.tags[counttag]["value"];
    //   }
    // }
    exportJson["tags"] = tagstemp;
    exportJson["desc"] = this.exportData.desc;

    let metaInfoNew = [];
    if (this.metaInfo != null) {
      for (const c in this.metaInfo) {
        let metaInfoObj = {};
        let refMetaInfo = {}
        refMetaInfo["type"] = this.metaType.label
        refMetaInfo["uuid"] = this.metaInfo[c].id
        refMetaInfo["name"] = this.metaInfo[c].itemName
        refMetaInfo["version"] = this.metaInfo[c].version

        metaInfoObj["ref"] = refMetaInfo;
        metaInfoNew[c] = metaInfoObj
      }
    }
    exportJson["metaInfo"] = metaInfoNew;

    exportJson["active"] = this.exportData.active == true ? 'Y' : "N"
    exportJson["includeDep"] = this.exportData.includeDep;
    console.log(JSON.stringify(exportJson));
    this._commonService.submit("export", exportJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error)
    )
  }


  OnSuccessubmit(response) {
    console.log(response)
    this.msgs = [];
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Export Submitted Successfully' });
    setTimeout(() => {
      this.goBack()
    }, 1000);
  }

  public goBack() {
    //  this._location.back();
    this.router.navigate(['app/admin/migration-assist']);
  }
}
