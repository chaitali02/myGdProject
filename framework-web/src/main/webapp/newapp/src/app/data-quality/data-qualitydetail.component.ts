
import {Component,Input,OnInit} from '@angular/core';
import {Router,Event as RouterEvent,ActivatedRoute,Params} from '@angular/router';
import { Location } from '@angular/common'; 
import {Message} from 'primeng/components/common/api';
import { SelectItem } from 'primeng/primeng';
import {MessageService} from 'primeng/components/common/messageservice';
  
import { CommonService } from '../metadata/services/common.service';
import { DataQualityService } from '../metadata/services/dataQuality.services';
import{ Version } from './../metadata/domain/version'
import{ DependsOn } from './dependsOn'
import {AttributeHolder} from './../metadata/domain/domain.attributeHolder'
  @Component({
    selector: 'app-data-pipeli',
    templateUrl: './data-qualitydetail.template.html',
    
    
  })
  
  export class DataQualityDetailComponent{
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
    filterTableArray: any;
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
    constructor(private _location: Location,private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _dataQualityService:DataQualityService){
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
      this.progressbarWidth=25*this.continueCount+"%";
      this.selectDataType="";
      this.selectdatefromate="";
      this.dataqualitycompare=null;
      this.filterTableArray=null;
      this.dqdata["active"]=true 
      this.breadcrumbDataFrom=[{

        "caption":"Data Quality",
        "routeurl":"/app/list/dq"
      },
        {
        "caption":"Rule",
        "routeurl":"/app/list/dq"
      },
      {
        "caption":"",
        "routeurl":null
      }
      ] 
      this.activatedRoute.params.subscribe((params : Params) => {
        this.id = params['id'];
        this.version = params['version'];
        this.mode = params['mode'];
        if(this.mode !== undefined) {
          this.getOneByUuidAndVersion(this.id,this.version);
          this.getAllVersionByUuid();
          this.getAllLatest()
        }
        else{
          this.getAllLatest()
        }
      });
    }
 
    public goBack() {
      //this._location.back();
      this.router.navigate(['app/list/dq']);
      
    }
    changeType(){
    
      this.selectAttribute=null;
      this.getAllAttributeBySource();
    }
    OnselectType = function() {
      if (this.selectDataType == "Date") {
        this.IsSelectDataType = true;
      } 
      else {
      this.IsSelectDataType=false;
      }
    }
    onSourceAttributeChagne = function() {
     
      if(this.selectAttribute != null) {
        this.IsSelectSoureceAttr = true
        this.dqdata.nullCheck = true;
        this.allRefIntegrity=this.allNames;
        this.allIntegrityAttribute=this.allAttribute;
      } 
      else {
        this.IsSelectSoureceAttr = false
        this.dqdata.nullCheck =false;
        this.dqdata.valueCheck = ""
        this.dqdata.lowerBound = "";
        this.dqdata.upperBound = "";
        this.selectDataType = "";
        this.selectdatefromate = "";
        this.dqdata.minLength = ""
        this.dqdata.maxLength = "";
        this.allRefIntegrity=[];
        this.selectRefIntegrity = "";
        this.allIntegrityAttribute=[];
        this.selectIntegrityAttribute = "";
      }
    }
    changeRefIntegrity(){

      this.allIntegrityAttribute=[]
      this._commonService.getAllAttributeBySource(this.selectRefIntegrity.uuid,this.source).subscribe(
        response => { 
          let temp=[];
          for (const n in response) {
            let allname={};
            allname["label"]=response[n]['dname'];
            allname["value"]={};
            allname["value"]["label"]=response[n]['dname'];      
            allname["value"]["u_Id"]=response[n]['id'];
            allname["value"]["uuid"]=response[n]['uuid'];
            allname["value"]["attrId"]=response[n]['attributeId'];
            temp[n]=allname
            //count=count+1;
          }
          this.allIntegrityAttribute=temp
        },
        error => console.log('Error :: ' + error)
      ) 
    }
    countContinue=function(){
      this.continueCount=this.continueCount+1;
      this.progressbarWidth=25*this.continueCount+"%"; 
    }

    countBack=function(){
      this.continueCount=this.continueCount-1;
      this.progressbarWidth=25*this.continueCount+"%";
    }
    addRow(){
      let newDataList=[];
      if(this.filterTableArray == null){
        this.filterTableArray=[];
      }
      else{
        newDataList=this.filterTableArray
      }
      var len=this.filterTableArray.length+1
      var filertable={};
      filertable["logicalOperator"]=" ";
      let  lhsFilter : AttributeHolder = new AttributeHolder();
      lhsFilter.label=this.lhsdatapodattributefilter[0].label;
      lhsFilter.u_Id=this.lhsdatapodattributefilter[0].value.u_Id;
      lhsFilter.uuid=this.lhsdatapodattributefilter[0].value.uuid;
      lhsFilter.attrId=this.lhsdatapodattributefilter[0].value.attrId;
      filertable["lhsFilter"]=lhsFilter;
      filertable["operator"]=this.operators[0]
      filertable["filtervalue"]=" "
      newDataList.splice(this.filterTableArray.length, 0,filertable);
      this.filterTableArray=newDataList;
    }
    removeRow(){
      let newDataList=[];
      this.selectedAllFitlerRow=false;
      this.filterTableArray.forEach(selected => {
        if(!selected.selected){
          newDataList.push(selected);
        }
      });
      if(newDataList.length >0){
       newDataList[0].logicalOperator="";
      }
     this.filterTableArray = newDataList;
    }
    checkAllFilterRow(){
      if (!this.selectedAllFitlerRow){
        this.selectedAllFitlerRow = true;
        }
      else {
        this.selectedAllFitlerRow = false;
        }
        this.filterTableArray.forEach(filter => {
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
      this.getAllAttributeBySource();
      if(this.mode != undefined  && this.IsSelectSoureceAttr) {
        this.allRefIntegrity=this.allNames;
        this.changeRefIntegrity();
        
      }
    }
    getAllAttributeBySource(){
      this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
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
          allname["label"]=response[n]['dname'];
          allname["value"]={};
          allname["value"]["label"]=response[n]['dname'];      
          allname["value"]["u_Id"]=response[n]['id'];
          allname["value"]["uuid"]=response[n]['uuid'];
          allname["value"]["attrId"]=response[n]['attributeId'];
          temp[n]=allname
          attribute[n]=allname

          //count=count+1;
        }
        this.allAttribute=temp
        this.lhsdatapodattributefilter=attribute
        this.allAttribute.splice(0, 0, allname);
       // this.lhsdatapodattributefilter.splice(0,1);
      }
    getOneByUuidAndVersion(id,version){
      this._dataQualityService.getOneByUuidAndVersion(id,version,'dq')
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
      this.filterTableArray=response.filterInfo
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
      dependOnTemp.label = response.dqdata["dependsOn"]["ref"]["name"];
      dependOnTemp.uuid = response.dqdata["dependsOn"]["ref"]["uuid"];
      this.sourcedata=dependOnTemp;
      if(response.dqdata["attribute"] !=null){
        this.IsSelectSoureceAttr=true
        let selectattribute : AttributeHolder = new AttributeHolder();
        selectattribute.label =response.dqdata["attribute"]["ref"]["name"]+"."+response.dqdata["attribute"]["attrName"];
        selectattribute.u_Id=response.dqdata["attribute"]["ref"]["uuid"]+"_"+response.dqdata["attribute"]["attrId"]
        selectattribute.uuid=response.dqdata["attribute"]["ref"]["uuid"];
        selectattribute.attrId=response.dqdata["attribute"]["attrId"];
        this.selectAttribute=selectattribute;
        
      }
      this.dqdata.duplicateKeyCheck=response.dqdata["duplicateKeyCheck"] =="Y"?true:false;
      this.dqdata.nullCheck=response.dqdata["nullCheck"] =="Y"?true:false;
      this.dqdata.upperBound = response.dqdata.rangeCheck.upperBound;
      this.dqdata.lowerBound = response.dqdata.rangeCheck.lowerBound;
      this.dqdata.maxLength = response.dqdata.lengthCheck.maxLength;
      this.dqdata.minLength = response.dqdata.lengthCheck.minLength;
      if (response.dqdata.refIntegrityCheck.ref != null) {
        let selectrefIntegrity: DependsOn = new DependsOn();
        selectrefIntegrity.label = response.dqdata.refIntegrityCheck.ref.name;
        selectrefIntegrity.uuid = response.dqdata.refIntegrityCheck.ref.uuid;
        this.selectRefIntegrity=selectrefIntegrity
        let selectintegrityattribute : AttributeHolder = new AttributeHolder();
        selectintegrityattribute.label = response.dqdata.refIntegrityCheck.ref.name;
        selectintegrityattribute.u_Id = response.dqdata.refIntegrityCheck.ref.uuid+"_"+response.dqdata.refIntegrityCheck.attrId;
        selectintegrityattribute.uuid = response.dqdata.refIntegrityCheck.ref.uuid
        selectintegrityattribute.attrId = response.dqdata.refIntegrityCheck.attrId
        this.selectIntegrityAttribute=selectintegrityattribute;
      }
    }
    
    getAllVersionByUuid(){
      this._commonService.getAllVersionByUuid('dq',this.id)
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
    
    dagSubmit(){
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
      let  dependsOn = {};
      let ref={};
      ref["type"] = this.source
      ref["uuid"] = this.sourcedata.uuid;
      dependsOn["ref"] = ref;
      dqJson["dependsOn"]=dependsOn;      
      if(this.selectAttribute !=null){
        let attributeref={};
        let attribute={};
        attributeref["type"] = "datapod";
        attributeref["uuid"] =this.selectAttribute.uuid;
        attribute["ref"] = attributeref;
        attribute["attrId"] =this.selectAttribute.attrId;
        dqJson["attribute"] = attribute;
        }
      else{
        dqJson["attribute"]=null;

      }
      dqJson["duplicateKeyCheck"] =this.dqdata.duplicateKeyCheck== true ?'Y':'N';
      dqJson["nullCheck"] =this.dqdata.nullCheck == true ?'Y':'N';
      var tagArrayvaluecheck = [];
      if ( this.valueCheck && this.valueCheck.length > 0) {
        for (var counttag = 0; counttag < this.valueCheck.length; counttag++) {
          tagArrayvaluecheck[counttag] =this.valueCheck[counttag]
        }
      }
       dqJson["valueCheck"] = tagArrayvaluecheck
      var rengeCheck = {};
      if( typeof this.dqdata.lowerBound !="undefined" && typeof this.dqdata.upperBound !="undefined"){
        rengeCheck["lowerBound"] = this.dqdata.lowerBound;
        rengeCheck["upperBound"] = this.dqdata.upperBound;
      }
      dqJson["rangeCheck"] = rengeCheck;
      dqJson["dataTypeCheck"] = this.selectDataType;
      dqJson["dateFormatCheck"] =this.selectdatefromate;
      dqJson["customFormatCheck"] = this.dqdata.customFormatCheck
      var lengthCheck = {}
      if(typeof this.dqdata.minLength !="undefined" && typeof this.dqdata.minLength !="undefined"){
        lengthCheck["minLength"] = this.dqdata.minLength;
        lengthCheck["maxLength"] =this.dqdata.maxLength;
        
      }
      dqJson["lengthCheck"] = lengthCheck
      let refIntegrityCheck = {};
      let refInte = {};
      if (typeof this.selectRefIntegrity != "undefined" && typeof this.selectIntegrityAttribute !="undefined") {
        ref["type"] = "datapod";
        ref["uuid"] = this.selectRefIntegrity.uuid;
        refIntegrityCheck["ref"] = ref;
        refIntegrityCheck["attrId"] = this.selectIntegrityAttribute.attrId;
        dqJson["refIntegrityCheck"] = refIntegrityCheck;
      } else {
        dqJson["refIntegrityCheck"] = {};
      }


      var filterInfoArray = [];
      var filter = {}
      if (this.dataqualitycompare != null) {
        if (this.dataqualitycompare.filter != null) {
          filter["uuid"]      = this.dataqualitycompare.filter.uuid;
          filter["name"]      = this.dataqualitycompare.filter.name;
          filter["createdBy"] = this.dataqualitycompare.filter.createdBy;
          filter["createdOn"] = this.dataqualitycompare.filter.createdOn;
          filter["active"]    = this.dataqualitycompare.filter.active;
          filter["tags"]      = this.dataqualitycompare.filter.tags;
          filter["desc"]      = this.dataqualitycompare.filter.desc;
          filter["dependsOn"] = this.dataqualitycompare.filter.dependsOn;
        }
      }
      if (this.filterTableArray != null) {
        if (this.filterTableArray.length > 0) {
          for (var i = 0; i < this.filterTableArray.length; i++) {
  
            if (this.dataqualitycompare != null && this.dataqualitycompare.filter != null && this.dataqualitycompare.filter.filterInfo.length == this.filterTableArray.length) {
              if (this.dataqualitycompare.filter.filterInfo[i].operand[0].attributeId != this.filterTableArray[i].lhsFilter.attributeId ||
                this.filterTableArray[i].logicalOperator != this.dataqualitycompare.filter.filterInfo[i].logicalOperator ||
                this.filterTableArray[i].filtervalue != this.dataqualitycompare.filter.filterInfo[i].operand[1].value ||
                this.filterTableArray[i].operator != this.dataqualitycompare.filter.filterInfo[i].operator) {
  
                  dqJson["filterChg"] = "y";
  
              } else {
  
                dqJson["filterChg"] = "n";
              }
  
            } else {
  
              dqJson["filterChg"] = "y";
            }
            var filterInfo = {};
            var operand = [];
            var operandfirst = {};
            var reffirst = {};
            var operandsecond = {};
            var refsecond = {};
            reffirst["type"] = "datapod"
            reffirst["uuid"] = this.filterTableArray[i].lhsFilter.uuid
            operandfirst["ref"] = reffirst;
            operandfirst["attributeId"] = this.filterTableArray[i].lhsFilter.attrId
            operand[0] = operandfirst;
            refsecond["type"] = "simple";
            operandsecond["ref"] = refsecond;
            if (typeof this.filterTableArray[i].filtervalue == "undefined") {
              operandsecond["value"] = "";
            } else {
  
              operandsecond["value"] = this.filterTableArray[i].filtervalue
            }
  
            operand[1] = operandsecond;
            if (typeof this.filterTableArray[i].logicalOperator == "undefined") {
              filterInfo["logicalOperator"] = "";
            } else {
              filterInfo["logicalOperator"] = this.filterTableArray[i].logicalOperator
            }
            filterInfo["operator"] = this.filterTableArray[i].operator
            filterInfo["operand"] = operand;
            filterInfoArray[i] = filterInfo;
          }
          filter["filterInfo"] = filterInfoArray;
          dqJson["filter"] = filter;
        } else {
  
          dqJson["filter"] = null;
          dqJson["filterChg"] = "n";
        }
      } else {
        dqJson["filter"] = null;
        dqJson["filterChg"] = "n";
  
      }

      console.log(dqJson);
      this._commonService.submit("dqview",dqJson).subscribe(
        response => { this.OnSuccessubmit(response)},
        error => console.log('Error :: ' + error)
      )
    }  
    OnSuccessubmit(response){
      if (this.checkboxModelexecution == true) {
        this._commonService.getOneById("dq",response).subscribe(
            response => {this.OnSucessGetOneById(response);
              this.goBack() },
            error => console.log('Error :: ' + error)
        )
      } //End if
      else{
        this.isSubmit="false";
        this.IsProgerssShow="false";
        this.msgs = [];
        this.msgs.push({severity:'success', summary:'Success Message', detail:'DQ Save Successfully'});
        setTimeout(() => {
        this.goBack();
        
        }, 1000);
      }
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
      this.router.navigate(['app/dataQuality/dq',uuid,version, 'false']);
    }

    showview(uuid, version) {
      this.router.navigate(['app/dataQuality/dq',uuid,version, 'true']);
    }
}
  
  