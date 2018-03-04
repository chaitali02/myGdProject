JobMonitoringModule= angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDqExecController', function($filter,$state,$stateParams,$rootScope,$scope,$sessionStorage,JobMonitoringService,sortFactory,dagMetaDataService) {

    $scope.uuid=$stateParams.id;
    $scope.mode=$stateParams.mode;
    $scope.showdqexec=true;
    $scope.selectTitle=dagMetaDataService.elementDefs['dqexec'].caption;
    $scope.state=dagMetaDataService.elementDefs['dqexec'].listState+"({type:'"+dagMetaDataService.elementDefs['dqexec'].execType+"'})"

    $scope.close = function () {
        if($stateParams.returnBack == "true" && $rootScope.previousState){
            //revertback
            $state.go($rootScope.previousState.name,$rootScope.previousState.params);
       }
       else{
           $scope.statedetail={};
           $scope.statedetail.name=dagMetaDataService.elementDefs['dqexec'].listState
           $scope.statedetail.params={}
           $scope.statedetail.params.type=dagMetaDataService.elementDefs['dqexec'].execType;
           $state.go($scope.statedetail.name,$scope.statedetail.params)
        }
    }
    JobMonitoringService.getLatestByUuid($scope.uuid,"dqexec").then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
        $scope.dqexecdata=response;
        var statusList=[];
        for(i=0;i<response.statusList.length;i++){
            d=$filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
            d = d.toString().replace("+0530","IST");
            statusList[i]=response.statusList[i].stage+"-"+d;
        }

        $scope.statusList=statusList
        var refkeylist=[];
        if(response.refKeyList !=null){
        	for(i=0;i<response.refKeyList.length;i++){
        		refkeylist[i]=response.refKeyList[i].type+"-"+response.refKeyList[i].name;

        	}
        }
        $scope.refkeylist=refkeylist

    }

    $scope.showLoadGraph=function(uuid,version){
    	$scope.showdqexec=false;
    	$scope.showgraph=false
    	$scope.graphDatastatusList=true
    	$scope.showgraphdiv=true;

    }

    $scope.showDqExecPage=function(){
    	$scope.showdqexec=true
    	$scope.showgraph=false
    	$scope.graphDatastatusList=false
    	$scope.showgraphdiv=false;
    }


});
