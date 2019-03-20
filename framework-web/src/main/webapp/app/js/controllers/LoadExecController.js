  JobMonitoringModule = angular.module('JobMonitoringModule');

  JobMonitoringModule.controller('DetailLoadExecController', function($filter, $state, $stateParams, $rootScope, $scope, $sessionStorage, JobMonitoringService, sortFactory, dagMetaDataService,privilegeSvc) {
    $scope.uuid = $stateParams.id;
    $scope.mode = $stateParams.mode;
    $scope.showExec = true;
    $scope.isEditInprogess=true;
    $scope.isEditVeiwError=false;
    $scope.selectTitle = dagMetaDataService.elementDefs['loadexec'].caption;
    $scope.state = dagMetaDataService.elementDefs['loadexec'].listState + "({type:'" + dagMetaDataService.elementDefs['loadexec'].execType + "'})"
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
    $scope.close = function() {
      if ($stateParams.returnBack == "true" && $rootScope.previousState) {
        //revertback
        $state.go($rootScope.previousState.name, $rootScope.previousState.params);
      } else {
        $scope.statedetail = {};
        $scope.statedetail.name = dagMetaDataService.elementDefs['loadexec'].listState
        $scope.statedetail.params = {}
        $scope.statedetail.params.type = dagMetaDataService.elementDefs['loadexec'].execType;
        $state.go($scope.statedetail.name, $scope.statedetail.params)
      }
    }

    JobMonitoringService.getLatestByUuid($scope.uuid, "loadexec")
    .then(function (response) { onSuccess(response.data)},function (response) { onError(response.data)});
    var onSuccess = function (response) {
      $scope.isEditInprogess=false;
      $scope.execData = response;
      var statusList = [];
      for (i = 0; i < response.statusList.length; i++) {
        d = $filter('date')(new Date(response.statusList[i].createdOn), "EEE MMM dd HH:mm:ss Z yyyy");
        d = d.toString().replace("+0530", "IST");
        statusList[i] = response.statusList[i].stage + "-" + d;
      }
      $scope.statusList = statusList;
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
    }
    var onError=function(){
      $scope.isEditInprogess=false;
      $scope.isEditVeiwError=true;
    }

    $scope.showGraph = function(uuid, version) {
      $scope.showExec = false;
      $scope.showGraphDiv = true;
    }

    $scope.onShowDetailDepOn=function(data){
      $rootScope.previousState = {};
      $rootScope.previousState.name = dagMetaDataService.elementDefs['loadexec'].detailState;
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

    $scope.showExecPage = function() {
      $scope.showExec = true
      $scope.showGraphDiv = false;
    }

    console.log(sqlFormatter.format("SELECT ds_customer_loss_simulation.cust_id  as cust_id ,ds_customer_loss_simulation.iteration_id  as iterationid , CASE WHEN ( ( ds_customer_loss_simulation.sqrt_correlation  * ds_customer_loss_simulation.factor_value  ) + ( sqrt ( 1 - ds_customer_loss_simulation.correlation  ) ) * ds_customer_loss_simulation.pd  ) < ds_customer_loss_simulation.def_point  THEN ( ds_customer_loss_simulation.exposure  * ds_customer_loss_simulation.lgd  ) ELSE 0 END as customer_loss ,ds_customer_loss_simulation.reporting_date  as reporting_date ,ds_customer_loss_simulation.version  as version  FROM ( SELECT customer_portfolio.cust_id  as cust_id ,customer_portfolio.industry  as industry ,customer_portfolio.exposure  as exposure ,customer_portfolio.lgd  as lgd ,customer_portfolio.correlation  as correlation ,customer_portfolio.sqrt_correlation  as sqrt_correlation ,customer_idiosyncratic_transpose.iterationid  as iterationid ,customer_idiosyncratic_transpose.reporting_date  as customer ,customer_idiosyncratic_transpose.customer  as pd ,industry_factor_transpose.iteration_id  as iteration_id ,industry_factor_transpose.reporting_date  as factor ,industry_factor_transpose.factor  as factor_value ,customer_portfolio.def_point  as def_point ,customer_portfolio.reporting_date  as reporting_date ,customer_portfolio.version  as version  FROM 7640609c_db5e_42fd_a814_f2d2f0d75fee_1526085077_1533069608 customer_portfolio  JOIN  ceb4f8ea_e02e_44f7_8083_2808cd8bdeee_1533083650_1533195956  industry_factor_transpose  ON (   (customer_portfolio.industry = industry_factor_transpose.factor)  AND (customer_portfolio.reporting_date = industry_factor_transpose.reporting_date) ) JOIN  ceb4f8ea_e02e_44f7_8083_2808cd8bdeqq_1533083693_1533195985  customer_idiosyncratic_transpose  ON (   (customer_portfolio.cust_id = customer_idiosyncratic_transpose.customer)  AND (customer_portfolio.reporting_date = customer_idiosyncratic_transpose.reporting_date) ) WHERE (1=1)  AND (((industry_factor_transpose.iteration_id = customer_idiosyncratic_transpose.iterationid) )) )  ds_customer_loss_simulation WHERE (1=1) ", {language:'sql'}));
  });
