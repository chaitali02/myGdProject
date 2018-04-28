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
  selectFunctionType: any;
  selectCatogory: any;
  msgs: any[];
  selectallrow: any;
  allParamType: { label: string; value: string; }[];
  functionTableArray: any;
  category: { label: string; value: string; }[];
  allTypes: any[];
  funcType: any[];
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
      if(this.mode !== undefined) {
      this.getOneByUuidAndVersion();
      this.getAllVersionByUuid();
      }
      this.funcType = [{label:"hive",value:"hive"},
      {label:"impala",value:"impala"},
      {label:"oracle",value:"oracle"},
      ]
      this.allTypes = [{label:"file",value:"file"},
      {label:"hive",value:"hive"},
      {label:"impala",value:"impala"},
      {label:"mysql",value:"mysql"},
      {label:"oracle",value:"oracle"},
      ]
      this.category = [{label:"math",value:"math"},
      {label:"string",value:"string"},
      {label:"date",value:"date"},
      {label:"conditional",value:"conditional"},
      {label:"aggregate",value:"aggregate"},
      ]
      this.allParamType=[
      {label:"string",value:"string"},
      {label:"function",value:"function"},]
      //this.funcType = ["hive", "impala", "oracle"];
      //this.allTypes = ["file", "hive", "impala", "mysql", "oracle"];
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
    this.functionData.inputFlag=response["inputReq"] == 'Y' ? true : false    
    this.selectVersion.version= response['version'];
    this.selectVersion.uuid=response['uuid']
    this.breadcrumbDataFrom[2].caption=this.functionData.name;
    this.functionTableArray = response.functionInfo;
    for(let i=0;i<this.functionTableArray.length;i++){
    for(let j=0;j<this.functionTableArray[i].paramInfoHolder.length;j++){
    if(this.functionTableArray[i].paramInfoHolder[j]["paramReq"]=='N'){
      this.functionTableArray[i].paramInfoHolder[j]["paramReq"]=false
    }
    else{
      this.functionTableArray[i].paramInfoHolder[j]["paramReq"]=true
    }
  }
  } 
  }

  OnSuccesgetAllVersionByUuid(response) {
    this.versions=response
  }
  onChangeActive(event) {
    if(event === true) {
      this.functionData.active = 'Y';
    }
    else {
      this.functionData.active = 'N';
    }
  }
  onChangePublish(event) {
    if(event === true) {
      this.functionData.published = 'Y';
    }
    else {
      this.functionData.published = 'N';
    }
  }
  onChangeInputFlag(event) {
    if(event === true) {
      this.functionData.inputFlag = 'Y';
    }
    else {
      this.functionData.inputFlag = 'N';
    }
  }
  selectAllRow(){
    this.functionTableArray.forEach(relation => {
      relation.selected = this.selectallrow;
    });
  }
  selectAllSubRow(index){
    (this.functionTableArray[index].paramInfoHolder).forEach(paramInfoHolder => {
      paramInfoHolder.selected = this.functionTableArray[index].selectallparamInfoHolder;
    });
  
  }
  addJoinSubRow(index){
    var paramInfoHolder={}
    paramInfoHolder["paramId"]=index+1;
    paramInfoHolder["paramReq"]=false;
    paramInfoHolder["paramType"]=this.allParamType[0];
    paramInfoHolder["paramName"]="''";
    this.functionTableArray[index].paramInfoHolder.splice(this.functionTableArray[index].paramInfoHolder.length, 0,paramInfoHolder);
 }

