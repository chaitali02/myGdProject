import { Response, Http } from '@angular/http';
import { SharedService } from './../../shared/shared.service';
import { Observable } from 'rxjs/Observable';
import { Injectable,Inject,Input } from '@angular/core';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/catch';
// import 'rxjs/add/operator/promise';

@Injectable()
export class MapServices{
    constructor(@Inject(Http)private http : Http,private _sharedService: SharedService){}
  
    getOneById(type:String,id:Number): Observable<any[]> {
        let url ='/metadata/getOneById?action=view&id='+id+'&type='+type;
        return this._sharedService.getCall(url)
        .map((response: Response) => {
          return <any[]>response.json();
    })
       .catch(this.handleError);
    }

    getAttributesByRule(uuid:Number,type:String): Observable<any[]> {
        let url ='/metadata/getAttributesByRule?action=view&uuid='+uuid+'&type='+type;
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