/**
 *
 */

DataPipelineModule= angular.module('DataPipelineModule');

DataPipelineModule.directive('gridResultsDirective',function ($rootScope,$compile,$location,$http, $filter) {
 return {
   scope : {
     name: "=",
     hcolumns:"=",
     data: "="
   },
   link: function ($scope, element, attrs) {   
     var initialised = false;
     $scope.filteredRows = [];
     $scope.pagination={
       currentPage:1,
       pageSize:10,
       paginationPageSizes:[10, 25, 50, 75, 100],
       maxSize:5,
     }
     
     $scope.gridOptions = {
       rowHeight: 40,
       useExternalPagination: true,
       exporterMenuPdf: false,
       exporterPdfOrientation: 'landscape',
       exporterPdfPageSize: 'A4',
       exporterPdfDefaultStyle: {fontSize: 9},
       exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
       enableGridMenu: true,
       rowHeight: 40,
       onRegisterApi:  function(gridApi){
         $scope.gridApi = gridApi;
         $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
       }
     }

         
     $scope.getGridStyle = function() {
       var style = {
         'margin-top': '10px',
         'margin-bottom': '10px',
       }
       if ($scope.filteredRows && $scope.filteredRows.length >0) {
         style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 60) + 'px';
       }
       else{
         style['height']="100px";
       }
       return style;
     }

     $scope.filterSearch =function (s) {
       var data = $filter('filter')($scope.orignalData, s, undefined);
       $scope.getResults(data)
     }

     $scope.$on('generateResults',function (e,params) {
       $rootScope.showGrid=true;
       $rootScope.showGroupDowne=false;
       $('#errorMsg').hide();
       $scope.searchtext = '';
       if(initialised){
         $scope.gridOptions.columnDefs = [];
         $scope.gridOptions.data = [];
       }
       $('#resultsloader').show();
       $('#resultswrapper').hide();
       var typeexec;
       if(params.type=="dataqual"){
         typeexec="dqexec";
       }else{
         typeexec=params.type+"exec";
       }

       $scope.downloadDetail={};
       $scope.downloadDetail.uuid=params.id;
       $scope.downloadDetail.version=params.version;
       $scope.downloadDetail.type=params.type; 
       var baseurl=$location.absUrl().split("app")[0];
       $http.get(baseurl+'metadata/getNumRowsbyExec?action=view&execUuid='+params.id+'&execVersion='+params.version+'&type='+typeexec).then(function (res) {
         var mode=res.data.runMode;
        
         var url;
         if(params.type == "train" || params.type == "predict" || params.type == "simulate"){
           url=baseurl+"model/"+params.type+"/getResults?action=view&uuid="+params.id+"&version="+params.version+"&mode="+mode+"&requestId=";
         }else{
           url=baseurl+params.type+"/getResults?action=view&uuid="+params.id+"&version="+params.version+"&mode="+mode+"&requestId=";
         }
         $scope.type=params.type
         $http({
               method: 'GET',
               url:url,
                 }).then(function (response,status,headers) {
                   debugger
                  $('#resultsloader').hide();
                  if(params.type == "train"){
                    $scope.trainData=response.data;
                    $('#resultswrapper').show();
                    renderTable(response.data);
                  }
                  else{
                   if(response.data.length >0){
                       $('#resultswrapper').show();
                      renderTable(response.data);}
                   else{  
                      $('#resultswrapper').hide();
                      $('#errorMsg').show();
                      $('#errorMsg').html('No data available.');
                   }
                  }
                 },function onError(err) {
                   $('#resultsloader').hide();
                   $('#errorMsg').show();
                   $('#errorMsg').html('Some Error Occured');
                 })
         });
       });
          
       function renderTable(data) {
         console.log('results table data',data);
         if($scope.type !="train"){
           $scope.orignalData = data;
           if($scope.orignalData.length >0){
             $scope.getResults($scope.orignalData);
           }
           var columns = []; 
           var count=0;
           angular.forEach(data[0], function(value, key) {
            count=count+1;
           })
           if(data.length && data.length > 0){
             angular.forEach(data[0],function (val,key) {
               var templateWithTooltip = `<div ng-mouseover="grid.appScope.onRowHover(row.entity,$event)" ng-mouseleave="grid.appScope.leave()" > <div class="ui-grid-cell-contents">{{ COL_FIELD }}</div></div>;`
               var hiveKey=["rownum","AttributeId","DatapodUUID","DatapodVersion","datapodUUID","datapodVersion",'version','sourceDatapodId','sourceDatapodVersion','sourceAttrId','targetDatapodId','targetDatapodVersion','targetAttrId','']
               if(hiveKey.indexOf(key) ==-1){
                var width;
                if(count >3){
                  width = key.split('').length + 12 + "%"
                 }
                 else{
                  width=(100/count)+"%";
                 }
                 columns.push({"name":key,"displayName":key.toLowerCase(),cellTemplate: templateWithTooltip, width:width,visible: true});
               }
               else if(hiveKey.indexOf(key) !=-1){
                var width;
                if(count >3){
                  width = key.split('').length + 12 + "%"
                 }
                 else{
                  width=(100/count)+"%";
                 }
              
                 columns.push({"name":key,"displayName":key.toLowerCase(),cellTemplate: templateWithTooltip, width:width,visible: false});
               }
             });
           }
           $scope.gridOptions.columnDefs = columns;
           console.log($scope.gridOptions.columnDefs)
           initialised = true;
         }else{
           $scope.modelresult=data;
         }
       }
           
       $scope.onRowHover = function(row,e) {
         $scope.mouseHowerRowValue=row;
         $('#tabletoshow').css('display','block')
         $('#tabletoshow').css('top',e.offsetY)
         $('#tabletoshow').css('left',e.offsetX)
       }
           
       $scope.leave = function(row){
         $('#tabletoshow').css('display','none')
       }
       $scope.selectPage = function(pageNo) {
         $scope.pagination.currentPage = pageNo;
       };

       $scope.onPerPageChange = function() {
         $scope.pagination.currentPage = 1;
         $scope.getResults($scope.orignalData)
       }

       $scope.pageChanged = function() {
         $scope.getResults($scope.orignalData)
       };

       $scope.getResults = function(params) {
         $scope.pagination.totalItems=params.length;
         if($scope.pagination.totalItems >0){
           $scope.pagination.to = ((($scope.pagination.currentPage - 1) * ($scope.pagination.pageSize))+1);
         }
         else{
           $scope.pagination.to=0;
         }
         if ($scope.pagination.totalItems < ($scope.pagination.pageSize*$scope.pagination.currentPage)) {
           $scope.pagination.from = $scope.pagination.totalItems;
         } else {
           $scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
         }
         var limit = ($scope.pagination.pageSize*$scope.pagination.currentPage);
         var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
         $scope.gridOptions.data=params.slice(offset,limit);
       }
       
       $scope.downloadTrainData=function(uuid){
         var linkElement = document.createElement('a');
         try {
             var blob = new Blob([$scope.trainData], {
                 type: "text/xml"
             });
             var url = window.URL.createObjectURL(blob);
             linkElement.setAttribute('href', url);
             linkElement.setAttribute("download", uuid+".txt");
             var clickEvent = new MouseEvent(
                 "click", {
                     "view" : window,
                     "bubbles" : true,
                     "cancelable" : false
                 });
             linkElement.dispatchEvent(clickEvent);
 
         } catch (ex) {
             console.log(ex);
         }
       }
       $scope.downloaddata=function(url,uuid){
         $http({
           method: 'GET',
           url:url,
           responseType: 'arraybuffer'
         }).success(function(data, status, headers) {
           headers = headers();  
           var filename = headers['filename'];
           var contentType = headers['content-type']; 
           var linkElement = document.createElement('a');
           try {
             var blob = new Blob([data], {
             type: contentType
           });
           var url = window.URL.createObjectURL(blob);
             linkElement.setAttribute('href', url);
             linkElement.setAttribute("download",filename);
             var clickEvent = new MouseEvent("click", {
               "view": window,
               "bubbles": true,
               "cancelable": false
             });
             linkElement.dispatchEvent(clickEvent);
           } catch (ex) {
             console.log(ex);
           }
         }).error(function(data) {
           console.log(data);
       }); 
       }
       window.downloadPiplineFile = function() {               
         var uuid = $scope.downloadDetail.uuid;
         var version=$scope.downloadDetail.version;
         var baseurl=$location.absUrl().split("app")[0];
         var typeexec;
         if($scope.downloadDetail.type=="dataqual"){
           typeexec="dqexec";
         }else{
           typeexec=$scope.downloadDetail.type+"exec";
         }
         $http.get(baseurl+'metadata/getNumRowsbyExec?action=view&execUuid='+uuid+'&execVersion='+version+'&type='+typeexec).then(function (res) {
           var mode=res.data.runMode;
           var url;
           if($scope.downloadDetail.type =="profile"){
             url=baseurl+"profile/download?action=view&profileExecUUID="+uuid+"&profileExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid)
           }
           else if($scope.downloadDetail.type =="dataqual"){
             url=baseurl+"dataqual/download?action=view&dataQualExecUUID="+uuid+"&dataQualExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid);
           }
           else if($scope.downloadDetail.type =="map"){
             url=baseurl+"map/download?action=view&mapExecUUID="+uuid+"&mapExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid)
           }
           else if($scope.downloadDetail.type =="recon"){
             url=baseurl+"recon/download?action=view&reconExecUUID="+uuid+"&reconExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid)
           }
           else if($scope.downloadDetail.type =="predict"){
             url=baseurl+"/model/predict/download?action=view&predictExecUUID="+uuid+"&predictExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid)
           }
           else if($scope.downloadDetail.type =="simulate"){
             url=baseurl+"/model/simulate/download?action=view&simulateExecUUID="+uuid+"&simulateExecVersion="+version+"&mode="+mode;
             $scope.downloaddata(url,uuid)
           }
           else if($scope.downloadDetail.type =="train"){
             url=baseurl+"model/getModelByTrainExec?action=view&uuid="+uuid+"&version="+version;
             $http({
               method: 'GET',
               url:url,
               
             }).success(function(data, status, headers) {
               if(data.customeFlag =="N"){
                 $scope.downloadTrainData(uuid);
                 return false;
               }
               else{
                 url=baseurl+"/model/train/download?action=view&trainExecUUID="+uuid+"&trainExecVersion="+version+"&mode="+mode;
                 $scope.downloaddata(url,uuid)
               }
             });
           
           }
                
        });
       };
     },
     template: `
       <div class="row" ng-show="type =='train'">
         <div class="col-md-12 col-sm-12 col-xs-12 col-lg-12">
          <!--<pre ng-bind="modelresult" style="min-height: 100px;white-space: pre-wrap"></pre>-->
           <json-formatter open="1" key="'Result'" json ='modelresult'></json-formatter>
         </div>        
       </div>
       <div class="row" ng-show="type !='train'">
         <div class="col-md-6 col-sm-6">
           <div class="dataTables_length" id="sample_1_length">
             <label>Show
               <select name="sample_1_length" aria-controls="sample_1" class="form-control input-sm input-xsmall input-inline" ng-model="pagination.pageSize" ng-options="r for r in pagination.paginationPageSizes" ng-change="onPerPageChange()">
               </select>
             </label>
           </div>
         </div>
           
         <div class="col-md-6 col-sm-6">
           <div style="float:right;">
             <label>Search:</label>
             <input type="search" class="form-control input-sm input-small input-inline" ng-change="filterSearch(searchtext)" ng-model="searchtext">
           </div>
         </div>
           
         <div class="col-md-12 col-sm-12">
           <div ui-grid="gridOptions"  ui-grid-resize-columns ui-grid-auto-resize ui-grid-exporter class="grid" ng-style="getGridStyle()">
             <div class="nodatawatermark_results" ng-show="!gridOptions.data.length">No data available</div>
               <div class="tooltipcustom" id="tabletoshow" style="position: fixed;display:none;z-index:9999;min-width:320px;min-height: 80px;opacity: 0.95
                 font-family: Roboto,Helvetica Neue,Helvetica,Arial,sans-serif;">      
                 <div style="margin-top: 5px;margin-right: 15px">
                   <div class="row" style="padding-left:6%">
                     <table ng-if="gridOptions.columnDefs">
                       <tr ng-repeat="m in gridOptions.columnDefs  |  limitTo :20">
                         <th ng-if="m.visible==true">{{m.displayName}}</th>
                         <td style="padding-left:6%" ng-if="m.visible==true">{{mouseHowerRowValue[m.name]}}</td>                       
                       </tr>
                       <tr ng-show="gridOptions.columnDefs.length >20" rowspan="2" >
                         <td colspan="2" style="text-align:right">......</td>                       
                       </tr>                     
                     </table>
                   </div>           
                 </div>
               </div>
             </div>
             <div class="row">
               <div class="col-md-6" style="margin-top:17px">
                 Showing {{pagination.to}} to {{pagination.from}} of {{pagination.totalItems}} records
               </div>
               <div class="col-md-6">
                 <ul uib-pagination items-per-page="pagination.pageSize" total-items="pagination.totalItems" ng-model="pagination.currentPage" ng-change="pageChanged()" style="float:right;overflow:hidden;z-index:1;" max-size="pagination.maxSize" class="pagination-md"  boundary-links="true" previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;"></ul>
               </div>
             </div>
           </div>
         </div> 
       `
     };
 });

