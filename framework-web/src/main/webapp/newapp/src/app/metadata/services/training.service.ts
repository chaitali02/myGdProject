import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http, Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import { CommonService } from './common.service';
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
import { BindingFlags } from '@angular/core/src/view';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class TrainingService {

  constructor( @Inject(Http) private http: Http, private _sharedService: SharedService, private _commonService: CommonService) { }
  getAllModelByType(flag: string, type: String): Observable<any[]> {
    let url = "model/getAllModelByType?action=view&customFlag=" + flag + "&type=" + type + "&modelType=algorithm";
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  getParamSetByAlgorithm(uuid: Number, version: String) {
    let url = "metadata/getParamSetByAlgorithm?action=view&algorithmUuid=" + uuid + "&algorithmVersion=null" ;
    return this._sharedService.getCall(url)
      .map((response: Response) => {
        return <any[]>response.json();
      })
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
  }




  executeWithParams(type, uuid, version, action){ 
    let url
    if(type=='train'){
      url = "model/train/execute?uuid=" + uuid + "&version=" + version+ '&action=view';
      let body=null
      return this._sharedService.postCall(url,body)
      .map((response: Response) => {
        return <any[]>response.json();
    })
     .catch(this.handleError);
    }
  }
  

}