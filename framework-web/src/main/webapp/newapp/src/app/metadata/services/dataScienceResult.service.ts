import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Response} from '@angular/http';
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()
export class DataScienceResultService{
  constructor(private _sharedService: SharedService) {}
  
  getModelByTrainExec(uuid:any, version:any): Observable<any[]>{
    let url ="model/getModelByTrainExec?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getTrainResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/train/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getPredictResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/predict/getResults?action=view&uuid=" + uuid + "&version=" + version;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getSimulateResults(uuid:any,version:any): Observable<any[]> {
    let url ="model/simulate/getResults?action=view&uuid=" + uuid + "&version=" + version;
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