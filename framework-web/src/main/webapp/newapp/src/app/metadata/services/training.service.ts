import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
//import { CommonService } from './common.service';
//import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
//import { BindingFlags } from '@angular/core/src/view';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class TrainingService {

  constructor( @Inject(Http) private http: Http, private _sharedService: SharedService) { }
  getAllModelByType(flag: string, type: String): Observable<any[]> {
    let url = "model/getAllModelByType?action=view&customFlag=" + flag + "&type=" + type + "&modelType=algorithm";
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getParamSetByAlgorithm(uuid: Number, version: String, isHyperParam : any) {
    let url = "metadata/getParamSetByAlgorithm?action=view&algorithmUuid=" + uuid + "&algorithmVersion=null&isHyperParam=" + isHyperParam ;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getParamListByAlgorithm(uuid: any, version: any, type:any, isHyperParam:any) {
    let url = "metadata/getParamListByAlgorithm?action=view&uuid=" + uuid + "&version="+version+"&type="+type+"&isHyperParam="+isHyperParam ;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getParamByParamList(uuid: any, type:any) : Observable<any>{
    let url = "metadata/getParamByParamList?action=view&uuid=" + uuid + "&type="+type ;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  executeWithParams(type, uuid, version, data){ 
    let url
    if(type=='train'){
      url = "model/train/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      let body=null
      return this._sharedService.postCall(url,data)
      .map((response: Response) => {
        return <any[]>response.json();
    })
     .catch(this.handleError);
    }
  }
  
  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }

}