DataPipelineModule.directive('renderGroupDirective',function ($rootScope,$compile,$location,$http,dagMetaDataService,$rootScope) {
   function setGrid(paper, gridSize, color) {
     // Set grid size on the JointJS paper object (joint.dia.Paper instance)
     paper.options.gridSize = gridSize;
     // Draw a grid into the HTML 5 canvas and convert it to a data URI image
     var canvas = $('<canvas/>', { width: gridSize, height: gridSize });
     canvas[0].width = gridSize;
     canvas[0].height = gridSize;
     var context = canvas[0].getContext('2d');
     context.beginPath();
     context.rect(1, 1, 1, 1);
     context.fillStyle = color || '#AAAAAA';
     context.fill();
     // Finally, set the grid background image of the paper container element.
     var gridBackgroundImage = canvas[0].toDataURL('image/png');
     paper.$el.css('background-image', 'url("' + gridBackgroundImage + '")');
   }
     
   return {
     template: `
       <div class="tooltipcustom" id="divtoshow" style="position: fixed;display:none;z-index:9999;min-width:320px;min-height: 80px;opacity: 0.8;
         font-family: Roboto,Helvetica Neue,Helvetica,Arial,sans-serif;">
         <div style="margin-top: 5px;margin-right: 15px">
           <div class="row">
             <div class="col-md-1">
               <div class="one" id="dagcolorID" style="margin-left:5px;margin-bottom: 10px"></div>
             </div>
             <div class="col-md-5" id="elementTypeText"></div>
           </div>

           <div class="row" style="margin-top: 5px">
             <span style="margin-left:20px;">Id</span><span id="task_Id" style="margin-left:60px"></span>
           </div>
           <div class="row" style="margin-top: 5px">
             <span style="margin-left:20px">Name</span><span id="task_Name" style="margin-left:36px"></span>
           </div>
           <div class="row" style="margin-top: 5px">
             <span id="task_Version_label" style="margin-left:20px">Version</span> <span id="task_Version" style="margin-left:23px"></span>
           </div>
           <div class="row" style="margin-top: 5px" ng-if="popoverData.startTime">
             <span style="margin-left:20px">Start Time</span> <span id="startTime" style="margin-left:4px">{{popoverData.startTime | date:'EEE, dd MMM yyyy HH:mm:ss'}}</span>
           </div>
           <div class="row" style="margin-top: 5px" ng-if="popoverData.endTime">
             <span style="margin-left:20px">End Time</span> <span id="endTime" style="margin-left:8px">{{popoverData.endTime |date:'EEE, dd MMM yyyy HH:mm:ss'}}</span>
           </div>
          <!--div class="row" style="margin-top: 5px" ng-if="popoverData.endTime">
          <span style="margin-left:20px">Total Time</span> <span id="endTime" style="margin-left:8px">{{popoverData.timeDiff}}</span>
        </div-->
           <!-- <button id="close" class="btn btn-xs btn-danger pull-right">Close</button> -->
         </div>
       </div>

       <div class="joint-graph-zoom-slider col-md-1 col-md-offset-11">
       <div class="pull-right" style="height:120px;width:25px;margin-right:25px">
         <a ng-click="changeSliderForward()"><i class="fa fa-search-plus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
         <rzslider rz-slider-model="zoomSize" rz-slider-options="{floor: 1, ceil: 20,minLimit:1,maxLimit:20,hidePointerLabels:true,hideLimitLabels:true,vertical: true}"></rzslider>
         <a ng-click="changeSliderBack()"> <i class="fa fa-search-minus" style="margin: 0 23px;z-index: 980;position: relative;font-size: 17px;margin-top: 5px;color: #999;"></i></a>
       </div>
       </div>
       <div id="group-graph-wrapper"></div>
       `,
       scope : {
       },
       link: function ($scope, element, attrs) {
      
         $scope.elementDefs = dagMetaDataService.elementDefs;
         $rootScope.allowReGroupExecution=false;
         var dx = $( document ).width();
         var dy = $( document ).height();
         var wrapper = $('#group-graph-wrapper');
         var graph = new joint.dia.Graph;
         var paper = new joint.dia.Paper({ el: wrapper,gridSize: 1, width: dx > 2000 ? dx : 2000, height: dy*2 > 1000 ? dy*2 : 1000,model: graph,
         interactive: {
           vertexAdd: false
         },
         // async:true,
         linkPinning : false,
         linkView: joint.dia.LinkView,
         multiLinks: false,
         snapLinks: true,
         markAvailable: true,
         interactive:false,
         // highlighting: {
         //   magnetAvailability: {
         //     name: 'stroke'
         //   }
         // },
         defaultLink : function(cellView, magnet) {
           // console.log('cellView',cellView);
           // console.log('magnet',magnet);
           // return;
           return new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
               attrs: { '.connection': { 'source-id':cellView.model.id  }}
           }));
         },
         validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
           if(!$scope.editMode){
             return false;
           }
           if(magnetT.attributes['port'].value == 'out'){
             return false
           }
           return dagValidationSvc.validate($scope.graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView);
           return true;
         },
         validateMagnet: function (cellView, magnet) {
           if(!$scope.editMode){
             return false;
           }
           if(magnet.attributes['port'].value == 'in'){
             return false
           }
           return true
         }
         });
         $scope.$watch('zoomSize',function (newSize,old) {
           try {
             paper.scale(newSize/10);
           } catch (e) {
           
           } finally {
           }
         });
        
         $scope.changeSliderForward=function() {
           $scope.zoomSize=$scope.zoomSize+1;
         }
         
         $scope.changeSliderBack=function() {
           $scope.zoomSize=$scope.zoomSize-1;
         }
       
         $scope.zoomSize = 7;
         setGrid(paper, 10, '#AAAAAA');
         var initialised = false;
         var startParams;
         var intervalId,statusCache;

         function latestStatus(statuses){
           var latest;
           angular.forEach(statuses,function (status) {
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
         
         function startStatusUpdate(params) {
           var Fn = function () {
             var url=$location.absUrl().split("app")[0];            
             var execType = dagMetaDataService.elementDefs[params.elementType].execType;
             $http.get(url+'metadata/getGroupExecStatus?action=view&type='+execType+'&uuid='+params.id+'&version='+params.version).then(
               function success(response){onSuccessGetStatusByDagExec(response.data)},
               function error(error) {
               stopStatusUpdate();
             });
             var onSuccessGetStatusByDagExec=function(response){
               console.log(response);
               if(!angular.equals(statusCache, response)){
                 updateGroupGraphStatus(response);
                 statusCache = response;
               }
             }
           }
           
           setTimeout(function () {
             Fn();
           }, 1000);
             intervalId = setInterval(Fn, 5000);
         };

         $scope.$on('$destroy',function(){
           stopStatusUpdate();
         });
         
         function stopStatusUpdate() {
           statusCache = undefined;
           if(intervalId)
             clearInterval(intervalId);
         }
         
         function updateGroupGraphStatus(data){
           angular.forEach(data,function (task) {
             var statusTask = task.statusList.length == 0 ? 'InProgress' : latestStatus(task.statusList).stage;
             var taskid = task.metaRef.ref.uuid;
             if(taskid == $scope.GroupExecUuid){
               if(['Failed','Killed'].indexOf(statusTask) !=-1){
                 $rootScope.allowReGroupExecution = true;
               }
               else{
                 $rootScope.allowReGroupExecution =false;
               }
             }
             
             if(taskid == $scope.GroupExecUuid && ['Completed','Failed','Killed'].indexOf(statusTask)>-1){
               stopStatusUpdate();
             }
               
             $(".status[element-id=" + taskid + "] .statusImg").attr("xlink:href","assets/layouts/layout/img/new_status/"+statusTask+".svg");
             $(".status[element-id=" + taskid + "] .statusTitle").text(statusTask);
             angular.forEach(task.status,function (status) {
               $(".status[element-id=" + taskid + "]").attr(status.stage,status.createdOn);
             });
           });
         }

         $( "#group-graph-wrapper" ).on("mouseover",".joint-element .body",function(e){
           $('.joint-element').mouseout(function(e){
             var divid = 'divtoshow';
             $("#"+divid).hide();
             $('.connection').removeClass('active');
           });
           var jointElement = $(this).closest(".joint-element");
           var s = jointElement.attr("model-id");
           $('.connection[source-id='+s+']').addClass('active');
           var cell = graph.getCell(s);
           var elementModel = cell.attributes['model-data'];
           try {
             var elementType = cell.attributes.elementType;
             if(elementType.slice(-4) == 'Exec'){
               elementType = elementType.slice(0,-4);
             }
           }
           catch(e){
             if(s.substr(0,3)=='dag'){
               var cell = $(this);
               var elementModel = cell.attr("model-data") ? JSON.parse(cell.attr("model-data")) : undefined;
                 var elementType = 'dag';
             }
             else if(cell.attributes.elementType=='stage'){
               var elementType = 'stage';
               var elementModel = {name : cell.attributes['model-data'].name, type : elementType, uuid:cell.attributes['model-data'].stageId};
             }else{
               var elementType = undefined;
             }
           }
           var allowedHover = angular.copy(dagMetaDataService.validTaskTypes);
           if(!elementModel || allowedHover.indexOf(elementType) < 0){
             return false;
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
             dagtypetext = $scope.elementDefs[elementType].caption;
             color = $scope.elementDefs[elementType].color;
             $("#dagcolorID").css("background-color", color);
           }
           catch(e){
             dagtypetext = elementType;
             $("#dagcolorID").css("background-color", "blue");
           }

           if(!elementModel || Object.keys(elementModel).length == 0){
             var txt1 ="", txt2 = "", txt3 = "";
           }
           else {
             var txt1 = elementModel.uuid || '';
             var txt2 = elementModel.name || '';
             var txt3 = elementModel.version || '';
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
           var status = jointElement.find('.status');
           var startTime = status.attr("inprogress");
           var endTime = status.attr("completed");
           $scope.popoverData = {};
           $scope.popoverData.startTime = startTime || '-';
           $scope.popoverData.endTime = endTime || '-';
           var intdate1= parseInt($scope.popoverData.startTime)
           var intdate2= parseInt($scope.popoverData.endTime)
           var date1 = new Date(intdate1)
           var date2 = new Date(intdate2)
           $scope.popoverData.timeDiff = moment.utc(moment(date2).diff(moment(date1))).format("HH:mm:ss")
           $scope.$apply();//this is required
         });//End mouseover
         
         $scope.$on('generateGroupGraph',function (e,params) {
           $rootScope.showGroupDowne=true;
           $scope.allowReExecution = false;
           $scope.GroupExecUuid = params.id;
           $scope.execMode = true;
           console.log('params',JSON.stringify(params));
           element.hide();
           startParams = params;
           graph.clear();
           $('#groupErrorMsg').hide();
           $('#grouploader').show();
           var url=$location.absUrl().split("app")[0];
           var actualType = params.type.slice(0,-5);
           $http({
             method: 'GET',
             url:url+params.url+actualType+"GroupExecUuid="+params.id+"&"+actualType+"GroupExecVersion="+params.version+'&action=view',
           }).
           then(function (response,status,headers) {
             stopStatusUpdate();
             element.show();
             console.log(response);
             $('#grouploader').hide();
             $scope.$emit('daggroupExecChanged',startParams.name || startParams.id);
             drawChildGraph(response.data);
             startStatusUpdate(params);
           },function onError(err) {
             $('#grouploader').hide();
             $('#groupErrorMsg').show();
             $('#groupErrorMsg').html('Some Error Occured');
           })
         });//End generateGroupGraph
         
         function semicirclePos(data) {
           var length = data.length;
           var halfLength = Math.round(length/2);
           var posArray = [];
           var posX = 200,posY = 100;
           posArray[0] = {x:posX,y:posY};
           for (var i = 1; i < length; i++) {
             posY = posY + 100;
             if(i <=   halfLength){
               posX = posX + 100;
               posArray[i] = {x:posX,y:posY};
             }
               
             else if(i > halfLength){
               posX = posX - 100;
               if(i == length - 1){
                 posArray[i] = {x:200,y:posY};
               }
               else {
                 posArray[i] = {x:posX,y:posY};
               }
             }
           }
           return posArray;
         }//End semicirclePos
          
         function circlePos(data) {
           var length = data.length;
           var angle,
           radius = length > 7 ? 30*length : 250,
           width = (radius * 2) + 150,
           height = (radius * 2) + 150,
           posArray = [];
           for (i=0; i<length; i++) {
             angle = (i / (length/2)) * Math.PI; // Calculate the angle at which the element will be placed.
             // For a semicircle, we would use (i / numNodes) * Math.PI.
             x = (radius * Math.cos(angle)) + (width/2); // Calculate the x position of the element.
             y = (radius * Math.sin(angle)) + (width/2); // Calculate the y position of the element.
             posArray[i] = {'x': x, 'y': y};
           }
           return posArray;
         }//End circlePos
          
       function drawChildGraph(data) {
         if(!data){
           return;
         }

         setTimeout(function () {
           $scope.$broadcast('rzSliderForceRender');
         }, 100);
         //  var posArray = semicirclePos(data);
         var posArray = circlePos(data);
         var cells = [];
         var links = [];
         cells.push( new joint.shapes.devs.Model(
           angular.merge({},dagMetaDataService.getCustomElement(startParams.elementType),{
             id: startParams.id,
             elementType : startParams.elementType,
             "model-data": {dependsOn:{ref : startParams.ref}, uuid  : startParams.id,name:startParams.name,version:startParams.version},
             //position: { x: 140 , y:100 + Math.round(data.length/2) *100 }, //semi circle
             position: { x: (data.length > 7 ? 30*data.length : 250) + 75, y: (data.length > 7 ? 30*data.length : 250) + 75 }, //circle
             attrs: {
               '.body': {
                 'element-id' : startParams.id,
                 "model-data": JSON.stringify({dependsOn:{ref : startParams.ref}}),
               },
               '.status': {
                 'element-id' : startParams.id,
               },
               text: { text: startParams.name || startParams.id}
             }
           })
         ));//End Cell
            
         if(data.length > 0){
           var taskXPos = 500;
           var taskYPos = 100;
           angular.forEach(data,function (task,taskKey) {
             var ref= task.dependsOn.ref;
             var type = ref.type;
             taskXPos = posArray[taskKey].x;
             taskYPos = posArray[taskKey].y;
             task.taskId = task.uuid.length > 3 ? task.uuid : startParams.id+'_'+"task_"+task.uuid
             cells.push( new joint.shapes.devs.Model(
               angular.merge({},dagMetaDataService.getCustomElement(type),{
                 id: task.taskId,
                 elementType : type,
                 "model-data": task,
                 position: { x: task.xPos || taskXPos, y: task.yPos || taskYPos },
                 attrs: {
                   '.body': {
                     'element-id' : task.taskId,
                     "model-data": JSON.stringify(task),
                   },
                   
                   '.status': {
                     'element-id' : task.taskId,
                   },
                   '.status image' : {
                     "xlink:href":"assets/layouts/layout/img/new_status/"+task.statusList[task.statusList.length -1].stage+".svg"
                   },
                   text: { text: task.name}
                 }
               })
             ));
                 
             var s = startParams.id;
             links.push(
               new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
                 source: { id: s}, target: { id: task.taskId },
                 attrs: {'.connection': {'source-id':s  }}
               }))
             )
           });
         }//End If
       
         graph.addCells(cells);
         graph.addCells(links);
         if($scope.execMode){
           $('#showgrouppaper svg').addClass('exec-mode');
         }
            
         d3.selectAll('#showgrouppaper .joint-element .body')
           .on('contextmenu', function(){
             d3.event.preventDefault();
             d3.event.stopPropagation();
             var vm = this;
             var id = vm.getAttribute("element-id");
             var cell = graph.getCell(id);
             if(!cell){
               return false
             }
             console.log(cell);
             var parentStage = cell.attributes.parentStage;
             var taskId = cell.attributes.id;
             var modelData = cell.attributes['model-data'];
             var ref = id == startParams.id ? modelData.dependsOn.ref : {uuid: modelData.uuid,name : modelData.name ,type : startParams.type.slice(0,-5)+'Exec', version: modelData.version};
             var type = ref.type;
             if(type.slice(-4) == 'Exec'){
               if(type.slice(-9) == 'groupExec'){
                 var isGroupExec = true;
               }
               else {
                 var isExec = true;
               }
               
               type = type.slice(0,-4);
               if(type == 'dataQual')
                 type = 'dq';
                 else if (type == 'dataQualGroup')
                   type = 'dqgroup';
                 }
                 var svg = document.querySelector('#showgrouppaper svg');
                 var pt = svg.createSVGPoint();
                 pt.x = d3.event.clientX; pt.y = d3.event.clientY;
                 var localPoint = pt.matrixTransform(svg.getScreenCTM().inverse());
                 var state;
                   
                 if(isExec || isGroupExec){
                   var iconMenuItems = [{title:'Show Details', type : 'element'}];
                   if($scope.execMode || true){
                     var status = $(".status[element-id=" + taskId + "] .statusTitle")[0].innerHTML;
                     if(status && (status=='Completed') && isGroupExec!=true){
                       iconMenuItems.push({title:'Show Results', type : 'results'});
                     }
                     else if(status && (status=='NotStarted' || status=='Resume')){
                       iconMenuItems.push({title:'On Hold', type : 'onhold'});
                     }
                     else if(status && status=='InProgress'){
                       iconMenuItems.push({title:'Kill', type : 'killexecution'});
                     }
                       
                     else if(status && status=='OnHold'){
                       iconMenuItems.push({title:'Resume', type : 'resume'});
                     }
                   }
                   iconMenu.resetItems(iconMenuItems);
                   var apis = {
                     dq : {name:'dataqual', label: 'DataQual'},
                     dqgroup : {name:'dataQualGroup', label: 'DataQualGroup',url :'dataqual/getdqExecBydqGroupExec?'},
                     profile : {name:'profile', label: 'Profile'},
                     profilegroup : {name:'profilegroup', label: 'ProfileGroup', url : 'profile/getProfileExecByProfileGroupExec?'},
                     load : {name:'load', label: 'Load'},
                     map : {name:'map', label: 'Map'},
                     model : {name:'model', label: 'Model'},
                     rule : {name:'rule', label: 'Rule'},
                     rulegroup : {name:'rulegroup', label: 'RuleGroup',url:'rule/getRuleExecByRGExec?'},
                     recon : {name:'recon', label: 'Recon'},
                     recongroup : {name:'recongroup', label: 'ReconGroup',url:'recon/getReconExecByRGExec?'},
                   }
                 
                   var resultparams = {id:ref.uuid,name:modelData.name,elementType:type,version:ref.version,type: apis[type].name,typeLabel:apis[type].label,url:apis[type].url,parentStage:parentStage,taskId:taskId};
                   if(id != startParams.id){
                     var url=$location.absUrl().split("app")[0];
                     $http.get(url+'common/getLatestByUuid?action=view&uuid='+modelData.dependsOn.ref.uuid+'&type='+modelData.dependsOn.ref.type).then(function (res) {
                       state = {state : dagMetaDataService.elementDefs[modelData.dependsOn.ref.type].state, params : {id :modelData.dependsOn.ref.uuid,name:modelData.dependsOn.ref.name,version :res.data.version,type:modelData.dependsOn.ref.type,mode:true, returnBack: true}};
                       iconMenu(localPoint.x, localPoint.y, JSON.stringify(state),JSON.stringify(resultparams));
                     });
                   }
                     
                   else {
                     var url=$location.absUrl().split("app")[0];
                     $http.get(url+'metadata/getMetaIdByExecId?action=view&execUuid='+ref.uuid+'&execVersion='+ref.version+'&type='+ref.type).then(function (res) {
                       state = {state : dagMetaDataService.elementDefs[res.data.type].state, params : {id :res.data.uuid,version: res.data.version,name:res.data.name,type:res.data.type, mode:true, returnBack: true}};
                       iconMenu(localPoint.x, localPoint.y, JSON.stringify(state),JSON.stringify(resultparams));
                     });
                   }
                 }
                   
                 else {
                   var url=$location.absUrl().split("app")[0];
                   $http.get(url+'common/getLatestByUuid?action=view&uuid='+ref.uuid+'&type='+ref.type).then(function (res) {
                     state = {state : dagMetaDataService.elementDefs[type].state, params : {id :ref.uuid,version:res.data.version,name:ref.name,type:ref.type, mode:true, returnBack: true}};
                     iconMenu(localPoint.x, localPoint.y, JSON.stringify(state));
                   });
                 }
               });//End contextmenu
             }//End drawChildGraph

           function iconContextMenu() {
             var height,
             width,
             margin = 0.1, // fraction of width
             items = [],
             rescale = false,
             style = {
               'rect': {
                 'mouseout': {
                   'fill': 'rgb(0,0,0)',
                   'opacity':'0.8',
                   'stroke': 'white',
                     'stroke-width': '1px'
                 },
                 'mouseover': {
                   'fill': '#32c5d2'
                 }
               },
               'text': {
                 'fill': 'white',
                 'font-size': '13',
                 'font-family': 'Open Sans'
               }
             };

             function menu(x, y, url,resultParams) {
               d3.select('.context-menu').remove();
               scaleItems();
               d3.select('#showgrouppaper svg')
               .append('g').attr('class', 'context-menu')
               .selectAll('tmp')
               .data(items).enter()
               .append('g').attr('class', 'menu-entry')
               .style({'cursor': 'pointer'})
               .on('mouseover', function(){
               d3.select(this).select('rect').style(style.rect.mouseover) })
               .on('mouseout', function(){
                 d3.select(this).select('rect').style(style.rect.mouseout) });
                 d3.selectAll('.menu-entry')
                 .append('rect')
                 .attr('x', x)
                 .attr('y', function(d, i){ return y + (i * height); })
                 .attr('onclick', function(d){
                 if(d.type == 'results'){
                   return 'showResult('+resultParams+')'
                 }
                 else if(d.type == 'onhold'){
                   return 'setSubTaskStatus('+resultParams+',"OnHold")'
                 }
                 else if(d.type == 'resume'){
                   return 'setSubTaskStatus('+resultParams+',"Resume")'
                 }
                 else if(d.type == 'killexecution'){
                   return 'setSubTaskStatus('+resultParams+',"Killed")'
                 }
                 else
                   return "navigateTo('"+url+"');"
                 })
                 .attr('width', width+50)
                 .attr('height', height)
                 .style(style.rect.mouseout);
                 d3.selectAll('.menu-entry')
                   .append('text')
                   .text(function(d){ return d.title; })
                   .attr('onclick', function(d){
                   if(d.type == 'results'){
                     return 'showResult('+resultParams+')'
                   }
                   else if(d.type == 'onhold'){
                     return 'setSubTaskStatus('+resultParams+',"OnHold")'
                   }
                   else if(d.type == 'resume'){
                     return 'setSubTaskStatus('+resultParams+',"Resume")'
                   }
                   else if(d.type == 'killexecution'){
                     return 'setSubTaskStatus('+resultParams+',"Killed")'
                   }
                   else
                     return "navigateTo('"+url+"');"
                   })
                   .attr('x', x+25)
                   .attr('y', function(d, i){ return y + (i * height); })
                   .attr('dy', height - 10 - margin / 2)
                   .attr('dx', margin)
                   .style(style.text);

                   // Other interactions
                   d3.select('body')
                     .on('click', function() {
                       d3.select('.context-menu').remove();
                     });
                 }//End menu

                 menu.items = function(argumentItems) {
                   if (!argumentItems.length) return items;
                     for (i in argumentItems) items.push(argumentItems[i]);
                       rescale = true;
                     return menu;
                   }
                 menu.resetItems = function(argumentItems) {
                   if (!argumentItems.length) return items;
                     items = [];
                   for (i in argumentItems) items.push(argumentItems[i]);
                     return menu;
                 }

                 // Automatically set width, height, and margin;
                 function scaleItems() {
                   if (rescale) {
                     d3.select('#showgrouppaper svg').selectAll('tmp')
                       .data(items).enter()
                       .append('text')
                       .text(function(d){ return d.title; })
                       .style(style.text)
                       .attr('x', -1000)
                       .attr('y', -1000)
                       .attr('class', 'tmp');
                       
                     var z = d3.selectAll('.tmp')[0]
                     .map(function(x){ return x.getBBox(); });
                       width = d3.max(z.map(function(x){ return x.width; }));
                       margin = margin * width;
                       width =  width + 2 * margin;
                       height = d3.max(z.map(function(x){ return x.height+ 15 + margin / 2; }));

                       // cleanup
                     d3.selectAll('.tmp').remove();
                       rescale = false;
                   }
                 }//end  scaleItems 
               return menu;
             }
             var iconMenuItems = [{title:'Show Details', type : 'element'},{title:'Show Results', type : 'results'},{title:'On Hold', type : 'onhold'},{title:'Resume', type : 'resume'},{title:'Kill', type : 'killexecution'}];
             var iconMenu = iconContextMenu().items(iconMenuItems);
             window.setSubTaskStatus = function (row,status) {
               $scope.setStatus(row,status);
             }
             $scope.setStatus = function (row,status) {
               var api = false;
               var execType = dagMetaDataService.elementDefs[row.elementType.toLowerCase()].execType;
               switch (row.elementType) {
                 case 'dq':
                   api = 'dataqual';
                   break;
                 case 'dqgroup':
                   api = 'dataqual';
                   break;
                 case 'profile':
                   api = 'profile';
                   break;
                 case 'profilegroup':
                 api = 'profile';
                 break;
                 case 'rule':
                   api = 'rule';
                   break;
                 case 'rulegroup':
                   api = 'rule';
                   break;
                 case 'dagexec':
                   api = 'dag';
                   break;
                 case 'recon':
                   api = 'recon';
                   break;
                 case 'recongroup':
                   api = 'recon';
                   break;
               }
               if(!api){
                 return
               }
               var url=$location.absUrl().split("app")[0];
               $http.put(url+''+api+'/setStatus?uuid='+row.id+'&version='+row.version+'&type='+execType+'&status='+status).then(function (response) {
                   console.log(response);
               });
             }
           } //End Link
         };
  });
