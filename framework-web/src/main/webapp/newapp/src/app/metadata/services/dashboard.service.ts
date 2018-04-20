import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import {CommonService} from './common.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class DashboardService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
  getVizpodByType(type:String,uuid:Number): Observable<any[]> {
    let url ='/metadata/getVizpodByType?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  private handleError(error: Response) {
    return Observable.throw(error.statusText);
}

getVizpodResult(uuid,version,action,data){
  let url="vizpod/getVizpodResults/"+uuid+"/"+version+"?action="+action+"&requestId=''"
  return this._sharedService.postCall(url,data)
  .map((response: Response) => {
    return <any[]>response.json();
})
// .catch(this.handleError);
}
getAttributeValues(uuid,attributeId){
  let url="datapod/getAttributeValues1?action=view&datapodUUID="+uuid+"&attributeId="+attributeId;
  return this._sharedService.getCall(url)
  .map((response: Response) => {
    return <any[]>response.json();
})
 //.catch(this.handleError);
}
getOneByUuidAndVersion(uuid,version,type):Observable<any> {
  return this._commonService.getOneByUuidAndVersion(uuid,version,type)
  .map((response: Response) => {
    let dashboadjson=response;
    for(let i=0;i< response["sectionInfo"].length;i++){
    let vizpodinfo={};
    vizpodinfo["label"]=response["sectionInfo"][i]["vizpodInfo"].name;
    vizpodinfo["uuid"]=response["sectionInfo"][i]["vizpodInfo"].uuid;
    vizpodinfo["type"]=response["sectionInfo"][i]["vizpodInfo"].type;
    vizpodinfo["name"]=response["sectionInfo"][i]["vizpodInfo"].name;
    dashboadjson["sectionInfo"][i]["vizpodInfo"]=vizpodinfo
   // console.log(dashboadjson)
    }
    return  <any>dashboadjson;
  })
}



}