import { Component, ViewEncapsulation } from '@angular/core';
import {Router} from '@angular/router';


@Component({
  selector: 'app-footer',
  styleUrls: [],
  templateUrl: './footer.template.html'
})
export class FooterComponent {
  constructor(public router: Router) {
  }
}
