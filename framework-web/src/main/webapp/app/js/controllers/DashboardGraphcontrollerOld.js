
DatavisualizationModule=angular.module('DatavisualizationModule')

DatavisualizationModule.controller('DashboradMenuController',function($filter,$rootScope,$scope,$sessionStorage,$state,DahsboardSerivce,CommonService,dagMetaDataService) {
  $scope.isListCard=false;
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
		displayName: 'Action',
		name: 'action',
		cellClass: 'text-center',
		headerCellClass: 'text-center',
		maxWidth: 100,
		cellTemplate: [
			'<div class="ui-grid-cell-contents"><a class="btn btn-xs btn-primary" name="execbutton"  ng-click="grid.appScope.show_dashboard(row.entity)">View</a></div>',
		].join('')
	});
	$scope.gridOptions.onRegisterApi = function(gridApi) {
    $scope.gridApi = gridApi;
    $scope.filteredRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid);
  };

  $scope.refreshData = function(searchtext) {
    //var data
    $scope.gridOptions.data = $filter('filter')($scope.originalData,searchtext, undefined);
  //  $scope.getResults(data);
  };
  $scope.$watchCollection('switchStatus', function() {
     $scope.isListCard=!$scope.isListCard;
  });
  // $scope.listAndCardShow=function (data) {
  // 	$scope.isListCard=!$scope.isListCard;
  // }
	$scope.selectdashboard = function(response) {
    $scope.selectedmodeldata = true;
      $scope.gridOptions.data=null;
    $scope.gridOptions.data = response.data;
    $scope.originalData = response.data;
		// if($scope.originalData.length >0){
		// 	$scope.getResults($scope.originalData);
		// }
  }

	// $scope.selectPage = function(pageNo) {
	// 	$scope.pagination.currentPage = pageNo;
	// };
	// $scope.onPerPageChange = function() {
	// 		$scope.pagination.currentPage = 1;
	// 	$scope.getResults($scope.originalData)
	// }
	// $scope.pageChanged = function() {
	// 	$scope.getResults($scope.originalData)
	// };
	// $scope.getResults = function(params) {
	// 	$scope.pagination.totalItems=params.length;
	// 	$scope.pagination.to = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize);
	// 	if ($scope.pagination.totalItems < ($scope.pagination.pageSize*$scope.pagination.currentPage)) {
	// 		$scope.pagination.from = $scope.pagination.totalItems;
	// 	} else {
	// 		$scope.pagination.from = (($scope.pagination.currentPage) * $scope.pagination.pageSize);
	// 	}
	// 	var limit = ($scope.pagination.pageSize*$scope.pagination.currentPage);
	// 	var offset = (($scope.pagination.currentPage - 1) * $scope.pagination.pageSize)
	// 	 $scope.gridOptions.data=params.slice(offset,limit);
	// }
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
  $scope.okDelete=function () {

    $('#DeleteConfModal').modal('hide');
    if($scope.dashboarddatadelete.active=='Y'){
			CommonService.delete($scope.dashboarddatadelete.id,'dashboard').then(function(response){OnSuccessDelete(response.data)});
			var OnSuccessDelete=function(response){
			 $scope.alldashboard[$scope.dashboarddatadelete.index].active=response.active;
       notify.type='success',
   		 notify.title= 'Success',
       notify.content="Dashboard Deleted Successfully"
       $scope.$emit('notify', notify);
			}
	  }
		else{
			CommonService.restore($scope.dashboarddatadelete.id,'dashboard').then(function(response){OnSuccessDelete(response.data)});
			var OnSuccessDelete=function(response){
			 $scope.alldashboard[$scope.dashboarddatadelete.index].active='Y'
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
    //$scope.dashboarddatadelete.index=index
    if(data.active=='Y'){
      $scope.deletemsg="Delete Dashboard"
    }
    else {
      $scope.deletemsg="Restore Dashboard"
    }
    $('#DeleteConfModal').modal({
      backdrop: 'static',
      keyboard: false
    });

    //setTimeout(function(){  $state.go("metaListdashboard",{'id':uuid,'mode':'false'});},100);

  }

	DahsboardSerivce.getAllLatest("dashboard").then(function(response){onSuccessGetAllLatest(response.data)});
	var onSuccessGetAllLatest=function(response){
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
        dashbardjson.class=colorarray[randomno];
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
		//$scope.alldashboard=response;
	}//End  onSuccessGetAllLatest

});//End DashboradMenuController



