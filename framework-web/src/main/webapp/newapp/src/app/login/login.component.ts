import { Component, ViewEncapsulation } from '@angular/core';
import {Router} from '@angular/router';
import { Login } from './login';
import { LoginService } from '../login/login.service';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { NgForm } from '@angular/forms';
@Component({
  selector: 'app-login',
  styleUrls: [],
  templateUrl: './login.template.html'
})
export class LoginComponent {
 
  promiseLogin: Promise<Login>;
  LoginStatus: Login;
  error:string;
  constructor(private http: Http, private _service: LoginService, public router: Router) {
   
  }
  
  
  onSubmit(event,username,password) {
    this.promiseLogin=this._service.getValidateUser(username,password);
    //this.promiseLogin.then(this.getResult);
    this.promiseLogin.then((res: Response)=>{
      let loginStaus="status";
      let result= JSON.stringify(res);   
      if(res[loginStaus] == "true"){
        localStorage.setItem('userDetail',result);
        this.router.navigate(['app']);
      }else{
        this.error=res["message"];
      }
    });
    //console.log('Test API - -- - - - ', this.promiseLogin);
  
  }
  
  

}
