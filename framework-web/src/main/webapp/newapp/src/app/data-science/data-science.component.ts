import { Component, 
    OnInit,
    ViewEncapsulation,
    ElementRef, Renderer,
    Input 
  }from '@angular/core';
  
  import {Router,
    Event as RouterEvent,
    NavigationStart,
    NavigationEnd,
    NavigationCancel,
    NavigationError,
    ActivatedRoute
  } from '@angular/router';
  
  
  @Component({
    selector: 'app-layout',
    templateUrl: './data-science.template.html',
    styleUrls: []
  })
  export class DataScienceComponent  {
    constructor(){
      console.log("dfd");
    }
    
  
  }