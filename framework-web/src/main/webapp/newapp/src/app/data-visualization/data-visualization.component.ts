
import { Component,ViewEncapsulation,ElementRef,Renderer,Input,OnInit } from '@angular/core';
import { Router,Event as RouterEvent,NavigationStart,NavigationEnd,NavigationCancel,NavigationError,ActivatedRoute,Params} from '@angular/router';

import {AppMetadata} from '../app.metadata';

@Component({
  selector: 'app-layout',
  templateUrl: './data-visualization.template.html',
  styleUrls: []
})
    
export class DataVisualizationComponent {
  routerUrl: any;
  constructor(private activatedRoute: ActivatedRoute,private router: Router,public metaconfig: AppMetadata){}
}
      
     
    
    