import { Component, Input, OnInit } from '@angular/core';
import { Version } from '../../shared/version';
import { SelectItem } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { DataIngestionService } from '../../metadata/services/dataIngestion.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-data-ingestion-rule-group',
  templateUrl: './data-ingestion-rule-group.component.html'
})
export class DataIngestionRuleGroupComponent implements OnInit {
  showGraph: boolean;
  isHomeEnable: boolean;
  isSubmit: string;
  msgs: any[];
  checkboxModelexecution: boolean;
  runInParallel: boolean;
  ruleInfoArray: any;
  dropdownSettingsRuleInfo: { singleSelection: boolean; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; disabled: boolean; };
  ruleInfo: any;
  tags: any[];
  createdBy: any;
  ingestGroupData: any;
  mode: any;
  version: any;
  id: any;
  uuid: any;
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];

  constructor(private _location: Location, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataInjectService: DataIngestionService) {
    this.isSubmit = "false"
    this.ingestGroupData = {};
    this.isHomeEnable = false;
    this.showGraph = false;
    this.ruleInfo = [];
    this.ruleInfoArray = [];
    this.breadcrumbDataFrom = [{
      "caption": "Data Ingestion",
      "routeurl": "/app/list/ingestgroup"
    },
    {
      "caption": "Rule",
      "routeurl": "/app/list/ingestgroup"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];

    this.dropdownSettingsRuleInfo = {
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
        this.getAllVersionByUuid();
        this.getOneByUuidAndVersion(this.id, this.version);
        this.getAllLatest();
      }
      else {
        this.getAllVersionByUuid();
        this.getAllLatest();
      }
    });
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
    this._commonService.getAllLatest('ingest')
      .subscribe(
      response => {
        this.OnSuccesgetgetAllLatest(response)
      },
      error => console.log("Error :: " + error));
  }

  OnSuccesgetgetAllLatest(response) {
    for (const i in response) {
      let obj = {};
      obj["itemName"] = response[i]['name'];
      obj["id"] = response[i]['uuid'];
      this.ruleInfoArray[i] = obj;
    }
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('ingestgroup', this.id)
      .subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response) },
      error => console.log("Error :: " + error));
  }

  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver = {};
      ver["label"] = response[i]['version'];
      ver["value"] = {};
      ver["value"]["label"] = response[i]['version'];
      ver["value"]["uuid"] = response[i]['uuid'];
      this.VersionList[i] = ver;
    }
  }

  onVersionChange() {
    this.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label);
  }

  getOneByUuidAndVersion(id, version) {
    this._dataInjectService.getOneByUuidAndVersion(id, version, "ingestgroup").subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error::", +error)
    )
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.ingestGroupData = response;
    this.breadcrumbDataFrom[2].caption = response.name;
    this.createdBy = response.createdBy.ref.name;
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion = version;
    if (response.tags != null) {
      let temp = []
      for (var i = 0; i < response.tags.length; i++) {
        var tag = {};
        tag['value'] = response.tags[i];
        tag['display'] = response.tags[i];
        temp[i] = tag
      }
      this.tags = temp;
    }
    this.ingestGroupData.published = response["published"] == 'Y' ? true : false;
    this.ingestGroupData.active = response["active"] == 'Y' ? true : false;
    let ruleInfo = [];
    for (const i in response.ruleInfo) {
      {
        let ruleInfotag = {};
        ruleInfotag["id"] = response.ruleInfo[i].ref.uuid;
        ruleInfotag["itemName"] = response.ruleInfo[i].ref.name;
        ruleInfo[i] = ruleInfotag;
      }
    }
    this.ruleInfo = ruleInfo;
    console.log(JSON.stringify(this.ruleInfo));
    this.runInParallel = response.inParallel == 'Y' ? true : false;;
  }

  submit() {
    this.isSubmit = "true";
    let ingestGroupJson = {};
    ingestGroupJson["uuid"] = this.ingestGroupData.uuid;
    ingestGroupJson["name"] = this.ingestGroupData.name;
    ingestGroupJson["desc"] = this.ingestGroupData.desc;
    var tagArray = [];
    if (this.tags != null) {
      for (var counttag = 0; counttag < this.tags.length; counttag++) {
        tagArray[counttag] = this.tags[counttag].value;
      }
    }
    ingestGroupJson['tags'] = tagArray
    ingestGroupJson["active"] = this.ingestGroupData.active == true ? 'Y' : "N"
    ingestGroupJson["published"] = this.ingestGroupData.published == true ? 'Y' : "N";
    let ruleInfo = [];
    for (const i in this.ruleInfo) {
      let ruleInfoObj = {}
      let ref = {};
      ref["uuid"] = this.ruleInfo[i]["id"];
      ref["type"] = "ingest";
      ruleInfoObj["ref"] = ref;
      ruleInfo[i] = ruleInfoObj
    }
    ingestGroupJson["ruleInfo"] = ruleInfo;

    ingestGroupJson["inParallel"] = this.runInParallel == true ? 'Y' : "N";

    console.log(JSON.stringify(ingestGroupJson));
    this._commonService.submit("ingestgroup", ingestGroupJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ' + error))
  }

  OnSuccessubmit(response) {
    if (this.checkboxModelexecution == true) {
      this._commonService.getOneById("ingestgroup", response).subscribe(
        response => { this.onSuccessgetOneById(response) },
        error => console.log('Error :: ' + error))
    }
    else {
      this.msgs = [];
      this.isSubmit = "true"
      this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Ingest Rule Group Saved Successfully' });
      setTimeout(() => { this.goBack() }, 1000);
    }
  }

  onSuccessgetOneById(response) {
    this._commonService.execute(response.uuid, response.version, "ingestgroup", "execute").subscribe(
      response => { this.onSuccessExecute(response) },
      error => this.showError(error))
  }

  onSuccessExecute(response) {
    this.msgs = [];
    this.isSubmit = "true"
    this.msgs.push({ severity: 'success', summary: 'Success Message', detail: 'Rule Group Saved and Submited Successfully' });
    setTimeout(() => { this.goBack() }, 1000);
  }

  showError(error) {
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
  }
  // showview(uuid, version) {
  //   this.router.navigate(['app/dataIngestion/ingestgroup', this.ingestGroupData.uuid, this.ingestGroupData.version, 'true']);
  // }
  clear() {
    this.ruleInfo = []
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  showDagGraph(uuid, version) {
    this.isHomeEnable = true;
    this.showGraph = true;
  }
}
