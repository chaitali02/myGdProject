import { CommonService } from './../../metadata/services/common.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { AppConfig } from './../../app.config';
import { Component, OnInit,ViewEncapsulation } from '@angular/core';
import { Location } from '@angular/common';
import { DatasetService } from '../../metadata/services/dataset.service';
import { NgOption} from '@ng-select/ng-select';
import{ Version } from '../../shared/version'
import { SelectItem } from 'primeng/primeng';
import{ DependsOn } from './dependsOn'
import {AppMetadata} from '../../app.metadata';


@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.template.html',
  styleUrls: ['./dataset.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DatasetComponent implements OnInit {
  selectVersion: any;
  msgs: any[];
  datasetCompare: any;
  selectRelation: string;
  selectAllAttributeRow: any;
  ruleLoadFunction: any;
  allMapExpression: any;
  allMapFormula: any;
  sourceAttributeTypes: { "value": string; "label": string; }[];
  attributeTableArray: any[];
  selectedAllFitlerRow: boolean;
  filterTableArray: any[];
  sources: { 'value': string; 'label': string; }[];
  allNames: SelectItem[] = [];
  sourcedata: DependsOn
  source: any;
  versions: any[];
  dataset: any;
  lhsdatapodattributefilter: any;
  showDataset: boolean;
  logicalOperators: any;
  tags: any;
  desc: any;
  createdOn: any;
  createdBy: any;
  name: any;
  id: any;
  mode: any;
  version: any;
  uuid: any;
  active: any;
  routerUrl:any;
  published: any;
  breadcrumbDataFrom:any;
  depends: any;
  allName : NgOption[];data
  operators: any;
  allMapSourceAttribute: SelectItem[] = [];
  VersionList: SelectItem[] = [];
  selectedVersion: Version
 isSubmitEnable:any;

  constructor(private _location: Location,config: AppConfig,private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,  private _datasetService:DatasetService,private activeroute:ActivatedRoute) { 
    this.showDataset = true;
    this.dataset = {};
    this.dataset["active"]=true
    this.isSubmitEnable=true;
    this.filterTableArray=[]
    this.sourcedata={'uuid':"","label":""}
    this.selectVersion={"version":""};
    this.logicalOperators=["","AND","OR"]
    this.operators=["=", "<", ">", "<=", ">=", "BETWEEN"];
    this.uuid = '';
    
    this.breadcrumbDataFrom=[{
      "caption":"Data Preparation ",
      "routeurl":"/app/list/dataset"
    },
    {
      "caption":"Dataset",
      "routeurl":"/app/list/dataset"
    },
    {
      "caption":"",
      "routeurl":null
    }
    ]
    this.sources = [
      {'value': 'datapod', 'label': 'datapod'},
      {'value': 'relation', 'label': 'relation'}
    ]; 
    this.sourceAttributeTypes=[
      {"value":"function","label":"function"},
      {"value":"string","label":"string"},
      {"value":"datapod","label":"attribute"},
      {"value":"expression","label":"expression"},
      {"value":"formula","label":"formula"}
    ]
    this.attributeTableArray=[]
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params : Params) => {
      this.id = params['id'];
      this.version = params['version'];
      this.mode = params['mode'];
      if(this.mode !== undefined) {
      this.getOneByUuidAndVersion(this.id,this.version);
      this.getAllVersionByUuid();
      }
    })
  }
  public goBack() { 
    //this._location.back();
    this.router.navigate(['app/list/dataset']);
    }
  onChangeActive(event) {
    if(event === true) {
      this.dataset.active = 'Y';
    }
    else {
      this.dataset.active = 'N';
    }
  }
  onChangePublish(event) {
    if(event === true) {
      this.dataset.published = 'Y';
    }
    else {
      this.dataset.published = 'N';
    }
  }
  getOneByUuidAndVersion(id,version){
    this._commonService.getOneByUuidAndVersion(id,version,'datasetview')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('dataset',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
    
  onSuccessgetOneByUuidAndVersion(response){
    this.dataset=response;
    this.datasetCompare=response;
    this.uuid=response.uuid;
    
    this.createdBy=response.createdBy.ref.name;
    this.dataset.published=response["published"] == 'Y' ? true : false
    this.dataset.active=response["active"] == 'Y' ? true : false
    const version: Version = new Version();
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    this.breadcrumbDataFrom[2].caption=this.dataset.name;
    this.source = response["dependsOn"]["ref"]["type"]
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.sourcedata=dependOnTemp
    let filterjson={};
    filterjson["filter"]=response;
    let filterInfoArray=[];
    this._commonService.getAllLatest(this.source).subscribe(
      response => { this.OnSuccesgetAllLatest(response)},
      error => console.log('Error :: ' + error)
    ) 
    if(response.filter!=null){
      for(let k=0;k<response.filter.filterInfo.length;k++){
     let filterInfo={};
     let lhsFilter={};
     lhsFilter["uuid"]=response.filter.filterInfo[k].operand[0].ref.uuid
     lhsFilter["datapodname"]=response.filter.filterInfo[k].operand[0].ref.name
     lhsFilter["attributeId"]=response.filter.filterInfo[k].operand[0].attributeId;
     lhsFilter["name"]=response.filter.filterInfo[k].operand[0].attributeName;
     lhsFilter["dname"]=lhsFilter["datapodname"]+"."+lhsFilter["name"];
     lhsFilter["id"]=lhsFilter["uuid"]+"_"+lhsFilter["attributeId"];
     filterInfo["logicalOperator"]=response.filter.filterInfo[k].logicalOperator
     filterInfo["lhsFilter"]=lhsFilter;
     filterInfo["operator"]=response.filter.filterInfo[k].operator;
     filterInfo["filtervalue"]=response.filter.filterInfo[k].operand[1].value;
     filterInfoArray.push(filterInfo);
      }
   }
    filterjson["filterInfo"]=filterInfoArray
    this.filterTableArray=filterInfoArray
    let attributeJson={};
    attributeJson["attributeData"]=response;
    let attributearray=[];
    for(let i=0;i<response.attributeInfo.length;i++){
      let attributeinfojson={};
      attributeinfojson["name"]=response.attributeInfo[i].attrSourceName
      if(response.attributeInfo[i].sourceAttr.ref.type =="datapod" || response.attributeInfo[i].sourceAttr.ref.type=="dataset" || response.attributeInfo[i].sourceAttr.ref.type=="rule"){
          var sourceattribute={}
          sourceattribute["uuid"]=response.attributeInfo[i].sourceAttr.ref.uuid;
          sourceattribute["name"]=response.attributeInfo[i].sourceAttr.ref.name;
          sourceattribute["dname"]=response.attributeInfo[i].sourceAttr.ref.name+'.'+response.attributeInfo[i].sourceAttr.attrName;
          sourceattribute["type"]=response.attributeInfo[i].sourceAttr.ref.type;
          sourceattribute["attributeId"]=response.attributeInfo[i].sourceAttr.attrId;
          sourceattribute["id"]=sourceattribute["uuid"]+"_"+sourceattribute["attributeId"]
          let obj={}
            obj["value"]="datapod"
            obj["label"]="attribute"
          attributeinfojson["sourceAttributeType"]=obj;
          attributeinfojson["isSourceAtributeSimple"]=false;
          attributeinfojson["isSourceAtributeDatapod"]=true;
          attributeinfojson["isSourceAtributeFormula"]=false;
          attributeinfojson["isSourceAtributeExpression"]=false;
      }
      else if(response.attributeInfo[i].sourceAttr.ref.type == "simple"){
            let obj={}
            obj["value"]="string"
            obj["label"]="string"
            attributeinfojson["sourceAttributeType"]=obj;
            attributeinfojson["isSourceAtributeSimple"]=true;
            attributeinfojson["sourcesimple"]=response.attributeInfo[i].sourceAttr.value
            attributeinfojson["isSourceAtributeDatapod"]=false;
            attributeinfojson["isSourceAtributeFormula"]=false;
            attributeinfojson["isSourceAtributeExpression"]=false;
            attributeinfojson["isSourceAtributeFunction"]=false;

          }
      if(response.attributeInfo[i].sourceAttr.ref.type  == "expression"){
            let sourceexpression={};
            sourceexpression["uuid"]=response.attributeInfo[i].sourceAttr.ref.uuid;
            sourceexpression["name"]=response.attributeInfo[i].sourceAttr.ref.name
            let obj={}
            obj["value"]="expression"
            obj["label"]="expression"
            attributeinfojson["sourceAttributeType"]=obj;
            attributeinfojson["sourceexpression"]=sourceexpression;
            attributeinfojson["isSourceAtributeSimple"]=false;
            attributeinfojson["isSourceAtributeDatapod"]=false;
            attributeinfojson["isSourceAtributeFormula"]=false;
            attributeinfojson["isSourceAtributeExpression"]=true;
            attributeinfojson["isSourceAtributeFunction"]=false;
            this.getAllExpression(false,0)
          }
       if(response.attributeInfo[i].sourceAttr.ref.type == "formula"){
            let sourceformula={};
            sourceformula["uuid"]=response.attributeInfo[i].sourceAttr.ref.uuid;
            sourceformula["name"]=response.attributeInfo[i].sourceAttr.ref.name;
            let obj={}
            obj["value"]="formula"
          obj["label"]="formula"
          attributeinfojson["sourceAttributeType"]=obj;
            attributeinfojson["sourceformula"]=sourceformula;
            attributeinfojson["isSourceAtributeSimple"]=false;
            attributeinfojson["isSourceAtributeDatapod"]=false;
            attributeinfojson["isSourceAtributeFormula"]=true;
            attributeinfojson["isSourceAtributeExpression"]=false;
            attributeinfojson["isSourceAtributeFunction"]=false;
            this.getAllFormula(false,0);
          }
       if(response.attributeInfo[i].sourceAttr.ref.type == "function"){
            let sourcefunction={};
            sourcefunction["uuid"]=response.attributeInfo[i].sourceAttr.ref.uuid;
            sourcefunction["name"]=response.attributeInfo[i].sourceAttr.ref.name
            let obj={}
            obj["value"]="function"
            obj["label"]="function"
            attributeinfojson["sourceAttributeType"]=obj;
            attributeinfojson["sourcefunction"]=sourcefunction;
            attributeinfojson["isSourceAtributeSimple"]=false;
            attributeinfojson["isSourceAtributeDatapod"]=false;
            attributeinfojson["isSourceAtributeFormula"]=false;
            attributeinfojson["isSourceAtributeExpression"]=false;
            attributeinfojson["isSourceAtributeFunction"]=true;
            this.getAllFunctions(false,0);
          }
      attributeinfojson["sourceattribute"]=sourceattribute;
      attributearray[i]=attributeinfojson
    }
    this.attributeTableArray=attributearray
    console.log(this.attributeTableArray)
    
  }

  OnSuccesgetAllVersionByUuid(response) {
    for (const i in response) {
      let ver={};
      ver["label"]=response[i]['version'];
      ver["value"]={};
      ver["value"]["label"]=response[i]['version'];      
      ver["value"]["uuid"]=response[i]['uuid']; 
      //allName["uuid"]=response[i]['uuid']
      this.VersionList[i]=ver;
    }
  }
  onVersionChange(){
    this.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label);
  }
  selectSourceType(){
    this._commonService.getAllLatest(this.source).subscribe(
      response => { 
        this.OnSuccesgetAllLatest(response)},
      error => console.log('Error :: ' + error)
    ) 
  }
  changeType(){
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
      response => { 
        this.OnSuccesgetAllAttributeBySource(response)},
      error => console.log('Error :: ' + error)
    ) 
  }
  OnSuccesgetAllLatest(response1){
    // let dependOnTemp: DependsOn = new DependsOn();
    // dependOnTemp.label =response1[0]["name"];
    // dependOnTemp.uuid = response1[0]["uuid"];
    // this.sourcedata=dependOnTemp
    let temp=[]
    for (const n in response1) {
      let allname={};
      allname["label"]=response1[n]['name'];
      allname["value"]={};
      allname["value"]["label"]=response1[n]['name'];      
      allname["value"]["uuid"]=response1[n]['uuid'];
      temp[n]=allname;
    }
    this.allNames = temp
    this.getAllAttributeBySource()
  }
  getAllAttributeBySource(){
    this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
      response => { this.OnSuccesgetAllAttributeBySource(response)},
      error => console.log('Error :: ' + error)
    ) 
  }
    OnSuccesgetAllAttributeBySource(response){
      //console.log(response)
      this.lhsdatapodattributefilter = response;
      let temp=[]
      for (const n in response) {
        let allname={};
        allname["label"]=response[n]['dname'];
        allname["value"]={};
        allname["value"]["label"]=response[n]['dname'];      
        allname["value"]["id"]=response[n]['id'];
        temp[n]=allname;
      }
      this.allMapSourceAttribute = temp
    }
    onChangeSourceAttribute(type,index){
      if(type == "string"){
        this.attributeTableArray[index].isSourceAtributeSimple=true;
        this.attributeTableArray[index].isSourceAtributeDatapod=false;
        this.attributeTableArray[index].isSourceAtributeFormula=false;
        this.attributeTableArray[index].sourcesimple="''";
        this.attributeTableArray[index].isSourceAtributeExpression=false;
        this.attributeTableArray[index].isSourceAtributeFunction=false;
      }
      else if(type == "datapod"){
        this.attributeTableArray[index].isSourceAtributeSimple=false;
        this.attributeTableArray[index].isSourceAtributeDatapod=true;
        this.attributeTableArray[index].isSourceAtributeFormula=false;
        this.attributeTableArray[index].isSourceAtributeExpression=false;
        this.attributeTableArray[index].isSourceAtributeFunction=false;
        this.attributeTableArray[index].sourceattribute={}
        this.getAllAttributeBySource();
        if(this.allMapSourceAttribute && this.allMapSourceAttribute.length > 0){
          let sourceattribute={}
          sourceattribute["dname"]=this.allMapSourceAttribute[0]["label"]
          sourceattribute["id"]=this.allMapSourceAttribute[0]["value"]["id"];
          this.attributeTableArray[index].sourceattribute=sourceattribute;
        }
      }
      else if(type =="formula"){
        this.attributeTableArray[index].isSourceAtributeSimple=false;
        this.attributeTableArray[index].isSourceAtributeDatapod=false;
        this.attributeTableArray[index].isSourceAtributeFormula=true;
        this.attributeTableArray[index].isSourceAtributeExpression=false;
        this.attributeTableArray[index].isSourceAtributeFunction=false;
        this.attributeTableArray[index].sourceformula={}
        this.getAllFormula(true,index);
       
      }
      else if(type =="expression"){  
        this.attributeTableArray[index].isSourceAtributeSimple=false;
        this.attributeTableArray[index].isSourceAtributeDatapod=false;
        this.attributeTableArray[index].isSourceAtributeFormula=false;
        this.attributeTableArray[index].isSourceAtributeExpression=true;
        this.attributeTableArray[index].isSourceAtributeFunction=false;
        this.attributeTableArray[index].sourceexpression={}
        this.getAllExpression(true,index);
       
      }
      else if(type =="function"){
        this.attributeTableArray[index].isSourceAtributeSimple=false;
        this.attributeTableArray[index].isSourceAtributeDatapod=false;
        this.attributeTableArray[index].isSourceAtributeFormula=false;
        this.attributeTableArray[index].isSourceAtributeExpression=false;
        this.attributeTableArray[index].isSourceAtributeFunction=true;
        this.attributeTableArray[index].isSourceAtributeFunction=true;
        this.attributeTableArray[index].sourcefunction={}
        this.getAllFunctions(true,index);
      }
    }
    getAllFunctions(defaulfMode,index){
    this._datasetService.getAllLatestFunction("function","N").subscribe(
      response => { this.onSuccessFunction(response,defaulfMode,index)},
      error => console.log('Error :: ' + error)
    ) 
    }
    onSuccessFunction(response,defaulfMode,index){
      let temp=[]
      for (const n in response) {
        let allname={};
        allname["label"]=response[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response[n]['name'];      
        allname["value"]["uuid"]=response[n]['uuid'];
        temp[n]=allname;
      }
      this.ruleLoadFunction = temp
      if(defaulfMode ==true){
        let sourcefunction={};
        sourcefunction["uuid"]=this.ruleLoadFunction[0]["value"].uuid;
        sourcefunction["name"]=this.ruleLoadFunction[0].label;
        this.attributeTableArray[index].sourcefunction=sourcefunction;
      }
    } 
    getAllExpression(defaulfMode,index){
      this._datasetService.getExpressionByType(this.sourcedata.uuid,this.source).subscribe(
        response => { this.onSuccessExpression(response,defaulfMode,index)},
        error => console.log('Error :: ' + error)
      ) 
    }
    onSuccessExpression(response,defaulfMode,index){
      let temp=[]
      for (const n in response) {
        let allname={};
        allname["label"]=response[n]['name'];
        allname["value"]={};
        allname["value"]["label"]=response[n]['name'];      
        allname["value"]["uuid"]=response[n]['uuid'];
        temp[n]=allname;
      }
      this.allMapExpression = temp;
      if(defaulfMode ==true){
        let sourceexpression={};
        sourceexpression["uuid"]=this.allMapExpression[0]["value"].uuid;
        sourceexpression["name"]=this.allMapExpression[0].label;
        this.attributeTableArray[index].sourceexpression=sourceexpression;
      }
    } 
    getAllFormula(defaulfMode,index){
      this._commonService.getFormulaByType(this.sourcedata.uuid,"formula").subscribe(
        response => { this.onSuccessgetAllFormula(response,defaulfMode,index)},
        error => console.log('Error :: ' + error)
      ) 
    }
    onSuccessgetAllFormula(response,defaulfMode,index){
    //this.allMapFormula = response
    let temp=[]
    for (const n in response) {
      let allname={};
      allname["label"]=response[n]['name'];
      allname["value"]={};
      allname["value"]["label"]=response[n]['name'];      
      allname["value"]["uuid"]=response[n]['uuid'];
      temp[n]=allname;
    }
    this.allMapFormula = temp;
    if(defaulfMode ==true){
      let sourceformula={};
      sourceformula["uuid"]=this.allMapFormula[0]["value"].uuid;
      sourceformula["name"]=this.allMapFormula[0].label;
      this.attributeTableArray[index].sourceformula=sourceformula;
    }
    }
    addRow(){
      if(this.filterTableArray == null){
        this.filterTableArray=[];
      }
      var len=this.filterTableArray.length+1
      var filertable={};
      filertable["logicalOperator"]=" ";
      filertable["lhsFilter"]=this.lhsdatapodattributefilter[0];
      //filertable["id"]=len-1;
      filertable["operator"]=this.operators[0]
      filertable["filtervalue"]=" "
      this.filterTableArray.splice(this.filterTableArray.length, 0,filertable);
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
    checkAllAttributeRow(){
      if (!this.selectAllAttributeRow){
        this.selectAllAttributeRow = true;
        }
      else {
        this.selectAllAttributeRow = false;
        }
      this.attributeTableArray.forEach(attribute => {
        attribute.selected = this.selectAllAttributeRow;
      });
    }
    addAttribute(){
      if(this.attributeTableArray == null){
         this.attributeTableArray=[];
      }
      let len=this.attributeTableArray.length+1
      let attrinfo={};
      attrinfo["name"]="attribute"+len;
      attrinfo["id"]=len-1;
      attrinfo["sourceAttributeType"]={"value":"string","label":"string"};
      attrinfo["isSourceAtributeSimple"]=true;
      attrinfo["isSourceAtributeDatapod"]=false;
      this.attributeTableArray.splice(this.attributeTableArray.length, 0,attrinfo);
    }
    removeAttribute(){
      var newDataList=[];
      this.selectAllAttributeRow=false
      this.attributeTableArray.forEach(selected => {
        if(!selected.selected){
          newDataList.push(selected);
        }
      });
      this.attributeTableArray = newDataList;
    }
    onChangeAttributeDatapod(data,index){
      if(data !=null){
        this.attributeTableArray[index].name=data.label.split(".")[1]
      }
    }
    onChangeFormula(data,index){
      this.attributeTableArray[index].name=data.name
    }
    onChangeExpression(data,index){
      this.attributeTableArray[index].name=data.name
    }
    submitDataset(){
      this.isSubmitEnable=true;
      let datasetJson={};
      datasetJson["uuid"]=this.dataset.uuid
      datasetJson["name"]=this.dataset.name
      datasetJson["srcChg"]="y";
      if(this.datasetCompare == null){
        datasetJson["srcChg"]="y";
        datasetJson["sourceChg"]="y";
        datasetJson["filterChg"]="y";
      }else{
        datasetJson["mapInfo"]=this.datasetCompare.mapInfo
      }
      
      var tagArray=[];
      if(this.dataset.tags !=null){
        for(var counttag=0;counttag<this.dataset.tags.length;counttag++){
          tagArray[counttag]=this.dataset.tags[counttag];
        }
      }
      
      datasetJson["tags"]=tagArray;
      datasetJson["desc"]=this.dataset.desc
      let dependsOn={};
      let ref={}
      ref["type"]=this.source
      ref["uuid"]=this.sourcedata.uuid
      dependsOn["ref"]=ref;
      datasetJson["dependsOn"] = dependsOn;
      datasetJson["active"]=this.dataset.active == true ?'Y' :"N"
      datasetJson["published"]=this.dataset.published == true ?'Y' :"N"
      if( this.datasetCompare != null && this.datasetCompare.dependsOn.ref.uuid !=this.sourcedata.uuid){
        datasetJson["sourceChg"]="y";
      }
      else{
        datasetJson["sourceChg"]="n";
      }
       
     //filterInfo
     var filterInfoArray=[];
     var filter={}
     if(this.datasetCompare != null && this.datasetCompare.filter !=null ){
     filter["uuid"]=this.datasetCompare.filter.uuid;
     filter["name"]=this.datasetCompare.filter.name;
     filter["version"]=this.datasetCompare.filter.version;
     filter["createdBy"]=this.datasetCompare.filter.createdBy;
     filter["createdOn"]=this.datasetCompare.filter.createdOn;
     filter["active"]=this.datasetCompare.filter.active;
     filter["tags"]=this.datasetCompare.filter.tags;
     filter["desc"]=this.datasetCompare.filter.desc;
     filter["dependsOn"]=this.datasetCompare.filter.dependsOn;
     }
     if(this.filterTableArray.length >0 ){
     for(var i=0;i<this.filterTableArray.length;i++){
        if(this.datasetCompare != null &&  this.datasetCompare.filter !=null && this.datasetCompare.filter.filterInfo.length == this.filterTableArray.length){
          if(this.datasetCompare.filter.filterInfo[i].operand[0].attributeId != this.filterTableArray[i].lhsFilter.attributeId
              || this.filterTableArray[i].logicalOperator !=this.datasetCompare.filter.filterInfo[i].logicalOperator
              || this.filterTableArray[i].filtervalue !=this.datasetCompare.filter.filterInfo[i].operand[1].value
              || this.filterTableArray[i].operator !=this.datasetCompare.filter.filterInfo[i].operator){
                datasetJson["filterChg"]="y";
          }
          else{
            datasetJson["filterChg"]="n";
          }
        }
        else{
          datasetJson["filterChg"]="y";
        }
       var filterInfo={};
       var operand=[];
       var operandfirst={};
       var reffirst={};
       var operandsecond={};
       var refsecond={};
       reffirst["type"]="datapod"
       let  Filteruuid=this.filterTableArray[i].lhsFilter.uuid
       let Filterattrid=this.filterTableArray[i].lhsFilter.attributeId
       reffirst["uuid"]=Filteruuid
       operandfirst["ref"]=reffirst;
       operandfirst["attributeId"]=Filterattrid
       operand[0]=operandfirst;
       refsecond["type"]="simple";
       operandsecond["ref"]=refsecond;
       if(typeof this.filterTableArray[i].filtervalue == "undefined"){
         operandsecond["value"]="";
       }
       else{

         operandsecond["value"]=this.filterTableArray[i].filtervalue
       }

       operand[1]=operandsecond;
       if (typeof this.filterTableArray[i].logicalOperator == "undefined") {
         filterInfo["logicalOperator"]=""
       }
       else{
         filterInfo["logicalOperator"]=this.filterTableArray[i].logicalOperator
       }
       filterInfo["operator"]=this.filterTableArray[i].operator
       filterInfo["operand"]=operand;

       filterInfoArray[i]=filterInfo;

     }//End FilterInfo
     filter["filterInfo"]=filterInfoArray;
     datasetJson["filter"]=filter;
     }
     else{
      datasetJson["filter"]=null;
      datasetJson["filterChg"]="y";
     }
     var sourceAttributesArray=[];
    for(var i=0;i<this.attributeTableArray.length;i++){
          var  attributemap={};
          attributemap["attrSourceId"]=i;
          attributemap["attrSourceName"]=this.attributeTableArray[i].name
          //attributeinfo.attrSourceName=$scope.attributeTableArray[l].name
          var sourceAttr={};
          var sourceref={};
          if(this.attributeTableArray[i].sourceAttributeType.value == "string"){
            sourceref["type"]="simple";
            sourceAttr["ref"]=sourceref;
            if(typeof this.attributeTableArray[i].sourcesimple == "undefined"){
              sourceAttr["value"]="";
            }
            else{
              sourceAttr["value"]=this.attributeTableArray[i].sourcesimple;
            }
            attributemap["sourceAttr"]=sourceAttr;
          }
          else if(this.attributeTableArray[i].sourceAttributeType.value == "datapod"){
            let  uuid=this.attributeTableArray[i].sourceattribute.id.split("_")[0]
            var attrid=this.attributeTableArray[i].sourceattribute.id.split("_")[1]
            sourceref["uuid"]=uuid;
            if(this.source == "relation"){
            sourceref["type"]="datapod";
            }
            else{
              sourceref["type"]=this.source;
            }
            sourceAttr["ref"]=sourceref;
            sourceAttr["attrId"]=attrid;
            attributemap["sourceAttr"]=sourceAttr;
          }
          else if(this.attributeTableArray[i].sourceAttributeType.value == "expression"){
          sourceref["type"]="expression";
          sourceref["uuid"]=this.attributeTableArray[i].sourceexpression.uuid;
           sourceAttr["ref"]=sourceref;
           attributemap["sourceAttr"]=sourceAttr;
     
          }
          else if(this.attributeTableArray[i].sourceAttributeType.value == "formula"){
            sourceref["type"]="formula";
            sourceref["uuid"]=this.attributeTableArray[i].sourceformula.uuid;
            sourceAttr["ref"]=sourceref;
            attributemap["sourceAttr"]=sourceAttr;
     
           }
          else if(this.attributeTableArray[i].sourceAttributeType.value == "function"){
            sourceref["type"]="function";
            sourceref["uuid"]=this.attributeTableArray[i].sourcefunction.uuid;
            sourceAttr["ref"]=sourceref;
            attributemap["sourceAttr"]=sourceAttr
          }
          sourceAttributesArray[i]=attributemap;
     }
    datasetJson["attributeInfo"]=sourceAttributesArray;
    console.log(JSON.stringify(datasetJson))
    this._commonService.submit("datasetview",datasetJson).subscribe(
    response => { this.OnSuccessubmit(response)},
    error => console.log('Error :: ' + error)
    )}
    
     OnSuccessubmit(response){
      this.isSubmitEnable=true;
      this.msgs = [];
      this.msgs.push({severity:'success', summary:'Success Message', detail:'Dataset Submitted Successfully'});
      setTimeout(() => {
        this.goBack()
        }, 1000);
    }
    enableEdit(uuid, version) {
      this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'false']);
      
   }
   showview(uuid, version) {
      this.router.navigate(['app/dataPreparation/dataset',uuid,version, 'true']);
  }

}
