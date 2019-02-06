import { Component, ViewEncapsulation } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';

import { LoginService } from '../login/login.service';

import { LoginStatus } from '../metadata/domain/domain.loginStatus';
@Component({
  selector: 'app-login',
  styleUrls: [],
  templateUrl: './login.template.html'
})
export class LoginComponent {
  error: any;
  loginResponse: LoginStatus;
  constructor(private http: Http, private _service: LoginService, public router: Router) {
    this.getLocalStorage();
  }

  getLocalStorage(){debugger
    let data = localStorage.getItem('userDetail');
  }

  onSubmit(event: any, username:any, password: any) {
    this._service.getValidateUser(username, password).subscribe(
      response => { this.onSuccessgetValidateUser(response) },
      error => { console.log("Error::", error) }
    )
  }

  onSuccessgetValidateUser(response: any) {
    this.loginResponse = JSON.parse(response._body);
    let result = JSON.stringify(this.loginResponse);
    if (this.loginResponse.status == "true") {
      // if(){
      //   localStorage.setItem('userDetail', result);
      // }
      this.router.navigate(['app']);
    }
    else {
      this.error = this.loginResponse.message;
    }
  }
}
