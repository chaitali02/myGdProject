import { Component, Input,OnInit,ViewChild } from '@angular/core';
import {Router,Event as RouterEvent,ActivatedRoute,Params,NavigationEnd} from '@angular/router';
import * as $ from 'jquery';
import * as _ from 'lodash';
import * as Backbone from 'backbone';
declare var require: any;
const joint = require('../../../node_modules/jointjs/dist/joint.js');
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { ShContextMenuModule ,IShContextMenuItem,BeforeMenuEvent} from 'ng2-right-click-menu'
import { SelectItem } from 'primeng/primeng';

import {JointjsGroupComponent} from '../shared/components/jointjsgroup/jointjsgroup.component'

import {JointjsService} from './jointjsservice'
import { CommonService } from '../metadata/services/common.service';
import{SharedDataService} from './shareddata.service'
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import {AppMetadata} from '../app.metadata';
@Component({
  selector: 'app-jointjs',
  templateUrl: './jointjs.template.html',
  styles:['']
})

export class JointjsComponent{
    params:any
    IsTableShow: boolean;
    IsGroupGraphShow: boolean;
    columnOptions: any[];
    colsdata: any;
    cars: any;
    cols: { field: string; header: string; }[];
    allOperatorInfo: any;
    operatorinfoMapInfo: any;
    popupModel:any
    paper: any;
    graph: any;
    @Input()
    data:any;
    @Input()
    mode:any;
    @Input()
    execMode:any;
    items:any[];
    dataObject:any;
    private d3: D3; // Define the private member which will hold the this.d3 reference
    IsGraphShow:any;
    @ViewChild(JointjsGroupComponent) d_JointjsGroupComponent: JointjsGroupComponent;
    constructor(private appMetadata: AppMetadata,private _jointjsService:JointjsService,d3Service:D3Service, private _commonService: CommonService,private _sharedDataService:SharedDataService,private _dataPipelineService:DataPipelineService,private activatedRoute: ActivatedRoute, public router: Router) {
        this.IsGraphShow=true;
        this.IsGroupGraphShow =false;
        this.IsTableShow = false;
        this.params={ "ref": {} };
        this.d3 = d3Service.getD3();
        let d_jointComponetObj=this;
        this.popupModel={"modelData":{}}
        this.dataObject={"one":"one"}; 
        let _allMetadataDefs=this.appMetadata.getAllMetadataDefs();
        let _menuCount=0;
        this.items=[];
        Object.keys(_allMetadataDefs).forEach(key => {
            if(_allMetadataDefs[key].allowInMenu == true){
                let ltem={}
                ltem["label"]='<img width="15" height="15" src='+_allMetadataDefs[key].iconPath+'></img> <span>'+_allMetadataDefs[key].caption+'</span>'
                ltem["onClick"]=this.clickEvent
                ltem["type"]=_allMetadataDefs[key].name;
                ltem["FunEvent"]=event;
                ltem["jointComponetObj"]=d_jointComponetObj;
                this.items[_menuCount]=ltem;
                _menuCount=_menuCount+1; 
                if(_allMetadataDefs[key].name !="model"){
                    this.items[_menuCount]={
                        divider: true
                    }
                    _menuCount=_menuCount+1;
                }  
            }
        })  
            
    }
      
    ngOnInit() {  
       if(Object.keys(this.data).length >0 && this.execMode == false){ 
            this.createGraph(this.data);
            
       }
    }
    
  
    private _setGrid(paper, gridSize, color) {
        paper.options.gridSize = gridSize; // Set grid size on the JointJS paper object (joint.dia.Paper instance
        var canvas = $('<canvas/>', { width: gridSize, height: gridSize }); // Draw a grid into the HTML 5 canvas and convert it to a data URI image
        canvas[0]["width"] = gridSize;
        canvas[0]["height"] = gridSize;
        var context = canvas[0]["getContext"]('2d');
        context["beginPath"]();
        context["rect"](1, 1, 1, 1);
        context["fillStyle"] = color || '#AAAAAA';
        context["fill"]();
        var gridBackgroundImage = canvas[0]["toDataURL"]('image/png'); // // Finally, set the grid background image of the paper container element.
        paper.$el.css('background-image', 'url("' + gridBackgroundImage + '")');
    }
    
