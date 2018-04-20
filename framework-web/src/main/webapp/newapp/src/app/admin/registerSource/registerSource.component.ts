import { Location } from '@angular/common';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { DatapodService } from '../../metadata/services/datapod.service';
import { AppConfig } from '../../app.config';
import { CommonService } from '../../metadata/services/common.service';
import { RegisterSourceService } from '../../metadata/services/registerSource.service';

@Component({
  selector: 'app-registerSource',
  templateUrl: './registerSource.template.html'
})
export class RegisterSourceComponent implements OnInit {
  tableData: any;
  id : any;
  version : any;
  mode : any;
  breadcrumbDataFrom: { "caption": string; "routeurl": any; }[];
  typeSelect : {'value': string; 'label': string; }[];
  type : any;
  allNamesTypes : any;
  uuid : any;
  typeUuid : any;
  selectAllAttributeRow : any;
  msgs : any;
  isSubmitEnable : any;
  registerArray : any;
  index : any;
  registerdata : any;
  arr : any;

  constructor(private _location : Location,private _datapodService : DatapodService,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _regiSourceService : RegisterSourceService) {
    this.isSubmitEnable =true;
    this.tableData = [] 
    this.breadcrumbDataFrom=[{
      "caption":"Admin",
      "routeurl":'/app/admin/registerSource'
    },
    {
      "caption":"Register Source",
      "routeurl":null
    }    
    ]

    this.typeSelect = [
    {'value': 'FILE', 'label': 'FILE' },
    {'value': 'HIVE', 'label': 'HIVE' }
    ]    
   }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];     
    })
  }

  selectType() { 
    this.tableData= null
    this._datapodService.getDatasourceByType(this.type).subscribe(
      response => { this.OnSuccesGetSelectType(response)},
      error => console.log('Error :: ' + error)
    ) 
  }

  OnSuccesGetSelectType(response){
   this.allNamesTypes = [];
    for (const i in response) {
      let allNameType={};
      allNameType["label"]=response[i]['name'];
      allNameType["value"]=response[i]['uuid'];
      allNameType["version"]=response[i]['version']
      this.allNamesTypes[i]=allNameType;                      
    } 
  }

  ChangeTableData() {
    this._commonService.getRegistryByDatasource(this.allNamesTypes[0]["value"]).subscribe(
      response => { this.onSuccessGetRegistryByDatasource(response)},
      error => console.log('Error :: ' + error)
    ) 
  }

  onSuccessGetRegistryByDatasource(response){ 
    this.tableData = response; 
  }  

  checkAllAttributeRow(){
    if (!this.selectAllAttributeRow){
      this.selectAllAttributeRow = true;
      }
    else {
      this.selectAllAttributeRow = false;
      }
    this.tableData.forEach(registerjson => {
      registerjson.selected = this.selectAllAttributeRow;
      console.log(JSON.stringify(registerjson))
      console.log(JSON.stringify(registerjson.selected))
    });    
  }

  selectRegisterSource(index){
    let result = true;
    for(let i=0;i<this.tableData.length;i++){
      if(i == index){
    
        result = false
        i=this.tableData.length;
      }
    }
    this.isSubmitEnable = result;
  }

  submitRegisterSource(){
    let count = 0;
    this.registerArray=[];
    for(let i=0;i<this.tableData.length;i++){
      let registerObj = {}
      if(this.tableData[i].selected == true){      
        registerObj["id"]=this.tableData[i].id;
        registerObj["name"]=this.tableData[i].name;
        registerObj["desc"]=this.tableData[i].desc;
        registerObj["registeredOn"]=this.tableData[i].registeredOn;
        registerObj["status"]=this.tableData[i].status;
        this.registerArray[count]=registerObj;
        count = count + 1;
      }
    }
    console.log(JSON.stringify(this.registerArray))
    
    this._regiSourceService.getRegister(this.allNamesTypes[0]["value"],this.allNamesTypes[0]["version"],this.registerArray,this.type).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
    )}

    OnSuccessubmit(response){
      console.log('success');
      this.registerdata = response;
          
      for(let i=0;i<this.registerdata.length;i++){
        let resultObj = {};
        this.arr = this.registerdata[i].id-1;
       resultObj["id"] = this.registerdata[i].id;
       resultObj["name"] = this.registerdata[i].name;
       resultObj["desc"] = this.registerdata[i].desc;
       resultObj["registeredOn"] = this.registerdata[i].registeredOn;
       if(response[i]["status"] == "UnRegistered"){   
         resultObj["status"]="Not Registered"
        }
        else{
          resultObj["status"]=response[i].status
        }
        resultObj["selected"]= false;
        this.tableData[this.arr] = resultObj;
       
      }
      this.msgs = [];
      this.msgs.push({severity:'success', summary:'Success Message', detail:'RegisterSource Submitted Successfully'});
      // this.router.navigate(['app/admin/registerSource']);
      setTimeout(() => {
        this.router.navigate(['app/admin/registerSource']);
        }, 1000);     
    }

  refreshTableData(){
    for(let i=0;i<this.tableData.length;i++)
    this.tableData[i].selected =false;
    this.registerArray=null;
  }
  
}