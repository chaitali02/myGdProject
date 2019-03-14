import { Component, ViewEncapsulation, Input } from '@angular/core';
import {Router} from '@angular/router';
import { AppConfig } from '../../../app.config';


@Component({
  selector: 'app-footer',
  styleUrls: [],
  templateUrl: './footer.template.html'
})
export class FooterComponent {

  version: any;

  constructor(public router: Router, private _appConfig: AppConfig) {

    this.version = _appConfig.config.version;
  
  }
}