    private _jointElementMouseOver(){
        let d_graph=this.graph; 
        let d_appMetadata=this.appMetadata
        let elementType;
        let elementModel;
        let txt1; 
        let txt2;        
        let txt3;
        $( "#paper").on("mouseover",".joint-element .body",function(e){
            $('.joint-element').mouseout(function(e){
              var divid = 'divtoshow';
              $("#"+divid).hide();
            $('.connection').removeClass('active');
            });

            var jointElement = $(this).closest(".joint-element");
            var s = jointElement.attr("model-id");
            $('.connection[source-id='+s+']').addClass('active');

            var cell = d_graph.getCell(s);
             elementModel = cell.attributes['model-data'];
            try {
                elementType = elementModel.operators[0].operatorInfo.ref.type;
                if(elementType.slice(-4) == 'Exec'){
                    elementType = elementType.slice(0,-4);
                }
            }
            catch(e){
                if(s.substr(0,3)=='dag'){
                    let cell = $(this);
                    elementModel = cell.attr("model-data") ? JSON.parse(cell.attr("model-data")) : undefined;
                    elementType = 'dag';
                }
                else if(cell.attributes.elementType=='stage'){
                    elementType = 'stage';
                    elementModel = {name : cell.attributes['model-data'].name, type : elementType, uuid:cell.attributes['model-data'].stageId};
                }else{
                    elementType = undefined;
                }
            }
            var divid = 'divtoshow';
            var xPercent = e.clientX / $( window ).width() * 100;
            if(xPercent > 50){
                var left  = (e.clientX-400)  + "px";
                var top  = e.clientY  + "px";
            }
            else {
                var left  = (e.clientX+ 40)  + "px";
                var top  = e.clientY  + "px";
            }
            var div = document.getElementById(divid);
            div.style.left = left;
            div.style.top = top;
            var dagtypetext = '';
            try {
                dagtypetext =d_appMetadata.getMetadataDefs(elementType)["caption"];
                let color= d_appMetadata.getMetadataDefs(elementType)["color"];
                $("#dagcolorID").css("background-color", color);
            } 
            catch(e){
                dagtypetext = elementType;
                $("#dagcolorID").css("background-color", "blue");
            }
            if(!elementModel || Object.keys(elementModel).length == 0){
                 txt1 ="", txt2 = "", txt3 = "";
            }
            else {
              var directRef = ['dag','stage'];
              txt1 = directRef.indexOf(elementType) > -1 ? elementModel.uuid : elementModel.operators[0].operatorInfo.ref.uuid || '';
              txt2 = directRef.indexOf(elementType) > -1 ? elementModel.name : elementModel.name || '';
              txt3 = directRef.indexOf(elementType) > -1 ? elementModel.version : elementModel.operators[0].operatorInfo.ref.version || '';
            }
            
            $("#elementTypeText").html(dagtypetext);
            $("#task_Id").html(txt1);
            $("#task_Name").html(txt2);
            if(txt3 == ''){
              $("#task_Version_label").html('');
              $("#task_Version").html('');
            }
            else {
              $("#task_Version_label").html('Version');
              $("#task_Version").html(txt3);
            }
            $("#"+divid).show();
        })
    }
    _jointElementOnRemoveClick(){
        let cc=this.graph;
        let d_sharedDataService=this._sharedDataService
        $( "#paper" ).on("click",".joint-element .remove",function(e){
            var jointElement = $(this).closest(".joint-element");
            var s = jointElement.attr("model-id");
            var cell = cc.getCell(s);
            cell.remove();
            d_sharedDataService.setData(cc.getCells());
          });
    }
    createGraph(data) {

        let d_link=this.appMetadata.getDefaultLink()
        let d_functionCall=this._jointjsService;
        let d_sharedDataService=this._sharedDataService
        this.graph = new joint.dia.Graph;
        let dx = $("#tab2").width();
        
        let dy = $("#tab2").height();
        var paperEl = $('#paper');
        this.graph = new joint.dia.Graph;
        let d_graph=this.graph
        this.paper = new joint.dia.Paper({ el: paperEl,gridSize: 1, width: dx > 2000 ? dx : 2000, height: dy*2 > 1000 ? dy*2 : 1000,model:this.graph,
        interactive: {
            vertexAdd: false
        },
        // async:true,
        linkPinning : false,
        linkView: joint.dia.LinkView,
        multiLinks: false,
        snapLinks: true,
        markAvailable: true,
        defaultLink : function(cellView, magnet) {

            console.log(d_graph.getCells())
            return new joint.dia.Link(Object.assign({},d_link,{
                attrs: { '.connection': { 'source-id':cellView.model.id}}
            }));
            
        },
        validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
            if(magnetT.attributes['port'].value == 'out'){
                return false
            }

            return d_functionCall.validate(d_graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView);
            
            //return true;
        },
        validateMagnet: function (cellView, magnet) {
            if(magnet.attributes['port'].value == 'in'){
                return false
            }
            return true
        }
        });
        this.paper.scale(0.7);
        this._setGrid(this.paper, 10, '#AAAAAA');
        let convertedData = this._jointjsService.convertDagToGraph(data);
       // console.log("dag data"+convertedData);
        let cells = convertedData.cells;
        let links = convertedData.links;
        this.graph.addCells(cells);
        this.graph.addCells(links);
        if(this.mode == "true"){
            $('#paper svg').addClass('view-mode');
        }
       
