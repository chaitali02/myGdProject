import { Component, ViewEncapsulation, ElementRef, Renderer, NgZone, ViewChild, Input, } from "@angular/core";
import { Router } from '@angular/router';
import { HeaderService } from './header.service';
import { onErrorResumeNext } from 'rxjs/operators';
declare var jQuery: any;
interface FileReaderEventTarget extends EventTarget {
  result: string
}

// interface FileReaderEvent extends Event {
//   target: FileReaderEventTarget;
//   getMessage():string;
// }
// interface HTMLInputEvent extends Event {
//   target: HTMLInputElement & EventTarget;
// }
@Component({
  selector: 'app-header',
  styleUrls: [],
  templateUrl: './header.template.html'
})
export class HeaderComponent {
  fileData: any;
  file: File;
  @ViewChild('AvtarModel') AvtarModel: ElementRef;
  uuid: any;
  userName: String;
  imagePath: string;
  ShowAvtarModel: String;
  lockScreenVisible: boolean;
  dummyArg: any;
  isErrorShow: boolean;
  password : any;
  constructor(public router: Router, private _headerService: HeaderService) {
    this.dummyArg = 1;
    this.ShowAvtarModel = 'false'
    let userDetail = JSON.parse(localStorage.getItem('userDetail'));
    this.userName = userDetail["userName"];
    this.uuid = userDetail["userUuid"];
    this.imagePath = "http://localhost:8080/app/avatars/" + this.uuid;
    this.lockScreenVisible = false;
    this.isErrorShow = false;
    this.password = "";
  }
  logOut() {
    this._headerService.logoutSession().subscribe(
      response => {
        this.router.navigate(['']);
        localStorage.clear();
      },
      error => {
        localStorage.clear();
        this.router.navigate(['']);
        console.log("Error :: " + error)
      }
    )
  }
  lockScreen() {
    this.lockScreenVisible = true;
  }
  notYpalrecha() {
    this.logOut();
  }
  uploadAvatar() {
    this.ShowAvtarModel = 'true'
    this.open();
  }
  readURL(event) {
    var files = event.srcElement.files;
    this.fileData = files;
    if (files && files[0]) {
      var reader = new FileReader();
      reader.onload = function (e: any) {
        jQuery('#avatar-preview')
          .attr('src', e.target.result)
          .show();
      };
      reader.readAsDataURL(files[0]);
    }
  }
  upload(file) {
    var f = file[0];
    console.log(f);
    var type = f.type.split('/')[1];
    console.log(type);
    this.imagePath = '';
    var fd = new FormData();
    fd.append('file', f);
    fd.append('fileName', this.uuid);
    console.log(fd);

    this._headerService.uploadImage(fd)
      .subscribe(
        response => {
          jQuery(this.AvtarModel.nativeElement).modal('hide');
          setTimeout(() => {
            this.call()
          }, 10000);
        })
  }
  call() {
    this.imagePath = "http://localhost:8080/app/avatars/" + this.uuid;
  }
  open() {
    jQuery(this.AvtarModel.nativeElement).modal('show');
  }
  close() {
    jQuery(this.AvtarModel.nativeElement).modal('hide');
  }

  passwordSubmit($event : any, username : any, password: any) {
    this._headerService.unlock(this.userName,this.password).subscribe(
      response => { this.onSuccessunlock(response) },
      error => { console.log("Error :: " + error) }
    )
  }

  onSuccessunlock(response: any) {
    if (response == true) {
      this.lockScreenVisible = false;
    }
    else {
      this.isErrorShow = true;
    }
    this.password = "";
  }
}
