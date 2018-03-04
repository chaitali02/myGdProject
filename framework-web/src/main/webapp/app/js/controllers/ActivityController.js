/****/
AdminModule= angular.module('AdminModule');
AdminModule.controller('AdminActivityController', function($state,$stateParams,$rootScope,$scope,AdminActivityService,privilegeSvc) {

	$scope.activity={};
	$scope.activity.versions=[];
	$scope.showactivity=true;
	$scope.showgraphdiv=false;
  $scope.isDependencyShow=false;
	if($stateParams.mode =='true'){
	$scope.isEdit=false;
	$scope.isversionEnable=false;
	$scope.isAdd=false;
	}
	else if($stateParams.mode =='false'){
	$scope.isEdit=true;
	$scope.isversionEnable=true;
	$scope.isAdd=false;
	}
	else{
	$scope.isAdd=true;
	}
	$scope.privileges = [];
	$scope.privileges = privilegeSvc.privileges['activty'] || [];
	$scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	$scope.$on('privilegesUpdated',function (e,data) {
	  $scope.privileges = privilegeSvc.privileges['activty'] || [];
	  $scope.isPrivlage=$scope.privileges.indexOf('Edit') == -1;
	});
	/*Start showActivityPage*/
	$scope.showActivityPage=function(){
		$scope.showactivity=true;
		$scope.showgraph=false
		$scope.graphDataStatus=false;
		$scope.showgraphdiv=false
	}/*End showActivityPage*/
	$scope.enableEdit=function (uuid,version) {
		$scope.showActivityPage()
		$state.go('adminListactivity', {
			id: uuid,
			version: version,
			mode:'false'
		});
	}
	$scope.showview=function (uuid,version) {
		$scope.showActivityPage()
		$state.go('adminListactivity', {
			id: uuid,
			version: version,
			mode:'true'
		});
	}
	$scope.showActivityGraph=function(uuid,version){
		$scope.showactivity=false;
		$scope.showgraph=false
		$scope.graphDataStatus=true
		$scope.showgraphdiv=true;
	}/*End ShowDatapodGraph*/
	$scope.mode=" "
	if(typeof $stateParams.id != "undefined"){
		$scope.mode=$stateParams.mode
		$scope.isDependencyShow=true;
		AdminActivityService.getAllVersionByUuid($stateParams.id,"activity").then(function(response){onGetAllVersionByUuid(response.data)});
		var onGetAllVersionByUuid =function(response){
			for(var i=0;i<response.length;i++){
				var activityversion={};
				activityversion.version=response[i].version;
	   	    	$scope.activity.versions[i]=activityversion;
	   	    	//alert($scope.user.versions[i])
			}
		}
		AdminActivityService.getLatestByUuid($stateParams.id,"activity").then(function(response){onGetLatestByUuid(response.data)});
		var onGetLatestByUuid =function(response){
			$scope.activitydata=response;
			var defaultversion={};
			defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.activity.defaultVersion=defaultversion;
    	    $scope.selectactivityType=response.type
    		// var userInfo={};
    	  //   for(var j=0;j<response.userInfo.length;j++){
    	  //   	userInfo[j]=response.userInfo[j].ref.name
    	  //      	$scope.userInfoTags=userInfo;
    	  //   }
		}
	}

	$scope.selectVersion=function(){
		AdminActivityService.getByOneUuidandVersion($scope.activity.defaultVersion.uuid,$scope.activity.defaultVersion.version,'activity').then(function(response){onGetByOneUuidandVersion(response.data)});
		var onGetByOneUuidandVersion =function(response){
			$scope.activitydata=response;
			var defaultversion={};
			defaultversion.version=response.version;
    	   	defaultversion.uuid=response.uuid;
    	    $scope.activity.defaultVersion=defaultversion;
    	    $scope.selectactivityType=response.type
		}
	}

	});
