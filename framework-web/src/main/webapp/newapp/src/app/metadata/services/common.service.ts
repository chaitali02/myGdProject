
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { debug } from 'util';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from '../../shared/shared.service';

import { AttributeIO } from '../domainIO/domain.attributeIO';

@Injectable()

export class CommonService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getAppLatest(type: String, inputFlag: String): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type + '&inputFlag=' + inputFlag;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAll(type: String): Observable<any[]> {
    let url = 'common/getAll?type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getOneById(type: String, id: Number): Observable<any> {
    let url = '/common/getOneById?action=view&id=' + id + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllVersionByUuid(type: String, uuid: Number): Observable<any[]> {
    let url = '/common/getAllVersionByUuid?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getLatestByUuid(uuid: Number, type: String): Observable<any> {
    let url = '/common/getLatestByUuid?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getOneByUuidAndVersion(uuid: Number, version: String, type: any): Observable<any> {
    let url = '/common/getOneByUuidAndVersion?action=view&uuid=' + uuid + '&version=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  submit(type: any, data: any, upd_tag?): Observable<any[]> {
    let url
    if (upd_tag) {
      url = '/common/submit?action=edit&type=' + type + '&upd_tag=' + upd_tag;
    }
    else {
      url = '/common/submit?action=edit&type=' + type;
    }
    return this._sharedService.postCall(url, data)
      .pipe(
        map(response => { return <any>response.text() }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllLatest(type: String): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getDatapodByRelation(uuid: Number, type: Number): Observable<any[]> {
    let url = '/metadata/getDatapodByRelation?action=view&relationUuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAttributesByDatapod(type: String, uuid: Number): Observable<any[]> {
    let url = '/metadata/getAttributesByDatapod?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAttributesByDataset(type: String, uuid: Number): Observable<any[]> {
    let url = '/metadata/getAttributesByDataset?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRegistryByDatasource(uuid: any, status): Observable<any[]> {
    let url = '/metadata/getRegistryByDatasource?type=datasource&action=view&datasourceUuid=' + uuid + "&status=" + status;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllAttributeBySource(uuid: any, type: String) {

    let url: any
    if (type == "dataset") {
      url = '/metadata/getAttributesByDataset?action=view&uuid=' + uuid + '&type=' + type;
    }
    else if (type == "datapod") {
      url = 'metadata/getAttributesByDatapod?action=view&uuid=' + uuid + '&type=' + type;
    }
    else if (type == "relation") {
      url = '/metadata/getDatapodByRelation?action=view&relationUuid=' + uuid + '&type=datapod';
    }
    else {
      url = 'metadata/getAttributesByRule?action=view&uuid=' + uuid + '&type=' + type;
    }
    return this._sharedService.getCall(url)
      .pipe(
        map(response => {
          if (type == "relation") {
            return this.modifyRelation(response.json());
          }
          else {
            return <any[]>this.modifyResponse(response.json());
          }

        }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  modifyRelation(response) {
    var attributes = [];
    for (var j = 0; j < response.length; j++) {
      for (var i = 0; i < response[j].attributes.length; i++) {
        var attributedetail = {};
        attributedetail["uuid"] = response[j].uuid;
        attributedetail["datapodname"] = response[j].name;
        attributedetail["name"] = response[j].attributes[i].name;
        attributedetail["dname"] = response[j].name + "." + response[j].attributes[i].name;
        attributedetail["attributeId"] = response[j].attributes[i].attributeId;
        attributedetail["id"] = response[j].uuid + "_" + response[j].attributes[i].attributeId;
        attributes.push(attributedetail)
      }
    }
    // console.log("from commonService :" + attributes);
    return attributes;
  }
  modifyResponse(response) {
    // var attributes = [];
    // for (var j = 0; j < response.length; j++) {
    //   var attributedetail = {};
    //   attributedetail["uuid"] = response[j].ref.uuid;
    //   attributedetail["datapodname"] = response[j].ref.name;
    //   attributedetail["name"] = response[j].attrName;
    //   attributedetail["attributeId"] = response[j].attrId;
    //   attributedetail["dname"] = response[j].ref.name + "." + response[j].attrName;
    //   attributedetail["id"] = response[j].ref.uuid + "_" + response[j].attrId;
    //   attributedetail["attrType"] = response[j].attrType;
    //   attributes.push(attributedetail)

    // }
    // return attributes;

    let attributeIOArray = [new AttributeIO];
    for (var j = 0; j < response.length; j++) {
      let attributeIO = new AttributeIO();
      attributeIO.id = response[j].ref.uuid + "_" + response[j].attrId;
      attributeIO.uuid = response[j].ref.uuid;
      attributeIO.datapodname = response[j].ref.name;
      attributeIO.name = response[j].attrName;
      attributeIO.dname = response[j].ref.name + "." + response[j].attrName;
      attributeIO.attributeId = response[j].attrId;
      attributeIO.attrType = response[j].attrType;

      attributeIO.label = response[j].ref.name + "." + response[j].attrName;
      attributeIO.value = { label: "", value: "", u_Id: "", uuid: "", attrId: "" };
      attributeIO.value.label = response[j].ref.name + "." + response[j].attrName;
      attributeIO.value.value = response[j].ref.name;
      attributeIO.value.u_Id = response[j].ref.uuid + "_" +response[j].attrId;
      attributeIO.value.uuid = response[j].ref.uuid;
      attributeIO.value.attrId = response[j].attrId;
      attributeIOArray[j] = attributeIO;
    }
    return attributeIOArray;
  }

  getGraphResults(version: String, degree: Number, uuid: Number): Observable<any[]> {
    let url = '/graph/getTreeGraphResults?action=view&uuid=' + uuid + '&version=' + version + '&degree=' + degree;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getFormulaByType(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getFormulaByType?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getFormulaByType2(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getFormulaByType2?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getExpressionByType(uuid: Number, type: String): Observable<any[]> {
    let url = '/metadata/getExpressionByType?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllLatestFunction(type: any, inputFlag: any): Observable<any[]> {
    let url = '/common/getAllLatest?action=view&type=' + type + '&inputFlag=' + inputFlag;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getRuleExecByRule(uuid: Number): Observable<any[]> {
    let url = '/rule/getRuleExecByRule?action=view&ruleUuid=' + uuid;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getFunctionByCriteria(category: any, inputReq: any, type: any): Observable<any[]> {
    let url = '/metadata/getFunctionByCriteria?action=view&category=' + category + '&inputReq=' + inputReq + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getParamByApp(uuid: any, type: any): Observable<any[]> {
    let url = '/metadata/getParamByApp?action=view&uuid=' + uuid + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  getAllLatestParamListByTemplate(templateFlg: any, type: String, paramListType: any): Observable<any[]> {

    let url = 'common/getAllLatestParamListByTemplate?action=view&templateFlg=' + templateFlg + "&type=" + type + "&paramListType=" + paramListType;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  saveAs(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = '/common/saveAs?action=clone&uuid=' + uuid + '&version=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }




  executeWithParams(type, uuid, version, data): Observable<any[]> {
    let url
    if (type == 'train') {
      url = "/model/train/execute?uuid=" + uuid + "&version=" + version + '&action=view';
    } else if (type == 'simulate') {
      url = '/model/simulate/execute?uuid=' + uuid + '&version=' + version + '&action=view';
    }
    let data1 = data;
    return this._sharedService.postCall(url, data1)
      .pipe(map(response => {
        console.log(response);
        return <any>response["_body"];
      }),
        catchError(error => this.handleError<string>(error, "Network Error!")));

  }

  restart(type, uuid, version): Observable<any[]> {
    let url = '/ingest/restart?uuid=' + uuid + '&version=' + version + '&action=execute'+ '&type=' + type;
    let data1 = {};
    return this._sharedService.postCall(url, data1)
      .pipe(map(response => {
        console.log(response);
        return <any>response;
      }),
        catchError(error => this.handleError<string>(error, "Network Error!")));

  }

  getNumRowsbyExec(uuid, version, type): Observable<any> {
    let url = 'metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + "&execVersion=" + version + "&type=" + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json() }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getParamListChilds(uuid, version, type): Observable<any> {
    let url = "metadata/getParamListChilds?action=view&uuid=" + uuid + "&version=" + version + "&type=" + type
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }



  // /ingest/restart?action=execute&uuid=2b80b258-3459-450d-9e86-9b42a3bbd343&version=1553780312&type=ingestgroup






  execute(uuid, version, type, action): Observable<any> {
    let url;
    if (type == "rulegroup") {
      url = '/rule/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profile") {
      url = '/profile/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "profilegroup") {
      url = '/profile/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dq") {
      url = '/dataqual/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dqgroup") {
      url = '/dataqual/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "dag") {
      url = '/dag/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "train") {
      url = "model/train/execute?uuid=" + uuid + "&version=" + version + '&action=view';
    }
    if (type == "predict") {
      url = '/model/predict/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "ingest") {
      url = '/ingest/execute?action=' + action + '&uuid=' + uuid + '&version=' + version + '&type=' + type;
    }
    if (type == "ingestgroup") {
      url = '/ingest/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version;
    }
    if (type == "batch") {
      url = '/batch/execute?action=' + action + '&uuid=' + uuid + '&version=' + version;
    }
    if (type == "recongroup") {
      url = '/recon/executeGroup?action=' + action + '&uuid=' + uuid + '&version=' + version;
    }
    if (type == "recon") {
      url = '/recon/execute?action=' + action + '&uuid=' + uuid + '&version=' + version;
    }
    let body = null
    return this._sharedService.postCall(url, body)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
  getParamByParamList(uuid: Number, type: String): Observable<any[]> {
    let url = "metadata/getParamByParamList?action=view&uuid=" + uuid + "&type=" + type;
    return this._sharedService.getCall(url)
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}
