import { Observable } from 'rxjs/Observable';
import { Inject, Injectable, Input } from '@angular/core';
import { Http,Response } from '@angular/http'
import { SharedService } from '../../shared/shared.service';
// import 'rxjs/add/operator/map';
// impo  [x: string]: any;
//import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()

export class CommonService{
    
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
  getAppLatest(type:String,inputFlag:String): Observable<any[]> {
    let url ='/common/getAllLatest?action=view&type='+type+'&inputFlag='+inputFlag;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getAll(type:String): Observable<any[]> {
    let url ='common/getAll?type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
   .catch(this.handleError);
  }

  getOneById(type:String,id:Number): Observable<any> {
    let url ='/common/getOneById?action=view&id='+id+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any>response.json();
  })
  .catch(this.handleError);
  }

  getAllVersionByUuid(type:String,uuid:Number): Observable<any[]> {
    let url ='/common/getAllVersionByUuid?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }

  downloadExport(uuid:Number): Observable<any> {
    let url ='admin/export/download?uuid='+uuid;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
    return <any>response.json();
    })
    .catch(this.handleError);
    }
  

  getLatestByUuid(uuid:Number,type:String): Observable<any> {
    let url ='/common/getLatestByUuid?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any>response.json();
  })
  .catch(this.handleError);
  }

  getOneByUuidAndVersion(uuid:Number,version:String,type:any): Observable<any> {
    let url ='/common/getOneByUuidAndVersion?action=view&uuid='+uuid+'&version='+version+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any>response.json();
  })
  .catch(this.handleError);
  }

submit(type:any,data:any): Observable<any[]> {
    let url ='/common/submit?action=edit&type='+type;
    return this._sharedService.postCall(url,data)
    .map((response: Response) => {
      return <any>response.text();
})
   .catch(this.handleError);
}

getAllLatest(type:String): Observable<any[]> {
    let url ='/common/getAllLatest?action=view&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
})
   .catch(this.handleError);
}

getDatapodByRelation(uuid:Number,type:Number): Observable<any[]> {
    let url ='/metadata/getDatapodByRelation?action=view&relationUuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
  })
  .catch(this.handleError);
  }

getAttributesByDatapod(type:String,uuid:Number): Observable<any[]> {
    let url ='/metadata/getAttributesByDatapod?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
})
   .catch(this.handleError);
}

getAttributesByDataset(type:String,uuid:Number): Observable<any[]> {
    let url ='/metadata/getAttributesByDataset?action=view&uuid='+uuid+'&type='+type;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
      return <any[]>response.json();
})
   .catch(this.handleError);
}

getRegistryByDatasource(uuid:any): Observable<any[]> {
  let url ='/metadata/getRegistryByDatasource?type=datasource&action=view&datasourceUuid='+uuid;
  return this._sharedService.getCall(url)
  .map((response: Response) => {
    return <any[]>response.json();
})
  .catch(this.handleError);
}
       
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
      return <any[]>this.modifyResponse(response.json());
    }
    
})
 .catch(this.handleError);
}
modifyRelation(response){
  var attributes=[];
  for(var j=0;j<response.length;j++){
    for(var i=0;i<response[j].attributes.length;i++){
      var attributedetail={};
      attributedetail["uuid"]=response[j].uuid;
      attributedetail["datapodname"]=response[j].name;
      attributedetail["name"]=response[j].attributes[i].name;
      attributedetail["dname"]=response[j].name+"."+response[j].attributes[i].name;
      attributedetail["attributeId"]=response[j].attributes[i].attributeId;
      attributedetail["id"]=response[j].uuid+"_"+response[j].attributes[i].attributeId;
      attributes.push(attributedetail)
    }
  }
  return attributes;
}
modifyResponse(response){
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
getGraphResults(version:String,degree:String,uuid:Number): Observable<any[]> {
    let url ='/graph/getGraphResults?action=view&uuid='+uuid+'&version='+version+'&degree='+degree;
    return this._sharedService.getCall(url)
    .map((response: Response) => {
        return <any[]>response.json();
})
   .catch(this.handleError);
}

getFormulaByType(uuid:Number,type:String): Observable<any[]> {
    let url ='/metadata/getFormulaByType?action=view&uuid='+uuid+'&type='+type;
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

getRuleExecByRule(uuid:Number): Observable<any[]> {
  let url ='/rule/getRuleExecByRule?action=view&ruleUuid='+uuid;
  return this._sharedService.getCall(url)
  .map((response: Response) => {
    return <any[]>response.json();
})
 .catch(this.handleError);
}




saveAs(uuid:Number,version:String,type:String): Observable<any[]> {
  let url ='/common/saveAs?action=clone&uuid='+uuid+'&version='+version+'&type='+type;
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



execute(uuid,version,type,action): Observable<any> {
  let url;
  if(type=="rulegroup"){
     url ='/rule/executeGroup?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="profile"){
    url = '/profile/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="profilegroup"){
     url = '/profile/executeGroup?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="dq"){
      url ='/dataqual/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="dqgroup"){
    url = '/dataqual/executeGroup?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="dag"){
      url = '/dag/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
  }
  if(type=="train"){
      url = "model/train/execute?uuid=" + uuid + "&version=" + version+ '&action=view'; 
    }
  if(type=="predict"){
    url = '/model/predict/execute?action='+ action +'&uuid=' + uuid + '&version=' + version + '&type=' + type;
}

  let body=null
  return this._sharedService.postCall(url,body)
  .map((response: Response) => {
    return <any[]>response.json();
})
 .catch(this.handleError);
}
}