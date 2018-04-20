import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
import {CommonService} from './common.service';


@Injectable()
export class DatapodService{

    constructor(@Inject(Http) private http: Http,private _sharedService: SharedService,private _commonService:CommonService) { }
  
    private handleError(error: Response) {
    return Observable.throw(error.statusText);
    }

   
    getOneByUuidAndVersion(uuid,version,type):Observable<any> {
        return this._commonService.getOneByUuidAndVersion(uuid,version,type)
            .map((response: Response) => {
                let datapodjson={}
                datapodjson["datapoddata"]=response;
                var attributearray=[];
                for(var i=0;i<response["attributes"].length;i++){
                   var  attribute={};
                   attribute["attributeId"]=response["attributes"][i].attributeId;
                   attribute["name"]=response["attributes"][i].name;
                   attribute["isAttributeEnable"]=true;
                   attribute["selected"]=false;
                   attribute["dispName"]=response["attributes"][i].dispName;
                   attribute["type"]=response["attributes"][i].type.toLowerCase();
                   attribute["desc"]=response["attributes"][i].desc;
                   if(response["attributes"][i].key !="" && response["attributes"][i].key !=null){
                       attribute["key"]=true;
                   }
                   else{
                       attribute["key"]=false;
                   }
                   if(response["attributes"][i].partition =="Y"){
                        attribute["partition"]=true;
                    }else{
                        attribute["partition"]=false;
                    }
    
                    if(response["attributes"][i].active =="Y"){
                        attribute["active"]=true;
                    }else{
                        attribute["active"]=false;
                    }
                   attributearray[i]=attribute
                }
                //console.log(JSON.stringify(attributearray))
               datapodjson["attributes"]=attributearray
            return  <any>datapodjson;
        })
      }
    getDatasourceByType(type): Observable<any[]> {
        let url ='metadata/getDatasourceByType?action=view&type='+type;
        return this._sharedService.getCall(url)
        .map((response: Response) => {
          return <any[]>response.json();
      })
      .catch(this.handleError);
    }
  


}