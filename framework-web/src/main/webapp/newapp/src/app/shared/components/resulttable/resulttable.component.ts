
import { Component, Input, OnInit } from '@angular/core';
import { Router, Event as RouterEvent, ActivatedRoute, Params, NavigationEnd } from '@angular/router';

import { AppMetadata } from '../../../app.metadata';
import { jointjsGroupService } from '../../components/jointjsgroup/joinjsgroup.service'
import * as MetaTypeEnum from '../../../metadata/enums/metaType';
@Component({
  selector: 'app-resulttable',
  templateUrl: './tablerender.template.html',
  styleUrls: []
})
export class TableRenderComponent {
  modeOfExec: any;
  type: any;
  version: any;
  uuid: any;
  IsTableShow: boolean;
  IsError: boolean;
  columnOptions: any[];
  cols: any[];
  colsdata: any;
  @Input()
  tableParms: any;

  constructor(private activatedRoute: ActivatedRoute, private router: Router, public metaconfig: AppMetadata, private _jointjsGroupService: jointjsGroupService) {
    console.log(this.tableParms)
    //this.renderTable();
    this.cols = null
    this.IsError = false;
    this.IsTableShow = false;
  }
  ngOnInit() {

  }

  renderTable(params) {
    this.IsTableShow = false;
    let type;
    switch (params.type) {
      case 'dq':
        type = MetaTypeEnum.MetaType.DQ;
        break;
      case 'dqgroup':
        type = MetaTypeEnum.MetaType.DATAQUAL;
        break;
      case 'profile':
        type = MetaTypeEnum.MetaType.PROFILE;
        break;
      case 'rule':
        type = MetaTypeEnum.MetaType.RULE;
        break;
      case 'recon':
        type = MetaTypeEnum.MetaType.RECON;
        break;
      case 'map':
        type = MetaTypeEnum.MetaType.MAP;
        break;
    }

    this._jointjsGroupService.getNumRows(params.uuid, params.version, type + 'exec')
      .subscribe(
        response => {
          this.modeOfExec = response["runMode"]
          this.results(type, params.uuid, params.version, this.modeOfExec)
        })
  }
  results(type, uuid, version, mode) {
    if (type == MetaTypeEnum.MetaType.DQ) {
      type = MetaTypeEnum.MetaType.DATAQUAL;
    }
    this._jointjsGroupService.getResults(type, uuid, version, mode)
      .subscribe(
        response => {
          this.IsTableShow = true;
          this.colsdata = response
          let columns = [];
          console.log(response)
          if (response.length && response.length > 0) {
            Object.keys(response[0]).forEach(val => {
              if (val != "rownum") {
                let width = ((val.split("").length * 9) + 20) + "px"
                columns.push({ "field": val, "header": val, colwidth: width });
              }
            });
          }

          this.cols = columns
          this.columnOptions = [];
          for (let i = 0; i < this.cols.length; i++) {
            this.columnOptions.push({ label: this.cols[i].header, value: this.cols[i] });
          }
        },
        error => {
          this.IsTableShow = true;
          console.log("Error :: " + error)
          this.IsError = true;
        }
      );

    this.uuid = uuid;
    this.version = version;
    this.type = type;
  }
}