addRow(){
if( this.functionTableArray == null){

 this.functionTableArray=[];
}
var functionTable={};
var paramInfoHolder=[];
functionTable["type"]=this.allTypes[0];
var paramInfoHolders={};
paramInfoHolder.push(paramInfoHolders)
functionTable["name"]=''
this.functionTableArray.splice(this.functionTableArray.length, 0,functionTable);

}
removeJoinSubRow(index){
  this.functionTableArray[index].selectalljoinkey=false
      var newDataList=[];
      this.functionTableArray[index].checkalljoinKeyrow=false;
      this.functionTableArray[index].paramInfoHolder.forEach(selected => {
        if(!selected.selected){
          newDataList.push(selected);
        }
      });
     if(newDataList.length >0){
     newDataList[0].logicalOperators="";
     }
      this.functionTableArray[index].paramInfoHolder = newDataList;
   }
   removeRow(){
    let newDataList=[];
    this.selectallrow=false;
    this.functionTableArray.forEach(selected => {
      if(!selected.selected){
        newDataList.push(selected);
      }
    });
    if(newDataList.length >0){
     newDataList[0].logicalOperator="";
    }
   this.functionTableArray = newDataList;
  }
  submitFunction(){
    this.isSubmitEnable=true;
    var functionJson = {};
		// this.isshowmodel = true;
		// this.dataLoading = true;
		// this.iSSubmitEnable = false;
		// this.functionHasChanged = true;
		// this.myform.$dirty = false;
		var functionJson = {}
		functionJson["uuid"] = this.functionData.uuid
		functionJson["name"] = this.functionData.name
		functionJson["desc"] = this.functionData.desc
		functionJson["active"] = this.functionData.active;
		functionJson["published"] = this.functionData.published;
		functionJson["functionInfo"] = this.functionData.functionInfo;
		functionJson["category"] = this.selectCatogory;
		functionJson["funcType"] = this.selectFunctionType;
		functionJson["inputReq"] = this.functionData.inputReq;
		// var tagArray = [];
		// if (this.tags != null) {
		// 	for (var counttag = 0; counttag < this.tags.length; counttag++) {
		// 		tagArray[counttag] = this.tags[counttag].text;
		// 	}
		// }
    // functionJson.tags = tagArray;
    var tagArray=[];
    if(this.functionData.tags !=null){
        for(var counttag=0;counttag<this.functionData.tags.length;counttag++){
          tagArray[counttag]=this.functionData.tags[counttag];
 
        }
    }
    functionJson["tags"]=tagArray;
		var functionInfoArray = [];

		if (this.functionTableArray != null) {
			if (this.functionTableArray.length > 0) {
				for (let j = 0; j < this.functionTableArray.length; j++) {
					var functionInfo = {};
					var paramInfoHolder = [];
					functionInfo["name"] = this.functionTableArray[j].name
          functionInfo["type"] = this.functionTableArray[j].type
          if (this.functionTableArray.paramInfoHolder) {
					for (var i = 0; i < this.functionTableArray[j].paramInfoHolder.length; i++) {
						var paramInfoHolderDetail = {};
						paramInfoHolderDetail["paramId"] = this.functionTableArray[j].paramInfoHolder[i].paramId;
						paramInfoHolderDetail["paramName"] = this.functionTableArray[j].paramInfoHolder[i].paramName;
            paramInfoHolderDetail["paramDefVal"] = this.functionTableArray[j].paramInfoHolder[i].paramName;
            if(this.functionTableArray[j].paramInfoHolder[i]["paramReq"]==false){
              paramInfoHolderDetail["paramReq"]="N"
            }
            else{
              paramInfoHolderDetail["paramReq"]="Y"
            }
						//paramInfoHolderDetail["paramReq"] = this.functionTableArray[j].paramInfoHolder[i].paramReq;
						paramInfoHolderDetail["paramType"] = this.functionTableArray[j].paramInfoHolder[i].paramType;
						paramInfoHolder[i] = paramInfoHolderDetail;
          }
					}
					functionInfo["paramInfoHolder"] = paramInfoHolder;
					functionInfoArray[j] = functionInfo

				}

			}
		}
		functionJson["functionInfo"] = functionInfoArray
		console.log(JSON.stringify(functionJson))
  this._commonService.submit("function",functionJson).subscribe(
  response => { this.OnSuccessubmit(response)},
  error => console.log('Error :: ' + error)
  )
  }
  OnSuccessubmit(response){
    this.isSubmitEnable=true;
    this.msgs = [];
    this.msgs.push({severity:'success', summary:'Success Message', detail:'Function Submitted Successfully'});
    setTimeout(() => {
      this.goBack()
      }, 1000);
     }
  
  enableEdit(uuid, version) {
    this.router.navigate(['app/dataPreparation/function',uuid,version, 'false']);
    }
    
  showview(uuid, version) {
    this.router.navigate(['app/dataPreparation/function',uuid,version, 'true']);
        }
        public goBack() {
          // this._location.back();
           this.router.navigate(['app/list/function']);
          }
}
