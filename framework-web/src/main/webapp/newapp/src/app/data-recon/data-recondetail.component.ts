
import {Component,Input,OnInit} from '@angular/core';
import {Router,Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import { Location } from '@angular/common'; 
import {Message} from 'primeng/components/common/api';
import { SelectItem } from 'primeng/primeng';
import {MessageService} from 'primeng/components/common/messageservice';
  
import { CommonService } from '../metadata/services/common.service';
import { DataReconService } from '../metadata/services/dataRecon.services';
import{ Version } from './../metadata/domain/version'
import{ DependsOn } from './dependsOn'
import {AttributeHolder} from './../metadata/domain/domain.attributeHolder'
  @Component({
    selector: 'app-data-recon',
    templateUrl: './data-recondetail.template.html',
    
    
  })
  
  export class DataReconDetailComponent{
    lhsTypes: { "text": string; "caption": string; }[];
    rhsTypes: { "text": string; "caption": string; }[];
    isSubmitEnable: boolean;
    allAttributeTarget: any[];
    allFormulaTarget: any[];
    targettableArray: any;
    allFormula: any[];
    selectallrow: boolean;
    rhsType: { "text": string; "caption": string; }[];
    lhsType: { "text": string; "caption": string; }[];
    allTargetAtrribute: any[];
    target: string;
    selectTargetAtrribute: DependsOn;
    selectTargetType: DependsOn;
    selectTargetFunction: DependsOn;
    selectSourceAtrribute: DependsOn;
    allSourceFunction: any[];
    allSourceAtrribute: any[];
    selectSoueceFunction: DependsOn;
    selectSourceType: DependsOn;
    IsProgerssShow: string;
    checkboxModelexecution: boolean;
    breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    dataqualitycompare: any;
    valueCheck: any;
    allRefIntegrity: any[];
    selectdatefromate: any;
    selectDataType: any;
    selectedAllFitlerRow: boolean;
    lhsdatapodattributefilter: any[];
    operators: string[];
    logicalOperators: string[];
    sourceTableArray: any;
    allIntegrityAttribute: any[];
    selectIntegrityAttribute: any;
    selectRefIntegrity: any;
    datefromate: string[];
    datatype: string[];
    selectAttribute:any;
    allAttribute: any[];
    dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
    dropdownList: any[];
    allNames: any[];
    sourcedata: DependsOn;
    source: string;
    sources: string[];
    selectedVersion: Version;
    VersionList: SelectItem[] = [];
    msgs: any[];
    tags: any;
    createdBy: any;
    dqdata: any;
    mode: any;
    version: any;
    id: any;
    uuid:any;
    continueCount:any;
    progressbarWidth:any;
    isSubmit:any
    IsSelectDataType:any
    IsSelectSoureceAttr:any
    constructor(private _location: Location,private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService, private _dataReconService:DataReconService){
      this.dqdata={};
      this.logicalOperators=["","AND","OR"]
      this.operators=["=", "<", ">", "<=", ">=", "BETWEEN"];
      this.datatype = ["", "String", "Int", "Float", "Double", "Date"];
      this.datefromate = ["dd/mm/yy", "dd/mm/yyyy", "d/m/yyyy", "dd-mmm-yy", "dd-mmm-yyyy", "d-mmm-yy", "d-mmm-yyyy", "d-mmmm-yy", "d-mmmm-yyyy", "yy/mm/dd", "yyyy/mm/dd", "mm/dd/yy", "mm/dd/yyyy", "mmm-dd-yy", "mmm-dd-yyyy", "yyyy-mm-dd", "mmm-yy", "yyyy"];
      this.continueCount=1;
      this.IsSelectSoureceAttr=false
      this.isSubmit="false"
      this.sources = ["datapod"];
      this.source=this.sources[0];
      this.target=this.sources[0];
      this.progressbarWidth=25*this.continueCount+"%";
      this.selectDataType="";
      this.selectdatefromate="";
      this.dataqualitycompare=null;
      this.sourceTableArray=null;
      this.dqdata["active"]=true 
      this.breadcrumbDataFrom=[{

        "caption":"Data Recon",
        "routeurl":"/app/list/recon"
      },
        {
        "caption":"Rule",
        "routeurl":"/app/list/recon"
      },
      {
        "caption":"",
        "routeurl":null
      }
      ] 
      this.lhsTypes = [
        { "text": "string", "caption": "string" },
        { "text": "datapod", "caption": "attribute" },
        { "text": "formula", "caption": "formula" },
        { "text": "integer", "caption": "integer" }]
      this.rhsTypes = [
        { "text": "string", "caption": "string" },
        { "text": "datapod", "caption": "attribute" },
        { "text": "formula", "caption": "formula" },
        { "text": "integer", "caption": "integer" },
        { "text": "dataset", "caption": "dataset" }]
      this.activatedRoute.params.subscribe((params : Params) => {
        this.id = params['id'];
        this.version = params['version'];
        this.mode = params['mode'];
        this.getFunctionByCategory()
        if(this.mode !== undefined) {
          this.getOneByUuidAndVersion(this.id,this.version);
          
        }
        else{
          this.getAllLatest()
        }
      });
    }
 
    public goBack() {
      //this._location.back();
      this.router.navigate(['app/list/recon']);
      
    }
    onChangeSource(){
    
      this.selectAttribute=null;
      this.getAllAttributeBySource();
    }
    onChangeTarget(){
      this.selectAttribute=null;
      this.getAllAttributeByTarget();
    }
    OnselectType = function() {
      if (this.selectDataType == "Date") {
        this.IsSelectDataType = true;
      } 
      else {
      this.IsSelectDataType=false;
      }
    }
    // onSourceAttributeChagne = function() {

    countContinue=function(){
      this.continueCount=this.continueCount+1;
      this.progressbarWidth=25*this.continueCount+"%"; 
    }

    countBack=function(){
      this.continueCount=this.continueCount-1;
      this.progressbarWidth=25*this.continueCount+"%";
    }
    
    checkAllFilterRow(){
      if (!this.selectedAllFitlerRow){
        this.selectedAllFitlerRow = true;
        }
      else {
        this.selectedAllFitlerRow = false;
        }
        this.sourceTableArray.forEach(filter => {
          filter.selected = this.selectedAllFitlerRow;
        });
    }
    getAllLatest(){
      this._commonService.getAllLatest(this.source).subscribe(
        response => { this.OnSuccesgetAllLatest(response)},
        error => console.log('Error :: ' + error)
      ) 
    }
    OnSuccesgetAllLatest(response1){
      let temp=[]
      if(this.mode == undefined) {
        let dependOnTemp: DependsOn = new DependsOn();
        dependOnTemp.label = response1[0]["name"];
        dependOnTemp.uuid = response1[0]["uuid"];
        this.sourcedata=dependOnTemp
       }
      for (const n in response1) {
        let allname={};
        allname["label"]=response1[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response1[n]['name'];      
        allname["value"]["uuid"]=response1[n]['uuid'];
        temp[n]=allname;
      }
      this.allNames = temp
      if(this.mode !== undefined) {
      this.getAllAttributeBySource();
      this.getAllAttributeByTarget();
      }
      // if(this.mode != undefined  && this.IsSelectSoureceAttr) {
      //   this.allRefIntegrity=this.allNames;
      //   this.changeRefIntegrity();
        
      // }
    }
    getAllAttributeBySource(){
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid,this.source).subscribe(
        response => { this.OnSuccesgetAllAttributeBySource(response)},
        error => console.log('Error :: ' + error)
      ) 
    }
    OnSuccesgetAllAttributeBySource(response){   
        let temp=[];
        let attribute=[]
        let count=1
        let allname={};
        allname["label"]='-select-';
        allname["value"]=null
        for (const n in response) {
          let allname={};
          allname["label"]=response[n]['name'];
          allname["value"]={};
          allname["value"]["label"]=response[n]['name'];      
          allname["value"]["u_Id"]=response[n]['id'];
          allname["value"]["uuid"]=response[n]['uuid'];
          allname["value"]["attrId"]=response[n]['attributeId'];
          temp[n]=allname
          attribute[n]=allname

          //count=count+1;
        }
        this.allSourceAtrribute=temp
        // this.lhsdatapodattributefilter=attribute
        // this.allAttribute.splice(0, 0, allname);
       // this.lhsdatapodattributefilter.splice(0,1);
      }
      getAllAttributeByTarget(){
        this._commonService.getAllAttributeBySource(this.selectTargetType.uuid,this.target).subscribe(
          response => { this.OnSuccesgetAllAttributeByTarget(response)},
          error => console.log('Error :: ' + error)
        ) 
      }
      OnSuccesgetAllAttributeByTarget(response){   
          let temp=[];
          let attribute=[]
          let count=1
          let allname={};
          allname["label"]='-select-';
          allname["value"]=null
          for (const n in response) {
            let allname={};
            allname["label"]=response[n]['name'];
            allname["value"]={};
            allname["value"]["label"]=response[n]['name'];      
            allname["value"]["u_Id"]=response[n]['id'];
            allname["value"]["uuid"]=response[n]['uuid'];
            allname["value"]["attrId"]=response[n]['attributeId'];
            temp[n]=allname
            attribute[n]=allname
  
            //count=count+1;
          }
          this.allTargetAtrribute=temp
          // this.lhsdatapodattributefilter=attribute
          // this.allAttribute.splice(0, 0, allname);
         // this.lhsdatapodattributefilter.splice(0,1);
        }
      getFunctionByCategory(){
        this._dataReconService.getFunctionByCategory("function").subscribe(
          response => { this.OnSuccesgetFunctionByCategory(response)},
          error => console.log('Error :: ' + error)
        ) 
      }
      OnSuccesgetFunctionByCategory(response){    
          let temp=[];
          let attribute=[]
          let count=1
          let allname={};
          allname["label"]='-select-';
          allname["value"]=null
          for (const n in response) {
            let allname={};
            allname["label"]=response[n]['name'];
            allname["value"]={};
            allname["value"]["label"]=response[n]['name'];      
            allname["value"]["u_Id"]=response[n]['id'];
            allname["value"]["uuid"]=response[n]['uuid'];
            allname["value"]["attrId"]=response[n]['attributeId'];
            temp[n]=allname
            attribute[n]=allname
  
            //count=count+1;
          }
          this.allSourceFunction=temp
          // this.lhsdatapodattributefilter=attribute
          // this.allAttribute.splice(0, 0, allname);
         // this.lhsdatapodattributefilter.splice(0,1);
        }
    getOneByUuidAndVersion(id,version){
      this._dataReconService.getOneByUuidAndVersion(id,version,'reconview')
      .subscribe(
      response =>{
        this.onSuccessgetOneByUuidAndVersion(response)},
      error => console.log("Error :: " + error)); 
    }
    onSuccessgetOneByUuidAndVersion(response){
      this.breadcrumbDataFrom[2].caption=response.dqdata.name
      this.dqdata=response.dqdata;
      this.dataqualitycompare=response.dqdata;
      console.log(response.filterInfo)
      this.sourceTableArray=response.filterInfo
      this.createdBy=response.dqdata.createdBy.ref.name
      this.dqdata.published=response.dqdata["published"] == 'Y' ? true : false
      this.dqdata.active=response.dqdata["active"] == 'Y' ? true : false
      this.tags = response.dqdata['tags'];
      const version: Version = new Version();
      this.uuid=response.uuid;
      version.label = response.dqdata['version'];
      version.uuid = response.dqdata['uuid'];
      this.selectedVersion=version
      let dependOnTemp: DependsOn = new DependsOn();
      dependOnTemp.label = response.dqdata["sourceAttr"]["ref"]["name"];
      dependOnTemp.uuid = response.dqdata["sourceAttr"]["ref"]["uuid"];
      //this.sourcedata=dependOnTemp;
      this.selectSourceType=dependOnTemp;
      let dependOn: DependsOn = new DependsOn();
      dependOn.label = response.dqdata["sourceFunc"]["ref"]["name"];
      dependOn.uuid = response.dqdata["sourceFunc"]["ref"]["uuid"];
      //this.sourcedata=dependOnTemp;
      this.selectSoueceFunction=dependOn;
      let sourceAttr: DependsOn = new DependsOn();
      sourceAttr.label = response.dqdata["sourceAttr"]["attrName"];
      sourceAttr["attrId"] = response.dqdata["sourceAttr"]["attrId"];
      //this.sourcedata=dependOnTemp;
      this.selectSourceAtrribute=sourceAttr;
      let target: DependsOn = new DependsOn();
      target.label = response.dqdata["targetAttr"]["ref"]["name"];
      target.uuid = response.dqdata["targetAttr"]["ref"]["uuid"];
      //this.sourcedata=dependOnTemp;
      this.selectTargetType=target;
      let targetFun: DependsOn = new DependsOn();
      targetFun.label = response.dqdata["targetFunc"]["ref"]["name"];
      targetFun.uuid = response.dqdata["targetFunc"]["ref"]["uuid"];
      //this.sourcedata=dependOnTemp;
      this.selectTargetFunction=targetFun;
      let targetAttr: DependsOn = new DependsOn();
      targetAttr.label = response.dqdata["targetAttr"]["attrName"];
      targetAttr["attrId"] = response.dqdata["targetAttr"]["attrId"];
      //this.sourcedata=dependOnTemp;
      this.getAllVersionByUuid();
      this.getAllLatest()
      this.selectTargetAtrribute=targetAttr;
  
      //this.selectTargetType=this.reconruledata.targetAttr.ref.type; 
      if(response.dqdata["sourcefilter"]){
        this.sourceTableArray=response.dqdata["sourcefilter"]["filterInfo"]
      }
      // else{
      //   this.sourceTableArray=[]
      // }
      if(response.dqdata["targetfilter"]){
        this.targettableArray=response.dqdata["targetfilter"]["filterInfo"]
      }
      else{

      }
      if (this.sourceTableArray) {
        for (var i = 0; i < this.sourceTableArray.length; i++) {
          this.sourceTableArray[i].lhstype={}
          this.sourceTableArray[i].rhstype={}
          this.sourceTableArray[i].lhsdatapodAttribute={}
          this.selectlhsType(this.sourceTableArray[i]["operand"][0]["ref"]["type"],i)
          this.selectrhsType(this.sourceTableArray[i]["operand"][1]["ref"]["type"],i)
          this.sourceTableArray[i].lhstype.text= this.sourceTableArray[i]["operand"][0]["ref"]["type"]   
          this.sourceTableArray[i].rhstype.text= this.sourceTableArray[i]["operand"][1]["ref"]["type"]  
          if (this.sourceTableArray[i].lhstype.text == "string") {
            this.sourceTableArray[i].lhsvalue=this.sourceTableArray[i]["operand"][0].value
          }
          else if (this.sourceTableArray[i].lhstype.text == "integer") {
            this.sourceTableArray[i].lhsInt=""
          }
          else if (this.sourceTableArray[i].lhstype.text == "datapod") {
            this.sourceTableArray[i].lhsdatapodAttribute.value=this.sourceTableArray[i]["operand"][0]["ref"]["uuid"]+"_"+this.sourceTableArray[i]["operand"][0]["attributeId"]
            this.sourceTableArray[i].lhsdatapodAttribute.label=this.sourceTableArray[i]["operand"][0]["ref"]["name"]+"."+this.sourceTableArray[i]["operand"][0]["attributeName"]
          }
          else if (this.sourceTableArray[i].lhstype.text == "formula") {
            this.sourceTableArray[i].lhsformula=this.sourceTableArray[i]["operand"][0]["ref"]["uuid"]
          } 
          
        }        
      }
      if (this.targettableArray) {
        for (var i = 0; i < this.targettableArray.length; i++) {
          this.targettableArray[i].lhstype={}
          this.targettableArray[i].rhstype={}
          this.targettableArray[i].lhstype.text= this.targettableArray[i]["operand"][0]["ref"]["type"]   
          this.targettableArray[i].rhstype.text= this.targettableArray[i]["operand"][1]["ref"]["type"]   
          this.targetselectlhsType(this.targettableArray[i]["operand"][0]["ref"]["type"],i)
          this.targetselectrhsType(this.targettableArray[i]["operand"][1]["ref"]["type"],i)
          if (this.targettableArray[i].lhstype.text == "string") {
            this.targettableArray[i].lhsvalue=this.sourceTableArray[i]["operand"][0].value
          }
          else if (this.targettableArray[i].lhstype.text == "integer") {
            this.targettableArray[i].lhsInt=""
          }
          else if (this.targettableArray[i].lhstype.text == "datapod") {
            this.sourceTableArray[i].lhsdatapodAttribute.value.id=this.targettableArray[i]["operand"][0]["ref"]["uuid"]+"_"+this.sourceTableArray[i]["operand"][0]["attributeId"]
            this.targettableArray[i].lhsdatapodAttribute.value.label=this.sourceTableArray[i]["operand"][0]["ref"]["name"]+"."+this.sourceTableArray[i]["operand"][0]["attributeName"]
            this.targettableArray[i].lhsdatapodAttribute.label=this.sourceTableArray[i]["operand"][0]["ref"]["name"]+"."+this.sourceTableArray[i]["operand"][0]["attributeName"]
          }
          else if (this.targettableArray[i].lhstype.text == "formula") {
            this.targettableArray[i].lhsformula=this.targettableArray[i]["operand"][0]["ref"]["uuid"]
          }
        }        
      }
    }
    
    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('recon',this.id)
      .subscribe(
      response =>{
        this.OnSuccesgetAllVersionByUuid(response)},
      error => console.log("Error :: " + error));
    }
    OnSuccesgetAllVersionByUuid(response) {
      for (const i in response) {
        let ver={};
        ver["label"]=response[i]['version'];
        ver["value"]={};
        ver["value"]["label"]=response[i]['version'];      
        ver["value"]["uuid"]=response[i]['uuid']; 
        this.VersionList[i]=ver;
      }
    } 
    onVersionChange(){
      this.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label);
    } 
    
    OnSucessGetOneById(response){
      this._commonService.execute(response.uuid,response.version,"dq","execute").subscribe(
        response => {
         this.showMassage('DQ Save and Submit Successfully','success','Success Message')
         setTimeout(() => {
          this.goBack()
        }, 1000);
        },
        error => console.log('Error :: ' + error)
      )
    }
    showMassage(msg,msgtype,msgsumary){
      this.isSubmit="false";
      this.IsProgerssShow="false";
      this.msgs = [];
      this.msgs.push({severity:msgtype, summary:msgsumary, detail:msg});
    }

    enableEdit(uuid, version) {
      this.router.navigate(['app/recon/createreconerule',uuid,version, 'false']);
    }

    showview(uuid, version) {
      this.router.navigate(['app/recon/createreconerule',uuid,version, 'true']);
    }
    selectlhsType(type, index) {
      
      if (type == "string" || type == "simple")  {
        this.sourceTableArray[index].islhsSimple = true;
        this.sourceTableArray[index].islhsDatapod = false;
      //  this.sourceTableArray[index].lhsvalue = "''";
        this.sourceTableArray[index].islhsFormula = false;
        this.sourceTableArray[index].islhsInt = false;
      }
      else if (type == "integer") {
        this.sourceTableArray[index].islhsInt = true;
        this.sourceTableArray[index].islhsDatapod = false;
        this.sourceTableArray[index].islhsFormula = false;
       // this.sourceTableArray[index].rhsvalue = "''";
        this.sourceTableArray[index].islhsSimple = false;
      }
      else if (type == "datapod") {
        this.sourceTableArray[index].islhsInt = false;
        this.sourceTableArray[index].islhsSimple = false;
        this.sourceTableArray[index].islhsDatapod = true;
        this.sourceTableArray[index].islhsFormula = false;
        this.getAllAttributeBySourceDrop(true, index, "lhs");
      }
      else if (type == "formula") {
        this.sourceTableArray[index].islhsInt = false;
        this.sourceTableArray[index].islhsFormula = true;
        this.sourceTableArray[index].islhsSimple = false;
        this.sourceTableArray[index].islhsDatapod = false;
        //this.sourceTableArray[index].sourceformula={}
        this.getAllFormula(true, index, 'lhs')
      }
  
  
    }
    selectrhsType(type, index) {
  
      if (type == "string" || type == "simple") {
        this.sourceTableArray[index].isrhsInt = false;
        this.sourceTableArray[index].isrhsSimple = true;
        this.sourceTableArray[index].isrhsDatapod = false;
        this.sourceTableArray[index].isrhsFormula = false;
       // this.sourceTableArray[index].rhsvalue = "''";
      }
      else if (type == "integer") {
        this.sourceTableArray[index].isrhsInt = true;
        this.sourceTableArray[index].isrhsDatapod = false;
        this.sourceTableArray[index].isrhsFormula = false;
       // this.sourceTableArray[index].rhsvalue = "''";
        this.sourceTableArray[index].isrhsSimple = false;
      }
      else if (type == "datapod") {
        this.sourceTableArray[index].isrhsInt = false;
        this.sourceTableArray[index].isrhsSimple = false;
        this.sourceTableArray[index].isrhsDatapod = true;
        this.sourceTableArray[index].isrhsFormula = false;
        this.sourceTableArray[index].sourceattribute = {}
        this.getAllAttributeBySourceDrop(true, index, 'rhs');
      }
      else if (type == "formula") {
        this.sourceTableArray[index].isrhsInt = false;
        this.sourceTableArray[index].isrhsFormula = true;
        this.sourceTableArray[index].isrhsSimple = false;
        this.sourceTableArray[index].isrhsDatapod = false;
        //this.sourceTableArray[index].sourceformula={}
        this.getAllFormula(true, index, 'rhs')
      }
  
    }
    targetselectlhsType(type, index) {
      
      if (type == "string" || type == "simple") {
        this.targettableArray[index].islhsSimple = true;
        this.targettableArray[index].islhsDatapod = false;
        //this.targettableArray[index].lhsvalue = "''";
        this.targettableArray[index].islhsFormula = false;
        this.targettableArray[index].islhsInt = false;
      }
      else if (type == "integer") {
        this.targettableArray[index].islhsInt = true;
        this.targettableArray[index].islhsDatapod = false;
        this.targettableArray[index].islhsFormula = false;
       // this.targettableArray[index].rhsvalue = "''";
        this.targettableArray[index].islhsSimple = false;
      }
      else if (type == "datapod") {
        this.targettableArray[index].islhsInt = false;
        this.targettableArray[index].islhsSimple = false;
        this.targettableArray[index].islhsDatapod = true;
        this.targettableArray[index].islhsFormula = false;
        this.getAllAttributeBySourceTarget(true, index, "lhs");
      }
      else if (type == "formula") {
        this.targettableArray[index].islhsInt = false;
        this.targettableArray[index].islhsFormula = true;
        this.targettableArray[index].islhsSimple = false;
        this.targettableArray[index].islhsDatapod = false;
        //this.targettableArray[index].sourceformula={}
        this.getAllFormulaTarget(true, index, 'lhs')
      }
  
  
    }
    targetselectrhsType(type, index) {
  
      if (type == "string" || type == "simple") {
        this.targettableArray[index].isrhsInt = false;
        this.targettableArray[index].isrhsSimple = true;
        this.targettableArray[index].isrhsDatapod = false;
        this.targettableArray[index].isrhsFormula = false;
       // this.targettableArray[index].rhsvalue = "''";
      }
      else if (type == "integer") {
        this.targettableArray[index].isrhsInt = true;
        this.targettableArray[index].isrhsDatapod = false;
        this.targettableArray[index].isrhsFormula = false;
       // this.targettableArray[index].rhsvalue = "''";
        this.targettableArray[index].isrhsSimple = false;
      }
      else if (type == "datapod") {
        this.targettableArray[index].isrhsInt = false;
        this.targettableArray[index].isrhsSimple = false;
        this.targettableArray[index].isrhsDatapod = true;
        this.targettableArray[index].isrhsFormula = false;
        this.targettableArray[index].sourceattribute = {}
        this.getAllAttributeBySourceTarget(true, index, 'rhs');
      }
      else if (type == "formula") {
        this.targettableArray[index].isrhsInt = false;
        this.targettableArray[index].isrhsFormula = true;
        this.targettableArray[index].isrhsSimple = false;
        this.targettableArray[index].isrhsDatapod = false;
        //this.targettableArray[index].sourceformula={}
        this.getAllFormulaTarget(true, index, 'rhs')
      }
  
    }
    getAllAttributeBySourceDrop(defaultValue, index, type) {
      this._commonService.getAllAttributeBySource(this.selectSourceType.uuid,this.source).subscribe(
        response => { this.OnSuccesgetAllAttributeBySourceDrop(response, defaultValue, index, type) },
        error => console.log('Error :: ' + error)
      )
    }
    OnSuccesgetAllAttributeBySourceDrop(response2, defaultValue, index, type) {
      //   
      let temp = []
      for (const n in response2) {
        let allname1 = {};
        allname1["label"] = response2[n]['dname'];
        allname1["value"] = {};
        allname1["value"]["label"] = response2[n]['dname'];
        allname1["value"]["id"] = response2[n]['id'];
        temp[n] = allname1;
      }
      this.allAttribute = temp;
      if (defaultValue == true && index != null) {
        let lhsdatapodAttribute = {}
        lhsdatapodAttribute["label"] = this.allAttribute[0]["label"];
        lhsdatapodAttribute["id"] = this.allAttribute[0]["value"]['id'];
        if (type == 'lhs') {
          this.sourceTableArray[index].lhsdatapodAttribute = lhsdatapodAttribute;
        } else {
          this.sourceTableArray[index].rhsdatapodAttribute = lhsdatapodAttribute;
        }
      }
  
    }
    getAllFormula(defaultValue, index, type) {
      this._commonService.getFormulaByType(this.selectSourceType.uuid, "formula").subscribe(
        response => { this.onSuccessgetAllFormula(response, defaultValue, index, type) },
        error => console.log('Error :: ' + error)
      )
    }
    onSuccessgetAllFormula(response, defaultValue, index, type) {
      let temp = []
      if (response[0]) {
        let sourceformula = {};
        sourceformula["label"] = response[0].name;
        sourceformula["uuid"] = response[0].uuid;
        if (type == 'lhs') {
          this.sourceTableArray[index].lhsformula = sourceformula;
        } else {
          this.sourceTableArray[index].rhsformula = sourceformula;
  
        }
      }
      for (const n in response) {
        let allname1 = {};
        allname1["label"] = response[n]['name'];
        allname1["value"] = {};
        allname1["value"]["label"] = response[n]['name'];
        allname1["value"]["uuid"] = response[n]['uuid'];
        temp[n] = allname1;
      }
      this.allFormula = temp
    }

    getAllAttributeBySourceTarget(defaultValue, index, type) {
      this._commonService.getAllAttributeBySource(this.selectTargetType.uuid,this.target).subscribe(
        response => { this.OnSuccesgetAllAttributeBySourceTarget(response, defaultValue, index, type) },
        error => console.log('Error :: ' + error)
      )
    }
    OnSuccesgetAllAttributeBySourceTarget(response2, defaultValue, index, type) {
      //   
      let temp = []
      for (const n in response2) {
        let allname1 = {};
        allname1["label"] = response2[n]['dname'];
        allname1["value"] = {};
        allname1["value"]["label"] = response2[n]['dname'];
        allname1["value"]["id"] = response2[n]['id'];
        temp[n] = allname1;
      }
      this.allAttributeTarget = temp;
      if (defaultValue == true && index != null) {
        let lhsdatapodAttribute = {}
        lhsdatapodAttribute["label"] = this.allAttributeTarget[0]["label"];
        lhsdatapodAttribute["id"] = this.allAttributeTarget[0]["value"]['id'];
        if (type == 'lhs') {
          this.targettableArray[index].lhsdatapodAttribute = lhsdatapodAttribute;
        } else {
          this.targettableArray[index].rhsdatapodAttribute = lhsdatapodAttribute;
        }
      }
  
    }
    getAllFormulaTarget(defaultValue, index, type) {
      this._commonService.getFormulaByType(this.selectTargetType.uuid, "formula").subscribe(
        response => { this.onSuccessgetAllFormulaTarget(response, defaultValue, index, type) },
        error => console.log('Error :: ' + error)
      )
    }
    onSuccessgetAllFormulaTarget(response, defaultValue, index, type) {
      let temp = []
      if (response[0]) {
        let sourceformula = {};
        sourceformula["label"] = response[0].name;
        sourceformula["uuid"] = response[0].uuid;
        if (type == 'lhs') {
          this.targettableArray[index].lhsformula = sourceformula;
        } else {
          this.targettableArray[index].rhsformula = sourceformula;
  
        }
      }
      for (const n in response) {
        let allname1 = {};
        allname1["label"] = response[n]['name'];
        allname1["value"] = {};
        allname1["value"]["label"] = response[n]['name'];
        allname1["value"]["uuid"] = response[n]['uuid'];
        temp[n] = allname1;
      }
      this.allFormulaTarget = temp
    }
    selectAllRowTarget() {
      this.targettableArray.forEach(expression => {
        expression.selected = this.selectallrow;
      });
    }
  
    addRowTarget() {
      if (this.targettableArray == null) {
  
        this.targettableArray = [];
      }
      var expessioninfo = {}
      expessioninfo["islhsDatapod"] = false;
      expessioninfo["islhsFormula"] = false;
      expessioninfo["islhsSimple"] = true;
      expessioninfo["isrhsDatapod"] = false;
      expessioninfo["isrhsFormula"] = false;
      expessioninfo["isrhsSimple"] = true;
      expessioninfo["operator"] = this.operators[0]
      expessioninfo["lhstype"] = this.lhsTypes[0]
      expessioninfo["rhstype"] = this.rhsTypes[0]
      expessioninfo["rhsvalue"] = "''";
      expessioninfo["lhsvalue"] = "''";
      expessioninfo["logicalOperator"] = this.logicalOperators[0];
      this.targettableArray.splice(this.targettableArray.length, 0, expessioninfo);
  
    }
    removeRowTarget() {
      let newDataList = [];
      this.selectallrow = false;
      this.targettableArray.forEach(selected => {
        if (!selected.selected) {
          newDataList.push(selected);
        }
      });
      if (newDataList.length > 0) {
        newDataList[0].logicalOperator = "";
      }
      this.targettableArray = newDataList;
    }
    selectAllRow() {
      this.sourceTableArray.forEach(expression => {
        expression.selected = this.selectallrow;
      });
    }
  
    addRow() {
      if (this.sourceTableArray == null) {
  
        this.sourceTableArray = [];
      }
      var expessioninfo = {}
      expessioninfo["islhsDatapod"] = false;
      expessioninfo["islhsFormula"] = false;
      expessioninfo["islhsSimple"] = true;
      expessioninfo["isrhsDatapod"] = false;
      expessioninfo["isrhsFormula"] = false;
      expessioninfo["isrhsSimple"] = true;
      expessioninfo["operator"] = this.operators[0]
      expessioninfo["lhstype"] = this.lhsTypes[0]
      expessioninfo["rhstype"] = this.rhsTypes[0]
      expessioninfo["rhsvalue"] = "''";
      expessioninfo["lhsvalue"] = "''";
      expessioninfo["logicalOperator"] = this.logicalOperators[0];
      this.sourceTableArray.splice(this.sourceTableArray.length, 0, expessioninfo);
  
    }
    removeRow() {
      let newDataList = [];
      this.selectallrow = false;
      this.sourceTableArray.forEach(selected => {
        if (!selected.selected) {
          newDataList.push(selected);
        }
      });
      if (newDataList.length > 0) {
        newDataList[0].logicalOperator = "";
      }
      this.sourceTableArray = newDataList;
    }
    submit(){
      this.isSubmitEnable = true;
      this.isSubmit="true"
      let dqJson={};
      dqJson["uuid"]=this.dqdata.uuid;
      dqJson["name"]=this.dqdata.name;
      dqJson["desc"]=this.dqdata.desc;
      let tagArray=[];
      if(this.dqdata.tags !=null){
        for(var counttag=0;counttag<this.dqdata.tags.length;counttag++){
             tagArray[counttag]=this.dqdata.tags[counttag];
        }
      }
      dqJson["tags"]=tagArray;
      dqJson["active"]=this.dqdata.active == true ?'Y' :"N"
      dqJson["published"]=this.dqdata.published == true ?'Y' :"N"
      var sourceattribute={}
      var ref ={}
      //sourceattribute["attrName"]=this.selectSourceAtrribute["attrName"]
      sourceattribute["attrId"]=this.selectSourceAtrribute["attrId"]
      ref["name"]=this.selectSourceType["name"]
      ref["type"]=this.source
      ref["uuid"]=this.selectSourceType["uuid"]
      sourceattribute["ref"]=ref
      dqJson["sourceAttr"]=sourceattribute
      var targetattribute={}
      var ref ={}
      //targetattribute["attrName"]=this.selectTargetAtrribute["attrName"]
      targetattribute["attrId"]=this.selectTargetAtrribute["attrId"]
      ref["name"]=this.selectTargetType["name"]
      ref["type"]=this.target
      ref["uuid"]=this.selectTargetType["uuid"]
      targetattribute["ref"]=ref
      dqJson["targetAttr"]=targetattribute
      var sourFunction={}
      var ref ={}
      ref["name"]=this.selectSoueceFunction["name"]
      ref["type"]='function'
      ref["uuid"]=this.selectSoueceFunction["uuid"]
      sourFunction["ref"]=ref
      dqJson["sourceFunc"]=sourFunction
      var targFunction={}
      var ref ={}
      ref["name"]=this.selectTargetFunction["name"]
      ref["type"]='function'
      ref["uuid"]=this.selectTargetFunction["uuid"]
      targFunction["ref"]=ref
      dqJson["targetFunc"]=targFunction
      var expressioninfoArray = [];
      if (this.sourceTableArray) {
        for (var i = 0; i < this.sourceTableArray.length; i++) {
          var expressioninfo = {};
          var operand = []
          var lhsoperand = {}
          var lhsref = {}
          var rhsoperand = {}
          var rhsref = {};
          expressioninfo["logicalOperator"] = this.sourceTableArray[i].logicalOperator;
          expressioninfo["operator"] = this.sourceTableArray[i].operator;
          if (this.sourceTableArray[i].lhstype.text == "string") {
  
            lhsref["type"] = "simple";
            lhsoperand["ref"] = lhsref;
            lhsoperand["value"] = this.sourceTableArray[i].lhsvalue;
          }
          else if (this.sourceTableArray[i].lhstype.text == "datapod") {
           
              lhsref["type"] = "datapod";
            
            let uuid = this.sourceTableArray[i].lhsdatapodAttribute.id.split("_")[0]
            var attrid = this.sourceTableArray[i].lhsdatapodAttribute.id.split("_")[1]
            lhsref["uuid"] = uuid
            //this.sourceTableArray[i].lhsdatapodAttribute.uuid;
            lhsoperand["ref"] = lhsref;
            lhsoperand["attributeId"] = attrid
          }
          else if (this.sourceTableArray[i].lhstype.text == "formula") {
  
            lhsref["type"] = "formula";
            lhsref["uuid"] = this.sourceTableArray[i].lhsformula.uuid;
            lhsoperand["ref"] = lhsref;
          }
          operand[0] = lhsoperand;
          if (this.sourceTableArray[i].rhstype.text == "string") {
  
            rhsref["type"] = "simple";
            rhsoperand["ref"] = rhsref;
            rhsoperand["value"] = this.sourceTableArray[i].rhsvalue;
          }
          else if (this.sourceTableArray[i].rhstype.text == "datapod") {

              rhsref["type"] = "datapod";
            
            rhsref["uuid"] = this.sourceTableArray[i].rhsdatapodAttribute.id.split("_")[0];
  
            rhsoperand["ref"] = rhsref;
            rhsoperand["attributeId"] = this.sourceTableArray[i].rhsdatapodAttribute.id.split("_")[1];
          }
          else if (this.sourceTableArray[i].rhstype.text == "formula") {
  
            rhsref["type"] = "formula";
            rhsref["uuid"] = this.sourceTableArray[i].rhsformula.uuid;
            rhsoperand["ref"] = rhsref;
          }
          operand[1] = rhsoperand;
          expressioninfo["operand"] = operand;
          expressioninfoArray[i] = expressioninfo
          
        }
        var test={}
        test["filterInfo"]=expressioninfoArray
        dqJson["sourcefilter"]=test
        dqJson["sourcefilterChg"]='Y'
      }
      else{
        dqJson["sourcefilter"]=null
        dqJson["sourcefilterChg"]='Y'
      }
      var expressioninfoArray1=[]
      if (this.targettableArray) {
        for (var i = 0; i < this.targettableArray.length; i++) {
          var expressioninfo = {};
          var operand = []
          var lhsoperand = {}
          var lhsref = {}
          var rhsoperand = {}
          var rhsref = {};
          expressioninfo["logicalOperator"] = this.targettableArray[i].logicalOperator;
          expressioninfo["operator"] = this.targettableArray[i].operator;
          if (this.targettableArray[i].lhstype.text == "string") {
  
            lhsref["type"] = "simple";
            lhsoperand["ref"] = lhsref;
            lhsoperand["value"] = this.targettableArray[i].lhsvalue;
          }
          else if (this.targettableArray[i].lhstype.text == "datapod") {
           
              lhsref["type"] = "datapod";
            
            let uuid = this.targettableArray[i].lhsdatapodAttribute.id.split("_")[0]
            var attrid = this.targettableArray[i].lhsdatapodAttribute.id.split("_")[1]
            lhsref["uuid"] = uuid
            //this.targettableArray[i].lhsdatapodAttribute.uuid;
            lhsoperand["ref"] = lhsref;
            lhsoperand["attributeId"] = attrid
          }
          else if (this.targettableArray[i].lhstype.text == "formula") {
  
            lhsref["type"] = "formula";
            lhsref["uuid"] = this.targettableArray[i].lhsformula.uuid;
            lhsoperand["ref"] = lhsref;
          }
          operand[0] = lhsoperand;
          if (this.targettableArray[i].rhstype.text == "string") {
  
            rhsref["type"] = "simple";
            rhsoperand["ref"] = rhsref;
            rhsoperand["value"] = this.targettableArray[i].rhsvalue;
          }
          else if (this.targettableArray[i].rhstype.text == "datapod") {

              rhsref["type"] = "datapod";
            
            rhsref["uuid"] = this.targettableArray[i].rhsdatapodAttribute.id.split("_")[0];
  
            rhsoperand["ref"] = rhsref;
            rhsoperand["attributeId"] = this.targettableArray[i].rhsdatapodAttribute.id.split("_")[1];
          }
          else if (this.targettableArray[i].rhstype.text == "formula") {
  
            rhsref["type"] = "formula";
            rhsref["uuid"] = this.targettableArray[i].rhsformula.uuid;
            rhsoperand["ref"] = rhsref;
          }
          operand[1] = rhsoperand;
          expressioninfo["operand"] = operand;
          expressioninfoArray1[i] = expressioninfo
          
        }
        var test1={}
        test1["filterInfo"]=expressioninfoArray1
        dqJson["targetfilter"]=test1
        dqJson["targetfilterChg"]='Y'
      }
      else{
        dqJson["targetfilter"]=null
        dqJson["targetfilterChg"]='Y'
      }
      console.log(JSON.stringify(dqJson))
      this._commonService.submit("reconview", dqJson).subscribe(
        response => { this.OnSuccessubmit(response) },
        error => console.log('Error :: ' + error)
      )
    }
    OnSuccessubmit(response){
      if (this.checkboxModelexecution == true) {
        this._commonService.getOneById("reconview",response).subscribe(
            response => {this.OnSucessGetOneById(response);
              this.goBack() },
            error => console.log('Error :: ' + error)
        )
      } //End if
      else{
        this.isSubmit="false";
        this.IsProgerssShow="false";
        this.msgs = [];
        this.msgs.push({severity:'success', summary:'Success Message', detail:'Recon Rule Save Successfully'});
        setTimeout(() => {
        this.goBack();
        
        }, 1000);
      }
    }
    
}
  
  