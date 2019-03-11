/**
 *
 */
InferyxApp=angular.module('InferyxApp');
InferyxApp.factory('setDataFactory',function(dagMetaDataService){
  var factory={};
  isObjectEmpty = function(execParam){
    for(var prop in execParam) {
      if(execParam.hasOwnProperty(prop))
          return false;
  }
  return true;
  }
  factory.setEXEC_PARAMS=function(operators){
    if(operators[0].operatorParams !=null && !isObjectEmpty(operators[0].operatorParams.EXEC_PARAMS)){
      if(operators[0].operatorParams.EXEC_PARAMS.paramListInfo !=null){
        var data=operators[0].operatorParams.EXEC_PARAMS.paramListInfo;
        for(var i=0;i<operators[0].operatorParams.EXEC_PARAMS.paramListInfo.length;i++){
          delete operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].paramName;
          delete operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].paramType;
          delete operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].ref.name;
          if(operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].attributeInfo !=null && operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].attributeInfo.length >0){
            
            for(var j=0;j<operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].attributeInfo.length;j++){
              delete operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].attributeInfo[j].ref.name;
              delete operators[0].operatorParams.EXEC_PARAMS.paramListInfo[i].attributeInfo[j].attrName;
            }
          }

        }
      }
    }
    console.log(operators);
    return operators;
  }
  return factory;
});
InferyxApp.factory('graphService',function(dagMetaDataService,setDataFactory){

    var factory={}
    factory.convertDagToGraph=function(dag,isTemplate,addMode){
      var cells = [];
      var links = [];
      cells.push( new joint.shapes.devs.Model(
        angular.merge({},dagMetaDataService.getCustomElement('dag',isTemplate,addMode),{
          id: "dag_0",
          position: { x: dag.xPos || 30, y : dag.yPos || 250 },
          attrs: {
              '.body': { "element-id":"dag_0",
               "active":true,
                "model-data": JSON.stringify({name:dag.name,uuid:dag.uuid,version:dag.version,isTemplate:true})},
              '.status': {
                'element-id' : 'dag_0',
              },
              //dag.name
              text: { text:'Start',y:'60px','font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray;','text-anchor': 'start'} }
          }
        })
      ));

      var stageYPos = 250;
      var taskXPos = 300;
      var taskYPos = -50;
      
      if(!dag.stages){
        cells.push( new joint.shapes.devs.Model(
          angular.merge({},dagMetaDataService.getCustomElement('stage',false,addMode),{
            id: "stage_0",
            elementType : 'stage',
            "model-data": {
              uuid: "stage_0",
              name : 'new stage',
              version:"N/a",
              operators : [ {
                operatorInfo :[ {
                  ref : {
                    name : 'new stage',
                    type : 'stage',
                    uuid:"stage_0",version:"N/a"
                  }
                }]
                
              }],
              isTemplate:true
            },
            position: { x: 150 , y: stageYPos },
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
                }],
                isTemplate:true
              },
              '.body': {
                "element-id": "stage_0",
                "active":true,
                "model-data": JSON.stringify({name:'stage',uuid:"stage_0",version:"N/a",isTemplate:true})
              },
               text: { text: 'stage'}
            }
          })
        ));
        links.push(
          new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
              source: { id: "dag_0",port: 'out' }, target: { id: "stage_0", port:'in' },
              attrs: { '.connection': { 'source-id':"dag_0"  } }
          }))
        );
      }
      var stagecount=0;
      angular.forEach(dag.stages,function (stage,stageKey) {
        stageYPos = stageYPos + 100;
        taskXPos  = 300;
        taskYPos  = -40+stageKey*80;
        stage.stageId = stage.stageId.length > 3 ? stage.stageId : "stage_"+stage.stageId;
        if(stagecount == 0 && isTemplate == true){
          stagecount=1;
          cells.push( new joint.shapes.devs.Model(
            angular.merge({},dagMetaDataService.getCustomElement('stage',false,addMode),{
              id: stage.stageId,
              elementType : 'stage',
              "model-data": stage,
              position: { x: stage.xPos || 200 , y: stage.yPos || stageYPos },
              attrs: {
                  '.status': {
                    'element-id' : stage.stageId,
                  },
                  '.body':{
                    "element-id": stage.stageId,
                    "active":true
                  },
                  text: { text: stage.name}
              }
            })
          ));
        }else{
          cells.push( new joint.shapes.devs.Model(
            angular.merge({},dagMetaDataService.getCustomElement('stage',isTemplate,addMode),{
              id: stage.stageId,
              elementType : 'stage',
              "model-data": stage,
              position: { x: stage.xPos || 200 , y: stage.yPos || stageYPos },
              attrs: {
                  '.status': {
                    'element-id' : stage.stageId,
                  },
                  '.body':{
                    "element-id": stage.stageId,
                    "active":!isTemplate
                  },
                  text: { text: stage.name}
              }
            })
          ));
        }
        if(stage.dependsOn.length == 0) stage.dependsOn.push('dag_0');
        angular.forEach(stage.dependsOn,function (dependancy) {
          var s = dependancy=="dag_0"? "dag_0" : ( dependancy.length > 3 ? dependancy : "stage_"+dependancy);
          links.push(
            new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
                source: { id: s,port: 'out' }, target: { id: stage.stageId, port:'in' },
                attrs: { '.connection': { 'source-id':s  } }
            }))
          );
        });
        if(stage.tasks.length > 0){
          taskXPos = taskXPos + 80;
          angular.forEach(stage.tasks,function (task,taskKey) {
            var o = task.operators[0];
            var type = o.operatorInfo[0].ref.type;
            taskXPos = taskXPos + 80
            taskYPos = taskYPos + 80;
            task.taskId = task.taskId.length > 3 ? task.taskId : stage.stageId+'_'+"task_"+task.taskId
            cells.push( new joint.shapes.devs.Model(
              angular.merge({},dagMetaDataService.getCustomElement(type,isTemplate,addMode),{
                  id: task.taskId,
                  elementType : type,
                  "model-data": task,
                  parentStage : stage.stageId,
                  dagversion:dag.version,
                  position: { x: task.xPos || taskXPos, y: task.yPos || taskYPos },
                  attrs: {
                      '.body': {
                          'element-id' : task.taskId,
                          "model-data": JSON.stringify(task),
                          "active":!isTemplate,
                         
                      },
                      '.status': {
                          'element-id' : task.taskId,
                      },
                      text: { text: task.name}
                  }
              })
            ));
            task.dependsOn.length == 0 ? task.dependsOn.push(stage.stageId) : '';
            if(task.dependsOn.length > 0){
              angular.forEach(task.dependsOn,function (dependancy) {
                var s = dependancy.length > 3 ? dependancy :  stage.stageId+"_task_"+dependancy;
                links.push(
                  new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
                      source: { id: s,port: 'out'}, target: { id: task.taskId,port: 'in' },
                      attrs: {'.connection': {'source-id':s  }}
                  }))
                )
              });
            }
          });
        }
      });
      return {cells:cells,links:links};
    }
    factory.convertGraphToDag=function(cells,isEXEC_PARAMS){
      //var getJointGrapObject = JSON.parse($scope.graph.content);
      var dag = {};
      var stages = {};
      var tasks = {};
      var links = {};

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
      // return;
      angular.forEach(cells, function (val) {
       //debugger
       // console.log(val.attributes.attrs[".body"].isTemplate);
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
          if(val.attributes.attrs[".body"].active ==true){
          stages[val.id] ={
            "stageId" : val.attributes.actualId || val.id,
            "dependsOn" : [],
            "active":val.attributes.attrs[".body"].active ==true?"Y":"N",
            "name" : val.attributes['model-data'].name,
            "xPos" : val.attributes.position.x,
            "yPos" : val.attributes.position.y,
            "tasks" : []
            // "properties":val.properties ? val.properties : {}
          };
          }
        }

        else if (val.attributes.type == 'devs.Model' && dagMetaDataService.validTaskTypes.indexOf(val.attributes.elementType) > -1) {
          if(val.attributes.attrs[".body"].active ==true){
          tasks[val.id] = {
            "taskId":val.attributes.actualId || val.id,
            "dependsOn" : [],
            "name" : val.attributes['model-data'].name,
            "active":val.attributes.attrs[".body"].active ==true?"Y":"N",
            "xPos" : val.attributes.position.x,
            "yPos" : val.attributes.position.y,
            "operators" : isEXEC_PARAMS == true ?setDataFactory.setEXEC_PARAMS(val.attributes['model-data'].operators):val.attributes['model-data'].operators || {}
            // "properties":val.properties ? val.properties : {}
          };
          }
        }

        else if(val.attributes.type=='link'){
          links[val.id] = val.attributes;
        }

      });

      angular.forEach(links,function (val) {
        var actualId = getActualId(val.source.id);
        if(tasks[val.target.id] && tasks[val.source.id]){
          if(tasks[val.target.id].dependsOn.indexOf(actualId) == -1){
            tasks[val.target.id].dependsOn.push(actualId);
          }
        }

        else if(stages[val.target.id] && stages[val.source.id]){
          if(stages[val.target.id].dependsOn.indexOf(actualId) == -1){
            stages[val.target.id].dependsOn.push(actualId);
          }
        }

        // else if(dag[val.target.id])
        //   dag[val.target.id].dependsOn.push(getActualId(val.source.id));


      });
      angular.forEach(links,function (val) {
        if(stages[val.source.id] && tasks[val.target.id]){
          stages[val.source.id].tasks.push(tasks[val.target.id]);
        }
        else if(tasks[val.source.id] && tasks[val.target.id]){
          if(!tasks[val.source.id].parallels){
            tasks[val.source.id].parallels = [];
          }
          tasks[val.source.id].parallels.push(tasks[val.target.id]);
        }
        else if(stages[val.source.id] && stages[val.target.id]){
          dag['dag_0'].stages.push(stages[val.target.id]);
        }
        else if(dag[val.source.id] && stages[val.target.id]){
          dag[val.source.id].stages.push(stages[val.target.id]);
        }
      });
      var loopCount = 1;
      do {
        loopCount--;
        var loop = false;
        angular.forEach(dag['dag_0'].stages,function (stage) {
          angular.forEach(stage.tasks,function (task) {
            if(task.parallels){
              angular.forEach(task.parallels,function (parallel) {
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
      angular.forEach(dag,function(val){
        inArrayFormat.push(val);
      });
       console.log("*******DAG JSON*******",JSON.stringify(inArrayFormat[0].stages));
       return inArrayFormat;
    }

   
   return factory;
});



