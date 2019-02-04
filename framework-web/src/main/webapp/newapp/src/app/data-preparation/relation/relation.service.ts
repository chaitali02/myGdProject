import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class RelationService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  getAllAttributeBySource(uuid:any,type:String){
    
      let url:any
      if(type == "dataset"){
         url ='/metadata/getAttributesByDataset?action=view&uuid='+uuid+'&type='+type;
      }
      else if(type == "datapod"){
         url ='/metadata/getAttributesByDatapod?action=view&uuid='+uuid+'&type='+type;
      }
      else if(type == "relation"){
        url ='/metadata/getDatapodByRelation?action=view&relationUuid='+uuid+'&type=datapod';
      }
      else{
        url ='metadata/getAttributesByRule?action=view&uuid='+uuid+'&type='+type;
      }
      return this._sharedService.getCall(url)
      .map((response: Response) => {
        if(type =="relation"){
          return this.modifyRelation(response.json());
        }
        else{
          return this.modifyResponse(response.json());
        }
        
    })
     .catch(this.handleError);
    }
    modifyRelation(response){
        var allattributes=[];
        var attributes=[];
            for(var j=0;j<response.length;j++){
                 var attr=[]
               for(var i=0;i<response[j].attributes.length;i++){
                 var attributedetail={};
                //  attributedetail["uuid"]=response[j].uuid;
                //  attributedetail["datapodname"]=response[j].name;
                //  attributedetail["name"]=response[j].attributes[i].name;
                //  attributedetail["attributeId"]=response[j].attributes[i].attributeId;
                 attributedetail["label"]=response[j].name+"."+response[j].attributes[i].name;
                 attributedetail["value"]={};
                 attributedetail["value"]["label"]=response[j].name+"."+response[j].attributes[i].name;      
                 attributedetail["value"]["uuid"]=response[j].uuid+"_"+response[j].attributes[i].attributeId;
                //  attributedetail["value"]["uuid"]=response[j].uuid;
                //  attributedetail["value"]["datapodname"]=response[j].name;
                //  attributedetail["value"]["name"]=response[j].attributes[i].name;
                //  attributedetail["value"]["attributeId"]=response[j].attributes[i].attributeId;
                 allattributes.push(attributedetail)
                 attr.push(attributedetail)
                 }

               attributes[j]=attr;
            }
            var relationattribute={}
            relationattribute["allattributes"]=allattributes;
            relationattribute["attributes"]=attributes;
            return relationattribute;
        }
    modifyResponse(response){
      var allattributes=[];
      var attributes=[];
      for(var j=0;j<response.length;j++){
        var attributedetail={};
        attributedetail["uuid"]=response[j].ref.uuid;
        attributedetail["datapodname"]=response[j].ref.name;
        attributedetail["name"]=response[j].attrName;
        attributedetail["attributeId"]=response[j].attrId;
        attributedetail["dname"]=response[j].ref.name+"."+response[j].attrName;
        attributedetail["id"]=response[j].ref.uuid+"_"+response[j].attrId;
        attributes.push(attributedetail)
        
      }
      return attributes;
    }
    getAttributesByRelation(uuid, version, type): Observable<any>{
      let url ="metadata/getAttributesByRelation?action=view&uuid=" + uuid + "&type=" + type
      return this._sharedService.getCall(url)
      .map((response: Response) => {      
          return this.modifyResponseAttributes(response.json());      
        
    })
     .catch(this.handleError);
    }
    private handleError(error: Response) {
        return Observable.throw(error.statusText);
    }
    modifyResponseAttributes(response){
      var allattributes = [];
      var attributes = [];
      var isNew = true;
      var count = 0;
      var attr;
      for (var j = 0; j < response.length; j++) {

        var attributedetail = {};
        attributedetail["uuid"] = response[j].ref.uuid;
        attributedetail["type"] = response[j].ref.type;
        attributedetail["datapodname"] = response[j].ref.name;
        attributedetail["name"] = response[j].attrName;
        attributedetail["attributeId"] = response[j].attrId;
        attributedetail["attrType"] = response[j].attrType;
        attributedetail["dname"] = response[j].ref.name + "." + response[j].attrName;
        allattributes.push(attributedetail);
        if (j == 0) {
          attr = [];
          attr.push(attributedetail);

        }
        if (j > 0 && attr.length > 0 && attr[attr.length - 1].uuid == attributedetail["uuid"])
          attr.push(attributedetail);
        else {
          if (j != 0) {
            attributes[count] = attr;
            attr = [];
            attr.push(attributedetail);

            count = count + 1;
          }
        }


      }
      attributes[count] = attr;
      var relationattribute = {}
      relationattribute["allattributes"] = allattributes;
      relationattribute["attributes"] = attributes
      return relationattribute;
    }
}