import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class DataQualityService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

  getDataQualExecByDataqual(uuid: Number): Observable<any[]> {
    let url = '/dataQual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getDataQualExecByDataqual1(uuid: Number, startDate: Date, endDate: Date): Observable<any[]> {
    let url = '/dataqual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getdqExecByDatapod(uuid: Number, startDate: Date, endDate: Date): Observable<any[]> {
    let url = '/dataqual/getdqExecByDatapod?action=view&datapodUUID=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getSummary(uuid: Number, version: String, type: String, mode: String): Observable<any[]> {
    let url = '/dataqual/getSummary?action=view&dataQualExecUUID=' + uuid + '&dataQualExecVersion=' + version + '&type=' + type + '&mode=' + mode;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getdqGroupExecBydqGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqGroupExecBydqGroup?action=view&dqGroupUUID=' + uuid + '&dqGroupVersion=' + version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  executeRule(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/execute?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getdqExecBydqGroupExec(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/getdqExecBydqGroupExec?action=view&dataQualGroupExecUuid=' + uuid + '&dataQualGroupExecVersion=' + version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  executeDQGroup(uuid: Number, version: String): Observable<any[]> {
    let url = '/dataqual/executeGroup?action=execute&uuid=' + uuid + '&version=' + version;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getNumRowsbyExec(uuid: Number, version: String, type: String): Observable<any[]> {
    let url = '/metadata/getNumRowsbyExec?action=view&execUuid=' + uuid + '&execVersion=' + version + '&type=' + type;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

  getOneByUuidAndVersion(uuid, version, type): Observable<any> {
    return this._commonService.getOneByUuidAndVersion(uuid, version, type)
      .map((response: Response) => {
        let dqJson = {};
        dqJson["dqdata"] = response;
        // var filterInfoArray=[];
        // if(response["filter"] !=null){
        //   for(var i=0;i<response["filter"].filterInfo.length;i++){
        //     let filterInfo={};
        //     //let lhsFilter={}
        //     filterInfo["logicalOperator"]=response["filter"].filterInfo[i].logicalOperator
        //     filterInfo["operator"]=response["filter"].filterInfo[i].operator;
        //     // lhsFilter["uuid"]=response["filter"].filterInfo[i].operand[0].ref.uuid;
        //     // lhsFilter["datapodname"]=response["filter"].filterInfo[i].operand[0].ref.name;
        //     // lhsFilter["name"]=response["filter"].filterInfo[i].operand[0].attributeName;
        //     // lhsFilter["attributeId"]=response["filter"].filterInfo[i].operand[0].attributeId;

        //     let  lhsFilter : AttributeHolder = new AttributeHolder();
        //     lhsFilter.label =response["filter"].filterInfo[i].operand[0].ref["name"]+"."+response["filter"].filterInfo[i].operand[0].attributeName;;
        //     lhsFilter.u_Id=response["filter"].filterInfo[i].operand[0].ref.uuid+"_"+response["filter"].filterInfo[i].operand[0].attributeId
        //     lhsFilter.uuid=response["filter"].filterInfo[i].operand[0].ref.uuid;
        //     lhsFilter.attrId=response["filter"].filterInfo[i].operand[0].attributeId;
        //     filterInfo["lhsFilter"]=lhsFilter;
        //     filterInfo["filtervalue"]=response["filter"].filterInfo[i].operand[1].value;
        //     filterInfoArray[i]=filterInfo
        //   }
        // }
        dqJson["filterInfo"] = response["filterInfoArray"];
        console.log(response)
        return <any>dqJson;
      })
  }
}