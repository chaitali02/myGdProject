import { Component, OnInit } from '@angular/core';
import { AppConfig } from '../../app.config';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { CommonService } from '../../metadata/services/common.service';
import {RelationService} from './relation.service';
import { Location } from '@angular/common';
import { Version } from './version';
import { SelectItem } from 'primeng/primeng';
import{ DependsOn } from './dependsOn'
@Component({
  selector: 'app-relation',
  templateUrl: './relation.template.html',
  styleUrls: ['./relation.component.css']
})
export class RelationComponent implements OnInit {
  selectallrow: any;
  rhsAllAttribute: any[];
  rowData: any;
  msgs: any[];
  operators: string[];
  relationTableArray: any;
  joinType: any;
  allNames: SelectItem[] = [];
  dependsOn: DependsOn
  allAttribute: SelectItem[] = [];
  dependsOnType: { 'value': string; 'label': string; }[];
  // selectVersion: any;
  showRelationData: boolean;
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
  relationData : any;
  depends: any;
  allName : any;
  active : any;
  published : any;
  breadcrumbDataFrom : any;
  cars: any [];
  logicalOperators: any;
  cols: any[];
  
  selectedCar: any;
  VersionList: SelectItem[] = [];
  selectedVersion: Version
  dialogVisible: boolean;
  isSubmitEnable:any;

