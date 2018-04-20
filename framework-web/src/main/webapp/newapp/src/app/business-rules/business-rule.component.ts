
import {Component,Input,OnInit} from '@angular/core';    
import {Router, Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import {AppMetadata} from '../app.metadata';

@Component({
  selector: 'app-businessrule',
  templateUrl: './business-rules.template.html',
  styleUrls: []
})
    
  export class BusinessRulesComponent {
    routerUrl: any;
    constructor(private activatedRoute: ActivatedRoute,private router: Router,public metaconfig: AppMetadata){
        
    }
  }
      
     
    
    