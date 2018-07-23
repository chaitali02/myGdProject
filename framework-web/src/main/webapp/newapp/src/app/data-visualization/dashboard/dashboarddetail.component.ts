
import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { DashboardService } from '../../metadata/services/dashboard.service'
import { CommonService } from '../../metadata/services/common.service';

import { Version } from './../../metadata/domain/version'
import{ DependsOn } from './../dependsOn'
import { AttributeHolder } from './../../metadata/domain/domain.attributeHolder'
import * as $ from 'jquery';
@Component({
    selector: 'app-dashboard-detail',
    styleUrls: [],
    templateUrl: './dashboarddetail.template.html'
  })

export class DashboardDetailComponent {
    intervalId:any;
    isSubmit: string;
    msgs: any;
    dropdownList: any[];
    dropdownSettings: { singleSelection: boolean; text: string; selectAllText: string; unSelectAllText: string; enableSearchFilter: boolean; classes: string; maxHeight: number; disabled: boolean; };
    selectedItems: any[];
    attributeTableArray: any;
    allVizpod: any;
    sectionRows: any;
    pageNo: number;
    sources: string[];
    allSourceAttribute: any[];
    allNames: any[];
    sourcedata: DependsOn;
    source:any;
    breadcrumbDataFrom: { "caption": string; "routeurl": string; }[];
    selectedVersion: Version;
    VersionList: any[];
    dashboardData: any;
    mode: any;
    version: any;
    uuid: any;

    constructor(private activatedRoute: ActivatedRoute,public router: Router,private _dashboardService:DashboardService,private _commonService:CommonService,private _location:Location){
         this. dashboardData={};
         this.dashboardData["active"]=true;
         this.pageNo=1;
         this.sectionRows=[];

         this.breadcrumbDataFrom=[{
            "caption":" Data Visualization",
            "routeurl":"/app/list/dashboard"
          },
          {
            "caption":"Dashboard",
            "routeurl":"/app/list/dashboard"
          },
          {
              "caption":"",
              "routeurl":null
          }
        ];
 
        this.sources = ["datapod","relation"];
        this.source=this.sources[0];
        this.dropdownSettings = { 
            singleSelection: false, 
            text:"Select Attrubutes",
            selectAllText:'Select All',
            unSelectAllText:'UnSelect All',
            enableSearchFilter: true,
            classes:"myclass custom-class",
            maxHeight:90,
            disabled:false
          };   
    }

    ngOnInit() {
        this.activatedRoute.params.subscribe((params: Params) => {
            this.uuid = params['id'];
            this.version = params['version'];
            this.mode = params['mode'];
            if(this.mode !== undefined){
                this.getAllVersionByUuid();
                this.getOneByUuidAndVersion();
            }
            else{
                this.getAllLatest(true);
                this.createEmptyRow();
               
               
            }
        });
        
    }
    convertSectionInfo(sectionInfo){
        let _columns={};
        let _columnsInfo=[];
        _columnsInfo[0]=sectionInfo[0];
        _columns["columns"]=_columnsInfo;
        this.sectionRows[0]=_columns;
        for(let i=0; i<sectionInfo.length;i++){
            let item =sectionInfo[i];
            if(this.sectionRows[item["rowNo"]-1]){
                this.sectionRows[item["rowNo"]-1].columns[item["colNo"]-1]=item
            }
            else {
                this.sectionRows[item["rowNo"]-1] = {columns:[item]}
            }
        }
        let len =this.sectionRows.length;
        let _columnsInfo1=[];
        let _columns1={};
        _columns1["columns"]=_columnsInfo1;
        this.sectionRows[len]=_columns1
        console.log(this.sectionRows)
    }
    
    onDragStart(event) {
     console.log(event)  
    }
    dndDragEnd(data,index){
        let IsEmpty=false;
        let allRows=[]    
        if(this.sectionRows.length-2 == index){
            for(let i=0;i< this.sectionRows.length;i++){          
                if(this.sectionRows[i].columns.length != 0){
                    allRows.push(this.sectionRows[i]);
                   IsEmpty=true
                }
            }
        }
       if(IsEmpty ==true){
        this.sectionRows=allRows;
        let len =this.sectionRows.length;
        let _columnsInfo1=[];
        let _columns1={};
        _columns1["columns"]=_columnsInfo1;
        this.sectionRows[len]=_columns1
       }
        
    }
    getColWidth(row) {
        let count = row.columns.length;
        let result=count <= 4 ? 12/(count) : '4'
        return result
    }

