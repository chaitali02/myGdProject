import { Component } from '@angular/core';
import { Http } from '@angular/http';
import { Router } from '@angular/router';

import { CookieService } from 'ngx-cookie-service';
import { LoginService } from '../login/login.service';

import { LoginStatus } from '../metadata/domain/domain.loginStatus';
import { AppConfig } from '../app.config';

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
  year: string;

  constructor(private http: Http, private _service: LoginService, public router: Router, 
    private cookieService: CookieService, private _appConfig: AppConfig) {
    this.getLocalStorage();
    this.year = _appConfig.config.year;
  }

  getLocalStorage() {
    this.username = this.cookieService.get('userName');
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
      localStorage.setItem('userDetail', result);
      this.router.navigate(['app']);
    }
    else {
      this.error = this.loginResponse.message;
    }
  }

  rememberMe() {
    if (this.remember == true) {
      this.cookieService.set('userName', this.username);
    }
    else if (this.remember == false) {
      this.cookieService.set('userName', '');
    }
  }
}
