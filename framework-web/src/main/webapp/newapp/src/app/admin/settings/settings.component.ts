import { ActivatedRoute, Params, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { DatapodService } from '../../metadata/services/datapod.service';
import { AppConfig } from '../../app.config';
import { SettingsService } from '../../metadata/services/settings.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.template.html',
  styleUrls: ['./settings.component.css']
})

export class SettingsComponent implements OnInit {

    private id : any;
    private version : any;
    private mode : any;
    private breadcrumbDataFrom: any;
    private cols: any;
    private title: any;
    private type: "setting"
    private action:"view"
    private generalSetting: any[]
    private metaEngine: any[]
    private ruleEngine:any[]
    private propertyName: any
    private propertyValue: any
    private tableData: any
    private selectallattribute: boolean;
    isSubmit:any;
    msgs: any;

    tabs = [
        { caption:'General',title:'General Settings'},
        { caption:'Meta Engine', title:'Meta Engine Settings'},
        { caption:'Rule Engine',title:'Rule Engine Settings'}
      ];
    constructor(private _datapodService : DatapodService,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _settingsService: SettingsService) {
        this.breadcrumbDataFrom=[{
            "caption":"Admin",
            "routeurl":'/app/admin/settings'
        },
        {
            "caption":"Settings",
            "routeurl":null
        },
        ]
    }
//--
    ngOnInit()  {
        this.activatedRoute.params.subscribe((params : Params) => {
            this.id = params['id']
            this.version = params['version']
            this.mode = params['mode']
            this.get();
            this.selectallattribute=false
        })
    }  
    
    public goBack() {
        //this._location.back(); 
        this.router.navigate(['app/DataDiscovery']);
    }
    get(){
        this._settingsService.get("setting","view")
        .subscribe(
        response =>{
          this.onSuccessgetData(response)},
        error => console.log("Error :: " + error)); 
    }
    onSuccessgetData(response){
        this.generalSetting = response['generalSetting'];
        this.metaEngine = response['metaEngine'];
        this.ruleEngine = response['ruleEngine'];
        this.tableData = this.generalSetting
    }

    onTabChange(event) {
        if(event.index =="0"){
            this.tableData = this.generalSetting
        }
        else if(event.index =="1"){
            this.tableData = this.metaEngine
        }
        else{
            this.tableData = this.ruleEngine
        }
    }
    addRow(){
        if(this.tableData == null){
            this.tableData=[];
          }
          var len=this.tableData.length+1
          var filertable={};
          filertable["propertyName"]=" ";
          filertable["propertyValue"]=" ";
          this.tableData.splice(this.tableData.length, 0,filertable);
    }
    removeRow(){
        let newDataList=[];
        this.selectallattribute=false;
        this.tableData.forEach(selected => {
          if(!selected.selected){
            newDataList.push(selected);
          }
        });
       this.tableData = newDataList;
    }
    checkAllAttributeRow(){        
        if (!this.selectallattribute){
            this.selectallattribute = true;
        }
        else {
            this.selectallattribute = false;
        }
        this.tableData.forEach(attribute => {
        attribute.selected = this.selectallattribute;
        });
    }

    submitSettings(){
        let newSetting={};
        this.isSubmit="true"

        newSetting["generalSetting"]=this.generalSetting;
        newSetting["metaEngine"]=this.metaEngine;
        newSetting["ruleEngine"]=this.ruleEngine;
        console.log(JSON.stringify(newSetting));
        
        this._settingsService.submit("datapod",newSetting).subscribe(
            response => { this.OnSuccessubmit(response)},
            error => console.log('Error :: ' + error)
          )
    
    }
    OnSuccessubmit(response){
        this.isSubmit="false"
        this.msgs = [];
        this.msgs.push({severity:'success', summary:'Success Message', detail:'Settings Submitted Successfully'});
        setTimeout(() => {
              //this.goBack()
        }, 1000);
    }
}