    dragoverCallback(index){
       var ColWidth =this.sectionRows[index].columns.length;
		if(ColWidth >= 4){
			$('.sectionRow#sectionRowNo_'+index).css('opacity','0.3');
            $('.sectionRow#sectionRowNo_'+index).css('background-color','red');
            clearInterval(this.intervalId);
            this.intervalId = setInterval(() => {
                $('.sectionRow#sectionRowNo_'+index).css('opacity','1');
                $('.sectionRow#sectionRowNo_'+index).css('background-color','transparent');
                if(ColWidth >= 4 ){
                    this.sectionRows[index].columns.splice(ColWidth,1);
                }
            }, 400);
            return false;
        }
        return true;
   } 
    dndInserted(){
        console.log("dndInserted")
    }
    createEmptyRow(){
        let len =this.sectionRows.length;
        let _columnsInfo=[];
        let _columns={};
        _columns["columns"]=_columnsInfo;
        this.sectionRows[len]=_columns;
    }
    checkIsEmpty(){
        let IsEmpty=false;
        let allRows=[]
        for(let i=0;i< this.sectionRows.length;i++){          
            if(this.sectionRows[i].columns.length != 0){
                allRows.push(this.sectionRows[i]);
                IsEmpty=true
            }
        }
        if(IsEmpty ==true){
            this.sectionRows=allRows
        }
        return  IsEmpty;
    }
    
    addSectionRow (data,rowindex,colindex,position){
      
      console.log(this.sectionRows)
        let ColWidth=0;
        if(this.mode != undefined){
            ColWidth=this.sectionRows[rowindex].columns.length;
        }
		if(ColWidth >= 4){
            console.log("inside loop"+ColWidth)
			$('.sectionRow#sectionRowNo_'+rowindex).css('opacity','0.3');
            $('.sectionRow#sectionRowNo_'+rowindex).css('background-color','red');
            clearInterval(this.intervalId);
            this.intervalId = setInterval(() => {
                $('.sectionRow#sectionRowNo_'+rowindex).css('opacity','1');
                $('.sectionRow#sectionRowNo_'+rowindex).css('background-color','transparent');
    
            }, 400);
        }
        else{

            let vizobj={};
            if(this.allVizpod.length >0){
            vizobj["label"]=this.allVizpod[0].label;
            vizobj["uuid"]=this.allVizpod[0]["value"]['uuid'];      
            vizobj["name"]=this.allVizpod[0]["value"]['name'];
            vizobj["type"]=this.allVizpod[0]["value"]['type'];
            }
            let columns=[];
            let colinfo={};
            colinfo["rowNo"]=""
            colinfo["colNo"]=""
            colinfo["name"]=vizobj["name"]
            colinfo["vizpodInfo"]=vizobj;
            colinfo["edit"]=true
            columns[0]=colinfo
            if(position != "right"){
                this.sectionRows.splice(rowindex+1, 0,{
                    columns
                });
            }
            else{
                this.sectionRows[rowindex]["columns"].splice(colindex+1, 0,colinfo);
            }
        }
    }
    onChangeVizpod(data,rowIndex,colIndex){
        this.sectionRows[rowIndex].columns[colIndex].name=data.name
    }

    public goBack() {
        this._location.back();
    }
    public continue(){
        this.pageNo=2;
    }
    public pageBack(){
        this.pageNo=1;
    }
    public removeItem(item: any, list: any[]): void {
        list.splice(list.indexOf(item), 1);
    }
    
    removeSectionColumn(columns,rowIndex,colIndex) {
		if(this.sectionRows.length > 1){
			this.sectionRows[rowIndex].columns.splice(colIndex,1);
        }
        let IsEmpty=this.checkIsEmpty();
        if(IsEmpty == true){
           this.createEmptyRow();
        }

	}
    getAllLatest(IsDefault){
        this._commonService.getAllLatest(this.source).subscribe(
            response => { this.OnSuccesgetAllLatest(response,IsDefault)},
            error => console.log('Error :: ' + error)
        ) 
    }
    changeType(){
        this.selectedItems=null;
        this.getAllLatest(false);
        this.getVizpodByType();

    }
    ChangeSourceType(){
        this.selectedItems=null;
        this.getAllLatest(true);
        this.getVizpodByType();
        this.getAllAttributeBySource(); 
    }

