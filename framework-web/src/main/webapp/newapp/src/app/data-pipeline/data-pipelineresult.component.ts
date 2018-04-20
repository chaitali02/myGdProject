
import {Component,Input,OnInit,ViewChild,AfterViewInit} from '@angular/core';
import {Router,Event as RouterEvent,ActivatedRoute,Params,NavigationEnd} from '@angular/router';
import { Location } from '@angular/common'; 
import {Message} from 'primeng/components/common/api';
import {MessageService} from 'primeng/components/common/messageservice';   

import{ JointjsComponent} from './jointjs.component';
import {JointjsGroupComponent} from '../shared/components/jointjsgroup/jointjsgroup.component'

import { CommonService } from '../metadata/services/common.service';
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import {JointjsService} from './jointjsservice'
import{SharedDataService} from './shareddata.service'
   
@Component({
      selector: 'app-data-pipeli',
      templateUrl: './data-pipelineresult.template.html',
      
      
 })
    
export class DataPiplineResultComponent{
    intervalId:any;
    dagexecdata: any;
    version: any;
    id: any;
    msgs: any[];
    dagdata: any;
    mode: any;
    breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    
    
    @ViewChild(JointjsComponent) d_JointjsComponent: JointjsComponent;
    @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
    constructor(private _location: Location,private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _jointjsService:JointjsService,private _sharedDataService:SharedDataService, private _dataPipelineService : DataPipelineService){
        this.dagdata={};
        // setTimeout(() => {
        //     // this.d_JointjsComponent.params={};
        //     // this.d_JointjsComponent.IsGroupGraphShow=false
        // }, 1000);
       
        this.router.events
        .subscribe((event) => {
          if (event instanceof NavigationEnd) {
            this.stopStatusUpdate();
           // console.log('NavigationEnd:', event);
          }
        });
        console.log(this.activatedRoute)
        this.activatedRoute.params.subscribe((params : Params) => {
            this.id = params['id'];
            this.version = params['version'];
            this.mode = params['mode'];
            if(this.mode !== undefined) {
                this.getOneByUuidAndVersion(this.id,this.version);
            }
        });
        this.breadcrumbDataFrom=[{
            "caption":"Data Pipeline ",
            "routeurl":"/app/list/dagexec"
          },
          {
            "caption":"Result",
            "routeurl":"/app/list/dagexec"
          },
          
          {
            "caption":"",
            "routeurl":null
          }
          ]
       
    }
   
    public goBack() {
        if(this.d_JointjsComponent.IsGraphShow==true){
            this._location.back();
        }
        else if(this.d_JointjsComponent.IsGroupGraphShow ==true){
              this.d_JointjsComponent.goBack();     
        }
        else if(this.d_JointjsComponent.IsTableShow ==true){
            this.d_JointjsComponent.IsTableShow=false
            this.d_JointjsComponent.IsGraphShow=true;
        }
        else{
            this.d_JointjsComponent.IsGraphShow=true;
        }
    }
      
    getOneByUuidAndVersion(id,version){
        this.stopStatusUpdate();
        this._commonService.getOneByUuidAndVersion(id,version,'dagexec')
        .subscribe(
            response =>{
                this.onSuccessgetOneByUuidAndVersion(response)
            },
            error => console.log("Error :: " + error)
        ); 
    }
    
    onSuccessgetOneByUuidAndVersion(response){
        this.breadcrumbDataFrom[2].caption=response.name;
        this.dagexecdata=response;
       setTimeout(() => {
            this.d_JointjsComponent.createGraph(this.dagexecdata);  
            this.startStatusUpdate(this.id);
       }, 1000);
       this.intervalId = setInterval(() => {
        this.startStatusUpdate(this.id);
      }, 5000); 
      
    }
    latestStatus(statuses){
        var latest;
        statuses.forEach(function (status) {
            if(latest){
                if(status.createdOn > latest.createdOn){
                    latest = status
                }
            }
            else {
                latest = status;
            }
        });
        return latest;
    }
    
    startStatusUpdate(uuid) {
       
        this._dataPipelineService.getStatusByDagExec(uuid)
        .subscribe(
            response =>{
                this.onSuccessGetStatusByDagExec(response)
            },
            error => console.log("Error :: " + error)
            );
        
            
    }
    onSuccessGetStatusByDagExec=function(response){
        //     if(latestStatus(response.status).stage == 'Failed'){
        //         $scope.allowReExecution = true;
        //     }
         if(['Completed','Failed','Killed'].indexOf(this.latestStatus(response["status"]).stage) > -1){
                 this.stopStatusUpdate();
        }
        //else{
        //     if(!angular.equals(statusCache, response)){
        setTimeout(() => {
            this.d_JointjsComponent.updateGraphStatus(response);
        }, 1000);
        
        //statusCache = response;
    }
    stopStatusUpdate() {
        //statusCache = undefined;
        if(this.intervalId)
            clearInterval(this.intervalId);
      }
  }