        if(this.execMode == true){
            $('#paper svg').addClass('exec-mode');
        }
        let d_d3=this.d3
        let d_this=this;
        $( "#paper" ).on("dblclick",".joint-element .body",function(){
            d_this.dblClickFn(event,'',this);
        });

        this.graph.on('change:source change:target', function(link) {
            if (link.get('source').id && link.get('target').id) {
                d_sharedDataService.setData(d_graph.getCells());
            }
           
        }) 
        if(this.execMode == true){
            this.paper.$el.on('contextmenu',".joint-element .body",function(){
                var vm = this;
                var id = vm.getAttribute("element-id");
                var cell = d_this.graph.getCell(id);
                var type = cell.attributes.elementType;
                let menuItem=d_this.getMenuItem(id,cell);
                if(menuItem.length >0)
                d_this.contextMenu1(d_d3,menuItem,d_this)
            })
            
        }
        this.paper.on('cell:pointerup blank:pointerup', function(cellView, x, y) {
            d_sharedDataService.setData(d_graph.getCells());
        });

        this._jointElementMouseOver();
        this._jointElementOnRemoveClick();
        this._sharedDataService.setData(this.graph.getCells());
    }


    addelement(e,operator){
        //create sub elements on this event
        var localPoint = this.paper.clientToLocalPoint(e.clientX, e.clientY);
        console.log(localPoint)
        var operator = operator || 'dqgroup';
        var cell = new joint.shapes.devs.Model({
            markup: '<g class="rotatable"><g class="scalable"><image class="body"/></g><image class="remove"/><text class="label" /><g class="inPorts"/><g class="outPorts"/></g>',
            // id: "dag_0",
          //  elementType: operator,
            parentStage: '',
            "model-data": {
              name : 'new '+operator,
              operators : [ {
                operatorInfo : {
                  ref : {
                    name : '',
                    type : operator,
                  }
                }
              }]
            },
            position: { x: localPoint.x, y : localPoint.y }, size: { width: 50, height: 50 },
            inPorts: ['in'],
            outPorts: ['out'],
            ports: {
                groups: {
                    'in': {
                        attrs: {
                            '.port-body': {
                                fill: '#fff',
                                r:5,
                                cx:-5
                            }
                        }
                    },
                    'out': {
                        attrs: {
                            '.port-body': {
                                fill: '#fff',
                                r: 5,
                                cx: 5
                            }
                        }
                    }
                }
            },
            elementType : operator,
            attrs: {
                "model-data": {
                  name : 'new '+operator,
                  operators : [ {
                    operatorInfo : {
                      ref : {
                        name : '',
                        type : operator,
                      }
                    }
                  }]
                },
                '.body': {
                    elementType : operator,
                    "model-data": "{}",
                    x:"0", y:"0",height:"50px", width:"50px",
                    "xlink:href":this.appMetadata.getMetadataDefs(operator)["iconPath"]
                },
                '.remove': {
                    x:"55", y:"-20",height:"25px", width:"25px",
                    "xlink:href": "assets/img/delete.png"
                },
                //text:{text:''},
                magnet:true,
                text: { text: 'new '+operator,y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } }
            }
        });
        this.graph.addCell(cell);
        //console.log(this.graph.getCells())
        this._sharedDataService.setData(this.graph.getCells());
        this.dblClickFn(e,cell,this)

     };
    
    clickEvent($event: any,d_this1){
        $event.menuItem.jointComponetObj.addelement($event.menuItem.FunEvent,$event.menuItem.type=='dataqual'?'dq':$event.menuItem.type)
    };
    
    onBefore = (event:BeforeMenuEvent) => {
        if(this.mode == "true"){
            event.event.cancelable;
        }
        else{
            event.open();
        }
    };

    dblClickFn(e,newCell,obj) {
        if(this.mode == "true"){
           return;
        }
    
        let id;
        let jointElement;
        let is_newCell=false;
        $('#divtoshow').hide();
        if(newCell){
           id = newCell.id;
           is_newCell=true;
        }
        else {
            jointElement = $(obj).closest(".joint-element");
            id = jointElement.attr('model-id');
        } 
        var thisModel = newCell || this.graph.getCell(id);

        var elementType = thisModel.attributes.elementType;
        // !$scope.editMode ||
        if(this.mode == "true" || elementType == "stage" || elementType == "dag"){
          return false;
        }

        var name = thisModel.attributes['model-data'].name || '';
        var text = '';
        var color = '';
        try {
            text =this.appMetadata.getMetadataDefs(elementType)["caption"];
            color =this.appMetadata.getMetadataDefs(elementType)["color"];
            $("#dagcolorID").css("background-color", color);
        }
        catch(e){
            text = elementType;
        }
        this.popupModel = {id:id,modelData :thisModel.attributes['model-data'],elementTypeText: text,
        headers : {color:color,title:text}
        };
        
        if(thisModel.attributes['model-data'].operators[0].operatorInfo.ref.uuid){
            this.popupModel["selectedType"] = thisModel.attributes['model-data'].operators[0].operatorInfo.ref.uuid+'|'+thisModel.attributes['model-data'].operators[0].operatorInfo.ref.name;
        }
        this.getAllLatest(elementType,is_newCell);
        
        var xPercent = e.clientX / $( window ).width() * 100;
        var yPercent = e.clientY / $( window ).height() * 100;
        if(xPercent > 50){
             var left  = (e.clientX-450)  + "px";
        }
        else {
            var left  = (e.clientX+ 20)  + "px";
        }
        if(yPercent > 50){
            var top  = (e.clientY-200)  + "px";
        }
        else {
             var top  = (e.clientY)  + "px";
        }
        $("#right-side-test").show();
        $("#right-side-test").css({  'top': top,'left':'2000px' }).animate({
                          'left' : left
                      });
        $("#typeOprator").text(text);
        $("#elementTypeText").html(text);
        $("#savePop").attr("data-id", id);
        $('#namepop').val(name);
    };
    getAllLatest(type,cellMode){
        this._commonService.getAllLatest(type)
        .subscribe(
        response =>{
          this.onSuccessGetAllLates(response,cellMode)},
        error => console.log("Error :: " + error)); 
    }
    onSuccessGetAllLates(response,cellMode){
        let temp=[]
        if(cellMode ==true){
            this.popupModel["selectedType"] = response[0]['uuid']+"|"+response[0]['name'];
        }
        for (const n in response) {
          let allname={};
          allname["label"]=response[n]['name'];
          allname["value"]=response[n]['uuid']+"|"+response[n]['name'];    
          temp[n]=allname;
        }
    
        this.allOperatorInfo = temp;
    }
    savePop = function (popupModel) {
        var cell = this.graph.getCell(popupModel.id);
        if(!cell) {
            let d_animate={}
            d_animate["left"]="2000px"
            $("#right-side-test").animate(d_animate);
            $("#right-side-test").hide();
            return;
        }
        if(!popupModel.selectedType){
          cell.remove();
          $("#right-side-test").animate({
                      'left' : '2000px'
          });
          $("#right-side-test").hide();
            return;
        }
        var temp = popupModel.selectedType.split('|');
        popupModel.modelData.operators[0].operatorInfo.ref.uuid = temp[0];
        popupModel.modelData.operators[0].operatorInfo.ref.name = temp[1];
       //  cell.attr('model-data',popupModel.modelData); // commented since model is already binded
        cell.attr('text', { text:popupModel.modelData.name});
         $("#right-side-test").animate({
                     'left' : '2000px'
         });
         $("#right-side-test").hide();
        this._sharedDataService.setData(this.graph.getCells());
        //console.log(this._jointjsService.convertGraphToDag(this.graph.getCells()))
       };
       changeType(){
        this.popupModel.modelData.name=this.popupModel.selectedType.split("|")[1]
    }
    
    latestStatus(statuses){
        var latest;
        statuses.forEach(function (status) {
            if(latest){
                if(status.createdOn > latest.createdOn){
                    latest = status
                }
            }
            else {
                latest = status;
            }
        });
        return latest;
    }

    updateGraphStatus(data){
        let statusDag = data.status.length == 0 ? 'InProgress' : this.latestStatus(data.status).stage;
    //  this.$parent.allowReExecution = false;
    //     // if(['Killed','Failed'].indexOf(statusDag) > -1)
    //     //   $scope.$parent.allowReExecution = true;
    //     // else
    //     //   $scope.$parent.allowReExecution = false;

        let dagid = "dag_0";
        $(".status[element-id=" + dagid + "] .statusImg").attr("xlink:href","assets/img/"+statusDag+".svg");
        $(".status[element-id=" + dagid + "] .statusTitle").text(statusDag);
        
        data.status.forEach(function (status) {
          $(".status[element-id=" + dagid + "]").attr(status.stage,status.createdOn);
        });
        let stages=data["stages"]
        Object.keys(stages).forEach(val => {
        let statusStage = stages[val].status.length == 0 ? 'InProgress' : this.latestStatus(stages[val].status).stage;
        let stageid =  stages[val].stageId; 
        $(".status[element-id=" + stageid + "] .statusImg").attr("xlink:href","assets/img/"+statusStage+".svg");
          $(".status[element-id=" + stageid + "] .statusTitle").text(statusStage);
            stages[val].status.forEach(function (status) {
            $(".status[element-id=" + stageid + "]").attr(status.stage,status.createdOn);
          });
        let tasks=stages[val].tasks
        Object.keys(tasks).forEach(val => {
            let  statusTask = tasks[val].status.length == 0 ? 'InProgress' : this.latestStatus( tasks[val].status).stage;
            let  taskid =  tasks[val].taskId ;
            $(".status[element-id=" + taskid + "] .statusImg").attr("xlink:href","assets/img/"+statusTask+".svg");
            $(".status[element-id=" + taskid + "] .statusTitle").text(statusTask);
            tasks[val].status.forEach(function (status) {
                $(".status[element-id=" + taskid + "]").attr(status.stage,status.createdOn);
            });
          });
        });
    }

    getPipeline():object{
        console.log(this.graph.getCells())
        let tem=this._jointjsService.convertGraphToDag(this.graph.getCells())
        return tem;
    }

    contextMenu1(d_d3,menu,d_this) {
        d_d3.select("#paper").selectAll('.context-menu').data([1])
            .enter()
            .append('div')
            .attr('class', 'context-menu');

        // close menu
        d_d3.select('body').on('click.context-menu', function() {
            d_d3.select('.context-menu').style('display', 'none');
        });
      
        var elm = this;
        d_d3.selectAll('.context-menu')
            .html('')
            .append('ul')
            .selectAll('li')
            .data(menu).enter()
            .append('li')
            .html(function(d) {
				return d.title;
			})
            .on('click', function(d,index) {
                d.action(elm,d,d_this);
                d_d3.select('.context-menu').style('display', 'none');
            })
        console.log(event)
        // show the context menu
        d_d3.select('.context-menu')
            .style('left', (event["layerX"] -2) + 'px')
            .style('top', (event["layerY"] - 12) + 'px')
            .style('display', 'block');
            event.preventDefault();
        
    };

    getMenuItem(id,cell):any{
        let menuItem=[];
        let status = $(".status[element-id=" + id + "] .statusTitle")[0].innerHTML;
        let type = cell.attributes.elementType;
        let isGroupExec;
        let isExec;
        if(type.slice(-4) == 'Exec'){
            if(type.slice(-9) == 'groupExec'){
                isGroupExec = true;
            }
            else {
                isExec = true;
            }
            type = type.slice(0,-4);
        }

        if(type == 'stage' || type == 'dag'){
            let item={}
			item["action"]=this.actionEvent
            if(status && (status=='NotStarted' || status=='Resume')){
                item["title"]="On Hold"
                item["type"]="onhold"
                item["status"]="OnHold"
                item["dag_uuid"]=id
                item["cell"]=cell;
                menuItem.push(item);
            }
            else if(status && status=='InProgress'){
                item["title"]="Kill"
                item["type"]="killexecution"
                item["status"]="Killed"
                item["dag_uuid"]=id
                item["cell"]=cell;
                menuItem.push(item);
            }
            else if(status && status=='OnHold'){
                item["title"]="Kill"
                item["type"]="killexecution"
                item["status"]="Killed"
                item["dag_uuid"]=id
                item["cell"]=cell;
                menuItem.push(item);
            }
        }
        else{
           
           let item={}
           item["action"]=this.actionEvent
           item["title"]="Show Details"
           item["type"]="element"
           item["dag_uuid"]=id
           item["cell"]=cell;
           menuItem.push(item);
            if(isExec || isGroupExec){
                let item={}
                item["action"]=this.actionEvent
                if(status && (status=='Completed') ||(status== 'Failed')|| (status== 'InProgress')){
                    
                    if(isExec && (status=='Completed')){
                        item["title"]="Show Results"
                        item["type"]="results"
                        item["cell"]=cell;
                        item["dag_uuid"]=id
                        menuItem.push(item);
                       
                    }
                    if(isGroupExec){
                        item["title"]="Show Results"
                        item["type"]="results"
                        item["cell"]=cell;
                        item["dag_uuid"]=id
                        menuItem.push(item);
                     
    
                    }
                }
                else if(status && (status=='NotStarted' || status=='Resume')){
                    item["title"]="On Hold"
                    item["type"]="onhold"
                    item["status"]="OnHold"
                    item["cell"]=cell;
                    item["dag_uuid"]=id
                    menuItem.push(item);
                }

                else if(status && status=='OnHold'){
                    item["title"]="Resume"
                    item["status"]="Resume"
                    item["type"]="resume"
                    item["dag_uuid"]=id
                    item["cell"]=cell;
                    menuItem.push(item);
                   
                }
                if(status && status=='InProgress'){
                    item["title"]="Kill"
                    item["status"]="Killed"
                    item["type"]="killexecution"
                    item["cell"]=cell;
                    item["dag_uuid"]=id
                    menuItem.push(item);
                    
                }
                
            }
        }
        return menuItem;
    }
    actionEvent(elm,data,d_this){
        console.log(elm);
        if(data.type == "killexecution" || data.type=="resume" || data.type=="onhold"){
            d_this.setStatus(data);
        }
        else if(data.type == "element"){
            d_this.navigateTo(data);
        }
        else if(data.type == "results"){
        d_this.showResult(data);
        }
    }
    setStatus(data){
       
        let uuid;
        let version="";
        let status;
        let stageId="";
        let taskId="";
        let modeldata=data.cell.attributes["model-data"]
        if(data.cell.attributes.elementType=="dag"){
           status=data.status
           uuid=modeldata.uuid;
        }
        else if(data.cell.attributes.elementType=="stage"){
            status=data.status
            uuid=data.dag_uuid;
            stageId=modeldata.stageId;
        }
        else{
            status=data.status
            uuid=data.dag_uuid;
            stageId=data.cell.attributes.parentStage;
            taskId=modeldata.taskId  
            console.log(stageId)
            console.log(taskId)    
        }
        
        this._dataPipelineService.setStatus(uuid,version,status,stageId,taskId)
        .subscribe(
            response =>{
                console.log(response)
            },
            error => console.log("Error :: " + error)
            );
        
    }

    navigateTo(data){
        console.log(data) 
        let d_this=this;
        let modeldata=data.cell.attributes["model-data"].operators[0].operatorInfo.ref;
        let type=data.cell.attributes.elementType
        let uuid=modeldata.uuid;
        let version=modeldata.version;
        debugger;
        this._dataPipelineService.getMetaIdByExecId(uuid,version,type)
        .subscribe(
            response =>{
                let _moduleUrl=this.appMetadata.getMetadataDefs(response.type)['moduleState']
                let _routerUrl=this.appMetadata.getMetadataDefs(response.type)['detailState']
                d_this.router.navigate(["./"+_moduleUrl+"/"+_routerUrl, response.uuid, response.version || "", 'true'],{relativeTo:this.activatedRoute});
            },
            error => console.log("Error :: " + error)
            );
    }
    showResult(data){
        this.IsGraphShow=false;
        this.IsGroupGraphShow =false;
        this.IsTableShow = false;
        var apis = {
            dq : {name:'dq', label: 'DataQual'},
            dqgroup : {name:'dqgroupexec', label: 'DataQualGroup',url :'dataqual/getdqExecBydqGroupExec?'},
            profile : {name:'profile', label: 'Profile'},
            profilegroup : {name:'profilegroupexec', label: 'ProfileGroup', url : 'profile/getProfileExecByProfileGroupExec?'},
            load : {name:'load', label: 'Load'},
            map : {name:'map', label: 'Map', url:""},
            model : {name:'model', label: 'Model'},
            rule : {name:'rule', label: 'Rule'},
            rulegroup : {name:'rulegroupexec', label: 'RuleGroup',url:'rule/getRuleExecByRGExec?'},
          }
        console.log(data)
        this.colsdata=null;
        this.cols=null;
        let modeldata=data.cell.attributes["model-data"].operators[0].operatorInfo.ref;
        let type=this.appMetadata.getMetadataDefs(data.cell.attributes.elementType.toLowerCase())['metaType']
        let uuid=modeldata.uuid;
        let version=modeldata.version;
        if(type.slice(-4) == 'Exec' || type.slice(-4) == 'exec'){
            if(type.slice(-9) == 'groupExec' || type.slice(-9) == 'groupexec' ){
                this.IsGroupGraphShow = true;
            }
            else {
              this.IsTableShow = true;
            }
            type = type.slice(0,-4);
        }
        else{
            this.IsTableShow = true;

        }
        this.params={
           "ref":{} 
        };
        this.params["url"]=apis[type].url;
        this.params["type"]=apis[type].name;
        this.params["id"]=uuid;
        this.params["name"]=data.cell.attributes["model-data"].operators[0].operatorInfo.ref.name;
        this.params["elementType"]=apis[type].name;;
        this.params["uuid"]=uuid;
        this.params["version"]=version;
        this.params.ref["id"]=uuid;
        this.params.ref["name"] =data.cell.attributes["model-data"].operators[0].operatorInfo.ref.name;
        this.params.ref["type"]=apis[type].name;;
        this.params.ref["version"]=version;
        console.log( this.params);
        if(this.IsTableShow == true){
        this._dataPipelineService.getResults(uuid,version,type)
        .subscribe(
            response =>{
              this.colsdata=response
              let columns=[];
              
              if(response.length && response.length > 0){
                Object.keys(response[0]).forEach(val=>{
                    if (val != "rownum"){
                    let width=(val.split("").length * 9+10)+"px"
                    columns.push({"field":val, "header":val,colwidth:width});
                    }
                });
              }
              
              this.cols=columns
              this.columnOptions = [];
              for(let i = 0; i < this.cols.length; i++) {
                  this.columnOptions.push({label: this.cols[i].header, value: this.cols[i]});
              }
            },
            error => console.log("Error :: " + error)
            );
        }
        else{
            setTimeout(() => {
            // this.d_JointjsComponent.params={};
            //  this.d_JointjsComponent.IsGroupGraphShow=false
            // this.d_JointjsGroupComponent.IsGraphShow=true;
            // this.d_JointjsGroupComponent.createGraph();
            // this.d_JointjsGroupComponent.generateGroupGraph(this.params);
            // this._jointElementMouseOver();
        }, 1000);
          
        }
    }

    public goBack() {
       if(this.d_JointjsGroupComponent.IsGraphShow == false){
        this.d_JointjsGroupComponent.IsGraphShow=true;
       }
       else{
        this.IsGroupGraphShow=false
        this.IsGraphShow=true;   
       }
    }
}