//Start ShowDashboradController
DatavisualizationModule.controller('ShowDashboradController',function($window,$timeout,$rootScope,$scope,$state,$stateParams,$q,NgTableParams,$sessionStorage,DahsboardSerivce) {

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
  $scope.RUNNINGarray=[];
  $scope.chartcolor=["#d98880","#f1948a","#c39bd3","#bb8fce","#7fb3d5","#85c1e9","#76d7c4","#73c6b6","#7dcea0","#82e0aa","#f7dc6f","#f8c471","#f0b27a","#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
  $scope.vizpodbody={};
  $scope.filterListarray=[];
  $scope.hideIcon=true
  $scope.$on('$stateChangeStart',function (event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName=fromState.name
    $sessionStorage.fromParams=fromParams

  });

  $scope.showDashboardGraph=function(uuid,version){
    $scope.showdashboard=false;
    $scope.showgraph=false
    $scope.graphDataStatus=true
    $scope.showgraphdiv=true;
   /* var newUuid=uuid+"_"+version;
    DahsboardSerivce.getGraphData(newUuid,version,"1")
      .then(function (result) {
        $scope.graphDataStatus=false;
        $scope.showgraph=true;
        console.log(JSON.stringify(result.data))
        $scope.dashpbardgraphdata=result.data;
        $scope.graphdata=result.data;
    });*/
    $scope.vizpodtypedetail1=[];
  }//End showDashboardGraph


  $scope.showDashboardPage=function(){
    $scope.showdashboard=true;
    $scope.showgraph=false
    $scope.graphDataStatus=false;
    $scope.showgraphdiv=false
    $scope.vizpodtypedetail1=[];
    $scope.callGraph();
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

  $scope.fullScreenVizpod=function(index,type){

    if($scope.showtooltip =='top'){
       $scope.showtooltip='bottom'
       $scope.showtooltiptitle="Minimize"
       $scope.hideIcon=true
       for(var i=0;i<$scope.vizpoddetail.length;i++){
        $scope.vizpoddetail[i].show=false;
         $scope.vizpoddetail[i].class="default-vizpod-size"

       }
        for(var i=0;i<$scope.vizpoddetailgride.length;i++){
        $scope.vizpoddetailgride[i].show=false;
         $scope.vizpoddetailgride[i].class="default-vizpod-size"

       }
      if(type =="data-grid"){
        $scope.vizpoddetailgride[index].show=true;
        $scope.vizpoddetailgride[index].class="fullscree-vizpod-size";
        $scope.vizpoddetailgride[index].iconclass="fa fa-compress"
      }
      else{
       $scope.vizpoddetail[index].show=true;
       $scope.vizpoddetail[index].class="fullscree-vizpod-size";
       $scope.vizpoddetail[index].iconclass="fa fa-compress"

      }

    }
    else{

       $scope.showtooltip='top'
       $scope.showtooltiptitle="Maximize"
       $scope.hideIcon=true
       for(var i=0;i<$scope.vizpoddetail.length;i++){
        $scope.vizpoddetail[i].show=true;
        $scope.vizpoddetail[i].class=""
        $scope.vizpoddetail[i].iconclass="fa fa-expand"
       }
       for(var i=0;i<$scope.vizpoddetailgride.length;i++){
        $scope.vizpoddetailgride[i].show=true;
        $scope.vizpoddetailgride[i].class="";
        $scope.vizpoddetailgride[i].iconclass="fa fa-expand"
       }
    }

   $timeout(function() {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }

  $scope.refreshDashboard=function(length){
    $scope.callGraph();
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

  $scope.onFilterChange=function(index){
    //alert(index)
    console.log(JSON.stringify($scope.filterAttribureIdValues[index].dname))
    console.log(JSON.stringify($scope.selectedAttributeValue))
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
    if(data.dashboarddata.filterInfo.length >0){
      var filterAttribureIdValue=[];
      for(var n=0;n<data.dashboarddata.filterInfo.length;n++){
        var filterattributeidvalepromise=DahsboardSerivce.getAttributeValues(data.dashboarddata.filterInfo[n].ref.uuid,data.dashboarddata.filterInfo[n].attrId);
        filterAttribureIdValue.push(filterattributeidvalepromise);
      }//End For Loop
      $q.all(filterAttribureIdValue).then(function(result){
        for(var i=0;i<result.length;i++){
        //  console.log(JSON.stringify(result[i].data));
          var filterAttribureIdvalueJSON={};
          var defaultvalue={}
          defaultvalue.id=null;
          defaultvalue.value="-select-"
          filterAttribureIdvalueJSON.vizpoduuid=
          filterAttribureIdvalueJSON.vizpodversion=data.dashboarddata.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapoduuid=data.dashboarddata.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapodattrId=data.dashboarddata.filterInfo[i].attrId;
          filterAttribureIdvalueJSON.dname=data.dashboarddata.filterInfo[i].ref.name+"."+data.dashboarddata.filterInfo[i].attrName;
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
    var vizpodgridcount=0
    $scope.vizpoddetailgride=[];
    for(var i=0;i<$scope.dashboarddata.vizpod.length;i++){
      var datax={}
      var vizpoddetailjson={};
      vizpoddetailjson.id="chart"+i
      vizpoddetailjson.uuid=$scope.dashboarddata.vizpod[i].uuid
			vizpoddetailjson.version=$scope.dashboarddata.vizpod[i].version
      vizpoddetailjson.name=$scope.dashboarddata.vizpod[i].name;
      vizpoddetailjson.type=$scope.dashboarddata.vizpod[i].type;
      vizpoddetailjson.class="";
      vizpoddetailjson.iconclass="fa fa-expand"
      vizpoddetailjson.show=true;
      $scope.vizpoddetail[i]=vizpoddetailjson
      datax.id=$scope.dashboarddata.vizpod[i].keys[0].attributeName//x value
      $scope.datax[i]=datax
      var datacolumnsarray=[];
      if($scope.dashboarddata.vizpod[i].type !="donut-chart" && $scope.dashboarddata.vizpod[i].type !="pie-chart" && $scope.dashboarddata.vizpod[i].type !="data-grid"){
        for(var j=0;j<$scope.dashboarddata.vizpod[i].values.length;j++){
          var datacolumnsjson={};

          if($scope.dashboarddata.vizpod[i].values[j].ref.type =="datapod"){
            datacolumnsjson.id=$scope.dashboarddata.vizpod[i].values[j].attributeName;
            datacolumnsjson.name=$scope.dashboarddata.vizpod[i].values[j].attributeName;
          }//End If Inside For

          else{
            datacolumnsjson.id=$scope.dashboarddata.vizpod[i].values[j].ref.name+"";
            datacolumnsjson.name=$scope.dashboarddata.vizpod[i].values[j].ref.name+"";
          }
          datacolumnsjson.type=$scope.dashboarddata.vizpod[i].type.split("-")[0];
          //datacolumnsjson.color='#'+Math.random().toString(16).substr(2,6);
          if(colorcount <=16 && $scope.dashboarddata.vizpod[i].type != "scatter-chart"){
            if(colorcount == 16){
              colorcount=0;
            }
            datacolumnsjson.color=$scope.chartcolor[colorcount];
            colorcount=colorcount+1;
          }//End If
          // var randomno = Math.floor((Math.random() *15) + 0);
          //datacolumnsjson.color=$scope.chartcolor[randomno];
          datacolumnsarray[j]=datacolumnsjson
          $scope.datacolumns[i]=datacolumnsarray
        }//End For J
      }//End If
      else{
        var vizpodtypedetail={}
        vizpodtypedetail.type=$scope.dashboarddata.vizpod[i].type;
        vizpodtypedetail.index=i
        $scope.vizpodtypedetail1[count]=vizpodtypedetail;
        count=count+1;
        if($scope.dashboarddata.vizpod[i].type =="data-grid"){
          var vizpoddetailjson={};
          vizpoddetailjson.uuid=$scope.dashboarddata.vizpod[i].uuid;
					vizpoddetailjson.version=$scope.dashboarddata.vizpod[i].version;
          vizpoddetailjson.name=$scope.dashboarddata.vizpod[i].name;
          vizpoddetailjson.type=$scope.dashboarddata.vizpod[i].type;
          vizpoddetailjson.class="";
          vizpoddetailjson.iconclass="fa fa-expand"
          vizpoddetailjson.show=true;
          $scope.vizpoddetailgride[vizpodgridcount]=vizpoddetailjson
          vizpodgridcount=vizpodgridcount+1;
        }
      }
    }//End For I
  }//End preparColumnData

  $scope.getVizpodResut=function(data){
    $scope.isDataError=false;
    $scope.vizpodResutsArray=[];
     $scope.isUserNotification=true;
     $scope.RUNNINGdata=true
    for(var i=0;i<$scope.dashboarddata.vizpod.length;i++){
      var vizpodresultpromise=DahsboardSerivce.getVizpodResults($scope.dashboarddata.vizpod[i].uuid,$scope.dashboarddata.vizpod[i].version,data);
      $scope.vizpodResutsArray.push(vizpodresultpromise);
    }
    $q.all($scope.vizpodResutsArray).then(function(result){
      for(var i=0;i<result.length;i++){
        //$scope.RUNNINGarray[i]=false;
        $scope.RUNNINGdata=false
        $scope.isUserNotification=false;
        $scope.datapoints[i]=result[i].data;
      }
      $scope.preparColumnDataFromResult();
    },function(response){

       $scope.RUNNINGdata=false;
       $scope.isDataError=true;
       $scope.datamessage="Some Error Occurred";

    });
  }

  $scope.preparColumnDataFromResult=function(){
    var count=0;
    var countgrid=0;
    if($scope.vizpodtypedetail1.length >0){
      for(var i=0;i<$scope.vizpodtypedetail1.length;i++){
        var index=$scope.vizpodtypedetail1[i].index
        if($scope.vizpodtypedetail1[i].type == "pie-chart" || $scope.vizpodtypedetail1[i].type == "donut-chart"){
          $scope.datax[index]=""
          var datacolumnsarray=[];
          for(var k=0;k<$scope.dashboarddata.vizpod[index].values.length;k++){
            var columnname=$scope.dashboarddata.vizpod[index].keys[0].attributeName
            var columnnamevalue;
            if($scope.dashboarddata.vizpod[index].values[0].ref.type == "datapod"){
              columnnamevalue=$scope.dashboarddata.vizpod[index].values[0].attributeName
            }
            else{
              columnnamevalue=$scope.dashboarddata.vizpod[index].values[0].ref.name
            }

            var dataarray=[];
            var colorcount=0;
            for(var l=0;l<$scope.datapoints[index].length;l++){
              var datacolumnsjson={};
              var datajson={};
              datacolumnsjson.id=$scope.datapoints[index][l][columnname]+"";
              datacolumnsjson.type=$scope.dashboarddata.vizpod[index].type.split("-")[0];
              //datacolumnsjson.color='#'+Math.random().toString(16).substr(2,6);
              if(colorcount <=15){
                if(colorcount == 15){
                  colorcount=0;
                }
                datacolumnsjson.color=$scope.chartcolor[colorcount];
                colorcount=colorcount+1;
              }//End If
              datacolumnsarray[l]=datacolumnsjson
              datajson[$scope.datapoints[index][l][columnname]]=$scope.datapoints[index][l][columnnamevalue];
              dataarray[l]=datajson
            }//End Loop L

            if($scope.datacolumns.length >0){
              $scope.datapoints.splice(index,1)
              $scope.datapoints.splice(index,0,dataarray)
              $scope.datacolumns.splice(index, 1);
              $scope.datacolumns.splice(index, 0, datacolumnsarray);

            }
            else{
              $scope.datapoints.splice(count,1)
              $scope.datapoints.splice(count,0,dataarray)
              $scope.datacolumns.splice(count, 0, datacolumnsarray);
              count=count+1;
            }
           console.log(JSON.stringify( $scope.datacolumns))
           console.log(JSON.stringify( $scope.datapoints))
          }//End Loop K
        }//End If
        else{
          if($scope.dashboarddata.vizpod[index].groups.length >0){
            $scope.keyvalueData = $scope.dashboarddata.vizpod[index].keys.concat($scope.dashboarddata.vizpod[index].values,$scope.dashboarddata.vizpod[index].groups);
          }//End Inner IF
          else{
            $scope.keyvalueData = $scope.dashboarddata.vizpod[index].keys.concat($scope.dashboarddata.vizpod[index].values);
          }//End Innder Else
          console.log(JSON.stringify($scope.keyvalueData))
          $scope.cols=[];
          for(var c=0;c<$scope.keyvalueData.length;c++){
            var attribute={};
            if($scope.keyvalueData[c].ref.type =="datapod"){
              attribute.field=$scope.keyvalueData[c].attributeName;
              attribute.title=$scope.keyvalueData[c].attributeName;
              attribute.sortable=$scope.keyvalueData[c].attributeName;
            }
            else{
              attribute.field=$scope.keyvalueData[c].ref.name;
              attribute.title=$scope.keyvalueData[c].ref.name;
              attribute.sortable=$scope.keyvalueData[c].ref.name;
            }
            attribute.show=true

            $scope.cols.push(attribute)
          }//End C loop
          $scope.datacolumns[index]=$scope.cols
          $scope.tabledata[countgrid]={}
          $scope.tabledata[countgrid].cols=$scope.cols
          $scope.tabledata[countgrid].tableParams= new NgTableParams({ count: 5 }, {counts: [5, 10, 20], dataset: $scope.datapoints[index]});
          countgrid=countgrid+1;
          //console.log(JSON.stringify($scope.cols))
        }//End Else
      }//End Loop I
    }//End If
  }

  $scope.callGraph=function(){
    $scope.vizpodtypedetail1=[];
    count=0;
    DahsboardSerivce.getLatestByUuid($stateParams.id,"dashboard").then(function(response){onSuccessLatestByUuid(response.data)});
    var onSuccessLatestByUuid=function(response){
		  $scope.dashboarddata=response;
      $scope.uuid = response.dashboarddata.uuid;
      $scope.version = response.dashboarddata.version;
      $scope.getFilterValue($scope.dashboarddata)// Method call for populate filter dropdown data;
      $scope.preparColumnData();
      $scope.getVizpodResut(null);
	  }//End onSuccessLatestByUuid
  }//End Call Graph()

  $scope.callGraph();
})//End ShowDashboradController
DatavisualizationModule.controller('ShowDashboradController2',function($window,$timeout,$rootScope,$scope,$state,$stateParams,$q,NgTableParams,$sessionStorage,DahsboardSerivce) {

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
  $scope.RUNNINGarray=[];
  $scope.chartcolor=["#d98880","#f1948a","#c39bd3","#bb8fce","#7fb3d5","#85c1e9","#76d7c4","#73c6b6","#7dcea0","#82e0aa","#f7dc6f","#f8c471","#f0b27a","#e59866"]//["#E6B0AA","#D7BDE2","#F5B7B1","#D2B4DE","#A9CCE3","#AED6F1","#A9CCE3","#A3E4D7","#A2D9CE","#A9DFBF","#ABEBC6","#F9E79F","#FAD7A0","#F5CBA7","#EDBB99"]
  $scope.vizpodbody={};
  $scope.filterListarray=[];
  $scope.hideIcon=true
  $scope.$on('$stateChangeStart',function (event, toState, toParams, fromState, fromParams) {
    console.log(fromParams)
    $sessionStorage.fromStateName=fromState.name
    $sessionStorage.fromParams=fromParams

  });

  $scope.showDashboardGraph=function(uuid,version){
    $scope.showdashboard=false;
    $scope.showgraph=false
    $scope.graphDataStatus=true
    $scope.showgraphdiv=true;
   /* var newUuid=uuid+"_"+version;
    DahsboardSerivce.getGraphData(newUuid,version,"1")
      .then(function (result) {
        $scope.graphDataStatus=false;
        $scope.showgraph=true;
        console.log(JSON.stringify(result.data))
        $scope.dashpbardgraphdata=result.data;
        $scope.graphdata=result.data;
    });*/
    $scope.vizpodtypedetail1=[];
  }//End showDashboardGraph


  $scope.showDashboardPage=function(){
    $scope.showdashboard=true;
    $scope.showgraph=false
    $scope.graphDataStatus=false;
    $scope.showgraphdiv=false
    $scope.vizpodtypedetail1=[];
    $scope.callGraph();
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

  $scope.fullScreenVizpod=function(index,type){

    if($scope.showtooltip =='top'){
       $scope.showtooltip='bottom'
       $scope.showtooltiptitle="Minimize"
       $scope.hideIcon=true
       for(var i=0;i<$scope.vizpoddetail.length;i++){
        $scope.vizpoddetail[i].show=false;
         $scope.vizpoddetail[i].class="default-vizpod-size"

       }
        for(var i=0;i<$scope.vizpoddetailgride.length;i++){
        $scope.vizpoddetailgride[i].show=false;
         $scope.vizpoddetailgride[i].class="default-vizpod-size"

       }
      if(type =="data-grid"){
        $scope.vizpoddetailgride[index].show=true;
        $scope.vizpoddetailgride[index].class="fullscree-vizpod-size";
        $scope.vizpoddetailgride[index].iconclass="fa fa-compress"
      }
      else{
       $scope.vizpoddetail[index].show=true;
       $scope.vizpoddetail[index].class="fullscree-vizpod-size";
       $scope.vizpoddetail[index].iconclass="fa fa-compress"

      }

    }
    else{

       $scope.showtooltip='top'
       $scope.showtooltiptitle="Maximize"
       $scope.hideIcon=true
       for(var i=0;i<$scope.vizpoddetail.length;i++){
        $scope.vizpoddetail[i].show=true;
        $scope.vizpoddetail[i].class=""
        $scope.vizpoddetail[i].iconclass="fa fa-expand"
       }
       for(var i=0;i<$scope.vizpoddetailgride.length;i++){
        $scope.vizpoddetailgride[i].show=true;
        $scope.vizpoddetailgride[i].class="";
        $scope.vizpoddetailgride[i].iconclass="fa fa-expand"
       }
    }

   $timeout(function() {
      $window.dispatchEvent(new Event("resize"));
    }, 100);
  }

  $scope.refreshDashboard=function(length){
    $scope.callGraph();
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

  $scope.onFilterChange=function(index){
    //alert(index)
    console.log(JSON.stringify($scope.filterAttribureIdValues[index].dname))
    console.log(JSON.stringify($scope.selectedAttributeValue))
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
    if(data.dashboarddata.filterInfo.length >0){
      var filterAttribureIdValue=[];
      for(var n=0;n<data.dashboarddata.filterInfo.length;n++){
        var filterattributeidvalepromise=DahsboardSerivce.getAttributeValues(data.dashboarddata.filterInfo[n].ref.uuid,data.dashboarddata.filterInfo[n].attrId);
        filterAttribureIdValue.push(filterattributeidvalepromise);
      }//End For Loop
      $q.all(filterAttribureIdValue).then(function(result){
        for(var i=0;i<result.length;i++){
        //  console.log(JSON.stringify(result[i].data));
          var filterAttribureIdvalueJSON={};
          var defaultvalue={}
          defaultvalue.id=null;
          defaultvalue.value="-select-"
          filterAttribureIdvalueJSON.vizpoduuid=
          filterAttribureIdvalueJSON.vizpodversion=data.dashboarddata.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapoduuid=data.dashboarddata.filterInfo[i].ref.uuid;
          filterAttribureIdvalueJSON.datapodattrId=data.dashboarddata.filterInfo[i].attrId;
          filterAttribureIdvalueJSON.dname=data.dashboarddata.filterInfo[i].ref.name+"."+data.dashboarddata.filterInfo[i].attrName;
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
    var vizpodgridcount=0
    $scope.vizpoddetailgride=[];
    for(var i=0;i<$scope.dashboarddata.vizpod.length;i++){
      var datax={}
      var vizpoddetailjson={};
      vizpoddetailjson.id="chart"+i
      vizpoddetailjson.uuid=$scope.dashboarddata.vizpod[i].uuid
			vizpoddetailjson.version=$scope.dashboarddata.vizpod[i].version
      vizpoddetailjson.name=$scope.dashboarddata.vizpod[i].name;
      vizpoddetailjson.type=$scope.dashboarddata.vizpod[i].type;
      vizpoddetailjson.class="";
      vizpoddetailjson.iconclass="fa fa-expand"
      vizpoddetailjson.show=true;
      $scope.vizpoddetail[i]=vizpoddetailjson
      datax.id=$scope.dashboarddata.vizpod[i].keys[0].attributeName//x value
      $scope.datax[i]=datax
      var datacolumnsarray=[];
      if($scope.dashboarddata.vizpod[i].type !="donut-chart" && $scope.dashboarddata.vizpod[i].type !="pie-chart" && $scope.dashboarddata.vizpod[i].type !="data-grid"){
        for(var j=0;j<$scope.dashboarddata.vizpod[i].values.length;j++){
          var datacolumnsjson={};

          if($scope.dashboarddata.vizpod[i].values[j].ref.type =="datapod"){
            datacolumnsjson.id=$scope.dashboarddata.vizpod[i].values[j].attributeName;
            datacolumnsjson.name=$scope.dashboarddata.vizpod[i].values[j].attributeName;
          }//End If Inside For

          else{
            datacolumnsjson.id=$scope.dashboarddata.vizpod[i].values[j].ref.name+"";
            datacolumnsjson.name=$scope.dashboarddata.vizpod[i].values[j].ref.name+"";
          }
          datacolumnsjson.type=$scope.dashboarddata.vizpod[i].type.split("-")[0];
          //datacolumnsjson.color='#'+Math.random().toString(16).substr(2,6);
          if(colorcount <=16 && $scope.dashboarddata.vizpod[i].type != "scatter-chart"){
            if(colorcount == 16){
              colorcount=0;
            }
            datacolumnsjson.color=$scope.chartcolor[colorcount];
            colorcount=colorcount+1;
          }//End If
          // var randomno = Math.floor((Math.random() *15) + 0);
          //datacolumnsjson.color=$scope.chartcolor[randomno];
          datacolumnsarray[j]=datacolumnsjson
          $scope.datacolumns[i]=datacolumnsarray
        }//End For J
      }//End If
      else{
        var vizpodtypedetail={}
        vizpodtypedetail.type=$scope.dashboarddata.vizpod[i].type;
        vizpodtypedetail.index=i
        $scope.vizpodtypedetail1[count]=vizpodtypedetail;
        count=count+1;
        if($scope.dashboarddata.vizpod[i].type =="data-grid"){
          var vizpoddetailjson={};
          vizpoddetailjson.uuid=$scope.dashboarddata.vizpod[i].uuid;
					vizpoddetailjson.version=$scope.dashboarddata.vizpod[i].version;
          vizpoddetailjson.name=$scope.dashboarddata.vizpod[i].name;
          vizpoddetailjson.type=$scope.dashboarddata.vizpod[i].type;
          vizpoddetailjson.class="";
          vizpoddetailjson.iconclass="fa fa-expand"
          vizpoddetailjson.show=true;
          $scope.vizpoddetailgride[vizpodgridcount]=vizpoddetailjson
          vizpodgridcount=vizpodgridcount+1;
        }
      }
    }//End For I
  }//End preparColumnData

  $scope.getVizpodResut=function(data){
    $scope.isDataError=false;
    $scope.vizpodResutsArray=[];
     $scope.isUserNotification=true;
     $scope.RUNNINGdata=true
    for(var i=0;i<$scope.dashboarddata.vizpod.length;i++){
      var vizpodresultpromise=DahsboardSerivce.getVizpodResults($scope.dashboarddata.vizpod[i].uuid,$scope.dashboarddata.vizpod[i].version,data);
      $scope.vizpodResutsArray.push(vizpodresultpromise);
    }
    $q.all($scope.vizpodResutsArray).then(function(result){
      for(var i=0;i<result.length;i++){
        //$scope.RUNNINGarray[i]=false;
        $scope.RUNNINGdata=false
        $scope.isUserNotification=false;
        $scope.datapoints[i]=result[i].data;
      }
      $scope.preparColumnDataFromResult();
    },function(response){

       $scope.RUNNINGdata=false;
       $scope.isDataError=true;
       $scope.datamessage="Some Error Occurred";

    });
  }

  $scope.preparColumnDataFromResult=function(){
    var count=0;
    var countgrid=0;
    if($scope.vizpodtypedetail1.length >0){
      for(var i=0;i<$scope.vizpodtypedetail1.length;i++){
        var index=$scope.vizpodtypedetail1[i].index
        if($scope.vizpodtypedetail1[i].type == "pie-chart" || $scope.vizpodtypedetail1[i].type == "donut-chart"){
          $scope.datax[index]=""
          var datacolumnsarray=[];
          for(var k=0;k<$scope.dashboarddata.vizpod[index].values.length;k++){
            var columnname=$scope.dashboarddata.vizpod[index].keys[0].attributeName
            var columnnamevalue;
            if($scope.dashboarddata.vizpod[index].values[0].ref.type == "datapod"){
              columnnamevalue=$scope.dashboarddata.vizpod[index].values[0].attributeName
            }
            else{
              columnnamevalue=$scope.dashboarddata.vizpod[index].values[0].ref.name
            }

            var dataarray=[];
            var colorcount=0;
            for(var l=0;l<$scope.datapoints[index].length;l++){
              var datacolumnsjson={};
              var datajson={};
              datacolumnsjson.id=$scope.datapoints[index][l][columnname]+"";
              datacolumnsjson.type=$scope.dashboarddata.vizpod[index].type.split("-")[0];
              //datacolumnsjson.color='#'+Math.random().toString(16).substr(2,6);
              if(colorcount <=15){
                if(colorcount == 15){
                  colorcount=0;
                }
                datacolumnsjson.color=$scope.chartcolor[colorcount];
                colorcount=colorcount+1;
              }//End If
              datacolumnsarray[l]=datacolumnsjson
              datajson[$scope.datapoints[index][l][columnname]]=$scope.datapoints[index][l][columnnamevalue];
              dataarray[l]=datajson
            }//End Loop L

            if($scope.datacolumns.length >0){
              $scope.datapoints.splice(index,1)
              $scope.datapoints.splice(index,0,dataarray)
              $scope.datacolumns.splice(index, 1);
              $scope.datacolumns.splice(index, 0, datacolumnsarray);

            }
            else{
              $scope.datapoints.splice(count,1)
              $scope.datapoints.splice(count,0,dataarray)
              $scope.datacolumns.splice(count, 0, datacolumnsarray);
              count=count+1;
            }
           console.log(JSON.stringify( $scope.datacolumns))
           console.log(JSON.stringify( $scope.datapoints))
          }//End Loop K
        }//End If
        else{
          if($scope.dashboarddata.vizpod[index].groups.length >0){
            $scope.keyvalueData = $scope.dashboarddata.vizpod[index].keys.concat($scope.dashboarddata.vizpod[index].values,$scope.dashboarddata.vizpod[index].groups);
          }//End Inner IF
          else{
            $scope.keyvalueData = $scope.dashboarddata.vizpod[index].keys.concat($scope.dashboarddata.vizpod[index].values);
          }//End Innder Else
          console.log(JSON.stringify($scope.keyvalueData))
          $scope.cols=[];
          for(var c=0;c<$scope.keyvalueData.length;c++){
            var attribute={};
            if($scope.keyvalueData[c].ref.type =="datapod"){
              attribute.field=$scope.keyvalueData[c].attributeName;
              attribute.title=$scope.keyvalueData[c].attributeName;
              attribute.sortable=$scope.keyvalueData[c].attributeName;
            }
            else{
              attribute.field=$scope.keyvalueData[c].ref.name;
              attribute.title=$scope.keyvalueData[c].ref.name;
              attribute.sortable=$scope.keyvalueData[c].ref.name;
            }
            attribute.show=true

            $scope.cols.push(attribute)
          }//End C loop
          $scope.datacolumns[index]=$scope.cols
          $scope.tabledata[countgrid]={}
          $scope.tabledata[countgrid].cols=$scope.cols
          $scope.tabledata[countgrid].tableParams= new NgTableParams({ count: 5 }, {counts: [5, 10, 20], dataset: $scope.datapoints[index]});
          countgrid=countgrid+1;
          //console.log(JSON.stringify($scope.cols))
        }//End Else
      }//End Loop I
    }//End If
  }

  $scope.callGraph=function(){
    $scope.vizpodtypedetail1=[];
    count=0;
    DahsboardSerivce.getLatestByUuid($stateParams.id,"dashboard").then(function(response){onSuccessLatestByUuid(response.data)});
    var onSuccessLatestByUuid=function(response){
		  $scope.dashboarddata=response;
      $scope.uuid = response.dashboarddata.uuid;
      $scope.version = response.dashboarddata.version;
      $scope.getFilterValue($scope.dashboarddata)// Method call for populate filter dropdown data;
      $scope.preparColumnData();
      $scope.getVizpodResut(null);
	  }//End onSuccessLatestByUuid
  }//End Call Graph()

  $scope.callGraph();
})//End ShowDashboradController
