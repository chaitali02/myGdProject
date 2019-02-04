
import { Location } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AppConfig } from '../../app.config';
import { SelectItem, DataGridModule } from 'primeng/primeng';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import { Version } from '../../shared/version';
import { KnowledgeGraphComponent } from '../../shared/components/knowledgeGraph/knowledgeGraph.component';


@Component({
  selector: 'app-datasource',
  templateUrl: './datasource.template.html',
  styleUrls: ['./datasource.component.css']
})
export class DatasourceComponent implements OnInit {

  breadcrumbDataFrom: any;
  showDatasource: any;
  datasource: any;
  showGraph: boolean;
  isHomeEnable: boolean;
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

  constructor(private _location: Location, config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showDatasource = true;
    this.isHomeEnable = false;
    this.showGraph = false
    this.datasource = {};
    this.datasource["active"] = true
    this.isSubmitEnable = true;

    this.breadcrumbDataFrom = [{
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
    }
    ]

    this.datasourceOnType = ["File", "Hive"];
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if (this.mode !== undefined) {
        this.getOneByUuidAndVersion();
        this.getAllVersionByUuid();
      }
    })
  }

  showDagGraph(uuid,version){
    this.isHomeEnable = true;
    this.showGraph = true;
    setTimeout(() => {
      this.d_KnowledgeGraphComponent.getGraphData(this.id,this.version);
    }, 1000); 
  }

  showMainPage() {
    this.isHomeEnable = false
    // this._location.back();
    this.showGraph = false;
  }

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'datasource')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

  getAllVersionByUuid() {
    this._commonService.getAllVersionByUuid('datasource', this.id)
      .subscribe(
      response => {
        this.OnSuccesgetAllVersionByUuid(response)
      },
      error => console.log("Error :: " + error));
  }

  onSuccessgetOneByUuidAndVersion(response) {
    this.datasource = response;
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
      this.datasource.tags = tags;
    }//End If
    this.createdBy = response.createdBy.ref.name;
    this.version = response['version'];
    this.datasource.published = response["published"] == 'Y' ? true : false
    this.datasource.active = response["active"] == 'Y' ? true : false
    this.datasource.type = response["type"]
    this.breadcrumbDataFrom[2].caption = response.name;

    // this.published = response['published'];
    // if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    // this.active = response['active'];
    // if(this.active === 'Y') { this.active = true; } else { this.active = false; }

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
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'datasource')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }
  onChangeActive(event) {
    if (event === true) {
      this.datasource.active = 'Y';
    }
    else {
      this.datasource.active = 'N';
    }
  }
  onChangePublished(event) {
    if (event === true) {
      this.datasource.published = 'Y';
    }
    else {
      this.datasource.published = 'N';
    }
  }

  submitDatasource() {
    this.isSubmitEnable = true;
    let datasourceJson = {};
    datasourceJson["name"] = this.datasource.name;
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
    var tagArray = [];
    if (this.datasource.tags != null) {
      for (var counttag = 0; counttag < this.datasource.tags.length; counttag++) {
        tagArray[counttag] = this.datasource.tags[counttag].value;

      }
    }
    datasourceJson['tags'] = tagArray
    datasourceJson["desc"] = this.datasource.desc;
    datasourceJson["active"] = this.datasource.active == true ? 'Y' : "N"
    datasourceJson["published"] = this.datasource.published == true ? 'Y' : "N"
    let attributeInfo = [];
    datasourceJson["type"] = this.datasource.type;
    datasourceJson["access"] = this.datasource.access;
    datasourceJson["driver"] = this.datasource.driver;
    datasourceJson["host"] = this.datasource.host;
    datasourceJson["dbname"] = this.datasource.dbname;
    datasourceJson["port"] = this.datasource.port;
    datasourceJson["username"] = this.datasource.username;
    datasourceJson["password"] = this.datasource.password;
    datasourceJson["path"] = this.datasource.path;
    datasourceJson["sessionParameters"] = this.datasource.sessionParameters;
    console.log(JSON.stringify(datasourceJson))
    this._commonService.submit("datasource", datasourceJson).subscribe(
      response => { this.OnSuccessubmit(response) },
      error => console.log('Error :: ', +error)
    )
  }

  OnSuccessubmit(response) {
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
  enableEdit(uuid, version) {
    this.router.navigate(['app/admin/datasource', uuid, version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/admin/datasource', uuid, version, 'true']);
  }
}