  constructor(private _location: Location,config: AppConfig, private activatedRoute: ActivatedRoute, public router: Router, private _commonService: CommonService,private _RelationService: RelationService) {
    this.showRelationData = true;
    this.relationData = {};
    this.relationData["active"]=true
    this.isSubmitEnable=true;
    this.dependsOn={'uuid':"","label":""}
    // this.selectVersion={"version":""};
    this.breadcrumbDataFrom=[{
      "caption":"Data Preparation ",
      "routeurl":"/app/list/relation"
    },
    {
      "caption":"Relation",
      "routeurl":"/app/list/relation"
    },
    {
    // Unable to display relationData.name
    //   "caption":this.relationData.name,
    //   "routeurl":null
    }
    ]
    this.depends="datapod"
    this.dependsOnType = [
      {'value': 'datapod', 'label': 'datapod'},
    ];
    this.joinType = [
      {label: 'EQUI JOIN', value: 'EQUI JOIN'},
      {label: 'LEFT OUTER', value: 'LEFT OUTER'},
      {label: 'RIGHT OUTER', value: 'RIGHT OUTER'},
      {label: 'FULL OUTER', value: 'FULL OUTER'},
      {label: 'LEFT SEMI', value: 'LEFT SEMI'}
  ];
    //this.joinType=["EQUI JOIN","LEFT OUTER",'RIGHT OUTER','FULL OUTER','LEFT SEMI']
    this.logicalOperators=["","AND","OR"]
    this.operators=["=","<",">"];
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
        else{
          this.rhsAllAttribute=[];
          this.getallLatest()
          this.changeType()
        }
    })
    this.cols = [ 
    {field: 'join.ref.uuid', header: 'Join Datapod'},
    {field: 'relationJoinType', header: 'Join Type'}
    ];
  }

  getOneByUuidAndVersion(id,version){
    this._commonService.getOneByUuidAndVersion(id,version,'relation')
    .subscribe(
    response =>{
      this.onSuccessgetOneByUuidAndVersion(response)},
    error => console.log("Error :: " + error)); 
  }

  getAllVersionByUuid(){
    this._commonService.getAllVersionByUuid('relation',this.id)
    .subscribe(
    response =>{
      this.OnSuccesgetAllVersionByUuid(response)},
    error => console.log("Error :: " + error));
  }
  onSuccessgetOneByUuidAndVersion(response){
    this.relationData=response;
    this.createdBy=response.createdBy.ref.name;
    this.relationData.published=response["published"] == 'Y' ? true : false
    this.relationData.active=response["active"] == 'Y' ? true : false
    const version: Version = new Version();
    this.uuid=response.uuid;
    version.label = response['version'];
    version.uuid = response['uuid'];
    this.selectedVersion=version
    // this.selectVersion.version= response['version'];
    // this.selectVersion.uuid=response['uuid']
    this.breadcrumbDataFrom[2].caption=this.relationData.name;
    this.depends=response["dependsOn"]["ref"]["type"];
    //this.dependsOn=response["dependsOn"]["ref"]
    let dependOnTemp: DependsOn = new DependsOn();
    dependOnTemp.label = response["dependsOn"]["ref"]["name"];
    dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
    this.dependsOn=dependOnTemp
    // this.dependsOn.uuid=response["dependsOn"]["ref"]["uuid"]
    // this.dependsOn.name=response["dependsOn"]["ref"]["name"];
    var relationjson={};
    relationjson["relationdata"]=response;
    var relationInfoArray=[];
    for(var i=0;i<response.relationInfo.length;i++){
       var relationInfo={};
     if(response.relationInfo[i].joinType == ""){
           relationInfo["relationJoinType"]="EQUI JOIN"
     }
     else{
           relationInfo["relationJoinType"]=response.relationInfo[i].joinType;
     }

     var ref={};
           ref["label"]=response.relationInfo[i].join.ref.name;
          // ref["name"]=response.relationInfo[i].join.ref.name;
           ref["uuid"]=response.relationInfo[i].join.ref.uuid;
           //ref["id"]=response.relationInfo[i].join.ref.uuid+"."+response.relationInfo[i].join.ref.attributeId;
           relationInfo["join"]=ref;
      // joinarray.push(ref);
     //relationInfo.joinarray=joinarray;
     var joinKeyArray=[];
     for(var l=0;l<response.relationInfo[i].joinKey.length;l++){
       var joinjson={};
       var lhsoperand={};
         var rhsoperand={};
         joinjson["logicalOperator"]=response.relationInfo[i].joinKey[l].logicalOperator
         joinjson["relationOperator"]=response.relationInfo[i].joinKey[l].operator
       lhsoperand["uuid"]=response.relationInfo[i].joinKey[l].operand[0].ref.uuid;
       lhsoperand["name"]=response.relationInfo[i].joinKey[l].operand[0].ref.name;
       lhsoperand["attributeId"]=response.relationInfo[i].joinKey[l].operand[0].attributeId;
       lhsoperand["attributeName"]=response.relationInfo[i].joinKey[l].operand[0].attributeName;
       lhsoperand["id"]=lhsoperand["uuid"]+"_"+lhsoperand["attributeId"];
       lhsoperand["dname"]=lhsoperand["name"]+"."+lhsoperand["attributeName"];
       rhsoperand["uuid"]=response.relationInfo[i].joinKey[l].operand[1].ref.uuid;
       rhsoperand["name"]=response.relationInfo[i].joinKey[l].operand[1].ref.name;
       rhsoperand["attributeId"]=response.relationInfo[i].joinKey[l].operand[1].attributeId;
       rhsoperand["attributeName"]=response.relationInfo[i].joinKey[l].operand[1].attributeName;
       rhsoperand["id"]=rhsoperand["uuid"]+"_"+rhsoperand["attributeId"];
       rhsoperand["dname"]=rhsoperand["name"]+"."+rhsoperand["attributeName"];
       joinjson["lhsoperand"]=lhsoperand
       joinjson["rhsoperand"]=rhsoperand
       joinKeyArray.push(joinjson);
       relationInfo["joinKey"]=joinKeyArray;
     }
       relationInfoArray.push(relationInfo);

   }

    this.relationTableArray=relationInfoArray;
    console.log(JSON.stringify(this.relationTableArray))
    this.getallLatest()
    this.getRhsData();
  }
  getallLatest(){
    this._commonService.getAllLatest("datapod").subscribe(
      response => { this.OnSuccesgetAllLatest(response)},
      error => console.log('Error :: ' + error)
    )
  }
  OnSuccesgetAllLatest(response){
    //this.allNames = response
    let temp=[]
    for (const n in response) {
      let allname={};
      allname["label"]=response[n]['name'];
      allname["value"]=response[n]['uuid'];
      allname["value"]={};
      allname["value"]["label"]=response[n]['name'];      
      allname["value"]["uuid"]=response[n]['uuid'];
      temp[n]=allname;
    }
    //this.allAttribute=temp
    this.allNames = temp
    if(this.mode !== undefined) {   
    this.getRhsData()
    }
    else{

    }
  }
  getRhsData(){
    this._RelationService.getAllAttributeBySource(this.relationData.uuid,"relation").subscribe(
      response => { this.OnSuccesgetAllLhsAttributeBySource(response)},
      error => console.log('Error :: ' + error)
    ) 
  }
  OnSuccesgetAllLhsAttributeBySource(response){
    this.allAttribute =response.allattributes
    this.rhsAllAttribute=[];
    var count=0;
    for(var i=0;i<response.attributes.length;i++){
     if(i != 0){
      this.rhsAllAttribute[count]=response.attributes[i];
      count=count+1;
     }
    }
  }
  OnSuccesgetAllVersionByUuid(response) {
    //this.versions = response;
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
changeType(){
  //this.getRhsData()
  this._RelationService.getAllAttributeBySource(this.dependsOn.uuid,"datapod").subscribe(
    response => { this.OnSucceschangeType(response)},
    error => console.log('Error :: ' + error)
  ) 
}
OnSucceschangeType(response){
let temp=[]
for (const n in response) {
  let allname={};
  allname["label"]=response[n]['dname'];  
  allname["value"]=response[n]['uuid'];
  allname["value"]={};
  allname["value"]["label"]=response[n]['dname'];      
  allname["value"]["uuid"]=response[n]['uuid']+"_"+response[n].attributeId;;
  temp[n]=allname;
}
this.allAttribute=temp
}
changeJoin(data,index){
  {
    if(typeof data != "undefined"){
      this._RelationService.getAllAttributeBySource(data.uuid,"datapod").subscribe(
        response => { 
          let temp=[]
          for (const n in response) {
            let allname={};
            allname["label"]=response[n]['dname'];  
            allname["value"]=response[n]['uuid'];
            allname["value"]={};
            allname["value"]["label"]=response[n]['dname'];      
            allname["value"]["uuid"]=response[n]['uuid']+"_"+response[n].attributeId;;
            temp[n]=allname;
          }
          this.rhsAllAttribute[index]=temp
        },
        error => console.log('Error :: ' + error)
      ) 
    }
  }
}
public goBack() {
 // this._location.back();
  this.router.navigate(['app/list/relation']);
 }
onVersionChange(){
  this.getOneByUuidAndVersion(this.selectedVersion.uuid,this.selectedVersion.label);
}
test(car){
  console.log(car)
}
onChangeActive(event) {
  if(event === true) {
    this.relationData.active = 'Y';
  }
  else {
    this.relationData.active = 'N';
  }
}
onChangePublish(event) {
  if(event === true) {
    this.relationData.published = 'Y';
  }
  else {
    this.relationData.published = 'N';
  }
}
selectAllSubRow(index){debugger
  (this.relationTableArray[index].joinKey).forEach(joinkey => {
    joinkey.selected = this.relationTableArray[index].selectalljoinkey;
  });

}
removeJoinSubRow(index){
  this.relationTableArray[index].selectalljoinkey=false
      var newDataList=[];
      this.relationTableArray[index].checkalljoinKeyrow=false;
      this.relationTableArray[index].joinKey.forEach(selected => {
        if(!selected.selected){
          newDataList.push(selected);
        }
      });
     if(newDataList.length >0){
     newDataList[0].logicalOperators="";
     }
      this.relationTableArray[index].joinKey = newDataList;
   }
addJoinSubRow(index){
      var joinKey={}
      joinKey["logicalOperator"]=this.logicalOperators[0];
      joinKey["relationOperator"]=this.operators[0];
      joinKey["rhsoperand"]="''";
      joinKey["lhsoperand"]="''";
      this.relationTableArray[index].joinKey.splice(this.relationTableArray[index].joinKey.length, 0,joinKey);
   }
   selectAllRow(){
    this.relationTableArray.forEach(relation => {
      relation.selected = this.selectallrow;
    });
}

addRow(){
 if( this.relationTableArray == null){

   this.relationTableArray=[];
 }
var relationtable={};
var joinKey=[];
relationtable["relationJoinType"]=this.joinType[0];
var joinkey={};
joinKey.push(joinkey)
relationtable["joinKey"]=joinKey
this.relationTableArray.splice(this.relationTableArray.length, 0,relationtable);

}
removeRow(){
  let newDataList=[];
  this.selectallrow=false;
  this.relationTableArray.forEach(selected => {
    if(!selected.selected){
      newDataList.push(selected);
    }
  });
  if(newDataList.length >0){
   newDataList[0].logicalOperator="";
  }
 this.relationTableArray = newDataList;
}
submitRelation(){
  this.isSubmitEnable=true;
	var relationjson={}
	relationjson["uuid"]=this.relationData.uuid;
  relationjson["name"]=this.relationData.name;
  relationjson["desc"]=this.relationData.desc;
//	relationjson["active"]=this.relationData.active;
//	relationjson["published"]=this.relationData.published;
relationjson["active"]=this.relationData.active == true ?'Y' :"N"
relationjson["published"]=this.relationData.published == true ?'Y' :"N"

	 var tagArray=[];
	 if(this.relationData.tags !=null){
	     for(var counttag=0;counttag<this.relationData.tags.length;counttag++){
	     	tagArray[counttag]=this.relationData.tags[counttag];

	     }
	 }
	 relationjson["tags"]=tagArray;
	 var dependsOn={};
	 var ref={};
	 ref["type"]=this.depends;
	 ref["uuid"]=this.dependsOn.uuid;
	 dependsOn["ref"]=ref;
   relationjson["dependsOn"]=dependsOn
   var relationInfoArray=[];
     if(this.relationTableArray !=null){
       if(this.relationTableArray.length >0){
       for(let j=0;j<this.relationTableArray.length;j++){
   
           var relationInfo={};
           var attributeList={};
           var join={}
           var joinKey=[];
   
           var joinref={}
           var operand=[]
           var firstoperad={}
           var scecondoperad={}
           var firstoperandref={}
           var scecondoperandref={}
            if(this.relationTableArray[j].relationJoinType.value == "EQUI JOIN"){
              relationInfo["joinType"]=""
           }
           else{
   
             relationInfo["joinType"]=this.relationTableArray[j].relationJoinType;
           }
   
   
           joinref["type"]="datapod";
           joinref["uuid"]=this.relationTableArray[j].join.uuid;
           join["ref"]=joinref;
           relationInfo["join"]=join;
           for(var i=0;i<this.relationTableArray[j].joinKey.length;i++){
   
             var operand=[]
             var  JoinKeyDetail={};
             var firstoperad={}
               var scecondoperad={}
               var firstoperandref={}
               var scecondoperandref={}
   
             if (typeof this.relationTableArray[j].joinKey[i].logicalOperator == "undefined") {
                 JoinKeyDetail["logicalOperator"]=""
             }
             else{
               JoinKeyDetail["logicalOperator"]=this.relationTableArray[j].joinKey[i].logicalOperator
             }

              JoinKeyDetail["operator"]=this.relationTableArray[j].joinKey[i].relationOperator
              firstoperandref["type"]="datapod";
              if(this.relationTableArray[j].joinKey[i].lhsoperand.id){
                var  uuid=this.relationTableArray[j].joinKey[i].lhsoperand.id.split("_")[0]
                var attrid=this.relationTableArray[j].joinKey[i].lhsoperand.id.split("_")[1]
              }
              else{
                var  uuid=this.relationTableArray[j].joinKey[i].lhsoperand.uuid.split("_")[0]
                var attrid=this.relationTableArray[j].joinKey[i].lhsoperand.uuid.split("_")[1]
              }                  
              firstoperandref["uuid"]=uuid
              firstoperad["ref"]=firstoperandref;
              firstoperad["attributeId"]=attrid
              scecondoperandref["type"]="datapod";
              if(this.relationTableArray[j].joinKey[i].rhsoperand.id){
                var  rhsUuid=this.relationTableArray[j].joinKey[i].rhsoperand.id.split("_")[0]
                var rhsAttrid=this.relationTableArray[j].joinKey[i].rhsoperand.id.split("_")[1]
              }
              else{
                var  rhsUuid=this.relationTableArray[j].joinKey[i].rhsoperand.uuid.split("_")[0]
                var rhsAttrid=this.relationTableArray[j].joinKey[i].rhsoperand.uuid.split("_")[1]
              }
              scecondoperandref["uuid"]=rhsUuid
              scecondoperad["attributeId"]=rhsAttrid
              scecondoperad["ref"]=scecondoperandref;
              operand[0]=firstoperad;   
              operand[1]=scecondoperad;
              JoinKeyDetail["operand"]=operand
              joinKey[i]=JoinKeyDetail;
              relationInfo["joinKey"]=joinKey;
           }
   
           relationInfoArray[j]=relationInfo   
         }
   
       }}
       relationjson["relationInfo"]=relationInfoArray
	 console.log(JSON.stringify(relationjson))

this._commonService.submit("relation",relationjson).subscribe(
response => { this.OnSuccessubmit(response)},
error => console.log('Error :: ' + error)
)
}
OnSuccessubmit(response){
  this.isSubmitEnable=true;
  this.msgs = [];
  this.msgs.push({severity:'success', summary:'Success Message', detail:'Relation Submitted Successfully'});
  setTimeout(() => {
    this.goBack()
    }, 1000);
   }

enableEdit(uuid, version) {
  this.router.navigate(['app/dataPreparation/relation',uuid,version, 'false']);
  }
  
showview(uuid, version) {
  this.router.navigate(['app/dataPreparation/relation',uuid,version, 'true']);
      }

  
}