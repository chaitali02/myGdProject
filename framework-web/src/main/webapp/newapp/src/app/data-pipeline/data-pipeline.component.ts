import {
    Component,
      ViewEncapsulation,
      ElementRef, Renderer,
      Input,
      OnInit,
      ViewChild,
      AfterViewInit
    } from '@angular/core';
    
    import {
      Router,
      Event as RouterEvent,
      NavigationStart,
      NavigationEnd,
      NavigationCancel,
      NavigationError,
      ActivatedRoute,
      Params
    } from '@angular/router';
    import { Location } from '@angular/common'; 
    import {Message} from 'primeng/components/common/api';
    import {MessageService} from 'primeng/components/common/messageservice';
    
    import{ JointjsComponent} from './jointjs.component';

    import { CommonService } from '../metadata/services/common.service';
    import {JointjsService} from './jointjsservice'
    import{SharedDataService} from './shareddata.service'
    @Component({
      selector: 'app-data-pipeli',
      templateUrl: './data-pipeline.template.html',
      
      
    })
    
    export class DataPiplineComponent{
      msgs: any[];
      tem: object;
      tags: any;
      createdBy: any;
      result: string;
      resulte: boolean;
      dagdata: any;
      mode: any;
      version: any;
      uuid:any;
      id: any;
      continueCount:any;
      progressbarWidth:any;
      isSubmit:any
      breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
      @ViewChild(JointjsComponent) d_JointjsComponent: JointjsComponent;
   
      constructor(private _location: Location,private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _jointjsService:JointjsService,private _sharedDataService:SharedDataService){
        this.dagdata={};
        this.continueCount=1;
        this.isSubmit="false"
        this.progressbarWidth=33.33*this.continueCount+"%";
        this.dagdata["active"]= "true";
        // this.dagdata["published"]= "false";
        this.activatedRoute.params.subscribe((params : Params) => {
          this.id = params['id'];
          this.version = params['version'];
          this.mode = params['mode'];
          if(this.mode !== undefined) {
            this.getOneByUuidAndVersion(this.id,this.version);
          }
        
        });
        this.breadcrumbDataFrom=[{
          "caption":"Data Pipeline",
          "routeurl":"/app/list/dag"
        },
        {
        "caption":"Pipeline",
        "routeurl":"/app/list/dag"
      },
      {
        "caption":"",
        "routeurl":null
      }
      ]
      }
   
      public goBack() {
        //this._location.back();
        this.router.navigate(['app/list/dag']);
         }
      getDagData(){
        setTimeout(() => {
          if(this.continueCount ==2){
            console.log(this.d_JointjsComponent.graph.getCells())
            // this.tem=this.d_JointjsComponent.getPipeline();
            // console.log(this.tem)
            // this.dagdata=this.tem;
            // this.d_JointjsComponent.createGraph(this.tem);
         // //  this.tem=this.d_JointjsComponent.getPipeline();
          //  console.log(this.tem)
         //   this.dagdata=this.tem;
          //  console.log(this.dagdata)
         }
        }, 1000);
      }
      countContinue=function(){
        this.continueCount=this.continueCount+1;
        this.progressbarWidth=33.33*this.continueCount+"%";
        this.getDagData()
       
        
      }
      countBack=function(){
        this.continueCount=this.continueCount-1;
        this.progressbarWidth=33.33*this.continueCount+"%";
        this.getDagData()
      }
      getOneByUuidAndVersion(id,version){
        this._commonService.getOneByUuidAndVersion(id,version,'dag')
        .subscribe(
        response =>{
          this.onSuccessgetOneByUuidAndVersion(response)},
        error => console.log("Error :: " + error)); 
      }
    onSuccessgetOneByUuidAndVersion(response){
      this.dagdata=response;
      this.breadcrumbDataFrom[2].caption=response.name;
      this.result="true";
      this.uuid=response.uuid;
      this.createdBy=this.dagdata.createdBy.ref.name
      this.tags = response['tags'];
      this.dagdata.published=response["published"] == 'Y' ? true : false
      this.dagdata.active=response["active"] == 'Y' ? true : false
    }
    dagSubmit(){
    this.isSubmit="true"
    let dagJson={};
    let temp=this._jointjsService.convertGraphToDag(this._sharedDataService.getData());
    dagJson["uuid"]=this.dagdata.uuid;
    dagJson["name"]=this.dagdata.name;
    dagJson["desc"]=this.dagdata.desc;
    // dagJson["active"]=this.dagdata.active == true ?'Y' :"N"
    // dagJson["published"]=this.dagdata.published == true ?'Y' :"N"
    dagJson["stages"]=temp["stages"];
    dagJson["xPos"]=temp["xPos"];
    dagJson["yPos"]=temp["yPos"];
    console.log(dagJson);
    this._commonService.submit("dag",dagJson).subscribe(
      response => { this.OnSuccessubmit(response)},
      error => console.log('Error :: ' + error)
      )
    }  
    OnSuccessubmit(response){
      this.msgs = [];
      this.isSubmit="true"
      this.msgs.push({severity:'success', summary:'Success Message', detail:'Dag Submitted Successfully'});
      setTimeout(() => {
        this.goBack()
        }, 1000);
       }

    enableEdit(uuid, version) {
      this.router.navigate(['app/dataPipeline/dag',uuid,version, 'false']);
       }

    showview(uuid, version) {
        this.router.navigate(['app/dataPipeline/dag',uuid,version, 'true']);
      }
  }
    
    