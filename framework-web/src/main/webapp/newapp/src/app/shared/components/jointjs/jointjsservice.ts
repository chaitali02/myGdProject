import { Injectable, Inject } from '@angular/core';
import {AppMetadata} from '../../../app.metadata';
declare var require: any;
const joint = require('../../../../../node_modules/jointjs/dist/joint.js');

@Injectable()
export class JointjsService {
  baseUrl: any;
  sessionId: string;
  url:any;
  constructor(private appMetadata: AppMetadata) {
  }

    convertDagToGraph(dag){
        var cells = [];
        var links = [];
        let dag_merge=this.appMetadata.assignDeep(this.appMetadata.getCustomElement('dag'),{
                id: "dag_0",
                position: { x: dag.xPos || 30, y : dag.yPos || 250 },
                "model-data":{uuid:dag.uuid,version:dag.version},
                attrs: {
                    '.body': { 
                    "element-id":"dag_0",
                    "model-data": JSON.stringify({name:dag.name,uuid:dag.uuid,version:dag.version})},
                    '.status': {
                        'element-id' : 'dag_0',
                    },
                    
                    text: { text: dag.name,y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' } }
                }
            });
        cells.push( new joint.shapes.devs.Model(dag_merge));

        var stageYPos = 50;
        var taskXPos = 300;
        var taskYPos = -50;
        if(!dag.stages){
            cells.push( new joint.shapes.devs.Model(
                this.appMetadata.assignDeep(this.appMetadata.getCustomElement('stage'),{
                    id: "stage_0",
                    elementType : 'stage',
                    "model-data": {
                        uuid: "stage_0",
                        name : 'new stage',
                        version:"N/a",
                        operators : [ {
                            operatorInfo : [{
                                ref : {
                                    name : 'new stage',
                                    type : 'stage',
                                    uuid:"stage_0",version:"N/a"
                                }
                            }]
                        }]
                    },

                    position: { x: 200 , y: stageYPos },
                    attrs: {
                        "model-data": {
                            uuid: "stage_0",
                            name : 'new stage',
                            version:"N/a",
                            operators : [ {
                                operatorInfo : [{
                                    ref : {
                                        name : 'new stage',
                                        type : 'stage',
                                        uuid:"stage_0",version:"N/a"
                                    }
                                }]
                            }]
                        },

                        '.body': {
                            "element-id": "stage_0",
                            "model-data": JSON.stringify({name:'stage',uuid:"stage_0",version:"N/a"})
                        },
                        text: { text: 'stage',y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' }}
                    }
                })
            ));
            links.push(
                new joint.dia.Link(this.appMetadata.assignDeep(this.appMetadata.getDefaultLink(),{
                    source: { id: "dag_0",port: 'out' }, target: { id: "stage_0", port:'in' },
                    attrs: { '.connection': { 'source-id':"dag_0"  } }
                }))
            );
        }//End If
        else{
        dag.stages.forEach(function (stage,stageKey){
            stageYPos = stageYPos + 100;
            taskXPos  = 300;
            taskYPos  = -40+stageKey*80;
            stage.stageId = stage.stageId.length > 3 ? stage.stageId : "stage_"+stage.stageId;
            cells.push( new joint.shapes.devs.Model(
                this.appMetadata.assignDeep(this.appMetadata.getCustomElement('stage'),{
                    id: stage.stageId,
                    elementType : 'stage',
                    "model-data": stage,
                    position: { x: stage.xPos || 200 , y: stage.yPos || stageYPos },
                    attrs: {
                        '.status': {
                        'element-id' : stage.stageId,
                        },
                        '.body':{
                        "element-id": stage.stageId
                        },
                        text: { text: stage.name,y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' }}
                    }
                })
            ));

            if(stage.dependsOn.length == 0)stage.dependsOn.push('dag_0');

            stage.dependsOn.forEach(function (dependancy){   
                var s = dependancy=="dag_0"? "dag_0" : ( dependancy.length > 3 ? dependancy : "stage_"+dependancy);
                links.push(
                    new joint.dia.Link(this.appMetadata.assignDeep(this.appMetadata.getDefaultLink(),{
                        source: { id: s,port: 'out' }, target: { id: stage.stageId, port:'in' },
                        attrs: { '.connection': { 'source-id':s  } }
                    }))
                );
            },this);
            
            if(stage.tasks.length > 0){
                taskXPos = taskXPos + 80;
                stage.tasks.forEach(function (task,taskKey){  
                    var o = task.operators[0];
                    var type = o.operatorInfo[0].ref.type;
                    taskXPos = taskXPos + 80
                    taskYPos = taskYPos + 80;
                    task.taskId = task.taskId.length > 3 ? task.taskId : stage.stageId+'_'+"task_"+task.taskId
                    cells.push( new joint.shapes.devs.Model(
                        this.appMetadata.assignDeep(this.appMetadata.getCustomElement(type),{
                            id: task.taskId,
                            elementType : type,
                            "model-data": task,
                            parentStage : stage.stageId,
                            position: { x: task.xPos || taskXPos, y: task.yPos || taskYPos },
                            attrs: {
                                '.body': {
                                'element-id' : task.taskId,
                                "model-data": JSON.stringify(task),
                                },
                                '.status': {
                                    'element-id' : task.taskId,
                                },
                                text: { text: task.name ,y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray' }}
                            }
                        })
                    ));
                    task.dependsOn.length == 0 ? task.dependsOn.push(stage.stageId) : '';
                    if(task.dependsOn.length > 0){
                        task.dependsOn.forEach(function (dependancy){ 
                            var s = dependancy.length > 3 ? dependancy :  stage.stageId+"_task_"+dependancy;
                            links.push(new joint.dia.Link({
                                    source: { id: s,port: 'out'}, target: { id: task.taskId,port: 'in' },
                                    attrs: {'.connection': {'source-id':s ,stroke: 'gray' },'.marker-target': { d: 'M 10 0 L 0 5 L 10 10 z',fill:'gray',stroke:'gray' }}
                                }))
                            
                        });
                    }
                },this);
            }
        },this);
    }
        return {cells:cells,links:links};
    }

     
    _dagToStage (s,t,inboundLinks){
        if(inboundLinks.length == 0 && s==='dag' && t ==='stage'){
            return true;
        }
        else
        return false ;
    }
    _stageToStage (s,t,inboundLinks){
        if(inboundLinks.length == 0 && s==='stage' && t==='stage'){
             return false;
        }
        else return true;
    }
    _stageToTask (s,t,inboundLinks){
        return inboundLinks.length == 0 && s==='stage' && this.appMetadata.validTaskTypes.indexOf(t) > -1;
      }
    _taskToTask (s,t,inboundLinks,g,cellViewS,cellViewT){
        var notLinkedWithStage = true;
        if(inboundLinks.length > 0){
          var firstLinkedCell = g.getCell(inboundLinks[0].attributes.source.id);
          notLinkedWithStage = false//firstLinkedCell.attributes.elementType == 'stage' ? false : true;
        }
        if(cellViewS.model.attributes.parentStage && cellViewT.model.attributes.parentStage && cellViewS.model.attributes.parentStage != cellViewT.model.attributes.parentStage){
          return false;
        }
        return notLinkedWithStage && this.appMetadata.validTaskTypes.indexOf(s) > -1 && this.appMetadata.validTaskTypes.indexOf(t) > -1;
      }
    
    public validate(graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView):boolean {
        var isValid = false;
        if(cellViewS.model.id === cellViewT.model.id){
          return false;
        }
        try{
            var cellT = graph.getCell(cellViewT.model.id)
            inboundLinks = graph.getConnectedLinks(cellT,{inbound : true});
            //console.log(inboundLinks);
        }
        catch(e){
            var inboundLinks = [];
        }
    
        var sType = cellViewS.model.attributes.elementType;
        var tType = cellViewT.model.attributes.elementType;
        isValid = this._stageToStage(sType,tType,inboundLinks)  && this._dagToStage(sType,tType,inboundLinks) || this._stageToTask(sType,tType,inboundLinks)  || this._taskToTask(sType,tType,inboundLinks,graph,cellViewS,cellViewT);
        return isValid;
    };
    

    convertGraphToDag=function(cells):object{
        //var getJointGrapObject = JSON.parse($scope.graph.content);
        var dag = {};
        var stages = {};
        var tasks = {};
        var links = {};
        let d_appMetadata=this.appMetadata
        function getActualId (id){
          if(dag[id]){
            // return dag[id].dagId || 'default';
            return 'dag_0';
          }
          else if(stages[id]){
            return stages[id].stageId;
          }
          else if(tasks[id]){
            return tasks[id].taskId;
          }
         };
        //var cells = $scope.graph.getCells();
        console.log(cells);
        cells.forEach(function (val) {
          if (val.attributes.type == 'devs.Model' && val.attributes.elementType == 'dag') {
            dag[val.id] ={
              "dagId" : val.attributes.actualId || val.id,
              "dependsOn" : [],
              "stages" : [],
              "name" : val.attributes.attrs.text.text || '',
              "xPos" : val.attributes.position.x,
              "yPos" : val.attributes.position.y
              // "properties":val.properties ? val.properties : {}
            };
          }
  
          else if (val.attributes.type == 'devs.Model' && val.attributes.elementType == 'stage') {
            stages[val.id] ={
              "stageId" : val.attributes.actualId || val.id,
              "dependsOn" : [],
              "name" : val.attributes['model-data'].name,
              "xPos" : val.attributes.position.x,
              "yPos" : val.attributes.position.y,
              "tasks" : []
              // "properties":val.properties ? val.properties : {}
            };
          }
  
          else if (val.attributes.type == 'devs.Model' &&  d_appMetadata.validTaskTypes.indexOf(val.attributes.elementType) > -1) {

            tasks[val.id] = {
              "taskId":val.attributes.actualId || val.id,
              "dependsOn" : [],
              "name" : val.attributes['model-data'].name,
              "xPos" : val.attributes.position.x,
              "yPos" : val.attributes.position.y,
              "operators" : val.attributes['model-data'].operators || {}
              // "properties":val.properties ? val.properties : {}
            };
          }
  
          else if(val.attributes.type=='link'){
            links[val.id] = val.attributes;
          }
  
        });
  
        Object.keys(links).forEach(val=> {
          var actualId = getActualId(links[val].source.id);
          if(tasks[links[val].target.id] && tasks[links[val].source.id]){
            if(tasks[links[val].target.id].dependsOn.indexOf(actualId) == -1){
              tasks[links[val].target.id].dependsOn.push(actualId);
            }
          }
  
          else if(stages[links[val].target.id] && stages[links[val].source.id]){
            if(stages[links[val].target.id].dependsOn.indexOf(actualId) == -1){
              stages[links[val].target.id].dependsOn.push(actualId);
            }
          }
  
          // else if(dag[val.target.id])
          //   dag[val.target.id].dependsOn.push(getActualId(val.source.id));
  
  
        });
        Object.keys(links).forEach(val=> {
          if(stages[links[val].source.id] && tasks[links[val].target.id]){
            stages[links[val].source.id].tasks.push(tasks[links[val].target.id]);
          }
          else if(tasks[links[val].source.id] && tasks[links[val].target.id]){
            if(!tasks[links[val].source.id].parallels){
              tasks[links[val].source.id].parallels = [];
            }
            tasks[links[val].source.id].parallels.push(tasks[links[val].target.id]);
          }
          else if(stages[links[val].source.id] && stages[links[val].target.id]){
            dag['dag_0'].stages.push(stages[links[val].target.id]);
          }
          else if(dag[links[val].source.id] && stages[links[val].target.id]){
            dag[links[val].source.id].stages.push(stages[links[val].target.id]);
          }
        });
        var loopCount = 1;
        do {
          loopCount--;
          var loop = false;
          dag['dag_0'].stages.forEach(function (stage) {
            stage.tasks.forEach(function (task) {
              if(task.parallels){
                task.parallels.forEach(function (parallel) {
                  if(parallel.parallels && parallel.parallels.length > 0){
                    loop = true;
                  }
                  if(stage.tasks.indexOf(parallel) == -1)
                    stage.tasks.push(parallel);
                });
                delete task.parallels;
              }
            });
          });
          if(loop){
            loopCount++;
          }
        }
        while(loopCount > 0);
        //console.log('dag',dag);
  
        var inArrayFormat = [];
        Object.keys(dag).forEach(function(val){
          inArrayFormat.push(val);
        });
        // console.log("*******DAG JSON*******",JSON.stringify(dag));
         return dag["dag_0"];
      }

}