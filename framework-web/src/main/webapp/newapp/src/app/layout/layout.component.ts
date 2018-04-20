import {
Component,
  ViewEncapsulation,
  ElementRef, Renderer,
  NgZone,
  ViewChild,
  Input,
  OnInit
} from '@angular/core';

import {
  Router,
  Event as RouterEvent,
  NavigationStart,
  NavigationEnd,
  NavigationCancel,
  NavigationError,
  ActivatedRoute
} from '@angular/router';
import {LayoutService } from './layout.service';
declare var jQuery:any;
@Component({
  selector: 'app-layout',
  templateUrl: './layout.template.html',
  styleUrls: ['./layout.component.css']
})



export class LayoutComponent {

  @ViewChild('myModal') myModal:ElementRef;
  AppData:any;
  RoleData:AnalyserNode
  selectedApp:any;
  selectedRole:any;
  userName:string;
  constructor(private _layoutService: LayoutService,private router:Router,private route: ActivatedRoute){
    this.AppData=null;
    let userDetail=JSON.parse(localStorage.getItem('userDetail'));
    this.userName=userDetail["userName"];
  }
  ngOnInit(){
    
      if(!localStorage.isAppRoleExists){
      this.open();
      this.getAppRole();
    }
  }    
  getAppRole(): void {
    this._layoutService.getAppRole()
      .subscribe(
      response => { this.OnSuccesGetAppRole(response) },
      error => console.log("Error :: " + error)
      )
  }
  OnSuccesGetAppRole(response) {
   //console.log(response);
    this.AppData=response;
    this.selectedApp=response[0];
    this.RoleData=this.selectedApp.roleInfo
    this.selectedRole=this.selectedApp.roleInfo[0]
  }

  open(){
    jQuery(this.myModal.nativeElement).modal('show'); 
  }
  
  cancelLogut(){
     jQuery(this.myModal.nativeElement).modal('hide'); 
  }
  getSelectedApp(){

    this.RoleData=this.selectedApp.roleInfo
    this.selectedRole=this.selectedApp.roleInfo[0]
  }
  setAppRole(){
    localStorage.isAppRoleExists=true
    jQuery(this.myModal.nativeElement).modal('hide');
    this._layoutService.setAppRole(this.selectedApp.appId.ref.uuid,this.selectedRole.ref.uuid)
    .subscribe(
      response => { this.OnSuccesSetAppRole(response) },
      error => {console.log("Error :: " + error)
      this.router.navigate(['./DataDiscovery'],{ relativeTo: this.route });
         
    }
      )
  }
  OnSuccesSetAppRole(response){
    console.log(response)
  }
}
  
 

