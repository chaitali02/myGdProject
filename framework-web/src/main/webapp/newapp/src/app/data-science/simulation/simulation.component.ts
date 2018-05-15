import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Location } from '@angular/common';
import { SelectItem } from 'primeng/primeng';

import { AppConfig } from '../../app.config';

import { CommonService } from '../../metadata/services/common.service';
import { SimulationService } from '../../metadata/services/simulation.service';

import { Version } from '../../metadata/domain/version';
import { DependsOn } from '../dependsOn';

@Component({
  selector: 'app-simulation',
  templateUrl: './simulation.template.html',
})
export class SimulationComponent implements OnInit {
  name: any;
  version: any;
  breadcrumbDataFrom: any;
  simulation: any;
  createdBy: any;
  tags: any;
  id: any;
  mode: any;
  uuid: any;
  active: any;
  published: any;
  continueCount: any;
  progressbarWidth: any;
  isSubmit: any
  selectedVersion: Version;
  VersionList: SelectItem[] = [];
  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _location: Location, private _modelService: SimulationService) {
    this.simulation = {};
    this.simulation["active"] = true;
    this.continueCount = 1;
    this.progressbarWidth = 25 * this.continueCount + "%";

    this.breadcrumbDataFrom = [{
      "caption": "Data Science",
      "routeurl": "/app/list/simulate"
    },
    {
      "caption": "Simulation",
      "routeurl": "/app/list/simulate"
    },
    {
      "caption": "",
      "routeurl": null
    }
    ];
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

  getOneByUuidAndVersion() {
    this._commonService.getOneByUuidAndVersion(this.id, this.version, 'simulate')
      .subscribe(
      response => {
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));

  }

  public get value(): string {
    return
  }
  getAllVersionByUuid() {
    {
      this._commonService.getAllVersionByUuid('simulate', this.id)
        .subscribe(
        response => {
          this.onSuccessgetAllVersionByUuid(response)
        },
        error => console.log("Error ::" + error)
        )
    }

  }

  countContinue() {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";

  }

  countBack = function () {
    this.continueCount = this.continueCount - 1;
    this.progressbarWidth = 25 * this.continueCount + "%";
  }


  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'paramset')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessgetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }


  onSuccessgetOneByUuidAndVersion(response) {
    this.simulation = response;
    this.uuid = response.uuid;
    const version: Version = new Version;
    version.label = response["version"];
    version.uuid = response["uuid"];
    this.selectedVersion = version;
    this.createdBy = response.createdBy.ref.name;
    this.simulation.active = response["active"] == 'Y' ? true : false;
    this.simulation.published = response["published"] == 'Y' ? true : false;
    this.breadcrumbDataFrom[2].caption = response.name;
    console.log('Data is' + response)
  }


  onSuccessgetAllVersionByUuid(response) {
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


}




