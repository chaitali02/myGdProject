import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../../metadata/services/common.service';
import { Location } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import { MigrationAssistService } from '../../../metadata/services/migration-assist.services';

@Component({
  selector: 'app-migration-assist-export',
  templateUrl: './migration-assist-export.template.html'
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
  metaTypeArray: any[];
  metaType: any;
  dropdownSettingsMetaInfo: any;
  dropdownSettingsMetaType: any;
  metaInfo: any;
  includeDep: any;
  location: any;
  type: any;
  allMataList : any;
  constructor(private _location: Location, private config: AppConfig, private activatedRoute: ActivatedRoute, private router: Router, private _commonService: CommonService, private _migrationAssist: MigrationAssistService) {
    this.showExportData = true;
    this.exportData = {};
    this.exportData["active"] = true
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [{
      "caption": "Admin",
      "routeurl": "/app/list"
    },
    {
      "caption": "Migration Assist",
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
    this.dropdownSettingsMetaInfo = {
      singleSelection: false,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      enableSearchFilter: true,
      disabled: false
    };
    this.dropdownSettingsMetaType = {
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
        this.dropdownSettingsMetaInfo.disabled = "this.mode === true" ? true : false;      
      }
      else {
        this.getAll();
      }     
    })
    this.metaTypeArray = [];
    this.metaType = [];
    this.allMataList = []
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
      meta["id"] = response[i]['uuid'];
      meta["itemName"] = response[i]['name'];

      temp[i] = meta;
    }
    this.metaTypeArray = temp
  }

  getAllByMetaList() {

    this.type = []
    for (const i in this.metaType) {
      this.type[i] = this.metaType[i].itemName;
    }
    console.log(this.type)
    this._migrationAssist.getAllByMetaList(this.type)
      .subscribe(
      response => {
        this.onSuccessGetAllByMetaList(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessGetAllByMetaList(response) {
    console.log(JSON.stringify(response))
    this.allMataList = []
    var count = 0;
    for (var i = 0; i < this.type.length; i++) {
      var result = response[this.type[i]]
      for (var j = 0; j < result.length; j++) {
        var metaList = {};
        metaList["type"] = this.type[i];
        metaList["itemName"] = metaList["type"] + "-" + result[j].name;
        metaList["uuid"] = result[j].uuid
        metaList["id"] = result[j].uuid
        this.allMataList[count] = metaList;
        count = count + 1
      }
    }
  }
    getMetaInfo() { 
      if(this.metaType !== null)
      {this.getAllByMetaList();}
    }

    onItemSelect(item: any) {
      this.getAllByMetaList()
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
      this.breadcrumbDataFrom[3].caption = response.name;
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
      let metaInfoArray = [];

      for (const i in response.metaInfo) {
        let metaInfo = {}
        metaInfo["id"] = response.metaInfo[i].ref.uuid;
        metaInfo["itemName"] = response.metaInfo[i].ref.name;
        metaInfo["type"] = response.metaInfo[i].ref.type;
        metaInfoArray[i] = metaInfo;
      }
      this.metaInfo = metaInfoArray;
      console.log(JSON.stringify(this.metaInfo));
      this.location = response.location;
      this.includeDep = response.includeDep;
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
          refMetaInfo["type"] = this.metaInfo[c].type
          refMetaInfo["uuid"] = this.metaInfo[c].uuid
         // refMetaInfo["name"] = this.metaInfo[c].itemName
          refMetaInfo["version"] = this.metaInfo[c].version

          metaInfoObj["ref"] = refMetaInfo;
          metaInfoNew[c] = metaInfoObj
        }
      }
      exportJson["metaInfo"] = metaInfoNew;     
      exportJson["includeDep"] = this.exportData.includeDep;
      console.log(JSON.stringify(exportJson));
      this._migrationAssist.exportSubmit("export", exportJson).subscribe(
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
    this._location.back();
  }

  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/migration-assist/export',uuid,version, 'false']);    
  }

  showview(uuid, version) {
    this.router.navigate(['app/admin/migration-assist/export',uuid,version, 'true']);    
  }
}
