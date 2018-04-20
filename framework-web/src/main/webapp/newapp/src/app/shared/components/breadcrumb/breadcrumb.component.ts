
import { Component, Input } from '@angular/core';
@Component({
  selector: 'app-breadcrumb',
  styleUrls: [],
  templateUrl: './breadcrumb.template.html'
})
export class BreadcrumbComponent {
  
  @Input()
  breadcrumbData:any;
  constructor() {
  this.breadcrumbData=null;

  }
}