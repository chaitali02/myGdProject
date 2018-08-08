
DatavisualizationModule=angular.module('DatavisualizationModule')

DatavisualizationModule.controller('DashboradMenuController2',function($filter,$rootScope,$scope,$sessionStorage,$state,DahsboardSerivce,CommonService,dagMetaDataService,FileSaver,Blob,privilegeSvc) {
  $scope.isListCard=false;
  $scope.IsVizpodDetailShow=false;
	$scope.optionsort=[
		{"caption":"Name A-Z",name:"name"},
		{"caption":"Name Z-A",name:"-name"},
		{"caption":"Date Asc",name:"createdOn"},
		{"caption":"Date Desc",name:"-createdOn"},
		]
  $scope.optiondata={"caption":"Name A-Z","name":"name"};
  $scope.$on('$stateChangeStart',function (event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName=fromState.name
    $sessionStorage.fromParams=fromParams

  });

	var notify = {
		type: 'info',
		title: 'Info',
		content: 'Dashboard is In-active. Please restore',
		timeout: 3000 //time in ms
};

$scope.privileges = [];
$scope.privileges = privilegeSvc.privileges["dashboard"] || [];

$scope.$on('privilegesUpdated', function (e, data) {
$scope.privileges = privilegeSvc.privileges["dashboard"] || [];
  
});
	$scope.pagination={
    currentPage:1,
    pageSize:10,
    paginationPageSizes:[10, 25, 50, 75, 100],
    maxSize:5,
  }

  $scope.getGridStyle = function() {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
    }
    if ($scope.filteredRows && $scope.filteredRows.length > 0) {
      style['height'] = (($scope.filteredRows.length < 10 ? $scope.filteredRows.length * 40 : 400) + 40) + 'px';
    } else {
      style['height'] = "100px"
    }
    return style;
  }
  $scope.gridOptions = angular.copy(dagMetaDataService.gridOptionsDefault);
	$scope.gridOptions.columnDefs.push({
		displayName: 'Status',
		name: 'active',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.active == "Y" ? "Active" : "In Active"}}</div>'
	},{
		displayName: 'Publish',
		name: 'publish',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		cellTemplate: '<div class="ui-grid-cell-contents">{{row.entity.published == "Y" ? "Yes" : "No"}}</div>'
	},{
		displayName: 'Action',
		name: 'action',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
    maxWidth: 150,
    // cellTemplate: [
      
    //   '<div class="ui-grid-cell-contents"><a class="btn btn-xs btn-primary" name="execbutton"  ng-click="grid.appScope.show_dashboard(row.entity)">View</a></div>',
    
    // ].join(''),
		cellTemplate: [
      
      '<div class="ui-grid-cell-contents">',
      '<div class="col-md-12" style="display:inline-flex;">',   
      '  <div class="col-md-10 dropdown" uib-dropdown dropdown-append-to-body>',
      '    <button class="btn green btn-xs btn-outline dropdown-toggle" uib-dropdown-toggle>Action',
      '    <i class="fa fa-angle-down"></i></button>',
      '    <ul uib-dropdown-menu class="dropdown-menu-grid">',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'View\') == -1"><a ng-click="grid.appScope.show_dashboard(row.entity)"><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Edit\') == -1" ><a ng-click="grid.appScope.editDashboard(row.entity)"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Delete\') == -1" ng-if="row.entity.active == \'Y\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Delete\')"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Restore\') == -1" ng-if="row.entity.active == \'N\'"><a ng-click="grid.appScope.deleteOrRestore(row.entity,\'Restore\')"><i class="fa fa-retweet" aria-hidden="true"></i>  Restore</a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Publish\') == -1" ng-if="row.entity.published == \'N\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Publish\')"><i class="fa fa-share-alt" aria-hidden="true"></i>  Publish</a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Unpublish\') == -1 || row.entity.createdBy.ref.name != grid.appScope.loginUser" ng-if="row.entity.published == \'Y\'"><a ng-click="grid.appScope.publishOrUnpublish(row.entity,\'Unpublish\')"><i class="fa fa-shield" aria-hidden="true"></i>  Unpublish</a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Clone\') == -1"><a ng-click="grid.appScope.createCopy(row.entity)"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>',
      '    <li ng-disabled="grid.appScope.privileges.indexOf(\'Export\') == -1"><a ng-click="grid.appScope.export(row.entity)"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>',
      '    </ul>',
      '  </div>',
      '</div>'
    
    ].join('')
	});
	$scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };
 
  $scope.refreshData = function(searchtext) {
    $scope.gridOptions.data = $filter('filter')($scope.originalData,searchtext, undefined);
  }

  $scope.$watchCollection('switchStatus', function() {
     $scope.isListCard=!$scope.isListCard;
  });

  $scope.addMode=function(){
    
  }
  $scope.selectdashboard = function(response) {
    $scope.selectedmodeldata = true;
    $scope.gridOptions.data=null;
    $scope.gridOptions.data = response.data;
    $scope.originalData = response.data;
  }

	
  $scope.showIcon=function(index){
    $scope.alldashboard[index].isIconShow=true;
  }

  $scope.hideIcon=function(index){
   $scope.alldashboard[index].isIconShow=false;
  }

  $scope.show_dashboard = function(data){
		if(data.active=='Y'){
      setTimeout(function(){  $state.go("showdashboard",{'id':data.uuid,'mode':'false'});},100);
	  }
		else{
      notify.content="Dashboard is In-active. Please restore"
      notify.type='info',
  		notify.title= 'Info',
			$scope.$emit('notify', notify);
		}
  }
  $scope.editDashboard=function(data){
    setTimeout(function(){  $state.go("metaListdashboard",{'id':data.uuid,'mode':'false'});},100);
  }
  
  $scope.createCopy = function (data) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = {};
    $scope.obj.uuid = uuid;
    $scope.obj.version = version;
    $scope.msg="Clone"
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  $scope.export = function (data) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj = {};
    $scope.obj.uuid = uuid;
    $scope.obj.version = version;
    $scope.msg="Export"
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }
  $scope.deleteOrRestore = function (data,action) {
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj =data;
   
    $scope.msg=action;
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }

  $scope.publishOrUnpublish = function (data,action){
    var uuid = data.uuid;
    var version = data.version;
    $scope.obj =data;
    $scope.msg=action;
    $('#confModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  }


  $scope.submitOk=function(action){
    if(action =="Clone"){
      $scope.okClone();
    }
   else if(action =="Export"){
      $scope.okExport();
    }
    else if(action =="Delete"){
      $scope.okDelete();
    }
    else if(action =="Restore"){
      $scope.okDelete();
    }else if(action =="Publish"){
      $scope.okPublished();
    }
    else if(action =="Unpublish"){
      $scope.okPublished();
    }
  }

  $scope.okClone = function () {
    $('#confModal').modal('hide');
    CommonService.getSaveAS($scope.obj.uuid, $scope.obj.version,"dashboard").then(function (response) {onSuccessSaveAs(response.data)});
    var onSuccessSaveAs = function (response) {
      $scope.originalData.splice(0, 0, response);
      $scope.message ="Dashboard Cloned Successfully"
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }


  $scope.okExport = function () {
    $('#confModal').modal('hide');
    CommonService.getLatestByUuid($scope.obj.uuid,"dashboard").then(function (response) {
      onSuccessGetUuid(response.data)
    });
    var onSuccessGetUuid = function (response) {
      var jsonobj = angular.toJson(response, true);
      var data = new Blob([jsonobj], {
        type: 'application/json;charset=utf-8'
      });
      FileSaver.saveAs(data, response.name + '.json');
      $scope.message ="Dashboard Downloaded Successfully";
      notify.type = 'success',
      notify.title = 'Success',
      notify.content = $scope.message
      $scope.$emit('notify', notify);
    }
  }

  $scope.okDelete=function () {
    $('#DeleteConfModal').modal('hide');
    $('#confModal').modal('hide');
    if($scope.obj.active=='Y'){
			CommonService.delete($scope.obj.id,'dashboard').then(function(response){OnSuccessDelete(response.data)});
			var OnSuccessDelete=function(response){
       $scope.alldashboard[$scope.obj.index].active=response.active;
       if($scope.gridOptions.data && $scope.gridOptions.data.length >0)
        $scope.gridOptions.data[$scope.obj.index].active=response.active;
       notify.type='success',
   		 notify.title= 'Success',
       notify.content="Dashboard Deleted Successfully"
       $scope.$emit('notify', notify);
			}
	  }
		else{
			CommonService.restore($scope.obj.id,'dashboard').then(function(response){OnSuccessDelete(response.data)});
			var OnSuccessDelete=function(response){
        $scope.alldashboard[$scope.obj.index].active='Y'
        if($scope.gridOptions.data && $scope.gridOptions.data.length >0)
          $scope.gridOptions.data[$scope.obj.index].active="Y"
        notify.type='success',
        notify.title= 'Success',
       notify.content="Dashboard Restored Successfully"
       $scope.$emit('notify', notify);
			}
		}

  }
  $scope.metadashboard = function($event,index,data){
    $event.stopPropagation();
    $scope.dashboarddatadelete=data;
    $scope.obj=data;
    if(data.active=='Y'){
      $scope.deletemsg="Delete Dashboard"
    }
    else {
      $scope.deletemsg="Restore Dashboard "
    }
    $('#DeleteConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });

  }

  
  

  $scope.okPublished = function () {
 
    $('#confModal').modal('hide');
    if($scope.obj.published=='N'){
			CommonService.publish($scope.obj.id,'dashboard').then(function(response){OnSuccessPublush(response.data)});
			var OnSuccessPublush=function(response){
       $scope.alldashboard[$scope.obj.index].published=response.published;
       if($scope.gridOptions.data && $scope.gridOptions.data.length >0)
        $scope.gridOptions.data[$scope.obj.index].published=response.published;
       notify.type='success',
   		 notify.title= 'Success',
       notify.content="Dashboard Publish Successfully"
       $scope.$emit('notify', notify);
			}
	  }
		else{
			CommonService.unpublish($scope.obj.id,'dashboard').then(function(response){OnSuccessUnpublush(response.data)});
			var OnSuccessUnpublush=function(response){
        $scope.alldashboard[$scope.obj.index].published='N'
        if($scope.gridOptions.data && $scope.gridOptions.data.length >0)
          $scope.gridOptions.data[$scope.obj.index].published="N"
        notify.type='success',
        notify.title= 'Success',
        notify.content="Dashboard Unpublish Successfully"
        $scope.$emit('notify', notify);
			}
		}
  }
   
  

	DahsboardSerivce.getAllLatestCompleteObjects("dashboard").then(function(response){onSuccessGetAllLatestCompleteObjects(response.data)});
	var onSuccessGetAllLatestCompleteObjects=function(response){
		if(response.length >0){
			var dashbardarray=[];
			var colorarray=["blue","green","purple"];
      var chartclassarray=["fa fa-bar-chart","fa fa-area-chart","fa fa-pie-chart"]
			var count=0;
			for(var i=0;i<response.length;i++){
				var dashbardjson={};
				/*if(count <=3){
					if(count == 3){
						count=0;
					}
				 dashbardjson.class=colorarray[count];
        // dashbardjson.chartclass=chartclassarray[count]
				 count=count+1;
				}//End If*/
        var randomno = Math.floor((Math.random() *3) + 0);
        dashbardjson.class="green";//colorarray[randomno];
        var randomno1 = Math.floor((Math.random() *3) + 0);
				dashbardjson.tooltip="Delete";
        dashbardjson.chartclass=chartclassarray[randomno1]
				dashbardjson.color='#'+Math.random().toString(16).substr(2,6);
        dashbardjson.isIconShow=false;
				dashbardjson.uuid=response[i].uuid;
				dashbardjson.id=response[i].id;
        dashbardjson.index=i;
				dashbardjson.active=response[i].active;
        if(response[i].name.split('').length >20){
          dashbardjson.name=response[i].name.substring(0, 20)+"..";
        }
				else{
          dashbardjson.name=response[i].name;
        }
        dashbardjson.title=response[i].name;
				dashbardjson.desc=response[i].desc;
				dashbardjson.createdOn=new Date(response[i].createdOn.split("IST")[0]).toLocaleDateString('en-US')//response[i].createdOn
				dashbardjson.sectionInfo=response[i].sectionInfo;
				dashbardarray[i]=dashbardjson;
			}//End For
		}//End If
		$scope.alldashboard=dashbardarray
	}//End  onSuccessGetAllLatest

});//End DashboradMenuController



//Start ShowDashboradController
DatavisualizationModule.controller('ShowDashboradController2',function($location,$http,$filter,dagMetaDataService,$window,$timeout,$rootScope,$scope,$state,$stateParams,$q,NgTableParams,$sessionStorage,DahsboardSerivce) {
  $scope.showmap=true;
  $scope.isApplyFilter=true
  $scope.datax=[];
  $scope.datacolumns=[];
  $scope.datapoints=[];
  $scope.datapointsgrid=[];
  $scope.vizpoddetail=[]
  $scope.vizpodtypedetail={};
  $scope.vizpodtypedetail1=[];
  $scope.tabledata=[];
  var count=0;
  $scope.showdashboard=true;
  $scope.showgraph=false
  $scope.showgraphdiv=false
  $scope.graphDataStatus=false
  $scope.showtooltip='top'
  $scope.showtooltiptitle="Maximize";
  $scope.inprogressarray=[];
  $scope.chartcolor=["#d98880","#f1948a","#c39bd3","#bb8fce","#7fb3d5","#85c1e9","#76d7c4","#73c6b6","#7dcea0","#82e0aa","#f7dc6f","#f8c471","#f0b27a","#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
  $scope.vizpodbody={};
  $scope.filterListarray=[];
  $scope.hideIcon=true
  $scope.sectionRows=[];
  $scope.pagination={
    currentPage:1,
    pageSize:10,
    paginationPageSizes:[10, 25, 50, 75, 100],
    maxSize:5,
  }
  $scope.gridOptions = {
    rowHeight: 40,
    useExternalPagination: true,
    exporterMenuPdf: true,
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
  $scope.filteredRows = [];
  $scope.getGridStyleDetail = function() {
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

  $window.addEventListener('resize', function(e) {
    $scope.showmap=false
    $timeout(function() {
       $scope.showmap=true;
      },100);
  });
  
  $scope.Preparedatagrid=function (i,j) {
    $scope.sectionRows[i].columns[j].filteredRows=[];
    $scope.sectionRows[i].columns[j].gridOptions={
    rowHeight: 40,
    enableGridMenu: true,
    useExternalPagination: true,
    exporterMenuPdf: true,
    exporterPdfOrientation: 'landscape',
    exporterPdfPageSize: 'A4',
    exporterPdfDefaultStyle: {fontSize: 9},
    exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},

  }

  $scope.sectionRows[i].columns[j].gridOptions.onRegisterApi = function(gridApi){
      $scope.gridApi = gridApi;
      $scope.sectionRows[i].columns[j].filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
    };
  }
  $scope.getGridStyle = function(data) {
    var style = {
      'margin-top': '10px',
      'margin-bottom': '10px',
      'max-height': '297px'
    }
    // if (data &&  data.length >0) {
    //     style['height'] = (( data.length < 10 ? data.length * 40 : 400) + 40) + 'px';
    //   }
    //  else{
    //     style['height']="100px";
    //   }
    return style;
  }

  $scope.$on('$stateChangeStart',function (event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName=fromState.name
    $sessionStorage.fromParams=fromParams

  });
  $scope.convertSectionInfo = function (sectionInfo) {
		if(!sectionInfo[0].rowNo){
			var row = 0;
			angular.forEach(sectionInfo,function (val,key) {
				if(key > 0 && key%2==0){
					row++;
				}
				if($scope.sectionRows[row])
					$scope.sectionRows[row].columns.push(val);
				else
					$scope.sectionRows[row] = { columns : [val] };

			});

		}
		else {

			angular.forEach(sectionInfo,function (val,key) {
				if($scope.sectionRows[val.rowNo-1]){
					$scope.sectionRows[val.rowNo-1].columns[val.colNo-1] = val
				}
				else {
					$scope.sectionRows[val.rowNo-1] = {columns:[val]}
				}
			})
		}
    //console.log(JSON.stringify($scope.sectionRows));
	}
  $scope.getColWidth = function (row) {
    var count = 0;
    angular.forEach(row.columns,function (val) {
      if(!val.fullWidth)
        count++;
    })
    return (count < 3 ? 12/(count||1) : '4')
  }
  $scope.showDashboardGraph=function(uuid,version){
    $scope.showdashboard=false;
    $scope.showgraph=false
    $scope.graphDataStatus=true
    $scope.showgraphdiv=true;
    $scope.vizpodtypedetail1=[];
  }//End showDashboardGraph


  $scope.showDashboardPage=function(){
    $scope.showdashboard=true;
    $scope.showgraph=false
    $scope.graphDataStatus=false;
    $scope.showgraphdiv=false
    $scope.vizpodtypedetail1=[];
  //  $scope.callGraph();
  }//End showDashboardPage


  $scope.fullscreen=function(){
    if($scope.showtooltip =='top'){
       $scope.showtooltip='bottom'
       $scope.showtooltiptitle="Minimize"
       $scope.hideIcon=false
    }
    else{
       $scope.showtooltip='top'
       $scope.showtooltiptitle="Maximize"
       $scope.hideIcon=true
    }
    $timeout(function() {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }

  $scope.fullScreenVizpod=function(parentIndex,index){
    if($scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass == 'fa fa-expand'){
      //$(".fullscreendashobard").addClass("portlet-fullscreen");
      for(var i=0;i<$scope.sectionRows.length;i++){
        for(var j=0;j<$scope.sectionRows[i].columns.length;j++){
          $scope.sectionRows[i].columns[j].vizpodDetails.show=false;
        }
      }
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.show=true;
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass='fa fa-compress';
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.showtooltiptitle="Minimize"
    }
    else{
      //  $(".fullscreendashobard").removeClass("portlet-fullscreen");
      for(var i=0;i<$scope.sectionRows.length;i++){
        for(var j=0;j<$scope.sectionRows[i].columns.length;j++){
          $scope.sectionRows[i].columns[j].vizpodDetails.show=true
        }
      }
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.iconclass='fa fa-expand';
      $scope.sectionRows[parentIndex].columns[index].vizpodDetails.showtooltiptitle="Maximize"

    }
    //$scope.fullscreen();

   $timeout(function() {
       $window.dispatchEvent(new Event("resize"));
     }, 100);
  }

  $scope.refreshDashboard=function(length){
    $scope.callGraph();
    $scope.isApplyFilter=true;
    $scope.selectedAttributeValue=[]
    for(var i=0;i<length;i++){
      var defaultvalue={}
      defaultvalue.id=null;
      defaultvalue.value="-select-"
      $scope.selectedAttributeValue[i]=defaultvalue
      $scope.filterAttribureIdValues[i].values.splice(0,1);
      $scope.filterAttribureIdValues[i].values.splice(0,0,defaultvalue);
    }


  }
  
  $scope.onChange=function(){
    $scope.isApplyFilter=false;
  }
  $scope.onFilterChange=function(index){
    // console.log(JSON.stringify($scope.filterAttribureIdValues[index].dname))
    // console.log(JSON.stringify($scope.selectedAttributeValue))
    var count=0;
    $scope.filterListarray=[];
     for(var i=0;i<$scope.selectedAttributeValue.length;i++){
      var filterList={};
      var ref={};
      if($scope.selectedAttributeValue[i].value !="-select-"){
        ref.type="datapod";
        ref.uuid=$scope.filterAttribureIdValues[i].datapoduuid
        filterList.ref=ref;
        filterList.attrId=$scope.filterAttribureIdValues[i].datapodattrId
        filterList.value="'"+$scope.selectedAttributeValue[i].value+"'";
        $scope.filterListarray[count]=filterList;
        count=count+1;
      }
    }
      console.log(JSON.stringify($scope.filterListarray));
      $scope.vizpodbody.filterInfo=$scope.filterListarray
      $scope.getVizpodResut($scope.vizpodbody);

  }

  $scope.getFilterValue=function(data){
    
    $scope.filterAttribureIdValues=[]
    $scope.selectedAttributeValue=[]
    if(data.filterInfo && data.filterInfo.length >0){
      var filterAttribureIdValue=[];
      for(var n=0;n<data.filterInfo.length;n++){
        var filterattributeidvalepromise=DahsboardSerivce.getAttributeValues(data.filterInfo[n].ref.uuid,data.filterInfo[n].attrId,data.filterInfo[n].ref.type);
        filterAttribureIdValue.push(filterattributeidvalepromise);
      }//End For Loop
      $q.all(filterAttribureIdValue).then(function(result){
        for(var i=0;i<result.length;i++){
          var filterAttribureIdvalueJSON={};
          var defaultvalue={}
          defaultvalue.id=null;
          defaultvalue.value="-select-"
          filterAttribureIdvalueJSON.vizpoduuid=
          filterAttribureIdvalueJSON.vizpodversion=data.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapoduuid=data.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapodattrId=data.filterInfo[i].attrId;
          filterAttribureIdvalueJSON.dname=data.filterInfo[i].ref.name+"."+data.filterInfo[i].attrName;
          filterAttribureIdvalueJSON.values=result[i].data
          filterAttribureIdvalueJSON.values.splice(0,0,defaultvalue)
          $scope.selectedAttributeValue[i]=defaultvalue

          $scope.filterAttribureIdValues[i]=filterAttribureIdvalueJSON
          console.log(JSON.stringify($scope.filterAttribureIdValues))
        }
      });//End $q.all
    }//End If

  }//End getFilterValue

  $scope.preparColumnData=function(){
    var colorcount=0;
    for(var i=0;i<$scope.sectionRows.length;i++){
      for(var j=0;j<$scope.sectionRows[i].columns.length;j++){
      var datax={}
      var vizpoddetailjson={};
      vizpoddetailjson.id="chart"+$scope.sectionRows[i].columns[j].vizpodInfo.id+i+j
      vizpoddetailjson.uuid=$scope.sectionRows[i].columns[j].vizpodInfo.uuid
			vizpoddetailjson.version=$scope.sectionRows[i].columns[j].vizpodInfo.version
      vizpoddetailjson.name=$scope.sectionRows[i].columns[j].vizpodInfo.name;
      vizpoddetailjson.type=$scope.sectionRows[i].columns[j].vizpodInfo.type;
      vizpoddetailjson.class="";
      vizpoddetailjson.iconclass="fa fa-expand";
      vizpoddetailjson.showtooltiptitle="Maximize";
      vizpoddetailjson.show=true;
      $scope.sectionRows[i].columns[j].vizpodDetails=vizpoddetailjson
      datax.id=$scope.sectionRows[i].columns[j].vizpodInfo.keys[0].attributeName//x value
      $scope.sectionRows[i].columns[j].vizpodDetails.datax=datax;
      var datacolumnsarray=[];
      for(var k=0;k<$scope.sectionRows[i].columns[j].vizpodInfo.values.length;k++){
        var datacolumnsjson={};
        if($scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.type =="datapod"){
          datacolumnsjson.id=$scope.sectionRows[i].columns[j].vizpodInfo.values[k].attributeName;
          datacolumnsjson.name=$scope.sectionRows[i].columns[j].vizpodInfo.values[k].attributeName;
        }//End If Inside For
        else{
          datacolumnsjson.id=$scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.name+"";
          datacolumnsjson.name=$scope.sectionRows[i].columns[j].vizpodInfo.values[k].ref.name+"";
        }
        datacolumnsjson.type=$scope.sectionRows[i].columns[j].vizpodInfo.type.split("-")[0];
        if(colorcount <=16){
          if(colorcount == 16){
            colorcount=0;
          }
          datacolumnsjson.color=$scope.chartcolor[colorcount];
          colorcount=colorcount+1;
        }//End If
        datacolumnsarray[k]=datacolumnsjson
        $scope.sectionRows[i].columns[j].vizpodDetails.datacolumns=datacolumnsarray
        //console.log(JSON.stringify($scope.sectionRows[i].columns[j].vizpodDetails.datacolumns));
      }//End For K

        if($scope.sectionRows[i].columns[j].vizpodInfo.type=="data-grid"){
          var keyvalueData=null;
          $scope.sectionRows[i].columns[j].gridOptions={}
          $scope.Preparedatagrid(i,j);
          $scope.sectionRows[i].columns[j].gridOptions.columnDefs=[];
          if($scope.sectionRows[i].columns[j].vizpodInfo.groups.length >0){
            keyvalueData = $scope.sectionRows[i].columns[j].vizpodInfo.keys.concat($scope.sectionRows[i].columns[j].vizpodInfo.values,$scope.sectionRows[i].columns[j].vizpodInfo.groups);
          }//End Inner IF
          else{
            keyvalueData = $scope.sectionRows[i].columns[j].vizpodInfo.keys.concat($scope.sectionRows[i].columns[j].vizpodInfo.values);
          }//End Innder Else
          console.log(JSON.stringify(keyvalueData))
          for(var c=0;c<keyvalueData.length;c++){
            var attribute={};
            if(keyvalueData[c].ref.type =="datapod"){
              attribute.name=keyvalueData[c].attributeName;
              attribute.displayName=keyvalueData[c].attributeName;
            //attribute.width =$scope.keyvalueData[c].attributeName.split('').length + 2 + "%"
            }
            else{
              attribute.name=keyvalueData[c].ref.name;
              attribute.displayName=keyvalueData[c].ref.name;
              //attribute.width=$scope.keyvalueData[c].ref.name;
            }

          $scope.sectionRows[i].columns[j].gridOptions.columnDefs.push(attribute)
          }//End C loop
        }//End Else
    }
   }//End For I
   //console.log("data_grid"+JSON.stringify($scope.sectionRows));
  }//End preparColumnData

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

  $scope.filterSearchVizpodDetail =function (s) {

    $scope.gridOptions.data = $filter('filter')($scope.orignalData, s, undefined);
    //$scope.getResults(data)
  }
  $scope.backVizpodSummary=function(){
    $scope.IsVizpodDetailShow=!$scope.IsVizpodDetailShow;
    $timeout(function() {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }
  $scope.selectData=function(data){
    var menu = [{
      title: 'Show Detail',
      action:$scope.actionEvent,
      disabled: false // optional, defaults to false
    }];
    console.log(data);
   $scope.contextMenu1(menu,data);
  }
  $scope.showClick = function(data){
    var menu = [{
      title: 'Show Detail',
      action:$scope.actionEvent,
      disabled: false // optional, defaults to false
    }]
    console.log(data)
    $scope.contextMenu1(menu,data);
  }
  $scope.actionEvent=function(d,i,data){
   debugger
    var filterinfoArray=[]
    var vizpodbody={}
    var filterInfo={};
    var ref={}
    if(data.dataobj.value !=""){
      ref.uuid=data.vizpod.vizpodInfo.values[0].ref.uuid;
      ref.version=null;
      ref.type=data.vizpod.vizpodInfo.values[0].ref.type
      filterInfo.ref=ref;
      filterInfo.attrId=data.vizpod.vizpodInfo.values[0].attributeId;
      for(var i=0;i<data.vizpod.vizpodInfo.values.length;i++){
        var atttrName=data.vizpod.vizpodInfo.values[i].attributeName;
       if(atttrName.indexOf(data.dataobj.id) !=-1){
        filterInfo.attrId=data.vizpod.vizpodInfo.values[i].attributeId;
        break;
       }  
      }
      filterInfo.value=data.dataobj.value
      filterinfoArray.push(filterInfo);
      vizpodbody.filterInfo=filterinfoArray
    }
    else{
      vizpodbody=null;  
    }
    $scope.IsVizpodDetailShow=true;
    $scope.inprogressdatavizpodetail=true;
    $scope.VizpodDetail=data.vizpod;
    DahsboardSerivce.getVizpodDetails(data.vizpod.vizpodInfo.uuid,data.vizpod.vizpodInfo.version,vizpodbody).then(function(response){onSuccessGetVizpodDetails(response.data)},function(response){onError(response.data)});
    var onSuccessGetVizpodDetails=function(response){
      $scope.isDataErrorvizpodetail=false;
      $scope.inprogressdatavizpodetail=false;
      var columns = [];
      $scope.orignalData=response;
      if(response.length && response.length > 0){
        angular.forEach(response[0],function (val,key) {
          if(key!='rownum'){
          var w=key.split('').length + 2 + "%"
            columns.push({"name":key,"displayName":key.toLowerCase(), width:w+"%"});}
          });
      }
      $scope.gridOptions.columnDefs = columns;
      console.log(response);
      $scope.getResults(response);
      //$scope.gridOptions.data=response;
    }//End onSuccessLatestByUuid 
    var onError=function(){
      $scope.isDataErrorvizpodetail=true;
      $scope.inprogressdatavizpodetail=false;
    }

  }

  $scope.contextMenu1=function(menu,vizpodbody){
       
    d3.select(".jitu").selectAll('.context-menu').data([1])
        .enter()
        .append('div')
        .attr('class', 'context-menu')

    // close menu
    d3.select('body').on('click.context-menu', function() {
        d3.selectAll('.context-menu').style('display', 'none');
    });
   
  
    var elm = this;
    d3.selectAll('.context-menu')
        .html('')
        .append('ul')
        .selectAll('li')
        .data(menu).enter()
        .append('li')
        .html(function(d) {
      return d.title;
    })
    .on('click', function(d,index) {
        d.action(elm,d,vizpodbody);
        d3.selectAll('.context-menu').style('display', 'none');
    })
    //console.log(event)
    // show the context menu
    d3.selectAll('.context-menu')
      .style('left', (d3.event["pageX"] -2) + 'px')
      .style('top', (d3.event["pageY"] - 30) + 'px')
      .style('display', 'block');
      d3.event.preventDefault();
    
};

  $scope.getVizpodResut=function(data){
    $scope.isDataError=false;
    $scope.vizpodResutsArray=[];
    $scope.vizpodtrack=[]
     $scope.isUserNotification=true;
     $scope.inprogressdata=true
    for(var i=0;i<$scope.dashboarddata.sectionInfo.length;i++){
      var vizpodResuts={};
      if($scope.dashboarddata.sectionInfo[i].vizpodInfo.type !='network-graph'){
      var vizpodresultpromise=DahsboardSerivce.getVizpodResults($scope.dashboarddata.sectionInfo[i].vizpodInfo.uuid,$scope.dashboarddata.sectionInfo[i].vizpodInfo.version,data);
      vizpodResuts.rowNo=$scope.dashboarddata.sectionInfo[i].rowNo-1;
      vizpodResuts.colNo=$scope.dashboarddata.sectionInfo[i].colNo-1;
      vizpodResuts.type=$scope.dashboarddata.sectionInfo[i].vizpodInfo.type;
      $scope.vizpodtrack.push(vizpodResuts);
      $scope.vizpodResutsArray.push(vizpodresultpromise);
      }
      else{
        console.log("network Graph");
        $scope.inprogressdata=false
      }
    }
    console.log($scope.vizpodResutsArray);
   
    $q.all($scope.vizpodResutsArray).then(function(result){

      for(var k=0;k<$scope.vizpodtrack.length;k++){
       
        if($scope.vizpodtrack[k] !="network-graph"){
        $scope.inprogressdata=false
        $scope.isUserNotification=false;
        $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].isDataError=false;
        $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].errormsg="";
        $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].vizpodDetails.datapoints=result[k].data;
        if($scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].vizpodDetails.type =="data-grid"){
          $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].gridOptions.data=result[k].data;
        }
      }
      }
      $scope.preparColumnDataFromResult();
    },function(response){
    
        for(var k=0;k<$scope.vizpodtrack.length;k++){
          $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].isDataError=true;
          $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].vizpodDetails.datapoints=[];
          $scope.sectionRows[$scope.vizpodtrack[k].rowNo].columns[$scope.vizpodtrack[k].colNo].errormsg="Some Error Occurred";
        }
       $scope.inprogressdata=false;
       //$scope.isDataError=true;
       //$scope.datamessage="Some Error Occurred";

    });

  }

  $scope.preparColumnDataFromResult=function(){
    for(var i=0;i<$scope.sectionRows.length;i++){
      for(var j=0;j<$scope.sectionRows[i].columns.length;j++){
        if($scope.sectionRows[i].columns[j].vizpodInfo.type =='pie-chart' ||$scope.sectionRows[i].columns[j].vizpodInfo.type =='donut-chart'){
          var columnname=$scope.sectionRows[i].columns[j].vizpodInfo.keys[0].attributeName
          if($scope.sectionRows[i].columns[j].vizpodInfo.values[0].ref.type == "datapod"){
            columnnamevalue=$scope.sectionRows[i].columns[j].vizpodInfo.values[0].attributeName
          }
          else{
            columnnamevalue=$scope.sectionRows[i].columns[j].vizpodInfo.values[0].ref.name
          }
          var columnarray=[]
          var dataarray=[]
          var colorcount=0;
          for(var k=0;k<$scope.sectionRows[i].columns[j].vizpodDetails.datapoints.length;k++){
            var datacolumnsjson={};
            var datajson={};
            datacolumnsjson.id=$scope.sectionRows[i].columns[j].vizpodDetails.datapoints[k][columnname]+"";
            datacolumnsjson.ref="jitu"
            datacolumnsjson.type=$scope.sectionRows[i].columns[j].vizpodDetails.type.split("-")[0];
            if(colorcount <=15){
              if(colorcount == 15){
                colorcount=0;
              }
              datacolumnsjson.color=$scope.chartcolor[colorcount];
              colorcount=colorcount+1;
            }//End If
            datajson[$scope.sectionRows[i].columns[j].vizpodDetails.datapoints[k][columnname]]=$scope.sectionRows[i].columns[j].vizpodDetails.datapoints[k][columnnamevalue];
            columnarray[k]=datacolumnsjson;
            dataarray[k]=datajson;
            console.log(JSON.stringify(columnarray));
          }

            console.log(JSON.stringify(dataarray));
          $scope.sectionRows[i].columns[j].vizpodDetails.datax="";
          $scope.sectionRows[i].columns[j].vizpodDetails.datapoints=dataarray;
          $scope.sectionRows[i].columns[j].vizpodDetails.datacolumns=columnarray;

        }
      }
    }
  //  console.log(JSON.stringify($scope.sectionRows));
  }

  $scope.callGraph=function(){
    $scope.vizpodtypedetail1=[];
    count=0;
    DahsboardSerivce.getLatestByUuidView($stateParams.id,"dashboardview").then(function(response){onSuccessLatestByUuid(response.data)});
    var onSuccessLatestByUuid=function(response){
      
		  $scope.dashboarddata=response;
      $scope.convertSectionInfo(response.sectionInfo)
      $scope.uuid = response.uuid;
      $scope.version = response.version;
      $scope.getFilterValue($scope.dashboarddata)// Method call for populate filter dropdown data;
      $scope.preparColumnData();
      $scope.getVizpodResut(null);
	  }//End onSuccessLatestByUuid
  }//End Call Graph()
  
  $scope.downloadFile = function(data) {
    
    var uuid = data.uuid;
    var version=data.version;
    var url=$location.absUrl().split("app")[0]
    $http({
      method: 'GET',
      url:url+"vizpod/download?action=view&vizpodUUID="+uuid+"&vizpodVersion="+version,
      responseType: 'arraybuffer'
    }).success(function(data, status, headers) {
      headers = headers();
    
      var filename = headers['x-filename'];
      var contentType = headers['content-type'];
    
      var linkElement = document.createElement('a');
      try {
      var blob = new Blob([data], {
        type: contentType
      });
      var url = window.URL.createObjectURL(blob);
    
      linkElement.setAttribute('href', url);
      linkElement.setAttribute("download", uuid+".xls");
    
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
    };
  $scope.callGraph();
})//End ShowDashboradController
