JobMonitoringModule= angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailDqGroupExecController', function($filter,$state,$stateParams,$rootScope,$scope,$sessionStorage,JobMonitoringService,sortFactory,dagMetaDataService) {

    $scope.uuid=$stateParams.id;
    $scope.mode=$stateParams.mode;
    $scope.showdqgroupexec=true;
    $scope.selectTitle=dagMetaDataService.elementDefs['dqgroupexec'].caption;
    $scope.state=dagMetaDataService.elementDefs['dqgroupexec'].listState+"({type:'"+dagMetaDataService.elementDefs['dqgroupexec'].execType+"'})"
    $scope.close = function () {
        if($stateParams.returnBack == "true" && $rootScope.previousState){
            //revertback
            $state.go($rootScope.previousState.name,$rootScope.previousState.params);
       }
       else{
           $scope.statedetail={};
           $scope.statedetail.name=dagMetaDataService.elementDefs['dqgroupexec'].listState
           $scope.statedetail.params={}
           $scope.statedetail.params.type=dagMetaDataService.elementDefs['dqgroupexec'].execType;
           $state.go($scope.statedetail.name,$scope.statedetail.params)
        }
    }
    $scope.onShowDetail = function(data){
        $rootScope.previousState={};
        $rootScope.previousState.name=dagMetaDataService.elementDefs['dqgroupexec'].detailState;
        $rootScope.previousState.params={};
        $rootScope.previousState.params.id=$stateParams.id;
        $rootScope.previousState.params.mode=true;
        var type=data.type
        var uuid= data.uuid
        var stageName=dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
        var stageparam={};
        stageparam.id=uuid;
        stageparam.mode=true;
        stageparam.returnBack=true;
        $state.go(stageName,stageparam);
    };

    $scope.menuOptions = [
        ['Show Details', function ($itemScope) {
              $scope.onShowDetail($itemScope.item)
        }],
        //null,

    ];
    JobMonitoringService.getLatestByUuid($scope.uuid,"dqgroupexec").then(function(response){onSuccess(response.data)});
    var onSuccess=function(response){
        $scope.dqgroupexecdata=response;
        var statusList=[];
        for(i=0;i<response.statusList.length;i++){
          var statusListjson={}
        	d=$filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
          d = d.toString().replace("+0530","IST");
          statusListjson.id=i+2;
          statusListjson.text=response.statusList[i].stage+"-"+d;
          statusList[i]=statusListjson
        }
        console.log(JSON.stringify(statusList));
        $scope.statusList=statusList
        var execList=[];
        for(i=0;i<response.execList.length;i++){
        	var pdataqualexec={};
            pdataqualexec.type=response.execList[i].ref.type;
            pdataqualexec.name=response.execList[i].ref.name;
            pdataqualexec.uuid=response.execList[i].ref.uuid;
            execList[i]=pdataqualexec;
        }
        $scope.execList=execList
    }

    $scope.showLoadGraph=function(uuid,version){
    	$scope.showdqgroupexec=false;
    	$scope.showgraph=false
    	$scope.graphDatastatusList=true
    	$scope.showgraphdiv=true;

    }
    $scope.showDqGroupExecPage=function(){
    	$scope.showdqgroupexec=true
    	$scope.showgraph=false
    	$scope.graphDatastatusList=false
    	$scope.showgraphdiv=false;
    }
    $scope.showDqExecPage=function(){
        $scope.showdqexec=true
        $scope.showgraph=false
        $scope.graphDatastatusList=false
        $scope.showgraphdiv=false;
    }
    $scope.searchObjet = function(JSONObject,key,value){
        for(var i=0;i<JSONObject.length;i++){
            if(JSONObject[i][key] == value){
                return true;
            }
        }
        return false;
    }

    $scope.searcLinkObjet = function(JSONObject,valuedst,valuesrc){
       for(var i=0;i<JSONObject.length;i++){
            if(JSONObject[i].dst == valuedst && JSONObject[i].src == valuesrc ){
                return true;
            }
        }
        return false;
    }

    /* Start getNodeData*/
    $scope.getNodeData=function(data){
        $scope.graphDatastatusList=true;
        $scope.showgraph=false;
        DqGroupExecService.getGraphData(data.uuid,data.version,"1").then(function (response) {onSuccess(response.data)});
        var onSuccess=function(response){
            $scope.graphDatastatusList=false;
            $scope.showgraph=true;
            console.log(JSON.stringify(response))
            var nodelen=$scope.graphdata.nodes.length;
            var linklen=$scope.graphdata.links.length;
            for(var j=0;j<$scope.graphdata.nodes.length;j++){
                delete $scope.graphdata.nodes[j].weight
                delete $scope.graphdata.nodes[j].index
                delete $scope.graphdata.nodes[j].x
                delete $scope.graphdata.nodes[j].y
                delete $scope.graphdata.nodes[j].px
                delete $scope.graphdata.nodes[j].py
            }
            var countnode=0;
            var countlink=0;
            for(var i=0;i<response.nodes.length;i++){
                if($scope.searchObjet($scope.graphdata.nodes,"id",response.nodes[i].id) !=true){
                  $scope.graphdata.nodes[nodelen+countnode]=response.nodes[i];
                  countnode=countnode+1;
                }
            }
            for(var j=0;j<response.links.length;j++){
                if($scope.searchObjet($scope.graphdata.links,response.links[j].dst,response.links[j].src) !=true){
                $scope.graphdata.links[linklen+countlink]=response.links[j];
                countlink=countlink+1;
                }
            }
            console.log(JSON.stringify($scope.graphdata))
            $scope.data=$scope.graphdata
            /* $scope.$apply(function(){}); */
        }
    }/* End getNodeData*/


});
