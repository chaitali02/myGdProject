import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {Observable} from 'rxjs/Observable';

import { Hero } from './hero';
@Injectable()
export class DatadiscoveryService {
  private heroesUrl = '';  // URL to web api

  constructor(@Inject(Http) private http: Http) { }

   private headers = new Headers({'sessionId': '3n1sktucszdv1rkbclbwmzhq6', 'X-Requested-With': 'XMLHttpRequest'});
  // getDatapodStats(): Promise<Hero[]> {
  //   return this.http.get(this.heroesUrl, {headers: this.headers})
  //     .toPromise()
  //     // .then(response => response.json().data as Hero[])
  //     .then(this.getResult)
  //     .catch(this.handleError);
  // }
  //
  // private getResult(res: Response){
  //   let body = res.json();
  //   return body;
  // }
  //
  // private handleError(error: any): Promise<any> {
  //   console.error('An error occurred', error); // for demo purposes only
  //   return Promise.reject(error.message || error);
  // }


  getDatapodStats(): Observable<any> {
    let url = 'http://localhost:8080/datapod/getDatapodStats?action=view';
    return this.http
      .get(url,{headers: this.headers})
      .map((response: Response) => {
        return <any>response.json();
      })
    // .catch(this.handleError);
  }


}
