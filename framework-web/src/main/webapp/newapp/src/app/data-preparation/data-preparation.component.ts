import {
    Component,
      ViewEncapsulation,
      ElementRef, Renderer,
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
    
    @Component({
      selector: 'app-layout',
      templateUrl: './data-preparation.template.html',
      styleUrls: []
    })
    
    export class DataPreparationComponent {

      constructor(){
        console.log("dfd");
      }
    }
      
     
    
    