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

export class DataReconService {

  constructor(@Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }

  getDataQualExecByDataqual(uuid: Number): Observable<any[]> {
    let url = '/dataQual/getDataQualExecByDataqual?action=view&dataQualUUID=' + uuid;
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
  getFunctionByCategory(type) {
    let url = "metadata/getFunctionByCategory?action=view&type=" + type
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
        let reconJson = {};
        reconJson["recondata"] = response
        if (response["sourceFilter"] != null) {
          let filterInfoArray = [];
          for (let k = 0; k < response["sourceFilter"].length; k++) {
            let filterInfo = {};
            filterInfo["logicalOperator"] = response["sourceFilter"][k].logicalOperator
            filterInfo["lhsType"] = response["sourceFilter"][k].operand[0].ref.type;
            filterInfo["operator"] = response["sourceFilter"][k].operator;
            filterInfo["rhsType"] = response["sourceFilter"][k].operand[1].ref.type;

            if (response["sourceFilter"][k].operand[0].ref.type == 'formula') {
              let lhsAttri1 = {}
              lhsAttri1["uuid"] = response["sourceFilter"][k].operand[0].ref.uuid;
              lhsAttri1["label"] = response["sourceFilter"][k].operand[0].ref.name;
              filterInfo["lhsAttribute"] = lhsAttri1;
            }
            else if (response["sourceFilter"][k].operand[0].ref.type == 'datapod') {
              let lhsAttri = {}
              lhsAttri["uuid"] = response["sourceFilter"][k].operand[0].ref.uuid;
              lhsAttri["label"] = response["sourceFilter"][k].operand[0].ref.name + "." + response["sourceFilter"][k].operand[0].attributeName;
              lhsAttri["attributeId"] = response["sourceFilter"][k].operand[0].attributeId;
              filterInfo["lhsAttribute"] = lhsAttri;
            }
            else if (response["sourceFilter"][k].operand[0].ref.type == 'attribute') {
              filterInfo["lhsType"] = 'attribute';
              filterInfo["lhsAttribute"] = response["sourceFilter"][k].operand[0].value;
            }

            else if (response["sourceFilter"][k].operand[0].ref.type == 'simple') {
              let stringValue = response["sourceFilter"][k].operand[0].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfo["lhsType"] = 'integer';
              } else {
                filterInfo["lhsType"] = 'string';
              }
              filterInfo["lhsAttribute"] = response["sourceFilter"][k].operand[0].value;
            }

            if (response["sourceFilter"][k].operand[1].ref.type == 'formula') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["sourceFilter"][k].operand[1].ref.uuid;
              rhsAttri["label"] = response["sourceFilter"][k].operand[1].ref.name;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'function') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["sourceFilter"][k].operand[1].ref.uuid;
              rhsAttri["label"] = response["sourceFilter"][k].operand[1].ref.name;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'paramlist') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["sourceFilter"][k].operand[1].ref.uuid;
              rhsAttri["attributeId"] = response["sourceFilter"][k].operand[1].attributeId;
              rhsAttri["label"] = "app." + response["sourceFilter"][k].operand[1].attributeName;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'dataset') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["sourceFilter"][k].operand[1].ref.uuid;
              rhsAttri["attributeId"] = response["sourceFilter"][k].operand[1].attributeId;
              rhsAttri["label"] = response["sourceFilter"][k].operand[1].attributeName;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'datapod') {
              let rhsAttri1 = {}
              rhsAttri1["uuid"] = response["sourceFilter"][k].operand[1].ref.uuid;
              rhsAttri1["label"] = response["sourceFilter"][k].operand[1].ref.name + "." + response["sourceFilter"][k].operand[1].attributeName;
              rhsAttri1["attributeId"] = response["sourceFilter"][k].operand[1].attributeId;
              filterInfo["rhsAttribute"] = rhsAttri1;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'attribute') {
              filterInfo["rhsType"] = 'datapod';
              filterInfo["rhsAttribute"] = response["sourceFilter"][k].operand[1].value;
            }
            else if (response["sourceFilter"][k].operand[1].ref.type == 'simple') {
              let stringValue = response["sourceFilter"][k].operand[1].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfo["rhsType"] = 'integer';
              } else {
                filterInfo["rhsType"] = 'string';
              }
              filterInfo["rhsAttribute"] = response["sourceFilter"][k].operand[1].value;

              let result2 = stringValue.includes("and")
              if (result2 == true) {
                filterInfo["rhsType"] = 'integer';

                let betweenValArray = []
                betweenValArray = stringValue.split("and");
                filterInfo["rhsAttribute1"] = betweenValArray[0];
                filterInfo["rhsAttribute2"] = betweenValArray[1];
              }
            }
            filterInfoArray.push(filterInfo);
            console.log(filterInfoArray);
            reconJson["filterTableArray"] = filterInfoArray;
          }
        }
        else {
          reconJson["filterTableArray"] = null;
        }

        if (response["targetFilter"] != null) {
          let filterInfoArray = [];
          for (let k = 0; k < response["targetFilter"].length; k++) {
            let filterInfo = {};
            filterInfo["logicalOperator"] = response["targetFilter"][k].logicalOperator
            filterInfo["lhsType"] = response["targetFilter"][k].operand[0].ref.type;
            filterInfo["operator"] = response["targetFilter"][k].operator;
            filterInfo["rhsType"] = response["targetFilter"][k].operand[1].ref.type;

            if (response["targetFilter"][k].operand[0].ref.type == 'formula') {
              let lhsAttri1 = {}
              lhsAttri1["uuid"] = response["targetFilter"][k].operand[0].ref.uuid;
              lhsAttri1["label"] = response["targetFilter"][k].operand[0].ref.name;
              filterInfo["lhsAttribute"] = lhsAttri1;
            }
            else if (response["targetFilter"][k].operand[0].ref.type == 'datapod') {
              let lhsAttri = {}
              lhsAttri["uuid"] = response["targetFilter"][k].operand[0].ref.uuid;
              lhsAttri["label"] = response["targetFilter"][k].operand[0].ref.name + "." + response["targetFilter"][k].operand[0].attributeName;
              lhsAttri["attributeId"] = response["targetFilter"][k].operand[0].attributeId;
              filterInfo["lhsAttribute"] = lhsAttri;
            }
            else if (response["targetFilter"][k].operand[0].ref.type == 'attribute') {
              filterInfo["lhsType"] = 'attribute';
              filterInfo["lhsAttribute"] = response["targetFilter"][k].operand[0].value;
            }

            else if (response["targetFilter"][k].operand[0].ref.type == 'simple') {
              let stringValue = response["targetFilter"][k].operand[0].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfo["lhsType"] = 'integer';
              } else {
                filterInfo["lhsType"] = 'string';
              }
              filterInfo["lhsAttribute"] = response["targetFilter"][k].operand[0].value;
            }

            if (response["targetFilter"][k].operand[1].ref.type == 'formula') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["targetFilter"][k].operand[1].ref.uuid;
              rhsAttri["label"] = response["targetFilter"][k].operand[1].ref.name;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'function') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["targetFilter"][k].operand[1].ref.uuid;
              rhsAttri["label"] = response["targetFilter"][k].operand[1].ref.name;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'paramlist') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["targetFilter"][k].operand[1].ref.uuid;
              rhsAttri["attributeId"] = response["targetFilter"][k].operand[1].attributeId;
              rhsAttri["label"] = "app." + response["targetFilter"][k].operand[1].attributeName;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'dataset') {
              let rhsAttri = {}
              rhsAttri["uuid"] = response["targetFilter"][k].operand[1].ref.uuid;
              rhsAttri["attributeId"] = response["targetFilter"][k].operand[1].attributeId;
              rhsAttri["label"] = response["targetFilter"][k].operand[1].attributeName;
              filterInfo["rhsAttribute"] = rhsAttri;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'datapod') {
              let rhsAttri1 = {}
              rhsAttri1["uuid"] = response["targetFilter"][k].operand[1].ref.uuid;
              rhsAttri1["label"] = response["targetFilter"][k].operand[1].ref.name + "." + response["targetFilter"][k].operand[1].attributeName;
              rhsAttri1["attributeId"] = response["targetFilter"][k].operand[1].attributeId;
              filterInfo["rhsAttribute"] = rhsAttri1;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'attribute') {
              filterInfo["rhsType"] = 'datapod';
              filterInfo["rhsAttribute"] = response["targetFilter"][k].operand[1].value;
            }
            else if (response["targetFilter"][k].operand[1].ref.type == 'simple') {
              let stringValue = response["targetFilter"][k].operand[1].value;
              let onlyNumbers = /^[0-9]+$/;
              let result = onlyNumbers.test(stringValue);
              if (result == true) {
                filterInfo["rhsType"] = 'integer';
              } else {
                filterInfo["rhsType"] = 'string';
              }
              filterInfo["rhsAttribute"] = response["targetFilter"][k].operand[1].value;

              let result2 = stringValue.includes("and")
              if (result2 == true) {
                filterInfo["rhsType"] = 'integer';

                let betweenValArray = []
                betweenValArray = stringValue.split("and");
                filterInfo["rhsAttribute1"] = betweenValArray[0];
                filterInfo["rhsAttribute2"] = betweenValArray[1];
              }
            }
            filterInfoArray.push(filterInfo);
            console.log(filterInfoArray);
            reconJson["targetFilterTableArray"] = filterInfoArray;
          }
        }
        else {
          reconJson["targetFilterTableArray"] = null;
        }
        console.log(response)
        return <any>reconJson;
      })
  }

  getReconExecByRecon(uuid: number, startDate: string, endDate: string): Observable<any[]> {
    let url = '/recon/getReconExecByRecon?action=view&uuid=' + uuid + '&startDate=' + startDate + '&endDate=' + endDate;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getResults(uuid: Number, version: String, type: String, mode: String, requestId?: Number): Observable<any[]> {
    let url = '/recon/getResults?action=view&uuid=' + uuid + '&version=' + version + '&type=' + type + '&mode=' + mode + '&requestId=' + requestId;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }
}