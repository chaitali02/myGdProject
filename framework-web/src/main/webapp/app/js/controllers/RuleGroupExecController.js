JobMonitoringModule= angular.module('JobMonitoringModule');

JobMonitoringModule.controller('DetailRuleGroupExecController', function( $filter,$state,$stateParams,$rootScope,$scope,$sessionStorage,JobMonitoringService,sortFactory,dagMetaDataService,privilegeSvc) {

    $scope.uuid=$stateParams.id;
    $scope.mode=$stateParams.mode;
    $scope.showExec=true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.selectTitle=dagMetaDataService.elementDefs['rulegroupexec'].caption;
    $scope.state=dagMetaDataService.elementDefs['rulegroupexec'].listState+"({type:'"+dagMetaDataService.elementDefs['rulegroupexec'].execType+"'})"
    $scope.onShowDetail = function(data){
        $rootScope.previousState={};
        $rootScope.previousState.name=dagMetaDataService.elementDefs['rulegroupexec'].detailState;
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
    $rootScope.isCommentVeiwPrivlage=true;
    var privileges = privilegeSvc.privileges['comment'] || [];
    $rootScope.isCommentVeiwPrivlage =privileges.indexOf('View') == -1;
    $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;
    $scope.$on('privilegesUpdated', function (e, data) {
      var privileges = privilegeSvc.privileges['comment'] || [];
      $rootScope.isCommentVeiwPrivlage = privileges.indexOf('View') == -1;
      $rootScope.isCommentDisabled=$rootScope.isCommentVeiwPrivlage;	
    });
    $scope.userDetail={}
    $scope.userDetail.uuid= $rootScope.setUseruuid;
    $scope.userDetail.name= $rootScope.setUserName;
    $scope.close = function () {
        if($stateParams.returnBack == "true" && $rootScope.previousState){
            //revertback
            $state.go($rootScope.previousState.name,$rootScope.previousState.params);
            if($rootScope.previousState.name!="resultgraphwf"){
                var stateTabPrevious={};
                stateTabPrevious.route=dagMetaDataService.elementDefs['rulegroupexec'].detailState;;
                stateTabPrevious.param={};
                stateTabPrevious.param.id = $stateParams.id;
                stateTabPrevious.param.mode = true;
                stateTabPrevious.param.returnBack = true;
                stateTabPrevious.param.name = $scope.execData.name;
                stateTabPrevious.param.version = $scope.execData.version;
                stateTabPrevious.param.type = dagMetaDataService.elementDefs['rulegroupexec'].execType;
                stateTabPrevious.active=false;
                var stateTabNew={};
                stateTabNew.route=$rootScope.previousState.name;
                stateTabNew.param= $rootScope.previousState.params;
                stateTabNew.active=false;
                $rootScope.$broadcast('isTabAvailable',stateTabPrevious,stateTabNew);
              }
       }
       else{
           $scope.statedetail={};
           $scope.statedetail.name=dagMetaDataService.elementDefs['rulegroupexec'].listState
           $scope.statedetail.params={}
           $scope.statedetail.params.type=dagMetaDataService.elementDefs['rulegroupexec'].execType;
           $state.go($scope.statedetail.name,$scope.statedetail.params)
        }
    }

    $scope.menuOptions = [
        ['Show Details', function ($itemScope) {
              $scope.onShowDetail($itemScope.item)
        }],
        //null,

    ];
    JobMonitoringService.getLatestByUuid($scope.uuid,"rulegroupexec")
        .then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
    var onSuccess = function (response) {
        $scope.isEditInprogess=false;    
        $scope.execData=response;
        var statusList=[];
        for(i=0;i<response.statusList.length;i++){
        	d=$filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
            d = d.toString().replace("+0530","IST");
            statusList[i]=response.statusList[i].stage+"-"+d;
       	}

        $scope.statusList=statusList
        var execList=[];
        	for(i=0;i<response.execList.length;i++){
                var ruleexec={};
                ruleexec.type=response.execList[i].ref.type;
                ruleexec.name=response.execList[i].ref.name;
                ruleexec.uuid=response.execList[i].ref.uuid;
                execList[i]=ruleexec;
        	}
           $scope.execList=execList;
           var dependsOnlist = [];
           if (response.dependsOn != null) {
               var dependsOn = {};
               dependsOn.type = response.dependsOn.ref.type;
               dependsOn.name = response.dependsOn.ref.type + "-"+response.dependsOn.ref.name;
               dependsOn.uuid = response.dependsOn.ref.uuid;
               dependsOn.version = response.dependsOn.ref.version;
               dependsOnlist[0] = dependsOn;
           }
           $scope.dependsOnlist=dependsOnlist;
    };
    var onError=function(){
        $scope.isEditInprogess=false;
        $scope.isEditVeiwError=true;
    }
    $scope.onShowDetailDepOn=function(data){
        $rootScope.previousState = {};
        $rootScope.previousState.name = dagMetaDataService.elementDefs['rulegroupexec'].detailState;
        $rootScope.previousState.params = {};
        $rootScope.previousState.params.id = $stateParams.id;
        $rootScope.previousState.params.mode = true;
        var type = data.type
        var uuid = data.uuid
        var stageName = dagMetaDataService.elementDefs[type.toLowerCase()].detailState;
        var stageparam = {};
        stageparam.id = uuid;
        stageparam.version = data.version;
        stageparam.mode = true;
        stageparam.returnBack = true;
        $state.go(stageName, stageparam);
    }

    $scope.showGraph=function(uuid,version){
    		$scope.showExec=false;
    		$scope.showGraphDiv=true;

    }

    $scope.showExecPage=function(){
    	$scope.showExec=true
    	$scope.showGraphDiv=false;
    }




});