DataPipelineModule.directive('jointGraphDirective',function ($state,$rootScope,graphService,dagValidationSvc,$location,$http,dagMetaDataService,CommonService,MetadataDagSerivce,$timeout,$filter) {
  return {
    restrict: 'AE',
    scope : {
     execMode : '=',
     editMode : '=',
     addMode:'=',
     graph : '=',
     isTemplate:'=',
    },
    link: function ($scope, element, attrs) {
     var taskDetail=null;
     $rootScope.showGrid=false;
     $rootScope.showGroupDowne=false;
     $scope.elementDefs = dagMetaDataService.elementDefs;
     $scope.paramTypes=["paramlist","paramset"];
     $scope.changeSliderForward=function() {
       $scope.zoomSize=$scope.zoomSize+1;
     }
     
     $scope.changeSliderBack=function() {
       $scope.zoomSize=$scope.zoomSize-1;
     }
     
     window.navigateTo = function(url){
       var state = JSON.parse(url);
       $rootScope.previousState = {name : $state.current.name, params : $state.params};
       $rootScope.previousState.params.tab = '1';
       var ispresent=false;
       if(ispresent !=true){
         var stateTab={};
         stateTab.route=state.state;
         stateTab.param=state.params;
         stateTab.active=false;
         $rootScope.$broadcast('onAddTab',stateTab);
       }
       $state.go(state.state,state.params);
     }
     
     $scope.closeResultDiv = function () {
       $scope.showResults = false;
     }
     
     window.showResult = function(params){
       $scope.lastParams = params;
       App.scrollTop();
       if(params.type.slice(-5).toLowerCase() == 'group'){
         $scope.showGroupGraph = true;
         $rootScope.showGroupDowne=true;
         $scope.lastGroupParams = params;
         $scope.$broadcast('generateGroupGraph',params);
       }
       else {
         $scope.lastResultsParams = params;
         $scope.showResults = true;
         setTimeout(function () {
           $scope.$broadcast('generateResults',params);
           $scope.$emit('resultExecChanged',params.name)
         }, 100);
       }
     }
     
     window.setStatus = function(params,status){
       $scope.lastParams = params;
       console.log(params);
       var notify = {
         type: 'success',
         title: 'Success',
         timeout: 3000 //time in ms
       };
       notify.type='success',
       notify.title= 'Success'
       if(params.type=="dag"){
         $scope.msgtype="Pipeline"
       }
       else if(params.type=="stage"){
         $scope.msgtype="Stage"
       }
       else{
         $scope.msgtype="Task"
       }
       if(status == 'Killed'){
         $scope.executionmsg =$scope.msgtype+" killed successfully"
         notify.content=$scope.executionmsg
       }
       if(status =='OnHold'){
         $scope.executionmsg =$scope.msgtype+" paused successfully"
         notify.content=$scope.executionmsg
       }
       if(status =='Resume'){
         $scope.executionmsg =$scope.msgtype+" resumed successfully"
         notify.content=$scope.executionmsg
       }
       $rootScope.$emit('notify', notify);
       var url=$location.absUrl().split("app")[0];
       if(params.type == 'dag'){
         $http.put(url+'dag/setStatus?uuid='+$scope.uuid+'&version='+$scope.version+'&status='+status).then(function (response) {
           console.log(response);
           if(status == 'Killed')
             $scope.$parent.allowReExecution = true;
           });
         }
         else if(params.type == 'stage'){
           var stageId = params.id;
           $http.put(url+'dag/setStatus?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId+'&status='+status).then(function (response) {
             console.log(response);
           });
         }
         else {
           var stageId = params.parentStage;
           if(params.taskId.length > 5 && params.taskId.indexOf('task_')>-1){
             var temp = params.taskId.split('task_');
             var taskId = temp[temp.length-1];
           }
           else {
             var taskId = params.taskId;
           }
           taskDetail={}; 
           taskDetail.taskOnOperation=true;
           taskDetail.taskId=taskId;
           var type=$(".body[element-id=" + taskId + "]").attr("element-type");
           $(".body[element-id=" + taskId + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
           setTimeout(function(){ 
              $(".body[element-id=" + taskId + "]").attr("xlink:href","assets/layouts/layout/img/"+type+".svg");  
              taskDetail=null;
           }, 3000);
           $http.put(url+'dag/setStatus?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId+'&taskId='+taskId+'&status='+status).then(function (response) {
             console.log(response);
           });
         }
       }
         
       window.holdExecution = function(params){
         $scope.lastParams = params;
         console.log(params);
         var url=$location.absUrl().split("app")[0];
         if(params.type == 'dag'){
           $http.put(url+'dag/setDAGOnHold?uuid='+$scope.uuid+'&version='+$scope.version).then(function (response) {
             console.log(response);
           });
         }
         else if(params.type == 'stage'){
           var stageId = params.id;
           $http.put(url+'dag/setStageOnHold?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId).then(function (response) {
             console.log(response);
           });
         }
         else {
           var stageId = params.parentStage;
           if(params.taskId.length > 5 && params.taskId.indexOf('task_')>-1){
             var temp = params.taskId.split('task_');
             var taskId = temp[temp.length-1];
           }
           else {
             var taskId = params.taskId;
           }
           $http.put(url+'dag/setTaskOnHold?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId+'&taskId='+taskId).then(function (response) {
             console.log(response);
           });
         }
       }
         
       window.resumeExecution = function(params){
         $scope.lastParams = params;
         console.log(params);
         var url=$location.absUrl().split("app")[0];
         if(params.type == 'dag'){
           $http.put(url+'dag/setDAGResume?uuid='+$scope.uuid+'&version='+$scope.version).then(function (response) {
             console.log(response);
           });
         }
         else if(params.type == 'stage'){
           var stageId = params.id;
           $http.put(url+'dag/setStageResume?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId).then(function (response) {
             console.log(response);
           });
         }
         else {
           var stageId = params.parentStage;
           if(params.taskId.length > 5 && params.taskId.indexOf('task_')>-1){
             var temp = params.taskId.split('task_');
             var taskId = temp[temp.length-1];
           }
           else {
             var taskId = params.taskId;
           }

           $http.put(url+'dag/setTaskResume?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId+'&taskId='+taskId).then(function (response) {
             console.log(response);
           });
         }
       }
         
       window.killExecution = function(params){
         $scope.lastParams = params;
         console.log(params);
         var url=$location.absUrl().split("app")[0];
         if(params.parentStage.length > 5 && params.parentStage.indexOf('stage_')>-1){
           var temp = params.parentStage.split('stage_');
           var stageId = temp[temp.length-1];
         }
         else {
           var stageId = params.parentStage;
         }

         if(params.taskId.length > 5 && params.taskId.indexOf('task_')>-1){
           var temp = params.taskId.split('task_');
           var taskId = temp[temp.length-1];
         }
         else {
           var taskId = params.taskId;
         }
           
         $http.put(url+'dag/killTask?uuid='+$scope.uuid+'&version='+$scope.version+'&stageId='+stageId+'&taskId='+taskId).then(function (response) {
           console.log(response);
         });
       }
         
       window.refreshSubGroupGraph = function () {
         if($scope.showGroupGraph && !$scope.showResults){
           window.showResult($scope.lastGroupParams);
         }
         else if($scope.showResults){
           window.showResult($scope.lastResultsParams);
         }
       }
       window.getAllCell=function(){
         return $scope.graph.getCells();
       }
           
       function setGrid(paper, gridSize, color) {
         paper.options.gridSize = gridSize;// Set grid size on the JointJS paper object (joint.dia.Paper instance)
         var canvas = $('<canvas/>', { width: gridSize, height: gridSize });// Draw a grid into the HTML 5 canvas and convert it to a data URI image
         canvas[0].width = gridSize;
         canvas[0].height = gridSize;
         var context = canvas[0].getContext('2d');
         context.beginPath();
         context.rect(1, 1, 1, 1);
         context.fillStyle = color || '#AAAAAA';
         context.fill();// Finally, set the grid background image of the paper container element.
         var gridBackgroundImage = canvas[0].toDataURL('image/png');
         paper.$el.css('background-image', 'url("' + gridBackgroundImage + '")');
       }

       $scope.$on('createGraph',function (e,data) {
         if(!data){
           return;
         }
         $scope.dagData=data;
        // console.log('graph data', data);
         $scope.uuid = data.uuid;
         $scope.version = data.version;
         var paperEl = $('#paper');
        // console.log(paperEl);
         if(paperEl.length == 0){
           setTimeout(function () {
             paperEl = $('#paper');
             if(paperEl.length == 0){
               setTimeout(function () {
                 paperEl = $('#paper');
                 createGraph(data);
               }, 500);
             }
             else{
               createGraph(data);
             }
           }, 300);
         }
         else {
           createGraph(data);
         }
       });
         
       $scope.$on('updateGraphStatus',function (e,data) {
         updateGraphStatus(data);
       });
         
       $scope.$on('removeGraph',function (e,data) {
         if($scope.graph){
           $scope.graphReady = false;
           $scope.graph.clear();
         }
       });
         
       function latestStatus(statuses){
         var latest;
         angular.forEach(statuses,function (status) {
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
         
       function updateGraphStatus(data){
         var statusDag = data.status.length == 0 ? 'InProgress' : latestStatus(data.status).stage;
         $scope.$parent.allowReExecution = false;
         if(['Killed','Failed'].indexOf(statusDag) > -1)
           $scope.$parent.allowReExecution = true;
         else
           $scope.$parent.allowReExecution = false;
         var dagid = "dag_0";
         $(".status[element-id=" + dagid + "] .statusImg").attr("xlink:href","assets/layouts/layout/img/new_status/"+statusDag+".svg");
         $(".status[element-id=" + dagid + "] .statusTitle").text(statusDag);
         angular.forEach(data.status,function (status) {
           $(".status[element-id=" + dagid + "]").attr(status.stage,status.createdOn);
         });
         angular.forEach(data.stages,function (stage) {
           var statusStage = stage.status.length == 0 ? 'InProgress' : latestStatus(stage.status).stage;
           var stageid = stage.stageId.length > 3 ? stage.stageId : 'stage_'+stage.stageId;
           $(".status[element-id=" + stageid + "] .statusImg").attr("xlink:href","assets/layouts/layout/img/new_status/"+statusStage+".svg");
           $(".status[element-id=" + stageid + "] .statusTitle").text(statusStage);
           angular.forEach(stage.status,function (status) {
             $(".status[element-id=" + stageid + "]").attr(status.stage,status.createdOn);
           });
           angular.forEach(stage.tasks,function (task) {
             var statusTask = task.status.length == 0 ? 'InProgress' : latestStatus(task.status).stage;
             var taskid = task.taskId.length > 3 ? task.taskId : stageid +'_' +'task_'+task.taskId;
             $(".status[element-id=" + taskid + "] .statusImg").attr("xlink:href","assets/layouts/layout/img/new_status/"+statusTask+".svg");
             $(".status[element-id=" + taskid + "] .statusTitle").text(statusTask);
             angular.forEach(task.status,function (status) {
               $(".status[element-id=" + taskid + "]").attr(status.stage,status.createdOn);
             });
           });
         });
       }

       function rightClick(){
         if(!$scope.editMode || $scope.isTemplate){
           return false; 
         }
         
         if($scope.editMode && $scope.isTemplate){
           return false; 
         }
         var menuItems = [];
         angular.forEach(dagMetaDataService.elementDefs,function (element,type) {
           if(element.allowInMenu){
             var menuItemsObj={}
             var chidernItems=[]
             //console.log(element.name)
             menuItemsObj.image=element.iconPath;
             menuItemsObj.title =element.parentIconCaption;
             menuItemsObj.id =element.name + '-add';
             menuItemsObj.type =element.name;
             menuItemsObj.menutype ="parent";
             menuItemsObj.onMenuClick=onMenuClick;
            //menuItemsObj.onMouseOver=onMouseClick;
             if(element.childMenu.length >0){
               for(var i=0;i<element.childMenu.length;i++){
                 var item=element.childMenu[i]
                 if(dagMetaDataService.elementDefs[item].allowInChildMenu ==true){
                   var childitem={};
                   childitem.title=dagMetaDataService.elementDefs[item].childIconCaption;
                   childitem.id =dagMetaDataService.elementDefs[item].name + '-add';
                   childitem.type =dagMetaDataService.elementDefs[item].name;
                   childitem.menutype ="child";
                   childitem.image=dagMetaDataService.elementDefs[item].iconPath;
                   childitem.onChildMenuClick=onChildMenuClick;
                   //childitem.onMouseOver=onMouseClick;
                   chidernItems[i]=childitem;
                 }
               }
             }
             menuItemsObj.chidernItems=chidernItems;
             menuItems.push(menuItemsObj)
           }
         });
         //console.log(menuItems)
         d3.selectAll('#paper').on('contextmenu', d3.contextMenu(menuItems));
       }
       
       function onMenuClick(elm, d, i){
         var item=dagMetaDataService.elementDefs[elm.type]
         if(item.childMenu.length ==0)
           addelement(event,elm)
       }

       function onChildMenuClick(elm, d, i){
         if(elm.menutype=="child")
           addelement(event,elm)
       }

       function createGraph(data) {
         var dx = $( document ).width();
         var dy = $( document ).height();
         if(!data && $scope.dagdata){
           data = $scope.dagdata;
         }
         if(!data){
           console.error('no data');
           return;
         }
         var paperEl = $('#paper');
         $scope.graph = new joint.dia.Graph;
         $scope.paper = new joint.dia.Paper({ el: paperEl,gridSize: 1, width: dx > 2000 ? dx : 2000, height: dy*2 > 1000 ? dy*2 : 1000,model: $scope.graph,
         interactive: {
           vertexAdd: false
         },
         // async:true,
         linkPinning : false,
         linkView: joint.dia.LinkView,
         multiLinks: false,
         snapLinks: true,
         markAvailable: true,
         // interactive:$scope.editMode,
           // highlighting: {
             //   magnetAvailability: {
             //     name: 'stroke'
             //   }
         // },
         defaultLink : function(cellView, magnet) {
           console.log(cellView)
           return new joint.dia.Link(angular.merge({},dagMetaDataService.getDefaultLink(),{
             attrs: { '.connection': { 'source-id':cellView.model.id  }}
           }));
         },
         
         validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
          
           if(!$scope.editMode || $scope.isTemplate){
             return false;
           }
           if(magnetT.attributes['port'].value == 'out'){
             return false
           }
           return dagValidationSvc.validate($scope.graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView);
           return true;
         },
         validateMagnet: function (cellView, magnet) {
           if(!$scope.editMode){
             return false;
           }
           if(magnet.attributes['port'].value == 'in'){
             return false
           }
           return true
         }
       });
       $scope.paper.scale(0.7);
       setGrid($scope.paper, 10, '#AAAAAA');
       var convertedData = graphService.convertDagToGraph(data,$scope.isTemplate,$scope.addMode);
       var cells = convertedData.cells;
       var links = convertedData.links;
       $scope.graph.addCells(cells);
       $scope.graph.addCells(links);
       $scope.zoomSize = 7;
       $scope.$broadcast('rzSliderForceRender');
       if(!$scope.graphReady){
         setTimeout(function () {
           $scope.$broadcast('registerJqueryEvents');
         }, 100);
             //to resolve the Jquery conflicts with JointJS
       }
           
       $scope.graphReady = true;

       rightClick();
       // d3.selectAll('#paper')
       //   .on('contextmenu', function(){
       //     if(!$scope.editMode || $scope.isTemplate){
       //       return false; 
       //     }
           
       //     if($scope.editMode && $scope.isTemplate){
       //       return false; 
       //     }
       //     //d3.event.preventDefault();
       //    // d3.contextMenu(chek)
       //    // menu(d3.mouse(this)[0], d3.mouse(this)[1]);
       //   });

       if($scope.execMode){
         $('#paper svg').addClass('exec-mode');
       }
             
       if(!$scope.editMode || $scope.isTemplate){
         $('#paper svg').addClass('view-mode');
         d3.selectAll('.joint-element .body')
         .on('contextmenu', function(){
          
           d3.event.preventDefault();
           d3.event.stopPropagation();
           var vm = this;
           var id = vm.getAttribute("element-id");
           var cell = $scope.graph.getCell(id);
           if(!cell){
             return false
           }
           var type = cell.attributes.elementType;
           if(type == 'stage' || type == 'dag'){
             var svg = document.querySelector('svg');
             var pt = svg.createSVGPoint();
             pt.x = d3.event.clientX; pt.y = d3.event.clientY;
             var localPoint = pt.matrixTransform(svg.getScreenCTM().inverse());
             var iconMenuItems = [];
             if($scope.execMode){
               var status = $(".status[element-id=" + id + "] .statusTitle")[0].innerHTML;
               if(status && ( status=='Resume')){
                 iconMenuItems.push({title:'On Hold', type : 'onhold'});
               }
               else if(status && status=='InProgress'){
                 iconMenuItems.push({title:'Kill', type : 'killexecution'});
               }
               else if(status && status=='OnHold'){
                 iconMenuItems.push({title:'Resume', type : 'resume'});
               }
             }
             
             if(iconMenuItems.length == 0){
               return;
             }
             iconMenu.resetItems(iconMenuItems);
             var apis = {
               dag : {name:'dag', label: 'dag'},
               stage : {name:'stage', label: 'stage'},
             }
             var resultparams = {id:id,name:type,elementType:type,type: apis[type].name,typeLabel:apis[type].label};
             iconMenu(localPoint.x, localPoint.y, JSON.stringify(state),JSON.stringify(resultparams));
             return;
           }//End If
           console.log(cell);
           var parentStage = cell.attributes.parentStage;
           var taskId = cell.attributes.id;
           if( taskDetail &&  taskDetail.taskId == taskId){
             return false;
           } 
           var ref = cell.attributes['model-data'].operators[0].operatorInfo.ref;
           var type = ref.type;
           if(type.slice(-4) == 'Exec'){
             if(type.slice(-9) == 'groupExec'){
               var isGroupExec = true;
             }
             else {
               var isExec = true;
             }
             type = type.slice(0,-4);
           }

           var svg = document.querySelector('svg');
           var pt = svg.createSVGPoint();
           pt.x = d3.event.clientX; pt.y = d3.event.clientY;
           var localPoint = pt.matrixTransform(svg.getScreenCTM().inverse());
           var state;
           if(isExec || isGroupExec){
             var iconMenuItems = [{title:'Show Details', type : 'element'}];
             if($scope.execMode){
               var status = $(".status[element-id=" + taskId + "] .statusTitle")[0].innerHTML;
               if(status && (status=='Completed') ||(status== 'Failed')|| (status== 'InProgress')){
                 if(isExec && (status=='Completed')){
                   iconMenuItems.push({title:'Show Results', type : 'results'});
                 }
                 if(isGroupExec){
                   iconMenuItems.push({title:'Show Results', type : 'results'})
                 }
               }
               else if(status && (status=='NotStarted' || status=='Resume')){
                 iconMenuItems.push({title:'On Hold', type : 'onhold'});
               }
               else if(status && status=='OnHold'){
                 iconMenuItems.push({title:'Resume', type : 'resume'});
               }
               if(status && status=='InProgress'){
                 iconMenuItems.push({title:'Kill', type : 'killexecution'});
               }
             }
             iconMenu.resetItems(iconMenuItems);
             var apis = {
               dq : {name:'dataqual', label: 'DataQual'},
               dqgroup : {name:'dataQualGroup', label: 'DataQualGroup',url :'dataqual/getdqExecBydqGroupExec?'},
               profile : {name:'profile', label: 'Profile'},
               profilegroup : {name:'profilegroup', label: 'ProfileGroup', url : 'profile/getProfileExecByProfileGroupExec?'},
               load : { name:'load', label: 'Load'},
               map : { name:'map', label: 'Map'},
               model : {name:'model', label: 'Model'},
               train : {name:'train', label: 'Train'},
               predict : {name:'predict', label: 'Predict'},
               simulate : {name:'simulate', label: 'Simulate'},
               operator : {name:'operator', label: 'Operator'},
               rule : {name:'rule', label: 'Rule'},
               rulegroup : {name:'rulegroup', label: 'RuleGroup',url:'rule/getRuleExecByRGExec?'},
               recon : {name:'recon', label: 'Recon'},
               recongroup : {name:'recongroup', label: 'ReconGroup',url:'recon/getReconExecByRGExec?'},
             }

             var resultparams = {id:ref.uuid,name:cell.attributes['model-data'].name,elementType:type,version:ref.version,type: apis[type].name ,typeLabel:apis[type].label,url:apis[type].url, ref :ref,parentStage:parentStage,taskId:taskId};
             var url=$location.absUrl().split("app")[0];
             $http.get(url+'metadata/getMetaIdByExecId?action=view&execUuid='+ref.uuid+'&execVersion='+ref.version+'&type='+ref.type).then(function (res) {
              state = {state : dagMetaDataService.elementDefs[res.data.type].state, params : {id :res.data.uuid,version:res.data.version || " ",name:ref.name,type:ref.type,mode:true, returnBack: true}};
               iconMenu(localPoint.x, localPoint.y, JSON.stringify(state),JSON.stringify(resultparams));
             });
           }//End If if(isExec || isGroupExec)
           else {
             var url=$location.absUrl().split("app")[0];
             $http.get(url+'common/getLatestByUuid?action=view&uuid='+ref.uuid+'&type='+ref.type).then(function (res) {
             state = {state : dagMetaDataService.elementDefs[type].state, params : {id :ref.uuid,version:res.data.version,name:ref.name,type:ref.type,mode:true, returnBack: true}};
             iconMenu(localPoint.x, localPoint.y, JSON.stringify(state));
             });
           }//End Else
         });
       }
     };
 
     $scope.$watch('zoomSize',function (newVal,oldVal) {
       try {
         $scope.paper.scale(newVal/10);
       } catch (e) {
       } finally {}
     });

      $scope.$on('closeSubTabs',function (e) {
        $scope.closeSubTabs();
      });
         
       $scope.closeSubTabs = function () {

       App.scrollTop();
       if($scope.showResults){
         $scope.showResults = false;
         if($scope.showGroupGraph)
         $rootScope.showGroupDowne=true;
         else
         $scope.showGroupGraph=false;
         $rootScope.showGrid=false;
         $scope.$emit('resultExecChanged',false);
       }
       else if($scope.showGroupGraph){
         $scope.showGroupGraph = false;
         $rootScope.showGroupDowne=false;
         $rootScope.showGrid=false;
         $scope.$emit('daggroupExecChanged',false);
       }
     }

     var dblClickFn = function(e,newCell,elemt) {
       $scope.isModelDisable=$scope.editMode==false?true:false
      //  if(!$scope.editMode || $scope.isTemplate){
      //    return;
      //  }
        if($scope.isTemplate){
         return;
        }
       $('#divtoshow').hide();
       if(newCell){
         var id = newCell.id;
       }
       else {
         var jointElement = $(this).closest(".joint-element");
         var id = jointElement.attr('model-id');
       }
       var thisModel = newCell || $scope.graph.getCell(id);
       var elementType = thisModel.attributes.elementType;
       if( elementType == "stage" || elementType == "dag"){
         return false;
       }

       var name = thisModel.attributes['model-data'].name || '';
       var text = '';
       var color = '';
       try {
         text = $scope.elementDefs[elementType].caption;
         color = $scope.elementDefs[elementType].color;
         $("#dagcolorID").css("background-color", color);
       }
       catch(e){
         text = elementType;
       }
       $scope.popupModel = {
         id:id,modelData :thisModel.attributes['model-data'],elementTypeText: text,
         headers : {color:color,title:text}
       };
          
       if(thisModel.attributes['model-data'].operators[0].operatorInfo.ref.uuid){
         $scope.popupModel.selectedType = thisModel.attributes['model-data'].operators[0].operatorInfo.ref.uuid+'|'+thisModel.attributes['model-data'].operators[0].operatorInfo.ref.name;
       }
       if(elementType =="operator" &&  !newCell){
        var uuid=$scope.popupModel.modelData.operators[0].operatorInfo.ref.uuid;
        CommonService.getOneByUuidAndVersion(uuid,"","operator").then(function(response){ 
          if(!response || !response.data){
            $scope.operatorinfoMapInfo = [];
            return
          }
          CommonService.getOperatorByOperatorType(response.data.operatorType).then(function(response){
            if(!response || !response.data){
              $scope.operatorinfoMapInfo = [];
              return
            }
            GetAllLatesMap(response.data);
            },function (error) {
              $scope.operatorinfoMapInfo = [];
            });
        });
      }
      else if(elementType =="operator" && newCell){
        var type =elemt.title
        CommonService.getOperatorByOperatorType(type.replace(/ /g,'')).then(function(response){
          if(!response || !response.data){
            $scope.operatorinfoMapInfo = [];
            return
          }
          GetAllLatesMap(response.data);
          },function (error) {
            $scope.operatorinfoMapInfo = [];
          });
      }
      else{ 
        MetadataDagSerivce.getAllLatest(elementType).then(function(response){
          if(!response || !response.data){
            $scope.operatorinfoMapInfo = [];
            return
          }
          GetAllLatesMap(response.data);
          },function (error) {
            $scope.operatorinfoMapInfo = [];
          }); 
         }
         var GetAllLatesMap=function(response){
          $scope.operatorinfoMapInfo = response;
          $scope.popupModel.type=elementType
          if(elementType =="operator" && newCell){
            for(var i=0;i< response.length;i++){
              if(response[i].name == elemt.title){
                $scope.popupModel.selectedType=response[i].uuid +"|"+ response[i].name;
                $scope.onChangeOperatorInfo(false);
                break;
              }
            }
          }   
        }  
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
        // $("#right-side-test").show();
        //$("#right-side-test").css({  'top': top,'left':'2000px' }).animate({'left' : left});
        // $("#right-side-test").css({  'top': '0px','right':'3%'});
        $scope.popupModel.operatorTypeText=text;
        $("#typeOprator").text(text);
        $("#elementTypeText").html(text);
        $("#savePop").attr("data-id", id);
      //  $('#namepop').val(name);
        $('#responsive').modal({
          backdrop: 'static',
          keyboard: false
        });
        var type = $scope.popupModel.modelData.operators[0].operatorInfo.ref.type;
        var typeParamListArray=["simulate","operator"];
        var typeParamSetArray=["train","rule"];
        if(typeParamSetArray.indexOf(type) !=-1 && ($scope.paramsetdata ||  $scope.popupModel.selectedType)){
          $scope.isExecParamSet=false;
          $scope.isTabelShow=false;
          $scope.allparamset=null;
          $scope.allParamList=null;
          $scope.isParamLsitTable=false;
          $scope.selectParamList=null;
          $scope.paramTypes=null;
          $scope.selectParamType=null;
          setTimeout(function(){  $scope.paramTypes=["paramlist","paramset"]; },100);
          if($scope.popupModel.selectedType){
            var temp = $scope.popupModel.selectedType.split('|');
            $scope.popupModel.modelData.operators[0].operatorInfo.ref.uuid = temp[0];
            $scope.popupModel.modelData.operators[0].operatorInfo.ref.name = temp[1];
          
            var objDetail={}
            objDetail.uuid=temp[0];
            objDetail.version="";
            objDetail.type=type;
            $scope.getExecParamsSetAndParamList(objDetail,$scope.popupModel);
          }
          
        }
        else if( typeParamListArray.indexOf(type) != -1 && ($scope.paramListHolder || $scope.popupModel.modelData.operators[0].operatorParams !=null)){
          $scope.isExecParamList=true;
          var temp = $scope.popupModel.selectedType.split('|');
          $scope.popupModel.modelData.operators[0].operatorInfo.ref.uuid = temp[0];
          $scope.popupModel.modelData.operators[0].operatorInfo.ref.name = temp[1];
          var objDetail={}
          objDetail.uuid=temp[0];
          objDetail.version="";
          objDetail.type=type;
          $scope.getExecParamList(objDetail,$scope.popupModel);
        }
      };
       
       

      $scope.savePop = function (popupModel) {
        $scope.isExecParamList=false;    
        $scope.isExecParamSet=false;
        var cell = $scope.graph.getCell(popupModel.id);
        // if(!cell) {
        // //$("#right-side-test").animate({'left' : '2000px'},function() {$("#right-side-test").hide();});
        // $("#right-side-test").animate({'right' : '3%'},function() {$("#right-side-test").hide();});
        
        // return;
        // }
        if(!popupModel.selectedType){
          cell.remove();
        // $("#right-side-test").animate({'left' : '2000px'},function() {$("#right-side-test").hide();});
       // $("#right-side-test").animate({'right' : '3%'},function() {$("#right-side-test").hide();});  
        return;
        }
        var temp = popupModel.selectedType.split('|');
        popupModel.modelData.operators[0].operatorInfo.ref.uuid = temp[0];
        popupModel.modelData.operators[0].operatorInfo.ref.name = temp[1];
        cell.attr('text', { text: popupModel.modelData.name});
       /* var objDetail={}
        objDetail.uuid=temp[0];
        objDetail.version="";
        var type = popupModel.modelData.operators[0].operatorInfo.ref.type;
        objDetail.type=type;
        

      //  $("#right-side-test").animate({'right' : '3%'},function() {$("#right-side-test").hide();});
        var typeParamSetArray=["train"];
        var typeParamListArray=["simulate","operator"];
        if(typeParamSetArray.indexOf(type) != -1){
          $scope.getExecParamsSet(objDetail,popupModel);
        }
      
        if(typeParamListArray.indexOf(type) != -1){
          $scope.getExecParamList(objDetail,popupModel);
        }*/
       
        //$("#right-side-test").animate({'left' : '2000px'},function() {$("#right-side-test").hide();});
      };


       $scope.$on('registerJqueryEvents',function (e,d) {
       $scope.paper.on('link:connect', function(evt, cellView, magnet, arrowhead) {
         var s = evt.sourceView;
         var t = evt.targetView;
         if(s.model.attributes.elementType == 'stage'){
           t.model.attributes.parentStage = s.model.attributes.id;
         }
         else if(dagMetaDataService.validTaskTypes.indexOf(s.model.attributes.elementType) > -1){
           if(s.model.attributes.parentStage){
             t.model.attributes.parentStage = angular.copy(s.model.attributes.parentStage);
           }
           else {
             alert('No Parent stage');
           }
         }
       });
       
       var checkDependsOn=function(dependsOn){
         if(dependsOn.length >0){
           for(var i=0;i<dependsOn.length;i++){
             var id=dependsOn[i];
             var type=$(".body[element-id=" + id + "]").attr("element-type");
             $(".body[element-id=" + id + "]").attr("active",true);
             $(".body[element-id=" + id + "]").attr("xlink:href","assets/layouts/layout/img/"+type+".svg");
             if(typeof id !='undefined'){
               $scope.graph.getCell(id).attributes.attrs[".body"].active=true;
             }
             if(i == dependsOn.length-1){
               if(typeof id !='undefined'){
                 dependsOn.push($scope.graph.getCell(id).attributes['parentStage'])
               }
             }
             if(dependsOn.length >0){
               if(typeof id !='undefined'){
                 checkDependsOn($scope.graph.getCell(id).attributes['model-data'].dependsOn)
               }
             }
            
             }
         }
       }
       var checkDependsOnInActive=function(parentId,taskId,type){
         if(type !="stage"){
          // debugger;
           for(var i=0;i<$scope.dagData.stages.length;i++){
             if(parentId == $scope.dagData.stages[i].stageId){
               for(var j=0;j<$scope.dagData.stages[i].tasks.length;j++){
                 var dependsOn=$scope.dagData.stages[i].tasks[j].dependsOn
                 if(dependsOn.indexOf(taskId) != -1){
                 var id=$scope.dagData.stages[i].tasks[j].taskId;
                 var type=$(".body[element-id=" + id + "]").attr("element-type");
                 $(".body[element-id=" + id + "]").attr("active",true);
                 $(".body[element-id=" + id + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
                 $scope.graph.getCell(id).attributes.attrs[".body"].active=false;
                 checkDependsOnInActive($scope.graph.getCell(id).attributes['parentStage'],id,type)
                 }  
               }
               if($scope.dagData.stages[i].tasks.length == 1){
                 var type=$(".body[element-id=" + parentId + "]").attr("element-type");
                 $(".body[element-id=" + parentId + "]").attr("active",true);
                 $(".body[element-id=" + parentId + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
                 $scope.graph.getCell(parentId).attributes.attrs[".body"].active=false;
               }else{
                // debugger;
                 var countActiveTask=0;
                 for(var j=0;j<$scope.dagData.stages[i].tasks.length;j++){
                   var taskId=$scope.dagData.stages[i].tasks[j].taskId
                   if($scope.graph.getCell(taskId).attributes.attrs[".body"].active ==true){
                     countActiveTask=countActiveTask+1;
                   }
                 }  
                 if(countActiveTask ==0){
                   var type=$(".body[element-id=" + parentId + "]").attr("element-type");
                   $(".body[element-id=" + parentId + "]").attr("active",true);
                   $(".body[element-id=" + parentId + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
                   $scope.graph.getCell(parentId).attributes.attrs[".body"].active=false;
                 }
               }
               
             }
           }
         }
         else{
           for(var i=0;i<$scope.dagData.stages.length;i++){
            // debugger;
             if(taskId == $scope.dagData.stages[i].stageId){
               for(var j=0;j<$scope.dagData.stages[i].tasks.length;j++){
                 var id=$scope.dagData.stages[i].tasks[j].taskId;
                 var type=$(".body[element-id=" + id + "]").attr("element-type");
                 $(".body[element-id=" + id + "]").attr("active",true);
                 $(".body[element-id=" + id + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
                 $scope.graph.getCell(id).attributes.attrs[".body"].active=false;
                
                //checkDependsOnInActive($scope.graph.getCell(id).attributes['parentStage'],id,type)
               }
               checkDependsOn($scope.dagData.stages[i].dependsOn);
             }
             
           }

         }
       }  

       $( "#paper" ).on("dblclick",".joint-element .body",dblClickFn);
       $( "#paper" ).on("click",".joint-element .body",function(e){
         
         var jointElement = $(this).closest(".joint-element");
         var id = jointElement.attr("model-id");
         var type=$(".body[element-id=" + id + "]").attr("element-type");
         if(type =="dag" || !$scope.isTemplate){
           return false;
         }else if(!$scope.editMode){
           return false;
         }
         var cell = $scope.graph.getCell(id)
         console.log(cell);
         console.log($scope.graph.getCell(id).attributes.attrs[".body"].active)
         if($scope.graph.getCell(id).attributes.attrs[".body"].active == false){
           var type=$(".body[element-id=" + id + "]").attr("element-type");
           $(".body[element-id=" + id + "]").attr("active",true);
           $(".body[element-id=" + id + "]").attr("xlink:href","assets/layouts/layout/img/"+type+".svg");
           $scope.graph.getCell(id).attributes.attrs[".body"].active=true;
           if(type !="dag"){
             var dependsOn=cell.attributes['model-data'].dependsOn;
           
             if(dependsOn.length >0){
               checkDependsOn($scope.graph.getCell(id).attributes['model-data'].dependsOn);
             }else{
               checkDependsOn([$scope.graph.getCell(id).attributes['parentStage']]);
             }
           }
         }
         else if($scope.graph.getCell(id).attributes.attrs[".body"].active == true){
           var type=$(".body[element-id=" + id + "]").attr("element-type");
           $(".body[element-id=" + id + "]").attr("active",true);
           $(".body[element-id=" + id + "]").attr("xlink:href","assets/layouts/layout/img/"+type+"inactive.svg");
           $scope.graph.getCell(id).attributes.attrs[".body"].active=false;
           //console.log($scope.graph.getCells());
           checkDependsOnInActive($scope.graph.getCell(id).attributes['parentStage'],id,type);
         }
         //console.log( $scope.graph.getCell(id));
         
       });
       
       $( "#paper" ).on("click",".joint-element .remove",function(e){
         if(!$scope.editMode){
           return;
         }
         var jointElement = $(this).closest(".joint-element");
         var s = jointElement.attr("model-id");
         var cell = $scope.graph.getCell(s);
         cell.remove();
       });
            
       $( "#paper" ).on("mouseover",".joint-element .body",function(e){
      
         if($scope.isTemplate){
           var divid = 'divtoshow';
           $("#"+divid).hide();
           $( "#paper .joint-element").find(".remove").css("display","none");
          // return false;
         }
         $('.joint-element').mouseout(function(e){
           var divid = 'divtoshow';
           $("#"+divid).hide();
           $('.connection').removeClass('active');
         });
         var jointElement = $(this).closest(".joint-element");
         var s = jointElement.attr("model-id");
         $('.connection[source-id='+s+']').addClass('active');
         var cell = $scope.graph.getCell(s);
         var elementModel = cell.attributes['model-data'];
      
         try {
           var elementType = elementModel.operators[0].operatorInfo.ref.type;
           if(elementType.slice(-4) == 'Exec'){
             elementType = elementType.slice(0,-4);
           }
           if(['dag','stage'].indexOf(elementType) ==-1){
            elementModel.version=cell.attributes['dagversion'];
          }
         }catch(e){
           if(s.substr(0,3)=='dag'){
             var cell = $(this);
             var elementModel = cell.attr("model-data") ? JSON.parse(cell.attr("model-data")) : undefined;
             var elementType = 'dag';
           }
           else if(cell.attributes.elementType=='stage'){
             var elementType = 'stage';
             var elementModel = {name : cell.attributes['model-data'].name, type : elementType, uuid:cell.attributes['model-data'].stageId};
           }else{
             var elementType = undefined;
             elementModel.version=cell.attributes['dagversion'];
           }
         }
       
         
         var allowedHover = angular.copy(dagMetaDataService.validTaskTypes);
         allowedHover.push('dag');
         allowedHover.push('stage');
         if(!elementModel || allowedHover.indexOf(elementType) < 0 || $scope.createMode){
           return false;
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
           dagtypetext = $scope.elementDefs[elementType].caption;
           color = $scope.elementDefs[elementType].color;
           $("#dagcolorID").css("background-color", color);
         }catch(e){
           dagtypetext = elementType;
           $("#dagcolorID").css("background-color", "blue");
         }
         
         if(!elementModel || Object.keys(elementModel).length == 0){
           var txt1 ="", txt2 = "", txt3 = "";
         }
         else {
           var directRef = ['dag','stage'];
           var txt1 = directRef.indexOf(elementType) > -1 ? elementModel.uuid : elementModel.taskId ||'' //operators[0].operatorInfo.ref.uuid || '';
           var txt2 = directRef.indexOf(elementType) > -1 ? elementModel.name : elementModel.name || '';
           var txt3 = directRef.indexOf(elementType) > -1 ? elementModel.version : elementModel.version || ''//elementModel.operators[0].operatorInfo.ref.version || '';
         }

         $("#elementTypeText").html(dagtypetext);
         $("#task_Id").html(txt1);
         $("#task_Name").html(txt2);
         if(txt3 == ''){
           $("#task_Version_label").html('');
           $("#task_Version").html('');
         }else {
           $("#task_Version_label").html('Version');
           $("#task_Version").html(txt3);
         }
         $("#"+divid).show();
         var status = jointElement.find('.status');
         var startTime = status.attr("inprogress");
         var endTime = status.attr("completed");
         $scope.popoverData = {};
         $scope.popoverData.startTime = startTime || '-';
         $scope.popoverData.endTime = endTime || '-';
         var intdate1= parseInt($scope.popoverData.startTime)
         var intdate2= parseInt($scope.popoverData.endTime)
         var date1 = new Date(intdate1)
         var date2 = new Date(intdate2)
         $scope.popoverData.timeDiff='-'
         if(endTime){
           $scope.popoverData.timeDiff = moment.utc(moment(date2).diff(moment(date1))).format("HH:mm:ss")
         }
         $scope.$apply();//this is required
       });
     });
          
     window.addelement = function(e,elemt){
       var operator=elemt.type
       //create sub elements on this event
       $scope.isExecParamList=false;    
       $scope.isExecParamSet=false;
       $scope.paramListHolder=null;
       var localPoint = $scope.paper.clientToLocalPoint(e.clientX, e.clientY);
       var operator = operator || 'dqgroup';
       var cell = new joint.shapes.devs.Model({
         markup: '<g class="rotatable"><g class="scalable"><image class="body"/></g><image class="remove"/><text class="label" /><g class="inPorts"/><g class="outPorts"/></g>',
         // id: "dag_0",
         elementType: operator,
         parentStage: '',
         dagversion:'N/a',
         "model-data": {
           name : 'New '+operator,
           operators : [ {
             operatorInfo : {
               ref : {
                 name : '',
                 type : operator,
               }
             },
             operatorParams:null
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
                   r:7,
                   cx:-5
                 }
               }
             },
             'out': {
               attrs: {
                 '.port-body': {
                   fill: '#fff',
                     r: 7,
                     cx: 5
                 }
               }
             }
           }
         },
         elementType : operator,
          attrs: {
            "model-data": {
              name : 'New '+operator,
              operators : [ {
                operatorInfo : {
                  ref : {
                    name : '',
                    type : operator,
                  }
                },
                operatorParams:{}
              }]
            },
           '.body': {
              elementType : operator,
              'active':true,
              "model-data": "{}",
              x:"0", y:"0",height:"50px", width:"50px",
              "xlink:href": $scope.elementDefs[operator].iconPath
            },
            '.remove': {
              x:"55", y:"-20",height:"25px", width:"25px",
              "xlink:href": "assets/layouts/layout/img/delete.png"
            },
            text:{text:''},
            magnet:true,
            text: { text: 'New '+operator,y:'60px', 'font-size': 10, style: { 'text-shadow': '1px 1px 1px lightgray; text-transform: capitalize' } }
          }
        });
        $scope.graph.addCell(cell);
        dblClickFn(e,cell,elemt);
      };

     /*function contextMenu() {
       var height,
       width,
       margin = 0.1, // fraction of width
       items = [],
       rescale = false,
       style = {
         'rect': {
           'mouseout': {
             'fill': 'rgb(0,0,0)',
             'opacity':'0.8',
             'stroke': 'white',
             'stroke-width': '1px'
           },
           'mouseover': {
             'fill': '#32c5d2'
           }
         },
         'text': {
           'fill': 'white',
           'font-size': '13',
           'font-family': 'Open Sans'
         }
       };

       function menu(x, y) {
         d3.select('.context-menu').remove();
         scaleItems();
         // Draw the menu
         d3.select('svg')
           .append('g').attr('class', 'context-menu')
           .selectAll('tmp')
           .data(items).enter()
           .append('g').attr('class', 'menu-entry')
           .style({'cursor': 'pointer'})
           .on('mouseover', function(){
             d3.select(this).select('rect').style(style.rect.mouseover) })
           .on('mouseout', function(){
             d3.select(this).select('rect').style(style.rect.mouseout) });
           d3.selectAll('.menu-entry')
             .append('rect')
             .attr('x', x)
             .attr('y', function(d, i){ return y + (i * height); })
             .attr('id', function(d){ return d.id; })
             .attr('onclick', function(d){return "addelement(event,'"+d.type+"');"})
             .attr('width', width+50)
             .attr('height', height)
             .style(style.rect.mouseout);

           d3.selectAll('.menu-entry')
             .append('image')
             .attr('x', x+10)
             .attr('y', function(d, i){ return y + 10 + (i * height); })
             .attr('onclick', function(d){return "addelement(event,'"+d.type+"');"})
             .attr('width','15')
             .attr('height','15')
             .attr('stroke-width','1')
             .attr('stroke','#ccc')
             .attr('xmlns:xlink',"http://www.w3.org/1999/xlink")
             .attr('xlink:href',function(d){return d.image;});
                  
             d3.selectAll('.menu-entry')
               .append('text')
               .text(function(d){ return d.title; })
               .attr('onclick', function(d){return "addelement(event,'"+d.type+"');"})
               .attr('x', x+25)
               .attr('y', function(d, i){ return y + (i * height); })
               .attr('dy', height - 10 - margin / 2)
               .attr('dx', margin)
               .style(style.text);
             // Other interactions
             
             d3.select('body')
               .on('click', function() {
                 d3.select('.context-menu').remove();
               });
             }

             menu.items = function(argumentItems) {
               if (!argumentItems.length) return items;
                 for (i in argumentItems) items.push(argumentItems[i]);
                   rescale = true;
                   return menu;
             }

             // Automatically set width, height, and margin;
             function scaleItems() {
               if (rescale) {
                 d3.select('svg').selectAll('tmp')
                   .data(items).enter()
                   .append('text')
                   .text(function(d){ return d.title; })
                   .style(style.text)
                   .attr('x', -1000)
                   .attr('y', -1000)
                   .attr('class', 'tmp');
                     
                 var z = d3.selectAll('.tmp')[0]
                   .map(function(x){ return x.getBBox(); });
                   width = d3.max(z.map(function(x){ return x.width; }));
                   margin = margin * width;
                   width =  width + 2 * margin;
                   height = d3.max(z.map(function(x){ return x.height+ 15 + margin / 2; }));
                   // cleanup
                 d3.selectAll('.tmp').remove();
                 rescale = false;
               }
             }
           return menu;
         }

         var menuitems = [];
         angular.forEach(dagMetaDataService.elementDefs,function (element,type) {
           if(element.allowInMenu){
             menuitems.push({
               image : element.iconPath,
               title : element.name,
               id : element.name + '-add',
               type : type
             })
           }
         });
         var menu = contextMenu().items(menuitems);*/

      function iconContextMenu() {
        var height,
        width,
        margin = 0.1, // fraction of width
        items = [],
        rescale = false,
          style = {
            'rect': {
              'mouseout': {
                'fill': 'rgb(0,0,0)',
                'opacity':'0.8',
                'stroke': 'white',
                'stroke-width': '1px'
              },
              'mouseover': {
                'fill': '#32c5d2'
              }
            },
            'text': {
              'fill': 'white',
              'font-size': '13',
              'font-family': 'Open Sans'
            }
          };

          function menu(x, y, url,resultParams) {
            d3.select('.context-menu').remove();
            scaleItems();
            // Draw the menu
            d3.select('svg')
              .append('g').attr('class', 'context-menu')
              .selectAll('tmp')
              .data(items).enter()
              .append('g').attr('class', 'menu-entry')
              .style({'cursor': 'pointer'})
              .on('mouseover', function(){
                d3.select(this).select('rect').style(style.rect.mouseover) })
              .on('mouseout', function(){
                d3.select(this).select('rect').style(style.rect.mouseout) });
            d3.selectAll('.menu-entry')
              .append('rect')
              .attr('x', x)
              .attr('y', function(d, i){ return y + (i * height); })
              .attr('onclick', function(d){
                if(d.type == 'results'){
                  return 'showResult('+resultParams+')'
                }
                else if(d.type == 'element'){
                  return "navigateTo('"+url+"');"
                }
                else if(d.type == 'onhold'){
                  return 'setStatus('+resultParams+',"OnHold")'
                }
                else if(d.type == 'resume'){
                  return 'setStatus('+resultParams+',"Resume")'
                }
                else if(d.type == 'killexecution'){
                  return 'setStatus('+resultParams+',"Killed")'
                }
              })
              .attr('width', width+50)
              .attr('height', height)
              .style(style.rect.mouseout);

            d3.selectAll('.menu-entry')
              .append('text')
              .text(function(d){ return d.title; })
              .attr('onclick', function(d){
                if(d.type == 'results'){
                  return 'showResult('+resultParams+')'
                }
                else if(d.type == 'element'){
                  return "navigateTo('"+url+"');"
                }
                else if(d.type == 'onhold'){
                  return 'setStatus('+resultParams+',"OnHold")'
                }
                else if(d.type == 'resume'){
                  return 'setStatus('+resultParams+',"Resume")'
                }
                else if(d.type == 'killexecution'){
                  return 'setStatus('+resultParams+',"Killed")'
                }
              })
              .attr('x', x+25)
              .attr('y', function(d, i){ return y + (i * height); })
              .attr('dy', height - 10 - margin / 2)
              .attr('dx', margin)
              .style(style.text);
              // Other interactions
            d3.select('body')
              .on('click', function() {
                d3.select('.context-menu').remove();
              });
          }//end Function

          menu.items = function(argumentItems) {
            if (!argumentItems.length) return items;
              for (i in argumentItems) items.push(argumentItems[i]);
                rescale = true;
                return menu;
          }

          menu.resetItems = function(argumentItems) {
            if (!argumentItems.length) return items;
              items = [];
              for (i in argumentItems) items.push(argumentItems[i]);
              //  rescale = true;
            return menu;
          }

          // Automatically set width, height, and margin;
          function scaleItems() {
            if (rescale) {
              d3.select('svg').selectAll('tmp')
              .data(items).enter()
              .append('text')
              .text(function(d){ return d.title; })
              .style(style.text)
              .attr('x', -1000)
              .attr('y', -1000)
              .attr('class', 'tmp');
                
              var z = d3.selectAll('.tmp')[0]
              .map(function(x){ return x.getBBox(); });
              width = d3.max(z.map(function(x){ return x.width; }));
              margin = margin * width;
              width =  (width + 2 * margin) < 100 ? 100 : (width + 2 * margin);
              height = d3.max(z.map(function(x){ return x.height+ 15 + margin / 2; }));
              // cleanup
              d3.selectAll('.tmp').remove();
              rescale = false;
            }
          }
        return menu;
      }
      
      var iconMenuItems = [{title:'Show Details', type : 'element'}];
      if($scope.execMode){
      }
      var iconMenu = iconContextMenu().items(iconMenuItems);
      

      $scope.onChangeOperatorInfo=function(defaultValue){
       // $scope.popupModel.modelData.operators[0].operatorParams=null;
       $scope.paramListHolder=null;
        var temp = $scope.popupModel.selectedType.split('|');
        $scope.popupModel.modelData.operators[0].operatorInfo.ref.uuid = temp[0];
        $scope.popupModel.modelData.operators[0].operatorInfo.ref.name = temp[1];
        var objDetail={}
        objDetail.uuid=temp[0];
        objDetail.version="";
        var type = $scope.popupModel.modelData.operators[0].operatorInfo.ref.type;
        objDetail.type=type;
        var typeParamSetArray=["train","rule"];
        var typeParamListArray=["simulate","operator"];
        if(typeParamSetArray.indexOf(type) != -1){
          $scope.getExecParamsSet(objDetail,$scope.popupModel);
          $scope.isExecParamSet=true;
        }
      
        if(typeParamListArray.indexOf(type) != -1){
          if(defaultValue){
            $scope.popupModel.modelData.operators[0].operatorParams=null;
          }
          $scope.getExecParamList(objDetail,$scope.popupModel);
          $scope.isExecParamList=true
        }

        
      }
      $scope.getExecParamsSet = function (data) {
        CommonService.getParamSetByType(data.type,data.uuid,data.version).then(function (response) {
          onSuccessGetExecuteModel(response.data)
        });
        var onSuccessGetExecuteModel = function (response) {
          $scope.allparamset = response;
          if($scope.popupModel.modelData.operators[0].operatorParams !=null){
            $scope.isExecParamSet=true;
            for(var i=0;i< response.length;i++){
              if(response[i].uuid == $scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramInfo[0].ref.uuid){
                $scope.paramsetdata=response[i];
                $scope.onSelectparamSet($scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramInfo);    
                break;
              }
            }
          }
        }
      }
      $scope.getParamListByTrainORRule=function(data){
        $scope.paramlistdata=null;
        CommonService.getParamListByTrainORRule(data.uuid,data.version,data.type).then(function (response){ onSuccesGetParamListByTrain(response.data)});
        var onSuccesGetParamListByTrain = function (response) {
          $scope.allParamList=response;
          if($scope.popupModel.modelData.operators[0].operatorParams !=null){
            $scope.isExecParamSet=true;
            for(var i=0;i< response.length;i++){
              if(response[i].uuid == $scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramListInfo[0].ref.uuid){
                $scope.paramlistdata=response[i]; 
                $scope.onChangeParamList();
                break;
              }
            }
          }
        }
      }
      $scope.onChangeParamType=function(){
        $scope.allparamset=null;
        $scope.allParamList=null;
        $scope.isParamLsitTable=false;
        $scope.selectParamList=null;
        $scope.popupModel.modelData.operators[0].operatorInfo;
        var objDetail={};
        objDetail.uuid=$scope.popupModel.modelData.operators[0].operatorInfo.ref.uuid;
        objDetail.type=$scope.popupModel.modelData.operators[0].operatorInfo.ref.type;
        objDetail.version= "";
        if($scope.selectParamType =="paramlist"){
          $scope.paramlistdata=null;
          $scope.getParamListByTrainORRule(objDetail);
        }
        else if($scope.selectParamType =="paramset"){
          $scope.getExecParamsSet(objDetail);
        }
      }
      $scope.onChangeParamList=function(){
        $scope.isParamLsitTable=false;
        CommonService.getParamByParamList($scope.paramlistdata.uuid,"paramlist").then(function (response){ onSuccesGetParamListByTrain(response.data)});
        var onSuccesGetParamListByTrain = function (response) {
          $scope.isParamLsitTable=true;
          $scope.selectParamList=response;
          var paramArray=[];
          for(var i=0;i<response.length;i++){
            var paramInfo={}
              paramInfo.paramId=response[i].paramId; 
              paramInfo.paramName=response[i].paramName;
              paramInfo.paramType=response[i].paramType.toLowerCase();
              if(response[i].paramValue !=null && response[i].paramValue.ref.type == "simple"){
                paramInfo.paramValue=response[i].paramValue.value;
                paramInfo.paramValueType="simple"
            }else if(response[i].paramValue !=null){
              var paramValue={};
              paramValue.uuid=response[i].paramValue.ref.uuid;
              paramValue.type=response[i].paramValue.ref.type;
              paramInfo.paramValue=paramValue;
              paramInfo.paramValueType=response[i].paramValue.ref.type;
            }else{
              
            }
            paramArray[i]=paramInfo;
          }
          $scope.selectParamList.paramInfo=paramArray;
        }
      }
      $scope.getExecParamsSetAndParamList = function (data) {
        $scope.paramtablecol = null
        $scope.paramtable = null;
        $scope.isTabelShow = false;
        if($scope.popupModel.modelData.operators[0].operatorParams !=null){
          $scope.isExecParamSet=true;
          if($scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramListInfo){
            $scope.selectParamType="paramlist";
            $scope.getParamListByTrainORRule(data);
          }
          else if($scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramInfo){
            $scope.selectParamType="paramset";
            $scope.getExecParamsSet(data);
          }
        }
      }
    
      $scope.onSelectparamSet = function (selectedParamInfo) {
        var paramSetjson = {};
        var paramInfoArray = [];
        $scope.selectallattribute=false;
        var selectedParamIdArray=[];
        if(selectedParamInfo !=null){
          for(var i=0;i<selectedParamInfo.length;i++){
            selectedParamIdArray[i]=selectedParamInfo[i].paramSetId;
          }
        }
        if ($scope.paramsetdata != null) {
          for (var i = 0; i < $scope.paramsetdata.paramInfo.length; i++) {
            var paramInfo = {};
            paramInfo.paramSetId = $scope.paramsetdata.paramInfo[i].paramSetId;
            if(selectedParamIdArray.length >0 && selectedParamIdArray.indexOf(paramInfo.paramSetId) != -1){
              paramInfo.selected=true;
              if($scope.paramsetdata.paramInfo.length == selectedParamIdArray.length){
                $scope.selectallattribute=true;
              }
            }
            var paramSetValarray = [];
            for (var j = 0; j < $scope.paramsetdata.paramInfo[i].paramSetVal.length; j++) {
              var paramSetValjson = {};
              
              paramSetValjson.paramId = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramId;
              paramSetValjson.paramName = $scope.paramsetdata.paramInfo[i].paramSetVal[j].paramName;
              paramSetValjson.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
              paramSetValjson.ref = $scope.paramsetdata.paramInfo[i].paramSetVal[j].ref;
              paramSetValarray[j] = paramSetValjson;
              paramInfo.paramSetVal = paramSetValarray;
              paramInfo.value = $scope.paramsetdata.paramInfo[i].paramSetVal[j].value;
            }
            paramInfoArray[i] = paramInfo;
          }
          $scope.paramtablecol = paramInfoArray[0].paramSetVal;
          $scope.paramtable = paramInfoArray;
          paramSetjson.paramInfoArray = paramInfoArray;
          $scope.isTabelShow = true;
        } else {
          $scope.isTabelShow = false;
        }
      }
      
      $scope.selectAllRow = function () {
        angular.forEach($scope.paramtable, function (stage) {
          stage.selected = $scope.selectallattribute;
        });
      }
      
      $scope.executeWithExecParams = function () {
        var cell = $scope.graph.getCell($scope.popupModel.id);
        cell.attr('text', { text: $scope.popupModel.modelData.name});
        $scope.isExecParamList=false;    
        $scope.isExecParamSet=false;
        if($scope.selectParamType =="paramlist"){
          if($scope.paramlistdata){
            var execParams = {};
            var paramListInfo =[];
            var paramInfo={};
            var paramInfoRef={};
            paramInfoRef.uuid=$scope.paramlistdata.uuid;
            paramInfoRef.type="paramlist";
            paramInfo.ref=paramInfoRef;
            paramListInfo[0]=paramInfo;
            var EXEC_PARAMS={};
            EXEC_PARAMS.paramListInfo = paramListInfo;
            execParams.EXEC_PARAMS=EXEC_PARAMS;
          }else{
            execParams=null;
          }
          $scope.paramlistdata=null;
          $scope.selectParamType=null;
        }
        else{
          $scope.newDataList = [];
          $scope.selectallattribute = false;
          angular.forEach($scope.paramtable, function (selected) {
            if (selected.selected) {
              $scope.newDataList.push(selected);
            }
          });
          var paramInfoArray = [];
          if ($scope.newDataList.length > 0) {
            var execParams = {}
            var ref = {}
            ref.uuid = $scope.paramsetdata.uuid;
            ref.version = $scope.paramsetdata.version;
            ref.type = 'paramset';
            for (var i = 0; i < $scope.newDataList.length; i++) {
              var paraminfo = {};
              paraminfo.paramSetId = $scope.newDataList[i].paramSetId;
              paraminfo.ref = ref;
              paramInfoArray[i] = paraminfo;
            }
          }
          if (paramInfoArray.length > 0) {
            var EXEC_PARAMS={};
            EXEC_PARAMS.paramInfo = paramInfoArray;
            execParams.EXEC_PARAMS=EXEC_PARAMS;
          } else {
            execParams = null
          }
        }
        $('#responsive').modal('hide');
       // $('.modal-open').css('overflow-y', 'auto !important');
        console.log(JSON.stringify(execParams));
        $scope.popupModel.modelData.operators[0].operatorParams=execParams;
      }

     
      $scope.getExecParamList=function(data){
       
        $scope.attributeTypes=['datapod','dataset','rule'];
        CommonService.getParamListByType(data.type,data.uuid,data.version).then(function (response) {
          onSuccessGetExecuteModel(response.data)
        });
        var onSuccessGetExecuteModel = function (response) {
          if(response.length ==0){
            //$scope.executeWithParams(null);
          }else{
            $('#responsive').modal({
              backdrop: 'static',
              keyboard: false
            });
          //  $scope.getAllLatest(null,null,false);
            $scope.paramListHolder = response;
            $scope.opringinalparamListHolder=$scope.paramListHolder
            var paramListHolder=[]
            if($scope.popupModel.modelData.operators[0].operatorParams !=null){
              var paramListInfo=$scope.popupModel.modelData.operators[0].operatorParams.EXEC_PARAMS.paramListInfo
              for(var i=0;i<paramListInfo.length;i++){ 
                var paramList={};
                paramList.uuid=paramListInfo[i].ref.uuid;
                paramList.type=paramListInfo[i].ref.type;
                paramList.paramId=paramListInfo[i].paramId;
                paramList.paramType=paramListInfo[i].paramType.toLowerCase();
                paramList.paramName=paramListInfo[i].paramName;
                paramList.ref=paramListInfo[i].ref;      
                if(paramListInfo[i].paramValue && paramListInfo[i].paramValue.ref.type =='distribution' ||
                paramListInfo[i].paramValue && paramListInfo[i].paramValue.ref.type =='datapod'){
                var paramValue={}
                var selectedParamValue={};
                selectedParamValue.type=paramListInfo[i].paramValue.ref.type;
                selectedParamValue.uuid=paramListInfo[i].paramValue.ref.uuid;
                paramValue.selectedParamValue=selectedParamValue;
                paramList.paramValue=paramValue;
                paramList.selectedParamValue=selectedParamValue;
                paramList.selectedParamValueType=paramListInfo[i].paramValue.ref.type
                paramListHolder[i]=paramList;
               
               }else if(paramListInfo[i].paramValue && paramListInfo[i].paramValue.ref.type== "simple" && paramListInfo[i].paramType !='list'){
                var paramValue={}
                paramValue.paramValue=paramListInfo[i].paramValue.value
                paramValue.selectedParamValueType=paramListInfo[i].paramValue.ref.type;
                paramValue.selectedParamValue=selectedParamValue
                paramList.selectedParamValueType=paramListInfo[i].paramValue.ref.type
                paramList.paramValue=paramListInfo[i].paramValue.value;
                paramListHolder[i]=paramList
               }
               else if(paramListInfo[i].paramValue && paramListInfo[i].paramValue.ref.type== "simple" && paramListInfo[i].paramType =='list'){
                var paramValue={}
                paramValue.paramValue=paramListInfo[i].paramValue.value
                paramValue.selectedParamValueType=paramListInfo[i].paramValue.ref.type;
                paramValue.selectedParamValue=selectedParamValue
                paramList.selectedParamValueType=paramListInfo[i].paramType;
                paramList.paramValue=paramListInfo[i].paramValue.value;
                if($scope.opringinalparamListHolder.length <= paramListInfo.length){
                  paramList.allListInfo=$scope.opringinalparamListHolder[i].allListInfo;
                }
                paramListHolder[i]=paramList
               }
               else if(paramListInfo[i].paramType == "attribute"){
                var selectedParamValue={}
                var attributeInfo={}
                paramList.selectedParamValueType=paramListInfo[i].attributeInfo[0].ref.type;
                selectedParamValue.uuid=paramListInfo[i].attributeInfo[0].ref.uuid;
                selectedParamValue.type=paramListInfo[i].attributeInfo[0].ref.type;
                paramList.selectedParamValue=selectedParamValue
                attributeInfo.uuid=paramListInfo[i].attributeInfo[0].ref.uuid;
                attributeInfo.type=paramListInfo[i].attributeInfo[0].ref.type;
                attributeInfo.attributeId=paramListInfo[i].attributeInfo[0].attrId;
                paramList.attributeInfo=attributeInfo
                paramListHolder[i]=paramList
               }
               else if(paramListInfo[i].paramType == "attributes"){
                var selectedParamValue={}
                paramList.selectedParamValueType=paramListInfo[i].attributeInfo[0].ref.type;
                selectedParamValue.uuid=paramListInfo[i].attributeInfo[0].ref.uuid;
                selectedParamValue.type=paramListInfo[i].attributeInfo[0].ref.uuid;
                paramList.selectedParamValue=selectedParamValue
                var attributeInfoArray=[]
                for(var j=0;j < paramListInfo[i].attributeInfo.length;j++){
                var attributeInfo={}
                attributeInfo.uuid=paramListInfo[i].attributeInfo[j].ref.uuid;
               
               // attributeInfo.type=paramListInfo[i].attributeInfo[j].ref.type;
                attributeInfo.datapodname=paramListInfo[i].attributeInfo[j].ref.name;
                attributeInfo.name=paramListInfo[i].attributeInfo[j].attrName;
                attributeInfo.dname=paramListInfo[i].attributeInfo[j].ref.name+"."+paramListInfo[i].attributeInfo[j].attrName;
                attributeInfo.attributeId=paramListInfo[i].attributeInfo[j].attrId;
                attributeInfo.id=paramListInfo[i].attributeInfo[j].ref.uuid+"_"+paramListInfo[i].attributeInfo[j].attrId;
                attributeInfoArray[j]=attributeInfo
                }
                paramList.attributeInfoTag=attributeInfoArray
                paramList.allAttributeinto=[];
                paramListHolder[i]=paramList
               }
                
              }
              console.log(paramListHolder )
              $scope.paramListHolder = paramListHolder;
           //   $scope.opringinalparamListHolder=$scope.paramListHolder
            }else{
              $scope.paramListHolder = response;
              for(var i=0;i<$scope.paramListHolder.length;i++){
                if(['list','simple'].indexOf($scope.paramListHolder[i].isParamType) ==-1){
                  if( $scope.paramListHolder[i].paramValue && $scope.paramListHolder[i].paramValue.ref.type =='distribution'){
                    $scope.onChangeDistribution($scope.paramListHolder[i].selectedParamValue,i);
                    break;
                  }
                }
              }
              $scope.opringinalparamListHolder=$scope.paramListHolder
            }
          }
        }
    
      }

      $scope.getAllLatest=function(type,index,defaultValue){  
        CommonService.getAllLatest(type || "datapod").then(function (response) { onSuccessGetAllLatest(response.data) });
        var onSuccessGetAllLatest = function (response) {
          if(type =="datapod"){
            $scope.allDatapod=response;
          }
          else if(type =="relation"){
            $scope.allRelation=response;
          }
          else if(type =="distribution"){
            $scope.allDistribution=response;
          }
          else if(type =="dataset"){
            $scope.allDataset=response;
          }
          else if(type =="rule"){
            $scope.allRule=response;
          }
         
        }
      }

      $scope.onChangeParamValueType=function(type,index){
        if(type !='simple'){
          $scope.getAllLatest(type,index,true);
        }else{
          $scope.paramListHolder[index].paramValue="";
        }
      }
      
      $scope.onChangeForAttributeInfo=function(data,type,index){
        $scope.getAllAttributeBySource(data,type,index);

      }
      
      $scope.getAllAttributeBySource=function(data,type,index,defaultValue){ 
        if(data !=null){ 
          CommonService.getAllAttributeBySource(data.uuid,type).then(function (response) { onSuccessGetAllAttributeBySource(response.data) });
          var onSuccessGetAllAttributeBySource = function (response) {
            $scope.paramListHolder[index].allAttributeinto=response
          
          }
        }
      }
      $scope.onChangeDistribution=function(data,index){
        
        CommonService.getParamListByType('distribution',data.uuid,data.version | "").then(function (response){ onSuccessGetParamListByType(response.data)});
        var onSuccessGetParamListByType = function (response) {
          if($scope.paramListHolder.length == $scope.opringinalparamListHolder.length){
            $scope.opringinalparamListHolder=$scope.paramListHolder;
          }
          else{
            $scope.paramListHolder=$scope.paramListHolder.slice(0,$scope.opringinalparamListHolder.length);
          }
          var paramList
          paramList = $scope.paramListHolder.concat(response);
          $scope.paramListHolder=paramList;
       //   $scope.opringinalparamListHolder=$scope.paramListHolder;

        }
      }
      $scope.loadAttributes = function(query,index) {
        return $timeout(function () {
          return $filter('filter')($scope.paramListHolder[index].allAttributeinto, query);
        });
       };
       
      $scope.executeWithExecParamList=function(){
        $scope.isExecParamList=false;    
        $scope.isExecParamSet=false;
       console.log(JSON.stringify($scope.paramListHolder))
        var cell = $scope.graph.getCell($scope.popupModel.id);
        cell.attr('text', { text: $scope.popupModel.modelData.name});
        $('#responsive').modal('hide');
        var execParams={};
        var paramListInfo=[];
        if($scope.paramListHolder.length>0){
          for(var i=0;i<$scope.paramListHolder.length;i++){
            var paramList={};
            paramList.paramId=$scope.paramListHolder[i].paramId;
          //  paramList.paramName=$scope.paramListHolder[i].paramName;
          //  paramList.paramType=$scope.paramListHolder[i].paramType;
            paramList.ref=$scope.paramListHolder[i].ref;
            if($scope.paramListHolder[i].paramType =='attribute'){
              var attributeInfoArray=[];
              var attributeInfo={};
              var attributeInfoRef={}
              attributeInfoRef.type=$scope.paramListHolder[i].selectedParamValueType;
              attributeInfoRef.uuid=$scope.paramListHolder[i].attributeInfo.uuid;
            //  attributeInfoRef.name=$scope.paramListHolder[i].attributeInfo.name
              attributeInfo.ref=attributeInfoRef;
              attributeInfo.attrId=$scope.paramListHolder[i].attributeInfo.attributeId;
              attributeInfoArray[0]=attributeInfo
              paramList.attributeInfo=attributeInfoArray;

            }
            if($scope.paramListHolder[i].paramType =='attributes'){
              var attributeInfoArray=[];
              for(var j=0;j<$scope.paramListHolder[i].attributeInfoTag.length;j++){
                var attributeInfo={};
                var attributeInfoRef={}
                attributeInfoRef.type=$scope.paramListHolder[i].selectedParamValueType;
                attributeInfoRef.uuid=$scope.paramListHolder[i].attributeInfoTag[j].uuid
               // attributeInfoRef.name=$scope.paramListHolder[i].attributeInfoTag[j].datapodname
                attributeInfo.ref=attributeInfoRef;
                attributeInfo.attrId=$scope.paramListHolder[i].attributeInfoTag[j].attributeId;
             ///   attributeInfo.attrName=$scope.paramListHolder[i].attributeInfoTag[j].name;
                attributeInfoArray[j]=attributeInfo
              }
              paramList.attributeInfo=attributeInfoArray;
            }
            else if($scope.paramListHolder[i].paramType=='distribution' || $scope.paramListHolder[i].paramType=='datapod'){
              var ref={};
              var paramValue={};  
              ref.type=$scope.paramListHolder[i].selectedParamValueType;
              ref.uuid=$scope.paramListHolder[i].selectedParamValue.uuid;  
              paramValue.ref=ref;
              paramList.paramValue=paramValue;
            }
            else if($scope.paramListHolder[i].selectedParamValueType =="simple"){
              var ref={};
              var paramValue={};  
              ref.type=$scope.paramListHolder[i].selectedParamValueType;
              paramValue.ref=ref;
              paramValue.value=$scope.paramListHolder[i].paramValue
              paramList.paramValue=paramValue; 
            }
            else if($scope.paramListHolder[i].selectedParamValueType =="list"){
              var ref={};
              var paramValue={};  
              ref.type='simple';
              paramValue.ref=ref;
              paramValue.value=$scope.paramListHolder[i].paramValue
              paramList.paramValue=paramValue;
            }
           
            paramListInfo[i]=paramList;
          }
          var EXEC_PARAMS={};
          EXEC_PARAMS.paramListInfo = paramListInfo;
          execParams.EXEC_PARAMS=EXEC_PARAMS;
          //execParams.paramListInfo=paramListInfo;
        }
        else{
          execParams=null;
        }
        console.log(JSON.stringify(execParams))
        $scope.popupModel.modelData.operators[0].operatorParams=execParams;
      }
    },//End of link Fn
    templateUrl: 'views/jointgraph.html',
  };
});
