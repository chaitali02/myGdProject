import { Inject, Injectable,Input } from "@angular/core";
import { Http,Response } from "@angular/http";
import { SharedService } from "../../shared/shared.service";
import { Observable } from "rxjs/Observable";
// import 'rxjs/add/operator/map';
//  import 'rxjs/add/operator/catch';
//  import 'rxjs/add/operator/promise';

@Injectable()
export class RegisterSourceService{
    constructor(@Inject(Http) private http: Http,private _sharedService: SharedService) { }
  
    getRegister(uuid : Number,version : any,data : any,type:String): Observable<any[]> {
        let url ='/metadata/register?action=edit&uuid='+uuid+'&version='+version+'&type='+type;
        return this._sharedService.postCall(url,data)
        .map((response: Response) => {
          return <any[]>response.json();
      })
      .catch(this.handleError);
      }
      private handleError(error: Response) {
        return Observable.throw(error.statusText);
    }


     // var OnSuccess=function(response){
      //   var result=[]
      //     for(var i=0;i<response.length;i++){
      //     var resultjson={};
      //     resultjson.id=response[i].id;
      //     resultjson.name=response[i].name;
      //     resultjson.dese=response[i].dese;
      //     resultjson.registeredOn=response[i].registeredOn;
      //     if(response[i].status == "UnRegistered"){

      //       resultjson.status="Not Registered"
      //     }
      //     else{
      //       resultjson.status=response[i].status
      //     }

      //     resultjson.selected="false"
      //       result[i]=resultjson

      //     }
      //     deferred.resolve({
      //       data:result
      //     });
      // }
      
    // submit(type:any,data:any): Observable<any[]> {

    //     let url ='/common/submit?action=edit&type='+type;
    //     return this._sharedService.postCall(url,data)
    //     .map((response: Response) => {
    //     return <any>response.text();
    // })
    // .catch(this.handleError);
    // }

    //   factory.findRegister=function(uuid,version,data,type){
    //     var url=$location.absUrl().split("app")[0]
    //     return $http({
    //             url:url+"metadata/register?action=edit&uuid="+uuid+"&version="+version+"&type="+type,
    //             headers: {
    //           'Accept':'*/*',
    //           'content-Type' : "application/json",
    //            },
    //       method:"POST",
    //       data:JSON.stringify(data),
    //             }).
    //             then(function (response,status,headers) {
    //                return response;
    //             })
    // }

    // factory.applicationSubmit=function(data){
    // 	var url=$location.absUrl().split("app")[0]
    //     return $http({
    //          url:url+"common/submit?action=edit&type=application",

    //            headers: {
    //             'Accept':'*/*',
    //             'content-Type' : "application/json",
    //              },
    //         method:"POST",
    //         data:JSON.stringify(data),
    //     }).success(function(response){return response})
    //  }

    // this.getRegister=function(uuid,version,data,type){
    //     var deferred = $q.defer();


    //         RegisterSourceFacoty.findRegister(uuid,version,data,type).then(function(response){OnSuccess(response.data)});
    //         var OnSuccess=function(response){
    //           var result=[]
    //             for(var i=0;i<response.length;i++){
    //               var resultjson={};
    //               resultjson.id=response[i].id;
    //               resultjson.name=response[i].name;
    //               resultjson.dese=response[i].dese;
    //               resultjson.registeredOn=response[i].registeredOn;
    //               if(response[i].status == "UnRegistered"){

    //                   resultjson.status="Not Registered"
    //               }
    //               else{
    //                   resultjson.status=response[i].status
    //               }

    //               resultjson.selected="false"
    //                   result[i]=resultjson

    //             }
    //             deferred.resolve({
    //                 data:result
    //           });
    //     }
}