    OnSuccesgetAllLatest(response1,IsDefault){
        let temp=[]
        if( IsDefault == true) {
            let dependOnTemp: DependsOn = new DependsOn();
            dependOnTemp.label = response1[0]["name"];
            dependOnTemp.uuid = response1[0]["uuid"];
            this.sourcedata=dependOnTemp;
            this.getVizpodByType(); 
           
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
      
     
    }

    getAllAttributeBySource(){
        this._commonService.getAllAttributeBySource(this.sourcedata.uuid,this.source).subscribe(
            response => { this.OnSuccesgetAllAttributeBySource(response)},
            error => console.log('Error :: ' + this.sourcedata.uuid)
        ) 
    }

    OnSuccesgetAllAttributeBySource(response){ 
        this.allSourceAttribute=[] 
        let attribute=[]
        for (const n in response) {
            let allname={};
            allname["id"]=response[n]['uuid']+"_"+response[n]['attributeId'];
            allname["itemName"]=response[n]['dname'];
            allname["uuid"]=response[n]['uuid'];
            allname["attrId"]=response[n]['attributeId'];
            attribute[n]=allname
        }
        this.dropdownList=attribute;
        
    }
    getAllVersionByUuid(){
        this._commonService.getAllVersionByUuid('dashboard',this.uuid)
        .subscribe(
            response =>{
                this.OnSuccesgetAllVersionByUuid(response)},
            error => console.log("Error :: " + error));
    }

    OnSuccesgetAllVersionByUuid(response) {
        var temp=[]
        for (const i in response) {
            let ver={};
            ver["label"]=response[i]['version'];
            ver["value"]={};
            ver["value"]["label"]=response[i]['version'];      
            ver["value"]["uuid"]=response[i]['uuid'];
            ver["value"]["u_Id"]=response[i]['uuid']+"_"+response[i]['version']
            temp[i]=ver;
        }
        this.VersionList=temp
    }  

    getOneByUuidAndVersion(){
       this._dashboardService.getOneByUuidAndVersion(this.uuid,this.version,"dashboardview")
       .subscribe(
        response =>{this.onSuccessGetOneByUuidAndVersion(response)},
        error => console.log("Error :: " + error));  
    }
    onSuccessGetOneByUuidAndVersion(response){
       this.breadcrumbDataFrom[2].caption=response.name
       this.dashboardData=response;
       this.uuid =response.uuid;
       const version: Version = new Version();
       version.label = response['version'];
       version.uuid = response['uuid'];
       version.u_Id=response['uuid']+"_"+response['version'];
       this.selectedVersion=version;
       this.dashboardData.published=response["published"] == 'Y' ? true : false
       this.dashboardData.active=response["active"] == 'Y' ? true : false;
       this.source=response["dependsOn"]["ref"].type
       let dependOnTemp: DependsOn = new DependsOn();
       dependOnTemp.label = response["dependsOn"]["ref"]["name"];
       dependOnTemp.uuid = response["dependsOn"]["ref"]["uuid"];
       this.sourcedata=dependOnTemp;
       let tmp=[];
       for(let i=0;i<response.filterInfo.length;i++){
         let filterinfo={};
         filterinfo["id"]=response.filterInfo[i]["ref"]["uuid"]+"_"+response.filterInfo[i].attrId;
         filterinfo["itemName"]=response.filterInfo[i]["ref"]["name"]+"."+response.filterInfo[i]["attrName"];
         filterinfo["uuid"]=response.filterInfo[i]["ref"]["uuid"];   
         filterinfo["attrId"]=response.filterInfo[i]["ref"]["attrId"];
         tmp[i]=filterinfo;
       }
       
       this.selectedItems=tmp;
       this.attributeTableArray=response.filterInfo
       this.getAllLatest(false);
       this.convertSectionInfo(response.sectionInfo);
       this.getVizpodByType();
       
    }

  onVersionChange() {
    this._commonService.getOneByUuidAndVersion(this.selectedVersion.uuid, this.selectedVersion.label, 'dashboard')
      .subscribe(
      response => {//console.log(response)},
        this.onSuccessGetOneByUuidAndVersion(response)
      },
      error => console.log("Error :: " + error));
  }

    getVizpodByType(){
        this._dashboardService.getVizpodByType(this.source,this.sourcedata.uuid)
        .subscribe(
         response =>{this.onSuccessGetVizpodByType(response)},
         error => console.log("Error :: " + error));  
    }
    
    onSuccessGetVizpodByType(response){
        let temp=[];
        for (const n in response) {
            let allname={};
            allname["label"]=response[n]['name'];
            allname["value"]={};
            allname["value"]["label"]=response[n]['name'];      
            allname["value"]["uuid"]=response[n]['uuid'];
            allname["value"]["name"]=response[n]['name'];
            allname["value"]["type"]=response[n]['type'];
            temp[n]=allname;
        }
        this.allVizpod=temp;
        if(this.mode == undefined){
            let vizobj={};
            if(this.allVizpod.length >0){
            vizobj["label"]=this.allVizpod[0].label;
            vizobj["uuid"]=this.allVizpod[0]["value"]['uuid'];      
            vizobj["name"]=this.allVizpod[0]["value"]['name'];
            vizobj["type"]=this.allVizpod[0]["value"]['type'];
            }
            let columns=[];
            let colinfo={};
            colinfo["rowNo"]=""
            colinfo["colNo"]=""
            colinfo["name"]=vizobj["name"]
            colinfo["vizpodInfo"]=vizobj;
            colinfo["edit"]=true
            columns[0]=colinfo
            console.log( this.sectionRows)
                // this.sectionRows.splice(0, 0,{
                //     columns
                // });
                this.sectionRows[0].columns=columns
                console.log( this.sectionRows)
            
            //this.addSectionRow("","0","0","bottom");
       }
    }
    submit(){
        this.isSubmit="true"
        let dashboardjson={}
        dashboardjson["uuid"]=this.dashboardData.uuid;
        dashboardjson["name"]=this.dashboardData.name;
        dashboardjson["desc"]=this.dashboardData.desc;
        let tagArray=[];
        if(this.dashboardData.tags !=null){
          for(var counttag=0;counttag<this.dashboardData.tags.length;counttag++){
               tagArray[counttag]=this.dashboardData.tags[counttag];
          }
        }
        dashboardjson["tags"]=tagArray;
        dashboardjson["active"]=this.dashboardData.active == true ?'Y' :"N"
        dashboardjson["published"]=this.dashboardData.published == true ?'Y' :"N"
        let  dependsOn = {};
        let ref={};
        ref["type"] = this.source
        ref["uuid"] = this.sourcedata.uuid;
        dependsOn["ref"] = ref;
        dashboardjson["dependsOn"]=dependsOn;
        let sectionInfo=[];
        let count=0
        let filterInfo=[]
       
        if(this.selectedItems && this.selectedItems.length >0){
            for(let i=0;i<this.selectedItems.length;i++){
                let filteinfo={};
                let ref={}
                ref["type"]="datapod";
                ref["uuid"]=this.selectedItems[i].uuid;
                filteinfo["ref"]=ref;
                filteinfo["attrId"]=this.selectedItems[i].attrId;
                filterInfo[i]=filteinfo;
            }
        }
        dashboardjson["filterInfo"]=filterInfo;
        if(this.sectionRows.length >0){
            for(let i=0;i<this.sectionRows.length;i++){
                for(let j=0;j<this.sectionRows[i].columns.length;j++){
                    let sectioninfo={};    
                    let vizpodInfo={}
                    let ref={};
                    sectioninfo["sectionId"]=count;
                    sectioninfo["name"]=this.sectionRows[i].columns[j].name;
                    ref["type"]="vizpod";
                    ref["uuid"]=this.sectionRows[i].columns[j].vizpodInfo.uuid;
                    vizpodInfo["ref"]=ref;
                    sectioninfo["vizpodInfo"]=vizpodInfo;
                    let col=j;
                    let row=i;
                    sectioninfo["rowNo"]=row+1;
                    sectioninfo["colNo"]=col+1;
                    sectionInfo.push(sectioninfo);
                    count=count+1;
                }

           } 
        }//End IF  
        dashboardjson["sectionInfo"]=sectionInfo;
        console.log(dashboardjson)
        this._commonService.submit("dashboard",dashboardjson).subscribe(
            response => { this.OnSuccessubmit(response)},
            error => console.log('Error :: ' + error)
          )
    }
    OnSuccessubmit(response){
            this.msgs = [];
            this.isSubmit="true"
            this.msgs.push({severity:'success', summary:'Success Message', detail:'Dashboard Saved Successfully'});
            setTimeout(() => {
              this.goBack()
            }, 1000);
         

    }
    showview(uuid,version){
        this.router.navigate(['app/dataVisualization/dashboarddetail',this.uuid , this.version, 'true']);

    }
}