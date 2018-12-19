import { Component, OnInit } from '@angular/core';
import { SidebarService } from './sidebar.service';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Sidebar } from './sidebar';
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  
  toggle: boolean;
  _MetaArray: Sidebar[];
  selectedItemId:any;
  item:any;
  
  _MetaStats: {};
  adminMenu: {
    id:any;
    subMenu: any[];
  }
  DataReconMenu:{
    id:any;
    subMenu: any[];
  } 
  DataScienceMenu: {
    id:any;
    subMenu: any[];
  }
  DataIngestionMenu: {
    id:any;
    subMenu: any[];
  }
  DataPreparationMenu: {
    id:any;
    subMenu: any[];
  }
  DataProfileMenu: {
    id:any;
    subMenu: any[];
  }
  DataQualityMenu: {
    id:any;
    subMenu: any[];
  }
  BusinessRuleMenu: {
    id:any;
    subMenu: any[];
  }
  DataPipelineMenu: {
    id:any;
    subMenu: any[];
  }
  DataVisualizationMenu: {
    id:any;
    subMenu: any[];
  }
  constructor(private http: Http, private _sidebarService: SidebarService) {
    this.adminMenu = {id:7,subMenu: [] };
    //this.DataScienceMenu = {  id:5, subMenu: [] };
    this.DataPreparationMenu = {id:3, subMenu: [] };
    this._MetaStats=null;
    this.DataVisualizationMenu = {
      id:0,
      subMenu: [{
        name: "Dashboard",
        type: "dashboard"
      },
      {
        name: "Vizpod",
        type: "vizpod"
      }
      ]
    };
    this.DataProfileMenu = {
      id:1,
      subMenu: [{
        name: "Rule",
        type: "profile"
      },
      {
        name: "Rule Group",
        type: "profilegroup"
      },
      {
        name: "Rule Results",
        type: "profileexec"
      }
      ]
    };
    
    this.DataQualityMenu = {
      id:2,
      subMenu: [{
        name: "Rule",
        type: "dq"
      },
      {
        name: "Rule Group",
        type: "dqgroup"
      },
      {
        name: "Rule Results",
        type: "dqexec"
      }
      ]
    };

    this.BusinessRuleMenu = {
      id:4,
      subMenu: [{
        name: "Rule",
        type: "rule"
      },
      {
        name: "Rule Group",
        type: "rulegroup"
      },
      {
        name: "Parameter List",
        type: "paramlist",
        parentType: "rule"
      },
      {
        name: "Rule Results",
        type: "ruleexec"
      }
      ]
    };
    this.DataPipelineMenu = {
      id:6,
      subMenu: [
      //   {
      //   name: "Create",
      //   type: "dag"
      // },
      {
        name: "List",
        type: "dag"
      },
      {
        name: "Results",
        type: "dagexec"
      }
      ]
    };
    this.DataScienceMenu = {
      id:10,
      subMenu: [{
        name: "Algorithm",
        type: "algorithm"
      },
      {
        name: "Distribution",
        type: "distribution"
      },
      {
        name: "Model",
        type: "model"
      },
      {
        name: "Parameter List",
        type: "paramlist",
        parentType: "model"
      },
      {
        name: "Parameter Set",
        type: "paramset"
      },
      {
        name:"Operator",
        type:"operator"
      },
      {
        name:"Training",
        type:"train"
      },
      {
        name:"Prediction",
        type:"predict"
      },
      {
        name:"Simulation",
        type:"simulate"
      }
      ]
    };
    this.DataReconMenu={
      id:11,
      subMenu: [{
        name: "Rule",
        type: "recon"
      },
      {
        name: "Rule Group",
        type: "recongroup"
      },
      {
        name: "Rule Results",
        type: "reconexec"
      }
      ]
    }
  
    this.DataIngestionMenu={
      id:12,
      subMenu: [{
        name: "Rule",
        type: "ingest"
      },
      {
        name: "Rule Group",
        type: "ingestgroup"
      },
      {
        name: "Rule Results",
        type: "ingestexec"
      }
      ]
    }
  }
  
  ngOnInit(): void {
    this.getAll();

  }
 
  check(index,toggle1){
    if(this.selectedItemId == index && this.toggle == true){
      this.toggle=false;
      this.selectedItemId=null;
    }else{
      this.selectedItemId=index 
      this.toggle=true; 
    }

  }
  getAll(): void {
    this._sidebarService.getAll()
      .subscribe(
      response => { this.OnSuccesAllMeta(response) },
      error => console.log("Error :: " + error)
      )

  }
  OnSuccesAllMeta(response) {
    
    this._MetaArray = response;
    for (let i = 0; i < this._MetaArray.length; i++) {
      if (this._MetaArray[i].menu == 'admin' && this._MetaArray[i].active == 'Y') {
        let adminJson = {};
        adminJson["name"] = this._MetaArray[i].name;
        adminJson["type"] = this._MetaArray[i].name;

        this.adminMenu.subMenu.push(adminJson);
      }
      // if (this._MetaArray[i].menu == 'DataScience' && this._MetaArray[i].active == 'Y') {
      //   let DataScienceJson = {};
      //   DataScienceJson["name"] = this._MetaArray[i].name;
      //   DataScienceJson["type"] = this._MetaArray[i].name;

      //   this.DataScienceMenu.subMenu.push(DataScienceJson);
      // }
      if (this._MetaArray[i].menu == 'DataPreparation' && this._MetaArray[i].active == 'Y') {
        let DataPreparationJson = {};
        DataPreparationJson["name"] = this._MetaArray[i].name;
        DataPreparationJson["type"] = this._MetaArray[i].name;

        this.DataPreparationMenu.subMenu.push(DataPreparationJson);
      }
      // this.DataScienceMenu.subMenu[4]["name"] = "Results";
      // this.DataScienceMenu.subMenu[4]["type"] = "Results";
    }
    this._sidebarService.getMetaStats()
    .subscribe(
      response =>{
        // console.log(this.adminMenu.adminList);
        // console.log(response)
        this._MetaStats={};
        response.forEach((item,index)=>{
          if (item.type!='condition' && item.type!='dimension'&& item.type!='message' && item.type!='log' && item.type!='measure') {
        this._MetaStats[item.type]=item; 
          }
      })

    },
      error => console.log("Error :: " + error)
    )

    console.log(this.adminMenu);
    // console.log(response);
  }
}