import { Injectable, Inject } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from "rxjs/operators";

@Injectable()
export class DatadiscoveryService {
  private heroesUrl = '';  // URL to web api

  constructor(@Inject(Http) private http: Http) { }

  private headers = new Headers({ 'sessionId': '3n1sktucszdv1rkbclbwmzhq6', 'X-Requested-With': 'XMLHttpRequest' });

  private handleError<T>(error: any, result?: T) {
    return throwError(error);
  }

  getDatapodStats(): Observable<any> {
    let url = 'http://localhost:8080/datapod/getDatapodStats?action=view';
    return this.http
      .get(url, { headers: this.headers })
      .pipe(
        map(response => { return <any[]>response.json(); }),
        catchError(error => this.handleError<string>(error, "Network Error!")));
  }
}
