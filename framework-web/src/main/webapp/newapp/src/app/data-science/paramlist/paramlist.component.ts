import { Location } from '@angular/common';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Version } from './../../shared/version';

import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/primeng';
import { CommonService } from '../../metadata/services/common.service';


@Component({
  selector: 'app-paramlist',
  templateUrl: './paramlist.component.html',
  styleUrls: ['./paramlist.component.css']
})
export class ParamlistComponent implements OnInit {
  
  showParamlist : any;
  versions: any[];
  active: any;
  published: any;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  breadcrumbDataFrom:any;
  paramlist : any;
  paramId : any;
  paramsArrays: any;
  loop : any;
  paramArrayTable : any;
  type : any;
  value : any;
  types : any;
  selectAllAttributeRow : any;
  msgs : any;
  isSubmitEnable:any;

  constructor(private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService) {
    this.showParamlist = true;
    this.paramlist = {};
    this.paramlist["active"]=true;
    this.isSubmitEnable=true;
    this.types = [{'value': 'date', 'label': 'date' },
    {'value': 'double', 'label': 'double' },
    {'value': 'integer', 'label': 'integer' },
    {'value': 'string', 'label': 'string' }]

    this.breadcrumbDataFrom=[{
      "caption":"Data Science",
      "routeurl":"/app/list/paramlist"
    },
    {
      "caption":"Parameter List",
      "routeurl":"/app/list/paramlist"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
   }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
    }); 
    if(this.mode !== undefined) {   
    this.getOneByUuidAndVersion(this.id,this.version);
    this.getAllVersionByUuid();
    }
  }

  getOneByUuidAndVersion(id,version){
    this._commonService.getOneByUuidAndVersion(this.id,this.version,'paramlist')
    .subscribe(
    response =>{//console.log(response)},
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  onSuccessgetOneByUuidAndVersion(response){
    
    this.paramlist=response
    const version: Version = new Version();
    this.uuid =response.uuid;
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version 

    this.createdBy=this.paramlist.createdBy.ref.name
    this.published = response['published'];
    if(this.published === 'Y') { this.published = true; } else { this.published = false; }
    this.active = response['active'];
    if(this.active === 'Y') { this.active = true; } else { this.active = false; }
    this.tags = response['tags']; 
    
    this.paramlist.published=response["published"] == 'Y' ? true : false
    this.paramlist.active=response["active"] == 'Y' ? true : false
    
    this.breadcrumbDataFrom[2].caption=response.name;

    let paramArray=[];
    if( response.params !=null){
    for(let i=0;i<response.params.length;i++){
      let paramObj={};
      paramObj["name"]=response.params[i].paramName;
      paramObj["type"]=response.params[i].paramType;
      paramObj["value"]=response.params[i].paramValue;
      paramArray[i]=paramObj
    }
  }
    this.paramArrayTable=paramArray
    console.log(this.paramArrayTable)
}
   
  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid("paramlist",this.id).subscribe(
      response => { this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log('Error :: ' + error)
    )
  }

  OnSuccesgetAllVersionByUuid(response){
    var temp=[]
    for (const i in response) {
      let ver={};
      ver["label"]=response[i]['version'];
      ver["value"]={};
      ver["value"]["label"]=response[i]['version'];      
      ver["value"]["uuid"]=response[i]['uuid']; 
      //allName["uuid"]=response[i]['uuid']
      temp[i]=ver;
    }
    this.VersionList=temp
  }

  onChangeActive(event) {
    if(event === true) {
      this.paramlist.active = 'Y';
    }
    else {
      this.paramlist.active = 'N';
    }
  }

  onChangePublished(event) {
    if(event === true) {
      this.paramlist.published = 'Y';
    }
    else {
      this.paramlist.published = 'N';
    }
  }

  addAttribute(){
    if(this.paramArrayTable == null){
       this.paramArrayTable=[];
    }
    let len=this.paramArrayTable.length+1
    let attrinfo={};
    attrinfo["name"]="";
    attrinfo["id"]=len-1;
    attrinfo[""]
      
    this.paramArrayTable.splice(this.paramArrayTable.length, 0,attrinfo);
  }

  removeAttribute(){
    var newDataList=[];
    this.paramArrayTable=false
    this.paramArrayTable.forEach(selected => {
      if(!selected.selected){
        newDataList.push(selected);
      }
    });
    this.paramArrayTable = newDataList;
  }

  checkAllAttributeRow(){
    if (!this.selectAllAttributeRow){
      this.selectAllAttributeRow = true;
      }
    else {
      this.selectAllAttributeRow = false;
      }
    this.paramArrayTable.forEach(attribute => {
      attribute.selected = this.selectAllAttributeRow;
    });
  }

  onVersionChange(){
    this.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label);
  }

  submitParamlist(){
    this.isSubmitEnable=true;
    let paramlistJson={};
    paramlistJson["uuid"]=this.paramlist.uuid
    paramlistJson["name"]=this.paramlist.name
    var tagArray=[];   
      if(this.paramlist.tags !=null){
        for(var counttag=0;counttag<this.paramlist.tags.length;counttag++){
              tagArray[counttag]=this.paramlist.tags[counttag];
        }
      }
    paramlistJson["tags"]=tagArray;
    paramlistJson["desc"]=this.paramlist.desc
    paramlistJson["active"]=this.paramlist.active == true ?'Y' :"N"
    paramlistJson["published"]=this.paramlist.published == true ?'Y' :"N"

    var sourceAttributesArray=[];
    for(var i=0;i<this.paramArrayTable.length;i++){
          var  attributemap={};
          attributemap["paramId"]=i;
          attributemap["paramName"]=this.paramArrayTable[i].name
          attributemap["paramType"]=this.paramArrayTable[i].type
          attributemap["paramValue"]=this.paramArrayTable[i].value
          sourceAttributesArray[i]=attributemap;
        }
    paramlistJson["params"]=sourceAttributesArray;

    console.log(JSON.stringify(paramlistJson))
    this._commonService.submit("paramlist",paramlistJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
    )}

    OnSuccessubmit(response){
      this.isSubmitEnable=true;
      this.msgs = [];
      this.msgs.push({severity:'success', summary:'Success Message', detail:'Paramlist Submitted Successfully'});
      setTimeout(() => {
        this.goBack()
        }, 1000);
          }


    public goBack() {
      //this._location.back();
      this.router.navigate(['app/list/paramlist']);
      
  }
  enableEdit(uuid, version) {
     this.router.navigate(['app/dataScience/paramlist',uuid,version, 'false']);
  }

  showview(uuid, version) {
    this.router.navigate(['app/dataScience/paramlist',uuid,version, 'true']);
  }

}
