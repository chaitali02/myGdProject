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
  remember: boolean;
  username: any;

  constructor(private http: Http, private _service: LoginService, public router: Router) {
    this.remember = false;
    this.getLocalStorage();
  }

  getLocalStorage() {
    this.username = localStorage.getItem('userName');
    console.log("stating user name:" + this.username)
  }

  onSubmit(event: any, username: any, password: any) {
    this._service.getValidateUser(username, password).subscribe(
      response => { this.onSuccessgetValidateUser(response) },
      error => { console.log("Error::", error) }
    )
  }

  onSuccessgetValidateUser(response: any) {
    this.loginResponse = JSON.parse(response._body);
    let result = JSON.stringify(this.loginResponse);
    if (this.loginResponse.status == "true") {
      localStorage.setItem('userDetail',result)
     // let userDetail = JSON.parse(localStorage.getItem('userDetail'));
      this.router.navigate(['app']);
    }
    else {
      this.error = this.loginResponse.message;
    }
  }

  rememberMe() {
    if (this.remember == true) {
     // let result = JSON.stringify(this.loginResponse);
      localStorage.setItem('userName', this.username);
      console.log("stating user name:" + this.username)
    }
    else  if (this.remember == false){
      localStorage.setItem('userName', '');
    }
  }
}
