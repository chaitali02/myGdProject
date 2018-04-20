import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppMetadata } from '../../app.metadata';
import { CommonService } from '../../metadata/services/common.service';
import { Location } from '@angular/common';
import { ModelService } from '../../metadata/services/model.service';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent {

  breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
  type: any;
  uuid: any;
  version: any;
  mode: any;
  istableShow: boolean;
  params:any;
  modelResult : any;
  id: any;
  uid: any;
  constructor(private _location:Location,private _activatedRoute: ActivatedRoute,private router: Router,public appMetadata: AppMetadata,private _commonService:CommonService, private _modelService:ModelService) {
    
    this.breadcrumbDataFrom=[{
      "caption":"Data Science ",
      "routeurl":"/app/list/modelexec"
    },
    {
      "caption":"Results",
      "routeurl":"/app/list/modelexec"
    },
    {
      "caption":"Model Results",
      "routeurl":null 
    }
    ]
    
    this._activatedRoute.params.subscribe((params: Params) => {
    
      this.version = params['version'];
      this.mode = params['mode'];
      this.type = params['type'];
      this.id = params['id'];
      this.getModelResults();
    });
   }

   getModelResults(){
    this._modelService.getModelResults(this.id,this.version)
    .subscribe(
    response =>{
      this.onSuccessgetModelResults(response)},
    error => console.log("Error :: " + error)); 
   }

   onSuccessgetModelResults(response){
    this.modelResult = response;
    
   }
   
   public goBack() {
    this._location.back();
}
}
