
import { Component, Input,OnInit } from '@angular/core';
import {Router,Event as RouterEvent,ActivatedRoute,Params,NavigationEnd} from '@angular/router';

import {AppMetadata} from '../../../app.metadata';
import {jointjsGroupService} from '../../components/jointjsgroup/joinjsgroup.service'
@Component({
  selector: 'app-resulttable',
  templateUrl: './tablerender.template.html',
  styleUrls: []
})
    
  export class TableRenderComponent {
  IsTableShow: boolean;
  IsError: boolean;
    columnOptions: any[];
    cols: any[];
    colsdata: any;
    @Input()
    tableParms:any

    constructor(private activatedRoute: ActivatedRoute,private router: Router,public metaconfig: AppMetadata,private _jointjsGroupService:jointjsGroupService){ 
     console.log(this.tableParms)
     //this.renderTable();
     this.cols=null
     this.IsError=false;
     this.IsTableShow=false;     
    }
    ngOnInit() {  
   
    }

    renderTable(params){
      this.IsTableShow=false; 
      let type;
      switch (params.type) {
        case 'dq':
          type = 'dataqual';
          break;
        case 'dqgroupexec':
          type = 'dataqual';
          break;
        case 'profile':
          type = 'profile';
          break;
         case 'rule':
          type = 'rule';
          break;      
    }
    this._jointjsGroupService.getResults(type,params.uuid,params.version)
      
      .subscribe(
          response =>{
            this.IsTableShow=true;
            this.colsdata=response
            let columns=[];
            console.log(response)
            if(response.length && response.length > 0){
              Object.keys(response[0]).forEach(val=>{
                  if (val != "rownum"){
                  let width=((val.split("").length * 9)+20)+"px"
                  columns.push({"field":val, "header":val,colwidth:width});
                  }
              });
            }
            
            this.cols=columns
            this.columnOptions = [];
            for(let i = 0; i < this.cols.length; i++) {
                this.columnOptions.push({label: this.cols[i].header, value: this.cols[i]});
            }
          },
          error => {
            this.IsTableShow=true; 
            console.log("Error :: " + error)
            this.IsError=true;    
        
        }
          );
    }
  }
     