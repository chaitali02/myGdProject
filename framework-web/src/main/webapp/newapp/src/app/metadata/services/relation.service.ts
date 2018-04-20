import { Response, Http } from '@angular/http';
import { SharedService } from './../../shared/shared.service';
import { Observable } from 'rxjs/Observable';
import { Injectable,Inject,Input } from '@angular/core';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/promise';

@Injectable()
export class mapServices{
    constructor(@Inject(Http)private http : Http,private _sharedService: SharedService){}

    getOneById(id:Number,type:String): Observable<any[]> {
        let url ='/metadata/getOneById?action=view&id='+id+'&type='+type;
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