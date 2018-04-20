import { Response, Http } from '@angular/http';
import { SharedService } from './../../shared/shared.service';
import { Observable } from 'rxjs/Observable';
import { Injectable,Inject,Input } from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()
export class FormulaService {
  
  constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
    getExpressionByType(uuid:Number,type:String): Observable<any[]> {
        let url ='/metadata/getExpressionByType?action=view&uuid='+uuid+'&type='+type;
        return this._sharedService.getCall(url)
        .map((response: Response) => {
          return <any[]>response.json();
    })
       .catch(this.handleError);
    }

    getFunctionByFunctionInfo(functioninfo:String): Observable<any[]> {
        let url ='/metadata/getFunctionByFunctionInfo?type=function&action=view&functionInfo='+functioninfo;
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