import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class AlgorithmService{
  sessionId :string;
  baseUrl:string;
  headers:Headers; 
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getExecuteModel(uuid:Number,version:String): Observable<any[]> {
    
    let url ='/model/train?action=execute&modelUUID='+uuid+'&modelVersion='+version;
    this.headers = new Headers({'sessionId':this.sessionId});
    this.headers.append('Accept','*/*')
    this.headers.append('content-Type',"application/json");
    // let url=this.baseUrl+apiUrl;
    // return this.http.put(url,JSON.stringify(data),{headers: this.headers});
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  submit(data :any,type :any,upd_tag :any){
    let url ='common/submit?action=edit&type='+type+"&upd_tag="+upd_tag;
    return this._sharedService.postCall(url,data)
    .map((response: Response) => {
      return <any>response.text();
})
   .catch(this.handleError);

  }


  

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
}
}