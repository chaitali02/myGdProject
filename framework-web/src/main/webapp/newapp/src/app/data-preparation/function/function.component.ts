import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Component, OnInit } from '@angular/core';
import { CommonService } from '../../metadata/services/common.service';

@Component({
  selector: 'app-function',
  templateUrl: './function.template.html',
  styleUrls: ['./function.component.css']
})
export class FunctionComponent implements OnInit {
  selectVersion: any;

  breadcrumbDataFrom : any;
  showFunctionData : boolean;
  versions: any[];
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  functionData : any;
  depends: any;
  allName : any;
  active : any;
  published : any;
  isSubmitEnable:any;
  
  constructor(config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) { 
    this.showFunctionData = true;
    this.functionData = {};
    this.functionData["active"]=true
    this.selectVersion={"version":""};
    this.breadcrumbDataFrom=[{
      "caption":"Data Preparation ",
      "routeurl":"/app/list/function"
    },
    {
      "caption":"Function",
      "routeurl":"/app/list/function"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
  
  })
  }

  getOneByUuidAndVersion(){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'function')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('function',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
    
  onSuccessgetOneByUuidAndVersion(response){
    this.functionData=response;
    this.createdBy=response.createdBy.ref.name;
    this.functionData.published=response["published"] == 'Y' ? true : false
    this.functionData.active=response["active"] == 'Y' ? true : false
    this.selectVersion.version= response['version'];
    this.selectVersion.uuid=response['uuid']
    this.breadcrumbDataFrom[2].caption=this.functionData.name;
  }

  OnSuccesgetAllVersionByUuid(response) {
    this.versions=response
  }

}
