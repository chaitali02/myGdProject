
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

import { SharedService } from './../../shared/shared.service';
import {CommonService} from './common.service';


@Injectable()

export class VizpodService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }
  getOneByUuidAndVersion(uuid,version,type):Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid,version,type)
    .pipe(
      map(response =>{
      let vizpodjson=response;
      let keyArray = [];
      for (let i = 0; i < response["keys"].length; i++) {
        let keyjson = {};
        keyjson["id"] = i;
        keyjson["itemName"] = response["keys"][i].ref.name+"."+response["keys"][i].attributeName;
        keyjson["name"] = response["keys"][i].ref.name
        keyjson["type"] = response["keys"][i].ref.type;
        keyjson["uuid"] = response["keys"][i].ref.uuid;
        keyjson["attrId"] = response["keys"][i].attributeId;
        keyjson["attrName"] = response["keys"][i].attributeName;
        keyArray[i] = keyjson;
      }
      vizpodjson["keys"] = keyArray;
      let groupArray = [];
      for (let i = 0; i < response["groups"].length; i++) {
        let groupjson = {};
        groupjson["id"] = i;
        groupjson["name"] = response["groups"][i].ref.name;
        groupjson["itemName"] = response["groups"][i].ref.name+"."+response["groups"][i].attributeName;;
        groupjson["type"] = response["groups"][i].ref.type;
        groupjson["uuid"] = response["groups"][i].ref.uuid;
        groupjson["attrId"] = response["groups"][i].attributeId;
        groupjson["attrName"] = response["groups"][i].attributeName;
        groupArray[i] = groupjson;
      }
      vizpodjson["groups"] = groupArray;
      let  valueArray = [];
      for (let i = 0; i < response["values"].length; i++) {
        let valuejson = {};
        valuejson["id"] = i;
       
        valuejson["type"] = response["values"][i].ref.type;
        valuejson["uuid"] = response["values"][i].ref.uuid;
        if(response["values"][i].ref.type == "datapod") {
          valuejson["itemName"] = response["values"][i].ref.name + "." + response["values"][i].attributeName;
          valuejson["attrId"] = response["values"][i].attributeId;
          valuejson["attrName"] = response["values"][i].attributeName;
          valuejson["name"] =  response["values"][i].attributeName;;
        } else {
          valuejson["itemName"] = response["values"][i].ref.name;
          valuejson["name"] = response["values"][i].ref.name;
        }
        valueArray[i] = valuejson;
      }
      vizpodjson["values"] = valueArray;
      return  <any>vizpodjson;
    }), catchError(error => this.handleError<string>(error, "Network Error!")));
  }

  submit(data :any,type :any,upd_tag :any){
    let url ='common/submit?action=edit&type='+type+"&upd_tag="+upd_tag;
    return this._sharedService.postCall(url,data)
    .pipe(
      map(response => { return <any[]>response.json(); }),
      catchError(error => this.handleError<string>(error, "Network Error!")));

  }

  
}