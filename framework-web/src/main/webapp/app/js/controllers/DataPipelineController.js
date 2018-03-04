/**
**/
  DataPipelineModule= angular.module('DataPipelineModule');



DataPipelineModule.controller('WorkflowResultListController', function($rootScope,$scope,$state,$stateParams,MetadataDagSerivce,$filter) {

	// MetadataDagSerivce.getAllLatest("dag").then(function(response){onSuccessDatapod(response.data)});
	// var onSuccessDatapod=function(response){
	//       $scope.dagAll=response;
  //       if($stateParams.dagid){
  //         angular.forEach($scope.dagAll,function (val) {
  //           if(val.uuid == $stateParams.dagid){
  //             $scope.selecteddagdata = val;
  //             $scope.selectDag();
  //           }
  //         })
  //       }
	// }

	// $scope.selectDag=function(){
  //   $scope.$broadcast('removeGraph');
	// 	MetadataDagSerivce.getDagExecByDag($scope.selecteddagdata.uuid,"dagexec").then(function(response){onSuccessGetDagExecByDag(response.data)});
	// 	var onSuccessGetDagExecByDag=function(response){
	// 		$scope.dagExec=response;
	// 	}
	// }
  $scope.fullscreen = false;
  $scope.requestFullscreenResult = function() {

    if($scope.fullscreen){
      $("#dag-exec-model").removeAttr('style');
      $scope.hideothers = false;
    }
    else {
      $scope.hideothers = true;
      // $("#dag-exec-model").css(
      //     {
      //       "z-index": "9999",
      //       "width": "100%",
      //       "height": "100%",
      //       "position": "fixed",
      //       "overflow" : "scroll",
      //       "top": "0",
      //       "left": "0",
      //    }
      //   );
    }
    $scope.fullscreen = !$scope.fullscreen;
  };
	$scope.getDagExec=function(data){
		var uuid=data.split(",")[1]
	  var version=data.split(",")[2]
    $state.go('resultgraphwf',{id:uuid,version:version,type:'dagexec',mode:true,dagid:uuid});
	}
  $scope.changeData=function(data){
    $scope.dagExec = data.data;
  }
  /*$scope.$on('searchCriteriaChanged',function (e,data) {
    $scope.dagExec = data.data;
  });
*/
})
DataPipelineModule.controller('WorkflowResultController', function($location,$http,$rootScope,$scope,$state,$stateParams,MetadataDagSerivce) {
  $scope.resultsExec = true;
  $scope.fullscreen = false;
  $rootScope.showGrid=false;
  $rootScope.showGroupDowne=false;
  $scope.allowReExecution = false;
  $scope.uuid = $stateParams.id;
  $scope.version = $stateParams.version;
  $scope.versionexcutionDag = $stateParams.version;
  var count=0;
  $scope.showgraphdiv=false;
  var notify = {
      type: 'success',
      title: 'Success',
      timeout: 3000 //time in ms
  };
  $scope.close = function () {
    if($scope.showgraphdiv)
      $scope.showgraphdiv = !$scope.showgraphdiv;
    else if($scope.daggroupExecName || $scope.resultExecName){
      $scope.$broadcast('closeSubTabs');
    }
    else
		  $state.go('resultwf',{dagid:$stateParams.dagid});
	}

  $scope.reExecute = function(data) {
    $('#DagConfExModal').modal({
      backdrop: 'static',
      keyboard: false
    });
  } //End excutionDag

  $scope.ok = function() {
    $('#DagConfExModal').modal('hide');
    $scope.executionmsg = "Pipeline Restarted Successfully"
    notify.type='success',
    notify.title= 'Success',
    notify.content=$scope.executionmsg
    $rootScope.$emit('notify', notify);
    //  $('#executionsubmit').modal({
    //    backdrop: 'static',
    //   keyboard: false
    // });
  var url=$location.absUrl().split("app")[0];
     //$http.post(url+'dag/execute?uuid='+$scope.uuid+'&version='+$scope.version+'&type=dagexec').then(function (response) {
    $http.post(url+'dag/restart?uuid='+$scope.uuid+'&version='+$scope.version+'&type=dagexec&action=execute').then(function (response) {

      console.log(response);
      $scope.getDagExec();
    })
  };

  $scope.$on('refreshData',function (e,data) {
    if(!$scope.showgraphdiv && !($scope.daggroupExecName || $scope.resultExecName)){
      $scope.getDagExec();
    }
  });
  $scope.$on('daggroupExecChanged',function (e,groupExecName) {
    $scope.daggroupExecName = groupExecName;
  })
  $scope.$on('resultExecChanged',function (e,resultExecName) {
    $scope.resultExecName = resultExecName;
  })
  $scope.requestFullscreenResult = function() {

    if($scope.fullscreen){
      $("#dag-exec-model").removeAttr('style');
      $scope.hideothers = false;
    }
    else {
      $scope.hideothers = true;
      // $("#dag-exec-model").css(
      //     {
      //       "z-index": "9999",
      //       "width": "100%",
      //       "height": "100%",
      //       "position": "fixed",
      //       "overflow" : "scroll",
      //       "top": "0",
      //       "left": "0",
      //    }
      //   );
    }
    $scope.fullscreen = !$scope.fullscreen;
  };
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
  function startStatusUpdate(uuid) {
    var Fn = function () {
      MetadataDagSerivce.getStatusByDagExec(uuid).then(function(response){onSuccessGetStatusByDagExec(response.data)});
      var onSuccessGetStatusByDagExec=function(response){
        //$scope.dagdataexec=response;
        if(latestStatus(response.status).stage == 'Failed'){
          $scope.allowReExecution = true;
        }
        if(['Completed','Failed','Killed'].indexOf(latestStatus(response.status).stage) > -1){
          stopStatusUpdate();
        }
        console.log(response);
        if(['InProgress'].indexOf(latestStatus(response.status).stage) > -1){
         if(count == 0){count=count+1};
       }
       if(count ==1){
          $scope.getDagExec();
          count=count+1;
        }
        else{
        if(!angular.equals(statusCache, response)){
          $scope.$broadcast('updateGraphStatus',response);
          statusCache = response;
        }
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
    // $scope.$broadcast('removeGraph');
    statusCache = undefined;
    if(intervalId)
    clearInterval(intervalId);
  }
	$scope.getDagExec=function(){
    stopStatusUpdate();
    var uuid = $stateParams.id;
		// MetadataDagSerivce.getLatestDagByUuidDagExec(uuid).then(function(response){onSuccessGetDagByDagExec(response.data)});
		MetadataDagSerivce.getOneByUuidAndVersion(uuid,$stateParams.version,$stateParams.type).then(function(response){onSuccessGetDagByDagExec(response.data)});
		var onSuccessGetDagByDagExec=function(response){
			//$scope.dagdataexec=response;
      console.log(response);
      $scope.dagExecName = response.dagdata.name;
			//console.log(JSON.stringify($scope.dagdataexec));
      $scope.$broadcast('createGraph',response.dagdata);
      startStatusUpdate(uuid);
		}

	}
  $scope.getDagExec();
  $scope.savePng=function (index,name) {
    let svg = document.querySelector('#paper').querySelector('svg');
    var w=document.querySelector('#paper').width
    saveSvgAsPng(svg, name+'.png',{"backgroundColor":"white","left":"-200","top":"-200","width":"2000"});
  }
  $scope.downloadPiplineFile=function(){
    window.downloadPiplineFile();
  }
})
