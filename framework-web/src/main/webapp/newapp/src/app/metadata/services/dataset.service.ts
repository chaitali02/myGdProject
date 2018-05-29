import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';


@Injectable()
export class DatasetService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getFormulaByType2(uuid:Number,type:any): Observable<any[]> {
    let url ='/metadata/getFormulaByType2?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }

  getExpressionByType2(uuid:Number,type:Number): Observable<any[]> {
    let url ='/metadata/getExpressionByType2?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }

  getDatasetSample(uuid:Number,version:String): Observable<any[]> {
    let url ='/dataset/getDatasetSample?action=view&datasetUUID='+uuid+'&datasetVersion='+version+'&row=100';
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }
  getExpressionByType(uuid:Number,type:String): Observable<any[]> {
    let url ='/metadata/getExpressionByType?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
})
   .catch(this.handleError);
}
getAllLatestFunction(type:any,inputFlag:any): Observable<any[]> {
    let url ='/common/getAllLatest?action=view&type='+type+'&inputFlag='+inputFlag;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
})
   .catch(this.handleError);
}
  private handleError(error: Response) {
    return Observable.throw(error.statusText);
}
}
